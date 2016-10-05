import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * @author linroid <linroid@gmail.com>
 * @since 03/10/2016
 */
public class Section extends ViewGroup {
    public Section(Context context) {
        super(context);
    }

    public Section(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Section(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
}
