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
import android.support.v7.app.ActionBar;
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

public class HomeActivity extends AppCompatActivity implements View.OnClickListener , NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener {

    private static final String TAG = "HomeActivity";

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    ImageView mProfilePicNav;
    TextView mFullNameNav, mEmailNav, welcome;
    ProgressBar mProgressbarNav, hProgressbar;
    Spinner mSpinner;
    String name, email;
    Uri profileurl;
    Boolean isProfilePic = false;
    String IsProfilePic = "fasle";
    ListView listView;
    ListUsers listUsers;
    Uri tempURI;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Work Arena");
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Work Arena");

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }


        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerLayout = navigationView.inflateHeaderView(R.layout.navbar_header);

        mProfilePicNav = headerLayout.findViewById(R.id.nav_header_profile_pic);
        mEmailNav = headerLayout.findViewById(R.id.nav_header_profile_email);
        mFullNameNav = headerLayout.findViewById(R.id.nav_header_profile_name);
        mProgressbarNav = headerLayout.findViewById(R.id.nav_header_progressbar);
        hProgressbar = findViewById(R.id.home_activity_progressbar);
        mSpinner = findViewById(R.id.spinner);
        welcome = findViewById(R.id.welcome);



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
                    welcome.setVisibility(View.GONE);
                    hProgressbar.setVisibility(View.VISIBLE);
                    Toast.makeText(HomeActivity.this, parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();

                   listUsers = new ListUsers(HomeActivity.this, usersData(data));

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hProgressbar.setVisibility(View.GONE);

                            listView.setAdapter(listUsers);

                        }
                    }, 1500);


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Toast.makeText(HomeActivity.this, "Nothing Selected", Toast.LENGTH_SHORT).show();

            }
        });

        listView.setOnItemClickListener(this);

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
        }, 1500);


        /*
        ListView horizontal_list = findViewById(R.id.horizontal_list);
        ListProfession listProfession = new ListProfession(this);
        horizontal_list.setAdapter(listProfession);
        */


    }

    @Override
    protected void onStart() {
        super.onStart();


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
                drawerLayout.closeDrawers();
                FragmentProfile profile = new FragmentProfile();
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.home_container, profile);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.nav_portfolio:
                drawerLayout.closeDrawers();
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
                    IsProfilePic = String.valueOf(dataSnapshot.child("isProfilePic").getValue());
                    isProfilePic = Boolean.valueOf(String.valueOf(dataSnapshot.child("isProfilePic").getValue()));
                    Log.d(TAG, "Boolean : isProfilePic " +isProfilePic);
                    Log.d(TAG, "onDataChange: IsProfilePic " + IsProfilePic);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {
                    Log.e(TAG, "onCancelled: %s", databaseError.toException());

                }
            });

            StorageReference reference1 = FirebaseStorage
                    .getInstance()
                    .getReference("profilepics")
                    .child(mAuth.getCurrentUser().getUid())
                    .child("profilepicture.jpg");

            if(!IsProfilePic.equalsIgnoreCase("false"))
            {
                Log.d(TAG, "getNavDataFromUsers: Prepare");
                reference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri)
                    {
                        Log.d(TAG, "onSuccess: Uri : " +uri);
                        profileurl = uri;
                        Log.d(TAG, "onSuccess: URL " + uri);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Log.e(TAG, "onFailure: Profile Pic failure %s", e);

                    }
                });
            }







            reference.keepSynced(true);


        }catch (NullPointerException e)
        {
            Log.e(TAG, "getNavDataFromUsers: $s", e);
        }catch (Exception e)
        {
            Log.e(TAG, "getNavDataFromUsers: $s", e);
        }
    }

    private ArrayList<Model_ListUserItem> usersData(final String data)
    {
        final ArrayList<Model_ListUserItem> users = new ArrayList<>();

        DatabaseReference usersRef = mDatabase.getReference("Users");

        ValueEventListener valueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    final Model_ListUserItem model = new Model_ListUserItem();

                    String profession = String.valueOf(dataSnapshot1.child("Portfolio").child("profession").getValue()).trim();
                    Log.d(TAG, "onDataChange: Profession : " + profession);

                    if(data.equals(profession))
                    {
                        Log.d(TAG, "Equals: this user id = " + dataSnapshot1.getKey());
                        Log.d(TAG, "Equals: compare : " + data + " = " + profession);

                        String user_id = String.valueOf(dataSnapshot1.getKey());

                        if(user_id.equals(String.valueOf(mAuth.getCurrentUser().getUid())))
                        {
                            continue;
                        }

                        String name = String.valueOf(dataSnapshot1.child("full name").getValue());
                        String email = String.valueOf(dataSnapshot1.child("email").getValue());

                        StorageReference reference = FirebaseStorage.getInstance().getReference("profilepics/" + user_id + "/profilepicture.jpg");
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                model.setUri(uri);
                            }
                        });
                        Log.d(TAG, "Equals: user_id " + user_id);

                        //users.add(user_id);

                        model.setName(name);
                        model.setEmail(email);
                        model.setuID(user_id);

                        users.add(model);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Model_ListUserItem model = (Model_ListUserItem) parent.getAdapter().getItem(position);

        Log.d(TAG, "onItemClick: ListView Item id is : " + model.getuID());

        Bundle bundle = new Bundle();
        bundle.putString("userID", model.getuID());
        FragmentUsers fragmentUsers = new FragmentUsers();
        fragmentUsers.setArguments(bundle);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.addToBackStack(null);
        transaction.add(R.id.home_container, fragmentUsers);
        transaction.commit();

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
