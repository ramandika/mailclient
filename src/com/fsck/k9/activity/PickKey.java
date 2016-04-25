package com.fsck.k9.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.fsck.k9.CustomAdapter;
import com.fsck.k9.R;
import com.fsck.k9.crypto.ECCElgamal.ECCEG;
import com.fsck.k9.crypto.ECCElgamal.EllipticalCurve;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by theon on 4/24/2016.
 */
public class PickKey extends Activity {
    private Button keyGenerate;
    ArrayList<Pair<String,String>> listobj;
    ArrayAdapter<View> adapter;
    ListView lview;
    /*
      SharedPreference
     */
    private static final String KEYFILE = "MyKeyfile";
    private int NOKEY;
    private SharedPreferences spKey;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_key);
        keyGenerate = (Button) findViewById(R.id.generate_key);
        listobj = new ArrayList();

        //Load object
        spKey = getSharedPreferences(KEYFILE,MODE_PRIVATE);
        NOKEY = spKey.getInt("NOKEY",0);
        editor = spKey.edit();
        Set<String> stringSet;
        for(int i=1;;i++){
            stringSet = spKey.getStringSet("Key-"+i,null);
            if(stringSet==null) break;
            String[] publicPrivate = stringSet.toArray(new String[stringSet.size()]);
            listobj.add(new Pair<String, String>(publicPrivate[0],publicPrivate[1]));
        }
        adapter =new CustomAdapter(this,listobj);
        lview = (ListView) findViewById(R.id.listkey);
        lview.setAdapter(adapter);


        keyGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ECCEG elgamal = new ECCEG(EllipticalCurve.P192.equation,EllipticalCurve.P192.Prime,
                        EllipticalCurve.P192.basePoint, EllipticalCurve.P192.order);
                elgamal.generatePublicPrivateKeys();
                Set<String> keyset = new HashSet<String>();
                keyset.add(elgamal.getPrivateKey().toString());
                keyset.add("["+elgamal.getPublicKey().getX()+","+
                        elgamal.getPublicKey().getY()+"]");
                NOKEY++;
                editor.putStringSet("Key-"+NOKEY,keyset);
                editor.putInt("NOKEY",NOKEY);
                editor.commit();
                listobj.add(new Pair<String, String>(elgamal.getPrivateKey().toString(),"["+elgamal.getPublicKey().getX()+","+
                        elgamal.getPublicKey().getY()+"]"));
                adapter.notifyDataSetChanged();
            }
        });

        lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = getIntent();
                TextView private_key = (TextView)view.findViewById(R.id.private_key);
                TextView public_key = (TextView) view.findViewById(R.id.public_key);
                intent.putExtra("private_key",private_key.getText());
                intent.putExtra("public_key",public_key.getText());
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
}
