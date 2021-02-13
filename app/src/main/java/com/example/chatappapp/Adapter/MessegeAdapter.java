package com.example.chatappapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatappapp.AllUser;
import com.example.chatappapp.Model.Chat;
import com.example.chatappapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessegeAdapter extends RecyclerView.Adapter<MessegeAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    public DatabaseReference reference;
    public AllUser arrayListUser;
    private Context mContext;
    private List<Chat> mChat;
    private String imageurl;
    public static FirebaseUser fuser;
    private StorageReference mStorageRef;


    public MessegeAdapter(Context context, List<Chat> mChat, String imageurl) {
        this.mContext = context;
        this.mChat = mChat;
        this.imageurl = imageurl;
    }

    public void getUsers() {
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayListUser = snapshot.getValue(AllUser.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return mChat == null ? 0 : mChat.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = mChat.get(position);
        if (holder.show_message != null) {
            holder.show_message.setText(chat.getMessage());

        }
    }

    private Chat getItem(int position) {
        return mChat.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fuser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else
            return MSG_TYPE_LEFT;
    }


    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        public CircleImageView chat_message_image;
        public TextView show_message;

        public ViewHolder(View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);
        }

        @Override
        public void onClick(View view) {
            Log.d("onclick", "onClick " + getLayoutPosition());
        }
    }

}
