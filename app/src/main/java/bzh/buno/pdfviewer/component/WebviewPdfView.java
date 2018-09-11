package bzh.buno.pdfviewer.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import bzh.buno.pdfviewer.utils.RandomUtils;

/**
 * Definition of the WebviewPdfView object.
 */
public class WebviewPdfView extends WebView {
    private static final String TAG = WebviewPdfView.class.getSimpleName();
    private static final String IMAGE_TAG = "<div class=\"page wrapper loading\"><img class=\"b-lazy\" src=data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw== data-src=\"{IMAGE_URL}\"/></div>";

    private Context mContext;
    private PdfRenderer mRenderer;

    private int mPdfId = RandomUtils.randomInt();

    public WebviewPdfView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public WebviewPdfView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WebviewPdfView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;

        // init webview
        clearCache(true);
        getSettings().setJavaScriptEnabled(true);
        getSettings().setBuiltInZoomControls(true);
        getSettings().setDisplayZoomControls(false);
        setWebViewClient(new PDFWebViewClient());
    }

    public void openPdf(String path) throws IOException {
        openPdf(new File(path));
    }

    public void openPdf(File file) throws IOException {
        mRenderer = new PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY));

        prepareHTML();
    }

    /**
     * Will inject image element into the webview
     */
    private void prepareHTML() {

        String html = getHtmlFromAssets();
        StringBuilder pagesHtml = new StringBuilder();
        Log.d(TAG, "prepareHTML()");

        int pageCount = mRenderer.getPageCount();
        for (int i = 0; i < pageCount; i++) {
            String imgTag = IMAGE_TAG;
            imgTag = imgTag.replace("{IMAGE_URL}", "page_" + mPdfId + "_" + i + ".png");
            pagesHtml.append(imgTag);
        }

        html = html.replace("{CONTENT}", pagesHtml.toString());

        loadDataWithBaseURL("file:///android_asset/pdf/index.html", html, MimeTypeMap.getSingleton().getMimeTypeFromExtension("html"), "UTF-8", "file:///android_asset/pdf/index.html");
    }

    private String getHtmlFromAssets() {
        StringBuilder buf = new StringBuilder();
        try {
            InputStream json = mContext.getAssets().open("pdf/pdf.html");

            BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;

            while ((str = in.readLine()) != null) {
                buf.append(str);
            }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buf.toString();
    }

    private class PDFWebViewClient extends WebViewClient {
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            Log.d("PDF", "shouldInterceptRequest(" + request.getUrl() + ")");
            String requestedFile = request.getUrl().getLastPathSegment();

            // We use a random number to avoid collision in pdf names
            if (requestedFile != null && requestedFile.startsWith("page_" + mPdfId)) {
                int pageNumber = Integer.parseInt(requestedFile.substring(10, 11));

                synchronized (mRenderer) {
                    final PdfRenderer.Page page = mRenderer.openPage(pageNumber);
                    final Bitmap bitmap = Bitmap.createBitmap(page.getWidth() * 2, page.getHeight() * 2, Bitmap.Config.ARGB_8888);
                    bitmap.eraseColor(Color.WHITE);
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                    page.close();

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();

                    return new WebResourceResponse(MimeTypeMap.getSingleton().getMimeTypeFromExtension("png"), "UTF-8", new ByteArrayInputStream(byteArray));
                }

            }
            return super.shouldInterceptRequest(view, request);
        }
    }
}
