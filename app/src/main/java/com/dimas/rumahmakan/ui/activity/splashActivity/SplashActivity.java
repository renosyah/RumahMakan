package com.dimas.rumahmakan.ui.activity.splashActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dimas.rumahmakan.R;
import com.dimas.rumahmakan.ui.activity.exploreActivity.ExploreActivity;
import com.dimas.rumahmakan.ui.activity.statistikActivity.StatistikActivity;

import java.util.Random;

// ini adalah aktivity yg pertamakali dijalankan
// untuk menunjukan kepada user sebelum menjelajahi
// aneka wisata kuliner diaplikasi ini
public class SplashActivity extends AppCompatActivity {

    // deklarasi konteks
    private Context context;

    // deklarasi gambar makanan
    private ImageView foodImage;

    // deklarasi text untuk pesan
    private TextView messageText;

    // deklarasi tombol explore
    private Button exploreButton;

    // deklarasi tombol statistik
    private Button buttonStatistic;

    // data-data gambar yg akan digunakan
    // disimpan dalam array
    int[] foodImages = {
            R.drawable.food_1,
            R.drawable.food_4,
            R.drawable.food_3,
            R.drawable.food_4,
            R.drawable.food_2,
            R.drawable.food_1,
            R.drawable.food_1,
            R.drawable.food_3,
            R.drawable.food_2
    };

    // data-data pesan yg akan digunakan
    // disimpan dalam array
    int[] message = {
            R.string.restaurant_near_me_text,
            R.string.indonesian_food,
            R.string.local_restaurant,
            R.string.your_new_experience
    };

    // ini adalah fungsi yg akan [ertamakali dijalankan
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // view yg dipakai
        setContentView(R.layout.activity_splash);

        // memanggil fungsi inisialisasi
        initWidget();
    }

    // fungsi inisialisasi
    private void initWidget(){

        // inisialisasi konteks
        context = this;

        // inisialisasi gambar makanan
        foodImage = findViewById(R.id.food_image);

        // menampilkan gambar makanan yg dipilih
        // secara random
        foodImage.setImageDrawable(ContextCompat.getDrawable(context,foodImages[new Random().nextInt(foodImages.length - 1)]));

        // inisialisasi gambar makanan
        messageText = findViewById(R.id.message_text);

        // menampilkan pesan yg dipilih
        // secara random
        messageText.setText(context.getString(message[new Random().nextInt(message.length - 1)]).toUpperCase());

        // inisialisasi tombol jelajah
        exploreButton = findViewById(R.id.button_explore);

        // pada saat diklik maka akan
        exploreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // memanggil aktivty
                // jelajah dan
                startActivity(new Intent(context, ExploreActivity.class));

                // menghancurkan aktivity
                finish();
            }
        });

        // inisialisasi tombol statistik
        buttonStatistic = findViewById(R.id.button_statistic);

        // pada saat diklik maka akan
        buttonStatistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // memanggil aktivty
                // yg menampilkan data statistik
                startActivity(new Intent(context, StatistikActivity.class));
            }
        });
    }
}
