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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class SalesOrderViewActivity extends AppCompatActivity {
    TextView txtviewbookingno,txtviewreviewdate,viewpaymenttypeinvoice,txtviewcustomername,txtviewareacity,
            viewcartgstnnumber,txtviewsubtotalamt,txtviewdiscountamt,txtviewcancel,txtviewcarttotalamt,
            txtviewSalesprint,reviewitems,txtviewcanceldummy,txtbillno,txtviewSalesprintdummy
            ,txtviewshortname,txtviewtotamt;
    ListView viewSalesListview;
    Context context;
    public static boolean deviceFound;
    ImageButton viewlistgoback;
    ArrayList<SalesItemListDetails> salesitemviewlist =new ArrayList<SalesItemListDetails>();
    LinearLayout gstnLL;
    DecimalFormat dft=new DecimalFormat("0.00");
    boolean networkstate;
    private static final int CAMERA_PIC_REQUEST = 1111;
    Bitmap bitmap1=null;
    static String varimageview = "";
    public String getsalestransactionno = "0";
    public static final String UPLOAD_URL = RestAPI.urlString+"syncimage.php";
    public static boolean isduplicate = false;
    //private PrintData printData;
    Dialog printpopup;
    public static LinearLayout confirmation;
    public static LinearLayout pleasewait;
    TextView txttransportname;

    BluetoothAdapter mBluetoothAdapter;
    public static PreferenceMangr preferenceMangr=null;
    ReceiveListener receiveListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_order_view);

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
        txtviewSalesprintdummy = (TextView)findViewById(R.id.txtviewSalesprintdummy);
        viewSalesListview = (ListView) findViewById(R.id.viewSalesListview);
        reviewitems = (TextView)findViewById(R.id.reviewitems);
        gstnLL = (LinearLayout)findViewById(R.id.gstnLL);
        viewlistgoback = (ImageButton)findViewById(R.id.viewlistgoback);
        txtviewcanceldummy = (TextView)findViewById(R.id.txtviewcanceldummy);
        txtviewshortname = (TextView)findViewById(R.id.txtviewshortname);
        txtviewtotamt = (TextView)findViewById(R.id.txtviewtotamt);
        txttransportname = (TextView)findViewById(R.id.txttransportname);
        isduplicate = false;

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



        if(SalesOrderListActivity.getstaticflag.equals("3") || SalesOrderListActivity.getstaticflag.equals("6")){
            txtviewSalesprint.setVisibility(View.GONE);
            txtviewSalesprintdummy.setVisibility(View.VISIBLE);
        }else{
            txtviewSalesprint.setVisibility(View.VISIBLE);
            txtviewSalesprintdummy.setVisibility(View.GONE);
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
            Cursor getsalesmaindetails = objdatabaseadpter.GetSalesOrderListDatasDB(SalesOrderListActivity.getsalesreviewtransactionno,
                    SalesOrderListActivity.getsalesreviewfinanicialyear, SalesOrderListActivity.getsalesreviewcompanycode);
            if(getsalesmaindetails.getCount()>0){
                getcancelschedulecode = getsalesmaindetails.getString(11);
                // for(int i=0;i<getsalesmaindetails.getCount();i++){
                txtviewbookingno.setText("Order.No. : "+getsalesmaindetails.getString(18) );
                txtbillno.setText("Order No. :" +getsalesmaindetails.getString(3));
                txtviewreviewdate.setText(getsalesmaindetails.getString(7));
                if(getsalesmaindetails.getString(9).equals("1")){
                    viewpaymenttypeinvoice.setText("CASH");
                }else{
                    viewpaymenttypeinvoice.setText("CREDIT");
                }
                txtviewshortname.setText(getsalesmaindetails.getString(24));
                txtviewtotamt.setText("Total ₹ "+dft.format(Double.parseDouble(getsalesmaindetails.getString(26))));
                txtviewcustomername.setText(getsalesmaindetails.getString(21));
                txttransportname.setText(getsalesmaindetails.getString(27));
                txtviewareacity.setText(getsalesmaindetails.getString(22)+" , "+getsalesmaindetails.getString(23));
                if(getsalesmaindetails.getString(10).equals("")
                        || getsalesmaindetails.getString(10).equals(null) ||
                        getsalesmaindetails.getString(10).equals("null")){
                    viewcartgstnnumber.setText("");
                    viewcartgstnnumber.setVisibility(View.GONE);
                }else {
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
                    /*txtviewcanceldummy.setVisibility(View.GONE);
                    txtviewcancel.setVisibility(View.VISIBLE);*/

                    txtviewcancel.setVisibility(View.GONE);
                    txtviewcanceldummy.setVisibility(View.VISIBLE);
                }



                if(!preferenceMangr.pref_getString("getcashclosecount").equals("0") && !preferenceMangr.pref_getString("getcashclosecount").equals("null") &&
                        !preferenceMangr.pref_getString("getcashclosecount").equals("") && !preferenceMangr.pref_getString("getcashclosecount").equals(null)){
                    txtviewcancel.setVisibility(View.GONE);
                    txtviewcanceldummy.setVisibility(View.VISIBLE);
                }
                if(!preferenceMangr.pref_getString("getsalesclosecount").equals("0") && !preferenceMangr.pref_getString("getsalesclosecount").equals("null") &&
                        !preferenceMangr.pref_getString("getsalesclosecount").equals("") && !preferenceMangr.pref_getString("getsalesclosecount").equals(null)){
                    txtviewcancel.setVisibility(View.GONE);
                    txtviewcanceldummy.setVisibility(View.VISIBLE);
                }
                if(SalesOrderListActivity.getcancelflag.equals("true")){
                    txtviewcancel.setVisibility(View.GONE);
                    txtviewcanceldummy.setVisibility(View.VISIBLE);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to cancel?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                DataBaseAdapter objdatabaseadpter =  new DataBaseAdapter(context);
                                try {
                                    objdatabaseadpter.open();
                                    String getsalesflag = objdatabaseadpter.UpdateSalesOrderCancelFlag(SalesOrderListActivity.getsalesreviewtransactionno,
                                            SalesOrderListActivity.getsalesreviewfinanicialyear);

                                    if(getsalesflag.equals("success")){

                                        if (objdatabaseadpter != null)
                                            objdatabaseadpter.close();

                                        networkstate = isNetworkAvailable();
                                        if (networkstate == true) {
                                            new AsyncSalesOrderCancelDetails().execute();
                                        }
                                        Toast toast = Toast.makeText(getApplicationContext(), "Cancelled Successfully",Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                        Intent i =new Intent(context,SalesOrderListActivity.class);
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
                        final String gettransactionnoprint = SalesOrderListActivity.getsalesreviewtransactionno;
                        final String getfinyrcode =  SalesOrderListActivity.getsalesreviewfinanicialyear;
                        final String finalGetSalestransano = gettransactionnoprint;
                        final  String financialyearcode = getfinyrcode;
                        try {

                            /*printData = new PrintData(context);
                            deviceFound = printData.findBT();
                            //deviceFound = LoginActivity.p.findBT();
                            isduplicate = true;
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
                                billPrinted = (boolean) printData.GetSalesOrderBillPrint(finalGetSalestransano,financialyearcode);
                                if (!billPrinted) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Unable to connect to Bluetooth Printer!", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    printpopup.dismiss();
                                    toast.show();
                                    return;
                                }

                                printpopup.dismiss();*//*

                            }*/

                            if(mBluetoothAdapter != null) {

                                if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                                    if(!preferenceMangr.pref_getString("SelectedPrinterAddress").equals("")) {
                                        disablePrintButtons();
                                        new AsyncPrintSalesOrderCartViewDetails().execute(finalGetSalestransano,financialyearcode);
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

        if(preferenceMangr.pref_getString("getbusiness_type").equals("2") && preferenceMangr.pref_getString("getorderprint").equals("yes")) {
            if(SalesOrderListActivity.getstaticflag.equals("3") || SalesOrderListActivity.getstaticflag.equals("6")) {
                txtviewSalesprint.setVisibility(View.GONE);
                txtviewSalesprintdummy.setVisibility(View.VISIBLE);
            }else{
                txtviewSalesprint.setVisibility(View.VISIBLE);
                txtviewSalesprintdummy.setVisibility(View.GONE);
            }
        }else{
            txtviewSalesprint.setVisibility(View.VISIBLE);
            txtviewSalesprintdummy.setVisibility(View.GONE);
        }
        if(SalesOrderListActivity.getbillstatus.equals("2")){
            txtviewSalesprint.setVisibility(View.GONE);
            txtviewSalesprintdummy.setVisibility(View.VISIBLE);
        }



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
            Cursor getsalesitemdetails = objdatabaseadpter.GetSalesOrderListItemDatasDB(SalesOrderListActivity.getsalesreviewtransactionno,
                    SalesOrderListActivity.getsalesreviewfinanicialyear, SalesOrderListActivity.getsalesreviewcompanycode);
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
                convertView = layoutInflater.inflate(R.layout.salesorderviewitemlist, parent, false);
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
                       // mHolder.listdiscount.setBackgroundColor(getResources().getColor(R.color.lightpink));
                        mHolder.listdiscount.setText("");
                    }else{
                       // mHolder.listdiscount.setBackgroundColor(getResources().getColor(R.color.lightpink));
                        mHolder.listdiscount.setText("");
                    }
                }else{
                   // mHolder.listdiscount.setBackgroundColor(getResources().getColor(R.color.lightpink));
                    mHolder.listdiscount.setText("");
                }

                //Check Free Item
                if(salesItemList.get(position).getFreeitemstatus().equals("freeitem")){
                    mHolder.itemLL.setBackgroundColor(getResources().getColor(R.color.lightblue));
                    mHolder.listdiscount.setBackgroundColor(getResources().getColor(R.color.lightblue));
                    mHolder.listdiscount.setText("");
                }else{
                    mHolder.itemLL.setBackgroundColor(getResources().getColor(R.color.lightvoilet));
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


    protected  class AsyncSalesOrderCancelDetails extends
            AsyncTask<String, JSONObject, ArrayList<SalesSyncDatas>> {
        ArrayList<SalesSyncDatas> List = null;
        JSONObject jsonObj = null;
        @Override
        protected  ArrayList<SalesSyncDatas> doInBackground(String... params) {
            RestAPI api = new RestAPI();
            try {
                JSONObject js_salesobj = new JSONObject();
                JSONObject js_salesitemobj = new JSONObject();
                try {
                    DataBaseAdapter dbadapter = new DataBaseAdapter(context);
                    dbadapter.open();
                    Cursor mCursales = dbadapter.GetSalesOrderCancelDatasDB();
                    Cursor mCursalesitems = dbadapter.GetSalesOrderCancelItemDatasDB();
                    JSONArray js_array2 = new JSONArray();
                    JSONArray js_array3 = new JSONArray();
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
                        obj.put("salestime", mCursales.getString(29));
                        obj.put("beforeroundoff", mCursales.getString(30));
                        obj.put("transportid", mCursales.getString(31));
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
                    js_salesobj.put("JSonObject", js_array2);
                    js_salesitemobj.put("JSonObject", js_array3);

                    jsonObj =  api.SalesOrderCancelDetails(js_salesobj.toString(), js_salesitemobj.toString(),context);
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
                            objdatabaseadapter.UpdateOrderCancelSalesFlag(List.get(0).TransactionNo[j]);
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


    public void goBack(View v) {
        Log.i("ongoback","ongoback");
        Intent i = new Intent(context, SalesOrderListActivity.class);
        startActivity(i);
    }
    @Override
    public void onBackPressed() {
        Log.i("onBackPressed","onBackPressed");
        goBack(null);
    }

    protected class AsyncPrintSalesOrderCartViewDetails extends
            AsyncTask<String, JSONObject, Boolean> {
        Boolean billPrinted = false;
        JSONObject jsonObj = null;
        ProgressDialog loading;
        @Override
        protected Boolean doInBackground(String... params) {
            final String finalGetreceipttransano = params[0];
            final String financialyearcode = params[1];
            try {
                EpsonT20Printer epsonT20Printer = new EpsonT20Printer(SalesOrderViewActivity.this, SalesOrderViewActivity.this, receiveListener);
                deviceFound = epsonT20Printer.findBT();
                Log.d("deviceFound",String.valueOf(deviceFound));
                if (!deviceFound) {
                    /*Toast toast = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);*/
                    //printpopup.dismiss();
                    billPrinted = deviceFound;
                    //toast.show();
                } else {
                    billPrinted = (boolean) epsonT20Printer.GetSalesOrderBillPrint(finalGetreceipttransano, financialyearcode,SalesOrderViewActivity.this);
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
        txtviewSalesprint.setEnabled(true);
    }

    public void disablePrintButtons() {
        txtviewSalesprint.setEnabled(false);
    }

}
