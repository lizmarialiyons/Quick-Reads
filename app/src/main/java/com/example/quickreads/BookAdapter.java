package com.example.quickreads;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private JSONArray booksArray;
    private Context context;

    public BookAdapter(Context context, JSONArray booksArray) {
        this.context = context;
        this.booksArray = booksArray;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        try {
            JSONObject book = booksArray.getJSONObject(position);
            String title = book.getString("title");
            String authors = book.getString("authors");
            String rating = book.getString("rating");
            String summary = book.getString("summary");
            String coverImageUrl = book.getString("coverImageUrl");

            holder.titleTextView.setText(title);
            holder.authorsTextView.setText("Authors: " + authors);
            holder.ratingTextView.setText("Rating: " + rating);
            holder.summaryTextView.setText(summary);


            // Log the URL for debugging
            Log.d("BookAdapter", "Cover Image URL: " + coverImageUrl);

            // Convert URL to HTTPS if necessary
            if (coverImageUrl != null && coverImageUrl.startsWith("http://")) {
                coverImageUrl = coverImageUrl.replace("http://", "https://");
            }

            // Load cover image with Picasso, using placeholder in case of an empty URL
            if (coverImageUrl != null && !coverImageUrl.isEmpty()) {
                Picasso.get()
                        .load(coverImageUrl)
                        .placeholder(R.drawable.placeholder_image)
                        .into(holder.coverImageView);
            } else {
                holder.coverImageView.setImageResource(R.drawable.placeholder_image);
            }


            String finalCoverImageUrl = coverImageUrl;
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, BookDetailActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("authors", authors);
                intent.putExtra("rating", rating);
                intent.putExtra("summary", summary);
                intent.putExtra("coverImageUrl", finalCoverImageUrl);
                context.startActivity(intent);
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return booksArray.length();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView coverImageView;
        TextView titleTextView, authorsTextView, ratingTextView, summaryTextView;

        public BookViewHolder(View itemView) {
            super(itemView);
            coverImageView = itemView.findViewById(R.id.bookCoverImageView);
            titleTextView = itemView.findViewById(R.id.bookTitleTextView);
            authorsTextView = itemView.findViewById(R.id.bookAuthorsTextView);
            ratingTextView = itemView.findViewById(R.id.bookRatingTextView);
            summaryTextView = itemView.findViewById(R.id.bookSummaryTextView);
        }
    }
}
