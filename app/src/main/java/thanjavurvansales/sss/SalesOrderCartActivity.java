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
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
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

public class SalesOrderCartActivity extends AppCompatActivity    {
    ListView reviewlistview;
    Context context;
    TextView addmore;
    ImageView reviewlistgoback;
    public  TextView paymenttypeinvoice,txtcustomername,txtareacity,
            txtcarttotalamt,reviewitems,
            txtdiscountamt,txtsubtotalamt,cartgstnnumber,txtbookingno,txtreviewdate,hidedummy;
    public DecimalFormat df;
    Button txtSalesprint,imgcamera;
    EditText txtremarks;
    public static boolean deviceFound;
    ArrayList<SalesOrderItemDetails> salesItemList;
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
    String[] transportid,transportname,vechicle_number;
    public static TextView txttransportname;
    Dialog transportdialog;
    public static  String gettransportid = "0",gettransportname="",gettransportmode;
    ListView lv_TransportList;
    Spinner selecttransportmode;
    String[] transportmodecode,transportmodetype;
    String getsalessordervoucherno ="";

    BluetoothAdapter mBluetoothAdapter;
    public boolean isTransportSelect=false;
    TextView tv_okay;
    TransportAdapter transportAdapter=null;
    public static PreferenceMangr preferenceMangr=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_order_cart);

        //Declare All variables
        context = this;
        reviewlistview = (ListView) findViewById(R.id.reviewlistview);
        reviewlistgoback = (ImageView) findViewById(R.id.reviewlistgoback);
        addmore = (TextView)findViewById(R.id.addmore);
        paymenttypeinvoice = (TextView)findViewById(R.id.paymenttypeinvoice);
        txtcustomername = (TextView)findViewById(R.id.txtcustomername);
        txtareacity = (TextView)findViewById(R.id.txtareacity);
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
        txttransportname = (TextView)findViewById(R.id.txttransportname);
        SalesViewActivity.isduplicate = false;
        imgcamera.setVisibility(View.GONE);
        hidedummy.setVisibility(View.VISIBLE);

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

        gstnLL.setVisibility(View.VISIBLE);
        paymenttypeinvoice.setText("CREDIT");
        paymenttypeinvoice.setVisibility(View.GONE);
        getbilltypecode = "2";

        txttransportname.setText(gettransportname);
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
            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
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
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                }
            }
        });
        //GSTN NUMBER
        if(!SalesOrderActivity.gstnnumber.equals("")){
            if(SalesOrderActivity.paymenttype){
                gstnLL.setVisibility(View.VISIBLE);
                cartgstnnumber.setText(SalesOrderActivity.gstnnumber);
                gstnLL.setBackgroundColor(getResources().getColor(R.color.graycolor));
                if(SalesOrderActivity.gstnnumber.equals("")){
                    gstnLL.setBackgroundColor(getResources().getColor(R.color.graycolor));
                }
            }else{
                gstnLL.setVisibility(View.VISIBLE);
                cartgstnnumber.setText(SalesOrderActivity.gstnnumber);
                gstnLL.setBackgroundColor(getResources().getColor(R.color.green));
                if(SalesOrderActivity.gstnnumber.equals("")){
                    gstnLL.setBackgroundColor(getResources().getColor(R.color.graycolor));
                }
            }
        }else{
            gstnLL.setVisibility(View.GONE);
            cartgstnnumber.setText("");
        }


        //Set customer name
        txtcustomername.setText(SalesOrderActivity.txtcustomername.getText().toString());
        //Set area and city
        txtareacity.setText(SalesOrderActivity.txtareaname.getText().toString());
        //set current date
        txtreviewdate.setText(SalesOrderActivity.txtsalesdate.getText().toString());
        getsalesdate = preferenceMangr.pref_getString("getformatdate");

        txttransportname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetTransportMode();
            }
        });
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
            getbookingno = objdatabaseadapter.GetSalesOrderBookingNo();
            getsalessordervoucherno = objdatabaseadapter.GetSalesOrderVoucherNo(getbookingno, preferenceMangr.pref_getString("getfinanceyrcode") ,getbilltypecode);
            gettransactionno = objdatabaseadapter.GetSalesOrderTransactionNo();
            txtbookingno.setText("Order.No. : "+getsalessordervoucherno);

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
        //Item Adapter
        setItemAdapter();

        if(SalesOrderActivity.ifsavedsales) {
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
                //GetTransportMode();
                SaveGetTransportMode();
                txtSalesprint.setEnabled(false);

            }
        });

    }

    public  void GetTransportMode(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor mcur = null;
        try {
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            mcur = objdatabaseadapter.GetTransportModeDB();

            if (mcur.getCount() > 0) {
                transportmodecode = new String[mcur.getCount()];
                transportmodetype = new String[mcur.getCount()];
                for (int i = 0; i < mcur.getCount(); i++) {
                    transportmodecode[i] = mcur.getString(0);
                    transportmodetype[i] = mcur.getString(1);
                    mcur.moveToNext();
                }
                transportdialog = new Dialog(context);
                transportdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                transportdialog.setContentView(R.layout.transportpopup);
                lv_TransportList = (ListView) transportdialog.findViewById(R.id.lv_TransportList);

                selecttransportmode = (Spinner)transportdialog.findViewById(R.id.selecttransportmode);
                ImageView close = (ImageView) transportdialog.findViewById(R.id.close);
                TextView tv_okay = (TextView) transportdialog.findViewById(R.id.ttokay);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        transportdialog.dismiss();
                    }
                });
                tv_okay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        transportdialog.dismiss();
                    }
                });
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                        android.R.layout.simple_spinner_item, transportmodetype);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                selecttransportmode.setAdapter(adapter);

                selecttransportmode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        // your code here
                        int index = parentView.getSelectedItemPosition();
                        String gettransportmodecode = transportmodecode[index];
                        //if(index !=0 ) {
                            if (gettransportmodecode.equals("1")) {
                                TransportAdapter adapter = new TransportAdapter(context);
                                lv_TransportList.setAdapter(null);
                                txttransportname.setText(transportmodetype[index]);
                                gettransportid = "0";
                                gettransportmode=gettransportmodecode;
                                gettransportname = transportmodetype[index];
                                //transportdialog.dismiss();
                                isTransportSelect=true;

                            } else {
                                GetTransport(LoginActivity.getareacode, gettransportmodecode);
                            }
                        //}

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // your code here
                    }

                });

                transportdialog.show();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(),"No transport mode available", Toast.LENGTH_LONG);
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
            if(mcur != null)
                mcur.close();
        }
    }

    public  void GetTransport(String areacode,String gettransportmodecode){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetTransportDB(areacode,gettransportmodecode);

            if(Cur.getCount()>0) {
                transportid = new String[Cur.getCount()];
                transportname = new String[Cur.getCount()];
                vechicle_number = new String[Cur.getCount()];
                for(int i=0;i<Cur.getCount();i++){
                    transportid[i] = Cur.getString(0);
                    transportname[i] = Cur.getString(1);
                    vechicle_number[i] = Cur.getString(3);
                    gettransportid = transportid[i];
                    Cur.moveToNext();
                }
                TransportAdapter adapter = new TransportAdapter(context);
                lv_TransportList.setAdapter(adapter);
            }else{
                lv_TransportList.setAdapter(null);
                gettransportid = "0";
                isTransportSelect=true;
                Toast toast = Toast.makeText(getApplicationContext(),"No transport available in this city", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.BOTTOM, 0, 150);
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
        if(SalesOrderActivity.staticreviewsalesorderitems.size()>0) {
            DataBaseAdapter dataBaseAdapter =null;
            Cursor getcartdatas = null;
            try {
                //Cart Add Functionality
                dataBaseAdapter = new DataBaseAdapter(context);
                dataBaseAdapter.open();
                //Get cart datas from database temp table
                getcartdatas = dataBaseAdapter.GetSalesOrderItemsCart();

                if (getcartdatas.getCount() > 0) {
                    SalesOrderActivity.staticreviewsalesorderitems.clear();
                    for (int i = 0; i < getcartdatas.getCount(); i++) {
                        SalesOrderActivity.staticreviewsalesorderitems.add(new SalesOrderItemDetails(getcartdatas.getString(1), getcartdatas.getString(2),
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
                                getcartdatas.getString(30), getcartdatas.getString(31),
                                getcartdatas.getString(21), "", "","",""));
                        getcartdatas.moveToNext();
                    }
                    //Adapter
                    ReviewItemAdapter adapter = new ReviewItemAdapter(context, SalesOrderActivity.staticreviewsalesorderitems);
                    reviewlistview.setAdapter(adapter);
                }else{
                    SalesOrderActivity.staticreviewsalesorderitems.clear();
                    reviewlistview.setAdapter(null);
                    Toast toast = Toast.makeText(getApplicationContext(),"Cart is empty", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    //Toast.makeText(getApplicationContext(),"Cart is empty",Toast.LENGTH_SHORT).show();
                    reviewitems.setText(String.valueOf(salesItemList.size()));
                    //  SalesOrderActivity.totalcartitems.setText(String.valueOf(salesItemList.size()));
                    return;
                }
            }catch (Exception e) {
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            } finally {
                if (dataBaseAdapter != null)
                    dataBaseAdapter.close();
                if (getcartdatas != null)
                    getcartdatas.close();
            }

        }else{
            SalesOrderActivity.staticreviewsalesorderitems.clear();
            reviewlistview.setAdapter(null);
            Toast toast = Toast.makeText(getApplicationContext(),"Cart is empty", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            //Toast.makeText(getApplicationContext(),"Cart is empty",Toast.LENGTH_SHORT).show();
            reviewitems.setText(String.valueOf(salesItemList.size()));
            //  SalesOrderActivity.totalcartitems.setText(String.valueOf(salesItemList.size()));
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
            result=false;

        }
        return result;
    }
    //Area Adapter
    public class TransportAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;
        String selecteditem="";

        TransportAdapter(Context c) {
            context = c;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return transportid.length;
        }

        @Override
        public Object getItem(int position) {
            return transportid[position];
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

            final ViewHolder mHolder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.transportpopuplist, parent, false);
                mHolder = new ViewHolder();
                try {
                    mHolder.listtransportname = (TextView) convertView.findViewById(R.id.listtransportname);
                    mHolder.listtransportday = (TextView) convertView.findViewById(R.id.listtransportday);
                    mHolder.cardview = (CardView) convertView.findViewById(R.id.card_view_LLL);

                } catch (Exception e) {
                    Log.i("Transport", e.toString());
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
                mHolder.listtransportname.setText(String.valueOf(transportname[position] ));
                mHolder.listtransportday.setText(vechicle_number[position]);



            } catch (Exception e) {
                Log.i("Transport value", e.toString());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txttransportname.setText(String.valueOf(transportname[position] +" - "+vechicle_number[position]));
                    gettransportid = String.valueOf(transportid[position]);
                    gettransportname = String.valueOf(transportname[position] +" - "+vechicle_number[position]);
                    //transportdialog.dismiss();
                    isTransportSelect=true;
                    selecteditem=String.valueOf(position);
                   notifyDataSetChanged();

                }
            });

            if(selecteditem.equals(String.valueOf(position))){
                mHolder.cardview.setCardBackgroundColor(context.getResources().getColor(R.color.gray));
            }else{
                mHolder.cardview.setCardBackgroundColor(context.getResources().getColor(R.color.white));
            }


            return convertView;
        }

        private class ViewHolder {
            private TextView listtransportname,listtransportday;
            CardView cardview;

        }

    }
    public class ReviewItemAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;

        DecimalFormat  dft = new DecimalFormat("0.00");
        ReviewItemAdapter(Context c,ArrayList<SalesOrderItemDetails> myList) {
            salesItemList = myList;
            context = c;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return salesItemList.size();
        }

        @Override
        public SalesOrderItemDetails getItem(int position) {
            return (SalesOrderItemDetails) salesItemList.get(position);
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
                convertView = layoutInflater.inflate(R.layout.salesorderreviewlist, parent, false);
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

                 //  mHolder.listdiscount.setBackgroundColor(getResources().getColor(R.color.lightredgray));
                    mHolder.listdiscount.setText("");
                }else{
                    mHolder.listdiscount.setBackgroundColor(getResources().getColor(R.color.lightvoilet));
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
                    mHolder.itemLL.setBackgroundColor(getResources().getColor(R.color.lightvoilet));
                    mHolder.deleteitem.setVisibility(View.VISIBLE);
                    mHolder.dummydeleteitem.setVisibility(View.GONE);
                }


            } catch (Exception e) {
                Log.i("Item value", e.toString());
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
                                    DataBaseAdapter objdatabaseadapter = null;
                                    Cursor getcartdatas = null;
                                    try {
                                        //Order item details
                                        objdatabaseadapter = new DataBaseAdapter(context);
                                        objdatabaseadapter.open();
                                        String getresult = objdatabaseadapter.DeleteOrderItemInCart(getitemcode);
                                        if(getresult.equals("Success")){
                                            Toast toast = Toast.makeText(getApplicationContext(),"Item removed from cart", Toast.LENGTH_LONG);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();

                                            //Get cart datas from database temp table
                                            getcartdatas = objdatabaseadapter.GetSalesOrderItemsCart();
                                            SalesOrderActivity.staticreviewsalesorderitems.clear();
                                            if(getcartdatas.getCount()>0) {

                                                for (int i = 0; i < getcartdatas.getCount(); i++) {
                                                    SalesOrderActivity.staticreviewsalesorderitems.add(new SalesOrderItemDetails(getcartdatas.getString(1), getcartdatas.getString(2),
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
                                                            getcartdatas.getString(30), getcartdatas.getString(31) ,getcartdatas.getString(21),"","","",""));
                                                    getcartdatas.moveToNext();
                                                }
                                            }
                                            setItemAdapter();
                                            CalculateTotal();
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
                                        if(getcartdatas!=null)
                                            getcartdatas.close();
                                    }
                                    /*for(int p=0;p<SalesOrderActivity.staticreviewsalesorderitems.size();p++){
                                        if(salesItemList.get(position).getItemcode().equals
                                                (SalesOrderActivity.staticreviewsalesorderitems.get(p).getPurchaseitemcode())
                                        && salesItemList.get(p).getFreeflag().equals("freeitem")){
                                            deletefree = true;
                                            getpurchasitemposition = p;
                                        }
                                    }
                                    if(deletefree){
                                        SalesOrderActivity.staticreviewsalesorderitems.remove(position);
                                        SalesOrderActivity.staticreviewsalesorderitems.remove(getpurchasitemposition);
                                        Toast toast = Toast.makeText(getApplicationContext(),"Item removed from cart", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                       // Toast.makeText(getApplicationContext(),"Item removed from cart",Toast.LENGTH_SHORT).show();
                                        setItemAdapter();
                                        CalculateTotal();
                                        return;
                                    }else{
                                        SalesOrderActivity.staticreviewsalesorderitems.remove(position);
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
        //txtreviewweight.setText(String.valueOf(dft.format(res2)));
        txtdiscountamt.setText("Discount ₹  "+dft.format(Math.round(res3)));
        txtcarttotalamt.setText(dft.format(Math.round(res4)));

        reviewitems.setText(String.valueOf(salesItemList.size()));
        SalesOrderActivity.totalcartitems.setText(String.valueOf(salesItemList.size()));
    }

    /**********Asynchronous Claass***************/

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
                    Cursor mCur2 = dbadapter.GetCustomerDatasDB();
                    Cursor mCursales = dbadapter.GetSalesOrderDatasDB();
                    Cursor mCursalesitems = dbadapter.GetSalesOrderItemDatasDB();
                    JSONArray js_array2 = new JSONArray();
                    JSONArray js_array3 = new JSONArray();
                    JSONArray js_array4 = new JSONArray();
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
                        obj.put("transportmode", mCursales.getString(33));
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
                SalesOrderActivity.staticreviewsalesorderitems.clear();
                SalesOrderActivity.salesitems.clear();
                txtSalesprint.setEnabled(true);
                Intent i = new Intent(context, SalesOrderListActivity.class);
                startActivity(i);
                networkstate = isNetworkAvailable();
                if (networkstate == true) {
                    new AsyncSalesOrderDetails().execute();
                    new AsyncPriceListTransaction().execute();
                }
            }else{
                Toast toast = Toast.makeText(getApplicationContext(),"Error in image Captured", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //Toast.makeText(getApplicationContext(),"Error in image Captured",Toast.LENGTH_SHORT).show();
                SalesOrderActivity.staticreviewsalesorderitems.clear();
                SalesOrderActivity.salesitems.clear();
                txtSalesprint.setEnabled(true);
                Intent i = new Intent(context, SalesOrderListActivity.class);
                startActivity(i);
                networkstate = isNetworkAvailable();
                if (networkstate == true) {
                    new AsyncSalesOrderDetails().execute();
                    new AsyncPriceListTransaction().execute();
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
            try{
                DataBaseAdapter objcustomerAdaptor = new DataBaseAdapter(context);
                objcustomerAdaptor.open();
                Cursor mCur = objcustomerAdaptor.GetSalesImageDetails();
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

            return result;
        }
    }


    public void goBack(View v) {
        if(SalesOrderActivity.ifsavedsales){
            SalesOrderActivity.staticreviewsalesorderitems.clear();
            SalesOrderActivity.salesitems.clear();
            Intent i = new Intent(context, SalesOrderListActivity.class);
            startActivity(i);
        }else {
            Intent i = new Intent(context, SalesOrderListActivity.class);
            startActivity(i);
        }
    }
    @Override
    public void onBackPressed() {
        if(SalesOrderActivity.ifsavedsales){
            SalesOrderActivity.staticreviewsalesorderitems.clear();
            SalesOrderActivity.salesitems.clear();
            Intent i = new Intent(context, SalesOrderListActivity.class);
            startActivity(i);
        }else {
            isfromcart = true;
            finish();
        }
    }

    protected class AsyncPrintSalesOrderCarttDetails extends
            AsyncTask<String, JSONObject, Boolean> {
        Boolean billPrinted = false;
        JSONObject jsonObj = null;
        ProgressDialog loading;

        @Override
        protected Boolean doInBackground(String... params) {
            final String finalGetreceipttransano = params[0];
            final String financialyearcode = params[1];
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
                    billPrinted = (boolean) printData.GetSalesOrderBillPrint(finalGetreceipttransano, financialyearcode,SalesOrderCartActivity.this);
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
                    SalesOrderActivity.staticreviewsalesorderitems.clear();
                    SalesOrderActivity.salesitems.clear();
                    txtSalesprint.setVisibility(View.GONE);
                    txtSalesprint.setEnabled(true);
                    Intent i = new Intent(context, SalesOrderListActivity.class);
                    startActivity(i);
                    printData = null;

                }else{
                    printpopup.dismiss();
                    SalesOrderActivity.staticreviewsalesorderitems.clear();
                    SalesOrderActivity.salesitems.clear();
                    txtSalesprint.setVisibility(View.GONE);
                    txtSalesprint.setEnabled(true);
                    Intent i = new Intent(context, SalesOrderListActivity.class);
                    startActivity(i);
                    //return;
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

    public void savesalesorder(){
        DataBaseAdapter objdatabaseadapter = null;
        try {
            //Sales Temp table
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            if (SalesOrderActivity.paymenttype) {
                SalesOrderActivity.gstnnumber = "";
            }




                if (SalesOrderActivity.staticreviewsalesorderitems.size() > 0){
                    if (salesItemList.size() > 0) {
                        txtSalesprint.setEnabled(false);
                        String getmaxrefno = objdatabaseadapter.GetMaxRefnoSalesOrderItems();
                        for (int i = 0; i < salesItemList.size(); i++) {
                            objdatabaseadapter.InsertTempSalesOrderItemDetails(salesItemList.get(i).getItemcode(),
                                    salesItemList.get(i).getCompanycode(), salesItemList.get(i).getItemqty(), salesItemList.get(i).getNewprice(),
                                    salesItemList.get(i).getDiscount(), salesItemList.get(i).getSubtotal(), salesItemList.get(i).getFreeflag(),
                                    salesItemList.get(i).getTax(), SalesOrderActivity.gstnnumber, getmaxrefno, (Double.parseDouble(salesItemList.get(i).getUnitweight()) * (Double.parseDouble(salesItemList.get(i).getItemqty()))),i+1);
                        }

                        String getbillcopystatus = "";
                        if (getbilltypecode.equals("2") || (getbilltypecode.equals("1") && !SalesOrderActivity.gstnnumber.equals("")
                                && !SalesOrderActivity.gstnnumber.equals("null") && !SalesOrderActivity.gstnnumber.equals(null))) {
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
                        getsalestransactionno = objdatabaseadapter.InsertSalesOrder(preferenceMangr.pref_getString("getvancode"), getsalesdate, SalesOrderActivity.customercode,
                                getbilltypecode, SalesOrderActivity.gstnnumber, preferenceMangr.pref_getString("getschedulecode"), getsubtotalamount,
                                getdiscountamt, getgrandtotal, preferenceMangr.pref_getString("getfinanceyrcode"),
                                txtremarks.getText().toString(), getbookingno, gettransactionno,
                                getmaxrefno, getbillcopystatus, getcashpaidstatus,gettransportid,getsalessordervoucherno,gettransportmode);
                        //Get General settings
                        if (!getsalestransactionno.equals("") && !getsalestransactionno.equals(null)
                                && !getsalestransactionno.equals("null")) {
                            Toast toast = Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.BOTTOM, 0, 150);
                            toast.show();
                            SalesOrderActivity.ifsavedsales = true;
                            SalesOrderActivity.gstnnumber ="";
                            SalesOrderActivity.customercode ="";
                            txtSalesprint.setEnabled(true);
                            tv_okay.setEnabled(true);
                            //delete cart table data
                            objdatabaseadapter.DeleteSalesCartItemCart();



                            try {
                                if((preferenceMangr.pref_getString("getbusiness_type").equals("2") && preferenceMangr.pref_getString("getorderprint").equals("yes")) ||
                                        (preferenceMangr.pref_getString("getbusiness_type").equals("3") && preferenceMangr.pref_getString("getorderprint").equals("yes"))) {
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

                                                    } else {



                                                        *//*boolean billPrinted = false;
                                                        billPrinted = (boolean) printData.GetSalesOrderBillPrint(getsalestransactionno, LoginActivity.getfinanceyrcode);
                                                        if (!billPrinted) {
                                                            Toast toast = Toast.makeText(getApplicationContext(), "Unable to connect to Bluetooth Printer!", Toast.LENGTH_LONG);
                                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                                            printpopup.dismiss();
                                                            toast.show();

                                                            SalesOrderActivity.staticreviewsalesorderitems.clear();
                                                            SalesOrderActivity.salesitems.clear();
                                                            txtSalesprint.setVisibility(View.GONE);
                                                            txtSalesprint.setEnabled(true);
                                                            Intent i = new Intent(context, SalesOrderListActivity.class);
                                                            startActivity(i);
                                                            //Toast.makeText(context, "Unable to connect to Bluetooth Printer!", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }else{
                                                            printpopup.dismiss();
                                                            SalesOrderActivity.staticreviewsalesorderitems.clear();
                                                            SalesOrderActivity.salesitems.clear();
                                                            txtSalesprint.setVisibility(View.GONE);
                                                            txtSalesprint.setEnabled(true);
                                                            Intent i = new Intent(context, SalesOrderListActivity.class);
                                                            startActivity(i);
                                                            return;
                                                        }*//*


                                                    }*/

                                                if(mBluetoothAdapter != null) {

                                                    if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                                                        if(!preferenceMangr.pref_getString("SelectedPrinterAddress").equals("")) {
                                                            new AsyncPrintSalesOrderCarttDetails().execute(getsalestransactionno, preferenceMangr.pref_getString("getfinanceyrcode"));
                                                            printpopup.dismiss();
                                                        }else{
                                                            Toast toast = Toast.makeText(getApplicationContext(), "Please select bluetooth printer in app settings", Toast.LENGTH_LONG);
                                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                                            toast.show();
                                                            printpopup.dismiss();
                                                            SalesOrderActivity.staticreviewsalesorderitems.clear();
                                                            SalesOrderActivity.salesitems.clear();
                                                            txtSalesprint.setVisibility(View.GONE);
                                                            txtSalesprint.setEnabled(true);
                                                            Intent i = new Intent(context, SalesOrderListActivity.class);
                                                            startActivity(i);
                                                        }
                                                    }else{
                                                        Toast toast = Toast.makeText(getApplicationContext(), "Please turn on the bluetooth", Toast.LENGTH_LONG);
                                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                                        toast.show();
                                                        printpopup.dismiss();
                                                        SalesOrderActivity.staticreviewsalesorderitems.clear();
                                                        SalesOrderActivity.salesitems.clear();
                                                        txtSalesprint.setVisibility(View.GONE);
                                                        txtSalesprint.setEnabled(true);
                                                        Intent i = new Intent(context, SalesOrderListActivity.class);
                                                        startActivity(i);
                                                    }
                                                }else{
                                                    Toast toast = Toast.makeText(getApplicationContext(), "Bluetooth is not available.", Toast.LENGTH_LONG);
                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                    toast.show();
                                                    printpopup.dismiss();
                                                    SalesOrderActivity.staticreviewsalesorderitems.clear();
                                                    SalesOrderActivity.salesitems.clear();
                                                    txtSalesprint.setVisibility(View.GONE);
                                                    txtSalesprint.setEnabled(true);
                                                    Intent i = new Intent(context, SalesOrderListActivity.class);
                                                    startActivity(i);
                                                }
                                            } catch (Exception e) {
                                                Toast toast = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                toast.show();
                                                printpopup.dismiss();

                                                SalesOrderActivity.staticreviewsalesorderitems.clear();
                                                SalesOrderActivity.salesitems.clear();
                                                txtSalesprint.setVisibility(View.GONE);
                                                txtSalesprint.setEnabled(true);
                                                Intent i = new Intent(context, SalesOrderListActivity.class);
                                                startActivity(i);
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
                                            SalesOrderActivity.staticreviewsalesorderitems.clear();
                                            SalesOrderActivity.salesitems.clear();
                                            txtSalesprint.setVisibility(View.GONE);
                                            txtSalesprint.setEnabled(true);
                                            Intent i = new Intent(context, SalesOrderListActivity.class);
                                            startActivity(i);

                                        }
                                    });
                                    printpopup.setCanceledOnTouchOutside(false);
                                    printpopup.setCancelable(false);
                                    printpopup.show();

                                    networkstate = isNetworkAvailable();
                                    if (networkstate == true) {
                                        new AsyncSalesOrderDetails().execute();
                                        new AsyncPriceListTransaction().execute();
                                    }

                                }else {

                                    networkstate = isNetworkAvailable();
                                    if (networkstate == true) {
                                        new AsyncSalesOrderDetails().execute();
                                        new AsyncPriceListTransaction().execute();
                                    }
                                    SalesOrderActivity.staticreviewsalesorderitems.clear();
                                    SalesOrderActivity.salesitems.clear();
                                    txtSalesprint.setVisibility(View.GONE);
                                    txtSalesprint.setEnabled(true);
                                    Intent i = new Intent(context, SalesOrderListActivity.class);
                                    startActivity(i);
                                }

                            } catch (Exception e) {
                                Toast toast1 = Toast.makeText(getApplicationContext(), "Unable to connect to Bluetooth Printer!", Toast.LENGTH_LONG);
                                toast1.setGravity(Gravity.CENTER, 0, 0);
                                toast1.show();
                                //  Toast.makeText(context, "Unable to connect to Bluetooth Printer!", Toast.LENGTH_SHORT).show();
                                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                mDbErrHelper.open();
                                String geterrror = e.toString();
                                mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                mDbErrHelper.close();
                            }
                        }
                    } else {
                        txtSalesprint.setEnabled(true);
                        Toast toast = Toast.makeText(getApplicationContext(), "Cart is empty", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        // Toast.makeText(getApplicationContext(),"Cart is empty",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    txtSalesprint.setEnabled(true);
                    Toast toast = Toast.makeText(getApplicationContext(),"Cart is empty", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    // Toast.makeText(getApplicationContext(),"Cart is empty",Toast.LENGTH_SHORT).show();
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

    public  void SaveGetTransportMode(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor mcur = null;
        try {
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            mcur = objdatabaseadapter.GetTransportModeDB();

            if (mcur.getCount() > 0) {
                transportmodecode = new String[mcur.getCount()];
                transportmodetype = new String[mcur.getCount()];
                for (int i = 0; i < mcur.getCount(); i++) {
                    transportmodecode[i] = mcur.getString(0);
                    transportmodetype[i] = mcur.getString(1);
                    mcur.moveToNext();
                }
                transportdialog = new Dialog(context);
                transportdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                transportdialog.setContentView(R.layout.transportpopup);
                lv_TransportList = (ListView) transportdialog.findViewById(R.id.lv_TransportList);
                selecttransportmode = (Spinner)transportdialog.findViewById(R.id.selecttransportmode);
                ImageView close = (ImageView) transportdialog.findViewById(R.id.close);
                tv_okay = (TextView) transportdialog.findViewById(R.id.ttokay);
                tv_okay.setEnabled(true);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        transportdialog.dismiss();
                        txtSalesprint.setEnabled(true);
                    }
                });
                tv_okay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        savesalesorder();
                        tv_okay.setEnabled(false);
                        transportdialog.dismiss();
                        //Toast.makeText(context,gettransportid,Toast.LENGTH_SHORT).show();
                    }
                });
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                        android.R.layout.simple_spinner_item, transportmodetype);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                selecttransportmode.setAdapter(adapter);

                selecttransportmode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        // your code here
                        int index = parentView.getSelectedItemPosition();
                        String gettransportmodecode = transportmodecode[index];
                        //if(index !=0 ) {
                        if (gettransportmodecode.equals("1")) {
                            TransportAdapter adapter = new TransportAdapter(context);
                            lv_TransportList.setAdapter(null);
                            txttransportname.setText(transportmodetype[index]);
                            gettransportid = "0";
                            gettransportname = transportmodetype[index];
                            gettransportmode=gettransportmodecode;
                            //transportdialog.dismiss();
                            isTransportSelect=true;

                        } else {
                            GetTransport(LoginActivity.getareacode, gettransportmodecode);
                            gettransportmode=gettransportmodecode;
                        }
                        //}

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // your code here
                    }

                });
                transportdialog.setCanceledOnTouchOutside(false);
                transportdialog.setCancelable(false);
                transportdialog.show();

            }else{
                Toast toast = Toast.makeText(getApplicationContext(),"No transport mode available", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.BOTTOM, 0, 150);
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
            if(mcur != null)
                mcur.close();
        }
    }

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
                Log.d("AsyncPriceList", e.getMessage());
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
