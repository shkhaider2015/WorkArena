package com.example.signupactivity;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class ListUsers extends BaseAdapter {

    private Context context;
    protected ArrayList<Model_ListUserItem> users;
    private String name = "", email = "";
    TextView tname, temail, tdistance;
    ImageView profilePicture;
    Uri uri;
    LayoutInflater inflater;

    public ListUsers(Context context, ArrayList<Model_ListUserItem> usersID)
    {
        this.context = context;
        this.users = usersID;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Model_ListUserItem model =users.get(position);

        if(convertView == null)
        {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_user_item, null);
        }

        tname = convertView.findViewById(R.id.list_user_item_name);
        temail = convertView.findViewById(R.id.list_user_item_email);
        profilePicture = convertView.findViewById(R.id.list_user_image);
        tdistance = convertView.findViewById(R.id.list_user_item_distance);

        DecimalFormat decimalFormat = new DecimalFormat("0.##");



                tname.setText(model.getName());
                temail.setText(model.getEmail());
                tdistance.setText(decimalFormat.format(model.getDistance()));
                Picasso.get()
                        .load(model.getUri())
                        .placeholder(R.drawable.person_black_18dp)
                        .into(profilePicture);




        return convertView;
    }

    private void getData(String id)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users/" + id);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profilepics/" + id + "/profilepicture.jpg");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Log.d(TAG, "onDataChange: " + dataSnapshot);

                name = String.valueOf(dataSnapshot.child("full name").getValue());
                email = String.valueOf(dataSnapshot.child("email").getValue());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Log.e(TAG, "onCancelled: %s", databaseError.toException());

            }
        });

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                if(uri != null)
                {
                    ListUsers.this.uri = uri;
                }

            }
        });


    }
}
