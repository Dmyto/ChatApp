package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.contentcapture.DataRemovalRequest;

import com.example.chatapp.Adapter.UserAdapter;
import com.example.chatapp.model.UserModel;
import com.example.chatapp.ui.login.SignInActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class UserListActivity extends AppCompatActivity {

    private static final int RC_AVATAR_PICKER = 1;


    private FirebaseAuth mAuth;
    private UserModel userModel;
    private String userName;

    private DatabaseReference usersDatabaseReference;
    private ChildEventListener usersChildEventListeners;

    private ArrayList<UserModel> userModelArrayList;
    private RecyclerView usersRecyclerView;
    private UserAdapter userAdapter;
    private RecyclerView.LayoutManager userLayoutManager;
    private StorageReference avatarImageStorageReference;
    private FirebaseStorage storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        Intent intent = getIntent();
        if (intent != null) {
            userName = intent.getStringExtra("username");
        }

        userModelArrayList = new ArrayList<>();
        storage = FirebaseStorage.getInstance();
        avatarImageStorageReference = storage.getReference().child("avatar_images");

        mAuth = FirebaseAuth.getInstance();
        buildRecyclerView();
        attachUserDatabaseReferenceListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sign_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(UserListActivity.this, SignInActivity.class));
                break;

            case R.id.profile_avatar:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Choose an image"),
                        RC_AVATAR_PICKER);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void attachUserDatabaseReferenceListener() {
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        if (usersChildEventListeners == null) {
            usersChildEventListeners = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    if (!userModel.getId().equals(mAuth.getCurrentUser().getUid())) {
                        userModel.setAvatarMockResource(userModel.getAvatarMockResource());
                        setUser(userModel);
                        userModelArrayList.add(userModel);
                        userAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            usersDatabaseReference.addChildEventListener(usersChildEventListeners);
        }
    }

    private void setUser(UserModel userModel) {
        this.userModel = userModel;
    }

    private void buildRecyclerView() {
        usersRecyclerView = findViewById(R.id.userListRecyclerView);
        usersRecyclerView.setHasFixedSize(true);
        usersRecyclerView.addItemDecoration(new DividerItemDecoration(usersRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL));
        userLayoutManager = new LinearLayoutManager(this);
        userAdapter = new UserAdapter(userModelArrayList);

        usersRecyclerView.setLayoutManager(userLayoutManager);
        usersRecyclerView.setAdapter(userAdapter);

        userAdapter.setOnUserClickListener(new UserAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(int position) {
                gotoChat(position);
            }
        });
    }

    private void gotoChat(int position) {
        Intent intent = new Intent(UserListActivity.this, ChatActivity.class);
        intent.putExtra("recipient", userModelArrayList.get(position).getId());
        intent.putExtra("userName", userName);
        intent.putExtra(" recipientAvatar", userModelArrayList.get(position).getAvatarMockResource());
        intent.putExtra("userAvatar", userModel.getAvatarMockResource());
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_AVATAR_PICKER && resultCode == RESULT_OK) {
            Uri selectedAvatarUri = data.getData();
            final StorageReference avatarReference = avatarImageStorageReference.child(selectedAvatarUri.getLastPathSegment());
            UploadTask uploadTask = avatarReference.putFile(selectedAvatarUri);

            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        task.getException();
                    }
                    return avatarReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        if (userModel != null) {
                            userModel.setAvatarMockResource(downloadUri.toString());
                        }
                    }
                }
            });
        }
    }
}
