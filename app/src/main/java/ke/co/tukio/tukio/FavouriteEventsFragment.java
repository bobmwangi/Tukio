package ke.co.tukio.tukio;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.afinal.simplecache.ACache;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ke.co.tukio.tukio.recycler.GetDataAdapter;
import ke.co.tukio.tukio.recycler.RecyclerViewAdapter;


public class FavouriteEventsFragment extends Fragment {
    TextView txt1, noFavourite, txt2, txt3;
    Button btn1;
    ImageView img1;

    List<GetDataAdapter> GetDataAdapter1;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    RecyclerView.Adapter recyclerViewadapter;

   // String GET_JSON_DATA_HTTP_URL = "http://tukio.co.ke/applicationfiles/fetcheventsjson.php";
    String JSON_EVENT_NAME = "event_name";
    String JSON_EVENT_DATE = "event_date";
    String JSON_EVENT_DATE2 = "event_date_two";
    String JSON__EVENT_ID = "id";
    String JSON_EVENT_VENUE = "event_venue";
    String JSON_EVENT_VENUE_ID = "venue_id";
    String JSON_EVENT_VIEWS = "views";
    String JSON_IMAGE_URL = "preview_img";
    String JSON_EVENT_DESC = "event_details";
    String JSON_EVENT_EVTIME = "event_time";
    String JSON_EVENT_EVAGE = "age";

    SwipyRefreshLayout mSwipyRefreshLayout;


    JsonArrayRequest jsonArrayRequest;
    RequestQueue requestQueue;

    public static FavouriteEventsFragment newInstance() {
        FavouriteEventsFragment fragment = new FavouriteEventsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        //recycler
        GetDataAdapter1 = new ArrayList<>();
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerview1);
        recyclerView.setHasFixedSize(true);

        //grid
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);
//        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerViewlayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewlayoutManager);

        noFavourite = (TextView) v.findViewById(R.id.noFavTxt);
        CheckFavVenuesCount(); // if count = 0, then user has not favourited any venues, will ssho no favs textView
//        showEvents();  // removed 28-11-2017 To be only checked if Count isn't 0


        //swipe
        mSwipyRefreshLayout = (SwipyRefreshLayout) v.findViewById(R.id.swipyrefreshlayout);
        mSwipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {

                Log.d("MainActivity", "Refresh triggered at " + (direction == SwipyRefreshLayoutDirection.TOP ? "top" : "bottom"));
                Connectivity();
            }
        });
        return v;
    }


    public void showEvents() {
        String fav_ev;
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        fav_ev = myPrefs.getString("fav_events_exists", "");
//        Toast.makeText(getContext(), "Status: " + fav_ev, Toast.LENGTH_SHORT).show();

        ACache mCache = ACache.get(getActivity());
        JSONArray value2 = mCache.getAsJSONArray("tukio_favourite_events");

        try {

            if (!value2.equals(null)) {  //not null
                JSON_PARSE_CACHED_DATA(value2);
//                Toast.makeText(getActivity(), "Cached favourite events: "+value2.length(), Toast.LENGTH_SHORT).show();
            }
            if(value2.length()<1){
//                Toast.makeText(getActivity(), "No favourites", Toast.LENGTH_SHORT).show();
            }
        } catch (NullPointerException e) {
//            Toast.makeText(this, "No previous kicks", Toast.LENGTH_SHORT).show();
            return;
        }


    }




    public void Connectivity() {
        ConnectivityManager cn = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf = cn.getActiveNetworkInfo();
        if (nf != null && nf.isConnected() == true) {
            JSON_DATA_WEB_CALL();  //there is internet, Get new data
            getFavVenuesCount();

        } else  //there is not internet, get Cached data
        {
            mSwipyRefreshLayout.setRefreshing(false);
            Snackbar.make(getActivity().findViewById(android.R.id.content),
                    "No internet connection!", Snackbar.LENGTH_LONG).show();
        }
    }

    public void JSON_DATA_WEB_CALL() {
        SharedPreferences pref1stRun = PreferenceManager.getDefaultSharedPreferences(getContext());
        final String uid = pref1stRun.getString("userId", "");
      String GET_JSON_DATA_HTTP_URL=  "http://tukio.co.ke/applicationfiles/geteventsfromfavouritevenues.php?uid=" + uid;
        jsonArrayRequest = new JsonArrayRequest(GET_JSON_DATA_HTTP_URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                CacheData(response);  // cache this response

        }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonArrayRequest);
    }

    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array) {
        CacheData(array); //send data to the cache method
        for (int i = 0; i < array.length(); i++) {
            GetDataAdapter GetDataAdapter2 = new GetDataAdapter();
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);
                GetDataAdapter2.setevName(json.getString(JSON_EVENT_NAME));
                GetDataAdapter2.setevVenue(json.getString(JSON_EVENT_VENUE));
                GetDataAdapter2.setevVenueId(json.getString(JSON_EVENT_VENUE_ID));
                GetDataAdapter2.setevDate(json.getString(JSON_EVENT_DATE));
                GetDataAdapter2.setevDate2(json.getString(JSON_EVENT_DATE2));
                GetDataAdapter2.setevId(json.getString(JSON__EVENT_ID));
                GetDataAdapter2.setevViews(json.getString(JSON_EVENT_VIEWS));
                GetDataAdapter2.setevDesc(json.getString(JSON_EVENT_DESC));
                GetDataAdapter2.setImageServerUrl(json.getString(JSON_IMAGE_URL));
                GetDataAdapter2.setevTime(json.getString(JSON_EVENT_EVTIME));
                GetDataAdapter2.setEvAge(json.getString(JSON_EVENT_EVAGE));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            GetDataAdapter1.add(GetDataAdapter2);
        }
        recyclerViewadapter = new RecyclerViewAdapter(GetDataAdapter1, getActivity());
        recyclerView.setAdapter(recyclerViewadapter);
    }

    public void CacheData(JSONArray myArray) {  // method to cache data
        ACache mCache = ACache.get(getActivity());
        mCache.put("tukio_favourite_events", myArray);
        mSwipyRefreshLayout.setRefreshing(false);
        reOpenFrag(); // refresh activity
    }


    public void JSON_PARSE_CACHED_DATA(JSONArray array) {  //No internet, load this to the recyclerview
        for (int i = 0; i < array.length(); i++) {
            GetDataAdapter GetDataAdapter2 = new GetDataAdapter();
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);
                GetDataAdapter2.setevName(json.getString(JSON_EVENT_NAME));
                GetDataAdapter2.setevVenue(json.getString(JSON_EVENT_VENUE));
                GetDataAdapter2.setevVenueId(json.getString(JSON_EVENT_VENUE_ID));
                GetDataAdapter2.setevDate(json.getString(JSON_EVENT_DATE));
                GetDataAdapter2.setevDate2(json.getString(JSON_EVENT_DATE2));
                GetDataAdapter2.setevId(json.getString(JSON__EVENT_ID));
                GetDataAdapter2.setevDesc(json.getString(JSON_EVENT_DESC));
                GetDataAdapter2.setevViews(json.getString(JSON_EVENT_VIEWS));
                GetDataAdapter2.setImageServerUrl(json.getString(JSON_IMAGE_URL));
                GetDataAdapter2.setevTime(json.getString(JSON_EVENT_EVTIME));
                GetDataAdapter2.setEvAge(json.getString(JSON_EVENT_EVAGE));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            GetDataAdapter1.add(GetDataAdapter2);
        }
        recyclerViewadapter = new RecyclerViewAdapter(GetDataAdapter1, getActivity());
        recyclerView.setAdapter(recyclerViewadapter);
    }

    public void reOpenFrag() {
        Intent pIntent = new Intent(getActivity(), MainActivity.class);
        startActivityForResult(pIntent, 0);

    }
    public void CheckFavVenuesCount() {
        SharedPreferences pref1stRun = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String vcount = pref1stRun.getString("count_favourite_venues", "");
        if (vcount.contentEquals("0")) {
            noFavourite.setVisibility(View.VISIBLE);  //no favs msg visible
        } else {
            showEvents();
        }
    }

    public void getFavVenuesCount() {
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String useridd = myPrefs.getString("userId", "");
        new Thread() {
            //        @Override
            public void run() {

                String path = "http://www.tukio.co.ke/applicationfiles/countfavouritevenues.php?userid=" + useridd;
                URL u = null;
                try {
                    u = new URL(path);
                    HttpURLConnection c = (HttpURLConnection) u.openConnection();
                    c.setRequestMethod("GET");
                    c.connect();
                    InputStream in = c.getInputStream();
                    final ByteArrayOutputStream bo = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    in.read(buffer); // Read from Buffer.
                    bo.write(buffer); // Write Into Buffer.

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            String vresp = bo.toString();
                            String vresp1 = vresp.trim();


                            ShowCountVenues(vresp1);
                            try {
                                bo.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public void ShowCountVenues(String vcount){
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("count_favourite_venues", vcount);
        editor.apply();
    }
}