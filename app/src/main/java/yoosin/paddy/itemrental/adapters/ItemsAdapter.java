package yoosin.paddy.itemrental.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import yoosin.paddy.itemrental.R;
import yoosin.paddy.itemrental.activities.ViewItemActivity;
import yoosin.paddy.itemrental.models.Item;
import yoosin.paddy.itemrental.models.MyOrder;


public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.myHolder> {
    private Context context;
    private List<Item> itemsAround;
    private static final String TAG = "ItemsAdapter";
    public ItemsAdapter(Context context, List<Item> itemsAround) {
        this.context = context;
        this.itemsAround = itemsAround;
    }

    @NonNull
    @Override
    public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new myHolder(LayoutInflater.from(context).inflate(R.layout.item_product, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull myHolder holder, int position) {
        Item h=itemsAround.get(position);
        holder.itemName.setText(h.getName());
        holder.itemPrice.setText(h.getPrice());
        holder.request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestItem(h);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Glide.with(context)
                    .load(h.getPhotoUrl())
                    .fallback(context.getDrawable(R.drawable.ic_image_placeholder))
                    .into(holder.itemImage);
        }else {
            Glide.with(context)
                    .load(h.getPhotoUrl())
                    .into(holder.itemImage);
        }
        holder.oneCard.setOnClickListener(v -> itemClick(h));

    }

    private void requestItem(Item item) {
        FirebaseApp firebaseApp= FirebaseApp.initializeApp(context);
        MyOrder order=new MyOrder(item);
        order.setStatus("pending");
        order.setSecondPartyId(FirebaseAuth.getInstance(firebaseApp).getUid());
        order.setSecondPartyName(FirebaseAuth.getInstance(firebaseApp).getCurrentUser().getDisplayName());

        DatabaseReference
                reference= FirebaseDatabase.getInstance().getReference().child("profiles/"+order.getSecondPartyId()+"/requested_items"+item.getId());
        DatabaseReference
                reference2= FirebaseDatabase.getInstance().getReference().child("profiles/"+ order.getOwnerId()+"/order_items"+item.getId());
        reference2.setValue(order);
        reference.setValue(order).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e(TAG, "onSuccess: " );
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: " );

            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Log.e(TAG, "onCanceled: " );

            }
        });
    }

    @Override
    public int getItemCount() {
        return itemsAround.size();
    }


    static class myHolder extends RecyclerView.ViewHolder{
        TextView itemName,itemPrice;
        Button request;
        ImageView itemImage;
        View oneCard;
        myHolder(@NonNull View itemView) {
            super(itemView);
            itemName=itemView.findViewById(R.id.itemName);
            itemPrice=itemView.findViewById(R.id.itemPrice);
            itemImage=itemView.findViewById(R.id.itemImage);
            request=itemView.findViewById(R.id.request);
            oneCard=itemView.findViewById(R.id.oneCard);
        }
    }
    private void itemClick(Item item){
        Intent intent = new Intent(context, ViewItemActivity.class);
        intent.putExtra("item",item);
        context.startActivity(intent);
    }

}
