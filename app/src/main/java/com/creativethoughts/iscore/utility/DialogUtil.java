package com.creativethoughts.iscore.utility;

import android.content.Context;

import com.creativethoughts.iscore.IScoreApplication;
import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.ui.widget.ZaarkDialog;

/**
 * Created by muthukrishnan on 21/01/15.
 */
public class DialogUtil {

    /**
     * To show the alert with given message.
     *
     * @param context
     * @param message - Content of the message.
     */
    public static void showAlert(Context context, String message) {
        if (message != null) {
            showAlert(context, IScoreApplication.getAppContext().getString(R.string.app_name),
                    message, IScoreApplication.getAppContext().getString(R.string.COMMON_ok), null,
                    null, null);
        }
    }

    /**
     * To show the alert with given message.
     *
     * @param context
     * @param message - Content of the message resource id.
     */
    public static void showAlert(Context context, int message) {
        showAlert(context, R.string.app_name, message, R.string.COMMON_ok, null, -1, null);
    }

    /**
     * To show the alert with given message and have positive button click listener.
     *
     * @param context
     * @param message          - Content of the message.
     * @param posClickListener - Positive button click listener.
     */
    public static void showAlert(Context context, String message, ZaarkDialog.OnPositiveButtonClickListener posClickListener) {
        if (message != null) {
            showAlert(context, IScoreApplication.getAppContext().getString(R.string.app_name),
                    message, IScoreApplication.getAppContext().getString(R.string.COMMON_ok),
                    posClickListener, null, null);
        }
    }

    /**
     * To show the alert with given message and have positive button click listener.
     *
     * @param context
     * @param message          - Content of the message resource id.
     * @param posClickListener - Positive button click listener.
     */
    public static void showAlert(Context context, int message, ZaarkDialog.OnPositiveButtonClickListener posClickListener) {
        showAlert(context, R.string.app_name, message, R.string.COMMON_ok, posClickListener, -1,
                null);
    }

    /**
     * To show the alert with title, message.
     *
     * @param context
     * @param title            - Title of the message.
     * @param message          - Content of the message resource id.
     * @param posClickListener - Positive button click listener.
     */
    public static void showAlert(Context context, String title, String message, ZaarkDialog.OnPositiveButtonClickListener posClickListener) {
        if (message != null) {
            showAlert(context, title, message,
                    IScoreApplication.getAppContext().getString(R.string.COMMON_ok),
                    posClickListener, null, null);
        }
    }

    /**
     * To show the alert with title, message.
     *
     * @param context
     * @param title            - Title of the message resource id.
     * @param message          - Content of the message resource id.
     * @param posClickListener - Positive button click listener.
     */
    public static void showAlert(Context context, int title, int message, ZaarkDialog.OnPositiveButtonClickListener posClickListener) {
        showAlert(context, title, message, R.string.COMMON_ok, posClickListener, -1, null);
    }

    /**
     * To show the alert with title, message.
     *
     * @param context
     * @param title   - Title of the message.
     * @param message - Content of the message resource id.
     */
    public static void showAlert(Context context, String title, String message) {
        if (message != null) {
            showAlert(context, title, message,
                    IScoreApplication.getAppContext().getString(R.string.COMMON_ok), null, null,
                    null);
        }
    }

    /**
     * To show the alert with title, message.
     *
     * @param context
     * @param title   - title of the message resource id.
     * @param message - Content of the message resource id.
     */
    public static void showAlert(Context context, int title, int message) {
        showAlert(context, title, message, R.string.COMMON_ok, null, -1, null);
    }

    /**
     * To show the alert with message, positive button and negative button.
     *
     * @param context
     * @param message          - Content of the message.
     * @param positiveButton   - Positive button text.
     * @param posClickListener - Positive button click listener.
     * @param negativeButton   - Negative button text.
     * @param negClickListener - Negative button click listener.
     */
    public static void showAlert(final Context context, final String message, final String positiveButton, final ZaarkDialog.OnPositiveButtonClickListener posClickListener, final String negativeButton, final ZaarkDialog.OnNegativeButtonClickListener negClickListener) {

        showAlert(context, IScoreApplication.getAppContext().getString(R.string.app_name), message,
                positiveButton, posClickListener, negativeButton, negClickListener);
    }

    /**
     * To show the alert with message, positive button and negative button.
     *
     * @param context
     * @param message          - Content of the message resource id.
     * @param positiveButton   - Positive button text resource id.
     * @param posClickListener - Positive button click listener.
     * @param negativeButton   - Negative button text resource id.
     * @param negClickListener - Negative button click listener.
     */
    public static void showAlert(final Context context, final int message, final int positiveButton, final ZaarkDialog.OnPositiveButtonClickListener posClickListener, final int negativeButton, final ZaarkDialog.OnNegativeButtonClickListener negClickListener) {

        showAlert(context, R.string.app_name, message, positiveButton, posClickListener,
                negativeButton, negClickListener);
    }

    /**
     * To show the alert with title, message, positive button and negative button.
     *
     * @param context
     * @param title            - Title of the dialog
     * @param message          - Content of the message.
     * @param positiveButton   - Positive button text
     * @param posClickListener - Positive button click listener.
     * @param negativeButton   - Negative button text
     * @param negClickListener - Negative button click listener.
     */
    public static void showAlert(final Context context, final String title, final String message, final String positiveButton, final ZaarkDialog.OnPositiveButtonClickListener posClickListener, final String negativeButton, final ZaarkDialog.OnNegativeButtonClickListener negClickListener) {
        new ZaarkDialog.Builder(context).setTitle(title).setMessage(message)
                .setPositiveText(positiveButton).setNegativeText(negativeButton)
                .setOnPositiveButtonClickListener(posClickListener)
                .setOnNegativeButtonClickListener(negClickListener).setCancelable(false).show();
    }

    /**
     * To show the alert with title, message, positive button and negative button.
     *
     * @param context
     * @param title            - Title of the dialog resource id.
     * @param message          - Content of the message resource id.
     * @param positiveButton   - Positive button text resource id.
     * @param posClickListener - Positive button click listener.
     * @param negativeButton   - Negative button text resource id.
     * @param negClickListener - Negative button click listener.
     */
    public static void showAlert(final Context context, final int title, final int message, final int positiveButton, final ZaarkDialog.OnPositiveButtonClickListener posClickListener, final int negativeButton, final ZaarkDialog.OnNegativeButtonClickListener negClickListener) {
        new ZaarkDialog.Builder(context).setTitle(title).setMessage(message)
                .setPositiveText(positiveButton).setNegativeText(negativeButton)
                .setOnPositiveButtonClickListener(posClickListener)
                .setOnNegativeButtonClickListener(negClickListener).setCancelable(false).show();
    }
}
