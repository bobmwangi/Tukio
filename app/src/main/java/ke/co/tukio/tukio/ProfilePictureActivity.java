package ke.co.tukio.tukio;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yashoid.instacropper.InstaCropperActivity;
import com.yashoid.instacropper.InstaCropperView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.yashoid.instacropper.InstaCropperView;

import org.afinal.simplecache.ACache;

public class ProfilePictureActivity extends AppCompatActivity {
    String CurrDateTime = "blank";

    private static final String TAG = "ProfilePictureActivity";
    Button saveBtn;
    ImageView img2;

    private InstaCropperView mInstaCropper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        img2 = (ImageView) findViewById(R.id.croppedimage);

        Calendar calendar = Calendar.getInstance();
//        SimpleDateFormat mdformat = new SimpleDateFormat("EEE d, MMM yyyy, kk:mm:ss"); // MM / dd "); "EEE, MMM d, ''yy"   Wed, July 10, '96
        SimpleDateFormat mdformat = new SimpleDateFormat("d-MMM-yyyy_kk-mm-ss"); // MM / dd "); "EEE, MMM d, ''yy"   Wed, July 10, '96
        CurrDateTime = mdformat.format(calendar.getTime());


//        tv.setText("Today: "+strDate);

        mInstaCropper = (InstaCropperView) findViewById(R.id.instacropper);
        saveBtn = (Button) findViewById(R.id.saveButton);
    }

    public void pickPhoto(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Intent intent = InstaCropperActivity.getIntent(this, data.getData(), Uri.fromFile(new File(getExternalCacheDir(), CurrDateTime + ".jpg")), 720, 50);
//                    Intent intent = InstaCropperActivity.getIntent(this, Uri.fromFile(getFile()), Uri.fromFile(getFile()), 720, 50);
                    startActivityForResult(intent, 2);
                }
                return;
            case 2:
                if (resultCode == RESULT_OK) {
                    mInstaCropper.setImageUri(data.getData());

                    Glide
                            .with(ProfilePictureActivity.this)
                            .load(data.getData())
                            .centerCrop()
                            .override(200, 200)
//                            .placeholder(R.drawable.loading_spinner)
                            .into(img2);

                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
//                        img2.setImageBitmap(selectedImage);
                        ACache mCache = ACache.get(ProfilePictureActivity.this);
                        mCache.put("profile_picture", selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                    /*Uri imageUri = data.getData();
//                    Bitmap bitmap222=null;
                    try {
                       Bitmap bitmap222 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        img2.setImageBitmap(bitmap222);
                    }
                    catch (IOException e){

                    }*/

//                    img2.setImageBitmap(bitmap222);

                    Toast.makeText(this, "Pic set", Toast.LENGTH_SHORT).show();
                    ACache mCache = ACache.get(ProfilePictureActivity.this);



//                    Bitmap bitmappp = ((BitmapDrawable)img2.getDrawable()).getBitmap();


//                    img2.setDrawingCacheEnabled(true);
//                    Bitmap bmap = img2.getDrawingCache();
//                    mCache.put("profile_picture", bitmappp);

//                    //to reduce size to get thumb nail
//                    Bitmap bmap2 = bmap;
//                    int maxSize = 70;
//                    int width = bmap2.getWidth();
//                    int height = bmap2.getHeight();
//
//                    float bitmapRatio = (float) width / (float) height;
//                    if (bitmapRatio > 1) {
//                        width = maxSize;
//                        height = (int) (width / bitmapRatio);
//                    } else {
//                        height = maxSize;
//                        width = (int) (height * bitmapRatio);
//                    }
//                    //cache thumbnail
//                    mCache.put("profile_picture_thumbnail", bmap2);

//                    saveBtn.setVisibility(View.VISIBLE);

                }
                return;
        }
    }

//    public void rotate(View v) {
////        mInstaCropper.setDrawableRotation(mInstaCropper.getDrawableRotation() + 15);
//    }

    public void savePhoto(View v) {
        mInstaCropper.crop(View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), new InstaCropperView.BitmapCallback() {

            @Override
            public void onBitmapReady(Bitmap bitmap) {
                if (bitmap == null) {
                    Toast.makeText(ProfilePictureActivity.this, "Returned bitmap is null.", Toast.LENGTH_SHORT).show();
                    return;
                }

                File file = getFile();

                try {
                    FileOutputStream os = new FileOutputStream(file);

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, os);

                    os.flush();
                    os.close();

                    mInstaCropper.setImageUri(Uri.fromFile(file));

                    Log.i(TAG, "Image updated.");
                } catch (IOException e) {
                    Log.e(TAG, "Failed to compress bitmap.", e);
                }
            }

        });
    }

    private File getFile() {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "instaCropper.jpg");
    }
}
