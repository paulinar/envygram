package com.paulina.envygram;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

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

        ViewHolder viewHolder;

        // 1. Get data item for this position
        InstagramPhoto photo = getItem(position);

        // 2. Check if using recycled view; if not, need to inflate (e.g. taking an XML file and turning it into an actual View we can use in our application)
        if (convertView == null) {
            // create a new view from the template
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent, false); // false = don't attach to parent i.e. container just yet

            // set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.tvCaption = (TextView) convertView.findViewById(R.id.tvCaption);
            viewHolder.ivPhoto = (ImageView) convertView.findViewById(R.id.ivPhoto);
            viewHolder.tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
            viewHolder.ivProfilePic = (ImageView) convertView.findViewById(R.id.ivProfilePic);
            viewHolder.tvNumEnvies = (TextView) convertView.findViewById(R.id.tvNumEnvies);
            viewHolder.tvHeart = (TextView) convertView.findViewById(R.id.tvHeart);
            viewHolder.tvLatestCommentText = (TextView) convertView.findViewById(R.id.tvLatestCommentText);
            viewHolder.tvLatestCommentUser = (TextView) convertView.findViewById(R.id.tvLatestCommentUser);

            // store the holder with the view
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

//        // 3. Look up views for populating the data (image, caption)
//        TextView tvCaption = (TextView) convertView.findViewById(R.id.tvCaption);
//        ImageView ivPhoto = (ImageView) convertView.findViewById(R.id.ivPhoto);
//        TextView tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
//        ImageView ivProfilePic = (ImageView) convertView.findViewById(R.id.ivProfilePic);
//        TextView tvNumEnvies = (TextView) convertView.findViewById(R.id.tvNumEnvies);
//        TextView tvHeart = (TextView) convertView.findViewById(R.id.tvHeart);
//        TextView tvLatestCommentText = (TextView) convertView.findViewById(R.id.tvLatestCommentText);
//        TextView tvLatestCommentUser = (TextView) convertView.findViewById(R.id.tvLatestCommentUser);

        // 4. Insert the Model data into each of the View items
        viewHolder.tvCaption.setText(photo.caption);

        // clear out the image view (because you could be using a recycled item)
        viewHolder.ivPhoto.setImageResource(0);
        viewHolder.ivProfilePic.setImageResource(0);

        // rounded border transformation
        Transformation roundedBorder = new RoundedTransformationBuilder()
                .cornerRadiusDp(30)
                .oval(false)
                .build();

        // insert image view using Picasso
        Picasso.with(getContext())
               .load(photo.imageUrl)
               .placeholder(R.drawable.placeholderimage)
               .fit()
               .into(viewHolder.ivPhoto);

        Picasso.with(getContext())
               .load(photo.profilePicUrl)
               .placeholder(R.drawable.placeholderprofile)
               .resize(100, 100)
               .transform(roundedBorder)
               .into(viewHolder.ivProfilePic);

        viewHolder.tvUsername.setText(photo.username);
        viewHolder.tvNumEnvies.setText(String.valueOf(photo.likesCount));
        viewHolder.tvHeart.setText( "" + (char) 0x2764 );
        viewHolder.tvLatestCommentText.setText(photo.comment.text);
        viewHolder.tvLatestCommentUser.setText(photo.comment.user);

        // 5. Return the created item as a View
        return convertView;
    }
}
