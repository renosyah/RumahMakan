package com.dimas.rumahmakan.ui.util;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.dimas.rumahmakan.R;

public class LoadingLayout {
    private Context c;
    private View includeParent;
    private TextView message;

    public LoadingLayout(Context c, View includeParent) {
        this.c = c;
        this.includeParent = includeParent;
        this.message = this.includeParent.findViewById(R.id.loading_message);
        this.show();
    }

    public void setMessage(String m){
        this.message.setText(m);
    }

    public void setVisibility(boolean v){
        this.includeParent.setVisibility(v ? View.VISIBLE : View.GONE);
    }

    public void show(){
        this.includeParent.setVisibility(View.VISIBLE);
    }

    public void hide(){
        this.includeParent.setVisibility(View.GONE);
    }
}
