package support;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author linroid <linroid@gmail.com>
 * @since 05/10/2016
 */
public class Utils {
    public static final List<View> getChildrenAsList(@NonNull ViewGroup viewGroup) {
        int count = viewGroup.getChildCount();
        List<View> children = new ArrayList<>(count);
        for (int i=0; i<count; i++) {
            children.add(viewGroup.getChildAt(i));
        }
        return children;
    }
}
