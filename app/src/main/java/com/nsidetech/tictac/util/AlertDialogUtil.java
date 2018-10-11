package com.nsidetech.tictac.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

/**
 * Created by fabrice on 2017-05-31.
 */

public class AlertDialogUtil {
    private static AlertDialog.Builder builder;

    private static void buildDialog(final Activity activity, String message) {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        builder = new AlertDialog.Builder(activity);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(message).setTitle("Permission");

        // Add the buttons
        builder.setPositiveButton("PARAMETRES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                openSettings(activity);
            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
    }

    public static void showDialog(Activity activity, String message) {
        buildDialog(activity, message);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static void openSettings(Activity activity) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivity(intent);
    }
}
