package trios.linesales;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;
import com.goodiebag.pinview.Pinview;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;

public class ReceiptActivity extends AppCompatActivity {
    public static SwipeMenuListView receiptlistView;
    ImageView addreceipt;
    Context context;
    private int year, month, day;
    private Calendar calendar;
    public static  TextView receiptsubtotalamt,txtreceiptlistdate,cashtotalamt,chequetotalamt,upitotalamt;
    public static SwipeMenuItem openItem,deleteItem;
    ImageButton syncreceiptdetails,receiptgoback,receiptgotohome,receiptlogout;
    boolean networkstate;
    Dialog dialog;
    ImageView closepopup,imageView;
    TextView txtreceiptdate,txtcompanyname,
            txtareaname,txtcustomername,txtremarks,txtnote,txtupivendername,txtupitransactionID;
    String getcompanycode="0",getcustomercode="0",getgstnnumber="0",getremarkscode="0",
            getpaymentvenderID="0",getimageurl="";
    EditText edittxtamount,edittxtchequerefno,edittxtchequebankname;
    TextView edittxtchequedate;
    Button btnsavereceipt;
    public  String getreceiptdate;
    ArrayList<ReceiptDetails> receiptlist = new ArrayList<ReceiptDetails>();
    public static ArrayList<ReceiptDetails> getdata;

    public static String getreceiptlistdate,getscheduleroutecode ;
    RadioButton radio_cheque,radio_cash,radio_upi;;
    String[] companycode,companyname,shortname,imageurl;
    Dialog companydialog,areadialog,customerdialog,remarksdialog,upipaymentvenderdialog;
    ListView lv_CompanyList,lv_AreaList,lv_CustomerList,lv_RemarksList,lv_AreaListAdapter,lv_Routelist,lv_UPIPaymentList;

    String[] venderid,vendername;
    LinearLayout LLChequeDetails,LLUPIDetails,LL2UPIDetails;

    String getreceipdate;
    String[] AreaCode,AreaName,AreaNameTamil,NoOfKm,CityCode,CityName,CustomerCount;
    String[] CustomerCode,CustomerName,CustomerNameTamil,Address,
            CustomerAreaCode,MobileNo,TelephoneNo,GSTN,SchemeApplicable,customertypecode,Customerbillcount;
    String[] receiptremarks,receiptremarkscode;
    String getlistcompanycode="0",getlistareacode="0";
    TextView receiptcompany,receiptarea,OtherRoute,ScheduleRoute,txtroutes;
    public static boolean deviceFound;
    //private PrintData printData;
    Dialog printpopup;

    Dialog salescashclose;
    TextView popup_salesclose,popup_cashclose,popup_salesclose_title;
    Dialog pindialog,routedialog;
    Pinview pinview;
    Button btnSubmitpin;
    public static String textchequedate ;
    BluetoothAdapter mBluetoothAdapter;
    public static PreferenceMangr preferenceMangr=null;
    ListView lv_freeitemlist;
    String[] FreeItemName,FreeItemOp,FreeItemHandover,FreeItemDistributed,FreeItemBalance,FreeItemCode,FreeItemSNO;
    Dialog freeitemdialog;

    LinearLayout OtherTab,ScheduleTab,OtherRoutesDrp,Routedivisiontab;
    Integer routeflag=0,pageflag=0;
    String[] routecode,routename,routenametamil,routeday;
    boolean issalesclosepopup=false;
    Dialog dialogstatus;
    String receiptTransano="";
    ReceiveListener receiveListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);
        context=this;
        receiptlistView = (SwipeMenuListView) findViewById(R.id.receiptlistView);
        addreceipt = (ImageView)findViewById(R.id.addreceipt);
        receiptsubtotalamt = (TextView)findViewById(R.id.receiptsubtotalamtval);
        receiptgoback= (ImageButton)findViewById(R.id.receiptgoback);
        receiptlogout = (ImageButton)findViewById(R.id.receiptlogout);
        txtreceiptlistdate = (TextView)findViewById(R.id.txtreceiptlistdate);
        receiptcompany = (TextView)findViewById(R.id.receiptcompany);
        receiptarea = (TextView)findViewById(R.id.receiptarea);
        cashtotalamt = (TextView)findViewById(R.id.cashtotalamt);
        chequetotalamt = (TextView)findViewById(R.id.chequetotalamt);
        upitotalamt = (TextView)findViewById(R.id.upitotalamt);

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
            Log.d("Bluetooth Adapter : ",e.toString());
        }

        receiptgoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack(null);
            }
        });

        //Get Current date
        DataBaseAdapter objdatabaseadapter = null;
        Cursor getschedulelist = null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            //LoginActivity.getformatdate = objdatabaseadapter.GenCreatedDate();
            //LoginActivity.getcurrentdatetime = objdatabaseadapter.GenCurrentCreatedDate();

            preferenceMangr.pref_putString("getformatdate",objdatabaseadapter.GenCreatedDate());
            preferenceMangr.pref_putString("getcurrentdatetime",objdatabaseadapter.GenCurrentCreatedDate());

            getschedulelist = objdatabaseadapter.GetScheduleDB();
            if(getschedulelist.getCount() >0){
                for(int i=0;i<getschedulelist.getCount();i++) {
                    MenuActivity.getschedulecode = getschedulelist.getString(0);
                    preferenceMangr.pref_putString("getschedulecode",getschedulelist.getString(0));
                }
            }else{
                MenuActivity.getschedulecode = "";
                preferenceMangr.pref_putString("getschedulecode","");
            }
            MenuActivity.getdenominationcount = objdatabaseadapter.GetDenominationCount(preferenceMangr.pref_getString("getschedulecode"));
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

            if (getschedulelist != null)
                getschedulelist.close();
        }

        txtreceiptlistdate.setText(preferenceMangr.pref_getString("getcurrentdatetime"));
        getreceiptlistdate = preferenceMangr.pref_getString("getformatdate");

        receiptlogout.setOnClickListener(new View.OnClickListener() {
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


        //Click receipt company
        receiptcompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetListCompanyName();
            }
        });

        //Click Area list
        receiptarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetListArea(preferenceMangr.pref_getString("getroutecode"));
            }
        });





        //Set Now Date
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);


        txtreceiptlistdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String vardate = "";
                        String getmonth="";
                        String getdate="";
                        monthOfYear = monthOfYear + 1;
                        if (dayOfMonth < 10) {
                            vardate = "0" + dayOfMonth;
                            getdate = "0" + dayOfMonth;
                        } else {
                            vardate = String.valueOf(dayOfMonth);
                            getdate = String.valueOf(dayOfMonth);
                        }
                        if (monthOfYear < 10) {
                            vardate = vardate + "-" + "0" + monthOfYear;
                            getmonth = "0" + monthOfYear;;
                        } else {
                            vardate = vardate +"-" + monthOfYear;
                            getmonth = String.valueOf(monthOfYear);;
                        }
                        vardate = vardate + "-" + year;
                        getreceiptlistdate = year+ "-"+getmonth+"-"+getdate;
                        txtreceiptlistdate.setText(vardate );
                        GetReceiptList();

                    }
                };
                DatePickerDialog dpDialog = new DatePickerDialog(context, listener, year, month, day);
                dpDialog.show();
            }
        });

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "print" item
                openItem = new SwipeMenuItem(
                        getApplicationContext());
               // openItem.setBackground(ContextCompat.getDrawable(context,R.drawable.noborder));
                openItem.setWidth(100);
                openItem.setIcon(R.drawable.ic_print_black);
                menu.addMenuItem(openItem);

                // create "delete" item
                deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                //deleteItem.setBackground(ContextCompat.getDrawable(context,R.drawable.border));
                // set item width
                deleteItem.setWidth(100);

                // set a icon
                deleteItem.setIcon(R.drawable.ic_cancel_black);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        receiptlistView.setMenuCreator(creator);

        //Click swipemenu action ...delete expense
        receiptlistView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        ArrayList<ReceiptDetails> currentListDatareviewprint = getdata;
                        final String gettransactionnoprint = currentListDatareviewprint.get(position).getTransactionno();
                        final String getfinyrcode = currentListDatareviewprint.get(position).getFinancialyrcode();
                        String getflag1 = currentListDatareviewprint.get(position).getFlag();
                        if(!getflag1.equals("3") && !getflag1.equals("6")) {

                            printpopup = new Dialog(context);
                            printpopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            printpopup.setContentView(R.layout.printpopup);

                            TextView txtYesAction = (TextView) printpopup.findViewById(R.id.txtYesAction);
                            TextView txtNoAction = (TextView) printpopup.findViewById(R.id.txtNoAction);
                            txtYesAction.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                        /*confirmation.setVisibility(View.GONE);
                        pleasewait.setVisibility(View.VISIBLE);*/

                                    final String finalGetreceipttransano = gettransactionnoprint;
                                    final String financialyearcode = getfinyrcode;
                                    try {

                                        if(mBluetoothAdapter != null) {

                                            if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                                                if(!preferenceMangr.pref_getString("SelectedPrinterAddress").equals("")) {
                                                    new AsyncPrintReceiptDetails().execute(gettransactionnoprint,getfinyrcode);
                                                    printpopup.dismiss();
                                                }else{
                                                    Toast toast = Toast.makeText(getApplicationContext(), "Please select the bluetooth printer in app", Toast.LENGTH_LONG);
                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                    toast.show();
                                                    printpopup.dismiss();
                                                }
                                            }else{
                                                Toast toast = Toast.makeText(getApplicationContext(), "Please turn on the bluetooth", Toast.LENGTH_LONG);
                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                toast.show();
                                                printpopup.dismiss();
                                            }
                                        }else{
                                            Toast toast = Toast.makeText(getApplicationContext(), "Bluetooth is not available.", Toast.LENGTH_LONG);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
                                            printpopup.dismiss();
                                        }
                                    }
                                    catch (Exception e){
                                        Toast toast = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                        printpopup.dismiss();
                                        /*DataBaseAdapter mDbErrHelper2 = new DataBaseAdapter(context);
                                        mDbErrHelper2.open();
                                        mDbErrHelper2.insertErrorLog(e.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                        mDbErrHelper2.close();*/

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

                            /*android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(context);
                            String message = "Are you sure you want to print?";
                            final String finalGetreceipttransano = gettransactionnoprint;
                            final String financialyearcode = getfinyrcode;
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
                                                    Toast toast = Toast.makeText(getApplicationContext(),"Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                    toast.show();
                                                   // Toast.makeText(context, "Please connect to the Bluetooth Printer!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    boolean billPrinted = false;
                                                    billPrinted = (boolean) printData.GetReceiptBillPrint(finalGetreceipttransano, financialyearcode);
                                                    if (!billPrinted) {
                                                        Toast toast = Toast.makeText(getApplicationContext(),"Unable to connect to Bluetooth Printer!", Toast.LENGTH_LONG);
                                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                                        toast.show();
                                                       // Toast.makeText(context, "Unable to connect to Bluetooth Printer!", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }
                                                }
                                            } catch (Exception e) {
                                                Toast toast = Toast.makeText(getApplicationContext(),"Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                toast.show();
                                                //Toast.makeText(context, "Please connect to the Bluetooth Printer!", Toast.LENGTH_SHORT).show();
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
                                            ;
                                        }
                                    }).show();*/
                        }else{
                            Toast toast = Toast.makeText(getApplicationContext(),"Receipt already cancelled", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            //Toast.makeText(getApplicationContext(), "Receipt already cancelled", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    break;
                    case 1:
                        ArrayList<ReceiptDetails> currentListDatareview = getdata;
                        final String gettransactionno = currentListDatareview.get(position).getTransactionno();
                        String getflag = currentListDatareview.get(position).getFlag();
                        String listschedulecode1 = currentListDatareview.get(position).getSchedulecode();
                        String listsalescount1 = "";
                        //Get Current date
                        DataBaseAdapter objdatabaseadapter1 = null;
                        try{
                            objdatabaseadapter1 = new DataBaseAdapter(context);
                            objdatabaseadapter1.open();
                            listsalescount1 = objdatabaseadapter1.GetSalesClose(listschedulecode1);
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
                        String getpaymentflag = "";
                        if(listsalescount1.equals("") || listsalescount1.equals("0") ||
                                listsalescount1.equals("null") ||listsalescount1.equals(null)){
                            getpaymentflag="false";
                        }else{
                            getpaymentflag="true";
                        }
                        /*if(getpaymentflag.equals("true")){
                            Toast toast = Toast.makeText(getApplicationContext(),"Schedule Closed", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            break ;
                        }
                        if(!MenuActivity. getsalesclosecount.equals("0") && !MenuActivity. getsalesclosecount.equals("null") &&
                                !MenuActivity. getsalesclosecount.equals("")
                                && !MenuActivity. getsalesclosecount.equals(null)){
                            Toast toast = Toast.makeText(getApplicationContext(),"Sales Closed ", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            //Toast.makeText(getApplicationContext(), "Cash Closed ", Toast.LENGTH_SHORT).show();
                            //break;
                            return false;
                        }*/
                        if(!preferenceMangr.pref_getString("getcashclosecount").equals("0") && !preferenceMangr.pref_getString("getcashclosecount").equals("null") &&
                                !preferenceMangr.pref_getString("getcashclosecount").equals("")
                                && !preferenceMangr.pref_getString("getcashclosecount").equals(null)  ){
                            Toast toast = Toast.makeText(getApplicationContext(),"Cash Closed ", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            //Toast.makeText(getApplicationContext(), "Cash Closed ", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        if(!getflag.equals("3") && !getflag.equals("6")) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("Are you sure you want to cancel?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            DataBaseAdapter objdatabaseadapter = null;
                                            try {
                                                //Order item details
                                                objdatabaseadapter = new DataBaseAdapter(context);
                                                objdatabaseadapter.open();
                                                String getresult = "";
                                                getresult = objdatabaseadapter.UpdateReceiptFlag(gettransactionno);
                                                if (getresult.equals("success")) {
                                                    Toast toast = Toast.makeText(getApplicationContext(),"Cancelled Successfully", Toast.LENGTH_LONG);
                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                    toast.show();
                                                    //Toast.makeText(getApplicationContext(), "Cancelled Successfully", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                    GetReceiptList();
                                                }
                                                networkstate = isNetworkAvailable();
                                                if (networkstate == true) {
                                                    new AsyncCancelReceiptDetails().execute();
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
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }else{
                            Toast toast = Toast.makeText(getApplicationContext(),"Receipt already cancelled", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                           // Toast.makeText(getApplicationContext(), "Receipt already cancelled", Toast.LENGTH_SHORT).show();

                        }
                        break;

                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });

        //Get Receipt List
        GetReceiptList();


        //add receipt
        addreceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{

                    DataBaseAdapter objdatabaseadapter1 = null;

                    //Get Schdule
                    objdatabaseadapter1 = new DataBaseAdapter(context);
                    objdatabaseadapter1.open();
                    String todayschedulecount = objdatabaseadapter1.gettodayschedulecount();
                    if (todayschedulecount.equals("0")) {
                        try {

                            ScheduleActivity.getschedulecount = objdatabaseadapter1.GetScheduleCount();
                            preferenceMangr.pref_putString("schedule_getschedulecount",objdatabaseadapter1.GetScheduleCount());

                            String getschedulecode = objdatabaseadapter1.GetScheduleCode();
                            //Get Cash close Count
                            ScheduleActivity.getcashclosecount = objdatabaseadapter1.GetCashClose(getschedulecode);
                            preferenceMangr.pref_putString("schedule_getcashclosecount",objdatabaseadapter1.GetCashClose(getschedulecode));

                            //Get sales close Count
                            ScheduleActivity.getsalesclosecount = objdatabaseadapter1.GetSalesClose(getschedulecode);
                            preferenceMangr.pref_putString("schedule_getsalesclosecount",objdatabaseadapter1.GetSalesClose(getschedulecode));

                            MenuActivity.getsalesclosecount = objdatabaseadapter1.GetSalesClose(getschedulecode);
                            preferenceMangr.pref_putString("getsalesclosecount",objdatabaseadapter1.GetSalesClose(getschedulecode));

                            ScheduleActivity.getreciptcount = objdatabaseadapter1.GetReceiptCount(getschedulecode);
                            preferenceMangr.pref_putString("schedule_getreciptcount",objdatabaseadapter1.GetReceiptCount(getschedulecode));

                            ScheduleActivity.getexpensecount = objdatabaseadapter1.GetExpenseCount(getschedulecode);
                            preferenceMangr.pref_putString("schedule_getexpensecount",objdatabaseadapter1.GetExpenseCount(getschedulecode));

                            String lastScheduleDate = objdatabaseadapter1.getLastScheduleDate(getschedulecode);

                            if (preferenceMangr.pref_getString("getbusiness_type").equals("2")) {
                                if (!preferenceMangr.pref_getString("schedule_getschedulecount").equals("0") && !preferenceMangr.pref_getString("schedule_getschedulecount").equals("null") && !preferenceMangr.pref_getString("schedule_getschedulecount").equals(null)) {
                                    if (!preferenceMangr.pref_getString("schedule_getreciptcount").equals("0") || !preferenceMangr.pref_getString("schedule_getexpensecount").equals("0")) {

                                        if (!preferenceMangr.pref_getString("schedule_getcashclosecount").equals("0") && !preferenceMangr.pref_getString("schedule_getcashclosecount").equals("null") &&
                                                !preferenceMangr.pref_getString("schedule_getcashclosecount").equals("")) {
                                            /*bottomlayout.setVisibility(View.GONE);
                                            btnSaveSchedule.setVisibility(View.VISIBLE);*/

                                        } else {
                                            salescashclose = new Dialog(context);
                                            salescashclose.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                            salescashclose.setContentView(R.layout.salescashclose_popup);
                                            salescashclose.setCanceledOnTouchOutside(false);
                                            popup_salesclose = (TextView) salescashclose.findViewById(R.id.popup_salesclose);
                                            popup_cashclose = (TextView) salescashclose.findViewById(R.id.popup_cashclose);
                                            popup_salesclose_title = (TextView) salescashclose.findViewById(R.id.popup_salesclose_title);

                                            popup_salesclose.setVisibility(View.GONE);
                                            popup_cashclose.setText("Close Cash");
                                            if(!Utilities.isNullOrEmpty(lastScheduleDate))
                                                popup_salesclose_title.setText(getString(R.string.previous_schedule_close_popup_title) + " (" +lastScheduleDate + ")");

                                            popup_cashclose.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    LoginActivity.iscash = true;
                                                    Intent i = new Intent(ReceiptActivity.this, CashReportActivity.class);
                                                    startActivity(i);
                                                    salescashclose.dismiss();

                                                }
                                            });
                                            salescashclose.show();
                                            popup_salesclose.setVisibility(View.GONE);
                                        }
                                    } else {
                                        Intent i = new Intent(ReceiptActivity.this, ScheduleActivity.class);
                                        startActivity(i);
                                    }
                                } else {
                                    Intent i = new Intent(ReceiptActivity.this, ScheduleActivity.class);
                                    startActivity(i);
                                }
                            }else {

                                salescashclose = new Dialog(context);
                                salescashclose.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                salescashclose.setContentView(R.layout.salescashclose_popup);
                                salescashclose.setCanceledOnTouchOutside(false);
                                popup_salesclose = (TextView) salescashclose.findViewById(R.id.popup_salesclose);
                                popup_cashclose = (TextView) salescashclose.findViewById(R.id.popup_cashclose);
                                popup_salesclose_title = (TextView) salescashclose.findViewById(R.id.popup_salesclose_title);
                                if(!Utilities.isNullOrEmpty(lastScheduleDate))
                                    popup_salesclose_title.setText(getString(R.string.previous_schedule_close_popup_title) + " (" +lastScheduleDate + ")");

                                if (!preferenceMangr.pref_getString("schedule_getschedulecount").equals("0") && !preferenceMangr.pref_getString("schedule_getschedulecount").equals("null") && !preferenceMangr.pref_getString("schedule_getschedulecount").equals(null)) {
                                    if (!preferenceMangr.pref_getString("schedule_getsalesclosecount").equals("0") && !preferenceMangr.pref_getString("schedule_getsalesclosecount").equals("null") &&
                                            !preferenceMangr.pref_getString("schedule_getsalesclosecount").equals("") && !preferenceMangr.pref_getString("schedule_getsalesclosecount").equals(null) &&
                                            !preferenceMangr.pref_getString("schedule_getcashclosecount").equals("0") && !preferenceMangr.pref_getString("schedule_getcashclosecount").equals("null") &&
                                            !preferenceMangr.pref_getString("schedule_getcashclosecount").equals("") && !preferenceMangr.pref_getString("schedule_getcashclosecount").equals(null)) {
                            /*bottomlayout.setVisibility(View.GONE);
                            btnSaveSchedule.setVisibility(View.VISIBLE);*/
                                        Intent i = new Intent(ReceiptActivity.this, ScheduleActivity.class);
                                        startActivity(i);

                                    } else {
                                        salescashclose = new Dialog(context);
                                        salescashclose.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        salescashclose.setContentView(R.layout.salescashclose_popup);
                                        salescashclose.setCanceledOnTouchOutside(false);
                                        popup_salesclose = (TextView) salescashclose.findViewById(R.id.popup_salesclose);
                                        popup_cashclose = (TextView) salescashclose.findViewById(R.id.popup_cashclose);
                                        popup_salesclose_title = (TextView) salescashclose.findViewById(R.id.popup_salesclose_title);

                                        popup_salesclose.setText("Close Sales");
                                        popup_cashclose.setText("Close Cash");
                                        if(!Utilities.isNullOrEmpty(lastScheduleDate))
                                            popup_salesclose_title.setText(getString(R.string.previous_schedule_close_popup_title) + " (" +lastScheduleDate + ")");

                                        popup_salesclose.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                popup_salesclose();
                                            }
                                        });

                                        popup_cashclose.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                LoginActivity.iscash = true;
                                                Intent i = new Intent(ReceiptActivity.this, CashReportActivity.class);
                                                startActivity(i);
                                                salescashclose.dismiss();

                                            }
                                        });
                                        salescashclose.show();
                                    }
                                } else {
                                    Intent i = new Intent(ReceiptActivity.this, ScheduleActivity.class);
                                    startActivity(i);
                                }
                                if (!preferenceMangr.pref_getString("schedule_getsalesclosecount").equals("0") && !preferenceMangr.pref_getString("schedule_getsalesclosecount").equals("null") &&
                                        !preferenceMangr.pref_getString("schedule_getsalesclosecount").equals("") && !preferenceMangr.pref_getString("schedule_getsalesclosecount").equals(null)) {
                                    popup_salesclose.setText("Sales Closed");
                                    popup_salesclose.setBackgroundResource(R.drawable.editbackgroundgray);
                                    popup_salesclose.setEnabled(false);
                                }
                                if (!preferenceMangr.pref_getString("schedule_getcashclosecount").equals("0") && !preferenceMangr.pref_getString("schedule_getcashclosecount").equals("null") &&
                                        !preferenceMangr.pref_getString("schedule_getcashclosecount").equals("") && !preferenceMangr.pref_getString("schedule_getcashclosecount").equals(null)) {
                                    popup_cashclose.setText("Cash Closed");
                                    popup_cashclose.setBackgroundResource(R.drawable.editbackgroundgray);
                                    popup_cashclose.setEnabled(false);
                                }

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
                        }


                    } else{
                        if(!preferenceMangr.pref_getString("getcashclosecount").equals("0") && !preferenceMangr.pref_getString("getcashclosecount").equals("null") &&
                                !preferenceMangr.pref_getString("getcashclosecount").equals("")
                                && !preferenceMangr.pref_getString("getcashclosecount").equals(null)   ){
                            Toast toast = Toast.makeText(getApplicationContext(),"Cash Closed ", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            //Toast.makeText(getApplicationContext(), "Cash Closed ", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(!preferenceMangr.pref_getString("getfinanceyrcode").equals("") &&
                                !preferenceMangr.pref_getString("getfinanceyrcode").equals("0") &&
                                !preferenceMangr.pref_getString("getfinanceyrcode").equals("null")
                                && !(preferenceMangr.pref_getString("getfinanceyrcode").equals(null))) {

                            DataBaseAdapter objdatabaseadapter = null;
                            objdatabaseadapter = new DataBaseAdapter(context);
                            objdatabaseadapter.open();

                            Cursor getschedulelist = objdatabaseadapter.GetScheduleDB();
                            if(getschedulelist.getCount() >0){
                                for(int i=0;i<getschedulelist.getCount();i++) {
                                    MenuActivity.getschedulecode = getschedulelist.getString(0);
                                    preferenceMangr.pref_putString("getschedulecode",getschedulelist.getString(0));
                                }
                            }else{
                                MenuActivity.getschedulecode = "";
                                preferenceMangr.pref_putString("getschedulecode","");
                            }
                            if (!(preferenceMangr.pref_getString("getschedulecode").equals(""))
                                    && !(preferenceMangr.pref_getString("getschedulecode").equals(null))) {

                                String getvouchersettingscount = "0";
                                try {
                                    //Vocher settings

                                    getvouchersettingscount = objdatabaseadapter.GetReceiptVoucherSettingsDB();
                                    if (getvouchersettingscount.equals("0")) {
                                        OpenReceipt();
                                    } else {
                                        Toast toast = Toast.makeText(getApplicationContext(),"Receipt voucher settings not available for this van", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                        //Toast.makeText(getApplicationContext(), "Receipt voucher settings not available for this van", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                } catch (Exception e) {
                                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                    mDbErrHelper.open();
                                    String geterrror = e.toString();
                                    mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                    mDbErrHelper.close();
                                } finally {
                                    objdatabaseadapter.close();
                                }
                            } else {
                                Toast toast = Toast.makeText(getApplicationContext(),"No Schedule for today", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                //Toast.makeText(getApplicationContext(), "No Schedule for today ", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }else {
                            Toast toast = Toast.makeText(getApplicationContext(),"Receipt voucher settings not available for this van", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            //Toast.makeText(getApplicationContext(), "Receipt voucher settings not available for this van", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }catch (Exception e){
                    DataBaseAdapter mDbErrHelper2 = new DataBaseAdapter(context);
                    mDbErrHelper2.open();
                    mDbErrHelper2.insertErrorLog("Receipt Addsales : "+e.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper2.close();
                }


            }
        });

    }
    //Open receipt popup and saved successfully
    public void OpenReceipt(){
        Cursor mCur=null;
        try {
            pageflag=1;
            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.addreceipt);

            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);

            closepopup = (ImageView)dialog.findViewById(R.id.closepopup);
            txtreceiptdate=(TextView)dialog.findViewById(R.id.txtreceiptdate);
            txtcompanyname=(TextView)dialog.findViewById(R.id.txtcompanyname);
            txtareaname=(TextView)dialog.findViewById(R.id.txtareaname);
            edittxtamount=(EditText)dialog.findViewById(R.id.edittxtamount);
            edittxtchequerefno=(EditText) dialog.findViewById(R.id.edittxtchequerefno);
            btnsavereceipt=(Button) dialog.findViewById(R.id.btnsavereceipt);
            txtcustomername = (TextView)dialog.findViewById(R.id.txtcustomername);
            txtremarks = (TextView)dialog.findViewById(R.id.txtremarks);
            txtnote = (TextView)dialog.findViewById(R.id.txtnote);
            radio_cheque = (RadioButton)dialog.findViewById(R.id.radio_cheque);
            radio_cash = (RadioButton)dialog.findViewById(R.id.radio_cash);
            edittxtchequebankname=(EditText) dialog.findViewById(R.id.edittxtchequebankname);
            edittxtchequedate=(TextView) dialog.findViewById(R.id.edittxtchequedate);
            txtupitransactionID=(TextView)dialog.findViewById(R.id.txtupitransactionID);
            txtupivendername=(TextView)dialog.findViewById(R.id.txtupivendername);
            imageView=(ImageView)dialog.findViewById(R.id.imageView);
            radio_upi = (RadioButton)dialog.findViewById(R.id.radio_upi);
            LLChequeDetails = (LinearLayout)dialog.findViewById(R.id.LLChequeDetails);
            LLUPIDetails = (LinearLayout)dialog.findViewById(R.id.LLUPIDetails);
            LL2UPIDetails = (LinearLayout)dialog.findViewById(R.id.LL2UPIDetails);
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            txtnote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == txtnote.getId())
                    {
                        txtnote.setCursorVisible(true);
                    }
                }
            });
            txtreceiptdate.setText(preferenceMangr.pref_getString("getcurrentdatetime"));
            getreceipdate = preferenceMangr.pref_getString("getformatdate");

            radio_cash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    edittxtchequerefno.setVisibility(View.GONE);
                    edittxtchequebankname.setVisibility(View.GONE);
                    edittxtchequedate.setVisibility(View.GONE);
                    LLChequeDetails.setVisibility(View.GONE);
                    LLUPIDetails.setVisibility(View.GONE);
                    LL2UPIDetails.setVisibility(View.GONE);
                }
            });
            radio_cheque.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    edittxtchequerefno.setVisibility(View.VISIBLE);
                    edittxtchequebankname.setVisibility(View.VISIBLE);
                    edittxtchequedate.setVisibility(View.VISIBLE);
                    LLChequeDetails.setVisibility(View.VISIBLE);
                    LLUPIDetails.setVisibility(View.GONE);
                    LL2UPIDetails.setVisibility(View.GONE);
                }
            });

            radio_upi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    edittxtchequerefno.setVisibility(View.GONE);
                    edittxtchequebankname.setVisibility(View.GONE);
                    edittxtchequedate.setVisibility(View.GONE);
                    LLChequeDetails.setVisibility(View.GONE);
                    Log.d("getimageurl",getimageurl);

                    if (getimageurl.equals("") || getimageurl.equals("null")) {
                        LLUPIDetails.setVisibility(View.GONE);
                        LL2UPIDetails.setVisibility(View.GONE);
                    } else {
                        LLUPIDetails.setVisibility(View.VISIBLE);
                        LL2UPIDetails.setVisibility(View.VISIBLE);
                        Picasso.with(context)
                                .load(getimageurl)//optional
//                            .resize(200, 200)         //optional
//                            .centerCrop()                        //optional
                                .into(imageView);
                    }
//                    Glide.with(ReceiptActivity.this).load(getimageurl).into(imageView);

                }
            });

            txtareaname.setText(LoginActivity.getareaname);


            btnsavereceipt.setEnabled(true);
            btnsavereceipt.setText("Save");

            txtcompanyname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GetCompanyName();
                }
            });

            txtupivendername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GetPaymentVenderName();
                }
            });

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
            txtremarks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GetReceiptRemarks();
                }
            });

            edittxtamount.addTextChangedListener(new TextWatcher() {

                public void onTextChanged(CharSequence s, int start, int before,
                                          int count) {
                }
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                }
                public void afterTextChanged(Editable s) {
                    String getreceiptamt = "0";
                    if(edittxtamount.getText().toString().equals("")){
                        getreceiptamt = "0";
                    }else{
                        getreceiptamt = edittxtamount.getText().toString();
                    }

                }
            });

            closepopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pageflag=0;
                    dialog.dismiss(); } });

            edittxtchequedate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            String vardate = "";
                            String getmonth="";
                            String getdate="";
                            monthOfYear = monthOfYear + 1;
                            if (dayOfMonth < 10) {
                                vardate = "0" + dayOfMonth;
                                getdate = "0" + dayOfMonth;
                            } else {
                                vardate = String.valueOf(dayOfMonth);
                                getdate = String.valueOf(dayOfMonth);
                            }
                            if (monthOfYear < 10) {
                                vardate = vardate + "-" + "0" + monthOfYear;
                                getmonth = "0" + monthOfYear;;
                            } else {
                                vardate = vardate +"-" + monthOfYear;
                                getmonth = String.valueOf(monthOfYear);;
                            }
                            vardate = vardate + "-" + year;
                            textchequedate = year+ "-"+getmonth+"-"+getdate;
                            edittxtchequedate.setText(vardate );
                        }
                    };
                    DatePickerDialog dpDialog = new DatePickerDialog(context, listener, year, month, day);
                    dpDialog.show();
                }
            });
            //Save Functionality
            btnsavereceipt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataBaseAdapter objdatabaseadapter = null;
                    try {
                        String getreceipttransano="";
                        String receiptdate=preferenceMangr.pref_getString("getformatdate");
                        String companyname= txtcompanyname.getText().toString();
                        String companycode=getcompanycode;
                        String vancode=preferenceMangr.pref_getString("getvancode");
                        String customercode=getcustomercode;
                        String customername= txtcustomername.getText().toString();
                        String areaname= txtareaname.getText().toString();
                        String schedulecode=preferenceMangr.pref_getString("getschedulecode");
                        String receiptremarkscode=getremarkscode;
                        String remarks = txtremarks.getText().toString();
                        String upivendername= txtupivendername.getText().toString();
                        String upitransactionID=txtupitransactionID.getText().toString();
                        String venderid = getpaymentvenderID;
                        String receiptmode="";
                        if(radio_cash.isChecked()){
                            receiptmode = "Cash";
                        }else if(radio_cheque.isChecked()){
                            receiptmode = "Cheque";
                        }else {
                            receiptmode = "UPI";
                        }
                        String chequerefno=edittxtchequerefno.getText().toString();
                        String chequebankname=edittxtchequebankname.getText().toString();
                        String chequedate=edittxtchequedate.getText().toString();
                        if(receiptmode.equals("Cash")){
                            chequerefno = "";
                            chequebankname = "";
                            chequedate = null;
                        }
                        String amount=edittxtamount.getText().toString();
                        final String financialyearcode=preferenceMangr.pref_getString("getfinanceyrcode");
                        String note=txtnote.getText().toString();

                        if(companycode.equals("") || companycode.equals(null)
                                || companyname.equals("") ||  companyname.equals(null)){
                            Toast toast = Toast.makeText(getApplicationContext(),"Please select company name", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            //Toast.makeText(getApplicationContext(),"Please select company name",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(customercode.equals("") || customercode.equals(null)
                                || customername.equals("") ||  customername.equals(null)){
                            Toast toast = Toast.makeText(getApplicationContext(),"Please select customer name", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            //Toast.makeText(getApplicationContext(),"Please select customer name",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(LoginActivity.getareacode.equals("") || LoginActivity.getareacode.equals(null)
                                || areaname.equals("") ||  areaname.equals(null)){
                            Toast toast = Toast.makeText(getApplicationContext(),"Please select area name", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            //Toast.makeText(getApplicationContext(),"Please select area name",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(customername.equals("") || customername.equals(null)
                                || customername.equals("") ||  customername.equals(null)){
                            Toast toast = Toast.makeText(getApplicationContext(),"Please select customer name", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            //Toast.makeText(getApplicationContext(),"Please select customer name",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(remarks.equals("") || remarks.equals(null)
                                || remarks.equals("") ||  remarks.equals(null)){
                            Toast toast = Toast.makeText(getApplicationContext(),"Please select remarks", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                           // Toast.makeText(getApplicationContext(),"Please select remarks",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(amount.equals("") || amount.equals(null)
                                || amount.equals("null") || amount.equals(".") ){
                            Toast toast = Toast.makeText(getApplicationContext(),"Please enter amount", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            //Toast.makeText(getApplicationContext(),"Please enter amount",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(Double.parseDouble(amount)<=0){
                            Toast toast = Toast.makeText(getApplicationContext(),"Please enter valid amount", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            //Toast.makeText(getApplicationContext(),"Please enter valid amount",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(receiptmode.equals("Cheque")) {
                            if (chequerefno.equals("") || chequerefno.equals(null)) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Please enter Cheque No.", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                //Toast.makeText(getApplicationContext(),"Please enter valid amount",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (chequebankname.equals("") || chequebankname.equals(null)) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Please enter Bank Name", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                //Toast.makeText(getApplicationContext(),"Please enter valid amount",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (chequedate.equals("") || chequedate.equals(null)) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Please enter Cheque Date", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                //Toast.makeText(getApplicationContext(),"Please enter valid amount",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        if(receiptmode.equals("UPI")) {
                            if(venderid.equals("") || venderid.equals(null)
                                    || upivendername.equals("") ||  upivendername.equals(null)){
                                Toast toast = Toast.makeText(getApplicationContext(),"Please select payment vender name", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                //Toast.makeText(getApplicationContext(),"Please select company name",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (upitransactionID.equals("") || upitransactionID.equals(null)) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Please enter Transaction ID", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                //Toast.makeText(getApplicationContext(),"Please enter valid amount",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        //Save receipt details
                        objdatabaseadapter = new DataBaseAdapter(context);
                        objdatabaseadapter.open();
                        getreceipttransano=objdatabaseadapter.insertReceipt(receiptdate,companycode,vancode,
                                customercode,schedulecode,receiptremarkscode,receiptmode,
                                chequerefno,amount,financialyearcode,note,chequebankname,
                                chequedate,venderid,upitransactionID);
                        if(!getreceipttransano.equals("") && !getreceipttransano.equals("null")
                            && !getreceipttransano.equals(null) && !getreceipttransano.equals("0")){
                            Toast toast = Toast.makeText(getApplicationContext(),"Saved Successfully", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.BOTTOM, 0, 150);
                            toast.show();
                            receiptTransano = getreceipttransano;
                            pageflag=0;
                           // Toast.makeText(getApplicationContext(),"Saved Successfully",Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                            // Send sms for receipt
                            networkstate = isNetworkAvailable();
                            if (networkstate == true) {
                                sendSMSForReceipt(getcustomercode, receiptTransano, receiptmode, upitransactionID);
                            }

                            printpopup = new Dialog(context);
                            printpopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            printpopup.setContentView(R.layout.printpopup);
                            final String finalGetreceipttransano = getreceipttransano;
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

                                           *//* boolean billPrinted = false;
                                            billPrinted = (boolean) printData.GetReceiptBillPrint(finalGetreceipttransano,financialyearcode);
                                            if (!billPrinted) {
                                                Toast toast = Toast.makeText(getApplicationContext(), "Unable to connect to Bluetooth Printer!", Toast.LENGTH_LONG);
                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                printpopup.dismiss();
                                                toast.show();
                                            }
                                            printpopup.dismiss();*//*

                                        }*/

                                        if(mBluetoothAdapter != null) {

                                            if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                                                if(!preferenceMangr.pref_getString("SelectedPrinterAddress").equals("")) {
                                                    new AsyncPrintReceiptDetails().execute(finalGetreceipttransano,financialyearcode);
                                                    printpopup.dismiss();
                                                }else{
                                                    Toast toast = Toast.makeText(getApplicationContext(), "Please select the bluetooth printer in app", Toast.LENGTH_LONG);
                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                    toast.show();
                                                    printpopup.dismiss();
                                                }
                                            }else{
                                                Toast toast = Toast.makeText(getApplicationContext(), "Please turn on the bluetooth", Toast.LENGTH_LONG);
                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                toast.show();
                                                printpopup.dismiss();
                                            }
                                        }else{
                                            Toast toast = Toast.makeText(getApplicationContext(), "Bluetooth is not available.", Toast.LENGTH_LONG);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
                                            printpopup.dismiss();
                                        }
                                    }
                                    catch (Exception e){
                                        Toast toast = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
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
                            /*android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                            String message = "Are you sure you want to print?";
                            final String finalGetreceipttransano = getreceipttransano;
                            builder.setMessage(message)
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
                                                    Toast toast = Toast.makeText(getApplicationContext(),"Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                    toast.show();
                                                    //Toast.makeText(context, "Please connect to the Bluetooth Printer!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    boolean billPrinted = false;
                                                   billPrinted = (boolean) printData.GetReceiptBillPrint(finalGetreceipttransano,financialyearcode);
                                                    if (!billPrinted) {
                                                        Toast toast = Toast.makeText(getApplicationContext(),"Unable to connect to Bluetooth Printer!", Toast.LENGTH_LONG);
                                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                                        toast.show();
                                                       // Toast.makeText(context, "Unable to connect to Bluetooth Printer!", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }
                                                }
                                            }
                                            catch (Exception e){
                                                Toast toast = Toast.makeText(getApplicationContext(),"Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                toast.show();
                                                //Toast.makeText(context, "Please connect to the Bluetooth Printer!", Toast.LENGTH_SHORT).show();
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
                                            dialog1.cancel();;
                                        }
                                    }).show();*/

                            GetReceiptList();
                        }
                        networkstate = isNetworkAvailable();
                        if (networkstate == true) {
                            new AsyncReceiptDetails().execute();
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


            dialog.show();
        }catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("AsyncSync", e.getMessage());
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
        finally {
            // this gets called even if there is an exception somewhere above
            if(mCur != null)
                mCur.close();
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
    /*****DROP DOWN FUNCTIONALITY**********/
    //Get Company name
    public  void GetCompanyName(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetCompanyNAmeDB();
            if(Cur.getCount()>0) {
                companycode = new String[Cur.getCount()];
                companyname = new String[Cur.getCount()];
                shortname = new String[Cur.getCount()];
                imageurl = new String[Cur.getCount()];

                for(int i=0;i<Cur.getCount();i++){
                    companycode[i] = Cur.getString(0);
                    companyname[i] = Cur.getString(1);
                    shortname[i] = Cur.getString(1);
                    imageurl[i] = Cur.getString(3);
                    Cur.moveToNext();
                }

                companydialog = new Dialog(context);
                companydialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                companydialog.setContentView(R.layout.companypopup);
                lv_CompanyList = (ListView) companydialog.findViewById(R.id.lv_CompanyList);
                ImageView close = (ImageView) companydialog.findViewById(R.id.close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        companydialog.dismiss();
                    }
                });
                CompanyAdapter adapter = new CompanyAdapter(context);
                lv_CompanyList.setAdapter(adapter);
                companydialog.show();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(),"No Area in this route", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
              //  Toast.makeText(getApplicationContext(),"No Area in this route",Toast.LENGTH_SHORT).show();
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

    public  void GetPaymentVenderName(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetPaymentVenderNAmeDB();
            if(Cur.getCount()>0) {
                venderid = new String[Cur.getCount()];
                vendername = new String[Cur.getCount()];
                for(int i=0;i<Cur.getCount();i++){
                    venderid[i] = Cur.getString(0);
                    vendername[i] = Cur.getString(1);
                    Cur.moveToNext();
                }

                upipaymentvenderdialog = new Dialog(context);
                upipaymentvenderdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                upipaymentvenderdialog.setContentView(R.layout.paymenttypepopup);
                lv_UPIPaymentList = (ListView) upipaymentvenderdialog.findViewById(R.id.lv_UPIPaymentList);
                ImageView close = (ImageView) upipaymentvenderdialog.findViewById(R.id.close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        upipaymentvenderdialog.dismiss();
                    }
                });
                UPIPaymentAdapter adapter = new UPIPaymentAdapter(context);
                lv_UPIPaymentList.setAdapter(adapter);
                upipaymentvenderdialog.show();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(),"No Area in this route", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //  Toast.makeText(getApplicationContext(),"No Area in this route",Toast.LENGTH_SHORT).show();
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

    //Get Area name
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
                if(routeflag == 1 )
                {
                    AreaAdapter adapter = new AreaAdapter(context);
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

    //Get customer name
    public  void GetCustomer(String areacode){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetCustomerDB(areacode,Constants.CUSTOMER_CATEGORY_RECEIPT);
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
                Customerbillcount = new String[Cur.getCount()];
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
                    Customerbillcount[i]=Cur.getString(15);
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
                CustomerAdapter adapter = new CustomerAdapter(context);
                lv_CustomerList.setAdapter(adapter);
                customerdialog.show();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(),"No Customer in this area", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
               // Toast.makeText(getApplicationContext(),"No Customer in this area",Toast.LENGTH_SHORT).show();
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

    //Get Area name
    public  void GetReceiptRemarks(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetReceiptRemarks();
            if(Cur.getCount()>0) {
                receiptremarks = new String[Cur.getCount()];
                receiptremarkscode = new String[Cur.getCount()];
                for(int i=0;i<Cur.getCount();i++){
                    receiptremarks[i] = Cur.getString(0);
                    receiptremarkscode[i] = Cur.getString(1);
                    Cur.moveToNext();
                }

                remarksdialog = new Dialog(context);
                remarksdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                remarksdialog.setContentView(R.layout.remarkspopup);
                lv_RemarksList = (ListView) remarksdialog.findViewById(R.id.lv_RemarksList);
                ImageView close = (ImageView) remarksdialog.findViewById(R.id.close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        remarksdialog.dismiss();
                    }
                });
                RemarksAdapter adapter = new RemarksAdapter(context);
                lv_RemarksList.setAdapter(adapter);
                remarksdialog.show();
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

    //Get company List
    public  void GetListCompanyName(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetCompanyDB();
            if(Cur.getCount()>0) {
                companycode = new String[Cur.getCount()];
                companyname = new String[Cur.getCount()];
                shortname = new String[Cur.getCount()];
                for(int i=0;i<Cur.getCount();i++){
                    companycode[i] = Cur.getString(0);
                    companyname[i] = Cur.getString(1);
                    shortname[i] = Cur.getString(2);
                    Cur.moveToNext();
                }

                companydialog = new Dialog(context);
                companydialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                companydialog.setContentView(R.layout.companypopup);
                lv_CompanyList = (ListView) companydialog.findViewById(R.id.lv_CompanyList);
                ImageView close = (ImageView) companydialog.findViewById(R.id.close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        companydialog.dismiss();
                    }
                });
                CompanyListAdapter adapter = new CompanyListAdapter(context);
                lv_CompanyList.setAdapter(adapter);
                companydialog.show();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(),"No Area in this route", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
               // Toast.makeText(getApplicationContext(),"No Area in this route",Toast.LENGTH_SHORT).show();
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

    //Get Area name
    public  void GetListArea(String routecode){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetReceiptAreaDB(routecode);
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
                    AreaListAdapter adapter = new AreaListAdapter(context);
                    lv_AreaListAdapter.setAdapter(adapter);
                    return;
                }
                areadialog = new Dialog(context);
                areadialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                areadialog.setContentView(R.layout.areapopup);
                lv_AreaListAdapter = (ListView) areadialog.findViewById(R.id.lv_AreaList);
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
                            lv_AreaListAdapter.setAdapter(null);
                        }
                        routeflag = 1;
                        GetListArea(getscheduleroutecode);
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
                        GetListArea(preferenceMangr.pref_getString("getroutecode"));

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
                AreaListAdapter adapter = new AreaListAdapter(context);
                lv_AreaListAdapter.setAdapter(adapter);
                areadialog.show();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(),"No Area in this route", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
              //  Toast.makeText(getApplicationContext(),"No Area in this route",Toast.LENGTH_SHORT).show();
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
    /*****************DROP DOWN**************/
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

    /************BASE ADAPTER*************/
    //Company Adapter
    public class CompanyAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;

        CompanyAdapter(Context c) {
            context = c;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return companycode.length;
        }

        @Override
        public Object getItem(int position) {
            return companycode[position];
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
                convertView = layoutInflater.inflate(R.layout.companypopuplist, parent, false);
                mHolder = new ViewHolder();
                try {
                    mHolder.listcompanyname = (TextView) convertView.findViewById(R.id.listcompanyname);
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
                mHolder.listcompanyname.setText(String.valueOf(shortname[position]));
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
                    txtcompanyname.setText(String.valueOf(shortname[position]));
                    getcompanycode = companycode[position];
                    getimageurl = imageurl[position];
                    LLUPIDetails.setVisibility(View.GONE);
                    LL2UPIDetails.setVisibility(View.GONE);
                    LLChequeDetails.setVisibility(View.GONE);
                    radio_cheque.setChecked(false);
                    radio_cash.setChecked(true);
                    radio_upi.setChecked(false);
                    if (getimageurl.equals("") || getimageurl.equals("null")) {
                        radio_upi.setVisibility(View.INVISIBLE);
//                        radio_upi.setEnabled(false);
//                        radio_upi.setBackgroundColor(getResources().getColor(R.color.graycolor));
                    } else {
                        radio_upi.setVisibility(View.VISIBLE);
//                        radio_upi.setEnabled(true);
//                        radio_upi.setBackgroundColor();
                    }
                    companydialog.dismiss();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView listcompanyname;

        }

    }

    public class UPIPaymentAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;

        UPIPaymentAdapter(Context c) {
            context = c;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return venderid.length;
        }

        @Override
        public Object getItem(int position) {
            return venderid[position];
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
                convertView = layoutInflater.inflate(R.layout.paymentvenderpopuplist, parent, false);
                mHolder = new ViewHolder();
                try {
                    mHolder.listvendername = (TextView) convertView.findViewById(R.id.listvendername);
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
                mHolder.listvendername.setText(String.valueOf(vendername[position]));
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
                    txtupivendername.setText(String.valueOf(vendername[position]));
                    getpaymentvenderID = venderid[position];
                    upipaymentvenderdialog.dismiss();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView listvendername;

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
                    txtareaname.setText(String.valueOf(AreaName[position] +" - "+CityName[position]));
                    LoginActivity.getareaname = String.valueOf(AreaName[position] +" - "+CityName[position]);
                    LoginActivity.getareacode = String.valueOf(AreaCode[position]);
                    txtcustomername.setText("");
                    getcustomercode="0";
                    getgstnnumber="0";
                    txtcustomername.setHint("Customer Name");
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
    public class CustomerAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;

        CustomerAdapter(Context c) {
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
                    mHolder.LLL = (LinearLayout)convertView.findViewById(R.id.LLL);
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
//                    mHolder.listcustomertype.setTextColor(getResources().getColor(R.color.black));
                    mHolder.listcustomertype.setTextColor(getResources().getColor(R.color.white));

                }else if(customertypecode[position].equals("2")){
                    mHolder.listcustomertype.setText("CR");
//                    mHolder.listcustomertype.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    mHolder.listcustomertype.setTextColor(getResources().getColor(R.color.white));

                }
                if(Utilities.isNullOrEmpty(Customerbillcount[position]) || !Customerbillcount[position].equals("0")) {
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
                    getcustomercode = CustomerCode[position];
                    getgstnnumber = GSTN[position];
                    customerdialog.dismiss();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView listcustomername,listgstin,listcustomertype;
            private LinearLayout LLL;

        }

    }

    //Receiptremarks Adapter
    public class RemarksAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;

        RemarksAdapter(Context c) {
            context = c;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return receiptremarkscode.length;
        }

        @Override
        public Object getItem(int position) {
            return receiptremarkscode[position];
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
                convertView = layoutInflater.inflate(R.layout.remarkspopuplist, parent, false);
                mHolder = new ViewHolder();
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
                mHolder = (ViewHolder) convertView.getTag();
            }
            try {
                mHolder.listreceiptname.setText(String.valueOf(receiptremarks[position] ));
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
                    txtremarks.setText(String.valueOf(receiptremarks[position]));
                    getremarkscode = receiptremarkscode[position];
                    remarksdialog.dismiss();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView listreceiptname;

        }

    }

    //Company adapter
    public class CompanyListAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;

        CompanyListAdapter(Context c) {
            context = c;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return companycode.length;
        }

        @Override
        public Object getItem(int position) {
            return companycode[position];
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
                convertView = layoutInflater.inflate(R.layout.companypopuplist, parent, false);
                mHolder = new ViewHolder();
                try {
                    mHolder.listcompanyname = (TextView) convertView.findViewById(R.id.listcompanyname);
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
                mHolder.listcompanyname.setText(String.valueOf(shortname[position]));
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
                    receiptcompany.setText(String.valueOf(shortname[position]));
                    getlistcompanycode = companycode[position];
                    companydialog.dismiss();
                    GetReceiptList();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView listcompanyname;

        }

    }

    //Area List Adapter
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
                    mHolder.listareaname.setText(String.valueOf(AreaName[position]));
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
                    receiptarea.setText(String.valueOf(AreaName[position] +" - "+CityName[position]));
                    getlistareacode = AreaCode[position];
                    GetReceiptList();
                    routeflag=0;
                    areadialog.dismiss();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView listareaname,listcustomercount;

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
    /************END BASE ADAPTER*************/
    public void GetReceiptList()
    {
        try{
            receiptlist.clear();
            DataBaseAdapter objcustomerAdaptor = new DataBaseAdapter(context);
            objcustomerAdaptor.open();
            Cursor mCur = objcustomerAdaptor.GetReceiptListDB(getreceiptlistdate,getlistcompanycode,getlistareacode);
            objcustomerAdaptor.close();
            if(mCur.getCount() >0) {
                for(int i=0;i<mCur.getCount();i++)
                {
                    receiptlist.add(new ReceiptDetails(mCur.getString(0),mCur.getString(1),
                            mCur.getString(2),mCur.getString(3),mCur.getString(4),
                            mCur.getString(5),
                            mCur.getString(6),mCur.getString(7),mCur.getString(8),
                            mCur.getString(9),
                            mCur.getString(10),mCur.getString(11),mCur.getString(12),
                            mCur.getString(13),mCur.getString(14),mCur.getString(15),
                            mCur.getString(16),mCur.getString(17),mCur.getString(18)
                            ,String.valueOf(i+1)));
                    mCur.moveToNext();
                }
                getdata = receiptlist;

                ReceiptListBaseAdapter adapter = new ReceiptListBaseAdapter(context,receiptlist);
                receiptlistView.setAdapter(adapter);
            }else{
                ReceiptListBaseAdapter adapter = new ReceiptListBaseAdapter(context,receiptlist);
                receiptlistView.setAdapter(adapter);
                ReceiptActivity.receiptsubtotalamt.setText("\u20B9 0.00");
                ReceiptActivity.cashtotalamt.setText("\u20B9 0.00 ");
                ReceiptActivity.chequetotalamt.setText("\u20B9 0.00 ");
                ReceiptActivity.upitotalamt.setText("\u20B9 0.00 ");
                Toast toast = Toast.makeText(getApplicationContext(),"No Receipt Available", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
               // Toast.makeText(context, "No Receipt Available", Toast.LENGTH_SHORT).show();
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
    }

    /**********Asynchronous Claass***************/

    protected  class AsyncReceiptDetails extends
            AsyncTask<String, JSONObject, ArrayList<ReceiptTransactionDetails>> {
        ArrayList<ReceiptTransactionDetails> List = null;
        JSONObject jsonObj = null;
        @Override
        protected  ArrayList<ReceiptTransactionDetails> doInBackground(String... params) {
            RestAPI api = new RestAPI();
            String result = "";
            try {
                JSONObject js_obj = new JSONObject();
                try {
                    DataBaseAdapter dbadapter = new DataBaseAdapter(context);
                    dbadapter.open();
                    Cursor mCur2 = dbadapter.GetReceiptDetailsDatasDB();
                    JSONArray js_array2 = new JSONArray();
                    for (int i = 0; i < mCur2.getCount(); i++) {
                        JSONObject obj = new JSONObject();
                        obj.put("autonum", mCur2.getString(0));
                        obj.put("transactionno", mCur2.getString(1));
                        obj.put("receiptdate", mCur2.getString(2));
                        obj.put("prefix", mCur2.getString(3));
                        obj.put("suffix", mCur2.getString(4));
                        obj.put("voucherno", mCur2.getString(5));
                        obj.put("refno", mCur2.getString(6));
                        obj.put("companycode", mCur2.getString(7));
                        obj.put("vancode", mCur2.getString(8));
                        obj.put("customercode", mCur2.getString(9));
                        obj.put("schedulecode", mCur2.getString(10));
                        obj.put("receiptremarkscode", mCur2.getString(11));
                        obj.put("receiptmode", mCur2.getString(12));
                        obj.put("chequerefno", mCur2.getString(13));
                        obj.put("amount", mCur2.getString(14));
                        obj.put("makerid", mCur2.getString(15));
                        obj.put("createddate", mCur2.getString(16));
                        obj.put("financialyearcode", mCur2.getString(17));
                        obj.put("flag", mCur2.getString(18));
                        obj.put("note", mCur2.getString(19));
                        obj.put("receipttime", mCur2.getString(21));
                        obj.put("chequebankname", mCur2.getString(22));
                        obj.put("chequedate", mCur2.getString(23));
                        obj.put("transactionid", mCur2.getString(24));
                        obj.put("venderid", mCur2.getString(25));
                        obj.put("type", mCur2.getString(26));

                        js_array2.put(obj);
                        mCur2.moveToNext();
                    }
                    js_obj.put("JSonObject", js_array2);

                    jsonObj =  api.ReceiptDetails(js_obj.toString(),context);
                    //Call Json parser functionality
                    JSONParser parser = new JSONParser();
                    //parse the json object to boolean
                    List = parser.parseReceiptDataList(jsonObj);
                    dbadapter.close();
                }
                catch (Exception e)
                {
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName()+ "Async Receipt Details", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
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
        protected void onPostExecute(ArrayList<ReceiptTransactionDetails> result) {
            // TODO Auto-generated method stub
            if (result.size() >= 1) {
                if(result.get(0).TransactionNo.length>0){
                    for(int j=0;j<result.get(0).TransactionNo.length;j++){
                        DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
                        dataBaseAdapter.open();
                        dataBaseAdapter.UpdateReceiptDetailsFlag(result.get(0).TransactionNo[j]);
                        dataBaseAdapter.close();
                    }
                }

            }

        }
    }

    protected  class AsyncCancelReceiptDetails extends
            AsyncTask<String, JSONObject, ArrayList<ReceiptTransactionDetails>> {
        ArrayList<ReceiptTransactionDetails> List = null;
        JSONObject jsonObj = null;
        @Override
        protected  ArrayList<ReceiptTransactionDetails> doInBackground(String... params) {
            RestAPI api = new RestAPI();
            String result = "";
            try {
                JSONObject js_obj = new JSONObject();
                try {
                    DataBaseAdapter dbadapter = new DataBaseAdapter(context);
                    dbadapter.open();
                    Cursor mCur2 = dbadapter.GetCancelReceiptDetailsDatasDB();
                    JSONArray js_array2 = new JSONArray();
                    for (int i = 0; i < mCur2.getCount(); i++) {
                        JSONObject obj = new JSONObject();
                        obj.put("autonum", mCur2.getString(0));
                        obj.put("transactionno", mCur2.getString(1));
                        obj.put("receiptdate", mCur2.getString(2));
                        obj.put("prefix", mCur2.getString(3));
                        obj.put("suffix", mCur2.getString(4));
                        obj.put("voucherno", mCur2.getString(5));
                        obj.put("refno", mCur2.getString(6));
                        obj.put("companycode", mCur2.getString(7));
                        obj.put("vancode", mCur2.getString(8));
                        obj.put("customercode", mCur2.getString(9));
                        obj.put("schedulecode", mCur2.getString(10));
                        obj.put("receiptremarkscode", mCur2.getString(11));
                        obj.put("receiptmode", mCur2.getString(12));
                        obj.put("chequerefno", mCur2.getString(13));
                        obj.put("amount", mCur2.getDouble(14));
                        obj.put("makerid", mCur2.getString(15));
                        obj.put("createddate", mCur2.getString(16));
                        obj.put("financialyearcode", mCur2.getString(17));
                        obj.put("flag", mCur2.getString(18));
                        obj.put("note", mCur2.getString(19));
                        js_array2.put(obj);
                        mCur2.moveToNext();
                    }
                    js_obj.put("JSonObject", js_array2);

                    jsonObj =  api.CancelREciptDetails(js_obj.toString(),context);
                    //Call Json parser functionality
                    JSONParser parser = new JSONParser();
                    //parse the json object to boolean
                    List = parser.parseReceiptDataList(jsonObj);
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
        protected void onPostExecute(ArrayList<ReceiptTransactionDetails> result) {
            // TODO Auto-generated method stub
            try {
                if (result.size() >= 1) {
                    if (result.get(0).TransactionNo.length > 0) {
                        for (int j = 0; j < result.get(0).TransactionNo.length; j++) {
                            DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
                            dataBaseAdapter.open();
                            dataBaseAdapter.UpdateCancelReceiptFlag(result.get(0).TransactionNo[j]);
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


    public void goBack(View v) {
        LoginActivity.ismenuopen=true;
        Intent i = new Intent(context, MenuActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        goBack(null);
    }

    //Set date
    private void showmainDate(int year, int monthOfYear, int dayOfMonth) {
        String vardate = "";
        String getmonth="";
        String getdate="";

        if (dayOfMonth < 10) {
            vardate = "0" + dayOfMonth;
            getdate = "0" + dayOfMonth;
        } else {
            vardate = String.valueOf(dayOfMonth);
            getdate = String.valueOf(dayOfMonth);
        }
        if (monthOfYear < 10) {
            vardate = vardate + "-" + "0" + monthOfYear;
            getmonth = "0" + monthOfYear;;
        } else {
            vardate = vardate +"-" + monthOfYear;
            getmonth = String.valueOf(monthOfYear);;
        }
        vardate = vardate + "-" + year;
        getreceiptlistdate = year +"-"+getmonth+"-"+getdate ;
        txtreceiptlistdate.setText(vardate);

    }

    protected class AsyncPrintReceiptDetails extends
            AsyncTask<String, JSONObject, Boolean> {
        Boolean billPrinted = false;
        JSONObject jsonObj = null;
        ProgressDialog loading;
        @Override
        protected Boolean doInBackground(String... params) {
            final String finalGetreceipttransano = params[0];
            final String financialyearcode = params[1];
            try {
                EpsonT20Printer epsonT20Printer = new EpsonT20Printer(ReceiptActivity.this, ReceiptActivity.this, receiveListener);
                deviceFound = epsonT20Printer.findBT();
                Log.d("deviceFound",String.valueOf(deviceFound));
                if (!deviceFound) {
                    /*Toast toast = Toast.makeText(getApplicationContext(), "Please connect to the Bluetooth Printer!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);*/
                    //printpopup.dismiss();
                    billPrinted = deviceFound;
                    //toast.show();
                } else {
                    billPrinted = (boolean) epsonT20Printer.GetReceiptBillPrint(finalGetreceipttransano, financialyearcode,ReceiptActivity.this);
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
                if (!billPrinted) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Unable to connect Bluetooth Printer. Please check the printer is turn or or not!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    //printpopup.dismiss();
                    toast.show();
                    //printData = null;

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


    public void popup_salesclose(){
        String popupmessage="Do you want to close sales?";
        DataBaseAdapter objdatabaseadapter = null;
        objdatabaseadapter = new DataBaseAdapter(context);

        String deviceid = preferenceMangr.pref_getString("deviceid");
        String result = "";
        try{
            getUPIPaymentPopUp("closesalespopup");
            GetFreeItemexcess();
            networkstate = isNetworkAvailable();
            if (networkstate == true) {
                objdatabaseadapter.open();
                String getschedulecode = objdatabaseadapter.GetScheduleCode();
                result = Utilities.getDeliveryNotePendingCount(deviceid, getschedulecode, context);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Please check internet connection", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            String geteinvoicependingbills = objdatabaseadapter.Checkeinvoicepending();
            if(geteinvoicependingbills!=null && !geteinvoicependingbills.equals("0")){
                popupmessage="You have "+geteinvoicependingbills+" GSTIN invoice(s) for which e-Invoice is " +
                        "not yet generated. \nAre you sure want to close sales?";
            }

        } catch (Exception e) {
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "),
                    this.getClass().getSimpleName()+" - Check e-invoice pending", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        } finally {
            if (objdatabaseadapter != null)
                objdatabaseadapter.close();
        }
        if (!result.equals("0")) {
            Utilities.CheckDeliveryNoteDialog(context);
        } else {
            if(issalesclosepopup) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirmation");
                // builder.setIcon(R.mipmap.ic_vanluncher);
                builder.setMessage(popupmessage)
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                pindialog = new Dialog(context);
                                pindialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                pindialog.setContentView(R.layout.validatepinumber);
                                btnSubmitpin = (Button) pindialog.findViewById(R.id.btnSubmitpin);
                                pinview = (Pinview) pindialog.findViewById(R.id.pinview);
                                ImageView closepopup = (ImageView) pindialog.findViewById(R.id.closepopup);
                                closepopup.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        pindialog.dismiss();
                                    }
                                });
                                btnSubmitpin.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        DataBaseAdapter objdatabaseadapter = null;
                                        try {

                                            String getEncryptedPIN = null;
                                            try {
                                                getEncryptedPIN = sha256(pinview.getValue());
                                            } catch (NoSuchAlgorithmException e) {
                                                e.printStackTrace();
                                            }
                                            if (getEncryptedPIN.equals(preferenceMangr.pref_getString("getpin"))) {
                                                objdatabaseadapter = new DataBaseAdapter(context);
                                                objdatabaseadapter.open();
                                                String getresult = "0";
                                                String getschedulecode = objdatabaseadapter.GetScheduleCode();
                                                //SAles close details
                                                getresult = objdatabaseadapter.InsertSalesClose(getschedulecode);
                                                if (getresult.equals("success")) {
                                                    pindialog.dismiss();
                                                    Toast toast = Toast.makeText(getApplicationContext(), "Sales closed Successfully", Toast.LENGTH_LONG);
                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                    toast.show();
                                                    closeKeyboard();
                                                    ScheduleActivity.getcashclosecount = objdatabaseadapter.GetCashClose(getschedulecode);
                                                    preferenceMangr.pref_putString("schedule_getcashclosecount", objdatabaseadapter.GetCashClose(getschedulecode));
                                                    //Get sales close Count
                                                    ScheduleActivity.getsalesclosecount = objdatabaseadapter.GetSalesClose(getschedulecode);
                                                    preferenceMangr.pref_putString("schedule_getsalesclosecount", objdatabaseadapter.GetSalesClose(getschedulecode));

                                                    popup_salesclose.setText("Sale Closed");
                                                    popup_salesclose.setBackgroundResource(R.drawable.editbackgroundgray);
                                                    popup_salesclose.setEnabled(false);

                                                    networkstate = isNetworkAvailable();
                                                    if (networkstate == true) {
                                                        new AsyncCloseSalesDetails().execute();
                                                    }

                                                } else {
                                                    Toast toast = Toast.makeText(getApplicationContext(), "Error in saving", Toast.LENGTH_LONG);
                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                    toast.show();
                                                }
                                            } else {
                                                Toast toast = Toast.makeText(getApplicationContext(), "Please enter correct pin", Toast.LENGTH_LONG);
                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                toast.show();
                                                return;
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
                                pindialog.show();
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
        }
    }

    //Sha Encrypt
    static String sha256(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA256");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
    //Close Keyboard
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected  class AsyncCloseSalesDetails extends
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
                    Cursor mCur2 = dbadapter.GetCloseSalesitemDatasDB();

                    JSONArray js_array2 = new JSONArray();
                    for (int i = 0; i < mCur2.getCount(); i++) {
                        JSONObject obj = new JSONObject();
                        obj.put("autonum", mCur2.getString(0));
                        obj.put("closedate", mCur2.getString(1));
                        obj.put("vancode", mCur2.getString(2));
                        obj.put("schedulecode", mCur2.getString(3));
                        obj.put("makerid", mCur2.getString(4));
                        obj.put("createddate", mCur2.getString(5));
                        js_array2.put(obj);
                        mCur2.moveToNext();
                    }

                    js_obj.put("JSonObject", js_array2);

                    jsonObj =  api.SalesCloseDetails(js_obj.toString(),context);
                    //Call Json parser functionality
                    JSONParser parser = new JSONParser();
                    //parse the json object to boolean
                    List = parser.parseCashReport(jsonObj);
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
            if (result.size() >= 1) {
                if(result.get(0).ScheduleCode.length>0){
                    for(int j=0;j<result.get(0).ScheduleCode.length;j++){
                        DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
                        dataBaseAdapter.open();
                        dataBaseAdapter.UpdateSalesCloseFlag(result.get(0).ScheduleCode[j]);
                        dataBaseAdapter.close();
                    }
                }

            }

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
                freeitemdialog.setCanceledOnTouchOutside(false);
                lv_freeitemlist = (ListView) freeitemdialog.findViewById(R.id.lv_freeitemlist);
                ImageView close = (ImageView) freeitemdialog.findViewById(R.id.closepopup);
                Button txtYesAction = (Button) freeitemdialog.findViewById(R.id.btnOkAction);
                txtYesAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        freeitemdialog.dismiss();
                        return;


                    }
                });
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

                    if(pageflag == 1)
                    {
                        GetArea(getscheduleroutecode);

                    }
                    else{
                        GetListArea(getscheduleroutecode);
                    }
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
//    public class SalesAreaAdapter extends BaseAdapter {
//
//        private Context context;
//        private LayoutInflater layoutInflater;
//
//        SalesAreaAdapter(Context c) {
//            context = c;
//            layoutInflater = LayoutInflater.from(context);
//        }
//
//        @Override
//        public int getCount() {
//            return AreaCode.length;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return AreaCode[position];
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
//            SalesAreaAdapter.ViewHolder mHolder;
//
//            if (convertView == null) {
//                convertView = layoutInflater.inflate(R.layout.areapopuplist, parent, false);
//                mHolder = new SalesAreaAdapter.ViewHolder();
//                try {
//                    mHolder.listareaname = (TextView) convertView.findViewById(R.id.listareaname);
//                    mHolder.listcustomercount = (TextView)convertView.findViewById(R.id.listcustomercount);
//                } catch (Exception e) {
//                    Log.i("Route", e.toString());
//                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
//                    mDbErrHelper.open();
//                    String geterrror = e.toString();
//                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
//                    mDbErrHelper.close();
//                }
//                convertView.setTag(mHolder);
//            } else {
//                mHolder = (SalesAreaAdapter.ViewHolder) convertView.getTag();
//            }
//            try {
//                mHolder.listareaname.setText(String.valueOf(AreaName[position] +" - "+CityName[position]));
//
//                mHolder.listcustomercount.setText(String.valueOf(CustomerCount[position]));
//
//
//                int getcount = 0;
//                for(int i = 0;i<AreaCode.length;i++){
//                    if(!AreaCode[i].equals("0")){
//                        getcount = getcount + Integer.parseInt(CustomerCount[i]);
//                    }
//                }
//            } catch (Exception e) {
//                Log.i("Route value", e.toString());
//                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
//                mDbErrHelper.open();
//                String geterrror = e.toString();
//                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
//                mDbErrHelper.close();
//            }
//            convertView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    txtareaname.setText(String.valueOf(AreaName[position] +" - "+CityName[position]));
//                    LoginActivity.getareaname = String.valueOf(AreaName[position] +" - "+CityName[position]);
//                    preferenceMangr.pref_putString(Constants.KEY_GETAREACODE, String.valueOf(AreaCode[position]));
////                    preferenceMangr.pref_putString(Constants.KEY_GETAREANAME, AreaName[position] +" - "+CityName[position]);
//                    LoginActivity.getareacode = String.valueOf(AreaCode[position]);
//
//                    areadialog.dismiss();
//                }
//            });
//            return convertView;
//        }
//
//        private class ViewHolder {
//            private TextView listareaname,listcustomercount;
//
//        }
//
//    }

    public  void ImagePreviewPopup(String imageUrl){
        try {
            Dialog confirmationPopup = new Dialog(context);
            confirmationPopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
            confirmationPopup.setContentView(R.layout.image_preview_popup);
            confirmationPopup.setCanceledOnTouchOutside(false);
            confirmationPopup.setCancelable(false);
            confirmationPopup.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            ImageView imageView_Popup = confirmationPopup.findViewById(R.id.imageView_Popup);
            TextView image_toolbar_title = confirmationPopup.findViewById(R.id.image_toolbar_title);
            ImageView closepopup = confirmationPopup.findViewById(R.id.closepopup);


            Picasso.with(context)
                    .load(getimageurl)//optional
//                            .resize(200, 200)         //optional
//                            .centerCrop()                        //optional
                    .into(imageView);

//            Glide.with(ReceiptActivity.this).load(imageUrl).into(imageView_Popup);


            closepopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    confirmationPopup.dismiss();
                }
            });
            confirmationPopup.show();
        }catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("confirmationPopup", e.getMessage());
        }
    }

    public void getUPIPaymentPopUp(String from) {
        DataBaseAdapter objdatabaseadapter = null;
        issalesclosepopup = false;
        try {
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cursor Cur1 = objdatabaseadapter.CheckUPIPayment();
            if(Cur1.getCount()>0) {
                issalesclosepopup = false;
                String getsalestransactionno = Cur1.getString(0);
                String  getfinanicialyear = Cur1.getString(1);
                String getbookingno = Cur1.getString(2);
                String getbillno = Cur1.getString(3);
                String getcompanycode = Cur1.getString(6);
                String getimageurl = Cur1.getString(7);
                final String getbillcopystatus = Cur1.getString(8);
                final String getfinalfrom = from;
                int getupipaidcount = Cur1.getInt(9);
                dialogstatus = new Dialog(context);
                dialogstatus.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogstatus.setContentView(R.layout.salesupipopup);
                dialogstatus.setCanceledOnTouchOutside(false);
                final TextView paymentbookingno = (TextView)dialogstatus.findViewById(R.id.paymentbookingno);
                final TextView paymentbillno = (TextView)dialogstatus.findViewById(R.id.paymentbillno);
                Button btnupisubmit = (Button) dialogstatus.findViewById(R.id.btnupisubmit);
                ImageView closepopup = (ImageView) dialogstatus.findViewById(R.id.closepopup);
                LinearLayout LLbillcopy = (LinearLayout) dialogstatus.findViewById(R.id.LLbillcopy);
                LinearLayout cashpaid = (LinearLayout) dialogstatus.findViewById(R.id.mainBill);
                LinearLayout payoutStatus = (LinearLayout) dialogstatus.findViewById(R.id.payoutStatus);
                ImageView imageView=(ImageView)dialogstatus.findViewById(R.id.imageView);

                final RadioButton radio_paid = (RadioButton) dialogstatus.findViewById(R.id.radio_paid);
                RadioButton radio_notpaid = (RadioButton) dialogstatus.findViewById(R.id.radio_notpaid);
                final RadioButton radio_upi = (RadioButton) dialogstatus.findViewById(R.id.radio_upi);

                txtupivendername=(TextView)dialogstatus.findViewById(R.id.txtupivendername);
                final EditText txtupitransactionID=(EditText) dialogstatus.findViewById(R.id.txtupitransactionID);
                final EditText txtbillamount=(EditText) dialogstatus.findViewById(R.id.txtbillamount);
                final EditText txtupiamount=(EditText)dialogstatus.findViewById(R.id.txtupiamount);
                final EditText txtcashamount=(EditText)dialogstatus.findViewById(R.id.txtcashamount);

                txtbillamount.setText(Cur1.getString(5));
                txtbillamount.setEnabled(false);
                txtupiamount.setText(Cur1.getString(5));
                payoutStatus.setVisibility(View.VISIBLE);
                radio_upi.setChecked(true);
                radio_notpaid.setChecked(false);
                radio_paid.setChecked(false);
                if(Cur1!=null)
                    Cur1.close();

                txtcashamount.setText("0");
                txtcashamount.setEnabled(false);
                paymentbookingno.setText("BK.NO. "+getbookingno);
                paymentbillno.setText("Bill No. "+getbillno);


                Picasso.with(context)
                        .load(getimageurl)
                        .into(imageView);

                txtupivendername.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GetPaymentVenderName();
                    }
                });
                txtupiamount.addTextChangedListener(new TextWatcher() {

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
                        if (!txtupiamount.getText().toString().equals("")) {

                            if (Integer.parseInt(txtupiamount.getText().toString()) > Integer.parseInt(txtbillamount.getText().toString())) {
                                Toast toast = Toast.makeText(context,"Please enter valid upi amount", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                txtupiamount.setText(txtbillamount.getText().toString());
                            } else {
                                Integer billAmount = Integer.parseInt(txtbillamount.getText().toString());
                                Integer upiAmount = Integer.parseInt(txtupiamount.getText().toString());
                                Integer cashAmount = billAmount - upiAmount;
                                txtcashamount.setText(String.valueOf(cashAmount));
                            }
                        }


                    }
                });


                closepopup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogstatus.dismiss();
                    }
                });


                closepopup.setVisibility(View.GONE);
                final DataBaseAdapter finalObjdatabaseadapter = new DataBaseAdapter(context);
                final String finalGetsalestransactionno = getsalestransactionno;
                final String finalGetfinanicialyear = getfinanicialyear;
                final String finalGetbookingno = getbookingno;
                final String finalGetCompanycode = getcompanycode;
                final int finalGetUpiPaidCount = getupipaidcount;

                btnupisubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            if(radio_upi.isChecked()) {
                                if(getpaymentvenderID.equals("") || getpaymentvenderID.equals(null)
                                        || txtupivendername.getText().toString().equals("") ||  txtupivendername.getText().toString().equals(null)){
                                    Toast toast = Toast.makeText(getApplicationContext(),"Please select payment vender name", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    //Toast.makeText(getApplicationContext(),"Please select company name",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (txtupitransactionID.getText().toString().equals("") || txtupitransactionID.getText().toString().equals(null)) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter Transaction ID", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    //Toast.makeText(getApplicationContext(),"Please enter valid amount",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (txtupitransactionID.getText().toString().equals("") || txtupitransactionID.getText().toString().equals(null)) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter UPI amount", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    //Toast.makeText(getApplicationContext(),"Please enter valid amount",Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if (Integer.parseInt(txtupiamount.getText().toString()) > Integer.parseInt(txtbillamount.getText().toString())) {
                                    Toast toast = Toast.makeText(context,"Please enter valid upi amount", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    return;
                                }

                                finalObjdatabaseadapter.open();
                                String getresult = finalObjdatabaseadapter.insertSalesReceipt(finalGetCompanycode,
                                        preferenceMangr.pref_getString("getvancode"),"UPI",txtupiamount.getText().toString(),
                                        finalGetfinanicialyear, getpaymentvenderID,txtupitransactionID.getText().toString(),finalGetsalestransactionno);

                                if (!txtcashamount.getText().toString().equals("0")) {
                                    getresult = finalObjdatabaseadapter.insertSalesReceipt(finalGetCompanycode,
                                            preferenceMangr.pref_getString("getvancode"),"Cash",txtcashamount.getText().toString(),
                                            finalGetfinanicialyear, "0","",finalGetsalestransactionno);
                                }
                                if (getresult.equals("success")) {
                                    dialogstatus.dismiss();
                                    Toast toast = Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.BOTTOM, 0, 150);
                                    toast.show();
//                                    if (getpaymentstatus.equals("upi")) {
                                    getUPIPaymentPopUp(getfinalfrom);
//                                    }
                                    networkstate = isNetworkAvailable();
                                    if (networkstate == true) {
                                        new AsyncUpdateSalesReceiptDetails().execute();
                                        new AsyncReceiptDetails().execute();

                                    }
                                }
                            }else{
                                String getpaymentstatus = "";
                                if (radio_paid.isChecked()) {
                                    getpaymentstatus = "yes";
                                } else if (radio_upi.isChecked()) {
                                    getpaymentstatus = "upi";
                                }else {
                                    getpaymentstatus = "no";
                                }
                                try {
                                    finalObjdatabaseadapter.open();
//                                    String getresult = finalObjdatabaseadapter.UpdateSalesListReceipt(finalGetsalestransactionno,
//                                            finalGetfinanicialyear, getbillcopystatus, getpaymentstatus);
                                    String getresult = "";
                                    if (finalGetUpiPaidCount == 0) {
                                        getresult = finalObjdatabaseadapter.UpdateSalesListReceipt(finalGetsalestransactionno,
                                                finalGetfinanicialyear, getbillcopystatus, getpaymentstatus);
                                    } else {
                                        getresult = finalObjdatabaseadapter.UpdateSalesListCompanyReceipt(finalGetsalestransactionno,
                                                finalGetfinanicialyear, getbillcopystatus, getpaymentstatus, finalGetCompanycode);
                                    }
                                    if (getresult.equals("success")) {
                                        dialogstatus.dismiss();
                                        Toast toast = Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.BOTTOM, 0, 150);
                                        toast.show();
                                        if (getfinalfrom.equals("closesales") || getfinalfrom.equals("closesalespopup")) {
                                            getUPIPaymentPopUp(getfinalfrom);
                                        }
                                        networkstate = isNetworkAvailable();
                                        if (networkstate == true) {
                                            new AsyncUpdateSalesReceiptDetails().execute();
                                        }
                                    }
                                } catch (Exception e) {
                                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                    mDbErrHelper.open();
                                    String geterrror = e.toString();
                                    mDbErrHelper.insertErrorLog(geterrror.replace("'", " "),
                                            this.getClass().getSimpleName()+" - Update SalesList Receipt", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                    mDbErrHelper.close();
                                } finally {
                                    if (finalObjdatabaseadapter != null)
                                        finalObjdatabaseadapter.close();
                                }
                            }



                        } catch (Exception e) {
                            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                            mDbErrHelper.open();
                            String geterrror = e.toString();
                            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "),
                                    this.getClass().getSimpleName()+" - Update SalesList Receipt", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            mDbErrHelper.close();
                        } finally {
                            if (finalObjdatabaseadapter != null)
                                finalObjdatabaseadapter.close();
                        }

                    }
                });
                dialogstatus.show();
            }else{
                if (from.equals("addsales")) {
                    Intent i = new Intent(context, SalesActivity.class);
                    startActivity(i);
                } else {
                    issalesclosepopup = true;
                }
            }
        } catch (Exception e) {
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName()+
                    " -getUPIPaymentPopUp", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        } finally {
            if(objdatabaseadapter!=null)
                objdatabaseadapter.close();

        }


    }
    protected  class AsyncUpdateSalesReceiptDetails extends
            AsyncTask<String, JSONObject, ArrayList<SalesSyncDatas>> {
        ArrayList<SalesSyncDatas> List = null;
        JSONObject jsonObj = null;
        @Override
        protected  ArrayList<SalesSyncDatas> doInBackground(String... params) {
            RestAPI api = new RestAPI();
            try {
                JSONObject js_salesobj = new JSONObject();
                DataBaseAdapter dbadapter = null;
                Cursor mCursales = null;
                try {
                    int count=0;
                    dbadapter = new DataBaseAdapter(context);
                    dbadapter.open();
                    mCursales = dbadapter.GetSalesReceiptDatasDB();
                    JSONArray js_array2 = new JSONArray();
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
                    if (mCursales != null && !mCursales.isClosed()){
                        count = mCursales.getCount();
                        mCursales.close();

                    }

                    js_salesobj.put("JSonObject", js_array2);

                    jsonObj =  api.SalesReceiptDetails(js_salesobj.toString(),context);
                    //Call Json parser functionality
                    JSONParser parser = new JSONParser();
                    //parse the json object to boolean
                    List = parser.parseSalesReceiptDataList(jsonObj);
//                    dbadapter.close();
                }
                catch (Exception e)
                {
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "),
                            this.getClass().getSimpleName()+" - AsyncUpdateSalesReceiptDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                }finally {
                    if(dbadapter!=null)
                        dbadapter.close();
                    if(mCursales!=null)
                        mCursales.close();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncSalesDetails", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "),
                        this.getClass().getSimpleName()+" - SalesReceiptDetails JSON Declare", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
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
                            //objdatabaseadapter.UpdateSalesRecieptFlag(List.get(0).TransactionNo[j]);
                        }
                    }
                }
            }catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncSalesDetails", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "),
                        this.getClass().getSimpleName()+" - AsyncUpdateSalesReceiptDetails Post execute", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            } finally {
                if(objdatabaseadapter!=null)
                    objdatabaseadapter.close();
            }

        }
    }

    public void sendSMSForReceipt(String customercode, String billno, String paymentType, String upiTransNo) {
        try {
            DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(getApplicationContext());
            dataBaseAdapter.open();
            String cusMobNum = dataBaseAdapter.getCustomerMobileNumber(customercode);
            dataBaseAdapter.close();
            if (Utilities.isNullOrEmpty(cusMobNum)) {
                Toast toast = Toast.makeText(getApplicationContext(), "This customer not have the mobile number. So unable to send the SMS for salesbill", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            new AsyncSendSMSForReceipt().execute(cusMobNum, billno, paymentType, upiTransNo, customercode);

        } catch (Exception e) {
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }

    protected  class AsyncSendSMSForReceipt extends
            AsyncTask<String, JSONObject, String> {
        ArrayList<CustomerDatas> List = null;
        JSONObject jsonObj = null;
        String success1="";
        ProgressDialog loading;
        String cusMobNum="",billNumber="", paymentType="", upiTransNo="", customerCode="";
        @Override
        protected  String doInBackground(String... params) {
            RestAPI api = new RestAPI();

            JSONObject js_obj = new JSONObject();
            try {
                if (params != null && params.length > 0){
                    cusMobNum = params[0];
                    billNumber = params[1];
                    paymentType = params[2];
                    upiTransNo = params[3];
                    customerCode = params[4];
                }
                if (Utilities.isNullOrEmpty(cusMobNum) || Utilities.isNullOrEmpty(billNumber))
                    return null;

                DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(getApplicationContext());
                dataBaseAdapter.open();
                Cursor mCursor = dataBaseAdapter.getReceiptForSMS(billNumber, customerCode,
                        preferenceMangr.pref_getString("getvancode"),
                        preferenceMangr.pref_getString("getschedulecode"),
                        preferenceMangr.pref_getString("getfinanceyrcode"));

                if (mCursor != null && mCursor.getCount() > 0) {
                    for (int i=0; i < mCursor.getCount(); i++) {
                        String billNum = mCursor.getString(mCursor.getColumnIndex("voucherno"));
                        String billAmt = mCursor.getString(mCursor.getColumnIndex("amount"));
                        jsonObj = api.sendSMS("receiptsms.php", context, cusMobNum, billNum, billAmt ,paymentType, upiTransNo);
                        if(jsonObj!=null){
                            success1 = jsonObj.getString("success");
                        }
                        mCursor.moveToNext();
                    }
                }
                if (mCursor != null)
                    mCursor.close();
                if (dataBaseAdapter != null)
                    dataBaseAdapter.close();
            }
            catch (Exception e)
            {
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }

            return "";
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(context, "", "Please wait...", true, true);
            loading.setCancelable(false);
            loading.setCanceledOnTouchOutside(false);
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            try {
                if (success1.equals("1")) {
                    Toast toast = Toast.makeText(getApplicationContext(), "SMS sent successfully", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "SMS not sent to customer", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
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
}
