package ke.co.tukio.tukio;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import ke.co.tukio.tukio.util.CheckConnectivity;

public class SignupActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    // Google sign in
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [START declare_auth_listener]
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;


    String publicjson = "nothing";
    MainActivity myMainActy;
    EditText editEmail, editName;
    ProgressDialog pDialog;
    TextView mStatusTextView;
    String MyREsponseFromphp, firebToken = "empty";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

//        findViewById(R.id.sign_in_button).setOnClickListener(SignupActivity.this);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        mStatusTextView = (TextView) findViewById(R.id.loginSt);
        editEmail = (EditText) findViewById(R.id.input_email);
        editName = (EditText) findViewById(R.id.input_name);
        editEmail.setEnabled(false);
        //notif bar color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(SignupActivity.this, R.color.colorPrimaryDark));
        }

        //get user email
        String email = null;

        Pattern gmailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(this).getAccounts();
        for (Account account : accounts) {
            if (gmailPattern.matcher(account.name).matches()) {
                email = account.name;
            }
            editEmail.setText(email);
        }
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        firebToken = myPrefs.getString("firebaseToken", "");


        //begin Google login
        // [START config_signin]-Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // [START auth_state_listener]
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d(TAG, "**********************************************************");
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getDisplayName());
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getPhotoUrl());
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getEmail());
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getProviderId());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // [START_EXCLUDE]
                updateUI(user);
                // [END_EXCLUDE]
            }
        };
        // [END auth_state_listener]
    }

    // [START on_start_add_listener]
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
        mAuth.addAuthStateListener(mAuthListener);
    }
    // [END on_start_add_listener]

    // [START on_stop_remove_listener]
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    // [END on_stop_remove_listener]

    // [START on_stop_remove_listener]
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }
    // [END on_stop_remove_listener]

    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult()");
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                System.out.println("Google user id: " + account.getId());
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }
    // [END onactivityresult]


    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        //showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [START_EXCLUDE]
                        //hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {
        Log.d(TAG, "signIn()");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

  /*  private void signOut() {
        Log.d(TAG, "signOut()");
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        updateUI(null);
                    }
                });
    }

    private void revokeAccess() {
        Log.d(TAG, "revokeAccess()");
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        updateUI(null);
                    }
                });
    }*/

    private void updateUI(FirebaseUser user) {
        Log.d(TAG, "updateUI()");
        //hideProgressDialog();
        if (user != null) {
            editEmail.setText(user.getEmail());
            editName.setText(user.getDisplayName());

            mStatusTextView.setText("signed_in");
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            //check net
            CheckConnectivity checkNetwork = new CheckConnectivity();
            if (checkNetwork.isConnected(SignupActivity.this)) {
                prepareSend();
            } else {
                Toast.makeText(myMainActy, "An internet connection is required to register your Account.", Toast.LENGTH_SHORT).show();
            }
//            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
//            mStatusTextView.setText(R.string.signed_out);
            mStatusTextView.setText("signed_out");
//            Toast.makeText(myMainActy, "USer is signed out", Toast.LENGTH_SHORT).show();
//            editEmail.setText(null);
//            editName.setText(null);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
//            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View vv) {

        int i = vv.getId();
        if (i == R.id.sign_in_button) {
            signIn();
        }
    }


    public void signup(View v) {
        checknet();
    }

    public void checknet() {
        ConnectivityManager cn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf = cn.getActiveNetworkInfo();
        if (nf != null && nf.isConnected() == true) {
            prepareSend();
        } else Snackbar();
    }

    public void CheckConnPlaces() {
        ConnectivityManager cn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf = cn.getActiveNetworkInfo();
        if (nf != null && nf.isConnected() == true) {
            refreshmap2();
        } else
            Toast.makeText(myMainActy, "Could not fetch venues. No internet connection.", Toast.LENGTH_SHORT).show();
    }

    public void checknetGoogleSignin() {
        ConnectivityManager cn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf = cn.getActiveNetworkInfo();
        if (nf != null && nf.isConnected() == true) {
            signIn();
        } else Snackbar();
    }

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


    public void prepareSend() {
        String uname, uemail, fbToken;
        uemail = editEmail.getText().toString();
        uname = editName.getText().toString();

        if (uname.isEmpty()) {
            Toast.makeText(this, "Please fill in both fields. You can also trying logging in with Google", Toast.LENGTH_SHORT).show();
            return;
        }
        if (uemail.isEmpty()) {
            Toast.makeText(this, "Please fill in both fields. You can also trying logging in with Google", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!uemail.contains("@") | uemail.length() < 5 | uemail.contains(" ")) {
            Toast.makeText(this, "Email invalid", Toast.LENGTH_SHORT).show();
        } else {
            SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            fbToken = myPrefs.getString("firebaseToken", "");
            Toast.makeText(this, "token " + fbToken, Toast.LENGTH_SHORT).show();

            pDialog = new ProgressDialog(SignupActivity.this);
            pDialog.setMessage("Sending...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
            insertToDatabase(uname, uemail, fbToken, "app");
        }
    }

    private void insertToDatabase(final String uname, String uemail, String fbToken, String RegSource) {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String paramname = params[0];
                String paramemail = params[1];
                String paramfbToken = params[2];
                String paramRegSource = params[3];

                // InputStream is = null;

                String uname = paramname;
                String uemail = paramemail;
                String fbToken = paramfbToken;
                String RegSource = paramRegSource;


                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
                nameValuePairs.add(new BasicNameValuePair("uname", uname));
                nameValuePairs.add(new BasicNameValuePair("uemail", uemail));
                nameValuePairs.add(new BasicNameValuePair("fbToken", fbToken));
                nameValuePairs.add(new BasicNameValuePair("RegSource", RegSource));

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://www.tukio.co.ke/applicationfiles/appinsertusers.php");
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpClient.execute(httpPost);

                    HttpEntity entity = response.getEntity();

                    final String content;
                    try {
                        content = EntityUtils.toString(entity);

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                MyREsponseFromphp = content;
                            }
                        });

                    } catch (ClientProtocolException e) {
                        Toast.makeText(SignupActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                } catch (ClientProtocolException e) {
                    Toast.makeText(SignupActivity.this, "ClientProtocolException", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(SignupActivity.this, "IOException", Toast.LENGTH_SHORT).show();
                }

                return ("done");


            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                pDialog.dismiss();
                if (MyREsponseFromphp.contentEquals("Account created")) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignupActivity.this);
                    alertDialog.setTitle("Success");
                    alertDialog.setCancelable(false);
                    alertDialog.setMessage("You have successfully created an account.");
                    alertDialog.setPositiveButton("proceed",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    QueryId();
                                }
                            });

                    alertDialog.show();
                    pDialog.dismiss();
//                    editName.setText("");
//                    editEmail.setText("");
                } else if (MyREsponseFromphp.contentEquals("Failed to create account")) {
                    Toast.makeText(SignupActivity.this, " Failed to create account", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();

                } else if (MyREsponseFromphp.contentEquals("Account exists")) {
                    Toast.makeText(SignupActivity.this, "Account exists. Recovering account.", Toast.LENGTH_SHORT).show();
                    QueryId();
                } else {
                    Toast.makeText(SignupActivity.this, "No response.", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                }

                //  setProgressBarVisibility(false);
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(uname, uemail, fbToken, RegSource);
    }

 /*   public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);

            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
*/


    //query id
    public void QueryId() {
        pDialog = new ProgressDialog(SignupActivity.this);
        pDialog.setMessage("Preparing account...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
        new Thread() {
            //        @Override
            public void run() {


                String uemail = editEmail.getText().toString().trim();

                String path = "http://www.tukio.co.ke/applicationfiles/showuserid.php?uemail=" + uemail;
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
                            pDialog.dismiss();

                            String returnedIDUntrimmed = bo.toString();
                            String returnedID = returnedIDUntrimmed.trim();


                            PopAccountDetails(returnedID);
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

    public void PopAccountDetails(final String returnedID) {
        if (!returnedID.isEmpty()) {
            final String username = editName.getText().toString().trim();
            final String useremail = editEmail.getText().toString().trim();

//            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
//            alertDialog.setTitle("Account is ready.");
//            alertDialog.setMessage("Your user ID is: " + returnedID);
//            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
            SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(SignupActivity.this);
            SharedPreferences.Editor editor = myPrefs.edit();
            editor.putString("firstrun", "yes");
            editor.putString("username", username);
            editor.putString("useremail", useremail);
            editor.putString("userId", returnedID.trim());
            editor.apply();
            editName.setText("");
            editEmail.setText("");

            Toast.makeText(SignupActivity.this, "Login details saved: " + returnedID + " " + username, Toast.LENGTH_SHORT).show();


            Intent pIntent = new Intent(getBaseContext(), MainActivity.class);
            startActivityForResult(pIntent, 0);

            finish();
//                }
//            });
//
//            alertDialog.show();
        }
        if (returnedID.isEmpty()) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Error.");
            alertDialog.setMessage("There was an error establishing your account. Please try again.");
            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });


        }
    }


    //get places
    public void refreshmap2() {
        ArrayList<HashMap<String, String>> PlaceList;
        PlaceList = new ArrayList<>();
        new Getplaces().execute();
    }

    private class Getplaces extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            Toast.makeText(SignupActivity.this,"Refreshing for new data",Toast.LENGTH_LONG).show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HTTPHandler sh = new HTTPHandler();
            // Making a request to url and getting response
            String url = "http://tukio.co.ke/applicationfiles/getlocations.php";
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            publicjson = jsonStr;
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray places = jsonObj.getJSONArray("places");


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

            FileOutputStream fos;
            try {
                fos = openFileOutput("jsonMapsDatafile.txt", Context.MODE_PRIVATE);
                //default mode is PRIVATE, can be APPEND etc.
                fos.write(publicjson.getBytes());
                fos.close();

                Toast.makeText(getApplicationContext(), "Map data received.", Toast.LENGTH_LONG).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
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
