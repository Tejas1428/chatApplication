package com.example.android.chatapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.android.chatapplication.R;
import com.example.android.chatapplication.activities.models.User;
import com.example.android.chatapplication.activities.utilitise.Constant;
import com.example.android.chatapplication.databinding.ActivityChatBinding;


public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private User receiverUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadRecieverDetails();
        setListener();
    }
    private void loadRecieverDetails(){
         receiverUser=(User) getIntent().getSerializableExtra(Constant.KEY_USER);
        binding.textName.setText(receiverUser.name);
    }
    private void setListener(){
        binding.imageBack.setOnClickListener(v->onBackPressed());
    }
}