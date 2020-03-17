package com.dimas.rumahmakan.ui.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.dimas.rumahmakan.R;
import com.dimas.rumahmakan.model.restaurantModel.RestaurantModel;
import com.dimas.rumahmakan.util.Unit;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterSwipeStackRestaurant extends BaseAdapter {

    private Context context;
    private ArrayList<RestaurantModel> list;
    private Unit<RestaurantModel> onClick;

    public AdapterSwipeStackRestaurant(Context context, ArrayList<RestaurantModel> list, Unit<RestaurantModel> onClick) {
        this.context = context;
        this.list = list;
        this.onClick = onClick;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = ((Activity)context).getLayoutInflater().inflate(R.layout.adapter_swipe_stack_restaurant,parent,false);

        RestaurantModel item = list.get(position);

        ImageView image = convertView.findViewById(R.id.restaurant_image);
        Picasso.get()
                .load(item.UrlImage)
                .into(image);

        TextView name = convertView.findViewById(R.id.restaurant_name);
        name.setText(item.Name);

        TextView address = convertView.findViewById(R.id.restaurant_address);
        address.setText(item.Address + " (" + String.format("%.1f", item.Distance) +" Km)");

        Button detail = convertView.findViewById(R.id.button_detail);
        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.invoke(item);
            }
        });

        return convertView;
    }
}
