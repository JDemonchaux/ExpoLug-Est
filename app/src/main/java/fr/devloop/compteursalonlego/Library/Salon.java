package fr.devloop.compteursalonlego.Library;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.NetworkOnMainThreadException;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.View;
import android.widget.EditText;


import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import fr.devloop.compteursalonlego.Library.Event.SalonAlmostFullEvent;
import fr.devloop.compteursalonlego.Library.Event.SalonFullEvent;
import fr.devloop.compteursalonlego.Library.Event.SocketConnectedEvent;
import fr.devloop.compteursalonlego.Library.Event.SocketConnectionErrorEvent;
import fr.devloop.compteursalonlego.Library.Event.SocketGetVisitorEvent;
import fr.devloop.compteursalonlego.MainActivity;
import fr.devloop.compteursalonlego.OutActivity;
import fr.devloop.compteursalonlego.R;
import fr.devloop.compteursalonlego.SettingsActivity;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by jeromedemonchaux on 14/06/2017.
 */

public class Salon {

    public static Socket socket;
    private static Salon instance;
    private static SharedPreferences prefs;
    private Context context;

    public static Boolean serverDiscovered = false;
    public static final String SERVER_PROTOCOL = "http://";
    public static final String SERVER_PORT_SEPARATOR = ":";
    public static final String SERVER_PORT = "7360";
    public static String SERVER_IP = "";

    public static String SERVER = SERVER_PROTOCOL + SERVER_IP + SERVER_PORT_SEPARATOR + SERVER_PORT;

    public static int MAX_VISITOR = 1600;

    public static final String API_GET_VISITOR = "countVisitor";
    public static final String API_ADD_VISITOR = "addVisitor";
    public static final String API_REMOVE_VISITOR = "removeVisitor";
    public static final String API_NOTIFY_VISITOR_FULL = "fullVisitor";
    public static final String API_NOTIFY_VISITOR_ALMOST_FULL = "almostFullVisitor";
    public static final String API_MAX_VISITOR = "maxVisitor";
    public static Integer CURRENT_VISITOR = 1;
    public static final Integer ID_NOTIF_VISITOR_FULL = 1;

    public static final String STATUS_CONNECTED = "connected";
    public static final String STATUS_ERROR = "error";
    public static String CURRENT_STATUS = "";

    public static Integer current_visitor = 0;


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
            socket = IO.socket(SERVER);
            socket.connect();
            listenForNotifications();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return socket;
    }


    private void readConfig() {
        SERVER_IP = prefs.getString(context.getString(R.string.pref_server_ip), "");
        MAX_VISITOR = Integer.parseInt(prefs.getString(context.getString(R.string.pref_max_visitor), "1"));
        SERVER = SERVER_PROTOCOL + SERVER_IP + SERVER_PORT_SEPARATOR + SERVER_PORT;
    }

    public void saveConfig(EditText ip) {
        saveConfig(ip.getText().toString());
    }

    public void saveConfig(String ip) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.pref_server_ip), ip);
        editor.apply();
    }

    public void close() {
        current_visitor = 1;
        if (socket != null) {
            if (socket.connected()) socket.disconnect();
            socket.close();
            socket = null;
            instance = null;
        }
    }

    public static boolean isServerReachable(String ip) {
        boolean reachable = false;
        try {
            String url = SERVER_PROTOCOL + ip + SERVER_PORT_SEPARATOR + SERVER_PORT;
            Socket s = IO.socket(url);
            s.connect();
            Thread.sleep(1000);
            reachable = s.connected();
            s.close();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return reachable;

    }

    public static void testConnection(String ip) {
        boolean connected = false;
        try {
            String url = SERVER_PROTOCOL + ip + SERVER_PORT_SEPARATOR + SERVER_PORT;
            Socket s = IO.socket(url);
            s.connect();
            Thread.sleep(1000);
            connected = s.connected();
            s.close();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (connected) {
            CURRENT_STATUS = STATUS_CONNECTED;
        } else {
            CURRENT_STATUS = STATUS_ERROR;
        }
    }

    /**
     * Listen for specific socketio event sent by server
     * And then notify in the app
     */
    private void listenForNotifications() {
        socket.on(Salon.API_GET_VISITOR, new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                Log.d("SOCKETIO", "GET VISITOR");
                CURRENT_VISITOR = Integer.parseInt(args[0].toString());
                EventBus.getDefault().post(new SocketGetVisitorEvent());
            }
        });
        socket.on(API_MAX_VISITOR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("SOCKETIO", "MAX VISITOR");
                MAX_VISITOR = (Integer) args[0];
            }
        });

        socket.on(API_GET_VISITOR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                current_visitor = (Integer) args[0];
            }
        });

        socket.on(API_NOTIFY_VISITOR_ALMOST_FULL, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                EventBus.getDefault().post(new SalonAlmostFullEvent(current_visitor));
            }
        });

        socket.on(API_NOTIFY_VISITOR_FULL, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                EventBus.getDefault().post(new SalonFullEvent(current_visitor));
            }
        });

        socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("SOCKETIO", "CONNECTION ERROR");
                CURRENT_STATUS = STATUS_ERROR;
                EventBus.getDefault().post(new SocketConnectionErrorEvent());
            }
        });

        socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("SOCKETIO", "DISCONNECT");
                CURRENT_STATUS = STATUS_ERROR;
                EventBus.getDefault().post(new SocketConnectionErrorEvent());
            }
        });

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("SOCKETIO", "CONNECTION SUCCESS");
                CURRENT_STATUS = STATUS_CONNECTED;
                EventBus.getDefault().post(new SocketConnectedEvent());
            }
        });

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
