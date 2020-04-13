package yoosin.paddy.itemrental.fragments.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

public class SearchActivity extends Fragment {

    View root;
    RecyclerView recyclerView;
    ItemsAdapter itemsAdapter;
    List<Item> itemsAround;
    List<Item> filteredItems=new ArrayList<>();
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    TextView textView;
    boolean isSearching=false;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_search, container, false);
        itemsAround=new ArrayList<>();
        FirebaseApp.initializeApp(getActivity());
        databaseReference= FirebaseDatabase.getInstance().getReference().child("items");
        itemsAdapter=new ItemsAdapter(getActivity(),itemsAround);
        getItemAround();
        recyclerView=root.findViewById(R.id.recycleItems);
        textView=root.findViewById(R.id.search);
        textView.addTextChangedListener(searchTextWatcher);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recyclerView.setAdapter(itemsAdapter);
        return root;
    }
    TextWatcher searchTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!isSearching){
                isSearching=true;
                searchNow(s.toString());
            }
        }
    };

    private void searchNow(String s) {
        if (itemsAround.size()>0){
            filteredItems=new ArrayList<>(0);
            for (Item item:itemsAround) {
                if (!item.getName().toLowerCase().contains(s.toLowerCase())&&!item.getPrice().toLowerCase().contains(s.toLowerCase())){
                    filteredItems.add(item);
                }
            }
            itemsAround.removeAll(filteredItems);
            itemsAdapter.notifyDataSetChanged();
            isSearching=false;

        }else {
            isSearching=false;
        }
    }

    private void getItemAround() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()){
                    for (DataSnapshot child:dataSnapshot.getChildren()) {
                        Item myItem= child.getValue(Item.class);
                        assert myItem != null;
                        itemsAround.add(myItem);

                    }
                    filteredItems=itemsAround;
                    itemsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
