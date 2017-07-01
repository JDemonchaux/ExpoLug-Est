package fr.devloop.compteursalonlego;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import fr.devloop.compteursalonlego.Library.Salon;
import io.socket.client.IO;

public class SplashActivity extends AppCompatActivity {

    private final String TAG = "SplashActivity";
    private final int CODE_WIFI_DISABLED = 1;
    private final int CODE_NOT_ON_WIFI = 2;
    private final int CODE_WIFI_OK = 3;
    private final int CODE_NO_INTERNET = 4;

    private final String CODE_PROGRESS_INIT = "Initialisation";
    private final String CODE_PROGRESS_CHECK_WIFI = "Vérification du Wifi";
    private final String CODE_PROGRESS_CHECK_SERVER = "Test de connection au serveur";
    private final String CODE_PROGRESS_CHECK_CONF = "Problème de configuration du serveur";
    private final String CODE_PROGRESS_OK = "Connection au server réussie!";

    TextView progressDialog;

    WifiManager mWifiManager;
    Activity activity;

    Runnable init;
    Handler splashHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splash);
        activity = this;
        progressDialog = (TextView) findViewById(R.id.text_progress);

        updateInitProgress(CODE_PROGRESS_INIT);

        connectToServer();
    }



    public void connectToServer() {
        splashHandler = new Handler();
        init = new Runnable() {
            @Override
            public void run() {
                updateInitProgress(CODE_PROGRESS_CHECK_WIFI);
                Integer resultCode = checkIfWifiEnable();
                switch (resultCode) {
                    case CODE_WIFI_DISABLED:
                        Log.d(TAG, "wifi disabled");
                        displayWifiDisabledAlert();
                        break;
                    case CODE_NOT_ON_WIFI:
                        displayWifiDisabledAlert();
                        Log.d(TAG, "not on wifi");
                        break;
                    case CODE_WIFI_OK:
                        Log.d(TAG, "wifi ok");
                        updateInitProgress(CODE_PROGRESS_CHECK_SERVER);
                        if (serverInConfig()) {
                            //Server is already configured and reachable: go to mainActivity
                            updateInitProgress(CODE_PROGRESS_OK);
                            Intent i = new Intent(activity, MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        } else {
                            //Server is not in configuration or unreachable, try to discover;


                            //Discovery failed, go to settingsActivity
                            updateInitProgress(CODE_PROGRESS_CHECK_CONF);
                            displayServerNeedConfigurationAlert();
                            if (splashHandler != null) {
                                if (init != null) {
                                    splashHandler.removeCallbacks(init);
                                }
                            }
                        }
                    default:
                        Log.d(TAG, "unknown error");
                        break;
                }
            }
        };
        splashHandler.postDelayed(init, 1500);
    }

    public Integer checkIfWifiEnable() {
        Integer resultCode;
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (!wm.isWifiEnabled()) {
            resultCode = CODE_WIFI_DISABLED;
        } else {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null) {
                if (activeNetwork.getType() != ConnectivityManager.TYPE_WIFI) {
                    resultCode = CODE_NOT_ON_WIFI;
                } else {
                    resultCode = CODE_WIFI_OK;
                    mWifiManager = wm;
                }
            } else {
                resultCode = CODE_NO_INTERNET;
            }
        }
        return resultCode;
    }

    public boolean serverInConfig() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String ip = prefs.getString(this.getString(R.string.pref_server_ip), "");

        //If server not in configuration, return false (to go to settings)
        if (ip.equals("")) {
            return false;
        }

        //if server is in configuration, but wrong adress (or not reachable)
        Boolean reach = Salon.isServerReachable(ip);
        return reach;

    }

    public boolean discoverServer() {
        boolean discovered = false;
        try {
            Salon.socket = IO.socket("noserver");

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return discovered;
    }

    public String intToSubnet(int i) {
        String ip = ((i >> 24) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                (i & 0xFF);
        String[] splittedIp = ip.split("\\.");
        StringBuilder sb = new StringBuilder();
        for (int j = splittedIp.length - 1; j >= 0; j--) {
            if (j != 0) {
                sb.append(splittedIp[j]);
                sb.append(".");
            }
        }
        return sb.toString();
    }

    public void writeServerToConfig(String ip) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString(this.getString(R.string.pref_server_ip), ip);
        editor.apply();
    }

    public void updateInitProgress(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null) {
                    progressDialog.setText(text);
                }
            }
        });
    }

    private void displayWifiDisabledAlert() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Attention")
                .setMessage("Vous devez activer votre wifi")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // start network settings
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // exit the app
                        activity.finish();
                        System.exit(0);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void displayServerNeedConfigurationAlert() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Attention")
                .setMessage("Le server n'est pas configuré ou est inaccessible, veuillez vérifier la configuration depuis les paramètres")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // start network settings
                        startActivity(new Intent(activity, SettingsActivity.class));
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // exit the app
                        activity.finish();
                        System.exit(0);
                    }
                })
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    @Override
    public void onStop() {
        super.onStop();
        if (splashHandler != null) {
            if (init != null) {
                splashHandler.removeCallbacks(init);
                init = null;
            }
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        if (init == null) {
            updateInitProgress(CODE_PROGRESS_INIT);
            connectToServer();
        }
    }


}
