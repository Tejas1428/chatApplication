package com.example.android.chatapplication.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class Dialog extends AppCompatDialogFragment {
    public Boolean isRsa;
    public String msg;
    public Dialog(Boolean isRrsa){
        this.isRsa = isRrsa;
        if(isRrsa){
            msg = "Your messages are encrypted with RSA \n You can change it from here";
        }
        else{
            msg = "Your messages are encrypted with AES \n You can change it from here";
        }

    }
    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Result")
                .setMessage(msg)
                .setPositiveButton("RSA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(),"Your Messages are now encrypted with RSA", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("AES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isRsa = false;
                        Toast.makeText(getContext(),"Your Messages are now encrypted with AES", Toast.LENGTH_SHORT).show();
                    }
                });

        return builder.create();

    }
}
