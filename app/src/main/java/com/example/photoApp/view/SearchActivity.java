package com.example.photoApp.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.photoApp.LogAnnotation;
import com.example.photoApp.R;
import com.example.photoApp.presenter.SearchActivityPresenter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity implements SearchActivityPresenter.View{
    private SearchActivityPresenter sPresenter;

    @LogAnnotation
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        sPresenter = new SearchActivityPresenter(this);

        try {
            sPresenter.setupCalendarDates();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void cancel(final View v) {
        finish();
    }

    @LogAnnotation
    public void go(final View v) {
        Intent i = new Intent();
        EditText from = (EditText) findViewById(R.id.etFromDateTime);
        EditText to = (EditText) findViewById(R.id.etToDateTime);
        EditText keywords = (EditText) findViewById(R.id.etKeywords);
        i.putExtra("STARTTIMESTAMP", from.getText() != null ? from.getText().toString() : "");
        i.putExtra("ENDTIMESTAMP", to.getText() != null ? to.getText().toString() : "");
        i.putExtra("KEYWORDS", keywords.getText() != null ? keywords.getText().toString() : "");
        setResult(RESULT_OK, i);
        finish();
    }


    @LogAnnotation
    @Override
    public void setFromAndToDates(Date yesterday, Date today) {
        ((EditText) findViewById(R.id.etFromDateTime)).setText(new SimpleDateFormat(
        "yyyy‐MM‐dd hh:mm", Locale.getDefault()).format(yesterday));
        ((EditText) findViewById(R.id.etToDateTime)).setText(new SimpleDateFormat(
        "yyyy‐MM‐dd hh:mm", Locale.getDefault()).format(today));
    }
}


