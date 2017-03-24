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
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CompoundButton;

import com.applikeysolutions.colorfulltoolbar.R;

public class ColorfulToolbar extends Toolbar {

    public ColorfulToolbar(Context context) {
        super(context);
        init();
    }

    public ColorfulToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColorfulToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_menu_ripple, this);

        final SwitchCompat switchCompat = (SwitchCompat) findViewById(R.id.switcher);
        final View color = findViewById(R.id.color);

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    createAnimator(switchCompat, color, R.color.colorAccent).start();
                } else {
                    createAnimator(switchCompat, color, R.color.colorPrimary).start();
                }
            }

            private Animator createAnimator(final View source, final View ripple, final @ColorRes int endColor) {
                int w = getWidth() - source.getWidth() / 2;
                int h = getHeight() - source.getHeight() / 2;
                final int startRadius = 0;
                final int endRadius = (int) Math.hypot(w, h);
                ripple.setBackgroundColor(ContextCompat.getColor(getContext(), endColor));

                final Animator revealAnimation = ViewAnimationUtils.createCircularReveal(ripple, w, h, startRadius, endRadius);
                final Animator fadeOut = ObjectAnimator.ofFloat(ripple, View.ALPHA, 0.8f, 0f);

                final AnimatorSet animationSet = new AnimatorSet();
                animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
                animationSet.setDuration(1000);
                animationSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        ripple.setVisibility(VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ripple.setVisibility(GONE);

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        ripple.setVisibility(GONE);

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

                final ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(),
                        ((ColorDrawable) getBackground()).getColor(),
                        ContextCompat.getColor(getContext(), endColor));
                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        setBackgroundColor((int) animator.getAnimatedValue());
                    }

                });

                animationSet.playTogether(revealAnimation, colorAnimation, fadeOut);

                return animationSet;
            }
        });
    }
}
