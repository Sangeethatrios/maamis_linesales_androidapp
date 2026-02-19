package trios.linesales;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class StockReturnCartActivity extends AppCompatActivity {
    ListView stockreturnlistview;
    Context context;
    ImageView reviewlistgoback;
    TextView cartprint,addmorecart,txtcapacity, editcart;
    ArrayList<OrderFormDetails> stockreturnlist;
    TextView stockreturnreviewitems,orderreviewweight;
    boolean networkstate;
    final DecimalFormat df = new DecimalFormat("0.00");
    boolean issaveditems=false;
    public static PreferenceMangr preferenceMangr=null;
    ProgressDialog progressDialog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_return_cart);

        //Declare All variables
        context = this;
        stockreturnlistview = (ListView) findViewById(R.id.orderlistview);
        reviewlistgoback = (ImageView) findViewById(R.id.reviewlistgoback);
        cartprint = (TextView)findViewById(R.id.cartprint);
        addmorecart = (TextView)findViewById(R.id.addmorecart);
        stockreturnreviewitems = (TextView)findViewById(R.id.orderreviewitems);
        orderreviewweight = (TextView)findViewById(R.id.orderreviewweight);
        txtcapacity = (TextView)findViewById(R.id.txtcapacity);
        editcart = (TextView) findViewById(R.id.editcart);

        try {
            preferenceMangr = new PreferenceMangr(context);
        }catch (Exception e){
            Log.d("Preference Manager : ",e.toString());
        }

        if (!Utilities.isNetworkAvailable(context, false)) {
            Toast.makeText(context, "Internet is required for this transaction", Toast.LENGTH_SHORT).show();
            finish();
        }

        txtcapacity.setText("Capacity "+preferenceMangr.pref_getString("getcapacity")+" kg");

        reviewlistgoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack(null);
            }
        });

        addmorecart.setVisibility(View.VISIBLE);
        cartprint.setVisibility(View.VISIBLE);
        editcart.setVisibility(View.GONE);

        if (getIntent() != null) {
            if (getIntent().hasExtra("MODE") &&
                !Utilities.isNullOrEmpty(getIntent().getStringExtra("MODE")) &&
                    (getIntent().getStringExtra("MODE").equals("EDIT") ||
                    getIntent().getStringExtra("MODE").equals("NEW"))) {
                //Bind item to cart
                SetStockReturnItemToCart();
            } else{
                getStockReturnFromDB();
            }
        } else {
            getStockReturnFromDB();
        }

        if (StockReturnActivity.cartStockReturnDetails == null)
            StockReturnActivity.cartStockReturnDetails = new ArrayList<OrderFormDetails>();

        if(StockReturnActivity.cartStockReturnDetails.size()>0) {
            stockreturnreviewitems.setText(String.valueOf(StockReturnActivity.cartStockReturnDetails.size()));
        }else{
            stockreturnreviewitems.setText("0");
        }
        addmorecart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack(null);
            }
        });
        editcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StockReturnCartActivity.this, StockReturnActivity.class);
                intent.putExtra("MODE", "EDIT");
                startActivity(intent);
            }
        });
        //Print Action
        cartprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!Utilities.isNetworkAvailable(context, false)) {
                    Toast.makeText(context, "Internet is required for this transaction", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (getIntent() != null) {
                    if (getIntent().hasExtra("MODE") &&
                            !Utilities.isNullOrEmpty(getIntent().getStringExtra("MODE")) &&
                            getIntent().getStringExtra("MODE").equals("EDIT")) {
                        if (!checkStockReturnStatus("SAVE"))
                            return;
                    }
                }

                DataBaseAdapter objdatabaseadapter = null;
                try {
                    //Order item details
                    objdatabaseadapter = new DataBaseAdapter(context);
                    objdatabaseadapter.open();
                    String getresult="";
                    if( stockreturnlist.size() >0) {
                        //Delete order details
                        objdatabaseadapter.DeleteStockReturn();
                        for (int i = 0; i < stockreturnlist.size(); i++) {
                             getresult=objdatabaseadapter.InsertStockReturn(stockreturnlist.get(i).getItemcode(),
                                    stockreturnlist.get(i).getQty(), stockreturnlist.get(i).getStatus() );
                        }
                        if(getresult.equals("success")){
                            Toast toast = Toast.makeText(getApplicationContext(),"Saved Successfully", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            //Toast.makeText(getApplicationContext(),"Saved Successfully",Toast.LENGTH_SHORT).show();
                            LoginActivity.ismenuopen=true;
                            Intent i = new Intent(context,MenuActivity.class);
                           startActivity(i);
                        }
                        if (Utilities.isNetworkAvailable(context)) {
                            new AsyncStockReturnDetails().execute();
                        }
                     }else{
                        Toast toast = Toast.makeText(getApplicationContext(),"Cart is empty", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }
                    } catch (Exception e) {
                        Toast toast = Toast.makeText(getApplicationContext(),"Error in saving", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        //Toast.makeText(getApplicationContext(),"Error in saving",Toast.LENGTH_SHORT).show();
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
    public void SetStockReturnItemToCart(){
        try {

            if (StockReturnActivity.cartStockReturnDetails.size() > 0) {
                StockReturnActivity.carttotamount.setText(String.valueOf(StockReturnActivity.cartStockReturnDetails.size()));
                OrderFormItemAdapter adapter = new OrderFormItemAdapter(context, StockReturnActivity.cartStockReturnDetails);
                stockreturnlistview.setAdapter(adapter);
            } else {
                StockReturnActivity.cartStockReturnDetails.clear();
                OrderFormItemAdapter adapter = new OrderFormItemAdapter(context, StockReturnActivity.cartStockReturnDetails);
                stockreturnlistview.setAdapter(adapter);
                StockReturnActivity.carttotamount.setText(String.valueOf(StockReturnActivity.cartStockReturnDetails.size()));
                Toast toast = Toast.makeText(getApplicationContext(), "Cart is empty", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //Toast.makeText(getApplicationContext(),"Cart is empty",Toast.LENGTH_SHORT).show();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    public  void GetStockReturnItems(String getorderschedulecode){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetStockReturnItemDB("0","0",getorderschedulecode);
            if(Cur.getCount()>0) {
                StockReturnActivity.cartStockReturnDetails.clear();
                for(int i=0;i<Cur.getCount();i++){
                    StockReturnActivity.cartStockReturnDetails.add(new OrderFormDetails(Cur.getString(0),Cur.getString(1),
                            Cur.getString(2),Cur.getString(3),Cur.getString(4),
                            Cur.getString(5),Cur.getString(6),
                            Cur.getString(7),Cur.getString(8),Cur.getString(9),
                            Cur.getString(11), String.valueOf(i+1),Cur.getString(10),"", Cur.getString(12) ));
                    Cur.moveToNext();
                }

                OrderFormItemAdapter adapter = new OrderFormItemAdapter(context, StockReturnActivity.cartStockReturnDetails);
                stockreturnlistview.setAdapter(adapter);
            }else{
                Toast toast = Toast.makeText(getApplicationContext(),"No item available", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //Toast.makeText(getApplicationContext(),"No item available",Toast.LENGTH_SHORT).show();
            }
        }  catch (Exception e){
            Log.i("GetItem", e.toString());
        }
        finally {
            // this gets called even if there is an exception somewhere above
            if(objdatabaseadapter != null)
                objdatabaseadapter.close();
            if(Cur != null)
                Cur.close();
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


    /**********Asynchronous Claass***************/

    protected  class AsyncStockReturnDetails extends
            AsyncTask<String, JSONObject, ArrayList<StockReturnDatas>> {
        ArrayList<StockReturnDatas> List = null;
        JSONObject jsonObj = null;
        @Override
        protected  ArrayList<StockReturnDatas> doInBackground(String... params) {
            RestAPI api = new RestAPI();
            try {
                JSONObject js_obj = new JSONObject();
                try {
                    DataBaseAdapter dbadapter = new DataBaseAdapter(context);
                    dbadapter.open();
                    Cursor mCur2 = dbadapter.GetStockReturnDetailsDatasDB();
                    JSONArray js_array2 = new JSONArray();
                    for (int i = 0; i < mCur2.getCount(); i++) {
                        JSONObject obj = new JSONObject();
                        obj.put("autonum", mCur2.getString(0));
                        obj.put("stocktransferno", mCur2.getString(1));
                        obj.put("transactiondate", mCur2.getString(2));
                        obj.put("vancode", mCur2.getString(3));
                        obj.put("schedulecode", mCur2.getString(4));
                        obj.put("itemcode", mCur2.getString(5));
                        obj.put("qty", mCur2.getString(6));
                        obj.put("makerid", mCur2.getString(7));
                        obj.put("createddate", mCur2.getString(8));
                        obj.put("flag", mCur2.getString(9));
                        obj.put("status", mCur2.getString(10));
                        js_array2.put(obj);
                        mCur2.moveToNext();
                    }
                    js_obj.put("JSonObject", js_array2);

                    jsonObj =  api.StockReturnDetails(js_obj.toString(),context);
                    //Call Json parser functionality
                    JSONParser parser = new JSONParser();
                    //parse the json object to boolean
                    List = parser.parseStockReturnDataList(jsonObj);
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
        protected void onPostExecute(ArrayList<StockReturnDatas> result) {
            // TODO Auto-generated method stub
            try {
                if (result.size() > 0) {
                    for (int j = 0; j < result.size(); j++) {
                        DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
                        dataBaseAdapter.open();
                        dataBaseAdapter.UpdateStockReturnFlag(result.get(j).getScheduleCode(), result.get(j).getStockTransferno());
                        dataBaseAdapter.close();
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

    /**********END Asynchronous Claass***************/
    //Get Order form item details
    public class OrderFormItemAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;
        DecimalFormat defor = null;

        OrderFormItemAdapter(Context c,ArrayList<OrderFormDetails> myList) {
            context = c;
            layoutInflater = LayoutInflater.from(context);
            stockreturnlist = myList;
        }

        @Override
        public int getCount() {
            return stockreturnlist.size();
        }

        @Override
        public OrderFormDetails getItem(int position) {
            return (OrderFormDetails) stockreturnlist.get(position);
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


        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final DecimalFormat df = new DecimalFormat("0.00");
            final ViewHolder1 mHolder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.stockreturncartlist, parent, false);
                mHolder = new ViewHolder1();
                try {
                    mHolder.cartsno = (TextView) convertView.findViewById(R.id.cartordersno);
                    mHolder.cartitemname = (TextView) convertView.findViewById(R.id.cartorderitemname);
                    mHolder.cartqty = (EditText) convertView.findViewById(R.id.cartorderqty);
                    mHolder.cartclosingstock = (TextView) convertView.findViewById(R.id.cartorderclosingstock);
                    mHolder.cartdelete = (ImageView) convertView.findViewById(R.id.cartdelete);
                    mHolder.card_view = (LinearLayout)convertView.findViewById(R.id.card_view);
                    mHolder.cartunit = (TextView)convertView.findViewById(R.id.cartorderunit);
                    mHolder.carttotalstock = (TextView)convertView.findViewById(R.id.cartordertotalstock);


                    convertView.setTag(mHolder);
                    convertView.setTag(R.id.cartordersno, mHolder.cartsno);
                    convertView.setTag(R.id.cartorderitemname, mHolder.cartitemname);
                    convertView.setTag(R.id.cartorderqty, mHolder.cartqty);
                    convertView.setTag(R.id.cartorderclosingstock, mHolder.cartclosingstock);
                    convertView.setTag(R.id.cartdelete, mHolder.cartdelete);
                    convertView.setTag(R.id.cartorderunit, mHolder.cartunit);
                    convertView.setTag(R.id.cartordertotalstock, mHolder.carttotalstock);
                    /*convertView.setTag(R.id.listitemname, mHolder.cartordersno);
                    convertView.setTag(R.id.listitemcode, mHolder.cartorderitemname);
                    convertView.setTag(R.id.listitemqty, mHolder.cartorderqty);
                    convertView.setTag(R.id.listitemrate, mHolder.cartorderclosingstock);
                    convertView.setTag(R.id.listitemtotal, mHolder.cartdelete);
                    convertView.setTag(R.id.cartorderunit, mHolder.cartorderunit);
                    convertView.setTag(R.id.cartordertotalstock, mHolder.cartordertotalstock);*/


                } catch (Exception e) {
                    Log.i("OrderForm", e.toString());
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
            mHolder.cartsno.setTag(position);
            mHolder.cartitemname.setTag(position);
            mHolder.cartqty.setTag(position);
            mHolder.cartclosingstock.setTag(position);
            mHolder.cartdelete.setTag(position);
            mHolder.cartunit.setTag(position);

            try {
                final OrderFormDetails currentListData = getItem(position);
                mHolder.cartsno.setText(String.valueOf(position+1));
                mHolder.cartclosingstock.setText(currentListData.getClosingstk());
                mHolder.cartqty.setText(currentListData.getQty());
                mHolder.cartunit.setVisibility(View.GONE);
                if (!(currentListData.getItemnametamil().equals(""))
                        && !(currentListData.getItemnametamil()).equals("null")
                        && !((currentListData.getItemnametamil()).equals(null))) {
                    mHolder.cartitemname.setText(currentListData.getItemnametamil() + " - " +
                            currentListData.getUnitname() );
                }else{
                    mHolder.cartitemname.setText(currentListData.getItemname() + " - " +
                            currentListData.getUnitname());
                }
                mHolder.cartitemname.setTextColor(Color.parseColor(currentListData.getColourcode()));
                mHolder.cartunit.setText(currentListData.getUnitname());


                if(currentListData.getStatus().equals("deleted")){
                   // mHolder.cartorderqty.setText("0");
                    mHolder.card_view.setBackgroundColor(Color.parseColor("#F8D3D6"));
                }

                String noOfDecimal = currentListData.getNoofdeciaml();
                String getnoofdigits = "0";
                if (noOfDecimal.equals("0")) {
                    getnoofdigits = "";
                }
                if (noOfDecimal.equals("1")) {
                    getnoofdigits = "0";
                }
                if (noOfDecimal.equals("2")) {
                    getnoofdigits = "00";
                }
                if (noOfDecimal.equals("3")) {
                    getnoofdigits = "000";
                }

                if (Utilities.isNullOrEmpty(getnoofdigits))
                    defor = new DecimalFormat("0");
                else
                    defor = new DecimalFormat("0." + getnoofdigits );


                mHolder.cartqty.addTextChangedListener(new TextWatcher() {
                    public void onTextChanged(CharSequence s, int start, int before,
                                              int count) {
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                  int after) {
                    }

                    public void afterTextChanged(Editable s) {
                        if(!Utilities.isNullOrEmpty(mHolder.cartqty.getText().toString())){
                            double a = Double.parseDouble(currentListData.getClosingstk());
                            double b = Double.parseDouble(mHolder.cartqty.getText().toString());
                            double c=a-b;
//                            int getval = (int) c;
                            double getval = Math.round(c);
//                            mHolder.carttotalstock.setText(String.valueOf(getval));
                            mHolder.carttotalstock.setText(defor.format(c));

                            currentListData.setQty( mHolder.cartqty.getText().toString());
                        }else{
                            mHolder.carttotalstock.setText("");
                            currentListData.setQty("0");
                        }
                    }
                });
                CalculateWeight();
                if(issaveditems){
                    mHolder.cartdelete.setVisibility(View.GONE);
                   /* if( !mHolder.cartorderqty.getText().toString().equals("") &&
                            !mHolder.cartorderqty.getText().toString().equals(null) &&
                            !mHolder.cartorderqty.getText().toString().equals("null")){
                        double a = Double.parseDouble(currentListData.getClosingstk());
                        double b = Double.parseDouble(mHolder.cartorderqty.getText().toString());
                        double c=a+b;
                        int getval = (int) c;
                        mHolder.cartordertotalstock.setText(String.valueOf(getval));

                        currentListData.setQty( mHolder.cartorderqty.getText().toString());
                    }*/
                }else{
                    mHolder.cartdelete.setVisibility(View.VISIBLE);
                }
                if(!Utilities.isNullOrEmpty(mHolder.cartqty.getText().toString())){
                    double a = Double.parseDouble(currentListData.getClosingstk());
                    double b = Double.parseDouble(mHolder.cartqty.getText().toString());
                    double c=a-b;
//                    int getval = (int) c;
                    double getval = Math.round(c);
                    mHolder.carttotalstock.setText(defor.format(c));

                    currentListData.setQty( mHolder.cartqty.getText().toString());
                }else{
                    mHolder.carttotalstock.setText("");
                    currentListData.setQty("0");
                }
                mHolder.cartdelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Delete item
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Are you sure you want to delete ?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        StockReturnActivity.cartStockReturnDetails.remove(position);
                                        Toast toast = Toast.makeText(getApplicationContext(),"Item removed from cart", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                        //Toast.makeText(getApplicationContext(),"Item removed from cart",Toast.LENGTH_SHORT).show();
                                        SetStockReturnItemToCart();
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


            } catch (Exception e) {
                Log.i("Order value", e.toString());

            }

            return convertView;
        }

        private class ViewHolder1 {
            private TextView cartsno, cartunit;
            private TextView cartitemname;
            private EditText cartqty;
            private TextView cartclosingstock, carttotalstock;
            private ImageView cartdelete;
            LinearLayout card_view;
        }
    }
    public void CalculateWeight() {
        final DecimalFormat dft = new DecimalFormat("0.00");
        double res1 = 0;
        double res2 = 0;
        if(stockreturnlist.size()>0) {
            for (int i = 0; i < stockreturnlist.size(); i++) {
                String uppweight = stockreturnlist.get(i).getUnitweight();
                String getqty = stockreturnlist.get(i).getQty();
                String getsaleseqty;
                if (getqty.equals("")) {
                    getsaleseqty = "0";
                } else {
                    getsaleseqty = getqty;
                }
                String getweight;
                if (uppweight.equals("")) {
                    getweight = "0";
                } else {
                    getweight = uppweight;
                }

                res1 = res1 + (Double.parseDouble(getsaleseqty) *  Double.parseDouble(getweight));
            }
            orderreviewweight.setText(dft.format(res1));
        }else {
            orderreviewweight.setText("0");
        }

    }
    public void goBack(View v) {
        /*Intent i = new Intent(context, StockReturnActivity.class);
        startActivity(i);*/
//        StockReturnActivity.GetItem("");
        finish();
    }
    @Override
    public void onBackPressed() {
        goBack(null);
    }

    public boolean checkStockReturnStatus (String from) {
        boolean result = false;
        ArrayList<StockReturnDatas> List = null;
        JSONObject jsonObj = null;
        String response = "";
        RestAPI api = new RestAPI();
        try {

            String vancode = preferenceMangr.pref_getString("getvancode");
            String schedulecode = preferenceMangr.pref_getString("getschedulecode");
            String transactiondate = preferenceMangr.pref_getString("getformatdate");

            showLoader();
            jsonObj =  api.CheckStockReturn(context, vancode, schedulecode, transactiondate);
            if(jsonObj!=null){
                response = jsonObj.getString("success");
                if (!Utilities.isNullOrEmpty(response)) {
                    if (response.equals("1")) {
                        result = true;
                        if (from.equals("LOAD")) {
                            editcart.setVisibility(View.VISIBLE);
                        }
                    } else if (response.equals("2")) {
                        result = false;
                        if (from.equals("LOAD")) {
                            editcart.setVisibility(View.GONE);
                            Toast.makeText(context, "This stock return has already been approved. Editing is not allowed", Toast.LENGTH_LONG).show();
                        } else if (from.equals("SAVE")) {
                            Toast.makeText(context, "This stock return has already been approved. Editing is not allowed", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
            hideLoader();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            hideLoader();
            Log.d("AsyncScheduleDetails", e.getMessage());
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
        return result;
    }

    public void showLoader() {
        progressDialog = ProgressDialog.show(context, "Loading", "Please wait...", true, true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    public void hideLoader() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    public void getStockReturnFromDB() {

        if (!Utilities.isNetworkAvailable(context, false)) {
            Toast.makeText(context, "Internet is required for this transaction", Toast.LENGTH_SHORT).show();
            return;
        }
        //Get Current date
        DataBaseAdapter objdatabaseadapter = null;
        try {
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            //LoginActivity.getformatdate = objdatabaseadapter.GenCreatedDate();
            //LoginActivity.getcurrentdatetime = objdatabaseadapter.GenCurrentCreatedDate();

            preferenceMangr.pref_putString("getformatdate", objdatabaseadapter.GenCreatedDate());
            preferenceMangr.pref_putString("getcurrentdatetime", objdatabaseadapter.GenCurrentCreatedDate());

            String getschedulecode = objdatabaseadapter.GetScheduleCode();
            String getstockreturncount = objdatabaseadapter.GetStockReturnCount(getschedulecode);
            if (!getstockreturncount.equals("") && !getstockreturncount.equals("0") && !getstockreturncount.equals("null")) {
                if (Double.parseDouble(getstockreturncount) > 0) {
                    issaveditems = true;
                    addmorecart.setVisibility(View.GONE);
                    cartprint.setVisibility(View.GONE);
//                    editcart.setVisibility(View.VISIBLE);
                    GetStockReturnItems(getschedulecode);
                } else {
                    issaveditems = false;
                }
            } else {
                issaveditems = false;
            }

            if (Utilities.isNetworkAvailable(context))
                checkStockReturnStatus("LOAD");

        } catch (Exception e) {
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        } finally {
            // this gets called even if there is an exception somewhere above
            if (objdatabaseadapter != null)
                objdatabaseadapter.close();
        }
    }
}
