package com.example.syltfabrik;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class contacts extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
    }


    public void addContact(View view) {
        Intent Startintent = new Intent(getApplicationContext(), ContactInfo.class);
        startActivity(Startintent);
    }
}
