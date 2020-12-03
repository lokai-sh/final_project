package com.example.photoApp.presenter;

import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;

import com.example.photoApp.model.Picture;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class MainActivityPresenter {

private final View view;

    public MainActivityPresenter(View view) {
        this.view = view;
    }

    //public void updatePicture(Picture picture, String caption, ArrayList<Picture> pictures, int index) {
    public void updatePicture(Picture picture, String caption) {
        String path = picture.getFilepath();
        String[] attr = path.split("_");
        if (attr.length >= 3) {
            File to = new File(attr[0] + "_" + caption + "_" + attr[2] + "_" + attr[3]);
            File from = new File(path);
            from.renameTo(to);
            //pictures.get(index).setFilepath(to.toString());
            picture.setFilepath(to.toString());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Picture> findPictures(Date startTimestamp, Date endTimestamp, String keywords) {
        File file = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath(), "/Android/data/com.example.photoApp/files/Pictures");
        ArrayList<Picture> pictures = new ArrayList<Picture>();
        if (file.listFiles() != null){
            ArrayList<File> fList = new ArrayList<File>(Arrays.asList(Objects.requireNonNull(file.listFiles())));
            // Functional lambda expression
            fList.forEach(f -> {
                if (((startTimestamp == null && endTimestamp == null) || (f.lastModified() >= startTimestamp.getTime()
                        && f.lastModified() <= endTimestamp.getTime())
                ) && (keywords.equals("") || f.getPath().toUpperCase().contains(keywords.toUpperCase())))
                    pictures.add(new Picture(f.getPath()));
            });
        }
        return pictures;
    }

    public interface View {
        //void updatePicture(String path, String caption);
        void updatePicture(Picture picture, String caption);
    }
}


