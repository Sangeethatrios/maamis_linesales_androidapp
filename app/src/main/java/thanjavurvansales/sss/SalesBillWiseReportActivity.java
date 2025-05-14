package thanjavurvansales.sss;

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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenuListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class SalesBillWiseReportActivity extends AppCompatActivity {
    public static SwipeMenuListView saleslistView;
    public Context context;
    private int year, month, day;
    private Calendar calendar;
    TextView salesdate;
    public static TextView totalamtval,cashtotalamt,credittotalamt,txtcompanyname;
    ImageView addsales,goback,salesitemwiselogout;
    public static String getsaleslistdate,companyshortname,getsalesfilterdate ;
    ArrayList<SalesListDetails> saleslist = new ArrayList<SalesListDetails>();
    Spinner selectpaymenttype,selectpaymentstatus;
    String[] arrapaymenttype,arrpaymentstatus;
    public static String getpaymenttype="All Bill";
    ImageButton print;
    private boolean deviceFound;
    public static String getpaymentstatus="All Pays";
    SalesBillWiseBaseAdapterList adapter=null;
    String[] companycode,companyname,shortname;
    Dialog companydialog;
    ListView lv_CompanyList;
    public static String getfiltercompanycode="0";
    public static ArrayList<SalesListDetails> getdata;
    private PrintData printData;
    Dialog printpopup;

    BluetoothAdapter mBluetoothAdapter;
    public static PreferenceMangr preferenceMangr=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_bill_wise_report);

        //declare all variables
        context=this;

        saleslistView = (SwipeMenuListView) findViewById(R.id.saleslistView);
        salesdate = (TextView)findViewById(R.id.salesdate);
        totalamtval = (TextView)findViewById(R.id.totalamtval);
        credittotalamt = (TextView)findViewById(R.id.credittotalamt);
        cashtotalamt = (TextView)findViewById(R.id.cashtotalamt);
        salesitemwiselogout = (ImageView)findViewById(R.id.salesitemwiselogout);
        goback = (ImageView)findViewById(R.id.goback);
        txtcompanyname = (TextView)findViewById(R.id.txtcompanyname);
        selectpaymenttype = (Spinner)findViewById(R.id.selectpaymenttype);
        selectpaymentstatus = (Spinner)findViewById(R.id.selectpaymentstatus);
        arrapaymenttype = getResources().getStringArray(R.array.paymenttype);
        arrpaymentstatus = getResources().getStringArray(R.array.paymentstatus);
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



        //Set Now Date
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        getsaleslistdate = preferenceMangr.pref_getString("getformatdate");
        getsalesfilterdate = preferenceMangr.pref_getString("getcurrentdatetime");
        salesdate.setText(preferenceMangr.pref_getString("getcurrentdatetime"));


        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                String message = "Are you sure you want to print?";
                builder.setMessage(message)
                        .setIcon(context.getApplicationInfo().icon)
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                             *//*   PrintData p = new PrintData(context);
                                deviceFound = p.findBT();*//*
                                try {
                                    printData = new PrintData(context);
                                    deviceFound = printData.findBT();

                                    if (!deviceFound) {
                                        Toast toast = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                        return;
                                    } else {

                                        DataBaseAdapter objcustomerAdaptor = new DataBaseAdapter(context);
                                        objcustomerAdaptor.open();
                                        Cursor  mCur = objcustomerAdaptor.GetSalesPrintListDB(getsaleslistdate,getfiltercompanycode,
                                                getpaymenttype,getpaymentstatus);
                                        objcustomerAdaptor.close();
                                        if (mCur.getCount() > 0) {
                                            boolean billPrinted = false;
                                            billPrinted = printData.GetSalesBillWiseReportPrint();
                                            if (!billPrinted) {
                                                Toast toast = Toast.makeText(getApplicationContext(), "Unable to connect to Bluetooth Printer!", Toast.LENGTH_LONG);
                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                toast.show();
                                                return;
                                            }

                                        } else {
                                            Toast toast = Toast.makeText(getApplicationContext(), "No Data Available", Toast.LENGTH_LONG);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
                                        }

                                    }
                                }
                                catch (Exception e)
                                {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    DataBaseAdapter mDbErrHelper2 = new DataBaseAdapter(context);
                                    mDbErrHelper2.open();
                                    mDbErrHelper2.insertErrorLog(e.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                    mDbErrHelper2.close();

                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();

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
                                Cursor  mCur = objcustomerAdaptor.GetSalesPrintListDB(getsaleslistdate,getfiltercompanycode,
                                        getpaymenttype,getpaymentstatus);
                                objcustomerAdaptor.close();
                                if (mCur.getCount() > 0) {


                                    if(mBluetoothAdapter != null) {

                                        if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                                            if(!preferenceMangr.pref_getString("SelectedPrinterAddress").equals("")) {
                                                new AsyncPrintSalesBillwiseReport().execute();
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
                                    printpopup.dismiss();
                                    Toast toast = Toast.makeText(getApplicationContext(), "No Data Available", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
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
                        getsalesfilterdate = vardate;
                        salesdate.setText(vardate);
                        GetSalesList();
                    }
                };
                DatePickerDialog dpDialog = new DatePickerDialog(context, listener, year, month, day);
                dpDialog.show();
            }
        });

        //Logout process
        salesitemwiselogout.setOnClickListener(new View.OnClickListener() {
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
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack(null);
            }
        });


        //Dropdown values
        //company name
        txtcompanyname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetCompanyName();
            }
        });

        selectpaymenttype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                int index = parentView.getSelectedItemPosition();
                getpaymenttype = arrapaymenttype[index];
                GetSalesList();

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
                GetSalesList();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
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

    //Sales List
    public  void GetSalesList(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            saleslist.clear();
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetSalesListDB(getsaleslistdate,getfiltercompanycode,
                    getpaymenttype,getpaymentstatus,"0","0","0");
            if(Cur.getCount()>0) {
                for(int i=0;i<Cur.getCount();i++){
                    saleslist.add(new SalesListDetails(Cur.getString(3),Cur.getString(7),
                            Cur.getString(8),Cur.getString(20),
                            Cur.getString(21),Cur.getString(9),Cur.getString(14),
                            String.valueOf(Cur.getCount()-i),Cur.getString(11),Cur.getString(23),
                            Cur.getString(19),
                            Cur.getString(22),Cur.getString(10),
                            Cur.getString(18),Cur.getString(24),Cur.getString(15)
                            ,Cur.getString(16),Cur.getString(2),Cur.getString(17),
                            Cur.getString(0),Cur.getInt(29)));
                    Cur.moveToNext();
                }
                getdata = saleslist;
                adapter = new SalesBillWiseBaseAdapterList(context,saleslist);
                saleslistView.setAdapter(adapter);
            }else{
                adapter = new SalesBillWiseBaseAdapterList(context,saleslist);
                saleslistView.setAdapter(adapter);
                Toast toast = Toast.makeText(getApplicationContext(),"No Sales Available", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //Toast.makeText(getApplicationContext(),"No Sales Available",Toast.LENGTH_SHORT).show();

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
                Toast toast = Toast.makeText(getApplicationContext(),"No Area in this route", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //Toast.makeText(getApplicationContext(),"No Area in this route",Toast.LENGTH_SHORT).show();
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

    /*******END FILTER FUNCTIONALITY********/
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
                    companyshortname=String.valueOf(shortname[position]);
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
    public void goBack(View v) {
        LoginActivity.ismenuopen=true;
        Intent i = new Intent(context, MenuActivity.class);
        startActivity(i);
    }
    @Override
    public void onBackPressed() {
        goBack(null);
    }


    protected class AsyncPrintSalesBillwiseReport extends
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
                    billPrinted = (boolean) printData.GetSalesBillWiseReportPrint(SalesBillWiseReportActivity.this);
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
