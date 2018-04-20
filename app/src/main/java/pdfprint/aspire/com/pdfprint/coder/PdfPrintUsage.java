package pdfprint.aspire.com.pdfprint.coder;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

import pdfprint.aspire.com.pdfprint.PdfPrint;
import pdfprint.aspire.com.pdfprint.R;

public class PdfPrintUsage extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "[MainActivityN]";

    WebView webViewSoftwareUpdate;
    LinearLayout printLL;
    PDFView pdfView;
    RelativeLayout PrintRL;
    private WebView wv;

//    String PDF_URL = "https://www.antennahouse.com/XSLsample/pdf/sample-link_1.pdf";
//    String PDF_URL = "http://gahp.net/wp-content/uploads/2017/09/sample.pdf";
    String PDF_URL = "https://www.tinaja.com/glib/pdflink.pdf";
//    String PDF_URL = "https://www.adobe.com/content/dam/acom/en/devnet/acrobat/pdfs/pdf_open_parameters.pdf";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pdf_print_usage);

        pdfView = (PDFView) findViewById(R.id.pdfView);
        PrintRL = (RelativeLayout) findViewById(R.id.PrintRL);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView titleTV = (TextView) toolbar.findViewById(R.id.titleTV);
        printLL = (LinearLayout) toolbar.findViewById(R.id.printLL);

        printLL.setOnClickListener(this);
        setSupportActionBar(toolbar);
        titleTV.setText("Test");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        webViewSoftwareUpdate = (WebView) findViewById(R.id.webViewSoftwareUpdate);

        init(PDF_URL, webViewSoftwareUpdate);

    }

    private void init(String viewUrl, WebView webview) {

        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);


        //Developer N
        PdfWebViewClientN pdfWebViewClient = new PdfWebViewClientN(this, webview);
        pdfWebViewClient.loadPdfUrl(viewUrl);

        webview.loadData(viewUrl, "text/html/", "UTF-8");
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void createWebPrintJob(WebView webView) {
        String jobName = getString(R.string.app_name) + " Document";
        PrintAttributes attributes = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setResolution(new PrintAttributes.Resolution("pdf", "pdf", 600, 600))
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS).build();
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/PDFTest/");

        PdfPrint pdfPrint = new PdfPrint(attributes);
        pdfPrint.print(webView.createPrintDocumentAdapter(jobName),
                path, "output_" + System.currentTimeMillis() + ".pdf");
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.printLL:
                Log.e(TAG, "onClick() method is called");
//                showPrintView(v);
                createWebPrintJob(webViewSoftwareUpdate);
                break;
        }

    }
}
