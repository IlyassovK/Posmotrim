package com.example.movies.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.movies.R;
import com.example.movies.data.MovieAdapter;
import com.example.movies.model.Movie;
import com.example.movies.model.User;
import com.example.movies.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.internal.Util;

public class MovieActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference moviesDatabaseReference;
    private FirebaseAuth auth;

    private ImageView poster;
    private TextView title;
    private TextView year;
    private TextView genre;
    private TextView actors;
    private RequestQueue requestQueue;
    private FloatingActionButton addMovieButton;
    private ChildEventListener movieChildEventListener;

    String userName;
    String selectedMovieKey;

    Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        database = FirebaseDatabase.getInstance();
        moviesDatabaseReference = database.getReference().child("movies");

        auth = FirebaseAuth.getInstance();

        poster = findViewById(R.id.moviePosterUrl);
        title = findViewById(R.id.movieTitle);
        year = findViewById(R.id.movieYear);
        genre = findViewById(R.id.movieGenre);
        actors = findViewById(R.id.movieActors);
        addMovieButton = findViewById(R.id.addMovieButton);

        movie = new Movie();

        requestQueue = Volley.newRequestQueue(this);

        Intent intent = getIntent();
        String imdbId = intent.getStringExtra(Utils.MOVIE_IMDBID_EXTRA);
        Boolean fromMain = intent.getBooleanExtra(Utils.MOVIE_CONTEXT_EXTRA, true);

        if(!fromMain){
            addMovieButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.btn_minor)));
            addMovieButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_close_24));
        }else{
            addMovieButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.btn_main)));
            addMovieButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_add_24));
        }

        String url = "http://www.omdbapi.com/?i=" + imdbId + "&apikey=93688c95"; // http://www.omdbapi.com/?i=tt2975590&apikey=93688c95

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    String movieTitle = response.getString("Title");
                    String movieYear = response.getString("Year");
                    String moviePosterUrl = response.getString("Poster");
                    String movieGenre = response.getString("Genre");
                    String movieActors = response.getString("Actors");

                    Log.v("title", movieTitle);

                    title.setText(movieTitle);
                    Picasso.get().load(moviePosterUrl).
                            fit().centerInside().into(poster);
                    year.setText(movieYear);
                    genre.setText(movieGenre);
                    actors.setText(movieActors);

                    movie.setTitle(movieTitle);
                    movie.setYear(movieYear);
                    movie.setPosterUrl(moviePosterUrl);
                    movie.setImdbId(imdbId);
                    movie.setUserId(auth.getCurrentUser().getUid());


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(jsonObjectRequest);

        if(movieChildEventListener == null){
            movieChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    selectedMovieKey = snapshot.getKey();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };

            moviesDatabaseReference.addChildEventListener(movieChildEventListener);


            addMovieButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(fromMain){
                        moviesDatabaseReference.push().setValue(movie);
                    }else{
                        moviesDatabaseReference.child(selectedMovieKey).removeValue();
                    }
                    Intent intent = new Intent(MovieActivity.this, SearchActivity.class);
                    intent.putExtra(Utils.SELECTED_MOVIE_EXTRA, movie);
                    startActivity(intent);
                }
            });
        }
    }
}