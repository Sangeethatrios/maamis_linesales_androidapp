package thanjavurvansales.sss;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {
    @BindViews({R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6,
            R.id.btn7, R.id.btn8, R.id.btn9, R.id.btn_clear})
    List<View> btnNumPads;
    @BindViews({R.id.dot_1, R.id.dot_2, R.id.dot_3, R.id.dot_4})
    List<Button> dots;
    private static final int MAX_LENGHT = 4;
    private String codeString = "";
    public Context context;
    static String deviceid = "";
    boolean networkstate;
    public String getpinvalue="";
    public String setpinvalue="";
    private Boolean exit = false;
    TextView txtvanname;
    boolean callschedule=false;
    String TAG = "DBG";
    public static String getpin="";
    public static  String getformatdate = "",getcurrentdatetime = "",getareaname = "",
            getareacode="",getvancode="",getvanname="",getfinanceyrcode="",getbusiness_type="",getorderprint="";
    //public static PrintData p;
    ImageView menusettings;
    Dialog dialog;
    public static  String ipaddress = "";
    public static boolean iscash=false;
    String[] getlocalipaddress = new String[0];
    public static  boolean ismenuopen=false;
    ImageView lock;
    ListView LVServerURLList;
    LinearLayout LLListviewlayout;
    private ScheduledExecutorService scheduleTaskExecutor,scheduleTaskExecutorForPricelist;
    String[] serveripadd,status,serverid,serverurlname;
    String Temp_ID,Temp_IP;
    EditText edittxtip,edittxturlname;
    Button btnSaveSettings;
    int StorageRequestCode=10;
    public static PreferenceMangr preferenceMangr=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;
        //Full screen window
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Create printdata object
        //p = new PrintData(context);


        //Declare all variables
        ButterKnife.bind(this);
        Button clean = (Button) findViewById(R.id.clear);
        txtvanname = (TextView)findViewById(R.id.txtvanname);
        lock = (ImageView)findViewById(R.id.lock);

        //Call Database
        DataBaseAdapter mDbHelper = new DataBaseAdapter(context);
        mDbHelper.createDatabase();

        //Set  version
        TextView lblversion = (TextView) findViewById(R.id.lblversion);
        try
        {
            lblversion.setText("v"+this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName);
        }
        catch (Exception e)
        {
            Log.e("Version", e.getMessage());
        }

        try {
            preferenceMangr = new PreferenceMangr(context);
        }catch (Exception e){
            Log.d("Preference Manager : ",e.toString());
        }

        //Get IP Address
        DataBaseAdapter objdatabaseadapter = null;
        try {
            //Order item details
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            String getipadd="";
            getipadd=objdatabaseadapter.GetServerSettingsDB();
            ipaddress = getipadd;
            preferenceMangr.pref_putString("ipaddress",getipadd);


        } catch (Exception e) {
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        } finally {
            if(objdatabaseadapter!=null)
                objdatabaseadapter.close();
        }

        //Clean password button values
        clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (codeString.length() > 0) {
                    //remove last character of code
                    codeString = removeAllChar(codeString);
                    setDotImagesState();
                }
                shakeAnimation();
            }
        });


        //Set lock
        lock.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                    dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.generalsettings);
                ImageView closepopup = (ImageView) dialog.findViewById(R.id.closepopup);
                closepopup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeKeyboard((Activity) context);
                        dialog.dismiss();
                    }
                });
//final EditText edittxtip
                edittxtip = (EditText)dialog.findViewById(R.id.edittxtip);
                edittxturlname = (EditText)dialog.findViewById(R.id.edittxturlname);
                btnSaveSettings = (Button)dialog.findViewById(R.id.btnAddSettings);
                btnSaveSettings.setText("Add");
                 LVServerURLList = (ListView)dialog.findViewById(R.id.LVServerURLList);
                LLListviewlayout = (LinearLayout) dialog.findViewById(R.id.LLListviewlayout);
                if(!ipaddress.equals("") && !ipaddress.equals("null") && !ipaddress.equals(null) ){
                    //edittxtip.setText(ipaddress);
                }
                GetServerURLList();


                btnSaveSettings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(edittxtip.getText().toString().trim().equals("")
                                || edittxtip.getText().toString().equals(null)){
                            Toast toast = Toast.makeText(getApplicationContext(),"Please enter server URL", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return;
                        }

                        if(edittxturlname.getText().toString().trim().equals("")
                                || edittxturlname.getText().toString().equals(null)){
                            Toast toast = Toast.makeText(getApplicationContext(),"Please enter URL Name", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return;
                        }

                        DataBaseAdapter objdatabaseadapter = null;
                        try {
                            //Order item details
                            objdatabaseadapter = new DataBaseAdapter(context);
                            objdatabaseadapter.open();
                            String getresult="";
                            if(btnSaveSettings.getText().equals("Add")) {
                                getresult = objdatabaseadapter.InsertServerSettings(edittxtip.getText().toString(),edittxturlname.getText().toString());
                                if (getresult.equals("success")) {
                                    GetServerURLList();
                                    edittxtip.setText("");
                                    edittxturlname.setText("");
                                    Toast toast = Toast.makeText(getApplicationContext(), "Saved Successfully ", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();

                                }
                            }
                            if(btnSaveSettings.getText().equals("Update")) {
                                getresult = objdatabaseadapter.EditServerSettings(edittxtip.getText().toString(),Temp_ID,edittxturlname.getText().toString());
                                if (getresult.equals("success")) {
                                    GetServerURLList();
                                    edittxtip.setText("");
                                    edittxturlname.setText("");
                                    btnSaveSettings.setText("Add");
                                    Toast toast = Toast.makeText(getApplicationContext(), "Updated Successfully ", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();

                                }
                            }

                        } catch (Exception e) {
                            Toast toast = Toast.makeText(getApplicationContext(),"Error in saving", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();

                            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                            mDbErrHelper.open();
                            String geterrror = e.toString();
                            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            mDbErrHelper.close();
                        } finally {
                            if(objdatabaseadapter!=null)
                                objdatabaseadapter.close();
                        }
                    }
                });


                // show soft keyboard
                edittxturlname.requestFocus();
//                edittxtip.requestFocus();
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                dialog.setCancelable(false);
                dialog.show();
                return true;
            }
        });
        //Get phone imei number
        /*if(ContextCompat.checkSelfPermission(context,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(LoginActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE}, StorageRequestCode);
        }else{
            GetIMEI();
        }*/

        checkPermission();




        //Call schedule every 15 minutes
        //Schedule a task to run every 5 seconds (or however long you want)
        scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                // Do stuff here!

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        networkstate = isNetworkAvailable();
                        if (networkstate == true) {
                            if(!preferenceMangr.pref_getString("getvancode").equals("") && !preferenceMangr.pref_getString("getvancode").equals("null")
                                    && preferenceMangr.pref_getString("getvancode") != "") {
                                new AsynconlineStatus().execute();
                            }
                        }
                        // Do stuff to update UI here!
                        //Toast.makeText(context, "Refreshing", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }, 0, 900, TimeUnit.SECONDS);


        scheduleTaskExecutorForPricelist = Executors.newScheduledThreadPool(5);
        scheduleTaskExecutorForPricelist.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                // Do stuff here!

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        networkstate = isNetworkAvailable();
                        if (networkstate == true) {
                            if(!preferenceMangr.pref_getString("getvancode").equals("") && !preferenceMangr.pref_getString("getvancode").equals("null")
                                    && preferenceMangr.pref_getString("getvancode") != "") {
                                new AsyncPricelist().execute();
                                new AsyncGetOrderStatus().execute();
                            }
                        }
                        // Do stuff to update UI here!
                        //Toast.makeText(context, "Its been 5 seconds", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }, 0, 600, TimeUnit.SECONDS);


        /*try{
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);


            PrinterSettingsActivity.SelectedPrinterName=(preferences.getString("SelectedPrinterName", ""));
            PrinterSettingsActivity.SelectedPrinterAddress=(preferences.getString("SelectedPrinterAddress", ""));

        }catch(Exception e){
            Log.d("Insert printer details ",e.toString());
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }*/
    }


    public void GetServerURLList(){
        try {
            DataBaseAdapter objcustomerAdaptor = new DataBaseAdapter(context);
            objcustomerAdaptor.open();
            Cursor mCur = objcustomerAdaptor.GetServerurllistdata();
            objcustomerAdaptor.close();
            serveripadd = new String[0];
            serverid = new String[0];
            status = new String[0];
           serverurlname=new String[0];
            if (mCur.getCount() > 0) {
                serveripadd = new String[mCur.getCount()];
                serverid = new String[mCur.getCount()];
                status = new String[mCur.getCount()];
                serverurlname = new String[mCur.getCount()];
                for (int i = 0; i < mCur.getCount(); i++) {
                    serverid[i] =  mCur.getString(0);
                    serveripadd[i] =  mCur.getString(1);
                    status[i] =  mCur.getString(2);
                    serverurlname[i]=mCur.getString(3);
                    mCur.moveToNext();
                }
                LLListviewlayout.setVisibility(View.VISIBLE);
                ServerIpAdapter adapter = new ServerIpAdapter(context);
                LVServerURLList.setAdapter(adapter);
            } else {
                LLListviewlayout.setVisibility(View.GONE);
                Toast toast = Toast.makeText(getApplicationContext(),"No Data Available", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                // Toast.makeText(context, "No Data Available", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }


    public class ServerIpAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;
        Dialog serverpopup  ;
        ServerIpAdapter(Context c) {
            context = c;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return serverid.length;
        }

        @Override
        public Object getItem(int position) {
            return serverid[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public int getViewTypeCount() {
            return getCount();
        }
        @Override
        public int getItemViewType(int position) {
            return position;
        }
        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder mHolder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.serverpopuplist, parent, false);
                mHolder = new ViewHolder();
                try {
                    mHolder.txtserversno = (TextView) convertView.findViewById(R.id.txtserversno);
                    mHolder.txtserverurl = (TextView) convertView.findViewById(R.id.txtserverurl);
                    mHolder.txturlname = (TextView) convertView.findViewById(R.id.txturlname);
                    mHolder.imgserverstatus = (ImageView) convertView.findViewById(R.id.imgserverstatus);
                    mHolder.listLLayout = (LinearLayout) convertView.findViewById(R.id.listLLayout);
                    mHolder.imgserverurldelete = (ImageView)convertView.findViewById(R.id.imgserverurldelete);
                    mHolder.imgserverurledit = (ImageView)convertView.findViewById(R.id.imgserverurledit);

                } catch (Exception e) {
                    Log.i("Route", e.toString());
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                }
                convertView.setTag(mHolder);
            } else {
                mHolder = (ViewHolder) convertView.getTag();
            }
            try {
                mHolder.txtserversno.setText(String.valueOf(position+1));
                mHolder.txtserverurl.setText(String.valueOf(serveripadd[position]));
                mHolder.txturlname.setText(String.valueOf(serverurlname[position]));
                if(status[position].equals("inactive")){
                    mHolder.imgserverstatus.setImageResource(R.drawable.ic_backup);
                }else{
                    mHolder.imgserverstatus.setImageResource(R.drawable.ic_checked);
                    mHolder.imgserverurldelete.setVisibility(View.INVISIBLE);
                    mHolder.imgserverurledit.setVisibility(View.INVISIBLE);
                }

               /* mHolder.txtserverurl.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return false;
                    }
                });*/
                mHolder.imgserverurledit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DataBaseAdapter objdatabaseadapter = null;
                        Cursor Cur = null;
                        try {
                            objdatabaseadapter = new DataBaseAdapter(context);
                            objdatabaseadapter.open();

                            String strid = serverid[position];
                            Cur = objdatabaseadapter.GetServerdetailDB(strid);
                            if (Cur.getCount() > 0) {
                                for (int i = 0; i < Cur.getCount(); i++) {
                                    String Val = Cur.getString(1);
                                    Temp_ID = Cur.getString(0);
                                    Temp_IP = Cur.getString(1);
                                    String urlname = Cur.getString(3);
                                    edittxtip.setText(Val);
                                    edittxturlname.setText(urlname);
                                    btnSaveSettings.setText("Update");
                                }
                            }
                        }catch (Exception e) {
                            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                            mDbErrHelper.open();
                            String geterrror = e.toString();
                            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            mDbErrHelper.close();
                        } finally {
                            if(objdatabaseadapter!=null)
                                objdatabaseadapter.close();
                            if(Cur != null)
                                Cur.close();
                        }
                    }
                });
               mHolder.imgserverurldelete.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                       builder.setTitle("Confirmation");
                       builder.setMessage("Are you sure you want to delete?")
                               .setCancelable(false)
                               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                   public void onClick(DialogInterface dialog, int id) {
                                       DataBaseAdapter objdatabaseadapter = null;
                                       try {
                                           objdatabaseadapter = new DataBaseAdapter(context);
                                           objdatabaseadapter.open();
                                           String getresult=objdatabaseadapter.DeleteServerSettings(serverid[position]);
                                           if(getresult.equals("success")){
                                               Toast toast = Toast.makeText(getApplicationContext(),"Deleted Successfully", Toast.LENGTH_LONG);
                                               toast.setGravity(Gravity.CENTER, 0, 0);
                                               toast.show();
                                               //Get IP Address
                                               DataBaseAdapter objdatabaseadapter2 = null;
                                               try {
                                                   //Order item details
                                                   objdatabaseadapter2 = new DataBaseAdapter(context);
                                                   objdatabaseadapter2.open();
                                                   String getipadd="";
                                                   getipadd=objdatabaseadapter2.GetServerSettingsDB();
                                                   ipaddress = getipadd;
                                                   preferenceMangr.pref_putString("ipaddress",getipadd);

                                               } catch (Exception e) {
                                                   DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                                   mDbErrHelper.open();
                                                   String geterrror = e.toString();
                                                   mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                                   mDbErrHelper.close();
                                               } finally {
                                                   if(objdatabaseadapter2!=null)
                                                       objdatabaseadapter2.close();
                                               }
                                               GetServerURLList();
                                           }
                                       } catch (Exception e) {
                                           Toast toast = Toast.makeText(getApplicationContext(),"Error in deleting", Toast.LENGTH_LONG);
                                           toast.setGravity(Gravity.CENTER, 0, 0);
                                           toast.show();

                                           DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                           mDbErrHelper.open();
                                           String geterrror = e.toString();
                                           mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                           mDbErrHelper.close();
                                       } finally {
                                           if(objdatabaseadapter!=null)
                                               objdatabaseadapter.close();
                                       }
                                   }
                               })
                               .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                   public void onClick(DialogInterface dialog, int id) {
                                       dialog.cancel();

                                   }
                               });
                       android.support.v7.app.AlertDialog alert = builder.create();
                       alert.show();
                   }
               });
                mHolder.listLLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                     //   ProgressDialog loading = null;
                        try {

                          /*  loading = ProgressDialog.show(context, "Connecting", "Please wait...", true, true);
                            loading.setCancelable(false);
                            loading.setCanceledOnTouchOutside(false);*/
                            serverpopup = new Dialog(context);
                            serverpopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            serverpopup.setContentView(R.layout.serverpopup);

                            final TextView txtYesAction = (TextView) serverpopup.findViewById(R.id.txtYesAction);
                            final TextView txtNoAction = (TextView) serverpopup.findViewById(R.id.txtNoAction);
                            final TextView txtservermgs = (TextView) serverpopup.findViewById(R.id.txtservermgs);
                            if(status[position].equals("inactive")) {
                                txtservermgs.setText("Are you sure you want to active this server URL?");
                            }else{
                                txtservermgs.setText("Are you sure you want to inactive this server URL?");

                            }
                            txtYesAction.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    txtYesAction.setEnabled(false);
                                    txtNoAction.setEnabled(false);
                                    DataBaseAdapter objdatabaseadapter = null;
                                    String getstatus= "";
                                    if(status[position].equals("inactive")){
                                        getstatus = "active";
                                    }else{
                                        getstatus = "inactive";
                                    }
                                    //Order item details
                                    objdatabaseadapter = new DataBaseAdapter(context);
                                    objdatabaseadapter.open();
                                    String getresult="";
                                    int code;
                                    Boolean result=false;
                                    if(getstatus.equals("active")) {
                                        try {
                                            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                            StrictMode.setThreadPolicy(policy);
//                                            URL siteURL = new URL("http://" + serveripadd[position]);

                                            URL siteURL = new URL( serveripadd[position]);
                                            HttpURLConnection.setFollowRedirects(true);
                                            HttpURLConnection connection = (HttpURLConnection) siteURL.openConnection();
                                            connection.setRequestMethod("HEAD");
                                            connection.setConnectTimeout(3000);
                                            connection.connect();
                                            code = connection.getResponseCode();
                                            if (code == 200) {
                                                result = true;
                                            }
                                            connection.disconnect();
                                        } catch (Exception e) {
                                            // TODO Auto-generated catch block
                                            Log.d("AsyncSyncLLClick", e.getMessage());
                                            result = false;

                                        }

                                        if (!result) {
                                            Toast toast = Toast.makeText(getApplicationContext(), "Please enter valid URL", Toast.LENGTH_LONG);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
                                            txtYesAction.setEnabled(true);
                                            txtNoAction.setEnabled(true);
                                            serverpopup.dismiss();
                                            //  loading.dismiss();
                                            return;
                                        }
                                    }
                                    getresult=objdatabaseadapter.UpdateServerSettings(getstatus,serverid[position]);
                                    if(getresult.equals("success")){
                                        if(getstatus.equals("active")){
                                            mHolder.imgserverstatus.setImageResource(R.drawable.ic_checked);
                                            txtYesAction.setEnabled(true);
                                            txtNoAction.setEnabled(true);
                                            serverpopup.dismiss();
                                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                            builder.setMessage("Connected Successfully. Please open the application now.")
                                                    .setCancelable(false)
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            //do things
                                                            finish();
                                                            System.exit(0);
                                                        }
                                                    });
                                            AlertDialog alert = builder.create();
                                            alert.show();
                                        }else{
                                            mHolder.imgserverstatus.setImageResource(R.drawable.ic_backup);
                                            Toast toast = Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_LONG);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();

                                        }
                                        if(objdatabaseadapter!=null)
                                            objdatabaseadapter.close();
                                        //Get IP Address
                                        DataBaseAdapter objdatabaseadapter2 = null;
                                        try {
                                            //Order item details
                                            objdatabaseadapter2 = new DataBaseAdapter(context);
                                            objdatabaseadapter2.open();
                                            String getipadd="";
                                            getipadd=objdatabaseadapter2.GetServerSettingsDB();
                                            ipaddress = getipadd;
                                            preferenceMangr.pref_putString("ipaddress",getipadd);

                                        } catch (Exception e) {
                                            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                            mDbErrHelper.open();
                                            String geterrror = e.toString();
                                            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                            mDbErrHelper.close();
                                        } finally {
                                            if(objdatabaseadapter2!=null)
                                                objdatabaseadapter2.close();
                                        }
                                        txtYesAction.setEnabled(true);
                                        txtNoAction.setEnabled(true);
                                        serverpopup.dismiss();
                                        //  loading.dismiss();
                                        GetServerURLList();
                                    }else{
                                        Toast toast = Toast.makeText(getApplicationContext(),"Error in saving", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                        txtYesAction.setEnabled(true);
                                        txtNoAction.setEnabled(true);
                                        serverpopup.dismiss();
                                        // loading.dismiss();
                                        return;
                                    }
                                }
                            });
                            txtNoAction.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    serverpopup.dismiss();
                                }
                            });
                            serverpopup.setCanceledOnTouchOutside(false);
                            serverpopup.show();


                        } catch (Exception e) {
                            Toast toast = Toast.makeText(getApplicationContext(),"Error in saving", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                           // loading.dismiss();
                            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                            mDbErrHelper.open();
                            String geterrror = e.toString();
                            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            mDbErrHelper.close();
                        } finally {

                        }
                    }
                });
            } catch (Exception e) {
                Log.i("Route value", e.toString());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView txtserversno,txtserverurl,txturlname;
            ImageView imgserverstatus,imgserverurldelete,imgserverurledit;
            LinearLayout listLLayout;

        }

    }
    protected  class AsynconlineStatus extends
            AsyncTask<String, JSONObject, ArrayList<ScheduleDatas>> {
        ArrayList<ScheduleDatas> List = null;
        JSONObject jsonObj = null;
        @Override
        protected  ArrayList<ScheduleDatas> doInBackground(String... params) {
            RestAPI api = new RestAPI();
            try {
                JSONObject js_obj = new JSONObject();
                String createddate = "";
                String synctime = "";
                try {
                    DataBaseAdapter dbadapter = new DataBaseAdapter(getApplicationContext());
                    dbadapter.open();
                    //Get Current date
                    DataBaseAdapter objdatabaseadapter = null;
                    try{
                        objdatabaseadapter = new DataBaseAdapter(getApplicationContext());
                        objdatabaseadapter.open();
                        createddate = objdatabaseadapter.GenCreatedDateTime();
                        synctime = objdatabaseadapter.GetDateTime();
                    }catch (Exception e){
                        DataBaseAdapter mDbErrHelper = new DataBaseAdapter(getApplicationContext());
                        mDbErrHelper.open();
                        String geterrror = e.toString();
                        mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        mDbErrHelper.close();
                    }finally {
                        // this gets called even if there is an exception somewhere above
                        if (objdatabaseadapter != null)
                            objdatabaseadapter.close();
                    }
                    jsonObj =  api.SaveOnlineStatus(createddate,synctime,context);
                    dbadapter.close();
                }catch (Exception e) {
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(getApplicationContext());
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsynconlineStatusDOINBA", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(getApplicationContext());
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            return List;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<ScheduleDatas> result) {
            // TODO Auto-generated method stub
            try {
                if (result.size() >= 1) {


                }
            }catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsynconlineStatus_POST", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(getApplicationContext());
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }

        }
    }


    protected  class AsyncPricelist extends
            AsyncTask<String, JSONObject, ArrayList<ScheduleDatas>> {
        ArrayList<ScheduleDatas> List = null;
        JSONObject jsonObj = null;
        @Override
        protected  ArrayList<ScheduleDatas> doInBackground(String... params) {
            RestAPI api = new RestAPI();
            try {
                JSONObject js_obj = new JSONObject();
                try {

                    //itemn price list transaction
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncpricelist_scheduler.php",context);
                    //Log.d("------------------------------------------------------------------------------","");
                    Log.d("Scheduler Price list : ",jsonObj.toString());
                    if (isSuccessful(jsonObj)) {
                        DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
                        dataBaseAdapter.open();
                        dataBaseAdapter.syncpricelist_every_five_seconds(jsonObj);
                        dataBaseAdapter.close();
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "pricelist", preferenceMangr.pref_getString("getvancode"),"");
                    }
                }catch (Exception e) {
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(getApplicationContext());
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    Log.d("Scheduler Error :",e.toString());
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncPricelist_DOINBACK", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(getApplicationContext());
                mDbErrHelper.open();
                String geterrror = e.toString();
                //Toast.makeText(context, geterrror, Toast.LENGTH_SHORT).show();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            return List;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<ScheduleDatas> result) {
            // TODO Auto-generated method stub
            try {
                if (result.size() >= 1) {


                }
            }catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncPricelist_POST", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(getApplicationContext());
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }

        }
    }



    protected  class AsyncGetOrderStatus extends
            AsyncTask<String, JSONObject, ArrayList<ScheduleDatas>> {
        ArrayList<ScheduleDatas> List = null;
        JSONObject jsonObj = null;
        @Override
        protected  ArrayList<ScheduleDatas> doInBackground(String... params) {
            RestAPI api = new RestAPI();
            try {
                JSONObject js_obj = new JSONObject();
                try {

                    //itemn price list transaction
                    jsonObj = api.GetOrderStatus(preferenceMangr.pref_getString("deviceid"), preferenceMangr.pref_getString("getvancode") ,"syncorderstatus_scheduler.php");
                    if (isSuccessful(jsonObj)) {
                        DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
                        dataBaseAdapter.open();
                        dataBaseAdapter.syncorderstatus_every_five_mints(jsonObj);
                        dataBaseAdapter.close();
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "orderstatus", preferenceMangr.pref_getString("getvancode"),"");
                    }
                }catch (Exception e) {
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(getApplicationContext());
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncGetOrderStatusDOIN", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(getApplicationContext());
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            return List;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<ScheduleDatas> result) {
            // TODO Auto-generated method stub
            try {
                if (result.size() >= 1) {


                }
            }catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncGetOrderStatusPOST", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(getApplicationContext());
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }

        }
    }
    //Sha Encrypt
    static String sha256(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA256");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }


    //Checking internet connection
    public boolean isNetworkAvailable() {

        int code;
        Boolean result=false;
        try {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        URL siteURL = new URL(RestAPI.urlString);
        HttpURLConnection.setFollowRedirects(false);
        HttpURLConnection connection = (HttpURLConnection) siteURL.openConnection();
        connection.setRequestMethod("HEAD");
        connection.setConnectTimeout(3000);
        connection.connect();
        code = connection.getResponseCode();
        if (code == 200) {
            result=true;
        }
        connection.disconnect();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("AsyncSyncNET", e.getMessage());
            result=false;

        }
        return result;
    }
    //Get phone imei number
    public void GetIMEI(){
        //Get phone imei number
        try{
           /* TelephonyManager tm = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
            if (tm.getDeviceId().equals(null)) {
                deviceid = "000000";
            } else {
                deviceid = tm.getDeviceId();
            }*/
            deviceid = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            preferenceMangr.pref_putString("deviceid",deviceid);

            //Get IMEI Number
            String getimeinocount1  = GetIMEICount();
            if(! getimeinocount1.equals("0")) {
                //Get Van Name for Corresponding imei number
                GetVanNameForIMEI();
            }else{
                if(preferenceMangr.pref_getString("ipaddress").equals("")) {
                    Toast toast = Toast.makeText(getApplicationContext(),"Please enter server URL", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }else {
                    String gettablecount = GetTableCount();
                    if (gettablecount.equals("0")) {
                        callschedule = true;
                    } else {
                        callschedule = false;
                    }
                    try{
                        networkstate= isNetworkAvailable();
                        if(networkstate == true){
                            new AsyncSyncVanMaster().execute();
                        }else{
                            Toast toast = Toast.makeText(getApplicationContext(),"Please check internet connection", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            //finish();
                            return;
                        }
                    }catch(Exception e){
                        /*Toast toast = Toast.makeText(getApplicationContext(),"Please check internet connection", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();*/
                    }

                }


            }

        }  catch (Exception e){
            Toast toast = Toast.makeText(getApplicationContext(),"Please enable app permission", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            /*Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);*/
            Log.i("Imei no : ", e.toString());

        }
    }
    //Close Keyboard
    private void closeKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public  void GetVanDetailsFromServer(){
        networkstate = isNetworkAvailable();
        if (networkstate == true) {
            if(!preferenceMangr.pref_getString("ipaddress").equals("")) {
                //Get Van Name for Corresponding imei number
                callschedule = true;
                String getimeinocount = GetIMEICount();
                if (!getimeinocount.equals("0")) {
                    if (preferenceMangr.pref_getString("deviceid") != null && preferenceMangr.pref_getString("deviceid") != "") {
                        DataBaseAdapter objdatabaseadapter = null;
                        Cursor Cur = null;
                        objdatabaseadapter = new DataBaseAdapter(context);
                        objdatabaseadapter.open();
                        Cur = objdatabaseadapter.GetVanNameForIMEIDB(preferenceMangr.pref_getString("deviceid"));
                        getformatdate = objdatabaseadapter.GenCreatedDate();
                        getcurrentdatetime = objdatabaseadapter.GenCurrentCreatedDate();

                        preferenceMangr.pref_putString("getformatdate",getformatdate);
                        preferenceMangr.pref_putString("getcurrentdatetime",getcurrentdatetime);

                        if (objdatabaseadapter != null) {
                            objdatabaseadapter.close();
                        }

                        if (Cur.getCount() > 0) {
                            setpinvalue = Cur.getString(3);
                            txtvanname.setText(Cur.getString(1));
                            getvancode = Cur.getString(0);
                            getbusiness_type = Cur.getString(4);
                            getorderprint = Cur.getString(5);
                            getvanname = Cur.getString(1);

                            preferenceMangr.pref_putString("getvanname",Cur.getString(1));
                            preferenceMangr.pref_putString("getvancode",Cur.getString(0));
                            preferenceMangr.pref_putString("getbusiness_type",Cur.getString(4));
                            preferenceMangr.pref_putString("getorderprint",Cur.getString(5));
                        } else {
                            txtvanname.setText("");
                        }
                    } else {
                        GetIMEI();
                    }
                }
                new AsyncSyncVanMaster().execute();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(),"Please enter server URL", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return ;
            }
        }else{
            //Get IMEI Number
            String getimeinocount = GetIMEICount();
            if (!getimeinocount.equals("0")) {
                if (preferenceMangr.pref_getString("deviceid") != null && preferenceMangr.pref_getString("deviceid") != "") {
                    DataBaseAdapter objdatabaseadapter =null;
                    Cursor Cur = null;
                    objdatabaseadapter = new DataBaseAdapter(context);
                    objdatabaseadapter.open();
                    Cur = objdatabaseadapter.GetVanNameForIMEIDB(preferenceMangr.pref_getString("deviceid"));
                    getformatdate = objdatabaseadapter.GenCreatedDate();
                    getcurrentdatetime = objdatabaseadapter.GenCurrentCreatedDate();

                    preferenceMangr.pref_putString("getformatdate",getformatdate);
                    preferenceMangr.pref_putString("getcurrentdatetime",getcurrentdatetime);

                    if(objdatabaseadapter!=null){
                        objdatabaseadapter.close();
                    }

                    if (Cur.getCount() > 0) {
                        setpinvalue = Cur.getString(3);
                        txtvanname.setText(Cur.getString(1));
                        getvancode = Cur.getString(0);
                        getbusiness_type = Cur.getString(4);
                        getorderprint = Cur.getString(5);
                        getvanname = Cur.getString(1);

                        preferenceMangr.pref_putString("getvanname",Cur.getString(1));
                        preferenceMangr.pref_putString("getvancode",Cur.getString(0));
                        preferenceMangr.pref_putString("getbusiness_type",Cur.getString(4));
                        preferenceMangr.pref_putString("getorderprint",Cur.getString(5));

                    } else {
                        txtvanname.setText("");
                    }
                } else {
                    GetIMEI();
                }
            }
            else if(!preferenceMangr.pref_getString("ipaddress").equals("")) {
                Toast toast = Toast.makeText(getApplicationContext(),"Please check internet connection", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                // Toast.makeText(getApplicationContext(),"Please check internet connection",Toast.LENGTH_SHORT).show();
               // finish();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(),"Please enter server URL", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //Toast.makeText(getApplicationContext(),"Please enter server ipaddress",Toast.LENGTH_SHORT).show();
                return ;
            }

        }
    }
    //Get Van Name for Corresponding imei number
    public  void  GetVanNameForIMEI(){
        //Get phone imei number
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
           /* TelephonyManager tm = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
            if (tm.getDeviceId().equals(null)) {
                deviceid = "000000";
            } else {
                deviceid = tm.getDeviceId();
            }
*/
            deviceid = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            preferenceMangr.pref_putString("deviceid",deviceid);

            String gettablecount  = GetTableCount();
            if( gettablecount.equals("0")) {
                callschedule = true;
            }else{
                callschedule = false;
            }

            GetVanDetailsFromServer();

        }  catch (Exception e){
            Toast toast = Toast.makeText(getApplicationContext(),"True time not initiate..Please check internet connection", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
           // Toast.makeText(getApplicationContext(),"True time not initiate..Please check internet connection",Toast.LENGTH_SHORT).show();
            Log.i("GetVanNameForIMEI", e.toString());
        }
        finally {
            // this gets called even if there is an exception somewhere above
            if(objdatabaseadapter != null)
                objdatabaseadapter.close();
            if(Cur != null)
                Cur.close();
        }
    }

    //Check count for imei number is exists or not
    public  String  GetIMEICount(){
        //Get phone imei number
        DataBaseAdapter objdatabaseadapter = null;
        String getcount="0";
        try{
            if(preferenceMangr.pref_getString("deviceid") != null && preferenceMangr.pref_getString("deviceid") != ""){
                objdatabaseadapter = new DataBaseAdapter(context);
                objdatabaseadapter.open();
                getcount = objdatabaseadapter.GetIMEICountDB(preferenceMangr.pref_getString("deviceid"));
            }else{
                GetIMEI();
            }
        }  catch (Exception e){
            Log.i("GetVanNameForIMEI", e.toString());
        }
        finally {
            // this gets called even if there is an exception somewhere above
            if(objdatabaseadapter != null)
                objdatabaseadapter.close();

        }
        return getcount;
    }
    //Check count for imei number is exists or not
    public  String  GetTableCount(){
        //Get phone imei number
        DataBaseAdapter objdatabaseadapter = null;
        String getcount="0";
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            getcount = objdatabaseadapter.GetCountDB();
        }  catch (Exception e){
            Log.i("GetVanNameForIMEI", e.toString());
        }
        finally {
            // this gets called even if there is an exception somewhere above
            if(objdatabaseadapter != null)
                objdatabaseadapter.close();

        }
        return getcount;
    }
    @OnClick(R.id.btn_clear)
    public void onClear() {
        if (codeString.length() > 0) {
            codeString = removeLastChar(codeString);
            setDotImagesState();
        }

    }
    @OnClick({R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6,
            R.id.btn7, R.id.btn8, R.id.btn9})

    public void onClick(Button button) {
        getStringCode(button.getId());
        if (codeString.length() == MAX_LENGHT) {
            getpinvalue = codeString;
            if(preferenceMangr.pref_getString("ipaddress").equals("")){
                Toast toast = Toast.makeText(getApplicationContext(),"Please enter valid URL", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                if (codeString.length() > 0) {
                    codeString = removeAllChar(codeString);
                    setDotImagesState();
                }

                shakeAnimation();
                return;
            }
            if(preferenceMangr.pref_getString("deviceid") ==null  || preferenceMangr.pref_getString("deviceid") == "") {
                //Get Van Name for Corresponding imei number
                GetVanNameForIMEI();
            }
            //Get IMEI Number
            String getimeinocount  = GetIMEICount();
            if(getimeinocount.equals("0")) {
                if (codeString.length() > 0) {
                    codeString = removeAllChar(codeString);
                    setDotImagesState();
                }
                shakeAnimation();
                GetVanDetailsFromServer();
                /*networkstate = isNetworkAvailable();
                if (networkstate == true) {
                    //Get Van Name for Corresponding imei number
                    if(!ipaddress.equals("")) {
                        //Get Van Name for Corresponding imei number
                        new AsyncSyncVanMaster().execute();
                    }else{
                        Toast toast = Toast.makeText(getApplicationContext(),"Please enter server ipaddress", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        //Toast.makeText(getApplicationContext(),"Please enter server ipaddress",Toast.LENGTH_SHORT).show();
                        return ;
                    }
                }
               else {
                    Toast toast = Toast.makeText(getApplicationContext(),"You are not authorized to use this application", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    //Toast.makeText(getApplicationContext(), "You are not authorized to use this application", Toast.LENGTH_SHORT).show();
                    return;
                }*/
            }else {
                String getEncryptedPIN = null;
                    try {
                    getEncryptedPIN = sha256(getpinvalue);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                if (getEncryptedPIN.equals(setpinvalue)) {
                    getpin =setpinvalue;
                    preferenceMangr.pref_putString("getpin",setpinvalue);

                    if (codeString.length() > 0) {
                        codeString = removeAllChar(codeString);
                        setDotImagesState();
                    }

                    shakeAnimation();



                   // Toast.makeText(getApplicationContext(),"Welcome",Toast.LENGTH_SHORT).show();
                    networkstate = isNetworkAvailable();
                    if (networkstate == true) {
                        new AsyncCheckIMEI().execute();
                    }else{

                        DataBaseAdapter objdatabaseadapter = null;
                        String getschedulecount = "0";
                        String getschedulestatus = "";
                        try {
                            //Save Schdule Functionality
                            objdatabaseadapter = new DataBaseAdapter(context);
                            objdatabaseadapter.open();
                            getschedulecount = objdatabaseadapter.GetScheduleCountDB();
                            getfinanceyrcode = objdatabaseadapter.GetFinancialYrCode();

                            preferenceMangr.pref_putString("getfinanceyrcode",objdatabaseadapter.GetFinancialYrCode());
                            //Get General settings
                            getschedulestatus = objdatabaseadapter.GetScheduleStatusDB();
                        } catch (Exception e) {
                            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                            mDbErrHelper.open();
                            String geterrror = e.toString();
                            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            mDbErrHelper.close();
                        } finally {
                            objdatabaseadapter.close();
                        }
                        /*if (getschedulecount.equals("0")) {
                            if(getschedulestatus.equals("yes")) {
                                Intent i = new Intent(context, ScheduleActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }else{
                                Intent i = new Intent(context, MenuActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }
                        } else {
                            Intent i = new Intent(context, MenuActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        }*/

                        Intent i = new Intent(context, MenuActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);

                        Toast toast1 = Toast.makeText(getApplicationContext(),"Please check internet connection", Toast.LENGTH_LONG);
                        toast1.setGravity(Gravity.CENTER, 0, 0);
                        toast1.show();
                        //Toast.makeText(getApplicationContext(),"Please check internet connection",Toast.LENGTH_SHORT).show();
                    }


                } else {
                    if (codeString.length() > 0) {
                        //remove last character of code
                        codeString = removeAllChar(codeString);
                        setDotImagesState();
                    }
                    shakeAnimation();
                    Toast toast = Toast.makeText(getApplicationContext(),"Incorrect Password", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                   // Toast.makeText(getApplicationContext(),"Incorrect Password",Toast.LENGTH_SHORT).show();
                }
            }
        }else if (codeString.length() > MAX_LENGHT){
            //reset the input code
            codeString = "";
            getStringCode(button.getId());
        }
        setDotImagesState();
    }

    //Animate Dots
    private void shakeAnimation() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.vibrate_anim);
        findViewById(R.id.dot_layout).startAnimation(shake);
    }
    //Get button string code
    private void getStringCode(int buttonId) {
        switch (buttonId) {
            case R.id.btn0:
                codeString += "0";
                break;
            case R.id.btn1:
                codeString += "1";
                break;
            case R.id.btn2:
                codeString += "2";
                break;
            case R.id.btn3:
                codeString += "3";
                break;
            case R.id.btn4:
                codeString += "4";
                break;
            case R.id.btn5:
                codeString += "5";
                break;
            case R.id.btn6:
                codeString += "6";
                break;
            case R.id.btn7:
                codeString += "7";
                break;
            case R.id.btn8:
                codeString += "8";
                break;
            case R.id.btn9:
                codeString += "9";
                break;
            default:
                break;
        }
    }

    //setImagestate
    private void setDotImagesState() {
        for (int i = 0; i < codeString.length(); i++) {
            dots.get(i).setBackgroundResource(R.drawable.paswd_enable);
        }
        if (codeString.length()<4) {
            for (int j = codeString.length(); j<4; j++) {
                dots.get(j).setBackgroundResource(R.drawable.paswd_disable);
            }
        }
    }

    private String removeFullChar(String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        return s.substring(0, s.length()-4 );
    }
    //Remove all characters
    private String removeAllChar(String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        return s.substring(0, s.length()-codeString.length() );
    }
    //Remove only last character
    private String removeLastChar(String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        return s.substring(0, s.length()-1 );

    }


    /*************************SYNC ALL DETAILS*********************/

    protected  class AsyncSyncVanMaster extends
            AsyncTask<String, JSONObject, String> {
        String List = "Success";
        JSONObject jsonObj = null;
        ProgressDialog loading;

        @Override
        protected String doInBackground(String... params) {

            RestAPI api = new RestAPI();
            String result = "";
            DataBaseAdapter mDbErrHelper1 = new DataBaseAdapter(context);
            try {
                mDbErrHelper1.open();
                networkstate = isNetworkAvailable();
                if (networkstate == true) {
                    if (callschedule) {
                        //Get Van Master
                        jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"), "syncdeleteimeino.php",context);
                    }
                    //Get Van Master
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"), "syncvanmaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        mDbErrHelper1.syncvanmaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "vanmaster", "0", "");
                    }
                    else
                    {
                        List="0";
                    }
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncSyncVanDINB", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            } finally {
                // this gets called even if there is an exception somewhere above
                if(mDbErrHelper1 != null)
                    mDbErrHelper1.close();
            }
            return List;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(context, "Loading", "Please wait...", true, true);
            loading.setCancelable(false);
            loading.setCanceledOnTouchOutside(false);
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            try {
                loading.dismiss();
                /*if(result.equals("0")){
                    Toast toast = Toast.makeText(getApplicationContext(),"You are not authorized to use this application", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }*/
                if(callschedule){
                    if (codeString.length() > 0) {
                        //remove last character of code
                        codeString = removeAllChar(codeString);
                        setDotImagesState();
                    }
                    shakeAnimation();
                }
                //Get phone imei number
                DataBaseAdapter objdatabaseadapter = null;
                Cursor Cur=null;
                try{
                    //Get IMEI Number
                    String getimeinocount  = GetIMEICount();
                    if(! getimeinocount.equals("0")) {
                        if (preferenceMangr.pref_getString("deviceid") != null && preferenceMangr.pref_getString("deviceid") != "") {
                            objdatabaseadapter = new DataBaseAdapter(context);
                            objdatabaseadapter.open();
                            Cur = objdatabaseadapter.GetVanNameForIMEIDB(preferenceMangr.pref_getString("deviceid"));
                            getformatdate = objdatabaseadapter.GenCreatedDate();
                            getcurrentdatetime = objdatabaseadapter.GenCurrentCreatedDate();

                            preferenceMangr.pref_putString("getformatdate",getformatdate);
                            preferenceMangr.pref_putString("getcurrentdatetime",getcurrentdatetime);

                            if (Cur.getCount() > 0) {
                                setpinvalue = Cur.getString(3);
                                txtvanname.setText(Cur.getString(1));
                                getvancode = Cur.getString(0);
                                getvanname = Cur.getString(1);
                                getbusiness_type = Cur.getString(4);
                                getorderprint = Cur.getString(5);

                                preferenceMangr.pref_putString("getvanname",Cur.getString(1));
                                preferenceMangr.pref_putString("getvancode",Cur.getString(0));
                                preferenceMangr.pref_putString("getbusiness_type",Cur.getString(4));
                                preferenceMangr.pref_putString("getorderprint",Cur.getString(5));

                            } else {
                                txtvanname.setText("");
                            }
                        }
                    }else{
                        final Dialog ipadddialog = new Dialog(context);
                        ipadddialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        ipadddialog.setContentView(R.layout.deviceidinfnpopup);
                        TextView txt_information = (TextView) ipadddialog.findViewById(R.id.txt_information);
                        ImageView closepopup = (ImageView) ipadddialog.findViewById(R.id.closepopup);
                        txt_information.setTextIsSelectable(true);
                        Button btn_okay = (Button) ipadddialog.findViewById(R.id.btn_okay);
                        closepopup.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ipadddialog.dismiss();
                            }
                        });


                        btn_okay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ipadddialog.dismiss();

                                ClipboardManager myClipboard;
                                myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                                ClipData myClip;
                                //String text = "hello world";
                                myClip = ClipData.newPlainText("Device ID", preferenceMangr.pref_getString("deviceid"));
                                myClipboard.setPrimaryClip(myClip);
                                Toast.makeText(context,"Device ID Copied to ClipBoard",Toast.LENGTH_SHORT).show();

                                GetVanDetailsFromServer();
                            }
                        });
                        ipadddialog.setCanceledOnTouchOutside(false);
                        txt_information.setText("Your deviceid not registered . Please register " + preferenceMangr.pref_getString("deviceid") + " this  device id .");
                        ipadddialog.show();
                    }
                }  catch (Exception e){
                    Log.i("GetVanNameForIMEI", e.toString());
                }
                finally {
                    // this gets called even if there is an exception somewhere above
                    if(objdatabaseadapter != null)
                        objdatabaseadapter.close();
                    if(Cur != null)
                        Cur.close();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncSyncVanPOST", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        }


    }
    private boolean isSuccessful(JSONObject object) {
        boolean success = false;
        if (object != null) {
            try {
                String success1 = object.getString("success");
                if (success1.equals("1")) {
                    success = (object.getJSONArray("Value").length()) > 0;
                }
            } catch (JSONException e) {
                Log.d("JSON Error", e.getMessage());
            }
        }
        return success;
    }


    //check IMEI as valid
    protected class AsyncCheckIMEI extends
            AsyncTask<String, JSONObject,String> {
        JSONObject jsonObj = null;
        ProgressDialog loading;
        int code;
        @Override
        protected String doInBackground(String... params) {

            String result = "";
            try {
                RestAPI api = new RestAPI();
                networkstate = isNetworkAvailable();
                if (networkstate == true) {
                    jsonObj = api.CheckIMEI(preferenceMangr.pref_getString("deviceid"),"check_imei.php");
                }
                else{
                    result = "";
                }
                if(jsonObj!=null) {
                    if(jsonObj.has("success")) {
                        result = jsonObj.getString("success");
                    }
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncSyncCk_imei_DINB", e.getMessage());
                result="";

            }
            return result;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            loading = ProgressDialog.show(context, "Loading", "Please wait...", true);
            loading.setCancelable(false);
            loading.setCanceledOnTouchOutside(false);
        }

        protected void onPostExecute(final String result) {
            try {
                loading.dismiss();
                if(result.equals("1"))
                {
                    Toast toast = Toast.makeText(getApplicationContext(),"Welcome", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    new AsyncSyncAllDetails().execute();
                }
                else if(result.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Server not reachable", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    DeleteIMEIDetails();
                    Toast toast = Toast.makeText(getApplicationContext(),"You are not authorized to use this application", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
            } catch (Exception e) {
                Log.d("servererror",e.getMessage());
            }

        }


    }

    //Delete IMEI details
    public  String  DeleteIMEIDetails(){
        //Get phone imei number
        DataBaseAdapter objdatabaseadapter = null;
        String getcount="0";
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            objdatabaseadapter.deletevanmaster();
        }  catch (Exception e){
            Log.i("DeleteIMEIDetails", e.toString());
        }
        finally {
            // this gets called even if there is an exception somewhere above
            if(objdatabaseadapter != null)
                objdatabaseadapter.close();

        }
        return getcount;
    }

   //Sync all  datas
    protected  class AsyncSyncAllDetails extends
            AsyncTask<String, JSONObject, String> {
        String List = "Success";
        JSONObject jsonObj = null;
        ProgressDialog loading;

        @Override
        protected  String doInBackground(String... params) {

            RestAPI api = new RestAPI();
            String result = "";
            DataBaseAdapter dataBaseAdapter =null;
            try {
                dataBaseAdapter = new DataBaseAdapter(context);
                dataBaseAdapter.open();

                    networkstate = isNetworkAvailable();
                    if (networkstate == true) {

                        //General settings
                        jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"), "syncgeneralsettings.php",context);
                        if (isSuccessful(jsonObj)) {
                            dataBaseAdapter.syncgeneralsettings(jsonObj);
                            api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "generalsettings", preferenceMangr.pref_getString("getvancode"), "");
                        }

                        try{
                            AsyncScheduleDetails();
                        }catch (Exception e) {
                            // TODO Auto-generated catch block
                            Log.d("LoginACT_AsyncSync", e.getMessage());
                            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                            mDbErrHelper.open();
                            String geterrror = e.toString();
                            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            mDbErrHelper.close();
                        }

                        dataBaseAdapter = new DataBaseAdapter(context);
                        dataBaseAdapter.open();

                        String getschedulestatus = dataBaseAdapter.GetScheduleStatusDB();
                        if(getschedulestatus.equals("yes")){
                            //Sales Schedule
                            jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncsalesschedulemobile.php",context);
                            if (isSuccessful(jsonObj)) {
                                dataBaseAdapter.syncsalesschedulemobile(jsonObj);
                                api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"),"salesschedule",preferenceMangr.pref_getString("getvancode"),"");
                            }
                        }else{
                            //Sales Schedule portal
                            jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncsalesschedule.php",context);
                            if (isSuccessful(jsonObj)) {
                                dataBaseAdapter.syncsalesschedule(jsonObj);
                                api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"),"salesscheduleportal",preferenceMangr.pref_getString("getvancode"),"");
                            }
                        }
                        String schedulecode=preferenceMangr.pref_getString("getsalesschedulecode");
                        if(schedulecode.equals("") || schedulecode==null){
                            String getschedulecode = dataBaseAdapter.GetScheduleCode();
                            if(!getschedulecode.equals(null) && !getschedulecode.equals("null"))
                                preferenceMangr.pref_putString("getsalesschedulecode",getschedulecode);
                        }

                        /*****SYNCH ALL MASTER************/
                        //company master
                        jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"), "synccompanymaster.php",context);
                        if (isSuccessful(jsonObj)) {
                            dataBaseAdapter.synccompanymaster(jsonObj);
                            api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "companymaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                        }
                        //area master
                        jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"), "syncareamaster.php",context);
                        if (isSuccessful(jsonObj)) {
                            dataBaseAdapter.syncareamaster(jsonObj);
                            api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "areamaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                        }

                        //brand master
                        jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"), "syncbrandmaster.php",context);
                        if (isSuccessful(jsonObj)) {
                            dataBaseAdapter.syncbrandmaster(jsonObj);
                            api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "brandmaster",preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                        }

                        //currency
                        jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"), "synccurrency.php",context);
                        if (isSuccessful(jsonObj)) {
                            dataBaseAdapter.synccurrency(jsonObj);
                            api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "currency", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                        }

                        //city
                        jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"), "synccitymaster.php",context);
                        if (isSuccessful(jsonObj)) {
                            dataBaseAdapter.synccitymaster(jsonObj);
                            api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "citymaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                        }

                        //employee catergory
                        jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"), "syncemployeecategory.php",context);
                        if (isSuccessful(jsonObj)) {
                            dataBaseAdapter.syncemployeecategory(jsonObj);
                            api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "employeecategory", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                        }

                        //employee master
                        jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"), "syncemployeemaster.php",context);
                        if (isSuccessful(jsonObj)) {
                            dataBaseAdapter.syncemployeemaster(jsonObj);
                            api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "employeemaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                        }


                        //financial year
                        jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"), "syncfinancialyear.php",context);
                        if (isSuccessful(jsonObj)) {
                            dataBaseAdapter.syncfinancialyear(jsonObj);
                            api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "financialyear", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                        }

                        //route
                        jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"), "syncroute.php",context);
                        if (isSuccessful(jsonObj)) {
                            dataBaseAdapter.syncroute(jsonObj);
                            api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "route", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                        }

                        //route details
                        jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"), "syncroutedetails.php",context);
                        if (isSuccessful(jsonObj)) {
                            dataBaseAdapter.syncroutedetails(jsonObj);
                            api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "routedetails",preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                        }

                        //vehicle master
                        jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"), "syncvehiclemaster.php",context);
                        if (isSuccessful(jsonObj)) {
                            dataBaseAdapter.syncvehiclemaster(jsonObj);
                            api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "vehiclemaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                        }

                        //voucher settings
                        jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"), "syncvouchersettings.php",context);
                        if (isSuccessful(jsonObj)) {
                            dataBaseAdapter.syncvouchersettings(jsonObj);
                            api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "vouchersettings", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                        }

                        //area master
                        jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"), "syncstatemaster.php",context);
                        if (isSuccessful(jsonObj)) {
                            dataBaseAdapter.syncstatemaster(jsonObj);
                            api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "statemaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                        }

                    }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncSyncAllDetailsDINB", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            finally {
                dataBaseAdapter.close();
            }
            return List;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(context, "Synching Data", "Please wait...", true, true);
            loading.setCancelable(false);
            loading.setCanceledOnTouchOutside(false);
        }
        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            try {
                loading.dismiss();

                //Backupdb
                DataBaseAdapter mDbHelper1 = new DataBaseAdapter(context);
                mDbHelper1.open();
                String filepath = mDbHelper1.udfnBackupdb(context);
                mDbHelper1.close();

                DataBaseAdapter objdatabaseadapter = null;
                String getschedulecount = "0";
                String getschedulestatus = "";
                try {
                    //Save Schdule Functionality
                    objdatabaseadapter = new DataBaseAdapter(context);
                    objdatabaseadapter.open();
                    getschedulecount = objdatabaseadapter.GetScheduleCountDB();
                    getfinanceyrcode = objdatabaseadapter.GetFinancialYrCode();

                    preferenceMangr.pref_putString("getfinanceyrcode",objdatabaseadapter.GetFinancialYrCode());
                    //Get General settings
                    getschedulestatus = objdatabaseadapter.GetScheduleStatusDB();
                } catch (Exception e) {
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                } finally {
                    objdatabaseadapter.close();
                }
                /*if (getschedulecount.equals("0")) {
                    if (getschedulestatus.equals("yes")) {
                        Intent i = new Intent(context, ScheduleActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    } else {
                        Intent i = new Intent(context, MenuActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }
                } else {
                    Intent i = new Intent(context, MenuActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }*/

                Intent i = new Intent(context, MenuActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

            }catch (Exception e) {
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }

        }
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),"Press Back again to Exit.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            //Toast.makeText(this, "Press Back again to Exit.",  Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 10:
                    if ((grantResults.length > 0) && (grantResults[0] + grantResults[1]== PackageManager.PERMISSION_GRANTED)) {
                        //TODO
                        Toast.makeText(context,"Permission Granted",Toast.LENGTH_SHORT).show();
                        GetIMEI();
                    }else{
                        Toast.makeText(context,"Permission Denied",Toast.LENGTH_SHORT).show();
                        checkPermission();
                    }
                break;

            default:
                break;
        }
        /*if(requestCode==StorageRequestCode && grantResults[0] == PackageManager.PERMISSION_GRANTED){

        }else{
            Toast.makeText(context,"Permission Denied",Toast.LENGTH_SHORT).show();
        }*/
    }

    public void checkPermission(){
        if(ContextCompat.checkSelfPermission(context,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(LoginActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE}, StorageRequestCode);
        }else{
            GetIMEI();
        }
    }

    /*********SCHEDULE DETAILS*************/
    public void AsyncScheduleDetails(){
        ArrayList<ScheduleDatas> List = null;
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();
        String result = "";
        try {
            JSONObject js_obj = new JSONObject();
            try {
                DataBaseAdapter dbadapter = new DataBaseAdapter(context);
                dbadapter.open();
                Cursor mCur2 = dbadapter.GetTodayScheduleDatasDB();
                JSONArray js_array2 = new JSONArray();
                for (int i = 0; i < mCur2.getCount(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("autonum", mCur2.getString(0));
                    obj.put("refno", mCur2.getString(1));
                    obj.put("schedulecode", mCur2.getString(2));
                    obj.put("scheduledate", mCur2.getString(3));
                    obj.put("vancode", mCur2.getString(4));
                    obj.put("routecode", mCur2.getString(5));
                    obj.put("vehiclecode", mCur2.getString(6));
                    obj.put("employeecode", mCur2.getString(7));
                    obj.put("drivercode", mCur2.getString(8));
                    obj.put("helpername", mCur2.getString(9));
                    obj.put("tripadvance", mCur2.getString(10));
                    obj.put("startingkm", mCur2.getString(11));
                    obj.put("endingkm", mCur2.getString(12));
                    obj.put("createddate", mCur2.getString(13));
                    obj.put("updatedate", mCur2.getString(14));
                    obj.put("makerid", mCur2.getString(15));
                    obj.put("lunch_start_time", mCur2.getString(17));
                    obj.put("lunch_end_time", mCur2.getString(18));
                    preferenceMangr.pref_putString("getsalesschedulecode",mCur2.getString(2));
                    js_array2.put(obj);
                    mCur2.moveToNext();
                }
                js_obj.put("JSonObject", js_array2);

                jsonObj =  api.ScheduleDetails(js_obj.toString(),context);
                //Call Json parser functionality
                JSONParser parser = new JSONParser();
                //parse the json object to boolean
                List = parser.parseScheduleDataList(jsonObj);
                dbadapter.close();
                if (List.size() >= 1) {
                    if(List.get(0).ScheduleCode.length>0){
                        for(int j=0;j<List.get(0).ScheduleCode.length;j++){
                            DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
                            dataBaseAdapter.open();
                            dataBaseAdapter.UpdateScheduleFlag(List.get(0).ScheduleCode[j]);
                            dataBaseAdapter.close();
                        }
                    }

                }
            }
            catch (Exception e)
            {
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("AsyncScheduleDetails", e.getMessage());
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }
}
