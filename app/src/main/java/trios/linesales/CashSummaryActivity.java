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
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;

import org.json.JSONObject;

import java.text.DecimalFormat;

public class CashSummaryActivity extends AppCompatActivity {

    public Context context;
    TextView summarytripadvance,firstcashbill,summaryfirstbill,
            secondcashbill,summarysecondbillno,summaryrecipt,summarytotal,cashdate;
    TextView txtheading1,txtadvanceamt,txtnetcashamt,txtexpenseamt,casinhandtotal,
            txttotalupiamt;
    public static String summaryschedulecode = "";
    DecimalFormat df = new DecimalFormat("0.00");
    LinearLayout  LLFirstCash,LLSecondCash;
    TextView summaryprintbtn,cashsummaryprintbtn;
    boolean deviceFound;
    ListView LVCashSummary;
    String[] getcashadavance,getcashsalesamount,getcashsalesreturnamount,
            getcashreceiptamount,getcashcompanyname,getcashexpenseamt,
            getcashupisalesamount,getcashupireceiptamount;
    ImageButton btngoback,btnlogout;
    //private PrintData printData;
    Dialog printpopup;

    BluetoothAdapter mBluetoothAdapter;
    public static PreferenceMangr preferenceMangr=null;
    ReceiveListener receiveListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_summary);

        context = this;

        summaryprintbtn = (TextView)findViewById(R.id.summaryprintbtn);
        cashsummaryprintbtn = (TextView)findViewById(R.id.cashsummaryprintbtn);
        cashdate = (TextView)findViewById(R.id.cashdate);
        txtadvanceamt = (TextView)findViewById(R.id.txtadvanceamt);
        LVCashSummary = (ListView)findViewById(R.id.LVCashSummary) ;
        txtnetcashamt = (TextView)findViewById(R.id.txtnetcashamt);
        txtexpenseamt = (TextView)findViewById(R.id.txtexpenseamt);
        casinhandtotal = (TextView)findViewById(R.id.casinhandtotal);
        btngoback = (ImageButton)findViewById(R.id.goback);
        btnlogout = (ImageButton)findViewById(R.id.cashlistlogout);
        txttotalupiamt = (TextView)findViewById(R.id.txttotalupiamt);

        btngoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack(null);
            }
        });


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
            Log.d("Bluetooth Adapter : ",e.toString());
        }

        /*summarytripadvance = (TextView)findViewById(R.id.summarytripadvance);
        firstcashbill = (TextView)findViewById(R.id.firstcashbill);
        summaryfirstbill = (TextView)findViewById(R.id.summaryfirstbill);
        secondcashbill = (TextView)findViewById(R.id.secondcashbill);
        summarysecondbillno = (TextView)findViewById(R.id.summarysecondbillno);
        summaryrecipt = (TextView)findViewById(R.id.summaryrecipt);
        summarytotal = (TextView)findViewById(R.id.summarytotal);
        cashdate = (TextView)findViewById(R.id.cashdate);
        LLFirstCash = (LinearLayout)findViewById(R.id.LLFirstCash);
        LLSecondCash = (LinearLayout)findViewById(R.id.LLSecondCash);
        summaryprintbtn = (FloatingActionButton)findViewById(R.id.summaryprintbtn);
        cashsummaryprintbtn = (FloatingActionButton)findViewById(R.id.cashsummaryprintbtn);


        DataBaseAdapter objdatabaseadapter = null;
        Cursor getcompanycode = null;
        Cursor getfirstcash = null;
        Cursor getsecondcash = null;
        double gettotal = 0;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            LoginActivity.getformatdate = objdatabaseadapter.GenCreatedDate();
            LoginActivity.getcurrentdatetime = objdatabaseadapter.GenCurrentCreatedDate();
            summaryschedulecode = objdatabaseadapter.GetScheduleCode();
            String gettripadvance = objdatabaseadapter.GetScheduleAdvance(summaryschedulecode);
            String getreceiptamt = objdatabaseadapter.GetReceiptAmountSummary(summaryschedulecode);
            summaryrecipt.setText(df.format(Double.parseDouble(getreceiptamt)));
            summarytripadvance.setText(df.format(Double.parseDouble(gettripadvance)));
            gettotal = gettotal+Double.parseDouble(getreceiptamt) +Double.parseDouble(gettripadvance);
            getcompanycode = objdatabaseadapter.GetAllCompanyCode();
            if(getcompanycode.getCount() > 0 ){
                for(int i=0;i<getcompanycode.getCount();i++) {
                    if(i==0) {
                        getfirstcash = objdatabaseadapter.GetFirstSales(summaryschedulecode,
                                getcompanycode.getString(0));
                        if (getfirstcash.getCount() > 0) {
                            LLFirstCash.setVisibility(View.VISIBLE);
                            for (int j = 0; j < getfirstcash.getCount(); j++) {
                                if(!getfirstcash.getString(1).equals("null") &&
                                        !getfirstcash.getString(1).equals("") &&
                                        !getfirstcash.getString(1).equals(null)) {
                                    LLFirstCash.setVisibility(View.VISIBLE);
                                    firstcashbill.setText(getcompanycode.getString(1)
                                            + " Cash Bill (" + getfirstcash.getString(1) + " to " + getfirstcash.getString(2) + ")");
                                    summaryfirstbill.setText(df.format(Double.parseDouble(getfirstcash.getString(0))));
                                    gettotal = gettotal + Double.parseDouble(getfirstcash.getString(0));
                                }else{
                                    LLFirstCash.setVisibility(View.GONE);
                                }
                            }
                        }else{
                            LLFirstCash.setVisibility(View.GONE);
                        }
                    }if(i==1) {
                        getsecondcash = objdatabaseadapter.GetFirstSales(summaryschedulecode,
                                getcompanycode.getString(0));
                        if (getsecondcash.getCount() > 0) {
                            LLSecondCash.setVisibility(View.VISIBLE);
                            for (int j = 0; j < getsecondcash.getCount(); j++) {
                                if(!getsecondcash.getString(1).equals("null") &&
                                        !getsecondcash.getString(1).equals("") &&
                                        !getsecondcash.getString(1).equals(null)) {
                                    LLSecondCash.setVisibility(View.VISIBLE);
                                    secondcashbill.setText(getcompanycode.getString(1)
                                            + " Cash Bill (" + getsecondcash.getString(1) + " to " + getsecondcash.getString(2) + ")");
                                    summarysecondbillno.setText(df.format(Double.parseDouble(getsecondcash.getString(0))));
                                    gettotal = gettotal + Double.parseDouble(getsecondcash.getString(0));
                                }else{
                                    LLSecondCash.setVisibility(View.GONE);
                                }
                            }
                        }else{
                            LLSecondCash.setVisibility(View.GONE);
                        }
                    }
                    getcompanycode.moveToNext();
                }
            }
            summarytotal.setText(df.format(gettotal));
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
            if (getcompanycode != null)
                getcompanycode.close();
            if (getfirstcash != null)
                getfirstcash.close();
            if (getsecondcash != null)
                getsecondcash.close();

        }*/

        //Set date
        cashdate.setText(preferenceMangr.pref_getString("getcurrentdatetime"));




        summaryprintbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                printpopup = new Dialog(context);
                printpopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
                printpopup.setContentView(R.layout.printpopup);

                TextView txtYesAction = (TextView) printpopup.findViewById(R.id.txtYesAction);
                TextView txtNoAction = (TextView) printpopup.findViewById(R.id.txtNoAction);
                txtYesAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {

                            if(mBluetoothAdapter != null) {

                                if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {

                                    if(!preferenceMangr.pref_getString("SelectedPrinterAddress").equals("")) {
                                        disablePrintButtons();
                                        new AsyncPrintSummaryBillReport().execute();
                                        printpopup.dismiss();
                                    }else{
                                        Toast toast = Toast.makeText(getApplicationContext(), "Please select the bluetooth printer in app", Toast.LENGTH_LONG);
                                        //toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                        printpopup.dismiss();
                                    }
                                }else{
                                    Toast toast = Toast.makeText(getApplicationContext(), "Please turn on the bluetooth", Toast.LENGTH_LONG);
                                    //toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    printpopup.dismiss();
                                }
                            }else{
                                Toast toast = Toast.makeText(getApplicationContext(), "Bluetooth is not available.", Toast.LENGTH_LONG);
                                //toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                printpopup.dismiss();
                            }
                        }
                        catch (Exception e){
                            Toast toast = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                            //toast.setGravity(Gravity.CENTER, 0, 0);
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

                /*AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to print?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    dialog.dismiss();
                                    printData = new PrintData(context);
                                    deviceFound = printData.findBT();
                                    if (!deviceFound) {
                                        Toast toast = Toast.makeText(getApplicationContext(),"Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    } else {
                                        boolean billPrinted = false;
                                        billPrinted = (boolean) printData.GetCashSummaryReportPrintBill();
                                        if (!billPrinted) {
                                            Toast toast = Toast.makeText(getApplicationContext(),"Unable to connect to Bluetooth Printer!", Toast.LENGTH_LONG);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
                                            return;
                                        }
                                    }
                                } catch (Exception e) {
                                    Toast toast = Toast.makeText(getApplicationContext(),"Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
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
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();*/
            }
        });

        cashsummaryprintbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printpopup = new Dialog(context);
                printpopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
                printpopup.setContentView(R.layout.printpopup);

                TextView txtYesAction = (TextView) printpopup.findViewById(R.id.txtYesAction);
                TextView txtNoAction = (TextView) printpopup.findViewById(R.id.txtNoAction);
                txtYesAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*confirmation.setVisibility(View.GONE);
                        pleasewait.setVisibility(View.VISIBLE);*/

                        final String gettransactionnoprint = SalesListActivity.getsalesreviewtransactionno;
                        final String getfinyrcode =  SalesListActivity.getsalesreviewfinanicialyear;
                        final String finalGetSalestransano = gettransactionnoprint;
                        final  String financialyearcode = getfinyrcode;
                        try {

                            /*printData = new PrintData(context);
                            deviceFound = printData.findBT();

                            if (!deviceFound) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                printpopup.dismiss();
                                toast.show();
                            } else {

                               *//* boolean billPrinted = false;
                                billPrinted = (boolean) printData.CashSummaryReportPrintBill();
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
                                        disablePrintButtons();
                                        new AsyncPrintCashSummaryBillReport().execute();
                                        printpopup.dismiss();
                                    }else{
                                        Toast toast = Toast.makeText(getApplicationContext(), "Please select the bluetooth printer in app", Toast.LENGTH_LONG);
                                        //toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                        printpopup.dismiss();
                                    }
                                }else{
                                    Toast toast = Toast.makeText(getApplicationContext(), "Please turn on the bluetooth", Toast.LENGTH_LONG);
                                    //toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    printpopup.dismiss();
                                }
                            }else{
                                Toast toast = Toast.makeText(getApplicationContext(), "Bluetooth is not available.", Toast.LENGTH_LONG);
                                //toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                printpopup.dismiss();
                            }
                        }
                        catch (Exception e){
                            Toast toast = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                            //toast.setGravity(Gravity.CENTER, 0, 0);
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
                /*AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to print?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    dialog.dismiss();
                                    printData = new PrintData(context);
                                    deviceFound = printData.findBT();
                                    if (!deviceFound) {
                                        Toast toast = Toast.makeText(getApplicationContext(),"Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    } else {
                                        boolean billPrinted = false;
                                        billPrinted = (boolean) printData.CashSummaryReportPrintBill();
                                        if (!billPrinted) {
                                            Toast toast = Toast.makeText(getApplicationContext(),"Unable to connect to Bluetooth Printer!", Toast.LENGTH_LONG);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
                                            return;
                                        }
                                    }
                                } catch (Exception e) {
                                    Toast toast = Toast.makeText(getApplicationContext(),"Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
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
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();*/
            }
        });

        btnlogout.setOnClickListener(new View.OnClickListener() {
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

        //Set All details
        GetCashSummarydetails();
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

    public  void GetCashSummarydetails(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        Cursor mCusrCashDetails = null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            //LoginActivity.getformatdate = objdatabaseadapter.GenCreatedDate();
            //LoginActivity.getcurrentdatetime = objdatabaseadapter.GenCurrentCreatedDate();

            preferenceMangr.pref_putString("getformatdate",objdatabaseadapter.GenCreatedDate());
            preferenceMangr.pref_putString("getcurrentdatetime",objdatabaseadapter.GenCurrentCreatedDate());

            summaryschedulecode = objdatabaseadapter.GetScheduleCode();
            Cur = objdatabaseadapter.GetCashSummaryCompany();
            if(Cur.getCount()>0) {
                getcashadavance = new String[Cur.getCount()];
                getcashsalesamount = new String[Cur.getCount()];
                getcashsalesreturnamount = new String[Cur.getCount()];
                getcashreceiptamount = new String[Cur.getCount()];
                getcashcompanyname = new String[Cur.getCount()];
                getcashupisalesamount = new String[Cur.getCount()];
                getcashupireceiptamount = new String[Cur.getCount()];
                for(int j=0;j<Cur.getCount();j++) {
                    mCusrCashDetails = objdatabaseadapter.GetCashSummaryDatas(summaryschedulecode, Cur.getString(0));

                    txtadvanceamt.setText(df.format(mCusrCashDetails.getDouble(0)));
                    getcashexpenseamt = new String[1];
                    getcashexpenseamt[0] =String.valueOf(mCusrCashDetails.getDouble(5));

                    txtexpenseamt.setText("(-) "+df.format(  mCusrCashDetails.getDouble(5)));
                    for (int i = 0; i < mCusrCashDetails.getCount(); i++) {
                        getcashadavance[j] = String.valueOf(mCusrCashDetails.getDouble(0));
                        getcashsalesamount[j] = String.valueOf(mCusrCashDetails.getDouble(1));
                        getcashsalesreturnamount[j] = String.valueOf(mCusrCashDetails.getDouble(3));
                        getcashreceiptamount[j] = String.valueOf(mCusrCashDetails.getDouble(2));
                        getcashcompanyname[j] = mCusrCashDetails.getString(4);
                        getcashupisalesamount[j] = String.valueOf(mCusrCashDetails.getDouble(7));
                        getcashupireceiptamount[j] = String.valueOf(mCusrCashDetails.getDouble(6));
                        mCusrCashDetails.moveToNext();
                    }
                    Cur.moveToNext();
                }
                CashSummaryAdapter adapter = new CashSummaryAdapter(context);
                LVCashSummary.setAdapter(adapter);
            }else{
                Toast toast = Toast.makeText(getApplicationContext(), "No Data Available",Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }  catch (Exception e){
            Log.i("CashSummary", e.toString());
        }
        finally {
            // this gets called even if there is an exception somewhere above
            if(objdatabaseadapter != null)
                objdatabaseadapter.close();
            if(Cur != null)
                Cur.close();
        }
    }


    //Route Adapter
    public class CashSummaryAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;
        String[] alaphabet = {"B","C","D","E"};

        CashSummaryAdapter(Context c) {
            context = c;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return getcashadavance.length;
        }

        @Override
        public Object getItem(int position) {
            return getcashadavance[position];
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
        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder mHolder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.cashsummarylist, parent, false);
                mHolder = new ViewHolder();
                try {
                    mHolder.txtcashcompanyname = (TextView) convertView.findViewById(R.id.txtcashcompanyname);
                    mHolder.txtcashbysales = (TextView) convertView.findViewById(R.id.txtcashbysales);
                    mHolder.txtcashbyreceipt = (TextView) convertView.findViewById(R.id.txtcashbyreceipt);
                    mHolder.txtcashsalesreturn = (TextView) convertView.findViewById(R.id.txtcashsalesreturn);
                    mHolder.txtcashtotal = (TextView) convertView.findViewById(R.id.txtcashtotal);
                    mHolder.totaltxt = (TextView)convertView.findViewById(R.id.totaltxt);
                    mHolder.txtupibysales = (TextView) convertView.findViewById(R.id.txtupibysales);
                    mHolder.txtupibyreceipt = (TextView) convertView.findViewById(R.id.txtupibyreceipt);
                } catch (Exception e) {
                    Log.i("Route", e.toString());
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                }
                convertView.setTag(mHolder);
                convertView.setTag(R.id.txtcashcompanyname, mHolder.txtcashcompanyname);
                convertView.setTag(R.id.txtcashbysales, mHolder.txtcashbysales);
                convertView.setTag(R.id.txtcashbyreceipt, mHolder.txtcashbyreceipt);
                convertView.setTag(R.id.txtcashsalesreturn, mHolder.txtcashsalesreturn);
                convertView.setTag(R.id.txtcashtotal, mHolder.txtcashtotal);
                convertView.setTag(R.id.totaltxt, mHolder.totaltxt);
                convertView.setTag(R.id.txtupibysales, mHolder.txtupibysales);
                convertView.setTag(R.id.txtupibyreceipt, mHolder.txtupibyreceipt);
            } else {
                mHolder = (ViewHolder) convertView.getTag();
            }
            try {
                mHolder.txtcashcompanyname.setTag(position);
                mHolder.txtcashbysales.setTag(position);
                mHolder.txtcashbyreceipt.setTag(position);
                mHolder.txtcashsalesreturn.setTag(position);
                mHolder.txtcashtotal.setTag(position);
                mHolder.totaltxt.setTag(position);
                mHolder.txtupibysales.setTag(position);
                mHolder.txtupibyreceipt.setTag(position);

                String str = "<u>" + getcashcompanyname[position]+" </u>";
                if (((int)Build.VERSION.SDK_INT) >= 24){
                    mHolder.txtcashcompanyname.setText(Html.fromHtml(str, Html.FROM_HTML_MODE_COMPACT));
                }
                else{
                    mHolder.txtcashcompanyname.setText(Html.fromHtml(str));
                }

                //mHolder.txtcashcompanyname.setText(getcashcompanyname[position]);
                mHolder.txtcashbysales.setText(df.format(Double.parseDouble(getcashsalesamount[position])));
                mHolder.txtcashbyreceipt.setText(df.format(Double.parseDouble(getcashreceiptamount[position])));
                mHolder.txtupibysales.setText(df.format(Double.parseDouble(getcashupisalesamount[position])));
                mHolder.txtupibyreceipt.setText(df.format(Double.parseDouble(getcashupireceiptamount[position])));
                mHolder.txtcashsalesreturn.setText("(-) "+df.format(Double.parseDouble(getcashsalesreturnamount[position])));

                mHolder.totaltxt.setText("Total ("+alaphabet[position]+")");

                Double gettotal = (Double.parseDouble(getcashsalesamount[position]) +
                        Double.parseDouble(getcashreceiptamount[position]) +
                        Double.parseDouble(getcashupisalesamount[position]) +
                        Double.parseDouble(getcashupireceiptamount[position]))
                        - Double.parseDouble(getcashsalesreturnamount[position]);
                mHolder.txtcashtotal.setText(String.valueOf(df.format(gettotal)));
                double netamt = 0.0;
                double netupiamt = 0.0;
                for(int k=0;k<getcashsalesamount.length;k++){
                    netamt = netamt + ( Double.parseDouble(getcashsalesamount[k]) +
                            Double.parseDouble(getcashreceiptamount[k]) +
                            Double.parseDouble(getcashupisalesamount[k]) +
                            Double.parseDouble(getcashupireceiptamount[k]))
                            - Double.parseDouble(getcashsalesreturnamount[k]);
                    netupiamt = netupiamt + (Double.parseDouble(getcashupisalesamount[k]) +
                            Double.parseDouble(getcashupireceiptamount[k]));

                }
                netamt = netamt + Double.parseDouble(getcashadavance[0]);
                txtnetcashamt.setText(String.valueOf(df.format(netamt)));

                txttotalupiamt.setText("(-) "+String.valueOf(df.format(netupiamt)));
                double cashinhand = 0.0;
                cashinhand = netamt - ( netupiamt + (Double.parseDouble(getcashexpenseamt[0]) ));
                casinhandtotal.setText(String.valueOf(df.format(cashinhand)));

            } catch (Exception e) {
                Log.i("Summary", e.toString());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }

            return convertView;
        }

        private class ViewHolder {
            private TextView txtcashcompanyname,txtcashbysales,
                    txtcashbyreceipt,txtcashsalesreturn,txtcashtotal,totaltxt,txtupibysales,txtupibyreceipt;

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


    protected class AsyncPrintSummaryBillReport extends
            AsyncTask<String, JSONObject, Boolean> {
        Boolean billPrinted = false;
        JSONObject jsonObj = null;
        ProgressDialog loading;
        @Override
        protected Boolean doInBackground(String... params) {
            //final String finalGetreceipttransano = params[0];
            //final String financialyearcode = params[1];
            try {
                EpsonT20Printer epsonT20Printer = new EpsonT20Printer(CashSummaryActivity.this, CashSummaryActivity.this, receiveListener);
                deviceFound = epsonT20Printer.findBT();
                Log.d("deviceFound",String.valueOf(deviceFound));
                if (!deviceFound) {
                    billPrinted = deviceFound;
                } else {
                    billPrinted = (boolean) epsonT20Printer.GetCashSummaryReportPrintBill(CashSummaryActivity.this);
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

    protected class AsyncPrintCashSummaryBillReport extends
            AsyncTask<String, JSONObject, Boolean> {
        Boolean billPrinted = false;
        JSONObject jsonObj = null;
        ProgressDialog loading;
        @Override
        protected Boolean doInBackground(String... params) {
            //final String finalGetreceipttransano = params[0];
            //final String financialyearcode = params[1];
            try {
                EpsonT20Printer epsonT20Printer = new EpsonT20Printer(CashSummaryActivity.this, CashSummaryActivity.this, receiveListener);
                deviceFound = epsonT20Printer.findBT();
                Log.d("deviceFound",String.valueOf(deviceFound));
                if (!deviceFound) {
                    billPrinted = deviceFound;
                } else {
                    billPrinted = (boolean) epsonT20Printer.CashSummaryReportPrintBill(CashSummaryActivity.this);
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
        summaryprintbtn.setEnabled(true);
        cashsummaryprintbtn.setEnabled(true);
    }

    public void disablePrintButtons() {
        summaryprintbtn.setEnabled(false);
        cashsummaryprintbtn.setEnabled(false);
    }
}
