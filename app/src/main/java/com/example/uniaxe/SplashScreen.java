package com.example.uniaxe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.cloudinary.android.MediaManager;
import com.example.uniaxe.MainActivity;
import com.example.uniaxe.R;

import java.util.HashMap;
import java.util.Map;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.
                FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // Show the activity in full screen

        setContentView(R.layout.activity_splash_screen);

        try {
            initConfig();
        } catch (Exception e) {
            Log.d("Media", String.valueOf(e));
        }

        // Navigate to MainActivity after a delay
        new Handler().postDelayed(() -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }, 1500);
    }

    private void initConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dts8ml0uo");
        config.put("api_key", "515729643293135");
        config.put("api_secret", "9x-mUzeY7eSgk2SD_b5jlIX0Ekk");
        // config.put("secure", true);
        MediaManager.init(this, config);
    }
}
