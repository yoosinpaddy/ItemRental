package yoosin.paddy.itemrental.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yayandroid.locationmanager.LocationManager;
import com.yayandroid.locationmanager.listener.LocationListener;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import yoosin.paddy.itemrental.R;
import yoosin.paddy.itemrental.fragments.ui.map.MapsActivity;
import yoosin.paddy.itemrental.sharedPreferenceManager.SharedPref;
import yoosin.paddy.itemrental.messaging.data.StaticConfig;
import yoosin.paddy.itemrental.messaging.ui.LoginActivity;

import static yoosin.paddy.itemrental.activities.CreateProduct.awesomeConfiguration;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseUser user;
    private static final String TAG = "HomeActivity";
    String latitude = "";
    String longitude = "";
    LocationManager awesomeLocationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initFirebase();
        initLocation();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add Item?", Snackbar.LENGTH_LONG)
                        .setAction("yes", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(HomeActivity.this, CreateProduct.class));
                            }
                        }).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_my_items, R.id.nav_search)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.action_settings){
            startActivity(new Intent(HomeActivity.this, yoosin.paddy.itemrental.messaging.MainActivity.class));
        }
        if (item.getItemId()==R.id.action_map){
            startActivity(new Intent(HomeActivity.this, MapsActivity.class));
        }
        if (item.getItemId()==R.id.action_myrequest){
            startActivity(new Intent(HomeActivity.this, MyRequest.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    public void initLocation(){
        // LocationManager MUST be initialized with Application context in order to prevent MemoryLeaks
        if (!SharedPref.hasLocation(HomeActivity.this)){
            awesomeLocationManager = new LocationManager.Builder(getApplicationContext())
                    .activity(HomeActivity.this) // Only required to ask permission and/or GoogleApi - SettingsApi
                    .notify(locationListener)
                    .configuration(awesomeConfiguration())
                    .build();
            awesomeLocationManager.get();
        }
    }
    private LocationListener locationListener =new LocationListener() {
        @Override
        public void onProcessTypeChanged(int processType) {

        }

        @Override
        public void onLocationChanged(Location location) {
            SharedPref.setMyLocation(HomeActivity.this,String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()));
        }

        @Override
        public void onLocationFailed(int type) {

        }

        @Override
        public void onPermissionGranted(boolean alreadyHadPermission) {
            Log.e(TAG, "onPermissionGranted: "+alreadyHadPermission );
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public void initFirebase(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        // User is signed in
        //                    startActivity(new Intent(MainActivity.this, yoosin.paddy.itemrental.activities.HomeActivity.class));
        //                    initTab();
        // User is signed in
        // ...
        FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    StaticConfig.UID = user.getUid();
                    Log.e(TAG, "onAuthStateChanged: " + StaticConfig.UID);

                    HomeActivity.this.finish();
                    // User is signed in
//                    startActivity(new Intent(MainActivity.this, yoosin.paddy.itemrental.activities.HomeActivity.class));
//                    initTab();
                } else {
                    HomeActivity.this.finish();
                    // User is signed in
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

}
