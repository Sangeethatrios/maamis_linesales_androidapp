package trios.linesales;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ScheduleActivity extends AppCompatActivity {
    ImageButton syncalldetails;
    public static Context context;
    public static TextView scheduledate, txtdrivername,  txtroute,
    txtvechiclename,
    txtsalesrep;
    public static String getschedulevehiclecode, getscheduledrivercode,getscheduleroutecode,getsalesrepcode,getewayurl;
    TextView txtvanname,txtothersroute,
            txthelpername,txttripaadvance,txtstartingkm,salesclose,cashclose;
    private int year, month, day;
    private Calendar calendar;
    ListView lv_Vehiclelist,lv_Salesreplist,lv_Driverlist,lv_Routelist;
    String[] vehiclecode,vehiclename,modelandyear,capacity,documentupload;
    String[] employeecategorycode,employeecode,employeename,employeenametamil,mobileno,employeedocumentupload;
    String[] drivercategorycode,drivercode,drivername,drivernametamil,drivermobileno,driverdocumentupload;
    String[] routecode,routename,routenametamil,routeday;
    Dialog vehicledialog,salesrepdialog,driverdialog,routedialog;
    String getschedulecode="",getroutecodeval="",getroutename="",gettripadvance="",
            getroutenametamil="",getcapacity="",getwishmsg="",getdenominationcount="0";
    public  boolean networkstate;
    public  static  String getsalesschedulecode="",isFromschedule="";

    Dialog pindialog;
    public static Button btnSaveSchedule;
    Button btnSubmitpin;
    Pinview pinview;
    public static String getcashclosecount="0",getsalesclosecount="0",getschedulecount="0",getreciptcount="0",getexpensecount="0";
    LinearLayout bottomlayout;
    public static final String UPLOAD_URL = RestAPI.urlString+"syncimage.php";
    public String whichroute = "";
    public static PreferenceMangr preferenceMangr=null;
    ListView lv_freeitemlist;
    String[] FreeItemName,FreeItemOp,FreeItemHandover,FreeItemDistributed,FreeItemBalance,FreeItemCode,FreeItemSNO;
    Dialog freeitemdialog;

    /* @BindView(R.id.tt_btn_refresh) Button refreshBtn;
     @BindView(R.id.tt_time_gmt) TextView timeGMT;
     @BindView(R.id.tt_time_pst) TextView timePST;
     @BindView(R.id.tt_time_device) TextView timeDeviceTime;*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        context =this;



       /*   ButterKnife.bind(this);
            refreshBtn.setEnabled(TrueTimeRx.isInitialized());
        */
        syncalldetails = (ImageButton)findViewById(R.id.syncalldetails);
        scheduledate = (TextView)findViewById(R.id.scheduledate);
        txtvanname = (TextView)findViewById(R.id.txtvanname);
        txtroute = (TextView)findViewById(R.id.txtroute);
        txtvechiclename = (TextView)findViewById(R.id.txtvechiclename) ;
        txtsalesrep = (TextView)findViewById(R.id.txtsalesrep) ;
        txtdrivername = (TextView)findViewById(R.id.txtdrivername) ;
        txthelpername = (TextView)findViewById(R.id.txthelpername) ;
        txttripaadvance = (TextView)findViewById(R.id.txttripaadvance) ;
        txtstartingkm = (TextView)findViewById(R.id.txtstartingkm) ;
        btnSaveSchedule = (Button)findViewById(R.id.btnSaveSchedule);
        cashclose = (TextView)findViewById(R.id.cashclose);
        salesclose = (TextView)findViewById(R.id.salesclose);
        bottomlayout = (LinearLayout)findViewById(R.id.bottomlayout);
        txtothersroute = (TextView)findViewById(R.id.txtothersroute);

        try {
            preferenceMangr = new PreferenceMangr(context);
        }catch (Exception e){
            Log.d("Preference Manager : ",e.toString());
        }
        whichroute = "";
        //Set Values for van name
        txtvanname.setText(preferenceMangr.pref_getString("getvanname"));

        //Open Menu
        syncalldetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(ScheduleActivity.this,SyncActivity.class);
                startActivity(i);

                /*networkstate = isNetworkAvailable();
                if (networkstate == true) {
                    new AsyncServer().execute();
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Please check internet connection", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }*/
            }
        });



        //Get Current date
        DataBaseAdapter objdatabaseadapter1 = null;
        try{
            objdatabaseadapter1 = new DataBaseAdapter(context);
            objdatabaseadapter1.open();
            //LoginActivity.getformatdate = objdatabaseadapter1.GenCreatedDate();
            //LoginActivity.getcurrentdatetime = objdatabaseadapter1.GenCurrentCreatedDate();
            preferenceMangr.pref_putString("getformatdate",objdatabaseadapter1.GenCreatedDate());
            preferenceMangr.pref_putString("getcurrentdatetime",objdatabaseadapter1.GenCurrentCreatedDate());
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

        //Set Now Date
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        //Set schedule date
        scheduledate.setText(preferenceMangr.pref_getString("getcurrentdatetime"));
        /*//Call Date Functionality
        showmainDate(year, month+1, day);*/

        /************ Set dropdown values******/

        //Route Click listener

        GetFirstRoute_Append();
        txtroute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whichroute = "";
                GetRoute();
            }
        });

        txtothersroute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whichroute = "Others";
                GetRoute();
            }
        });

        //Vechicle Click listener
        txtvechiclename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetVechicle();
            }
        });

        //Sales Rep Click Listener
        txtsalesrep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetSalesRep();
            }
        });

        //Driver Click Listener
        txtdrivername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetDriver();
            }
        });





        /***** SCHEDULE DETAILES************/
        DataBaseAdapter objdatabaseadapter = null;
        try {
            //Get Schdule
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            getschedulecount = objdatabaseadapter.GetScheduleCount();
            preferenceMangr.pref_putString("schedule_getschedulecount",objdatabaseadapter1.GetScheduleCount());

            String getschedulecode = objdatabaseadapter.GetScheduleCode();
            //Get Cash close Count
            getcashclosecount = objdatabaseadapter.GetCashClose(getschedulecode);
            preferenceMangr.pref_putString("schedule_getcashclosecount",objdatabaseadapter.GetCashClose(getschedulecode));
            //Get sales close Count
            getsalesclosecount = objdatabaseadapter.GetSalesClose(getschedulecode);
            preferenceMangr.pref_putString("schedule_getsalesclosecount",objdatabaseadapter.GetSalesClose(getschedulecode));

            getreciptcount = objdatabaseadapter.GetReceiptCount(getschedulecode);
            preferenceMangr.pref_putString("schedule_getreciptcount",objdatabaseadapter.GetReceiptCount(getschedulecode));

            getexpensecount = objdatabaseadapter.GetExpenseCount(getschedulecode);
            preferenceMangr.pref_putString("schedule_getexpensecount",objdatabaseadapter.GetExpenseCount(getschedulecode));

            if(preferenceMangr.pref_getString("getbusiness_type").equals("2")){
                txtstartingkm.setEnabled(false);
                txtstartingkm.setBackgroundResource(R.drawable.editbackgroundlightgray);
               /* Cursor getsalesrep = objdatabaseadapter.GetSalesRepDB();
                if(getsalesrep.getCount()>0){
                    for(int i=0;i<getsalesrep.getCount();i++){
                        txtsalesrep.setText(getsalesrep.getString(3));
                         getsalesrepcode = getsalesrep.getString(1);
                    }
                    getsalesrep.close();
                }*/
                Cursor getdriver = objdatabaseadapter.GetDriverDB();
                if(getdriver.getCount()>0){
                    for(int i=0;i<getdriver.getCount();i++){
                        txtdrivername.setText(getdriver.getString(3));
                        getscheduledrivercode = getdriver.getString(1);
                    }
                    getdriver.close();
                    txtdrivername.setEnabled(false);
                    txtdrivername.setBackgroundResource(R.drawable.editbackgroundlightgray);
                }

                Cursor getvehicle = objdatabaseadapter.GetVehicleDB();
                if(getvehicle.getCount()>0){
                    for(int i=0;i<getvehicle.getCount();i++){
                        txtvechiclename.setText(getvehicle.getString(1));
                        getschedulevehiclecode = getvehicle.getString(0);
                    }
                    getvehicle.close();
                    txtvechiclename.setEnabled(false);
                    txtvechiclename.setBackgroundResource(R.drawable.editbackgroundlightgray);
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
        }


        /*if(!getschedulecount.equals("0") && !getschedulecount.equals("null") && !getschedulecount.equals(null)) {
            if (!getsalesclosecount.equals("0") && !getsalesclosecount.equals("null") &&
                    !getsalesclosecount.equals("") && !getsalesclosecount.equals(null) &&
                    !getcashclosecount.equals("0") && !getcashclosecount.equals("null") &&
                    !getcashclosecount.equals("") && !getcashclosecount.equals(null)) {
                    bottomlayout.setVisibility(View.GONE);
                    btnSaveSchedule.setVisibility(View.VISIBLE);
            } else {
                bottomlayout.setVisibility(View.VISIBLE);
                btnSaveSchedule.setVisibility(View.GONE);
            }
        }else{
            bottomlayout.setVisibility(View.GONE);
            btnSaveSchedule.setVisibility(View.VISIBLE);
        }

        if(! getsalesclosecount.equals("0") && ! getsalesclosecount.equals("null") &&
                ! getsalesclosecount.equals("") && ! getsalesclosecount.equals(null)){
            salesclose.setText("Sale Closed");
            salesclose.setBackgroundResource(R.drawable.editbackgroundgray);
            salesclose.setEnabled(false);
        }

        if(LoginActivity.getbusiness_type.equals("2")){
            if(!getschedulecount.equals("0") && !getschedulecount.equals("null") && !getschedulecount.equals(null)) {

                if (getreciptcount.equals("0")  && getexpensecount.equals("0")) {
                    bottomlayout.setVisibility(View.GONE);
                    btnSaveSchedule.setVisibility(View.VISIBLE);
                } else {
                    if( !getcashclosecount.equals("0") && !getcashclosecount.equals("null") &&
                            !getcashclosecount.equals("") ) {
                        bottomlayout.setVisibility(View.GONE);
                        btnSaveSchedule.setVisibility(View.VISIBLE);
                    }else{
                        bottomlayout.setVisibility(View.VISIBLE);
                        salesclose.setVisibility(View.GONE);
                        btnSaveSchedule.setVisibility(View.GONE);
                    }
                }
            }else{
                *//*if( !getcashclosecount.equals("0") && !getcashclosecount.equals("null") &&
                        !getcashclosecount.equals("") ) {
                    bottomlayout.setVisibility(View.VISIBLE);
                    btnSaveSchedule.setVisibility(View.GONE);
                    salesclose.setVisibility(View.GONE);
                }else{*//*
                    bottomlayout.setVisibility(View.GONE);
                    btnSaveSchedule.setVisibility(View.VISIBLE);
                //}

            }
        }*/
        if(preferenceMangr.pref_getString("getbusiness_type").equals("2")){
            txtroute.setEnabled(true);
            txtvechiclename.setEnabled(true);
            txtsalesrep.setEnabled(true);
            txtdrivername.setEnabled(true);
            btnSaveSchedule.setEnabled(true);
            txtroute.setBackgroundResource(R.drawable.editbackground);
            txtsalesrep.setBackgroundResource(R.drawable.editbackground);
            txtvechiclename.setBackgroundResource(R.drawable.editbackground);
            txtdrivername.setBackgroundResource(R.drawable.editbackground);

            btnSaveSchedule.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            btnSaveSchedule.setTextColor(getResources().getColor(R.color.white));
        }else {
            networkstate = isNetworkAvailable();
            if (networkstate == true) {
                isFromschedule = "yes";
                new AsyncSyncscheduleDetails().execute();
                btnSaveSchedule.setEnabled(true);
                btnSaveSchedule.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btnSaveSchedule.setTextColor(getResources().getColor(R.color.white));
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Please check internet connection")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();

                btnSaveSchedule.setEnabled(false);
                btnSaveSchedule.setBackgroundColor(getResources().getColor(R.color.gray));
                btnSaveSchedule.setTextColor(getResources().getColor(R.color.black));
            }
        }
        btnSaveSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(preferenceMangr.pref_getString("getvancode").equals("")){
                    Toast toast = Toast.makeText(getApplicationContext(), "Please select van name",Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                if(txtroute.getText().toString().equals("") || getscheduleroutecode.equals("") ||  getscheduleroutecode.equals("0")){
                    Toast toast = Toast.makeText(getApplicationContext(), "Please select route",Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                if(!preferenceMangr.pref_getString("getbusiness_type").equals("2")) {
                    if (txtvechiclename.getText().toString().equals("") || getschedulevehiclecode.equals("")) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please select vehicle", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }

                    if (txtdrivername.getText().toString().equals("") || getscheduledrivercode.equals("")) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please select driver", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }
                    if (txtstartingkm.getText().toString().trim().equals("") || txtstartingkm.getText().toString().trim().equals(".") || txtstartingkm.getText().toString().equals("0")) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please enter valid  starting km", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }
                    if (Double.parseDouble(txtstartingkm.getText().toString())<=0) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please enter valid  starting km", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }

                }
                if (txtsalesrep.getText().toString().equals("") || getsalesrepcode.equals("")) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please select sales rep", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                if (txttripaadvance.getText().toString().equals("")) {
                    txttripaadvance.setText("0");
                }
                /**********Show review dialog**********/
                final Dialog schedulereviewdialog = new Dialog(context);
                schedulereviewdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                schedulereviewdialog.setContentView(R.layout.schedule_review_popup);
                schedulereviewdialog.setCanceledOnTouchOutside(false);
                //Declare variables
                ImageView closepopup = (ImageView)schedulereviewdialog.findViewById(R.id.closepopup);
                //Close a popup
                closepopup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        schedulereviewdialog.dismiss();
                    }
                });

                TextView txtscheduledate = (TextView)schedulereviewdialog.findViewById(R.id.txtscheduledate);
                TextView txtschedulevanname = (TextView)schedulereviewdialog.findViewById(R.id.txtschedulevanname);
                TextView txtschedulevehiclename = (TextView)schedulereviewdialog.findViewById(R.id.txtschedulevehiclename);
                TextView txtscheduleroutename = (TextView)schedulereviewdialog.findViewById(R.id.txtscheduleroutename);
                TextView txtschedulesalesrepname = (TextView)schedulereviewdialog.findViewById(R.id.txtschedulesalesrepname);
                TextView txtscheduledrivername = (TextView)schedulereviewdialog.findViewById(R.id.txtscheduledrivername);
                TextView txtschedulehelpername = (TextView)schedulereviewdialog.findViewById(R.id.txtschedulehelpername);
                TextView txtscheduletripadvance = (TextView)schedulereviewdialog.findViewById(R.id.txtscheduletripadvance);
                TextView txtschedulestartingkm = (TextView)schedulereviewdialog.findViewById(R.id.txtschedulestartingkm);
                final  Button btn_save_schedule = (Button)schedulereviewdialog.findViewById(R.id.btn_save_schedule);
                CheckBox checkbox_confirm = (CheckBox)schedulereviewdialog.findViewById(R.id.checkbox_confirm);

                txtscheduledate.setText(scheduledate.getText().toString());
                if(txtroute.getText().toString().equals("Others")){
                    txtscheduleroutename.setText(txtothersroute.getText().toString());
                }else{
                    txtscheduleroutename.setText(txtroute.getText().toString());
                }

                txtschedulevehiclename.setText(txtvechiclename.getText().toString());
                txtschedulesalesrepname.setText(txtsalesrep.getText().toString());
                txtscheduledrivername.setText(txtdrivername.getText().toString());
                txtschedulevanname.setText(txtvanname.getText().toString());

                DecimalFormat df = new DecimalFormat("0.00");
                if(txthelpername.getText().toString().trim().equals("") || txthelpername.getText().toString().trim().equals("null")){
                    txtschedulehelpername.setText("NIL");
                }else{
                    txtschedulehelpername.setText(txthelpername.getText().toString());
                }
                if(txttripaadvance.getText().toString().trim().equals("") || txttripaadvance.getText().toString().trim().equals("null")){
                    txtscheduletripadvance.setText(" 0.00");
                }else{
                    txtscheduletripadvance.setText(" "+df.format(Double.parseDouble(txttripaadvance.getText().toString())));
                }
                if(txtstartingkm.getText().toString().trim().equals("") || txtstartingkm.getText().toString().trim().equals("null")){
                    txtstartingkm.setText("0");
                    txtschedulestartingkm.setText("0 Km");
                }else{
                    txtschedulestartingkm.setText(txtstartingkm.getText().toString()+" Km");
                }

                checkbox_confirm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                       @Override
                       public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                           if (isChecked){
                               btn_save_schedule.setEnabled(true);
                               btn_save_schedule.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                           }else{
                               btn_save_schedule.setEnabled(false);
                               btn_save_schedule.setBackgroundColor(getResources().getColor(R.color.gray));
                           }
                       }
                   }
                );

                btn_save_schedule.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DataBaseAdapter objdatabaseadapter=null;
                        try {
                            //Save Schedule Functionality
                            objdatabaseadapter = new DataBaseAdapter(context);
                            objdatabaseadapter.open();
                            String getschedulecodevalue = objdatabaseadapter.GetOrderScheduleCode();

                            if(preferenceMangr.pref_getString("getbusiness_type").equals("2")){
                                if(!getschedulecodevalue.equals("")
                                        && !getschedulecodevalue.equals(null) && !getschedulecodevalue.equals("0")){
                                  String  getresult = objdatabaseadapter.InsertOrderSalesClose(getschedulecodevalue);
                                }
                                String getschedulecode = objdatabaseadapter.insertSchedule(scheduledate.getText().toString(), getscheduleroutecode, getschedulevehiclecode,
                                        getsalesrepcode, getscheduledrivercode, txthelpername.getText().toString(),
                                        txttripaadvance.getText().toString(), txtstartingkm.getText().toString(),getewayurl);
                                getsalesschedulecode = getschedulecode;
                                preferenceMangr.pref_putString("getsalesschedulecode",getschedulecode);

                                Toast toast = Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                networkstate = isNetworkAvailable();
                                if (networkstate == true) {
                                    new AsyncScheduleDetails().execute();
                                }
                                isFromschedule="";
                                Intent i = new Intent(context, MenuActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }
                            else if(!getschedulecodevalue.equals("")&& !getschedulecodevalue.equals("null")
                                    && !getschedulecodevalue.equals(null) && !getschedulecodevalue.equals("0")){
                                networkstate = isNetworkAvailable();
                                if (networkstate == true) {
                                   // new AsyncCheckPreviousDaySchedule().execute();
                                    new AsyncSyncCheckVanStockVerification().execute();
                                }else{
                                    Toast toast = Toast.makeText(getApplicationContext(),"Please check internet connection", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    return;
                                }
                            }else {
                                networkstate = isNetworkAvailable();
                                if (networkstate == true) {
                                    new AsyncSyncCheckVanStock().execute();

                                }else{
                                    Toast toast = Toast.makeText(getApplicationContext(),"Please check internet connection", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    return;
                                }


                            }
                        } catch (Exception e) {
                            Toast toast = Toast.makeText(getApplicationContext(), "Error in  schedule saving",Toast.LENGTH_LONG);
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
                });
                //Popup show
                schedulereviewdialog.show();


            }
        });

        /**********END Dropdown values*********/

        //Cash REport Activity
        cashclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.iscash = true;
                Intent i = new Intent(ScheduleActivity.this,CashReportActivity.class);
                startActivity(i);
            }
        });

        salesclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String popupmessage = "Do you want to close sales?";
                DataBaseAdapter objdatabaseadapter = null;
                objdatabaseadapter = new DataBaseAdapter(context);
                objdatabaseadapter.open();
                String deviceid = preferenceMangr.pref_getString("deviceid");
                String result = "";
                try {
                    GetFreeItemexcess();
                    networkstate = isNetworkAvailable();
                    if (networkstate == true) {
                        String getschedulecode = objdatabaseadapter.GetScheduleCode();
                        result =   Utilities.getDeliveryNotePendingCount(deviceid, getschedulecode, context);
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please check internet connection", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }
                    String geteinvoicependingbills = objdatabaseadapter.Checkeinvoicepending();
                    if (geteinvoicependingbills != null && !geteinvoicependingbills.equals("0")) {
                        popupmessage = "You have " + geteinvoicependingbills + " GSTIN invoice(s) for which e-Invoice is " +
                                "not yet generated. \nAre you sure want to close sales?";
                    }

                } catch (Exception e) {
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'", " "),
                            this.getClass().getSimpleName() + " - Check e-invoice pending", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                } finally {
                    if (objdatabaseadapter != null)
                        objdatabaseadapter.close();
                }
                if (!result.equals("0")) {
                    Utilities.CheckDeliveryNoteDialog(context);
                } else {
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
                                                        getcashclosecount = objdatabaseadapter.GetCashClose(getschedulecode);
                                                        preferenceMangr.pref_putString("schedule_getcashclosecount", objdatabaseadapter.GetCashClose(getschedulecode));
                                                        //Get sales close Count
                                                        getsalesclosecount = objdatabaseadapter.GetSalesClose(getschedulecode);
                                                        preferenceMangr.pref_putString("schedule_getsalesclosecount", objdatabaseadapter.GetSalesClose(getschedulecode));

                                                        salesclose.setText("Sale Closed");
                                                        salesclose.setBackgroundResource(R.drawable.editbackgroundgray);
                                                        salesclose.setEnabled(false);

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
        });

    }
    //Sync all  datas
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

                    //General settings
                    jsonObj = api.GetscheduleDetails(preferenceMangr.pref_getString("deviceid"),scheduledate.getText().toString(), "syncdeliverynotescheduledetails.php",context);
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
                    btnSaveSchedule.setEnabled(true);
                    btnSaveSchedule.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    btnSaveSchedule.setTextColor(getResources().getColor(R.color.white));
                } else {
                    Toast toast = Toast.makeText(context, "Schedule data not available for "+txtvanname.getText().toString(),Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    btnSaveSchedule.setEnabled(false);
                    btnSaveSchedule.setBackgroundColor(getResources().getColor(R.color.gray));
                    btnSaveSchedule.setTextColor(getResources().getColor(R.color.black));
                    return;
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
   public void  syncscheduledetails(JSONObject object){
        if(object!=null){
            JSONArray json_category=null;
            try{
                json_category = object.optJSONArray("Value");
                if(json_category != null && json_category.length()>0){

                    for(int i=0;i<json_category.length();i++) {
                        JSONObject obj = (JSONObject) json_category.get(i);
                        txtdrivername.setText(obj.getString("drivername"));
                        getscheduledrivercode = obj.getString("drivercode");
                        txtroute.setText(obj.getString("routename"));
                        getscheduleroutecode=obj.getString("routecode");
                        txtvechiclename.setText(obj.getString("vehiclename"));
                        getschedulevehiclecode = obj.getString("vehiclecode");
                        txtsalesrep.setText(obj.getString("salespersonname"));
                        getsalesrepcode = obj.getString("employeecode");
                        getewayurl=obj.getString("ewaybillurl");
                        btnSaveSchedule.setEnabled(true);
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

    /****** Drop Down Functions**********/
    public  void GetFirstRoute_Append(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetFirstRouteDB();
            if(Cur.getCount()>0) {
                routecode = new String[Cur.getCount()];
                routename = new String[Cur.getCount()];
                routenametamil = new String[Cur.getCount()];
                routeday = new String[Cur.getCount()];
                for(int i=0;i<Cur.getCount();i++){
                    routecode[i] = Cur.getString(0);
                    routename[i] = Cur.getString(1);
                    routenametamil[i] = Cur.getString(2);
                    routeday[i] = Cur.getString(3);
                    Cur.moveToNext();
                }

                txtroute.setText(routenametamil[0]);
                getscheduleroutecode=routecode[0];


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
    //Get Route list
    public  void GetRoute(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            if(whichroute.equals("Others")){
                Cur = objdatabaseadapter.GetOthersRouteDB();
            }else {
                Cur = objdatabaseadapter.GetRouteDB();
            }
            if(Cur.getCount()>0) {
                routecode = new String[Cur.getCount()];
                routename = new String[Cur.getCount()];
                routenametamil = new String[Cur.getCount()];
                routeday = new String[Cur.getCount()];
                for(int i=0;i<Cur.getCount();i++){
                    routecode[i] = Cur.getString(0);
                    routename[i] = Cur.getString(1);
                    routenametamil[i] = Cur.getString(2);
                    routeday[i] = Cur.getString(3);
                    Cur.moveToNext();
                }

                routedialog = new Dialog(context);
                routedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                routedialog.setContentView(R.layout.routepopup);
                lv_Routelist = (ListView) routedialog.findViewById(R.id.lv_Routelist);
                ImageView close = (ImageView) routedialog.findViewById(R.id.close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        routedialog.dismiss();
                    }
                });
                RouteAdapter adapter = new RouteAdapter(context);
                lv_Routelist.setAdapter(adapter);
                routedialog.show();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(), "No Route Available",Toast.LENGTH_LONG);
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
    //Get Vehicle list
    public  void GetVechicle(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetVehicleDB();
            if(Cur.getCount()>0) {
                vehiclecode = new String[Cur.getCount()];
                vehiclename = new String[Cur.getCount()];
                modelandyear = new String[Cur.getCount()];
                capacity = new String[Cur.getCount()];
                documentupload = new String[Cur.getCount()];
                for(int i=0;i<Cur.getCount();i++){
                    vehiclecode[i] = Cur.getString(0);
                    vehiclename[i] = Cur.getString(1);
                    modelandyear[i] = Cur.getString(2);
                    capacity[i] = Cur.getString(4);
                    documentupload[i] = Cur.getString(8);
                    Cur.moveToNext();
                }

                vehicledialog = new Dialog(context);
                vehicledialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                vehicledialog.setContentView(R.layout.vehiclepopup);
                lv_Vehiclelist = (ListView) vehicledialog.findViewById(R.id.lv_Vehiclelist);
                ImageView close = (ImageView) vehicledialog.findViewById(R.id.close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vehicledialog.dismiss();
                    }
                });
                VehicleAdapter adapter = new VehicleAdapter(context);
                lv_Vehiclelist.setAdapter(adapter);
                vehicledialog.show();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(), "No Vehicle Available",Toast.LENGTH_LONG);
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

    //Get Sales Rep
    public  void GetSalesRep(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetSalesRepDB();
            if(Cur.getCount()>0) {
                employeecategorycode = new String[Cur.getCount()];
                employeecode = new String[Cur.getCount()];
                employeename = new String[Cur.getCount()];
                employeenametamil = new String[Cur.getCount()];
                mobileno = new String[Cur.getCount()];
                employeedocumentupload = new String[Cur.getCount()];
                for(int i=0;i<Cur.getCount();i++){
                    employeecategorycode[i] = Cur.getString(0);
                    employeecode[i] = Cur.getString(1);
                    employeename[i] = Cur.getString(2);
                    employeenametamil[i] = Cur.getString(3);
                    mobileno[i] = Cur.getString(4);
                    employeedocumentupload[i] = Cur.getString(5);
                    Cur.moveToNext();
                }

                salesrepdialog = new Dialog(context);
                salesrepdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                salesrepdialog.setContentView(R.layout.salesreppopup);
                lv_Salesreplist = (ListView) salesrepdialog.findViewById(R.id.lv_Salesreplist);
                ImageView close = (ImageView) salesrepdialog.findViewById(R.id.close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        salesrepdialog.dismiss();
                    }
                });
                SalesRepAdapter adapter = new SalesRepAdapter(context);
                lv_Salesreplist.setAdapter(adapter);
                salesrepdialog.show();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(), "No Sales Rep Available",Toast.LENGTH_LONG);
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

    //Get Driver
    public  void GetDriver(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetDriverDB();
            if(Cur.getCount()>0) {
                drivercategorycode = new String[Cur.getCount()];
                drivercode = new String[Cur.getCount()];
                drivername = new String[Cur.getCount()];
                drivernametamil = new String[Cur.getCount()];
                drivermobileno = new String[Cur.getCount()];
                driverdocumentupload = new String[Cur.getCount()];
                for(int i=0;i<Cur.getCount();i++){
                    drivercategorycode[i] = Cur.getString(0);
                    drivercode[i] = Cur.getString(1);
                    drivername[i] = Cur.getString(2);
                    drivernametamil[i] = Cur.getString(3);
                    drivermobileno[i] = Cur.getString(4);
                    driverdocumentupload[i] = Cur.getString(5);
                    Cur.moveToNext();
                }

                driverdialog = new Dialog(context);
                driverdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                driverdialog.setContentView(R.layout.driverpopup);
                lv_Driverlist = (ListView) driverdialog.findViewById(R.id.lv_Driverlist);
                ImageView close = (ImageView) driverdialog.findViewById(R.id.close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        driverdialog.dismiss();
                    }
                });
                DriverAdapter adapter = new DriverAdapter(context);
                lv_Driverlist.setAdapter(adapter);
                driverdialog.show();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(), "No Driver Available",Toast.LENGTH_LONG);
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

    /**********End Dropdown functions******/


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

                    String getscheduleroutecode = "0";
                    dbadapter.open();
                    Cursor getschedulelist = dbadapter.GetScheduleDB();
                    if (getschedulelist.getCount() > 0) {
                        for (int i = 0; i < getschedulelist.getCount(); i++) {
                            getscheduleroutecode = getschedulelist.getString(1);
                        }
                    }
                    // customer
                    jsonObj = api.GetCustomerDetails(preferenceMangr.pref_getString("deviceid"), getscheduleroutecode, "synccustomer.php");
                    if (isSuccessful(jsonObj)) {
                        dbadapter.synccustomer(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "customer", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    // cashnotpaiddetails
                    jsonObj = api.GetCashNotPaidDetails(preferenceMangr.pref_getString("deviceid"),preferenceMangr.pref_getString("getvancode"),getscheduleroutecode,"synccashnotpaiddetails.php");
                    dbadapter.DeleteCashNotPaid();
                    if (isSuccessful(jsonObj)) {
                        dbadapter.synccashnotpaiddetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "cashnotpaiddetails", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }
                    //Get General settings
                    String getschedulestatus = dbadapter.GetScheduleStatusDB();
                    if(getschedulestatus.equals("yes")){
                        //Sales Schedule
                        jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncsalesschedulemobile.php",context);
                        if (isSuccessful(jsonObj)) {
                            dbadapter.syncsalesschedulemobile(jsonObj);
                            api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"),"salesschedule",preferenceMangr.pref_getString("getvancode"),"");
                        }
                    }else{
                        //Sales Schedule portal
                        jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncsalesschedule.php",context);
                        if (isSuccessful(jsonObj)) {
                            dbadapter.syncsalesschedule(jsonObj);
                            api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"),"salesscheduleportal",preferenceMangr.pref_getString("getvancode"),"");
                        }
                    }


                    /*****SYNCH ALL MASTER************/
                    //company master
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"synccompanymaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.synccompanymaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "companymaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //if(BuildConfig.DEBUG)
                        Log.w("Schedule Activity : "," Sync All : UPI Vender Details");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"), "syncupivendermaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncupivendermaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "upivendermaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //if(BuildConfig.DEBUG)
                        Log.w("Schedulee Activity : "," Sync All : Company Vender Master");
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"), "synccompanyvenderdetails.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.synccompanyvenderdetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "companyvendermaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //area master
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncareamaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncareamaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "areamaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //brand master
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncbrandmaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncbrandmaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "brandmaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //currency
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"synccurrency.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.synccurrency(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "currency", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //Receipt remarks
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncreceiptremarks.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncreceiptremarks(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "receiptremarks", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }
                    //tax
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"synctax.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.synctax(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "tax", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }
                    //Bill type
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncbilltype.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncbilltype(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "billtype", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }
                    //city
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"synccitymaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.synccitymaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "citymaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //employee catergory
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncemployeecategory.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncemployeecategory(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "employeecategory", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //employee master
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncemployeemaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncemployeemaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "employeemaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    Cursor getschedulelist1 = dbadapter.GetScheduleDB();
                    if(getschedulelist1.getCount() >0){
                        for(int i=0;i<getschedulelist1.getCount();i++) {
                            MenuActivity.getroutecode = getschedulelist1.getString(1);
                            preferenceMangr.pref_putString("getroutecode",getschedulelist1.getString(1));
                        }
                    }

                    //expenses head
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncexpenseshead.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncexpenseshead(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "expenseshead", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //financial year
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncfinancialyear.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncfinancialyear(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "financialyear", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //general settings
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncgeneralsettings.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncgeneralsettings(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "generalsettings", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //itemgroup master
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncitemgroupmaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncitemgroupmaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "itemgroupmaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //item master
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncitemmaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncitemmaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "itemmaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //itemn price list transaction
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncitempricelisttransaction.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncitempricelisttransaction(jsonObj);

                        /*Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat mformat= new SimpleDateFormat("dd-MM-yyyy h:mm a");
                        MenuActivity.pricelistlastsyncdate = mformat.format(calendar.getTime());*/
                        //Toast.makeText(context,pricelistlastsyncdate,Toast.LENGTH_SHORT).show();

                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "itempricelisttransaction", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }


                    //item subgroup master
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncitemsubgroupmaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncitemsubgroupmaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "itemsubgroupmaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //route
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncroute.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncroute(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "route", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //route details
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncroutedetails.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncroutedetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "routedetails", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }
                    //transport mode
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"synctransportmode.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.synctransportmode(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "transportmode", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //transport
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"synctransport.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.synctransport(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "transport", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //transportareamapping
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"synctransportcitymapping.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.synctransportareamapping(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "transportcitymapping", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //scheme
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncscheme.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncscheme(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "scheme", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //scheme item details
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncschemeitemdetails.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncschemeitemdetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "schemeitemdetails", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //scheme rate details
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncschemeratedetails.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncschemeratedetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "schemeratedetails", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //unit master
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncunitmaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncunitmaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "unitmaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //van stock
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncvanstock.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncvanstocktransaction(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "vanstock", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //vehicle master
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncvehiclemaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncvehiclemaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "vehiclemaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //voucher settings
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncvouchersettings.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncvouchersettings(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "vouchersettings", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //expenses
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncexpenses.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncexpensesmaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "expenses", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //cashclose
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"synccashclose.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.synccashclose(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "cashclose", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //cashclose
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncsalesclose.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncsalesclose(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "salesclose", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //cashreport
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"synccashreport.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.synccashreport(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "cashreport", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //denomination
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncdenomination.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncdenomination(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "denomination", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //receipt
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncreceipt.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncreceipt(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "receipt", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //order details
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncorderdetails.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncorderdetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "orderdetails", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //Sales
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncsales.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncsales(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "sales", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //Salesitemdetails
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncsalesitemdetails.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncsalesitemdetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "salesitemdetails", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //Sales order
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncsalesorder.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncsalesorder(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "salesorder", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }
                    //Sales order itemdetails
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncsalesorderitemdetails.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncsalesorderitemdetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "salesorderitemdetails", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //Salesreturn
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncsalesreturn.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncsalesreturn(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "salesreturn", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }


                    //Salesretrunitemdetails
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncsalesreturnitemdetails.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncsalesreturnitemdetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "salesreturnitemdetails", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //nilstock
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncnilstock.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncnilstock(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "nilstock", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    //maxrefno
                    LoginActivity.getfinanceyrcode = dbadapter.GetFinancialYrCode();
                    preferenceMangr.pref_putString("getfinanceyrcode",dbadapter.GetFinancialYrCode());
                    jsonObj = api.GetMaxCode(preferenceMangr.pref_getString("deviceid"),preferenceMangr.pref_getString("getfinanceyrcode"),"syncmaxrefno.php");
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncmaxrefno(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "maxrefno", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }

                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"), "syncvanmaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncvanmaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "vanmaster", "0", "");
                    }

                    /*try{
                        *//*Cursor cursor =dbadapter.getprinterdetails();
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
                        dbadapter.close();
                    }*/

                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"), "syncstatemaster.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncstatemaster(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "statemaster", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }
                    //Not Purchased
                    jsonObj = api.GetNotPurchasedDetails(preferenceMangr.pref_getString(Constants.KEY_DEVICEID),"syncnotpurchaseddetails.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncnotpurchaseddetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString(Constants.KEY_DEVICEID), "notpurchased", preferenceMangr.pref_getString(Constants.KEY_GETVANCODE), preferenceMangr.pref_getString(Constants.KEY_GET_SCHEDULE_SCHEDULECODE));
                    }

                    //Not purchased remarks
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString(Constants.KEY_DEVICEID),"syncnotpurchasedremarks.php",context);
                    if (isSuccessful(jsonObj)) {
                        dbadapter.syncNotPurchasedRemarks(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString(Constants.KEY_DEVICEID), "receiptremarks", preferenceMangr.pref_getString(Constants.KEY_GETVANCODE), preferenceMangr.pref_getString(Constants.KEY_GET_SCHEDULE_SCHEDULECODE));
                    }
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

                try {

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
                        DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                        mDbErrHelper.open();
                        String geterrror = e.toString();
                        mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        mDbErrHelper.close();
                    } finally {
                        objdatabaseadapter.close();
                    }
               /* if(!MenuActivity. getsalesclosecount.equals("0") && !MenuActivity. getsalesclosecount.equals("null") &&
                        !MenuActivity. getsalesclosecount.equals("") && !MenuActivity. getsalesclosecount.equals(null)){
                    MenuActivity.OrderForm.setVisibility(View.VISIBLE);
                }else{
                    MenuActivity.OrderForm.setVisibility(View.GONE);
                }*/

                }catch (Exception e) {
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
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
            try {
                if (result.size() >= 1) {
                    if (result.get(0).ScheduleCode.length > 0) {
                        for (int j = 0; j < result.get(0).ScheduleCode.length; j++) {
                            DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
                            dataBaseAdapter.open();
                            dataBaseAdapter.UpdateSalesCloseFlag(result.get(0).ScheduleCode[j]);
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

    /**********END Asynchronous Claass***************/
    /*********************START BASE ADAPTER ********************/
    //Route Adapter
    public class RouteAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;

        RouteAdapter(Context c) {
            context = c;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return routecode.length;
        }

        @Override
        public Object getItem(int position) {
            return routecode[position];
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
                convertView = layoutInflater.inflate(R.layout.routepopuplist, parent, false);
                mHolder = new ViewHolder();
                try {
                    mHolder.listroutename = (TextView) convertView.findViewById(R.id.listroutename);
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
                if(!String.valueOf(routenametamil[position]).equals("") && !String.valueOf(routenametamil[position]).equals(null)
                        && !String.valueOf(routenametamil[position]).equals("null")){
                    mHolder.listroutename.setText(String.valueOf(routenametamil[position]));
                }else{
                    mHolder.listroutename.setText(String.valueOf(routename[position]));
                }
            } catch (Exception e) {
                Log.i("Route", e.toString());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!String.valueOf(routenametamil[position]).equals("") && !String.valueOf(routenametamil[position]).equals(null)
                            && !String.valueOf(routenametamil[position]).equals("null")){
                        if(whichroute.equals("Others")){
                            txtothersroute.setText(routenametamil[position]);
                        }else{
                            txtroute.setText(routenametamil[position]);
                        }

                    }else{
                        if(whichroute.equals("Others")){
                            txtothersroute.setText(routename[position]);
                        }else{
                            txtroute.setText(routename[position]);
                        }
                    }
                    getscheduleroutecode = routecode[position];
                    if(routecode[position].equals("0")){
                        txtothersroute.setVisibility(View.VISIBLE);
                        txtothersroute.setText("");
                    }else{
                        txtothersroute.setVisibility(View.GONE);
                        txtothersroute.setText("");
                    }

                    if(whichroute.equals("Others")){
                        txtothersroute.setVisibility(View.VISIBLE);
                        txtothersroute.setText(routenametamil[position]);
                    }
                    routedialog.dismiss();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView listroutename;

        }

    }
    //Vehicle Adapter
    public class VehicleAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;

        VehicleAdapter(Context c) {
            context = c;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return vehiclecode.length;
        }

        @Override
        public Object getItem(int position) {
            return vehiclecode[position];
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
                convertView = layoutInflater.inflate(R.layout.vehiclepopuplist, parent, false);
                mHolder = new ViewHolder();
                try {
                    mHolder.listvehiclename = (TextView) convertView.findViewById(R.id.listvehiclename);
                } catch (Exception e) {
                    Log.i("Vehicle", e.toString());
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
                mHolder.listvehiclename.setText(String.valueOf(vehiclename[position]));
            } catch (Exception e) {
                Log.i("Vehicle", e.toString());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtvechiclename.setText(vehiclename[position]);
                    getschedulevehiclecode = vehiclecode[position];
                    vehicledialog.dismiss();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView listvehiclename;

        }

    }
    //Sales Rep Adapter
    public class SalesRepAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;

        SalesRepAdapter(Context c) {
            context = c;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return employeecode.length;
        }

        @Override
        public Object getItem(int position) {
            return employeecode[position];
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
                convertView = layoutInflater.inflate(R.layout.salesreppopuplist, parent, false);
                mHolder = new ViewHolder();
                try {
                    mHolder.listsalesrepname = (TextView) convertView.findViewById(R.id.listsalesrepname);
                } catch (Exception e) {
                    Log.i("Sales Rep", e.toString());
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
                if(!employeenametamil[position].equals("") && !employeenametamil[position].equals(null)
                        && !employeenametamil[position].equals("null")){
                    mHolder.listsalesrepname.setText(String.valueOf(employeenametamil[position]));
                }else{
                    mHolder.listsalesrepname.setText(String.valueOf(employeename[position]));
                }

            } catch (Exception e) {
                Log.i("Sales Rep", e.toString());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!employeenametamil[position].equals("") && !employeenametamil[position].equals(null)
                            && !employeenametamil[position].equals("null")){
                        txtsalesrep.setText(employeenametamil[position]);
                    }else{
                        txtsalesrep.setText(employeename[position]);
                    }
                    getsalesrepcode = employeecode[position];
                    salesrepdialog.dismiss();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView listsalesrepname;

        }

    }
    //Driver Adapter
    public class DriverAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;

        DriverAdapter(Context c) {
            context = c;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return drivercode.length;
        }

        @Override
        public Object getItem(int position) {
            return drivercode[position];
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
                convertView = layoutInflater.inflate(R.layout.driverpopuplist, parent, false);
                mHolder = new ViewHolder();
                try {
                    mHolder.listdrivername = (TextView) convertView.findViewById(R.id.listdrivername);
                } catch (Exception e) {
                    Log.i("Driver", e.toString());
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
                if(!drivernametamil[position].equals("") && !drivernametamil[position].equals(null)
                        && !drivernametamil[position].equals("null")){
                    mHolder.listdrivername.setText(String.valueOf(drivernametamil[position]));
                }else{
                    mHolder.listdrivername.setText(String.valueOf(drivername[position]));
                }

            } catch (Exception e) {
                Log.i("Driver", e.toString());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!drivernametamil[position].equals("") && !drivernametamil[position].equals(null) &&
                            !drivernametamil[position].equals("null")){
                        txtdrivername.setText(drivernametamil[position]);
                    }else{
                        txtdrivername.setText(drivername[position]);
                    }
                    getscheduledrivercode = drivercode[position];
                    driverdialog.dismiss();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView listdrivername;

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
    }
    /*********************END BASE ADAPTER ********************/

    protected  class AsyncSyncCheckVanStock  extends
            AsyncTask<String, JSONObject, String> {
        String List = "Success";
        JSONObject jsonObj = null;
        ProgressDialog loading;

        @Override
        protected String doInBackground(String... params) {

            RestAPI api = new RestAPI();
            String result = "";
            DataBaseAdapter dataBaseAdapter = null;
            String deviceid = preferenceMangr.pref_getString("deviceid");
            String getvancode = preferenceMangr.pref_getString("getvancode");
            try {
                dataBaseAdapter = new DataBaseAdapter(context);
                dataBaseAdapter.open();
                networkstate = isNetworkAvailable();
                if (networkstate == true) {
                    jsonObj = api.CheckVanStock(deviceid, "check_vanstock.php", preferenceMangr.pref_getString("getvancode"));
                    if (isSuccessful(jsonObj)) {
                        JSONArray json_category = jsonObj.getJSONArray("Value");
                        JSONObject obj = (JSONObject) json_category.get(0);
                        result = obj.getString("count");
                        api.udfnSyncDetails(deviceid, "checkvanstock", preferenceMangr.pref_getString("getvancode"), getschedulecode);
                    }
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncSync", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            } finally {
                dataBaseAdapter.close();
            }
            return result;
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


                DataBaseAdapter objdatabaseadapter = null;
                try {
                    objdatabaseadapter = new DataBaseAdapter(context);
                    objdatabaseadapter.open();
                    if(!result.equals("") && !result.equals("null") && !result.equals(null)){
                        if(Integer.parseInt(result)<=0){
                            Toast toast = Toast.makeText(getApplicationContext(), "Insufficient van stock..",Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return;
                        }else if(Integer.parseInt(result)==999999999){
                            Toast toast = Toast.makeText(getApplicationContext(), "Van stock verification not completed for previous schedule",Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return;
                        } else{
                            String getschedulecode = objdatabaseadapter.insertSchedule(scheduledate.getText().toString(), getscheduleroutecode, getschedulevehiclecode,
                                    getsalesrepcode, getscheduledrivercode, txthelpername.getText().toString(),
                                    txttripaadvance.getText().toString(), txtstartingkm.getText().toString(),getewayurl);
                            getsalesschedulecode = getschedulecode;
                            preferenceMangr.pref_putString("getsalesschedulecode",getschedulecode);

                            Toast toast = Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            networkstate = isNetworkAvailable();
                            if (networkstate == true) {
                                new AsyncScheduleDetails().execute();
                            }
                            isFromschedule="";
                            if(preferenceMangr.pref_getString("getbusiness_type").equals("2")){
                                Intent i = new Intent(context, MenuActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }else {
                                Intent i = new Intent(context, ReviewCashNotpaidDetails.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }

                        }
                    }

                } catch (Exception e) {
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                } finally {
                    objdatabaseadapter.close();
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

    protected  class AsyncSyncCheckVanStockVerification extends
            AsyncTask<String, JSONObject, String> {
        String List = "Success";
        JSONObject jsonObj = null;
        ProgressDialog loading;

        @Override
        protected String doInBackground(String... params) {

            RestAPI api = new RestAPI();
            String result = "";
            DataBaseAdapter dataBaseAdapter = null;
            String deviceid = preferenceMangr.pref_getString("deviceid");
            String getvancode = preferenceMangr.pref_getString("getvancode");
            try {
                dataBaseAdapter = new DataBaseAdapter(context);
                dataBaseAdapter.open();
                networkstate = isNetworkAvailable();
                if (networkstate == true) {
                    //Get Van Master
                    String getschedulecode = dataBaseAdapter.GetScheduleCode();
                    if(!getschedulecode.equals("") && !getschedulecode.equals("null") && !getschedulecode.equals(null)) {
                        jsonObj = api.VanStockVerification(deviceid, "check_vanstockverification.php", getschedulecode);
                        if (isSuccessful(jsonObj)) {
                            JSONArray json_category = jsonObj.getJSONArray("Value");
                            JSONObject obj = (JSONObject) json_category.get(0);
                            result = obj.getString("count");
                            api.udfnSyncDetails(deviceid, "checkvanstockverification", preferenceMangr.pref_getString("getvancode"), getschedulecode);
                        }
                    }else{
                        result = "1";
                    }

                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncSync", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            } finally {
                dataBaseAdapter.close();
            }
            return result;
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


                DataBaseAdapter objdatabaseadapter = null;
                try {
                    objdatabaseadapter = new DataBaseAdapter(context);
                    objdatabaseadapter.open();
                   if(!result.equals("") && !result.equals("null") && !result.equals(null)){
                       if(Integer.parseInt(result)<=0){
                           Toast toast = Toast.makeText(getApplicationContext(), "Van stock verification not completed for previous schedule",Toast.LENGTH_LONG);
                           toast.setGravity(Gravity.CENTER, 0, 0);
                           toast.show();
                           return;
                       }else{
                           networkstate = isNetworkAvailable();
                           if (networkstate == true) {
                               new AsyncSyncCheckDeliveryNote().execute();
                           }else{
                               Toast toast = Toast.makeText(getApplicationContext(),"Please check internet connection", Toast.LENGTH_LONG);
                               toast.setGravity(Gravity.CENTER, 0, 0);
                               toast.show();
                               return;
                           }
                       }
                   }else{
                       Toast toast = Toast.makeText(getApplicationContext(), "Van stock verification not completed for previous schedule",Toast.LENGTH_LONG);
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
                    objdatabaseadapter.close();
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


    protected  class AsyncSyncCheckDeliveryNote extends
            AsyncTask<String, JSONObject, String> {
        String List = "Success";
        JSONObject jsonObj = null;
        ProgressDialog loading;

        @Override
        protected String doInBackground(String... params) {

            RestAPI api = new RestAPI();
            String result = "";
            DataBaseAdapter dataBaseAdapter = null;
            String deviceid = preferenceMangr.pref_getString("deviceid");
            String getvancode = preferenceMangr.pref_getString("getvancode");
            try {
                dataBaseAdapter = new DataBaseAdapter(context);
                dataBaseAdapter.open();
                networkstate = isNetworkAvailable();
                if (networkstate == true) {
                    //Get schedulecode
                    String getschedulecode = dataBaseAdapter.GetScheduleCode();
                    if(!getschedulecode.equals("") && !getschedulecode.equals("null") && !getschedulecode.equals(null)) {
                        jsonObj = api.DeliveryNote(deviceid, "check_deliverynote.php", getschedulecode);
                        if (isSuccessful(jsonObj)) {
                            JSONArray json_category = jsonObj.getJSONArray("Value");
                            JSONObject obj = (JSONObject) json_category.get(0);
                            result = obj.getString("count");
                            api.udfnSyncDetails(deviceid, "check_deliverynote", preferenceMangr.pref_getString("getvancode"), getschedulecode);
                        }
                    }else{
                        result = "1";
                    }

                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncSync", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            } finally {
                dataBaseAdapter.close();
            }
            return result;
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


                DataBaseAdapter objdatabaseadapter = null;
                try {
                    objdatabaseadapter = new DataBaseAdapter(context);
                    objdatabaseadapter.open();
                    if(!result.equals("") && !result.equals("null") && !result.equals(null)){
                        if(Integer.parseInt(result)<=0){
                            Toast toast = Toast.makeText(getApplicationContext(), "Delivery note not yet generated for this schedule ",Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return;
                        }else{
                           String getschedulecode = objdatabaseadapter.insertSchedule(scheduledate.getText().toString(), getscheduleroutecode, getschedulevehiclecode,
                                   getsalesrepcode, getscheduledrivercode, txthelpername.getText().toString(),
                                   txttripaadvance.getText().toString(), txtstartingkm.getText().toString(),getewayurl);
                           getsalesschedulecode = getschedulecode;
                            preferenceMangr.pref_putString("getsalesschedulecode",getschedulecode);

                           Toast toast = Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_LONG);
                           toast.setGravity(Gravity.CENTER, 0, 0);
                           toast.show();
                           networkstate = isNetworkAvailable();
                           if (networkstate == true) {
                               new AsyncScheduleDetails().execute();
                           }
                            isFromschedule="";
                            if(preferenceMangr.pref_getString("getbusiness_type").equals("2")){
                                Intent i = new Intent(context, MenuActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }else {
                                Intent i = new Intent(context, ReviewCashNotpaidDetails.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }
                        }
                    }else{
                        Toast toast = Toast.makeText(getApplicationContext(), "Delivery note not yet generated for this schedule",Toast.LENGTH_LONG);
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
                    objdatabaseadapter.close();
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

    public void goBack(View v) {
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

    //Close Keyboard
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    @Override
    public void onBackPressed() {
        //goBack(null);
        finish();
    }

    protected  class AsyncCheckPreviousDaySchedule extends
            AsyncTask<String, JSONObject, String> {
        String List = "Success";
        JSONObject jsonObj = null;
        ProgressDialog loading;

        @Override
        protected String doInBackground(String... params) {

            RestAPI api = new RestAPI();
            String result = "";
            DataBaseAdapter dataBaseAdapter = null;
            String deviceid = preferenceMangr.pref_getString("deviceid");
            String getvancode = preferenceMangr.pref_getString("getvancode");
            try {
                dataBaseAdapter = new DataBaseAdapter(context);
                dataBaseAdapter.open();
                networkstate = isNetworkAvailable();
                if (networkstate == true) {
                    //Get Van Master
                    String getschedulecode = dataBaseAdapter.GetScheduleCode();
                    if(!getschedulecode.equals("") && !getschedulecode.equals("null") && !getschedulecode.equals(null)) {
                        jsonObj = api.CheckPreviousdayschedule(deviceid, "check_previousdayschedule.php", getschedulecode,getvancode);
                        if (isSuccessful(jsonObj)) {
                            JSONArray json_category = jsonObj.getJSONArray("Value");
                            JSONObject obj = (JSONObject) json_category.get(0);
                            result = obj.getString("count");
                            api.udfnSyncDetails(deviceid, "checkvanstockverification", preferenceMangr.pref_getString("getvancode"), getschedulecode);
                        }
                    }else{
                        result = "1";
                    }

                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncSync", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            } finally {
                dataBaseAdapter.close();
            }
            return result;
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


                DataBaseAdapter objdatabaseadapter = null;
                try {
                    objdatabaseadapter = new DataBaseAdapter(context);
                    objdatabaseadapter.open();
                    if(!result.equals("") && !result.equals("null") && !result.equals(null)){
                         if(Integer.parseInt(result)<=0){
                            Toast toast = Toast.makeText(getApplicationContext(), "Please wait data's are syncing ",Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            //return;

                        }else if(Integer.parseInt(result)==999999999){
                            Toast toast = Toast.makeText(getApplicationContext(), "Previous schedule sales not close",Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return;
                        }else if(Integer.parseInt(result)==888888888){
                            Toast toast = Toast.makeText(getApplicationContext(), "Previous schedule cash not close",Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return;
                        }
                        else if(Integer.parseInt(result)==1){
                            networkstate = isNetworkAvailable();
                            if (networkstate == true) {

                            }else{
                                Toast toast = Toast.makeText(getApplicationContext(),"Please check internet connection", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                return;
                            }
                        }

                    }

                } catch (Exception e) {
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                } finally {
                    objdatabaseadapter.close();
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
}
