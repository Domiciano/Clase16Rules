package co.domi.clase10.activities;

import androidx.appcompat.app.AppCompatActivity;
import co.domi.clase10.R;
import co.domi.clase10.comm.HTTPSWebUtilDomi;
import co.domi.clase10.model.ChatRelationship;
import co.domi.clase10.model.FCMMessage;
import co.domi.clase10.model.FCMWrapper;
import co.domi.clase10.model.Message;
import co.domi.clase10.model.User;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText msgET;
    private Button sendBtn;
    private TextView messagesTV;
    private User myUser;
    private User userClicked;
    private FirebaseFirestore db;
    private ChatRelationship currentChat;
    private ListenerRegistration listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        msgET = findViewById(R.id.msgET);
        sendBtn = findViewById(R.id.sendBtn);
        messagesTV = findViewById(R.id.messagesTV);

        myUser = (User) getIntent().getExtras().getSerializable("myUser");
        userClicked = (User) getIntent().getExtras().getSerializable("userClicked");

        sendBtn.setOnClickListener(this);

        Toast.makeText(this, "Usted esta en la conversacion de " + myUser.getUsername() + " con " + userClicked.getUsername(), Toast.LENGTH_LONG).show();

        db = FirebaseFirestore.getInstance();



        resolveCurrentChat();







    }

    private void suscribeToMessages() {
        CollectionReference messagesRef = db.collection("chats")
                .document(currentChat.getChatID()).collection("messages");
        Query query = messagesRef.orderBy("timestamp", Query.Direction.DESCENDING).limit(20);
        listener = query.addSnapshotListener(
                (data, error)->{
                    messagesTV.setText("");
                    for(DocumentSnapshot doc : data.getDocuments()){
                        Message m = doc.toObject(Message.class);
                        messagesTV.append(m.getBody()+"\n");
                    }
                }
        );
    }

    @Override
    protected void onDestroy() {
        listener.remove();
        super.onDestroy();
    }

    private void resolveCurrentChat() {
        CollectionReference userRef = db.collection("users").document(myUser.getId()).collection("chats");
        Query query = userRef.whereEqualTo("contactID", userClicked.getId());

        query.get().addOnCompleteListener(
                task -> {
                    if(task.isSuccessful()){
                        if(task.getResult().size() > 0){
                            for(QueryDocumentSnapshot doc : task.getResult()){
                                currentChat = doc.toObject(ChatRelationship.class);
                            }
                        }else{
                            //Primera vez
                            String chatUUID = UUID.randomUUID().toString();
                            ChatRelationship rel1 = new ChatRelationship(chatUUID, userClicked.getId(), userClicked.getUsername());
                            ChatRelationship rel2 = new ChatRelationship(chatUUID, myUser.getId(), myUser.getUsername());
                            db.collection("users").document(myUser.getId()).collection("chats").document(chatUUID).set(rel1);
                            db.collection("users").document(userClicked.getId()).collection("chats").document(chatUUID).set(rel2);
                            currentChat = rel1;
                        }
                        suscribeToMessages();
                    }else{
                        Log.e(">>>", task.getException().getMessage());
                    }
                }
        );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sendBtn:
                //Mandar a Firestore
                String msg = msgET.getText().toString();
                Message message = new Message(UUID.randomUUID().toString(), msg, myUser.getId(), new Date().getTime());
                db.collection("chats").document(currentChat.getChatID()).collection("messages").document(message.getId()).set(message);

                //Mandar a FCM
                new Thread(
                        ()->{
                            HTTPSWebUtilDomi utilDomi = new HTTPSWebUtilDomi();
                            Gson gson = new Gson();
                            FCMMessage fcmMessage = new FCMMessage(UUID.randomUUID().toString(), msg);
                            FCMWrapper wrapper = new FCMWrapper("/topics/"+userClicked.getUsername(),fcmMessage);
                            String json = gson.toJson( wrapper );
                            utilDomi.POSTtoFCM(json);
                        }
                ).start();

                break;
        }
    }

    @Override
    protected void onPause() {
        //Suscribimos a nuestra propia rama
        FirebaseMessaging.getInstance().subscribeToTopic(myUser.getUsername());
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseMessaging.getInstance().unsubscribeFromTopic(myUser.getUsername());
    }

}