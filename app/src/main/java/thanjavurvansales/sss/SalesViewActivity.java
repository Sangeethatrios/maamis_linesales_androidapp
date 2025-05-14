package thanjavurvansales.sss;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class SalesViewActivity extends AppCompatActivity {

    TextView txtviewbookingno,txtviewreviewdate,viewpaymenttypeinvoice,txtviewcustomername,txtviewareacity,
            viewcartgstnnumber,txtviewsubtotalamt,txtviewdiscountamt,txtviewcancel,txtviewcarttotalamt,
            txtviewSalesprint,reviewitems,txtreviewweight,txtviewcanceldummy,hidedummy,hidedummy1,txtbillno
            ,txtviewshortname,txtviewtotamt,  txtviewSalesDCprint;
    ListView viewSalesListview;
    Context context;
    ImageView cameraview,imgcamera;
    public static boolean deviceFound;
    ImageButton viewlistgoback;
    ArrayList<SalesItemListDetails> salesitemviewlist =new ArrayList<SalesItemListDetails>();
    LinearLayout gstnLL;
    DecimalFormat dft=new DecimalFormat("0.00");
    boolean networkstate;
    private static final int CAMERA_PIC_REQUEST = 1111;
    Bitmap bitmap1=null;
    static String varimageview = "";
    public String getsalestransactionno = "0",txtshowdcchallan="yes",getgstinforbill="";
    public static final String UPLOAD_URL = RestAPI.urlString+"syncimage.php";
    public static boolean isduplicate = false;
    private PrintData printData;
    Dialog printpopup;
    public static LinearLayout confirmation;
    public static LinearLayout pleasewait;

    BluetoothAdapter mBluetoothAdapter;
    public static PreferenceMangr preferenceMangr=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_view);
        context = this;
        //Declare variables
        txtviewbookingno = (TextView)findViewById(R.id.txtviewbookingno);
        txtviewreviewdate = (TextView)findViewById(R.id.txtviewreviewdate);
        viewpaymenttypeinvoice = (TextView)findViewById(R.id.viewpaymenttypeinvoice);
        txtviewcustomername = (TextView)findViewById(R.id.txtviewcustomername);
        txtviewareacity = (TextView)findViewById(R.id.txtviewareacity);
        viewcartgstnnumber = (TextView)findViewById(R.id.viewcartgstnnumber);
        txtviewsubtotalamt = (TextView)findViewById(R.id.txtviewsubtotalamt);
        txtviewdiscountamt = (TextView)findViewById(R.id.txtviewdiscountamt);
        txtbillno = (TextView)findViewById(R.id.txtbillno);
        txtviewcancel = (TextView)findViewById(R.id.txtviewcancel);
        txtviewcarttotalamt = (TextView)findViewById(R.id.txtviewcarttotalamt);
        txtviewSalesprint = (TextView)findViewById(R.id.txtviewSalesprint);
        viewSalesListview = (ListView) findViewById(R.id.viewSalesListview);
        txtreviewweight = (TextView)findViewById(R.id.txtreviewweight);
        reviewitems = (TextView)findViewById(R.id.reviewitems);
        gstnLL = (LinearLayout)findViewById(R.id.gstnLL);
        viewlistgoback = (ImageButton)findViewById(R.id.viewlistgoback);
        txtviewcanceldummy = (TextView)findViewById(R.id.txtviewcanceldummy);
        cameraview = (ImageView)findViewById(R.id.cameraview);
        hidedummy = (TextView)findViewById(R.id.hidedummy);
        hidedummy1 = (TextView)findViewById(R.id.hidedummy1);
        imgcamera = (ImageView)findViewById(R.id.imgcamera);
        txtviewshortname = (TextView)findViewById(R.id.txtviewshortname);
        txtviewtotamt = (TextView)findViewById(R.id.txtviewtotamt);
        txtviewSalesDCprint = (TextView)findViewById(R.id.txtviewSalesDCprint);

        isduplicate = false;

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

        if(SalesListActivity.getstaticflag.equals("3") || SalesListActivity.getstaticflag.equals("6")){
            txtviewSalesprint.setVisibility(View.GONE);
            txtviewSalesDCprint.setVisibility(View.GONE);
            hidedummy.setVisibility(View.VISIBLE);
            hidedummy1.setVisibility(View.VISIBLE);
        }else{
            txtviewSalesprint.setVisibility(View.VISIBLE);
            txtviewSalesDCprint.setVisibility(View.VISIBLE);
            hidedummy.setVisibility(View.GONE);
            hidedummy1.setVisibility(View.GONE);
        }
        //Goback process
        viewlistgoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack(null);
            }
        });

        DataBaseAdapter objdatabaseadpter =  new DataBaseAdapter(context);
        String getcancelschedulecode="";
        try {
            objdatabaseadpter.open();
            Cursor getsalesmaindetails = objdatabaseadpter.GetSalesListDatasDB(SalesListActivity.getsalesreviewtransactionno,
                    SalesListActivity.getsalesreviewfinanicialyear, SalesListActivity.getsalesreviewcompanycode);
            if(getsalesmaindetails.getCount()>0){
                getcancelschedulecode = getsalesmaindetails.getString(11);
                // for(int i=0;i<getsalesmaindetails.getCount();i++){
                txtviewbookingno.setText("BK.No. : "+getsalesmaindetails.getString(18) );
                txtbillno.setText("Bill No. :" +getsalesmaindetails.getString(3));
                txtviewreviewdate.setText(getsalesmaindetails.getString(7));
                if(getsalesmaindetails.getString(9).equals("1")){
                    viewpaymenttypeinvoice.setText("CASH");
                }else{
                    viewpaymenttypeinvoice.setText("CREDIT");
                }
                txtviewshortname.setText(getsalesmaindetails.getString(24));
                txtviewtotamt.setText("Total ₹ "+dft.format(Double.parseDouble(getsalesmaindetails.getString(26))));
                txtviewcustomername.setText(getsalesmaindetails.getString(21));
                txtviewareacity.setText(getsalesmaindetails.getString(22)+" , "+getsalesmaindetails.getString(23));
                if(getsalesmaindetails.getString(10).equals("")
                        || getsalesmaindetails.getString(10).equals(null) ||
                        getsalesmaindetails.getString(10).equals("null")){
                    viewcartgstnnumber.setText("");
                    viewcartgstnnumber.setVisibility(View.GONE);

                }else {
                    getgstinforbill=getsalesmaindetails.getString(10);
                    viewcartgstnnumber.setVisibility(View.VISIBLE);
                    viewcartgstnnumber.setText("GSTIN :"+getsalesmaindetails.getString(10));
                }
                txtviewcarttotalamt.setText("₹ "+dft.format(Math.round(Double.parseDouble(getsalesmaindetails.getString(14)))));
                txtviewsubtotalamt.setText("₹ "+dft.format(Math.round(Double.parseDouble(getsalesmaindetails.getString(12)))));
                txtviewdiscountamt.setText("₹ "+dft.format(Math.round(Double.parseDouble(getsalesmaindetails.getString(13)))));
                if(getsalesmaindetails.getString(19).equals("3")
                        || getsalesmaindetails.getString(19).equals("6")){
                    txtviewcancel.setVisibility(View.GONE);
                    txtviewcanceldummy.setVisibility(View.VISIBLE);
                }else{
                    txtviewcanceldummy.setVisibility(View.GONE);
                    txtviewcancel.setVisibility(View.VISIBLE);
                }


                if((getsalesmaindetails.getString(27)!=null &&
                        !getsalesmaindetails.getString(27).equals("null"))
                        || (getsalesmaindetails.getString(28)!=null &&
                        !getsalesmaindetails.getString(28).equals("null") )){
                    txtshowdcchallan="no";
//                    txtviewcancel.setVisibility(View.GONE);
//                    txtviewcanceldummy.setVisibility(View.VISIBLE);
                }
//                else{
//                    txtviewcanceldummy.setVisibility(View.GONE);
//                    txtviewcancel.setVisibility(View.VISIBLE);
//                }


                if(!preferenceMangr.pref_getString("getcashclosecount") .equals("0") && !preferenceMangr.pref_getString("getcashclosecount").equals("null") &&
                        !preferenceMangr.pref_getString("getcashclosecount").equals("") && !preferenceMangr.pref_getString("getcashclosecount").equals(null)){
                    txtviewcancel.setVisibility(View.GONE);
                    txtviewcanceldummy.setVisibility(View.VISIBLE);
                }
                if(!preferenceMangr.pref_getString("getsalesclosecount").equals("0") && !preferenceMangr.pref_getString("getsalesclosecount").equals("null") &&
                        !preferenceMangr.pref_getString("getsalesclosecount").equals("") && !preferenceMangr.pref_getString("getsalesclosecount").equals(null)){
                    txtviewcancel.setVisibility(View.GONE);
                    txtviewcanceldummy.setVisibility(View.VISIBLE);
                }
                if(SalesListActivity.getcancelflag.equals("true")){
                    txtviewcancel.setVisibility(View.GONE);
                    txtviewcanceldummy.setVisibility(View.VISIBLE);
                }
                if(getsalesmaindetails.getString(9).equals("2")) {
                    imgcamera.setVisibility(View.VISIBLE);
                    hidedummy.setVisibility(View.GONE);
                    hidedummy1.setVisibility(View.GONE);
                    if (getsalesmaindetails.getString(25) != null) {
                        if (!getsalesmaindetails.getString(25).equals("")
                                && !getsalesmaindetails.getString(25).equals(null)
                                && !getsalesmaindetails.getString(25).equals("null")
                                && !getsalesmaindetails.getString(25).equals("BLOB")
                                && getsalesmaindetails.getString(25) != null
                                && getsalesmaindetails.getString(25) != "") {
                            cameraview.setVisibility(View.VISIBLE);
                            hidedummy.setVisibility(View.GONE);
                            hidedummy1.setVisibility(View.GONE);
                        } else {
                            hidedummy.setVisibility(View.GONE);
                            hidedummy1.setVisibility(View.VISIBLE);
                            cameraview.setVisibility(View.GONE);
                        }
                    }
                }
                //}
                GetViewItem();
            }

        }catch (Exception e) {
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        } finally {
            if (objdatabaseadpter != null)
                objdatabaseadpter.close();
        }



        //Cancel Sales
        final String finalGetcancelschedulecode = getcancelschedulecode;
        txtviewcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataBaseAdapter objdatabaseadapter = null;
                final ProgressDialog[] loading = new ProgressDialog[1];
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to cancel?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                DataBaseAdapter objdatabaseadpter =  new DataBaseAdapter(context);
                                try {
                                    String einvoice_status="0";
                                    String einvoiceurl="";
                                    String einvoicecount="";
                                    String successflag="0";
                                    String toastflag="0";

                                    if(!getgstinforbill.equals("")){
                                        objdatabaseadpter.open();
                                        Cursor Cur = objdatabaseadpter.CheckEinvoice(SalesListActivity.getsalesreviewtransactionno,
                                                SalesListActivity.getsalesreviewcompanycode);
                                        if(Cur.getCount()>0) {
                                            einvoice_status = Cur.getString(1);
                                            einvoiceurl = Cur.getString(3);
                                            einvoicecount=Cur.getString(4);

                                        }
                                        // && !einvoice_status.equals("2")
                                        if(!einvoicecount.equals("0") ){
                                            if(!einvoiceurl.equals("") && !einvoice_status.equals("2")){
                                                networkstate = isNetworkAvailable();
                                                if (networkstate == true) {
                                                    RestAPI api = new RestAPI();
                                                    DataBaseAdapter dbadapter = new DataBaseAdapter(context);
                                                    dbadapter.open();
                                                    try {
                                                        JSONObject jsoncancelObj = api.CancelInvoicePdf(SalesListActivity.getsalesreviewtbilldate, SalesListActivity.getsalesreviewtransactionno,
                                                                SalesListActivity.gesalesreviewbookingno, SalesListActivity.getsalesreviewfinanicialyear,
                                                                SalesListActivity.getsalesreviewcompanycode,
                                                                PreferenceMangr.prefer_getString("getvancode", context), "cancelEinvoice.php");
                                                        if (jsoncancelObj != null) {
                                                                if (jsoncancelObj.getInt("status") == 1) {
                                                                    successflag="1";
                                                                    toastflag="1";
                                                                }
                                                        }else{
                                                            return;
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
                                                }else{
                                                    Toast toast = Toast.makeText(getApplicationContext(),"Please check internet connection", Toast.LENGTH_LONG);
                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                    toast.show();
                                                    return;
                                                }

                                            }else if(einvoiceurl.equals("") && !einvoice_status.equals("2")){
                                                successflag="1";
                                                toastflag="0";
                                            }else{
                                                return;
                                            }


                                        }
                                        if(successflag.equals("1") ){
                                            DataBaseAdapter objdatabaseadapter1 = null;
                                            try {
                                                objdatabaseadapter1 = new DataBaseAdapter(context);
                                                objdatabaseadapter1.open();
                                                                    String getsaleseinvoiceflag = objdatabaseadapter1.UpdateSalesEinvoiceCancel(SalesListActivity.getsalesreviewtransactionno,
                                                                            SalesListActivity.getsalesreviewfinanicialyear, SalesListActivity.getsalesreviewcompanycode);
                                                                    String geteinvoiceresult = objdatabaseadapter1.UpdateEinvoiceNilStock(preferenceMangr.pref_getString("getvancode"),
                                                                            finalGetcancelschedulecode,
                                                                            SalesListActivity.getsalesreviewtransactionno,
                                                                            SalesListActivity.getsalesreviewfinanicialyear, SalesListActivity.getsalesreviewcompanycode);

                                                                    String getsalesitemeinvoiceflag = objdatabaseadapter1.UpdateSalesEinvoiceStockTransaction(SalesListActivity.getsalesreviewtransactionno,
                                                                            SalesListActivity.getsalesreviewfinanicialyear, SalesListActivity.getsalesreviewcompanycode);
                                                                    if (getsalesitemeinvoiceflag.equals("success")) {
                                                                        if (objdatabaseadapter1 != null)
                                                                            objdatabaseadapter1.close();
                                                                        networkstate = isNetworkAvailable();
                                                                        if (networkstate == true) {
                                                                            new AsyncSalesCancelDetails().execute();
                                                                            new AsyncNilStockDetails().execute();
                                                                        }
                                                                        if(toastflag.equals("1")){
                                                                            Toast toast = Toast.makeText(getApplicationContext(), "e-Invoice Cancelled Successfully", Toast.LENGTH_LONG);
                                                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                                                            toast.show();
                                                                        }else{
                                                                            Toast toast = Toast.makeText(getApplicationContext(), "Cancelled Successfully", Toast.LENGTH_LONG);
                                                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                                                            toast.show();
                                                                        }

                                                                        Intent i = new Intent(context, SalesListActivity.class);
                                                                        startActivity(i);
                                                                        return;
                                                                    }

                                            } catch (Exception e) {
                                                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                                mDbErrHelper.open();
                                                String geterrror = e.toString();
                                                mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                                mDbErrHelper.close();
                                            } finally {
                                                // this gets called even if there is an exception somewhere above
                                                if (objdatabaseadapter1 != null)
                                                    objdatabaseadapter1.close();

                                            }
                                        }

                                    }

                                    objdatabaseadpter.open();
                                    String getsalesflag = objdatabaseadpter.UpdateSalesCancelFlag(SalesListActivity.getsalesreviewtransactionno,
                                            SalesListActivity.getsalesreviewfinanicialyear);
                                    String getresult = objdatabaseadpter.UpdateNilStock(preferenceMangr.pref_getString("getvancode"),
                                            finalGetcancelschedulecode,
                                            SalesListActivity.getsalesreviewtransactionno,
                                            SalesListActivity.getsalesreviewfinanicialyear);

                                    String getsalesitemflag = objdatabaseadpter.UpdateSalesStockTransaction(SalesListActivity.getsalesreviewtransactionno,
                                            SalesListActivity.getsalesreviewfinanicialyear);



                                    if(getsalesitemflag.equals("success")){


                                        if (objdatabaseadpter != null)
                                            objdatabaseadpter.close();

                                        networkstate = isNetworkAvailable();
                                        if (networkstate == true) {
                                            new AsyncSalesCancelDetails().execute();
                                            new AsyncNilStockDetails().execute();
                                        }
                                        Toast toast = Toast.makeText(getApplicationContext(), "Cancelled Successfully",Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                        Intent i =new Intent(context,SalesListActivity.class);
                                        startActivity(i);
                                    }

                                }catch (Exception e) {
                                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                    mDbErrHelper.open();
                                    String geterrror = e.toString();
                                    mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                    mDbErrHelper.close();
                                } finally {
                                    if (objdatabaseadpter != null)
                                        objdatabaseadpter.close();
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
        });

        //SAles Print
        txtviewSalesprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                printpopup = new Dialog(context);
                printpopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
                printpopup.setContentView(R.layout.printpopup);

                final TextView txtYesAction = (TextView) printpopup.findViewById(R.id.txtYesAction);
                final TextView txtNoAction = (TextView) printpopup.findViewById(R.id.txtNoAction);

                confirmation = (LinearLayout) printpopup.findViewById(R.id.confirmation);
                pleasewait = (LinearLayout) printpopup.findViewById(R.id.pleasewait);
                final TextView printprogressBar = (TextView) printpopup.findViewById(R.id.printprogressBar);


                txtYesAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*confirmation.setVisibility(View.GONE);
                        pleasewait.setVisibility(View.VISIBLE);*/
                        txtYesAction.setEnabled(false);
                        txtNoAction.setEnabled(false);
                        final String gettransactionnoprint = SalesListActivity.getsalesreviewtransactionno;
                        final String getfinyrcode =  SalesListActivity.getsalesreviewfinanicialyear;
                        final String finalGetSalestransano = gettransactionnoprint;
                        final  String financialyearcode = getfinyrcode;
                        try {
                            isduplicate = true;
                            /*printData = new PrintData(context);
                            deviceFound = printData.findBT();
                            //deviceFound = LoginActivity.p.findBT();

                            if (!deviceFound) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                printpopup.dismiss();
                                toast.show();
                                txtYesAction.setEnabled(true);
                                txtNoAction.setEnabled(true);
                            } else {
                                //new AsyncBlutoothPrint().execute(finalGetSalestransano,financialyearcode);
                                *//*boolean billPrinted = false;
                                billPrinted = (boolean) printData.GetSalesBillPrint(finalGetSalestransano,financialyearcode);
                                if (!billPrinted) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Unable to connect to Bluetooth Printer!", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    printpopup.dismiss();
                                    toast.show();
                                    return;
                                }
                                billPrinted = (boolean)printData.GetDCPrint(finalGetSalestransano, financialyearcode);
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
                                        new AsyncPrintSalesBillDetails().execute(finalGetSalestransano,financialyearcode);
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

                /*final String gettransactionnoprint = SalesListActivity.getsalesreviewtransactionno;
                final String getfinyrcode =  SalesListActivity.getsalesreviewfinanicialyear;
                final String finalGetSalestransano = gettransactionnoprint;
                final  String financialyearcode = getfinyrcode;

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to print?")
                        .setIcon(context.getApplicationInfo().icon)
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog1, int which) {
                                dialog1.dismiss();
                                try {

                                    printData = new PrintData(context);
                                    deviceFound = printData.findBT();
                                    //deviceFound = LoginActivity.p.findBT();
                                    isduplicate = true;
                                    if (!deviceFound) {
                                        Toast toast = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        progressloading.dismiss();
                                        toast.show();
                                    } else {
                                        boolean billPrinted = false;
                                        billPrinted = (boolean) printData.GetSalesBillPrint(finalGetSalestransano,financialyearcode);
                                        billPrinted = (boolean)printData.GetDCPrint(finalGetSalestransano, financialyearcode);
                                        if (!billPrinted) {
                                            Toast toast = Toast.makeText(getApplicationContext(), "Unable to connect to Bluetooth Printer!", Toast.LENGTH_LONG);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
                                            progressloading.dismiss();
                                            return;
                                        }
                                    }
                                }
                                catch (Exception e){
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
                            public void onClick(DialogInterface dialog1, int which) {
                                dialog1.cancel();
                            }
                        }).show();*/

            }
        });

        //SAles Print
        txtviewSalesDCprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                printpopup = new Dialog(context);
                printpopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
                printpopup.setContentView(R.layout.printpopup);

                final TextView txtYesAction = (TextView) printpopup.findViewById(R.id.txtYesAction);
                final TextView txtNoAction = (TextView) printpopup.findViewById(R.id.txtNoAction);

                confirmation = (LinearLayout) printpopup.findViewById(R.id.confirmation);
                pleasewait = (LinearLayout) printpopup.findViewById(R.id.pleasewait);
                final TextView printprogressBar = (TextView) printpopup.findViewById(R.id.printprogressBar);


                txtYesAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*confirmation.setVisibility(View.GONE);
                        pleasewait.setVisibility(View.VISIBLE);*/
                        txtYesAction.setEnabled(false);
                        txtNoAction.setEnabled(false);
                        final String gettransactionnoprint = SalesListActivity.getsalesreviewtransactionno;
                        final String getfinyrcode =  SalesListActivity.getsalesreviewfinanicialyear;
                        final String finalGetSalestransano = gettransactionnoprint;
                        final  String financialyearcode = getfinyrcode;
                        try {
                            //isduplicate = true;


                            if(mBluetoothAdapter != null) {

                                if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                                    if(!preferenceMangr.pref_getString("SelectedPrinterAddress").equals("")) {
                                        new AsyncPrintSalesBillDCDetails().execute(finalGetSalestransano,financialyearcode);
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
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                }
            }
        });
        //Camera View Icon
        cameraview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog imagedialog = new Dialog(context);
                imagedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                imagedialog.setContentView(R.layout.loadimage);
                ImageView showimageview = (ImageView) imagedialog.findViewById(R.id.showimageview);
                ImageView close = (ImageView) imagedialog.findViewById(R.id.close);
                ImageView deleteimg = (ImageView)imagedialog.findViewById(R.id.deleteimg);
                deleteimg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                        String message = "Are you sure you want to delete?";
                        builder.setMessage(message)
                                .setIcon(context.getApplicationInfo().icon)
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                        mDbErrHelper.open();
                                        mDbErrHelper.UpdateSalesBitmapImage(SalesListActivity.getsalesreviewtransactionno,
                                                SalesListActivity.getsalesreviewfinanicialyear);
                                        mDbErrHelper.close();
                                        Toast toast = Toast.makeText(getApplicationContext(),"Deleted Successfully",Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                        dialog.cancel();
                                        imagedialog.dismiss();
                                        cameraview.setVisibility(View.GONE);
                                        cameraview.setVisibility(View.INVISIBLE);
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();
                    }
                });
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imagedialog.cancel();
                    }
                });
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                Cursor getimgCur = mDbErrHelper.GetSalesImageData(SalesListActivity.getsalesreviewtransactionno,
                        SalesListActivity.getsalesreviewfinanicialyear);
                mDbErrHelper.close();
                try{
                    byte [] encodeByte= Base64.decode(getimgCur.getBlob(0),Base64.DEFAULT);
                    Bitmap getimagedata= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                    //Bitmap getimagedata = getimgCur.getBlob(0);
                    showimageview.setImageBitmap(getimagedata);
                }catch(Exception e){
                    e.getMessage();


                }
                imagedialog.show();
            }
        });

    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try{
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                Log.i("keydown","KEYCODE_BACK");
                //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
                if(printpopup!=null){
                    if(printpopup.isShowing()){
//                Toast.makeText(getApplicationContext(), "back press",
//                        Toast.LENGTH_LONG).show();
                        return true;
                    }
                }

            }
        }
        catch (Exception ex){
            ex.printStackTrace();
            Log.i("keydownerror",ex.toString());
        }

        return super.onKeyDown(keyCode, event);
    }

    public void GetViewItem(){
        DataBaseAdapter objdatabaseadpter =  new DataBaseAdapter(context);
        try {
            objdatabaseadpter.open();
            Cursor getsalesitemdetails = objdatabaseadpter.GetSalesListItemDatasDB(SalesListActivity.getsalesreviewtransactionno,
                    SalesListActivity.getsalesreviewfinanicialyear, SalesListActivity.getsalesreviewcompanycode);
            if(getsalesitemdetails.getCount()>0){
                salesitemviewlist.clear();
                for(int i=0;i<getsalesitemdetails.getCount();i++){
                    salesitemviewlist.add(new SalesItemListDetails(getsalesitemdetails.getString(0),
                            getsalesitemdetails.getString(1),
                            getsalesitemdetails.getString(2),getsalesitemdetails.getString(3),
                            getsalesitemdetails.getString(4),getsalesitemdetails.getString(5),
                            getsalesitemdetails.getString(6),
                            getsalesitemdetails.getString(7),getsalesitemdetails.getString(8),
                            getsalesitemdetails.getString(9),
                            getsalesitemdetails.getString(10),getsalesitemdetails.getString(11),
                            getsalesitemdetails.getString(12)));
                    getsalesitemdetails.moveToNext();
                }
                reviewitems.setText(String.valueOf(getsalesitemdetails.getCount()));
                //Adapter
                ViewItemAdapter adapter = new ViewItemAdapter(context, salesitemviewlist);
                viewSalesListview.setAdapter(adapter);
            }else{
                //Adapter
                viewSalesListview.setAdapter(null);
                Toast toast = Toast.makeText(getApplicationContext(), "No Items Available",Toast.LENGTH_LONG);
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
            if (objdatabaseadpter != null)
                objdatabaseadpter.close();
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
    public class ViewItemAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;
        ArrayList<SalesItemListDetails> salesItemList;

        ViewItemAdapter(Context c,ArrayList<SalesItemListDetails> myList) {
            salesItemList = myList;
            context = c;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return salesItemList.size();
        }

        @Override
        public SalesItemListDetails getItem(int position) {
            return (SalesItemListDetails) salesItemList.get(position);
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

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder1 mHolder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.salesviewitemlist, parent, false);
                mHolder = new ViewHolder1();
                try {
                    mHolder.listitemname = (TextView) convertView.findViewById(R.id.listitemname);
                    mHolder.listitemcode = (TextView) convertView.findViewById(R.id.listitemcode);
                    mHolder.listitemqty = (TextView) convertView.findViewById(R.id.listitemqty);
                    mHolder.listitemrate = (TextView) convertView.findViewById(R.id.listitemrate);
                    mHolder.listitemtotal = (TextView) convertView.findViewById(R.id.listitemtotal);
                    mHolder.labelhsntax = (TextView)convertView.findViewById(R.id.labelhsntax);
                    mHolder.itemLL = (LinearLayout)convertView.findViewById(R.id.itemLL);
                    mHolder.stockvalueLL = (LinearLayout)convertView.findViewById(R.id.stockvalueLL);
                    mHolder.labelnilstock = (TextView)convertView.findViewById(R.id.labelnilstock);
                    mHolder.labelstockunit = (TextView)convertView.findViewById(R.id.labelstockunit);
                    mHolder.listdiscount = (TextView)convertView.findViewById(R.id.listdiscount);

                    convertView.setTag(mHolder);
                    convertView.setTag(R.id.listitemname, mHolder.listitemname);
                    convertView.setTag(R.id.listitemcode, mHolder.listitemcode);
                    convertView.setTag(R.id.listitemqty, mHolder.listitemqty);
                    convertView.setTag(R.id.listitemrate, mHolder.listitemrate);
                    convertView.setTag(R.id.listitemtotal, mHolder.listitemtotal);
                    convertView.setTag(R.id.labelhsntax, mHolder.labelhsntax);
                    convertView.setTag(R.id.labelstockunit, mHolder.labelstockunit);
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
            mHolder.listitemname.setTag(position);
            mHolder.listitemcode.setTag(position);
            mHolder.listitemqty.setTag(position);
            mHolder.listitemrate.setTag(position);
            mHolder.listitemtotal.setTag(position);
            mHolder.labelstockunit.setTag(position);
            mHolder.labelhsntax.setTag(position);
            try {
                DecimalFormat df;
                DecimalFormat dft=new DecimalFormat("0.00");
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


                mHolder.listitemname.setText(String.valueOf(salesItemList.get(position).getItemname()));

                mHolder.listitemcode.setText(String.valueOf(salesItemList.get(position).getItemcode()));

                mHolder.listitemrate.setText(dft.format(Double.parseDouble(salesItemList.get(position).getPrice())));


                mHolder.listitemname.setTextColor(Color.parseColor(salesItemList.get(position).getColourcode()));

                mHolder.labelhsntax.setText(salesItemList.get(position).getHsn() +" @ "+salesItemList.get(position).getTax() +"%");
                if(!getnoofdigits.equals("")) {
                    mHolder.listitemqty.setText(df.format(Double.parseDouble(salesItemList.get(position).getQty())));
                }else{
                    mHolder.listitemqty.setText(salesItemList.get(position).getQty());
                }
                mHolder.listitemtotal.setText(dft.format(Double.parseDouble(salesItemList.get(position).getAmount())));
                mHolder.labelstockunit.setText(salesItemList.get(position).getUnitname());

                if(!salesItemList.get(position).getDiscount().equals("")
                        && !salesItemList.get(position).getDiscount().equals(null)
                        && !salesItemList.get(position).getDiscount().equals("0.0")
                        && !salesItemList.get(position).getDiscount().equals("0")){
                    if(Double.parseDouble(salesItemList.get(position).getDiscount()) > 0) {
                        /*mHolder.listdiscount.setBackgroundColor(getResources().getColor(R.color.orangecolor));
                        mHolder.listdiscount.setText("Disc " + dft.format(Double.parseDouble(salesItemList.get(position).getDiscount())));*/
                        mHolder.listdiscount.setBackgroundColor(getResources().getColor(R.color.lightbiscuit));
                        mHolder.listdiscount.setText("");
                    }else{
                        mHolder.listdiscount.setBackgroundColor(getResources().getColor(R.color.lightbiscuit));
                        mHolder.listdiscount.setText("");
                    }
                }else{
                    mHolder.listdiscount.setBackgroundColor(getResources().getColor(R.color.lightbiscuit));
                    mHolder.listdiscount.setText("");
                }

                //Check Free Item
                if(salesItemList.get(position).getFreeitemstatus().equals("freeitem")){
                    mHolder.itemLL.setBackgroundColor(getResources().getColor(R.color.lightblue));
                    mHolder.listdiscount.setBackgroundColor(getResources().getColor(R.color.lightblue));
                    mHolder.listdiscount.setText("");
                }else{
                    mHolder.itemLL.setBackgroundColor(getResources().getColor(R.color.lightbiscuit));
                }

            } catch (Exception e) {
                Log.i("Item value", e.toString());
            }
            calculateWeight();
            return convertView;
        }

        private class ViewHolder1 {
            private TextView listitemname,labelnilstock;
            private TextView listitemcode,listitemqty,listitemrate;
            private TextView listitemtotal,labelhsntax,labelstockunit,listdiscount;
            private LinearLayout itemLL,stockvalueLL;
        }

    }
    public void calculateWeight() {
        Double addweight = 0.0;

        try {
            DecimalFormat df = new DecimalFormat("0.00");
            for (int i = 0; i < salesitemviewlist.size(); i++) {
                Double qty = Double.parseDouble(salesitemviewlist.get(i).getWeight());
                addweight = addweight + qty;
            }
            txtreviewweight.setText(df.format(addweight));
        } catch (Exception ex) {
            Log.i("Calculate  Exception", ex.toString());
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = ex.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }



    /**********Asynchronous Claass***************/
    protected  class AsyncNilStockDetails extends
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
                    Cursor mCur2 = dbadapter.GetNilStockDetailsDB();

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
                        if(result.get(0).ScheduleCode[j] != "" && !result.get(0).ScheduleCode[j].equals("")){
                            String getsplitval[] = result.get(0).ScheduleCode[j].split("~");
                            dataBaseAdapter.UpdateNilStockFlag(getsplitval[0],getsplitval[1]);
                        }
                        dataBaseAdapter.close();
                    }
                }

            }

        }
    }

    protected  class AsyncSalesCancelDetails extends
            AsyncTask<String, JSONObject, ArrayList<SalesSyncDatas>> {
        ArrayList<SalesSyncDatas> List = null;
        JSONObject jsonObj = null;
        @Override
        protected  ArrayList<SalesSyncDatas> doInBackground(String... params) {
            RestAPI api = new RestAPI();
            try {
                JSONObject js_salesobj = new JSONObject();
                JSONObject js_salesitemobj = new JSONObject();
                JSONObject js_stockobj = new JSONObject();
                try {
                    DataBaseAdapter dbadapter = new DataBaseAdapter(context);
                    dbadapter.open();
                    Cursor mCursales = dbadapter.GetSalesCancelDatasDB();
                    Cursor mCursalesitems = dbadapter.GetSalesCancelItemDatasDB();
                    Cursor mCurStock = dbadapter.GetCancelStockTransactionDatasDB();
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

                        js_array3.put(obj);
                        mCursalesitems.moveToNext();
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
                    js_salesobj.put("JSonObject", js_array2);
                    js_salesitemobj.put("JSonObject", js_array3);
                    js_stockobj.put("JSonObject",js_stockarray);

                    jsonObj =  api.SalesCancelDetails(js_salesobj.toString(), js_salesitemobj.toString(), js_stockobj.toString(),context);
                    //Call Json parser functionality
                    JSONParser parser = new JSONParser();
                    //parse the json object to boolean
                    List = parser.parseSalesCancelDataList(jsonObj);
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
            }

        }
    }
    /**********END Asynchronous Claass***************/

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
            String getupdateimgcount = mDbErrHelper.updateSalesImageData(SalesListActivity.getsalesreviewtransactionno,
                    SalesListActivity.getsalesreviewfinanicialyear,uploadImagebitmap);
            mDbErrHelper.close();
            networkstate = isNetworkAvailable();
            if (networkstate == true) {
                new UploadImage().execute();
            }
            if (Integer.parseInt(getupdateimgcount) > 0) {
                Toast toast = Toast.makeText(getApplicationContext(), "Image Captured",Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                cameraview.setVisibility(View.VISIBLE);
                hidedummy.setVisibility(View.GONE);
                hidedummy1.setVisibility(View.GONE);
            }else{
                Toast toast = Toast.makeText(getApplicationContext(), "Error in image Captured",Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                hidedummy.setVisibility(View.GONE);
                hidedummy1.setVisibility(View.VISIBLE);
                cameraview.setVisibility(View.GONE);
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
            DataBaseAdapter objcustomerAdaptor = new DataBaseAdapter(context);
            objcustomerAdaptor.open();
            Cursor mCur = objcustomerAdaptor.GetSalesImageDetails();
            String uploadImage;
            String result="";
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
            }


            return result;
        }
    }

    public  class AsyncBlutoothPrint extends AsyncTask<String, JSONObject, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {

        }

        @Override
        protected String doInBackground(String... params) {
            boolean billPrinted = false;
            String result="1";
            billPrinted = (boolean) printData.GetSalesBillPrint(params[0],params[1],SalesViewActivity.this);
            if (!billPrinted) {
                Toast toast = Toast.makeText(getApplicationContext(), "Unable to connect to Bluetooth Printer!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                printpopup.dismiss();
                toast.show();
                result="0";
                return result;
            }
            billPrinted = (boolean)printData.GetDCPrint(params[0], params[1],SalesViewActivity.this);
            if (!billPrinted) {
                Toast toast = Toast.makeText(getApplicationContext(), "Unable to connect to Bluetooth Printer!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                printpopup.dismiss();
                toast.show();
                result="0";
            }
            printpopup.dismiss();
            return result;
        }
    }

    public void goBack(View v) {
        Log.i("ongoback","ongoback");
        Intent i = new Intent(context, SalesListActivity.class);
        startActivity(i);
    }
    @Override
    public void onBackPressed() {
        Log.i("onBackPressed","onBackPressed");
        goBack(null);
    }

    protected class AsyncPrintSalesBillDetails extends
            AsyncTask<String, JSONObject, Boolean> {
        Boolean billPrinted = false;
        //String Getbilltypecode;
        JSONObject jsonObj = null;
        String finalGetsalestransano= null;
        String financialyearcode= null;
        ProgressDialog loading;
        @Override
        protected Boolean doInBackground(String... params) {
            finalGetsalestransano = params[0];
            financialyearcode = params[1];
            //Getbilltypecode = params[2];
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
                    //GetSalesBillPrint
                    billPrinted = (boolean) printData.GetSalesBillPrint(finalGetsalestransano, financialyearcode,SalesViewActivity.this);
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
                if(txtshowdcchallan.equals("yes")){
                    new AsyncPrintSalesBillDCDetails().execute(finalGetsalestransano,financialyearcode);
                }


                // <----------------sales delivery note print function --------->
                //billPrinted = (boolean) printData.GetDCPrint(finalGetsalestransano, financialyearcode,SalesViewActivity.this);

               /* if (!billPrinted) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Unable to connect Bluetooth Printer. Please check the printer is turn or or not!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    //printpopup.dismiss();
                    toast.show();
                    //Toast.makeText(context, "Unable to connect to Bluetooth Printer!", Toast.LENGTH_SHORT).show();
                    printData = null;

                }*/

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


    protected class AsyncPrintSalesBillDCDetails extends
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
                    billPrinted = deviceFound;
                    //toast.show();
                } else {
                    billPrinted = (boolean) printData.GetDCPrint(finalGetsalestransano, financialyearcode,SalesViewActivity.this);
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
            loading = ProgressDialog.show(context,"Connecting to printer","Please wait",true);
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
                    //Toast.makeText(context, "Unable to connect to Bluetooth Printer!", Toast.LENGTH_SHORT).show();
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
}
