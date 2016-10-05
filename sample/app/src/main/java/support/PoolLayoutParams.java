package support;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.Map;

/**
 * @author linroid <linroid@gmail.com>
 * @since 05/10/2016
 */
public class PoolLayoutParams extends ViewGroup.LayoutParams {
    private static Map<Class<? extends View>, Class<? extends ViewGroup.LayoutParams>> sPoolMap = new HashMap<>();

    static {
        sPoolMap.put(RelativeLayout.class, RelativeLayout.LayoutParams.class);
        sPoolMap.put(LinearLayout.class, LinearLayout.LayoutParams.class);
        sPoolMap.put(FrameLayout.class, FrameLayout.LayoutParams.class);
        sPoolMap.put(RadioGroup.class, RadioGroup.LayoutParams.class);
        sPoolMap.put(GridLayout.class, GridLayout.LayoutParams.class);
    }

    public PoolLayoutParams(Context c, AttributeSet attrs) {
        super(c, attrs);
    }
}
