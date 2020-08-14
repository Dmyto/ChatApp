package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.chatapp.Adapter.UserAdapter;
import com.example.chatapp.model.UserModel;
import com.example.chatapp.ui.login.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UserListActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private DatabaseReference usersDatabaseReference;
    private ChildEventListener usersChildEventListeners;

    private ArrayList<UserModel> userModelArrayList;
    private RecyclerView usersRecyclerView;
    private UserAdapter userAdapter;
    private RecyclerView.LayoutManager userLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        userModelArrayList = new ArrayList<>();
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

    private void buildRecyclerView() {
        usersRecyclerView = findViewById(R.id.userListRecyclerView);
        usersRecyclerView.setHasFixedSize(true);
        usersRecyclerView.addItemDecoration(new DividerItemDecoration(usersRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL));
        userLayoutManager = new LinearLayoutManager(this);
        userAdapter = new UserAdapter(userModelArrayList);

        usersRecyclerView.setLayoutManager(userLayoutManager);
        usersRecyclerView.setAdapter(userAdapter);

        userAdapter.setOnUserClickListener(position -> gotoChat(position));
    }

    private void gotoChat(int position) {
        Intent intent = new Intent(UserListActivity.this, ChatActivity.class);
        intent.putExtra("recipient", userModelArrayList.get(position).getId());
        startActivity(intent);
    }
}
