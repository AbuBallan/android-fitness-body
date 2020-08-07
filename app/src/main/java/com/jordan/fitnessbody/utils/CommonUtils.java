package com.jordan.fitnessbody.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class CommonUtils {

    private CommonUtils() {
    }

    public static void openGalleryIntentForSingleImage(Activity activity) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        activity.startActivityForResult(Intent.createChooser(intent, "Select image"), AppConstants.REQUEST_GET_GALLERY_SINGLE_IMAGE);

    }

    public static Uri handleGalleryIntentResultForSingleImage(Context context, Intent data) {

        Uri selectedImageUri = data.getData();
        final String path = getPathFromURI(context, selectedImageUri);
        if (path != null) {
            File f = new File(path);
            selectedImageUri = Uri.fromFile(f);
        }

        return selectedImageUri;
    }


    public static String getPathFromURI(Context context, Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

}
