package com.example.quickreads;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HomeActivity extends AppCompatActivity {

    private EditText searchBar;
    private ImageView searchIcon, profileButton;
    private Button logoutButton;
    private FirebaseAuth mAuth;

    // Google Books API key (if needed)
    private final String API_KEY = "AIzaSyByT_RypLIMvVJnInryn1yq7dZyUqSdUkU";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        searchBar = findViewById(R.id.searchBar);
        searchIcon = findViewById(R.id.searchButton);
        profileButton = findViewById(R.id.profileButton);
        logoutButton = findViewById(R.id.logoutButton);

        // Set search icon click listener
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchBar.getText().toString().trim();
                if (!query.isEmpty()) {
                    // Trigger the search
                    new FetchBooksTask().execute(query);
                } else {
                    Toast.makeText(HomeActivity.this, "Please enter a search term", Toast.LENGTH_SHORT).show();
                }
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ProfileActivity
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        // Set logout button click listener
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Toast.makeText(HomeActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                // Return to login screen
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Close the home activity
            }
        });
    }

    // AsyncTask to fetch books from Google Books API
    private class FetchBooksTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... queries) {
            String query = queries[0];
            String jsonResponse = "";
            try {
                // Build the API request URL
                String apiUrl = "https://www.googleapis.com/books/v1/volumes?q=" + query + "&key=" + API_KEY;
                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                // Read the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                jsonResponse = stringBuilder.toString();
                reader.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.isEmpty()) {
                Toast.makeText(HomeActivity.this, "No books found", Toast.LENGTH_SHORT).show();
            } else {
                // Parse the JSON response
                parseBooksJson(result);
            }
        }
    }

    // Method to parse the JSON response and extract book details
    private void parseBooksJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray items = jsonObject.getJSONArray("items");

            JSONArray bookDetailsArray = new JSONArray();

            for (int i = 0; i < items.length(); i++) {
                JSONObject book = items.getJSONObject(i).getJSONObject("volumeInfo");

                // Create a JSON object to hold book details
                JSONObject bookDetails = new JSONObject();
                bookDetails.put("title", book.optString("title"));
                bookDetails.put("authors", book.has("authors") ? book.getJSONArray("authors").join(", ") : "Unknown Author");
                bookDetails.put("rating", book.has("averageRating") ? book.getString("averageRating") : "No rating");
                bookDetails.put("summary", book.has("description") ? book.getString("description") : "No summary available");
                String coverImageUrl = book.has("imageLinks") ? book.getJSONObject("imageLinks").optString("thumbnail") : "";
                bookDetails.put("coverImageUrl", coverImageUrl);
                // Add the book details to the array
                bookDetailsArray.put(bookDetails);
            }

            // Pass the JSON array to ResultsActivity
            Intent intent = new Intent(HomeActivity.this, ResultsActivity.class);
            intent.putExtra("booksJson", bookDetailsArray.toString());
            startActivity(intent);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(HomeActivity.this, "Error parsing book details", Toast.LENGTH_SHORT).show();
        }
    }
}