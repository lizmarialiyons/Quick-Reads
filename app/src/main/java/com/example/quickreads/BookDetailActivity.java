package com.example.quickreads;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class BookDetailActivity extends AppCompatActivity {
    private ImageView coverImageView;
    private TextView titleTextView, authorsTextView, ratingTextView, summaryTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        coverImageView = findViewById(R.id.coverImageView);
        titleTextView = findViewById(R.id.titleTextView);
        authorsTextView = findViewById(R.id.authorsTextView);
        ratingTextView = findViewById(R.id.ratingTextView);
        summaryTextView = findViewById(R.id.summaryTextView);

        // Get data from intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String authors = intent.getStringExtra("authors");
        String rating = intent.getStringExtra("rating");
        String summary = intent.getStringExtra("summary");
        String coverImageUrl = intent.getStringExtra("coverImageUrl");

        // Set data to views
        titleTextView.setText(title);
        authorsTextView.setText("Authors: " + authors);
        ratingTextView.setText("Rating: " + rating);
        summaryTextView.setText(summary);

        // Load cover image using Picasso or Glide
        if (!coverImageUrl.isEmpty()) {
            Picasso.get().load(coverImageUrl).into(coverImageView);
        } else {
            coverImageView.setImageResource(R.drawable.placeholder_image);
        }
    }
}
