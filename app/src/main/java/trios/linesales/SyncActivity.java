package trios.linesales;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.BuildCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class SyncActivity extends AppCompatActivity {
    LinearLayout sync_allLL,sync_itemll,sync_customerLL,sync_scheduleLL,sync_transactionLL,sync_salescashcloshLL,
            sync_transportLL,sync_settingsLL,sync_schemeLL;
    boolean clickitem = true,clickcustomer = true,clickschedule = true;
    TextView syncitemHead,synccustomerHead,syncscheduleHead;

    ImageView syncgoback,synclogout;

    Context context;
    boolean networkstate;
    public static final String UPLOAD_URL = RestAPI.urlString+"syncimage.php";
    public static PreferenceMangr preferenceMangr=null;
    ArrayList<Object> downloadUPIData;

    DropBoxAsyncResponseListener dropboxlistener;

    public long downloadFileID=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);


        context =this;
        sync_allLL = (LinearLayout) findViewById(R.id.syncallLL);
        sync_itemll = (LinearLayout) findViewById(R.id.syncitemLL);
        sync_customerLL = (LinearLayout) findViewById(R.id.synccustomerLL);
        sync_scheduleLL = (LinearLayout) findViewById(R.id.syncscheduleLL);
        sync_transactionLL = (LinearLayout) findViewById(R.id.synctransacionLL);
        sync_salescashcloshLL = (LinearLayout) findViewById(R.id.syncsalescashcloseLL);
        /*sync_transportLL = (LinearLayout) findViewById(R.id.synctransportLL);
        sync_settingsLL = (LinearLayout) findViewById(R.id.syncsettingsLL);*/
        syncgoback = (ImageView) findViewById(R.id.syncgoback);
        synclogout = (ImageView) findViewById(R.id.synclogout);
        sync_schemeLL = (LinearLayout) findViewById(R.id.syncschemeLL);


        syncitemHead = (TextView) findViewById(R.id.syncitemHead);
        synccustomerHead = (TextView) findViewById(R.id.synccustomerHead);
        syncscheduleHead = (TextView) findViewById(R.id.syncscheduleHead);

        try {
            preferenceMangr = new PreferenceMangr(context);
        }catch (Exception e){
            Log.d("Preference Manager : ",e.toString());
        }

        final Animation aniFade = AnimationUtils.loadAnimation(context,R.anim.slide_up);

        this.dropboxlistener = new DropBoxAsyncResponseListener() {



            @Override
            public void onAsyncTaskResponseReceived(Object response, int requestType) {
                onReceiveAsyncResult(response, requestType);
            }
        };

        sync_allLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                networkstate = isNetworkAvailable();
                if (networkstate == true) {
                    new AsyncServer().execute();
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Please check internet connection", Toast.LENGTH_LONG);
                    //toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                    //Toast.makeText(getApplicationContext(),"Please check internet connection",Toast.LENGTH_SHORT).show();
                }
            }
        });


        sync_itemll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                networkstate = isNetworkAvailable();
                if (networkstate == true) {
                    new AsyncSyncItemandPriceDetails().execute();
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Please check internet connection", Toast.LENGTH_LONG);
                    //toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                    //Toast.makeText(getApplicationContext(),"Please check internet connection",Toast.LENGTH_SHORT).show();
                }
            }
        });
        sync_customerLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                networkstate = isNetworkAvailable();
                if (networkstate == true) {
                    new AsyncSyncCustomerDetails().execute();
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Please check internet connection", Toast.LENGTH_LONG);
                    //toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                    //Toast.makeText(getApplicationContext(),"Please check internet connection",Toast.LENGTH_SHORT).show();
                }
            }
        });


        sync_scheduleLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                networkstate = isNetworkAvailable();
                if (networkstate == true) {
                    new AsyncSyncScheduleandVanstockDetails().execute();
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Please check internet connection", Toast.LENGTH_LONG);
                    //toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                    //Toast.makeText(getApplicationContext(),"Please check internet connection",Toast.LENGTH_SHORT).show();
                }
            }
        });
        sync_transactionLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                networkstate = isNetworkAvailable();
                //networkstate = true;
                if (networkstate == true) {
                    new AsyncSyncTransactionDetails().execute();
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Please check internet connection", Toast.LENGTH_LONG);
                    //toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                    //Toast.makeText(getApplicationContext(),"Please check internet connection",Toast.LENGTH_SHORT).show();
                }
            }
        });
        sync_schemeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                networkstate = isNetworkAvailable();
                //networkstate = true;
                if (networkstate == true) {
                    new AsyncSchemeDetails().execute();
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Please check internet connection", Toast.LENGTH_LONG);
                    //toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                    //Toast.makeText(getApplicationContext(),"Please check internet connection",Toast.LENGTH_SHORT).show();
                }
            }
        });
        sync_salescashcloshLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                networkstate = isNetworkAvailable();
                if (networkstate == true) {
                    new AsyncSyncSalesCashCloseDetails().execute();
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Please check internet connection", Toast.LENGTH_LONG);
                    //toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                    //Toast.makeText(getApplicationContext(),"Please check internet connection",Toast.LENGTH_SHORT).show();
                }
            }
        });

        syncgoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack(null);
            }
        });
        synclogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirmation");
                builder.setIcon(R.mipmap.ic_van);
                builder.setMessage("Are you sure you want to logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(context, LoginActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(i);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

    }

    protected class AsyncServer extends
            AsyncTask<String, JSONObject,String> {
        JSONObject jsonObj = null;
        ProgressDialog loading;
        int code;
        @Override
        protected String doInBackground(String... params) {

            String result = "";
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                URL siteURL = new URL(RestAPI.urlString);
                HttpURLConnection connection = (HttpURLConnection) siteURL.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10*3000);
                connection.connect();
                code = connection.getResponseCode();
                if (code == 200) {
                    result="success";
                }
                connection.disconnect();

            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncSync", e.getMessage());
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
                if(result.equals("success"))
                {
                    new AsyncCheckIMEI().execute();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Server not reachable", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (Exception e) {
                Log.d("servererror",e.getMessage());
            }

        }


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
                Log.d("AsyncSync", e.getMessage());
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
                    //toast.setGravity(Gravity.CENTER, 0, 0);
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

    public boolean isNetworkAvailable() {
        /*ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();*/
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
            Log.d("AsyncSync", e.getMessage());
            result=false;

        }
        return result;
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
    //Sync all  datas
    protected  class AsyncSyncAllDetails extends
            AsyncTask<String, JSONObject, String> {
        String List = "Success";
        JSONObject jsonObj = null;
        ProgressDialog loading;

        @SuppressLint("WrongThread")
        @Override
        protected  String doInBackground(String... params) {

            RestAPI api = new RestAPI();
            String result = "";
            DataBaseAdapter dataBaseAdapter =null;
            String deviceid = preferenceMangr.pref_getString("deviceid");
            try {
                dataBaseAdapter = new DataBaseAdapter(context);
                dataBaseAdapter.open();
                networkstate = isNetworkAvailable();
                if (networkstate == true) {
                    ////if(BuildConfig.DEBUG)
                        Log.w("---> Sync Activity : "," Sync All Started");
                    //Get Van Master
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Van master");
                    jsonObj = api.GetAllDetails(deviceid, "syncvanmaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncvanmaster(jsonObj);
                        api.udfnSyncDetails(deviceid, "vanmaster", "0", "");
                    }
                    //General settings
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Generalsettings");
                    jsonObj = api.GetAllDetails(deviceid,"syncgeneralsettings.php",context);

                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncgeneralsettings(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"),"generalsettings",preferenceMangr.pref_getString("getvancode"),"");
                    }
                    networkstate = isNetworkAvailable();
                    if (networkstate == true) {
                        try{
                            AsyncScheduleDetails();
                            AsyncCashReportDetails();
                            AsyncCloseCashDetails();
                            AsyncCustomerDetails();
                            AsyncExpenseDetails();
                            AsyncCancelExpenseDetails();
                            AsyncOrderDetails();
                            AsyncSalesDetails();
                            AsyncUpdateSalesReceiptDetails();
                            AsyncCloseSalesDetails();
                            AsyncSalesCancelDetails();
                            AsyncSalesReturnDetails();
                            AsyncSalesReturnCancelDetails();
                            AsyncReceiptDetails();
                            AsyncCancelReceiptDetails();
                            AsyncNilStockDetails();
                            AsyncSalesOrderDetails();
                            AsyncSalesOrderCancelDetails();
                            AsyncNotPurchasedDetails();

                        }catch (Exception e) {
                            // TODO Auto-generated catch block
                            Log.e("AsyncSyncAllDetails DIB", e.getMessage());
                            if(dataBaseAdapter != null)
                                dataBaseAdapter.close();
                            /*DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                            mDbErrHelper.open();
                            String geterrror = e.toString();
                            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            mDbErrHelper.close();*/
                            InsertError(e.toString(),"SYNC ALL Local DB to Server Sync");
                        }
                    }

                    /**************DELETE LAST DAY TRANSACTIONS IN SQL LITE***********/
                    dataBaseAdapter.open();

                    dataBaseAdapter.DeleteExpensesDays();
                    dataBaseAdapter.DeleteorderDetails();
                    dataBaseAdapter.DeleteReceiptDays();
                    dataBaseAdapter.DeleteSalesDays();
                    dataBaseAdapter.DeleteSalesItemDetailsDays();
                    dataBaseAdapter.DeleteSalesReturnDays();
                    dataBaseAdapter.DeleteSalesReturnItemDetailsDays();
                    dataBaseAdapter.DeleteSalesSchedule();
                    dataBaseAdapter.DeleteErrorLogDays();

                    /**************END DELETE LAST DAY TRANSACTIONS IN SQL LITE***********/

                    //Get General settings
                    String getschedulestatus = dataBaseAdapter.GetScheduleStatusDB();
                    if(getschedulestatus.equals("yes")){
                        //Sales Schedule
                        ////if(BuildConfig.DEBUG)
                            Log.w("Sync Activity : "," Sync All : Salesschedule");
                        jsonObj = api.GetAllDetails(deviceid,"syncsalesschedulemobile.php",context);
                        if (isSuccessful(jsonObj)) {
                            dataBaseAdapter.syncsalesschedulemobile(jsonObj);
                            api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"),"salesschedule",preferenceMangr.pref_getString("getvancode"),"");
                        }
                    }else{
                        //Sales Schedule portal
                        ////if(BuildConfig.DEBUG)
                            Log.w("Sync Activity : "," Sync All : Salesschedule portal");
                        jsonObj = api.GetAllDetails(deviceid,"syncsalesschedule.php",context);
                        if (isSuccessful(jsonObj)) {
                            dataBaseAdapter.syncsalesschedule(jsonObj);
                            api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"),"salesscheduleportal",preferenceMangr.pref_getString("getvancode"),"");
                        }
                    }
                    String schedulecode=preferenceMangr.pref_getString("getsalesschedulecode");
                    if(schedulecode.equals("") || schedulecode==null) {
                        String getschedulecode = dataBaseAdapter.GetScheduleCode();
                        if (!getschedulecode.equals(null) && !getschedulecode.equals("null"))
                            preferenceMangr.pref_putString("getsalesschedulecode", getschedulecode);
                    }
                    /*****SYNCH ALL MASTER************/
                    //company master
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Company master");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"synccompanymaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.synccompanymaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "companymaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : UPI Vender Details");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"), "syncupivendermaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncupivendermaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "upivendermaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Company Vender Master");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"), "synccompanyvenderdetails.php",context);
                    //if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.synccompanyvenderdetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "companyvendermaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));

                    //}

                    //area master
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Area master");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncareamaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncareamaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "areamaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //brand master
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Brand master");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncbrandmaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncbrandmaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "brandmaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //currency
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Currency");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"synccurrency.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.synccurrency(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "currency", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //Receipt remarks
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Receipt Remarks");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncreceiptremarks.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncreceiptremarks(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "receiptremarks", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }
                    //tax
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Tax");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"synctax.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.synctax(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "tax", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }
                    //Bill type
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Bill type");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncbilltype.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncbilltype(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "billtype", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }
                    //city
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : City Master");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"synccitymaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.synccitymaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "citymaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //employee catergory
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Employee category");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncemployeecategory.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncemployeecategory(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "employeecategory", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //employee master
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Employee master");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncemployeemaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncemployeemaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "employeemaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    Cursor getschedulelist = dataBaseAdapter.GetScheduleDB();
                    if(getschedulelist.getCount() >0){
                        for(int i=0;i<getschedulelist.getCount();i++) {
                            MenuActivity.getroutecode = getschedulelist.getString(1);
                            preferenceMangr.pref_putString("getroutecode",getschedulelist.getString(1));
                        }
                    }
                    // customer
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Customer");
                    jsonObj = api.GetCustomerDetails(preferenceMangr.pref_getString("deviceid"),preferenceMangr.pref_getString("getroutecode"),"synccustomer.php");
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.synccustomer(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "customer", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    // cashnotpaiddetails
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Cash not paid details");
                    jsonObj = api.GetCashNotPaidDetails(preferenceMangr.pref_getString("deviceid"),preferenceMangr.pref_getString("getvancode"),preferenceMangr.pref_getString("getroutecode"),"synccashnotpaiddetails.php");
                    dataBaseAdapter.DeleteCashNotPaid();
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.synccashnotpaiddetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "cashnotpaiddetails", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }



                    //expenses head
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Expenses head");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncexpenseshead.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncexpenseshead(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "expenseshead", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //financial year
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Financialyear");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncfinancialyear.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncfinancialyear(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "financialyear", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //general settings
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : General settings");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncgeneralsettings.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncgeneralsettings(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "generalsettings", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //itemgroup master
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Item group master");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncitemgroupmaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncitemgroupmaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "itemgroupmaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //item master
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Item master");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncitemmaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncitemmaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "itemmaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //itemn price list transaction
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Item pricelist transaction");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncitempricelisttransaction.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncitempricelisttransaction(jsonObj);

                        /*Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat mformat= new SimpleDateFormat("dd-MM-yyyy h:mm a");
                        MenuActivity.pricelistlastsyncdate = mformat.format(calendar.getTime());*/
                        //Toast.makeText(context,pricelistlastsyncdate,Toast.LENGTH_SHORT).show();

                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "itempricelisttransaction", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }


                    //item subgroup master
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Item subgroup master");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncitemsubgroupmaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncitemsubgroupmaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "itemsubgroupmaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //route
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Route");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncroute.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncroute(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "route", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //route details
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Route details");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncroutedetails.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncroutedetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "routedetails", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }
                    //transport mode
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Transport mode");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"synctransportmode.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.synctransportmode(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "transportmode", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //transport
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Transport");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"synctransport.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.synctransport(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "transport", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //transportareamapping
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Transport city mapping");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"synctransportcitymapping.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.synctransportareamapping(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "transportcitymapping", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //scheme
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Scheme");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncscheme.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncscheme(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "scheme", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //scheme item details
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Scheme item details");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncschemeitemdetails.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncschemeitemdetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "schemeitemdetails", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //scheme rate details
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Scheme rate details");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncschemeratedetails.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncschemeratedetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "schemeratedetails", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //unit master
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Unit master");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncunitmaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncunitmaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "unitmaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //van stock
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Van stock");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncvanstock.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncvanstocktransaction(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "vanstock", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //vehicle master
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Vehicle master");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncvehiclemaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncvehiclemaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "vehiclemaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //voucher settings
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Voucher settings");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncvouchersettings.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncvouchersettings(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "vouchersettings", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //expenses
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Expenses");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncexpenses.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncexpensesmaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "expenses", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //cashclose
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Cash close");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"synccashclose.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.synccashclose(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "cashclose", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //sales close
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Sales close");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncsalesclose.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncsalesclose(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "salesclose", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //cashreport
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Cash report");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"synccashreport.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.synccashreport(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "cashreport", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //denomination
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Denomination");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncdenomination.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncdenomination(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "denomination", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //receipt
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Receipt");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncreceipt.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncreceipt(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "receipt", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //order details
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Order details");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncorderdetails.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncorderdetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "orderdetails", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //Sales
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Sales");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncsales.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncsales(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "sales", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //Salesitemdetails
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Sales item details");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncsalesitemdetails.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncsalesitemdetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "salesitemdetails", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //Sales order
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Sales order");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncsalesorder.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncsalesorder(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "salesorder", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }
                    //Sales order itemdetails
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Sales order itemdetails");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncsalesorderitemdetails.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncsalesorderitemdetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "salesorderitemdetails", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //Salesreturn
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Sales return");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncsalesreturn.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncsalesreturn(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "salesreturn", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }


                    //Salesretrunitemdetails
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Sales return item details");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncsalesreturnitemdetails.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncsalesreturnitemdetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "salesreturnitemdetails", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //nilstock
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Nil stock");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncnilstock.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncnilstock(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "nilstock", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }


                    LoginActivity.getfinanceyrcode = dataBaseAdapter.GetFinancialYrCode();
                    preferenceMangr.pref_putString("getfinanceyrcode",dataBaseAdapter.GetFinancialYrCode());

                    //maxrefno
                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : Max refno");
                    jsonObj = api.GetMaxCode(preferenceMangr.pref_getString("deviceid"),preferenceMangr.pref_getString("getfinanceyrcode"),"syncmaxrefno.php");
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncmaxrefno(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "maxrefno", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

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

                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : State Master");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"), "syncstatemaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncstatemaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "statemaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    ////if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync All : UPI Vender Details");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"), "syncupivendermaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncupivendermaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "upivendermaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }


                    if(ScheduleActivity.isFromschedule.equals("yes")) {
                        jsonObj = api.GetscheduleDetails(preferenceMangr.pref_getString("deviceid"), ScheduleActivity.scheduledate.getText().toString(), "syncdeliverynotescheduledetails.php", context);
                        if (isSuccessful(jsonObj)) {
                            syncscheduledetails(jsonObj);
                        }
                    }
                    // sync schedule eway
//
                    jsonObj = api.GetscheduleDetails(preferenceMangr.pref_getString("deviceid"),dataBaseAdapter.GenCurrentCreatedDate(),"syncdeliverynotescheduledetails.php", context);
                    if (isSuccessful(jsonObj)) {
                        syncscheduleewaydetails(jsonObj);
                    }
                    //Not Purchased

                    jsonObj = api.GetNotPurchasedDetails(preferenceMangr.pref_getString(Constants.KEY_DEVICEID),"syncnotpurchaseddetails.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncnotpurchaseddetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString(Constants.KEY_DEVICEID), "notpurchased", preferenceMangr.pref_getString(Constants.KEY_GETVANCODE), preferenceMangr.pref_getString(Constants.KEY_GET_SCHEDULE_SCHEDULECODE));
                    }

                    //Not purchased remarks
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString(Constants.KEY_DEVICEID),"syncnotpurchasedremarks.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncNotPurchasedRemarks(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString(Constants.KEY_DEVICEID), "receiptremarks", preferenceMangr.pref_getString(Constants.KEY_GETVANCODE), preferenceMangr.pref_getString(Constants.KEY_GET_SCHEDULE_SCHEDULECODE));
                    }
                    new UploadImage().execute();

                    ////if(BuildConfig.DEBUG)
                        Log.w("---> Sync Activity : "," Sync All Completed");
                }


            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.e("AsyncSyncAllDetails DIB", e.getMessage());
                if(dataBaseAdapter != null)
                    dataBaseAdapter.close();
                InsertError(e.toString(), "SYNC ALL DIB");
            }
            finally {
                if(dataBaseAdapter != null)
                    dataBaseAdapter.close();
            }
            return List;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(context, "Synching All Data", "Please wait...", true, true);
            loading.setCancelable(false);
            loading.setCanceledOnTouchOutside(false);
        }
        @SuppressLint("LongLogTag")
        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            //DataBaseAdapter mDbHelper1 = null;
            DataBaseAdapter objdatabaseadapter =  new DataBaseAdapter(context);
            objdatabaseadapter.open();
            try{
                //Backupdb
                String filepath = objdatabaseadapter.udfnBackupdb(context);
                objdatabaseadapter.close();
            }catch (Exception e){
                Log.d("Exception in DB_Backup ", e.getLocalizedMessage());
                if(objdatabaseadapter != null)
                    objdatabaseadapter.close();
            }finally {
                if(objdatabaseadapter != null)
                    objdatabaseadapter.close();
            }


            try {
                /*Calendar calendar = Calendar.getInstance();
                SimpleDateFormat mformat= new SimpleDateFormat("dd-MM-yyyy h:mm a");
                pricelistlastsyncdate = mformat.format(calendar.getTime());
                Toast.makeText(context,pricelistlastsyncdate,Toast.LENGTH_SHORT).show();*/

                String getschedulecount = "0";
                String getschedulestatus = "";
                try {
                    //Save Schdule Functionality
                    objdatabaseadapter.open();

                    LoginActivity.getfinanceyrcode = objdatabaseadapter.GetFinancialYrCode();
                    preferenceMangr.pref_putString("getfinanceyrcode",objdatabaseadapter.GetFinancialYrCode());
                    Cursor getschedulelist = objdatabaseadapter.GetScheduleDB();
                    if(getschedulelist.getCount() >0){
                        for(int i=0;i<getschedulelist.getCount();i++){
                            MenuActivity.getschedulecode = getschedulelist.getString(0);
                            MenuActivity.getroutecode = getschedulelist.getString(1);
                            MenuActivity.getroutename = getschedulelist.getString(4);
                            MenuActivity.gettripadvance =  getschedulelist.getString(3);
                            MenuActivity.getroutenametamil =  getschedulelist.getString(4);
                            MenuActivity.getcapacity = getschedulelist.getString(5);

                            preferenceMangr.pref_putString("getschedulecode",getschedulelist.getString(0));
                            preferenceMangr.pref_putString("getroutecode",getschedulelist.getString(1));
                            preferenceMangr.pref_putString("getroutename",getschedulelist.getString(4));
                            preferenceMangr.pref_putString("getcapacity",getschedulelist.getString(5));
                        }
                        //Get cash close Count
                        MenuActivity.getcashclosecount = objdatabaseadapter.GetCashClose(preferenceMangr.pref_getString("getschedulecode"));
                        preferenceMangr.pref_putString("getcashclosecount",objdatabaseadapter.GetCashClose(preferenceMangr.pref_getString("getschedulecode")));

                        ScheduleActivity.getcashclosecount =  objdatabaseadapter.GetCashClose(preferenceMangr.pref_getString("getschedulecode"));
                        preferenceMangr.pref_putString("schedule_getcashclosecount",objdatabaseadapter.GetCashClose(preferenceMangr.pref_getString("getschedulecode")));

                        //Get sales close Count
                        MenuActivity.getdenominationcount = objdatabaseadapter.GetDenominationCount(preferenceMangr.pref_getString("getschedulecode"));

                        MenuActivity.getsalesclosecount = objdatabaseadapter.GetSalesClose(preferenceMangr.pref_getString("getschedulecode"));
                        preferenceMangr.pref_putString("getsalesclosecount",objdatabaseadapter.GetSalesClose(preferenceMangr.pref_getString("getschedulecode")));

                        ScheduleActivity.getsalesclosecount =  objdatabaseadapter.GetSalesClose(preferenceMangr.pref_getString("getschedulecode"));
                        preferenceMangr.pref_putString("schedule_getsalesclosecount",objdatabaseadapter.GetSalesClose(preferenceMangr.pref_getString("getschedulecode")));

                        //GetWish messsgae
                        MenuActivity.getwishmsg = objdatabaseadapter.GetWishmsg();
                        preferenceMangr.pref_putString("getwishmsg",objdatabaseadapter.GetWishmsg());
                    }
                    Cursor Cur = objdatabaseadapter.GetVanNameForIMEIDB(preferenceMangr.pref_getString("deviceid"));
                    if (Cur.getCount() > 0) {
                        LoginActivity.getvancode = Cur.getString(0);
                        LoginActivity.getbusiness_type = Cur.getString(4);
                        LoginActivity.getorderprint = Cur.getString(5);
                        LoginActivity.getvanname = Cur.getString(1);

                        preferenceMangr.pref_putString("getvanname",Cur.getString(1));
                        preferenceMangr.pref_putString("getvancode",Cur.getString(0));
                        preferenceMangr.pref_putString("getbusiness_type",Cur.getString(4));
                        preferenceMangr.pref_putString("getorderprint",Cur.getString(5));
                    }
                } catch (Exception e) {
                    if(objdatabaseadapter != null)
                        objdatabaseadapter.close();
                    /*DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();*/
                    InsertError(e.toString(),"SYNC ALL Post");
                } finally {
                    if(objdatabaseadapter != null)
                        objdatabaseadapter.close();
                }
                if(!preferenceMangr.pref_getString("getsalesclosecount").equals("0") && !preferenceMangr.pref_getString("getsalesclosecount").equals("null") &&
                        !preferenceMangr.pref_getString("getsalesclosecount").equals("") && !preferenceMangr.pref_getString("getsalesclosecount").equals(null)){
                    MenuActivity.OrderForm.setVisibility(View.VISIBLE);
                }else{
                    MenuActivity.OrderForm.setVisibility(View.GONE);
                }

            }catch (Exception e) {
                Log.e("AsyncSyncAllDetails Post", e.getMessage());
                if(objdatabaseadapter != null)
                    objdatabaseadapter.close();
                InsertError(e.toString(),"SYNC ALL Post");
            }
            finally {
                if(objdatabaseadapter != null)
                    objdatabaseadapter.close();
            }

            //DataBaseAdapter objdbclearadapter = null;
            try{
                objdatabaseadapter.open();
                objdatabaseadapter.ClearLocalDB();
            }catch (Exception e){
                Log.e("AsyncSyncAllDetails Post DB Clear : " , e.getLocalizedMessage());
                if(objdatabaseadapter != null)
                    objdatabaseadapter.close();
                InsertError(e.toString(),"SYNC ALL Post Local DB Clear");
            }
            finally{
                if(objdatabaseadapter!=null){
                    objdatabaseadapter.close();
                }
            }
            try {
                objdatabaseadapter.open();
                networkstate = isNetworkAvailable();
                if (networkstate == true) {


                    Cursor mCur2 = objdatabaseadapter.GetCompanyVenderDetails();
                    /*if (mCur2 != null && mCur2.getCount() > 0){
                        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator + Constants.FOLDER_UPI_IMAGES);
                        if (file.exists()) {
                            File[] existingFiles = file.listFiles();
                            if (existingFiles != null && existingFiles.length > 0) {
                                for (int i=0; i<existingFiles.length; i++) {
                                    existingFiles[i].delete();
                                }
                            }
                        }
                    }*/
                    for (int i = 0; i < mCur2.getCount(); i++) {
                        downloadUPIData = new ArrayList<>();
                        downloadUPIData.add(Constants.KEY_INDEX_0,mCur2.getString(0));
                        downloadUPIData.add(Constants.KEY_INDEX_1,mCur2.getString(1));
                        downloadUPIData.add(Constants.KEY_INDEX_2,mCur2.getString(2));

                        AsyncTaskClass asyncTaskClass = new AsyncTaskClass(context,dropboxlistener,downloadUPIData,Constants.DOWNLOAD_VENDER_IMAGE);
                        asyncTaskClass.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                        mCur2.moveToNext();
                    }

                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Please check internet connection", Toast.LENGTH_LONG);
                    //toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }catch (Exception e){
                Log.e("AsyncSyncAllDetails Download UPI Image : " , e.getLocalizedMessage());
                if(objdatabaseadapter != null)
                    objdatabaseadapter.close();
                InsertError(e.toString(),"SYNC ALL Post Download UPI Image");
            }
            finally{
                if(objdatabaseadapter!=null){
                    objdatabaseadapter.close();
                }
            }
            loading.dismiss();
        }
    }
    public void  syncscheduledetails(JSONObject object){
        if(object!=null){
            JSONArray json_category=null;
            try{
                json_category = object.optJSONArray("Value");
                if(json_category != null && json_category.length()>0){

                    for(int i=0;i<json_category.length();i++) {
                        JSONObject obj = (JSONObject) json_category.get(i);
                        ScheduleActivity.btnSaveSchedule.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        ScheduleActivity.btnSaveSchedule.setTextColor(getResources().getColor(R.color.white));
                        ScheduleActivity.txtdrivername.setText(obj.getString("drivername"));
                        ScheduleActivity.getscheduledrivercode = obj.getString("drivercode");

                        ScheduleActivity.txtroute.setText(obj.getString("routename"));
                        ScheduleActivity.getscheduleroutecode=obj.getString("routecode");
                        ScheduleActivity.txtvechiclename.setText(obj.getString("vehiclename"));
                        ScheduleActivity.getschedulevehiclecode = obj.getString("vehiclecode");
                        ScheduleActivity.txtsalesrep.setText(obj.getString("salespersonname"));
                        ScheduleActivity.getsalesrepcode = obj.getString("employeecode");
                        ScheduleActivity.btnSaveSchedule.setEnabled(true);

//                        txtView.setBackgroundColor(Color.parseColor("#AA3456"))
//                        getsalesrepcode
//                        txtdrivername.setEnabled(false);
//                        txtroute.setEnabled(false);
//                        txtvechiclename.setEnabled(false);
//                        txtsalesrep.setEnabled(false);

                    }
                }
            }
            catch(JSONException e){
                Log.d("JSON Error", e.getMessage());
            }
        }

    }
    public void syncscheduleewaydetails(JSONObject object) {
        if(object!=null){
            JSONArray json_category=null;
            try{
                json_category = object.optJSONArray("Value");
                if(json_category != null && json_category.length()>0){

                    for(int i=0;i<json_category.length();i++) {
                        JSONObject obj = (JSONObject) json_category.get(i);
                        String schedulecode=preferenceMangr.pref_getString("getsalesschedulecode");
                        DataBaseAdapter objdatabaseadapter=null;
                        if(schedulecode!="" || schedulecode!=null){
                            try {
                                //Save Schedule Functionality
                                objdatabaseadapter = new DataBaseAdapter(context);
                                objdatabaseadapter.open();
                                objdatabaseadapter.insertScheduleeway(obj.getString("scheduledate"), schedulecode.toString(), obj.getString("ewaybillurl"));

                            }catch (Exception e) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Error in  schedule ewaybill url sync",Toast.LENGTH_LONG);
                                //toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                mDbErrHelper.open();
                                String geterrror = e.toString();
                                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                mDbErrHelper.close();
                            }
                            finally {
                                if(objdatabaseadapter != null)
                                    objdatabaseadapter.close();
                            }
                        }
//

                    }
                }
            }
            catch(JSONException e){
                Log.d("JSON Error", e.getMessage());
            }
        }
    }
    protected  class AsyncSyncItemandPriceDetails extends
            AsyncTask<String, JSONObject, String> {
        String List = "Success";
        JSONObject jsonObj = null;
        ProgressDialog loading;

        @SuppressLint("LongLogTag")
        @Override
        protected  String doInBackground(String... params) {

            RestAPI api = new RestAPI();
            String result = "";
            DataBaseAdapter dataBaseAdapter =null;
            String deviceid = preferenceMangr.pref_getString("deviceid");
            try {
                dataBaseAdapter = new DataBaseAdapter(context);
                dataBaseAdapter.open();
                networkstate = isNetworkAvailable();
                if (networkstate == true) {
                    if(ScheduleActivity.isFromschedule.equals("yes")) {
                        jsonObj = api.GetscheduleDetails(preferenceMangr.pref_getString("deviceid"), ScheduleActivity.scheduledate.getText().toString(), "syncdeliverynotescheduledetails.php", context);
                        if (isSuccessful(jsonObj)) {
                            syncscheduledetails(jsonObj);

                        }
                    }
                    /*****SYNCH ALL MASTER************/
                    //company master
                    //if(BuildConfig.DEBUG)
                        Log.w("---> Sync Activity : "," Sync item & price list Started");

                    //brand master
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync item & price list : Brandmaster");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncbrandmaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncbrandmaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "brandmaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }


                    //tax
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync item & price list : Tax");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"synctax.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.synctax(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "tax", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //itemgroup master
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync item & price list : Itemgroup master");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncitemgroupmaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncitemgroupmaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "itemgroupmaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //item master
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync item & price list : Item master");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncitemmaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncitemmaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "itemmaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }


                    //item price list transaction
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync item & price list : Item pricelist transaction");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncitempricelisttransaction.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncitempricelisttransaction(jsonObj);

                        /*Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat mformat= new SimpleDateFormat("dd-MM-yyyy h:mm a");
                        MenuActivity.pricelistlastsyncdate = mformat.format(calendar.getTime());*/
                        //Toast.makeText(context,pricelistlastsyncdate,Toast.LENGTH_SHORT).show();

                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "itempricelisttransaction", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }


                    //item subgroup master
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync item & price list : Item subgroup master");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncitemsubgroupmaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncitemsubgroupmaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "itemsubgroupmaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }


                    //scheme
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync item & price list : Scheme");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncscheme.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncscheme(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "scheme", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //scheme item details
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync item & price list : Scheme item details");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncschemeitemdetails.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncschemeitemdetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "schemeitemdetails", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //scheme rate details
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync item & price list : Scheme rate details");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncschemeratedetails.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncschemeratedetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "schemeratedetails", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //unit master
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync item & price list : Unit master");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncunitmaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncunitmaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "unitmaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }


                    //if(BuildConfig.DEBUG)
                        Log.w("---> Sync Activity : "," Sync item & price list Completed");
                }


            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.e("AsyncSyncItemandPriceDetails DIB", e.getMessage());
                if(dataBaseAdapter != null)
                    dataBaseAdapter.close();
                /*DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();*/
                InsertError(e.toString(),"SYNC Item and price DIB");
            }
            finally {
                if(dataBaseAdapter != null)
                    dataBaseAdapter.close();
            }
            return List;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(context, "Synching Item & Price List Data", "Please wait...", true, true);
            loading.setCancelable(false);
            loading.setCanceledOnTouchOutside(false);
        }
        @SuppressLint("LongLogTag")
        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            DataBaseAdapter objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            try {

                String getschedulecount = "0";
                String getschedulestatus = "";
                try {
                    //Save Schdule Functionality
                    LoginActivity.getfinanceyrcode = objdatabaseadapter.GetFinancialYrCode();
                    preferenceMangr.pref_putString("getfinanceyrcode",objdatabaseadapter.GetFinancialYrCode());
                    Cursor getschedulelist = objdatabaseadapter.GetScheduleDB();
                    if(getschedulelist.getCount() >0){
                        for(int i=0;i<getschedulelist.getCount();i++){
                            MenuActivity.getschedulecode = getschedulelist.getString(0);
                            MenuActivity.getroutecode = getschedulelist.getString(1);
                            MenuActivity.getroutename = getschedulelist.getString(4);
                            MenuActivity.gettripadvance =  getschedulelist.getString(3);
                            MenuActivity.getroutenametamil =  getschedulelist.getString(4);
                            MenuActivity.getcapacity = getschedulelist.getString(5);

                            preferenceMangr.pref_putString("getschedulecode",getschedulelist.getString(0));
                            preferenceMangr.pref_putString("getroutecode",getschedulelist.getString(1));
                            preferenceMangr.pref_putString("getroutename",getschedulelist.getString(4));
                            preferenceMangr.pref_putString("getcapacity",getschedulelist.getString(5));
                        }
                        //Get cash close Count
                        MenuActivity.getcashclosecount = objdatabaseadapter.GetCashClose(preferenceMangr.pref_getString("getschedulecode"));
                        preferenceMangr.pref_putString("getcashclosecount",objdatabaseadapter.GetCashClose(preferenceMangr.pref_getString("getschedulecode")));
                        //Get sales close Count
                        MenuActivity.getdenominationcount = objdatabaseadapter.GetDenominationCount(preferenceMangr.pref_getString("getschedulecode"));

                        MenuActivity.getsalesclosecount = objdatabaseadapter.GetSalesClose(preferenceMangr.pref_getString("getschedulecode"));
                        preferenceMangr.pref_putString("getsalesclosecount",objdatabaseadapter.GetSalesClose(preferenceMangr.pref_getString("getschedulecode")));
                        //GetWish messsgae
                        MenuActivity.getwishmsg = objdatabaseadapter.GetWishmsg();
                        preferenceMangr.pref_putString("getwishmsg",objdatabaseadapter.GetWishmsg());
                    }
                    Cursor Cur = objdatabaseadapter.GetVanNameForIMEIDB(preferenceMangr.pref_getString("deviceid"));
                    if (Cur.getCount() > 0) {
                        LoginActivity.getvancode = Cur.getString(0);
                        LoginActivity.getbusiness_type = Cur.getString(4);
                        LoginActivity.getorderprint = Cur.getString(5);
                        LoginActivity.getvanname = Cur.getString(1);

                        preferenceMangr.pref_putString("getvanname",Cur.getString(1));
                        preferenceMangr.pref_putString("getvancode",Cur.getString(0));
                        preferenceMangr.pref_putString("getbusiness_type",Cur.getString(4));
                        preferenceMangr.pref_putString("getorderprint",Cur.getString(5));
                    }
                } catch (Exception e) {
                    Log.e("AsyncSyncItemandPriceDetails POST", e.getMessage());
                    if(objdatabaseadapter != null)
                        objdatabaseadapter.close();
                    String geterrror = e.toString();
                    InsertError(e.toString(), "SYNC Item and price Post");
                } finally {
                    objdatabaseadapter.close();
                }
                /*if(!preferenceMangr.pref_getString("getsalesclosecount").equals("0") && !preferenceMangr.pref_getString("getsalesclosecount").equals("null") &&
                        !preferenceMangr.pref_getString("getsalesclosecount").equals("") && !preferenceMangr.pref_getString("getsalesclosecount").equals(null)){
                    MenuActivity.OrderForm.setVisibility(View.VISIBLE);
                }else{
                    MenuActivity.OrderForm.setVisibility(View.GONE);
                }*/

            }catch (Exception e) {
                Log.e("AsyncSyncItemandPriceDetails POST", e.getMessage());
                if(objdatabaseadapter != null)
                    objdatabaseadapter.close();
                InsertError(e.toString(),"SYNC Item and price Post");
            }
            finally {
                if(objdatabaseadapter != null)
                    objdatabaseadapter.close();
            }
            loading.dismiss();
        }
    }

    protected  class AsyncSyncCustomerDetails extends
            AsyncTask<String, JSONObject, String> {
        String List = "Success";
        JSONObject jsonObj = null;
        ProgressDialog loading;

        @SuppressLint("LongLogTag")
        @Override
        protected  String doInBackground(String... params) {

            RestAPI api = new RestAPI();
            String result = "";
            DataBaseAdapter dataBaseAdapter =null;
            String deviceid = preferenceMangr.pref_getString("deviceid");
            try {
                dataBaseAdapter = new DataBaseAdapter(context);
                dataBaseAdapter.open();

                networkstate = isNetworkAvailable();
                if (networkstate == true) {
                    //if(BuildConfig.DEBUG)
                        Log.w("---> Sync Activity : "," Sync Customer Started");

                    try{
                        AsyncCustomerDetails();
                        if(ScheduleActivity.isFromschedule.equals("yes")) {
                            jsonObj = api.GetscheduleDetails(preferenceMangr.pref_getString("deviceid"), ScheduleActivity.scheduledate.getText().toString(), "syncdeliverynotescheduledetails.php", context);
                            if (isSuccessful(jsonObj)) {
                                syncscheduledetails(jsonObj);
                            }
                        }
                    }catch (Exception e) {
                        // TODO Auto-generated catch block
                        Log.e("AsyncSyncCustomerDetails : ",e.getLocalizedMessage());
                        if(dataBaseAdapter != null)
                            dataBaseAdapter.close();
                        InsertError(e.toString(), "Sync Customer local db to server");
                    }
                    dataBaseAdapter.open();

                    //area master
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync Customer : Area master");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncareamaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncareamaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "areamaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }


                    //city
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync Customer : City master");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"synccitymaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.synccitymaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "citymaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }


                    Cursor getschedulelist = dataBaseAdapter.GetScheduleDB();
                    if(getschedulelist.getCount() >0){
                        for(int i=0;i<getschedulelist.getCount();i++) {
                            MenuActivity.getroutecode = getschedulelist.getString(1);
                            preferenceMangr.pref_putString("getroutecode",getschedulelist.getString(1));
                        }
                    }
                    // customer
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync Customer : Customer");
                    jsonObj = api.GetCustomerDetails(preferenceMangr.pref_getString("deviceid"),preferenceMangr.pref_getString("getroutecode"),"synccustomer.php");
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.synccustomer(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "customer", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //route
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync Customer : Route");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncroute.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncroute(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "route", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //route details
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync Customer : Route Details");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncroutedetails.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncroutedetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "routedetails", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //if(BuildConfig.DEBUG)
                        Log.w("Sync Activity : "," Sync Customer : State Master");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"), "syncstatemaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncstatemaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "statemaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //if(BuildConfig.DEBUG)
                        Log.w("---> Sync Activity : "," Sync Customer Completed");
                }


            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.e("AsyncSyncCustomerDetails DIB : ",e.getLocalizedMessage());
                if(dataBaseAdapter != null)
                    dataBaseAdapter.close();
                InsertError(e.toString(),"Sync Customer DIB");
            }
            finally {
                if(dataBaseAdapter != null)
                    dataBaseAdapter.close();
            }
            return List;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(context, "Synching Customer Data", "Please wait...", true, true);
            loading.setCancelable(false);
            loading.setCanceledOnTouchOutside(false);
        }
        @SuppressLint("LongLogTag")
        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            DataBaseAdapter objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            try {

                String getschedulecount = "0";
                String getschedulestatus = "";
                try {
                    //Save Schdule Functionality

                    LoginActivity.getfinanceyrcode = objdatabaseadapter.GetFinancialYrCode();
                    preferenceMangr.pref_putString("getfinanceyrcode",objdatabaseadapter.GetFinancialYrCode());
                    Cursor getschedulelist = objdatabaseadapter.GetScheduleDB();
                    if(getschedulelist.getCount() >0){
                        for(int i=0;i<getschedulelist.getCount();i++){
                            MenuActivity.getschedulecode = getschedulelist.getString(0);
                            MenuActivity.getroutecode = getschedulelist.getString(1);
                            MenuActivity.getroutename = getschedulelist.getString(4);
                            MenuActivity.gettripadvance =  getschedulelist.getString(3);
                            MenuActivity.getroutenametamil =  getschedulelist.getString(4);
                            MenuActivity.getcapacity = getschedulelist.getString(5);

                            preferenceMangr.pref_putString("getschedulecode",getschedulelist.getString(0));
                            preferenceMangr.pref_putString("getroutecode",getschedulelist.getString(1));
                            preferenceMangr.pref_putString("getroutename",getschedulelist.getString(4));
                            preferenceMangr.pref_putString("getcapacity",getschedulelist.getString(5));
                        }
                        //Get cash close Count
                        MenuActivity.getcashclosecount = objdatabaseadapter.GetCashClose(preferenceMangr.pref_getString("getschedulecode"));
                        preferenceMangr.pref_putString("getcashclosecount",objdatabaseadapter.GetCashClose(preferenceMangr.pref_getString("getschedulecode")));
                        //Get sales close Count
                        MenuActivity.getdenominationcount = objdatabaseadapter.GetDenominationCount(preferenceMangr.pref_getString("getschedulecode"));

                        MenuActivity.getsalesclosecount = objdatabaseadapter.GetSalesClose(preferenceMangr.pref_getString("getschedulecode"));
                        preferenceMangr.pref_putString("getsalesclosecount",objdatabaseadapter.GetSalesClose(preferenceMangr.pref_getString("getschedulecode")));

                        //GetWish messsgae
                        MenuActivity.getwishmsg = objdatabaseadapter.GetWishmsg();
                        preferenceMangr.pref_putString("getwishmsg",objdatabaseadapter.GetWishmsg());
                    }
                    Cursor Cur = objdatabaseadapter.GetVanNameForIMEIDB(preferenceMangr.pref_getString("deviceid"));
                    if (Cur.getCount() > 0) {
                        LoginActivity.getvancode = Cur.getString(0);
                        LoginActivity.getbusiness_type = Cur.getString(4);
                        LoginActivity.getorderprint = Cur.getString(5);
                        LoginActivity.getvanname = Cur.getString(1);

                        preferenceMangr.pref_putString("getvanname",Cur.getString(1));
                        preferenceMangr.pref_putString("getvancode",Cur.getString(0));
                        preferenceMangr.pref_putString("getbusiness_type",Cur.getString(4));
                        preferenceMangr.pref_putString("getorderprint",Cur.getString(5));
                    }
                } catch (Exception e) {
                    Log.e("Async Customer Post : ",e.getLocalizedMessage());
                    if( objdatabaseadapter != null)
                        objdatabaseadapter.close();
                    InsertError(e.toString(),"Sync Customer Post");
                } finally {
                    if( objdatabaseadapter != null)
                        objdatabaseadapter.close();
                }
                /*if(!preferenceMangr.pref_getString("getsalesclosecount").equals("0") && !preferenceMangr.pref_getString("getsalesclosecount").equals("null") &&
                        !preferenceMangr.pref_getString("getsalesclosecount").equals("") && !preferenceMangr.pref_getString("getsalesclosecount").equals(null)){
                    MenuActivity.OrderForm.setVisibility(View.VISIBLE);
                }else{
                    MenuActivity.OrderForm.setVisibility(View.GONE);
                }*/

            }catch (Exception e) {
                Log.e("AsyncSyncCustomerDetails Post : ",e.getLocalizedMessage());
                if( objdatabaseadapter != null)
                    objdatabaseadapter.close();
                InsertError(e.toString(),"Sync Customer Post");
            }
            finally{
                if(objdatabaseadapter != null)
                    objdatabaseadapter.close();
            }
            loading.dismiss();
        }
    }

    protected  class AsyncSyncScheduleandVanstockDetails extends
            AsyncTask<String, JSONObject, String> {
        String List = "Success";
        JSONObject jsonObj = null;
        ProgressDialog loading;

        @SuppressLint("LongLogTag")
        @Override
        protected  String doInBackground(String... params) {

            RestAPI api = new RestAPI();
            String result = "";
            DataBaseAdapter dataBaseAdapter =null;
            String deviceid = preferenceMangr.pref_getString("deviceid");
            try {
                dataBaseAdapter = new DataBaseAdapter(context);
                dataBaseAdapter.open();
                networkstate = isNetworkAvailable();
                if (networkstate == true) {
                    //if(BuildConfig.DEBUG)
                        Log.w("---> Sync Activity : "," Sync schedule and van stock Started");

                    try{
                        AsyncScheduleDetails();
                        if(ScheduleActivity.isFromschedule.equals("yes")) {
                            jsonObj = api.GetscheduleDetails(preferenceMangr.pref_getString("deviceid"), ScheduleActivity.scheduledate.getText().toString(), "syncdeliverynotescheduledetails.php", context);
                            if (isSuccessful(jsonObj)) {
                                syncscheduledetails(jsonObj);
                            }
                        }
                        // sync schedule eway
                        jsonObj = api.GetscheduleDetails(preferenceMangr.pref_getString("deviceid"),dataBaseAdapter.GenCurrentCreatedDate(),"syncdeliverynotescheduledetails.php", context);
                        if (isSuccessful(jsonObj)) {
                            syncscheduleewaydetails(jsonObj);
                        }
                    }catch (Exception e) {
                        // TODO Auto-generated catch block
                        Log.e("AsyncSyncScheduleandVanstockDetails : ",e.getLocalizedMessage());
                        if(dataBaseAdapter != null)
                            dataBaseAdapter.close();
                        InsertError(e.toString(),"Sync Schedule and Van stock local db to server");
                    }
                    dataBaseAdapter.open();
                    //Get General settings
                    String getschedulestatus = dataBaseAdapter.GetScheduleStatusDB();
                    if(getschedulestatus.equals("yes")){
                        //Sales Schedule
                        //if(BuildConfig.DEBUG)
                             Log.w("Sync Activity : "," Sync schedule and van stock : Sales schedule");
                        jsonObj = api.GetAllDetails(deviceid,"syncsalesschedulemobile.php",context);
                        if (isSuccessful(jsonObj)) {
                            dataBaseAdapter.syncsalesschedulemobile(jsonObj);
                            api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"),"salesschedule",preferenceMangr.pref_getString("getvancode"),"");
                        }
                    }else{
                        //Sales Schedule portal
                        //if(BuildConfig.DEBUG)
                             Log.w("Sync Activity : "," Sync schedule and van stock : Sales schedule portal");
                        jsonObj = api.GetAllDetails(deviceid,"syncsalesschedule.php",context);
                        if (isSuccessful(jsonObj)) {
                            dataBaseAdapter.syncsalesschedule(jsonObj);
                            api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"),"salesscheduleportal",preferenceMangr.pref_getString("getvancode"),"");
                        }
                    }

                    /*****SYNCH ALL MASTER************/

                    //van stock
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync schedule and van stock : Van stock");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncvanstock.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncvanstocktransaction(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "vanstock", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }



///                 <-------------------- Transport -------------------->
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync schedule and van stock : Van master");
                    jsonObj = api.GetAllDetails(deviceid, "syncvanmaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncvanmaster(jsonObj);
                        api.udfnSyncDetails(deviceid, "vanmaster", "0", "");
                    }



                    //transport mode
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync schedule and van stock : Transport mode");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"synctransportmode.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.synctransportmode(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "transportmode", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //transport
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync schedule and van stock : Transport");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"synctransport.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.synctransport(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "transport", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //transportareamapping
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync schedule and van stock : Transport city mapping");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"synctransportcitymapping.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.synctransportareamapping(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "transportcitymapping", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }


//                  <----------------------- Settings  ----------------------------->
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync schedule and van stock : Vehicle master");
                    //vehicle master
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncvehiclemaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncvehiclemaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "vehiclemaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //General settings
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync schedule and van stock : General settings");
                    jsonObj = api.GetAllDetails(deviceid,"syncgeneralsettings.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncgeneralsettings(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"),"generalsettings",preferenceMangr.pref_getString("getvancode"),"");
                    }

                    //currency
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync schedule and van stock : Currency");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"synccurrency.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.synccurrency(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "currency", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //Receipt remarks
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync schedule and van stock : Receipt remarks");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncreceiptremarks.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncreceiptremarks(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "receiptremarks", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //Bill type
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync schedule and van stock : Bill type");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncbilltype.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncbilltype(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "billtype", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //expenses head
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync schedule and van stock : Expenses head");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncexpenseshead.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncexpenseshead(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "expenseshead", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //financial year
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync schedule and van stock : Financialyear");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncfinancialyear.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncfinancialyear(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "financialyear", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //voucher settings
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync schedule and van stock : Voucher settings");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncvouchersettings.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncvouchersettings(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "vouchersettings", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    LoginActivity.getfinanceyrcode = dataBaseAdapter.GetFinancialYrCode();
                    preferenceMangr.pref_putString("getfinanceyrcode",dataBaseAdapter.GetFinancialYrCode());

                    //maxrefno
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync schedule and van stock : Max refno");
                    jsonObj = api.GetMaxCode(preferenceMangr.pref_getString("deviceid"),preferenceMangr.pref_getString("getfinanceyrcode"),"syncmaxrefno.php");
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncmaxrefno(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "maxrefno", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    /*try{
                        *//*Cursor cursor =dataBaseAdapter.getprinterdetails();
                        if(cursor.getCount()>0){
                            PrinterSettingsActivity.SelectedPrinterName=(cursor.getString(1));
                            PrinterSettingsActivity.SelectedPrinterAddress=(cursor.getString(2));
                        }*//*
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
                    /*finally {
                        dataBaseAdapter.close();
                    }*/
                    //Not purchased remarks
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString(Constants.KEY_DEVICEID),"syncnotpurchasedremarks.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncNotPurchasedRemarks(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString(Constants.KEY_DEVICEID), "receiptremarks", preferenceMangr.pref_getString(Constants.KEY_GETVANCODE), preferenceMangr.pref_getString(Constants.KEY_GET_SCHEDULE_SCHEDULECODE));
                    }
                    //if(BuildConfig.DEBUG)
                        Log.w("---> Sync Activity : "," Sync schedule and van stock Completed");
                }


            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.e("AsyncSyncScheduleandVanstockDetails DIB : ",e.getLocalizedMessage());
                if(dataBaseAdapter != null)
                    dataBaseAdapter.close();
                InsertError(e.toString(),"Sync Schedule and Van stock local db to server DIB");
            }
            finally {
                if(dataBaseAdapter != null)
                    dataBaseAdapter.close();
            }
            return List;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(context, "Synching Schedule and Van Stock Data", "Please wait...", true, true);
            loading.setCancelable(false);
            loading.setCanceledOnTouchOutside(false);
        }
        @SuppressLint("LongLogTag")
        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            DataBaseAdapter objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            try {

                String getschedulecount = "0";
                String getschedulestatus = "";
                try {
                    //Save Schdule Functionality
                    LoginActivity.getfinanceyrcode = objdatabaseadapter.GetFinancialYrCode();
                    preferenceMangr.pref_putString("getfinanceyrcode",objdatabaseadapter.GetFinancialYrCode());
                    Cursor getschedulelist = objdatabaseadapter.GetScheduleDB();
                    if(getschedulelist.getCount() >0){
                        for(int i=0;i<getschedulelist.getCount();i++){
                            MenuActivity.getschedulecode = getschedulelist.getString(0);
                            MenuActivity.getroutecode = getschedulelist.getString(1);
                            MenuActivity.getroutename = getschedulelist.getString(4);
                            MenuActivity.gettripadvance =  getschedulelist.getString(3);
                            MenuActivity.getroutenametamil =  getschedulelist.getString(4);
                            MenuActivity.getcapacity = getschedulelist.getString(5);

                            preferenceMangr.pref_putString("getschedulecode",getschedulelist.getString(0));
                            preferenceMangr.pref_putString("getroutecode",getschedulelist.getString(1));
                            preferenceMangr.pref_putString("getroutename",getschedulelist.getString(4));
                            preferenceMangr.pref_putString("getcapacity",getschedulelist.getString(5));

                        }
                        //Get cash close Count
                        MenuActivity.getcashclosecount = objdatabaseadapter.GetCashClose(preferenceMangr.pref_getString("getschedulecode"));
                        preferenceMangr.pref_putString("getcashclosecount",objdatabaseadapter.GetCashClose(preferenceMangr.pref_getString("getschedulecode")));
                        //Get sales close Count
                        MenuActivity.getdenominationcount = objdatabaseadapter.GetDenominationCount(preferenceMangr.pref_getString("getschedulecode"));

                        MenuActivity.getsalesclosecount = objdatabaseadapter.GetSalesClose(preferenceMangr.pref_getString("getschedulecode"));
                        preferenceMangr.pref_putString("getsalesclosecount",objdatabaseadapter.GetSalesClose(preferenceMangr.pref_getString("getschedulecode")));
                        //GetWish messsgae
                        MenuActivity.getwishmsg = objdatabaseadapter.GetWishmsg();
                        preferenceMangr.pref_putString("getwishmsg",objdatabaseadapter.GetWishmsg());
                    }
                    Cursor Cur = objdatabaseadapter.GetVanNameForIMEIDB(preferenceMangr.pref_getString("deviceid"));
                    if (Cur.getCount() > 0) {
                        LoginActivity.getvancode = Cur.getString(0);
                        LoginActivity.getbusiness_type = Cur.getString(4);
                        LoginActivity.getorderprint = Cur.getString(5);
                        LoginActivity.getvanname = Cur.getString(1);

                        preferenceMangr.pref_putString("getvanname",Cur.getString(1));
                        preferenceMangr.pref_putString("getvancode",Cur.getString(0));
                        preferenceMangr.pref_putString("getbusiness_type",Cur.getString(4));
                        preferenceMangr.pref_putString("getorderprint",Cur.getString(5));
                    }
                } catch (Exception e) {
                    Log.e("AsyncSyncScheduleandVanstockDetails Post : ",e.getLocalizedMessage());
                    if(objdatabaseadapter != null)
                        objdatabaseadapter.close();
                    InsertError(e.toString(),"Sync Schedule and Van stock Post");
                } finally {
                    if(objdatabaseadapter != null)
                        objdatabaseadapter.close();
                }
                /*if(!preferenceMangr.pref_getString("getsalesclosecount").equals("0") && !preferenceMangr.pref_getString("getsalesclosecount").equals("null") &&
                        !preferenceMangr.pref_getString("getsalesclosecount").equals("") && !preferenceMangr.pref_getString("getsalesclosecount").equals(null)){
                    MenuActivity.OrderForm.setVisibility(View.VISIBLE);
                }else{
                    MenuActivity.OrderForm.setVisibility(View.GONE);
                }*/

            }catch (Exception e) {
                Log.e("AsyncSyncScheduleandVanstockDetails Post : ",e.getLocalizedMessage());
                if(objdatabaseadapter != null)
                    objdatabaseadapter.close();
                InsertError(e.toString(),"Sync Schedule and Van stock Post");
            }
            finally {
                if(objdatabaseadapter != null)
                    objdatabaseadapter.close();
            }
            loading.dismiss();
        }
    }

    protected  class AsyncSyncTransactionDetails extends
            AsyncTask<String, JSONObject, String> {
        String List = "Success";
        JSONObject jsonObj = null;
        ProgressDialog loading;

        @SuppressLint("LongLogTag")
        @Override
        protected  String doInBackground(String... params) {

            RestAPI api = new RestAPI();
            String result = "";
            DataBaseAdapter dataBaseAdapter =null;
            String deviceid = preferenceMangr.pref_getString("deviceid");
            try {
                dataBaseAdapter = new DataBaseAdapter(context);
                dataBaseAdapter.open();

                //AsyncSalesDetails();

                networkstate = isNetworkAvailable();
                if (networkstate == true) {

                    //if(BuildConfig.DEBUG)
                        Log.w("---> Sync Activity : "," Sync Transaction Started");
                    networkstate = isNetworkAvailable();
                    if (networkstate == true) {
                        try{
                            AsyncExpenseDetails();
                            AsyncCancelExpenseDetails();
                            AsyncOrderDetails();
                            AsyncSalesDetails();
                            AsyncUpdateSalesReceiptDetails();
                            AsyncSalesCancelDetails();
                            AsyncSalesReturnDetails();
                            AsyncSalesReturnCancelDetails();
                            AsyncReceiptDetails();
                            AsyncCancelReceiptDetails();
                            AsyncNilStockDetails();
                            AsyncSalesOrderDetails();
                            AsyncSalesOrderCancelDetails();
                            AsyncNotPurchasedDetails();
                            if(ScheduleActivity.isFromschedule.equals("yes")) {
                                jsonObj = api.GetscheduleDetails(preferenceMangr.pref_getString("deviceid"), ScheduleActivity.scheduledate.getText().toString(), "syncdeliverynotescheduledetails.php", context);
                                if (isSuccessful(jsonObj)) {
                                    syncscheduledetails(jsonObj);
                                }
                            }

                        }catch (Exception e) {
                            // TODO Auto-generated catch block
                            Log.d("AsyncSyncTransactionDetails", e.getMessage());
                            if(dataBaseAdapter != null)
                                dataBaseAdapter.close();
                            InsertError(e.toString(),"Sync Transaction local db to server");
                        }
                    }

                    /**************DELETE LAST DAY TRANSACTIONS IN SQL LITE***********/
                    dataBaseAdapter.open();

                    dataBaseAdapter.DeleteExpensesDays();
                    dataBaseAdapter.DeleteorderDetails();
                    dataBaseAdapter.DeleteReceiptDays();
                    dataBaseAdapter.DeleteSalesDays();
                    dataBaseAdapter.DeleteSalesItemDetailsDays();
                    dataBaseAdapter.DeleteSalesReturnDays();
                    dataBaseAdapter.DeleteSalesReturnItemDetailsDays();


                    /**************END DELETE LAST DAY TRANSACTIONS IN SQL LITE***********/
                    //expenses
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync Transaction : Expenses");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncexpenses.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncexpensesmaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "expenses", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }


                    //receipt
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync Transaction : Receipt");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncreceipt.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncreceipt(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "receipt", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //order details
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync Transaction : Order details");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncorderdetails.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncorderdetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "orderdetails", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //Sales
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync Transaction : Sales");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncsales.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncsales(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "sales", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //Salesitemdetails
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync Transaction : Sales item details");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncsalesitemdetails.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncsalesitemdetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "salesitemdetails", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //Sales order
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync Transaction : Sales order");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncsalesorder.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncsalesorder(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "salesorder", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }
                    //Sales order itemdetails
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync Transaction : Sales order itemdetails");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncsalesorderitemdetails.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncsalesorderitemdetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "salesorderitemdetails", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //Salesreturn
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync Transaction : Sales return");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncsalesreturn.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncsalesreturn(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "salesreturn", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }


                    //Salesretrunitemdetails
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync Transaction : Sales return item details");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncsalesreturnitemdetails.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncsalesreturnitemdetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "salesreturnitemdetails", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //nilstock
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync Transaction : Nil stock");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncnilstock.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncnilstock(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "nilstock", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    // customer
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync Transaction : Customer");
                    jsonObj = api.GetCashNotPaidDetails(preferenceMangr.pref_getString("deviceid"),preferenceMangr.pref_getString("getvancode"),preferenceMangr.pref_getString("getroutecode"),"synccashnotpaiddetails.php");
                    dataBaseAdapter.DeleteCashNotPaid();
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.synccashnotpaiddetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "cashnotpaiddetails", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //item price list transaction
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync Transaction : Item price list transaction");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncitempricelisttransaction.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncitempricelisttransaction(jsonObj);

                        /*Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat mformat= new SimpleDateFormat("dd-MM-yyyy h:mm a");
                        MenuActivity.pricelistlastsyncdate = mformat.format(calendar.getTime());*/
                        //Toast.makeText(context,pricelistlastsyncdate,Toast.LENGTH_SHORT).show();

                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "itempricelisttransaction", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }
                    //Not Purchased
                    jsonObj = api.GetNotPurchasedDetails(preferenceMangr.pref_getString(Constants.KEY_DEVICEID),"syncnotpurchaseddetails.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncnotpurchaseddetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString(Constants.KEY_DEVICEID), "notpurchased", preferenceMangr.pref_getString(Constants.KEY_GETVANCODE), preferenceMangr.pref_getString(Constants.KEY_GET_SCHEDULE_SCHEDULECODE));
                    }

                    //if(BuildConfig.DEBUG)
                        Log.w("---> Sync Activity : "," Sync Transaction Completed");
                }


            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.e("AsyncSyncTransactionDetails DIB : ",e.getLocalizedMessage());
                if(dataBaseAdapter != null)
                    dataBaseAdapter.close();
                InsertError(e.toString(),"Sync Transaction DIB");
            }
            finally {
                if(dataBaseAdapter != null)
                    dataBaseAdapter.close();
            }
            return List;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(context, "Synching Transaction Data", "Please wait...", true, true);
            loading.setCancelable(false);
            loading.setCanceledOnTouchOutside(false);
        }
        @SuppressLint("LongLogTag")
        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            DataBaseAdapter objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            try {

                String getschedulecount = "0";
                String getschedulestatus = "";
                try {
                    //Save Schdule Functionality
                    LoginActivity.getfinanceyrcode = objdatabaseadapter.GetFinancialYrCode();
                    preferenceMangr.pref_putString("getfinanceyrcode",objdatabaseadapter.GetFinancialYrCode());
                    Cursor getschedulelist = objdatabaseadapter.GetScheduleDB();
                    if(getschedulelist.getCount() >0){
                        for(int i=0;i<getschedulelist.getCount();i++){
                            MenuActivity.getschedulecode = getschedulelist.getString(0);
                            MenuActivity.getroutecode = getschedulelist.getString(1);
                            MenuActivity.getroutename = getschedulelist.getString(4);
                            MenuActivity.gettripadvance =  getschedulelist.getString(3);
                            MenuActivity.getroutenametamil =  getschedulelist.getString(4);
                            MenuActivity.getcapacity = getschedulelist.getString(5);

                            preferenceMangr.pref_putString("getschedulecode",getschedulelist.getString(0));
                            preferenceMangr.pref_putString("getroutecode",getschedulelist.getString(1));
                            preferenceMangr.pref_putString("getroutename",getschedulelist.getString(4));
                            preferenceMangr.pref_putString("getcapacity",getschedulelist.getString(5));
                        }
                        //Get cash close Count
                        MenuActivity.getcashclosecount = objdatabaseadapter.GetCashClose(preferenceMangr.pref_getString("getschedulecode"));
                        preferenceMangr.pref_putString("getcashclosecount",objdatabaseadapter.GetCashClose(preferenceMangr.pref_getString("getschedulecode")));
                        //Get sales close Count
                        MenuActivity.getdenominationcount = objdatabaseadapter.GetDenominationCount(preferenceMangr.pref_getString("getschedulecode"));

                        MenuActivity.getsalesclosecount = objdatabaseadapter.GetSalesClose(preferenceMangr.pref_getString("getschedulecode"));
                        preferenceMangr.pref_putString("getsalesclosecount",objdatabaseadapter.GetSalesClose(preferenceMangr.pref_getString("getschedulecode")));
                        //GetWish messsgae
                        MenuActivity.getwishmsg = objdatabaseadapter.GetWishmsg();
                        preferenceMangr.pref_putString("getwishmsg",objdatabaseadapter.GetWishmsg());
                    }
                    Cursor Cur = objdatabaseadapter.GetVanNameForIMEIDB(preferenceMangr.pref_getString("deviceid"));
                    if (Cur.getCount() > 0) {
                        LoginActivity.getvancode = Cur.getString(0);
                        LoginActivity.getbusiness_type = Cur.getString(4);
                        LoginActivity.getorderprint = Cur.getString(5);
                        LoginActivity.getvanname = Cur.getString(1);

                        preferenceMangr.pref_putString("getvanname",Cur.getString(1));
                        preferenceMangr.pref_putString("getvancode",Cur.getString(0));
                        preferenceMangr.pref_putString("getbusiness_type",Cur.getString(4));
                        preferenceMangr.pref_putString("getorderprint",Cur.getString(5));
                    }
                } catch (Exception e) {
                    Log.e("AsyncSyncTransactionDetails Post : ",e.getLocalizedMessage());
                    if(objdatabaseadapter != null)
                        objdatabaseadapter.close();
                    InsertError(e.toString(),"Sync Transaction Post");
                } finally {
                    if(objdatabaseadapter != null)
                        objdatabaseadapter.close();
                }
                /*if(!preferenceMangr.pref_getString("getsalesclosecount").equals("0") && !preferenceMangr.pref_getString("getsalesclosecount").equals("null") &&
                        !preferenceMangr.pref_getString("getsalesclosecount").equals("") && !preferenceMangr.pref_getString("getsalesclosecount").equals(null)){
                    MenuActivity.OrderForm.setVisibility(View.VISIBLE);
                }else{
                    MenuActivity.OrderForm.setVisibility(View.GONE);
                }*/

            }catch (Exception e) {
                Log.e("AsyncSyncTransactionDetails Post : ",e.getLocalizedMessage());
                if(objdatabaseadapter != null)
                    objdatabaseadapter.close();
                InsertError(e.toString(),"Sync Transaction Post");
            }finally{
                if(objdatabaseadapter != null)
                    objdatabaseadapter.close();
            }
            loading.dismiss();
        }
    }

    protected  class AsyncSyncSalesCashCloseDetails extends
            AsyncTask<String, JSONObject, String> {
        String List = "Success";
        JSONObject jsonObj = null;
        ProgressDialog loading;

        @SuppressLint("LongLogTag")
        @Override
        protected  String doInBackground(String... params) {

            RestAPI api = new RestAPI();
            String result = "";
            DataBaseAdapter dataBaseAdapter =null;
            String deviceid = preferenceMangr.pref_getString("deviceid");
            try {
                dataBaseAdapter = new DataBaseAdapter(context);
                dataBaseAdapter.open();
                networkstate = isNetworkAvailable();
                if (networkstate == true) {

                    //if(BuildConfig.DEBUG)
                        Log.w("---> Sync Activity : "," Sync sales cash close Started");

                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync sales cash close : Van master");
                    jsonObj = api.GetAllDetails(deviceid, "syncvanmaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncvanmaster(jsonObj);
                        api.udfnSyncDetails(deviceid, "vanmaster", "0", "");
                    }
                    //General settings
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync sales cash close : General settings");
                    jsonObj = api.GetAllDetails(deviceid,"syncgeneralsettings.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncgeneralsettings(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"),"generalsettings",preferenceMangr.pref_getString("getvancode"),"");
                    }
                    networkstate = isNetworkAvailable();
                    if (networkstate == true) {
                        try{

                            AsyncCashReportDetails();
                            AsyncCloseCashDetails();
                            AsyncCloseSalesDetails();
                            if(ScheduleActivity.isFromschedule.equals("yes")) {
                                jsonObj = api.GetscheduleDetails(preferenceMangr.pref_getString("deviceid"), ScheduleActivity.scheduledate.getText().toString(), "syncdeliverynotescheduledetails.php", context);
                                if (isSuccessful(jsonObj)) {
                                    syncscheduledetails(jsonObj);
                                }
                            }

                        }catch (Exception e) {
                            // TODO Auto-generated catch block
                            Log.e("AsyncSyncSalesCashCloseDetails", e.getMessage());
                            if(dataBaseAdapter != null)
                                dataBaseAdapter.close();
                            InsertError(e.toString(),"Sync Sales Cash Close local db to server");
                        }
                    }

                    /**************END DELETE LAST DAY TRANSACTIONS IN SQL LITE***********/

                    dataBaseAdapter.open();

                    //cashclose
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync sales cash close : Cash close");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"synccashclose.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.synccashclose(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "cashclose", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //salesclose
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync sales cash close : Sales close");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncsalesclose.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncsalesclose(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "salesclose", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //cashreport
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync sales cash close : Cash report");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"synccashreport.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.synccashreport(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "cashreport", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //denomination
                    //if(BuildConfig.DEBUG)
                         Log.w("Sync Activity : "," Sync sales cash close : Denomination");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncdenomination.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncdenomination(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "denomination", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //if(BuildConfig.DEBUG)
                        Log.w("---> Sync Activity : "," Sync sales cash close Completed");
                }


            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.e("AsyncSyncSalesCashCloseDetails DIB : ",e.getLocalizedMessage());
                if(dataBaseAdapter != null)
                    dataBaseAdapter.close();
                InsertError(e.toString(),"Sync Sales Cash Close DIB");
            }
            finally {
                if(dataBaseAdapter != null)
                    dataBaseAdapter.close();
            }
            return List;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(context, "Synching Sales and Cash Close Data", "Please wait...", true, true);
            loading.setCancelable(false);
            loading.setCanceledOnTouchOutside(false);
        }
        @SuppressLint("LongLogTag")
        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            DataBaseAdapter objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            try {


                String getschedulecount = "0";
                String getschedulestatus = "";
                try {
                    //Save Schdule Functionality
                    LoginActivity.getfinanceyrcode = objdatabaseadapter.GetFinancialYrCode();
                    preferenceMangr.pref_putString("getfinanceyrcode",objdatabaseadapter.GetFinancialYrCode());
                    Cursor getschedulelist = objdatabaseadapter.GetScheduleDB();
                    if(getschedulelist.getCount() >0){
                        for(int i=0;i<getschedulelist.getCount();i++){
                            MenuActivity.getschedulecode = getschedulelist.getString(0);
                            MenuActivity.getroutecode = getschedulelist.getString(1);
                            MenuActivity.getroutename = getschedulelist.getString(4);
                            MenuActivity.gettripadvance =  getschedulelist.getString(3);
                            MenuActivity.getroutenametamil =  getschedulelist.getString(4);
                            MenuActivity.getcapacity = getschedulelist.getString(5);

                            preferenceMangr.pref_putString("getschedulecode",getschedulelist.getString(0));
                            preferenceMangr.pref_putString("getroutecode",getschedulelist.getString(1));
                            preferenceMangr.pref_putString("getroutename",getschedulelist.getString(4));
                            preferenceMangr.pref_putString("getcapacity",getschedulelist.getString(5));
                        }
                        //Get cash close Count
                        MenuActivity.getcashclosecount = objdatabaseadapter.GetCashClose(preferenceMangr.pref_getString("getschedulecode"));
                        preferenceMangr.pref_putString("getcashclosecount",objdatabaseadapter.GetCashClose(preferenceMangr.pref_getString("getschedulecode")));

                        ScheduleActivity.getcashclosecount =  objdatabaseadapter.GetCashClose(preferenceMangr.pref_getString("getschedulecode"));
                        preferenceMangr.pref_putString("schedule_getcashclosecount",objdatabaseadapter.GetCashClose(preferenceMangr.pref_getString("getschedulecode")));

                        //Get sales close Count
                        MenuActivity.getdenominationcount = objdatabaseadapter.GetDenominationCount(preferenceMangr.pref_getString("getschedulecode"));

                        MenuActivity.getsalesclosecount = objdatabaseadapter.GetSalesClose(preferenceMangr.pref_getString("getschedulecode"));
                        preferenceMangr.pref_putString("getsalesclosecount",objdatabaseadapter.GetSalesClose(preferenceMangr.pref_getString("getschedulecode")));

                        ScheduleActivity.getsalesclosecount =  objdatabaseadapter.GetSalesClose(preferenceMangr.pref_getString("getschedulecode"));
                        preferenceMangr.pref_putString("schedule_getsalesclosecount",objdatabaseadapter.GetSalesClose(preferenceMangr.pref_getString("getschedulecode")));
                        //GetWish messsgae
                        MenuActivity.getwishmsg = objdatabaseadapter.GetWishmsg();
                        preferenceMangr.pref_putString("getwishmsg",objdatabaseadapter.GetWishmsg());
                    }
                    Cursor Cur = objdatabaseadapter.GetVanNameForIMEIDB(preferenceMangr.pref_getString("deviceid"));
                    if (Cur.getCount() > 0) {
                        LoginActivity.getvancode = Cur.getString(0);
                        LoginActivity.getbusiness_type = Cur.getString(4);
                        LoginActivity.getorderprint = Cur.getString(5);
                        LoginActivity.getvanname = Cur.getString(1);

                        preferenceMangr.pref_putString("getvanname",Cur.getString(1));
                        preferenceMangr.pref_putString("getvancode",Cur.getString(0));
                        preferenceMangr.pref_putString("getbusiness_type",Cur.getString(4));
                        preferenceMangr.pref_putString("getorderprint",Cur.getString(5));
                    }
                } catch (Exception e) {
                    Log.e("AsyncSyncSalesCashCloseDetails Post : ",e.getLocalizedMessage());
                    if(objdatabaseadapter != null)
                        objdatabaseadapter.close();
                    InsertError(e.toString(),"Sync Sales Cash Close Post");
                } finally {
                    if(objdatabaseadapter != null)
                        objdatabaseadapter.close();
                }
                /*if(!preferenceMangr.pref_getString("getsalesclosecount").equals("0") && !preferenceMangr.pref_getString("getsalesclosecount").equals("null") &&
                        !preferenceMangr.pref_getString("getsalesclosecount").equals("") && !preferenceMangr.pref_getString("getsalesclosecount").equals(null)){
                    MenuActivity.OrderForm.setVisibility(View.VISIBLE);
                }else{
                    MenuActivity.OrderForm.setVisibility(View.GONE);
                }*/

            }catch (Exception e) {
                Log.e("AsyncSyncSalesCashCloseDetails Post : ",e.getLocalizedMessage());
                if(objdatabaseadapter != null)
                    objdatabaseadapter.close();
                InsertError(e.toString(),"Sync Sales Cash Close Post");
            }
            finally {
                if(objdatabaseadapter != null)
                    objdatabaseadapter.close();
            }
            loading.dismiss();
        }
    }




    @SuppressLint("LongLogTag")
    public void AsyncCashReportDetails(){
        ArrayList<ScheduleDatas> List = null;
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();
        DataBaseAdapter dbadapter = new DataBaseAdapter(context);
        try {
            JSONObject js_obj = new JSONObject();
            JSONObject js_obj1 = new JSONObject();
            try {

                dbadapter.open();
                Cursor mCur2 = dbadapter.GetCashReportDatasDB();
                Cursor mCur3 = dbadapter.GetDenominationDatasDB();
                JSONArray js_array2 = new JSONArray();
                for (int i = 0; i < mCur2.getCount(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("autonum", mCur2.getString(0));
                    obj.put("schedulecode", mCur2.getString(1));
                    obj.put("vancode", mCur2.getString(2));
                    obj.put("sales", mCur2.getDouble(3));
                    obj.put("salesreturn", mCur2.getDouble(4));
                    obj.put("advance", mCur2.getDouble(5));
                    obj.put("receipt", mCur2.getDouble(6));
                    obj.put("expenses", mCur2.getDouble(7));
                    obj.put("cash", mCur2.getDouble(8));
                    obj.put("denominationcash", mCur2.getDouble(9));
                    obj.put("makerid", mCur2.getString(10));
                    obj.put("createddate", mCur2.getString(11));
                    js_array2.put(obj);
                    mCur2.moveToNext();
                }
                JSONArray js_array3 = new JSONArray();
                for (int i = 0; i < mCur3.getCount(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("autonum", mCur3.getString(0));
                    obj.put("vancode", mCur3.getString(1));
                    obj.put("schedulecode", mCur3.getString(2));
                    obj.put("currencycode", mCur3.getString(3));
                    obj.put("qty", mCur3.getDouble(4));
                    obj.put("amount", mCur3.getDouble(5));
                    obj.put("makerid", mCur3.getString(6));
                    obj.put("createddate", mCur3.getString(7));
                    js_array3.put(obj);
                    mCur3.moveToNext();
                }
                js_obj.put("JSonObject", js_array2);
                js_obj1.put("JSonObject", js_array3);
                jsonObj =  api.CashReportDetails(js_obj.toString(),js_obj1.toString(),context);
                //Call Json parser functionality
                JSONParser parser = new JSONParser();
                //parse the json object to boolean
                List = parser.parseCashReport(jsonObj);
                dbadapter.close();

                if (List.size() >= 1) {
                    if(List.get(0).ScheduleCode.length>0){
                        for(int j=0;j<List.get(0).ScheduleCode.length;j++){
                            DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
                            dataBaseAdapter.open();
                            dataBaseAdapter.UpdateCashDetailsFlag(List.get(0).ScheduleCode[j]);
                            dataBaseAdapter.close();
                        }
                    }

                }
            }
            catch (Exception e)
            {
                Log.e("AsyncCashReportDetails : ", e.getMessage());
                if(dbadapter != null)
                    dbadapter.close();
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("AsyncCashReportDetails : ", e.getMessage());
            if(dbadapter != null)
                dbadapter.close();
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }

    }
    //Cash close Details
    @SuppressLint("LongLogTag")
    public void AsyncCloseCashDetails(){
        ArrayList<ScheduleDatas> List = null;
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();
        DataBaseAdapter dbadapter = new DataBaseAdapter(context);
        try {
            JSONObject js_obj = new JSONObject();
            JSONObject js_obj1 = new JSONObject();
            try {

                dbadapter.open();
                Cursor mCur2 = dbadapter.GetCloseSalesDatasDB();
                Cursor mCur3 = dbadapter.GetEndingKmScheduleDatasDB();
                JSONArray js_array2 = new JSONArray();
                for (int i = 0; i < mCur2.getCount(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("autonum", mCur2.getString(0));
                    obj.put("closedate", mCur2.getString(1));
                    obj.put("vancode", mCur2.getString(2));
                    obj.put("schedulecode", mCur2.getString(3));
                    obj.put("makerid", mCur2.getString(4));
                    obj.put("createddate", mCur2.getString(5));
                    obj.put("paidparties", mCur2.getString(6));
                    obj.put("expenseentries", mCur2.getString(7));
                    js_array2.put(obj);
                    mCur2.moveToNext();
                }
                JSONArray js_array3 = new JSONArray();
                for (int i = 0; i < mCur3.getCount(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("endingkm", mCur3.getString(0));
                    obj.put("schedulecode", mCur3.getString(1));
                    js_array3.put(obj);
                    mCur3.moveToNext();
                }
                js_obj.put("JSonObject", js_array2);
                js_obj1.put("JSonObject", js_array3);
                jsonObj =  api.CashCloseDetails(js_obj.toString(),js_obj1.toString(),context);
                //Call Json parser functionality
                JSONParser parser = new JSONParser();
                //parse the json object to boolean
                List = parser.parseCashReport(jsonObj);
                dbadapter.close();
                if (List.size() >= 1) {
                    if(List.get(0).ScheduleCode.length>0){
                        for(int j=0;j<List.get(0).ScheduleCode.length;j++){
                            DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
                            dataBaseAdapter.open();
                            dataBaseAdapter.UpdateCashCloseFlag(List.get(0).ScheduleCode[j]);
                            dataBaseAdapter.close();
                        }
                    }

                }
            }
            catch (Exception e)
            {
                Log.e("AsyncCloseCashDetails : ", e.getMessage());
                if(dbadapter != null)
                    dbadapter.close();
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncCloseCashDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("AsyncCloseCashDetails : ", e.getMessage());
            if(dbadapter != null)
                dbadapter.close();
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncCloseCashDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }
    /********CUSTOMER DETAILS***********/
    public void AsyncCustomerDetails(){
        ArrayList<CustomerDatas> List = null;
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();
        DataBaseAdapter dbadapter = new DataBaseAdapter(context);
        try {
            JSONObject js_obj = new JSONObject();
            try {

                dbadapter.open();
                Cursor mCur2 = dbadapter.GetCustomerDatasDB();
                JSONArray js_array2 = new JSONArray();
                for (int i = 0; i < mCur2.getCount(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("autonum", mCur2.getString(0));
                    obj.put("customercode", mCur2.getString(1));
                    obj.put("refno", mCur2.getString(2));
                    obj.put("customername", mCur2.getString(3));
                    obj.put("customernametamil", mCur2.getString(4));
                    obj.put("address", mCur2.getString(5));
                    obj.put("areacode", mCur2.getString(6));
                    obj.put("emailid", mCur2.getString(7));
                    obj.put("mobileno", mCur2.getString(8));
                    obj.put("telephoneno", mCur2.getString(9));
                    obj.put("aadharno", mCur2.getString(10));
                    obj.put("gstin", mCur2.getString(11));
                    obj.put("status", mCur2.getString(12));
                    obj.put("makerid", mCur2.getString(13));
                    obj.put("createddate", mCur2.getString(14));
                    obj.put("updateddate", mCur2.getString(15));
                    obj.put("latitude", mCur2.getString(16));
                    obj.put("longitude", mCur2.getString(17));
                    obj.put("flag", mCur2.getString(18));
                    obj.put("schemeapplicable", mCur2.getString(19));
                    obj.put("uploaddocument", mCur2.getString(20));
                    obj.put("business_type", mCur2.getString(23));
                    obj.put("customertypecode", mCur2.getString(22));
                    obj.put("whatsappno", mCur2.getString(24));
                    obj.put("mobilenoverificationstatus", mCur2.getString(25));

                    js_array2.put(obj);
                    mCur2.moveToNext();
                }
                js_obj.put("JSonObject", js_array2);

                jsonObj =  api.CustomerDetails(js_obj.toString(),context);
                //Call Json parser functionality
                JSONParser parser = new JSONParser();
                //parse the json object to boolean
                List = parser.parseCustomerDataList(jsonObj);
                dbadapter.close();

                if (List.size() >= 1) {
                    if(List.get(0).CustomerCode.length>0){
                        for(int j=0;j<List.get(0).CustomerCode.length;j++){
                            DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
                            dataBaseAdapter.open();
                            dataBaseAdapter.UpdateCustomerFlag(List.get(0).CustomerCode[j]);
                            dataBaseAdapter.close();
                        }
                    }

                }
            }
            catch (Exception e)
            {
                Log.e("AsyncCustomerDetails : ", e.getMessage());
                if(dbadapter != null)
                    dbadapter.close();
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncCustomerDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("AsyncCustomerDetails : ", e.getMessage());
            if(dbadapter != null)
                dbadapter.close();
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncCustomerDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }
    /**********EXPENSE ACTIVITY*********/
    public void  AsyncExpenseDetails(){
        ArrayList<ExpenseDetails> List = null;
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();
        DataBaseAdapter dbadapter = new DataBaseAdapter(context);
        try {
            JSONObject js_obj = new JSONObject();
            try {

                dbadapter.open();
                Cursor mCur2 = dbadapter.GetExpensesDetailsDatasDB();
                JSONArray js_array2 = new JSONArray();
                for (int i = 0; i < mCur2.getCount(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("autonum", mCur2.getString(0));
                    obj.put("transactionno", mCur2.getString(1));
                    obj.put("transactiondate", mCur2.getString(2));
                    obj.put("expensesheadcode", mCur2.getString(3));
                    obj.put("amount", mCur2.getString(4));
                    obj.put("remarks", mCur2.getString(5));
                    obj.put("makerid", mCur2.getString(6));
                    obj.put("createdate", mCur2.getString(7));
                    obj.put("schedulecode", mCur2.getString(8));
                    obj.put("financialyearcode", mCur2.getString(9));
                    obj.put("vancode", mCur2.getString(10));
                    obj.put("flag", mCur2.getString(11));
                    js_array2.put(obj);
                    mCur2.moveToNext();
                }
                js_obj.put("JSonObject", js_array2);

                jsonObj =  api.ExpenseDetails(js_obj.toString(),context);
                //Call Json parser functionality
                JSONParser parser = new JSONParser();
                //parse the json object to boolean
                List = parser.parseExpenseDataList(jsonObj);
                dbadapter.close();

                if (List.size() >= 1) {
                    if(List.get(0).TransactionNo.length>0){
                        for(int j=0;j<List.get(0).TransactionNo.length;j++){
                            DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
                            dataBaseAdapter.open();
                            dataBaseAdapter.UpdateExpenseDetailsFlag(List.get(0).TransactionNo[j]);
                            dataBaseAdapter.close();
                        }
                    }

                }
            }
            catch (Exception e)
            {
                Log.e("AsyncExpenseDetails : ", e.getMessage());
                if(dbadapter != null)
                    dbadapter.close();
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncExpenseDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("AsyncExpenseDetails : ", e.getMessage());
            if(dbadapter != null)
                dbadapter.close();
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncExpenseDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }
    //Expebse cancel details
    @SuppressLint("LongLogTag")
    public void  AsyncCancelExpenseDetails(){
        ArrayList<ExpenseDetails> List = null;
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();
        DataBaseAdapter dbadapter = new DataBaseAdapter(context);
        try {
            JSONObject js_obj = new JSONObject();
            try {

                dbadapter.open();
                Cursor mCur2 = dbadapter.GetCancelExpensesDetailsDatasDB();
                JSONArray js_array2 = new JSONArray();
                for (int i = 0; i < mCur2.getCount(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("autonum", mCur2.getString(0));
                    obj.put("transactionno", mCur2.getString(1));
                    obj.put("transactiondate", mCur2.getString(2));
                    obj.put("expensesheadcode", mCur2.getString(3));
                    obj.put("amount", mCur2.getString(4));
                    obj.put("remarks", mCur2.getString(5));
                    obj.put("makerid", mCur2.getString(6));
                    obj.put("createdate", mCur2.getString(7));
                    obj.put("schedulecode", mCur2.getString(8));
                    obj.put("financialyearcode", mCur2.getString(9));
                    obj.put("vancode", mCur2.getString(10));
                    obj.put("flag", mCur2.getString(11));
                    js_array2.put(obj);
                    mCur2.moveToNext();
                }
                js_obj.put("JSonObject", js_array2);

                jsonObj =  api.DeleteExpenseDetails(js_obj.toString(),context);
                //Call Json parser functionality
                JSONParser parser = new JSONParser();
                //parse the json object to boolean
                List = parser.parseExpenseDataList(jsonObj);
                dbadapter.close();

                if (List.size() >= 1) {
                    if(List.get(0).TransactionNo.length>0){
                        for(int j=0;j<List.get(0).TransactionNo.length;j++){
                            DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
                            dataBaseAdapter.open();
                            dataBaseAdapter.DeleteExpenseDetailsFlag(List.get(0).TransactionNo[j]);
                            dataBaseAdapter.close();
                        }
                    }

                }
            }
            catch (Exception e)
            {
                Log.e("AsyncCancelExpenseDetails : ", e.getMessage());
                if(dbadapter != null)
                    dbadapter.close();
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncCancelExpenseDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("AsyncCancelExpenseDetails : ", e.getMessage());
            if(dbadapter != null)
                dbadapter.close();
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncCancelExpenseDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }
    /************next day requirement details*******/
    public  void AsyncOrderDetails(){
        ArrayList<ScheduleDatas> List = null;
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();
        DataBaseAdapter dbadapter = new DataBaseAdapter(context);
        try {
            JSONObject js_obj = new JSONObject();
            try {

                dbadapter.open();
                Cursor mCur2 = dbadapter.GetOrderDetailsDatasDB();
                JSONArray js_array2 = new JSONArray();
                for (int i = 0; i < mCur2.getCount(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("autonum", mCur2.getString(0));
                    obj.put("vancode", mCur2.getString(1));
                    obj.put("schedulecode", mCur2.getString(2));
                    obj.put("itemcode", mCur2.getString(3));
                    obj.put("qty", mCur2.getString(4));
                    obj.put("makerid", mCur2.getString(5));
                    obj.put("createddate", mCur2.getString(6));
                    obj.put("orderdate", mCur2.getString(7));
                    obj.put("flag", mCur2.getString(8));
                    obj.put("status", mCur2.getString(9));
                    js_array2.put(obj);
                    mCur2.moveToNext();
                }
                js_obj.put("JSonObject", js_array2);

                jsonObj =  api.OrderDetails(js_obj.toString(),context);
                //Call Json parser functionality
                JSONParser parser = new JSONParser();
                //parse the json object to boolean
                List = parser.parseOrderDataList(jsonObj);
                dbadapter.close();
                if (List.size() >= 1) {
                    if(List.get(0).ScheduleCode.length>0){
                        for(int j=0;j<List.get(0).ScheduleCode.length;j++){
                            DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
                            dataBaseAdapter.open();
                            dataBaseAdapter.UpdateOrderDetailsFlag(List.get(0).ScheduleCode[j]);
                            dataBaseAdapter.close();
                        }
                    }

                }
            }
            catch (Exception e)
            {
                Log.e("AsyncOrderDetails : ", e.getMessage());
                if(dbadapter != null)
                    dbadapter.close();
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncOrderDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("AsyncOrderDetails : ", e.getMessage());
            if(dbadapter != null)
                dbadapter.close();
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncOrderDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }

    }
    /***************** SALES DETAILS *********************************/
    public void  AsyncSalesDetails(){
        ArrayList<SalesSyncDatas> List = null;
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();
        DataBaseAdapter dbadapter = new DataBaseAdapter(context);
        try {
            JSONObject js_obj = new JSONObject();
            JSONObject js_salesobj = new JSONObject();
            JSONObject js_salesitemobj = new JSONObject();
            JSONObject js_stockobj = new JSONObject();
            try {

                dbadapter.open();
                Cursor mCur2 = dbadapter.GetCustomerDatasDB();
                Cursor mCursales = dbadapter.GetSalesDatasDB();
                Cursor mCursalesitems = dbadapter.GetSalesItemDatasDB();
                Cursor mCurStock = dbadapter.GetSalesStockTransactionDatasDB();
                JSONArray js_array2 = new JSONArray();
                JSONArray js_array3 = new JSONArray();
                JSONArray js_stockarray = new JSONArray();
                JSONArray js_array4 = new JSONArray();
                for (int i = 0; i < mCur2.getCount(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("autonum", mCur2.getString(0));
                    obj.put("customercode", mCur2.getString(1));
                    obj.put("refno", mCur2.getString(2));
                    obj.put("customername", mCur2.getString(3));
                    obj.put("customernametamil", mCur2.getString(4));
                    obj.put("address", mCur2.getString(5));
                    obj.put("areacode", mCur2.getString(6));
                    obj.put("emailid", mCur2.getString(7));
                    obj.put("mobileno", mCur2.getString(8));
                    obj.put("telephoneno", mCur2.getString(9));
                    obj.put("aadharno", mCur2.getString(10));
                    obj.put("gstin", mCur2.getString(11));
                    obj.put("status", mCur2.getString(12));
                    obj.put("makerid", mCur2.getString(13));
                    obj.put("createddate", mCur2.getString(14));
                    obj.put("updateddate", mCur2.getString(15));
                    obj.put("latitude", mCur2.getString(16));
                    obj.put("longitude", mCur2.getString(17));
                    obj.put("flag", mCur2.getString(18));
                    obj.put("schemeapplicable", mCur2.getString(19));
                    obj.put("uploaddocument", mCur2.getString(20));
                    obj.put("business_type", mCur2.getString(23));
                    obj.put("customertypecode", mCur2.getString(22));
                    obj.put("whatsappno", mCur2.getString(24));
                    obj.put("mobilenoverificationstatus", mCur2.getString(25));
                    js_array4.put(obj);
                    mCur2.moveToNext();
                }
                for (int i = 0; i < mCursales.getCount(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("autonum", mCursales.getString(0));
                    obj.put("companycode", mCursales.getString(1));
                    obj.put("vancode", mCursales.getString(2));
                    obj.put("transactionno", mCursales.getString(3));
                    obj.put("billno", mCursales.getString(4));
                    obj.put("refno", mCursales.getString(5));
                    obj.put("prefix", mCursales.getString(6));
                    obj.put("suffix", mCursales.getString(7));
                    obj.put("billdate", mCursales.getString(8));
                    obj.put("customercode", mCursales.getString(9));
                    obj.put("billtypecode", mCursales.getString(10));
                    obj.put("gstin", mCursales.getString(11));
                    obj.put("schedulecode", mCursales.getString(12));
                    obj.put("subtotal", mCursales.getDouble(13));
                    obj.put("discount", mCursales.getDouble(14));
                    obj.put("totaltaxamount", mCursales.getDouble(15));
                    obj.put("grandtotal", mCursales.getDouble(16));
                    obj.put("billcopystatus", mCursales.getString(17));
                    obj.put("cashpaidstatus", mCursales.getString(18));
                    obj.put("flag", mCursales.getString(19));
                    obj.put("makerid", mCursales.getString(20));
                    obj.put("createddate", mCursales.getString(21));
                    obj.put("updateddate", mCursales.getString(22));
                    obj.put("bitmapimage", mCursales.getString(23));
                    obj.put("financialyearcode", mCursales.getString(24));
                    obj.put("remarks", mCursales.getString(25));
                    obj.put("bookingno", mCursales.getString(26));
                    obj.put("salestime", mCursales.getString(29));
                    obj.put("beforeroundoff", mCursales.getString(30));
                    obj.put("lunchflag", mCursales.getString(31));
                    String einvoiceurl = "";
                    if (!mCursales.isNull(33))
                        einvoiceurl = mCursales.getString(33);
                    obj.put("einvoiceurl",einvoiceurl);
                    String irn_no = "";
                    if (!mCursales.isNull(34))
                        irn_no = mCursales.getString(34);
                    obj.put("irn_no", irn_no);
                    String ack_no = "";
                    if (!mCursales.isNull(35))
                        ack_no = mCursales.getString(35);
                    obj.put("ack_no", ack_no);
                    String ackdate = "";
                    if (!mCursales.isNull(36))
                        ackdate = mCursales.getString(36);
                    obj.put("ackdate", ackdate);
                    String einvoice_status = "";
                    if (!mCursales.isNull(37))
                        einvoice_status = mCursales.getString(37);
                    obj.put("einvoice_status", einvoice_status);
                    String einvoiceresponse = "";
                    if (!mCursales.isNull(38))
                        einvoiceresponse = mCursales.getString(38);
                    obj.put("einvoiceresponse", einvoiceresponse);
                    String einvoiceqrcodeurl = "";
                    if (!mCursales.isNull(39))
                        einvoiceqrcodeurl = mCursales.getString(39);
                    obj.put("einvoiceqrcodeurl", einvoiceqrcodeurl);
                    String ratediscount = "";
                    if (!mCursales.isNull(40))
                        ratediscount = mCursales.getString(40);
                    obj.put("ratediscount", ratediscount);
                    String schemeapplicable = "";
                    if (!mCursales.isNull(41))
                        schemeapplicable = mCursales.getString(41);
                    obj.put("schemeapplicable", schemeapplicable);

                    String latlong = "";
                    if (!mCursales.isNull(42))
                        latlong = mCursales.getString(42);
                    obj.put("latlong", latlong);

                    String orderTransNo = "";
                    if (!mCursales.isNull(43))
                        orderTransNo = mCursales.getString(43);
                    obj.put("ordertransactionno", orderTransNo);

                    js_array2.put(obj);
                    mCursales.moveToNext();
                }
                for (int i = 0; i < mCursalesitems.getCount(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("autonum", mCursalesitems.getString(0));
                    obj.put("transactionno", mCursalesitems.getString(1));
                    obj.put("companycode", mCursalesitems.getString(2));
                    obj.put("itemcode", mCursalesitems.getString(3));
                    obj.put("qty", mCursalesitems.getString(4));
                    obj.put("weight", mCursalesitems.getString(5));
                    obj.put("price", mCursalesitems.getString(6));
                    obj.put("discount", mCursalesitems.getDouble(7));
                    obj.put("amount", mCursalesitems.getDouble(8));
                    obj.put("cgst", mCursalesitems.getDouble(9));
                    obj.put("sgst", mCursalesitems.getDouble(10));
                    obj.put("igst", mCursalesitems.getDouble(11));
                    obj.put("cgstamt", mCursalesitems.getDouble(12));
                    obj.put("sgstamt", mCursalesitems.getDouble(13));
                    obj.put("igstamt", mCursalesitems.getDouble(14));
                    obj.put("freeitemstatus", mCursalesitems.getString(15));
                    obj.put("makerid", mCursalesitems.getString(16));
                    obj.put("createddate", mCursalesitems.getString(17));
                    obj.put("updateddate", mCursalesitems.getString(18));
                    obj.put("bookingno", mCursalesitems.getString(19));
                    obj.put("financialyearcode", mCursalesitems.getString(20));
                    obj.put("vancode", mCursalesitems.getString(21));
                    obj.put("flag", mCursalesitems.getString(22));
                    obj.put("ratediscount", mCursalesitems.getString(23));

                    String schemeapplicable = "";
                    if (!mCursalesitems.isNull(24))
                        schemeapplicable = mCursalesitems.getString(24);
                    obj.put("schemeapplicable", schemeapplicable);
                    obj.put("orgprice", mCursalesitems.getString(25));
                    js_array3.put(obj);
                    mCursalesitems.moveToNext();
                }

                for (int i = 0; i < mCurStock.getCount(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("transactionno", mCurStock.getString(0));
                    obj.put("transactiondate", mCurStock.getString(1));
                    obj.put("vancode", mCurStock.getString(2));
                    obj.put("itemcode", mCurStock.getString(3));
                    obj.put("inward", mCurStock.getString(4));
                    obj.put("outward", mCurStock.getString(5));
                    obj.put("type", mCurStock.getString(6));
                    obj.put("refno", mCurStock.getString(7));
                    obj.put("createddate", mCurStock.getString(8));
                    obj.put("flag", mCurStock.getString(9));
                    obj.put("companycode", mCurStock.getString(10));
                    obj.put("financialyearcode", mCurStock.getString(11));
                    obj.put("autonum", mCurStock.getString(12));

                    js_stockarray.put(obj);
                    mCurStock.moveToNext();
                }
                js_obj.put("JSonObject", js_array4);
                js_salesobj.put("JSonObject", js_array2);
                js_salesitemobj.put("JSonObject", js_array3);
                js_stockobj.put("JSonObject",js_stockarray);
                jsonObj =  api.SalesDetails(js_salesobj.toString(),js_salesitemobj.toString(),js_stockobj.toString(),js_obj.toString(),context);
                //Call Json parser functionality
                JSONParser parser = new JSONParser();
                //parse the json object to boolean
                List = parser.parseSalesDataList(jsonObj);
                dbadapter.close();

                DataBaseAdapter objdatabaseadapter = new DataBaseAdapter(context);
                try {
                    objdatabaseadapter.open();
                    if (List.size() >= 1) {
                        if (List.get(0).TransactionNo.length > 0) {
                            for (int j = 0; j < List.get(0).TransactionNo.length; j++) {
                                objdatabaseadapter.UpdateSalesFlag(List.get(0).TransactionNo[j]);
                            }
                        }
                        if (List.get(0).SalesItemTransactionNo.length > 0) {
                            for (int j = 0; j < List.get(0).SalesItemTransactionNo.length; j++) {
                                objdatabaseadapter.UpdateSalesItemFlag(List.get(0).SalesItemTransactionNo[j]);
                            }
                        }
                        if (List.get(0).StockTransactionNo.length > 0) {
                            for (int j = 0; j < List.get(0).StockTransactionNo.length; j++) {
                                String[] getArr = List.get(0).StockTransactionNo[j].split("~");
                                objdatabaseadapter.UpdateSalesStockTransactionFlag(getArr[0],getArr[1]);
                            }
                        }
                    }
                }catch (Exception e) {
                    // TODO Auto-generated catch block
                    Log.e("AsyncSalesDetails : ", e.getMessage());
                    if(dbadapter != null)
                        dbadapter.close();
                    if(objdatabaseadapter!=null)
                        objdatabaseadapter.close();
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncSalesDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                } finally {
                    if(objdatabaseadapter!=null)
                        objdatabaseadapter.close();
                }
            }
            catch (Exception e)
            {
                Log.e("AsyncSalesDetails : ", e.getMessage());
                if(dbadapter != null)
                    dbadapter.close();
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncSalesDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("AsyncSalesDetails : ", e.getMessage());
            if(dbadapter != null)
                dbadapter.close();
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncSalesDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }
    //Sales  update receipt and bill wise
    @SuppressLint("LongLogTag")
    public void  AsyncUpdateSalesReceiptDetails(){
        ArrayList<SalesSyncDatas> List = null;
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();
        DataBaseAdapter dbadapter = new DataBaseAdapter(context);
        try {
            JSONObject js_salesobj = new JSONObject();
            try {

                dbadapter.open();
                Cursor mCursales = dbadapter.GetSalesReceiptDatasDB();
                JSONArray js_array2 = new JSONArray();
                for (int i = 0; i < mCursales.getCount(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("autonum", mCursales.getString(0));
                    obj.put("companycode", mCursales.getString(1));
                    obj.put("vancode", mCursales.getString(2));
                    obj.put("transactionno", mCursales.getString(3));
                    obj.put("billno", mCursales.getString(4));
                    obj.put("refno", mCursales.getString(5));
                    obj.put("prefix", mCursales.getString(6));
                    obj.put("suffix", mCursales.getString(7));
                    obj.put("billdate", mCursales.getString(8));
                    obj.put("customercode", mCursales.getString(9));
                    obj.put("billtypecode", mCursales.getString(10));
                    obj.put("gstin", mCursales.getString(11));
                    obj.put("schedulecode", mCursales.getString(12));
                    obj.put("subtotal", mCursales.getDouble(13));
                    obj.put("discount", mCursales.getDouble(14));
                    obj.put("totaltaxamount", mCursales.getDouble(15));
                    obj.put("grandtotal", mCursales.getDouble(16));
                    obj.put("billcopystatus", mCursales.getString(17));
                    obj.put("cashpaidstatus", mCursales.getString(18));
                    obj.put("flag", mCursales.getString(19));
                    obj.put("makerid", mCursales.getString(20));
                    obj.put("createddate", mCursales.getString(21));
                    obj.put("updateddate", mCursales.getString(22));
                    obj.put("bitmapimage", mCursales.getString(23));
                    obj.put("financialyearcode", mCursales.getString(24));
                    obj.put("remarks", mCursales.getString(25));
                    obj.put("bookingno", mCursales.getString(26));

                    js_array2.put(obj);
                    mCursales.moveToNext();
                }

                js_salesobj.put("JSonObject", js_array2);

                jsonObj =  api.SalesReceiptDetails(js_salesobj.toString(),context);
                //Call Json parser functionality
                JSONParser parser = new JSONParser();
                //parse the json object to boolean
                List = parser.parseSalesReceiptDataList(jsonObj);
                dbadapter.close();
                DataBaseAdapter objdatabaseadapter = new DataBaseAdapter(context);
                try {
                    objdatabaseadapter.open();
                    if (List.size() >= 1) {
                        if (List.get(0).TransactionNo.length > 0) {
                            for (int j = 0; j < List.get(0).TransactionNo.length; j++) {
                                // objdatabaseadapter.UpdateSalesRecieptFlag(List.get(0).TransactionNo[j]);
                            }
                        }
                    }
                }catch (Exception e) {
                    // TODO Auto-generated catch block
                    Log.e("AsyncUpdateSalesReceiptDetails : ", e.getMessage());
                    if(dbadapter != null)
                        dbadapter.close();
                    if(objdatabaseadapter!=null)
                        objdatabaseadapter.close();
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncUpdateSalesReceiptDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                } finally {
                    if(objdatabaseadapter!=null)
                        objdatabaseadapter.close();
                }
            }
            catch (Exception e)
            {
                Log.e("AsyncUpdateSalesReceiptDetails : ", e.getMessage());
                if(dbadapter != null)
                    dbadapter.close();
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncUpdateSalesReceiptDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("AsyncUpdateSalesReceiptDetails : ", e.getMessage());
            if(dbadapter != null)
                dbadapter.close();
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncUpdateSalesReceiptDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }
    //Sales Close
    @SuppressLint("LongLogTag")
    public void AsyncCloseSalesDetails(){
        ArrayList<ScheduleDatas> List = null;
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();
        DataBaseAdapter dbadapter = new DataBaseAdapter(context);
        try {
            JSONObject js_obj = new JSONObject();
            try {

                dbadapter.open();
                Cursor mCur2 = dbadapter.GetCloseSalesitemDatasDB();

                JSONArray js_array2 = new JSONArray();
                for (int i = 0; i < mCur2.getCount(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("autonum", mCur2.getString(0));
                    obj.put("closedate", mCur2.getString(1));
                    obj.put("vancode", mCur2.getString(2));
                    obj.put("schedulecode", mCur2.getString(3));
                    obj.put("makerid", mCur2.getString(4));
                    obj.put("createddate", mCur2.getString(5));
                    js_array2.put(obj);
                    mCur2.moveToNext();
                }

                js_obj.put("JSonObject", js_array2);

                jsonObj =  api.SalesCloseDetails(js_obj.toString(),context);
                //Call Json parser functionality
                JSONParser parser = new JSONParser();
                //parse the json object to boolean
                List = parser.parseCashReport(jsonObj);
                dbadapter.close();
                if (List.size() >= 1) {
                    if(List.get(0).ScheduleCode.length>0){
                        for(int j=0;j<List.get(0).ScheduleCode.length;j++){
                            DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
                            dataBaseAdapter.open();
                            dataBaseAdapter.UpdateSalesCloseFlag(List.get(0).ScheduleCode[j]);
                            dataBaseAdapter.close();
                        }
                    }

                }
            }
            catch (Exception e)
            {
                Log.e("AsyncCloseSalesDetails : ", e.getMessage());
                if(dbadapter != null)
                    dbadapter.close();
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncCloseSalesDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("AsyncCloseSalesDetails : ", e.getMessage());
            if(dbadapter != null)
                dbadapter.close();
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncCloseSalesDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }
    //Sales Cancel
    @SuppressLint("LongLogTag")
    public void AsyncSalesCancelDetails(){
        ArrayList<SalesSyncDatas> List = null;
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();
        DataBaseAdapter dbadapter = new DataBaseAdapter(context);
        try {
            JSONObject js_salesobj = new JSONObject();
            JSONObject js_salesitemobj = new JSONObject();
            JSONObject js_stockobj = new JSONObject();
            try {

                dbadapter.open();
                Cursor mCursales = dbadapter.GetSalesCancelDatasDB();
                Cursor mCursalesitems = dbadapter.GetSalesCancelItemDatasDB();
                Cursor mCurStock = dbadapter.GetCancelStockTransactionDatasDB();
                JSONArray js_array2 = new JSONArray();
                JSONArray js_array3 = new JSONArray();
                JSONArray js_stockarray = new JSONArray();
                for (int i = 0; i < mCursales.getCount(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("autonum", mCursales.getString(0));
                    obj.put("companycode", mCursales.getString(1));
                    obj.put("vancode", mCursales.getString(2));
                    obj.put("transactionno", mCursales.getString(3));
                    obj.put("billno", mCursales.getString(4));
                    obj.put("refno", mCursales.getString(5));
                    obj.put("prefix", mCursales.getString(6));
                    obj.put("suffix", mCursales.getString(7));
                    obj.put("billdate", mCursales.getString(8));
                    obj.put("customercode", mCursales.getString(9));
                    obj.put("billtypecode", mCursales.getString(10));
                    obj.put("gstin", mCursales.getString(11));
                    obj.put("schedulecode", mCursales.getString(12));
                    obj.put("subtotal", mCursales.getDouble(13));
                    obj.put("discount", mCursales.getDouble(14));
                    obj.put("totaltaxamount", mCursales.getDouble(15));
                    obj.put("grandtotal", mCursales.getDouble(16));
                    obj.put("billcopystatus", mCursales.getString(17));
                    obj.put("cashpaidstatus", mCursales.getString(18));
                    obj.put("flag", mCursales.getString(19));
                    obj.put("makerid", mCursales.getString(20));
                    obj.put("createddate", mCursales.getString(21));
                    obj.put("updateddate", mCursales.getString(22));
                    obj.put("bitmapimage", mCursales.getString(23));
                    obj.put("financialyearcode", mCursales.getString(24));
                    obj.put("remarks", mCursales.getString(25));
                    obj.put("bookingno", mCursales.getString(26));

                    js_array2.put(obj);
                    mCursales.moveToNext();
                }

                for (int i = 0; i < mCursalesitems.getCount(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("autonum", mCursalesitems.getString(0));
                    obj.put("transactionno", mCursalesitems.getString(1));
                    obj.put("companycode", mCursalesitems.getString(2));
                    obj.put("itemcode", mCursalesitems.getString(3));
                    obj.put("qty", mCursalesitems.getString(4));
                    obj.put("weight", mCursalesitems.getString(5));
                    obj.put("price", mCursalesitems.getString(6));
                    obj.put("discount", mCursalesitems.getDouble(7));
                    obj.put("amount", mCursalesitems.getDouble(8));
                    obj.put("cgst", mCursalesitems.getDouble(9));
                    obj.put("sgst", mCursalesitems.getDouble(10));
                    obj.put("igst", mCursalesitems.getDouble(11));
                    obj.put("cgstamt", mCursalesitems.getDouble(12));
                    obj.put("sgstamt", mCursalesitems.getDouble(13));
                    obj.put("igstamt", mCursalesitems.getDouble(14));
                    obj.put("freeitemstatus", mCursalesitems.getString(15));
                    obj.put("makerid", mCursalesitems.getString(16));
                    obj.put("createddate", mCursalesitems.getString(17));
                    obj.put("updateddate", mCursalesitems.getString(18));
                    obj.put("bookingno", mCursalesitems.getString(19));
                    obj.put("financialyearcode", mCursalesitems.getString(20));
                    obj.put("vancode", mCursalesitems.getString(21));
                    obj.put("flag", mCursalesitems.getString(22));

                    js_array3.put(obj);
                    mCursalesitems.moveToNext();
                }

                for (int i = 0; i < mCurStock.getCount(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("transactionno", mCurStock.getString(0));
                    obj.put("transactiondate", mCurStock.getString(1));
                    obj.put("vancode", mCurStock.getString(2));
                    obj.put("itemcode", mCurStock.getString(3));
                    obj.put("inward", mCurStock.getString(4));
                    obj.put("outward", mCurStock.getString(5));
                    obj.put("type", mCurStock.getString(6));
                    obj.put("refno", mCurStock.getString(7));
                    obj.put("createddate", mCurStock.getString(8));
                    obj.put("flag", mCurStock.getString(9));
                    obj.put("companycode", mCurStock.getString(10));
                    obj.put("financialyearcode", mCurStock.getString(12));
                    obj.put("autonum", mCurStock.getString(13));
                    js_stockarray.put(obj);
                    mCurStock.moveToNext();
                }
                js_salesobj.put("JSonObject", js_array2);
                js_salesitemobj.put("JSonObject", js_array3);
                js_stockobj.put("JSonObject",js_stockarray);

                jsonObj =  api.SalesCancelDetails(js_salesobj.toString(), js_salesitemobj.toString(), js_stockobj.toString(),context);
                //Call Json parser functionality
                JSONParser parser = new JSONParser();
                //parse the json object to boolean
                List = parser.parseSalesCancelDataList(jsonObj);
                dbadapter.close();
                DataBaseAdapter objdatabaseadapter = new DataBaseAdapter(context);
                try {
                    objdatabaseadapter.open();
                    if (List.size() >= 1) {
                        if (List.get(0).TransactionNo.length > 0) {
                            for (int j = 0; j < List.get(0).TransactionNo.length; j++) {
                                objdatabaseadapter.UpdateCancelSalesFlag(List.get(0).TransactionNo[j]);
                            }
                        }
                        if (List.get(0).StockTransactionNo.length > 0) {
                            for (int j = 0; j < List.get(0).StockTransactionNo.length; j++) {
                                objdatabaseadapter.UpdateCancelSalesStockTransactionFlag(List.get(0).StockTransactionNo[j]);
                            }
                        }
                    }
                }catch (Exception e) {
                    // TODO Auto-generated catch block
                    Log.e("AsyncSalesCancelDetails : ", e.getMessage());
                    if(dbadapter != null)
                        dbadapter.close();
                    if(objdatabaseadapter!=null)
                        objdatabaseadapter.close();
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncSalesCancelDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                } finally {
                    if(objdatabaseadapter!=null)
                        objdatabaseadapter.close();
                }
            }
            catch (Exception e)
            {
                Log.e("AsyncSalesCancelDetails : ", e.getMessage());
                if(dbadapter != null)
                    dbadapter.close();
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncSalesCancelDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("AsyncSalesCancelDetails : ", e.getMessage());
            if(dbadapter != null)
                dbadapter.close();
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncSalesCancelDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }



    /***************** Sales Return DETAILS *********************************/
    @SuppressLint("LongLogTag")
    public void AsyncSalesReturnDetails(){
        ArrayList<SalesSyncDatas> List = null;
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();
        DataBaseAdapter dbadapter = new DataBaseAdapter(context);
        try {
            JSONObject js_salesobj = new JSONObject();
            JSONObject js_salesitemobj = new JSONObject();
            JSONObject js_stockobj = new JSONObject();
            try {

                dbadapter.open();
                Cursor mCursales = dbadapter.GetSalesReturnDatasDB();
                Cursor mCursalesitems = dbadapter.GetSalesReturnItemDatasDB();
                Cursor mCurStock = dbadapter.GetStockTransactionDatasDB();
                JSONArray js_array2 = new JSONArray();
                JSONArray js_array3 = new JSONArray();
                JSONArray js_stockarray = new JSONArray();
                for (int i = 0; i < mCursales.getCount(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("autonum", mCursales.getString(0));
                    obj.put("companycode", mCursales.getString(1));
                    obj.put("vancode", mCursales.getString(2));
                    obj.put("transactionno", mCursales.getString(3));
                    obj.put("billno", mCursales.getString(4));
                    obj.put("refno", mCursales.getString(5));
                    obj.put("prefix", mCursales.getString(6));
                    obj.put("suffix", mCursales.getString(7));
                    obj.put("billdate", mCursales.getString(8));
                    obj.put("customercode", mCursales.getString(9));
                    obj.put("billtypecode", mCursales.getString(10));
                    obj.put("gstin", mCursales.getString(11));
                    obj.put("schedulecode", mCursales.getString(12));
                    obj.put("subtotal", mCursales.getDouble(13));
                    obj.put("discount", mCursales.getDouble(14));
                    obj.put("totaltaxamount", mCursales.getDouble(15));
                    obj.put("grandtotal", mCursales.getDouble(16));
                    obj.put("billcopystatus", mCursales.getString(17));
                    obj.put("cashpaidstatus", mCursales.getString(18));
                    obj.put("flag", mCursales.getString(19));
                    obj.put("makerid", mCursales.getString(20));
                    obj.put("createddate", mCursales.getString(21));
                    obj.put("updateddate", mCursales.getString(22));
                    obj.put("bitmapimage", mCursales.getString(23));
                    obj.put("financialyearcode", mCursales.getString(24));
                    obj.put("remarks", mCursales.getString(25));
                    obj.put("bookingno", mCursales.getString(26));
                    obj.put("salestime", mCursales.getString(28));
                    obj.put("beforeroundoff", mCursales.getString(29));
                    js_array2.put(obj);
                    mCursales.moveToNext();
                }
                for (int i = 0; i < mCursalesitems.getCount(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("autonum", mCursalesitems.getString(0));
                    obj.put("transactionno", mCursalesitems.getString(1));
                    obj.put("companycode", mCursalesitems.getString(2));
                    obj.put("itemcode", mCursalesitems.getString(3));
                    obj.put("qty", mCursalesitems.getString(4));
                    obj.put("weight", mCursalesitems.getString(5));
                    obj.put("price", mCursalesitems.getString(6));
                    obj.put("discount", mCursalesitems.getDouble(7));
                    obj.put("amount", mCursalesitems.getDouble(8));
                    obj.put("cgst", mCursalesitems.getDouble(9));
                    obj.put("sgst", mCursalesitems.getDouble(10));
                    obj.put("igst", mCursalesitems.getDouble(11));
                    obj.put("cgstamt", mCursalesitems.getDouble(12));
                    obj.put("sgstamt", mCursalesitems.getDouble(13));
                    obj.put("igstamt", mCursalesitems.getDouble(14));
                    obj.put("freeitemstatus", mCursalesitems.getString(15));
                    obj.put("makerid", mCursalesitems.getString(16));
                    obj.put("createddate", mCursalesitems.getString(17));
                    obj.put("updateddate", mCursalesitems.getString(18));
                    obj.put("bookingno", mCursalesitems.getString(19));
                    obj.put("financialyearcode", mCursalesitems.getString(20));
                    obj.put("vancode", mCursalesitems.getString(21));
                    obj.put("flag", mCursalesitems.getString(22));

                    js_array3.put(obj);
                    mCursalesitems.moveToNext();
                }

                for (int i = 0; i < mCurStock.getCount(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("transactionno", mCurStock.getString(0));
                    obj.put("transactiondate", mCurStock.getString(1));
                    obj.put("vancode", mCurStock.getString(2));
                    obj.put("itemcode", mCurStock.getString(3));
                    obj.put("inward", mCurStock.getString(4));
                    obj.put("outward", mCurStock.getString(5));
                    obj.put("type", mCurStock.getString(6));
                    obj.put("refno", mCurStock.getString(7));
                    obj.put("createddate", mCurStock.getString(8));
                    obj.put("flag", mCurStock.getString(9));
                    obj.put("companycode", mCurStock.getString(10));
                    obj.put("financialyearcode", mCurStock.getString(11));
                    obj.put("autonum", mCurStock.getString(12));
                    js_stockarray.put(obj);
                    mCurStock.moveToNext();
                }
                js_salesobj.put("JSonObject", js_array2);
                js_salesitemobj.put("JSonObject", js_array3);
                js_stockobj.put("JSonObject",js_stockarray);

                jsonObj =  api.SalesReturnDetails(js_salesobj.toString(),js_salesitemobj.toString(),js_stockobj.toString(),context);
                //Call Json parser functionality
                JSONParser parser = new JSONParser();
                //parse the json object to boolean
                List = parser.parseSalesDataList(jsonObj);
                dbadapter.close();
                DataBaseAdapter objdatabaseadapter = new DataBaseAdapter(context);
                try {
                    objdatabaseadapter.open();
                    if (List.size() >= 1) {
                        if (List.get(0).TransactionNo.length > 0) {
                            for (int j = 0; j < List.get(0).TransactionNo.length; j++) {
                                objdatabaseadapter.UpdateSalesReturnFlag(List.get(0).TransactionNo[j]);
                            }
                        }
                        if (List.get(0).SalesItemTransactionNo.length > 0) {
                            for (int j = 0; j < List.get(0).SalesItemTransactionNo.length; j++) {
                                objdatabaseadapter.UpdateSalesReturnItemFlag(List.get(0).SalesItemTransactionNo[j]);
                            }
                        }
                        if (List.get(0).StockTransactionNo.length > 0) {
                            for (int j = 0; j < List.get(0).StockTransactionNo.length; j++) {
                                objdatabaseadapter.UpdateStockTransactionFlag(List.get(0).StockTransactionNo[j]);
                            }
                        }
                    }
                }catch (Exception e) {
                    // TODO Auto-generated catch block
                    Log.e("AsyncSalesReturnDetails : ", e.getMessage());
                    if(dbadapter != null)
                        dbadapter.close();
                    if(objdatabaseadapter!=null)
                        objdatabaseadapter.close();
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncSalesReturnDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                } finally {
                    if(objdatabaseadapter!=null)
                        objdatabaseadapter.close();
                }
            }
            catch (Exception e)
            {
                Log.e("AsyncSalesReturnDetails : ", e.getMessage());
                if(dbadapter != null)
                    dbadapter.close();
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncSalesReturnDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("AsyncSalesReturnDetails : ", e.getMessage());
            if(dbadapter != null)
                dbadapter.close();
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncSalesReturnDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }
    //Cancel sales return
    @SuppressLint("LongLogTag")
    public void AsyncSalesReturnCancelDetails(){
        ArrayList<SalesSyncDatas> List = null;
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();
        DataBaseAdapter dbadapter = new DataBaseAdapter(context);
        try {
            JSONObject js_salesobj = new JSONObject();
            JSONObject js_stockobj = new JSONObject();
            try {

                dbadapter.open();
                Cursor mCursales = dbadapter.GetSalesReturnCancelDatasDB();
                Cursor mCurStock = dbadapter.GetCancelReturnStockTransactionDatasDB();
                JSONArray js_array2 = new JSONArray();
                JSONArray js_stockarray = new JSONArray();
                for (int i = 0; i < mCursales.getCount(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("autonum", mCursales.getString(0));
                    obj.put("companycode", mCursales.getString(1));
                    obj.put("vancode", mCursales.getString(2));
                    obj.put("transactionno", mCursales.getString(3));
                    obj.put("billno", mCursales.getString(4));
                    obj.put("refno", mCursales.getString(5));
                    obj.put("prefix", mCursales.getString(6));
                    obj.put("suffix", mCursales.getString(7));
                    obj.put("billdate", mCursales.getString(8));
                    obj.put("customercode", mCursales.getString(9));
                    obj.put("billtypecode", mCursales.getString(10));
                    obj.put("gstin", mCursales.getString(11));
                    obj.put("schedulecode", mCursales.getString(12));
                    obj.put("subtotal", mCursales.getDouble(13));
                    obj.put("discount", mCursales.getDouble(14));
                    obj.put("totaltaxamount", mCursales.getDouble(15));
                    obj.put("grandtotal", mCursales.getDouble(16));
                    obj.put("billcopystatus", mCursales.getString(17));
                    obj.put("cashpaidstatus", mCursales.getString(18));
                    obj.put("flag", mCursales.getString(19));
                    obj.put("makerid", mCursales.getString(20));
                    obj.put("createddate", mCursales.getString(21));
                    obj.put("updateddate", mCursales.getString(22));
                    obj.put("bitmapimage", mCursales.getString(23));
                    obj.put("financialyearcode", mCursales.getString(24));
                    obj.put("remarks", mCursales.getString(25));
                    obj.put("bookingno", mCursales.getString(26));

                    js_array2.put(obj);
                    mCursales.moveToNext();
                }

                for (int i = 0; i < mCurStock.getCount(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("transactionno", mCurStock.getString(0));
                    obj.put("transactiondate", mCurStock.getString(1));
                    obj.put("vancode", mCurStock.getString(2));
                    obj.put("itemcode", mCurStock.getString(3));
                    obj.put("inward", mCurStock.getString(4));
                    obj.put("outward", mCurStock.getString(5));
                    obj.put("type", mCurStock.getString(6));
                    obj.put("refno", mCurStock.getString(7));
                    obj.put("createddate", mCurStock.getString(8));
                    obj.put("flag", mCurStock.getString(9));
                    obj.put("companycode", mCurStock.getString(10));
                    obj.put("financialyearcode", mCurStock.getString(11));
                    obj.put("autonum", mCurStock.getString(12));
                    js_stockarray.put(obj);
                    mCurStock.moveToNext();
                }
                js_salesobj.put("JSonObject", js_array2);
                js_stockobj.put("JSonObject",js_stockarray);

                jsonObj =  api.SalesReturnCancelDetails(js_salesobj.toString(),js_stockobj.toString(),context);
                //Call Json parser functionality
                JSONParser parser = new JSONParser();
                //parse the json object to boolean
                List = parser.parseSalesCancelDataList(jsonObj);
                dbadapter.close();
                DataBaseAdapter objdatabaseadapter = new DataBaseAdapter(context);
                try {
                    objdatabaseadapter.open();
                    if (List.size() >= 1) {
                        if (List.get(0).TransactionNo.length > 0) {
                            for (int j = 0; j < List.get(0).TransactionNo.length; j++) {
                                objdatabaseadapter.UpdateCancelSalesReturnFlag(List.get(0).TransactionNo[j]);
                            }
                        }
                        if (List.get(0).StockTransactionNo.length > 0) {
                            for (int j = 0; j < List.get(0).StockTransactionNo.length; j++) {
                                objdatabaseadapter.UpdateCancelSalesReturnStockTransactionFlag(List.get(0).StockTransactionNo[j]);
                            }
                        }
                    }
                }catch (Exception e) {
                    // TODO Auto-generated catch block
                    Log.e("AsyncSalesReturnCancelDetails : ", e.getMessage());
                    if(dbadapter != null)
                        dbadapter.close();
                    if(objdatabaseadapter!=null)
                        objdatabaseadapter.close();
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncSalesReturnCancelDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                } finally {
                    if(objdatabaseadapter!=null)
                        objdatabaseadapter.close();
                }
            }
            catch (Exception e)
            {
                Log.e("AsyncSalesReturnCancelDetails : ", e.getMessage());
                if(dbadapter != null)
                    dbadapter.close();
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncSalesReturnCancelDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("AsyncSalesReturnCancelDetails : ", e.getMessage());
            if(dbadapter != null)
                dbadapter.close();
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncSalesReturnCancelDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }
    /***************Receipt detils********/
    public void AsyncReceiptDetails(){
        ArrayList<ReceiptTransactionDetails> List = null;
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();
        DataBaseAdapter dbadapter = new DataBaseAdapter(context);
        try {
            JSONObject js_obj = new JSONObject();
            try {

                dbadapter.open();
                Cursor mCur2 = dbadapter.GetReceiptDetailsDatasDB();
                JSONArray js_array2 = new JSONArray();
                for (int i = 0; i < mCur2.getCount(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("autonum", mCur2.getString(0));
                    obj.put("transactionno", mCur2.getString(1));
                    obj.put("receiptdate", mCur2.getString(2));
                    obj.put("prefix", mCur2.getString(3));
                    obj.put("suffix", mCur2.getString(4));
                    obj.put("voucherno", mCur2.getString(5));
                    obj.put("refno", mCur2.getString(6));
                    obj.put("companycode", mCur2.getString(7));
                    obj.put("vancode", mCur2.getString(8));
                    obj.put("customercode", mCur2.getString(9));
                    obj.put("schedulecode", mCur2.getString(10));
                    obj.put("receiptremarkscode", mCur2.getString(11));
                    obj.put("receiptmode", mCur2.getString(12));
                    obj.put("chequerefno", mCur2.getString(13));
                    obj.put("amount", mCur2.getDouble(14));
                    obj.put("makerid", mCur2.getString(15));
                    obj.put("createddate", mCur2.getString(16));
                    obj.put("financialyearcode", mCur2.getString(17));
                    obj.put("flag", mCur2.getString(18));
                    obj.put("note", mCur2.getString(19));
                    obj.put("receipttime", mCur2.getString(21));
                    obj.put("chequebankname", mCur2.getString(22));
                    obj.put("chequedate", mCur2.getString(23));
                    obj.put("transactionid", mCur2.getString(24));
                    obj.put("venderid", mCur2.getString(25));
                    obj.put("type", mCur2.getString(26));

                    js_array2.put(obj);
                    mCur2.moveToNext();
                }
                js_obj.put("JSonObject", js_array2);

                jsonObj =  api.ReceiptDetails(js_obj.toString(),context);
                //Call Json parser functionality
                JSONParser parser = new JSONParser();
                //parse the json object to boolean
                List = parser.parseReceiptDataList(jsonObj);
                dbadapter.close();
                if (List.size() >= 1) {
                    if(List.get(0).TransactionNo.length>0){
                        for(int j=0;j<List.get(0).TransactionNo.length;j++){
                            DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
                            dataBaseAdapter.open();
                            dataBaseAdapter.UpdateReceiptDetailsFlag(List.get(0).TransactionNo[j]);
                            dataBaseAdapter.close();
                        }
                    }

                }
            }
            catch (Exception e)
            {
                Log.e("AsyncReceiptDetails : ", e.getMessage());
                if(dbadapter != null)
                    dbadapter.close();
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncReceiptDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("AsyncReceiptDetails : ", e.getMessage());
            if(dbadapter != null)
                dbadapter.close();
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncReceiptDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }
    //Receipt cancel details
    @SuppressLint("LongLogTag")
    public  void  AsyncCancelReceiptDetails(){
        ArrayList<ReceiptTransactionDetails> List = null;
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();
        DataBaseAdapter dbadapter = new DataBaseAdapter(context);
        try {
            JSONObject js_obj = new JSONObject();
            try {

                dbadapter.open();
                Cursor mCur2 = dbadapter.GetCancelReceiptDetailsDatasDB();
                JSONArray js_array2 = new JSONArray();
                for (int i = 0; i < mCur2.getCount(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("autonum", mCur2.getString(0));
                    obj.put("transactionno", mCur2.getString(1));
                    obj.put("receiptdate", mCur2.getString(2));
                    obj.put("prefix", mCur2.getString(3));
                    obj.put("suffix", mCur2.getString(4));
                    obj.put("voucherno", mCur2.getString(5));
                    obj.put("refno", mCur2.getString(6));
                    obj.put("companycode", mCur2.getString(7));
                    obj.put("vancode", mCur2.getString(8));
                    obj.put("customercode", mCur2.getString(9));
                    obj.put("schedulecode", mCur2.getString(10));
                    obj.put("receiptremarkscode", mCur2.getString(11));
                    obj.put("receiptmode", mCur2.getString(12));
                    obj.put("chequerefno", mCur2.getString(13));
                    obj.put("amount", mCur2.getDouble(14));
                    obj.put("makerid", mCur2.getString(15));
                    obj.put("createddate", mCur2.getString(16));
                    obj.put("financialyearcode", mCur2.getString(17));
                    obj.put("flag", mCur2.getString(18));
                    obj.put("note", mCur2.getString(19));
                    js_array2.put(obj);
                    mCur2.moveToNext();
                }
                js_obj.put("JSonObject", js_array2);

                jsonObj =  api.CancelREciptDetails(js_obj.toString(),context);
                //Call Json parser functionality
                JSONParser parser = new JSONParser();
                //parse the json object to boolean
                List = parser.parseReceiptDataList(jsonObj);
                dbadapter.close();
                if (List.size() >= 1) {
                    if(List.get(0).TransactionNo.length>0){
                        for(int j=0;j<List.get(0).TransactionNo.length;j++){
                            DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
                            dataBaseAdapter.open();
                            dataBaseAdapter.UpdateCancelReceiptFlag(List.get(0).TransactionNo[j]);
                            dataBaseAdapter.close();
                        }
                    }

                }
            }
            catch (Exception e)
            {
                Log.e("AsyncCancelReceiptDetails : ", e.getMessage());
                if(dbadapter != null)
                    dbadapter.close();
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncCancelReceiptDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("AsyncCancelReceiptDetails : ", e.getMessage());
            if(dbadapter != null)
                dbadapter.close();
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncCancelReceiptDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }

    public void AsyncNilStockDetails(){
        RestAPI api = new RestAPI();
        DataBaseAdapter dbadapter = new DataBaseAdapter(context);
        try {
            JSONObject js_obj = new JSONObject();
            ArrayList<ScheduleDatas> List = null;
            try {

                dbadapter.open();
                Cursor mCur2 = dbadapter.GetNilStockDetailsDB();

                JSONArray js_array2 = new JSONArray();
                for (int i = 0; i < mCur2.getCount(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("autonum", mCur2.getString(0));
                    obj.put("vancode", mCur2.getString(1));
                    obj.put("schedulecode", mCur2.getString(2));
                    obj.put("salestransactionno", mCur2.getString(3));
                    obj.put("salesbookingno", mCur2.getString(4));
                    obj.put("salesfinacialyearcode", mCur2.getString(5));
                    obj.put("salescustomercode", mCur2.getString(6));
                    obj.put("salesitemcode", mCur2.getString(7));
                    obj.put("createddate", mCur2.getString(8));
                    obj.put("flag", mCur2.getString(9));
                    js_array2.put(obj);
                    mCur2.moveToNext();
                }

                js_obj.put("JSonObject", js_array2);

                JSONObject jsonObj =  api.NilStockDetails(js_obj.toString(),context);
                //Call Json parser functionality
                JSONParser parser = new JSONParser();
                //parse the json object to boolean
                List = parser.parseNilStockReport(jsonObj);
                dbadapter.close();

                if (List.size() >= 1) {
                    if(List.get(0).ScheduleCode.length>0){
                        for(int j=0;j<List.get(0).ScheduleCode.length;j++){
                            DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
                            dataBaseAdapter.open();
                            if(List.get(0).ScheduleCode[j] != "" && !List.get(0).ScheduleCode[j].equals("")){
                                String getsplitval[] = List.get(0).ScheduleCode[j].split("~");
                                dataBaseAdapter.UpdateNilStockFlag(getsplitval[0],getsplitval[1]);
                            }
                            dataBaseAdapter.close();
                        }
                    }

                }
            }
            catch (Exception e)
            {
                Log.e("AsyncNilStockDetails : ", e.getMessage());
                if(dbadapter != null)
                    dbadapter.close();
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncNilStockDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("AsyncNilStockDetails : ", e.getMessage());
            if(dbadapter != null)
                dbadapter.close();
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncNilStockDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }

    @SuppressLint("LongLogTag")
    public void AsyncSalesOrderDetails(){
        ArrayList<SalesSyncDatas> List = null;
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();
        String result = "";
        DataBaseAdapter dbadapter = new DataBaseAdapter(context);
        try {
            JSONObject js_obj = new JSONObject();
            JSONObject js_salesobj = new JSONObject();
            JSONObject js_salesitemobj = new JSONObject();
            try {

                dbadapter.open();
                Cursor mCur2 = dbadapter.GetCustomerDatasDB();
                Cursor mCursales = dbadapter.GetSalesOrderDatasDB();
                Cursor mCursalesitems = dbadapter.GetSalesOrderItemDatasDB();
                JSONArray js_array2 = new JSONArray();
                JSONArray js_array3 = new JSONArray();
                JSONArray js_array4 = new JSONArray();
                for (int i = 0; i < mCur2.getCount(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("autonum", mCur2.getString(0));
                    obj.put("customercode", mCur2.getString(1));
                    obj.put("refno", mCur2.getString(2));
                    obj.put("customername", mCur2.getString(3));
                    obj.put("customernametamil", mCur2.getString(4));
                    obj.put("address", mCur2.getString(5));
                    obj.put("areacode", mCur2.getString(6));
                    obj.put("emailid", mCur2.getString(7));
                    obj.put("mobileno", mCur2.getString(8));
                    obj.put("telephoneno", mCur2.getString(9));
                    obj.put("aadharno", mCur2.getString(10));
                    obj.put("gstin", mCur2.getString(11));
                    obj.put("status", mCur2.getString(12));
                    obj.put("makerid", mCur2.getString(13));
                    obj.put("createddate", mCur2.getString(14));
                    obj.put("updateddate", mCur2.getString(15));
                    obj.put("latitude", mCur2.getString(16));
                    obj.put("longitude", mCur2.getString(17));
                    obj.put("flag", mCur2.getString(18));
                    obj.put("schemeapplicable", mCur2.getString(19));
                    obj.put("uploaddocument", mCur2.getString(20));
                    obj.put("business_type", mCur2.getString(23));
                    obj.put("customertypecode", mCur2.getString(22));
                    obj.put("whatsappno", mCur2.getString(24));
                    obj.put("mobilenoverificationstatus", mCur2.getString(25));

                    js_array4.put(obj);
                    mCur2.moveToNext();
                }
                for (int i = 0; i < mCursales.getCount(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("autonum", mCursales.getString(0));
                    obj.put("companycode", mCursales.getString(1));
                    obj.put("vancode", mCursales.getString(2));
                    obj.put("transactionno", mCursales.getString(3));
                    obj.put("billno", mCursales.getString(4));
                    obj.put("refno", mCursales.getString(5));
                    obj.put("prefix", mCursales.getString(6));
                    obj.put("suffix", mCursales.getString(7));
                    obj.put("billdate", mCursales.getString(8));
                    obj.put("customercode", mCursales.getString(9));
                    obj.put("billtypecode", mCursales.getString(10));
                    obj.put("gstin", mCursales.getString(11));
                    obj.put("schedulecode", mCursales.getString(12));
                    obj.put("subtotal", mCursales.getDouble(13));
                    obj.put("discount", mCursales.getDouble(14));
                    obj.put("totaltaxamount", mCursales.getDouble(15));
                    obj.put("grandtotal", mCursales.getDouble(16));
                    obj.put("billcopystatus", mCursales.getString(17));
                    obj.put("cashpaidstatus", mCursales.getString(18));
                    obj.put("flag", mCursales.getString(19));
                    obj.put("makerid", mCursales.getString(20));
                    obj.put("createddate", mCursales.getString(21));
                    obj.put("updateddate", mCursales.getString(22));
                    obj.put("bitmapimage", mCursales.getString(23));
                    obj.put("financialyearcode", mCursales.getString(24));
                    obj.put("remarks", mCursales.getString(25));
                    obj.put("bookingno", mCursales.getString(26));
                    obj.put("salestime", mCursales.getString(29));
                    obj.put("beforeroundoff", mCursales.getString(30));
                    obj.put("transportid", mCursales.getString(31));
                    obj.put("status", mCursales.getString(32));
                    obj.put("transportmode", mCursales.getString(33));
                    obj.put("latlong", mCursales.getString(34));
                    js_array2.put(obj);
                    mCursales.moveToNext();
                }
                for (int i = 0; i < mCursalesitems.getCount(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("autonum", mCursalesitems.getString(0));
                    obj.put("transactionno", mCursalesitems.getString(1));
                    obj.put("companycode", mCursalesitems.getString(2));
                    obj.put("itemcode", mCursalesitems.getString(3));
                    obj.put("qty", mCursalesitems.getString(4));
                    obj.put("weight", mCursalesitems.getString(5));
                    obj.put("price", mCursalesitems.getString(6));
                    obj.put("discount", mCursalesitems.getDouble(7));
                    obj.put("amount", mCursalesitems.getDouble(8));
                    obj.put("cgst", mCursalesitems.getDouble(9));
                    obj.put("sgst", mCursalesitems.getDouble(10));
                    obj.put("igst", mCursalesitems.getDouble(11));
                    obj.put("cgstamt", mCursalesitems.getDouble(12));
                    obj.put("sgstamt", mCursalesitems.getDouble(13));
                    obj.put("igstamt", mCursalesitems.getDouble(14));
                    obj.put("freeitemstatus", mCursalesitems.getString(15));
                    obj.put("makerid", mCursalesitems.getString(16));
                    obj.put("createddate", mCursalesitems.getString(17));
                    obj.put("updateddate", mCursalesitems.getString(18));
                    obj.put("bookingno", mCursalesitems.getString(19));
                    obj.put("financialyearcode", mCursalesitems.getString(20));
                    obj.put("vancode", mCursalesitems.getString(21));
                    obj.put("flag", mCursalesitems.getString(22));

                    js_array3.put(obj);
                    mCursalesitems.moveToNext();
                }


                js_obj.put("JSonObject", js_array4);
                js_salesobj.put("JSonObject", js_array2);
                js_salesitemobj.put("JSonObject", js_array3);

                jsonObj =  api.SalesOrderDetails(js_salesobj.toString(),js_salesitemobj.toString(),js_obj.toString(),context);
                //Call Json parser functionality
                JSONParser parser = new JSONParser();
                //parse the json object to boolean
                List = parser.parseSalesDataList(jsonObj);
                dbadapter.close();

                DataBaseAdapter objdatabaseadapter = new DataBaseAdapter(context);
                try {
                    objdatabaseadapter.open();
                    if (List.size() >= 1) {
                        if (List.get(0).TransactionNo.length > 0) {
                            for (int j = 0; j < List.get(0).TransactionNo.length; j++) {
                                objdatabaseadapter.UpdateSalesOrderFlag(List.get(0).TransactionNo[j]);
                            }
                        }
                        if (List.get(0).SalesItemTransactionNo.length > 0) {
                            for (int j = 0; j < List.get(0).SalesItemTransactionNo.length; j++) {
                                objdatabaseadapter.UpdateSalesOrderItemFlag(List.get(0).SalesItemTransactionNo[j]);
                            }
                        }

                    }
                }catch (Exception e) {
                    // TODO Auto-generated catch block
                    Log.e("AsyncSalesOrderDetails : ", e.getMessage());
                    if(dbadapter != null)
                        dbadapter.close();
                    if(objdatabaseadapter!=null)
                        objdatabaseadapter.close();
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncSalesOrderDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                } finally {
                    if(objdatabaseadapter!=null)
                        objdatabaseadapter.close();
                }
            }
            catch (Exception e)
            {
                Log.e("AsyncSalesOrderDetails : ", e.getMessage());
                if(dbadapter != null)
                    dbadapter.close();
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncSalesOrderDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("AsyncSalesOrderDetails : ", e.getMessage());
            if(dbadapter != null)
                dbadapter.close();
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncSalesOrderDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }

    //Async sales ordercancel
    @SuppressLint("LongLogTag")
    public void AsyncSalesOrderCancelDetails(){
        ArrayList<SalesSyncDatas> List = null;
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();
        DataBaseAdapter dbadapter = new DataBaseAdapter(context);
        try {
            JSONObject js_salesobj = new JSONObject();
            JSONObject js_salesitemobj = new JSONObject();
            try {

                dbadapter.open();
                Cursor mCursales = dbadapter.GetSalesOrderCancelDatasDB();
                Cursor mCursalesitems = dbadapter.GetSalesOrderCancelItemDatasDB();
                JSONArray js_array2 = new JSONArray();
                JSONArray js_array3 = new JSONArray();
                for (int i = 0; i < mCursales.getCount(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("autonum", mCursales.getString(0));
                    obj.put("companycode", mCursales.getString(1));
                    obj.put("vancode", mCursales.getString(2));
                    obj.put("transactionno", mCursales.getString(3));
                    obj.put("billno", mCursales.getString(4));
                    obj.put("refno", mCursales.getString(5));
                    obj.put("prefix", mCursales.getString(6));
                    obj.put("suffix", mCursales.getString(7));
                    obj.put("billdate", mCursales.getString(8));
                    obj.put("customercode", mCursales.getString(9));
                    obj.put("billtypecode", mCursales.getString(10));
                    obj.put("gstin", mCursales.getString(11));
                    obj.put("schedulecode", mCursales.getString(12));
                    obj.put("subtotal", mCursales.getString(13));
                    obj.put("discount", mCursales.getString(14));
                    obj.put("totaltaxamount", mCursales.getString(15));
                    obj.put("grandtotal", mCursales.getString(16));
                    obj.put("billcopystatus", mCursales.getString(17));
                    obj.put("cashpaidstatus", mCursales.getString(18));
                    obj.put("flag", mCursales.getString(19));
                    obj.put("makerid", mCursales.getString(20));
                    obj.put("createddate", mCursales.getString(21));
                    obj.put("updateddate", mCursales.getString(22));
                    obj.put("bitmapimage", mCursales.getString(23));
                    obj.put("financialyearcode", mCursales.getString(24));
                    obj.put("remarks", mCursales.getString(25));
                    obj.put("bookingno", mCursales.getString(26));
                    obj.put("salestime", mCursales.getString(29));
                    obj.put("beforeroundoff", mCursales.getString(30));
                    obj.put("transportid", mCursales.getString(31));
                    js_array2.put(obj);
                    mCursales.moveToNext();
                }

                for (int i = 0; i < mCursalesitems.getCount(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("autonum", mCursalesitems.getString(0));
                    obj.put("transactionno", mCursalesitems.getString(1));
                    obj.put("companycode", mCursalesitems.getString(2));
                    obj.put("itemcode", mCursalesitems.getString(3));
                    obj.put("qty", mCursalesitems.getString(4));
                    obj.put("weight", mCursalesitems.getString(5));
                    obj.put("price", mCursalesitems.getString(6));
                    obj.put("discount", mCursalesitems.getDouble(7));
                    obj.put("amount", mCursalesitems.getDouble(8));
                    obj.put("cgst", mCursalesitems.getDouble(9));
                    obj.put("sgst", mCursalesitems.getDouble(10));
                    obj.put("igst", mCursalesitems.getDouble(11));
                    obj.put("cgstamt", mCursalesitems.getDouble(12));
                    obj.put("sgstamt", mCursalesitems.getDouble(13));
                    obj.put("igstamt", mCursalesitems.getDouble(14));
                    obj.put("freeitemstatus", mCursalesitems.getString(15));
                    obj.put("makerid", mCursalesitems.getString(16));
                    obj.put("createddate", mCursalesitems.getString(17));
                    obj.put("updateddate", mCursalesitems.getString(18));
                    obj.put("bookingno", mCursalesitems.getString(19));
                    obj.put("financialyearcode", mCursalesitems.getString(20));
                    obj.put("vancode", mCursalesitems.getString(21));
                    obj.put("flag", mCursalesitems.getString(22));

                    js_array3.put(obj);
                    mCursalesitems.moveToNext();
                }
                js_salesobj.put("JSonObject", js_array2);
                js_salesitemobj.put("JSonObject", js_array3);

                jsonObj =  api.SalesOrderCancelDetails(js_salesobj.toString(), js_salesitemobj.toString(),context);
                //Call Json parser functionality
                JSONParser parser = new JSONParser();
                //parse the json object to boolean
                List = parser.parseSalesCancelDataList(jsonObj);
                dbadapter.close();

                DataBaseAdapter objdatabaseadapter = new DataBaseAdapter(context);
                try {
                    objdatabaseadapter.open();
                    if (List.size() >= 1) {
                        if (List.get(0).TransactionNo.length > 0) {
                            for (int j = 0; j < List.get(0).TransactionNo.length; j++) {
                                objdatabaseadapter.UpdateOrderCancelSalesFlag(List.get(0).TransactionNo[j]);
                            }
                        }

                    }
                }catch (Exception e) {
                    // TODO Auto-generated catch block
                    Log.e("AsyncSalesOrderCancelDetails : ", e.getMessage());
                    if(dbadapter != null)
                        dbadapter.close();
                    if(objdatabaseadapter!=null)
                        objdatabaseadapter.close();
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncSalesOrderCancelDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                } finally {
                    if(objdatabaseadapter!=null)
                        objdatabaseadapter.close();
                }
            }
            catch (Exception e)
            {
                Log.e("AsyncSalesOrderCancelDetails : ", e.getMessage());
                if(dbadapter != null)
                    dbadapter.close();
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncSalesOrderCancelDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("AsyncSalesOrderCancelDetails : ", e.getMessage());
            if(dbadapter != null)
                dbadapter.close();
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncSalesOrderCancelDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }

    /*********SCHEDULE DETAILS*************/
    public void AsyncScheduleDetails(){
        ArrayList<ScheduleDatas> List = null;
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();
        String result = "";
        DataBaseAdapter dbadapter = new DataBaseAdapter(context);
        try {
            JSONObject js_obj = new JSONObject();
            try {

                dbadapter.open();
                Cursor mCur2 = dbadapter.GetScheduleDatasDB();
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
                Log.e("AsyncScheduleDetails : ", e.getMessage());
                if(dbadapter != null)
                    dbadapter.close();
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncScheduleDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("AsyncScheduleDetails : ", e.getMessage());
            if(dbadapter != null)
                dbadapter.close();
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncScheduleDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }

    /*************************END FUNCTIONS***********************/

    public  class UploadImage extends AsyncTask<String, JSONObject, String> {

        ProgressDialog loading;
        RequestHandler rh = new RequestHandler();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {

        }

        @Override
        protected String doInBackground(String... params) {
            String result="";
            DataBaseAdapter objcustomerAdaptor = new DataBaseAdapter(context);
            try{
                // Bitmap bitmap = params[0];

                objcustomerAdaptor.open();
                Cursor mCur = objcustomerAdaptor.GetSalesImageDetails();
                String uploadImage;

                for (int i=0;i<mCur.getCount();i++){
                    if(mCur.getString(1) == null){
                        uploadImage="";
                    }else {
                        uploadImage = mCur.getString(1);
                    }
                    HashMap<String, String> data = new HashMap<>();
                    data.put("paraimage", uploadImage);
                    data.put("paratransno", mCur.getString(0));
                    data.put("parafinacialyearcode", mCur.getString(2));
                    data.put("paraimagecode", mCur.getString(3));

                    result = rh.sendPostRequest(UPLOAD_URL, data);
                    if(result.equals("Success")) {
                        DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                        mDbErrHelper.open();
                        mDbErrHelper.SalesImageSetFlag(mCur.getString(0),mCur.getString(2));
                        mDbErrHelper.close();
                    }
                    mCur.moveToNext();
                }
            }catch (Exception e) {
                // TODO Auto-generated catch block
                Log.e("UploadImage : ", e.getMessage());
                if(objcustomerAdaptor != null)
                    objcustomerAdaptor.close();
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - UploadImage", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            finally {
                if(objcustomerAdaptor != null)
                    objcustomerAdaptor.close();
            }


            return result;
        }
    }

    /*********SCHEME DETAILS*************/

    protected  class AsyncSchemeDetails extends
            AsyncTask<String, JSONObject, String> {
        String List = "Success";
        JSONObject jsonObj = null;
        ProgressDialog loading;

        @SuppressLint("LongLogTag")
        @Override
        protected  String doInBackground(String... params) {

            RestAPI api = new RestAPI();
            String result = "";
            DataBaseAdapter dataBaseAdapter =null;
            String deviceid = preferenceMangr.pref_getString("deviceid");
            try {
                dataBaseAdapter = new DataBaseAdapter(context);
                dataBaseAdapter.open();

                    //if(BuildConfig.DEBUG)
                        Log.w("---> Sync Activity : "," Sync Scheme Started");
                    networkstate = isNetworkAvailable();
                    if (networkstate == true) {
                        try{
                            jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncscheme.php",context);
                            if (isSuccessful(jsonObj)) {
                                dataBaseAdapter.syncscheme(jsonObj);
                                api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "scheme", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                            }
                            jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncschemeitemdetails.php",context);
                            if (isSuccessful(jsonObj)) {
                                dataBaseAdapter.syncschemeitemdetails(jsonObj);
                                api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "schemeitemdetails", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                            }
                            //scheme rate details
                            jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncschemeratedetails.php",context);
                            if (isSuccessful(jsonObj)) {
                                dataBaseAdapter.syncschemeratedetails(jsonObj);
                                api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "schemeratedetails", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                            }

                        }catch (Exception e) {
                            // TODO Auto-generated catch block
                            Log.d("AsyncSyncSchemeDetails", e.getMessage());
                            if(dataBaseAdapter != null)
                                dataBaseAdapter.close();
                            InsertError(e.toString(),"Sync Scheme local db to server");
                        }
                    }
                    //if(BuildConfig.DEBUG)
                        Log.w("---> Sync Activity : "," Sync Scheme Completed");



            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.e("AsyncSyncTransactionDetails DIB : ",e.getLocalizedMessage());
                if(dataBaseAdapter != null)
                    dataBaseAdapter.close();
                InsertError(e.toString(),"Sync Scheme DIB");
            }
            finally {
                if(dataBaseAdapter != null)
                    dataBaseAdapter.close();
            }
            return List;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(context, "Synching Scheme Data", "Please wait...", true, true);
            loading.setCancelable(false);
            loading.setCanceledOnTouchOutside(false);
        }
        @SuppressLint("LongLogTag")
        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            loading.dismiss();
        }
    }

    public void onReceiveAsyncResult(Object response, int requestType) {
        switch (requestType) {
            case Constants.DOWNLOAD_VENDER_IMAGE:
                try{
                    if(response == null)
                        return;
                    downloadFileID = (long) response;
                    if(downloadFileID == 0)
                        return;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_EXPORTED);
                    } else {
                        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                    }
                }catch (Exception e){
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'", " "),
                            this.getClass().getSimpleName()+" - onReceiveAsyncResult", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                }

                break;

        }
    }
    BroadcastReceiver onComplete=new BroadcastReceiver() {
        @SuppressLint("LongLogTag")
        public void onReceive(Context ctxt, Intent intent) {
            // Do Something
            try{
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    if(downloadFileID == downloadId){
                        DownloadManager.Query query = new DownloadManager.Query();
                        //query.setFilterById(enq);
                        DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                        Cursor c = downloadManager.query(query);
                        if (c.moveToFirst()) {
                            int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                            if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {

                                String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));


                                String decodedPath = Uri.decode(uriString);
                                //TODO : Use this local uri and launch intent to open file
                                try{

                                    //Toast.makeText(context,"Successfully file Downloaded...!",Toast.LENGTH_SHORT).show();
                                    Log.e("UPDATELOCALPATH", "DownLoad");
                                    insertPDF_FilePath_InLocalDB(decodedPath);
//                                    Utilities.insertPDF_FilePath_InLocalDB(context, downloadUPIData, decodedPath);


                                }catch (Exception e){
                                    Log.d("Share the file in android 11 " , e.getLocalizedMessage());
                                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                    mDbErrHelper.open();
                                    String geterrror = e.toString();
                                    mDbErrHelper.insertErrorLog(geterrror.replace("'", " "),
                                            this.getClass().getSimpleName()+" - Share the file in android 11", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                    mDbErrHelper.close();
                                }
                            }
                        }
                    }



                }
            }catch (Exception e){
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "),
                        this.getClass().getSimpleName() + " - Exception in share the downloaded file ", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }


        }
    };

    public void insertPDF_FilePath_InLocalDB(String downloadedFilePath){
        DataBaseAdapter dataBaseAdapter = null;
        if(downloadUPIData == null || downloadUPIData.size() < 0)
            return;
        try{

            String companycode = (String) downloadUPIData.get(Constants.KEY_INDEX_0);
            String venderid = (String) downloadUPIData.get(Constants.KEY_INDEX_1);

            dataBaseAdapter = new DataBaseAdapter(context);
            dataBaseAdapter.open();
            dataBaseAdapter.insertImageFile_LocalPath(companycode, venderid, downloadedFilePath);
            dataBaseAdapter.close();
        }catch (Exception e){
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "),
                    this.getClass().getSimpleName() + " - insert ImageFile In LocalDB ", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }

    /*************************END FUNCTIONS***********************/

    public void goBack(View v) {
        finish();
    }

    @SuppressLint("LongLogTag")
    public void InsertError(String geterrror,String methodName){
        try{
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            if(mDbErrHelper == null){
                mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
            }
            if(mDbErrHelper != null){
                mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName() + methodName, String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        }catch (Exception e){
            Log.e("Exception in Insert Error ",e.getLocalizedMessage());
        }
    }

    @SuppressLint("LongLogTag")
    public void  AsyncNotPurchasedDetails(){
        ArrayList<SalesSyncDatas> List = null;
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();

        try {
            JSONObject js_salesobj = new JSONObject();
            try {
                DataBaseAdapter dbadapter = new DataBaseAdapter(context);
                dbadapter.open();

                Cursor mCursales = dbadapter.GetNotPurchasedDatasDB();

                JSONArray js_array2 = new JSONArray();

                for (int i = 0; i < mCursales.getCount(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("vancode", mCursales.getString(1));
                    obj.put("billdate", mCursales.getString(2));
                    obj.put("customercode", mCursales.getString(3));
                    obj.put("schedulecode", mCursales.getString(4));
                    obj.put("createddate", mCursales.getString(5));
                    obj.put("financialyearcode", mCursales.getString(7));
                    obj.put("remarks", mCursales.getString(8));
                    obj.put("syncstatus",mCursales.getString(9));
                    obj.put("salestime", mCursales.getString(10));
                    obj.put("latlong", mCursales.getString(11));

                    js_array2.put(obj);
                    mCursales.moveToNext();
                }

                js_salesobj.put("JSonObject", js_array2);

                jsonObj =  api.insertNotPurchasedDetails(js_salesobj.toString(),context);
                //Call Json parser functionality
                JSONParser parser = new JSONParser();
                //parse the json object to boolean
                List = parser.parseSalesDataList(jsonObj);
                dbadapter.close();

                DataBaseAdapter objdatabaseadapter = new DataBaseAdapter(context);
                try {
                    objdatabaseadapter.open();
                    if (List.size() >= 1) {
                        if (List.get(0).TransactionNo.length > 0) {
                            for (int j = 0; j < List.get(0).TransactionNo.length; j++) {
                                objdatabaseadapter.UpdateNotPurchaseFlag(List.get(0).TransactionNo[j]);
                            }
                        }
                    }
                }catch (Exception e) {
                    // TODO Auto-generated catch block
                    Log.d("AsyncNotPurchasedDetails", e.getMessage());
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncNotPurchasedDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                } finally {
                    if(objdatabaseadapter!=null)
                        objdatabaseadapter.close();
                }
            }
            catch (Exception e)
            {
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncNotPurchasedDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("AsyncNotPurchasedDetails", e.getMessage());
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }

}