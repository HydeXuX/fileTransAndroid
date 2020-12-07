package com.example.filetrans;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.MergeAdapter;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class MainPage extends AppCompatActivity {
    //All the interactive interfaces
    Button logOut;
    ImageButton sendText, chooseImage, chooseFile, takePhoto;
    EditText inputText;
    String fileUrl;
    //All the objects
    Intent intent;
    List<Chat> chatList;
    ChatAdapter chatAdapt;
    RecyclerView recyclerView;
    Uri filePath;
    String fileName;
    final int isPhone = 1;
    final int PICK_IMAGE_REQUEST = 71;
    final int PICK_FILE_REQUEST = 102;
    final int CAMERA_REQUEST = 103;
    //Firebase objects
    FirebaseAuth Auth;
    DatabaseReference reference;
    FirebaseUser user;
    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        Auth = FirebaseAuth.getInstance();
        intent = getIntent();

        user = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        //----------------START OF THE LOGOUT FEATURE----------------
        //When the user clicks on LOGOUT button
        logOut = findViewById(R.id.logOut);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Auth.signOut();
                Toast.makeText(MainPage.this, "Logged out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), LoginPage.class));
                finish();
                //Logs the user out and finishes.
            }
        });
        //----------------END OF THE LOGOUT FEATURE----------------


        //----------------START OF THE SEND_MSG FEATURE----------------
        sendText = findViewById(R.id.sendText);
        inputText = findViewById(R.id.input_field);
        //When users clicks SEND_TEXT button
        sendText.setOnClickListener(view -> {
            String msg = inputText.getText().toString().trim();
            if(!msg.equals("")){
                sendMsg(user.getUid(), msg);
            }else{}
        });
        //----------------END OF THE SEND_MSG FEATURE----------------


        //----------------START OF THE READ_CHAT FEATURE----------------
        //When there's a new item added to the database
        reference = FirebaseDatabase.getInstance().getReference("chats").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                readChat();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        //----------------END OF THE READ_CHAT FEATURE----------------


        //----------------START OF THE CHOOSE_IMAGE FEATURE----------------
        chooseImage = findViewById(R.id.selectImage);
        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        //----------------END OF THE CHOOSE_IMAGE FEATURE----------------

        //----------------START OF THE CHOOSE_FILE FEATURE----------------
        chooseFile = findViewById(R.id.selectFile);
        chooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseFile();
            }
        });
        //----------------END OF THE CHOOSE_FILE FEATURE----------------


        //----------------START OF THE CHOOSE_FILE FEATURE----------------
        chooseFile = findViewById(R.id.takePhoto);
        chooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });
        //----------------END OF THE CHOOSE_FILE FEATURE----------------

    }//----------------------------------------------END OF onCreate()--------------------------------------------
     //----------------------------------------------END OF onCreate()--------------------------------------------
     //----------------------------------------------END OF onCreate()--------------------------------------------


    //----------------START OF THE READ_IMG METHOD----------------
    private void readChat(){
        chatList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Chat chat = postSnapshot.getValue(Chat.class);
                    if(chat.getUserId().equals(user.getUid())) {
                        chatList.add(chat);
                    }
                    chatAdapt = new ChatAdapter(MainPage.this, chatList);
                    recyclerView.setAdapter(chatAdapt);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainPage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    //----------------END OF THE READ_IMG METHOD----------------


    //----------------START OF THE SEND_MSG METHOD----------------
    private void sendMsg(String userId, String msg){
        //Clean the input field first
        inputText.setText("");

        //Store the input into a Message object
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> message = new HashMap<>();
        message.put("userId", userId);
        message.put("detail", msg);
        message.put("isPhone", isPhone);
        message.put("type","msg");
        message.put("fileName","msg");
        //Send the object to FireStore
        reference.child("chats").push().setValue(message);
    }
    //----------------END OF THE SEND_MSG METHOD----------------


    //----------------START OF THE CHOOSE_FILE METHOD----------------
    private void chooseFile(){
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Choose File"), PICK_FILE_REQUEST);
    }
    //----------------END OF THE CHOOSE_FILE METHOD----------------


    //----------------START OF THE TAKE_PHOTO METHOD----------------
    private void takePhoto(){
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST);
    }
    //----------------END OF THE TAKE_PHOTO METHOD----------------

    //----------------START OF THE CHOOSE_IMAGE METHOD----------------
    private void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    //----------------END OF THE CHOOSE_IMAGE METHOD----------------


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //When the user chose to open chooseFile()
        if(requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            fileName = getFileName();
            Log.d("FilePath is","this: "+ filePath);
            Toast.makeText(MainPage.this, "File upload", Toast.LENGTH_SHORT).show();
            uploadFile("file");
        }
        //When the user chose to open chooseImage()
        else if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            fileName = getFileName();
            uploadFile("img");
        }
        //When the user chose to open takePhoto()
        else if(requestCode == CAMERA_REQUEST && resultCode == RESULT_OK
                && data != null )

        {
            //Log.d("ENTERED","CAMERA_REQUEST RESULT");
            Bitmap thumb = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumb.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));
            Date currentTime = Calendar.getInstance().getTime();
            DateFormat date = new SimpleDateFormat("yyyyMMdd_HHmmss");

            date.setTimeZone(TimeZone.getTimeZone("GMT-7:00"));

            String localTime = date.format(currentTime);
            File destination = new File(Environment.getExternalStorageDirectory(), localTime+".jpg");
            if (destination.exists()) {
                destination.delete();
            }
            FileOutputStream out;
            try {
                out = new FileOutputStream(destination);
                out.write(bytes.toByteArray());
                out.close();
                Log.d("File Path ", " " + destination.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            filePath = Uri.fromFile(destination);
            Log.d("filePath is: ","thissssssssssssssssss:"+filePath);
            fileName = getFileName();
            uploadFile("img");
        }
    }


    //----------------START OF THE UPLOAD_IMAGE METHOD----------------
    private void uploadFile(String fileType){
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("files/" +
                    System.currentTimeMillis()+ "." + getFileExtension(filePath));
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(MainPage.this, "Uploaded", Toast.LENGTH_SHORT).show();

                            if(taskSnapshot.getMetadata()!= null){
                                if (taskSnapshot.getMetadata().getReference()!=null){
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            fileUrl = uri.toString();
                                            HashMap<String, Object> file = new HashMap<>();
                                            file.put("detail",fileUrl);
                                            file.put("type",fileType);
                                            file.put("isPhone",isPhone);
                                            file.put("userId",user.getUid());
                                            file.put("fileName",fileName);
                                            reference = FirebaseDatabase.getInstance().getReference("chats");
                                            reference.push().setValue(file);
                                        }
                                    });
                                }
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(MainPage.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }
    //----------------END OF THE UPLOAD_IMAGE METHOD----------------


    private String getFileName(){
        Log.d("FILE PATH IS",filePath.getPath());
        int cut = filePath.getPath().lastIndexOf('/');
        if (cut != -1) {
            fileName = filePath.getPath().substring(cut + 1);
        }
        Log.d("FILE NAME IS",fileName);
        return fileName;
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

}