package applikeysolutions.com.switchtoolbar.view;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.transition.AutoTransition;
import android.support.transition.TransitionManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import applikeysolutions.com.switchtoolbar.R;

//TODO add logic to retain state
public class Switch extends FrameLayout {

    private static final int[] COLOR_CHECKED_STATE_SET = {android.R.attr.state_checked};
    private static final int[] COLOR_UNCHECKED_STATE_SET = {-android.R.attr.state_checked};

    private static final int ANIMATION_DURATION_DEFAULT = 400;

    private ColorStateList mColorStateList;

    private int mAnimationDuration = ANIMATION_DURATION_DEFAULT;

    private boolean isChecked = false;
    private OnCheckedChangeListener mOnCheckedChangeListener;

    private ImageView mRight;
    private ImageView mLeft;

    private ImageView mLeftMask;
    private ImageView mRightMask;

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

    public void setColorStateList(ColorStateList colorStateList) {
        mColorStateList = colorStateList;
    }

    public void setDrawableLeft(@Nullable Drawable drawable) {
        if (drawable != null && drawable.getConstantState() != null) {
            applyDrawable(mLeft, drawable,
                    mColorStateList.getColorForState(COLOR_CHECKED_STATE_SET, mColorStateList.getDefaultColor()));

            applyDrawable(mLeftMask, drawable.getConstantState().newDrawable(), android.R.color.white);
        }
    }

    public void setDrawableRight(@Nullable Drawable drawable) {
        if (drawable != null && drawable.getConstantState() != null) {
            applyDrawable(mRight, drawable,
                    mColorStateList.getColorForState(COLOR_UNCHECKED_STATE_SET, mColorStateList.getDefaultColor()));

            applyDrawable(mRightMask, drawable.getConstantState().newDrawable(), android.R.color.white);
        }
    }

    public void setAnimationDuration(int animationDuration) {
        mAnimationDuration = animationDuration;
    }

    public boolean isChecked() {
        return isChecked;
    }

    private void init() {
        inflate(getContext(), R.layout.view_icon_switch, this);
        final ViewGroup container = (ViewGroup) findViewById(R.id.container);
        final LinearLayout indicator = (LinearLayout) findViewById(R.id.indicator);
        mLeft = (ImageView) findViewById(R.id.left);
        mRight = (ImageView) findViewById(R.id.right);

        mLeftMask = (ImageView) findViewById(R.id.left_mask);
        mRightMask = (ImageView) findViewById(R.id.right_mask);

        container.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(Switch.this, new AutoTransition().setDuration(mAnimationDuration));

                final LayoutParams indicatorLayoutParams = (LayoutParams) indicator.getLayoutParams();
                final LinearLayout.LayoutParams leftMaskLayoutParams = (LinearLayout.LayoutParams) mLeftMask.getLayoutParams();
                final LinearLayout.LayoutParams rightMaskLayoutParams = (LinearLayout.LayoutParams) mRightMask.getLayoutParams();

                final int colorChecked = mColorStateList.getColorForState(COLOR_CHECKED_STATE_SET,
                        mColorStateList.getDefaultColor());
                final int colorUnchecked = mColorStateList.getColorForState(COLOR_UNCHECKED_STATE_SET,
                        mColorStateList.getDefaultColor());

                if (isChecked) {
                    changeStateAnimation(indicator, colorUnchecked, colorChecked);
                    indicatorLayoutParams.gravity = (Gravity.START);

                    leftMaskLayoutParams.weight = 1;
                    rightMaskLayoutParams.weight = 0;
                } else {
                    changeStateAnimation(indicator, colorChecked, colorUnchecked);
                    indicatorLayoutParams.gravity = (Gravity.END);

                    leftMaskLayoutParams.weight = 0;
                    rightMaskLayoutParams.weight = 1;
                }

                indicator.setLayoutParams(indicatorLayoutParams);
                mLeftMask.setLayoutParams(leftMaskLayoutParams);
                mRightMask.setLayoutParams(rightMaskLayoutParams);
                isChecked = !isChecked;
                if (mOnCheckedChangeListener != null) {
                    mOnCheckedChangeListener.onCheckedChanged(isChecked);
                }
            }
        });
    }

    private void applyDrawable(ImageView imageView, @Nullable Drawable drawable, int color) {
        ThemeUtils.applyTint(getContext(), drawable, color);
        imageView.setImageDrawable(drawable);
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
