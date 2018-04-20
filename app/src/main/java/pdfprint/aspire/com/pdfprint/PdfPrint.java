package pdfprint.aspire.com.pdfprint;

import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.util.Log;

import java.io.File;

/**
 * Created by Shailesh on 4/6/2018.
 */

public class PdfPrint  extends PrintDocumentAdapter{

    private static final String TAG = PdfPrint.class.getSimpleName();
    private final PrintAttributes printAttributes;
    /*PageRange[] pageRanges;
    ParcelFileDescriptor destination;
    CancellationSignal cancellationSignal;
    PrintDocumentAdapter.WriteResultCallback callback;*/

    PrintAttributes printAttributes1;
    CancellationSignal cancellationSignal;
    LayoutResultCallback layoutResultCallback;
    Bundle bundle;

    public PdfPrint(PrintAttributes printAttributes) {
        this.printAttributes = printAttributes;
    }

    public void print(final PrintDocumentAdapter printAdapter, final File path, final String fileName) {
        printAdapter.onLayout(printAttributes,
                printAttributes1,
                cancellationSignal,
                layoutResultCallback,
                bundle);
    }

    private ParcelFileDescriptor getOutputFile(File path, String fileName) {
        if (!path.exists()) {
            path.mkdirs();
        }
        File file = new File(path, fileName);
        try {
            file.createNewFile();
            return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE);
        } catch (Exception e) {
            Log.e(TAG, "Failed to open ParcelFileDescriptor", e);
        }
        return null;
    }

    @Override
    public void onLayout(PrintAttributes printAttributes,
                         PrintAttributes printAttributes1,
                         CancellationSignal cancellationSignal,
                         LayoutResultCallback layoutResultCallback,
                         Bundle bundle) {

    }

    @Override
    public void onWrite(PageRange[] pageRanges,
                        ParcelFileDescriptor parcelFileDescriptor,
                        CancellationSignal cancellationSignal,
                        WriteResultCallback writeResultCallback) {

    }
}
