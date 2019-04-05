package com.example.signupactivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener , NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivity";

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    ImageView mProfilePicNav;
    TextView mFullNameNav, mEmailNav;
    ProgressBar mProgressbarNav, hProgressbar;
    Spinner mSpinner;
    String name, email, profileurl;
    boolean isProfilePic = false;
    ListView listView;
    ArrayAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }


        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerLayout = navigationView.inflateHeaderView(R.layout.navbar_header);

        mProfilePicNav = headerLayout.findViewById(R.id.nav_header_profile_pic);
        mEmailNav = headerLayout.findViewById(R.id.nav_header_profile_email);
        mFullNameNav = headerLayout.findViewById(R.id.nav_header_profile_name);
        mProgressbarNav = headerLayout.findViewById(R.id.nav_header_progressbar);
        hProgressbar = findViewById(R.id.home_activity_progressbar);
        mSpinner = findViewById(R.id.spinner);



        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.profession_array, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(arrayAdapter);
        listView = findViewById(R.id.users_list);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String data = String.valueOf(parent.getItemAtPosition(position)).trim();
                Log.d(TAG, "onItemSelected: " + data);
                Log.d(TAG, "onItemSelected: Item Id -->> " + id);
                if(id == 0)
                {
                    Toast.makeText(HomeActivity.this, "This is exclude", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    hProgressbar.setVisibility(View.VISIBLE);
                    Toast.makeText(HomeActivity.this, parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();

                    final ArrayAdapter listAdapter = new ArrayAdapter<String>(HomeActivity.this, R.layout.list_item, usersData(data));

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hProgressbar.setVisibility(View.GONE);

                            listView.setAdapter(listAdapter);

                        }
                    }, 3000);


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Toast.makeText(HomeActivity.this, "Nothing Selected", Toast.LENGTH_SHORT).show();

            }
        });




    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth != null)
        {
            mProgressbarNav.setVisibility(View.VISIBLE);
            new Async().execute();
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                updateUINavHeader();
            }
        }, 6000);

    }

    @Override
    public void onClick(View v)
    {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId())
        {
            case R.id.menu_edit_profile:
                startActivity(new Intent(HomeActivity.this, EditProfileActivity.class));
                break;
            case R.id.menu_edit_portfolio:
                startActivity(new Intent(HomeActivity.this, EditPortfolioActivity.class));
                break;
            case R.id.menu_logout:
                mDatabase = null;
                mAuth.signOut();

                startActivity(new Intent(HomeActivity.this, MainActivity.class));
                finish();

                break;
        }


        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId())
        {
            case R.id.nav_profile:
                FragmentProfile profile = new FragmentProfile();
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.home_container, profile);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.nav_portfolio:
                FragmentManager manager1 = getSupportFragmentManager();
                FragmentTransaction transaction1 = manager1.beginTransaction();
                transaction1.replace(R.id.home_container, new FragmentPortfolio());
                transaction1.addToBackStack(null);
                transaction1.commit();
                break;
        }

        return true;
    }

    private void updateUINavHeader()
    {
        mFullNameNav.setText(name);
        mEmailNav.setText(email);

        if(isProfilePic)
        {
            Log.d(TAG, "updateUINavHeader: URL is not NULL !!!!");
            Picasso.get()
                    .load(profileurl)
                    .placeholder(R.drawable.person_black_18dp)
                    .into(mProfilePicNav);
        }
        else
        {
            Log.d(TAG, "updateUINavHeader: URL is NULL");
            mProfilePicNav.setImageResource(R.drawable.person_black_18dp);
        }


        mProgressbarNav.setVisibility(View.GONE);
    }

    private void getNavDataFromUsers()
    {

        try
        {
            DatabaseReference reference = mDatabase.getReference("Users")
                    .child(String.valueOf(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()));

            reference.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    Log.d(TAG, "onDataChange: users reference " + dataSnapshot);

                    name = String.valueOf(dataSnapshot.child("full name").getValue());
                    email = String.valueOf(dataSnapshot.child("email").getValue());
                    isProfilePic = Boolean.valueOf(String.valueOf(dataSnapshot.child("isProfilePic").getValue()));

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {
                    Log.e(TAG, "onCancelled: %s", databaseError.toException());

                }
            });

            reference.keepSynced(true);

            StorageReference reference1 = FirebaseStorage
                    .getInstance()
                    .getReference("profilepics/" + mAuth.getCurrentUser().getUid() + "/profilepicture.jpg");

            if(isProfilePic)
            {
                reference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri)
                    {
                        Log.d(TAG, "onSuccess: Uri : " +uri);
                        if(uri != null)
                        {
                            profileurl = String.valueOf(uri);
                        }
                        Log.d(TAG, "Uri is null : ----->>");

                    }
                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {
                                Log.e(TAG, "onFailure: Profile Pic failure %s", e);

                            }
                        });


            }


        }catch (NullPointerException e)
        {
            Log.e(TAG, "getNavDataFromUsers: $s", e);
        }catch (Exception e)
        {
            Log.e(TAG, "getNavDataFromUsers: $s", e);
        }
    }

    private ArrayList<String> usersData(final String data)
    {
        final ArrayList<String> users = new ArrayList<>();

        DatabaseReference usersRef = mDatabase.getReference("Users");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    Log.d(TAG, "onDataChange: name = " + name);
                    Log.d(TAG, "onDataChange: datasnapshot --> " + dataSnapshot1);

                    String profession = String.valueOf(dataSnapshot1.child("Portfolio").child("profession").getValue()).trim();
                    Log.d(TAG, "onDataChange: profession : " + profession);
                    if(data.equals(profession))
                    {
                        Log.d(TAG, "onDataChange: compare : " + data);
                        String name = String.valueOf(dataSnapshot1.child("full name").getValue());
                        users.add(name);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        };
        usersRef.addValueEventListener(valueEventListener);



        return users;

    }

    private class Async extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {

                getNavDataFromUsers();

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
