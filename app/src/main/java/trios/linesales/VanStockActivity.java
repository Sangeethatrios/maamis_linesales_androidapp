package trios.linesales;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class VanStockActivity extends AppCompatActivity {
    public static ListView listvanstock;
    ArrayList<VanStockDetails> vanstocklist = new ArrayList<VanStockDetails>();
    public DecimalFormat df = new DecimalFormat("0.00");
    public Context context;
    ImageView goback,vanstocklistlogout;
    TextView txtvanstockdate,txtvanstock;
    private int year, month, day;
    private Calendar calendar;
    VanStockBaseAdapter adapter=null;
    ImageButton print;
    boolean deviceFound;
    public static String stockdate;
    String[] companycode,companyname,shortname;
    Dialog companydialog;
    ListView lv_CompanyList,lv_SubGroupList;
    TextView vancompany,vanitemsubgroup;
    Spinner selectitemstatus;
    public static String getlistcompanycode = "0",getlistitemsubgroupcode = "0";
    public static String getitemsatus="All Items";
    String[] arraitemstatus=new String[0];
    String[] itemsubgroupcode,itemsubgroupname,itemsubgroupnametamil;
    Dialog itemsubgroupdialog;
    //private PrintData printData;
    Dialog printpopup;

    BluetoothAdapter mBluetoothAdapter;
    public static PreferenceMangr preferenceMangr=null;
    ReceiveListener receiveListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_van_stock);

        context = this;

        listvanstock = (ListView) findViewById(R.id.listvanstock);
        vanstocklistlogout = (ImageView)findViewById(R.id.vanstocklistlogout);
        goback = (ImageView)findViewById(R.id.goback);
        txtvanstockdate = (TextView)findViewById(R.id.txtvanstockdate);
        txtvanstock = (TextView)findViewById(R.id.txtvanname);
        print = (ImageButton)findViewById(R.id.printbtn);
        vancompany = (TextView)findViewById(R.id.vancompany);
        selectitemstatus = (Spinner)findViewById(R.id.selectitemstatus);
        vanitemsubgroup =(TextView)findViewById(R.id.vanitemsubgroup);
        arraitemstatus = getResources().getStringArray(R.array.itemstatus);

        getlistcompanycode = "0";
        getlistitemsubgroupcode = "0";

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }catch (Exception e){
            Log.d("Bluetooth Adapter : ",e.toString());
        }
        try {
            preferenceMangr = new PreferenceMangr(context);
            receiveListener =  new ReceiveListener() {
                @Override
                public void onPtrReceive(Printer printer, int i, PrinterStatusInfo printerStatusInfo, String s) {
                    enablePrintButtons();
                }
            };
        }catch (Exception e){
            Log.d("Bluetooth Adapter : ",e.toString());
        }
        txtvanstock.setText(preferenceMangr.pref_getString("getvanname"));

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



        //Set  Date
        //Set Now Date
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        /*month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        //Call Date Functionality
        showmainDate(year, month+1, day);*/
        stockdate = preferenceMangr.pref_getString("getformatdate");
        txtvanstockdate.setText(preferenceMangr.pref_getString("getcurrentdatetime"));


        //Logout process
        vanstocklistlogout.setOnClickListener(new View.OnClickListener() {
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

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vanstocklist.size() > 0 ) {

                    printpopup = new Dialog(context);
                    printpopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    printpopup.setContentView(R.layout.printpopup);

                    TextView txtYesAction = (TextView) printpopup.findViewById(R.id.txtYesAction);
                    TextView txtNoAction = (TextView) printpopup.findViewById(R.id.txtNoAction);
                    txtYesAction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            try {
                                DataBaseAdapter objcustomerAdaptor = new DataBaseAdapter(context);
                                objcustomerAdaptor.open();
                                Cursor mCur = objcustomerAdaptor.GetVanStock(getlistcompanycode,getitemsatus,getlistitemsubgroupcode);
                                objcustomerAdaptor.close();
                                if (mCur.getCount() > 0) {

                                    if(mBluetoothAdapter != null) {

                                        if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                                            if(!preferenceMangr.pref_getString("SelectedPrinterAddress").equals("")) {
                                                disablePrintButtons();
                                                new AsyncPrintVanStockDetails().execute();
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
                                }
                            } catch (Exception e) {
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
                    Toast toast = Toast.makeText(getApplicationContext(), "No Data Available", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }



            }
        });
        //Goback process
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack(null);
            }
        });


        selectitemstatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                int index = parentView.getSelectedItemPosition();
                getitemsatus = arraitemstatus[index];
                GetVanStock();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        //Company List
        vancompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetListCompanyName();
            }
        });

        //Item Sub Group List
        vanitemsubgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetItemSubGroup();
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
    //Item sub group
    public  void GetItemSubGroup(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetOrderItemSubgroupDB("0");
            if(Cur.getCount()>0) {
                itemsubgroupcode = new String[Cur.getCount()];
                itemsubgroupname = new String[Cur.getCount()];
                itemsubgroupnametamil = new String[Cur.getCount()];
                for(int i=0;i<Cur.getCount();i++){
                    itemsubgroupcode[i] = Cur.getString(0);
                    itemsubgroupname[i] = Cur.getString(1);
                    itemsubgroupnametamil[i] = Cur.getString(2);
                    Cur.moveToNext();
                }

                itemsubgroupdialog = new Dialog(context);
                itemsubgroupdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                itemsubgroupdialog.setContentView(R.layout.itemsubgrouppopup);
                lv_SubGroupList = (ListView) itemsubgroupdialog.findViewById(R.id.lv_SubGroupList);
                ImageView close = (ImageView) itemsubgroupdialog.findViewById(R.id.close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemsubgroupdialog.dismiss();
                    }
                });
                ItemSubGroupAdapter adapter = new ItemSubGroupAdapter(context);
                lv_SubGroupList.setAdapter(adapter);
                itemsubgroupdialog.show();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(),"No item sub group in this item group", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //Toast.makeText(getApplicationContext(),"No item sub group in this item group",Toast.LENGTH_SHORT).show();
            }
        }  catch (Exception e){
            Log.i("GetItemSubGroup", e.toString());
        }
        finally {
            // this gets called even if there is an exception somewhere above
            if(objdatabaseadapter != null)
                objdatabaseadapter.close();
            if(Cur != null)
                Cur.close();
        }
    }

    //Get company List
    public  void GetListCompanyName(){
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
                CompanyListAdapter adapter = new CompanyListAdapter(context);
                lv_CompanyList.setAdapter(adapter);
                companydialog.show();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(),"No Area in this route",Toast.LENGTH_LONG);
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
    public  void GetVanStock(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            vanstocklist.clear();
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetVanStock(getlistcompanycode,getitemsatus,getlistitemsubgroupcode);
            if(Cur.getCount()>0) {
                for(int i=0;i<Cur.getCount();i++){
                    vanstocklist.add(new VanStockDetails(Cur.getString(0),Cur.getString(2),Cur.getString(3),
                            Cur.getString(4),Cur.getString(5),String.valueOf(i+1),Cur.getString(1),
                            Cur.getString(6),Cur.getString(11),Cur.getString(12)));

                    Cur.moveToNext();
                }

                adapter = new VanStockBaseAdapter(context, vanstocklist);
                listvanstock.setAdapter(adapter);
            }else{
                adapter = new VanStockBaseAdapter(context,vanstocklist);
                listvanstock.setAdapter(adapter);
                Toast toast = Toast.makeText(getApplicationContext(),"No Stock Available",Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

            }
        }  catch (Exception e){
            Log.i("Stocklist", e.toString());
        }
        finally {
            // this gets called even if there is an exception somewhere above
            if(objdatabaseadapter != null)
                objdatabaseadapter.close();
            if(Cur != null)
                Cur.close();
        }
    }
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
        stockdate=vardate;
        txtvanstockdate.setText(vardate);

    }
    //Company adapter
    public class CompanyListAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;

        CompanyListAdapter(Context c) {
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
                    vancompany.setText(String.valueOf(shortname[position]));
                    getlistcompanycode = companycode[position];
                    companydialog.dismiss();
                    GetVanStock();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView listcompanyname;

        }

    }

    //Item Sub Group Adapter
    public class ItemSubGroupAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;

        ItemSubGroupAdapter(Context c) {
            context = c;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return itemsubgroupcode.length;
        }

        @Override
        public Object getItem(int position) {
            return itemsubgroupcode[position];
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
                convertView = layoutInflater.inflate(R.layout.itemsubgrouppopuplist, parent, false);
                mHolder = new ViewHolder();
                try {
                    mHolder.listsubgroupname = (TextView) convertView.findViewById(R.id.listsubgroupname);
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
                if (!(String.valueOf(itemsubgroupnametamil[position])).equals("")
                        && !(String.valueOf(itemsubgroupnametamil[position])).equals("null")
                        && !(String.valueOf(itemsubgroupnametamil[position])).equals(null)) {
                    mHolder.listsubgroupname.setText(String.valueOf(itemsubgroupnametamil[position]));
                } else {
                    mHolder.listsubgroupname.setText(String.valueOf(itemsubgroupname[position]));
                }
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
                    if (!(String.valueOf(itemsubgroupnametamil[position])).equals("")
                            && !(String.valueOf(itemsubgroupnametamil[position])).equals("null")
                            && !(String.valueOf(itemsubgroupnametamil[position])).equals(null)) {
                        vanitemsubgroup.setText(String.valueOf(itemsubgroupnametamil[position] ));
                    } else {
                        vanitemsubgroup.setText(String.valueOf(itemsubgroupname[position] ));
                    }
                    getlistitemsubgroupcode = itemsubgroupcode[position];
                    itemsubgroupdialog.dismiss();
                    GetVanStock();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView listsubgroupname;

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

    protected class AsyncPrintVanStockDetails extends
            AsyncTask<String, JSONObject, Boolean> {
        Boolean billPrinted = false;
        JSONObject jsonObj = null;
        ProgressDialog loading;
        @Override
        protected Boolean doInBackground(String... params) {
            //final String finalGetreceipttransano = params[0];
            //final String financialyearcode = params[1];
            try {
                EpsonT20Printer epsonT20Printer = new EpsonT20Printer(VanStockActivity.this, VanStockActivity.this, receiveListener);
                deviceFound = epsonT20Printer.findBT();
                Log.d("deviceFound",String.valueOf(deviceFound));
                if (!deviceFound) {
                    /*Toast toast = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);*/
                    //printpopup.dismiss();
                    billPrinted = deviceFound;
                    //toast.show();
                } else {
                    billPrinted = (boolean) epsonT20Printer.GetVanStockPrint(VanStockActivity.this);
                    //printpopup.dismiss();
                }
                Log.d("billPrinted",String.valueOf(billPrinted));
            }
            catch (Exception e){
                /*Toast toast = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();*/
                //printpopup.dismiss();
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
