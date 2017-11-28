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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

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

import ke.co.tukio.tukio.recycler.GetDataAdapterVen;
//import ke.co.tukio.tukio.recycler.RecyclerViewAdapter;
import ke.co.tukio.tukio.recycler.RecyclerViewAdapterFavVen;
import ke.co.tukio.tukio.util.CheckConnectivity;

public class FavouriteVenuesActivity extends AppCompatActivity {
    List<GetDataAdapterVen> GetDataAdapter1;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    RecyclerView.Adapter recyclerViewadapter;
    ProgressDialog pDialog;

    String JSON_VENUE_ID = "id";
    String JSON_VENUE_NAME = "manager_club";
    String JSON_VENUE_LOC = "location";

    String GET_JSON_DATA_HTTP_URL=null;

    JsonArrayRequest jsonArrayRequest;
    RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_venues);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(FavouriteVenuesActivity.this, R.color.colorPrimary));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        GetDataAdapter1 = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview1);
        recyclerView.setHasFixedSize(true);
       //list
        recyclerViewlayoutManager = new LinearLayoutManager(FavouriteVenuesActivity.this);
        recyclerView.setLayoutManager(recyclerViewlayoutManager);

        CheckConnectivity checkNetwork = new CheckConnectivity();
        if (checkNetwork.isConnected(FavouriteVenuesActivity.this)) {
            FetchVenuesFromServer();
        }
        else{

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fav_venues, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.DELETEFavourites) {
            clearFavs();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void FetchVenuesFromServer(){
        SharedPreferences pref1stRun = PreferenceManager.getDefaultSharedPreferences(this);
        final String uid = pref1stRun.getString("userId", "");
        if (uid.isEmpty()){
            Toast.makeText(this, "Problem encountered. Please sign in", Toast.LENGTH_SHORT).show();
        }
        else{
            GET_JSON_DATA_HTTP_URL = "http://tukio.co.ke/applicationfiles/getfavouritevenues.php?uid="+uid.trim();
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
        requestQueue = Volley.newRequestQueue(FavouriteVenuesActivity.this);
        requestQueue.add(jsonArrayRequest);
    }
    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            GetDataAdapterVen GetDataAdapter2 = new GetDataAdapterVen();
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);
                GetDataAdapter2.setVenId(json.getString(JSON_VENUE_ID));
                GetDataAdapter2.setVenName(json.getString(JSON_VENUE_NAME));
                GetDataAdapter2.setVenLoc(json.getString(JSON_VENUE_LOC));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            GetDataAdapter1.add(GetDataAdapter2);
        }
        recyclerViewadapter = new RecyclerViewAdapterFavVen(GetDataAdapter1, FavouriteVenuesActivity.this);
        recyclerView.setAdapter(recyclerViewadapter);
    }

    public void clearFavs(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FavouriteVenuesActivity.this);
        alertDialog.setTitle("Remove");
        alertDialog.setMessage("Delete all venues from favourites?");
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {


                SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(FavouriteVenuesActivity.this);
                String uid  = myPrefs.getString("userId", "");

                CheckConnectivity checkNetwork = new CheckConnectivity();
                if (checkNetwork.isConnected(FavouriteVenuesActivity.this)) {
                    deleteOnServer(uid);
                }
                else
                {
                    Snackbar.make(FavouriteVenuesActivity.this  .findViewById(android.R.id.content),
                            "No internet connection!", Snackbar.LENGTH_LONG).show();
                }


            }
        });
        alertDialog.show();
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

                String path = "http://www.tukio.co.ke/applicationfiles/deleteallfavouritevenues.php?userid=" + uid;
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

                            String response = bo.toString();
                            String response2 = response.trim();

                            pDialog.dismiss();
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
            SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(FavouriteVenuesActivity.this);
            SharedPreferences.Editor editor = myPrefs.edit();
            editor.putString("count_favourite_venues", "0");
            editor.apply();
            Intent pIntent = new Intent(getBaseContext(), MainActivity.class);
            startActivityForResult(pIntent, 0);
        }
        else{
            Toast.makeText(this, "Deletion failed", Toast.LENGTH_SHORT).show();
        }
    }
}