package cz.pule.tmdbdemo;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class DisplayMovieActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getResources().getBoolean(R.bool.tablet_layout))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_display_movie);

        Toolbar toolbar = (Toolbar) findViewById(R.id.displayMovieToolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        ImageLoader imageLoader = VolleySingleton.getInstance(getApplicationContext()).getImageLoader();

        Intent intent = getIntent();

        String imgUrl = intent.getStringExtra("IMG_URL");
        NetworkImageView fullImageView = (NetworkImageView) findViewById(R.id.fullImageView);
        fullImageView.setImageUrl(imgUrl, imageLoader);
    }
}
