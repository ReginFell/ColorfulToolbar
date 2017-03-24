package com.applikeysolutions.colorfulltoolbar.ui.view;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.transition.TransitionManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.applikeysolutions.colorfulltoolbar.R;

public class IconSwitch extends FrameLayout {

    private boolean isChecked = false;
    private OnCheckedChangeListener mOnCheckedChangeListener;

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

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        mOnCheckedChangeListener = onCheckedChangeListener;
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
        final ViewGroup container = (ViewGroup) findViewById(R.id.container);
        final LinearLayout indicator = (LinearLayout) findViewById(R.id.indicator);
        final View leftMask = findViewById(R.id.left_mask);
        final View rightMask = findViewById(R.id.right_mask);

        container.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(IconSwitch.this);

                final FrameLayout.LayoutParams indicatorLayoutParams = (FrameLayout.LayoutParams) indicator.getLayoutParams();
                final LinearLayout.LayoutParams leftMaskLayoutParams = (LinearLayout.LayoutParams) leftMask.getLayoutParams();
                final LinearLayout.LayoutParams rightMaskLayoutParams = (LinearLayout.LayoutParams) rightMask.getLayoutParams();

                if (isChecked) {
                    changeStateAnimation(indicator, R.color.colorPrimary, R.color.colorAccent);
                    indicatorLayoutParams.gravity = (Gravity.START);

                    leftMaskLayoutParams.weight = 1;
                    rightMaskLayoutParams.weight = 0;
                } else {
                    changeStateAnimation(indicator, R.color.colorAccent, R.color.colorPrimary);
                    indicatorLayoutParams.gravity = (Gravity.END);

                    leftMaskLayoutParams.weight = 0;
                    rightMaskLayoutParams.weight = 1;
                }

                indicator.setLayoutParams(indicatorLayoutParams);
                leftMask.setLayoutParams(leftMaskLayoutParams);
                rightMask.setLayoutParams(rightMaskLayoutParams);
                isChecked = !isChecked;
                if (mOnCheckedChangeListener != null) {
                    mOnCheckedChangeListener.onCheckedChanged(isChecked);
                }
            }
        });
    }

    private void changeStateAnimation(final View source, final @ColorRes int fromColor, final @ColorRes int endColor) {
        final ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(),
                ContextCompat.getColor(getContext(), fromColor),   ContextCompat.getColor(getContext(), endColor));
        colorAnimation.setDuration(400);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                final Drawable wrappedDrawable = DrawableCompat.wrap(source.getBackground()).mutate();
                DrawableCompat.setTint(wrappedDrawable, (int) animator.getAnimatedValue());
            }
        });

        new NoPauseAnimator(colorAnimation).start();
    }


    public interface OnCheckedChangeListener {
        void onCheckedChanged(boolean isChecked);
    }
}
