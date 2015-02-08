package com.paulina.randogram;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by pramos on 2/7/15.
 */
public class InstagramPhotosAdapter extends ArrayAdapter<InstagramPhoto> {
    // What data do we need from the activity
    // - Context: the activity itself. How the adapter knows which view owns it)
    // - Resource: if we want the activity to tell us which layout we want to use for our item, but in this case, we know the layout
    // - Objects: the data source
    public InstagramPhotosAdapter(Context context, List<InstagramPhoto> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    // What our item looks like
    // Use the xml template to display each photo
    // getView() gets automatically called by the ListView
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Get data item for this position
        InstagramPhoto photo = getItem(position);

        // 2. Check if using recycled view; if not, need to inflate (e.g. taking an XML file and turning it into an actual View we can use in our application)
        if (convertView == null) {
            // create a new view from the template
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent, false); // false = don't attach to parent i.e. container just yet
        }

        // 3. Look up views for populating the data (image, caption)
        TextView tvCaption = (TextView) convertView.findViewById(R.id.tvCaption);
        ImageView ivPhoto = (ImageView) convertView.findViewById(R.id.ivPhoto);

        // 4. Insert the Model data into each of the View items
        tvCaption.setText(photo.caption);
        // clear out the image view (because you could be using a recycled item)
        ivPhoto.setImageResource(0);
        // insert image view using Picasso
        Picasso.with(getContext())
               .load(photo.imageUrl)
               .into(ivPhoto);

//        Picasso.with(getContext())
//                .load(photo.imageUrl)
//                .fit()
//                .centerCrop()
//                .placeholder(R.drawable.ic_launcher)
//                .into(ivPhoto);

        // 5. Return the created item as a View
        return convertView;
    }
}
