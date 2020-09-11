package kr.ac.hansung.demap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ChattingActivity extends AppCompatActivity {
    static final String TAG = ChattingActivity.class.getSimpleName();
    static String TOPIC1 = "";
    static String currentUserId = "";

    private ChatAdapter chatAdapter;
    private MqttClient mqttClient1;

    ListView chatListView;
    EditText chatEditText;
    TextView chatSendButton;

    private String folder_name;
    private String nickName;
    private String messageId;
    private String chat_msg, chat_user;

    private ArrayList<String> list = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;

    // 채팅 저장 realDB
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        TOPIC1 = intent.getStringExtra("folder_id");
        folder_name = intent.getStringExtra("folder_name");
        nickName = intent.getStringExtra("nickname");
        messageId = TOPIC1+"_message"; // 채팅방 아이디

        // ActionBar에 타이틀 변경
        getSupportActionBar().setTitle(folder_name);
        // ActionBar의 배경색 변경
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.colorWhite));
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorWhite));

        // 홈 아이콘 표시
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 채팅방DB 생성
        reference = FirebaseDatabase.getInstance().getReference().child(messageId);

        chatAdapter = new ChatAdapter();

        chatListView = findViewById(R.id.chatList);

        chatAdapter.setNickname(nickName);
        chatListView.setAdapter(chatAdapter);

        chatEditText = findViewById(R.id.chatEditText);

        chatSendButton = findViewById(R.id.chatSendButton);
        chatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String content = chatEditText.getText().toString();

                if(content.equals("")){ }
                else{
                    JSONObject json = new JSONObject();
                    try{
                        json.put("id", nickName);
                        json.put("content", content);
                        mqttClient1.publish(TOPIC1,new MqttMessage(json.toString().getBytes()));

                        // 채팅 저장 부분
                        Map<String, Object> map = new HashMap<String, Object>();

                        String key = reference.push().getKey();
                        reference.updateChildren(map);

                        DatabaseReference root = reference.child(key);

                        Map<String, Object> objectMap = new HashMap<String, Object>();

                        objectMap.put("id", nickName);
                        objectMap.put("content", content);

                        root.updateChildren(objectMap);

                    }catch (Exception e){

                    } finally {
                        chatEditText.setText("");
                    }

                }
            }
        });

        reference.addChildEventListener(new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                chatConversation(dataSnapshot);
            }

            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                chatConversation(dataSnapshot);
            }

            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override public void onCancelled(DatabaseError databaseError) {

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

        mqttClient1 = new MqttClient("tcp://192.168.168.100:1883", MqttClient.generateClientId(), null);
        //mqttClient2 = new MqttClient("tcp://192.168.168.100:1883", MqttClient.generateClientId(), null);
        mqttClient1.connect();
        mqttClient1.subscribe(TOPIC1);
        mqttClient1.setCallback(mqttCallback);

    }

    private void chatConversation(DataSnapshot dataSnapshot) {
        Iterator i = dataSnapshot.getChildren().iterator();

        while (i.hasNext()) {
            chat_user = (String) ((DataSnapshot) i.next()).getValue();
            chat_msg = (String) ((DataSnapshot) i.next()).getValue();

            arrayAdapter.add(chat_user + " : " + chat_msg);
        }

        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}