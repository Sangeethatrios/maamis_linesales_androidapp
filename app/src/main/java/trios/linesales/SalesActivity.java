package trios.linesales;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import androidx.annotation.RequiresApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.goodiebag.pinview.Pinview;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SalesActivity extends AppCompatActivity implements View.OnClickListener,View.OnTouchListener {
    private static final long START_TIME_IN_MILLIS = 600000;
    private PopupWindow window,orderwindow,orderitemswindow;
    FloatingActionButton fabgroupitem,addsalescart;
    private Boolean isFabOpen = false;
    private Animation rotate_forward,rotate_backward;
    Context context;
    boolean isopenpopup,isorderopenpopup,isorderitemsopenshowpopup=false;
    ViewGroup view_root;
    private int _xDelta;
    private int _yDelta;
    ImageView salegoback,salelogout;
    float dX;
    float dY;
    int lastAction;
    public DecimalFormat df;
    private OrderExpandableAdapter orderExpandableAdapter;
    private ExpandableAdapter expandableAdapter;
    private ExpandableListView expList,orderexpList,transexpList;

    private List<String> listDataHEader,orderlistDataHEader,translistDataHEader;
    private HashMap<String,List<String>> listhash,orderlisthash,translisthash ;

    TextView billisttotalamount,salesreview;
    ToggleButton togglegstin;
    static RadioButton radio_cash,radio_credit;

    ListView lv_sales_items,lv_AreaList,lv_CustomerList,lv_subgroup,lv_CustomerAreaList,lv_CityList,
            lv_CustomertypeList,lv_salestypeList,lv_freeitemlist,lv_Routelist,lv_RemarksList;
    public static ListView listordereditems;
    TextView txtroutename;
    public  static TextView txtcustomername,txtareaname,totalcartitems,txtsalesdate;
    String[] AreaCode,AreaName,AreaNameTamil,NoOfKm,CityCode,CityName,CustomerCount;
    String[] SubGroupCode,SubGroupName,SubGroupNameTamil;
    String[] CustomerCode,CustomerName,CustomerNameTamil,Address,CustomerAreaCode,MobileNo,
            TelephoneNo,GSTN,SchemeApplicable,customertypecode,CustomerCityName,CustomerAreaName,CustomerTotalOrder,
            Customerbillcount, CusNotPurchasedCount;
    String[] FreeItemName,FreeItemOp,FreeItemHandover,FreeItemDistributed,FreeItemBalance,
            FreeItemCode,FreeItemSNO;
    Dialog areadialog,customerdialog,freeitemdialog, routedialog,dialogNotPurchased,remarksDialog;
    static public String  customercode="",gstnnumber="",
            getschemeapplicable="",getstaticsubcode="",getstaticchilditemcode="",getstaticgetchildqty="",
            getlabelchildqty="",customercityname="",customerareaname="",customername="",customercityarea="",fromcustomer="",
            otptimevalidity="",otptimevaliditybackend="",getmobilenoverifycount="",orderbillno="",
            ordertransactionno="",orderfinancialyearcode="";
    public static ArrayList<SalesItemDetails> salesitems = new ArrayList<SalesItemDetails>();
    ArrayList<SalesItemDetails> freeitems = new ArrayList<SalesItemDetails>();
    static ArrayList<SalesItemDetails> staticreviewsalesitems = new ArrayList<SalesItemDetails>();
    Dialog stockconvertdialog,dialog,citydialog,customertypedialog,salestypedialog;
    public static boolean paymenttype;
    ImageView btnaddcustomer;
    TextView txtpopupustomername,txtcustomernametamil,txtaddress,txtcityname,
            txtarea,txtphoneno,txtlandlineno,txtemailid,txtgstin,txtadhar,txtcustomertype,txtsalestype,txtwhatsappno;
    Button btnSaveCustomer,btnGetOTPCustomer;
    String getcitycode,getareacode,getRemarksCode;
    String getcustomertypecode,getsalestypecode;
    String[] areacode,areaname,areanametamil,customercount;
    String[] citycode,cityname,citynametamil;
    String[] customertypecodearr,customertypenamearr;
    String[] salestypecodearr,salestypenamearr;
    public static boolean ifsavedsales = false;
    public static final String GSTINFORMAT_REGEX = "[0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[Z]{1}[0-9a-zA-Z]{1}";
    public static final String GSTN_CODEPOINT_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static String getitemsfromcode = "0", getscheduleroutecode;
    boolean networkstate;
    boolean isopenshowpopup=false;
    boolean cartflag =false;
    boolean cartpriceflag =false;
    boolean cartcheckflag =false;
    public String getSchemebusinesstype="0";
    public String getSchemebilltypecode="0";
    String[] ar;
    public static String getpaymenttypecode="",getpaymenttypename="";
    public boolean ItemApplyScheme =false;
    Dialog billtypedialog;
    static Boolean confromBilltype=false;
    public static PreferenceMangr preferenceMangr=null;
    boolean isstockcoversionpopupopen=false;
    public static int gblitemcount=0;
    Integer mobilenoverificationstatus=0;
    Dialog pindialog;
    Button btnVerifypin,btnGetOTP;
    Pinview otppinview;
    EditText txtotpphoneno;
    TextView txttimer,txtcustomer,txtcustomercity,txtcustomerarea,OtherRoute,ScheduleRoute,txtroutes,
            txtremarks;
    LinearLayout LL3,LL2,OtherTab,ScheduleTab,OtherRoutesDrp,Routedivisiontab;
    String otpprocess="send";
    CountDownTimer CountDownTimer;
    //FOR route master
    String[] routecode,routename,routenametamil,routeday;
    Integer routeflag=0;
    boolean isorderopenshowpopup=false;
    public static Double ordertosalestotal;
    OrderedItemsAdapter orderedItemsAdapter=null;
    private TransactionsExpandableAdapter transactionsExpandableAdapter;
    String[] notPurchasedRemarks, notPurchasedRemarksCode;
    GPSTracker gpsTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);
        try {
            context = this;

            //Declare All ListView,TextView,Edit Text ,Fab Buttons
            fabgroupitem = (FloatingActionButton) findViewById(R.id.fabgroupitem);
            lv_sales_items = (ListView) findViewById(R.id.lv_sales_items);
            rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
            rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
            billisttotalamount = (TextView) findViewById(R.id.billisttotalamount);
            view_root = (ViewGroup) findViewById(R.id.view_root);
            togglegstin = (ToggleButton) findViewById(R.id.togglegstin);
            salesreview = (TextView) findViewById(R.id.salesreview);
            salegoback = (ImageView) findViewById(R.id.salegoback);
            salelogout = (ImageView) findViewById(R.id.salelogout);
            txtareaname = (TextView) findViewById(R.id.txtareaname);
            txtcustomername = (TextView) findViewById(R.id.txtcustomername);
            txtroutename = (TextView) findViewById(R.id.txtroutename);
            txtsalesdate = (TextView) findViewById(R.id.txtsalesdate);
            radio_cash = (RadioButton) findViewById(R.id.radio_cash);
            radio_credit = (RadioButton) findViewById(R.id.radio_credit);
            totalcartitems = (TextView) findViewById(R.id.totalcartitems);
            addsalescart = (FloatingActionButton) findViewById(R.id.addsalescart);
            btnaddcustomer = (ImageView) findViewById(R.id.btnaddcustomer);


            try {
                preferenceMangr = new PreferenceMangr(context);
            }catch (Exception e){
                Log.d("Preference Manager : ",e.toString());
            }

            ifsavedsales = false;

            try{
                //locManager = new LocManager(context,SalesActivity.this,Constants.FROM_SALES_ACT);
                if(preferenceMangr.pref_getBoolean(Constants.KEY_GET_GPSTRACKINGSTATUS))
                    gpsTracker = new GPSTracker(context);
            }catch (Exception e){

            }
            final View dragView = findViewById(R.id.movecart);
            dragView.setOnTouchListener(this);


            //Get Current date
            DataBaseAdapter objdatabaseadapter = null;
            try {
                objdatabaseadapter = new DataBaseAdapter(context);
                objdatabaseadapter.open();
                //LoginActivity.getformatdate = objdatabaseadapter.GenCreatedDate();
                //LoginActivity.getcurrentdatetime = objdatabaseadapter.GenCurrentCreatedDate();
                otptimevalidity=objdatabaseadapter.Getotptimevalidity();
                otptimevaliditybackend=objdatabaseadapter.Getotptimevaliditybackend();

                preferenceMangr.pref_putString("getformatdate",objdatabaseadapter.GenCreatedDate());
                preferenceMangr.pref_putString("getcurrentdatetime",objdatabaseadapter.GenCurrentCreatedDate());

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
            //Set values for corresponding values
            txtareaname.setText(LoginActivity.getareaname);

            //salesfabclickoption
            fabgroupitem.setOnClickListener(this);

            //Set total cart item value
            totalcartitems.setText(String.valueOf(staticreviewsalesitems.size()));

            CheckToggleButton();
            togglegstin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (radio_cash.isChecked()) {
                    CheckToggleButton();
                }
                }
            });
            togglegstin.setBackgroundColor(getResources().getColor(R.color.graycolor));


            //if check credit automatic gstin button as green
            radio_credit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    togglegstin.setChecked(true);
                    CheckToggleButton();
                    togglegstin.setTextOn("GSTIN \n " + gstnnumber);
                    togglegstin.setBackgroundColor(getResources().getColor(R.color.green));
                    if (gstnnumber.equals("")) {
                        togglegstin.setBackgroundColor(getResources().getColor(R.color.graycolor));
                    }
                }
            });

            //open Review Screen
            salesreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (staticreviewsalesitems.size() > 0) {
                        boolean checkquantityflag = true;
                        if (salesitems.size() > 0) {
                        /*for (int i = 0; i < salesitems.size(); i++) {
                            if (!salesitems.get(i).getItemqty().equals("")
                                    && !salesitems.get(i).getItemqty().equals(null)
                                    && !salesitems.get(i).getItemqty().equals("0")
                                    && !salesitems.get(i).getNewprice().equals(null)
                                    && !salesitems.get(i).getNewprice().equals("")) {
                                if (Double.parseDouble(salesitems.get(i).getItemqty()) > 0
                                        && Double.parseDouble(salesitems.get(i).getSubtotal()) > 0) {
                                    checkquantityflag = true;
                                } else {
                                    checkquantityflag = false;
                                }

                            }
                        }*/
                            for (int i = 0; i < lv_sales_items.getChildCount(); i++) {
                                View listRow = lv_sales_items.getChildAt(i);
                                EditText getlistqty = (EditText) listRow.findViewById(R.id.listitemqty);
                                TextView getlisttotal = (TextView) listRow.findViewById(R.id.listitemtotal);
                                String getitemlistqty = getlistqty.getText().toString();
                                String getitemlisttotal = getlisttotal.getText().toString();
                                if (!getitemlistqty.equals("") && !getitemlistqty.equals(null)) {
                                    if (Double.parseDouble(getitemlistqty) > 0 && Double.parseDouble(getitemlisttotal) <= 0.0) {
                                        getlistqty.requestFocus();
                                        Toast toast = Toast.makeText(getApplicationContext(), "Some items are not added to cart", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                        return;
                                    }
                                }
                            }
                        }
                        salesitems.clear();
                        SalesItemAdapter adapter = new SalesItemAdapter(context, salesitems,"0");
                        lv_sales_items.setAdapter(adapter);
                        Intent i = new Intent(context, ReviewActivity.class);
                        i.putExtra("Payment_Type",radio_cash.isChecked());
                        startActivity(i);
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), "Cart is empty", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        //Toast.makeText(getApplicationContext(),"Cart is empty",Toast.LENGTH_SHORT).show();
                        return;
                    }

                }
            });

            //Logout process
            salelogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // HomeActivity.logoutprocess = "True";
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Confirmation");
                    builder.setIcon(R.mipmap.ic_van);
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
                    showgroup();
                    gblitemcount=0;
                    if(staticreviewsalesitems.size()>0){
                        //Toast.makeText(context,"area name ",Toast.LENGTH_SHORT).show();
                        txtareaname.setEnabled(false);
                    }else{
                        txtareaname.setEnabled(true);
                        GetArea(preferenceMangr.pref_getString("getroutecode"));
                    }

                }
            });
            txtcustomername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showgroup();
                    gblitemcount=0;
                    if(staticreviewsalesitems.size()>0){
                        txtcustomername.setEnabled(false);
                        //Toast.makeText(context,"customer name ",Toast.LENGTH_SHORT).show();
                    }else{
                        txtcustomername.setEnabled(true);
                        GetCustomer(LoginActivity.getareacode);
                    }

                }
            });


            /*******END FILTER FUNCTIONALITY******/
            //Set Routename and current date
            txtroutename.setText(preferenceMangr.pref_getString("getroutename"));
            txtsalesdate.setText(preferenceMangr.pref_getString("getcurrentdatetime"));

            //Set total cart items
            totalcartitems.setText("0");


            btnaddcustomer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showgroup();
                    GetCustomerPopup();
                    gblitemcount=0;
                }
            });

            /*radio_cash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //if(!radio_cash.isChecked()){

                    if(staticreviewsalesitems.size() > 0 ){
                        radio_cash.setChecked(false);
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
                                        DataBaseAdapter objdatabaseadapter = new DataBaseAdapter(context);
                                        objdatabaseadapter.open();
                                        objdatabaseadapter.DeleteSalesItemCart();
                                        objdatabaseadapter.close();
                                        SalesItemAdapter adapter = new SalesItemAdapter(context,salesitems);
                                        lv_sales_items.setAdapter(adapter);

                                        radio_cash.setChecked(true);
                                        getpaymenttypecode="1";
                                        getpaymenttypename="CASH";
                                        radio_credit.setChecked(false);
                                        Toast.makeText(context, "CASH", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        radio_cash.setChecked(true);
                                        radio_credit.setChecked(false);
                                        dialog.cancel();
                                        Toast.makeText(context, "CASH1", Toast.LENGTH_SHORT).show();

                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }


                   // }
                }
            });

            radio_credit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //if(!radio_credit.isChecked()){

                    if(staticreviewsalesitems.size() > 0 ){
                        radio_credit.setChecked(false);
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
                                        DataBaseAdapter objdatabaseadapter = new DataBaseAdapter(context);
                                        objdatabaseadapter.open();
                                        objdatabaseadapter.DeleteSalesItemCart();
                                        objdatabaseadapter.close();
                                        SalesItemAdapter adapter = new SalesItemAdapter(context,salesitems);
                                        lv_sales_items.setAdapter(adapter);

                                        radio_credit.setChecked(true);
                                        getpaymenttypecode="2";
                                        getpaymenttypename="CREDIT";
                                        radio_cash.setChecked(false);
                                        Toast.makeText(context, "CREDIT", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        radio_credit.setChecked(true);
                                        radio_cash.setChecked(false);
                                        dialog.cancel();
                                        Toast.makeText(context, "CREDIT1", Toast.LENGTH_SHORT).show();

                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }

                    //}
                }
            });*/


        }catch (Exception e){
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }
    /************OPEN CUSTOMR POPUP*********************/
    public void GetCustomerPopup(){
        try {
            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.addcustomer);
            ImageView closepopup = (ImageView) dialog.findViewById(R.id.closepopup);
            txtpopupustomername = (TextView) dialog.findViewById(R.id.txtcustomername);
            txtcustomernametamil = (TextView) dialog.findViewById(R.id.txtcustomernametamil);
            txtaddress = (TextView) dialog.findViewById(R.id.txtaddress);
            txtcityname = (TextView) dialog.findViewById(R.id.txtcityname);
            txtarea = (TextView) dialog.findViewById(R.id.txtarea);
            txtphoneno = (TextView) dialog.findViewById(R.id.txtphoneno);
            txtlandlineno = (TextView) dialog.findViewById(R.id.txtlandlineno);
            txtemailid = (TextView) dialog.findViewById(R.id.txtemailid);
            txtgstin = (TextView) dialog.findViewById(R.id.txtgstin);
            txtadhar = (TextView) dialog.findViewById(R.id.txtadhar);
            txtcustomertype = (TextView) dialog.findViewById(R.id.txtcustomertype);
            txtsalestype = (TextView) dialog.findViewById(R.id.txtsalestype);
            btnSaveCustomer = (Button) dialog.findViewById(R.id.btnSaveCustomer);
            txtwhatsappno = (TextView)dialog.findViewById(R.id.txtwhatsappno);
            btnGetOTPCustomer = (Button)dialog.findViewById(R.id.btnGetOTPCustomer);



            btnSaveCustomer.setText("Save");

            txtcustomertype.setEnabled(false);
            txtcustomertype.setText("Cash Party");
            txtcustomertype.setBackgroundResource(R.drawable.editbackgroundgray);
            getcustomertypecode= "1";

            txtcityname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GetCity(preferenceMangr.pref_getString("getroutecode"));
                }
            });
            txtarea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GetCustomerArea(getcitycode);
                }
            });
            txtcustomertype.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //GetCustomerType();
                }
            });
            txtsalestype.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GetSalesType();
                }
            });
            btnGetOTPCustomer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (txtpopupustomername.getText().toString().equals("") || txtpopupustomername.getText().toString().equals("null")
                            || txtpopupustomername.getText().toString().equals(null)) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please enter customer name", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        // Toast.makeText(getApplicationContext(),"Please enter customer name",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (txtcityname.getText().toString().equals("") || txtcityname.getText().toString().equals("null")
                            || txtcityname.getText().toString().equals(null) || getcitycode.equals("") || getcitycode.equals(null)) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please select city name", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        //  Toast.makeText(getApplicationContext(),"Please select city name",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (txtarea.getText().toString().equals("") || txtarea.getText().toString().equals("null")
                            || txtarea.getText().toString().equals(null) || txtarea.equals("") || txtarea.equals(null)) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please select area name", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        //Toast.makeText(getApplicationContext(),"Please select area name",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(txtphoneno.getText().toString().equals("") || txtphoneno.getText().toString().equals("null")
                            || txtphoneno.getText().toString().equals(null)){
                        Toast toast = Toast.makeText(getApplicationContext(),"Please enter Mobile No.", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        //Toast.makeText(getApplicationContext(), "Please enter Mobile No. or GSTIN", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(!txtphoneno.getText().toString().equals("") && !txtphoneno.getText().toString().equals("null")
                            && !txtphoneno.getText().toString().equals(null) ) {
                        if (txtphoneno.getText().toString().length() < 10) {
                            Toast toast = Toast.makeText(getApplicationContext(),"Mobile No. should contain 10 digits", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            //Toast.makeText(getApplicationContext(), "Mobile No. should contain 10 digits", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    customername=txtcustomernametamil.getText().toString().equals("")?txtpopupustomername.getText().toString():txtcustomernametamil.getText().toString();
                    customercityarea= txtcityname.getText().toString() + ',' + txtarea.getText().toString();
                    Showmobilenoverify("add");
                    btnSaveCustomer.setEnabled(true);
                }
            });
            btnSaveCustomer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String getcustomername = txtpopupustomername.getText().toString();
                    String getcustomernametamil = txtcustomernametamil.getText().toString();
                    String getaddress = txtaddress.getText().toString();
                    String getcityname = txtcityname.getText().toString();
                    String getarea = txtarea.getText().toString();
                    String getphoneno = txtphoneno.getText().toString();
                    String getlandlineno = txtlandlineno.getText().toString();
                    String getemailid = txtemailid.getText().toString();
                    String getgstin = txtgstin.getText().toString();
                    String getaadharno = txtadhar.getText().toString();
                    String getcustomertype = txtcustomertype.getText().toString();
                    String getsalestype = txtsalestype.getText().toString();
                    String getwhatsappno = txtwhatsappno.getText().toString();

                    if (getcustomername.equals("") || getcustomername.equals("null")
                            || getcustomername.equals(null)) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please enter customer name", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        // Toast.makeText(getApplicationContext(),"Please enter customer name",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (getcityname.equals("") || getcityname.equals("null")
                            || getcityname.equals(null) || getcitycode.equals("") || getcitycode.equals(null)) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please select city name", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        //  Toast.makeText(getApplicationContext(),"Please select city name",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (getarea.equals("") || getarea.equals("null")
                            || getarea.equals(null) || getareacode.equals("") || getareacode.equals(null)) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please select area name", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        //Toast.makeText(getApplicationContext(),"Please select area name",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if ((getphoneno.equals("") || getphoneno.equals("null")
                            || getphoneno.equals(null)) && (getgstin.equals("") || getgstin.equals("null")
                            || getgstin.equals(null))) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please enter Mobile No. or GSTIN", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        //Toast.makeText(getApplicationContext(), "Please enter Mobile No. or GSTIN", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(getphoneno.equals("") || getphoneno.equals("null")
                            || getphoneno.equals(null)){
                        Toast toast = Toast.makeText(getApplicationContext(),"Please enter Mobile No.", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        //Toast.makeText(getApplicationContext(), "Please enter Mobile No. or GSTIN", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!getphoneno.equals("") && !getphoneno.equals("null")
                            && !getphoneno.equals(null)) {
                        if (getphoneno.length() < 10) {
                            Toast toast = Toast.makeText(getApplicationContext(), "Mobile No. should contain 10 digits", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            //Toast.makeText(getApplicationContext(), "Mobile No. should contain 10 digits", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    if(mobilenoverificationstatus==0){
                        Toast toast = Toast.makeText(getApplicationContext(),"Please verify Mobile No.", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        //Toast.makeText(getApplicationContext(), "Mobile No. should contain 10 digits", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(!getwhatsappno.equals("") && !getwhatsappno.equals("null")
                            && !getwhatsappno.equals(null) ) {
                        if (getwhatsappno.length() < 10) {
                            Toast toast = Toast.makeText(getApplicationContext(),"Whatsapp No. should contain 10 digits", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return;
                        }
                    }

                    if (!getgstin.equals("") && !getgstin.equals("null")
                            && !getgstin.equals(null)) {
                        if (getgstin.length() > 0) {
                            try {
                                if (!validGSTIN(getgstin)) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter valid GSTIN", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    // Toast.makeText(getApplicationContext(), "Please enter valid GSTIN", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (!getaadharno.equals("") && !getaadharno.equals("null")
                            && !getaadharno.equals(null)) {
                        if (getaadharno.length() < 12) {
                            Toast toast = Toast.makeText(getApplicationContext(), "Aadhar No. should contain 12 digits", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            //Toast.makeText(getApplicationContext(), "Aadhar No. should contain 12 digits", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    if ( getcustomertype.equals("") || getcustomertype.equals("null") || getcustomertype.equals(null) ||
                            getcustomertypecode.equals("") || getcustomertypecode.equals("null") || getcustomertypecode.equals(null) ) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please select customer type", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        //Toast.makeText(getApplicationContext(), "Please enter Mobile No. or GSTIN", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if ( getsalestype.equals("") || getsalestype.equals("null") || getsalestype.equals(null) ||
                            getsalestypecode.equals("") || getsalestypecode.equals("null") || getsalestypecode.equals(null)) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please select sales type", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        //Toast.makeText(getApplicationContext(), "Please enter Mobile No. or GSTIN", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    btnSaveCustomer.setEnabled(false);
                    DataBaseAdapter objdatabaseadapter = null;
                    try {
                        if (getcustomernametamil.equals("") || getcustomernametamil.equals(null)
                                || getcustomernametamil.equals("null")) {
                            getcustomernametamil = getcustomername;
                        }
                        //Order item details
                        objdatabaseadapter = new DataBaseAdapter(context);
                        objdatabaseadapter.open();
                        String latitude="0",longtitude="0";
                        try{
                            if(preferenceMangr.pref_getBoolean(Constants.KEY_GET_GPSTRACKINGSTATUS)){
                                if(gpsTracker.isGPSTrackingEnabled){
                                    latitude = String.valueOf(gpsTracker.getLatitude());
                                    longtitude = String.valueOf(gpsTracker.getLongitude());
                                }
                            }

                        }catch (Exception e){
                            objdatabaseadapter.insertErrorLog(String.valueOf(e).replace("'", " "), this.getClass().getSimpleName() + "Exception in getting GPS latlong", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }

                        String getresult = "";
                        String getretailercount = objdatabaseadapter.CheckCustomerAlreadyExistsCount(getareacode, getcustomername);
                        if (getretailercount.equals("Success")) {
                            getresult = objdatabaseadapter.InsertCustomerDetails(getcustomername, getcustomernametamil,
                                    getaddress, getareacode, getemailid, getphoneno, getlandlineno, getaadharno,
                                    getgstin,getcustomertypecode,getsalestypecode,getwhatsappno,mobilenoverificationstatus,latitude,longtitude);
                            if (!getresult.equals("")) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                LoginActivity.getareacode = getareacode;
                                LoginActivity.getareaname = getcityname + " - " + getarea;
                                txtareaname.setText(LoginActivity.getareaname);
                                txtcustomername.setText(getcustomernametamil);
                                customercode = getresult;
                                gstnnumber = getgstin.trim();

                                if(getcustomertypecode.equals("1")){
                                    SalesActivity.getpaymenttypecode="1";
                                    radio_cash.setChecked(true);
                                    //radio_cash.setEnabled(false);
                                    radio_credit.setEnabled(false);
                                }else if(getcustomertypecode.equals("2")){
                                    SalesActivity.getpaymenttypecode="2";
                                    // radio_credit.setChecked(true);
                                    // // radio_credit.setEnabled(false);
                                    // radio_cash.setEnabled(true);
                                    radio_cash.setChecked(false);
                                    radio_credit.setChecked(true);
                                    radio_cash.setEnabled(true);
                                    radio_credit.setEnabled(true);
                                }

                                if(getgstin.trim().equals("")||getgstin.equals(null)||getgstin.equals("0")){
                                    togglegstin.setBackgroundColor(getResources().getColor(R.color.graycolor));
                                }else{
                                    togglegstin.setBackgroundColor(getResources().getColor(R.color.green));
                                }
                               /* radio_cash.setChecked(true);
                                radio_credit.setEnabled(false);
                                radio_cash.setEnabled(false);*/


                                togglegstin.setText("GSTIN \n "+getgstin.trim());
                                togglegstin.setTextOn("GSTIN \n "+getgstin.trim());
                                togglegstin.setTextOff("GSTIN \n "+getgstin.trim());
                                //Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_SHORT).show();
                                getcustomertypecode = "";
                                getsalestypecode = "";
                                dialog.dismiss();
                                btnSaveCustomer.setEnabled(true);
                                lv_sales_items.setAdapter(null);
                                getschemeapplicable = "";
                            }
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), "Customer name already exists", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            // Toast.makeText(context, "Customer name already exists", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        networkstate = isNetworkAvailable();
                        if (networkstate == true) {
                            new AsyncCustomerDetails().execute();
                        }

                    } catch (Exception e) {
                        DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                        mDbErrHelper.open();
                        String geterrror = e.toString();
                        mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        mDbErrHelper.close();
                    } finally {
                        if (objdatabaseadapter != null)
                            objdatabaseadapter.close();
                    }

                }
            });

            closepopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } catch (Exception e){
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
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
    /*******FILTER FUNCTIONALITY********/

    //Get Area name
    public  void GetCustomerArea(String citycode){

        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetAreaCityDB(citycode,preferenceMangr.pref_getString("getroutecode"));
            if(Cur.getCount()>0) {
                areacode = new String[Cur.getCount()];
                areaname = new String[Cur.getCount()];
                areanametamil = new String[Cur.getCount()];
                customercount = new String[Cur.getCount()];
                for(int i=0;i<Cur.getCount();i++){
                    areacode[i] = Cur.getString(0);
                    areaname[i] = Cur.getString(1);
                    areanametamil[i] = Cur.getString(2);
                    customercount[i] = Cur.getString(6);
                    customercount[i] = Cur.getString(6);
                    Cur.moveToNext();
                }

                areadialog = new Dialog(context);
                areadialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                areadialog.setContentView(R.layout.areapopup);
                lv_CustomerAreaList = (ListView) areadialog.findViewById(R.id.lv_AreaList);
                ImageView close = (ImageView) areadialog.findViewById(R.id.close);

                Routedivisiontab=(LinearLayout) areadialog.findViewById(R.id.LLTab);
                OtherTab=(LinearLayout) areadialog.findViewById(R.id.OtherTab);
                ScheduleTab= (LinearLayout) areadialog.findViewById(R.id.ScheduleTab);
                OtherRoute = (TextView) areadialog.findViewById(R.id.OtherRouteTxt);
                ScheduleRoute = (TextView)areadialog.findViewById(R.id.ScheduleRouteTxt);
                OtherRoutesDrp=(LinearLayout)areadialog.findViewById(R.id.OtherRoutesDrp);
                txtroutes=(TextView) areadialog.findViewById(R.id.txtroutes);
                Routedivisiontab.setVisibility((View.GONE));
                OtherTab.setVisibility(View.GONE);
                OtherRoutesDrp.setVisibility(View.GONE);


                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        areadialog.dismiss();
                    }
                });
                AreaAdapter adapter = new AreaAdapter(context);
                lv_CustomerAreaList.setAdapter(adapter);
                areadialog.show();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(),"No Area in this route", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //Toast.makeText(getApplicationContext(),"No Area in this route",Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
        finally {
            // this gets called even if there is an exception somewhere above
            if(objdatabaseadapter != null)
                objdatabaseadapter.close();
            if(Cur != null)
                Cur.close();
        }
    }

    //Get city name
    public  void GetCity(String routecode){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetCityDB(routecode);
            if(Cur.getCount()>0) {
                citycode = new String[Cur.getCount()];
                cityname = new String[Cur.getCount()];
                citynametamil = new String[Cur.getCount()];
                for(int i=0;i<Cur.getCount();i++){
                    citycode[i] = Cur.getString(0);
                    cityname[i] = Cur.getString(1);
                    citynametamil[i] = Cur.getString(2);
                    Cur.moveToNext();
                }

                citydialog = new Dialog(context);
                citydialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                citydialog.setContentView(R.layout.citypopup);
                lv_CityList = (ListView) citydialog.findViewById(R.id.lv_CityList);
                ImageView close = (ImageView) citydialog.findViewById(R.id.close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        citydialog.dismiss();
                    }
                });
                CityAdapter adapter = new CityAdapter(context);
                lv_CityList.setAdapter(adapter);
                citydialog.show();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(),"No city in this route", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                // Toast.makeText(getApplicationContext(),"No city in this route",Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
        finally {
            // this gets called even if there is an exception somewhere above
            if(objdatabaseadapter != null)
                objdatabaseadapter.close();
            if(Cur != null)
                Cur.close();
        }
    }


    public  void GetArea(String routecode){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetAreaDB(routecode,Constants.CUSTOMER_CATEGORY_SALES);
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
                if(routeflag == 1 )
                {
                    SalesAreaAdapter adapter = new SalesAreaAdapter(context);
                    lv_AreaList.setAdapter(adapter);
                    return;
                }

                areadialog = new Dialog(context);
                areadialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                areadialog.setContentView(R.layout.areapopup);
                lv_AreaList = (ListView) areadialog.findViewById(R.id.lv_AreaList);
                ImageView close = (ImageView) areadialog.findViewById(R.id.close);

                OtherTab=(LinearLayout) areadialog.findViewById(R.id.OtherTab);
                ScheduleTab= (LinearLayout) areadialog.findViewById(R.id.ScheduleTab);
                OtherRoute = (TextView) areadialog.findViewById(R.id.OtherRouteTxt);
                ScheduleRoute = (TextView)areadialog.findViewById(R.id.ScheduleRouteTxt);
                OtherRoutesDrp=(LinearLayout)areadialog.findViewById(R.id.OtherRoutesDrp);
                txtroutes=(TextView) areadialog.findViewById(R.id.txtroutes);

                OtherRoutesDrp.setVisibility(View.GONE);

//                ScheduleTab.setBackgroundColor( getResources().getColor(R.color.colorPrimaryDark));
//                ScheduleRoute.setTextColor(getResources().getColor(R.color.white));

                //other routes click event
                OtherTab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(getscheduleroutecode == "" || getscheduleroutecode == null ||
                                getscheduleroutecode == "undefined") {
                            lv_AreaList.setAdapter(null);
                        }
                        routeflag = 1;
                        GetArea(getscheduleroutecode);
                        OtherTab.setBackgroundColor( getResources().getColor(R.color.colorPrimaryDark));
                        OtherRoute.setTextColor(getResources().getColor(R.color.white));
                        ScheduleTab.setBackgroundColor(getResources().getColor(R.color.white));
                        ScheduleRoute.setTextColor(getResources().getColor(R.color.black));
                        OtherRoutesDrp.setVisibility(View.VISIBLE);
                    }
                });
                ScheduleTab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        routeflag = 1;
                        GetArea(preferenceMangr.pref_getString("getroutecode"));

                        ScheduleTab.setBackgroundColor( getResources().getColor(R.color.colorPrimaryDark));
                        ScheduleRoute.setTextColor(getResources().getColor(R.color.white));
                        OtherTab.setBackgroundColor(getResources().getColor(R.color.white));
                        OtherRoute.setTextColor(getResources().getColor(R.color.black));
                        OtherRoutesDrp.setVisibility(View.GONE);
                    }
                });
                txtroutes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GetRoute();
//                        if(staticreviewsalesitems.size()>0){
//                            //Toast.makeText(context,"area name ",Toast.LENGTH_SHORT).show();
//                            txtroutes.setEnabled(false);
//                        }else{
//                            txtroutes.setEnabled(true);
//                            GetArea(preferenceMangr.pref_getString("getroutecode"));
//                        }

                    }
                });

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
                Toast toast = Toast.makeText(getApplicationContext(),"No Area in this route", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //Toast.makeText(getApplicationContext(),"No Area in this route",Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
        finally {
            // this gets called even if there is an exception somewhere above
            if(objdatabaseadapter != null)
                objdatabaseadapter.close();
            if(Cur != null)
                Cur.close();
        }
    }


    public  void GetCustomer(String areacode){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetCustomerDB(areacode,Constants.CUSTOMER_CATEGORY_SALES);

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
                CustomerCityName = new String[Cur.getCount()];
                CustomerAreaName = new String[Cur.getCount()];
                CustomerTotalOrder = new String[Cur.getCount()];
                Customerbillcount = new String[Cur.getCount()];
                CusNotPurchasedCount = new String[Cur.getCount()];
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
                    CustomerAreaName[i] = Cur.getString(12);
                    CustomerCityName[i] = Cur.getString(13);
                    CustomerTotalOrder[i]=Cur.getString(14);
                    Customerbillcount[i]=Cur.getString(15);
                    CusNotPurchasedCount[i]=Cur.getString(16);
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
                Toast toast = Toast.makeText(getApplicationContext(),"No Customer in this area", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //Toast.makeText(getApplicationContext(),"No Customer in this area",Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
        finally {
            // this gets called even if there is an exception somewhere above
            if(objdatabaseadapter != null)
                objdatabaseadapter.close();
            if(Cur != null)
                Cur.close();
        }
    }

    /*********CLOSE FILTER FUNCTIONALITY********/

    //Check gstin functionality

    //cHECK GSTIN FORMAT
    private   boolean validGSTIN(String gstin) throws Exception {
        boolean isValidFormat = false;
        if (checkPattern(gstin, GSTINFORMAT_REGEX)) {
            isValidFormat = verifyCheckDigit(gstin);
        }
        return isValidFormat;

    }
    private   boolean verifyCheckDigit(String gstinWCheckDigit) throws Exception {
        Boolean isCDValid = false;
        String newGstninWCheckDigit = getGSTINWithCheckDigit(
                gstinWCheckDigit.substring(0, gstinWCheckDigit.length() - 1));

        if (gstinWCheckDigit.trim().equals(newGstninWCheckDigit)) {
            isCDValid = true;
        }
        return isCDValid;
    }
    public static boolean checkPattern(String inputval, String regxpatrn) {
        boolean result = false;
        if ((inputval.trim()).matches(regxpatrn)) {
            result = true;
        }
        return result;
    }
    public   String getGSTINWithCheckDigit(String gstinWOCheckDigit) throws Exception {
        int factor = 2;
        int sum = 0;
        int checkCodePoint = 0;
        char[] cpChars;
        char[] inputChars;

        try {
            if (gstinWOCheckDigit == null) {
                throw new Exception("GSTIN supplied for checkdigit calculation is null");
            }
            cpChars = GSTN_CODEPOINT_CHARS.toCharArray();
            inputChars = gstinWOCheckDigit.trim().toUpperCase().toCharArray();

            int mod = cpChars.length;
            for (int i = inputChars.length - 1; i >= 0; i--) {
                int codePoint = -1;
                for (int j = 0; j < cpChars.length; j++) {
                    if (cpChars[j] == inputChars[i]) {
                        codePoint = j;
                    }
                }
                int digit = factor * codePoint;
                factor = (factor == 2) ? 1 : 2;
                digit = (digit / mod) + (digit % mod);
                sum += digit;
            }
            checkCodePoint = (mod - (sum % mod)) % mod;
            return gstinWOCheckDigit + cpChars[checkCodePoint];
        } finally {
            inputChars = null;
            cpChars = null;
        }
    }

    /********POPUP GROUP FUNCTIONALITY********/


    public  void GetSubGroupList(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetSubGroupDB();
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
                Toast toast = Toast.makeText(getApplicationContext(),"Van out of stock", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                // Toast.makeText(getApplicationContext(),"Van out of stock",Toast.LENGTH_SHORT).show();
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

    public  void GetItems(String itemsubgroupcode){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            lv_sales_items.setAdapter(null);
            Cur = objdatabaseadapter.GetItemsDB(itemsubgroupcode,preferenceMangr.pref_getString("getroutecode"),LoginActivity.getareacode);
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
                            Cur.getString(23),"0","0",Cur.getString(24),
                            "","","0","",Cur.getString(20),
                            Cur.getString(29),Cur.getString(30),Cur.getString(32),Cur.getString(33),
                            Cur.getString(34) ,"","",Cur.getString(20),""));
                    Cur.moveToNext();
                }
                CalculateTotal();


                SalesItemAdapter adapter = new SalesItemAdapter(context,salesitems,"0");
                lv_sales_items.setAdapter(adapter);
                isopenshowpopup=false;
                //Set ITEMQTY PRICE AND TOTAL
                for (int i = 0; i < staticreviewsalesitems.size(); i++) {
                    for (int j = 0; j < salesitems.size();j++) {
                        if (staticreviewsalesitems.get(i).getItemcode().equals(salesitems.get(j).getItemcode()) &&
                                !(staticreviewsalesitems.get(i).getFreeflag().equals("freeitem"))) {
                            salesitems.get(j).setItemqty(staticreviewsalesitems.get(i).getItemqty());
                            salesitems.get(j).setNewprice(staticreviewsalesitems.get(i).getNewprice());
                            salesitems.get(j).setSubtotal(staticreviewsalesitems.get(i).getSubtotal());
                            isopenshowpopup=true;
                        }
                    }
                }


            }else{
                Toast toast = Toast.makeText(getApplicationContext(),"No Items Available", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //Toast.makeText(getApplicationContext(),"No Items Available",Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
        finally {
            // this gets called even if there is an exception somewhere above
            if(objdatabaseadapter != null)
                objdatabaseadapter.close();
            if(Cur != null)
                Cur.close();
        }
    }

    public  void GetFreeItemexcess(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.getexcessfreeitemqty();
            if(Cur.getCount()>0) {
                FreeItemName = new String[Cur.getCount()];
                FreeItemOp = new String[Cur.getCount()];
                FreeItemHandover = new String[Cur.getCount()];
                FreeItemDistributed = new String[Cur.getCount()];
                FreeItemBalance = new String[Cur.getCount()];
                FreeItemCode = new String[Cur.getCount()];
                FreeItemSNO = new String[Cur.getCount()];
                int j=0;
                for(int i=0;i<Cur.getCount();i++){
                    j++;
                    FreeItemSNO[i] = String.valueOf(j);
                    FreeItemName[i] = Cur.getString(0);
                    FreeItemOp[i] = Cur.getString(3);
                    FreeItemHandover[i] = Cur.getString(1);
                    FreeItemDistributed[i] = Cur.getString(5);
                    FreeItemBalance[i] = Cur.getString(4);
                    FreeItemCode[i] = Cur.getString(2);
                    Cur.moveToNext();
                }

                freeitemdialog = new Dialog(context);
                freeitemdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                freeitemdialog.setContentView(R.layout.freeitemexcesspopup);
                lv_freeitemlist = (ListView) freeitemdialog.findViewById(R.id.lv_freeitemlist);
                ImageView close = (ImageView) freeitemdialog.findViewById(R.id.close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        freeitemdialog.dismiss();
                    }
                });
                FreeItemBaseAdapter adapter = new FreeItemBaseAdapter(context);
                lv_freeitemlist.setAdapter(adapter);
                freeitemdialog.show();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(),"No Customer in this area", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //Toast.makeText(getApplicationContext(),"No Customer in this area",Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
        finally {
            // this gets called even if there is an exception somewhere above
            if(objdatabaseadapter != null)
                objdatabaseadapter.close();
            if(Cur != null)
                Cur.close();
        }
    }

    /*****END GROUP FUNCTIONALITY**********/

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                dX = view.getX() - event.getRawX();
                dY = view.getY() - event.getRawY();
                lastAction = MotionEvent.ACTION_DOWN;
                break;

            case MotionEvent.ACTION_MOVE:
                view.setY(event.getRawY() + dY);
                view.setX(event.getRawX() + dX);
                lastAction = MotionEvent.ACTION_MOVE;
                break;

            case MotionEvent.ACTION_UP:

                break;

            default:
                return false;
        }
        return true;
    }



    public void CheckToggleButton(){
        try {
            if (togglegstin.isChecked()) {
                paymenttype = false;
                togglegstin.setTextOn("GSTIN \n " + gstnnumber);
                togglegstin.setBackgroundColor(getResources().getColor(R.color.green));
                if (gstnnumber.equals("")) {
                    togglegstin.setBackgroundColor(getResources().getColor(R.color.graycolor));
                }
            } else {
                paymenttype = true;
                togglegstin.setTextOff("GSTIN \n " + gstnnumber);
                if (gstnnumber.equals("")) {
                    togglegstin.setBackgroundColor(getResources().getColor(R.color.graycolor));
                }
                togglegstin.setBackgroundColor(getResources().getColor(R.color.graycolor));
            }
        } catch (Exception e){
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
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


            /*try{
                if(radio_cash.isChecked()){
                    radio_credit.setEnabled(false);
                }else if(radio_credit.isChecked()){
                    radio_cash.setEnabled(false);
                }

            }  catch (Exception e){
                Log.i("GetDrildown", e.toString());
            }*/


            if(drilldownitem.equals("group")) {
                isopenshowpopup=true;
                LayoutInflater inflater = (LayoutInflater) SalesActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                isopenshowpopup=true;
                LayoutInflater inflater = (LayoutInflater) SalesActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
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
            Cur = objdatabaseadapter.GetGroupDB();
            if(Cur.getCount()>0) {
                for(int i=0;i<Cur.getCount();i++){
                    listDataHEader.add(Cur.getString(2));
                    Cur1 = objdatabaseadapter.GetSubGroup_GroupDB(Cur.getString(0));
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
                Toast toast = Toast.makeText(getApplicationContext(),"Van out of stock", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //Toast.makeText(getApplicationContext(),"Van out of stock",Toast.LENGTH_SHORT).show();

            }
        }catch (Exception e){
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
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
        if (id == R.id.fabgroupitem) {
            animateFAB();
        }
    }
    public void animateFAB(){
        try{
            if(txtcustomername.getText().toString().equals("") || txtcustomername.getText().toString().equals(null) ||
                    customercode.equals("") ){
                Toast toast = Toast.makeText(getApplicationContext(),"Please select customer", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //Toast.makeText(getApplicationContext(),"Please select customer",Toast.LENGTH_SHORT).show();
                return;
            }else if(!getmobilenoverifycount.equals("0")){
                    Showmobilenoverify("verifycustomermobileno");
            }
            else {
                boolean checkquantityflag = true;
                if(salesitems.size() > 0) {
                /*for (int i = 0; i < salesitems.size(); i++) {
                    if (!salesitems.get(i).getItemqty().equals("")
                            && !salesitems.get(i).getItemqty().equals(null)
                            && !salesitems.get(i).getItemqty().equals("0")
                            && !salesitems.get(i).getNewprice().equals(null)
                            && !salesitems.get(i).getNewprice().equals("")) {
                        if (Double.parseDouble(salesitems.get(i).getItemqty()) > 0
                                && Double.parseDouble(salesitems.get(i).getSubtotal()) > 0) {
                            checkquantityflag = true;
                        } else {
                            checkquantityflag = false;
                        }

                    }
                }*/
                    for (int i = 0; i < lv_sales_items.getChildCount(); i++) {
                        View listRow = lv_sales_items.getChildAt(i);
                        EditText getlistqty = (EditText) listRow.findViewById(R.id.listitemqty);
                        TextView getlisttotal = (TextView) listRow.findViewById(R.id.listitemtotal);
                        String getitemlistqty = getlistqty.getText().toString();
                        String getitemlisttotal = getlisttotal.getText().toString();
                        if(!getitemlistqty.equals("") && !getitemlistqty.equals(null)){
                            if(Double.parseDouble(getitemlistqty)>0 && Double.parseDouble(getitemlisttotal)<=0.0){
                                getlistqty.requestFocus();
                                Toast toast = Toast.makeText(getApplicationContext(),"Some items are not added to cart", Toast.LENGTH_LONG);
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
                    ItemApplyScheme =false;
                    ShowPopupWindow();
                    /*
                    Date : 20-06-2024
                    Vetri sir told to remove this build type confirmation popup
                    */
                    /*if(getpaymenttypecode.equals("2")){
                        if(!confromBilltype){
                            ShowPaymentTypePopup();
                        }else{
                            ShowPopupWindow();
                        }
                    }else{
                        ShowPopupWindow();
                    }*/


                }
                if(confromBilltype) {
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
        }catch (Exception e){
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }

    public class SalesItemAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;
        ArrayList<SalesItemDetails> salesItemList;
        String isparentopen="";
        final DecimalFormat dft = new DecimalFormat("0.00");
        String getstaticparentitemcode="0";
        String getstaticchilditemname="",getstaticchildunitname = "";
        String getstaticchildcode="";
        String isfreestock="";
        boolean isnillstockconversion=false;
        String freeitemstatus="";
        String schemeitemdiscount="0";
        String schemeratestatus="";
        String itemschemeapplicable="not applicable";
        Double TotalQty=0.0,RemainingQty=0.00;

        SalesItemAdapter(Context c,ArrayList<SalesItemDetails> myList,String itemsubgroupcode) {
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
        @SuppressLint({"InflateParams", "LongLogTag"})
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder1 mHolder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.salesitemlist, parent, false);
                mHolder = new ViewHolder1();
                try {
                    mHolder.listitemname = (TextView) convertView.findViewById(R.id.listitemname);
                    mHolder.listitemcode = (TextView) convertView.findViewById(R.id.listitemcode);
                    mHolder.listitemqty = (EditText) convertView.findViewById(R.id.listitemqty);
                    mHolder.listitemrate = (EditText) convertView.findViewById(R.id.listitemrate);
                    mHolder.listitemtotal = (TextView) convertView.findViewById(R.id.listitemtotal);
                    mHolder.listitemtax = (TextView) convertView.findViewById(R.id.listitemtax);
                    mHolder.labelstock = (TextView)convertView.findViewById(R.id.labelstock);
                    mHolder.labelhsntax = (TextView)convertView.findViewById(R.id.labelhsntax);
                    mHolder.itemLL = (LinearLayout)convertView.findViewById(R.id.itemLL);
                    mHolder.stockvalueLL = (LinearLayout)convertView.findViewById(R.id.stockvalueLL);
                    mHolder.labelnilstock = (TextView)convertView.findViewById(R.id.labelnilstock);
                    mHolder.labelstockunit = (TextView)convertView.findViewById(R.id.labelstockunit);
                    mHolder.pricearrow = (ImageView)convertView.findViewById(R.id.pricearrow);
                    mHolder.listdiscount = (TextView)convertView.findViewById(R.id.listdiscount);
                    mHolder.schemecount = (TextView)convertView.findViewById(R.id.schemecount);
                    mHolder.dummycount = (TextView)convertView.findViewById(R.id.dummycount);
                    mHolder.listitemupp =(TextView)convertView.findViewById(R.id.listitemupp);
                    //Item PRice text change Listner
                    mHolder.listitemqty.addTextChangedListener(new TextWatcher() {
                        public void onTextChanged(CharSequence s, int start, int before,
                                                  int count) {
                            mHolder.listitemtotal.setEnabled(true);
                            mHolder.labelstock.setText(salesItemList.get(position).getStockqty());
                            mHolder.listitemrate.setText(dft.format(Double.parseDouble(salesItemList.get(position).getDumyprice())));
                            if (!(mHolder.listitemqty.getText().toString()).equals("") &&
                                    !(mHolder.listitemqty.getText().toString()).equals(" ")
                                    && !(mHolder.listitemqty.getText().toString()).equals("0")
                                    && !(mHolder.listitemqty.getText().toString()).equals("0.0")
                                    && !(mHolder.listitemqty.getText().toString()).equals(null)
                                    && !(mHolder.listitemrate.getText().toString()).equals("")
                                    && !(mHolder.listitemrate.getText().toString()).equals(0)
                                    && !(mHolder.listitemqty.getText().toString()).equals(".")
                                    && !(mHolder.listitemrate.getText().toString()).equals(".")) {
                                final int pos1 = (Integer) mHolder.listitemrate.getTag();
                                String getqtyval = mHolder.listitemqty.getText().toString();
                                salesItemList.get(pos1).setItemqty(mHolder.listitemqty.getText().toString());
                                if(Double.parseDouble(mHolder.listitemtotal.getText().toString()) >0){
                                    mHolder.listitemtotal.setText("0.00");
                                    mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
                                    mHolder.listitemtotal.setText("0.00");
                                    mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
                                    DataBaseAdapter objdatabaseadapter = null;
                                    Cursor getcartdatas = null;
                                    try {
                                        //Order item details
                                        objdatabaseadapter = new DataBaseAdapter(context);
                                        objdatabaseadapter.open();
                                        String getresult = objdatabaseadapter.DeleteItemInCart(salesItemList.get(pos1).getItemcode());
                                        if (getresult.equals("Success")) {
                                            getcartdatas = objdatabaseadapter.GetSalesItemsCart();
                                            if(getcartdatas.getCount()>0){
                                                totalcartitems.setText(String.valueOf(getcartdatas.getCount()));
                                            }else{
                                                SalesActivity.staticreviewsalesitems.clear();
                                                totalcartitems.setText(String.valueOf("0"));
                                            }
                                            salesItemList.get(pos1).setItemqty("");
                                            salesItemList.get(pos1).setNewprice("");
                                        }
                                    } catch (Exception e) {
                                        DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                        mDbErrHelper.open();
                                        String geterrror = e.toString();
                                        mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                        mDbErrHelper.close();
                                    } finally {
                                        if (objdatabaseadapter != null)
                                            objdatabaseadapter.close();
                                        if (getcartdatas != null)
                                            getcartdatas.close();

                                    }
                                }
                            }
                            else{
                                final int pos1 = (Integer) mHolder.listitemrate.getTag();
                                if(!mHolder.listitemtotal.getText().toString().equals("")
                                        && !mHolder.listitemtotal.getText().toString().equals(null)) {
                                    if (Double.parseDouble(mHolder.listitemtotal.getText().toString()) > 0) {
                                        mHolder.listitemtotal.setText("0.00");
                                        mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
                                        DataBaseAdapter objdatabaseadapter = null;
                                        Cursor getcartdatas = null;
                                        try {
                                            //Order item details
                                            objdatabaseadapter = new DataBaseAdapter(context);
                                            objdatabaseadapter.open();
                                            String getresult = objdatabaseadapter.DeleteItemInCart(salesItemList.get(pos1).getItemcode());
                                            if (getresult.equals("Success")) {
                                                getcartdatas = objdatabaseadapter.GetSalesItemsCart();
                                                if(getcartdatas.getCount()>0){
                                                    totalcartitems.setText(String.valueOf(getcartdatas.getCount()));
                                                }else{
                                                    SalesActivity.staticreviewsalesitems.clear();
                                                    totalcartitems.setText(String.valueOf("0"));
                                                }
                                                salesItemList.get(pos1).setItemqty("");
                                                salesItemList.get(pos1).setNewprice("");
                                                salesitems.get(pos1).setItemqty("");
                                                salesitems.get(pos1).setNewprice("");
                                                mHolder.listitemqty.setText("");

                                            }
                                        } catch (Exception e) {
                                            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                            mDbErrHelper.open();
                                            String geterrror = e.toString();
                                            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                            mDbErrHelper.close();
                                        } finally {
                                            if (objdatabaseadapter != null)
                                                objdatabaseadapter.close();
                                            if (getcartdatas != null)
                                                getcartdatas.close();

                                        }
                                    }
                                }
                            }

                        }
                        public void beforeTextChanged(CharSequence s, int start, int count,
                                                      int after) {
                        }
                        public void afterTextChanged(Editable s) {

                        }
                    });

                    convertView.setTag(mHolder);
                    convertView.setTag(R.id.listitemname, mHolder.listitemname);
                    convertView.setTag(R.id.listitemcode, mHolder.listitemcode);
                    convertView.setTag(R.id.listitemqty, mHolder.listitemqty);
                    convertView.setTag(R.id.listitemrate, mHolder.listitemrate);
                    convertView.setTag(R.id.listitemtotal, mHolder.listitemtotal);
                    convertView.setTag(R.id.listitemtax, mHolder.listitemtax);
                    convertView.setTag(R.id.labelstock, mHolder.labelstock);
                    convertView.setTag(R.id.labelhsntax, mHolder.labelhsntax);
                    convertView.setTag(R.id.labelstockunit, mHolder.labelstockunit);
                    convertView.setTag(R.id.schemecount, mHolder.schemecount);
                    convertView.setTag(R.id.dummycount, mHolder.dummycount);
                    convertView.setTag(R.id.listitemupp, mHolder.listitemupp);

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
            mHolder.labelstock.setTag(position);
            mHolder.labelhsntax.setTag(position);
            mHolder.labelstockunit.setTag(position);
            mHolder.schemecount.setTag(position);
            mHolder.dummycount.setTag(position);
            mHolder.listitemupp.setTag(position);

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
                mHolder.listitemupp.setText(String.valueOf(salesItemList.get(position).getUpp()));

                if(!String.valueOf(salesItemList.get(position).getNewprice()).equals("null")
                        && !String.valueOf(salesItemList.get(position).getNewprice()).equals("")
                        &&!String.valueOf(salesItemList.get(position).getNewprice()).equals(null)) {
                    mHolder.listitemrate.setText(dft.format(Double.parseDouble(salesItemList.get(position).getNewprice())));

                }else{
                    //if(!getnoofdigits.equals("")) {
                    mHolder.listitemrate.setText(dft.format(Double.parseDouble("0")));
                    //}else{
                    //    mHolder.listitemrate.setText(String.valueOf(Double.parseDouble("0")));
                    //}
                }
                mHolder.listitemname.setTextColor(Color.parseColor(salesItemList.get(position).getColourcode()));

                if(Double.parseDouble(salesItemList.get(position).getOldprice()) >
                        Double.parseDouble(salesItemList.get(position).getNewprice()) ){
                    mHolder.pricearrow.setImageResource(R.drawable.ic_arrow_downward);
                }else{
                    mHolder.pricearrow.setImageResource(R.drawable.ic_arrow_upward_black_24dp);
                }
                mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
                //Stock
                if(salesItemList.get(position).getStockqty().equals("0") || salesItemList.get(position).getStockqty().equals("null")
                        ||  salesItemList.get(position).getStockqty().equals(null) || salesItemList.get(position).getStockqty().equals("") ||
                        Double.parseDouble(salesItemList.get(position).getStockqty())<=0) {
                    mHolder.labelnilstock.setVisibility(View.VISIBLE);
                    mHolder.labelnilstock.setText("Nil Stk");
                    mHolder.stockvalueLL.setVisibility(View.GONE);
                    mHolder.labelstock.setText("0");
                    mHolder.labelstock.setBackgroundColor(getResources().getColor(R.color.red));
                    mHolder.itemLL.setBackgroundColor(getResources().getColor(R.color.lightgray));
                    mHolder.labelstock.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    mHolder.listitemrate.setBackgroundResource(R.drawable.editbackgroundgray);
                    mHolder.listitemqty.setBackgroundResource(R.drawable.editbackgroundgray);
                    mHolder.listitemrate.setEnabled(false);
                    mHolder.listitemqty.setEnabled(false);

                }else{
                    mHolder.stockvalueLL.setVisibility(View.VISIBLE);
                    mHolder.labelnilstock.setVisibility(View.GONE);
                    mHolder.labelstock.setText(salesItemList.get(position).getStockqty());
                    mHolder.labelstockunit.setText(" "+salesItemList.get(position).getUnitname());
                    mHolder.labelstock.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    mHolder.labelstock.setBackground(ContextCompat.getDrawable(context, R.color.green));
                    //mHolder.itemLL.setBackgroundColor(getResources().getColor(R.color.white));
                    mHolder.listitemrate.setBackgroundResource(R.drawable.editbackground);
                    mHolder.listitemqty.setBackgroundResource(R.drawable.editbackground);
                    mHolder.listitemrate.setEnabled(true);
                    mHolder.listitemqty.setEnabled(true);
                }
                if (salesItemList.get(position).getAllownegativestock().equals("no")) {
                    if (salesItemList.get(position).getItemcategory().equals("child")) {
                        final int pos = (Integer) mHolder.listitemqty.getTag();
                        double getparentstock = 0.0;
                        DataBaseAdapter objdatabaseadapter = null;
                        Cursor getStockCur = null;
                        try {
                            //Get Stock for parent item
                            objdatabaseadapter = new DataBaseAdapter(context);
                            objdatabaseadapter.open();
                            getStockCur = objdatabaseadapter.GetStockForItem(salesItemList.get(pos).getParentitemcode());
                            if (getStockCur.getCount() > 0) {
                                // for (int i = 0; i < getStockCur.getCount(); i++) {
                                getparentstock = getStockCur.getDouble(0);
                                //}
                            }
                            if (getparentstock > 0) {
                                isfreestock = "";
                                getstaticparentitemcode = salesItemList.get(pos).getParentitemcode();
                                getstaticchilditemname = salesItemList.get(pos).getItemnametamil();
                                getstaticchildunitname = salesItemList.get(pos).getUnitname();
                                isnillstockconversion = true;

                                mHolder.listitemname.setBackgroundColor(getResources().getColor(R.color.yellow));
                                mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
                            }/*else{
                                //isnillstockconversion = false;
                            }*/
                        }catch (Exception e) {
                            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                            mDbErrHelper.open();
                            String geterrror = e.toString();
                            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            mDbErrHelper.close();
                        } finally {
                            if (objdatabaseadapter != null)
                                objdatabaseadapter.close();
                            if (getStockCur != null)
                                getStockCur.close();
                        }

                    }else{
                        // isnillstockconversion=false;
                    }
                }else{
                    isnillstockconversion=false;
                }


                mHolder.labelhsntax.setText(salesItemList.get(position).getHsn() +" @ "+salesItemList.get(position).getTax() +"%");


                //Set Stock Conversion Qty for child item
                if(!getstaticgetchildqty.equals("") && !getstaticgetchildqty.equals(null)
                        && !getstaticgetchildqty.equals("0")){
                    if(salesItemList.get(position).getItemcode().equals(getstaticchilditemcode)){
                        if(Double.parseDouble(salesItemList.get(position).getStockqty())
                                > Double.parseDouble(getstaticgetchildqty)){
                            mHolder.listitemqty.setText(getstaticgetchildqty);
                            getstaticgetchildqty="";
                        }else{
                            mHolder.listitemqty.setText("");
                        }

                    }
                }


                DataBaseAdapter objdataschemabaseadapter= new DataBaseAdapter(context);
                Cursor schemebusinesstype = null;
                try{
                    ItemApplyScheme =false;
                    if(SalesActivity.radio_cash.isChecked()){
                        getpaymenttypename="CASH";
                        getpaymenttypecode = "1";

                    }else{
                        getpaymenttypename="CREDIT";
                        getpaymenttypecode = "2";
                    }
                    objdataschemabaseadapter.open();
                    schemebusinesstype =objdataschemabaseadapter.getschemebusinesstype(salesItemList.get(position).getItemcode());

                    if(schemebusinesstype != null && schemebusinesstype.getCount() > 0) {
                        getSchemebusinesstype = schemebusinesstype.getString(0);
                        getSchemebilltypecode = schemebusinesstype.getString(3);


                        //String date = getSchemebilltypecode;
                        ar = getSchemebilltypecode.split("[,]");

                        for (int i = 0; i < ar.length; i++) {
                            //Toast.makeText(context,ar[i],Toast.LENGTH_SHORT).show();
                            if (ar[i].equals(getpaymenttypecode)) {
                                ItemApplyScheme = true;
                            }
                        }
                        Log.d("Scheme businesstype :", getSchemebusinesstype + " - " + salesItemList.get(position).getItemcode());
                    }
                }catch (Exception e){
                    Log.d("Scheme businesstype error (line : 1662) :",e.toString());
                }finally{
                    if (objdataschemabaseadapter != null)
                        objdataschemabaseadapter.close();
                    if(schemebusinesstype !=null)
                        schemebusinesstype.close();

                }
//          free item then donot alloe to sales that item
                if(salesItemList.get(position).getItemtype().equals("2")){
                    mHolder.listitemtotal.setEnabled(false);
                    mHolder.listitemqty.setBackgroundResource(R.drawable.editbackgroundgray);
                    mHolder.listitemqty.setEnabled(false);
                }
//                else{
//                    mHolder.listitemtotal.setEnabled(true);
//                    mHolder.listitemqty.setBackgroundResource(R.drawable.editbackground);
//                    mHolder.listitemqty.setEnabled(true);
//                }
                if (getschemeapplicable.equals("yes")) {
                    if(ItemApplyScheme) {
                        if (getSchemebusinesstype.contains("1") || getSchemebusinesstype.contains("3")) {
                            if (Double.parseDouble(salesItemList.get(position).getRatecount()) > 0) {
                                mHolder.schemecount.setVisibility(View.VISIBLE);
                                mHolder.schemecount.setText("R");
                                mHolder.dummycount.setVisibility(View.GONE);
                                //set the scheme apply status for each items in list
                                salesItemList.get(position).setApplyratescheme("yes");
                            }else{
                                //set the scheme apply status for each items in list
                                salesItemList.get(position).setApplyratescheme("no");
                            }
                            if (Double.parseDouble(salesItemList.get(position).getFreecount()) > 0) {
                                mHolder.schemecount.setVisibility(View.VISIBLE);
                                mHolder.schemecount.setText("F");
                                mHolder.dummycount.setVisibility(View.GONE);
                                //set the scheme apply status for each items in list
                                salesItemList.get(position).setApplyitemscheme("yes");
                            }else{
                                //set the scheme apply status for each items in list
                                salesItemList.get(position).setApplyitemscheme("no");
                            }
                        }
                    }
                }else{
                    mHolder.dummycount.setVisibility(View.VISIBLE);
                    mHolder.schemecount.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                Log.i("Item value", e.toString());
            }

            //Allow price Edit
            if(!Utilities.isNullOrEmpty(salesItemList.get(position).getAllowpriceedit()) && salesItemList.get(position).getAllowpriceedit().equals("yes") &&
                    !Utilities.isNullOrEmpty(salesItemList.get(position).getRouteallowpricedit()) && salesItemList.get(position).getRouteallowpricedit().equals("yes")){
                mHolder.listitemrate.setBackgroundResource(R.drawable.editbackground);
                mHolder.listitemrate.setEnabled(true);
            }else{
                mHolder.listitemrate.setBackgroundResource(R.drawable.editbackgroundgray);
                mHolder.listitemrate.setEnabled(false);
            }

            //Set Discount background
            mHolder.listdiscount.setBackgroundColor(getResources().getColor(R.color.lightbiscuit));
            mHolder.listdiscount.setText("");


            //Set Quantity,price
            if (!salesItemList.get(position).getItemqty().equals("") &&
                    !salesItemList.get(position).getItemqty().equals("0")
                    && !salesItemList.get(position).getItemqty().equals(null) &&  isopenshowpopup) {
                DataBaseAdapter objdatabaseadapter = null;
                Cursor getItemCur = null;
                try {
                    //Get Stock for parent item
                    objdatabaseadapter = new DataBaseAdapter(context);
                    objdatabaseadapter.open();
                    getItemCur = objdatabaseadapter.GetCartItemQtyAndPrice(salesItemList.get(position).getItemcode());
                    if (getItemCur.getCount() > 0) {
                        mHolder.listitemrate.setText(String.valueOf(getItemCur.getString(1)));
                        mHolder.listitemqty.setText(String.valueOf(getItemCur.getString(0)));
                        mHolder.listitemtotal.callOnClick();
                    }

                }catch (Exception e) {
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                } finally {
                    if (objdatabaseadapter != null)
                        objdatabaseadapter.close();
                    if (getItemCur != null)
                        getItemCur.close();
                }


            }

            //Item PRice text change Listner
            mHolder.listitemrate.addTextChangedListener(new TextWatcher() {
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
                            && !(mHolder.listitemqty.getText().toString()).equals(null)
                            && !(mHolder.listitemrate.getText().toString()).equals("")
                            && !(mHolder.listitemrate.getText().toString()).equals(0)
                            && !(mHolder.listitemqty.getText().toString()).equals(".")
                            && !(mHolder.listitemrate.getText().toString()).equals(".")) {
                        final int pos1 = (Integer) mHolder.listitemrate.getTag();
                        String getqtyval = salesItemList.get(pos1).getItemqty();
                        // mHolder.listitemqty.setText(getqtyval);
                        //  mHolder.listitemtotal.callOnClick();
                    }/*else{
                        final int pos1 = (Integer) mHolder.listitemrate.getTag();
                        //mHolder.listitemtotal.setText("0.00");
                        //Set Discount background
                        mHolder.listdiscount.setBackgroundColor(getResources().getColor(R.color.white));
                        mHolder.listdiscount.setText("");
                        salesItemList.get(pos1).setSubtotal(mHolder.listitemtotal.getText().toString());
                    }*/
                }
            });



            /**********************************************************************************/
            //ONCHANGE QUANTITY EVENT

            mHolder.listitemtotal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mHolder.listitemtotal.setEnabled(false);
                    isfreestock = "";
                    itemschemeapplicable="not applicable";
                    //Delcare variables
                    double getminqty = 0;
                    double getdiscount = 0;
                    double getparentstock = 0;
                    boolean addedqty = false;
                    final int pos = (Integer) mHolder.listitemtotal.getTag();
                    mHolder.labelstock.setText(salesItemList.get(pos).getStockqty());
                    String gbllistitemcode=mHolder.listitemcode.getText().toString();


                    if((mHolder.labelstock.getText().toString()).equals("0")  ) {
//                        mHolder.listitemname.callOnClick();
                        if(salesItemList.get(pos).getItemcategory().equals("child")){
                            gblitemcount=gblitemcount+1;
                        }
                        stockconvertion(isnillstockconversion,salesItemList.get(pos),position,mHolder
                                ,isfreestock,getstaticparentitemcode,getstaticchilditemname,
                                getstaticchildunitname,isparentopen,dft,getstaticchildcode);
                        return;

                    }

                    if (!(mHolder.listitemqty.getText().toString()).equals("") &&
                            !(mHolder.listitemqty.getText().toString()).equals(" ")
                            && !(mHolder.listitemqty.getText().toString()).equals("0")
                            && !(mHolder.listitemqty.getText().toString()).equals("0.0")
                            && !(mHolder.listitemqty.getText().toString()).equals(null)
                            && !(mHolder.listitemqty.getText().toString()).equals(".")
                            && Double.parseDouble(mHolder.listitemqty.getText().toString()) > 0) {
                        //mHolder.listitemname.setBackgroundColor(getResources().getColor(R.color.white));
                        // isparentopen = "";




//||
//                                    !(Integer.parseInt(mHolder.listitemqty.getText().toString())>=
//                                            Integer.parseInt(mHolder.listitemupp.getText().toString()))
//                            if( (!(Integer.parseInt(mHolder.listitemqty.getText().toString())>=
//                                    Integer.parseInt(mHolder.listitemupp.getText().toString()))
//                                    && (Integer.parseInt(mHolder.labelstock.getText().toString())
//                                    <Integer.parseInt(mHolder.listitemqty.getText().toString())))
//                                           ) {
//
//                            }
                        if( (!(Integer.parseInt(mHolder.listitemqty.getText().toString())>=
                                Integer.parseInt(mHolder.listitemupp.getText().toString()))
                                && (Integer.parseInt(mHolder.labelstock.getText().toString())
                                <Integer.parseInt(mHolder.listitemqty.getText().toString()))) ||
                                (!(Integer.parseInt(mHolder.listitemqty.getText().toString())>=
                                        Integer.parseInt(mHolder.listitemupp.getText().toString()))
                                        && (mHolder.labelnilstock.getText().toString()).equals("Nil Stk")) ) {
                            if(salesItemList.get(pos).getItemcategory().equals("child")){
                                gblitemcount=gblitemcount+1;
                            }
                            stockconvertion(isnillstockconversion,salesItemList.get(pos),position,mHolder
                                    ,isfreestock,getstaticparentitemcode,getstaticchilditemname,
                                    getstaticchildunitname,isparentopen,dft,getstaticchildcode);


                        }
                        if(isstockcoversionpopupopen){
                            return;
                        }
                        /***********CHECK STOCK TRANSACTION******************/
                        if (salesItemList.get(pos).getAllownegativestock().equals("no")) {
                            if (Double.parseDouble(mHolder.listitemqty.getText().toString()) >
                                    Double.parseDouble(mHolder.labelstock.getText().toString())) {
                                if (salesItemList.get(pos).getItemcategory().equals("child")) {
                                    DataBaseAdapter objdatabaseadapter = null;
                                    Cursor getStockCur = null;
                                    try {
                                        //Get Stock for parent item
                                        objdatabaseadapter = new DataBaseAdapter(context);
                                        objdatabaseadapter.open();
                                        getStockCur = objdatabaseadapter.GetStockForItem(salesItemList.get(pos).getParentitemcode());
                                        if (getStockCur.getCount() > 0) {
                                            // for (int i = 0; i < getStockCur.getCount(); i++) {
                                            getparentstock = getStockCur.getDouble(0);
                                            //}
                                        }
                                        if (getparentstock > 0) {
                                            isfreestock = "";
                                            getstaticparentitemcode = salesItemList.get(pos).getParentitemcode();
                                            getstaticchilditemname = salesItemList.get(pos).getItemnametamil();
                                            getstaticchildunitname = salesItemList.get(pos).getUnitname();
                                            isparentopen = "yes";
                                            Toast toast = Toast.makeText(getApplicationContext(),"Insufficient stock..", Toast.LENGTH_LONG);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
                                            mHolder.listitemname.setBackgroundColor(getResources().getColor(R.color.yellow));
                                            mHolder.listitemqty.setText("");
                                            mHolder.listitemtotal.setText("0.00");
                                            salesItemList.get(pos).setSubtotal("0.00");
                                            mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));

                                            String getresult=DeleteItemCart(salesItemList.get(pos).getItemcode());
                                            if(getresult.equals("Success")) {
                                                salesItemList.get(pos).setItemqty("");
                                                salesItemList.get(pos).setNewprice("");
                                            }

                                        } else {
                                            //mHolder.listitemname.setBackgroundColor(getResources().getColor(R.color.white));
                                            Toast toast = Toast.makeText(getApplicationContext(),"Insufficient stock..", Toast.LENGTH_LONG);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
                                            mHolder.listitemtotal.setText("0.00");
                                            mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
                                            mHolder.listitemrate.setText(dft.format(Double.parseDouble(salesItemList.get(pos).getDumyprice())));
                                            //Toast.makeText(getApplicationContext(), "Insufficient stock..", Toast.LENGTH_SHORT).show();
                                            mHolder.listitemqty.setText("");
                                            String getresult=DeleteItemCart(salesItemList.get(pos).getItemcode());
                                            if(getresult.equals("Success")) {
                                                salesItemList.get(pos).setItemqty("");
                                                salesItemList.get(pos).setNewprice("");
                                            }
                                        }
                                    } catch (Exception e) {
                                        DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                        mDbErrHelper.open();
                                        String geterrror = e.toString();
                                        mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                        mDbErrHelper.close();
                                    } finally {
                                        if (objdatabaseadapter != null)
                                            objdatabaseadapter.close();
                                        if (getStockCur != null)
                                            getStockCur.close();
                                    }
                                } else {
                                    Toast toast = Toast.makeText(getApplicationContext(),"Insufficient stock..", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    mHolder.listitemtotal.setText("0.00");
                                    mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
                                    mHolder.listitemrate.setText(dft.format(Double.parseDouble(salesItemList.get(pos).getDumyprice())));
                                    //Toast.makeText(getApplicationContext(), "Insufficient stock..", Toast.LENGTH_SHORT).show();
                                    mHolder.listitemqty.setText("");
                                    String getresult=DeleteItemCart(salesItemList.get(pos).getItemcode());
                                    if(getresult.equals("Success")) {
                                        salesItemList.get(pos).setItemqty("");
                                        salesItemList.get(pos).setNewprice("");
                                    }
                                }
                            }
                        }
                        /***********END CHECK STOCK TRANSACTION******************/
                        if(salesItemList.get(pos).getItemcategory().equals("child")){
                            DataBaseAdapter minimumstockqtyadapter = null;
                            try{
                                minimumstockqtyadapter = new DataBaseAdapter(context);
                                minimumstockqtyadapter.open();
                                double listitemqty=0.0;

                                // String Totalsales=minimumstockqtyadapter.getTotalSalesQuantity(salesItemList.get(pos).getItemcode());
                                String MinimumSalesQty=salesItemList.get(pos).getMinsalesqty();

                                   /* if(!MinimumSalesQty.equals("") && !MinimumSalesQty.equals("null") && !MinimumSalesQty.equals(null) ){
                                        if(Double.parseDouble(MinimumSalesQty)>0){
                                            if(!Totalsales.equals("") && !Totalsales.equals("null") && !Totalsales.equals(null) ){
                                                //if(Double.parseDouble(Totalsales)>0){

                                                    if(Double.parseDouble(Totalsales) >= Double.parseDouble(salesItemList.get(pos).getMinsalesqty())){
                                                        Toast toast = Toast.makeText(getApplicationContext(),"Item "+salesitems.get(pos).getItemnametamil()+" is reach the minimum sales quanitity", Toast.LENGTH_LONG);
                                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                                        toast.show();
                                                        mHolder.listitemqty.setText("");
                                                        return;
                                                    }
                                                    double listitemqty=0.0;
                                                    if(!mHolder.listitemqty.getText().toString().equals("") && !mHolder.listitemqty.getText().toString().equals("null") &&
                                                            !mHolder.listitemqty.getText().toString().equals(null) ) {
                                                        listitemqty = Double.parseDouble(mHolder.listitemqty.getText().toString());
                                                    }
                                                    if(Double.parseDouble(Totalsales) + listitemqty > Double.parseDouble(salesItemList.get(pos).getMinsalesqty())){
                                                        Toast toast = Toast.makeText(getApplicationContext(),"Item "+salesitems.get(pos).getItemnametamil()+" total sales quanitity is not greater then "+salesItemList.get(pos).getMinsalesqty(), Toast.LENGTH_LONG);
                                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                                        toast.show();
                                                        mHolder.listitemqty.setText("");
                                                        return;
                                                    }
                                                //}
                                            }
                                        }
                                    }*/
                                if(!mHolder.listitemqty.getText().toString().equals("") && !mHolder.listitemqty.getText().toString().equals("null") &&
                                        !mHolder.listitemqty.getText().toString().equals(null) && !mHolder.listitemqty.getText().toString().equals("0") ) {
                                    listitemqty = Double.parseDouble(mHolder.listitemqty.getText().toString());
                                }
                                if(Double.parseDouble(MinimumSalesQty)!=0) {
                                    double modparentchildqty = listitemqty % Double.parseDouble(MinimumSalesQty);
                                    //                                if(listitemqty < Double.parseDouble(MinimumSalesQty)){
                                    if (modparentchildqty != 0.00 || modparentchildqty != 0.0 || modparentchildqty != 0) {
                                        //                                    //greater then
//                                        Toast toast = Toast.makeText(getApplicationContext(),"Item "+salesitems.get(pos).getItemnametamil()+" minimum sales quanitity is "+salesItemList.get(pos).getMinsalesqty() +". So please enter value that multiply of  "+salesItemList.get(pos).getMinsalesqty(), Toast.LENGTH_LONG);
                                        Toast toast = Toast.makeText(getApplicationContext(), "You can sell only multiples of " + salesItemList.get(pos).getMinsalesqty() + ". So please enter valid qty", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                        mHolder.listitemqty.setText("");
                                        return;
                                    }
                                }


                            }catch (Exception e){
                                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                mDbErrHelper.open();
                                String geterrror = e.toString();
                                mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName() + " - MinimumSalesQty", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                mDbErrHelper.close();
                            }finally {
                                if (minimumstockqtyadapter != null) {
                                    minimumstockqtyadapter.close();
                                }
                            }
                        }


                            /*if(Double.parseDouble(salesItemList.get(pos).getStockqty()) <= Double.parseDouble(salesItemList.get(pos).getMinstockqty())){
                                Toast toast = Toast.makeText(getApplicationContext(),"Item "+salesitems.get(pos).getItemnametamil()+" is reach the minimum stock quanitity", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                mHolder.listitemqty.setText("");
                                return;
                            }
                            double listitemqty=0.0;
                            if(!mHolder.listitemqty.getText().toString().equals("") && !mHolder.listitemqty.getText().toString().equals("null") &&
                                   !mHolder.listitemqty.getText().toString().equals(null) ) {
                                listitemqty = Double.parseDouble(mHolder.listitemqty.getText().toString());
                            }

                            if((Double.parseDouble(salesItemList.get(pos).getStockqty()) - listitemqty)
                                    < Double.parseDouble(salesItemList.get(pos).getMinstockqty())){
                                Toast toast = Toast.makeText(getApplicationContext(),"Item "+salesitems.get(pos).getItemnametamil()+" item quantity should not be less than or equal to "+
                                        salesitems.get(pos).getMinstockqty(), Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                mHolder.listitemqty.setText("");
                                return;
                            }*/


                        if (!mHolder.listitemqty.getText().toString().equals("")) {
                            if (Double.parseDouble(mHolder.listitemqty.getText().toString()) >0  ) {




                                //<=Double.parseDouble(mHolder.labelstock.getText().toString())
                                DecimalFormat dffor = new DecimalFormat("0.00");
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

//                                    /*********SCHEME DETAILS*************/
//                                    if (getschemeapplicable.equals("yes")) {
//                                        if(getSchemebusinesstype.equals("1") || getSchemebusinesstype.equals("3")){
//                                            Log.w("getSchemebusinesstype",getSchemebusinesstype);
//                                        //Get Scheme Rate based
//                                        DataBaseAdapter objdatabaseadapter = null;
//                                        Cursor getschemeCur = null;
//                                        try {
//                                            //Scheme Functionality
//                                            //check the item have a free rate count and apply scheme status
//                                            if(Double.parseDouble(salesItemList.get(position).getRatecount()) >0 && salesItemList.get(pos).getApplyratescheme().equals("yes")) {
//                                                objdatabaseadapter = new DataBaseAdapter(context);
//                                                objdatabaseadapter.open();
//                                                getschemeCur = objdatabaseadapter.GetSchemeFORItemDB(salesItemList.get(pos).getItemcode(),
//                                                        preferenceMangr.pref_getString("getroutecode"), mHolder.listitemqty.getText().toString());
//                                                if (getschemeCur.getCount() > 0) {
//                                                    //  for (int i = 0; i < getschemeCur.getCount(); i++) {
//                                                    getminqty = getschemeCur.getDouble(0);
//                                                    getdiscount = getschemeCur.getDouble(1);
//                                                    // }
//                                                }
//                                                schemeratestatus="yes";
//                                            }
//                                        } catch (Exception e) {
//                                            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
//                                            mDbErrHelper.open();
//                                            String geterrror = e.toString();
//                                            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
//                                            mDbErrHelper.close();
//                                        } finally {
//                                            if (objdatabaseadapter != null)
//                                                objdatabaseadapter.close();
//                                            if (getschemeCur != null)
//                                                getschemeCur.close();
//                                        }
//
//                                        //Get Item based Free Item
//                                        //GetFreeItem(salesItemList.get(pos).getItemcode(),mHolder.listitemqty.getText().toString());
//
//                                        /********FREE ITEM******************/
//
//                                        String getitemcode = salesItemList.get(pos).getItemcode();
//                                        String getqty = mHolder.listitemqty.getText().toString();
//                                        //Get Free Item variable
//                                        String getfreeitemname = "";
//                                        //Get free and purchase item code
//                                        String getpurchaseitemcode = "";
//                                        String getpurchaseqty = "";
//                                        String getfreeitemcode = "";
//                                        String getfreeqty = "";
//                                        String getfreeitemschemetype ="";
//
//                                        String getallownegativestock = "";
//                                        double getfreestockitem = 0;
//                                        String getitemcategory = "";
//                                        String getparentcode = "";
//                                        String getchilditemname = "";
//                                        String getchildunitname = "";
//
//                                        //Get Item based Free Item
//                                        DataBaseAdapter objdatabaseadapterfree = null;
//                                        Cursor getitemschemeCur = null;
//                                        Cursor getitemschemetype = null;
//                                        Cursor getFreeStock = null;
//                                        try {
//                                            //Scheme Functionality
//                                            objdatabaseadapterfree = new DataBaseAdapter(context);
//                                            objdatabaseadapterfree.open();
//                                            getitemschemeCur = objdatabaseadapterfree.GetSchemeFORFreeItemDB(getitemcode, preferenceMangr.pref_getString("getroutecode"));
//                                            getitemschemetype = objdatabaseadapterfree.GetOrderSchemeType(getitemcode, preferenceMangr.pref_getString("getroutecode"));
//                                            //get item scheme type
//                                            if(getitemschemetype.getCount()>0){
//                                                getfreeitemschemetype = getitemschemetype.getString(0);
//
//                                                 if(getfreeitemschemetype.equals("rate")){
//                                                    freeitemstatus="freerate";
//                                                    schemeitemdiscount = String.valueOf(getitemschemetype.getDouble(2));
//                                                }
//                                            }
//                                            if (getitemschemeCur.getCount() > 0) {
//                                                //freeitems.clear();
//                                                // for (int i = 0; i < getitemschemeCur.getCount(); i++) {
//                                                getpurchaseitemcode = getitemschemeCur.getString(0);
//                                                getpurchaseqty = getitemschemeCur.getString(1);
//                                                getfreeitemcode = getitemschemeCur.getString(2);
//                                                getfreeqty = getitemschemeCur.getString(3);
//
//
//                                                // }
//                                                if (Double.parseDouble(getpurchaseqty) <=
//                                                        Double.parseDouble(getqty)) {
//                                                    try {
//                                                        //Get Stock for parent item
//                                                        String getmanualfreeitemname = objdatabaseadapterfree.GetFreeitemname(getfreeitemcode);
//                                                        getfreeitemname = getmanualfreeitemname;
//                                                        getFreeStock = objdatabaseadapterfree.GetPurchaseItems(getfreeitemcode, preferenceMangr.pref_getString("getroutecode"), LoginActivity.getareacode);
//                                                        if (getFreeStock.getCount() > 0) {
//                                                            for (int i = 0; i < getFreeStock.getCount(); i++) {
//                                                                //getfreeitemstock = getFreeStock.getDouble(0);
//                                                                getallownegativestock = getFreeStock.getString(14);
//                                                                getfreestockitem = getFreeStock.getDouble(16);
//                                                                getitemcategory = getFreeStock.getString(11);
//                                                                getparentcode = getFreeStock.getString(12);
//                                                                getchilditemname = getFreeStock.getString(5);
//                                                                getchildunitname = getFreeStock.getString(17);
//
//                                                            }
//                                                        }
//
//                                                        //Calculate Qty with total qty
//                                                        Double getactualqty = Double.parseDouble(getqty);
//                                                        int getfreeqtyval = (int) (getactualqty / Double.parseDouble(getpurchaseqty));
//                                                        int getactualqtyvalue = (int) (getfreeqtyval * Double.parseDouble(getfreeqty));
//                                                        String getcartqty = objdatabaseadapterfree.GetCartItemStock(getfreeitemcode);
//                                                        if (getpurchaseitemcode.equals(getfreeitemcode)) {
//                                                            if (Double.parseDouble(mHolder.labelstock.getText().toString()) < (Double.parseDouble(String.valueOf(getactualqtyvalue)) + Double.parseDouble(getqty))) {
//                                                                Toast toast = Toast.makeText(getApplicationContext(), "Insufficient stock for " + getfreeitemname, Toast.LENGTH_LONG);
//                                                                toast.setGravity(Gravity.CENTER, 0, 0);
//                                                                toast.show();
//                                                                mHolder.listitemtotal.setText("0.00");
//                                                                mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
//                                                                return;
//                                                            }
//                                                        }
//
//                                                        //check the item have a free item count and apply scheme status
//                                                        if(Double.parseDouble(salesItemList.get(position).getFreecount()) > 0 && salesItemList.get(pos).getApplyitemscheme().equals("yes")) {
//                                                            if (getfreestockitem >= Double.parseDouble(String.valueOf(getactualqtyvalue))) {
//                                                                getFreeStock = null;
//                                                                //Get Stock for parent item
//                                                                getFreeStock = objdatabaseadapterfree.GetPurchaseItems(getfreeitemcode, preferenceMangr.pref_getString("getroutecode"), LoginActivity.getareacode);
//
//                                                                if (getFreeStock.getCount() > 0) {
//                                                                    for (int i = 0; i < getFreeStock.getCount(); i++) {
//                                                                        double getsubtotal = Double.parseDouble(getFreeStock.getString(20)) * getactualqtyvalue;
//
//                                                                        for (int j = 0; j < freeitems.size(); j++) {
//                                                                            if (getpurchaseitemcode.equals(freeitems.get(j).getPurchaseitemcode()) &&
//                                                                                    getfreeitemcode.equals(freeitems.get(j).getFreeitemcode())) {
//                                                                                freeitems.remove(j);
//                                                                            }
//                                                                        }
//                                                                        freeitems.add(new SalesItemDetails(getFreeStock.getString(0), getFreeStock.getString(1),
//                                                                                getFreeStock.getString(2)
//                                                                                , getFreeStock.getString(3), getFreeStock.getString(4),
//                                                                                getFreeStock.getString(5), getFreeStock.getString(6),
//                                                                                getFreeStock.getString(7)
//                                                                                , getFreeStock.getString(8), getFreeStock.getString(9), getFreeStock.getString(10)
//                                                                                , getFreeStock.getString(11), getFreeStock.getString(12)
//                                                                                , getFreeStock.getString(13), getFreeStock.getString(14), getFreeStock.getString(15)
//                                                                                , getFreeStock.getString(16), getFreeStock.getString(17)
//                                                                                , getFreeStock.getString(18), getFreeStock.getString(19),
//                                                                                getFreeStock.getString(20)
//                                                                                , getFreeStock.getString(21), getFreeStock.getString(22)
//                                                                                , getFreeStock.getString(23), String.valueOf(getactualqtyvalue), String.valueOf(getsubtotal),
//                                                                                getFreeStock.getString(24), String.valueOf(getsubtotal),
//                                                                                "freeitem", getpurchaseitemcode, getfreeitemcode, getFreeStock.getString(20), "", "",getFreeStock.getString(28),""));
//
//                                                                    }
//                                                                }
//                                                            } else {
//                                                           /* Toast toast = Toast.makeText(getApplicationContext(),"Insufficient stock for " + getfreeitemname + " this free item", Toast.LENGTH_LONG);
//                                                            toast.setGravity(Gravity.CENTER, 0, 0);
//                                                            toast.show();
//                                                            mHolder.listitemtotal.setText("0.00");
//                                                            mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
//                                                            mHolder.listitemrate.setText(dft.format(Double.parseDouble(salesItemList.get(pos).getDumyprice())));
//                                                            return;*/
//
//                                                                //freeitems.clear();
//                                                                if (getallownegativestock.equals("no")) {
//                                                                    // if (Double.parseDouble(getqty) > getfreestockitem) {
//                                                                    if (getitemcategory.equals("child")) {
//                                                                        DataBaseAdapter objdatabaseadapter1 = null;
//                                                                        Cursor getStockCur = null;
//                                                                        try {
//                                                                            //Get Stock for parent item
//                                                                            objdatabaseadapter1 = new DataBaseAdapter(context);
//                                                                            objdatabaseadapter1.open();
//                                                                            getStockCur = objdatabaseadapter1.GetStockForItem(getparentcode);
//                                                                            String getcartparentqty = objdatabaseadapter.GetCartItemStock(getparentcode);
//                                                                            if (getStockCur.getCount() > 0) {
//                                                                                for (int i = 0; i < getStockCur.getCount(); i++) {
//                                                                                    getparentstock = getStockCur.getDouble(0);
//
//                                                                                }
//                                                                                getparentstock = getparentstock - (Double.parseDouble(getcartparentqty));
//                                                                            }
//
//                                                                            if (getparentstock > 0) {
//
//                                                                                //> Double.parseDouble(String.valueOf(getactualqtyvalue))
//
//                                                                                Toast toast = Toast.makeText(getApplicationContext(), "Insufficient stock for " + getfreeitemname + " this free item", Toast.LENGTH_LONG);
//                                                                                toast.setGravity(Gravity.CENTER, 0, 0);
//                                                                                toast.show();
//                                                                                mHolder.listitemtotal.setText("0.00");
//                                                                                mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
//                                                                                mHolder.listitemrate.setText(dft.format(Double.parseDouble(salesItemList.get(pos).getDumyprice())));
//                                                                                return;
//                                                                                //Stock Conversion
//                                                                                /*isfreestock = "yes";
//                                                                                getstaticchildcode = getfreeitemcode;
//                                                                                getstaticparentitemcode = getparentcode;
//                                                                                getstaticchilditemname = getchilditemname;
//                                                                                getstaticchildunitname = getchildunitname;
//                                                                                isparentopen = "yes";
//                                                                                mHolder.listitemname.setBackgroundColor(getResources().getColor(R.color.yellow));
//                                                                                mHolder.listitemqty.setText("");
//                                                                                mHolder.listitemtotal.setText("0.00");
//                                                                                salesItemList.get(pos).setSubtotal("0.00");
//                                                                                mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));*/
//
//                                                                            } else {
//                                                                                Toast toast = Toast.makeText(getApplicationContext(), "Insufficient stock for " + getfreeitemname, Toast.LENGTH_LONG);
//                                                                                toast.setGravity(Gravity.CENTER, 0, 0);
//                                                                                toast.show();
//                                                                                mHolder.listitemtotal.setText("0.00");
//                                                                                mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
//                                                                                mHolder.listitemrate.setText(dft.format(Double.parseDouble(salesItemList.get(pos).getDumyprice())));
//
//                                                                            }
//                                                                        } catch (Exception e) {
//                                                                            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
//                                                                            mDbErrHelper.open();
//                                                                            String geterrror = e.toString();
//                                                                            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
//                                                                            mDbErrHelper.close();
//                                                                        } finally {
//                                                                            if (objdatabaseadapter1 != null)
//                                                                                objdatabaseadapter1.close();
//                                                                            if (getStockCur != null)
//                                                                                getStockCur.close();
//                                                                        }
//                                                                    } else {
//                                                                        Toast toast = Toast.makeText(getApplicationContext(), "Insufficient stock for " + getfreeitemname + " this free item", Toast.LENGTH_LONG);
//                                                                        toast.setGravity(Gravity.CENTER, 0, 0);
//                                                                        toast.show();
//                                                                        mHolder.listitemtotal.setText("0.00");
//                                                                        mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
//                                                                        mHolder.listitemrate.setText(dft.format(Double.parseDouble(salesItemList.get(pos).getDumyprice())));
//
//                                                                    }
//                                                                    //}
//                                                                }
//                                                            }
//                                                        }
//
//                                                        //scheme process end
//                                                    } catch (Exception e) {
//                                                        DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
//                                                        mDbErrHelper.open();
//                                                        String geterrror = e.toString();
//                                                        mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
//                                                        mDbErrHelper.close();
//                                                    }
//                                                } /*else {
//                                                 *//*  for (int k = 0; k < freeitems.size(); k++) {
//                                                        for (int l = 0; l < staticreviewsalesitems.size(); l++) {
//                                                            if (freeitems.get(k).getPurchaseitemcode().equals(staticreviewsalesitems.get(l).getPurchaseitemcode()) &&
//                                                                    freeitems.get(k).getFreeitemcode().equals(staticreviewsalesitems.get(l).getFreeitemcode())) {
//                                                                if (staticreviewsalesitems.get(l).getFreeflag().equals("freeitem")) {
//                                                                    staticreviewsalesitems.remove(l);
//                                                                }
//                                                            }
//                                                        }
//                                                    }*//*
//                                                    //freeitems.clear();
//                                                }*/
//
//                                            }
//                                        } catch (Exception e) {
//                                            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
//                                            mDbErrHelper.open();
//                                            String geterrror = e.toString();
//                                            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
//                                            mDbErrHelper.close();
//                                        } finally {
//                                            if (objdatabaseadapterfree != null)
//                                                objdatabaseadapterfree.close();
//                                            if (getitemschemeCur != null)
//                                                getitemschemeCur.close();
//                                            if (getFreeStock != null)
//                                                getFreeStock.close();
//                                        }
//
//                                    }
//                                }
//                                    /*********END SCHEME DETAILS*************/

                                if (getschemeapplicable.equals("yes")) {
                                    if (getSchemebusinesstype.contains("1") || getSchemebusinesstype.contains("3")) {
                                        //Get Scheme Rate based
                                        DataBaseAdapter objdatabaseadapter = null;
                                        Cursor getschemeCur = null;
                                        Cursor getschemetype = null;
                                        try {
                                            //Scheme Functionality
                                            //check the item have a free rate count and apply scheme status
                                            if (Double.parseDouble(salesItemList.get(position).getRatecount()) > 0 && salesItemList.get(pos).getApplyratescheme().equals("yes")) {
                                                objdatabaseadapter = new DataBaseAdapter(context);
                                                objdatabaseadapter.open();
                                                getschemeCur = objdatabaseadapter.GetSchemeFORItemDB(salesItemList.get(pos).getItemcode(),
                                                        preferenceMangr.pref_getString("getroutecode"), mHolder.listitemqty.getText().toString());
                                                if (getschemeCur.getCount() > 0) {
                                                    //  for (int i = 0; i < getschemeCur.getCount(); i++) {
                                                    getminqty = getschemeCur.getDouble(0);
                                                    getdiscount = getschemeCur.getDouble(1);
                                                    // }
                                                }

                                                getschemetype = objdatabaseadapter.GetOrderSchemeType(salesItemList.get(pos).getItemcode(), preferenceMangr.pref_getString("getroutecode"));
                                                //get item scheme type
                                                if(getschemetype.getCount()>0){
                                                   String getitemschemetype = getschemetype.getString(0);
                                                    if(getitemschemetype.equals("rate")){
                                                        freeitemstatus="freerate";
                                                       // schemeitemdiscount = String.valueOf(getschemetype.getDouble(2));
                                                    }
                                                }
                                                schemeratestatus = "yes";
                                            }
                                        } catch (Exception e) {
                                            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                            mDbErrHelper.open();
                                            String geterrror = e.toString();
                                            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                            mDbErrHelper.close();
                                        } finally {
                                            if (objdatabaseadapter != null)
                                                objdatabaseadapter.close();
                                            if (getschemeCur != null)
                                                getschemeCur.close();
                                        }
                                    }
                                }
                                String varQty = mHolder.listitemqty.getText().toString();
                                if (varQty.equals("0") || varQty.equals("") || varQty.equals(null)) {
                                    varQty = "0";
                                }
                                //if(!varQty.equals("") && !varQty.equals("null")
                                //   && !varQty.equals(null) && !varQty.equals("0")) {
                                salesItemList.get(pos).setItemqty(varQty);
                                salesItemList.get(pos).setDumyprice(mHolder.listitemrate.getText().toString());
                                //mHolder.listitemrate.setText(dft.format(Double.parseDouble(salesItemList.get(pos).getDumyprice())));
                                salesItemList.get(pos).setNewprice(dft.format(Double.parseDouble(salesItemList.get(pos).getDumyprice())));
                                if (getminqty > 0) {
                                    //do your work here
                                    Double a = Double.parseDouble(mHolder.listitemqty.getText().toString());

                                    Double b = Double.parseDouble(mHolder.listitemrate.getText().toString());
                                    Double res = a * b;

                                    int getdivideqty = (int) (a / getminqty);
                                    Double getactucaldiscount = getdivideqty * getdiscount;
                                    // Double getres = res - getactucaldiscount;

                                    if (getdivideqty > 0) {
                                        double newrate = Double.parseDouble(salesItemList.get(pos).getNewprice());
                                        double getnewrate = newrate - getdiscount ;
                                        mHolder.listitemrate.setText(dft.format(getnewrate));
                                        salesItemList.get(pos).setNewprice(dft.format(getnewrate));
                                        Double getres = getnewrate * Double.parseDouble(varQty);
                                        Double ratediscount = (getdiscount * Double.parseDouble(varQty));
                                        mHolder.listitemtotal.setText(dft.format(getres));
                                        salesItemList.get(pos).setratediscount(ratediscount.toString());
                                            /*mHolder.listdiscount.setBackgroundColor(getResources().getColor(R.color.orangecolor));
                                            mHolder.listdiscount.setText("Disc " + dft.format(getactucaldiscount));
                                            salesItemList.get(pos).setDiscount(dft.format(getactucaldiscount));*/
                                        mHolder.listdiscount.setBackgroundColor(getResources().getColor(R.color.lightbiscuit));
                                        mHolder.listdiscount.setText("");
                                        salesItemList.get(pos).setDiscount("");

                                    } else {
                                        mHolder.listdiscount.setBackgroundColor(getResources().getColor(R.color.lightbiscuit));
                                        mHolder.listdiscount.setText("");
                                    }


                                    //Set Array value for subtotal
                                    salesItemList.get(pos).setSubtotal(dft.format(Double.parseDouble(mHolder.listitemtotal.getText().toString())));
                                    mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.darkblue));
                                    salesItemList.get(pos).setNewprice(mHolder.listitemrate.getText().toString());
                                } else {
                                    //do your work here
                                    Double a = Double.parseDouble(mHolder.listitemqty.getText().toString());
                                    Double b = Double.parseDouble(mHolder.listitemrate.getText().toString());
                                    Double res = a * b;
                                    salesItemList.get(pos).setItemqty(mHolder.listitemqty.getText().toString());

                                    mHolder.listitemtotal.setText(dft.format(res));
                                    String itemlisttotal=mHolder.listitemtotal.getText().toString();
                                    //Set Array value for subtotal
                                    salesItemList.get(pos).setSubtotal(dft.format(res));
                                    mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.darkblue));
                                    salesItemList.get(pos).setNewprice(mHolder.listitemrate.getText().toString());
                                    mHolder.listdiscount.setBackgroundColor(getResources().getColor(R.color.lightbiscuit));
                                    mHolder.listdiscount.setText("");
                                }
                                addedqty = true;
                                mHolder.labelstock.setText(String.valueOf(Integer.parseInt(salesItemList.get(pos).getStockqty()) - Integer.parseInt(salesItemList.get(pos).getItemqty())));
                                CalculateTotal();
                            } else {
                                Toast toast = Toast.makeText(getApplicationContext(),"Please enter valid quantity", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        } else {
                            mHolder.listdiscount.setBackgroundColor(getResources().getColor(R.color.lightbiscuit));
                            mHolder.listdiscount.setText("");
                            mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
                            salesItemList.get(pos).setItemqty(mHolder.listitemqty.getText().toString());
                            salesItemList.get(pos).setSubtotal(mHolder.listitemtotal.getText().toString());
                        }


                        if(addedqty) {
                            if(salesItemList.get(pos).getItemqty().equals("") ||
                                    salesItemList.get(pos).getItemqty().equals("0") ){
                                Toast toast = Toast.makeText(getApplicationContext(),"Please enter  quantity", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                return;
                            }
                            if(Double.parseDouble(salesItemList.get(pos).getItemqty())<=0 ){
                                Toast toast = Toast.makeText(getApplicationContext(),"Please enter valid quantity", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                return;
                            }
                            if(salesItemList.get(pos).getNewprice().equals("") ||
                                    salesItemList.get(pos).getNewprice().equals("0") ){
                                Toast toast = Toast.makeText(getApplicationContext(),"Please enter  price", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                return;
                            }
                            if(Double.parseDouble(salesItemList.get(pos).getNewprice())<=0 ){
                                Toast toast = Toast.makeText(getApplicationContext(),"Please enter valid price", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                return;
                            }
                            if(salesItemList.get(pos).getSubtotal().equals("") ||
                                    salesItemList.get(pos).getSubtotal().equals("0") ){
                                Toast toast = Toast.makeText(getApplicationContext(),"Please enter  total", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                return;
                            }
                            if(Double.parseDouble(salesItemList.get(pos).getSubtotal())<=0 ){
                                Toast toast = Toast.makeText(getApplicationContext(),"Please enter valid total", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                return;
                            }



                            //Added to cart
                            boolean addedcart = false;
                            boolean addedpricecart = true;
                            boolean checkfreeitem = false;
                            DataBaseAdapter dataBaseAdapter = null;
                            Cursor getcartdatas = null;
                            String insertcart="";
                            try {
                                //Cart Add Functionality
                                dataBaseAdapter = new DataBaseAdapter(context);
                                dataBaseAdapter.open();
                                String getcartqty = dataBaseAdapter.GetFreeCartItemStock(salesitems.get(pos).getItemcode());
                                if(Double.parseDouble(getcartqty) >0) {
                                    double getactualstock = Double.parseDouble(salesitems.get(pos).getStockqty()) - Double.parseDouble(getcartqty);

                                    if ( Double.parseDouble(salesitems.get(pos).getItemqty()) > getactualstock) {
                                        Toast toast = Toast.makeText(getApplicationContext(), "Insufficient stock...This item alredy added to cart", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                        mHolder.listitemtotal.setText("0.00");
                                        mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
                                        mHolder.listitemrate.setText(dft.format(Double.parseDouble(salesItemList.get(pos).getDumyprice())));
                                        salesItemList.get(pos).setItemqty("");
                                        salesItemList.get(pos).setSubtotal("0");
                                        return;
                                    }
                                }
                                // for (int i = 0; i < salesitems.size(); i++) {
                                if(salesitems.size() >0) {
                                    Log.w("itemschemeapplicable",itemschemeapplicable);
                                    int i = pos;
                                    if (!salesitems.get(i).getItemqty().equals("")
                                            && !salesitems.get(i).getItemqty().equals(null)
                                            && !salesitems.get(i).getNewprice().equals(null)
                                            && !salesitems.get(i).getNewprice().equals("")) {
                                        if (Double.parseDouble(salesitems.get(i).getItemqty()) > 0
                                                && (Double.parseDouble(salesitems.get(i).getSubtotal())) > 0) {
                                            if (Double.parseDouble(salesitems.get(i).getNewprice()) > 0) {
                                                double getsubtotal = Double.parseDouble(salesitems.get(i).getNewprice()) *
                                                        Double.parseDouble(salesitems.get(i).getItemqty());
                                                if (!salesitems.get(i).getDiscount().equals("") && !salesitems.get(i).getDiscount().equals("0")
                                                        && !salesitems.get(i).getDiscount().equals(null)) {
                                                    getsubtotal = getsubtotal - Double.parseDouble(salesitems.get(i).getDiscount());
                                                } else {
                                                    getsubtotal = getsubtotal;
                                                }
                                                salesitems.get(i).setSubtotal(String.valueOf(getsubtotal));
                                                double totalschemediscount=0;

                                                if(freeitemstatus==""){
                                                    schemeitemdiscount = "";
                                                }else if (freeitemstatus=="freerate"){
                                                    //schemeitemdiscount = schemeitemdiscount;
                                                    if(Double.parseDouble(salesItemList.get(position).getRatecount()) >0 && schemeratestatus.equals("yes")) {
                                                        // totalschemediscount = Double.parseDouble(salesitems.get(i).getItemqty()) * Double.parseDouble(schemeitemdiscount);
                                                        //schemeitemdiscount = String.valueOf(totalschemediscount);
                                                        schemeitemdiscount="0";
                                                    }else{
                                                        schemeitemdiscount="0";
                                                    }
                                                }
                                                if(Double.parseDouble(salesItemList.get(position).getFreecount()) > 0 && (!Utilities.isNullOrEmpty(salesItemList.get(pos).getApplyitemscheme()) && salesItemList.get(pos).getApplyitemscheme().equals("yes"))) {
                                                    itemschemeapplicable = "no";
                                                }
                                                insertcart = dataBaseAdapter.insertSalesCart(String.valueOf(salesitems.get(i).getItemcode()), String.valueOf(salesitems.get(i).getCompanycode()),
                                                        String.valueOf(salesitems.get(i).getBrandcode()), String.valueOf(salesitems.get(i).getManualitemcode())
                                                        , String.valueOf(salesitems.get(i).getItemname()), String.valueOf(salesitems.get(i).getItemnametamil()),
                                                        String.valueOf(salesitems.get(i).getUnitcode()), String.valueOf(salesitems.get(i).getUnitweightunitcode())
                                                        , String.valueOf(salesitems.get(i).getUnitweight()), String.valueOf(salesitems.get(i).getUppunitcode())
                                                        , String.valueOf(salesitems.get(i).getUppweight()), String.valueOf(salesitems.get(i).getItemcategory()),
                                                        String.valueOf(salesitems.get(i).getParentitemcode()),
                                                        String.valueOf(salesitems.get(i).getAllowpriceedit()), String.valueOf(salesitems.get(i).getAllownegativestock())
                                                        , String.valueOf(salesitems.get(i).getAllowdiscount()),
                                                        String.valueOf(salesitems.get(i).getStockqty()), String.valueOf(salesitems.get(i).getUnitname()),
                                                        String.valueOf(salesitems.get(i).getNoofdecimals()),
                                                        String.valueOf(salesitems.get(i).getOldprice()),
                                                        String.valueOf(salesitems.get(i).getNewprice()), String.valueOf(salesitems.get(i).getColourcode()), String.valueOf(salesitems.get(i).getHsn()),
                                                        String.valueOf(salesitems.get(i).getTax()), String.valueOf(salesitems.get(i).getItemqty()), String.valueOf(getsubtotal)
                                                        , String.valueOf(salesitems.get(i).getRouteallowpricedit()), schemeitemdiscount, freeitemstatus,
                                                        String.valueOf(salesitems.get(i).getPurchaseitemcode()), String.valueOf(salesitems.get(i).getFreeitemcode()),
                                                        String.valueOf(salesitems.get(i).getMinsalesqty()),String.valueOf(salesitems.get(i).getDumyprice()),String.valueOf(salesitems.get(i).getratediscount())
                                                        ,itemschemeapplicable,String.valueOf(salesitems.get(i).getOrgprice()));

                                                //txtareaname.setEnabled(false);
                                                //txtcustomername.setEnabled(false);

                                            }
                                        }

                                    }
                                }

                                // }

                                /*********SCHEME DETAILS*************/
                                String getpurchaseitemcode1="";
                                if (getschemeapplicable.equals("yes")) {
                                    if(getSchemebusinesstype.contains("1") || getSchemebusinesstype.contains("3")){
                                        //Get Scheme Rate based
                                        DataBaseAdapter objdatabaseadapter = null;
//                                        Cursor getschemeCur = null;
//                                        try {
//                                            //Scheme Functionality
//                                            //check the item have a free rate count and apply scheme status
//                                            if(Double.parseDouble(salesItemList.get(position).getRatecount()) >0 && salesItemList.get(pos).getApplyratescheme().equals("yes")) {
//                                                objdatabaseadapter = new DataBaseAdapter(context);
//                                                objdatabaseadapter.open();
//                                                getschemeCur = objdatabaseadapter.GetSchemeFORItemDB(salesItemList.get(pos).getItemcode(),
//                                                        preferenceMangr.pref_getString("getroutecode"), mHolder.listitemqty.getText().toString());
//                                                if (getschemeCur.getCount() > 0) {
//                                                    //  for (int i = 0; i < getschemeCur.getCount(); i++) {
//                                                    getminqty = getschemeCur.getDouble(0);
//                                                    getdiscount = getschemeCur.getDouble(1);
//                                                    // }
//                                                }
//                                                schemeratestatus="yes";
//                                            }
//                                        } catch (Exception e) {
//                                            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
//                                            mDbErrHelper.open();
//                                            String geterrror = e.toString();
//                                            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
//                                            mDbErrHelper.close();
//                                        } finally {
//                                            if (objdatabaseadapter != null)
//                                                objdatabaseadapter.close();
//                                            if (getschemeCur != null)
//                                                getschemeCur.close();
//                                        }

                                        //Get Item based Free Item
                                        //GetFreeItem(salesItemList.get(pos).getItemcode(),mHolder.listitemqty.getText().toString());

                                        /********FREE ITEM******************/

                                        String getitemcode = salesItemList.get(pos).getItemcode();
                                        String getqty = mHolder.listitemqty.getText().toString();
                                        //Get Free Item variable
                                        String getfreeitemname = "";
                                        //Get free and purchase item code
                                        String getpurchaseitemcode = "";
                                        String getpurchaseqty = "";
                                        String getfreeitemcode = "";
                                        String getfreeqty = "";
                                        String getfreeitemschemetype ="";

                                        String getallownegativestock = "";
                                        double getfreestockitem = 0;
                                        String getitemcategory = "";
                                        String getparentcode = "";
                                        String getchilditemname = "";
                                        String getchildunitname = "";

                                        //Get Item based Free Item
                                        DataBaseAdapter objdatabaseadapterfree = null;
                                        Cursor getitemschemeCur = null;
                                        Cursor getitemschemetype = null;
                                        Cursor getFreeStock = null;
                                        try {
                                            //Scheme Functionality
                                            objdatabaseadapterfree = new DataBaseAdapter(context);
                                            objdatabaseadapterfree.open();
                                            getitemschemeCur = objdatabaseadapterfree.GetSchemeFORFreeItemDB(getitemcode, preferenceMangr.pref_getString("getroutecode"));
                                            getitemschemetype = objdatabaseadapterfree.GetOrderSchemeType(getitemcode, preferenceMangr.pref_getString("getroutecode"));
                                           //get item scheme type
                                            if(getitemschemetype.getCount()>0){
                                                getfreeitemschemetype = getitemschemetype.getString(0);
//                                                if(getfreeitemschemetype.equals("rate")){
//                                                    freeitemstatus="freerate";
//                                                    schemeitemdiscount = String.valueOf(getitemschemetype.getDouble(2));
//                                                }
                                            }
                                            if (getitemschemeCur.getCount() > 0) {
                                                //freeitems.clear();
                                                // for (int i = 0; i < getitemschemeCur.getCount(); i++) {
                                                getpurchaseitemcode = getitemschemeCur.getString(0);
                                                getpurchaseitemcode1 = getitemschemeCur.getString(0);
                                                getpurchaseqty = getitemschemeCur.getString(1);
                                                getfreeitemcode = getitemschemeCur.getString(2);
                                                getfreeqty = getitemschemeCur.getString(3);


                                                // }
                                                String getpurchaseitemweight= objdatabaseadapterfree.GetCartItemScheme(getpurchaseitemcode,preferenceMangr.pref_getString("getroutecode"));
                                                String getpurchaseitemforcart= objdatabaseadapterfree.GetCartPurchaseItemforScheme(getpurchaseitemcode,preferenceMangr.pref_getString("getroutecode"));

                                                if (Double.parseDouble(getpurchaseqty) <=
                                                        Double.parseDouble(getpurchaseitemweight)) {
                                                    try {
                                                        //Get Stock for parent item
                                                        String getmanualfreeitemname = objdatabaseadapterfree.GetFreeitemname(getfreeitemcode);
                                                        getfreeitemname = getmanualfreeitemname;
                                                        getFreeStock = objdatabaseadapterfree.GetPurchaseItems(getfreeitemcode, preferenceMangr.pref_getString("getroutecode"), LoginActivity.getareacode);

                                                        if (getFreeStock.getCount() > 0) {
                                                            for (int i = 0; i < getFreeStock.getCount(); i++) {
                                                                //getfreeitemstock = getFreeStock.getDouble(0);
                                                                getallownegativestock = getFreeStock.getString(14);
                                                                getfreestockitem = getFreeStock.getDouble(16);
                                                                getitemcategory = getFreeStock.getString(11);
                                                                getparentcode = getFreeStock.getString(12);
                                                                getchilditemname = getFreeStock.getString(5);
                                                                getchildunitname = getFreeStock.getString(17);

                                                            }
                                                        }

                                                        //Calculate Qty with total qty
                                                        Double getactualqty = Double.parseDouble(getqty);

                                                        int getfreeqtyval = (int) ( Double.parseDouble(getpurchaseitemweight) / Double.parseDouble(getpurchaseqty));
                                                        int getactualqtyvalue = (int) (getfreeqtyval * Double.parseDouble(getfreeqty));
                                                        if (getpurchaseitemcode.equals(getfreeitemcode)) {
                                                            if (Double.parseDouble(mHolder.labelstock.getText().toString()) < (Double.parseDouble(String.valueOf(getactualqtyvalue)) + Double.parseDouble(getqty))) {
                                                                Toast toast = Toast.makeText(getApplicationContext(), "Insufficient stock for " + getfreeitemname, Toast.LENGTH_LONG);
                                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                                toast.show();
                                                                mHolder.listitemtotal.setText("0.00");
                                                                mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
                                                                return;
                                                            }
                                                        }
                                                        //check the item have a free item count and apply scheme status
                                                        if(Double.parseDouble(salesItemList.get(position).getFreecount()) > 0 && salesItemList.get(pos).getApplyitemscheme().equals("yes")) {
                                                            itemschemeapplicable = "no";
                                                            if (getfreestockitem >= Double.parseDouble(String.valueOf(getactualqtyvalue))) {
                                                                getFreeStock = null;
                                                                //Get Stock for parent item
                                                                getFreeStock = objdatabaseadapterfree.GetPurchaseItems(getfreeitemcode, preferenceMangr.pref_getString("getroutecode"), LoginActivity.getareacode);

                                                                if (getFreeStock.getCount() > 0) {
                                                                    for (int i = 0; i < getFreeStock.getCount(); i++) {
                                                                        double getsubtotal = Double.parseDouble(getFreeStock.getString(20)) * getactualqtyvalue;
                                                                        itemschemeapplicable = "yes";
                                                                        for (int j = 0; j < freeitems.size(); j++) {
                                                                            if (getpurchaseitemcode.equals(freeitems.get(j).getPurchaseitemcode()) &&
                                                                                    getfreeitemcode.equals(freeitems.get(j).getFreeitemcode())) {
                                                                                freeitems.remove(j);
                                                                            }
                                                                        }
                                                                        freeitems.add(new SalesItemDetails(getFreeStock.getString(0), getFreeStock.getString(1),
                                                                                getFreeStock.getString(2)
                                                                                , getFreeStock.getString(3), getFreeStock.getString(4),
                                                                                getFreeStock.getString(5), getFreeStock.getString(6),
                                                                                getFreeStock.getString(7)
                                                                                , getFreeStock.getString(8), getFreeStock.getString(9), getFreeStock.getString(10)
                                                                                , getFreeStock.getString(11), getFreeStock.getString(12)
                                                                                , getFreeStock.getString(13), getFreeStock.getString(14), getFreeStock.getString(15)
                                                                                , getFreeStock.getString(16), getFreeStock.getString(17)
                                                                                , getFreeStock.getString(18), getFreeStock.getString(19),
                                                                                getFreeStock.getString(20)
                                                                                , getFreeStock.getString(21), getFreeStock.getString(22)
                                                                                , getFreeStock.getString(23), String.valueOf(getactualqtyvalue), String.valueOf(getsubtotal),
                                                                                getFreeStock.getString(24), String.valueOf(getsubtotal),
                                                                                "freeitem", getpurchaseitemforcart, getfreeitemcode, getFreeStock.getString(20),
                                                                                "", "",getFreeStock.getString(28),"","","",itemschemeapplicable,"",getFreeStock.getString(29)));


                                                                    }
                                                                }
                                                            } else {

                                                                if (getallownegativestock.equals("no")) {
                                                                    // if (Double.parseDouble(getqty) > getfreestockitem) {
                                                                    if (getitemcategory.equals("child")) {
                                                                        DataBaseAdapter objdatabaseadapter1 = null;
                                                                        Cursor getStockCur = null;
                                                                        try {
                                                                            //Get Stock for parent item
                                                                            objdatabaseadapter1 = new DataBaseAdapter(context);
                                                                            objdatabaseadapter1.open();
                                                                            getStockCur = objdatabaseadapter1.GetStockForItem(getparentcode);
                                                                            String getcartparentqty = objdatabaseadapter.GetCartItemStock(getparentcode);
                                                                            if (getStockCur.getCount() > 0) {
                                                                                for (int i = 0; i < getStockCur.getCount(); i++) {
                                                                                    getparentstock = getStockCur.getDouble(0);

                                                                                }
                                                                                getparentstock = getparentstock - (Double.parseDouble(getcartparentqty));
                                                                            }

                                                                            if (getparentstock > 0) {

                                                                                //> Double.parseDouble(String.valueOf(getactualqtyvalue))

                                                                                Toast toast = Toast.makeText(getApplicationContext(), "Insufficient stock for " + getfreeitemname + " this free item", Toast.LENGTH_LONG);
                                                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                                                toast.show();
                                                                                mHolder.listitemtotal.setText("0.00");
                                                                                mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
                                                                                mHolder.listitemrate.setText(dft.format(Double.parseDouble(salesItemList.get(pos).getDumyprice())));
                                                                                return;
                                                                                //Stock Conversion
                                                                                /*isfreestock = "yes";
                                                                                getstaticchildcode = getfreeitemcode;
                                                                                getstaticparentitemcode = getparentcode;
                                                                                getstaticchilditemname = getchilditemname;
                                                                                getstaticchildunitname = getchildunitname;
                                                                                isparentopen = "yes";
                                                                                mHolder.listitemname.setBackgroundColor(getResources().getColor(R.color.yellow));
                                                                                mHolder.listitemqty.setText("");
                                                                                mHolder.listitemtotal.setText("0.00");
                                                                                salesItemList.get(pos).setSubtotal("0.00");
                                                                                mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));*/

                                                                            } else {
                                                                                Toast toast = Toast.makeText(getApplicationContext(), "Insufficient stock for " + getfreeitemname, Toast.LENGTH_LONG);
                                                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                                                toast.show();
                                                                                mHolder.listitemtotal.setText("0.00");
                                                                                mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
                                                                                mHolder.listitemrate.setText(dft.format(Double.parseDouble(salesItemList.get(pos).getDumyprice())));

                                                                            }
                                                                        } catch (Exception e) {
                                                                            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                                                            mDbErrHelper.open();
                                                                            String geterrror = e.toString();
                                                                            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                                                            mDbErrHelper.close();
                                                                        } finally {
                                                                            if (objdatabaseadapter1 != null)
                                                                                objdatabaseadapter1.close();
                                                                            if (getStockCur != null)
                                                                                getStockCur.close();
                                                                        }
                                                                    } else {
                                                                        Toast toast = Toast.makeText(getApplicationContext(), "Insufficient stock for " + getfreeitemname + " this free item", Toast.LENGTH_LONG);
                                                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                                                        toast.show();
                                                                        mHolder.listitemtotal.setText("0.00");
                                                                        mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
                                                                        mHolder.listitemrate.setText(dft.format(Double.parseDouble(salesItemList.get(pos).getDumyprice())));
                                                                        //return;
                                                                    }
                                                                    //}
                                                                }
                                                            }
                                                        }

                                                        //scheme process end
                                                    } catch (Exception e) {
                                                        DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                                        mDbErrHelper.open();
                                                        String geterrror = e.toString();
                                                        mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                                        mDbErrHelper.close();
                                                    }finally {
                                                        if (objdatabaseadapterfree != null)
                                                            objdatabaseadapterfree.close();
                                                        if (getitemschemeCur != null)
                                                            getitemschemeCur.close();
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
                                            if (objdatabaseadapterfree != null)
                                                objdatabaseadapterfree.close();
                                            if (getitemschemeCur != null)
                                                getitemschemeCur.close();
                                            if (getFreeStock != null)
                                                getFreeStock.close();
                                        }

                                    }
                                }
                                /*********END SCHEME DETAILS*************/
                                //Add free item to cart
                                for (int k = 0; k < freeitems.size(); k++) {
                                    insertcart = dataBaseAdapter.insertFreeSalesCart(String.valueOf(freeitems.get(k).getItemcode()), String.valueOf(freeitems.get(k).getCompanycode()),
                                            String.valueOf(freeitems.get(k).getBrandcode()), String.valueOf(freeitems.get(k).getManualitemcode())
                                            , String.valueOf(freeitems.get(k).getItemname()),String.valueOf( freeitems.get(k).getItemnametamil()),
                                            String.valueOf(freeitems.get(k).getUnitcode()), String.valueOf(freeitems.get(k).getUnitweightunitcode())
                                            , String.valueOf(freeitems.get(k).getUnitweight()), String.valueOf(freeitems.get(k).getUppunitcode())
                                            , String.valueOf(freeitems.get(k).getUppweight()), String.valueOf(freeitems.get(k).getItemcategory()),
                                            String.valueOf(freeitems.get(k).getParentitemcode()),
                                            String.valueOf(freeitems.get(k).getAllowpriceedit()), String.valueOf(freeitems.get(k).getAllownegativestock())
                                            , String.valueOf(freeitems.get(k).getAllowdiscount()),
                                            String.valueOf(freeitems.get(k).getStockqty()), String.valueOf(freeitems.get(k).getUnitname()),
                                            String.valueOf(freeitems.get(k).getNoofdecimals()),
                                            String.valueOf(freeitems.get(k).getOldprice()),
                                            String.valueOf(freeitems.get(k).getNewprice()),String.valueOf(freeitems.get(k).getColourcode()), String.valueOf(freeitems.get(k).getHsn()),
                                            String.valueOf(freeitems.get(k).getTax()), String.valueOf(freeitems.get(k).getItemqty()), String.valueOf(freeitems.get(k).getSubtotal())
                                            , String.valueOf(freeitems.get(k).getRouteallowpricedit()), String.valueOf(freeitems.get(k).getDiscount()), String.valueOf(freeitems.get(k).getFreeflag()),
                                            String.valueOf(freeitems.get(k).getPurchaseitemcode()),
                                            String.valueOf(freeitems.get(k).getFreeitemcode()),
                                            String.valueOf(freeitems.get(k).getMinsalesqty()),
                                            preferenceMangr.pref_getString("getroutecode"),getpurchaseitemcode1,"",
                                            String.valueOf(freeitems.get(k).getschemeapplicable()),String.valueOf(freeitems.get(k).getNewprice()));

                                    checkfreeitem = true;
                                }

                                //Get cart datas from database temp table
                                if(dataBaseAdapter!=null){
                                    dataBaseAdapter.close();
                                    dataBaseAdapter.open();
                                }

                                getcartdatas = dataBaseAdapter.GetSalesItemsCart();
                                if(getcartdatas.getCount()>0) {
                                    staticreviewsalesitems.clear();
                                    for (int i = 0; i < getcartdatas.getCount(); i++) {
                                        staticreviewsalesitems.add(new SalesItemDetails(getcartdatas.getString(1), getcartdatas.getString(2),
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
                                                "","", getcartdatas.getString(32),"","",
                                                getcartdatas.getString(34),getcartdatas.getString(35),getcartdatas.getString(36),""));
                                        getcartdatas.moveToNext();
                                    }
                                }



                            } catch (Exception e) {
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

                            if (checkfreeitem) {
                                freeitems.clear();
                            }
                            //Collections.sort(staticreviewsalesitems, new SalesItemDetails.SortbyPurchaseItemDesc());
                            totalcartitems.setText(String.valueOf(staticreviewsalesitems.size()));
                        }
                    } else {
//                            final int pos = (Integer) mHolder.listitemqty.getTag();



//                            mHolder.labelstock.setText(salesItemList.get(pos).getStockqty());
                        mHolder.listdiscount.setBackgroundColor(getResources().getColor(R.color.lightbiscuit));
                        mHolder.listdiscount.setText("");
                        isparentopen = "";
                        //mHolder.listitemname.setBackgroundColor(getResources().getColor(R.color.white));
                        mHolder.listitemtotal.setText("0.00");
                        mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
                        mHolder.listitemrate.setText(dft.format(Double.parseDouble(salesItemList.get(pos).getDumyprice())));
                        salesItemList.get(pos).setNewprice(dft.format(Double.parseDouble(salesItemList.get(pos).getDumyprice())));
                        salesItemList.get(pos).setItemqty(mHolder.listitemqty.getText().toString());
                        salesItemList.get(pos).setSubtotal(mHolder.listitemtotal.getText().toString());
                        Toast toast = Toast.makeText(getApplicationContext(),"Please enter valid quantity", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        String getitemcode = salesItemList.get(position).getItemcode();
                        DataBaseAdapter objdatabaseadapter = null;
                        Cursor getcartdatas = null;
                        try {
                            //Order item details
                            objdatabaseadapter = new DataBaseAdapter(context);
                            objdatabaseadapter.open();
                            String getresult = objdatabaseadapter.DeleteItemInCart(getitemcode);
                            if(getresult.equals("Success")){

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
                                                getcartdatas.getString(30), getcartdatas.getString(31),
                                                getcartdatas.getString(21),"","" ,
                                                getcartdatas.getString(32),"","",
                                                getcartdatas.getString(34),getcartdatas.getString(35),getcartdatas.getString(36),""));
                                        getcartdatas.moveToNext();
                                    }
                                }
                            }
                            totalcartitems.setText(String.valueOf(staticreviewsalesitems.size()));


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
                        return;
                    }


                    // }
                }
            });

//            mHolder.listitemname.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    boolean checkchilditem = false;
//                    if(isnillstockconversion) {
//                        if (salesItemList.get(position).getAllownegativestock().equals("no")) {
//                            if (salesItemList.get(position).getItemcategory().equals("child")) {
//                                final int pos = (Integer) mHolder.listitemqty.getTag();
//                                double getparentstock = 0.0;
//                                DataBaseAdapter objdatabaseadapter = null;
//                                Cursor getStockCur = null;
//                                try {
//                                    //Get Stock for parent item
//                                    objdatabaseadapter = new DataBaseAdapter(context);
//                                    objdatabaseadapter.open();
//                                    getStockCur = objdatabaseadapter.GetStockForItem(salesItemList.get(pos).getParentitemcode());
//                                    if (getStockCur.getCount() > 0) {
//                                        // for (int i = 0; i < getStockCur.getCount(); i++) {
//                                        getparentstock = getStockCur.getDouble(0);
//                                        //}
//                                    }
//                                    if (getparentstock > 0) {
//                                        isfreestock = "";
//                                        getstaticparentitemcode = salesItemList.get(pos).getParentitemcode();
//                                        getstaticchilditemname = salesItemList.get(pos).getItemnametamil();
//                                        getstaticchildunitname = salesItemList.get(pos).getUnitname();
//                                        checkchilditem = true;
//
//                                        mHolder.listitemname.setBackgroundColor(getResources().getColor(R.color.yellow));
//                                        mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
//                                    } else {
//                                        checkchilditem=false;
//                                    }
//                                } catch (Exception e) {
//                                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
//                                    mDbErrHelper.open();
//                                    String geterrror = e.toString();
//                                    mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
//                                    mDbErrHelper.close();
//                                } finally {
//                                    if (objdatabaseadapter != null)
//                                        objdatabaseadapter.close();
//                                    if (getStockCur != null)
//                                        getStockCur.close();
//                                }
//
//                            }
//                        }
//                        if (checkchilditem) {
//                            double getminqty = 0;
//                            double getdiscount = 0;
//                            double getparentstock = 0;
//
//                            final int pos = (Integer) mHolder.listitemqty.getTag();
//                            DataBaseAdapter objdatabaseadapter = null;
//                            Cursor getStockCur = null;
//                            try {
//                                //Get Stock for parent item
//                                objdatabaseadapter = new DataBaseAdapter(context);
//                                objdatabaseadapter.open();
//                                getStockCur = objdatabaseadapter.GetStockForItem(salesItemList.get(pos).getParentitemcode());
//                                if (getStockCur.getCount() > 0) {
//                                    for (int i = 0; i < getStockCur.getCount(); i++) {
//                                        getparentstock = getStockCur.getDouble(0);
//                                    }
//                                }
//                                if (getparentstock > 0) {
//                                    isfreestock = "";
//                                    getstaticparentitemcode = salesItemList.get(pos).getParentitemcode();
//                                    getstaticchilditemname = salesItemList.get(pos).getItemnametamil();
//                                    getstaticchildunitname = salesItemList.get(pos).getUnitname();
//                                    isparentopen = "yes";
//                                    mHolder.listitemname.setBackgroundColor(getResources().getColor(R.color.yellow));
//                                   // mHolder.listitemqty.setText("");
//                                } else {
//                                    //mHolder.listitemname.setBackgroundColor(getResources().getColor(R.color.white));
//                                    Toast toast = Toast.makeText(getApplicationContext(), "Insufficient stock..", Toast.LENGTH_LONG);
//                                    toast.setGravity(Gravity.CENTER, 0, 0);
//                                    toast.show();
//                                    mHolder.listitemtotal.setText("0.00");
//                                    mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
//                                    mHolder.listitemrate.setText(dft.format(Double.parseDouble(salesItemList.get(pos).getDumyprice())));
//                                    //Toast.makeText(getApplicationContext(), "Insufficient stock..", Toast.LENGTH_SHORT).show();
//                                    //mHolder.listitemqty.setText("");
//                                    String getresult=DeleteItemCart(salesItemList.get(pos).getItemcode());
//                                    if(getresult.equals("Success")) {
//                                        salesItemList.get(pos).setItemqty("");
//                                        salesItemList.get(pos).setNewprice("");
//                                    }
//                                }
//                            } catch (Exception e) {
//                                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
//                                mDbErrHelper.open();
//                                String geterrror = e.toString();
//                                mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
//                                mDbErrHelper.close();
//                            } finally {
//                                if (objdatabaseadapter != null)
//                                    objdatabaseadapter.close();
//                                if (getStockCur != null)
//                                    getStockCur.close();
//                            }
//                        }
//                    }
//                    if(isparentopen.equals("yes"))
//                    {
//                        double getparentstock = 0;
//                        String getparentitemname = "";
//                        String getunitname = "";
//                        String getupp = "";
//                        final int pos = (Integer) mHolder.listitemqty.getTag();
//                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                        DataBaseAdapter objdatabaseadapter = null;
//                        Cursor getStockCur = null;
//                        try {
//                            //Get Stock for parent item
//                            objdatabaseadapter = new DataBaseAdapter(context);
//                            objdatabaseadapter.open();
//                            getStockCur = objdatabaseadapter.GetStockForItem(getstaticparentitemcode);
//                            String getcartqty = objdatabaseadapter.GetCartItemStock(salesItemList.get(pos).getParentitemcode());
//                            if (getStockCur.getCount() > 0) {
//                                for (int i = 0; i < getStockCur.getCount(); i++) {
//                                    getparentstock = getStockCur.getDouble(0);
//                                    getparentitemname = getStockCur.getString(1);
//                                    getunitname = getStockCur.getString(2);
//                                    getupp = getStockCur.getString(3);
//                                }
//                                getparentstock = getparentstock - (Double.parseDouble(getcartqty));
//                            }
//
//                        } catch (Exception e) {
//                            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
//                            mDbErrHelper.open();
//                            String geterrror = e.toString();
//                            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
//                            mDbErrHelper.close();
//                        } finally {
//                            if (objdatabaseadapter != null)
//                                objdatabaseadapter.close();
//                            if (getStockCur != null)
//                                getStockCur.close();
//                        }
//                        final String finalGetparentitemname = getparentitemname;
//                        final int finalGetparentstock = (int) getparentstock;
//                        final String finalGetunitname = getunitname;
//                        final String finalGetupp = getupp;
//                        final String getparentcode = getstaticparentitemcode;
//
//                    /*    builder.setMessage("Do you want to convert stock?")
//                                .setCancelable(false)
//                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                    public void onClick(final DialogInterface dialog, int id) {*/
//                                        //dialog.dismiss();
//                                        stockconvertdialog = new Dialog(context);
//                                        stockconvertdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                                        stockconvertdialog.setContentView(R.layout.stockconversion);
//                                        stockconvertdialog.setCanceledOnTouchOutside(false);
//                                        ImageView closepopup = (ImageView) stockconvertdialog.findViewById(R.id.closepopup);
//                                        closepopup.setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//                                               // isparentopen = "";
//                                               // mHolder.listitemname.setBackgroundColor(getResources().getColor(R.color.white));
//                                                stockconvertdialog.dismiss();
//                                                isparentopen ="";
//                                                isnillstockconversion =true;
//                                                mHolder.listitemqty.setText("");
//                                            }
//                                        });
//                                        final TextView txtparentitem = (TextView) stockconvertdialog.findViewById(R.id.txtparentitem);
//                                        final TextView txtparentstock = (TextView) stockconvertdialog.findViewById(R.id.txtparentstock);
//                                        final TextView txtparentunit = (TextView) stockconvertdialog.findViewById(R.id.txtparentunit);
//                                        final EditText txtparentqty = (EditText) stockconvertdialog.findViewById(R.id.txtparentqty);
//                                        final TextView txtchilditem = (TextView) stockconvertdialog.findViewById(R.id.txtchilditem);
//                                        final TextView txtchildqty = (TextView) stockconvertdialog.findViewById(R.id.txtchildqty);
//                                        final TextView txtchildconvertunit = (TextView)stockconvertdialog.findViewById(R.id.txtchildconvertunit);
//                                        final TextView txtparentconvertunit = (TextView)stockconvertdialog.findViewById(R.id.txtparentconvertunit) ;
//
//                                        txtparentitem.setText(finalGetparentitemname);
//                                        txtparentstock.setText(String.valueOf(finalGetparentstock));
//                                        txtparentunit.setText(finalGetunitname);
//                                        txtparentconvertunit.setText(finalGetunitname);
//                                        if(finalGetparentstock >= 1 ){
//                                            txtparentqty.setText(String.valueOf("1"));
//                                        }else{
//                                            txtparentqty.setText(String.valueOf(finalGetparentstock));
//                                        }
//
//
//                                        if (finalGetupp.equals("0") || finalGetupp.equals("")) {
//                                            txtchildqty.setText(String.valueOf(finalGetparentstock));
//                                        } else {
//                                            if(finalGetparentstock >= 1 ){
//                                                double getcompleteqty = 1 * Double.parseDouble(finalGetupp);
//                                                txtchildqty.setText(String.valueOf(getcompleteqty));
//                                            }else{
//                                                double getcompleteqty = finalGetparentstock * Double.parseDouble(finalGetupp);
//                                                txtchildqty.setText(String.valueOf(getcompleteqty));
//                                            }
//
//                                        }
//                                        /***********PARENT QTY TEXT CHANGE LISTENER******/
//                                        txtparentqty.addTextChangedListener(new TextWatcher() {
//
//                                            @Override
//                                            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                                                // TODO Auto-generated method stub
//                                            }
//
//                                            @Override
//                                            public void beforeTextChanged(CharSequence s, int start, int count,
//                                                                          int after) {
//                                                // TODO Auto-generated method stub
//                                            }
//
//                                            @Override
//                                            public void afterTextChanged(Editable s) {
//                                                // TODO Auto-generated method stub
//                                                if (!txtparentqty.getText().toString().equals("")) {
//                                                    if (finalGetparentstock >= Double.parseDouble(txtparentqty.getText().toString())) {
//                                                        if (finalGetupp.equals("0") || finalGetupp.equals("")) {
//                                                            txtchildqty.setText(String.valueOf(txtparentqty.getText().toString()));
//                                                        } else {
//                                                            double getcompleteqty = Double.parseDouble(txtparentqty.getText().toString()) * Double.parseDouble(finalGetupp);
//                                                            txtchildqty.setText(String.valueOf(getcompleteqty).toString());
//                                                        }
//                                                    } else {
//                                                        txtchildqty.setText("0");
//                                                        Toast toast = Toast.makeText(getApplicationContext(),"Stock exceed with parent qty", Toast.LENGTH_LONG);
//                                                        toast.setGravity(Gravity.CENTER, 0, 0);
//                                                        toast.show();
//                                                        //Toast.makeText(getApplicationContext(), "Stock exceed with parent qty", Toast.LENGTH_SHORT).show();
//                                                    }
//
//                                                }else{
//                                                    txtchildqty.setText("");
//                                                    Toast toast = Toast.makeText(getApplicationContext(),"Please enter valid qty", Toast.LENGTH_LONG);
//                                                    toast.setGravity(Gravity.CENTER, 0, 0);
//                                                    toast.show();
//                                                    //Toast.makeText(getApplicationContext(), "Please enter valid qty", Toast.LENGTH_SHORT).show();
//                                                }
//
//
//                                            }
//                                        });
//                                        /***********END PARENT QTY TEXT CHANGE LISTENER******/
//
//                                        txtchilditem.setText(getstaticchilditemname);
//                                        txtchildconvertunit.setText(getstaticchildunitname);
//
//                                        Button btnConversion = (Button) stockconvertdialog.findViewById(R.id.btnConversion);
//                                        btnConversion.setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//                                                if (txtparentitem.getText().toString().equals("")
//                                                        || txtparentitem.getText().toString().equals(null)) {
//                                                    Toast toast = Toast.makeText(getApplicationContext(),"Please select parent item", Toast.LENGTH_LONG);
//                                                    toast.setGravity(Gravity.CENTER, 0, 0);
//                                                    toast.show();
//                                                    //Toast.makeText(getApplicationContext(), "Please select parent item", Toast.LENGTH_SHORT).show();
//                                                    return;
//                                                }
//                                                if (txtparentstock.getText().toString().equals("")
//                                                        || txtparentstock.getText().toString().equals(null)) {
//                                                    Toast toast = Toast.makeText(getApplicationContext(),"Please enter parent stock", Toast.LENGTH_LONG);
//                                                    toast.setGravity(Gravity.CENTER, 0, 0);
//                                                    toast.show();
//                                                    //Toast.makeText(getApplicationContext(), "Please enter parent stock", Toast.LENGTH_SHORT).show();
//                                                    return;
//                                                }
//                                                if (txtparentunit.getText().toString().equals("")
//                                                        || txtparentunit.getText().toString().equals(null)) {
//                                                    Toast toast = Toast.makeText(getApplicationContext(),"Please enter parent unit", Toast.LENGTH_LONG);
//                                                    toast.setGravity(Gravity.CENTER, 0, 0);
//                                                    toast.show();
//                                                    //Toast.makeText(getApplicationContext(), "Please enter parent unit", Toast.LENGTH_SHORT).show();
//                                                    return;
//                                                }
//                                                if (txtparentqty.getText().toString().equals("")
//                                                        || txtparentqty.getText().toString().equals(null)) {
//                                                    Toast toast = Toast.makeText(getApplicationContext(),"Please select parent qty", Toast.LENGTH_LONG);
//                                                    toast.setGravity(Gravity.CENTER, 0, 0);
//                                                    toast.show();
//                                                    //Toast.makeText(getApplicationContext(), "Please select parent qty", Toast.LENGTH_SHORT).show();
//                                                    return;
//                                                }
//                                                if (txtchilditem.getText().toString().equals("")
//                                                        || txtchilditem.getText().toString().equals(null)) {
//                                                    Toast toast = Toast.makeText(getApplicationContext(),"Please select child item", Toast.LENGTH_LONG);
//                                                    toast.setGravity(Gravity.CENTER, 0, 0);
//                                                    toast.show();
//                                                   // Toast.makeText(getApplicationContext(), "Please select child item", Toast.LENGTH_SHORT).show();
//                                                    return;
//                                                }
//                                                if (txtchildqty.getText().toString().equals("")
//                                                        || txtchildqty.getText().toString().equals(null)) {
//                                                    Toast toast = Toast.makeText(getApplicationContext(),"Please enter child qty", Toast.LENGTH_LONG);
//                                                    toast.setGravity(Gravity.CENTER, 0, 0);
//                                                    toast.show();
//                                                    //Toast.makeText(getApplicationContext(), "Please enter child qty", Toast.LENGTH_SHORT).show();
//                                                    return;
//                                                }
//                                                if (Double.parseDouble(txtchildqty.getText().toString())<=0) {
//                                                    Toast toast = Toast.makeText(getApplicationContext(),"Please enter valid child qty", Toast.LENGTH_LONG);
//                                                    toast.setGravity(Gravity.CENTER, 0, 0);
//                                                    toast.show();
//                                                    //Toast.makeText(getApplicationContext(), "Please enter child qty", Toast.LENGTH_SHORT).show();
//                                                    return;
//                                                }
//                                                String getchilditemcode = salesItemList.get(pos).getItemcode();
//                                                DataBaseAdapter stockobjdatabaseadapter = null;
//                                                String insertstock = null;
//                                                try {
//                                                    //Stock Conversion Functionality
//                                                    stockobjdatabaseadapter = new DataBaseAdapter(context);
//                                                    stockobjdatabaseadapter.open();
//                                                    String getitemstockcode= "";
//                                                    if(isfreestock.equals("yes")){
//                                                        getitemstockcode = getstaticchildcode;
//                                                    }else{
//                                                        getitemstockcode =  getchilditemcode;
//                                                    }
//                                                    insertstock = stockobjdatabaseadapter.insertStockConversion(getparentcode, txtchildqty.getText().toString(),
//                                                            txtparentqty.getText().toString(),getitemstockcode, preferenceMangr.pref_getString("getschedulecode"));
//                                                    if (insertstock.equals("success")) {
//                                                        getstaticchilditemcode = salesItemList.get(pos).getItemcode();
//                                                        getstaticgetchildqty = mHolder.listitemqty.getText().toString();
//                                                        Toast toast = Toast.makeText(getApplicationContext(),"Stock converted successfully", Toast.LENGTH_LONG);
//                                                        toast.setGravity(Gravity.CENTER, 0, 0);
//                                                        toast.show();
//                                                       // Toast.makeText(getApplicationContext(), "Saved successfully", Toast.LENGTH_SHORT).show();
//                                                        stockconvertdialog.dismiss();
//
//                                                        //dialog.dismiss();
//                                                        isfreestock = "";
//                                                        getitemsfromcode = getstaticsubcode;
//
//
//                                                        GetItems(getstaticsubcode);
//
//                                                    }
//
//                                                } catch (Exception e) {
//                                                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
//                                                    mDbErrHelper.open();
//                                                    String geterrror = e.toString();
//                                                    mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
//                                                    mDbErrHelper.close();
//                                                } finally {
//                                                    if (stockobjdatabaseadapter != null)
//                                                        stockobjdatabaseadapter.close();
//                                                }
//
//                                            }
//                                        });
//                                        stockconvertdialog.show();
//                                    //}
//                                //})
//                                /*.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        dialog.cancel();
//                                        mHolder.listitemqty.setText("");
//                                        *//*Toast toast = Toast.makeText(getApplicationContext(),"Insufficient stock..", Toast.LENGTH_LONG);
//                                        toast.setGravity(Gravity.CENTER, 0, 0);
//                                        toast.show();*//*
//                                        //Toast.makeText(getApplicationContext(), "Insufficient stock..", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                        AlertDialog alert = builder.create();
//                        alert.show();*/
//                    }
//                }
//            });

            mHolder.listitemqty.setTag(position);
            mHolder.listitemrate.setTag(position);
            return convertView;
        }

        private class ViewHolder1 {
            private TextView listitemname,schemecount,dummycount;
            private TextView listitemcode,labelnilstock;
            private EditText listitemqty;
            private EditText listitemrate;
            private TextView listitemtotal,listitemtax,labelstock,labelhsntax,labelstockunit,listdiscount,listitemupp;
            private LinearLayout itemLL,stockvalueLL;
            private  ImageView pricearrow;


        }
    }
    //    public class ViewHolder1 {
//        private TextView listitemname,schemecount,dummycount;
//        private TextView listitemcode,labelnilstock;
//        private EditText listitemqty;
//        private EditText listitemrate;
//        private TextView listitemtotal,listitemtax,labelstock,labelhsntax,labelstockunit,listdiscount,listitemupp;
//        private LinearLayout itemLL,stockvalueLL;
//        private  ImageView pricearrow;
//
//
//    }
    public  String DeleteItemCart(String getitemcode){

        DataBaseAdapter objdatabaseadapter = null;
        Cursor getcartdatas = null;
        String getresult ="";
        try {
            //Order item details
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            getresult = objdatabaseadapter.DeleteItemInCart(getitemcode);
            if (getresult.equals("Success")) {
                getcartdatas = objdatabaseadapter.GetSalesItemsCart();
                if(getcartdatas.getCount()>0){
                    totalcartitems.setText(String.valueOf(getcartdatas.getCount()));
                }else{
                    totalcartitems.setText(String.valueOf("0"));
                }

            }
        } catch (Exception e) {
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        } finally {
            if (objdatabaseadapter != null)
                objdatabaseadapter.close();
            if (getcartdatas != null)
                getcartdatas.close();

        }
        return getresult;
    }

    public void CalculateTotal() {
        try{
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
                res2 = res2 + Double.parseDouble(getsaleseqty);
            }
            billisttotalamount.setText(dft.format(res1));
        }catch (Exception e){
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }


    /************BASE ADAPTER*************/
    //City Adapter
    public class CityAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;

        CityAdapter(Context c) {
            context = c;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return citycode.length;
        }

        @Override
        public Object getItem(int position) {
            return citycode[position];
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

            ViewHolder mHolder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.citypopuplist, parent, false);
                mHolder = new ViewHolder();
                try {
                    mHolder.listcityname = (TextView) convertView.findViewById(R.id.listcityname);
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
                if(String.valueOf(citynametamil[position]).equals("") || String.valueOf(citynametamil[position]).equals("null") ||
                        String.valueOf(citynametamil[position]).equals(null)) {
                    mHolder.listcityname.setText(String.valueOf(cityname[position]));
                }else{
                    mHolder.listcityname.setText(String.valueOf(citynametamil[position]));
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
                    if(String.valueOf(citynametamil[position]).equals("") || String.valueOf(citynametamil[position]).equals("null") ||
                            String.valueOf(citynametamil[position]).equals(null)) {
                        txtcityname.setText(String.valueOf(cityname[position]));
                    }else{
                        txtcityname.setText(String.valueOf(citynametamil[position]));
                    }
                    getcitycode = citycode[position];
                    txtarea.setText("");
                    getareacode="0";
                    txtarea.setHint("Area");
                    citydialog.dismiss();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView listcityname;

        }

    }
    //Area Adapter
    public class AreaAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;

        AreaAdapter(Context c) {
            context = c;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return areacode.length;
        }

        @Override
        public Object getItem(int position) {
            return areacode[position];
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
                if(!String.valueOf(areanametamil[position]).equals("")
                        && !String.valueOf(areanametamil[position]).equals("null")
                        && !String.valueOf(areanametamil[position]).equals(null)) {
                    mHolder.listareaname.setText(String.valueOf(areanametamil[position]));
                }else{
                    mHolder.listareaname.setText(String.valueOf(areaname[position]));
                }
                mHolder.listcustomercount.setText(String.valueOf(customercount[position]));

                int getcount = 0;
                for(int i = 0;i<areacode.length;i++){
                    if(!areacode[i].equals("0")){
                        getcount = getcount + Integer.parseInt(customercount[i]);
                    }
                }

                if(areacode[position].equals("0")){
                    mHolder.listcustomercount.setText(String.valueOf(getcount));
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
                    if(!String.valueOf(areanametamil[position]).equals("")
                            && !String.valueOf(areanametamil[position]).equals("null")
                            && !String.valueOf(areanametamil[position]).equals(null)) {
                        txtarea.setText(String.valueOf(areanametamil[position]));
                    }else{
                        txtarea.setText(String.valueOf(areaname[position]));
                    }
                    getareacode = areacode[position];

                    areadialog.dismiss();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView listareaname,listcustomercount;

        }

    }
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
            return getCount();
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
                    preferenceMangr.pref_putString(Constants.KEY_GETAREACODE, String.valueOf(AreaCode[position]));
//                    preferenceMangr.pref_putString(Constants.KEY_GETAREANAME, AreaName[position] +" - "+CityName[position]);
                    LoginActivity.getareacode = String.valueOf(AreaCode[position]);
                    radio_credit.setEnabled(true);
                    radio_cash.setEnabled(true);
                    radio_cash.setChecked(true);
                    if(staticreviewsalesitems.size() > 0 ){
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Confirmation");
                        builder.setMessage("Are you sure you want to clear cart?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        txtcustomername.setText("");
                                        customercode = "0";
                                        gstnnumber = "";
                                        getschemeapplicable = "";
                                        togglegstin.setBackgroundColor(getResources().getColor(R.color.graycolor));
                                        togglegstin.setText("GSTIN \n ");
                                        togglegstin.setTextOn("GSTIN \n ");
                                        togglegstin.setTextOff("GSTIN \n ");
                                        txtcustomername.setHint("Customer Name");
                                        staticreviewsalesitems.clear();
                                        //Set total cart item value
                                        totalcartitems.setText(String.valueOf(staticreviewsalesitems.size()));
                                        salesitems.clear();
                                        DataBaseAdapter objdatabaseadapter = new DataBaseAdapter(context);
                                        objdatabaseadapter.open();
                                        objdatabaseadapter.DeleteSalesItemCart();
                                        objdatabaseadapter.close();

                                        Utilities.deleteOrderToSalesConverstionDetails(getApplicationContext());

                                        SalesItemAdapter adapter = new SalesItemAdapter(context,salesitems,"0");
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
                        txtcustomername.setText("");
                        customercode = "0";
                        gstnnumber = "";
                        getschemeapplicable = "";
                        togglegstin.setBackgroundColor(getResources().getColor(R.color.graycolor));
                        togglegstin.setText("GSTIN \n ");
                        togglegstin.setTextOn("GSTIN \n ");
                        togglegstin.setTextOff("GSTIN \n ");
                        txtcustomername.setHint("Customer Name");
                        togglegstin.setText("GSTIN \n "+gstnnumber);
                        togglegstin.setTextOn("GSTIN \n "+gstnnumber);
                        togglegstin.setTextOff("GSTIN \n "+gstnnumber);
                        salesitems.clear();
                        SalesItemAdapter adapter = new SalesItemAdapter(context,salesitems,"0");
                        lv_sales_items.setAdapter(adapter);

                    }
                    routeflag = 0;
                    areadialog.dismiss();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView listareaname,listcustomercount;

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
                convertView = layoutInflater.inflate(R.layout.customerpopuplist, parent, false);
                mHolder = new ViewHolder();
                try {
                    mHolder.LLL = (LinearLayout)convertView.findViewById(R.id.LLL);
                    mHolder.listcustomername = (TextView) convertView.findViewById(R.id.listcustomername);
                    mHolder.listgstin = (TextView)convertView.findViewById(R.id.listgstin);
                    mHolder.listcustomertype = (TextView)convertView.findViewById(R.id.listcustomertype);
                    mHolder.listTotalOrder = (TextView) convertView.findViewById(R.id.listTotalOrder);
                    mHolder.LLTotalOrder = (CardView) convertView.findViewById(R.id.LLTotalOrder);
                    mHolder.listordercount=(TextView) convertView.findViewById(R.id.listordercount);
                    mHolder.LLMenu = (LinearLayout) convertView.findViewById(R.id.LLMenu);
                    mHolder.LLMenu.setVisibility(View.VISIBLE);
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

                if(customertypecode[position].equals("1")){
                    mHolder.listcustomertype.setText("CA");
//                    mHolder.listcustomertype.setTextColor(getResources().getColor(R.color.black));
                    mHolder.listcustomertype.setTextColor(getResources().getColor(R.color.white));

                }else if(customertypecode[position].equals("2")){
                    mHolder.listcustomertype.setText("CR");
//                    mHolder.listcustomertype.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    mHolder.listcustomertype.setTextColor(getResources().getColor(R.color.white));

                }

                if(GSTN[position].equals("") || GSTN[position].equals("null") ||GSTN[position].equals(null)){
                    mHolder.listgstin.setVisibility(View.INVISIBLE);
                }else{
                    mHolder.listgstin.setVisibility(View.VISIBLE);
                }
                if(Utilities.isNullOrEmpty(CustomerTotalOrder[position]) || CustomerTotalOrder[position].equals("0")){
                    mHolder.LLTotalOrder.setVisibility(View.INVISIBLE);
                    mHolder.listordercount.setVisibility(View.INVISIBLE);
                }else{
                   /* if(Utilities.isNullOrEmpty(GSTN[position]))
                        mHolder.listgstin.setVisibility(View.INVISIBLE);

                    if(CustomerOutstanding[position].equals("") || CustomerOutstanding[position].equals("null") || CustomerOutstanding[position].equals(null)
                            || CustomerOutstanding[position].equals("0") || CustomerOutstanding[position].equals("0.0") || CustomerOutstanding[position].equals("0.00"))
                        mHolder.listcustomeroutstanding.setVisibility(View.INVISIBLE);*/

                    mHolder.LLTotalOrder.setVisibility(View.VISIBLE);
                    mHolder.listordercount.setVisibility(View.VISIBLE);
//
                    mHolder.listTotalOrder.setText(CustomerTotalOrder[position]);
                    if(Integer.parseInt(CustomerTotalOrder[position])>1){
                        mHolder.listordercount.setText("Orders");
                    }
                    else
                    {
                        mHolder.listordercount.setText("Order");

                    }

                }
                if((!Utilities.isNullOrEmpty(Customerbillcount[position]) && Integer.parseInt(Customerbillcount[position]) > 0) ||
                        (!Utilities.isNullOrEmpty(CusNotPurchasedCount[position]) && Integer.parseInt(CusNotPurchasedCount[position]) > 0)) {
                    mHolder.LLL.setBackgroundColor(getResources().getColor(R.color.billedcustomer));
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
                    customercityname = CustomerCityName[position];
                    customerareaname = CustomerAreaName[position];
                    customername = CustomerNameTamil[position];
                    customercityarea= CustomerCityName[position] + ',' + CustomerAreaName[position];
                    if(gstnnumber.equals("")||gstnnumber.equals(null)||gstnnumber.equals("0")){
                        togglegstin.setBackgroundColor(getResources().getColor(R.color.graycolor));
                    }else{
                        togglegstin.setBackgroundColor(getResources().getColor(R.color.green));
                    }
                    if(customertypecode[position].equals("1")){
                        radio_cash.setChecked(true);
                        radio_credit.setChecked(false);
                        radio_credit.setEnabled(false);
                        //radio_cash.setEnabled(false);

                        getpaymenttypecode = "1";
                        getpaymenttypename = "CASH";
                        confromBilltype=true;
                    }
                    if(customertypecode[position].equals("2")){
                        radio_cash.setChecked(false);
                        radio_credit.setChecked(true);
                        radio_cash.setEnabled(true);
                        radio_credit.setEnabled(true);
                        //radio_credit.setEnabled(false);
                        getpaymenttypecode = "2";
                        getpaymenttypename = "CREDIT";
                        confromBilltype=false;
                    }


                    togglegstin.setText("GSTIN \n "+gstnnumber);
                    togglegstin.setTextOn("GSTIN \n "+gstnnumber);
                    togglegstin.setTextOff("GSTIN \n "+gstnnumber);
                    salesitems.clear();
                    SalesItemAdapter adapter = new SalesItemAdapter(context,salesitems,"0");
                    lv_sales_items.setAdapter(adapter);
                    //confromBilltype=false;
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
                                        DataBaseAdapter objdatabaseadapter = new DataBaseAdapter(context);
                                        objdatabaseadapter.open();
                                        objdatabaseadapter.DeleteSalesItemCart();
                                        objdatabaseadapter.close();

                                        Utilities.deleteOrderToSalesConverstionDetails(getApplicationContext());

                                        SalesItemAdapter adapter = new SalesItemAdapter(context,salesitems,"0");
                                        lv_sales_items.setAdapter(adapter);
                                        confromBilltype=false;
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


                    // check mobile number is verified
                    DataBaseAdapter objdatabaseadapter = null;
                    objdatabaseadapter = new DataBaseAdapter(context);
                    objdatabaseadapter.open();
                    try{
                        String mobilenoverifycount=objdatabaseadapter.Checkemobilenoverify(customercode,preferenceMangr.pref_getString("getroutecode"));
                        getmobilenoverifycount=mobilenoverifycount;
                        if(!mobilenoverifycount.equals("0")) {
                            Showmobilenoverify("verifycustomermobileno");
                        }
                    } catch (Exception e) {
                        DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                        mDbErrHelper.open();
                        String geterrror = e.toString();
                        Log.w("customercode",geterrror);
                        mDbErrHelper.insertErrorLog(geterrror.replace("'", " "),
                                this.getClass().getSimpleName()+" - Check mobile no. pending", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        mDbErrHelper.close();
                    } finally {
                        if (objdatabaseadapter != null)
                            objdatabaseadapter.close();
                    }
                    customerdialog.dismiss();


                }
            });

            mHolder.LLMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(context, mHolder.LLMenu);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.customer_menu);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            if (id == R.id.cusMenuOrder) {
                                orderToSales(position);
                                return true;
                            }
                            if (id == R.id.cusMenuTransactions) {
                                customerdialog.dismiss();

                                if (Utilities.isNetworkAvailable(context)) {
                                    new AsyncRecentTransactions().execute(CustomerCode[position]);
                                    notifyDataSetChanged();
                                }

                                return true;
                            }
                            if (id == R.id.cusMenuNotPurchase) {
                                DataBaseAdapter objDataBaseAdapter = new DataBaseAdapter(context);
                                try{
                                    objDataBaseAdapter.open();
                                    String status = objDataBaseAdapter.checkNotPurchasedDetails(CustomerCode[position]);
                                    if(status.equals(Constants.RESULT_FAILURE)){
                                        Toast toast = Toast.makeText(getApplicationContext(),getString(R.string.toast_already_cus_in_not_purchase),Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                        return false;
                                    }
                                    if(objDataBaseAdapter != null)
                                        objDataBaseAdapter.close();

                                    dialogNotPurchased = new Dialog(context);
                                    dialogNotPurchased.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialogNotPurchased.setContentView(R.layout.addnotpurchased);
                                    ImageView closepopup = (ImageView)dialogNotPurchased.findViewById(R.id.closepopup);
                                    txtremarks = (TextView)dialogNotPurchased.findViewById(R.id.txt_notpurchased_remarks);
                                    Button btnsave=(Button) dialogNotPurchased.findViewById(R.id.btnsave_notpurchased);

                                    closepopup.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialogNotPurchased.dismiss();
                                        }
                                    });
                                    txtremarks.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            getNotPurchasedRemarks();
                                        }
                                    });

                                    btnsave.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            String remarks = txtremarks.getText().toString();
                                            if(Utilities.isNullOrEmpty(remarks) || Utilities.isNullOrEmpty(getRemarksCode)){
                                                Toast.makeText(getApplicationContext(),getString(R.string.remarks_warning),Toast.LENGTH_SHORT).show();
                                            }else {
                                                insertNotPurchasedDetails(CustomerCode[position],getRemarksCode);
                                            }

                                        }
                                    });
                                    dialogNotPurchased.show();
                                }catch (Exception e){
                                    if(objDataBaseAdapter == null)
                                        objDataBaseAdapter.open();
                                    String geterrror = e.toString();
                                    objDataBaseAdapter.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + "Exception in not purchase selection", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                    objDataBaseAdapter.close();
                                }
                                return true;
                            }
                            return false;
                        }
                    });
                    popup.show();
                }
            });

            return convertView;
        }

        private class ViewHolder {
            private TextView listcustomername,listgstin,listcustomertype,listTotalOrder,listordercount;
            private LinearLayout LLMenu,LLL;
            private CardView LLTotalOrder;

        }
    

    }
//convert order to sales
    public void orderToSales(int position) {
        isorderopenshowpopup = true;
        LayoutInflater inflater = (LayoutInflater) SalesActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.orderpopup, null);
        orderwindow = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, false);

        orderexpList = (ExpandableListView) layout.findViewById(R.id.order_expandible_listview);
        ImageView close = (ImageView) layout.findViewById(R.id.order_close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderwindow.dismiss();

                isorderopenpopup = false;
            }
        });
        //window.setOutsideTouchable(true);
        orderwindow.showAtLocation(layout, Gravity.BOTTOM, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //setUpAdapter();
        orderwindow.setOutsideTouchable(false);
        isorderopenpopup = true;
        setOrderChildItems(CustomerCode[position]);

        orderExpandableAdapter = new OrderExpandableAdapter(context, orderlistDataHEader, orderlisthash);
        orderexpList.setAdapter(orderExpandableAdapter);

        orderexpList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup)
                    orderexpList.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });

        // Set cutomer details
        txtcustomername.setText(String.valueOf(CustomerNameTamil[position]));
        customercode = CustomerCode[position];
        gstnnumber = GSTN[position];
        getschemeapplicable = SchemeApplicable[position];
        if (gstnnumber.equals("") || gstnnumber.equals(null) || gstnnumber.equals("0")) {
            togglegstin.setBackgroundColor(getResources().getColor(R.color.graycolor));
        } else {
            togglegstin.setBackgroundColor(getResources().getColor(R.color.green));
        }
        radio_cash.setEnabled(true);
        radio_credit.setEnabled(true);
//        txtpaymenttype.setEnabled(true);

        if (customertypecode[position].equals("1")) {
            radio_cash.setChecked(true);
            radio_credit.setEnabled(false);
            //radio_cash.setEnabled(false);

//            txtpaymenttype.setText("CASH");
            getpaymenttypecode = "1";
            getpaymenttypename = "CASH";
            // txtpaymenttype.setEnabled(false);

        }
        if (customertypecode[position].equals("2")) {
            radio_credit.setEnabled(true);
            //radio_cash.setEnabled(true);
            radio_cash.setChecked(false);

//            txtpaymenttype.setText("CREDIT");
            getpaymenttypecode = "2";
            getpaymenttypename = "CREDIT";
            // txtpaymenttype.setEnabled(false);
        }

//        if (customertypecode[position].equals("4")) {
//            //radio_credit.setEnabled(true);
//            //radio_cash.setEnabled(true);
//            //radio_cash.setChecked(true);
//
//            txtpaymenttype.setText("ONLINE");
//            getpaymenttypecode = "4";
//            getpaymenttypename = "ONLINE";
//            // txtpaymenttype.setEnabled(false);
//        }


        togglegstin.setText("GSTIN \n " + gstnnumber);
        togglegstin.setTextOn("GSTIN \n " + gstnnumber);
        togglegstin.setTextOff("GSTIN \n " + gstnnumber);
        salesitems.clear();
        SalesItemAdapter adapter = new SalesItemAdapter(context, salesitems, "0");
        lv_sales_items.setAdapter(adapter);
        if (staticreviewsalesitems.size() > 0) {
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
                            OrdertoSalesCalculateTotal();
                            billisttotalamount.setText("0.00");
                            DataBaseAdapter objdatabaseadapter = new DataBaseAdapter(context);
                            objdatabaseadapter.open();
                            objdatabaseadapter.DeleteSalesItemCart();
                            objdatabaseadapter.close();

                            Utilities.deleteOrderToSalesConverstionDetails(getApplicationContext());

                            SalesItemAdapter adapter = new SalesItemAdapter(context, salesitems, "0");
                            lv_sales_items.setAdapter(adapter);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            if (orderwindow != null || orderwindow.isShowing()) {
                                orderwindow.dismiss();
                            }
                            if (orderitemswindow != null || orderitemswindow.isShowing()) {
                                orderitemswindow.dismiss();
                            }

                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        customerdialog.dismiss();
    }
    public void OrdertoSalesCalculateTotal() {

        try{
            SalesActivity.ordertosalestotal = 0.00;
            final DecimalFormat dft = new DecimalFormat("0.00");
            double res1 = 0;
            double res2 = 0;
            for (int i = 0; i < SalesActivity.staticreviewsalesitems.size(); i++) {
                String saleseqty = SalesActivity.staticreviewsalesitems.get(i).getItemqty();
                String salessubtotal = SalesActivity.staticreviewsalesitems.get(i).getSubtotal();
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
//                res2 = res2 + Double.parseDouble(getsaleseqty);
            }
//            res1 =  ordertosalestotal;
            SalesActivity.ordertosalestotal = res1;
            billisttotalamount.setText(dft.format(res1));
        }catch (Exception e){
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }
    public class OrderExpandableAdapter extends BaseExpandableListAdapter {

        Context ctx;
        private List<String> listDataheader;
        private HashMap<String,List<String>> listHashMap;
        SalesActivity objsales = new SalesActivity();
        //public static ArrayList<ArrayList<String>> childList;
        //private String[] parents;
        public OrderExpandableAdapter(Context ctx, List<String> listDataheader, HashMap<String, List<String>> listHashMap) {
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
            final String headertitle = (String)getGroup(i);
            if(view == null){
                LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.orderparent_layout, null);

            }

            TextView billdate_textview = (TextView) view.findViewById(R.id.recenttrans_billdate);
            billdate_textview.setText(headertitle.split("#")[0]);
            TextView billno_textview = (TextView) view.findViewById(R.id.recenttrans_billno);
            billno_textview.setText(headertitle.split("#")[1]);
            df = new DecimalFormat("0.00");
            TextView amount_textview = (TextView) view.findViewById(R.id.recenttrans_amount);
            amount_textview.setText(df.format(Double.parseDouble(headertitle.split("#")[2])));
            SalesActivity.orderbillno = headertitle.split("#")[1];
            TextView buttonview = (TextView) view.findViewById(R.id.converttosales);
            buttonview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DataBaseAdapter databaseadapter = null;
                    try {
                        databaseadapter = new DataBaseAdapter(context);
                        databaseadapter.open();
                        Cursor Cur = null;
                        Cursor Cur1 = null;
                        Cursor Cur2 = null;
                        Cur1 = databaseadapter.GetSalesOrderListItemDatasDB(headertitle.split("#")[3],headertitle.split("#")[4],headertitle.split("#")[5]);
                        ordertransactionno=headertitle.split("#")[3];
                        orderfinancialyearcode=headertitle.split("#")[4];
                        for(int j=0;j<Cur1.getCount();j++) {
                            DecimalFormat dft = new DecimalFormat("0.00");
                            databaseadapter = new DataBaseAdapter(context);
                            databaseadapter.open();
                            Cur2 = databaseadapter.GetItemDB(Cur1.getString(0), preferenceMangr.pref_getString(Constants.KEY_GET_MENU_ROUTECODE), preferenceMangr.pref_getString(Constants.KEY_GETAREACODE));
                            if(Cur2.getCount() > 0){

                                boolean addedqty = false;
                                String itemcode = Cur2.getString(0);
                                String companycode = Cur2.getString(1);
                                String brandcode =  Cur2.getString(2);
                                String manualitemcode =  Cur2.getString(3);
                                String itemname =  Cur2.getString(4);
                                String itemnametamil = Cur2.getString(5);

                                String unitcode = Cur2.getString(6);
                                String unitweightunitcode = Cur2.getString(7);
                                String unitweight = Cur2.getString(8);
                                String uppunitcode = Cur2.getString(9);
                                String uppweight = Cur2.getString(10);

                                String itemcategory = Cur2.getString(11);
                                String parentitemcode = Cur2.getString(12);
                                String allowpriceedit = Cur2.getString(13);

                                String allownegativestock = Cur2.getString(14);
                                String allowdiscount = Cur2.getString(15);

                                String labelstock = Cur2.getString(16);
                                String unitname = Cur2.getString(17);
                                String noofdecimals = Cur2.getString(18);
                                String oldprice =  Cur2.getString(19);
                                String newprice = Cur2.getString(20);
                                String colourcode = Cur2.getString(21);
                                String hsn = Cur2.getString(22);
                                String tax = Cur2.getString(23);
                                String itemqty = "0";
                                String subtotal = "0";
                                String routeallowpricedit = Cur2.getString(24);
                                String discount = "";
                                String freeflag =  "";
                                String purchaseitemcode = "0";
                                String freeitemcode =  "";
                                String dummyprice = Cur2.getString(20);
                                String ratecount = Cur2.getString(29);
                                String freecount = Cur2.getString(30);
                                String orgprice = Cur2.getString(20);
                                String itemsubgroupcode = Cur2.getString(32);

                                String orderqty = Cur1.getString(2);
                                String itemrate = Cur1.getString(4);
                                String listitemtotal = Cur1.getString(6);
                                String qty = Cur1.getString(2);

                                if(Double.parseDouble(labelstock.toString())<=0)
                                {
                                    if(!parentitemcode.equals("0"))
                                    {
                                        DataBaseAdapter objdatabaseadapter = null;
                                        Cursor getStockCur = null;
                                        try {
                                            objdatabaseadapter = new DataBaseAdapter(context);
                                            objdatabaseadapter.open();
                                            String insertstock = null;
                                            Double getparentstock = 0.00;
                                            getStockCur = objdatabaseadapter.GetStockForItem(parentitemcode);
                                            Cursor getTempStockCur = objdatabaseadapter.GetTempStockForItem(parentitemcode);
                                            if (getStockCur.getCount() > 0) {
                                                // for (int i = 0; i < getStockCur.getCount(); i++) {
                                                if (getTempStockCur.getCount() > 0) {
                                                    getparentstock = getStockCur.getDouble(0) - getTempStockCur.getDouble(0);
                                                }
                                                else
                                                    getparentstock = getStockCur.getDouble(0);
                                                Double total_stock = getparentstock * getStockCur.getDouble(3);

                                                if (getparentstock > 0) {
                                                    if(total_stock <= Double.parseDouble(qty)) {
                                                        qty = String.valueOf(total_stock);




                                                        insertstock = objdatabaseadapter.insertOrderStockConversion(parentitemcode, String.valueOf(total_stock),
                                                                String.valueOf(getparentstock),itemcode, preferenceMangr.pref_getString(Constants.KEY_GET_MENU_SCHEDULECODE),SalesActivity.orderbillno);
                                                    }
                                                    else
                                                    {
                                                        Double remainingstock = Double.parseDouble(qty);
                                                        Double temp_parentstock=getparentstock;
                                                        Double parent_weight =  getStockCur.getDouble(3);
                                                        Double parentqty = remainingstock / parent_weight;
                                                        Double converted_qty = Math.ceil(parentqty);
//                                                     int inc=0;
//                                                     do{
//                                                         if(temp_parentstock > 0) {
//                                                             inc = inc+1;
//                                                             temp_parentstock = temp_parentstock - 1;
//                                                             remainingstock = remainingstock - parent_weight;
//                                                         }
//
//                                                     }while(remainingstock >0);
                                                        total_stock = converted_qty * parent_weight;
                                                        Double remaining_child_stock = total_stock - remainingstock;
                                                        insertstock = objdatabaseadapter.insertOrderStockConversion(parentitemcode, String.valueOf(total_stock),
                                                                String.valueOf(converted_qty),itemcode, preferenceMangr.pref_getString(Constants.KEY_GET_MENU_SCHEDULECODE),SalesActivity.orderbillno);
                                                        qty = qty;
                                                    }




                                                } else {
                                                    qty = "0";
                                                }
                                                //}
                                            }
                                        }
                                        catch (Exception e)
                                        {
                                            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                            mDbErrHelper.open();
                                            String geterrror = e.toString();
                                            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                            mDbErrHelper.close();
                                        }

                                    }
                                    else
                                    {
                                        qty =  "0";
                                    }

                                }
                                else
                                {
                                    if(Double.parseDouble(orderqty.toString()) < Double.parseDouble(labelstock.toString()) )
                                    {
                                        qty = Cur1.getString(2);
                                    }
                                    else
                                    {
                                        qty = qty = Cur1.getString(2);
                                        String stockqty =  Cur2.getString(16);
                                        Double balance_qty = Double.parseDouble(qty) - Double.parseDouble(stockqty);
                                        Double getqty = 0.00;

                                        if(!parentitemcode.equals("0"))
                                        {
                                            DataBaseAdapter objdatabaseadapter = null;
                                            Cursor getStockCur = null;
                                            try {
                                                objdatabaseadapter = new DataBaseAdapter(context);
                                                objdatabaseadapter.open();
                                                String insertstock = null;
                                                Double getparentstock = 0.00;
                                                double tempStok = 0.0;
                                                getStockCur = objdatabaseadapter.GetStockForItem(parentitemcode);
                                                Cursor getTempStockCur = objdatabaseadapter.GetTempStockForItem(parentitemcode);
                                                if (getStockCur.getCount() > 0) {
                                                    if(getTempStockCur.getCount() >0 ){
                                                        tempStok = getTempStockCur.getDouble(0);
                                                    }
                                                    // for (int i = 0; i < getStockCur.getCount(); i++) {
                                                    getparentstock = getStockCur.getDouble(0) - tempStok;
                                                    Double total_stock = getparentstock * getStockCur.getDouble(3);

                                                    if (getparentstock > 0) {
                                                        if(total_stock <= balance_qty) {
                                                            getqty = total_stock;




                                                            insertstock = objdatabaseadapter.insertOrderStockConversion(parentitemcode, String.valueOf(total_stock),
                                                                    String.valueOf(getparentstock),itemcode, preferenceMangr.pref_getString(Constants.KEY_GET_MENU_SCHEDULECODE),SalesActivity.orderbillno);
                                                        }
                                                        else
                                                        {
                                                            Double remainingstock = balance_qty;
                                                            Double temp_parentstock=getparentstock;
                                                            Double parent_weight =  getStockCur.getDouble(3);
                                                            Double parentqty = remainingstock / parent_weight;
                                                            Double converted_qty = Math.ceil(parentqty);
//                                                            int inc=0;
//                                                            do{
//                                                                if(temp_parentstock > 0) {
//                                                                    inc = inc+1;
//                                                                    temp_parentstock = temp_parentstock - 1;
//                                                                    remainingstock = remainingstock - parent_weight;
//                                                                }
//
//                                                            }while(remainingstock >0);
                                                            total_stock = converted_qty * parent_weight;
                                                            Double remaining_child_stock = total_stock - remainingstock;
                                                            insertstock = objdatabaseadapter.insertOrderStockConversion(parentitemcode, String.valueOf(total_stock),
                                                                    String.valueOf(converted_qty),itemcode, preferenceMangr.pref_getString(Constants.KEY_GET_MENU_SCHEDULECODE),SalesActivity.orderbillno);
                                                            getqty = balance_qty;
                                                        }




                                                    } else {
                                                        getqty = 0.00;
                                                    }
                                                    //}
                                                }
                                            }
                                            catch (Exception e)
                                            {
                                                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                                mDbErrHelper.open();
                                                String geterrror = e.toString();
                                                mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                                mDbErrHelper.close();
                                            }

                                        }
                                        qty = String.valueOf(Double.parseDouble(stockqty) + getqty);

                                    }
                                }

                                if(Double.parseDouble(orderqty.toString()) > Double.parseDouble(qty) )
                                {
                                    Toast toast = Toast.makeText(getApplicationContext(),"Insufficient stock for " + itemnametamil , Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }

                                if ((!(qty).equals("") && !(qty).equals(" ")  && !(qty).equals("0")  && !(qty).equals("0.0")


                                        && !(qty).equals(null)
                                        && !(qty).equals(".")
                                        && Double.parseDouble(qty.toString()) > 0)
                                        || allownegativestock.equals("yes")) {

                                    String isfreestock = "";
                                    double getminqty = 0;
                                    double getdiscount = 0;
                                    double getparentstock = 0;


//                                    if (allownegativestock.equals("no")) {
//                                        if (Double.parseDouble(qty.toString()) >
//                                                Double.parseDouble(labelstock.toString())) {
//                                            if (itemcategory.equals("child")) {
//                                                DataBaseAdapter objdatabaseadapter = null;
//                                                Cursor getStockCur = null;
//                                                try {
//                                                    objdatabaseadapter = new DataBaseAdapter(context);
//                                                    objdatabaseadapter.open();
//                                                    getStockCur = objdatabaseadapter.GetStockForItem(parentitemcode);
//                                                    if (getStockCur.getCount() > 0) {
//                                                        // for (int i = 0; i < getStockCur.getCount(); i++) {
//                                                        getparentstock = getStockCur.getDouble(0);
//                                                        //}
//                                                    }
//                                                    if (getparentstock > 0) {
//                                                        isfreestock = "";
//                                                        String getstaticparentitemcode = parentitemcode;
//                                                        String getstaticchilditemname = itemnametamil;
//                                                        String getstaticchildunitname = unitname;
//                                                        String isparentopen = "yes";
//                                                        qty="";
//                                                        listitemtotal= "0.00";
//                                                        subtotal = "0.00";
//                                                        String getresult=DeleteItemCart(itemcode);
//                                                        if(getresult.equals("Success")) {
//                                                            itemqty = "";
//                                                            newprice ="";
//                                                        }
//                                                    } else {
//
//                                                        listitemtotal = "0.00";
//                                                        itemrate = dft.format(Double.parseDouble(dummyprice));
//                                                        qty = "";
//
//
//                                                        String getresult=DeleteItemCart(itemcode);
//                                                        if(getresult.equals("Success")) {
//                                                            itemqty = "";
//                                                            newprice ="";
//                                                        }
//
//                                                    }
//                                                } catch (Exception e) {
//                                                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
//                                                    mDbErrHelper.open();
//                                                    String geterrror = e.toString();
//                                                    mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
//                                                    mDbErrHelper.close();
//                                                }
//                                                finally {
//                                                    if (objdatabaseadapter != null)
//                                                        objdatabaseadapter.close();
//                                                    if (getStockCur != null)
//                                                        getStockCur.close();
//                                                }
//                                            } else {
//                                                listitemtotal = "0.00";
//
//                                                itemrate = dft.format(Double.parseDouble(dummyprice));
//                                                itemqty ="";
//
//                                                String getresult=DeleteItemCart(itemcode);
//                                                if(getresult.equals("Success")) {
//                                                    itemqty = "";
//                                                    newprice ="";
//                                                }
//                                            }
//                                        }
//                                    }


                                    if (!qty.toString().equals("")) {
                                        if (Double.parseDouble(qty.toString()) >0
                                                || allownegativestock.equals("yes") ) {
                                            DecimalFormat dffor = new DecimalFormat("0.00");
                                            String getdecimalvalue = noofdecimals;
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
                                            if (getschemeapplicable.equals("yes")) {

                                                DataBaseAdapter objdatabaseadapter = null;
                                                Cursor getschemeCur = null;

                                                try {
                                                    //Scheme Functionality
                                                    objdatabaseadapter = new DataBaseAdapter(context);
                                                    objdatabaseadapter.open();
                                                    getschemeCur = objdatabaseadapter.GetSchemeFORItemDB(itemcode,
                                                            preferenceMangr.pref_getString(Constants.KEY_GET_MENU_ROUTECODE),qty.toString());
                                                    if (getschemeCur.getCount() > 0) {
                                                        //  for (int i = 0; i < getschemeCur.getCount(); i++) {
                                                        getminqty = getschemeCur.getDouble(0);
                                                        getdiscount = getschemeCur.getDouble(1);
                                                        // }
                                                    }
                                                } catch (Exception e) {
                                                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                                    mDbErrHelper.open();
                                                    String geterrror = e.toString();
                                                    mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                                    mDbErrHelper.close();
                                                } finally {
                                                    if (objdatabaseadapter != null)
                                                        objdatabaseadapter.close();
                                                    if (getschemeCur != null)
                                                        getschemeCur.close();
                                                }

                                                String getitemcode = itemcode;
                                                String getqty = qty.toString();


                                                //Get Free Item variable
                                                String getfreeitemname = "";
                                                //Get free and purchase item code
                                                String getpurchaseitemcode = "";
                                                String getpurchaseqty = "";
                                                String getfreeitemcode = "";
                                                String getfreeqty = "";
                                                String getallownegativestock = "";
                                                double getfreestockitem = 0;
                                                String getitemcategory = "";
                                                String getparentcode = "";
                                                String getchilditemname = "";
                                                String getchildunitname = "";

                                                //Get Item based Free Item
                                                DataBaseAdapter objdatabaseadapterfree = null;
                                                Cursor getitemschemeCur = null;
                                                Cursor getFreeStock = null;

                                                try {
                                                    //Scheme Functionality
                                                    objdatabaseadapterfree = new DataBaseAdapter(context);
                                                    objdatabaseadapterfree.open();
                                                    getitemschemeCur = objdatabaseadapterfree.GetSchemeFORFreeItemDB(getitemcode, preferenceMangr.pref_getString(Constants.KEY_GET_MENU_ROUTECODE));
                                                    if (getitemschemeCur.getCount() > 0) {
                                                        getpurchaseitemcode = getitemschemeCur.getString(0);
                                                        getpurchaseqty = getitemschemeCur.getString(1);
                                                        getfreeitemcode = getitemschemeCur.getString(2);
                                                        getfreeqty = getitemschemeCur.getString(3);
                                                        if (Double.parseDouble(getpurchaseqty) <=
                                                                Double.parseDouble(getqty)) {

                                                            try {
                                                                //Get Stock for parent item
                                                                String getmanualfreeitemname = objdatabaseadapterfree.GetFreeitemname(getfreeitemcode);
                                                                getfreeitemname = getmanualfreeitemname;
                                                                getFreeStock = objdatabaseadapterfree.GetPurchaseItems(getfreeitemcode, preferenceMangr.pref_getString(Constants.KEY_GET_MENU_ROUTECODE), preferenceMangr.pref_getString(Constants.KEY_GETAREACODE));
                                                                if (getFreeStock.getCount() > 0) {
                                                                    for (int i = 0; i < getFreeStock.getCount(); i++) {
                                                                        //getfreeitemstock = getFreeStock.getDouble(0);
                                                                        getallownegativestock = getFreeStock.getString(14);
                                                                        getfreestockitem = getFreeStock.getDouble(16);
                                                                        getitemcategory = getFreeStock.getString(11);
                                                                        getparentcode = getFreeStock.getString(12);
                                                                        getchilditemname = getFreeStock.getString(5);
                                                                        getchildunitname = getFreeStock.getString(17);

                                                                    }
                                                                }

                                                                //Calculate Qty with total qty
                                                                Double getactualqty = Double.parseDouble(getqty);
                                                                int getfreeqtyval = (int) (getactualqty / Double.parseDouble(getpurchaseqty));
                                                                int getactualqtyvalue = (int) (getfreeqtyval * Double.parseDouble(getfreeqty));
                                                                String getcartqty = objdatabaseadapter.GetCartItemStock(getfreeitemcode);
                                                                if(getpurchaseitemcode.equals(getfreeitemcode)){
                                                                    if(Double.parseDouble(labelstock.toString())<(Double.parseDouble(String.valueOf(getactualqtyvalue))+Double.parseDouble(getqty))){
                                                                        Toast toast = Toast.makeText(getApplicationContext(),"Insufficient stock for " + getfreeitemname , Toast.LENGTH_LONG);
                                                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                                                        toast.show();
                                                                        listitemtotal = "0.00";

                                                                        return;
                                                                    }
                                                                }

                                                                if (getfreestockitem  >= Double.parseDouble(String.valueOf(getactualqtyvalue))) {
                                                                    getFreeStock = null;
                                                                    getFreeStock = objdatabaseadapterfree.GetPurchaseItems(getfreeitemcode, preferenceMangr.pref_getString(Constants.KEY_GET_MENU_ROUTECODE), preferenceMangr.pref_getString(Constants.KEY_GETAREACODE));
                                                                    if (getFreeStock.getCount() > 0) {
                                                                        for (int i = 0; i < getFreeStock.getCount(); i++) {

                                                                            double getsubtotal = Double.parseDouble(getFreeStock.getString(20)) * getactualqtyvalue;
                                                                            for ( j = 0; j < freeitems.size(); j++) {
                                                                                if (getpurchaseitemcode.equals(freeitems.get(j).getPurchaseitemcode()) &&
                                                                                        getfreeitemcode.equals(freeitems.get(j).getFreeitemcode())) {
                                                                                    freeitems.remove(j);
                                                                                }
                                                                            }
                                                                            freeitems.add(new SalesItemDetails(getFreeStock.getString(0), getFreeStock.getString(1),
                                                                                    getFreeStock.getString(2)
                                                                                    , getFreeStock.getString(3), getFreeStock.getString(4),
                                                                                    getFreeStock.getString(5), getFreeStock.getString(6),
                                                                                    getFreeStock.getString(7)
                                                                                    , getFreeStock.getString(8), getFreeStock.getString(9), getFreeStock.getString(10)
                                                                                    , getFreeStock.getString(11), getFreeStock.getString(12)
                                                                                    , getFreeStock.getString(13), getFreeStock.getString(14), getFreeStock.getString(15)
                                                                                    , getFreeStock.getString(16), getFreeStock.getString(17)
                                                                                    , getFreeStock.getString(18), getFreeStock.getString(19),
                                                                                    getFreeStock.getString(20)
                                                                                    , getFreeStock.getString(21), getFreeStock.getString(22)
                                                                                    , getFreeStock.getString(23), String.valueOf(getactualqtyvalue), String.valueOf(getsubtotal),
                                                                                    getFreeStock.getString(24), "",
                                                                                    "freeitem", getpurchaseitemcode, getfreeitemcode,getFreeStock.getString(20),"","",
                                                                                    "",getFreeStock.getString(27),"","","","",getFreeStock.getString(29)));

                                                                            Log.d("------------Free item : ",freeitems.toString());

                                                                        }
                                                                    }
                                                                } else {
                                                                    //freeitems.clear();
                                                                    if (getallownegativestock.equals("no")) {
                                                                        // if (Double.parseDouble(getqty) > getfreestockitem) {
                                                                        if (getitemcategory.equals("child")) {
                                                                            DataBaseAdapter objdatabaseadapter1 = null;
                                                                            Cursor getStockCur = null;
                                                                            try {
                                                                                //Get Stock for parent item
                                                                                objdatabaseadapter1 = new DataBaseAdapter(context);
                                                                                objdatabaseadapter1.open();
                                                                                getStockCur = objdatabaseadapter1.GetStockForItem(getparentcode);
                                                                                String getcartparentqty = objdatabaseadapter.GetCartItemStock(getparentcode);
                                                                                if (getStockCur.getCount() > 0) {
                                                                                    for (int i = 0; i < getStockCur.getCount(); i++) {
                                                                                        getparentstock = getStockCur.getDouble(0);

                                                                                    }
                                                                                    getparentstock = getparentstock - (Double.parseDouble(getcartparentqty));
                                                                                }
                                                                                if (getparentstock > 0 ) {
                                                                                    listitemtotal="0.00";
                                                                                    itemrate = dft.format(Double.parseDouble(dummyprice));
                                                                                    return;


                                                                                } else {
                                                                                    listitemtotal = "0.00";
                                                                                    itemrate = dft.format(Double.parseDouble(dummyprice));



                                                                                }
                                                                            } catch (Exception e) {
                                                                                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                                                                mDbErrHelper.open();
                                                                                String geterrror = e.toString();
                                                                                mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                                                                mDbErrHelper.close();
                                                                            } finally {
                                                                                if (objdatabaseadapter1 != null)
                                                                                    objdatabaseadapter1.close();
                                                                                if (getStockCur != null)
                                                                                    getStockCur.close();
                                                                            }
                                                                        } else {
                                                                            listitemtotal ="0.00";
                                                                            itemrate = dft.format(Double.parseDouble(dummyprice));


                                                                        }
                                                                        //}
                                                                    }
                                                                }
                                                            } catch (Exception e) {
                                                                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                                                mDbErrHelper.open();
                                                                String geterrror = e.toString();
                                                                mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                                                mDbErrHelper.close();
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
                                                    if (objdatabaseadapterfree != null)
                                                        objdatabaseadapterfree.close();
                                                    if (getitemschemeCur != null)
                                                        getitemschemeCur.close();
                                                    if (getFreeStock != null)
                                                        getFreeStock.close();
                                                }

                                            }

                                            String varQty = qty.toString();
                                            if (varQty.equals("0") || varQty.equals("") || varQty.equals(null)) {
                                                varQty = "0";
                                            }

                                            itemqty = varQty;
                                            dummyprice = itemrate;
                                            newprice = dft.format(Double.parseDouble(dummyprice));
                                            if (getminqty > 0) {
                                                Double a = Double.parseDouble(qty.toString());
                                                Double b = Double.parseDouble(itemrate.toString());
                                                Double res = a * b;
                                                int getdivideqty = (int) (a / getminqty);
                                                Double getactucaldiscount = getdivideqty * getdiscount;

                                                if (getdivideqty > 0) {
                                                    double newrate = Double.parseDouble(newprice);
                                                    double getnewrate = newrate - getdiscount ;
                                                    itemrate = dft.format(getnewrate);
                                                    newprice = dft.format(getnewrate);
                                                    Double getres = getnewrate * Double.parseDouble(varQty);
                                                    listitemtotal = dft.format(getres);

                                                    discount = "";
                                                }
                                                else {
                                                    discount ="";
                                                }
                                                subtotal = dft.format(Double.parseDouble(listitemtotal.toString()));
                                                newprice = itemrate.toString();
                                            } else {
                                                Double a = Double.parseDouble(qty.toString());
                                                Double b = Double.parseDouble(itemrate.toString());
                                                Double res = a * b;
                                                itemqty = qty.toString();
                                                listitemtotal = dft.format(res);
                                                subtotal =  dft.format(res);
                                                newprice = itemrate.toString();
                                                discount = "";
                                            }
                                            addedqty = true;
                                            // order to sales total calculation
//                                            OrdertoSalesCalculateTotal();
                                        } else {
                                            Toast toast = Toast.makeText(getApplicationContext(),"Please enter valid quantity", Toast.LENGTH_LONG);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
                                        }
                                    } else {

                                        discount = "";
                                        itemqty = qty.toString();
                                        subtotal = listitemtotal.toString();

                                    }

                                    if(addedqty) {

                                        boolean addedcart = false;
                                        boolean addedpricecart = true;
                                        boolean checkfreeitem = false;
                                        DataBaseAdapter dataBaseAdapter = null;
                                        Cursor getcartdatas = null;
                                        String insertcart="";
                                        try {
                                            dataBaseAdapter = new DataBaseAdapter(context);
                                            dataBaseAdapter.open();
                                            if (!qty.equals("")
                                                    && !qty.equals(null)
                                                    && !newprice.equals(null)
                                                    && !newprice.equals("")) {
                                                if (Double.parseDouble(qty) > 0
                                                        && (Double.parseDouble(subtotal)) > 0) {
                                                    if (Double.parseDouble(newprice) > 0) {
                                                        double getsubtotal = Double.parseDouble(newprice) *
                                                                Double.parseDouble(qty);
                                                        if (!discount.equals("") && !discount.equals("0")
                                                                && !discount.equals(null)) {
                                                            getsubtotal = getsubtotal - Double.parseDouble(discount);
                                                        } else {
                                                            getsubtotal = getsubtotal;
                                                        }
                                                        subtotal = String.valueOf(getsubtotal);
                                                        double getorgprice = Double.parseDouble(orgprice) ;

                                                        orgprice = String.valueOf(dft.format(getorgprice));

                                                        insertcart = dataBaseAdapter.insertSalesCart(String.valueOf(itemcode), String.valueOf(companycode),
                                                                String.valueOf(brandcode), String.valueOf(manualitemcode)
                                                                , String.valueOf(itemname), String.valueOf(itemnametamil),
                                                                String.valueOf(unitcode), String.valueOf(unitweightunitcode)
                                                                , String.valueOf(unitweight), String.valueOf(uppunitcode)
                                                                , String.valueOf(uppweight), String.valueOf(itemcategory),
                                                                String.valueOf(parentitemcode),
                                                                String.valueOf(allowpriceedit), String.valueOf(allownegativestock)
                                                                , String.valueOf(allowdiscount),
                                                                String.valueOf(labelstock), String.valueOf(unitname),
                                                                String.valueOf(noofdecimals),
                                                                String.valueOf(oldprice),
                                                                String.valueOf(newprice), String.valueOf(colourcode), String.valueOf(hsn),
                                                                String.valueOf(tax), String.valueOf(itemqty), String.valueOf(getsubtotal)
                                                                , String.valueOf(routeallowpricedit), String.valueOf(discount), "",
                                                                String.valueOf(purchaseitemcode), String.valueOf(freeitemcode),"","","","",
                                                                String.valueOf(orgprice));
                                                    }
                                                }else{
                                                    try{
                                                        Cursor empcur = dataBaseAdapter.Getemptyproductcount(itemsubgroupcode);
                                                        if(Integer.parseInt(empcur.getString(0))>0){
                                                            if (Double.parseDouble(newprice) > 0) {
                                                                double getsubtotal = Double.parseDouble(newprice) *  Double.parseDouble(itemqty);
                                                                if (!discount.equals("") && !discount.equals("0") && !discount.equals(null)) {
                                                                    getsubtotal = getsubtotal - Double.parseDouble(discount);
                                                                } else {
                                                                    getsubtotal = getsubtotal;
                                                                }
                                                                subtotal = String.valueOf(getsubtotal);
                                                                double getorgprice = Double.parseDouble(orgprice);
                                                                orgprice = String.valueOf(dft.format(getorgprice));

                                                                insertcart = dataBaseAdapter.insertSalesCart(String.valueOf(itemcode), String.valueOf(companycode),
                                                                        String.valueOf(brandcode), String.valueOf(manualitemcode)
                                                                        , String.valueOf(itemname), String.valueOf(itemnametamil),
                                                                        String.valueOf(unitcode), String.valueOf(unitweightunitcode)
                                                                        , String.valueOf(unitweight), String.valueOf(uppunitcode)
                                                                        , String.valueOf(uppweight), String.valueOf(itemcategory),
                                                                        String.valueOf(parentitemcode),
                                                                        String.valueOf(allowpriceedit), String.valueOf(allownegativestock)
                                                                        , String.valueOf(allowdiscount),
                                                                        String.valueOf(labelstock), String.valueOf(unitname),
                                                                        String.valueOf(noofdecimals),
                                                                        String.valueOf(oldprice),
                                                                        String.valueOf(newprice), String.valueOf(colourcode), String.valueOf(hsn),
                                                                        String.valueOf(tax), String.valueOf(itemqty), String.valueOf(getsubtotal)
                                                                        , String.valueOf(routeallowpricedit), String.valueOf(discount), "",
                                                                        String.valueOf(purchaseitemcode), String.valueOf(freeitemcode),"","","","",String.valueOf(orgprice));
                                                            }
                                                        }

                                                    }catch (Exception e){
                                                        Toast.makeText(context,String.valueOf(e),Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                            }

                                            for (int k = 0; k < freeitems.size(); k++) {
                                                Log.e(freeitems.get(k).toString(),"datas free items");
                                                insertcart = dataBaseAdapter.insertFreeSalesCart(String.valueOf(freeitems.get(k).getItemcode()), String.valueOf(freeitems.get(k).getCompanycode()),
                                                        String.valueOf(freeitems.get(k).getBrandcode()), String.valueOf(freeitems.get(k).getManualitemcode())
                                                        , String.valueOf(freeitems.get(k).getItemname()),String.valueOf( freeitems.get(k).getItemnametamil()),
                                                        String.valueOf(freeitems.get(k).getUnitcode()), String.valueOf(freeitems.get(k).getUnitweightunitcode())
                                                        , String.valueOf(freeitems.get(k).getUnitweight()), String.valueOf(freeitems.get(k).getUppunitcode())
                                                        , String.valueOf(freeitems.get(k).getUppweight()), String.valueOf(freeitems.get(k).getItemcategory()),
                                                        String.valueOf(freeitems.get(k).getParentitemcode()),
                                                        String.valueOf(freeitems.get(k).getAllowpriceedit()), String.valueOf(freeitems.get(k).getAllownegativestock())
                                                        , String.valueOf(freeitems.get(k).getAllowdiscount()),
                                                        String.valueOf(freeitems.get(k).getStockqty()), String.valueOf(freeitems.get(k).getUnitname()),
                                                        String.valueOf(freeitems.get(k).getNoofdecimals()),
                                                        String.valueOf(freeitems.get(k).getOldprice()),
                                                        String.valueOf(freeitems.get(k).getNewprice()),String.valueOf(freeitems.get(k).getColourcode()), String.valueOf(freeitems.get(k).getHsn()),
                                                        String.valueOf(freeitems.get(k).getTax()), String.valueOf(freeitems.get(k).getItemqty()), String.valueOf(freeitems.get(k).getSubtotal())
                                                        , String.valueOf(freeitems.get(k).getRouteallowpricedit()), String.valueOf(freeitems.get(k).getDiscount()), String.valueOf(freeitems.get(k).getFreeflag()),
                                                        String.valueOf(freeitems.get(k).getPurchaseitemcode()), String.valueOf(freeitems.get(k).getFreeitemcode()),"","","","","",String.valueOf(freeitems.get(k).getNewprice()));

                                                checkfreeitem = true;
                                            }
                                            getcartdatas = dataBaseAdapter.GetSalesItemsCart();
                                            if(getcartdatas.getCount()>0) {
                                                staticreviewsalesitems.clear();
                                                for (int i = 0; i < getcartdatas.getCount(); i++) {
                                                    staticreviewsalesitems.add(new SalesItemDetails(getcartdatas.getString(1), getcartdatas.getString(2),
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
                                                            getcartdatas.getString(30), getcartdatas.getString(31) ,getcartdatas.getString(21),"","",getcartdatas.getString(32),"","",
                                                            "","",getcartdatas.getString(36),""));
                                                    getcartdatas.moveToNext();
                                                }
                                            }
                                        } catch (Exception e) {
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

                                        if (checkfreeitem) {
                                            freeitems.clear();
                                        }
                                        //Collections.sort(staticreviewsalesitems, new SalesItemDetails.SortbyPurchaseItemDesc());
                                        totalcartitems.setText(String.valueOf(staticreviewsalesitems.size()));
                                    }
                                } else {

                                    discount = "";
                                    String isparentopen = "";
                                    listitemtotal = "0.00";
                                    itemrate = dft.format(Double.parseDouble(dummyprice));

                                    newprice = dft.format(Double.parseDouble(dummyprice));
                                    itemqty = qty;
                                    subtotal = listitemtotal.toString();
                                    String getitemcode = itemcode;
                                    DataBaseAdapter objdatabaseadapter = null;
                                    Cursor getcartdatas = null;
                                    try {
                                        objdatabaseadapter = new DataBaseAdapter(context);
                                        objdatabaseadapter.open();
                                        String getresult = objdatabaseadapter.DeleteItemInCart(getitemcode);
                                        if(getresult.equals("Success")){
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
                                                            getcartdatas.getString(30), getcartdatas.getString(31),getcartdatas.getString(21),
                                                            "","",getcartdatas.getString(32),"","","",
                                                            "",getcartdatas.getString(32),""));
                                                    getcartdatas.moveToNext();
                                                }
                                            }
                                        }
                                        totalcartitems.setText(String.valueOf(staticreviewsalesitems.size()));
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
                                    //  return;
                                }




                            }

                            Cur1.moveToNext();
                        }
                        // order to sales total calculation
//                        salesitems.clear();
//                        salesitems = staticreviewsalesitems;
                        OrdertoSalesCalculateTotal();
                        if(SalesActivity.staticreviewsalesitems.size()>0) {
                            isorderitemsopenshowpopup = true;
                            LayoutInflater inflater = (LayoutInflater) SalesActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View layout = inflater.inflate(R.layout.orderitemspopup, null);
                            orderitemswindow = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT, false);
                            ImageView close = (ImageView) layout.findViewById(R.id.order_close);
                            final Button btn_save_schedule = (Button) layout.findViewById(R.id.btnSaveItems);
                            btn_save_schedule.setOnClickListener(new View.OnClickListener() {
                                                                     @Override
                                                                     public void onClick(View view) {
                                                                         orderitemswindow.dismiss();
                                                                         orderwindow.dismiss();
                                                                         Intent i = new Intent(context, ReviewActivity.class);
                                                                         i.putExtra("Payment_Type",radio_cash.isChecked());
                                                                         //i.putExtra(Constants.KEY_ORDER_TO_SALES_TRANS_NO, headertitle.split("#")[3]);
                                                                         //i.putExtra(Constants.KEY_ORDER_TO_SALES_FINANCIALYEAR, headertitle.split("#")[4]);
                                                                         //i.putExtra(Constants.KEY_ORDER_TO_SALES_COMPANYCODE, headertitle.split("#")[5]);
                                                                         preferenceMangr.pref_putString(Constants.KEY_ORDER_TO_SALES_TRANS_NO, headertitle.split("#")[3]);
                                                                         preferenceMangr.pref_putString(Constants.KEY_ORDER_TO_SALES_FINANCIALYEAR, headertitle.split("#")[4]);
                                                                         preferenceMangr.pref_putString(Constants.KEY_ORDER_TO_SALES_COMPANYCODE, headertitle.split("#")[5]);
                                                                         startActivity(i);
                                                                     }
                                                                 }
                            );

                            close.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    orderitemswindow.dismiss();

                                    isorderitemsopenshowpopup = false;

                                    staticreviewsalesitems.clear();
                                    //Set total cart item value
                                    totalcartitems.setText(String.valueOf(staticreviewsalesitems.size()));
                                    salesitems.clear();
                                    gstnnumber = "";
                                    OrdertoSalesCalculateTotal();
                                    billisttotalamount.setText("0.00");

                                    DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
                                    dataBaseAdapter.open();
                                    dataBaseAdapter.DeleteSalesItemCart();
                                    dataBaseAdapter.close();

                                }
                            });
                            //orderitemswindow.showAtLocation(layout, Gravity.BOTTOM, 0, 140);
                            orderitemswindow.showAtLocation(layout, Gravity.BOTTOM, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                            //setUpAdapter();
                            orderitemswindow.setOutsideTouchable(false);
                            isorderitemsopenshowpopup = true;
                            listordereditems = (ListView) layout.findViewById(R.id.listordereditems);
                            orderedItemsAdapter = new OrderedItemsAdapter(context, SalesActivity.staticreviewsalesitems);
                            listordereditems.setAdapter(orderedItemsAdapter);
                        }
                        else{
                            Toast toast = Toast.makeText(getApplicationContext(), "No Stock available", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }



                    }catch (Exception e){
                        DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                        mDbErrHelper.open();
                        String geterrror = e.toString();
                        mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        mDbErrHelper.close();
                    }

                }
            });


            return view;
        }

        @Override
        public View getChildView(int i, int j, boolean b, View view, ViewGroup viewGroup) {
            String getsubgroupcode="";
            final String childtext = (String)getChild(i,j);
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            if(view == null){
//
//                view = inflater.inflate(R.layout.order_transaction_list, null);
//            }
            if (j == 0) {
                view = inflater.inflate(R.layout.trans_header_layout, null);
                TextView itemname = (TextView) view.findViewById(R.id.header1);
                itemname.setText(childtext.split("#")[0]);
                TextView rate = (TextView) view.findViewById(R.id.header2);
                rate.setText(childtext.split("#")[1]);
                TextView qty = (TextView) view.findViewById(R.id.header3);
                qty.setText(childtext.split("#")[2]);

                TextView amount_textview = (TextView) view.findViewById(R.id.header4);
                amount_textview.setText(childtext.split("#")[3]);

            }
            else {
                view = inflater.inflate(R.layout.order_transaction_list, null);
                TextView billdate_textview = (TextView) view.findViewById(R.id.row_trans_list_item_name);
                billdate_textview.setText(childtext.split("#")[0]);
                TextView rate_textview = (TextView) view.findViewById(R.id.row_trans_list_item_rate);

                df = new DecimalFormat("0.00");


                rate_textview.setText(df.format(Double.parseDouble(childtext.split("#")[1])));
                TextView billno_textview = (TextView) view.findViewById(R.id.row_trans_list_item_qty);
                billno_textview.setText(childtext.split("#")[2]+" "+childtext.split("#")[4]);

                TextView amount_textview = (TextView) view.findViewById(R.id.row_trans_list_item_amount);
                amount_textview.setText(childtext.split("#")[3]+" "+childtext.split("#")[4]);

            }

            return  view;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }

    private  void setOrderChildItems(String customercode){
        orderlistDataHEader = new ArrayList<>();
        orderlisthash = new HashMap<>();

        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        Cursor Cur1=null;
        Cursor Cur2= null;
        Cursor Cur3= null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetCustomerOrderDB(customercode);
            if(Cur.getCount()>0) {
                for(int i=0;i<Cur.getCount();i++){
                    orderlistDataHEader.add(Cur.getString(1) + "#" +Cur.getString(2)+ "#" +Cur.getString(3)+ "#" +Cur.getString(0)+ "#" +Cur.getString(4)+ "#" +Cur.getString(5));
                    Cur1 = objdatabaseadapter.GetSalesOrderListItemDatasDB(Cur.getString(0),Cur.getString(4),Cur.getString(5));
                    List<String> orderlist = new ArrayList<>();
                    orderlist.add("Itemname" + "#" +"Rate" + "#" +"Qty" + "#" +"Stock"+ "#" +"Unit");

                    for(int j=0;j<Cur1.getCount();j++){
                        Cur2 = objdatabaseadapter.GetStockForItem(Cur1.getString(0));
                        String stock;

                        orderlist.add(Cur1.getString(1) + "#" +Cur1.getString(6) + "#" +Cur1.getString(2) + "#" +Cur2.getString(0)+ "#" +Cur1.getString(8));
                        Cur1.moveToNext();
                    }
                    orderlisthash.put(orderlistDataHEader.get(i),orderlist);
                    Cur.moveToNext();
                }

                /*Cur2 = objdatabaseadapter.GetEmptyTinGroupDB();
                if(Cur2.getCount()>0){
                    for(int i=0;i< Cur2.getCount();i++){
                        listDataHEader.add(Cur2.getString(3));
                        Cur3 = objdatabaseadapter.GetEmptyTinSubGroup_GroupDB(Cur2.getString(3));
                        List<String> subgrouplist = new ArrayList<>();
                        for(int j=0;j<Cur3.getCount();j++){
                            subgrouplist.add(Cur3.getString(1) + "-" +Cur3.getString(2));
                            Cur3.moveToNext();
                        }
                        listhash.put(listDataHEader.get(i),subgrouplist);
                        Cur.moveToNext();

                    }
                }*/


            }else{
                orderwindow.dismiss();
                Toast toast = Toast.makeText(getApplicationContext(),"No order available", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //Toast.makeText(getApplicationContext(),"Van out of stock",Toast.LENGTH_SHORT).show();

            }
        }catch (Exception e){
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
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
            return getCount();
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
                    getitemsfromcode = SubGroupCode[position];
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


    //EXPENDABLE ADAPTER

    public class ExpandableAdapter extends BaseExpandableListAdapter {

        Context ctx;
        private List<String> listDataheader;
        private HashMap<String,List<String>> listHashMap;
        SalesActivity objsales = new SalesActivity();
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
                    getitemsfromcode = finalGetsubgroupcode;
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

    // free item apapter
    public class FreeItemBaseAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater layoutInflater;
        FreeItemBaseAdapter(Context c) {
            context = c;
            layoutInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return FreeItemCode.length;
        }

        @Override
        public Object getItem(int position) {
            return FreeItemCode[position];
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

            ViewHolder mHolder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.freeitemexcesslist, parent, false);
                mHolder = new ViewHolder();
                try {
                    mHolder.listsno = (TextView) convertView.findViewById(R.id.listsno);
                    mHolder.listitemname = (TextView) convertView.findViewById(R.id.listitemname);
                    mHolder.listdistributed = (TextView) convertView. findViewById(R.id.listdistributed);
                    mHolder.listbalance = (TextView) convertView. findViewById(R.id.listbalance);
                    mHolder.listhandover = (TextView) convertView. findViewById(R.id.listhandover);
                    mHolder.listopening = (TextView)convertView.findViewById(R.id.listopening);
                    mHolder.freestocklist = (LinearLayout) convertView. findViewById(R.id.freestocklist);
                    mHolder.card_view = (CardView)convertView.findViewById(R.id.card_view);
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

                mHolder.listsno.setText(String.valueOf(FreeItemSNO[position]));
                mHolder.listitemname .setText(String.valueOf(FreeItemName[position]));
                mHolder.listdistributed.setText(String.valueOf(FreeItemDistributed[position]));
                mHolder.listbalance.setText(String.valueOf(FreeItemBalance[position]));
                mHolder.listhandover.setText(String.valueOf(FreeItemHandover[position]));
                mHolder.listopening.setText(String.valueOf(FreeItemOp[position]));


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
//                    FreeItemName,FreeItemOp,FreeItemHandover,FreeItemDistributed,FreeItemBalance,FreeItemCode
//                    customercode = FreeItemCode[position];
//                    gstnnumber = GSTN[position];
//                    getschemeapplicable = SchemeApplicable[position];
//                    customercityname = CustomerCityName[position];
//                    customerareaname = CustomerAreaName[position];
//                    customername = FreeItemName[position];
//                    customercityarea= CustomerCityName[position] + ',' + CustomerAreaName[position];
                    freeitemdialog.dismiss();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView listsno, listitemname,listdistributed,
                    listbalance,listhandover,listopening;
            private LinearLayout freestocklist;
            private CardView card_view;
        }
//        @Override
//        public int getCount() {
//            return SubGroupCode.length;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return SubGroupCode[position];
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//        @Override
//        public int getViewTypeCount() {
//            return getCount();
//        }
//        @Override
//        public int getItemViewType(int position) {
//            return position;
//        }
//        @SuppressLint("InflateParams")
//        @Override
//        public View getView(final int position, View convertView, ViewGroup parent) {
//
//            ViewHolder mHolder;
//
//            if (convertView == null) {
//                convertView = layoutInflater.inflate(R.layout.freeitemexcesslist, parent, false);
//                mHolder = new ViewHolder();;
//                try {
////                    mHolder.listsubgroup = (TextView) convertView.findViewById(R.id.listsubgroup);
//                    mHolder.listsno = (TextView) convertView.findViewById(R.id.listsno);
//                    mHolder.listitemname = (TextView) convertView.findViewById(R.id.listitemname);
//                    mHolder.listinward = (TextView) convertView. findViewById(R.id.listinward);
//                    mHolder.listoutward = (TextView) convertView. findViewById(R.id.listoutward);
//                    mHolder.listclosing = (TextView) convertView. findViewById(R.id.listclosing);
//                    mHolder.listopening = (TextView)convertView.findViewById(R.id.listopening);
//                    mHolder.vanstocklist = (LinearLayout) convertView. findViewById(R.id.vanstocklist);
//                    mHolder.listsales = (TextView)convertView.findViewById(R.id.listsales);
//                    mHolder.card_view = (CardView)convertView.findViewById(R.id.card_view);
//                } catch (Exception e) {
//                    Log.i("Customer", e.toString());
//                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
//                    mDbErrHelper.open();
//                    String geterrror = e.toString();
//                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
//                    mDbErrHelper.close();
//                }
//                convertView.setTag(mHolder);
//            } else {
//                mHolder = (ViewHolder) convertView.getTag();
//            }
//            try {
//                mHolder.listsno.setText(String.valueOf(SubGroupNameTamil[position]));
//                mHolder.listitemname .setText(String.valueOf(SubGroupNameTamil[position]));
//                mHolder.listinward.setText(String.valueOf(SubGroupNameTamil[position]));
//                mHolder.listoutward.setText(String.valueOf(SubGroupNameTamil[position]));
//                mHolder.listclosing.setText(String.valueOf(SubGroupNameTamil[position]));
//                mHolder.listopening.setText(String.valueOf(SubGroupNameTamil[position]));
//                mHolder.vanstocklist.setText(String.valueOf(SubGroupNameTamil[position]));
//                mHolder.listsales.setText(String.valueOf(SubGroupNameTamil[position]));
//                mHolder.card_view.setText(String.valueOf(SubGroupNameTamil[position]));
//
//                                VanStockDetails currentListData = getItem(position);
//                mHolder.listsno.setText(currentListData.getSno());
//                mHolder.listitemname.setText(currentListData.getItemName()+" - "+currentListData.getUOM());
//
//                DecimalFormat df;
//                String getdecimalvalue  = currentListData.getNoofdecimals();
//                String getnoofdigits = "0";
//                if(getdecimalvalue.equals("0")){
//                    getnoofdigits = "";
//                }
//                if(getdecimalvalue.equals("1")){
//                    getnoofdigits = "0";
//                }
//                if(getdecimalvalue.equals("2")){
//                    getnoofdigits = "00";
//                }
//                if(getdecimalvalue.equals("3")){
//                    getnoofdigits = "000";
//                }
//
//                df = new DecimalFormat("0.'"+getnoofdigits+"'");
//                if(getnoofdigits.equals("")) {
//                    mHolder.listinward.setText(currentListData.getInward());
//                }else{
//                    mHolder.listinward.setText(df.format(Double.parseDouble(currentListData.getInward())));
//                }
//                if(getnoofdigits.equals("")) {
//                    mHolder.listoutward.setText(currentListData.getOutward());
//                }else{
//                    mHolder.listoutward.setText(df.format(Double.parseDouble(currentListData.getOutward())));
//                }
//                if(getnoofdigits.equals("")) {
//                    mHolder.listclosing.setText(currentListData.getClosing());
//                }else{
//                    mHolder.listclosing.setText(df.format(Double.parseDouble(currentListData.getClosing())));
//                }
//                if(getnoofdigits.equals("")) {
//                    mHolder.listopening.setText(currentListData.getOpening());
//                }else{
//                    mHolder.listopening.setText(df.format(Double.parseDouble(currentListData.getOpening())));
//                }
//
//                if(getnoofdigits.equals("")) {
//                    mHolder.listsales.setText("\n"+currentListData.getSales());
//                }else{
//                    mHolder.listsales.setText("\n"+df.format(Double.parseDouble(currentListData.getSales())));
//                }
//
//
//                mHolder.listitemname.setTextColor(Color.parseColor(currentListData.getColourcode()));
//
//
//                if (position % 2 == 1) {
//                    mHolder.card_view.setCardBackgroundColor(Color.parseColor("#ffffff"));
//                } else {
//                    mHolder.card_view.setCardBackgroundColor(Color.parseColor("#caeaf3"));
//                }
//
//            } catch (Exception e) {
//                Log.i("Customer", e.toString());
//                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
//                mDbErrHelper.open();
//                String geterrror = e.toString();
//                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
//                mDbErrHelper.close();
//            }
//            convertView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    getstaticsubcode = SubGroupCode[position];
//                    getitemsfromcode = SubGroupCode[position];
//                    GetItems(SubGroupCode[position]);
//                    window.dismiss();
//                    fabgroupitem.startAnimation(rotate_backward);
//                    isFabOpen = false;
//                    isopenpopup = false;
//                }
//            });
//            return convertView;
//        }
//
//        private class ViewHolder {
//            private TextView listsno, listitemname,listinward,
//                    listoutward,listclosing,listopening,listsales;
//            private LinearLayout vanstocklist;
//            private CardView card_view;
//
//        }
//        public VanStockBaseAdapter( Context context,ArrayList<VanStockDetails> myList) {
//            this.myList = myList;
//            this.context = context;
//            inflater = LayoutInflater.from(context);
//        }
//
//
//        public View getView(final int position, View convertView, ViewGroup parent) {
//
//            thanjavurvansales.sss.VanStockBaseAdapter.ViewHolder mHolder;
//            if (convertView == null) {
//                convertView = inflater.inflate(R.layout.vansaleslist, parent, false);
//                mHolder = new thanjavurvansales.sss.VanStockBaseAdapter.ViewHolder();
//                try {
//                    mHolder.listsno = (TextView) convertView.findViewById(R.id.listsno);
//                    mHolder.listitemname = (TextView) convertView.findViewById(R.id.listitemname);
//                    mHolder.listinward = (TextView) convertView. findViewById(R.id.listinward);
//                    mHolder.listoutward = (TextView) convertView. findViewById(R.id.listoutward);
//                    mHolder.listclosing = (TextView) convertView. findViewById(R.id.listclosing);
//                    mHolder.listopening = (TextView)convertView.findViewById(R.id.listopening);
//                    mHolder.vanstocklist = (LinearLayout) convertView. findViewById(R.id.vanstocklist);
//                    mHolder.listsales = (TextView)convertView.findViewById(R.id.listsales);
//                    mHolder.card_view = (CardView)convertView.findViewById(R.id.card_view);
//
//                } catch (Exception e) {
//                    Log.i("Van stock", e.toString());
//                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
//                    mDbErrHelper.open();
//                    mDbErrHelper.insertErrorLog(e.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
//                    mDbErrHelper.close();
//                }
//                convertView.setTag(mHolder);
//            } else {
//                mHolder = (thanjavurvansales.sss.VanStockBaseAdapter.ViewHolder) convertView.getTag();
//            }
//            try {
//                VanStockDetails currentListData = getItem(position);
//                mHolder.listsno.setText(currentListData.getSno());
//                mHolder.listitemname.setText(currentListData.getItemName()+" - "+currentListData.getUOM());
//
//                DecimalFormat df;
//                String getdecimalvalue  = currentListData.getNoofdecimals();
//                String getnoofdigits = "0";
//                if(getdecimalvalue.equals("0")){
//                    getnoofdigits = "";
//                }
//                if(getdecimalvalue.equals("1")){
//                    getnoofdigits = "0";
//                }
//                if(getdecimalvalue.equals("2")){
//                    getnoofdigits = "00";
//                }
//                if(getdecimalvalue.equals("3")){
//                    getnoofdigits = "000";
//                }
//
//                df = new DecimalFormat("0.'"+getnoofdigits+"'");
//                if(getnoofdigits.equals("")) {
//                    mHolder.listinward.setText(currentListData.getInward());
//                }else{
//                    mHolder.listinward.setText(df.format(Double.parseDouble(currentListData.getInward())));
//                }
//                if(getnoofdigits.equals("")) {
//                    mHolder.listoutward.setText(currentListData.getOutward());
//                }else{
//                    mHolder.listoutward.setText(df.format(Double.parseDouble(currentListData.getOutward())));
//                }
//                if(getnoofdigits.equals("")) {
//                    mHolder.listclosing.setText(currentListData.getClosing());
//                }else{
//                    mHolder.listclosing.setText(df.format(Double.parseDouble(currentListData.getClosing())));
//                }
//                if(getnoofdigits.equals("")) {
//                    mHolder.listopening.setText(currentListData.getOpening());
//                }else{
//                    mHolder.listopening.setText(df.format(Double.parseDouble(currentListData.getOpening())));
//                }
//
//                if(getnoofdigits.equals("")) {
//                    mHolder.listsales.setText("\n"+currentListData.getSales());
//                }else{
//                    mHolder.listsales.setText("\n"+df.format(Double.parseDouble(currentListData.getSales())));
//                }
//
//
//                mHolder.listitemname.setTextColor(Color.parseColor(currentListData.getColourcode()));
//
//
//                if (position % 2 == 1) {
//                    mHolder.card_view.setCardBackgroundColor(Color.parseColor("#ffffff"));
//                } else {
//                    mHolder.card_view.setCardBackgroundColor(Color.parseColor("#caeaf3"));
//                }
//            } catch (Exception e) {
//                Log.i("vanstock value", e.toString());
//                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
//                mDbErrHelper.open();
//                String geterrror = e.toString();
//                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
//                mDbErrHelper.close();
//            }
//            return convertView;
//        }
//        private class ViewHolder {
//            TextView listsno, listitemname,listinward,
//                    listoutward,listclosing,listopening,listsales;
//            LinearLayout vanstocklist;
//            CardView card_view;
//
//        }
    }
    /************END BASE ADAPTER*********/

    protected  class AsyncCustomerDetails extends
            AsyncTask<String, JSONObject, ArrayList<CustomerDatas>> {
        ArrayList<CustomerDatas> List = null;
        JSONObject jsonObj = null;
        @Override
        protected  ArrayList<CustomerDatas> doInBackground(String... params) {
            RestAPI api = new RestAPI();
            try {
                JSONObject js_obj = new JSONObject();
                try {
                    DataBaseAdapter dbadapter = new DataBaseAdapter(context);
                    dbadapter.open();
                    Cursor mCur2 = dbadapter.GetCustomerDatasDB();
                    JSONArray js_array2 = new JSONArray();
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
                        js_array2.put(obj);
                        mCur2.moveToNext();
                    }
                    js_obj.put("JSonObject", js_array2);

                    jsonObj =  api.CustomerDetails(js_obj.toString(),context);
                    //Call Json parser functionality
                    JSONParser parser = new JSONParser();
                    //parse the json object to boolean
                    List = parser.parseCustomerDataList(jsonObj);
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
                Log.d("Customer", e.getMessage());
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
        protected void onPostExecute(ArrayList<CustomerDatas> result) {
            // TODO Auto-generated method stub
            if (result.size() >= 1) {
                if(result.get(0).CustomerCode.length>0){
                    for(int j=0;j<result.get(0).CustomerCode.length;j++){
                        DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
                        dataBaseAdapter.open();
                        dataBaseAdapter.UpdateCustomerFlag(result.get(0).CustomerCode[j]);
                        dataBaseAdapter.close();
                    }
                }

            }

        }
    }

    protected  class AsyncCheckCustomerMobileNumber extends
            AsyncTask<String, JSONObject, ArrayList<CustomerDatas>> {
        ArrayList<CustomerDatas> List = null;
        JSONObject jsonObj = null;
        String success1="";
        ProgressDialog loading;
        @Override
        protected  ArrayList<CustomerDatas> doInBackground(String... params) {
            RestAPI api = new RestAPI();
            try {
                jsonObj = api.checkCustomerMobileNumber(txtotpphoneno.getText().toString(),"checkCustomerMobileNumber.php",context);
                if(jsonObj!=null){
                    success1 = jsonObj.getString("success");
                }
            } catch (Exception e) {
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
            loading = ProgressDialog.show(context, "Checking", "Please wait...", true, true);
            loading.setCancelable(false);
            loading.setCanceledOnTouchOutside(false);
        }

        @Override
        protected void onPostExecute(ArrayList<CustomerDatas> result) {
            // TODO Auto-generated method stub
            loading.dismiss();
            try {
                if (success1.equals("2")) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Mobile Number Already Exist", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else if (success1.equals("1")) {
                    btnGetOTP.setEnabled(false);
                    txtotpphoneno.setEnabled(false);
                    DataBaseAdapter dataBaseAdapter =null;
                    JSONObject jsonObj = null;
                    RestAPI api = new RestAPI();
                    try {
                        dataBaseAdapter = new DataBaseAdapter(context);
                        dataBaseAdapter.open();
                        otppinview.requestFocus();
                        CountDownTimer("getotp");
                        networkstate = isNetworkAvailable();
                        if (networkstate == true) {
                            new Asyncgetotp().execute();
                        }else{
                            Toast toast = Toast.makeText(getApplicationContext(),"Please check internet connection", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        Log.e("GETOTP DIB", e.getMessage());
                        if(dataBaseAdapter != null)
                            dataBaseAdapter.close();
                        DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                        mDbErrHelper.open();
                        String geterrror = e.toString();
                        mDbErrHelper.insertErrorLog(geterrror.replace("'", " "),
                                this.getClass().getSimpleName() + " Sales - AsynccheckCustomerMobileNumber : GETOTP pin", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        mDbErrHelper.close();
                    }
                    finally {
                        if(dataBaseAdapter != null)
                            dataBaseAdapter.close();
                    }
                }
            }catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncScheduleDetails", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "),
                        this.getClass().getSimpleName() + " - AsynccheckCustomerMobileNumber", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        }
    }

    protected  class Asyncgetotp extends
            AsyncTask<String, JSONObject, ArrayList<CustomerDatas>> {
        ArrayList<CustomerDatas> List = null;
        JSONObject jsonObj = null;
        String success1="";
        ProgressDialog loading;
        @Override
        protected  ArrayList<CustomerDatas> doInBackground(String... params) {
            RestAPI api = new RestAPI();
            try {
                JSONObject js_obj = new JSONObject();
                try {
                    jsonObj = api.GetOTPapi(txtotpphoneno.getText().toString(),otpprocess,"" ,"otpsms.php",context);
                    if(jsonObj!=null){
                        success1 = jsonObj.getString("success");
                    }
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
                Log.d("Customer", e.getMessage());
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
            loading = ProgressDialog.show(context, "Get OTP", "Please wait...", true, true);
            loading.setCancelable(false);
            loading.setCanceledOnTouchOutside(false);
        }

        @Override
        protected void onPostExecute(ArrayList<CustomerDatas> result) {
            // TODO Auto-generated method stub
            try {
                if (success1.equals("1")) {
                    //btnVerifypin.setVisibility(View.VISIBLE);
                }else{
                    txtotpphoneno.setEnabled(true);
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
            loading.dismiss();

        }
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
                        Toast toast = Toast.makeText(getApplicationContext(), "Some items are not added to cart", Toast.LENGTH_LONG);
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
                            staticreviewsalesitems.clear();
                            //Set total cart item value
                            totalcartitems.setText(String.valueOf(staticreviewsalesitems.size()));
                            salesitems.clear();
                            gstnnumber="";

                            DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
                            dataBaseAdapter.open();
                            dataBaseAdapter.DeleteSalesItemCart();
                            dataBaseAdapter.close();

                            Utilities.deleteOrderToSalesConverstionDetails(getApplicationContext());

                            Intent i = new Intent(context, SalesListActivity.class);
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
            staticreviewsalesitems.clear();
            //Set total cart item value
            totalcartitems.setText(String.valueOf(staticreviewsalesitems.size()));
            salesitems.clear();
            gstnnumber="";

            DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
            dataBaseAdapter.open();
            dataBaseAdapter.DeleteSalesItemCart();
            dataBaseAdapter.close();

            Intent i = new Intent(context, SalesListActivity.class);
            startActivity(i);
        }

    }
    @Override
    public void onBackPressed() {
        goBack(null);
    }

    public  void GetCustomerType(){

        try{
            customertypecodearr = new String[2];
            customertypenamearr = new String[2];

            customertypenamearr[0] = "Cash Party";
            customertypecodearr[0] = "1";

            customertypenamearr[1] = "Credit Party";
            customertypecodearr[1] = "2";


            customertypedialog = new Dialog(context);
            customertypedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            customertypedialog.setContentView(R.layout.customertypepopup);
            lv_CustomertypeList = (ListView) customertypedialog.findViewById(R.id.lv_CustomerTypeList);
            ImageView close = (ImageView) customertypedialog.findViewById(R.id.close);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customertypedialog.dismiss();
                }
            });
            CustomerTypeAdapter adapter = new CustomerTypeAdapter(context);
            lv_CustomertypeList.setAdapter(adapter);
            customertypedialog.show();

        }catch (Exception e){
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }

    }

    public  void GetSalesType(){

        try{
            salestypecodearr = new String[2];
            salestypenamearr = new String[2];

            salestypenamearr[0] = "Line Sales";
            salestypecodearr[0] = "1";
            salestypenamearr[1] = "Both";
            salestypecodearr[1] = "3";

            salestypedialog = new Dialog(context);
            salestypedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            salestypedialog.setContentView(R.layout.salestypepopup);
            lv_salestypeList = (ListView) salestypedialog.findViewById(R.id.lv_SalesTypeList);
            ImageView close = (ImageView) salestypedialog.findViewById(R.id.close);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    salestypedialog.dismiss();
                }
            });
            SalesTypeAdapter adapter = new SalesTypeAdapter(context);
            lv_salestypeList.setAdapter(adapter);
            salestypedialog.show();

        }catch (Exception e){
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }

    }

    public class CustomerTypeAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;

        CustomerTypeAdapter(Context c) {
            context = c;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return customertypecodearr.length;
        }

        @Override
        public Object getItem(int position) {
            return customertypecodearr[position];
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

            ViewHolder mHolder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.customertypepopuplist, parent, false);
                mHolder = new ViewHolder();
                try {
                    mHolder.listcustomertype = (TextView) convertView.findViewById(R.id.listcustomertype);
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
                if(!String.valueOf(customertypenamearr[position]).equals("") || !String.valueOf(customertypenamearr[position]).equals("null") ||
                        !String.valueOf(customertypenamearr[position]).equals(null)) {
                    mHolder.listcustomertype.setText(String.valueOf(customertypenamearr[position]));
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
                    if(!String.valueOf(customertypenamearr[position]).equals("") || !String.valueOf(customertypenamearr[position]).equals("null") ||
                            !String.valueOf(customertypenamearr[position]).equals(null)) {
                        txtcustomertype.setText(String.valueOf(customertypenamearr[position]));
                    }
                    getcustomertypecode= customertypecodearr[position];
                    //Toast.makeText(getApplicationContext(),getcustomertypecode,Toast.LENGTH_SHORT).show();
                    customertypedialog.dismiss();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView listcustomertype;

        }

    }

    public class SalesTypeAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;

        SalesTypeAdapter(Context c) {
            context = c;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return salestypecodearr.length;
        }

        @Override
        public Object getItem(int position) {
            return salestypecodearr[position];
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

            ViewHolder mHolder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.salestypepopuplist, parent, false);
                mHolder = new ViewHolder();
                try {
                    mHolder.listsalestype = (TextView) convertView.findViewById(R.id.listsalestype);
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
                if(!String.valueOf(salestypenamearr[position]).equals("") || !String.valueOf(salestypenamearr[position]).equals("null") ||
                        !String.valueOf(salestypenamearr[position]).equals(null)) {
                    mHolder.listsalestype.setText(String.valueOf(salestypenamearr[position]));
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
                    if(!String.valueOf(salestypenamearr[position]).equals("") || !String.valueOf(salestypenamearr[position]).equals("null") ||
                            !String.valueOf(salestypenamearr[position]).equals(null)) {
                        txtsalestype.setText(String.valueOf(salestypenamearr[position]));
                    }
                    getsalestypecode= salestypecodearr[position];
                    //Toast.makeText(getApplicationContext(),getsalestypecode,Toast.LENGTH_SHORT).show();
                    salestypedialog.dismiss();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView listsalestype;

        }

    }

    //show the credit party bill type conform popup
    public void ShowPaymentTypePopup(){
        billtypedialog = new Dialog(context);
        billtypedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        billtypedialog.setContentView(R.layout.billtypepopup);
        // billtypedialog.setCancelable(false);

        ImageView closepopup = (ImageView) billtypedialog.findViewById(R.id.closepopup);
        final RadioButton conform_radio_cash = (RadioButton) billtypedialog.findViewById(R.id.conform_radio_cash);
        final RadioButton conform_radio_credit = (RadioButton) billtypedialog.findViewById(R.id.conform_radio_credit);
        Button btn_conform = (Button) billtypedialog.findViewById(R.id.btnconformbillType);

        if(radio_cash.isChecked()){
            conform_radio_cash.setChecked(true);
            conform_radio_credit.setChecked(false);
            //conform_radio_credit.setEnabled(false);
            //getpaymenttypename="CASH";
            //getpaymenttypecode = "1";
        }else if(radio_credit.isChecked()){
            conform_radio_cash.setChecked(false);
            conform_radio_credit.setChecked(true);
            conform_radio_cash.setEnabled(true);
            conform_radio_credit.setEnabled(true);
            //getpaymenttypename="CREDIT";
            // getpaymenttypecode = "2";
            togglegstin.setTextOn("GSTIN \n " + gstnnumber);
            togglegstin.setBackgroundColor(getResources().getColor(R.color.green));
            if (gstnnumber.equals("")) {
                togglegstin.setBackgroundColor(getResources().getColor(R.color.graycolor));
            }
        }
        btn_conform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(conform_radio_cash.isChecked()){
                    radio_cash.setChecked(true);
                    radio_credit.setChecked(false);
                    radio_credit.setEnabled(false);
                    getpaymenttypename="CASH";
                    getpaymenttypecode = "1";
                }else  if(conform_radio_credit.isChecked()){
                    radio_credit.setChecked(true);
                    radio_cash.setChecked(false);
                    radio_cash.setEnabled(false);
                    getpaymenttypename="CREDIT";
                    getpaymenttypecode = "2";
                    togglegstin.setChecked(true);
                    CheckToggleButton();
                    togglegstin.setTextOn("GSTIN \n " + gstnnumber);
                    togglegstin.setBackgroundColor(getResources().getColor(R.color.green));
                    if (gstnnumber.equals("")) {
                        togglegstin.setBackgroundColor(getResources().getColor(R.color.graycolor));
                    }
                }
                ShowPopupWindow();
                billtypedialog.dismiss();
                confromBilltype=true;
                //txtcustomername.setEnabled(false);
                // Toast.makeText(context,getpaymenttypecode,Toast.LENGTH_SHORT).show();
            }
        });

        closepopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                billtypedialog.dismiss();
                confromBilltype=false;
            }
        });
        billtypedialog.show();


    }
    public void showgroup(){
        if (isopenpopup) {
            window.dismiss();
            isFabOpen = true;
            isopenpopup = false;
        }
        if(confromBilltype) {
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

    public void stockconvertion(boolean isnillstockconversion, SalesItemDetails salesItemList,
                                int position, final SalesItemAdapter.ViewHolder1 mHolder,
                                String isfreestock, String getstaticparentitemcode,
                                String getstaticchilditemname, String getstaticchildunitname, String isparentopen,
                                DecimalFormat dft, String getstaticchildcode){
//        final boolean[] Isstockcoversionpopupopen={false};
//        mHolder.listitemqty.setText("");
        try{
            boolean checkchilditem = false;
            final String[] isParentOP = {isparentopen};
            final boolean[] isNillstockconversion={isnillstockconversion};
            final String[] isFreestock={isfreestock};
            final String[] Getstaticchildcode={getstaticchildcode};
            final SalesItemDetails[] SalesItemList={salesItemList};
            final SalesItemAdapter.ViewHolder1[] MHolder={mHolder};

            if(isnillstockconversion) {
                if (SalesItemList[0].getAllownegativestock().equals("no")) {
                    if (SalesItemList[0].getItemcategory().equals("child")) {
                        final int pos = (Integer) MHolder[0].listitemqty.getTag();
                        double getparentstock = 0.0;
                        DataBaseAdapter objdatabaseadapter = null;
                        Cursor getStockCur = null;
                        try {
                            //Get Stock for parent item
                            objdatabaseadapter = new DataBaseAdapter(context);
                            objdatabaseadapter.open();
                            getStockCur = objdatabaseadapter.GetStockForItem(SalesItemList[0].getParentitemcode());
                            if (getStockCur.getCount() > 0) {
                                // for (int i = 0; i < getStockCur.getCount(); i++) {
                                getparentstock = getStockCur.getDouble(0);
                                //}
                            }
                            if (getparentstock > 0) {
                                isFreestock[0] = "";
                                getstaticparentitemcode = SalesItemList[0].getParentitemcode();
                                getstaticchilditemname = SalesItemList[0].getItemnametamil();
                                getstaticchildunitname = SalesItemList[0].getUnitname();
                                checkchilditem = true;

                                MHolder[0].listitemname.setBackgroundColor(getResources().getColor(R.color.yellow));
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    MHolder[0].listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
                                }
                            } else {
                                checkchilditem=false;
                            }
                        } catch (Exception e) {
                            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                            mDbErrHelper.open();
                            String geterrror = e.toString();
                            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            mDbErrHelper.close();
                        } finally {
                            if (objdatabaseadapter != null)
                                objdatabaseadapter.close();
                            if (getStockCur != null)
                                getStockCur.close();
                        }

                    }
                }
                if (checkchilditem) {
                    double getminqty = 0;
                    double getdiscount = 0;
                    double getparentstock = 0;

                    final int pos = (Integer) MHolder[0].listitemqty.getTag();
                    DataBaseAdapter objdatabaseadapter = null;
                    Cursor getStockCur = null;
                    try {
                        //Get Stock for parent item
                        objdatabaseadapter = new DataBaseAdapter(context);
                        objdatabaseadapter.open();
                        getStockCur = objdatabaseadapter.GetStockForItem(SalesItemList[0].getParentitemcode());
                        if (getStockCur.getCount() > 0) {
                            for (int i = 0; i < getStockCur.getCount(); i++) {
                                getparentstock = getStockCur.getDouble(0);
                            }
                        }
                        if (getparentstock > 0) {
                            isFreestock[0] = "";
                            getstaticparentitemcode = SalesItemList[0].getParentitemcode();
                            getstaticchilditemname = SalesItemList[0].getItemnametamil();
                            getstaticchildunitname = SalesItemList[0].getUnitname();
                            isParentOP[0] = "yes";
                            MHolder[0].listitemname.setBackgroundColor(getResources().getColor(R.color.yellow));
                            // mHolder.listitemqty.setText("");
                        } else {
                            //mHolder.listitemname.setBackgroundColor(getResources().getColor(R.color.white));
                            Toast toast = Toast.makeText(getApplicationContext(), "Insufficient stock..", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            MHolder[0].listitemtotal.setText("0.00");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                MHolder[0].listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
                            }
                            MHolder[0].listitemrate.setText(dft.format(Double.parseDouble(
                                    SalesItemList[0].getDumyprice())));
                            //Toast.makeText(getApplicationContext(), "Insufficient stock..", Toast.LENGTH_SHORT).show();
                            //mHolder.listitemqty.setText("");
                            String getresult=DeleteItemCart(SalesItemList[0].getItemcode());
                            if(getresult.equals("Success")) {
                                SalesItemList[0].setItemqty("");
                                SalesItemList[0].setNewprice("");
                            }
                        }
                    } catch (Exception e) {
                        DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                        mDbErrHelper.open();
                        String geterrror = e.toString();
                        mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), "SalesActivity - stockConversion : GetStockForItem", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        mDbErrHelper.close();
                    } finally {
                        if (objdatabaseadapter != null)
                            objdatabaseadapter.close();
                        if (getStockCur != null)
                            getStockCur.close();
                    }
                }
            }
            if(isParentOP[0].equals("yes"))
            {
                double getparentstock = 0;
                String getparentitemname = "";
                String getunitname = "";
                String getupp = "";
                final int pos = (Integer) MHolder[0].listitemqty.getTag();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                DataBaseAdapter objdatabaseadapter = null;
                Cursor getStockCur = null;
                try {
                    //Get Stock for parent item
                    objdatabaseadapter = new DataBaseAdapter(context);
                    objdatabaseadapter.open();
                    getStockCur = objdatabaseadapter.GetStockForItem(getstaticparentitemcode);
                    String getcartqty = objdatabaseadapter.GetCartItemStock(
                            SalesItemList[0].getParentitemcode());
                    if (getStockCur.getCount() > 0) {
                        for (int i = 0; i < getStockCur.getCount(); i++) {
                            getparentstock = getStockCur.getDouble(0);
                            getparentitemname = getStockCur.getString(1);
                            getunitname = getStockCur.getString(2);
                            getupp = getStockCur.getString(3);
                        }
                        getparentstock = getparentstock - (Double.parseDouble(getcartqty));
                    }

                } catch (Exception e) {
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), "SalesActivity - stockConversion : getParentStock", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                } finally {
                    if (objdatabaseadapter != null)
                        objdatabaseadapter.close();
                    if (getStockCur != null)
                        getStockCur.close();
                }
                final String finalGetparentitemname = getparentitemname;
                final int finalGetparentstock = (int) getparentstock;
                final String finalGetunitname = getunitname;
                final String finalGetupp = getupp;
                final String getparentcode = getstaticparentitemcode;

                    /*    builder.setMessage("Do you want to convert stock?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, int id) {*/
                //dialog.dismiss();
                stockconvertdialog = new Dialog(context);
                stockconvertdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                stockconvertdialog.setContentView(R.layout.stockconversion);
                stockconvertdialog.setCanceledOnTouchOutside(false);
                stockconvertdialog.setCancelable(false);
                ImageView closepopup = (ImageView) stockconvertdialog.findViewById(R.id.closepopup);

                closepopup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // isparentopen = "";
                        // mHolder.listitemname.setBackgroundColor(getResources().getColor(R.color.white));
                        stockconvertdialog.dismiss();
                        isParentOP[0] ="";
                        isNillstockconversion[0] =true;
                        MHolder[0].listitemqty.setText("");
                        isstockcoversionpopupopen=false;
                        gblitemcount=0;
                        mHolder.listitemtotal.setEnabled(true);
                    }
                });
                final TextView txtparentitem = (TextView) stockconvertdialog.findViewById(R.id.txtparentitem);
                final TextView txtparentstock = (TextView) stockconvertdialog.findViewById(R.id.txtparentstock);
                final TextView txtparentunit = (TextView) stockconvertdialog.findViewById(R.id.txtparentunit);
                final EditText txtparentqty = (EditText) stockconvertdialog.findViewById(R.id.txtparentqty);
                final TextView txtchilditem = (TextView) stockconvertdialog.findViewById(R.id.txtchilditem);
                final TextView txtchildqty = (TextView) stockconvertdialog.findViewById(R.id.txtchildqty);
                final TextView txtchildconvertunit = (TextView)stockconvertdialog.findViewById(R.id.txtchildconvertunit);
                final TextView txtparentconvertunit = (TextView)stockconvertdialog.findViewById(R.id.txtparentconvertunit) ;

                txtparentitem.setText(finalGetparentitemname);
                txtparentstock.setText(String.valueOf(finalGetparentstock));
                txtparentunit.setText(finalGetunitname);
                txtparentconvertunit.setText(finalGetunitname);
                if(finalGetparentstock >= 1 ){
                    txtparentqty.setText(String.valueOf("1"));
                }else{
                    txtparentqty.setText(String.valueOf(finalGetparentstock));
                    Toast toast = Toast.makeText(getApplicationContext(),"Parent have not sufficient qty", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }

                txtparentqty.setEnabled(false);
                if (finalGetupp.equals("0") || finalGetupp.equals("")) {
                    txtchildqty.setText(String.valueOf(finalGetparentstock));
                } else {
                    if(finalGetparentstock >= 1 ){
                        double getcompleteqty = 1 * Double.parseDouble(finalGetupp);
                        txtchildqty.setText(String.valueOf(getcompleteqty));
                    }else{
                        double getcompleteqty = finalGetparentstock * Double.parseDouble(finalGetupp);
                        txtchildqty.setText(String.valueOf(getcompleteqty));
                    }

                }
                /***********PARENT QTY TEXT CHANGE LISTENER******/
                txtparentqty.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                  int after) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        // TODO Auto-generated method stub
                        if (!txtparentqty.getText().toString().equals("")) {
                            if (finalGetparentstock >= Double.parseDouble(txtparentqty.getText().toString())) {
                                if (finalGetupp.equals("0") || finalGetupp.equals("")) {
                                    txtchildqty.setText(String.valueOf(txtparentqty.getText().toString()));
                                } else {
                                    double getcompleteqty = Double.parseDouble(txtparentqty.getText().toString()) * Double.parseDouble(finalGetupp);
                                    txtchildqty.setText(String.valueOf(getcompleteqty).toString());
                                }
                            } else {
                                txtchildqty.setText("0");
                                Toast toast = Toast.makeText(getApplicationContext(),"Stock exceed with parent qty", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                //Toast.makeText(getApplicationContext(), "Stock exceed with parent qty", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            txtchildqty.setText("");
                            Toast toast = Toast.makeText(getApplicationContext(),"Please enter valid qty", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            //Toast.makeText(getApplicationContext(), "Please enter valid qty", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
                /***********END PARENT QTY TEXT CHANGE LISTENER******/

                txtchilditem.setText(getstaticchilditemname);
                txtchildconvertunit.setText(getstaticchildunitname);

                Button btnConversion = (Button) stockconvertdialog.findViewById(R.id.btnConversion);
                btnConversion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (txtparentitem.getText().toString().equals("")
                                || txtparentitem.getText().toString().equals(null)) {
                            Toast toast = Toast.makeText(getApplicationContext(),"Please select parent item", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            //Toast.makeText(getApplicationContext(), "Please select parent item", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (txtparentstock.getText().toString().equals("")
                                || txtparentstock.getText().toString().equals(null)) {
                            Toast toast = Toast.makeText(getApplicationContext(),"Please enter parent stock", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            //Toast.makeText(getApplicationContext(), "Please enter parent stock", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (txtparentunit.getText().toString().equals("")
                                || txtparentunit.getText().toString().equals(null)) {
                            Toast toast = Toast.makeText(getApplicationContext(),"Please enter parent unit", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            //Toast.makeText(getApplicationContext(), "Please enter parent unit", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (txtparentqty.getText().toString().equals("")
                                || txtparentqty.getText().toString().equals(null)) {
                            Toast toast = Toast.makeText(getApplicationContext(),"Please select parent qty", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            //Toast.makeText(getApplicationContext(), "Please select parent qty", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (txtchilditem.getText().toString().equals("")
                                || txtchilditem.getText().toString().equals(null)) {
                            Toast toast = Toast.makeText(getApplicationContext(),"Please select child item", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            // Toast.makeText(getApplicationContext(), "Please select child item", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (txtchildqty.getText().toString().equals("")
                                || txtchildqty.getText().toString().equals(null)) {
                            Toast toast = Toast.makeText(getApplicationContext(),"Please enter child qty", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            //Toast.makeText(getApplicationContext(), "Please enter child qty", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (Double.parseDouble(txtchildqty.getText().toString())<=0) {
                            Toast toast = Toast.makeText(getApplicationContext(),"Please enter valid child qty", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            //Toast.makeText(getApplicationContext(), "Please enter child qty", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String getchilditemcode = SalesItemList[0].getItemcode();
                        DataBaseAdapter stockobjdatabaseadapter = null;
                        String insertstock = null;

                        try {
                            //Stock Conversion Functionality
                            stockobjdatabaseadapter = new DataBaseAdapter(context);
                            stockobjdatabaseadapter.open();
                            String getitemstockcode= "";
                            if(isFreestock[0].equals("yes")){
                                getitemstockcode = Getstaticchildcode[0];
                            }else{
                                getitemstockcode =  getchilditemcode;
                            }
                            insertstock = stockobjdatabaseadapter.insertStockConversion(getparentcode, txtchildqty.getText().toString(),
                                    txtparentqty.getText().toString(),getitemstockcode, preferenceMangr.pref_getString("getschedulecode"),
                                    preferenceMangr.pref_getString("getvancode"));
                            if (insertstock.equals("success")) {
                                getstaticchilditemcode = SalesItemList[0].getItemcode();
                                getstaticgetchildqty = MHolder[0].listitemqty.getText().toString();
                                Toast toast = Toast.makeText(getApplicationContext(),"Stock converted successfully", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                // Toast.makeText(getApplicationContext(), "Saved successfully", Toast.LENGTH_SHORT).show();
                                stockconvertdialog.dismiss();

                                //dialog.dismiss();
                                isFreestock[0] = "";
                                getitemsfromcode = getstaticsubcode;


                                GetItems(getstaticsubcode);
                                isstockcoversionpopupopen=false;

                                MHolder[0].listitemtotal.callOnClick();
                                gblitemcount=0;
                                mHolder.listitemtotal.setEnabled(true);
                            } else {
                                Toast toast = Toast.makeText(getApplicationContext(),"Unable to convert the stock", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }

                        } catch (Exception e) {
                            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                            mDbErrHelper.open();
                            String geterrror = e.toString();
                            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), "SalesActivity - insertStockConversion", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            mDbErrHelper.close();
                        } finally {
                            if (stockobjdatabaseadapter != null)
                                stockobjdatabaseadapter.close();
                        }

                    }
                });
                if(gblitemcount==1){
                    stockconvertdialog.show();
                    isstockcoversionpopupopen=true;
                }



                //}
                //})
                                /*.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        mHolder.listitemqty.setText("");
                                        *//*Toast toast = Toast.makeText(getApplicationContext(),"Insufficient stock..", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();*//*
                                        //Toast.makeText(getApplicationContext(), "Insufficient stock..", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();*/
            }
        }catch (Exception e) {
            Log.i("Route", e.toString());
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), "SalesActivity - stockConversion", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }

    }
    public void CountDownTimer(String from){
        final String strfrom=from;
        Integer intotptimevalidity=Integer.parseInt(otptimevalidity);
        long milliseconds=intotptimevalidity*1000;
        CountDownTimer = new CountDownTimer(milliseconds, 1000) {
            public void onTick(long millisUntilFinished) {
                txttimer.setText("00:00:00");
                if(strfrom.equals("initial")){
                    if (CountDownTimer != null) {
                        CountDownTimer.cancel();
                    }
                    return;
                }
                // Used for formatting digit to be in 2 digits only
                NumberFormat f = new DecimalFormat("00");
                long hour = (millisUntilFinished / 3600000) % 24;
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                txttimer.setText(f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
            }
            // When the task is over it will print 00:00:00 there
            public void onFinish() {
                txttimer.setText("00:00:00");
                btnGetOTP.setText("Resend OTP");
//                txtphoneno.setText("");
                btnGetOTP.setEnabled(true);
                txtotpphoneno.setEnabled(true);
                otpprocess="resend";
            }
        }.start();
    }
    public  void Showmobilenoverify(String from){

                fromcustomer=from;
                pindialog = new Dialog(context);
                pindialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                pindialog.setCanceledOnTouchOutside(false);
                pindialog.setContentView(R.layout.otpverificationpopup);
                btnVerifypin = (Button) pindialog.findViewById(R.id.btnVerifypin);
                otppinview = (Pinview) pindialog.findViewById(R.id.otppinview);
                txtotpphoneno = (EditText) pindialog.findViewById(R.id.txtotpphoneno);
                btnGetOTP = (Button) pindialog.findViewById(R.id.btnGetOTP);
                txttimer = (TextView) pindialog.findViewById(R.id.txttimer);
                txtcustomer = (TextView) pindialog.findViewById(R.id.txtcustomer);
                txtcustomercity = (TextView) pindialog.findViewById(R.id.txtcustomercityarea);
//                txtcustomerarea = (TextView) pindialog.findViewById(R.id.txtcustomerarea);
                LL3 = (LinearLayout) pindialog.findViewById(R.id.LL3);
                LL2 = (LinearLayout) pindialog.findViewById(R.id.LL2);
//                LL3.setVisibility(View.GONE);
//                LL2.setVisibility(View.GONE);
//                btnVerifypin.setVisibility(View.GONE);
                txtcustomer.setText(customername);
                txtcustomercity.setText(customercityarea);
                btnGetOTP.setText("Get OTP");
                if(fromcustomer.equals("verifycustomermobileno")) {
                    txtotpphoneno.setText("");
                    txtotpphoneno.setEnabled(true);
                }else{
                    if(txtphoneno.getText()!=null){
                        txtotpphoneno.setText(txtphoneno.getText());
        //                txtotpphoneno.setEnabled(false);
                    }
                }
                txttimer.setText("00:00:00");
                LL3.setVisibility(View.VISIBLE);
                LL2.setVisibility(View.VISIBLE);
                btnVerifypin.setVisibility(View.VISIBLE);
                CountDownTimer("initial");
//                txtcustomerarea.setText(customerareaname);
//                ImageView closepopup = (ImageView) pindialog.findViewById(R.id.closepopup);
//                closepopup.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        pindialog.dismiss();
//                    }
//                });

       // mobilenoverificationstatus=1;
                btnGetOTP.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0;i < otppinview.getPinLength();i++) {
                            otppinview.onKey(otppinview.getFocusedChild(), KeyEvent.KEYCODE_DEL, new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_DEL));
                        }
                        String getphoneno = txtotpphoneno.getText().toString();
                        txttimer.requestFocus();
                        if(getphoneno.equals("") || getphoneno.equals("null")
                                || getphoneno.equals(null)){
                            Toast toast = Toast.makeText(getApplicationContext(),"Please enter Mobile No.", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            //Toast.makeText(getApplicationContext(), "Please enter Mobile No. or GSTIN", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(!getphoneno.equals("") && !getphoneno.equals("null")
                                && !getphoneno.equals(null) ) {
                            if (getphoneno.length() < 10) {
                                Toast toast = Toast.makeText(getApplicationContext(),"Mobile No. should contain 10 digits", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                //Toast.makeText(getApplicationContext(), "Mobile No. should contain 10 digits", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        if (isNetworkAvailable()){
                            new AsyncCheckCustomerMobileNumber().execute();
                        } else{
                            Toast toast = Toast.makeText(getApplicationContext(),"Please check internet connection", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return;
                        }
                        //btnVerifypin.setBackgroundColor(getResources().getColor(R.color.graycolor));
                    }
                });
                btnVerifypin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DataBaseAdapter finalObjdatabaseadapter1 = null;
                        finalObjdatabaseadapter1 = new DataBaseAdapter(context);
                        finalObjdatabaseadapter1.open();
                        try{
                            if(otppinview.getValue().equals("") ) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Please enter OTP", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                //Toast.makeText(getApplicationContext(), "Mobile No. should contain 10 digits", Toast.LENGTH_SHORT).show();
                                return;
                            }else if(otppinview.getValue().length()<6){
                                Toast toast = Toast.makeText(getApplicationContext(), "Please enter 6 digit OTP", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                //Toast.makeText(getApplicationContext(), "Mobile No. should contain 10 digits", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            else{

                                String result = "";
                                DataBaseAdapter dataBaseAdapter =null;
                                JSONObject jsonObj = null;
                                RestAPI api = new RestAPI();
                                try {
                                    dataBaseAdapter = new DataBaseAdapter(context);
                                    dataBaseAdapter.open();
                                    networkstate = isNetworkAvailable();
                                    if (networkstate == true) {
                                        jsonObj = api.GetOTPapi(txtotpphoneno.getText().toString(),"",
                                                otppinview.getValue() ,"checkotp.php",context);
                                        if(jsonObj!=null) {
                                            String success1 = jsonObj.getString("success");
                                            if (success1.equals("1")) {
                                                if(fromcustomer.equals("add")) {
                                                    txtphoneno.setText(txtotpphoneno.getText());
                                                }
                                                mobilenoverificationstatus=1;

                                                if(fromcustomer.equals("verifycustomermobileno")){
                                                    DataBaseAdapter dataBaseAdapter1 = new DataBaseAdapter(context);
                                                    dataBaseAdapter1.open();
                                                    dataBaseAdapter1.UpdateCustomerMobilenostatus(customercode,txtotpphoneno.getText().toString());
                                                    dataBaseAdapter1.close();
                                                    networkstate = isNetworkAvailable();
                                                    if (networkstate == true) {
                                                        new AsyncCustomerDetails().execute();
                                                    }
                                                }
                                                Toast toast = Toast.makeText(getApplicationContext(), "Mobile No. Verified successfully", Toast.LENGTH_LONG);
                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                toast.show();
                                                getmobilenoverifycount="0";
                                                pindialog.dismiss();
                                            }else{
                                                String message = jsonObj.getString("message");
                                                Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                toast.show();
                                            }
                                        }

                                    }else{
                                        Toast toast = Toast.makeText(getApplicationContext(),"Please check internet connection", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    }
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    Log.e("Check OTP DIB", e.getMessage());
                                    if(dataBaseAdapter != null)
                                        dataBaseAdapter.close();
                                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                    mDbErrHelper.open();
                                    String geterrror = e.toString();
                                    mDbErrHelper.insertErrorLog(geterrror.replace("'", " "),
                                            this.getClass().getSimpleName() + " - Check OTP", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                    mDbErrHelper.close();
                                }
                                finally {
                                    if(dataBaseAdapter != null)
                                        dataBaseAdapter.close();
                                }


                            }

                        } catch (Exception e) {
                            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                            mDbErrHelper.open();
                            String geterrror = e.toString();
                            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "),
                                    this.getClass().getSimpleName() + " - Submit pin", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            mDbErrHelper.close();
                        } finally {
                            if (finalObjdatabaseadapter1 != null)
                                finalObjdatabaseadapter1.close();
                        }
                    }
                });
                pindialog.show();

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
    public  void GetRoute(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();

                Cur = objdatabaseadapter.GetRouteDB();

            if(Cur.getCount()>0) {
                routecode = new String[Cur.getCount()];
                routename = new String[Cur.getCount()];
                routenametamil = new String[Cur.getCount()];
                routeday = new String[Cur.getCount()];
                for(int i=0;i<Cur.getCount();i++){
                    if(preferenceMangr.pref_getString("getroutecode") == Cur.getString(0))
                    {
                        continue;
                    }
                    routecode[i] = Cur.getString(0);
                    routename[i] = Cur.getString(1);
                    routenametamil[i] = Cur.getString(2);
                    routeday[i] = Cur.getString(3);
                    Cur.moveToNext();
                }

                routedialog = new Dialog(context);
                routedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                routedialog.setContentView(R.layout.routepopup);
                lv_Routelist = (ListView) routedialog.findViewById(R.id.lv_Routelist);
                ImageView close = (ImageView) routedialog.findViewById(R.id.close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        routedialog.dismiss();
                    }
                });
                RouteAdapter adapter = new RouteAdapter(context);
                lv_Routelist.setAdapter(adapter);
                routedialog.show();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(), "No Route Available",Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
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
    //Route Adapter
    public class RouteAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;

        RouteAdapter(Context c) {
            context = c;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return routecode.length;
        }

        @Override
        public Object getItem(int position) {
            return routecode[position];
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

            RouteAdapter.ViewHolder mHolder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.routepopuplist, parent, false);
                mHolder = new RouteAdapter.ViewHolder();
                try {
                    mHolder.listroutename = (TextView) convertView.findViewById(R.id.listroutename);
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
                mHolder = (RouteAdapter.ViewHolder) convertView.getTag();
            }
            try {
                if(!String.valueOf(routenametamil[position]).equals("") && !String.valueOf(routenametamil[position]).equals(null)
                        && !String.valueOf(routenametamil[position]).equals("null")){
                    mHolder.listroutename.setText(String.valueOf(routenametamil[position]));
                }else{
                    mHolder.listroutename.setText(String.valueOf(routename[position]));
                }
            } catch (Exception e) {
                Log.i("Route", e.toString());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!String.valueOf(routenametamil[position]).equals("") && !String.valueOf(routenametamil[position]).equals(null)
                            && !String.valueOf(routenametamil[position]).equals("null")){

                        txtroutes.setText(routenametamil[position]);


                    }else{

                        txtroutes.setText(routename[position]);

                    }
                    getscheduleroutecode = routecode[position];
                    routeflag= 1;
                    GetArea(getscheduleroutecode);
//                    if(routecode[position].equals("0")){
//                        txtothersroute.setVisibility(View.VISIBLE);
//                        txtothersroute.setText("");
//                    }else{
//                        txtothersroute.setVisibility(View.GONE);
//                        txtothersroute.setText("");
//                    }

                    routedialog.dismiss();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView listroutename;

        }

    }

    public class OrderedItemsAdapter extends BaseAdapter {
        LayoutInflater inflater;
        Context context;
        ArrayList<SalesItemDetails> myList;
        DecimalFormat dft = new DecimalFormat("0.00");

        public OrderedItemsAdapter( Context context,ArrayList<SalesItemDetails> myList) {
            this.myList = myList;
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return myList.size();
        }

        @Override
        public SalesItemDetails getItem(int position) {
            return (SalesItemDetails) myList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder mHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.row_transaction_list, parent, false);
                mHolder = new ViewHolder();
                try {

                    mHolder.listitemname = (TextView) convertView.findViewById(R.id.row_trans_list_item_name);
                    mHolder.listitemrate = (TextView) convertView.findViewById(R.id.row_trans_list_item_rate);
                    mHolder.listitemqty = (TextView) convertView.findViewById(R.id.row_trans_list_item_qty);
                    mHolder.listitemamount = (TextView) convertView.findViewById(R.id.row_trans_list_item_amount);



                } catch (Exception e) {
                    Log.i("Ordered items", e.toString());
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    mDbErrHelper.insertErrorLog(e.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                }
                convertView.setTag(mHolder);
            } else {
                mHolder = (ViewHolder) convertView.getTag();
            }
            try {
                SalesItemDetails currentListData = getItem(position);
                String getdecimalvalue  = currentListData.getNoofdecimals();

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
                mHolder.listitemname.setText(currentListData.getItemnametamil());
                if(getnoofdigits!="") {
                    mHolder.listitemqty.setText(df.format(Double.parseDouble(currentListData.getItemqty())) +" "+currentListData.getUnitname());
                }else{
                    mHolder.listitemqty.setText(currentListData.getItemqty()+" "+currentListData.getUnitname());
                }
                mHolder.listitemamount.setText(dft.format(Double.parseDouble(currentListData.getSubtotal())));




                if(!String.valueOf(currentListData.getNewprice()).equals("null")
                        && !String.valueOf(currentListData.getNewprice()).equals("")
                        &&!String.valueOf(currentListData.getNewprice()).equals(null)) {
                    mHolder.listitemrate.setText(dft.format(Double.parseDouble(currentListData.getNewprice())));

                }else{
                    mHolder.listitemrate.setText(dft.format(Double.parseDouble("0")));

                }



            } catch (Exception e) {
                Log.i("ordered item list value", e.toString());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            return convertView;
        }
        private class ViewHolder {
            TextView  listitemname,listitemrate,
                    listitemqty,listitemamount;
            LinearLayout ordereditemlist;
            CardView card_view;

        }
    }


    //transaction history
    protected class AsyncRecentTransactions extends
            AsyncTask<String, JSONObject,String> {
        JSONObject jsonObj = null;
        ProgressDialog loading;
        JSONArray json_category = null;
        JSONArray item_category = null;

        int code;
        @Override
        protected String doInBackground(String... params) {

            String result = "";
            try {
                RestAPI api = new RestAPI();

                if (Utilities.isNetworkAvailable(context)) {
                    jsonObj = api.GetRecentTrans(params[0],"get_recent_trans.php");
                }
                else{
                    result = "";
                }
                if(jsonObj!=null) {
                    if(jsonObj.has("success")) {
                        result = jsonObj.getString("success");

                        if(result.equals("1"))
                        {

                            translistDataHEader = new ArrayList<>();
                            translisthash = new HashMap<>();


                            try{
                                json_category = jsonObj.getJSONArray("Value");
                                if(json_category.length()>0) {
                                    for(int i=0;i<json_category.length();i++){
                                        JSONObject obj = (JSONObject) json_category.get(i);
                                        translistDataHEader.add(obj.getString("billdate") + "#" +obj.getString("billno")+ "#" +obj.getString("amount"));
                                        JSONArray arr =  obj.getJSONArray("Items");
                                        List<String> itemlist = new ArrayList<>();
                                        itemlist.add("Itemname" + "#" +"Price"+ "#" +"Qty"+ "#" +"Amount");
                                        for(int j=0;j<arr.length();j++){
                                            JSONObject obj1 = (JSONObject) arr.get(j);
                                            itemlist.add(obj1.getString("itemname") + "#" +obj1.getString("qty")+ "#" +obj1.getString("grandtotal")+ "#" +obj1.getString("rate"));

                                        }
                                        translisthash.put(translistDataHEader.get(i),itemlist);

                                    }

                /*Cur2 = objdatabaseadapter.GetEmptyTinGroupDB();
                if(Cur2.getCount()>0){
                    for(int i=0;i< Cur2.getCount();i++){
                        listDataHEader.add(Cur2.getString(3));
                        Cur3 = objdatabaseadapter.GetEmptyTinSubGroup_GroupDB(Cur2.getString(3));
                        List<String> subgrouplist = new ArrayList<>();
                        for(int j=0;j<Cur3.getCount();j++){
                            subgrouplist.add(Cur3.getString(1) + "-" +Cur3.getString(2));
                            Cur3.moveToNext();
                        }
                        listhash.put(listDataHEader.get(i),subgrouplist);
                        Cur.moveToNext();

                    }
                }*/


                                }else{
                                    Toast toast = Toast.makeText(getApplicationContext(),"Van out of stock", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    //Toast.makeText(getApplicationContext(),"Van out of stock",Toast.LENGTH_SHORT).show();

                                }
                            }catch (Exception e){
                                Log.d("AsyncSync", e.getMessage());
                            }

//                            translisthash = new HashMap<>();
//                            if (isSuccessful(jsonObj)) {
//                                json_category = jsonObj.getJSONArray("Value");
//
//                                recentTrans.clear();
//                                List<String> recentTranslist = new ArrayList<>();
//                                for(int i=0; i<json_category.length(); i++)
//                                {
//
//                                    JSONObject obj = (JSONObject) json_category.get(i);
//                                    Log.d("Items",obj.getJSONArray("Items").toString());
//                                    recentTranslist.add(obj.getString("billno"));
//                                    recentTrans.add( new RecentTrans(obj.getString("billdate"),obj.getString("billno"),obj.getString("amount")));
//                                    //rowTrans.add(new RowTrans(obj.getJSONArray("Items").getString(0),obj.getJSONArray("Items").getString(1),obj.getJSONArray("Items").getString(2)));
//                                    List<String> itemlist = new ArrayList<>();
//                                    JSONArray arr = obj.getJSONArray("Items");
//                                    rowTrans.clear();
//                                    for(int j=0;j<arr.length();j++){
//                                       Log.d("checkarr",arr.toString());
//                                        JSONObject obj1 = (JSONObject) arr.get(j);
//                                       itemlist.add(obj1.getString("itemname") + "-" +obj1.getString("qty"));
//                                       // rowTrans.add(new RowTrans(obj1.getString("itemname"),obj1.getString("qty"),obj1.getString("grandtotal")));
//                                     //   List<String> itemlist = new ArrayList<>();
//
//                                    }
//                                    translisthash.put(recentTranslist.get(i),itemlist);
//                                }
//
//                            }
                        }

                    }
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncSync", e.getMessage());
                result="";

            }
            return result;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            // loading = ProgressDialog.show(context, "Loading", "Please wait...", true);
            // loading.setCancelable(false);
            // loading.setCanceledOnTouchOutside(false);
        }

        protected void onPostExecute(final String result) {
            try {
//                loading.dismiss();
                if(result.equals("1"))
                {
//                    Dialog transdialog = new Dialog(context);
//                    transdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                    transdialog.setContentView(R.layout.recenttranspopup);
//                    ImageView close = (ImageView) transdialog.findViewById(R.id.close);
//                    lv_RecentTransList = (ListView) transdialog.findViewById(R.id.lv_RecentTransList);
//                    close.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            transdialog.dismiss();
//                        }
//                    });
//                    RecentTransAdapter adapter = new RecentTransAdapter(context);
//                    lv_RecentTransList.setAdapter(adapter);
//                    rowTrans.clear();
//                    transdialog.show();

                    LayoutInflater inflater = (LayoutInflater) SalesActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View layout = inflater.inflate(R.layout.customertransactions, null);
                    window = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,  false);

                    transexpList = (ExpandableListView) layout.findViewById(R.id.transactions_expandible_listview);



                    ImageView close = (ImageView) layout.findViewById(R.id.transactions_close);

                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            window.dismiss();

                            isopenpopup = false;
                        }
                    });
                    //window.setOutsideTouchable(true);
                    //window.showAtLocation(layout, Gravity.BOTTOM, 0, 0);
                    window.showAtLocation(layout, Gravity.BOTTOM, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    //  setUpAdapter();
                    window.setOutsideTouchable(false);
                    isopenpopup = true;
//                    setChildItems();

                    transactionsExpandableAdapter = new TransactionsExpandableAdapter(context,translistDataHEader,translisthash);

                    transexpList.setAdapter(transactionsExpandableAdapter);

                    transexpList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                        int previousGroup = -1;

                        @Override
                        public void onGroupExpand(int groupPosition) {

                            if (groupPosition != previousGroup)
                                transexpList.collapseGroup(previousGroup);

                            previousGroup = groupPosition;
                        }
                    });





                }
                else if( result.equals("0"))
                {
                    Toast toast = Toast.makeText(getApplicationContext(), "No Sales Available", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                else if(result.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Something went to be wrong", Toast.LENGTH_SHORT).show();
                    return;
                }


            } catch (Exception e) {
                Log.d("servererror",e.getMessage());
            }

        }


    }
    public class TransactionsExpandableAdapter extends BaseExpandableListAdapter {

        Context ctx;
        private List<String> listDataheader;
        private HashMap<String,List<String>> listHashMap;
        SalesActivity objsales = new SalesActivity();
        //public static ArrayList<ArrayList<String>> childList;
        //private String[] parents;
        public TransactionsExpandableAdapter(Context ctx, List<String> listDataheader, HashMap<String, List<String>> listHashMap) {
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
            return 0;
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
                view = inflater.inflate(R.layout.transparent_layout, null);

            }

            TextView billdate_textview = (TextView) view.findViewById(R.id.recenttrans_billdate);
            billdate_textview.setText(headertitle.split("#")[0]);
            TextView billno_textview = (TextView) view.findViewById(R.id.recenttrans_billno);
            billno_textview.setText(headertitle.split("#")[1]);
            TextView amount_textview = (TextView) view.findViewById(R.id.recenttrans_amount);
            amount_textview.setText(headertitle.split("#")[2]);

//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    LinearLayout header_layout = (LinearLayout) v.findViewById(R.id.LLH);
//                    header_layout.setVisibility(View.VISIBLE);
//
//
//
//
//                }
//            });

            return  view;
        }


        @Override
        public View getChildView(int i, int j, boolean b, View view, ViewGroup viewGroup) {
            String getsubgroupcode="";
            final String childtext = (String)getChild(i,j);
            try {
                LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                Log.d(String.valueOf(j),"meaage");

                if (j == 0) {
                    view = inflater.inflate(R.layout.trans_header_layout, null);
                }
                else {
                    view = inflater.inflate(R.layout.row_transaction_list, null);

                    // view = inflater.inflate(R.layout.row_transaction_list, null);


                    TextView billdate_textview = (TextView) view.findViewById(R.id.row_trans_list_item_name);
                    billdate_textview.setText(childtext.split("#")[0]);
                    TextView rate_textview = (TextView) view.findViewById(R.id.row_trans_list_item_rate);
                    rate_textview.setText(childtext.split("#")[3]);
                    TextView billno_textview = (TextView) view.findViewById(R.id.row_trans_list_item_qty);
                    billno_textview.setText(childtext.split("#")[1]);

                    TextView amount_textview = (TextView) view.findViewById(R.id.row_trans_list_item_amount);
                    amount_textview.setText(childtext.split("#")[2]);

                }

            }
            catch (Exception e)
            {
                Log.d(e.getMessage(),"error");
            }

            return  view;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }

    //not purchased

    public  void getNotPurchasedRemarks(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.getNotPurchasedRemarks();
            if(Cur.getCount()>0) {
                notPurchasedRemarks = new String[Cur.getCount()];
                notPurchasedRemarksCode = new String[Cur.getCount()];
                for(int i=0;i<Cur.getCount();i++){
                    notPurchasedRemarks[i] = Cur.getString(0);
                    notPurchasedRemarksCode[i] = Cur.getString(1);
                    Cur.moveToNext();
                }

                remarksDialog = new Dialog(context);
                remarksDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                remarksDialog.setContentView(R.layout.remarkspopup);
                lv_RemarksList = (ListView) remarksDialog.findViewById(R.id.lv_RemarksList);
                ImageView close = (ImageView) remarksDialog.findViewById(R.id.close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        remarksDialog.dismiss();
                    }
                });
                RemarksAdapter adapter = new RemarksAdapter(context);
                lv_RemarksList.setAdapter(adapter);
                remarksDialog.show();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(),"No remarks available", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                // Toast.makeText(getApplicationContext(),"No remarks available",Toast.LENGTH_SHORT).show();
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

    public void insertNotPurchasedDetails(String customercode,String remarksCode){
        Log.d("LATLONG ", "Customercode " + customercode);
        DataBaseAdapter objDataBaseAdapter = new DataBaseAdapter(context);
        try{
            objDataBaseAdapter.open();
            String latLong="0";
            //String latitude="0";String longtitude="0";
            try{
                if(preferenceMangr.pref_getBoolean(Constants.KEY_GET_GPSTRACKINGSTATUS)){
                    if(gpsTracker.isGPSTrackingEnabled){
                        latLong = String.valueOf(gpsTracker.getLatitude()) + "," + String.valueOf(gpsTracker.getLongitude());
                        //latitude = String.valueOf(gpsTracker.getLatitude());
                        //longtitude = String.valueOf(gpsTracker.getLongitude());
                    }
                }


            }catch (Exception e){
                objDataBaseAdapter.insertErrorLog(String.valueOf(e).replace("'", " "), this.getClass().getSimpleName() + "Exception in getting GPS latlong", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
            /*try{
                if(locManager.checkPermissions(context))
                    latLong = String.valueOf(locManager.getLatitude()) + "," + String.valueOf(locManager.getLongitude());
            }catch (Exception e){
                objDataBaseAdapter.insertErrorLog(String.valueOf(e).replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }*/
            String result = objDataBaseAdapter.insertNotPurchasedSales(preferenceMangr.pref_getString(Constants.KEY_GETVANCODE), preferenceMangr.pref_getString(Constants.KEY_GETFORMATDATE), customercode,
                    preferenceMangr.pref_getString("getschedulecode"), preferenceMangr.pref_getString(Constants.KEY_GETFINANCIALYEARCODE), remarksCode, latLong);

            if(result.equals(Constants.RESULT_SUCCESS)){
                Toast.makeText(context, getString(R.string.savedSuccessfully),Toast.LENGTH_SHORT).show();
                if (customerdialog != null)
                    customerdialog.dismiss();
                GetCustomer(LoginActivity.getareacode);
                new AsyncNotPurchaseDetails().execute();
            } else
                Toast.makeText(context, getString(R.string.notSavedSuccessfully),Toast.LENGTH_SHORT).show();

            if(objDataBaseAdapter != null)
                objDataBaseAdapter.close();
        }catch (Exception e){
            if(objDataBaseAdapter == null)
                objDataBaseAdapter.open();
            String geterrror = e.toString();
            objDataBaseAdapter.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - insertNotPurchasedDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            objDataBaseAdapter.close();
        }finally{
            //customerdialog.dismiss();
        }
    }

    public class RemarksAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;

        RemarksAdapter(Context c) {
            context = c;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return notPurchasedRemarksCode.length;
        }

        @Override
        public Object getItem(int position) {
            return notPurchasedRemarksCode[position];
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

            RemarksAdapter.ViewHolder mHolder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.remarkspopuplist, parent, false);
                mHolder = new RemarksAdapter.ViewHolder();
                try {
                    mHolder.listreceiptname = (TextView) convertView.findViewById(R.id.listreceiptname);
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
                mHolder = (RemarksAdapter.ViewHolder) convertView.getTag();
            }
            try {
                mHolder.listreceiptname.setText(String.valueOf(notPurchasedRemarks[position] ));
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
                    txtremarks.setText(String.valueOf(notPurchasedRemarks[position]));
                    getRemarksCode = notPurchasedRemarksCode[position];
                    remarksDialog.dismiss();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView listreceiptname;

        }

    }

    protected  class AsyncNotPurchaseDetails extends
            AsyncTask<String, JSONObject, ArrayList<SalesSyncDatas>> {
        ArrayList<SalesSyncDatas> List = null;
        JSONObject jsonObj = null;
        ProgressDialog loading;
        @Override
        protected  ArrayList<SalesSyncDatas> doInBackground(String... params) {
            RestAPI api = new RestAPI();
            String result = "";
            try {
                JSONObject js_salesobj = new JSONObject();;
                try {
                    DataBaseAdapter dbadapter = new DataBaseAdapter(context);
                    dbadapter.open();

                    Cursor mCursales = dbadapter.GetNotPurchasedDatasDB();

                    JSONArray js_array2 = new JSONArray();

                    for (int i = 0; i < mCursales.getCount(); i++) {
                        JSONObject obj = new JSONObject();
                        obj.put("vancode", mCursales.getString(1));
                        obj.put("billdate", mCursales.getString(2));
                        obj.put("customercode", mCursales.getString(3));
                        obj.put("schedulecode", mCursales.getString(4));
                        obj.put("createddate", mCursales.getString(5));
                        obj.put("financialyearcode", mCursales.getString(7));
                        obj.put("remarks", mCursales.getString(8));
                        obj.put("syncstatus",mCursales.getString(9));
                        obj.put("salestime", mCursales.getString(10));
                        obj.put("latlong", mCursales.getString(11));

                        js_array2.put(obj);
                        mCursales.moveToNext();
                    }

                    js_salesobj.put("JSonObject", js_array2);

                    jsonObj =  api.insertNotPurchasedDetails(js_salesobj.toString(),context);
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
                Log.d("AsyncNotPurchaseDetails", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + " - AsyncNotPurchaseDetails - DOINBACK", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            return List;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(context, "Loading", "Please wait...", true);
            loading.setCancelable(false);
            loading.setCanceledOnTouchOutside(false);
        }

        @Override
        protected void onPostExecute(ArrayList<SalesSyncDatas> List) {
            // TODO Auto-generated method stub
            loading.dismiss();
            DataBaseAdapter objdatabaseadapter = new DataBaseAdapter(context);
            try {
                objdatabaseadapter.open();
                if (List.size() >= 1) {
                    if (List.get(0).TransactionNo.length > 0) {
                        for (int j = 0; j < List.get(0).TransactionNo.length; j++) {
                            objdatabaseadapter.UpdateNotPurchaseFlag(List.get(0).TransactionNo[j]);
                        }
                    }
                }
                if(objdatabaseadapter != null)
                    objdatabaseadapter.close();
            } catch (Exception e){
                if(objdatabaseadapter != null)
                    objdatabaseadapter.close();
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName() + "- AsyncNotPurchaseDetails - POSTEXEC", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            } finally {
                //finish();
                dialogNotPurchased.dismiss();
            }
        }
    }

}