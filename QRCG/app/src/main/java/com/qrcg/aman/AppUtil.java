package com.qrcg.aman;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by aman on 11/3/16.
 */
public class AppUtil {
    private static final String LOG_TAG = "AppUtil";

    public static ProgressDialog createProgressDialog(Context context, String title, String msg, boolean cancelable) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        if (title != null && title.length() > 0) {
            progressDialog.setTitle(title);
        }
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(cancelable);
        progressDialog.setCanceledOnTouchOutside(cancelable);
        return progressDialog;
    }

    public static AlertDialog createAlertDialog(Context context, String title, String msg, String positive, boolean cancelable, final OnDialogOkButtonClick onDialogOkButtonClick) {
        AlertDialog dialog = null;
        try {
            AlertDialog.Builder e = new AlertDialog.Builder(context);
            if (title != null && title.length() > 0) {
                e.setTitle(title);
            }
            e.setMessage(msg).setTitle(title);
            e.setPositiveButton(positive, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (onDialogOkButtonClick != null) {
                        onDialogOkButtonClick.onButtonClick();
                    }
                }
            });
            e.setCancelable(cancelable);
            dialog = e.create();
            dialog.show();
        } catch (Exception var6) {
            var6.printStackTrace();
        }
        return dialog;
    }

    public static boolean showProgressDialog(ProgressDialog progressDialog) {
        boolean done = false;
        if (progressDialog != null) {
            progressDialog.show();
            done = true;
        }
        return done;
    }

    public static boolean dismissProgressDialog(ProgressDialog progressDialog) {
        boolean done = false;
        if ((progressDialog != null) && progressDialog.isShowing()) {
            progressDialog.dismiss();
            done = true;
        }
        return done;
    }

    public static boolean check_internet(Context context, boolean should_notify) {
        boolean is_connected = check_internet_connection(context);
        if (!is_connected && should_notify) {
            show_no_internet((Activity) context);
        }
        return is_connected;

    }

    public static Boolean check_internet_connection(Context activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return Boolean.valueOf(isConnected);
    }

    static android.app.AlertDialog no_internet_alert = null;

    public static void show_no_internet(final Activity activity) {
        try {
            no_internet_alert = new android.app.AlertDialog.Builder(activity).create();
            View view = activity.getLayoutInflater().inflate(R.layout.nointernet_layout_full, null);

            no_internet_alert.setView(view);

            no_internet_alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
            WindowManager.LayoutParams wmlp = no_internet_alert.getWindow().getAttributes();

            wmlp.gravity = Gravity.TOP;
            //        wmlp.x = 100;   //x position
            //        wmlp.y = 100;   //y position

            no_internet_alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if (no_internet_alert != null) {
                        no_internet_alert.dismiss();
                        no_internet_alert = null;
                    }
                }
            });
            no_internet_alert.show();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception - " + e.toString(), e);
        }
    }
}