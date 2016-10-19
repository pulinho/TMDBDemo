package cz.pule.tmdbdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

/**
 * Created by pule on 16.10.2016.
 */

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {

    private static final String TAG = "MovieListAdapter";
    private static final String IMG_BASE_URL = "https://image.tmdb.org/t/p/w500";

    private ArrayList<MovieDetails> dataSet;
    private ImageLoader imageLoader;
    private MovieListLoader movieListLoader;
    private Context context;
    private RecyclerView recyclerView;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final NetworkImageView networkImageView;

        public ViewHolder(View v) {
            super(v);
            textView = (TextView) v.findViewById(R.id.myTextView);
            networkImageView = (NetworkImageView) v.findViewById(R.id.networkImageView);
        }

        public TextView getTextView() {
            return textView;
        }

        public NetworkImageView getNetworkImageView() {
            return networkImageView;
        }
    }

    public MovieListAdapter(Context context) {
        this.context = context;
        this.recyclerView = (RecyclerView)((Activity)context).findViewById(R.id.myRecyclerView);

        dataSet = new ArrayList<>();
        imageLoader = VolleySingleton.getInstance(context).getImageLoader();
    }

    @Override
    public MovieListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int itemPosition = recyclerView.getChildLayoutPosition(view);
                Log.d(TAG, "item " + itemPosition + " clicked");
                viewMovieDetails(itemPosition);
            }
        });

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        Log.d(TAG, "Element " + position + " set - " + dataSet.get(position).getTitle());

        if(movieListLoader != null) movieListLoader.notifyViewHolderPosition(position);

        viewHolder.getTextView().setText(dataSet.get(position).getTitle());
        viewHolder.getNetworkImageView().setDefaultImageResId(R.drawable.no_poster);

        String posterPath = dataSet.get(position).getPosterPath();

        //next line should accomplish that requests for 'null' posters won't be sent. But then the
        //'no_poster' image was replaced with some random other poster for some reason

        //if(posterPath.equals("null")) return;

        String imgUrl = IMG_BASE_URL + posterPath;

        viewHolder.getNetworkImageView().setErrorImageResId(R.drawable.no_poster);
        viewHolder.getNetworkImageView().setImageUrl(imgUrl, imageLoader);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public void addItemToList(MovieDetails details){
        dataSet.add(details);
        notifyItemInserted(dataSet.size() - 1);
    }

    public void clearList(){
        dataSet.clear();
        notifyDataSetChanged();
    }

    public void setMovieListLoader(MovieListLoader movieListLoader) {
        this.movieListLoader = movieListLoader;
    }

    private void viewMovieDetails(int position){
        Intent intent = new Intent(context, DisplayMovieActivity.class);

        MovieDetails md = dataSet.get(position);

        if(md.getPosterPath().equals("null")) intent.putExtra(DisplayMovieActivity.IMG_URL, "null");
        else intent.putExtra("IMG_URL", IMG_BASE_URL + md.getPosterPath());

        intent.putExtra(DisplayMovieActivity.TITLE, md.getTitle());
        intent.putExtra(DisplayMovieActivity.OVERVIEW, md.getOverview());

        context.startActivity(intent);
    }
}
