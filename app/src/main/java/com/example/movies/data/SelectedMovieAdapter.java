package com.example.movies.data;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movies.R;
import com.example.movies.activities.MovieActivity;
import com.example.movies.model.Movie;
import com.example.movies.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SelectedMovieAdapter extends RecyclerView.Adapter<SelectedMovieAdapter.SelectedMovieViewHolder>{

    private Context context;
    private ArrayList<Movie> movies;

    public SelectedMovieAdapter(Context context, ArrayList<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @NonNull
    @Override
    public SelectedMovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_item, parent, false);
        return new SelectedMovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedMovieViewHolder holder, int position) {
        Movie currentMovie = movies.get(position);

        if(currentMovie != null){
            String title = currentMovie.getTitle();
            String year = currentMovie.getYear();
            String posterUrl = currentMovie.getPosterUrl();
            String imdbId = currentMovie.getImdbId();


            holder.titleTextView.setText(title);
            holder.yearTextView.setText(year);
            Picasso.get().load(posterUrl).
                    fit().centerInside().into(holder.posterImageView);
        }
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class SelectedMovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        ImageView posterImageView;
        TextView titleTextView;
        TextView yearTextView;

        public SelectedMovieViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            posterImageView = itemView.findViewById(R.id.poster);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            yearTextView = itemView.findViewById(R.id.yearTextView);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Movie clickedMovie = movies.get(position);
            Intent intent = new Intent(context, MovieActivity.class);
            intent.putExtra(Utils.MOVIE_IMDBID_EXTRA, clickedMovie.getImdbId());
            intent.putExtra(Utils.MOVIE_CONTEXT_EXTRA, false);
            context.startActivity(intent);
        }

    }
}
