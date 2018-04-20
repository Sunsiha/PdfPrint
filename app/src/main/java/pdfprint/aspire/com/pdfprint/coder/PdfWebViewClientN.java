package pdfprint.aspire.com.pdfprint.coder;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by sunisha on 4/5/2018.
 */

public class PdfWebViewClientN extends WebViewClient {

    private static final String TAG = "PdfWebViewClient";
    private static final String PDF_EXTENSION = ".pdf";
    private static final String PDF_VIEWER_URL = "http://docs.google.com/gview?embedded=true&url=";

    private Context mContext;
    private WebView mWebView;
    private ProgressDialog mProgressDialog;
    private boolean isLoadingPdfUrl;

    public PdfWebViewClientN(Context context, WebView webView)
    {
        mContext = context;
        mWebView = webView;
        mWebView.setWebViewClient(this);
    }

    public void loadPdfUrl(String url)
    {
        mWebView.stopLoading();

        if (!TextUtils.isEmpty(url))
        {
            isLoadingPdfUrl = isPdfUrl(url);
            if (isLoadingPdfUrl)
            {
                mWebView.clearHistory();
            }

            showProgressDialog();
        }

        mWebView.loadUrl(url);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String url)
    {
        return shouldOverrideUrlLoading(url);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl)
    {
        handleError(errorCode, description.toString(), failingUrl);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest request)
    {
        final Uri uri = request.getUrl();
        return shouldOverrideUrlLoading(webView, uri.toString());
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onReceivedError(final WebView webView, final WebResourceRequest request, final WebResourceError error)
    {
        final Uri uri = request.getUrl();
        handleError(error.getErrorCode(), error.getDescription().toString(), uri.toString());
    }

    @Override
    public void onPageFinished(final WebView view, final String url)
    {
        Log.i(TAG, "Finished loading. URL : " + url);
        dismissProgressDialog();
    }

    private boolean shouldOverrideUrlLoading(final String url)
    {
        Log.i(TAG, "shouldOverrideUrlLoading() URL : " + url);

        if (!isLoadingPdfUrl && isPdfUrl(url))
        {
            mWebView.stopLoading();

            final String pdfUrl = PDF_VIEWER_URL + url;

            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    loadPdfUrl(pdfUrl);
                }
            }, 1000);

            return true;
        }

        return false; // Load url in the webView itself
    }

    private void handleError(final int errorCode, final String description, final String failingUrl)
    {
        Log.e(TAG, "Error : " + errorCode + ", " + description + " URL : " + failingUrl);
    }

    private void showProgressDialog()
    {
        dismissProgressDialog();
        mProgressDialog = ProgressDialog.show(mContext, "", "Loading...");
    }

    private void dismissProgressDialog()
    {
        if (mProgressDialog != null && mProgressDialog.isShowing())
        {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    private boolean isPdfUrl(String url)
    {
        if (!TextUtils.isEmpty(url))
        {
            url = url.trim();
            int lastIndex = url.toLowerCase().lastIndexOf(PDF_EXTENSION);
            if (lastIndex != -1)
            {
                return url.substring(lastIndex).equalsIgnoreCase(PDF_EXTENSION);
            }
        }
        return false;
    }
}
