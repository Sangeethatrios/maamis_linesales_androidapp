package trios.linesales;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.goodiebag.pinview.Pinview;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SalesOrderListActivity extends AppCompatActivity {
    public static SwipeMenuListView saleslistView;
    public Context context;
    private int year, month, day;
    Button btnSubmitpin;
    private Calendar calendar;
    String getschedulecode="0";
    Pinview pinview;
    TextView salesdate;
    public static TextView totalamtval,cashtotalamt,credittotalamt,txtcompanyname;
    ImageView addsales,saleslistgoback,saleslogout;
    public static String getsaleslistdate ;
    Dialog pindialog;
    ArrayList<SalesOrderListDetails> saleslist = new ArrayList<SalesOrderListDetails>();
    public static ArrayList<SalesOrderListDetails> getdata;
    Dialog dialogstatus,companydialog;
    boolean networkstate;
    String[] companycode,companyname,shortname;
    ListView lv_CompanyList;
    public static String getsalesreviewtransactionno="",getsalesreviewfinanicialyear="",
            getsalesreviewcompanycode="",getfiltercompanycode="0",getstaticflag="0",getcancelflag="",getbillstatus="";

    public  boolean issales=false;
    SalesOrderListBaseAdapterList adapter=null;
    boolean deviceFound;
    boolean issalesclose=false;
    private PrintData printData;
    Dialog printpopup;

    Dialog salescashclose;
    TextView popup_salesclose,popup_cashclose,popup_salesclose_title;
    public static PreferenceMangr preferenceMangr=null;
    ListView lv_freeitemlist;
    String[] FreeItemName,FreeItemOp,FreeItemHandover,FreeItemDistributed,FreeItemBalance,FreeItemCode,FreeItemSNO;
    Dialog freeitemdialog;

    boolean issalesclosepopup=false;
    String[] venderid,vendername;

    TextView txtupivendername;
    public static String getpaymentvenderID="0";
    Dialog upipaymentvenderdialog;
    ListView lv_UPIPaymentList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_order_list);

        //declare all variables
        context=this;
        saleslistView = (SwipeMenuListView) findViewById(R.id.saleslistView);
        salesdate = (TextView)findViewById(R.id.salesdate);
        addsales = (ImageView)findViewById(R.id.addsales);
        totalamtval = (TextView)findViewById(R.id.totalamtval);
        credittotalamt = (TextView)findViewById(R.id.credittotalamt);
        cashtotalamt = (TextView)findViewById(R.id.cashtotalamt);
        saleslogout = (ImageView)findViewById(R.id.saleslogout);
        saleslistgoback = (ImageView)findViewById(R.id.saleslistgoback);
        txtcompanyname = (TextView)findViewById(R.id.txtcompanyname);

        getfiltercompanycode="0";

        try {
            preferenceMangr = new PreferenceMangr(context);
        }catch (Exception e){
            Log.d("Preference Manager : ",e.toString());
        }

        //Get Current date
        DataBaseAdapter objdatabaseadapter = null;
        Cursor getschedulelist=null;
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
            //Get Cash close Count
            MenuActivity.getcashclosecount = objdatabaseadapter.GetCashClose(preferenceMangr.pref_getString("getschedulecode"));
            preferenceMangr.pref_putString("getcashclosecount",objdatabaseadapter.GetCashClose(preferenceMangr.pref_getString("getschedulecode")));
            //Get sales close Count
            MenuActivity.getsalesclosecount = objdatabaseadapter.GetSalesClose(preferenceMangr.pref_getString("getschedulecode"));
            preferenceMangr.pref_putString("getsalesclosecount",objdatabaseadapter.GetSalesClose(preferenceMangr.pref_getString("getschedulecode")));

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
            if(getschedulelist != null)
                getschedulelist.close();
        }


        //open sales screen
        addsales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if(!MenuActivity. getcashclosecount.equals("0") && !MenuActivity. getcashclosecount.equals("null") &&
                        !MenuActivity. getcashclosecount.equals("") && !MenuActivity. getcashclosecount.equals(null) ){
                    Toast toast = Toast.makeText(getApplicationContext(),"Cash Closed ", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    //Toast.makeText(getApplicationContext(), "Cash Closed ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!MenuActivity. getsalesclosecount.equals("0") && !MenuActivity. getsalesclosecount.equals("null") &&
                        !MenuActivity. getsalesclosecount.equals("") && !MenuActivity. getsalesclosecount.equals(null)){
                    Toast toast = Toast.makeText(getApplicationContext(),"Sales Closed ", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    //Toast.makeText(getApplicationContext(), "Sales Closed ", Toast.LENGTH_SHORT).show();
                    return;
                }*/

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
                                                   Intent i = new Intent(SalesOrderListActivity.this, CashReportActivity.class);
                                                   startActivity(i);
                                                   salescashclose.dismiss();

                                               }
                                           });
                                           salescashclose.show();
                                           popup_salesclose.setVisibility(View.GONE);
                                       }
                                   } else {
                                       Intent i = new Intent(SalesOrderListActivity.this, ScheduleActivity.class);
                                       startActivity(i);
                                   }
                               } else {
                                   Intent i = new Intent(SalesOrderListActivity.this, ScheduleActivity.class);
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
                                       Intent i = new Intent(SalesOrderListActivity.this, ScheduleActivity.class);
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
                                               Intent i = new Intent(SalesOrderListActivity.this, CashReportActivity.class);
                                               startActivity(i);
                                               salescashclose.dismiss();

                                           }
                                       });
                                       salescashclose.show();
                                   }
                               } else {
                                   Intent i = new Intent(SalesOrderListActivity.this, ScheduleActivity.class);
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
                               !preferenceMangr.pref_getString("getcashclosecount").equals("") && !preferenceMangr.pref_getString("getcashclosecount").equals(null) ){
                           Toast toast = Toast.makeText(getApplicationContext(), "Cash Closed ", Toast.LENGTH_LONG);
                           //toast.setGravity(Gravity.CENTER, 0, 0);
                           toast.show();
                           // Toast.makeText(getApplicationContext(), "Cash Closed ", Toast.LENGTH_SHORT).show();
                           return;
                       }
                       if(SalesOrderCartActivity.txttransportname != null){
                           SalesOrderCartActivity.txttransportname.setText("");
                           SalesOrderCartActivity.gettransportid="0";
                           SalesOrderCartActivity.gettransportname="";
                       }
                       if(!preferenceMangr.pref_getString("getfinanceyrcode").equals("") &&
                               !preferenceMangr.pref_getString("getfinanceyrcode").equals("0") &&
                               !preferenceMangr.pref_getString("getfinanceyrcode").equals("null")
                               && !(preferenceMangr.pref_getString("getfinanceyrcode").equals(null))) {


                           DataBaseAdapter objdatabaseadapter = null;
                           String getvouchersettingscount = "0";
                           String billtypecode="0";
                           String billcopystatus="";
                           String cashpaidstatus="";
                           String getsalestransactionno="0";
                           String getfinanicialyear="0";
                           String getbookingno = "";
                           String getbillno = "";
                           Cursor getschedulelist1=null;
                           String getgstinno = "";
                           try {
                               //Vocher settings
                               objdatabaseadapter = new DataBaseAdapter(context);
                               objdatabaseadapter.open();
                               getschedulelist1 = objdatabaseadapter.GetScheduleDB();
                               if(getschedulelist1.getCount() >0){
                                   for(int i=0;i<getschedulelist1.getCount();i++) {
                                       MenuActivity.getschedulecode = getschedulelist1.getString(0);
                                       preferenceMangr.pref_putString("getschedulecode",getschedulelist1.getString(0));
                                   }
                               }else{
                                   MenuActivity.getschedulecode = "";
                                   preferenceMangr.pref_putString("getschedulecode","");
                               }
                               if (!(preferenceMangr.pref_getString("getschedulecode").equals(""))
                                       && !(preferenceMangr.pref_getString("getschedulecode").equals(null))) {
                                   getvouchersettingscount = objdatabaseadapter.GetSalesOrderVoucherSettingsDB();
                                   objdatabaseadapter.DeleteSalesCartItemCart();
                                   if (!getvouchersettingscount.equals("0")) {
                                       Intent i = new Intent(context, SalesOrderActivity.class);
                                       startActivity(i);
                                   } else {
                                       Toast toast = Toast.makeText(getApplicationContext(),"Order voucher settings not available for this van", Toast.LENGTH_LONG);
                                       //toast.setGravity(Gravity.CENTER, 0, 0);
                                       toast.show();
                                       return;
                                   }
                               } else {
                                   Toast toast = Toast.makeText(getApplicationContext(),"No Schedule for today", Toast.LENGTH_LONG);
                                   //toast.setGravity(Gravity.CENTER, 0, 0);
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
                               objdatabaseadapter.close();
                               if(getschedulelist1!=null)
                                   getschedulelist1.close();
                           }

                       }else {
                           Toast toast = Toast.makeText(getApplicationContext(),"Sales voucher settings not available for this van", Toast.LENGTH_LONG);
                           //toast.setGravity(Gravity.CENTER, 0, 0);
                           toast.show();
                           return;
                       }
                   }
               }catch (Exception e){
                   DataBaseAdapter mDbErrHelper2 = new DataBaseAdapter(context);
                   mDbErrHelper2.open();
                   mDbErrHelper2.insertErrorLog("Order List Addsales : "+e.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                   mDbErrHelper2.close();
               }


            }
        });

        //Set Now Date
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        getsaleslistdate = preferenceMangr.pref_getString("getformatdate");
        salesdate.setText(preferenceMangr.pref_getString("getcurrentdatetime"));




        salesdate.setOnClickListener(new View.OnClickListener() {
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
                        getsaleslistdate = year+ "-"+getmonth+"-"+getdate;
                        salesdate.setText(vardate );
                        GetSalesList();

                    }
                };
                DatePickerDialog dpDialog = new DatePickerDialog(context, listener, year, month, day);
                dpDialog.show();
            }
        });

        //Swipe menu editior functionality
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create Edit Bill copy item
              /*  SwipeMenuItem cancelItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item width
                cancelItem.setWidth(130);
                // set a icon
                cancelItem.setIcon(R.drawable.ic_mode_edit);
                // add to menu
                menu.addMenuItem(cancelItem);*/

                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                //  openItem.setBackground(ContextCompat.getDrawable(context,R.drawable.noborder));
                openItem.setWidth(130);
                openItem.setIcon(R.drawable.ic_view);
                menu.addMenuItem(openItem);


            }
        };

        saleslistView.setMenuCreator(creator);

        //Click swipemenu action ...Edit sales receipt and then print
        saleslistView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        ArrayList<SalesOrderListDetails> currentListDatareview = getdata;
                        getsalesreviewtransactionno = currentListDatareview.get(position).getTransactionno();
                        getsalesreviewfinanicialyear = currentListDatareview.get(position).getFinancialyearcode();
                        getsalesreviewcompanycode = currentListDatareview.get(position).getCompanycode();
                        getstaticflag = currentListDatareview.get(position).getFlag();
                        getbillstatus = currentListDatareview.get(position).getStatus();
                        final String listschedulecode = currentListDatareview.get(position).getSchedulecode();
                        String listsalescount = "";
                        //Get Current date
                        DataBaseAdapter objdatabaseadapter = null;
                        try{
                            objdatabaseadapter = new DataBaseAdapter(context);
                            objdatabaseadapter.open();
                            listsalescount = objdatabaseadapter.GetSalesClose(listschedulecode);
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
                        if(listsalescount.equals("") || listsalescount.equals("0") ||
                                listsalescount.equals("null") ||listsalescount.equals(null)){
                            getcancelflag="false";
                        }else{
                            getcancelflag="true";
                        }


                        //Call saels view page
                        //if(!currentListDatareview.get(position).getStatus().equals("2")) {
                            Intent i = new Intent(context, SalesOrderViewActivity.class);
                            startActivity(i);
                        /*}else{
                            Toast toast=Toast.makeText(context,"Order Cancelled",Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                        }*/
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });

        //Logout process
        saleslogout.setOnClickListener(new View.OnClickListener() {
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
        saleslistgoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack(null);
            }
        });

        //company name
        txtcompanyname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetCompanyName();
            }
        });




        //GetSale order List
        GetSalesList();
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
    public void SalesClose(){
        String popupmessage="Do you want to close sales?";
        if(issalesclose) {
            DataBaseAdapter objdatabaseadapter = null;
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            String deviceid = preferenceMangr.pref_getString("deviceid");
            String result = "";
            try {
                GetFreeItemexcess();
                networkstate = isNetworkAvailable();
                if (networkstate == true) {
                    String getschedulecode = objdatabaseadapter.GetScheduleCode();
                    result = Utilities.getDeliveryNotePendingCount(deviceid, getschedulecode, context);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please check internet connection", Toast.LENGTH_LONG);
                    //toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                String geteinvoicependingbills = objdatabaseadapter.Checkeinvoicepending();
                if (geteinvoicependingbills != null && !geteinvoicependingbills.equals("0")) {
                    popupmessage = "You have " + geteinvoicependingbills + " GSTIN invoice(s) for which e-Invoice is " +
                            "not yet generated. \nAre you sure want to close sales?";
                }

            } catch (Exception e) {
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'", " "),
                        this.getClass().getSimpleName() + " - Check e-invoice pending", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            } finally {
                if (objdatabaseadapter != null)
                    objdatabaseadapter.close();
            }
            if (!result.equals("0")) {
                Utilities.CheckDeliveryNoteDialog(context);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirmation");
                // builder.setIcon(R.mipmap.ic_van);

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
                                        DataBaseAdapter finalObjdatabaseadapter1 = null;
                                        finalObjdatabaseadapter1 = new DataBaseAdapter(context);
                                        finalObjdatabaseadapter1.open();
                                        try {

                                            String getEncryptedPIN = null;
                                            try {
                                                getEncryptedPIN = sha256(pinview.getValue());
                                            } catch (NoSuchAlgorithmException e) {
                                                e.printStackTrace();
                                            }
                                            if (getEncryptedPIN.equals(preferenceMangr.pref_getString("getpin"))) {

                                                String getresult = "0";
                                                getschedulecode = finalObjdatabaseadapter1.GetScheduleCode();
                                                //SAles close details
                                                getresult = finalObjdatabaseadapter1.InsertSalesClose(getschedulecode);
                                                if (getresult.equals("success")) {
                                                    pindialog.dismiss();
                                                    Toast toast = Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_LONG);
                                                    //toast.setGravity(Gravity.CENTER, 0, 0);
                                                    toast.show();
                                                    closeKeyboard();
                                                    //Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_SHORT).show();
                                                    networkstate = isNetworkAvailable();
                                                    if (networkstate == true) {
                                                        // new AsyncCloseSalesDetails().execute();
                                                    }
                                                    Intent i = new Intent(context, OrderFormActivity.class);
                                                    startActivity(i);
                                                } else {
                                                    Toast toast = Toast.makeText(getApplicationContext(), "Error in saving", Toast.LENGTH_LONG);
                                                    //toast.setGravity(Gravity.CENTER, 0, 0);
                                                    toast.show();
                                                    //Toast.makeText(getApplicationContext(), "Error in saving", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast toast = Toast.makeText(getApplicationContext(), "Please enter correct pin", Toast.LENGTH_LONG);
                                                //toast.setGravity(Gravity.CENTER, 0, 0);
                                                toast.show();
                                                // Toast.makeText(getApplicationContext(),"Please enter correct pin",Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        } catch (Exception e) {
                                            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                            mDbErrHelper.open();
                                            String geterrror = e.toString();
                                            mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                            mDbErrHelper.close();
                                        } finally {
                                            if (finalObjdatabaseadapter1 != null)
                                                finalObjdatabaseadapter1.close();
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
    /*******FILTER FUNCTIONALITY********/
    public  void GetCompanyName(){
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
                CompanyAdapter adapter = new CompanyAdapter(context);
                lv_CompanyList.setAdapter(adapter);
                companydialog.show();
            }else{
                Toast.makeText(getApplicationContext(),"No Area in this route",Toast.LENGTH_SHORT).show();
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
    //Sales List
    public  void GetSalesList(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            saleslist.clear();
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetSalesOrderListDB(getsaleslistdate,getfiltercompanycode);
            if(Cur.getCount()>0) {
                issales=true;
                for(int i=0;i<Cur.getCount();i++){
                    saleslist.add(new SalesOrderListDetails(Cur.getString(3),Cur.getString(7),
                            Cur.getString(8),Cur.getString(20),
                            Cur.getString(21),Cur.getString(25),Cur.getString(14),
                            String.valueOf(Cur.getCount()-i),Cur.getString(11),Cur.getString(23),Cur.getString(19),
                            Cur.getString(22),Cur.getString(10),
                            Cur.getString(18),Cur.getString(24),Cur.getString(15)
                            ,Cur.getString(16),Cur.getString(2),Cur.getString(17),
                            Cur.getString(0)));
                    Cur.moveToNext();
                }
                getdata = saleslist;
                adapter = new SalesOrderListBaseAdapterList(context,saleslist);
                saleslistView.setAdapter(adapter);
            }else{
                adapter = new SalesOrderListBaseAdapterList(context,saleslist);
                saleslistView.setAdapter(adapter);
                totalamtval.setText("\u20B9 0.00");
                Toast.makeText(getApplicationContext(),"No Order Available",Toast.LENGTH_SHORT).show();

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
                    getfiltercompanycode = companycode[position];
                    GetSalesList();
                    companydialog.dismiss();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView listcompanyname;

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
        getsaleslistdate = year +"-"+getmonth+"-"+getdate ;
        salesdate.setText(vardate);

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
    class SalesOrderListBaseAdapterList extends BaseAdapter {
        LayoutInflater inflater;
        Context context;
        ArrayList<SalesOrderListDetails> myList;
        DecimalFormat dft = new DecimalFormat("0.00");
        public SalesOrderListBaseAdapterList(Context context,ArrayList<SalesOrderListDetails> myList) {
            this.myList = myList;
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return myList.size();
        }

        @Override
        public SalesOrderListDetails getItem(int position) {
            return (SalesOrderListDetails) myList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
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

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder mHolder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.sales_order__list_details, parent, false);
                mHolder = new ViewHolder();
                try {
                    mHolder.saleslistSno = (TextView) convertView.findViewById(R.id.saleslistSno);
                    mHolder.saleslistbillno = (TextView) convertView.findViewById(R.id.saleslistbillno);
                    mHolder.saleslistretailer = (TextView) convertView.findViewById(R.id.saleslistretailer);
                    mHolder.saleslisttotal = (TextView) convertView.findViewById(R.id.saleslisttotal);
                    mHolder.listLL = (LinearLayout) convertView.findViewById(R.id.listLL);
                    mHolder.salescard_view= (CardView)convertView.findViewById(R.id.salescard_view);
                    mHolder.saleslistcity = (TextView)convertView.findViewById(R.id.saleslistcity);
                    mHolder.saleslistarea = (TextView)convertView.findViewById(R.id.saleslistarea);
                    mHolder.saleslistgstin = (TextView)convertView.findViewById(R.id.saleslistgstin);
                    mHolder.saleslistamounttype = (TextView)convertView.findViewById(R.id.saleslistamounttype);
                    mHolder.saleslistbookingno = (TextView)convertView.findViewById(R.id.saleslistbookingno);
                    mHolder.saleslistpaymenttype = (TextView)convertView.findViewById(R.id.saleslistpaymenttype);
                    mHolder.saleslistcompanyname = (TextView)convertView.findViewById(R.id.saleslistcompanyname);
                    mHolder.saleslistbillcopyrequired = (TextView)convertView.findViewById(R.id.saleslistbillcopyrequired) ;

                    convertView.setTag(mHolder);
                    convertView.setTag(R.id.saleslistSno, mHolder.saleslistSno);
                    convertView.setTag(R.id.saleslistbillno, mHolder.saleslistbillno);
                    convertView.setTag(R.id.saleslistretailer, mHolder.saleslistretailer);
                    convertView.setTag(R.id.saleslisttotal, mHolder.saleslisttotal);
                    convertView.setTag(R.id.listLL, mHolder.listLL);
                    convertView.setTag(R.id.salescard_view,mHolder.salescard_view);
                    convertView.setTag(R.id.saleslistcity,mHolder.saleslistcity);
                    convertView.setTag(R.id.saleslistarea,mHolder.saleslistarea);
                    convertView.setTag(R.id.saleslistgstin,mHolder.saleslistgstin);
                    convertView.setTag(R.id.saleslistcompanyname,mHolder.saleslistcompanyname);
                    convertView.setTag(R.id.saleslistbookingno,mHolder.saleslistbookingno);
                    convertView.setTag(R.id.saleslistpaymenttype,mHolder.saleslistpaymenttype);
                    convertView.setTag(R.id.saleslistbillcopyrequired,mHolder.saleslistbillcopyrequired);

                } catch (Exception e) {
                    Log.i("Sales List Details", e.toString());
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
                mHolder.saleslistSno.setTag(position);
                mHolder.saleslistbillno.setTag(position);
                mHolder.saleslistretailer.setTag(position);
                mHolder.saleslisttotal.setTag(position);
                mHolder.saleslistcity.setTag(position);
                mHolder.saleslistarea.setTag(position);
                mHolder.saleslistgstin.setTag(position);
                mHolder.saleslistcompanyname.setTag(position);
                mHolder.saleslistbookingno.setTag(position);
                mHolder.saleslistpaymenttype.setTag(position);
                mHolder.saleslistbillcopyrequired.setTag(position);


                final SalesOrderListDetails currentListData = getItem(position);

                mHolder.saleslistSno.setText(currentListData.getSno());
                mHolder.saleslistbillno.setText(currentListData.getBillcode());

                mHolder.saleslistretailer.setText(currentListData.getRetailernametamil());
                mHolder.saleslisttotal.setText(dft.format(Double.parseDouble(currentListData.getGrandtotal())));
                mHolder.saleslistcity.setText(currentListData.getRetailercity());
                mHolder.saleslistarea.setText(currentListData.getArea());
                mHolder.saleslistbookingno.setText("Order.No. "+currentListData.getBookingno());
                mHolder.saleslistcompanyname.setText(currentListData.getCompanyshortname());



                //Flag 2 means cancelled bill
                if(currentListData.getStatus().equals("4") ){
                    mHolder.salescard_view.setCardBackgroundColor(context.getResources().getColor(R.color.gray));
                    mHolder.saleslistSno.setTextColor(context.getResources().getColor(R.color.green));
                    mHolder.saleslistbillno.setTextColor(context.getResources().getColor(R.color.green));
                    mHolder.saleslistretailer.setTextColor(context.getResources().getColor(R.color.green));
                    mHolder.saleslisttotal.setTextColor(context.getResources().getColor(R.color.green));
                    mHolder.saleslistcity.setTextColor(context.getResources().getColor(R.color.green));
                    mHolder.saleslistarea.setTextColor(context.getResources().getColor(R.color.green));
                    mHolder.saleslistcompanyname.setTextColor(context.getResources().getColor(R.color.green));
                    mHolder.saleslistbookingno.setTextColor(context.getResources().getColor(R.color.green));
                    mHolder.saleslistpaymenttype.setTextColor(context.getResources().getColor(R.color.green));

                }
                if(currentListData.getStatus().equals("2") ){
                    mHolder.salescard_view.setCardBackgroundColor(context.getResources().getColor(R.color.gray));
                    mHolder.saleslistSno.setTextColor(context.getResources().getColor(R.color.red));
                    mHolder.saleslistbillno.setTextColor(context.getResources().getColor(R.color.red));
                    mHolder.saleslistretailer.setTextColor(context.getResources().getColor(R.color.red));
                    mHolder.saleslisttotal.setTextColor(context.getResources().getColor(R.color.red));
                    mHolder.saleslistcity.setTextColor(context.getResources().getColor(R.color.red));
                    mHolder.saleslistarea.setTextColor(context.getResources().getColor(R.color.red));
                    mHolder.saleslistcompanyname.setTextColor(context.getResources().getColor(R.color.red));
                    mHolder.saleslistbookingno.setTextColor(context.getResources().getColor(R.color.red));
                    mHolder.saleslistpaymenttype.setTextColor(context.getResources().getColor(R.color.red));

                }
                mHolder.saleslistcompanyname.setVisibility(View.GONE);


                if(position %2 == 1)
                {
                    // Set a background color for ListView regular row/item
                    mHolder.listLL.setBackgroundColor(getResources().getColor(R.color.lightredgray));
                }
                else
                {
                    // Set the background color for alternate row/item
                    mHolder.listLL.setBackgroundColor(getResources().getColor(R.color.lightvoilet));
                }

            } catch (Exception e) {
                Log.i("SalesList Exception", e.toString());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            calculateTotalAmount();
            return convertView;
        }
        private class ViewHolder {
            TextView saleslistSno, saleslistbillno,  saleslistretailer,
                    saleslisttotal,saleslistcity,saleslistarea,saleslistgstin,
                    saleslistamounttype,saleslistbookingno,saleslistpaymenttype,saleslistcompanyname,saleslistbillcopyrequired;
            LinearLayout listLL;
            CardView salescard_view;
        }
        public void calculateTotalAmount() {
            Double addamt = 0.0;
            Double cashamt = 0.0;
            Double creditamt = 0.0;

            try {
                DecimalFormat df = new DecimalFormat("0.00");
                for (int i = 0; i < myList.size(); i++) {
                    if(!myList.get(i).getFlag().equals("3")  && !myList.get(i).getFlag().equals("6") && !myList.get(i).getStatus().equals("2")) {
                        Double qty = Double.parseDouble(myList.get(i).getGrandtotal());
                        addamt = addamt + qty;
                    }
                }
                totalamtval.setText("\u20B9 "+df.format(addamt));
            } catch (Exception ex) {
                Log.i("Calculate  Exception", ex.toString());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = ex.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
        }
    }

    //Close Keyboard
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public void goBack(View v) {
        LoginActivity.ismenuopen=true;
        Intent i = new Intent(context, MenuActivity.class);
        startActivity(i);
    }
    @Override
    public void onBackPressed() {
        goBack(null);
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
                //toast.setGravity(Gravity.CENTER, 0, 0);
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
            if (issalesclosepopup) {
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
                                                    //toast.setGravity(Gravity.CENTER, 0, 0);
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
                                                    //toast.setGravity(Gravity.CENTER, 0, 0);
                                                    toast.show();
                                                }
                                            } else {
                                                Toast toast = Toast.makeText(getApplicationContext(), "Please enter correct pin", Toast.LENGTH_LONG);
                                                //toast.setGravity(Gravity.CENTER, 0, 0);
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
                UPIPaymentAdapter adapter = new UPIPaymentAdapter(context,venderid,vendername,
                        txtupivendername,getpaymentvenderID,upipaymentvenderdialog,SalesOrderListActivity.this);
                lv_UPIPaymentList.setAdapter(adapter);
                upipaymentvenderdialog.show();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(),"No payment vender", Toast.LENGTH_LONG);
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
}
