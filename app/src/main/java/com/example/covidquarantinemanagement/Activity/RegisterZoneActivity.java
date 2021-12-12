package com.example.covidquarantinemanagement.Activity;

import androidx.appcompat.app.AppCompatActivity;
import com.example.covidquarantinemanagement.R;
import com.google.android.material.textfield.TextInputLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterZoneActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private String dvhcvn;
    private JSONArray dtb;
    private EditText regName, regStreet;
//    private TextInputLayout regCityLayout, regDistrictLayout, regWardLayout;
    private AutoCompleteTextView regCity, regDistrict, regWard;

    private HashMap<String, String> level1 = new HashMap<String, String>();
    private HashMap<String, String> level2 = new HashMap<String, String>();
    private ArrayList<String> level3 = new ArrayList<>();
    private HashMap<String, String> currentLevelName = new HashMap<>();

    private ArrayAdapter<String> cityAdapter, districtAdapter, wardAdapter;

    private void parseJson() {
        InputStream is = getResources().openRawResource(R.raw.dvhcvn);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            int n;
            while (true) {
                assert reader != null;
                if ((n = reader.read(buffer)) == -1) break;
                writer.write(buffer, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        dvhcvn = writer.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_zone);
        getSupportActionBar().show();

        parseJson();

//        regCityLayout = (TextInputLayout) findViewById(R.id.reg_city_layout);
        regCity = (AutoCompleteTextView) findViewById(R.id.reg_city);
//        regDistrictLayout = (TextInputLayout) findViewById(R.id.reg_district_layout);
        regDistrict = (AutoCompleteTextView) findViewById(R.id.reg_district);
//        regWardLayout = (TextInputLayout) findViewById(R.id.reg_ward_layout);
        regWard = (AutoCompleteTextView) findViewById(R.id.rec_ward);

        try {
            dtb = new JSONArray(dvhcvn);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            for (int i = 0; i < dtb.length(); i++) {
                JSONObject level1Object = dtb.getJSONObject(i);
                level1.put(level1Object.getString("name"),level1Object.getString("type").toLowerCase());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        cityAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item, level1.keySet().toArray(new String[0]));
        districtAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item, level2.keySet().toArray(new String[0]));
        wardAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item, level3);

        regCity.setAdapter(cityAdapter);
        regDistrict.setAdapter(districtAdapter);
        regWard.setAdapter(wardAdapter);

        regCity.setThreshold(2);
        regDistrict.setThreshold(2);
        regWard.setThreshold(2);

        regCity.setOnItemClickListener(this);
        regDistrict.setOnItemClickListener(this);
        regWard.setOnItemClickListener(this);

        Button submitButton = (Button) findViewById(R.id.rec_sumbit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(RegisterZoneActivity.this, MapsActivity.class);
//                i.putExtra("tokenSymbol", tokenSymbol);
                setResult(RESULT_OK, i);
                finish();

//                // Get the input text
//                tokenSymbol = tokenNameText.getText().toString();
//
//                // If existed, send back the token's symbol to MainActivity. Else keep "toasting"
//                if (TOKEN_LIST.contains(tokenSymbol.toUpperCase()) &&
//                        !existedList.contains(tokenSymbol.toUpperCase())) {
//                    Intent i = new Intent(RegisterZoneActivity.this,MapsActivity.class);
//                    i.putExtra("tokenSymbol", tokenSymbol);
//                    setResult(RESULT_OK, i);
//                    finish();
//                } else if (!TOKEN_LIST.contains(tokenSymbol.toUpperCase())) {
//                    Toast.makeText(getApplicationContext(), "Sorry the token isn't available in the database. Please try other token.", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(getApplicationContext(), "You've already saved this token.", Toast.LENGTH_SHORT).show();
//                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // fetch the user selected value
        String item = parent.getItemAtPosition(position).toString();

        // Get the current change
        currentLevelName.clear();

        if (level1.containsKey(item)) {
            currentLevelName.put(item, level1.get(item));
            Toast.makeText(RegisterZoneActivity.this, "Selected Item is: \t" + item + " at " + level1.get(item), Toast.LENGTH_LONG).show();
        }
        else if (level2.containsKey(item)) {
            currentLevelName.put(item, level2.get(item));
            Toast.makeText(RegisterZoneActivity.this, "Selected Item is: \t" + item + " at " + level2.get(item), Toast.LENGTH_LONG).show();
        }
        checkChange();
        System.out.println(currentLevelName);
        System.out.println(level1);
        System.out.println(level2);
        System.out.println(level3);
    }

    public void checkChange() {
        if (currentLevelName.containsValue("tỉnh") |
                currentLevelName.containsValue("thành phố trung ương")) {
            System.out.println("Update neeeeeeeeee");
            // Update level2
            regDistrict.setText("");
            level2.clear();
            for (int i = 0; i < dtb.length(); i++) {
                JSONObject level1Object = null;
                try {
                    level1Object = dtb.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    Map.Entry<String,String> entry = currentLevelName.entrySet().iterator().next();
                    if (level1Object.getString("name").equals(entry.getKey())) {
                        JSONArray level2Array = level1Object.getJSONArray("level2s");
                        for (int j = 0; j < level2Array.length(); j++) {
                            JSONObject level2Object = level2Array.getJSONObject(j);
                            level2.put(level2Object.getString("name"),level2Object.getString("type").toLowerCase());
                        }
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            districtAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item, level2.keySet().toArray(new String[0]));
            regDistrict.setAdapter(districtAdapter);
            districtAdapter.notifyDataSetChanged();
            // Clear level 3
            regWard.setText("");
            level3.clear();
        }  else if (currentLevelName.containsValue("quận") |
                currentLevelName.containsValue("huyện") |
                currentLevelName.containsValue("thành phố") |
                currentLevelName.containsValue("thị xã")) {
            System.out.println("Update 2 nefefefeef");
            // Update level3
            regWard.setText("");
            level3.clear();
            for (int i = 0; i < dtb.length(); i++) {
                JSONObject level1Object = null;
                try {
                    level1Object = dtb.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray level2Array = null;
                try {
                    level2Array = level1Object.getJSONArray("level2s");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int j = 0; j < level2Array.length(); j++) {
                    JSONObject level2Object = null;
                    try {
                        level2Object = level2Array.getJSONObject(j);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        Map.Entry<String,String> entry1 = currentLevelName.entrySet().iterator().next();
                        System.out.println(entry1.getKey());
                        System.out.println(level2Object.getString("name"));

                        if (level2Object.getString("name").equals(entry1.getKey())) {
                            JSONArray level3Array = level2Object.getJSONArray("level3s");
                            for (int k = 0; k < level3Array.length(); k++) {
                                JSONObject level3Object = level3Array.getJSONObject(k);
                                level3.add(level3Object.getString("name"));
                            }
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            wardAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item, level3);
            regWard.setAdapter(wardAdapter);
        }
    }
}