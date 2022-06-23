package com.example.android.chatapplication.activities;



import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.example.android.chatapplication.activities.Listners.ConversationListener;
import com.example.android.chatapplication.activities.adapters.RecentConversationsAdapter;
import com.example.android.chatapplication.activities.models.ChatMessage;
import com.example.android.chatapplication.activities.models.User;
import com.example.android.chatapplication.activities.utilitise.Constant;
import com.example.android.chatapplication.activities.utilitise.PreferenceManager;
import com.example.android.chatapplication.databinding.ActivityMainBinding;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseActivity implements ConversationListener {
    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;
    private List<ChatMessage> conversations;
    private RecentConversationsAdapter conversationsAdapter;
    private FirebaseFirestore database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager=new PreferenceManager(getApplicationContext());
        init();
        loadUserDetails();
        setListeners();
        listenConversation();
//        getToken();
    }
    private void init(){
        conversations=new ArrayList<>();
        conversationsAdapter=new RecentConversationsAdapter(conversations,this);
        binding.conversionRecyclerView.setAdapter(conversationsAdapter);
        database=FirebaseFirestore.getInstance();
    }

    private void setListeners(){
        binding.imageSignOut.setOnClickListener(v->signOut());
        binding.fabNewChat.setOnClickListener(v->{
            startActivity(new Intent(getApplicationContext(),UsersActivity.class));
        });
    }
    private void loadUserDetails(){
        binding.textName.setText(preferenceManager.getString(Constant.KEY_NAME));
        byte[] bytes= Base64.decode(preferenceManager.getString(Constant.KEY_IMAGE),Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    private void listenConversation(){
        database.collection(Constant.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constant.KEY_SENDER_ID,preferenceManager.getString(Constant.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        database.collection(Constant.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constant.KEY_RECEIVER_ID,preferenceManager.getString(Constant.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }
    private final EventListener<QuerySnapshot>eventListener=(value,error)->{
      if(error!=null){
          return;
      }
      if(value!=null){
          for(DocumentChange documentChange: value.getDocumentChanges()){
              if(documentChange.getType()==DocumentChange.Type.ADDED){
                  String senderId=documentChange.getDocument().getString(Constant.KEY_SENDER_ID);
                  String receiverId=documentChange.getDocument().getString(Constant.KEY_RECEIVER_ID);
                  ChatMessage chatMessage=new ChatMessage();
                  chatMessage.senderId=senderId;
                  chatMessage.receiverId=receiverId;
                  if(preferenceManager.getString(Constant.KEY_USER_ID).equals(senderId)){
                      chatMessage.conversionImage=documentChange.getDocument().getString(Constant.KEY_RECEIVER_IMAGE);
                      chatMessage.conversionName=documentChange.getDocument().getString(Constant.KEY_RECEIVER_NAME);
                      chatMessage.conversionId=documentChange.getDocument().getString(Constant.KEY_RECEIVER_ID);

                  }else{
                      chatMessage.conversionImage=documentChange.getDocument().getString(Constant.KEY_SENDER_IMAGE);
                      chatMessage.conversionName=documentChange.getDocument().getString(Constant.KEY_SENDER_NAME);
                      chatMessage.conversionId=documentChange.getDocument().getString(Constant.KEY_SENDER_ID);
                  }
                  chatMessage.message=documentChange.getDocument().getString(Constant.KEY_LAST_MESSAGE);
                  chatMessage.dateObject=documentChange.getDocument().getDate(Constant.KEY_TIMESTAMP);
                  conversations.add(chatMessage);
              }else if(documentChange.getType()==DocumentChange.Type.MODIFIED){
                  for(int i=0;i<conversations.size();i++){
                      String senderId=documentChange.getDocument().getString(Constant.KEY_SENDER_ID);
                      String receiverId=documentChange.getDocument().getString(Constant.KEY_RECEIVER_ID);
                      if(conversations.get(i).senderId.equals(senderId)&&conversations.get(i).receiverId.equals(receiverId)){
                          conversations.get(i).message=documentChange.getDocument().getString(Constant.KEY_LAST_MESSAGE);
                          conversations.get(i).dateObject=documentChange.getDocument().getDate(Constant.KEY_TIMESTAMP);
                          break;
                      }
                  }
              }
          }
          Collections.sort(conversations,(obj1,obj2)->obj2.dateObject.compareTo(obj1.dateObject));
          conversationsAdapter.notifyDataSetChanged();
          binding.conversionRecyclerView.smoothScrollToPosition(0);
          binding.conversionRecyclerView.setVisibility(View.VISIBLE);
          binding.progressBar.setVisibility(View.GONE);
      }
    };
    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }
    private void updateToken(String token){
        FirebaseFirestore database=FirebaseFirestore.getInstance();
        DocumentReference documentReference=database.collection(Constant.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constant.KEY_USER_ID));
        documentReference.update(Constant.KEY_FCM_TOKEN,token)
                .addOnSuccessListener(unused -> {
                    showToast("Token updated succesfully");
                })
                .addOnFailureListener(e->showToast("unable to update token"));
    }
    private void signOut(){
        showToast("Signing out ...");
        FirebaseFirestore database=FirebaseFirestore.getInstance();
        DocumentReference documentReference=database.collection(Constant.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constant.KEY_USER_ID));
        HashMap<String, Object> updates=new HashMap<>();
        updates.put(Constant.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> showToast("unable to signout.."));
    }
    public void onConversionClicked(User user){
        Intent intent=new Intent(getApplicationContext(),ChatActivity.class);
        intent.putExtra(Constant.KEY_USER,user);
        startActivity(intent);
    }
}