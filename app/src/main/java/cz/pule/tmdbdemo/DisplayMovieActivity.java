package cz.pule.tmdbdemo;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class DisplayMovieActivity extends AppCompatActivity {

    public final static String TITLE = "TITLE";
    public final static String IMG_URL = "IMG_URL";
    public final static String OVERVIEW = "OVERVIEW";

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

        String movieTitle = intent.getStringExtra(TITLE);
        ab.setTitle(movieTitle);

        String imgUrl = intent.getStringExtra(IMG_URL);
        if(!imgUrl.equals("null")){
            NetworkImageView fullImageView = (NetworkImageView) findViewById(R.id.fullImageView);
            fullImageView.setImageUrl(imgUrl, imageLoader);
        }

        String overview = intent.getStringExtra(OVERVIEW);
        if(!overview.equals("null")){
            TextView overviewTextView = (TextView) findViewById(R.id.overView);
            overviewTextView.setText(overview);
        }
    }
}
