package fr.devloop.compteursalonlego;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import fr.devloop.compteursalonlego.UI.DonutProgress;

/**
 * Created by jerom on 01/07/2017.
 */

public class LegoActivity extends AppCompatActivity {

    Toolbar toolBar;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
    }


    protected void setActionBar(Activity activity) {
        int bgColor, textColor;
        if (activity instanceof InActivity || activity instanceof SettingsActivity) {
            bgColor = R.color.colorAccent;
            textColor = R.color.colorSecondaryText;
        } else {
            bgColor = R.color.colorPrimary;
            textColor = R.color.colorSecondaryText;
        }
        toolBar = (Toolbar) findViewById(R.id.app_toolbar);
        toolBar.setBackgroundColor(ContextCompat.getColor(this, bgColor));
        toolBar.setTitleTextColor(ContextCompat.getColor(this, textColor));
        setSupportActionBar(toolBar);
    }

    public void setProgressingColor(final DonutProgress visitor_number) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                visitor_number.setUnfinishedStrokeColor(ContextCompat.getColor(activity, R.color.colorSecondary));
                visitor_number.setFinishedStrokeColor(ContextCompat.getColor(activity, R.color.colorAccent));
            }
        });
    }

    public void setProgressFinishedColor(final DonutProgress visitor_number) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                visitor_number.setUnfinishedStrokeColor(ContextCompat.getColor(activity, R.color.colorPrimary));
                visitor_number.setFinishedStrokeColor(ContextCompat.getColor(activity, R.color.colorPrimary));
            }
        });
    }
}
