package com.nutriapp.upeu.viewer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.joanzapata.pdfview.PDFView;

import com.nutriapp.upeu.R;

public class ViewPdfLocal extends AppCompatActivity {
    private PDFView pdfView;
    public static final String SAMPLE_FILE = "principito.pdf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf_local);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pdfView = (PDFView)findViewById(R.id.pdfview);
        pdfView.fromAsset(SAMPLE_FILE)
                .pages(0, 2, 1, 3, 3, 3)
                .defaultPage(1)
                .showMinimap(false)
                .enableSwipe(true)
                //.onDraw(onDrawListener)
                //.onLoad(onLoadCompleteListener)
                //.onPageChange(onPageChangeListener)
                .load();

    }

}
