package core.base.app;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.List;
import java.util.Map;

import core.base.util.HttpUtil;

/**
 * 封装了WebView的Fragment
 *
 * @author DrkCore
 * @since 2016年1月10日10:51:02
 */
public class WebFrag extends CoreFrag {

    private class FragWebClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            WebFrag.this.onPageStarted(url, favicon);
        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            super.onPageCommitVisible(view, url);
            WebFrag.this.onPageCommitVisible(url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            WebFrag.this.onPageFinished(url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return WebFrag.this.onAction(url, HttpUtil.getActionsFromUrl(url), HttpUtil.getUrlParamsFromUrl(url))
                    || super.shouldOverrideUrlLoading(webView, url);
        }
    }

	/*成员*/

    private int webViewId;
    private int layoutRes;

    private WebView webView;

    protected final WebView getWebView() {
        return webView;
    }

    public final int getWebViewId() {
        return webViewId;
    }

    protected final void configWebViewId(@IdRes int webViewId) {
        if (webView != null) {
            throw new IllegalStateException("ListView已经创建，此时无法设置webViewId");
        }
        this.webViewId = webViewId;
    }

    protected final void configLayoutRes(@LayoutRes int layoutRes) {
        if (webView != null) {
            throw new IllegalStateException("ListView已经创建，此时无法设置layoutRes");
        }
        this.layoutRes = layoutRes;
    }

	/* 继承 */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView;
        if (layoutRes <= 0) {// 并未指定布局，直接创建ListView
            contentView = new WebView(getActivity());
        } else {// 指定了布局
            contentView = inflater.inflate(layoutRes, container, false);
        }
        return contentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (view instanceof WebView) {// 布局本身就是ListView或者其子类
            webView = (WebView) view;
        } else if (webViewId > 0) {// 指定了id
            webView = (WebView) view.findViewById(webViewId);
        }

        if (webView == null) {// 不存在webView
            throw new IllegalStateException("可用的WebView不存在");
        }

        onPrepareWebView(webView);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            webView.pauseTimers();
        } else {
            webView.resumeTimers();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        webView.resumeTimers();
    }

    @Override
    public void onPause() {
        super.onPause();
        webView.pauseTimers();
    }

	/*内部回调*/

    protected void onPrepareWebView(WebView webView) {
        webView.setWebViewClient(new FragWebClient());
    }

    public void onPageStarted(String url, Bitmap favicon) {
    }

    public void onPageCommitVisible(String url) {
    }

    public void onPageFinished(String url) {
    }

    public boolean onAction(String url, List<String> actions, Map<String, String> params) {
        return false;
    }

}
