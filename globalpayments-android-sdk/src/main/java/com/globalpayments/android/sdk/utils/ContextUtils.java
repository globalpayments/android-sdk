package com.globalpayments.android.sdk.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.globalpayments.android.sdk.utils.Strings.EMPTY;

public class ContextUtils {
    private static final String TAG = "ContextUtils";

    public static String getFileNameFromUri(Uri uri, Context context) {
        String fileName = EMPTY;

        try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            }
        } catch (Exception e) {
            Log.d(TAG, "Get file name from uri failed: " + e);
        }

        return fileName;
    }

    public static InputStream getInputStreamForUri(Uri uri, Context context) {
        InputStream inputStream = null;

        try {
            inputStream = context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "Open input stream failed" + e);
        }

        return inputStream;
    }

    public static OutputStream getOutputStreamForUri(Uri uri, Context context) {
        OutputStream outputStream = null;

        try {
            outputStream = context.getContentResolver().openOutputStream(uri);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "Open output stream failed" + e);
        }

        return outputStream;
    }

    public static boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static File getAppDocumentsDirectory(Context context) {
        return getAppPrivateStoreDirectory(context, Environment.DIRECTORY_DOCUMENTS);
    }

    public static File getAppPrivateStoreDirectory(Context context, String type) {
        if (isExternalStorageWritable()) {
            return context.getExternalFilesDir(type);
        } else {
            return context.getFilesDir();
        }
    }
}
