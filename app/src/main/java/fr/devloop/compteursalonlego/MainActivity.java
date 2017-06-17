package fr.devloop.compteursalonlego;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import fr.devloop.compteursalonlego.Library.Salon;
import fr.devloop.compteursalonlego.UI.DonutProgress;

public class MainActivity extends AppCompatActivity {

    private Socket socket;
    private Salon salon;

    DonutProgress visitor_number;
    Button bt_activity_in;
    Button bt_activity_out;
    Toolbar toolBar;

    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        activity = this;

        //Initialize toolbar as appbar
        toolBar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(toolBar);

        visitor_number = (DonutProgress) findViewById(R.id.current_visitor);
        visitor_number.setMax(Salon.MAX_VISITOR);

        salon = new Salon(this);
        socket = salon.initSocket();
        if (!socket.connected()) socket.connect();

        socket.on(Salon.API_GET_VISITOR, new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateVisitorNumber(args[0].toString());
                        Salon.current_visitor_number = Integer.parseInt(args[0].toString());
                    }
                });
            }
        });

        updateVisitorNumber(String.valueOf(Salon.current_visitor_number));



        bt_activity_in = (Button) findViewById(R.id.button_activity_in);
        bt_activity_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salon.close();
                Intent in = new Intent(getApplicationContext(), InActivity.class);
                startActivity(in);
            }
        });

        bt_activity_out = (Button) findViewById(R.id.button_activity_out);
        bt_activity_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salon.close();
                Intent out = new Intent(getApplicationContext(), OutActivity.class);
                startActivity(out);
            }
        });

    }


    @Override
    public void onBackPressed() {
        salon.close();
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        if (socket == null) {
            socket = new Salon(this).initSocket();
        } else if (!socket.connected()) {
            socket.connect();
        }
        super.onResume();
    }


    private void updateVisitorNumber(String number) {
        int value = Integer.valueOf(number);

        visitor_number.setText(number);
        visitor_number.setProgress(Float.valueOf(number));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Construit le menu
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                salon.close();
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
