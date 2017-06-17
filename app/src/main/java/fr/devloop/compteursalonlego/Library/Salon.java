package fr.devloop.compteursalonlego.Library;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.EditText;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.socketio.client.SocketIOException;

import java.net.URISyntaxException;
import java.util.ArrayList;

import fr.devloop.compteursalonlego.R;

/**
 * Created by jeromedemonchaux on 14/06/2017.
 */

public class Salon {

    public static Socket socket;
    public static Salon instance;
    private static SharedPreferences prefs;
    private static Context context;

    private static final String SERVER_PROTOCOL = "http://";
    private static final String SERVER_PORT_SEPARATOR = ":";
    public static String SERVER_PORT = "";
    public static String SERVER_IP = "";

    private static String SERVER = SERVER_PROTOCOL + SERVER_IP + SERVER_PORT_SEPARATOR + SERVER_PORT;

    public static int MAX_VISITOR = 1600;
    public static final String API_GET_VISITOR = "countVisiteur";
    public static final String API_ADD_VISITOR = "addVisiteur";
    public static final String API_REMOVE_VISITOR = "removeVisiteur";
    public static int current_visitor_number;

    public Salon(Context ctx) {
        prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        context = ctx;
        readConfig();
    }

    public Socket initSocket() {
        try {
            if (socket == null) {
                socket = IO.socket(SERVER);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return socket;
    }


    public void readConfig() {
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
        socket.close();
        socket = null;

    }



}
