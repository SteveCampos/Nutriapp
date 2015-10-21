package com.nutriapp.upeu.asyntask;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Toast;

/**
 * Created by Kelvin on 12/09/2015.
 */
public class ServiceDownloandPDF extends Service {

    private DownloadManager.Request request;
    private String URL = "";

    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        URL = (String) intent.getExtras().get("URL");
        Log.d("URL2", URL);
        DownloadManager.Request request ;
        request = new DownloadManager.Request(Uri.parse(URL));
        request.setTitle("DOWNLOADING PDF");
        request.setDescription("THANKS FOR YOUR DOWNLOAD...");
        //This code is for if your download only wifi or packet date of your device android.
        //request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        String nameOfFile = URLUtil.guessFileName(URL, null, MimeTypeMap.getFileExtensionFromUrl(URL));
        Log.d("DESCARGA", nameOfFile);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, nameOfFile);

        DownloadManager manager = (DownloadManager)getBaseContext().getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
        Toast.makeText(getApplicationContext(),"Descarga Terminada",Toast.LENGTH_SHORT).show();
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
