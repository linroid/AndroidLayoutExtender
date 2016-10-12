package support;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

/**
 * @author linroid <linroid@gmail.com>
 * @since 05/10/2016
 */
public class Factory2Proxy implements LayoutInflater.Factory2 {
    private LayoutInflater.Factory2 mDelegateFactory;

    public void setDelegateFactory(LayoutInflater.Factory2 delegateFactory) {
        mDelegateFactory = delegateFactory;
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        if (mDelegateFactory != null) {
            mDelegateFactory.onCreateView(parent, name, context, attrs);
        }
        return null;
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return null;
    }
}
