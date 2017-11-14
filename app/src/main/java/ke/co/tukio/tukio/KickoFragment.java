package ke.co.tukio.tukio;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.channguyen.rsv.RangeSliderView;
import com.google.android.gms.appdatasearch.DocumentContents;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class KickoFragment extends Fragment implements  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    View v;
    TextView kickmsg, gpsmsg, loctxtv,distText, searchMsgTV, venueID, distInKm;
    Button btnKick;
    ImageView hotimg, venueimg;
    ImageButton aboutKicko;
    ScrollView myScroll;
String ResponseFromPhp, LONGserver="0", LATserver="0", _cid, _cname, _cloc, _cdist, _ckicko, _imgurl=null;
    private static final String TAG_ID = "id";
    private static final String TAG_LOCATION = "location";
    private static final String TAG_NAME = "manager_club";
    private static final String TAG_DIST = "club_distance";
    private static final String TAG_KICKO = "kickobar";
    //Images & colors
    private Bitmap mFinalBitmap;
    private int mColorCode;
    boolean GpsStatus ;
    LocationManager locationManager ;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    ProgressDialog pDialog3;
    JSONParser jsonParser = new JSONParser();
    private GPSTracker2 gpsTracker;
    List<CharSequence> placeslist = new ArrayList<CharSequence>();
    private RangeSliderView smallSlider;

    public static KickoFragment newInstance() {
        KickoFragment fragment = new KickoFragment();

        return fragment;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

                 v = inflater.inflate(R.layout.fragment_kicko, container, false);
         kickmsg = (TextView) v.findViewById(R.id.kickMsg);
         venueID = (TextView) v.findViewById(R.id.placeID);
         gpsmsg = (TextView) v.findViewById(R.id.GPSMsg);
         loctxtv = (TextView) v.findViewById(R.id.locationLL);
         searchMsgTV = (TextView) v.findViewById(R.id.searchMessage);
         distText = (TextView) v.findViewById(R.id.distanceToTV);
         distInKm = (TextView) v.findViewById(R.id.venueDistanceInKm);
        hotimg = (ImageView) v.findViewById(R.id.kickimg);
        venueimg = (ImageView) v.findViewById(R.id.venueImg);
btnKick= (Button) v.findViewById(R.id.kickingBtn);
        myScroll = (ScrollView) v.findViewById(R.id.myScrolllView);
        aboutKicko = (ImageButton) v.findViewById(R.id.aboutKicko);

        //range bar
        smallSlider = (RangeSliderView) v.findViewById(R.id.rsv_small);
//        smallSlider.setInitialIndex(3);
//        smallSlider.setFilledColor(Color.BLUE);
        //makes the slider inactive
        smallSlider.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        //actions when sliding
//        final RangeSliderView.OnSlideListener listener = new RangeSliderView.OnSlideListener() {
//            @Override
//            public void onSlide(int index) {
//                Toast.makeText(getActivity(), "Hi index: " + index,Toast.LENGTH_SHORT) .show();
//            }
//        };
//        smallSlider.setOnSlideListener(listener);

        //end range bar
        checkNet();



        return v;
    }



    public void checkNet(){
//        TextView kickmsg = (TextView) getActivity().findViewById(R.id.kickMsg);
        ConnectivityManager cn = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf = cn.getActiveNetworkInfo();
        if (nf != null && nf.isConnected() == true) {

//            kickmsg.setText("You are connected");
            kickmsg.setVisibility(View.GONE);

            //CHANGE COLOURS
            mColorCode = Color.parseColor("#04982B");
            Drawable sourceDrawable = getResources().getDrawable(R.drawable.kickobar_fire);
            Bitmap sourceBitmap = UtilsColor.convertDrawableToBitmap(sourceDrawable);
            mFinalBitmap = UtilsColor.changeImageColor(sourceBitmap, mColorCode);
            hotimg.setImageBitmap(mFinalBitmap);

            //hotimg.setImageDrawable(getResources().getDrawable(R.drawable.hot_green));
            hotimg.setEnabled(false);
            CheckGpsStatus() ;
            if(GpsStatus == true)
            {
                /*gpsmsg.setText("Location services is enabled");
                buildGoogleApiClient();
                if(mGoogleApiClient!= null){
                    mGoogleApiClient.connect();
                }
                else
                    Toast.makeText(getActivity(), "Not connected...", Toast.LENGTH_SHORT).show();
            */
                getLocation();
            }

            else {
                smallSlider.setVisibility(View.GONE);
                gpsmsg.setText("Location service is disabled.\nClick here to enable");
                gpsmsg.setTextColor(Color.parseColor("#43B2F2"));
                searchMsgTV.setText("Helps you find nearby venues");
//                searchMsgTV.setText("Searching for places near you...");
//                turnON.setVisibility(View.VISIBLE);

            }
        }
        else {

            smallSlider.setVisibility(View.GONE);
            kickmsg.setText("Unable to search for nearby venues./\nTurn on your internet and GPS");
            searchMsgTV.setText("Helps you find nearby venues");
//            searchMsgTV.setText("Unable to search for nearby places");
            //hotimg.setImageDrawable(getResources().getDrawable(R.drawable.hot_grey));
            //CHANGE COLOURS
            mColorCode = Color.parseColor("#DADADA");
            Drawable sourceDrawable = getResources().getDrawable(R.drawable.kickobar_fire);
            Bitmap sourceBitmap = UtilsColor.convertDrawableToBitmap(sourceDrawable);
            mFinalBitmap = UtilsColor.changeImageColor(sourceBitmap, mColorCode);
            hotimg.setImageBitmap(mFinalBitmap);

            hotimg.setEnabled(false);
        }
    }
    public void CheckGpsStatus(){

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }





    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        Toast.makeText(getActivity(), "Failed to connect...", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnected(Bundle arg0) {

       /* mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        hotimg.setImageDrawable(getResources().getDrawable(R.drawable.hot_red));

        Location loc1= new Location("");
        loc1.setLatitude(mLastLocation.getLatitude());
        loc1.setLongitude(mLastLocation.getLongitude());

        Location loc2 =new Location("");
        loc2.setLatitude(-1.2895252);
        loc2.setLongitude(36.8174148);

        float distancebtw=loc1.distanceTo(loc2);
        if (mLastLocation != null) {
            loctxtv.setText("Latitude: "+ String.valueOf(mLastLocation.getLatitude())+"\nLongitude: "+
                    String.valueOf(mLastLocation.getLongitude())+"\nDistance "+distancebtw +" m");
        }*/

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        Toast.makeText(getActivity(), "Connection suspended...", Toast.LENGTH_SHORT).show();

    }
    public void getLocation(){
//        Toast.makeText(getActivity(), "Running search", Toast.LENGTH_SHORT).show();

//        new FetchVenuesFromServer2().execute(); // call method to fetch places and subtract

        gpsTracker = new GPSTracker2(getActivity());
        //
        if (gpsTracker.canGetLocation())
        {
            //your actual location
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();


            Location loc1= new Location("");
            loc1.setLatitude(latitude);
            loc1.setLongitude(longitude);

            //send you loc to server for analysis
            if (!String.valueOf(latitude).contentEquals("0.0") || !String.valueOf(longitude).contentEquals("0.0")){
                LATserver = String.valueOf(latitude);
                LONGserver = String.valueOf(longitude);
                Toast.makeText(getActivity(), "Proceed "+ LATserver+" "+LONGserver, Toast.LENGTH_SHORT).show();
//                sendToServer(String.valueOf(latitude), String.valueOf(longitude));
                new DistanceAnalysis().execute();
            }



            //the nearest location
//            Location loc2 =new Location("");
//            loc2.setLatitude(-1.3225671906309513);
//            loc2.setLongitude(36.80201323071447);

            //distance btwn your location and nearest venue
//            float distancebtw=loc1.distanceTo(loc2);

            gpsmsg.setVisibility(View.GONE);
//            btnKick.setVisibility(View.VISIBLE);

            //CHANGE COLOURS
            mColorCode = Color.parseColor("#ff0000");
            Drawable sourceDrawable = getResources().getDrawable(R.drawable.kickobar_fire);
            Bitmap sourceBitmap = UtilsColor.convertDrawableToBitmap(sourceDrawable);
            mFinalBitmap = UtilsColor.changeImageColor(sourceBitmap, mColorCode);
            hotimg.setImageBitmap(mFinalBitmap);
            /*
//            Animation startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blinking_anim);
            Animation startAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.blinking_anim);
//            btnKick.startAnimation(startAnimation);
            hotimg.setImageDrawable(getResources().getDrawable(R.drawable.hot_red));
            hotimg.startAnimation(startAnimation);
            */

            //START
            loctxtv.setText("Your location: " + latitude +", " + longitude);
            searchMsgTV.setText("Searching for nearby venues.");

            DecimalFormat fmt = new DecimalFormat("#,###,###");
//            String dist = fmt.format(distancebtw/1000);
//            if (distancebtw>=1000) {
//                distText.setText(dist + " km away from venue");
//            }
//            else if (distancebtw<1000){
//            distText.setText(distancebtw+" m left");
////            Toast.makeText(getApplicationContext(), "Lat: " + latitude +"Long:" + longitude, Toast.LENGTH_LONG).show();
//            }
        }
        else
        {
            // show message box if not available
            gpsTracker.showSettingsAlert();
        }
    }


    class DistanceAnalysis extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //getting  json and parse
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            String URL_ = "http://tukio.co.ke/applicationfiles/distance.php?lat=" + LATserver + "&long=" + LONGserver + "&submit=Submit+Query";


            params.add(new BasicNameValuePair("lat", LATserver));
            params.add(new BasicNameValuePair("long", LONGserver));
            // getting JSON string from URL
            String json = jsonParser.makeHttpRequest(URL_, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("JSON response: ", json);

            try {
                JSONArray jsonArr = new JSONArray(json);

                JSONObject jObj = jsonArr.getJSONObject(0);
                if (jObj != null) {

                    _cloc = jObj.getString(TAG_LOCATION);
                    _cdist = jObj.getString(TAG_DIST);
                    _cid = jObj.getString(TAG_ID);
                    _cname = jObj.getString(TAG_NAME);
                    _ckicko = jObj.getString(TAG_KICKO);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        //After completing background task Dismiss the progress dialog

        protected void onPostExecute(String file_url) {

            //removes nullpointer exception when you switch tabs very fast
            if(getActivity() == null)
                return;
            // updating UI from Background Thread
            getActivity().runOnUiThread(new Runnable() {

                public void run() {


                    venueID.setText(_cid);
                    btnKick.setVisibility(View.VISIBLE);
                    venueimg.setVisibility(View.VISIBLE);
                    aboutKicko.setVisibility(View.VISIBLE);
                    myScroll.setBackgroundColor(Color.parseColor("#89000000"));
                    searchMsgTV.setText("Found: " + _cname+"\nat "+_cloc);
//                    searchMsgTV.setBackgroundResource(R.drawable.buttonbg);
                    searchMsgTV.setTextColor(Color.WHITE);
                    distText.setTextColor(Color.WHITE);
                    loctxtv.setTextColor(Color.WHITE);

                    distText.setText(_cdist + " km away\n(Kickobar: "+_ckicko+ ")");
                    distInKm.setText(_cdist);

                    //call method to fetch venue's picture
                    FetchVenueImage(_cname);
                    int mykicko=Integer.parseInt(_ckicko);
                    if(mykicko==0){
                        smallSlider.setInitialIndex(0);
                        smallSlider.setFilledColor(ContextCompat.getColor(getContext(), R.color.notkicking));
                        //CHANGE COLOURS
//                        mColorCode = Color.parseColor("#32cd32");
                        mColorCode = ContextCompat.getColor(getContext(), R.color.notkicking);
                        Drawable sourceDrawable = getResources().getDrawable(R.drawable.kickobar_fire);
                        Bitmap sourceBitmap = UtilsColor.convertDrawableToBitmap(sourceDrawable);
                        mFinalBitmap = UtilsColor.changeImageColor(sourceBitmap, mColorCode);
                        hotimg.setImageBitmap(mFinalBitmap);
                    }
                    if(mykicko>0 && mykicko<10){
                        smallSlider.setInitialIndex(1);
                        smallSlider.setFilledColor(ContextCompat.getColor(getContext(), R.color.slightly));
                        //CHANGE COLOURS
//                        mColorCode = Color.parseColor("#ffc125");
                        mColorCode = ContextCompat.getColor(getContext(), R.color.slightly);
                        Drawable sourceDrawable = getResources().getDrawable(R.drawable.kickobar_fire);
                        Bitmap sourceBitmap = UtilsColor.convertDrawableToBitmap(sourceDrawable);
                        mFinalBitmap = UtilsColor.changeImageColor(sourceBitmap, mColorCode);
                        hotimg.setImageBitmap(mFinalBitmap);
                    }
                    if(mykicko>=10 && mykicko<25){
                        smallSlider.setInitialIndex(2);
                        smallSlider.setFilledColor(ContextCompat.getColor(getContext(), R.color.moderate));
                        //CHANGE COLOURS
//                        mColorCode = Color.parseColor("#ff7f00");
                        mColorCode = ContextCompat.getColor(getContext(), R.color.moderate);
                        Drawable sourceDrawable = getResources().getDrawable(R.drawable.kickobar_fire);
                        Bitmap sourceBitmap = UtilsColor.convertDrawableToBitmap(sourceDrawable);
                        mFinalBitmap = UtilsColor.changeImageColor(sourceBitmap, mColorCode);
                        hotimg.setImageBitmap(mFinalBitmap);
                    }
                    if(mykicko>25 && mykicko<50){
                        smallSlider.setInitialIndex(3);
//                        smallSlider.setFilledColor(Color.parseColor("#ff4500"));
                        smallSlider.setFilledColor(ContextCompat.getColor(getContext(), R.color.kicking));
                        //CHANGE COLOURS
//                        mColorCode = Color.parseColor("#ff4500");
                        mColorCode = ContextCompat.getColor(getContext(), R.color.kicking);
                        Drawable sourceDrawable = getResources().getDrawable(R.drawable.kickobar_fire);
                        Bitmap sourceBitmap = UtilsColor.convertDrawableToBitmap(sourceDrawable);
                        mFinalBitmap = UtilsColor.changeImageColor(sourceBitmap, mColorCode);
                        hotimg.setImageBitmap(mFinalBitmap);
                    }
                    if(mykicko>=50){
                        smallSlider.setInitialIndex(4);
                        smallSlider.setFilledColor(Color.parseColor("#ee0000"));
                        //CHANGE COLOURS
                    //    mColorCode = Color.parseColor("#ee0000");
                        mColorCode = ContextCompat.getColor(getContext(), R.color.lit); //Android m (03-11-2014)
                        Drawable sourceDrawable = getResources().getDrawable(R.drawable.kickobar_fire);
                        Bitmap sourceBitmap = UtilsColor.convertDrawableToBitmap(sourceDrawable);
                        mFinalBitmap = UtilsColor.changeImageColor(sourceBitmap, mColorCode);
                        hotimg.setImageBitmap(mFinalBitmap);
                    }
//                    else {
//                        smallSlider.setInitialIndex(1);
//                        smallSlider.setFilledColor(Color.parseColor("#666666"));
//                    }
                }
            });

        }

    }

    public void FetchVenueImage(String venuename){

        _imgurl="http://tukio.co.ke/applicationfiles/venueimages/"+venuename+".jpg";
        Glide.with(KickoFragment.this)
                .load(_imgurl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(venueimg);

//        LoadImageFromURL loadImage = new LoadImageFromURL();
//        loadImage.execute();
    }
    public class LoadImageFromURL extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                URL url = new URL(_imgurl);
                InputStream is = url.openConnection().getInputStream();
                Bitmap bitMap = BitmapFactory.decodeStream(is);
                return bitMap;
            } catch (MalformedURLException e)
            {
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
            venueimg.setImageBitmap(result);
        }

}

}
