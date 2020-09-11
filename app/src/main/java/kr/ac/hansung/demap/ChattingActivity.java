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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class ChattingActivity extends AppCompatActivity {

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    static final String TAG = ChattingActivity.class.getSimpleName();
    static String TOPIC1 = "";
    static String currentUserId = "";

    private ArrayList<ChatItem> ChatItemList = new ArrayList<ChatItem>();

    private ChatAdapter chatAdapter;
    private MqttClient mqttClient1;

    ListView chatListView;
    EditText chatEditText;
    TextView chatSendButton;

    private String folder_name;
    private String nickName;
    private String messageId;
    private String chat_msg, chat_user, chat_time;

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

        firestore.collection("chat").document(TOPIC1).collection("messages").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        ChatItem chatItem = document.toObject(ChatItem.class);
                        chatAdapter.add(chatItem);
                    }

                    chatAdapter.notifyDataSetChanged();

                } else {
                    System.out.println("Error getting documents: " + task.getException());

                }
            }
        });

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
                        Long time = System.currentTimeMillis();
                        json.put("timestamp", String.valueOf(time));
                        mqttClient1.publish(TOPIC1,new MqttMessage(json.toString().getBytes()));

                        // 채팅 저장 부분
                        ChatItem chatItem = new ChatItem(nickName, content, time);
                        firestore.collection("chat").document(TOPIC1).collection("messages").add(chatItem);

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
                chatAdapter.add(new ChatItem(json.getString("id"), json.getString("content"), Long.parseLong(json.getString("timestamp"))));
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

//        mqttClient1 = new MqttClient("tcp://192.168.168.100:1883", MqttClient.generateClientId(), null);
        mqttClient1 = new MqttClient("tcp://172.30.1.56:1883", MqttClient.generateClientId(), null);

        mqttClient1.connect();
        mqttClient1.subscribe(TOPIC1);
        mqttClient1.setCallback(mqttCallback);

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