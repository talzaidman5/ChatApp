package com.example.chatappapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatappapp.Adapter.UserAdapter;
import com.example.chatappapp.Model.Chat;
import com.example.chatappapp.Model.User;
import com.example.chatappapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ChatsFragment extends Fragment {

    private RecyclerView recycler_view;
    private User allUser;
    private UserAdapter adapterUser;
    private List<User> mUsers;
    private ImageView chatsImage;
    private FirebaseUser fuser;
    private DatabaseReference reference;
    private List<String> usersList;
    private TextView chatsText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        recycler_view = view.findViewById(R.id.recycler_view);
        chatsText = view.findViewById(R.id.chatsText);
        chatsImage = view.findViewById(R.id.chatsImage);
        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        usersList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Chat chat = snap.getValue(Chat.class);
                    if (chat != null) {
                        if (chat.getSender().equals(fuser.getUid()))
                            usersList.add(chat.getReceiver());

                        if (chat.getReceiver().equals(fuser.getUid()))
                            usersList.add(chat.getSender());
                    }
                }
                readChats();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return view;
    }

    private void readChats() {
        mUsers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    for (String id : usersList) {
                        if (user.getId().equals(id)) {
                            if (mUsers.size() != 0) {
                                for (User user1 : mUsers) {
                                    if (!user.getId().equals(user1.getId()))
                                        mUsers.add(user);
                                }
                            } else
                                mUsers.add(user);
                        }
                    }
                }
                if(mUsers.size()>0) {
                    adapterUser = new UserAdapter(getContext(), mUsers);
                    recycler_view.setAdapter(adapterUser);
                    chatsImage.setVisibility(View.INVISIBLE);
                    chatsText.setText("");

                }
                else{
                    chatsImage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void readFromFB() {
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allUser = snapshot.getValue(User.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}