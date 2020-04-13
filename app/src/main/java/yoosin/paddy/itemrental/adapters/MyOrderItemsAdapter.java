package yoosin.paddy.itemrental.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import yoosin.paddy.itemrental.R;
import yoosin.paddy.itemrental.activities.ViewItemActivity;
import yoosin.paddy.itemrental.models.MyItem;
import yoosin.paddy.itemrental.models.MyOrder;


public class MyOrderItemsAdapter extends RecyclerView.Adapter<MyOrderItemsAdapter.myHolder>  {
    private Context context;
    private List<MyOrder> itemsAround;

    public MyOrderItemsAdapter(Context context, List<MyOrder> itemsAround) {
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
        MyOrder h=itemsAround.get(position);
        holder.itemName.setText(h.getName());
        holder.itemPrice.setText(h.getPrice());
        holder.itemStatus.setText(h.getStatus());
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

    @Override
    public int getItemCount() {
        return itemsAround.size();
    }

    static class myHolder extends RecyclerView.ViewHolder{
        TextView itemName,itemPrice,itemStatus;
        ImageView itemImage;
        View oneCard;
        myHolder(@NonNull View itemView) {
            super(itemView);
            itemName=itemView.findViewById(R.id.itemName);
            itemPrice=itemView.findViewById(R.id.itemPrice);
            itemImage=itemView.findViewById(R.id.itemImage);
            oneCard=itemView.findViewById(R.id.oneCard);
            itemStatus=itemView.findViewById(R.id.itemStatus);
        }
    }
    private void itemClick(MyOrder item){
        Intent intent = new Intent(context, ViewItemActivity.class);
        intent.putExtra("item",item);
        context.startActivity(intent);
    }

}
