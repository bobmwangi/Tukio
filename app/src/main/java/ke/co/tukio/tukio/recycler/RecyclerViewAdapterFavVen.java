package ke.co.tukio.tukio.recycler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import ke.co.tukio.tukio.MainActivity;
import ke.co.tukio.tukio.R;
import ke.co.tukio.tukio.SplashActivity;
import ke.co.tukio.tukio.VenueDetailsActivity;

public class RecyclerViewAdapterFavVen extends RecyclerView.Adapter<RecyclerViewAdapterFavVen.ViewHolder> {

    Context context;
    List<GetDataAdapterVen> getDataAdapterVen;
//    ImageLoader imageLoader1;


    public RecyclerViewAdapterFavVen(List<GetDataAdapterVen> getDataAdapterVen, Context context) {

        super();
        this.getDataAdapterVen = getDataAdapterVen;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_items_fav_venues, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder Viewholder, int position) {

        GetDataAdapterVen getDataAdapter3 = getDataAdapterVen.get(position);


        Viewholder.vname.setText(getDataAdapter3.getVenName());
        Viewholder.vid.setText(getDataAdapter3.getVenId());
        Viewholder.vloc.setText(getDataAdapter3.getVenLoc());

    }

    @Override
    public int getItemCount() {

        return getDataAdapterVen.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView vloc, vid, vname;
//        ImageView venImageView;
//        public NetworkImageView networkImageView ;

        public ViewHolder(View itemView) {

            super(itemView);

            vid = (TextView) itemView.findViewById(R.id.tvVID);
            vloc = (TextView) itemView.findViewById(R.id.tvVLoc);
            vname = (TextView) itemView.findViewById(R.id.tvVName);


//            venImageView = (ImageView) itemView.findViewById(R.id.imgVen);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


//                    int itemPosition = getLayoutPosition();
                    String venueid = vid.getText().toString().trim();
                    String venuename = vname.getText().toString().trim();
//                    Toast.makeText(context, "ID: " + ":" + String.valueOf(vid.getText()) + " Name: " + String.valueOf(vname.getText()), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(context, itemPosition + ": " +String.valueOf(vid.getText()),  Toast.LENGTH_SHORT).show();
                    //check internet connec
                    ConnectivityManager cn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo nf = cn.getActiveNetworkInfo();
                    if (nf != null && nf.isConnected() == true) {
                        AlertDiag(venueid, venuename);
                    } else {
                        Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

    public void AlertDiag(final String venueid, String venuename) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Remove");
        alertDialog.setMessage("Events from " + venuename + " will no longer be shown on your favourites newsfeed.");
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deleteFavVenue(venueid);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    public void deleteFavVenue(final String vid) {
        SharedPreferences pref1stRun = PreferenceManager.getDefaultSharedPreferences(context);
        final String uid = pref1stRun.getString("userId", "");

        new Thread() {
            //        @Override
            public void run() {

                String path = "http://www.tukio.co.ke/applicationfiles/deletefavouritevenue.php?venueid=" + vid + "&userid=" + uid;
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

                    ((Activity) context).runOnUiThread(new Runnable() {
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

    public void ToastResults(String resp) {
        if (resp.contentEquals("deleted")) {
            GetNewFavVenueCount();
            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Deletion failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void GetNewFavVenueCount() {
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
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

                    ((Activity) context).runOnUiThread(new Runnable() {
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

    public void ShowCount(String count) {
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("count_favourite_venues", count);
        editor.apply();
        Intent mii = new Intent(context, MainActivity.class);
        context.startActivity(mii);
    }
}
