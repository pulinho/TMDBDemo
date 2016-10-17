package cz.pule.tmdbdemo;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by pule on 16.10.2016.
 */

public class MovieListLoader {

    private static final String TAG = "MovieListLoader";
    private static final String API_KEY = "ae30862a3e8dee879fd475e5b2453527";

    private ArrayList<Integer> idList = new ArrayList<>();

    private MovieListAdapter adapter;
    private JsonObjectRequest pendingMovieDetailsRequest = null;

    private int movieDetailsRequestsSent = 0;
    private int movieDetailsRequestsSuccess = 0;
    private int totalPages = 1;
    private int page = 0;

    public MovieListLoader(MovieListAdapter adapter){
        this.adapter = adapter;
    }

    public void newQuery(int days){

        adapter.clearList();
        idList.clear();

        movieDetailsRequestsSent = 0;
        movieDetailsRequestsSuccess = 0;

        if(pendingMovieDetailsRequest != null){
            pendingMovieDetailsRequest.cancel();
            pendingMovieDetailsRequest = null; //?
        }

        page = 0;
        totalPages = 1;

        //set start date

        loadNextPage();
    }

    private void loadNextPage(){

        if(page >= totalPages) return;
        page++;

        Log.d(TAG, "Loading page " + page + "/" + totalPages);

        String url = "https://api.themoviedb.org/3/movie/changes?api_key=" + API_KEY + "&page=" + page;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        parseResponse(response);
                        if(pendingMovieDetailsRequest == null) requestNextMovieDetails(); // condition necessary?
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d(TAG, "Get movie list error");
                    }
                });

        VolleySingleton.getInstance(null).addToRequestQueue(jsObjRequest);
    }

    private void parseResponse(JSONObject response){

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

        String url = "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=" + API_KEY;

        pendingMovieDetailsRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        parseMovieDetailsAndAddToAdapter(response);
                        movieDetailsRequestsSent++;
                        movieDetailsRequestsSuccess++;
                        requestNextMovieDetails();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Handle errors
                        //Log.d(TAG, "Get movie details error");
                        movieDetailsRequestsSent++;
                        requestNextMovieDetails();
                    }
                });

        VolleySingleton.getInstance(null).addToRequestQueue(pendingMovieDetailsRequest);
    }

    private void parseMovieDetailsAndAddToAdapter(JSONObject response){

        MovieDetails details = new MovieDetails();

        try {
            details.setTitle(response.getString("title"));
            details.setPosterPath(response.getString("poster_path"));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        adapter.addItemToList(details);
    }

    public void compareMaxViewHolderPosition(int pos){
        if(movieDetailsRequestsSuccess - 1 == pos && pendingMovieDetailsRequest == null) //todo ... -10 <= pos
            loadNextPage();
    }
}
