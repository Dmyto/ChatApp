package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.chatapp.Adapter.MessageListAdapter;
import com.example.chatapp.model.ModelMessage;
import com.example.chatapp.model.UserModel;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.messageListView)
    RecyclerView messageRecyclerView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.sendPhotoButton)
    ImageView sendPhotoButton;

    @BindView(R.id.sendMessageButton)
    Button sendMessageButton;

    @BindView(R.id.messageEditText)
    EditText messageEditText;


    private String userName;
    private String recipientUserId;

    private MessageListAdapter messageListAdapter;

    private static final int RC_IMAGE_PICKER = 123;

    private FirebaseDatabase database;
    private DatabaseReference messagesDatabaseReferences;
    private ChildEventListener messagesChildEventListener;
    private DatabaseReference usersDatabaseReferences;
    private ChildEventListener usersChildEventListener;
    private FirebaseStorage storage;
    private StorageReference chatImageStorageReference;

    private FirebaseAuth auth;

    List<ModelMessage> modelMessageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);


        auth = FirebaseAuth.getInstance();
        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        messagesDatabaseReferences = database.getReference().child("messages");
        usersDatabaseReferences = database.getReference().child("users");
        chatImageStorageReference = storage.getReference().child("chat_images");

        Intent intent = getIntent();
        if (intent != null) {
            userName = intent.getStringExtra("userName");
            recipientUserId = intent.getStringExtra("recipient");
        } else {
            userName = "Default";
        }


        messageListAdapter = new MessageListAdapter(modelMessageList, recipientUserId, getSupportFragmentManager());
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        messageRecyclerView.setAdapter(messageListAdapter);

        progressBar.setVisibility(ProgressBar.INVISIBLE);

        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    sendMessageButton.setEnabled(true);
                } else {
                    sendMessageButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        messageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(500)});

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ModelMessage modelMessage = new ModelMessage();
                modelMessage.setText(messageEditText.getText().toString());
                modelMessage.setName(userName);
                modelMessage.setSender(auth.getCurrentUser().getUid());
                modelMessage.setRecipient(recipientUserId);
                modelMessage.setImageUrl(null);

                DateFormat dateFormat = new SimpleDateFormat("HH:mm");
                modelMessage.setTimeSent(dateFormat.format(new Date()));

                messagesDatabaseReferences.push().setValue(modelMessage);

                messageEditText.setText("");
            }
        });

        sendPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Choose an image"),
                        RC_IMAGE_PICKER);
            }
        });

        messagesChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ModelMessage message = dataSnapshot.getValue(ModelMessage.class);

                if (message.getSender().equals(auth.getCurrentUser().getUid()) && message.getRecipient().equals(recipientUserId)
                        || message.getRecipient().equals(auth.getCurrentUser().getUid()) && message.getSender().equals(recipientUserId)) {
                    modelMessageList.add(message);
                    messageListAdapter.notifyDataSetChanged();
                    messageRecyclerView.scrollToPosition(modelMessageList.size()-1);
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



        usersChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                if (userModel.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    userName = userModel.getName();
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
        usersDatabaseReferences.addChildEventListener(usersChildEventListener);
        messagesDatabaseReferences.addChildEventListener(messagesChildEventListener);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_IMAGE_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            final StorageReference imageReference = chatImageStorageReference.child(selectedImageUri.getLastPathSegment());

            UploadTask uploadTask = imageReference.putFile(selectedImageUri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return imageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        ModelMessage message = new ModelMessage();
                        message.setImageUrl(downloadUri.toString());
                        message.setSender(auth.getCurrentUser().getUid());
                        message.setRecipient(recipientUserId);
                        message.setName(userName);
                        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
                        message.setTimeSent(dateFormat.format(new Date()));
                        messagesDatabaseReferences.push().setValue(message);
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });

        }
    }
}