package ke.co.tukio.tukio;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
//import com.cocosw.bottomsheet.BottomSheet;

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
import ke.co.tukio.tukio.recycler.RecyclerViewAdapterFavEvents;
import ke.co.tukio.tukio.util.CheckConnectivity;

public class ReminderEventsActivity extends AppCompatActivity {

    List<GetDataAdapter> GetDataAdapter1;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    RecyclerView.Adapter recyclerViewadapter;
    ProgressDialog pDialog;

//    String GET_JSON_DATA_HTTP_URL = "http://tukio.co.ke/applicationfiles/fetcheventsjson.php";
    String JSON_EVENT_NAME = "event_name";
    String JSON_EVENT_REM_ID = "event_id";  //the id of the event in the reinder_Events tble
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

    String GET_JSON_DATA_HTTP_URL=null;

    JsonArrayRequest jsonArrayRequest;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_events);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(ReminderEventsActivity.this, R.color.colorPrimary));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle(null);

        GetDataAdapter1 = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview1);
        recyclerView.setHasFixedSize(true);
        //grid
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(ReminderEventsActivity.this,2);
//        recyclerView.setLayoutManager(gridLayoutManager);
        ///list
        recyclerViewlayoutManager = new LinearLayoutManager(ReminderEventsActivity.this);
        recyclerView.setLayoutManager(recyclerViewlayoutManager);

//        Connectivity();

        FetchEventsFromServer();
    }
    public void FetchEventsFromServer(){
        SharedPreferences pref1stRun = PreferenceManager.getDefaultSharedPreferences(this);
        final String uid = pref1stRun.getString("userId", "");
        if (uid.isEmpty()){
            Toast.makeText(this, "Problem encountered. Please sign in", Toast.LENGTH_SHORT).show();
        }
        else{
            GET_JSON_DATA_HTTP_URL = "http://tukio.co.ke/applicationfiles/getfavouriteevents.php?uid="+uid.trim();
//        String GET_JSON_DATA_HTTP_URL = "http://tukio.co.ke/applicationfiles/fetcheventsjson.php";
            JSON_DATA_WEB_CALL();
        }
    }
    public void FetchEventsFromServer2(){

        SharedPreferences pref1stRun = PreferenceManager.getDefaultSharedPreferences(this);
        String stored_events_ids = pref1stRun.getString("myEvents", "");
        if (stored_events_ids.isEmpty()){
            Toast.makeText(this, "You have no favourite events.", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Saved event ids: "+stored_events_ids, Toast.LENGTH_SHORT).show();
         GET_JSON_DATA_HTTP_URL = "http://tukio.co.ke/applicationfiles/fetchfavouriteevents.php?idarray[]="+stored_events_ids.trim();
//        String GET_JSON_DATA_HTTP_URL = "http://tukio.co.ke/applicationfiles/fetcheventsjson.php";
            JSON_DATA_WEB_CALL();
        }
    }

    public void JSON_DATA_WEB_CALL() {
        jsonArrayRequest = new JsonArrayRequest(GET_JSON_DATA_HTTP_URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSON_PARSE_DATA_AFTER_WEBCALL(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        requestQueue = Volley.newRequestQueue(ReminderEventsActivity.this);
        requestQueue.add(jsonArrayRequest);
    }

    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            GetDataAdapter GetDataAdapter2 = new GetDataAdapter();
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);
                GetDataAdapter2.setevName(json.getString(JSON_EVENT_NAME));
                GetDataAdapter2.setevRemId(json.getString(JSON_EVENT_REM_ID));
                GetDataAdapter2.setevVenue(json.getString(JSON_EVENT_VENUE));
                GetDataAdapter2.setevVenueId(json.getString(JSON_EVENT_VENUE_ID));
                GetDataAdapter2.setevDate(json.getString(JSON_EVENT_DATE));
                GetDataAdapter2.setevDate2(json.getString(JSON_EVENT_DATE2));
                GetDataAdapter2.setevId(json.getString(JSON__EVENT_ID));
                GetDataAdapter2.setevViews(json.getString(JSON_EVENT_VIEWS));
                GetDataAdapter2.setevDesc(json.getString(JSON_EVENT_DESC));
                GetDataAdapter2.setImageServerUrl(json.getString(JSON_IMAGE_URL));
                GetDataAdapter2.setEvAge(json.getString(JSON_EVENT_EVAGE));
                GetDataAdapter2.setevTime(json.getString(JSON_EVENT_EVTIME));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            GetDataAdapter1.add(GetDataAdapter2);
        }
        recyclerViewadapter = new RecyclerViewAdapterFavEvents(GetDataAdapter1, ReminderEventsActivity.this);
        recyclerView.setAdapter(recyclerViewadapter);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reminders, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.clearFavourites) {
            clearFav();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void clearFav(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ReminderEventsActivity.this);
        alertDialog.setTitle("Remove");
        alertDialog.setMessage("Delete all events from favourites?");
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {


                SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(ReminderEventsActivity.this);
                String uid  = myPrefs.getString("userId", "");

                CheckConnectivity checkNetwork = new CheckConnectivity();
                if (checkNetwork.isConnected(ReminderEventsActivity.this)) {
                deleteOnServer(uid);
                }
                else
                {
                    Snackbar.make(ReminderEventsActivity.this  .findViewById(android.R.id.content),
                            "No internet connection!", Snackbar.LENGTH_LONG).show();
                }


//                Toast.makeText(ReminderEventsActivity.this, "Cleared", Toast.LENGTH_SHORT).show();
//                Intent poIntetnt = new Intent(getBaseContext(), MainActivity.class);
//                startActivityForResult(poIntetnt, 0);
            }
        });


        alertDialog.show();
    }


   /* public void ShowBottomSheet(){
        new BottomSheet.Builder(ReminderEventsActivity.this)
                .title("FSD")
                .sheet(R.menu.list)
                .listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.help:
//                                        q.toast("Help me!");
                                Toast.makeText(ReminderEventsActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }).show();
    }*/
    public void AccDetails(View v){  //works for clicks from the BS
        Toast.makeText(ReminderEventsActivity.this, "Hello", Toast.LENGTH_SHORT).show();
    }

    public void deleteOnServer(final String uid) {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("deleting...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        new Thread() {
            //        @Override
            public void run() {

                String path = "http://www.tukio.co.ke/applicationfiles/deleteallfavouritesevents.php?userid=" + uid;
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

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pDialog.dismiss();
                            String response = bo.toString();
                            String response2 = response.trim();


                            ToastResults(response2);
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
    public void ToastResults(String resp){
        if (resp.contentEquals("deleted")){
            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
            SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(ReminderEventsActivity.this);
            SharedPreferences.Editor editor = myPrefs.edit();
            editor.putString("count_favourite_events", "0");
            editor.putString("myEvents", "");
            editor.putString("myVenues", "");
            editor.apply();
            ACache mCache = ACache.get(ReminderEventsActivity.this);
            mCache.put("tukio_favourite_events", "");

            Intent pIntent = new Intent(getBaseContext(), MainActivity.class);
            startActivityForResult(pIntent, 0);
        }
        else{
            Toast.makeText(this, "Deletion failed", Toast.LENGTH_SHORT).show();
        }
    }
}
