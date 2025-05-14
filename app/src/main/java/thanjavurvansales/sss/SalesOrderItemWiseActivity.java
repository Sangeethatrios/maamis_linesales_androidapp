package thanjavurvansales.sss;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SalesOrderItemWiseActivity extends AppCompatActivity {
    Context context;
    ArrayList<SalesItemWiseReportDetails> outstandinglist = new ArrayList<SalesItemWiseReportDetails>();
    ImageButton goback,salesitemwiselogout,salesitemwisegotohome;
    private int year, month, day;
    private Calendar calendar;
    public static String getsaleslistdate,getbillfromdate,getbilltodate;
    final DecimalFormat df = new DecimalFormat("0.00");
    public static ArrayList<SalesItemWiseReportDetails> getdata;
    public static String getfiltercompanycode="0";
    private boolean deviceFound;
    ImageButton print;
    BluetoothSocket mmSocket;
    String[] companycode,companyname,shortname;
    Dialog companydialog;
    ListView lv_CompanyList;

    public static ListView salesreturnitemwiselistview;
    public static TextView itemlisttotalqty,itemlisttotalamount,salesreturnitemfilterdate,companylist,txtcompanyname;
    private PrintData printData;
    Dialog printpopup;

    BluetoothAdapter mBluetoothAdapter;
    public static PreferenceMangr preferenceMangr=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_order_item_wise);
        context = this;
        goback = (ImageButton)findViewById(R.id.goback);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack(null);
            }
        });
        salesreturnitemwiselistview = (ListView) findViewById(R.id.salesreturnitemwiselistview);
        itemlisttotalqty = (TextView)findViewById(R.id.itemlisttotalqty);
        itemlisttotalamount = (TextView)findViewById(R.id.itemlisttotalamount);

        txtcompanyname = (TextView) findViewById(R.id.txtcompanyname);
        salesreturnitemfilterdate = (TextView) findViewById(R.id.salesreturnitemfilterdate);
        print = (ImageButton)findViewById(R.id.printbtn);

        getfiltercompanycode="0";

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }catch (Exception e){
            Log.d("Bluetooth Adapter : ",e.toString());
        }
        try {
            preferenceMangr = new PreferenceMangr(context);
        }catch (Exception e){
            Log.d("Bluetooth Adapter : ",e.toString());
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

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);


        getbillfromdate = preferenceMangr.pref_getString("getformatdate");
        getbilltodate = preferenceMangr.pref_getString("getformatdate");
        salesreturnitemfilterdate.setText(preferenceMangr.pref_getString("getcurrentdatetime"));
        getsaleslistdate=preferenceMangr.pref_getString("getcurrentdatetime");



        salesitemwiselogout = (ImageButton)findViewById(R.id.salesitemwiselogout);




        //Dropdown values
        //company name
        txtcompanyname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetCompanyName();
            }
        });


        salesitemwiselogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  HomeActivity.logoutprocess = "True";
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



        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(outstandinglist.size() > 0 ) {

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
                                    Cursor mCur = objcustomerAdaptor.GetSalesOrderItemWiseReport_Date(getbillfromdate);
                                    objcustomerAdaptor.close();
                                    if (mCur.getCount() > 0) {
                                        /*boolean billPrinted = false;
                                        billPrinted = printData.GetSalesOrderItemreportBillPrint();
                                        if (!billPrinted) {
                                            Toast toast = Toast.makeText(getApplicationContext(), "Unable to connect to Bluetooth Printer!", Toast.LENGTH_LONG);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
                                            //Toast.makeText(context, "Unable to connect to Bluetooth Printer!", Toast.LENGTH_SHORT).show();
                                            printpopup.dismiss();
                                            return;
                                        }
                                        printpopup.dismiss();*/

                                        if(mBluetoothAdapter != null) {

                                            if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                                                if(!preferenceMangr.pref_getString("SelectedPrinterAddress").equals("")) {
                                                    new AsyncPrintSalesOrderItemWiseReport().execute();
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
                                        toast.show();
                                        printpopup.dismiss();
                                        //Toast.makeText(context, "No Data Available", Toast.LENGTH_SHORT).show();
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
                    printpopup.setCancelable(true);
                    printpopup.show();
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(), "No Data Available", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }


            }
        });

        //salesfilterdate date
        salesreturnitemfilterdate.setOnClickListener(new View.OnClickListener() {
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
                        getbillfromdate = year+ "-"+getmonth+"-"+getdate;
                        salesreturnitemfilterdate.setText(vardate );
                        getsaleslistdate = vardate;
                        GetSalesItemWiseReport_Date();

                    }
                };
                DatePickerDialog dpDialog = new DatePickerDialog(context, listener, year, month, day);
                dpDialog.show();
            }
        });
        GetSalesItemWiseReport_Date();

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
                Toast toast = Toast.makeText(getApplicationContext(), "No Area in this route", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                // Toast.makeText(getApplicationContext(),"No Area in this route",Toast.LENGTH_SHORT).show();
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
                    GetSalesItemWiseReport_Date();
                    companydialog.dismiss();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView listcompanyname;

        }

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
        getbillfromdate = year +"-"+getmonth+"-"+getdate ;
        getbilltodate=year +"-"+getmonth+"-"+getdate ;
        salesreturnitemfilterdate.setText(preferenceMangr.pref_getString("getcurrentdatetime") +" to " + preferenceMangr.pref_getString("getcurrentdatetime") );
        getsaleslistdate = preferenceMangr.pref_getString("getformatdate")  +" to " + preferenceMangr.pref_getString("getformatdate") ;

    }
    public void GetSalesItemWiseReport_Date() {


        try {
            outstandinglist.clear();
            itemlisttotalamount.setText("");
            DataBaseAdapter objcustomerAdaptor = new DataBaseAdapter(context);
            objcustomerAdaptor.open();
            Cursor mCur = objcustomerAdaptor.GetSalesOrderItemWiseReport_Date(getbillfromdate);
            objcustomerAdaptor.close();
            if (mCur.getCount() > 0) {
                for (int i = 0; i < mCur.getCount(); i++) {

                    outstandinglist.add(new SalesItemWiseReportDetails(mCur.getString(0),
                            mCur.getString(1),
                            df.format(Double.parseDouble(mCur.getString(2))),mCur.getString(3),
                            df.format(Double.parseDouble(mCur.getString(4)))
                            ,String.valueOf(i+1),mCur.getString(9) ));

                    mCur.moveToNext();
                }
                getdata = outstandinglist;
                SalesOrderItemWiseBaseAdapter adapter = new SalesOrderItemWiseBaseAdapter(context, outstandinglist);
                salesreturnitemwiselistview.setAdapter(adapter);
            } else {
                SalesOrderItemWiseBaseAdapter adapter = new SalesOrderItemWiseBaseAdapter(context, outstandinglist);
                salesreturnitemwiselistview.setAdapter(adapter);
                Toast toast = Toast.makeText(getApplicationContext(), "No Data Available", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //Toast.makeText(context, "No Data Available", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }


    }

    class SalesOrderItemWiseBaseAdapter extends BaseAdapter {
        LayoutInflater inflater;
        Context context;
        public   double total=0.00;
        ArrayList<SalesItemWiseReportDetails> myList;
        DecimalFormat dft = new DecimalFormat("0.00");
        LinearLayout listLL;
        Dialog lisdetailsdialog;
        ListView setListViewDeails;
        public    TextView tt1;
        public  String itemname;
        public SalesOrderItemWiseBaseAdapter( Context context,ArrayList<SalesItemWiseReportDetails> myList) {
            this.myList = myList;
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return myList.size();
        }

        @Override
        public SalesItemWiseReportDetails getItem(int position) {
            return (SalesItemWiseReportDetails) myList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder mHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.salesitemwisereport, parent, false);
                mHolder = new ViewHolder();
                try {
                    mHolder.listsno = (TextView) convertView.findViewById(R.id.listsno);
                    mHolder.listitemname = (TextView) convertView.findViewById(R.id.listitemname);
                    mHolder.listqty = (TextView) convertView. findViewById(R.id.listqty);
                    mHolder.listuom = (TextView) convertView. findViewById(R.id.listuom);
                    mHolder.listamount = (TextView) convertView. findViewById(R.id.listamount);
                    mHolder.card_view = (CardView)convertView.findViewById(R.id.card_view);
                    mHolder.LLOutstandingReportList = (LinearLayout) convertView. findViewById(R.id.LLOutstandingReportList);
                } catch (Exception e) {
                    Log.i("Route", e.toString());
                }
                convertView.setTag(mHolder);
            } else {
                mHolder = (ViewHolder) convertView.getTag();
            }
            try {
                SalesItemWiseReportDetails currentListData = getItem(position);
                mHolder.listsno.setText(currentListData.getSno());
                mHolder.listitemname.setText(currentListData.getItemname());
                double val = Double.parseDouble(currentListData.getQty());
                int getqty = (int) val;
                mHolder.listqty.setText(String.valueOf(getqty));
                mHolder.listuom.setText(currentListData.getUom());
                mHolder.listamount.setText(currentListData.getAmount());
                mHolder.listitemname.setTextColor(Color.parseColor(currentListData.getColourcode()));

                if (position % 2 == 1) {
                    mHolder.card_view.setCardBackgroundColor(Color.parseColor("#ffffff"));
                } else {
                    mHolder.card_view.setCardBackgroundColor(Color.parseColor("#caeaf3"));
                }

            } catch (Exception e) {
            }

            calculateTotalAmount();
            return convertView;
        }
        private class ViewHolder {
            TextView listsno, listitemname,listqty,listamount,listuom;
            LinearLayout LLOutstandingReportList;
            CardView card_view;
        }

        public void calculateTotalAmount() {
            Double addamt = 0.0;
            Double addqty = 0.0;
            try {

                for (int i = 0; i < myList.size(); i++) {
                    Double qty = Double.parseDouble(myList.get(i).getAmount());
                    addamt = addamt + qty;
                }
                DecimalFormat df = new DecimalFormat("#.00");
                itemlisttotalamount.setText("Total  \u20B9 "+df.format(addamt));


            } catch (Exception ex) {
            }
        }
    }
    public void goBack(View v) {
        Intent i = new Intent(context ,MenuActivity.class);
        startActivity(i);
    }
    @Override
    public void onBackPressed() {
        goBack(null);
    }

    protected class AsyncPrintSalesOrderItemWiseReport extends
            AsyncTask<String, JSONObject, Boolean> {
        Boolean billPrinted = false;
        JSONObject jsonObj = null;
        ProgressDialog loading;
        @Override
        protected Boolean doInBackground(String... params) {
            //final String finalGetreceipttransano = params[0];
            //final String financialyearcode = params[1];
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
                    billPrinted = (boolean) printData.GetSalesOrderItemreportBillPrint(SalesOrderItemWiseActivity.this);
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
                    printData = null;

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

}
