package com.qrcg.aman;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by aman on 11/2/16.
 */
public class FileCreator {
    private static final String LOG_TAG = "FileCreator";

    public static FileOutputStream getOrCreateFile(String path) {
        try {
            File output = null;
            FileOutputStream fos = null;
            if (path != null) {
                output = new File(path);
                if (!output.exists()) {
                    output.createNewFile();

                }
                fos = new FileOutputStream(output);

                return fos;
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception - " + e);
        }
        return null;
    }

   /* public static File getOrCreateAppFolder(Context context) {
        try {
            File folder = new File(Environment.getExternalStorageDirectory() +
                    File.separator + context.getResources().getString(R.string.app_name));
            if (!folder.exists()) {
                folder.mkdirs();
            }
            Log.i(LOG_TAG, "getOrCreateAppFolder path - "+folder.getAbsolutePath());
            return folder;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception - " + e);
        }
        return null;
    }*/

    public static File getOrCreateFolder(String folderName) {
        try {
            File folder = new File(Environment.getExternalStorageDirectory() +
                    File.separator + folderName);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            return folder;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception - " + e);
        }
        return null;
    }

  /*  public static File getOrCreateFolderNew(Context context, String folderName) {
        try {
            File folder = new File(getOrCreateAppFolder(context) +
                    File.separator + folderName);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            Log.i(LOG_TAG, "getOrCreateFolderNew path - "+folder.getAbsolutePath());
            return folder;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception - " + e);
        }
        return null;
    }*/

    public static void scan_media(Context context, String path) {
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        Intent scanFileIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        context.sendBroadcast(scanFileIntent);

    }
}
