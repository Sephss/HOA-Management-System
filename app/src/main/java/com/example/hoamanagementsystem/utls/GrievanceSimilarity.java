package com.example.hoamanagementsystem.utls;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Decides whether two grievances describe the SAME issue.
 * Pure Java logic, no Firebase or Android dependencies.
 */
public final class GrievanceSimilarity {

    private GrievanceSimilarity() {}

    public static class Result {
        public boolean isDuplicate;
        public double locationScore;
        public double descriptionScore;
        public int sharedConcepts;

        public double totalScore() {
            return locationScore + descriptionScore;
        }
    }

    // Types where "same type + same place + still open" is already the same issue
    private static final Set<String> LOCATION_DECIDES = new HashSet<>(Arrays.asList(
            "noise disturbance",
            "illegal parking",
            "waste/sanitation"
    ));

    private static final double LOCATION_MATCH = 0.75;
    private static final double DESC_THRESHOLD = 0.40;
    private static final double DESC_THRESHOLD_OTHERS = 0.50;

    // ------------------------------------------------------------------
    // PUBLIC API
    // ------------------------------------------------------------------
    /** A = new submission, B = existing record. */
    public static Result compare(String incidentType,
                                 String locationA, String titleA, String descA,
                                 String locationB, String titleB, String descB) {

        Result r = new Result();

        Location la = parseLocation(locationA);
        Location lb = parseLocation(locationB);
        r.locationScore = locationScore(la, lb);

        String typeKey = incidentType == null ? "" : incidentType.trim().toLowerCase(Locale.ROOT);

        if (LOCATION_DECIDES.contains(typeKey)) {
            r.isDuplicate = r.locationScore >= LOCATION_MATCH;
            return r;
        }

        // Types that need the content to agree as well
        Set<String> typeTokens = conceptTokens(incidentType);
        Set<String> ta = conceptTokens(nz(titleA) + " " + nz(descA));
        Set<String> tb = conceptTokens(nz(titleB) + " " + nz(descB));
        ta.removeAll(typeTokens);
        tb.removeAll(typeTokens);

        if (!ta.isEmpty() && !tb.isEmpty()) {
            Set<String> inter = new HashSet<>(ta);
            inter.retainAll(tb);
            r.sharedConcepts = inter.size();
            r.descriptionScore = (double) inter.size() / Math.min(ta.size(), tb.size());
        }

        double threshold = typeKey.equals("others") ? DESC_THRESHOLD_OTHERS : DESC_THRESHOLD;
        r.isDuplicate = r.locationScore >= LOCATION_MATCH
                && r.sharedConcepts >= 2
                && r.descriptionScore >= threshold;
        return r;
    }

    // ------------------------------------------------------------------
    // LOCATION
    // ------------------------------------------------------------------
    private static class Location {
        Map<String, String> markers = new HashMap<>(); // block / phase / lot -> value
        Set<String> words = new HashSet<>();           // leftover place words
    }

    private static final Pattern PHASE = Pattern.compile("\\b(?:phase|phs|ph)\\s*(?:no)?\\s*(\\d{1,3}[a-z]?)\\b");
    private static final Pattern BLOCK = Pattern.compile("\\b(?:block|blck|blk|bl|b)\\s*(?:no)?\\s*(\\d{1,3}[a-z]?)\\b");
    private static final Pattern LOT   = Pattern.compile("\\b(?:lot|lt|l)\\s*(?:no)?\\s*(\\d{1,3}[a-z]?)\\b");

    private static final Map<String, String> NUMBER_WORDS = new HashMap<>();
    static {
        String[] w = {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten"};
        for (int i = 0; i < w.length; i++) NUMBER_WORDS.put(w[i], String.valueOf(i + 1));
    }

    private static Location parseLocation(String raw) {
        String s = nz(raw).toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        StringBuilder sb = new StringBuilder();
        for (String t : s.split(" ")) {
            String n = NUMBER_WORDS.get(t);
            sb.append(n != null ? n : t).append(' ');
        }
        s = sb.toString();

        Location loc = new Location();
        s = extract(s, PHASE, "phase", loc.markers);
        s = extract(s, BLOCK, "block", loc.markers);
        s = extract(s, LOT, "lot", loc.markers);

        for (String t : s.trim().split("\\s+")) {
            if (t.length() > 1 && !STOP.contains(t)) loc.words.add(stem(t));
        }
        return loc;
    }

    private static String extract(String s, Pattern p, String key, Map<String, String> out) {
        Matcher m = p.matcher(s);
        if (m.find()) out.put(key, m.group(1));
        return m.replaceAll(" ");
    }

    private static double locationScore(Location a, Location b) {
        if (!a.markers.isEmpty() && !b.markers.isEmpty()) {
            int shared = 0;
            for (Map.Entry<String, String> e : a.markers.entrySet()) {
                String other = b.markers.get(e.getKey());
                if (other != null) {
                    if (!other.equals(e.getValue())) return 0.0; // conflict, e.g. block 2 vs block 3
                    shared++;
                }
            }
            if (shared == 0) return 0.0;                          // e.g. "block 2" vs "phase 1"
            if (a.markers.keySet().equals(b.markers.keySet())) return 1.0;
            boolean subset = b.markers.keySet().containsAll(a.markers.keySet())
                    || a.markers.keySet().containsAll(b.markers.keySet());
            return subset ? 0.75 : 0.6;
        }

        // No usable structure on one side: fall back to place-word overlap
        if (a.words.isEmpty() || b.words.isEmpty()) return 0.0;
        Set<String> inter = new HashSet<>(a.words);
        inter.retainAll(b.words);
        Set<String> union = new HashSet<>(a.words);
        union.addAll(b.words);
        double jaccard = (double) inter.size() / union.size();
        return jaccard >= 0.6 ? 0.8 : jaccard * 0.5;
    }

    // ------------------------------------------------------------------
    // TEXT -> CONCEPTS (so different wordings meet at the same idea)
    // ------------------------------------------------------------------
    private static final String[][] GROUPS = {
            {"noise", "noisy", "loud", "ingay", "maingay", "karaoke", "videoke", "music", "sound", "speaker", "party", "shouting", "yelling", "sigaw", "inuman"},
            {"parking", "park", "car", "vehicle", "van", "truck", "motorcycle", "motor", "tricycle", "sasakyan", "kotse", "obstruct", "blocking", "harang"},
            {"waste", "trash", "garbage", "basura", "dump", "dumping", "litter", "smell", "odor", "stink", "amoy", "burn", "burning"},
            {"leak", "leaking", "flood", "flooding", "drainage", "canal", "clogged", "baha", "tubig", "pipe"},
            {"light", "streetlight", "lamp", "poste", "dark", "blackout"},
            {"damage", "broken", "destroy", "destroyed", "sira", "wasak", "crack", "cracked"},
            {"fence", "gate", "wall", "bakod", "pader"},
            {"pet", "dog", "aso", "cat", "pusa", "bark", "barking", "bite", "bit", "kagat", "stray"},
            {"construction", "construct", "build", "building", "renovation", "extension", "expansion", "cement"},
            {"vandal", "vandalism", "graffiti", "spray", "paint"},
            {"suspicious", "stranger", "loiter", "loitering", "unknown", "thief", "theft", "steal", "magnanakaw"},
            {"pothole", "hole", "manhole"},
            {"wire", "wires", "electric", "exposed"}
    };

    private static final Map<String, String> CONCEPT = new HashMap<>();
    static {
        for (String[] g : GROUPS) {
            for (String w : g) {
                CONCEPT.put(w, "c:" + g[0]);
                CONCEPT.put(stem(w), "c:" + g[0]);
            }
        }
    }

    private static final Set<String> STOP = new HashSet<>(Arrays.asList(
            "the", "a", "an", "and", "or", "of", "in", "on", "at", "to", "is", "are", "was", "were",
            "there", "this", "that", "it", "for", "with", "near", "by", "from", "my", "our", "has", "have",
            "been", "being", "very", "so", "please", "po", "ang", "ng", "sa", "na", "si", "mga", "at",
            "street", "st", "road", "rd", "village", "subd", "subdivision", "house", "front", "beside", "behind",
            "again", "always", "every", "night", "day", "morning", "today", "yesterday"
    ));

    private static Set<String> conceptTokens(String text) {
        Set<String> out = new HashSet<>();
        String s = nz(text).toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9 ]", " ");
        for (String t : s.split("\\s+")) {
            if (t.length() < 3 || STOP.contains(t)) continue;
            String c = CONCEPT.get(t);
            if (c == null) c = CONCEPT.get(stem(t));
            out.add(c != null ? c : stem(t));
        }
        return out;
    }

    private static String stem(String t) {
        if (t.endsWith("ing") && t.length() > 5) return t.substring(0, t.length() - 3);
        if (t.endsWith("ed") && t.length() > 4) return t.substring(0, t.length() - 2);
        if (t.endsWith("s") && !t.endsWith("ss") && t.length() > 3) return t.substring(0, t.length() - 1);
        return t;
    }

    private static String nz(String s) { return s == null ? "" : s; }
}