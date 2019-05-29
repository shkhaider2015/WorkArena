package com.example.signupactivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
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

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
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
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener , NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener {

        //For Geolocation
        private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
        private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
        private static final int REQUEST_CHECK_SETTINGS = 100;

        private String mLastUpdateTime;
        // bunch of location related apis
        private FusedLocationProviderClient mFusedLocationClient;
        private SettingsClient mSettingsClient;
        private LocationRequest mLocationRequest;
        private LocationSettingsRequest mLocationSettingsRequest;
        private LocationCallback mLocationCallback;
        private Location mCurrentLocation;

        // boolean flag to toggle the ui
        private Boolean mRequestingLocationUpdates;

        private static final String TAG = "HomeActivity";
        public static final String CHANNEL_ID = "simplified_coding";

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

            init();
            restoreValuesFromBundle(savedInstanceState);
            startLocationButtonClick();

            Handler handler1 = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run()
                {
                    stopLocationUpdates();
                }
            }, 50000);

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

                            double latitude = 0, longitude = 0;
                            if(dataSnapshot1.child("Location").child("latitude").exists() && dataSnapshot1.child("Location").child("longitude").exists() )
                            {
                                latitude = (double) dataSnapshot1.child("Location").child("latitude").getValue();
                                longitude = (double) dataSnapshot1.child("Location").child("longitude").getValue();

                                Log.d(TAG, "onDataChange: latitude is : " +latitude + "Longitude is : " + longitude);
                            }


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
                            if(latitude != 0 && longitude != 0 && mCurrentLocation != null)
                            {
                                Log.d(TAG, "onDataChange: Double is not equal to 0");
                                model.setLocation(latitude, longitude, mCurrentLocation);
                            }
                            else
                            {
                                Location location = new Location("temp");
                                location.setLatitude(24.886832);
                                location.setLongitude(67.035789);
                                model.setLocation(24.929587,  67.019509, location);
                            }


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



        //-----------------------------
        // This code is for geolocation
        //-----------------------------
        //-----------------------------


        private void init() {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mSettingsClient = LocationServices.getSettingsClient(this);

            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    // location is received
                    mCurrentLocation = locationResult.getLastLocation();
                    mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                    uploadLocation();
                }
            };

            mRequestingLocationUpdates = false;

            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
            mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
            mLocationSettingsRequest = builder.build();
        }
        /**
         * Restoring values from saved instance state
         */
        private void restoreValuesFromBundle(Bundle savedInstanceState) {
            if (savedInstanceState != null)
            {
                if (savedInstanceState.containsKey("is_requesting_updates")) {
                    mRequestingLocationUpdates = savedInstanceState.getBoolean("is_requesting_updates");
                }

                if (savedInstanceState.containsKey("last_known_location")) {
                    mCurrentLocation = savedInstanceState.getParcelable("last_known_location");
                }

                if (savedInstanceState.containsKey("last_updated_on")) {
                    mLastUpdateTime = savedInstanceState.getString("last_updated_on");
                }
            }

            uploadLocation();
           // updateLocationUI();
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putBoolean("is_requesting_updates", mRequestingLocationUpdates);
            outState.putParcelable("last_known_location", mCurrentLocation);
            outState.putString("last_updated_on", mLastUpdateTime);

        }
    /**
     * Starting location updates
     * Check whether location settings are satisfied and then
     * location updates will be requested
     */
    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        uploadLocation();
                        //updateLocationUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(HomeActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

                        uploadLocation();
                       // updateLocationUI();
                    }
                });
    }
    public void startLocationButtonClick() {
        // Requesting ACCESS_FINE_LOCATION using Dexter library
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mRequestingLocationUpdates = true;
                        startLocationUpdates();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // open device settings when the permission is
                            // denied permanently
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }
    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    public void stopLocationUpdates() {
        mRequestingLocationUpdates = false;
        // Removing location updates
        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Location updates stopped!", Toast.LENGTH_SHORT).show();
                        //toggleButtons();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.e(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e(TAG, "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        break;
                }
                break;
        }
    }
    @Override
    public void onResume() {
        super.onResume();

        // Resuming location updates depending on button state and
        // allowed permissions
        if (mRequestingLocationUpdates && checkPermissions()) {
            startLocationUpdates();
        }

        uploadLocation();
       // updateLocationUI();
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mRequestingLocationUpdates) {
            // pausing location updates
            stopLocationUpdates();
        }
    }


    private void uploadLocation()
    {
        if(mCurrentLocation != null)
        {
            DatabaseReference location = mDatabase.getReference("Users")
                    .child(mAuth.getCurrentUser().getUid())
                    .child("Location");
            location.child("latitude").setValue(mCurrentLocation.getLatitude());
            location.child("longitude").setValue(mCurrentLocation.getLongitude());

            Toast.makeText(this, "Location uploaded", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "uploadLocation: mCurrent location is ready !!");
        }
        else
        {
            Log.d(TAG, "uploadLocation: mCurrentLocation is null cant uploaded");
        }

    }





    //-----------------------------
    // This code is for geolocation
    //-----------------------------
    // -----------------------------

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
