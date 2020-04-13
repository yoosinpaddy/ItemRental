package yoosin.paddy.itemrental.fragments.ui.map;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import yoosin.paddy.itemrental.R;
import yoosin.paddy.itemrental.models.Item;
import yoosin.paddy.itemrental.sharedPreferenceManager.SharedPref;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private FirebaseUser user;
    private static final String TAG = "MapsActivity";
    RecyclerView recyclerView;
    List<Item> itemsAround;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    Marker[] marker = new Marker[0]; //change length of array according to you

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        itemsAround = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("items/");
        getItemAround();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        LatLng sydney = null;
        for (int i = 0; i < itemsAround.size(); i++) {
            Item myItem = itemsAround.get(i);
            if (!myItem.getLatitude().contentEquals("") && !myItem.getLongitude().contentEquals("")) {
                sydney = new LatLng(Double.parseDouble(myItem.getLatitude()), Double.parseDouble(myItem.getLongitude()));
                marker[i] = createMarker(myItem, mMap);
            }
            marker[i] = createMarker(myItem, mMap);
        }
        // Add a marker in Sydney and move the camera
        if (sydney != null)
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private Marker createMarker(Item myItem, GoogleMap mMap) {
        LatLng sydney;
        sydney = new LatLng(Double.parseDouble(myItem.getLatitude()), Double.parseDouble(myItem.getLongitude()));
        mMap.addMarker(new MarkerOptions().position(sydney).title(myItem.getName()));
        return mMap.addMarker(new MarkerOptions().position(sydney).title(myItem.getName()));
    }

    private void getItemAround() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Item myItem = child.getValue(Item.class);
                        assert myItem != null;
                        if (!SharedPref.hasLocation(MapsActivity.this)) {
//                            itemsAround.add(myItem);
                        } else {
                            if (myItem.getLatitude().contentEquals("") || myItem.getLongitude().contentEquals("")) {
                                Log.e(TAG, "onDataChange: something mission" );
//                                itemsAround.add(myItem);
                            } else {
                                if (myItem.getDistance(MapsActivity.this) < 1 && myItem.getDistance(MapsActivity.this) > -1) {
                                    Log.e(TAG, "onDataChange: we got something" );
                                    itemsAround.add(myItem);
                                }
                            }
                        }

                    }
                    marker = new Marker[itemsAround.size()];

                    LatLng sydney = null;
                    for (int i = 0; i < itemsAround.size(); i++) {
                        Item myItem = itemsAround.get(i);
                        if (!myItem.getLatitude().contentEquals("") && !myItem.getLongitude().contentEquals("")) {
                            Log.e(TAG, "onDataChange: adding");
                            sydney = new LatLng(Double.parseDouble(myItem.getLatitude()), Double.parseDouble(myItem.getLongitude()));
                            marker[i] = createMarker(myItem, mMap);
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                        } else {
                            Log.e(TAG, "onDataChange: not adding");

                        }
                        marker[i] = createMarker(myItem, mMap);
                    }
                    // Add a marker in Sydney and move the camera
                    if (sydney != null)
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

//        Intent intent = new Intent(context, ViewItemActivity.class);
//        intent.putExtra("item",item);
//        context.startActivity(intent);
        return false;
    }
}
