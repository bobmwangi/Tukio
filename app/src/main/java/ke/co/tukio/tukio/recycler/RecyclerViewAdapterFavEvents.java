package ke.co.tukio.tukio.recycler;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ke.co.tukio.tukio.MainActivity;
import ke.co.tukio.tukio.R;
import ke.co.tukio.tukio.VenueDetailsActivity;
import ke.co.tukio.tukio.util.CheckConnectivity;


public class RecyclerViewAdapterFavEvents extends RecyclerView.Adapter<RecyclerViewAdapterFavEvents.ViewHolder> {

    Context context;
    List<GetDataAdapter> getDataAdapter;
    ImageLoader imageLoader1;
    private Activity activity;
//String eventDate2, eventTime2,eventAge2;

    public RecyclerViewAdapterFavEvents(List<GetDataAdapter> getDataAdapter, Context context) {

        super();
        this.getDataAdapter = getDataAdapter;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_items, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);

        this.context = context;
        context = parent.getContext();

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder Viewholder, int position) {

        GetDataAdapter getDataAdapter1 = getDataAdapter.get(position);

        imageLoader1 = ServerImageParseAdapter.getInstance(context).getImageLoader();

        imageLoader1.get(getDataAdapter1.getImageServerUrl(),
                ImageLoader.getImageListener(
                        Viewholder.networkImageView,//Server Image
                        R.drawable.ic_action_placeholderphoto,//Before loading server image the default showing image.
//                        R.mipmap.ic_launcher,//Before loading server image the default showing image.
                        android.R.drawable.ic_dialog_alert //Error image if requested image dose not found on server.
                )
        );

        Viewholder.networkImageView.setImageUrl(getDataAdapter1.getImageServerUrl(), imageLoader1);

        Viewholder.ename.setText(getDataAdapter1.getevName());
        Viewholder.eid.setText(getDataAdapter1.getevId());
        Viewholder.evRemId.setText(getDataAdapter1.getevRemId());
        Viewholder.evenue.setText(getDataAdapter1.getevVenue());
        Viewholder.evenueid.setText(getDataAdapter1.getevVenueId());
        Viewholder.eviews.setText(getDataAdapter1.getevViews());
        Viewholder.edate.setText(getDataAdapter1.getevDate());
        Viewholder.edate2.setText(getDataAdapter1.getevDate2());
        Viewholder.edesc.setText(getDataAdapter1.getevDesc());
        Viewholder.eurl.setText(getDataAdapter1.getImageServerUrl());
        Viewholder.evage.setText(getDataAdapter1.getevAge());
        Viewholder.evtime.setText(getDataAdapter1.getEvTime());

//        eventDate2 = getDataAdapter1.getevDate2();
//        eventTime2 = getDataAdapter1.getEvTime();
//        eventAge2 = getDataAdapter1.getevAge();

    }

    @Override
    public int getItemCount() {

        return getDataAdapter.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView ename, evenue, edate, edate2, eviews, eid, edesc, eurl, evenueid, evage, evtime, evRemId;
        public NetworkImageView networkImageView;


        public ViewHolder(final View itemView) {

            super(itemView);

            eid = (TextView) itemView.findViewById(R.id.tvID);
            edate = (TextView) itemView.findViewById(R.id.tvDate);
            edate2 = (TextView) itemView.findViewById(R.id.tvDate2);
            ename = (TextView) itemView.findViewById(R.id.tvEventName);
            evRemId = (TextView) itemView.findViewById(R.id.tvReminderID);
            evenue = (TextView) itemView.findViewById(R.id.tvVenue);
            evenueid = (TextView) itemView.findViewById(R.id.tvVenueid);
            eviews = (TextView) itemView.findViewById(R.id.tvViews);
            edesc = (TextView) itemView.findViewById(R.id.tvDesc);
            eurl = (TextView) itemView.findViewById(R.id.tvIMGurl);

            evtime = (TextView) itemView.findViewById(R.id.tvTime);
            evage = (TextView) itemView.findViewById(R.id.tvAge);


            networkImageView = (NetworkImageView) itemView.findViewById(R.id.VollyNetworkImageView1);


//            itemView.setOnClickListener(context); {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


//                        IncreViews(String.valueOf(eid.getText()));  //increase views if there is internet


                    Context context = view.getContext();
                    final Context mcontext = view.getContext();
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    View vieww = inflater.inflate(R.layout.bottom_sheet_fav_events, null);
                    TextView ttl = (TextView) vieww.findViewById(R.id.eventTitleBS);
                    TextView ven = (TextView) vieww.findViewById(R.id.viewVenBS);
                    TextView dat = (TextView) vieww.findViewById(R.id.dateBS);
                    TextView tim = (TextView) vieww.findViewById(R.id.timeBS);
                    TextView seenby = (TextView) vieww.findViewById(R.id.seenByBS);
                    TextView age = (TextView) vieww.findViewById(R.id.ageBS);
                    TextView about = (TextView) vieww.findViewById(R.id.aboutBS);
                   // TextView reminder = (TextView) vieww.findViewById(R.id.setRemBS);
                    TextView deleteEvent = (TextView) vieww.findViewById(R.id.deleteEvent);
                    ttl.setText(String.valueOf(ename.getText()));
                    ven.setText("View '" + String.valueOf(evenue.getText())+"'");
                    dat.setText(String.valueOf(edate.getText()));
                    tim.setText(String.valueOf(evtime.getText()));
                    age.setText("Age: " + String.valueOf(evage.getText()));
                    seenby.setText("Seen by: " + String.valueOf(eviews.getText()));
                    about.setText(String.valueOf(edesc.getText()));
                    BottomSheetDialog dialog = new BottomSheetDialog(context);
                    dialog.setContentView(vieww);
                    dialog.setTitle(String.valueOf(ename.getText()));
                    dialog.setCancelable(true);
                    deleteEvent.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            String reventIdOnRem = String.valueOf(evRemId.getText());
                            deleteEventOnServer(reventIdOnRem);
                            Toast.makeText(mcontext, "eid: "+reventIdOnRem, Toast.LENGTH_SHORT).show();
                            }
                    });
                    ven.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            //OnCLick Stuff
                            CheckConnectivity checkNetwork = new CheckConnectivity();
                            if (checkNetwork.isConnected(mcontext)) {
//                                Toast.makeText(mcontext, "Venue: " + String.valueOf(evenue.getText()), Toast.LENGTH_SHORT).show();
                                Intent mii = new Intent(mcontext, VenueDetailsActivity.class);
                                mii.putExtra("vname", String.valueOf(evenue.getText()));
                                mcontext.startActivity(mii);
                            } else {
                                Toast.makeText(mcontext, "No internet connection.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                    dialog.show();
                }
            });
        }
    }

    public void deleteEventOnServer(final String eid) {
        SharedPreferences pref1stRun = PreferenceManager.getDefaultSharedPreferences(context);
        final String uid = pref1stRun.getString("userId", "");

        new Thread() {
            //        @Override
            public void run() {

                String path = "http://www.tukio.co.ke/applicationfiles/deletefavouritevent.php?eventid=" + eid+"&userid="+uid;
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

                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

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
            GetNewFavEventCount();
            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(context, "Deletion failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void GetNewFavEventCount() {
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
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

                    ((Activity)context).runOnUiThread(new Runnable() {
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
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("count_favourite_events", count);
        editor.apply();
        Intent mii = new Intent(context, MainActivity.class);
        context.startActivity(mii);
    }





}
