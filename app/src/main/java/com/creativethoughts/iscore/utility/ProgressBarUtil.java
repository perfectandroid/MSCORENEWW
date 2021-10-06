package com.creativethoughts.iscore.utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.WindowManager.BadTokenException;

import com.creativethoughts.iscore.R;

/**
 * This class is used to show the progress dialog.
 *
 * @author Muthu.Krishnan
 */

public class ProgressBarUtil {
    private static final String TAG = ProgressBarUtil.class.getSimpleName();
    private static ProgressDialog progressDialog;

    /**
     * To show the progress dialog with the title as application name and message as "Loading" text.
     *
     * @param context
     */
    public static void showProgressDialog(Context context) {
        if (context == null) {
            return;
        }
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            try {
                progressDialog.show();
            } catch (BadTokenException e) {

            }
            progressDialog.setCancelable(false);
            progressDialog.setContentView(R.layout.progress_dialog_layout);
        }

    }

    /**
     * To dismiss the progress dialog
     */
    public static void dismissProgressDialog() {

        // try catch block for handling java.lang.IllegalArgumentException
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {

        }
        progressDialog = null;
    }

}
