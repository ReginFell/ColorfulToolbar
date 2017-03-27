package applikeysolutions.com.switchtoolbar.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;

import applikeysolutions.com.switchtoolbar.R;

public class SwitchToolbar extends Toolbar {

    private static final int UNCHECKED_COLOR_DEFAULT = R.attr.colorAccent;
    private static final int CHECKED_COLOR_DEFAULT = R.attr.colorPrimary;
    private static final boolean WITH_RIPPLE_DEFAULT = true;
    private static final int ANIMATION_DURATION_DEFAULT = 400;

    @ColorRes private int mUncheckedColor = UNCHECKED_COLOR_DEFAULT;
    @ColorRes private int mCheckedColor = CHECKED_COLOR_DEFAULT;
    private boolean mWithRipple = WITH_RIPPLE_DEFAULT;
    private int mAnimationDuration = ANIMATION_DURATION_DEFAULT;

    private Switch mSwitch;
    private View mRipple;

    public SwitchToolbar(Context context) {
        super(context);
        inflate(context, null, 0);
        init();
    }

    public SwitchToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, attrs, 0);
        init();
    }

    public SwitchToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, attrs, defStyleAttr);
        init();
    }

    private void inflate(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwitchToolbar, 0, defStyleAttr);
            try {
                mUncheckedColor = typedArray.getResourceId(R.styleable.SwitchToolbar_uncheckedColor,
                        ThemeUtils.getThemeColorRes(getContext(), UNCHECKED_COLOR_DEFAULT));
                mCheckedColor = typedArray.getResourceId(R.styleable.SwitchToolbar_checkedColor,
                        ThemeUtils.getThemeColorRes(getContext(), CHECKED_COLOR_DEFAULT));
                mWithRipple = typedArray.getBoolean(R.styleable.SwitchToolbar_withRipple, WITH_RIPPLE_DEFAULT);
                mAnimationDuration = typedArray.getInt(R.styleable.SwitchToolbar_animationDuration, ANIMATION_DURATION_DEFAULT);

            } finally {
                typedArray.recycle();
            }
        }
    }

    private void init() {
        inflate(getContext(), R.layout.view_menu_ripple, this);

        mSwitch = (Switch) findViewById(R.id.switcher);
        mSwitch.setCheckedColor(mCheckedColor);
        mSwitch.setUncheckedColor(mUncheckedColor);

        mRipple = findViewById(R.id.ripple);

        mSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean isChecked) {
                changeStateAnimation(mSwitch, mRipple, isChecked ? mCheckedColor : mUncheckedColor);
            }
        });
    }

    private void changeStateAnimation(final View source, final View ripple, final @ColorRes int endColor) {
        final AnimatorSet animationSet = new AnimatorSet();
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.setDuration(mAnimationDuration);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mWithRipple) {
            animationSet.play(createRippleAnimator(source, ripple, endColor));
        }

        final Animator fadeOut = ObjectAnimator.ofFloat(ripple, View.ALPHA, 1f, 0f);

        final ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(),
                ((ColorDrawable) getBackground()).getColor(),
                ContextCompat.getColor(getContext(), endColor));
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                setBackgroundColor((int) animator.getAnimatedValue());
            }
        });

        animationSet.playTogether(colorAnimation, fadeOut);

        new NoPauseAnimator(animationSet).start();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private Animator createRippleAnimator(final View source, final View ripple, final @ColorRes int endColor) {
        ripple.setBackgroundColor(ContextCompat.getColor(getContext(), endColor));

        int w = ripple.getWidth() - source.getWidth() / 2;
        int h = ripple.getHeight() - source.getHeight() / 2;

        final int startRadius = 0;
        final int endRadius = (int) Math.hypot(w, h) / 2;

        return ViewAnimationUtils.createCircularReveal(ripple, w, h, startRadius, endRadius);
    }
}
