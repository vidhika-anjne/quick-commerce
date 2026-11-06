package com.vidhika.zepto;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        View btnBack = findViewById(R.id.btn_back_settings);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    public void zeptoCash(View view) {
        Toast.makeText(this, "Zepto Cash Balance: ₹75", Toast.LENGTH_SHORT).show();
    }
}
