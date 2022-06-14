package com.example.android.chatapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.android.chatapplication.R;
import com.example.android.chatapplication.activities.utilitise.Constant;
import com.example.android.chatapplication.databinding.ActivityChatBinding;
import com.google.firebase.firestore.auth.User;


public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private User receiverUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        loadRecieverDetails();
    }
    private void loadRecieverDetails(){
//        receiverUser=(User) getIntent().getSerializableExtra(Constant.KEY_USER);
//        binding.textName.setText(receiverUser.name);
    }

}