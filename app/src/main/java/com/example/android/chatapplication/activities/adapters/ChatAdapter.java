package com.example.android.chatapplication.activities.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.chatapplication.activities.models.ChatMessage;
import com.example.android.chatapplication.databinding.ItemConatainerReceivedMessageBinding;
import com.example.android.chatapplication.databinding.ItemContainerSendMessageBinding;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<ChatMessage>chatMessages;
    private final String senderId;
    private final Bitmap receiverProfileImage;

    public static final int VIEW_TYPE_SENT=1;
    public static final int VIEW_TYPE_RECEIVED=2;
    public ChatAdapter(List<ChatMessage> chatMessages, String senderId, Bitmap receiverProfileImage) {
        this.chatMessages = chatMessages;
        this.senderId = senderId;
        this.receiverProfileImage = receiverProfileImage;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==VIEW_TYPE_SENT){
            return new SendMessageViewHolder(
              ItemContainerSendMessageBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false)
            );
        }else{
            return new ReceivedMessageViewHolder(
              ItemConatainerReceivedMessageBinding.inflate(
                      LayoutInflater.from(parent.getContext()),
                      parent,
                      false
              )
            );
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position)==VIEW_TYPE_SENT){
            ((SendMessageViewHolder)holder).setData(chatMessages.get(position));
        }else{
            ((ReceivedMessageViewHolder)holder).setData(chatMessages.get(position),receiverProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }
    @Override
    public int getItemViewType(int position){
        if(chatMessages.get(position).senderId.equals(senderId)){
            return VIEW_TYPE_SENT;
        }else return VIEW_TYPE_RECEIVED;
    }
    static class SendMessageViewHolder extends RecyclerView.ViewHolder{
        private final ItemContainerSendMessageBinding binding;
        SendMessageViewHolder(ItemContainerSendMessageBinding itemContainerSendMessageBinding){
            super(itemContainerSendMessageBinding.getRoot());
            binding=itemContainerSendMessageBinding;
        }
        void setData(ChatMessage chatMessage){
            binding.textMessages.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dataTime);
        }
    }
    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder{
        private final ItemConatainerReceivedMessageBinding binding;
        ReceivedMessageViewHolder(ItemConatainerReceivedMessageBinding itemConatainerReceivedMessageBinding){
            super(itemConatainerReceivedMessageBinding.getRoot());
            binding=itemConatainerReceivedMessageBinding;
        }
        void setData(ChatMessage chatMessage,Bitmap receiverProfileImage){
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dataTime);
            binding.imageProfile.setImageBitmap(receiverProfileImage);
        }
    }
}
