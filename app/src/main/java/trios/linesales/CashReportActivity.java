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
import android.os.StrictMode;

import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CashReportActivity extends AppCompatActivity {
    ImageButton goback ,cashlistlogout;
    Context context;
    TextView toolbar_title;
    public static  TextView txtsalescash,txtreceiptcash,txtadvancecash,
            txttotalcash,txtexpenseamt,txtsalesreturnamt,txttotalexpense,txtcashinhand,cashdate,
            txtstartingkm,txtendingkm,totlakm;
    ListView lv_denomination;
    public static String getsubtotalval;
    TextView subtotalval,remaingamtval;
    FloatingActionButton closecash;
    public static  String[]  qty,value;

    public static  String[] Values,ValuesCode ;
    public static  String[] QtyValues ;
    public static  String[] TotalValues;
    public static  String[] oldQtyValues ;
    Dialog opencashdialog,pindialog;
    FloatingActionButton printbtn;
    public static String getschedulecode="0";
    DecimalFormat dft = new DecimalFormat("0.00");
    boolean networkstate;
    NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
    public static String getsalescash="0";
    public static String getsalesreturn="0";
    public static String getadvance="0";
    public static String getreceiptcash="0";
    public static String getexpense="0";
    public static String getcashinhand="0";
    public static String getdenominationcash="0",gettotalcash = "0",
    gettotalexp = "0";
    EditText edittxtendingkm;
    Button btnSubmitpin;
    Pinview pinview;
    boolean deviceFound;
    boolean getdenominationsave=false;
    String getsalesclose = "0",getcashclose="0";
    Double getdenominationcount=0.0;
    public static String getstaticcashroutename = "";
    String getstartingkm = "0";
    double resultval = 0.0;
    boolean isdencount=false;
    boolean iscashcount=false;
    //private PrintData printData;
    Dialog printpopup;

    BluetoothAdapter mBluetoothAdapter;
    public static PreferenceMangr preferenceMangr=null;
    String checkclosecount="0";
    boolean isValueChanged=false;
    ReceiveListener receiveListener = null;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_report);
        context = this;
        goback = (ImageButton)findViewById(R.id.goback);
        toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        cashlistlogout = (ImageButton)findViewById(R.id.cashlistlogout);
        closecash = (FloatingActionButton)findViewById(R.id.closecash);

        txtsalescash = (TextView)findViewById(R.id.txtsalescash);
        txtreceiptcash = (TextView) findViewById(R.id.txtreceiptcash);
        txtadvancecash = (TextView) findViewById(R.id.txtadvancecash);
        txttotalcash = (TextView) findViewById(R.id.txttotalcash);
        txtexpenseamt = (TextView) findViewById(R.id.txtexpenseamt);
        txtsalesreturnamt = (TextView)findViewById(R.id.txtsalesreturnamt);
        txttotalexpense = (TextView) findViewById(R.id.txttotalexpense);
        txtcashinhand = (TextView)findViewById(R.id.txtcashinhand);
        txtstartingkm = (TextView)findViewById(R.id.startingkm);
        txtendingkm = (TextView)findViewById(R.id.endingkm);
        totlakm = (TextView)findViewById(R.id.totlakm);
        printbtn = (FloatingActionButton)findViewById(R.id.printbtn);
        cashdate = (TextView)findViewById(R.id.cashdate);

        try {
            preferenceMangr = new PreferenceMangr(context);
            receiveListener = new ReceiveListener() {
                @Override
                public void onPtrReceive(Printer printer, int i, PrinterStatusInfo printerStatusInfo, String s) {
                    enablePrintButtons();
                }
            };
        }catch (Exception e){
            Log.d("Preference Manager : ",e.toString());
        }

        DataBaseAdapter objdatabaseadapter = null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            getschedulecode = objdatabaseadapter.GetScheduleCode();

            //LoginActivity.getformatdate = objdatabaseadapter.GenCreatedDate();
           // LoginActivity.getcurrentdatetime = objdatabaseadapter.GenCurrentCreatedDate();

            preferenceMangr.pref_putString("getformatdate",objdatabaseadapter.GenCreatedDate());
            preferenceMangr.pref_putString("getcurrentdatetime",objdatabaseadapter.GenCurrentCreatedDate());

            getcashclose = objdatabaseadapter.GetCashCloseCount(getschedulecode);
            getdenominationcount = objdatabaseadapter.GetDenominationCount(getschedulecode);
            if (!getcashclose.equals("0") && !getcashclose.equals("null") &&
                    !getcashclose.equals("") && !getcashclose.equals(null)) {
                closecash.setVisibility(View.GONE);
            } else {
                closecash.setVisibility(View.VISIBLE);
            }

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
            //DataBaseAdapter objdatabaseadapter =  new DataBaseAdapter(context);
            objdatabaseadapter.open();
            //Get cash close Count
            ScheduleActivity.getschedulecount = objdatabaseadapter.GetScheduleCount();
            preferenceMangr.pref_putString("schedule_getschedulecount",objdatabaseadapter.GetScheduleCount());

            String getschedulecode = objdatabaseadapter.GetScheduleCode();
            //Get Cash close Count
            ScheduleActivity.getcashclosecount = objdatabaseadapter.GetCashClose(getschedulecode);
            preferenceMangr.pref_putString("schedule_getcashclosecount",objdatabaseadapter.GetCashClose(getschedulecode));

            //Get sales close Count
            ScheduleActivity.getsalesclosecount = objdatabaseadapter.GetSalesClose(getschedulecode);
            preferenceMangr.pref_putString("schedule_getsalesclosecount",objdatabaseadapter.GetSalesClose(getschedulecode));

            MenuActivity.getsalesclosecount = objdatabaseadapter.GetSalesClose(getschedulecode);
            preferenceMangr.pref_putString("getsalesclosecount",objdatabaseadapter.GetSalesClose(getschedulecode));

            ScheduleActivity.getreciptcount = objdatabaseadapter.GetReceiptCount(getschedulecode);
            preferenceMangr.pref_putString("schedule_getreciptcount",objdatabaseadapter.GetReceiptCount(getschedulecode));

            ScheduleActivity.getexpensecount = objdatabaseadapter.GetExpenseCount(getschedulecode);
            preferenceMangr.pref_putString("schedule_getexpensecount",objdatabaseadapter.GetExpenseCount(getschedulecode));
        }catch (Exception e){

        }

        if(LoginActivity.iscash){
            getsalesclose = preferenceMangr.pref_getString("schedule_getsalesclosecount");
        }else{
            getsalesclose = preferenceMangr.pref_getString("getsalesclosecount");
        }

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }catch (Exception e){
            Log.d("Bluetooth Adapter : ",e.toString());
        }

        /*if(LoginActivity.iscash){
            getsalesclose = ScheduleActivity.getsalesclosecount;
            getcashclose = ScheduleActivity.getcashclosecount;
            getcashclose = objdatabaseadapter.GetCashCloseCount(getschedulecode);
            if(getdenominationcount>0){
                isdencount=false;
                printbtn.setVisibility(View.VISIBLE);
                closecash.setVisibility(View.GONE);
            }else{
                isdencount=true;
                printbtn.setVisibility(View.GONE);
                closecash.setVisibility(View.VISIBLE);
            }

            if (!getcashclose.equals("0") && !getcashclose.equals("null") &&
                    !getcashclose.equals("") && !getcashclose.equals(null)) {
                iscashcount=false;
                closecash.setVisibility(View.GONE);
                printbtn.setVisibility(View.VISIBLE);
            } else {
                iscashcount=false;
                closecash.setVisibility(View.VISIBLE);
                printbtn.setVisibility(View.GONE);
            }
        }else{*/
           /* getsalesclose = MenuActivity.getsalesclosecount;
            getcashclose = MenuActivity.getcashclosecount;*/
            /*if(!getsalesclose.equals("0") && !getsalesclose.equals("null") &&
                    !getsalesclose.equals("") && !getsalesclose.equals(null)){
                closecash.setVisibility(View.GONE);
            }else{
                closecash.setVisibility(View.VISIBLE);
            }*/
            if(getdenominationcount>0){
                isdencount=false;
                printbtn.setVisibility(View.VISIBLE);
                closecash.setVisibility(View.GONE);
            }else{
                isdencount=true;
                printbtn.setVisibility(View.GONE);
                closecash.setVisibility(View.VISIBLE);
            }

            if (!getcashclose.equals("0") && !getcashclose.equals("null") &&
                    !getcashclose.equals("") && !getcashclose.equals(null)) {
                iscashcount=false;
                closecash.setVisibility(View.GONE);
                printbtn.setVisibility(View.VISIBLE);
            } else {
                iscashcount=false;
                closecash.setVisibility(View.VISIBLE);
                printbtn.setVisibility(View.GONE);
            }
        //}

        //if(getdenominationsave) {

        //}else{
       //     closecash.setVisibility(View.GONE);
       // }


        cashdate.setText(preferenceMangr.pref_getString("getcurrentdatetime"));

        subtotalval = (TextView) findViewById(R.id.subtotalval);
        remaingamtval = (TextView)findViewById(R.id.remaingamtval);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack(null);
            }
        });
        //Get cash amount
        GetCashAndExpenseAmount();

        closecash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((!getsalesclose.equals("0") && !getsalesclose.equals("null") &&
                        !getsalesclose.equals("") && !getsalesclose.equals(null)) ||  (preferenceMangr.pref_getString("getbusiness_type").equals("2")) ) {
                   // if (Double.parseDouble(getdenominationcash) > 0) {
                    opencashdialog = new Dialog(context);
                    opencashdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    opencashdialog.setContentView(R.layout.closecash);
                    ImageView closepopup = (ImageView) opencashdialog.findViewById(R.id.closepopup);
                    TextView cashyes = (TextView) opencashdialog.findViewById(R.id.cashyes);
                    TextView cashno = (TextView) opencashdialog.findViewById(R.id.cashno);
                    TextView startingkm = (TextView) opencashdialog.findViewById(R.id.startingkm);
                    final TextView totalkm = (TextView) opencashdialog.findViewById(R.id.totalkm);
                    final RadioButton radio_yes = (RadioButton) opencashdialog.findViewById(R.id.radio_yes);
                    final RadioButton radio_no = (RadioButton) opencashdialog.findViewById(R.id.radio_no);
                    final RadioButton radio_expyes = (RadioButton) opencashdialog.findViewById(R.id.radio_expyes);
                    final RadioButton radio_expno = (RadioButton) opencashdialog.findViewById(R.id.radio_expno);

                    edittxtendingkm = (EditText) opencashdialog.findViewById(R.id.edittxtendingkm);
                    startingkm.setText("Starting " + getstartingkm + " km");

                    if(preferenceMangr.pref_getString("getbusiness_type").equals("2")){
                        edittxtendingkm.setEnabled(false);
                        edittxtendingkm.setBackgroundResource(R.drawable.editbackgroundlightgray);
                    }

                    closepopup.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            opencashdialog.dismiss();
                        }
                    });
                    cashno.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            opencashdialog.dismiss();
                        }
                    });
                    edittxtendingkm.addTextChangedListener(new TextWatcher() {

                        @Override
                        public void afterTextChanged(Editable s) {
                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start,
                                                      int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start,
                                                  int before, int count) {
                            if (s.length() != 0) {
                                if (!getstartingkm.equals("") && !getstartingkm.equals("null") && !getstartingkm.equals(".")
                                        && !edittxtendingkm.getText().toString().equals("") &&
                                        !edittxtendingkm.getText().toString().equals("null") &&
                                        !edittxtendingkm.getText().toString().equals(".")) {
                                    double res = Double.parseDouble(edittxtendingkm.getText().toString())
                                            - Double.parseDouble(getstartingkm);
                                    resultval = res;
                                    totalkm.setText(String.valueOf("Total \n" + res + " km"));
                                }
                            } else {
                                totalkm.setText(String.valueOf("Total \n 0.00 km"));
                                resultval = 0.0;
                            }

                        }
                    });

                    cashyes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String getpaidparties = "";
                            String getexpenseentries = "";
                            if(!preferenceMangr.pref_getString("getbusiness_type").equals("2")) {
                                if (!radio_yes.isChecked() && !radio_no.isChecked()) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Please select yes or no", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    return;
                                }
                                if (!radio_expyes.isChecked() && !radio_expno.isChecked()) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Please select yes or no", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    return;
                                }
                            }
                            if (radio_yes.isChecked()) {
                                getpaidparties = "yes";
                            } else {
                                getpaidparties = "no";
                            }
                            if (radio_expyes.isChecked()) {
                                getexpenseentries = "yes";
                            } else {
                                getexpenseentries = "no";
                            }
                            if(edittxtendingkm.getText().toString().trim().equals("")){
                                edittxtendingkm.setText("0");
                            }
                            if(!preferenceMangr.pref_getString("getbusiness_type").equals("2")) {
                                if (edittxtendingkm.getText().toString().trim().equals("") || edittxtendingkm.getText().toString().trim().equals(".") ||
                                edittxtendingkm.getText().toString().equals("0")) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter valid ending km", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    //Toast.makeText(getApplicationContext(),"Please enter valid ending km",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (Double.parseDouble(edittxtendingkm.getText().toString())<=0) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter valid  ending km", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    return;
                                }

                                if (resultval < 0) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter valid ending km", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    //Toast.makeText(getApplicationContext(),"Please enter valid ending km",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }

                            DataBaseAdapter objdatabaseadapter = null;
                            Cursor getschedulekm1 = null;
                            try{

                                objdatabaseadapter = new DataBaseAdapter(context);
                                objdatabaseadapter.open();
                                String getresult="";
                                getresult = objdatabaseadapter.InsertCashClose(getschedulecode,
                                        edittxtendingkm.getText().toString(), getpaidparties, getexpenseentries);
                                if (getresult.equals("success")) {
                                    Toast toast = Toast.makeText(getApplicationContext(),"Saved Successfully", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    closeKeyboard();

                                    MenuActivity.getcashclosecount = objdatabaseadapter.GetCashCloseCount(getschedulecode);
                                    preferenceMangr.pref_putString("getcashclosecount",objdatabaseadapter.GetCashCloseCount(getschedulecode));

                                    getcashclose = objdatabaseadapter.GetCashCloseCount(getschedulecode);
                                    if (!getcashclose.equals("0") && !getcashclose.equals("null") &&
                                            !getcashclose.equals("") && !getcashclose.equals(null)) {
                                        closecash.setVisibility(View.GONE);
                                    } else {
                                        closecash.setVisibility(View.VISIBLE);
                                    }
                                    printbtn.setImageDrawable(getResources().getDrawable(R.drawable.indian_rupee));
                                    printbtn.setVisibility(View.VISIBLE);

                                    double startkm = 0;
                                    double endkm =0;
                                    getschedulekm1 = objdatabaseadapter.GetScheduleKM(getschedulecode);
                                    if (getschedulekm1.getCount() > 0) {
                                        startkm = getschedulekm1.getFloat(0);
                                        endkm = getschedulekm1.getFloat(1);
                                    } else {
                                        startkm = 0;
                                        endkm = 0;
                                    }
                                    txtstartingkm.setText("Start km " + String.valueOf(startkm));
                                    txtendingkm.setText("End km " + String.valueOf(endkm));
                                    Double caltotalkm =endkm - startkm;
                                    totlakm.setText("Total \n" + String.valueOf(caltotalkm) + " km ");
                                    isdencount=false;
                                    iscashcount = false;
                                    //Get currency list
                                    GetDenominationList();

                                    //Backupdb
                                    DataBaseAdapter mDbHelper1 = new DataBaseAdapter(context);
                                    mDbHelper1.open();
                                    String filepath = mDbHelper1.udfnBackupdb(context);
                                    mDbHelper1.close();

                                    opencashdialog.dismiss();
                                }else{
                                    Toast toast = Toast.makeText(getApplicationContext(),"Error in saving", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                            }catch (Exception e) {
                                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                mDbErrHelper.open();
                                String geterrror = e.toString();
                                mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                mDbErrHelper.close();
                            } finally {
                                if (objdatabaseadapter != null)
                                    objdatabaseadapter.close();
                                if (getschedulekm1 != null)
                                    getschedulekm1.close();
                            }



                        }
                    });
                    opencashdialog.show();

                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Please close the sales", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    //Toast.makeText(getApplicationContext(), "Please close the sales", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        lv_denomination = (ListView) findViewById(R.id.lv_denomination);
        lv_denomination.setItemsCanFocus(true);

        cashlistlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        //Get currency list
        GetDenominationList();

        //Print button action
        printbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if((!getsalesclose.equals("0") && !getsalesclose.equals("null") &&
                        !getsalesclose.equals("") && !getsalesclose.equals(null)) ||  (preferenceMangr.pref_getString("getbusiness_type").equals("2")) ) {

                    if(!Utilities.isNullOrEmpty(getcashinhand) && Double.parseDouble(getcashinhand) > 0 ){
                        if (Double.parseDouble(getdenominationcash) > 0) {

                            if(getdenominationcount>0 && Integer.parseInt(checkclosecount) > 0){
                                if(Integer.parseInt(checkclosecount) == 0 ){
                                    showPinEnterDialogForCashClose();
                                }/*else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                                builder.setMessage("Are you sure you want to print?")
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                try {
                                                    *//*printData = new PrintData(context);
                                                    deviceFound = printData.findBT();
                                                    if (!deviceFound) {
                                                        Toast toast = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                                        toast.show();
                                                        //Toast.makeText(context, "Please connect to the Bluetooth Printer!", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        *//**//*boolean billPrinted = false;
                                                        billPrinted = (boolean) printData.GetCashReportPrintBill();
                                                        if (!billPrinted) {
                                                            Toast toast = Toast.makeText(getApplicationContext(), "Unable to connect to Bluetooth Printer!", Toast.LENGTH_LONG);
                                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                                            toast.show();
                                                            //Toast.makeText(context, "Unable to connect to Bluetooth Printer!", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }*//**//*

                                                    }*//*

                                                    if(mBluetoothAdapter != null) {

                                                        if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                                                            if(!preferenceMangr.pref_getString("SelectedPrinterAddress").equals("")) {
                                                                new AsyncCashReportBill().execute();

                                                            }else{
                                                                Toast toast = Toast.makeText(getApplicationContext(), "Please select the bluetooth printer in app", Toast.LENGTH_LONG);
                                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                                toast.show();

                                                            }
                                                        }else{
                                                            Toast toast = Toast.makeText(getApplicationContext(), "Please turn on the bluetooth", Toast.LENGTH_LONG);
                                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                                            toast.show();

                                                        }
                                                    }else{
                                                        Toast toast = Toast.makeText(getApplicationContext(), "Bluetooth is not available.", Toast.LENGTH_LONG);
                                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                                        toast.show();

                                                    }
                                                } catch (Exception e) {
                                                    Toast toast = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
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
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();

                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }*/
                            }
                            if(getdenominationcount ==0 || (getdenominationcount > 0 && Integer.parseInt(checkclosecount) == 0)){
                                AlertDialog.Builder conformationDialog = new AlertDialog.Builder(context);
                                conformationDialog.setTitle("Confirmation");
                                conformationDialog.setIcon(R.mipmap.ic_van);
                                conformationDialog.setMessage("Are you sure you want to close the cash ?");
                                conformationDialog.setCancelable(false);
                                conformationDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i1) {
                                        //if(getdenominationcount == 0 || (getdenominationcount > 0 && Integer.parseInt(checkclosecount) == 0))  {
                                        boolean isdeleted = false;
                                        if (ValuesCode.length > 0) {
                                            String getresult = "";
                                            DataBaseAdapter objdatabaseadapter = null;
                                            objdatabaseadapter = new DataBaseAdapter(context);
                                            objdatabaseadapter.open();

                                            for (int i = 0; i < ValuesCode.length; i++) {
                                                if (!QtyValues[i].equals("") && !QtyValues[i].equals("0")
                                                        && !QtyValues[i].equals("null") && !TotalValues[i].equals("")
                                                        && !TotalValues[i].equals("0")) {

                                                } else {
                                                    TotalValues[i] = "0";
                                                    QtyValues[i] = "0";
                                                }
                                                try {
                                                    String getdeleteresult = "0";
                                                    if (!isdeleted) {
                                                        getdeleteresult = objdatabaseadapter.DeleteDenomination(getschedulecode);
                                                    }
                                                    if (getdeleteresult.equals("success")) {
                                                        isdeleted = true;
                                                    }
                                                    //Cash report denomination details
                                                    getresult = objdatabaseadapter.InsertDenomination(getschedulecode, ValuesCode[i], QtyValues[i], TotalValues[i]);
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
                                            GetDenominationList();
                                            if (getresult.equals("success")) {
                                                String getcashresult = objdatabaseadapter.InsertCashReport(getschedulecode, getsalescash, getsalesreturn,
                                                        getadvance, getreceiptcash, getexpense, getcashinhand, getdenominationcash);
                                                if (getcashresult.equals("success")) {
                                                    Toast toast = Toast.makeText(getApplicationContext(),"Saved Successfully", Toast.LENGTH_LONG);
                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                    toast.show();

                                                    pindialog = new Dialog(context);
                                                    pindialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                    pindialog.setContentView(R.layout.cashclosevalidatepinumber);
                                                    ImageView closepopup = (ImageView) pindialog.findViewById(R.id.closepopup);
                                                    btnSubmitpin = (Button) pindialog.findViewById(R.id.btnSubmitpin);
                                                    pinview = (Pinview) pindialog.findViewById(R.id.pinview);

                                                    closepopup.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            pindialog.dismiss();
                                                        }
                                                    });
                                                    btnSubmitpin.setText("Close Cash");
                                                    btnSubmitpin.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {

                                                            DataBaseAdapter objdatabaseadapter = null;
                                                            Cursor getschedulekm1 = null;

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
                                                                    //Cash close details
                                                                    getresult = objdatabaseadapter.UpdateCashClose(getschedulecode);
                                                                    if (getresult.equals("success")) {
                                                                        pindialog.dismiss();
                                                                        Toast toast = Toast.makeText(getApplicationContext(), "Cash Closed Successfully", Toast.LENGTH_LONG);
                                                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                                                        toast.show();
                                                                        closeKeyboard();
                                                                        //Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_SHORT).show();
                                                                        MenuActivity.getcashclosecount = objdatabaseadapter.GetCashCloseCount(getschedulecode);
                                                                        preferenceMangr.pref_putString("getcashclosecount",objdatabaseadapter.GetCashCloseCount(getschedulecode));

                                                                        getcashclose = objdatabaseadapter.GetCashCloseCount(getschedulecode);
                                                                        getdenominationcount = objdatabaseadapter.GetDenominationCount(getschedulecode);
                                                                        double startkm = 0;
                                                                        double endkm = 0;
                                                                        getschedulekm1 = objdatabaseadapter.GetScheduleKM(getschedulecode);
                                                                        if (getschedulekm1.getCount() > 0) {
                                                                            startkm = getschedulekm1.getFloat(0);
                                                                            endkm = getschedulekm1.getFloat(1);
                                                                        } else {
                                                                            startkm = 0;
                                                                            endkm = 0;
                                                                        }
                                                                        txtstartingkm.setText("Start km " + String.valueOf(startkm));
                                                                        txtendingkm.setText("End km " + String.valueOf(endkm));
                                                                        Double caltotalkm = endkm - startkm;
                                                                        totlakm.setText("Total \n" + String.valueOf(caltotalkm) + " km ");
                                                                        isdencount=false;
                                                                        iscashcount = false;

                                                                        networkstate = isNetworkAvailable();
                                                                        if (networkstate == true) {
                                                                            new AsyncCloseCashDetails().execute();
                                                                        }
                                                                        //Get currency list
                                                                        GetDenominationList();

                                                                        //getdenominationcount = objdatabaseadapter.GetDenominationCount(getschedulecode);

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
                                                                                                new AsyncCashReportBill().execute();
                                                                                                printpopup.dismiss();
                                                                                            }else{
                                                                                                Toast toast = Toast.makeText(getApplicationContext(), "Please select the bluetooth printer in app", Toast.LENGTH_LONG);
                                                                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                                                                toast.show();
                                                                                                printpopup.dismiss();
                                                                                                finish();
                                                                                            }
                                                                                        }else{
                                                                                            Toast toast = Toast.makeText(getApplicationContext(), "Please turn on the bluetooth", Toast.LENGTH_LONG);
                                                                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                                                                            toast.show();
                                                                                            printpopup.dismiss();
                                                                                            finish();
                                                                                        }
                                                                                    }else{
                                                                                        Toast toast = Toast.makeText(getApplicationContext(), "Bluetooth is not available.", Toast.LENGTH_LONG);
                                                                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                                                                        toast.show();
                                                                                        printpopup.dismiss();
                                                                                        finish();
                                                                                    }
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
                                                                                    finish();
                                                                                }
                                                                            }
                                                                        });

                                                                        txtNoAction.setOnClickListener(new View.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(View v) {
                                                                                printpopup.dismiss();
                                                                                finish();
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
                                                                                printData = new PrintData(context);
                                                                                deviceFound = printData.findBT();
                                                                                if (!deviceFound) {
                                                                                    Toast toast = Toast.makeText(getApplicationContext(),"Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                                                    toast.show();

                                                                                    //Toast.makeText(context, "Please connect to the Bluetooth Printer!", Toast.LENGTH_SHORT).show();
                                                                                } else {
                                                                                    boolean billPrinted = false;
                                                                                    billPrinted = (boolean) printData.GetCashReportPrintBill();
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
                                                                                //Toast.makeText(context, "Please connect to the Bluetooth Printer!", Toast.LENGTH_SHORT).show();
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
                                                                    //Toast.makeText(getApplicationContext(), "Please enter correct pin", Toast.LENGTH_SHORT).show();
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
                                            }
                                            networkstate = isNetworkAvailable();
                                            if (networkstate == true) {
                                                new AsyncCashReportDetails().execute();
                                            }
                                        }
                                        //}
                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i1) {
                                        dialogInterface.cancel();
                                    }
                                });
                                AlertDialog conformationAlert = conformationDialog.create();
                                conformationAlert.show();

                            }else{
                                String checkclosecount  ="0";
                                DataBaseAdapter objdatabaseadapter1 = null;
                                try{
                                    objdatabaseadapter1 = new DataBaseAdapter(context);
                                    objdatabaseadapter1.open();
                                    checkclosecount = objdatabaseadapter1.GetCashClose(getschedulecode);

                                }catch (Exception e) {
                                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                    mDbErrHelper.open();
                                    String geterrror = e.toString();
                                    mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                    mDbErrHelper.close();
                                } finally {
                                    if (objdatabaseadapter1 != null)
                                        objdatabaseadapter1.close();
                                }

                                if(Integer.parseInt(checkclosecount) == 0 ){
                                    pindialog = new Dialog(context);
                                    pindialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    pindialog.setContentView(R.layout.cashclosevalidatepinumber);
                                    pindialog.setCancelable(false);
                                    ImageView closepopup = (ImageView) pindialog.findViewById(R.id.closepopup);
                                    btnSubmitpin = (Button) pindialog.findViewById(R.id.btnSubmitpin);
                                    pinview = (Pinview) pindialog.findViewById(R.id.pinview);

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
                                            Cursor getschedulekm1 = null;

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
                                                    //Cash close details
                                                    getresult = objdatabaseadapter.UpdateCashClose(getschedulecode);
                                                    if (getresult.equals("success")) {
                                                        pindialog.dismiss();
                                                        Toast toast = Toast.makeText(getApplicationContext(), "Cash Closed Successfully", Toast.LENGTH_LONG);
                                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                                        toast.show();
                                                        //Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_SHORT).show();
                                                        MenuActivity.getcashclosecount = objdatabaseadapter.GetCashCloseCount(getschedulecode);
                                                        preferenceMangr.pref_putString("getcashclosecount",objdatabaseadapter.GetCashCloseCount(getschedulecode));

                                                        networkstate = isNetworkAvailable();
                                                        if (networkstate == true) {
                                                            new AsyncCloseCashDetails().execute();
                                                            new AsyncCashReportDetails().execute();
                                                        }
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
                                                    //Toast.makeText(getApplicationContext(), "Please enter correct pin", Toast.LENGTH_SHORT).show();
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
                                }else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                                    builder.setMessage("Are you sure you want to print?")
                                            .setCancelable(false)
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    try {
                                                    /*printData = new PrintData(context);
                                                    deviceFound = printData.findBT();
                                                    if (!deviceFound) {
                                                        Toast toast = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                                        toast.show();
                                                        //Toast.makeText(context, "Please connect to the Bluetooth Printer!", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        *//*boolean billPrinted = false;
                                                        billPrinted = (boolean) printData.GetCashReportPrintBill();
                                                        if (!billPrinted) {
                                                            Toast toast = Toast.makeText(getApplicationContext(), "Unable to connect to Bluetooth Printer!", Toast.LENGTH_LONG);
                                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                                            toast.show();
                                                            //Toast.makeText(context, "Unable to connect to Bluetooth Printer!", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }*//*

                                                    }*/

                                                        if(mBluetoothAdapter != null) {

                                                            if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                                                                if(!preferenceMangr.pref_getString("SelectedPrinterAddress").equals("")) {
                                                                    disablePrintButtons();
                                                                    new AsyncCashReportBill().execute();

                                                                }else{
                                                                    Toast toast = Toast.makeText(getApplicationContext(), "Please select the bluetooth printer in app", Toast.LENGTH_LONG);
                                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                                    toast.show();

                                                                }
                                                            }else{
                                                                Toast toast = Toast.makeText(getApplicationContext(), "Please turn on the bluetooth", Toast.LENGTH_LONG);
                                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                                toast.show();

                                                            }
                                                        }else{
                                                            Toast toast = Toast.makeText(getApplicationContext(), "Bluetooth is not available.", Toast.LENGTH_LONG);
                                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                                            toast.show();

                                                        }
                                                    } catch (Exception e) {
                                                        Toast toast = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
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
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();

                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                }
                            }



                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(),"Please enter valid quantity", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            //Toast.makeText(getApplicationContext(), "Please enter valid quantity", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }else{
                        showPinEnterDialogForCashClose();
                    }

                }else {
                    Toast toast = Toast.makeText(getApplicationContext(),"Please close the sales first", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    //Toast.makeText(getApplicationContext(), "Please close the sales", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

        try{
            DataBaseAdapter objdatabaseadapter1 = null;
            objdatabaseadapter1 = new DataBaseAdapter(context);
            objdatabaseadapter1.open();

            try{
                checkclosecount = objdatabaseadapter1.GetCashClose(getschedulecode);
            }catch (Exception e) {
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            } finally {
                if (objdatabaseadapter1 != null)
                    objdatabaseadapter1.close();
            }

            if(getdenominationcount>0){
                if (!checkclosecount.equals("0") && !checkclosecount.equals("null") &&
                        !checkclosecount.equals("") && !checkclosecount.equals(null)) {
                    printbtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_print));
                }else{
                    printbtn.setImageDrawable(getResources().getDrawable(R.drawable.indian_rupee));
                }

            }else{
                printbtn.setImageDrawable(getResources().getDrawable(R.drawable.indian_rupee));
            }
        }catch (Exception e){

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
    //Get cash amount
    public void GetCashAndExpenseAmount(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor getschedulekm = null;
        double startkm=0,endkm=0;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            getschedulecode = objdatabaseadapter.GetScheduleCode();
            String getsalescashamt = objdatabaseadapter.GetSalesCash(getschedulecode);
            String getreceiptcashamt = objdatabaseadapter.GetReceiptCash(getschedulecode);
            String getadvancecash = objdatabaseadapter.GetScheduleAdvance(getschedulecode);
            String getexpenseamt = objdatabaseadapter.GetExpenseAmount(getschedulecode);
            String getsalesreturnamt = objdatabaseadapter.GetSalesReturnAmount(getschedulecode);
            getstaticcashroutename = objdatabaseadapter.GetCashRoutename(getschedulecode);
            getstartingkm = objdatabaseadapter.GetCashStartingKm(getschedulecode);
            getschedulekm = objdatabaseadapter.GetScheduleKM(getschedulecode);
            if(getschedulekm.getCount()>0){
                startkm = getschedulekm.getFloat(0);
                endkm = getschedulekm.getFloat(1);
            }else{
                startkm = 0;
                endkm = 0;
            }
            txtstartingkm.setText("Start km "+String.valueOf(startkm));
            txtendingkm.setText("End km "+String.valueOf(endkm));
            Double caltotalkm = endkm - startkm;
            totlakm.setText("Total \n"+String.valueOf(caltotalkm)+" km ");
            if(getadvancecash.equals("")){
                getadvancecash = "0";
            }
            Double totalcash = Double.parseDouble(getsalescashamt) +
                     Double.parseDouble(getreceiptcashamt) + Double.parseDouble(getadvancecash);
            totalcash = Double.valueOf(Math.round(totalcash));
            Double totalexp = Double.parseDouble(getexpenseamt);
            // + Double.parseDouble(getsalesreturnamt)
            totalexp = Double.valueOf(Math.round(totalexp));
            Double cashinhand = totalcash - totalexp;

            txtsalescash.setText( "\u20B9 " + dft.format(Double.parseDouble(getsalescashamt)));
            txtreceiptcash.setText("\u20B9 " + dft.format(Double.parseDouble(getreceiptcashamt)));
            txtadvancecash.setText("\u20B9 " + dft.format(Double.parseDouble(getadvancecash)));

            txttotalcash.setText( "\u20B9 " + dft.format(totalcash));
            txtexpenseamt.setText("\u20B9 " + dft.format(Double.parseDouble(getexpenseamt)));
            txtsalesreturnamt.setText("\u20B9 " + dft.format(Double.parseDouble(getsalesreturnamt)));
            txttotalexpense.setText("\u20B9 " + dft.format(totalexp));
            txtcashinhand.setText("\u20B9 " + dft.format(cashinhand));

            getsalescash=getsalescashamt;
            getsalesreturn=getsalesreturnamt;
            getadvance=getadvancecash;
            getreceiptcash=getreceiptcashamt;
            getexpense=getexpenseamt;
            getcashinhand=String.valueOf(cashinhand);
            gettotalcash = String.valueOf(totalcash);
            gettotalexp = String.valueOf(totalexp);

        }  catch (Exception e){
            Log.i("CashReportAmount", e.toString());
        }
        finally {
            // this gets called even if there is an exception somewhere above
            if(objdatabaseadapter != null)
                objdatabaseadapter.close();
        }
    }
    //Get currency list
    public void GetDenominationList()
    {
        try{
            DataBaseAdapter objcustomerAdaptor = new DataBaseAdapter(context);
            objcustomerAdaptor.open();
            Cursor mCur = objcustomerAdaptor.GetCurrencyDB();


            ValuesCode = new String[mCur.getCount()];
            Values = new String[mCur.getCount()];
            QtyValues = new String[mCur.getCount()];
            oldQtyValues = new String[mCur.getCount()];
            TotalValues = new String[mCur.getCount()];
            for(int i=0;i<mCur.getCount();i++)
            {
                ValuesCode[i]=mCur.getString(0);
                Values[i]=mCur.getString(1);
                Cursor mCurSchedule = objcustomerAdaptor.GetScheduleCurrencyDB(getschedulecode);
                if(mCurSchedule.getCount() >0) {
                    for (int j = 0; j < mCurSchedule.getCount(); j++) {

                        if (ValuesCode[i].equals(mCurSchedule.getString(0))) {
                            getdenominationsave = true;
                            QtyValues[i] = mCurSchedule.getString(1);
                            oldQtyValues [i] = mCurSchedule.getString(1);
                        }
                        mCurSchedule.moveToNext();
                    }
                }else{
                    QtyValues[i]="";
                    oldQtyValues[i]="";
                }

                TotalValues[i]="";
                mCur.moveToNext();
            }
            objcustomerAdaptor.close();
           if(mCur.getCount() >0) {
                DenominationAdapter adapter = new DenominationAdapter(context);
                lv_denomination.setAdapter(adapter);
            }
            else{
               Toast toast = Toast.makeText(getApplicationContext(),"No Currency Available", Toast.LENGTH_LONG);
               toast.setGravity(Gravity.CENTER, 0, 0);
               toast.show();
                //Toast.makeText(context, "No Currency Available", Toast.LENGTH_SHORT).show();
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
    }
    public class DenominationAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;

        DenominationAdapter(Context c) {
            context = c;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return Values.length;
        }

        @Override
        public Object getItem(int position) {
            return Values[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final DecimalFormat df = new DecimalFormat("0.00");
            final ViewHolder1 mHolder;
            //final String[] valueBefore = new String[QtyValues.length];
            //final String[] valueAfter={""};
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.cashreportdenomination, parent, false);
                mHolder = new ViewHolder1();
                try {
                    mHolder.txtvalue = (TextView) convertView.findViewById(R.id.txtvalue);
                    mHolder.txtmultiply = (TextView) convertView.findViewById(R.id.txtmultiply);
                    mHolder.txtqty = (EditText) convertView.findViewById(R.id.txtqty);
                    mHolder.txtequal = (TextView) convertView.findViewById(R.id.txtequal);
                    mHolder.txttotal = (TextView) convertView.findViewById(R.id.txttotal);
                    mHolder.txtgetvalue = (TextView)convertView.findViewById(R.id.txtgetvalue);

                    mHolder.txtqty.addTextChangedListener(new TextWatcher() {
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            final int pos =(Integer) mHolder.txtqty.getTag();
                            final String varQty = mHolder.txtqty.getText().toString();
                            QtyValues[pos]=varQty;

                            if(!(mHolder.txtqty.getText().toString()).equals("") ) {
                                int a = Integer.parseInt( mHolder.txtqty.getText().toString());
                                int  b=0;
                                if(ValuesCode[pos].equals("999")){ b = 1;}
                                else{ b = Integer.parseInt(mHolder.txtgetvalue.getText().toString());}
                                int res = a*b;
                                mHolder.txttotal.setText(df.format(res));
                                TotalValues[pos]=df.format(res);
                                QtyValues[pos]=mHolder.txtqty.getText().toString();
                                mHolder.txttotal.setTextColor(context.getResources().getColor(R.color.white));
                                mHolder.txttotal.setBackgroundColor(context.getResources().getColor(R.color.green));

                            }else{
                                mHolder.txttotal.setText(df.format(0));
                                TotalValues[pos]=df.format(0);
                                mHolder.txttotal.setTextColor(context.getResources().getColor(R.color.black));
                                mHolder.txttotal.setBackgroundColor(context.getResources().getColor(R.color.graycolor));
                            }
                            double res1=0;

                            for (int i = 0; i < TotalValues.length; i++) {
                                if(!(TotalValues[i]).equals("") ) {
                                    res1 = res1+ Double.parseDouble(TotalValues[i]);
                                }

                            }
                            getdenominationcash=String.valueOf(res1);
                            getsubtotalval =  " \u20B9 " + df.format(res1);
                            subtotalval.setText("Total \u20B9 " + df.format(res1));
                            double getremaing = res1 - Double.parseDouble(getcashinhand)  ;
                            remaingamtval.setText("Difference \u20B9 " + df.format(getremaing));
                        }
                        public void beforeTextChanged(CharSequence s, int start, int count,
                                                      int after) {
                        }
                        public void afterTextChanged(Editable s) {

                        }
                    });

                    convertView.setTag(mHolder);
                    convertView.setTag(R.id.txtvalue, mHolder.txtvalue);
                    convertView.setTag(R.id.txtmultiply, mHolder.txtmultiply);
                    convertView.setTag(R.id.txtqty, mHolder.txtqty);
                    convertView.setTag(R.id.txtequal, mHolder.txtequal);
                    convertView.setTag(R.id.txttotal, mHolder.txttotal);
                    convertView.setTag(R.id.txtgetvalue, mHolder.txtgetvalue);
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
                mHolder = (ViewHolder1) convertView.getTag();
            }
            try {
                mHolder.txtqty.setEnabled(true);
               /* if(!getcashclose.equals("0") && !getcashclose.equals("null") &&
                        !getcashclose.equals("") && !getcashclose.equals(null) && getdenominationcount >0){
                    mHolder.txtqty.setBackgroundResource(R.drawable.editbackgroundgray);
                    mHolder.txtqty.setEnabled(false);
                }*/
                if(Double.parseDouble(getcashclose) <= 0.0 || getdenominationcount >0){
                    mHolder.txtqty.setBackgroundResource(R.drawable.editbackgroundgray);
                    mHolder.txtqty.setEnabled(false);
                }
                if((getdenominationcount > 0 && Integer.parseInt(checkclosecount) == 0)){
                    mHolder.txtqty.setBackgroundResource(R.drawable.editbackground);
                    mHolder.txtqty.setEnabled(true);
                }
                Log.i("Position",String.valueOf(position ));
                mHolder.txtvalue.setTag(position);
                mHolder.txtmultiply.setTag(position);
                mHolder.txtqty.setTag(position);
                mHolder.txtequal.setTag(position);
                mHolder.txttotal.setTag(position);
                mHolder.txtgetvalue.setTag(position);

                mHolder.txtvalue.setText("\u20B9 " +String.valueOf(Values[position]));
                mHolder.txtgetvalue.setText(String.valueOf(Values[position]));
                if(QtyValues[position].equals(null) || QtyValues[position].equals("null")){
                    QtyValues[position] = "";
                }
                mHolder.txtqty.setText(String.valueOf(QtyValues[position]));

                mHolder.txtgetvalue.setTag(position);
                mHolder.txtvalue.setTag(position);

            } catch (Exception e) {
                Log.i("value", e.toString());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            return convertView;
        }

        private class ViewHolder1 {
            private TextView txtvalue;
            private TextView txtmultiply;
            private EditText txtqty;
            private TextView txtequal;
            private TextView txttotal;
            private TextView txtgetvalue;
        }
    }

    /**********Asynchronous Claass***************/

    protected  class AsyncCloseCashDetails extends
            AsyncTask<String, JSONObject, ArrayList<ScheduleDatas>> {
        ArrayList<ScheduleDatas> List = null;
        JSONObject jsonObj = null;
        @Override
        protected  ArrayList<ScheduleDatas> doInBackground(String... params) {
            RestAPI api = new RestAPI();
            String result = "";
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
                    List = parser.parseCashCloseReport(jsonObj);
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
                            dataBaseAdapter.UpdateCashCloseFlag(result.get(0).ScheduleCode[j]);
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

    protected  class AsyncCashReportDetails extends
            AsyncTask<String, JSONObject, ArrayList<ScheduleDatas>> {
        ArrayList<ScheduleDatas> List = null;
        JSONObject jsonObj = null;
        @Override
        protected  ArrayList<ScheduleDatas> doInBackground(String... params) {
            RestAPI api = new RestAPI();
            String result = "";
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
                        obj.put("makerid", mCur3.getDouble(6));
                        obj.put("createddate", mCur3.getDouble(7));
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
                            dataBaseAdapter.UpdateCashDetailsFlag(result.get(0).ScheduleCode[j]);
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

    /**********End Asynchronous Claass***************/
    public void goBack(View v) {
        if(LoginActivity.iscash){
            if(!getdenominationsave) {
                if (Double.parseDouble(getdenominationcash) > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Confirmation");
                    builder.setIcon(R.mipmap.ic_van);
                    builder.setMessage("Are you sure you want to clear cart?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    /*Intent i = new Intent(CashReportActivity.this, ScheduleActivity.class);
                                    startActivity(i);*/
                                    finish();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();

                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    /*Intent i = new Intent(CashReportActivity.this, ScheduleActivity.class);
                    startActivity(i);*/
                    finish();
                }
            }else {
                /*Intent i = new Intent(CashReportActivity.this, ScheduleActivity.class);
                startActivity(i);*/
                finish();
            }

        }else {
            if(!getdenominationsave) {
                if (Double.parseDouble(getdenominationcash) > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Confirmation");
                    builder.setIcon(R.mipmap.ic_van);
                    builder.setMessage("Are you sure you want to clear cart?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    LoginActivity.ismenuopen=true;
                                    Intent i = new Intent(CashReportActivity.this, MenuActivity.class);
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
                }else{
                    LoginActivity.ismenuopen=true;
                    Intent i = new Intent(CashReportActivity.this, MenuActivity.class);
                    startActivity(i);
                }
            }else if(getdenominationcount>0 && Integer.parseInt(checkclosecount)==0){
                for(int i=0;i<QtyValues.length;i++){
                    if(!QtyValues[i].equals(oldQtyValues[i])){
                        isValueChanged=true;
                        break;
                    }
                    isValueChanged = false;
                }
                if(isValueChanged){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Confirmation");
                    builder.setIcon(R.mipmap.ic_van);
                    builder.setMessage("Are you want to discard the changes")
                            .setCancelable(false)
                            .setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                }
                            })
                            .setNegativeButton("Keep editing", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();

                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }else{
                    LoginActivity.ismenuopen=true;
                    Intent i = new Intent(CashReportActivity.this, MenuActivity.class);
                    startActivity(i);
                }
            }else{
                LoginActivity.ismenuopen=true;
                Intent i = new Intent(CashReportActivity.this, MenuActivity.class);
                startActivity(i);
            }

        }

        /*if(getdenominationcount>0 && Integer.parseInt(checkclosecount)==0){
            if(isValueChanged){
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirmation");
                builder.setMessage("Are you want to discard the changes")
                        .setCancelable(false)
                        .setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        })
                        .setNegativeButton("Keep editing", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }*/

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
        goBack(null);
    }

    protected class AsyncCashReportBill extends
            AsyncTask<String, JSONObject, Boolean> {
        Boolean billPrinted = false;
        JSONObject jsonObj = null;
        ProgressDialog loading;
        @Override
        protected Boolean doInBackground(String... params) {
            //final String finalGetreceipttransano = params[0];
            //final String financialyearcode = params[1];
            try {
                EpsonT20Printer epsonT20Printer = new EpsonT20Printer(CashReportActivity.this, CashReportActivity.this, receiveListener);
                deviceFound = epsonT20Printer.findBT();
                Log.d("deviceFound",String.valueOf(deviceFound));
                if (!deviceFound) {
                    billPrinted = deviceFound;
                } else {
                    billPrinted = (boolean) epsonT20Printer.GetCashReportPrintBill(CashReportActivity.this);
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
                finish();
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

    public void showPinEnterDialogForCashClose(){
        pindialog = new Dialog(context);
        pindialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pindialog.setContentView(R.layout.cashclosevalidatepinumber);
        ImageView closepopup = (ImageView) pindialog.findViewById(R.id.closepopup);
        btnSubmitpin = (Button) pindialog.findViewById(R.id.btnSubmitpin);
        pinview = (Pinview) pindialog.findViewById(R.id.pinview);

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
                Cursor getschedulekm1 = null;

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
                        //Cash close details
                        getresult = objdatabaseadapter.UpdateCashClose(getschedulecode);
                        if (getresult.equals("success")) {
                            pindialog.dismiss();
                            printbtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_print));
                            Toast toast = Toast.makeText(getApplicationContext(), "Cash Closed Successfully", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            //Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_SHORT).show();
                            MenuActivity.getcashclosecount = objdatabaseadapter.GetCashCloseCount(getschedulecode);
                            preferenceMangr.pref_putString("getcashclosecount",objdatabaseadapter.GetCashCloseCount(getschedulecode));

                            networkstate = isNetworkAvailable();
                            if (networkstate == true) {
                                new AsyncCloseCashDetails().execute();
                                new AsyncCashReportDetails().execute();
                            }
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
                        //Toast.makeText(getApplicationContext(), "Please enter correct pin", Toast.LENGTH_SHORT).show();
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

    public void enablePrintButtons() {
        printbtn.setEnabled(true);
    }

    public void disablePrintButtons() {
        printbtn.setEnabled(false);
    }

}
