package com.creativethoughts.iscore.ui.widget;

/**
 * Created by muthukrishnan on 20/01/15.
 */

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.text.method.LinkMovementMethod;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.creativethoughts.iscore.R;

/**
 * Custom dialog to show the dialog like material dialog.
 */
public class ZaarkDialog extends AlertDialog implements DialogInterface.OnShowListener, View.OnClickListener {

    private final static String POSITIVE_BUTTON_TAG = "positive";
    private final static String NEGATIVE_BUTTON_TAG = "negative";
    private final View mView;
    private final Builder mBuilder;
    private OnShowListener mShowListener;
    private ImageView mIcon;
    private TextView mTitle;
    private View mTitleFrame;
    private View mPositiveButton;
    private View mNegativeButton;

    private ZaarkDialog(Builder builder) {
        super(new ContextThemeWrapper(builder.mContext, R.style.zaarkdialogfragment));

        mBuilder = builder;

        /**
         * Going to update custom view instead of default view.
         */
        mView = LayoutInflater.from(getContext()).inflate(R.layout.zaark_custom_dialog, null);

        this.setCancelable(builder.cancelable);

        mTitle = (TextView) mView.findViewById(R.id.title);
        mIcon = (ImageView) mView.findViewById(R.id.icon);
        mTitleFrame = mView.findViewById(R.id.titleFrame);

        final TextView content = (TextView) mView.findViewById(R.id.content);

        content.setText(builder.message);
        content.setMovementMethod(new LinkMovementMethod());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            //noinspection ResourceType
            mTitle.setTextAlignment(gravityToAlignment(builder.titleGravity));
        } else {
            mTitle.setGravity(gravityIntToGravity(builder.titleGravity));
        }

        mIcon.setVisibility(View.GONE);

        if (builder.title == null || builder.title.toString().trim().length() == 0) {
            mTitleFrame.setVisibility(View.GONE);
        } else {
            mTitle.setText(builder.title);
        }

        if (mBuilder.positiveText == null && mBuilder.negativeText == null) {
            mView.findViewById(R.id.buttonFrame).setVisibility(View.GONE);
        } else {
            mView.findViewById(R.id.buttonFrame).setVisibility(View.VISIBLE);

            mPositiveButton = mView.findViewById(R.id.buttonPositive);

            // Show the positive button when the positive text is not empty.
            if (mBuilder.positiveText != null) {

                TextView positiveTextView =
                        (TextView) ((FrameLayout) mPositiveButton).getChildAt(0);
                positiveTextView.setText(mBuilder.positiveText);
                positiveTextView.setTextColor(getActionTextStateList(R.color.colorPrimary));

                mPositiveButton.setTag(POSITIVE_BUTTON_TAG);
                mPositiveButton.setOnClickListener(this);
            } else {
                mPositiveButton.setVisibility(View.GONE);
            }

            mNegativeButton = mView.findViewById(R.id.buttonNegative);

            // Show the negative button when the negative text is not empty.
            if (mBuilder.negativeText != null) {
                TextView negativeTextView =
                        (TextView) ((FrameLayout) mNegativeButton).getChildAt(0);

                negativeTextView.setText(mBuilder.negativeText);
                negativeTextView.setTextColor(getActionTextStateList(R.color.colorPrimary));

                mNegativeButton.setTag(NEGATIVE_BUTTON_TAG);
                mNegativeButton.setVisibility(View.VISIBLE);
                mNegativeButton.setOnClickListener(this);
            } else {
                mNegativeButton.setVisibility(View.GONE);
            }
        }

        setView(mView);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            setInverseBackgroundForced(true);
            mTitle.setTextColor(Color.BLACK);
            content.setTextColor(Color.BLACK);
        }

    }

    /**
     * Set an alignment for jelly bean and new devices.
     *
     * @param gravity
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static int gravityToAlignment(ALIGNMENT gravity) {
        switch (gravity) {
            case CENTER:
                return View.TEXT_ALIGNMENT_CENTER;
            case END:
                return View.TEXT_ALIGNMENT_VIEW_END;
            default:
                return View.TEXT_ALIGNMENT_VIEW_START;
        }
    }

    /**
     * Set an alignment for pre Jelly bean devices.
     *
     * @param gravity
     * @return
     */
    private static int gravityIntToGravity(ALIGNMENT gravity) {
        switch (gravity) {
            case CENTER:
                return Gravity.CENTER_HORIZONTAL;
            case END:
                return Gravity.END;
            default:
                return Gravity.START;
        }
    }

    @Override
    public final void setOnShowListener(OnShowListener listener) {
        mShowListener = listener;
    }

    @Override
    public void onClick(View v) {
        String tag = (String) v.getTag();

        if (tag == POSITIVE_BUTTON_TAG) {
            if (mBuilder.onPositiveButtonClickListener != null) {
                mBuilder.onPositiveButtonClickListener.onClick();
            }
            dismiss();
        } else if (tag == NEGATIVE_BUTTON_TAG) {
            if (mBuilder.onNegativeButtonClickListener != null) {
                mBuilder.onNegativeButtonClickListener.onClick();
            }
            dismiss();
        }
    }

    @Override
    public void onShow(DialogInterface dialog) {
        if (mShowListener != null) {
            mShowListener.onShow(dialog);
        }
    }

    private int resolveColor(Context context, int attr) {
        return resolveColor(context, attr, 0);
    }

    private int resolveColor(Context context, int attr, int fallback) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});
        try {
            return a.getColor(0, fallback);
        } finally {
            a.recycle();
        }
    }

    /**
     * To adjust an alpha for a color.
     *
     * @param color
     * @param factor
     * @return
     */
    private int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    /**
     * Get text color state.
     *
     * @param newPrimaryColor
     * @return
     */
    private ColorStateList getActionTextStateList(int newPrimaryColor) {
        final int fallBackButtonColor = resolveColor(getContext(), android.R.attr.textColorPrimary);
        if (newPrimaryColor == 0) {
            newPrimaryColor = fallBackButtonColor;
        }
        int[][] states = new int[][]{new int[]{-android.R.attr.state_enabled}, // disabled
                new int[]{} // enabled
        };
        int[] colors = new int[]{adjustAlpha(newPrimaryColor, 0.4f), newPrimaryColor};
        return new ColorStateList(states, colors);
    }

    public final void setTitle(CharSequence title) {
        this.mTitle.setText(title);
    }

    /**
     * To align the texts.
     */
    public enum ALIGNMENT {
        START, CENTER, END
    }

    public interface OnPositiveButtonClickListener {
        public void onClick();
    }

    public interface OnNegativeButtonClickListener {
        public void onClick();
    }

    /**
     * Builder class to set all the alert dialog items.
     */
    public static class Builder {

        protected final Context mContext;
        protected CharSequence message;
        protected CharSequence title;
        protected CharSequence positiveText;
        protected CharSequence negativeText;
        protected boolean cancelable = true;

        protected OnPositiveButtonClickListener onPositiveButtonClickListener;
        protected OnNegativeButtonClickListener onNegativeButtonClickListener;

        protected ALIGNMENT titleGravity = ALIGNMENT.START;

        public Builder(Context context) {
            mContext = context;
        }

        /**
         * To set Title text resource id for Alert dialog.
         *
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            setTitle(this.mContext.getString(title));
            return this;
        }

        /**
         * To set the title text for alert dialog.
         *
         * @param title
         * @return
         */
        public Builder setTitle(CharSequence title) {
            this.title = title;
            return this;
        }

        /**
         * To set message text resource id for alert dialog.
         *
         * @param message
         * @return
         */
        public Builder setMessage(int message) {
            setMessage(this.mContext.getString(message));
            return this;
        }

        /**
         * To set message text for alert dialog.
         *
         * @param message
         * @return
         */
        public Builder setMessage(CharSequence message) {
            this.message = message;
            return this;
        }

        /**
         * Set positive text button.
         *
         * @param postiveRes
         * @return
         */
        public Builder setPositiveText(int postiveRes) {
            setPositiveText(this.mContext.getString(postiveRes));
            return this;
        }

        /**
         * Set positive text button.
         *
         * @param positiveText
         * @return
         */
        public Builder setPositiveText(CharSequence positiveText) {
            this.positiveText = positiveText;
            return this;
        }

        /**
         * To set negative text for negative button.
         *
         * @param negativeRes
         * @return
         */
        public Builder setNegativeText(int negativeRes) {
            if (negativeRes <= 0) {
                return this;
            }
            return setNegativeText(this.mContext.getString(negativeRes));
        }

        /**
         * To set negative text for negative button.
         *
         * @param negativeBtnText
         * @return
         */
        public Builder setNegativeText(CharSequence negativeBtnText) {
            this.negativeText = negativeBtnText;
            return this;
        }

        /**
         * Tos make Alert dialog as cancelable or not.
         *
         * @param cancelable
         * @return
         */
        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        /**
         * To set Positive Button click listener.
         *
         * @param buttonClickListener
         * @return
         */
        public Builder setOnPositiveButtonClickListener(OnPositiveButtonClickListener buttonClickListener) {
            this.onPositiveButtonClickListener = buttonClickListener;
            return this;
        }

        /**
         * To set Negaive Button click listener.
         *
         * @param buttonClickListener
         * @return
         */
        public Builder setOnNegativeButtonClickListener(OnNegativeButtonClickListener buttonClickListener) {
            this.onNegativeButtonClickListener = buttonClickListener;
            return this;
        }

        private ZaarkDialog build() {
            return new ZaarkDialog(this);
        }

        /**
         * To show Zaark Alert Dialog.
         *
         * @return
         */
        public ZaarkDialog show() {
            ZaarkDialog dialog = build();
            dialog.show();
            return dialog;
        }
    }
}

