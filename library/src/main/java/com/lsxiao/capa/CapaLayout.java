package com.lsxiao.capa;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AnimRes;
import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ViewAnimator;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * write with Capa
 * author:lsxiao
 * date:2017-05-18 01:04
 * github:https://github.com/lsxiao
 * zhihu:https://zhihu.com/people/lsxiao
 */

public class CapaLayout extends ViewAnimator {
    @Retention(SOURCE)
    @IntDef({LOAD, EMPTY, ERROR, CONTENT, NETWORK_ERROR})
    @interface StateMode {

    }

    public static final int LOAD = 0;
    public static final int EMPTY = LOAD + 1;
    public static final int ERROR = EMPTY + 1;
    public static final int CONTENT = ERROR + 1;
    public static final int NETWORK_ERROR = CONTENT + 1;


    private int mState = CONTENT;

    @LayoutRes
    private int mLoadLayoutId;
    @LayoutRes
    private int mEmptyLayoutId;
    @LayoutRes
    private int mErrorLayoutId;
    @LayoutRes
    private int mNetworkErrorLayoutId;

    private View mLoadView;
    private View mEmptyView;
    private View mErrorView;
    private View mContentView;
    private View mNetworkView;
    private View mInitView;
    private boolean mAnimEnable;
    private int mAnimIn;
    private int mAnimOut;

    public CapaLayout(Context context) {
        this(context, null);
    }

    public CapaLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        receiveAttributes(attrs);
    }

    private void receiveAttributes(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.CapaLayout);
        mState = isInEditMode() ? CONTENT : a.getInt(R.styleable.CapaLayout_cp_state, LOAD);
        mLoadLayoutId = a.getResourceId(R.styleable.CapaLayout_cp_load_layout, R.layout.capa_load_layout);
        mEmptyLayoutId = a.getResourceId(R.styleable.CapaLayout_cp_empty_layout, R.layout.capa_empty_layout);
        mErrorLayoutId = a.getResourceId(R.styleable.CapaLayout_cp_error_layout, R.layout.capa_error_layout);
        mNetworkErrorLayoutId = a.getResourceId(R.styleable.CapaLayout_cp_network_error_layout, R.layout.capa_network_error_layout);
        mAnimEnable = a.getBoolean(R.styleable.CapaLayout_cp_anim_enable, true);
        mAnimIn = a.getResourceId(R.styleable.CapaLayout_cp_anim_in, R.anim.capa_fade_in);
        mAnimOut = a.getResourceId(R.styleable.CapaLayout_cp_anim_out, R.anim.capa_fade_out);
        a.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }


    private void init() {
        initViews();
        initAnimation();
    }


    private void initViews() {
        check();

        mContentView = getChildAt(0);
        mLoadView = LayoutInflater.from(getContext()).inflate(mLoadLayoutId, this, false);
        mEmptyView = LayoutInflater.from(getContext()).inflate(mEmptyLayoutId, this, false);
        mErrorView = LayoutInflater.from(getContext()).inflate(mErrorLayoutId, this, false);
        mNetworkView = LayoutInflater.from(getContext()).inflate(mNetworkErrorLayoutId, this, false);

        addView(mEmptyView, getChildCount());
        addView(mErrorView, getChildCount());
        addView(mLoadView, getChildCount());
        addView(mNetworkView, getChildCount());

        hideAllView();
        findInitView().setVisibility(VISIBLE);
    }


    private void check() {
        if (getChildCount() != 1) {
            throw new IllegalArgumentException("Child node which in CapaLayout must exist, and there can be only one.(CapaLayout的子节点必须存在，且只能有一个。)");
        }
    }

    private View findInitView() {
        switch (mState) {
            case CONTENT:
                mInitView = mContentView;
                break;
            case LOAD:
                mInitView = mLoadView;
                break;
            case EMPTY:
                mInitView = mEmptyView;
                break;
            case ERROR:
                mInitView = mErrorView;
                break;
            case NETWORK_ERROR:
                mInitView = mNetworkView;
                break;
        }
        return mInitView;
    }

    private void hideAllView() {
        mContentView.setVisibility(GONE);
        mLoadView.setVisibility(GONE);
        mEmptyView.setVisibility(GONE);
        mErrorView.setVisibility(GONE);
    }

    private void initAnimation() {
        if (!mAnimEnable) {
            return;
        }
        animIn(mAnimIn);
        animOut(mAnimOut);
    }

    public void animFade() {
        animIn(R.anim.capa_fade_in);
        animOut(R.anim.capa_fade_out);
    }

    public void animSlideInTop() {
        animIn(R.anim.capa_slide_in_top);
        animOut(R.anim.capa_fade_out);
    }

    public void animSlideInBottom() {
        animIn(R.anim.capa_slide_in_bottom);
        animOut(R.anim.capa_fade_out);
    }

    public void animNone() {
        setInAnimation(null);
        setOutAnimation(null);
    }

    public void animIn(@AnimRes int in) {
        setInAnimation(getContext(), in);
    }

    public void animOut(@AnimRes int out) {
        setOutAnimation(getContext(), out);
    }

    public int getState() {
        return mState;
    }

    public boolean isState(@StateMode int state) {
        return mState == state;
    }

    private void to(int state) {
        if (mState == state) {
            return;
        }
        setDisplayedChild(getViewIndexByState(state));
        mState = state;
    }

    public void toContent() {
        to(CONTENT);
    }

    private int getViewIndexByState(@StateMode int state) {
        switch (state) {
            case LOAD:
                return indexOfChild(mLoadView);
            case EMPTY:
                return indexOfChild(mEmptyView);
            case ERROR:
                return indexOfChild(mErrorView);
            case NETWORK_ERROR:
                return indexOfChild(mNetworkView);
            case CONTENT:
            default:
                return indexOfChild(mContentView);
        }
    }

    public void toError() {
        to(ERROR);
    }

    public void toNetworkError() {
        to(NETWORK_ERROR);
    }

    public void toLoad() {
        to(LOAD);
    }

    public void toEmpty() {
        to(EMPTY);
    }


    public View getLoadView() {
        return mLoadView;
    }

    public View getEmptyView() {
        return mEmptyView;
    }

    public View getErrorView() {
        return mErrorView;
    }

    public View getContentView() {
        return mContentView;
    }

    public View getNetworkView() {
        return mNetworkView;
    }
}
