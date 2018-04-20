package pdfprint.aspire.com.pdfprint.coder;

import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;

/**
 * Created by Shailesh on 4/6/2018.
 */

public class PrintingDocumentAdapter extends PrintDocumentAdapter
{

    @Override
    public void onLayout(PrintAttributes printAttributes,
                         PrintAttributes printAttributes1,
                         CancellationSignal cancellationSignal,
                         LayoutResultCallback layoutResultCallback,
                         Bundle bundle) {

    }

    @Override
    public void onWrite(PageRange[] pageRanges, ParcelFileDescriptor parcelFileDescriptor, CancellationSignal cancellationSignal, WriteResultCallback writeResultCallback) {

    }
}
