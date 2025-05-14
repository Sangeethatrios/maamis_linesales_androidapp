package trios.linesales;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class OrderFormCartActivity extends AppCompatActivity {
    ListView orderlistview;
    Context context;
    ImageView reviewlistgoback;
    TextView cartprint,addmorecart,txtcapacity;
    ArrayList<OrderFormDetails> orderformlist;
    TextView orderreviewitems,orderreviewweight;
    boolean networkstate;
    final DecimalFormat df = new DecimalFormat("0.00");
    boolean issaveditems=false;
    public static PreferenceMangr preferenceMangr=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_form_cart);

        //Declare All variables
        context = this;
        orderlistview = (ListView) findViewById(R.id.orderlistview);
        reviewlistgoback = (ImageView) findViewById(R.id.reviewlistgoback);
        cartprint = (TextView)findViewById(R.id.cartprint);
        addmorecart = (TextView)findViewById(R.id.addmorecart);
        orderreviewitems = (TextView)findViewById(R.id.orderreviewitems);
        orderreviewweight = (TextView)findViewById(R.id.orderreviewweight);
        txtcapacity = (TextView)findViewById(R.id.txtcapacity);

        try {
            preferenceMangr = new PreferenceMangr(context);
        }catch (Exception e){
            Log.d("Preference Manager : ",e.toString());
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

        //Get Current date
        DataBaseAdapter objdatabaseadapter = null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            //LoginActivity.getformatdate = objdatabaseadapter.GenCreatedDate();
            //LoginActivity.getcurrentdatetime = objdatabaseadapter.GenCurrentCreatedDate();

            preferenceMangr.pref_putString("getformatdate",objdatabaseadapter.GenCreatedDate());
            preferenceMangr.pref_putString("getcurrentdatetime",objdatabaseadapter.GenCurrentCreatedDate());

            String  getorderschedulecode = objdatabaseadapter.GetScheduleCode();
            String getordercount = objdatabaseadapter.GetOrderFormCount(getorderschedulecode);
            if(!getordercount.equals("") && !getordercount.equals("0") && !getordercount.equals("null")){
                if(Double.parseDouble(getordercount) >0) {
                    issaveditems = true;
                    addmorecart.setVisibility(View.GONE);
                    cartprint.setVisibility(View.GONE);
                    GetOrderItem(getorderschedulecode);
                }else{
                    issaveditems =false;
                }
            }else{
                issaveditems =false;
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

        if(OrderFormActivity.cartorderFormDetails.size()>0) {
            orderreviewitems.setText(String.valueOf(OrderFormActivity.cartorderFormDetails.size()));
        }else{
            orderreviewitems.setText("0");
        }
        addmorecart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack(null);
            }
        });
        //Print Action
        cartprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataBaseAdapter objdatabaseadapter = null;
                try {
                    //Order item details
                    objdatabaseadapter = new DataBaseAdapter(context);
                    objdatabaseadapter.open();
                    String getresult="";
                    if( orderformlist.size() >0) {
                        //Delete order details
                        objdatabaseadapter.DeleteOrderDetails();
                        for (int i = 0; i < orderformlist.size(); i++) {
                             getresult=objdatabaseadapter.InsertOrderDetails(orderformlist.get(i).getItemcode(),
                                    orderformlist.get(i).getQty(),orderformlist.get(i).getStatus() );
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
                        networkstate = isNetworkAvailable();
                        if (networkstate == true) {
                            new AsyncOrderDetails().execute();
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

        //Bind item to cart
        SetOrderItemToCart();
    }
    public void SetOrderItemToCart(){
        try {

            if (OrderFormActivity.cartorderFormDetails.size() > 0) {
                OrderFormActivity.carttotamount.setText(String.valueOf(OrderFormActivity.cartorderFormDetails.size()));
                OrderFormItemAdapter adapter = new OrderFormItemAdapter(context, OrderFormActivity.cartorderFormDetails);
                orderlistview.setAdapter(adapter);
            } else {
                OrderFormActivity.cartorderFormDetails.clear();
                OrderFormItemAdapter adapter = new OrderFormItemAdapter(context, OrderFormActivity.cartorderFormDetails);
                orderlistview.setAdapter(adapter);
                OrderFormActivity.carttotamount.setText(String.valueOf(OrderFormActivity.cartorderFormDetails.size()));
                Toast toast = Toast.makeText(getApplicationContext(), "Cart is empty", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //Toast.makeText(getApplicationContext(),"Cart is empty",Toast.LENGTH_SHORT).show();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    public  void GetOrderItem(String getorderschedulecode){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetNextdayItemDB("0","0",getorderschedulecode);
            if(Cur.getCount()>0) {
                OrderFormActivity.cartorderFormDetails.clear();
                for(int i=0;i<Cur.getCount();i++){
                    OrderFormActivity.cartorderFormDetails.add(new OrderFormDetails(Cur.getString(0),Cur.getString(1),
                            Cur.getString(2),Cur.getString(3),Cur.getString(4),
                            Cur.getString(5),Cur.getString(6),
                            Cur.getString(7),Cur.getString(8),Cur.getString(9),
                            Cur.getString(11), String.valueOf(i+1),Cur.getString(10),"" ));
                    Cur.moveToNext();
                }

                OrderFormItemAdapter adapter = new OrderFormItemAdapter(context, OrderFormActivity.cartorderFormDetails);
                orderlistview.setAdapter(adapter);
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

    protected  class AsyncOrderDetails extends
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
                    Cursor mCur2 = dbadapter.GetOrderDetailsDatasDB();
                    JSONArray js_array2 = new JSONArray();
                    for (int i = 0; i < mCur2.getCount(); i++) {
                        JSONObject obj = new JSONObject();
                        obj.put("autonum", mCur2.getString(0));
                        obj.put("vancode", mCur2.getString(1));
                        obj.put("schedulecode", mCur2.getString(2));
                        obj.put("itemcode", mCur2.getString(3));
                        obj.put("qty", mCur2.getString(4));
                        obj.put("makerid", mCur2.getString(5));
                        obj.put("createddate", mCur2.getString(6));
                        obj.put("orderdate", mCur2.getString(7));
                        obj.put("flag", mCur2.getString(8));
                        obj.put("status", mCur2.getString(9));
                        js_array2.put(obj);
                        mCur2.moveToNext();
                    }
                    js_obj.put("JSonObject", js_array2);

                    jsonObj =  api.OrderDetails(js_obj.toString(),context);
                    //Call Json parser functionality
                    JSONParser parser = new JSONParser();
                    //parse the json object to boolean
                    List = parser.parseOrderDataList(jsonObj);
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
                            dataBaseAdapter.UpdateOrderDetailsFlag(result.get(0).ScheduleCode[j]);
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

    /**********END Asynchronous Claass***************/
    //Get Order form item details
    public class OrderFormItemAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;

        OrderFormItemAdapter(Context c,ArrayList<OrderFormDetails> myList) {
            context = c;
            layoutInflater = LayoutInflater.from(context);
            orderformlist = myList;
        }

        @Override
        public int getCount() {
            return orderformlist.size();
        }

        @Override
        public OrderFormDetails getItem(int position) {
            return (OrderFormDetails) orderformlist.get(position);
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
                convertView = layoutInflater.inflate(R.layout.orderformcartlist, parent, false);
                mHolder = new ViewHolder1();
                try {
                    mHolder.cartordersno = (TextView) convertView.findViewById(R.id.cartordersno);
                    mHolder.cartorderitemname = (TextView) convertView.findViewById(R.id.cartorderitemname);
                    mHolder.cartorderqty = (EditText) convertView.findViewById(R.id.cartorderqty);
                    mHolder.cartorderclosingstock = (TextView) convertView.findViewById(R.id.cartorderclosingstock);
                    mHolder.cartdelete = (ImageView) convertView.findViewById(R.id.cartdelete);
                    mHolder.card_view = (LinearLayout)convertView.findViewById(R.id.card_view);
                    mHolder.cartorderunit = (TextView)convertView.findViewById(R.id.cartorderunit);
                    mHolder.cartordertotalstock = (TextView)convertView.findViewById(R.id.cartordertotalstock);


                    convertView.setTag(mHolder);
                    convertView.setTag(R.id.cartordersno, mHolder.cartordersno);
                    convertView.setTag(R.id.cartorderitemname, mHolder.cartorderitemname);
                    convertView.setTag(R.id.cartorderqty, mHolder.cartorderqty);
                    convertView.setTag(R.id.cartorderclosingstock, mHolder.cartorderclosingstock);
                    convertView.setTag(R.id.cartdelete, mHolder.cartdelete);
                    convertView.setTag(R.id.cartorderunit, mHolder.cartorderunit);
                    convertView.setTag(R.id.cartordertotalstock, mHolder.cartordertotalstock);
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
            mHolder.cartordersno.setTag(position);
            mHolder.cartorderitemname.setTag(position);
            mHolder.cartorderqty.setTag(position);
            mHolder.cartorderclosingstock.setTag(position);
            mHolder.cartdelete.setTag(position);
            mHolder.cartorderunit.setTag(position);

            try {
                final OrderFormDetails currentListData = getItem(position);
                mHolder.cartordersno.setText(String.valueOf(position+1));
                mHolder.cartorderclosingstock.setText(currentListData.getClosingstk());
                mHolder.cartorderqty.setText(currentListData.getQty());
                mHolder.cartorderunit.setVisibility(View.GONE);
                if (!(currentListData.getItemnametamil().equals(""))
                        && !(currentListData.getItemnametamil()).equals("null")
                        && !((currentListData.getItemnametamil()).equals(null))) {
                    mHolder.cartorderitemname.setText(currentListData.getItemnametamil() + " - " +
                            currentListData.getUnitname() );
                }else{
                    mHolder.cartorderitemname.setText(currentListData.getItemname() + " - " +
                            currentListData.getUnitname());
                }
                mHolder.cartorderitemname.setTextColor(Color.parseColor(currentListData.getColourcode()));
                mHolder.cartorderunit.setText(currentListData.getUnitname());


                if(currentListData.getStatus().equals("deleted")){
                   // mHolder.cartorderqty.setText("0");
                    mHolder.card_view.setBackgroundColor(Color.parseColor("#F8D3D6"));
                }


                mHolder.cartorderqty.addTextChangedListener(new TextWatcher() {
                    public void onTextChanged(CharSequence s, int start, int before,
                                              int count) {
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                  int after) {
                    }

                    public void afterTextChanged(Editable s) {
                        if( !mHolder.cartorderqty.getText().toString().equals("") &&
                                !mHolder.cartorderqty.getText().toString().equals(null) &&
                                !mHolder.cartorderqty.getText().toString().equals("null")){
                            double a = Double.parseDouble(currentListData.getClosingstk());
                            double b = Double.parseDouble(mHolder.cartorderqty.getText().toString());
                            double c=a+b;
                            int getval = (int) c;
                            mHolder.cartordertotalstock.setText(String.valueOf(getval));

                            currentListData.setQty( mHolder.cartorderqty.getText().toString());
                        }else{
                            mHolder.cartordertotalstock.setText("");
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
                if( !mHolder.cartorderqty.getText().toString().equals("") &&
                        !mHolder.cartorderqty.getText().toString().equals(null) &&
                        !mHolder.cartorderqty.getText().toString().equals("null")){
                    double a = Double.parseDouble(currentListData.getClosingstk());
                    double b = Double.parseDouble(mHolder.cartorderqty.getText().toString());
                    double c=a+b;
                    int getval = (int) c;
                    mHolder.cartordertotalstock.setText(String.valueOf(getval));

                    currentListData.setQty( mHolder.cartorderqty.getText().toString());
                }else{
                    mHolder.cartordertotalstock.setText("");
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
                                        OrderFormActivity.cartorderFormDetails.remove(position);
                                        Toast toast = Toast.makeText(getApplicationContext(),"Item removed from cart", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                        //Toast.makeText(getApplicationContext(),"Item removed from cart",Toast.LENGTH_SHORT).show();
                                        SetOrderItemToCart();
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
            private TextView cartordersno,cartorderunit;
            private TextView cartorderitemname;
            private EditText cartorderqty;
            private TextView cartorderclosingstock,cartordertotalstock;
            private ImageView cartdelete;
            LinearLayout card_view;
        }
    }
    public void CalculateWeight() {
        final DecimalFormat dft = new DecimalFormat("0.00");
        double res1 = 0;
        double res2 = 0;
        if(orderformlist.size()>0) {
            for (int i = 0; i < orderformlist.size(); i++) {
                String uppweight = orderformlist.get(i).getUnitweight();
                String getqty = orderformlist.get(i).getQty();
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
        /*Intent i = new Intent(context, OrderFormActivity.class);
        startActivity(i);*/
        OrderFormActivity.GetItem();
        finish();
    }
    @Override
    public void onBackPressed() {
        goBack(null);
    }
}
