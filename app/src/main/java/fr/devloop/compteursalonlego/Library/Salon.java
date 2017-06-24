package fr.devloop.compteursalonlego.Library;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.EditText;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.socketio.client.SocketIOException;

import org.greenrobot.eventbus.EventBus;

import java.net.URISyntaxException;
import java.util.ArrayList;

import fr.devloop.compteursalonlego.Library.Event.SalonAlmostFullEvent;
import fr.devloop.compteursalonlego.MainActivity;
import fr.devloop.compteursalonlego.R;

/**
 * Created by jeromedemonchaux on 14/06/2017.
 */

public class Salon {

    public static Socket socket;
    private static Salon instance;
    private static SharedPreferences prefs;
    private Context context;

    private static final String SERVER_PROTOCOL = "http://";
    private static final String SERVER_PORT_SEPARATOR = ":";
    public static String SERVER_PORT = "";
    public static String SERVER_IP = "";

    private static String SERVER = SERVER_PROTOCOL + SERVER_IP + SERVER_PORT_SEPARATOR + SERVER_PORT;

    public static int MAX_VISITOR = 1600; //TODO: get from server
    public static int current_visitor_number;

    public static final String API_GET_VISITOR = "countVisiteur";
    public static final String API_ADD_VISITOR = "addVisiteur";
    public static final String API_REMOVE_VISITOR = "removeVisiteur";
    public static final String API_NOTIFY_VISITOR_FULL = "fullVisiteur";

    public static final Integer ID_NOTIF_VISITOR_FULL = 1;


    private Salon(Context ctx) {
        instance = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        context = ctx;
        readConfig();
        initSocket();
    }

    public static Salon getInstance(Context ctx) {
        if (instance == null) {
            instance = new Salon(ctx);
        }
        return instance;
    }

    private Socket initSocket() {
        try {
            if (socket == null) {
                socket = IO.socket(SERVER);
                socket.connect();
                listenForNotifications();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return socket;
    }


    private void readConfig() {
        SERVER_IP = prefs.getString(context.getString(R.string.pref_server_ip), "");
        SERVER_PORT = prefs.getString(context.getString(R.string.pref_server_port), "");
        MAX_VISITOR = Integer.parseInt(prefs.getString(context.getString(R.string.pref_max_visitor), "0"));
        SERVER = SERVER_PROTOCOL + SERVER_IP + SERVER_PORT_SEPARATOR + SERVER_PORT;
    }

    public void saveConfig(EditText ip, EditText port, EditText visitor) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.pref_server_ip), ip.getText().toString());
        editor.putString(context.getString(R.string.pref_server_port), port.getText().toString());
        editor.putString(context.getString(R.string.pref_max_visitor), visitor.getText().toString());
        editor.apply();
    }

    public void close() {
        current_visitor_number = -1;
        socket.disconnect();
        socket.close();
        socket = null;
    }

    /**
     * Listen for specific socketio event sent by server
     * And then notify in the app
     */
    private void listenForNotifications() {
        socket.on(API_NOTIFY_VISITOR_FULL, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                SalonAlmostFullEvent event = new SalonAlmostFullEvent();
                event.visitorNumber = (Integer) args[0];
                EventBus.getDefault().post(event);
            }
        });
    }


}
