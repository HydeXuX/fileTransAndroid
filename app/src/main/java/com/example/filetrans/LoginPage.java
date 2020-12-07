package com.example.filetrans;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class LoginPage extends AppCompatActivity {
    TextView newRegister;
    FirebaseAuth Auth;

    //A method to move to the REGISTER page
    void moveToRegister(){
        Intent intent = new Intent(LoginPage.this, AccountCreation.class);
        LoginPage.this.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        EditText login_email, login_password;
        Button login;
        newRegister = findViewById(R.id.newRegister);

        Auth = FirebaseAuth.getInstance();

        //Check to see if there is a user logged in now
        if (Auth.getCurrentUser() !=null ){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            userEmail.userEmail = user.getEmail();
            startActivity(new Intent(getApplicationContext(),MainPage.class));
            finish();
        }

        //Connect each one with the views
        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        login = findViewById(R.id.login);


        //----------------START OF THE GO_TO_REGISTER FEATURE----------------
        //When LOGIN textView is clicked
        newRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToRegister();
            }
        });
        //----------------END OF THE GO_TO_REGISTER FEATURE----------------


        //----------------START OF THE LOGIN FEATURE----------------
        //When LOGIN textView is clicked
        login.setOnClickListener(new View.OnClickListener() {
            //when login is clicked
            @Override
            public void onClick(View v) {
                //Get the string values of the user inputs
                String string_email = login_email.getText().toString().trim();
                String string_password = login_password.getText().toString().trim();
                //Email Authentication format
                String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                        "[a-zA-Z0-9_+&*-]+)*@" +
                        "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                        "A-Z]{2,7}$";
                Pattern pat = Pattern.compile(emailRegex);

                //Check the email and its format
                if (TextUtils.isEmpty(string_email)){
                    login_email.setError("Email is required.");
                }
                else if (!pat.matcher(string_email).matches()){
                    login_email.setError("Invalid email format.");
                }

                //Check the password and its length
                //TODO: ADD FORMAT FOR THIS
                if (TextUtils.isEmpty(string_password)){
                    login_password.setError("Password is required.");
                    return;
                }
                else if (string_password.length()<6){
                    login_password.setError("Password must be longer than 6 characters");
                    return;
                }

                //AUTHENTICATION of the user
                Auth.signInWithEmailAndPassword(string_email,string_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginPage.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainPage.class));
                            userEmail.userEmail = string_email;
                            finish();
                        }
                        else {
                            Toast.makeText(LoginPage.this, "Email or Password incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
        //----------------END OF THE LOGIN FEATURE----------------

    }


}