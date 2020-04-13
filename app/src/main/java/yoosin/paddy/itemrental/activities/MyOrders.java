package yoosin.paddy.itemrental.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import yoosin.paddy.itemrental.adapters.MyItemsAdapter;
import yoosin.paddy.itemrental.adapters.MyOrderItemsAdapter;
import yoosin.paddy.itemrental.models.MyItem;
import yoosin.paddy.itemrental.models.MyOrder;
import yoosin.paddy.itemrental.sharedPreferenceManager.SharedPref;

public class MyOrders extends AppCompatActivity {
    RecyclerView recyclerView;
    MyOrderItemsAdapter itemsAdapter;
    List<MyOrder> itemsAround;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        FirebaseApp.initializeApp(this);
        itemsAround=new ArrayList<>();
        FirebaseUser user=firebaseAuth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("profiles/"+user.getUid()+"/order_items");
        itemsAdapter=new MyOrderItemsAdapter(MyOrders.this,itemsAround);
        getItemAround();
        recyclerView=findViewById(R.id.recycleItems);
        recyclerView.setLayoutManager(new GridLayoutManager(MyOrders.this,2));
        recyclerView.setAdapter(itemsAdapter);
    }

    private void getItemAround() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()){
                    for (DataSnapshot child:dataSnapshot.getChildren()) {
                        MyOrder myItem= child.getValue(MyOrder.class);
                        assert myItem != null;

                        if (!SharedPref.hasLocation(MyOrders.this)){
                            itemsAround.add(myItem);
                        }else {
                            if (myItem.getLatitude().contentEquals("")||myItem.getLongitude().contentEquals("")){
                                itemsAround.add(myItem);
                            }else {
                                if (myItem.getDistance(MyOrders.this)<1&&myItem.getDistance(MyOrders.this)<-1){
                                    itemsAround.add(myItem);
                                }
                            }
                        }

                    }
                    itemsAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(MyOrders.this, "You have no request", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
