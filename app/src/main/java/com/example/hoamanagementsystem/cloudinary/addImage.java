package com.example.hoamanagementsystem.cloudinary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    private static byte[] compressImage(Context context, Uri imageUri) throws Exception {

        InputStream inputStream = context.getContentResolver().openInputStream(imageUri);

        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

        if (inputStream != null)
            inputStream.close();

        return compressBitmap(bitmap, false);
    }

    /**
     * Resizes (if needed) and compresses a bitmap.
     *
     * @param bitmap     The source bitmap.
     * @param isLineArt  If true, compresses as PNG (no artifacts, good for signatures/
     *                   line art with transparent backgrounds). If false, compresses
     *                   as JPEG (smaller size, good for photos).
     */
    private static byte[] compressBitmap(Bitmap bitmap, boolean isLineArt) throws Exception {

        // Resize first
        int maxWidth = 1280;
        int maxHeight = 1280;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float ratio = Math.min(
                (float) maxWidth / width,
                (float) maxHeight / height
        );
        // don't upscale images that are already smaller than the max bounds
        ratio = Math.min(ratio, 1f);

        width = Math.round(width * ratio);
        height = Math.round(height * ratio);

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        if (isLineArt) {
            // PNG: lossless, preserves crisp strokes/transparency (signatures)
            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        } else {
            // JPEG quality (0-100), smaller file size for photos
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream);
        }

        return outputStream.toByteArray();
    }
}