package com.example.signupactivity;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class ListUsers extends BaseAdapter {

    private Context context;
    private ArrayList<String> usersID;
    private String name = "", email = "";
    TextView tname, temail;
    LayoutInflater inflater;

    public ListUsers(Context context, ArrayList<String> usersID)
    {
        this.context = context;
        this.usersID = usersID;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return usersID.size();
    }

    @Override
    public Object getItem(int position) {
        return usersID.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        getData(usersID.get(position));


        if(convertView == null)
        {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_user_item, null);

            tname = convertView.findViewById(R.id.list_user_item_name);
            temail = convertView.findViewById(R.id.list_user_item_email);
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                tname.setText(name);
                temail.setText(email);

            }
        }, 2000);




        return convertView;
    }

    private void getData(String id)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users/" + id);

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
    }
}
