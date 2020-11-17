package com.example.photoApp.presenter;

import android.media.ExifInterface;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class MainActivityPresenter {

private final View view;

    public MainActivityPresenter(View view) {
        this.view = view;
    }

    public void updatePhoto(String path, String caption, ArrayList<String> photos, int index) {
        String[] attr = path.split("_");
        if (attr.length >= 3) {
            File to = new File(attr[0] + "_" + caption + "_" + attr[2] + "_" + attr[3]);
            File from = new File(path);
            from.renameTo(to);
            photos.set(index, to.toString());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<String> findPhotos(Date startTimestamp, Date endTimestamp, String keywords) {
        File file = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath(), "/Android/data/com.example.photoApp/files/Pictures");
        ArrayList<String> photos = new ArrayList<String>();
        if (file.listFiles() != null){
            ArrayList<File> fList = new ArrayList<File>(Arrays.asList(Objects.requireNonNull(file.listFiles())));
            // Functional lambda expression
            fList.forEach(f -> {
                if (((startTimestamp == null && endTimestamp == null) || (f.lastModified() >= startTimestamp.getTime()
                        && f.lastModified() <= endTimestamp.getTime())
                ) && (keywords.equals("") || f.getPath().contains(keywords)))
                    photos.add(f.getPath());
            });
        }
        return photos;
    }

    public interface View {
        void updatePhoto(String path, String caption);
    }
}


