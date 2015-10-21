package com.nutriapp.upeu;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusClient.OnAccessRevokedListener;
import com.google.android.gms.plus.model.people.Person;
import com.nutriapp.upeu.sqlite.DbUsuarios;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class SignInActivity extends Activity implements
        ConnectionCallbacks, OnConnectionFailedListener, OnClickListener,
        OnAccessRevokedListener {

    //Sqlite Open Databae
    private DbUsuarios dbUsuarios;
    //Get Activity
    private Activity activity;

    //private static final String TAG = "SignInActivity";
    private static final String DIRECTORY_IMAGES = "NutriImages";
    private static final String DIRECTORY_DOCUMENTS = "NutriDocuments";

    // A magic number we will use to know that our sign-in error
    // resolution activity has completed.
    private static final int OUR_REQUEST_CODE = 49404;

    // The core Google+ client.
    private PlusClient mPlusClient;

    // A flag to stop multiple dialogues appearing for the user.
    private boolean mResolveOnFail;

    // We can store the connection result from a failed connect()
    // attempt in order to make the application feel a bit more
    // responsive for the user.
    private ConnectionResult mConnectionResult;

    // A progress dialog to display when the user is connecting in
    // case there is a delay in any of the dialogs being ready.
    private ProgressDialog mConnectionProgressDialog;
    //For the ImageView
    private ImageView imageViewAcount;
    //
    private static String stringIgGoogle;



    private TextView textViewEstado;
    //private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_test);
        textViewEstado = (TextView) findViewById(R.id.textEstado);
        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Log.d("IDANDROID", "" + android_id);


        //}
        //else
        //{
        //    Log.i(TAG, "No se ha encontrado Google Play Services.");
        //}
        //   }
        // });
        //for open database
        dbUsuarios = new DbUsuarios(this);
        dbUsuarios.open();
        stringIgGoogle = dbUsuarios.getIdGoogleUser();
        //Is connected Internet
        if (stringIgGoogle != "") {
            if (estaConectado()) {
             //Si no disponemos de Registration ID comenzamos el registro

                activity = this;
                displayWidgets();

            } else {
                textViewEstado.setText("No tiene conexion a internet.");
                Intent intent = new Intent(getApplicationContext(), DrawerMenu.class);
                intent.putExtra("idGoogle", stringIgGoogle);
                startActivity(intent);

            }
        } else {
            //displayMessageConnectedFirst();
            //Toast Necesitas estar Conectado la primera vez
            //Toast.makeText(getApplicationContext(),"Necesita estar conectado la primera vez",Toast.LENGTH_LONG).show();

        }


    }

    private void displayMessageConnectedFirst() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
        alertDialogBuilder.setTitle("Ten En Cuenta");
        alertDialogBuilder.setMessage("Necesita estar conectado a internet al menos la primera vez.");
        AlertDialog.Builder builder = alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Esta Bien", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }

                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }





    private void displayWidgets() {
        // 3. Object to call onConnectionFailed on
        imageViewAcount = (ImageView) findViewById(R.id.imgAcount);
        mPlusClient = new PlusClient.Builder(this, this, this).setActions("http://schemas.google.com/BuyActivity")
                .build();


        // We use mResolveOnFail as a flag to say whether we should trigger
        // the resolution of a connectionFailed ConnectionResult.
        mResolveOnFail = false;

        // Connect our sign in, sign out and disconnect buttons.
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        // findViewById(R.id.sign_out_button).setOnClickListener(this);
        // findViewById(R.id.revoke_access_button).setOnClickListener(this);
        // findViewById(R.id.sign_out_button).setVisibility(View.INVISIBLE);
        // findViewById(R.id.revoke_access_button).setVisibility(View.INVISIBLE);

        // Configure the ProgressDialog that will be shown if there is a
        // delay in presenting the user with the next sign in step.
        mConnectionProgressDialog = new ProgressDialog(this);
        mConnectionProgressDialog.setMessage("Conectando");

    }

    protected Boolean estaConectado() {
        if (conectadoWifi()) {
            // user.setText("Conexion a Wifi");
            return true;
        } else {
            if (conectadoRedMovil()) {
                //    user.setText("Conexion a Movil");
                return true;
            } else {
                //  user.setText("No Tiene Conexion a Internet");
                return false;
            }
        }
    }

    private void createDirectoryForImages() {

        //
        File folder = new File(Environment.getExternalStorageDirectory() + "/" + DIRECTORY_IMAGES);
        boolean success = true;
        if (!folder.exists()) {
            Toast.makeText(this, "Directory Images Does Not Exist, Create It", Toast.LENGTH_SHORT).show();
            success = folder.mkdir();
        }
        if (success) {
            Toast.makeText(this, "Directory Images Created", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed - Error Images", Toast.LENGTH_SHORT).show();
        }

    }

    private void createDirectoryForDocuments() {
        //
        File folder = new File(Environment.getExternalStorageDirectory() + "/" + DIRECTORY_DOCUMENTS);
        boolean success = true;
        if (!folder.exists()) {
            Toast.makeText(this, "Directory Documents Does Not Exist, Create It", Toast.LENGTH_SHORT).show();
            success = folder.mkdir();
        }
        if (success) {
            Toast.makeText(this, "Directory Documents Created", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed - Error Documents", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(getApplicationContext(), "Start", Toast.LENGTH_SHORT).show();
        // Every time we start we want to try to connect. If it
        // succeeds we'll get an onConnected() callback. If it
        // fails we'll get onConnectionFailed(), with a result!
        if (estaConectado()) {
            activity = this;
            displayWidgets();
            mPlusClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        // It can be a little costly to keep the connection open
        // to Google Play Services, so each time our activity is
        // stopped we should disconnect.
        if (estaConectado()) {
            mPlusClient.disconnect();
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Toast.makeText(getApplicationContext(), "ConnectionFailed", Toast.LENGTH_SHORT).show();
        // Most of the time, the connection will fail with a
        // user resolvable result. We can store that in our
        // mConnectionResult property ready for to be used
        // when the user clicks the sign-in button.
        if (result.hasResolution()) {
            mConnectionResult = result;
            if (mResolveOnFail) {
                // This is a local helper function that starts
                // the resolution of the problem, which may be
                // showing the user an account chooser or similar.
                startResolution();
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Yay! We can get the oAuth 2.0 access token we are using.
        Toast.makeText(getApplicationContext(), "Exito Conectado!", Toast.LENGTH_SHORT).show();

        // Turn off the flag, so if the user signs out they'll have to
        // tap to sign in again.
        mResolveOnFail = false;

        // Hide the progress dialog if its showing.
        mConnectionProgressDialog.dismiss();

        // Hide the sign in button, show the sign out buttons.
        //findViewById(R.id.sign_in_button).setVisibility(View.INVISIBLE);
        //findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
        // findViewById(R.id.revoke_access_button).setVisibility(View.VISIBLE);

        // Retrieve the oAuth 2.0 access token.
        final Context context = this.getApplicationContext();
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object... params) {
                String scope = "oauth2:" + Scopes.PLUS_LOGIN;
                try {
                    // We can retrieve the token to check via
                    // tokeninfo or to pass to a service-side
                    // application.
                    String token = GoogleAuthUtil.getToken(context,
                            mPlusClient.getAccountName(), scope);
                    //

                } catch (UserRecoverableAuthException e) {
                    // This error is recoverable, so we could fix this
                    // by displaying the intent to the user.
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (GoogleAuthException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                Toast.makeText(getApplicationContext(), "ID" + mPlusClient.getCurrentPerson().getId() + ": NAME" + mPlusClient.getCurrentPerson().getName() + " ", Toast.LENGTH_SHORT).show();


                Person person = mPlusClient.getCurrentPerson();
                if(person.hasCover() && person.hasImage()){
                    setImageViewCover(person, mPlusClient.getAccountName());
                }else {

                }

                //setImageView(person);

                //
                super.onPostExecute(o);
            }
        };
        task.execute((Void) null);
    }

    protected Boolean conectadoWifi() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }

    protected Boolean conectadoRedMovil() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }


    private void setImageViewCover(final Person photo, String email) {
        new AsyncTask<String, Void, Bitmap>() {
            private String idGoogle;
            private String name;
            private String email;
            private Bitmap coverImage;
            private Bitmap photoImage;


            @Override
            protected Bitmap doInBackground(String... params) {

                try {

                    String[] urls = params[4].split("\\?");
                    Log.e("URL", "" + urls[0]);
                    URL urlCover = new URL(params[0]);
                    idGoogle = params[1];
                    name = params[2];
                    email = params[3];
                    URL urlProfile = new URL(urls[0]);
                    //Get Cover Image
                    InputStream inCover = urlCover.openStream();
                    coverImage = BitmapFactory.decodeStream(inCover);
                    //end
                    //Get Photo Image
                    InputStream inPhoto = urlProfile.openStream();
                    // photoImage = BitmapFactory.decodeStream(inPhoto);
                    photoImage = BitmapFactory.decodeStream(inPhoto);
                    //Return for not building error
                    return BitmapFactory.decodeStream(inCover);
                } catch (Exception e) {
                        /* TODO log error */
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                ByteArrayOutputStream streamProfile = new ByteArrayOutputStream();
                ByteArrayOutputStream streamCover = new ByteArrayOutputStream();
                coverImage.compress(Bitmap.CompressFormat.JPEG, 100, streamCover);
                byte[] byteArrayCover = streamCover.toByteArray();
                photoImage.compress(Bitmap.CompressFormat.PNG, 100, streamProfile);
                byte[] byteArrayProfile = streamProfile.toByteArray();
                String[] arrayPerson = getInformation(name);
                //Log.e("DATOS", idGoogle + "-" + name + "-" + byteArray.toString() + "-" + byteArray.toString() + "-" + email + "-" + 1);
                long state = dbUsuarios.createUser(idGoogle, byteArrayProfile, byteArrayCover, email, arrayPerson[0], arrayPerson[1], 1);
                Log.e("Estado", "ESTADO" + state);

                if (state > 0) {
                    imageViewAcount.setImageResource(R.mipmap.health);
                    Intent intent = new Intent(getApplicationContext(), DrawerMenu.class);
                    intent.putExtra("idGoogle", idGoogle);
                    startActivity(intent);
                    activity.finish();

                } else {
                    Toast.makeText(getApplicationContext(), "Ocurrio un error", Toast.LENGTH_SHORT).show();
                }


            }
        }.execute(photo.getCover().getCoverPhoto().getUrl(), photo.getId(), photo.getName().toString(), email, photo.getImage().getUrl());
    }

    private String[] getInformation(String objJsonPerson) {

        String[] dateReturn = new String[]{"", ""};
        try {
            JSONObject jsonObjectPerson = new JSONObject(objJsonPerson);
            dateReturn[0] = jsonObjectPerson.getString("familyName");
            dateReturn[1] = jsonObjectPerson.getString("givenName");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dateReturn;
    }

    @Override
    public void onDisconnected() {
        // Bye!
        Toast.makeText(getApplicationContext(), "Disconnected. Bye!", Toast.LENGTH_SHORT).show();
    }

    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        Toast.makeText(getApplicationContext(), "ActivityResult: " + requestCode, Toast.LENGTH_SHORT).show();
        if (requestCode == OUR_REQUEST_CODE && responseCode == RESULT_OK) {
            // If we have a successful result, we will want to be able to
            // resolve any further errors, so turn on resolution with our
            // flag.
            mResolveOnFail = true;
            // If we have a successful result, lets call connect() again. If
            // there are any more errors to resolve we'll get our
            // onConnectionFailed, but if not, we'll get onConnected.
            mPlusClient.connect();
        } else if (requestCode == OUR_REQUEST_CODE && responseCode != RESULT_OK) {
            // If we've got an error we can't resolve, we're no
            // longer in the midst of signing in, so we can stop
            // the progress spinner.
            mConnectionProgressDialog.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                Toast.makeText(getApplicationContext(), "Tapped sign in", Toast.LENGTH_SHORT).show();
                if (!mPlusClient.isConnected()) {
                    // Show the dialog as we are now signing in.
                    mConnectionProgressDialog.show();
                    // Make sure that we will start the resolution (e.g. fire the
                    // intent and pop up a dialog for the user) for any errors
                    // that come in.
                    mResolveOnFail = true;
                    // We should always have a connection result ready to resolve,
                    // so we can start that process.
                    if (mConnectionResult != null) {
                        startResolution();
                    } else {
                        // If we don't have one though, we can start connect in
                        // order to retrieve one.
                        mPlusClient.connect();
                    }
                }else{
                    startActivity(new Intent(getApplicationContext(),DrawerMenu.class).putExtra("idGoogle",stringIgGoogle));
                }
                break;

            default:
                // Unknown id.
        }
    }

    @Override
    public void onAccessRevoked(ConnectionResult status) {
        // mPlusClient is now disconnected and access has been revoked.
        // We should now delete any data we need to comply with the
        // developer properties. To reset ourselves to the original state,
        // we should now connect again. We don't have to disconnect as that
        // happens as part of the call.
        mPlusClient.connect();

        // Hide the sign out buttons, show the sign in button.
        findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        //  findViewById(R.id.sign_out_button).setVisibility(View.INVISIBLE);
        //  findViewById(R.id.revoke_access_button).setVisibility(View.INVISIBLE);
    }

    /**
     * A helper method to flip the mResolveOnFail flag and start the resolution
     * of the ConnenctionResult from the failed connect() call.
     */
    private void startResolution() {
        try {
            // Don't start another resolution now until we have a
            // result from the activity we're about to start.
            mResolveOnFail = false;
            // If we can resolve the error, then call start resolution
            // and pass it an integer tag we can use to track. This means
            // that when we get the onActivityResult callback we'll know
            // its from being started here.
            mConnectionResult.startResolutionForResult(this, OUR_REQUEST_CODE);
        } catch (SendIntentException e) {
            // Any problems, just try to connect() again so we get a new
            // ConnectionResult.
            mPlusClient.connect();
        }
    }


}