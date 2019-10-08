package com.example.syltfabrik;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void contacts(View view){
        Intent contactintent = new Intent(getApplicationContext(), contacts.class);
        startActivity(contactintent);
    }

    public void recipe(View View){
        Intent recipeintent = new Intent(getApplicationContext(), recipe.class);
        startActivity(recipeintent);
    }

}


