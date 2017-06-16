package fr.devloop.compteursalonlego;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import fr.devloop.compteursalonlego.Library.Salon;
import fr.devloop.compteursalonlego.UI.DonutProgress;

public class OutActivity extends AppCompatActivity {
    private Socket socket;

    DonutProgress visitor_number;
    Button bt_1;
    Button bt_2;
    Button bt_3;
    Button bt_4;
    Button bt_5;

    Toolbar toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_out);
        //initialize toolbar as appbar
        toolBar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(toolBar);

        //set back button
        ActionBar ab = getSupportActionBar();
        if (ab!=null) ab.setDisplayHomeAsUpEnabled(true);

        visitor_number = (DonutProgress) findViewById(R.id.current_visitor);
        visitor_number.setMax(Salon.MAX_VISITOR);

        socket = Salon.initSocket();
        socket.connect();

        socket.on(Salon.API_GET_VISITOR, new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                OutActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateVisitorNumber(args[0].toString());
                    }
                });
            }
        });

        bt_1 = (Button) findViewById(R.id.button_out_1);
        bt_2 = (Button) findViewById(R.id.button_out_2);
        bt_3 = (Button) findViewById(R.id.button_out_3);
        bt_4 = (Button) findViewById(R.id.button_out_4);
        bt_5 = (Button) findViewById(R.id.button_out_5);

        bt_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeVisitor(1);
            }
        });
        bt_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeVisitor(2);
            }
        });
        bt_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeVisitor(3);
            }
        });
        bt_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeVisitor(4);
            }
        });
        bt_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeVisitor(5);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        socket.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        socket = Salon.initSocket();
        socket.connect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Construit le menu
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void removeVisitor(int number) {
        socket.emit(Salon.API_REMOVE_VISITOR, number);
    }

    private void updateVisitorNumber(String number) {
        int value = Integer.valueOf(number);
        if (value <= 0) {
            bt_1.setEnabled(false);
            bt_2.setEnabled(false);
            bt_3.setEnabled(false);
            bt_4.setEnabled(false);
            bt_5.setEnabled(false);
        } else {
            bt_1.setEnabled(true);
            bt_2.setEnabled(true);
            bt_3.setEnabled(true);
            bt_4.setEnabled(true);
            bt_5.setEnabled(true);
        }

        visitor_number.setText(number);
        visitor_number.setProgress(Float.valueOf(number));
    }
}
