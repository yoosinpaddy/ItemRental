package yoosin.paddy.itemrental.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
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
import yoosin.paddy.itemrental.adapters.ItemsAdapter;
import yoosin.paddy.itemrental.adapters.MyItemsAdapter;
import yoosin.paddy.itemrental.models.Item;
import yoosin.paddy.itemrental.models.MyItem;
import yoosin.paddy.itemrental.sharedPreferenceManager.SharedPref;

public class MyRequest extends AppCompatActivity {
    RecyclerView recyclerView;
    MyItemsAdapter itemsAdapter;
    List<MyItem> itemsAround;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_request);
        FirebaseApp.initializeApp(this);
        firebaseAuth=FirebaseAuth.getInstance();
        itemsAround=new ArrayList<>();
        FirebaseUser user=firebaseAuth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("profiles/"+user.getUid()+"/requested_items");
        itemsAdapter=new MyItemsAdapter(MyRequest.this,itemsAround);
        getItemAround();
        recyclerView=findViewById(R.id.recycleItems);
        recyclerView.setLayoutManager(new GridLayoutManager(MyRequest.this,2));
        recyclerView.setAdapter(itemsAdapter);
    }

    private void getItemAround() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()){
                    for (DataSnapshot child:dataSnapshot.getChildren()) {
                        MyItem myItem= child.getValue(MyItem.class);
                        assert myItem != null;

                        if (!SharedPref.hasLocation(MyRequest.this)){
                            itemsAround.add(myItem);
                        }else {
                            if (myItem.getLatitude().contentEquals("")||myItem.getLongitude().contentEquals("")){
                                itemsAround.add(myItem);
                            }else {
//                                if (myItem.getDistance(MyRequest.this)<1&&myItem.getDistance(MyRequest.this)>-1){
                                    itemsAround.add(myItem);
//                                }
                            }
                        }

                    }
                    itemsAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(MyRequest.this, "You have no request", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
