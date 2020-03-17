package com.dimas.rumahmakan.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dimas.rumahmakan.R;
import com.dimas.rumahmakan.model.restaurantModel.RestaurantModel;
import com.dimas.rumahmakan.util.Unit;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterRestaurant extends RecyclerView.Adapter<AdapterRestaurant.Holder> {

    private Context context;
    private ArrayList<RestaurantModel> list;
    private Unit<RestaurantModel> onClick;

    public AdapterRestaurant(Context context, ArrayList<RestaurantModel> list, Unit<RestaurantModel> onClick) {
        this.context = context;
        this.list = list;
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(((Activity)context).getLayoutInflater().inflate(R.layout.adapter_restaurant,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        RestaurantModel item = list.get(position);

        Picasso.get()
                .load(item.UrlImage)
                .into(holder.image);

        holder.name.setText(item.Name);

        holder.distance.setText(String.format("%.1f", item.Distance) +" Km");

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.invoke(item);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        public ImageView image;
        public TextView name;
        public TextView distance;

        public Holder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.restaurant_image);
            name = itemView.findViewById(R.id.restaurant_name_text);
            distance= itemView.findViewById(R.id.restaurant_distance_text);
        }
    }
}
