package thanjavurvansales.sss;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.goodiebag.pinview.Pinview;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SalesListActivity extends AppCompatActivity {
    public static SwipeMenuListView saleslistView;
    public Context context;
    private int year, month, day;
    Button btnSubmitpin;
    private Calendar calendar;
    String getschedulecode="0";
    Pinview pinview;
    TextView salesdate;
    public static TextView totalamtval,cashtotalamt,credittotalamt,txtcompanyname;
    TextView einvocie_pending,total_einvoice ,einvoice_generated,total_bill;
    ImageView addsales,saleslistgoback,saleslogout,closesales;
    public static String getsaleslistdate ;
    Dialog pindialog;
    ArrayList<SalesListDetails> saleslist = new ArrayList<SalesListDetails>();
    public static ArrayList<SalesListDetails> getdata;
    Dialog dialogstatus,companydialog,dialogeinvoice;
    boolean networkstate;
    String[] companycode,companyname,shortname;
    ListView lv_CompanyList;
    public static String getsalesreviewtransactionno="",getsalesreviewfinanicialyear="",
            getsalesreviewcompanycode="",getfiltercompanycode="0",getstaticflag="0",getcancelflag="",gesalesreviewbookingno="",
            getsalesreviewtbilldate="",geteinvoicepending="0", geteinvoicegenerated="0",
            gettotaleinvoicebills="0";
    public static Integer geteinvoicestatus=0;
    Spinner selectpaymenttype,selectpaymentstatus;
    String[] arrapaymenttype,arrpaymentstatus;
    String getpaymenttype="All Bills";
    String getpaymentstatus="Paid &amp; Not Paid";

    Dialog areadialog,customerdialog;
    public  boolean issales=false,isloadeinvoice=false;
    SalesListBaseAdapterList adapter=null;
    boolean deviceFound;
    boolean issalesclose=false;
    private PrintData printData;
    Dialog printpopup;
    ImageView menusyncsales;
    LinearLayout lveinvoicepending, lveinvoicegenerated, lvtotaleinvoicebills, lvtotalbills;

    Dialog salescashclose;
    TextView popup_salesclose,popup_cashclose;

    BluetoothAdapter mBluetoothAdapter;
    public static PreferenceMangr preferenceMangr=null;
    public String downloadFileName="";
    ProgressDialog downloadLoading;
    DropBoxAsyncResponseListener dropboxlistener;
    public long downloadFileID=0;
    public String custMobilenoForPDFShare="";
    ListView lv_freeitemlist;
    String[] FreeItemName,FreeItemOp,FreeItemHandover,FreeItemDistributed,FreeItemBalance,FreeItemCode,FreeItemSNO;
    Dialog freeitemdialog;

    ArrayList<Object> downloadData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_list);

        //declare all variables
        context=this;
        saleslistView = (SwipeMenuListView) findViewById(R.id.saleslistView);
        salesdate = (TextView)findViewById(R.id.salesdate);
        addsales = (ImageView)findViewById(R.id.addsales);
        totalamtval = (TextView)findViewById(R.id.totalamtval);
        credittotalamt = (TextView)findViewById(R.id.credittotalamt);
        cashtotalamt = (TextView)findViewById(R.id.cashtotalamt);
        saleslogout = (ImageView)findViewById(R.id.saleslogout);
        saleslistgoback = (ImageView)findViewById(R.id.saleslistgoback);
        closesales = (ImageView)findViewById(R.id.closesales);
        txtcompanyname = (TextView)findViewById(R.id.txtcompanyname);
        selectpaymenttype = (Spinner)findViewById(R.id.selectpaymenttype);
        selectpaymentstatus = (Spinner)findViewById(R.id.selectpaymentstatus);
        arrapaymenttype = getResources().getStringArray(R.array.paymenttype);
        arrpaymentstatus = getResources().getStringArray(R.array.paymentstatus);
        menusyncsales = (ImageView)findViewById(R.id.menusyncsales);
        einvocie_pending=(TextView)findViewById(R.id.einvocie_pending);
        total_einvoice=(TextView)findViewById(R.id.total_einvoice);
        einvoice_generated=(TextView)findViewById(R.id.einvoice_generated);
        total_bill=(TextView)findViewById(R.id.total_bill);
        lveinvoicepending = (LinearLayout)findViewById(R.id.lveinvoicepending);
        lveinvoicegenerated = (LinearLayout)findViewById(R.id.lveinvoicegenerated);
        lvtotaleinvoicebills =(LinearLayout)findViewById(R.id.lvtotaleinvoicebills);
        lvtotalbills=(LinearLayout)findViewById(R.id.lvtotalbills);
        getfiltercompanycode="0";
        geteinvoicepending="0";
        geteinvoicegenerated="0";
        gettotaleinvoicebills="0";
        total_einvoice.setText("0");
        einvoice_generated.setText("0");
        einvocie_pending.setText("0");
        total_bill.setText("0");
        isloadeinvoice=false;
        if(getdata!=null){
            getdata.clear();
        }



        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }catch (Exception e){
            Log.d("Bluetooth Adapter : ",e.toString());
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName()+
                    " - Get default bluetooth adapter", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
        try {
            preferenceMangr = new PreferenceMangr(context);
        }catch (Exception e){
            Log.d("PreferenceMangr : ",e.toString());
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName()+
                    " - Get PreferenceMangr", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }

        this.dropboxlistener = new DropBoxAsyncResponseListener() {



            @Override
            public void onAsyncTaskResponseReceived(Object response, int requestType) {
                onReceiveAsyncResult(response, requestType);
            }
        };

        //Get Current date
        DataBaseAdapter objdatabaseadapter = null;
        Cursor getschedulelist=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            //LoginActivity.getformatdate = objdatabaseadapter.GenCreatedDate();
            //LoginActivity.getcurrentdatetime = objdatabaseadapter.GenCurrentCreatedDate();

            preferenceMangr.pref_putString("getformatdate",objdatabaseadapter.GenCreatedDate());
            preferenceMangr.pref_putString("getcurrentdatetime",objdatabaseadapter.GenCurrentCreatedDate());

            getschedulelist = objdatabaseadapter.GetScheduleDB();
            if(getschedulelist.getCount() >0){
                for(int i=0;i<getschedulelist.getCount();i++) {
                    MenuActivity.getschedulecode = getschedulelist.getString(0);
                    preferenceMangr.pref_putString("getschedulecode",getschedulelist.getString(0));
                }
            }else{
                MenuActivity.getschedulecode = "";
                preferenceMangr.pref_putString("getschedulecode","");
            }
            //Get Cash close Count
            MenuActivity.getcashclosecount = objdatabaseadapter.GetCashClose(preferenceMangr.pref_getString("getschedulecode"));
            preferenceMangr.pref_putString("getcashclosecount",objdatabaseadapter.GetCashClose(preferenceMangr.pref_getString("getschedulecode")));
            //Get sales close Count
            MenuActivity.getsalesclosecount = objdatabaseadapter.GetSalesClose(preferenceMangr.pref_getString("getschedulecode"));
            preferenceMangr.pref_putString("getsalesclosecount",objdatabaseadapter.GetSalesClose(preferenceMangr.pref_getString("getschedulecode")));

            MenuActivity.getdenominationcount = objdatabaseadapter.GetDenominationCount(preferenceMangr.pref_getString("getschedulecode"));
            SalesActivity.confromBilltype=false;
        }catch (Exception e){
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName()+
                    " - Get and set currendate and schedule code", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }finally {
            // this gets called even if there is an exception somewhere above
            if (objdatabaseadapter != null)
                objdatabaseadapter.close();
            if(getschedulelist != null)
                getschedulelist.close();
        }



        DataBaseAdapter objdatabaseadapter1 = null;

        //Get Schdule
        objdatabaseadapter1 = new DataBaseAdapter(context);
        objdatabaseadapter1.open();
        String todayschedulecount = objdatabaseadapter1.gettodayschedulecount();
        if (todayschedulecount.equals("0")) {
            closesales.setVisibility(View.GONE);
        }else{
            // closesales.setVisibility(View.VISIBLE);
            if(!preferenceMangr.pref_getString("getsalesclosecount").equals("0") && !preferenceMangr.pref_getString("getsalesclosecount").equals("null") &&
                    !preferenceMangr.pref_getString("getsalesclosecount").equals("") && !preferenceMangr.pref_getString("getsalesclosecount").equals(null)){
                closesales.setVisibility(View.GONE);
            }else{
                closesales.setVisibility(View.VISIBLE);
            }
        }




        //Sync sales details
        menusyncsales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                networkstate = isNetworkAvailable();
                if(networkstate == true){
                    new AsyncCustomerDetails().execute();
                    new AsyncSalesDetails().execute();
                    new AsyncNilStockDetails().execute();
                    new AsyncSalesCancelDetails().execute();
                    new AsyncSalesStockConversionDetails().execute();
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Please check internet connection", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
            }
        });

        //open sales screen
        addsales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    DataBaseAdapter objdatabaseadapter1 = null;

                    //Get Schdule
                    if (context == null && getApplicationContext() != null)
                        context=getApplicationContext();
                    if(preferenceMangr==null)
                        preferenceMangr = new PreferenceMangr(context);

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

                            if (preferenceMangr.pref_getString("getbusiness_type").equals("2")) {
                                if (!preferenceMangr.pref_getString("schedule_getschedulecount").equals("0") && !preferenceMangr.pref_getString("schedule_getschedulecount").equals("null") && !preferenceMangr.pref_getString("schedule_getschedulecount").equals(null)) {
                                    if (!preferenceMangr.pref_getString("schedule_getreciptcount").equals("0") || !preferenceMangr.pref_getString("schedule_getexpensecount").equals("0")) {

                                        if (!preferenceMangr.pref_getString("schedule_getcashclosecount").equals("0") && !preferenceMangr.pref_getString("schedule_getcashclosecount").equals("null") &&
                                                !preferenceMangr.pref_getString("schedule_getcashclosecount").equals("")) {
                                            /*bottomlayout.setVisibility(View.GONE);
                                            btnSaveSchedule.setVisibility(View.VISIBLE);*/

                                        } else {
                                            salescashclose = new Dialog(context);
                                            salescashclose.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                            salescashclose.setContentView(R.layout.salescashclose_popup);
                                            salescashclose.setCanceledOnTouchOutside(false);
                                            popup_salesclose = (TextView) salescashclose.findViewById(R.id.popup_salesclose);
                                            popup_cashclose = (TextView) salescashclose.findViewById(R.id.popup_cashclose);

                                            popup_salesclose.setVisibility(View.GONE);
                                            popup_cashclose.setText("Close Cash");


                                            popup_cashclose.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    LoginActivity.iscash = true;
                                                    Intent i = new Intent(SalesListActivity.this, CashReportActivity.class);
                                                    startActivity(i);
                                                    salescashclose.dismiss();

                                                }
                                            });
                                            salescashclose.show();
                                            popup_salesclose.setVisibility(View.GONE);
                                        }
                                    } else {
                                        Intent i = new Intent(SalesListActivity.this, ScheduleActivity.class);
                                        startActivity(i);
                                    }
                                } else {
                                    Intent i = new Intent(SalesListActivity.this, ScheduleActivity.class);
                                    startActivity(i);
                                }
                            }else {

                                salescashclose = new Dialog(context);
                                salescashclose.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                salescashclose.setContentView(R.layout.salescashclose_popup);
                                salescashclose.setCanceledOnTouchOutside(false);
                                popup_salesclose = (TextView) salescashclose.findViewById(R.id.popup_salesclose);
                                popup_cashclose = (TextView) salescashclose.findViewById(R.id.popup_cashclose);

                                if (!preferenceMangr.pref_getString("schedule_getschedulecount").equals("0") && !preferenceMangr.pref_getString("schedule_getschedulecount").equals("null") && !preferenceMangr.pref_getString("schedule_getschedulecount").equals(null)) {
                                    if (!preferenceMangr.pref_getString("schedule_getsalesclosecount").equals("0") && !preferenceMangr.pref_getString("schedule_getsalesclosecount").equals("null") &&
                                            !preferenceMangr.pref_getString("schedule_getsalesclosecount").equals("") && !preferenceMangr.pref_getString("schedule_getsalesclosecount").equals(null) &&
                                            !preferenceMangr.pref_getString("schedule_getcashclosecount").equals("0") && !preferenceMangr.pref_getString("schedule_getcashclosecount").equals("null") &&
                                            !preferenceMangr.pref_getString("schedule_getcashclosecount").equals("") && !preferenceMangr.pref_getString("schedule_getcashclosecount").equals(null)) {
                            /*bottomlayout.setVisibility(View.GONE);
                            btnSaveSchedule.setVisibility(View.VISIBLE);*/
                                        Intent i = new Intent(SalesListActivity.this, ScheduleActivity.class);
                                        startActivity(i);

                                    } else {
                                        salescashclose = new Dialog(context);
                                        salescashclose.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        salescashclose.setContentView(R.layout.salescashclose_popup);
                                        salescashclose.setCanceledOnTouchOutside(false);
                                        popup_salesclose = (TextView) salescashclose.findViewById(R.id.popup_salesclose);
                                        popup_cashclose = (TextView) salescashclose.findViewById(R.id.popup_cashclose);

                                        popup_salesclose.setText("Close Sales");
                                        popup_cashclose.setText("Close Cash");

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
                                                Intent i = new Intent(SalesListActivity.this, CashReportActivity.class);
                                                startActivity(i);
                                                salescashclose.dismiss();

                                            }
                                        });
                                        salescashclose.show();
                                    }
                                } else {
                                    Intent i = new Intent(SalesListActivity.this, ScheduleActivity.class);
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
                            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName()+
                                    " - todayschedulecount is zero", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            mDbErrHelper.close();
                        } finally {
                            if (objdatabaseadapter1 != null)
                                objdatabaseadapter1.close();
                        }


                    }else{

                        if(!preferenceMangr.pref_getString("getcashclosecount").equals("0") && !preferenceMangr.pref_getString("getcashclosecount").equals("null") &&
                                !preferenceMangr.pref_getString("getcashclosecount").equals("") && !preferenceMangr.pref_getString("getcashclosecount").equals(null) ){
                            Toast toast = Toast.makeText(getApplicationContext(),"Cash Closed ", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            //Toast.makeText(getApplicationContext(), "Cash Closed ", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(!preferenceMangr.pref_getString("getsalesclosecount").equals("0") && !preferenceMangr.pref_getString("getsalesclosecount").equals("null") &&
                                !preferenceMangr.pref_getString("getsalesclosecount").equals("") && !preferenceMangr.pref_getString("getsalesclosecount").equals(null)){
                            Toast toast = Toast.makeText(getApplicationContext(),"Sales Closed ", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            //Toast.makeText(getApplicationContext(), "Sales Closed ", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(!preferenceMangr.pref_getString("getfinanceyrcode").equals("") &&
                                !preferenceMangr.pref_getString("getfinanceyrcode").equals("0") &&
                                !preferenceMangr.pref_getString("getfinanceyrcode").equals("null")
                                && !(preferenceMangr.pref_getString("getfinanceyrcode").equals(null))) {


                            DataBaseAdapter objdatabaseadapter = null;
                            String getvouchersettingscount = "0";
                            String billtypecode="0";
                            String billcopystatus="";
                            String cashpaidstatus="";
                            String getsalestransactionno="0";
                            String getfinanicialyear="0";
                            String getbookingno = "";
                            String getbillno = "";
                            Cursor getschedulelist1=null;
                            String getgstinno = "";
                            String gstin="";
//                            int einvoice_status=0;
//                            String einvoiceurl="";
//                            String filepath="";
//                            String showeinvoicepopup="0";
                            try {
                                //Vocher settings
                                objdatabaseadapter = new DataBaseAdapter(context);
                                objdatabaseadapter.open();
                                getschedulelist1 = objdatabaseadapter.GetScheduleDB();
                                if(getschedulelist1.getCount() >0){
                                    for(int i=0;i<getschedulelist1.getCount();i++) {
                                        MenuActivity.getschedulecode = getschedulelist1.getString(0);
                                        preferenceMangr.pref_putString("getschedulecode",getschedulelist1.getString(0));
                                    }
                                }else{
                                    MenuActivity.getschedulecode = "";
                                    preferenceMangr.pref_putString("getschedulecode","");
                                }
                                if (!(preferenceMangr.pref_getString("getschedulecode").equals(""))
                                        && !(preferenceMangr.pref_getString("getschedulecode").equals(null))) {
                                    getvouchersettingscount = objdatabaseadapter.GetSalesVoucherSettingsDB();
                                    String getcount = objdatabaseadapter.GetSalesItemStockCount();

                                    String getshowpopupstatus = objdatabaseadapter.getshowcashpaidpopupstatus();

                                    if(getshowpopupstatus.equals("null") || getshowpopupstatus.equals(null)){
                                        getshowpopupstatus="no";
                                    }

                                    objdatabaseadapter.DeleteSalesItemCart();
                                    if(Double.parseDouble(getcount) > 0 ){
                                        if (getvouchersettingscount.equals("0")) {
//                                            Cursor Cur1 = objdatabaseadapter.CheckEinvoice();
//                                            if(Cur1.getCount()>0) {
//                                                gstin=Cur1.getString(0);
//                                                einvoice_status=  Cur1.getInt(1);
//                                                einvoiceurl=Cur1.getString(2);
//                                                filepath=Cur1.getString(3);
//
//                                                if(((einvoiceurl.equals("") )
//                                                        || (filepath.equals("") ))
//                                                        && (einvoice_status!=2) ){
//                                                    showeinvoicepopup="1";
//                                                }else{
//                                                    showeinvoicepopup="0";
//                                                }
//                                            }
//
//                                            if(getEinvoicepopupstatus.equals("yes") && showeinvoicepopup.equals("1")){
//
//                                                dialogeinvoice = new Dialog(context);
//                                                dialogeinvoice.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                                                dialogeinvoice.setContentView(R.layout.einvoicepopup);
//                                                dialogeinvoice.setCanceledOnTouchOutside(false);
//                                                final Button txtYesAction = (Button) dialogeinvoice.findViewById(R.id.btnOkAction);
//                                                final ImageView closepopup= (ImageView) dialogeinvoice.findViewById(R.id.closepopup);
////                                                    final TextView txtNoAction = (TextView) dialogeinvoice.findViewById(R.id.txtNoAction);
//
//                                                txtYesAction.setOnClickListener(new View.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(View view) {
//                                                        txtYesAction.setEnabled(false);
////                                                            txtNoAction.setEnabled(false);
//                                                        dialogeinvoice.dismiss();
//                                                        return;
//
//
//                                                    }
//                                                });
//                                                closepopup.setOnClickListener(new View.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(View view) {
//                                                        dialogeinvoice.dismiss();
//
//                                                    }
//                                                });
//                                                dialogeinvoice.setCanceledOnTouchOutside(false);
//                                                dialogeinvoice.show();
//
//                                            }
//                                            else{
                                            String getbillcopystatus = objdatabaseadapter.GetBillCopyDB();

                                            if (getbillcopystatus.equals("yes") || getshowpopupstatus.equals("yes")){
                                                Cursor Cur = objdatabaseadapter.CheckPaymentVoucher();
                                                if(Cur.getCount()>0) {
                                                    billtypecode = Cur.getString(0);
                                                    billcopystatus = Cur.getString(1);
                                                    cashpaidstatus = Cur.getString(2);
                                                    getsalestransactionno = Cur.getString(3);
                                                    getfinanicialyear = Cur.getString(4);
                                                    getbookingno = Cur.getString(5);
                                                    getbillno = Cur.getString(6);
                                                    getgstinno = Cur.getString(7);

                                                    if (cashpaidstatus.equals("")){

                                                        //Open Payment Voucher
                                                        dialogstatus = new Dialog(context);
                                                        dialogstatus.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                        dialogstatus.setContentView(R.layout.salesreceipt);
                                                        dialogstatus.setCanceledOnTouchOutside(false);
                                                        final TextView paymentbookingno = (TextView)dialogstatus.findViewById(R.id.paymentbookingno);
                                                        final TextView paymentbillno = (TextView)dialogstatus.findViewById(R.id.paymentbillno);
                                                        final CheckBox checkbillcopy = (CheckBox) dialogstatus.findViewById(R.id.checkbillcopy);
                                                        final RadioButton radio_paid = (RadioButton) dialogstatus.findViewById(R.id.radio_paid);
                                                        RadioButton radio_notpaid = (RadioButton) dialogstatus.findViewById(R.id.radio_notpaid);
                                                        Button btnsalessubmit = (Button) dialogstatus.findViewById(R.id.btnsalessubmit);
                                                        ImageView closepopup = (ImageView) dialogstatus.findViewById(R.id.closepopup);
                                                        LinearLayout LLbillcopy = (LinearLayout) dialogstatus.findViewById(R.id.LLbillcopy);
                                                        LinearLayout cashpaid = (LinearLayout) dialogstatus.findViewById(R.id.mainBill);

                                                        paymentbookingno.setText("BK.NO. "+getbookingno);
                                                        paymentbillno.setText("Bill No. "+getbillno);
                                                        closepopup.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                dialogstatus.dismiss();
                                                            }
                                                        });

                                                        if(getbillcopystatus.equals("yes")){
                                                            LLbillcopy.setVisibility(View.VISIBLE);
                                                        }else{
                                                            LLbillcopy.setVisibility(View.GONE);
                                                        }

                                                        if(getshowpopupstatus.equals("yes")){
                                                            cashpaid.setVisibility(View.VISIBLE);
                                                        }else{
                                                            cashpaid.setVisibility(View.GONE);
                                                        }

                                                        if (billtypecode.equals("2")) {
                                                            radio_paid.setChecked(false);
                                                            radio_notpaid.setChecked(true);
                                                            radio_paid.setEnabled(false);
                                                            radio_notpaid.setEnabled(false);
                                                            checkbillcopy.setChecked(true);
                                                        } else {
                                                            radio_paid.setChecked(true);
                                                            radio_notpaid.setChecked(false);
                                                            radio_paid.setEnabled(true);
                                                            radio_notpaid.setEnabled(true);
                                                            if(!getgstinno.equals("")) {
                                                                checkbillcopy.setChecked(true);
                                                            }else{
                                                                checkbillcopy.setChecked(false);
                                                            }
                                                        }
                                                        closepopup.setVisibility(View.GONE);
                                                        final DataBaseAdapter finalObjdatabaseadapter = new DataBaseAdapter(context);
                                                        final String finalGetsalestransactionno = getsalestransactionno;
                                                        final String finalGetfinanicialyear = getfinanicialyear;
                                                        btnsalessubmit.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                String getbillcopy = "";
                                                                String getpaymentstatus = "";
                                                                if (checkbillcopy.isChecked()) {
                                                                    getbillcopy = "yes";
                                                                } else {
                                                                    getbillcopy = "no";
                                                                }
                                                                if (radio_paid.isChecked()) {
                                                                    getpaymentstatus = "yes";
                                                                } else {
                                                                    getpaymentstatus = "no";
                                                                }
                                                                try {
                                                                    finalObjdatabaseadapter.open();
                                                                    String getresult = finalObjdatabaseadapter.UpdateSalesListReceipt(finalGetsalestransactionno,
                                                                            finalGetfinanicialyear, getbillcopy, getpaymentstatus);

                                                                    if (getresult.equals("success")) {
                                                                        dialogstatus.dismiss();
                                                                        Toast toast = Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_LONG);
                                                                        toast.setGravity(Gravity.BOTTOM, 0, 150);
                                                                        toast.show();


                                                                        if (getpaymentstatus.equals("yes")) {

                                                                /*android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(context);
                                                                String message = "Are you sure you want to print?";
                                                                final String finalGettransano = finalGetsalestransactionno;
                                                                final String financialyearcode = finalGetfinanicialyear;
                                                                builder1.setMessage(message)
                                                                        .setIcon(context.getApplicationInfo().icon)
                                                                        .setCancelable(false)
                                                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialog1, int which) {
                                                                                dialog1.dismiss();
                                                                                try {
                                                                                    printData = new PrintData(context);
                                                                                    deviceFound = printData.findBT();
                                                                                    if (!deviceFound) {
                                                                                        Toast toast = Toast.makeText(getApplicationContext(),"Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                                                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                                                                        toast.show();
                                                                                        //Toast.makeText(context, "Please connect to the Bluetooth Printer!", Toast.LENGTH_SHORT).show();
                                                                                    } else {
                                                                                        boolean billPrinted = false;
                                                                                        billPrinted = (boolean) printData.GetSalesReceipt(finalGettransano, financialyearcode);
                                                                                        Intent i = new Intent(context, SalesActivity.class);
                                                                                        startActivity(i);
                                                                                        if (!billPrinted) {
                                                                                            Toast toast = Toast.makeText(getApplicationContext(),"Unable to connect to Bluetooth Printer!", Toast.LENGTH_LONG);
                                                                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                                                                            toast.show();
                                                                                            //Toast.makeText(context, "Unable to connect to Bluetooth Printer!", Toast.LENGTH_SHORT).show();
                                                                                            return;
                                                                                        }
                                                                                    }
                                                                                } catch (Exception e) {
                                                                                    Toast toast = Toast.makeText(getApplicationContext(),"Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                                                    toast.show();
                                                                                   // Toast.makeText(context, "Please connect to the Bluetooth Printer!", Toast.LENGTH_SHORT).show();
                                                                                    DataBaseAdapter mDbErrHelper2 = new DataBaseAdapter(context);
                                                                                    mDbErrHelper2.open();
                                                                                    mDbErrHelper2.insertErrorLog(e.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                                                                    mDbErrHelper2.close();

                                                                                }
                                                                            }
                                                                        })
                                                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialog1, int which) {
                                                                                dialog1.cancel();
                                                                                Intent i = new Intent(context, SalesActivity.class);
                                                                                startActivity(i);
                                                                            }
                                                                        }).show();*/

                                                                            printpopup = new Dialog(context);
                                                                            printpopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                                            printpopup.setContentView(R.layout.printpopup);

                                                                            TextView txtYesAction = (TextView) printpopup.findViewById(R.id.txtYesAction);
                                                                            TextView txtNoAction = (TextView) printpopup.findViewById(R.id.txtNoAction);
                                                                            txtYesAction.setOnClickListener(new View.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(View v) {
                                                                                    final String finalGettransano = finalGetsalestransactionno;
                                                                                    final String financialyearcode = finalGetfinanicialyear;
                                                                                    try {

                                                                                        /*printData = new PrintData(context);
                                                                                        deviceFound = printData.findBT();

                                                                                        if (!deviceFound) {
                                                                                            Toast toast = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                                                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                                                                            printpopup.dismiss();
                                                                                            toast.show();
                                                                                        } else {

                                                                                *//*boolean billPrinted = false;
                                                                                billPrinted = (boolean) printData.GetSalesReceipt(finalGettransano, financialyearcode);
                                                                                Intent i = new Intent(context, SalesActivity.class);
                                                                                startActivity(i);
                                                                                if (!billPrinted) {
                                                                                    Toast toast = Toast.makeText(getApplicationContext(), "Unable to connect to Bluetooth Printer!", Toast.LENGTH_LONG);
                                                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                                                    printpopup.dismiss();
                                                                                    toast.show();
                                                                                }
                                                                                printpopup.dismiss();*//*

                                                                                        }*/

                                                                                        if(mBluetoothAdapter != null) {

                                                                                            if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                                                                                                if(!preferenceMangr.pref_getString("SelectedPrinterAddress").equals("")) {
                                                                                                    try{
                                                                                                        new AsyncPrintSalesDetails().execute(finalGettransano, financialyearcode);
                                                                                                        printpopup.dismiss();
                                                                                                    }catch(Exception e){
                                                                                                        Log.d("AsyncMethod error : ",e.getLocalizedMessage());
                                                                                                        DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                                                                                        mDbErrHelper.open();
                                                                                                        String geterrror = e.toString();
                                                                                                        mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName()+
                                                                                                                " - Call AsyncPrintSalesDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                                                                                        mDbErrHelper.close();
                                                                                                    }
                                                                                                }else{
                                                                                                    Toast toast = Toast.makeText(getApplicationContext(), "Please select the bluetooth printer in app", Toast.LENGTH_LONG);
                                                                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                                                                    toast.show();
                                                                                                    printpopup.dismiss();
                                                                                                }
                                                                                            }else{
                                                                                                Toast toast = Toast.makeText(getApplicationContext(), "Please turn on the bluetooth", Toast.LENGTH_LONG);
                                                                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                                                                toast.show();
                                                                                                printpopup.dismiss();
                                                                                            }
                                                                                        }else{
                                                                                            Toast toast = Toast.makeText(getApplicationContext(), "Bluetooth is not available.", Toast.LENGTH_LONG);
                                                                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                                                                            toast.show();
                                                                                            printpopup.dismiss();
                                                                                        }
                                                                                    }
                                                                                    catch (Exception e){
                                                                                        Toast toast = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                                                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                                                                        toast.show();
                                                                                        printpopup.dismiss();
                                                                                        DataBaseAdapter mDbErrHelper2 = new DataBaseAdapter(context);
                                                                                        mDbErrHelper2.open();
                                                                                        mDbErrHelper2.insertErrorLog(e.toString(), this.getClass().getSimpleName()
                                                                                                +" -  check bluetooth adapter", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                                                                        mDbErrHelper2.close();

                                                                                    }
                                                                                }
                                                                            });

                                                                            txtNoAction.setOnClickListener(new View.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(View v) {
                                                                                    printpopup.dismiss();
                                                                                    Intent i = new Intent(context, SalesActivity.class);
                                                                                    startActivity(i);
                                                                                }
                                                                            });
                                                                            printpopup.setCanceledOnTouchOutside(false);
                                                                            printpopup.setCancelable(false);
                                                                            printpopup.show();
                                                                        }else{
                                                                            Intent i = new Intent(context, SalesActivity.class);
                                                                            startActivity(i);
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
                                                        });
                                                        dialogstatus.show();
                                                    }else{
                                                        Intent i = new Intent(context, SalesActivity.class);
                                                        startActivity(i);
                                                    }
                                                }else {
                                                    Intent i = new Intent(context, SalesActivity.class);
                                                    startActivity(i);
                                                }
                                            }
                                            else {
                                                Intent i = new Intent(context, SalesActivity.class);
                                                startActivity(i);
                                            }
//                                            }



                                        } else {
                                            Toast toast = Toast.makeText(getApplicationContext(),"Sales voucher settings not available for this van", Toast.LENGTH_LONG);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
                                            //Toast.makeText(getApplicationContext(), "Sales voucher settings not available for this van", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }else{
                                        Toast toast = Toast.makeText(getApplicationContext(),"Stock not available for this van", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                        //Toast.makeText(getApplicationContext(), "Stock not available for this van", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                } else {
                                    Toast toast = Toast.makeText(getApplicationContext(),"No Schedule for today", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    //Toast.makeText(getApplicationContext(), "No Schedule for today ", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                            } catch (Exception e) {
                                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                mDbErrHelper.open();
                                String geterrror = e.toString();
                                mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName()+
                                        " -Deleting cart items and print receipt", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                mDbErrHelper.close();
                            } finally {
                                objdatabaseadapter.close();
                                if(getschedulelist1!=null)
                                    getschedulelist1.close();
                            }

                        }else {
                            Toast toast = Toast.makeText(getApplicationContext(),"Sales voucher settings not available for this van", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            // Toast.makeText(getApplicationContext(), "Sales voucher settings not available for this van ", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }catch (Exception e){
                    DataBaseAdapter mDbErrHelper2 = new DataBaseAdapter(context);
                    mDbErrHelper2.open();
                    mDbErrHelper2.insertErrorLog("Sales List Addsales : "+e.toString(),
                            this.getClass().getSimpleName() + " - addsales Click event ", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper2.close();
                }



            }
        });

        //Set Now Date
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        getsaleslistdate = preferenceMangr.pref_getString("getformatdate");
        salesdate.setText(preferenceMangr.pref_getString("getcurrentdatetime"));


        closesales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataBaseAdapter objdatabaseadapter = null;
                objdatabaseadapter = new DataBaseAdapter(context);
                objdatabaseadapter.open();

                String billtypecode="0";
                String billcopystatus="";
                String cashpaidstatus="";
                String getsalestransactionno="0";
                String getfinanicialyear="0";
                String getbookingno = "";
                String getbillno = "";
                Cursor getschedulelist1=null;

                issalesclose=false;

                String getbillcopystatus = objdatabaseadapter.GetBillCopyDB();
                if (getbillcopystatus.equals("yes")){

                    Cursor Cur = objdatabaseadapter.CheckPaymentVoucher();
                    if(Cur.getCount()>0) {
                        billtypecode = Cur.getString(0);
                        billcopystatus = Cur.getString(1);
                        cashpaidstatus = Cur.getString(2);
                        getsalestransactionno = Cur.getString(3);
                        getfinanicialyear = Cur.getString(4);
                        getbookingno = Cur.getString(5);
                        getbillno = Cur.getString(6);

                        if (cashpaidstatus.equals("")){

                            //Open Payment Voucher
                            dialogstatus = new Dialog(context);
                            dialogstatus.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialogstatus.setContentView(R.layout.salesreceipt);
                            dialogstatus.setCanceledOnTouchOutside(false);
                            final TextView paymentbookingno = (TextView)dialogstatus.findViewById(R.id.paymentbookingno);
                            final TextView paymentbillno = (TextView)dialogstatus.findViewById(R.id.paymentbillno);
                            final CheckBox checkbillcopy = (CheckBox) dialogstatus.findViewById(R.id.checkbillcopy);
                            final RadioButton radio_paid = (RadioButton) dialogstatus.findViewById(R.id.radio_paid);
                            RadioButton radio_notpaid = (RadioButton) dialogstatus.findViewById(R.id.radio_notpaid);
                            Button btnsalessubmit = (Button) dialogstatus.findViewById(R.id.btnsalessubmit);
                            ImageView closepopup = (ImageView) dialogstatus.findViewById(R.id.closepopup);

                            paymentbookingno.setText("BK.NO. "+getbookingno);
                            paymentbillno.setText("Bill No. "+getbillno);
                            closepopup.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    issalesclose=false;
                                    dialogstatus.dismiss();
                                }
                            });
                            if (billtypecode.equals("2")) {
                                radio_paid.setChecked(false);
                                radio_notpaid.setChecked(true);
                                radio_paid.setEnabled(false);
                                radio_notpaid.setEnabled(false);
                                checkbillcopy.setChecked(true);
                            } else {
                                radio_paid.setChecked(true);
                                radio_notpaid.setChecked(false);
                                radio_paid.setEnabled(true);
                                radio_notpaid.setEnabled(true);
                                checkbillcopy.setChecked(false);
                            }
                            DataBaseAdapter finalObjdatabaseadapter1=null;
                            Cursor getsalesreceipt =null;
                            try {
                                finalObjdatabaseadapter1 = new DataBaseAdapter(context);
                                finalObjdatabaseadapter1.open();
                                getsalesreceipt = finalObjdatabaseadapter1.GetSalesReceiptDB(getsalestransactionno, getfinanicialyear);
                                if (getsalesreceipt.getCount() > 0) {
                                    if (getsalesreceipt.getString(0).equals("yes")) {
                                        checkbillcopy.setChecked(true);
                                    } else {
                                        checkbillcopy.setChecked(false);
                                    }
                                    if (getsalesreceipt.getString(1).equals("yes")) {
                                        radio_paid.setChecked(true);
                                        radio_notpaid.setChecked(false);
                                    } else {
                                        radio_paid.setChecked(true);
                                        radio_notpaid.setChecked(false);
                                    }
                                }
                            }catch (Exception e) {
                                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                mDbErrHelper.open();
                                String geterrror = e.toString();
                                mDbErrHelper.insertErrorLog(geterrror.replace("'", " "),
                                        this.getClass().getSimpleName() + " - GetSalesReceiptDB for close sales", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                mDbErrHelper.close();
                            } finally {
                                if (finalObjdatabaseadapter1 != null)
                                    finalObjdatabaseadapter1.close();
                                if(getsalesreceipt!=null)
                                    getsalesreceipt.close();
                            }
                            closepopup.setVisibility(View.GONE);
                            final DataBaseAdapter finalObjdatabaseadapter = new DataBaseAdapter(context);
                            final String finalGetsalestransactionno = getsalestransactionno;
                            final String finalGetfinanicialyear = getfinanicialyear;
                            btnsalessubmit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String getbillcopy = "";
                                    String getpaymentstatus = "";
                                    if (checkbillcopy.isChecked()) {
                                        getbillcopy = "yes";
                                    } else {
                                        getbillcopy = "no";
                                    }
                                    if (radio_paid.isChecked()) {
                                        getpaymentstatus = "yes";
                                    } else {
                                        getpaymentstatus = "no";
                                    }
                                    try {
                                        finalObjdatabaseadapter.open();
                                        String getresult = finalObjdatabaseadapter.UpdateSalesListReceipt(finalGetsalestransactionno,
                                                finalGetfinanicialyear, getbillcopy, getpaymentstatus);

                                        if (getresult.equals("success")) {
                                            issalesclose=true;
                                            dialogstatus.dismiss();
                                            Toast toast = Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_LONG);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();



                                            if (getpaymentstatus.equals("yes")) {
                                                /*android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(context);
                                                String message = "Are you sure you want to print?";
                                                final String finalGettransano = finalGetsalestransactionno;
                                                final String financialyearcode = finalGetfinanicialyear;
                                                builder1.setMessage(message)
                                                        .setIcon(context.getApplicationInfo().icon)
                                                        .setCancelable(false)
                                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog1, int which) {
                                                                dialog1.dismiss();
                                                                try {
                                                                    printData = new PrintData(context);
                                                                    deviceFound = printData.findBT();
                                                                    if (!deviceFound) {
                                                                        issalesclose=true;
                                                                        SalesClose();
                                                                        Toast toast = Toast.makeText(getApplicationContext(),"Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                                                        toast.show();
                                                                        //Toast.makeText(context, "Please connect to the Bluetooth Printer!", Toast.LENGTH_SHORT).show();
                                                                    } else {
                                                                        boolean billPrinted = false;
                                                                        billPrinted = (boolean) printData.GetSalesReceipt(finalGettransano, financialyearcode);
                                                                        issalesclose=true;
                                                                        SalesClose();
                                                                        if (!billPrinted) {
                                                                            Toast toast = Toast.makeText(getApplicationContext(),"Unable to connect to Bluetooth Printer!", Toast.LENGTH_LONG);
                                                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                                                            toast.show();

                                                                        }
                                                                    }
                                                                } catch (Exception e) {
                                                                    issalesclose=true;
                                                                    SalesClose();
                                                                    Toast toast = Toast.makeText(getApplicationContext(),"Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                                    toast.show();
                                                                    // Toast.makeText(context, "Please connect to the Bluetooth Printer!", Toast.LENGTH_SHORT).show();
                                                                    DataBaseAdapter mDbErrHelper2 = new DataBaseAdapter(context);
                                                                    mDbErrHelper2.open();
                                                                    mDbErrHelper2.insertErrorLog(e.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                                                    mDbErrHelper2.close();

                                                                }
                                                            }
                                                        })
                                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog1, int which) {
                                                                issalesclose=true;
                                                                SalesClose();
                                                                dialog1.cancel();
                                                            }
                                                        }).show();*/

                                                printpopup = new Dialog(context);
                                                printpopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                printpopup.setContentView(R.layout.printpopup);

                                                TextView txtYesAction = (TextView) printpopup.findViewById(R.id.txtYesAction);
                                                TextView txtNoAction = (TextView) printpopup.findViewById(R.id.txtNoAction);
                                                txtYesAction.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        final String finalGettransano = finalGetsalestransactionno;
                                                        final String financialyearcode = finalGetfinanicialyear;
                                                        try {

                                                            /*printData = new PrintData(context);
                                                            deviceFound = printData.findBT();

                                                            if (!deviceFound) {
                                                                issalesclose=true;
                                                                SalesClose();
                                                                Toast toast = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                                printpopup.dismiss();
                                                                toast.show();
                                                            } else {

                                                               *//* boolean billPrinted = false;
                                                                billPrinted = (boolean) printData.GetSalesReceipt(finalGettransano, financialyearcode);
                                                                issalesclose=true;
                                                                SalesClose();
                                                                if (!billPrinted) {
                                                                    Toast toast = Toast.makeText(getApplicationContext(), "Unable to connect to Bluetooth Printer!", Toast.LENGTH_LONG);
                                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                                    printpopup.dismiss();
                                                                    toast.show();
                                                                }
                                                                printpopup.dismiss();*//*

                                                            }*/

                                                            if(mBluetoothAdapter != null) {

                                                                if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                                                                    if(!preferenceMangr.pref_getString("SelectedPrinterAddress").equals("")) {
                                                                        new AsyncPrintSalesDetails().execute(finalGettransano, financialyearcode);
                                                                        printpopup.dismiss();
                                                                    }else{
                                                                        Toast toast = Toast.makeText(getApplicationContext(), "Please select the bluetooth printer in app", Toast.LENGTH_LONG);
                                                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                                                        toast.show();
                                                                        printpopup.dismiss();
                                                                    }
                                                                }else{
                                                                    Toast toast = Toast.makeText(getApplicationContext(), "Please turn on the bluetooth", Toast.LENGTH_LONG);
                                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                                    toast.show();
                                                                    printpopup.dismiss();
                                                                }
                                                            }else{
                                                                Toast toast = Toast.makeText(getApplicationContext(), "Bluetooth is not available.", Toast.LENGTH_LONG);
                                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                                toast.show();
                                                                printpopup.dismiss();
                                                            }
                                                        }
                                                        catch (Exception e){
                                                            Toast toast = Toast.makeText(getApplicationContext(),
                                                                    "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                                            toast.show();
                                                            issalesclose=true;
                                                            SalesClose();
                                                            printpopup.dismiss();
                                                            DataBaseAdapter mDbErrHelper2 = new DataBaseAdapter(context);
                                                            mDbErrHelper2.open();
                                                            mDbErrHelper2.insertErrorLog(e.toString(),
                                                                    this.getClass().getSimpleName() + " - check printer for UpdateSalesListReceipt", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                                            mDbErrHelper2.close();

                                                        }
                                                    }
                                                });

                                                txtNoAction.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        issalesclose=true;
                                                        SalesClose();
                                                        printpopup.dismiss();
                                                    }
                                                });
                                                printpopup.setCanceledOnTouchOutside(false);
                                                printpopup.setCancelable(false);
                                                printpopup.show();
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
                                                this.getClass().getSimpleName() + " - UpdateSalesListReceipt", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                        mDbErrHelper.close();
                                    } finally {
                                        if (finalObjdatabaseadapter != null)
                                            finalObjdatabaseadapter.close();
                                    }

                                }
                            });
                            dialogstatus.show();
                        }else{
                            issalesclose=true;
                            SalesClose();
                        }
                    } else{
                        issalesclose=true;
                        SalesClose();
                    }
                }else{
                    issalesclose=true;
                    SalesClose();
                }
                objdatabaseadapter.close();


            }
        });




        salesdate.setOnClickListener(new View.OnClickListener() {
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
                        getsaleslistdate = year+ "-"+getmonth+"-"+getdate;
                        salesdate.setText(vardate );
                        GetSalesList();
                        calculateeinvoicebill();

                    }
                };
                DatePickerDialog dpDialog = new DatePickerDialog(context, listener, year, month, day);
                dpDialog.show();
            }
        });

        //Swipe menu editior functionality
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create Edit Bill copy item
                SwipeMenuItem cancelItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item width
                cancelItem.setWidth(130);
                // set a icon
                cancelItem.setIcon(R.drawable.ic_mode_edit);
                // add to menu
                menu.addMenuItem(cancelItem);

                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                //  openItem.setBackground(ContextCompat.getDrawable(context,R.drawable.noborder));
                openItem.setWidth(130);
                openItem.setIcon(R.drawable.ic_view);
                menu.addMenuItem(openItem);

                SwipeMenuItem downloadItem = new SwipeMenuItem(
                        getApplicationContext());
                //  openItem.setBackground(ContextCompat.getDrawable(context,R.drawable.noborder));
                downloadItem.setWidth(130);
                downloadItem.setIcon(R.drawable.ic_invoice_download);
                menu.addMenuItem(downloadItem);

            }
        };

        saleslistView.setMenuCreator(creator);
        SwipeMenuCreator creatorBasedType = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // Create different menus depending on the view type
                ArrayList<SalesListDetails> currentListData = getdata;
                Integer viewtype = 0;
                int syncstatus=currentListData.get(menu.getViewType()).getSyncstatus();
                if(Utilities.isNullOrEmpty(currentListData.get(menu.getViewType()).getGstinumber())
                        && (!currentListData.get(menu.getViewType()).getFlag().equals("3")
                        && !currentListData.get(menu.getViewType()).getFlag().equals("6")) )
                    viewtype=1;
                else if(!Utilities.isNullOrEmpty(currentListData.get(menu.getViewType()).getGstinumber())
                        && (!currentListData.get(menu.getViewType()).getFlag().equals("3")
                        && !currentListData.get(menu.getViewType()).getFlag().equals("6"))) {
                     if(syncstatus==0){
                         viewtype=1;
                     }else {
                         viewtype=2;

                         String filePath = Utilities.getPDFLocalFilePath(context,currentListData.get(menu.getViewType()).getVoucherdate(),
                                 currentListData.get(menu.getViewType()).getTransactionno(),
                                 currentListData.get(menu.getViewType()).getBookingno(),
                                 currentListData.get(menu.getViewType()).getFinancialyearcode(),
                                 currentListData.get(menu.getViewType()).getCompanycode(),
                                 currentListData.get(menu.getViewType()).getBillcode());
                         if((!Utilities.isNullOrEmpty(filePath))){
                             viewtype=3;
                         }
                     }


                }else if(currentListData.get(menu.getViewType()).getFlag().equals("3") ||
                        currentListData.get(menu.getViewType()).getFlag().equals("6") ){
                    viewtype=4;
                }else{

                }
                switch (viewtype) {

                    case 1:
                        addSwipeMenuItems(menu,Constants.KEY_MENU_ITEM_VIEW);
                        addSwipeMenuItems(menu,Constants.KEY_MENU_ITEM_EDIT);

                        break;
                    case 2:
                        addSwipeMenuItems(menu,Constants.KEY_MENU_ITEM_VIEW);
                        addSwipeMenuItems(menu,Constants.KEY_MENU_ITEM_EDIT);
                        addSwipeMenuItems(menu,Constants.KEY_MENU_ITEM_UPLOAD);
                        break;

                    case 3:
                        addSwipeMenuItems(menu,Constants.KEY_MENU_ITEM_VIEW);
                        addSwipeMenuItems(menu,Constants.KEY_MENU_ITEM_EDIT);
                        addSwipeMenuItems(menu,Constants.KEY_MENU_ITEM_DOWNLOAD);
                        addSwipeMenuItems(menu,Constants.KEY_MENU_ITEM_SHARE);
                        break;
                    case 4:
                        addSwipeMenuItems(menu,Constants.KEY_MENU_ITEM_VIEW);
                        break;
                }
            }

        };

        saleslistView.setMenuCreator(creatorBasedType);

        //Click swipemenu action ...Edit sales receipt and then print
        saleslistView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {

                    case 0:
                        ArrayList<SalesListDetails> currentListDatareview = getdata;
                        getsalesreviewtransactionno = currentListDatareview.get(position).getTransactionno();
                        getsalesreviewfinanicialyear = currentListDatareview.get(position).getFinancialyearcode();
                        getsalesreviewcompanycode = currentListDatareview.get(position).getCompanycode();
                        getstaticflag = currentListDatareview.get(position).getFlag();
                        gesalesreviewbookingno=currentListDatareview.get(position).getBookingno();
                        getsalesreviewtbilldate=currentListDatareview.get(position).getVoucherdate();
                        final String listschedulecode = currentListDatareview.get(position).getSchedulecode();
                        String listsalescount = "";
                        //Get Current date
                        DataBaseAdapter objdatabaseadapter = null;
                        try{
                            objdatabaseadapter = new DataBaseAdapter(context);
                            objdatabaseadapter.open();
                            listsalescount = objdatabaseadapter.GetSalesClose(listschedulecode);
                        }catch (Exception e){
                            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                            mDbErrHelper.open();
                            String geterrror = e.toString();
                            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "),
                                    this.getClass().getSimpleName() +" - View bill details", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            mDbErrHelper.close();
                        }finally {
                            // this gets called even if there is an exception somewhere above
                            if (objdatabaseadapter != null)
                                objdatabaseadapter.close();
                        }
                        if(listsalescount.equals("") || listsalescount.equals("0") ||
                                listsalescount.equals("null") ||listsalescount.equals(null)){
                            getcancelflag="false";
                        }else{
                            getcancelflag="true";
                        }


                        //Call saels view page
                        Intent i = new Intent(context,SalesViewActivity.class);
                        startActivity(i);

                        break;
                    case 1:
                        ArrayList<SalesListDetails> currentListData = getdata;
                        final String getsalestransactionno = currentListData.get(position).getTransactionno();
                        final String getfinanicialyear = currentListData.get(position).getFinancialyearcode();
                        final String getflag = currentListData.get(position).getFlag();
                        final String getbilltype = currentListData.get(position).getPaymenttype();
                        final String listschedulecode1 = currentListData.get(position).getSchedulecode();
                        String listsalescount1 = "";
                        String listcashcount = "";
                        //Get Current date
                        DataBaseAdapter objdatabaseadapter1 = null;
                        try{
                            objdatabaseadapter1 = new DataBaseAdapter(context);
                            objdatabaseadapter1.open();
                            listsalescount1 = objdatabaseadapter1.GetSalesClose(listschedulecode1);
                            listcashcount = objdatabaseadapter1.GetCountCashClose(listschedulecode1);
                        }catch (Exception e){
                            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                            mDbErrHelper.open();
                            String geterrror = e.toString();
                            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "),
                                    this.getClass().getSimpleName()+" - GetSalesClose and GetCountCashClose", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            mDbErrHelper.close();
                        }finally {
                            // this gets called even if there is an exception somewhere above
                            if (objdatabaseadapter1 != null)
                                objdatabaseadapter1.close();
                        }
                        String getpaymentflag = "";
                        if(listsalescount1.equals("") || listsalescount1.equals("0") ||
                                listsalescount1.equals("null") ||listsalescount1.equals(null)){
                            getpaymentflag="false";
                        }else{
                            getpaymentflag="true";
                        }

                        String getcloseflag = "";
                        if(listcashcount.equals("") || listcashcount.equals("0") ||
                                listcashcount.equals("null") ||listcashcount.equals(null)){
                            getcloseflag="false";
                        }else{
                            getcloseflag="true";
                        }
                        if(getpaymentflag.equals("true") && getcloseflag.equals("true")){
                            Toast toast = Toast.makeText(getApplicationContext(),"Schedule Closed", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            break ;
                        }


                        if(!preferenceMangr.pref_getString("getcashclosecount").equals("0") && !preferenceMangr.pref_getString("getcashclosecount").equals("null") &&
                                !preferenceMangr.pref_getString("getcashclosecount").equals("") && !preferenceMangr.pref_getString("getcashclosecount").equals(null)  ){
                            Toast toast = Toast.makeText(getApplicationContext(),"Cash Closed ", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            //Toast.makeText(getApplicationContext(), "Cash Closed ", Toast.LENGTH_SHORT).show();
                            break;
                        }

                        final DataBaseAdapter finalObjdatabaseadapter = new DataBaseAdapter(context);
                        finalObjdatabaseadapter.open();
                        Cursor Cur = finalObjdatabaseadapter.SalesListPaymentVoucher(getsalestransactionno,getfinanicialyear);
                        String getbookingno = "";
                        String getbillno = "";
                        if(Cur.getCount()>0) {
                            getbookingno = Cur.getString(5);
                            getbillno = Cur.getString(6);
                        }if(Cur != null){
                        Cur.close();
                    }

                        // if (getbilltype.equals("1")){
                        if (!getflag.equals("3") && !getflag.equals("6")) {
                            dialogstatus = new Dialog(context);
                            dialogstatus.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialogstatus.setContentView(R.layout.salesreceipt);
                            dialogstatus.setCanceledOnTouchOutside(false);
                            final CheckBox checkbillcopy = (CheckBox) dialogstatus.findViewById(R.id.checkbillcopy);
                            final RadioButton radio_paid = (RadioButton) dialogstatus.findViewById(R.id.radio_paid);
                            RadioButton radio_notpaid = (RadioButton) dialogstatus.findViewById(R.id.radio_notpaid);
                            Button btnsalessubmit = (Button) dialogstatus.findViewById(R.id.btnsalessubmit);
                            ImageView closepopup = (ImageView) dialogstatus.findViewById(R.id.closepopup);

                            final TextView paymentbookingno = (TextView)dialogstatus.findViewById(R.id.paymentbookingno);
                            final TextView paymentbillno = (TextView)dialogstatus.findViewById(R.id.paymentbillno);

                            paymentbookingno.setText("BK.NO. "+getbookingno);
                            paymentbillno.setText("Bill No. "+getbillno);
                            closepopup.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialogstatus.dismiss();
                                }
                            });

                            if (getbilltype.equals("2")) {
                                radio_paid.setChecked(false);
                                radio_notpaid.setChecked(true);
                                radio_paid.setEnabled(false);
                                radio_notpaid.setEnabled(false);
                                checkbillcopy.setChecked(true);
                                checkbillcopy.setEnabled(false);
                            } else {
                                radio_paid.setChecked(true);
                                radio_notpaid.setChecked(false);
                                radio_paid.setEnabled(true);
                                radio_notpaid.setEnabled(true);
                                checkbillcopy.setChecked(false);
                                checkbillcopy.setEnabled(true);
                            }


                            Cursor getsalesreceipt = finalObjdatabaseadapter.GetSalesReceiptDB(getsalestransactionno, getfinanicialyear);
                            if (getsalesreceipt.getCount() > 0) {
                                if (getsalesreceipt.getString(0).equals("yes")) {
                                    checkbillcopy.setChecked(true);
                                } else {
                                    checkbillcopy.setChecked(false);
                                }
                                if (getsalesreceipt.getString(1).equals("yes")) {
                                    radio_paid.setChecked(true);
                                    radio_notpaid.setChecked(false);
                                } else {
                                    radio_paid.setChecked(false);
                                    radio_notpaid.setChecked(true);
                                }
                            }
                            btnsalessubmit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String getbillcopy = "";
                                    String getpaymentstatus = "";
                                    if (checkbillcopy.isChecked()) {
                                        getbillcopy = "yes";
                                    } else {
                                        getbillcopy = "no";
                                    }
                                    if (radio_paid.isChecked()) {
                                        getpaymentstatus = "yes";
                                    } else {
                                        getpaymentstatus = "no";
                                    }
                                    try {
                                        String getresult = finalObjdatabaseadapter.UpdateSalesListReceipt(getsalestransactionno, getfinanicialyear, getbillcopy, getpaymentstatus);

                                        if (getresult.equals("success")) {
                                            dialogstatus.dismiss();
                                            Toast toast = Toast.makeText(getApplicationContext(),"Updated Successfully", Toast.LENGTH_LONG);
                                            toast.setGravity(Gravity.BOTTOM, 0, 150);
                                            toast.show();
                                            //Toast.makeText(getApplicationContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                                        }
                                        if(getpaymentstatus.equals("yes")){
                                                /*android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(context);
                                                String message = "Are you sure you want to print?";
                                                final String finalGettransano = getsalestransactionno;
                                                final  String financialyearcode = getfinanicialyear;
                                                builder1.setMessage(message)
                                                        .setIcon(context.getApplicationInfo().icon)
                                                        .setCancelable(false)
                                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog1, int which) {
                                                                dialog1.dismiss();
                                                                try {
                                                                    printData = new PrintData(context);
                                                                    deviceFound = printData.findBT();
                                                                    if (!deviceFound) {
                                                                        Toast toast = Toast.makeText(getApplicationContext(),"Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                                                        toast.show();
                                                                        //Toast.makeText(context, "Please connect to the Bluetooth Printer!", Toast.LENGTH_SHORT).show();
                                                                    } else {
                                                                        boolean billPrinted = false;
                                                                        billPrinted = (boolean) printData.GetSalesReceipt(finalGettransano,financialyearcode);
                                                                        if (!billPrinted) {
                                                                            Toast toast = Toast.makeText(getApplicationContext(),"Unable to connect to Bluetooth Printer!", Toast.LENGTH_LONG);
                                                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                                                            toast.show();
                                                                           // Toast.makeText(context, "Unable to connect to Bluetooth Printer!", Toast.LENGTH_SHORT).show();
                                                                            return;
                                                                        }
                                                                    }
                                                                }
                                                                catch (Exception e){
                                                                    Toast toast = Toast.makeText(getApplicationContext(),"Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                                    toast.show();
                                                                   // Toast.makeText(context, "Please connect to the Bluetooth Printer!", Toast.LENGTH_SHORT).show();
                                                                    DataBaseAdapter mDbErrHelper2 = new DataBaseAdapter(context);
                                                                    mDbErrHelper2.open();
                                                                    mDbErrHelper2.insertErrorLog(e.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                                                    mDbErrHelper2.close();

                                                                }
                                                            }
                                                        })
                                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog1, int which) {
                                                                dialog1.cancel();;
                                                            }
                                                        }).show();*/

                                            printpopup = new Dialog(context);
                                            printpopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                            printpopup.setContentView(R.layout.printpopup);

                                            TextView txtYesAction = (TextView) printpopup.findViewById(R.id.txtYesAction);
                                            TextView txtNoAction = (TextView) printpopup.findViewById(R.id.txtNoAction);
                                            txtYesAction.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    final String finalGettransano = getsalestransactionno;
                                                    final  String financialyearcode = getfinanicialyear;
                                                    try {

                                                        printData = new PrintData(context);
                                                        deviceFound = printData.findBT();

                                                            /*if (!deviceFound) {
                                                                Toast toast = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                                printpopup.dismiss();
                                                                toast.show();
                                                            } else {

                                                                *//*boolean billPrinted = false;
                                                                billPrinted = (boolean) printData.GetSalesReceipt(finalGettransano,financialyearcode);
                                                                if (!billPrinted) {
                                                                    Toast toast = Toast.makeText(getApplicationContext(), "Unable to connect to Bluetooth Printer!", Toast.LENGTH_LONG);
                                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                                    printpopup.dismiss();
                                                                    toast.show();
                                                                }
                                                                printpopup.dismiss();*//*

                                                            }*/

                                                        if(mBluetoothAdapter != null) {

                                                            if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                                                                if(!preferenceMangr.pref_getString("SelectedPrinterAddress").equals("")) {
                                                                    new AsyncPrintSalesDetails().execute(finalGettransano,financialyearcode);
                                                                    printpopup.dismiss();
                                                                }else{
                                                                    Toast toast = Toast.makeText(getApplicationContext(), "Please select the bluetooth printer in app", Toast.LENGTH_LONG);
                                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                                    toast.show();
                                                                    printpopup.dismiss();
                                                                }
                                                            }else{
                                                                Toast toast = Toast.makeText(getApplicationContext(), "Please turn on the bluetooth", Toast.LENGTH_LONG);
                                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                                toast.show();
                                                                printpopup.dismiss();
                                                            }
                                                        }else{
                                                            Toast toast = Toast.makeText(getApplicationContext(), "Bluetooth is not available.", Toast.LENGTH_LONG);
                                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                                            toast.show();
                                                            printpopup.dismiss();
                                                        }
                                                    }
                                                    catch (Exception e){
                                                        Toast toast = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                                        toast.show();
                                                        printpopup.dismiss();
                                                        DataBaseAdapter mDbErrHelper2 = new DataBaseAdapter(context);
                                                        mDbErrHelper2.open();
                                                        mDbErrHelper2.insertErrorLog(e.toString(), this.getClass().getSimpleName()
                                                                +" - Chek bluetooth printer for list payment", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                                        mDbErrHelper2.close();

                                                    }
                                                }
                                            });

                                            txtNoAction.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    printpopup.dismiss();
                                                }
                                            });
                                            printpopup.setCanceledOnTouchOutside(false);
                                            printpopup.setCancelable(false);
                                            printpopup.show();
                                        }
                                        //GetSale List
                                        GetSalesList();
                                        networkstate = isNetworkAvailable();
                                        if (networkstate == true) {
                                            new AsyncUpdateSalesReceiptDetails().execute();
                                        }

                                    } catch (Exception e) {
                                        DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                        mDbErrHelper.open();
                                        String geterrror = e.toString();
                                        mDbErrHelper.insertErrorLog(geterrror.replace("'", " "),
                                                this.getClass().getSimpleName() + " - UpdateSalesListReceipt in list ", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                        mDbErrHelper.close();
                                    } finally {
                                        if (finalObjdatabaseadapter != null)
                                            finalObjdatabaseadapter.close();
                                    }

                                }
                            });
                            dialogstatus.show();
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(),"Cancelled bill can't be edited", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            //Toast.makeText(getApplicationContext(), "Cancelled bill can't be edited", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        /*}else{
                            Toast toast = Toast.makeText(getApplicationContext(),"Credit bill can't be edited", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            //Toast.makeText(getApplicationContext(), "Credit bill can't be edited", Toast.LENGTH_SHORT).show();
                            break;
                        }*/
                        break;

                    // This case 2 and case 3 for e-invoice
                    case 2:
                        ArrayList<SalesListDetails> currentListDataDownload = getdata;
                        final String listschedulecode2 = currentListDataDownload.get(position).getSchedulecode();
                        String listsalescount2 = "";
                        String listcashcount2 = "";
                        String getsalesperson = "";
                        String einvoiceurlpath="";
                        int syncstatus=currentListDataDownload.get(position).getSyncstatus();
                        if(syncstatus==1) {
                            //Get Current date
                            DataBaseAdapter objdatabaseadapter2 = null;
                            try{
                                objdatabaseadapter2 = new DataBaseAdapter(context);
                                objdatabaseadapter2.open();
                                listsalescount2 = objdatabaseadapter2.GetSalesClose(listschedulecode2);
                                listcashcount2 = objdatabaseadapter2.GetCountCashClose(listschedulecode2);
                                getsalesperson=objdatabaseadapter2.GetSalesPersonname(listschedulecode2);
                                einvoiceurlpath = objdatabaseadapter2.GetBillInVoiceURL(currentListDataDownload.get(position).getVoucherdate(),
                                        currentListDataDownload.get(position).getTransactionno(),
                                        currentListDataDownload.get(position).getBookingno(),
                                        currentListDataDownload.get(position).getFinancialyearcode(),
                                        currentListDataDownload.get(position).getCompanycode(),
                                        currentListDataDownload.get(position).getBillcode());
                            }catch (Exception e){
                                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                mDbErrHelper.open();
                                String geterrror = e.toString();
                                mDbErrHelper.insertErrorLog(geterrror.replace("'", " "),
                                        this.getClass().getSimpleName() +
                                                " - GetSalesClose and GetCountCashClose for generate einvoice ", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                mDbErrHelper.close();
                            }finally {
                                // this gets called even if there is an exception somewhere above
                                if (objdatabaseadapter2 != null)
                                    objdatabaseadapter2.close();
                            }
                            String getcloseflag1 = "";
                            if(listcashcount2.equals("") || listcashcount2.equals("0") ||
                                    listcashcount2.equals("null") ||listcashcount2.equals(null)){
                                getcloseflag1="false";
                            }else{
                                getcloseflag1="true";
                            }

                            if( getcloseflag1.equals("true") && Utilities.isNullOrEmpty(einvoiceurlpath)){
                                Toast toast = Toast.makeText(getApplicationContext(),"Schedule Closed", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                break ;
                            }
                            if(!preferenceMangr.pref_getString("getcashclosecount").equals("0") && !preferenceMangr.pref_getString("getcashclosecount").equals("null") &&
                                    !preferenceMangr.pref_getString("getcashclosecount").equals("") && !preferenceMangr.pref_getString("getcashclosecount").equals(null)
                                    && Utilities.isNullOrEmpty(einvoiceurlpath)  ){
                                Toast toast = Toast.makeText(getApplicationContext(),"Cash Closed ", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                //Toast.makeText(getApplicationContext(), "Cash Closed ", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            if(getsalesperson!=null){
                                getsalesperson=getsalesperson;
                            }else{
                                getsalesperson="";
                            }
                            String vanname = preferenceMangr.pref_getString("getvanname");;
                            vanname =vanname.replace(" ","");
                            networkstate = isNetworkAvailable();
                            if (networkstate == true) {
                                downloadData = new ArrayList<>();
                                downloadData.add(Constants.KEY_INDEX_0,currentListDataDownload.get(position).getVoucherdate());
                                downloadData.add(Constants.KEY_INDEX_1,currentListDataDownload.get(position).getTransactionno());
                                downloadData.add(Constants.KEY_INDEX_2,currentListDataDownload.get(position).getBookingno());
                                downloadData.add(Constants.KEY_INDEX_3,currentListDataDownload.get(position).getFinancialyearcode());
                                downloadData.add(Constants.KEY_INDEX_4,currentListDataDownload.get(position).getCompanycode());
                                downloadData.add(Constants.KEY_INDEX_5,currentListDataDownload.get(position).getBillcode().replace("/","_"));
                                downloadData.add(Constants.KEY_INDEX_6,vanname+"/"+getsalesperson);

                                getCustomerMobileNumber(currentListDataDownload.get(position).getRetailercode());
                                if(currentListDataDownload.get(position).getFlag().equals("6") ||
                                        currentListDataDownload.get(position).getFlag().equals("3") ){
                                    Toast.makeText(context, "Cancelled bill can't be able to generate the e-Invoice", Toast.LENGTH_SHORT).show();

                                    break;
                                }
                                if(!Utilities.isNullOrEmpty(currentListDataDownload.get(position).getGstinumber()))
                                    showTheConformationPopup(downloadData);
                                else
                                    Toast.makeText(context, "Unable to download the e-invoice. Because this bill doesn't have a GST Number", Toast.LENGTH_SHORT).show();

                                // new Asynccheckcanceleinvoicefordownload().execute(new Object[]{currentListDataDownload.get(position), Constants.DOWNLOAD_UPLOAD_Einvoice});
                            }else{
                                Toast toast = Toast.makeText(getApplicationContext(),"Please check internet connection", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        }
                       break;

                    case 3:
                        ArrayList<SalesListDetails> shareCurrentBillInvoicePDF = getdata;
//                        networkstate = isNetworkAvailable();
//                        if (networkstate == true) {
                        DataBaseAdapter objdbadapter1 = null;
                        String filePath="";
                        try{
                            objdbadapter1 = new DataBaseAdapter(context);
                            objdbadapter1.open();

                            filePath = objdbadapter1.GetBillInVoicePDFLocalURL(shareCurrentBillInvoicePDF.get(position).getVoucherdate(),
                                    shareCurrentBillInvoicePDF.get(position).getTransactionno(),
                                    shareCurrentBillInvoicePDF.get(position).getBookingno(),
                                    shareCurrentBillInvoicePDF.get(position).getFinancialyearcode(),
                                    shareCurrentBillInvoicePDF.get(position).getCompanycode(),
                                    shareCurrentBillInvoicePDF.get(position).getBillcode());

                            getCustomerMobileNumber(shareCurrentBillInvoicePDF.get(position).getRetailercode());

                        }catch (Exception e){
                            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                            mDbErrHelper.open();
                            String geterrror = e.toString();
                            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "),
                                    this.getClass().getSimpleName()+" - share e-Invoice pdf", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            mDbErrHelper.close();
                        }finally {
                            // this gets called even if there is an exception somewhere above
                            if (objdbadapter1 != null)
                                objdbadapter1.close();
                        }

                        if(Utilities.isNullOrEmpty(filePath)){
                            Toast.makeText(context,"Please download the PDF file and then try again to share",Toast.LENGTH_SHORT).show();
                            break;
                        }
                        String filenamearr[] = filePath.split("/");
                        String  FileName=filenamearr[filenamearr.length-1];

                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                            Utilities.SharePDFToAll_Above_Android9(SalesListActivity.this, context,FileName);
                        } else {
                            Utilities.SharePDFToAll_Below_Android9(SalesListActivity.this,FileName,filePath);
                        }
                        //    showShareOptionPopup(filePath);
                        // new Asynccheckcanceleinvoicefordownload().execute(new Object[]{shareCurrentBillInvoicePDF.get(position), Constants.Share_Einvoice});

                        // new Asynccheckcanceleinvoiceforshare().execute(shareCurrentBillInvoicePDF.get(position));
//                        }

                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });

        //Logout process
        saleslogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // HomeActivity.logoutprocess = "True";
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirmation");
                builder.setIcon(R.mipmap.ic_vanlauncher);
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
        //Goback process
        saleslistgoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack(null);
            }
        });

      /*  //GetSale List
        GetSalesList();*/

        //company name
        txtcompanyname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetCompanyName();
            }
        });

        lveinvoicepending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geteinvoicepending="1";
                geteinvoicegenerated="0";
                gettotaleinvoicebills="0";
                einvocie_pending.setBackgroundColor(context.getResources().getColor(R.color.lightyellow));
                total_einvoice.setBackgroundColor(context.getResources().getColor(R.color.white));
                einvoice_generated.setBackgroundColor(context.getResources().getColor(R.color.white));
                total_bill.setBackgroundColor(context.getResources().getColor(R.color.white));

                GetSalesList();
            }
        });
        lveinvoicegenerated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geteinvoicegenerated="1";
                geteinvoicepending="0";
                gettotaleinvoicebills="0";
                einvocie_pending.setBackgroundColor(context.getResources().getColor(R.color.white));
                total_einvoice.setBackgroundColor(context.getResources().getColor(R.color.white));
                einvoice_generated.setBackgroundColor(context.getResources().getColor(R.color.lightyellow));
                total_bill.setBackgroundColor(context.getResources().getColor(R.color.white));
                GetSalesList();
            }
        });
        lvtotaleinvoicebills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gettotaleinvoicebills="1";
                geteinvoicegenerated="0";
                geteinvoicepending="0";
                einvocie_pending.setBackgroundColor(context.getResources().getColor(R.color.white));
                total_einvoice.setBackgroundColor(context.getResources().getColor(R.color.lightyellow));
                einvoice_generated.setBackgroundColor(context.getResources().getColor(R.color.white));
                total_bill.setBackgroundColor(context.getResources().getColor(R.color.white));

                GetSalesList();
            }
        });
        lvtotalbills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geteinvoicepending="0";
                geteinvoicegenerated="0";
                gettotaleinvoicebills="0";
                einvocie_pending.setBackgroundColor(context.getResources().getColor(R.color.white));
                total_einvoice.setBackgroundColor(context.getResources().getColor(R.color.white));
                einvoice_generated.setBackgroundColor(context.getResources().getColor(R.color.white));
                total_bill.setBackgroundColor(context.getResources().getColor(R.color.lightyellow));
                GetSalesList();
            }
        });

        selectpaymenttype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                int index = parentView.getSelectedItemPosition();
                getpaymenttype = arrapaymenttype[index];
                GetSalesList();
                if(!isloadeinvoice)
                    calculateeinvoicebill();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        selectpaymentstatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                int index = parentView.getSelectedItemPosition();
                getpaymentstatus = arrpaymentstatus[index];
                if(issales) {
                    GetSalesList();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

    }

    /**********Asynchronous Claass***************/
    protected  class AsyncCustomerDetails extends
            AsyncTask<String, JSONObject, ArrayList<CustomerDatas>> {
        ArrayList<CustomerDatas> List = null;
        JSONObject jsonObj = null;
        ProgressDialog loading;
        @Override
        protected  ArrayList<CustomerDatas> doInBackground(String... params) {
            RestAPI api = new RestAPI();
            DataBaseAdapter dbadapter = new DataBaseAdapter(context);
            dbadapter.open();
            try{
                // customer
                jsonObj = api.GetCustomerDetails(preferenceMangr.pref_getString("deviceid"),preferenceMangr.pref_getString("getroutecode"),"synccustomer.php");
                if (isSuccessful(jsonObj)) {
                    dbadapter.synccustomer(jsonObj);
                    api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "customer", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                }
            }catch (Exception e){
                Log.d("Customer", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "),
                        this.getClass().getSimpleName() + "Sales List Customer Sync", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }

            try {
                JSONObject js_obj = new JSONObject();
                try {
                    /*DataBaseAdapter dbadapter = new DataBaseAdapter(context);*/
                    dbadapter.open();
                    Cursor mCur2 = dbadapter.GetWholeCustomerDatasDB();
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
                }
                catch (Exception e)
                {
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "),
                            this.getClass().getSimpleName()+" - GetWholeCustomerDatasDB api", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("Customer", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() +
                        " - json declare for GetWholeCustomerDatasDB", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            return List;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(context, "Loading", "Please wait...", true);
            loading.setCancelable(false);
            loading.setCanceledOnTouchOutside(false);
        }

        @Override
        protected void onPostExecute(ArrayList<CustomerDatas> result) {
            // TODO Auto-generated method stub
            try {
                if (result.size() >= 1) {
                    if (result.get(0).CustomerCode.length > 0) {
                        for (int j = 0; j < result.get(0).CustomerCode.length; j++) {
                            DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
                            dataBaseAdapter.open();
                            dataBaseAdapter.UpdateCustomerFlag(result.get(0).CustomerCode[j]);
                            dataBaseAdapter.close();
                        }
                    }

                }
                loading.dismiss();
            }catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncScheduleDetails", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "),
                        this.getClass().getSimpleName()+ " - AsyncCustomerDetails post execute", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }

        }
    }

    protected  class Asynccheckcanceleinvoicefordownload extends
            AsyncTask< Object[], JSONObject,String> {
        ArrayList<CustomerDatas> List = null;
        JSONObject jsonObj = null;
        ProgressDialog loading;

        SalesListDetails currentListDataDownload=null;
        String option="";
        @Override
        protected  String doInBackground(Object[]... arrayLists) {
            RestAPI api = new RestAPI();
            DataBaseAdapter dbadapter = new DataBaseAdapter(context);
            dbadapter.open();
            ProgressDialog loading;
            String einvoice_status="";

            Object[] data = arrayLists[0];
            currentListDataDownload = (SalesListDetails) data[0];
            option=(String) data[1];

            try{

                JSONObject jsonObj = api.getsalescancelandeinvoice(currentListDataDownload.getVoucherdate(), currentListDataDownload.getTransactionno(),
                        currentListDataDownload.getBookingno(), currentListDataDownload.getFinancialyearcode(),
                        currentListDataDownload.getCompanycode(),
                        preferenceMangr.pref_getString("deviceid"),"synccanceleinvoicesales.php" );
                if(jsonObj!=null) {
                    JSONArray json_category = null;
                    json_category = jsonObj.getJSONArray("Value");
                    JSONObject obj = (JSONObject) json_category.get(0);
                    String einvoicestatus=obj.getString("einvoicestatus");

                    if(einvoicestatus.equals("einvoicecancelled")){
                        dbadapter.syncsalescancelandeinvoice(jsonObj);
                        einvoice_status= "1";
                    }


                }


            }catch (Exception e){

                Log.d("Customer", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + "Sales List Customer Sync", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }finally {
                if(dbadapter!=null){
                    dbadapter.close();
                }
            }
            return einvoice_status;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(context, "Loading", "Please wait we are checking with server", true);
            loading.setCancelable(false);
            loading.setCanceledOnTouchOutside(false);
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            loading.dismiss();
            if(result.equals("1")){
                //  GetSalesList();
                Toast toast = Toast.makeText(getApplicationContext(), "This e-Invoice has been cancelled",Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            switch (option){

                case Constants.DOWNLOAD_UPLOAD_Einvoice:
                    downloadData = new ArrayList<>();
                    downloadData.add(Constants.KEY_INDEX_0,currentListDataDownload.getVoucherdate());
                    downloadData.add(Constants.KEY_INDEX_1,currentListDataDownload.getTransactionno());
                    downloadData.add(Constants.KEY_INDEX_2,currentListDataDownload.getBookingno());
                    downloadData.add(Constants.KEY_INDEX_3,currentListDataDownload.getFinancialyearcode());
                    downloadData.add(Constants.KEY_INDEX_4,currentListDataDownload.getCompanycode());
                    downloadData.add(Constants.KEY_INDEX_5,currentListDataDownload.getBillcode().replace("/","_"));

                    getCustomerMobileNumber(currentListDataDownload.getRetailercode());
                    if(currentListDataDownload.getFlag().equals("6") ||
                            currentListDataDownload.getFlag().equals("3") ){
                        Toast.makeText(context, "Cancelled bill can't be able to generate the e-Invoice", Toast.LENGTH_SHORT).show();

                        return;
                    }
                    if(!Utilities.isNullOrEmpty(currentListDataDownload.getGstinumber()))
                        showTheConformationPopup(downloadData);
                    else
                        Toast.makeText(context, "Unable to download the e-invoice. Because this bill doesn't have a GST Number", Toast.LENGTH_SHORT).show();

                    break;
                case Constants.Share_Einvoice:
                    DataBaseAdapter objdbadapter1 = null;
                    String filePath="";
                    try{
                        objdbadapter1 = new DataBaseAdapter(context);
                        objdbadapter1.open();

                        filePath = objdbadapter1.GetBillInVoicePDFLocalURL(currentListDataDownload.getVoucherdate(),
                                currentListDataDownload.getTransactionno(),
                                currentListDataDownload.getBookingno(),
                                currentListDataDownload.getFinancialyearcode(),
                                currentListDataDownload.getCompanycode(),
                                currentListDataDownload.getBillcode());

                        getCustomerMobileNumber(currentListDataDownload.getRetailercode());

                    }catch (Exception e){
                        DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                        mDbErrHelper.open();
                        String geterrror = e.toString();
                        mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        mDbErrHelper.close();
                    }finally {
                        // this gets called even if there is an exception somewhere above
                        if (objdbadapter1 != null)
                            objdbadapter1.close();
                    }
                    if(Utilities.isNullOrEmpty(filePath)){
                        Toast.makeText(context,"Please download the PDF file and then try again to share",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                        Utilities.SharePDFToAll_Above_Android9(SalesListActivity.this, context,downloadFileName);
                    } else {
                        Utilities.SharePDFToAll_Below_Android9(SalesListActivity.this,downloadFileName,filePath);
                    }
//                  showShareOptionPopup(filePath);
                    break;

            }



        }
    }
    //    protected  class Asynccheckcanceleinvoiceforshare extends
//            AsyncTask<SalesListDetails, JSONObject,String> {
//        ArrayList<CustomerDatas> List = null;
//        JSONObject jsonObj = null;
//        ProgressDialog loading;
//
//        SalesListDetails shareCurrentBillInvoicePDF=null;
//        @Override
//        protected  String doInBackground(SalesListDetails... arrayLists) {
//            RestAPI api = new RestAPI();
//            DataBaseAdapter dbadapter = new DataBaseAdapter(context);
//            dbadapter.open();
//            ProgressDialog loading;
//            String einvoice_status="";
//            shareCurrentBillInvoicePDF = arrayLists[0];
//            try{
//
//
//                JSONObject jsonObj = api.getsalescancelandeinvoice(shareCurrentBillInvoicePDF.getVoucherdate(), shareCurrentBillInvoicePDF.getTransactionno(),
//                        shareCurrentBillInvoicePDF.getBookingno(), shareCurrentBillInvoicePDF.getFinancialyearcode(),
//                        shareCurrentBillInvoicePDF.getCompanycode(),
//                        preferenceMangr.pref_getString("deviceid"),"synccanceleinvoicesales.php" );
//                if(jsonObj!=null) {
//                    JSONArray json_category = null;
//                    json_category = jsonObj.getJSONArray("Value");
//                    JSONObject obj = (JSONObject) json_category.get(0);
//                    String einvoicestatus=obj.getString("einvoicestatus");
//
//                    if(einvoicestatus.equals("einvoicecancelled")){
//                        dbadapter.syncsalescancelandeinvoice(jsonObj);
//
//                        einvoice_status= "1";
//                    }
//
//
//                }
//
//
//            }catch (Exception e){
//
//                Log.d("Customer", e.getMessage());
//                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
//                mDbErrHelper.open();
//                String geterrror = e.toString();
//                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + "Sales List Customer Sync", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
//                mDbErrHelper.close();
//            }finally {
//                if(dbadapter!=null){
//                    dbadapter.close();
//                }
//            }
//            return einvoice_status;
//        }
//
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            loading = ProgressDialog.show(context, "Loading", "Please wait we are checking with server", true);
//            loading.setCancelable(false);
//            loading.setCanceledOnTouchOutside(false);
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            // TODO Auto-generated method stub
//            loading.dismiss();
//            if(result.equals("1")){
//                GetSalesList();
//                Toast toast = Toast.makeText(getApplicationContext(), "This e-Invoice has been cancelled",Toast.LENGTH_LONG);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();
//                return;
//            }
//
//
//
//        }
//    }
    protected  class AsyncSalesDetails extends
            AsyncTask<String, JSONObject, ArrayList<SalesSyncDatas>> {
        ArrayList<SalesSyncDatas> List = null;
        JSONObject jsonObj = null;
        ProgressDialog loading;
        @Override
        protected  ArrayList<SalesSyncDatas> doInBackground(String... params) {
            RestAPI api = new RestAPI();
            String result = "";
            try {
                JSONObject js_obj = new JSONObject();
                JSONObject js_salesobj = new JSONObject();
                JSONObject js_salesitemobj = new JSONObject();
                JSONObject js_stockobj = new JSONObject();
                DataBaseAdapter dbadapter = null;
                Cursor mCursales = null;
                Cursor mCursalesitems = null;
                Cursor mCurStock = null;
                try {
                    int count=0,salesitemscount=0,stockcount=0;
                    dbadapter = new DataBaseAdapter(context);
                    dbadapter.open();

                    mCursales = dbadapter.GetWholeSalesDatasDB(getsaleslistdate);
                    mCursalesitems = dbadapter.GetWholeSalesItemDatasDB(getsaleslistdate);
                    mCurStock = dbadapter.GetSalesStockTransactionDatasforPDDB(getsaleslistdate);
                    JSONArray js_array2 = new JSONArray();
                    JSONArray js_array3 = new JSONArray();
                    JSONArray js_array4 = new JSONArray();
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
                        js_array2.put(obj);
                        mCursales.moveToNext();
                    }
                    if (mCursales != null && !mCursales.isClosed()){
                        count = mCursales.getCount();
                        mCursales.close();

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
                        js_array3.put(obj);
                        mCursalesitems.moveToNext();
                    }

                    if (mCursalesitems != null && !mCursalesitems.isClosed()){
                        salesitemscount = mCursalesitems.getCount();
                        mCursalesitems.close();

                    }
                    js_obj.put("JSonObject", js_array4);
                    js_salesobj.put("JSonObject", js_array2);
                    js_salesitemobj.put("JSonObject", js_array3);
                    js_stockobj.put("JSonObject",js_stockarray);

                    jsonObj =  api.allSalesDetails(js_salesobj.toString(),js_salesitemobj.toString(),context);
                    //Call Json parser functionality
                    JSONParser parser = new JSONParser();
                    //parse the json object to boolean
                    List = parser.parseSalesDataList(jsonObj);
//                    dbadapter.close();
                }
                catch (Exception e)
                {
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName()
                            + " - AsyncSalesDetails ", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                }finally {
                    if (dbadapter != null)
                        dbadapter.close();
                    if (mCurStock != null)
                        mCurStock.close();
                    if (mCursales != null)
                        mCursales.close();
                    if (mCursalesitems != null)
                        mCursalesitems.close();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncSalesDetails", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "),
                        this.getClass().getSimpleName() + " - AsyncSalesDetails json object declare", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            return List;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(context, "Loading", "Please wait...", true);
            loading.setCancelable(false);
            loading.setCanceledOnTouchOutside(false);
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
                            objdatabaseadapter.UpdateSalesFlag(List.get(0).TransactionNo[j]);
                        }
                    }
                    if (List.get(0).SalesItemTransactionNo.length > 0) {
                        for (int j = 0; j < List.get(0).SalesItemTransactionNo.length; j++) {
                            objdatabaseadapter.UpdateSalesItemFlag(List.get(0).SalesItemTransactionNo[j]);
                        }
                    }
//                    if (List.get(0).StockTransactionNo.length > 0) {
//                        for (int j = 0; j < List.get(0).StockTransactionNo.length; j++) {
//                            String[] getArr = List.get(0).StockTransactionNo[j].split("~");
//                            objdatabaseadapter.UpdateSalesStockTransactionFlag(getArr[0],getArr[1]);
//                        }
//                    }
                }
                GetSalesList();

            }catch (Exception e) {
                // TODO Auto-generated catch block
                loading.dismiss();
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
            loading.dismiss();

        }
    }


    //cancel sales
    protected  class AsyncSalesStockConversionDetails extends
            AsyncTask<String, JSONObject, ArrayList<SalesSyncDatas>> {
        ArrayList<SalesSyncDatas> List = null;
        JSONObject jsonObj = null;
        ProgressDialog loading;
        @Override
        protected  ArrayList<SalesSyncDatas> doInBackground(String... params) {
            RestAPI api = new RestAPI();
            try {
                JSONObject js_salesobj = new JSONObject();
                JSONObject js_salesitemobj = new JSONObject();
                JSONObject js_stockobj = new JSONObject();
                DataBaseAdapter dbadapter = null;
                Cursor mCurStock = null;
                try {
                    int count=0;
                    dbadapter = new DataBaseAdapter(context);
                    dbadapter.open();
                    mCurStock = dbadapter.GetWholeStockConversionDatasDB(getsaleslistdate);
                    JSONArray js_array2 = new JSONArray();
                    JSONArray js_array3 = new JSONArray();
                    JSONArray js_stockarray = new JSONArray();
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
                    if (mCurStock != null && !mCurStock.isClosed()){
                        count = mCurStock.getCount();
                        mCurStock.close();

                    }
                    js_salesobj.put("JSonObject", js_array2);
                    js_salesitemobj.put("JSonObject", js_array3);
                    js_stockobj.put("JSonObject",js_stockarray);

                    jsonObj =  api.allSalesStockConversionDetails(  js_stockobj.toString(),context);
                    //Call Json parser functionality
                   /* JSONParser parser = new JSONParser();
                    //parse the json object to boolean
                    List = parser.parseSalesCancelDataList(jsonObj);*/
//                    dbadapter.close();
                }
                catch (Exception e)
                {
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "),
                            this.getClass().getSimpleName() + " - AsyncSalesStockConversionDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                }finally {
                    if (dbadapter != null)
                        dbadapter.close();
                    if (mCurStock != null)
                        mCurStock.close();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncSalesDetails", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "),
                        this.getClass().getSimpleName() + " - AsyncSalesStockConversionDetails JSON declare", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            return List;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(context, "Loading", "Please wait...", true);
            loading.setCancelable(false);
            loading.setCanceledOnTouchOutside(false);
        }

        @Override
        protected void onPostExecute(ArrayList<SalesSyncDatas> List) {
            // TODO Auto-generated method stub
            loading.dismiss(); //Backupdb
            DataBaseAdapter mDbHelper1 = new DataBaseAdapter(context);
            mDbHelper1.open();
            String filepath = mDbHelper1.udfnBackupdb(context);
            mDbHelper1.close();
        }
    }


    //cancel sales
    protected  class AsyncSalesCancelDetails extends
            AsyncTask<String, JSONObject, ArrayList<SalesSyncDatas>> {
        ArrayList<SalesSyncDatas> List = null;
        JSONObject jsonObj = null;
        ProgressDialog loading;
        @Override
        protected  ArrayList<SalesSyncDatas> doInBackground(String... params) {
            RestAPI api = new RestAPI();
            try {
                JSONObject js_salesobj = new JSONObject();
                JSONObject js_salesitemobj = new JSONObject();
                JSONObject js_stockobj = new JSONObject();
                DataBaseAdapter dbadapter = null;
                Cursor mCursales = null;
                Cursor mCurStock = null;

                try {
                    dbadapter = new DataBaseAdapter(context);
                    dbadapter.open();
                    int count=0,stockcount=0;
                    mCursales = dbadapter.GetWholeSalesCancelDatasDB();
                    // Cursor mCursalesitems = dbadapter.GetWholeSalesCancelItemDatasDB();
                    mCurStock = dbadapter.GetWholeCancelStockTransactionDatasDB(getsaleslistdate);
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
                    if (mCurStock != null && !mCurStock.isClosed()){
                        stockcount = mCurStock.getCount();
                        mCurStock.close();

                    }
                    js_salesobj.put("JSonObject", js_array2);
                    js_salesitemobj.put("JSonObject", js_array3);
                    js_stockobj.put("JSonObject",js_stockarray);

                    jsonObj =  api.allSalesCancelDetails(js_salesobj.toString(), js_stockobj.toString(),context);
                    //Call Json parser functionality
                   /* JSONParser parser = new JSONParser();
                    //parse the json object to boolean
                    List = parser.parseSalesCancelDataList(jsonObj);*/
//                    dbadapter.close();
                }
                catch (Exception e)
                {
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "),
                            this.getClass().getSimpleName() + " - AsyncSalesCancelDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                }finally {
                    if (dbadapter != null)
                        dbadapter.close();
                    if (mCurStock != null)
                        mCurStock.close();
                    if (mCursales != null)
                        mCursales.close();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncSalesDetails", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "),
                        this.getClass().getSimpleName()  + " - AsyncSalesCancelDetails JSON Declare", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            return List;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(context, "Loading", "Please wait...", true);
            loading.setCancelable(false);
            loading.setCanceledOnTouchOutside(false);
        }

        @Override
        protected void onPostExecute(ArrayList<SalesSyncDatas> List) {
            // TODO Auto-generated method stub
            loading.dismiss();
           /* DataBaseAdapter objdatabaseadapter = new DataBaseAdapter(context);
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
            }*/

        }
    }


    protected  class AsyncNilStockDetails extends
            AsyncTask<String, JSONObject, ArrayList<ScheduleDatas>> {
        ArrayList<ScheduleDatas> List = null;
        JSONObject jsonObj = null;
        ProgressDialog loading;
        @Override
        protected  ArrayList<ScheduleDatas> doInBackground(String... params) {
            RestAPI api = new RestAPI();
            try {
                JSONObject js_obj = new JSONObject();
                DataBaseAdapter dbadapter = null;
                Cursor mCur2 = null;
                try {
                    int count=0;
                    dbadapter = new DataBaseAdapter(context);
                    dbadapter.open();
                    mCur2 = dbadapter.GetWholeNilStockDetailsDB();

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
                    if (mCur2 != null && !mCur2.isClosed()){
                        count = mCur2.getCount();
                        mCur2.close();

                    }
                    js_obj.put("JSonObject", js_array2);

                    jsonObj =  api.NilStockDetails(js_obj.toString(),context);
                    //Call Json parser functionality
                    JSONParser parser = new JSONParser();
                    //parse the json object to boolean
                    List = parser.parseNilStockReport(jsonObj);
                    //dbadapter.close();
                }
                catch (Exception e)
                {
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "),
                            this.getClass().getSimpleName()+ " - AsyncNilStockDetails ", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                }finally{
                    if(dbadapter!=null)
                        dbadapter.close();
                    if (mCur2 != null )
                        mCur2.close();
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
            loading = ProgressDialog.show(context, "Loading", "Please wait...", true);
            loading.setCancelable(false);
            loading.setCanceledOnTouchOutside(false);
        }

        @Override
        protected void onPostExecute(ArrayList<ScheduleDatas> result) {
            // TODO Auto-generated method stub
            DataBaseAdapter dataBaseAdapter = null;
            try {
                if (result.size() >= 1) {
                    if (result.get(0).ScheduleCode.length > 0) {
                        for (int j = 0; j < result.get(0).ScheduleCode.length; j++) {
                            dataBaseAdapter = new DataBaseAdapter(context);
                            dataBaseAdapter.open();
                            if (result.get(0).ScheduleCode[j] != "" && !result.get(0).ScheduleCode[j].equals("")) {
                                String getsplitval[] = result.get(0).ScheduleCode[j].split("~");
                                dataBaseAdapter.UpdateNilStockFlag(getsplitval[0], getsplitval[1]);
                            }
                            dataBaseAdapter.close();
                        }
                    }

                }
                loading.dismiss();
            }catch (Exception e) {
                // TODO Auto-generated catch block
                loading.dismiss();
                Log.d("AsyncSalesDetails", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "),
                        this.getClass().getSimpleName() + " - AsyncNilStockDetails JSON Declare", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            } finally {
                if(dataBaseAdapter!=null)
                    dataBaseAdapter.close();
            }

        }
    }

    /**********END Asynchronous Claass***************/


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            if(printpopup!=null){
                if(printpopup.isShowing()){
//                Toast.makeText(getApplicationContext(), "back press",
//                        Toast.LENGTH_LONG).show();
                    return true;
                }
            }

        }
        return super.onKeyDown(keyCode, event);
    }
    public void SalesClose(){
            String popupmessage="Do you want to close sales?";
        if(issalesclose) {
            DataBaseAdapter objdatabaseadapter = null;
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cursor Cur=null;
            String deviceid = preferenceMangr.pref_getString("deviceid");
            String result = "";
            try{
                GetFreeItemexcess();
                networkstate = isNetworkAvailable();
                if (networkstate == true) {
                    String getschedulecode = objdatabaseadapter.GetScheduleCode();
                    result=Utilities.getDeliveryNotePendingCount(deviceid,getschedulecode,context);
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Please check internet connection", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                String geteinvoicependingbills = objdatabaseadapter.Checkeinvoicepending();

                if(geteinvoicependingbills!=null && !geteinvoicependingbills.equals("0")){
                    popupmessage="You have "+geteinvoicependingbills+" GSTIN invoice(s) for which e-Invoice is " +
                            "not yet generated. \n Are you sure want to close sales?";
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
            if(!result.equals("0")){
                Utilities.CheckDeliveryNoteDialog(context);
            }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirmation");
                // builder.setIcon(R.mipmap.ic_vanlauncher);

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
                                        DataBaseAdapter finalObjdatabaseadapter1 = null;
                                        finalObjdatabaseadapter1 = new DataBaseAdapter(context);
                                        finalObjdatabaseadapter1.open();
                                        try {

                                            String getEncryptedPIN = null;
                                            try {
                                                getEncryptedPIN = sha256(pinview.getValue());
                                            } catch (NoSuchAlgorithmException e) {
                                                e.printStackTrace();
                                                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                                mDbErrHelper.open();
                                                String geterrror = e.toString();
                                                mDbErrHelper.insertErrorLog(geterrror.replace("'", " "),
                                                        this.getClass().getSimpleName() + " - getEncryptedPIN ", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                                mDbErrHelper.close();
                                            }
                                            if (getEncryptedPIN.equals(preferenceMangr.pref_getString("getpin"))) {

                                                String getresult = "0";
                                                getschedulecode = finalObjdatabaseadapter1.GetScheduleCode();
                                                //SAles close details
                                                getresult = finalObjdatabaseadapter1.InsertSalesClose(getschedulecode);
                                                if (getresult.equals("success")) {
                                                    pindialog.dismiss();
                                                    Toast toast = Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_LONG);
                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                    toast.show();
                                                    closeKeyboard();
                                                    //Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_SHORT).show();
                                                    networkstate = isNetworkAvailable();
                                                    if (networkstate == true) {
                                                        new AsyncCloseSalesDetails().execute();
                                                    }
                                                    Intent i = new Intent(context, OrderFormActivity.class);
                                                    startActivity(i);
                                                } else {
                                                    Toast toast = Toast.makeText(getApplicationContext(), "Error in saving", Toast.LENGTH_LONG);
                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                    toast.show();
                                                    //Toast.makeText(getApplicationContext(), "Error in saving", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast toast = Toast.makeText(getApplicationContext(), "Please enter correct pin", Toast.LENGTH_LONG);
                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                toast.show();
                                                // Toast.makeText(getApplicationContext(),"Please enter correct pin",Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        } catch (Exception e) {
                                            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                            mDbErrHelper.open();
                                            String geterrror = e.toString();
                                            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "),
                                                    this.getClass().getSimpleName() + " - Submit pin", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                            mDbErrHelper.close();
                                        } finally {
                                            if (finalObjdatabaseadapter1 != null)
                                                finalObjdatabaseadapter1.close();
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

    public  void GetFreeItemexcess(){
        DataBaseAdapter freeobjdatabaseadapter = null;
        Cursor Cur=null;
        try{
            freeobjdatabaseadapter = new DataBaseAdapter(context);
            freeobjdatabaseadapter.open();
            Cur = freeobjdatabaseadapter.getexcessfreeitemqty();
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
//                        return;


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
            if(freeobjdatabaseadapter != null)
                freeobjdatabaseadapter.close();
            if(Cur != null)
                Cur.close();
        }
    }
    /*******FILTER FUNCTIONALITY********/
    public  void GetCompanyName(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetCompanyDB();
            if(Cur.getCount()>0) {
                companycode = new String[Cur.getCount()];
                companyname = new String[Cur.getCount()];
                shortname = new String[Cur.getCount()];
                for(int i=0;i<Cur.getCount();i++){
                    companycode[i] = Cur.getString(0);
                    companyname[i] = Cur.getString(1);
                    shortname[i] = Cur.getString(2);
                    Cur.moveToNext();
                }

                companydialog = new Dialog(context);
                companydialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                companydialog.setContentView(R.layout.companypopup);
                lv_CompanyList = (ListView) companydialog.findViewById(R.id.lv_CompanyList);
                ImageView close = (ImageView) companydialog.findViewById(R.id.close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        companydialog.dismiss();
                    }
                });
                CompanyAdapter adapter = new CompanyAdapter(context);
                lv_CompanyList.setAdapter(adapter);
                companydialog.show();
            }else{
                Toast.makeText(getApplicationContext(),"No Area in this route",Toast.LENGTH_SHORT).show();
            }
        }  catch (Exception e){
            Log.i("GetArea", e.toString());
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "),
                    this.getClass().getSimpleName()+" - Get company", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
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
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "),
                    this.getClass().getSimpleName()+" - isNetworkAvailable", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
            result=false;

        }
        return result;
    }
    //Sales List
    public  void GetSalesList(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            saleslist.clear();
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetSalesListDB(getsaleslistdate,getfiltercompanycode,
                    getpaymenttype,getpaymentstatus,geteinvoicegenerated,geteinvoicepending,gettotaleinvoicebills);
            if(Cur.getCount()>0) {
                issales=true;
                for(int i=0;i<Cur.getCount();i++){
                    saleslist.add(new SalesListDetails(Cur.getString(3),Cur.getString(7),
                            Cur.getString(8),Cur.getString(20),
                            Cur.getString(21),Cur.getString(9),Cur.getString(14),
                            String.valueOf(Cur.getCount()-i),Cur.getString(11),Cur.getString(23),Cur.getString(19),
                            Cur.getString(22),Cur.getString(10),
                            Cur.getString(18),Cur.getString(24),Cur.getString(15)
                            ,Cur.getString(16),Cur.getString(2),Cur.getString(17),
                            Cur.getString(0),Cur.getInt(29)));
                    Cur.moveToNext();
                }
                getdata = saleslist;
                adapter = new SalesListBaseAdapterList(context,saleslist);
                saleslistView.setAdapter(adapter);
            }else{
                adapter = new SalesListBaseAdapterList(context,saleslist);
                saleslistView.setAdapter(adapter);
                //adapter.notifyDataSetChanged();
                SalesListActivity.totalamtval.setText("\u20B9 0.00");
                SalesListActivity.cashtotalamt.setText("\u20B9 0.00" );
                SalesListActivity.credittotalamt.setText("\u20B9 0.00" );
                Toast.makeText(getApplicationContext(),"No Sales Available",Toast.LENGTH_SHORT).show();

            }
        }  catch (Exception e){
            Log.i("SalesList", e.toString());
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "),
                    this.getClass().getSimpleName()+" - Getsales list", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
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



    /**********Asynchronous Claass***************/

    protected  class AsyncCloseSalesDetails extends
            AsyncTask<String, JSONObject, ArrayList<ScheduleDatas>> {
        ArrayList<ScheduleDatas> List = null;
        JSONObject jsonObj = null;
        @Override
        protected  ArrayList<ScheduleDatas> doInBackground(String... params) {
            RestAPI api = new RestAPI();
            try {
                JSONObject js_obj = new JSONObject();
                DataBaseAdapter dbadapter = null;
                Cursor mCur2 = null;
                try {
                    int count=0;
                    dbadapter = new DataBaseAdapter(context);
                    dbadapter.open();
                    mCur2 = dbadapter.GetCloseSalesitemDatasDB();

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
                    if (mCur2 != null && !mCur2.isClosed()){
                        count = mCur2.getCount();
                        mCur2.close();

                    }
                    js_obj.put("JSonObject", js_array2);

                    jsonObj =  api.SalesCloseDetails(js_obj.toString(),context);
                    //Call Json parser functionality
                    JSONParser parser = new JSONParser();
                    //parse the json object to boolean
                    List = parser.parseCashReport(jsonObj);
//                    dbadapter.close();
                }
                catch (Exception e)
                {
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "),
                            this.getClass().getSimpleName()+" - AsyncCloseSalesDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                }finally {
                    if(dbadapter!=null)
                        dbadapter.close();
                    if(mCur2 != null)
                        mCur2.close();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncScheduleDetails", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "),
                        this.getClass().getSimpleName()+" - AsyncCloseSalesDetails JSON Declare", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
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

    /**********END Asynchronous Claass***************/

    /************BASE ADAPTER*************/
    //Company Adapter
    public class CompanyAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;

        CompanyAdapter(Context c) {
            context = c;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return companycode.length;
        }

        @Override
        public Object getItem(int position) {
            return companycode[position];
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
                convertView = layoutInflater.inflate(R.layout.companypopuplist, parent, false);
                mHolder = new ViewHolder();
                try {
                    mHolder.listcompanyname = (TextView) convertView.findViewById(R.id.listcompanyname);
                } catch (Exception e) {
                    Log.i("Route", e.toString());
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "),
                            this.getClass().getSimpleName() + " - convertView CompanyAdapter ", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                }
                convertView.setTag(mHolder);
            } else {
                mHolder = (ViewHolder) convertView.getTag();
            }
            try {
                mHolder.listcompanyname.setText(String.valueOf(shortname[position]));
            } catch (Exception e) {
                Log.i("Route value", e.toString());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName()
                        +" - set  listcompanyname for mHolder ", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtcompanyname.setText(String.valueOf(shortname[position]));
                    getfiltercompanycode = companycode[position];
                    GetSalesList();
                    companydialog.dismiss();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView listcompanyname;

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
    /************END BASE ADAPTER*************/
    //Set date
    private void showmainDate(int year, int monthOfYear, int dayOfMonth) {
        String vardate = "";
        String getmonth="";
        String getdate="";

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
        getsaleslistdate = year +"-"+getmonth+"-"+getdate ;
        salesdate.setText(vardate);

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
    public void goBack(View v) {
        LoginActivity.ismenuopen=true;
        Intent i = new Intent(context, MenuActivity.class);
        startActivity(i);
    }
    @Override
    public void onBackPressed() {
        goBack(null);
    }

    protected class AsyncPrintSalesDetails extends
            AsyncTask<String, JSONObject, Boolean> {
        Boolean billPrinted = false;
        JSONObject jsonObj = null;
        ProgressDialog loading;
        @Override
        protected Boolean doInBackground(String... params) {
            final String finalGetsalestransano = params[0];
            final String financialyearcode = params[1];
            try {
                printData = new PrintData(context);
                deviceFound = printData.findBT();
                Log.d("deviceFound",String.valueOf(deviceFound));
                if (!deviceFound) {
                    /*Toast toast = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);*/
                    //printpopup.dismiss();
                    billPrinted = deviceFound;
                    //toast.show();
                } else {
                    billPrinted = (boolean) printData.GetSalesReceipt(finalGetsalestransano, financialyearcode,SalesListActivity.this);
                    //printpopup.dismiss();
                }
                Log.d("billPrinted",String.valueOf(billPrinted));
            }
            catch (Exception e){
                /*Toast toast = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();*/
                //printpopup.dismiss();
                printData = null;
                billPrinted = true;
                Log.d("aaaa", e.getLocalizedMessage());
                DataBaseAdapter mDbErrHelper2 = new DataBaseAdapter(context);
                mDbErrHelper2.open();
                mDbErrHelper2.insertErrorLog(e.toString(), this.getClass().getSimpleName()
                        +" - AsyncPrintSalesDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper2.close();
            }
            return billPrinted;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(context, "Connecting to printer", "Please wait", true);
            loading.setCancelable(false);
            loading.setCanceledOnTouchOutside(false);
        }
        @Override
        protected void onPostExecute(Boolean billPrinted) {
            // TODO Auto-generated method stub
            try {
                loading.dismiss();
                Log.d("billPrinted 1q",String.valueOf(billPrinted));
                if (!billPrinted) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Unable to connect Bluetooth Printer. Please check the printer is turn or or not!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    //printpopup.dismiss();
                    toast.show();
                    printData = null;

                }
            }catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncScheduleDetails", e.getLocalizedMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() +
                        " - AsyncPrintSalesDetails post execute", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        }
    }


    public void popup_salesclose(){
        String popupmessage="Do you want to close sales?";
        DataBaseAdapter objdatabaseadapter = null;
        objdatabaseadapter = new DataBaseAdapter(context);

        Cursor Cur=null;
        String deviceid = preferenceMangr.pref_getString("deviceid");
        String result = "";
        try{
            GetFreeItemexcess();

             networkstate = isNetworkAvailable();
            if (networkstate == true) {
                objdatabaseadapter.open();
                String getschedulecode = objdatabaseadapter.GetScheduleCode();
                result= Utilities.getDeliveryNotePendingCount(deviceid,getschedulecode,context);
            }else{
                  Toast toast = Toast.makeText(getApplicationContext(),"Please check internet connection", Toast.LENGTH_LONG);
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
        if(!result.equals("0")){
            Utilities.CheckDeliveryNoteDialog(context);
        }
        else {
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
                                            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                            mDbErrHelper.open();
                                            String geterrror = e.toString();
                                            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "),
                                                    this.getClass().getSimpleName() + " - Submitpin for popup_salesclose", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                            mDbErrHelper.close();
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
                                        mDbErrHelper.insertErrorLog(geterrror.replace("'", " "),
                                                this.getClass().getSimpleName() + " - btnSubmitpin click event", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
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

    protected class AsyncDownloadInvoicePdf extends
            AsyncTask<String, JSONObject,String> {
        JSONObject jsonObj = null;

        String billdate,transactionno,bookingno,financialyearcode,customercode,schedulecode;
        int code;
        @Override
        protected String doInBackground(String... params) {

            billdate = params[0]; transactionno = params[1];
            bookingno = params[2]; financialyearcode = params[3];
            customercode = params[4]; schedulecode = params[5];

            //downloadFileName = params[1];
            String result = "";
            try {
                String vanname = preferenceMangr.pref_getString("getvanname");
                vanname =vanname.replace(" ","");
                RestAPI api = new RestAPI();
                networkstate = isNetworkAvailable();
                if (networkstate == true) {

                    //jsonObj = api.DownloadInvoicePdf(billdate, transactionno, bookingno, financialyearcode, customercode,
                    //  preferenceMangr.pref_getString("getvancode"),"");
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

                /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                    downloadTaskForAndroid11(fileUrl,fileName);
                } else {
                    downloadTaskForAndroid6(fileUrl,fileName);
                }*/
                downloadLoading.dismiss();

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

    public void showTheConformationPopup(final ArrayList<Object> downloadData){
        pindialog = new Dialog(context);
        pindialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pindialog.setContentView(R.layout.validatepinnumber_einvoice);
        btnSubmitpin = (Button)pindialog.findViewById(R.id.btnSubmitpin);
        pinview = (Pinview)pindialog.findViewById(R.id.pinview);
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
                        DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                        mDbErrHelper.open();
                        String geterrror = e.toString();
                        mDbErrHelper.insertErrorLog(geterrror.replace("'", " "),
                                this.getClass().getSimpleName()+" - showTheConformationPopup submit pin", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        mDbErrHelper.close();
                    }
                    if (getEncryptedPIN.equals(preferenceMangr.pref_getString("getpin"))) {

                        /*Toast toast = Toast.makeText(getApplicationContext(), "Successfully",Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();*/
                        pindialog.dismiss();
                        closeKeyboard();
                        if(downloadData != null && downloadData.size() > 0){

                            String billdate = (String) downloadData.get(Constants.KEY_INDEX_0);
                            String transactionno = (String) downloadData.get(Constants.KEY_INDEX_1);
                            String bookingno = (String) downloadData.get(Constants.KEY_INDEX_2);
                            String financialyearcode = (String) downloadData.get(Constants.KEY_INDEX_3);
                            String companycode = (String) downloadData.get(Constants.KEY_INDEX_4);
                            String billno = (String) downloadData.get(Constants.KEY_INDEX_5);
                            String salesandvaname = (String) downloadData.get(Constants.KEY_INDEX_6);
                            //downloadData.add(Constants.KEY_INDEX_6,"http://"+preferenceMangr.pref_getString("ipaddress")+"/INVOICE_PDF/Einvoice.pdf");
                            downloadData.add(Constants.KEY_INDEX_7,"Einvoice_" + billdate + "_" + billno + ".pdf");

                            //new AsyncDownloadInvoicePdf().execute(billdate,transactionno,bookingno,financialyearcode,customercode,schedulecode);

                            if (Utilities.isNetworkAvailable(context)) {
                                downloadFileName = "Einvoice_" + billdate + "_" + billno + ".pdf";
                                AsyncTaskClass asyncTaskClass = new AsyncTaskClass(context,dropboxlistener,downloadData,Constants.DOWNLOAD_SALES_BILL_INVOICE);
                                asyncTaskClass.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            } else {
                                Toast toast1 = Toast.makeText(context,"Please check internet connection", Toast.LENGTH_LONG);
                                toast1.setGravity(Gravity.CENTER, 0, 0);
                                toast1.show();
                            }

                        }

                    }else{
                        Toast toast = Toast.makeText(getApplicationContext(), "Please enter correct pin",Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }
                } catch (Exception e) {
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'", " "),
                            this.getClass().getSimpleName()+" - showTheConformationPopup submit pin click event", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                } finally {
                    if(objdatabaseadapter!=null)
                        objdatabaseadapter.close();
                }
            }
        });
        pindialog.show();
    }
    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public void onReceiveAsyncResult(Object response, int requestType) {
        switch (requestType) {
            case Constants.DOWNLOAD_SALES_BILL_INVOICE:
                try{
                    if(response == null)
                        return;
                    downloadFileID = (long) response;
                    if(downloadFileID == 0)
                        return;
                    registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
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
                                Log.d("downloaded uriString : ",uriString);

                                String decodedPath = Uri.decode(uriString);
                                Log.d("downloaded decoded uriString : ",decodedPath);
                                //TODO : Use this local uri and launch intent to open file
                                try{

                                    Toast.makeText(context,"Successfully file Downloaded...!",Toast.LENGTH_SHORT).show();

                                    insertPDF_FilePath_InLocalDB(decodedPath);
                                    geteinvoicepending="0";
                                    geteinvoicegenerated="0";
                                    gettotaleinvoicebills="0";
                                    einvocie_pending.setBackgroundColor(context.getResources().getColor(R.color.white));
                                    total_einvoice.setBackgroundColor(context.getResources().getColor(R.color.white));
                                    einvoice_generated.setBackgroundColor(context.getResources().getColor(R.color.white));
                                    total_bill.setBackgroundColor(context.getResources().getColor(R.color.white));
                                    total_einvoice.setText("0");
                                    einvoice_generated.setText("0");
                                    einvocie_pending.setText("0");
                                    total_bill.setText("0");
                                    GetSalesList();

                                    calculateeinvoicebill();
//                                    showShareOptionPopup(decodedPath);
                                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                                        Utilities.SharePDFToAll_Above_Android9(SalesListActivity.this, context,downloadFileName);
                                    } else {
                                        Utilities.SharePDFToAll_Below_Android9(SalesListActivity.this,downloadFileName,decodedPath);
                                    }

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


    public void showShareOptionPopup(final String decodedPath){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Share to");
        builder.setIcon(R.mipmap.ic_vanlauncher);
        //builder.setMessage("Share to?")
        builder.setCancelable(false)
                .setPositiveButton("Whatsapp", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Uri filePathURI=null;
                        String sendType="";
                        if(!Utilities.isNullOrEmpty(custMobilenoForPDFShare)){
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                                File sourceFile =  new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator + Uri.decode(downloadFileName) );
                                filePathURI = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID , new File(sourceFile.getPath()));
                                Utilities.sharePDFToWhatsapp_Above_Android9(SalesListActivity.this, custMobilenoForPDFShare, filePathURI,context,downloadFileName);
                            }else{
                                filePathURI = Uri.parse(decodedPath);
                                Utilities.sharePDFToWhatsapp_Below_Android9(SalesListActivity.this, custMobilenoForPDFShare, filePathURI);
                            }

                            //openWhatsapp(SalesListActivity.this,"91"+"8124907589","Hello");

                        }

                        else
                            Toast.makeText(context,"This customer doesn't have a valid mobile number (" +custMobilenoForPDFShare + ")" ,Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Others", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                            Utilities.SharePDFToAll_Above_Android9(SalesListActivity.this, context,downloadFileName);
                        } else {
                            Utilities.SharePDFToAll_Below_Android9(SalesListActivity.this,downloadFileName,decodedPath);
                        }

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void insertPDF_FilePath_InLocalDB(String downloadedFilePath){
        DataBaseAdapter dataBaseAdapter = null;
        if(downloadData == null || downloadData.size() < 0)
            return;
        try{

            String billdate = (String) downloadData.get(Constants.KEY_INDEX_0);
            String transactionno = (String) downloadData.get(Constants.KEY_INDEX_1);
            String bookingno = (String) downloadData.get(Constants.KEY_INDEX_2);
            String financialyearcode = (String) downloadData.get(Constants.KEY_INDEX_3);
            String companycode = (String) downloadData.get(Constants.KEY_INDEX_4);
            String billno = (String) downloadData.get(Constants.KEY_INDEX_5);

            dataBaseAdapter = new DataBaseAdapter(context);
            dataBaseAdapter.open();
            dataBaseAdapter.insertPDFFile_LocalPath(billdate, transactionno, bookingno, financialyearcode, companycode, billno, downloadedFilePath);
            dataBaseAdapter.close();
        }catch (Exception e){
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "),
                    this.getClass().getSimpleName() + " - insert PDF_FilePath In LocalDB ", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }

    public void addSwipeMenuItems(SwipeMenu menu,String type){

        switch (type){

            case Constants.KEY_MENU_ITEM_VIEW:

                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                openItem.setWidth(130);
                openItem.setIcon(R.drawable.ic_view);
                menu.addMenuItem(openItem);
                break;

            case Constants.KEY_MENU_ITEM_EDIT:
                SwipeMenuItem cancelItem = new SwipeMenuItem(
                        getApplicationContext());
                cancelItem.setWidth(130);
                cancelItem.setIcon(R.drawable.ic_indian_rupee_symbol24);
                menu.addMenuItem(cancelItem);

                break;

            case Constants.KEY_MENU_ITEM_UPLOAD:
                SwipeMenuItem uploadItem = new SwipeMenuItem(
                        getApplicationContext());
                uploadItem.setWidth(130);
                uploadItem.setIcon(R.drawable.ic_baseline_cloud_upload_24);
                menu.addMenuItem(uploadItem);
                break;

            case Constants.KEY_MENU_ITEM_DOWNLOAD:
                SwipeMenuItem downloadItem1 = new SwipeMenuItem(
                        getApplicationContext());
                downloadItem1.setWidth(130);
                //ic_baseline_cloud_download_24
                downloadItem1.setIcon(R.drawable.ic_invoice_download);
                menu.addMenuItem(downloadItem1);
                break;

            case Constants.KEY_MENU_ITEM_SHARE:
                SwipeMenuItem shareItem = new SwipeMenuItem(
                        getApplicationContext());
                shareItem.setWidth(130);
                shareItem.setIcon(R.drawable.ic_share);
                menu.addMenuItem(shareItem);
                break;
        }
    }

    public void getCustomerMobileNumber(String customercode){
        DataBaseAdapter objdbadapter = null;

        try{
            objdbadapter = new DataBaseAdapter(context);
            objdbadapter.open();
            custMobilenoForPDFShare = objdbadapter.GetCustomerMobileNumber(customercode);
        }catch (Exception e){
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "),
                    this.getClass().getSimpleName() + " - get Customer MobileNumber", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }finally {
            // this gets called even if there is an exception somewhere above
            if (objdbadapter != null)
                objdbadapter.close();
        }
    }
    public void calculateeinvoicebill() {
        total_einvoice.setText("0");
        einvoice_generated.setText("0");
        einvocie_pending.setText("0");
        total_bill.setText("0");
        ArrayList<SalesListDetails> myList = getdata;
        Integer einvoicebill = 0;
        Integer einvoicegenerate = 0;
        Integer einvoicepending = 0;
        Integer totalbill = 0;
        try {
            if(myList!=null) {
                for (int i = 0; i < myList.size(); i++) {
                    if (!myList.get(i).getFlag().equals("3") && !myList.get(i).getFlag().equals("6")) {
                        totalbill = totalbill + 1;
                    }
                }

                for (int i = 0; i < myList.size(); i++) {
                    if (!myList.get(i).getFlag().equals("3") && !myList.get(i).getFlag().equals("6") &&
                            !Utilities.isNullOrEmpty(myList.get(i).getGstinumber())) {
                        einvoicebill = einvoicebill + 1;
                    }
                }

                for (int i = 0; i < myList.size(); i++) {


                    if (!myList.get(i).getFlag().equals("3") && !myList.get(i).getFlag().equals("6")
                            && !Utilities.isNullOrEmpty(myList.get(i).getGstinumber())) {
                        String einvoicefilePath = Utilities.getPDFLocalFilePath(context, myList.get(i).getVoucherdate(),
                                myList.get(i).getTransactionno(),
                                myList.get(i).getBookingno(),
                                myList.get(i).getFinancialyearcode(),
                                myList.get(i).getCompanycode(),
                                myList.get(i).getBillcode());
                        if (!Utilities.isNullOrEmpty(einvoicefilePath)) {
                            einvoicegenerate = einvoicegenerate + 1;
                        }

                    }
                }

                for (int i = 0; i < myList.size(); i++) {

                    if (!myList.get(i).getFlag().equals("3") && !myList.get(i).getFlag().equals("6")
                            && !Utilities.isNullOrEmpty(myList.get(i).getGstinumber())) {
                        String einvoicefilePath = Utilities.getPDFLocalFilePath(context, myList.get(i).getVoucherdate(),
                                myList.get(i).getTransactionno(),
                                myList.get(i).getBookingno(),
                                myList.get(i).getFinancialyearcode(),
                                myList.get(i).getCompanycode(),
                                myList.get(i).getBillcode());
                        if (Utilities.isNullOrEmpty(einvoicefilePath)) {
                            einvoicepending = einvoicepending + 1;
                        }
                    }
                }
                total_einvoice.setText(String.valueOf(einvoicebill));
                einvoice_generated.setText(String.valueOf(einvoicegenerate));
                einvocie_pending.setText(String.valueOf(einvoicepending));
                total_bill.setText(String.valueOf(totalbill));
                isloadeinvoice = true;
            }
        } catch (Exception ex) {
            Log.i("Calculate  Exception", ex.toString());
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = ex.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "),
                    this.getClass().getSimpleName() + " - Calculate  Exception ", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }

}