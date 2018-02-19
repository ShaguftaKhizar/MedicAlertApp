package com.example.shagufta.medicalert_v3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    static final String SETTINGS_CLOUDANT_USER = "pref_key_username";
    static final String SETTINGS_CLOUDANT_DB = "pref_key_dbname";
    static final String SETTINGS_CLOUDANT_API_KEY = "pref_key_api_key";
    static final String SETTINGS_CLOUDANT_API_SECRET = "pref_key_api_password";

    MqttAndroidClient mqttAndroidClient;

    private static final String IOT_ORGANIZATION_TCP = ".messaging.internetofthings.ibmcloud.com:1883";
    private static final String IOT_DEVICE_USERNAME  = "use-token-auth";

    private String organization = "icqwqr";
    private String deviceType = "Android";
    private String deviceID = "MedicAlertAndroid";
    private String authorizationToken="3d0n6dX(nQ&J9bMMxv";

    final String subscriptionTopic = "iot-2/cmd/prendreMedoc25/fmt/String";
    //final String publishTopic = "iot-2/evt/majHoraires/fmt/String";
    final String publishTopic = "iot-2/evt/majMedoc/fmt/String";
    final String publishMessage = "maj";

    static final String LOG_TAG = "MainActivity";
    private static TaskModel sTasks;

    private Date currentTime;
    private int hour, minute;
    private static int day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.registerOnSharedPreferenceChangeListener(this);

        String clientID = "d:" +organization + ":" + deviceType + ":" + deviceID;
        final String connectionURI;
        connectionURI = "tcp://" + organization + IOT_ORGANIZATION_TCP;

        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), connectionURI, clientID);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

                if (reconnect) {
                    //addToHistory("Reconnected to : " + connectionURI);
                    // Because Clean Session is true, we need to re-subscribe
                    subscribeToTopic();
                } else {
                    addToHistory("Connected to: " + connectionURI);
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                addToHistory("The Connection was lost.");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                addToHistory("Incoming message: " + new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
        String username = IOT_DEVICE_USERNAME;
        char[] password = authorizationToken.toCharArray();

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password);


        try {
            //addToHistory("Connecting to " + serverUri);
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    addToHistory("Failed to connect to: " + connectionURI);
                }
            });


        } catch (MqttException ex){
            ex.printStackTrace();
        }


        if (sTasks == null) {
            // Model needs to stay in existence for lifetime of app.
            this.sTasks = new TaskModel(this.getApplicationContext());
        }
        this.sTasks.setReplicationListener(this);

        TextView stockText = (TextView) findViewById(R.id.stock);
        stockText.setText(R.string.stockVideTextView);

        currentTime = Calendar.getInstance().getTime();
        day = currentTime.getDay();
        hour = currentTime.getHours();
        minute = currentTime.getMinutes();

    }

    /*
    public void intent_start() {

        Intent i = Ordonnance.makeIntent(MainActivity.this);
        this.startActivityForResult(i, 1);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        int stock = 0;
        int jourFin = 0;

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                stock = data.getIntExtra("stock", stock);
                jourFin = data.getIntExtra("jourFin", jourFin);

                TextView stockText = (TextView) findViewById(R.id.stock);

                if(stock >= 0)
                {
                    stockText.setText("Vous avez " + stock + " comprime en trop");
                }
                else if(stock < 0)
                {
                    //notif a J-2
                    stockText.setText("Fin du stock de medicament le " + jourFin);
                }
                else
                {
                    stockText.setText("Stock de medicament vide pour le moment");
                }
            }
        }
    }

    public int getHour() { return this.hour; }
    public int getMinute() { return this.minute; }

    public void setHour(int hour) { this.hour = hour; }
    public void setMinute(int minute) { this.minute = minute; }
    public void setDay(int day) { this.day = day; }
    */
    public static int getDay() { return day; }
    private void reloadReplicationSettings() {
        try {
            this.sTasks.reloadReplicationSettings();
        } catch (URISyntaxException e) {
            Log.e(LOG_TAG, "Unable to construct remote URI from configuration", e);
            Toast.makeText(getApplicationContext(), R.string.replication_error, Toast.LENGTH_LONG).show();
        }
    }

    public static TaskModel getTaskModel() { return sTasks; }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(LOG_TAG, "onSharedPreferenceChanged()");
        reloadReplicationSettings();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_voirOrdonnance:
                this.startActivity(
                        new Intent().setClass(this, Ordonnance.class)
                );
                return true;
            case R.id.action_famille:
                //intent_start();
                //this.showDialog(DIALOG_PROGRESS);
                //sTasks.startPushReplication();
                return true;
            case R.id.action_settings:
                this.startActivity(
                        new Intent().setClass(this, SettingsActivity.class)
                );
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Au clic du bouton, publie un message
    public void testMQTT(View view){
        publishMessage();
    }

    // Affichage un bulle toast sur l'application avec le message mainText
    private void addToHistory(String mainText){
        System.out.println("LOG: " + mainText);
        Toast.makeText(MainActivity.this, mainText, Toast.LENGTH_LONG).show();

    }

    public void subscribeToTopic(){
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 1, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    addToHistory("Subscribed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    addToHistory("Failed to subscribe");
                }
            });

            // THIS DOES NOT WORK!
            mqttAndroidClient.subscribe(subscriptionTopic, 1, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    System.out.println("Message: " + topic + " : " + new String(message.getPayload()));
                    addToHistory(new String(message.getPayload()));


                }
            });

        } catch (MqttException ex){
            System.err.println("Exception whilst subscribing");
            ex.printStackTrace();
        }
    }

    public void publishMessage(){

        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(publishMessage.getBytes());//Publie le message qu'on veut, publishMessage est en global la haut
            mqttAndroidClient.publish(publishTopic, message);//Publie le message, faut le transformer en bytes
            addToHistory("Message Published"); //Affiche en toast
            if(!mqttAndroidClient.isConnected()){
                addToHistory(mqttAndroidClient.getBufferedMessageCount() + " messages in buffer.");
            }
        } catch (MqttException e) {
            System.err.println("Error Publishing: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
