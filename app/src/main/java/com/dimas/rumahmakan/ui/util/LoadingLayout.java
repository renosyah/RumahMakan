package com.dimas.rumahmakan.ui.util;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.dimas.rumahmakan.R;

// ini adalah class untuk
// mngatur layout loading
// yg akan ditampilkan di setiap view
// activity dan fragment
public class LoadingLayout {

    // deklarasi variabel context
    private Context c;

    // deklarasi variabel view yg diinclude
    private View includeParent;

    // deklarasi variabel text pesan loading
    private TextView message;

    // konstruktor
    // dengan 3 parameter
    public LoadingLayout(Context c, View includeParent) {

        // inisialisais konteks
        this.c = c;

        // inisialisais view
        this.includeParent = includeParent;

        // inisialisasi pesan loading
        this.message = this.includeParent.findViewById(R.id.loading_message);

        // tampilkan layout loading
        this.show();
    }

    // fungsi untuk menampilkan pesan error
    // yg akan ditampilkan
    public void setMessage(String m){
        this.message.setText(m);
    }

    // fungsi untuk menetukan
    // apakah view ditampilkan atau tidak
    public void setVisibility(boolean v){
        this.includeParent.setVisibility(v ? View.VISIBLE : View.GONE);
    }

    // fungsi menampilkan view
    public void show(){
        this.includeParent.setVisibility(View.VISIBLE);
    }

    // fungsi menyembunyikan view
    public void hide(){
        this.includeParent.setVisibility(View.GONE);
    }
}
