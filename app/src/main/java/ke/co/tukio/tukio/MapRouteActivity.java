package ke.co.tukio.tukio;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.ahmadrosid.lib.drawroutemap.DrawMarker;
import com.ahmadrosid.lib.drawroutemap.DrawRouteMaps;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class MapRouteActivity extends AppCompatActivity implements OnMapReadyCallback {

    //pulse

    //anime
    private Circle lastUserCircle;
    private static final long PULSE_DURATION = 4000;
    private static final float RADIUS = 250.0f;
    private ValueAnimator lastPulseAnimator;

    private GoogleMap mMap;
    private GPSTracker2 gpsTracker;
    String longi, lati, vname;
    Double Dlat, Dlong, myLat, myLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_route);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(MapRouteActivity.this, R.color.colorPrimary));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle(null);
        Intent i = getIntent();
        lati = i.getStringExtra("lat");
        longi= i.getStringExtra("long");
        vname = i.getStringExtra("vname");
        getSupportActionBar().setTitle(vname);

        Dlat = Double.parseDouble(lati);
        Dlong = Double.parseDouble(longi);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        gpsTracker = new GPSTracker2(MapRouteActivity.this);
        if (gpsTracker.canGetLocation()) {
            //your actual location old code till 04-11-17
             myLat = gpsTracker.getLatitude();
             myLong = gpsTracker.getLongitude();

            Location myLoc =new Location("");
            Location venuesLoc =new Location("");
            myLoc.setLatitude(myLat);
            myLoc.setLongitude(myLong);
            venuesLoc.setLatitude(Dlat);
            venuesLoc.setLongitude(Dlong);

            //distance btwn your location and nearest venue
            float distancebtw=myLoc.distanceTo(venuesLoc );
            float dist2 = distancebtw/1000;
            getSupportActionBar().setSubtitle(dist2+" km away");
            mapFragment.getMapAsync(this);
        }
        else{
            Toast.makeText(this, "Couldn't get your location.", Toast.LENGTH_SHORT).show();
        }
    }

        @Override
        public void onMapReady (GoogleMap googleMap){
            mMap = googleMap;
            googleMap.setMyLocationEnabled(true);
//            start anim
            final   LatLng userLatlng = new LatLng(myLat, myLong);
            if(lastPulseAnimator != null){
                lastPulseAnimator.cancel();
            }
            if(lastUserCircle != null) {
                lastUserCircle.setCenter(userLatlng);
            }
            lastPulseAnimator = valueAnimate(RADIUS, new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if(lastUserCircle != null)
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
            //end anim
            LatLng origin = new LatLng(myLat, myLong);
            LatLng destination = new LatLng(Dlat, Dlong);
            DrawRouteMaps.getInstance(this)
                    .draw(origin, destination, mMap);
//            DrawMarker.getInstance(this).draw(mMap, origin, R.drawable.map_person_koteng, "You");
            DrawMarker.getInstance(this).draw(mMap, destination, R.drawable.map_marker_blue_with_drop_four_eight, vname);

            LatLngBounds bounds = new LatLngBounds.Builder()
                    .include(origin)
                    .include(destination).build();
            Point displaySize = new Point();
            getWindowManager().getDefaultDisplay().getSize(displaySize);
//            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, displaySize.x, 250, 30));
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, displaySize.x, 250, 10));
        }

    protected ValueAnimator valueAnimate(float radius, ValueAnimator.AnimatorUpdateListener updateListener){

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, radius);
        valueAnimator.setDuration(PULSE_DURATION);
        valueAnimator.addUpdateListener(updateListener);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);

        valueAnimator.start();
        return valueAnimator;
    }
}
