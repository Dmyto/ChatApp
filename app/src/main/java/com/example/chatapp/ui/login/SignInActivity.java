package com.example.chatapp.ui.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.chatapp.R;
import com.example.chatapp.UserListActivity;
import com.example.chatapp.model.UserModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";
    private static final int RC_AVATAR_PICKER = 1;

    private StorageReference avatarImageStorageReference;
    private FirebaseStorage firebaseStorage;
    private Task<Uri> urlTask;

    private FirebaseAuth mAuth;

    @BindView(R.id.emailEditText)
    EditText emailEditText;

    @BindView(R.id.passwordEditText)
    EditText passwordEditText;
    @BindView(R.id.repeatPasswordEditText)
    EditText repeatPasswordEditText;

    @BindView(R.id.nameEditText)
    EditText nameEditText;

    @BindView(R.id.toggleLogInSignUpTextView)
    TextView toggleLogInSignUpTextView;

    @BindView(R.id.loginSignUpButton)
    Button loginSignUpButton;

    @BindView(R.id.account_avatar)
    ImageView accountAvatarImageView;

    @BindView(R.id.logoImageView)
    ImageView logoImageView;

    private boolean loginModeActive;

    FirebaseDatabase database;
    DatabaseReference usersDatabaseReferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        firebaseStorage = FirebaseStorage.getInstance();

        database = FirebaseDatabase.getInstance();
        usersDatabaseReferences = database.getReference().child("users");
        avatarImageStorageReference = firebaseStorage.getReference().child("avatar_images");

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, UserListActivity.class));
        }

        loginSignUpButton.setOnClickListener(view ->
                loginSignUpUser(emailEditText.getText().toString().trim(),
                        passwordEditText.getText().toString().trim()));

        accountAvatarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStorage();
            }
        });
    }

    private void openStorage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Choose an image"),
                RC_AVATAR_PICKER);
    }

    private void loginSignUpUser(String email, String password) {
        if (loginModeActive) {
            if (passwordEditText.getText().toString().trim().length() < 7) {
                Toast.makeText(this, "Password must be at least 7 characters", Toast.LENGTH_LONG).show();
            } else if (emailEditText.getText().toString().trim().equals("")) {
                Toast.makeText(this, "Input your email", Toast.LENGTH_LONG).show();
            } else {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Intent intent = new Intent(SignInActivity.this, UserListActivity.class);
                                    intent.putExtra("userName", nameEditText.getText().toString().trim());
                                    startActivity(intent);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(SignInActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

        } else {

            if (!passwordEditText.getText().toString().trim().equals(repeatPasswordEditText.getText().toString().trim())) {
                Toast.makeText(this, "Passwords don`t match", Toast.LENGTH_LONG).show();
            } else if (passwordEditText.getText().toString().trim().length() < 7) {
                Toast.makeText(this, "Password must be at least 7 characters", Toast.LENGTH_LONG).show();
            } else if (emailEditText.getText().toString().trim().equals("")) {
                Toast.makeText(this, "Input your email", Toast.LENGTH_LONG).show();
            } else {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    createUser(user);
                                    Intent intent = new Intent(SignInActivity.this, UserListActivity.class);
                                    startActivity(intent);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignInActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    }

    private void createUser(FirebaseUser user) {
        UserModel userModel = new UserModel();
        userModel.setId(user.getUid());
        userModel.setEmail(user.getEmail());
        if (urlTask == null) {
            userModel.setAvatarMockResource(null);
        } else {
            userModel.setAvatarMockResource(urlTask.getResult().toString());
        }
        userModel.setName(nameEditText.getText().toString().trim());
        usersDatabaseReferences.push().setValue(userModel);
    }

    public void toggleLogInMode(View view) {
        if (loginModeActive) {
            loginModeActive = false;
            loginSignUpButton.setText("Sign Up");
            accountAvatarImageView.setVisibility(View.VISIBLE);
            logoImageView.setVisibility(View.GONE);
            toggleLogInSignUpTextView.setText("Or, Log In");
            repeatPasswordEditText.setVisibility(View.VISIBLE);
            nameEditText.setVisibility(View.VISIBLE);
        } else {
            loginModeActive = true;
            loginSignUpButton.setText("Log In");
            toggleLogInSignUpTextView.setText("Or, SignUp");
            accountAvatarImageView.setVisibility(View.GONE);
            logoImageView.setVisibility(View.VISIBLE);
            repeatPasswordEditText.setVisibility(View.GONE);
            nameEditText.setVisibility(View.GONE);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_AVATAR_PICKER && resultCode == RESULT_OK) {
            Uri file = data.getData();
            StorageReference riversRef = avatarImageStorageReference.child(file.getLastPathSegment());
            UploadTask uploadTask = riversRef.putFile(file);

            // Register observers to listen for when the download is done or if it fails
            urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return riversRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Glide.with(accountAvatarImageView.getContext())
                                .load(downloadUri)
                                .apply(RequestOptions.circleCropTransform())
                                .into(accountAvatarImageView);
                    } else {
                        Toast.makeText(getApplicationContext(), "Unknown error", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
