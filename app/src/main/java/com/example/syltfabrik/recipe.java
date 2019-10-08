package com.example.syltfabrik;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class recipe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipe);
    }

    public void addrecipe(View view) {
        Intent addrecipe = new Intent(getApplicationContext(), addRecipe.class);
        startActivity(addrecipe);
    }
}
