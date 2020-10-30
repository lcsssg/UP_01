package com.example.up_01;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.app.backup.SharedPreferencesBackupHelper;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    private CanvasView canvasView;
    private Paint myPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int brushSize = 5;
    private int brushColor = Color.BLACK;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        canvasView = new CanvasView(this);
        canvasView.setId(View.generateViewId());
        FrameLayout layout = findViewById(R.id.canvas_frame);
        layout.addView(canvasView);
        myPaint.setColor(Color.BLACK);


        SeekBar bar = findViewById(R.id.brush_size);


        bar.setOnSeekBarChangeListener(this);

        Button greenButton = findViewById(R.id.button_green);
        Button greenDarkButton = findViewById(R.id.button_green_dark);
        Button blueButton = findViewById(R.id.button_blue);
        Button blueLightButton = findViewById(R.id.button_blue_light);
        Button blueDarkButton = findViewById(R.id.button_blue_dark);
        Button purpleButton = findViewById(R.id.button_purple);
        Button orangeButton = findViewById(R.id.button_orange);
        Button orangeDarkButton = findViewById(R.id.button_orange_dark);
        Button redButton = findViewById(R.id.button_red);

        greenButton.setOnClickListener(this);
        greenDarkButton.setOnClickListener(this);
        blueButton.setOnClickListener(this);
        blueLightButton.setOnClickListener(this);
        blueDarkButton.setOnClickListener(this);
        purpleButton.setOnClickListener(this);
        orangeButton.setOnClickListener(this);
        orangeDarkButton.setOnClickListener(this);
        redButton.setOnClickListener(this);

    }

    private String getStorePath() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String storePath = preferences.getString(getString(R.string.settings_store_path), null);
        if (storePath == null) {
            storePath = Environment.getExternalStorageDirectory().toString();

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(getString(R.string.settings_store_path), storePath);

            editor.commit();

        }
        return storePath;
    }


    private int getImageQuality() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int quality = preferences.getInt(getString(R.string.settings_image_quality), -1);

        if (quality == -1) {
            quality = 95;


            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(getString(R.string.settings_image_quality), quality);
            editor.commit();
        }
        return quality;
    }

    private Bitmap.CompressFormat getImageFormat() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String format = preferences.getString(getString(R.string.settings_image_format), null);
        if (format == null) {
            format = "png";
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(getString(R.string.settings_image_format), format);
            editor.commit();
        }
        switch (format) {
            case "png":
                return Bitmap.CompressFormat.PNG;
            case "jpeg":
                return Bitmap.CompressFormat.JPEG;
            default:
                return null; // Никогда не должно произойти
        }
    }


    private void saveImage() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0);
            return;
        }

        Bitmap image = canvasView.getImage();

        String storePath = getStorePath();
        Bitmap.CompressFormat format = getImageFormat();
        String imageFileName = ImageSaver.generateImageFileName(storePath, format);
        try {

            ImageSaver.saveImage(image, format, getImageQuality(), imageFileName);
            Toast.makeText(this, "Изображение сохранено:" + imageFileName,
                    Toast.LENGTH_LONG).show();

        } catch (IOException e) {

            Log.e(MainActivity.class.getCanonicalName(),
                    e.getMessage());

        }
    }


    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.menu_item_save:
                saveImage();
                break;

            case R.id.menu_item_settings:
                openSettings();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        Canvas canvas = canvasView.getCanvas();

        int[] location = new int[2];
        canvasView.getLocationOnScreen(location);

        myPaint.setColor(brushColor);
        canvas.drawCircle(event.getX(), event.getY() - location[1], brushSize, myPaint);

        canvasView.invalidate();


        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
        brushSize = i;
    }

    @Override
    public void onStartTrackingTouch(SeekBar  seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View v) {
        Button button = (Button) v;
        brushColor = button.getBackgroundTintList().getDefaultColor();

    }
}
