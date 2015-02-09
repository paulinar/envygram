package com.paulina.envygram;

import android.app.ActionBar;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PhotosActivity extends ActionBarActivity {

    public static final String CLIENT_ID = "fcf5dc6f63774c73bc45a4c3d1da61ac";
    private ArrayList<InstagramPhoto> photos;
    private InstagramPhotosAdapter aPhotos;

    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        // custom action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar_layout);
        TextView tvActionBarTitle = (TextView)findViewById(R.id.tvActionBarTitle);
        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/billabong.ttf");
        tvActionBarTitle.setTypeface(customFont);

        /* for pull-to-refresh feature */
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false) once the network request has completed successfully.
                fetchPopularPhotos();
                if (swipeContainer != null) {
                    swipeContainer.setRefreshing(false);
                }
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        photos = new ArrayList<>();

        // 1. Create the adapter ,linking it to the source
        aPhotos = new InstagramPhotosAdapter(this, photos);

        // 2. Find the ListView from the layout
        ListView lvPhotos = (ListView) findViewById(R.id.lvPhotos);

        // 3. Set the adapter, binding it to the ListView
        lvPhotos.setAdapter(aPhotos);

        // SEND OUT API REQUEST to POPULAR PHOTOS
        fetchPopularPhotos();

    }

    // Trigger API request
    private void fetchPopularPhotos() {
        /*
            - Client ID:
            - Popular w/ access token: https://api.instagram.com/v1/media/popular?access_token=ACCESS-TOKEN
            - Popular w/ client ID: https://api.instagram.com/v1/media/popular?client_id=CLIENT-ID
        */

        String url = "https://api.instagram.com/v1/media/popular?client_id=" + CLIENT_ID;

        // Create the network client
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, null, new JsonHttpResponseHandler() {

            // will either call: onSuccess (worked, 200) or onFailure (fail)

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // Expecting a JSON object response, not an array
                // Type:        { “data” => [x] => “type” } (“image” or “video”)
                // URL:         { “data” => [x] => “images” => “Standard_resolution” => “url” }
                // Caption:     { “data” => [x] => “caption” => “text” }
                // Author Name: { “data” => [x] => “user” => “username” }

//                Log.i("DEBUG", response.toString());



                // Iterate each of the photo items and decode the item into a Java object
                JSONArray photosJSON = null;
                try {
                    photosJSON = response.getJSONArray("data"); // pass in KEY=data to get array of posts

                    // iterate array of posts
                    for (int i = 0; i < photosJSON.length(); i++) {

                        // get the JSON object at that position in the array
                        JSONObject photoJSON = photosJSON.getJSONObject(i);

                        // decode the attributes of the JSON into a data model
                        InstagramPhoto photo = new InstagramPhoto();
                        photo.username = photoJSON.getJSONObject("user").getString("username");
                        if (photoJSON.optJSONObject("caption") != null) {
                            photo.caption = photoJSON.getJSONObject("caption").getString("text");
                        }
                        photo.type = photoJSON.getString("type");
                        photo.imageUrl = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                        photo.imageHeight = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getInt("height");
                        photo.likesCount = photoJSON.getJSONObject("likes").getInt("count");
                        photo.profilePicUrl = photoJSON.getJSONObject("user").getString("profile_picture");

                        InstagramComment comment = new InstagramComment();

                        if (photoJSON.optJSONObject("comments") != null) {
                            JSONArray commentsArray = photoJSON.getJSONObject("comments").getJSONArray("data");
                            int latestCommentIndex = commentsArray.length() - 1;
                            JSONObject latestComment = commentsArray.getJSONObject(latestCommentIndex);
                            comment.text = latestComment.getString("text");
                            comment.user = latestComment.getJSONObject("from").getString("username");
                            photo.comment = comment;
                        }

                        // add decoded object to the photos array
                        photos.add(photo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // callback
                // after we've added all of our photos, it'll trigger the ListView and the adapter to refresh & all of the items will show up
                aPhotos.notifyDataSetChanged();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.getStackTraceString(throwable);
            }
        });

        photos.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
