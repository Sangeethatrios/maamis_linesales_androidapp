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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class PremiumCustomerOrderViewActivity extends AppCompatActivity {
    TextView txtviewbookingno,txtviewreviewdate,viewpaymenttypeinvoice,txtviewcustomername,txtviewareacity,
            viewcartgstnnumber,txtviewsubtotalamt,txtviewdiscountamt,txtviewcarttotalamt,
            reviewitems,txtbillno,txtviewshortname,txtviewtotamt, txtConvertToSales;
    ListView viewSalesListview;
    Context context;
    public static boolean deviceFound;
    ImageButton viewlistgoback;
    ArrayList<PremiumCustomerOrderItemDetails> salesitemviewlist =new ArrayList<PremiumCustomerOrderItemDetails>();
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

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium_customer_order_view);

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
        txtviewcarttotalamt = (TextView)findViewById(R.id.txtviewcarttotalamt);
        viewSalesListview = (ListView) findViewById(R.id.viewSalesListview);
        reviewitems = (TextView)findViewById(R.id.reviewitems);
        gstnLL = (LinearLayout)findViewById(R.id.gstnLL);
        viewlistgoback = (ImageButton)findViewById(R.id.viewlistgoback);
        txtviewshortname = (TextView)findViewById(R.id.txtviewshortname);
        txtviewtotamt = (TextView)findViewById(R.id.txtviewtotamt);
        txttransportname = (TextView)findViewById(R.id.txttransportname);
        txtConvertToSales = (TextView) findViewById(R.id.txtConvertToSales);

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
            Cursor curOrderDetails = objdatabaseadpter.GetPremiumCustomerOrderListDatasDB(PremiumCustomerOrderListActivity.getsalesreviewtransactionno,
                    PremiumCustomerOrderListActivity.getsalesreviewfinanicialyear, PremiumCustomerOrderListActivity.getsalesreviewcompanycode);
            if(curOrderDetails.getCount()>0){

                // for(int i=0;i<curOrderDetails.getCount();i++){
                txtviewbookingno.setText("Order.No. : "+curOrderDetails.getString(curOrderDetails.getColumnIndex("orderno")) );
                txtbillno.setText("Order No. :" +curOrderDetails.getString(curOrderDetails.getColumnIndex("orderno")));
                txtviewreviewdate.setText(curOrderDetails.getString(curOrderDetails.getColumnIndex("orderdate")));

                txtviewshortname.setText(curOrderDetails.getString(curOrderDetails.getColumnIndex("shortname")));
                txtviewtotamt.setText("Total ₹ "+dft.format(Double.parseDouble(curOrderDetails.getString(curOrderDetails.getColumnIndex("totalamt")))));
                txtviewcustomername.setText(curOrderDetails.getString(curOrderDetails.getColumnIndex("customernametamil")));
                txttransportname.setText(curOrderDetails.getString(curOrderDetails.getColumnIndex("transport")));
                txtviewareacity.setText(curOrderDetails.getString(curOrderDetails.getColumnIndex("areaname"))+" , "+curOrderDetails.getString(curOrderDetails.getColumnIndex("cityname")));

                if(curOrderDetails.getString(curOrderDetails.getColumnIndex("gstin")).equals("")
                        || curOrderDetails.getString(curOrderDetails.getColumnIndex("gstin")).equals(null) ||
                        curOrderDetails.getString(curOrderDetails.getColumnIndex("gstin")).equals("null")){
                    viewcartgstnnumber.setText("");
                    viewcartgstnnumber.setVisibility(View.GONE);
                }else {
                    viewcartgstnnumber.setVisibility(View.VISIBLE);
                    viewcartgstnnumber.setText("GSTIN :"+curOrderDetails.getString(curOrderDetails.getColumnIndex("gstin")));
                }

                txtviewcarttotalamt.setText("₹ "+dft.format(Math.round(Double.parseDouble(curOrderDetails.getString(curOrderDetails.getColumnIndex("grandtotal"))))));
                txtviewsubtotalamt.setText("₹ "+dft.format(Math.round(Double.parseDouble(curOrderDetails.getString(curOrderDetails.getColumnIndex("subtotal"))))));
                txtviewdiscountamt.setText("₹ "+dft.format(Math.round(Double.parseDouble(curOrderDetails.getString(curOrderDetails.getColumnIndex("discount"))))));

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

        txtConvertToSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            Cursor curItemDetails = objdatabaseadpter.GetPremiumCustomerOrderListItemDatasDB(PremiumCustomerOrderListActivity.getsalesreviewtransactionno,
                    PremiumCustomerOrderListActivity.getsalesreviewfinanicialyear, PremiumCustomerOrderListActivity.getsalesreviewcompanycode);
            if(curItemDetails.getCount()>0){
                salesitemviewlist.clear();
                for(int i=0;i<curItemDetails.getCount();i++){
                    salesitemviewlist.add(new PremiumCustomerOrderItemDetails(curItemDetails.getString(0),
                            curItemDetails.getString(1),
                            curItemDetails.getString(2),curItemDetails.getString(3),
                            curItemDetails.getString(4),curItemDetails.getString(5),
                            curItemDetails.getString(6),
                            curItemDetails.getString(7),curItemDetails.getString(8),
                            curItemDetails.getString(9),
                            curItemDetails.getString(10),curItemDetails.getString(11),
                            curItemDetails.getString(12)));
                    curItemDetails.moveToNext();
                }
                reviewitems.setText(String.valueOf(curItemDetails.getCount()));
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
        ArrayList<PremiumCustomerOrderItemDetails> salesItemList;

        ViewItemAdapter(Context c,ArrayList<PremiumCustomerOrderItemDetails> myList) {
            salesItemList = myList;
            context = c;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return salesItemList.size();
        }

        @Override
        public PremiumCustomerOrderItemDetails getItem(int position) {
            return (PremiumCustomerOrderItemDetails) salesItemList.get(position);
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
                    mHolder.schemecount = (TextView)convertView.findViewById(R.id.schemecount);
                    mHolder.dummycount = (TextView)convertView.findViewById(R.id.dummycount);

                    convertView.setTag(mHolder);
                    convertView.setTag(R.id.listitemname, mHolder.listitemname);
                    convertView.setTag(R.id.listitemcode, mHolder.listitemcode);
                    convertView.setTag(R.id.listitemqty, mHolder.listitemqty);
                    convertView.setTag(R.id.listitemrate, mHolder.listitemrate);
                    convertView.setTag(R.id.listitemtotal, mHolder.listitemtotal);
                    convertView.setTag(R.id.labelhsntax, mHolder.labelhsntax);
                    convertView.setTag(R.id.labelstockunit, mHolder.labelstockunit);
                    convertView.setTag(R.id.schemecount, mHolder.schemecount);
                    convertView.setTag(R.id.dummycount, mHolder.dummycount);
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
            mHolder.schemecount.setTag(position);
            mHolder.dummycount.setTag(position);
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

                df = new DecimalFormat("0."+getnoofdigits);


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

                if(!Utilities.isNullOrEmpty(salesItemList.get(position).getDiscount())
                        && Double.parseDouble(salesItemList.get(position).getDiscount()) > 0){

                    mHolder.listdiscount.setBackgroundColor(getResources().getColor(R.color.orangecolor));
                    mHolder.listdiscount.setText("Disc: " + dft.format(Double.parseDouble(salesItemList.get(position).getDiscount())));
                }else{
                    mHolder.listdiscount.setBackgroundColor(getResources().getColor(R.color.lightvoilet));
                    mHolder.listdiscount.setText("");
                }

                //Check Free Item
                if(salesItemList.get(position).getFreeitemstatus().equals("freeitem")){
                    mHolder.itemLL.setBackgroundColor(getResources().getColor(R.color.lightblue));
                    mHolder.listdiscount.setBackgroundColor(getResources().getColor(R.color.lightblue));
                    mHolder.listdiscount.setText("");

                    mHolder.schemecount.setVisibility(View.VISIBLE);
                    mHolder.schemecount.setText("F");
                    mHolder.dummycount.setVisibility(View.GONE);
                } else if(salesItemList.get(position).getFreeitemstatus().equals("freerate")) {
                    mHolder.itemLL.setBackgroundColor(getResources().getColor(R.color.lightbiscuit));
                    mHolder.listdiscount.setBackgroundColor(getResources().getColor(R.color.orangecolor));

                    mHolder.schemecount.setVisibility(View.VISIBLE);
                    mHolder.schemecount.setText("R");
                    mHolder.dummycount.setVisibility(View.GONE);
                }else{
                    mHolder.itemLL.setBackgroundColor(getResources().getColor(R.color.lightbiscuit));
                    mHolder.listdiscount.setBackgroundColor(getResources().getColor(R.color.lightbiscuit));

                    mHolder.schemecount.setVisibility(View.GONE);
                    mHolder.schemecount.setText("");
                    mHolder.dummycount.setVisibility(View.VISIBLE);
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
            private TextView listitemtotal,labelhsntax,labelstockunit,listdiscount, schemecount, dummycount;
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

    public void goBack(View v) {
        Log.i("ongoback","ongoback");
        Intent i = new Intent(context, PremiumCustomerOrderListActivity.class);
        startActivity(i);
    }
    @Override
    public void onBackPressed() {
        Log.i("onBackPressed","onBackPressed");
        goBack(null);
    }

}
