package ke.co.tukio.tukio.recycler;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ke.co.tukio.tukio.R;

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
                    Toast.makeText(context, "ID: " + ":" + String.valueOf(vid.getText()) + " Name: " + String.valueOf(vname.getText()), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(context, itemPosition + ": " +String.valueOf(vid.getText()),  Toast.LENGTH_SHORT).show();
                    //check internet connec
                    ConnectivityManager cn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo nf = cn.getActiveNetworkInfo();
                    if (nf != null && nf.isConnected() == true) {
                        AlertDiag(venueid);
                    } else {
                        Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

    public void AlertDiag(String venueid) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Remove from favourites");
        alertDialog.setMessage("Events from " + venueid + " will no longer be shown on your favourites newsfeed.");
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }
}
