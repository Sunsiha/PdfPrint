package pdfprint.aspire.com.pdfprint.coder;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintJob;
import android.print.PrintManager;
import android.provider.DocumentsContract;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
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
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.shockwave.pdfium.PdfPasswordException;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import pdfprint.aspire.com.pdfprint.MainActivity;
import pdfprint.aspire.com.pdfprint.R;

public class MainActivityN extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "[MainActivityN]";
    WebView webViewSoftwareUpdate;
    LinearLayout printLL;
    PDFView pdfView;
    RelativeLayout PrintRL;
//    String PDF_URL = "https://www.antennahouse.com/XSLsample/pdf/sample-link_1.pdf";
//    String PDF_URL = "http://gahp.net/wp-content/uploads/2017/09/sample.pdf";
    String PDF_URL = "https://www.aspiresoft.co.ke/ERP/POSitiveBOTest/Inventory/InventoryReports.aspx?1=RptStockTake!195";
//    String PDF_URL = "https://www.adobe.com/content/dam/acom/en/devnet/acrobat/pdfs/pdf_open_parameters.pdf";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_my);

        pdfView = (PDFView) findViewById(R.id.pdfView);
        PrintRL = (RelativeLayout) findViewById(R.id.PrintRL);

        new DownloadAsycTask().execute(PDF_URL);

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

        /*getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);*/

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

        init(PDF_URL, webViewSoftwareUpdate);

    }

    private void init(String viewUrl, WebView webview) {

        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        //Original
        /*PdfWebViewClient pdfWebViewClient = new PdfWebViewClient(this, webview);
        pdfWebViewClient.loadPdfUrl(viewUrl);*/

        //Developer N
        PdfWebViewClientN pdfWebViewClient = new PdfWebViewClientN(this, webview);
        pdfWebViewClient.loadPdfUrl(viewUrl);
    }

    private void loadPDF(WebView webView){
        String doc="<iframe src='http://docs.google.com/viewer?url=" + PDF_URL + "&embedded=true' width='100%' height='100%'  style='border: none;'></iframe>";
        webView.setVisibility(WebView.VISIBLE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.setWebViewClient(new Callback());
        webView.loadData(doc, "text/html", "UTF-8");

        init(PDF_URL, webView);
    }

    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(
                WebView view, String url) {
            return(false);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @SuppressLint("RestrictedApi")
    private void showPrintView(View v) {
        PopupMenu popup = new PopupMenu(MainActivityN.this, v);
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
        MenuPopupHelper menuHelper = new MenuPopupHelper(MainActivityN.this, (MenuBuilder) popup.getMenu(), v);
        menuHelper.setForceShowIcon(true);
        menuHelper.setGravity(Gravity.END);
        menuHelper.show();
    }

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

    public void printPDF(PDFView view) {
        int countPages = view.getPageCount();

        view.stopFling();

        PrintManager printManager = (PrintManager) this
                .getSystemService(Context.PRINT_SERVICE);

        String jobName = this.getString(R.string.app_name) +
                " Document";

        printManager.print(jobName, new PrintDocumentAdapterN(this,
                        view, countPages, view),
                new PrintAttributes.Builder().build());

//        myPrintPDF(view, countPages);


    }

    private void createWebPrintJob(WebView webView) {

        // Get a PrintManager instance
        PrintManager printManager = (PrintManager) this
                .getSystemService(Context.PRINT_SERVICE);


    }

    public void myPrintPDF(View view, int countPages) {
        PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);
        printManager.print("print_any_view_job_name",
                new ViewPrintAdapterN(this,
                findViewById(R.id.pdfView), countPages), new PrintAttributes.Builder().build());
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
                Log.e(TAG, "onClick() method is called");
                showPrintView(v);
                break;
        }

    }

    private class DownloadAsycTask extends AsyncTask<String, Void, byte[]> implements OnPageChangeListener, OnLoadCompleteListener {


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
                        .spacing(50)
                        .enableSwipe(true)
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
                                    pdfView.cancelLongPress();
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
                                pdfView.cancelLongPress();
                            }
                        })
                        .load();
                pdfView.loadPages();

            }
        }

        @Override
        public void onPageChanged(int page, int pageCount) {

        }

        @Override
        public void loadComplete(int nbPages) {

        }
    }
}
