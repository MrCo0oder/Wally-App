package com.codebook.wallyapp;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class WallpaperView extends AppCompatActivity {
    String title, image, imageFileName;
    DisplayMetrics displayMetrics;
    BitmapDrawable bitmapDrawable;
    Bitmap bitmap;
    WallpaperManager wallpaperManager;
    ImageView imageViewFull, imageViewDownload;
    Button buttonLockScreen, buttonHomeScreen;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper_view);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        image = getIntent().getStringExtra("image");
        title = getIntent().getStringExtra("title");
        initViews();
        Glide.with(this).load(image).centerCrop().
                listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        buttonHomeScreen.setOnClickListener(view -> setWallpaper("home"));
                        buttonLockScreen.setOnClickListener(        view -> setWallpaper("lock"));
                        imageViewDownload.setOnClickListener(view -> downLoadZImage());
                        return false;
                    }
                }).error(R.drawable.no_image).placeholder(R.drawable.ic_loading).into(imageViewFull);
    }

    private void downLoadZImage() {
        //it actually exported from image view;
        imageFileName = image.substring(image.lastIndexOf("/"));
        FileOutputStream fileOutputStream;
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Wally App");
        if (!file.exists() && !file.mkdir()) {
            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
        } else {
            File fileName = new File(file.getAbsolutePath() + "/" + imageFileName);
            int[] size = getScreenSize();
            try {
                fileOutputStream = new FileOutputStream(fileName);
                bitmapDrawable = (BitmapDrawable) imageViewFull.getDrawable();
                bitmap = bitmapDrawable.getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap, size[0], size[1], false);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                Toast.makeText(getApplicationContext(), "Saved to Gallery\n" + fileName, Toast.LENGTH_SHORT).show();
                fileOutputStream.flush();
                fileOutputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void setWallpaper(String type) {
        int[] size = getScreenSize();
        wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        bitmapDrawable = (BitmapDrawable) imageViewFull.getDrawable();
        bitmap = bitmapDrawable.getBitmap();
        bitmap = Bitmap.createScaledBitmap(bitmap, size[0], size[1], false);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (type.equals("lock")) {
                    wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);
                    Toast.makeText(getApplicationContext(), "Success{Lock}", Toast.LENGTH_SHORT).show();
                } else {
                    wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                    Toast.makeText(getApplicationContext(), "Success{Home}", Toast.LENGTH_SHORT).show();
                }
            } else {
                wallpaperManager.setBitmap(bitmap);
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
            }
            wallpaperManager.suggestDesiredDimensions(size[0], size[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int[] getScreenSize() {
        //gets screen size
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int[] size = new int[2];
        size[0] = displayMetrics.widthPixels;
        size[1] = displayMetrics.heightPixels;
        return size;
    }

    private void initViews() {
        imageViewDownload = findViewById(R.id.downloadImage);
        imageViewFull = findViewById(R.id.imageFull);
        buttonHomeScreen = findViewById(R.id.setHomeScreen);
        buttonLockScreen = findViewById(R.id.setLockScreen);
    }
}