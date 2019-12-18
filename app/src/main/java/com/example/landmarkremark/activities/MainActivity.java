package com.example.landmarkremark.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.landmarkremark.fragments.HomeFragment;
import com.example.landmarkremark.helper.Constants;
import com.example.landmarkremark.helper.DatabaseHelper;
import com.example.landmarkremark.helper.LocationPermission;
import com.example.landmarkremark.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import static com.example.landmarkremark.helper.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.example.landmarkremark.helper.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;


public class MainActivity extends AppCompatActivity implements HomeFragment.LocationListener {

    private final static String TAG = "MainActivity";

    //Toolbar and Navigation drawer components
    private AppBarConfiguration mAppBarConfiguration;
    private NavigationView navigationView;
    NavController navController;

    //Navigation headerview components
    View headerView;
    TextView header_userName,
            header_Email;

    private boolean mLocationPermissionGranted = false;     //check is the user has granted the location permission

    //Firebase Auth
    private FirebaseAuth mAuth;                             //FirebaseAuth instance to use Firebase components
    private GoogleSignInOptions gso;                        //GoogleSignInOptions to enable user to login through Google account
    private GoogleSignInClient googleSignInClient;

    private boolean isUserLoggedIn = false;                 //boolean to check if a user is logged in or not when app starts
    FirebaseUser currentUser;

    //Firebase Database
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    LatLng currentLocation;
    String currentAddress = "";
    AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set up toolbar and navigation drawer
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_notes, R.id.nav_signOut)
                .setDrawerLayout(drawer)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //access header components from navigation drawer
        headerView = navigationView.getHeaderView(0);
        header_userName = headerView.findViewById(R.id.header_userName);
        header_Email = headerView.findViewById(R.id.header_userEmail);

        //add notes button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if the user is already logged in
                //if not build an alert dialog to prompt the user to log in

                buildAlertDialogLogin(isUserLoggedIn);
            }
        });


        mAuth = FirebaseAuth.getInstance();         //Initialize Firebase Auth
        firebaseDatabase = FirebaseDatabase.getInstance();      //Initialize Firebase database
        databaseReference = firebaseDatabase.getReference();

        //Configureing Google Sign in
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);


        LocationPermission locationPermission = new LocationPermission(MainActivity.this);
        //first check if the android phone has Google Play Services
        if (locationPermission.checkMapServices()) {
            //ask for location
            if (mLocationPermissionGranted) {
                Log.d(TAG, "Permission Granted.");
            } else {
                getLocationPermission();
            }
        }

    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            //means user is signed in so prompt a user to add notes on "+" button clicked
            isUserLoggedIn = true;

            //display the notes and sign out menus if user is not logged in
            navigationView.getMenu().findItem(R.id.nav_notes).setVisible(true).setChecked(false);
            navigationView.getMenu().findItem(R.id.nav_signOut).setVisible(true).setChecked(false);
            header_userName.setText(currentUser.getDisplayName());
            header_Email.setText(currentUser.getEmail());

        } else {
            //prompt user to sign in
            isUserLoggedIn = false;
            //hide the notes and sign out menus if user is not logged in
            navigationView.getMenu().findItem(R.id.nav_notes).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_signOut).setVisible(false);
        }

    }


    //getLocationPermission from user
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */

        //Check if the permission is granted previously
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    //request location permission callback function
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            }else {
                finish();
            }
        }
    }


    //This alertdialog will prompt the user for an action when "+" button is clicked
    public void buildAlertDialogLogin(boolean isUserLoggedIn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //if user has not logged in yet, prompt user to sign in first
        if (!isUserLoggedIn) {
            LayoutInflater inflater = LayoutInflater.from(this);
            View v = inflater.inflate(R.layout.alert_prompt_signin_view, null);

            builder.setView(v);

            SignInButton btnLogin = v.findViewById(R.id.btn_login);
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signIn();

                }
            });
        }
        //if user is already logged in prompt user to add notes
        else {
            LayoutInflater inflater = LayoutInflater.from(this);
            View v = inflater.inflate(R.layout.alert_prompt_addnotes_view, null);

            builder.setView(v);

            final EditText addNotes = v.findViewById(R.id.editText_notes);
            Button btn_addNote = v.findViewById(R.id.btn_addNote);

            btn_addNote.setEnabled(false);

            if (currentLocation != null) {
                btn_addNote.setEnabled(true);
                btn_addNote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (addNotes.getText().toString().isEmpty()) {
                            return;
                        }
                        //add it to the Notes
                        DatabaseHelper dbHelper = new DatabaseHelper(mAuth, databaseReference, dialog);
                        String enteredNote = addNotes.getText().toString();
                        dbHelper.addNoteToDB(enteredNote, currentLocation, currentAddress);


                    }
                });
            }
        }

        dialog = builder.create();
        dialog.show();

    }

    //sign in method
    public void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();   //sign in via google account using googleSignInClient
        startActivityForResult(signInIntent, Constants.RC_SIGN_IN);
        dialog.cancel();
    }


    //method to signout the user
    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        isUserLoggedIn = false;
        navigationView.getMenu().findItem(R.id.nav_signOut).setVisible(false);

        //display dummy data when not logged in
        header_userName.setText("Guest");
        header_Email.setText("GuestEmail@android.com");

        googleSignInClient.signOut();

        //Go to the MainActivity when logged out
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //if the request code matches the location permission request
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (mLocationPermissionGranted) {
                    Log.d(TAG, "Location Permission Granted");
                } else {
                    getLocationPermission();
                }
            }

            //if the request code matches google sign in request code
            case Constants.RC_SIGN_IN:
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account);
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(TAG, "Google sign in failed", e);
                }
        }

    }


    //log in to the app using google account
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            header_userName.setText(user.getDisplayName());
                            header_Email.setText(user.getEmail());
                            isUserLoggedIn = true;
                            navigationView.getMenu().findItem(R.id.nav_signOut).setVisible(true).setChecked(false);
                            navigationView.getMenu().findItem(R.id.nav_notes).setVisible(true).setChecked(false);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication Failed!", Toast.LENGTH_LONG).show();

                        }

                    }
                });
    }


    //override method from LocationListener interface to interact with HomeFragment fragment
    @Override
    public void onLocationObtained(LatLng location, String address) {
        currentLocation = location;
        currentAddress = address;
    }
}
