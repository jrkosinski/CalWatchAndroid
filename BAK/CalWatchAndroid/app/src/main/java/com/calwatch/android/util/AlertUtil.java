package com.calwatch.android.util;

import android.app.Activity;
import android.app.AlertDialog;
import com.calwatch.android.tasks.ICallback;
import android.content.DialogInterface;

/**
 * Created by Home on 23/1/2559.
 */
public class AlertUtil {

    public static void showAlert(Activity context, String title, String text) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title);
        alert.setMessage(text);
        alert.setPositiveButton("OK", null);
        alert.show();
    }

    public static void showDialogTwoOptions(
            Activity context,
            String title,
            String text,
            String positiveButtonText,
            String negativeButtonText,
            final ICallback onPositiveClick,
            final ICallback onNegativeClick
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
