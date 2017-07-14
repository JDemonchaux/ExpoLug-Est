package fr.devloop.compteursalonlego.UI;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import fr.devloop.compteursalonlego.R;
import fr.devloop.compteursalonlego.SettingsActivity;

/**
 * Created by jeromedemonchaux on 14/07/2017.
 */

public class DevloopErrorDialog extends Dialog implements android.view.View.OnClickListener {

    public static final int ERR_WIFI = 0;
    public static final int ERR_SERVER_NOT_FOUND = 1;


    int ERROR_CODE;

    Activity activity;
    Button yes, no;
    TextView title, message;

    public DevloopErrorDialog(@NonNull Activity activity, int errCode) {
        super(activity);
        this.activity = activity;
        ERROR_CODE = errCode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_dialog_error);
        yes = (Button) findViewById(R.id.button_dialog_yes);
        no = (Button) findViewById(R.id.button_dialog_no);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);

        title = (TextView) findViewById(R.id.dialog_error_title);
        message = (TextView) findViewById(R.id.dialog_error_text);

        title.setText(R.string.dialog_error_title);
        if (ERROR_CODE == ERR_WIFI) {
            message.setText(R.string.dialog_err_wifi);
        } else if (ERROR_CODE == ERR_SERVER_NOT_FOUND) {
            message.setText(R.string.dialog_err_serv_not_found);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_dialog_yes:
                if (ERROR_CODE == ERR_WIFI) {
                    activity.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                } else if (ERROR_CODE == ERR_SERVER_NOT_FOUND) {
                    activity.startActivity(new Intent(activity, SettingsActivity.class));
                }
                dismiss();
                break;
            case R.id.button_dialog_no:
                dismiss();
                activity.finish();
                System.exit(0);
                break;
            default:
                break;
        }
        dismiss();
    }
}
