package applikeysolutions.com.switchtoolbar.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import applikeysolutions.com.switchtoolbar.R;

//TODO add logic to retain state
public class SwitchToolbar extends Toolbar {

    private static final int[] COLOR_CHECKED_STATE_SET = {android.R.attr.state_checked};
    private static final int[] COLOR_UNCHECKED_STATE_SET = {-android.R.attr.state_checked};

    private static final boolean WITH_RIPPLE_DEFAULT = true;
    private static final int ANIMATION_DURATION_DEFAULT = 300;

    private boolean mWithRipple = WITH_RIPPLE_DEFAULT;
    private int mAnimationDuration = ANIMATION_DURATION_DEFAULT;
    private ColorStateList mColorStateList;
    private Drawable mDrawableLeft;
    private Drawable mDrawableRight;

    private OnCheckedChangeListener mOnCheckedChangeListener;

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

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        mOnCheckedChangeListener = onCheckedChangeListener;
    }

    public boolean isChecked() {
        return mSwitch.isChecked();
    }

    private void inflate(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwitchToolbar, 0, defStyleAttr);
            try {
                if (typedArray.hasValue(R.styleable.SwitchToolbar_itemColor)) {
                    mColorStateList = typedArray.getColorStateList(R.styleable.SwitchToolbar_itemColor);
                }

                if (typedArray.hasValue(R.styleable.SwitchToolbar_drawableLeft)) {
                    mDrawableLeft = typedArray.getDrawable(R.styleable.SwitchToolbar_drawableLeft);
                }

                if (typedArray.hasValue(R.styleable.SwitchToolbar_drawableRight)) {
                    mDrawableRight = typedArray.getDrawable(R.styleable.SwitchToolbar_drawableRight);
                }

                if (typedArray.hasValue(R.styleable.SwitchToolbar_withRipple)) {
                    mWithRipple = typedArray.getBoolean(R.styleable.SwitchToolbar_withRipple, WITH_RIPPLE_DEFAULT);
                }

                mAnimationDuration = typedArray.getInt(R.styleable.SwitchToolbar_animationDuration, ANIMATION_DURATION_DEFAULT);

            } finally {
                typedArray.recycle();
            }
        }

        if (mColorStateList == null) {
            mColorStateList = createDefaultColorStateList();
        }
    }

    private void init() {
        mRipple = createRippleView();
        mSwitch = (Switch) createSwitch();
        mSwitch.setColorStateList(mColorStateList);
        mSwitch.setDrawableLeft(mDrawableLeft);
        mSwitch.setDrawableRight(mDrawableRight);
        mSwitch.setAnimationDuration(mAnimationDuration);

        mSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean isChecked) {
                changeStateAnimation(mSwitch, mRipple,
                        mColorStateList.getColorForState(isChecked ? COLOR_CHECKED_STATE_SET : COLOR_UNCHECKED_STATE_SET,
                                mColorStateList.getDefaultColor()));
                if (mOnCheckedChangeListener != null) {
                    mOnCheckedChangeListener.onCheckedChanged(isChecked);
                }
            }
        });

        final FrameLayout frameLayout = new FrameLayout(getContext());
        final Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                getActionBarSize(getContext()));
        frameLayout.setLayoutParams(layoutParams);
        layoutParams.gravity = Gravity.END;

        frameLayout.addView(mRipple);
        frameLayout.addView(mSwitch);
        addView(frameLayout);
    }

    private void changeStateAnimation(final View source, final View ripple, final @ColorRes int endColor) {
        final List<Animator> animators = new ArrayList<>();

        final AnimatorSet animationSet = new AnimatorSet();
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.setDuration(mAnimationDuration);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mWithRipple) {
            animators.add(createRippleAnimator(source, ripple, endColor));
        }

        animators.add(createFadeAnimator(ripple));
        animators.add(createColorEvaluationAnimator(endColor));

        animationSet.playTogether(animators);
        new NoPauseAnimator(animationSet).start();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private Animator createRippleAnimator(final View source, final View ripple, final @ColorRes int endColor) {
        ripple.setBackgroundColor(ContextCompat.getColor(getContext(), endColor));

        int w = ripple.getWidth() - source.getWidth() / 2;
        int h = ripple.getHeight() - source.getHeight() / 2;

        final int startRadius = 0;
        final int endRadius = (int) Math.hypot(w, h) / 2;

        final Animator animator = ViewAnimationUtils.createCircularReveal(ripple, w, h, startRadius, endRadius);
        animator.addListener(new Animator.AnimatorListener() {
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

        return animator;
    }

    private Animator createFadeAnimator(final View ripple) {
        return ObjectAnimator.ofFloat(ripple, View.ALPHA, 1f, 0f);
    }

    private Animator createColorEvaluationAnimator(final @ColorRes int endColor) {
        final ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(),
                ((ColorDrawable) getBackground()).getColor(),
                ContextCompat.getColor(getContext(), endColor));
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                setBackgroundColor((int) animator.getAnimatedValue());
            }
        });

        return colorAnimator;
    }

    private ColorStateList createDefaultColorStateList() {
        int colorAccent = ThemeUtils.getThemeColorRes(getContext(), R.attr.colorAccent);
        int colorPrimary = ThemeUtils.getThemeColorRes(getContext(), R.attr.colorPrimary);
        return new ColorStateList(new int[][] {
                COLOR_CHECKED_STATE_SET,
                COLOR_CHECKED_STATE_SET,
                EMPTY_STATE_SET
        }, new int[] {
                colorAccent,
                colorPrimary,
                colorPrimary
        });
    }

    private int getActionBarSize(Context context) {
        final TypedValue typedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
            return TypedValue.complexToDimensionPixelSize(typedValue.data, getResources().getDisplayMetrics());
        }
        return -1;
    }

    private View createRippleView() {
        final View view = new View(getContext());
        final FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(200, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        view.setVisibility(GONE);
        return view;
    }

    private View createSwitch() {
        final View view = new Switch(getContext());
        final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.END | Gravity.CENTER_VERTICAL);
        layoutParams.setMarginEnd(getContext().getResources().getDimensionPixelOffset(R.dimen.switch_padding_right));
        view.setLayoutParams(layoutParams);
        return view;
    }

    public interface OnCheckedChangeListener {

        void onCheckedChanged(boolean isChecked);

    }
}
