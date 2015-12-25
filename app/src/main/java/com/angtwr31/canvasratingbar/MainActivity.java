package com.angtwr31.canvasratingbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.angtwr31.library.CanvasRatingBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CanvasRatingBar canvasRatingBar=((CanvasRatingBar) findViewById(R.id.ratingbar));
    }
}
