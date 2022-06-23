package com.example.android.chatapplication.activities;



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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
import java.util.Objects;


public class ChatActivity extends BaseActivity {
    private ActivityChatBinding binding;
    private User receiverUser;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private String conversationId=null;
    private Boolean isReceiverAvailable=false;
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
        if(conversationId!=null){
            updateConversion(binding.inputMessage.getText().toString());
        }else{
            HashMap<String, Object>conversion=new HashMap<>();
            conversion.put(Constant.KEY_SENDER_ID,preferenceManager.getString(Constant.KEY_USER_ID));
            conversion.put(Constant.KEY_SENDER_NAME,preferenceManager.getString(Constant.KEY_NAME));
            conversion.put(Constant.KEY_SENDER_IMAGE,preferenceManager.getString(Constant.KEY_IMAGE));
            conversion.put(Constant.KEY_RECEIVER_ID,receiverUser.id);
            conversion.put(Constant.KEY_RECEIVER_NAME,receiverUser.name);
            conversion.put(Constant.KEY_RECEIVER_IMAGE,receiverUser.image);
            conversion.put(Constant.KEY_LAST_MESSAGE,binding.inputMessage.getText().toString());
            conversion.put(Constant.KEY_TIMESTAMP,new Date());
            addConversion(conversion);
        }
        binding.inputMessage.setText(null);
    }
    private void listenAvailabilityOfReceiver(){
        database.collection(Constant.KEY_COLLECTION_USERS)
                .document(receiverUser.id)
                .addSnapshotListener(ChatActivity.this,(value,error)->{
                    if(error!=null) return;
                    if(value!=null){
                        if(value.getLong(Constant.KEY_AVAILABILITY)!=null){
                            int availability= Objects.requireNonNull(
                                    value.getLong(Constant.KEY_AVAILABILITY).intValue()
                            );
                            isReceiverAvailable=availability==1;
                        }
                    }
                    if(isReceiverAvailable){
                        binding.textAvailability.setVisibility(View.VISIBLE);
                    }else{
                        binding.textAvailability.setVisibility(View.GONE);
                    }
                });
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
        if(conversationId==null){
            checkForConversion();
        }
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
    private void addConversion(HashMap<String,Object>conversion){
        database.collection(Constant.KEY_COLLECTION_CONVERSATIONS)
                .add(conversion)
                .addOnSuccessListener(documentReference -> conversationId=documentReference.getId());
    }
    private void updateConversion(String message){
        DocumentReference documentReference=database.collection(Constant.KEY_COLLECTION_CONVERSATIONS).document(conversationId);
        documentReference.update(Constant.KEY_LAST_MESSAGE,message,Constant.KEY_TIMESTAMP,new Date());
    }
    private void checkForConversion(){
        if(chatMessages.size()!=0){
            checkForConversionRemotely(
                    preferenceManager.getString(Constant.KEY_USER_ID),
                    receiverUser.id
            );
            checkForConversionRemotely(
                    receiverUser.id,
                    preferenceManager.getString(Constant.KEY_USER_ID)
            );
        }
    }
    private void checkForConversionRemotely(String senderid, String receiverId){
        database.collection(Constant.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constant.KEY_SENDER_ID,senderid)
                .whereEqualTo(Constant.KEY_RECEIVER_ID,receiverId)
                .get()
                .addOnCompleteListener(conversionOnCompleteListener);
    }
    private final OnCompleteListener<QuerySnapshot> conversionOnCompleteListener=task->{
        if(task.isSuccessful()&&task.getResult()!=null && task.getResult().getDocuments().size()>0){
            DocumentSnapshot documentSnapshot=task.getResult().getDocuments().get(0);
            conversationId= documentSnapshot.getId();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        listenAvailabilityOfReceiver();
    }
}