package kr.ac.hansung.demap;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.ac.hansung.demap.ui.main.MainActivity;

public class ChattingActivity extends AppCompatActivity {
    static final String TAG = MainActivity.class.getSimpleName();
    static final String TOPIC1 = "topic";
    //static final String TOPIC2 = "topic2";
    static String currentUserId = "";

    private ChatAdapter chatAdapter;
    private MqttClient mqttClient1;
    //private MqttClient mqttClient2;

    @BindView(R.id.chatList)
    ListView chatListView;

    @BindView(R.id.chatEditText)
    EditText chatEditText;


    @OnClick(R.id.chatSendButton)
    public void sendChat(){

        //String currentUserId = "";
        //if(!tmpId.equals("")) {
        //    if(tmpId.equals("Soyeon") || tmpId.equals("Siru")) {
        currentUserId = mqttClient1.getClientId();
        //        idEditText.setEnabled(false);
        //    }
        //    else if(tmpId.equals("Sumin")) {
        currentUserId = mqttClient1.getClientId();
        //        idEditText.setEnabled(false);
        //    }
        //    else {
        //        idEditText.setText("존재하지않는 id");
        //    }
        //} else {
        //    idEditText.setHintTextColor(Color.RED);
        //}
        String content = chatEditText.getText().toString();
        if(content.equals("")){ }
        else{
            JSONObject json = new JSONObject();
            try{
                json.put("id",currentUserId);
                json.put("content",content);
                //if(currentUserId.equals(mqttClient2.getClientId())) {
                //    mqttClient2.publish(TOPIC2, new MqttMessage(json.toString().getBytes()));
                //} else if(currentUserId.equals(mqttClient1.getClientId())) {
                mqttClient1.publish(TOPIC1,new MqttMessage(json.toString().getBytes()));
                //}
            }catch (Exception e){

            } finally {
                chatEditText.setText("");
            }

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        chatAdapter = new ChatAdapter();
        chatListView.setAdapter(chatAdapter);
        try{
            connectMqtt();
        }catch(Exception e){
            Log.d(TAG,"MqttConnect Error");
        }
    }

    private void connectMqtt() throws Exception{

        MqttCallback mqttCallback = new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.d(TAG,"Mqtt ReConnect");
                try{connectMqtt();}catch(Exception e){Log.d(TAG,"MqttReConnect Error");}
            }
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                JSONObject json = new JSONObject(new String(message.getPayload(), "UTF-8"));
                chatAdapter.add(new ChatItem(json.getString("id"), json.getString("content")));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatAdapter.notifyDataSetChanged();
                    }
                });
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        };

        mqttClient1 = new MqttClient("tcp://192.168.168.100:1883", MqttClient.generateClientId(), null);
        mqttClient1.connect();
        mqttClient1.subscribe(TOPIC1);
        mqttClient1.setCallback(mqttCallback);


        //mqttClient2 = new MqttClient("tcp://192.168.168.100:1883", MqttClient.generateClientId(), null);
        //mqttClient2.connect();
        //mqttClient2.subscribe(TOPIC1);
        //mqttClient2.setCallback(mqttCallback);
    }
}
