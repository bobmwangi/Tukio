package ke.co.tukio.tukio;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.afinal.simplecache.ACache;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import ke.co.tukio.tukio.util.CheckConnectivity;


public class SplashActivity extends AppCompatActivity {

    String GET_JSON_DATA_HTTP_URL = "http://tukio.co.ke/applicationfiles/fetchvenuesjson.php";
    JsonArrayRequest jsonArrayRequest;
    RequestQueue requestQueue;
    String publicjson = "nothing";
    TextView noCon;
    // Animation
    Animation slidedown;

    private String TAG = MainActivity.class.getSimpleName();
    ArrayList<HashMap<String, String>> PlaceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

       /* //notif bar color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.setStatusBarColor(ContextCompat.getColor(SplashActivity.this, R.color.white));
        }*/
        slidedown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        ImageView img = (ImageView) findViewById(R.id.imageView);
        img.startAnimation(slidedown);
        noCon = (TextView) findViewById(R.id.tvNoInternet);
//        Glide.with(SplashActivity.this)
//                .load(R.drawable.fire)
//                .asGif()
//                .crossFade()
//                .into(img);


//        ConnectivityManager cn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo nf = cn.getActiveNetworkInfo();
//        if (nf != null && nf.isConnected() == true) {


        CheckConnectivity checkNetwork = new CheckConnectivity();
        if (checkNetwork.isConnected(SplashActivity.this)) {
            UserLogChecks();
            ConnFetchVenuesForMap(); //get map items
            ConnFetchVenues();  //get a list of venues
            JSON_EVENTS_WEB_CALL(); //get events
            JSON_FAVOURITE_EVENTS_WEB_CALL(); //get events from fav venues
            CountFavouriteEvents();  //count events the user has fav'd
            CountFavouriteVenues();  //count events the user has fav'd

            int sDelayed = 4;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    CheckFirst();

                }
            }, sDelayed * 1000);
        } else {
            noCon.setVisibility(View.VISIBLE);
        }


//        ConnFetchVenuesForMap(); //get map items
//        ConnFetchVenues();  //get a list of venues


    }

    private void UserLogChecks() {
        String FirstRunStatus, userid;
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        FirstRunStatus = myPrefs.getString("firstrun", "");
        if (FirstRunStatus.contentEquals("yes")) {
            userid = myPrefs.getString("userId", "");

            UserLog(userid);
        } else {

        }
    }

    private void UserLog(final String uid) {
        new Thread() {
            //        @Override
            public void run() {

                String path = "http://www.tukio.co.ke/applicationfiles/registeruserlog.php?uid=" + uid;
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

                            String response1 = bo.toString();
                            String response = response1.trim();


                            ToastLogResult(response);
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

    private void ToastLogResult(final String resp) {
        if (resp.contentEquals("log registered")) {
            ACache mCache = ACache.get(SplashActivity.this);
            mCache.put("userlog", "userlogged", 60);
            Toast.makeText(this, "Log registered", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "log registration failed", Toast.LENGTH_SHORT).show();
        }

    }

    private void CheckFirst() {
        String FirstRunStatus;
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        FirstRunStatus = myPrefs.getString("firstrun", "");
        if (FirstRunStatus.contentEquals("yes")) {
//            Toast.makeText(this, "first run: "+FirstRunStatus, Toast.LENGTH_SHORT).show();
            Intent ff = new Intent(getBaseContext(), MainActivity.class);
//            Intent ff = new Intent(getBaseContext(), SignUpActivity2.class);  //09-11-2017
            startActivityForResult(ff, 0);
            finish();
        } else
            startActivity(new Intent(SplashActivity.this, SignupActivity.class));
        finish();

    }


    public void ConnFetchVenues() {
        ConnectivityManager cn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf = cn.getActiveNetworkInfo();
        if (nf != null && nf.isConnected() == true) {
            JSON_DATA_WEB_CALL();
        } else {
//            Toast.makeText(this, "Turn on your internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    public void ConnFetchVenuesForMap() {
        ConnectivityManager cn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf = cn.getActiveNetworkInfo();
        if (nf != null && nf.isConnected() == true) {
            PlaceList = new ArrayList<>();
            new Getplaces().execute();


        } else
//            Toast.makeText(this, "Turn on your internet connection.", Toast.LENGTH_SHORT).show();
            ;
    }

    public void JSON_DATA_WEB_CALL() {
        jsonArrayRequest = new JsonArrayRequest(GET_JSON_DATA_HTTP_URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //call cache lib
                CacheVenues(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }


    public void CacheVenues(JSONArray myArray) {  // method to cache data
        ACache mCache = ACache.get(this);
        mCache.put("tukio_venues", myArray);
//        mCache.put("test_key3", "test value", 2 * ACache.TIME_DAY);  //2 days
    }

    public class Getplaces extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            Toast.makeText(SplashActivity.this,"Fetching new data",Toast.LENGTH_LONG).show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HTTPHandler sh = new HTTPHandler();

            String url = "http://tukio.co.ke/applicationfiles/getlocations.php";
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            publicjson = jsonStr;
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray places = jsonObj.getJSONArray("places");

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            Toast.makeText(getApplicationContext(),      "Json parsing error: " + e.getMessage(),  Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Couldn't get data from server!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


            FileOutputStream fos;
            try {
                fos = openFileOutput("jsonMapsDatafile.txt", Context.MODE_PRIVATE);
                //default mode is PRIVATE, can be APPEND etc.
                fos.write(publicjson.getBytes());
                fos.close();

                Toast.makeText(getApplicationContext(), "Map data saved.", Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    //get events
    public void JSON_EVENTS_WEB_CALL() {

        String events_url = "http://tukio.co.ke/applicationfiles/fetcheventsjson.php";
        jsonArrayRequest = new JsonArrayRequest(events_url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                CacheEvents(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        requestQueue = Volley.newRequestQueue(SplashActivity.this);
        requestQueue.add(jsonArrayRequest);
    }

    public void CacheEvents(JSONArray array) { //Cache this data
        ACache mCache = ACache.get(SplashActivity.this);
        mCache.put("tukio_events", array);

    }

    //get events from selected venues
    public void JSON_FAVOURITE_EVENTS_WEB_CALL() {
        SharedPreferences pref1stRun = PreferenceManager.getDefaultSharedPreferences(this);
        final String uid = pref1stRun.getString("userId", "");
        final String firstrun = pref1stRun.getString("firstrun", "");
        if (firstrun.contentEquals("yes")) {
            Toast.makeText(this, "Fetching events from favourite venues", Toast.LENGTH_SHORT).show();
            String FavVenEventsURL = "http://tukio.co.ke/applicationfiles/geteventsfromfavouritevenues.php?uid=" + uid;

            jsonArrayRequest = new JsonArrayRequest(FavVenEventsURL, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
//                    CacheFavouriteEvents(response);

                    if(response.length()>0){
                        Toast.makeText(SplashActivity.this, "Response exist", Toast.LENGTH_SHORT).show();
                        CacheFavouriteEvents(response);
                    }
                    else{
                        Toast.makeText(SplashActivity.this, "User has no favourite venues", Toast.LENGTH_SHORT).show();

                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });
            requestQueue = Volley.newRequestQueue(SplashActivity.this);
            requestQueue.add(jsonArrayRequest);
        } else {
        }
    }

    public void JSON_FAVOURITE_EVENTS_WEB_CALL2() {
        //read from shared pref
        SharedPreferences pref1stRun = PreferenceManager.getDefaultSharedPreferences(this);
        String stored_venue_ids = pref1stRun.getString("myVenues", "");
        if (stored_venue_ids.isEmpty()) {
//            Toast.makeText(this, "You have no favourite venues.", Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(this, "Saved venue ids: " + stored_venue_ids, Toast.LENGTH_SHORT).show();
            String FavVenEventsURL = "http://tukio.co.ke/applicationfiles/fetchfavouritevenueevents.php?idarray[]=" + stored_venue_ids.trim();
//            JSON_DATA_WEB_CALL();
//            String events_url = "http://tukio.co.ke/applicationfiles/fetcheventsjson.php";
            jsonArrayRequest = new JsonArrayRequest(FavVenEventsURL, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {

                        CacheFavouriteEvents(response);

                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });
            requestQueue = Volley.newRequestQueue(SplashActivity.this);
            requestQueue.add(jsonArrayRequest);
        }
    }

    public void CacheFavouriteEvents(JSONArray array) { //Cache this data

        ACache mCache = ACache.get(SplashActivity.this);
        mCache.put("tukio_favourite_events", array);

        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("fav_events_exists", "yes");
        editor.apply();
        Toast.makeText(this, "Saved fav events", Toast.LENGTH_SHORT).show();
    }
    public void CountFavouriteEvents() {
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(this);
       final String useridd = myPrefs.getString("userId", "");
        new Thread() {
            //        @Override
            public void run() {

                String path = "http://www.tukio.co.ke/applicationfiles/countfavouriteevents.php?userid=" + useridd;
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

                            String resp = bo.toString();
                            String resp1 = resp.trim();


                            ShowCount(resp1);
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
public void ShowCount(String count){
    SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
    SharedPreferences.Editor editor = myPrefs.edit();
    editor.putString("count_favourite_events", count);
    editor.apply();
}


    public void CountFavouriteVenues() {
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(this);
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

                    runOnUiThread(new Runnable() {
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
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("count_favourite_venues", vcount);
        editor.apply();
    }
}


