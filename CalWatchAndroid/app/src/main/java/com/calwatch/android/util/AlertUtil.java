package com.calwatch.android.util;

import android.app.Activity;
import android.app.AlertDialog;
import com.calwatch.android.tasks.Callback;
import android.content.DialogInterface;

/**
 * Created by John R. Kosinski on 23/1/2559.
 * Utilities for displaying alert popup messages.
 */
public class AlertUtil
{
    public static void showAlert(Activity context, String title, String text) {
        showAlert(context, title, text, null);
    }

    public static void showAlert(Activity context, String title, String text, final Callback onClick) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title);
        alert.setMessage(text);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (onClick != null)
                    onClick.execute();
                dialog.dismiss();
            }
        });
        alert.show();
    }

    public static void showDialogTwoOptions(
            Activity context,
            String title,
            String text,
            String positiveButtonText,
            String negativeButtonText,
            final Callback onPositiveClick,
            final Callback onNegativeClick
    )
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(title);
        builder.setMessage(text);

        builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                if (onPositiveClick != null)
                    onPositiveClick.execute();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (onNegativeClick != null)
                    onNegativeClick.execute();
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
