package com.fsck.k9;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.fsck.k9.activity.PickKey;

import java.util.ArrayList;

/**
 * Created by theon on 4/24/2016.
 */
public class CustomAdapter extends ArrayAdapter {
    private ArrayList<PickKey.Element> list;
    private static final String KEYFILE = "MyKeyfile";
    public CustomAdapter(Context context, ArrayList<PickKey.Element> objects) {
        super(context, R.layout.key_single, objects);
        list=objects;
    }

    public View getView(final int position, final View convertView, final ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View customView = inflater.inflate(R.layout.key_single,parent,false);
        TextView privateKey = (TextView) customView.findViewById(R.id.private_key);
        TextView publicKey = (TextView) customView.findViewById(R.id.public_key);
        Button deleteKey = (Button) customView.findViewById(R.id.delete_key);
        final TextView title = (TextView) customView.findViewById(R.id.title_key);
        deleteKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.remove(position);
                SharedPreferences.Editor editor = getContext().getSharedPreferences(KEYFILE,getContext().MODE_PRIVATE).edit();
                editor.remove(title.getText().toString());
                editor.apply();
                CustomAdapter.this.notifyDataSetChanged();
            }
        });
        PickKey.Element key = (PickKey.Element) getItem(position);
        title.setText(key.name);
        privateKey.setText(key.private_key);
        publicKey.setText(key.public_key);
        return customView;
    }
}
