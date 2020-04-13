package yoosin.paddy.itemrental.fragments.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import yoosin.paddy.itemrental.R;
import yoosin.paddy.itemrental.adapters.ItemsAdapter;
import yoosin.paddy.itemrental.models.Item;
import yoosin.paddy.itemrental.sharedPreferenceManager.SharedPref;

public class HomeFragment extends Fragment {

    View root;
    RecyclerView recyclerView;
    ItemsAdapter itemsAdapter;
    List<Item> itemsAround;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        itemsAround=new ArrayList<>();
        FirebaseApp.initializeApp(getActivity());
        databaseReference= FirebaseDatabase.getInstance().getReference().child("items");
        itemsAdapter=new ItemsAdapter(getActivity(),itemsAround);
        getItemAround();
        recyclerView=root.findViewById(R.id.recycleItems);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recyclerView.setAdapter(itemsAdapter);
        return root;
    }

    private void getItemAround() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()){
                    for (DataSnapshot child:dataSnapshot.getChildren()) {
                        Item myItem= child.getValue(Item.class);
                        assert myItem != null;

                        if (!SharedPref.hasLocation(getActivity())){
                            itemsAround.add(myItem);
                        }else {
                            if (myItem.getLatitude().contentEquals("")||myItem.getLongitude().contentEquals("")){
                                itemsAround.add(myItem);
                            }else {
                                if (myItem.getDistance(getActivity())<1&&myItem.getDistance(getActivity())<-1){
                                    itemsAround.add(myItem);
                                }
                            }
                        }

                    }
                    itemsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
