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

public class SalesReturnViewActivity extends AppCompatActivity {

    TextView txtviewbookingno,txtviewreviewdate,viewpaymenttypeinvoice,txtviewcustomername,txtviewareacity,
            viewcartgstnnumber,txtviewsubtotalamt,txtviewdiscountamt,txtviewcancel,txtviewcarttotalamt,
            txtviewSalesprint,reviewitems,txtreviewweight,txtviewcanceldummy,txtbillno,txtviewSalesprintdummy,
            txtviewshortname,txtviewtotamt;
    ListView viewSalesListview;
    Context context;
    ImageButton viewlistgoback;
    ArrayList<SalesItemListDetails> salesitemviewlist =new ArrayList<SalesItemListDetails>();
    LinearLayout gstnLL;
    DecimalFormat dft=new DecimalFormat("0.00");
    boolean networkstate;
    public  static boolean isduplicate;
    boolean deviceFound;
    //private PrintData printData;
    Dialog printpopup;

    BluetoothAdapter mBluetoothAdapter;
    public static PreferenceMangr preferenceMangr=null;
    ReceiveListener receiveListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_return_view);
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
        txtviewcancel = (TextView)findViewById(R.id.txtviewcancel);
        txtviewcarttotalamt = (TextView)findViewById(R.id.txtviewcarttotalamt);
        txtviewSalesprint = (TextView)findViewById(R.id.txtviewSalesprint);
        txtviewSalesprintdummy = (TextView)findViewById(R.id.txtviewSalesprintdummy);
        viewSalesListview = (ListView) findViewById(R.id.viewSalesListview);
        txtreviewweight = (TextView)findViewById(R.id.txtreviewweight);
        reviewitems = (TextView)findViewById(R.id.reviewitems);
        gstnLL = (LinearLayout)findViewById(R.id.gstnLL);
        viewlistgoback = (ImageButton)findViewById(R.id.viewlistgoback);
        txtviewcanceldummy = (TextView)findViewById(R.id.txtviewcanceldummy);
        txtbillno = (TextView)findViewById(R.id.txtbillno);
        txtviewshortname = (TextView)findViewById(R.id.txtviewshortname);
        txtviewtotamt = (TextView)findViewById(R.id.txtviewtotamt);

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
        //Goback process
        viewlistgoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack(null);
            }
        });



        DataBaseAdapter objdatabaseadpter =  new DataBaseAdapter(context);
        try {
            objdatabaseadpter.open();
            Cursor getsalesmaindetails = objdatabaseadpter.GetSalesReturnListDatasDB(SalesReturnListActivity.getsalesreviewtransactionno,
                    SalesReturnListActivity.getsalesreviewfinanicialyear, SalesReturnListActivity.getsalesreviewcompanycode);
            if(getsalesmaindetails.getCount()>0){
                for(int i=0;i<getsalesmaindetails.getCount();i++){
                    txtviewbookingno.setText("BK.No. : "+getsalesmaindetails.getString(18) );
                    txtbillno.setText("Bill No. :" +getsalesmaindetails.getString(3));
                    txtviewreviewdate.setText(getsalesmaindetails.getString(7));
                    if(getsalesmaindetails.getString(9).equals("1")){
                        viewpaymenttypeinvoice.setText("Sales Return");
                    }else{
                        viewpaymenttypeinvoice.setText("Sales Return");
                    }
                    txtviewcustomername.setText(getsalesmaindetails.getString(21));
                    txtviewshortname.setText(getsalesmaindetails.getString(24));
                    txtviewtotamt.setText("Total ₹ "+dft.format(Double.parseDouble(getsalesmaindetails.getString(25))));
                    txtviewareacity.setText(getsalesmaindetails.getString(22)+" , "+getsalesmaindetails.getString(23));
                    if(getsalesmaindetails.getString(10).equals("")
                            || getsalesmaindetails.getString(10).equals(null) ||
                            getsalesmaindetails.getString(10).equals("null")){
                        viewcartgstnnumber.setText("");
                        viewcartgstnnumber.setVisibility(View.GONE);
                    }else {
                        viewcartgstnnumber.setVisibility(View.VISIBLE);
                        viewcartgstnnumber.setText("GSTIN : "+getsalesmaindetails.getString(10));
                    }
                    txtviewcarttotalamt.setText("₹ "+dft.format(Math.round((Double.parseDouble(getsalesmaindetails.getString(14))))));
                    txtviewsubtotalamt.setText("₹ "+dft.format(Math.round((Double.parseDouble(getsalesmaindetails.getString(12))))));
                    txtviewdiscountamt.setText("₹ "+dft.format(Math.round((Double.parseDouble(getsalesmaindetails.getString(13))))));
                    if(getsalesmaindetails.getString(19).equals("3")
                            || getsalesmaindetails.getString(19).equals("6")){
                        txtviewcancel.setVisibility(View.GONE);
                        txtviewcanceldummy.setVisibility(View.VISIBLE);
                        txtviewSalesprint.setVisibility(View.GONE);
                        txtviewSalesprintdummy.setVisibility(View.VISIBLE);
                    }else{
                        txtviewcanceldummy.setVisibility(View.GONE);
                        txtviewcancel.setVisibility(View.VISIBLE);
                        txtviewSalesprint.setVisibility(View.VISIBLE);
                        txtviewSalesprintdummy.setVisibility(View.GONE);
                    }
                    if(!preferenceMangr.pref_getString("getcashclosecount").equals("0") && !preferenceMangr.pref_getString("getcashclosecount").equals("null") &&
                            !preferenceMangr.pref_getString("getcashclosecount").equals("") && !preferenceMangr.pref_getString("getcashclosecount").equals(null)){
                        txtviewcancel.setVisibility(View.GONE);
                        txtviewcanceldummy.setVisibility(View.VISIBLE);
                        //txtviewSalesprint.setVisibility(View.GONE);
                       // txtviewSalesprintdummy.setVisibility(View.VISIBLE);
                    }
                    if(!preferenceMangr.pref_getString("getsalesclosecount").equals("0") && !preferenceMangr.pref_getString("getsalesclosecount").equals("null") &&
                            !preferenceMangr.pref_getString("getsalesclosecount").equals("") && !preferenceMangr.pref_getString("getsalesclosecount").equals(null)){
                        txtviewcancel.setVisibility(View.GONE);
                        txtviewcanceldummy.setVisibility(View.VISIBLE);
                        //txtviewSalesprint.setVisibility(View.GONE);
                       // txtviewSalesprintdummy.setVisibility(View.VISIBLE);
                    }
                    if(SalesReturnListActivity.getcancelflag.equals("true")){
                        txtviewcancel.setVisibility(View.GONE);
                        txtviewcanceldummy.setVisibility(View.VISIBLE);
                    }
                }
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



        txtviewSalesprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    /*android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(context);
                    String message = "Are you sure you want to print?";
                    builder1.setMessage(message)
                            .setIcon(context.getApplicationInfo().icon)
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog1, int which) {
                                    dialog1.dismiss();
                                    try {
                                        printData = new PrintData(context);
                                        deviceFound = printData.findBT();
                                        if (!deviceFound) {
                                            Toast toast1 = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                            toast1.setGravity(Gravity.CENTER, 0, 0);
                                            toast1.show();

                                        } else {
                                            boolean billPrinted = false;
                                            SalesReturnViewActivity.isduplicate=true;
                                            billPrinted = (boolean) printData.GetSalesReturnBillPrint(
                                                    SalesReturnListActivity.getsalesreviewtransactionno, SalesReturnListActivity.getsalesreviewfinanicialyear);
                                            billPrinted = (boolean) printData.GetDCSalesReturnPrint(SalesReturnListActivity.getsalesreviewtransactionno,
                                                    SalesReturnListActivity.getsalesreviewfinanicialyear);

                                            if (!billPrinted) {
                                                Toast toast1 = Toast.makeText(getApplicationContext(), "Unable to connect to Bluetooth Printer!", Toast.LENGTH_LONG);
                                                toast1.setGravity(Gravity.CENTER, 0, 0);
                                                toast1.show();
                                                return;
                                            }


                                        }
                                    } catch (Exception e) {
                                        Toast.makeText(context, "Please connect to the Bluetooth Printer!", Toast.LENGTH_SHORT).show();
                                        SalesReturnActivity.staticreviewsalesitems.clear();

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

                    printpopup = new Dialog(context);
                    printpopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    printpopup.setContentView(R.layout.printpopup);

                    TextView txtYesAction = (TextView) printpopup.findViewById(R.id.txtYesAction);
                    TextView txtNoAction = (TextView) printpopup.findViewById(R.id.txtNoAction);
                    txtYesAction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            try {
                                SalesReturnViewActivity.isduplicate=true;
                                /*printData = new PrintData(context);
                                deviceFound = printData.findBT();

                                if (!deviceFound) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    printpopup.dismiss();
                                    toast.show();
                                } else {

                                    boolean billPrinted = false;



                                    *//*billPrinted = (boolean) printData.GetSalesReturnBillPrint(
                                            SalesReturnListActivity.getsalesreviewtransactionno, SalesReturnListActivity.getsalesreviewfinanicialyear);
                                    if (!billPrinted) {
                                        Toast toast = Toast.makeText(getApplicationContext(), "Unable to connect to Bluetooth Printer!", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        printpopup.dismiss();
                                        toast.show();
                                        return;
                                    }
                                    billPrinted = (boolean) printData.GetDCSalesReturnPrint(SalesReturnListActivity.getsalesreviewtransactionno,
                                            SalesReturnListActivity.getsalesreviewfinanicialyear);*//*
                                }*/

                                if(mBluetoothAdapter != null) {

                                    if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                                        if(!preferenceMangr.pref_getString("SelectedPrinterAddress").equals("")) {
                                            disablePrintButtons();
                                            new AsyncPrintSalesReturnBillDetails().execute(SalesReturnListActivity.getsalesreviewtransactionno, SalesReturnListActivity.getsalesreviewfinanicialyear);
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
                } catch (Exception e) {
                    Toast.makeText(context, "Please connect to the Bluetooth Printer!", Toast.LENGTH_SHORT).show();
                    SalesReturnActivity.staticreviewsalesitems.clear();

                    DataBaseAdapter mDbErrHelper2 = new DataBaseAdapter(context);
                    mDbErrHelper2.open();
                    mDbErrHelper2.insertErrorLog(e.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper2.close();

                }
            }
        });
        //Cancel Sales
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
                                    String getstockcheckingcount = objdatabaseadpter.StockChecking(SalesReturnListActivity.getsalesreviewtransactionno,
                                            SalesReturnListActivity.getsalesreviewfinanicialyear);

                                    if(getstockcheckingcount.equals("0")){
                                        String getsalesflag = objdatabaseadpter.UpdateSalesReturnCancelFlag(SalesReturnListActivity.getsalesreviewtransactionno,
                                                SalesReturnListActivity.getsalesreviewfinanicialyear);

                                        String getsalesitemflag = objdatabaseadpter.UpdateSalesReturnStockTransaction(SalesReturnListActivity.getsalesreviewtransactionno,
                                                SalesReturnListActivity.getsalesreviewfinanicialyear);

                                        if(getsalesitemflag.equals("success")){
                                            Toast toast = Toast.makeText(getApplicationContext(),"Cancelled Successfully",Toast.LENGTH_LONG);
                                            //toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
                                            Intent i =new Intent(context,SalesReturnListActivity.class);
                                            startActivity(i);
                                        }
                                        networkstate = isNetworkAvailable();
                                        if (networkstate == true) {
                                            new AsyncSalesReturnCancelDetails().execute();
                                        }
                                    }
                                    else{
                                        Toast toast = Toast.makeText(getApplicationContext(),"You can not cancel this transaction. Sales was done for any one of the item.",Toast.LENGTH_LONG);
                                        //toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                        return;
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

    public void GetViewItem(){
        DataBaseAdapter objdatabaseadpter =  new DataBaseAdapter(context);
        try {
            objdatabaseadpter.open();
            Cursor getsalesitemdetails = objdatabaseadpter.GetSalesReturnListItemDatasDB(SalesReturnListActivity.getsalesreviewtransactionno,
                    SalesReturnListActivity.getsalesreviewfinanicialyear, SalesReturnListActivity.getsalesreviewcompanycode);
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
                Toast toast = Toast.makeText(getApplicationContext(),"No Items Available",Toast.LENGTH_LONG);
                //toast.setGravity(Gravity.CENTER, 0, 0);
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
                convertView = layoutInflater.inflate(R.layout.salesreturnviewitemlist, parent, false);
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


                    convertView.setTag(mHolder);
                    convertView.setTag(R.id.listitemname, mHolder.listitemname);
                    convertView.setTag(R.id.listitemcode, mHolder.listitemcode);
                    convertView.setTag(R.id.listitemqty, mHolder.listitemqty);
                    convertView.setTag(R.id.listitemrate, mHolder.listitemrate);
                    convertView.setTag(R.id.listitemtotal, mHolder.listitemtotal);
                    convertView.setTag(R.id.labelhsntax, mHolder.labelhsntax);

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
            txtreviewweight.setText(String.valueOf(addweight));
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

    protected  class AsyncSalesReturnCancelDetails extends
            AsyncTask<String, JSONObject, ArrayList<SalesSyncDatas>> {
        ArrayList<SalesSyncDatas> List = null;
        JSONObject jsonObj = null;
        @Override
        protected  ArrayList<SalesSyncDatas> doInBackground(String... params) {
            RestAPI api = new RestAPI();
            try {
                JSONObject js_salesobj = new JSONObject();
                JSONObject js_stockobj = new JSONObject();
                try {
                    DataBaseAdapter dbadapter = new DataBaseAdapter(context);
                    dbadapter.open();
                    Cursor mCursales = dbadapter.GetSalesReturnCancelDatasDB();
                    Cursor mCurStock = dbadapter.GetCancelReturnStockTransactionDatasDB();
                    JSONArray js_array2 = new JSONArray();
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
                    js_stockobj.put("JSonObject",js_stockarray);

                    jsonObj =  api.SalesReturnCancelDetails(js_salesobj.toString(),js_stockobj.toString(),context);
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
                            objdatabaseadapter.UpdateCancelSalesReturnFlag(List.get(0).TransactionNo[j]);
                        }
                    }
                    if (List.get(0).StockTransactionNo.length > 0) {
                        for (int j = 0; j < List.get(0).StockTransactionNo.length; j++) {
                            objdatabaseadapter.UpdateCancelSalesReturnStockTransactionFlag(List.get(0).StockTransactionNo[j]);
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
        Intent i = new Intent(context, SalesReturnListActivity.class);
        startActivity(i);
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
                EpsonT20Printer epsonT20Printer = new EpsonT20Printer(SalesReturnViewActivity.this, SalesReturnViewActivity.this, receiveListener);
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
                    billPrinted = (boolean) epsonT20Printer.GetSalesReturnBillPrint(finalGetsalestransano, financialyearcode,SalesReturnViewActivity.this, true);
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

                //new AsyncPrintSalesReturnBillDCDetails().execute(finalGetsalestransano,financialyearcode);
                //billPrinted = (boolean) printData.GetDCSalesReturnPrint(finalGetsalestransano, financialyearcode,SalesReturnViewActivity.this);
                if (!billPrinted) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Unable to connect Bluetooth Printer. Please check the printer is turn or or not!", Toast.LENGTH_LONG);
                    //toast.setGravity(Gravity.CENTER, 0, 0);
                    //printpopup.dismiss();
                    toast.show();
                    //Toast.makeText(context, "Unable to connect to Bluetooth Printer!", Toast.LENGTH_SHORT).show();
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


    /*protected class AsyncPrintSalesReturnBillDCDetails extends
            AsyncTask<String, JSONObject, Boolean> {
        Boolean billPrinted = false;
        JSONObject jsonObj = null;
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
                    billPrinted = (boolean) printData.GetDCSalesReturnPrint(finalGetsalestransano, financialyearcode,SalesReturnViewActivity.this);
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
        }
        @Override
        protected void onPostExecute(Boolean billPrinted) {
            // TODO Auto-generated method stub
            try {
                Log.d("billPrinted 1q",String.valueOf(billPrinted));
                if (!billPrinted) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Unable to connect to Bluetooth Printer!", Toast.LENGTH_LONG);
                    //toast.setGravity(Gravity.CENTER, 0, 0);
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
    }*/

    public void enablePrintButtons() {
        txtviewSalesprint.setEnabled(true);
    }

    public void disablePrintButtons() {
        txtviewSalesprint.setEnabled(false);
    }
}
