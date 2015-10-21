package com.nutriapp.upeu.managers;


import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.nutriapp.upeu.DrawerMenu;
import com.nutriapp.upeu.MainPushRegistration;
import com.nutriapp.upeu.R;
import com.nutriapp.upeu.viewer.WebViewPdf;

/**
 * Created by Kelvin on 6/09/2015.
 */
public class GCMIntentService extends IntentService {
    private static final int NOTIF_ALERTA_ID = 1;
    public GCMIntentService() {
        super("GCMIntentService");
    }
    @Override
    protected void onHandleIntent(Intent intent)


    {
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);
        Bundle extras = intent.getExtras();

        if (!extras.isEmpty())
        {
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType))
            {
                mostrarNotification(extras.getString("msg"));
            }
        }

        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void mostrarNotification(String msg)
    {
        String [] datos = msg.split("\\{");

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.background_home)
                        .setContentTitle("Notificacion :D")
                        .setContentText(datos[0]);

        Intent notIntent =  new Intent(this, WebViewPdf.class);
        notIntent.putExtra("url",datos[1]);
        Log.d("URL",datos[1]);
        PendingIntent contIntent = PendingIntent.getActivity(
                this, 0, notIntent, 0);
        mBuilder.setContentIntent(contIntent);
        mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        mNotificationManager.notify(NOTIF_ALERTA_ID, mBuilder.build());
    }
}