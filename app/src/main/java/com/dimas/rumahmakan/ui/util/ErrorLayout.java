package com.dimas.rumahmakan.ui.util;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dimas.rumahmakan.R;


// ini adalah class untuk
// mngatur layout error
// yg akan ditampilkan di setiap view
// activity dan fragment
public class ErrorLayout {

    // deklarasi variabel context
    private Context c;

    // deklarasi variabel view yg diinclude
    private View includeParent;

    // deklarasi variabel text pesan error
    private TextView message;

    // deklarasi variabel button
    // pada saat ditekan akan menjalankan aksi
    // pada callback
    private Button tryAgain;

    // konstruktor
    // dengan 4 parameter
    public ErrorLayout(Context c, View includeParent, View.OnClickListener onClickListener) {

        // inisialisais konteks
        this.c = c;

        // inisialisasi view
        this.includeParent = includeParent;

        // inisialisasi pesan
        this.message = this.includeParent.findViewById(R.id.error_message_text);

        // ini sialisasi tombol
        // coba lagi
        this.tryAgain = this.includeParent.findViewById(R.id.button_try_again);

        // set pada saat tombol diclick
        this.tryAgain.setOnClickListener(onClickListener);

        // secara default
        // sembunyikan tampilan error
        this.hide();
    }

    // fungsi untuk menampilkan pesan error
    // yg akan ditampilkan
    public void setMessage(String m){
        this.message.setText(m);
    }

    // fungsi untuk menetukan
    // apakah view ditampilkan atau tidak
    public void setVisibility(boolean v){ this.includeParent.setVisibility(v ? View.VISIBLE : View.GONE); }

    // fungsi menampilkan view
    public void show(){
        this.includeParent.setVisibility(View.VISIBLE);
    }

    // fungsi menyembunyikan view
    public void hide(){
        this.includeParent.setVisibility(View.GONE);
    }
}
