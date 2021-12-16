package com.example.covidquarantinemanagement.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.L;
import com.example.covidquarantinemanagement.Adapter.InfoWindowAdapter;
import com.example.covidquarantinemanagement.Adapter.SearchAdapter;
import com.example.covidquarantinemanagement.Model.Zone;
import com.example.covidquarantinemanagement.R;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import java.util.List;
import java.util.Map;

// Reference https://www.youtube.com/watch?v=9xpvAjirN2s
public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private RecyclerView recyclerView;
    private SearchAdapter searchAdapter;
    private SearchView searchView;

    private String dvhcvn;
    private JSONArray dtb;

    private EditText filterCapacity;
    private AutoCompleteTextView filterCity, filterDistrict, filterWard;
    private HashMap<String, String> level1 = new HashMap<String, String>();
    private HashMap<String, String> level2 = new HashMap<String, String>();
    private ArrayList<String> level3 = new ArrayList<>();
    private HashMap<String, String> currentLevelName = new HashMap<>();

    private ArrayAdapter<String> cityAdapter, districtAdapter, wardAdapter;
    private String city, district, ward;
    private int capacity;

    private ProgressDialog pd;

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
        setContentView(R.layout.activity_search);

        filterCity = (AutoCompleteTextView) findViewById(R.id.search_city);
        filterDistrict = (AutoCompleteTextView) findViewById(R.id.search_district);
        filterWard = (AutoCompleteTextView) findViewById(R.id.search_ward);
        filterCapacity = findViewById(R.id.search_capacity);

        parseJson();
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

        cityAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.layout_dropdown_item, level1.keySet().toArray(new String[0]));
        districtAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.layout_dropdown_item, level2.keySet().toArray(new String[0]));
        wardAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.layout_dropdown_item, level3);

        filterCity.setAdapter(cityAdapter);
        filterDistrict.setAdapter(districtAdapter);
        filterWard.setAdapter(wardAdapter);

        filterCity.setThreshold(1);
        filterDistrict.setThreshold(1);
        filterWard.setThreshold(1);

        filterCity.setOnItemClickListener(this);
        filterDistrict.setOnItemClickListener(this);
        filterWard.setOnItemClickListener(this);


        recyclerView = findViewById(R.id.search_recycleview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        searchAdapter = new SearchAdapter(SearchActivity.this,getListZones());
        recyclerView.setAdapter(searchAdapter);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        
    }

    private List<Zone> getListZones() {
        List<Zone> list = new ArrayList<>();
        Intent i = getIntent();
        list = (ArrayList<Zone>)getIntent().getSerializableExtra("zonesList");
        return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search,menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // fetch the user selected value
        String item = parent.getItemAtPosition(position).toString();

        // Get the current change
        currentLevelName.clear();

        if (level1.containsKey(item)) {
            currentLevelName.put(item, level1.get(item));
            Toast.makeText(SearchActivity.this, "Selected Item is: \t" + item + " at " + level1.get(item), Toast.LENGTH_LONG).show();
        }
        else if (level2.containsKey(item)) {
            currentLevelName.put(item, level2.get(item));
            Toast.makeText(SearchActivity.this, "Selected Item is: \t" + item + " at " + level2.get(item), Toast.LENGTH_LONG).show();
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
            filterDistrict.setText("");
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
            districtAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.layout_dropdown_item, level2.keySet().toArray(new String[0]));
            filterDistrict.setAdapter(districtAdapter);
//            districtAdapter.notifyDataSetChanged();
            // Clear level 3
            filterWard.setText("");
            level3.clear();
            wardAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.layout_dropdown_item, level3);
            filterWard.setAdapter(wardAdapter);
        }  else if (currentLevelName.containsValue("quận") |
                currentLevelName.containsValue("huyện") |
                currentLevelName.containsValue("thành phố") |
                currentLevelName.containsValue("thị xã")) {
            System.out.println("Update 2 nefefefeef");
            // Update level3
            filterWard.setText("");
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
                        Map.Entry<String, String> entry1 = currentLevelName.entrySet().iterator().next();
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
            wardAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.layout_dropdown_item, level3);
            filterWard.setAdapter(wardAdapter);
        }
    }

}