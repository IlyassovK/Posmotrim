package com.example.movies.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.movies.R;
import com.example.movies.data.MovieAdapter;
import com.example.movies.data.SelectedMovieAdapter;
import com.example.movies.model.Movie;
import com.example.movies.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    EditText searchEditText;
    ImageButton searchButton;

    FirebaseDatabase database;
    private DatabaseReference moviesDatabaseReference;

    private FirebaseAuth auth;

    ArrayList<Movie> movies;

    RecyclerView recyclerView;
    SelectedMovieAdapter selectedMovieAdapter;
    ChildEventListener moviesChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        database = FirebaseDatabase.getInstance();
        moviesDatabaseReference = database.getReference().child("movies");

        auth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.selectedMoviesRecyclerView);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        movies = new ArrayList<>();
        selectedMovieAdapter = new SelectedMovieAdapter(SearchActivity.this, movies);
        recyclerView.setAdapter(selectedMovieAdapter);

        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(searchEditText.getText())){
                    Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                    intent.putExtra(Utils.MOVIE_NAME_EXTRA, String.valueOf(searchEditText.getText()));
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(), "Enter the movie name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(moviesChildEventListener == null){
            moviesChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Movie movie = snapshot.getValue(Movie.class);
                    if(movie.getUserId().equals(auth.getCurrentUser().getUid())){
                        movies.add(movie);
                        selectedMovieAdapter.notifyDataSetChanged();
                    }
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

            moviesDatabaseReference.addChildEventListener(moviesChildEventListener);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out:{
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(SearchActivity.this, SignInActivity.class));
                return true;
            } default:{
                return super.onOptionsItemSelected(item);
            }
        }
    }
}