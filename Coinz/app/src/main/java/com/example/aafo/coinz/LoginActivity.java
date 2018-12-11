package com.example.aafo.coinz;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//Followed the youtube videos https://www.youtube.com/watch?v=lF5m4o_CuNg&list=PLbte_tgDKVWQOCRIzkgEQ8umdn_S6ZnHr

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  {

    //Firebase
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference users;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    Button mEmailRegButton;
    Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Set the action bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Log in");

        //Firebase
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        //If the user is logged in, then stop this activity and go to the main one
        if(user != null){
            finish();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));

        }

        // Set up the login form.
        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);

        mEmailRegButton = findViewById(R.id.register_button);
        mEmailRegButton.setOnClickListener(view -> {
            if (validate()){
                //Upload data to database
                String userEmail = mEmailView.getText().toString().trim();
                String userPassword = mPasswordView.getText().toString().trim();

                mAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                    else{
                        Toast.makeText(LoginActivity.this, "Registration gone wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });




        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(v -> {
            if (validate()){
                String userEmail = mEmailView.getText().toString().trim();
                String userPassword = mPasswordView.getText().toString().trim();

                mAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                    else{
                        Toast.makeText(LoginActivity.this, "Login gone wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        findViewById(R.id.login_form);
    }

    private Boolean validate(){
        Boolean result = false;

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Enter all the details", Toast.LENGTH_SHORT).show();
        }
        else{
            result = true;
        }
        return result;
    }
}

