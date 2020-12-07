package com.example.filetrans;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import static android.content.Context.DOWNLOAD_SERVICE;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    public static  final int MSG_TYPE_LEFT = 0;
    public static  final int MSG_TYPE_RIGHT = 1;
    public static  final int FILE_TYPE_LEFT = 2;
    public static  final int FILE_TYPE_RIGHT = 3;
    private static final String TAG = "ChatAdapter";

    private Context context;
    private List<Chat> chat;


    FirebaseUser user;

    public ChatAdapter(Context context, List<Chat> chat){
        this.context = context;
        this.chat = chat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Log.d("viewType issssssss","isssssssssssssssss"+viewType);
        //MSG_TYPE_LEFT = 0;
        //MSG_TYPE_RIGHT = 1;
        //FILE_TYPE_LEFT = 2;
        //FILE_TYPE_RIGHT = 3;
        if(viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new ChatAdapter.ViewHolder(view);
        }
        else if (viewType == FILE_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.file_item_right, parent, false);
            return new ChatAdapter.ViewHolder(view);
        }
        else if (viewType == MSG_TYPE_LEFT){
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new ChatAdapter.ViewHolder(view);
        }
        else if (viewType == FILE_TYPE_LEFT){
            View view = LayoutInflater.from(context).inflate(R.layout.file_item_left, parent, false);
            return new ChatAdapter.ViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chats = chat.get(position);
        if (chats.getType().equals("img")) {
            Picasso.with(context)
                    .load(chats.getDetail())
                    .fit()
                    .placeholder(R.drawable.ic_launcher_background)
                    .centerCrop()
                    .into(holder.show_img);
        }else if(chats.getType().equals("msg")){
            holder.show_message.setText(chats.getDetail());
        }
        else if(chats.getType().equals("file")){
            holder.show_file.setText(chats.getFileName());
        }
        if (!chats.getType().equals("msg")) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String fileName = chat.get(position).getFileName();
                    DownloadManager dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(chat.get(position).getDetail()));
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                            .setDestinationInExternalPublicDir(Environment.getExternalStorageDirectory() + File.separator, fileName)
                            .setTitle(fileName).setDescription("Downloaded")
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    dm.enqueue(request);
                    Toast.makeText(context, "Saved to" + Environment.getExternalStorageDirectory(), Toast.LENGTH_LONG).show();

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return chat.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView show_img;
        public TextView show_message;
        public TextView show_file;
        public ViewHolder(View itemView) {
            super(itemView);
            show_img = itemView.findViewById(R.id.show_img);
            show_message = itemView.findViewById(R.id.show_message);
            show_file = itemView.findViewById(R.id.show_file);
        }

    }
    @Override
    public int getItemViewType(int position) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        //Log.d("phone","isssssssssssssssssss"+chat.get(position).getIsPhone());
        //Log.d("type","isssssssssssssssssss"+chat.get(position).getType());
        if (chat.get(position).getIsPhone() == 1){
            if(chat.get(position).getType().equals("file")){
                return FILE_TYPE_RIGHT;
            }
            else {
                return MSG_TYPE_RIGHT;
            }
        } else {
            if(chat.get(position).getType().equals("file")){
                return FILE_TYPE_LEFT;
            }
            else {
                return MSG_TYPE_LEFT;
            }
        }
    }

}
