package trios.linesales;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;
import com.goodiebag.pinview.Pinview;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SalesOrderBillwiseReportActivity extends AppCompatActivity {
    public static SwipeMenuListView saleslistView;
    public Context context;
    private int year, month, day;
    Button btnSubmitpin;
    private Calendar calendar;
    String getschedulecode="0";
    Pinview pinview;
    public static TextView salesdate;
    public static TextView totalamtval,cashtotalamt,credittotalamt,txtcompanyname;
    ImageView addsales,saleslistgoback,saleslogout;
    public static String getsaleslistdate,getbillfromdate,getbilltodate ;

    Dialog pindialog;
    ArrayList<SalesOrderListDetails> saleslist = new ArrayList<SalesOrderListDetails>();
    public static ArrayList<SalesOrderListDetails> getdata;
    Dialog dialogstatus,companydialog;
    boolean networkstate;
    String[] companycode,companyname,shortname;
    ListView lv_CompanyList;
    public static String getsalesreviewtransactionno="",getsalesreviewfinanicialyear="",
            getsalesreviewcompanycode="",getfiltercompanycode="0",getstaticflag="0",getcancelflag="";

    public  boolean issales=false;
    SalesOrderListBaseAdapterList adapter=null;
    boolean deviceFound;
    boolean issalesclose=false;
    //private PrintData printData;
    Dialog printpopup;
    ImageButton print;

    BluetoothAdapter mBluetoothAdapter;
    public static PreferenceMangr preferenceMangr=null;
    ReceiveListener receiveListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_order_billwise_report);

        //declare all variables
        context=this;
        saleslistView = (SwipeMenuListView) findViewById(R.id.saleslistView);
        salesdate = (TextView)findViewById(R.id.salesdate);
        totalamtval = (TextView)findViewById(R.id.totalamtval);
        credittotalamt = (TextView)findViewById(R.id.credittotalamt);
        cashtotalamt = (TextView)findViewById(R.id.cashtotalamt);
        saleslogout = (ImageView)findViewById(R.id.saleslogout);
        saleslistgoback = (ImageView)findViewById(R.id.saleslistgoback);
        txtcompanyname = (TextView)findViewById(R.id.txtcompanyname);
        print = (ImageButton)findViewById(R.id.printbtn);
        getfiltercompanycode="0";

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }catch (Exception e){
            Log.d("Bluetooth Adapter : ",e.toString());
        }
        try {
            preferenceMangr = new PreferenceMangr(context);
            receiveListener = new ReceiveListener() {
                @Override
                public void onPtrReceive(Printer printer, int i, PrinterStatusInfo printerStatusInfo, String s) {
                    enablePrintButtons();
                }
            };
        }catch (Exception e){
            Log.d("PreferenceMangr : ",e.toString());
        }

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
        salesdate.setText(preferenceMangr.pref_getString("getcurrentdatetime"));

        getbillfromdate = preferenceMangr.pref_getString("getformatdate");
        getbilltodate = preferenceMangr.pref_getString("getformatdate");

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
                        getbillfromdate= year+ "-"+getmonth+"-"+getdate;
                        salesdate.setText(vardate );
                        getsaleslistdate = vardate;
                        getbilltodate = vardate;
                        GetSalesList();

                    }
                };
                DatePickerDialog dpDialog = new DatePickerDialog(context, listener, year, month, day);
                dpDialog.show();
            }
        });
        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(saleslist.size() >0) {
                    printpopup = new Dialog(context);
                    printpopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    printpopup.setContentView(R.layout.printpopup);

                    TextView txtYesAction = (TextView) printpopup.findViewById(R.id.txtYesAction);
                    TextView txtNoAction = (TextView) printpopup.findViewById(R.id.txtNoAction);
                    txtYesAction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            try {

                                /*printData = new PrintData(context);
                                deviceFound = printData.findBT();

                                if (!deviceFound) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    printpopup.dismiss();
                                    toast.show();
                                } else {*/
                                    DataBaseAdapter objcustomerAdaptor = new DataBaseAdapter(context);
                                    objcustomerAdaptor.open();
                                    Cursor  mCur = objcustomerAdaptor.GetSalesOrderPrintListDB(getsaleslistdate);
                                    objcustomerAdaptor.close();
                                    if (mCur.getCount() > 0) {
                                        /*boolean billPrinted = false;
                                        billPrinted = printData.GetSalesOrderBillWiseReportPrint();
                                        if (!billPrinted) {
                                            Toast toast = Toast.makeText(getApplicationContext(), "Unable to connect to Bluetooth Printer!", Toast.LENGTH_LONG);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
                                            printpopup.dismiss();
                                            return;
                                        }
                                        printpopup.dismiss();*/

                                        if(mBluetoothAdapter != null) {

                                            if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                                                if(!preferenceMangr.pref_getString("SelectedPrinterAddress").equals("")) {
                                                    disablePrintButtons();
                                                    new AsyncPrintSalesOrderBillWiseReport().execute();
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
                                    } else {
                                        Toast toast = Toast.makeText(getApplicationContext(), "No Data Available", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        printpopup.dismiss();
                                        toast.show();
                                    }

                                //}
                            }
                            catch (Exception e){
                                Toast toast = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                printpopup.dismiss();
                                DataBaseAdapter mDbErrHelper2 = new DataBaseAdapter(context);
                                mDbErrHelper2.open();
                                mDbErrHelper2.insertErrorLog(e.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
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
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(), "No Sales Return Available", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }



            }
        });

        //Click swipemenu action ...Edit sales receipt and then print
        saleslistView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        ArrayList<SalesOrderListDetails> currentListDatareview = getdata;
                        getsalesreviewtransactionno = currentListDatareview.get(position).getTransactionno();
                        getsalesreviewfinanicialyear = currentListDatareview.get(position).getFinancialyearcode();
                        getsalesreviewcompanycode = currentListDatareview.get(position).getCompanycode();
                        getstaticflag = currentListDatareview.get(position).getFlag();
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
                            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
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
                        Intent i = new Intent(context,SalesOrderViewActivity.class);
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

        //GetSale order List
        GetSalesList();

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(printpopup!=null){
                if(printpopup.isShowing()){
                    return true;
                }
            }

        }
        return super.onKeyDown(keyCode, event);
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
    public  void GetSalesList(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            saleslist.clear();
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetSalesOrderListDB(getsaleslistdate,getfiltercompanycode);
            if(Cur.getCount()>0) {
                issales=true;
                for(int i=0;i<Cur.getCount();i++){
                    saleslist.add(new SalesOrderListDetails(Cur.getString(3),Cur.getString(7),
                            Cur.getString(8),Cur.getString(20),
                            Cur.getString(21),Cur.getString(25),Cur.getString(14),
                            String.valueOf(Cur.getCount()-i),Cur.getString(11),Cur.getString(23),Cur.getString(19),
                            Cur.getString(22),Cur.getString(10),
                            Cur.getString(18),Cur.getString(24),Cur.getString(15)
                            ,Cur.getString(16),Cur.getString(2),Cur.getString(17),
                            Cur.getString(0)));
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
    class SalesOrderListBaseAdapterList extends BaseAdapter {
        LayoutInflater inflater;
        Context context;
        ArrayList<SalesOrderListDetails> myList;
        DecimalFormat dft = new DecimalFormat("0.00");
        public SalesOrderListBaseAdapterList(Context context,ArrayList<SalesOrderListDetails> myList) {
            this.myList = myList;
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return myList.size();
        }

        @Override
        public SalesOrderListDetails getItem(int position) {
            return (SalesOrderListDetails) myList.get(position);
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
                    mHolder.saleslistSno = (TextView) convertView.findViewById(R.id.saleslistSno);
                    mHolder.saleslistbillno = (TextView) convertView.findViewById(R.id.saleslistbillno);
                    mHolder.saleslistretailer = (TextView) convertView.findViewById(R.id.saleslistretailer);
                    mHolder.saleslisttotal = (TextView) convertView.findViewById(R.id.saleslisttotal);
                    mHolder.listLL = (LinearLayout) convertView.findViewById(R.id.listLL);
                    mHolder.salescard_view= (CardView)convertView.findViewById(R.id.salescard_view);
                    mHolder.saleslistcity = (TextView)convertView.findViewById(R.id.saleslistcity);
                    mHolder.saleslistarea = (TextView)convertView.findViewById(R.id.saleslistarea);
                    mHolder.saleslistgstin = (TextView)convertView.findViewById(R.id.saleslistgstin);
                    mHolder.saleslistamounttype = (TextView)convertView.findViewById(R.id.saleslistamounttype);
                    mHolder.saleslistbookingno = (TextView)convertView.findViewById(R.id.saleslistbookingno);
                    mHolder.saleslistpaymenttype = (TextView)convertView.findViewById(R.id.saleslistpaymenttype);
                    mHolder.saleslistcompanyname = (TextView)convertView.findViewById(R.id.saleslistcompanyname);
                    mHolder.saleslistbillcopyrequired = (TextView)convertView.findViewById(R.id.saleslistbillcopyrequired) ;

                    convertView.setTag(mHolder);
                    convertView.setTag(R.id.saleslistSno, mHolder.saleslistSno);
                    convertView.setTag(R.id.saleslistbillno, mHolder.saleslistbillno);
                    convertView.setTag(R.id.saleslistretailer, mHolder.saleslistretailer);
                    convertView.setTag(R.id.saleslisttotal, mHolder.saleslisttotal);
                    convertView.setTag(R.id.listLL, mHolder.listLL);
                    convertView.setTag(R.id.salescard_view,mHolder.salescard_view);
                    convertView.setTag(R.id.saleslistcity,mHolder.saleslistcity);
                    convertView.setTag(R.id.saleslistarea,mHolder.saleslistarea);
                    convertView.setTag(R.id.saleslistgstin,mHolder.saleslistgstin);
                    convertView.setTag(R.id.saleslistcompanyname,mHolder.saleslistcompanyname);
                    convertView.setTag(R.id.saleslistbookingno,mHolder.saleslistbookingno);
                    convertView.setTag(R.id.saleslistpaymenttype,mHolder.saleslistpaymenttype);
                    convertView.setTag(R.id.saleslistbillcopyrequired,mHolder.saleslistbillcopyrequired);

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
                mHolder.saleslistSno.setTag(position);
                mHolder.saleslistbillno.setTag(position);
                mHolder.saleslistretailer.setTag(position);
                mHolder.saleslisttotal.setTag(position);
                mHolder.saleslistcity.setTag(position);
                mHolder.saleslistarea.setTag(position);
                mHolder.saleslistgstin.setTag(position);
                mHolder.saleslistcompanyname.setTag(position);
                mHolder.saleslistbookingno.setTag(position);
                mHolder.saleslistpaymenttype.setTag(position);
                mHolder.saleslistbillcopyrequired.setTag(position);


                final SalesOrderListDetails currentListData = getItem(position);

                mHolder.saleslistSno.setText(currentListData.getSno());
                mHolder.saleslistbillno.setText(currentListData.getBillcode());

                mHolder.saleslistretailer.setText(currentListData.getRetailernametamil());
                mHolder.saleslisttotal.setText(dft.format(Double.parseDouble(currentListData.getGrandtotal())));
                mHolder.saleslistcity.setText(currentListData.getRetailercity());
                mHolder.saleslistarea.setText(currentListData.getArea());
                mHolder.saleslistbookingno.setText("Order.No. "+currentListData.getBookingno());
                mHolder.saleslistcompanyname.setText(currentListData.getCompanyshortname());



                //Flag 3 means cancelled bill
                if(currentListData.getStatus().equals("1") ){
                    mHolder.salescard_view.setCardBackgroundColor(context.getResources().getColor(R.color.gray));
                    mHolder.saleslistSno.setTextColor(context.getResources().getColor(R.color.green));
                    mHolder.saleslistbillno.setTextColor(context.getResources().getColor(R.color.green));
                    mHolder.saleslistretailer.setTextColor(context.getResources().getColor(R.color.green));
                    mHolder.saleslisttotal.setTextColor(context.getResources().getColor(R.color.green));
                    mHolder.saleslistcity.setTextColor(context.getResources().getColor(R.color.green));
                    mHolder.saleslistarea.setTextColor(context.getResources().getColor(R.color.green));
                    mHolder.saleslistcompanyname.setTextColor(context.getResources().getColor(R.color.green));
                    mHolder.saleslistbookingno.setTextColor(context.getResources().getColor(R.color.green));
                    mHolder.saleslistpaymenttype.setTextColor(context.getResources().getColor(R.color.green));

                }
                if(currentListData.getStatus().equals("2") ){
                    mHolder.salescard_view.setCardBackgroundColor(context.getResources().getColor(R.color.gray));
                    mHolder.saleslistSno.setTextColor(context.getResources().getColor(R.color.red));
                    mHolder.saleslistbillno.setTextColor(context.getResources().getColor(R.color.red));
                    mHolder.saleslistretailer.setTextColor(context.getResources().getColor(R.color.red));
                    mHolder.saleslisttotal.setTextColor(context.getResources().getColor(R.color.red));
                    mHolder.saleslistcity.setTextColor(context.getResources().getColor(R.color.red));
                    mHolder.saleslistarea.setTextColor(context.getResources().getColor(R.color.red));
                    mHolder.saleslistcompanyname.setTextColor(context.getResources().getColor(R.color.red));
                    mHolder.saleslistbookingno.setTextColor(context.getResources().getColor(R.color.red));
                    mHolder.saleslistpaymenttype.setTextColor(context.getResources().getColor(R.color.red));

                }


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
                Log.i("SalesList Exception", e.toString());
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
            TextView saleslistSno, saleslistbillno,  saleslistretailer,
                    saleslisttotal,saleslistcity,saleslistarea,saleslistgstin,
                    saleslistamounttype,saleslistbookingno,saleslistpaymenttype,saleslistcompanyname,saleslistbillcopyrequired;
            LinearLayout listLL;
            CardView salescard_view;
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

    protected class AsyncPrintSalesOrderBillWiseReport extends
            AsyncTask<String, JSONObject, Boolean> {
        Boolean billPrinted = false;
        JSONObject jsonObj = null;
        ProgressDialog loading;
        @Override
        protected Boolean doInBackground(String... params) {
            //final String finalGetreceipttransano = params[0];
            //final String financialyearcode = params[1];
            try {
                EpsonT20Printer epsonT20Printer = new EpsonT20Printer(SalesOrderBillwiseReportActivity.this, SalesOrderBillwiseReportActivity.this, receiveListener);
                deviceFound = epsonT20Printer.findBT();
                Log.d("deviceFound",String.valueOf(deviceFound));
                if (!deviceFound) {
                    billPrinted = deviceFound;
                } else {
                    billPrinted = (boolean) epsonT20Printer.GetSalesOrderBillWiseReportPrint(SalesOrderBillwiseReportActivity.this);
                }
                Log.d("billPrinted",String.valueOf(billPrinted));
            }
            catch (Exception e){
                billPrinted = true;
                Log.d("aaaa", e.getLocalizedMessage());
                DataBaseAdapter mDbErrHelper2 = new DataBaseAdapter(context);
                mDbErrHelper2.open();
                mDbErrHelper2.insertErrorLog(e.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
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
                    enablePrintButtons();

                }
            }catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncScheduleDetails", e.getLocalizedMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        }
    }

    public void enablePrintButtons() {
        print.setEnabled(true);
    }

    public void disablePrintButtons() {
        print.setEnabled(false);
    }

}
