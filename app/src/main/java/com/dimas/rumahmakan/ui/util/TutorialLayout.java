package com.dimas.rumahmakan.ui.util;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.dimas.rumahmakan.R;
import com.dimas.rumahmakan.util.SerializableSave;

import static com.dimas.rumahmakan.util.StaticVariabel.TUTOR_SWIPE;

public class TutorialLayout {
    private Context c;
    private String name;
    private View includeParent;
    private ImageView image;
    private TextView message;
    private Button close;

    public TutorialLayout(Context c,String name, View includeParent, View.OnClickListener onClickListener) {
        this.c = c;
        this.includeParent = includeParent;
        this.name = name;
        this.image = this.includeParent.findViewById(R.id.image_tutorial);
        this.message = this.includeParent.findViewById(R.id.tutorial_message_text);
        this.close= this.includeParent.findViewById(R.id.button_close_tutorial);
        this.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SerializableSave(c,name).save(new SerializableSave.SimpleCache("done"));
                onClickListener.onClick(v);
            }
        });
        this.show();
        if (new SerializableSave(c, this.name).load() != null){
            this.hide();
        }
    }

    public void setMessage(String m){
        this.message.setText(m);
    }

    public void setImage(int i){
        this.image.setImageDrawable(ContextCompat.getDrawable(this.c,i));
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
