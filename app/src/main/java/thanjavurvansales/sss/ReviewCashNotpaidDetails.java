package thanjavurvansales.sss;

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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ReviewCashNotpaidDetails extends AppCompatActivity {

    ListView lv_cashnotpaid_status;
    Context context;
    TextView txt_next;
    ArrayList<CashNotpaidDetails> cashNotpaidDetails = new ArrayList<CashNotpaidDetails>();
    boolean networkstate;
    ImageButton goback;
    LinearLayout footerLL;
    public static PreferenceMangr preferenceMangr=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_cash_notpaid_details);

        context = this;
        //Declare all variables
        lv_cashnotpaid_status = (ListView)findViewById(R.id.lv_cashnotpaid_status);
        txt_next = (TextView)findViewById(R.id.txt_next);
        goback = (ImageButton)findViewById(R.id.goback);
        footerLL = (LinearLayout)findViewById(R.id.footerLL);

        try {
            preferenceMangr = new PreferenceMangr(context);
        }catch (Exception e){
            Log.d("Preference Manager : ",e.toString());
        }

        networkstate = isNetworkAvailable();
        if (networkstate == true) {
            new AsyncSyncCashnotPaidDetails().execute();
        }else{
            //Call detail function
            GetCashNotPaid_Details();
        }

        //Next btn click action
        txt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, MenuActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });

        if(MyScheduleActivity.ifopenfromschedule.equals("yes")){
            goback.setVisibility(View.VISIBLE);
            footerLL.setVisibility(View.GONE);
        }else{
            goback.setVisibility(View.GONE);
            footerLL.setVisibility(View.VISIBLE);
        }

        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack(null);
            }
        });
    }

    protected  class AsyncSyncCashnotPaidDetails extends
            AsyncTask<String, JSONObject, String> {
        String List = "Success";
        JSONObject jsonObj = null;
        ProgressDialog loading;

        @Override
        protected String doInBackground(String... params) {

            RestAPI api = new RestAPI();
            String result = "";
            DataBaseAdapter dataBaseAdapter = null;
            Cursor getschedulelist = null;
            try {
                int count=0;
                dataBaseAdapter = new DataBaseAdapter(context);
                dataBaseAdapter.open();
                networkstate = isNetworkAvailable();
                if (networkstate == true) {
                    String getroutecode = "0";
                    dataBaseAdapter.open();
                    getschedulelist = dataBaseAdapter.GetScheduleDB();
                    if (getschedulelist.getCount() > 0) {
                        for (int i = 0; i < getschedulelist.getCount(); i++) {
                            getroutecode = getschedulelist.getString(1);
                        }
                    }
                    if (getschedulelist != null && !getschedulelist.isClosed()){
                        count = getschedulelist.getCount();
                        getschedulelist.close();

                    }
                    // cashnotpaiddetails
                    jsonObj = api.GetCashNotPaidDetails(preferenceMangr.pref_getString("deviceid"),preferenceMangr.pref_getString("getvancode"),getroutecode,"synccashnotpaiddetails.php");
                    dataBaseAdapter.DeleteCashNotPaid();
                    if (isSuccessful(jsonObj)) {
                        dataBaseAdapter.synccashnotpaiddetails(jsonObj);
                        api.udfnSyncDetails(preferenceMangr.pref_getString("deviceid"), "cashnotpaiddetails", preferenceMangr.pref_getString("getvancode"), preferenceMangr.pref_getString("getsalesschedulecode"));
                    }
                   // dataBaseAdapter.close();
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncSync", e.getMessage());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            } finally {
                if(dataBaseAdapter!=null)
                 dataBaseAdapter.close();
                if (getschedulelist != null)
                    getschedulelist.close();
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(context, "Loading", "Please wait...", true, true);
            loading.setCancelable(false);
            loading.setCanceledOnTouchOutside(false);
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            try {
                loading.dismiss();


                try {
                    //Call detail function
                    GetCashNotPaid_Details();

                } catch (Exception e) {
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                } finally {
                }

            }catch (Exception e) {
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
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
    //Checking internet connection
    public boolean isNetworkAvailable() {
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

    //Item master
    public  void GetCashNotPaid_Details(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetCashNotPaidDetails();
            if(Cur.getCount()>0) {
                cashNotpaidDetails.clear();
                for(int i=0;i<Cur.getCount();i++){
                    cashNotpaidDetails.add(new CashNotpaidDetails(Cur.getString(0),Cur.getString(1),
                            Cur.getString(2),Cur.getString(3),Cur.getString(4),
                            Cur.getString(5),Cur.getString(6) ));

                    Cur.moveToNext();
                }

                CashNotPaidBaseAdapter adapter = new CashNotPaidBaseAdapter(context, cashNotpaidDetails);
                lv_cashnotpaid_status.setAdapter(adapter);
            }else{
                cashNotpaidDetails.clear();
                CashNotPaidBaseAdapter adapter = new CashNotPaidBaseAdapter(context, cashNotpaidDetails);
                lv_cashnotpaid_status.setAdapter(adapter);
                if(!MyScheduleActivity.ifopenfromschedule.equals("yes")){
                    Intent i = new Intent(context, MenuActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),"No Customer Available", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }

                // Toast.makeText(getApplicationContext(),"No item available",Toast.LENGTH_SHORT).show();
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


    class CashNotPaidBaseAdapter  extends BaseAdapter {
        LayoutInflater inflater;
        Context context;
        ArrayList<CashNotpaidDetails> myList;
        DecimalFormat dft = new DecimalFormat("0.00");


        public CashNotPaidBaseAdapter( Context context,ArrayList<CashNotpaidDetails> myList) {
            this.myList = myList;
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return myList.size();
        }

        @Override
        public CashNotpaidDetails getItem(int position) {
            return (CashNotpaidDetails) myList.get(position);
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
                convertView = inflater.inflate(R.layout.cashnotpaidlist, parent, false);
                mHolder = new ViewHolder();
                try {
                    mHolder.cashnotpaid_sno = (TextView) convertView.findViewById(R.id.cashnotpaid_sno);
                    mHolder.cashnotpaid_customername = (TextView) convertView.findViewById(R.id.cashnotpaid_customername);
                    mHolder.cashnotpaid_areaname = (TextView) convertView.findViewById(R.id.cashnotpaid_areaname);
                    mHolder.cashnotpaid_amt = (TextView) convertView.findViewById(R.id.cashnotpaid_amt);
                    mHolder.card_view = (CardView)convertView.findViewById(R.id.card_view);
                    mHolder.listLL = (LinearLayout)convertView.findViewById(R.id.listLL);
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
                CashNotpaidDetails currentListData = getItem(position);

                mHolder.cashnotpaid_sno.setText(String.valueOf(position+1));
                mHolder.cashnotpaid_customername.setText(currentListData.getCustomernametamil());
                mHolder.cashnotpaid_areaname.setText(currentListData.getAreanametamil());
                mHolder.cashnotpaid_amt.setText(dft.format(Double.parseDouble(String.valueOf(currentListData.getGrandtotal()))));
                if(position %2 == 1)
                {
                    // Set a background color for ListView regular row/item
                    mHolder.listLL.setBackgroundColor(getResources().getColor(R.color.lightcolorred));
                }
                else
                {
                    // Set the background color for alternate row/item
                    mHolder.listLL.setBackgroundColor(getResources().getColor(R.color.lightblue));
                }
            } catch (Exception e) {
                Log.i("Route value", e.toString());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            return convertView;
        }
        private class ViewHolder {
            TextView cashnotpaid_sno,cashnotpaid_customername,cashnotpaid_areaname,cashnotpaid_amt ;
            CardView card_view;
            LinearLayout listLL;

        }

    }

    public void goBack(View v) {
        if(!MyScheduleActivity.ifopenfromschedule.equals("yes")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Confirmation");
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
        }else{
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        goBack(null);
    }

}
