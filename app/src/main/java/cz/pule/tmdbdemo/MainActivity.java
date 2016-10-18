package cz.pule.tmdbdemo;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";

    private RecyclerView recyclerView;
    private MovieListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private MovieListLoader movieListLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(myToolbar);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_toggle_log:
                showDaysPickDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDaysPickDialog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.days_pick_dialog, (ViewGroup) findViewById(R.id.dialog_root_element));
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(layout)
                .setTitle(R.string.dialog_title)
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        SeekBar sb = (SeekBar)layout.findViewById(R.id.dialog_seekbar);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                Log.d(TAG, "Seekbar progress: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
