package support;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


import com.linroid.plugin.support.R;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author linroid <linroid@gmail.com>
 * @since 03/10/2016
 */
public class layout extends FrameLayout {
    public static final int INVALID_LAYOUT_ID = -1;
    private Map<String, section> mSections = new HashMap<>();
    private int mParentLayoutId = -1;
    private boolean mPreviewRoot;
    private List<ViewGroup.LayoutParams> mAttributeSets = new ArrayList<>();
    private boolean mInflating;
    private layout mParentView;

    public layout(Context context) {
        super(context);
        checkEditMode();
    }

    public layout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public layout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        checkEditMode();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.layout);
        mParentLayoutId = ta.getResourceId(R.styleable.layout_parent, INVALID_LAYOUT_ID);
        if (!isRootLayout()) {
            mPreviewRoot = true;
            LayoutInflater inflater = LayoutInflater.from(context);
//            View view = inflater.inflate(mParentLayoutId, this, false);
            Factory2Proxy factory2Proxy = null;
            if (inflater.getFactory2() == null) {
                factory2Proxy = new Factory2Proxy();
                inflater.setFactory2(factory2Proxy);
            } else {
                if (inflater.getFactory2() instanceof Factory2Proxy) {
                    factory2Proxy = (Factory2Proxy) inflater.getFactory2();
                }
            }
            if (factory2Proxy != null) {
                factory2Proxy.setDelegateFactory(new LayoutFactory());
            }

            mInflating = true;
            XmlPullParser parser = context.getResources().getLayout(mParentLayoutId);
            View view = inflater.inflate(parser, this, false);
            if (view instanceof layout) {
                mParentView = (layout) view;
                mParentView.setPreviewRoot(false);
            } else {
                throw new IllegalStateException("not support");
            }
            addView(mParentView);
            mInflating = false;
        }
        ta.recycle();
    }

    public boolean isPreviewRoot() {
//        ViewParent parent = getParent();
//        if (parent != null && parent instanceof layout) {
//            return false;
//        }
//        if (!isInEditMode()) {
//            if (parent == null) {
//                return false;
//            }
//        }
//        return true;
        return mPreviewRoot;
    }

    public void setPreviewRoot(boolean previewRoot) {
        mPreviewRoot = previewRoot;
    }

    /**
     * is the root layout?
     */
    private boolean isRootLayout() {
        return mParentLayoutId == INVALID_LAYOUT_ID;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        int childCount = getChildCount();
        List<View> children = Utils.getChildrenAsList(this);
        for (int i = 0; i < childCount; i++) {
            View child = children.get(i);
            if (child instanceof section) {
                removeView(child);
                putSection(((section) child).getName(), (section) child);
            }
//            else if (!isRootLayout()) {
//                throw new IllegalStateException("not support!");
//            }
        }
        if (isPreviewRoot()) {
            layout rootLayout = findRootLayout(this);
            if (rootLayout == null) {
                throw new IllegalStateException("find root layout failed");
            }
            rootLayout.replaceSections(this, mSections);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewGroup parent = (ViewGroup) getParent();
        final int index = parent.indexOfChild(this);
        parent.removeViewInLayout(this);
        List<View> children = Utils.getChildrenAsList(this);
        int childCount = children.size();
        for (int i = 0; i < childCount; i++) {
            View view = children.get(i);
            AttributeLayoutParams layoutParams = (AttributeLayoutParams) view.getLayoutParams();
//                    ViewGroup.LayoutParams params = parent.generateLayoutParams(layoutParams.getAttributeSet());
            ViewGroup.LayoutParams params = layoutParams.getLayoutParams();
            removeView(view);
            if ((parent instanceof layout) || params == null) {
                parent.addView(view, index + i);
            } else {
                parent.addView(view, index + i, params);
            }
        }
    }
//    @Override
//    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
//        return findRootView().generateLayoutParams(attrs);
//    }


    @Override
    public AttributeLayoutParams generateLayoutParams(AttributeSet attrs) {
        LayoutParams layoutParams = super.generateLayoutParams(attrs);
        AttributeLayoutParams attributeLayoutParams = new AttributeLayoutParams(layoutParams);
        attributeLayoutParams.setAttributeSet(attrs);
        attributeLayoutParams.setLayoutParams(new MarginLayoutParams(getContext(), attrs));

        return attributeLayoutParams;
    }

    private section findSectionHolderByName(String sectionName) {
        return findSectionHolderByName(this, sectionName);
    }

    private section findSectionHolderByName(View view, String sectionName) {
        if (view instanceof section) {
            if (sectionName.equals(((section) view).getName())) {
                return (section) view;
            }
            return null;
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                section found = findSectionHolderByName(child, sectionName);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    public static class AttributeLayoutParams extends LayoutParams {
        private AttributeSet mAttributeSet;
        private MarginLayoutParams mLayoutParams;
        private View mParent;

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

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public AttributeLayoutParams(LayoutParams source) {
            super(source);
        }

        public void setAttributeSet(AttributeSet attributeSet) {
            mAttributeSet = attributeSet;
        }

        public AttributeSet getAttributeSet() {
            return mAttributeSet;
        }

        public void setLayoutParams(MarginLayoutParams layoutParams) {
            mLayoutParams = layoutParams;
        }

        public MarginLayoutParams getLayoutParams() {
            return mLayoutParams;
        }

        public void setParent(View parent) {
            mParent = parent;
        }
    }

    private ViewGroup findRootView() {
        ViewGroup parent = (ViewGroup) getParent();
        if (parent instanceof layout) {
            return ((layout) parent).findRootView();
        } else {
            return parent;
        }
    }

    /**
     * 遍历所有的 View
     */
    private void replaceSections(ViewGroup viewGroup, Map<String, section> sections) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ViewGroup) {
                if (child instanceof section) {
                    ((section) child).loadSection(sections);
                } else {
                    replaceSections((ViewGroup) child, sections);
                }
            }
        }
    }


    private layout findRootLayout(View view) {
        if (view instanceof layout) {
            if (((layout) view).isRootLayout()) {
                return (layout) view;
            }
            int childCount = ((layout) view).getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = ((layout) view).getChildAt(i);
                layout found = findRootLayout(child);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private void checkEditMode() {
//        if (!isInEditMode()) {
//            throw new IllegalAccessError("support.layout only works for support Android Studio preview");
//        }
    }

    private section findSectionByName(String name) {
//        int childCount = getChildCount();
//        for (int i = 0; i < childCount; i++) {
//            View child = getChildAt(i);
//            if (child instanceof section) {
//                if (name.equals(((section) child).getName()) {
//                    return (section) child;
//                }
//            }
//        }
//        return null;
        return mSections.get(name);
    }

    private void putSection(String name, section sec) {
        if (isPreviewRoot()) {
            if (!mSections.containsKey(name)) {
                mSections.put(name, sec);
            }
        } else {
            getParentLayout().putSection(name, sec);
        }
    }

    public layout getParentLayout() {
        if (!isRootLayout()) {
            return (layout) getParent();
        }
        return null;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    class LayoutFactory implements LayoutInflater.Factory2 {
        @Override
        public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
            if ("support.section".equals(name)) {
                section sec = new section(context, attrs);
                String sectionName = sec.getName();
                if (!mInflating && mParentView != null) {
                    section found = mParentView.findSectionHolderByName(sectionName);
                    if (found == null) {
                        throw new IllegalStateException("could not find section:" + sectionName);
                    }
                    ViewGroup viewGroup = (ViewGroup) found.getParent();
                    sec.setParent(viewGroup);
                }
                return sec;
            }
            return null;
        }

        @Override
        public View onCreateView(String name, Context context, AttributeSet attrs) {
            if (name.contains("section")) {
                throw new IllegalStateException(name);
            }
            return null;
        }
    }
}
