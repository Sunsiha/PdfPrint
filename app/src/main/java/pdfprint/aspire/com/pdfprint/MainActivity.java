package pdfprint.aspire.com.pdfprint;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.shockwave.pdfium.PdfPasswordException;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import pdfprint.aspire.com.pdfprint.coder.PrintDocumentAdapterN;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    WebView webViewSoftwareUpdate;
    LinearLayout printLL;
    //    private ProgressDialog progressBar;
    PDFView pdfView;
    RelativeLayout PrintRL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        pdfView = (PDFView) findViewById(R.id.pdfView);
        PrintRL = (RelativeLayout) findViewById(R.id.PrintRL);

        new DownloadAsycTask().execute("http://gahp.net/wp-content/uploads/2017/09/sample.pdf");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView titleTV = (TextView) toolbar.findViewById(R.id.titleTV);
        printLL = (LinearLayout) toolbar.findViewById(R.id.printLL);
        printLL.setOnClickListener(this);
        setSupportActionBar(toolbar);
        titleTV.setText("Test");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        webViewSoftwareUpdate = (WebView) findViewById(R.id.webViewSoftwareUpdate);
        if (Build.VERSION.SDK_INT >= 19) {
            webViewSoftwareUpdate.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webViewSoftwareUpdate.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        WebSettings settings = webViewSoftwareUpdate.getSettings();
        settings.setJavaScriptEnabled(true);
        webViewSoftwareUpdate.getSettings().setBuiltInZoomControls(true);
        webViewSoftwareUpdate.getSettings().setDisplayZoomControls(false);
        webViewSoftwareUpdate.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webViewSoftwareUpdate.getSettings().setLoadWithOverviewMode(true);
        webViewSoftwareUpdate.getSettings().setUseWideViewPort(true);
        webViewSoftwareUpdate.getSettings().setAllowFileAccess(true);
        webViewSoftwareUpdate.getSettings().setPluginState(WebSettings.PluginState.ON);
        webViewSoftwareUpdate.getSettings().setDomStorageEnabled(true);
        webViewSoftwareUpdate.clearView();
        webViewSoftwareUpdate.getSettings().setAllowContentAccess(true);
        webViewSoftwareUpdate.getSettings().setDatabaseEnabled(true);
        webViewSoftwareUpdate.getSettings().setUseWideViewPort(true);
//        progressBar = ProgressDialog.show(StockViewPrintDetailActivity.this, "", "Loading...");
/*
        webViewSoftwareUpdate.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                webViewSoftwareUpdate.setVisibility(View.GONE);
            }
        });
*/
//        webViewSoftwareUpdate.loadUrl(viewUrl);
        init("http://gahp.net/wp-content/uploads/2017/09/sample.pdf", webViewSoftwareUpdate);

    }

    private void init(String viewUrl, WebView webview) {
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        PdfWebViewClient pdfWebViewClient = new PdfWebViewClient(this, webview);
        pdfWebViewClient.loadPdfUrl(viewUrl);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private void showPrintView(View v) {
        PopupMenu popup = new PopupMenu(MainActivity.this, v);
        popup.getMenuInflater().inflate(R.menu.print_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem menu_item) {
                switch (menu_item.getItemId()) {
                    case R.id.action_settings:
//                        createWebPagePrint(webViewSoftwareUpdate);
                        printPDF(pdfView);
                        break;
                }
                return true;
            }
        });
        MenuPopupHelper menuHelper = new MenuPopupHelper(MainActivity.this, (MenuBuilder) popup.getMenu(), v);
        menuHelper.setForceShowIcon(true);
        menuHelper.setGravity(Gravity.END);
        menuHelper.show();
    }

    //    public void createWebPagePrint(WebView webView) {
//        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
//        PrintDocumentAdapter printAdapter = null;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            printAdapter = webView.createPrintDocumentAdapter();
//            String jobName = getString(R.string.app_name) + " Document";
//            PrintAttributes.Builder builder = null;
//            builder = new PrintAttributes.Builder();
//            builder.setMediaSize(PrintAttributes.MediaSize.ISO_A4);
//            PrintJob printJob = null;
//            printJob = printManager.print(jobName, printAdapter, builder.build());
//            if (printJob.isCompleted()) {
//                Toast.makeText(getApplicationContext(), "Print Complete", Toast.LENGTH_LONG).show();
//            } else if (printJob.isFailed()) {
//                Toast.makeText(getApplicationContext(), "Print Failed", Toast.LENGTH_LONG).show();
//            }
//            builder.setMediaSize(PrintAttributes.MediaSize.ISO_A4)
//                    .setResolution(new PrintAttributes.Resolution("1", Context.PRINT_SERVICE, 300, 300))
//                    .setColorMode(PrintAttributes.COLOR_MODE_COLOR).
//                    setMinMargins(PrintAttributes.Margins.NO_MARGINS).build();
//        }
//    }
    public void createWebPagePrint(WebView webView) {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter printAdapter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            printAdapter = webView.createPrintDocumentAdapter();
            String jobName = getString(R.string.app_name) + " Document";
            PrintAttributes.Builder builder = null;
            builder = new PrintAttributes.Builder();
            builder.setMediaSize(PrintAttributes.MediaSize.ISO_A4);
            PrintJob printJob = null;
            printJob = printManager.print(jobName, printAdapter, builder.build());
            if (printJob.isCompleted()) {
                Toast.makeText(getApplicationContext(), "Print Complete", Toast.LENGTH_LONG).show();
            } else if (printJob.isFailed()) {
                Toast.makeText(getApplicationContext(), "Print Failed", Toast.LENGTH_LONG).show();
            }
            builder.setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setResolution(new PrintAttributes.Resolution("1", Context.PRINT_SERVICE, 300, 300))
                    .setColorMode(PrintAttributes.COLOR_MODE_COLOR).
                    setMinMargins(PrintAttributes.Margins.NO_MARGINS).build();
        }
    }

    /*
        public void printPDF(PDFView PrintRL) {
            int countPages=PrintRL.getPageCount();
            PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);
            printManager.print("print_any_view_job_name", new ViewPrintAdapter(this,
                    findViewById(R.id.pdfView),countPages), null);
        }
    */
    public void printPDF(PDFView view) {
        int countPages = view.getPageCount();
        PrintManager printManager = (PrintManager) this
                .getSystemService(Context.PRINT_SERVICE);

        String jobName = this.getString(R.string.app_name) +
                " Document";

        printManager.print(jobName, new PrintDocumentAdapterN(this,
                        findViewById(R.id.pdfView), countPages, view),
                null);
    }

    /**
     * Calculates the Letter Size Paper's Height depending on the LetterSize Dimensions and Given width.
     *
     * @param width
     * @return
     */
    private int getLetterSizeHeight(int width) {
        return (int) ((float) (11 * width) / 8.5);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.printLL:
                showPrintView(v);
                break;
        }

    }


    private class PdfWebViewClient extends WebViewClient {
        private static final String TAG = "PdfWebViewClient";
        private static final String PDF_EXTENSION = ".pdf";
        private static final String PDF_VIEWER_URL = "http://docs.google.com/gview?embedded=true&url=";

        private Context mContext;
        private WebView mWebView;
        private ProgressDialog mProgressDialog;
        private boolean isLoadingPdfUrl;

        public PdfWebViewClient(Context context, WebView webView) {
            mContext = context;
            mWebView = webView;
            mWebView.setWebViewClient(this);
        }

        public void loadPdfUrl(String url) {
            mWebView.stopLoading();

            if (!TextUtils.isEmpty(url)) {
                isLoadingPdfUrl = isPdfUrl(url);
                if (isLoadingPdfUrl) {
                    mWebView.clearHistory();
                }

                showProgressDialog();
            }

            mWebView.loadUrl(url);
        }

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            return shouldOverrideUrlLoading(url);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl) {
            handleError(errorCode, description.toString(), failingUrl);
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest request) {
            final Uri uri = request.getUrl();
            return shouldOverrideUrlLoading(webView, uri.toString());
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public void onReceivedError(final WebView webView, final WebResourceRequest request, final WebResourceError error) {
            final Uri uri = request.getUrl();
            handleError(error.getErrorCode(), error.getDescription().toString(), uri.toString());
        }

        @Override
        public void onPageFinished(final WebView view, final String url) {
            dismissProgressDialog();
        }

        private boolean shouldOverrideUrlLoading(final String url) {

            if (!isLoadingPdfUrl && isPdfUrl(url)) {
                mWebView.stopLoading();

                final String pdfUrl = PDF_VIEWER_URL + url;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadPdfUrl(pdfUrl);
                    }
                }, 300);

                return true;
            }

            return false; // Load url in the webView itself
        }

        private void handleError(final int errorCode, final String description, final String failingUrl) {
            Log.e(TAG, "Error : " + errorCode + ", " + description + " URL : " + failingUrl);
        }

        private void showProgressDialog() {
            dismissProgressDialog();
            mProgressDialog = ProgressDialog.show(mContext, "", "Loading...");
        }

        private void dismissProgressDialog() {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }

        private boolean isPdfUrl(String url) {
            if (!TextUtils.isEmpty(url)) {
                url = url.trim();
                int lastIndex = url.toLowerCase().lastIndexOf(PDF_EXTENSION);
                if (lastIndex != -1) {
                    return url.substring(lastIndex).equalsIgnoreCase(PDF_EXTENSION);
                }
            }
            return false;
        }
    }


    private class DownloadAsycTask extends AsyncTask<String, Void, byte[]> {


        @Override
        protected byte[] doInBackground(String... params) {
            byte[] bytes = null;
            InputStream inputStream = null;
            String pdfUrl = params[0];
            try {
                URL url = new URL(pdfUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                int responseCode = httpURLConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
                    inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                    bytes = IOUtils.toByteArray(inputStream);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bytes;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);

            if (bytes != null) {
                pdfView.fromBytes(bytes)
                        .enableDoubletap(true)
                        .onError(new OnErrorListener() {
                            @Override
                            public void onError(Throwable t) {
                                try {
                                    if (t.getClass().equals(PdfPasswordException.class)) {
//                                        showToast("Password protected pdfs can't be opened");
                                    } else {
//                                        showToast("Error loading pdf document");
                                    }
//                                    dismissProgress();
                                } catch (Exception e) {
                                    e.printStackTrace();
//                                    dismissProgress();
//                                    showToast("Error loading pdf document");
                                }
                            }
                        })
                        .onLoad(new OnLoadCompleteListener() {
                            @Override
                            public void loadComplete(int nbPages) {
//                                dismissProgress();
                            }
                        })
                        .load();

            }
        }

    }
}
