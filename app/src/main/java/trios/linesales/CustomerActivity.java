package trios.linesales;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.goodiebag.pinview.Pinview;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class CustomerActivity extends AppCompatActivity {

    ImageView addcustomer;
    Dialog dialog;
    Context context;
    ArrayList<CustomerDetails> customerlist = new ArrayList<CustomerDetails>();
    ArrayList<CustomerDetails> getdata;
    public static SwipeMenuListView listView;
    ImageView customerlistgoback,customerlogout;
    public boolean isinsert = true;
    public  static TextView txtareaname;
    TextView txtcustomername,txtcustomernametamil,txtaddress,txtcityname,
            txtarea,txtphoneno,txtlandlineno,txtemailid,txtgstin,txtadhar,listtxtroute,listtxtarea,totalcustomers,
            txtwhatsappno;
    Button btnSaveCustomer,btnGetOTPCustomer,btnUpdateCustomerLocation;
    String[] citycode,cityname,citynametamil;
    String[] areacode,areaname,areanametamil,customercount;
    Dialog citydialog,areadialog,routedialog;
    ListView lv_CityList,lv_ListAreaList,lv_Routelist,lv_AreaList;
    String getcitycode="0",getareacode="0";
    boolean networkstate;
    String getlistroutecode="0",getlistareacode="0",getmobilenoverifystatus="All Mobile status";
    CustomerListBaseAdapter adapter =null;
    String[] routecode,routename,routenametamil,routeday;
    String[] AreaCode,AreaName,AreaNameTamil,NoOfKm,CityCode,CityName,CustomerCount;
    public static final String GSTINFORMAT_REGEX = "[0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[Z]{1}[0-9a-zA-Z]{1}";
    public static final String GSTN_CODEPOINT_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String listcustomercode=""; String listcustomername=""; String listcustomernametamil="";
    String listaddress=""; String listareacode=""; String listmobileno=""; String listtelephoneno="";
    String listgstin=""; String listareaname="",listcitycode="",listcityname="",listemailid="",listaadharino="";
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String lunch_start_time = "",lunch_end_time="";
    String listwhatsappno="";
    String listmobileverificationstatus="";

    String getcustomertypecode,getsalestypecode;
    TextView txtcustomertype,txtsalestype,OtherRoute,ScheduleRoute,txtroutes;
    String[] customertypecodearr,customertypenamearr;
    String[] salestypecodearr,salestypenamearr;
    Dialog customertypedialog,salestypedialog;
    ListView lv_CustomertypeList,lv_salestypeList;
    String listcustomertype="",listbusinesstype="";
    Integer mobilenoverificationstatus=0,routeflag=0;
    String customername="",customercityarea="",fromcustomer="";
    Dialog pindialog;
    Button btnVerifypin,btnGetOTP;
    Pinview otppinview;
    EditText txtotpphoneno;
    TextView txttimer,txtcustomer,txtcustomercity,txtcustomerarea;
    LinearLayout LL3,LL2,OtherTab,ScheduleTab,OtherRoutesDrp,Routedivisiontab;
    String otptimevalidity="",otptimevaliditybackend="";
    String stphoneno="";
    String otpprocess="send";
    CountDownTimer CountDownTimer;
    public static PreferenceMangr preferenceMangr=null;
    Spinner selectmobilestatus;
    String[] arrmobilestatus;
    public static String getscheduleroutecode;
    GPSTracker gpsTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        context = this;
        listView = (SwipeMenuListView) findViewById(R.id.listView);
        addcustomer = (ImageView)findViewById(R.id.addcustomer);
        customerlistgoback = (ImageView)findViewById(R.id.customerlistgoback);
        customerlogout = (ImageView)findViewById(R.id.customerlogout);
        listtxtroute = (TextView)findViewById(R.id.listtxtroute);
        listtxtarea = (TextView)findViewById(R.id.listtxtarea);
        totalcustomers = (TextView)findViewById(R.id.totalcustomers);
        selectmobilestatus = (Spinner) findViewById(R.id.selectmobilestatus);
        arrmobilestatus = getResources().getStringArray(R.array.mobilestatus);

        try {
            preferenceMangr = new PreferenceMangr(context);
        }catch (Exception e){
            Log.d("Preference Manager : ",e.toString());
        }

        //Set current route
        if(!String.valueOf(MenuActivity.getroutenametamil).equals("")
                && !String.valueOf(MenuActivity.getroutenametamil).equals("null")
                && !String.valueOf(MenuActivity.getroutenametamil).equals(null)) {
            listtxtroute.setText(String.valueOf(MenuActivity.getroutenametamil));
        }
//        else{
//            listtxtroute.setText(String.valueOf(MenuActivity.getroutenametamil));
//        }
        getlistroutecode = preferenceMangr.pref_getString("getroutecode");


        if(preferenceMangr.pref_getString("getroutecode").equals("0")){
            getlistroutecode = "0";
            listtxtroute.setText("");
            listtxtroute.setHint("All Routes");
        }



        addcustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!lunch_start_time.equals("") && !lunch_start_time.equals(" ")  && !lunch_start_time.equals("null")
                        && !lunch_start_time.equals("1900-01-01 00:00:00") && ( lunch_end_time.equals("") ||
                        lunch_end_time.equals(" ")  || lunch_end_time.equals("null") || lunch_end_time.equals("1900-01-01 00:00:00") ) ){
                    Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.lunchstarted), Toast.LENGTH_LONG);
                    //toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }else {
                    if(preferenceMangr.pref_getBoolean(Constants.KEY_GET_GPSTRACKINGSTATUS)){

                        if (gpsTracker.isGPSEnabled || gpsTracker.isNetworkEnabled ) {
                            GetCustomerPopup();
                            //Toast.makeText(getApplicationContext(), "Lat - " + locManager.getLatitude() + "Long - " + locManager.getLongitude(), Toast.LENGTH_SHORT).show();
                            //Log.d("Trip ", "Latlong " + locManager.getLatitude() + "," + locManager.getLongitude());
                        }else{
                            gpsTracker.showSettingsAlert();
                        }
                    }else{
                        GetCustomerPopup();
                    }
//                    GetCustomerPopup();
                }
            }
        });


        //Get Current date
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            //LoginActivity.getformatdate = objdatabaseadapter.GenCreatedDate();
            //LoginActivity.getcurrentdatetime = objdatabaseadapter.GenCurrentCreatedDate();
            otptimevalidity=objdatabaseadapter.Getotptimevalidity();
            otptimevaliditybackend=objdatabaseadapter.Getotptimevaliditybackend();

            preferenceMangr.pref_putString("getformatdate",objdatabaseadapter.GenCreatedDate());
            preferenceMangr.pref_putString("getcurrentdatetime",objdatabaseadapter.GenCurrentCreatedDate());

            Cur = objdatabaseadapter.GetScheduleListDB(preferenceMangr.pref_getString("getformatdate"));
            if(Cur.getCount()>0) {
                for (int i = 0; i < Cur.getCount(); i++) {
                    lunch_start_time = Cur.getString(11);
                    lunch_end_time = Cur.getString(12);
                }
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
            if(Cur != null)
                Cur.close();
        }
        //Logout process
        customerlogout.setOnClickListener(new View.OnClickListener() {
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
        customerlistgoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack(null);
            }
        });

        //Swipe menu editior functionality
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                //  openItem.setBackground(ContextCompat.getDrawable(context,R.drawable.noborder));
                openItem.setWidth(130);
                openItem.setIcon(R.drawable.ic_mode_edit);
                menu.addMenuItem(openItem);
            }
        };
        listView.setMenuCreator(creator);



        //Click swipemenu action ...delete expense
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        ArrayList<CustomerDetails> currentListDatareview = getdata;
                        listcustomercode = currentListDatareview.get(position).getCustomercode();
                        listcustomername = currentListDatareview.get(position).getCustomername();
                        listcustomernametamil = currentListDatareview.get(position).getCustomernametamil();
                        listaddress = currentListDatareview.get(position).getAddress();
                        listareacode = currentListDatareview.get(position).getAreacode();
                        listmobileno = currentListDatareview.get(position).getMobileno();
                        listtelephoneno = currentListDatareview.get(position).getTelephoneno();
                        listgstin = currentListDatareview.get(position).getGstin();
                        listareaname = currentListDatareview.get(position).getAreaname();
                        listcitycode = currentListDatareview.get(position).getCitycode();
                        listcityname = currentListDatareview.get(position).getCityname();
                        listemailid = currentListDatareview.get(position).getEmailid();
                        listaadharino = currentListDatareview.get(position).getAadharno();
                        listcustomertype = currentListDatareview.get(position).getCustomertype();
                        listbusinesstype = currentListDatareview.get(position).getBusinesstype();
                        listwhatsappno = currentListDatareview.get(position).getwhatsappno();
                        listmobileverificationstatus = currentListDatareview.get(position).getmobilenoverificationstatus();
                        Log.w("listmobile",listmobileverificationstatus);
                        if(listmobileverificationstatus.equals("1")){
                            mobilenoverificationstatus=1;
                        }else{
                            mobilenoverificationstatus=0;
                        }
                        GetCustomerUpdatePopup();
                        break;

                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });

        //Get Customer List
        GetCustomerList();

        //Click route dropdown
        listtxtroute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataBaseAdapter objdatabaseadapter = null;
                try {
                    //Order item details
                    objdatabaseadapter = new DataBaseAdapter(context);
                    objdatabaseadapter.open();
                    String getcustomerroute = objdatabaseadapter.GetCustomerStatusDB();
                    if(getcustomerroute.equals("yes")) {
                        GetRouteList();
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

        //Click area dropdown
        listtxtarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetListArea(getlistroutecode);
            }
        });

        // click mobile no status
        selectmobilestatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View view, int i, long l) {
                int index = parentView.getSelectedItemPosition();
                getmobilenoverifystatus = String.valueOf(arrmobilestatus[index]);
                GetCustomerList();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try{
            //locManager = new LocManager(context,SalesActivity.this,Constants.FROM_SALES_ACT);
            if(preferenceMangr.pref_getBoolean(Constants.KEY_GET_GPSTRACKINGSTATUS))
                gpsTracker = new GPSTracker(context);
        }catch (Exception e){

        }
    }

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

    //Customer Popup

    public void GetCustomerPopup(){
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.addcustomer);
        ImageView closepopup = (ImageView) dialog.findViewById(R.id.closepopup);
        txtcustomername = (TextView)dialog.findViewById(R.id.txtcustomername) ;
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
        btnUpdateCustomerLocation = (Button) dialog.findViewById(R.id.btnUpdateCustomerLocation);

        //btnSaveCustomer.setEnabled(false);
        btnSaveCustomer.setText("Save");
        btnUpdateCustomerLocation.setVisibility(View.GONE);

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
                GetArea(getcitycode,preferenceMangr.pref_getString("getroutecode"));
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
                if (txtcustomername.getText().toString().equals("") || txtcustomername.getText().toString().equals("null")
                        || txtcustomername.getText().toString().equals(null)) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter customer name", Toast.LENGTH_LONG);
                    //toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    // Toast.makeText(getApplicationContext(),"Please enter customer name",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (txtcityname.getText().toString().equals("") || txtcityname.getText().toString().equals("null")
                        || txtcityname.getText().toString().equals(null) || getcitycode.equals("") || getcitycode.equals(null)) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please select city name", Toast.LENGTH_LONG);
                    //toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    //  Toast.makeText(getApplicationContext(),"Please select city name",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (txtarea.getText().toString().equals("") || txtarea.getText().toString().equals("null")
                        || txtarea.getText().toString().equals(null) || txtarea.equals("") || txtarea.equals(null)) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please select area name", Toast.LENGTH_LONG);
                    //toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    //Toast.makeText(getApplicationContext(),"Please select area name",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(txtphoneno.getText().toString().equals("") || txtphoneno.getText().toString().equals("null")
                        || txtphoneno.getText().toString().equals(null)){
                    Toast toast = Toast.makeText(getApplicationContext(),"Please enter Mobile No.", Toast.LENGTH_LONG);
                    //toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    //Toast.makeText(getApplicationContext(), "Please enter Mobile No. or GSTIN", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!txtphoneno.getText().toString().equals("") && !txtphoneno.getText().toString().equals("null")
                        && !txtphoneno.getText().toString().equals(null) ) {
                    if (txtphoneno.getText().toString().length() < 10) {
                        Toast toast = Toast.makeText(getApplicationContext(),"Mobile No. should contain 10 digits", Toast.LENGTH_LONG);
                        //toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        //Toast.makeText(getApplicationContext(), "Mobile No. should contain 10 digits", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                customername=txtcustomernametamil.getText().toString().equals("")?txtcustomername.getText().toString():txtcustomernametamil.getText().toString();
                customercityarea= txtcityname.getText().toString() + ',' + txtarea.getText().toString();
                Showmobilenoverify("add");
//                mobilenoverificationstatus=1;
                //btnSaveCustomer.setEnabled(true);
            }
        });
        btnSaveCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getcustomername = txtcustomername.getText().toString();
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
                    //toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                    // Toast.makeText(getApplicationContext(),"Please enter customer name",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(getcityname.equals("") || getcityname.equals("null")
                        || getcityname.equals(null) || getcitycode.equals("")|| getcitycode.equals(null) ){
                    Toast toast = Toast.makeText(getApplicationContext(),"Please select city name", Toast.LENGTH_LONG);
                    //toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    //Toast.makeText(getApplicationContext(),"Please select city name",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(getarea.equals("") || getarea.equals("null")
                        || getarea.equals(null) || getareacode.equals("")|| getareacode.equals(null) ){
                    Toast toast = Toast.makeText(getApplicationContext(),"Please select area name", Toast.LENGTH_LONG);
                    //toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    //Toast.makeText(getApplicationContext(),"Please select area name",Toast.LENGTH_SHORT).show();
                    return;
                }
                if((getphoneno.equals("") || getphoneno.equals("null")
                        || getphoneno.equals(null)) && (getgstin.equals("") || getgstin.equals("null")
                        || getgstin.equals(null)) ){
                    Toast toast = Toast.makeText(getApplicationContext(),"Please enter Mobile No. or GSTIN", Toast.LENGTH_LONG);
                    //toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    //Toast.makeText(getApplicationContext(), "Please enter Mobile No. or GSTIN", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(getphoneno.equals("") || getphoneno.equals("null")
                        || getphoneno.equals(null)){
                    Toast toast = Toast.makeText(getApplicationContext(),"Please enter Mobile No.", Toast.LENGTH_LONG);
                    //toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    //Toast.makeText(getApplicationContext(), "Please enter Mobile No. or GSTIN", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!getphoneno.equals("") && !getphoneno.equals("null")
                        && !getphoneno.equals(null) ) {
                    if (getphoneno.length() < 10) {
                        Toast toast = Toast.makeText(getApplicationContext(),"Mobile No. should contain 10 digits", Toast.LENGTH_LONG);
                        //toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        //Toast.makeText(getApplicationContext(), "Mobile No. should contain 10 digits", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if(mobilenoverificationstatus==0){
                    Toast toast = Toast.makeText(getApplicationContext(),"Please verify Mobile No.", Toast.LENGTH_LONG);
                    //toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    //Toast.makeText(getApplicationContext(), "Mobile No. should contain 10 digits", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!getwhatsappno.equals("") && !getwhatsappno.equals("null")
                        && !getwhatsappno.equals(null) ) {
                    if (getwhatsappno.length() < 10) {
                        Toast toast = Toast.makeText(getApplicationContext(),"Whatsapp No. should contain 10 digits", Toast.LENGTH_LONG);
                        //toast.setGravity(Gravity.CENTER, 0, 0);
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
                                //toast.setGravity(Gravity.CENTER, 0, 0);
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
                        //toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        //Toast.makeText(getApplicationContext(), "Aadhar No. should contain 16 digits", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if(!getemailid.equals("") && !getemailid.equals("null")
                        && !getemailid.equals(null) ) {
                    if (!getemailid.matches(emailPattern) && getemailid.length() > 0) {
                        Toast toast = Toast.makeText(getApplicationContext(),"Please enter valid email ID", Toast.LENGTH_LONG);
                        //toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        // Toast.makeText(getApplicationContext(), "Please enter valid email ID", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if ( getcustomertype.equals("") || getcustomertype.equals("null") || getcustomertype.equals(null) ||
                        getcustomertypecode.equals("") || getcustomertypecode.equals("null") || getcustomertypecode.equals(null) ) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please select customer type", Toast.LENGTH_LONG);
                    //toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    //Toast.makeText(getApplicationContext(), "Please enter Mobile No. or GSTIN", Toast.LENGTH_SHORT).show();
                    return;
                }

                if ( getsalestype.equals("") || getsalestype.equals("null") || getsalestype.equals(null) ||
                        getsalestypecode.equals("") || getsalestypecode.equals("null") || getsalestypecode.equals(null)) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please select sales type", Toast.LENGTH_LONG);
                    //toast.setGravity(Gravity.CENTER, 0, 0);
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

                    String getresult="";
                    String getretailercount = objdatabaseadapter.CheckCustomerAlreadyExistsCount(getareacode,getcustomername);
                    if(getretailercount.equals("Success")) {
                        getresult = objdatabaseadapter.InsertCustomerDetails(getcustomername, getcustomernametamil,
                                getaddress, getareacode, getemailid, getphoneno, getlandlineno, getaadharno,
                                getgstin,getcustomertypecode,getsalestypecode,getwhatsappno,mobilenoverificationstatus,latitude,longtitude);
                        if (!getresult.equals("")) {
                            Toast toast = Toast.makeText(getApplicationContext(),"Saved Successfully", Toast.LENGTH_LONG);
                            //toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();

                            //Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_SHORT).show();
                            getcustomertypecode="";
                            getsalestypecode="";
                            mobilenoverificationstatus=0;
                            dialog.dismiss();
                            btnSaveCustomer.setEnabled(true);
                            GetCustomerList();
                        }
                    }else{
                        btnSaveCustomer.setEnabled(true);
                        Toast toast = Toast.makeText(getApplicationContext(),"Customer name already exists", Toast.LENGTH_LONG);
                        //toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        //Toast.makeText(context, "Customer name already exists", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    networkstate = isNetworkAvailable();
                    if (networkstate == true) {
                        new AsyncCustomerDetails().execute();
                    }

                } catch (Exception e) {
                    btnSaveCustomer.setEnabled(true);
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

    public void GetCustomerUpdatePopup(){
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.addcustomer);
        ImageView closepopup = (ImageView) dialog.findViewById(R.id.closepopup);
        txtcustomername = (TextView)dialog.findViewById(R.id.txtcustomername) ;
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
        txtwhatsappno = (TextView) dialog.findViewById(R.id.txtwhatsappno);
        btnGetOTPCustomer = (Button)dialog.findViewById(R.id.btnGetOTPCustomer);
        btnUpdateCustomerLocation = (Button) dialog.findViewById(R.id.btnUpdateCustomerLocation);

       // btnSaveCustomer.setEnabled(false);

        txtcustomername.setEnabled(false);
        txtcustomernametamil.setEnabled(false);
        txtaddress.setEnabled(false);
        txtcityname.setEnabled(false);
        txtarea.setEnabled(false);
        txtcustomername.setBackgroundResource(R.drawable.editbackgroundgray);
        txtcustomernametamil.setBackgroundResource(R.drawable.editbackgroundgray);
        txtaddress.setBackgroundResource(R.drawable.editbackgroundgray);
        txtcityname.setBackgroundResource(R.drawable.editbackgroundgray);
        txtarea.setBackgroundResource(R.drawable.editbackgroundgray);


        txtphoneno.setEnabled(true);
        txtlandlineno.setEnabled(true);
        txtemailid.setEnabled(true);
        txtgstin.setEnabled(true);
        txtadhar.setEnabled(true);
        txtwhatsappno.setEnabled(true);
        //Set Text
        txtcustomername.setText(listcustomername);
        txtcustomernametamil.setText(listcustomernametamil);
        txtaddress.setText(listaddress);
        txtcityname.setText(listcityname);
        txtarea.setText(listareaname);
        txtphoneno.setText(listmobileno);
        txtlandlineno.setText(listtelephoneno);
        txtemailid.setText(listemailid);
        txtgstin.setText(listgstin);
        txtadhar.setText(listaadharino);
        txtwhatsappno.setText(listwhatsappno);
        stphoneno=listmobileno;
        if(listcustomertype.equals("1")){
            txtcustomertype.setText("Cash Party");
        }else if(listcustomertype.equals("2")){
            txtcustomertype.setText("Credit Party");
        }

        if(listbusinesstype.equals("1")){
            txtsalestype.setText("Line Sales");
        }else if(listbusinesstype.equals("2")){
            txtsalestype.setText("Order");
        }else if(listbusinesstype.equals("3")){
            txtsalestype.setText("Both");
        }

        btnSaveCustomer.setText("Update");
        btnUpdateCustomerLocation.setVisibility(View.VISIBLE);
        getcustomertypecode=listcustomertype;
        getsalestypecode=listbusinesstype;

        txtcustomertype.setEnabled(false);
        txtcustomertype.setBackgroundResource(R.drawable.editbackgroundgray);
        //txtcustomertype.setText("Cash Party");
        //getcustomertypecode= "1";

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

        if (stphoneno.equals(txtphoneno.getText().toString()) && mobilenoverificationstatus==1) {
            btnGetOTPCustomer.setText("Change");
        }

        btnGetOTPCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (txtcustomername.getText().toString().equals("") || txtcustomername.getText().toString().equals("null")
                            || txtcustomername.getText().toString().equals(null)) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please enter customer name", Toast.LENGTH_LONG);
                        //toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        // Toast.makeText(getApplicationContext(),"Please enter customer name",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (txtcityname.getText().toString().equals("") || txtcityname.getText().toString().equals("null")
                            || txtcityname.getText().toString().equals(null) || getcitycode.equals("") || getcitycode.equals(null)) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please select city name", Toast.LENGTH_LONG);
                        //toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        //  Toast.makeText(getApplicationContext(),"Please select city name",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (txtarea.getText().toString().equals("") || txtarea.getText().toString().equals("null")
                            || txtarea.getText().toString().equals(null) || txtarea.equals("") || txtarea.equals(null)) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please select area name", Toast.LENGTH_LONG);
                        //toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        //Toast.makeText(getApplicationContext(),"Please select area name",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (txtphoneno.getText().toString().equals("") || txtphoneno.getText().toString().equals("null")
                            || txtphoneno.getText().toString().equals(null)) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please enter Mobile No.", Toast.LENGTH_LONG);
                        //toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        //Toast.makeText(getApplicationContext(), "Please enter Mobile No. or GSTIN", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!txtphoneno.getText().toString().equals("") && !txtphoneno.getText().toString().equals("null")
                            && !txtphoneno.getText().toString().equals(null)) {
                        if (txtphoneno.getText().toString().length() < 10) {
                            Toast toast = Toast.makeText(getApplicationContext(), "Mobile No. should contain 10 digits", Toast.LENGTH_LONG);
                            //toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            //Toast.makeText(getApplicationContext(), "Mobile No. should contain 10 digits", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    customername = txtcustomernametamil.getText().toString().equals("") ? txtcustomername.getText().toString() : txtcustomernametamil.getText().toString();
                    customercityarea = txtcityname.getText().toString() + ',' + txtarea.getText().toString();
                    Showmobilenoverify("add");
//                mobilenoverificationstatus=1;
//                btnSaveCustomer.setEnabled(true);

            }
        });

        btnUpdateCustomerLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gpsTracker = new GPSTracker(getApplicationContext());
                btnUpdateCustomerLocation.setEnabled(false);
                double latitude = 0;
                double longitude = 0;
                String address = "";
                String city = "";
                String state = "";
                String country = "";
                String postalCode = "";
                List<Address> addresses;
                if(gpsTracker.isGPSTrackingEnabled) {
                    latitude = gpsTracker.getLatitude();
                    longitude = gpsTracker.getLongitude();
                    Log.d("latitiude",String.valueOf(latitude));
                    Log.d("longitude",String.valueOf(longitude));

                    final double finalLatitude = latitude;
                    final double finalLongitude = longitude;
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Confirmation");
                    builder.setMessage("Are you sure you want to update the customer location?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    DataBaseAdapter objdatabaseadapter = null;
                                    try {

                                        //Order item details
                                        objdatabaseadapter = new DataBaseAdapter(context);
                                        objdatabaseadapter.open();
                                        String getresult="";
                                        getresult = objdatabaseadapter.UpdateCustomerLocation(listcustomercode, finalLatitude, finalLongitude);
                                        if (getresult.equals("success")) {
                                            Toast toast = Toast.makeText(getApplicationContext(),"Updated Successfully", Toast.LENGTH_LONG);
                                            //toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
                                            //Toast.makeText(getApplicationContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                            btnUpdateCustomerLocation.setEnabled(true);
                                            GetCustomerList();
                                        }

                                        if (Utilities.isNetworkAvailable(getApplicationContext())) {
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
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    btnUpdateCustomerLocation.setEnabled(true);
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    gpsTracker.showSettingsAlert();
                }
            }
        });

        btnSaveCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getcustomername = txtcustomername.getText().toString();
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
                    //Toast.makeText(getApplicationContext(),"Please select city name",Toast.LENGTH_SHORT).show();
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
                        return;
                    }
                }

                if(!getemailid.equals("") && !getemailid.equals("null")
                        && !getemailid.equals(null) ) {
                    if (!getemailid.matches(emailPattern) && getemailid.length() > 0) {
                        Toast toast = Toast.makeText(getApplicationContext(),"Please enter valid email ID", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        // Toast.makeText(getApplicationContext(), "Please enter valid email ID", Toast.LENGTH_SHORT).show();
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
                    getresult = objdatabaseadapter.UpdateCustomerDetails(getcustomername, getcustomernametamil,
                            getaddress, getareacode, getemailid, getphoneno, getlandlineno, getaadharno, getgstin,listcustomercode,
                            getcustomertypecode,getsalestypecode,getwhatsappno,mobilenoverificationstatus);
                    if (getresult.equals("success")) {
                        Toast toast = Toast.makeText(getApplicationContext(),"Updated Successfully", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        //Toast.makeText(getApplicationContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                        getcustomertypecode = "";
                        getsalestypecode = "";
                        mobilenoverificationstatus=0;
                        dialog.dismiss();
                        btnSaveCustomer.setEnabled(true);
                        GetCustomerList();
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
    //Get Customer List
    public void GetCustomerList(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            customerlist.clear();
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetCustomerListDB(getlistroutecode,getlistareacode,getmobilenoverifystatus);
            totalcustomers.setText(String.valueOf(Cur.getCount()));
            if(Cur.getCount()>0) {
                for(int i=0;i<Cur.getCount();i++){
                    customerlist.add(new CustomerDetails(Cur.getString(0),Cur.getString(1),
                            Cur.getString(2),Cur.getString(3),Cur.getString(4),
                            Cur.getString(5),Cur.getString(6),Cur.getString(7)
                            ,Cur.getString(8),Cur.getString(9),
                            Cur.getString(10),Cur.getString(11)
                            ,Cur.getString(12),String.valueOf(i+1),Cur.getString(13),Cur.getString(14),
                            Cur.getString(15),Cur.getString(16)));
                    Cur.moveToNext();
                }
                getdata = customerlist;
                adapter =  new CustomerListBaseAdapter(context, customerlist);
                listView.setAdapter(adapter);
            }else{
                adapter = new CustomerListBaseAdapter(context, customerlist);
                listView.setAdapter(adapter);
                Toast toast = Toast.makeText(getApplicationContext(),"No Customer Available", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //Toast.makeText(getApplicationContext(),"No Customer Available",Toast.LENGTH_SHORT).show();
            }
        }  catch (Exception e){
            Log.i("SalesList", e.toString());
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
    //*************Drop Down Functionality*********/
    //Get City name
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
                //Toast.makeText(getApplicationContext(),"No city in this route",Toast.LENGTH_SHORT).show();
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

    public  void GetListArea(String routecode){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetCustomerListAreaDB(routecode);
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
                lv_ListAreaList = (ListView) areadialog.findViewById(R.id.lv_AreaList);
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
                AreaListAdapter adapter = new AreaListAdapter(context);
                lv_ListAreaList.setAdapter(adapter);
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

    //Get Route List
    public void GetRouteList(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            customerlist.clear();
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetRouteListDB();
            if(Cur.getCount()>0) {
                routecode = new String[Cur.getCount()];
                routename = new String[Cur.getCount()];
                routenametamil = new String[Cur.getCount()];
                for(int i=0;i<Cur.getCount();i++){
                    routecode[i] = Cur.getString(0);
                    routename[i] = Cur.getString(1);
                    routenametamil[i] = Cur.getString(2);
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
                Toast toast = Toast.makeText(getApplicationContext(),"No Route Available", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //Toast.makeText(getApplicationContext(),"No Route Available",Toast.LENGTH_SHORT).show();
            }
        }  catch (Exception e){
            Log.i("SalesList", e.toString());
        }
        finally {
            // this gets called even if there is an exception somewhere above
            if(objdatabaseadapter != null)
                objdatabaseadapter.close();
            if(Cur != null)
                Cur.close();
        }

    }


    //Get Area name
    public  void GetArea(String citycode,String routecode){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetAreaCityDB(citycode,routecode);
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
                lv_AreaList = (ListView) areadialog.findViewById(R.id.lv_AreaList);
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

    //*************END Drop Down Functionality*********/

    /******Base Adapter***********/
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
               /* if(!String.valueOf(areanametamil[position]).equals("")
                        && !String.valueOf(areanametamil[position]).equals("null")
                        && !String.valueOf(areanametamil[position]).equals(null)) {*/
                mHolder.listareaname.setText(String.valueOf(areanametamil[position]));
               /* }else{
                    mHolder.listareaname.setText(String.valueOf(areaname[position]));
                }*/
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
                mHolder.listcustomercount.setTag(position);
                mHolder.listareaname.setTag(position);
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
                  /*  if(!String.valueOf(areanametamil[position]).equals("")
                            && !String.valueOf(areanametamil[position]).equals("null")
                            && !String.valueOf(areanametamil[position]).equals(null)) {*/
                    txtarea.setText(String.valueOf(areanametamil[position]));
                   /* }else{
                        txtarea.setText(String.valueOf(areaname[position]));
                    }*/
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

            ViewHolder mHolder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.routepopuplist, parent, false);
                mHolder = new ViewHolder();
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
                mHolder = (ViewHolder) convertView.getTag();
            }
            try {
                if(!String.valueOf(routenametamil[position]).equals("")
                        && !String.valueOf(routenametamil[position]).equals("null")
                        && !String.valueOf(routenametamil[position]).equals(null)) {
                    mHolder.listroutename.setText(String.valueOf(routenametamil[position]));


                }else{
                    mHolder.listroutename.setText(String.valueOf(routename[position]));


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
                    if(!String.valueOf(routenametamil[position]).equals("")
                            && !String.valueOf(routenametamil[position]).equals("null")
                            && !String.valueOf(routenametamil[position]).equals(null)) {
                        listtxtroute.setText(String.valueOf(routenametamil[position]));

                    }else{
                        listtxtroute.setText(String.valueOf(routename[position]));

                    }
                    getlistroutecode = routecode[position];
                    listtxtarea.setText("");
                    getlistareacode ="0";
                    listtxtarea.setHint("All Areas");

                    routedialog.dismiss();

                    GetCustomerList();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView listroutename;
        }
    }


    //Area Adapter
    public class AreaListAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;

        AreaListAdapter(Context c) {
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
                if(CityName[position].equals("")){
                    mHolder.listareaname.setText(String.valueOf(AreaName[position] ));
                }else {
                    mHolder.listareaname.setText(String.valueOf(AreaName[position] + " - " + CityName[position]));
                }
                mHolder.listcustomercount.setText(String.valueOf(CustomerCount[position]));

                int getcount = 0;
                for(int i = 0;i<AreaCode.length;i++){
                    if(!AreaCode[i].equals("0")){
                        getcount = getcount + Integer.parseInt(CustomerCount[i]);
                    }
                }

                if(AreaCode[position].equals("0")){
                    mHolder.listcustomercount.setText(String.valueOf(getcount));
                }

                mHolder.listcustomercount.setTag(position);
                mHolder.listareaname.setTag(position);
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
                    if(CityName[position].equals("")){
                        listtxtarea.setText(String.valueOf(AreaName[position]));
                    }else {
                        listtxtarea.setText(String.valueOf(AreaName[position] + " - " + CityName[position]));
                    }
                    getlistareacode =AreaCode[position];
                    areadialog.dismiss();
                    GetCustomerList();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView listareaname,listcustomercount;

        }

    }

    /**********END Base Adapter*********/

    /**********Asynchronous Claass***************/

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
            try {
                if (result.size() >= 1) {
                    if (result.get(0).CustomerCode.length > 0) {
                        for (int j = 0; j < result.get(0).CustomerCode.length; j++) {
                            DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
                            dataBaseAdapter.open();
                            dataBaseAdapter.UpdateCustomerFlag(result.get(0).CustomerCode[j]);
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
                    DataBaseAdapter otpdataBaseAdapter =null;
                    JSONObject jsonObj = null;
                    RestAPI api = new RestAPI();
                    try {
                        otpdataBaseAdapter = new DataBaseAdapter(context);
                        otpdataBaseAdapter.open();
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
                        Log.e("GET OTP DIB", e.getMessage());
                        if(otpdataBaseAdapter != null)
                            otpdataBaseAdapter.close();
                        DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                        mDbErrHelper.open();
                        String geterrror = e.toString();
                        mDbErrHelper.insertErrorLog(geterrror.replace("'", " "),
                                this.getClass().getSimpleName() + " Customer : AsyncCheckCustomerMobileNumber : - GETOTP pin", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        mDbErrHelper.close();
                        btnGetOTP.setEnabled(true);
                    }
                    finally {
                        if(otpdataBaseAdapter != null)
                            otpdataBaseAdapter.close();
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
            loading = ProgressDialog.show(context, "", "Please wait...", true, true);
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

    protected  class Asynccheckotp extends
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
                    jsonObj = api.GetOTPapi(txtotpphoneno.getText().toString(),"",
                            otppinview.getValue() ,"checkotp.php",context);
                    if(jsonObj!=null) {
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
            loading = ProgressDialog.show(context, "", "Please wait...", true, true);
            loading.setCancelable(false);
            loading.setCanceledOnTouchOutside(false);
        }

        @Override
        protected void onPostExecute(ArrayList<CustomerDatas> result) {
            // TODO Auto-generated method stub
            try {
                if (success1.equals("1")) {
                    if(fromcustomer.equals("add")) {
                        txtphoneno.setText(txtotpphoneno.getText());
                    }
                    mobilenoverificationstatus=1;
                    btnVerifypin.setEnabled(true);
                    Toast toast = Toast.makeText(getApplicationContext(), "Mobile No. Verified successfully", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    pindialog.dismiss();
                }else{
                    String message = jsonObj.getString("message");
                    Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    btnVerifypin.setEnabled(true);
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
    /**********END Asynchronous Claass***************/

    public void goBack(View v) {
        LoginActivity.ismenuopen=true;
        Intent i = new Intent(context, MenuActivity.class);
        startActivity(i);
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
            salestypecodearr = new String[3];
            salestypenamearr = new String[3];

            salestypenamearr[0] = "Line Sales";
            salestypecodearr[0] = "1";
            salestypenamearr[1] = "Order";
            salestypecodearr[1] = "2";
            salestypenamearr[2] = "Both";
            salestypecodearr[2] = "3";


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
        ProgressDialog loading;
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

                if (isNetworkAvailable()){
                    new AsyncCheckCustomerMobileNumber().execute();
                } else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Please check internet connection", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
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
                    mobilenoverificationstatus=0;
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
                        btnVerifypin.setEnabled(false);
                        String result = "";
                        DataBaseAdapter dataBaseAdapter =null;
                        JSONObject jsonObj = null;
                        RestAPI api = new RestAPI();
                        try {
                            dataBaseAdapter = new DataBaseAdapter(context);
                            dataBaseAdapter.open();
                            networkstate = isNetworkAvailable();
                            if (networkstate == true) {
                                new Asynccheckotp().execute();
//                                if (success.equals("1")) {
//                                    mobilenoverificationstatus=1;
//                                    btnVerifypin.setEnabled(true);
//                                    Toast toast = Toast.makeText(getApplicationContext(), "Mobile No. Verified successfully", Toast.LENGTH_LONG);
//                                    toast.setGravity(Gravity.CENTER, 0, 0);
//                                    toast.show();
//                                    pindialog.dismiss();
//                                }else{
//                                    String message = jsonObj.getString("message");
//                                    Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
//                                    toast.setGravity(Gravity.CENTER, 0, 0);
//                                    toast.show();
//                                    btnVerifypin.setEnabled(true);
//                                }
                            }else{
                                btnVerifypin.setEnabled(true);
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
        CountDownTimer =  new CountDownTimer(milliseconds, 1000) {
            public void onTick(long millisUntilFinished) {
                txttimer.setText("00:00:00");
                if(strfrom.equals("initial")){
//                    if (CountDownTimer != null) {
                        CountDownTimer.cancel();
//                    }
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
//                otppinview.setValue("");
//                txtphoneno.setText("");
                btnGetOTP.setEnabled(true);
                txtotpphoneno.setEnabled(true);
                otpprocess="resend";
            }
        };
        CountDownTimer.start();
    }
    private boolean isSuccessful(JSONObject object) {
        boolean success = false;
        if (object != null) {
            try {
                String success1 = object.getString("success");
                if (success1.equals("1") || object.getInt("success")==1) {
                    success = (object.getJSONArray("Value").length()) > 0;
                }
            } catch (JSONException e) {
                Log.d("JSON Error", e.getMessage());
            }
        }
        return success;
    }


}
