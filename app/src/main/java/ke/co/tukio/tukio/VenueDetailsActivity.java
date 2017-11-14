package ke.co.tukio.tukio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VenueDetailsActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    private static final String TAG_ID = "id";
    private static final String TAG_LOCATION = "location";
    String _vid, _vloc, _vurl, vname_;
    TextView vname, vloc, vdate, vid;
    ImageView imgv, scrollImg;

    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    JSONArray items = null;

    SliderLayout sliderLayout;
    HashMap<String, String> Hash_file_maps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling_single_venue);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        scrollImg = (ImageView) findViewById(R.id.scrollImg);
        //notif bar color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(VenueDetailsActivity.this, R.color.black));
        }
        // Get items from previous Activity
        Intent i = getIntent();
        vname_ = i.getStringExtra("vname");
        //swipe pics
        Hash_file_maps = new HashMap<String, String>();
        sliderLayout = (SliderLayout) findViewById(R.id.slider);

//        _vid = i.getStringExtra("vid");

        //define url to fetch image from
        _vurl = "http://tukio.co.ke/applicationfiles/venueimages/" + vname_ + ".jpg";

//        vdate= (TextView) findViewById(R.id.venueDate);
        vloc = (TextView) findViewById(R.id.venueLocation);
        vid = (TextView) findViewById(R.id.VenueId);
        vname = (TextView) findViewById(R.id.venueName);

//        setTitle(vname_);

        if (vname != null) {
            vname.setText(vname_);
        }
//        vdate.setText("Opens everyday");

        initCollapsingToolbar(); //toolbar stuff
//        Fetch More Data about the Venue
        new LoadVenueData().execute();

        //Load image from server
//        imgv = (ImageView) findViewById(R.id.venueImage);
        LoadImageFromURL loadImage = new LoadImageFromURL();
        loadImage.execute();

//        GetPictures(); //swipe pics

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Favourite this", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

  /*  public void url() {

        String URLr_ = "http://www.tukio.co.ke/applicationfiles/fetchvenuedata.php/?idquery=B Club";
        String vname_2 = URLr_.replace(" ", "+");
        Toast.makeText(this, "Url" + vname_2, Toast.LENGTH_SHORT).show();
    }*/

    public class LoadImageFromURL extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
//                URL url = new URL("http://tukio.co.ke/applicationfiles/imageuploads/"+_imglink);
                URL url = new URL(_vurl);
                InputStream is = url.openConnection().getInputStream();
                Bitmap bitMap = BitmapFactory.decodeStream(is);
                return bitMap;
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
//            imgv.setImageBitmap(result);
            scrollImg.setImageBitmap(result);
        }

    }


    class LoadVenueData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(VenueDetailsActivity.this);
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        //getting  json and parse
        protected String doInBackground(String... args) {


            List<NameValuePair> params = new ArrayList<NameValuePair>();
//            String URL_ = "http://tukio.co.ke/applicationfiles/fetchvenuedata.php/?idquery="+_vid;

            String vname_2 = vname_.replace(" ", "+");
            String URL_ = "http://tukio.co.ke/applicationfiles/fetchvenuedata.php/?idquery=" + vname_2 + "&submit=Submit+Query";
            ;
         /*   try {
//                URLEncoder.encode(queryStr, "UTF-8");
//
                URL_= URLEncoder.encode("http://www.tukio.co.ke/applicationfiles/fetchvenuedata.php/?idquery="+vname_, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                Log.e("Yourapp", "UnsupportedEncodingException");
            }*/
            // post  GET parameters
//            HttpGet httpget = new HttpGet();
            params.add(new BasicNameValuePair("items", vname_2));
            // getting JSON string from URL
            String json = jsonParser.makeHttpRequest(URL_, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("JSON response: ", json);

            try {
                JSONArray jsonArr = new JSONArray(json);

                JSONObject jObj = jsonArr.getJSONObject(0);
                if (jObj != null) {

                    _vloc = jObj.getString(TAG_LOCATION);
                    _vid = jObj.getString(TAG_ID);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        //After completing background task Dismiss the progress dialog

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {

//vloc.setText(_vloc);
                    vloc.setText(_vloc);
                    vid.setText(_vid);
//                    Toast.makeText(VenueDetailsActivity.this, "Id "+_vid, Toast.LENGTH_SHORT).show();
                    GetPictures(_vid); //swipe pics
                }
            });

        }

    }


    public void GetPictures(String vid) {

//        String myid = vid.getText().toString();
//        Toast.makeText(this, "Place id "+myid, Toast.LENGTH_SHORT).show();
        Hash_file_maps.put("Image 1", "http://tukio.co.ke/applicationfiles/images/swipeimages/" + vid + "_1.jpg");
        Hash_file_maps.put("Image 2", "http://tukio.co.ke/applicationfiles/images/swipeimages/" + vid + "_2.jpg");
        Hash_file_maps.put("Image 3", "http://tukio.co.ke/applicationfiles/images/swipeimages/" + vid + "_3.jpg");

        for (String name : Hash_file_maps.keySet()) {

            TextSliderView textSliderView = new TextSliderView(VenueDetailsActivity.this);
            textSliderView
                    .description(name)
                    .image(Hash_file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", name);
            sliderLayout.addSlider(textSliderView);
        }
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(3000);
        sliderLayout.addOnPageChangeListener(this);
    }

    @Override
    protected void onStop() {

        sliderLayout.stopAutoCycle();

        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

        Toast.makeText(this, slider.getBundle().get("extra") + "", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {

        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbar.setTitle(" ");
        collapsingToolbar.setContentScrimColor(getResources().getColor(R.color.black));
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
//                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    collapsingToolbar.setTitle(vname_);
                    isShow = true;
                } else if (isShow) {

                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.venue_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.addFavourite) {
            addFav();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addFav() {
//        Toast.makeText(this, "Add to fav", Toast.LENGTH_SHORT).show();
        String new_venue_id = "0";
        //Read shared Pref
        SharedPreferences pref1stRun = PreferenceManager.getDefaultSharedPreferences(this);
        String stored_venue_ids = pref1stRun.getString("myVenues", "");

        if (stored_venue_ids.contains(_vid)) {
            Toast.makeText(this, "Venue already exists in favourites", Toast.LENGTH_SHORT).show();
            return;
        }
        if (stored_venue_ids.isEmpty()) {
            new_venue_id = _vid;
        }
//            SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(VenueDetailsActivity.this);
//            SharedPreferences.Editor editor = myPrefs.edit();
//            editor.putString("myVenues", new_venue_id);
//            editor.apply();

        if (!stored_venue_ids.isEmpty()){
        new_venue_id = stored_venue_ids + "," + _vid;
    }
            SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(VenueDetailsActivity.this);
            SharedPreferences.Editor editor = myPrefs.edit();
            editor.putString("myVenues", new_venue_id);
            editor.apply();
        Snackbar();

        Toast.makeText(this, "old: " + stored_venue_ids + "new: " + new_venue_id, Toast.LENGTH_SHORT).show();

    }
    public void Snackbar() {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                "Added to favourites", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        snackbar.setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }

}