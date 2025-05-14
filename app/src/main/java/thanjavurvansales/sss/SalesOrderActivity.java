package thanjavurvansales.sss;

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
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.goodiebag.pinview.Pinview;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SalesOrderActivity extends AppCompatActivity  implements View.OnClickListener  {
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

    ListView lv_sales_items,lv_AreaList,lv_CustomerList,lv_subgroup,lv_CustomerAreaList,lv_CityList;

    TextView txtroutename;
    public  static TextView txtcustomername,txtareaname,totalcartitems,txtsalesdate;
    String gettransportid="0";
    String[] AreaCode,AreaName,AreaNameTamil,NoOfKm,CityCode,CityName,CustomerCount;

    String[] SubGroupCode,SubGroupName,SubGroupNameTamil;
    String[] CustomerCode,CustomerName,CustomerNameTamil,Address,CustomerAreaCode,MobileNo,
            TelephoneNo,GSTN,SchemeApplicable,customertypecode,CustomerCityName,CustomerAreaName;
    Dialog areadialog,customerdialog;
    static public String  customercode="",gstnnumber="",
            getschemeapplicable="",getstaticsubcode="",getstaticchilditemcode="",getstaticgetchildqty="";
    public static ArrayList<SalesOrderItemDetails> salesitems = new ArrayList<SalesOrderItemDetails>();
    ArrayList<SalesOrderItemDetails> freeitems = new ArrayList<SalesOrderItemDetails>();
    static ArrayList<SalesOrderItemDetails> staticreviewsalesorderitems = new ArrayList<SalesOrderItemDetails>();
    Dialog stockconvertdialog,dialog,citydialog;
    public static boolean paymenttype;
    TextView txtpopupustomername,txtcustomernametamil,txtaddress,txtcityname,
            txtarea,txtphoneno,txtlandlineno,txtemailid,txtgstin,txtadhar,txtwhatsappno;
    Button btnSaveCustomer,btnGetOTPCustomer;
    String getcitycode,getareacode;
    String[] areacode,areaname,areanametamil,customercount;
    String[] citycode,cityname,citynametamil;
    public static boolean ifsavedsales = false;
    public static final String GSTINFORMAT_REGEX = "[0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[Z]{1}[0-9a-zA-Z]{1}";
    public static final String GSTN_CODEPOINT_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static String getitemsfromcode = "0";
    boolean networkstate;
    boolean isopenshowpopup=false;
    boolean cartflag =false;
    boolean cartpriceflag =false;
    ImageView btnaddcustomer;
    boolean cartcheckflag =false;
    public String getSchemebusinesstype="0";
    Integer mobilenoverificationstatus=0;
    String customercityname="",customerareaname="",customername="",customercityarea="",fromcustomer="";
    Dialog pindialog;
    Button btnVerifypin,btnGetOTP;
    Pinview otppinview;
    EditText txtotpphoneno;
    TextView txttimer,txtcustomer,txtcustomercity,txtcustomerarea;
    LinearLayout LL3,LL2;
    String otptimevalidity="",otptimevaliditybackend="";
    String getcustomertypecode,getsalestypecode;
    TextView txtcustomertype,txtsalestype;
    String[] customertypecodearr,customertypenamearr;
    String[] salestypecodearr,salestypenamearr;
    Dialog customertypedialog,salestypedialog;
    ListView lv_CustomertypeList,lv_salestypeList;
    String otpprocess="send";
    CountDownTimer CountDownTimer;
    public static PreferenceMangr preferenceMangr=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_order);

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
        totalcartitems = (TextView)findViewById(R.id.totalcartitems);
        btnaddcustomer = (ImageView)findViewById(R.id.btnaddcustomer);
        ifsavedsales = false;

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

            otptimevalidity=objdatabaseadapter.Getotptimevalidity();
            otptimevaliditybackend=objdatabaseadapter.Getotptimevaliditybackend();

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
        //Set values for corresponding values
        txtareaname.setText(LoginActivity.getareaname);

        //salesfabclickoption
        fabgroupitem.setOnClickListener(this);

        //Set total cart item value
        totalcartitems.setText(String.valueOf(staticreviewsalesorderitems.size()));

        CheckToggleButton();
        togglegstin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                   // CheckToggleButton();
            }
        });
        togglegstin.setBackgroundColor(getResources().getColor(R.color.graycolor));




        //open Review Screen
        salesreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(staticreviewsalesorderitems.size() > 0 ) {
                    boolean checkquantityflag = true;
                    if (salesitems.size() > 0) {

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
                        salesitems.clear();
                        SalesItemAdapter adapter = new SalesItemAdapter(context, salesitems);
                        lv_sales_items.setAdapter(adapter);
                        Intent i = new Intent(context, SalesOrderCartActivity.class);
                        startActivity(i);

                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Cart is empty", Toast.LENGTH_LONG);
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

        //Set total cart items
        totalcartitems.setText("0");

        btnaddcustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetCustomerPopup();
            }
        });

    }
    /************OPEN CUSTOMR POPUP*********************/
    public void GetCustomerPopup(){
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.addcustomer);
        ImageView closepopup = (ImageView) dialog.findViewById(R.id.closepopup);
        txtpopupustomername = (TextView)dialog.findViewById(R.id.txtcustomername) ;
        txtcustomernametamil = (TextView)dialog.findViewById(R.id.txtcustomernametamil) ;
        txtaddress = (TextView)dialog.findViewById(R.id.txtaddress) ;
        txtcityname = (TextView)dialog.findViewById(R.id.txtcityname) ;
        txtarea = (TextView)dialog.findViewById(R.id.txtarea) ;
        txtphoneno = (TextView)dialog.findViewById(R.id.txtphoneno) ;
        txtlandlineno = (TextView)dialog.findViewById(R.id.txtlandlineno) ;
        txtemailid = (TextView)dialog.findViewById(R.id.txtemailid) ;
        txtgstin = (TextView)dialog.findViewById(R.id.txtgstin) ;
        txtadhar = (TextView)dialog.findViewById(R.id.txtadhar) ;
        txtcustomertype = (TextView) dialog.findViewById(R.id.txtcustomertype);
        txtsalestype = (TextView) dialog.findViewById(R.id.txtsalestype);
        btnSaveCustomer = (Button)dialog.findViewById(R.id.btnSaveCustomer);
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
               // GetCustomerType();
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
                if (txtcustomername.getText().toString().equals("") || txtcustomername.getText().toString().equals("null")
                        || txtcustomername.getText().toString().equals(null)) {
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
                customername=txtcustomernametamil.getText().toString().equals("")?txtcustomername.getText().toString():txtcustomernametamil.getText().toString();
                customercityarea= txtcityname.getText().toString() + ',' + txtarea.getText().toString();
                Showmobilenoverify("add");
//                mobilenoverificationstatus=1;
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

                if(getcustomername.equals("") || getcustomername.equals("null")
                        || getcustomername.equals(null) ){
                    Toast toast = Toast.makeText(getApplicationContext(),"Please enter customer name", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    // Toast.makeText(getApplicationContext(),"Please enter customer name",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(getcityname.equals("") || getcityname.equals("null")
                        || getcityname.equals(null) || getcitycode.equals("")|| getcitycode.equals(null) ){
                    Toast toast = Toast.makeText(getApplicationContext(),"Please select city name", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    //  Toast.makeText(getApplicationContext(),"Please select city name",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(getarea.equals("") || getarea.equals("null")
                        || getarea.equals(null) || getareacode.equals("")|| getareacode.equals(null) ){
                    Toast toast = Toast.makeText(getApplicationContext(),"Please select area name", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    //Toast.makeText(getApplicationContext(),"Please select area name",Toast.LENGTH_SHORT).show();
                    return;
                }
                if((getphoneno.equals("") || getphoneno.equals("null")
                        || getphoneno.equals(null)) && (getgstin.equals("") || getgstin.equals("null")
                        || getgstin.equals(null)) ){
                    Toast toast = Toast.makeText(getApplicationContext(),"Please enter Mobile No. or GSTIN", Toast.LENGTH_LONG);
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
                if(!getgstin.equals("") && !getgstin.equals("null")
                        && !getgstin.equals(null) ) {
                    if (getgstin.length() > 0) {
                        try {
                            if (!validGSTIN(getgstin)) {
                                Toast toast = Toast.makeText(getApplicationContext(),"Please enter valid GSTIN", Toast.LENGTH_LONG);
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

                if(!getaadharno.equals("") && !getaadharno.equals("null")
                        && !getaadharno.equals(null) ) {
                    if (getaadharno.length() <12) {
                        Toast toast = Toast.makeText(getApplicationContext(),"Aadhar No. should contain 12 digits", Toast.LENGTH_LONG);
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
                    if(getcustomernametamil.equals("")||getcustomernametamil.equals(null)
                            || getcustomernametamil.equals("null")){
                        getcustomernametamil = getcustomername;
                    }
                    //Order item details
                    objdatabaseadapter = new DataBaseAdapter(context);
                    objdatabaseadapter.open();
                    String getresult="";
                    String getretailercount = objdatabaseadapter.CheckCustomerAlreadyExistsCount(getareacode,getcustomername);
                    if(getretailercount.equals("Success")) {
                        getresult = objdatabaseadapter.InsertCustomerDetails(getcustomername, getcustomernametamil,
                                getaddress, getareacode, getemailid, getphoneno, getlandlineno, getaadharno,
                                getgstin,getcustomertypecode,getsalestypecode,getwhatsappno,mobilenoverificationstatus);
                        if (!getresult.equals("")) {
                            Toast toast = Toast.makeText(getApplicationContext(),"Saved Successfully", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            LoginActivity.getareacode=getareacode;
                            LoginActivity.getareaname=getcityname+" - " +getarea;
                            txtareaname.setText(LoginActivity.getareaname);
                            txtcustomername.setText(getcustomernametamil);
                            customercode = getresult;
                            gstnnumber = getgstin.trim();
                            if(getgstin.trim().equals("")||getgstin.equals(null)||getgstin.equals("0")){
                                togglegstin.setBackgroundColor(getResources().getColor(R.color.graycolor));
                            }else{
                                togglegstin.setBackgroundColor(getResources().getColor(R.color.green));
                            }
                            togglegstin.setText("GSTIN \n "+getgstin.trim());
                            togglegstin.setTextOn("GSTIN \n "+getgstin.trim());
                            togglegstin.setTextOff("GSTIN \n "+getgstin.trim());
                            //Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_SHORT).show();
                            getcustomertypecode = "";
                            getsalestypecode = "";
                            dialog.dismiss();
                            btnSaveCustomer.setEnabled(true);
                        }
                    }else{
                        Toast toast = Toast.makeText(getApplicationContext(),"Customer name already exists", Toast.LENGTH_LONG);
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
                    if(objdatabaseadapter!=null)
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
                    Cur.moveToNext();
                }

                areadialog = new Dialog(context);
                areadialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                areadialog.setContentView(R.layout.areapopup);
                lv_CustomerAreaList = (ListView) areadialog.findViewById(R.id.lv_AreaList);
                ImageView close = (ImageView) areadialog.findViewById(R.id.close);
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


    public  void GetArea(String routecode){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetAreaDB(routecode,Constants.CUSTOMER_CATEGORY_ORDER);
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
                Toast toast = Toast.makeText(getApplicationContext(),"No Area in this route", Toast.LENGTH_LONG);
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
            Cur = objdatabaseadapter.GetCustomerDB(areacode,Constants.CUSTOMER_CATEGORY_ORDER);
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

    /*********CLOSE FILTER FUNCTIONALITY********/

    //Check gstin functionality

    //cHECK GSTIN FORMAT
    private static boolean validGSTIN(String gstin) throws Exception {
        boolean isValidFormat = false;
        if (checkPattern(gstin, GSTINFORMAT_REGEX)) {
            isValidFormat = verifyCheckDigit(gstin);
        }
        return isValidFormat;

    }
    private static boolean verifyCheckDigit(String gstinWCheckDigit) throws Exception {
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
    public static String getGSTINWithCheckDigit(String gstinWOCheckDigit) throws Exception {
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
        double newprice=0.0;

        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            lv_sales_items.setAdapter(null);
            Cur = objdatabaseadapter.GetSalesOrderItemDB(itemsubgroupcode,preferenceMangr.pref_getString("getroutecode"),LoginActivity.getareacode);
            salesitems.clear();
            if(Cur.getCount()>0) {
                for(int i=0;i<Cur.getCount();i++){
                    newprice = Double.parseDouble(Cur.getString(20));
                    if(newprice>0){
                        salesitems.add(new SalesOrderItemDetails(Cur.getString(0),Cur.getString(1),
                                Cur.getString(2),Cur.getString(3),Cur.getString(4),
                                Cur.getString(5),Cur.getString(6),Cur.getString(7),
                                Cur.getString(8),Cur.getString(9),Cur.getString(10),
                                Cur.getString(11),Cur.getString(12),Cur.getString(13),
                                Cur.getString(14),Cur.getString(15),Cur.getString(16),
                                Cur.getString(17),Cur.getString(18),Cur.getString(19),
                                Cur.getString(20),Cur.getString(21),Cur.getString(22),
                                Cur.getString(23),"","",Cur.getString(24),
                                "","","0","",Cur.getString(20),
                                Cur.getString(29),Cur.getString(30),Cur.getString(27),Cur.getString(28)));
                    }

                    Cur.moveToNext();
                }
                CalculateTotal();


                SalesItemAdapter adapter = new SalesItemAdapter(context,salesitems);
                lv_sales_items.setAdapter(adapter);
                isopenshowpopup=false;
                //Set ITEMQTY PRICE AND TOTAL
                for (int i = 0; i < staticreviewsalesorderitems.size(); i++) {
                    for (int j = 0; j < salesitems.size();j++) {
                        if (staticreviewsalesorderitems.get(i).getItemcode().equals(salesitems.get(j).getItemcode()) &&
                                !(staticreviewsalesorderitems.get(i).getFreeflag().equals("freeitem"))) {
                            salesitems.get(j).setItemqty(staticreviewsalesorderitems.get(i).getItemqty());
                            salesitems.get(j).setNewprice(staticreviewsalesorderitems.get(i).getNewprice());
                            salesitems.get(j).setSubtotal(staticreviewsalesorderitems.get(i).getSubtotal());
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

    /*****END GROUP FUNCTIONALITY**********/



    public void CheckToggleButton(){
        if (togglegstin.isChecked()) {
            paymenttype = false;
            togglegstin.setTextOn("GSTIN \n "+gstnnumber);
            togglegstin.setBackgroundColor(getResources().getColor(R.color.green));
            if(gstnnumber.equals("")){
                togglegstin.setBackgroundColor(getResources().getColor(R.color.graycolor));
            }
        }
        else{
            paymenttype = true;
            togglegstin.setTextOff("GSTIN \n "+gstnnumber);
            if(gstnnumber.equals("")){
                togglegstin.setBackgroundColor(getResources().getColor(R.color.graycolor));
            }
            togglegstin.setBackgroundColor(getResources().getColor(R.color.graycolor));
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
                drilldownitem = objdatabaseadapter.GetOrderDrildownGroupStatusDB();

            }  catch (Exception e){
                Log.i("GetDrildown", e.toString());
            }
            finally {
                // this gets called even if there is an exception somewhere above
                if(objdatabaseadapter != null)
                    objdatabaseadapter.close();
            }

            if(drilldownitem.equals("group")) {
                isopenshowpopup=true;
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            Cur = objdatabaseadapter.GetAllGroupDB();
            if(Cur.getCount()>0) {
                for(int i=0;i<Cur.getCount();i++){
                    listDataHEader.add(Cur.getString(2));
                    Cur1 = objdatabaseadapter.GetSubGroup_GroupOrderDB(Cur.getString(0));
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
                Toast toast = Toast.makeText(getApplicationContext(),"Item not available", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //Toast.makeText(getApplicationContext(),"Van out of stock",Toast.LENGTH_SHORT).show();

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
            Toast toast = Toast.makeText(getApplicationContext(),"Please select customer", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            //Toast.makeText(getApplicationContext(),"Please select customer",Toast.LENGTH_SHORT).show();
            return;
        }else {
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
                            Toast toast = Toast.makeText(getApplicationContext(),"Please enter valid total amount", Toast.LENGTH_LONG);
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

    public class SalesItemAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;
        ArrayList<SalesOrderItemDetails> salesItemList;
        String isparentopen="";
        final DecimalFormat dft = new DecimalFormat("0.00");
        String getstaticparentitemcode="0";
        String getstaticchilditemname="",getstaticchildunitname = "";
        String getstaticchildcode="";
        String isfreestock="";
        boolean isnillstockconversion=false;
        String GETFREEITEMSTATUS;
        String freeitemstatus="";
        String schemeitemdiscount ="0";
        Double TotalQty=0.0,RemainingQty=0.00;

        SalesItemAdapter(Context c,ArrayList<SalesOrderItemDetails> myList) {
            this.salesItemList = myList;
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
                convertView = layoutInflater.inflate(R.layout.salesorderitemlist, parent, false);
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
                //Item PRice text change Listner
                    mHolder.listitemqty.addTextChangedListener(new TextWatcher() {
                        public void onTextChanged(CharSequence s, int start, int before,
                                                  int count) {
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
                                        String getresult = objdatabaseadapter.DeleteItemOrderInCart(salesItemList.get(pos1).getItemcode());
                                        if (getresult.equals("Success")) {
                                            getcartdatas = objdatabaseadapter.GetSalesOrderItemsCarts();
                                            if(getcartdatas.getCount()>0){
                                                totalcartitems.setText(String.valueOf(getcartdatas.getCount()));
                                            }else{
                                                staticreviewsalesorderitems.clear();
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
                                mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
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
                                            String getresult = objdatabaseadapter.DeleteItemOrderInCart(salesItemList.get(pos1).getItemcode());
                                            if (getresult.equals("Success")) {
                                                getcartdatas = objdatabaseadapter.GetSalesOrderItemsCarts();
                                                if(getcartdatas.getCount()>0){
                                                    totalcartitems.setText(String.valueOf(getcartdatas.getCount()));
                                                }else{
                                                    staticreviewsalesorderitems.clear();
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




                mHolder.labelhsntax.setText(salesItemList.get(position).getHsn() +" @ "+salesItemList.get(position).getTax() +"%");


                DataBaseAdapter objdataschemabaseadapter= new DataBaseAdapter(context);
                Cursor schemebusinesstype = null;
                try{
                    objdataschemabaseadapter.open();
                    schemebusinesstype =objdataschemabaseadapter.getschemebusinesstype(salesItemList.get(position).getItemcode());

                    getSchemebusinesstype=schemebusinesstype.getString(0);
                    Log.d("Scheme businesstype :",getSchemebusinesstype +" - "+salesItemList.get(position).getItemcode());

                }catch (Exception e){
                    Log.d("Scheme businesstype error (line : 1527) :",e.toString());
                }finally{
                    if (objdataschemabaseadapter != null)
                        objdataschemabaseadapter.close();
                    if(schemebusinesstype !=null)
                        schemebusinesstype.close();

                }


                if (getschemeapplicable.equals("yes")) {
                    if(getSchemebusinesstype.equals("2") || getSchemebusinesstype.equals("3")){
                        if(Double.parseDouble(salesItemList.get(position).getRatecount()) >0) {
                            mHolder.schemecount.setVisibility(View.VISIBLE);
                            mHolder.schemecount.setText("R");
                            mHolder.dummycount.setVisibility(View.GONE);
                            GETFREEITEMSTATUS="rate";
                        }
                        if(Double.parseDouble(salesItemList.get(position).getFreecount()) >0) {
                            mHolder.schemecount.setVisibility(View.VISIBLE);
                            mHolder.schemecount.setText("F");
                            mHolder.dummycount.setVisibility(View.GONE);
                            GETFREEITEMSTATUS="item";
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
            if(salesItemList.get(position).getAllowpriceedit().equals("yes") &&
                    salesItemList.get(position).getRouteallowpricedit().equals("yes")){
                mHolder.listitemrate.setBackgroundResource(R.drawable.editbackground);
                mHolder.listitemrate.setEnabled(true);
            }else{
                mHolder.listitemrate.setBackgroundResource(R.drawable.editbackgroundgray);
                mHolder.listitemrate.setEnabled(false);
            }

            //Set Discount background
            mHolder.listdiscount.setBackgroundColor(getResources().getColor(R.color.lightvoilet));
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
                    getItemCur = objdatabaseadapter.GetCartOrderItemQtyAndPrice(salesItemList.get(position).getItemcode());
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
                    boolean addedqty = false;
                    if (!(mHolder.listitemqty.getText().toString()).equals("") &&
                            !(mHolder.listitemqty.getText().toString()).equals(" ")
                            && !(mHolder.listitemqty.getText().toString()).equals("0")
                            && !(mHolder.listitemqty.getText().toString()).equals("0.0")
                            && !(mHolder.listitemqty.getText().toString()).equals(null)
                            && !(mHolder.listitemqty.getText().toString()).equals(".")
                            && Double.parseDouble(mHolder.listitemqty.getText().toString()) > 0) {
                        //mHolder.listitemname.setBackgroundColor(getResources().getColor(R.color.white));
                        // isparentopen = "";
                        isfreestock = "";
                        //Delcare variables
                        double getminqty = 0;
                        double getdiscount = 0;
                        double getparentstock = 0;

                        final int pos = (Integer) mHolder.listitemtotal.getTag();

                        if (!mHolder.listitemqty.getText().toString().equals("")) {
                            if (Double.parseDouble(mHolder.listitemqty.getText().toString()) >0  ) {



                                DataBaseAdapter ordertotalqtydatabaseadapter = null;

                                try {
                                    ordertotalqtydatabaseadapter = new DataBaseAdapter(context);
                                    ordertotalqtydatabaseadapter.open();
                                    //Get Item Total Quantity
                                    String totalqty=ordertotalqtydatabaseadapter.getTotalOrderQuantity(salesItemList.get(pos).getItemcode());
                                    //Toast.makeText(context,totalqty,Toast.LENGTH_SHORT).show();

                                    TotalQty=Double.parseDouble(totalqty);
                                    if(salesItemList.get(pos).getOrderstatus().equals("1")){
                                        RemainingQty=Double.parseDouble(salesItemList.get(pos).getMaxorderqty())-TotalQty;
                                    }


                                } catch (Exception e) {
                                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                    mDbErrHelper.open();
                                    String geterrror = e.toString();
                                    mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName() + "Total Order Quantity", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                    mDbErrHelper.close();
                                } finally {
                                    if (ordertotalqtydatabaseadapter != null)
                                        ordertotalqtydatabaseadapter.close();
                                }

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

                                /*********SCHEME DETAILS*************/
                                if (getschemeapplicable.equals("yes")) {
                                    if (getSchemebusinesstype.equals("2") || getSchemebusinesstype.equals("3")) {
                                        //Get Scheme Rate based
                                        DataBaseAdapter objdatabaseadapter = null;
                                        Cursor getschemeCur = null;
                                        try {
                                            //Scheme Functionality
                                            objdatabaseadapter = new DataBaseAdapter(context);
                                            objdatabaseadapter.open();
                                            getschemeCur = objdatabaseadapter.GetOrderSchemeFORItemDB(salesItemList.get(pos).getItemcode(),
                                                    preferenceMangr.pref_getString("getroutecode"), mHolder.listitemqty.getText().toString());
                                            if (getschemeCur.getCount() > 0) {
                                                getminqty = getschemeCur.getDouble(0);
                                                getdiscount = getschemeCur.getDouble(1);
                                                //schemeitemdiscount = String.valueOf(getschemeCur.getDouble(1));
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

                                            getitemschemeCur = objdatabaseadapterfree.GetOrderSchemeFORFreeItemDB(getitemcode, preferenceMangr.pref_getString("getroutecode"));
                                            getitemschemetype = objdatabaseadapterfree.GetOrderSchemeType(getitemcode, preferenceMangr.pref_getString("getroutecode"));
                                            if(getitemschemetype.getCount()>0){
                                                getfreeitemschemetype = getitemschemetype.getString(0);

                                                 if(getfreeitemschemetype.equals("rate")) {
                                                    freeitemstatus = "freerate";
                                                     schemeitemdiscount = String.valueOf(getitemschemetype.getDouble(2));
                                                }

                                            //Toast.makeText(context,freeitemstatus,Toast.LENGTH_SHORT).show();
                                            if (getitemschemeCur.getCount() > 0) {
                                                //freeitems.clear();
                                                // for (int i = 0; i < getitemschemeCur.getCount(); i++) {
                                                getpurchaseitemcode = getitemschemeCur.getString(0);
                                                getpurchaseqty = getitemschemeCur.getString(1);
                                                getfreeitemcode = getitemschemeCur.getString(2);
                                                getfreeqty = getitemschemeCur.getString(3);

                                                //get item scheme type

                                                }





                                                // }
                                                if (Double.parseDouble(getpurchaseqty) <=
                                                        Double.parseDouble(getqty)) {
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
                                                        int getfreeqtyval = (int) (getactualqty / Double.parseDouble(getpurchaseqty));
                                                        int getactualqtyvalue = (int) (getfreeqtyval * Double.parseDouble(getfreeqty));
                                                        String getcartqty = objdatabaseadapter.GetCartOrderItemStock(getfreeitemcode);


                                                        getFreeStock = null;

                                                        //Get Stock for parent item
                                                        getFreeStock = objdatabaseadapterfree.GetPurchaseItems(getfreeitemcode, preferenceMangr.pref_getString("getroutecode"), LoginActivity.getareacode);


                                                        if (getFreeStock.getCount() > 0) {

                                                            if (salesItemList.get(pos).getOrderstatus().equals("1")) {
                                                                if (Double.parseDouble(getqty) > Double.parseDouble(salesItemList.get(pos).getMaxorderqty())) {
                                                                    Toast toast = Toast.makeText(getApplicationContext(), salesItemList.get(pos).getItemnametamil() + " item quantity should be less than or equal to " + salesItemList.get(pos).getMaxorderqty(), Toast.LENGTH_LONG);
                                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                                    toast.show();
                                                                    mHolder.listitemtotal.setText("0.00");
                                                                    mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
                                                                    return;
                                                                }
                                                            }

                                                            if (salesItemList.get(pos).getOrderstatus().equals("1")) {
                                                                if ( TotalQty >= Double.parseDouble(salesItemList.get(pos).getMaxorderqty())) {
                                                                    Toast toast = Toast.makeText(getApplicationContext(), salesItemList.get(pos).getItemnametamil() + " item reach the maximum order quantity ", Toast.LENGTH_LONG);
                                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                                    toast.show();
                                                                    mHolder.listitemtotal.setText("0.00");
                                                                    mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
                                                                    return;
                                                                }
                                                            }

                                                            if (salesItemList.get(pos).getOrderstatus().equals("1")) {
                                                                if ( Double.parseDouble(mHolder.listitemqty.getText().toString())  > RemainingQty) {
                                                                    Toast toast = Toast.makeText(getApplicationContext(), salesItemList.get(pos).getItemnametamil() + " item quantity should be less than or equal to "+ RemainingQty, Toast.LENGTH_LONG);
                                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                                    toast.show();
                                                                    mHolder.listitemtotal.setText("0.00");
                                                                    mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
                                                                    return;
                                                                }
                                                            }

                                                            for (int i = 0; i < getFreeStock.getCount(); i++) {
                                                                if (getFreeStock.getString(26).equals("1")) {
                                                                    if (getactualqtyvalue > Double.parseDouble(getFreeStock.getString(27))) {
                                                                        Toast toast = Toast.makeText(getApplicationContext(), getfreeitemname + "  free item should be less than or equal to " + salesItemList.get(pos).getMaxorderqty(), Toast.LENGTH_LONG);
                                                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                                                        toast.show();
                                                                        mHolder.listitemtotal.setText("0.00");
                                                                        mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
                                                                        return;
                                                                    }
                                                                }
                                                                double getsubtotal = Double.parseDouble(getFreeStock.getString(20)) * getactualqtyvalue;

                                                                for (int j = 0; j < freeitems.size(); j++) {
                                                                    if (getpurchaseitemcode.equals(freeitems.get(j).getPurchaseitemcode()) &&
                                                                            getfreeitemcode.equals(freeitems.get(j).getFreeitemcode())) {
                                                                        freeitems.remove(j);
                                                                    }
                                                                }
                                                                freeitems.add(new SalesOrderItemDetails(getFreeStock.getString(0), getFreeStock.getString(1),
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
                                                                        getFreeStock.getString(24),  String.valueOf(getsubtotal),
                                                                        "freeitem", getpurchaseitemcode, getfreeitemcode, getFreeStock.getString(20), "", "", "", ""));
                                                                //Toast.makeText(context,freeitemstatus,Toast.LENGTH_SHORT).show();
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
                                }

                                /*********END SCHEME DETAILS*************/


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
                                        mHolder.listitemtotal.setText(dft.format(getres));
                                        mHolder.listdiscount.setBackgroundColor(getResources().getColor(R.color.lightvoilet));
                                        mHolder.listdiscount.setText("");
                                        salesItemList.get(pos).setDiscount("");

                                    } else {
                                        mHolder.listdiscount.setBackgroundColor(getResources().getColor(R.color.lightvoilet));
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
                                    //Set Array value for subtotal
                                    salesItemList.get(pos).setSubtotal(dft.format(res));
                                    mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.darkblue));
                                    salesItemList.get(pos).setNewprice(mHolder.listitemrate.getText().toString());
                                    mHolder.listdiscount.setBackgroundColor(getResources().getColor(R.color.lightvoilet));
                                    mHolder.listdiscount.setText("");
                                }
                                addedqty = true;
                                CalculateTotal();
                            } else {
                                Toast toast = Toast.makeText(getApplicationContext(),"Please enter valid quantity", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        } else {
                            mHolder.listdiscount.setBackgroundColor(getResources().getColor(R.color.lightvoilet));
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
                                mHolder.listitemtotal.setText("0.00");
                                mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
                                return;
                            }
                            if(Double.parseDouble(salesItemList.get(pos).getItemqty())<=0 ){
                                Toast toast = Toast.makeText(getApplicationContext(),"Please enter valid quantity", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                mHolder.listitemtotal.setText("0.00");
                                mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
                                return;
                            }
                            if(salesItemList.get(pos).getNewprice().equals("") ||
                                    salesItemList.get(pos).getNewprice().equals("0") ){
                                Toast toast = Toast.makeText(getApplicationContext(),"Please enter  price", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                mHolder.listitemtotal.setText("0.00");
                                mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
                                return;
                            }
                            if(Double.parseDouble(salesItemList.get(pos).getNewprice())<=0 ){
                                Toast toast = Toast.makeText(getApplicationContext(),"Please enter valid price", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
                                return;
                            }
                            if(salesItemList.get(pos).getSubtotal().equals("") ||
                                    salesItemList.get(pos).getSubtotal().equals("0") ){
                                Toast toast = Toast.makeText(getApplicationContext(),"Please enter  total", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                mHolder.listitemtotal.setText("0.00");
                                mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
                                return;
                            }
                            if(Double.parseDouble(salesItemList.get(pos).getSubtotal())<=0 ){
                                Toast toast = Toast.makeText(getApplicationContext(),"Please enter valid total", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                mHolder.listitemtotal.setText("0.00");
                                mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
                                return;
                            }
                            if(salesItemList.get(pos).getOrderstatus().equals("1")){
                                if(Double.parseDouble(salesItemList.get(pos).getItemqty()) > Double.parseDouble(salesItemList.get(pos).getMaxorderqty())){
                                    Toast toast = Toast.makeText(getApplicationContext(),salesItemList.get(pos).getItemnametamil()+" item quantity should be less than or equal to "+salesItemList.get(pos).getMaxorderqty(), Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    mHolder.listitemtotal.setText("0.00");
                                    mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
                                    return;
                                }
                            }

                            if (salesItemList.get(pos).getOrderstatus().equals("1")) {
                                if ( TotalQty >= Double.parseDouble(salesItemList.get(pos).getMaxorderqty())) {
                                    Toast toast = Toast.makeText(getApplicationContext(), salesItemList.get(pos).getItemnametamil() + " item reach the maximum order quantity ", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    mHolder.listitemtotal.setText("0.00");
                                    mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
                                    return;
                                }
                            }

                            if (salesItemList.get(pos).getOrderstatus().equals("1")) {
                                if ( Double.parseDouble(mHolder.listitemqty.getText().toString())  > RemainingQty) {
                                    Toast toast = Toast.makeText(getApplicationContext(), salesItemList.get(pos).getItemnametamil() + " item quantity should be less than or equal to "+ RemainingQty, Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    mHolder.listitemtotal.setText("0.00");
                                    mHolder.listitemtotal.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryDark));
                                    return;
                                }
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

                                }
                                // for (int i = 0; i < salesitems.size(); i++) {
                                if(salesitems.size() >0) {
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
                                                    totalschemediscount = Double.parseDouble(salesitems.get(i).getItemqty()) * Double.parseDouble(schemeitemdiscount);
                                                    schemeitemdiscount = String.valueOf(totalschemediscount);
                                                }
                                                insertcart = dataBaseAdapter.insertSalesOrderCart(String.valueOf(salesitems.get(i).getItemcode()), String.valueOf(salesitems.get(i).getCompanycode()),
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
                                                        String.valueOf(salesitems.get(i).getPurchaseitemcode()), String.valueOf(salesitems.get(i).getFreeitemcode()));

                                                freeitemstatus="";
                                            }
                                        }

                                    }
                                }

                                // }


                                //Add free item to cart
                                for (int k = 0; k < freeitems.size(); k++) {
                                    insertcart = dataBaseAdapter.insertFreeSalesOrderCart(String.valueOf(freeitems.get(k).getItemcode()), String.valueOf(freeitems.get(k).getCompanycode()),
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
                                            String.valueOf(freeitems.get(k).getPurchaseitemcode()), String.valueOf(freeitems.get(k).getFreeitemcode()),"0");

                                    //Toast.makeText(context,String.valueOf(freeitems.get(k).getFreeflag()),Toast.LENGTH_SHORT).show();
                                    checkfreeitem = true;
                                }

                                //Get cart datas from database temp table
                                getcartdatas = dataBaseAdapter.GetSalesOrderItemsCarts();
                                if(getcartdatas.getCount()>0) {
                                    staticreviewsalesorderitems.clear();
                                    for (int i = 0; i < getcartdatas.getCount(); i++) {
                                        staticreviewsalesorderitems.add(new SalesOrderItemDetails(getcartdatas.getString(1), getcartdatas.getString(2),
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
                            totalcartitems.setText(String.valueOf(staticreviewsalesorderitems.size()));
                        }
                    } else {
                        final int pos = (Integer) mHolder.listitemqty.getTag();
                        mHolder.listdiscount.setBackgroundColor(getResources().getColor(R.color.lightvoilet));
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
                            String getresult = objdatabaseadapter.DeleteItemOrderInCart(getitemcode);
                            if(getresult.equals("Success")){

                                //Get cart datas from database temp table
                                getcartdatas = objdatabaseadapter.GetSalesOrderItemsCarts();
                                staticreviewsalesorderitems.clear();
                                if(getcartdatas.getCount()>0) {

                                    for (int i = 0; i < getcartdatas.getCount(); i++) {
                                        staticreviewsalesorderitems.add(new SalesOrderItemDetails(getcartdatas.getString(1), getcartdatas.getString(2),
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
                                                getcartdatas.getString(21),"","" ,"",""));
                                        getcartdatas.moveToNext();
                                    }
                                }
                            }
                            totalcartitems.setText(String.valueOf(staticreviewsalesorderitems.size()));


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


                }
            });

            if(salesItemList.get(position).getOrderstatus().equals("3")){
                mHolder.itemLL.setBackgroundColor(getResources().getColor(R.color.gray));
                mHolder.listitemrate.setBackgroundResource(R.drawable.editbackgroundgray);
                mHolder.listitemqty.setBackgroundResource(R.drawable.editbackgroundgray);
                mHolder.listitemrate.setEnabled(false);
                mHolder.listitemqty.setEnabled(false);
                mHolder.listitemtotal.setEnabled(false);
                mHolder.listdiscount.setBackgroundColor(getResources().getColor(R.color.gray));
                mHolder.listdiscount.setText("");
            }



            mHolder.listitemqty.setTag(position);
            mHolder.listitemrate.setTag(position);
            return convertView;
        }

        private class ViewHolder1 {
            private TextView listitemname,schemecount,dummycount;
            private TextView listitemcode,labelnilstock;
            private EditText listitemqty;
            private EditText listitemrate;
            private TextView listitemtotal,listitemtax,labelstock,labelhsntax,labelstockunit,listdiscount;
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
            res2 = res2 + Double.parseDouble(getsaleseqty);
        }
        billisttotalamount.setText(dft.format(res1));
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
                    LoginActivity.getareacode = String.valueOf(AreaCode[position]);
                    if(SalesOrderCartActivity.txttransportname != null){
                        SalesOrderCartActivity.txttransportname.setText("");
                        SalesOrderCartActivity.gettransportid="0";
                        SalesOrderCartActivity.gettransportname="";
                    }

                    gettransportid = "0";
                    if(staticreviewsalesorderitems.size() > 0 ){
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
                                        staticreviewsalesorderitems.clear();
                                        //Set total cart item value
                                        totalcartitems.setText(String.valueOf(staticreviewsalesorderitems.size()));
                                        salesitems.clear();
                                        DataBaseAdapter objdatabaseadapter = new DataBaseAdapter(context);
                                        objdatabaseadapter.open();
                                        objdatabaseadapter.DeleteSalesCartItemCart();
                                        objdatabaseadapter.close();
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
                    customercityname = CustomerCityName[position];
                    customerareaname = CustomerAreaName[position];
                    customername = CustomerNameTamil[position];
                    customercityarea= CustomerCityName[position] + ',' + CustomerAreaName[position];
                    if(gstnnumber.equals("")||gstnnumber.equals(null)||gstnnumber.equals("0")){
                        togglegstin.setBackgroundColor(getResources().getColor(R.color.graycolor));
                    }else{
                        togglegstin.setBackgroundColor(getResources().getColor(R.color.green));
                    }



                    togglegstin.setText("GSTIN \n "+gstnnumber);
                    togglegstin.setTextOn("GSTIN \n "+gstnnumber);
                    togglegstin.setTextOff("GSTIN \n "+gstnnumber);
                    salesitems.clear();
                    SalesItemAdapter adapter = new SalesItemAdapter(context,salesitems);
                    lv_sales_items.setAdapter(adapter);
                    if(staticreviewsalesorderitems.size() > 0 ){
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Confirmation");
                        builder.setMessage("Are you sure you want to clear cart?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        staticreviewsalesorderitems.clear();
                                        //Set total cart item value
                                        totalcartitems.setText(String.valueOf(staticreviewsalesorderitems.size()));
                                        salesitems.clear();
                                        DataBaseAdapter objdatabaseadapter = new DataBaseAdapter(context);
                                        objdatabaseadapter.open();
                                        objdatabaseadapter.DeleteSalesCartItemCart();
                                        objdatabaseadapter.close();
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
                    }

                    // check mobile number is verified
                    DataBaseAdapter objdatabaseadapter = null;
                    objdatabaseadapter = new DataBaseAdapter(context);
                    objdatabaseadapter.open();
                    try{
                        String mobilenoverifycount=objdatabaseadapter.Checkemobilenoverify(customercode,preferenceMangr.pref_getString("getroutecode"));
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
            return convertView;
        }

        private class ViewHolder {
            private TextView listcustomername,listgstin,listcustomertype;

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
                    jsonObj = api.GetOTPapi(txtphoneno.getText().toString(),otpprocess,"" ,"otpsms.php",context);
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
            loading = ProgressDialog.show(context, "Synching All Data", "Please wait...", true, true);
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
                        Toast toast = Toast.makeText(getApplicationContext(), "Please enter valid total amount", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }
                }
            }
        }
        if(staticreviewsalesorderitems.size() > 0 ){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Confirmation");
            builder.setMessage("Are you sure you want to clear cart?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            staticreviewsalesorderitems.clear();
                            //Set total cart item value
                            totalcartitems.setText(String.valueOf(staticreviewsalesorderitems.size()));
                            salesitems.clear();
                            gstnnumber="";

                            DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
                            dataBaseAdapter.open();
                            dataBaseAdapter.DeleteSalesCartItemCart();
                            dataBaseAdapter.close();

                            Intent i = new Intent(context, SalesOrderListActivity.class);
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
            staticreviewsalesorderitems.clear();
            //Set total cart item value
            totalcartitems.setText(String.valueOf(staticreviewsalesorderitems.size()));
            salesitems.clear();
            gstnnumber="";

            DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
            dataBaseAdapter.open();
            dataBaseAdapter.DeleteSalesCartItemCart();
            dataBaseAdapter.close();

            Intent i = new Intent(context, SalesOrderListActivity.class);
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

            salestypenamearr[0] = "Order";
            salestypecodearr[0] = "2";
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

            CustomerTypeAdapter.ViewHolder mHolder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.customertypepopuplist, parent, false);
                mHolder = new CustomerTypeAdapter.ViewHolder();
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
                mHolder = (CustomerTypeAdapter.ViewHolder) convertView.getTag();
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

            SalesTypeAdapter.ViewHolder mHolder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.salestypepopuplist, parent, false);
                mHolder = new SalesTypeAdapter.ViewHolder();
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
                mHolder = (SalesTypeAdapter.ViewHolder) convertView.getTag();
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
                    salestypedialog.dismiss();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView listsalestype;

        }

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
//        LL3.setVisibility(View.GONE);
//        LL2.setVisibility(View.GONE);
//        btnVerifypin.setVisibility(View.GONE);
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
//        CountDownTimer.cancel();
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
                btnGetOTP.setEnabled(false);
                txtotpphoneno.setEnabled(false);
                String result = "";
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
//                        if (isSuccessful(jsonObj)) {
//                            otppinview.requestFocus();
//                            CountDownTimer();
//                            LL3.setVisibility(View.VISIBLE);
//                            LL2.setVisibility(View.VISIBLE);
//                            btnVerifypin.setVisibility(View.VISIBLE);
//                        }
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
                            this.getClass().getSimpleName() + " - GETOTP pin", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                }
                finally {
                    if(dataBaseAdapter != null)
                        dataBaseAdapter.close();
                }
//                otppinview.requestFocus();
//                CountDownTimer();
//                LL3.setVisibility(View.VISIBLE);
//                LL2.setVisibility(View.VISIBLE);
//                btnVerifypin.setVisibility(View.VISIBLE);
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
                    }else{
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
                                            dataBaseAdapter1.UpdateCustomerMobilenostatus(customercode);
                                            dataBaseAdapter1.close();
                                            networkstate = isNetworkAvailable();
                                            if (networkstate == true) {
                                                new AsyncCustomerDetails().execute();
                                            }
                                        }
                                        Toast toast = Toast.makeText(getApplicationContext(), "Mobile No. Verified successfully", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
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
//                        mobilenoverificationstatus=1;
//                        if(fromcustomer.equals("verifycustomermobileno")){
//                            DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
//                            dataBaseAdapter.open();
//                            dataBaseAdapter.UpdateCustomerMobilenostatus(customercode);
//                            dataBaseAdapter.close();
//                            networkstate = isNetworkAvailable();
//                            if (networkstate == true) {
//                                new AsyncCustomerDetails().execute();
//                            }
//                        }
//                        Toast toast = Toast.makeText(getApplicationContext(), "Mobile No. Verified successfully", Toast.LENGTH_LONG);
//                        toast.setGravity(Gravity.CENTER, 0, 0);
//                        toast.show();
//                        pindialog.dismiss();

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
}
