package com.applikeysolutions.colorfulltoolbar.ui.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.applikeysolutions.colorfulltoolbar.R;

public class SwitchToolbar extends Toolbar {

    private Switch mSwitch;
    private View mRipple;

    public SwitchToolbar(Context context) {
        super(context);
        init();
    }

    public SwitchToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SwitchToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_menu_ripple, this);

        mSwitch = (Switch) findViewById(R.id.switcher);
        mRipple = findViewById(R.id.ripple);

        mSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean isChecked) {
                if (isChecked) {
                    changeStateAnimation(mSwitch, mRipple, R.color.colorAccent);
                } else {
                    changeStateAnimation(mSwitch, mRipple, R.color.colorPrimary);
                }
            }
        });
    }

    private void changeStateAnimation(final View source, final View ripple, final @ColorRes int endColor) {
        ripple.setBackgroundColor(ContextCompat.getColor(getContext(), endColor));

        int w = ripple.getWidth() - source.getWidth() / 2;
        int h = ripple.getHeight() - source.getHeight() / 2;

        final int startRadius = 0;
        final int endRadius = (int) Math.hypot(w, h) / 2;

        final Animator revealAnimator = ViewAnimationUtils.createCircularReveal(ripple, w, h, startRadius, endRadius);
        final Animator fadeOut = ObjectAnimator.ofFloat(ripple, View.ALPHA, 1f, 0f);

        final AnimatorSet animationSet = new AnimatorSet();
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.setDuration(400);

        final ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(),
                ((ColorDrawable) getBackground()).getColor(),
                ContextCompat.getColor(getContext(), endColor));
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                setBackgroundColor((int) animator.getAnimatedValue());
            }
        });

        animationSet.playTogether(revealAnimator, colorAnimation, fadeOut);

        new NoPauseAnimator(animationSet).start();
    }
}
