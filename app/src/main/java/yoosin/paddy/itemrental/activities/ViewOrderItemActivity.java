package yoosin.paddy.itemrental.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.util.ArrayList;
import java.util.HashMap;

import yoosin.paddy.itemrental.R;
import yoosin.paddy.itemrental.messaging.data.FriendDB;
import yoosin.paddy.itemrental.messaging.data.StaticConfig;
import yoosin.paddy.itemrental.messaging.model.Friend;
import yoosin.paddy.itemrental.messaging.ui.ChatActivity;
import yoosin.paddy.itemrental.models.Item;
import yoosin.paddy.itemrental.models.MyItem;
import yoosin.paddy.itemrental.models.MyOrder;

public class ViewOrderItemActivity extends AppCompatActivity {
    ImageView imageView,itemVote;
    FloatingActionButton message, edit;
    TextView title, amount, description, itemDistance;
    MyOrder item;
    Item item2;
    String idRoom;
    Spinner itemStatusSpinner;
    Context context;
    boolean hasUpVoted=false;
    boolean canVote=true;
    private static final String TAG = "ViewItemActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_order);
        FirebaseApp.initializeApp(ViewOrderItemActivity.this);

        context = ViewOrderItemActivity.this;
        imageView = findViewById(R.id.productImage);
        itemVote = findViewById(R.id.itemVote);
        message = findViewById(R.id.message);
        edit = findViewById(R.id.edit);
        title = findViewById(R.id.itemName);
        amount = findViewById(R.id.itemPrice);
        description = findViewById(R.id.ItemDescription);
        itemDistance = findViewById(R.id.itemDistance);
        itemStatusSpinner = findViewById(R.id.itemStatusSpinner);
        setUpSpinner();
        if (getIntent().getExtras() != null) {
            item = (MyOrder) getIntent().getSerializableExtra("item");
            if (item.getOwnerId().contentEquals(FirebaseAuth.getInstance().getUid())) {
                edit.setVisibility(View.VISIBLE);
                idRoom = item.getOwnerId().compareTo(FirebaseAuth.getInstance().getCurrentUser().getUid()) > 0 ? (StaticConfig.UID + item.getOwnerId()).hashCode() + "" : "" + (item.getOwnerId() + StaticConfig.UID).hashCode();
            }
            title.setText(item.getName());
            amount.setText(item.getPrice());
            description.setText(item.getDescription());

            if (item.getLatitude().contentEquals("") || item.getLongitude().contentEquals("")) {
                itemDistance.setText(R.string.unknown_dis);
            } else {
                String dis= String.valueOf(item.getDistance(ViewOrderItemActivity.this))+" km";
                if (item.getDistance(ViewOrderItemActivity.this) < 1 && item.getDistance(ViewOrderItemActivity.this) < -1) {
                    itemDistance.setText(dis);
                }else {
                    itemDistance.setText(dis);
                }
            }
            Glide.with(ViewOrderItemActivity.this)
                    .load(item.getPhotoUrl())
                    .fallback(R.drawable.ef_image_placeholder)
                    .into(imageView);
        }
        itemVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView=(ImageView)v;
                if (canVote){
                    canVote=false;
                    if (hasUpVoted){
                        hasUpVoted=false;
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_thumb_up_dull_24dp));
                        uploadMyItem(true);
                    }else {
                        hasUpVoted=true;
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_thumb_up_black_24dp));
                        uploadMyItem(false);
                    }
                }
            }
        });
    }

    private void setUpSpinner() {
        itemStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        updatestatusItem("pending");
                        break;
                    case 1:
                        updatestatusItem("lended");
                        break;
                    case 2:
                        updatestatusItem("returned");
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void updatestatusItem(String status) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Saving...");
        progressDialog.show();
        MyOrder order=item;
        order.setStatus(status);

        DatabaseReference
                reference= FirebaseDatabase.getInstance().getReference().child("profiles/"+order.getSecondPartyId()+"/requested_items/"+item.getId());
        DatabaseReference
                reference2= FirebaseDatabase.getInstance().getReference().child("profiles/"+FirebaseAuth.getInstance().getUid()+"/order_items/"+item.getId());
        reference2.setValue(order);
        reference.setValue(order).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();

            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                progressDialog.dismiss();

            }
        });

    }

    public void uploadItem(boolean up) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Saving...");
        progressDialog.show();
        item.setVote(up?1:0);

        DatabaseReference
                reference= FirebaseDatabase.getInstance().getReference().child("items/"+item.getId());
        DatabaseReference
                reference2= FirebaseDatabase.getInstance().getReference().child("items/"+item.getId());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                item2=dataSnapshot.getValue(Item.class);
                int a= item2.getVotes();
                if (up){
                    a++;
                }else {
                    a--;
                }
                item2.setVotes(a);
                reference2.setValue(item2);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });

    }
    public void uploadMyItem(boolean up) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Saving...");
        progressDialog.show();
        item.setVote(up?1:0);

        DatabaseReference
                reference= FirebaseDatabase.getInstance().getReference().child("profiles/"+StaticConfig.UID+"/requested_items");
        reference.setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                uploadItem(up);
                canVote=true;
                progressDialog.dismiss();
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                canVote=true;
                progressDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                canVote=true;
                progressDialog.dismiss();

            }
        });

    }
    public void sendMessage(View view) {
        StaticConfig.UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Intent intent = new Intent(ViewOrderItemActivity.this, ChatActivity.class);
        intent.putExtra(StaticConfig.INTENT_KEY_CHAT_FRIEND, " ");
        ArrayList<CharSequence> idFriend = new ArrayList<CharSequence>();
        idFriend.add(item.getOwnerId());
        intent.putCharSequenceArrayListExtra(StaticConfig.INTENT_KEY_CHAT_ID, idFriend);
        intent.putExtra(StaticConfig.INTENT_KEY_CHAT_ROOM_ID, idRoom);
        ChatActivity.bitmapAvataFriend = new HashMap<>();
//                        if (!avata.equals(StaticConfig.STR_DEFAULT_BASE64)) {
//                            byte[] decodedString = Base64.decode(avata, Base64.DEFAULT);
//                            ChatActivity.bitmapAvataFriend.put(id, BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
//                        } else {
        ChatActivity.bitmapAvataFriend.put(item.getOwnerId(), "");
//                        }

//            mapMark.put(id, null);
        checkBeforAddFriend(item.getOwnerId());
        startActivityForResult(intent, 1);
    }

    public void editPost(View view) {
        Intent intent = new Intent(context, CreateProduct.class);
        intent.putExtra("item", item);
        context.startActivity(intent);
    }

    private void checkBeforAddFriend(final String idFriend) {

        LovelyProgressDialog dialogWait = new LovelyProgressDialog(ViewOrderItemActivity.this);
        //                            HashMap userMap = (HashMap) ((HashMap) dataSnapshot.getValue()).get(id);
        Friend user = new Friend();
        user.name = " ";
        user.email = " ";
        user.avata = " ";
        user.id = item.getOwnerId();
        user.idRoom = idRoom;
        Log.e(TAG, "onDataChange: " + user.email);
        dialogWait.setCancelable(false)
                .setIcon(R.drawable.ic_add_friend)
                .setTitle("Add friend....")
                .setTopColorRes(R.color.colorPrimary)
                .show();

        FriendDB.getInstance(ViewOrderItemActivity.this).addFriend(user);

    }
}
