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



import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import fr.devloop.compteursalonlego.Library.Event.SocketConnectedEvent;
import fr.devloop.compteursalonlego.Library.Event.SocketConnectionErrorEvent;
import fr.devloop.compteursalonlego.Library.Salon;
import io.socket.client.Socket;

public class SettingsActivity extends AppCompatActivity {

    Toolbar toolBar;
    Salon salon;
    Socket socket;

    ConstraintLayout layout;
    EditText inputServerIp;
    EditText inputMaxVisitor;
    LinearLayout connectionConnecting;
    LinearLayout connectionSuccess;
    LinearLayout connectionError;


    Button btValidate;
    Button btTestConnection;

    Activity activity;

    String serverIp;
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
        salon = Salon.getInstance(this);
        socket = Salon.socket;

        connectionConnecting = (LinearLayout) findViewById(R.id.layout_connection_connecting);
        connectionSuccess = (LinearLayout) findViewById(R.id.layout_connection_success);
        connectionError = (LinearLayout) findViewById(R.id.layout_connection_error);
        inputServerIp = (EditText) findViewById(R.id.input_server_ip);
        inputServerIp.setText(Salon.SERVER_IP);
        btValidate = (Button) findViewById(R.id.button_validate_settings);
        btValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salon.saveConfig(inputServerIp);
                salon.close();
                salon = Salon.getInstance(activity);
                socket = Salon.socket;
                Intent i = new Intent(activity, MainActivity.class);
                startActivity(i);
            }
        });
        btTestConnection = (Button) findViewById(R.id.button_test_connection);
        btTestConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salon.saveConfig(inputServerIp);
                updateConnectionStatus();
            }
        });
        updateConnectionStatus();
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
        super.onBackPressed();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    // @TODO: déplacer dans un fichier Utils.
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }


    @Subscribe
    public void onSocketConnected(SocketConnectedEvent event) {
        updateConnectionStatus();
    }

    @Subscribe
    public void onSocketConnectionError(SocketConnectionErrorEvent event) {
        updateConnectionStatus();
    }


    public void updateConnectionStatus() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (Salon.CURRENT_STATUS) {
                    case Salon.STATUS_CONNECTED:
                        connectionConnecting.setVisibility(View.GONE);
                        connectionSuccess.setVisibility(View.VISIBLE);
                        connectionError.setVisibility(View.GONE);
                        break;
                    case Salon.STATUS_ERROR:
                        connectionConnecting.setVisibility(View.GONE);
                        connectionSuccess.setVisibility(View.GONE);
                        connectionError.setVisibility(View.VISIBLE);
                        break;
                    default:
                        connectionConnecting.setVisibility(View.VISIBLE);
                        connectionSuccess.setVisibility(View.GONE);
                        connectionError.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }
}
