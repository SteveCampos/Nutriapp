package com.nutriapp.upeu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.nutriapp.upeu.fragments.About;
import com.nutriapp.upeu.fragments.Articles;
import com.nutriapp.upeu.fragments.Developers;
import com.nutriapp.upeu.fragments.Events;
import com.nutriapp.upeu.fragments.Happend;
import com.nutriapp.upeu.fragments.HomeFragment;
import com.nutriapp.upeu.sqlite.DbUsuarios;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class DrawerMenu extends AppCompatActivity implements View.OnClickListener {
    DrawerLayout drawerLayout;
    Toolbar toolbar = null;
    ActionBarDrawerToggle drawerToggle;
    private String ID_GOOGLE;
    FragmentManager fragmentManager;
    private DbUsuarios dbUsuarios;
    private NavigationView navigationView;
    //GCM
    //GCM Android
    String SENDER_ID = "766714458924";
    //public static final long EXPIRATION_TIME_MS = 1000 * 3600 * 24 * 7;
    private static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String PROPERTY_EXPIRATION_TIME = "onServerExpirationTimeMs";
    private static final String PROPERTY_USER = "user";
    static final String TAG = "GCMDemo";
    private String userName = "usuario2";
    private Context context;
    private GoogleCloudMessaging gcm;
    private String regid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_menu);
        dbUsuarios = new DbUsuarios(this);
        dbUsuarios.open();
        ID_GOOGLE = dbUsuarios.getIdGoogleUser();
        setupToolbar();
        setupDrawerLayout();
        setupInit();
        gcmdStart();
    }
    private void gcmdStart(){
        context = getApplicationContext();
        gcm = GoogleCloudMessaging.getInstance(DrawerMenu.this);

        //Obtenemos el Registration ID guardado
        regid = getRegistrationId(context);

        Log.d("IDDEVICE", regid);
        if (regid.equals("")) {
            TareaRegistroGCM tarea = new TareaRegistroGCM();
            tarea.execute(userName);

        }
    }
    private String getRegistrationId(Context context) {
        SharedPreferences prefs = getSharedPreferences(
                MainPushRegistration.class.getSimpleName(),
                Context.MODE_PRIVATE);

        String registrationId = prefs.getString(PROPERTY_REG_ID, "");

        if (registrationId.length() == 0) {
            Log.d(TAG, "Registro GCM no encontrado.");
            return "";
        }

        String registeredUser =
                prefs.getString(PROPERTY_USER, "user");

        int registeredVersion =
                prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);

        long expirationTime =
                prefs.getLong(PROPERTY_EXPIRATION_TIME, -1);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String expirationDate = sdf.format(new Date(expirationTime));

        Log.d(TAG, "Registro GCM encontrado (usuario=" + registeredUser +
                ", version=" + registeredVersion +
                ", expira=" + expirationDate + ")");

        int currentVersion = getAppVersion(context);

        if (registeredVersion != currentVersion) {
            Log.d(TAG, "Nueva versión de la aplicación.");
            return "";
        } else if (System.currentTimeMillis() > expirationTime) {
            Log.d(TAG, "Registro GCM expirado.");
            return "";
        } else if (!userName.equals(registeredUser)) {
            Log.d(TAG, "Nuevo nombre de usuario.");
            return "";
        }

        return registrationId;
    }
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);

            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Error al obtener versión: " + e);
        }
    }

    private void setupToolbar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    private void setupDrawerLayout() {

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
       inflateHeader();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                selectDrawerItem(menuItem);
                return true;
            }
        });

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(drawerToggle);
    }
    private void inflateHeader(){
        //Inflate Layout for set Image in header android
        Cursor cursor = dbUsuarios.getUserById(ID_GOOGLE);
        Log.e("COUNT", "" + cursor.getCount());

        cursor.moveToFirst();
        byte[] byteProfile = cursor.getBlob(cursor.getColumnIndexOrThrow(DbUsuarios.US_imageProfile));
        Bitmap bitmapProfile = BitmapFactory.decodeByteArray(byteProfile, 0, byteProfile.length);
        //-----------------
        byte[] byteCover = cursor.getBlob(cursor.getColumnIndexOrThrow(DbUsuarios.US_cover));
        Bitmap bitmapCover = BitmapFactory.decodeByteArray(byteCover, 0, byteCover.length);
        Drawable drawableCover = new BitmapDrawable(getResources(), bitmapCover);
        //Getting Email and Name of Cursor
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DbUsuarios.US_usuario));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(DbUsuarios.US_correo));
        //End getting Images for Cursor
        //Set Image And email to Navigation Header
        View inflateHeaderView = navigationView.inflateHeaderView(R.layout.drawer_header);
        ImageView imageViewProfile = (ImageView) inflateHeaderView.findViewById(R.id.avatar);
        imageViewProfile.setImageBitmap(bitmapProfile);
        //Set Bscgroung image to Cover
        RelativeLayout relativeLayoutCover = (RelativeLayout) inflateHeaderView.findViewById(R.id.headerbackground);
        relativeLayoutCover.setBackground(drawableCover);
        //End
        //Set Email and Name to TextView in headerLayout
        TextView textViewName = (TextView)inflateHeaderView.findViewById(R.id.name);
        TextView textViewEmail = (TextView)inflateHeaderView.findViewById(R.id.email);
        textViewName.setText(name);
        textViewEmail.setText(email);
        //end
        //set Cancaled Suscription
        imageViewProfile.setOnClickListener(this);
        relativeLayoutCover.setOnClickListener(this);
        textViewName.setOnClickListener(this);
        textViewEmail.setOnClickListener(this);
        //end
    }


    private void setupInit() {
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();

        drawerLayout.openDrawer(GravityCompat.START);
    }

    private void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;
        Class fragmentClass;
        Float elevation = getResources().getDimension(R.dimen.elevation_toolbar);

        switch (menuItem.getItemId()) {
            case R.id.drawer_articles:
                fragmentClass = Articles.class;
                elevation = 0.0f;
                break;
            case R.id.drawer_events:
                fragmentClass = Events.class;
                elevation = 0.0f;
                break;
            case R.id.drawer_about:
                fragmentClass = About.class;
                break;
            case R.id.drawer_whatsup:
                fragmentClass = Happend.class;
                break;
            case R.id.drawer_createdby:
                fragmentClass = Developers.class;
                break;
            /*case R.id.drawer_home:
                fragmentClass = HomeFragment.class;
                break;
            case R.id.drawer_favorites:
                fragmentClass = FavoritesFragment.class;
                elevation = 0.0f;
                break;
            case R.id.drawer_settings:
                fragmentClass = SettingsFragment.class;
                break;*/

            default:
                fragmentClass = Articles.class;
                break;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.addToBackStack("FRAGMENT");
        fragmentTransaction.commit();

        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            toolbar.setElevation(elevation);

        drawerLayout.closeDrawers();
    }

    public void showSnackbarMessage(View v) {
        EditText et_snackbar = (EditText) findViewById(R.id.et_snackbar);
        TextInputLayout textInputLayout = (TextInputLayout) findViewById(R.id.textInputLayout);
        View view = findViewById(R.id.coordinator_layout);
        if (et_snackbar.getText().toString().isEmpty()) {
            textInputLayout.setError(getString(R.string.alert_text));
        } else {
            textInputLayout.setErrorEnabled(false);
            et_snackbar.onEditorAction(EditorInfo.IME_ACTION_DONE);
            Snackbar.make(view, et_snackbar.getText().toString(), Snackbar.LENGTH_LONG)
                    .setAction(getResources().getString(android.R.string.ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Do nothing
                        }
                    })
                    .show();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.name:
                setLogout();
                break;
            case R.id.email:
                setLogout();
                break;
            case R.id.headerbackground:
                setLogout();
                break;
            case R.id.avatar:
                setLogout();
                break;

        }
    }
    private void setLogout(){
       startActivity( new Intent(this,FabActivity.class));
    }

    private class TareaRegistroGCM extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... params) {
            // RestApi restApi = new RestApi();

            String msg = "";

            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }

                //Nos registramos en los servidores de GCM
                regid = gcm.register(SENDER_ID);

                Log.d(TAG, "Registrado en GCM: registration_id=" + regid);

                //Nos registramos en nuestro servidor
                // boolean registrado = registroServidor(params[0], regid);
                // JSONObject jsonObject = restApi.CreateNewAccount(params[0], regid + "");
                // Log.d(TAG, "" + jsonObject.toString());

                //Guardamos los datos del registro
                // if (jsonObject != null) {
                //     setRegistrationId(context, params[0], regid);
                // }
            } catch (IOException ex) {
                Log.d(TAG, "Error registro en GCM:" + ex.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return msg;
        }
    }
}
