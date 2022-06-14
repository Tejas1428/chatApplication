package com.example.android.chatapplication.activities.utilitise;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.View;

import com.example.android.chatapplication.R;
import com.example.android.chatapplication.activities.Listners.UserListner;
//import com.example.android.chatapplication.activities.UserAdapter.UsersAdapter;
import com.example.android.chatapplication.databinding.ActivityUsersBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity implements UserListner {
    private ActivityUsersBinding binding ;
    private PreferenceManager preferenceManager ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
//        binding = ActivityUsersBinding.inflate(get Layout Inflater()) ;
//        setContentView(binding.getRoot()) ;
//        preferenceManager = new PreferenceManager(getApplication Context()) ;
//        setListeners() ;
//        getUsers( ) ;
    }
//    private void setListeners ( ) { binding.imageBack.setOnClickListener ( v - > on BackPressed ( ) ) ; }
//    private void getUsers ( ) {
//        loading isLoading : true ) ;
//        FirebaseFirestore database = FirebaseFirestore.getInstance ( ) ;
//        database.collection ( Constants.KEY_COLLECTION_USERS )
//                .get ( )
//                .addOnComplete Listener ( task - > {
//                loading isloading : false ) ;
//        String currentUserId = preferenceManager.getString ( Constants.KEY_USER_ID ) ;
//        if ( task.isSuccessful ( ) && task.getResult ( ) ! = null ) {
//            List<User> users = new ArrayList< >( ) ;
//            for ( QueryDocument Snapshot query Document Snapshot task.getResult ( ) ) {
//                if ( currentUserId.equals ( query Document Snapshot.getId ( ) ) ) {
//                    continue ;
//                }
//                User user = new User ( ) ;
//                user.name = query Document Snapshot.getString ( Constants.KEY_NAME ) ;
//                user.email = query Document Snapshot.getString ( Constants.KEY_EMAIL ) ;
//                user.image = query Document Snapshot.getString ( Constants.KEY_IMAGE ) ;
//                user.token = query Document Snapshot.getString ( Constants.KEY_FCM_TOKEN ) ;
//                users.add ( user ) ;
//            }
//            if ( users.size ( ) > 0 ) {
//                UsersAdapter usersAdapter = new UsersAdapter ( users,this ) ;
//                binding.usersRecyclerView.setAdapter ( usersAdapter ) ;
//                binding.usersRecyclerView.setAdapter ( usersAdapter ) ;
//                binding.usersRecyclerView.setVisibility ( View.VISIBLE ) ;
//            } else {
//                showErrorMessage ( ) ;
//            }
//        } else {
//        } ) ;
//        showErrorMessage ( ) ;
}