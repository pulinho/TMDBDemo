package cz.pule.tmdbdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MovieListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private MovieListLoader movieListLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VolleySingleton.getInstance(getApplicationContext()); //init

        recyclerView = (RecyclerView) findViewById(R.id.myRecyclerView);
        
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MovieListAdapter();
        recyclerView.setAdapter(adapter);

        movieListLoader = new MovieListLoader(adapter);
        adapter.setMovieListLoader(movieListLoader);

        movieListLoader.newQuery(1);
    }
}
