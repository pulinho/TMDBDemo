package cz.pule.tmdbdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MovieListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private MovieListLoader movieListLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VolleySingleton.getInstance(getApplicationContext()); //init

        mRecyclerView = (RecyclerView) findViewById(R.id.myRecyclerView);
        
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MovieListAdapter();
        mRecyclerView.setAdapter(mAdapter);

        movieListLoader = new MovieListLoader(mAdapter);
    }
}
