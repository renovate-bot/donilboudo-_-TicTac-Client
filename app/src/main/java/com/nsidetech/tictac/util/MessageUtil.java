package com.nsidetech.tictac.util;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import com.nsidetech.tictac.R;

/**
 * Created by fabrice on 2017-05-28.
 */

public class MessageUtil {
    public static MessageUtil instance = null;

    private MessageUtil() {
    }

    public static MessageUtil getInstance() {
        if (instance == null)
        {
            instance = new MessageUtil();
        }

        return instance;
    }

    public void ToastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public void SnackMessage(Activity activity, String message, int viewId) {
        if (activity != null && message != null)
        {
            Snackbar snackbar = Snackbar.make(activity.findViewById(viewId), message, Snackbar.LENGTH_LONG);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(activity.getResources().getColor(R.color.colorRed));
            snackbar.show();
        }
    }
}
