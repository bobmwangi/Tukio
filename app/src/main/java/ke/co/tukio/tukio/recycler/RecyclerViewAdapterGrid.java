package ke.co.tukio.tukio.recycler;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import ke.co.tukio.tukio.R;

public class RecyclerViewAdapterGrid extends RecyclerView.Adapter<RecyclerViewAdapterGrid.ViewHolder> {

    Context context;
    ArrayList<GetDataAdapter> getDataAdapter;
    ImageLoader imageLoader1;


    public RecyclerViewAdapterGrid(ArrayList<GetDataAdapter> getDataAdapter, Context context){

        super();
        this.getDataAdapter = getDataAdapter;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_items, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);



        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder Viewholder, int position) {

        GetDataAdapter getDataAdapter1 =  getDataAdapter.get(position);

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

    }

    @Override
    public int getItemCount() {

        return getDataAdapter.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView ename, evenue, edate, edate2, eviews, eid, edesc, eurl, evenueid;
        public NetworkImageView networkImageView ;

        public ViewHolder(View itemView) {

            super(itemView);

            eid = (TextView) itemView.findViewById(R.id.tvID) ;
            edate = (TextView) itemView.findViewById(R.id.tvDate) ;
            edate2 = (TextView) itemView.findViewById(R.id.tvDate2) ;
            ename = (TextView) itemView.findViewById(R.id.tvEventName) ;
            evenue = (TextView) itemView.findViewById(R.id.tvVenue) ;
            evenueid = (TextView) itemView.findViewById(R.id.tvVenueid) ;
            eviews = (TextView) itemView.findViewById(R.id.tvViews) ;
            edesc = (TextView) itemView.findViewById(R.id.tvDesc) ;
            eurl = (TextView) itemView.findViewById(R.id.tvIMGurl) ;


            networkImageView = (NetworkImageView) itemView.findViewById(R.id.VollyNetworkImageView1) ;


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
                  /*  Intent ii = new Intent(context, EventsDetailsActivity.class);
                    ii.putExtra("eid", String.valueOf(eid.getText()));
                    ii.putExtra("ename", String.valueOf(ename.getText()));
                    ii.putExtra("evenue", String.valueOf(evenue.getText()));
                    ii.putExtra("evenueid", String.valueOf(evenueid.getText()));
                    ii.putExtra("edate", String.valueOf(edate.getText()));
                    ii.putExtra("edate2", String.valueOf(edate2.getText()));
                    ii.putExtra("eviews", String.valueOf(eviews.getText()));
                    ii.putExtra("edesc", String.valueOf(edesc.getText()));
                    ii.putExtra("eurl", String.valueOf(eurl.getText()));
                    context.startActivity(ii);*/
                    }
                    else {
                        Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
                    }

                     }
            });
        }
    }


}
