package trios.linesales;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.goodiebag.pinview.Pinview;
import com.itextpdf.text.pdf.parser.Line;
import com.leavjenn.smoothdaterangepicker.date.SmoothDateRangePickerFragment;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class PremiumCustomerOrderListActivity extends AppCompatActivity {
    public static SwipeMenuListView saleslistView;
    public Context context;
    private int year, month, day;
    Button btnSubmitpin;
    private Calendar calendar;
    String getschedulecode="0";
    Pinview pinview;
    TextView txtFilterDate;
    public static TextView totalamtval,txtcompanyname;
    ImageView saleslistgoback,saleslogout;
    public static String getsaleslistdate ;
    Dialog pindialog;
    ArrayList<PremiumCustomerOrderListDetails> saleslist = new ArrayList<PremiumCustomerOrderListDetails>();
    public static ArrayList<PremiumCustomerOrderListDetails> getdata;
    Dialog dialogstatus,companydialog;
    boolean networkstate;
    String[] companycode,companyname,shortname;
    ListView lv_CompanyList;
    public static String getsalesreviewtransactionno="",getsalesreviewfinanicialyear="",
            getsalesreviewcompanycode="",getfiltercompanycode="0",getstaticflag="0",getcancelflag="",getbillstatus="";

    public  boolean issales=false;
    SalesOrderListBaseAdapterList adapter=null;
    boolean deviceFound;
    boolean issalesclose=false;
    private PrintData printData;
    Dialog printpopup;

    Dialog salescashclose;
    TextView popup_salesclose,popup_cashclose,popup_salesclose_title;
    public static PreferenceMangr preferenceMangr=null;
    ListView lv_freeitemlist;
    String[] FreeItemName,FreeItemOp,FreeItemHandover,FreeItemDistributed,FreeItemBalance,FreeItemCode,FreeItemSNO;
    Dialog freeitemdialog;

    boolean issalesclosepopup=false;
    String[] venderid,vendername;

    TextView txtupivendername, txtFilterRoute, txtFilterCustomer;
    public static String getpaymentvenderID="0", filterRouteCode = "0", filterCustomerCode = "0";
    Dialog upipaymentvenderdialog, routeDialog, customerDialog;
    ListView lv_UPIPaymentList;

    ArrayList<RouteDetails> routeDetails = new ArrayList<>();
    ArrayList<CustomerDetails> customerDetails = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium_customer_order_list);

        //declare all variables
        context=this;
        saleslistView = (SwipeMenuListView) findViewById(R.id.saleslistView);
        txtFilterDate = (TextView)findViewById(R.id.txtFilterDate);
        totalamtval = (TextView)findViewById(R.id.totalamtval);
        saleslogout = (ImageView)findViewById(R.id.saleslogout);
        saleslistgoback = (ImageView)findViewById(R.id.saleslistgoback);
        txtcompanyname = (TextView)findViewById(R.id.txtcompanyname);
        txtFilterRoute = (TextView) findViewById(R.id.txtFilterRoute);
        txtFilterCustomer = (TextView) findViewById(R.id.txtFilterCustomer);

        getfiltercompanycode="0";
        filterRouteCode = "0";
        filterCustomerCode = "0";

        try {
            preferenceMangr = new PreferenceMangr(context);
        }catch (Exception e){
            Log.d("Preference Manager : ",e.toString());
        }

        Calendar calendarFilter = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String todate = sdf.format(calendarFilter.getTime());

        calendarFilter.add(Calendar.DAY_OF_YEAR, - 30);
        String startDate = sdf.format(calendarFilter.getTime());

        txtFilterDate.setText(startDate + " to " + todate);

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
            if(getschedulelist != null)
                getschedulelist.close();
        }


        //Set Now Date
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        getsaleslistdate = preferenceMangr.pref_getString("getformatdate");

        txtFilterDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SmoothDateRangePickerFragment smoothDateRangePickerFragment =
                        SmoothDateRangePickerFragment
                                .newInstance(new SmoothDateRangePickerFragment.OnDateRangeSetListener() {
                                    @Override
                                    public void onDateRangeSet(SmoothDateRangePickerFragment view,
                                                               int yearStart, int monthStart,
                                                               int dayStart, int yearEnd,
                                                               int monthEnd, int dayEnd) {
                                        String day;
                                        if(dayStart < 10){
                                            day = "0"+String.valueOf((dayStart));
                                        }else {
                                            day = String.valueOf((dayStart));
                                        }
                                        String endday;
                                        if(dayEnd < 10){
                                            endday = "0"+String.valueOf((dayEnd));
                                        }else {
                                            endday = String.valueOf((dayEnd));
                                        }
                                        String monthvalue;
                                        int startval = ++monthStart;
                                        if(startval < 10){
                                            monthvalue = "0"+String.valueOf((startval));
                                        }else {
                                            monthvalue = String.valueOf((startval));
                                        }
                                        String monthendvalue;
                                        int endval = ++monthEnd;
                                        if(endval < 10){
                                            monthendvalue = "0"+String.valueOf((endval));
                                        }else {
                                            monthendvalue = String.valueOf((endval));
                                        }
                                        String date = (day) + "-" + (monthvalue)
                                                + "-" + yearStart + " to " + endday + "-"
                                                + (monthendvalue) + "-" + yearEnd;
                                        txtFilterDate.setText(date);

                                        GetPremiumCustomersOrderList();
                                    }
                                });
                smoothDateRangePickerFragment.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        //Swipe menu editior functionality
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create Edit Bill copy item
              /*  SwipeMenuItem cancelItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item width
                cancelItem.setWidth(130);
                // set a icon
                cancelItem.setIcon(R.drawable.ic_mode_edit);
                // add to menu
                menu.addMenuItem(cancelItem);*/

                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                //  openItem.setBackground(ContextCompat.getDrawable(context,R.drawable.noborder));
                openItem.setWidth(130);
                openItem.setIcon(R.drawable.ic_view);
                menu.addMenuItem(openItem);


            }
        };

        saleslistView.setMenuCreator(creator);

        //Click swipemenu action ...Edit sales receipt and then print
        saleslistView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        ArrayList<PremiumCustomerOrderListDetails> currentListDatareview = getdata;
                        getsalesreviewtransactionno = currentListDatareview.get(position).getTransactionno();
                        getsalesreviewfinanicialyear = currentListDatareview.get(position).getFinancialyearcode();
                        getsalesreviewcompanycode = currentListDatareview.get(position).getCompanycode();
                        getstaticflag = currentListDatareview.get(position).getFlag();
                        getbillstatus = currentListDatareview.get(position).getStatus();

                        Intent i = new Intent(context, PremiumCustomerOrderViewActivity.class);
                        startActivity(i);

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
        //Goback process
        saleslistgoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack(null);
            }
        });

        //company name
        txtcompanyname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetCompanyName();
            }
        });

        txtFilterRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetRouteList();
            }
        });

        txtFilterCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetCustomerList();
            }
        });

        GetPremiumCustomersOrderList();
    }
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
            String deviceid = preferenceMangr.pref_getString("deviceid");
            String result = "";
            try {
                GetFreeItemexcess();
                networkstate = isNetworkAvailable();
                if (networkstate == true) {
                    String getschedulecode = objdatabaseadapter.GetScheduleCode();
                    result = Utilities.getDeliveryNotePendingCount(deviceid, getschedulecode, context);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please check internet connection", Toast.LENGTH_LONG);
                    //toast.setGravity(Gravity.CENTER, 0, 0);
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
                // builder.setIcon(R.mipmap.ic_van);

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
                                            }
                                            if (getEncryptedPIN.equals(preferenceMangr.pref_getString("getpin"))) {

                                                String getresult = "0";
                                                getschedulecode = finalObjdatabaseadapter1.GetScheduleCode();
                                                //SAles close details
                                                getresult = finalObjdatabaseadapter1.InsertSalesClose(getschedulecode);
                                                if (getresult.equals("success")) {
                                                    pindialog.dismiss();
                                                    Toast toast = Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_LONG);
                                                    //toast.setGravity(Gravity.CENTER, 0, 0);
                                                    toast.show();
                                                    closeKeyboard();
                                                    //Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_SHORT).show();
                                                    networkstate = isNetworkAvailable();
                                                    if (networkstate == true) {
                                                        // new AsyncCloseSalesDetails().execute();
                                                    }
                                                    Intent i = new Intent(context, OrderFormActivity.class);
                                                    startActivity(i);
                                                } else {
                                                    Toast toast = Toast.makeText(getApplicationContext(), "Error in saving", Toast.LENGTH_LONG);
                                                    //toast.setGravity(Gravity.CENTER, 0, 0);
                                                    toast.show();
                                                    //Toast.makeText(getApplicationContext(), "Error in saving", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast toast = Toast.makeText(getApplicationContext(), "Please enter correct pin", Toast.LENGTH_LONG);
                                                //toast.setGravity(Gravity.CENTER, 0, 0);
                                                toast.show();
                                                // Toast.makeText(getApplicationContext(),"Please enter correct pin",Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        } catch (Exception e) {
                                            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                            mDbErrHelper.open();
                                            String geterrror = e.toString();
                                            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
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
            result=false;

        }
        return result;
    }
    //Sales List
    @SuppressLint("Range")
    public  void GetPremiumCustomersOrderList(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            saleslist.clear();
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetPremiumCustomersOrderListDB(txtFilterDate.getText().toString(), filterRouteCode, filterCustomerCode);
            if(Cur.getCount()>0) {
                issales=true;
                for(int i=0;i<Cur.getCount();i++){
                    saleslist.add(new PremiumCustomerOrderListDetails(String.valueOf(Cur.getCount()-i), Cur.getString(Cur.getColumnIndex("orderno")),
                            Cur.getString(Cur.getColumnIndex("orderdate")), Cur.getString(Cur.getColumnIndex("customercode")),
                            Cur.getString(Cur.getColumnIndex("customername")), Cur.getString(Cur.getColumnIndex("customernametamil")),
                            Cur.getString(Cur.getColumnIndex("status")), Cur.getString(Cur.getColumnIndex("grandtotal")),
                            Cur.getString(Cur.getColumnIndex("cityname")), Cur.getString(Cur.getColumnIndex("flag")),
                            Cur.getString(Cur.getColumnIndex("areaname")), Cur.getString(Cur.getColumnIndex("gstin")),
                            Cur.getString(Cur.getColumnIndex("bookingno")), Cur.getString(Cur.getColumnIndex("shortname")),
                            Cur.getString(Cur.getColumnIndex("transactionno")), Cur.getString(Cur.getColumnIndex("financialyearcode")),
                            Cur.getString(Cur.getColumnIndex("companycode")), Cur.getString(Cur.getColumnIndex("ordertime"))));
                    Cur.moveToNext();
                }
                getdata = saleslist;
                adapter = new SalesOrderListBaseAdapterList(context,saleslist);
                saleslistView.setAdapter(adapter);
            }else{
                adapter = new SalesOrderListBaseAdapterList(context,saleslist);
                saleslistView.setAdapter(adapter);
                totalamtval.setText("\u20B9 0.00");
                Toast.makeText(getApplicationContext(),"No Order Available",Toast.LENGTH_SHORT).show();

            }
        }  catch (Exception e){
            Log.i("SalesList", e.toString());
        }
        finally {
            // this gets called even if there is an exception somewhere above
            if(objdatabaseadapter != null)
                objdatabaseadapter.close();
            if(Cur != null)
                Cur.close();
        }
    }



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
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
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
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtcompanyname.setText(String.valueOf(shortname[position]));
                    getfiltercompanycode = companycode[position];
                    GetPremiumCustomersOrderList();
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
    }

    /************END BASE ADAPTER*************/

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
    class SalesOrderListBaseAdapterList extends BaseAdapter {
        LayoutInflater inflater;
        Context context;
        ArrayList<PremiumCustomerOrderListDetails> myList;
        DecimalFormat dft = new DecimalFormat("0.00");

        public SalesOrderListBaseAdapterList(Context context,ArrayList<PremiumCustomerOrderListDetails> myList) {
            this.myList = myList;
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return myList.size();
        }

        @Override
        public PremiumCustomerOrderListDetails getItem(int position) {
            return (PremiumCustomerOrderListDetails) myList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
        @Override
        public int getViewTypeCount() {
            if(getCount() > 0){
                return getCount();
            }else{
                return super.getViewTypeCount();
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder mHolder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.sales_order__list_details, parent, false);
                mHolder = new ViewHolder();
                try {
                    mHolder.listSno = (TextView) convertView.findViewById(R.id.saleslistSno);
                    mHolder.listbillno = (TextView) convertView.findViewById(R.id.saleslistbillno);
                    mHolder.listbilldate = (TextView) convertView.findViewById(R.id.saleslistbilldate);
                    mHolder.listbilltime = (TextView) convertView.findViewById(R.id.saleslistbilltime);
                    mHolder.listretailer = (TextView) convertView.findViewById(R.id.saleslistretailer);
                    mHolder.listtotal = (TextView) convertView.findViewById(R.id.saleslisttotal);
                    mHolder.listLL = (LinearLayout) convertView.findViewById(R.id.listLL);
                    mHolder.ordercard_view = (CardView)convertView.findViewById(R.id.salescard_view);
                    mHolder.listcity = (TextView)convertView.findViewById(R.id.saleslistcity);
                    mHolder.listarea = (TextView)convertView.findViewById(R.id.saleslistarea);
                    mHolder.listgstin = (TextView)convertView.findViewById(R.id.saleslistgstin);
                    mHolder.listamounttype = (TextView)convertView.findViewById(R.id.saleslistamounttype);
                    mHolder.listbookingno = (TextView)convertView.findViewById(R.id.saleslistbookingno);
                    mHolder.listpaymenttype = (TextView)convertView.findViewById(R.id.saleslistpaymenttype);
                    mHolder.listcompanyname = (TextView)convertView.findViewById(R.id.saleslistcompanyname);
                    mHolder.listbillcopyrequired = (TextView)convertView.findViewById(R.id.saleslistbillcopyrequired) ;

                    convertView.setTag(mHolder);
                    convertView.setTag(R.id.saleslistSno, mHolder.listSno);
                    convertView.setTag(R.id.saleslistbillno, mHolder.listbillno);
                    convertView.setTag(R.id.saleslistbilldate, mHolder.listbilldate);
                    convertView.setTag(R.id.saleslistbilltime, mHolder.listbilltime);
                    convertView.setTag(R.id.saleslistretailer, mHolder.listretailer);
                    convertView.setTag(R.id.saleslisttotal, mHolder.listtotal);
                    convertView.setTag(R.id.listLL, mHolder.listLL);
                    convertView.setTag(R.id.salescard_view,mHolder.ordercard_view);
                    convertView.setTag(R.id.saleslistcity,mHolder.listcity);
                    convertView.setTag(R.id.saleslistarea,mHolder.listarea);
                    convertView.setTag(R.id.saleslistgstin,mHolder.listgstin);
                    convertView.setTag(R.id.saleslistcompanyname,mHolder.listcompanyname);
                    convertView.setTag(R.id.saleslistbookingno,mHolder.listbookingno);
                    convertView.setTag(R.id.saleslistpaymenttype,mHolder.listpaymenttype);
                    convertView.setTag(R.id.saleslistbillcopyrequired,mHolder.listbillcopyrequired);

                } catch (Exception e) {
                    Log.i("Sales List Details", e.toString());
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    mDbErrHelper.insertErrorLog(e.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                }
                convertView.setTag(mHolder);
            } else {
                mHolder = (ViewHolder) convertView.getTag();
            }
            try {
                mHolder.listSno.setTag(position);
                mHolder.listbillno.setTag(position);
                mHolder.listbilldate.setTag(position);
                mHolder.listbilltime.setTag(position);
                mHolder.listretailer.setTag(position);
                mHolder.listtotal.setTag(position);
                mHolder.listcity.setTag(position);
                mHolder.listarea.setTag(position);
                mHolder.listgstin.setTag(position);
                mHolder.listcompanyname.setTag(position);
                mHolder.listbookingno.setTag(position);
                mHolder.listpaymenttype.setTag(position);
                mHolder.listbillcopyrequired.setTag(position);


                final PremiumCustomerOrderListDetails currentListData = getItem(position);

                mHolder.listSno.setText(currentListData.getSno());
                mHolder.listbillno.setText(currentListData.getBillcode());

                if (!Utilities.isNullOrEmpty(currentListData.getOrderdate())) {
                    mHolder.listbilldate.setVisibility(View.VISIBLE);
                    mHolder.listbilldate.setText(currentListData.getOrderdate());
                }

                if (!Utilities.isNullOrEmpty(currentListData.getOrdertime())) {
                    mHolder.listbilltime.setVisibility(View.VISIBLE);
                    mHolder.listbilltime.setText(currentListData.getOrdertime());
                }

                mHolder.listretailer.setText(currentListData.getCustomernametamil());
                mHolder.listtotal.setText(dft.format(Double.parseDouble(currentListData.getGrandtotal())));
                mHolder.listcity.setText(currentListData.getCustomercity());
                mHolder.listarea.setText(currentListData.getArea());
                mHolder.listbookingno.setText("Order.No. "+currentListData.getBookingno());
                mHolder.listcompanyname.setText(currentListData.getCompanyshortname());

                //Flag 2 means cancelled bill
//                if(currentListData.getStatus().equals("4") ){
//                    mHolder.ordercard_view.setCardBackgroundColor(context.getResources().getColor(R.color.gray));
//                    mHolder.listSno.setTextColor(context.getResources().getColor(R.color.green));
//                    mHolder.listbillno.setTextColor(context.getResources().getColor(R.color.green));
//                    mHolder.listretailer.setTextColor(context.getResources().getColor(R.color.green));
//                    mHolder.listtotal.setTextColor(context.getResources().getColor(R.color.green));
//                    mHolder.listcity.setTextColor(context.getResources().getColor(R.color.green));
//                    mHolder.listarea.setTextColor(context.getResources().getColor(R.color.green));
//                    mHolder.listcompanyname.setTextColor(context.getResources().getColor(R.color.green));
//                    mHolder.listbookingno.setTextColor(context.getResources().getColor(R.color.green));
//                    mHolder.listpaymenttype.setTextColor(context.getResources().getColor(R.color.green));
//
//                }
//                if(currentListData.getStatus().equals("2") ){
//                    mHolder.ordercard_view.setCardBackgroundColor(context.getResources().getColor(R.color.gray));
//                    mHolder.listSno.setTextColor(context.getResources().getColor(R.color.red));
//                    mHolder.listbillno.setTextColor(context.getResources().getColor(R.color.red));
//                    mHolder.listretailer.setTextColor(context.getResources().getColor(R.color.red));
//                    mHolder.listtotal.setTextColor(context.getResources().getColor(R.color.red));
//                    mHolder.listcity.setTextColor(context.getResources().getColor(R.color.red));
//                    mHolder.listarea.setTextColor(context.getResources().getColor(R.color.red));
//                    mHolder.listcompanyname.setTextColor(context.getResources().getColor(R.color.red));
//                    mHolder.listbookingno.setTextColor(context.getResources().getColor(R.color.red));
//                    mHolder.listpaymenttype.setTextColor(context.getResources().getColor(R.color.red));
//
//                }
                mHolder.listcompanyname.setVisibility(View.GONE);


                if(position %2 == 1)
                {
                    // Set a background color for ListView regular row/item
                    mHolder.listLL.setBackgroundColor(getResources().getColor(R.color.lightredgray));
                }
                else
                {
                    // Set the background color for alternate row/item
                    mHolder.listLL.setBackgroundColor(getResources().getColor(R.color.lightvoilet));
                }

            } catch (Exception e) {
                Log.i("Premium Customer Order List Exception", e.toString());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            calculateTotalAmount();
            return convertView;
        }
        private class ViewHolder {
            TextView listSno, listbillno, listbilldate, listbilltime, listretailer, listtotal, listcity, listarea, listgstin,
                    listamounttype, listbookingno, listpaymenttype, listcompanyname, listbillcopyrequired;
            LinearLayout listLL;
            CardView ordercard_view;
        }
        public void calculateTotalAmount() {
            Double addamt = 0.0;
            Double cashamt = 0.0;
            Double creditamt = 0.0;

            try {
                DecimalFormat df = new DecimalFormat("0.00");
                for (int i = 0; i < myList.size(); i++) {
                    if(!myList.get(i).getFlag().equals("3")  && !myList.get(i).getFlag().equals("6") && !myList.get(i).getStatus().equals("2")) {
                        Double qty = Double.parseDouble(myList.get(i).getGrandtotal());
                        addamt = addamt + qty;
                    }
                }
                totalamtval.setText("\u20B9 "+df.format(addamt));
            } catch (Exception ex) {
                Log.i("Calculate  Exception", ex.toString());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = ex.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        }
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
                result = Utilities.getDeliveryNotePendingCount(deviceid, getschedulecode, context);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Please check internet connection", Toast.LENGTH_LONG);
                //toast.setGravity(Gravity.CENTER, 0, 0);
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
                                                    //toast.setGravity(Gravity.CENTER, 0, 0);
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
                                                    //toast.setGravity(Gravity.CENTER, 0, 0);
                                                    toast.show();
                                                }
                                            } else {
                                                Toast toast = Toast.makeText(getApplicationContext(), "Please enter correct pin", Toast.LENGTH_LONG);
                                                //toast.setGravity(Gravity.CENTER, 0, 0);
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
                        txtupivendername,getpaymentvenderID,upipaymentvenderdialog, PremiumCustomerOrderListActivity.this);
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
//                txtupiamount.setEnabled(false) ;
                txtbillamount.setEnabled(false) ;
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

                if (!Utilities.isNullOrEmpty(getimageurl)) {
                    File file = new File(getimageurl);
                    Picasso.with(context)
                            .load(file)
                            .into(imageView);
                }

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

    public  void GetRouteList(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetAllRouteListDB();

            if(Cur.getCount()>0) {
                routeDetails.clear();
                for(int i=0;i<Cur.getCount();i++){
                    routeDetails.add(new RouteDetails(Cur.getString(0), Cur.getString(2)));
                    Cur.moveToNext();
                }

                routeDialog = new Dialog(context);
                routeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                routeDialog.setContentView(R.layout.customerpopup);
                ListView lv_RouteList = (ListView) routeDialog.findViewById(R.id.lv_CustomerList);
                ImageView close = (ImageView) routeDialog.findViewById(R.id.close);

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        routeDialog.dismiss();
                    }
                });
                RouteListAdapter adapter = new RouteListAdapter(context, routeDetails);
                lv_RouteList.setAdapter(adapter);
                routeDialog.show();
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

    public class RouteListAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;
        private ArrayList<RouteDetails> routeDetails = new ArrayList<>();

        RouteListAdapter(Context c, ArrayList<RouteDetails> routeDetails) {
            context = c;
            layoutInflater = LayoutInflater.from(context);
            this.routeDetails = routeDetails;
        }

        @Override
        public int getCount() {
            return routeDetails.size();
        }

        @Override
        public RouteDetails getItem(int position) {
            return routeDetails.get(position);
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
                convertView = layoutInflater.inflate(R.layout.routepopuplist, parent, false);
                mHolder = new ViewHolder();
                try {
                    mHolder.listroutename = (TextView) convertView.findViewById(R.id.listroutename);
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
            RouteDetails currentListDetails = getItem(position);
            try {
                mHolder.listroutename.setText(String.valueOf(currentListDetails.getRouteName()));
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
                    filterRouteCode = currentListDetails.getRouteCode();
                    txtFilterRoute.setText(currentListDetails.getRouteName());
                    routeDialog.dismiss();
                    GetPremiumCustomersOrderList();
                }
            });

            return convertView;
        }

        private class ViewHolder {
            private TextView listroutename;
        }
    }

    public  void GetCustomerList(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetAllCustomerListDB(filterRouteCode);

            if(Cur.getCount()>0) {
                customerDetails.clear();
                customerDetails.add(new CustomerDetails("0", "All Customers", "All Customers"));
                for(int i=0;i<Cur.getCount();i++){
                    customerDetails.add(new CustomerDetails(Cur.getString(0), Cur.getString(1), Cur.getString(2)));
                    Cur.moveToNext();
                }

                customerDialog = new Dialog(context);
                customerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                customerDialog.setContentView(R.layout.customerpopup);
                ListView lv_RouteList = (ListView) customerDialog.findViewById(R.id.lv_CustomerList);
                ImageView close = (ImageView) customerDialog.findViewById(R.id.close);

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customerDialog.dismiss();
                    }
                });

                CustomerListAdapter adapter = new CustomerListAdapter(context, customerDetails);
                lv_RouteList.setAdapter(adapter);
                customerDialog.show();

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

    public class CustomerListAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;
        private ArrayList<CustomerDetails> customerDetails = new ArrayList<>();

        CustomerListAdapter(Context c, ArrayList<CustomerDetails> customerDetails) {
            context = c;
            layoutInflater = LayoutInflater.from(context);
            this.customerDetails = customerDetails;
        }

        @Override
        public int getCount() {
            return this.customerDetails.size();
        }

        @Override
        public CustomerDetails getItem(int position) {
            return this.customerDetails.get(position);
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
                convertView = layoutInflater.inflate(R.layout.customerpopuplist, parent, false);
                mHolder = new ViewHolder();
                try {
                    mHolder.listcustomername = (TextView) convertView.findViewById(R.id.listcustomername);
                    mHolder.linear1 = (LinearLayout) convertView.findViewById(R.id.linear1);
                    mHolder.linear1.setVisibility(View.GONE);
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
            CustomerDetails currentListDetails = getItem(position);
            try {
                mHolder.listcustomername.setText(String.valueOf(currentListDetails.getCustomernametamil()));
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
                    filterCustomerCode = currentListDetails.getCustomercode();
                    txtFilterCustomer.setText(currentListDetails.getCustomernametamil());
                    customerDialog.dismiss();
                    GetPremiumCustomersOrderList();
                }
            });

            return convertView;
        }

        private class ViewHolder {
            private TextView listcustomername;
            private LinearLayout linear1;
        }
    }
}
