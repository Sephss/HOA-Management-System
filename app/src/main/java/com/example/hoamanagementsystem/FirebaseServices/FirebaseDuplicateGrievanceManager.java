package com.example.hoamanagementsystem.FirebaseServices;

import androidx.annotation.NonNull;

import com.example.hoamanagementsystem.FirebaseServices.callback.DuplicateCheckCallback;
import com.example.hoamanagementsystem.utls.GrievanceSimilarity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class FirebaseDuplicateGrievanceManager {

    // TODO: must be the SAME node that FirebaseGrievanceManager.createReport() writes to
    private static final String GRIEVANCE_NODE = "Grievance";

    // Only compare against reports newer than this (an old unresolved report can be re-reported)
    private static final long ACTIVE_WINDOW_MS = 30L * 24 * 60 * 60 * 1000;

    // If the check itself fails (offline, permission), allow the submission instead of blocking the user
    private static final boolean FAIL_OPEN = true;

    // Statuses that mean "this issue is closed", so a new report is legitimate
    private static final Set<String> CLOSED_STATUSES = new HashSet<>(Arrays.asList(
            "resolved", "rejected", "closed", "cancelled", "canceled", "dismissed", "declined"
    ));

    public static void checkDuplicate(String incidentType,
                                      String location,
                                      String title,
                                      String description,
                                      DuplicateCheckCallback callback) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(GRIEVANCE_NODE);

        // Step 1: only same-type records come down (needs .indexOn incidentType)
        ref.orderByChild("incidentType").equalTo(incidentType)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long now = System.currentTimeMillis();

                        DataSnapshot bestMatch = null;
                        double bestScore = -1;

                        for (DataSnapshot child : snapshot.getChildren()) {

                            // Step 2: must still be active
                            String status = str(child, "incidentStatus");
                            String statusKey = status.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
                            if (CLOSED_STATUSES.contains(statusKey)) continue;

                            // Step 3: must be recent
                            long ts = parseLong(str(child, "timestamp"));
                            if (ts > 0 && (now - ts) > ACTIVE_WINDOW_MS) continue;

                            // Step 4: same place (and same content where the type requires it)
                            GrievanceSimilarity.Result r = GrievanceSimilarity.compare(
                                    incidentType,
                                    location, title, description,
                                    str(child, "incidentExactLocation"),
                                    str(child, "incidentTitle"),
                                    str(child, "incidentDescription")
                            );

                            if (r.isDuplicate && r.totalScore() > bestScore) {
                                bestScore = r.totalScore();
                                bestMatch = child;
                            }
                        }

                        if (bestMatch != null) {
                            callback.onResult(true,
                                    str(bestMatch, "incidentTicket"),
                                    str(bestMatch, "incidentStatus"),
                                    str(bestMatch, "dateSubmitted"),
                                    str(bestMatch, "incidentReporterID"));
                        } else {
                            callback.onResult(false, "", "", "", "");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if (FAIL_OPEN) callback.onResult(false, "", "", "", "");
                        else callback.onError(error.getMessage());
                    }
                });
    }

    private static String str(DataSnapshot s, String key) {
        String v = s.child(key).getValue(String.class);
        return v == null ? "" : v;
    }

    private static long parseLong(String s) {
        try { return Long.parseLong(s); } catch (Exception e) { return 0; }
    }
}