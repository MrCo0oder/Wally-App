package com.codebook.wallyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    List<DataHandler> dataHandlerList;
    SwipeRefreshLayout swipeRefreshLayout;
    WallpaperAdapter wallpaperAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.list);
        swipeRefreshLayout = findViewById(R.id.swipe);
        dataHandlerList = new ArrayList<>();
        wallpaperAdapter=new WallpaperAdapter(getApplicationContext(),R.layout.list_items,dataHandlerList);
        loadData("First");
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData("Refresh");
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent =new Intent(getApplicationContext(),WallpaperView.class);
                intent.putExtra("title",dataHandlerList.get(i).getTitle());
                intent.putExtra("image",dataHandlerList.get(i).image);
                startActivity(intent);
            }
        });
    }

    void loadData(String type) {
        swipeRefreshLayout.setRefreshing(true);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "https://waally.herokuapp.com/apis/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        swipeRefreshLayout.setRefreshing(false);
                        parseJSON(response ,type);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getApplicationContext(),error.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                        System.out.println(error.getMessage());
                    }
                }
        );
        requestQueue.add(stringRequest);
    }

    private void parseJSON(String response, String type) {
        String title, thumbnail, image;

        if(type.equals("Refresh")){
            dataHandlerList.clear();
            wallpaperAdapter.notifyDataSetChanged();
        }
        try {
            JSONArray jsonArray =new JSONArray(response);
            for(int i=0;i< jsonArray.length();i++)
            {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                title=jsonObject.get("title").toString();
                thumbnail=jsonObject.get("thumbnail").toString();
                image=jsonObject.get("image").toString();
                dataHandlerList.add(new DataHandler(title,thumbnail,image));
            }
            wallpaperAdapter=new WallpaperAdapter(getApplicationContext(),R.layout.list_items,dataHandlerList);
            listView.setAdapter(wallpaperAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}