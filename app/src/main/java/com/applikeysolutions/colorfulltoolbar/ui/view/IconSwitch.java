package com.applikeysolutions.colorfulltoolbar.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.applikeysolutions.colorfulltoolbar.R;

public class IconSwitch extends FrameLayout {

    private boolean isChecked = false;

    public IconSwitch(@NonNull Context context) {
        super(context);
    }

    public IconSwitch(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IconSwitch(@NonNull Context context,
            @Nullable AttributeSet attrs,
            @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public IconSwitch(@NonNull Context context,
            @Nullable AttributeSet attrs,
            @AttrRes int defStyleAttr,
            @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_icon_switch, this);
        final ImageView indicator = (ImageView) findViewById(R.id.indicator);

        indicator.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(IconSwitch.this);

                final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) indicator.getLayoutParams();
                params.gravity = (!isChecked ? Gravity.END : Gravity.START);
                isChecked = !isChecked;
                indicator.setLayoutParams(params);
                indicator.setImageResource(!isChecked ? R.drawable.ic_pin : R.drawable.ic_menu);
            }
        });
    }
}
