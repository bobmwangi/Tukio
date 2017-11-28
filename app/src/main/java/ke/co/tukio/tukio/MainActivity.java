package ke.co.tukio.tukio;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.afinal.simplecache.ACache;
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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog2, pDialog3, pDialog4;
    TextView venueIdKicko;
    String MyREsponse;
    private FirebaseAuth mAuth;  //to help in logout
    private GoogleApiClient mGoogleApiClient;  //to help in logout
    private String TAG = MainActivity.class.getSimpleName();
    String publicjson = "nothing";
    String useremail = "imagemissing";
    ArrayList<HashMap<String, String>> PlaceList;
    //    ArrayList dataModels;  //new to populate array
    List<CharSequence> list = new ArrayList<CharSequence>(); //checklist


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigation);

        disableShiftMode(bottomNavigationView);  //added to allow 4 items on the bottom nav - 23/08/2014
        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.action_home:
//                                selectedFragment = HomeFragment.newInstance();
                                selectedFragment = FragmentHome2.newInstance();
                                break;
                            case R.id.action_places:
                                selectedFragment = PlacesFragment.newInstance();
                                break;
                            case R.id.action_kicko:
                                selectedFragment = KickoFragment.newInstance();
                                break;
                            case R.id.action_profile:
                                selectedFragment = ProfileFragment.newInstance();
//                                getSupportActionBar().hide();
                                break;
                        }
//                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment, "TAG").commit();
//                        return true;
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.frame_layout, HomeFragment.newInstance());
        transaction.replace(R.id.frame_layout, FragmentHome2.newInstance());
        transaction.commit();

        // [START initialize_auth]   //initialize Firebase so you can be able to logout
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]


        //Used to select an item programmatically
        //bottomNavigationView.getMenu().getItem(2).setChecked(true);

      /*  TextView uname = (TextView) findViewById(R.id.username);
        String nm;
        SharedPreferences myprefs = PreferenceManager.getDefaultSharedPreferences(this);
        nm = myprefs.getString("username", "");
        uname.setText(nm);*/

        //load checklist
//        for (int i=0;i<20;i++){
//            list.add("test " + i);  // Add the item in the list
//        }

//        list = new ArrayList();
//        dataModels = new ArrayList();

//        list.add("Apple 1");
//        list.add("Apple 2");
//        list.add("Apple 3");
//        list.add("Apple 4");
//        list.add("Apple 5");
        //end check list
    }
// start menu
/*    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
                // Handle action bar actions click
        switch (item.getItemId()) {
           *//* case R.id.share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, "Hey, check out the official KU Android App. @\nhttps://goo.gl/4hZx03");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Kenyatta University\n");
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
                break;*//*
            //return true;
//            case R.id.menu:
//                become();
//                break;
            case R.id.refreshmap:
                refreshmap();
                break;
            //return true;
            default:
                return super.onOptionsItemSelected(item);
        }

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }*/
    //end menu

    /*public void trending(View v){
        Toast.makeText(this, "tt", Toast.LENGTH_SHORT).show();
        Intent pIntent = new Intent(getBaseContext(), EventsActivity.class);
        startActivityForResult(pIntent, 0);
    }*/
    public void Snackbar() {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                "No internet connection!", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
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

    public void ConnLoc() {
        ConnectivityManager cn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf = cn.getActiveNetworkInfo();
        if (nf != null && nf.isConnected() == true) {

            Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent1);
        } else Snackbar();
    }

   /* public void reminders(View v) {
        ConnectivityManager cn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf = cn.getActiveNetworkInfo();
        if (nf != null && nf.isConnected() == true) {

            Intent pIntent = new Intent(getBaseContext(), ReminderEventsActivity.class);
            startActivityForResult(pIntent, 0);
        } else Snackbar();
    }*/


    public void promote(View v) {
        Uri uri = Uri.parse("mailto:info@tukio.co.ke");
        Intent uu = new Intent(Intent.ACTION_SENDTO, uri);
        uu.putExtra(Intent.EXTRA_SUBJECT, "Promote event on Tukio");
        uu.putExtra(Intent.EXTRA_TEXT, "**Sent from Android v1.0**");
        startActivity(uu);
        /*
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_custom_dialog, null);
        final EditText event = (EditText) alertLayout.findViewById(R.id.eventED);
        final EditText phone = (EditText) alertLayout.findViewById(R.id.phoneED);
//        Edphone.setText(_telephone);

        android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(this);
        alert.setTitle("Tell us about the event");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.setPositiveButton("Send", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String ev = event.getText().toString();
                String ph = phone.getText().toString();
                if (ev.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Event name cannot be empty", Toast.LENGTH_SHORT).show();

                }
                if (ph.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please provide your phone number", Toast.LENGTH_SHORT).show();

                }
                else

                {

                    ConnectivityManager cn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo nf = cn.getActiveNetworkInfo();
                    if (nf != null && nf.isConnected() == true) {

                        pDialog2 = new ProgressDialog(MainActivity.this);
                        pDialog2.setMessage("Sending...");
                        pDialog2.setIndeterminate(false);
                        pDialog2.setCancelable(true);
                        pDialog2.show();
                        insertToDatabase(ev, ph);
                    }
                    else Snackbar();
                }


            }


        });
        android.support.v7.app.AlertDialog dialog = alert.create();
        dialog.show();
        */
    }
    /*public void support(View v)
    {
        Toast.makeText(this, "Feature under development.", Toast.LENGTH_SHORT).show();
    }*/

    public void about(View v) {
        final Dialog dialog2 = new Dialog(this);
        dialog2.setContentView(R.layout.customdialogabout);
        Button dialogButton = (Button) dialog2.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom dialog
//        dialog.setTitle("Welcome");
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("mailto:info@tukio.co.ke");
                Intent uu = new Intent(Intent.ACTION_SENDTO, uri);
                uu.putExtra(Intent.EXTRA_SUBJECT, "Promote event on Tukio");
                uu.putExtra(Intent.EXTRA_TEXT, "**Sent from Android v1.0**");
                startActivity(uu);
                dialog2.dismiss();

            }
        });
        dialog2.show();
//        Toast.makeText(this, "Feature under development.", Toast.LENGTH_SHORT).show();
    }


    public void share22(View v) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            //Attaching BufferedReader to the FileInputStream by the help of InputStreamReader
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(
                    openFileInput("jsonMapsDatafile.txt")));
            String inputString;
            //Reading data line by line and storing it into the stringbuffer
            while ((inputString = inputReader.readLine()) != null) {
                stringBuffer.append(inputString + "\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Displaying data on the toast
        Toast.makeText(getApplicationContext(), "Saved data" + stringBuffer.toString(),
                Toast.LENGTH_LONG).show();

    }


    /* public void share(View v){
         Intent pIntent = new Intent(getBaseContext(), EventsActivity.class);
         startActivityForResult(pIntent, 0);
     }*/
    public void share(View v) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Hey, check out Tukio Android App. @\nhttp://tukio.co.ke");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Tukio App\n");
        startActivity(Intent.createChooser(sharingIntent, "Share app using"));
    }

    public void favourites(View v) {
        ConnectivityManager cn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf = cn.getActiveNetworkInfo();
        if (nf != null && nf.isConnected() == true) {

            new FetchVenuesFromServer().execute();
        } else Snackbar();


//        refreshmap();
//        Toast.makeText(this, "Feature under development.", Toast.LENGTH_SHORT).show();
//        Intent pIntent = new Intent(getBaseContext(), SlideUpActivity.class);
//        startActivityForResult(pIntent, 0);
    }


    private void insertToDatabase(String eventNa, final String phoneNo) {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String paramname = params[0];
                String paramphoneNo = params[1];

                // InputStream is = null;

                String eventNa = paramname;
                String phoneNo = paramphoneNo;


                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("eventNa", eventNa));
                nameValuePairs.add(new BasicNameValuePair("phoneNo", phoneNo));

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://www.tukio.co.ke/applicationfiles/appaddevent.php");
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpClient.execute(httpPost);

                    HttpEntity entity = response.getEntity();

                    final String content;
                    try {
                        content = EntityUtils.toString(entity);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MyREsponse = content;
                            }
                        });

                    } catch (ClientProtocolException e) {
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                } catch (ClientProtocolException e) {
                    Toast.makeText(MainActivity.this, "ClientProtocolException", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, "IOException", Toast.LENGTH_SHORT).show();
                }

                return "Sent";


            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                pDialog2.dismiss();
                if (MyREsponse.contains("Success")) {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    alertDialog.setTitle("Success");
                    alertDialog.setCancelable(false);
                    alertDialog.setMessage("Event sent");
                    alertDialog.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    alertDialog.show();
                } else
                    Toast.makeText(MainActivity.this, MyREsponse + ". Error while sending. Try again later", Toast.LENGTH_SHORT).show();

            }

        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(eventNa, phoneNo);
    }

    public void TurnOnLoc(View v) {
        ConnLoc();

    }

    private void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("BNVHelper", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }
    }

    public void kicking(View v) {
        TextView diist = (TextView) findViewById(R.id.venueDistanceInKm);
        double dista = Double.parseDouble(diist.getText().toString());
        if (dista < 1.0) {
            checkCache();
        }
        if (dista >= 1.0) {
            Snackbar.make(this.findViewById(android.R.id.content),
                    "You cannot kick this venue until you're inside it!", Snackbar.LENGTH_LONG).show();
//            Toast.makeText(this, "You cannot kick this venue until you're inside it.", Toast.LENGTH_SHORT).show();
        }
    }

    public void checkCache() {

        ACache mCache = ACache.get(MainActivity.this);
        String kst = mCache.getAsString("kickstatus");
//        Toast.makeText(this, "Cache: " + kst, Toast.LENGTH_SHORT).show();
        try {
            if (kst.equals(null)) {
                CheckNetKick();
            }
            if (kst.contentEquals("placekicked")) {
                Snackbar.make(this.findViewById(android.R.id.content),
                        "You recently kicked this place. Please wait before you can kick again.!", Snackbar.LENGTH_LONG).show();

//                Toast.makeText(this, "You recently kicked this place. Please wait before you can kick again.", Toast.LENGTH_SHORT).show();

            }
        } catch (NullPointerException e) {
//            Toast.makeText(this, "No previous kicks", Toast.LENGTH_SHORT).show();
            CheckNetKick();
        }

    }

    public void CheckNetKick() {
        ConnectivityManager cn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf = cn.getActiveNetworkInfo();
        if (nf != null && nf.isConnected() == true) {

            kickPlace();
        } else Snackbar();

    }


    public void kickPlace() {
        TextView kickoTv = (TextView) findViewById(R.id.kickoValue);
        String kickValue = kickoTv.getText().toString().trim();
        int kicko = Integer.parseInt(kickValue);
        if (kicko < 0) {
            Toast.makeText(this, "You cannot kick this venue.", Toast.LENGTH_SHORT).show();
        } else {
            kickPlace2();
        }

    }

    public void kickPlace2() {
        final Button kBtn = (Button) findViewById(R.id.kickingBtn);
        kBtn.setText("Kicking...".toString().toLowerCase());
        venueIdKicko = (TextView) findViewById(R.id.placeID);
        final String placeid = venueIdKicko.getText().toString();
        SharedPreferences pref1stRun = PreferenceManager.getDefaultSharedPreferences(this);
        final String uid = pref1stRun.getString("userId", "");
//        Toast.makeText(MainActivity.this, "Venue id: "+placeid, Toast.LENGTH_SHORT).show();

        new Thread() {
            //        @Override
            public void run() {

                String path = "http://www.tukio.co.ke/applicationfiles/kickplace.php?venue_id=" + placeid + "&user_id=" + uid;
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
//                                pDialog.dismiss();

//                            Toast.makeText(MainActivity.this, "Place kicked", Toast.LENGTH_SHORT).show();
                            kBtn.setText("Kicked!".toString().toLowerCase());
                            //save in Cache that place has been kicked for 30s
                            ACache mCache = ACache.get(MainActivity.this);
                            mCache.put("kickstatus", "placekicked", 30);
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

    public void setFeed2(View v) {
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", cal.getTimeInMillis());
        intent.putExtra("allDay", true);
        intent.putExtra("rrule", "FREQ=YEARLY");
        intent.putExtra("endTime", cal.getTimeInMillis() + 60 * 60 * 1000);
        intent.putExtra("title", "A Test Event from android app");
        startActivity(intent);
    }

    public void setFeed(View v) {

        ConnectivityManager cn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf = cn.getActiveNetworkInfo();
        if (nf != null && nf.isConnected() == true) {

            new FetchVenuesFromServer().execute();
        } else Snackbar();
    }

    //    public void setFeed3(View v){
    public void setFeed3() {
        //Toast.makeText(this, "Create a news feed.", Toast.LENGTH_SHORT).show();

        // Intialize  readable sequence of char values
        final CharSequence[] dialogList = list.toArray(new CharSequence[list.size()]);
        final AlertDialog.Builder builderDialog = new AlertDialog.Builder(MainActivity.this);
        builderDialog.setTitle("Select Place");
        int count = dialogList.length;
        boolean[] is_checked = new boolean[count];

        // Creating multiple selection by using setMutliChoiceItem method
        builderDialog.setMultiChoiceItems(dialogList, is_checked,
                new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton, boolean isChecked) {
                    }
                });

        builderDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ListView list = ((AlertDialog) dialog).getListView();
                        // make selected item in the comma separated string
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < list.getCount(); i++) {
                            boolean checked = list.isItemChecked(i);

                            if (checked) {
                                if (stringBuilder.length() > 0) stringBuilder.append(",");
                                stringBuilder.append(list.getItemAtPosition(i));


                            }
                        }

                        /*Check string builder is empty or not. If string builder is not empty.
                          It will display on the screen.
                         */
                        if (stringBuilder.toString().trim().equals("")) {

//                            ((TextView) findViewById(R.id.Checktext)).setText("Click here to open Dialog");
                            stringBuilder.setLength(0);

                        } else {

//                            ((TextView) findViewById(R.id.Checktext)).setText(stringBuilder);
                        }
                    }
                });

        builderDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        builderDialog.show()
//                        ((TextView) findViewById(R.id.Checktext)).setText("");
                    }
                });
        AlertDialog alert = builderDialog.create();
        alert.show();

    }
//        Intent pIntent = new Intent(getBaseContext(), ParseJSON2.class);
//        startActivityForResult(pIntent, 0);

    public void refreshmap(View v) {
        PlaceList = new ArrayList<>();

        new Getplaces().execute();
    }

    public void refreshmap2() {
        PlaceList = new ArrayList<>();

        new Getplaces().execute();
    }

    public class Getplaces extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Refreshing for new data", Toast.LENGTH_LONG).show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HTTPHandler sh = new HTTPHandler();
            // Making a request to url and getting response
//            String url = "http://tukio.co.ke/applicationfiles/places.json";
            String url = "http://tukio.co.ke/applicationfiles/getlocations.php";
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            publicjson = jsonStr;
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);


//                    publicjson=jsonStr;  //to help in saving

                    // Getting JSON Array node
                    JSONArray places = jsonObj.getJSONArray("places");

                    // looping through All
//                    for (int i = 0; i < places.length(); i++) {
//                        JSONObject c = places.getJSONObject(i);
//                        String id = c.getString("id");
//                        String name = c.getString("name");
//                        String manager = c.getString("manager");
//                        String lat = c.getString("latitude");
//                        String longg = c.getString("longitude");
//
//                        // tmp hash map for single place
//                        HashMap<String, String> place = new HashMap<>();
//
//                        // adding each child node to HashMap key => value
//                        place.put("id", id);
//                        place.put("name", name);
//                        place.put("manager", manager);
//                        place.put("lat", lat);
//                        place.put("long", longg);
//
//                        // adding place to place list
//                        PlaceList.add(place);
//                        Toast.makeText(ParseJSON.this, "Contact list: "+contactList, Toast.LENGTH_SHORT).show();
//                    }
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
                        Toast.makeText(getApplicationContext(), "Couldn't get json from server. Check Logcat!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


//            Toast.makeText(ParseJSON.this, "data: "+publicjson, Toast.LENGTH_SHORT).show();

            //Functional in writing. Not reading
            /*try {
                FileWriter file = new FileWriter("/data/data/" + getApplicationContext().getPackageName() + "/" + jsonMapsDatafile);
                file.write(publicjson);
                file.flush();
                file.close();
                Toast.makeText(ParseJSON.this, "Saved", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }*/ //end


            FileOutputStream fos;
            try {
                fos = openFileOutput("jsonMapsDatafile.txt", Context.MODE_PRIVATE);
                //default mode is PRIVATE, can be APPEND etc.
                fos.write(publicjson.getBytes());
                fos.close();

//                Toast.makeText(getApplicationContext(), "Data saved", Toast.LENGTH_LONG).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    class FetchVenuesFromServer extends AsyncTask<String, String, String> {
        String jsonValues = null;

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog3 = new ProgressDialog(MainActivity.this);
            pDialog3.setMessage("Loading venues...");
            pDialog3.setIndeterminate(false);
            pDialog3.setCancelable(true);
            pDialog3.show();
        }

        /**
         * getting song json and parsing
         */
        protected String doInBackground(String... args) {

            // List<NameValuePair> params = new ArrayList<NameValuePair>();
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            String URL_ = "http://tukio.co.ke/applicationfiles/appcallplaces.php";
            // getting JSON string from URL
            jsonValues = jsonParser.makeHttpRequest(URL_, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("JSON values: ", jsonValues);

            try {
                JSONArray jsonArr = new JSONArray(jsonValues);
//                Toast.makeText(MainActivity.this, "Count: "+jsonValues.length(), Toast.LENGTH_SHORT).show();
                //can't toast at background
//                JSONObject jObj = jsonArr.getJSONObject(0);
//                if(jObj != null){
//
////                    _id = jObj.getString(TAG_ID);
//                }

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplication(), "Exception Error", Toast.LENGTH_SHORT).show();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting song information
            pDialog3.dismiss();

            runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        JSONArray jsonArray = new JSONArray(jsonValues);
//                        Toast.makeText(MainActivity.this, "No of places: " + jsonArray.length(), Toast.LENGTH_SHORT).show();
                        list.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObj = jsonArray.getJSONObject(i);
                            list.add(jsonObj.getString("manager_club"));
//                            setFeed3();
//                            list.add("Apple 1");
//                            list.add("Apple 2");
//                            list.add("Apple 3");


                        }
                        setFeed3();
                    } catch (JSONException e) {
                        Log.e("PLACES", "Json parsing error: " + e.getMessage());
                    }
//                    Toast.makeText(MainActivity.this, "end", Toast.LENGTH_SHORT).show(); //working well
                }
            });

        }
    }

    //    public void loadQR(View v) {
//        ACache mCache = ACache.get(this);
//        String value = mCache.getAsString("test_key");
//        Toast.makeText(this, "Cached: "+value, Toast.LENGTH_SHORT).show();
//    }
    public void loadQR(View v) {
        LoadImageFromURL loadImage = new LoadImageFromURL();
        loadImage.execute();
        pDialog4 = new ProgressDialog(MainActivity.this);
        pDialog4.setMessage("Loading QR...");
        pDialog4.setIndeterminate(false);
        pDialog4.setCancelable(true);
        pDialog4.show();
        SharedPreferences pref1stRun = PreferenceManager.getDefaultSharedPreferences(this);
        useremail = pref1stRun.getString("useremail", "");
    }

    public class LoadImageFromURL extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                URL url = new URL("http://tukio.co.ke/applicationfiles/qrimages/" + useremail + ".png");
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
        protected void onPostExecute(final Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
//            iv.setImageBitmap(result);
            pDialog4.dismiss();
            AlertDialog.Builder alertadd = new AlertDialog.Builder(MainActivity.this);

//            alertadd.setTitle("Android");
            LayoutInflater factory = LayoutInflater.from(MainActivity.this);
            final View view = factory.inflate(R.layout.custom_dialog_image, null);
            ImageView image = (ImageView) view.findViewById(R.id.QRimageView);
//            image.setImageResource(R.drawable.ic_launcher);
            image.setImageBitmap(result);
//            TextView text= (TextView) view.findViewById(R.id.textView);
//            text.setText("Android");
            alertadd.setView(view);
            alertadd.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dlg, int sumthin) {
                    ACache mCache = ACache.get(MainActivity.this);
                    mCache.put("qrimage", result);
                }
            });

            alertadd.show();

        }

    }

    public void logout(View v) {
        AlertDialog.Builder alertadd = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater factory = LayoutInflater.from(MainActivity.this);
        final View view = factory.inflate(R.layout.custom_dialog_logout, null);
        alertadd.setView(view);
        alertadd.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlg, int sumthin) {

            }
        });
        alertadd.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlg, int sumthin) {

//                SIGN OUT
                Log.d(TAG, "signOut()");
                // Firebase sign out
                mAuth.signOut();
                mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                        .addApi(Auth.GOOGLE_SIGN_IN_API)
                        .build();
                // Google sign out
                mGoogleApiClient.disconnect();
                if (mGoogleApiClient.isConnected()) {
//                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                    mGoogleApiClient.clearDefaultAccountAndReconnect();

                }
//                END LOGOUT

                //check if profile pic exists

                ACache mCache = ACache.get(MainActivity.this);
                Bitmap ppBitmap = mCache.getAsBitmap("profile_picture");
                if (ppBitmap != null) {// profile exists, convert to Base 64 and cache it in Shared pref
                    Bitmap immage = ppBitmap;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] b = baos.toByteArray();
                    String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

                    SharedPreferences BmyPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    SharedPreferences.Editor editor = BmyPrefs.edit();
                    editor.putString("profile_picture", imageEncoded);
                    editor.putString("saved_profile_picture", "yes");
                    editor.apply();
                    Log.d("Image Log:", imageEncoded);
//                    return imageEncoded;
                } else {
                    //prof doesn't exists. Do nothing

                }

//                ACache mCache = ACache.get(MainActivity.this);
                mCache.clear();
                SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = myPrefs.edit();
                editor.putString("firstrun", "");
                editor.putString("username", "");
                editor.putString("useremail", "");
                editor.putString("userId", "");
                editor.putString("myVenues", "");
                editor.putString("myEvents", "");
                editor.apply();
                FileOutputStream fos;
                try {
                    fos = openFileOutput("jsonMapsDatafile.txt", Context.MODE_PRIVATE);
                    //default mode is PRIVATE, can be APPEND etc.
                    fos.write("".getBytes());
                    fos.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Toast.makeText(MainActivity.this, "All data cleared.", Toast.LENGTH_SHORT).show();

                Intent ff = new Intent(getBaseContext(), SplashActivity.class);
                ff.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivityForResult(ff, 0);
                finish();
            }
        });

        alertadd.show();

    }

    public void aboutKicko(View v) {
        PopupWindow popup = new PopupWindow(MainActivity.this);
        View layout = getLayoutInflater().inflate(R.layout.popup_window_content, null);
        popup.setContentView(layout);
        // Set content width and height
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        // Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        // Show anchored to button
//        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.showAsDropDown(v, 1, 0);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
        startActivity(intent);
        finish();
        System.exit(0);
    }

}