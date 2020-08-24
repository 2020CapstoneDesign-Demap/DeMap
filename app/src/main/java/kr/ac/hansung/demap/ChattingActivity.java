package kr.ac.hansung.demap;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChattingActivity extends AppCompatActivity {
    static final String TAG = ChattingActivity.class.getSimpleName();
    static final String TOPIC1 = "topic";
    static String currentUserId = "";

    private ChatAdapter chatAdapter;
    private MqttClient mqttClient1;

    ListView chatListView;
    EditText chatEditText;
    TextView chatSendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        ButterKnife.bind(this);

        // ActionBar에 타이틀 변경
        getSupportActionBar().setTitle("제주도 여행");
        // ActionBar의 배경색 변경
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.colorWhite));
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorWhite));

        // 홈 아이콘 표시
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        chatAdapter = new ChatAdapter();

        chatListView = findViewById(R.id.chatList);
        chatListView.setAdapter(chatAdapter);

        chatEditText = findViewById(R.id.chatEditText);

        chatSendButton = findViewById(R.id.chatSendButton);
        chatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentUserId = mqttClient1.getClientId();

                String content = chatEditText.getText().toString();
                if(content.equals("")){ }
                else{
                    JSONObject json = new JSONObject();
                    try{
                        json.put("id",currentUserId);
                        json.put("content",content);
                        mqttClient1.publish(TOPIC1,new MqttMessage(json.toString().getBytes()));
                    }catch (Exception e){

                    } finally {
                        chatEditText.setText("");
                    }

                }
            }
        });

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

        mqttClient1 = new MqttClient("tcp://172.30.1.57:1883", MqttClient.generateClientId(), null);
        mqttClient1.connect();
        mqttClient1.subscribe(TOPIC1);
        mqttClient1.setCallback(mqttCallback);

    }
}