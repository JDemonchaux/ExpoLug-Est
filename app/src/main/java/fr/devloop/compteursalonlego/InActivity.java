package fr.devloop.compteursalonlego;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import fr.devloop.compteursalonlego.Library.Event.SocketGetVisitorEvent;
import io.socket.client.Socket;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import fr.devloop.compteursalonlego.Library.Event.SalonAlmostFullEvent;
import fr.devloop.compteursalonlego.Library.NotificationsUtils;
import fr.devloop.compteursalonlego.Library.Salon;
import fr.devloop.compteursalonlego.UI.DonutProgress;
import io.socket.emitter.Emitter;

public class InActivity extends AppCompatActivity {

    private Socket socket;
    private Salon salon;

    DonutProgress visitor_number;
    Button bt_1;
    Button bt_2;
    Button bt_3;
    Button bt_4;
    Button bt_5;

    Toolbar toolBar;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_in);
        //initialize toolbar as appbar
        toolBar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(toolBar);
        activity = this;

        //set back button
        ActionBar ab = getSupportActionBar();
        if (ab != null) ab.setDisplayHomeAsUpEnabled(true);

        visitor_number = (DonutProgress) findViewById(R.id.current_visitor);
        visitor_number.setMax(Salon.MAX_VISITOR);
//        visitor_number.setProgress(Salon.CURRENT_VISITOR);

        salon = Salon.getInstance(this);
        socket = Salon.socket;

        bt_1 = (Button) findViewById(R.id.button_in_1);
        bt_2 = (Button) findViewById(R.id.button_in_2);
        bt_3 = (Button) findViewById(R.id.button_in_3);
        bt_4 = (Button) findViewById(R.id.button_in_4);
        bt_5 = (Button) findViewById(R.id.button_in_5);

        bt_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVisitor(1);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        visitor_number.setText(String.valueOf((Integer.parseInt(visitor_number.getText()) + 1)));
                    }
                });
            }
        });
        bt_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVisitor(2);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        visitor_number.setText(String.valueOf((Integer.parseInt(visitor_number.getText()) + 2)));
                    }
                });
            }
        });
        bt_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVisitor(3);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        visitor_number.setText(String.valueOf((Integer.parseInt(visitor_number.getText()) + 3)));
                    }
                });
            }
        });
        bt_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVisitor(4);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        visitor_number.setText(String.valueOf((Integer.parseInt(visitor_number.getText()) + 4)));
                    }
                });
            }
        });
        bt_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVisitor(5);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        visitor_number.setText(String.valueOf((Integer.parseInt(visitor_number.getText()) + 5)));
                    }
                });
            }
        });

        updateVisitorNumber(Salon.CURRENT_VISITOR);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        socket = Salon.socket;
        if (!socket.connected()) socket.connect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Construit le menu
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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

    private void addVisitor(int number) {
        socket.emit(Salon.API_ADD_VISITOR, String.valueOf(number));
    }

    private void updateVisitorNumber(final Integer number) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int value = number;
                Float progress = Float.valueOf(number);
                if (value > (Salon.MAX_VISITOR * 0.99)) {
                    progress = 100f;
                } else {
                    progress = (float) ((value / Salon.MAX_VISITOR) * 100);
                }
                visitor_number.setText(number.toString());
                visitor_number.setProgress(progress);
                visitor_number.setMax(Salon.MAX_VISITOR);
            }
        });
    }

    @Subscribe
    public void onSalonAlmostFullEvent(SalonAlmostFullEvent event) {
        NotificationsUtils.notifySalonAlmostFull(this, event.visitorNumber, InActivity.class);
    }

    ;

    @Subscribe
    public void onSocketGetVisitorEvent(SocketGetVisitorEvent event) {
        updateVisitorNumber(Salon.CURRENT_VISITOR);
    }
}
