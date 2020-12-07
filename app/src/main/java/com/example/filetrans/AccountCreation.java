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

import java.util.regex.Pattern;

public class AccountCreation extends AppCompatActivity {
    //create all the variables
    EditText input_email, input_password;
    Button register;
    TextView newLogin;
    FirebaseAuth Auth;

    //A method to move to the LOGIN page
    void moveToLogin(){
        Intent intent = new Intent(AccountCreation.this, LoginPage.class);
        AccountCreation.this.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_creation);

        //Initialize all the views
        input_email = findViewById(R.id.input_email);
        input_password = findViewById(R.id.input_password);
        register = findViewById(R.id.register);
        Auth = FirebaseAuth.getInstance();
        newLogin = findViewById(R.id.newLogin);


        //----------------START OF THE GO_TO_LOGIN FEATURE----------------
        //When LOGIN textView is clicked
        newLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToLogin();
            }
        });
        //----------------END OF THE GO_TO_LOGOUT FEATURE----------------

        //----------------START OF THE REGISTER FEATURE----------------
        //When REGISTER button is clicked
        register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                //Get the string values of the user inputs
                String string_email = input_email.getText().toString().trim();
                String string_password = input_password.getText().toString().trim();
                String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                        "[a-zA-Z0-9_+&*-]+)*@" +
                        "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                        "A-Z]{2,7}$";
                Pattern pat = Pattern.compile(emailRegex);


                //Check the EMAIL and its format
                if (TextUtils.isEmpty(string_email)){
                    input_email.setError("Email is required.");
                }
                else if (!pat.matcher(string_email).matches()){
                    input_email.setError("Invalid email format.");
                }

                //Check the PASSWORD and its length
                //TODO: ADD FORMAT FOR THIS
                if (TextUtils.isEmpty(string_password)){
                    input_password.setError("Password is required.");
                    return;
                }
                else if (string_password.length()<6){
                    input_password.setError("Password must be longer than 6 characters");
                    return;
                }


                //If all the conditions above is checked, ACCOUNT CREATION through firebase
                //ACCOUNT CREATION through firebase
                Auth.createUserWithEmailAndPassword(string_email,string_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Check to see if the creation was successful
                        if (task.isSuccessful()){
                            Toast.makeText(AccountCreation.this, "Account successfully created", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), LoginPage.class));
                            userEmail.userEmail = string_email;
                        }
                        //if the creation failed...
                        else {
                            Toast.makeText(AccountCreation.this, "Account creation failed " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
        //----------------END OF THE REGISTER FEATURE----------------
    }
}