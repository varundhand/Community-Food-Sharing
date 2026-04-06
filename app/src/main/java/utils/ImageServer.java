package utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class ImageServer {
    // references
    // https://developer.android.com/training/data-storage/shared/documents-files
    // https://www.geeksforgeeks.org/android/showing-image-view-from-file-path-in-android/
    // https://developer.android.com/reference/android/graphics/Bitmap
    // picker: https://developer.android.com/training/data-storage/shared/photo-picker#java
    Context context;

    public ImageServer(Context context) {
        this.context = context;
    }

    public String saveImage(Uri uri) {
        // returns image key
        // create random image key
        UUID uuid = UUID.randomUUID();
        String mimeType = context.getContentResolver().getType(uri);

        String extension;
        if ("image/png".equals(mimeType)) {
            extension = ".png";
        } else if ("image/webp".equals(mimeType)) {
            extension = ".webp";
        } else {
            extension = ".jpg"; // fallback
        }

        String filename = uuid.toString() + extension;
        File file = new File(context.getFilesDir(), filename);
        try (
                InputStream input = context.getContentResolver().openInputStream(uri);
                OutputStream output = new FileOutputStream(file);
        ) {
            if (input == null) {
                throw new IOException("Could not open input stream");
            }

            // read file bytes into buffer and write them to the destination file
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            output.flush();

            return filename;
        }
        catch (Exception e) {
            return null;
        }
    }

    public Bitmap loadImage(String filename) {
        File imgFile = new File(context.getFilesDir(), filename);
        if (imgFile.exists()) {
            try {
                return BitmapFactory.decodeFile(imgFile.getAbsolutePath()); // can be null
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    // reference
    // https://stackoverflow.com/a/4717740
    public Bitmap loadImage(Uri uri) {
        try {
            return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (IOException e) {
            return null;
        }
    }

    // reference
    // https://stackoverflow.com/a/24659789
    public boolean removeImage(String filename) {
        File imgFile = new File(context.getFilesDir(), filename);
        if (imgFile.exists()) {
            try {
                return imgFile.delete();
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }
}
