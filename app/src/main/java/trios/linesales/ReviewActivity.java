    package trios.linesales;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class ReviewActivity extends AppCompatActivity {
    ListView reviewlistview;
    Context context;
    TextView addmore;
    ImageView reviewlistgoback;
    public  TextView paymenttypeinvoice,txtcustomername,txtareacity,
            txtcarttotalamt,txtreviewweight,reviewitems,
            txtdiscountamt,txtsubtotalamt,cartgstnnumber,txtbookingno,txtreviewdate,hidedummy;
    public DecimalFormat df;
    Button txtSalesprint,imgcamera;
    EditText txtremarks;
    public static boolean deviceFound;
    ArrayList<SalesItemDetails> salesItemList;
    LinearLayout gstnLL;
    String getsalesdate="",getbilltypecode="",getsubtotalamount="",
            getdiscountamt="",getgrandtotal="",getbookingno="",gettransactionno="";
    public  boolean networkstate;
    Dialog dialogstatus;
    private static final int CAMERA_PIC_REQUEST = 1111;
    Bitmap bitmap1=null;
    static String varimageview = "";
    public String getsalestransactionno = "0";
    public static final String UPLOAD_URL = RestAPI.urlString+"syncimage.php";
    public  static boolean isfromcart = false;
    private PrintData printData;
    Dialog printpopup;

    public static ProgressDialog SalesPrintloading;
    BluetoothAdapter mBluetoothAdapter;
    public static PreferenceMangr preferenceMangr=null;
    GPSTracker gpsTracker;
    String orderTransNo="",orderFinancialyear="",orderCompanyCode="";
    ReceiveListener receiveListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        try{
            //Declare All variables
            context = this;
            reviewlistview = (ListView) findViewById(R.id.reviewlistview);
            reviewlistgoback = (ImageView) findViewById(R.id.reviewlistgoback);
            addmore = (TextView)findViewById(R.id.addmore);
            paymenttypeinvoice = (TextView)findViewById(R.id.paymenttypeinvoice);
            txtcustomername = (TextView)findViewById(R.id.txtcustomername);
            txtareacity = (TextView)findViewById(R.id.txtareacity);
            txtreviewweight = (TextView)findViewById(R.id.txtreviewweight);
            reviewitems = (TextView)findViewById(R.id.reviewitems);
            txtcarttotalamt = (TextView)findViewById(R.id.txtcarttotalamt);
            txtdiscountamt = (TextView)findViewById(R.id.txtdiscountamt);
            txtsubtotalamt = (TextView)findViewById(R.id.txtsubtotalamt);
            cartgstnnumber = (TextView)findViewById(R.id.cartgstnnumber);
            gstnLL = (LinearLayout)findViewById(R.id.gstnLL);
            txtbookingno = (TextView)findViewById(R.id.txtbookingno);
            txtSalesprint = (Button)findViewById(R.id.txtSalesprint);
            txtreviewdate = (TextView)findViewById(R.id.txtreviewdate);
            txtremarks = (EditText)findViewById(R.id.txtremarks);
            imgcamera = (Button)findViewById(R.id.imgcamera);
            hidedummy = (TextView)findViewById(R.id.hidedummy);
            SalesViewActivity.isduplicate = false;

            imgcamera.setVisibility(View.GONE);
            hidedummy.setVisibility(View.VISIBLE);

            try {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                // String cuscode=SalesActivity.customercode;
            }catch (Exception e){
                Log.d("Bluetooth Adapter : ",e.toString());
            }
            try {
                preferenceMangr = new PreferenceMangr(context);
                receiveListener = new ReceiveListener() {
                    @Override
                    public void onPtrReceive(Printer printer, int i, PrinterStatusInfo printerStatusInfo, String s) {

                    }
                };
            }catch (Exception e){
                Log.d("PreferenceMangr : ",e.toString());
            }

            if(getIntent().hasExtra("Payment_Type")){
                //set Invoice heading
                boolean b = getIntent().getBooleanExtra("Payment_Type", false);

                if(b){
                    paymenttypeinvoice.setText("CASH");
                    getbilltypecode = "1";

                }else{
                    gstnLL.setVisibility(View.VISIBLE);
                    paymenttypeinvoice.setText("CREDIT");
                    if(!SalesActivity.gstnnumber.equals("")){
                        getbilltypecode = "2";
                    }
                    if(SalesActivity.gstnnumber.equals("")){
                        getbilltypecode = "3";
                    }
                }

            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Please check the payment type", Toast.LENGTH_LONG);
                //toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            try{
                orderTransNo = preferenceMangr.pref_getString(Constants.KEY_ORDER_TO_SALES_TRANS_NO);
                orderFinancialyear = preferenceMangr.pref_getString(Constants.KEY_ORDER_TO_SALES_FINANCIALYEAR);
                orderCompanyCode = preferenceMangr.pref_getString(Constants.KEY_ORDER_TO_SALES_COMPANYCODE);
            }catch (Exception e){

            }
            /*if(SalesActivity.getpaymenttypecode.equals("1")){
                paymenttypeinvoice.setText("CASH");
                getbilltypecode = "1";


            }else if(SalesActivity.getpaymenttypecode.equals("2")){
                gstnLL.setVisibility(View.VISIBLE);
                paymenttypeinvoice.setText("CREDIT");
                getbilltypecode = "2";
            }*/



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
                mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName()
                        +" - get and set date from preference", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }finally {
                // this gets called even if there is an exception somewhere above
                if (objdatabaseadapter1 != null)
                    objdatabaseadapter1.close();
            }


            imgcamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try
                    {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, CAMERA_PIC_REQUEST);
                    }
                    catch (Exception e)
                    {
                        DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                        mDbErrHelper.open();
                        String geterrror = e.toString();
                        mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName()
                                +" - imgcamera", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        mDbErrHelper.close();
                    }
                }
            });
            //GSTN NUMBER
            if(!SalesActivity.gstnnumber.equals("")){
                if(SalesActivity.paymenttype){
                    gstnLL.setVisibility(View.VISIBLE);
                    cartgstnnumber.setText(SalesActivity.gstnnumber);
                    gstnLL.setBackgroundColor(getResources().getColor(R.color.graycolor));
                    if(SalesActivity.gstnnumber.equals("")){
                        gstnLL.setBackgroundColor(getResources().getColor(R.color.graycolor));
                    }
                }else{
                    gstnLL.setVisibility(View.VISIBLE);
                    cartgstnnumber.setText(SalesActivity.gstnnumber);
                    gstnLL.setBackgroundColor(getResources().getColor(R.color.green));
                    if(SalesActivity.gstnnumber.equals("")){
                        gstnLL.setBackgroundColor(getResources().getColor(R.color.graycolor));
                    }
                }
            }else{
                gstnLL.setVisibility(View.GONE);
                cartgstnnumber.setText("");
            }


            //Set customer name
            txtcustomername.setText(SalesActivity.txtcustomername.getText().toString());
            //Set area and city
            txtareacity.setText(SalesActivity.txtareaname.getText().toString());
            //set current date
            txtreviewdate.setText(SalesActivity.txtsalesdate.getText().toString());
            getsalesdate = preferenceMangr.pref_getString("getformatdate");

            //open Review Screen
            addmore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isfromcart = true;
                    finish();
                }
            });
            reviewlistgoback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isfromcart = true; finish();
                }
            });

            /****Booking Number************/
            /***** SALES TRANSACTION DETAILES************/
            DataBaseAdapter objdatabaseadapter = null;
            try {
                //Get Booking No.
                objdatabaseadapter = new DataBaseAdapter(context);
                objdatabaseadapter.open();
                getbookingno = objdatabaseadapter.GetBookingNo();
                gettransactionno = objdatabaseadapter.GetTransactionNo();
                txtbookingno.setText("BK.No. : "+getbookingno);

            } catch (Exception e) {
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName()
                        +" - get and set booking no.", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            } finally {
                if(objdatabaseadapter!=null)
                    objdatabaseadapter.close();
            }
            //Item Adapter
            setItemAdapter();

            if(SalesActivity.ifsavedsales) {
                if (getbilltypecode.equals("2")) {
                    imgcamera.setVisibility(View.VISIBLE);
                    txtSalesprint.setVisibility(View.GONE);
                } else {
                    imgcamera.setVisibility(View.GONE);
                    txtSalesprint.setVisibility(View.GONE);
                }

            }

            //Sales save functionality
            txtSalesprint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataBaseAdapter objdatabaseadapter = null;
                    try {
                        //Sales Temp table
                        objdatabaseadapter = new DataBaseAdapter(context);
                        objdatabaseadapter.open();
                        if (SalesActivity.paymenttype) {
                            SalesActivity.gstnnumber = "";
                        } else {
                            SalesActivity.gstnnumber = SalesActivity.gstnnumber;
                        }
                        if (SalesActivity.staticreviewsalesitems.size() > 0) {
                            if (salesItemList.size() > 0) {
                                txtSalesprint.setEnabled(false);
                                String getmaxrefno = objdatabaseadapter.GetMaxRefnoSalesItems();
                                objdatabaseadapter.UpdateSalesOrderCompletedFlag(SalesActivity.ordertransactionno,SalesActivity.orderfinancialyearcode);
                                for (int i = 0; i < salesItemList.size(); i++) {
                                    objdatabaseadapter.InsertTempSalesItemDetails(salesItemList.get(i).getItemcode(),
                                            salesItemList.get(i).getCompanycode(), salesItemList.get(i).getItemqty(), salesItemList.get(i).getNewprice(),
                                            salesItemList.get(i).getDiscount(), salesItemList.get(i).getSubtotal(), salesItemList.get(i).getFreeflag(),
                                            salesItemList.get(i).getTax(), SalesActivity.gstnnumber, getmaxrefno,
                                            (Double.parseDouble(salesItemList.get(i).getUnitweight()) * (Double.parseDouble(salesItemList.get(i).getItemqty()))),
                                            i + 1, salesItemList.get(i).getratediscount(),salesItemList.get(i).getschemeapplicable(),salesItemList.get(i).getOrgprice());
                                }

                                String getbillcopystatus = "";
                                if (getbilltypecode.equals("2") || (getbilltypecode.equals("1") && !SalesActivity.gstnnumber.equals("")
                                        && !SalesActivity.gstnnumber.equals("null") && !SalesActivity.gstnnumber.equals(null))) {
                                    getbillcopystatus = "yes";
                                } else {
                                    getbillcopystatus = "";
                                }
                                String getcashpaidstatus = "";
                                if (getbilltypecode.equals("2")) {
                                    getcashpaidstatus = "no";
                                } else {
                                    getcashpaidstatus = "";
                                }

                                String getshowpopupstatus = objdatabaseadapter.getshowcashpaidpopupstatus();
                                if (getshowpopupstatus.equals("no") || getshowpopupstatus.equals("null") || getshowpopupstatus.equals(null)) {
                                    if (getbilltypecode.equals("2")) {
                                        getcashpaidstatus = "no";
                                    } else {
                                        getcashpaidstatus = "yes";
                                    }
                                }

                                //get current location
                                String latLong="0";
                                String latitude="0",longtitude="0";
                                try{
                                    if(preferenceMangr.pref_getBoolean(Constants.KEY_GET_GPSTRACKINGSTATUS)){
                                        gpsTracker = new GPSTracker(getApplicationContext());
                                        if(gpsTracker.isGPSTrackingEnabled){
                                            latLong = String.valueOf(gpsTracker.getLatitude()) + "," + String.valueOf(gpsTracker.getLongitude());
                                            latitude = String.valueOf(gpsTracker.getLatitude());
                                            longtitude = String.valueOf(gpsTracker.getLongitude());
                                        }
                                    }

                                }catch (Exception e){
                                    objdatabaseadapter.insertErrorLog("ReviewActivity : Exception in getLatLong value : " + String.valueOf(e).replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                }

                                getsalestransactionno = objdatabaseadapter.InsertSales(preferenceMangr.pref_getString("getvancode"), getsalesdate, SalesActivity.customercode,
                                        getbilltypecode, SalesActivity.gstnnumber, preferenceMangr.pref_getString("getschedulecode"), getsubtotalamount,
                                        getdiscountamt, getgrandtotal, preferenceMangr.pref_getString("getfinanceyrcode"),
                                        txtremarks.getText().toString(), getbookingno, gettransactionno,
                                        getmaxrefno, getbillcopystatus, getcashpaidstatus,latLong,latitude,longtitude,
                                        orderTransNo,orderFinancialyear,orderCompanyCode);
                                //Get General settings
                                if (!getsalestransactionno.equals("") && !getsalestransactionno.equals(null)
                                        && !getsalestransactionno.equals("null")) {
                                    /*Toast toast = Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.BOTTOM, 0, 150);
                                    toast.show();*/
                                    SalesActivity.ifsavedsales = true;
                                    SalesActivity.gstnnumber = "";
//                                SalesActivity.customercode ="";

                                    SalesActivity.getpaymenttypecode = "";
                                    SalesActivity.getpaymenttypename = "";
                                    SalesActivity.confromBilltype = false;

                                    SalesActivity.txtareaname.setEnabled(true);
                                    SalesActivity.txtcustomername.setEnabled(true);

                                    Utilities.deleteOrderToSalesConverstionDetails(getApplicationContext());

                                    String result = objdatabaseadapter.InsertnilStock(preferenceMangr.pref_getString("getvancode"),
                                            preferenceMangr.pref_getString("getschedulecode"), getsalestransactionno, getbookingno, preferenceMangr.pref_getString("getfinanceyrcode"),
                                            SalesActivity.customercode);


                                    try {
                                        printpopup = new Dialog(context);
                                        printpopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        printpopup.setContentView(R.layout.printpopup);

                                        TextView tvheading = (TextView) printpopup.findViewById(R.id.tvheading);
                                        tvheading.setText(getString(R.string.billSaved_wantPrint));
                                        TextView txtYesAction = (TextView) printpopup.findViewById(R.id.txtYesAction);
                                        TextView txtNoAction = (TextView) printpopup.findViewById(R.id.txtNoAction);
                                        txtYesAction.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                try {

                                                /*printData = new PrintData(context);
                                                deviceFound = printData.findBT();

                                                if (!deviceFound) {
                                                    Toast toast1 = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                                    toast1.setGravity(Gravity.CENTER, 0, 0);
                                                   // printpopup.dismiss();
                                                    toast1.show();
                                                    if (getbilltypecode.equals("2")) {
                                                        imgcamera.setVisibility(View.VISIBLE);
                                                        txtSalesprint.setVisibility(View.GONE);
                                                    } else {
                                                        SalesActivity.staticreviewsalesitems.clear();
                                                        SalesActivity.salesitems.clear();
                                                        txtSalesprint.setVisibility(View.GONE);
                                                        txtSalesprint.setEnabled(true);
                                                        Intent i = new Intent(ReviewActivity.this, SalesListActivity.class);
                                                        startActivity(i);

                                                    }
                                                } else {*/

                                                    /*boolean billPrinted = false;
                                                    billPrinted = (boolean) printData.GetSalesBillPrint(getsalestransactionno, LoginActivity.getfinanceyrcode);
                                                    if (!billPrinted) {
                                                        Toast toast2 = Toast.makeText(getApplicationContext(), "Unable to connect to Bluetooth Printer!", Toast.LENGTH_LONG);
                                                        toast2.setGravity(Gravity.CENTER, 0, 0);
                                                        //printpopup.dismiss();
                                                        toast2.show();
                                                        if(getbilltypecode.equals("2")) {
                                                            imgcamera.setVisibility(View.VISIBLE);
                                                            txtSalesprint.setVisibility(View.GONE);
                                                        }else {
                                                            SalesActivity.staticreviewsalesitems.clear();
                                                            SalesActivity.salesitems.clear();
                                                            txtSalesprint.setVisibility(View.GONE);
                                                            txtSalesprint.setEnabled(true);
                                                            Intent i = new Intent(ReviewActivity.this, SalesListActivity.class);
                                                            startActivity(i);

                                                        }
                                                        SalesActivity.staticreviewsalesitems.clear();
                                                        SalesActivity.salesitems.clear();
                                                        txtSalesprint.setVisibility(View.GONE);
                                                        txtSalesprint.setEnabled(true);
                                                        Intent i = new Intent(ReviewActivity.this, SalesListActivity.class);
                                                        startActivity(i);
                                                        //Toast.makeText(context, "Unable to connect to Bluetooth Printer!", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }
                                                    billPrinted = (boolean) printData.GetDCPrint(getsalestransactionno, LoginActivity.getfinanceyrcode);

                                                    if (!billPrinted) {
                                                        Toast toast3 = Toast.makeText(getApplicationContext(), "Unable to connect to Bluetooth Printer!", Toast.LENGTH_LONG);
                                                        toast3.setGravity(Gravity.CENTER, 0, 0);
                                                        //printpopup.dismiss();
                                                        toast3.show();
                                                        if(getbilltypecode.equals("2")) {
                                                            imgcamera.setVisibility(View.VISIBLE);
                                                            txtSalesprint.setVisibility(View.GONE);
                                                        }else {
                                                            SalesActivity.staticreviewsalesitems.clear();
                                                            SalesActivity.salesitems.clear();
                                                            txtSalesprint.setVisibility(View.GONE);
                                                            txtSalesprint.setEnabled(true);
                                                            Intent i = new Intent(ReviewActivity.this, SalesListActivity.class);
                                                            startActivity(i);

                                                        }
                                                        SalesActivity.staticreviewsalesitems.clear();
                                                        SalesActivity.salesitems.clear();
                                                        txtSalesprint.setVisibility(View.GONE);
                                                        txtSalesprint.setEnabled(true);
                                                        Intent i = new Intent(ReviewActivity.this, SalesListActivity.class);
                                                        startActivity(i);
                                                        //Toast.makeText(context, "Unable to connect to Bluetooth Printer!", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }else {
                                                        //printpopup.dismiss();
                                                        if(getbilltypecode.equals("2")) {
                                                            imgcamera.setVisibility(View.VISIBLE);
                                                            txtSalesprint.setVisibility(View.GONE);
                                                        }else {
                                                            SalesActivity.staticreviewsalesitems.clear();
                                                            SalesActivity.salesitems.clear();
                                                            txtSalesprint.setVisibility(View.GONE);
                                                            txtSalesprint.setEnabled(true);
                                                            Intent i = new Intent(ReviewActivity.this, SalesListActivity.class);
                                                            startActivity(i);

                                                        }
                                                        SalesActivity.staticreviewsalesitems.clear();
                                                        SalesActivity.salesitems.clear();
                                                        txtSalesprint.setVisibility(View.GONE);
                                                        txtSalesprint.setEnabled(true);
                                                        Intent i = new Intent(ReviewActivity.this, SalesListActivity.class);
                                                        startActivity(i);
                                                    }*/
                                                    try {


                                                        if (mBluetoothAdapter != null) {

                                                            if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                                                                if (!preferenceMangr.pref_getString("SelectedPrinterAddress").equals("")) {
                                                                    new AsyncPrintSalesDetails().execute(getsalestransactionno, preferenceMangr.pref_getString("getfinanceyrcode"), getbilltypecode);
                                                                    printpopup.dismiss();
                                                                } else {
                                                                    Toast toast = Toast.makeText(getApplicationContext(), "Please select bluetooth printer in app settings", Toast.LENGTH_LONG);
                                                                    //toast.setGravity(Gravity.CENTER, 0, 0);
                                                                    toast.show();
                                                                    printpopup.dismiss();
                                                                    SalesActivity.staticreviewsalesitems.clear();
                                                                    SalesActivity.salesitems.clear();
                                                                    txtSalesprint.setVisibility(View.GONE);
                                                                    txtSalesprint.setEnabled(true);
                                                                    Intent i = new Intent(ReviewActivity.this, SalesListActivity.class);
                                                                    startActivity(i);
                                                                    if (getbilltypecode.equals("2")) {
                                                                        imgcamera.setVisibility(View.VISIBLE);
                                                                        txtSalesprint.setVisibility(View.GONE);
                                                                    } else {
                                                                        SalesActivity.staticreviewsalesitems.clear();
                                                                        SalesActivity.salesitems.clear();
                                                                        txtSalesprint.setVisibility(View.GONE);
                                                                        txtSalesprint.setEnabled(true);
                                                                        Intent objindent = new Intent(ReviewActivity.this, SalesListActivity.class);
                                                                        startActivity(objindent);

                                                                    }
                                                                }
                                                            } else {
                                                                Toast toast = Toast.makeText(getApplicationContext(), "Please turn on the bluetooth", Toast.LENGTH_LONG);
                                                                //toast.setGravity(Gravity.CENTER, 0, 0);
                                                                toast.show();
                                                                printpopup.dismiss();
                                                                SalesActivity.staticreviewsalesitems.clear();
                                                                SalesActivity.salesitems.clear();
                                                                txtSalesprint.setVisibility(View.GONE);
                                                                txtSalesprint.setEnabled(true);
                                                                Intent i = new Intent(ReviewActivity.this, SalesListActivity.class);
                                                                startActivity(i);
                                                                if (getbilltypecode.equals("2")) {
                                                                    imgcamera.setVisibility(View.VISIBLE);
                                                                    txtSalesprint.setVisibility(View.GONE);
                                                                } else {
                                                                    SalesActivity.staticreviewsalesitems.clear();
                                                                    SalesActivity.salesitems.clear();
                                                                    txtSalesprint.setVisibility(View.GONE);
                                                                    txtSalesprint.setEnabled(true);
                                                                    Intent objindent = new Intent(ReviewActivity.this, SalesListActivity.class);
                                                                    startActivity(objindent);

                                                                }
                                                            }
                                                        } else {
                                                            Toast toast = Toast.makeText(getApplicationContext(), "Bluetooth is not available.", Toast.LENGTH_LONG);
                                                            //toast.setGravity(Gravity.CENTER, 0, 0);
                                                            toast.show();
                                                            printpopup.dismiss();
                                                            SalesActivity.staticreviewsalesitems.clear();
                                                            SalesActivity.salesitems.clear();
                                                            txtSalesprint.setVisibility(View.GONE);
                                                            txtSalesprint.setEnabled(true);
                                                            Intent i = new Intent(ReviewActivity.this, SalesListActivity.class);
                                                            startActivity(i);
                                                            if (getbilltypecode.equals("2")) {
                                                                imgcamera.setVisibility(View.VISIBLE);
                                                                txtSalesprint.setVisibility(View.GONE);
                                                            } else {
                                                                SalesActivity.staticreviewsalesitems.clear();
                                                                SalesActivity.salesitems.clear();
                                                                txtSalesprint.setVisibility(View.GONE);
                                                                txtSalesprint.setEnabled(true);
                                                                Intent objindent = new Intent(ReviewActivity.this, SalesListActivity.class);
                                                                startActivity(objindent);

                                                            }
                                                        }
                                                    } catch (Exception e) {
                                                        Log.d("AsyncMethod error : ", e.getLocalizedMessage());
                                                        DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                                        mDbErrHelper.open();
                                                        String geterrror = e.toString();
                                                        mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName()
                                                                + " - bluetooth printer.", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                                        mDbErrHelper.close();
                                                    }

                                                    //}
                                                } catch (Exception e) {
                                                    Toast toast4 = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                                    //toast4.setGravity(Gravity.CENTER, 0, 0);
                                                    toast4.show();
                                                    printpopup.dismiss();
                                                    if (getbilltypecode.equals("2")) {
                                                        imgcamera.setVisibility(View.VISIBLE);
                                                        txtSalesprint.setVisibility(View.GONE);
                                                    } else {
                                                        SalesActivity.staticreviewsalesitems.clear();
                                                        SalesActivity.salesitems.clear();
                                                        txtSalesprint.setVisibility(View.GONE);
                                                        txtSalesprint.setEnabled(true);
                                                        Intent i = new Intent(ReviewActivity.this, SalesListActivity.class);
                                                        startActivity(i);

                                                    }
                                                    SalesActivity.staticreviewsalesitems.clear();
                                                    SalesActivity.salesitems.clear();
                                                    txtSalesprint.setVisibility(View.GONE);
                                                    txtSalesprint.setEnabled(true);
                                                    Intent i = new Intent(ReviewActivity.this, SalesListActivity.class);
                                                    startActivity(i);
                                                    DataBaseAdapter mDbErrHelper2 = new DataBaseAdapter(context);
                                                    mDbErrHelper2.open();
                                                    mDbErrHelper2.insertErrorLog(e.toString(), this.getClass().getSimpleName()
                                                            + " bluetooth not connected and clear sales activity items", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                                    mDbErrHelper2.close();

                                                }
                                            }
                                        });

                                        txtNoAction.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                printpopup.dismiss();
                                                SalesActivity.staticreviewsalesitems.clear();
                                                SalesActivity.salesitems.clear();
                                                txtSalesprint.setVisibility(View.GONE);
                                                txtSalesprint.setEnabled(true);

                                                if (getbilltypecode.equals("2")) {
                                                    imgcamera.setVisibility(View.VISIBLE);
                                                    txtSalesprint.setVisibility(View.GONE);
                                                } else {
                                                    SalesActivity.staticreviewsalesitems.clear();
                                                    SalesActivity.salesitems.clear();
                                                    txtSalesprint.setVisibility(View.GONE);
                                                    txtSalesprint.setEnabled(true);
                                                    // Intent objindent = new Intent(ReviewActivity.this, SalesListActivity.class);
                                                    //startActivity(objindent);

                                                }
                                                Intent i = new Intent(ReviewActivity.this, SalesListActivity.class);
                                                startActivity(i);
                                            }
                                        });
                                        printpopup.setCanceledOnTouchOutside(false);
                                        printpopup.setCancelable(false);
                                        printpopup.show();


                                        networkstate = isNetworkAvailable();
                                        if (networkstate == true) {
                                            new AsyncSalesDetails().execute("true");
//                                        new AsyncNilStockDetails().execute();
//                                        runThread();
//                                        AsyncPriceListTransaction();
//                                        new AsyncPriceListTransaction().execute();
                                            new AsyncSalesOrderDetails().execute();
                                        }

                                    } catch (Exception e) {
                                        Toast toast1 = Toast.makeText(getApplicationContext(), "Unable to connect to Bluetooth Printer!", Toast.LENGTH_LONG);
                                        //toast1.setGravity(Gravity.CENTER, 0, 0);
                                        toast1.show();
                                        //  Toast.makeText(context, "Unable to connect to Bluetooth Printer!", Toast.LENGTH_SHORT).show();
                                        DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                        mDbErrHelper.open();
                                        String geterrror = e.toString();
                                        mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName()
                                                + " - Unable to connect bluetooth", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                        mDbErrHelper.close();
                                    }
/*
                        String getbillcopystatus = objdatabaseadapter.GetBillCopyDB();
                        if (getbillcopystatus.equals("yes")){
                            if (!getsalestransactionno.equals("") && !getsalestransactionno.equals(null)
                                    && !getsalestransactionno.equals("null")) {
                                SalesActivity.ifsavedsales = true;
                                dialogstatus = new Dialog(context);
                                dialogstatus.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialogstatus.setContentView(R.layout.salesreceipt);
                                dialogstatus.setCanceledOnTouchOutside(false);
                                final CheckBox checkbillcopy = (CheckBox) dialogstatus.findViewById(R.id.checkbillcopy);
                                final RadioButton radio_paid = (RadioButton) dialogstatus.findViewById(R.id.radio_paid);
                                RadioButton radio_notpaid = (RadioButton) dialogstatus.findViewById(R.id.radio_notpaid);
                                Button btnsalessubmit = (Button) dialogstatus.findViewById(R.id.btnsalessubmit);
                                ImageView closepopup = (ImageView) dialogstatus.findViewById(R.id.closepopup);
                                closepopup.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialogstatus.dismiss();
                                    }
                                });
                                if(getbilltypecode.equals("2")){
                                    radio_paid.setChecked(false);
                                    radio_notpaid.setChecked(true);
                                    radio_paid.setEnabled(false);
                                    radio_notpaid.setEnabled(false);
                                    checkbillcopy.setChecked(true);
                                }else{
                                    radio_paid.setChecked(true);
                                    radio_notpaid.setChecked(false);
                                    radio_paid.setEnabled(true);
                                    radio_notpaid.setEnabled(true);
                                    checkbillcopy.setChecked(false);
                                }
                                closepopup.setVisibility(View.GONE);
                                final DataBaseAdapter finalObjdatabaseadapter = new DataBaseAdapter(context);
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
                                            String getresult = finalObjdatabaseadapter.UpdateSalesReceipt(getsalestransactionno, getbillcopy, getpaymentstatus);

                                            if (getresult.equals("success")) {
                                                dialogstatus.dismiss();
                                                Toast toast = Toast.makeText(getApplicationContext(),"Saved Successfully", Toast.LENGTH_LONG);
                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                toast.show();
                                                //Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_SHORT).show();
                                                SalesActivity.staticreviewsalesitems.clear();
                                                SalesActivity.salesitems.clear();

                                                try {
                                                    deviceFound = LoginActivity.p.findBT();
                                                    if (!deviceFound) {
                                                        Toast toast1 = Toast.makeText(getApplicationContext(),"Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                                        toast1.setGravity(Gravity.CENTER, 0, 0);
                                                        toast1.show();
                                                        //Toast.makeText(context, "Please connect to the Bluetooth Printer!", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        boolean billPrinted = false;
                                                        billPrinted = (boolean) LoginActivity.p.GetSalesBillPrint(getsalestransactionno, LoginActivity.getfinanceyrcode);
                                                        billPrinted = (boolean)LoginActivity.p.GetDCPrint(getsalestransactionno, LoginActivity.getfinanceyrcode);
                                                        if (!billPrinted) {
                                                            Toast toast1 = Toast.makeText(getApplicationContext(),"Unable to connect to Bluetooth Printer!", Toast.LENGTH_LONG);
                                                            toast1.setGravity(Gravity.CENTER, 0, 0);
                                                            toast1.show();
                                                            //Toast.makeText(context, "Unable to connect to Bluetooth Printer!", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }
                                                    }
                                                }
                                                catch (Exception e) {
                                                    Toast toast1 = Toast.makeText(getApplicationContext(),"Unable to connect to Bluetooth Printer!", Toast.LENGTH_LONG);
                                                    toast1.setGravity(Gravity.CENTER, 0, 0);
                                                    toast1.show();
                                                    //Toast.makeText(context, "Unable to connect to Bluetooth Printer!", Toast.LENGTH_SHORT).show();
                                                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                                    mDbErrHelper.open();
                                                    String geterrror = e.toString();
                                                    mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                                    mDbErrHelper.close();
                                                }
                                                if(getbilltypecode.equals("2")) {
                                                    imgcamera.setVisibility(View.VISIBLE);
                                                    txtSalesprint.setVisibility(View.GONE);
                                                }else {
                                                    txtSalesprint.setVisibility(View.GONE);
                                                    Intent in = new Intent(ReviewActivity.this, SalesListActivity.class);
                                                    startActivity(in);
                                                    networkstate = isNetworkAvailable();
                                                    if (networkstate == true) {
                                                        new AsyncSalesDetails().execute();
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
                                            if (finalObjdatabaseadapter != null)
                                                finalObjdatabaseadapter.close();
                                        }

                                    }
                                });
                                dialogstatus.show();
                            }f
                    }else{

                        }*/
                                } else {
                                    txtSalesprint.setEnabled(true);
                                    Toast toast = Toast.makeText(getApplicationContext(), "Cart is empty", Toast.LENGTH_LONG);
                                    //toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    // Toast.makeText(getApplicationContext(),"Cart is empty",Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                txtSalesprint.setEnabled(true);
                                Toast toast = Toast.makeText(getApplicationContext(), "Cart is empty", Toast.LENGTH_LONG);
                                //toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                // Toast.makeText(getApplicationContext(),"Cart is empty",Toast.LENGTH_SHORT).show();
                            }


                        }

                    } catch (Exception e) {
                        DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                        mDbErrHelper.open();
                        String geterrror = e.toString();
                        mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName()
                                + " - save bill and print the bill", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        mDbErrHelper.close();
                    } finally {
                        if (objdatabaseadapter != null)
                            objdatabaseadapter.close();
                    }
                }
            });
        }catch (Exception e){
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName()+
                    " - oncreate Review", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }

    }

    protected  class AsyncNilStockDetails extends
            AsyncTask<String, JSONObject, ArrayList<ScheduleDatas>> {
        ArrayList<ScheduleDatas> List = null;
        JSONObject jsonObj = null;
        @Override
        protected  ArrayList<ScheduleDatas> doInBackground(String... params) {
            RestAPI api = new RestAPI();
            try {
                JSONObject js_obj = new JSONObject();
                DataBaseAdapter dbadapter =null;
                Cursor mCur2=null;
                try {
                    int count = 0;
                    if (context == null && getApplicationContext() != null)
                        context=getApplicationContext();
                    dbadapter = new DataBaseAdapter(context);
                    dbadapter.open();
                    mCur2 = dbadapter.GetNilStockDetailsDB();

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

                    js_obj.put("JSonObject", js_array2);

                    jsonObj =  api.NilStockDetails(js_obj.toString(),context);
                    //Call Json parser functionality
                    JSONParser parser = new JSONParser();
                    //parse the json object to boolean
                    List = parser.parseNilStockReport(jsonObj);
                    // dbadapter.close();
                    if (mCur2 != null && !mCur2.isClosed()){
                        count = mCur2.getCount();
                        mCur2.close();

                    }

                }
                catch (Exception e)
                {
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName()
                            + " - sync nill stock", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                }finally {
                    if (dbadapter != null)
                        dbadapter.close();
                    if (mCur2 != null)
                        mCur2.close();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncScheduleDetails", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName()
                        + " - AsyncNilStockDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
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
            if (result != null && result.size() >= 1) {
                if(result.get(0).ScheduleCode.length>0){
                    for(int j=0;j<result.get(0).ScheduleCode.length;j++){
                        DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
                        dataBaseAdapter.open();
                        if(result.get(0).ScheduleCode[j] != "" && !result.get(0).ScheduleCode[j].equals("")){
                            String getsplitval[] = result.get(0).ScheduleCode[j].split("~");
                            dataBaseAdapter.UpdateNilStockFlag(getsplitval[0],getsplitval[1]);
                        }
                        dataBaseAdapter.close();
                    }
                }

            }
            new AsyncPriceListTransaction().execute();

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
    public void setItemAdapter(){
        if(SalesActivity.staticreviewsalesitems.size()>0) {
            DataBaseAdapter dataBaseAdapter =null;
            Cursor getcartdatas = null;
            try {
                //Cart Add Functionality
                dataBaseAdapter = new DataBaseAdapter(context);
                dataBaseAdapter.open();
                //Get cart datas from database temp table
                getcartdatas = dataBaseAdapter.GetSalesItemsCart();

                if (getcartdatas.getCount() > 0) {
                    SalesActivity.staticreviewsalesitems.clear();
                    for (int i = 0; i < getcartdatas.getCount(); i++) {
                        SalesActivity.staticreviewsalesitems.add(new SalesItemDetails(getcartdatas.getString(1), getcartdatas.getString(2),
                                getcartdatas.getString(3), getcartdatas.getString(4)
                                , getcartdatas.getString(5), getcartdatas.getString(6),
                                getcartdatas.getString(7), getcartdatas.getString(8)
                                , getcartdatas.getString(9), getcartdatas.getString(10)
                                , getcartdatas.getString(11), getcartdatas.getString(12), getcartdatas.getString(13),
                                getcartdatas.getString(14), getcartdatas.getString(15)
                                , getcartdatas.getString(16),
                                getcartdatas.getString(17), getcartdatas.getString(18),
                                getcartdatas.getString(19), getcartdatas.getString(20),
                                getcartdatas.getString(21), getcartdatas.getString(22), getcartdatas.getString(23),
                                getcartdatas.getString(24), getcartdatas.getString(25), getcartdatas.getString(26)
                                , getcartdatas.getString(27), getcartdatas.getString(28), getcartdatas.getString(29),
                                getcartdatas.getString(30), getcartdatas.getString(31), getcartdatas.getString(21),
                                "", "", getcartdatas.getString(32),"","",
                                getcartdatas.getString(34),getcartdatas.getString(35),getcartdatas.getString(36),""));
                        getcartdatas.moveToNext();
                    }
                    //Adapter
                    ReviewItemAdapter adapter = new ReviewItemAdapter(context, SalesActivity.staticreviewsalesitems);
                    reviewlistview.setAdapter(adapter);
                }else{
                    SalesActivity.staticreviewsalesitems.clear();
                    reviewlistview.setAdapter(null);
                    Toast toast = Toast.makeText(getApplicationContext(),"Cart is empty", Toast.LENGTH_LONG);
                    //toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    //Toast.makeText(getApplicationContext(),"Cart is empty",Toast.LENGTH_SHORT).show();
                    reviewitems.setText(String.valueOf(salesItemList.size()));
                    //  SalesActivity.totalcartitems.setText(String.valueOf(salesItemList.size()));
                    return;
                }
            }catch (Exception e) {
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName()+
                        " - setItemAdapter", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            } finally {
                if (dataBaseAdapter != null)
                    dataBaseAdapter.close();
                if (getcartdatas != null)
                    getcartdatas.close();
            }

        }else{
            SalesActivity.staticreviewsalesitems.clear();
            reviewlistview.setAdapter(null);
            Toast toast = Toast.makeText(getApplicationContext(),"Cart is empty", Toast.LENGTH_LONG);
            //toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            //Toast.makeText(getApplicationContext(),"Cart is empty",Toast.LENGTH_SHORT).show();
            reviewitems.setText(String.valueOf(salesItemList.size()));
            //  SalesActivity.totalcartitems.setText(String.valueOf(salesItemList.size()));
            return;
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
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName()
                    +" - Network ", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
            result=false;

        }
        return result;
    }
    public class ReviewItemAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;

        DecimalFormat  dft = new DecimalFormat("0.00");
        ReviewItemAdapter(Context c,ArrayList<SalesItemDetails> myList) {
            salesItemList = myList;
            context = c;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return salesItemList.size();
        }

        @Override
        public SalesItemDetails getItem(int position) {
            return (SalesItemDetails) salesItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
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

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder1 mHolder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.salesreviewlist, parent, false);
                mHolder = new ViewHolder1();
                try {
                    mHolder.listitemname = (TextView) convertView.findViewById(R.id.listitemname);
                    mHolder.listitemcode = (TextView) convertView.findViewById(R.id.listitemcode);
                    mHolder.listitemqty = (TextView) convertView.findViewById(R.id.listitemqty);
                    mHolder.listitemrate = (TextView) convertView.findViewById(R.id.listitemrate);
                    mHolder.listitemtotal = (TextView) convertView.findViewById(R.id.listitemtotal);
                    mHolder.listitemtax = (TextView) convertView.findViewById(R.id.listitemtax);
                    // mHolder.labelstock = (TextView)convertView.findViewById(R.id.labelstock);
                    mHolder.labelhsntax = (TextView)convertView.findViewById(R.id.labelhsntax);
                    mHolder.itemLL = (LinearLayout)convertView.findViewById(R.id.itemLL);
                    mHolder.stockvalueLL = (LinearLayout)convertView.findViewById(R.id.stockvalueLL);
                    mHolder.labelnilstock = (TextView)convertView.findViewById(R.id.labelnilstock);
                    mHolder.labelstockunit = (TextView)convertView.findViewById(R.id.labelstockunit);
                    mHolder.pricearrow = (ImageView)convertView.findViewById(R.id.pricearrow);
                    mHolder.listdiscount = (TextView)convertView.findViewById(R.id.listdiscount);
                    mHolder.deleteitem  = (ImageView)convertView.findViewById(R.id.deleteitem);
                    mHolder.dummydeleteitem = (TextView)convertView.findViewById(R.id.dummydeleteitem);

                    convertView.setTag(mHolder);
                    convertView.setTag(R.id.listitemname, mHolder.listitemname);
                    convertView.setTag(R.id.listitemcode, mHolder.listitemcode);
                    convertView.setTag(R.id.listitemqty, mHolder.listitemqty);
                    convertView.setTag(R.id.listitemrate, mHolder.listitemrate);
                    convertView.setTag(R.id.listitemtotal, mHolder.listitemtotal);
                    convertView.setTag(R.id.listitemtax, mHolder.listitemtax);
                    // convertView.setTag(R.id.labelstock, mHolder.labelstock);
                    convertView.setTag(R.id.labelhsntax, mHolder.labelhsntax);
                    convertView.setTag(R.id.labelstockunit, mHolder.labelstockunit);
                } catch (Exception e) {
                    Log.i("Route", e.toString());
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName()
                            +" - convertView for Review cart adapter", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                }
                convertView.setTag(mHolder);
            } else {
                mHolder = (ViewHolder1) convertView.getTag();
            }
            mHolder.listitemname.setTag(position);
            mHolder.listitemcode.setTag(position);
            mHolder.listitemqty.setTag(position);
            mHolder.listitemrate.setTag(position);
            mHolder.listitemtotal.setTag(position);
            mHolder.listitemtax.setTag(position);
            mHolder.labelstockunit.setTag(position);
            mHolder.labelhsntax.setTag(position);
            try {

                String getdecimalvalue  = salesItemList.get(position).getNoofdecimals();

                String getnoofdigits = "0";
                if(getdecimalvalue.equals("0")){
                    getnoofdigits = "";
                }
                if(getdecimalvalue.equals("1")){
                    getnoofdigits = "0";
                }
                if(getdecimalvalue.equals("2")){
                    getnoofdigits = "00";
                }
                if(getdecimalvalue.equals("3")){
                    getnoofdigits = "000";
                }

                df = new DecimalFormat("0.'"+getnoofdigits+"'");

                mHolder.dummydeleteitem.setVisibility(View.GONE);
                if (!(salesItemList.get(position).getItemnametamil().equals(""))
                        && !(salesItemList.get(position).getItemnametamil()).equals("null")
                        && !((salesItemList.get(position).getItemnametamil()).equals(null))) {
                    mHolder.listitemname.setText(String.valueOf(salesItemList.get(position).getItemnametamil()));
                } else {
                    mHolder.listitemname.setText(String.valueOf(salesItemList.get(position).getItemname()));
                }
                mHolder.listitemcode.setText(String.valueOf(salesItemList.get(position).getItemcode()));

                if(!String.valueOf(salesItemList.get(position).getNewprice()).equals("null")
                        && !String.valueOf(salesItemList.get(position).getNewprice()).equals("")
                        &&!String.valueOf(salesItemList.get(position).getNewprice()).equals(null)) {
                    mHolder.listitemrate.setText(dft.format(Double.parseDouble(salesItemList.get(position).getNewprice())));

                }else{
                    mHolder.listitemrate.setText(dft.format(Double.parseDouble("0")));

                }
                mHolder.listitemname.setTextColor(Color.parseColor(salesItemList.get(position).getColourcode()));

                if(Double.parseDouble(salesItemList.get(position).getOldprice()) >
                        Double.parseDouble(salesItemList.get(position).getNewprice()) ){
                    mHolder.pricearrow.setImageResource(R.drawable.ic_arrow_downward);
                }else{
                    mHolder.pricearrow.setImageResource(R.drawable.ic_arrow_upward_black_24dp);
                }


                mHolder.labelhsntax.setText(salesItemList.get(position).getHsn() +" @ "+salesItemList.get(position).getTax() +"%");
                if(getnoofdigits!="") {
                    mHolder.listitemqty.setText(df.format(Double.parseDouble(salesItemList.get(position).getItemqty())));
                }else{
                    mHolder.listitemqty.setText(salesItemList.get(position).getItemqty());
                }
                mHolder.listitemtotal.setText(dft.format(Double.parseDouble(salesItemList.get(position).getSubtotal())));
                mHolder.labelstockunit.setText(salesItemList.get(position).getUnitname());

                if(!salesItemList.get(position).getDiscount().equals("")
                        && !salesItemList.get(position).getDiscount().equals(null)){
                   /* mHolder.listdiscount.setBackgroundColor(getResources().getColor(R.color.orangecolor));
                    mHolder.listdiscount.setText("Disc " + dft.format(Double.parseDouble(salesItemList.get(position).getDiscount())));*/
                    mHolder.listdiscount.setBackgroundColor(getResources().getColor(R.color.lightbiscuit));
                    mHolder.listdiscount.setText("");
                }else{
                    mHolder.listdiscount.setBackgroundColor(getResources().getColor(R.color.lightbiscuit));
                    mHolder.listdiscount.setText("");
                }

                //Check Free Item
                if(salesItemList.get(position).getFreeflag().equals("freeitem")){
                    mHolder.itemLL.setBackgroundColor(getResources().getColor(R.color.lightblue));
                    mHolder.deleteitem.setVisibility(View.GONE);
                    mHolder.listdiscount.setBackgroundColor(getResources().getColor(R.color.lightblue));
                    mHolder.listdiscount.setText("");
                    mHolder.dummydeleteitem.setVisibility(View.VISIBLE);
                }else{
                    mHolder.itemLL.setBackgroundColor(getResources().getColor(R.color.lightbiscuit));
                    mHolder.deleteitem.setVisibility(View.VISIBLE);
                    mHolder.dummydeleteitem.setVisibility(View.GONE);
                }


            } catch (Exception e) {
                Log.i("Item value", e.toString());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName()
                        +" - set text for Review cart adapter", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            //Delete click listener
            mHolder.deleteitem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Are you sure you want to delete ?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    boolean deletefree = false;
                                    int getpurchasitemposition = 011111111222;
                                    String getitemcode = salesItemList.get(position).getItemcode();
                                    String getparentitemcode=salesItemList.get(position).getParentitemcode();
                                    DataBaseAdapter objdatabaseadapter = null;
                                    Cursor getcartdatas = null;
                                    try {
                                        //Order item details
                                        objdatabaseadapter = new DataBaseAdapter(context);
                                        objdatabaseadapter.open();
                                        String getresult = objdatabaseadapter.DeleteItemInCart(getitemcode);
                                        String getresult1 = objdatabaseadapter.DeleteItemInStockConversion(getitemcode,getparentitemcode);
                                        if(getresult.equals("Success") && getresult1.equals("Success")){
                                            SalesActivity.gblitemcount=0;
                                            Toast toast = Toast.makeText(getApplicationContext(),"Item removed from cart", Toast.LENGTH_LONG);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();

                                            //Get cart datas from database temp table
                                            getcartdatas = objdatabaseadapter.GetSalesItemsCart();
                                            SalesActivity.staticreviewsalesitems.clear();
                                            if(getcartdatas.getCount()>0) {

                                                for (int i = 0; i < getcartdatas.getCount(); i++) {
                                                    SalesActivity.staticreviewsalesitems.add(new SalesItemDetails(getcartdatas.getString(1), getcartdatas.getString(2),
                                                            getcartdatas.getString(3), getcartdatas.getString(4)
                                                            , getcartdatas.getString(5), getcartdatas.getString(6),
                                                            getcartdatas.getString(7), getcartdatas.getString(8)
                                                            , getcartdatas.getString(9),getcartdatas.getString(10)
                                                            , getcartdatas.getString(11), getcartdatas.getString(12), getcartdatas.getString(13),
                                                            getcartdatas.getString(14), getcartdatas.getString(15)
                                                            , getcartdatas.getString(16),
                                                            getcartdatas.getString(17), getcartdatas.getString(18),
                                                            getcartdatas.getString(19), getcartdatas.getString(20),
                                                            getcartdatas.getString(21), getcartdatas.getString(22), getcartdatas.getString(23),
                                                            getcartdatas.getString(24), getcartdatas.getString(25), getcartdatas.getString(26)
                                                            , getcartdatas.getString(27), getcartdatas.getString(28), getcartdatas.getString(29),
                                                            getcartdatas.getString(30), getcartdatas.getString(31) ,getcartdatas.getString(21),
                                                            "","",getcartdatas.getString(32),"","",getcartdatas.getString(34),
                                                            getcartdatas.getString(35),getcartdatas.getString(36),""));
                                                    getcartdatas.moveToNext();
                                                }
                                            }
                                            setItemAdapter();
                                            CalculateTotal();

                                            if(SalesActivity.staticreviewsalesitems.size()==0){
                                                SalesActivity.txtcustomername.setEnabled(true);
                                                SalesActivity.txtareaname.setEnabled(true);
                                            }
                                        }


                                    } catch (Exception e) {
                                        DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                        mDbErrHelper.open();
                                        String geterrror = e.toString();
                                        mDbErrHelper.insertErrorLog(geterrror.replace("'", " "),
                                                this.getClass().getSimpleName()+" - Delete itemcart when click delete", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                        mDbErrHelper.close();
                                    } finally {
                                        if(objdatabaseadapter!=null)
                                            objdatabaseadapter.close();
                                        if(getcartdatas!=null)
                                            getcartdatas.close();
                                    }
                                    /*for(int p=0;p<SalesActivity.staticreviewsalesitems.size();p++){
                                        if(salesItemList.get(position).getItemcode().equals
                                                (SalesActivity.staticreviewsalesitems.get(p).getPurchaseitemcode())
                                        && salesItemList.get(p).getFreeflag().equals("freeitem")){
                                            deletefree = true;
                                            getpurchasitemposition = p;
                                        }
                                    }
                                    if(deletefree){
                                        SalesActivity.staticreviewsalesitems.remove(position);
                                        SalesActivity.staticreviewsalesitems.remove(getpurchasitemposition);
                                        Toast toast = Toast.makeText(getApplicationContext(),"Item removed from cart", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                       // Toast.makeText(getApplicationContext(),"Item removed from cart",Toast.LENGTH_SHORT).show();
                                        setItemAdapter();
                                        CalculateTotal();
                                        return;
                                    }else{
                                        SalesActivity.staticreviewsalesitems.remove(position);
                                        Toast toast = Toast.makeText(getApplicationContext(),"Item removed from cart", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                       // Toast.makeText(getApplicationContext(),"Item removed from cart",Toast.LENGTH_SHORT).show();
                                        setItemAdapter();
                                        return;
                                    }*/
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
            CalculateTotal();

            return convertView;
        }

        private class ViewHolder1 {
            private TextView listitemname,dummydeleteitem;
            private TextView listitemcode,labelnilstock,listitemqty,listitemrate;
            private TextView listitemtotal,listitemtax,labelhsntax,labelstockunit,listdiscount;
            private LinearLayout itemLL,stockvalueLL;
            private  ImageView pricearrow,deleteitem;
        }
    }
    public void CalculateTotal() {
        final DecimalFormat dft = new DecimalFormat("0.00");
        double res1 = 0;
        double res2 = 0;
        double res3 = 0;
        double res4 = 0;
        for (int i = 0; i < salesItemList.size(); i++) {
            String salessubtotal = salesItemList.get(i).getSubtotal();
            String salesweight = salesItemList.get(i).getUnitweight();
            String salesdiscounttotal = salesItemList.get(i).getSubtotal();
            String salesqty = salesItemList.get(i).getItemqty();

            String getsalessubtotal;
            String getsalesweight;
            String getdiscount;
            String getqty;
            if (salessubtotal.equals("")) {
                getsalessubtotal = "0";
            } else {
                getsalessubtotal = salessubtotal;
            }
            if (salesweight.equals("")) {
                getsalesweight = "0";
            } else {
                getsalesweight = salesweight;
            }
            if (salesdiscounttotal.equals("")) {
                getdiscount = "0";
            } else {
                getdiscount = salesdiscounttotal;
            }
            if (salesqty.equals("")) {
                getqty = "0";
            } else {
                getqty = salesqty;
            }
            if(salesItemList.get(i).getFreeflag().equals("freeitem")){
                res3 = res3 + Double.parseDouble(getdiscount);
            }
            res1 = res1 + Double.parseDouble(getsalessubtotal);
            res2 = res2 + (Double.parseDouble(getqty)*Double.parseDouble(getsalesweight));
            res4 = res1 - res3;
        }
        getsubtotalamount = dft.format( Math.round(res1));
        getdiscountamt = dft.format( Math.round(res3));
        getgrandtotal =dft.format( Math.round(res4));
        txtsubtotalamt.setText("Total    ₹  "+dft.format( Math.round(res1)));
        txtreviewweight.setText(String.valueOf(dft.format(res2)));
        txtdiscountamt.setText("Discount ₹  "+dft.format(Math.round(res3)));
        txtcarttotalamt.setText(dft.format(Math.round(res4)));

        reviewitems.setText(String.valueOf(salesItemList.size()));
        SalesActivity.totalcartitems.setText(String.valueOf(salesItemList.size()));
    }

    /**********Asynchronous Claass***************/

    protected  class AsyncSalesDetails extends
            AsyncTask<String, JSONObject, ArrayList<SalesSyncDatas>> {
        ArrayList<SalesSyncDatas> List = null;
        JSONObject jsonObj = null;
        String from="";
        @Override
        protected  ArrayList<SalesSyncDatas> doInBackground(String... params) {
            RestAPI api = new RestAPI();
            String result = "";
            from=params[0];
            try {
                if (context == null && getApplicationContext() != null)
                    context=getApplicationContext();
                JSONObject js_obj = new JSONObject();
                JSONObject js_salesobj = new JSONObject();
                JSONObject js_salesitemobj = new JSONObject();
                JSONObject js_stockobj = new JSONObject();
                DataBaseAdapter dbadapter = null;
                Cursor mCur2 = null;
                Cursor mCursales = null;
                Cursor mCursalesitems = null;
                Cursor mCurStock = null;

                try {
                    int count = 0,salescount=0,salesitemscount=0,Stockcount=0;
                    dbadapter = new DataBaseAdapter(context);
                    dbadapter.open();
                    mCur2 = dbadapter.GetCustomerDatasDB();
                    mCursales = dbadapter.GetSalesDatasDB();
                    mCursalesitems = dbadapter.GetSalesItemDatasDB();
                    mCurStock = dbadapter.GetSalesStockTransactionDatasDB();
                    JSONArray js_array2 = new JSONArray();
                    JSONArray js_array3 = new JSONArray();
                    JSONArray js_array4 = new JSONArray();
                    JSONArray js_stockarray = new JSONArray();
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

                        js_array4.put(obj);
                        mCur2.moveToNext();
                    }
                    if (mCur2 != null && !mCur2.isClosed()){
                        count = mCur2.getCount();
                        mCur2.close();

                    }

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
                        String latlong = "";
                        if (!mCursales.isNull(42))
                            latlong = mCursales.getString(42);
                        obj.put("latlong", latlong);

                        String orderTransNo = "";
                        if (!mCursales.isNull(43))
                            orderTransNo = mCursales.getString(43);
                        obj.put("ordertransactionno", orderTransNo);

                        js_array2.put(obj);
                        mCursales.moveToNext();
                    }
                    if (mCursales != null && !mCursales.isClosed()){
                        salescount = mCursales.getCount();
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
                        obj.put("orgprice", mCursalesitems.getString(25));
                        js_array3.put(obj);
                        mCursalesitems.moveToNext();
                    }
                    if (mCursalesitems != null && !mCursalesitems.isClosed()){
                        salesitemscount = mCursalesitems.getCount();
                        mCursalesitems.close();

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
                        obj.put("financialyearcode", mCurStock.getString(11));
                        obj.put("autonum", mCurStock.getString(12));
                        js_stockarray.put(obj);
                        mCurStock.moveToNext();
                    }
                    if (mCurStock != null && !mCurStock.isClosed()){
                        Stockcount = mCurStock.getCount();
                        mCurStock.close();

                    }
                    js_obj.put("JSonObject", js_array4);
                    js_salesobj.put("JSonObject", js_array2);
                    js_salesitemobj.put("JSonObject", js_array3);
                    js_stockobj.put("JSonObject",js_stockarray);

                    jsonObj =  api.SalesDetails(js_salesobj.toString(),js_salesitemobj.toString(),js_stockobj.toString(),js_obj.toString(),context);
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
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName()+
                            " - AsyncSalesDetails api call", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                }finally {
                    if (dbadapter != null)
                        dbadapter.close();
                    if (mCur2 != null)
                        mCur2.close();
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
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName()+
                        " - AsyncSalesDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
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
                            objdatabaseadapter.UpdateSalesFlag(List.get(0).TransactionNo[j]);
                        }
                    }
                    if (List.get(0).SalesItemTransactionNo.length > 0) {
                        for (int j = 0; j < List.get(0).SalesItemTransactionNo.length; j++) {
                            objdatabaseadapter.UpdateSalesItemFlag(List.get(0).SalesItemTransactionNo[j]);
                        }
                    }
                    if (List.get(0).StockTransactionNo.length > 0) {
                        for (int j = 0; j < List.get(0).StockTransactionNo.length; j++) {
                            String[] getArr = List.get(0).StockTransactionNo[j].split("~");
                            objdatabaseadapter.UpdateSalesStockTransactionFlag(getArr[0],getArr[1]);
                        }
                    }
                }
            }catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncSalesDetails", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName()+
                        " post execute for updatesalesflag and updateslesitemflag ", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            } finally {
                if(objdatabaseadapter!=null)
                    objdatabaseadapter.close();
            }
            if(from=="true")
                new AsyncNilStockDetails().execute();

        }
    }

    /**********END Asynchronous Claass***************/


    /***********************CAMERA FUNCTION***********/
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] arr=baos.toByteArray();
        String result= Base64.encodeToString(arr, Base64.DEFAULT);
        return result;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_PIC_REQUEST && resultCode == RESULT_OK) {
            bitmap1 = (Bitmap) data.getExtras().get("data");
            varimageview = BitMapToString(bitmap1);

            String uploadImagebitmap = getStringImage(bitmap1);

            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String getupdateimgcount = mDbErrHelper.updateSalesImageData(getsalestransactionno, preferenceMangr.pref_getString("getfinanceyrcode") , uploadImagebitmap);
            mDbErrHelper.close();
            networkstate = isNetworkAvailable();
            if (networkstate == true) {
                new UploadImage().execute();
            }
            if (Integer.parseInt(getupdateimgcount) > 0) {
                Toast toast = Toast.makeText(getApplicationContext(),"Image Captured", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                // Toast.makeText(getApplicationContext(),"Image Captured",Toast.LENGTH_SHORT).show();
                SalesActivity.staticreviewsalesitems.clear();
                SalesActivity.salesitems.clear();
                txtSalesprint.setEnabled(true);
                Intent i = new Intent(ReviewActivity.this, SalesListActivity.class);
                startActivity(i);
                networkstate = isNetworkAvailable();
                if (networkstate == true) {
                    new AsyncSalesDetails().execute("false");
                }
            }else{
                Toast toast = Toast.makeText(getApplicationContext(),"Error in image Captured", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //Toast.makeText(getApplicationContext(),"Error in image Captured",Toast.LENGTH_SHORT).show();
                SalesActivity.staticreviewsalesitems.clear();
                SalesActivity.salesitems.clear();
                txtSalesprint.setEnabled(true);
                Intent i = new Intent(ReviewActivity.this, SalesListActivity.class);
                startActivity(i);
                networkstate = isNetworkAvailable();
                if (networkstate == true) {
                    new AsyncSalesDetails().execute("false");
                }
            }
        }

    }
    public  class UploadImage extends AsyncTask<String, JSONObject, String> {

        ProgressDialog loading;
        RequestHandler rh = new RequestHandler();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {

        }

        @Override
        protected String doInBackground(String... params) {
            // Bitmap bitmap = params[0];
            String result="";
            DataBaseAdapter objcustomerAdaptor = null;
            Cursor mCur = null;
            try{
                objcustomerAdaptor = new DataBaseAdapter(context);
                objcustomerAdaptor.open();
                mCur = objcustomerAdaptor.GetSalesImageDetails();
                String uploadImage;
                for (int i=0;i<mCur.getCount();i++){
                    if(mCur.getString(1) == null){
                        uploadImage="";
                    }else {
                        uploadImage = mCur.getString(1);
                    }
                    HashMap<String, String> data = new HashMap<>();
                    data.put("paraimage", uploadImage);
                    data.put("paratransno", mCur.getString(0));
                    data.put("parafinacialyearcode", mCur.getString(2));
                    data.put("paraimagecode", mCur.getString(3));

                    result = rh.sendPostRequest(UPLOAD_URL, data);
                    if(result.equals("Success")) {
                        DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                        mDbErrHelper.open();
                        mDbErrHelper.SalesImageSetFlag(mCur.getString(0),mCur.getString(2));
                        mDbErrHelper.close();
                    }
                    mCur.moveToNext();
                    if (mCur != null)
                        mCur.close();
                }
            }catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncScheduleDetails", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName()+
                        " - UploadImage", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }finally {
                if (objcustomerAdaptor != null)
                    objcustomerAdaptor.close();
                if (mCur != null)
                    mCur.close();
            }

            return result;
        }
    }


    public void goBack(View v) {
        if(SalesActivity.ifsavedsales){
            SalesActivity.staticreviewsalesitems.clear();
            SalesActivity.salesitems.clear();
            Intent i = new Intent(context, SalesListActivity.class);
            startActivity(i);
        }else {
            Intent i = new Intent(context, SalesActivity.class);
            startActivity(i);
        }
    }
    @Override
    public void onBackPressed() {
        if(SalesActivity.ifsavedsales){
            SalesActivity.staticreviewsalesitems.clear();
            SalesActivity.salesitems.clear();
            Intent i = new Intent(context, SalesListActivity.class);
            startActivity(i);
        }else {
            isfromcart = true;
            finish();
        }
    }

    protected class AsyncPrintSalesDetails extends
            AsyncTask<String, JSONObject, Boolean> {
        Boolean billPrinted = false;
        String Getbilltypecode = null;
        JSONObject jsonObj = null;
        String finalGetsalestransano;
        String financialyearcode;

        @Override
        protected Boolean doInBackground(String... params) {
            finalGetsalestransano = params[0];
            financialyearcode = params[1];
            Getbilltypecode = params[2];
            try {
                //printData = new PrintData(context);
                EpsonT20Printer epsonT20Printer = new EpsonT20Printer(ReviewActivity.this, ReviewActivity.this, receiveListener);
                deviceFound = epsonT20Printer.findBT();
                Log.d("deviceFound",String.valueOf(deviceFound));
                if (!deviceFound) {
                    /*Toast toast = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);*/
                    //printpopup.dismiss();
                    Log.d("Device found",String.valueOf(deviceFound));
                    billPrinted = deviceFound;
                    //toast.show();
                } else {
                    Log.d("Begin Print",String.valueOf(deviceFound));
                    billPrinted = (boolean) epsonT20Printer.GetSalesBillPrint(finalGetsalestransano, financialyearcode,ReviewActivity.this, true);
                    //printpopup.dismiss();
                }
                Log.d("After print",String.valueOf(billPrinted));
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
                mDbErrHelper2.insertErrorLog(e.toString(), this.getClass().getSimpleName()+
                        " - AsyncPrintSalesDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper2.close();
            }
            return billPrinted;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SalesPrintloading = ProgressDialog.show(ReviewActivity.this, "Connecting to printer", "Please wait", true);
            SalesPrintloading.setCancelable(false);
            SalesPrintloading.setCanceledOnTouchOutside(false);
        }
        @Override
        protected void onPostExecute(Boolean billPrinted) {
            // TODO Auto-generated method stub
            try {
                //Thread.sleep(2000);
                SalesPrintloading.dismiss();
                Log.d("billPrinted 1q",String.valueOf(billPrinted));
                SalesActivity.staticreviewsalesitems.clear();
                SalesActivity.salesitems.clear();
                txtSalesprint.setVisibility(View.GONE);
                txtSalesprint.setEnabled(true);
                Intent i = new Intent(ReviewActivity.this, SalesListActivity.class);
                startActivity(i);

                //SalesDCprint(finalGetsalestransano, financialyearcode,Getbilltypecode);

                //new AsyncPrintSalesDCDetails().execute(finalGetsalestransano, financialyearcode,Getbilltypecode);


                // sales delivery note print function
                //billPrinted = (boolean) printData.GetDCPrint(finalGetsalestransano, financialyearcode,ReviewActivity.this);

                /*if (!billPrinted) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Unable to connect Bluetooth Printer. Please check the printer is turn or or not!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    //printpopup.dismiss();
                    toast.show();
                    if(Getbilltypecode.equals("2")) {
                        imgcamera.setVisibility(View.VISIBLE);
                        txtSalesprint.setVisibility(View.GONE);
                    }else {
                        SalesActivity.staticreviewsalesitems.clear();
                        SalesActivity.salesitems.clear();
                        txtSalesprint.setVisibility(View.GONE);
                        txtSalesprint.setEnabled(true);
                        Intent i = new Intent(ReviewActivity.this, SalesListActivity.class);
                        startActivity(i);

                    }

                    //Toast.makeText(context, "Unable to connect to Bluetooth Printer!", Toast.LENGTH_SHORT).show();
                    printData = null;

                }
                SalesActivity.staticreviewsalesitems.clear();
                SalesActivity.salesitems.clear();
                txtSalesprint.setVisibility(View.GONE);
                txtSalesprint.setEnabled(true);
                Intent i = new Intent(ReviewActivity.this, SalesListActivity.class);
                startActivity(i);*/

            }catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncScheduleDetails", e.getLocalizedMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName()+
                        " - call DC print", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        }
    }


    protected class AsyncPrintSalesDCDetails extends
            AsyncTask<String, JSONObject, Boolean> {
        Boolean billPrinted = false;
        JSONObject jsonObj = null;
        String Getbilltypecode;
        ProgressDialog loader = null;
        @Override
        protected Boolean doInBackground(String... params) {
            final String finalGetsalestransano = params[0];
            final String financialyearcode = params[1];
            Getbilltypecode = params[2];
            try {
                //Log.d("AsyncDOINB_DCPrint", "AsyncDOINB_DCPrint");
                printData = new PrintData(context);
                deviceFound = printData.findBT();
                Log.d("deviceFound",String.valueOf(deviceFound));
                if (!deviceFound) {
                    billPrinted = deviceFound;
                } else {
                    billPrinted = (boolean) printData.GetDCPrint(finalGetsalestransano, financialyearcode,ReviewActivity.this);

                }
                Log.d("billPrinted",String.valueOf(billPrinted));
            }
            catch (Exception e){
                printData = null;
                billPrinted = true;

                Log.d("Sales_DC_PRINT", e.getLocalizedMessage());
                DataBaseAdapter mDbErrHelper2 = new DataBaseAdapter(context);
                mDbErrHelper2.open();
                mDbErrHelper2.insertErrorLog(e.toString(), this.getClass().getSimpleName()+
                        " - AsyncPrintSalesDCDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper2.close();
            }
            return billPrinted;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loader = ProgressDialog.show(ReviewActivity.this, "Connecting to printer", "Please wait");
            loader.setCanceledOnTouchOutside(false);
            loader.setCancelable(false);
        }
        @Override
        protected void onPostExecute(Boolean billPrinted) {
            // TODO Auto-generated method stub
            try {
                //Log.d("AsyncPOST_DCPrint",String.valueOf(billPrinted));
                loader.dismiss();
                if (!billPrinted) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Unable to connect Bluetooth Printer. Please check the printer is turn or or not!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    if(Getbilltypecode.equals("2")) {
                        imgcamera.setVisibility(View.VISIBLE);
                        txtSalesprint.setVisibility(View.GONE);
                    }
                    printData = null;

                }
                SalesActivity.staticreviewsalesitems.clear();
                SalesActivity.salesitems.clear();
                txtSalesprint.setVisibility(View.GONE);
                txtSalesprint.setEnabled(true);
                Intent i = new Intent(ReviewActivity.this, SalesListActivity.class);
                startActivity(i);

            }catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncScheduleDetails", e.getLocalizedMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName()+
                        " - Check Bluetooth in post execute", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        }
    }

//    private void runThread() {
//
//        new Thread() {
//            public void run() {
////                while (i++ < 1000) {
//                    try {
//                        runOnUiThread(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                AsyncPriceListTransaction();
//                            }
//                        });
//                        Thread.sleep(300);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
////            }
//        }.start();
//    }
//    public void AsyncPriceListTransaction(){
//        JSONObject jsonObj = null;
//        RestAPI api = new RestAPI();
//        String result = "";
//        DataBaseAdapter pricelistdataBaseAdapter =null;
//        String deviceid = preferenceMangr.pref_getString("deviceid");
//
//        try {
//            if (context == null && getApplicationContext() != null)
//                context=getApplicationContext();
//            pricelistdataBaseAdapter = new DataBaseAdapter(context);
//            pricelistdataBaseAdapter.open();
//            networkstate = isNetworkAvailable();
//            if (networkstate == true) {
//                //itemn price list transaction
//                jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncitempricelisttransaction.php",context);
//                if (isSuccessful(jsonObj)) {
//                    pricelistdataBaseAdapter.syncitempricelisttransaction(jsonObj);
//
//                            /*Calendar calendar = Calendar.getInstance();
//                            SimpleDateFormat mformat= new SimpleDateFormat("dd-MM-yyyy h:mm a");
//                            MenuActivity.pricelistlastsyncdate = mformat.format(calendar.getTime());*/
//                    //Toast.makeText(context,pricelistlastsyncdate,Toast.LENGTH_SHORT).show();
//
//                    api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "itempricelisttransaction", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
//                }
//
//
//            }
//
//
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            Log.d("AsyncPriceList", e.getMessage());
//            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
//            mDbErrHelper.open();
//            String geterrror = e.toString();
//            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "),
//                    this.getClass().getSimpleName() +" Review Activity Sync - AsyncPriceListTransaction", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
//            mDbErrHelper.close();
//        }
//        finally {
//            if (pricelistdataBaseAdapter != null) {
//                pricelistdataBaseAdapter.close();
//            }
//        }
//
//    }
    protected  class AsyncPriceListTransaction extends
            AsyncTask<String, JSONObject, String> {
        String List = "Success";
        JSONObject jsonObj = null;
        ProgressDialog loading;

        @Override
        protected  String doInBackground(String... params) {

            RestAPI api = new RestAPI();
            String result = "";
            DataBaseAdapter pricelistdataBaseAdapter =null;
            String deviceid = preferenceMangr.pref_getString("deviceid");

            try {
                pricelistdataBaseAdapter = new DataBaseAdapter(context);
                pricelistdataBaseAdapter.open();
                networkstate = isNetworkAvailable();
                if (networkstate == true) {
                    //itemn price list transaction
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncitempricelisttransaction.php",context);
                    if (isSuccessful(jsonObj)) {
                        pricelistdataBaseAdapter.syncitempricelisttransaction(jsonObj);

                        /*Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat mformat= new SimpleDateFormat("dd-MM-yyyy h:mm a");
                        MenuActivity.pricelistlastsyncdate = mformat.format(calendar.getTime());*/
                        //Toast.makeText(context,pricelistlastsyncdate,Toast.LENGTH_SHORT).show();

                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "itempricelisttransaction", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }
                }


            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncPriceList", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "),
                        this.getClass().getSimpleName() +" Review Activity Sync - AsyncPriceListTransaction", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            finally {
                if (pricelistdataBaseAdapter != null) {
                    pricelistdataBaseAdapter.close();
                }
            }
            return List;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //loading = ProgressDialog.show(context, "Synching Customer Data", "Please wait...", true, true);
            //loading.setCancelable(false);
            //loading.setCanceledOnTouchOutside(false);
        }
        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
//            new AsyncSalesDetails().execute();
           /* try {
                //loading.dismiss();


            }catch (Exception e) {
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }*/

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

    protected  class AsyncSalesOrderDetails extends
            AsyncTask<String, JSONObject, ArrayList<SalesSyncDatas>> {
        ArrayList<SalesSyncDatas> List = null;
        JSONObject jsonObj = null;
        @Override
        protected  ArrayList<SalesSyncDatas> doInBackground(String... params) {
            RestAPI api = new RestAPI();
            String result = "";
            try {
                JSONObject js_obj = new JSONObject();
                JSONObject js_salesobj = new JSONObject();
                JSONObject js_salesitemobj = new JSONObject();
                try {
                    DataBaseAdapter dbadapter = new DataBaseAdapter(context);
                    dbadapter.open();
                    Cursor mCursales = dbadapter.GetSalesOrderDatasDB();
                    JSONArray js_array2 = new JSONArray();
                    JSONArray js_array3 = new JSONArray();
                    JSONArray js_array4 = new JSONArray();

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
                        obj.put("transportid", mCursales.getString(31));
                        obj.put("status", mCursales.getString(32));
                        obj.put("transportmode", mCursales.getString(33));
                        obj.put("latlong", mCursales.getString(34));

                        js_array2.put(obj);
                        mCursales.moveToNext();
                    }



                    js_obj.put("JSonObject", js_array4);
                    js_salesobj.put("JSonObject", js_array2);
                    js_salesitemobj.put("JSonObject", js_array3);

                    jsonObj =  api.SalesOrderDetails(js_salesobj.toString(),js_salesitemobj.toString(),js_obj.toString(),context);
                    //Call Json parser functionality
                    JSONParser parser = new JSONParser();
                    //parse the json object to boolean
                    List = parser.parseSalesDataList(jsonObj);
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
                Log.d("AsyncSalesDetails", e.getMessage());
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
        protected void onPostExecute(ArrayList<SalesSyncDatas> List) {
            // TODO Auto-generated method stub
            DataBaseAdapter objdatabaseadapter = new DataBaseAdapter(context);
            try {
                objdatabaseadapter.open();
                if (List.size() >= 1) {
                    if (List.get(0).TransactionNo.length > 0) {
                        for (int j = 0; j < List.get(0).TransactionNo.length; j++) {
                            objdatabaseadapter.UpdateSalesOrderFlag(List.get(0).TransactionNo[j]);
                        }
                    }
                    if (List.get(0).SalesItemTransactionNo.length > 0) {
                        for (int j = 0; j < List.get(0).SalesItemTransactionNo.length; j++) {
                            objdatabaseadapter.UpdateSalesOrderItemFlag(List.get(0).SalesItemTransactionNo[j]);
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
            }

        }
    }



}
