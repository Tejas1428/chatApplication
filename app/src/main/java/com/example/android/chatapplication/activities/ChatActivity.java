package com.example.android.chatapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import com.example.android.chatapplication.activities.adapters.ChatAdapter;
import com.example.android.chatapplication.activities.models.ChatMessage;
import com.example.android.chatapplication.activities.models.User;
import com.example.android.chatapplication.activities.utilitise.Constant;
import com.example.android.chatapplication.activities.utilitise.PreferenceManager;
import com.example.android.chatapplication.databinding.ActivityChatBinding;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private User receiverUser;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadRecieverDetails();
        setListener();
        init();
        listenMessage();
    }
    private void init(){
        preferenceManager=new PreferenceManager(getApplicationContext());
        chatMessages=new ArrayList<>();
        chatAdapter=new ChatAdapter(
                        chatMessages,
                preferenceManager.getString(Constant.KEY_USER_ID),
                getBitmapFromEncodedString(receiverUser.image)
                );
        binding.chatRecyclerView.setAdapter(chatAdapter);
        database=FirebaseFirestore.getInstance();
    }
    private void sendMessage(){
        HashMap<String,Object>message=new HashMap<>();
        message.put(Constant.KEY_SENDER_ID,preferenceManager.getString(Constant.KEY_USER_ID));
        message.put(Constant.KEY_RECEIVER_ID,receiverUser.id);
        message.put(Constant.KEY_MESSAGE,binding.inputMessage.getText().toString());
        message.put(Constant.KEY_TIMESTAMP,new Date());
        database.collection(Constant.KEY_COLLECTION_CHAT).add(message);
        binding.inputMessage.setText(null);
    }
    private void listenMessage(){
        database.collection(Constant.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constant.KEY_SENDER_ID,preferenceManager.getString(Constant.KEY_USER_ID))
                .whereEqualTo(Constant.KEY_RECEIVER_ID,receiverUser.id)
                .addSnapshotListener(eventListener);
        database.collection(Constant.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constant.KEY_SENDER_ID,receiverUser.id)
                .whereEqualTo(Constant.KEY_RECEIVER_ID,preferenceManager.getString(Constant.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }
    private final EventListener<QuerySnapshot>eventListener=(value,error)->{
        if(error!=null) return;
        if(value!=null){
            int count=chatMessages.size();
            for(DocumentChange documentChange: value.getDocumentChanges()){
                if(documentChange.getType()==DocumentChange.Type.ADDED){
                    ChatMessage chatMessage=new ChatMessage();
                    chatMessage.senderId=documentChange.getDocument().getString(Constant.KEY_SENDER_ID);
                    chatMessage.receiverId=documentChange.getDocument().getString(Constant.KEY_RECEIVER_ID);
                    chatMessage.message=documentChange.getDocument().getString(Constant.KEY_MESSAGE);
                    chatMessage.dataTime=getReadableDateTime(documentChange.getDocument().getDate(Constant.KEY_TIMESTAMP));
                    chatMessage.dateObject=documentChange.getDocument().getDate(Constant.KEY_TIMESTAMP);
                    chatMessages.add(chatMessage);
                }
            }
            Collections.sort(chatMessages,(obj1,obj2)->obj1.dateObject.compareTo(obj2.dateObject));
            if(count==0){
                chatAdapter.notifyDataSetChanged();
            }else{
                chatAdapter.notifyItemRangeInserted(chatMessages.size(),chatMessages.size());
                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size()-1);
            }
            binding.chatRecyclerView.setVisibility(View.VISIBLE);
        }
        binding.progressBar.setVisibility(View.GONE);
    };
    private Bitmap getBitmapFromEncodedString(String encodedImage){
        byte[] bytes= Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0, bytes.length);
    }
    private void loadRecieverDetails(){
         receiverUser=(User) getIntent().getSerializableExtra(Constant.KEY_USER);
        binding.textName.setText(receiverUser.name);
    }
    private void setListener(){
        binding.imageBack.setOnClickListener(v->onBackPressed());
        binding.layoutSend.setOnClickListener((v->sendMessage()));
    }
    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }
}