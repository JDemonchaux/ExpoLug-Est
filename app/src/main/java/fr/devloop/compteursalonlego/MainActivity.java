package fr.devloop.compteursalonlego;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import fr.devloop.compteursalonlego.Library.Event.SalonAlmostFullEvent;
import fr.devloop.compteursalonlego.Library.Event.SalonFullEvent;
import fr.devloop.compteursalonlego.Library.Event.SocketGetVisitorEvent;
import fr.devloop.compteursalonlego.Library.NotificationsUtils;
import fr.devloop.compteursalonlego.Library.Salon;
import fr.devloop.compteursalonlego.UI.DonutProgress;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends LegoActivity {

    private Socket socket;
    private Salon salon;

    DonutProgress visitor_number;
    Button bt_activity_in;
    Button bt_activity_out;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        activity = this;

        super.setActionBar(this);

        visitor_number = (DonutProgress) findViewById(R.id.current_visitor);
        visitor_number.setMax(Salon.MAX_VISITOR);

        salon = Salon.getInstance(this);
        socket = Salon.socket;

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateVisitorNumber(Salon.CURRENT_VISITOR);
            }
        }, 500);


        bt_activity_in = (Button) findViewById(R.id.button_activity_in);
        bt_activity_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getApplicationContext(), InActivity.class);
                startActivity(in);
            }
        });

        bt_activity_out = (Button) findViewById(R.id.button_activity_out);
        bt_activity_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent out = new Intent(getApplicationContext(), OutActivity.class);
                startActivity(out);
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        if (socket == null) {
            socket = Salon.socket;
        } else if (!socket.connected()) {
            socket.connect();
        }
        updateVisitorNumber(Salon.current_visitor);
        super.onResume();
    }



    private void updateVisitorNumber(final Integer number) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                float value = Float.valueOf(number);
                if (value > (Salon.MAX_VISITOR * 0.99)) {
                    MainActivity.super.setProgressFinishedColor(visitor_number);
                } else {
                    MainActivity.super.setProgressingColor(visitor_number);
                }
                visitor_number.setText(number.toString());
                visitor_number.setProgress(value);
                visitor_number.setMax(Salon.MAX_VISITOR);
            }
        });
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
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }


    /*
     * EventBus
     */

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

    @Subscribe
    public void onSalonAlmostFullEvent(SalonAlmostFullEvent event) {
        NotificationsUtils.notifySalonAlmostFull(this, event.visitorNumber, InActivity.class);
    }

    @Subscribe
    public void onSalonFullEvent(SalonFullEvent event) {
        NotificationsUtils.notifySalonFull(this, event.visitorNumber, InActivity.class);
    }

    @Subscribe
    public void onSocketGetVisitorEvent(SocketGetVisitorEvent event) {
        updateVisitorNumber(Salon.CURRENT_VISITOR);
    }
}
