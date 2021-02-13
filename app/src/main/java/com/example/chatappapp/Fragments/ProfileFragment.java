package com.example.chatappapp.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.chatappapp.Model.User;
import com.example.chatappapp.R;
import com.example.chatappapp.StartActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private CircleImageView image_profile;
    private Button change_profile_image,logout;
    private TextView username;
    private User user;
    private DatabaseReference reference;
    private FirebaseUser fuser;
    private final int PICK_IMAGE_REQUEST = 22;
    private Uri filePath;
    private ImageView image;
    private StorageReference mStorageRef;
    private DatabaseReference refernce;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        image_profile = view.findViewById(R.id.profile_image_my_profile);
        logout = view.findViewById(R.id.logout);
        username = view.findViewById(R.id.user_item_username);
        change_profile_image = view.findViewById(R.id.change_profile_image);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        fuser = FirebaseAuth.getInstance().getCurrentUser();


        change_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("pttt", "tal");
                askCameraPermission();
            }
        });

        if (fuser != null) {
            reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    user = snapshot.getValue(User.class);
                    if(user!=null)
                    username.setText("HI "+user.getUsername().toUpperCase());

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("pttt",error.getMessage());
                }
            });
        }
        if(fuser!=null) {
            mStorageRef.child(fuser.getUid()).getDownloadUrl().
                    addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).rotate(270).into(image_profile);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    image_profile.setBackgroundResource(R.mipmap.ic_launcher_round);

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                }
            });
        }
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
        return view;
    }


    private  void signOut(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getContext(), StartActivity.class));
        getActivity().finish();
    }
    private void askCameraPermission() {
        int MY_PERMISSIONS_REQUEST_CAMERA = 0;
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
                SelectImage(image_profile);
                Toast.makeText(getContext(), "camera permission granted", Toast.LENGTH_LONG).show();

            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
                Toast.makeText(getContext(), "camera permission denied", Toast.LENGTH_LONG).show();
            }
        } else
            SelectImage(image_profile);

    }

    private void SelectImage(CircleImageView imageToChange) {
        image = imageToChange;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == -1
                && data != null
                && data.getData() != null) {
            filePath = data.getData();
            Picasso.get().load(filePath).rotate(270).into(image);
             refernce = FirebaseDatabase.getInstance().getReference("Users").child(user.getId());
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("id", user.getId());
                hashMap.put("username", user.getUsername());
                hashMap.put("imageURL", filePath.toString());

                refernce.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                uploadImage(filePath);

        }
    }

    private void uploadImage(Uri path) {
        StorageReference ref = mStorageRef.child(fuser.getUid());
        ref.putFile(path).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                Toast.makeText(getContext(), "uploaded ", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

    }


}