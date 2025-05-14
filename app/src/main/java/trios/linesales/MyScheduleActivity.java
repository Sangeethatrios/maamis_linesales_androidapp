package trios.linesales;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MyScheduleActivity extends AppCompatActivity   {
    public Context context;
    TextView txtscheduledate,txtschedulevanname,txtschedulevehiclename,txtscheduleroutename,txtschedulesalesrepname,
            txtscheduletripadvance,txtscheduledrivername,txtschedulehelpername,txtschedulestartingkm,
            txtscheduleendingkm,txtewayurl;

    private int year, month, day;
    private Calendar calendar;
    ImageButton goback ,logout;
    ImageView imgpdf;
    String getschdeuleformatdate="";
    LinearLayout LLvehicledoc;
    private static final int WRITE_REQUEST_CODE = 300;
    private static final String TAG = MyScheduleActivity.class.getSimpleName();
    private String url;
    DecimalFormat df = new DecimalFormat("0.00");
    private static final int PERMISSION_STROAGE_CODE = 1000;
    // Progress Dialog
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
    //Button declare
    TextView btn_lunch_start_time,btn_lunch_end_time,txtschedulelunctime,btn_vieweway,txt_eway;
    LinearLayout LLlunctime,LLEndingkm,LLView,LLpdf;
    String getschedulecode="";
    String lunch_start_time = "";
    String lunch_end_time = "";
    boolean networkstate = false;
    ImageButton btn_cashnotpaiddetails;
    public static String ifopenfromschedule="";
//    ListView listView1;

    // File url to download
    private static String file_url = "https://api.androidhive.info/progressdialog/hive.jpg";

    Dialog salescashclose;
    TextView popup_salesclose,popup_cashclose,popup_salesclose_title;
    Dialog pindialog;
    Button btnSubmitpin;
    Pinview pinview;
    LinearLayout LLNoSchedule,LLscheduledetails,LLlunch,LLeway;
    TextView txtnewschedule;
    String getschdeuleformatdate1="";
    String currentdate="";
    GridLayout GLView;
    public static PreferenceMangr preferenceMangr=null;
    ListView lv_freeitemlist;
    String[] FreeItemName,FreeItemOp,FreeItemHandover,FreeItemDistributed,FreeItemBalance,FreeItemCode,FreeItemSNO;
    Dialog freeitemdialog;

    boolean issalesclosepopup=false;
    Dialog dialogstatus;
    String[] venderid,vendername;

    TextView txtupivendername;
    public static  String getpaymentvenderID="0";
    Dialog upipaymentvenderdialog;
    ListView lv_UPIPaymentList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_schedule);

        context =this;

        //Declare all variables
        txtscheduledate = (TextView)findViewById(R.id.txtscheduledate);
        txtschedulevanname = (TextView)findViewById(R.id.txtschedulevanname);
        txtschedulevehiclename = (TextView)findViewById(R.id.txtschedulevehiclename);
        txtscheduleroutename = (TextView)findViewById(R.id.txtscheduleroutename);
        txtschedulesalesrepname = (TextView)findViewById(R.id.txtschedulesalesrepname);
        txtscheduletripadvance = (TextView)findViewById(R.id.txtscheduletripadvance);
        txtscheduledrivername = (TextView)findViewById(R.id.txtscheduledrivername);
        txtschedulehelpername = (TextView)findViewById(R.id.txtschedulehelpername);
        txtschedulestartingkm = (TextView)findViewById(R.id.txtschedulestartingkm);
        txtscheduleendingkm = (TextView)findViewById(R.id.txtscheduleendingkm);
        LLvehicledoc  = (LinearLayout) findViewById(R.id.LLvehicledoc);
        goback = (ImageButton)findViewById(R.id.goback);
        logout = (ImageButton)findViewById(R.id.logout);
        btn_lunch_start_time = (TextView)findViewById(R.id.btn_launch_start_time);
        btn_lunch_end_time = (TextView)findViewById(R.id.btn_launch_end_time);
        txtschedulelunctime = (TextView)findViewById(R.id.txtschedulelunctime);
        LLlunctime = (LinearLayout)findViewById(R.id.LLlunctime);
        LLEndingkm = (LinearLayout)findViewById(R.id.LLEndingkm);
        btn_cashnotpaiddetails = (ImageButton)findViewById(R.id.btn_cashnotpaiddetails);
        LLNoSchedule = (LinearLayout) findViewById(R.id.LLnoscheduledetails);
        LLscheduledetails = (LinearLayout) findViewById(R.id.LLscheduledetails);
        txtnewschedule = (TextView) findViewById(R.id.txtnewschedule);
        LLlunch= (LinearLayout) findViewById(R.id.LLlunch);
        GLView=(GridLayout) findViewById(R.id.GLView);
        LLeway= (LinearLayout) findViewById(R.id.LLeway);
        btn_vieweway=(TextView)findViewById(R.id.btn_vieweway);
        txt_eway=(TextView)findViewById(R.id.txt_eway);

        try {
            preferenceMangr = new PreferenceMangr(context);
        }catch (Exception e){
            Log.d("Preference Manager : ",e.toString());
        }
        //Get Current date
        DataBaseAdapter objdatabaseadapter = null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            //LoginActivity.getformatdate = objdatabaseadapter.GenCreatedDate();
            //LoginActivity.getcurrentdatetime = objdatabaseadapter.GenCurrentCreatedDate();

            preferenceMangr.pref_putString("getformatdate",objdatabaseadapter.GenCreatedDate());
            preferenceMangr.pref_putString("getcurrentdatetime",objdatabaseadapter.GenCurrentCreatedDate());
        }catch (Exception e){
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }finally {
            // this gets called even if there is an exception somewhere above
            if (objdatabaseadapter != null)
                objdatabaseadapter.close();
        }

        try{
            DataBaseAdapter objdatabaseadapter1 = null;

            //Get Schdule
            objdatabaseadapter1 = new DataBaseAdapter(context);
            objdatabaseadapter1.open();
            try{

                ScheduleActivity.getschedulecount = objdatabaseadapter1.GetScheduleCount();
                preferenceMangr.pref_putString("schedule_getschedulecount",objdatabaseadapter1.GetScheduleCount());

                String getschedulecode = objdatabaseadapter1.GetScheduleCode();
                //Get Cash close Count
                ScheduleActivity.getcashclosecount = objdatabaseadapter1.GetCashClose(getschedulecode);
                preferenceMangr.pref_putString("schedule_getcashclosecount",objdatabaseadapter1.GetCashClose(getschedulecode));

                //Get sales close Count
                ScheduleActivity.getsalesclosecount = objdatabaseadapter1.GetSalesClose(getschedulecode);
                preferenceMangr.pref_putString("schedule_getsalesclosecount",objdatabaseadapter1.GetSalesClose(getschedulecode));

                MenuActivity. getsalesclosecount = objdatabaseadapter1.GetSalesClose(getschedulecode);
                preferenceMangr.pref_putString("getsalesclosecount",objdatabaseadapter1.GetSalesClose(getschedulecode));

                ScheduleActivity.getreciptcount = objdatabaseadapter1.GetReceiptCount(getschedulecode);
                preferenceMangr.pref_putString("schedule_getreciptcount",objdatabaseadapter1.GetReceiptCount(getschedulecode));

                ScheduleActivity.getexpensecount = objdatabaseadapter1.GetExpenseCount(getschedulecode);
                preferenceMangr.pref_putString("schedule_getexpensecount",objdatabaseadapter1.GetExpenseCount(getschedulecode));
            }catch (Exception e){
                DataBaseAdapter mDbErrHelper2 = new DataBaseAdapter(context);
                mDbErrHelper2.open();
                mDbErrHelper2.insertErrorLog("MySchedule ScheduleCount : "+e.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper2.close();
            }finally {
                if(objdatabaseadapter1!=null){
                    objdatabaseadapter1.close();
                }
            }
        }catch (Exception e){
            DataBaseAdapter mDbErrHelper2 = new DataBaseAdapter(context);
            mDbErrHelper2.open();
            mDbErrHelper2.insertErrorLog("MySchedule ScheduleCount : "+e.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper2.close();
        }

        //Set Now Date
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        //Set Current date
        txtscheduledate.setText(preferenceMangr.pref_getString("getcurrentdatetime"));
        getschdeuleformatdate = preferenceMangr.pref_getString("getformatdate");
        currentdate=preferenceMangr.pref_getString("getcurrentdatetime");

        //Download vehicle document
        LLvehicledoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if SD card is present or not
             //   new DownloadFileFromURL().execute(file_url);
             if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ){
                 if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                         PackageManager.PERMISSION_DENIED){
                     String[] permisssions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                     requestPermissions(permisssions,PERMISSION_STROAGE_CODE);
                 }else{
                     new DownloadFileFromURL().execute();
                 }
             }else{
                 new DownloadFileFromURL().execute();
             }
            }
        });
        //Schedule date
        txtscheduledate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String vardate = "";
                        String getmonth="";
                        String getdate="";
                        monthOfYear = monthOfYear + 1;
                        if (dayOfMonth < 10) {
                            vardate = "0" + dayOfMonth;
                            getdate = "0" + dayOfMonth;
                        } else {
                            vardate = String.valueOf(dayOfMonth);
                            getdate = String.valueOf(dayOfMonth);
                        }
                        if (monthOfYear < 10) {
                            vardate = vardate + "-" + "0" + monthOfYear;
                            getmonth = "0" + monthOfYear;;
                        } else {
                            vardate = vardate +"-" + monthOfYear;
                            getmonth = String.valueOf(monthOfYear);;
                        }
                        vardate = vardate + "-" + year;
                        getschdeuleformatdate = year+ "-"+getmonth+"-"+getdate;
                        getschdeuleformatdate1=getdate+"-"+getmonth+"-"+year;


                        try {
                            Calendar cal = Calendar.getInstance();
                            Calendar cal1 = Calendar.getInstance();
                            Date curdate = null, seldate = null;

                            SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");
                            curdate = sdf.parse(String.valueOf(currentdate));
                            seldate = sdf.parse(String.valueOf(getschdeuleformatdate1));

                            cal.setTime(curdate);
                            cal1.setTime(seldate);



                            /*if (cal.compareTo(cal1) == 0) {
                                //Toast.makeText(context, "twodays are same", Toast.LENGTH_SHORT).show();
                                txtnewschedule.setVisibility(View.VISIBLE);
                                LLlunch.setVisibility(View.VISIBLE);
                                txtscheduledate.setText(vardate );
                                //Call schedule List
                                GetScheduleList();
                            } else if (cal.compareTo(cal1) < 0) {
                                //Toast.makeText(context, "today is lower", Toast.LENGTH_SHORT).show();
                                //tomorrow is greater then today
                                //txtscheduledate.setText(currentdate);
                                txtnewschedule.setVisibility(View.GONE);
                                Toast toast = Toast.makeText(context, "Date selection no longer to current date", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER,0,0);
                                toast.show();
                            } else if (cal.compareTo(cal1) > 0) {
                                txtnewschedule.setVisibility(View.GONE);
                                LLlunch.setVisibility(View.GONE);
                                txtscheduledate.setText(vardate );
                                //Call schedule List
                                GetScheduleList();
                                //Toast.makeText(context, "today is higher", Toast.LENGTH_SHORT).show();
                            }*/
                            long CURDATE=cal.getTimeInMillis();
                            long SELDATE=cal1.getTimeInMillis();

                            if (CURDATE==SELDATE ) {
                                //Toast.makeText(context, "twodays are same", Toast.LENGTH_SHORT).show();
                                txtnewschedule.setVisibility(View.VISIBLE);
                                LLlunch.setVisibility(View.VISIBLE);
                                txtscheduledate.setText(vardate );
                                //Call schedule List
                                GetScheduleList();
//                                if(getschedulecode!="" || getschedulecode!=null) {
//                                    if (networkstate == true) {
//                                        new AsyncSyncscheduleDetails().execute();
//                                    } else {
//                                        Toast toast = Toast.makeText(getApplicationContext(), "Please check your internet connection", Toast.LENGTH_LONG);
//                                        toast.setGravity(Gravity.CENTER, 0, 0);
//                                        toast.show();
//                                        return;
//                                    }
//                                }

                            } else if (CURDATE < SELDATE) {
                                //Toast.makeText(context, "today is lower", Toast.LENGTH_SHORT).show();
                                //tomorrow is greater then today
                                //txtscheduledate.setText(currentdate);
                                txtnewschedule.setVisibility(View.GONE);
                                //Toast toast = Toast.makeText(context, "Date selection no longer to current date", Toast.LENGTH_SHORT);
                               //toast.setGravity(Gravity.CENTER,0,0);
                               // toast.show();
                                txtscheduledate.setText(vardate );
                                GetScheduleList();
//                                if(getschedulecode!="" || getschedulecode!=null) {
//                                    if (networkstate == true) {
//                                        new AsyncSyncscheduleDetails().execute();
//                                    } else {
//                                        Toast toast = Toast.makeText(getApplicationContext(), "Please check your internet connection", Toast.LENGTH_LONG);
//                                        toast.setGravity(Gravity.CENTER, 0, 0);
//                                        toast.show();
//                                        return;
//                                    }
//                                }

                            } else if (CURDATE > SELDATE) {
                                txtnewschedule.setVisibility(View.GONE);
                                LLlunch.setVisibility(View.GONE);
                                txtscheduledate.setText(vardate );
                                networkstate = isNetworkAvailable();
                                //Call schedule List
                                GetScheduleList();
//                                if(getschedulecode!="" || getschedulecode!=null) {
//                                    if (networkstate == true) {
//                                        new AsyncSyncscheduleDetails().execute();
//                                    } else {
//                                        Toast toast = Toast.makeText(getApplicationContext(), "Please check your internet connection", Toast.LENGTH_LONG);
//                                        toast.setGravity(Gravity.CENTER, 0, 0);
//                                        toast.show();
//                                        return;
//                                    }
//                                }

                                //Toast.makeText(context, "today is higher", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                            mDbErrHelper.open();
                            String geterrror = e.toString();
                            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            mDbErrHelper.close();
                        }

                    }
                };
                DatePickerDialog dpDialog = new DatePickerDialog(context, listener, year, month, day);
                dpDialog.show();
            }
        });

        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack(null);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
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



        //Click start time
        btn_lunch_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!preferenceMangr.pref_getString("schedule_getcashclosecount").equals("0") && !preferenceMangr.pref_getString("schedule_getcashclosecount").equals("null") &&
                        !preferenceMangr.pref_getString("schedule_getcashclosecount").equals("") && !preferenceMangr.pref_getString("schedule_getcashclosecount").equals(null)){
                    Toast toast = Toast.makeText(getApplicationContext(),"Cash Closed", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                if(!preferenceMangr.pref_getString("schedule_getsalesclosecount").equals("0") && !preferenceMangr.pref_getString("schedule_getsalesclosecount").equals("null") &&
                        !preferenceMangr.pref_getString("schedule_getsalesclosecount").equals("") && !preferenceMangr.pref_getString("schedule_getsalesclosecount").equals(null)){
                    Toast toast = Toast.makeText(getApplicationContext(),"Sales Closed", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                starttime_action();
            }
        });

        //Click end time
        btn_lunch_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!preferenceMangr.pref_getString("schedule_getcashclosecount").equals("0") && !preferenceMangr.pref_getString("schedule_getcashclosecount").equals("null") &&
                        !preferenceMangr.pref_getString("schedule_getcashclosecount").equals("") && !preferenceMangr.pref_getString("schedule_getcashclosecount").equals(null)){
                    Toast toast = Toast.makeText(getApplicationContext(),"Cash Closed", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                if(!preferenceMangr.pref_getString("schedule_getsalesclosecount").equals("0") && !preferenceMangr.pref_getString("schedule_getsalesclosecount").equals("null") &&
                        !preferenceMangr.pref_getString("schedule_getsalesclosecount").equals("") && !preferenceMangr.pref_getString("schedule_getsalesclosecount").equals(null)){
                    Toast toast = Toast.makeText(getApplicationContext(),"Sales Closed", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                endtime_action();
            }
        });

        //
        //Click start time
        btn_vieweway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getschedulecode!="" || getschedulecode!=null) {
                    networkstate = isNetworkAvailable();
                    if (networkstate == true) {
                        new AsyncSyncscheduleDetails().execute();
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please check your internet connection", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
//                 return;
                    }
                }
            }
        });
        //Call schedule List
        GetScheduleList();





        //click not paid cash details
        btn_cashnotpaiddetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ifopenfromschedule="yes";
                Intent i = new Intent(context,ReviewCashNotpaidDetails.class);
                startActivity(i);
            }
        });


        txtnewschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    DataBaseAdapter objdatabaseadapter1 = null;

                    //Get Schdule
                    objdatabaseadapter1 = new DataBaseAdapter(context);
                    objdatabaseadapter1.open();
                    String todayschedulecount = objdatabaseadapter1.gettodayschedulecount();
                    if (todayschedulecount.equals("0")) {
                        try {

                            ScheduleActivity.getschedulecount = objdatabaseadapter1.GetScheduleCount();
                            preferenceMangr.pref_putString("schedule_getschedulecount",objdatabaseadapter1.GetScheduleCount());

                            String getschedulecode = objdatabaseadapter1.GetScheduleCode();
                            //Get Cash close Count
                            ScheduleActivity.getcashclosecount = objdatabaseadapter1.GetCashClose(getschedulecode);
                            preferenceMangr.pref_putString("schedule_getcashclosecount",objdatabaseadapter1.GetCashClose(getschedulecode));

                            //Get sales close Count
                            ScheduleActivity.getsalesclosecount = objdatabaseadapter1.GetSalesClose(getschedulecode);
                            preferenceMangr.pref_putString("schedule_getsalesclosecount",objdatabaseadapter1.GetSalesClose(getschedulecode));

                            MenuActivity.getsalesclosecount = objdatabaseadapter1.GetSalesClose(getschedulecode);
                            preferenceMangr.pref_putString("getsalesclosecount",objdatabaseadapter1.GetSalesClose(getschedulecode));

                            ScheduleActivity.getreciptcount = objdatabaseadapter1.GetReceiptCount(getschedulecode);
                            preferenceMangr.pref_putString("schedule_getreciptcount",objdatabaseadapter1.GetReceiptCount(getschedulecode));

                            ScheduleActivity.getexpensecount = objdatabaseadapter1.GetExpenseCount(getschedulecode);
                            preferenceMangr.pref_putString("schedule_getexpensecount",objdatabaseadapter1.GetExpenseCount(getschedulecode));

                            String lastScheduleDate = objdatabaseadapter1.getLastScheduleDate(getschedulecode);

                            if (preferenceMangr.pref_getString("getbusiness_type").equals("2")) {
                                if (!preferenceMangr.pref_getString("schedule_getschedulecount").equals("0") && !preferenceMangr.pref_getString("schedule_getschedulecount").equals("null") && !preferenceMangr.pref_getString("schedule_getschedulecount").equals(null)) {
                                    if (!preferenceMangr.pref_getString("schedule_getcashclosecount").equals("0") && !preferenceMangr.pref_getString("schedule_getcashclosecount").equals("null") &&
                                            !preferenceMangr.pref_getString("schedule_getcashclosecount").equals("")) {

                                        Intent i = new Intent(MyScheduleActivity.this, ScheduleActivity.class);
                                        startActivity(i);

                                    } else {
                                        /*Intent i = new Intent(MyScheduleActivity.this, ScheduleActivity.class);
                                        startActivity(i);*/
                                        /*if (!preferenceMangr.pref_getString("schedule_getreciptcount").equals("0") ||
                                                !preferenceMangr.pref_getString("schedule_getexpensecount").equals("0")) {*/
                                            /*bottomlayout.setVisibility(View.GONE);
                                            btnSaveSchedule.setVisibility(View.VISIBLE);*/
                                            salescashclose = new Dialog(context);
                                            salescashclose.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                            salescashclose.setContentView(R.layout.salescashclose_popup);
                                            salescashclose.setCanceledOnTouchOutside(false);
                                            popup_salesclose = (TextView) salescashclose.findViewById(R.id.popup_salesclose);
                                            popup_cashclose = (TextView) salescashclose.findViewById(R.id.popup_cashclose);
                                            popup_salesclose_title = (TextView) salescashclose.findViewById(R.id.popup_salesclose_title);

                                            popup_salesclose.setVisibility(View.GONE);
                                            popup_cashclose.setText("Close Cash");
                                            if(!Utilities.isNullOrEmpty(lastScheduleDate))
                                                popup_salesclose_title.setText(getString(R.string.previous_schedule_close_popup_title) + " (" +lastScheduleDate + ")");

                                            popup_cashclose.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    LoginActivity.iscash = true;
                                                    Intent i = new Intent(MyScheduleActivity.this, CashReportActivity.class);
                                                    startActivity(i);
                                                    salescashclose.dismiss();

                                                }
                                            });
                                            salescashclose.show();
                                            popup_salesclose.setVisibility(View.GONE);
                                        /*} else {
                                            Intent i = new Intent(MyScheduleActivity.this, ScheduleActivity.class);
                                            startActivity(i);
                                        }*/
                                    }
                                } else {
                                    Intent i = new Intent(MyScheduleActivity.this, ScheduleActivity.class);
                                    startActivity(i);
                                }
                            }else {
                                salescashclose = new Dialog(context);
                                salescashclose.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                salescashclose.setContentView(R.layout.salescashclose_popup);
                                salescashclose.setCanceledOnTouchOutside(false);
                                popup_salesclose = (TextView) salescashclose.findViewById(R.id.popup_salesclose);
                                popup_cashclose = (TextView) salescashclose.findViewById(R.id.popup_cashclose);
                                popup_salesclose_title = (TextView) salescashclose.findViewById(R.id.popup_salesclose_title);
                                if(!Utilities.isNullOrEmpty(lastScheduleDate))
                                    popup_salesclose_title.setText(getString(R.string.previous_schedule_close_popup_title) + " (" +lastScheduleDate + ")");

                                if (!preferenceMangr.pref_getString("schedule_getschedulecount").equals("0") && !preferenceMangr.pref_getString("schedule_getschedulecount").equals("null") && !preferenceMangr.pref_getString("schedule_getschedulecount").equals(null)) {
                                    if (!preferenceMangr.pref_getString("schedule_getsalesclosecount").equals("0") && !preferenceMangr.pref_getString("schedule_getsalesclosecount").equals("null") &&
                                            !preferenceMangr.pref_getString("schedule_getsalesclosecount").equals("") && !preferenceMangr.pref_getString("schedule_getsalesclosecount").equals(null) &&
                                            !preferenceMangr.pref_getString("schedule_getcashclosecount").equals("0") && !preferenceMangr.pref_getString("schedule_getcashclosecount").equals("null") &&
                                            !preferenceMangr.pref_getString("schedule_getcashclosecount").equals("") && !preferenceMangr.pref_getString("schedule_getcashclosecount").equals(null)) {
                            /*bottomlayout.setVisibility(View.GONE);
                            btnSaveSchedule.setVisibility(View.VISIBLE);*/
                                        Intent i = new Intent(MyScheduleActivity.this, ScheduleActivity.class);
                                        startActivity(i);

                                    } else {
                                        salescashclose = new Dialog(context);
                                        salescashclose.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        salescashclose.setContentView(R.layout.salescashclose_popup);
                                        salescashclose.setCanceledOnTouchOutside(false);
                                        popup_salesclose = (TextView) salescashclose.findViewById(R.id.popup_salesclose);
                                        popup_cashclose = (TextView) salescashclose.findViewById(R.id.popup_cashclose);
                                        popup_salesclose_title = (TextView) salescashclose.findViewById(R.id.popup_salesclose_title);

                                        popup_salesclose.setText("Close Sales");
                                        popup_cashclose.setText("Close Cash");
                                        if(!Utilities.isNullOrEmpty(lastScheduleDate))
                                            popup_salesclose_title.setText(getString(R.string.previous_schedule_close_popup_title) + " (" +lastScheduleDate + ")");

                                        popup_salesclose.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                popup_salesclose();
                                            }
                                        });

                                        popup_cashclose.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                LoginActivity.iscash = true;
                                                Intent i = new Intent(MyScheduleActivity.this, CashReportActivity.class);
                                                startActivity(i);
                                                salescashclose.dismiss();

                                            }
                                        });
                                        salescashclose.show();
                                    }
                                } else {
                                    Intent i = new Intent(MyScheduleActivity.this, ScheduleActivity.class);
                                    startActivity(i);
                                }
                                if (!preferenceMangr.pref_getString("schedule_getsalesclosecount").equals("0") && !preferenceMangr.pref_getString("schedule_getsalesclosecount").equals("null") &&
                                        !preferenceMangr.pref_getString("schedule_getsalesclosecount").equals("") && !preferenceMangr.pref_getString("schedule_getsalesclosecount").equals(null)) {
                                    popup_salesclose.setText("Sales Closed");
                                    popup_salesclose.setBackgroundResource(R.drawable.editbackgroundgray);
                                    popup_salesclose.setEnabled(false);
                                }
                                if (!preferenceMangr.pref_getString("schedule_getcashclosecount").equals("0") && !preferenceMangr.pref_getString("schedule_getcashclosecount").equals("null") &&
                                        !preferenceMangr.pref_getString("schedule_getcashclosecount").equals("") && !preferenceMangr.pref_getString("schedule_getcashclosecount").equals(null)) {
                                    popup_cashclose.setText("Cash Closed");
                                    popup_cashclose.setBackgroundResource(R.drawable.editbackgroundgray);
                                    popup_cashclose.setEnabled(false);
                                }

                            }
                        } catch (Exception e) {
                            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                            mDbErrHelper.open();
                            String geterrror = e.toString();
                            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName() + "Sales Cash Popup", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            mDbErrHelper.close();
                        } finally {
                            if (objdatabaseadapter1 != null)
                                objdatabaseadapter1.close();
                        }


                    }
                }catch (Exception e){
                    DataBaseAdapter mDbErrHelper2 = new DataBaseAdapter(context);
                    mDbErrHelper2.open();
                    mDbErrHelper2.insertErrorLog("MySchedule NewSchedule : "+e.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper2.close();
                }
            }
        });

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

    //Start time action
    public  void starttime_action(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to start lunch ?");
                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                DataBaseAdapter objdatabaseadapter = null;
                                try {
                                    //Order item details
                                    objdatabaseadapter = new DataBaseAdapter(context);
                                    objdatabaseadapter.open();
                                    String getresult="";
                                    if(!getschedulecode.equals("")) {
                                        getresult = objdatabaseadapter.InsertLaunchtime(getschedulecode);
                                        if (getresult.equals("success")) {
                                            Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.lunchsavestartmsg), Toast.LENGTH_LONG);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();

                                            Cursor Cur = objdatabaseadapter.GetScheduleListDB(getschdeuleformatdate);
                                            if (Cur.getCount() > 0) {
                                                for (int i = 0; i < Cur.getCount(); i++) {
                                                    lunch_start_time = Cur.getString(11);
                                                    lunch_end_time = Cur.getString(12);
                                                }
                                            }
                                            Cur.close();

                                            btn_lunch_start_time.setBackgroundColor(getResources().getColor(R.color.gray));
                                            btn_lunch_start_time.setTextColor(getResources().getColor(R.color.darkgraycolor));

                                            btn_lunch_end_time.setBackgroundColor(getResources().getColor(R.color.lunchred));
                                            btn_lunch_end_time.setTextColor(getResources().getColor(R.color.white));

                                            btn_lunch_start_time.setEnabled(false);
                                            btn_lunch_end_time.setEnabled(true);

                                            LLlunctime.setVisibility(View.VISIBLE);

                                            String output[] = lunch_start_time.split(" ");

                                            String s = output[1].toString();
                                            DateFormat f1 = new SimpleDateFormat("HH:mm:ss"); //HH for hour of the day (0 - 23)
                                            Date d = f1.parse(s);
                                            DateFormat f2 = new SimpleDateFormat("hh:mm a");

                                            txtschedulelunctime.setText(f2.format(d).toUpperCase());


                                            networkstate = isNetworkAvailable();
                                            if (networkstate == true) {
                                                new AsyncScheduleDetails().execute();
                                            }
                                        }
                                    }else{
                                        Toast toast = Toast.makeText(getApplicationContext(),"Don't have schedule details", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                        return;
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

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    //Checking internet connection
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


    //end time action
    public  void endtime_action(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to end lunch? ");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        DataBaseAdapter objdatabaseadapter = null;
                        try {
                            //Order item details
                            objdatabaseadapter = new DataBaseAdapter(context);
                            objdatabaseadapter.open();
                            String getresult = "";
                            if(!getschedulecode.equals("")) {
                            getresult = objdatabaseadapter.InsertLaunchEndtime(getschedulecode);
                            if (getresult.equals("success")) {
                                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.lunchsaveendmsg), Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();

                                Cursor Cur = objdatabaseadapter.GetScheduleListDB(getschdeuleformatdate);
                                if (Cur.getCount() > 0) {
                                    for (int i = 0; i < Cur.getCount(); i++) {
                                        lunch_start_time = Cur.getString(11);
                                        lunch_end_time = Cur.getString(12);
                                    }
                                }
                                Cur.close();

                                btn_lunch_end_time.setBackgroundColor(getResources().getColor(R.color.gray));
                                btn_lunch_end_time.setTextColor(getResources().getColor(R.color.darkgraycolor));

                                btn_lunch_start_time.setBackgroundColor(getResources().getColor(R.color.gray));
                                btn_lunch_start_time.setTextColor(getResources().getColor(R.color.darkgraycolor));

                                btn_lunch_start_time.setEnabled(false);
                                btn_lunch_end_time.setEnabled(false);

                                LLlunctime.setVisibility(View.VISIBLE);
                                String output[] = lunch_start_time.split(" ");
                                String output1[] = lunch_end_time.split(" ");


                                String s = output[1].toString();
                                DateFormat f1 = new SimpleDateFormat("HH:mm:ss"); //HH for hour of the day (0 - 23)
                                Date d = f1.parse(s);
                                DateFormat f2 = new SimpleDateFormat("hh:mm a");

                                String s1 = output1[1].toString();
                                DateFormat f4 = new SimpleDateFormat("HH:mm:ss"); //HH for hour of the day (0 - 23)
                                Date d1 = f4.parse(s1);
                                DateFormat f3 = new SimpleDateFormat("hh:mm a");

                                txtschedulelunctime.setText(f2.format(d).toUpperCase() + " to " + f3.format(d1).toUpperCase());


                                networkstate = isNetworkAvailable();
                                if (networkstate == true) {
                                    new AsyncScheduleDetails().execute();
                                }
                            }
                        }else{
                                Toast toast = Toast.makeText(getApplicationContext(),"Don't have schedule details", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                return;
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

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case  PERMISSION_STROAGE_CODE:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    new DownloadFileFromURL().execute();
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Permission denied...", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    //Toast.makeText(getApplicationContext(),"Permission denied...",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    protected  class AsyncSyncscheduleDetails extends
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
                //sync vanstock
                String getschedulecode = dataBaseAdapter.GetScheduleCode();
//                jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncvanstock.php",context);
//                if (isSuccessful(jsonObj)) {
//                    dataBaseAdapter.syncvanstocktransaction(jsonObj);
//                    api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "vanstock", preferenceMangr.pref_getString("getvancode"), getschedulecode);
//                }
                //General settings
                jsonObj = api.GetscheduleDetails(preferenceMangr.pref_getString("deviceid"),getschdeuleformatdate, "syncdeliverynotescheduledetails.php",context);
                if (isSuccessful(jsonObj)) {
                    result = "success";
                }else{
                    result = "";
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
            return result;
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
                if(result.equals("success")){
                    syncscheduledetails(jsonObj);
                }


            }catch (Exception e) {
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }

        }
    }
    public void syncscheduledetails(JSONObject object){
        if(object!=null){
            JSONArray json_category=null;
            try{
                json_category = object.optJSONArray("Value");
                if(json_category != null && json_category.length()>0){

                    for(int i=0;i<json_category.length();i++) {
                        JSONObject obj = (JSONObject) json_category.get(i);
                        String schedulecode=getschedulecode;
                        DataBaseAdapter objdatabaseadapter=null;
                        if(schedulecode!="" || schedulecode!=null){
                            try {

                                //Save Schedule Functionality
                                objdatabaseadapter = new DataBaseAdapter(context);
                                objdatabaseadapter.open();
                                objdatabaseadapter.insertScheduleeway(obj.getString("scheduledate"), schedulecode.toString(), obj.getString("ewaybillurl"));
                                objdatabaseadapter.close();
//                                GetScheduleList();
                                GetEway();
                            }catch (Exception e) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Error in  schedule ewaybill url sync",Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
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
    //Get Schedule List
    public  void GetScheduleList(){
        LLscheduledetails.setVisibility(View.VISIBLE);
        LLNoSchedule.setVisibility(View.GONE);
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetScheduleListDB(getschdeuleformatdate);
            if(Cur.getCount()>0) {
                for(int i=0;i<Cur.getCount();i++){
                    getschedulecode = Cur.getString(0);
                   // txtschedulevanname.setText(Cur.getString(2));
                    String getvanname=Cur.getString(2);
                    try{
                        if(getvanname.equals("") || getvanname.equals(null) || getvanname.equals("null")){
                            String vanname=objdatabaseadapter.getVanname(Cur.getString(0));
                            txtschedulevanname.setText(vanname);
                        }else{
                            txtschedulevanname.setText(Cur.getString(2));
                        }
                    }catch (Exception e){
                        Log.i("ScheduleList_Vanname", e.toString());
                    }
                    txtschedulevehiclename.setText(Cur.getString(4));
                    txtscheduleroutename.setText(Cur.getString(3));
                    txtschedulesalesrepname.setText(Cur.getString(5));
                    txtscheduledrivername.setText(Cur.getString(6));
                    String gethelpername = Cur.getString(7);
                    if(gethelpername.equals("") || gethelpername.equals("null") || gethelpername.equals(null)){
                        gethelpername = "NIL";
                    }
                    txtschedulehelpername.setText(gethelpername);
                    String gettripadvance = Cur.getString(8);
                    if(gettripadvance.equals("") || gettripadvance.equals("null") || gettripadvance.equals(null)){
                        gettripadvance = "NIL";
                        txtscheduletripadvance.setText(gettripadvance);
                    }else {
                        txtscheduletripadvance.setText(df.format(Double.parseDouble(gettripadvance)));
                    }
                    if(Cur.getString(4).split(" - ")[1].equals("null")){
                        txtschedulevehiclename.setText(Cur.getString(4).split(" - ")[0]);
                    }
                    String getstartingkm = Cur.getString(9);
                    if(getstartingkm.equals("") || getstartingkm.equals("null") || getstartingkm.equals(null)){
                        getstartingkm = "NIL";
                    }

                    txtschedulestartingkm.setText(getstartingkm+" km");

                    String getendingkm = Cur.getString(10);
                    if(!getendingkm.equals("") && !getendingkm.equals("null") && !getendingkm.equals(null)
                            && !getendingkm.equals("0")){
                        txtscheduleendingkm.setText(getendingkm+" km");
                        LLEndingkm.setVisibility(View.VISIBLE);
                    }else{
                        LLEndingkm.setVisibility(View.GONE);
                    }

                     lunch_start_time = Cur.getString(11);
                     lunch_end_time = Cur.getString(12);


//                     if(preferenceMangr.pref_getString("getbusiness_type").equals("2")){
//                         LLeway.setVisibility(View.GONE);
//                     }else{
//                         LLeway.setVisibility(View.VISIBLE);
//
//                    String ewayurl=Cur.getString(13);
//                   final String[] res = ewayurl.split("[,]", 0);
//                    GLView.removeAllViews();
//                    for(int j=0;j<res.length;j++){
//                        View vChild = LayoutInflater.from(this).inflate(R.layout.pdflayout, null);
//                        GLView.addView(vChild);
//                        String[] filearray=res[j].split("[/]");
//                        String strfilename="";
//                        //filearray[4].contains("Eway_")
//                        if(filearray[5].contains("Eway_")) {
//                            strfilename = filearray[5].replace("Eway_", "");
//                        }else{
//                            strfilename=filearray[5];
//                        }
//                        txtewayurl = (TextView)vChild.findViewById(R.id.txtewayurl);
//                        txtewayurl.setText(strfilename);
//                        imgpdf=(ImageView) vChild.findViewById(R.id.imgpdf);
//
//                        final int k=j;
//                        final String filename=filearray[5];
//                        imgpdf.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                networkstate = isNetworkAvailable();
//                                if (networkstate != true) {
//                                    Toast toast = Toast.makeText(getApplicationContext(),"Please check your internet connection" , Toast.LENGTH_LONG);
//                                    toast.setGravity(Gravity.CENTER, 0, 0);
//                                    toast.show();
//                                    return;
//                                }
//                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(res[k]));
//                                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
//                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); // to notify when download is complete
//                                request.allowScanningByMediaScanner();// if you want to be available from media players
//                                DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//                                manager.enqueue(request);
//
//                                Toast.makeText(context,"Successfully file Downloaded...!",Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                     }

                    if(!lunch_end_time.equals("") && !lunch_end_time.equals(" ")  && !lunch_end_time.equals("null") && !lunch_end_time.equals("1900-01-01 00:00:00")
                            &&!lunch_start_time.equals("") && !lunch_start_time.equals(" ")  && !lunch_start_time.equals("null")  && !lunch_start_time.equals("1900-01-01 00:00:00")){
                        btn_lunch_end_time.setBackgroundColor(getResources().getColor(R.color.gray));
                        btn_lunch_end_time.setTextColor(getResources().getColor(R.color.darkgraycolor));

                        btn_lunch_start_time.setBackgroundColor(getResources().getColor(R.color.gray));
                        btn_lunch_start_time.setTextColor(getResources().getColor(R.color.darkgraycolor));

                        btn_lunch_start_time.setEnabled(false);
                        btn_lunch_end_time.setEnabled(false);

                        LLlunctime.setVisibility(View.VISIBLE);
                        String output[] = lunch_start_time.split(" ");
                        String output1[] = lunch_end_time.split(" ");


                        String s = output[1].toString();
                        DateFormat f1 = new SimpleDateFormat("HH:mm:ss"); //HH for hour of the day (0 - 23)
                        Date d = f1.parse(s);
                        DateFormat f2 = new SimpleDateFormat("hh:mm a");

                        String s1 = output1[1].toString();
                        DateFormat f4 = new SimpleDateFormat("HH:mm:ss"); //HH for hour of the day (0 - 23)
                        Date d1 = f4.parse(s1);
                        DateFormat f3 = new SimpleDateFormat("hh:mm a");

                        txtschedulelunctime.setText( f2.format(d).toUpperCase() + " to "+ f3.format(d1).toUpperCase() );



                    }else if(!lunch_start_time.equals("") && !lunch_start_time.equals(" ")  && !lunch_start_time.equals("null") && !lunch_start_time.equals("1900-01-01 00:00:00") ){
                        btn_lunch_start_time.setBackgroundColor(getResources().getColor(R.color.gray));
                        btn_lunch_start_time.setTextColor(getResources().getColor(R.color.darkgraycolor));

                        btn_lunch_end_time.setBackgroundColor(getResources().getColor(R.color.lunchred));
                        btn_lunch_end_time.setTextColor(getResources().getColor(R.color.white));

                        btn_lunch_start_time.setEnabled(false);
                        btn_lunch_end_time.setEnabled(true);

                        LLlunctime.setVisibility(View.VISIBLE);
                        String output[] = lunch_start_time.split(" ");

                        String s = output[1].toString();
                        DateFormat f1 = new SimpleDateFormat("HH:mm:ss"); //HH for hour of the day (0 - 23)
                        Date d = f1.parse(s);
                        DateFormat f2 = new SimpleDateFormat("hh:mm a");
                        txtschedulelunctime.setText( f2.format(d).toUpperCase());

                    }else{
                        btn_lunch_end_time.setBackgroundColor(getResources().getColor(R.color.gray));
                        btn_lunch_end_time.setTextColor(getResources().getColor(R.color.darkgraycolor));

                        btn_lunch_start_time.setBackgroundColor(getResources().getColor(R.color.lunchgreen));
                        btn_lunch_start_time.setTextColor(getResources().getColor(R.color.white));

                        btn_lunch_start_time.setEnabled(true);
                        btn_lunch_end_time.setEnabled(false);
                        LLlunctime.setVisibility(View.GONE);
                        txtschedulelunctime.setText("");
                    }


                }

            }else{
                String nillvalue = "NIL";
                txtschedulevanname.setText(nillvalue);
                txtschedulevehiclename.setText(nillvalue);
                txtscheduleroutename.setText(nillvalue);
                txtschedulesalesrepname.setText(nillvalue);
                txtscheduledrivername.setText(nillvalue);
                txtschedulehelpername.setText(nillvalue);
                txtscheduletripadvance.setText(nillvalue);
                txtschedulestartingkm.setText(nillvalue);
                LLEndingkm.setVisibility(View.GONE);
                Toast toast = Toast.makeText(getApplicationContext(),"No Schedule Available", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                LLscheduledetails.setVisibility(View.GONE);
                LLNoSchedule.setVisibility(View.VISIBLE);
               // Toast.makeText(getApplicationContext(),"No Schedule Available",Toast.LENGTH_SHORT).show();

            }
        }  catch (Exception e){
            Log.i("ScheduleList", e.toString());
        }
        finally {
            // this gets called even if there is an exception somewhere above
            if(objdatabaseadapter != null)
                objdatabaseadapter.close();
            if(Cur != null)
                Cur.close();
        }
    }
   public void GetEway(){
       DataBaseAdapter objdatabaseadapter = null;
       Cursor Cur=null;
       try {
           objdatabaseadapter = new DataBaseAdapter(context);
           objdatabaseadapter.open();
           Cur = objdatabaseadapter.GetScheduleListDB(getschdeuleformatdate);
           if(Cur.getCount()>0) {
               for(int i=0;i<Cur.getCount();i++) {
                   if(preferenceMangr.pref_getString("getbusiness_type").equals("2")){
                         LLeway.setVisibility(View.GONE);
                     }else{
                         LLeway.setVisibility(View.VISIBLE);
                         txt_eway.setVisibility(View.VISIBLE);
                    String ewayurl=Cur.getString(13);
                   final String[] res = ewayurl.split("[,]", 0);
                    GLView.removeAllViews();
                    for(int j=0;j<res.length;j++){
                        View vChild = LayoutInflater.from(this).inflate(R.layout.pdflayout, null);
                        GLView.addView(vChild);
                        String[] filearray=res[j].split("[/]");
                        String strfilename="";
                        //filearray[4].contains("Eway_")
                        if(filearray[5].contains("Eway_")) {
                            strfilename = filearray[5].replace("Eway_", "");
                        }else{
                            strfilename=filearray[5];
                        }
                        txtewayurl = (TextView)vChild.findViewById(R.id.txtewayurl);
                        txtewayurl.setText(strfilename);
                        imgpdf=(ImageView) vChild.findViewById(R.id.imgpdf);

                        final int k=j;
                        final String filename=filearray[5];
                        imgpdf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                networkstate = isNetworkAvailable();
                                if (networkstate != true) {
                                    Toast toast = Toast.makeText(getApplicationContext(),"Please check your internet connection" , Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    return;
                                }
                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(res[k]));
                                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); // to notify when download is complete
                                request.allowScanningByMediaScanner();// if you want to be available from media players
                                DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                manager.enqueue(request);

                                Toast.makeText(context,"Successfully file Downloaded...!",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                     }
               }
               }
       } catch (Exception e){
           Log.i("ScheduleList", e.toString());
       }
       finally {
           // this gets called even if there is an exception somewhere above
           if(objdatabaseadapter != null)
               objdatabaseadapter.close();
           if(Cur != null)
               Cur.close();
       }
   }
    public void goBack(View v) {
        LoginActivity.ismenuopen=true;
        Intent i = new Intent(context, MenuActivity.class);
       /* i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);*/
        startActivity(i);
    }



    /**
     * Showing Dialog
     * */

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                String url = "http://122.165.65.120:8090/linesales/tn67AA6699.pdf";
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE);
                request.setTitle("Download");
                request.setDescription("Downloading file...");
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,""+System.currentTimeMillis());

                DownloadManager manager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);


            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            dismissDialog(progress_bar_type);
            Toast toast = Toast.makeText(getApplicationContext(),"Download Completed ...", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
           // Toast.makeText(getApplicationContext(),"Download Completed ...",Toast.LENGTH_SHORT).show();
        }

    }

    /**********Asynchronous Claass***************/

    protected  class AsyncScheduleDetails extends
            AsyncTask<String, JSONObject, ArrayList<ScheduleDatas>> {
        ArrayList<ScheduleDatas> List = null;
        JSONObject jsonObj = null;
        @Override
        protected  ArrayList<ScheduleDatas> doInBackground(String... params) {
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
            return List;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<ScheduleDatas> result) {
            try{
                // TODO Auto-generated method stub
                if (result.size() >= 1) {
                    if(result.get(0).ScheduleCode.length>0){
                        for(int j=0;j<result.get(0).ScheduleCode.length;j++){
                            DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
                            dataBaseAdapter.open();
                            dataBaseAdapter.UpdateScheduleFlag(result.get(0).ScheduleCode[j]);
                            dataBaseAdapter.close();
                        }
                    }

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

        }
    }

    @Override
    public void onBackPressed() {
        goBack(null);
    }

    public void popup_salesclose(){
        String popupmessage="Do you want to close sales?";
        DataBaseAdapter objdatabaseadapter = null;
        objdatabaseadapter = new DataBaseAdapter(context);

        String deviceid = preferenceMangr.pref_getString("deviceid");
        String result = "";
        try{
            getUPIPaymentPopUp("closesalespopup");
            GetFreeItemexcess();
            networkstate = isNetworkAvailable();
            if (networkstate == true) {
                objdatabaseadapter.open();
                String getschedulecode = objdatabaseadapter.GetScheduleCode();
                result= Utilities.getDeliveryNotePendingCount(deviceid, getschedulecode, context);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Please check internet connection", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            String geteinvoicependingbills = objdatabaseadapter.Checkeinvoicepending();
            if(geteinvoicependingbills!=null && !geteinvoicependingbills.equals("0")){
                popupmessage="You have "+geteinvoicependingbills+" GSTIN invoice(s) for which e-Invoice is " +
                        "not yet generated. \nAre you sure want to close sales?";
            }

        } catch (Exception e) {
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "),
                    this.getClass().getSimpleName()+" - Check e-invoice pending", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        } finally {
            if (objdatabaseadapter != null)
                objdatabaseadapter.close();
        }
        if (!result.equals("0")) {
            Utilities.CheckDeliveryNoteDialog(context);
        } else {
            if (issalesclosepopup) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirmation");
                // builder.setIcon(R.mipmap.ic_vanluncher);
                builder.setMessage(popupmessage)
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                pindialog = new Dialog(context);
                                pindialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                pindialog.setContentView(R.layout.validatepinumber);
                                btnSubmitpin = (Button) pindialog.findViewById(R.id.btnSubmitpin);
                                pinview = (Pinview) pindialog.findViewById(R.id.pinview);
                                ImageView closepopup = (ImageView) pindialog.findViewById(R.id.closepopup);
                                closepopup.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        pindialog.dismiss();
                                    }
                                });
                                btnSubmitpin.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        DataBaseAdapter objdatabaseadapter = null;
                                        try {

                                            String getEncryptedPIN = null;
                                            try {
                                                getEncryptedPIN = sha256(pinview.getValue());
                                            } catch (NoSuchAlgorithmException e) {
                                                e.printStackTrace();
                                            }
                                            if (getEncryptedPIN.equals(preferenceMangr.pref_getString("getpin"))) {
                                                objdatabaseadapter = new DataBaseAdapter(context);
                                                objdatabaseadapter.open();
                                                String getresult = "0";
                                                String getschedulecode = objdatabaseadapter.GetScheduleCode();
                                                //SAles close details
                                                getresult = objdatabaseadapter.InsertSalesClose(getschedulecode);
                                                if (getresult.equals("success")) {
                                                    pindialog.dismiss();
                                                    Toast toast = Toast.makeText(getApplicationContext(), "Sales closed Successfully", Toast.LENGTH_LONG);
                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                    toast.show();
                                                    closeKeyboard();
                                                    ScheduleActivity.getcashclosecount = objdatabaseadapter.GetCashClose(getschedulecode);
                                                    preferenceMangr.pref_putString("schedule_getcashclosecount", objdatabaseadapter.GetCashClose(getschedulecode));
                                                    //Get sales close Count
                                                    ScheduleActivity.getsalesclosecount = objdatabaseadapter.GetSalesClose(getschedulecode);
                                                    preferenceMangr.pref_putString("schedule_getsalesclosecount", objdatabaseadapter.GetSalesClose(getschedulecode));

                                                    popup_salesclose.setText("Sale Closed");
                                                    popup_salesclose.setBackgroundResource(R.drawable.editbackgroundgray);
                                                    popup_salesclose.setEnabled(false);

                                                    networkstate = isNetworkAvailable();
                                                    if (networkstate == true) {
                                                        new AsyncCloseSalesDetails().execute();
                                                    }

                                                } else {
                                                    Toast toast = Toast.makeText(getApplicationContext(), "Error in saving", Toast.LENGTH_LONG);
                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                    toast.show();
                                                }
                                            } else {
                                                Toast toast = Toast.makeText(getApplicationContext(), "Please enter correct pin", Toast.LENGTH_LONG);
                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                toast.show();
                                                return;
                                            }
                                        } catch (Exception e) {
                                            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                            mDbErrHelper.open();
                                            String geterrror = e.toString();
                                            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                            mDbErrHelper.close();
                                        } finally {
                                            if (objdatabaseadapter != null)
                                                objdatabaseadapter.close();
                                        }
                                    }
                                });
                                pindialog.show();
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
    //Close Keyboard
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected  class AsyncCloseSalesDetails extends
            AsyncTask<String, JSONObject, ArrayList<ScheduleDatas>> {
        ArrayList<ScheduleDatas> List = null;
        JSONObject jsonObj = null;
        @Override
        protected  ArrayList<ScheduleDatas> doInBackground(String... params) {
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
            return List;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<ScheduleDatas> result) {
            // TODO Auto-generated method stub
            if (result.size() >= 1) {
                if(result.get(0).ScheduleCode.length>0){
                    for(int j=0;j<result.get(0).ScheduleCode.length;j++){
                        DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
                        dataBaseAdapter.open();
                        dataBaseAdapter.UpdateSalesCloseFlag(result.get(0).ScheduleCode[j]);
                        dataBaseAdapter.close();
                    }
                }

            }

        }
    }

    // free item apapter
    public class FreeItemBaseAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater layoutInflater;
        FreeItemBaseAdapter(Context c) {
            context = c;
            layoutInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return FreeItemCode.length;
        }

        @Override
        public Object getItem(int position) {
            return FreeItemCode[position];
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

            ViewHolder mHolder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.freeitemexcesslist, parent, false);
                mHolder = new ViewHolder();
                try {
                    mHolder.listsno = (TextView) convertView.findViewById(R.id.listsno);
                    mHolder.listitemname = (TextView) convertView.findViewById(R.id.listitemname);
                    mHolder.listdistributed = (TextView) convertView. findViewById(R.id.listdistributed);
                    mHolder.listbalance = (TextView) convertView. findViewById(R.id.listbalance);
                    mHolder.listhandover = (TextView) convertView. findViewById(R.id.listhandover);
                    mHolder.listopening = (TextView)convertView.findViewById(R.id.listopening);
                    mHolder.freestocklist = (LinearLayout) convertView. findViewById(R.id.freestocklist);
                    mHolder.card_view = (CardView)convertView.findViewById(R.id.card_view);
                } catch (Exception e) {
                    Log.i("Customer", e.toString());
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

                mHolder.listsno.setText(String.valueOf(FreeItemSNO[position]));
                mHolder.listitemname .setText(String.valueOf(FreeItemName[position]));
                mHolder.listdistributed.setText(String.valueOf(FreeItemDistributed[position]));
                mHolder.listbalance.setText(String.valueOf(FreeItemBalance[position]));
                mHolder.listhandover.setText(String.valueOf(FreeItemHandover[position]));
                mHolder.listopening.setText(String.valueOf(FreeItemOp[position]));


            } catch (Exception e) {
                Log.i("Customer", e.toString());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    FreeItemName,FreeItemOp,FreeItemHandover,FreeItemDistributed,FreeItemBalance,FreeItemCode
//                    customercode = FreeItemCode[position];
//                    gstnnumber = GSTN[position];
//                    getschemeapplicable = SchemeApplicable[position];
//                    customercityname = CustomerCityName[position];
//                    customerareaname = CustomerAreaName[position];
//                    customername = FreeItemName[position];
//                    customercityarea= CustomerCityName[position] + ',' + CustomerAreaName[position];
                    freeitemdialog.dismiss();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView listsno, listitemname,listdistributed,
                    listbalance,listhandover,listopening;
            private LinearLayout freestocklist;
            private CardView card_view;
        }
//        @Override
//        public int getCount() {
//            return SubGroupCode.length;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return SubGroupCode[position];
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//        @Override
//        public int getViewTypeCount() {
//            return getCount();
//        }
//        @Override
//        public int getItemViewType(int position) {
//            return position;
//        }
//        @SuppressLint("InflateParams")
//        @Override
//        public View getView(final int position, View convertView, ViewGroup parent) {
//
//            ViewHolder mHolder;
//
//            if (convertView == null) {
//                convertView = layoutInflater.inflate(R.layout.freeitemexcesslist, parent, false);
//                mHolder = new ViewHolder();;
//                try {
////                    mHolder.listsubgroup = (TextView) convertView.findViewById(R.id.listsubgroup);
//                    mHolder.listsno = (TextView) convertView.findViewById(R.id.listsno);
//                    mHolder.listitemname = (TextView) convertView.findViewById(R.id.listitemname);
//                    mHolder.listinward = (TextView) convertView. findViewById(R.id.listinward);
//                    mHolder.listoutward = (TextView) convertView. findViewById(R.id.listoutward);
//                    mHolder.listclosing = (TextView) convertView. findViewById(R.id.listclosing);
//                    mHolder.listopening = (TextView)convertView.findViewById(R.id.listopening);
//                    mHolder.vanstocklist = (LinearLayout) convertView. findViewById(R.id.vanstocklist);
//                    mHolder.listsales = (TextView)convertView.findViewById(R.id.listsales);
//                    mHolder.card_view = (CardView)convertView.findViewById(R.id.card_view);
//                } catch (Exception e) {
//                    Log.i("Customer", e.toString());
//                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
//                    mDbErrHelper.open();
//                    String geterrror = e.toString();
//                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
//                    mDbErrHelper.close();
//                }
//                convertView.setTag(mHolder);
//            } else {
//                mHolder = (ViewHolder) convertView.getTag();
//            }
//            try {
//                mHolder.listsno.setText(String.valueOf(SubGroupNameTamil[position]));
//                mHolder.listitemname .setText(String.valueOf(SubGroupNameTamil[position]));
//                mHolder.listinward.setText(String.valueOf(SubGroupNameTamil[position]));
//                mHolder.listoutward.setText(String.valueOf(SubGroupNameTamil[position]));
//                mHolder.listclosing.setText(String.valueOf(SubGroupNameTamil[position]));
//                mHolder.listopening.setText(String.valueOf(SubGroupNameTamil[position]));
//                mHolder.vanstocklist.setText(String.valueOf(SubGroupNameTamil[position]));
//                mHolder.listsales.setText(String.valueOf(SubGroupNameTamil[position]));
//                mHolder.card_view.setText(String.valueOf(SubGroupNameTamil[position]));
//
//                                VanStockDetails currentListData = getItem(position);
//                mHolder.listsno.setText(currentListData.getSno());
//                mHolder.listitemname.setText(currentListData.getItemName()+" - "+currentListData.getUOM());
//
//                DecimalFormat df;
//                String getdecimalvalue  = currentListData.getNoofdecimals();
//                String getnoofdigits = "0";
//                if(getdecimalvalue.equals("0")){
//                    getnoofdigits = "";
//                }
//                if(getdecimalvalue.equals("1")){
//                    getnoofdigits = "0";
//                }
//                if(getdecimalvalue.equals("2")){
//                    getnoofdigits = "00";
//                }
//                if(getdecimalvalue.equals("3")){
//                    getnoofdigits = "000";
//                }
//
//                df = new DecimalFormat("0.'"+getnoofdigits+"'");
//                if(getnoofdigits.equals("")) {
//                    mHolder.listinward.setText(currentListData.getInward());
//                }else{
//                    mHolder.listinward.setText(df.format(Double.parseDouble(currentListData.getInward())));
//                }
//                if(getnoofdigits.equals("")) {
//                    mHolder.listoutward.setText(currentListData.getOutward());
//                }else{
//                    mHolder.listoutward.setText(df.format(Double.parseDouble(currentListData.getOutward())));
//                }
//                if(getnoofdigits.equals("")) {
//                    mHolder.listclosing.setText(currentListData.getClosing());
//                }else{
//                    mHolder.listclosing.setText(df.format(Double.parseDouble(currentListData.getClosing())));
//                }
//                if(getnoofdigits.equals("")) {
//                    mHolder.listopening.setText(currentListData.getOpening());
//                }else{
//                    mHolder.listopening.setText(df.format(Double.parseDouble(currentListData.getOpening())));
//                }
//
//                if(getnoofdigits.equals("")) {
//                    mHolder.listsales.setText("\n"+currentListData.getSales());
//                }else{
//                    mHolder.listsales.setText("\n"+df.format(Double.parseDouble(currentListData.getSales())));
//                }
//
//
//                mHolder.listitemname.setTextColor(Color.parseColor(currentListData.getColourcode()));
//
//
//                if (position % 2 == 1) {
//                    mHolder.card_view.setCardBackgroundColor(Color.parseColor("#ffffff"));
//                } else {
//                    mHolder.card_view.setCardBackgroundColor(Color.parseColor("#caeaf3"));
//                }
//
//            } catch (Exception e) {
//                Log.i("Customer", e.toString());
//                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
//                mDbErrHelper.open();
//                String geterrror = e.toString();
//                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
//                mDbErrHelper.close();
//            }
//            convertView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    getstaticsubcode = SubGroupCode[position];
//                    getitemsfromcode = SubGroupCode[position];
//                    GetItems(SubGroupCode[position]);
//                    window.dismiss();
//                    fabgroupitem.startAnimation(rotate_backward);
//                    isFabOpen = false;
//                    isopenpopup = false;
//                }
//            });
//            return convertView;
//        }
//
//        private class ViewHolder {
//            private TextView listsno, listitemname,listinward,
//                    listoutward,listclosing,listopening,listsales;
//            private LinearLayout vanstocklist;
//            private CardView card_view;
//
//        }
//        public VanStockBaseAdapter( Context context,ArrayList<VanStockDetails> myList) {
//            this.myList = myList;
//            this.context = context;
//            inflater = LayoutInflater.from(context);
//        }
//
//
//        public View getView(final int position, View convertView, ViewGroup parent) {
//
//            thanjavurvansales.sss.VanStockBaseAdapter.ViewHolder mHolder;
//            if (convertView == null) {
//                convertView = inflater.inflate(R.layout.vansaleslist, parent, false);
//                mHolder = new thanjavurvansales.sss.VanStockBaseAdapter.ViewHolder();
//                try {
//                    mHolder.listsno = (TextView) convertView.findViewById(R.id.listsno);
//                    mHolder.listitemname = (TextView) convertView.findViewById(R.id.listitemname);
//                    mHolder.listinward = (TextView) convertView. findViewById(R.id.listinward);
//                    mHolder.listoutward = (TextView) convertView. findViewById(R.id.listoutward);
//                    mHolder.listclosing = (TextView) convertView. findViewById(R.id.listclosing);
//                    mHolder.listopening = (TextView)convertView.findViewById(R.id.listopening);
//                    mHolder.vanstocklist = (LinearLayout) convertView. findViewById(R.id.vanstocklist);
//                    mHolder.listsales = (TextView)convertView.findViewById(R.id.listsales);
//                    mHolder.card_view = (CardView)convertView.findViewById(R.id.card_view);
//
//                } catch (Exception e) {
//                    Log.i("Van stock", e.toString());
//                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
//                    mDbErrHelper.open();
//                    mDbErrHelper.insertErrorLog(e.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
//                    mDbErrHelper.close();
//                }
//                convertView.setTag(mHolder);
//            } else {
//                mHolder = (thanjavurvansales.sss.VanStockBaseAdapter.ViewHolder) convertView.getTag();
//            }
//            try {
//                VanStockDetails currentListData = getItem(position);
//                mHolder.listsno.setText(currentListData.getSno());
//                mHolder.listitemname.setText(currentListData.getItemName()+" - "+currentListData.getUOM());
//
//                DecimalFormat df;
//                String getdecimalvalue  = currentListData.getNoofdecimals();
//                String getnoofdigits = "0";
//                if(getdecimalvalue.equals("0")){
//                    getnoofdigits = "";
//                }
//                if(getdecimalvalue.equals("1")){
//                    getnoofdigits = "0";
//                }
//                if(getdecimalvalue.equals("2")){
//                    getnoofdigits = "00";
//                }
//                if(getdecimalvalue.equals("3")){
//                    getnoofdigits = "000";
//                }
//
//                df = new DecimalFormat("0.'"+getnoofdigits+"'");
//                if(getnoofdigits.equals("")) {
//                    mHolder.listinward.setText(currentListData.getInward());
//                }else{
//                    mHolder.listinward.setText(df.format(Double.parseDouble(currentListData.getInward())));
//                }
//                if(getnoofdigits.equals("")) {
//                    mHolder.listoutward.setText(currentListData.getOutward());
//                }else{
//                    mHolder.listoutward.setText(df.format(Double.parseDouble(currentListData.getOutward())));
//                }
//                if(getnoofdigits.equals("")) {
//                    mHolder.listclosing.setText(currentListData.getClosing());
//                }else{
//                    mHolder.listclosing.setText(df.format(Double.parseDouble(currentListData.getClosing())));
//                }
//                if(getnoofdigits.equals("")) {
//                    mHolder.listopening.setText(currentListData.getOpening());
//                }else{
//                    mHolder.listopening.setText(df.format(Double.parseDouble(currentListData.getOpening())));
//                }
//
//                if(getnoofdigits.equals("")) {
//                    mHolder.listsales.setText("\n"+currentListData.getSales());
//                }else{
//                    mHolder.listsales.setText("\n"+df.format(Double.parseDouble(currentListData.getSales())));
//                }
//
//
//                mHolder.listitemname.setTextColor(Color.parseColor(currentListData.getColourcode()));
//
//
//                if (position % 2 == 1) {
//                    mHolder.card_view.setCardBackgroundColor(Color.parseColor("#ffffff"));
//                } else {
//                    mHolder.card_view.setCardBackgroundColor(Color.parseColor("#caeaf3"));
//                }
//            } catch (Exception e) {
//                Log.i("vanstock value", e.toString());
//                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
//                mDbErrHelper.open();
//                String geterrror = e.toString();
//                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
//                mDbErrHelper.close();
//            }
//            return convertView;
//        }
//        private class ViewHolder {
//            TextView listsno, listitemname,listinward,
//                    listoutward,listclosing,listopening,listsales;
//            LinearLayout vanstocklist;
//            CardView card_view;
//
//        }
    }
    public  void GetFreeItemexcess(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.getexcessfreeitemqty();
            if(Cur.getCount()>0) {
                FreeItemName = new String[Cur.getCount()];
                FreeItemOp = new String[Cur.getCount()];
                FreeItemHandover = new String[Cur.getCount()];
                FreeItemDistributed = new String[Cur.getCount()];
                FreeItemBalance = new String[Cur.getCount()];
                FreeItemCode = new String[Cur.getCount()];
                FreeItemSNO = new String[Cur.getCount()];
                int j=0;
                for(int i=0;i<Cur.getCount();i++){
                    j++;
                    FreeItemSNO[i] = String.valueOf(j);
                    FreeItemName[i] = Cur.getString(0);
                    FreeItemOp[i] = Cur.getString(3);
                    FreeItemHandover[i] = Cur.getString(1);
                    FreeItemDistributed[i] = Cur.getString(5);
                    FreeItemBalance[i] = Cur.getString(4);
                    FreeItemCode[i] = Cur.getString(2);
                    Cur.moveToNext();
                }

                freeitemdialog = new Dialog(context);
                freeitemdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                freeitemdialog.setContentView(R.layout.freeitemexcesspopup);
                freeitemdialog.setCanceledOnTouchOutside(false);
                lv_freeitemlist = (ListView) freeitemdialog.findViewById(R.id.lv_freeitemlist);
                ImageView close = (ImageView) freeitemdialog.findViewById(R.id.closepopup);
                Button txtYesAction = (Button) freeitemdialog.findViewById(R.id.btnOkAction);
                txtYesAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        freeitemdialog.dismiss();
                        return;


                    }
                });
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        freeitemdialog.dismiss();
                    }
                });
                FreeItemBaseAdapter adapter = new FreeItemBaseAdapter(context);
                lv_freeitemlist.setAdapter(adapter);
                freeitemdialog.show();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(),"No Customer in this area", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //Toast.makeText(getApplicationContext(),"No Customer in this area",Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
        finally {
            // this gets called even if there is an exception somewhere above
            if(objdatabaseadapter != null)
                objdatabaseadapter.close();
            if(Cur != null)
                Cur.close();
        }
    }

    public  void GetPaymentVenderName(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetPaymentVenderNAmeDB();
            if(Cur.getCount()>0) {
                venderid = new String[Cur.getCount()];
                vendername = new String[Cur.getCount()];
                for(int i=0;i<Cur.getCount();i++){
                    venderid[i] = Cur.getString(0);
                    vendername[i] = Cur.getString(1);
                    Cur.moveToNext();
                }

                upipaymentvenderdialog = new Dialog(context);
                upipaymentvenderdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                upipaymentvenderdialog.setContentView(R.layout.paymenttypepopup);
                lv_UPIPaymentList = (ListView) upipaymentvenderdialog.findViewById(R.id.lv_UPIPaymentList);
                ImageView close = (ImageView) upipaymentvenderdialog.findViewById(R.id.close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        upipaymentvenderdialog.dismiss();
                    }
                });
                UPIPaymentAdapter adapter = new UPIPaymentAdapter(context,venderid,vendername,
                        txtupivendername,getpaymentvenderID,upipaymentvenderdialog,MyScheduleActivity.this);
                lv_UPIPaymentList.setAdapter(adapter);
                upipaymentvenderdialog.show();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(),"No payment vender", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }  catch (Exception e){
            Log.i("GetArea", e.toString());
        }
        finally {
            // this gets called even if there is an exception somewhere above
            if(objdatabaseadapter != null)
                objdatabaseadapter.close();
            if(Cur != null)
                Cur.close();
        }
    }

    public void getUPIPaymentPopUp(String from) {
        DataBaseAdapter objdatabaseadapter = null;
        issalesclosepopup = false;
        try {
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cursor Cur1 = objdatabaseadapter.CheckUPIPayment();
            if(Cur1.getCount()>0) {
                issalesclosepopup = false;
                String getsalestransactionno = Cur1.getString(0);
                String  getfinanicialyear = Cur1.getString(1);
                String getbookingno = Cur1.getString(2);
                String getbillno = Cur1.getString(3);
                String getcompanycode = Cur1.getString(6);
                String getimageurl = Cur1.getString(7);
                final String getbillcopystatus = Cur1.getString(8);
                final String getfinalfrom = from;
                int getupipaidcount = Cur1.getInt(9);
                dialogstatus = new Dialog(context);
                dialogstatus.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogstatus.setContentView(R.layout.salesupipopup);
                dialogstatus.setCanceledOnTouchOutside(false);
                final TextView paymentbookingno = (TextView)dialogstatus.findViewById(R.id.paymentbookingno);
                final TextView paymentbillno = (TextView)dialogstatus.findViewById(R.id.paymentbillno);
                Button btnupisubmit = (Button) dialogstatus.findViewById(R.id.btnupisubmit);
                ImageView closepopup = (ImageView) dialogstatus.findViewById(R.id.closepopup);
                LinearLayout LLbillcopy = (LinearLayout) dialogstatus.findViewById(R.id.LLbillcopy);
                LinearLayout cashpaid = (LinearLayout) dialogstatus.findViewById(R.id.mainBill);
                LinearLayout payoutStatus = (LinearLayout) dialogstatus.findViewById(R.id.payoutStatus);
                ImageView imageView=(ImageView)dialogstatus.findViewById(R.id.imageView);

                final RadioButton radio_paid = (RadioButton) dialogstatus.findViewById(R.id.radio_paid);
                RadioButton radio_notpaid = (RadioButton) dialogstatus.findViewById(R.id.radio_notpaid);
                final RadioButton radio_upi = (RadioButton) dialogstatus.findViewById(R.id.radio_upi);

                txtupivendername=(TextView)dialogstatus.findViewById(R.id.txtupivendername);
                final EditText txtupitransactionID=(EditText) dialogstatus.findViewById(R.id.txtupitransactionID);
                final EditText txtbillamount=(EditText) dialogstatus.findViewById(R.id.txtbillamount);
                final EditText txtupiamount=(EditText)dialogstatus.findViewById(R.id.txtupiamount);
                final EditText txtcashamount=(EditText)dialogstatus.findViewById(R.id.txtcashamount);

                txtbillamount.setText(Cur1.getString(5));
                txtbillamount.setEnabled(false);
                txtupiamount.setText(Cur1.getString(5));
                payoutStatus.setVisibility(View.VISIBLE);
                radio_upi.setChecked(true);
                radio_notpaid.setChecked(false);
                radio_paid.setChecked(false);
                if(Cur1!=null)
                    Cur1.close();

                txtcashamount.setText("0");
                txtcashamount.setEnabled(false);
                paymentbookingno.setText("BK.NO. "+getbookingno);
                paymentbillno.setText("Bill No. "+getbillno);


                Picasso.with(context)
                        .load(getimageurl)
                        .into(imageView);

                txtupivendername.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GetPaymentVenderName();
                    }
                });
                txtupiamount.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                  int after) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        // TODO Auto-generated method stub
                        if (!txtupiamount.getText().toString().equals("")) {

                            if (Integer.parseInt(txtupiamount.getText().toString()) > Integer.parseInt(txtbillamount.getText().toString())) {
                                Toast toast = Toast.makeText(context,"Please enter valid upi amount", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                txtupiamount.setText(txtbillamount.getText().toString());
                            } else {
                                Integer billAmount = Integer.parseInt(txtbillamount.getText().toString());
                                Integer upiAmount = Integer.parseInt(txtupiamount.getText().toString());
                                Integer cashAmount = billAmount - upiAmount;
                                txtcashamount.setText(String.valueOf(cashAmount));
                            }
                        }


                    }
                });


                closepopup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogstatus.dismiss();
                    }
                });


                closepopup.setVisibility(View.GONE);
                final DataBaseAdapter finalObjdatabaseadapter = new DataBaseAdapter(context);
                final String finalGetsalestransactionno = getsalestransactionno;
                final String finalGetfinanicialyear = getfinanicialyear;
                final String finalGetbookingno = getbookingno;
                final String finalGetCompanycode = getcompanycode;
                final int finalGetUpiPaidCount = getupipaidcount;

                btnupisubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            if(radio_upi.isChecked()) {
                                if(getpaymentvenderID.equals("") || getpaymentvenderID.equals(null)
                                        || txtupivendername.getText().toString().equals("") ||  txtupivendername.getText().toString().equals(null)){
                                    Toast toast = Toast.makeText(getApplicationContext(),"Please select payment vender name", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    //Toast.makeText(getApplicationContext(),"Please select company name",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (txtupitransactionID.getText().toString().equals("") || txtupitransactionID.getText().toString().equals(null)) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter Transaction ID", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    //Toast.makeText(getApplicationContext(),"Please enter valid amount",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (txtupitransactionID.getText().toString().equals("") || txtupitransactionID.getText().toString().equals(null)) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter UPI amount", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    //Toast.makeText(getApplicationContext(),"Please enter valid amount",Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if (Integer.parseInt(txtupiamount.getText().toString()) > Integer.parseInt(txtbillamount.getText().toString())) {
                                    Toast toast = Toast.makeText(context,"Please enter valid upi amount", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    return;
                                }

                                finalObjdatabaseadapter.open();
                                String getresult = finalObjdatabaseadapter.insertSalesReceipt(finalGetCompanycode,
                                        preferenceMangr.pref_getString("getvancode"),"UPI",txtupiamount.getText().toString(),
                                        finalGetfinanicialyear, getpaymentvenderID,txtupitransactionID.getText().toString(),finalGetsalestransactionno);

                                if (!txtcashamount.getText().toString().equals("0")) {
                                    getresult = finalObjdatabaseadapter.insertSalesReceipt(finalGetCompanycode,
                                            preferenceMangr.pref_getString("getvancode"),"Cash",txtcashamount.getText().toString(),
                                            finalGetfinanicialyear, "0","",finalGetsalestransactionno);
                                }
                                if (getresult.equals("success")) {
                                    dialogstatus.dismiss();
                                    Toast toast = Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.BOTTOM, 0, 150);
                                    toast.show();
//                                    if (getpaymentstatus.equals("upi")) {
                                    getUPIPaymentPopUp(getfinalfrom);
//                                    }
                                    networkstate = isNetworkAvailable();
                                    if (networkstate == true) {
                                        new AsyncUpdateSalesReceiptDetails().execute();
                                        new AsyncReceiptDetails().execute();

                                    }
                                }
                            }else{
                                String getpaymentstatus = "";
                                if (radio_paid.isChecked()) {
                                    getpaymentstatus = "yes";
                                } else if (radio_upi.isChecked()) {
                                    getpaymentstatus = "upi";
                                }else {
                                    getpaymentstatus = "no";
                                }
                                try {
                                    finalObjdatabaseadapter.open();
//                                    String getresult = finalObjdatabaseadapter.UpdateSalesListReceipt(finalGetsalestransactionno,
//                                            finalGetfinanicialyear, getbillcopystatus, getpaymentstatus);
                                    String getresult = "";
                                    if (finalGetUpiPaidCount == 0) {
                                        getresult = finalObjdatabaseadapter.UpdateSalesListReceipt(finalGetsalestransactionno,
                                                finalGetfinanicialyear, getbillcopystatus, getpaymentstatus);
                                    } else {
                                        getresult = finalObjdatabaseadapter.UpdateSalesListCompanyReceipt(finalGetsalestransactionno,
                                                finalGetfinanicialyear, getbillcopystatus, getpaymentstatus, finalGetCompanycode);
                                    }

                                    if (getresult.equals("success")) {
                                        dialogstatus.dismiss();
                                        Toast toast = Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.BOTTOM, 0, 150);
                                        toast.show();
                                        if (getfinalfrom.equals("closesales") || getfinalfrom.equals("closesalespopup")) {
                                            getUPIPaymentPopUp(getfinalfrom);
                                        }
                                        networkstate = isNetworkAvailable();
                                        if (networkstate == true) {
                                            new AsyncUpdateSalesReceiptDetails().execute();
                                        }
                                    }
                                } catch (Exception e) {
                                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                    mDbErrHelper.open();
                                    String geterrror = e.toString();
                                    mDbErrHelper.insertErrorLog(geterrror.replace("'", " "),
                                            this.getClass().getSimpleName()+" - Update SalesList Receipt", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                    mDbErrHelper.close();
                                } finally {
                                    if (finalObjdatabaseadapter != null)
                                        finalObjdatabaseadapter.close();
                                }
                            }



                        } catch (Exception e) {
                            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                            mDbErrHelper.open();
                            String geterrror = e.toString();
                            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "),
                                    this.getClass().getSimpleName()+" - Update SalesList Receipt", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            mDbErrHelper.close();
                        } finally {
                            if (finalObjdatabaseadapter != null)
                                finalObjdatabaseadapter.close();
                        }

                    }
                });
                dialogstatus.show();
            }else{
                if (from.equals("addsales")) {
                    Intent i = new Intent(context, SalesActivity.class);
                    startActivity(i);
                } else {
                    issalesclosepopup = true;
                }
            }
        } catch (Exception e) {
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName()+
                    " -getUPIPaymentPopUp", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        } finally {
            if(objdatabaseadapter!=null)
                objdatabaseadapter.close();

        }


    }

    protected  class AsyncUpdateSalesReceiptDetails extends
            AsyncTask<String, JSONObject, ArrayList<SalesSyncDatas>> {
        ArrayList<SalesSyncDatas> List = null;
        JSONObject jsonObj = null;
        @Override
        protected  ArrayList<SalesSyncDatas> doInBackground(String... params) {
            RestAPI api = new RestAPI();
            try {
                JSONObject js_salesobj = new JSONObject();
                DataBaseAdapter dbadapter = null;
                Cursor mCursales = null;
                try {
                    int count=0;
                    dbadapter = new DataBaseAdapter(context);
                    dbadapter.open();
                    mCursales = dbadapter.GetSalesReceiptDatasDB();
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

                        js_array2.put(obj);
                        mCursales.moveToNext();
                    }
                    if (mCursales != null && !mCursales.isClosed()){
                        count = mCursales.getCount();
                        mCursales.close();

                    }

                    js_salesobj.put("JSonObject", js_array2);

                    jsonObj =  api.SalesReceiptDetails(js_salesobj.toString(),context);
                    //Call Json parser functionality
                    JSONParser parser = new JSONParser();
                    //parse the json object to boolean
                    List = parser.parseSalesReceiptDataList(jsonObj);
//                    dbadapter.close();
                }
                catch (Exception e)
                {
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "),
                            this.getClass().getSimpleName()+" - AsyncUpdateSalesReceiptDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                }finally {
                    if(dbadapter!=null)
                        dbadapter.close();
                    if(mCursales!=null)
                        mCursales.close();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncSalesDetails", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "),
                        this.getClass().getSimpleName()+" - SalesReceiptDetails JSON Declare", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            return List;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<SalesSyncDatas> List) {
            // TODO Auto-generated method stub
            DataBaseAdapter objdatabaseadapter = new DataBaseAdapter(context);
            try {
                objdatabaseadapter.open();
                if (List.size() >= 1) {
                    if (List.get(0).TransactionNo.length > 0) {
                        for (int j = 0; j < List.get(0).TransactionNo.length; j++) {
                            //objdatabaseadapter.UpdateSalesRecieptFlag(List.get(0).TransactionNo[j]);
                        }
                    }
                }
            }catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncSalesDetails", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "),
                        this.getClass().getSimpleName()+" - AsyncUpdateSalesReceiptDetails Post execute", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            } finally {
                if(objdatabaseadapter!=null)
                    objdatabaseadapter.close();
            }

        }
    }

    protected  class AsyncReceiptDetails extends
            AsyncTask<String, JSONObject, ArrayList<ReceiptTransactionDetails>> {
        ArrayList<ReceiptTransactionDetails> List = null;
        JSONObject jsonObj = null;
        @Override
        protected  ArrayList<ReceiptTransactionDetails> doInBackground(String... params) {
            RestAPI api = new RestAPI();
            String result = "";
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
                        obj.put("amount", mCur2.getString(14));
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
                }
                catch (Exception e)
                {
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName()+ "Async Receipt Details", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
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
            return List;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<ReceiptTransactionDetails> result) {
            // TODO Auto-generated method stub
            if (result.size() >= 1) {
                if(result.get(0).TransactionNo.length>0){
                    for(int j=0;j<result.get(0).TransactionNo.length;j++){
                        DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
                        dataBaseAdapter.open();
                        dataBaseAdapter.UpdateReceiptDetailsFlag(result.get(0).TransactionNo[j]);
                        dataBaseAdapter.close();
                    }
                }

            }

        }
    }
}
