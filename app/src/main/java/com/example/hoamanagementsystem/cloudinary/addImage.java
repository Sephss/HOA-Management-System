package com.example.hoamanagementsystem.cloudinary;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

public class addImage {
    private static final String CLOUD_NAME = "gmtq06oo";
    private static final String API_KEY = "181487115313841";
    private static final String API_SECRET = "gHG-b3L_82PfrK1nACLrq_p5YZ8";
    private static final int MAX_SIZE = 1280;

    private static final Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", CLOUD_NAME,
            "api_key", API_KEY,
            "api_secret", API_SECRET
    ));

    /**
     * Uploads an image to Cloudinary and retrieves the URL.
     *
     * @param context  The application context for accessing resources.
     * @param imageUri The URI of the image to be uploaded.
     * @param callback The callback to handle the result (success or failure).
     */
    public static void uploadImage(Context context, Uri imageUri, UploadCallback callback) {
        try {
            byte[] imageBytes = compressImage(context, imageUri);

            new Thread(() -> {
                try {
                    Map uploadResult = cloudinary.uploader().upload(imageBytes, ObjectUtils.emptyMap());
                    String imageUrl = (String) uploadResult.get("url");

                    // Notify success via callback
                    callback.onSuccess(imageUrl);
                } catch (Exception e) {
                    Log.e("Cloudinary", "Error uploading image", e);
                    // Notify failure via callback
                    callback.onFailure(e);
                }
            }).start();
        } catch (Exception e) {
            Log.e("Cloudinary", "Error reading image file", e);
            // Notify failure via callback
            callback.onFailure(e);
        }
    }

    /**
     * Uploads up to two images (e.g. the two document slots) one after the other.
     * Pass null for a slot that has no new image; its URL comes back as null so the
     * caller can keep the existing one. If either upload fails, onFailure is called once.
     * Callbacks run on a background thread, so use runOnUiThread() when touching views.
     *
     * @param context   The application context for accessing resources.
     * @param firstUri  New image for slot 1, or null to skip.
     * @param secondUri New image for slot 2, or null to skip.
     * @param callback  Receives the uploaded URLs (null for any skipped slot).
     */
    public static void uploadTwoImages(Context context, Uri firstUri, Uri secondUri, TwoImagesCallback callback) {
        new Thread(() -> {
            try {
                String firstUrl = null;
                String secondUrl = null;

                if (firstUri != null) {
                    firstUrl = uploadBytes(compressImage(context, firstUri));
                }

                if (secondUri != null) {
                    secondUrl = uploadBytes(compressImage(context, secondUri));
                }

                callback.onSuccess(firstUrl, secondUrl);
            } catch (Exception e) {
                Log.e("Cloudinary", "Error uploading two images", e);
                callback.onFailure(e);
            }
        }).start();
    }

    private static String uploadBytes(byte[] imageBytes) throws Exception {
        Map uploadResult = cloudinary.uploader().upload(imageBytes, ObjectUtils.emptyMap());
        return (String) uploadResult.get("url");
    }

    /**
     * Uploads a Bitmap directly to Cloudinary. Used for content that doesn't come
     * from a file Uri, e.g. the signature pad, which hands back a Bitmap.
     *
     * @param context  The application context.
     * @param bitmap   The bitmap to upload (e.g. signaturePad.getSignatureBitmap()).
     * @param callback The callback to handle the result (success or failure).
     */
    public static void uploadBitmap(Context context, Bitmap bitmap, UploadCallback callback) {
        try {
            byte[] imageBytes = compressBitmap(bitmap, true);

            new Thread(() -> {
                try {
                    Map uploadResult = cloudinary.uploader().upload(imageBytes, ObjectUtils.emptyMap());
                    String imageUrl = (String) uploadResult.get("url");

                    callback.onSuccess(imageUrl);
                } catch (Exception e) {
                    Log.e("Cloudinary", "Error uploading bitmap", e);
                    callback.onFailure(e);
                }
            }).start();
        } catch (Exception e) {
            Log.e("Cloudinary", "Error compressing bitmap", e);
            callback.onFailure(e);
        }
    }

    public static void updateImage(Context context, Uri newImageUri, String oldImageUrl,UploadCallback callback) {
        try {
            // Convert new image to byte array
            byte[] imageBytes = compressImage(context, newImageUri);

            new Thread(() -> {
                try {
                    // Extract the public ID from the old image URL
                    String publicId = extractPublicIdFromUrl(oldImageUrl);

                    // Delete the old image from Cloudinary
                    cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

                    // Upload the new image
                    Map uploadResult = cloudinary.uploader().upload(imageBytes, ObjectUtils.emptyMap());
                    String newImageUrl = (String) uploadResult.get("url");

                    callback.onSuccess(newImageUrl);
                } catch (Exception e) {
                    Log.e("Cloudinary", "Error updating image", e);
                    callback.onFailure(e);
                }
            }).start();

        } catch (Exception e) {
            Toast.makeText(context, "Failed to process new image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("Cloudinary", "Error reading new image file", e);
            callback.onFailure(e);
        }
    }

    // Helper method to extract public ID from Cloudinary URL
    private static String extractPublicIdFromUrl(String url) {
        // Assuming the public ID is the part after the last '/' and before the file extension
        String[] parts = url.split("/");
        String filenameWithExtension = parts[parts.length - 1];
        return filenameWithExtension.substring(0, filenameWithExtension.lastIndexOf('.'));
    }

    private static byte[] getBytesFromInputStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }
        return byteArrayOutputStream.toByteArray();
    }

    public interface UploadCallback {
        void onSuccess(String imageUrl);

        void onFailure(Exception e);
    }

    public interface TwoImagesCallback {
        /** A URL is null when its slot had no new image to upload. */
        void onSuccess(String firstUrl, String secondUrl);

        void onFailure(Exception e);
    }


    /** Largest power-of-2 sample size that keeps the result >= the requested size. */
    private static int calculateInSampleSize(int width, int height, int reqWidth, int reqHeight) {
        int inSampleSize = 1;
        while ((width / (inSampleSize * 2)) >= reqWidth && (height / (inSampleSize * 2)) >= reqHeight) {
            inSampleSize *= 2;
        }
        return inSampleSize;
    }

    private static Bitmap applyExifOrientation(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.postScale(1, -1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.postRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.postRotate(270);
                matrix.postScale(-1, 1);
                break;
            default:
                return bitmap; // normal / undefined: nothing to do
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private static byte[] compressImage(Context context, Uri imageUri) throws Exception {
        ContentResolver resolver = context.getContentResolver();

        // 1. Read only the image dimensions (no pixels loaded into memory)
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        try (InputStream in = resolver.openInputStream(imageUri)) {
            if (in == null) throw new Exception("Unable to open image");
            BitmapFactory.decodeStream(in, null, bounds);
        }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
            throw new Exception("Unable to decode image");
        }

        // 2. Decode a downsampled version (much less RAM than full resolution)
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = calculateInSampleSize(bounds.outWidth, bounds.outHeight, MAX_SIZE, MAX_SIZE);

        Bitmap bitmap;
        try (InputStream in = resolver.openInputStream(imageUri)) {
            if (in == null) throw new Exception("Unable to open image");
            bitmap = BitmapFactory.decodeStream(in, null, opts);
        }
        if (bitmap == null) {
            throw new Exception("Unable to decode image");
        }

        // 3. Read EXIF orientation and rotate/flip so the image isn't sideways
        int orientation = ExifInterface.ORIENTATION_NORMAL;
        try (InputStream in = resolver.openInputStream(imageUri)) {
            if (in != null) {
                orientation = new ExifInterface(in).getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            }
        } catch (Exception e) {
            Log.w("Cloudinary", "Could not read EXIF orientation", e);
        }

        Bitmap oriented = applyExifOrientation(bitmap, orientation);
        if (oriented != bitmap) {
            bitmap.recycle();
        }

        try {
            return compressBitmap(oriented, false);
        } finally {
            oriented.recycle(); // we created this bitmap, so it's safe to recycle
        }
    }

    /**
     * Resizes (if needed) and compresses a bitmap.
     *
     * @param bitmap     The source bitmap.
     * @param isLineArt  If true, compresses as PNG (no artifacts, good for signatures/
     *                   line art with transparent backgrounds). If false, compresses
     *                   as JPEG (smaller size, good for photos).
     */

    /**
     * Resizes (if needed) and compresses a bitmap.
     * Does NOT recycle the bitmap passed in (the caller owns it, e.g. the signature pad).
     */
    private static byte[] compressBitmap(Bitmap bitmap, boolean isLineArt) throws Exception {
        if (bitmap == null) throw new Exception("Bitmap is null");

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float ratio = Math.min((float) MAX_SIZE / width, (float) MAX_SIZE / height);
        ratio = Math.min(ratio, 1f); // never upscale

        int newWidth = Math.max(1, Math.round(width * ratio));
        int newHeight = Math.max(1, Math.round(height * ratio));

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            if (isLineArt) {
                resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            } else {
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream);
            }
            return outputStream.toByteArray();
        } finally {
            // createScaledBitmap returns the same instance when size is unchanged,
            // so only recycle if it's a new bitmap
            if (resizedBitmap != bitmap) {
                resizedBitmap.recycle();
            }
        }
    }
}