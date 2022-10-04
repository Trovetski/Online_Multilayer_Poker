package com.example.simplecardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ShowCardActivity extends AppCompatActivity {

    ImageView card1View, card2View;
    Button ok;
    Card c1, c2;
    Resource resource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_card);

        resource = new Resource(this);

        ok = findViewById(R.id.button);
        card1View = findViewById(R.id.card1);
        card2View = findViewById(R.id.card2);

        c1 = new Card(getIntent().getExtras().getInt("card1"));
        c2 = new Card(getIntent().getExtras().getInt("card2"));

        card1View.setImageBitmap(resource.getCard(c1));
        card2View.setImageBitmap(resource.getCard(c2));

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        getWindow().setLayout(3*width/4,(int)(0.769*width));
    }
}
