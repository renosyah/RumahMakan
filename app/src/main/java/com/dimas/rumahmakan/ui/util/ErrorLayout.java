package com.dimas.rumahmakan.ui.util;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dimas.rumahmakan.R;

public class ErrorLayout {
    private Context c;
    private View includeParent;
    private TextView message;
    private Button tryAgain;

    public ErrorLayout(Context c, View includeParent, View.OnClickListener onClickListener) {
        this.c = c;
        this.includeParent = includeParent;
        this.message = this.includeParent.findViewById(R.id.error_message_text);
        this.tryAgain = this.includeParent.findViewById(R.id.button_try_again);
        this.tryAgain.setOnClickListener(onClickListener);
        this.hide();
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
