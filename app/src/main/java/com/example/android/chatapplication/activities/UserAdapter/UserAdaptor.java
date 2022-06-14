package com.example.android.chatapplication.activities.UserAdapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.chatapplication.activities.ChatActivity;
import com.example.android.chatapplication.activities.Listners.UserListner;
import com.google.firebase.firestore.auth.User;

import java.util.List;
import java.util.zip.Inflater;


//    public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {
class UsersAdapter{
//        private final List<User> users ;
//        private final UserListner userListner;
//        public UsersAdapter ( List <User>users,UserListner userListner ) {
//            this.users = users ;
//            this.userListner=userListner;
//        }
//        E
//        @NonNull
//        @Override
//                A
//        4 public UserViewHolder onCreateViewHolder ( @NonNull ViewGroup parent , int viewType ) {
//            ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate (
//                    Layout Inflater.from ( parent.getContext()) ,
//                    parent ,
//                    attachToParent : false
//) ;
//            return new UserViewHolder ( itemContainerUserBinding ) ;
//        }
//        @Override
//        public void onBindViewHolder ( @NonNull UsersAdapter.UserViewHolder holder , int position ) {
//            holder.setUserData(users.get(position)) ;
//        }
//        @Override
//        public int getItemCount(){return users.size(); }
//        class UserViewHolder extends RecyclerView.ViewHolder{
//            ItemContainerUserBinding binding ;
//            UserViewHolder ( ItemContainerUserBinding itemContainerUserBinding ) {
//                super ( itemContainerUserBinding.getRoot() ) ;
//                binding = itemContainerUserBinding ;
//            }
//            void setUserData ( User user ) {
//                binding.textName.setText(user.name) ;
//                binding.textEmail.setText(user.email) ;
//                binding.imageProfile.setImageBitmap(getUserImage(user.image)) ;
//                binding.getRoot().setOnClickListener(v-> userListener.onUserClicked (user));
//            }
//        }
//        private Bitmap getUserImage (String encodedImage) {
//            byte[] bytes = Base64.decode(encodedImage , Base64.DEFAULT );
//            return BitmapFactory.decodeByteArray( bytes , 0 , bytes.length ) ;
//        }
//        private void showErrorMessage() {
//            binding.textErrorMessage.setText( String.format ( " % s " , " No user available " ) ) ;
//            binding.textErrorMessage.setVisibility( View.VISIBLE ) ;
//        }
//        private void loading ( Boolean isLoading ) {
//            if (isLoading) {
//                binding.progressBar.setVisibility(View.VISIBLE);
//            } else {
//                binding.progressBar.setVisibility(View.INVISIBLE);
//            }
//        }
//
//        @Override
//        public void onUserClicked (User user ) {
//            Intent intent = new Intent(getApplicationContext(), ChatActivity.class ) ;
//            intent.putExtra(Constants.KEY_USER , user );
//            startActivity(intent ) ;
//            finish();
//
//        }
}
