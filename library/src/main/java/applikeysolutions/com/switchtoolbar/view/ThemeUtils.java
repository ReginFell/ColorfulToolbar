package applikeysolutions.com.switchtoolbar.view;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.util.TypedValue;

final class ThemeUtils {

    @IdRes
    public static int getThemeColorRes(@NonNull final Context context, @AttrRes final int attributeColor) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(attributeColor, value, true);
        return value.resourceId;
    }
}
