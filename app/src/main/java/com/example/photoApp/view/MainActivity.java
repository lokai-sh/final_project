package com.example.photoApp.view;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.photoApp.LogAnnotation;
import com.example.photoApp.R;
import com.example.photoApp.model.DatabaseHelper;
import com.example.photoApp.presenter.MainActivityPresenter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements MainActivityPresenter.View, GestureDetector.OnGestureListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int SEARCH_ACTIVITY_REQUEST_CODE = 2;
    String mCurrentPhotoPath;
    private ArrayList<String> photos = null;
    private int index = 0;

    Button btn_favourite, btn_remove;

    BottomNavigationView bottomNavigation;

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_VELOCITY_THRESHOLD = 200;

    DatabaseHelper mdb;
    SQLiteDatabase db;
    Cursor c;
    byte[] img1;

    // Animation
    Animation animMove;

    private MainActivityPresenter mPresenter;
    private GestureDetector gestureScanner;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @LogAnnotation
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        mdb = new DatabaseHelper(getApplicationContext(), "tempDB", null, 1);
        this.deleteDatabase("tempDB");

        // load the animation
        animMove = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.move);
        animMove.reset();

        mPresenter = new MainActivityPresenter(this);

        gestureScanner = new GestureDetector(getBaseContext(), this);

        btn_favourite = findViewById(R.id.btnFavourite);
        btn_favourite.setOnClickListener(v -> saveToSQLiteDatabase());

        btn_remove = findViewById(R.id.btnRemove);
        btn_remove.setOnClickListener(v -> removeImageFromSDCard());

        checkPermissions();

        updatePhotos();

    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        return true;
                    case R.id.navigation_search:
                        searchPhoto();
                        return true;
                    case R.id.navigation_camera:
                        takePhoto();
                        return true;
                    case R.id.navigation_upload:
                        sharingToSocialMedia();
                        return true;
                }
                return false;
            };

    private void updatePhotos() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            photos = mPresenter.findPhotos(new Date(Long.MIN_VALUE), new Date(), "");
        }
        if (photos.size() == 0) {
            displayPhoto(null);
        } else {
            displayPhoto(photos.get(index));
        }
    }


    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, 1000);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 1001);
        }
    }

    public void sharingToSocialMedia() {

        if (checkIfAnyPhotoIsPresent()) {
            try {
                File file = new File(photos.get(index));
                Uri bmpUri = FileProvider.getUriForFile(this, "com.example.photoApp.fileprovider", file);

//            EditText mEdit   = (EditText)findViewById(R.id.etCaption);
//            String text = mEdit.getText().toString();

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
//            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
                shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                shareIntent.setType("image/*");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "Share images using"));

            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getApplicationContext(), "Please install the application first", Toast.LENGTH_LONG).show();

            }
        }
    }

    @LogAnnotation
    public void searchPhoto() {
        Intent searchIntent = new Intent(this, SearchActivity.class);
        startActivityForResult(searchIntent, SEARCH_ACTIVITY_REQUEST_CODE);
    }

    public void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there is a camera activity to handle the intent
        //Todo: Commenting this out for now to allow pictures to work. Not sure why it is always null
        //if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this, "com.example.photoApp.fileprovider", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @LogAnnotation
    public void scrollPhotos(View v) {
        if (photos.size() != 0) {
            updatePhoto(photos.get(index), ((EditText) findViewById(R.id.etCaption)).getText().toString());
            switch (v.getId()) {
                case R.id.btnPrev:
                    scrollImageToPrevious();
                    break;
                case R.id.btnNext:
                    scrollImageToNext();
                    break;
                default:
                    break;
            }
        }
    }

    public void scrollImageToNext() {
        if (index < (photos.size() - 1)) {
            index++;
        }

        ImageView iv = (ImageView) findViewById(R.id.ivGallery);
        iv.startAnimation(animMove);

        displayPhoto(photos.get(index));
    }

    public void scrollImageToPrevious() {
        if (index > 0) {
            index--;
        }
        ImageView iv = (ImageView) findViewById(R.id.ivGallery);
        TranslateAnimation animate = new TranslateAnimation(-iv.getWidth(), 0, 0, 0);
        animate.setDuration(800);
        animate.setFillAfter(true);
        iv.startAnimation(animate);

        displayPhoto(photos.get(index));
    }

    private void displayPhoto(String path) {
        ImageView iv = (ImageView) findViewById(R.id.ivGallery);
        TextView tv = (TextView) findViewById(R.id.tvTimestamp);
        EditText et = (EditText) findViewById(R.id.etCaption);
        if (path == null || path.equals("")) {
            iv.setImageResource(R.mipmap.ic_launcher);
            et.setText("");
            tv.setText("");
        } else {
            iv.setImageBitmap(BitmapFactory.decodeFile(path));
            String[] attr = path.split("_");
            et.setText(attr[1]);
            tv.setText(attr[2]);
            //addLocationTagging(path);
        }
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                DateFormat format = new SimpleDateFormat("yyyy‐MM‐dd HH:mm:ss");
                Date startTimestamp, endTimestamp;
                try {
                    String from = (String) data.getStringExtra("STARTTIMESTAMP");
                    String to = (String) data.getStringExtra("ENDTIMESTAMP");
                    startTimestamp = format.parse(from);
                    endTimestamp = format.parse(to);
                } catch (Exception ex) {
                    startTimestamp = null;
                    endTimestamp = null;
                }
                String keywords = (String) data.getStringExtra("KEYWORDS");
                index = 0;
                photos = mPresenter.findPhotos(startTimestamp, endTimestamp, keywords);
                if (photos.size() == 0) {
                    displayPhoto(null);
                } else {
                    displayPhoto(photos.get(index));
                }
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ImageView mImageView = (ImageView) findViewById(R.id.ivGallery);
            mImageView.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
            updatePhotos();
            new AlertDialog.Builder(this)
                    .setTitle("Picture snapped succesfully and saved!")
                    .setMessage("Saved to SD card.")
                    .show();
            //photos = mPresenter.findPhotos(new Date(Long.MIN_VALUE), new Date(), "");
        }
        if (requestCode == 10) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                String command = result.get(0);

                if (command.contains("next")) {
                    scrollImageToNext();
                } else if (command.contains("previous")) {
                    scrollImageToPrevious();
                } else if (command.contains("search")) {
                    searchPhoto();
                } else if (command.contains("snap")) {
                    takePhoto();
                } else if (command.contains("share")) {
                    sharingToSocialMedia();
                } else if (command.contains("favourite")) {
                    saveToSQLiteDatabase();
                } else if (command.contains("remove")) {
                    removeImageFromSDCard();
                } else {

                }
            }
        }
    }

    public void getSpeechInput(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please give command");

        try {
            startActivityForResult(intent, 10);
        } catch (Exception e) {
            Toast.makeText(this, "Your Device Doesn't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void updatePhoto(String path, String caption) {
        mPresenter.updatePhoto(path, caption, photos, index);
    }


    @Override
    public boolean onTouchEvent(MotionEvent me) {
        return gestureScanner.onTouchEvent(me);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
                return false;
            }
            // left to right swipe
            if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                scrollImageToPrevious();
            }
            // right to left swipe
            else if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                scrollImageToNext();
            }

        } catch (Exception e) {

        }

        return true;
    }

    public void saveToSQLiteDatabase() {
        if (checkIfAnyPhotoIsPresent()) {
            try {
                ImageView iv = (ImageView) findViewById(R.id.ivGallery);

                //to write in a database
                db = mdb.getWritableDatabase();
                FileInputStream fs = new FileInputStream(photos.get(index));
                byte[] byteImage = new byte[fs.available()];
                fs.read(byteImage);

                ContentValues cv = new ContentValues();
                cv.put("name", "temp" + index + ".png");
                cv.put("image", byteImage);
                db.insert("tableimage", null, cv);
                fs.close();

                // retrieve image from the database
                String selectQuery = "SELECT * FROM tableimage";
                c = db.rawQuery(selectQuery, null);
                if (c != null) {
                    c.moveToFirst();
                    do {
                        img1 = c.getBlob(1);
                        String name = c.getString(0);
                    } while (c.moveToNext());
                }

                Bitmap b1 = BitmapFactory.decodeByteArray(img1, 0, img1.length);
                iv.setImageBitmap(b1);
                //btn_favourite.setEnabled(false);

                new AlertDialog.Builder(this)
                        .setTitle("Picture saved successfully!")
                        .setMessage("Saved to Android SQLite database.")
                        .show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean checkIfAnyPhotoIsPresent() {
        boolean photoPresent = false;

        if (photos.size() == 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Image file doesn't exist!")
                    .setMessage("There are no photos in the collection")
                    .show();
        } else {
            photoPresent = true;
        }
        return photoPresent;

    }

    public File removeImageFromSDCard() {
        File file = null;
        if (checkIfAnyPhotoIsPresent()) {

            file = new File(photos.get(index));
            if (file.exists()) {
                file.delete();

                // Continue only if the File was successfully deleted
                new AlertDialog.Builder(this)
                        .setTitle("Picture removed successfully!")
                        .setMessage("Removed from the SD Card.")
                        .show();
                index = 0;
                updatePhotos();

            }
        }
        return file;
    }
}