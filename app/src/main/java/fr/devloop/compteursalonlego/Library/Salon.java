package fr.devloop.compteursalonlego.Library;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.socketio.client.SocketIOException;

import java.net.URISyntaxException;

/**
 * Created by jeromedemonchaux on 14/06/2017.
 */

public class Salon {

    public static Socket socket;

    private static final String SERVER_PROTOCOL = "http://";
    private static final String SERVER_PORT = ":3000";
    private static String SERVER_IP = "192.168.1.106";

    private static String SERVER = SERVER_PROTOCOL + SERVER_IP + SERVER_PORT;

    public static final int MAX_VISITOR = 1600;
    public static final String API_GET_VISITOR = "countVisiteur";
    public static final String API_ADD_VISITOR = "addVisiteur";
    public static final String API_REMOVE_VISITOR = "removeVisiteur";

    public Salon() {

    }

    public static Socket initSocket() {
        try {
            socket = IO.socket(SERVER);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return socket;
    }







}
