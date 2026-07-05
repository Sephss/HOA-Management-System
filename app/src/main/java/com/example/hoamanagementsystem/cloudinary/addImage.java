package com.example.hoamanagementsystem.cloudinary;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

public class addImage {

    private static final String CLOUD_NAME = "dioxovniz";
    private static final String API_KEY = "895797867172465";
    private static final String API_SECRET = "I8oGdsbAwavCMI-WvQsJd1CBR9g";

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
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            byte[] imageBytes = getBytesFromInputStream(inputStream);

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

    public static void updateImage(Context context, Uri newImageUri, String oldImageUrl,UploadCallback callback) {
        try {
            // Convert new image to byte array
            InputStream inputStream = context.getContentResolver().openInputStream(newImageUri);
            byte[] imageBytes = getBytesFromInputStream(inputStream);

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
}
