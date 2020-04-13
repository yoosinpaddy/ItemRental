package yoosin.paddy.itemrental.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.util.ArrayList;
import java.util.HashMap;

import yoosin.paddy.itemrental.R;
import yoosin.paddy.itemrental.models.Item;
import yoosin.paddy.itemrental.messaging.data.FriendDB;
import yoosin.paddy.itemrental.messaging.data.StaticConfig;
import yoosin.paddy.itemrental.messaging.model.Friend;
import yoosin.paddy.itemrental.messaging.ui.ChatActivity;

public class ViewItemActivity extends AppCompatActivity {
    ImageView imageView;
    FloatingActionButton message, edit;
    TextView title, amount, description, itemDistance;
    Item item;
    String idRoom;
    Context context;
    private static final String TAG = "ViewItemActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);
        FirebaseApp.initializeApp(ViewItemActivity.this);
        context = ViewItemActivity.this;
        imageView = findViewById(R.id.productImage);
        message = findViewById(R.id.message);
        edit = findViewById(R.id.edit);
        title = findViewById(R.id.itemName);
        amount = findViewById(R.id.itemPrice);
        description = findViewById(R.id.ItemDescription);
        itemDistance = findViewById(R.id.itemDistance);
        if (getIntent().getExtras() != null) {
            item = (Item) getIntent().getSerializableExtra("item");
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
                String dis= String.valueOf(item.getDistance(ViewItemActivity.this))+" km";
                if (item.getDistance(ViewItemActivity.this) < 1 && item.getDistance(ViewItemActivity.this) < -1) {
                    itemDistance.setText(dis);
                }else {
                    itemDistance.setText(dis);
                }
            }
            Glide.with(ViewItemActivity.this)
                    .load(item.getPhotoUrl())
                    .fallback(R.drawable.ef_image_placeholder)
                    .into(imageView);
        }
    }

    public void sendMessage(View view) {
        StaticConfig.UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Intent intent = new Intent(ViewItemActivity.this, ChatActivity.class);
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

        LovelyProgressDialog dialogWait = new LovelyProgressDialog(ViewItemActivity.this);
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

        FriendDB.getInstance(ViewItemActivity.this).addFriend(user);

    }
}
