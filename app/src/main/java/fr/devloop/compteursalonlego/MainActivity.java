package fr.devloop.compteursalonlego;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import fr.devloop.compteursalonlego.Library.Salon;

public class MainActivity extends AppCompatActivity {

    private Socket socket;

    DonutProgress visitor_number;
    Button bt_activity_in;
    Button bt_activity_out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        visitor_number = (DonutProgress) findViewById(R.id.current_visitor);
        visitor_number.setMax(Salon.MAX_VISITOR);

        socket = Salon.initSocket();
        socket.connect();

        socket.on(Salon.API_GET_VISITOR, new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateVisitorNumber(args[0].toString());
                    }
                });
            }
        });

        bt_activity_in = (Button) findViewById(R.id.button_activity_in);
        bt_activity_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socket.close();
                Intent in = new Intent(getApplicationContext(), InActivity.class);
                startActivity(in);
            }
        });

        bt_activity_out = (Button) findViewById(R.id.button_activity_out);
        bt_activity_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socket.close();
                Intent out = new Intent(getApplicationContext(), OutActivity.class);
                startActivity(out);
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


    private void updateVisitorNumber(String number) {
        int value = Integer.valueOf(number);

        visitor_number.setText(number);
        visitor_number.setProgress(Float.valueOf(number));
    }
}
