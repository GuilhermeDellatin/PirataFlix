package br.com.guilhermedellatin.pirataflix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.guilhermedellatin.pirataflix.model.Movie;

public class MainActivity extends AppCompatActivity {

    private MainAdapter mainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_main);

        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < 30; i++){
            Movie movie = new Movie();
            movie.setCoverUrl(R.drawable.movie);
            movies.add(movie);
        }

        mainAdapter = new MainAdapter(movies);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));//Formato que a recycler vai trabalhar
        recyclerView.setAdapter(mainAdapter); //O cara que vai gerenciar minha recycler view é esse adapter

    }

    private static class MovieHolder extends RecyclerView.ViewHolder{

        final ImageView imageViewCover;

        public MovieHolder(@NonNull View itemView) {
            super(itemView);//ItemView será o container principal do layout dinamico que vamos criar, especifico da celula
            imageViewCover= itemView.findViewById(R.id.image_view_cover);
        }
    }

    private class MainAdapter extends RecyclerView.Adapter<MovieHolder>{

        private final List<Movie> movies;

        private MainAdapter(List<Movie> movies) {
            this.movies = movies;
        }

        @NonNull
        @Override
        public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //Qual que é o layout xml que ele vai manipular, espera um ItemView
            return new MovieHolder(getLayoutInflater().inflate(R.layout.movie_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MovieHolder holder, int position) {//Sempre devolve um ViewHolder e a posição desse item
            Movie movie = movies.get(position);
            holder.imageViewCover.setImageResource(movie.getCoverUrl());
        }

        @Override
        public int getItemCount() { //Qual a quantidade de itens dessa coleção
            return movies.size();
        }
    }

}