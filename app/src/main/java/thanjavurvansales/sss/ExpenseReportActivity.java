package thanjavurvansales.sss;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ExpenseReportActivity extends AppCompatActivity {
    public static ListView listView;
    TextView expenseslistdate;
    public static String getexpenselistdate ;
    private int year, month, day;
    private Calendar calendar;
    ArrayList<ExpensesDetails> expenseslist = new ArrayList<ExpensesDetails>();
    public static TextView expensestotalamt,expensesremainingamt,expensestripadvance;
    public Context context;
    ImageView goback,expensereportlogout;
    DecimalFormat dft = new DecimalFormat("0.00");
    ExpensesReportListBaseAdapter adapter=null;
    public static PreferenceMangr preferenceMangr=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_report);
        context = this;

        listView = (ListView) findViewById(R.id.expenseslistView);
        expenseslistdate = (TextView)findViewById(R.id.expenseslistdate);
        expensestotalamt = (TextView)findViewById(R.id.expensestotalamt);
        expensesremainingamt = (TextView)findViewById(R.id.expensesremainingamt);
        expensestripadvance = (TextView)findViewById(R.id.expensestripadvance);
        goback = (ImageView)findViewById(R.id.goback);
        expensereportlogout = (ImageView)findViewById(R.id.expensereportlogout);

        expensestripadvance.setText("0");

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

        //Set Curent date
        getexpenselistdate = preferenceMangr.pref_getString("getformatdate");
        expenseslistdate.setText(preferenceMangr.pref_getString("getcurrentdatetime"));

        //Set Now Date
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        expenseslistdate.setOnClickListener(new View.OnClickListener() {
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
                        getexpenselistdate = year+ "-"+getmonth+"-"+getdate;
                        expenseslistdate.setText(vardate );
                        GetExpensesList();

                    }
                };
                DatePickerDialog dpDialog = new DatePickerDialog(context, listener, year, month, day);
                dpDialog.show();
            }
        });

        //Logout process
        expensereportlogout.setOnClickListener(new View.OnClickListener() {
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
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack(null);
            }
        });

        //Get Expense list
        GetExpensesList();

    }
    public  void GetExpensesList(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            expenseslist.clear();
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetExpenseListDB(getexpenselistdate);
            String gettripadvance = objdatabaseadapter.GetScheduleTripAdavanceDB(getexpenselistdate);
            if(gettripadvance.equals("")){
                gettripadvance = "0";
            }
            expensestripadvance.setText(String.valueOf(dft.format(Double.parseDouble(gettripadvance))));
            if(Cur.getCount()>0) {
                for(int i=0;i<Cur.getCount();i++){
                    expenseslist.add(new ExpensesDetails(Cur.getString(0),Cur.getString(1),
                            Cur.getString(2),Cur.getString(3),Cur.getString(4),
                            Cur.getString(5),Cur.getString(6),String.valueOf(i+1),Cur.getString(7)));
                    Cur.moveToNext();
                }
                adapter = new ExpensesReportListBaseAdapter(context, expenseslist);
                listView.setAdapter(adapter);
            }else{
                adapter = new ExpensesReportListBaseAdapter(context, expenseslist);
                listView.setAdapter(adapter);
                expensestotalamt.setText("\u20B9 0.00");
                expensesremainingamt.setText("\u20B9 0.00");
                Toast toast = Toast.makeText(getApplicationContext(),"No Expenses Available", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //Toast.makeText(getApplicationContext(),"No Expenses Available",Toast.LENGTH_SHORT).show();

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
        getexpenselistdate = year +"-"+getmonth+"-"+getdate ;
        expenseslistdate.setText(vardate);

    }
}
