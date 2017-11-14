package ke.co.tukio.tukio.recycler;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import ke.co.tukio.tukio.R;
import ke.co.tukio.tukio.ReminderEventsActivity;
import ke.co.tukio.tukio.VenueDetailsActivity;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    Context context;
    List<GetDataAdapter> getDataAdapter;
    ImageLoader imageLoader1;
    private Activity activity;
//String eventDate2, eventTime2,eventAge2;

    public RecyclerViewAdapter(List<GetDataAdapter> getDataAdapter, Context context) {

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

        public TextView ename, evenue, edate, edate2, eviews, eid, edesc, eurl, evenueid, evage, evtime;
        public NetworkImageView networkImageView;


        public ViewHolder(final View itemView) {

            super(itemView);

            eid = (TextView) itemView.findViewById(R.id.tvID);
            edate = (TextView) itemView.findViewById(R.id.tvDate);
            edate2 = (TextView) itemView.findViewById(R.id.tvDate2);
            ename = (TextView) itemView.findViewById(R.id.tvEventName);
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

//                    int itemPosition = getLayoutPosition();
                    //Toast.makeText(context, itemPosition + ":" +String.valueOf(eid.getText()) + String.valueOf(ename.getText()), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(context, itemPosition + ": " +String.valueOf(eid.getText()),  Toast.LENGTH_SHORT).show();
                    ConnectivityManager cn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo nf = cn.getActiveNetworkInfo();
                    if (nf != null && nf.isConnected() == true) {

//                        Toast.makeText(context, "Name: "+String.valueOf(ename.getText()), Toast.LENGTH_SHORT).show();
//                    Intent ii = new Intent(context, EventsDetailsActivity.class);
//                    ii.putExtra("eid", String.valueOf(eid.getText()));
//                    ii.putExtra("ename", String.valueOf(ename.getText()));
//                    ii.putExtra("evenue", String.valueOf(evenue.getText()));
//                    ii.putExtra("evenueid", String.valueOf(evenueid.getText()));
//                    ii.putExtra("edate", String.valueOf(edate.getText()));
//                    ii.putExtra("edate2", String.valueOf(edate2.getText()));
//                    ii.putExtra("eviews", String.valueOf(eviews.getText()));
//                    ii.putExtra("edesc", String.valueOf(edesc.getText()));
//                    ii.putExtra("eurl", String.valueOf(eurl.getText()));
//                    context.startActivity(ii);
//mthd();
//                            IncreViews(String.valueOf(eviews.getText()));




                        Context context = view.getContext();
                        final Context mcontext = view.getContext();
                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                        View vieww = inflater.inflate(R.layout.bottom_sheet_events, null);
                        TextView ttl = (TextView) vieww.findViewById(R.id.eventTitleBS);
                        TextView ven = (TextView) vieww.findViewById(R.id.viewVenBS);
                        TextView dat = (TextView) vieww.findViewById(R.id.dateBS);
                        TextView tim = (TextView) vieww.findViewById(R.id.timeBS);
                        TextView seenby = (TextView) vieww.findViewById(R.id.seenByBS);
                        TextView age = (TextView) vieww.findViewById(R.id.ageBS);
                        TextView about = (TextView) vieww.findViewById(R.id.aboutBS);
                        TextView reminder = (TextView) vieww.findViewById(R.id.setRemBS);
                        ttl.setText(String.valueOf(ename.getText()));
                        ven.setText(String.valueOf(evenue.getText()));
                        dat.setText(String.valueOf(edate.getText()));
                        tim.setText(String.valueOf(evtime.getText()));
                        age.setText("Age: "+String.valueOf(evage.getText()));
//                        tim.setText(eventTime2);
                        seenby.setText("Seen by: "+String.valueOf(eviews.getText()));
//                        age.setText("Age: "+eventAge2);
                        about.setText(String.valueOf(edesc.getText()));
                        BottomSheetDialog dialog = new BottomSheetDialog(context);
                        dialog.setContentView(vieww);
                        dialog.setTitle(String.valueOf(ename.getText()));
                        dialog.setCancelable(true);
                        reminder.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                //OnCLick Stuff
//                                Toast.makeText(mcontext, "reminder", Toast.LENGTH_SHORT).show();
                                addToCalender(String.valueOf(edate2.getText()), String.valueOf(eid.getText()), String.valueOf(ename.getText()));
                            }
                        });
                        ven.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                //OnCLick Stuff
                                Intent mii = new Intent(mcontext, VenueDetailsActivity.class);
                                Toast.makeText(mcontext, "Venue: "+String.valueOf(evenue.getText()), Toast.LENGTH_SHORT).show();
                                mii.putExtra("vname", String.valueOf(evenue.getText()));
                                mcontext.startActivity(mii);
                            }
                        });
                        dialog.show();


//                        final Dialog mBottomSheetDialog = new Dialog (context, R.style.AppTheme);
//                        mBottomSheetDialog.setTitle("Title");
//                        mBottomSheetDialog.setContentView(vieww);
//                        mBottomSheetDialog.setCancelable(true);
//                        mBottomSheetDialog.setCancelable (true);
//                        mBottomSheetDialog.getWindow ().setLayout (LinearLayout.LayoutParams.MATCH_PARENT,
//                                LinearLayout.LayoutParams.WRAP_CONTENT);
//                        mBottomSheetDialog.getWindow ().setGravity (Gravity.BOTTOM);
//                        mBottomSheetDialog.show ();
                       /* AlertDialog.Builder alertDialog = new AlertDialog.Builder(view.getContext());
                        alertDialog.setTitle("Account is ready.");
                        alertDialog.setMessage("Your user ID is: ");
                        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }

                        });
//
            alertDialog.show();*/
//                        Activity activity = (ReminderEventsActivity.getApplicationContext()).getCurrentActivity();

                    /*  BottomSheet bs = new BottomSheet.Builder(RecyclerViewAdapter.ViewHolder.this)
//                        new BottomSheet.Builder(Activity activity(){
                                .title("title")
                                .sheet(R.menu.list)
                                .listener(new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case R.id.help:
//                                        q.toast("Help me!");
                                                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
                                                break;
                                        }
                                    }
                                }).show();*/
                    } else {
                        Intent iii = new Intent(context, ReminderEventsActivity.class);
                        iii.putExtra("eid", String.valueOf(eid.getText()));
                        iii.putExtra("ename", String.valueOf(ename.getText()));
                        iii.putExtra("method", "ShowBottomSheet");
                        context.startActivity(iii);
//                        Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();


                    }

                }
            });
        }
    }

    public void mthd(View v) {


        Context context = v.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.customdialogabout, null);

        final Dialog mBottomSheetDialog = new Dialog(context, R.style.AppTheme);
        mBottomSheetDialog.setTitle("Title");
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();


    }

    public void IncreViews(final String evId){
//        Context context = vv.getContext();
        new Thread() {
            //        @Override
            public void run(){


                String path ="http://www.tukio.co.ke/applicationfiles/increaseeventviews.php?id="+evId;
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

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                                PopAccountDetails(returnedID);
                            try {
                                bo.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public void addToCalender(String _edate2, String _eid, String evname) {
        String new_event_id = "0";
        //Read shared Pref
        SharedPreferences pref1stRun = PreferenceManager.getDefaultSharedPreferences(context);
        String stored_events_ids = pref1stRun.getString("myEvents", "");

        if (stored_events_ids.contains(_eid)) {
            Toast.makeText(context, "Event already exists in favourites", Toast.LENGTH_SHORT).show();
            return;
        }
        if (stored_events_ids.isEmpty()) {
            new_event_id = _eid;

        }
        if (!stored_events_ids.isEmpty()){
            new_event_id = stored_events_ids + "," + _eid;

        }
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("myEvents", new_event_id);
        editor.apply();
//        Snackbar();
//        PostFavouriteToServer();
//        Toast.makeText(context, "Added to Favouritedold: "+stored_events_ids+ "new: "+new_event_id, Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "Added to Favourites", Toast.LENGTH_SHORT).show();






        addToSystemCalendar(_edate2, evname); //add to system calendar

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
        notificationIntent.addCategory("android.intent.category.DEFAULT");

        PendingIntent broadcast = PendingIntent.getBroadcast(context, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //time difference
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");    //Specify the data format
        Calendar calendar = Calendar.getInstance();
        String fromDate =  df.format(calendar.getTime());
        String toDate = _edate2;
        long diff = 0;

        try {
            //Convert to Date
            Date startDate = df.parse(fromDate);
            Calendar c1 = Calendar.getInstance();
            //Change to Calendar Date
            c1.setTime(startDate);

            //Convert to Date
            Date endDate = df.parse(toDate);
            Calendar c2 = Calendar.getInstance();
            //Change to Calendar Date
            c2.setTime(endDate);

            //get Time in milli seconds
            long ms1 = c1.getTimeInMillis();
            long ms2 = c2.getTimeInMillis();
            //get difference in milli seconds
            diff = ms2 - ms1;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Find number of days by dividing the mili seconds
//        int diffInDays = (int) (diff / (24 * 60 * 60 * 1000));
        int diffInHours = (int) (diff /(1000 * 60 * 60));
//        Toast.makeText(this, "Diff "+ diffInHours+" hours", Toast.LENGTH_SHORT).show();





        //set alarm
        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.MINUTE, 2);
        cal.add(Calendar.HOUR, diffInHours);
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
//        Toast.makeText(context, "Added to reminder", Toast.LENGTH_SHORT).show();

    }
    public void addToSystemCalendar(String _edate2, String evname){
        long startTime=0,endTime;

//        String startDate = "2017-11-01";
        String startDate = _edate2;
//        Toast.makeText(this, "Date "+_edate2, Toast.LENGTH_SHORT).show();
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
            startTime=date.getTime();
        }
        catch(Exception e){ }

        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
//        intent.putExtra("beginTime", cal.getTimeInMillis());
        intent.putExtra("beginTime", startTime);
        intent.putExtra("allDay", true);
//        intent.putExtra("rrule", "FREQ=YEARLY");
        intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
        intent.putExtra("title", "TUKIO EVENT: "+evname);
        context.startActivity(intent);
    }

}
