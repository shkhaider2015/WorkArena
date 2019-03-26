package com.example.signupactivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener , NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivity";

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    ImageView mProfilePicNav;
    TextView mFullNameNav, mEmailNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabase.setPersistenceEnabled(true);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerLayout = navigationView.inflateHeaderView(R.layout.navbar_header);

        mProfilePicNav = headerLayout.findViewById(R.id.nav_header_profile_pic);
        mEmailNav = headerLayout.findViewById(R.id.nav_header_profile_email);
        mFullNameNav = headerLayout.findViewById(R.id.nav_header_profile_name);



        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();




    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth != null)
        {
            new Async().execute();
        }
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

    private void getNavData()
    {
        String userUID = mAuth.getCurrentUser().getUid();

        if(userUID != null)
        {
            DatabaseReference users = mDatabase.getReference("Users/" + userUID);
            DatabaseReference profile = users.child("Profile");

            profile.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Log.d(TAG, "Datasnapshot : " +dataSnapshot);
                    Log.d(TAG, "onDataChange.getValue : " +dataSnapshot.getValue());
                    String name = dataSnapshot.child("full name").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();
                    Log.d(TAG, "onDataChange: name :" +name +" email : " + email);

                    Uri uri = Uri.parse(dataSnapshot.child("profilepicurl").getValue().toString());
                    mFullNameNav.setText(name);
                    mEmailNav.setText(email);
                    Picasso.get()
                            .load(uri)
                            .placeholder(R.drawable.person_black_18dp)
                            .into(mProfilePicNav);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private class Async extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {
            getNavData();
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
