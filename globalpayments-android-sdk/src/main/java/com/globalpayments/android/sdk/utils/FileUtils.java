package com.globalpayments.android.sdk.utils;

import android.util.Log;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    private static final String TAG = "FileUtils";
    public static final int BUFFER_SIZE = 8192; //8 KBytes
    public static final int END_OF_FILE = -1;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static byte[] readIntoByteArray(InputStream inputStream) {
        byte[] byteArray = null;

        try {
            byteArray = new byte[inputStream.available()];
            inputStream.read(byteArray);

        } catch (IOException e) {
            Log.d(TAG, "Read into byte array failed: " + e);
        } finally {
            closeCloseables(inputStream);
        }

        return byteArray;
    }

    public static boolean writeByteArrayToFile(byte[] byteArray, File file) {
        boolean isWriteSuccessful = false;

        if (byteArray == null || file == null) {
            return isWriteSuccessful;
        }

        deleteFile(file);
        OutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(byteArray);
            outputStream.flush();
            isWriteSuccessful = true;
        } catch (IOException e) {
            Log.d(TAG, "Write byte array to file failed: " + e);
        } finally {
            closeCloseables(outputStream);
        }

        return isWriteSuccessful;
    }

    public static boolean writeByteArrayToOutputStream(byte[] byteArray, OutputStream outputStream) {
        boolean isWriteSuccessful = false;

        if (byteArray == null || outputStream == null) {
            return isWriteSuccessful;
        }

        try {
            outputStream.write(byteArray);
            outputStream.flush();
            isWriteSuccessful = true;
        } catch (IOException e) {
            Log.d(TAG, "Write byte array to OutputStream failed: " + e);
        } finally {
            closeCloseables(outputStream);
        }

        return isWriteSuccessful;
    }

    public static boolean writeStreamToFile(InputStream inputStream, File file) {
        boolean isWriteSuccessful = false;

        if (inputStream == null || file == null) {
            return isWriteSuccessful;
        }

        deleteFile(file);
        OutputStream outputStream = null;

        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            outputStream = new FileOutputStream(file);

            while (true) {
                int chunk = inputStream.read(buffer);
                if (chunk == END_OF_FILE) {
                    break;
                }
                outputStream.write(buffer, 0, chunk);
            }

            outputStream.flush();
            isWriteSuccessful = true;

        } catch (IOException e) {
            Log.d(TAG, "Write stream to file failed: " + e);
        } finally {
            closeCloseables(inputStream, outputStream);
        }

        return isWriteSuccessful;
    }

    public static void closeCloseables(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    Log.d(TAG, "Close stream failed: " + e);
                }
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void deleteFile(File file) {
        try {
            if (file != null && file.exists()) {
                file.delete();
            }
        } catch (SecurityException e) {
            Log.d(TAG, "Delete file failed: " + e);
        }
    }

    public static boolean isFileReadable(File file) {
        return file != null && file.exists() && file.canRead() && file.length() > 0;
    }
}
