package ke.co.tukio.tukio;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yashoid.instacropper.InstaCropperActivity;
import com.yashoid.instacropper.InstaCropperView;

import org.afinal.simplecache.ACache;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import ke.co.tukio.tukio.util.CheckConnectivity;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private InstaCropperView mInstaCropper;
    String CurrDateTime = "blank";

    String uemail = null, _imgurl;
    TextView remindersTV, fistLetter, favEventsTv;
    ImageView qrimage, profPic;
    private static final String IMAGE_DIRECTORY = "/Tukio";
    private int GALLERY = 1;
    View v;

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_profile, container, false);

        SharedPreferences myprefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        String nm = myprefs.getString("username", "");
        String nm = myprefs.getString("username", "");
        TextView uname = (TextView) v.findViewById(R.id.username);  //UPDATE
        qrimage = (ImageView) v.findViewById(R.id.qrImg);
        profPic = (ImageView) v.findViewById(R.id.profImg);
        fistLetter = (TextView) v.findViewById(R.id.firstLetter);  //UPDATE
        remindersTV = (TextView) v.findViewById(R.id.reminderTv);  //UPDATE
        favEventsTv = (TextView) v.findViewById(R.id.favouriteVenuesTv);  //UPDATE

//        mInstaCropper = (InstaCropperView) v.findViewById(R.id.instacropper);


        fistLetter.setOnClickListener(this);
        favEventsTv.setOnClickListener(this);
        remindersTV.setOnClickListener(this);
        profPic.setOnClickListener(this);
//        String fl = nm.substring(0, 1);
        String fl = nm.substring(0, 1);
//        String fl = nm.substring(0, 1);

        uname.setText(nm);
        fistLetter.setText(fl.toUpperCase());

        //check count from sh. pref
        SharedPreferences pref1stRun = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String count = pref1stRun.getString("count_favourite_events", "");
        String vcount = pref1stRun.getString("count_favourite_venues", "");
        remindersTV.setText("Reminders (" + count + ")");
        favEventsTv.setText("Favourite Venues (" + vcount + ")");
//        if (stored_events_ids.isEmpty()) {
//            remindersTV.setText("Reminders (" + 0 + ")");
//        } else if (!stored_events_ids.isEmpty()) {
//            {
//                int countItems = 0, myCount = 0;
//                countItems = stored_events_ids.length() - stored_events_ids.replace(",", "").length();
//
//                myCount = countItems + 1;
//                remindersTV.setText("Reminders (" + myCount + ")");
//            }
//        }

        CheckImageCache();
        CheckProfPicImageCache();

        return v;

    }

    public void setText() {
        TextView uname = (TextView) getView().findViewById(R.id.username);  //UPDATE

//        TextView uname = (TextView) findViewById(R.id.username);
        String nm;
//        SharedPreferences myprefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
//        nm = myprefs.getString("username", "");
        uname.setText("kj");

    }

    public void CheckImageCache() {
        ACache mCache = ACache.get(getActivity());
        Bitmap qr = mCache.getAsBitmap("qrimage");
        if (qr != null) {
//            Toast.makeText(getActivity(), "QR image exists", Toast.LENGTH_SHORT).show();
            qrimage.setImageBitmap(qr);
        } else {
//            Toast.makeText(getActivity(), "No QR", Toast.LENGTH_SHORT).show();
            //check connectivity
            ConnectivityManager cn = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nf = cn.getActiveNetworkInfo();
            if (nf != null && nf.isConnected() == true) {
                SharedPreferences myprefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                uemail = myprefs.getString("useremail", "");
                _imgurl = "http://tukio.co.ke/applicationfiles/qrimages/" + uemail + ".png";

                FetchQrImage loadImage = new FetchQrImage();
                loadImage.execute();

            } else {
                Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public class FetchQrImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                URL url = new URL(_imgurl);
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
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            //set image
            qrimage.setImageBitmap(result);
            //cache image
            ACache mCache = ACache.get(getActivity());
            mCache.put("qrimage", result);
        }
    }

    @Override
    public void onClick(View v) {
        // implements your things
        int i = v.getId();
        if (i == R.id.firstLetter) {
            callPPAct();
        }
        if (i == R.id.favouriteVenuesTv) {
            CheckNetFavVenues();
        }
        if (i == R.id.reminderTv) {
            CheckNetRem();
        }
        if (i == R.id.profImg) {
            ViewPhoto();
        }
    }

    public void ViewPhoto() {
        final ACache mCache = ACache.get(getActivity());
        Bitmap dp = mCache.getAsBitmap("profile_picture");
       /* if (dp != null) {
            profPic.setImageBitmap(dp);
              } else {
            Toast.makeText(getActivity(), "Pic not there", Toast.LENGTH_SHORT).show();
        }*/
       final AlertDialog.Builder alertadd = new AlertDialog.Builder(getActivity());
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View view = factory.inflate(R.layout.custom_dialog_image_dp, null);
//        ImageView delete = (ImageView) view.findViewById(R.id.DPdelete);
//        ImageView edit = (ImageView) view.findViewById(R.id.DPedit);

        /*
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCache.put("profile_picture", "");
                Toast.makeText(getActivity(), "Picture deleted", Toast.LENGTH_SHORT).show();
                profPic.setVisibility(View.GONE);
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });*/
        ImageView image = (ImageView) view.findViewById(R.id.DPimageView);
        if (dp != null) {
            image.setImageBitmap(dp);
        } else {
            image.setImageResource(R.drawable.ic_action_profile);
        }
//        image.setImageBitmap(result);
        alertadd.setView(view);
        alertadd.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlg, int sumthin) {
                mCache.put("profile_picture", "");
                Toast.makeText(getActivity(), "Picture deleted", Toast.LENGTH_SHORT).show();
                SwitchFrag();
                profPic.setVisibility(View.GONE);
            }
        });
        alertadd.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlg, int sumthin) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });

        alertadd.show();
    }

    public void selectPic() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    /* @Override
     public void onActivityResult(int requestCode, int resultCode, Intent data) {

         super.onActivityResult(requestCode, resultCode, data);

         if (requestCode == GALLERY) {
             if (data != null) {
                 Uri contentURI = data.getData();
                 try {
 //                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                     Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), contentURI);
 //                    String path = saveImage(bitmap);
                     getResizedBitmap(bitmap, 120);
 //                    Toast.makeText(getActivity(), "Image Saved!" +bitmap.getHeight()+" "+bitmap.getWidth(), Toast.LENGTH_SHORT).show();
 //                    profPic.setImageBitmap(bitmap);

                 } catch (IOException e) {
                     e.printStackTrace();
                     Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                 }
             }

         }
     }*/
    //to reduce size
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

//        profPic.setImageBitmap(image);
        Toast.makeText(getActivity(), "New size " + height + " " + width, Toast.LENGTH_SHORT).show();
        return Bitmap.createScaledBitmap(image, width, height, true);

    }

    public void CheckNetFavVenues() {
        SharedPreferences pref1stRun = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String vcount = pref1stRun.getString("count_favourite_venues", "");
        if (vcount.contentEquals("0")) {
            Snackbar.make(getActivity().findViewById(android.R.id.content),
                    "You have no favourite venues!", Snackbar.LENGTH_LONG).show();
        } else {
            CheckNetFavVenues2();
        }
    }

    public void CheckNetFavVenues2() {
        CheckConnectivity checkNetwork = new CheckConnectivity();
        if (checkNetwork.isConnected(getContext())) {
            Intent pIntent = new Intent(getContext(), FavouriteVenuesActivity.class);
            startActivityForResult(pIntent, 0);
        } else {
            Snackbar.make(getActivity().findViewById(android.R.id.content),
                    "No internet connection!", Snackbar.LENGTH_LONG).show();
        }

    }

    public void CheckNetRem() {
        SharedPreferences pref1stRun = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String vcount = pref1stRun.getString("count_favourite_events", "");
        if (vcount.contentEquals("0")) {
            Snackbar.make(getActivity().findViewById(android.R.id.content),
                    "You have no events on reminder!", Snackbar.LENGTH_LONG).show();
        } else {
            CheckNetRem2();
        }
    }

    public void CheckNetRem2() {
        CheckConnectivity checkNetwork = new CheckConnectivity();
        if (checkNetwork.isConnected(getContext())) {
            Intent pIntent = new Intent(getContext(), ReminderEventsActivity.class);
            startActivityForResult(pIntent, 0);
        } else {
            Snackbar.make(getActivity().findViewById(android.R.id.content),
                    "No internet connection!", Snackbar.LENGTH_LONG).show();
        }

    }

    public void callPPAct() {
//        Intent pt = new Intent(getContext(), ProfilePictureActivity.class);
//        startActivityForResult(pt, 0);
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, 1);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Intent intent = InstaCropperActivity.getIntent(getActivity(), data.getData(), Uri.fromFile(new File(getActivity().getExternalCacheDir(), CurrDateTime + ".jpg")), 720, 50);
//                    Intent intent = InstaCropperActivity.getIntent(this, Uri.fromFile(getFile()), Uri.fromFile(getFile()), 720, 50);
                    startActivityForResult(intent, 2);
                }
                return;
            case 2:
                if (resultCode == RESULT_OK) {
//                    mInstaCropper.setImageUri(data.getData());

                    Glide
                            .with(getActivity())
                            .load(data.getData())
                            .centerCrop()
                            .override(200, 200)
//                            .placeholder(R.drawable.loading_spinner)
                            .into(profPic);

                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
//                        img2.setImageBitmap(selectedImage);
                        ACache mCache = ACache.get(getActivity());
                        mCache.put("profile_picture", selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }

                    Toast.makeText(getActivity(), "Pic set", Toast.LENGTH_SHORT).show();
                    fistLetter.setVisibility(View.GONE);
                    SwitchFrag();
//                    Intent intent = new Intent(getActivity(), ProfileFragment.class);
//                    startActivity(intent);

                }
                return;
        }
    }


    public void SwitchFrag() {
        // Create new fragment and transaction
        Fragment newFragment = new ProfileFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment, and add the transaction to the back stack
        transaction.replace(R.id.frame_layout, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }
    public void CheckProfPicImageCache() {
        ACache mCache = ACache.get(getActivity());
        Bitmap ppBitmap = mCache.getAsBitmap("profile_picture");
        if (ppBitmap != null) {
//            Toast.makeText(getActivity(), "QR image exists", Toast.LENGTH_SHORT).show();
            profPic.setImageBitmap(ppBitmap);
            fistLetter.setVisibility(View.GONE);
//            Toast.makeText(getActivity(), "Size: " + qr2.getWidth() + " " + qr2.getHeight(), Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(getActivity(), "Pic not there", Toast.LENGTH_SHORT).show();
            profPic.setVisibility(View.GONE);
        }
    }
}