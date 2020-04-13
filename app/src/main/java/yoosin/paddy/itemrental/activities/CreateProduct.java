package yoosin.paddy.itemrental.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yayandroid.locationmanager.LocationManager;
import com.yayandroid.locationmanager.configuration.DefaultProviderConfiguration;
import com.yayandroid.locationmanager.configuration.GooglePlayServicesConfiguration;
import com.yayandroid.locationmanager.configuration.LocationConfiguration;
import com.yayandroid.locationmanager.configuration.PermissionConfiguration;
import com.yayandroid.locationmanager.listener.LocationListener;

import java.util.UUID;

import yoosin.paddy.itemrental.R;
import yoosin.paddy.itemrental.models.Item;
import yoosin.paddy.itemrental.messaging.MainActivity;
import yoosin.paddy.itemrental.messaging.data.StaticConfig;

public class CreateProduct extends AppCompatActivity {
    private static final String TAG = "CreateProduct";
    ImageView imageView;
    String imagePath = "";
    TextView textName, textPrice, textDescription;
    String productId = String.valueOf(System.currentTimeMillis());
    Item item = new Item();
    String latitude = "";
    String longitude = "";
    String ownerid = "";
    String photoUrl = "";//Firebase
    FirebaseStorage storage;
    StorageReference storageReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private boolean firstTimeAccess;
    LocationManager awesomeLocationManager;
    boolean haspermision=false;
    Location location1;

    DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("items" );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product);
        textName = findViewById(R.id.txtName);
        textPrice = findViewById(R.id.txtPrice);
        textDescription = findViewById(R.id.txtDescription);
        imageView = findViewById(R.id.imageProduct);
        initFirebase();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // LocationManager MUST be initialized with Application context in order to prevent MemoryLeaks
        awesomeLocationManager = new LocationManager.Builder(getApplicationContext())
                .activity(CreateProduct.this) // Only required to ask permission and/or GoogleApi - SettingsApi
                .notify(locationListener)
                .configuration(awesomeConfiguration())
                .build();
        awesomeLocationManager.get();

        if (getIntent().getExtras()!=null){
            item= (Item)getIntent().getSerializableExtra("item");
            textName.setText(item.getName());
            textPrice.setText(item.getPrice());
            textDescription.setText(item.getDescription());
            reference=reference.child(item.getId());
            Glide.with(CreateProduct.this)
                    .load(item.getPhotoUrl())
                    .fallback(R.drawable.ef_image_placeholder)
                    .into(imageView);
        }
    }

    private LocationListener locationListener =new LocationListener() {
        @Override
        public void onProcessTypeChanged(int processType) {

        }

        @Override
        public void onLocationChanged(Location location) {
            location1=location;
            longitude=String.valueOf(location.getLongitude());
            latitude=String.valueOf(location.getLatitude());
            haspermision=true;
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

    public void submitProduct(View view) {
        if (imagePath.trim().contentEquals("")) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        } else if (textName.getText().toString().trim().contentEquals("")) {
            textName.setError("Please fill this");
            textName.setError("Please fill this");
        } else if (textPrice.getText().toString().trim().contentEquals("")) {
            textPrice.setError("Please fill this");
        } else if (textDescription.getText().toString().trim().contentEquals("")) {
            textDescription.setError("Please fill this");
        } else if (location1==null|| !haspermision) {
            Toast.makeText(this, "You must allow permission", Toast.LENGTH_SHORT).show();// LocationManager MUST be initialized with Application context in order to prevent MemoryLeaks
            awesomeLocationManager.get();
        } else {
            item.setId(productId);
            item.setDescription(textDescription.getText().toString());
            item.setLatitude(latitude);
            item.setLongitude(longitude);
            item.setOwnerId(ownerid);
            item.setPhotoUrl(photoUrl);
            item.setPrice(textPrice.getText().toString());
            item.setName(textName.getText().toString());
            uploadPhoto();
        }
    }

    public void uploadPhoto() {
        if (imagePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(Uri.parse(imagePath))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.setMessage("Confirming upload...");
                            Toast.makeText(CreateProduct.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onSuccess: " + taskSnapshot.getStorage().getDownloadUrl());
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    progressDialog.dismiss();
                                    uploadItem(String.valueOf(uri));
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(CreateProduct.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    public void uploadItem(String downloadUrl) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Saving...");
        progressDialog.show();
        item.setPhotoUrl(downloadUrl);
        item.setOwnerId(StaticConfig.UID);

        reference.child(productId);
        reference.setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                progressDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();

            }
        });

    }

    public void takePhoto(View view) {
        imageView = (ImageView) view;
        ImagePicker.create(this)
                .returnMode(ReturnMode.ALL) // set whether pick and / or camera action should return immediate result or not.
                .folderMode(false) // folder mode (false by default)
                .toolbarImageTitle("Tap to select") // image selection title
                .toolbarArrowColor(getResources().getColor(R.color.colorAccent)) // Toolbar 'up' arrow color
                .includeVideo(false) // Show video on image picker
                .single() // single mode
                .showCamera(true) // show camera or not (true by default)
                .enableLog(false) // disabling log
                .start(); // start image picker activity with request code

    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // Get a list of picked images
//            List<Image> images = ImagePicker.getImages(data);
            // or get a single image only
            Image image = ImagePicker.getFirstImageOrNull(data);
            imagePath = "file://"+image.getPath();
            Log.e(TAG, "onActivityResult: " + image.getPath());
            Glide.with(CreateProduct.this)
                    .load(image.getPath())
                    .fallback(R.drawable.ef_image_placeholder)
                    .into(imageView);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    StaticConfig.UID = user.getUid();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    if (firstTimeAccess) {
                        startActivity(new Intent(CreateProduct.this, MainActivity.class));
                        CreateProduct.this.finish();
                    }
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                firstTimeAccess = false;
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    public static LocationConfiguration awesomeConfiguration() {
        return new LocationConfiguration.Builder()
                .askForPermission(new PermissionConfiguration.Builder().rationaleMessage("We will need location to upload this product").build())
                .useGooglePlayServices(new GooglePlayServicesConfiguration.Builder().build())
                .useDefaultProviders(new DefaultProviderConfiguration.Builder().gpsMessage("We will need location to upload this product").build())
                .build();
    }
}
