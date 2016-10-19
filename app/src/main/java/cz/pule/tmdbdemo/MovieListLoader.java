package cz.pule.tmdbdemo;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by pule on 16.10.2016.
 */

public class MovieListLoader {

    private static final String TAG = "MovieListLoader";
    private static final String API_KEY = "ae30862a3e8dee879fd475e5b2453527";

    private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String CHANGES_BASE_URL = "https://api.themoviedb.org/3/movie/changes";

    private ArrayList<Integer> idList = new ArrayList<>();

    private MovieListAdapter adapter;

    private JsonObjectRequest pendingMovieDetailsRequest = null;
    private JsonObjectRequest pendingMovieListRequest = null;

    private int movieDetailsRequestsSent = 0;
    private int movieDetailsRequestsSuccess = 0;
    private int totalPages = 1;
    private int page = 0;
    private int days = 0;
    private String startDateString = "";
    private String endDateString = "";

    public MovieListLoader(MovieListAdapter adapter){
        this.adapter = adapter;
    }

    public void newQuery(int days){

        Log.d(TAG, "newQuery: " + days);

        if(this.days == days) return;
        this.days = days;

        if(pendingMovieDetailsRequest != null){
            pendingMovieDetailsRequest.cancel();
            pendingMovieDetailsRequest = null;
        }
        if(pendingMovieListRequest != null){
            pendingMovieListRequest.cancel();
            pendingMovieListRequest = null;
        }

        adapter.clearList();
        idList.clear();

        movieDetailsRequestsSent = 0;
        movieDetailsRequestsSuccess = 0;

        page = 0;
        totalPages = 1;

        //set start date and end date for query
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);

        Calendar calendar=Calendar.getInstance();
        endDateString = df.format(calendar.getTime());

        //substract number of days selected by user
        calendar.add(Calendar.DAY_OF_YEAR, -days);
        startDateString = df.format(calendar.getTime());

        Log.d(TAG, "start date: " + startDateString + ", end date: " + endDateString);

        loadNextPage();
    }

    private void loadNextPage(){

        if(page >= totalPages) return;
        page++;

        Log.d(TAG, "Loading page " + page + "/" + totalPages);

        String url = CHANGES_BASE_URL + "?api_key=" + API_KEY + "&start_date=" + startDateString
                   + "&end_date=" + endDateString + "&page=" + page;

        pendingMovieListRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        parseMovieListResponse(response);
                        pendingMovieListRequest = null;
                        requestNextMovieDetails();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Handle errors
                        Log.d(TAG, "Get movie list error");
                    }
                });

        VolleySingleton.getInstance(null).addToRequestQueue(pendingMovieListRequest);
    }

    private void parseMovieListResponse(JSONObject response){

        JSONArray array;
        try {
            array = response.getJSONArray("results");
            totalPages = response.getInt("total_pages");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        for(int i = 0 ; i < array.length() ; i++){
            try {
                idList.add(array.getJSONObject(i).getInt("id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void requestNextMovieDetails(){

        if(movieDetailsRequestsSent >= idList.size()){
            pendingMovieDetailsRequest = null;
            return;
        }
        int movieId = idList.get(movieDetailsRequestsSent);

        String url = MOVIE_BASE_URL + movieId + "?api_key=" + API_KEY;

        pendingMovieDetailsRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        parseMovieDetailsAndAddToAdapter(response);
                        movieDetailsRequestsSuccess++;
                        requestNextMovieDetails();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Handle errors
                        requestNextMovieDetails();
                    }
                });

        VolleySingleton.getInstance(null).addToRequestQueue(pendingMovieDetailsRequest);
        movieDetailsRequestsSent++;
    }

    private void parseMovieDetailsAndAddToAdapter(JSONObject response){

        MovieDetails details = new MovieDetails();

        try {
            details.setTitle(response.getString("title"));
            details.setPosterPath(response.getString("poster_path"));
            details.setOverview(response.getString("overview"));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        adapter.addItemToList(details);
    }

    // Gets current viewHolder position. If it's close enough to the list's current end and no other
    // items are being loaded, the loader will request next page of the query.
    public void notifyViewHolderPosition(int pos){
        if(movieDetailsRequestsSuccess -10 <= pos && pendingMovieDetailsRequest == null
                && pendingMovieListRequest==null)
            loadNextPage();
    }

    public int getDays() {
        return days;
    }
}
