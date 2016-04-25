package com.fsck.k9;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by theon on 4/24/2016.
 */
public class CustomAdapter extends ArrayAdapter {

    public CustomAdapter(Context context, ArrayList<Pair<String, String>> objects) {
        super(context, R.layout.key_single, objects);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.key_single,parent,false);
        TextView privateKey = (TextView) customView.findViewById(R.id.private_key);
        TextView publicKey = (TextView) customView.findViewById(R.id.public_key);
        Pair<String,String> key = (Pair<String, String>) getItem(position);
        privateKey.setText(key.first);
        publicKey.setText(key.second);
        return customView;
    }
}
