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

// ini adalah class untuk
// mngatur layout tutorial
// yg akan ditampilkan di setiap view
// activity dan fragment
public class TutorialLayout {

    // deklarasi variabel context
    private Context c;

    // deklarasi nama tutorial
    private String name;

    // deklarasi variabel view yg diinclude
    private View includeParent;

    // deklarasi variabel gambar ilustrasi
    private ImageView image;

    // deklarasi variabel text pesan tutorial
    private TextView message;

    // deklarasi variabel button
    // pada saat ditekan akan menjalankan aksi
    // pada callback
    private Button close;

    // konstruktor
    // dengan 4 parameter
    public TutorialLayout(Context c,String name, View includeParent, View.OnClickListener onClickListener) {

        // inisialisasi konteks
        this.c = c;

        // inisialisasi view
        this.includeParent = includeParent;

        // inisialisasi nama
        this.name = name;

        // inisialisasi gambar
        this.image = this.includeParent.findViewById(R.id.image_tutorial);

        // inisialisasi pesan
        this.message = this.includeParent.findViewById(R.id.tutorial_message_text);

        // inisialisasi tombol tutup
        this.close= this.includeParent.findViewById(R.id.button_close_tutorial);

        // pada saat tombol di klik
        this.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // simpan cache
                // agar tutorial tidak ditampilkan lagi
                new SerializableSave(c,name).save(new SerializableSave.SimpleCache("done"));

                // invoke fungsi callback
                onClickListener.onClick(v);
            }
        });

        // tampilkan layout
        this.show();

        // jika cache ada
        if (new SerializableSave(c, this.name).load() != null){

            // sembunyikan
            this.hide();
        }
    }

    // fungsi untuk menampilkan pesan error
    // yg akan ditampilkan
    public void setMessage(String m){
        this.message.setText(m);
    }

    // fungsi untuk menampilkan gambar ilustrasi
    // yg akan ditampilkan
    public void setImage(int i){
        this.image.setImageDrawable(ContextCompat.getDrawable(this.c,i));
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
