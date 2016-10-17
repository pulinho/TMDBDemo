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
    private int movieDetailsRequestsSent = 0;

    public MovieListLoader(MovieListAdapter adapter){
        this.adapter = adapter;
        loadList();
    }

    private void loadList(){ //todo: start date, end date, pages

        String url = "https://api.themoviedb.org/3/movie/changes?api_key=" + API_KEY;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        parseResponse(response);
                        requestNextMovieDetails();
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

    //todo: E/Volley: [155] BasicNetwork.performRequest: Unexpected response code 404 for https://api.themoviedb.org/3/movie/421541?api_key=ae30862a3e8dee879fd475e5b2453527
    private void requestNextMovieDetails(){

        if(movieDetailsRequestsSent >= idList.size()) return;
        int movieId = idList.get(movieDetailsRequestsSent);

        String url = "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=" + API_KEY;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        parseMovieDetailsAndAddToAdapter(response);
                        movieDetailsRequestsSent++;
                        requestNextMovieDetails();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d(TAG, "Get movie details error");
                    }
                });

        VolleySingleton.getInstance(null).addToRequestQueue(jsObjRequest);
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
}
