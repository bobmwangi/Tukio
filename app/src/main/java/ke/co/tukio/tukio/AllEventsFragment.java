package ke.co.tukio.tukio;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
//import com.orangegangsters.github.swiperefreshlayout.R;
//import com.orangegangsters.github.swiperefreshlayout.databinding.ActivityMainBinding;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.afinal.simplecache.ACache;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ke.co.tukio.tukio.recycler.GetDataAdapter;
import ke.co.tukio.tukio.recycler.RecyclerViewAdapter;


public class AllEventsFragment extends Fragment {
    TextView txt1, txt2, txt3;
    Button btn1;
    ImageView img1;

    private RecyclerViewAdapter myAdapter;  // to solve No adapter attached; skipping layout in Staggered

    public static final int DISMISS_TIMEOUT = 2000;
    ArrayList<GetDataAdapter> GetDataAdapter1;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    RecyclerView.Adapter recyclerViewadapter;

    String GET_JSON_DATA_HTTP_URL = "http://tukio.co.ke/applicationfiles/fetcheventsjson.php";
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


    public static AllEventsFragment newInstance() {
        AllEventsFragment fragment = new AllEventsFragment();
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
        recyclerView.setItemAnimator(null); // solve scrroll issues

        //former functional staggered
        // recyclerView       .setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));// Here 2 is no. of columns to be displayed


//        recyclerView.setLayoutManager(layoutManager);
//        adapter = new ArtistArrayAdapter( artists , R.layout.list_item ,getApplicationContext());
//        recyclerViewadapter = new RecyclerViewAdapterGrid(GetDataAdapter1, getActivity());
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setAdapter( myAdapter );

//        favPlaces.setHasFixedSize(true);
        //grid
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);
//        recyclerView.setLayoutManager(gridLayoutManager);
        //list
        recyclerViewlayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewlayoutManager);

//        Connectivity();
        showEvents();

   mSwipyRefreshLayout = (SwipyRefreshLayout) v.findViewById(R.id.swipyrefreshlayout);
        mSwipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {

//                Toast.makeText(getContext(), "pull", Toast.LENGTH_SHORT).show();
                Log.d("MainActivity", "Refresh triggered at " + (direction == SwipyRefreshLayoutDirection.TOP ? "top" : "bottom"));

                Connectivity();
//                mSwipyRefreshLayout.setRefreshing(false);
            }
        });
        return v;
    }
// swipe to refresh start

    // swipe to refresh stop
    public void showEvents() {
        ACache mCache = ACache.get(getActivity());
        JSONArray value2 = mCache.getAsJSONArray("tukio_events");
        Toast.makeText(getActivity(), "Saved events: " + value2.length(), Toast.LENGTH_SHORT).show();

        if (value2.length() == 0) {
            Toast.makeText(getActivity(), "No cached data", Toast.LENGTH_SHORT).show();
        } else {
            JSON_PARSE_CACHED_DATA(value2);
        }
    }

    public void Connectivity() {
        ConnectivityManager cn = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf = cn.getActiveNetworkInfo();
        if (nf != null && nf.isConnected() == true) {
            JSON_DATA_WEB_CALL();  //there is internet, Get new data

        } else  //there is not internet, get Cached data
        {
            Toast.makeText(getContext(), "No internet to refresh.", Toast.LENGTH_SHORT).show();
//            Don't do anything
//            ACache mCache = ACache.get(getActivity());
//            JSONArray value2 = mCache.getAsJSONArray("tukio_events");
//            Toast.makeText(getActivity(), "Cached events: " + value2.length(), Toast.LENGTH_SHORT).show();
//
//            if (value2.length() == 0) {
//                Toast.makeText(getActivity(), "No cached data", Toast.LENGTH_SHORT).show();
//            } else {
//                JSON_PARSE_CACHED_DATA(value2);
//            }
        }
    }

    public void JSON_DATA_WEB_CALL() {
        jsonArrayRequest = new JsonArrayRequest(GET_JSON_DATA_HTTP_URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
              //  JSON_PARSE_DATA_AFTER_WEBCALL(response);
                CacheData(response); //
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
//        CacheData(array); //send data to the cache method
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

//        recyclerViewadapter = new RecyclerViewAdapterGrid(GetDataAdapter1, getActivity());
        recyclerViewadapter = new RecyclerViewAdapter(GetDataAdapter1, getActivity());
        recyclerView.setAdapter(recyclerViewadapter);
        //staggered
        recyclerViewadapter.notifyDataSetChanged();
    }

    public void CacheData(JSONArray myArray) {  // method to cache data
        ACache mCache = ACache.get(getActivity());
        mCache.put("tukio_events", myArray);
        mSwipyRefreshLayout.setRefreshing(false);
        Toast.makeText(getContext(), "Events saved", Toast.LENGTH_SHORT).show();
        recyclerView.invalidate();
        recyclerViewadapter.notifyDataSetChanged();
        showEvents();
//        FragmentManager fm = getFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
//        AllEventsFragment llf = new AllEventsFragment();
//        ft.replace(R.id.frame_layout, llf);
//        ft.commit();
//        onCreate(new Bundle());
//        getActivity().runOnUiThread(new Runnable() {
//            public void run() {
//                showEvents();
//                recyclerView.invalidate();
//                recyclerViewadapter.notifyDataSetChanged();
//            }
//            });
//        recyclerView.invalidate();
//        recyclerViewadapter.notifyDataSetChanged();
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
//        recyclerViewadapter = new RecyclerViewAdapterGrid(GetDataAdapter1, getActivity());
        recyclerViewadapter = new RecyclerViewAdapter(GetDataAdapter1, getActivity());
        recyclerView.setAdapter(recyclerViewadapter);

        //staggered
        recyclerViewadapter.notifyDataSetChanged();
    }

}