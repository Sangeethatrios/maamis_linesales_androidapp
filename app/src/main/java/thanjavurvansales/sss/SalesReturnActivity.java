package thanjavurvansales.sss;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SalesReturnActivity extends AppCompatActivity implements View.OnClickListener{
    private static final long START_TIME_IN_MILLIS = 600000;
    private PopupWindow window;
    FloatingActionButton fabgroupitem;
    private Boolean isFabOpen = false;
    private Animation rotate_forward,rotate_backward;
    Context context;
    boolean isopenpopup;
    ViewGroup view_root;
    private int _xDelta;
    private int _yDelta;
    ImageView salegoback,salelogout;
    float dX;
    float dY;
    int lastAction;
    public DecimalFormat df;

    private ExpandableAdapter expandableAdapter;
    private ExpandableListView expList;

    private List<String> listDataHEader;
    private HashMap<String,List<String>> listhash;

    TextView billisttotalamount,salesreview;
    ToggleButton togglegstin;
    static RadioButton radio_cash,radio_credit;

    ListView lv_sales_items,lv_AreaList,lv_CustomerList,lv_subgroup;

    TextView txtroutename;
    public  static TextView txtcustomername,txtareaname,totalcartitems,txtsalesdate;
    String[] AreaCode,AreaName,AreaNameTamil,NoOfKm,CityCode,CityName,CustomerCount;
    String[] SubGroupCode,SubGroupName,SubGroupNameTamil;
    String[] CustomerCode,CustomerName,CustomerNameTamil,Address,CustomerAreaCode,MobileNo,TelephoneNo,GSTN,SchemeApplicable,customertypecode;
    Dialog areadialog,customerdialog;
    static public String  customercode="",gstnnumber="",
            getschemeapplicable="",getstaticsubcode="" ;
    ArrayList<SalesItemDetails> salesitems = new ArrayList<SalesItemDetails>();
    ArrayList<SalesItemDetails> freeitems = new ArrayList<SalesItemDetails>();
    static ArrayList<SalesItemDetails> staticreviewsalesitems = new ArrayList<SalesItemDetails>();
    Dialog stockconvertdialog;
    public static boolean paymenttype;
    FloatingActionButton movecart;
    public static PreferenceMangr preferenceMangr=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_return);

        context =this;

        //Declare All ListView,TextView,Edit Text ,Fab Buttons
        fabgroupitem = (FloatingActionButton)findViewById(R.id.fabgroupitem);
        lv_sales_items = (ListView)findViewById(R.id.lv_sales_items) ;
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
        billisttotalamount  = (TextView)findViewById(R.id.billisttotalamount);
        view_root = (ViewGroup)findViewById(R.id.view_root);
        togglegstin = (ToggleButton)findViewById(R.id.togglegstin);
        salesreview = (TextView)findViewById(R.id.salesreview);
        salegoback = (ImageView)findViewById(R.id.salegoback);
        salelogout = (ImageView)findViewById(R.id.salelogout);
        txtareaname = (TextView)findViewById(R.id.txtareaname);
        txtcustomername = (TextView)findViewById(R.id.txtcustomername);
        txtroutename = (TextView)findViewById(R.id.txtroutename);
        txtsalesdate = (TextView)findViewById(R.id.txtsalesdate);
        radio_cash = (RadioButton)findViewById(R.id.radio_cash);
        radio_credit = (RadioButton)findViewById(R.id.radio_credit);
        totalcartitems = (TextView)findViewById(R.id.totalcartitems);

        try {
            preferenceMangr = new PreferenceMangr(context);
        }catch (Exception e){
            Log.d("Preference Manager : ",e.toString());
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
        //final View dragView = findViewById(R.id.movecart);
        //dragView.setOnTouchListener(this);
        movecart=(FloatingActionButton) findViewById(R.id.movecart);

        //Set values for corresponding values
        txtareaname.setText(LoginActivity.getareaname);
        //Set total cart items
        totalcartitems.setText("0");
        //salesfabclickoption
        fabgroupitem.setOnClickListener(this);

        //Set total cart item value
        totalcartitems.setText(String.valueOf(staticreviewsalesitems.size()));

        CheckToggleButton();
        togglegstin.setBackgroundColor(getResources().getColor(R.color.graycolor));
        togglegstin.setTextOn("GSTIN \n "+gstnnumber);

        togglegstin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                /*if(radio_cash.isChecked()){
                    CheckToggleButton();
                }*/
            }
        });

        //if check credit automatic gstin button as green
        radio_credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gstnnumber.equals("")||gstnnumber.equals(null)||gstnnumber.equals("0")) {
                    togglegstin.setTextOff("GSTIN \n "+gstnnumber);
                    togglegstin.setBackgroundColor(getResources().getColor(R.color.graycolor));
                }else{
                    togglegstin.setTextOn("GSTIN \n " + gstnnumber);
                    togglegstin.setBackgroundColor(getResources().getColor(R.color.green));
                }
            }
        });

        //open Review Screen
        salesreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(salesitems.size() > 0) {
                    for (int i = 0; i < lv_sales_items.getChildCount(); i++) {
                        View listRow = lv_sales_items.getChildAt(i);
                        EditText getlistqty = (EditText) listRow.findViewById(R.id.listitemqty);
                        TextView getlisttotal = (TextView) listRow.findViewById(R.id.listitemtotal);
                        String getitemlistqty = getlistqty.getText().toString();
                        String getitemlisttotal = getlisttotal.getText().toString();
                        if (!getitemlistqty.equals("") && !getitemlistqty.equals(null)) {
                            if (Double.parseDouble(getitemlistqty) > 0 && Double.parseDouble(getitemlisttotal) <= 0.0) {
                                getlistqty.requestFocus();
                                Toast toast = Toast.makeText(getApplicationContext(), "Please enter valid total amount", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                return;
                            }
                        }
                    }
                }
                if(staticreviewsalesitems.size() > 0 ){
                    salesitems.clear();
                    SalesItemAdapter adapter = new SalesItemAdapter(context,salesitems);
                    lv_sales_items.setAdapter(adapter);
                    Intent i = new Intent(context, ReturnReviewActivity.class);
                    startActivity(i);
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(), "No items added to cart", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    //Toast.makeText(getApplicationContext(),"No items added to cart",Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
/*

        //open Review Screen
        movecart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean addedcart = false;

                for (int i = 0; i < salesitems.size(); i++) {
                    if(!salesitems.get(i).getItemqty().equals("")
                            && ! salesitems.get(i).getItemqty().equals(null) && ! salesitems.get(i).getNewprice().equals(null)
                            && ! salesitems.get(i).getNewprice().equals("") && ! salesitems.get(i).getSubtotal().equals("")){
                        if(Double.parseDouble(salesitems.get(i).getItemqty()) > 0
                                && Double.parseDouble(salesitems.get(i).getNewprice()) > 0 &&
                                Double.parseDouble(salesitems.get(i).getSubtotal()) > 0){

                            for (int j=0;j<staticreviewsalesitems.size();j++){
                                if(salesitems.get(i).getItemcode().equals
                                        (staticreviewsalesitems.get(j).getItemcode()) &&
                                        !(staticreviewsalesitems.get(j).getFreeflag().equals("freeitem"))){
                                    staticreviewsalesitems.remove(j);
                                }
                            }


                            staticreviewsalesitems.add(new SalesItemDetails(salesitems.get(i).getItemcode(),salesitems.get(i).getCompanycode(),
                                    salesitems.get(i).getBrandcode(),salesitems.get(i).getManualitemcode()
                                    ,salesitems.get(i).getItemname(),salesitems.get(i).getItemnametamil(),
                                    salesitems.get(i).getUnitcode(),salesitems.get(i).getUnitweightunitcode()
                                    ,salesitems.get(i).getUnitweight(),salesitems.get(i).getUppunitcode()
                                    ,salesitems.get(i).getUppweight(),salesitems.get(i).getItemcategory(),salesitems.get(i).getParentitemcode(),
                                    salesitems.get(i).getAllowpriceedit(), salesitems.get(i).getAllownegativestock()
                                    ,salesitems.get(i).getAllowdiscount(),
                                    salesitems.get(i).getStockqty(),salesitems.get(i).getUnitname(),
                                    salesitems.get(i).getNoofdecimals(),salesitems.get(i).getOldprice(),
                                    salesitems.get(i).getNewprice(),salesitems.get(i).getColourcode(),salesitems.get(i).getHsn(),
                                    salesitems.get(i).getTax(),salesitems.get(i).getItemqty(),salesitems.get(i).getSubtotal()
                                    ,salesitems.get(i).getRouteallowpricedit(),salesitems.get(i).getDiscount(),"",
                                    salesitems.get(i).getPurchaseitemcode(),salesitems.get(i).getFreeitemcode(),salesitems.get(i).getNewprice(),"",""));
                            addedcart = true;
                        }
                    }

                }


                if(addedcart){
                    Toast toast = Toast.makeText(getApplicationContext(), "Item added to cart", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    //Toast.makeText(getApplicationContext(),"Successfully added to cart",Toast.LENGTH_SHORT).show();

                }else{
                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter any quantity", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                   // Toast.makeText(getApplicationContext(),"Please enter any qty",Toast.LENGTH_SHORT).show();
                }

                totalcartitems.setText(String.valueOf(staticreviewsalesitems.size()));

            }
        });

*/
        //Logout process
        salelogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // HomeActivity.logoutprocess = "True";
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirmation");
                builder.setIcon(R.mipmap.ic_vanlauncher);
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
        //Goback process
        salegoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack(null);
            }
        });

        /*******FILTER FUNCTIONALITY********/
        txtareaname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetArea(preferenceMangr.pref_getString("getroutecode"));
            }
        });
        txtcustomername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetCustomer(LoginActivity.getareacode);
            }
        });


        /*******END FILTER FUNCTIONALITY******/
        //Set Routename and current date
        txtroutename.setText(preferenceMangr.pref_getString("getroutename"));
        txtsalesdate.setText(preferenceMangr.pref_getString("getcurrentdatetime"));



    }


    /*******FILTER FUNCTIONALITY********/
    public  void GetArea(String routecode){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetAreaDB(routecode,Constants.CUSTOMER_CATEGORY_BOTH);
            if(Cur.getCount()>0) {
                AreaCode = new String[Cur.getCount()];
                AreaName = new String[Cur.getCount()];
                AreaNameTamil = new String[Cur.getCount()];
                NoOfKm = new String[Cur.getCount()];
                CityCode = new String[Cur.getCount()];
                CityName = new String[Cur.getCount()];
                CustomerCount = new String[Cur.getCount()];
                for(int i=0;i<Cur.getCount();i++){
                    AreaCode[i] = Cur.getString(0);
                    AreaName[i] = Cur.getString(1);
                    AreaNameTamil[i] = Cur.getString(2);
                    NoOfKm[i] = Cur.getString(3);
                    CityCode[i] = Cur.getString(4);
                    CityName[i] = Cur.getString(5);
                    CustomerCount[i] = Cur.getString(6);
                    Cur.moveToNext();
                }

                areadialog = new Dialog(context);
                areadialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                areadialog.setContentView(R.layout.areapopup);
                lv_AreaList = (ListView) areadialog.findViewById(R.id.lv_AreaList);
                ImageView close = (ImageView) areadialog.findViewById(R.id.close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        areadialog.dismiss();
                    }
                });
                SalesAreaAdapter adapter = new SalesAreaAdapter(context);
                lv_AreaList.setAdapter(adapter);
                areadialog.show();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(), "No Area Available", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //Toast.makeText(getApplicationContext(),"No Area Available",Toast.LENGTH_SHORT).show();
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

    /************BASE ADAPTER*************/
    //Area Adapter
    public class SalesAreaAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;

        SalesAreaAdapter(Context c) {
            context = c;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return AreaCode.length;
        }

        @Override
        public Object getItem(int position) {
            return AreaCode[position];
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

            ViewHolder mHolder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.areapopuplist, parent, false);
                mHolder = new ViewHolder();
                try {
                    mHolder.listareaname = (TextView) convertView.findViewById(R.id.listareaname);
                    mHolder.listcustomercount = (TextView)convertView.findViewById(R.id.listcustomercount);
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
                mHolder = (ViewHolder) convertView.getTag();
            }
            try {
                mHolder.listareaname.setText(String.valueOf(AreaName[position] +" - "+CityName[position]));

                mHolder.listcustomercount.setText(String.valueOf(CustomerCount[position]));


                int getcount = 0;
                for(int i = 0;i<AreaCode.length;i++){
                    if(!AreaCode[i].equals("0")){
                        getcount = getcount + Integer.parseInt(CustomerCount[i]);
                    }
                }
            } catch (Exception e) {
                Log.i("Route value", e.toString());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtareaname.setText(String.valueOf(AreaName[position] +" - "+CityName[position]));
                    LoginActivity.getareaname = String.valueOf(AreaName[position] +" - "+CityName[position]);
                    LoginActivity.getareacode = String.valueOf(AreaCode[position]);
                    txtcustomername.setText("");
                    customercode = "0";
                    gstnnumber = "";
                    getschemeapplicable = "";
                    togglegstin.setBackgroundColor(getResources().getColor(R.color.graycolor));
                    togglegstin.setText("GSTIN \n ");
                    togglegstin.setTextOn("GSTIN \n ");
                    togglegstin.setTextOff("GSTIN \n ");
                    txtcustomername.setHint("Customer Name");

                    if(staticreviewsalesitems.size() > 0 ){
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Confirmation");
                        builder.setMessage("Are you sure you want to clear cart?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        staticreviewsalesitems.clear();
                                        //Set total cart item value
                                        totalcartitems.setText(String.valueOf(staticreviewsalesitems.size()));
                                        salesitems.clear();

                                        SalesItemAdapter adapter = new SalesItemAdapter(context,salesitems);
                                        lv_sales_items.setAdapter(adapter);
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
                        salesitems.clear();
                        SalesItemAdapter adapter = new SalesItemAdapter(context,salesitems);
                        lv_sales_items.setAdapter(adapter);
                    }
                    areadialog.dismiss();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView listareaname,listcustomercount;

        }

    }

    // Customer Dropdown
    public  void GetCustomer(String areacode){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetCustomerDB(areacode,Constants.CUSTOMER_CATEGORY_BOTH);
            if(Cur.getCount()>0) {
                CustomerCode = new String[Cur.getCount()];
                CustomerName = new String[Cur.getCount()];
                CustomerNameTamil = new String[Cur.getCount()];
                Address = new String[Cur.getCount()];
                CustomerAreaCode = new String[Cur.getCount()];
                MobileNo = new String[Cur.getCount()];
                TelephoneNo = new String[Cur.getCount()];
                GSTN = new String[Cur.getCount()];
                SchemeApplicable = new String[Cur.getCount()];
                customertypecode = new String[Cur.getCount()];

                for(int i=0;i<Cur.getCount();i++){
                    CustomerCode[i] = Cur.getString(0);
                    CustomerName[i] = Cur.getString(1);
                    CustomerNameTamil[i] = Cur.getString(2);
                    Address[i] = Cur.getString(3);
                    CustomerAreaCode[i] = Cur.getString(4);
                    MobileNo[i] = Cur.getString(6);
                    TelephoneNo[i] = Cur.getString(7);
                    GSTN[i] = Cur.getString(9);
                    SchemeApplicable[i] = Cur.getString(10);
                    customertypecode[i] = Cur.getString(11);
                    Cur.moveToNext();
                }

                customerdialog = new Dialog(context);
                customerdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                customerdialog.setContentView(R.layout.customerpopup);
                lv_CustomerList = (ListView) customerdialog.findViewById(R.id.lv_CustomerList);
                ImageView close = (ImageView) customerdialog.findViewById(R.id.close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customerdialog.dismiss();
                    }
                });
                SalesCustomerAdapter adapter = new SalesCustomerAdapter(context);
                lv_CustomerList.setAdapter(adapter);
                customerdialog.show();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(), "No Customer Available", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
               // Toast.makeText(getApplicationContext(),"No Customer Available",Toast.LENGTH_SHORT).show();
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

    //Customer Adapter
    public class SalesCustomerAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;

        SalesCustomerAdapter(Context c) {
            context = c;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return CustomerCode.length;
        }

        @Override
        public Object getItem(int position) {
            return CustomerCode[position];
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

            ViewHolder mHolder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.customerpopuplist, parent, false);

                mHolder = new ViewHolder();
                try {
                    mHolder.listcustomername = (TextView) convertView.findViewById(R.id.listcustomername);
                    mHolder.listgstin = (TextView)convertView.findViewById(R.id.listgstin);
                    mHolder.listcustomertype = (TextView)convertView.findViewById(R.id.listcustomertype);
                } catch (Exception e) {
                    Log.i("Customer", e.toString());
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
                mHolder.listcustomername.setText(String.valueOf(CustomerNameTamil[position]));

                if(GSTN[position].equals("") || GSTN[position].equals("null") ||GSTN[position].equals(null)){
                    mHolder.listgstin.setVisibility(View.GONE);
                }else{
                    mHolder.listgstin.setVisibility(View.VISIBLE);
                }

                if(customertypecode[position].equals("1")){
                    mHolder.listcustomertype.setText("CA");
                    mHolder.listcustomertype.setTextColor(getResources().getColor(R.color.black));
                }else if(customertypecode[position].equals("2")){
                    mHolder.listcustomertype.setText("CR");
                    mHolder.listcustomertype.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                }
            } catch (Exception e) {
                Log.i("Customer", e.toString());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtcustomername.setText(String.valueOf(CustomerNameTamil[position]));
                    customercode = CustomerCode[position];
                    gstnnumber = GSTN[position];
                    getschemeapplicable = SchemeApplicable[position];
                    if(gstnnumber.equals("")||gstnnumber.equals(null)||gstnnumber.equals("0")){
                        togglegstin.setBackgroundColor(getResources().getColor(R.color.graycolor));
                    }else{
                        togglegstin.setBackgroundColor(getResources().getColor(R.color.green));
                    }
                    togglegstin.setText("GSTIN \n "+gstnnumber);
                    togglegstin.setTextOn("GSTIN \n "+gstnnumber);
                    togglegstin.setTextOff("GSTIN \n "+gstnnumber);
                    if(staticreviewsalesitems.size() > 0 ){
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Confirmation");
                        builder.setMessage("Are you sure you want to clear cart?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        staticreviewsalesitems.clear();
                                        //Set total cart item value
                                        totalcartitems.setText(String.valueOf(staticreviewsalesitems.size()));
                                        salesitems.clear();

                                        SalesItemAdapter adapter = new SalesItemAdapter(context,salesitems);
                                        lv_sales_items.setAdapter(adapter);
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
                        salesitems.clear();
                        SalesItemAdapter adapter = new SalesItemAdapter(context,salesitems);
                        lv_sales_items.setAdapter(adapter);
                    }
                    customerdialog.dismiss();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView listcustomername,listgstin,listcustomertype;

        }

    }

    /********POPUP SUB GROUP FUNCTIONALITY********/


    public  void GetSubGroupList(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetSubGroupDBSalesReturn();
            if(Cur.getCount()>0) {
                SubGroupCode = new String[Cur.getCount()];
                SubGroupName = new String[Cur.getCount()];
                SubGroupNameTamil = new String[Cur.getCount()];
                for(int i=0;i<Cur.getCount();i++){
                    SubGroupCode[i] = Cur.getString(0);
                    SubGroupName[i] = Cur.getString(1);
                    SubGroupNameTamil[i] = Cur.getString(2);
                    Cur.moveToNext();
                }

                SalesSubGroupAdapter adapter = new SalesSubGroupAdapter(context);
                lv_subgroup.setAdapter(adapter);
            }else{
                Toast toast = Toast.makeText(getApplicationContext(), "No Subgroup Available", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //Toast.makeText(getApplicationContext(),"No Subgroup Available",Toast.LENGTH_SHORT).show();
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


    //Subgroup Adapter
    public class SalesSubGroupAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;
        SalesSubGroupAdapter(Context c) {
            context = c;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return SubGroupCode.length;
        }

        @Override
        public Object getItem(int position) {
            return SubGroupCode[position];
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

            ViewHolder mHolder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.subgrouppopuplist, parent, false);
                mHolder = new ViewHolder();
                try {
                    mHolder.listsubgroup = (TextView) convertView.findViewById(R.id.listsubgroup);
                } catch (Exception e) {
                    Log.i("Customer", e.toString());
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
                mHolder.listsubgroup.setText(String.valueOf(SubGroupNameTamil[position]));
            } catch (Exception e) {
                Log.i("Customer", e.toString());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getstaticsubcode = SubGroupCode[position];
                    GetItems(SubGroupCode[position]);
                    window.dismiss();
                    fabgroupitem.startAnimation(rotate_backward);
                    isFabOpen = false;
                    isopenpopup = false;
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView listsubgroup;

        }

    }

    // get Items
    public  void GetItems(String itemsubgroupcode){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            lv_sales_items.setAdapter(null);
            Cur = objdatabaseadapter.GetItemsDBSalesReturn(itemsubgroupcode,preferenceMangr.pref_getString("getroutecode"),LoginActivity.getareacode);
            salesitems.clear();
            if(Cur.getCount()>0) {
                for(int i=0;i<Cur.getCount();i++){
                    salesitems.add(new SalesItemDetails(Cur.getString(0),Cur.getString(1),
                            Cur.getString(2),Cur.getString(3),Cur.getString(4),
                            Cur.getString(5),Cur.getString(6),Cur.getString(7),
                            Cur.getString(8),Cur.getString(9),Cur.getString(10),
                            Cur.getString(11),Cur.getString(12),Cur.getString(13),
                            Cur.getString(14),Cur.getString(15),Cur.getString(16),
                            Cur.getString(17),Cur.getString(18),Cur.getString(19),
                            Cur.getString(20),Cur.getString(21),Cur.getString(22),
                            Cur.getString(23),"","",Cur.getString(24),
                            "","","","",Cur.getString(20),"",
                            "",Cur.getString(29),"","","",""));
                    Cur.moveToNext();
                }
                SalesItemAdapter adapter = new SalesItemAdapter(context,salesitems);
                lv_sales_items.setAdapter(adapter);

                //Set ITEMQTY PRICE AND TOTAL
                for (int i = 0; i < staticreviewsalesitems.size(); i++) {
                    for (int j = 0; j < salesitems.size();j++) {
                        if (staticreviewsalesitems.get(i).getItemcode().equals(salesitems.get(j).getItemcode()) &&
                                !(staticreviewsalesitems.get(i).getFreeflag().equals("freeitem"))) {
                            salesitems.get(j).setItemqty(staticreviewsalesitems.get(i).getItemqty());
                            salesitems.get(j).setNewprice(staticreviewsalesitems.get(i).getNewprice());
                            salesitems.get(j).setSubtotal(staticreviewsalesitems.get(i).getSubtotal());
                        }
                    }
                }

            }else{
                Toast toast = Toast.makeText(getApplicationContext(), "No Items Available", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //Toast.makeText(getApplicationContext(),"No Items Available",Toast.LENGTH_SHORT).show();
            }
        }  catch (Exception e){
            Log.i("Items", e.toString());
        }
        finally {
            // this gets called even if there is an exception somewhere above
            if(objdatabaseadapter != null)
                objdatabaseadapter.close();
            if(Cur != null)
                Cur.close();
        }
    }



    public void CheckToggleButton(){
        if (togglegstin.isChecked()) {
            paymenttype = false;
            if(gstnnumber.equals("")||gstnnumber.equals(null)||gstnnumber.equals("0")) {
                togglegstin.setTextOff("GSTIN \n "+gstnnumber);
                togglegstin.setBackgroundColor(getResources().getColor(R.color.graycolor));
            }else{
                togglegstin.setTextOn("GSTIN \n " + gstnnumber);
                togglegstin.setBackgroundColor(getResources().getColor(R.color.green));
            }
        }
        else{
            paymenttype = true;
            if(gstnnumber.equals("")||gstnnumber.equals(null)||gstnnumber.equals("0")) {
                togglegstin.setTextOff("GSTIN \n "+gstnnumber);
                togglegstin.setBackgroundColor(getResources().getColor(R.color.graycolor));
            }else{
                togglegstin.setTextOn("GSTIN \n " + gstnnumber);
                togglegstin.setBackgroundColor(getResources().getColor(R.color.green));
            }
        }
    }
    private void ShowPopupWindow(){
        try {
            //Get phone imei number
            DataBaseAdapter objdatabaseadapter = null;
            String drilldownitem="subgroup";
            try{
                objdatabaseadapter = new DataBaseAdapter(context);
                objdatabaseadapter.open();
                drilldownitem = objdatabaseadapter.GetDrildownGroupStatusDB();

            }  catch (Exception e){
                Log.i("GetDrildown", e.toString());
            }
            finally {
                // this gets called even if there is an exception somewhere above
                if(objdatabaseadapter != null)
                    objdatabaseadapter.close();
            }
            if(drilldownitem.equals("group")) {
                LayoutInflater inflater = (LayoutInflater) SalesReturnActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.grouppopup, null);
                window = new PopupWindow(layout, 650, 1000, false);

                expList = (ExpandableListView) layout.findViewById(R.id.expandible_listview);
                ImageView close = (ImageView) layout.findViewById(R.id.close);

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        window.dismiss();
                        fabgroupitem.startAnimation(rotate_backward);
                        isFabOpen = false;
                        isopenpopup = false;
                    }
                });
                //window.setOutsideTouchable(true);
                window.showAtLocation(layout, Gravity.BOTTOM, 0, 140);
                //setUpAdapter();
                window.setOutsideTouchable(false);
                isopenpopup = true;
                setChildItems();

                expandableAdapter = new ExpandableAdapter(this, listDataHEader, listhash);
                expList.setAdapter(expandableAdapter);

                expList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                    int previousGroup = -1;

                    @Override
                    public void onGroupExpand(int groupPosition) {
                        if (groupPosition != previousGroup)
                            expList.collapseGroup(previousGroup);
                        previousGroup = groupPosition;
                    }
                });
            }else{
                LayoutInflater inflater = (LayoutInflater) SalesReturnActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.subgrouppopup, null);
                window = new PopupWindow(layout, 650, 1000, false);

                lv_subgroup = (ListView) layout.findViewById(R.id.lv_subgroup);
                ImageView close = (ImageView) layout.findViewById(R.id.close);

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        window.dismiss();
                        fabgroupitem.startAnimation(rotate_backward);
                        isFabOpen = false;
                        isopenpopup = false;
                    }
                });
                //window.setOutsideTouchable(true);
                window.showAtLocation(layout, Gravity.BOTTOM, 0, 140);
                //setUpAdapter();
                window.setOutsideTouchable(false);
                isopenpopup = true;

                //Call Sub group list
                GetSubGroupList();

            }

        }catch (Exception e){

        }
    }

    private  void setChildItems(){
        listDataHEader = new ArrayList<>();
        listhash = new HashMap<>();

        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        Cursor Cur1=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetGroupDBSalesReturn();
            if(Cur.getCount()>0) {
                for(int i=0;i<Cur.getCount();i++){
                    listDataHEader.add(Cur.getString(2));
                    Cur1 = objdatabaseadapter.GetSubGroup_GroupDBSalesReturn(Cur.getString(0));
                    List<String> subgrouplist = new ArrayList<>();
                    List<String> subgrouplistcode = new ArrayList<>();
                    for(int j=0;j<Cur1.getCount();j++){
                        subgrouplist.add(Cur1.getString(2) + "-" +Cur1.getString(0));
                        Cur1.moveToNext();
                    }
                    listhash.put(listDataHEader.get(i),subgrouplist);
                    Cur.moveToNext();
                }

            }else{
                Toast toast = Toast.makeText(getApplicationContext(), "No Items Available", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //Toast.makeText(getApplicationContext(),"No Group Available",Toast.LENGTH_SHORT).show();

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
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.fabgroupitem:
                animateFAB();
                break;
        }
    }
    public void animateFAB(){
        if(txtcustomername.getText().toString().equals("") || txtcustomername.getText().toString().equals(null) ||
                customercode.equals("") ){
            Toast toast = Toast.makeText(getApplicationContext(), "Please select customer", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            //Toast.makeText(getApplicationContext(),"Please select customer",Toast.LENGTH_SHORT).show();
            return;
        }else {
            if(salesitems.size() > 0) {
                for (int i = 0; i < lv_sales_items.getChildCount(); i++) {
                    View listRow = lv_sales_items.getChildAt(i);
                    EditText getlistqty = (EditText) listRow.findViewById(R.id.listitemqty);
                    TextView getlisttotal = (TextView) listRow.findViewById(R.id.listitemtotal);
                    String getitemlistqty = getlistqty.getText().toString();
                    String getitemlisttotal = getlisttotal.getText().toString();
                    if (!getitemlistqty.equals("") && !getitemlistqty.equals(null)) {
                        if (Double.parseDouble(getitemlistqty) > 0 && Double.parseDouble(getitemlisttotal) <= 0.0) {
                            getlistqty.requestFocus();
                            Toast toast = Toast.makeText(getApplicationContext(), "Please enter valid total amount", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return;
                        }
                    }
                }
            }
            if (isopenpopup) {
                window.dismiss();
                isFabOpen = true;
                isopenpopup = false;
            } else {
                ShowPopupWindow();
            }
            if (isFabOpen) {
                fabgroupitem.startAnimation(rotate_backward);
                isFabOpen = false;
                Log.d("Fab", "close");
            } else {
                fabgroupitem.startAnimation(rotate_forward);
                isFabOpen = true;
                Log.d("Fab", "open");
            }
        }
    }


    //EXPENDABLE ADAPTER

    public class ExpandableAdapter extends BaseExpandableListAdapter {

        Context ctx;
        private List<String> listDataheader;
        private HashMap<String,List<String>> listHashMap;
        SalesReturnActivity objsales = new SalesReturnActivity();
        //public static ArrayList<ArrayList<String>> childList;
        //private String[] parents;
        public ExpandableAdapter(Context ctx, List<String> listDataheader, HashMap<String, List<String>> listHashMap) {
            this.ctx = ctx;
            this.listDataheader = listDataheader;
            this.listHashMap = listHashMap;
        }

        @Override
        public int getGroupCount() {
            return listDataheader.size();
        }

        @Override
        public int getChildrenCount(int i) {
            return listHashMap.get(listDataheader.get(i)).size();
        }

        @Override
        public Object getGroup(int i) {
            return listDataheader.get(i);
        }

        @Override
        public Object getChild(int i, int j) {
            return listHashMap.get(listDataheader.get(i)).get(j);
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int j) {
            return j;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            String headertitle = (String)getGroup(i);
            if(view == null){
                LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.parent_layout, null);

            }

            TextView parent_textvew = (TextView) view.findViewById(R.id.parent_txt);
            parent_textvew.setText(headertitle);
            return  view;
        }

        @Override
        public View getChildView(int i, int j, boolean b, View view, ViewGroup viewGroup) {
            String getsubgroupcode="";
            final String childtext = (String)getChild(i,j);
            if(view == null){
                LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.child_layout, null);
            }

            TextView child_textvew = (TextView) view.findViewById(R.id.child_txt);
            getsubgroupcode =  childtext.split("-")[1];
            child_textvew.setText(childtext.split("-")[0]);

            final String finalGetsubgroupcode = getsubgroupcode;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getstaticsubcode = finalGetsubgroupcode;
                    GetItems(finalGetsubgroupcode);
                    window.dismiss();
                    fabgroupitem.startAnimation(rotate_backward);
                    isFabOpen = false;
                    isopenpopup = false;
                }
            });
            return  view;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }


    public class SalesItemAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;
        ArrayList<SalesItemDetails> salesItemList;

        SalesItemAdapter(Context c,ArrayList<SalesItemDetails> myList) {
            this.salesItemList = myList;
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
        //@SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder1 mHolder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.salesreturnitemlist, parent, false);
                mHolder = new ViewHolder1();
                try {
                    mHolder.listitemname = (TextView) convertView.findViewById(R.id.listitemname);
                    mHolder.listitemcode = (TextView) convertView.findViewById(R.id.listitemcode);
                    mHolder.listitemqty = (EditText) convertView.findViewById(R.id.listitemqty);
                    mHolder.listitemrate = (EditText) convertView.findViewById(R.id.listitemrate);
                    mHolder.listitemtotal = (TextView) convertView.findViewById(R.id.listitemtotal);
                    mHolder.listitemtax = (TextView) convertView.findViewById(R.id.listitemtax);
                    mHolder.labelhsntax = (TextView)convertView.findViewById(R.id.labelhsntax);
                    mHolder.itemLL = (LinearLayout)convertView.findViewById(R.id.itemLL);
                    mHolder.stockvalueLL = (LinearLayout)convertView.findViewById(R.id.stockvalueLL);
                    mHolder.pricearrow = (ImageView)convertView.findViewById(R.id.pricearrow);


                    convertView.setTag(mHolder);
                    convertView.setTag(R.id.listitemname, mHolder.listitemname);
                    convertView.setTag(R.id.listitemcode, mHolder.listitemcode);
                    convertView.setTag(R.id.listitemqty, mHolder.listitemqty);
                    convertView.setTag(R.id.listitemrate, mHolder.listitemrate);
                    convertView.setTag(R.id.listitemtotal, mHolder.listitemtotal);
                    convertView.setTag(R.id.listitemtax, mHolder.listitemtax);
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
            mHolder.listitemtax.setTag(position);
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

                df = new DecimalFormat("0.00");

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
                        mHolder.listitemrate.setText(df.format(Double.parseDouble(salesItemList.get(position).getNewprice())));
                    }else{
                        mHolder.listitemrate.setText(String.valueOf(Double.parseDouble(salesItemList.get(position).getNewprice())));
                    }
                }else{
                    if(!getnoofdigits.equals("")) {
                        mHolder.listitemrate.setText(df.format(Double.parseDouble("0")));
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



            } catch (Exception e) {
                Log.i("Item value", e.toString());
            }

            //Set Quantity,price
            if (!salesItemList.get(position).getItemqty().equals("")
                    && !salesItemList.get(position).getItemqty().equals(null)
                    && !salesItemList.get(position).getItemqty().equals("0") && !salesItemList.get(position).getSubtotal().equals("")
                    && !salesItemList.get(position).getSubtotal().equals(null)
                    && !salesItemList.get(position).getSubtotal().equals("0")
                    &&  !salesItemList.get(position).getSubtotal().equals("0.0")
                    &&  Double.parseDouble(salesItemList.get(position).getSubtotal())>0) {
                DecimalFormat dft = new DecimalFormat("0.00");
                final int pos = (Integer) mHolder.listitemqty.getTag();
                //do your work here
                Double a = Double.parseDouble(salesItemList.get(position).getItemqty());
                Double b = Double.parseDouble(salesItemList.get(position).getNewprice());
                Double res = a * b;

                mHolder.listitemtotal.setText(dft.format(res));
                //Set Array value for subtotal
                salesItemList.get(pos).setSubtotal(mHolder.listitemtotal.getText().toString());
                salesItemList.get(pos).setNewprice(dft.format(Double.parseDouble(mHolder.listitemrate.getText().toString())));
                mHolder.listitemrate.setText(String.valueOf(salesItemList.get(position).getNewprice()));
                mHolder.listitemqty.setText(String.valueOf(salesItemList.get(position).getItemqty()));
                mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimary));
            }


           //ONCHANGE QUANTITY EVENT
            mHolder.listitemqty.addTextChangedListener(new TextWatcher() {
                public void onTextChanged(CharSequence s, int start, int before,
                                          int count) {
                }
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                }
                public void afterTextChanged(Editable s) {
                    if (!(mHolder.listitemqty.getText().toString()).equals("") &&
                            !(mHolder.listitemqty.getText().toString()).equals(" ")
                            && !(mHolder.listitemqty.getText().toString()).equals("0")
                            && !(mHolder.listitemqty.getText().toString()).equals("0.0")
                            && !(mHolder.listitemqty.getText().toString()).equals(null)) {
                        final int pos1 = (Integer) mHolder.listitemqty.getTag();

                        salesItemList.get(pos1).setItemqty(mHolder.listitemqty.getText().toString());

                    } else {
                        final int pos1 = (Integer) mHolder.listitemqty.getTag();
                        mHolder.listitemtotal.setText("0.0");
                        salesItemList.get(pos1).setItemqty("0");
                        //Set Array value for subtotal
                        salesItemList.get(pos1).setSubtotal("0.0");


                        for (int j=0;j<staticreviewsalesitems.size();j++){
                            if(salesitems.get(pos1).getItemcode().equals
                                    (staticreviewsalesitems.get(j).getItemcode()) &&
                                    !(staticreviewsalesitems.get(j).getFreeflag().equals("freeitem"))){
                                staticreviewsalesitems.remove(j);
                            }
                        }
                        totalcartitems.setText(String.valueOf(staticreviewsalesitems.size()));

                        mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
                    }
                }
            });


          //Onclick of total

          mHolder.listitemtotal.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  if (!(mHolder.listitemqty.getText().toString()).equals("") &&
                          !(mHolder.listitemqty.getText().toString()).equals(" ")
                          && !(mHolder.listitemqty.getText().toString()).equals("0")
                          && !(mHolder.listitemqty.getText().toString()).equals("0.0")
                          && !(mHolder.listitemqty.getText().toString()).equals(null) &&
                          !(mHolder.listitemrate.getText().toString()).equals("") &&
                          !(mHolder.listitemrate.getText().toString()).equals(" ")
                          && !(mHolder.listitemrate.getText().toString()).equals("0")
                          && !(mHolder.listitemrate.getText().toString()).equals("0.0")
                          && !(mHolder.listitemrate.getText().toString()).equals(null)) {

                      DecimalFormat dft = new DecimalFormat("0.00");
                      final int pos = (Integer) mHolder.listitemqty.getTag();
                      if (!mHolder.listitemqty.getText().toString().equals("")) {

                          mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimary));

                          String getdecimalvalue = salesItemList.get(pos).getNoofdecimals();
                          String getnoofdigits = "0";
                          if (getdecimalvalue.equals("0")) {
                              getnoofdigits = "";
                          }
                          if (getdecimalvalue.equals("1")) {
                              getnoofdigits = "0";
                          }
                          if (getdecimalvalue.equals("2")) {
                              getnoofdigits = "00";
                          }
                          if (getdecimalvalue.equals("3")) {
                              getnoofdigits = "000";
                          }

                          df = new DecimalFormat("0.'" + getnoofdigits + "'");


                          final String varQty = mHolder.listitemqty.getText().toString();
                          if(getnoofdigits.equals("")){
                              salesItemList.get(pos).setItemqty(varQty);
                          }else{
                              salesItemList.get(pos).setItemqty(df.format(Double.parseDouble(varQty)));
                          }


                          //do your work here
                          Double a = Double.parseDouble(mHolder.listitemqty.getText().toString());
                          Double b = Double.parseDouble(mHolder.listitemrate.getText().toString());
                          Double res = a * b;

                          mHolder.listitemtotal.setText(dft.format(res));
                          //Set Array value for subtotal
                          salesItemList.get(pos).setSubtotal(mHolder.listitemtotal.getText().toString());
                          salesItemList.get(pos).setNewprice(dft.format(Double.parseDouble(mHolder.listitemrate.getText().toString())));

                          CalculateTotal();

                          for (int i = 0; i < salesitems.size(); i++) {
                              if(!salesitems.get(i).getItemqty().equals("")
                                      && ! salesitems.get(i).getItemqty().equals(null) && ! salesitems.get(i).getNewprice().equals(null)
                                      && ! salesitems.get(i).getNewprice().equals("") && ! salesitems.get(i).getSubtotal().equals("")){
                                  if(Double.parseDouble(salesitems.get(i).getItemqty()) > 0
                                          && Double.parseDouble(salesitems.get(i).getNewprice()) > 0 &&
                                          Double.parseDouble(salesitems.get(i).getSubtotal()) > 0){

                                      for (int j=0;j<staticreviewsalesitems.size();j++){
                                          if(salesitems.get(i).getItemcode().equals
                                                  (staticreviewsalesitems.get(j).getItemcode()) &&
                                                  !(staticreviewsalesitems.get(j).getFreeflag().equals("freeitem"))){
                                              staticreviewsalesitems.remove(j);
                                          }
                                      }


                                      staticreviewsalesitems.add(new SalesItemDetails(salesitems.get(i).getItemcode(),salesitems.get(i).getCompanycode(),
                                              salesitems.get(i).getBrandcode(),salesitems.get(i).getManualitemcode()
                                              ,salesitems.get(i).getItemname(),salesitems.get(i).getItemnametamil(),
                                              salesitems.get(i).getUnitcode(),salesitems.get(i).getUnitweightunitcode()
                                              ,salesitems.get(i).getUnitweight(),salesitems.get(i).getUppunitcode()
                                              ,salesitems.get(i).getUppweight(),salesitems.get(i).getItemcategory(),salesitems.get(i).getParentitemcode(),
                                              salesitems.get(i).getAllowpriceedit(), salesitems.get(i).getAllownegativestock()
                                              ,salesitems.get(i).getAllowdiscount(),
                                              salesitems.get(i).getStockqty(),salesitems.get(i).getUnitname(),
                                              salesitems.get(i).getNoofdecimals(),salesitems.get(i).getOldprice(),
                                              salesitems.get(i).getNewprice(),salesitems.get(i).getColourcode(),salesitems.get(i).getHsn(),
                                              salesitems.get(i).getTax(),salesitems.get(i).getItemqty(),salesitems.get(i).getSubtotal()
                                              ,salesitems.get(i).getRouteallowpricedit(),salesitems.get(i).getDiscount(),"",
                                              salesitems.get(i).getPurchaseitemcode(),salesitems.get(i).getFreeitemcode(),
                                              salesitems.get(i).getNewprice(),"","",
                                              salesitems.get(i).getMinsalesqty(),"","","",""));

                                  }
                              }

                          }
                          totalcartitems.setText(String.valueOf(staticreviewsalesitems.size()));
                      } else {
                          final int pos1 = (Integer) mHolder.listitemqty.getTag();
                          mHolder.listitemtotal.setText("0.0");
                          salesItemList.get(pos1).setItemqty("0");
                          //Set Array value for subtotal
                          salesItemList.get(pos1).setSubtotal("0.0");

                          for (int i = 0; i < salesitems.size(); i++) {
                              for (int j = 0; j < staticreviewsalesitems.size(); j++) {
                                  if (salesitems.get(i).getItemcode().equals
                                          (staticreviewsalesitems.get(j).getItemcode()) &&
                                          !(staticreviewsalesitems.get(j).getFreeflag().equals("freeitem"))) {
                                      staticreviewsalesitems.remove(j);
                                  }
                              }
                          }
                          totalcartitems.setText(String.valueOf(staticreviewsalesitems.size()));

                      }
                  } else {
                      if(!(mHolder.listitemrate.getText().toString()).equals("") &&
                              !(mHolder.listitemrate.getText().toString()).equals(" ")
                              && !(mHolder.listitemrate.getText().toString()).equals("0")
                              && !(mHolder.listitemrate.getText().toString()).equals("0.0")
                              && !(mHolder.listitemrate.getText().toString()).equals(null)){
                          Toast toast = Toast.makeText(getApplicationContext(), "Please enter valid qty", Toast.LENGTH_LONG);
                          toast.setGravity(Gravity.CENTER, 0, 0);
                          toast.show();
                      }else{
                          Toast toast = Toast.makeText(getApplicationContext(), "Please enter valid price", Toast.LENGTH_LONG);
                          toast.setGravity(Gravity.CENTER, 0, 0);
                          toast.show();
                      }

                      for (int i = 0; i < salesitems.size(); i++) {
                          for (int j = 0; j < staticreviewsalesitems.size(); j++) {
                              if (salesitems.get(i).getItemcode().equals
                                      (staticreviewsalesitems.get(j).getItemcode()) &&
                                      !(staticreviewsalesitems.get(j).getFreeflag().equals("freeitem"))) {
                                  staticreviewsalesitems.remove(j);
                              }
                          }
                      }
                      totalcartitems.setText(String.valueOf(staticreviewsalesitems.size()));

                      return;

                  }
              }
          });

            return convertView;
        }

        private class ViewHolder1 {
            private TextView listitemname;
            private TextView listitemcode;
            private EditText listitemqty;
            private EditText listitemrate;
            private TextView listitemtotal,listitemtax,labelstock,labelhsntax;
            private LinearLayout itemLL,stockvalueLL;
            private  ImageView pricearrow;

        }
    }
    public void CalculateTotal() {
        final DecimalFormat dft = new DecimalFormat("0.00");
        double res1 = 0;
        double res2 = 0;
        for (int i = 0; i < salesitems.size(); i++) {
            String saleseqty = salesitems.get(i).getItemqty();
            String salessubtotal = salesitems.get(i).getSubtotal();
            String getsaleseqty;
            if (saleseqty.equals("")) {
                getsaleseqty = "0";
            } else {
                getsaleseqty = saleseqty;
            }
            String getsalessubtotal;
            if (salessubtotal.equals("")) {
                getsalessubtotal = "0";
            } else {
                getsalessubtotal = salessubtotal;
            }

            res1 = res1 + Double.parseDouble(getsalessubtotal);
            //res2 = res2 + Double.parseDouble(getsaleseqty);
        }
        billisttotalamount.setText(dft.format(res1));
    }




    public void goBack(View v) {
        if(salesitems.size() > 0) {
            for (int i = 0; i < lv_sales_items.getChildCount(); i++) {
                View listRow = lv_sales_items.getChildAt(i);
                EditText getlistqty = (EditText) listRow.findViewById(R.id.listitemqty);
                TextView getlisttotal = (TextView) listRow.findViewById(R.id.listitemtotal);
                String getitemlistqty = getlistqty.getText().toString();
                String getitemlisttotal = getlisttotal.getText().toString();
                if (!getitemlistqty.equals("") && !getitemlistqty.equals(null)) {
                    if (Double.parseDouble(getitemlistqty) > 0 && Double.parseDouble(getitemlisttotal) <= 0.0) {
                        getlistqty.requestFocus();
                        Toast toast = Toast.makeText(getApplicationContext(), "Please enter valid total amount", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }
                }
            }
        }
        if(staticreviewsalesitems.size() > 0 ){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Confirmation");
            builder.setMessage("Are you sure you want to clear cart?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SalesReturnActivity.staticreviewsalesitems.clear();
                            salesitems.clear();
                            gstnnumber="";
                            Intent i = new Intent(context, SalesReturnListActivity.class);
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
            SalesReturnActivity.staticreviewsalesitems.clear();
            salesitems.clear();
            gstnnumber="";

            Intent i = new Intent(context, SalesReturnListActivity.class);
            startActivity(i);
        }

    }
    @Override
    public void onBackPressed() {
        goBack(null);
    }

}
