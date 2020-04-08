package com.dimas.rumahmakan.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dimas.rumahmakan.R;
import com.dimas.rumahmakan.model.cityModel.CityModel;

import java.util.ArrayList;

public class AdapterCityModel extends RecyclerView.Adapter<AdapterCityModel.Holder> {

    private Context context;
    private ArrayList<CityModel> list;

    public AdapterCityModel(Context context, ArrayList<CityModel> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(((Activity)context).getLayoutInflater().inflate(R.layout.adapter_city_model,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        CityModel item = list.get(position);

        holder.name.setText(item.Name);
        holder.total.setText(item.Total + " "+ context.getString(R.string.app_name));
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Holder extends RecyclerView.ViewHolder{

        public TextView name;
        public TextView total;

        public Holder(@NonNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.city_name);
            this.total = itemView.findViewById(R.id.total);
        }
    }
}
