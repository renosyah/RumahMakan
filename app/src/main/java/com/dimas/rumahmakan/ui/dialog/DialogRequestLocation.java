package com.dimas.rumahmakan.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import com.dimas.rumahmakan.R;
import com.dimas.rumahmakan.util.Unit;

public class DialogRequestLocation {
    private Context context;
    private Unit<Boolean> onOk;

    public DialogRequestLocation(Context context, Unit<Boolean> onOk) {
        this.context = context;
        this.onOk = onOk;
    }

    public void show(){
        View v = ((Activity)context).getLayoutInflater().inflate(R.layout.dialog_request_location,null);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .create();

        Button ok = v.findViewById(R.id.button_ok);
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
