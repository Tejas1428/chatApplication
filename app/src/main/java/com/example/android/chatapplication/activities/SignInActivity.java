package com.example.android.chatapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.android.chatapplication.activities.utilitise.Constant;
import com.example.android.chatapplication.activities.utilitise.PreferenceManager;
import com.example.android.chatapplication.databinding.ActivitySignInBinding;
import com.google.android.gms.common.internal.Constants;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class SignInActivity extends AppCompatActivity {
    private ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager= new PreferenceManager(getApplicationContext());
        if(preferenceManager.getBoolean(Constant.KEY_IS_SIGNED_IN)){
            Intent intent= new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        binding=ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }
    private void setListeners(){
//        binding.textCreateNewAccount.setOnClickListener(v-> startActivity(new Intent(getApplicationContext(),SignUpActivity.class)));
        binding.buttonSignin.setOnClickListener(v-> {
            if(isValidSignInDetails()) {
                signIn();
            }
        });
    }

        private void signIn(){
            loading(true);
            FirebaseFirestore database= FirebaseFirestore.getInstance();
            database.collection(Constant.KEY_COLLECTION_USERS)
                    .whereEqualTo(Constant.KEY_EMAIL, binding.inputEmail.getText().toString())
                    .whereEqualTo(Constant.KEY_PASSWORD, binding.inputPassword.getText().toString())
                    .get()
                    .addOnCompleteListener(task ->
                    {
                        if(task.isSuccessful() && task.getResult()!= null
                        && task.getResult().getDocuments().size()>0){
                            DocumentSnapshot documentSnapshot=task.getResult().getDocuments().get(0);
                            preferenceManager.putBoolean(Constant.KEY_IS_SIGNED_IN, true);
                            preferenceManager.putString(Constant.KEY_USER_ID, documentSnapshot.getId());
                            preferenceManager.putString(Constant.KEY_NAME, documentSnapshot.getString(Constant.KEY_NAME));
                            preferenceManager.putString(Constant.KEY_IMAGE, documentSnapshot.getString(Constant.KEY_IMAGE));
                            Intent intent= new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }else{
                            loading(false);
                            showToast("Unable to sign in");
                        }
                    });

        }

        private void loading(Boolean isLoading){
        if(isLoading){
            binding.buttonSignin.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);

        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSignin.setVisibility(View.VISIBLE);

        }


        }
        private void showToast(String message){
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
        }
        private Boolean isValidSignInDetails(){
        if(binding.inputEmail.getText().toString().trim().isEmpty()){
            showToast("Enter email");
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()){
            showToast("Enter valid email");
            return false;
        }else if(binding.inputPassword.getText().toString().trim().isEmpty()){
            showToast("Enter Password");
            return false;
        }else{
            return true;
        }
        }
}