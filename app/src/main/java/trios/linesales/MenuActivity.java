package trios.linesales;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class MenuActivity extends AppCompatActivity {
    LinearLayout salesLL,ReportstLL,receiptLL,ExpensetLL,
            CustomertLL,VanStocktLL,SalesChildLL,ReportsChildLL,ScheduleLL,
            SalesOrderLL,Cash_Report_LL,Sales_Return_ReporsLL,Cash_Close_LL,ReportLayout,SettingsLL,AdditionStocktLL;
    public Context context;
    public static Context context1;
    boolean clicksales=true,clickreports=true;
    ImageButton menulogout,menusyncall;
    TextView SalesHead,ReportsHead,Sales,salesitemwise,
            cashreport,expensereport,salesbillwise,SalesReturn,
            PriceListHead,salesreturnitemwise,cashsummaryreport,salesreturnbillwise,salesorderbillwise,salesorderitemwise;
    public static TextView OrderForm;
    boolean networkstate;
    static public String getschedulecode="",getroutecode="",getroutename="",gettripadvance="",
        getroutenametamil="",getcapacity="",getcashclosecount="0",getsalesclosecount="0",getwishmsg="";
    static double  getdenominationcount = 0;
    public static final String UPLOAD_URL = RestAPI.urlString+"syncimage.php";
    String lunch_start_time = "",lunch_end_time="";
    View cashcloseview;
    LinearLayout Sales_Layout,ReportsOrderChildLL;
    Handler handler = new Handler();
    Runnable runnable;
    public int refreshtime=300000;

    TextView generatePDF;
    int StorageRequestCode=10;

    public static MenuActivity menuActivity=new MenuActivity();
    public static PreferenceMangr preferenceMangr=null;
    public String downloadFileName="";
    ProgressDialog downloadLoading;
    DropBoxAsyncResponseListener dropboxlistener;
    public long downloadFileID=0;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        context = this;
        context1 = this;

        //LinearLayout declaration
        salesLL = (LinearLayout) findViewById(R.id.salesLL);
        SalesChildLL = (LinearLayout)findViewById(R.id.SalesChildLL);
        SalesHead = (TextView)findViewById(R.id.SalesHead);
        ReportsChildLL = (LinearLayout)findViewById(R.id.ReportsChildLL);
        ScheduleLL = (LinearLayout)findViewById(R.id.ScheduleLL);
        ReportstLL = (LinearLayout)findViewById(R.id.ReportstLL);
        receiptLL=(LinearLayout)findViewById(R.id.ReceiptLL);
        ReportsHead = (TextView)findViewById(R.id.ReportsHead);
        Sales = (TextView)findViewById(R.id.Sales);
        ExpensetLL = (LinearLayout) findViewById(R.id.ExpensetLL);
        salesitemwise=(TextView)findViewById(R.id.salesitemwise);
        VanStocktLL = (LinearLayout) findViewById(R.id.VanStocktLL);
        cashreport = (TextView)findViewById(R.id.cashreport);
        expensereport = (TextView)findViewById(R.id.expensereport);
        salesbillwise = (TextView)findViewById(R.id.salesbillwise);
        CustomertLL = (LinearLayout)findViewById(R.id.CustomertLL);
        SalesReturn = (TextView)findViewById(R.id.SalesReturn);
        menulogout = (ImageButton)findViewById(R.id.menulogout);
        menusyncall = (ImageButton)findViewById(R.id.menusyncall);
        PriceListHead=(TextView)findViewById(R.id.PriceListHead);
        OrderForm = (TextView)findViewById(R.id.OrderForm);
        salesreturnitemwise = (TextView)findViewById(R.id.salesreturnitemwise);
        cashsummaryreport = (TextView)findViewById(R.id.cashsummaryreport);
        salesreturnbillwise = (TextView)findViewById(R.id.salesreturnbillwise);
        cashcloseview = (View)findViewById(R.id.cashcloseview);
        SalesOrderLL = (LinearLayout)findViewById(R.id.SalesOrderLL);
        Cash_Report_LL = (LinearLayout)findViewById(R.id.Cash_Report_LL);
        Sales_Return_ReporsLL = (LinearLayout)findViewById(R.id.Sales_Return_ReporsLL);
        Cash_Close_LL = (LinearLayout)findViewById(R.id.Cash_Close_LL);
        ReportLayout = (LinearLayout)findViewById(R.id.ReportLayout);
        Sales_Layout = (LinearLayout)findViewById(R.id.Sales_Layout);
        salesorderbillwise = (TextView)findViewById(R.id.salesorderbillwise);
        salesorderitemwise = (TextView)findViewById(R.id.salesorderitemwise);
        ReportsOrderChildLL =(LinearLayout)findViewById(R.id.ReportsOrderChildLL);
        generatePDF = (TextView) findViewById(R.id.generatepdf);
        SettingsLL =(LinearLayout) findViewById(R.id.SettingsLL);
        AdditionStocktLL = (LinearLayout) findViewById(R.id.AdditionStocktLL);
        LoginActivity.iscash =false;
        getschedulecode="";getwishmsg="";

        /*try{
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dformat = new SimpleDateFormat("dd-MM-yyyy h:mm a");
            String scheduledatetime = String.valueOf(dformat.format(calendar.getTime()));
            Toast.makeText(context,scheduledatetime,Toast.LENGTH_SHORT).show();
        }catch(Exception e){

        }*/
        try {
            preferenceMangr = new PreferenceMangr(context);
        }catch (Exception e){
            Log.d("Preference Manager : ",e.toString());
        }

        this.dropboxlistener = new DropBoxAsyncResponseListener() {

            @Override
            public void onAsyncTaskResponseReceived(Object response, int requestType) {
                onReceiveAsyncResult(response, requestType);
            }
        };

        OrderForm.setVisibility(View.GONE);
        cashcloseview.setVisibility(View.GONE);
        menusyncall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MenuActivity.this,SyncActivity.class);
                startActivity(i);
            }
        });
        //Get Current date
        DataBaseAdapter objdatabaseadapter1 = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter1 = new DataBaseAdapter(context);
            objdatabaseadapter1.open();
            //LoginActivity.getformatdate = objdatabaseadapter1.GenCreatedDate();
            //LoginActivity.getcurrentdatetime = objdatabaseadapter1.GenCurrentCreatedDate();
            preferenceMangr.pref_putString("getformatdate",objdatabaseadapter1.GenCreatedDate());
            preferenceMangr.pref_putString("getcurrentdatetime",objdatabaseadapter1.GenCurrentCreatedDate());

            Cur = objdatabaseadapter1.GetScheduleListDB(preferenceMangr.pref_getString("getformatdate") );
            if(Cur.getCount()>0) {
                for (int i = 0; i < Cur.getCount(); i++) {
                     lunch_start_time = Cur.getString(11);
                     lunch_end_time = Cur.getString(12);
                }
            }
        }catch (Exception e){
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }finally {
            // this gets called even if there is an exception somewhere above
            if (objdatabaseadapter1 != null)
                objdatabaseadapter1.close();
            if(Cur != null)
                Cur.close();
        }



       //Open Sales List
        Sales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("KEY_GET_GPSTRACKINGSTATUS", String.valueOf(preferenceMangr.pref_getBoolean(Constants.KEY_GET_GPSTRACKINGSTATUS)));
                if(preferenceMangr.pref_getBoolean(Constants.KEY_GET_GPSTRACKINGSTATUS)){
                    if (PermissionManager.getInstance().checkAndAskPermissionForThisEvent(MenuActivity.this, Constants.PERMISSION_LOCATION)) {
                        GPSTracker gpsTracker = new GPSTracker(context);

                        //Log.d("Trip ", "Latlong " + locManager.getLatitude() + "," + locManager.getLongitude());
                        if (gpsTracker.isGPSEnabled || gpsTracker.isNetworkEnabled) {
                            Intent i = new Intent(context, SalesListActivity.class);
                            startActivity(i);
                            //Toast.makeText(getApplicationContext(), "Lat - " + locManager.getLatitude() + "Long - " + locManager.getLongitude(), Toast.LENGTH_SHORT).show();
                            //Log.d("Trip ", "Latlong " + locManager.getLatitude() + "," + locManager.getLongitude());
                        }else{
                            gpsTracker.showSettingsAlert();
                        }
                    }
                }else {
                    Intent i = new Intent(context, SalesListActivity.class);
                    startActivity(i);
                }
            }
        });
        //Open Sales Item Wise Report
        salesitemwise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, SalesItemWiseReportActivity.class);
                startActivity(i);
            }
        });
        //sales order form
        SalesOrderLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!lunch_start_time.equals("") && !lunch_start_time.equals(" ")  && !lunch_start_time.equals("null")
                        && !lunch_start_time.equals("1900-01-01 00:00:00") && (lunch_end_time.equals("") ||
                        lunch_end_time.equals(" ")  || lunch_end_time.equals("null") || lunch_end_time.equals("1900-01-01 00:00:00") ) ){
                    Toast toast = Toast.makeText(getApplicationContext(),  getString(R.string.lunchstarted), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }else {
                    Intent i = new Intent(context, SalesOrderListActivity.class);
                    startActivity(i);
                }
            }
        });
        //Open Receipt List
        receiptLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!lunch_start_time.equals("") && !lunch_start_time.equals(" ")  && !lunch_start_time.equals("null")
                        && !lunch_start_time.equals("1900-01-01 00:00:00") && ( lunch_end_time.equals("") ||
                        lunch_end_time.equals(" ")  || lunch_end_time.equals("null") || lunch_end_time.equals("1900-01-01 00:00:00") ) ){
                    Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.lunchstarted), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }else {
                    Intent i = new Intent(context, ReceiptActivity.class);
                    startActivity(i);
                }
            }
        });
//Open Additional Stock Receipt List
        AdditionStocktLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, AdditionalStockReceiptActivity.class);
                startActivity(i);
            }
        });
        //Open Van Stock List
        VanStocktLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, VanStockActivity.class);
                startActivity(i);
            }
        });
        //Open Cash Report
        cashreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, CashReportActivity.class);
                startActivity(i);
            }
        });
        //Open Cash Report
        Cash_Close_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!lunch_start_time.equals("") && !lunch_start_time.equals(" ")  && !lunch_start_time.equals("null")
                        && !lunch_start_time.equals("1900-01-01 00:00:00") && (lunch_end_time.equals("") ||
                        lunch_end_time.equals(" ")  || lunch_end_time.equals("null") || lunch_end_time.equals("1900-01-01 00:00:00") ) ){
                    Toast toast = Toast.makeText(getApplicationContext(),  getString(R.string.lunchstarted), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }else {
                    Intent i = new Intent(context, CashReportActivity.class);
                    startActivity(i);
                }
            }
        });
        //Open Sales Return List
        SalesReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, SalesReturnListActivity.class);
                startActivity(i);
            }
        });
        //Open Expense Report
        expensereport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ExpenseReportActivity.class);
                startActivity(i);
            }
        });
        //Open Sales Bill wise Report
        salesbillwise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, SalesBillWiseReportActivity.class);
                startActivity(i);
            }
        });
        //Open Customer List
        CustomertLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, CustomerActivity.class);
                startActivity(i);
            }
        });
        //Open Price List
        PriceListHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, PriceListActivity.class);
                startActivity(i);
            }
        });
        //Open sales return bill wise
        salesreturnbillwise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, SalesReturnBillwiseReportActivity.class);
                startActivity(i);
            }
        });
        //Open Order form
        OrderForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get Current date
                DataBaseAdapter objdatabaseadapter1 = null;
                try{
                    objdatabaseadapter1 = new DataBaseAdapter(context);
                    objdatabaseadapter1.open();
                    String  getorderschedulecode = objdatabaseadapter1.GetScheduleCode();
                    String getordercount = objdatabaseadapter1.GetOrderFormCount(getorderschedulecode);
                    if(!getordercount.equals("") && !getordercount.equals("0") && !getordercount.equals("null")){
                        if(Double.parseDouble(getordercount) >0) {
                            Intent i = new Intent(context, OrderFormCartActivity.class);
                            startActivity(i);
                        }else{
                            Intent i = new Intent(context, OrderFormActivity.class);
                            startActivity(i);
                        }
                    }else{
                        Intent i = new Intent(context, OrderFormActivity.class);
                        startActivity(i);
                    }
                }catch (Exception e){
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                }finally {
                    // this gets called even if there is an exception somewhere above
                    if (objdatabaseadapter1 != null)
                        objdatabaseadapter1.close();
                }


            }
        });
        //Open Order form
        salesreturnitemwise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, SalesReturnItemWiseReportActivity.class);
                startActivity(i);
            }
        });
        //Open Order form
        salesorderbillwise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, SalesOrderBillwiseReportActivity.class);
                startActivity(i);
            }
        });
        //Open Order form
        salesorderitemwise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, SalesOrderItemWiseActivity.class);
                startActivity(i);
            }
        });
        //Open myschedule
        ScheduleLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, MyScheduleActivity.class);
                startActivity(i);
            }
        });

        //Open cash summary
        cashsummaryreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, CashSummaryActivity.class);
                startActivity(i);
            }
        });

        final Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_up);
        //Open Sales Child Menus
        salesLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(!lunch_start_time.equals("") && !lunch_start_time.equals(" ")  && !lunch_start_time.equals("null")
                    && !lunch_start_time.equals("1900-01-01 00:00:00") && (lunch_end_time.equals("") ||
                        lunch_end_time.equals(" ")  || lunch_end_time.equals("null") || lunch_end_time.equals("1900-01-01 00:00:00") ) ){
                Toast toast = Toast.makeText(getApplicationContext(),  getString(R.string.lunchstarted), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }else {
                if (!clickreports) {
                    ReportsChildLL.setVisibility(View.GONE);
                    clickreports = true;
                    ReportsHead.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_reports, 0, R.drawable.ic_keyboard_arrow_down, 0);
                }
                if (clicksales) {
                    SalesChildLL.setVisibility(View.VISIBLE);
                    clicksales = false;
                    SalesChildLL.startAnimation(aniFade);
                    SalesHead.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sale, 0, R.drawable.ic_keyboard_arrow_up, 0);
                } else {
                    SalesChildLL.setVisibility(View.GONE);
                    clicksales = true;
                    SalesHead.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sale, 0, R.drawable.ic_keyboard_arrow_down, 0);
                }
            }

            }
        });

        //Open Reports Child Menus
        ReportstLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!clicksales){
                    SalesChildLL.setVisibility(View.GONE);
                    clicksales=true;
                    SalesHead.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sale, 0,  R.drawable.ic_keyboard_arrow_down, 0);
                }
                if(clickreports){
                    ReportsChildLL.setVisibility(View.VISIBLE);
                    clickreports=false;
                    ReportsChildLL.startAnimation(aniFade);
                    ReportsHead.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_reports, 0, R.drawable.ic_keyboard_arrow_up, 0);
                }else{
                    ReportsChildLL.setVisibility(View.GONE);
                    clickreports=true;
                    ReportsHead.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_reports, 0,  R.drawable.ic_keyboard_arrow_down, 0);
                }
            }
        });

        //Open Expenses List
        ExpensetLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent i = new Intent(context, ExpensesActivity.class);
            startActivity(i);
            }
        });

        //Logout process
        menulogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // HomeActivity.logoutprocess = "True";
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



        /***** SCHEDULE DETAILES************/
        DataBaseAdapter objdatabaseadapter = null;
        Cursor getschedulelist = null;
        try {
            //Get Schdule
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            getschedulelist = objdatabaseadapter.GetScheduleDB();
            if(getschedulelist.getCount() >0){
                for(int i=0;i<getschedulelist.getCount();i++){
                    getschedulecode = getschedulelist.getString(0);
                    getroutecode = getschedulelist.getString(1);
                    getroutename = getschedulelist.getString(4);
                    gettripadvance =  getschedulelist.getString(3);
                    getroutenametamil =  getschedulelist.getString(4);
                    getcapacity = getschedulelist.getString(5);

                    preferenceMangr.pref_putString("getschedulecode",getschedulelist.getString(0));
                    preferenceMangr.pref_putString("getroutecode",getschedulelist.getString(1));
                    preferenceMangr.pref_putString("getroutename",getschedulelist.getString(4));
                    preferenceMangr.pref_putString("getcapacity",getschedulelist.getString(5));
                }
                //Get Cash close Count
                getcashclosecount = objdatabaseadapter.GetCashClose(preferenceMangr.pref_getString("getschedulecode"));
                preferenceMangr.pref_putString("getcashclosecount",objdatabaseadapter.GetCashClose(preferenceMangr.pref_getString("getschedulecode")));

                //Get sales close Count
                getsalesclosecount = objdatabaseadapter.GetSalesClose(preferenceMangr.pref_getString("getschedulecode"));
                preferenceMangr.pref_putString("getsalesclosecount",objdatabaseadapter.GetSalesClose(preferenceMangr.pref_getString("getschedulecode")));

                getdenominationcount = objdatabaseadapter.GetDenominationCount(preferenceMangr.pref_getString("getschedulecode"));
                //preferenceMangr.pref_putString("getdenominationcount",objdatabaseadapter.GetDenominationCount(preferenceMangr.pref_getString("getschedulecode")));

                //GetWish messsgae
                getwishmsg = objdatabaseadapter.GetWishmsg();
                preferenceMangr.pref_putString("getwishmsg",objdatabaseadapter.GetWishmsg());

                if(!preferenceMangr.pref_getString("getsalesclosecount").equals("0") && !preferenceMangr.pref_getString("getsalesclosecount").equals("null") &&
                        !preferenceMangr.pref_getString("getsalesclosecount").equals("") && !preferenceMangr.pref_getString("getsalesclosecount").equals(null)){
                   /* String  getorderschedulecode = objdatabaseadapter.GetScheduleCode();
                    String getordercount = objdatabaseadapter.GetOrderFormCount(getorderschedulecode);
                    if(getordercount.equals("") || getordercount.equals("0") || getordercount.equals("null")){*/
                        OrderForm.setVisibility(View.VISIBLE);
                    cashcloseview.setVisibility(View.VISIBLE);
                   /* }else{
                        OrderForm.setVisibility(View.GONE);
                    }*/
                }else{
                    OrderForm.setVisibility(View.GONE);
                    cashcloseview.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        } finally {
            if(objdatabaseadapter!=null)
                objdatabaseadapter.close();
            if (getschedulelist != null)
                getschedulelist.close();
        }




        //Menu handling by Business_type
        if(preferenceMangr.pref_getString("getbusiness_type").equals("2")){
            VanStocktLL.setVisibility(View.GONE);
            AdditionStocktLL.setVisibility(View.GONE);
            Cash_Report_LL.setVisibility(View.VISIBLE);
            Sales_Return_ReporsLL.setVisibility(View.GONE);
            Cash_Close_LL.setVisibility(View.VISIBLE);
            Sales_Layout.setVisibility(View.GONE);
            ReportsOrderChildLL.setVisibility(View.VISIBLE);
            SalesOrderLL.setVisibility(View.VISIBLE);
        }else   if(preferenceMangr.pref_getString("getbusiness_type").equals("1")){
            Cash_Close_LL.setVisibility(View.GONE);
            SalesOrderLL.setVisibility(View.GONE);
            ReportsOrderChildLL.setVisibility(View.GONE);
        }else{
            SalesOrderLL.setVisibility(View.VISIBLE);
            Cash_Close_LL.setVisibility(View.GONE);
            ReportsOrderChildLL.setVisibility(View.VISIBLE);
        }



        generatePDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // String Vname=preferenceMangr.pref_getString("getvanname");
                if (PermissionManager.getInstance().checkAndAskPermissionForThisEvent(MenuActivity.this, Constants.WRITE_EXTERNAL_STORAGE_PERMISSION)) {
					DataBaseAdapter dataBaseAdapter=null;
					dataBaseAdapter = new DataBaseAdapter(context);
					dataBaseAdapter.open();
					String todayschedulecount = "";
					try{
                        todayschedulecount = dataBaseAdapter.gettodayschedulecount();
                        if(todayschedulecount.equals("0")){
                            Toast toast = Toast.makeText(getApplicationContext(),"No schedule for today", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }else{
                            //GeneratePDFDOC();

                            if (Utilities.isNetworkAvailable(context)) {

                                String vanname = preferenceMangr.pref_getString("getvanname");;
                                vanname =vanname.replace(" ","");
                                long date = System.currentTimeMillis();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_hh:mm");
                                String dateString = sdf.format(date);

                                ArrayList<Object> downloadData = new ArrayList<>();
                                downloadData.add(0,preferenceMangr.pref_getString("ipaddress")+"/PDF/"+preferenceMangr.pref_getString("getformatdate")+"/CashSummaryReport_"+vanname+"_" + preferenceMangr.pref_getString("getformatdate") +".pdf");
                                downloadData.add(1,"CashSummaryReport_"+vanname+"_" + dateString + ".pdf");
                               // new AsyncDownloadPdf().execute("http://"+preferenceMangr.pref_getString("ipaddress")+"/PDF/"+preferenceMangr.pref_getString("getformatdate")+"/CashSummaryReport_"+vanname+"_" + preferenceMangr.pref_getString("getformatdate") +".pdf" , "CashSummaryReport_"+vanname+"_" + dateString + ".pdf");
                                downloadFileName = "CashSummaryReport_"+vanname+"_" + dateString + ".pdf";
                                AsyncTaskClass asyncTaskClass = new AsyncTaskClass(context,dropboxlistener,downloadData,Constants.DOWNLOAD_PDF);
                                asyncTaskClass.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }else{
                                Toast toast = Toast.makeText(getApplicationContext(),"Please check internet connection", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        }
                    }catch (Exception e){
                        DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                        mDbErrHelper.open();
                        String geterrror = e.toString();
                        mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        mDbErrHelper.close();
                    }finally {
                        dataBaseAdapter.close();
                    }
                }

            }
        });

        SettingsLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, SettingsActivity.class);
                startActivity(i);
            }
        });

        try{
            boolean firstTimeSync = preferenceMangr.pref_getBoolean("FirstTimeSync");
            if(firstTimeSync != true){
                preferenceMangr.pref_putBoolean("FirstTimeSync",true);
                new AsyncSyncAllDetails().execute();
            }
        }catch (Exception e){
            //Toast.makeText(context, String.valueOf(e), Toast.LENGTH_SHORT).show();
            Log.d("Refresh Error : ", e.toString());
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case Constants.WRITE_EXTERNAL_STORAGE_PERMISSION:
            /*case Constants.STORAGE_PERMISSION_ANDROID_14:
            case Constants.STORAGE_PERMISSION_ANDROID_13:
            case Constants.STORAGE_PERMISSION_ANDROID_11:*/
                if (grantResults.length > 0) {
                    boolean permissionGranted = true;
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                            permissionGranted = false;
                    }
                    if (permissionGranted) {
                        Toast toast1 = Toast.makeText(MenuActivity.this, "Permission Granted", Toast.LENGTH_LONG);
                        toast1.setGravity(Gravity.CENTER, 0, 0);
                        toast1.show();

                        DataBaseAdapter dataBaseAdapter = null;
                        dataBaseAdapter = new DataBaseAdapter(context);
                        dataBaseAdapter.open();
                        String todayschedulecount = "";
                        try {
                            todayschedulecount = dataBaseAdapter.gettodayschedulecount();
                            if (todayschedulecount.equals("0")) {
                                Toast toast = Toast.makeText(getApplicationContext(), "No schedule for today", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            } else {
                                networkstate = isNetworkAvailable();
                                if (networkstate == true) {
                                    String vanname = preferenceMangr.pref_getString("getvanname");
                                    vanname = vanname.replace(" ", "");
                                    long date = System.currentTimeMillis();
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_hh:mm");
                                    String dateString = sdf.format(date);

                                    ArrayList<Object> downloadData = new ArrayList<>();
                                    downloadData.add(0, "http://" + preferenceMangr.pref_getString("ipaddress") + "/PDF/" + preferenceMangr.pref_getString("getformatdate") + "/CashSummaryReport_" + vanname + "_" + preferenceMangr.pref_getString("getformatdate") + ".pdf");
                                    downloadData.add(1, "CashSummaryReport_" + vanname + "_" + dateString + ".pdf");
                                    downloadFileName = "CashSummaryReport_" + vanname + "_" + dateString + ".pdf";
                                    //new AsyncDownloadPdf().execute("http://"+preferenceMangr.pref_getString("ipaddress")+"/PDF/"+ preferenceMangr.pref_getString("getformatdate") +"/CashSummaryReport_"+vanname+"_"+ preferenceMangr.pref_getString("getformatdate") +".pdf" , "CashSummaryReport_"+vanname+"_" + dateString + ".pdf");
                                    AsyncTaskClass asyncTaskClass = new AsyncTaskClass(context, dropboxlistener, downloadData, Constants.DOWNLOAD_PDF);
                                    asyncTaskClass.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                } else {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Please check internet connection", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                            }
                        } catch (Exception e) {
                            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                            mDbErrHelper.open();
                            String geterrror = e.toString();
                            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            mDbErrHelper.close();
                        } finally {
                            dataBaseAdapter.close();
                        }

                    } else {
                        Toast toast = Toast.makeText(MenuActivity.this, "Permission Denied", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
                break;

            default:
                break;
        }
        if (requestCode == Constants.PERMISSION_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (requestCode == StorageRequestCode) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(context, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
                DataBaseAdapter dataBaseAdapter = null;
                dataBaseAdapter = new DataBaseAdapter(context);
                dataBaseAdapter.open();
                String todayschedulecount = "";
                try {
                    todayschedulecount = dataBaseAdapter.gettodayschedulecount();
                    if (todayschedulecount.equals("0")) {
                        Toast toast = Toast.makeText(getApplicationContext(), "No schedule for today", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else {
                        networkstate = isNetworkAvailable();
                        if (networkstate == true) {
                            String vanname = preferenceMangr.pref_getString("getvanname");
                            vanname = vanname.replace(" ", "");
                            long date = System.currentTimeMillis();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_hh:mm");
                            String dateString = sdf.format(date);

                            ArrayList<Object> downloadData = new ArrayList<>();
                            downloadData.add(0, "http://" + preferenceMangr.pref_getString("ipaddress") + "/PDF/" + preferenceMangr.pref_getString("getformatdate") + "/CashSummaryReport_" + vanname + "_" + preferenceMangr.pref_getString("getformatdate") + ".pdf");
                            downloadData.add(1, "CashSummaryReport_" + vanname + "_" + dateString + ".pdf");
                            downloadFileName = "CashSummaryReport_" + vanname + "_" + dateString + ".pdf";
                            //new AsyncDownloadPdf().execute("http://"+preferenceMangr.pref_getString("ipaddress")+"/PDF/"+ preferenceMangr.pref_getString("getformatdate") +"/CashSummaryReport_"+vanname+"_"+ preferenceMangr.pref_getString("getformatdate") +".pdf" , "CashSummaryReport_"+vanname+"_" + dateString + ".pdf");
                            AsyncTaskClass asyncTaskClass = new AsyncTaskClass(context, dropboxlistener, downloadData, Constants.DOWNLOAD_PDF);
                            asyncTaskClass.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), "Please check internet connection", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                } catch (Exception e) {
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                } finally {
                    dataBaseAdapter.close();
                }

            } else if (requestCode == Constants.PERMISSION_LOCATION) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "Location Permission Denied", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(context, "Location Permission Granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Checking internet connection
    //sdfdfg
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

    public void goBack(View v) {
        DataBaseAdapter objdatabaseadapter = null;
        String getschedulecount = "0";
        String getschedulestatus ="no";
        try {
            //Save Schdule Functionality
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            getschedulecount = objdatabaseadapter.GetScheduleCountDB();
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
        /*if(getschedulestatus.equals("yes")) {
            if (getschedulecount.equals("0")) {
                Intent i = new Intent(context, ScheduleActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirmation");
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
        }else {*/
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

        //}


    }

    @Override
    public void onBackPressed() {
        goBack(null);
    }


    protected class AsyncDownloadPdf extends
            AsyncTask<String, JSONObject,String> {
        JSONObject jsonObj = null;

        String fileUrl,fileName;
        int code;
        @Override
        protected String doInBackground(String... params) {

            fileUrl = params[0];
            fileName = params[1];
            downloadFileName = params[1];
            String result = "";
            try {
                String vanname = preferenceMangr.pref_getString("getvanname");
                vanname =vanname.replace(" ","");
                RestAPI api = new RestAPI();
                networkstate = isNetworkAvailable();
                if (networkstate == true) {

                    jsonObj = api.DownloadPdf(preferenceMangr.pref_getString("getvancode"),preferenceMangr.pref_getString("getschedulecode"),preferenceMangr.pref_getString("getfinanceyrcode"),vanname,"downloadpdf.php");
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Please check internet connection", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    result = "";
                    //Toast.makeText(getApplicationContext(),"Please check internet connection",Toast.LENGTH_SHORT).show();
                }
                /*else{
                    result = "";
                }*/
                if(jsonObj!=null) {
                    if(jsonObj.has("success")) {
                        result = jsonObj.getString("success");
                    }
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block

                result="";
                Log.d("AsyncDownloadPDF", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), "AsyncDownloadPDF", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();

            }
            return result;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            downloadLoading = ProgressDialog.show(context, "Downloading", "Please wait...", true);
            downloadLoading.setCancelable(false);
            downloadLoading.setCanceledOnTouchOutside(false);
        }

        protected void onPostExecute(final String result) {
            try {

                /*DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
                request.setDestinationUri(Uri.fromFile(destinationFile));
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); // to notify when download is complete
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                request.allowScanningByMediaScanner();// if you want to be available from media players
                DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                manager.enqueue(request);
                long downloadID = downloadManager.enqueue(request); */

                downloadLoading.dismiss();
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    downloadTaskForAndroid11(fileUrl,fileName);
                } else {
                    downloadTaskForAndroid6(fileUrl,fileName);
                }


                //mToast(mContext, "Starting download...");
                //registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

            } catch (Exception e) {
                Log.d("AsyncDownloadPDF", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), "AsyncDownloadPDF", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }

        }

    }

    public static class FileDownloader {
        private static final int  MEGABYTE = 1024 * 1024;

        public static void downloadFile(String fileUrl, File directory){
            try {

                int code;
                Boolean result=false;

                URL url = new URL(fileUrl);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);
                /*urlConnection.setRequestMethod("HEAD");
                urlConnection.setConnectTimeout(3000);*/
                urlConnection.connect();
                code = urlConnection.getResponseCode();

                if(code==200){
                    InputStream inputStream = urlConnection.getInputStream();
                    FileOutputStream fileOutputStream = new FileOutputStream(directory);
                    int totalSize = urlConnection.getContentLength();

                    byte[] buffer = new byte[MEGABYTE];
                    int bufferLength = 0;
                    while((bufferLength = inputStream.read(buffer))>0 ){
                        fileOutputStream.write(buffer, 0, bufferLength);
                    }
                    fileOutputStream.close();
                    Toast.makeText(context1,"Successfully file Downloaded...!",Toast.LENGTH_SHORT).show();

                    openPDF();

                }else{
                    Toast.makeText(context1,"No file to Download...!",Toast.LENGTH_SHORT).show();
                }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.d("AsyncDownloadPDF", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context1);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), "AsyncDownloadPDF", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d("AsyncDownloadPDF", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context1);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), "AsyncDownloadPDF", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("AsyncDownloadPDF", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context1);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), "AsyncDownloadPDF", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        }
    }

    public static void openPDF(){
        String vanname = preferenceMangr.pref_getString("getvanname");
        vanname =vanname.replace(" ","");

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/PDF/"+ preferenceMangr.pref_getString("getformatdate") +"/CashSummaryReport_"+vanname+"_"+ preferenceMangr.pref_getString("getformatdate") +".pdf");

        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(Uri.fromFile(file),"application/pdf");

        Toast.makeText(context1,String.valueOf(Uri.fromFile(file)),Toast.LENGTH_SHORT).show();

        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        //target.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        target.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        /*PackageManager pm = context1.getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setType("application/pdf");*/

        Intent intent = Intent.createChooser(target, "Open File");
        /*List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        if (resInfo.size() > 0) {
            try {
                context1.startActivity(intent);

            } catch (Throwable e) {
                Toast.makeText(context1, e.toString(), Toast.LENGTH_SHORT).show();
                Log.d("File chooser error : ",e.toString());
                // PDF apps are not installed
            }
        } else {
            Toast.makeText(context1, "PDF apps are not installed 2", Toast.LENGTH_SHORT).show();
        }*/
        try {
            context1.startActivity(Intent.createChooser(intent,"Open File"));
            //menuActivity.startActivityForResult(intent,100);
        } catch (ActivityNotFoundException e) {
            // Instruct the user to install a PDF reader here, or something
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100){
            Intent intent = Intent.createChooser(data, "Open File");
            startActivity(intent);
        }
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
            String deviceid = preferenceMangr.pref_getString("deviceid");
            try {
                dataBaseAdapter = new DataBaseAdapter(context);
                dataBaseAdapter.open();
                networkstate = isNetworkAvailable();
                if (networkstate == true) {
                    //Get Van Master
                    ////if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Van master");
                    jsonObj = api.GetAllDetails(deviceid, "syncvanmaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncvanmaster(jsonObj);
                        api.udfnSyncDetails(deviceid, "vanmaster", "0", "");
                    }
                    //General settings
                    ////if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Generalsettings");
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
                            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                            mDbErrHelper.open();
                            String geterrror = e.toString();
                            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            mDbErrHelper.close();
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
                             Log.w("Menu Activity : "," Sync All : Salesschedule");
                        jsonObj = api.GetAllDetails(deviceid,"syncsalesschedulemobile.php",context);
                        if (isSuccessful(jsonObj)) {
                            dataBaseAdapter.syncsalesschedulemobile(jsonObj);
                            api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"),"salesschedule",preferenceMangr.pref_getString("getvancode"),"");
                        }
                    }else{
                        //Sales Schedule portal
                        ////if(BuildConfig.DEBUG)
                             Log.w("Menu Activity : "," Sync All : Salesschedule portal");
                        jsonObj = api.GetAllDetails(deviceid,"syncsalesschedule.php",context);
                        if (isSuccessful(jsonObj)) {
                            dataBaseAdapter.syncsalesschedule(jsonObj);
                            api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"),"salesscheduleportal",preferenceMangr.pref_getString("getvancode"),"");
                        }
                    }

                    /*****SYNCH ALL MASTER************/
                    //company master
                    ////if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Company master");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"synccompanymaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.synccompanymaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "companymaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    ////if(BuildConfig.DEBUG)
                        Log.w("Menu Activity : "," Sync All : UPI Vender Details");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"), "syncupivendermaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncupivendermaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "upivendermaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    ////if(BuildConfig.DEBUG)
                        Log.w("Menu Activity : "," Sync All : Company Vender Master");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"), "synccompanyvenderdetails.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.synccompanyvenderdetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "companyvendermaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //area master
                    ////if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Area master");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncareamaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncareamaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "areamaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //brand master
                    ////if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Brand master");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncbrandmaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncbrandmaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "brandmaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //currency
                    ////if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Currency");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"synccurrency.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.synccurrency(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "currency", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //Receipt remarks
                    ////if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Receipt Remarks");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncreceiptremarks.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncreceiptremarks(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "receiptremarks", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }
                    //tax
                    ////if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Tax");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"synctax.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.synctax(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "tax", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }
                    //Bill type
                    ////if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Bill type");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncbilltype.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncbilltype(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "billtype", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }
                    //city
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : City Master");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"synccitymaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.synccitymaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "citymaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //employee catergory
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Employee category");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncemployeecategory.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncemployeecategory(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "employeecategory", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //employee master
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Employee master");
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
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Customer");
                    jsonObj = api.GetCustomerDetails(preferenceMangr.pref_getString("deviceid"),preferenceMangr.pref_getString("getroutecode"),"synccustomer.php");
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.synccustomer(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "customer", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    // cashnotpaiddetails
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Cash not paid details");
                    jsonObj = api.GetCashNotPaidDetails(preferenceMangr.pref_getString("deviceid"),preferenceMangr.pref_getString("getvancode"),preferenceMangr.pref_getString("getroutecode"),"synccashnotpaiddetails.php");
                    dataBaseAdapter.DeleteCashNotPaid();
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.synccashnotpaiddetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "cashnotpaiddetails", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }



                    //expenses head
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Expenses head");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncexpenseshead.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncexpenseshead(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "expenseshead", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //financial year
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Financialyear");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncfinancialyear.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncfinancialyear(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "financialyear", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //general settings
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : General settings");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncgeneralsettings.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncgeneralsettings(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "generalsettings", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //itemgroup master
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Item group master");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncitemgroupmaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncitemgroupmaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "itemgroupmaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //item master
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Item master");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncitemmaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncitemmaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "itemmaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //itemn price list transaction
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Item pricelist transaction");
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
                         Log.w("Menu Activity : "," Sync All : Item subgroup master");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncitemsubgroupmaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncitemsubgroupmaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "itemsubgroupmaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //route
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Route");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncroute.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncroute(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "route", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //route details
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Route details");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncroutedetails.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncroutedetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "routedetails", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }
                    //transport mode
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Transport mode");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"synctransportmode.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.synctransportmode(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "transportmode", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //transport
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Transport");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"synctransport.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.synctransport(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "transport", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //transportareamapping
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Transport city mapping");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"synctransportcitymapping.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.synctransportareamapping(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "transportcitymapping", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //scheme
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Scheme");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncscheme.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncscheme(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "scheme", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //scheme item details
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Scheme item details");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncschemeitemdetails.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncschemeitemdetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "schemeitemdetails", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //scheme rate details
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Scheme rate details");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncschemeratedetails.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncschemeratedetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "schemeratedetails", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //unit master
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Unit master");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncunitmaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncunitmaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "unitmaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //van stock
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Van stock");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncvanstock.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncvanstocktransaction(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "vanstock", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //vehicle master
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Vehicle master");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncvehiclemaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncvehiclemaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "vehiclemaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //voucher settings
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Voucher settings");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncvouchersettings.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncvouchersettings(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "vouchersettings", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //expenses
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Expenses");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncexpenses.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncexpensesmaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "expenses", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //cashclose
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Cash close");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"synccashclose.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.synccashclose(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "cashclose", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //sales close
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Sales close");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncsalesclose.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncsalesclose(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "salesclose", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //cashreport
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Cash report");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"synccashreport.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.synccashreport(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "cashreport", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //denomination
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Denomination");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncdenomination.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncdenomination(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "denomination", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //receipt
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Receipt");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncreceipt.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncreceipt(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "receipt", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //order details
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Order details");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncorderdetails.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncorderdetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "orderdetails", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //Sales
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Sales");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncsales.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncsales(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "sales", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //Salesitemdetails
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Sales item details");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncsalesitemdetails.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncsalesitemdetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "salesitemdetails", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //Sales order
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Sales order");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncsalesorder.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncsalesorder(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "salesorder", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }
                    //Sales order itemdetails
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Sales order itemdetails");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncsalesorderitemdetails.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncsalesorderitemdetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "salesorderitemdetails", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //Salesreturn
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Sales return");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncsalesreturn.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncsalesreturn(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "salesreturn", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }


                    //Salesretrunitemdetails
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Sales return item details");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncsalesreturnitemdetails.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncsalesreturnitemdetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "salesreturnitemdetails", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //nilstock
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Nil stock");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncnilstock.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncnilstock(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "nilstock", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }


                    LoginActivity.getfinanceyrcode = dataBaseAdapter.GetFinancialYrCode();
                    preferenceMangr.pref_putString("getfinanceyrcode",dataBaseAdapter.GetFinancialYrCode());

                    //maxrefno
                    //if(BuildConfig.DEBUG)
                         Log.w("Menu Activity : "," Sync All : Max refno");
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

                    //if(BuildConfig.DEBUG)
                        Log.w("Menu Activity : "," Sync All : State Master");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"), "syncstatemaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncstatemaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "statemaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }
                    //Not Purchased
                    jsonObj = api.GetNotPurchasedDetails(preferenceMangr.pref_getString(Constants.KEY_DEVICEID), "syncnotpurchaseddetails.php",context);
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
                }


            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.e("AsyncSyncAllDetails DIB", e.getMessage());
                if(dataBaseAdapter != null)
                    dataBaseAdapter.close();
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
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
                    boolean getGPSTrackingStatus = objdatabaseadapter.getGPSTrackingStatus();
                    preferenceMangr.pref_putBoolean(Constants.KEY_GET_GPSTRACKINGSTATUS, getGPSTrackingStatus);
                } catch (Exception e) {
                    if(objdatabaseadapter != null)
                        objdatabaseadapter.close();
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName() + " - SYNC ALL Post", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
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
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName() + " - SYNC ALL Post", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
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
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName() + " - SYNC ALL Post Local DB Clear", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            finally{
                if(objdatabaseadapter!=null){
                    objdatabaseadapter.close();
                }
            }
            loading.dismiss();
        }
    }

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

    public void AsyncCashReportDetails(){
        ArrayList<ScheduleDatas> List = null;
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();
        try {
            JSONObject js_obj = new JSONObject();
            JSONObject js_obj1 = new JSONObject();
            try {
                DataBaseAdapter dbadapter = new DataBaseAdapter(context);
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
    //Cash close Details
    public void AsyncCloseCashDetails(){
        ArrayList<ScheduleDatas> List = null;
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();
        try {
            JSONObject js_obj = new JSONObject();
            JSONObject js_obj1 = new JSONObject();
            try {
                DataBaseAdapter dbadapter = new DataBaseAdapter(context);
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
    /********CUSTOMER DETAILS***********/
    public void AsyncCustomerDetails(){
        ArrayList<CustomerDatas> List = null;
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();
        try {
            JSONObject js_obj = new JSONObject();
            try {
                DataBaseAdapter dbadapter = new DataBaseAdapter(context);
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
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("Customer", e.getMessage());
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }
    /**********EXPENSE ACTIVITY*********/
    public void  AsyncExpenseDetails(){
        ArrayList<ExpenseDetails> List = null;
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();
        try {
            JSONObject js_obj = new JSONObject();
            try {
                DataBaseAdapter dbadapter = new DataBaseAdapter(context);
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
    //Expebse cancel details
    public void  AsyncCancelExpenseDetails(){
        ArrayList<ExpenseDetails> List = null;
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();
        try {
            JSONObject js_obj = new JSONObject();
            try {
                DataBaseAdapter dbadapter = new DataBaseAdapter(context);
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
    /************next day requirement details*******/
    public  void AsyncOrderDetails(){
        ArrayList<ScheduleDatas> List = null;
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();
        try {
            JSONObject js_obj = new JSONObject();
            try {
                DataBaseAdapter dbadapter = new DataBaseAdapter(context);
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
    /***************** SALES DETAILS *********************************/
    public void  AsyncSalesDetails(){
        ArrayList<SalesSyncDatas> List = null;
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();
        try {
            JSONObject js_obj = new JSONObject();
            JSONObject js_salesobj = new JSONObject();
            JSONObject js_salesitemobj = new JSONObject();
            JSONObject js_stockobj = new JSONObject();
            try {
                DataBaseAdapter dbadapter = new DataBaseAdapter(context);
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
                    Log.d("AsyncSalesDetails", e.getMessage());
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
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
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("AsyncSalesDetails", e.getMessage());
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }
    //Sales  update receipt and bill wise
    public void  AsyncUpdateSalesReceiptDetails(){
        ArrayList<SalesSyncDatas> List = null;
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();
        try {
            JSONObject js_salesobj = new JSONObject();
            try {
                DataBaseAdapter dbadapter = new DataBaseAdapter(context);
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
                    Log.d("AsyncSalesDetails", e.getMessage());
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
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
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("AsyncSalesDetails", e.getMessage());
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }
    //Sales Close
    public void AsyncCloseSalesDetails(){
        ArrayList<ScheduleDatas> List = null;
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();
        try {
            JSONObject js_obj = new JSONObject();
            try {
                DataBaseAdapter dbadapter = new DataBaseAdapter(context);
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
    //Sales Cancel
    public void AsyncSalesCancelDetails(){
        ArrayList<SalesSyncDatas> List = null;
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();
        try {
            JSONObject js_salesobj = new JSONObject();
            JSONObject js_salesitemobj = new JSONObject();
            JSONObject js_stockobj = new JSONObject();
            try {
                DataBaseAdapter dbadapter = new DataBaseAdapter(context);
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
                    Log.d("AsyncSalesDetails", e.getMessage());
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
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
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("AsyncSalesDetails", e.getMessage());
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }

    /***************** Sales Return DETAILS *********************************/
    public void AsyncSalesReturnDetails(){
        ArrayList<SalesSyncDatas> List = null;
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();
        try {
            JSONObject js_salesobj = new JSONObject();
            JSONObject js_salesitemobj = new JSONObject();
            JSONObject js_stockobj = new JSONObject();
            try {
                DataBaseAdapter dbadapter = new DataBaseAdapter(context);
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
                    Log.d("AsyncSalesreturnDetails", e.getMessage());
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
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
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("AsyncSalesDetails", e.getMessage());
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }
    //Cancel sales return
    public void AsyncSalesReturnCancelDetails(){
        ArrayList<SalesSyncDatas> List = null;
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();
        try {
            JSONObject js_salesobj = new JSONObject();
            JSONObject js_stockobj = new JSONObject();
            try {
                DataBaseAdapter dbadapter = new DataBaseAdapter(context);
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
                    Log.d("AsyncSalesDetails", e.getMessage());
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
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
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("AsyncSalesDetails", e.getMessage());
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }
    /***************Receipt detils********/
    public void AsyncReceiptDetails(){
        ArrayList<ReceiptTransactionDetails> List = null;
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();
        try {
            JSONObject js_obj = new JSONObject();
            try {
                DataBaseAdapter dbadapter = new DataBaseAdapter(context);
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
    //Receipt cancel details
    public  void  AsyncCancelReceiptDetails(){
        ArrayList<ReceiptTransactionDetails> List = null;
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();
        try {
            JSONObject js_obj = new JSONObject();
            try {
                DataBaseAdapter dbadapter = new DataBaseAdapter(context);
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

    public void AsyncNilStockDetails(){
        RestAPI api = new RestAPI();
        try {
            JSONObject js_obj = new JSONObject();
            ArrayList<ScheduleDatas> List = null;
            try {
                DataBaseAdapter dbadapter = new DataBaseAdapter(context);
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

    public void AsyncSalesOrderDetails(){
        ArrayList<SalesSyncDatas> List = null;
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();
        String result = "";
        try {
            JSONObject js_obj = new JSONObject();
            JSONObject js_salesobj = new JSONObject();
            JSONObject js_salesitemobj = new JSONObject();
            try {
                DataBaseAdapter dbadapter = new DataBaseAdapter(context);
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
                    Log.d("AsyncSalesDetails", e.getMessage());
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
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
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("AsyncSalesDetails", e.getMessage());
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }

    //Async sales ordercancel
    public void AsyncSalesOrderCancelDetails(){
        ArrayList<SalesSyncDatas> List = null;
        JSONObject jsonObj = null;
        RestAPI api = new RestAPI();
        try {
            JSONObject js_salesobj = new JSONObject();
            JSONObject js_salesitemobj = new JSONObject();
            try {
                DataBaseAdapter dbadapter = new DataBaseAdapter(context);
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
                    Log.d("AsyncSalesDetails", e.getMessage());
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
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
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("AsyncSalesDetails", e.getMessage());
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }

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
            try{
                // Bitmap bitmap = params[0];
                DataBaseAdapter objcustomerAdaptor = new DataBaseAdapter(context);
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
                Log.d("AsyncScheduleDetails", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }


            return result;
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

    /*@Override
    protected void onResume() {
        super.onResume();
        if(!preferenceMangr.pref_getString("getsalesclosecount").equals("0") && !preferenceMangr.pref_getString("getsalesclosecount").equals("null") &&
                !preferenceMangr.pref_getString("getsalesclosecount").equals("") && !preferenceMangr.pref_getString("getsalesclosecount").equals(null))
        {
            MenuActivity.OrderForm.setVisibility(View.VISIBLE);
        }else{
            MenuActivity.OrderForm.setVisibility(View.GONE);
        }
    }*/
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
                                Log.d("downloaded uriString : ",uriString);

                                String decodedPath = Uri.decode(uriString);
                                Log.d("downloaded decoded uriString : ",decodedPath);
                                //TODO : Use this local uri and launch intent to open file
                                try{

                                    Toast.makeText(context,"Successfully file Downloaded...!",Toast.LENGTH_SHORT).show();

                                    String filename = decodedPath;
                                    filename = filename.replace("file://","");
                                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                                        Utilities.SharePDFToAll_Above_Android9(MenuActivity.this, getApplicationContext(), filename);
                                    } else {
                                        Utilities.SharePDFToAll_Below_Android9(MenuActivity.this, filename);
                                    }

                                    /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){

                                        File sourceFile =  new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator + Uri.decode(decodedPath) );
                                        //File destinationFile =  new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath() + File.separator + Uri.decode(downloadFileName));

                                        //boolean fileCopied =  Utilities.copyFileUsingChannel(sourceFile.getAbsoluteFile(), destinationFile.getAbsoluteFile());

                                        // if(fileCopied){

                                        // Zip the PDF file
                                        String[] s = new String[1];
                                        String filename = decodedPath;
                                        filename = filename.replace("file://","");
                                        s[0] = filename;


                                        //filename = filename.replace("file://","");
                                        filename = filename.replace(" ","_");
                                        filename = filename.replace(".pdf",".zip");
                                        // create the zip
                                        ShareDataBaseBackup.zip(s, filename);

                                        sourceFile =  new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator + Uri.decode(filename) );

                                        Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID , new File(sourceFile.getPath()));
                                        String decodedURIString = Uri.decode(uri.toString());
                                        Uri decodedURI = Uri.parse(decodedURIString);

                                        Log.d("URI String : ", uri.toString());
                                        Log.d("URI String : decodedURIString ", decodedURIString);
                                        Log.d("URI String : decodedURI ", decodedURI.toString());
                                        Log.d("URI String : downloadFileName ", downloadFileName);

                                        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(sourceFile).toString());
                                        String mimeType = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

                                        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                                        intent.setType("files/pdf");
                                        intentShareFile.putExtra(Intent.EXTRA_STREAM, decodedURI );//Uri.fromFile(new File(file.getPath() + File.separator + Uri.decode(downloadFileName));
                                        intentShareFile.setDataAndType(decodedURI ,mimeType);
                                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                                        for (ResolveInfo resolveInfo : resInfoList) {
                                            String packageName = resolveInfo.activityInfo.packageName;
                                            context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                            context.revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        }

                                        startActivity(Intent.createChooser(intentShareFile,"Share File"));
                                        // }


                                    } else {
                                        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                                        String extDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

                                        File fileInDir = new File( extDirPath +"/"+ downloadFileName);

                                        if(fileInDir.exists()) {
                                            intentShareFile.setType("application/pdf");
                                            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse(decodedPath));
                                            //intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");

                                            startActivity(Intent.createChooser(intentShareFile, "Share File"));
                                        }
                                    }*/




                                }catch (Exception e){
                                    Log.d("Share the file in android 11 " , e.getLocalizedMessage());
                                }
                            }
                        }
                    }



                }
            }catch (Exception e){
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - Exception in share the downloaded file ", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }


        }
    };

    private boolean downloadTaskForAndroid6(String downloadURL,String downloadFileName) throws Exception {
        if (!downloadURL.startsWith("http")) {
            return false;
        }

        try {
            //File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));

            String extDirPath = "";
            File destDir = null;
            /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                //extDirPath = Environment.getExternalStorageDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                //context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString();
                destDir = new File( Environment.getExternalStorageDirectory(),"Download");
            } else {*/
                extDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                destDir = new File( extDirPath );
            //}

            if (!destDir.exists())
                destDir.mkdirs();

            File downloadFile = new File(destDir.getAbsolutePath() + File.separator + downloadFileName);

            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadURL));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
            request.setDestinationUri(Uri.fromFile(downloadFile));
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            if (downloadManager != null) {
                long downloadID = downloadManager.enqueue(request);
            }

            /*MediaScannerConnection.scanFile(MenuActivity.this, new String[]{downloadFile.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                    }
                });*/
            //downloadLoading.dismiss();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_EXPORTED);
            } else {
                registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            }

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private void downloadTaskForAndroid11(String downloadURL,String downloadFileName) throws Exception {
        if (!downloadURL.startsWith("http")) {
            return;
        }
        //String name = "temp.mcaddon";
        try {
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "Download");
            if (!file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.mkdirs();
            }
            File result = new File(file.getAbsolutePath() + File.separator + downloadFileName);

            long downloadId=0;
            final DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadURL));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
            request.setDestinationUri(Uri.fromFile(result));
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            if (downloadManager != null) {
                downloadId = downloadManager.enqueue(request);

            }


            //mToast(mContext, "Starting download...");
            final long finalDownloadId = downloadId;
            MediaScannerConnection.scanFile(MenuActivity.this, new String[]{result.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            /*try {
                                //downloadManager.openDownloadedFile(finalDownloadId);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }*/
                        }
                    });
            /*boolean flag = true;
            boolean downloading =true;
            try{
                DownloadManager mManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request mRqRequest = new DownloadManager.Request(
                        Uri.parse(downloadURL));
                long idDownLoad=mManager.enqueue(mRqRequest);
                DownloadManager.Query query = new DownloadManager.Query();
                Cursor c = null;
                if(query!=null) {
                    query.setFilterByStatus(DownloadManager.STATUS_FAILED|DownloadManager.STATUS_PAUSED|DownloadManager.STATUS_SUCCESSFUL|DownloadManager.STATUS_RUNNING|DownloadManager.STATUS_PENDING);
                } else {
                    return;
                }

                while (downloading) {
                    c = mManager.query(query);
                    if(c.moveToFirst()) {
                        Log.i ("FLAG","Downloading");
                        int status =c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));

                        if (status==DownloadManager.STATUS_SUCCESSFUL) {
                            Log.i ("FLAG","done");
                            downloading = false;
                            flag=true;
                            break;
                        }
                        if (status==DownloadManager.STATUS_FAILED) {
                            Log.i ("FLAG","Fail");
                            downloading = false;
                            flag=false;
                            break;
                        }
                    }
                }

                //return flag;
            }catch (Exception e) {
                flag = false;
                //return flag;
            }*/
        } catch (Exception e) {
            Log.e(">>>>>", e.toString());
            //mToast(this, e.toString());
            //return false;
        }
        //return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void onReceiveAsyncResult(Object response, int requestType) {
        switch (requestType) {
            case Constants.DOWNLOAD_PDF:
                //shareTheDownLoadedFile();
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

                }

                break;

        }
    }
    public void AsyncNotPurchasedDetails() {
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
                    obj.put("syncstatus", mCursales.getString(9));
                    obj.put("salestime", mCursales.getString(10));
                    obj.put("latlong", mCursales.getString(11));

                    js_array2.put(obj);
                    mCursales.moveToNext();
                }

                js_salesobj.put("JSonObject", js_array2);

                jsonObj = api.insertNotPurchasedDetails(js_salesobj.toString(),context);
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
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    Log.d("AsyncNotPurchasedDetails", e.getMessage());
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName() + " - AsyncNotPurchasedDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                } finally {
                    if (objdatabaseadapter != null)
                        objdatabaseadapter.close();
                }
            } catch (Exception e) {
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName() + " - AsyncNotPurchasedDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("AsyncSalesDetails", e.getMessage());
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }
}
