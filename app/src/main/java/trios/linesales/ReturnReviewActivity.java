package trios.linesales;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ReturnReviewActivity extends AppCompatActivity {
    ListView reviewlistview;
    Context context;
    TextView addmore;
    ImageView reviewlistgoback;
    public  TextView paymenttypeinvoice,txtcustomername,txtareacity,
            txtcarttotalamt,txtreviewweight,reviewitems,
            txtdiscountamt,txtsubtotalamt,cartgstnnumber,txtbookingno,txtSalesprint,txtreviewdate;
    public DecimalFormat df;
    EditText txtremarks;
    ArrayList<SalesItemDetails> salesItemList;
    LinearLayout gstnLL;
    String getsalesdate="",getbilltypecode="",getsubtotalamount="",
            getdiscountamt="",getgrandtotal="",getbookingno="",gettransactionno="";
    public  boolean networkstate;
    Dialog dialogstatus;
    boolean deviceFound;
    //private PrintData printData;
    Dialog printpopup;

    BluetoothAdapter mBluetoothAdapter;
    public static PreferenceMangr preferenceMangr=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_review);

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
        txtSalesprint = (TextView)findViewById(R.id.txtSalesprint);
        txtreviewdate = (TextView)findViewById(R.id.txtreviewdate);
        txtremarks = (EditText)findViewById(R.id.txtremarks);

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

        //set Invoice heading
        if(SalesReturnActivity.radio_cash.isChecked()){
            paymenttypeinvoice.setText("Sales Return");
            getbilltypecode = "1";

        }else{
            gstnLL.setVisibility(View.VISIBLE);
            paymenttypeinvoice.setText("Sales Return");
            getbilltypecode = "2";
        }
        networkstate = isNetworkAvailable();
        if (networkstate == true) {
            new AsyncSalesReturnDetails().execute();
        }
        SalesReturnViewActivity.isduplicate = false;
        //GSTN NUMBER
        if(!SalesReturnActivity.gstnnumber.equals("")){
            if(SalesReturnActivity.paymenttype){
                gstnLL.setVisibility(View.GONE);
                cartgstnnumber.setText("");
            }else{
                gstnLL.setVisibility(View.VISIBLE);
                cartgstnnumber.setText(SalesReturnActivity.gstnnumber);
            }
        }else{
            gstnLL.setVisibility(View.GONE);
            cartgstnnumber.setText("");
        }



        //Set customer name
        txtcustomername.setText(SalesReturnActivity.txtcustomername.getText().toString());
        //Set area and city
        txtareacity.setText(SalesReturnActivity.txtareaname.getText().toString());
        //set current date
        txtreviewdate.setText(SalesReturnActivity.txtsalesdate.getText().toString());
        getsalesdate = preferenceMangr.pref_getString("getformatdate");

        //open Review Screen
        addmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        reviewlistgoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /****Booking Number************/
        /***** SCHEDULE DETAILES************/
        DataBaseAdapter objdatabaseadapter1 = null;
        try {
            //Get Booking No.
            objdatabaseadapter1 = new DataBaseAdapter(context);
            objdatabaseadapter1.open();
            getbookingno = objdatabaseadapter1.GetBookingNoSalesReturn();
            gettransactionno = objdatabaseadapter1.GetTransactionNoSalesReturn();
            txtbookingno.setText("BK.No. : "+getbookingno);

        } catch (Exception e) {
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        } finally {
            if(objdatabaseadapter1!=null)
                objdatabaseadapter1.close();
        }
        //Item Adapter
        setItemAdapter();



        //Sales save functionality
        txtSalesprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataBaseAdapter objdatabaseadapter = null;
                try {
                    //Sales Temp table
                    objdatabaseadapter = new DataBaseAdapter(context);
                    objdatabaseadapter.open();
                    if( salesItemList.size() >0) {
                        txtSalesprint.setEnabled(false);
                        String getmaxrefno = objdatabaseadapter.GetMaxRefnoSalesItems();
                        for (int i = 0; i < salesItemList.size(); i++) {
                            objdatabaseadapter.InsertTempSalesItemDetails(salesItemList.get(i).getItemcode(),
                                    salesItemList.get(i).getCompanycode(),salesItemList.get(i).getItemqty(),salesItemList.get(i).getNewprice(),
                                    salesItemList.get(i).getDiscount(),salesItemList.get(i).getSubtotal(),salesItemList.get(i).getFreeflag(),
                                    salesItemList.get(i).getTax(),SalesReturnActivity.gstnnumber,getmaxrefno,
                                    (Double.parseDouble(salesItemList.get(i).getUnitweight())*(Double.parseDouble(salesItemList.get(i).getItemqty()))
                                    ),i+1,salesItemList.get(i).getratediscount(),salesItemList.get(i).getschemeapplicable(),salesItemList.get(i).getOrgprice());
                        }


                        final String getsalestransactionno = objdatabaseadapter.InsertSalesReturn(preferenceMangr.pref_getString("getvancode"),getsalesdate,SalesReturnActivity.customercode,
                                getbilltypecode,SalesReturnActivity.gstnnumber,preferenceMangr.pref_getString("getschedulecode"),getsubtotalamount,
                                getdiscountamt,getgrandtotal,preferenceMangr.pref_getString("getfinanceyrcode"),
                                txtremarks.getText().toString(),getbookingno,gettransactionno,getmaxrefno);

                        if(!getsalestransactionno.equals("") && !getsalestransactionno.equals(null)
                                && !getsalestransactionno.equals("null")){

                                    try {
                                            Toast toast = Toast.makeText(getApplicationContext(),"Saved Successfully", Toast.LENGTH_LONG);
                                            toast.setGravity(Gravity.BOTTOM, 0, 150);
                                            toast.show();
                                        SalesReturnActivity.gstnnumber ="";
                                        SalesReturnActivity.customercode ="";


                                        try {

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
                                                                    new AsyncPrintSalesReturnBillDetails().execute(getsalestransactionno, preferenceMangr.pref_getString("getfinanceyrcode"));
                                                                    printpopup.dismiss();
                                                                }else{
                                                                    Toast toast = Toast.makeText(getApplicationContext(), "Please select the bluetooth printer in app", Toast.LENGTH_LONG);
                                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                                    toast.show();
                                                                    printpopup.dismiss();
                                                                    SalesReturnActivity.staticreviewsalesitems.clear();
                                                                    Intent i = new Intent(ReturnReviewActivity.this,SalesReturnListActivity.class);
                                                                    startActivity(i);
                                                                }
                                                            }else{
                                                                Toast toast = Toast.makeText(getApplicationContext(), "Please turn on the bluetooth", Toast.LENGTH_LONG);
                                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                                toast.show();
                                                                printpopup.dismiss();
                                                                SalesReturnActivity.staticreviewsalesitems.clear();
                                                                Intent i = new Intent(ReturnReviewActivity.this,SalesReturnListActivity.class);
                                                                startActivity(i);
                                                            }
                                                        }else{
                                                            Toast toast = Toast.makeText(getApplicationContext(), "Bluetooth is not available.", Toast.LENGTH_LONG);
                                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                                            toast.show();
                                                            printpopup.dismiss();
                                                            SalesReturnActivity.staticreviewsalesitems.clear();
                                                            Intent i = new Intent(ReturnReviewActivity.this,SalesReturnListActivity.class);
                                                            startActivity(i);
                                                        }
                                                    }
                                                    catch (Exception e){
                                                        Toast toast = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                                        toast.show();
                                                        printpopup.dismiss();
                                                        SalesReturnActivity.staticreviewsalesitems.clear();
                                                        Intent i = new Intent(ReturnReviewActivity.this,SalesReturnListActivity.class);
                                                        startActivity(i);
                                                        /*networkstate = isNetworkAvailable();
                                                        if (networkstate == true) {
                                                            new AsyncSalesReturnDetails().execute();
                                                        }*/
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
                                                    SalesReturnActivity.staticreviewsalesitems.clear();
                                                    Intent i = new Intent(ReturnReviewActivity.this,SalesReturnListActivity.class);
                                                    startActivity(i);
                                                    networkstate = isNetworkAvailable();

                                                }
                                            });
                                            printpopup.setCanceledOnTouchOutside(false);
                                            printpopup.setCancelable(false);
                                            printpopup.show();

                                            if (networkstate == true) {
                                                new AsyncSalesReturnDetails().execute();
                                                new AsyncPriceListTransaction().execute();
                                            }

                                        } catch (Exception e) {
                                            Toast.makeText(context, "Please connect to the Bluetooth Printer!", Toast.LENGTH_SHORT).show();
                                            SalesReturnActivity.staticreviewsalesitems.clear();
                                            Intent i = new Intent(ReturnReviewActivity.this,SalesReturnListActivity.class);
                                            startActivity(i);
                                            networkstate = isNetworkAvailable();
                                            if (networkstate == true) {
                                                new AsyncSalesReturnDetails().execute();
                                                new AsyncPriceListTransaction().execute();
                                            }
                                            DataBaseAdapter mDbErrHelper2 = new DataBaseAdapter(context);
                                            mDbErrHelper2.open();
                                            mDbErrHelper2.insertErrorLog(e.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                            mDbErrHelper2.close();

                                        }



                                    }catch (Exception e) {
                                        DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                        mDbErrHelper.open();
                                        String geterrror = e.toString();
                                        mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                        mDbErrHelper.close();
                                    } finally {
                                        if(objdatabaseadapter!=null)
                                        objdatabaseadapter.close();
                                    }



                        }
                    }else{
                        Toast toast = Toast.makeText(getApplicationContext(),"Cart is empty", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                       // Toast.makeText(getApplicationContext(),"No items available",Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                } finally {
                    if(objdatabaseadapter!=null)
                        objdatabaseadapter.close();
                }
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
    public void setItemAdapter(){
        try {
            if (SalesReturnActivity.staticreviewsalesitems.size() > 0) {
                //Adapter
                ReviewItemAdapter adapter = new ReviewItemAdapter(context, SalesReturnActivity.staticreviewsalesitems);
                reviewlistview.setAdapter(adapter);
            } else {
                reviewlistview.setAdapter(null);
                Toast toast = Toast.makeText(getApplicationContext(), "No items available", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                // Toast.makeText(getApplicationContext(),"No Items Available",Toast.LENGTH_SHORT).show();
                reviewitems.setText(String.valueOf(salesItemList.size()));
                SalesReturnActivity.totalcartitems.setText(String.valueOf(salesItemList.size()));
                return;
            }
        }catch (Exception ex){
            ex.printStackTrace();
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
    public class ReviewItemAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;


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
                convertView = layoutInflater.inflate(R.layout.salesreturnreviewlist, parent, false);
                mHolder = new ViewHolder1();
                try {
                    mHolder.listitemname = (TextView) convertView.findViewById(R.id.listitemname);
                    mHolder.listitemcode = (TextView) convertView.findViewById(R.id.listitemcode);
                    mHolder.listitemqty = (TextView) convertView.findViewById(R.id.listitemqty);
                    mHolder.listitemrate = (TextView) convertView.findViewById(R.id.listitemrate);
                    mHolder.listitemtotal = (TextView) convertView.findViewById(R.id.listitemtotal);
                    mHolder.listitemtax = (TextView) convertView.findViewById(R.id.listitemtax);
                    mHolder.labelhsntax = (TextView)convertView.findViewById(R.id.labelhsntax);
                    mHolder.itemLL = (LinearLayout)convertView.findViewById(R.id.itemLL);
                    mHolder.stockvalueLL = (LinearLayout)convertView.findViewById(R.id.stockvalueLL);

                    mHolder.pricearrow = (ImageView)convertView.findViewById(R.id.pricearrow);

                    mHolder.deleteitem  = (ImageView)convertView.findViewById(R.id.deleteitem);
                    mHolder.dummydeleteitem = (TextView)convertView.findViewById(R.id.dummydeleteitem);

                    convertView.setTag(mHolder);
                    convertView.setTag(R.id.listitemname, mHolder.listitemname);
                    convertView.setTag(R.id.listitemcode, mHolder.listitemcode);
                    convertView.setTag(R.id.listitemqty, mHolder.listitemqty);
                    convertView.setTag(R.id.listitemrate, mHolder.listitemrate);
                    convertView.setTag(R.id.listitemtotal, mHolder.listitemtotal);
                    convertView.setTag(R.id.listitemtax, mHolder.listitemtax);
                    convertView.setTag(R.id.labelhsntax, mHolder.labelhsntax);
                    // convertView.setTag(R.id.labelstockunit, mHolder.labelstockunit);
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
            mHolder.listitemtax.setTag(position);
            mHolder.labelhsntax.setTag(position);
            try {
                DecimalFormat dft = new DecimalFormat("0.00");
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
                    if(!getnoofdigits.equals("")){
                        mHolder.listitemrate.setText(dft.format(Double.parseDouble(salesItemList.get(position).getNewprice())));
                    }else{
                        mHolder.listitemrate.setText(String.valueOf(Double.parseDouble(salesItemList.get(position).getNewprice())));
                    }
                }else{
                    if(!getnoofdigits.equals("")) {
                        mHolder.listitemrate.setText(dft.format(Double.parseDouble("0")));
                    }else{
                        mHolder.listitemrate.setText(String.valueOf(Double.parseDouble("0")));
                    }
                }
                mHolder.listitemname.setTextColor(Color.parseColor(salesItemList.get(position).getColourcode()));

                if(Double.parseDouble(salesItemList.get(position).getOldprice()) >
                        Double.parseDouble(salesItemList.get(position).getNewprice()) ){
                    mHolder.pricearrow.setImageResource(R.drawable.ic_arrow_downward);
                }else{
                    mHolder.pricearrow.setImageResource(R.drawable.ic_arrow_upward_black_24dp);
                }


                mHolder.labelhsntax.setText("@"+salesItemList.get(position).getHsn() +" - "+salesItemList.get(position).getTax() +"%");
                if(getnoofdigits.equals("")){
                    mHolder.listitemqty.setText(salesItemList.get(position).getItemqty());
                }else {
                    mHolder.listitemqty.setText(df.format(Double.parseDouble(salesItemList.get(position).getItemqty())));
                }
                mHolder.listitemtotal.setText(dft.format(Double.parseDouble(salesItemList.get(position).getSubtotal())));


                    mHolder.itemLL.setBackgroundColor(getResources().getColor(R.color.lightgreen));
                    mHolder.deleteitem.setVisibility(View.VISIBLE);
                    mHolder.dummydeleteitem.setVisibility(View.GONE);



            } catch (Exception e) {
                Log.i("Item value", e.toString());
            }



            //Delete click listener
            mHolder.deleteitem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Do you want to delete ?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    for(int p=0;p<SalesReturnActivity.staticreviewsalesitems.size();p++){
                                        if(salesItemList.get(position).getItemcode().equals
                                                (SalesReturnActivity.staticreviewsalesitems.get(p).getPurchaseitemcode())){
                                            SalesReturnActivity.staticreviewsalesitems.remove(position);
                                            SalesReturnActivity.staticreviewsalesitems.remove(p);
                                            setItemAdapter();
                                            CalculateTotal();
                                            return;
                                        }else{
                                            SalesReturnActivity.staticreviewsalesitems.remove(position);
                                            setItemAdapter();
                                            return;
                                        }
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
            CalculateTotal();

            return convertView;
        }

        private class ViewHolder1 {
            private TextView listitemname,dummydeleteitem;
            private TextView listitemcode,labelnilstock,listitemqty,listitemrate;
            private TextView listitemtotal,listitemtax,labelstock,labelhsntax,labelstockunit,listdiscount;
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
        txtsubtotalamt.setText("Sub Total ₹  "+dft.format(Math.round(res1)));
        txtreviewweight.setText(dft.format(res2));
        txtdiscountamt.setText("Discount  ₹  "+dft.format(Math.round(res3)));
        txtcarttotalamt.setText(dft.format(Math.round(res4)));

        reviewitems.setText(String.valueOf(salesItemList.size()));
        SalesReturnActivity.totalcartitems.setText(String.valueOf(salesItemList.size()));
    }

    /**********Asynchronous Claass***************/

    protected  class AsyncSalesReturnDetails extends
            AsyncTask<String, JSONObject, ArrayList<SalesSyncDatas>> {
        ArrayList<SalesSyncDatas> List = null;
        JSONObject jsonObj = null;
        @Override
        protected  ArrayList<SalesSyncDatas> doInBackground(String... params) {
            RestAPI api = new RestAPI();
            String result = "";
            try {
                JSONObject js_salesobj = new JSONObject();
                JSONObject js_salesitemobj = new JSONObject();
                JSONObject js_stockobj = new JSONObject();
                try {
                    DataBaseAdapter dbadapter = new DataBaseAdapter(context);
                    dbadapter.open();
                    Cursor mCursales = dbadapter.GetSalesReturnDatasDB();
                    Cursor mCursalesitems = dbadapter.GetSalesReturnItemDatasDB();
                    Cursor mCurStock = dbadapter.GetStockTransactionDatasDB();
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
                        obj.put("salestime", mCursales.getString(28));
                        obj.put("beforeroundoff", mCursales.getString(29));
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
                        obj.put("financialyearcode", mCurStock.getString(11));
                        obj.put("autonum", mCurStock.getString(12));
                        js_stockarray.put(obj);
                        mCurStock.moveToNext();
                    }
                    js_salesobj.put("JSonObject", js_array2);
                    js_salesitemobj.put("JSonObject", js_array3);
                    js_stockobj.put("JSonObject",js_stockarray);

                    jsonObj =  api.SalesReturnDetails(js_salesobj.toString(),js_salesitemobj.toString(),js_stockobj.toString(),context);
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
                            objdatabaseadapter.UpdateSalesReturnFlag(List.get(0).TransactionNo[j]);
                        }
                    }
                    if (List.get(0).SalesItemTransactionNo.length > 0) {
                        for (int j = 0; j < List.get(0).SalesItemTransactionNo.length; j++) {
                            objdatabaseadapter.UpdateSalesReturnItemFlag(List.get(0).SalesItemTransactionNo[j]);
                        }
                    }
                    if (List.get(0).StockTransactionNo.length > 0) {
                        for (int j = 0; j < List.get(0).StockTransactionNo.length; j++) {
                            objdatabaseadapter.UpdateStockTransactionFlag(List.get(0).StockTransactionNo[j]);
                        }
                    }
                }
            }catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncSalesreturnDetails", e.getMessage());
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
        /*Intent i = new Intent(context, SalesReturnActivity.class);
        startActivity(i);*/
        finish();
    }
    @Override
    public void onBackPressed() {
        goBack(null);
    }

    protected class AsyncPrintSalesReturnBillDetails extends
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
                EpsonT20Printer epsonT20Printer = new EpsonT20Printer(ReturnReviewActivity.this, ReturnReviewActivity.this, null);
                deviceFound = epsonT20Printer.findBT();
                Log.d("deviceFound",String.valueOf(deviceFound));
                if (!deviceFound) {
                    /*Toast toast = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);*/
                    //printpopup.dismiss();
                    billPrinted = deviceFound;
                    //toast.show();
                } else {
                    //GetSalesBillPrint
                    billPrinted = (boolean) epsonT20Printer.GetSalesReturnBillPrint(finalGetsalestransano, financialyearcode,ReturnReviewActivity.this, true);
                    //printpopup.dismiss();
                }
                Log.d("billPrinted",String.valueOf(billPrinted));
            }
            catch (Exception e){
                /*Toast toast = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();*/
                //printpopup.dismiss();
                //printData = null;
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

                //new AsyncPrintSalesReturnBillDCDetails().execute(finalGetsalestransano,financialyearcode);

                if (!billPrinted) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Unable to connect to Bluetooth Printer. Please check the printer is turn or or not!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    //printpopup.dismiss();
                    toast.show();
                    //Toast.makeText(context, "Unable to connect to Bluetooth Printer!", Toast.LENGTH_SHORT).show();
                    SalesReturnActivity.staticreviewsalesitems.clear();
                    Intent i = new Intent(ReturnReviewActivity.this,SalesReturnListActivity.class);
                    startActivity(i);
                    /*networkstate = isNetworkAvailable();
                    if (networkstate == true) {
                        new AsyncSalesReturnDetails().execute();
                    }*/
                    //printData = null;

                    // return;
                }else {
                    SalesReturnActivity.staticreviewsalesitems.clear();
                    Intent i = new Intent(ReturnReviewActivity.this, SalesReturnListActivity.class);
                    startActivity(i);
                    /*networkstate = isNetworkAvailable();
                    if (networkstate == true) {
                        new AsyncSalesReturnDetails().execute();
                    }*/
                }
                //new AsyncPrintSalesReturnBillDCDetails().execute(finalGetsalestransano,financialyearcode);

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


    /*protected class AsyncPrintSalesReturnBillDCDetails extends
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
                    *//*Toast toast = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);*//*
                    //printpopup.dismiss();
                    billPrinted = deviceFound;
                    //toast.show();
                } else {
                    billPrinted = (boolean) printData.GetDCSalesReturnPrint(finalGetsalestransano, financialyearcode,ReturnReviewActivity.this);
                    //printpopup.dismiss();
                }
                Log.d("billPrinted",String.valueOf(billPrinted));
            }
            catch (Exception e){
                *//*Toast toast = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();*//*
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
                    SalesReturnActivity.staticreviewsalesitems.clear();
                    Intent i = new Intent(ReturnReviewActivity.this,SalesReturnListActivity.class);
                    startActivity(i);
                    networkstate = isNetworkAvailable();
                    if (networkstate == true) {
                        new AsyncSalesReturnDetails().execute();
                        new AsyncPriceListTransaction().execute();
                    }
                    //Toast.makeText(context, "Unable to connect to Bluetooth Printer!", Toast.LENGTH_SHORT).show();
                    printData = null;

                }else {
                    //printpopup.dismiss();
                    SalesReturnActivity.staticreviewsalesitems.clear();
                    Intent i = new Intent(ReturnReviewActivity.this,SalesReturnListActivity.class);
                    startActivity(i);
                    networkstate = isNetworkAvailable();
                    if (networkstate == true) {
                        new AsyncSalesReturnDetails().execute();
                        new AsyncPriceListTransaction().execute();
                    }
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
    }*/

    protected  class AsyncPriceListTransaction extends
            AsyncTask<String, JSONObject, String> {
        String List = "Success";
        JSONObject jsonObj = null;
        ProgressDialog loading;

        @Override
        protected  String doInBackground(String... params) {

            RestAPI api = new RestAPI();
            String result = "";
            DataBaseAdapter dataBaseAdapter =null;
            String deviceid = preferenceMangr.pref_getString("deviceid");
            try {


                networkstate = isNetworkAvailable();
                if (networkstate == true) {

                    dataBaseAdapter = new DataBaseAdapter(context);
                    dataBaseAdapter.open();

                    //itemn price list transaction
                    jsonObj = api.GetAllDetails(preferenceMangr.pref_getString("deviceid"),"syncitempricelisttransaction.php",context);
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.syncitempricelisttransaction(jsonObj);

                        /*Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat mformat= new SimpleDateFormat("dd-MM-yyyy h:mm a");
                        MenuActivity.pricelistlastsyncdate = mformat.format(calendar.getTime());*/
                        //Toast.makeText(context,pricelistlastsyncdate,Toast.LENGTH_SHORT).show();

                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "itempricelisttransaction", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }


                }


            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncPriceListTransaction", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() +" Sync - AsyncPriceListTransaction", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            finally {
                dataBaseAdapter.close();
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
}
