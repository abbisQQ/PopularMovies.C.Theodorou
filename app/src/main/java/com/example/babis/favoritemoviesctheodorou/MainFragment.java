package com.example.babis.favoritemoviesctheodorou;



import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;


import static com.example.babis.favoritemoviesctheodorou.R.id.my_grid_view;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    int width;
    ImageAdapter adapter;
    GridView gridview;
    static ArrayList<String> posters, ratings, titles, overviews, dates;



    //Enter your API key Here
    //!!!!!!!!!!!!!!!!!!!!!!!
    //!!!!!!!!!!!!!!!!!!!!!!!
    final String API_KEY = "";
    //!!!!!!!!!!!!!!!!!!!!!!!
    //!!!!!!!!!!!!!!!!!!!!!!!
    //!!!!!!!!!!!!!!!!!!!!!!!



    static boolean sortByPop = true;



    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_blank, container, false);

        ArrayList<String> array = new ArrayList<>();
        adapter = new ImageAdapter(getActivity(), array, width);
        gridview = (GridView) rootView.findViewById(my_grid_view);

        //Handler on clicks in the grid-view
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Intent intent = new Intent(getActivity(),MovieDetailsActivity.class)
                        .putExtra("overview",overviews.get(position))
                        .putExtra("rating",ratings.get(position))
                        .putExtra("title",titles.get(position))
                        .putExtra("date",dates.get(position))
                        .putExtra("poster",posters.get(position));

                startActivity(intent);


            }
        });


        WindowManager wm =(WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        if(MainActivity.TABLET)
        {
            width = size.x/6;
        }
        else width=size.x/2;
        if(getActivity()!=null)
        {
            ImageAdapter adapter = new ImageAdapter(getActivity(),array,width);

            gridview.setColumnWidth(width);
            gridview.setAdapter(adapter);
        }

        return rootView;

    }



    @Override
    public void onStart() {
        super.onStart();

        if(isNetworkAvailable())
        {
            new ImageLoadTask().execute();
        }
        else{
            Toast.makeText(getContext(),R.string.no_internet,Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);

    }



    public boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo !=null &&activeNetworkInfo.isConnected();
    }



    public class ImageLoadTask extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            while(true){
                try{
                    posters = new ArrayList(Arrays.asList(getPathsFromAPI(sortByPop)));
                    return posters;
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }

        }
        @Override
        protected void onPostExecute(ArrayList<String>result)
        {
            if(result!=null && getActivity()!=null)
            {
                ImageAdapter adapter = new ImageAdapter(getActivity(),result, width);
                gridview.setAdapter(adapter);

            }
        }
        String[] getPathsFromAPI(boolean sortbypop)
        {
            while(true)
            {
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String JSONResult;

                try {
                    String urlString;
                    if (sortbypop) {
                        urlString = "http://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY;
                    } else {
                        urlString = "http://api.themoviedb.org/3/movie/top_rated?api_key=" + API_KEY;
                    }
                    URL url = new URL(urlString);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    //Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() == 0) {
                        return null;
                    }
                    JSONResult = buffer.toString();

                    try {

                        overviews = new ArrayList<>(Arrays.asList(getMovieDetailsFromJSON(JSONResult,"overview")));
                        dates = new ArrayList<>(Arrays.asList(getMovieDetailsFromJSON(JSONResult,"release_date")));
                        ratings = new ArrayList<>(Arrays.asList(getMovieDetailsFromJSON(JSONResult,"vote_average")));
                        titles = new ArrayList<>(Arrays.asList(getMovieDetailsFromJSON(JSONResult,"original_title")));

                        return getPathsFromJSON(JSONResult);

                    } catch (JSONException e) {
                        return null;
                    }
                }catch(Exception e)
                {
                    e.printStackTrace();
                }finally {
                    if(urlConnection!=null)
                    {
                        urlConnection.disconnect();
                    }
                    if(reader!=null)
                    {
                        try{
                            reader.close();
                        }catch(final IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }


            }
        }

        String[] getMovieDetailsFromJSON(String JSONStringParam, String param) throws JSONException{
            JSONObject JSONString = new JSONObject(JSONStringParam);

            JSONArray moviesArray = JSONString.getJSONArray("results");
            String[] result = new String[moviesArray.length()];

            for(int i = 0; i<moviesArray.length();i++)
            {
                JSONObject movie = moviesArray.getJSONObject(i);

                if(param.equals("vote_average")) {
                    Double movieRating = movie.getDouble("vote_average");
                    String data =  Double.toString(movieRating)+"/10";
                    result[i]=data;
                }else {
                    // instead of the poster_path this time we can get everything else using the second parameter param
                    String data = movie.getString(param);
                    result[i] = data;
                }
            }
            return result;
        }
        }

        String[] getPathsFromJSON(String JSONStringParam) throws JSONException{

            JSONObject JSONString = new JSONObject(JSONStringParam);

            JSONArray moviesArray = JSONString.getJSONArray("results");
            String[] result = new String[moviesArray.length()];

            for(int i = 0; i<moviesArray.length();i++)
            {
                JSONObject movie = moviesArray.getJSONObject(i);
                String moviePath = movie.getString("poster_path");
                result[i] = moviePath;
            }
            return result;
        }
    }
































