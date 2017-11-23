package ke.co.tukio.tukio;


import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.ScrollableViewHelper;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.afinal.simplecache.ACache;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ke.co.tukio.tukio.recycler.GetDataAdapterVen;
import ke.co.tukio.tukio.recycler.RecyclerViewAdapterVen;
import test.jinesh.lib.GoogleMapStyler;

public class PlacesFragment extends Fragment implements OnMapReadyCallback {

    private GPSTracker2 gpsTracker;

    //anime
    private Circle lastUserCircle;
    private static final long PULSE_DURATION = 4000;
    private static final float RADIUS = 250.0f;
    private ValueAnimator lastPulseAnimator;
    //slide up
    private SlidingUpPanelLayout mLayout;
    private static final String TAG = "PlacesFragment";
    //    TextView textView;
    Context mContext; //added for Google map styling
    //end slider up

    //RECYCLER
    String JSON_VENUE_NAME = "manager_club";
    String JSON_VENUE_ID = "id";
    String JSON_VENUE_LOCATION = "location";
    String JSON_VENUE_KICKOBAR = "kickobar";

    List<GetDataAdapterVen> GetDataAdapter3;
    RecyclerView recyclerViewVen;
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    RecyclerView.Adapter recyclerViewadapterVen;
    //end recycler

    private GoogleMap mMap;
    String publicjson = "empty string";
    ArrayList<HashMap<String, String>> PlaceList;

    public static PlacesFragment newInstance() {
        PlacesFragment fragment = new PlacesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_places, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        jsonFetchFromLocal();
//       GoogleMap map = (SupportMapFragment)v.getSu

        //slider
        mLayout = (SlidingUpPanelLayout) v.findViewById(R.id.sliding_layout);
//        textView = (TextView) v.findViewById(R.id.list_main);

        //   listview = (ListView) findViewById(R.id.list);
//            init();            // call init method
//            setListview();    // call setListview method
        panelListener();  // Call paneListener method

        //recycler
        //recycler
        GetDataAdapter3 = new ArrayList<>();
        recyclerViewVen = (RecyclerView) v.findViewById(R.id.recyclerviewVenues);
        recyclerViewVen.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewVen.setLayoutManager(recyclerViewlayoutManager);
        GetVenues();// method to fetch cached data
        return v;
    }

    public void jsonFetchFromLocal() {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            //Attaching BufferedReader to the FileInputStream by the help of InputStreamReader
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(getActivity().openFileInput("jsonMapsDatafile.txt")));
            String inputString;
            //Reading data line by line and storing it into the stringbuffer
            while ((inputString = inputReader.readLine()) != null) {
                stringBuffer.append(inputString + "\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        String jsonData = stringBuffer.toString();
        publicjson = jsonData;
//    Toast.makeText(getContext(), "jj"+ publicjson, Toast.LENGTH_SHORT).show();
        //you can access stringBuffer.toString() here
//    PrepareLoop(jsonData);

    }


/*
    try {
        CreateMarkers(jsonData.toString());
//        publicjson=jsonData;
//        createMarkersFromJson(json.toString());
    } catch (JSONException e) {
        Log.e("LOG_TAG", "Error processing JSON", e);
    }
//    CreateMarkers(jsonData);

}*/
/*    public void CreateMarkers(String jsonDataa) throws JSONException {
//        mMap = googleMap;
        JSONArray jsonArray = new JSONArray(jsonDataa);
        for (int i = 0; i < jsonArray.length(); i++) {
            // Create a marker for each city in the JSON data.
            JSONObject jsonObj = jsonArray.getJSONObject(i);
//            mMap.addMarker(new MarkerOptions()
//                    .title(jsonObj.getString("name"))
//                    .position(new LatLng(jsonObj.getDouble("lat"), jsonObj.getDouble("long")))
//                    .snippet(Integer.toString(jsonObj.getInt("population")))
//                    .position(new LatLng(
//                            jsonObj.getJSONArray("lat").getDouble(0),
//                            jsonObj.getJSONArray("long").getDouble(1)
//                    ))
//            );
        }
    }*/

    public void PrepareLoop(String jsonStr) {
        try {
            JSONObject jsonObj = new JSONObject(jsonStr);

            // Getting JSON Array node
            JSONArray places = jsonObj.getJSONArray("places");

            // looping through All
            for (int i = 0; i < places.length(); i++) {
                JSONObject c = places.getJSONObject(i);
                String id = c.getString("id");
                String name = c.getString("name");
                String manager = c.getString("manager");
                String lat = c.getString("lat");
                String longg = c.getString("long");

                // tmp hash map for single place
                HashMap<String, String> place = new HashMap<>();

                // adding each child node to HashMap key => value
                place.put("id", id);
                place.put("name", name);
                place.put("manager", manager);
                place.put("lat", lat);
                place.put("long", longg);

                // adding place to place list
                PlaceList.add(place);

            }
        } catch (final JSONException e) {
            Log.e("PLACES", "Json parsing error: " + e.getMessage());


//            int id;
//        String name;
//        JSONArray array = new JSONArray(string_of_json_array);
//        for (int i = 0; i < array.length(); i++) {
//            JSONObject row = array.getJSONObject(i);
//            id = row.getInt("id");
//            name = row.getString("name");
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;


        //show users current location - 04-11-17
        mMap.setMyLocationEnabled(true);


//        Toast.makeText(getContext(), "jj"+ publicjson, Toast.LENGTH_SHORT).show();
        double longitude = -1.264451, latitude = 36.804580; //initialized to National Archives
        //to add current location of a uer
        gpsTracker = new GPSTracker2(getActivity());
        if (gpsTracker.canGetLocation()) {
            //your actual location old code till 04-11-17
//            latitude = gpsTracker.getLatitude();
//             longitude = gpsTracker.getLongitude();
            addPulsatingEffect();
        } else {
            Toast.makeText(getActivity(), "Turn on you location to find venues near you.", Toast.LENGTH_LONG).show();
        }
//        LatLng locc = new LatLng(-1.264451, 36.804580);
        LatLng locc = new LatLng(latitude, longitude);
//        mMap.addMarker(new MarkerOptions().position(locc).title("My location").icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin_home)).title("My location").snippet("You are here"));
//        mMap.addMarker(new MarkerOptions().position(locc).title("My location").icon(BitmapDescriptorFactory.fromResource(R.drawable.person_icon_koteng)).title("My location").snippet("You are here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(locc));
//end user location

        //style the map
        mMap.setMapStyle(googleMapStyler.getMapStyleOptions());

        try {
            JSONArray jsonArray = new JSONArray(publicjson);
//            Toast.makeText(getContext(), "Saved venues for map: " + jsonArray.length(), Toast.LENGTH_SHORT).show();
            for (int i = 0; i < jsonArray.length(); i++) {
                // Create a marker for each city in the JSON data.
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                mMap.addMarker(new MarkerOptions()
                                .title(jsonObj.getString("manager_club"))
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin))
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_red_star))
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_red_star))
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_red_star))
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_red_star))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_blue_with_drop_six_four)) //iconfinder
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_blue_waterdroop))
//                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
//                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.))
//                              .snippet(jsonObj.getString("manager_name")+" Kickobar "+jsonObj.getString("kickobar"))
                                .snippet("Kickobar " + jsonObj.getString("kickobar"))

                                .position(new LatLng(jsonObj.getDouble("latitude"), jsonObj.getDouble("longitude")))
//                    .snippet(Integer.toString(jsonObj.getInt("name")))
//                    .position(new LatLng(
//                            jsonObj.getJSONArray("lat").getDouble(0),
//                            jsonObj.getJSONArray("long").getDouble(1)
//                    ))
//
                );

            }
        } catch (JSONException e) {
            Log.e("PLACES", "Json parsing error: " + e.getMessage());
        }


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-1.2840526270690147, 36.824231734168634), 11.0f));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {

                if (marker.equals(marker)) {

                    if (marker.getSnippet().contentEquals("You are here")) {
                        Toast.makeText(getActivity(), "You are here", Toast.LENGTH_SHORT).show();
                        return true;
                    }


                    //handle click here
                    final Dialog dialog2 = new Dialog(getActivity());
                    dialog2.setContentView(R.layout.customdialogvenue);
                    Button dialogButton = (Button) dialog2.findViewById(R.id.dialogButtonOK);
                    TextView venuename = (TextView) dialog2.findViewById(R.id.venueName);
                    TextView kicko = (TextView) dialog2.findViewById(R.id.kickobarTV);
                    TextView venueText = (TextView) dialog2.findViewById(R.id.venueDescriprion);
                    ImageView kimg = (ImageView) dialog2.findViewById(R.id.kickoImgDialog);


                    String kickoString = marker.getSnippet();
                    String[] separated = kickoString.split(" ");
                    int mColorCode = 0;

                    int kickoValue = Integer.parseInt(separated[1]);
                    if(kickoValue<0){
                        mColorCode = ContextCompat.getColor(getContext(), R.color.black);
//                        mColorCode = Color.parseColor("#32cd32");
                    }
                    if (kickoValue == 0 && kickoValue < 10) {
                        mColorCode = ContextCompat.getColor(getContext(), R.color.notkicking);
//                        mColorCode = Color.parseColor("#ffc125");
                    }
                    if (kickoValue >= 10 && kickoValue < 25) {
                        mColorCode = ContextCompat.getColor(getContext(), R.color.slightly);
//                        mColorCode = Color.parseColor("#ff7f00");
                    }

                    if (kickoValue > 25 && kickoValue < 50) {
                        mColorCode = ContextCompat.getColor(getContext(), R.color.kicking);
//
                    }
                    if (kickoValue >= 50) {
                        mColorCode = ContextCompat.getColor(getContext(), R.color.lit);
                    }
                    //CHANGE COLOURS
//                    int mColorCode = Color.parseColor("#ff0000");
                    Drawable sourceDrawable = getResources().getDrawable(R.drawable.kicko_fire_white__one_two_eight);
                    Bitmap sourceBitmap = UtilsColor.convertDrawableToBitmap(sourceDrawable);
                    Bitmap mFinalBitmap = UtilsColor.changeImageColor(sourceBitmap, mColorCode);
                    kimg.setImageBitmap(mFinalBitmap);

                    venuename.setText(marker.getTitle());
                    kicko.setText(marker.getSnippet());
                    if (kickoValue<0) {
                        venueText.setText("Oops! "+marker.getTitle() + " is not available today.\nCheck out some other time.");
                    }
                    else {
                        venueText.setText("Get to " + marker.getTitle() + " to kick the place on the Kickobar.");
                    }
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //check internet connec
                            ConnectivityManager cn = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo nf = cn.getActiveNetworkInfo();
                            if (nf != null && nf.isConnected() == true) {
                                Intent i = new Intent(getActivity(), VenueDetailsActivity.class);
                                i.putExtra("vname", String.valueOf(marker.getTitle()));
                                getActivity().startActivity(i);
                            } else {
                                Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
                            }
                            dialog2.dismiss();

                        }
                    });
                    dialog2.show();
                    Window window = dialog2.getWindow();
                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//                    Toast.makeText(getActivity(), "Click " + marker.getTitle()+ marker.getSnippet(), Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getActivity(), "Not place", Toast.LENGTH_SHORT).show();
                return true;
            }

        });
    }


    //slider
    public void panelListener() {

        mLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

            // During the transition of expand and collapse onPanelSlide function will be called.
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.e(TAG, "onPanelSlide, offset " + slideOffset);
            }

            // This method will be call after slide up layout
            @Override
            public void onPanelExpanded(View panel) {
                Log.e(TAG, "onPanelExpanded");

            }

            // This method will be call after slide down layout.
            @Override
            public void onPanelCollapsed(View panel) {
                Log.e(TAG, "onPanelCollapsed");

            }

            @Override
            public void onPanelAnchored(View panel) {
                Log.e(TAG, "onPanelAnchored");
            }

            @Override
            public void onPanelHidden(View panel) {
                Log.e(TAG, "onPanelHidden");
            }
        });
    }


    //populate Recycler
    public void GetVenues() {
        ACache mCache = ACache.get(getActivity());
        JSONArray value2 = mCache.getAsJSONArray("tukio_venues");
//        Toast.makeText(getActivity(), "Cached venues for slider: " + value2.length(), Toast.LENGTH_SHORT).show();

        if (value2.length() == 0) {
            Toast.makeText(getActivity(), "No cached data", Toast.LENGTH_SHORT).show();
        } else {
            JSON_PARSE_CACHED_DATA_VENUES(value2);
        }
    }

    public void JSON_PARSE_CACHED_DATA_VENUES(JSONArray array) {  //No internet, load this to the recyclerview
//        CacheData(array); //send data to the cache method
        for (int i = 0; i < array.length(); i++) {
            GetDataAdapterVen GetDataAdapter4 = new GetDataAdapterVen();
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);
                GetDataAdapter4.setVenName(json.getString(JSON_VENUE_NAME));
                GetDataAdapter4.setVenId(json.getString(JSON_VENUE_ID));
                GetDataAdapter4.setVenLoc(json.getString(JSON_VENUE_LOCATION));
                GetDataAdapter4.setKicko(json.getString(JSON_VENUE_KICKOBAR));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            GetDataAdapter3.add(GetDataAdapter4);
        }
        recyclerViewadapterVen = new RecyclerViewAdapterVen(GetDataAdapter3, getActivity());
        recyclerViewVen.setAdapter(recyclerViewadapterVen);
    }

    GoogleMapStyler googleMapStyler = new GoogleMapStyler.Builder(getContext())


            .setMainGeometryColor(Color.parseColor("#d1d1d2"))

//            .setAllPlaceTextColor(Color.parseColor("#666666"))

            .setAllMainTownTextColor(Color.parseColor("#000000"))
//            .setAllPoiTextColor(Color.parseColor("#ff0000"))
//            parks
            .setAllPoiParkBackgroundColor(Color.parseColor("#baeec7"))
            .setAllPoiParkTextColor(Color.parseColor("#0b28cc"))
            //roads
            .setAllRoadTextColor(Color.parseColor("#2E4066"))
            .setAllRoadBackgroundColor(Color.parseColor("#C4C2C0")) //was read
            .setAllRoadArterialStrokeColor(Color.parseColor("#212a37"))
            //rpad highwa
            .setAllRoadHighwayBackgroundColor(Color.parseColor("#746855"))
            .setAllRoadHighwayTextColor(Color.parseColor("#666666"))  // Langat Road
            .setAllRoadHighwayStrokeColor(Color.parseColor("#ffffff"))

            //road local
            .setAllRoadLocalBackgroundColor(Color.parseColor("#ffffff"))
            .setAllRoadLocalStrokeColor(Color.parseColor("#212a37"))
            //transit
            .setAllTransitStationTextColor(Color.parseColor("#000000")) //d59563
            .setAllTransitBackgroundColor(Color.parseColor("#b9666666"))  //airport
            //waters
            .setAllWaterTextColor(Color.parseColor("#ffffff"))
            .setAllWaterBackgroundColor(Color.parseColor("#73CCFF"))

            .build();


    //animate current location
    private void addPulsatingEffect2(Location userLocation) {

    }

    private void addPulsatingEffect() {
        gpsTracker = new GPSTracker2(getActivity());
//        if (gpsTracker.canGetLocation()) {
        //your actual location old code till 04-11-17
        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();
//        }

//        final LatLng userLatlng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
        final LatLng userLatlng = new LatLng(latitude, longitude);
        if (lastPulseAnimator != null) {
            lastPulseAnimator.cancel();
        }

        if (lastUserCircle != null) {
            lastUserCircle.setCenter(userLatlng);
        }

        lastPulseAnimator = valueAnimate(RADIUS, new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                if (lastUserCircle != null)
                    lastUserCircle.setRadius((Float) animation.getAnimatedValue());
                else {
                    lastUserCircle = mMap.addCircle(new CircleOptions()
                            .center(userLatlng)
                            .radius((Float) animation.getAnimatedValue())
                            .strokeColor(Color.BLACK)
                            .fillColor(Color.parseColor("#B541B5CF"))
                            .strokeWidth(0f));
                }
            }
        });
    }

    protected ValueAnimator valueAnimate(float radius, ValueAnimator.AnimatorUpdateListener updateListener) {

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, radius);
        valueAnimator.setDuration(PULSE_DURATION);
        valueAnimator.addUpdateListener(updateListener);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);

        valueAnimator.start();
        return valueAnimator;
    }
}


//        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
//        {
//            @Override
//            public void onMapClick(LatLng arg0)
//            {
//                Toast.makeText(getContext(), "Mpa clicked", Toast.LENGTH_SHORT).show();
//            }
//        });


//old code
// Add a marker in Sydney and move the camera
//        LatLng locc = new LatLng(-1.264451, 36.804580);
//        mMap.addMarker(new MarkerOptions().position(locc).title("Privee- Westlands"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(locc));
//
//        LatLng loc2 = new LatLng(-1.2906902, 36.7808174);
//        mMap.addMarker(new MarkerOptions().position(loc2).title("B-Club"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc2));

//        LatLng loc3 = new LatLng(PlaceList.hashCode("lat"), 36.7808174);
//        mMap.addMarker(new MarkerOptions().position(loc2).title("B-Club"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc3));


//        mMap.setMaxZoomPreference(2.0f);
//        mMap.setMinZoomPreference(14.0f);

// ArrayList<LatLng> coordinates; // your ArrayList with marker's coordinates
//        int size = PlaceList.size();
//        Toast.makeText(getContext(), "Size "+size, Toast.LENGTH_SHORT).show();
//        for (int i = 0; i < size; ++i) {
//            LatLng coordinate = PlaceList.get(i);
//
//            googleMap.addMarker(new MarkerOptions().position(coordinate).title("Title" + (i + 1)).snippet("Snippet" + (i + 1))
//                    .anchor(0.5f, 0.5f));
//        }
//    }
//
//}
