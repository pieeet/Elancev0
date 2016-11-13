package com.rocdev.android.elancev0.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rocdev.android.elancev0.R;
import com.rocdev.android.elancev0.activities.AfspraakDetailActivity;
import com.rocdev.android.elancev0.activities.AfsprakenActivity;
import com.rocdev.android.elancev0.activities.MainActivity;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.rocdev.android.elancev0.interfaces.Constants.KEY_AFSPRAAK_ID;


/**
 * Created by piet on 07-11-16.
 *
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        sendNotification(remoteMessage);
    }



    private void sendNotification(RemoteMessage remoteMessage) {
        Map<String, String> dataMap = remoteMessage.getData();
        String afspraakId = dataMap.get("afspraakId");
        String locatie = dataMap.get("locatie");
        String tijdStr = dataMap.get("tijd");
        Long tijdMs = Long.parseLong(tijdStr);
        Date date = new Date(tijdMs);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm");
        String tijdFormat = sdf.format(date);
        // Creates an explicit intent for an Activity in your app
        Intent intent = new Intent(this, AfsprakenActivity.class);
        intent.putExtra(KEY_AFSPRAAK_ID, afspraakId);
//        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(AfsprakenActivity.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_elance_logo_transparant)
                .setColor(getResources().getColor(R.color.colorPrimaryDark))
                .setContentTitle("Nieuwe afspraak")
                .setContentText("Er is een nieuwe afsraak gemaakt")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        // Sets a title for the Inbox in expanded layout
        inboxStyle.setBigContentTitle("Nieuwe afspraak");
        // Moves events into the expanded layout
        inboxStyle.addLine("Je hebt een nieuwe afspraak");
        inboxStyle.addLine("locatie: " + locatie);
        inboxStyle.addLine("tijd: " + tijdFormat);
        notificationBuilder.setStyle(inboxStyle);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }




}
