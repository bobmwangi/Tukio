package ke.co.tukio.tukio.recycler;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import ke.co.tukio.tukio.R;
import ke.co.tukio.tukio.UtilsColor;
import ke.co.tukio.tukio.VenueDetailsActivity;

public class RecyclerViewAdapterVen extends RecyclerView.Adapter<RecyclerViewAdapterVen.ViewHolder> {

    Context context;
    List<GetDataAdapterVen> getDataAdapterVen;
    ImageLoader imageLoader1;



    public RecyclerViewAdapterVen(List<GetDataAdapterVen> getDataAdapterVen, Context context){

        super();
        this.getDataAdapterVen = getDataAdapterVen;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_items_venues, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);



        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder Viewholder, int position) {

        GetDataAdapterVen getDataAdapter3 =  getDataAdapterVen.get(position);

//        imageLoader1 = ServerImageParseAdapter.getInstance(context).getImageLoader();
//
//        imageLoader1.get(getDataAdapter3.getImageServerUrl(),
//                ImageLoader.getImageListener(
//                        Viewholder.networkImageView,//Server Image
//                        R.mipmap.ic_launcher,//Before loading server image the default showing image.
//                        android.R.drawable.ic_dialog_alert //Error image if requested image dose not found on server.
//                )
//        );
//
//        Viewholder.networkImageView.setImageUrl(getDataAdapter3.getImageServerUrl(), imageLoader1);
//        Viewholder.venImageView.setImageResource(R.drawable.venue_fire_white);//.setImageUrl(getDataAdapter3.getImageServerUrl(), imageLoader1);
//CHANGE COLOURS
        Bitmap mFinalBitmap;
        int mColorCode=0;

        int kickV = Integer.parseInt(getDataAdapter3.getVenId());

        if(kickV ==0){
            mColorCode = Color.parseColor("#32cd32");
        }
        if(kickV >0 && kickV<10){
            mColorCode = Color.parseColor("#ffc125");
        }
        if(kickV >10 && kickV<25){
            mColorCode = Color.parseColor("#ff7f00");
        }
        if(kickV >25 && kickV<50){
            mColorCode = Color.parseColor("#32cd32");
        }
        if(kickV >=50){
            mColorCode = Color.parseColor("#ee0000");
        }
        Drawable sourceDrawable = context.getResources().getDrawable(R.drawable.venue_fire_white);
        Bitmap sourceBitmap = UtilsColor.convertDrawableToBitmap(sourceDrawable);
        mFinalBitmap = UtilsColor.changeImageColor(sourceBitmap, mColorCode);
//        venImageView.setImageBitmap(mFinalBitmap);

        Viewholder.venImageView.setImageBitmap(mFinalBitmap);
//        Viewholder.venImageView.setImageResource(R.drawable.venue_fire_white);

        Viewholder.vname.setText(getDataAdapter3.getVenName());
        Viewholder.vid.setText(getDataAdapter3.getVenId());
        Viewholder.vloc.setText(getDataAdapter3.getVenLoc());

    }

    @Override
    public int getItemCount() {

        return getDataAdapterVen.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView vloc, vid, vname;
        ImageView venImageView;
//        public NetworkImageView networkImageView ;

        public ViewHolder(View itemView) {

            super(itemView);

            vid = (TextView) itemView.findViewById(R.id.tvVID) ;
            vloc = (TextView) itemView.findViewById(R.id.tvVLoc) ;
            vname = (TextView) itemView.findViewById(R.id.tvVName) ;


            venImageView = (ImageView) itemView.findViewById(R.id.imgVen);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


//                    int itemPosition = getLayoutPosition();
                    //Toast.makeText(context, itemPosition + ":" +String.valueOf(eid.getText()) + String.valueOf(ename.getText()), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(context, itemPosition + ": " +String.valueOf(vid.getText()),  Toast.LENGTH_SHORT).show();
                    //check internet connec
                    ConnectivityManager cn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo nf = cn.getActiveNetworkInfo();
                    if (nf != null && nf.isConnected() == true) {

                    Intent i = new Intent(context, VenueDetailsActivity.class);
//                    i.putExtra("vid", String.valueOf(vid.getText()));
                    i.putExtra("vname", String.valueOf(vname.getText()));
                    context.startActivity(i);
                    }
                    else {
                        Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
                    }

                     }
            });
        }
    }


}
