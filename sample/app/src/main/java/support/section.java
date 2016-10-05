package support;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.linroid.androidlayoutextender.R;

import java.util.List;
import java.util.Map;

/**
 * @author linroid <linroid@gmail.com>
 * @since 03/10/2016
 */
public class section extends FrameLayout {
    private String mName;
    private ViewGroup mParent;

    public section(Context context) {
        super(context);
        checkEditMode();
    }

    public section(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public section(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        checkEditMode();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.section);
        mName = ta.getString(R.styleable.section_name);
        if (TextUtils.isEmpty(mName)) {
            throw new IllegalArgumentException("attribute name required");
        }
        ta.recycle();
    }

    private void checkEditMode() {
//        if (!isInEditMode()) {
//            throw new IllegalAccessError("support.section only works for support Android Studio preview");
//        }
    }

    public String getName() {
        return mName;
    }

    public void loadSection(Map<String, section> sections) {
        section shouldLoad = sections.get(mName);
        final ViewGroup parent = (ViewGroup) getParent();
        final int index = parent.indexOfChild(this);
        parent.removeViewInLayout(this);
        if (shouldLoad != null) { // 有重写 section
            List<View> shouldLoadChildren = Utils.getChildrenAsList(shouldLoad);
            int childCount = shouldLoadChildren.size();
            for (int i = 0; i < childCount; i++) {
//                final ViewGroup.LayoutParams layoutParams = getLayoutParams();
//                if (layoutParams != null) {
//                    parent.addView(shouldLoad, index, layoutParams);
//                } else {
//                    parent.addView(shouldLoad, index);
//                }
                View view = shouldLoadChildren.get(i);
                shouldLoad.removeView(view);
                RelativeLayout.LayoutParams
//                parent.addView(view, index + i);
                AttributeLayoutParams layoutParams = (AttributeLayoutParams) view.getLayoutParams();
//                    ViewGroup.LayoutParams params = parent.generateLayoutParams(layoutParams.getAttributeSet());
                ViewGroup.LayoutParams params = layoutParams.getLayoutParams();
                if ((parent instanceof section) || params == null) {
                    parent.addView(view, index + i);
                } else {
                    parent.addView(view, index + i, params);
                    throw new IllegalArgumentException(params.toString());
                }
            }
//            removeAllViews();
//            addView(shouldLoad);
        } else { // 子布局没有重写 section
            List<View> children = Utils.getChildrenAsList(this);
            int childCount = children.size();
            for (int i = 0; i < childCount; i++) {
                View view = children.get(i);
                if (view != null) {
                    removeView(view);
//                    parent.addView(view, index + i);
                    AttributeLayoutParams layoutParams = (AttributeLayoutParams) view.getLayoutParams();
//                    ViewGroup.LayoutParams params = parent.generateLayoutParams(layoutParams.getAttributeSet());
                    ViewGroup.LayoutParams params = layoutParams.getLayoutParams();
                    if ((parent instanceof layout) || params == null) {
                        parent.addView(view, index + i);
                    } else {
                        parent.addView(view, index + i, params);
                        throw new IllegalArgumentException(params.toString());
                    }
                }
            }
        }

        //            throw new IllegalStateException("section named " + mName + " not exists!");

//        setLayoutParams(shouldLoad.getLayoutParams());
        // TODO: 04/10/2016 处理子 layout 新增的 section
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        widthMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY);
//        heightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY);
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }


    @Override
    public AttributeLayoutParams generateLayoutParams(AttributeSet attrs) {
        AttributeLayoutParams attributeLayoutParams = new AttributeLayoutParams(getContext(), attrs);
        attributeLayoutParams.setAttributeSet(attrs);
//        attributeLayoutParams.setLayoutParams(new MarginLayoutParams(getContext(), attrs));
        if (mParent != null) {
            attributeLayoutParams.setLayoutParams(mParent.generateLayoutParams(attrs));
        }
        return attributeLayoutParams;
    }

    public void setParent(ViewGroup parent) {
        mParent = parent;
    }

    private class AttributeLayoutParams extends LayoutParams {
        private AttributeSet mAttributeSet;
        private ViewGroup.LayoutParams mLayoutParams;

        public AttributeLayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public AttributeLayoutParams(int width, int height) {
            super(width, height);
        }

        public AttributeLayoutParams(int width, int height, int gravity) {
            super(width, height, gravity);
        }

        public AttributeLayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public AttributeLayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public AttributeLayoutParams(LayoutParams source) {
            super(source);
        }

        public void setAttributeSet(AttributeSet attributeSet) {
            mAttributeSet = attributeSet;
        }

        public AttributeSet getAttributeSet() {
            return mAttributeSet;
        }

        public ViewGroup.LayoutParams getLayoutParams() {
            return mLayoutParams;
        }

        public void setLayoutParams(ViewGroup.LayoutParams layoutParams) {
            mLayoutParams = layoutParams;
            throw new IllegalArgumentException(layoutParams.toString());
        }
    }

}
