package com.nutriapp.upeu.viewer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nutriapp.upeu.R;
import com.nutriapp.upeu.asyntask.ServiceDownloandPDF;

public class WebViewPdf extends AppCompatActivity {
    private WebView webViewPdf;
    //private ProgressBar progressBarPdf;
    private static String URLPDF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        URLPDF = b.getString("url");
        //URLPDF="http://www.agirregabiria.net/g/sylvainaitor/principito.pdf";
        setContentView(R.layout.activity_web_view);
        webViewPdf = (WebView)findViewById(R.id.webViewPdfRead);
        //progressBarPdf = (ProgressBar)findViewById(R.id.progressBarPdf);


        WebSettings webSettings = webViewPdf.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webViewPdf.setWebViewClient(new MyCustomWebPdf());
        webViewPdf.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webViewPdf.loadUrl(URLPDF);




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Descargando...",Toast.LENGTH_LONG).show();
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                startService(new Intent(getApplicationContext(),ServiceDownloandPDF.class).putExtra("URL",URLPDF));
            }
        });


    }

    private class MyCustomWebPdf extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            super.onPageFinished(view, url);
        }
    }

}
