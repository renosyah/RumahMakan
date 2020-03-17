package com.dimas.rumahmakan.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.dimas.rumahmakan.R;
import com.dimas.rumahmakan.model.restaurantModel.RestaurantModel;
import com.dimas.rumahmakan.util.Unit;

public class DialogDetailRestaurant {
    private Context context;
    private RestaurantModel item;
    private Unit<Boolean> onOk;


    public DialogDetailRestaurant(Context context, RestaurantModel item, Unit<Boolean> onOk) {
        this.context = context;
        this.item = item;
        this.onOk = onOk;
    }

    public void show(){
        View v = ((Activity)context).getLayoutInflater().inflate(R.layout.dialog_detail_restaurant,null);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .create();

        TextView name = v.findViewById(R.id.restaurant_name_text);
        name.setText(item.Name);

        TextView address = v.findViewById(R.id.restaurant_address_text);
        address.setText(item.Address + " (" + String.format("%.1f", item.Distance) +" Km)");

        Button ok = v.findViewById(R.id.button_detail);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOk.invoke(true);
                dialog.dismiss();
            }
        });

        dialog.setView(v);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();
    }
}
