package applikeysolutions.com.switchtoolbar.view;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.transition.AutoTransition;
import android.support.transition.TransitionManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import applikeysolutions.com.switchtoolbar.R;

public class Switch extends FrameLayout {

    private static final int UNCHECKED_COLOR_DEFAULT = R.attr.colorAccent;
    private static final int CHECKED_COLOR_DEFAULT = R.attr.colorPrimary;
    private static final int ANIMATION_DURATION_DEFAULT = 400;

    @ColorRes private int mUncheckedColor = UNCHECKED_COLOR_DEFAULT;
    @ColorRes private int mCheckedColor = CHECKED_COLOR_DEFAULT;
    private int mAnimationDuration = ANIMATION_DURATION_DEFAULT;

    private boolean isChecked = false;
    private OnCheckedChangeListener mOnCheckedChangeListener;

    public Switch(Context context) {
        super(context);
        init();
    }

    public Switch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Switch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Switch(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        mOnCheckedChangeListener = onCheckedChangeListener;
    }

    public void setUncheckedColor(int uncheckedColor) {
        mUncheckedColor = uncheckedColor;
    }

    public void setCheckedColor(int checkedColor) {
        mCheckedColor = checkedColor;
    }

    public void setAnimationDuration(int animationDuration) {
        mAnimationDuration = animationDuration;
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
                TransitionManager.beginDelayedTransition(Switch.this, new AutoTransition().setDuration(mAnimationDuration));

                final LayoutParams indicatorLayoutParams = (LayoutParams) indicator.getLayoutParams();
                final LinearLayout.LayoutParams leftMaskLayoutParams = (LinearLayout.LayoutParams) leftMask.getLayoutParams();
                final LinearLayout.LayoutParams rightMaskLayoutParams = (LinearLayout.LayoutParams) rightMask.getLayoutParams();

                if (isChecked) {
                    changeStateAnimation(indicator, mUncheckedColor, mCheckedColor);
                    indicatorLayoutParams.gravity = (Gravity.START);

                    leftMaskLayoutParams.weight = 1;
                    rightMaskLayoutParams.weight = 0;
                } else {
                    changeStateAnimation(indicator, mCheckedColor, mUncheckedColor);
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
                ContextCompat.getColor(getContext(), fromColor), ContextCompat.getColor(getContext(), endColor));
        colorAnimation.setDuration(mAnimationDuration);
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
