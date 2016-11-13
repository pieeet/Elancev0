package com.rocdev.android.elancev0.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rocdev.android.elancev0.R;
import com.rocdev.android.elancev0.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by piet on 20-10-16.
 *
 */

public class UserListAdapter extends BaseAdapter {

    private ArrayList<User> users;
    private Context context;

    public UserListAdapter(ArrayList<User> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem;
        if (convertView == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.layout_user_listitem, null);
        } else {
            listItem = convertView;
        }
        User user = users.get(position);
        CircleImageView imgView = (CircleImageView) listItem.findViewById(R.id.userListImageView);

        // anders laadt verkeerde plaatje
        //http://stackoverflow.com/questions/25429683/picasso-loads-pictures-to-the-wrong-imageview-in-a-list-adapter
        Picasso.with(context).cancelRequest(imgView);
        TextView userNameTextView = (TextView) listItem.findViewById(R.id.userListTextView);
        if (user.getPhotoUrl() == null) {
            imgView.setImageDrawable(ContextCompat
                    .getDrawable(context,
                            R.drawable.ic_account_circle_black_36dp));
        } else {
            Picasso.with(context).load(user.getPhotoUrl())
                    .into(imgView);
        }
        userNameTextView.setText(user.getNaam() + " " + user.getAchternaam());


        return listItem;
    }
}
