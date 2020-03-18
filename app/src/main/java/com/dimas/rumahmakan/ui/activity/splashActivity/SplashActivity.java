package com.dimas.rumahmakan.ui.activity.splashActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.dimas.rumahmakan.R;
import com.dimas.rumahmakan.ui.activity.exploreActivity.ExploreActivity;

import java.util.Random;

public class SplashActivity extends AppCompatActivity {

    private Context context;
    private ImageView foodImage;
    private Button exploreButton;

    int[] foodImages = {
            R.drawable.food_1,
            R.drawable.food_4,
            R.drawable.food_3,
            R.drawable.food_4,
            R.drawable.food_2,
            R.drawable.food_1,
            R.drawable.food_1,
            R.drawable.food_3,
            R.drawable.food_2,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initWidget();
    }

    private void initWidget(){
        context = this;

        foodImage = findViewById(R.id.food_image);
        foodImage.setImageDrawable(ContextCompat.getDrawable(context,foodImages[new Random().nextInt(foodImages.length - 1)]));

        exploreButton = findViewById(R.id.button_explore);
        exploreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ExploreActivity.class));
                finish();
            }
        });

    }
}
