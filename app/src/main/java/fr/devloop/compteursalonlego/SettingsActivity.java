package fr.devloop.compteursalonlego;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import fr.devloop.compteursalonlego.Library.Salon;

public class SettingsActivity extends AppCompatActivity {

    Toolbar toolBar;
    Salon salon;
    Socket socket;

    ConstraintLayout layout;
    EditText inputServerIp;
    EditText inputServerPort;
    EditText inputMaxVisitor;
    LinearLayout connectionConnecting;
    LinearLayout connectionSuccess;
    LinearLayout connectionError;


    Button btValidate;

    Activity activity;

    String serverIp;
    String serverPort;
    String maxVisitor;
    boolean serverConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_settings);
        layout = (ConstraintLayout) findViewById(R.id.layout_settings);

        //initialize toolbar as appbar
        toolBar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(toolBar);

        //set back button
        ActionBar ab = getSupportActionBar();
        if (ab != null) ab.setDisplayHomeAsUpEnabled(true);

        activity = this;
        salon = new Salon(this);
        socket = salon.initSocket();
        if (!socket.connected()) socket.connect();

        connectionConnecting = (LinearLayout) findViewById(R.id.layout_connection_connecting);
        connectionSuccess = (LinearLayout) findViewById(R.id.layout_connection_success);
        connectionError = (LinearLayout) findViewById(R.id.layout_connection_error);
        inputServerIp = (EditText) findViewById(R.id.input_server_ip);
        inputServerIp.setText(Salon.SERVER_IP);
        inputServerPort = (EditText) findViewById(R.id.input_server_port);
        inputServerPort.setText(Salon.SERVER_PORT);
        inputMaxVisitor = (EditText) findViewById(R.id.input_max_visitor);
        inputMaxVisitor.setText(String.valueOf(Salon.MAX_VISITOR));
        btValidate = (Button) findViewById(R.id.button_validate_settings);
        btValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socket.close();
                salon.saveConfig(inputServerIp, inputServerPort, inputMaxVisitor);
                Intent i = new Intent(activity, MainActivity.class);
                startActivity(i);
            }
        });



        socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("SOCKETIO", "CONNECTION ERROR");
                serverConnected = false;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connectionConnecting.setVisibility(View.GONE);
                        connectionSuccess.setVisibility(View.GONE);
                        connectionError.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("SOCKETIO", "CONNECTION SUCCESS");
                serverConnected = true;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connectionConnecting.setVisibility(View.GONE);
                        connectionSuccess.setVisibility(View.VISIBLE);
                        connectionError.setVisibility(View.GONE);
                    }
                });
            }
        });

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(activity);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Construit le menu
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        socket.close();
        super.onBackPressed();
    }

    // @TODO: déplacer dans un fichier Utils.
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}
