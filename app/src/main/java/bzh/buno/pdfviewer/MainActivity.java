package bzh.buno.pdfviewer;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import bzh.buno.pdfviewer.component.WebviewPdfView;
import bzh.buno.pdfviewer.utils.FileUtils;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.pdf_view)
    WebviewPdfView mPdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            AssetManager am = getAssets();
            InputStream inputStream = am.open("raw/test.pdf");
            File file = createFileFromInputStream(inputStream);
            if (file != null) {
                mPdfView.openPdf(file);
            } else {
                Toast.makeText(this, "No PDF found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private File createFileFromInputStream(InputStream inputStream) {

        try {
            File file = new File(FileUtils.getRandomFileCachePath(this));
            OutputStream outputStream = new FileOutputStream(file);
            byte buffer[] = new byte[1024];
            int length = 0;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
