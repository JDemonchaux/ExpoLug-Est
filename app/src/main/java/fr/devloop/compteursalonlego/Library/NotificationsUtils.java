package fr.devloop.compteursalonlego.Library;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import fr.devloop.compteursalonlego.InActivity;
import fr.devloop.compteursalonlego.MainActivity;
import fr.devloop.compteursalonlego.R;

/**
 * Created by jeromedemonchaux on 24/06/2017.
 */

public class NotificationsUtils {


    public static void notifySalonAlmostFull(Context ctx, Integer visitorNumber, Class parentActivity) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx);
        mBuilder.setSmallIcon(R.drawable.ico_settings);
        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        mBuilder.setContentTitle("Salon Lego");
        mBuilder.setContentText("Attention, le salon est presque plein! " + visitorNumber.toString() + " visiteurs!");


        Intent resultIntent = new Intent(ctx, parentActivity);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 113, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.setFullScreenIntent(pendingIntent, true);

        NotificationManager mNotificationManager =
                (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(Salon.ID_NOTIF_VISITOR_FULL, mBuilder.build());
    }
}
