package trios.linesales;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

//import com.instacart.library.truetime.TrueTimeRx;

public class DataBaseAdapter
{
    private final Context mContext;
    public static SQLiteDatabase mDb;
    private DataBaseHelper mDbHelper;
    DecimalFormat dft = new DecimalFormat("0.00");
    public String statusvar = "Active";
    public String getdate="";
    public static PreferenceMangr preferenceMangr=null;

    //Common function for database
    public DataBaseAdapter(Context context)
    {
        this.mContext = context;
        if(mDbHelper == null)
            mDbHelper = new DataBaseHelper(mContext);
        preferenceMangr = new PreferenceMangr(mContext);
    }

    public DataBaseAdapter createDatabase() throws SQLException
    {
        try
        {
            mDbHelper.createDataBase();
        }
        catch (IOException mIOException)
        {
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public DataBaseAdapter open() throws SQLException
    {
        try
        {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();

        }
        catch (SQLException mSQLException)
        {
            throw mSQLException;
        }
        return this;
    }

    public void close() {
        mDbHelper.close();
    }
    //Get Created Date
    public String GenCreatedDate()
    {
        String dateString="";
        try{
            Date deviceTime = new Date();

            long date = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dateString = sdf.format(date);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        // Date trueTime = TrueTimeRx.now();

        //dateString = _formatDate(trueTime, "yyyy-MM-dd", TimeZone.getDefault());
        return dateString;
    }

    //Get Created Date
    public String GenCreatedDateTime()
    {
        String dateString="";
        try{
            Date deviceTime = new Date();

            long date = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateString = sdf.format(date);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        // Date trueTime = TrueTimeRx.now();

        //dateString = _formatDate(trueTime, "yyyy-MM-dd", TimeZone.getDefault());
        return dateString;
    }

    //Get Created Date
    public String GetDateTime()
    {
        String dateString="";
        try{
            Date deviceTime = new Date();

            long date = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            dateString = sdf.format(date);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        // Date trueTime = TrueTimeRx.now();

        // dateString = _formatDate(trueTime, "hh:mm a", TimeZone.getDefault());
        return dateString;
    }



    //Get formated date
    public String GenCurrentCreatedDate()
    {
        String dateString="";
        try{
            Date date = new Date();

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            dateString = sdf.format(date);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        // Date trueTime = TrueTimeRx.now();

        // dateString = _formatDate(trueTime, "dd-MM-yyyy", TimeZone.getDefault());
        return dateString;
    }
    private String _formatDate(Date date, String pattern, TimeZone timeZone) {
        DateFormat format=null;
        try{
            format = new SimpleDateFormat(pattern, Locale.ENGLISH);
            format.setTimeZone(timeZone);

        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return format.format(date);
    }
    //Insert Error log
    public void insertErrorLog(String error,String classname,String linenumber)
    {
        String Gencode= GenCreatedDateTime();
        String sql = "INSERT INTO  'tblerrorlogdetails'(error,classname,linenumber,deviceid,date) " +
                "VALUES('"+error.replace("'","")+"','"+classname+"','"+linenumber+"','"+preferenceMangr.pref_getString("deviceid")+"','"+ Gencode +"')";
        mDb.execSQL(sql);

    }
    /**************** END COMMON FUNCTION**********/

    //Get Van Datas form database for each imeino
    public Cursor GetVanNameForIMEIDB(String deviceid)
    {
        Cursor mCur = null;
        try{
            String sql ="select vancode,vanname,imeino,pin,business_type,orderprint from tblvanmaster where imeino='"+deviceid+"'" +
                    " and  status='"+statusvar+"' ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }


        return mCur;


    }
    //Get Van Datas form database for each imeino
    public String GetIMEICountDB(String deviceid)
    {
        String getcount="0";
        try{

            String sql ="select coalesce(count(*),0) from tblvanmaster where imeino='"+deviceid+"'" +
                    " and  status='"+statusvar+"' ";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getcount = mCur.getString(0);
            }
            if(mCur != null && !mCur.isClosed()){
                mCur.close();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getcount;
    }
    //Get table count
    public String GetCountDB()
    {
        String getcount = "0";
        try{
            String sql ="select coalesce(count(*),0) from tblvanmaster ";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getcount = mCur.getString(0);
            }
            if(mCur != null && !mCur.isClosed()){
                mCur.close();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getcount;
    }
    //GetServerSettingsDB
    public String GetServerSettingsDB()
    {
        String getimeino = "";
        try{
            String sql ="select coalesce(internetip,'') as ipaddress from tblserversettings where status='active' ";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getimeino = mCur.getString(0);
            }else{
                getimeino = "";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }


        return getimeino;
    }
    //Check today schedule count
    public String GetScheduleCountDB()
    {
        Cursor mCur = null;
        try{
            String Gencode= GenCreatedDate();
            String sql ="select coalesce(count(*),0) from tblsalesschedule where vancode='"+ preferenceMangr.pref_getString("getvancode") +"'" +
                    " and  scheduledate=datetime('"+Gencode+"') ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur.getString(0);
    }
    //Check voucher settings for sales
    public String GetSalesVoucherSettingsDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select count(count1-count2) from (select sum(count1) as count1,sum(count2) as count2 " +
                    " from (select count(*) as count1,0 as count2 from tblcompanymaster,tblbilltype  " +
                    " where companycode in (select companycode from tblitemmaster ) union all " +
                    "select 0 , count(*) from tblvouchersettings " +
                    "where financialyearcode='"+ preferenceMangr.pref_getString("getfinanceyrcode") +"' and type='sales' and vancode='"+ preferenceMangr.pref_getString("getvancode")+"')" +
                    " as derv) as de where count1<>count2;";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur.getString(0);
    }


    //Check voucher settings for sales
    public String GetSalesOrderVoucherSettingsDB()
    {
        Cursor mCur = null;
        try{
          /*  String sql ="select count(count1-count2) from (select sum(count1) as count1,sum(count2) as count2 " +
                    " from (select count(*) as count1,0 as count2 from tblcompanymaster union all " +
                    "select 0 , count(*) from tblvouchersettings " +
                    "where financialyearcode='"+LoginActivity.getfinanceyrcode+"' and" +
                    "  type='salesorder' and vancode='"+LoginActivity.getvancode+"')" +
                    " as derv) as de where count1<>count2;";*/
            String sql ="select coalesce(count(*),'0') as count from tblvouchersettings " +
                    "where financialyearcode='"+ preferenceMangr.pref_getString("getfinanceyrcode") +"' and" +
                    "  type='salesorder' and vancode='"+ preferenceMangr.pref_getString("getvancode") +"'  ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur.getString(0);
    }

    //Check item stock
    public String GetSalesItemStockCount()
    {
        Cursor mCur = null;
        try{
            String sql ="select coalesce(count(*),0) from tblitemmaster as a inner join tblstocktransaction as b on " +
                    " a.itemcode = b.itemcode where b.vancode = '"+ preferenceMangr.pref_getString("getvancode") +"' and b.flag!=3 ;";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur.getString(0);
    }

    //delete item cart data
    public void DeleteSalesItemCart()
    {
        try{
            String sqldeletesalescart="delete from tblsalescartdatas ";
            mDb.execSQL(sqldeletesalescart);
            String sqldeletestockconversion="delete from tblstockconversion ";
            mDb.execSQL(sqldeletestockconversion);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

    }

    //delete item cart data
    public void DeleteSalesCartItemCart()
    {
        try{
            String sqldeletesalescart="delete from tblsalesordercartdatas ";
            mDb.execSQL(sqldeletesalescart);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

    }

    //Check voucher settings for receipt
    public String GetReceiptVoucherSettingsDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select count(count1-count2) from (select sum(count1) as count1,sum(count2) as count2 " +
                    " from (select count(*) as count1,0 as count2 from tblcompanymaster union all " +
                    "select 0 , count(*) from tblvouchersettings " +
                    "where financialyearcode='"+ preferenceMangr.pref_getString("getfinanceyrcode") +"' and" +
                    "  type='receipt' and vancode='"+ preferenceMangr.pref_getString("getvancode") +"')" +
                    " as derv) as de where count1<>count2;";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur.getString(0);
    }
    //Check get financial yearcode
    public String GetFinancialYrCode()
    {
        String finacialyrcode="0";
        try{
            String Gencode= GenCreatedDate();
            String sql ="select coalesce(financialyearcode,0) from tblfinancialyear where " +
                    " fromdate <= datetime('"+Gencode+"') and todate >= datetime('"+Gencode+"') ";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                finacialyrcode = mCur.getString(0);
            }else{
                finacialyrcode = "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return finacialyrcode;
    }
    //Get Schedule List
    public Cursor GetScheduleDB()
    {
        Cursor mCur = null;
        try{
            String Gencode= GenCreatedDate();
            String sql ="select schedulecode,routecode,(select routename from tblroute where routecode=a.routecode) as routename," +
                    " tripadvance,(select routenametamil from tblroute where routecode=a.routecode) as routenametamil," +
                    " (select capacity from tblvehiclemaster where vehiclecode=a.vehiclecode) as capacity from " +
                    " tblsalesschedule as a where  vancode='"+ preferenceMangr.pref_getString("getvancode") +"'" +
                    " and  scheduledate=datetime('"+Gencode+"') ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get Booking No
    public String GetBookingNo()
    {
        Cursor mCur = null;
        try{
            String Gencode= GenCreatedDate();
         /*   String sql ="select  case when (select Count(*) from tblsales)=0 then case when " +
                    "(select coalesce(max(bookingno),1) from tblmaxcode  where code=2 " +
                    " and datetime(transdate)= datetime('"+Gencode+"') )=1 then  coalesce(max(bookingno),0)+1 " +
                    "else (select coalesce(max(bookingno),0)+1 from tblmaxcode where code=2" +
                    " and datetime(transdate)= datetime('"+Gencode+"')  ) " +
                    " end else  coalesce(max(bookingno),0)+1  " +
                    "end as bookingno from tblsales as  a where financialyearcode='"+LoginActivity.getfinanceyrcode+"'" +
                    " and datetime(billdate)= datetime('"+Gencode+"'); ";*/
            String sql ="select   coalesce(max(bookingno),0)+1  " +
                    "  as bookingno from tblsales as  a where financialyearcode='"+ preferenceMangr.pref_getString("getfinanceyrcode") +"'" +
                    " and datetime(billdate)= datetime('"+Gencode+"'); ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur.getString(0);
    }

    //Get Booking No
    public String GetSalesOrderBookingNo()
    {
        Cursor mCur = null;
        try{
            String Gencode= GenCreatedDate();
          /*  String sql ="select  case when (select Count(*) from tblsalesorder)=0 then case when " +
                    "(select coalesce(max(bookingno),1) from tblmaxcode  where code=7 " +
                    " and datetime(transdate)= datetime('"+Gencode+"') )=1 then  coalesce(max(bookingno),0)+1 " +
                    "else (select coalesce(max(bookingno),0)+1 from tblmaxcode where code=7" +
                    " and datetime(transdate)= datetime('"+Gencode+"')  ) " +
                    " end else  coalesce(max(bookingno),0)+1  " +
                    "end as bookingno from tblsalesorder as  a where financialyearcode='"+LoginActivity.getfinanceyrcode+"'" +
                    " and datetime(billdate)= datetime('"+Gencode+"'); ";*/
            String sql ="select   coalesce(max(bookingno),0)+1  " +
                    "  as bookingno from tblsalesorder as  a where financialyearcode='"+preferenceMangr.pref_getString("getfinanceyrcode") +"'" +
                    " and datetime(billdate)= datetime('"+Gencode+"'); ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur.getString(0);
    }
    //Get Booking No
    public String GetSalesOrderVoucherNo(String bookingno,String financialyearcode,String billtypecode)
    {
        Cursor mCur = null;
        try{
            String Gencode= GenCreatedDate();

            String sql ="select   case when (select Count(*) from tblsalesorder where  " +
                    "   billtypecode = '"+billtypecode+"' )=0 " +
                    " then case when (select coalesce(max(refno),0)+1 from tblmaxcode where code=777 " +
                    " and billtypecode = '"+billtypecode+"')=1 then " +
                    "(select prefix || printf('%0'||noofdigit||'d','"+ bookingno+"')||suffix FROM tblvouchersettings " +
                    "where type='salesorder' and billtypecode='"+billtypecode+"' and vancode='"+ preferenceMangr.pref_getString("getvancode") +"' " +
                    "  and financialyearcode='"+ financialyearcode +"')" +
                    "else" +
                    "(select prefix || printf('%0'||noofdigit||'d', '"+bookingno+"')||suffix " +
                    " FROM tblvouchersettings where type='salesorder' and " +
                    "billtypecode='"+billtypecode+"' and vancode='"+ preferenceMangr.pref_getString("getvancode") +"'  " +
                    "and financialyearcode='"+ financialyearcode +"')" +
                    "end else" +
                    "(select prefix || printf('%0'||noofdigit||'d', '"+bookingno+"')||suffix " +
                    "FROM tblvouchersettings where type='salesorder' and billtypecode='"+billtypecode+"' and " +
                    "vancode='"+ preferenceMangr.pref_getString("getvancode") +"' and financialyearcode='"+ financialyearcode +"')" +
                    " end as billno";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur.getString(0);
    }

    //Get Close cah details
    public String GetCashClose(String getschedulecode)
    {
        String getcashcount = "0";
        try{
            String sql ="select  coalesce(count(*),0) from tblclosecash where schedulecode = '"+getschedulecode+"' and status='2'  ";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getcashcount =  mCur.getString(0);
            }else{
                getcashcount =  "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getcashcount;
    }

    //Get Close cah details
    public String GetCashCloseCount(String getschedulecode)
    {
        String getcashcount = "0";
        try{
            String sql ="select  coalesce(count(*),0) from tblclosecash where schedulecode = '"+getschedulecode+"'    ";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getcashcount =  mCur.getString(0);
            }else{
                getcashcount =  "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getcashcount;
    }

    //Get GetDenominationCount
    public Double GetDenominationCount(String getschedulecode)
    {
        Double getcashcount = 0.0;
        try{
            String sql ="select  coalesce(count(*),0) from tbldenomination where schedulecode = '"+getschedulecode+"' ";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getcashcount =  mCur.getDouble(0);
            }else{
                getcashcount =  0.0;
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getcashcount;
    }

    //Get Close sales details
    public String GetSalesClose(String getschedulecode)
    {
        String getcashcount = "0";
        try{
            String sql ="select  coalesce(count(*),0) from tblclosesales where schedulecode = '"+getschedulecode+"' ";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getcashcount =  mCur.getString(0);
            }else{
                getcashcount =  "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getcashcount;
    }

    //Get Close sales details
    public String GetReceiptCount(String getschedulecode)
    {
        String getcashcount = "0";
        try{
            String sql ="select  coalesce(count(*),0) from tblreceipt where schedulecode = '"+getschedulecode+"' and type='Receipt'";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getcashcount =  mCur.getString(0);
            }else{
                getcashcount =  "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getcashcount;
    }


    //Get Close sales details
    public String GetExpenseCount(String getschedulecode)
    {
        String getcashcount = "0";
        try{
            String sql ="select  coalesce(count(*),0) from tblexpenses where schedulecode = '"+getschedulecode+"' ";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getcashcount =  mCur.getString(0);
            }else{
                getcashcount =  "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getcashcount;
    }

    //Get Close sales details
    public String GetCountCashClose(String getschedulecode)
    {
        String getcashcount = "0";
        try{
            String sql ="select  coalesce(count(*),0) from tblclosecash where schedulecode = '"+getschedulecode+"' and status='2' ";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getcashcount =  mCur.getString(0);
            }else{
                getcashcount =  "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getcashcount;
    }

    //Get wish msg
    public String GetWishmsg()
    {
        String getmsg = "";
        try{
            String sql ="select  coalesce(wishmsg,'') from tblgeneralsettings limit 1 ";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getmsg =  mCur.getString(0);
            }else{
                getmsg =  "";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getmsg;
    }
    //Get otp_time_validity
    public String Getotptimevalidity()
    {
        String getmsg = "";
        try{
            String sql ="select  otp_time_validity  from tblgeneralsettings limit 1 ";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getmsg =  mCur.getString(0);
            }else{
                getmsg =  "";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getmsg;
    }
    //Get otp_time_validity_backend
    public String Getotptimevaliditybackend()
    {
        String getmsg = "";
        try{
            String sql ="select  otp_time_validity_backend from tblgeneralsettings limit 1 ";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getmsg =  mCur.getString(0);
            }else{
                getmsg =  "";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getmsg;
    }
    //Get Tranasaction No
    public String GetTransactionNo()
    {
        Cursor mCur = null;
        try{
            String sql ="select  case when (select Count(*) from tblsales)=0 then case when " +
                    "(select coalesce(max(transactionno),1) from tblmaxcode where code=22  )=1 then " +
                    " coalesce(max(transactionno),0)+1 " +
                    "else (select coalesce(max(transactionno),0)+1 from tblmaxcode where code=22 ) end " +
                    " else  coalesce(max(transactionno),0)+1  " +
                    "end as transactionno from tblsales as  a ; ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur.getString(0);
    }

    //Get Tranasaction No
    public String GetSalesOrderTransactionNo()
    {
        Cursor mCur = null;
        try{
            String sql ="select  case when (select Count(*) from tblsalesorder)=0 then case when " +
                    "(select coalesce(max(transactionno),1) from tblmaxcode where code=77  )=1 then " +
                    " coalesce(max(transactionno),0)+1 " +
                    "else (select coalesce(max(transactionno),0)+1 from tblmaxcode where code=77 ) end " +
                    " else  coalesce(max(transactionno),0)+1  " +
                    "end as transactionno from tblsalesorder as  a ; ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur.getString(0);
    }
    //Get Van Datas form database for each imeino
    public String GetDrildownGroupStatusDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select coalesce(drilldownitem,'no') from tblgeneralsettings ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur.getString(0);
    }
    public String GetOrderDrildownGroupStatusDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select coalesce(drilldownorder,'no') from tblgeneralsettings ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur.getString(0);
    }
    //Get schedule settings
    public String GetScheduleStatusDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select coalesce(salesschedulemobileapp,'yes') from tblgeneralsettings ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur.getString(0);
    }
    //Get Customer settings
    public String GetCustomerStatusDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select coalesce(enableallcustomersmobileapp,'no') from tblgeneralsettings ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur.getString(0);
    }
    //Get schedule settings
    public String GetBillCopyDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select coalesce(billcopypopup,'yes') from tblgeneralsettings ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur.getString(0);
    }

    //Get Receipt remarks
    public Cursor GetReceiptRemarks()
    {
        Cursor mCur = null;
        try{
            String sql ="select receiptremarks,receiptremarkscode from tblreceiptremarks;";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get area based on route
    public Cursor GetAreaDB(String routecode,int category)
    {
        Cursor mCur = null;
        try{
            String getbusinesstype = "";
            if(preferenceMangr.pref_getString("getbusiness_type").equals("2")){
                getbusinesstype=" (b.business_type = 2 or b.business_type = 3) ";
            }else  if(preferenceMangr.pref_getString("getbusiness_type").equals("1")){
                getbusinesstype="(b.business_type = 1 or b.business_type = 3)";
            }else{
                if(category == Constants.CUSTOMER_CATEGORY_SALES)
                    getbusinesstype = "(business_type='1' or business_type='3')";
                if(category == Constants.CUSTOMER_CATEGORY_ORDER)
                    getbusinesstype = "(business_type='2' or business_type='3')";
                if(category == Constants.CUSTOMER_CATEGORY_BOTH)
                    getbusinesstype = "(business_type='1' or business_type='2' or business_type='3')";
                //getbusinesstype="(b.business_type = 1 or  b.business_type = 2 or b.business_type = 3)";
            }
            String sql ="select distinct a.areacode,areaname,areanametamil,noofkm,a.citycode,c.citynametamil," +
                    " (select coalesce(count(*),0) as count from tblcustomer as b where areacode=a.areacode " +
                    " and "+getbusinesstype+") as customercount " +
                    "from tblareamaster as a inner join tblroutedetails as b on a.areacode=b.areacode  " +
                    "inner join tblcitymaster as c on c.citycode=a.citycode where routecode='"+routecode+"' " +
                    "and a.status='"+statusvar+"' and c.status='"+statusvar+"' order by cast(b.areaserialno as integer) asc ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get area based on route
    public Cursor GetTransportDB(String getareacode,String gettransportmodecode)
    {
        Cursor mCur = null;
        try{
            String getdate = GenCreatedDate();
            String sqlc =  "select case cast (strftime('%w', '"+getdate+"') as integer) " +
                    " when 0 then 'Sunday' when 1 then 'Monday' when 2 then 'Tuesday' when 3 then 'Wednesday' when 4 then 'Thursday' when 5 then " +
                    " 'Friday' else 'Saturday' end as dayname" ;
            Cursor mCurc = mDb.rawQuery(sqlc, null);
            mCurc.moveToFirst();
            String getdayname= (mCurc.moveToFirst())?mCurc.getString(0):"Sunday";


            String sql ="select a.transportid,a.transportname,a.vechicle_number,b.day_of_dispatch from tbltransportmaster as a inner join" +
                    "  tbltransportcitymapping as b on a.transportid=b.transportid" +
                    " where a.transportmodecode='"+gettransportmodecode+"' and " +
                    " b.citycode= (select citycode from tblareamaster where areacode='"+getareacode+"')   ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get area based on route
    public Cursor GetTransportModeDB()
    {
        Cursor mCur = null;
        try{


            String sql ="select transportmodecode,transportmodetype from tbltransportmode order by transportmodetype asc";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get Area Customer List
    public Cursor GetCustomerListAreaDB(String routecode)
    {
        Cursor mCur = null;
        try{
            String getbusinesstype="";
            if(preferenceMangr.pref_getString("getbusiness_type").equals("2")){
                getbusinesstype=" (business_type = 2 or business_type = 3) ";
            }else  if(preferenceMangr.pref_getString("getbusiness_type").equals("1")){
                getbusinesstype="(business_type = 1 or business_type = 3)";
            }else{
                getbusinesstype="(business_type = 1 or  business_type = 2 or business_type = 3)";
            }
            String sql ="select '0' as areacode,'All Areas' as areaname,'All Areas' as areanametamil,'0' as noofkm," +
                    " '0' as citycode,'' as citynametamil,'0' as customercount,'0' as areaserialno  " +
                    " union all " +
                    " select * from(select distinct a.areacode,areaname,areanametamil,noofkm,a.citycode,c.citynametamil," +
                    " (select coalesce(count(*),0) as count from tblcustomer where areacode=a.areacode and "+getbusinesstype+") as customercount," +
                    " cast(areaserialno as integer) as areaserialno  " +
                    "from tblareamaster as a inner join tblroutedetails as b on a.areacode=b.areacode  " +
                    "inner join tblcitymaster as c on c.citycode=a.citycode where routecode='"+routecode+"' " +
                    "and a.status='"+statusvar+"' and c.status='"+statusvar+"' order by areaserialno asc ) as dev ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get Receipt Area List
    public Cursor GetReceiptAreaDB(String routecode)
    {
        Cursor mCur = null;
        try{
            String sql ="select '0' as areacode,'All Areas' as areaname,'All Areas' as areanametamil," +
                    " '0' as noofkm,'0' as citycode,'' as citynametamil,'' as customercount union all" +
                    " select distinct a.areacode,areaname,areanametamil,noofkm,a.citycode,c.citynametamil," +
                    " (select coalesce(count(*),0) as count from tblcustomer where areacode=a.areacode) as customercount " +
                    "from tblareamaster as a inner join tblroutedetails as b on a.areacode=b.areacode  " +
                    "inner join tblcitymaster as c on c.citycode=a.citycode where routecode='"+routecode+"' " +
                    "and a.status='"+statusvar+"' and c.status='"+statusvar+"' ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get area based on city
    public Cursor GetAreaCityDB(String getcitycode,String getroutecode)
    {
        Cursor mCur = null;
        try{
            String getbusinesstype="";
            if(preferenceMangr.pref_getString("getbusiness_type").equals("2")){
                getbusinesstype=" (business_type = 2 or business_type = 3) ";
            }else  if(preferenceMangr.pref_getString("getbusiness_type").equals("1")){
                getbusinesstype="(business_type = 1 or business_type = 3)";
            }else{
                getbusinesstype="(business_type = 1 or  business_type = 2 or business_type = 3)";
            }
            /*String sql ="select distinct a.areacode,areaname,areanametamil,noofkm,a.citycode,c.cityname," +
                    " (select coalesce(count(*),0) as count from tblcustomer where areacode=a.areacode and  "+getbusinesstype+") as customercount " +
                    "from tblareamaster as a inner join tblroutedetails as b on a.areacode=b.areacode  " +
                    "inner join tblcitymaster as c on c.citycode=a.citycode where routecode='"+getroutecode+"' " +
                    " and a.citycode='"+getcitycode+"' " +
                    "and a.status='"+statusvar+"' and c.status='"+statusvar+"' order by areanametamil ";*/

            String sql ="select distinct a.areacode,areaname,areanametamil,noofkm,a.citycode,c.cityname," +
                    " (select coalesce(count(*),0) as count from tblcustomer where areacode=a.areacode and  "+getbusinesstype+") as customercount " +
                    " from tblareamaster as a " +
                    " inner join tblroutedetails as b on a.areacode=b.areacode  " +
                    " inner join tblcitymaster as c on c.citycode=a.citycode " +
                    " where a.citycode='"+getcitycode+"' " +
                    " and a.status='"+statusvar+"' and c.status='"+statusvar+"' order by areanametamil ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get City based on route
    public Cursor GetCityDB(String routecode)
    {
        Cursor mCur = null;
        try{
            /*String sql ="select distinct a.citycode,a.cityname,a.citynametamil " +
                    "from tblcitymaster as a " +
                    "inner join tblareamaster as b on a.citycode=b.citycode " +
                    "where b.areacode in (select areacode from tblroutedetails where routecode='"+routecode+"') and " +
                    " a.status='"+statusvar+"' and b.status='"+statusvar+"' order by a.citynametamil ; ";*/
            String sql ="select distinct a.citycode,a.cityname,a.citynametamil " +
                    "from tblcitymaster as a " +
                    "inner join tblareamaster as b on a.citycode=b.citycode " +
                    "where a.status='"+statusvar+"' and b.status='"+statusvar+"' order by a.citynametamil ; ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get item group
    public Cursor GetOrderItemgroupDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select '0' as itemgroupcode,'All Item Group' as itemgroupname,'All Item Group' as itemgroupnametamil " +
                    " union all " +
                    " select itemgroupcode,itemgroupname,itemgroupnametamil" +
                    " from tblitemgroupmaster where status='"+statusvar+"' order by itemgroupcode desc ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }


    //Get item sub group
    public Cursor GetOrderItemSubgroupDB(String getitemgroupcode)
    {
        Cursor mCur = null;
        try{
            String itemgroupcode="";
            if(getitemgroupcode.equals("0")){
                itemgroupcode = "1=1";
            }else{
                itemgroupcode = "itemgroupcode='"+getitemgroupcode+"'";
            }
            String sql ="select '0' as itemsubgroupcode,'All Item Sub-Group' as itemsubgroupname,'All Item Sub-Group' as  " +
                    " itemsubgroupnametamil union all " +
                    " select itemsubgroupcode,itemsubgroupname,itemsubgroupnametamil " +
                    " from tblitemsubgroupmaster where status='"+statusvar+"' and "+itemgroupcode+" "+
                    "order by itemsubgroupcode desc " ;
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Sales Return List
    public Cursor GetSalesReturnListDB(String getdate,String getcompanycode,String paymenttype,String paymentstatus)
    {
        Cursor mCur = null;
        try{
            String sql="";
            String getbilltype="";
            String getcompany = "";
            if(paymenttype.equals("All Bills")){
                paymenttype = "0";
            }else if(paymenttype.equals("Cash")){
                paymenttype ="1";
            }else{
                paymenttype ="2";
            }


            if(paymenttype.equals("0")){
                getbilltype = "1=1";
            }if(paymenttype.equals("1")){
                getbilltype = "billtypecode=1";
            }if(paymenttype.equals("2")){
                getbilltype = "billtypecode=2";
            }

            if(getcompanycode.equals("0") || getcompanycode.equals("")) {
                getcompany = "1=1";
            }else{
                getcompany="companycode='"+getcompanycode+"'";
            }
            sql = "select companycode,vancode,transactionno,billno,refno,prefix,suffix," +
                    "strftime('%d-%m-%Y',billdate) as billdate,customercode," +
                    " billtypecode,gstin,schedulecode," +
                    "subtotal,discount,grandtotal,billcopystatus,cashpaidstatus,financialyearcode,bookingno,flag," +
                    "(select customername from tblcustomer where customercode=a.customercode) as customername," +
                    "(select customernametamil from tblcustomer where customercode=a.customercode) as customernametamil ," +
                    "(select areanametamil from tblareamaster where areacode=(select areacode from tblcustomer where customercode=a.customercode )) as areaname," +
                    "(select citynametamil from tblcitymaster where citycode=(select citycode from tblareamaster where areacode=" +
                    "(select areacode from tblcustomer where customercode=a.customercode ))) as cityname," +
                    "(select shortname from tblcompanymaster where companycode=a.companycode) as shortname,syncstatus " +
                    "from tblsalesreturn as a where billdate = datetime('" + getdate + "') " +
                    "and vancode = '" + preferenceMangr.pref_getString("getvancode") + "' and  " +
                    " "+getcompany+" and "+getbilltype+"  order by transactionno desc ";

            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Sales Return List
    public Cursor GetSalesPrintReturnListDB(String getdate,String getcompanycode,String paymenttype,String paymentstatus)
    {
        Cursor mCur = null;
        try{
            String sql="";
            String getbilltype="";
            String getcompany = "";
            if(paymenttype.equals("All Bills")){
                paymenttype = "0";
            }else if(paymenttype.equals("Cash")){
                paymenttype ="1";
            }else{
                paymenttype ="2";
            }


            if(paymenttype.equals("0")){
                getbilltype = "1=1";
            }if(paymenttype.equals("1")){
                getbilltype = "billtypecode=1";
            }if(paymenttype.equals("2")){
                getbilltype = "billtypecode=2";
            }

            if(getcompanycode.equals("0") || getcompanycode.equals("")) {
                getcompany = "1=1";
            }else{
                getcompany="companycode='"+getcompanycode+"'";
            }
            sql = "select companycode,vancode,transactionno,billno,refno,prefix,suffix," +
                    "strftime('%d-%m-%Y',billdate) as billdate,customercode," +
                    " billtypecode,gstin,schedulecode," +
                    "subtotal,discount,grandtotal,billcopystatus,cashpaidstatus,financialyearcode,bookingno,flag," +
                    "(select customername from tblcustomer where customercode=a.customercode) as customername," +
                    "(select customernametamil from tblcustomer where customercode=a.customercode) as customernametamil ," +
                    "(select areanametamil from tblareamaster where areacode=(select areacode from tblcustomer where customercode=a.customercode )) as areaname," +
                    "(select citynametamil from tblcitymaster where citycode=(select citycode from tblareamaster where areacode=" +
                    "(select areacode from tblcustomer where customercode=a.customercode ))) as cityname," +
                    "(select shortname from tblcompanymaster where companycode=a.companycode) as shortname " +
                    "from tblsalesreturn as a where billdate = datetime('" + getdate + "') " +
                    "and vancode = '" + preferenceMangr.pref_getString("getvancode") + "' and   a.flag!=3 and a.flag!=6 and  " +
                    " "+getcompany+" and "+getbilltype+"  order by transactionno desc ";

            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Sales Return List
    public Cursor GetSalesOrderPrintListDB(String getdate)
    {
        Cursor mCur = null;
        try{
            String sql="";

            sql = "select companycode,vancode,transactionno,billno,refno,prefix,suffix," +
                    "strftime('%d-%m-%Y',billdate) as billdate,customercode," +
                    " billtypecode,gstin,schedulecode," +
                    "subtotal,discount,grandtotal,billcopystatus,cashpaidstatus,financialyearcode,bookingno,flag," +
                    "(select customername from tblcustomer where customercode=a.customercode) as customername," +
                    "(select customernametamil from tblcustomer where customercode=a.customercode) as customernametamil ," +
                    "(select areanametamil from tblareamaster where areacode=(select areacode from tblcustomer where customercode=a.customercode )) as areaname," +
                    "(select citynametamil from tblcitymaster where citycode=(select citycode from tblareamaster where areacode=" +
                    "(select areacode from tblcustomer where customercode=a.customercode ))) as cityname," +
                    "(select shortname from tblcompanymaster where companycode=a.companycode) as shortname " +
                    "from tblsalesorder as a where billdate = datetime('" + getdate + "') " +
                    "and vancode = '" + preferenceMangr.pref_getString("getvancode") + "' and   a.flag!=3 and a.flag!=6    " +
                    "   order by transactionno desc ";

            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get close sales report
    public Cursor GetCloseSalesitemDatasDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select * from tblclosesales where flag=1  ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get NilStockDetail
    public Cursor GetNilStockDetailsDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select * from tblnilstocktransaction where syncflag=0  ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get NilStockDetail
    public Cursor GetWholeNilStockDetailsDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select * from tblnilstocktransaction    ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get Sales Return List data Details
    public Cursor GetSalesReturnListDatasDB(String gettransactionno,String getfinancialyr,String getcompanycode)
    {
        Cursor mCur = null;
        try{
            String sql ="select companycode,vancode,transactionno,billno,refno,prefix,suffix," +
                    "strftime('%d-%m-%Y',billdate) as billdate,customercode," +
                    " billtypecode,gstin,schedulecode," +
                    "subtotal,discount,grandtotal,billcopystatus,cashpaidstatus,financialyearcode,bookingno,flag," +
                    "(select customername from tblcustomer where customercode=a.customercode) as customername," +
                    "(select customernametamil from tblcustomer where customercode=a.customercode) as customernametamil ," +
                    "(select areanametamil from tblareamaster where areacode=(select areacode from tblcustomer where customercode=a.customercode )) as areaname," +
                    "(select citynametamil from tblcitymaster where citycode=(select citycode from tblareamaster where areacode=" +
                    "(select areacode from tblcustomer where customercode=a.customercode ))) as cityname," +
                    "(select shortname from tblcompanymaster where companycode=a.companycode) as shortname," +
                    " (select sum(grandtotal) from tblsalesreturn as b where transactionno = '"+gettransactionno+"' and financialyearcode = '"+getfinancialyr+"' ) as totalamt " +
                    " from tblsalesreturn as a where transactionno = '"+gettransactionno+"' " +
                    " and financialyearcode = '"+getfinancialyr+"' and companycode='"+getcompanycode+"'  ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }


    //Get Sales Return List data Details
    public Cursor GetSalesReturnListItemDatasDB(String gettransactionno,String getfinancialyr,String getcompanycode)
    {
        Cursor mCur = null;
        try{
            String sql ="select a.itemcode,b.itemnametamil,a.qty,a.weight,a.price,a.discount,a.amount,a.freeitemstatus ," +
                    "(select unitname from tblunitmaster where unitcode=b.unitcode) as unitname," +
                    "(select hsn from tblitemsubgroupmaster where itemsubgroupcode=b.itemsubgroupcode) as hsn," +
                    "(select tax from tblitemsubgroupmaster where itemsubgroupcode=b.itemsubgroupcode) as tax," +
                    " coalesce((Select noofdecimals from tblunitmaster where unitcode=b.unitcode),0) as noofdecimals," +
                    " CASE WHEN itemtype=2 then (SELECT freeitemcolor from tblgeneralsettings) " +
                    " else coalesce((Select colourcode from tblcompanymaster where companycode=a.companycode),'#000000')  END as colourcode" +
                    " from tblsalesreturnitemdetails as a inner join tblitemmaster as b on a.itemcode=b.itemcode" +
                    " where a.transactionno='"+gettransactionno+"' and a.companycode='"+getcompanycode+"' " +
                    " and a.financialyearcode='"+getfinancialyr+"' order by itemtype ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }


    //Get Sales Return cancel Datas
    public Cursor GetSalesReturnCancelDatasDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select * from tblsalesreturn where flag=3 ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get group based on sub group return
    public Cursor GetSubGroup_GroupDBSalesReturn(String itemgroupcode)
    {
        Cursor mCur = null;
        try{
            getdate = GenCreatedDate();
            String sql ="select distinct c.itemsubgroupcode,c.itemsubgroupname,c.itemsubgroupnametamil from " +
                    "  tblitemmaster  as b inner join  " +
                    "tblitemsubgroupmaster as c on c.itemsubgroupcode=b.itemsubgroupcode where " +
                    " c.itemgroupcode='"+itemgroupcode+"' " +
                    " and b.status='"+statusvar+"' and c.Status='"+statusvar+"' order by c.itemsubgroupnametamil";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    public String insertReceipt(String receiptdate,String companycode,
                                String vancode,String customercode,String schedulecode,String receiptremarkscode,
                                String receiptmode,
                                String chequerefno,String amount,String financialyearcode,String note ,
                                String chequebankname,String chequedate, String venderid, String upitransactionID)

    {
        String transno= "1";
        try{
            String Gencode= GenCreatedDate();
            mDb = mDbHelper.getReadableDatabase();
            String getcurtime = GetDateTime();


            String sqlc = "select  count(*) from tblreceipt " ;
            Cursor mCurc = mDb.rawQuery(sqlc, null);
            mCurc.moveToFirst();
            String getcount= (mCurc.moveToFirst())?mCurc.getString(0):"0";


            if(getcount.equals("0")){
                String sql1 = "select coalesce(transactionno,0) from tblmaxcode where code=4  ";
                Cursor mCur = mDb.rawQuery(sql1, null);
                mCur.moveToFirst();
                transno = (mCur.moveToFirst()) ? mCur.getString(0) : "0";
                if(transno.equals("0")){
                    transno ="1";
                }
            }else {
                String sql1 = "select coalesce(max(transactionno),0)+1 from tblreceipt";
                Cursor mCur = mDb.rawQuery(sql1, null);
                mCur.moveToFirst();
                transno = (mCur.moveToFirst()) ? mCur.getString(0) : "0";
            }
            String sql="INSERT INTO tblreceipt(autonum,transactionno,receiptdate,prefix,suffix,voucherno,refno," +
                    "companycode,vancode," +
                    "customercode,schedulecode,receiptremarkscode,receiptmode,chequerefno,amount," +
                    "makerid,createddate,financialyearcode," +
                    "flag,note,syncstatus,receipttime,chequebankname,chequedate,transactionid,venderid,type) VALUES ((select coalesce(max(autonum),0)+1 from tblreceipt),'"+ transno +"',datetime('"+ receiptdate +"')," +
                    "(select prefix FROM tblvouchersettings where type='receipt'  and vancode='"+ vancode +"' " +
                    "and companycode='"+ companycode +"' and financialyearcode='"+ financialyearcode +"')  ," +
                    "(select suffix FROM tblvouchersettings where type='receipt'  and vancode='"+ vancode +"' " +
                    "and companycode='"+ companycode +"' and financialyearcode='"+ financialyearcode +"')  ," +
                    "case when (select Count(*) from tblreceipt where companycode='"+companycode+"'" +
                    " AND type='Receipt')=0 then " +
                    " case when (select coalesce(max(refno),1) " +
                    "from tblmaxcode where code=44 and companycode='"+companycode+"' )=1 then " +
                    "(select prefix || printf('%0'||noofdigit||'d', startingno)||suffix FROM tblvouchersettings " +
                    "where type='receipt'  and vancode='"+ vancode +"' and companycode='"+ companycode +"' " +
                    "and financialyearcode='"+ financialyearcode +"')" +
                    "else" +
                    "(select prefix || printf('%0'||noofdigit||'d', (select coalesce(max(refno),0) from tblmaxcode " +
                    "where code=44 and companycode='"+companycode+"'))||suffix FROM tblvouchersettings where type='receipt'  and vancode='"+ vancode +"' " +
                    "and companycode='"+ companycode+"' and financialyearcode='"+ financialyearcode +"')" +
                    "end else" +
                    "(select prefix || printf('%0'||noofdigit||'d', (select coalesce(max(refno),0)+1 from tblreceipt where  " +
                    " companycode='"+ companycode+"' and financialyearcode='"+ financialyearcode +"' AND type='Receipt' ))||suffix FROM " +
                    " tblvouchersettings where type='receipt'  and vancode='"+ vancode +"' and companycode='"+ companycode +"'" +
                    " and financialyearcode='"+ financialyearcode +"')" +
                    " end  ,case when (select Count(*) from tblreceipt  where companycode='"+companycode+"' AND type='Receipt')=0 then " +
                    " case when (select coalesce(max(refno),1) " +
                    " from tblmaxcode where code=44 and companycode='"+companycode+"')=1 then " +
                    " (select  printf('%0'||noofdigit||'d', startingno) FROM tblvouchersettings where type='receipt'  " +
                    " and vancode='"+ vancode +"' and companycode='"+ companycode +"' and financialyearcode='"+ financialyearcode +"')" +
                    " else " +
                    " (select printf('%0'||noofdigit||'d', (select coalesce(max(refno),0)  from tblmaxcode where code=44 and companycode='"+companycode+"')) " +
                    " FROM tblvouchersettings where type='receipt' and vancode='"+ vancode +"' and companycode='"+ companycode +"'" +
                    " and financialyearcode='"+ financialyearcode +"')" +
                    "end else" +
                    "(select printf('%0'||noofdigit||'d', (select coalesce(max(refno),0)+1 from tblreceipt where " +
                    " companycode='"+ companycode+"' and financialyearcode='"+ financialyearcode +"' AND type='Receipt')) FROM tblvouchersettings " +
                    "where type='receipt'  and vancode='"+ vancode +"' and companycode='"+ companycode +"' and " +
                    " financialyearcode='"+ financialyearcode +"')" +
                    " end  ,'"+ companycode +"','"+ vancode +"','"+ customercode +"','"+ schedulecode +"'," +
                    " '"+ receiptremarkscode +"','"+ receiptmode +"','"+ chequerefno +"'," +
                    "'"+ amount +"','1',datetime('now', 'localtime'),'"+ financialyearcode +"','1'," +
                    "'"+ note +"',0,'"+getcurtime+"','"+chequebankname+"','"+chequedate+"','"+ upitransactionID + "'," +
                    " '"+ venderid +"', 'Receipt')";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "insertReceipt", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return transno;
    }
    //Update Sales Return Stock transaction
    public String UpdateSalesReturnStockTransaction(String gettransactiono,String getfinancialyrcode)
    {
        try{
            String GetDate= GenCreatedDate();
            String sql = " insert into tblstocktransaction (transactionno,transactiondate,vancode,itemcode,inward,outward,type,refno,flag,createddate,companycode,op,financialyearcode,autonum)" +
                    "select (select coalesce(max(transactionno),0)+1 from tblstocktransaction),datetime('"+GetDate+"')," +
                    "vancode,itemcode,'-'||qty,0,'salesreturncancel',transactionno,1,datetime('now', 'localtime'),companycode,0,'"+getfinancialyrcode+"',(select coalesce(max(autonum),0)+1 from tblstocktransaction)+autonum " +
                    " from tblsalesreturnitemdetails where transactionno='"+gettransactiono+"' " +
                    "and financialyearcode='"+getfinancialyrcode+"'  ";
            mDb.execSQL(sql);


            String sql1 = " insert into tblappstocktransactiondetails (transactionno,transactiondate,vancode,itemcode,inward,outward,type,refno,flag,createddate,companycode,op,financialyearcode,syncstatus)" +
                    "select (select coalesce(max(transactionno),0)+1 from tblappstocktransactiondetails),datetime('"+GetDate+"')," +
                    "vancode,itemcode,'-'||qty,0,'salesreturncancel',transactionno,1,datetime('"+GetDate+"'),companycode,0,'"+getfinancialyrcode+"',0 " +
                    " from tblsalesreturnitemdetails where transactionno='"+gettransactiono+"' " +
                    "and financialyearcode='"+getfinancialyrcode+"'  ";
            mDb.execSQL(sql1);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName()  + "UpdateSalesReturnStockTransaction", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

      /*  String sql = " update  tblstocktransaction set flag=3 " +
                " where refno='"+gettransactiono+"' " +
                "and financialyearcode='"+getfinancialyrcode+"' and type='salesreturn'  ";
        mDb.execSQL(sql);

        String sql1 = " update  tblappstocktransactiondetails set flag=3 and syncstatus=0 " +
                " where refno='"+gettransactiono+"' " +
                "and financialyearcode='"+getfinancialyrcode+"' and type='salesreturn'  ";
        mDb.execSQL(sql1);*/
        return "success";
    }

    //stock checking for return cancel
    public String StockChecking(String paraTransactionNo,String parafinancialyearcode)
    {
        String getcount = "0";
        try{
            String sql ="select count(*) from (" +
                    "select itemcode,sum(op)+sum(inward)-sum(outward) as closing from tblstocktransaction where " +
                    "datetime(transactiondate)<=datetime('now', 'localtime')  and flag!=3 group by itemcode) as derv inner join (" +
                    "select itemcode,sum(qty) as returnstock from tblsalesreturnitemdetails where transactionno='"+paraTransactionNo+"' and financialyearcode='"+parafinancialyearcode+"' and flag!=3 group by itemcode) as returnval on " +
                    "derv.itemcode=returnval.itemcode where closing-returnstock<0";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getcount = mCur.getString(0);
            }else{
                getcount = "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName()  + "StockChecking", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getcount;
    }



    //Update Sales Return Cancel Flag
    public String UpdateSalesReturnCancelFlag(String gettransactiono,String getfinancialyrcode)
    {
        try{
            String sql = "update tblsalesreturn set flag=3,syncstatus=0 where transactionno='"+gettransactiono+"' and " +
                    " financialyearcode='"+getfinancialyrcode+"' and vancode='"+ preferenceMangr.pref_getString("getvancode") +"'  ";
            mDb.execSQL(sql);

            String sql1 = "update tblsalesreturnitemdetails set flag=3 where transactionno='"+gettransactiono+"' and " +
                    " financialyearcode='"+getfinancialyrcode+"' and vancode='"+ preferenceMangr.pref_getString("getvancode") +"'  ";
            mDb.execSQL(sql1);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName()  + "UpdateSalesReturnCancelFlag", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return "success";
    }

    //Update Sales Return Flag
    public void UpdateCancelSalesReturnFlag(String gettransactiono)
    {
        try{
            String sql = "update tblsalesreturn set flag=6,syncstatus=1 where transactionno='" + gettransactiono + "' and flag=3 ";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName()  + "UpdateCancelSalesReturnFlag", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }


    }
    //Get item sub group
    public Cursor GetOrderItemDB(String getitemgroupcode,String getsubitemgroupcode)
    {
        Cursor mCur = null;
        try{
            String itemsubgroupcode="";
            if(getsubitemgroupcode.equals("0")){
                itemsubgroupcode = "1=1";
            }else{
                itemsubgroupcode = "a.itemsubgroupcode='"+getsubitemgroupcode+"'";
            }
            String itemgroupcode="";
            if(getitemgroupcode.equals("0")){
                itemgroupcode = "1=1";
            }else{
                itemgroupcode = "c.itemgroupcode='"+getitemgroupcode+"'";
            }
            String sql ="select a.itemcode,a.itemname,a.itemnametamil,a.unitweight," +
                    "a.companycode,CASE WHEN itemtype=2 then (SELECT freeitemcolor from tblgeneralsettings) else" +
                    " (select colourcode from tblcompanymaster where companycode=a.companycode) END as colourcode," +
                    "(select unitname from tblunitmaster where unitcode=a.unitcode) as unitname," +
                    "(select hsn from tblitemsubgroupmaster where itemsubgroupcode=a.itemsubgroupcode) as hsn," +
                    "(select tax from tblitemsubgroupmaster where itemsubgroupcode=a.itemsubgroupcode) as tax " +
                    ",(select coalesce(sum(op)+sum(inward)-sum(outward),0) from tblstocktransaction" +
                    " where itemcode=a.itemcode and flag!=3) as stock,a.uppweight" +
                    " from tblitemmaster as a inner join tblitemsubgroupmaster as c on a.itemsubgroupcode=c.itemsubgroupcode " +
                    " inner join tblbrandmaster as d on a.brandcode=d.brandcode " +
                    " where a.status='"+statusvar+"' and "+itemgroupcode+" and "+itemsubgroupcode+" group  by a.itemcode,a.companycode,a.brandcode,a.manualitemcode," +
                    " a.itemname,a.itemnametamil,a.unitcode,a.unitweightunitcode,a.unitweight,a.uppunitcode,a.uppweight," +
                    " a.itemcategory,a.parentitemcode,a.allowpriceedit,a.allownegativestock,a.allowdiscount" +
                    " order by itemtype,c.itemsubgroupname,d.brandname,a.itemcategory desc";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName()  + "GetOrderItemDB", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get Items Sales Return
    public Cursor GetSalesOrderItemDB(String itemsubgroupcode,String getroutecode,String getareacode)
    {
        Cursor mCur = null;
        try{
            String getbusinesstype = "";
//            if(preferenceMangr.pref_getString("getbusiness_type").equals("2")){
//                getbusinesstype=" (b.business_type = 2 or b.business_type = 3) ";
//            }else  if(preferenceMangr.pref_getString("getbusiness_type").equals("1")){
//                getbusinesstype="(b.business_type = 1 or b.business_type = 3)";
//            }else{
//                getbusinesstype="(b.business_type = 1 or  b.business_type = 2 or b.business_type = 3)";
//            }
            String arr[]=preferenceMangr.pref_getString("getbusiness_type").split(",");
            if(arr.length>0) {
                for (int i = 0; i < arr.length; i++) {
                    if(arr[i].equals("2")){
                        getbusinesstype=" ((','||b.business_type||',') LIKE '%,2,%' or (','||b.business_type||',') LIKE '%,3,%') ";
                    }else if(arr[i].equals("1")){
                        getbusinesstype=" ((','||b.business_type||',') LIKE '%,1,%' or (','||b.business_type||',') LIKE '%,3,%') ";
                    }else{
                        getbusinesstype=" ((','||b.business_type||',') LIKE '%,1,%' or (','||b.business_type||',') LIKE '%,2,%' or (','||b.business_type||',') LIKE '%,3,%') ";
                    }
                }
            }
            String getitembusinesstype = "";
//            if(preferenceMangr.pref_getString("getbusiness_type").equals("2")){
//                getitembusinesstype=" (a.business_type = 2 or a.business_type = 3) ";
//            }else  if(preferenceMangr.pref_getString("getbusiness_type").equals("1")){
//                getitembusinesstype="(a.business_type = 1 or a.business_type = 3)";
//            }else{
//                getitembusinesstype="(a.business_type = 1 or  a.business_type = 2 or a.business_type = 3)";
//            }
            if(arr.length>0) {
                for (int i = 0; i < arr.length; i++) {
                    if(arr[i].equals("2")){
                        getitembusinesstype=" (a.business_type = 2 or a.business_type = 3) ";
                    }else if(arr[i].equals("1")){
                        getitembusinesstype=" (a.business_type = 1 or a.business_type = 3) ";
                    }else{
                        getitembusinesstype=" (a.business_type = 1 or  a.business_type = 2 or a.business_type = 3) ";
                    }
                }
            }
            getdate = GenCreatedDate();
            String sql ="select a.itemcode,a.companycode,a.brandcode,a.manualitemcode,a.itemname,a.itemnametamil,a.unitcode," +
                    "a.unitweightunitcode,a.unitweight,a.uppunitcode,a.uppweight,a.itemcategory,a.parentitemcode," +
                    "a.allowpriceedit,a.allownegativestock,a.allowdiscount, '0' as stockqty," +
                    "(Select unitname  from tblunitmaster where unitcode=a.unitcode) as unitname," +
                    "coalesce((Select noofdecimals from tblunitmaster where unitcode=a.unitcode),0) as noofdecimals," +
                    "coalesce((select oldorderprice from tblitempricelisttransaction where itemcode=a.itemcode " +
                    "order by autonum desc limit 1),0) as oldorderprice,coalesce((select neworderprice from tblitempricelisttransaction" +
                    " where itemcode=a.itemcode order by autonum desc limit 1),0) as neworderprice," +
                    "CASE WHEN itemtype=2 then (SELECT freeitemcolor from tblgeneralsettings) else coalesce((select colourcode " +
                    "from tblcompanymaster where companycode=a.companycode),'#000000') END as colourcode,coalesce(c.hsn,'') " +
                    "as hsn,coalesce(c.tax,'') as tax,(select allowpriceedit from tblroutedetails where routecode='"+getroutecode+"' and areacode='"+getareacode+"')" +
                    " as routeallowpricedit,case when parentitemcode=0 then a.itemcode else parentitemcode " +
                    " end as parentcode,case when itemcategory='parent' then 1 else  2 end as itemorder, " +
                    "  a.orderstatus,a.maxorderqty ,(select count(*) from tblschemeratedetails as aa inner join " +
                    "  tblscheme as b on aa.schemecode=b.schemecode where aa.itemcode=a.itemcode and b.status='"+statusvar+"' " +
                    "  and (','||multipleroutecode||',') LIKE '%,"+ getroutecode +",%' " +
                    "  and(validityfrom<=datetime('"+getdate+"')) and (ifnull(validityto,'')='' or " +
                    " (validityfrom<=datetime('"+getdate+"') " +
                    " and  validityto>=datetime('"+getdate+"'))) and "+getbusinesstype+" ) as ratecount," +
                    " (select count(*) from tblschemeitemdetails as ab  inner join  tblscheme as b " +
                    " on ab.schemecode=b.schemecode where ab.purchaseitemcode=a.itemcode and b.status='"+statusvar+"' and (','||multipleroutecode||',') " +
                    " LIKE '%,"+getroutecode+",%' and(validityfrom<=datetime('"+getdate+"')) and (ifnull(validityto,'')='' or (validityfrom<=datetime('"+getdate+"') " +
                    " and  validityto>=datetime('"+getdate+"')))  and  "+getbusinesstype+" ) as freecount " +
                    " from tblitemmaster as a   " +
                    " inner join tblitemsubgroupmaster as c on c.itemsubgroupcode=a.itemsubgroupcode" +
                    " inner join tblbrandmaster as d on a.brandcode=d.brandcode  where "+getitembusinesstype+" and " +
                    " a.itemsubgroupcode ='"+itemsubgroupcode+"' and a.status='"+statusvar+"'  and " +
                    " itemtype!=2 group by a.itemcode,a.companycode,a.brandcode,a.manualitemcode," +
                    "a.itemname,a.itemnametamil,a.unitcode,a.unitweightunitcode,a.unitweight,a.uppunitcode,a.uppweight," +
                    "a.itemcategory,a.parentitemcode,a.allowpriceedit,a.allownegativestock,a.allowdiscount" +
                    " order by itemtype,c.itemsubgroupname,d.brandname,a.itemcategory desc ";
            //parentcode,itemorder,a.itemname
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetSalesOrderItemDB", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    public Cursor GetNextdayItemDB(String getitemgroupcode,String getsubitemgroupcode,String Schedulecode)
    {
        Cursor mCur = null;
        try{
            String itemsubgroupcode="";
            if(getsubitemgroupcode.equals("0")){
                itemsubgroupcode = "1=1";
            }else{
                itemsubgroupcode = "a.itemsubgroupcode='"+getsubitemgroupcode+"'";
            }
            String itemgroupcode="";
            if(getitemgroupcode.equals("0")){
                itemgroupcode = "1=1";
            }else{
                itemgroupcode = "c.itemgroupcode='"+getitemgroupcode+"'";
            }
            String sql ="select a.itemcode,a.itemname,a.itemnametamil,a.unitweight," +
                    "a.companycode,(select colourcode from tblcompanymaster where companycode=a.companycode) as colourcode," +
                    "(select unitname from tblunitmaster where unitcode=a.unitcode) as unitname," +
                    "(select hsn from tblitemsubgroupmaster where itemsubgroupcode=a.itemsubgroupcode) as hsn," +
                    "(select tax from tblitemsubgroupmaster where itemsubgroupcode=a.itemsubgroupcode) as tax " +
                    ",(select coalesce(sum(op)+sum(inward)-sum(outward),0) from tblstocktransaction" +
                    " where itemcode=a.itemcode and flag!=3) as stock,a.uppweight,d.qty" +
                    " from tblitemmaster as a inner join tblitemsubgroupmaster as c on" +
                    "  a.itemsubgroupcode=c.itemsubgroupcode inner join tblorderdetails as d on d.itemcode=a.itemcode  " +
                    "where a.status='"+statusvar+"' and  "+itemgroupcode+" and "+itemsubgroupcode+"" +
                    " and d.schedulecode='"+Schedulecode+"' and itemtype=1 ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetNextdayItemDB", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get item sub group
    public Cursor GetPriceItemDB(String getitemgroupcode,String getsubitemgroupcode,
                                 String getitemname,String itemtype)
    {
        Cursor mCur = null;
        try{
            String itemsubgroupcode="";
            if(getsubitemgroupcode.equals("0")){
                itemsubgroupcode = "1=1";
            }else{
                itemsubgroupcode = "a.itemsubgroupcode='"+getsubitemgroupcode+"'";
            }
            String itemgroupcode="";
            if(getitemgroupcode.equals("0")){
                itemgroupcode = "1=1";
            }else{
                itemgroupcode = "c.itemgroupcode='"+getitemgroupcode+"'";
            }

            String getitemtype="";
            if(itemtype.equals("All Items")){
                getitemtype = "1=1";
            }else{
                if(itemtype.equals("Parent")){
                    itemtype = "parent";
                }else{
                    itemtype = "child";
                }
                getitemtype = "a.itemcategory='"+itemtype+"' ";
            }
            String sql ="select a.itemcode,a.itemname,a.itemnametamil,a.unitcode," +
                    "a.companycode,CASE WHEN itemtype=2 then (SELECT freeitemcolor from tblgeneralsettings) else" +
                    " (select colourcode from tblcompanymaster where companycode=a.companycode) END as colourcode," +
                    "(select unitname from tblunitmaster where unitcode=a.unitcode) as unitname," +
                    "c.hsn, c.tax, " +
                    " coalesce((select oldprice from tblitempricelisttransaction where itemcode=a.itemcode " +
                    " order by autonum desc limit 1),0) as oldprice," +
                    " coalesce((select newprice from tblitempricelisttransaction" +
                    " where itemcode=a.itemcode order by autonum desc limit 1),0) as newprice," +
                    " case when parentitemcode=0 then a.itemcode else parentitemcode " +
                    " end as parentcode,case when itemcategory='parent' then 1 else  2 end as itemorder ," +
                    " c.itemsubgroupname,d.brandname,a.itemcategory," +
                    " case when f.createddate  >= datetime('now','-1 day')   then 'pricechanged' else 'nochanges' end as pricetatus,  " +
                    " coalesce((select oldorderprice from tblitempricelisttransaction where itemcode=a.itemcode " +
                    " order by autonum desc limit 1),0) as oldorderprice," +
                    " coalesce((select neworderprice from tblitempricelisttransaction" +
                    " where itemcode=a.itemcode order by autonum desc limit 1),0) as neworderprice " +
                    " from tblitemmaster as a inner join tblitemsubgroupmaster as c on" +
                    " a.itemsubgroupcode=c.itemsubgroupcode  inner join tblbrandmaster as d on a.brandcode=d.brandcode inner join " +
                    " tblitempricelisttransaction as f on f.itemcode=a.itemcode " +
                    "where a.status='"+statusvar+"' and  "+itemgroupcode+" and "+itemsubgroupcode+" and "+getitemtype+" " +
                    " and itemname like '%"+getitemname+"%' order by itemtype,c.itemgroupcode, c.itemsubgroupcode," +
                    " d.brandname,a.itemcategory desc";
            //parentcode,itemorder,a.itemname,
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetPriceItemDB", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    // GetCashNotPaidDetails
    public Cursor GetCashNotPaidDetails()
    {
        Cursor mCur = null;
        try{

            String sql ="SELECT transactionno,bookingno,customercode,grandtotal,customernametamil,schedulecode," +
                    " areanametamil from tblcashnotpaiddetails order by grandtotal desc";
            //parentcode,itemorder,a.itemname,
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName()  + "GetCashNotPaidDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get company
    public Cursor GetCompanyDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select '0' as companycode,'All Company' as companyname,'All Company' as shortname  union all" +
                    " select companycode,companyname,shortname from tblcompanymaster where status='"+statusvar+"' ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetCompanyDB", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get company
    public Cursor GetCompanyNAmeDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select companycode,companyname,shortname,coalesce((SELECT case when localfilepath = '' then " +
                    "downloadpath else localfilepath end as imageurl FROM tblcompanyvenderdetails WHERE " +
                    "companycode = a.companycode and status='"+statusvar+"'),'') AS imageurl from tblcompanymaster AS a " +
                    "where status='"+statusvar+"'" +
                    " order by shortname ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetCompanyNAmeDB", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    public Cursor GetPaymentVenderNAmeDB()
    {
        Cursor mCur = null;
        try{
            String sql ="SELECT venderid,vendername FROM tblupivendermaster WHERE statusid = 1 ORDER BY vendername ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetPaymentVenderNAmeDB", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get Route -- test git
    public Cursor GetRouteDB()
    {
        Cursor mCur = null;
        try{
            String getdate = GenCreatedDate();
            //  String sql ="select routecode,routename,routenametamil,routeday from tblroute where status='"+statusvar+"'" +
            //      " order by routenametamil ";
            String sqlc =  "select case cast (strftime('%w', '"+getdate+"') as integer) " +
                    " when 0 then 'Sunday' when 1 then 'Monday' when 2 then 'Tuesday' when 3 then 'Wednesday' when 4 then 'Thursday' when 5 then " +
                    " 'Friday' else 'Saturday' end as dayname" ;
            Cursor mCurc = mDb.rawQuery(sqlc, null);
            mCurc.moveToFirst();
            String getdayname= (mCurc.moveToFirst())?mCurc.getString(0):"Sunday";

            String bussinessType = "";
            if(preferenceMangr.pref_getString("getbusiness_type").equals("2")) {
                bussinessType = "(business_type='2'  or business_type='3')";
            }else if(preferenceMangr.pref_getString("getbusiness_type").equals("1")){
                bussinessType = "(business_type='1'  or business_type='3')";
            }else{
                bussinessType = "(business_type='1' or business_type='2' or  business_type='3')";
            }
            //and vancode='" + preferenceMangr.pref_getString("getvancode") + "'
            String sql = "  SELECT  routecode,routename,routenametamil,routeday FROM tblroute where status='" + statusvar + "'" +
                    " and " + bussinessType +
                    " and routecode not in (select routecode from tblsalesschedule where vancode='" + preferenceMangr.pref_getString("getvancode") + "' and date(scheduledate)=date('" + getdate + "'))" +
                    " union all SELECT '0' as routecode,'Others' as routename,'Others' as routenametamil,'' as routeday ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetRouteDB", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get Route
    public Cursor GetFirstRouteDB()
    {
        Cursor mCur = null;
        try{
            String getdate = GenCreatedDate();
            //  String sql ="select routecode,routename,routenametamil,routeday from tblroute where status='"+statusvar+"'" +
            //      " order by routenametamil ";
            String sqlc =  "select case cast (strftime('%w', '"+getdate+"') as integer) " +
                    " when 0 then 'Sunday' when 1 then 'Monday' when 2 then 'Tuesday' when 3 then 'Wednesday' when 4 then 'Thursday' when 5 then " +
                    " 'Friday' else 'Saturday' end as dayname" ;
            Cursor mCurc = mDb.rawQuery(sqlc, null);
            mCurc.moveToFirst();
            String getdayname= (mCurc.moveToFirst())?mCurc.getString(0):"Sunday";

            String sql = "";
            if(preferenceMangr.pref_getString("getbusiness_type").equals("2")) {
                sql = "  SELECT  routecode,routename,routenametamil,routeday FROM tblroute where status='" + statusvar + "'" +
                        "  and vancode='" + preferenceMangr.pref_getString("getvancode") + "' and routeday like '%" + getdayname + "%' " +
                        " and (business_type='2'  or business_type='3') limit 1    ";
            }else if(preferenceMangr.pref_getString("getbusiness_type").equals("1")) {
                sql = "  SELECT  routecode,routename,routenametamil,routeday FROM tblroute where status='" + statusvar + "'" +
                        "  and vancode='" + preferenceMangr.pref_getString("getvancode") + "' and routeday like '%" + getdayname + "%' " +
                        " and (business_type='1'  or business_type='3') limit 1    ";
            }else{
                sql = "  SELECT  routecode,routename,routenametamil,routeday FROM tblroute where status='" + statusvar + "'" +
                        "  and vancode='" + preferenceMangr.pref_getString("getvancode") + "' and routeday like '%" + getdayname + "%' " +
                        " and (business_type='1'  or business_type='2' or business_type='3') limit 1    ";
            }

            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetFirstRouteDB", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Route
    public Cursor GetOthersRouteDB()
    {
        Cursor mCur = null;
        try{
            String getdate = GenCreatedDate();
            String sqlc =  "select case cast (strftime('%w', '"+getdate+"') as integer) " +
                    " when 0 then 'Sunday' when 1 then 'Monday' when 2 then 'Tuesday' when 3 then 'Wednesday' when 4 then 'Thursday' when 5 then " +
                    " 'Friday' else 'Saturday' end as dayname" ;
            Cursor mCurc = mDb.rawQuery(sqlc, null);
            mCurc.moveToFirst();
            String getdayname= (mCurc.moveToFirst())?mCurc.getString(0):"Sunday";

            //  String sql ="select routecode,routename,routenametamil,routeday from tblroute where status='"+statusvar+"'" +
            //      " order by routenametamil ";
            String sql = "";
            if(preferenceMangr.pref_getString("getbusiness_type").equals("2")){
                sql = "  SELECT  routecode,routename,routenametamil,routeday FROM tblroute where status='"+statusvar+"'" +
                        "  and vancode!='"+preferenceMangr.pref_getString("getvancode")+"' and (business_type='2'  or business_type='3') ";
            }else  if(preferenceMangr.pref_getString("getbusiness_type").equals("1")) {
                sql = "  SELECT  routecode,routename,routenametamil,routeday FROM tblroute where status='" + statusvar + "'" +
                        "  and vancode!='" + preferenceMangr.pref_getString("getvancode") + "' and (business_type='1'  or business_type='3')    ";
            }else {
                sql = "  SELECT  routecode,routename,routenametamil,routeday FROM tblroute where status='" + statusvar + "'" +
                        "  and vancode!='" + preferenceMangr.pref_getString("getvancode") + "' and (business_type='1'  or business_type='2'  or business_type='3')    ";
            }

            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetOthersRouteDB", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get GetCashSummaryCompany
    public Cursor GetCashSummaryCompany()
    {
        Cursor mCur = null;
        try{
            String sql ="select companycode from tblcompanymaster order by companycode asc ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetCashSummaryCompany", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get GetCashSummaryDatas
    public Cursor GetCashSummaryDatas(String getschedulecode,String getcompanycode)
    {
        Cursor mCur = null;
        try{
//            String sql ="select coalesce((select tripadvance from tblsalesschedule where schedulecode='"+getschedulecode+"'),0) as advance, " +
//                    "coalesce(sum(grandtotal),0) as amount, " +
//                    "(select coalesce(sum(amount),0) from tblreceipt where schedulecode='"+getschedulecode+"' and receiptmode='Cash'  " +
//                    "and companycode='"+getcompanycode+"'  and flag!=3 and flag !=6 and type='Receipt') as receiptamount, " +
//                    "(select coalesce(sum(grandtotal),0) from tblsalesreturn where schedulecode='"+getschedulecode+"' and  billtypecode='1' " +
//                    "and companycode='"+getcompanycode+"' and flag!=3 and flag !=6) as salesreturnamount, " +
//                    "(select companyname from tblcompanymaster where companycode='"+getcompanycode+"') as companyname," +
//                    " (select coalesce(sum(amount),0) from tblexpenses where schedulecode='"+getschedulecode+"' and flag!=3) as expenseamt  " +
//                    " from tblsales where billtypecode='1' and cashpaidstatus='yes' " +
//                    " and schedulecode='"+getschedulecode+"' and companycode='"+getcompanycode+"'  and flag!=3 and flag !=6 ";
            String sql = "SELECT coalesce((select tripadvance from tblsalesschedule where " +
                    "schedulecode='"+getschedulecode+"'),0) as advance, coalesce(sum(totalcash),0) as " +
                    "amount,(select coalesce(sum(amount),0) from tblreceipt where schedulecode='"+getschedulecode+"' " +
                    "and receiptmode='Cash'  and companycode='"+getcompanycode+"'  and flag!=3 and flag !=6 and type='Receipt') as " +
                    "receiptamount,(select coalesce(sum(grandtotal),0) from tblsalesreturn where schedulecode='"+getschedulecode+"' " +
                    "and  billtypecode='1' and companycode='"+getcompanycode+"' and flag!=3 and flag !=6) as salesreturnamount, " +
                    "(select companyname from tblcompanymaster where companycode='"+getcompanycode+"') as companyname," +
                    " (select coalesce(sum(amount),0) from tblexpenses where schedulecode='"+getschedulecode+"' and flag!=3) " +
                    "as expenseamt,(select coalesce(sum(amount),0) from tblreceipt where schedulecode='"+getschedulecode+"' " +
                    "and receiptmode='UPI'  and companycode='"+getcompanycode+"'  and flag!=3 and flag !=6 and " +
                    "type='Receipt') as receiptupiamount,(select coalesce(sum(amount),0) from tblreceipt  AS a INNER " +
                    "JOIN tblsales As b ON a.voucherno = b.billno and a.financialyearcode = b.financialyearcode and " +
                    " a.companycode = b.companycode where a.schedulecode='"+getschedulecode+"' and a.receiptmode='UPI' " +
                    "and a.companycode='"+getcompanycode+"'  and a.flag!=3 and a.flag !=6 and type='Sales') as " +
                    "salesupiamount  FROM (SELECT coalesce(sum(totalcash),0) as totalcash FROM(select coalesce" +
                    "(sum(grandtotal),0) as totalcash from tblsales where  billtypecode='1' and cashpaidstatus='yes'" +
                    " and schedulecode='"+getschedulecode+"' and companycode='"+getcompanycode+"'  and flag!=3 and flag !=6 " +
                    "UNION ALL select coalesce(sum(a.amount),0) as totalcash from tblreceipt AS a INNER JOIN tblsales " +
                    "As b ON a.voucherno = b.billno and a.financialyearcode = b.financialyearcode and " +
                    "a.companycode = b.companycode where b.billtypecode=1 and b.cashpaidstatus='upi' and  " +
                    "a.schedulecode='"+getschedulecode+"' and  b.flag!=3 and b.flag!=6  AND a.companycode='"+getcompanycode+"' AND" +
                    " a.type = 'Sales' and receiptmode='Cash') AS dev ) AS a";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetCashSummaryDatas", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    public Cursor GetExpenseHeadDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select expensesheadcode,expenseshead,expensesheadtamil " +
                    " from tblexpenseshead where status='"+statusvar+"' order by expensesheadtamil ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetExpenseHeadDB", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get denomination
    public Cursor GetCurrencyDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select currencycode,currency from tblcurrency  order by currencycode asc ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetCurrencyDB", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get Schedule denomination
    public Cursor GetScheduleCurrencyDB(String getschedulecode)
    {
        Cursor mCur = null;
        try{
            String sql ="select currencycode,qty from tbldenomination where schedulecode='"+getschedulecode+"';  ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetScheduleCurrencyDB", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get Schedule for cash report
    public String GetScheduleCode()
    {
        String getschedulecode = "0";
        try{

            /*String dqlsql = "delete from tblsalesschedule where schedulecode='V340010'";
            mDb.execSQL(dqlsql);*/

            String sql ="select schedulecode from tblsalesschedule where vancode='"+ preferenceMangr.pref_getString("getvancode") +"' " +
                    " order by scheduledate desc limit 1 ";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getschedulecode = mCur.getString(0);
            }else{
                getschedulecode = "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetScheduleCode", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getschedulecode;
    }

    //Get Schedule for cash report
    public String GetOrderScheduleCode()
    {
        String getschedulecode = "0";
        try{
            String getdate = GenCreatedDate();
            String sql ="select schedulecode from tblsalesschedule where vancode='"+ preferenceMangr.pref_getString("getvancode") +"'" +
                    " and datetime(scheduledate) != datetime('"+getdate+"') " +
                    " order by scheduledate desc limit 1 ";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getschedulecode = mCur.getString(0);
            }else{
                getschedulecode = "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetOrderScheduleCode", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getschedulecode;
    }
    //Get Schedule for cash report
    public String GetOrderFormCount(String getschedulecode)
    {
        String getordercount = "0";
        try{
            String sql ="select coalesce(count(*),0) from tblorderdetails" +
                    "  where schedulecode='"+getschedulecode+"' " +
                    "  limit 1 ";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getordercount = mCur.getString(0);
            }else{
                getordercount = "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetOrderFormCount", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getordercount;
    }
    //Get Schedule KM
    public Cursor GetScheduleKM(String schedulecode)
    {
        Cursor mCur = null;
        try{
            String sql ="select coalesce(startingkm,''),coalesce(endingkm,'')" +
                    "  from tblsalesschedule where schedulecode='"+schedulecode+"' ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();

            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetScheduleKM", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    public String GetCashRoutename(String getschedulecode)
    {
        String getroutename = "0";
        try{
            String sql =" select a.routecode,(select routenametamil from tblroute where routecode=a.routecode) " +
                    " as routename FROM tblsalesschedule as a where schedulecode='"+getschedulecode+"' limit 1 ";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getroutename = mCur.getString(1);
            }else{
                getroutename = "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetCashRoutename", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getroutename;
    }

    public String GetCashStartingKm(String getschedulecode)
    {
        String getstartingkm = "0";
        try{
            String sql =" select startingkm FROM tblsalesschedule as a where schedulecode='"+getschedulecode+"' limit 1 ";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getstartingkm = mCur.getString(0);
            }else{
                getstartingkm = "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetCashStartingKm", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getstartingkm;
    }

    public String GetScheduleCount()
    {
        String getschedulecode = "0";
        try{
            String Gencode= GenCreatedDate();
            String sql ="select coalesce(count(*),'0') as count from tblsalesschedule";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getschedulecode = mCur.getString(0);
            }else{
                getschedulecode = "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetScheduleCount", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getschedulecode;
    }
    //Get sales amount for cash report
    public String GetSalesCash(String getschedulecode)
    {
        String getsalescash = "0";
        try{
//            String sql ="select coalesce(sum(grandtotal),0) as totalcash from tblsales where " +
//                    " schedulecode='"+getschedulecode+"' " +
//                    " and billtypecode=1 and flag!=3 and flag!=6 ";
            String sql = "SELECT coalesce(sum(totalcash),0) as totalcash FROM(select coalesce(sum(grandtotal),0)" +
                    " as totalcash from tblsales where  schedulecode='"+getschedulecode+"' and billtypecode=1 " +
                    "and flag!=3 and flag!=6 and cashpaidstatus!='upi' UNION ALL select " +
                    "coalesce(sum(a.amount),0) as totalcash from tblreceipt AS a INNER JOIN tblsales As " +
                    "b ON a.voucherno = b.billno and a.financialyearcode = b.financialyearcode and " +
                    "a.companycode = b.companycode where  a.schedulecode='"+getschedulecode+"' and " +
                    "b.billtypecode=1 and b.flag!=3 and b.flag!=6 and b.cashpaidstatus='upi' AND " +
                    "a.type = 'Sales' and receiptmode='Cash') AS dev";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getsalescash = String.valueOf( mCur.getDouble(0));
            }else{
                getsalescash = "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetSalesCash", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getsalescash;
    }
    //Get receipt amount for cash report
    public String GetReceiptCash(String getschedulecode)
    {
        String getreceiptcash = "0";
        try{
            String sql ="select coalesce(sum(amount),0) from tblreceipt where schedulecode='"+getschedulecode+"' and " +
                    " receiptmode='Cash' and flag!=3 and flag!=6 AND type='Receipt'";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getreceiptcash = String.valueOf(mCur.getDouble(0));
            }else{
                getreceiptcash = "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetReceiptCash", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getreceiptcash;
    }

    //Get schedule advance amount for cash report
    public String GetScheduleAdvance(String getschedulecode)
    {
        String getadvance = "0";
        try{
            String sql ="select coalesce(tripadvance,0) from tblsalesschedule where schedulecode='"+getschedulecode+"' ";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getadvance =String.valueOf( mCur.getDouble(0));
            }else{
                getadvance = "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetScheduleAdvance", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getadvance;
    }

    //Get schedule advance amount for cash report
    public String GetReceiptAmountSummary(String getschedulecode)
    {
        String getadvance = "0";
        try{
            String sql ="select coalesce(sum(amount),0) from tblreceipt where schedulecode='"+getschedulecode+"' " +
                    " and receiptmode='Cash' and flag!=3 and flag!=6 AND type='Receipt'";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getadvance = mCur.getString(0);
            }else{
                getadvance = "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetReceiptAmountSummary", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getadvance;
    }

    //Get schedule advance amount for cash report
    public Cursor GetAllCompanyCode()
    {
        Cursor mCur = null;
        try{
            String sql ="select distinct companycode,shortname from tblcompanymaster   ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetAllCompanyCode", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get GetCompanyDetails
    public Cursor GetCompanyDetails()
    {
        Cursor mCur = null;
        try{
            String sql ="select distinct b.companycode,b.companyname,b.street,b.area,b.city,b.pincode," +
                    " b.telephone,b.mobileno,b.gstin, " +
                    " b.fssaino,b.panno " +
                    " from tblcompanymaster  as b order by companycode desc";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetCompanyDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }


    //Get GetCompanyDetails
    public Cursor GetScheduleSummaryDetails(String getschedulecode)
    {
        Cursor mCur = null;
        try{
            String sql =" select (select registrationno from tblvehiclemaster where vehiclecode=a.vehiclecode) as vechiclename," +
                    "(select employeenametamil from tblemployeemaster where employeecode=a.employeecode) as salesname," +
                    "(select employeenametamil from tblemployeemaster where employeecode=a.drivercode) as drivername" +
                    "  from tblsalesschedule as a where schedulecode='"+getschedulecode+"' limit 1 ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetScheduleSummaryDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }


    //Get GetSummary Sales
    public Cursor GetSalesSummaryDetails(String getschedulecode,String getcompanycode)
    {
        Cursor mCur = null;
        try{
            String sql ="  SELECT billno,substr((SELECT customernametamil FROM tblcustomer WHERE customercode = a.customercode),1,50) as customertamil,\n" +
                    " grandtotal,cashpaidstatus,CASE WHEN cashpaidstatus = 'yes' then grandtotal ELSE CASE WHEN " +
                    "cashpaidstatus = 'upi' THEN (SELECT amount FROM tblreceipt WHERE voucherno= a.billno AND" +
                    " financialyearcode = a.financialyearcode and type = 'Sales' AND receiptmode='Cash') ELSE 0 END " +
                    "END AS cashamount,CASE WHEN cashpaidstatus = 'upi' THEN (SELECT amount FROM tblreceipt WHERE " +
                    "voucherno= a.billno AND financialyearcode = a.financialyearcode and type = 'Sales' AND " +
                    "receiptmode='UPI') ELSE 0 END AS upiamount from tblsales as a" +
                    " where schedulecode='"+getschedulecode+"' and companycode='"+getcompanycode+"' and billtypecode='1'" +
                    "  and flag!=3 and flag!=6 ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetSalesSummaryDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get GetSummary Sales Return
    public Cursor GetSalesReturnSummaryDetails(String getschedulecode,String getcompanycode)
    {
        Cursor mCur = null;
        try{
            String sql ="  select voucherno,substr((select customernametamil from tblcustomer where " +
                    "customercode = a.customercode),1,50)" +
                    "  as customernametamil,amount,receiptmode from tblreceipt as a " +
                    " where schedulecode='"+getschedulecode+"' and companycode='"+getcompanycode+"'  " +
                    "and flag!=3 and flag!=6 AND type='Receipt' ORDER BY voucherno";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetSalesReturnSummaryDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get GetSummary Recipt
    public Cursor GetReceiptSummaryDetails(String getschedulecode,String getcompanycode)
    {
        Cursor mCur = null;
        try{
            String sql ="  select voucherno,substr((select customernametamil from tblcustomer where " +
                    "customercode = a.customercode),1,50)" +
                    "  as customernametamil,amount,receiptmode from tblreceipt as a " +
                    " where schedulecode='"+getschedulecode+"' and companycode='"+getcompanycode+"'  " +
                    "and flag!=3 and flag!=6 AND type='Receipt' ORDER BY voucherno";

            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetReceiptSummaryDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    public Cursor GetFirstSales(String getschedulecode,String getcompanycode)
    {
        Cursor mCur = null;
        try{
            String sql ="select coalesce(sum(grandtotal),0) as total,(select billno from  tblsales where " +
                    " schedulecode='"+getschedulecode+"'  and companycode='"+getcompanycode+"' and billtypecode='1' " +
                    " order by transactionno asc limit 1 ) as frombillno, " +
                    " (select billno from  tblsales where schedulecode='"+getschedulecode+"'  " +
                    " and companycode='"+getcompanycode+"' and billtypecode='1' " +
                    " order by transactionno desc limit 1 )  as tobillno " +
                    " from tblsales where schedulecode='"+getschedulecode+"' and billtypecode='1' " +
                    "  and companycode='"+getcompanycode+"' and flag!=3 and flag!=6  ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetFirstSales", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get expense amount for cash report
    public String GetExpenseAmount(String getschedulecode)
    {
        String getexpenseamt = "0";
        try{
            String sql ="select coalesce(sum(amount),0) from tblexpenses " +
                    " where schedulecode='"+getschedulecode+"' and flag!=3 ";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getexpenseamt = String.valueOf(mCur.getDouble(0));
            }else{
                getexpenseamt = "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetExpenseAmount", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getexpenseamt;
    }
    public String GetSummaryAdvanceAmount(String getschedulecode)
    {
        String getadvance = "0";
        try{
            String sql ="select coalesce(tripadvance,0) from tblsalesschedule " +
                    " where schedulecode='"+getschedulecode+"'   ";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getadvance = mCur.getString(0);
            }else{
                getadvance = "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetSummaryAdvanceAmount", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getadvance;
    }

    public String GetSummarySalesAmount(String getschedulecode,String getcompanycode)
    {
        String getadvance = "0";
        try{
//            String sql =" select coalesce(sum(grandtotal),0)  as amount from tblsales as a" +
//                    " where schedulecode='"+getschedulecode+"' and billtypecode='1' " +
//                    " and cashpaidstatus='yes' and companycode='"+getcompanycode+"'  and flag!=3 and flag!=6  ";
            String sql = "SELECT coalesce(sum(amount),0)  as amount FROM(select coalesce(sum(grandtotal),0) " +
                    " as amount from tblsales as a where schedulecode='"+getschedulecode+"' and " +
                    "billtypecode='1' and cashpaidstatus='yes' and companycode='"+getcompanycode+"' " +
                    " and flag!=3 and flag!=6 " +
                    " UNION ALL " +
                    " select coalesce(sum(amount),0)  as amount from tblreceipt AS a INNER JOIN tblsales as b" +
                    " ON a.voucherno = b.billno and a.financialyearcode = b.financialyearcode and " +
                    "a.companycode = b.companycode where b.schedulecode='"+getschedulecode+"' and " +
                    "billtypecode='1' and cashpaidstatus='upi' and receiptmode='Cash' and " +
                    "b.companycode='"+getcompanycode+"'  and a.flag!=3 and a.flag!=6 AND a.type='Sales') AS DEV";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getadvance = mCur.getString(0);
            }else{
                getadvance = "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetSummarySalesAmount", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return getadvance;
    }

    public String GetSummarySalesUpiAmount(String getschedulecode,String getcompanycode)
    {
        String getsalesupiamount = "0";
        try{
            String sql = "SELECT coalesce(sum(amount),0)  as amount from tblreceipt AS a INNER JOIN tblsales as b" +
                    " ON a.voucherno = b.billno and a.financialyearcode = b.financialyearcode and " +
                    "a.companycode = b.companycode where b.schedulecode='"+getschedulecode+"' " +
                    "and billtypecode='1' and cashpaidstatus='upi' and receiptmode='UPI' and " +
                    "b.companycode='"+getcompanycode+"'  and a.flag!=3 and a.flag!=6 AND a.type='Sales'";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getsalesupiamount = mCur.getString(0);
            }else{
                getsalesupiamount = "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetSummarySalesAmount", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return getsalesupiamount;
    }

    public String GetSummarySalesReturnAmount(String getschedulecode,String getcompanycode)
    {
        String getadvance = "0";
        try{
            String sql =" select coalesce(sum(grandtotal),0)  as amount from tblsalesreturn as a" +
                    " where schedulecode='"+getschedulecode+"' and companycode='"+getcompanycode+"' and billtypecode='1'" +
                    "  and flag!=3 and flag!=6    ";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getadvance = mCur.getString(0);
            }else{
                getadvance = "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetSummarySalesReturnAmount", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getadvance;
    }

    public String GetSummaryReceiptAmount(String getschedulecode,String getcompanycode)
    {
        String getadvance = "0";
        try{
            String sql = "select coalesce(sum(amount),0) as amount from tblreceipt as a where " +
                    "schedulecode='"+getschedulecode+"' and companycode='"+getcompanycode+"' " +
                    "and flag!=3 and flag!=6  AND type='Receipt' AND receiptmode='Cash'";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getadvance = mCur.getString(0);
            }else{
                getadvance = "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetSummaryReceiptAmount", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getadvance;
    }


    public String GetSummaryReceiptUpiAmount(String getschedulecode,String getcompanycode)
    {
        String getadvance = "0";
        try{
            String sql = " select coalesce(sum(amount),0) as amount from tblreceipt as a where " +
                    "schedulecode='"+getschedulecode+"' and companycode='"+getcompanycode+"' " +
                    "and flag!=3 and flag!=6  AND type='Receipt' AND receiptmode='UPI'";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getadvance = mCur.getString(0);
            }else{
                getadvance = "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetSummaryReceiptUpiAmount", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getadvance;
    }

    //Get sales return amount for cash report
    public String GetSalesReturnAmount(String getschedulecode)
    {
        String getsalesreturn = "0";
        try{
            String sql ="select coalesce(sum(grandtotal),0) from tblsalesreturn where schedulecode='"+getschedulecode+"' " +
                    " and flag!=3 and flag!=6 ";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getsalesreturn = String.valueOf(mCur.getDouble(0));
            }else{
                getsalesreturn = "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetSalesReturnAmount", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getsalesreturn;
    }

    //Get Schedule List
    public Cursor GetScheduleListDB(String getschedulecode)
    {
        Cursor mCur=null;
        try{
            String sql ="select schedulecode,scheduledate,(select vanname from tblvanmaster where vancode =a.vancode) as " +
                    "vanname,(select routenametamil from tblroute where routecode= a.routecode) as routename," +
                    "(select coalesce(vehiclename,'') || ' - ' || coalesce(registrationno,'NIL') as vehiclename from tblvehiclemaster" +
                    " where vehiclecode=a.vehiclecode) as vechiclename," +
                    "(select employeenametamil from tblemployeemaster where employeecode=a.employeecode) employeename," +
                    "(select employeenametamil from tblemployeemaster where employeecode=a.drivercode) drivername,helpername," +
                    "tripadvance,startingkm,endingkm,coalesce(lunch_start_time,'') as lunch_start_time ," +
                    " coalesce(lunch_end_time,'') as lunch_end_time ," +
                    "(select ewayurl from tblscheduleeway where schedulecode=a.schedulecode) as ewayurl "+
                    "from tblsalesschedule as a where scheduledate=datetime('"+getschedulecode+"')" +
                    " and vancode='"+ preferenceMangr.pref_getString("getvancode") +"' ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetScheduleListDB", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //get server details for edit
    public Cursor GetServerdetailDB(String getserverid)
    {
        Cursor mCur=null;
        try{
            String sql ="select serverid,internetip,status,urlname from tblserversettings where serverid='"+getserverid+"' ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetServerdetailDB", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get Sales List
    public Cursor GetSalesListDB(String getdate,String getcompanycode,String paymenttype,String paymentstatus,
                                 String geteinvoicegenerated,String geteinvoicepending,
                                 String gettotaleinvoicebills)
    {
        Cursor mCur=null;
        try{
            String sql="";
            String getbilltype="";
            String getpaymentstaus="";
            String getcompany = "";
            String einvoicepending="";
            String einvoicegenerated="";
            String totaleinvoicebills="";
            if(paymenttype.equals("All Bills")){
                paymenttype = "0";
            }else if(paymenttype.equals("Cash")){
                paymenttype ="1";
            }else if(paymenttype.equals("Credit")){
                paymenttype ="2";
            }else{
                paymenttype ="3";
            }
            if(paymentstatus.equals("Paid")){
                paymentstatus = "yes";
            }else if(paymentstatus.equals("Not Paid")){
                paymentstatus ="no";
            }else{
                paymentstatus ="0";
            }

            if(paymenttype.equals("0")){
                getbilltype = "1=1";
            }if(paymenttype.equals("1")){
                getbilltype = "billtypecode=1";
            }if(paymenttype.equals("2")){
                getbilltype = "billtypecode=2";
            }
            if(paymentstatus.equals("0")){
                getpaymentstaus = "1=1";
            }if(paymentstatus.equals("yes")){
                getpaymentstaus = "cashpaidstatus='yes'";
            }if(paymentstatus.equals("no")){
                getpaymentstaus = "cashpaidstatus='no'";
            }
            if(getcompanycode.equals("0") || getcompanycode.equals("")) {
                getcompany = "1=1";
            }else{
                getcompany="companycode='"+getcompanycode+"'";
            }
            if(geteinvoicepending.equals("0")){
                einvoicepending="1=1";
            }else{
                einvoicepending="a.flag!=3 and a.flag!=6 and gstin !='' and pdflocalpath is null";
            }
            if(geteinvoicegenerated.equals("0")){
                einvoicegenerated="1=1";
            }else{
                einvoicegenerated="a.flag!=3 and a.flag!=6 and gstin !='' and pdflocalpath is not null";
            }
            if(gettotaleinvoicebills.equals("0")){
                totaleinvoicebills="1=1";
            }else{
                totaleinvoicebills="a.flag!=3 and a.flag!=6 and gstin !='' ";
            }
            sql = "select companycode,vancode,transactionno,billno,refno,prefix,suffix," +
                    "strftime('%d-%m-%Y',billdate) as billdate,customercode," +
                    " billtypecode,gstin,schedulecode," +
                    "subtotal," +
                    "(case when discount>0 then (select sum(amount) from tblsalesitemdetails where transactionno = a.transactionno and financialyearcode = a.financialyearcode and companycode=a.companycode and freeitemstatus='freeitem') else 0 end) as discount," +
                    "(case when discount>0 then subtotal - coalesce((select sum(amount) from tblsalesitemdetails where transactionno = a.transactionno and financialyearcode = a.financialyearcode and companycode=a.companycode and freeitemstatus='freeitem'),0) else grandtotal end) as grandtotal,billcopystatus,cashpaidstatus,financialyearcode,bookingno,flag," +
                    "(select customername from tblcustomer where customercode=a.customercode) as customername," +
                    "(select customernametamil from tblcustomer where customercode=a.customercode) as customernametamil ," +
                    "(select areanametamil from tblareamaster where areacode=(select areacode from tblcustomer where customercode=a.customercode )) as areaname," +
                    "(select citynametamil from tblcitymaster where citycode=(select citycode from tblareamaster where areacode=" +
                    "(select areacode from tblcustomer where customercode=a.customercode ))) as cityname," +
                    "(select shortname from tblcompanymaster where companycode=a.companycode) as shortname," +
                    "einvoiceurl,irn_no,ack_no,einvoice_status,syncstatus,(SELECT count(voucherno) FROM " +
                    " tblreceipt where voucherno in  (SELECT billno FROM tblsales WHERE a.transactionno " +
                    "= transactionno AND financialyearcode = a.financialyearcode) AND type = 'Sales') " +
                    "AS upipaid " +
                    "from tblsales as a where billdate = datetime('" + getdate + "') " +
                    "and vancode = '" + preferenceMangr.pref_getString("getvancode") + "'  and " +
                    " "+getcompany+" and "+getbilltype+" and  "+getpaymentstaus+"" +
                    " and "+einvoicepending+" and "+einvoicegenerated+" and " +
                    " "+totaleinvoicebills+" order by transactionno desc,companycode desc ";

            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetSalesListDB", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Sales List
    public Cursor GetSalesOrderListDB(String getdate,String getcompanycode)
    {
        Cursor mCur=null;
        try{
            String sql="";
            String getcompany = "";

            if(getcompanycode.equals("0") || getcompanycode.equals("")) {
                getcompany = "1=1";
            }else{
                getcompany="companycode='"+getcompanycode+"'";
            }
            sql = "select companycode,vancode,transactionno,billno,refno,prefix,suffix," +
                    "strftime('%d-%m-%Y',billdate) as billdate,customercode," +
                    " billtypecode,gstin,schedulecode," +
                    "subtotal,discount,grandtotal,billcopystatus,cashpaidstatus,financialyearcode,bookingno,flag," +
                    "(select customername from tblcustomer where customercode=a.customercode) as customername," +
                    "(select customernametamil from tblcustomer where customercode=a.customercode) as customernametamil ," +
                    "(select areanametamil from tblareamaster where areacode=(select areacode from tblcustomer where customercode=a.customercode )) as areaname," +
                    "(select citynametamil from tblcitymaster where citycode=(select citycode from tblareamaster where areacode=" +
                    "(select areacode from tblcustomer where customercode=a.customercode ))) as cityname," +
                    "(select shortname from tblcompanymaster where companycode=a.companycode) as shortname,coalesce(status,'') as status " +
                    "from tblsalesorder as a where billdate = datetime('" + getdate + "') " +
                    "and vancode = '" + preferenceMangr.pref_getString("getvancode") + "'  and " +
                    " "+getcompany+"   order by transactionno desc ";

            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName()  + "GetSalesOrderListDB", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get Sales List
    public Cursor GetSalesPrintListDB(String getdate,String getcompanycode,String paymenttype,String paymentstatus)
    {
        Cursor mCur=null;
        try{
            String sql="";
            String getbilltype="";
            String getpaymentstaus="";
            String getcompany = "";
            if(paymenttype.equals("All Bills")){
                paymenttype = "0";
            }else if(paymenttype.equals("Cash")){
                paymenttype ="1";
            }else{
                paymenttype ="2";
            }
            if(paymentstatus.equals("Paid")){
                paymentstatus = "yes";
            }else if(paymentstatus.equals("Not Paid")){
                paymentstatus ="no";
            }else{
                paymentstatus ="0";
            }

            if(paymenttype.equals("0")){
                getbilltype = "1=1";
            }if(paymenttype.equals("1")){
                getbilltype = "billtypecode=1";
            }if(paymenttype.equals("2")){
                getbilltype = "billtypecode=2";
            }
            if(paymentstatus.equals("0")){
                getpaymentstaus = "1=1";
            }if(paymentstatus.equals("yes")){
                getpaymentstaus = "cashpaidstatus='yes'";
            }if(paymentstatus.equals("no")){
                getpaymentstaus = "cashpaidstatus='no'";
            }
            if(getcompanycode.equals("0") || getcompanycode.equals("")) {
                getcompany = "1=1";
            }else{
                getcompany="companycode='"+getcompanycode+"'";
            }
            sql = "select companycode,vancode,transactionno,billno,refno,prefix,suffix," +
                    "strftime('%d-%m-%Y',billdate) as billdate,customercode," +
                    " billtypecode,gstin,schedulecode," +
                    "subtotal,discount,grandtotal,billcopystatus,cashpaidstatus,financialyearcode,bookingno,flag," +
                    "(select customername from tblcustomer where customercode=a.customercode) as customername," +
                    "(select customernametamil from tblcustomer where customercode=a.customercode) as customernametamil ," +
                    "(select areanametamil from tblareamaster where areacode=(select areacode from tblcustomer where customercode=a.customercode )) as areaname," +
                    "(select citynametamil from tblcitymaster where citycode=(select citycode from tblareamaster where areacode=" +
                    "(select areacode from tblcustomer where customercode=a.customercode ))) as cityname," +
                    "(select shortname from tblcompanymaster where companycode=a.companycode) as shortname " +
                    "from tblsales as a where billdate = datetime('" + getdate + "') " +
                    "and vancode = '" + preferenceMangr.pref_getString("getvancode") + "' and  a.flag!=3 and a.flag!=6 and " +
                    " "+getcompany+" and "+getbilltype+" and  "+getpaymentstaus+" order by transactionno desc ";

            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetSalesPrintListDB", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Sales List
    public Cursor GetSalesBillPrintListDB(String getdate,String getcompanycode,String paymenttype,String paymentstatus)
    {
        Cursor mCur=null;
        try{
            String sql="";
            String getbilltype="";
            String getpaymentstaus="";
            String getcompany = "";
            if(paymenttype.equals("All Bills")){
                paymenttype = "0";
            }else if(paymenttype.equals("Cash")){
                paymenttype ="1";
            }else{
                paymenttype ="2";
            }
            if(paymentstatus.equals("Paid")){
                paymentstatus = "yes";
            }else if(paymentstatus.equals("Not Paid")){
                paymentstatus ="no";
            }else{
                paymentstatus ="0";
            }

            if(paymenttype.equals("0")){
                getbilltype = "1=1";
            }if(paymenttype.equals("1")){
                getbilltype = "billtypecode=1";
            }if(paymenttype.equals("2")){
                getbilltype = "billtypecode=2";
            }
            if(paymentstatus.equals("0")){
                getpaymentstaus = "1=1";
            }if(paymentstatus.equals("yes")){
                getpaymentstaus = "cashpaidstatus='yes'";
            }if(paymentstatus.equals("no")){
                getpaymentstaus = "cashpaidstatus='no'";
            }
            if(getcompanycode.equals("0") || getcompanycode.equals("")) {
                getcompany = "1=1";
            }else{
                getcompany="companycode='"+getcompanycode+"'";
            }
            sql = "select companycode,vancode,transactionno,billno,refno,prefix,suffix," +
                    "strftime('%d-%m-%Y',billdate) as billdate,customercode," +
                    " billtypecode,gstin,schedulecode," +
                    "subtotal,discount,grandtotal,billcopystatus,cashpaidstatus,financialyearcode,bookingno,flag," +
                    "(select customername from tblcustomer where customercode=a.customercode) as customername," +
                    " SUBSTR( (select customername from tblcustomer where customercode=a.customercode),1,15) as customernametamil ," +
                    "SUBSTR( (select areaname from tblareamaster where areacode=(select areacode from tblcustomer where customercode=a.customercode )),1,15) as areaname," +
                    "(select citynametamil from tblcitymaster where citycode=(select citycode from tblareamaster where areacode=" +
                    "(select areacode from tblcustomer where customercode=a.customercode ))) as cityname," +
                    "(select shortname from tblcompanymaster where companycode=a.companycode) as shortname " +
                    "from tblsales as a where billdate = datetime('" + getdate + "') " +
                    "and vancode = '" + preferenceMangr.pref_getString("getvancode") + "' and  " +
                    " "+getcompany+" and "+getbilltype+" and  "+getpaymentstaus+" and a.flag!=3 and a.flag!=6 order by transactionno desc ";

            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetSalesBillPrintListDB", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }



    //Get Sales List
    public Cursor GetSalesReturnBillListDB(String getdate,String getcompanycode,String paymenttype)
    {
        Cursor mCur=null;
        try{
            String sql="";
            String getbilltype="";
            String getcompany = "";
            if(paymenttype.equals("All Bills")){
                paymenttype = "0";
            }else if(paymenttype.equals("Cash")){
                paymenttype ="1";
            }else{
                paymenttype ="2";
            }


            if(paymenttype.equals("0")){
                getbilltype = "1=1";
            }if(paymenttype.equals("1")){
                getbilltype = "billtypecode=1";
            }if(paymenttype.equals("2")){
                getbilltype = "billtypecode=2";
            }

            if(getcompanycode.equals("0") || getcompanycode.equals("")) {
                getcompany = "1=1";
            }else{
                getcompany="companycode='"+getcompanycode+"'";
            }
            sql = "select companycode,vancode,transactionno,billno,refno,prefix,suffix," +
                    "strftime('%d-%m-%Y',billdate) as billdate,customercode," +
                    " billtypecode,gstin,schedulecode," +
                    "subtotal,discount,grandtotal,billcopystatus,cashpaidstatus,financialyearcode,bookingno,flag," +
                    "(select customername from tblcustomer where customercode=a.customercode) as customername," +
                    " SUBSTR( (select customername from tblcustomer where customercode=a.customercode),1,15) as customernametamil ," +
                    " SUBSTR( (select areaname from tblareamaster where areacode=(select areacode from tblcustomer where customercode=a.customercode )),1,15) as areaname," +
                    "(select citynametamil from tblcitymaster where citycode=(select citycode from tblareamaster where areacode=" +
                    "(select areacode from tblcustomer where customercode=a.customercode ))) as cityname," +
                    "(select shortname from tblcompanymaster where companycode=a.companycode) as shortname " +
                    "from tblsalesreturn as a where billdate = datetime('" + getdate + "') " +
                    "and vancode = '" + preferenceMangr.pref_getString("getvancode") + "' and  " +
                    " "+getcompany+" and "+getbilltype+"   and a.flag!=3 and a.flag!=6 order by transactionno desc ";

            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "GetSalesReturnBillListDB", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Sales List
    public Cursor GetSalesOrderBillListDB(String getdate)
    {
        Cursor mCur=null;
        try{
            String sql="";

            sql = "select companycode,vancode,transactionno,billno,refno,prefix,suffix," +
                    "strftime('%d-%m-%Y',billdate) as billdate,customercode," +
                    " billtypecode,gstin,schedulecode," +
                    "subtotal,discount,grandtotal,billcopystatus,cashpaidstatus,financialyearcode,bookingno,flag," +
                    "(select customername from tblcustomer where customercode=a.customercode) as customername," +
                    " SUBSTR( (select customername from tblcustomer where customercode=a.customercode),1,15) as customernametamil ," +
                    " SUBSTR( (select areaname from tblareamaster where areacode=(select areacode from tblcustomer where customercode=a.customercode )),1,15) as areaname," +
                    "(select citynametamil from tblcitymaster where citycode=(select citycode from tblareamaster where areacode=" +
                    "(select areacode from tblcustomer where customercode=a.customercode ))) as cityname," +
                    "(select shortname from tblcompanymaster where companycode=a.companycode) as shortname, " +
                    "(select companyname from tblcompanymaster where companycode=a.companycode) as companyname, " +
                    "(select city from tblcompanymaster where companycode=a.companycode) as city " +
                    "from tblsalesorder as a where billdate = datetime('" + getdate + "') " +
                    "and vancode = '" + preferenceMangr.pref_getString("getvancode") + "'    " +
                    "   and a.flag!=3 and a.flag!=6 and a.status<>2 order by transactionno desc ";

            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() +" - GetSalesOrderBillListDB", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get Sales List
    public Cursor GetSalesBillWiseCompanyListDB(String getdate,String getcompanycode,String paymenttype,
                                                String paymentstatus)
    {
        Cursor mCur=null;
        try{
            String sql="";
            String getbilltype="";
            String getpaymentstaus="";
            String getcompany = "";
            if(paymenttype.equals("All Bills")){
                paymenttype = "0";
            }else if(paymenttype.equals("Cash")){
                paymenttype ="1";
            }else{
                paymenttype ="2";
            }
            if(paymentstatus.equals("Paid")){
                paymentstatus = "yes";
            }else if(paymentstatus.equals("Not Paid")){
                paymentstatus ="no";
            }else{
                paymentstatus ="0";
            }

            if(paymenttype.equals("0")){
                getbilltype = "1=1";
            }if(paymenttype.equals("1")){
                getbilltype = "billtypecode=1";
            }if(paymenttype.equals("2")){
                getbilltype = "billtypecode=2";
            }
            if(paymentstatus.equals("0")){
                getpaymentstaus = "1=1";
            }if(paymentstatus.equals("yes")){
                getpaymentstaus = "cashpaidstatus='yes'";
            }if(paymentstatus.equals("no")){
                getpaymentstaus = "cashpaidstatus='no'";
            }
            if(getcompanycode.equals("0") || getcompanycode.equals("")) {
                getcompany = "1=1";
            }else{
                getcompany="companycode='"+getcompanycode+"'";
            }
            sql = "select distinct a.companycode,f.companyname, f.companynametamil, f.shortname, f.street, f.area, f.mobileno, " +
                    " f.gstin, f.city, f.telephone, f.panno, f.pincode,a.schedulecode  " +
                    "from tblsales as a inner join tblcompanymaster as f on a.companycode=f.companycode" +
                    "  where billdate = datetime('" + getdate + "') " +
                    "and vancode = '" + preferenceMangr.pref_getString("getvancode") + "' and  " +
                    " "+getcompany+" and "+getbilltype+" and  "+getpaymentstaus+" order by transactionno desc ";

            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetSalesBillWiseCompanyListDB", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get schedule details
    public Cursor GetBillScheduleDetails(String schedulecode)
    {
        Cursor mCur=null;
        try{
            String sql="";
            sql = "select (select routenametamil from tblroute where routecode=a.routecode) as routename " +
                    ",(select registrationno from tblvehiclemaster where vehiclecode=a.vehiclecode) as vehcilename, " +
                    "(select employeenametamil from tblemployeemaster where employeecode=a.employeecode) as employeenametamil," +
                    "(select employeenametamil from tblemployeemaster where employeecode=a.drivercode) as drivername " +
                    " from tblsalesschedule as a where schedulecode = '"+schedulecode+"' ";

            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetBillScheduleDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Sales List
    public Cursor GetSalesReturnBillWiseCompanyListDB(String getdate,String getcompanycode,String paymenttype,
                                                      String paymentstatus)
    {
        Cursor mCur=null;
        try{
            String sql="";
            String getbilltype="";
            String getcompany = "";
            if(paymenttype.equals("All Bills")){
                paymenttype = "0";
            }else if(paymenttype.equals("Cash")){
                paymenttype ="1";
            }else{
                paymenttype ="2";
            }


            if(paymenttype.equals("0")){
                getbilltype = "1=1";
            }if(paymenttype.equals("1")){
                getbilltype = "billtypecode=1";
            }if(paymenttype.equals("2")){
                getbilltype = "billtypecode=2";
            }

            if(getcompanycode.equals("0") || getcompanycode.equals("")) {
                getcompany = "1=1";
            }else{
                getcompany="companycode='"+getcompanycode+"'";
            }
            sql = "select distinct a.companycode,f.companyname, f.companynametamil, f.shortname, f.street, f.area, f.mobileno, " +
                    " f.gstin, f.city, f.telephone, f.panno, f.pincode,a.schedulecode  " +
                    "from tblsalesreturn as a inner join tblcompanymaster as f on a.companycode=f.companycode" +
                    "  where billdate = datetime('" + getdate + "') " +
                    "and vancode = '" + preferenceMangr.pref_getString("getvancode") + "' and  " +
                    " "+getcompany+" and "+getbilltype+" order by a.companycode asc ";

            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetSalesReturnBillWiseCompanyListDB", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Sales List
    public Cursor GetBillwiseSalesListDB(String getdate,String getcompanycode,String paymenttype,String paymentstatus)
    {
        Cursor mCur=null;
        try{
            String sql="";
            String getbilltype="";
            String getpaymentstaus="";
            String getcompany = "";
            if(paymenttype.equals("All Bills")){
                paymenttype = "0";
            }else if(paymenttype.equals("Cash")){
                paymenttype ="1";
            }else{
                paymenttype ="2";
            }
            if(paymentstatus.equals("Paid")){
                paymentstatus = "yes";
            }else if(paymentstatus.equals("Not Paid")){
                paymentstatus ="no";
            }else{
                paymentstatus ="0";
            }

            if(paymenttype.equals("0")){
                getbilltype = "1=1";
            }if(paymenttype.equals("1")){
                getbilltype = "billtypecode=1";
            }if(paymenttype.equals("2")){
                getbilltype = "billtypecode=2";
            }
            if(paymentstatus.equals("0")){
                getpaymentstaus = "1=1";
            }if(paymentstatus.equals("yes")){
                getpaymentstaus = "cashpaidstatus='yes'";
            }if(paymentstatus.equals("no")){
                getpaymentstaus = "cashpaidstatus='no'";
            }
            if(getcompanycode.equals("0") || getcompanycode.equals("")) {
                getcompany = "1=1";
            }else{
                getcompany="companycode='"+getcompanycode+"'";
            }
            sql = "select companycode,vancode,transactionno,billno,refno,prefix,suffix," +
                    "strftime('%d-%m-%Y',billdate) as billdate,customercode," +
                    " billtypecode,gstin,schedulecode," +
                    "subtotal,discount,grandtotal,billcopystatus,cashpaidstatus,financialyearcode,bookingno,flag," +
                    "(select customername from tblcustomer where customercode=a.customercode) as customername," +
                    "(select customernametamil from tblcustomer where customercode=a.customercode) as customernametamil ," +
                    "(select areanametamil from tblareamaster where areacode=(select areacode from tblcustomer where customercode=a.customercode )) as areaname," +
                    "(select citynametamil from tblcitymaster where citycode=(select citycode from tblareamaster where areacode=" +
                    "(select areacode from tblcustomer where customercode=a.customercode ))) as cityname," +
                    "(select shortname from tblcompanymaster where companycode=a.companycode) as shortname " +
                    "from tblsales as a where billdate = datetime('" + getdate + "') " +
                    "and vancode = '" + preferenceMangr.pref_getString("getvancode") + "' and  " +
                    " "+getcompany+" and "+getbilltype+" and  "+getpaymentstaus+" order by transactionno desc ";

            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetBillwiseSalesListDB", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }


    //Get CheckPaymentVoucher
    public Cursor CheckPaymentVoucher()
    {
        Cursor mCur=null;
        try{
            String sql="";

            String getdate= GenCreatedDate();

            sql = "select coalesce(count(*),'0') from tblsales  ";
            Cursor mCur1 = mDb.rawQuery(sql, null);
            if (mCur1.getCount() > 0)
            {
                mCur1.moveToFirst();
            }
            //  if(!mCur1.getString(0).equals("0")) {

            sql = "select billtypecode,billcopystatus,cashpaidstatus,transactionno,financialyearcode," +
                    " bookingno,GROUP_CONCAT(billno),gstin,companycode" +
                    " from tblsales as a " +
                    " where " +
                    " vancode = '" + preferenceMangr.pref_getString("getvancode") + "' and flag!=3 and flag!=6 and date(billdate)=date('"+preferenceMangr.pref_getString("getformatdate")+"') and cashpaidstatus='' group by transactionno " +
                    " order by transactionno desc limit 1 ";
            // billdate = datetime('" + getdate + "') and
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0) {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - CheckPaymentVoucher", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
        /*}else{
            return mCur1;
        }*/

    }

    public Cursor CheckPaymentVoucherV1()
    {
        Cursor mCur=null;
        try{
            String sql="";

            String getdate= GenCreatedDate();

            sql = "select coalesce(count(*),'0') from tblsales  ";
            Cursor mCur1 = mDb.rawQuery(sql, null);
            if (mCur1.getCount() > 0)
            {
                mCur1.moveToFirst();
            }
            //  if(!mCur1.getString(0).equals("0")) {

            sql = "select billtypecode,billcopystatus,cashpaidstatus,transactionno,financialyearcode," +
                    " bookingno,GROUP_CONCAT(billno),a.gstin,a.companycode, companyname,customernametamil,subtotal," +
                    " (case when discount>0 then (select sum(amount) from tblsalesitemdetails where transactionno = a.transactionno and financialyearcode = a.financialyearcode and companycode=a.companycode and freeitemstatus='freeitem') else 0 end) as discount," +
                    " (case when discount>0 then subtotal - coalesce((select sum(amount) from tblsalesitemdetails where transactionno = a.transactionno and financialyearcode = a.financialyearcode and companycode=a.companycode and freeitemstatus='freeitem'),0) else grandtotal end) as grandtotal " +
                    " from tblsales as a " +
                    " inner join tblcompanymaster as b on a.companycode=b.companycode " +
                    " inner join tblcustomer as c on a.customercode=c.customercode " +
                    " where vancode = '" + preferenceMangr.pref_getString("getvancode") + "' and a.flag!=3 and a.flag!=6 and date(billdate)=date('"+preferenceMangr.pref_getString("getformatdate")+"') and cashpaidstatus='' " +
                    " group by transactionno, a.companycode " +
                    " order by a.companycode asc limit 1";
            // billdate = datetime('" + getdate + "') and
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0) {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - CheckPaymentVoucher", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
        /*}else{
            return mCur1;
        }*/

    }


    public Cursor CheckUPIPayment()
    {
        Cursor mCur=null;
        try{
            String sql="";

            String getdate= GenCreatedDate();

            sql = "select coalesce(count(*),'0') from tblsales  ";
            Cursor mCur1 = mDb.rawQuery(sql, null);
            if (mCur1.getCount() > 0)
            {
                mCur1.moveToFirst();
            }
            //  if(!mCur1.getString(0).equals("0")) {

            sql = "select transactionno,financialyearcode,bookingno,billno,gstin," +
                    "(case when discount>0 then subtotal - coalesce((select sum(amount) from tblsalesitemdetails where transactionno = a.transactionno and financialyearcode = a.financialyearcode and companycode=a.companycode and freeitemstatus='freeitem'),0) else grandtotal end) as grandtotal," +
                    "companycode,coalesce((SELECT case when " +
                    "localfilepath = '' then downloadpath else localfilepath end as imageurl FROM " +
                    "tblcompanyvenderdetails WHERE companycode = a.companycode),'') AS imageurl,billcopystatus,(SELECT count(voucherno) " +
                    "FROM tblreceipt where voucherno in (SELECT billno FROM tblsales WHERE a.transactionno = transactionno " +
                    "AND financialyearcode = a.financialyearcode) AND type = 'Sales') AS upipaid " +
                    "from tblsales as a  where vancode = '" + preferenceMangr.pref_getString("getvancode") + "' " +
                    "and flag!=3 and flag!=6 and date(billdate)=(SELECT date(scheduledate) FROM tblsalesschedule " +
                    "WHERE  vancode = '" + preferenceMangr.pref_getString("getvancode") + "' ORDER by scheduledate DESC LIMIT 1)" +
                    "and cashpaidstatus = 'upi' and a.billno not in (SELECT voucherno FROM tblreceipt WHERE type='Sales')" +
                    " order by companycode desc limit 1 ";
            // billdate = datetime('" + getdate + "') and
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0) {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - CheckUPIPayment", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return mCur;
    }

    public Cursor CheckUPIPaymentV1(String companycode, String transactionno)
    {
        Cursor mCur=null;
        try{
            String getdate= GenCreatedDate();
            String queryCompany = "1=1";
            String queryCashPaidStatus = "(cashpaidstatus = '' OR cashpaidstatus='no')";
            if (!Utilities.isNullOrEmpty(companycode)) {
                queryCompany = "a.companycode=" + companycode;
                queryCashPaidStatus = "1=1";
            }
            String queryTransactionno = "1=1";
            if (!Utilities.isNullOrEmpty(transactionno)) {
                queryTransactionno = " a.transactionno = " + transactionno;
            } else {
                queryTransactionno = " a.transactionno = (select transactionno from tblsales as a " +
                                        " where vancode = '" + preferenceMangr.pref_getString("getvancode") + "' " +
                                        " and flag!=3 and flag!=6 and date(billdate)=(SELECT date(scheduledate) FROM tblsalesschedule WHERE  vancode = '" + preferenceMangr.pref_getString("getvancode") + "' ORDER by scheduledate DESC LIMIT 1) " +
                                        " and a.billno not in (SELECT voucherno FROM tblreceipt WHERE type='Sales')" +
                                        " order by transactionno desc limit 1) ";
            }

            String sql =
                    "select * from (" +
                    "   select transactionno,financialyearcode,bookingno,billno,gstin," +
                    "   (case when discount>0 then subtotal - coalesce((select sum(amount) from tblsalesitemdetails where transactionno = a.transactionno and financialyearcode = a.financialyearcode and companycode=a.companycode and freeitemstatus='freeitem'),0) else grandtotal end) as grandtotal," +
                    "   companycode,coalesce((SELECT case when localfilepath = '' then downloadpath else localfilepath end as imageurl FROM tblcompanyvenderdetails WHERE companycode = a.companycode and status ='Active'),'') AS imageurl,billcopystatus," +
                    "   (SELECT count(voucherno) FROM tblreceipt where voucherno in (SELECT billno FROM tblsales WHERE a.transactionno = transactionno AND financialyearcode = a.financialyearcode) AND type = 'Sales') AS upipaid " +
                    "   from tblsales as a where billtypecode=1 and " + queryCashPaidStatus + " and " + queryCompany + " and " + queryTransactionno +
                    ") as dev order by transactionno desc";

            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0) {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - CheckUPIPaymentV1", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return mCur;
    }

    //Get Check e-Invoice pending
    public String Checkeinvoicepending()
    {
        Cursor mCur=null;
        Cursor mCur2=null;
        try{
            String sql="";
            String getscheduledate="";

            String getdate= GenCreatedDate();

            sql = "select coalesce(count(*),'0') from tblsales  ";
            Cursor mCur1 = mDb.rawQuery(sql, null);
            if (mCur1.getCount() > 0)
            {
                mCur1.moveToFirst();
            }
            //  if(!mCur1.getString(0).equals("0")) {
            String sql1 ="select scheduledate from tblsalesschedule where vancode='"+ preferenceMangr.pref_getString("getvancode") +"' " +
                    " order by scheduledate desc limit 1 ";
            mCur2 = mDb.rawQuery(sql1, null);

            if (mCur2.getCount() > 0)
            {
                mCur2.moveToFirst();
                getscheduledate = mCur2.getString(0);
            }else{
                getscheduledate = "";
            }

            sql = "select count(*) as count from tblsales where  gstin!='' and " +
                    "(einvoiceurl='null' or einvoiceurl='' or  einvoiceurl is null or pdflocalpath=''" +
                    " or pdflocalpath is  null) and einvoice_status!=2  and" +
                    " vancode = '" + preferenceMangr.pref_getString("getvancode") + "' and flag!=3 and flag!=6 and " +
                    " date(billdate)=date('"+getscheduledate+"')";
            //"+preferenceMangr.pref_getString("getformatdate")+"
            // billdate = datetime('" + getdate + "') and
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0) {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - Checkeinvoicepending", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur.getString(0);
        /*}else{
            return mCur1;
        }*/

    }

    //Get CheckPaymentVoucher
    public Cursor SalesListPaymentVoucher(String gettransno,String getfinacialyr,String getcompanycode)
    {
        Cursor mCur = null;
        try{
            String sql="";
            mCur=null;
            sql = "select billtypecode,billcopystatus,cashpaidstatus,transactionno,financialyearcode," +
                    " bookingno,GROUP_CONCAT(billno)," +
                    " coalesce((SELECT case when localfilepath = '' then downloadpath else localfilepath end as imageurl FROM tblcompanyvenderdetails WHERE companycode = a.companycode and status='Active'),'') AS imageurl," +
                    "  companyname, customernametamil,subtotal," +
                    " (case when discount>0 then (select sum(amount) from tblsalesitemdetails where transactionno = a.transactionno and financialyearcode = a.financialyearcode and companycode=a.companycode and freeitemstatus='freeitem') else 0 end) as discount," +
                    " (case when discount>0 then subtotal - coalesce((select sum(amount) from tblsalesitemdetails where transactionno = a.transactionno and financialyearcode = a.financialyearcode and companycode=a.companycode and freeitemstatus='freeitem'),0) else grandtotal end) as grandtotal " +
                    " from tblsales as a " +
                    " inner join tblcompanymaster as b on a.companycode=b.companycode " +
                    " inner join tblcustomer as c on a.customercode=c.customercode " +
                    " where " +
                    " vancode = '" + preferenceMangr.pref_getString("getvancode") + "' and a.flag!=3 and a.flag!=6 and " +
                    " transactionno = '"+gettransno+"' and financialyearcode='"+getfinacialyr+"' and " +
                    " a.companycode = '"+ getcompanycode +"'group by transactionno " +
                    " order by transactionno desc limit 1 ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0) {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;

    }

    public Cursor SalesListUPIPaymentVoucher(String gettransno,String getfinacialyr,String getcompanycode,String getschedulecode)
    {
        Cursor mCur = null;
        try{
            String sql="";
            mCur=null;
            sql = "SELECT transactionno,financialyearcode,bookingno,billno,gstin," +
                    "(case when discount>0 then subtotal - coalesce((select sum(amount) from tblsalesitemdetails where transactionno = a.transactionno and financialyearcode = a.financialyearcode and companycode=a.companycode and freeitemstatus='freeitem'),0) else grandtotal end) as grandtotal," +
                    "companycode, billtypecode,billcopystatus,cashpaidstatus,coalesce(upiamount,0) AS " +
                    "upiamount,coalesce(cashamount,0) AS cashamount,coalesce(transactionid,'') AS " +
                    "transactionid,coalesce(venderid,0) AS venderid,coalesce(vendername,'') AS vendername," +
                    "coalesce((SELECT case when localfilepath = '' then downloadpath else localfilepath " +
                    "end as imageurl FROM tblcompanyvenderdetails WHERE companycode = a.companycode and status='Active'),'')" +
                    " AS imageurl,a.billcopystatus,(SELECT count(voucherno) FROM tblreceipt where voucherno " +
                    "in (SELECT billno FROM tblsales WHERE a.transactionno = transactionno AND " +
                    "financialyearcode = a.financialyearcode) AND type = 'Sales') AS upipaid from " +
                    "tblsales as a LEFT JOIN (SELECT * FROM (SELECT  " +
                    "voucherno,sum(upiamount) AS upiamount,sum(cashamount) AS cashamount," +
                    "group_concat(transactionid, '') AS transactionid,group_concat(vendername, '') AS " +
                    "vendername,(SELECT venderid FROM tblreceipt WHERE voucherno = dev.voucherno AND " +
                    "type = 'Sales' AND receiptmode = 'UPI') AS venderid  FROM (SELECT a.voucherno," +
                    "CASE WHEN type = 'Sales' AND a.receiptmode = 'UPI' THEN a.amount ELSE 0 END AS " +
                    "upiamount,CASE WHEN type = 'Sales' AND a.receiptmode = 'Cash' THEN " +
                    "a.amount ELSE 0 END AS cashamount,a.transactionid,(SELECT vendername FROM " +
                    "tblupivendermaster WHERE venderid = a.venderid) AS vendername FROM tblreceipt " +
                    "AS a WHERE type = 'Sales' AND schedulecode='" +  getschedulecode + "') AS dev GROUP BY voucherno) AS DERV) AS b " +
                    "on a.billno = b.voucherno where a.vancode = '" + preferenceMangr.pref_getString("getvancode") + "'" +
                    " and a.flag!=3 and a.flag!=6 and a.transactionno = '"+ gettransno +"' and " +
                    "a.financialyearcode='"+getfinacialyr+"' and a.companycode = '"+ getcompanycode +"' ";

            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0) {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;

    }

    //Get Check Mobile verifation pending for customer in a area
    public String Checkemobilenoverify(String customercode,String routecode)
    {
        Cursor mCur=null;

        try{
            String sql="";
            sql = "SELECT count(*) FROM tblcustomer as a inner join tblroutedetails as b on a.areacode=b.areacode where " +
                    "routecode='"+routecode+"' and customercode='"+customercode+"' and allowmobilenoverify='yes' and mobilenoverificationstatus=0";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0) {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return mCur.getString(0);
    }

    //Get Sales List data Details
    public Cursor GetSalesListDatasDB(String gettransactionno,String getfinancialyr,String getcompanycode)
    {
        Cursor mCur = null;
        try{
            String sql ="select companycode,vancode,transactionno,billno,refno,prefix,suffix," +
                    "strftime('%d-%m-%Y',billdate) as billdate,customercode," +
                    " billtypecode,gstin,schedulecode," +
                    "subtotal," +
                    "(case when discount>0 then (select sum(amount) from tblsalesitemdetails where transactionno = '"+gettransactionno+"' and financialyearcode = '"+getfinancialyr+"' and companycode='"+getcompanycode+"' and freeitemstatus='freeitem') else 0 end) as discount," +
                    "(case when discount>0 then subtotal else grandtotal end) as grandtotal,billcopystatus,cashpaidstatus,financialyearcode,bookingno,flag," +
                    "(select customername from tblcustomer where customercode=a.customercode) as customername," +
                    "(select customernametamil from tblcustomer where customercode=a.customercode) as customernametamil ," +
                    "(select areanametamil from tblareamaster where areacode=(select areacode from tblcustomer where customercode=a.customercode ))" +
                    "  as areaname," +
                    "(select citynametamil from tblcitymaster where citycode=(select citycode from tblareamaster where areacode=" +
                    "(select areacode from tblcustomer where customercode=a.customercode ))) as cityname," +
                    "(select shortname from tblcompanymaster where companycode=a.companycode) as shortname,bitmapimage," +
                    " (select sum(grandtotal) from tblsales as b where transactionno = '"+gettransactionno+"' and financialyearcode = '"+getfinancialyr+"' ) as totalamt" +
                    " ,a.pdflocalpath,a.einvoiceurl,a.ack_no,a.irn_no from tblsales as a where transactionno = '"+gettransactionno+"' " +
                    " and financialyearcode = '"+getfinancialyr+"' and companycode='"+getcompanycode+"'  ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Sales List data Details
    public Cursor GetSalesOrderListDatasDB(String gettransactionno,String getfinancialyr,String getcompanycode)
    {
        Cursor mCur = null;
        try{
            String sql ="select companycode,vancode,transactionno,billno,refno,prefix,suffix," +
                    "strftime('%d-%m-%Y',billdate) as billdate,customercode," +
                    " billtypecode,gstin,schedulecode," +
                    "subtotal,discount,grandtotal,billcopystatus,cashpaidstatus,financialyearcode,bookingno,flag," +
                    "(select customername from tblcustomer where customercode=a.customercode) as customername," +
                    "(select customernametamil from tblcustomer where customercode=a.customercode) as customernametamil ," +
                    "(select areanametamil from tblareamaster where areacode=(select areacode from tblcustomer where customercode=a.customercode ))" +
                    "  as areaname," +
                    "(select citynametamil from tblcitymaster where citycode=(select citycode from tblareamaster where areacode=" +
                    "(select areacode from tblcustomer where customercode=a.customercode ))) as cityname," +
                    "(select shortname from tblcompanymaster where companycode=a.companycode) as shortname,bitmapimage," +
                    " (select sum(grandtotal) from tblsalesorder as b where transactionno = '"+gettransactionno+"' and financialyearcode = '"+getfinancialyr+"' ) as totalamt, " +
                    " case when a.transportid!='0' then   (select transportname || ' - ' || (select day_of_dispatch from tbltransportcitymapping " +
                    " where citycode= (select citycode from tblareamaster where areacode =(select areacode from tblcustomer where customercode=a.customercode)) )" +
                    "  from tbltransportmaster where transportid= a.transportid) else  " +
                    " (select transportmodetype from tbltransportmode where transportmodecode=a.transportmode ) end  as transport" +
                    " from tblsalesorder as a where transactionno = '"+gettransactionno+"' " +
                    " and financialyearcode = '"+getfinancialyr+"'     ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get Expenses List
    public Cursor GetExpensesListDatasDB(String gettransactionno,String getfinancialyr,String getcompanycode)
    {
        Cursor mCur = null;

        try{
            String sql ="select companycode,vancode,transactionno,billno,refno,prefix,suffix," +
                    "strftime('%d-%m-%Y',billdate) as billdate,customercode," +
                    " billtypecode,gstin,schedulecode," +
                    "subtotal,discount,grandtotal,billcopystatus,cashpaidstatus,financialyearcode,bookingno,flag," +
                    "(select customername from tblcustomer where customercode=a.customercode) as customername," +
                    "(select customernametamil from tblcustomer where customercode=a.customercode) as customernametamil ," +
                    "(select areaname from tblareamaster where areacode=(select areacode from tblcustomer where customercode=a.customercode )) as areaname," +
                    "(select cityname from tblcitymaster where citycode=(select citycode from tblareamaster where areacode=" +
                    "(select areacode from tblcustomer where customercode=a.customercode ))) as cityname," +
                    "(select shortname from tblcompanymaster where companycode=a.companycode) as shortname " +
                    "from tblsales as a where transactionno = '"+gettransactionno+"' " +
                    " and financialyearcode = '"+getfinancialyr+"' and companycode='"+getcompanycode+"'  ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Sales List data Details
    public Cursor GetSalesListItemDatasDB(String gettransactionno,String getfinancialyr,String getcompanycode)
    {
        Cursor mCur = null;
        try{
            String sql ="select a.itemcode,b.itemnametamil,a.qty,a.weight,a.price,a.discount,a.amount,a.freeitemstatus ," +
                    "(select unitname from tblunitmaster where unitcode=b.unitcode) as unitname," +
                    "(select hsn from tblitemsubgroupmaster where itemsubgroupcode=b.itemsubgroupcode) as hsn," +
                    "(select tax from tblitemsubgroupmaster where itemsubgroupcode=b.itemsubgroupcode) as tax," +
                    " coalesce((Select noofdecimals from tblunitmaster where unitcode=b.unitcode),0) as noofdecimals," +
                    " CASE WHEN itemtype=2 then (SELECT freeitemcolor from tblgeneralsettings) else " +
                    "coalesce((Select colourcode from tblcompanymaster where companycode=a.companycode),'#000000') END as colourcode" +
                    " from tblsalesitemdetails as a inner join tblitemmaster as b on a.itemcode=b.itemcode" +
                    " where a.transactionno='"+gettransactionno+"' and a.companycode='"+getcompanycode+"' " +
                    " and a.financialyearcode='"+getfinancialyr+"' order by itemtype  ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Sales List data Details
    public Cursor GetSalesOrderListItemDatasDB(String gettransactionno,String getfinancialyr,String getcompanycode)
    {
        Cursor mCur = null;
        try{
            String sql ="select a.itemcode,b.itemnametamil,a.qty,a.weight,a.price,a.discount,a.amount,a.freeitemstatus ," +
                    "(select unitname from tblunitmaster where unitcode=b.unitcode) as unitname," +
                    "(select hsn from tblitemsubgroupmaster where itemsubgroupcode=b.itemsubgroupcode) as hsn," +
                    "(select tax from tblitemsubgroupmaster where itemsubgroupcode=b.itemsubgroupcode) as tax," +
                    " coalesce((Select noofdecimals from tblunitmaster where unitcode=b.unitcode),0) as noofdecimals," +
                    " CASE WHEN itemtype=2 then (SELECT freeitemcolor from tblgeneralsettings) else " +
                    "coalesce((Select colourcode from tblcompanymaster where companycode=a.companycode),'#000000') END as colourcode" +
                    " from tblsalesorderitemdetails as a inner join tblitemmaster as b on a.itemcode=b.itemcode" +
                    " where a.transactionno='"+gettransactionno+"'   " +
                    " and a.financialyearcode='"+getfinancialyr+"' order by itemtype ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get Schedule Datas
    public Cursor GetScheduleDatasDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select * from tblsalesschedule  where coalesce(flag,0)=0";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get Customer Datas
    public Cursor GetCustomerDatasDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select * from tblcustomer where flag=1 ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Customer Datas
    public Cursor GetWholeCustomerDatasDB()
    {
        Cursor mCur = null;
        try{
            getdate = GenCreatedDate();
            String sql ="select * from tblcustomer as a   where  strftime('%Y-%m-%d',a.createddate)='"+getdate+"' ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get Order Datas
    public Cursor GetOrderDetailsDatasDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select * from tblorderdetails where flag=1 ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get Expense Datas
    public Cursor GetExpensesDetailsDatasDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select * from tblexpenses where (flag=1 or syncstatus=0) ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get Cash report
    public Cursor GetCashReportDatasDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select autonum,schedulecode,vancode,sales,salesreturn,advance,receipt,expenses,cash,denominationcash,makerid,createddate from tblcashreport where flag=1  ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get close cash report
    public Cursor GetCloseSalesDatasDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select autonum,closedate,vancode,schedulecode,makerid,createddate,paidparties,expenseentries from tblclosecash where flag=1 and status='2'  ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Denomination
    public Cursor GetDenominationDatasDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select * from tbldenomination where flag=1  ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Ending km
    public Cursor GetEndingKmScheduleDatasDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select endingkm,schedulecode from tblsalesschedule where schedulecode in " +
                    " (select schedulecode from tblclosecash )  ";
            mCur = mDb.rawQuery(sql,null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Receipt Datas
    public Cursor GetReceiptDetailsDatasDB()
    {
        Cursor mCur = null;
        try{
//            --
            String sql ="select * from tblreceipt where (flag=1 or syncstatus=0)";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get Expense Datas
    public Cursor GetCancelExpensesDetailsDatasDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select * from tblexpenses where flag=3 ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get Receipt Datas
    public Cursor GetCancelReceiptDetailsDatasDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select * from tblreceipt where flag=3 ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get Sales Datas
    public Cursor GetSalesDatasDB()
    {
        Cursor mCur = null;
        try{
            String getdate = GenCreatedDate();
            //and syncstatus=0
            String sql ="select * from tblsales where (flag in (1,3,4)) and syncstatus=0 and date(billdate)=date('"+getdate+"')";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Sales Datas
    public Cursor GetWholeSalesDatasDB(String getdate)
    {
        Cursor mCur = null;
        try{
//            getdate =GenCreatedDate();
            String sql ="select * from tblsales where  billdate = datetime('" + getdate + "')  ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get Sales Datas
    public Cursor GetSalesOrderDatasDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select * from tblsalesorder where (flag in (1,2,3,4)) and syncstatus=0 ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get Sales Datas
    public Cursor GetSalesReceiptDatasDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select * from tblsales where flag=4 ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Sales Item Datas
    public Cursor GetSalesItemDatasDB()
    {
        Cursor mCur = null;
        try{
            String getdate = GenCreatedDate();
            String sql = "select * from tblsalesitemdetails where flag in (1,3) " +
                    "and date(createddate)=date('"+getdate+"')";

            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        // String sql ="select * from tblsalesitemdetails where transactionno in" +
        //" (select transactionno from tblsales where flag=1 or syncstatus=0) ";

        return mCur;
    }
    //Get Sales Item Datas
    public Cursor GetWholeSalesItemDatasDB(String getdate)
    {
        Cursor mCur = null;
        try{
//            getdate = GenCreatedDate();
            String sql = "select * from tblsalesitemdetails as a   where strftime('%Y-%m-%d',a.createddate)='"+getdate+"'  ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        // String sql ="select * from tblsalesitemdetails where transactionno in" +
        //" (select transactionno from tblsales where flag=1 or syncstatus=0) ";

        return mCur;
    }

    //Get Sales Item Datas
    public Cursor GetSalesOrderItemDatasDB()
    {
        Cursor mCur = null;
        try{
            String sql = "select * from tblsalesorderitemdetails where flag in (1,3)";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        // String sql ="select * from tblsalesitemdetails where transactionno in" +
        //" (select transactionno from tblsales where flag=1 or syncstatus=0) ";

        return mCur;
    }
    //Get Stock transaction Datas
    public Cursor GetStockTransactionDatasDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select transactionno,transactiondate,vancode,itemcode,inward,outward,type," +
                    " refno,createddate,flag,companycode,op,financialyearcode,autonum from tblstocktransaction where flag=1 and type='salesreturn' ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get Stock transaction Datas
    public Cursor GetSalesStockTransactionDatasDB()
    {
        Cursor mCur = null;

        try{
            getdate = GenCreatedDate();
            String sql ="select * from tblsalesstockconversion as a   where  strftime('%Y-%m-%d',a.transactiondate) ='"+getdate+"' and " +
                    " financialyearcode='"+preferenceMangr.pref_getString("getfinanceyrcode") +"' ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    // sale sync for particular date
    public Cursor GetSalesStockTransactionDatasforPDDB(String getdate)
    {
        Cursor mCur = null;

        try{
//            getdate = GenCreatedDate();
            String sql ="select * from tblsalesstockconversion as a   where  strftime('%Y-%m-%d',a.transactiondate) ='"+getdate+"' and " +
                    " financialyearcode='"+preferenceMangr.pref_getString("getfinanceyrcode") +"' ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get Sales cancel Datas
    public Cursor GetSalesCancelDatasDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select * from tblsales where flag=3 and syncstatus=0  ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get Sales stock conversion cancel Datas
    public Cursor GetSalesConversionCancelDatasDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select * from tblsalesstockconversion where flag=3 ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get Sales cancel Datas
    public Cursor GetWholeSalesCancelDatasDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select * from tblsales where (flag=3 or flag=6)   ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Sales cancel Datas
    public Cursor GetSalesOrderCancelDatasDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select * from tblsalesorder where flag=3 and syncstatus=0  ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Sales cancel Datas
    public Cursor GetSalesCancelItemDatasDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select b.* from tblsales as a inner join tblsalesitemdetails as b on a.transactionno=b.transactionno " +
                    "and a.companycode=b.companycode and a.vancode=b.vancode where a.flag=3 and a.syncstatus=0  ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }


    //Get Sales cancel Datas
    public Cursor GetWholeSalesCancelItemDatasDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select b.* from tblsales as a inner join tblsalesitemdetails as b on a.transactionno=b.transactionno " +
                    "and a.companycode=b.companycode and a.vancode=b.vancode where a.flag=3   ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }


    //Get Sales cancel Datas
    public Cursor GetSalesOrderCancelItemDatasDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select b.* from tblsalesorder as a inner join " +
                    " tblsalesorderitemdetails as b on a.transactionno=b.transactionno and a.companycode=b.companycode " +
                    " and a.vancode=b.vancode where a.flag=3 and a.syncstatus=0  ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get cancel Stock transaction Datas
    public Cursor GetCancelStockTransactionDatasDB()
    {
        Cursor mCur = null;
        try{
            getdate = GenCreatedDate();
            String sql ="select transactionno,transactiondate,vancode,itemcode,inward,outward,type, " +
                    "refno,createddate,flag,companycode,op,financialyearcode,'1' as autonum from tblappstocktransactiondetails as a where" +
                    "   type='salescancel' and  strftime('%Y-%m-%d',a.transactiondate)='"+getdate+"'  ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get cancel Stock transaction Datas
    public Cursor GetWholeCancelStockTransactionDatasDB(String getdate)
    {
        Cursor mCur = null;
        try{
//            getdate = GenCreatedDate();
            String sql ="select transactionno,transactiondate,vancode,itemcode,inward,outward,type, " +
                    "refno,createddate,flag,companycode,op,financialyearcode,'1' as autonum from tblappstocktransactiondetails as a where" +
                    "   type='salescancel' and  strftime('%Y-%m-%d',a.transactiondate)='"+getdate+"'  ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get cancel Stock transaction Datas
    public Cursor GetWholeStockConversionDatasDB(String getdate)
    {
        Cursor mCur = null;
        try{
//            getdate = GenCreatedDate();
            String sql ="select * from tblsalesstockconversion as a   where  strftime('%Y-%m-%d',a.transactiondate) ='"+getdate+"' and " +
                    " financialyearcode='"+preferenceMangr.pref_getString("getfinanceyrcode") +"' ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get cancel Stock transaction Datas
    public Cursor GetCancelReturnStockTransactionDatasDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select transactionno,transactiondate,vancode,itemcode,inward,outward,type, refno,createddate,flag,companycode,op,financialyearcode,autonum from tblstocktransaction where flag=1 and type='salesreturncancel' ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Update Sales Flag
    public void UpdateSalesFlag(String gettransactiono)
    {
        try{
            String sql = "update tblsales set flag=2 ,syncstatus=1 where" +
                    " transactionno='" + gettransactiono + "' and (flag=1 or flag=4) ";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

    }

    //Update Sales Flag
    public void UpdateSalesOrderFlag(String gettransactiono)
    {
        try{
            String sql = "update tblsalesorder set flag=2 ,syncstatus=1 where" +
                    " transactionno='" + gettransactiono + "' and (flag=1 or flag=4) ";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

    }
    //Update Sales Flag
    public void UpdateCancelSalesFlag(String gettransactiono)
    {
        try{
            String sql = "update tblsales set flag=6,syncstatus=1 where transactionno='" + gettransactiono + "' and flag=3 ";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }


    }


    //Update Sales Flag
    public void UpdateOrderCancelSalesFlag(String gettransactiono)
    {
        try{
            String sql = "update tblsalesorder set flag=6,syncstatus=1 where transactionno='" + gettransactiono + "' and flag=3 ";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }


    }
    //Update Sales Item Flag
    public void UpdateSalesItemFlag(String gettransactiono)
    {
        try{
            String sql = "update tblsalesitemdetails set flag=2 where transactionno='"+gettransactiono+"' ";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

    }
    //Update Sales Item Flag
    public void UpdateSalesOrderItemFlag(String gettransactiono)
    {
        try{
            String sql = "update tblsalesorderitemdetails set flag=2 where transactionno='"+gettransactiono+"' ";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

    }
    //Update stock transaction Flag
    public void UpdateStockTransactionFlag(String gettransactiono)
    {
        try{
            String sql = "update tblstocktransaction set flag=2 where refno='"+gettransactiono+"' and type='salesreturn' ";
            mDb.execSQL(sql);
            String sql1 = "update tblappstocktransactiondetails set flag=2 and syncstatus=1 " +
                    " where refno='"+gettransactiono+"' and type='salesreturn' ";
            mDb.execSQL(sql1);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }


    }
    public void UpdateSalesStockTransactionFlag(String getrefno,String gettransactiono)
    {
        try{
            String sql = "update tblsalesstockconversion set flag=2 where refno='"+getrefno+"' and (type='stockconversion')" +
                    "  and transactionno='"+gettransactiono+"' ";
            mDb.execSQL(sql);
            String sql1 = "update tblappstocktransactiondetails set flag=2 and syncstatus=1 " +
                    " where refno='"+getrefno+"' and ( type='stockconversion') ";
            mDb.execSQL(sql1);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

    }
    public void UpdateCancelSalesStockTransactionFlag(String gettransactiono)
    {
        try{
            String sql = "update tblstocktransaction set flag=2 where refno='"+gettransactiono+"' and type='salescancel' ";
            mDb.execSQL(sql);
            String sql1 = "update tblappstocktransactiondetails set flag=2 and syncstatus=1 " +
                    " where refno='"+gettransactiono+"' and type='salescancel' ";
            mDb.execSQL(sql1);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }


    }

    public void UpdateCancelSalesReturnStockTransactionFlag(String gettransactiono)
    {
        try{
            String sql = "update tblstocktransaction set flag=2 where refno='"+gettransactiono+"'" +
                    " and type='salesreturncancel' ";
            mDb.execSQL(sql);
            String sql1 = "update tblappstocktransactiondetails set flag=2 and syncstatus=1 " +
                    " where refno='"+gettransactiono+"' and type='salesreturncancel' ";
            mDb.execSQL(sql1);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }


    }

    //Update Sales Cancel Flag
    public String UpdateSalesCancelFlag(String gettransactiono,String getfinancialyrcode)
    {
        try{
            String sql = "update tblsales set flag=3,syncstatus=0 where transactionno='"+gettransactiono+"' and " +
                    " financialyearcode='"+getfinancialyrcode+"' and vancode='"+preferenceMangr.pref_getString("getvancode")+"'  ";
            mDb.execSQL(sql);

            String sql1 = "update tblsalesitemdetails set flag=3 where transactionno='"+gettransactiono+"' and " +
                    " financialyearcode='"+getfinancialyrcode+"' and vancode='"+preferenceMangr.pref_getString("getvancode")+"'  ";
            mDb.execSQL(sql1);

        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return "success";
    }

    public String UpdateSalesReceiptCancelFlag(String gettransactiono,String getfinancialyrcode)
    {
        try{
            String sql = "update tblreceipt set flag=3,syncstatus=0 WHERE type = 'Sales' AND " +
                    "voucherno IN (SELECT DISTINCT billno FROM tblsales WHERE transactionno = '"+ gettransactiono +"' AND " +
                    "financialyearcode = '"+ getfinancialyrcode +"' ) " +
                    "AND financialyearcode = '"+ getfinancialyrcode +"' ";
            mDb.execSQL(sql);

        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - UpdateSalesReceiptCancelFlag", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return "success";
    }

    //Update Sales Cancel einvoice Flag
    public String UpdateSalesEinvoiceCancel(String gettransactiono,String getfinancialyrcode,String companycode)
    {
        try{
            String sql = "update tblsales set flag=3,syncstatus=0 where transactionno='"+gettransactiono+"' and " +
                    " financialyearcode='"+getfinancialyrcode+"' and " +
                    "vancode='"+preferenceMangr.pref_getString("getvancode")+"' and companycode='"+companycode+"' ";
            mDb.execSQL(sql);

            String sql1 = "update tblsalesitemdetails set flag=3 where transactionno='"+gettransactiono+"' and " +
                    " financialyearcode='"+getfinancialyrcode+"' and " +
                    "vancode='"+preferenceMangr.pref_getString("getvancode")+"' and companycode='"+companycode+"' ";
            mDb.execSQL(sql1);

        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return "success";
    }

    public String  UpdateSaleEinvoiceReceiptCancel(String gettransactiono,String getfinancialyrcode,String companycode) {
        try{
            mDb = mDbHelper.getReadableDatabase();
            String sql = "update tblreceipt set flag=3,syncstatus=0 WHERE type = 'Sales' AND companycode = '"+ companycode +"' AND " +
                    "voucherno = (SELECT billno FROM tblsales WHERE transactionno = '"+ gettransactiono +"' AND " +
                    "financialyearcode = '"+ getfinancialyrcode +"' and companycode = '"+ companycode +"') " +
                    "AND financialyearcode = '"+ getfinancialyrcode +"' ";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }


        return "success";

    }

    //Update Sales Cancel Flag
    public String UpdateSalesOrderCancelFlag(String gettransactiono,String getfinancialyrcode)
    {
        try{
            String sql = "update tblsalesorder set flag=3,syncstatus=0 where transactionno='"+gettransactiono+"' and " +
                    " financialyearcode='"+getfinancialyrcode+"' and vancode='"+preferenceMangr.pref_getString("getvancode")+"'  ";
            mDb.execSQL(sql);

            String sql1 = "update tblsalesorderitemdetails set flag=3 where transactionno='"+gettransactiono+"' and " +
                    " financialyearcode='"+getfinancialyrcode+"' and vancode='"+preferenceMangr.pref_getString("getvancode")+"'  ";
            mDb.execSQL(sql1);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return "success";
    }

    //Update Sales Stock transaction
    public String UpdateSalesStockTransaction(String gettransactiono,String getfinancialyrcode)
    {
        Cursor mCur = null;
        try{
            String GetDate= GenCreatedDate();

            String sql = " insert into tblstocktransaction (transactionno,transactiondate,vancode," +
                    "  itemcode,inward,outward,type,refno,flag,createddate,companycode,op,financialyearcode,autonum)" +
                    "select (select coalesce(max(transactionno),0)+1 from tblstocktransaction),datetime('"+GetDate+"')," +
                    "vancode,itemcode,0,'-'||qty,'salescancel',transactionno,1,datetime('now', 'localtime'),companycode ,0,'"+getfinancialyrcode+"',(select coalesce(max(autonum),0)+1 from tblstocktransaction)+(select count(*) from tblsalesitemdetails b  " +
                    "where transactionno='"+gettransactiono+"' and financialyearcode='"+getfinancialyrcode+"' and a.autonum >= b.autonum) " +
                    " from tblsalesitemdetails as a where transactionno='"+gettransactiono+"' " +
                    "and financialyearcode='"+getfinancialyrcode+"'  ";

            mDb.execSQL(sql);

            String sql1 = " insert into tblappstocktransactiondetails (transactionno,transactiondate,vancode," +
                    "  itemcode,inward,outward,type,refno,flag,createddate,companycode,op,financialyearcode,syncstatus)" +
                    "select (select coalesce(max(transactionno),0)+1 from tblappstocktransactiondetails),datetime('"+GetDate+"')," +
                    "vancode,itemcode,0,'-'||qty,'salescancel',transactionno,1,datetime('"+GetDate+"'),companycode ,0," +
                    " '"+getfinancialyrcode+"',0 " +
                    " from tblsalesitemdetails where transactionno='"+gettransactiono+"' " +
                    "and financialyearcode='"+getfinancialyrcode+"' ";


            mDb.execSQL(sql1);


        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

      /*  String sql = " UPDATE tblstocktransaction set flag=3 " +
                "  where refno='"+gettransactiono+"' " +
                "and financialyearcode='"+getfinancialyrcode+"' and type='sales' ";
        mDb.execSQL(sql);
        String sql1 = " UPDATE tblappstocktransactiondetails set flag=3 and syncstatus=0 " +
                "  where refno='"+gettransactiono+"' " +
                "and financialyearcode='"+getfinancialyrcode+"' and type='sales' ";
        mDb.execSQL(sql1);*/
        return "success";
    }

    //Update Sales Stock transaction
    public String UpdateSalesEinvoiceStockTransaction(String gettransactiono,String getfinancialyrcode,String companycode)
    {
        Cursor mCur = null;
        try{
            String GetDate= GenCreatedDate();

            String sql = " insert into tblstocktransaction (transactionno,transactiondate,vancode," +
                    "  itemcode,inward,outward,type,refno,flag,createddate,companycode,op,financialyearcode,autonum)" +
                    "select (select coalesce(max(transactionno),0)+1 from tblstocktransaction),datetime('"+GetDate+"')," +
                    "vancode,itemcode,0,'-'||qty,'salescancel',transactionno,1,datetime('now', 'localtime'),companycode ,0,'"+getfinancialyrcode+"',(select coalesce(max(autonum),0)+1 from tblstocktransaction)+(select count(*) from tblsalesitemdetails b  " +
                    "where transactionno='"+gettransactiono+"' and financialyearcode='"+getfinancialyrcode+"' and a.autonum >= b.autonum) " +
                    " from tblsalesitemdetails as a where transactionno='"+gettransactiono+"' " +
                    "and financialyearcode='"+getfinancialyrcode+"' and companycode='"+companycode+"' ";

            mDb.execSQL(sql);

            String sql1 = " insert into tblappstocktransactiondetails (transactionno,transactiondate,vancode," +
                    "  itemcode,inward,outward,type,refno,flag,createddate,companycode,op,financialyearcode,syncstatus)" +
                    "select (select coalesce(max(transactionno),0)+1 from tblappstocktransactiondetails),datetime('"+GetDate+"')," +
                    "vancode,itemcode,0,'-'||qty,'salescancel',transactionno,1,datetime('"+GetDate+"'),companycode ,0," +
                    " '"+getfinancialyrcode+"',0 " +
                    " from tblsalesitemdetails where transactionno='"+gettransactiono+"' " +
                    "and financialyearcode='"+getfinancialyrcode+"'  and companycode='"+companycode+"' ";


            mDb.execSQL(sql1);


        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return "success";
    }
    //Get vehicle
    public Cursor GetVehicleDB()
    {
        Cursor mCur = null;
        try{
            String sql ="";
            if(preferenceMangr.pref_getString("getbusiness_type").equals("2")){
                sql ="select vehiclecode,vehiclename ,modelandyear,registrationno,capacity,fc,permit,insurance,documentupload," +
                        " status from tblvehiclemaster where status='"+statusvar+"' and vehiclecode='0' order by vehiclename ";
            }else {
                sql = "select vehiclecode,vehiclename || ' - ' || registrationno as vehiclename ,modelandyear,registrationno,capacity,fc,permit,insurance,documentupload," +
                        " status from tblvehiclemaster where status='" + statusvar + "' and vehiclecode !='0' order by vehiclename ";
            }
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Sales Rep
    public Cursor GetSalesRepDB()
    {
        Cursor mCur = null;
        try{
            String sql ="";
           /* if(LoginActivity.getbusiness_type.equals("2")){
                sql ="select employeecategorycode,employeecode,employeename,employeenametamil,mobileno,documentupload from " +
                        "tblemployeemaster where employeecode='0' and status='"+statusvar+"' order by employeenametamil ";
            }else {*/
            sql = "select employeecategorycode,employeecode,employeename,employeenametamil,mobileno,documentupload from " +
                    "tblemployeemaster where employeecode !='0' and employeecategorycode=1 and status='" + statusvar + "' order by employeenametamil ";
            // }
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Driver
    public Cursor GetDriverDB()
    {
        Cursor mCur = null;
        try{
            String sql="";
            if(preferenceMangr.pref_getString("getbusiness_type").equals("2")){
                sql ="select employeecategorycode,employeecode,employeename,employeenametamil,mobileno,documentupload from " +
                        "tblemployeemaster where employeecode='0' and status='"+statusvar+"' order by employeenametamil ";
            }else {
                sql = "select employeecategorycode,employeecode,employeename,employeenametamil,mobileno,documentupload from " +
                        "tblemployeemaster where employeecode !='0' and employeecategorycode=2 and status='" + statusvar + "' order by employeenametamil ";
            }
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get area based on route
    public Cursor GetCustomerDB(String areacode,int category)
    {
        Cursor mCur = null;
        try{
            String sql = "";

            String billdate=preferenceMangr.pref_getString("getcurrentdatetime");
            String billqry="";

            if(category==Constants.CUSTOMER_CATEGORY_SALES){

                billqry=", (select count(*) from tblsales where strftime('%d-%m-%Y',date(billdate))='"+billdate+"' and customercode=a.customercode) as billcount," +
                        "(select count(*) from tblnotpurchased where strftime('%d-%m-%Y',date(billdate))='"+billdate+"' and customercode=a.customercode) as notpurchasedcount";

            }else if(category==Constants.CUSTOMER_CATEGORY_ORDER) {
                billqry=", (select count(*) from tblsalesorder where strftime('%d-%m-%Y',date(billdate))='"+billdate+"' and customercode=a.customercode) as billcount,0 as notpurchasedcount";

            }
            else if(category==Constants.CUSTOMER_CATEGORY_BOTH){
                billqry=", (select count(*) from tblsalesreturn where strftime('%d-%m-%Y',date(billdate))='"+billdate+"' and customercode=a.customercode) as billcount,0 as notpurchasedcount";

            }
            else if(category==Constants.CUSTOMER_CATEGORY_RECEIPT)
            {
                billqry=", (select count(*) from tblreceipt where strftime('%d-%m-%Y',date(receiptdate))='"+billdate+"' and customercode=a.customercode) as billcount,0 as notpurchasedcount";

            }

            if(preferenceMangr.pref_getString("getbusiness_type").equals("2")) {
//                sql = "select customercode,customername,customernametamil,address,areacode,emailid,mobileno,telephoneno," +
//                        " aadharno,gstin,schemeapplicable,coalesce(customertypecode,'1')  from tblcustomer " +
//                        " where areacode = '" + areacode + "' and status='" + statusvar + "' and (business_type='2' or business_type='3') " +
//                        " order by customernametamil ";

                sql = "select * from (select customercode,customername,customernametamil,address,a.areacode,emailid,mobileno,telephoneno," +
                        " aadharno,gstin,schemeapplicable,coalesce(customertypecode,'1'),areanametamil,citynametamil," +
                        " (select count(*) from tblsalesorder where status = '1' and flag<>3 and flag<>6 and customercode=a.customercode) as orderCount " +
                        ""+billqry+" from tblcustomer " +
                        " as a inner join tblareamaster as b on a.areacode=b.areacode inner join tblcitymaster as c " +
                        " on b.citycode=c.citycode" +
                        " where a.areacode = '" + areacode + "' and a.status='" + statusvar + "' and (business_type='2' or business_type='3') " +
                        " order by customernametamil) as dev order by billcount asc,notpurchasedcount asc ";

            }
            else if(preferenceMangr.pref_getString("getbusiness_type").equals("1")) {

//                sql = "select customercode,customername,customernametamil,address,areacode,emailid,mobileno,telephoneno," +
//                        " aadharno,gstin,schemeapplicable,coalesce(customertypecode,'1')  from tblcustomer " +
//                        " where areacode = '" + areacode + "' and status='" + statusvar + "' and (business_type='1' or business_type='3') " +
//                        " order by customernametamil ";
                sql = "select * from (select customercode,customername,customernametamil,address,a.areacode,emailid,mobileno,telephoneno," +
                        " aadharno,gstin,schemeapplicable,coalesce(customertypecode,'1'),areanametamil,citynametamil," +
                        " (select count(*) from tblsalesorder where status = '1' and flag<>3 and flag<>6 and customercode=a.customercode) as orderCount" +
                        ""+ billqry + " from tblcustomer " +
                        " as a inner join tblareamaster as b on a.areacode=b.areacode inner join tblcitymaster as c on b.citycode=c.citycode" +
                        " where a.areacode = '" + areacode + "' and a.status='" + statusvar + "' and (business_type='1' or business_type='3') " +
                        " order by customernametamil" +
                        ") as dev order by billcount asc,notpurchasedcount asc ";
            }else {
                String varBusinessType = "";
                if(category == Constants.CUSTOMER_CATEGORY_SALES)
                    varBusinessType = "(business_type='1' or business_type='3')";
                if(category == Constants.CUSTOMER_CATEGORY_ORDER)
                    varBusinessType = "(business_type='2' or business_type='3')";
                if(category == Constants.CUSTOMER_CATEGORY_BOTH || category == Constants.CUSTOMER_CATEGORY_RECEIPT )
                    varBusinessType = "(business_type='1' or business_type='2' or business_type='3')";

//                sql = "select customercode,customername,customernametamil,address,areacode,emailid,mobileno,telephoneno," +
//                        " aadharno,gstin,schemeapplicable,coalesce(customertypecode,'1')  from" +
//                        " tblcustomer where areacode = '" + areacode + "' and status='" + statusvar + "' and " + varBusinessType +
//                        " order by customernametamil ";
                sql = "select * from (select customercode,customername,customernametamil,address,a.areacode,emailid,mobileno,telephoneno," +
                        " aadharno,gstin,schemeapplicable,coalesce(customertypecode,'1'),areanametamil,citynametamil," +
                        " (select count(*) from tblsalesorder where status = '1' and flag<>3 and flag<>6 and customercode=a.customercode) as orderCount " +
                        ""+billqry+" from" +
                        " tblcustomer as a inner join tblareamaster as b on a.areacode=b.areacode inner " +
                        " join tblcitymaster as c on b.citycode=c.citycode where a.areacode = '" + areacode + "' and" +
                        " a.status='" + statusvar + "' and " + varBusinessType +
                        " order by customernametamil)  as dev order by billcount asc,notpurchasedcount asc ";
            }

                mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get sub group based on stock transfer items
    public Cursor GetSubGroupDB()
    {
        Cursor mCur = null;
        try{
            getdate = GenCreatedDate();
            /*and a.transactiondate=datetime('"+getdate+"')*/
            String sql =" select * from (select  c.itemsubgroupcode,c.itemsubgroupname,c.itemsubgroupnametamil, " +
                    " c.itemgroupcode,sum(op)+sum(inward)-sum(outward) as closing from tblstocktransaction as a inner join tblitemmaster  " +
                    " as b on a.itemcode = b.itemcode inner join tblitemsubgroupmaster as c on " +
                    " c.itemsubgroupcode=b.itemsubgroupcode where a.vancode='"+preferenceMangr.pref_getString("getvancode")+"'" +
                    "  and b.status='"+statusvar+"' " +
                    " and c.Status='"+statusvar+"'  and a.flag!=3 " +
                    " group by c.itemsubgroupcode,c.itemsubgroupname,c.itemsubgroupnametamil,c.itemgroupcode ) as dev where dev.closing>0 " +
                    " order by dev.itemgroupcode ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    public Cursor GetAllGroupDB(){
        Cursor mCur = null;
        try{
            getdate = GenCreatedDate();
            /* " and a.transactiondate=datetime('"+getdate+"') " +*/
            String sql =" SELECT * from ( select  d.itemgroupcode,d.itemgroupname,d.itemgroupnametamil " +
                    " from   tblitemmaster  as b inner join tblitemsubgroupmaster as c   on c.itemsubgroupcode=b.itemsubgroupcode " +
                    " inner join tblitemgroupmaster as d      on d.itemgroupcode = c.itemgroupcode where b.status='"+statusvar+"' and" +
                    " c.Status='"+statusvar+"' and  d.status ='"+statusvar+"' group by d.itemgroupcode,d.itemgroupname,d.itemgroupnametamil  )  " +
                    " as dev  order by dev.itemgroupcode ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get group based on sub group
    public Cursor GetAllSubGroup_GroupDB(String itemgroupcode)
    {
        Cursor mCur = null;
        try{
            getdate = GenCreatedDate();
            String sql ="select distinct c.itemsubgroupcode,c.itemsubgroupname,c.itemsubgroupnametamil,c.itemgroupcode from " +
                    "    tblitemmaster  as b   inner join  " +
                    " tblitemsubgroupmaster as c on c.itemsubgroupcode=b.itemsubgroupcode where " +
                    "   c.itemgroupcode='"+itemgroupcode+"' " +
                    " and b.status='"+statusvar+"' and c.Status='"+statusvar+"'  " +
                    "    order by c.itemgroupcode ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        /*  "  and a.transactiondate=datetime('"+getdate+"') " +*/

        return mCur;
    }
    //Get sub group based on stock transfer items
    public Cursor GetGroupDB()
    {
        Cursor mCur = null;
        try{
            getdate = GenCreatedDate();
            /* " and a.transactiondate=datetime('"+getdate+"') " +*/
            String sql =" select * from ( select  d.itemgroupcode,d.itemgroupname,d.itemgroupnametamil, " +
                    "sum(op)+sum(inward)-sum(outward) as closing from tblstocktransaction  " +
                    "as a inner join tblitemmaster  as b on a.itemcode = b.itemcode inner join tblitemsubgroupmaster as c " +
                    "on c.itemsubgroupcode=b.itemsubgroupcode inner join tblitemgroupmaster as d   " +
                    "on d.itemgroupcode = c.itemgroupcode where a.vancode='"+preferenceMangr.pref_getString("getvancode")+"'  " +
                    "and b.status='"+statusvar+"' and c.Status='"+statusvar+"' and a.flag!=3 and  d.status ='"+statusvar+"'" +
                    " group by d.itemgroupcode,d.itemgroupname,d.itemgroupnametamil  ) as dev where dev.closing>0  " +
                    "  order by dev.itemgroupcode ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get group based on sub group
    public Cursor GetSubGroup_GroupDB(String itemgroupcode)
    {
        Cursor mCur = null;
        try{
            getdate = GenCreatedDate();
           /* String sql =" select * from (select  c.itemsubgroupcode,c.itemsubgroupname,c.itemsubgroupnametamil, " +
                    " c.itemgroupcode,sum(op)+sum(inward)-sum(outward) as closing from tblstocktransaction as a inner join tblitemmaster  " +
                    " as b on a.itemcode = b.itemcode inner join tblitemsubgroupmaster as c on " +
                    " c.itemsubgroupcode=b.itemsubgroupcode where a.vancode='"+LoginActivity.getvancode+"'" +
                    "  and c.itemgroupcode='"+itemgroupcode+"'  and b.status='"+statusvar+"' " +
                    " and c.Status='"+statusvar+"'  and a.flag!=3 " +
                    " group by c.itemsubgroupcode,c.itemsubgroupname,c.itemsubgroupnametamil,c.itemgroupcode ) as dev where dev.closing>0 " +
                    " order by dev.itemgroupcode ";
*/
            String sql ="select distinct c.itemsubgroupcode,c.itemsubgroupname,c.itemsubgroupnametamil from " +
                    " tblstocktransaction as a inner join tblitemmaster  as b on a.itemcode = b.itemcode inner join  " +
                    "tblitemsubgroupmaster as c on c.itemsubgroupcode=b.itemsubgroupcode where " +
                    " a.vancode='"+preferenceMangr.pref_getString("getvancode")+"' and c.itemgroupcode='"+itemgroupcode+"' " +
                    "and b.status='"+statusvar+"' and c.Status='"+statusvar+"'  " +
                    "    order by c.itemgroupcode ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        /*  "  and a.transactiondate=datetime('"+getdate+"') " +*/

        return mCur;
    }
    //Get group based on sub group
    public Cursor GetSubGroup_GroupOrderDB(String itemgroupcode)
    {
        Cursor mCur = null;
        try{
            getdate = GenCreatedDate();
            String sql ="select distinct c.itemsubgroupcode,c.itemsubgroupname,c.itemsubgroupnametamil from " +
                    "   tblitemmaster  as b inner join  " +
                    "tblitemsubgroupmaster as c on c.itemsubgroupcode=b.itemsubgroupcode where " +
                    "  c.itemgroupcode='"+itemgroupcode+"' " +
                    "and b.status='"+statusvar+"' and c.Status='"+statusvar+"'  " +
                    "    order by c.itemgroupcode ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        /*  "  and a.transactiondate=datetime('"+getdate+"') " +*/

        return mCur;
    }
    //Get Sales items for cart
    public Cursor GetSalesItemsCart()
    {
        Cursor mCur = null;
        try{
            /*  "  and a.transactiondate=datetime('"+getdate+"') " +*/
            getdate = GenCreatedDate();
            String sql ="SELECT * FROM (select * from tblsalescartdatas where freeflag!='freeitem' order by cartcode asc) " +
                    " as dev UNION ALL " +
                    " SELECT * FROM (select * from tblsalescartdatas where freeflag='freeitem' order by cartcode asc) as dev1 ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Sales items for cart
    public Cursor GetSalesOrderItemsCarts()
    {
        Cursor mCur = null;
        try{
            /*  "  and a.transactiondate=datetime('"+getdate+"') " +*/
            getdate = GenCreatedDate();
            String sql ="select * from tblsalesordercartdatas order by cartcode asc ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Sales items for cart
    public Cursor GetSalesOrderItemsCart()
    {
        Cursor mCur = null;
        try{
            /*  "  and a.transactiondate=datetime('"+getdate+"') " +*/
            getdate = GenCreatedDate();
            String sql ="select * from tblsalesordercartdatas order by cartcode asc ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get Items
    public Cursor GetItemsDB(String itemsubgroupcode,String getroutecode,String getareacode)
    {
        Cursor mCur = null;
        try{
            /*and b.transactiondate=datetime('"+getdate+"')*/
            String getdate = GenCreatedDate();
            String getbusinesstype = "";
//            if(preferenceMangr.pref_getString("getbusiness_type").equals("2")){
//                getbusinesstype=" (b.business_type = 2 or b.business_type = 3) ";
//            }else  if(preferenceMangr.pref_getString("getbusiness_type").equals("1")){
//                getbusinesstype="(b.business_type = 1 or b.business_type = 3)";
//            }else{
//                getbusinesstype="(b.business_type = 1 or  b.business_type = 2 or b.business_type = 3)";
//            }
            String arr[]=preferenceMangr.pref_getString("getbusiness_type").split(",");
            if(arr.length>0) {
                for (int i = 0; i < arr.length; i++) {
                    if(arr[i].equals("2")){
                        getbusinesstype=" ((','||b.business_type||',') LIKE '%,2,%' or (','||b.business_type||',') LIKE '%,3,%') ";
                    }else if(arr[i].equals("1")){
                        getbusinesstype=" ((','||b.business_type||',') LIKE '%,1,%' or (','||b.business_type||',') LIKE '%,3,%') ";
                    }else{
                        getbusinesstype="((','||b.business_type||',') LIKE '%,1,%' or (','||b.business_type||',') LIKE '%,2,%' or (','||b.business_type||',') LIKE '%,3,%')";
                    }
                }
            }
            String getitembusinesstype = "";
//            if(preferenceMangr.pref_getString("getbusiness_type").equals("2")){
//                getitembusinesstype=" (a.business_type = 2 or a.business_type = 3) ";
//            }else  if(preferenceMangr.pref_getString("getbusiness_type").equals("1")){
//                getitembusinesstype="(a.business_type = 1 or a.business_type = 3)";
//            }else{
//                getitembusinesstype="(a.business_type = 1 or  a.business_type = 2 or a.business_type = 3)";
//            }
            if(arr.length>0) {
                for (int i = 0; i < arr.length; i++) {
                    if(arr[i].equals("2")){
                        getitembusinesstype=" (a.business_type = 2 or a.business_type = 3) ";
                    }else if(arr[i].equals("1")){
                        getitembusinesstype=" (a.business_type = 1 or a.business_type = 3) ";
                    }else{
                        getitembusinesstype=" (a.business_type = 1 or  a.business_type = 2 or a.business_type = 3) ";
                    }
                }
            }
        /*String sql =" select a.itemcode,a.companycode,a.brandcode,a.manualitemcode,a.itemname,a.itemnametamil,a.unitcode," +
                " a.unitweightunitcode,a.unitweight,a.uppunitcode,a.uppweight,a.itemcategory,a.parentitemcode," +
                " a.allowpriceedit,a.allownegativestock,a.allowdiscount, (sum(b.op)+sum(b.inward)-sum(b.outward)) as stockqty," +
                " (Select unitname  from tblunitmaster where unitcode=a.unitcode) as unitname," +
                " coalesce((Select noofdecimals from tblunitmaster where unitcode=a.unitcode),0) as noofdecimals," +
                " coalesce((select oldprice from tblitempricelisttransaction where itemcode=a.itemcode " +
                " order by autonum desc limit 1),0) as oldprice,coalesce((select newprice from tblitempricelisttransaction" +
                " where itemcode=a.itemcode order by autonum desc limit 1),0) as newprice,coalesce((select colourcode " +
                " from tblcompanymaster where companycode=a.companycode),'#000000') as colourcode,coalesce(c.hsn,'') " +
                " as hsn,coalesce(c.tax,'') as tax,(select allowpriceedit from tblroutedetails where routecode='"+getroutecode+"' and areacode='"+getareacode+"')" +
                " as routeallowpricedit,case when parentitemcode=0 then a.itemcode else parentitemcode " +
                " end as parentcode,case when itemcategory='parent' then 1 else  2 end as itemorder," +
                " c.itemsubgroupname,d.brandname " +
                " from tblitemmaster as a inner join tblstocktransaction as b " +
                " on a.itemcode=b.itemcode inner join tblitemsubgroupmaster as c on" +
                " c.itemsubgroupcode=a.itemsubgroupcode inner join tblbrandmaster as d on a.brandcode=d.brandcode " +
                " where a.itemsubgroupcode ='"+itemsubgroupcode+"' and a.status='"+statusvar+"' " +
                " and b.vancode='"+LoginActivity.getvancode+"' and" +
                " a.companycode in (select companycode from tblcompanymaster where status='"+statusvar+"') " +
                " group by a.itemcode,a.companycode,a.brandcode,a.manualitemcode," +
                " a.itemname,a.itemnametamil,a.unitcode,a.unitweightunitcode,a.unitweight,a.uppunitcode,a.uppweight," +
                " a.itemcategory,a.parentitemcode,a.allowpriceedit,a.allownegativestock,a.allowdiscount" +
                " order by c.itemsubgroupname,d.brandname,parentcode,itemorder,a.itemname ";*/
            String sql = "select * from (select a.itemcode,a.companycode,a.brandcode,a.manualitemcode," +
                    "a.itemname,a.itemnametamil,a.unitcode," +
                    " a.unitweightunitcode,a.unitweight,a.uppunitcode,a.uppweight,a.itemcategory,a.parentitemcode," +
                    " a.allowpriceedit,a.allownegativestock,a.allowdiscount, " +
                    " coalesce(coalesce((select sum(op)+sum(inward)-Sum(outward) from tblstocktransaction where itemcode=a.itemcode" +
                    " and vancode='"+preferenceMangr.pref_getString("getvancode")+"' and flag!=3),0) + coalesce((select sum(inward)-Sum(outward) from tblstockconversion where itemcode=a.itemcode "  +
                    " and vancode='"+preferenceMangr.pref_getString("getvancode")+"'  ),0),0)" +
                    " as stockqty," +
                    " (Select unitname  from tblunitmaster where unitcode=a.unitcode) as unitname," +
                    " coalesce((Select noofdecimals from tblunitmaster where unitcode=a.unitcode),0) as noofdecimals," +
                    " coalesce((select oldprice from tblitempricelisttransaction where itemcode=a.itemcode " +
                    " order by autonum desc limit 1),0) as oldprice, " +
                    " coalesce((select newprice from tblitempricelisttransaction " +
                    " where itemcode=a.itemcode order by autonum desc limit 1),0) as newprice, " +
                    " CASE WHEN itemtype=2 then (SELECT freeitemcolor from tblgeneralsettings) else" +
                    " coalesce((select colourcode " +
                    " from tblcompanymaster where companycode=a.companycode),'#000000') END as colourcode,coalesce(c.hsn,'') " +
                    " as hsn,coalesce(c.tax,'') as tax,(select allowpriceedit from tblroutedetails where routecode='"+getroutecode+"' and areacode='"+getareacode+"')" +
                    " as routeallowpricedit,case when parentitemcode=0 then a.itemcode else parentitemcode " +
                    " end as parentcode,case when itemcategory='parent' then 1 else  2 end as itemorder," +
                    " c.itemsubgroupname,d.brandname,(select count(*) from tblschemeratedetails as aa inner join " +
                    "  tblscheme as b on aa.schemecode=b.schemecode where aa.itemcode=a.itemcode and b.status='"+statusvar+"' " +
                    "  and (','||multipleroutecode||',') LIKE '%,"+ getroutecode +",%'" +
                    " and (','||multipleareacode||',')  LIKE '%,"+getareacode+",%' " +
                    "  and(validityfrom<=datetime('"+getdate+"')) and (ifnull(validityto,'')='' or " +
                    " (validityfrom<=datetime('"+getdate+"') " +
                    " and  validityto>=datetime('"+getdate+"'))) and  "+getbusinesstype+") as ratecount," +
                    " (select count(*) from tblschemeitemdetails as ab  inner join  tblscheme as b " +
                    " on ab.schemecode=b.schemecode where ab.purchaseitemcode=a.itemcode and b.status='"+statusvar+"' " +
                    "and (','||multipleroutecode||',')  LIKE '%,"+getroutecode+",%'" +
                    " and (','||multipleareacode||',')  LIKE '%,"+getareacode+",%' " +
                    " and(validityfrom<=datetime('"+getdate+"')) and (ifnull(validityto,'')='' or (validityfrom<=datetime('"+getdate+"') " +
                    " and  validityto>=datetime('"+getdate+"')))  and "+getbusinesstype+" ) as freecount, " +
                    " coalesce(coalesce((select sum(op)+sum(inward)-Sum(outward) from tblstocktransaction where itemcode=a.parentitemcode " +
                    " and vancode='"+preferenceMangr.pref_getString("getvancode")+"' and flag!=3),0) + " +
                    " coalesce((select sum(inward)-Sum(outward) from tblstockconversion where itemcode=a.parentitemcode " +
                    " and vancode='"+preferenceMangr.pref_getString("getvancode")+"'  ),0),0) " +
                    " as parentstockqty,(case when(a.minimumsalesqty<>'null' or a.minimumsalesqty<>null) then a.minimumsalesqty else 0 end) as minimumsalesqty,"+
                    " case when parentitemcode=0 then upp else (Select upp from tblitemmaster where itemcode=a.parentitemcode) end as upp,itemtype"+
                    " from tblitemmaster as a  inner join tblitemsubgroupmaster as c on " +
                    " c.itemsubgroupcode=a.itemsubgroupcode inner join tblbrandmaster as d on a.brandcode=d.brandcode " +
                    " where " + getitembusinesstype + " and a.itemsubgroupcode ='"+itemsubgroupcode+"' and a.status='"+statusvar+"'  " +
                    " and (a.itemcode in (select itemcode from tblstocktransaction where  flag!=3) " +
                    " or parentcode in (select itemcode from tblstocktransaction where  flag!=3)) and " +
                    " a.companycode in (select companycode from tblcompanymaster where status='"+statusvar+"' ) ) as dec " +
                    " where (itemcategory='child'  and (parentstockqty>0 or stockqty>0) ) or (itemcategory= 'parent' and stockqty>0 )   " +
                    " order by   itemtype,itemcategory desc,uppweight desc";

            //brandname
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0) {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }


    //Get Scheme Details
    public Cursor GetSchemeFORItemDB(String getitemcode,String getroutecode,String getqty)
    {
        Cursor mCur = null;
        try{
            String GenDate= GenCreatedDate();
            String getbusinesstype = "";
//            if(preferenceMangr.pref_getString("getbusiness_type").equals("2")){
//                getbusinesstype=" (b.business_type = 2 or b.business_type = 3) ";
//            }else  if(preferenceMangr.pref_getString("getbusiness_type").equals("1")){
//                getbusinesstype="(b.business_type = 1 or b.business_type = 3)";
//            }else{
//                getbusinesstype="(b.business_type = 1 or  b.business_type = 2 or b.business_type = 3)";
//            }
            String arr[]=preferenceMangr.pref_getString("getbusiness_type").split(",");
            if(arr.length>0) {
                for (int i = 0; i < arr.length; i++) {
                    if(arr[i].equals("2")){
                        getbusinesstype=" ((','||b.business_type||',') LIKE '%,2,%' or (','||b.business_type||',') LIKE '%,3,%') ";
                    }else if(arr[i].equals("1")){
                        getbusinesstype=" ((','||b.business_type||',') LIKE '%,1,%' or (','||b.business_type||',') LIKE '%,3,%') ";
                    }else{
                        getbusinesstype=" ((','||b.business_type||',') LIKE '%,1,%' or (','||b.business_type||',') LIKE '%,2,%' or (','||b.business_type||',') LIKE '%,3,%') ";
                    }
                }
            }
            String sql ="select coalesce(a.minqty,0) as minqty,coalesce(a.discountamount,0) as discountamount from tblschemeratedetails as a inner join " +
                    " tblscheme as b on a.schemecode=b.schemecode where a.itemcode='"+getitemcode+"'" +
                    " and b.status='"+statusvar+"' and "+getbusinesstype+" and (','||multipleroutecode||',') LIKE '%,"+ getroutecode +",%' and" +
                    "(validityfrom<=datetime('"+GenDate+"')) " +
                    "and (ifnull(validityto,'')='' " +
                    "or (validityfrom<=datetime('"+GenDate+"') and  validityto>=datetime('"+GenDate+"')))  and  a.minqty <='"+getqty+"' order by minqty desc limit 1 ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Scheme Details
    public Cursor GetOrderSchemeFORItemDB(String getitemcode,String getroutecode,String getqty)
    {
        Cursor mCur = null;
        try{
            String GenDate= GenCreatedDate();
            String getbusinesstype = "";
//            if(preferenceMangr.pref_getString("getbusiness_type").equals("2")){
//                getbusinesstype=" (b.business_type = 2 or b.business_type = 3) ";
//            }else  if(preferenceMangr.pref_getString("getbusiness_type").equals("1")){
//                getbusinesstype="(b.business_type = 1 or b.business_type = 3)";
//            }else{
//                getbusinesstype="(b.business_type = 1 or  b.business_type = 2 or b.business_type = 3)";
//            }
            String arr[]=preferenceMangr.pref_getString("getbusiness_type").split(",");
            if(arr.length>0) {
                for (int i = 0; i < arr.length; i++) {
                    if(arr[i].equals("2")){
                        getbusinesstype="  (','||b.business_type||',') LIKE '%,2,%' or (','||b.business_type||',') LIKE '%,3,%') ";
                    }else if(arr[i].equals("1")){
                        getbusinesstype=" ((','||b.business_type||',') LIKE '%,1,%' or (','||b.business_type||',') LIKE '%,3,%') ";
                    }else{
                        getbusinesstype=" ((','||b.business_type||',') LIKE '%,1,%' or (','||b.business_type||',') LIKE '%,2,%' or (','||b.business_type||',') LIKE '%,3,%') ";
                    }
                }
            }
            String sql ="select coalesce(a.minqty,0) as minqty,coalesce(a.discountamount,0) as discountamount from tblschemeratedetails as a inner join " +
                    " tblscheme as b on a.schemecode=b.schemecode where a.itemcode='"+getitemcode+"' " +
                    "and b.status='"+statusvar+"' and "+getbusinesstype+" and (','||multipleroutecode||',') LIKE '%,"+ getroutecode +",%' and" +
                    "(validityfrom<=datetime('"+GenDate+"')) " +
                    "and (ifnull(validityto,'')='' " +
                    "or (validityfrom<=datetime('"+GenDate+"') and  validityto>=datetime('"+GenDate+"'))) " +
                    " and  a.minqty <='"+getqty+"' order by minqty desc limit 1 ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Scheme  Free Item Details
    public Cursor GetSchemeFORFreeItemDB(String getitemcode,String getroutecode)
    {
        Cursor mCur = null;
        try{
            String getbusinesstype = "";
//            if(preferenceMangr.pref_getString("getbusiness_type").equals("2")){
//                getbusinesstype=" (b.business_type = 2 or b.business_type = 3) ";
//            }else  if(preferenceMangr.pref_getString("getbusiness_type").equals("1")){
//                getbusinesstype="(b.business_type = 1 or b.business_type = 3)";
//            }else{
//                getbusinesstype="(b.business_type = 1 or  b.business_type = 2 or b.business_type = 3)";
//            }
            String arr[]=preferenceMangr.pref_getString("getbusiness_type").split(",");
            if(arr.length>0) {
                for (int i = 0; i < arr.length; i++) {
                    if(arr[i].equals("2")){
                        getbusinesstype=" (','||b.business_type||',') LIKE '%,2,%' or (','||b.business_type||',') LIKE '%,3,%') ";
                    }else if(arr[i].equals("1")){
                        getbusinesstype=" ((','||b.business_type||',') LIKE '%,1,%' or (','||b.business_type||',') LIKE '%,3,%') ";
                    }else{
                        getbusinesstype=" ((','||b.business_type||',') LIKE '%,1,%' or (','||b.business_type||',') LIKE '%,2,%' or (','||b.business_type||',') LIKE '%,3,%') ";
                    }
                }
            }
            String GenDate= GenCreatedDate();
            String sql ="select purchaseitemcode,b.purchaseqty,b.freeitemcode,b.freeqty,schemetype from tblschemeitemdetails as a  inner join " +
                    " tblscheme as b on a.schemecode=b.schemecode where a.purchaseitemcode='"+getitemcode+"'" +
                    " and b.status='"+statusvar+"'  and "+getbusinesstype+" and (','||multipleroutecode||',') LIKE '%,"+ getroutecode +",%' and" +
                    "(validityfrom<=datetime('"+GenDate+"')) " +
                    "and (ifnull(validityto,'')='' " +
                    "or (validityfrom<=datetime('"+GenDate+"') and  validityto>=datetime('"+GenDate+"'))) ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Scheme  Free Item Details
    public Cursor GetOrderSchemeFORFreeItemDB(String getitemcode,String getroutecode)
    {
        Cursor mCur = null;
        try{
            String getbusinesstype = "";
//            if(preferenceMangr.pref_getString("getbusiness_type").equals("2")){
//                getbusinesstype=" (b.business_type = 2 or b.business_type = 3) ";
//            }else  if(preferenceMangr.pref_getString("getbusiness_type").equals("1")){
//                getbusinesstype="(b.business_type = 1 or b.business_type = 3)";
//            }else{
//                getbusinesstype="(b.business_type = 1 or  b.business_type = 2 or b.business_type = 3)";
//            }
            String arr[]=preferenceMangr.pref_getString("getbusiness_type").split(",");
            if(arr.length>0) {
                for (int i = 0; i < arr.length; i++) {
                    if(arr[i].equals("2")){
                        getbusinesstype=" ((','||b.business_type||',') LIKE '%,2,%' or (','||b.business_type||',') LIKE '%,3,%') ";
                    }else if(arr[i].equals("1")){
                        getbusinesstype=" ((','||b.business_type||',') LIKE '%,1,%' or (','||b.business_type||',') LIKE '%,3,%') ";
                    }else{
                        getbusinesstype=" ((','||b.business_type||',') LIKE '%,1,%' or (','||b.business_type||',') LIKE '%,2,%' or (','||b.business_type||',') LIKE '%,3,%') ";
                    }
                }
            }
            String GenDate= GenCreatedDate();
            String sql ="select purchaseitemcode,b.purchaseqty,b.freeitemcode,b.freeqty,b.schemetype from tblschemeitemdetails as a  inner join " +
                    " tblscheme as b on a.schemecode=b.schemecode where a.purchaseitemcode='"+getitemcode+"'" +
                    " and b.status='"+statusvar+"' and "+getbusinesstype+" and (','||multipleroutecode||',') LIKE '%,"+ getroutecode +",%' and" +
                    "(validityfrom<=datetime('"+GenDate+"')) " +
                    "and (ifnull(validityto,'')='' " +
                    "or (validityfrom<=datetime('"+GenDate+"') and  validityto>=datetime('"+GenDate+"'))) ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get Stock for parent item details
    public Cursor GetStockForItem(String getitemcode)
    {
        Cursor mCur = null;
        try{
            String sql ="select (coalesce(sum(a.op)+sum(a.inward)-sum(a.outward),0))+" +
                    " (select coalesce((sum(inward)-sum(outward)),0) from tblstockconversion where itemcode='"+getitemcode+"' )" +
                    " as stock,b.itemnametamil," +
                    "(select unitname from tblunitmaster where unitcode=b.unitcode) as unitname,coalesce(b.upp,0) as upp " +
                    " from tblstocktransaction as a inner join tblitemmaster as b on a.itemcode=b.itemcode  " +
                    " where a.itemcode='"+getitemcode+"' and vancode='"+preferenceMangr.pref_getString("getvancode")+"' and a.flag!=3  ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Stock for parent item details
    public String GetCartItemStock(String getitemcode)
    {
        String getqty = "0";
        try{
            String sql ="select coalesce(itemqty,0) from tblsalescartdatas where itemcode = '"+getitemcode+"' ";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getqty = mCur.getString(0);
            }else{
                getqty = "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getqty;
    }
    //Get scheme item in cart
    public String GetCartItemScheme(String getitemcode,String getroutecode)
    {
        String getqty = "0";
        try{
            String getbusinesstype = "";
            String arr[]=preferenceMangr.pref_getString("getbusiness_type").split(",");
            if(arr.length>0) {
                for (int i = 0; i < arr.length; i++) {
                    if(arr[i].equals("2")){
                        getbusinesstype=" ((','||a.business_type||',') LIKE '%,2,%' or (','||a.business_type||',') LIKE '%,3,%') ";
                    }else if(arr[i].equals("1")){
                        getbusinesstype=" ((','||a.business_type||',') LIKE '%,1,%' or (','||a.business_type||',') LIKE '%,3,%') ";
                    }else{
                        getbusinesstype=" ((','||a.business_type||',') LIKE '%,1,%' or (','||a.business_type||',') LIKE '%,2,%' or (','||a.business_type||',') LIKE '%,3,%') ";
                    }
                }
            }
            String GenDate= GenCreatedDate();
//            String sql ="select coalesce(itemqty,0) from tblsalescartdatas where itemcode = '"+getitemcode+"' ";
            String sql="SELECT coalesce(sum(itemqty*unitweight),0) from tblsalescartdatas where itemcode in(" +
                    " SELECT purchaseitemcode from tblschemeitemdetails" +
                    " where schemecode=(select a.schemecode from tblscheme as a inner join tblschemeitemdetails as b on a.schemecode=b.schemecode" +
                    " where purchaseitemcode='"+getitemcode+"' and a.status='"+statusvar+"' and schemetype='item' and "+getbusinesstype+" and (','||multipleroutecode||',') LIKE '%,"+ getroutecode +",%' and" +
                    " (validityfrom<=datetime('"+GenDate+"')) and (ifnull(validityto,'')='' or (validityfrom<=datetime('"+GenDate+"')" +
                    " and  validityto>=datetime('"+GenDate+"')))" +
                    " ))";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getqty = mCur.getString(0);
            }else{
                getqty = "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getqty;
    }

    //Get scheme item in cart
    public String GetCartPurchaseItemforScheme(String getitemcode,String getroutecode)
    {
        String getmultiplepurchasecode = "0";
        try{
            String getbusinesstype = "";
            String arr[]=preferenceMangr.pref_getString("getbusiness_type").split(",");
            if(arr.length>0) {
                for (int i = 0; i < arr.length; i++) {
                    if(arr[i].equals("2")){
                        getbusinesstype=" ((','||a.business_type||',') LIKE '%,2,%' or (','||a.business_type||',') LIKE '%,3,%') ";
                    }else if(arr[i].equals("1")){
                        getbusinesstype=" ((','||a.business_type||',') LIKE '%,1,%' or (','||a.business_type||',') LIKE '%,3,%') ";
                    }else{
                        getbusinesstype=" ((','||a.business_type||',') LIKE '%,1,%' or (','||a.business_type||',') LIKE '%,2,%' or (','||a.business_type||',') LIKE '%,3,%') ";
                    }
                }
            }
            String GenDate= GenCreatedDate();

            String sql="SELECT *,(cast(totalweight as INTEGER)/cast(purchaseqty as INTEGER)) as flag from (SELECT " +
                    "group_concat(itemcode,',') as multipleitemcode,cast(sum(itemweight) as INTEGER) as totalweight,*" +
                    " from (SELECT (select (a.purchaseqty) from tblscheme as a  inner join tblschemeitemdetails " +
                    "as b on a.schemecode=b.schemecode  where purchaseitemcode='"+getitemcode+"' and a.status='"+statusvar+"' " +
                    " and schemetype='item' and "+getbusinesstype+" and (','||multipleroutecode||',') LIKE '%,"+getroutecode+",%' and (validityfrom<=datetime('"+GenDate+"')) " +
                    "and (ifnull(validityto,'')='' or (validityfrom<=datetime('"+GenDate+"') and  " +
                    " validityto>=datetime('"+GenDate+"')))  ) as purchaseqty, coalesce((itemqty*unitweight),0) as itemweight," +
                    " itemcode,itemqty,cartcode from tblsalescartdatas where itemcode in(SELECT purchaseitemcode from " +
                    "tblschemeitemdetails where schemecode=(select distinct a.schemecode from tblscheme as a inner join " +
                    "tblschemeitemdetails as b on a.schemecode=b.schemecode where purchaseitemcode='"+getitemcode+"' and " +
                    "a.status='"+statusvar+"' and schemetype='item' and "+getbusinesstype+" and " +
                    "(','||multipleroutecode||',') LIKE '%,"+ getroutecode +",%' and (validityfrom<=datetime('"+GenDate+"')) and " +
                    "(ifnull(validityto,'')='' or (validityfrom<=datetime('"+GenDate+"') and " +
                    " validityto>=datetime('"+GenDate+"')))))) as dev) as dev1 where flag>0";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getmultiplepurchasecode= mCur.getString(0);
            }else{
                getmultiplepurchasecode = "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getmultiplepurchasecode;
    }
    //Get Stock for parent item details
    public String GetCartOrderItemStock(String getitemcode)
    {
        String getqty = "0";
        try{
            String sql ="select coalesce(itemqty,0) from tblsalesordercartdatas where itemcode = '"+getitemcode+"' ";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getqty = mCur.getString(0);
            }else{
                getqty = "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getqty;
    }

    //Get Item qty and price
    public Cursor GetCartItemQtyAndPrice(String getitemcode)
    {
        Cursor mCur = null;
        try{
            String sql ="select coalesce(itemqty,0),coalesce(newprice,0) " +
                    " from tblsalescartdatas where itemcode = '"+getitemcode+"' ";
            mCur = mDb.rawQuery(sql, null);
            String getqty = "0";
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Item qty and price
    public Cursor GetCartOrderItemQtyAndPrice(String getitemcode)
    {
        Cursor mCur = null;
        try{
            String sql ="select coalesce(itemqty,0),coalesce(newprice,0) " +
                    " from tblsalesordercartdatas where itemcode = '"+getitemcode+"' ";
            mCur = mDb.rawQuery(sql, null);
            String getqty = "0";
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get Stock for parent item details
    public String GetFreeCartItemStock(String getitemcode)
    {
        String getqty = "0";
        try{
//            String sql ="select coalesce(itemqty,0) from tblsalescartdatas where itemcode = '"+getitemcode+"' and freeflag !='' ";
            String sql ="select coalesce(itemqty,0) from tblsalescartdatas where itemcode = '"+getitemcode+"' and freeflag ='freeitem' ";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getqty = mCur.getString(0);
            }else{
                getqty = "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getqty;
    }


    //Get Purchase Items
    public Cursor GetPurchaseItems(String itemcode,String getroutecode,String getareacode)
    {
        Cursor mCur = null;
        try{
            getdate = GenCreatedDate();
            String sql ="select a.itemcode,a.companycode,a.brandcode,a.manualitemcode,a.itemname,a.itemnametamil,a.unitcode," +
                    "a.unitweightunitcode,a.unitweight,a.uppunitcode,a.uppweight,a.itemcategory,a.parentitemcode," +
                    "a.allowpriceedit,a.allownegativestock,a.allowdiscount, " +
                    "(select coalesce((sum(op)+sum(inward)-sum(outward)),0) from tblstocktransaction where itemcode='"+itemcode+"' and flag!=3 ) " +
                    " + (select coalesce((sum(inward)-sum(outward)),0) from tblstockconversion where itemcode='"+itemcode+"' ) as stockqty," +
                    "(Select unitname  from tblunitmaster where unitcode=a.unitcode) as unitname," +
                    "coalesce((Select noofdecimals from tblunitmaster where unitcode=a.unitcode),0) as noofdecimals," +
                    "coalesce((select oldprice from tblitempricelisttransaction where itemcode=a.itemcode " +
                    "order by autonum desc limit 1),0) as oldprice,coalesce((select newprice from tblitempricelisttransaction" +
                    " where itemcode=a.itemcode order by autonum desc limit 1),0) as newprice," +
                    "CASE WHEN itemtype=2 then (SELECT freeitemcolor from tblgeneralsettings)" +
                    "else coalesce((select colourcode " +
                    "from tblcompanymaster where companycode=a.companycode),'#000000') END as colourcode,coalesce(c.hsn,'') " +
                    "as hsn,coalesce(c.tax,'') as tax,(select allowpriceedit from tblroutedetails where routecode='"+getroutecode+"' and areacode='"+getareacode+"')" +
                    " as routeallowpricedit ,(select itemname from tblitemmaster as parent where parent.itemcode=a.itemcode) " +
                    " as parentitemnae,a.orderstatus,a.maxorderqty,(case when(a.minimumsalesqty<>'null' or a.minimumsalesqty<>null) then a.minimumsalesqty else 0 end) as minimumsalesqty,a.itemsubgroupcode" +
                    " from tblitemmaster as a " +
                    "   inner join tblitemsubgroupmaster as c " +
                    " on c.itemsubgroupcode=a.itemsubgroupcode " +
                    " where a.itemcode ='"+itemcode+"' and a.status='"+statusvar+"'   group by a.itemcode,a.companycode,a.brandcode,a.manualitemcode," +
                    "a.itemname,a.itemnametamil,a.unitcode,a.unitweightunitcode,a.unitweight,a.uppunitcode,a.uppweight," +
                    "a.itemcategory,a.parentitemcode,a.allowpriceedit,a.allownegativestock,a.allowdiscount ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }



    //Get Purchase Items
    public String GetFreeitemname(String itemcode)
    {
        Cursor mCur =null;
        try{
            getdate = GenCreatedDate();
            String sql ="select a.itemnametamil " +
                    " from tblitemmaster as a "+
                    " where a.itemcode ='"+itemcode+"' and a.status='"+statusvar+"'  ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur.getString(0);
    }

    //Get tripadavance for corresponding van and schedule date
    public String GetScheduleTripAdavanceDB(String getdate)
    {
        String gettripadvance="0";
        try{
            String sql =" select schedulecode,coalesce(tripadvance,0) from " +
                    " tblsalesschedule as a where  vancode='"+preferenceMangr.pref_getString("getvancode")+"' " +
                    " and  scheduledate=datetime('"+getdate+"')  ";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                gettripadvance = mCur.getString(1);
            }else{
                gettripadvance = "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return gettripadvance;
    }
    //Get Expense List
    public Cursor GetExpenseListDB(String getdate)
    {
        Cursor mCur = null;
        try{
            String sql ="select transactionno,strftime('%d-%m-%Y',transactiondate) transactiondate,expensesheadcode," +
                    " amount,remarks," +
                    "(select expenseshead from tblexpenseshead where expensesheadcode=a.expensesheadcode) as expensesheadname," +
                    "(select expensesheadtamil from tblexpenseshead where expensesheadcode=a.expensesheadcode)  " +
                    " as expensesheadnametamil ,schedulecode" +
                    " from tblexpenses as a where transactiondate=datetime('"+getdate+"') and ( flag=1 or flag=2) ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get Customer List
    public Cursor GetCustomerListDB(String getroutecode,String getareacode,String getmobilenoverificationstatus)
    {
        Cursor mCur = null;
        try{
            String routecode="";
            if(getroutecode.equals("0") || getroutecode.equals("")){
                routecode = "1=1";
            }else{
                routecode="c.routecode='"+getroutecode+"'";
            }

            String areacode="";
            if(getareacode.equals("0") || getareacode.equals("")){
                areacode = "1=1";
            }else{
                areacode="a.areacode='"+getareacode+"'";
            }
            String sql = "";
            String getbusinesstype = "";
            if(preferenceMangr.pref_getString("getbusiness_type").equals("2")){
                getbusinesstype=" (a.business_type = 2 or a.business_type = 3) ";
            }else  if(preferenceMangr.pref_getString("getbusiness_type").equals("1")){
                getbusinesstype="(a.business_type = 1 or a.business_type = 3)";
            }else{
                getbusinesstype="(a.business_type = 1 or  a.business_type = 2 or a.business_type = 3)";
            }
            String mobilenoverificationstatus="";
//            if(getmobilenoverificationstatus.equals("0") || getmobilenoverificationstatus.equals("")){
//                mobilenoverificationstatus = "1=1";
//            }else{
//                mobilenoverificationstatus="a.mobilenoverificationstatus='"+getmobilenoverificationstatus+"'";
//            }
            if(getmobilenoverificationstatus.equals("All Mobile status")){
                mobilenoverificationstatus = "1=1";
            }else if(getmobilenoverificationstatus.equals("Verified")){
                mobilenoverificationstatus ="a.mobilenoverificationstatus='1'";
            }else{
                mobilenoverificationstatus ="a.mobilenoverificationstatus='0'";
            }
            sql ="select distinct customercode,customername,customernametamil,address," +
                    " a.areacode,mobileno,coalesce(telephoneno,'') as telephoneno,gstin ," +
                    "b.areanametamil,b.citycode,(select citynametamil from tblcitymaster where citycode=(" +
                    "select citycode from tblareamaster where areacode=b.areacode)) as citynametamil,emailid,aadharno," +
                    "a.customertypecode,a.business_type,coalesce(whatsappno,'') as whatsappno," +
                    "coalesce(mobilenoverificationstatus,0) as mobilenoverificationstatus " +
                    "from tblcustomer as a inner join tblareamaster as b on a.areacode=b.areacode " +
                    " left outer join tblroutedetails as c on c.areacode=b.areacode where "+areacode+" " +
                    " and "+routecode+" and a.status='"+statusvar+"' and "+getbusinesstype+"" +
                    " and "+mobilenoverificationstatus+" order by refno desc ";


            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Receipt List
    public Cursor GetReceiptListDB(String getdate,String getcompanycode,String getareacode)
    {
        Cursor mCur = null;
        try{
            String companycode="";
            if(getcompanycode.equals("0") || getcompanycode.equals("")){
                companycode = "1=1";
            }else{
                companycode="a.companycode='"+getcompanycode+"'";
            }

            String areacode="";
            if(getareacode.equals("0") || getareacode.equals("")){
                areacode = "1=1";
            }else{
                areacode="b.areacode='"+getareacode+"'";
            }
            String sql ="select a.transactionno,strftime('%d-%m-%Y',a.receiptdate) as receiptdate,a.voucherno,a.refno,a.companycode,a.vancode,a.customercode,a.schedulecode," +
                    "a.receiptremarkscode,a.receiptmode,a.chequerefno,a.amount,a.note,b.customernametamil," +
                    "(select areanametamil from tblareamaster where areacode=b.areacode) as areaname," +
                    "(select citynametamil from tblcitymaster where citycode=" +
                    "(select citycode from tblareamaster where areacode=b.areacode)) as cityname," +
                    "(select shortname from tblcompanymaster where companycode=a.companycode) as shortname,a.flag,a.financialyearcode" +
                    " from tblreceipt as a inner join tblcustomer as b on a.customercode=b.customercode where "+areacode+" " +
                    " and "+companycode+" and a.receiptdate=datetime('"+getdate+"') and b.status='"+statusvar+"'  AND a.type='Receipt' order by transactionno desc ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Route List
    public Cursor GetRouteListDB()
    {
        Cursor mCur = null;
        try{
            String sql =" select distinct b.routecode,b.routename,b.routenametamil " +
                    " FROM  tblroute as b  where " +
                    " b.vancode='"+preferenceMangr.pref_getString("getvancode")+"' and b.status='"+statusvar+"' order by b.routenametamil ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }


    //Check voucher settings for sales
    public String GetSalesReturnVoucherSettingsDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select count(count1-count2) from (select sum(count1) as count1,sum(count2) as count2 " +
                    " from (select count(*) as count1,0 as count2 from tblcompanymaster,tblbilltype  " +
                    " where companycode in (select companycode from tblitemmaster ) union all " +
                    "select 0 , count(*) from tblvouchersettings " +
                    "where financialyearcode='"+preferenceMangr.pref_getString("getfinanceyrcode")+"' and " +
                    " type='salesreturn' and vancode='"+preferenceMangr.pref_getString("getvancode")+"')" +
                    " as derv) as de where count1<>count2;";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur.getString(0);
    }
    //Get sub group based on stock transfer items for sales Returb
    public Cursor GetSubGroupDBSalesReturn()
    {
        Cursor mCur = null;
        try{
            getdate = GenCreatedDate();
            String sql ="select distinct c.itemsubgroupcode,c.itemsubgroupname,c.itemsubgroupnametamil from " +
                    "tblitemmaster  as b  inner join " +
                    "tblitemsubgroupmaster as c on c.itemsubgroupcode=b.itemsubgroupcode where " +
                    " b.status='"+statusvar+"' and c.Status='"+statusvar+"' order by c.itemgroupcode ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get Items Sales Return
    public Cursor GetItemsDBSalesReturn(String itemsubgroupcode,String getroutecode,String getareacode)
    {
        Cursor mCur = null;
        try{
            getdate = GenCreatedDate();
            String sql ="select a.itemcode,a.companycode,a.brandcode,a.manualitemcode,a.itemname,a.itemnametamil,a.unitcode," +
                    "a.unitweightunitcode,a.unitweight,a.uppunitcode,a.uppweight,a.itemcategory,a.parentitemcode," +
                    "a.allowpriceedit,a.allownegativestock,a.allowdiscount, (sum(b.op)+sum(b.inward)-sum(b.outward)) as stockqty," +
                    "(Select unitname  from tblunitmaster where unitcode=a.unitcode) as unitname," +
                    "coalesce((Select noofdecimals from tblunitmaster where unitcode=a.unitcode),0) as noofdecimals," +
                    "coalesce((select oldprice from tblitempricelisttransaction where itemcode=a.itemcode " +
                    "order by autonum desc limit 1),0) as oldprice,coalesce((select newprice from tblitempricelisttransaction" +
                    " where itemcode=a.itemcode order by autonum desc limit 1),0) as newprice,CASE WHEN itemtype=2 then" +
                    " (SELECT freeitemcolor from tblgeneralsettings) else coalesce((select colourcode " +
                    "from tblcompanymaster where companycode=a.companycode),'#000000') END as colourcode,coalesce(c.hsn,'') " +
                    "as hsn,coalesce(c.tax,'') as tax,(select allowpriceedit from tblroutedetails where routecode='"+getroutecode+"' and areacode='"+getareacode+"')" +
                    " as routeallowpricedit,case when parentitemcode=0 then a.itemcode else parentitemcode " +
                    " end as parentcode,case when itemcategory='parent' then 1 else  2 end as itemorder, " +
                    "  c.itemsubgroupname,d.brandname,(case when(a.minimumsalesqty<>'null' or a.minimumsalesqty<>null) then a.minimumsalesqty else 0 end) as minimumsalesqty " +
                    " from tblitemmaster as a inner join tblstocktransaction as b " +
                    " inner join tblitemsubgroupmaster as c on c.itemsubgroupcode=a.itemsubgroupcode" +
                    " inner join tblbrandmaster as d on a.brandcode=d.brandcode  where " +
                    " a.itemsubgroupcode ='"+itemsubgroupcode+"' and a.status='"+statusvar+"' and b.flag!=3  " +
                    "and itemtype!=2 group by a.itemcode,a.companycode,a.brandcode,a.manualitemcode," +
                    "a.itemname,a.itemnametamil,a.unitcode,a.unitweightunitcode,a.unitweight,a.uppunitcode,a.uppweight," +
                    "a.itemcategory,a.parentitemcode,a.allowpriceedit,a.allownegativestock,a.allowdiscount" +
                    " order by itemtype,c.itemsubgroupname,d.brandname,a.itemcategory desc ";
            //parentcode,itemorder,a.itemname
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get sub group based on stock transfer items for Sales Return
    public Cursor GetGroupDBSalesReturn()
    {
        Cursor mCur = null;
        try{
            getdate = GenCreatedDate();
            String sql ="select distinct d.itemgroupcode,d.itemgroupname,d.itemgroupnametamil from " +
                    " tblitemmaster  as b  inner join tblitemsubgroupmaster as c " +
                    "on c.itemsubgroupcode=b.itemsubgroupcode inner join tblitemgroupmaster as d   " +
                    "on d.itemgroupcode = c.itemgroupcode where " +
                    " b.status='"+statusvar+"' and c.Status='"+statusvar+"' order by c.itemgroupcode ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Booking No Sales Return
    public String GetBookingNoSalesReturn()
    {
        Cursor mCur = null;
        try{
            String Gencode= GenCreatedDate();
            /*String sql ="select  case when (select Count(*) from tblsalesreturn)=0 then case when " +
                    "(select coalesce(max(bookingno),1) from tblmaxcode where code=3 " +
                    " and datetime(transdate)= datetime('"+Gencode+"'))=1 " +
                    " then  coalesce(max(bookingno),0)+1 " +
                    "else (select coalesce(max(bookingno),0)+1 from tblmaxcode where" +
                    " code=3  and datetime(transdate)= datetime('"+Gencode+"')) " +
                    " end else  coalesce(max(bookingno),0)+1  " +
                    "end as bookingno from tblsalesreturn as  a where " +
                    "financialyearcode='"+LoginActivity.getfinanceyrcode+"' " +
                    " and datetime(billdate)= datetime('"+Gencode+"') ; ";*/
            String sql ="select   coalesce(max(bookingno),0)+1  " +
                    "  as bookingno from tblsalesreturn as  a where financialyearcode='"+preferenceMangr.pref_getString("getfinanceyrcode") +"'" +
                    " and datetime(billdate)= datetime('"+Gencode+"'); ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur.getString(0);
    }

    //Get Tranasaction No Sales Return
    public String GetTransactionNoSalesReturn()
    {
        Cursor mCur = null;
        try{
            String sql ="select  case when (select Count(*) from tblsalesreturn)=0 then case when " +
                    "(select coalesce(max(transactionno),1) from tblmaxcode where code=33)=1 " +
                    "then  coalesce(max(transactionno),0)+1 " +
                    "else (select coalesce(max(transactionno),0)+1 from tblmaxcode where code=33) " +
                    "end else  coalesce(max(transactionno),0)+1  " +
                    "end as transactionno from tblsalesreturn as  a ; ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur.getString(0);
    }


    //Insert into Sales Return
    public String InsertSalesReturn(String vancode,String billdate,String customercode,String billtypecode,
                                    String gstin,String schedulecode,String subtotal,String discount,String
                                            grandtotal,String financialyearcode,String remarks,String bookingno,
                                    String Transactionno,String getrefno)
    {
        try{
            String Gencode= GenCreatedDate();
            mDb = mDbHelper.getReadableDatabase();
            String getcurtime = GetDateTime();

            String sql="INSERT INTO tblsalesreturn(autonum,companycode,vancode,transactionno,billno,refno,bookingno,prefix,suffix,billdate,customercode,billtypecode,gstin," +
                    "schedulecode,subtotal,discount,grandtotal,flag,makerid,createddate,financialyearcode,remarks,syncstatus,salestime,beforeroundoff)" +
                    " select (select coalesce(max(autonum),0)+1 from tblsalesreturn)+companycode,companycode,'"+ vancode +"','"+ Transactionno +"', case when " +
                    "(select Count(*) from tblsalesreturn where  companycode=a.companycode and billtypecode = "+billtypecode+")=0 then " +
                    "case when (select coalesce(max(refno),0)+1 " +
                    "from tblmaxcode where code=333 and companycode=a.companycode and billtypecode = "+billtypecode+" )=1 then " +
                    "(select prefix || printf('%0'||noofdigit||'d', startingno)||suffix FROM tblvouchersettings" +
                    " where type='salesreturn' and billtypecode='"+ billtypecode +"' and vancode='"+ vancode +"' " +
                    "and companycode=a.companycode and financialyearcode='"+ financialyearcode +"')" +
                    "else" +
                    "(select prefix || printf('%0'||noofdigit||'d', (select coalesce(max(refno),0)+1 " +
                    "from tblmaxcode where code=333 and companycode=a.companycode and billtypecode = "+billtypecode+"))" +
                    " ||suffix FROM tblvouchersettings where type='salesreturn' " +
                    "and billtypecode='"+ billtypecode +"' and vancode='"+ vancode +"' and companycode=a.companycode" +
                    " and financialyearcode='"+ financialyearcode +"')" +
                    "end else" +
                    "(select prefix || printf('%0'||noofdigit||'d', (select coalesce(max(refno),0)+1 from tblsalesreturn where" +
                    " companycode=a.companycode and financialyearcode='"+ financialyearcode +"' and billtypecode = "+billtypecode+" ))" +
                    "||suffix FROM tblvouchersettings where type='salesreturn' and billtypecode='"+ billtypecode +"'" +
                    " and vancode='"+ vancode +"' and companycode=a.companycode and " +
                    "financialyearcode='"+ financialyearcode +"')" +
                    " end as billno," +
                    "case when (select Count(*) from tblsalesreturn where  companycode=a.companycode and billtypecode = "+billtypecode+")=0 then" +
                    " case when (select coalesce(max(refno),0)+1 " +
                    " from tblmaxcode where code=333 and companycode=a.companycode and billtypecode = "+billtypecode+")=1 then " +
                    " (select  printf('%0'||noofdigit||'d', startingno) FROM tblvouchersettings where " +
                    "type='salesreturn' and billtypecode='"+ billtypecode +"' and vancode='"+ vancode +"' " +
                    "and companycode=a.companycode and financialyearcode='"+ financialyearcode +"')" +
                    " else " +
                    " (select printf('%0'||noofdigit||'d', (select coalesce(max(refno),0)+1 from tblmaxcode " +
                    "where code=333 and companycode=a.companycode and billtypecode = "+billtypecode+" )) FROM" +
                    " tblvouchersettings where type='salesreturn' " +
                    " and billtypecode='"+ billtypecode +"'" +
                    " and vancode='"+ vancode +"' and companycode=a.companycode and " +
                    "financialyearcode='"+ financialyearcode +"')" +
                    "end else" +
                    "(select printf('%0'||noofdigit||'d', (select coalesce(max(refno),0)+1 from tblsalesreturn where " +
                    " companycode=a.companycode and financialyearcode='"+ financialyearcode +"' and billtypecode = "+billtypecode+")) " +
                    "FROM tblvouchersettings where type='salesreturn' and billtypecode='"+ billtypecode +"' " +
                    "and vancode='"+ vancode +"' and companycode=a.companycode and " +
                    "financialyearcode='"+ financialyearcode +"')" +
                    " end as refno,'"+ bookingno +"',(select prefix FROM tblvouchersettings where" +
                    " type='salesreturn' and billtypecode='"+ billtypecode +"' and vancode='"+ vancode +"' " +
                    "and companycode=a.companycode and financialyearcode='"+ financialyearcode +"') as prefix," +
                    " (select suffix FROM tblvouchersettings where type='salesreturn' and " +
                    "billtypecode='"+ billtypecode +"' and vancode='"+ vancode+"' and companycode=a.companycode " +
                    "and financialyearcode='"+ financialyearcode +"') as suffix,datetime('"+ billdate +"')," +
                    " '"+ customercode +"','"+ billtypecode +"','"+ gstin +"','"+ schedulecode +"',round(sum(amount),0)," +
                    "coalesce((select sum(amount) from tbltempsalesitemdetails where refno='"+ getrefno +"' " +
                    "and companycode=a.companycode " +
                    " and freeitemstatus<>''),0)," +
                    "round(round(sum(amount),0)-coalesce((select sum(amount) from tbltempsalesitemdetails where refno='"+ getrefno +"' " +
                    "and companycode=a.companycode and freeitemstatus<>''),0),0),'1','1',datetime('now', 'localtime')," +
                    "'"+ financialyearcode +"','"+ remarks +"',0,'"+getcurtime+"',sum(amount)-coalesce((select sum(amount) from tbltempsalesitemdetails where refno='"+ getrefno +"'" +
                    " and companycode=a.companycode and freeitemstatus<>''),0) "  +
                    " from tbltempsalesitemdetails as  a where refno='"+ getrefno +"' group by companycode";
            mDb.execSQL(sql);

            String sqlitem="INSERT INTO tblsalesreturnitemdetails(autonum,transactionno,bookingno,financialyearcode,companycode,itemcode,qty,price,discount,amount,cgst,sgst,igst," +
                    "cgstamt,sgstamt,igstamt,freeitemstatus,makerid,createddate,vancode,flag,weight)" +
                    "SELECT (select coalesce(max(autonum),0)+1 from tblsalesreturnitemdetails)+autonum,'"+ Transactionno +"','"+ bookingno +"','"+ financialyearcode +"',companycode,itemcode,qty,price,discount,amount,cgst,sgst,igst,cgstamt,sgstamt,igstamt," +
                    "freeitemstatus,'1',datetime('now', 'localtime'),'"+vancode+"',1,weight from tbltempsalesitemdetails as  a where refno='"+ getrefno +"'";
            mDb.execSQL(sqlitem);

            String sqlstock="INSERT INTO tblstocktransaction(transactionno,transactiondate,vancode,itemcode,inward,outward,type,refno,createddate,flag,op,companycode,financialyearcode,autonum)" +
                    "SELECT (select coalesce(max(transactionno),0)+1 from tblstocktransaction),datetime('"+ Gencode +"'),'"+ vancode +"',itemcode,qty,0," +
                    "'salesreturn','"+ Transactionno +"',datetime('now', 'localtime'),1,0,companycode,'"+financialyearcode+"',(select coalesce(max(autonum),0)+1 from tblstocktransaction)+autonum from tbltempsalesitemdetails as  a where refno='"+ getrefno +"'";
            mDb.execSQL(sqlstock);

            String sqlstock1="INSERT INTO tblappstocktransactiondetails(transactionno,transactiondate,vancode,itemcode,inward,outward,type,refno,createddate,flag,op,companycode,financialyearcode,syncstatus)" +
                    "SELECT (select coalesce(max(transactionno),0)+1 from tblappstocktransactiondetails),datetime('"+ Gencode +"'),'"+ vancode +"',itemcode,qty,0," +
                    "'salesreturn','"+ Transactionno +"',datetime('now', 'localtime'),1,0,companycode,'"+financialyearcode+"',0 from tbltempsalesitemdetails as  a where refno='"+ getrefno +"'";

            mDb.execSQL(sqlstock1);

            String sqldeletesales="delete from tbltempsalesitemdetails where refno='"+ getrefno +"'";
            mDb.execSQL(sqldeletesales);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }


        return Transactionno;
    }

    //Get Sales Return Datas
    public Cursor GetSalesReturnDatasDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select * from tblsalesreturn where (flag=1 or syncstatus=0)";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Sales Return Item Datas
    public Cursor GetSalesReturnItemDatasDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select * from tblsalesreturnitemdetails where flag in (1,3) ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    //Get Sales Receipt
    public Cursor GetSalesReceiptDB(String getsalestransactionno,String getfinacialyear, String companycode)
    {
        Cursor mCur =null;
        try{
            String sql ="select billcopystatus,cashpaidstatus from " +
                    " tblsales where transactionno='"+getsalestransactionno+"' and " +
                    " financialyearcode='"+getfinacialyear+"' and companycode='" + companycode + "' ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    public Cursor GetSalesReceiptCompanyDB(String getsalestransactionno,String getfinacialyear,String getcompanycode)
    {
        Cursor mCur =null;
        try{
            String sql ="select billcopystatus,cashpaidstatus from " +
                    " tblsales where transactionno='"+getsalestransactionno+"' and " +
                    " financialyearcode='"+getfinacialyear+"' AND companycode = '"+getcompanycode+"'";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Update Sales Return Flag
    public void UpdateSalesReturnFlag(String gettransactiono)
    {
        try{
            String sql = "update tblsalesreturn set flag=2,syncstatus=1 where transactionno='"+gettransactiono+"'  and flag=1 ";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

    }
    //Update Sales Item Flag
    public void UpdateSalesReturnItemFlag(String gettransactiono)
    {
        try{
            String sql = "update tblsalesreturnitemdetails set flag=2 where transactionno='"+gettransactiono+"' ";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

    }



    /*************************************START INSERT UPDATE FUNCTIONALITY******************************/

    public String insertSchedule(String getscheduledate,String getroutecode,String getvehiclecode,String getemployeecode,
                                 String getdrivercode,String gethelpername,String gettripadvance,String getstartingkm,String getewayurl)
    {
        String generateschedulecode="";
        try{
            String Gencode= GenCreatedDate();
            mDb = mDbHelper.getReadableDatabase();

            /* String dqlsql = "delete from tblsalesschedule where schedulecode='V340010'";
            mDb.execSQL(dqlsql);*/

            String sqlc = "select  count(*) from tblsalesschedule " ;
            Cursor mCurc = mDb.rawQuery(sqlc, null);
            mCurc.moveToFirst();
            String getcount= (mCurc.moveToFirst())?mCurc.getString(0):"0";
            String getmaxschedulecode= "1";

            if(getcount.equals("0")){
                String sql1 = "select coalesce(refno,0) from tblmaxcode where code=1";
                Cursor mCur = mDb.rawQuery(sql1, null);
                mCur.moveToFirst();
                getmaxschedulecode = (mCur.moveToFirst()) ? mCur.getString(0) : "0";
                if(getmaxschedulecode.equals("0")){
                    getmaxschedulecode ="1";
                }
            }else {
                String sql1 = "select coalesce(max(refno),0)+1 from tblsalesschedule  where refno != 'null' ";
                Cursor mCur = mDb.rawQuery(sql1, null);
                mCur.moveToFirst();
                getmaxschedulecode = (mCur.moveToFirst()) ? mCur.getString(0) : "0";
            }
            String sql2 = "select 'V' || substr('00'||'"+preferenceMangr.pref_getString("getvancode")+"', -2, 2) || substr('0000'||'"+getmaxschedulecode+"', -4, 4) ";
            Cursor mCur1 = mDb.rawQuery(sql2, null);
            mCur1.moveToFirst();
            generateschedulecode= (mCur1.moveToFirst())?mCur1.getString(0):"0";
            if(gettripadvance.equals("")){
                gettripadvance="0";
            }

            String chlschedule = "select count(*) as schedulecount from tblsalesschedule where scheduledate=datetime('"+Gencode+"') ";
            Cursor mschedule = mDb.rawQuery(chlschedule, null);
            mschedule.moveToFirst();
            String getschedulecount = (mschedule.moveToFirst()) ? mschedule.getString(0) : "0";

            if(getschedulecount.equals("0")){
                String sql = "INSERT INTO  'tblsalesschedule' (autonum, refno,schedulecode, scheduledate,vancode,routecode,vehiclecode,employeecode, drivercode,helpername," +
                        "tripadvance,startingkm,endingkm,createddate, updateddate,makerid,flag,lunch_start_time,lunch_end_time) VALUES " +
                        "(0,'"+getmaxschedulecode+"','"+generateschedulecode+"',datetime('"+Gencode+"')," +
                        " '"+preferenceMangr.pref_getString("getvancode")+"','"+ getroutecode +"','"+ getvehiclecode +"','"+ getemployeecode +"'," +
                        " '"+getdrivercode+"','"+gethelpername+"','"+gettripadvance+"','"+getstartingkm+"',0," +
                        " datetime('now', 'localtime'),datetime('now', 'localtime'),0,0,'','')";
                mDb.execSQL(sql);
                String ewaysql = "INSERT INTO 'tblscheduleeway' (schedulecode,scheduledate,ewayurl,createddate) Values " +
                        "('"+generateschedulecode+"',datetime('"+Gencode+"'),'"+getewayurl+"', datetime('now', 'localtime'))";
                mDb.execSQL(ewaysql);

            }


        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }


        return generateschedulecode;

    }
    //insert schedule eway
    public String insertScheduleeway(String getscheduledate,
                                     String getschedulecode,String getewayurl)
    {

        try{
            String Gencode= GenCreatedDate();
            mDb = mDbHelper.getReadableDatabase();

            /* String dqlsql = "delete from tblsalesschedule where schedulecode='V340010'";
            mDb.execSQL(dqlsql);*/

//        String sqlc = "select  count(*) from tblsalesschedule " ;
//        Cursor mCurc = mDb.rawQuery(sqlc, null);
//        mCurc.moveToFirst();
//        String getcount= (mCurc.moveToFirst())?mCurc.getString(0):"0";
//        String getmaxschedulecode= "1";




            String chlschedule = "select count(*) as schedulecount from tblscheduleeway where schedulecode='" +getschedulecode+ "'";
            Cursor mschedule = mDb.rawQuery(chlschedule, null);
//        mschedule.moveToFirst();
            String getschedulecount = (mschedule.moveToFirst()) ? mschedule.getString(0) : "0";

            if(!getschedulecount.equals("0")){

                String detelescheduleeway="DELETE FROM 'tblscheduleeway' where schedulecode='" +getschedulecode+ "'";
                mDb.execSQL(detelescheduleeway);
            }
            String ewaysql = "INSERT INTO 'tblscheduleeway' (schedulecode,scheduledate,ewayurl,createddate) Values " +
                    "('"+getschedulecode+"',datetime('"+getscheduledate+"'),'"+getewayurl+"', datetime('now', 'localtime'))";
            mDb.execSQL(ewaysql);

        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }


        return getschedulecode;

    }
    //Insert Stock Conversion
    public String insertStockConversion(String getparentitemcode,String getinward,String getoutward,
                                        String getchilditemcode,String getschedulecode, String vanCode)
    {
        try{
            String GenDate= GenCreatedDate();
            mDb = mDbHelper.getReadableDatabase();
       /* String sql1 = "select coalesce(max(transactionno),0)+1 from tblstocktransaction";
        Cursor mCur = mDb.rawQuery(sql1, null);
        mCur.moveToFirst();
        String getmaxtransactionno = (mCur.moveToFirst()) ? mCur.getString(0) : "0";*/

            String sqlconvert = "select coalesce(max(transactionno),0)+1 from tblstockconversion";
            Cursor mCurconvert = mDb.rawQuery(sqlconvert, null);
            mCurconvert.moveToFirst();
            String getmaxstocktransactionno = (mCurconvert.moveToFirst()) ? mCurconvert.getString(0) : "0";

       /* String sqlstockoutward = "INSERT INTO  'tblstocktransaction' VALUES " +
                "('"+getmaxtransactionno+"',datetime('"+GenDate+"')," +
                " '"+LoginActivity.getvancode+"','"+ getparentitemcode +"',0,'"+ getoutward +"'," +
                " 'stockconversion','"+getmaxstocktransactionno+"',datetime('"+GenDate+"'),1,0,0)";
        mDb.execSQL(sqlstockoutward);

        String sqlstockinward = "INSERT INTO  'tblstocktransaction' VALUES " +
                "('"+getmaxtransactionno+"',datetime('"+GenDate+"')," +
                " '"+LoginActivity.getvancode+"','"+ getchilditemcode +"','"+ getinward +"',0," +
                " 'stockconversion','"+getmaxstocktransactionno+"',datetime('"+GenDate+"'),1,0,0)";
        mDb.execSQL(sqlstockinward);*/

            /*String sqlconvertinward = "INSERT INTO  'tblstockconversion' VALUES " +
                    "('"+getmaxstocktransactionno+"',datetime('"+GenDate+"')," +
                    " '" + vanCode + "','"+ getparentitemcode +"',0,'"+ getoutward +"'," +
                    " 'stockconversion','"+getschedulecode+"',datetime('now', 'localtime'))";
            mDb.execSQL(sqlconvertinward);*/

            ContentValues contentValues = new ContentValues();
            contentValues.put("transactionno", getmaxstocktransactionno);
            contentValues.put("transactiondate", GenDate + " 00:00:00");
            contentValues.put("vancode",vanCode);
            contentValues.put("itemcode",getparentitemcode);
            contentValues.put("inward",0);
            contentValues.put("outward",getoutward);
            contentValues.put("type","stockconversion");
            contentValues.put("schedulecode",getschedulecode);
            contentValues.put("createddate",GenCreatedDateTime());
            long outwardEntryValues = mDb.insertOrThrow("tblstockconversion", null, contentValues);


//            datetime('"+GenDate+"'))

            /*String sqlconvertoutward = "INSERT INTO  'tblstockconversion' VALUES " +
                    "('"+getmaxstocktransactionno+"',datetime('"+GenDate+"')," +
                    " '" + vanCode + "','"+ getchilditemcode +"','"+ getinward +"',0," +
                    " 'stockconversion','"+getschedulecode+"',datetime('now', 'localtime'))";
            mDb.execSQL(sqlconvertoutward);*/

            ContentValues contentValues1 = new ContentValues();
            contentValues1.put("transactionno", getmaxstocktransactionno);
            contentValues1.put("transactiondate", GenDate + " 00:00:00");
            contentValues1.put("vancode",vanCode);
            contentValues1.put("itemcode",getchilditemcode);
            contentValues1.put("inward",getinward);
            contentValues1.put("outward",0);
            contentValues1.put("type","stockconversion");
            contentValues1.put("schedulecode",getschedulecode);
            contentValues1.put("createddate",GenCreatedDateTime());
            long inwardEntryValues = mDb.insertOrThrow("tblstockconversion", null, contentValues1);


        }catch (SQLException ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - insertStockConversion", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            return "failure";
        }


        return "success";

    }


    //Insert Sales item in cart table
    public String insertSalesCart(String itemcode, String companycode,String brandcode, String manualitemcode,
                                  String itemname, String itemnametamil,
                                  String unitcode, String unitweightunitcode, String unitweight,
                                  String uppunitcode, String uppweight, String itemcategory, String parentitemcode,
                                  String allowpriceedit,
                                  String allownegativestock, String allowdiscount, String stockqty, String unitname, String noofdecimals,
                                  String oldprice, String newprice,
                                  String colourcode, String hsn, String tax,
                                  String itemqty, String subtotal,
                                  String routeallowpricedit, String discount, String freeflag,
                                  String purchaseitemcode, String freeitemcode,String minstockqty,
                                  String actualprice,String ratediscount,String itemschemeapplicable,String orgprice)
    {
        try{
            String GenDate= GenCreatedDate();
            mDb = mDbHelper.getReadableDatabase();
            String sql1 = "select coalesce(max(cartcode),0)+1 from tblsalescartdatas";
            Cursor mCur = mDb.rawQuery(sql1, null);
            mCur.moveToFirst();
            String getmaxcartcode = (mCur.moveToFirst()) ? mCur.getString(0) : "0";


            String sql2 = "select coalesce(count(*),0) as count,coalesce(cartcode,0) as cartcode,coalesce(freeflag,'') as freeflag from tblsalescartdatas " +
                    " where itemcode = '"+itemcode+"' and (ifnull(freeflag,'')='' or (freeflag='freerate')) ";
            Cursor mCur1 = mDb.rawQuery(sql2, null);
            mCur1.moveToFirst();
            String getcount = (mCur1.moveToFirst()) ? mCur1.getString(0) : "0";
            String getcartcode = (mCur1.moveToFirst()) ? mCur1.getString(1) : "0";
            String getfreeflag =  String.valueOf(mCur1.getString(2));
            if(getcount.equals("0") ) {
                String sqlcart = "INSERT INTO  'tblsalescartdatas' VALUES " +
                        "('" + getmaxcartcode + "','" + itemcode + "'," +
                        " '" + companycode + "','" + brandcode + "','" + manualitemcode + "','" + itemname + "','" + itemnametamil + "'," +
                        " '" + unitcode + "','" + unitweightunitcode + "','" + unitweight + "'," +
                        " '" + uppunitcode + "','" + uppweight + "','" + itemcategory + "'," +
                        " '" + parentitemcode + "','" + allowpriceedit + "','" + allownegativestock + "' ," +
                        " '" + allowdiscount + "','" + stockqty + "','" + unitname + "'," +
                        " '" + noofdecimals + "','" + oldprice + "','" + newprice + "'," +
                        " '" + colourcode + "','" + hsn + "','" + tax + "'," +
                        " '" + itemqty + "','" + subtotal + "','" + routeallowpricedit + "'," +
                        " '" + discount + "','" + freeflag + "','" + purchaseitemcode + "'," +
                        " '" + freeitemcode + "', '" + minstockqty + "','"+ actualprice +"','"+ratediscount+"'," +
                        " '" + itemschemeapplicable + "','" + orgprice + "'  )";
                mDb.execSQL(sqlcart);
            }else if(Integer.parseInt(getcount) > 0){
                if(getfreeflag.equals("")){
                    String sqlcart = "UPDATE   'tblsalescartdatas' SET " +
                            " companycode= '" + companycode + "',brandcode='" + brandcode + "'," +
                            " manualitemcode='" + manualitemcode + "',itemname='" + itemname + "',itemnametamil='" + itemnametamil + "'," +
                            " unitcode='" + unitcode + "',unitweightunitcode='" + unitweightunitcode + "',unitweight='" + unitweight + "'," +
                            " uppunitcode='" + uppunitcode + "',uppweight='" + uppweight + "',itemcategory='" + itemcategory + "'," +
                            " parentitemcode='" + parentitemcode + "',allowpriceedit='" + allowpriceedit + "',allownegativestock='" + allownegativestock + "' ," +
                            " allowdiscount='" + allowdiscount + "',stockqty='" + stockqty + "',unitname='" + unitname + "'," +
                            " noofdecimals='" + noofdecimals + "',oldprice='" + oldprice + "',newprice='" + newprice + "'," +
                            " colourcode='" + colourcode + "',hsn='" + hsn + "',tax='" + tax + "'," +
                            " itemqty='" + itemqty + "',subtotal='" + subtotal + "',routeallowpricedit='" + routeallowpricedit + "'," +
                            " discount='" + discount + "',freeflag='" + freeflag + "',purchaseitemcode='" + purchaseitemcode + "'," +
                            " freeitemcode='" + freeitemcode + "',minimumsalesqty='" + minstockqty + "'" +
                            ",actualamount='"+actualprice+"',ratediscount='"+ ratediscount +"'  " +
                            " , schemeapplicable =  '" + itemschemeapplicable + "',orgprice='" + orgprice + "' " +
                            " where  cartcode='"+getcartcode+"'" +
                            " and itemcode='"+itemcode+"' and  freeflag='' ";
                    mDb.execSQL(sqlcart);
                }else  if(getfreeflag.equals("freerate")){
                    String sqlcart = "UPDATE   'tblsalescartdatas' SET " +
                            " companycode= '" + companycode + "',brandcode='" + brandcode + "'," +
                            " manualitemcode='" + manualitemcode + "',itemname='" + itemname + "',itemnametamil='" + itemnametamil + "'," +
                            " unitcode='" + unitcode + "',unitweightunitcode='" + unitweightunitcode + "',unitweight='" + unitweight + "'," +
                            " uppunitcode='" + uppunitcode + "',uppweight='" + uppweight + "',itemcategory='" + itemcategory + "'," +
                            " parentitemcode='" + parentitemcode + "',allowpriceedit='" + allowpriceedit + "',allownegativestock='" + allownegativestock + "' ," +
                            " allowdiscount='" + allowdiscount + "',stockqty='" + stockqty + "',unitname='" + unitname + "'," +
                            " noofdecimals='" + noofdecimals + "',oldprice='" + oldprice + "',newprice='" + newprice + "'," +
                            " colourcode='" + colourcode + "',hsn='" + hsn + "',tax='" + tax + "'," +
                            " itemqty='" + itemqty + "',subtotal='" + subtotal + "',routeallowpricedit='" + routeallowpricedit + "'," +
                            " discount='" + discount + "',freeflag='" + freeflag + "',purchaseitemcode='" + purchaseitemcode + "'," +
                            " freeitemcode='" + freeitemcode + "',minimumsalesqty='" + minstockqty + "'," +
                            "actualamount='"+ actualprice +"',ratediscount='"+ ratediscount +"'," +
                            " schemeapplicable =  '" + itemschemeapplicable + "' ,orgprice='" + orgprice + "'" +
                            "  where  cartcode='"+getcartcode+"'" +
                            " and itemcode='"+itemcode+"' and  freeflag='freerate' ";
                    mDb.execSQL(sqlcart);
                }
                /*String sqlcart = "UPDATE   'tblsalescartdatas' SET " +
                        " companycode= '" + companycode + "',brandcode='" + brandcode + "'," +
                        " manualitemcode='" + manualitemcode + "',itemname='" + itemname + "',itemnametamil='" + itemnametamil + "'," +
                        " unitcode='" + unitcode + "',unitweightunitcode='" + unitweightunitcode + "',unitweight='" + unitweight + "'," +
                        " uppunitcode='" + uppunitcode + "',uppweight='" + uppweight + "',itemcategory='" + itemcategory + "'," +
                        " parentitemcode='" + parentitemcode + "',allowpriceedit='" + allowpriceedit + "',allownegativestock='" + allownegativestock + "' ," +
                        " allowdiscount='" + allowdiscount + "',stockqty='" + stockqty + "',unitname='" + unitname + "'," +
                        " noofdecimals='" + noofdecimals + "',oldprice='" + oldprice + "',newprice='" + newprice + "'," +
                        " colourcode='" + colourcode + "',hsn='" + hsn + "',tax='" + tax + "'," +
                        " itemqty='" + itemqty + "',subtotal='" + subtotal + "',routeallowpricedit='" + routeallowpricedit + "'," +
                        " discount='" + discount + "',freeflag='" + freeflag + "',purchaseitemcode='" + purchaseitemcode + "'," +
                        " freeitemcode='" + freeitemcode + "'   where  cartcode='"+getcartcode+"'" +
                        " and itemcode='"+itemcode+"' and  freeflag='' ";
                mDb.execSQL(sqlcart);*/

            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }




        return "success";

    }



    //Insert Sales item in cart table
    public String insertSalesOrderCart(String itemcode, String companycode,String brandcode, String manualitemcode,
                                       String itemname, String itemnametamil,
                                       String unitcode, String unitweightunitcode, String unitweight,
                                       String uppunitcode, String uppweight, String itemcategory, String parentitemcode,
                                       String allowpriceedit,
                                       String allownegativestock, String allowdiscount, String stockqty, String unitname, String noofdecimals,
                                       String oldprice, String newprice,
                                       String colourcode, String hsn, String tax,
                                       String itemqty, String subtotal,
                                       String routeallowpricedit, String discount, String freeflag,
                                       String purchaseitemcode, String freeitemcode)
    {
        try{
            String GenDate= GenCreatedDate();
            mDb = mDbHelper.getReadableDatabase();
            String sql1 = "select coalesce(max(cartcode),0)+1 from tblsalesordercartdatas";
            Cursor mCur = mDb.rawQuery(sql1, null);
            mCur.moveToFirst();
            String getmaxcartcode = (mCur.moveToFirst()) ? mCur.getString(0) : "0";


            String sql2 = "select coalesce(count(*),0) as count,coalesce(cartcode,0) as cartcode,coalesce(freeflag,'') as freeflag from tblsalesordercartdatas " +
                    " where itemcode = '"+itemcode+"' and (ifnull(freeflag,'')='' or (freeflag='freerate')) ";
            Cursor mCur1 = mDb.rawQuery(sql2, null);
            mCur1.moveToFirst();
            String getcount = (mCur1.moveToFirst()) ? mCur1.getString(0) : "0";
            String getcartcode = (mCur1.moveToFirst()) ? mCur1.getString(1) : "0";
            String getfreeflag =  String.valueOf(mCur1.getString(2));
            String itemfreeflag="";
            /*if(getfreeflag.equals("")){
                itemfreeflag = "freeflag='' ";
            }else if(getfreeflag.equals("freerate")){
                itemfreeflag = "freeflag='freerate' ";
            }*/

            if(getcount.equals("0") ) {
                String sqlcart = "INSERT INTO  'tblsalesordercartdatas' VALUES " +
                        "('" + getmaxcartcode + "','" + itemcode + "'," +
                        " '" + companycode + "','" + brandcode + "','" + manualitemcode + "','" + itemname + "','" + itemnametamil + "'," +
                        " '" + unitcode + "','" + unitweightunitcode + "','" + unitweight + "'," +
                        " '" + uppunitcode + "','" + uppweight + "','" + itemcategory + "'," +
                        " '" + parentitemcode + "','" + allowpriceedit + "','" + allownegativestock + "' ," +
                        " '" + allowdiscount + "','" + stockqty + "','" + unitname + "'," +
                        " '" + noofdecimals + "','" + oldprice + "','" + newprice + "'," +
                        " '" + colourcode + "','" + hsn + "','" + tax + "'," +
                        " '" + itemqty + "','" + subtotal + "','" + routeallowpricedit + "'," +
                        " '" + discount + "','" + freeflag + "','" + purchaseitemcode + "'," +
                        " '" + freeitemcode + "'  )";
                mDb.execSQL(sqlcart);
            }else if(Integer.parseInt(getcount) > 0){
                if(getfreeflag.equals("")){
                    String sqlcart = "UPDATE   'tblsalesordercartdatas' SET " +
                            " companycode= '" + companycode + "',brandcode='" + brandcode + "'," +
                            " manualitemcode='" + manualitemcode + "',itemname='" + itemname + "',itemnametamil='" + itemnametamil + "'," +
                            " unitcode='" + unitcode + "',unitweightunitcode='" + unitweightunitcode + "',unitweight='" + unitweight + "'," +
                            " uppunitcode='" + uppunitcode + "',uppweight='" + uppweight + "',itemcategory='" + itemcategory + "'," +
                            " parentitemcode='" + parentitemcode + "',allowpriceedit='" + allowpriceedit + "',allownegativestock='" + allownegativestock + "' ," +
                            " allowdiscount='" + allowdiscount + "',stockqty='" + stockqty + "',unitname='" + unitname + "'," +
                            " noofdecimals='" + noofdecimals + "',oldprice='" + oldprice + "',newprice='" + newprice + "'," +
                            " colourcode='" + colourcode + "',hsn='" + hsn + "',tax='" + tax + "'," +
                            " itemqty='" + itemqty + "',subtotal='" + subtotal + "',routeallowpricedit='" + routeallowpricedit + "'," +
                            " discount='" + discount + "',freeflag='" + freeflag + "',purchaseitemcode='" + purchaseitemcode + "'," +
                            " freeitemcode='" + freeitemcode + "'  where  cartcode='"+getcartcode+"'" +
                            " and itemcode='"+itemcode+"' and  freeflag='' ";
                    mDb.execSQL(sqlcart);
                }else if(getfreeflag.equals("freerate")){
                    String sqlcart = "UPDATE   'tblsalesordercartdatas' SET " +
                            " companycode= '" + companycode + "',brandcode='" + brandcode + "'," +
                            " manualitemcode='" + manualitemcode + "',itemname='" + itemname + "',itemnametamil='" + itemnametamil + "'," +
                            " unitcode='" + unitcode + "',unitweightunitcode='" + unitweightunitcode + "',unitweight='" + unitweight + "'," +
                            " uppunitcode='" + uppunitcode + "',uppweight='" + uppweight + "',itemcategory='" + itemcategory + "'," +
                            " parentitemcode='" + parentitemcode + "',allowpriceedit='" + allowpriceedit + "',allownegativestock='" + allownegativestock + "' ," +
                            " allowdiscount='" + allowdiscount + "',stockqty='" + stockqty + "',unitname='" + unitname + "'," +
                            " noofdecimals='" + noofdecimals + "',oldprice='" + oldprice + "',newprice='" + newprice + "'," +
                            " colourcode='" + colourcode + "',hsn='" + hsn + "',tax='" + tax + "'," +
                            " itemqty='" + itemqty + "',subtotal='" + subtotal + "',routeallowpricedit='" + routeallowpricedit + "'," +
                            " discount='" + discount + "',freeflag='" + freeflag + "',purchaseitemcode='" + purchaseitemcode + "'," +
                            " freeitemcode='" + freeitemcode + "'  where  cartcode='"+getcartcode+"'" +
                            " and itemcode='"+itemcode+"' and  freeflag='freerate' ";
                    mDb.execSQL(sqlcart);
                }
                /*String sqlcart = "UPDATE   'tblsalesordercartdatas' SET " +
                        " companycode= '" + companycode + "',brandcode='" + brandcode + "'," +
                        " manualitemcode='" + manualitemcode + "',itemname='" + itemname + "',itemnametamil='" + itemnametamil + "'," +
                        " unitcode='" + unitcode + "',unitweightunitcode='" + unitweightunitcode + "',unitweight='" + unitweight + "'," +
                        " uppunitcode='" + uppunitcode + "',uppweight='" + uppweight + "',itemcategory='" + itemcategory + "'," +
                        " parentitemcode='" + parentitemcode + "',allowpriceedit='" + allowpriceedit + "',allownegativestock='" + allownegativestock + "' ," +
                        " allowdiscount='" + allowdiscount + "',stockqty='" + stockqty + "',unitname='" + unitname + "'," +
                        " noofdecimals='" + noofdecimals + "',oldprice='" + oldprice + "',newprice='" + newprice + "'," +
                        " colourcode='" + colourcode + "',hsn='" + hsn + "',tax='" + tax + "'," +
                        " itemqty='" + itemqty + "',subtotal='" + subtotal + "',routeallowpricedit='" + routeallowpricedit + "'," +
                        " discount='" + discount + "',freeflag='" + freeflag + "',purchaseitemcode='" + purchaseitemcode + "'," +
                        " freeitemcode='" + freeitemcode + "'  where  cartcode='"+getcartcode+"'" +
                        " and itemcode='"+itemcode+"' and  freerate='' ";
                mDb.execSQL(sqlcart);*/

            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }




        return "success";

    }



    //Insert Sales item in cart table
    public String insertFreeSalesCart(String itemcode, String companycode,String brandcode, String manualitemcode,
                                      String itemname, String itemnametamil,
                                      String unitcode, String unitweightunitcode, String unitweight,
                                      String uppunitcode, String uppweight, String itemcategory, String parentitemcode,
                                      String allowpriceedit,
                                      String allownegativestock, String allowdiscount, String stockqty, String unitname, String noofdecimals,
                                      String oldprice, String newprice,
                                      String colourcode, String hsn, String tax,
                                      String itemqty, String subtotal,
                                      String routeallowpricedit, String discount, String freeflag,
                                      String purchaseitemcode, String freeitemcode,String minimumsalesqty
            ,String getroutecode,String curitemcode,String ratediscount,String getschemeapplicable,String orgprice)
    {
        Cursor mCur = null;
        Cursor mCur1 = null;
        Cursor mCur3 = null;
        Cursor mCur2 = null;
        Cursor mCurItemDS = null;
        try{
            double vartaxamount=((Double.parseDouble(newprice)/(1+Double.parseDouble(tax)/100))*(Double.parseDouble(tax)/100))*Double.parseDouble(itemqty);
            double vardiscount=(Double.parseDouble(newprice)*Double.parseDouble(itemqty))-vartaxamount;
//            double roundoffs = dft.format(vardiscount) - Math.floor(dft.format(vardiscount));
//            if (roundoffs >= 0.5) {
//                var dataround = 1 - parseFloat(roundoffs).toFixed(2);
//                signwithroundoff = parseFloat(dataround).toFixed(2);
//            } else if (roundoffs != 0) {
//                signwithroundoff = '- ' + parseFloat(roundoffs).toFixed(2);
//            } else {
//                signwithroundoff = parseFloat(roundoffs).toFixed(2);
//            }
            String getbusinesstype = "";
            String arr[]=preferenceMangr.pref_getString("getbusiness_type").split(",");
            if(arr.length>0) {
                for (int i = 0; i < arr.length; i++) {
                    if(arr[i].equals("2")){
                        getbusinesstype=" ((','||a.business_type||',') LIKE '%,2,%' or (','||a.business_type||',') LIKE '%,3,%') ";
                    }else if(arr[i].equals("1")){
                        getbusinesstype=" ((','||a.business_type||',') LIKE '%,1,%' or (','||a.business_type||',') LIKE '%,3,%') ";
                    }else{
                        getbusinesstype=" ((','||a.business_type||',') LIKE '%,1,%' or (','||a.business_type||',') LIKE '%,2,%' or (','||a.business_type||',') LIKE '%,3,%') ";
                    }
                }
            }
            String GenDate= GenCreatedDate();
            mDb = mDbHelper.getReadableDatabase();
            String sql1 = "select coalesce(max(cartcode),0)+1 from tblsalescartdatas";
            mCur = mDb.rawQuery(sql1, null);
            mCur.moveToFirst();
            String getmaxcartcode = (mCur.moveToFirst()) ? mCur.getString(0) : "0";
            //get free item discount from general settings
            String itemDiscount = "SELECT zro_price_item_disc,cash_item_disc,item_disc_efrom FROM tblgeneralsettings " +
                    "WHERE datetime(item_disc_efrom)<=datetime('"+GenDate+"')";
            mCurItemDS = mDb.rawQuery(itemDiscount, null);
            String itemdiscountAmount="";
            String itemsubtotal="";
            String itemprice="";
            if(mCurItemDS!=null) {
                mCurItemDS.moveToFirst();
                itemdiscountAmount = (mCurItemDS.moveToFirst()) ?((mCurItemDS.getString(0).equals("yes"))?"0": dft.format(vardiscount)): "0";
                itemsubtotal= (mCurItemDS.moveToFirst()) ?((mCurItemDS.getString(0).equals("yes"))?"0": subtotal): "0";
                itemprice= (mCurItemDS.moveToFirst()) ?((mCurItemDS.getString(0).equals("yes"))?"0":newprice): "0";
            }else{
                itemdiscountAmount=dft.format(vardiscount);
                itemsubtotal= subtotal;
                itemprice=newprice;
            }
            // end
            String sqlpurchaseitemcode="SELECT  group_concat(purchaseitemcode,',') as purchaseitemcode " +
                    "from (select purchaseitemcode  from tblsalescartdatas  where " +
                    "freeitemcode = '" + freeitemcode + "' and freeflag='freeitem'" +
                    " union all select '"+curitemcode+"' as purchaseitemcode) as dev ";
            mCur3 = mDb.rawQuery(sqlpurchaseitemcode, null);
            String purchasearr=null;
            if(mCur3!=null){
                mCur.moveToFirst();
                String purchaseitemvalue=(mCur3.moveToFirst()) ? mCur3.getString(0) : curitemcode;
                purchasearr= purchaseitemvalue.replace(",", "', '") ;
            }
            else{
                purchasearr = curitemcode;
            }
            String strpurchasecode= purchaseitemcode.replace(",", "', '") ;
//            String sql3 = "select coalesce(count(*),0) as count,coalesce(cartcode,0) as cartcode from tblsalescartdatas " +
//                    " where freeitemcode = '"+freeitemcode+"' and purchaseitemcode='"+purchaseitemcode+"' " +
//                    " and freeflag='freeitem' ";
//                        String sql3 = "select coalesce(count(*),0) as count,coalesce(cartcode,0) as cartcode from tblsalescartdatas " +
//                    " where freeitemcode = '"+freeitemcode+"' and purchaseitemcode in " +
//                    " (SELECT purchaseitemcode from tblschemeitemdetails where schemecode=(select a.schemecode from tblscheme as a " +
//                    " inner join tblschemeitemdetails as b on a.schemecode=b.schemecode where purchaseitemcode='"+purchaseitemcode+"' " +
//                    " and a.status='"+statusvar+"'  and "+getbusinesstype+" and (','||multipleroutecode||',') LIKE '%,"+ getroutecode +",%' and" +
//                    " (validityfrom<=datetime('"+GenDate+"')) and (ifnull(validityto,'')=''  or (validityfrom<=datetime('"+GenDate+"') and " +
//                    " validityto>=datetime('"+GenDate+"'))) )) and freeflag='freeitem' ";
            String sql3 = "select coalesce(count(*),0) as count,coalesce(cartcode,0) as cartcode from tblsalescartdatas " +
                    " where freeitemcode = '"+freeitemcode+"' and  '"+curitemcode+"' in ('"+purchasearr+"') and '"+curitemcode+"' in " +
                    " (SELECT purchaseitemcode from tblschemeitemdetails where schemecode=(select DISTINCT a.schemecode from tblscheme as a " +
                    " inner join tblschemeitemdetails as b on a.schemecode=b.schemecode where purchaseitemcode in ('"+strpurchasecode+"') " +
                    " and a.status='"+statusvar+"' and schemetype='item'  and "+getbusinesstype+" and (','||multipleroutecode||',') LIKE '%,"+ getroutecode +",%' and" +
                    " (validityfrom<=datetime('"+GenDate+"')) and (ifnull(validityto,'')=''  or (validityfrom<=datetime('"+GenDate+"') and " +
                    " validityto>=datetime('"+GenDate+"'))) )) and freeflag='freeitem' ";
            mCur2 = mDb.rawQuery(sql3, null);
            mCur2.moveToFirst();
            String getfreecount = (mCur2.moveToFirst()) ? mCur2.getString(0) : "0";
            String getfreecartcode = (mCur2.moveToFirst()) ? mCur2.getString(1) : "0";
            if(getfreecount.equals("0")) {
                String sqlcart = "INSERT INTO  'tblsalescartdatas' VALUES " +
                        "('" + getmaxcartcode + "','" + itemcode + "'," +
                        " '" + companycode + "','" + brandcode + "','" + manualitemcode + "','" + itemname + "','" + itemnametamil + "'," +
                        " '" + unitcode + "','" + unitweightunitcode + "','" + unitweight + "'," +
                        " '" + uppunitcode + "','" + uppweight + "','" + itemcategory + "'," +
                        " '" + parentitemcode + "','" + allowpriceedit + "','" + allownegativestock + "' ," +
                        " '" + allowdiscount + "','" + stockqty + "','" + unitname + "'," +
                        " '" + noofdecimals + "','" + oldprice + "','" + itemprice + "'," +
                        " '" + colourcode + "','" + hsn + "','" + tax + "'," +
                        " '" + itemqty + "','" + itemsubtotal + "','" + routeallowpricedit + "'," +
                        " '" + itemdiscountAmount + "','" + freeflag + "','" + purchaseitemcode + "'," +
                        " '" + freeitemcode + "', '" + minimumsalesqty + "','" + newprice +"','" + ratediscount +"'" +
                        ",'" + getschemeapplicable + "','" + orgprice + "'  )";
                mDb.execSQL(sqlcart);
            }else if(Integer.parseInt(getfreecount) > 0){
//                String sqlcart = "UPDATE   'tblsalescartdatas' SET " +
//                        " companycode= '" + companycode + "',brandcode='" + brandcode + "'," +
//                        " manualitemcode='" + manualitemcode + "',itemname='" + itemname + "',itemnametamil='" + itemnametamil + "'," +
//                        " unitcode='" + unitcode + "',unitweightunitcode='" + unitweightunitcode + "',unitweight='" + unitweight + "'," +
//                        " uppunitcode='" + uppunitcode + "',uppweight='" + uppweight + "',itemcategory='" + itemcategory + "'," +
//                        " parentitemcode='" + parentitemcode + "',allowpriceedit='" + allowpriceedit + "',allownegativestock='" + allownegativestock + "' ," +
//                        " allowdiscount='" + allowdiscount + "',stockqty='" + stockqty + "',unitname='" + unitname + "'," +
//                        " noofdecimals='" + noofdecimals + "',oldprice='" + oldprice + "',newprice='" + newprice + "'," +
//                        " colourcode='" + colourcode + "',hsn='" + hsn + "',tax='" + tax + "'," +
//                        " itemqty='" + itemqty + "',subtotal='" + subtotal + "',routeallowpricedit='" + routeallowpricedit + "'," +
//                        " discount='" + discount + "',freeflag='" + freeflag + "',purchaseitemcode='" + purchaseitemcode + "'," +
//                        " freeitemcode='" + freeitemcode + "',minimumsalesqty='"+minimumsalesqty+"'   where  cartcode='"+getfreecartcode+"'" +
//                        " and freeitemcode='"+freeitemcode+"' and purchaseitemcode='"+purchaseitemcode+"'" +
//                        "  and  freeflag='freeitem' ";
                String sqlcart = "UPDATE   'tblsalescartdatas' SET " +
                        " companycode= '" + companycode + "',brandcode='" + brandcode + "'," +
                        " manualitemcode='" + manualitemcode + "',itemname='" + itemname + "',itemnametamil='" + itemnametamil + "'," +
                        " unitcode='" + unitcode + "',unitweightunitcode='" + unitweightunitcode + "',unitweight='" + unitweight + "'," +
                        " uppunitcode='" + uppunitcode + "',uppweight='" + uppweight + "',itemcategory='" + itemcategory + "'," +
                        " parentitemcode='" + parentitemcode + "',allowpriceedit='" + allowpriceedit + "',allownegativestock='" + allownegativestock + "' ," +
                        " allowdiscount='" + allowdiscount + "',stockqty='" + stockqty + "',unitname='" + unitname + "'," +
                        " noofdecimals='" + noofdecimals + "',oldprice='" + oldprice + "',newprice='" + itemprice + "'," +
                        " colourcode='" + colourcode + "',hsn='" + hsn + "',tax='" + tax + "'," +
                        " itemqty='" + itemqty + "',subtotal='" + itemsubtotal + "',routeallowpricedit='" + routeallowpricedit + "'," +
                        " discount='" + itemdiscountAmount + "',freeflag='" + freeflag + "',purchaseitemcode='" + purchaseitemcode + "'," +
                        " freeitemcode='" + freeitemcode + "',minimumsalesqty='"+minimumsalesqty+"'" +
                        ",actualamount='"+ newprice +"',ratediscount='"+ ratediscount +"' " +
                        ",schemeapplicable='" + getschemeapplicable + "',orgprice='" + orgprice + "'" +
                        " where  cartcode='"+getfreecartcode+"'" +
                        " and freeitemcode='"+freeitemcode+"' and '"+curitemcode+"' in" +
                        " (SELECT purchaseitemcode from tblschemeitemdetails where schemecode=(select DISTINCT a.schemecode from tblscheme as a " +
                        " inner join tblschemeitemdetails as b on a.schemecode=b.schemecode where purchaseitemcode in ('"+strpurchasecode+"') " +
                        " and a.status='"+statusvar+"' and schemetype='item' and "+getbusinesstype+" and (','||multipleroutecode||',') LIKE '%,"+ getroutecode +",%' and" +
                        " (validityfrom<=datetime('"+GenDate+"')) and (ifnull(validityto,'')=''  or (validityfrom<=datetime('"+GenDate+"') and " +
                        " validityto>=datetime('"+GenDate+"'))) ))  and  freeflag='freeitem' ";
                mDb.execSQL(sqlcart);
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }finally {
            if(mCur != null)
                mCur.close();
            if(mCur2 != null)
                mCur2.close();
            if(mCur3 != null)
                mCur3.close();
        }
        return "success";
    }

    //Insert Sales item in cart table
    public String insertFreeSalesOrderCart(String itemcode, String companycode,String brandcode, String manualitemcode,
                                           String itemname, String itemnametamil,
                                           String unitcode, String unitweightunitcode, String unitweight,
                                           String uppunitcode, String uppweight, String itemcategory, String parentitemcode,
                                           String allowpriceedit,
                                           String allownegativestock, String allowdiscount, String stockqty, String unitname, String noofdecimals,
                                           String oldprice, String newprice,
                                           String colourcode, String hsn, String tax,
                                           String itemqty, String subtotal,
                                           String routeallowpricedit, String discount, String freeflag,
                                           String purchaseitemcode, String freeitemcode,String rateitemschemediscount)
    {
        try{
            String GenDate= GenCreatedDate();
            mDb = mDbHelper.getReadableDatabase();
            String sql1 = "select coalesce(max(cartcode),0)+1 from tblsalesordercartdatas";
            Cursor mCur = mDb.rawQuery(sql1, null);
            mCur.moveToFirst();
            String getmaxcartcode = (mCur.moveToFirst()) ? mCur.getString(0) : "0";

            String sql3 = "select coalesce(count(*),0) as count,coalesce(cartcode,0) as cartcode from tblsalesordercartdatas " +
                    " where freeitemcode = '"+freeitemcode+"' and purchaseitemcode='"+purchaseitemcode+"' " +
                    " and freeflag='freeitem' ";
            Cursor mCur2 = mDb.rawQuery(sql3, null);
            mCur2.moveToFirst();
            String getfreecount = (mCur2.moveToFirst()) ? mCur2.getString(0) : "0";
            String getfreecartcode = (mCur2.moveToFirst()) ? mCur2.getString(1) : "0";

            if(getfreecount.equals("0")) {
                String sqlcart = "INSERT INTO  'tblsalesordercartdatas' VALUES " +
                        "('" + getmaxcartcode + "','" + itemcode + "'," +
                        " '" + companycode + "','" + brandcode + "','" + manualitemcode + "','" + itemname + "','" + itemnametamil + "'," +
                        " '" + unitcode + "','" + unitweightunitcode + "','" + unitweight + "'," +
                        " '" + uppunitcode + "','" + uppweight + "','" + itemcategory + "'," +
                        " '" + parentitemcode + "','" + allowpriceedit + "','" + allownegativestock + "' ," +
                        " '" + allowdiscount + "','" + stockqty + "','" + unitname + "'," +
                        " '" + noofdecimals + "','" + oldprice + "','" + newprice + "'," +
                        " '" + colourcode + "','" + hsn + "','" + tax + "'," +
                        " '" + itemqty + "','" + subtotal + "','" + routeallowpricedit + "'," +
                        " '" + discount + "','" + freeflag + "','" + purchaseitemcode + "'," +
                        " '" + freeitemcode + "'  )";
                mDb.execSQL(sqlcart);
            }else if(Integer.parseInt(getfreecount) > 0){
                String sqlcart = "UPDATE   'tblsalesordercartdatas' SET " +
                        " companycode= '" + companycode + "',brandcode='" + brandcode + "'," +
                        " manualitemcode='" + manualitemcode + "',itemname='" + itemname + "',itemnametamil='" + itemnametamil + "'," +
                        " unitcode='" + unitcode + "',unitweightunitcode='" + unitweightunitcode + "',unitweight='" + unitweight + "'," +
                        " uppunitcode='" + uppunitcode + "',uppweight='" + uppweight + "',itemcategory='" + itemcategory + "'," +
                        " parentitemcode='" + parentitemcode + "',allowpriceedit='" + allowpriceedit + "',allownegativestock='" + allownegativestock + "' ," +
                        " allowdiscount='" + allowdiscount + "',stockqty='" + stockqty + "',unitname='" + unitname + "'," +
                        " noofdecimals='" + noofdecimals + "',oldprice='" + oldprice + "',newprice='" + newprice + "'," +
                        " colourcode='" + colourcode + "',hsn='" + hsn + "',tax='" + tax + "'," +
                        " itemqty='" + itemqty + "',subtotal='" + subtotal + "',routeallowpricedit='" + routeallowpricedit + "'," +
                        " discount='" + discount + "',freeflag='" + freeflag + "',purchaseitemcode='" + purchaseitemcode + "'," +
                        " freeitemcode='" + freeitemcode + "'   where  cartcode='"+getfreecartcode+"'" +
                        " and freeitemcode='"+freeitemcode+"' and purchaseitemcode='"+purchaseitemcode+"'" +
                        "  and  freeflag='freeitem' ";
                mDb.execSQL(sqlcart);
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }




        return "success";

    }

    //Update schedule Flag
    public void UpdateScheduleFlag(String getschedulecode)
    {
        try{
            String sql = "update tblsalesschedule set flag=1 where schedulecode='"+getschedulecode+"'";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

    }
    //Update Customer Flag
    public void UpdateCustomerFlag(String getcustomercode)
    {
        try{
            String sql = "update tblcustomer set flag=2 where customercode='"+getcustomercode+"'";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

    }
    //Update Customer Flag
    public void UpdateCustomerMobilenostatus(String getcustomercode, String mobileno)
    {
        try{
            String sql = "update tblcustomer set mobilenoverificationstatus=1,flag=1,mobileno='"+mobileno+"' " +
                    "where customercode='"+getcustomercode+"'";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

    }
    //Update Order Details Flag
    public void UpdateOrderDetailsFlag(String getschedulecode)
    {
        try{
            String Getdate= GenCreatedDate();
            String sql = "update tblorderdetails set flag=2 where schedulecode='"+getschedulecode+"'" +
                    " and vancode='"+preferenceMangr.pref_getString("getvancode")+"' and orderdate=datetime('"+Getdate+"') ";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

    }
    //Update Expense Flag
    public void UpdateExpenseDetailsFlag(String gettransactionno)
    {
        try{
            String Getdate= GenCreatedDate();
            String sql = "update tblexpenses set flag=2 ,syncstatus=1 where transactionno='"+gettransactionno+"'" +
                    " and vancode='"+preferenceMangr.pref_getString("getvancode")+"' and " +
                    " financialyearcode='"+preferenceMangr.pref_getString("getfinanceyrcode")+"' and flag=1 ";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

    }

    //Update Cash reporrt and denomination Flag
    public void UpdateCashDetailsFlag(String getschedulecode)
    {
        try{
            String sql1 = "update tblcashreport set flag=2  where schedulecode='"+getschedulecode+"' ";
            mDb.execSQL(sql1);
            String sql = "update tbldenomination set flag=2  where schedulecode='"+getschedulecode+"' ";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

    }

    //Update Cash close Flag
    public void UpdateCashCloseFlag(String getschedulecode)
    {
        try{
            String sql1 = "update tblclosecash set flag=2  where schedulecode='"+getschedulecode+"' ";
            mDb.execSQL(sql1);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

    }
    //Update Sales close Flag
    public void UpdateSalesCloseFlag(String getschedulecode)
    {
        try{
            String sql1 = "update tblclosesales set flag=2  where schedulecode='"+getschedulecode+"' ";
            mDb.execSQL(sql1);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

    }

    //Update NIl stock close Flag
    public void UpdateNilStockFlag(String getschedulecode,String getautonum)
    {
        try{
            String sql1 = "update tblnilstocktransaction set syncflag=1  where schedulecode='"+getschedulecode+"' and autonum ='"+getautonum+"' ";
            mDb.execSQL(sql1);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

    }

    //Update Receipt Flag
    public void UpdateReceiptDetailsFlag(String gettransactionno)
    {
        try{
            String Getdate= GenCreatedDate();
            String sql = "update tblreceipt set flag=2 ,syncstatus=1 where transactionno='"+gettransactionno+"'" +
                    " and vancode='"+preferenceMangr.pref_getString("getvancode")+"' and " +
                    " financialyearcode='"+preferenceMangr.pref_getString("getfinanceyrcode") +"' and flag=1  AND type='Receipt' ";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

    }
    //Delete Expense Flag
    public void DeleteExpenseDetailsFlag(String gettransactionno)
    {
        try{
            String sql = "delete from tblexpenses where transactionno='"+gettransactionno+"'" +
                    " and vancode='"+preferenceMangr.pref_getString("getvancode")+"' and " +
                    " financialyearcode='"+preferenceMangr.pref_getString("getfinanceyrcode")+"' and flag=3 ";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

    }

    //Update cancel Receipt flag
    public String  UpdateCancelReceiptFlag(String gettranasactionno) {
        try{
            mDb = mDbHelper.getReadableDatabase();
            String sql = "update tblreceipt set flag=6,syncstatus=1 where transactionno = '"+gettranasactionno+"'" +
                    "and vancode='"+preferenceMangr.pref_getString("getvancode")+"' and financialyearcode='"+preferenceMangr.pref_getString("getfinanceyrcode")+"'" +
                    " and flag=3 AND type='Receipt'";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }


        return "success";

    }
    /***********************INSERT SALES FUNCTIONALITY***********************/
    public String GetMaxRefnoSalesItems(){
        String getrefno="";
        try{
            mDb = mDbHelper.getReadableDatabase();
            String sql1 = "select COALESCE(max(refno),0)+1 from tbltempsalesitemdetails";
            Cursor mCur = mDb.rawQuery(sql1, null);
            mCur.moveToFirst();
            getrefno= (mCur.moveToFirst())?mCur.getString(0):"0";
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return  getrefno;
    }

    public String GetMaxRefnoSalesOrderItems(){
        String getrefno="";
        try{
            mDb = mDbHelper.getReadableDatabase();
            String sql1 = "select COALESCE(max(refno),0)+1 from tbltempsalesorderitemdetails";
            Cursor mCur = mDb.rawQuery(sql1, null);
            mCur.moveToFirst();
            getrefno= (mCur.moveToFirst())?mCur.getString(0):"0";
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return  getrefno;
    }
    // insert temp sales item details
    public void  InsertTempSalesItemDetails(String itemcode,String companycode,String qty,String price,
                                            String discount,String amount,String freeitemstatus,String tax,
                                            String gstin,String getrefno,double getweight,int autonum,
                                            String ratediscount,String schemeapplicable,String orgprice) {
        try{
            mDb = mDbHelper.getReadableDatabase();
            double cgst,sgst,igst,cgstamt,sgstamt,igstamt;

            double vartaxamount=((Double.parseDouble(price)/(1+Double.parseDouble(tax)/100))*(Double.parseDouble(tax)/100))*Double.parseDouble(qty);
            if (!gstin.equals(""))
            {
                if(freeitemstatus.equals("freeitem") && discount.equals("0")) {
                    igst=0;
                    cgst=0;
                    sgst=0;
                    cgstamt=0;
                    sgstamt=0;
                    igstamt=0;
                }else{
                    if ((gstin.substring(0,2)).equals("33"))
                    {
                        cgst=Double.parseDouble(tax)/2;
                        sgst=cgst;
                        igst=0;
                        cgstamt=vartaxamount/2;
                        sgstamt=cgstamt;
                        igstamt=0;
                    }
                    else
                    {
                        igst=Double.parseDouble(tax);
                        cgst=0;
                        sgst=0;
                        cgstamt=0;
                        sgstamt=0;
                        igstamt=vartaxamount;

                    }
                }

            }
            else
            {
                if(freeitemstatus.equals("freeitem") && discount.equals("0")) {
                    igst=0;
                    cgst=0;
                    sgst=0;
                    cgstamt=0;
                    sgstamt=0;
                    igstamt=0;
                }else {
                    cgst = Double.parseDouble(tax) / 2;
                    sgst = cgst;
                    igst = 0;
                    cgstamt = vartaxamount / 2;
                    sgstamt = cgstamt;
                    igstamt = 0;
                }
            }


            String sql="INSERT INTO tbltempsalesitemdetails(autonum,refno,companycode,itemcode,qty,price,discount," +
                    "amount,cgst,sgst,igst,cgstamt,sgstamt,igstamt,freeitemstatus,weight,ratediscount,schemeapplicable,orgprice) " +
                    "values ('"+autonum+"','"+ getrefno +"','"+ companycode +"','"+ itemcode +"','"+ Double.parseDouble(qty) +"','"+ Double.parseDouble(price) +"'," +
                    "'"+ discount +"','"+ amount +"','"+ dft.format(cgst) +"','"+ dft.format(sgst) +"','"+ dft.format(igst)+"'," +
                    "'"+ dft.format(cgstamt) +"','"+ dft.format(sgstamt) +"','"+ dft.format(igstamt) +"'," +
                    "'"+ freeitemstatus +"','"+getweight+"','"+ratediscount+"','"+schemeapplicable+"','"+orgprice+"')";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }


    }

    // insert temp sales item details
    public void  InsertTempSalesOrderItemDetails(String itemcode,String companycode,String qty,String price,
                                                 String discount,String amount,String freeitemstatus,String tax,
                                                 String gstin,String getrefno,double getweight,int autonum) {
        try{
            mDb = mDbHelper.getReadableDatabase();

            double vartaxamount=((Double.parseDouble(price)/(1+Double.parseDouble(tax)/100))*(Double.parseDouble(tax)/100))*Double.parseDouble(qty);
            double cgst,sgst,igst,cgstamt,sgstamt,igstamt;
            if (!gstin.equals(""))
            {
                if ((gstin.substring(0,2)).equals("33"))
                {
                    cgst=Double.parseDouble(tax)/2;
                    sgst=cgst;
                    igst=0;
                    cgstamt=vartaxamount/2;
                    sgstamt=cgstamt;
                    igstamt=0;
                }
                else
                {
                    igst=Double.parseDouble(tax);
                    cgst=0;
                    sgst=0;
                    cgstamt=0;
                    sgstamt=0;
                    igstamt=vartaxamount;

                }
            }
            else
            {
                cgst=Double.parseDouble(tax)/2;
                sgst=cgst;
                igst=0;
                cgstamt=vartaxamount/2;
                sgstamt=cgstamt;
                igstamt=0;
            }
            String sql="INSERT INTO tbltempsalesorderitemdetails(autonum,refno,companycode,itemcode,qty,price,discount," +
                    "amount,cgst,sgst,igst,cgstamt,sgstamt,igstamt,freeitemstatus,weight) " +
                    "values ('"+autonum+"','"+ getrefno +"','"+ companycode +"','"+ itemcode +"','"+ Double.parseDouble(qty) +"','"+ Double.parseDouble(price) +"'," +
                    "'"+ discount +"','"+ amount +"','"+ dft.format(cgst) +"','"+ dft.format(sgst) +"','"+ dft.format(igst)+"'," +
                    "'"+ dft.format(cgstamt) +"','"+ dft.format(sgstamt) +"','"+ dft.format(igstamt) +"','"+ freeitemstatus +"','"+getweight+"')";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }


    }

    //Insert into Sales
    public String InsertSales(String vancode,String billdate,String customercode,String billtypecode,
                              String gstin,String schedulecode,String subtotal,String discount,String
                                      grandtotal,String financialyearcode,String remarks,String bookingno,
                              String Transactionno,String getrefno,String billcopystatus,String getcashpaidstatus,
                              String billLatLong,String billlatitute,String billLongtitude,
                              String orderTransNo,String orderFinancialyearcode,String orderCompanycode)
    {
        try{
            String Gencode= GenCreatedDate();
            mDb = mDbHelper.getReadableDatabase();
            String getcurtime = GetDateTime();

            getdate= GenCreatedDateTime();
            String lunchflag="";
            Cursor mCurc=null;
            Cursor mCurc_lasttransno=null;
            String LASTtransactionno="";
            try{
                String lasttransactionno = "select coalesce(transactionno,0) from tblsales where date(billdate)=date('"+getdate+"') and schedulecode='"+schedulecode+"' and vancode='"+preferenceMangr.pref_getString("getvancode")+"' and " +
                        " financialyearcode='"+preferenceMangr.pref_getString("getfinanceyrcode")+"' order by transactionno desc limit 1";
                mCurc = mDb.rawQuery(lasttransactionno, null);
                if (mCurc.getCount() > 0) {
                    mCurc.moveToFirst();
                }

                if (mCurc.getCount() > 0 && mCurc.getString(0) != null) {
                    LASTtransactionno= mCurc.getString(0);
                    String lunchflagcount = "select coalesce(count(*),0) from tblsales where date(billdate)=date('"+getdate+"') and schedulecode='"+schedulecode+"' and vancode='"+preferenceMangr.pref_getString("getvancode")+"' and " +
                            " financialyearcode='"+preferenceMangr.pref_getString("getfinanceyrcode")+"' and lunchflag=1 and transactionno='"+LASTtransactionno+"' order by transactionno desc limit 1";
                    mCurc_lasttransno = mDb.rawQuery(lunchflagcount, null);
                    if (mCurc_lasttransno.getCount() > 0) {
                        mCurc_lasttransno.moveToFirst();
                    }
                    int count = Integer.parseInt(mCurc_lasttransno.getString(0));
                    if(count>0){
                        lunchflag="2";
                    }
                }



            }catch (Exception ex){
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
            String sql1="select count(*) from tblstockconversion where itemcode not in (" +
                    "select itemcode from tbltempsalesitemdetails union all " +
                    "select parentitemcode from tblitemmaster as a inner join tbltempsalesitemdetails as b" +
                    "  on a.itemcode=b.itemcode )";
            Cursor mCurc1 = mDb.rawQuery(sql1, null);
            mCurc.moveToFirst();
            String getcount= (mCurc.moveToFirst())?mCurc.getString(0):"0";
            if(!getcount.equals("0")){
                String sqldeletestockconversion1="delete from tblstockconversion where itemcode not in " +
                        "(select itemcode from tbltempsalesitemdetails union all" +
                        " select parentitemcode from tblitemmaster as a inner join tbltempsalesitemdetails as b" +
                        " on a.itemcode=b.itemcode  ) ";
                mDb.execSQL(sqldeletestockconversion1);
            }
            String sql="INSERT INTO tblsales(autonum,companycode,vancode,transactionno,billno,refno,bookingno,prefix,suffix,billdate,customercode," +
                    "billtypecode,gstin," +
                    "schedulecode,subtotal,discount,grandtotal,flag,makerid,createddate,financialyearcode,remarks,syncstatus," +
                    "billcopystatus,cashpaidstatus,salestime,beforeroundoff,lunchflag,ratediscount,schemeapplicable,latlong,ordertransactionno)" +
                    " select (select coalesce(max(autonum),0)+1 from tblsales),companycode,'"+ vancode +"','"+ Transactionno +"', case when (select Count(*) from tblsales where  companycode=a.companycode  and billtypecode = "+billtypecode+" )=0 " +
                    " then case when (select coalesce(max(refno),0)+1 from tblmaxcode where code=222 " +
                    " and companycode=a.companycode and billtypecode = "+billtypecode+" )=1 then " +
                    "(select prefix || printf('%0'||noofdigit||'d', startingno)||suffix FROM tblvouchersettings " +
                    "where type='sales' and billtypecode='"+ billtypecode +"' and vancode='"+ vancode +"' " +
                    "and companycode=a.companycode and financialyearcode='"+ financialyearcode +"')" +
                    "else" +
                    "(select prefix || printf('%0'||noofdigit||'d', (select coalesce(max(refno),0)+1 from " +
                    "tblmaxcode where code=222 and companycode=a.companycode and billtypecode = "+billtypecode+" ))||suffix FROM tblvouchersettings where type='sales' and " +
                    "billtypecode='"+ billtypecode +"' and vancode='"+ vancode +"' and companycode=a.companycode " +
                    "and financialyearcode='"+ financialyearcode +"')" +
                    "end else" +
                    "(select prefix || printf('%0'||noofdigit||'d', (select coalesce(max(refno),0)+1 from tblsales " +
                    "where  companycode=a.companycode and billtypecode = "+billtypecode+"  and financialyearcode='"+ financialyearcode +"'))||suffix " +
                    "FROM tblvouchersettings where type='sales' and billtypecode='"+ billtypecode +"' and " +
                    "vancode='"+ vancode +"' and companycode=a.companycode and financialyearcode='"+ financialyearcode +"')" +
                    " end as billno," +
                    "case when (select Count(*) from tblsales  where  companycode=a.companycode" +
                    " and billtypecode = "+billtypecode+"  )=0 then case when (select coalesce(max(refno),0)+1 from " +
                    " tblmaxcode where  code=222 and companycode=a.companycode and billtypecode = "+billtypecode+"  )=1 then " +
                    " (select  printf('%0'||noofdigit||'d', startingno) FROM tblvouchersettings where type='sales' " +
                    "and billtypecode='"+billtypecode+"' and vancode='"+vancode+"' and companycode=a.companycode" +
                    " and financialyearcode='"+financialyearcode+"')" +
                    " else " +
                    " (select printf('%0'||noofdigit||'d', (select coalesce(max(refno),0)+1 from tblmaxcode where " +
                    " code=222 and companycode=a.companycode and billtypecode = "+billtypecode+"  ))" +
                    " FROM tblvouchersettings where type='sales' and billtypecode='"+ billtypecode +"' and " +
                    "vancode='"+ vancode +"' and companycode=a.companycode and financialyearcode='"+ financialyearcode +"')" +
                    "end else" +
                    "(select printf('%0'||noofdigit||'d', (select coalesce(max(refno),0)+1 from tblsales " +
                    "where  companycode=a.companycode and billtypecode = "+billtypecode+"  and financialyearcode='"+ financialyearcode +"')) FROM tblvouchersettings " +
                    "where type='sales' and billtypecode='"+ billtypecode +"' and vancode='"+ vancode +"' and" +
                    " companycode=a.companycode and financialyearcode='"+ financialyearcode +"')" +
                    " end as refno,'"+ bookingno +"',(select prefix FROM tblvouchersettings where type='sales' " +
                    "and billtypecode='"+ billtypecode +"' and vancode='"+ vancode +"' and " +
                    "companycode=a.companycode and financialyearcode='"+ financialyearcode +"') as prefix," +
                    " (select suffix FROM tblvouchersettings where type='sales' and billtypecode='"+ billtypecode +"'" +
                    " and vancode='"+ vancode+"' and companycode=a.companycode and " +
                    " financialyearcode='"+ financialyearcode +"') as suffix,datetime('"+ billdate +"')," +
                    " '"+ customercode +"','"+ billtypecode +"','"+ gstin +"','"+ schedulecode +"', round(sum(amount),0)," +
                    "coalesce((select sum(discount) from tbltempsalesitemdetails where refno='"+ getrefno +"' " +
                    " and companycode=a.companycode " +
                    " and freeitemstatus<>''),0)," +
                    "  round(round(sum(amount),0)-coalesce((select sum(discount) from tbltempsalesitemdetails where refno='"+ getrefno +"' " +
                    "and companycode=a.companycode and freeitemstatus='freeitem'),0),0),'1','1',datetime('now', 'localtime')," +
                    " '"+ financialyearcode +"','"+ remarks +"' ,0,'"+billcopystatus+"','"+getcashpaidstatus+"','"+getcurtime+"'," +
                    "sum(amount)-coalesce((select sum(discount) from tbltempsalesitemdetails where refno='"+ getrefno +"' " +
                    "and companycode=a.companycode and freeitemstatus<>''),0),'"+lunchflag+"',coalesce(sum(ratediscount),0) " +
                    ",(SELECT CASE WHEN (SELECT count(*) as count FROM tblsalescartdatas where schemeapplicable='yes') > 0  \n" +
                    "then 'yes' else CASE WHEN (SELECT count(*) as count FROM tblsalescartdatas where schemeapplicable='no')\n" +
                    "then 'no' else 'not applicable' end end as scheme),'" + billLatLong + "','" + orderTransNo + "' "  +
                    " from tbltempsalesitemdetails as  a where refno='"+ getrefno +"' group by companycode";
            mDb.execSQL(sql);

            String sqlseletecash =  "UPDATE tblnilstocktransaction set flag=3,syncflag=0 where salesitemcode" +
                    " in (SELECT itemcode  from tbltempsalesitemdetails)";
            mDb.execSQL(sqlseletecash);

            String sqlitem="INSERT INTO tblsalesitemdetails(autonum,transactionno,bookingno,financialyearcode,companycode,itemcode,qty,price,discount,amount,cgst,sgst,igst," +
                    "cgstamt,sgstamt,igstamt,freeitemstatus,makerid,createddate,vancode,flag,weight,ratediscount,schemeapplicable,orgprice)" +
                    "SELECT (select coalesce(max(autonum),0)+1 from tblsalesitemdetails)+autonum,'"+ Transactionno +"','"+ bookingno +"','"+ financialyearcode +"',companycode,itemcode,qty,price,discount,amount,cgst,sgst,igst,cgstamt,sgstamt,igstamt," +
                    "freeitemstatus,'1',datetime('now', 'localtime'),'"+vancode+"',1,weight,ratediscount,schemeapplicable,orgprice from tbltempsalesitemdetails as  a where refno='"+ getrefno +"'";
            mDb.execSQL(sqlitem);

            String sqlstock="INSERT INTO tblstocktransaction(transactionno,transactiondate,vancode,itemcode,inward,outward,type,refno,createddate,flag,companycode,op,financialyearcode,autonum)" +
                    "SELECT (select coalesce(max(transactionno),0)+1 from tblstocktransaction),datetime('"+ Gencode +"'),'"+ vancode +"',itemcode,0,qty," +
                    "'sales','"+ Transactionno +"',datetime('now', 'localtime'),1,companycode,0,'"+financialyearcode+"',(select coalesce(max(autonum),0)+1 from tblstocktransaction)+autonum from tbltempsalesitemdetails as  a where refno='"+ getrefno +"'";

            mDb.execSQL(sqlstock);

            String sqlstock1="INSERT INTO tblappstocktransactiondetails(transactionno,transactiondate,vancode,itemcode,inward,outward,type,refno,createddate,flag,companycode,op,financialyearcode,syncstatus)" +
                    "SELECT (select coalesce(max(transactionno),0)+1 from tblappstocktransactiondetails),datetime('"+ Gencode +"'),'"+ vancode +"',itemcode,0,qty," +
                    "'sales','"+ Transactionno +"',datetime('now', 'localtime'),1,companycode,0,'"+financialyearcode+"',0 from tbltempsalesitemdetails as  a where refno='"+ getrefno +"'";

            mDb.execSQL(sqlstock1);


            //Stock conversion
            String sqlstockconversion = "INSERT INTO  'tblstocktransaction' (transactionno,transactiondate,vancode,itemcode,inward,outward,type,refno,createddate,flag,companycode,op,financialyearcode,autonum) " +
                    " SELECT (select coalesce(max(transactionno),0)+1 from tblstocktransaction) ,transactiondate," +
                    " vancode,itemcode,inward,outward,type,'"+ Transactionno +"',createddate,1,0,0,'"+financialyearcode+"',(select coalesce(max(transactionno),0)+1 from tblstocktransaction) from tblstockconversion ";
            mDb.execSQL(sqlstockconversion);

            //Stock conversion
            String sqlsalesstockconversion = "INSERT INTO  'tblsalesstockconversion' (transactiondate,vancode,itemcode,inward,outward,type," +
                    " refno,createddate,flag,companycode,op,financialyearcode,autonum) " +
                    " SELECT transactiondate," +
                    " vancode,itemcode,inward,outward,type,'"+ Transactionno +"',createddate,1,0,0,'"+financialyearcode+"'," +
                    "(select coalesce(max(autonum),0)+1 from tblsalesstockconversion)" +
                    " from tblstockconversion ";
            mDb.execSQL(sqlsalesstockconversion);



            String sqlstockconversion1 = "INSERT INTO  'tblappstocktransactiondetails'  " +
                    " SELECT (select coalesce(max(transactionno),0)+1 from tblappstocktransactiondetails) ,transactiondate," +
                    " vancode,itemcode,inward,outward,type,'"+ Transactionno +"',createddate,1,0,0,'"+financialyearcode+"',0 from tblstockconversion ";
            mDb.execSQL(sqlstockconversion1);

            String sqldeletesales="delete from tbltempsalesitemdetails where refno='"+ getrefno +"'";
            mDb.execSQL(sqldeletesales);

            String sqldeletestockconversion="delete from tblstockconversion ";
            mDb.execSQL(sqldeletestockconversion);


            Cursor mCur=null;
            try{
                String custLatLong="";
                String sqlCustomerLatLng = "select (coalesce(latitude,null) || ',' || coalesce(longitude,null)) as latlong from tblcustomer " +
                        "where customercode='" + customercode + "' and status='Active' and areacode=" + preferenceMangr.pref_getString(Constants.KEY_GETAREACODE);
                mCur = mDb.rawQuery(sqlCustomerLatLng, null);
                if(mCur != null && mCur.getCount() > 0){
                    mCur.moveToFirst();
                    custLatLong = mCur.getString(0);
                }
                if(mCur != null)
                    mCur.close();
                if(Utilities.isNullOrEmpty(custLatLong) || custLatLong.equals(Constants.LATLONG_NULL_VALUE) ||
                        custLatLong.equals(Constants.LATLONG_ZERO_VALUE)  || custLatLong.equals("0.0") || custLatLong.equals("0,0") || custLatLong.equals("0")){
                    String sqlupdateCustLatlong =  "UPDATE tblcustomer set latitude='" + billlatitute + "',longitude='" + billLongtitude + "'," +
                            " updateddate=datetime('now', 'localtime'), flag=1 " +
                            " where customercode='" + customercode + "' and status='Active' and areacode=" + preferenceMangr.pref_getString(Constants.KEY_GETAREACODE);
                    mDb.execSQL(sqlupdateCustLatlong);
                }
            }catch (Exception ex){
                insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "insert sales - Exception in customer master latlong insert", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }

            try{
                if(!Utilities.isNullOrEmpty(orderCompanycode) && !Utilities.isNullOrEmpty(orderTransNo) &&  !Utilities.isNullOrEmpty(orderFinancialyearcode)){
                    String sqlUpdateOrderStatus="update tblsalesorder set status=4,syncstatus=0,updateddate=datetime('now', 'localtime') " +
                            " where transactionno=" + orderTransNo + " and customercode='" + customercode + "' and " +
                            " financialyearcode=" + orderFinancialyearcode + " and status=1";
                    mDb.execSQL(sqlUpdateOrderStatus);
                }
            }catch (Exception ex){
                insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "insert sales - Exception in update order status", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }

        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }


        return Transactionno;
    }


    //Insert into Sales
    public String InsertSalesOrder(String vancode,String billdate,String customercode,String billtypecode,
                                   String gstin,String schedulecode,String subtotal,String discount,String
                                           grandtotal,String financialyearcode,String remarks,String bookingno,
                                   String Transactionno,String getrefno,String billcopystatus,String getcashpaidstatus,
                                   String transportid,String getsalessordervoucherno,String gettransportmode,
                                   String billLatLong,Integer flag,String status )
    {
        try{
            String Gencode= GenCreatedDate();
            mDb = mDbHelper.getReadableDatabase();
            String getcurtime = GetDateTime();

            String sql="INSERT INTO tblsalesorder(autonum,companycode,vancode,transactionno,billno,refno,bookingno,prefix,suffix,billdate,customercode,billtypecode,gstin," +
                    "schedulecode,subtotal,discount,grandtotal,flag,makerid,createddate,financialyearcode,remarks,syncstatus," +
                    "billcopystatus,cashpaidstatus,salestime,beforeroundoff,transportid,status,transportmode,latlong)" +
                    " select (select coalesce(max(autonum),0)+1 from tblsalesorder),companycode,'"+ vancode +"'," +
                    " '"+ Transactionno +"', '"+getsalessordervoucherno+"' ," +
                    "case when (select Count(*) from tblsalesorder  where  " +
                    "  billtypecode = "+billtypecode+"  )=0 then case when (select coalesce(max(refno),0)+1 from " +
                    " tblmaxcode where  code=777  and billtypecode = "+billtypecode+"  )=1 then " +
                    " (select  printf('%0'||noofdigit||'d', startingno) FROM tblvouchersettings where type='salesorder' " +
                    "and billtypecode='"+billtypecode+"' and vancode='"+vancode+"' " +
                    " and financialyearcode='"+financialyearcode+"')" +
                    " else " +
                    " (select printf('%0'||noofdigit||'d', (select coalesce(max(refno),0)+1 from tblmaxcode where " +
                    " code=777  and billtypecode = "+billtypecode+"  ))" +
                    " FROM tblvouchersettings where type='salesorder' and billtypecode='"+ billtypecode +"' and " +
                    "vancode='"+ vancode +"'  and financialyearcode='"+ financialyearcode +"')" +
                    "end else" +
                    "(select printf('%0'||noofdigit||'d', (select coalesce(max(refno),0)+1 from tblsalesorder " +
                    "where  billtypecode = "+billtypecode+"  and financialyearcode='"+ financialyearcode +"')) FROM tblvouchersettings " +
                    "where type='salesorder' and billtypecode='"+ billtypecode +"' and vancode='"+ vancode +"' and" +
                    " financialyearcode='"+ financialyearcode +"')" +
                    " end as refno,'"+ bookingno +"',(select prefix FROM tblvouchersettings where type='salesorder' " +
                    "and billtypecode='"+ billtypecode +"' and vancode='"+ vancode +"' and " +
                    " financialyearcode='"+ financialyearcode +"') as prefix," +
                    " (select suffix FROM tblvouchersettings where type='salesorder' and billtypecode='"+ billtypecode +"'" +
                    " and vancode='"+ vancode+"'  and " +
                    " financialyearcode='"+ financialyearcode +"') as suffix,datetime('"+ billdate +"')," +
                    " '"+ customercode +"','"+ billtypecode +"','"+ gstin +"','"+ schedulecode +"', round(sum(amount),0)," +
                    "coalesce((select sum(discount) from tbltempsalesorderitemdetails where refno='"+ getrefno +"' " +
                    " " +
                    " and freeitemstatus<>''),0)," +
                    "  round(round(sum(amount),0)-coalesce((select sum(discount) from tbltempsalesorderitemdetails where refno='"+ getrefno +"' " +
                    " and freeitemstatus='freeitem' ),0),0),'"+ flag +"','1',datetime('now', 'localtime')," +
                    " '"+ financialyearcode +"','"+ remarks +"' ,0,'"+billcopystatus+"','"+getcashpaidstatus+"','"+getcurtime+"'," +
                    "sum(amount)-coalesce((select sum(discount) from tbltempsalesorderitemdetails where refno='"+ getrefno +"' " +
                    " and freeitemstatus<>''),0),'"+transportid+"','"+status+"','"+gettransportmode+"','"+billLatLong+"' "  +
                    " from tbltempsalesorderitemdetails as  a where refno='"+ getrefno +"' ";
            mDb.execSQL(sql);

            String sqlitem="INSERT INTO tblsalesorderitemdetails(autonum,transactionno,bookingno,financialyearcode,companycode,itemcode,qty,price,discount,amount,cgst,sgst,igst," +
                    "cgstamt,sgstamt,igstamt,freeitemstatus,makerid,createddate,vancode,flag,weight)" +
                    "SELECT (select coalesce(max(autonum),0)+1 from tblsalesorderitemdetails)+autonum,'"+ Transactionno +"','"+ bookingno +"','"+ financialyearcode +"',companycode,itemcode,qty,price,discount,amount,cgst,sgst,igst,cgstamt,sgstamt,igstamt," +
                    "freeitemstatus,'1',datetime('now', 'localtime'),'"+vancode+"',1,weight from tbltempsalesorderitemdetails as  a where refno='"+ getrefno +"'";
            mDb.execSQL(sqlitem);





            String sqldeletesales="delete from tbltempsalesorderitemdetails where refno='"+ getrefno +"'";
            mDb.execSQL(sqldeletesales);


        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }


        return Transactionno;
    }
    //Update sales receipt
    public String UpdateSalesReceipt(String gettransactionno,String getbillcopystatus ,String getpaymentstatus)
    {
        try{
            String sql = "update tblsales set billcopystatus='"+getbillcopystatus+"' ," +
                    " cashpaidstatus = '"+getpaymentstatus+"' where transactionno='"+gettransactionno+"' " +
                    "and financialyearcode ='"+preferenceMangr.pref_getString("getfinanceyrcode") +"' ";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return  "success";
    }

    //Find and insert nil stock
    public String InsertnilStock(String getvancode,String getschedulecode,String getsalestransactionno,String getsalesbookingno,
                                 String getsalesfinacialyearcode,String getsalescustomercode )
    {
        try{
            String getdate = GenCreatedDateTime();
            String sql = "INSERT INTO tblnilstocktransaction (vancode,schedulecode,salestransactionno,salesbookingno,salesfinacialyearcode," +
                    "salescustomercode,salesitemcode,createddate,flag,syncflag) " +
                    "select '"+getvancode+"','"+getschedulecode+"','"+getsalestransactionno+"'," +
                    " '"+getsalesbookingno+"','"+getsalesfinacialyearcode+"','"+getsalescustomercode+"'," +
                    " dev.itemcode,datetime('"+getdate+"') ,1,0 from (select a.itemcode,coalesce((sum(b.op)+sum(b.inward)-sum(b.outward)),0) as stock" +
                    "  from tblsalesitemdetails as a inner join tblstocktransaction as b on a.itemcode=b.itemcode " +
                    " where a.transactionno ='"+getsalestransactionno+"' and a.financialyearcode='"+getsalesfinacialyearcode+"'" +
                    "  and a.bookingno='"+getsalesbookingno+"' and datetime(transactiondate)<= " +
                    "datetime('"+getdate+"')  and b.flag!=3   group by a.itemcode ) as dev where dev.stock = 0";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return  "success";
    }

    //update nil stock
    public String UpdateNilStock(String getvancode,String getschedulecode,String getsalestransactionno,
                                 String getsalesfinacialyearcode )
    {
        try{
            String getdate = GenCreatedDate();
            String sql = "UPDATE tblnilstocktransaction set  flag=3,syncflag=0  where vancode='"+getvancode+"'" +
                    " and schedulecode='"+getschedulecode+"' and salestransactionno='"+getsalestransactionno+"'" +
                    " and salesfinacialyearcode='"+getsalesfinacialyearcode+"' ";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return  "success";
    }

    //update nil stock for einvoice cancel
    public String UpdateEinvoiceNilStock(String getvancode,String getschedulecode,String getsalestransactionno,
                                         String getsalesfinacialyearcode ,String companycode)
    {
        try{
            String getdate = GenCreatedDate();
            String sql = "UPDATE tblnilstocktransaction set  flag=3,syncflag=0  where vancode='"+getvancode+"'" +
                    " and schedulecode='"+getschedulecode+"' and salestransactionno='"+getsalestransactionno+"'" +
                    " and salesfinacialyearcode='"+getsalesfinacialyearcode+"' and salesitemcode in" +
                    "  (select itemcode from tblsalesitemdetails where transactionno= '"+getsalestransactionno+"'" +
                    "  and financialyearcode='"+getsalesfinacialyearcode+"' and companycode='"+companycode+"')";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return  "success";
    }
    public String updateSalesImageData(String gettransactionno, String getfinancialyr,
                                       String getbitmapimagedata)
    {
        Cursor mCurs =null;
        try{
            if(!(gettransactionno.equals(""))) {

                String exequery = "UPDATE tblsales set bitmapimage='"+getbitmapimagedata+"'," +
                        " imgflag=0 where transactionno='"+gettransactionno+"' and" +
                        " financialyearcode ='"+getfinancialyr+"' ";
                mDb.execSQL(exequery);
            }
            String sqls = "SELECT count(*) from 'tblsales'  where transactionno='"+gettransactionno+"' and " +
                    " financialyearcode ='"+getfinancialyr+"' and imgflag=0 ";
            mCurs = mDb.rawQuery(sqls, null);
            mCurs.moveToFirst();
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return (mCurs.moveToFirst())?mCurs.getString(0):"0";
    }
    public Cursor GetSalesImageDetails() {
        Cursor mCur = null;
        try{
            String sql = "select transactionno,bitmapimage,financialyearcode," +
                    " (select 'V' || substr('00'||vancode, -2, 2) || substr('0000'||transactionno, -4, 4) || '-' ||" +
                    " substr('0000'||financialyearcode, -3, 3) as imagecode " +
                    " ) from tblsales where imgflag=0;";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0) {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    public Cursor GetSalesImageData(String transno,String finayr) {
        Cursor mCur =null;
        try{
            String sql = "select bitmapimage from tblsales where transactionno='"+transno+"' " +
                    " and financialyearcode = '"+finayr+"' ;";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0) {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    public Cursor GetServerurllistdata() {
        Cursor mCur =null;
        try{
            String sql = "select serverid,internetip,status,coalesce(urlname,'') as urlname from tblserversettings ;";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0) {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    public void SalesImageSetFlag(String gettransactionno,String getfinancialyrcode)
    {
        try{
            String sql = "update tblsales set imgflag=1 where transactionno='"+gettransactionno+"' and" +
                    " financialyearcode='"+getfinancialyrcode+"' ";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

    }
    public void UpdateSalesBitmapImage(String transno,String finayr)
    {
        try{
            String sql = "update tblsales set bitmapimage='BLOB',imgflag=2 where transactionno='"+transno+"' " +
                    " and financialyearcode = '"+finayr+"' ";
            mDb.execSQL(sql);
            String sqls1 = "select bitmapimage from tblsales where transactionno='"+transno+"' " +
                    " and financialyearcode = '"+finayr+"' ";
            Cursor mCur1 = mDb.rawQuery(sqls1, null);
            String getbitmapimg = (mCur1.moveToFirst())?mCur1.getString(0):"0";
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }


    }
    //Update sales List receipt
    //Flag 4 means updated sales receipt
    public String UpdateSalesListReceipt(String gettransactionno,String getfinanicialyear,String getbillcopystatus ,String getpaymentstatus)
    {
        try{
            String sql = "update tblsales set billcopystatus='"+getbillcopystatus+"' ," +
                    " cashpaidstatus = '"+getpaymentstatus+"',flag = 4  where transactionno='"+gettransactionno+"' and" +
                    " financialyearcode = '"+getfinanicialyear+"' and cashpaidstatus='' ";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return  "success";
    }

    public String UpdateSalesListCompanyReceipt(String gettransactionno,String getfinanicialyear,
                                                String getbillcopystatus ,String getpaymentstatus,
                                                String getCompanycode)
    {
        try{
            String sql = "update tblsales set billcopystatus='"+getbillcopystatus+"' ," +
                    " cashpaidstatus = '"+getpaymentstatus+"',flag = 4  where transactionno='"+gettransactionno+"' and" +
                    " financialyearcode = '"+getfinanicialyear+"' AND companycode = '"+ getCompanycode +"' ";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return  "success";
    }
    public void DeleteOrderDetails(){
        try{
            String sql1 = "delete from tblorderdetails where " +
                    " orderdate=datetime('"+ preferenceMangr.pref_getString("getformatdate") +"') " +
                    "  and schedulecode='"+preferenceMangr.pref_getString("getschedulecode")+"'  and vancode='"+preferenceMangr.pref_getString("getvancode")+"' ";
            mDb.execSQL(sql1);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

    }
    public String  InsertOrderDetails(String itemcode,String qty,String status) {
        try{
            mDb = mDbHelper.getReadableDatabase();

            String sql1 = "select COALESCE(count(*),0) from tblorderdetails where " +
                    " orderdate=datetime('"+ preferenceMangr.pref_getString("getformatdate") +"') and  itemcode='"+itemcode+"'" +
                    "  and schedulecode='"+preferenceMangr.pref_getString("getschedulecode")+"' ";
            Cursor mCur = mDb.rawQuery(sql1, null);
            mCur.moveToFirst();
            String getcount= (mCur.moveToFirst())?mCur.getString(0):"0";
            if(getcount.equals("0")) {
                String sql = "insert into tblorderdetails (autonum,vancode,schedulecode,itemcode,qty,makerid,createddate,orderdate,flag,status) " +
                        "select coalesce(max(autonum),0)+1 ,'" + preferenceMangr.pref_getString("getvancode") + "','" + preferenceMangr.pref_getString("getschedulecode") + "','" + itemcode + "','" + Double.parseDouble(qty) + "'," +
                        "'0',datetime('now', 'localtime'),datetime('" + preferenceMangr.pref_getString("getformatdate") + "'),1 ,'"+status+"' from tblorderdetails";
                mDb.execSQL(sql);
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return "success";

    }

    public String  InsertExpensesDetails(String getexpensesheadcode,String getamount,String getremarks) {
        try{
            mDb = mDbHelper.getReadableDatabase();


            String sqlc = "select  count(*) from tblexpenses " ;
            Cursor mCurc = mDb.rawQuery(sqlc, null);
            mCurc.moveToFirst();
            String getcount= (mCurc.moveToFirst())?mCurc.getString(0):"0";
            String gettransactionno= "1";

            if(getcount.equals("0")){
                String sql1 = "select coalesce(refno,0) from tblmaxcode where code=5";
                Cursor mCur = mDb.rawQuery(sql1, null);
                mCur.moveToFirst();
                gettransactionno = (mCur.moveToFirst()) ? mCur.getString(0) : "0";
                if(gettransactionno.equals("0")){
                    gettransactionno ="1";
                }
            }else {
                String sql1 = "select coalesce(max(transactionno),0)+1 from tblexpenses";
                Cursor mCur = mDb.rawQuery(sql1, null);
                mCur.moveToFirst();
                gettransactionno = (mCur.moveToFirst()) ? mCur.getString(0) : "0";
            }
            String sql = "insert into tblexpenses (autonum,transactionno,transactiondate,expensesheadcode," +
                    "amount,remarks,makerid,createdate,schedulecode," +
                    " financialyearcode,vancode,flag,syncstatus) select coalesce(max(autonum),0)+1 ,'" + gettransactionno + "',datetime('" + preferenceMangr.pref_getString("getformatdate") + "')," +
                    "'" + getexpensesheadcode + "','" + Double.parseDouble(getamount) + "'," +
                    " '"+getremarks+"','0',datetime('now', 'localtime'),'" +preferenceMangr.pref_getString("getschedulecode")+ "'" +
                    " ,'"+preferenceMangr.pref_getString("getfinanceyrcode")+"','"+preferenceMangr.pref_getString("getvancode")+"',1 ,0 from tblexpenses";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }


        return "success";

    }
    //Cash Reoprt denomination
    public String  InsertDenomination(String getschedulecode,String getcurrencycode,
                                      String getqty,String getamount) {
        try{
            mDb = mDbHelper.getReadableDatabase();

            String checkSql = "select count(*) from tbldenomination where schedulecode='"+getschedulecode+"' and currencycode="+getcurrencycode;
            Cursor mCur = mDb.rawQuery(checkSql, null);
            mCur.moveToFirst();
            String count = mCur.getString(0);
            String sql="";
            if(Integer.parseInt(count) == 0){
                sql = "insert into tbldenomination (autonum,vancode,schedulecode,currencycode,qty,amount,flag)  " +
                        " select coalesce(max(autonum),0)+1 ,'" + preferenceMangr.pref_getString("getvancode") + "','" + getschedulecode + "'," +
                        "'" + getcurrencycode + "','" + Double.parseDouble(getqty) + "'," +
                        " '"+getamount+"',1 from tbldenomination";
            }
            if(Integer.parseInt(count) > 0){
                sql="update tbldenomination set qty="+ Double.parseDouble(getqty) +", amount = "+ getamount +",flag=1 " +
                        "where schedulecode='" + getschedulecode + "' and currencycode=" + getcurrencycode;
            }

            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }


        return "success";

    }

    //InsertServerSettings
    public String  InsertServerSettings(String getipaddress,String urlname) {
        try{
            String sqlc = "select  count(*) from tblserversettings where  internetip = '"+getipaddress+"' " ;
            Cursor mCurc = mDb.rawQuery(sqlc, null);
            mCurc.moveToFirst();
            String getcount= (mCurc.moveToFirst())?mCurc.getString(0):"0";
            if(getcount.equals("0")){
                String sql = "insert into tblserversettings (serverid,internetip,status,urlname)  " +
                        "  select coalesce(max(serverid),0)+1 ,'"+getipaddress+"','inactive','"+urlname+"' from tblserversettings ";
                mDb.execSQL(sql);
            }else {
                String sql = "Update tblserversettings set internetip = '"+getipaddress+"',urlname='"+urlname+"' ";
                mDb.execSQL(sql);
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return "success";

    }
    //update server
    public String  EditServerSettings(String getipaddress,String getserverid,String urlname) {
        try{
            // String sql = "Update tblserversettings set internetip = '"+getipaddress+"',status = 'inactive',urlname='"+urlname+"' where serverid='"+getserverid+"' ";
            String sql = "Update tblserversettings set internetip = '"+getipaddress+"'" +
                    " ,urlname='"+urlname+"' where serverid='"+getserverid+"' and status = 'inactive' ";
            mDb.execSQL(sql);

        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            return "failure";
        }
        return "success";

    }
    //InsertServerSettings
    public String  UpdateServerSettings(String getstatus,String getserverid) {
        try{
            String sql = "Update tblserversettings set status = '"+getstatus+"' where serverid='"+getserverid+"' ";
            mDb.execSQL(sql);

            String sql1 = "Update tblserversettings set status = 'inactive' where serverid!='"+getserverid+"' ";
            mDb.execSQL(sql1);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        // mDb = mDbHelper.getReadableDatabase();
        // getdate= GenCreatedDate();



        return "success";

    }

    //DeleteServerSettings
    public String  DeleteServerSettings(String getserverid) {
        try{
            String sql = "delete from tblserversettings  where serverid='"+getserverid+"' ";
            mDb.execSQL(sql);

        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return "success";

    }


    //Insert sales lauch start time
    public String  InsertLaunchtime(String getschedulecode) {
        try{
            mDb = mDbHelper.getReadableDatabase();
            getdate= GenCreatedDateTime();

            String transactionno="";
            try{

                String lasttransactionno = "select transactionno from tblsales where date(billdate)=date('"+getdate+"') and schedulecode='"+getschedulecode+"' and vancode='"+preferenceMangr.pref_getString("getvancode")+"' and " +
                        " financialyearcode='"+preferenceMangr.pref_getString("getfinanceyrcode")+"' order by transactionno desc limit 1";
                Cursor mCurc = mDb.rawQuery(lasttransactionno, null);
                if (mCurc.getCount() > 0) {
                    mCurc.moveToFirst();
                }
                transactionno=mCurc.getString(0);
                String sql = "update tblsales set lunchflag=1 where schedulecode='"+getschedulecode+"' and vancode='"+preferenceMangr.pref_getString("getvancode")+"' and " +
                        " financialyearcode='"+preferenceMangr.pref_getString("getfinanceyrcode")+"' and transactionno='"+transactionno+"' and date(billdate)=date('"+getdate+"') ";
                mDb.execSQL(sql);


            }catch (Exception ex){
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
            String sql = "update tblsalesschedule set lunch_start_time = '"+getdate+"' ,flag = 0 where schedulecode='"+getschedulecode+"' ";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return "success";

    }


    //Insert sales lauch start time
    public String  InsertLaunchEndtime(String getschedulecode) {
        try{
            mDb = mDbHelper.getReadableDatabase();
            getdate= GenCreatedDateTime();
            String sql = "update tblsalesschedule set lunch_end_time = '"+getdate+"' ,flag = 0 where schedulecode='"+getschedulecode+"' ";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return "success";

    }
    //Cash  Close
    public String  InsertCashClose(String getschedulecode,String getendingkm,String getpaidparties,String getexpenseentries) {
        try{
            mDb = mDbHelper.getReadableDatabase();
            getdate= GenCreatedDate();
            String sql1 = "update tblsalesschedule set endingkm='"+getendingkm+"' where schedulecode='"+getschedulecode+"' ";
            mDb.execSQL(sql1);

            String sql2 = "delete from tblclosecash where schedulecode='"+getschedulecode+"' ";
            mDb.execSQL(sql2);

            String sql = "insert into tblclosecash (autonum,closedate,vancode,schedulecode,status,flag,paidparties,expenseentries,createddate)  " +
                    " select coalesce(max(autonum),0)+1 ,datetime('"+getdate+"'),'" + preferenceMangr.pref_getString("getvancode") + "','" + getschedulecode + "'," +
                    "1,1,'"+getpaidparties+"','"+getexpenseentries+"',datetime('now', 'localtime') from tblclosecash";
            mDb.execSQL(sql);

        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return "success";

    }

    //Cash  Close
    public String  UpdateCashClose(String getschedulecode) {
        try{
            mDb = mDbHelper.getReadableDatabase();
            getdate= GenCreatedDate();
            String sql1 = "update tblclosecash set status='2' where schedulecode='"+getschedulecode+"' ";
            mDb.execSQL(sql1);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }


        return "success";

    }
    //Cash  Close
    public String  InsertSalesClose(String getschedulecode) {
        try{
            mDb = mDbHelper.getReadableDatabase();
            getdate= GenCreatedDate();
            String sqlc = "select  coalesce(count(*),0) from tblclosesales where schedulecode= '"+getschedulecode+"' " ;
            Cursor mCurc = mDb.rawQuery(sqlc, null);
            mCurc.moveToFirst();
            String getcount= (mCurc.moveToFirst())?mCurc.getString(0):"0";
            if(!getcount.equals("0")){
                String sqldelete = "DELETE from tblclosesales where schedulecode= '"+getschedulecode+"' ";
                mDb.execSQL(sqldelete);
            }
            String sql = "insert into tblclosesales (autonum,closedate,vancode,schedulecode,flag,createddate)  " +
                    " select coalesce(max(autonum),0)+1 ,datetime('"+getdate+"'),'" + preferenceMangr.pref_getString("getvancode") + "','" + getschedulecode + "'," +
                    "1,datetime('now', 'localtime') from tblclosesales";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return "success";
    }

    //Cash  Close
    public String  InsertOrderSalesClose(String getschedulecode) {
        try{
            mDb = mDbHelper.getReadableDatabase();
            getdate= GenCreatedDate();
            String sqlc = "select  coalesce(count(*),0) from tblclosesales where schedulecode= '"+getschedulecode+"' " ;
            Cursor mCurc = mDb.rawQuery(sqlc, null);
            mCurc.moveToFirst();
            String getcount= (mCurc.moveToFirst())?mCurc.getString(0):"0";
            if(!getcount.equals("0")){
                String sqldelete = "DELETE from tblclosesales where schedulecode= '"+getschedulecode+"' ";
                mDb.execSQL(sqldelete);
            }
            String sql = "insert into tblclosesales (autonum,closedate,vancode,schedulecode,flag,createddate)  " +
                    " select coalesce(max(autonum),0)+1 ,datetime('"+getdate+"'),'" + preferenceMangr.pref_getString("getvancode") + "','" + getschedulecode + "'," +
                    "1,datetime('now', 'localtime') from tblclosesales";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return "success";
    }
    //Delete Cash Reoprt denomination
    public String  DeleteDenomination(String getschedulecode) {
        try{
            mDb = mDbHelper.getReadableDatabase();
            String sql = "delete from tbldenomination where schedulecode='" + getschedulecode + "' ";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return "success";
    }
    //Cash Reoprt denomination
    public String  InsertCashReport(String getschedulecode,String getsalescash,String getsalesreturn,
                                    String getadvance,String getreceiptcash,String getexpense,String getcashinhand,
                                    String getdenominationcash) {
        try{
            mDb = mDbHelper.getReadableDatabase();
            String sql1 = "delete from tblcashreport where schedulecode='" + getschedulecode + "' ";
            mDb.execSQL(sql1);
            String sql = "insert into tblcashreport (autonum,schedulecode,vancode,sales,salesreturn,advance,receipt," +
                    " expenses,cash,denominationcash,flag,createddate)  " +
                    " select coalesce(max(autonum),0)+1 ,'" + getschedulecode + "','" + preferenceMangr.pref_getString("getvancode") + "'," +
                    "'" + getsalescash + "','" + getsalesreturn + "'," +
                    " '"+getadvance+"' ,'"+getreceiptcash+"' ,'"+getexpense+"','"+getcashinhand+"'" +
                    ",'"+getdenominationcash+"',1,datetime('now', 'localtime')   from tblcashreport";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }


        return "success";

    }
    //Check customer already exists
    public String DeleteItemInCart(String getitemcode)
    {
        Cursor mCur = null;
        try{
            mDb = mDbHelper.getReadableDatabase();
            String getbusinesstype = "";
//
            String arr[]=preferenceMangr.pref_getString("getbusiness_type").split(",");
            if(arr.length>0) {
                for (int i = 0; i < arr.length; i++) {
                    if(arr[i].equals("2")){
                        getbusinesstype=" (','||c.business_type||',') LIKE '%,2,%' or (','||c.business_type||',') LIKE '%,3,%') ";
                    }else if(arr[i].equals("1")){
                        getbusinesstype=" ((','||c.business_type||',') LIKE '%,1,%' or (','||c.business_type||',') LIKE '%,3,%') ";
                    }else{
                        getbusinesstype=" ((','||c.business_type||',') LIKE '%,1,%' or (','||c.business_type||',') LIKE '%,2,%' or (','||c.business_type||',') LIKE '%,3,%') ";
                    }
                }
            }
            String GenDate= GenCreatedDate();
            String sql="SELECT ((cast(itemweight as INTEGER)/cast(purchaseqty as INTEGER))*cast(freeqty as INTEGER)) as" +
                    "  freeitemqty,(select purchaseitemcode  from tblsalescartdatas  where  freeitemcode =dev.freeitemcode " +
                    "and freeflag='freeitem') as apcitemcode,(select newprice  from tblsalescartdatas  where " +
                    " freeitemcode =dev.freeitemcode and freeflag='freeitem')" +
                    " as price,(SELECT (select coalesce((sum(op)+sum(inward)-sum(outward)),0) from tblstocktransaction " +
                    " where itemcode=dev.freeitemcode and flag!=3 ) + (select coalesce((sum(inward)-sum(outward)),0) " +
                    " from tblstockconversion where itemcode=dev.freeitemcode )) as stockqty,* FROM" +
                    " (SELECT coalesce(sum(itemqty*unitweight),0) as " +
                    "itemweight,group_concat(itemcode,',') as purchaseitemcode,c.purchaseqty,(select " +
                    "(freeqty) from tblscheme as c where  c.schemecode=b.schemecode  and '"+getitemcode+"' in " +
                    "(SELECT purchaseitemcode from tblschemeitemdetails where c.schemecode=schemecode )  ) as freeqty," +
                    " (select (freeitemcode) from tblscheme as c where   c.schemecode=b.schemecode  and '"+getitemcode+"' in " +
                    "(SELECT purchaseitemcode from tblschemeitemdetails where c.schemecode=schemecode ) ) as" +
                    " freeitemcode FROM tblsalescartdatas as a inner join tblschemeitemdetails as b on " +
                    "b.purchaseitemcode=a.itemcode INNER JOIN tblscheme as c on c.schemecode=b.schemecode where " +
                    "schemetype='item' and status='"+statusvar+"' and   "+getbusinesstype+" " +
                    "  and (','||multipleroutecode||',') LIKE '%,"+ preferenceMangr.pref_getString("getroutecode") +",%'" +
                    "  and (validityfrom<=datetime('"+GenDate+"')) and (ifnull(validityto,'')='' or " +
                    "(validityfrom<=datetime('"+GenDate+"')  and" +
                    " validityto>=datetime('"+GenDate+"'))) and c.schemecode in (SELECT distinct schemecode from " +
                    "tblschemeitemdetails where purchaseitemcode='"+getitemcode+"' ) and itemcode<>'"+getitemcode+"') as dev";
//            String sql ="SELECT ((cast(itemweight as INTEGER)/cast(purchaseqty as INTEGER))*cast(freeqty as INTEGER)) as " +
//                    " freeitemqty,(select purchaseitemcode  from tblsalescartdatas  where " +
//                    " freeitemcode =dev.freeitemcode and freeflag='freeitem') as apcitemcode," +
//                    " (select newprice  from tblsalescartdatas  where " +
//                    " freeitemcode =dev.freeitemcode and freeflag='freeitem') as price," +
//                    "(SELECT (select coalesce((sum(op)+sum(inward)-sum(outward)),0) from tblstocktransaction " +
//                    " where itemcode=dev.freeitemcode and flag!=3 ) + (select coalesce((sum(inward)-sum(outward)),0) " +
//                    " from tblstockconversion where itemcode=dev.freeitemcode )) as stockqty," +
//                    "* from (SELECT (SELECT  coalesce(sum(itemqty*unitweight),0) as itemweight" +
//                    " from (SELECT c.* from tblsalescartdatas as c inner join tblschemeitemdetails as b on " +
//                    " c.itemcode=b.purchaseitemcode inner join tblscheme as a on b.schemecode=a.schemecode where " +
//                    " schemetype='item' and  a.status='"+statusvar+"' and  "+getbusinesstype+" " +
//                    " and (','||multipleroutecode||',') LIKE '%,"+ preferenceMangr.pref_getString("getroutecode") +",%' " +
//                    "and (validityfrom<=datetime('"+GenDate+"')) " +
//                    " and (ifnull(validityto,'')='' or   (validityfrom<=datetime('"+GenDate+"') and " +
//                    " validityto>=datetime('"+GenDate+"'))) and   a.schemecode=(SELECT distinct" +
//                    " schemecode from tblschemeitemdetails where purchaseitemcode='"+getitemcode+"') " +
//                    " and itemcode<>'"+getitemcode+"') ) as itemweight," +
//                    " (SELECT group_concat(itemcode,',') " +
//                    " from (SELECT itemcode from tblsalescartdatas as c inner join tblschemeitemdetails as b on " +
//                    " c.itemcode=b.purchaseitemcode inner join tblscheme as a on b.schemecode=a.schemecode where " +
//                    " schemetype='item' and  a.status='"+statusvar+"' and  "+getbusinesstype+"  " +
//                    " and (','||multipleroutecode||',') LIKE '%,"+ preferenceMangr.pref_getString("getroutecode") +",%' " +
//                    "and (validityfrom<=datetime('"+GenDate+"')) and (ifnull(validityto,'')='' or " +
//                    "  (validityfrom<=datetime('"+GenDate+"') and  validityto>=datetime('"+GenDate+"'))) and " +
//                    "  a.schemecode=(SELECT distinct schemecode from tblschemeitemdetails where" +
//                    " purchaseitemcode='"+getitemcode+"')  and itemcode<>'"+getitemcode+"') ) " +
//                    " as purchaseitemcode,(select (purchaseqty) from tblscheme as a where a.schemetype='item' and a.status='"+statusvar+"' and" +
//                    "  "+getbusinesstype+" and (','||multipleroutecode||',') LIKE '%,"+ preferenceMangr.pref_getString("getroutecode") +",%' " +
//                    " and (validityfrom<=datetime('"+GenDate+"')) and (ifnull(validityto,'')='' or (validityfrom<=datetime('"+GenDate+"') " +
//                    " and validityto>=datetime('"+GenDate+"')))  ) as purchaseqty,(select (freeqty) from tblscheme as a where " +
//                    " a.schemetype='item' and a.status='"+statusvar+"' and "+getbusinesstype+" and (','||multipleroutecode||',') LIKE " +
//                    "'%,"+ preferenceMangr.pref_getString("getroutecode") +",%' and (validityfrom<=datetime('"+GenDate+"'))" +
//                    " and (ifnull(validityto,'')='' or (validityfrom<=datetime('"+GenDate+"') and "+
//                    " validityto>=datetime('"+GenDate+"'))) " +
//                    " and '"+getitemcode+"' in (SELECT purchaseitemcode from tblschemeitemdetails where a.schemecode=schemecode )  ) as freeqty," +
//                    "(select (freeitemcode) from tblscheme as a where " +
//                    " a.schemetype='item' and a.status='"+statusvar+"' and "+getbusinesstype+" and (','||multipleroutecode||',') LIKE " +
//                    "'%,"+ preferenceMangr.pref_getString("getroutecode") +",%' and (validityfrom<=datetime('"+GenDate+"'))" +
//                    " and (ifnull(validityto,'')='' or (validityfrom<=datetime('"+GenDate+"') and "+
//                    " validityto>=datetime('"+GenDate+"'))) and '"+getitemcode+"' in (SELECT purchaseitemcode " +
//                    "from tblschemeitemdetails where a.schemecode=schemecode ) ) as freeitemcode from tblsalescartdatas" +
//                    " where itemcode <> '"+getitemcode+"' AND freeflag='') as dev ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                String getfreecount = (mCur.moveToFirst()) ? mCur.getString(0) : "0";
                String getapcitemcode = (mCur.moveToFirst()) ? mCur.getString(1) : "0";
                String getfreeitemprice = (mCur.moveToFirst()) ? mCur.getString(2) : "0";
                String getstockqty = (mCur.moveToFirst()) ? mCur.getString(3) : "0";
                String getpurchaseitemcode = (mCur.moveToFirst()) ? mCur.getString(5) : "0";
                String getfreeitemcode = (mCur.moveToFirst()) ? mCur.getString(8) : "0";
                if(getapcitemcode!=null && getapcitemcode!="null"){
                    String apcitemcodearr= getapcitemcode.replace(",", "', '") ;
                    if(getfreecount.equals("0") ) {
//                        String sql1= "delete from tblsalescartdatas   where" +
//                                " '"+getitemcode+"' in  ('"+apcitemcodearr+"')  and  freeflag='freeitem' ";
//                        mDb.execSQL(sql1);
                        String sql1= "delete from tblsalescartdatas   where" +
                                " (','||purchaseitemcode||',') LIKE '%,"+getitemcode+",%' and  freeflag='freeitem' ";
                        mDb.execSQL(sql1);
                    }else{
                        double getsubtotal = Double.parseDouble(getfreeitemprice) * Double.parseDouble(getfreecount);
                        String sql1= "Update tblsalescartdatas set  discount='"+getsubtotal+"'," +
                                " subtotal='"+getsubtotal+"', purchaseitemcode='"+getpurchaseitemcode+"'," +
                                " itemqty='"+getfreecount+"',stockqty='"+getstockqty+"' where " +
                                " '"+getitemcode+"' in  ('"+apcitemcodearr+"') and  itemcode='"+getfreeitemcode+"'" +
                                " and freeflag='freeitem' ";
                        mDb.execSQL(sql1);
                    }
                }else{
                    String sql1= "delete from tblsalescartdatas   where" +
                            " '"+getitemcode+"' in (purchaseitemcode)  and  freeflag='freeitem' ";
                    mDb.execSQL(sql1);

                }
                String sql2 = "delete from tblsalescartdatas  where" +
                        " itemcode='"+getitemcode+"' and  freeflag='' ";
                mDb.execSQL(sql2);

            } else{
                String sql2 = "delete from tblsalescartdatas  where" +
                        " itemcode='"+getitemcode+"' and  freeflag='' ";
                mDb.execSQL(sql2);

                String sql1= "delete from tblsalescartdatas   where" +
                        " (','||purchaseitemcode||',') LIKE '%,"+getitemcode+",%' and  freeflag='freeitem' ";
                mDb.execSQL(sql1);
            }



            String sql3 = "delete from tblsalescartdatas  where" +
                    " itemcode='"+getitemcode+"' and  freeflag='freerate' ";
            mDb.execSQL(sql3);
//            String sql4 = "delete from tblstockconversion  where" +
//                    " itemcode='"+getitemcode+"'";
//            mDb.execSQL(sql4);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return "Success";
    }
    // delete stock conversion table
    public String DeleteItemInStockConversion(String getitemcode,String getparentitemcode)
    {
        Cursor mCur = null;
        try{
            mDb = mDbHelper.getReadableDatabase();
            if(getparentitemcode.equals("0")){
                String sql = "SELECT itemcode from tblitemmaster where parentitemcode='"+getitemcode+"'";
                mCur = mDb.rawQuery(sql, null);
                if (mCur.getCount() > 0) {
                    mCur.moveToFirst();
                    String getchildcode = (mCur.moveToFirst()) ? mCur.getString(0) : "0";
                    String childsql = "delete from tblstockconversion  where" +
                            " itemcode='"+getchildcode+"'";
                    mDb.execSQL(childsql);
                }
            }
            String sql1 = "delete from tblstockconversion  where" +
                    " itemcode='"+getitemcode+"'";
            mDb.execSQL(sql1);
            String sql2 = "delete from tblstockconversion  where" +
                    " itemcode='"+getparentitemcode+"'";
            mDb.execSQL(sql2);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return "Success";
    }

    //Check customer already exists
    public String DeleteOrderItemInCart(String getitemcode)
    {
        try{
            mDb = mDbHelper.getReadableDatabase();
            String sql2 = "delete from tblsalesordercartdatas  where" +
                    " itemcode='"+getitemcode+"' and  freeflag='' ";
            mDb.execSQL(sql2);

            String sql1= "delete from tblsalesordercartdatas   where" +
                    " itemcode='"+getitemcode+"' and  freeflag='freeitem' ";
            mDb.execSQL(sql1);

            String sql3= "delete from tblsalesordercartdatas   where" +
                    " itemcode='"+getitemcode+"' and  freeflag='freerate' ";
            mDb.execSQL(sql3);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return "Success";
    }
    //Check customer already exists
    public String DeleteItemOrderInCart(String getitemcode)
    {
        try{
            mDb = mDbHelper.getReadableDatabase();
            String sql2 = "delete from tblsalesordercartdatas  where" +
                    " itemcode='"+getitemcode+"' and  freeflag='' ";
            mDb.execSQL(sql2);

            String sql1= "delete from tblsalesordercartdatas   where" +
                    " purchaseitemcode='"+getitemcode+"' and  freeflag='freeitem' ";
            mDb.execSQL(sql1);

            String sql3 = "delete from tblsalesordercartdatas  where" +
                    " itemcode='"+getitemcode+"' and  freeflag='freerate' ";
            mDb.execSQL(sql3);

        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return "Success";
    }


    //Check customer already exists
    public String CheckCustomerAlreadyExistsCount(String getareacode,String getcustomername)
    {
        String returnval="";
        try{
            mDb = mDbHelper.getReadableDatabase();
            String sql1 = "select coalesce(count(*),0) from tblcustomer where " +
                    "lower(customername) =lower('"+getcustomername.replaceAll("'","''")+"')  ";
            Cursor mCur = mDb.rawQuery(sql1, null);
            mCur.moveToFirst();
            if(mCur.getString(0).equals("0")){
                returnval=  "Success";
            }
            else
            {
                returnval= "Exists";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return returnval;
    }
    //Insert Customer Details
    public String  InsertCustomerDetails(String getcustomername,String getcustomernametamil,String getaddress,
                                         String getareacode,String getemailid,String getmobileno,String gettelephoneno,
                                         String getaadharno,String getgstin,String getcustomertypecode,String getbusinesstype,
                                         String getwhatsappno,Integer mobilenoverificationstatus,String latitude,String longtitude) {
        String generatecustomercode="";
        try{
            mDb = mDbHelper.getReadableDatabase();
            String getmaxcustomercode= "1";
            String GetDate= GenCreatedDate();
            String sqlc = "select  count(*) from tblcustomer " ;
            Cursor mCurc = mDb.rawQuery(sqlc, null);
            mCurc.moveToFirst();
            String getcount= (mCurc.moveToFirst())?mCurc.getString(0):"0";

            if(getcount.equals("0")){
                String sql1 = "select coalesce(refno,0) from tblmaxcode where code=6";
                Cursor mCur = mDb.rawQuery(sql1, null);
                mCur.moveToFirst();
                getmaxcustomercode = (mCur.moveToFirst()) ? mCur.getString(0) : "0";
                if(getmaxcustomercode.equals("0")){
                    getmaxcustomercode ="1";
                }
            }else {
                String sql1 = "select coalesce(max(refno),0)+1 from tblcustomer where refno <>'null' ";
                Cursor mCur = mDb.rawQuery(sql1, null);
                mCur.moveToFirst();
                getmaxcustomercode = (mCur.moveToFirst()) ? mCur.getString(0) : "0";
            }
//            String cus=MenuActivity.getschedulecode;


            String sql2 = "select substr('0000000'||'"+preferenceMangr.pref_getString("getschedulecode")+"', -7, 7) || '_' || 'VC' || substr('00'||'"+preferenceMangr.pref_getString("getvancode")+"', -2, 2) || substr('00000'||'"+getmaxcustomercode+"', -5, 5)  ";
            Cursor mCur1 = mDb.rawQuery(sql2, null);
            mCur1.moveToFirst();
            generatecustomercode= (mCur1.moveToFirst())?mCur1.getString(0):"0";

            /*String businesstype = "1";
            if(LoginActivity.getbusiness_type.equals("2")){
                businesstype = "2";
            }else if(LoginActivity.getbusiness_type.equals("1")){
                businesstype = "1";
            }else{
                businesstype = "3";
            }*/
            String sql = "insert into tblcustomer (autonum,customercode,customername,customernametamil," +
                    "address,areacode,emailid,mobileno,telephoneno,aadharno,gstin," +
                    " status,makerid,createddate,updateddate,latitude,longitude,flag,schemeapplicable," +
                    "uploaddocument,refno,business_type,customertypecode,whatsappno," +
                    "mobilenoverificationstatus) " +
                    " select coalesce(max(autonum),0)+1 ,'" + generatecustomercode + "',('" + getcustomername.replaceAll("'","''") + "')," +
                    "('" + getcustomernametamil.replaceAll("'","''") + "'),'" + getaddress + "'," +
                    " '"+getareacode+"','"+getemailid+"','" + getmobileno + "','" +gettelephoneno+ "'" +
                    " ,'"+getaadharno+"','"+getgstin+"','Active',0,datetime('now', 'localtime')," +
                    "datetime('now', 'localtime'),'" + latitude + "','" + longtitude + "',1,'no','','"+getmaxcustomercode+"','"+getbusinesstype+"'," +
                    "'"+getcustomertypecode+"','"+getwhatsappno+"'," +
                    "'"+mobilenoverificationstatus+"' from tblcustomer";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }


        return generatecustomercode;

    }
    //Update Customer Details
    public String UpdateCustomerDetails(String getcustomername,String getcustomernametamil,String getaddress,
                                        String getareacode,String getemailid,String getmobileno,String gettelephoneno,
                                        String getaadharno,String getgstin,String getcustomercode,
                                        String getcustomertype,String getbusinesstype,
                                        String getwhatsappno,Integer mobilenoverificationstatus){
        try{
            String sql1 = "UPDATE 'tblcustomer' SET " +
                    " emailid='" +  getemailid +"'," +
                    "mobileno='" + getmobileno +"',telephoneno='" + gettelephoneno +"'" +
                    ",aadharno='" + getaadharno +"',gstin='" + getgstin +"',flag=1," +
                    " customertypecode='" + getcustomertype +"',business_type='" + getbusinesstype +"'," +
                    " whatsappno='" + getwhatsappno +"',mobilenoverificationstatus='" + mobilenoverificationstatus +"' " +
                    " WHERE customercode='" + getcustomercode +"' ";
            mDb.execSQL(sql1);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return  "success";
    }
    //Update expense flag
    public String  UpdateExpenseFlag(String gettranasactionno) {
        try{
            mDb = mDbHelper.getReadableDatabase();
            String sql = "update tblexpenses set flag=3,syncstatus=0 where transactionno = '"+gettranasactionno+"' ";
            mDb.execSQL(sql);

        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return "success";

    }

    //Update Receipt flag
    public String  UpdateReceiptFlag(String gettranasactionno) {
        try{
            mDb = mDbHelper.getReadableDatabase();
            String sql = "update tblreceipt set flag=3,syncstatus=0 where transactionno = '"+gettranasactionno+"'" +
                    "and vancode='"+preferenceMangr.pref_getString("getvancode")+"' " +
                    "and financialyearcode='"+preferenceMangr.pref_getString("getfinanceyrcode")+"' AND type='Receipt' ";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - UpdateReceiptFlag", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }


        return "success";

    }
    /*************************************END INSERT UPDATE FUNCTIONALITY******************************/


    /*******************REPORT QUERYS***********************/
    public Cursor GetVanStockCompany(String companycode,String itemtype,String itemsubgroupcode)
    {
        Cursor mCur =null;
        try{
            String getcompanycode="";
            if(companycode.equals("0")){
                getcompanycode = "1=1";
            }else{
                getcompanycode = "b.companycode='"+companycode+"'";
            }


            String getitemsubgroupcode="";
            if(itemsubgroupcode.equals("0")){
                getitemsubgroupcode = "1=1";
            }else{
                getitemsubgroupcode = "d.itemsubgroupcode='"+itemsubgroupcode+"'";
            }

            String getitemtype="";
            if(itemtype.equals("All Items")){
                getitemtype = "1=1";
            }else{
                if(itemtype.equals("Parent")){
                    itemtype = "parent";
                }else{
                    itemtype = "child";
                }
                getitemtype = "b.itemcategory='"+itemtype+"' ";
            }

            String sql="";
            getdate = GenCreatedDate();
            sql = " select b.companycode,f.companyname, f.companynametamil, f.shortname, f.street, f.area, f.mobileno, " +
                    " f.gstin, f.city, f.telephone, f.panno, f.pincode  " +
                    " from " +
                    " tblstocktransaction as a inner join tblitemmaster as b on " +
                    " a.itemcode=b.itemcode inner join tblunitmaster as c on b.unitcode=c.unitcode " +
                    " inner join tblitemsubgroupmaster as d on  d.itemsubgroupcode=b.itemsubgroupcode " +
                    " inner join tblbrandmaster as e on b.brandcode=e.brandcode  inner join tblcompanymaster as f on f.companycode=b.companycode " +
                    " where "+getcompanycode+" and  "+getitemtype+" and "+getitemsubgroupcode+" and a.flag!=3  "  +
                    " group by b.companycode order by b.companycode desc ";
            //parentcode,itemorder,b.itemname

            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }


        return mCur;
    }
    public Cursor GetVanStock(String companycode,String itemtype,String itemsubgroupcode)
    {
        Cursor mCur =null;
        try{
            String getcompanycode="";
            if(companycode.equals("0")){
                getcompanycode = "1=1";
            }else{
                getcompanycode = "b.companycode='"+companycode+"'";
            }


            String getitemsubgroupcode="";
            if(itemsubgroupcode.equals("0")){
                getitemsubgroupcode = "1=1";
            }else{
                getitemsubgroupcode = "d.itemsubgroupcode='"+itemsubgroupcode+"'";
            }

            String getitemtype="";
            if(itemtype.equals("All Items")){
                getitemtype = "1=1";
            }else{
                if(itemtype.equals("Parent")){
                    itemtype = "parent";
                }else{
                    itemtype = "child";
                }
                getitemtype = "b.itemcategory='"+itemtype+"' ";
            }

            String sql="";
            getdate = GenCreatedDate();
            sql = " select  * from  (select b.itemnametamil,c.unitname,coalesce((select Sum(op)+Sum(inward)-sum(outward)" +
                    " from tblstocktransaction where itemcode=a.itemcode " +
                    " and datetime(transactiondate)<datetime('" + getdate + "') and flag!=3),0) as op," +
                    " coalesce((select Sum(inward) from tblstocktransaction where itemcode=a.itemcode " +
                    " and datetime(transactiondate)=datetime('" + getdate + "')  and flag!=3 ),0) as inward, " +
                    " coalesce((select Sum(outward) from tblstocktransaction where itemcode=a.itemcode " +
                    " and datetime(transactiondate)=datetime('" + getdate + "') and type!='sales' and  type!='salescancel'  and flag!=3  ),0) as outward," +
                    " coalesce((select sum(op)+sum(inward)-sum(outward) from tblstocktransaction" +
                    " where itemcode=a.itemcode " +
                    " and datetime(transactiondate)<=datetime('" + getdate + "')  and flag!=3 ),0) as closing,c.noofdecimals," +
                    " case when parentitemcode=0 then b.itemcode else b.parentitemcode " +
                    " end as parentcode,case when itemcategory='parent' then 1 else  2 end as itemorder," +
                    " d.itemsubgroupname,e.brandname," +
                    " CASE WHEN itemtype=2 then (SELECT freeitemcolor from tblgeneralsettings) else " +
                    "coalesce((Select colourcode from tblcompanymaster where companycode=b.companycode),'#000000') END as colourcode," +
                    " coalesce((select Sum(outward) from tblstocktransaction where itemcode=a.itemcode " +
                    " and datetime(transactiondate)=datetime('" + getdate + "') and (type='sales' or type='salescancel') and flag!=3 ),0) as sales " +
                    " from " +
                    " tblstocktransaction as a inner join tblitemmaster as b on " +
                    " a.itemcode=b.itemcode inner join tblunitmaster as c on b.unitcode=c.unitcode " +
                    " inner join tblitemsubgroupmaster as d on  d.itemsubgroupcode=b.itemsubgroupcode " +
                    " inner join tblbrandmaster as e on b.brandcode=e.brandcode " +
                    " where "+getcompanycode+" and "+getitemtype+" and "+getitemsubgroupcode+" and a.flag!=3  "  +
                    " group by b.itemname,c.unitname" +
                    " order by b.itemtype,d.itemgroupcode,d.itemsubgroupcode,e.brandname,b.itemcategory desc,itemnametamil ) as dev where dev.op!=0 " +
                    " or dev.inward!=0 or dev.outward!=0 or dev.sales!=0";
            //parentcode,itemorder,b.itemname

            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }


    public Cursor Getcompanydetails(String companycode,String fromdate,String todate)
    {

        Cursor mCur =null;
        try{
            String sql,getcompany;

            if(companycode.equals("0") || companycode.equals("")) {
                getcompany = "1=1";
            }else{
                getcompany="d.companycode in ('"+companycode+"') ";
            }


            sql="select distinct a.companycode, a.companyname, a.companynametamil, a.shortname, a.street, a.area, a.mobileno," +
                    "  a.gstin, a.city, a.telephone, a.panno, a.pincode,d.schedulecode from tblcompanymaster as a inner join " +
                    " tblsales as d on d.companycode=a.companycode where datetime(d.billdate) = datetime('"+fromdate+"') "  +
                    "    and d.flag!=3 and d.flag!=6" +
                    "  and a.status='"+statusvar+"' and "+ getcompany+" order by a.companycode asc";

            mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    public Cursor Getsalesreturncompanydetails(String companycode,String fromdate,String todate)
    {
        Cursor mCur =null;
        try{
            String sql,getcompany;

            if(companycode.equals("0") || companycode.equals("")) {
                getcompany = "1=1";
            }else{
                getcompany="d.companycode in ('"+companycode+"') ";
            }


            sql="select distinct a.companycode, a.companyname, a.companynametamil, a.shortname, a.street, a.area, a.mobileno," +
                    "  a.gstin, a.city, a.telephone, a.panno, a.pincode,d.schedulecode from tblcompanymaster as a inner join " +
                    " tblsalesreturn as d on d.companycode=a.companycode where datetime(d.billdate) = datetime('"+fromdate+"') "  +
                    "  and d.flag!=3 and d.flag!=6" +
                    "  and a.status='"+statusvar+"' and "+ getcompany+" order by a.companycode asc ";

            mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    public Cursor Getsalesordercompanydetails(String fromdate,String todate)
    {
        Cursor mCur =null;
        try{
            String sql,getcompany;

          /*  if(companycode.equals("0") || companycode.equals("")) {
                getcompany = "1=1";
            }else{
                getcompany="d.companycode in ('"+companycode+"') ";
            }*/


            // /*sql="select distinct a.companycode, a.companyname, a.companynametamil, a.shortname, a.street, a.area, a.mobileno," +
            //      "  a.gstin, a.city, a.telephone, a.panno, a.pincode,c.schedulecode from tblcompanymaster as a inner join " +
            //     " tblsalesreturn as d on d.companycode=a.companycode  inner join tblsalesorder as c on c.transactionno = d.transactionno" +
            //     " where datetime(c.billdate) = datetime('"+fromdate+"') "  +
            //     "  and c.flag!=3 and c.flag!=6" +
            //   "  and a.status='"+statusvar+"'   order by a.companycode asc ";*/
            sql="select distinct a.companycode, a.companyname, a.companynametamil, a.shortname, a.street, a.area, a.mobileno," +
                    "a.gstin, a.city, a.telephone, a.panno, a.pincode,c.schedulecode from tblcompanymaster as a inner join " +
                    "tblsalesorder as c on a.companycode=c.companycode "  +
                    "where datetime(c.billdate) = datetime('"+fromdate+"') "+
                    "and c.flag!=3 and c.flag!=6 " +
                    "and a.status='"+statusvar+"' order by a.companycode asc ";

            mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    public Cursor GetSalesItemWiseReport_Date(String fromdate,String getcompanycode)
    {
        Cursor mCur =null;
        try{
            String sql,getcompany;

            if(getcompanycode.equals("0") || getcompanycode.equals("")) {
                getcompany = "1=1";
            }else{
                getcompany="d.companycode='"+getcompanycode+"'";
            }

            sql="SELECT a.itemcode,COALESCE(itemnametamil,itemname) as itemname,COALESCE(sum(qty),0) " +
                    " as quantity,u.unitname,cast(sum(COALESCE(amount,0)) as decimal(32,2)) " +
                    "as totalamt, case when parentitemcode=0 then i.itemcode else i.parentitemcode " +
                    "  end as parentcode,case when itemcategory='parent' then 1 else  2 end as itemorder, " +
                    "  e.itemsubgroupname,g.brandname," +
                    " CASE WHEN itemtype=2 then (SELECT freeitemcolor from tblgeneralsettings) " +
                    "else coalesce((Select colourcode from tblcompanymaster where companycode=a.companycode),'#000000')  END as colourcode" +
                    " FROM tblsalesitemdetails as a inner join tblitemmaster " +
                    " as i on i.itemcode=a.itemcode  inner join tblunitmaster as u " +
                    " on u.unitcode=i.unitcode inner join tblsales as d on d.transactionno=a.transactionno" +
                    " and d.companycode=a.companycode inner join tblitemsubgroupmaster as e on  " +
                    " e.itemsubgroupcode=i.itemsubgroupcode inner join tblbrandmaster as g on g.brandcode=i.brandcode  " +
                    " where "+ getcompany +" and  datetime(d.billdate) = datetime('"+fromdate+"') and a.freeitemstatus<>'freeitem' " +
                    "  and d.flag!=3 and d.flag!=6 group by a.itemcode  " +
                    " order by  itemtype,e.itemgroupcode,e.itemsubgroupcode,g.brandname,i.itemcategory desc";
            //group by a.itemcode ,itemname,itemnametamil,u.unitname
            //,parentcode,itemorder,i.itemname
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    public Cursor GetSalesReturnItemWiseReport_Date(String fromdate,String getcompanycode)
    {
        Cursor mCur =null;
        try{
            String sql,getcompany;

            if(getcompanycode.equals("0") || getcompanycode.equals("")) {
                getcompany = "1=1";
            }else{
                getcompany="d.companycode='"+getcompanycode+"'";
            }

            sql="SELECT a.itemcode,COALESCE(itemnametamil,itemname) as itemname,COALESCE(sum(qty),0) as quantity," +
                    " u.unitname,cast(sum(COALESCE(amount,0)) as decimal(32,2)) " +
                    "as totalamt , case when parentitemcode=0 then i.itemcode else i.parentitemcode " +
                    "  end as parentcode,case when itemcategory='parent' then 1 else  2 end as itemorder, " +
                    "  e.itemsubgroupname,g.brandname," +
                    " CASE WHEN itemtype=2 then (SELECT freeitemcolor from tblgeneralsettings)" +
                    " else coalesce((Select colourcode from tblcompanymaster where companycode=a.companycode),'#000000') END as colourcode" +
                    " FROM tblsalesreturnitemdetails as a inner join " +
                    " tblitemmaster as i on i.itemcode=a.itemcode  inner join tblunitmaster as " +
                    " u on u.unitcode=i.unitcode inner join tblsalesreturn as d on d.transactionno=a.transactionno " +
                    " and d.companycode=a.companycode inner join tblitemsubgroupmaster as e on " +
                    " e.itemsubgroupcode=i.itemsubgroupcode inner join tblbrandmaster as g on g.brandcode=i.brandcode  " +
                    " where "+ getcompany +" and  datetime(d.billdate) = datetime('"+fromdate+"')" +
                    "  and d.flag!=3 and d.flag!=6 group by a.itemcode " +
                    " order by  itemtype,e.itemgroupcode,e.itemsubgroupcode,g.brandname,i.itemcategory desc";

            //parentcode,itemorder,i.itemname

            //group by a.itemcode,itemname,itemnametamil,u.unitname
            mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    public Cursor GetSalesOrderItemWiseReport_Date(String fromdate)
    {
        Cursor mCur =null;
        try{
            String sql;


            sql="SELECT a.itemcode,COALESCE(itemnametamil,itemname) as itemname,COALESCE(sum(qty),0) as quantity," +
                    " u.unitname,cast(sum(COALESCE(amount,0)) as decimal(32,2)) " +
                    "as totalamt , case when parentitemcode=0 then i.itemcode else i.parentitemcode " +
                    "  end as parentcode,case when itemcategory='parent' then 1 else  2 end as itemorder, " +
                    "  e.itemsubgroupname,g.brandname," +
                    " CASE WHEN itemtype=2 then (SELECT freeitemcolor from tblgeneralsettings)" +
                    " else coalesce((Select colourcode from tblcompanymaster where companycode=a.companycode),'#000000') END as colourcode" +
                    " FROM tblsalesorderitemdetails as a inner join " +
                    " tblitemmaster as i on i.itemcode=a.itemcode  inner join tblunitmaster as " +
                    " u on u.unitcode=i.unitcode inner join tblsalesorder as d on d.transactionno=a.transactionno " +
                    " inner join tblitemsubgroupmaster as e on " +
                    " e.itemsubgroupcode=i.itemsubgroupcode inner join tblbrandmaster as g on g.brandcode=i.brandcode  " +
                    " where    datetime(d.billdate) = datetime('"+fromdate+"') and a.freeitemstatus<>'freeitem' " +
                    "  and d.flag!=3 and d.flag!=6 and d.status<>2 group by a.itemcode " +
                    " order by  itemtype,e.itemgroupcode,e.itemsubgroupcode,g.brandname,i.itemcategory desc";

            //parentcode,itemorder,i.itemname

            //group by a.itemcode,itemname,itemnametamil,u.unitname
            mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }


    public Cursor GetSalesOrderItemCompanyWiseReport_Date(String fromdate,String getcompanycode)
    {
        Cursor mCur =null;
        try{
            String sql,getcompany;


            if(getcompanycode.equals("0") || getcompanycode.equals("")) {
                getcompany = "1=1";
            }else{
                getcompany="a.companycode='"+getcompanycode+"'";
            }
            sql="SELECT a.itemcode,COALESCE(itemnametamil,itemname) as itemname,COALESCE(sum(qty),0) as quantity," +
                    " u.unitname,cast(sum(COALESCE(amount,0)) as decimal(32,2)) " +
                    "as totalamt , case when parentitemcode=0 then i.itemcode else i.parentitemcode " +
                    "  end as parentcode,case when itemcategory='parent' then 1 else  2 end as itemorder, " +
                    "  e.itemsubgroupname,g.brandname," +
                    "  CASE WHEN itemtype=2 then (SELECT freeitemcolor from tblgeneralsettings) " +
                    " else coalesce((Select colourcode from tblcompanymaster where companycode=a.companycode),'#000000') END as colourcode" +
                    " FROM tblsalesorderitemdetails as a inner join " +
                    " tblitemmaster as i on i.itemcode=a.itemcode  inner join tblunitmaster as " +
                    " u on u.unitcode=i.unitcode inner join tblsalesorder as d on d.transactionno=a.transactionno " +
                    " inner join tblitemsubgroupmaster as e on " +
                    " e.itemsubgroupcode=i.itemsubgroupcode inner join tblbrandmaster as g on g.brandcode=i.brandcode  " +
                    " where  "+ getcompany +" and   datetime(d.billdate) = datetime('"+fromdate+"')" +
                    "  and d.flag!=3 and d.flag!=6 and d.status<>2 group by a.itemcode " +
                    " order by  itemtype,e.itemgroupcode,e.itemsubgroupcode,g.brandname,i.itemcategory desc";

            //parentcode,itemorder,i.itemname

            //group by a.itemcode,itemname,itemnametamil,u.unitname
            mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }
    /*******************END REPORT QUERYS***********************/
    //Backup DB
    public String udfnBackupdb(Context context)
    {
        String filename="";
        try{
            preferenceMangr = new PreferenceMangr(context);
            filename=mDbHelper.udfnBackupdb(context,preferenceMangr);

        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return filename;
    }



    /*******************SYNCH DETAILES*****************************/

    //Sync van master to sqllite table
    //column name
    //"autonum"
    //"vancode"
    //"vanname"
    //"imeino"
    //"pin"
    //"createddate"
    //"updateddate"
    //"makerid"
    //"status"
    public void syncvanmaster (JSONObject object)
    {
//        try{
//
//        }catch (Exception ex){
//            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
//        }
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    String sql1="DELETE FROM 'tblvanmaster'";
                    mDb.execSQL(sql1);
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);
                            // if (obj.getString("process").equals("Insert")) {
                            String sql = "SELECT * FROM 'tblvanmaster' WHERE vancode=" + obj.getInt("vancode");
                            if (!((mDb.rawQuery(sql, null)).moveToFirst())) {
                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                sql = "INSERT INTO 'tblvanmaster' VALUES(" + Integer.toString(gc)
                                        + ",'" + obj.getInt("vancode") + "','" + obj.getString("vanname").replaceAll("'","''") + "','"+obj.getString("imeino")+"'," +
                                        " '" + obj.getString("pin") + "','" + obj.getString("createddate") + "' ,'" + obj.getString("updateddate") + "' ," +
                                        " '" + obj.getString("makerid") + "','" + obj.getString("status") + "','" + obj.getString("business_type") + "','" + obj.getString("orderprint") + "')";
                                mDb.execSQL(sql);
                            }

                            //}
                            /*else {
                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                String sql1 = "UPDATE 'tblvanmaster' SET autonum='" +
                                        gc + "', vanname='" + obj.getString("vanname") + "'," +
                                        "imeino='" + obj.getString("imeino") + "',pin='" + obj.getString("pin") + "' ," +
                                        "createddate='" + obj.getString("createddate") + "' ," +
                                        "updateddate='" + obj.getString("updateddate") + "', " +
                                        "makerid='" + obj.getString("makerid") + "' ," +
                                        "status='" + obj.getString("status") + "' " +
                                        " WHERE vancode=" + obj.getInt("vancode");
                                mDb.execSQL(sql1);
                            }*/
                        } catch (JSONException ex) {
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }

    public void deletevanmaster ()
    {
        try{
            String sql1="DELETE FROM 'tblvanmaster'";
            mDb.execSQL(sql1);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

    }


    //Sync barnd master to sqlite
    //column name
//    "autonum"
//    "brandcode"
//    "brandname"
//    "brandnametamil"
//    "status"
//    "createddate"
//    "updateddate"
//    "makerid"
    public void syncbrandmaster (JSONObject object)
    {

        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);
                            if (obj.getString("process").equals("Insert")) {
                                String sql = "SELECT * FROM 'tblbrandmaster' WHERE brandcode=" + obj.getInt("brandcode");
                                if (!((mDb.rawQuery(sql, null)).moveToFirst())) {
                                    int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                    sql = "INSERT INTO 'tblbrandmaster' VALUES(" + Integer.toString(gc)
                                            + ",'" + obj.getInt("brandcode") + "','" + obj.getString("brandname").replaceAll("'","''") + "','"+obj.getString("brandnametamil").replaceAll("'","''")+"', " +
                                            " '" + obj.getString("status") + "','" + obj.getString("createddate") + "' ,'" + obj.getString("updateddate") + "' ," +
                                            " '" + obj.getString("makerid") + "')";
                                    mDb.execSQL(sql);
                                }
                                else {
                                    int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                    String sql1 = "UPDATE 'tblbrandmaster' SET autonum='" +
                                            gc + "', brandname='" + obj.getString("brandname").replaceAll("'","''") + "'," +
                                            "brandnametamil='" + obj.getString("brandnametamil").replaceAll("'","''") + "',status='" + obj.getString("status") + "' ," +
                                            "createddate='" + obj.getString("createddate") + "' ," +
                                            "updateddate='" + obj.getString("updateddate") + "', " +
                                            "makerid='" + obj.getString("makerid") + "'  " +
                                            " WHERE brandcode=" + obj.getInt("brandcode");
                                    mDb.execSQL(sql1);
                                }

                            }
                            else {
                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                String sql1 = "UPDATE 'tblbrandmaster' SET autonum='" +
                                        gc + "', brandname='" + obj.getString("brandname").replaceAll("'","''") + "'," +
                                        "brandnametamil='" + obj.getString("brandnametamil").replaceAll("'","''") + "',status='" + obj.getString("status") + "' ," +
                                        "createddate='" + obj.getString("createddate") + "' ," +
                                        "updateddate='" + obj.getString("updateddate") + "', " +
                                        "makerid='" + obj.getString("makerid") + "'  " +
                                        " WHERE brandcode=" + obj.getInt("brandcode");
                                mDb.execSQL(sql1);
                            }
                        } catch (JSONException ex) {
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }

    //Sync area mater to sqlite
    //column name
//        "autonum"
//        "citycode"
//        "areacode"
//        "areaname"
//        "areanametamil"
//        "noofkm"
//        "status"
//        "makerid"
//        "createddate"
//        "updateddate"

    public void syncareamaster (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {

                            JSONObject obj = (JSONObject) json_category.get(i);
                            if (obj.getString("process").equals("Insert")) {
                                String sql = "SELECT * FROM 'tblareamaster' WHERE areacode=" + obj.getInt("areacode");
                                if (!((mDb.rawQuery(sql, null)).moveToFirst())) {
                                    int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                    sql = "INSERT INTO 'tblareamaster' VALUES(" + Integer.toString(gc)
                                            + ",'" + obj.getString("citycode") + "','" + obj.getString("areacode") + "'," +
                                            " '" + obj.getString("areaname").replaceAll("'","''") + "'," +
                                            " '"+obj.getString("areanametamil").replaceAll("'","''")+"'," +
                                            " '"+obj.getString("noofkm")+"', '" + obj.getString("status") + "'," +
                                            " '" + obj.getString("makerid") + "' ,'" + obj.getString("createddate") + "' ," +
                                            " '" + obj.getString("updateddate") + "', '" + obj.getString("pincode") + "' ," +
                                            " '" +obj.getString("mobilenoverify")+ "')";
                                    mDb.execSQL(sql);
                                }else {
                                    int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                    String sql1 = "UPDATE 'tblareamaster' SET autonum='" +
                                            gc + "', citycode='" + obj.getString("citycode") + "'," +
                                            "areaname='" + obj.getString("areaname").replaceAll("'","''") + "'," +
                                            " areanametamil='" + obj.getString("areanametamil").replaceAll("'","''") + "'," +
                                            "noofkm='" + obj.getString("noofkm") + "',status='" + obj.getString("status") + "' ," +
                                            "createddate='" + obj.getString("createddate") + "' ," +
                                            "updateddate='" + obj.getString("updateddate") + "', " +
                                            "makerid='" + obj.getString("makerid") + "'," +
                                            "mobilenoverify='" + obj.getString("mobilenoverify") + "'," +
                                            "pincode='" + obj.getString("pincode") + "' " +
                                            " WHERE areacode=" + obj.getInt("areacode");
                                    mDb.execSQL(sql1);
                                }

                            }
                            else {
                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                String sql1 = "UPDATE 'tblareamaster' SET autonum='" +
                                        gc + "', citycode='" + obj.getString("citycode") + "'," +
                                        "areaname='" + obj.getString("areaname").replaceAll("'","''") + "'," +
                                        " areanametamil='" + obj.getString("areanametamil").replaceAll("'","''") + "'," +
                                        "noofkm='" + obj.getString("noofkm") + "',status='" + obj.getString("status") + "' ," +
                                        "createddate='" + obj.getString("createddate") + "' ," +
                                        "updateddate='" + obj.getString("updateddate") + "', " +
                                        "makerid='" + obj.getString("makerid") + "', " +
                                        "mobilenoverify='" + obj.getString("mobilenoverify") + "'," +
                                        "pincode='" + obj.getString("pincode") + "' " +
                                        " WHERE areacode=" + obj.getInt("areacode");
                                mDb.execSQL(sql1);
                            }
                        } catch (Exception ex) {
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }

    public void syncstatemaster (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {

                            JSONObject obj = (JSONObject) json_category.get(i);

                            String sql = "SELECT * FROM 'tblstatemaster' WHERE statecode=" + obj.getInt("statecode");
                            if (!((mDb.rawQuery(sql, null)).moveToFirst())) {
                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                sql = "INSERT INTO 'tblstatemaster' VALUES(" + Integer.toString(gc) + ",'" + obj.getString("statecode") + "', " +
                                        " '" + obj.getString("statename").replaceAll("'","''") + "'," +
                                        " '"+obj.getString("statenametamil").replaceAll("'","''")+"'," +
                                        " '"+obj.getString("defaultstate")+"', '" + obj.getString("gststatecode") + "')";
                                mDb.execSQL(sql);
                            }else {
                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                String sql1 = "UPDATE 'tblstatemaster' SET autonum='" + gc + "', statecode='" + obj.getString("statecode") + "'," +
                                        " statename='" + obj.getString("statename").replaceAll("'","''") + "'," +
                                        " statenametamil='" + obj.getString("statenametamil").replaceAll("'","''") + "'," +
                                        " defaultstate='" + obj.getString("defaultstate") + "',gststatecode='" + obj.getString("gststatecode") + "' " +
                                        " WHERE statecode=" + obj.getInt("statecode");
                                mDb.execSQL(sql1);
                            }

                        } catch (Exception ex) {
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - syncstatemaster", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - syncstatemaster", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }

    public void syncdefsalescategory (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            String deleteSql = "DELETE FROM 'def_sales_category'";
            mDb.execSQL(deleteSql);
            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {

                            JSONObject obj = (JSONObject) json_category.get(i);


                            String sql = "INSERT INTO def_sales_category(sacid,sac_name,sac_code) VALUES('"+obj.getString("sacid")+"','" + obj.getString("sac_name") + "', " + " '"+obj.getString("sac_code")+"' )";
                                mDb.execSQL(sql);


                        } catch (Exception ex) {
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - syncdefsalescategory", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - syncdefsalescategory", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }


    public void syncupivendermaster (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    String deleteSql = "DELETE FROM 'tblupivendermaster'";
                    mDb.execSQL(deleteSql);
                    json_category = object.getJSONArray("Value");
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {

                            JSONObject obj = (JSONObject) json_category.get(i);

                            String sql = "SELECT * FROM 'tblupivendermaster' WHERE venderid=" + obj.getInt("venderid");
                            if (!((mDb.rawQuery(sql, null)).moveToFirst())) {
                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                sql = "INSERT INTO 'tblupivendermaster' VALUES(" + Integer.toString(gc) + ",'" + obj.getString("venderid") + "', " +
                                        " '" + obj.getString("vendername").replaceAll("'","''") + "'," +
                                        " '"+obj.getString("statusid")+"'," +
                                        "'"+obj.getString("status").replaceAll("'","''")+"')";
                                mDb.execSQL(sql);
                            }else {
                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                String sql1 = "UPDATE 'tblupivendermaster' SET autonum='" + gc + "', venderid='" + obj.getString("venderid") + "'," +
                                        " vendername='" + obj.getString("vendername").replaceAll("'","''") + "'," +
                                        " status='" + obj.getString("status").replaceAll("'","''") + "'," +
                                        " statusid='" + obj.getString("statusid") + "'" +
                                        " WHERE venderid=" + obj.getInt("venderid");
                                mDb.execSQL(sql1);
                            }

                        } catch (Exception ex) {
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - syncupivendermaster", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - syncupivendermaster", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }

    //sync currency to sqlite
    //column name

//    "autonum"
//    "currencycode"
//    "currency"

    public void synccurrency (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    String sql1="DELETE FROM 'tblcurrency'";
                    mDb.execSQL(sql1);
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);

                            int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                            String sql = "INSERT INTO 'tblcurrency' VALUES(" + Integer.toString(gc)
                                    + ",'" + obj.getString("currencycode") + "','" + obj.getString("currency") + "')";
                            mDb.execSQL(sql);

                        } catch (JSONException ex) {
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }

    //Sync Remarks
    public void syncreceiptremarks (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    String sql1="DELETE FROM 'tblreceiptremarks'";
                    mDb.execSQL(sql1);
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);

                            int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                            String sql = "INSERT INTO 'tblreceiptremarks' VALUES(" + Integer.toString(gc)
                                    + ",'" + obj.getString("receiptremarks").replaceAll("'","''") + "','" + obj.getString("receiptremarkscode") + "')";
                            mDb.execSQL(sql);

                        } catch (JSONException ex) {
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }

    //Sync Remarks
    public void synctax (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    String sql1="DELETE FROM 'tbltax'";
                    mDb.execSQL(sql1);
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);

                            int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                            String sql = "INSERT INTO 'tbltax' VALUES(" + Integer.toString(gc)
                                    + ",'" + obj.getString("tax") + "','" + obj.getString("status") + "'," +
                                    "'" + obj.getString("makerid") + "','" + obj.getString("createddate") + "'," +
                                    "'" + obj.getString("updateddate") + "')";
                            mDb.execSQL(sql);


                        } catch (JSONException ex) {
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }

    //Sync Remarks
    public void syncbilltype (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    String sql1="DELETE FROM 'tblbilltype'";
                    mDb.execSQL(sql1);
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);

                            int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                            String sql = "INSERT INTO 'tblbilltype' VALUES(" + Integer.toString(gc)
                                    + ",'" + obj.getString("billtype").replaceAll("'","''") + "','" + obj.getString("billtypecode") + "')";
                            mDb.execSQL(sql);

                        } catch (JSONException ex) {
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }


    //Sync employeemaster to sqlite
    //column name
//    "autonum"
//    "employeecategorycode"
//    "employeecode"
//    "employeename"
//    "employeenametamil"
//    "mobileno"
//    "emailid"
//    "aadharno"
//    "documentupload"
//    "status"
//    "createddate"
//    "updateddate"
//    "makeid"
    public void syncemployeemaster (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);
                            if (obj.getString("process").equals("Insert")) {
                                String sql = "SELECT * FROM 'tblemployeemaster' WHERE employeecode=" + obj.getInt("employeecode");
                                if (!((mDb.rawQuery(sql, null)).moveToFirst())) {
                                    int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                    sql = "INSERT INTO 'tblemployeemaster' VALUES(" + Integer.toString(gc)
                                            + ",'" + obj.getString("employeecategorycode") + "','" + obj.getString("employeecode") + "','" + obj.getString("employeename").replaceAll("'","''") + "','"+obj.getString("employeenametamil").replaceAll("'","''")+"','"+obj.getString("mobileno")+"', " +
                                            " '" + obj.getString("emailid") + "','" + obj.getString("aadharno") + "','" + obj.getString("documentupload") + "','" + obj.getString("status") + "','" + obj.getString("createddate") + "' ," +
                                            " '" + obj.getString("updateddate") + "','" + obj.getString("makerid") + "' )";
                                    mDb.execSQL(sql);
                                }

                            }
                            else {
                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                String sql1 = "UPDATE 'tblemployeemaster' SET autonum='" +
                                        gc + "', employeecategorycode='" + obj.getString("employeecategorycode") + "'," +
                                        "employeename='" + obj.getString("employeename").replaceAll("'","''") + "',employeenametamil='" + obj.getString("employeenametamil").replaceAll("'","''") + "'," +
                                        "mobileno='" + obj.getString("mobileno") + "',emailid='" + obj.getString("emailid") + "',aadharno='" + obj.getString("aadharno") + "'" +
                                        ",documentupload='" + obj.getString("documentupload") + "',status='" + obj.getString("status") + "' ," +
                                        "createddate='" + obj.getString("createddate") + "' ," +
                                        "updateddate='" + obj.getString("updateddate") + "', " +
                                        "makerid='" + obj.getString("makerid") + "'  " +
                                        " WHERE employeecode=" + obj.getInt("employeecode");
                                mDb.execSQL(sql1);
                            }
                        } catch (JSONException ex) {
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }


    //sync employee category to sqlite
    //column name
//    "autonum"
//    "employeecategorycode"
//    "employeecategoryname"

    public void syncemployeecategory (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    String sql1="DELETE FROM 'tblemployeecategory'";
                    mDb.execSQL(sql1);
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);

                            int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                            String sql = "INSERT INTO 'tblemployeecategory' VALUES(" + Integer.toString(gc)
                                    + ",'" + obj.getString("employeecategorycode") + "'," +
                                    " '" + obj.getString("employeecategoryname").replaceAll("'","''") + "')";
                            mDb.execSQL(sql);

                        } catch (JSONException ex) {
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }

    //Sync Customer to sqlite
    //column name
//        "autonum"
//        "customercode"
//        "customername"
//        "customernametamil"
//        "address"
//        "areacode"
//        "emailid"
//        "mobileno"
//        "telephoneno"
//        "aadharno"
//        "gstin"
//        "status"
//        "makerid"
//        "createddate"
//        "updateddate"
//        "latitude"
//        "longitude","flag","schemeapplicable","uploaddocument"


    public void synccustomer (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;
            Cursor cursor = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);
                            if (obj.getString("process").equals("Insert")) {
                                String sql = "SELECT * FROM 'tblcustomer' WHERE customercode='" + obj.getString("customercode")+"' ";
                                cursor = mDb.rawQuery(sql, null);
                                if (!cursor.moveToFirst()) {
                                    int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                    sql = "INSERT INTO 'tblcustomer' (autonum,customercode,refno, customername,customernametamil,address,areacode,emailid,mobileno,telephoneno," +
                                            "aadharno,gstin,status,makerid,createddate,updateddate,latitude,longitude,flag,schemeapplicable,uploaddocument,gstinverificationstatus," +
                                            "customertypecode,business_type,whatsappno,mobilenoverificationstatus,categorycode,erpitemcode)" +
                                            " VALUES('" + gc +"','" + obj.getString("customercode")+"'," +
                                            "'" + obj.getString("refno")+"'," +
                                            "'" + obj.getString("customername").replaceAll("'","''")+"'," +
                                            " '" + obj.getString("customernametamil").replaceAll("'","''")+"'," +
                                            "'" + obj.getString("address").replaceAll("'","''")+"','" + obj.getString("areacode")+"'," +
                                            "'" + obj.getString("emailid")+"','" + obj.getString("mobileno")+"'," +
                                            "'" + obj.getString("telephoneno")+"','" + obj.getString("aadharno")+"'," +
                                            "'" + obj.getString("gstin")+"','" + obj.getString("status")+"'," +
                                            "'" + obj.getString("makerid")+"','" + obj.getString("createddate")+"'," +
                                            "'" + obj.getString("updateddate")+"','" + obj.getString("latitude")+"'," +
                                            "'" + obj.getString("longitude")+"','2'," +
                                            "'" + obj.getString("schemeapplicable")+"','" + obj.getString("uploaddocument")+"'," +
                                            "'" + obj.getString("gstinverificationstatus")+"','" + obj.getString("customertypecode")+"'," +
                                            " '"+obj.getString("business_type")+"','"+obj.getString("whatsappno")+"'," +
                                            "'"+obj.getString("mobilenoverificationstatus")+"','"+obj.getString("categorycode")+"','"+obj.getString("erpitemcode")+"')";
                                    mDb.execSQL(sql);
                                }
                                else {
                                    int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                    String sql1 = "UPDATE 'tblcustomer' SET autonum='" + gc +"'," +
                                            "customercode='" + obj.getString("customercode")+"'," +
                                            "refno='" + obj.getString("refno")+"'," +
                                            "customername='" + obj.getString("customername").replaceAll("'","''")+"'," +
                                            "customernametamil='" + obj.getString("customernametamil").replaceAll("'","''")+"'," +
                                            "address='" + obj.getString("address").replaceAll("'","''")+"'," +
                                            "areacode='" + obj.getString("areacode")+"',emailid='" + obj.getString("emailid")+"'," +
                                            "mobileno='" + obj.getString("mobileno")+"',telephoneno='" + obj.getString("telephoneno")+"'" +
                                            ",aadharno='" + obj.getString("aadharno")+"',gstin='" + obj.getString("gstin")+"'," +
                                            "status='" + obj.getString("status")+"',makerid='" + obj.getString("makerid")+"'," +
                                            "createddate='" + obj.getString("createddate")+"',updateddate='" + obj.getString("updateddate")+"'," +
                                            "latitude='" + obj.getString("latitude")+"',longitude='" + obj.getString("longitude")+"'," +
                                            "schemeapplicable='" + obj.getString("schemeapplicable")+"'," +
                                            "uploaddocument='" + obj.getString("uploaddocument")+"'," +
                                            "customertypecode='"+obj.getString("customertypecode")+"' ," +
                                            "business_type='"+obj.getString("business_type")+"',  " +
                                            " gstinverificationstatus =  '" + obj.getString("gstinverificationstatus")+"', " +
                                            " mobilenoverificationstatus =  '" + obj.getString("mobilenoverificationstatus")+"', " +
                                            " whatsappno =  '" + obj.getString("whatsappno")+"', " +
                                            "categorycode = '"+obj.getString("categorycode")+"',"+
                                            "erpitemcode = '"+obj.getString("erpitemcode")+"'"+
                                            " WHERE customercode='" + obj.getString("customercode")+"' ";
                                    mDb.execSQL(sql1);

                                }
                            }
                            else {
                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                String sql1 = "UPDATE 'tblcustomer' SET autonum='" + gc +"'," +
                                        "customercode='" + obj.getString("customercode")+"'," +
                                        "refno='" + obj.getString("refno")+"'," +
                                        "customername='" + obj.getString("customername").replaceAll("'","''")+"'," +
                                        "customernametamil='" + obj.getString("customernametamil").replaceAll("'","''")+"'," +
                                        "address='" + obj.getString("address").replaceAll("'","''")+"'," +
                                        "areacode='" + obj.getString("areacode")+"',emailid='" + obj.getString("emailid")+"'," +
                                        "mobileno='" + obj.getString("mobileno")+"',telephoneno='" + obj.getString("telephoneno")+"'" +
                                        ",aadharno='" + obj.getString("aadharno")+"',gstin='" + obj.getString("gstin")+"'," +
                                        "status='" + obj.getString("status")+"',makerid='" + obj.getString("makerid")+"'," +
                                        "createddate='" + obj.getString("createddate")+"',updateddate='" + obj.getString("updateddate")+"'," +
                                        "latitude='" + obj.getString("latitude")+"',longitude='" + obj.getString("longitude")+"'," +
                                        "schemeapplicable='" + obj.getString("schemeapplicable")+"'," +
                                        "uploaddocument='" + obj.getString("uploaddocument")+"' ," +
                                        " gstinverificationstatus =  '" + obj.getString("gstinverificationstatus")+"'," +
                                        " customertypecode='"+obj.getString("customertypecode")+"', " +
                                        " business_type='"+obj.getString("business_type")+"'  " +
                                        " gstinverificationstatus =  '" + obj.getString("gstinverificationstatus")+"', " +
                                        " mobilenoverificationstatus =  '" + obj.getString("mobilenoverificationstatus")+"', " +
                                        " whatsappno =  '" + obj.getString("whatsappno")+"', " +
                                        "categorycode = '"+obj.getString("categorycode")+"',"+
                                        "erpitemcode = '"+obj.getString("erpitemcode")+"'"+
                                        " WHERE customercode='" + obj.getString("customercode")+"' ";
                                         mDb.execSQL(sql1);
                            }
                        } catch (JSONException ex) {
                            if(cursor != null)
                                cursor.close();
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        } finally {
                            if(cursor != null)
                                cursor.close();
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                if(cursor != null)
                    cursor.close();
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            } finally {
                if(cursor != null)
                    cursor.close();
            }
        }
    }


    //Sync company master to sqlite
    //column name
//    "autonum","companycode"
//    "companyname"
//    "shortname"
//    "street"
//    "emailid"
//    "fssaino"
//    "area"
//    "mobileno"
//    "gstin"
//    "city"
//    "telephone"
//    "panno"
//    "pincode"
//    "statecode"
//    "website"
//    "gstcertificateupload"
//    "colourcode"
//    "status"
//    "makerid"
//    "createddate"
//    "updateddate"

    public void synccompanymaster (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");

                    String deletesql="delete from tblcompanymaster";
                    mDb.execSQL(deletesql);

                    for(int i=0;i<json_category.length();i++)
                    {
                        try {

                            JSONObject obj = (JSONObject) json_category.get(i);
                            if (obj.getString("process").equals("Insert")) {


                                String sql = "SELECT * FROM 'tblcompanymaster' WHERE companycode=" + obj.getInt("companycode");
                                if (!((mDb.rawQuery(sql, null)).moveToFirst())) {
                                    int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                    sql = "INSERT INTO 'tblcompanymaster' VALUES('" + gc +"', " +
                                            "'" + obj.getString("companycode")+"'," +
                                            "'" + obj.getString("companyname").replaceAll("'","''")+"','" + obj.getString("companynametamil").replaceAll("'","''")+"','" + obj.getString("shortname").replaceAll("'","''")+"'," +
                                            "'" + obj.getString("street").replaceAll("'","''")+"','" + obj.getString("emailid")+"'," +
                                            "'" + obj.getString("fssaino")+"','" + obj.getString("area")+"'," +
                                            "'" + obj.getString("mobileno")+"','" + obj.getString("gstin")+"'," +
                                            "'" + obj.getString("city")+"','" + obj.getString("telephone")+"'," +
                                            "'" + obj.getString("panno")+"','" + obj.getString("pincode")+"','" + obj.getString("statecode")+"'," +
                                            "'" + obj.getString("website")+"','" + obj.getString("gstcertificateupload")+"'," +
                                            "'" + obj.getString("colourcode")+"','" + obj.getString("status")+"'," +
                                            "'" + obj.getString("makerid")+"','" + obj.getString("createddate")+"'," +
                                            "'" + obj.getString("updateddate")+"','" + obj.getString("gststatecode") + "'," +
                                            "'" + obj.getString("companytype")+"')";
                                    mDb.execSQL(sql);
                                } else {
                                    int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                    String sql1 = "UPDATE 'tblcompanymaster' SET autonum='" + gc +"',companycode='" + obj.getString("companycode")+"'," +
                                            "companyname='" + obj.getString("companyname").replaceAll("'","''")+"',companynametamil='" + obj.getString("companynametamil").replaceAll("'","''")+"',shortname='" + obj.getString("shortname").replaceAll("'","''")+"'," +
                                            "street='" + obj.getString("street").replaceAll("'","''")+"',emailid='" + obj.getString("emailid")+"'," +
                                            "fssaino='" + obj.getString("fssaino")+"',area='" + obj.getString("area")+"'," +
                                            "mobileno='" + obj.getString("mobileno")+"',gstin='" + obj.getString("gstin")+"'," +
                                            "city='" + obj.getString("city")+"',telephone='" + obj.getString("telephone")+"'," +
                                            "panno='" + obj.getString("panno")+"',pincode='" + obj.getString("pincode")+"'," +
                                            "statecode='" + obj.getString("statecode")+"',website='" + obj.getString("website")+"'," +
                                            "gstcertificateupload='" + obj.getString("gstcertificateupload")+"'," +
                                            "colourcode='" + obj.getString("colourcode")+"',status='" + obj.getString("status")+"'," +
                                            "makerid='" + obj.getString("makerid")+"',createddate='" + obj.getString("createddate")+"'," +
                                            "updateddate='" + obj.getString("updateddate")+"',gststatecode='" + obj.getString("gststatecode") + "', " +
                                            "companytype='"+ obj.getString("companytype") +"' " +
                                            " WHERE companycode=" + obj.getInt("companycode");
                                    mDb.execSQL(sql1);
                                }

                            }
                            else {
                                String sql = "SELECT * FROM 'tblcompanymaster' WHERE companycode=" + obj.getInt("companycode");
                                if (!((mDb.rawQuery(sql, null)).moveToFirst())) {
                                    int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                    String sql1 = "UPDATE 'tblcompanymaster' SET autonum='" + gc +"',companycode='" + obj.getString("companycode")+"'," +
                                            "companyname='" + obj.getString("companyname").replaceAll("'","''")+"',companynametamil='" + obj.getString("companynametamil").replaceAll("'","''")+"',shortname='" + obj.getString("shortname").replaceAll("'","''")+"'," +
                                            "street='" + obj.getString("street").replaceAll("'","''")+"',emailid='" + obj.getString("emailid")+"'," +
                                            "fssaino='" + obj.getString("fssaino")+"',area='" + obj.getString("area")+"'," +
                                            "mobileno='" + obj.getString("mobileno")+"',gstin='" + obj.getString("gstin")+"'," +
                                            "city='" + obj.getString("city")+"',telephone='" + obj.getString("telephone")+"'," +
                                            "panno='" + obj.getString("panno")+"',pincode='" + obj.getString("pincode")+"'," +
                                            "statecode='" + obj.getString("statecode")+"',website='" + obj.getString("website")+"'," +
                                            "gstcertificateupload='" + obj.getString("gstcertificateupload")+"'," +
                                            "colourcode='" + obj.getString("colourcode")+"',status='" + obj.getString("status")+"'," +
                                            "makerid='" + obj.getString("makerid")+"',createddate='" + obj.getString("createddate")+"'," +
                                            "updateddate='" + obj.getString("updateddate")+"',gststatecode=" + obj.getString("gststatecode") + "' , " +
                                            "companytype='"+ obj.getString("companytype") +"' " +
                                            " WHERE companycode=" + obj.getInt("companycode");
                                    mDb.execSQL(sql1);
                                }
                            }
                        } catch (JSONException ex) {
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }

    public void synccompanyvenderdetails (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;


            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    String deletesql="DELETE FROM tblcompanyvenderdetails";
                    mDb.execSQL(deletesql);


                    for(int i=0;i<json_category.length();i++)
                    {
                        try {

                            JSONObject obj = (JSONObject) json_category.get(i);
                            if (obj.getString("process").equals("Insert")) {
                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                String localfilepath = "";
                                String  sql = "INSERT INTO 'tblcompanyvenderdetails' VALUES('" + gc +"', " +
                                        "'" + obj.getString("seqno")+"'," +
                                        "'" + obj.getString("companycode")+"'," +
                                        "'" + obj.getString("venderid")+"'," +
                                        "'" + obj.getString("status").replaceAll("'","''")+"'," +
                                        "'" + obj.getString("makerid")+"'," +
                                        "'" + obj.getString("upiimageurl")+"'," +
                                        "'" + localfilepath +"'," +
                                        "'" + obj.getString("downloadURL")+"')";
                                mDb.execSQL(sql);
                            }

                        } catch (JSONException ex) {
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "synccompanyvenderdetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "synccompanyvenderdetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }

    public Cursor GetCompanyVenderDetails()
    {
        Cursor mCur = null;
        try{
            String sql ="SELECT companycode,venderid,downloadpath FROM tblcompanyvenderdetails WHERE status =  'Active' AND  localfilepath=''";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //sync city master to sqlite
    //column name
//        "autonum"
//        "citycode"
//        "cityname"
//        "citynametamil"
//        "status"
//        "makerid"
//        "createddate"
//        "updateddate"
    public void synccitymaster (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);
                            if (obj.getString("process").equals("Insert")) {
                                String sql = "SELECT * FROM 'tblcitymaster' WHERE citycode=" + obj.getInt("citycode");
                                if (!((mDb.rawQuery(sql, null)).moveToFirst())) {
                                    int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                    sql = "INSERT INTO 'tblcitymaster' VALUES('" + gc +"','" + obj.getString("citycode")+"'," +
                                            "'" + obj.getString("cityname").replaceAll("'","''")+"','" + obj.getString("citynametamil").replaceAll("'","''")+"'," +
                                            "'" + obj.getString("status")+"','" + obj.getString("makerid")+"'," +
                                            "'" + obj.getString("createddate")+"','" + obj.getString("updateddate")+"'," +
                                            "'" + obj.getString("statecode") + "' )";
                                    mDb.execSQL(sql);
                                } else {
                                    int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                    String sql1 = "UPDATE 'tblcitymaster' SET autonum='" + gc +"'," +
                                            "citycode='" + obj.getString("citycode")+"',cityname='" + obj.getString("cityname").replaceAll("'","''")+"'," +
                                            "citynametamil='" + obj.getString("citynametamil").replaceAll("'","''")+"',status='" + obj.getString("status")+"'," +
                                            "makerid='" + obj.getString("makerid")+"',createddate='" + obj.getString("createddate")+"'," +
                                            "updateddate='" + obj.getString("updateddate")+"'," +
                                            "statecode='" + obj.getString("statecode") + "'  " +
                                            " WHERE citycode=" + obj.getInt("citycode");
                                    mDb.execSQL(sql1);
                                }

                            }
                            else {
                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                String sql1 = "UPDATE 'tblcitymaster' SET autonum='" + gc +"'," +
                                        "citycode='" + obj.getString("citycode")+"',cityname='" + obj.getString("cityname").replaceAll("'","''")+"'," +
                                        "citynametamil='" + obj.getString("citynametamil").replaceAll("'","''")+"',status='" + obj.getString("status")+"'," +
                                        "makerid='" + obj.getString("makerid")+"',createddate='" + obj.getString("createddate")+"'," +
                                        "updateddate='" + obj.getString("updateddate")+"' , " +
                                        "statecode='" + obj.getString("statecode") + "'  " +
                                        " WHERE citycode=" + obj.getInt("citycode");
                                mDb.execSQL(sql1);
                            }
                        } catch (JSONException ex) {
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }

    // sync expenses head to sqlite
    //column name

    //        "autonum"
//        "expensesheadcode"
//        "expensesgroupcode"
//        "expenseshead"
//        "expensesheadtamil"
//        "status"
//        "makerid"
//        "createddate"
//        "updateddate"
    public void syncexpenseshead (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);
                            if (obj.getString("process").equals("Insert")) {
                                String sql = "SELECT * FROM 'tblexpenseshead' WHERE expensesheadcode=" + obj.getInt("expensesheadcode");
                                if (!((mDb.rawQuery(sql, null)).moveToFirst())) {
                                    int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                    sql = "INSERT INTO 'tblexpenseshead' VALUES('" + gc +"', " +
                                            "'" + obj.getString("expensesheadcode")+"','" + obj.getString("expensesgroupcode")+"'," +
                                            "'" + obj.getString("expenseshead").replaceAll("'","''")+"','" + obj.getString("expensesheadtamil").replaceAll("'","''")+"'," +
                                            "'" + obj.getString("status")+"','" + obj.getString("makerid")+"'," +
                                            "'" + obj.getString("createddate")+"','" + obj.getString("updateddate")+"')";
                                    mDb.execSQL(sql);
                                }

                            }
                            else {
                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                String sql1 = "UPDATE 'tblexpenseshead' SET autonum='" + gc +"',expensesheadcode='" + obj.getString("expensesheadcode")+"'," +
                                        "expensesgroupcode='" + obj.getString("expensesgroupcode")+"'," +
                                        "expenseshead='" + obj.getString("expenseshead").replaceAll("'","''")+"'," +
                                        "expensesheadtamil='" + obj.getString("expensesheadtamil").replaceAll("'","''")+"',status='" + obj.getString("status")+"'," +
                                        "makerid='" + obj.getString("makerid")+"',createddate='" + obj.getString("createddate")+"'," +
                                        "updateddate='" + obj.getString("updateddate")+"' " +
                                        " WHERE expensesheadcode=" + obj.getString("expensesheadcode");
                                mDb.execSQL(sql1);
                            }
                        } catch (JSONException ex) {
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }


    //sync financial year
    // column name
//    "autonum"
//    "financialyearcode"
//    "fromdate"
//    "todate"
//    "financialyear"
//    "makerid"
//    "createddate"
//    "updateddate"

    public void syncfinancialyear (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);
                            if (obj.getString("process").equals("Insert")) {
                                String sql = "SELECT * FROM 'tblfinancialyear' WHERE financialyearcode=" + obj.getString("financialyearcode");
                                if (!((mDb.rawQuery(sql, null)).moveToFirst())) {
                                    int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                    sql = "INSERT INTO 'tblfinancialyear' VALUES('" + gc +"','" + obj.getString("financialyearcode")+"'," +
                                            "'" + obj.getString("fromdate")+"','" + obj.getString("todate")+"'," +
                                            "'" + obj.getString("financialyear")+"','" + obj.getString("makerid")+"'," +
                                            "'" + obj.getString("createddate")+"','" + obj.getString("updateddate")+"')";
                                    mDb.execSQL(sql);
                                } else {
                                    int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                    String sql1 = "UPDATE 'tblfinancialyear' SET autonum='" + gc +"'," +
                                            "financialyearcode='" + obj.getString("financialyearcode")+"'," +
                                            "fromdate='" + obj.getString("fromdate")+"',todate='" + obj.getString("todate")+"'," +
                                            "financialyear='" + obj.getString("financialyear")+"',makerid='" + obj.getString("makerid")+"'," +
                                            "createddate='" + obj.getString("createddate")+"',updateddate='" + obj.getString("updateddate")+"' " +
                                            " WHERE financialyearcode=" + obj.getString("financialyearcode");
                                    mDb.execSQL(sql1);
                                }

                            }
                            else {
                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                String sql1 = "UPDATE 'tblfinancialyear' SET autonum='" + gc +"'," +
                                        "financialyearcode='" + obj.getString("financialyearcode")+"'," +
                                        "fromdate='" + obj.getString("fromdate")+"',todate='" + obj.getString("todate")+"'," +
                                        "financialyear='" + obj.getString("financialyear")+"',makerid='" + obj.getString("makerid")+"'," +
                                        "createddate='" + obj.getString("createddate")+"',updateddate='" + obj.getString("updateddate")+"' " +
                                        " WHERE financialyearcode=" + obj.getString("financialyearcode");
                                mDb.execSQL(sql1);
                            }
                        } catch (JSONException ex) {
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }


    //sync general settings
    //column namne
//        "autonum"
//        "restrictmobileappdays"
//        "allowedithsn"
//        "allowedittax"
//        "enablebillwisediscount"
//        "enablegpstracking"
//        "internetip"
//        "intranetip"
//        "enableallcustomersmobileapp"
//        "salesschedulemobileapp"
//        "billcopypopup"
//        "drilldownitem"
    public void syncgeneralsettings (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");

                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);

                            String sqlc = "select  count(*) from tblgeneralsettings " ;
                            Cursor mCurc = mDb.rawQuery(sqlc, null);
                            mCurc.moveToFirst();
                            String getcount= (mCurc.moveToFirst())?mCurc.getString(0):"0";
                            if(getcount.equals("0")){
                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                String sql = "INSERT INTO 'tblgeneralsettings' (autonum,restrictmobileappdays,allowedithsn,allowedittax,enablebillwisediscount,enablegpstracking" +
                                        " ,enableallcustomersmobileapp,salesschedulemobileapp,billcopypopup,drilldownitem,wishmsg,drilldownorder,jurisdiction,printheader," +
                                        " showcashpaidpopup,freeitemcolor,item_disc_efrom,zro_price_item_disc,cash_item_disc,otp_time_validity," +
                                        "otp_time_validity_backend,orderautoapproval,maxbillannualamount,maxbillamount) VALUES('" + gc +"'," +
                                        "'" + obj.getString("restrictmobileappdays")+"','" + obj.getString("allowedithsn")+"'," +
                                        "'" + obj.getString("allowedittax")+"','" + obj.getString("enablebillwisediscount")+"'," +
                                        "'" + obj.getString("enablegpstracking")+"','" + obj.getString("enableallcustomersmobileapp")+"'," +
                                        "'" + obj.getString("salesschedulemobileapp")+"','" + obj.getString("billcopypopup")+"'," +
                                        "'" + obj.getString("drilldownitem")+"','"+obj.getString("wishmsg")+"'," +
                                        "'"+obj.getString("drilldownorder")+"','"+obj.getString("jurisdiction")+"'," +
                                        "'"+obj.getString("printheader")+"'," +
                                        "'"+obj.getString("showcashpaidpopup")+"'," +
                                        "'"+obj.getString("freeitemcolor")+"' ," +
                                        "'"+obj.getString("item_disc_efrom")+"' ," +
                                        "'"+obj.getString("zro_price_item_disc")+"' ," +
                                        "'"+obj.getString("cash_item_disc")+"' ," +
                                        "'"+obj.getString("otp_time_validity")+"'," +
                                        "'"+obj.getString("otp_time_validity_backend")+"',"+
                                        "'"+obj.getString("orderautoapproval")+"','"+obj.getString("maxbillannualamount")+"','"+obj.getString("maxbillamount")+"' ) ";
                                mDb.execSQL(sql);
                            }else{
                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                String sql = "UPDATE 'tblgeneralsettings' set autonum='"+gc+"'," +
                                        " restrictmobileappdays='" + obj.getString("restrictmobileappdays")+"'," +
                                        " allowedithsn='"+obj.getString("allowedithsn")+"'," +
                                        " allowedittax='"+ obj.getString("allowedittax")+"'," +
                                        " enablebillwisediscount='"+obj.getString("enablebillwisediscount")+"'," +
                                        " enablegpstracking='"+obj.getString("enablegpstracking")+"', " +
                                        " enableallcustomersmobileapp='"+ obj.getString("enableallcustomersmobileapp")+"'," +
                                        " salesschedulemobileapp='"+obj.getString("salesschedulemobileapp")+"'," +
                                        " billcopypopup='"+obj.getString("billcopypopup")+"'," +
                                        " wishmsg='"+obj.getString("wishmsg")+"'," +
                                        " drilldownitem='"+obj.getString("drilldownitem")+"', " +
                                        "drilldownorder='"+obj.getString("drilldownorder")+"',jurisdiction='"+ obj.getString("jurisdiction") +"'," +
                                        "printheader='"+ obj.getString("printheader") +"', " +
                                        "showcashpaidpopup='"+ obj.getString("showcashpaidpopup") +"'," +
                                        " freeitemcolor='"+ obj.getString("freeitemcolor")+"'," +
                                        "item_disc_efrom='"+ obj.getString("item_disc_efrom") +"'," +
                                        "zro_price_item_disc='"+ obj.getString("zro_price_item_disc") +"'," +
                                        "cash_item_disc='"+ obj.getString("cash_item_disc") +"'," +
                                        "otp_time_validity='"+obj.getString("otp_time_validity")+"'," +
                                        "otp_time_validity_backend='"+obj.getString("otp_time_validity_backend")+"',"+
                                        "orderautoapproval='"+obj.getString("orderautoapproval")+"', " +
                                        "maxbillamount='"+obj.getString("maxbillamount")+"', " +
                                        "maxbillannualamount='"+obj.getString("maxbillannualamount")+"' ";
                                mDb.execSQL(sql);
                            }


                        } catch (JSONException ex) {
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }

    //sync itemgroup master
    // coumn name
//        "autonum"
//        "itemgroupname"
//        "itemgroupnametamil"
//        "itemgroupcode"
//        "makerid"
//        "createddate"
//        "updateddate"
//        "status"

    public void syncitemgroupmaster (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);
                            if (obj.getString("process").equals("Insert")) {
                                String sql = "SELECT * FROM 'tblitemgroupmaster' WHERE itemgroupcode=" + obj.getString("itemgroupcode");
                                if (!((mDb.rawQuery(sql, null)).moveToFirst())) {
                                    int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                    sql = "INSERT INTO 'tblitemgroupmaster' VALUES('" + gc +"'," +
                                            "'" + obj.getString("itemgroupname").replaceAll("'","''")+"','" + obj.getString("itemgroupnametamil").replaceAll("'","''")+"', " +
                                            "'" + obj.getString("itemgroupcode")+"','" + obj.getString("makerid")+"'," +
                                            "'" + obj.getString("createddate")+"','" + obj.getString("updateddate")+"'," +
                                            "'" + obj.getString("status")+"')";
                                    mDb.execSQL(sql);
                                }   else {
                                    int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                    String sql1 = "UPDATE 'tblitemgroupmaster' SET autonum='" + gc +"'," +
                                            "itemgroupname='" + obj.getString("itemgroupname").replaceAll("'","''")+"'," +
                                            "itemgroupnametamil='" + obj.getString("itemgroupnametamil").replaceAll("'","''")+"',itemgroupcode='" + obj.getString("itemgroupcode")+"'," +
                                            "makerid='" + obj.getString("makerid")+"',createddate='" + obj.getString("createddate")+"'," +
                                            "updateddate='" + obj.getString("updateddate")+"',status='" + obj.getString("status")+"' " +
                                            " WHERE itemgroupcode=" + obj.getString("itemgroupcode");
                                    mDb.execSQL(sql1);
                                }

                            }
                            else {
                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                String sql1 = "UPDATE 'tblitemgroupmaster' SET autonum='" + gc +"'," +
                                        "itemgroupname='" + obj.getString("itemgroupname").replaceAll("'","''")+"'," +
                                        "itemgroupnametamil='" + obj.getString("itemgroupnametamil").replaceAll("'","''")+"',itemgroupcode='" + obj.getString("itemgroupcode")+"'," +
                                        "makerid='" + obj.getString("makerid")+"',createddate='" + obj.getString("createddate")+"'," +
                                        "updateddate='" + obj.getString("updateddate")+"',status='" + obj.getString("status")+"' " +
                                        " WHERE itemgroupcode=" + obj.getString("itemgroupcode");
                                mDb.execSQL(sql1);
                            }
                        } catch (JSONException ex) {
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }

    //sync itemmaster to sqlitre
    //column name
//             "autonum"
//            "itemcode"
//            "companycode"
//            "itemsubgroupcode"
//            "brandcode"
//            "manualitemcode"
//            "itemname"
//            "itemnametamil"
//            "unitcode"
//            "unitweightunitcode"
//            "unitweight"
//            "uppunitcode"
//            "uppweight"
//            "itemcategory"
//            "parentitemcode"
//            "allowpriceedit"
//            "allownegativestock"
//            "allowdiscount"
//            "status"
//            "createddate"
//            "updateddate"
//            "makerid"

    public void syncitemmaster (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;
            Cursor cursor = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);
                            if (obj.getString("process").equals("Insert")) {
                                String sql = "SELECT * FROM 'tblitemmaster' WHERE itemcode=" + obj.getString("itemcode");
                                cursor = mDb.rawQuery(sql, null);
                                if (!cursor.moveToFirst()) {
                                    int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                    sql = "INSERT INTO 'tblitemmaster' VALUES('" + gc +"','" + obj.getString("itemcode")+"','" + obj.getString("companycode")+"'," +
                                            "'" + obj.getString("itemsubgroupcode")+"','" + obj.getString("brandcode")+"'," +
                                            "'" + obj.getString("manualitemcode")+"','" + obj.getString("itemname").replaceAll("'","''")+"'," +
                                            "'" + obj.getString("itemnametamil").replaceAll("'","''")+"','" + obj.getString("unitcode")+"'," +
                                            "'" + obj.getString("unitweightunitcode")+"','" + obj.getString("unitweight")+"'," +
                                            "'" + obj.getString("uppunitcode")+"','" + obj.getString("uppweight")+"'," +
                                            "'" + obj.getString("itemcategory")+"','" + obj.getString("parentitemcode")+"'," +
                                            "'" + obj.getString("allowpriceedit")+"','" + obj.getString("allownegativestock")+"'," +
                                            "'" + obj.getString("allowdiscount")+"','" + obj.getString("status")+"'," +
                                            "'" + obj.getString("createddate")+"','" + obj.getString("updateddate")+"'," +
                                            "'" + obj.getString("makerid")+"','" + obj.getString("upp")+"'," +
                                            "'" + obj.getString("uppweightunitcode")+"','" + obj.getString("offset")+"'," +
                                            "'" + obj.getString("orderstatus")+"','" + obj.getString("maxorderqty")+"' ," +
                                            "'" + obj.getString("business_type")+"','" + obj.getString("minimumsalesqty")+"'," +
                                            " '" + obj.getString("itemtype")+"','"+obj.getString("erpitemcode")+"')";
                                    mDb.execSQL(sql);
                                } else {
                                    int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                    String sql1 = "UPDATE 'tblitemmaster' SET autonum='" + gc +"',itemcode='" + obj.getString("itemcode")+"'," +
                                            "companycode='" + obj.getString("companycode")+"'," +
                                            "itemsubgroupcode='" + obj.getString("itemsubgroupcode")+"'," +
                                            "brandcode='" + obj.getString("brandcode")+"',manualitemcode='" + obj.getString("manualitemcode")+"'," +
                                            "itemname='" + obj.getString("itemname").replaceAll("'","''")+"',itemnametamil='" + obj.getString("itemnametamil").replaceAll("'","''")+"',unitcode='" + obj.getString("unitcode")+"'," +
                                            "unitweightunitcode='" + obj.getString("unitweightunitcode")+"'," +
                                            "unitweight='" + obj.getString("unitweight")+"'," +
                                            "uppunitcode='" + obj.getString("uppunitcode")+"'," +
                                            "uppweight='" + obj.getString("uppweight")+"',itemcategory='" + obj.getString("itemcategory")+"'," +
                                            "parentitemcode='" + obj.getString("parentitemcode")+"'," +
                                            "allowpriceedit='" + obj.getString("allowpriceedit")+"'," +
                                            "allownegativestock='" + obj.getString("allownegativestock")+"'," +
                                            "allowdiscount='" + obj.getString("allowdiscount")+"',status='" + obj.getString("status")+"'," +
                                            "createddate='" + obj.getString("createddate")+"',updateddate='" + obj.getString("updateddate")+"'," +
                                            "makerid='" + obj.getString("makerid")+"' ,upp='" + obj.getString("upp")+"'," +
                                            "uppweightunitcode = '" + obj.getString("uppweightunitcode") + "', " +
                                            "offset='" + obj.getString("offset")+"'," +
                                            "orderstatus='" + obj.getString("orderstatus")+"' ,maxorderqty='" + obj.getString("maxorderqty")+"' ," +
                                            " business_type='" + obj.getString("business_type")+"'," +
                                            " minimumsalesqty='" + obj.getString("minimumsalesqty")+"', " +
                                            " itemtype='"+ obj.getString("itemtype") +"'," +
                                            "erpitemcode='"+obj.getString("erpitemcode")+"'"+
                                            " WHERE itemcode=" + obj.getString("itemcode");
                                    mDb.execSQL(sql1);
                                }

                            }
                            else {
                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                String sql1 = "UPDATE 'tblitemmaster' SET autonum='" + gc +"',itemcode='" + obj.getString("itemcode")+"'," +
                                        "companycode='" + obj.getString("companycode")+"'," +
                                        "itemsubgroupcode='" + obj.getString("itemsubgroupcode")+"'," +
                                        "brandcode='" + obj.getString("brandcode")+"',manualitemcode='" + obj.getString("manualitemcode")+"'," +
                                        "itemname='" + obj.getString("itemname").replaceAll("'","''")+"',itemnametamil='" + obj.getString("itemnametamil").replaceAll("'","''")+"',unitcode='" + obj.getString("unitcode")+"'," +
                                        "unitweightunitcode='" + obj.getString("unitweightunitcode")+"'," +
                                        "unitweight='" + obj.getString("unitweight")+"'," +
                                        "uppunitcode='" + obj.getString("uppunitcode")+"'," +
                                        "uppweight='" + obj.getString("uppweight")+"',itemcategory='" + obj.getString("itemcategory")+"'," +
                                        "parentitemcode='" + obj.getString("parentitemcode")+"'," +
                                        "allowpriceedit='" + obj.getString("allowpriceedit")+"'," +
                                        "allownegativestock='" + obj.getString("allownegativestock")+"'," +
                                        "allowdiscount='" + obj.getString("allowdiscount")+"',status='" + obj.getString("status")+"'," +
                                        "createddate='" + obj.getString("createddate")+"',updateddate='" + obj.getString("updateddate")+"'," +
                                        "makerid='" + obj.getString("makerid")+"' ,upp='" + obj.getString("upp")+"'," +
                                        "uppweightunitcode = '" + obj.getString("uppweightunitcode") + "', " +
                                        "offset='" + obj.getString("offset")+"'," +
                                        "orderstatus='" + obj.getString("orderstatus")+"' ,maxorderqty='" + obj.getString("maxorderqty")+"'," +
                                        " business_type='" + obj.getString("business_type")+"'," +
                                        " minimumsalesqty='" + obj.getString("minimumsalesqty")+"' , " +
                                        " itemtype='"+ obj.getString("itemtype") +"'," +
                                        "erpitemcode='"+obj.getString("erpitemcode")+"'"+
                                        " WHERE itemcode=" + obj.getString("itemcode");
                                mDb.execSQL(sql1);
                            }
                        } catch (JSONException ex) {
                            if(cursor != null)
                                cursor.close();
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        } finally{
                            if(cursor != null)
                                cursor.close();
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                if(cursor != null)
                    cursor.close();
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            } finally {
                if(cursor != null)
                    cursor.close();
            }
        }
    }


    public void syncscheduleeway (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;
            Cursor cursor = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    int flag=0;
                    String sql;
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);
//                            if (obj.getString("process").equals("Insert")) {
                            if(flag==0) {
                                sql = "DELETE FROM 'tblscheduleeway' WHERE schedulecode=" + obj.getString("schedulecode");
                                cursor = mDb.rawQuery(sql, null);
                            }
                            if (!cursor.moveToFirst()) {
                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                sql = "INSERT INTO 'tblscheduleeway' (schedulecode,scheduledate,ewayurl,createddate,filename) VALUES('" + obj.getString("schedulecode")+"','" + obj.getString("scheduledate")+"'," +
                                        "'" + obj.getString("ewaybillurl")+"','" + obj.getString("createddate")+"'," +
                                        "'" + obj.getString("filename")+"')";
                                mDb.execSQL(sql);
                            }

//                            }

                        } catch (JSONException ex) {
                            if(cursor != null)
                                cursor.close();
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        } finally{
                            if(cursor != null)
                                cursor.close();
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                if(cursor != null)
                    cursor.close();
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            } finally {
                if(cursor != null)
                    cursor.close();
            }
        }
    }
    public void syncpricelist_every_five_seconds (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {

                    json_category = object.getJSONArray("Value");
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);
                            //String dqlsql="DELETE from tblitempricelisttransaction WHERE itemcode=" + obj.getString("itemcode");
                            //mDb.execSQL(dqlsql);
                            String sql = "SELECT * FROM 'tblitempricelisttransaction' WHERE itemcode=" + obj.getString("itemcode");
                            if (!((mDb.rawQuery(sql, null)).moveToFirst())) {
                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                sql = "INSERT INTO 'tblitempricelisttransaction'(autonum,itemcode,oldprice," +
                                        " newprice,oldorderprice,neworderprice,createddate,wmsrate) VALUES('" + gc +"'," +
                                        "'" + obj.getString("itemcode")+"'" +
                                        ",'" + obj.getString("oldprice")+"', '" + obj.getString("newprice")+"' " +
                                        ",'" + obj.getString("oldorderprice")+"', '" + obj.getString("neworderprice")+"'," +
                                        "'" + obj.getString("createddate")+"','"+obj.getString("wmsrate")+"')";
                                mDb.execSQL(sql);
                            } else {
                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                String sql1 = "UPDATE 'tblitempricelisttransaction' SET autonum='" + gc +"'," +
                                        "oldprice='" + obj.getString("oldprice")+"'," +
                                        "newprice='" + obj.getString("newprice")+"'," +
                                        "oldorderprice='" + obj.getString("oldorderprice")+"'," +
                                        "neworderprice='" + obj.getString("neworderprice")+"'," +
                                        "createddate='" + obj.getString("createddate")+"',  " +
                                        "wmsrate='" + obj.getString("wmsrate")+"'  " +
                                        " WHERE itemcode=" + obj.getString("itemcode");
                                mDb.execSQL(sql1);
                            }


                        } catch (JSONException ex) {
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }

    public void syncorderstatus_every_five_mints (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);
                            String sql = "SELECT * FROM 'tblsalesorder' WHERE transactionno=" + obj.getString("transactionno");

                            String sql1 = "UPDATE 'tblsalesorder' SET  " +
                                    "status='" + obj.getString("status")+"' " +
                                    " WHERE transactionno='" + obj.getString("transactionno")+"' "+
                                    " and vancode='" + obj.getString("vancode")+"' and " +
                                    " bookingno = '"+obj.getString("bookingno")+"' "+
                                    " and financialyearcode='" + obj.getString("financialyearcode")+"' ";
                            mDb.execSQL(sql1);


                        } catch (JSONException ex) {
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }
    //sync pricelisttransaction to sqlite
    //column name

//   ```` ```` "autonum"
//            "itemcode"
//            "oldprice"
//            "newprice"
//            "transactionno"
//            "transactiondate"
//            "makerid"
//            "createddate"
//            "updateddate"

    public void syncitempricelisttransaction (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;
            Cursor cursor = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    //String sql1="DELETE FROM 'tblitempricelisttransaction'";
                    // mDb.execSQL(sql1);
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);

                            /*int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                            String sql = "INSERT INTO 'tblitempricelisttransaction'(autonum,itemcode,oldprice," +
                                    " newprice,oldorderprice,neworderprice,createddate) VALUES('" + gc +"'," +
                                    "'" + obj.getString("itemcode")+"','" + obj.getString("oldprice")+"'," +
                                    " '" + obj.getString("newprice")+"', '" + obj.getString("oldorderprice")+"'," +
                                    "'" + obj.getString("neworderprice")+"','" + obj.getString("createddate")+"')";
                            mDb.execSQL(sql);*/

                            String sql = "SELECT * FROM 'tblitempricelisttransaction' WHERE itemcode=" + obj.getString("itemcode");
                            cursor = mDb.rawQuery(sql, null);
                            if (!cursor.moveToFirst()) {
                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                sql = "INSERT INTO 'tblitempricelisttransaction'(autonum,itemcode,oldprice," +
                                        " newprice,oldorderprice,neworderprice,createddate,wmsrate) VALUES('" + gc +"'," +
                                        "'" + obj.getString("itemcode")+"'" +
                                        ",'" + obj.getString("oldprice")+"', '" + obj.getString("newprice")+"' " +
                                        ",'" + obj.getString("oldorderprice")+"', '" + obj.getString("neworderprice")+"'," +
                                        "'" + obj.getString("createddate")+"','"+obj.getString("wmsrate")+"')";
                                mDb.execSQL(sql);
                            } else {
                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                String sql1 = "UPDATE 'tblitempricelisttransaction' SET autonum='" + gc +"'," +
                                        "oldprice='" + obj.getString("oldprice")+"'," +
                                        "newprice='" + obj.getString("newprice")+"'," +
                                        "oldorderprice='" + obj.getString("oldorderprice")+"'," +
                                        "neworderprice='" + obj.getString("neworderprice")+"'," +
                                        "createddate='" + obj.getString("createddate")+"',  " +
                                        "wmsrate='" + obj.getString("wmsrate")+"'  " +
                                        " WHERE itemcode=" + obj.getString("itemcode");
                                mDb.execSQL(sql1);
                            }

                        } catch (JSONException ex) {
                            if(cursor != null)
                                cursor.close();
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        } finally{
                            if(cursor != null)
                                cursor.close();
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                if(cursor != null)
                    cursor.close();
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            } finally{
                if(cursor != null)
                    cursor.close();
            }
        }
    }


    //sync itemsubgroupmaster to sqlite
    //column name
//            "autonum"
//        "itemsubgroupcode"
//        "itemsubgroupname"
//        "itemsubgroupnametamil"
//        "itemgroupcode"
//        "hsn"
//        "tax"
//        "status"
//        "makerid"
//        "createddate"
//        "updateddate"

    public void syncitemsubgroupmaster (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);
                            if (obj.getString("process").equals("Insert")) {
                                String sql = "SELECT * FROM 'tblitemsubgroupmaster' WHERE itemsubgroupcode=" + obj.getString("itemsubgroupcode");
                                if (!((mDb.rawQuery(sql, null)).moveToFirst())) {
                                    int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                    sql = "INSERT INTO 'tblitemsubgroupmaster' VALUES('" + gc +"'," +
                                            "'" + obj.getString("itemsubgroupcode")+"','" + obj.getString("itemsubgroupname").replaceAll("'","''")+"'," +
                                            "'" + obj.getString("itemsubgroupnametamil").replaceAll("'","''")+"','" + obj.getString("itemgroupcode")+"'," +
                                            "'" + obj.getString("hsn")+"','" + obj.getString("tax")+"'," +
                                            "'" + obj.getString("status")+"','" + obj.getString("makerid")+"'," +
                                            "'" + obj.getString("createddate")+"','" + obj.getString("updateddate")+"')";
                                    mDb.execSQL(sql);
                                }else {
                                    int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                    String sql1 = "UPDATE 'tblitemsubgroupmaster' SET autonum='" + gc +"'," +
                                            "itemsubgroupcode='" + obj.getString("itemsubgroupcode")+"'," +
                                            "itemsubgroupname='" + obj.getString("itemsubgroupname").replaceAll("'","''")+"',itemsubgroupnametamil='" + obj.getString("itemsubgroupnametamil").replaceAll("'","''")+"'," +
                                            "itemgroupcode='" + obj.getString("itemgroupcode")+"',hsn='" + obj.getString("hsn")+"'," +
                                            "tax='" + obj.getString("tax")+"',status='" + obj.getString("status")+"'," +
                                            "makerid='" + obj.getString("makerid")+"',createddate='" + obj.getString("createddate")+"'," +
                                            "updateddate='" + obj.getString("updateddate")+"' " +
                                            " WHERE itemsubgroupcode=" + obj.getString("itemsubgroupcode");
                                    mDb.execSQL(sql1);
                                }

                            }
                            else {
                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                String sql1 = "UPDATE 'tblitemsubgroupmaster' SET autonum='" + gc +"'," +
                                        "itemsubgroupcode='" + obj.getString("itemsubgroupcode")+"'," +
                                        "itemsubgroupname='" + obj.getString("itemsubgroupname").replaceAll("'","''")+"',itemsubgroupnametamil='" + obj.getString("itemsubgroupnametamil").replaceAll("'","''")+"'," +
                                        "itemgroupcode='" + obj.getString("itemgroupcode")+"',hsn='" + obj.getString("hsn")+"'," +
                                        "tax='" + obj.getString("tax")+"',status='" + obj.getString("status")+"'," +
                                        "makerid='" + obj.getString("makerid")+"',createddate='" + obj.getString("createddate")+"'," +
                                        "updateddate='" + obj.getString("updateddate")+"' " +
                                        " WHERE itemsubgroupcode=" + obj.getString("itemsubgroupcode");
                                mDb.execSQL(sql1);
                            }
                        } catch (JSONException ex) {
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }


    //sync route to sqlite
    //column name
//        "autonum"
//        "routecode"
//        "routename"
//        "routenametamil"
//        "routeday"
//        "status"
//        "makerid"
//        "createddate"
//        "updateddate"
    public void syncroute (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    //String deletesql="delete from tblroute";
                    //mDb.execSQL(deletesql);

                    json_category = object.getJSONArray("Value");
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);
                            if (obj.getString("process").equals("Insert")) {
                                String sql = "SELECT * FROM 'tblroute' WHERE routecode=" + obj.getString("routecode");
                                if (!((mDb.rawQuery(sql, null)).moveToFirst())) {
                                    int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                    sql = "INSERT INTO 'tblroute' VALUES('" + gc +"','" + obj.getString("routecode")+"'," +
                                            "'" + obj.getString("routename").replaceAll("'","''")+"','" + obj.getString("routenametamil")+"'," +
                                            "'" + obj.getString("routeday")+"','" + obj.getString("status")+"'," +
                                            "'" + obj.getString("makerid")+"','" + obj.getString("createddate")+"'," +
                                            "'" + obj.getString("updateddate")+"','"+obj.getString("vancode")+"'," +
                                            " '"+obj.getString("business_type")+"')";
                                    mDb.execSQL(sql);
                                    String sqldelete="DELETE FROM 'tblroutedetails' where routecode='"+ obj.getString("routecode")+"'";
                                    mDb.execSQL(sqldelete);
                                }else{
                                    int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                    String sql1 = "UPDATE 'tblroute' SET autonum='" + gc +"',routecode='" + obj.getString("routecode")+"'," +
                                            "routename='" + obj.getString("routename").replaceAll("'","''")+"',routenametamil='" + obj.getString("routenametamil").replaceAll("'","''")+"'," +
                                            "routeday='" + obj.getString("routeday")+"',status='" + obj.getString("status")+"'," +
                                            "makerid='" + obj.getString("makerid")+"',createddate='" + obj.getString("createddate")+"'," +
                                            "updateddate='" + obj.getString("updateddate")+"', " +
                                            "vancode='" + obj.getString("vancode")+"',business_type='"+obj.getString("business_type")+"' " +
                                            " WHERE routecode=" + obj.getString("routecode");
                                    mDb.execSQL(sql1);

                                    String sqldelete="DELETE FROM 'tblroutedetails' where routecode='"+ obj.getString("routecode")+"'";
                                    mDb.execSQL(sqldelete);
                                }

                            }
                            else {
                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                String sql1 = "UPDATE 'tblroute' SET autonum='" + gc +"',routecode='" + obj.getString("routecode")+"'," +
                                        "routename='" + obj.getString("routename").replaceAll("'","''")+"',routenametamil='" + obj.getString("routenametamil").replaceAll("'","''")+"'," +
                                        "routeday='" + obj.getString("routeday")+"',status='" + obj.getString("status")+"'," +
                                        "makerid='" + obj.getString("makerid")+"',createddate='" + obj.getString("createddate")+"'," +
                                        "updateddate='" + obj.getString("updateddate")+"', " +
                                        "vancode='" + obj.getString("vancode")+"',business_type='"+obj.getString("business_type")+"' " +
                                        " WHERE routecode=" + obj.getString("routecode");
                                mDb.execSQL(sql1);

                                String sqldelete="DELETE FROM 'tblroutedetails' where routecode='"+ obj.getString("routecode")+"'";
                                mDb.execSQL(sqldelete);
                            }
                        } catch (JSONException ex) {
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }


    //sync route details
    // column name
//            "autonum"
//            "routecode"
//            "areacode"
//            "allowpriceedit"
//            "createddate"
//            "updateddate"
    public void syncroutedetails (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);
                            //  if (obj.getString("process").equals("Insert")) {
                            //String sql = "select * FROM 'tblroutedetails' WHERE routecode=" + obj.getString("routecode");
                            // mDb.execSQL(sql);
                            // if (!((mDb.rawQuery(sql, null)).moveToFirst())) {
                            int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                            String sql = "INSERT INTO 'tblroutedetails' VALUES('" + gc +"'," +
                                    "'" + obj.getString("routecode")+"','" + obj.getString("areacode")+"'," +
                                    "'" + obj.getString("allowpriceedit")+"'," +
                                    "'" + obj.getString("createddate")+"'," +
                                    "'" + obj.getString("updateddate")+"',"+
                                    "'" + obj.getString("areaserialno")+"'," +
                                    "'" + obj.getString("allowmobilenoverify")+"')";
                            mDb.execSQL(sql);
                                /*}else{
                                    int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                    sql="update 'tblroutedetails' set autonum ='"+ gc +"'," +
                                            " areacode='"+ obj.getString("areacode") +"'," +
                                            " allowpriceedit='"+obj.getString("allowpriceedit")+"',createddate='" + obj.getString("createddate")+"'," +
                                            " updateddate='" + obj.getString("updateddate")+"', areaserialno='"+ obj.getString("areaserialno") +"'" +
                                            " where routecode='"+ obj.getString("routecode") +"' ";
                                    mDb.execSQL(sql);
                                }*/

                            //}
                        } catch (JSONException ex) {
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }

    //sync transport mode
    public void synctransportmode (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    String sqldelete ="DELETE FROM tbltransportmode";
                    mDb.execSQL(sqldelete);

                    json_category = object.getJSONArray("Value");
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);
                            if (obj.getString("process").equals("Insert")) {
                                String sql = "SELECT * FROM 'tbltransportmode' WHERE transportmodecode=" + obj.getString("transportmodecode");
                                if (!((mDb.rawQuery(sql, null)).moveToFirst())) {

                                    sql = "INSERT INTO 'tbltransportmode' VALUES('" + obj.getString("autonum")+"'," +
                                            "'" + obj.getString("transportmodecode")+"'," +
                                            "'" + obj.getString("transportmodetype")+"')";
                                    mDb.execSQL(sql);
                                }

                            }
                            else {
                                String sql1 = "UPDATE 'tbltransportmode' SET autonum='" + obj.getString("autonum")+"'," +
                                        "transportmodetype='" + obj.getString("transportmodetype")+"' " +
                                        " WHERE transportmodecode=" + obj.getString("transportmodecode");
                                mDb.execSQL(sql1);
                            }
                        } catch (JSONException ex) {
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }

    //sync transport
    public void synctransport (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);
                            if (obj.getString("process").equals("Insert")) {
                                String sql = "SELECT * FROM 'tbltransportmaster' WHERE transportid=" + obj.getString("transportid");
                                if (!((mDb.rawQuery(sql, null)).moveToFirst())) {

                                    sql = "INSERT INTO 'tbltransportmaster' VALUES('" + obj.getString("transportid")+"'," +
                                            "'" + obj.getString("transportname").replaceAll("'","''")+"'," +
                                            "'" + obj.getString("vechicle_number")+"'," +
                                            "'" + obj.getString("contact_name")+"','" + obj.getString("contact_number")+"'," +
                                            "'" + obj.getString("city")+"', '"+obj.getString("transportmodecode")+"', " +
                                            "'" + obj.getString("createddate")+"','"+obj.getString("updateddate")+"'," +
                                            " '"+obj.getString("makerid")+"','"+obj.getString("status")+"')";
                                    mDb.execSQL(sql);
                                }

                            }
                            else {
                                String sql1 = "UPDATE 'tbltransportmaster' SET transportname='" + obj.getString("transportname").replaceAll("'","''")+"'," +
                                        "vechicle_number='" + obj.getString("vechicle_number")+"',contact_name='" + obj.getString("contact_name").replaceAll("'","''")+"'," +
                                        "contact_number='" + obj.getString("contact_number")+"',city='" + obj.getString("city")+"'," +
                                        "transportmodecode='" + obj.getString("transportmodecode")+"'," +
                                        "createddate='" + obj.getString("createddate")+"'," +
                                        "updateddate='" + obj.getString("updateddate")+"', " +
                                        "makerid='" + obj.getString("makerid")+"',status='"+obj.getString("status")+"' " +
                                        " WHERE transportid=" + obj.getString("transportid");
                                mDb.execSQL(sql1);
                            }
                        } catch (JSONException ex) {
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }

    //sync transport
    public void synctransportareamapping (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);
                            if (obj.getString("process").equals("Insert")) {
                                String sql = "SELECT * FROM 'tbltransportcitymapping' WHERE transportid='" + obj.getString("transportid") +"' and citycode='" + obj.getString("citycode") +"' ";
                                if (!((mDb.rawQuery(sql, null)).moveToFirst())) {

                                    sql = "INSERT INTO 'tbltransportcitymapping' VALUES('" + obj.getString("citymappingid")+"'," +
                                            "'" + obj.getString("transportid")+"'," +
                                            "'" + obj.getString("citycode")+"','" + obj.getString("day_of_dispatch")+"', " +
                                            "'" + obj.getString("createddate")+"','" + obj.getString("updateddate")+"')";
                                    mDb.execSQL(sql);
                                } else {
                                    String sql1 = "UPDATE 'tbltransportcitymapping' SET citymappingid='" + obj.getString("citymappingid")+"'," +
                                            "citycode='" + obj.getString("citycode")+"',day_of_dispatch='" + obj.getString("day_of_dispatch")+"'" +
                                            ",createddate='" + obj.getString("createddate")+"'," +
                                            "updateddate='" + obj.getString("updateddate")+"'  " +
                                            " WHERE transportid='" + obj.getString("transportid") +"' and citycode='" + obj.getString("citycode") +"' ";
                                    mDb.execSQL(sql1);
                                }

                            }
                            else {
                                String sql1 = "UPDATE 'tbltransportcitymapping' SET citymappingid='" + obj.getString("citymappingid")+"'," +
                                        "citycode='" + obj.getString("citycode")+"',day_of_dispatch='" + obj.getString("day_of_dispatch")+"'" +
                                        ",createddate='" + obj.getString("createddate")+"'," +
                                        "updateddate='" + obj.getString("updateddate")+"'  " +
                                        " WHERE transportid='" + obj.getString("transportid") +"' and citycode='" + obj.getString("citycode") +"' ";
                                mDb.execSQL(sql1);
                            }
                        } catch (JSONException ex) {
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }
    //sync sales schedule
    //column name
//        "autonum"
//        "refno"
//        "schedulecode"
//        "scheduledate"
//        "vancode"
//        "routecode"
//        "vehiclecode"
//        "employeecode"
//        "drivercode"
//        "helpername"
//        "tripadvance"
//        "startingkm"
//        "endingkm"
//        "createddate"
//        "updatedate"
//        "makerid"
//        "flag"
    public void syncsalesschedule (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);
                            if (obj.getString("process").equals("Insert")) {


                                String sql = "SELECT * FROM 'tblsalesschedule' WHERE schedulecode='" + obj.getString("schedulecode")+"' ";
                                if (!((mDb.rawQuery(sql, null)).moveToFirst())) {
                                    int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                    String vartripadvance = obj.isNull("tripadvance") ? "0" : obj.getString("tripadvance");
                                    if(vartripadvance.equals("")){
                                        vartripadvance="0";
                                    }
                                    sql = "INSERT INTO 'tblsalesschedule' (autonum, refno,schedulecode, scheduledate,vancode,routecode,vehiclecode,employeecode, drivercode,helpername," +
                                            "tripadvance,startingkm,endingkm,createddate, updateddate,makerid,flag,lunch_start_time,lunch_end_time) VALUES('" + gc +"','" + obj.getString("refno")+"'," +
                                            "'" + obj.getString("schedulecode")+"','" + obj.getString("scheduledate")+"','" + obj.getString("vancode")+"'," +
                                            "'" + obj.getString("routecode")+"','" + obj.getString("vehiclecode")+"','" + obj.getString("employeecode")+"'," +
                                            "'" + obj.getString("drivercode")+"','" + obj.getString("helpername").replaceAll("'","''")+"','" + vartripadvance +"'," +
                                            "'" + obj.getString("startingkm")+"','" + obj.getString("endingkm")+"','" + obj.getString("createddate")+"'," +
                                            "'" + obj.getString("updateddate")+"','" + obj.getString("makerid")+"',1,'" + obj.getString("lunch_start_time")+"','" + obj.getString("lunch_end_time")+"')";
                                    mDb.execSQL(sql);
                                }

                            }
                            else {
                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                String vartripadvance = obj.isNull("tripadvance") ? "0" : obj.getString("tripadvance");
                                if(vartripadvance.equals("")){
                                    vartripadvance="0";
                                }
                                String sql1 = "UPDATE 'tblsalesschedule' SET autonum='" + gc +"',schedulecode='" + obj.getString("schedulecode")+"'," +
                                        "refno='" + obj.getString("refno")+"',scheduledate='" + obj.getString("scheduledate")+"'," +
                                        "vancode='" + obj.getString("vancode")+"',routecode='" + obj.getString("routecode")+"'," +
                                        "vehiclecode='" + obj.getString("vehiclecode")+"',employeecode='" + obj.getString("employeecode")+"'," +
                                        "drivercode='" + obj.getString("drivercode")+"',helpername='" + obj.getString("helpername").replaceAll("'","''")+"'," +
                                        "tripadvance='" + vartripadvance +"',startingkm='" + obj.getString("startingkm")+"'," +
                                        "endingkm='" + obj.getString("endingkm")+"',createddate='" + obj.getString("createddate")+"'," +
                                        "updateddate='" + obj.getString("updateddate")+"',makerid='" + obj.getString("makerid")+"'," +
                                        " lunch_start_time='" + obj.getString("lunch_start_time")+"',lunch_end_time='" + obj.getString("lunch_end_time")+"' " +
                                        " WHERE schedulecode='" + obj.getString("schedulecode")+"' ";
                                mDb.execSQL(sql1);
                            }
                        } catch (JSONException ex) {
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }


    //sunv scheme to sqlite
    // column name

//            "autonum"
//            "schemecode"
//            "companycode"
//            "schemename"
//            "multipleroutecode"
//            "validityfrom"
//            "validityto"
//            "schemetype"
//            "status"
//            "makerid"
//            "createddate"
//            "updateddate"

    public void syncscheme (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);
                            if (obj.getString("process").equals("Insert")) {
                                String sql = "SELECT * FROM 'tblscheme' WHERE schemecode=" + obj.getString("schemecode");
                                if (!((mDb.rawQuery(sql, null)).moveToFirst())) {
                                    int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                    sql = "INSERT INTO 'tblscheme' VALUES('" + gc +"','" + obj.getString("schemecode")+"'," +
                                            "'" + obj.getString("companycode")+"','" + obj.getString("schemename").replaceAll("'","''")+"'," +
                                            "'" + obj.getString("multipleroutecode")+"','" + obj.getString("validityfrom")+"'," +
                                            "'" + obj.getString("validityto")+"','" + obj.getString("schemetype")+"'," +
                                            "'" + obj.getString("status")+"','" + obj.getString("makerid")+"'," +
                                            "'" + obj.getString("createddate")+"','" + obj.getString("updateddate")+"'," +
                                            " '" + obj.getString("business_type")+"','"+obj.getString("multiplebilltypecode")+"'," +
                                            "'"+obj.getString("itemsubgroupcode")+"','"+obj.getString("freeitemcode")+"'," +
                                            "'"+obj.getString("freeqty")+"','"+obj.getString("purchaseqty")+"'," +
                                            "'"+obj.getString("multipleareacode")+"','"+obj.getString("freeunit")+"'," +
                                            "'"+obj.getString("purchaseunit")+"')";
                                    mDb.execSQL(sql);
                                    /*String sqldelete="DELETE FROM 'tblschemeitemdetails' WHERE schemecode='"+ obj.getString("schemecode") +"'";
                                    mDb.execSQL(sqldelete);*/

                                    /*String sqldelete1="DELETE FROM 'tblschemeratedetails' WHERE schemecode='"+ obj.getString("schemecode") +"'";
                                    mDb.execSQL(sqldelete1);*/
                                }else {
                                    int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                    String sql1 = "UPDATE 'tblscheme' SET autonum='" + obj.getString("autonum")+"'," +
                                            "schemecode='" + obj.getString("schemecode")+"',companycode='" + obj.getString("companycode")+"'," +
                                            "schemename='" + obj.getString("schemename").replaceAll("'","''")+"',multipleroutecode='" + obj.getString("multipleroutecode")+"'," +
                                            "validityfrom='" + obj.getString("validityfrom")+"',validityto='" + obj.getString("validityto")+"'," +
                                            "schemetype='" + obj.getString("schemetype")+"',status='" + obj.getString("status")+"'," +
                                            "makerid='" + obj.getString("makerid")+"',createddate='" + obj.getString("createddate")+"'," +
                                            "updateddate='" + obj.getString("updateddate")+"', " +
                                            "business_type='" + obj.getString("business_type")+"', " +
                                            "multiplebilltypecode='" + obj.getString("multiplebilltypecode")+"'," +
                                            "itemsubgroupcode='"+obj.getString("itemsubgroupcode")+"',freeitemcode='"+obj.getString("freeitemcode")+"'," +
                                            "freeqty='"+obj.getString("freeqty")+"',purchaseqty='"+obj.getString("purchaseqty")+"'," +
                                            "multipleareacode='"+obj.getString("multipleareacode")+"',freeunit='"+obj.getString("freeunit")+"'," +
                                            "purchaseunit='"+obj.getString("purchaseunit")+"'"+
                                            " WHERE schemecode=" + obj.getString("schemecode");
                                    mDb.execSQL(sql1);

                                /*String sqldelete="DELETE FROM 'tblschemeitemdetails' WHERE schemecode='"+ obj.getString("schemecode") +"'";
                                mDb.execSQL(sqldelete);*/

                                /*String sqldelete1="DELETE FROM 'tblschemeratedetails' WHERE schemecode='"+ obj.getString("schemecode") +"'";
                                mDb.execSQL(sqldelete1);*/
                                }

                            }else {
                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                String sql1 = "UPDATE 'tblscheme' SET autonum='" + obj.getString("autonum")+"'," +
                                        "schemecode='" + obj.getString("schemecode")+"',companycode='" + obj.getString("companycode")+"'," +
                                        "schemename='" + obj.getString("schemename").replaceAll("'","''")+"',multipleroutecode='" + obj.getString("multipleroutecode")+"'," +
                                        "validityfrom='" + obj.getString("validityfrom")+"',validityto='" + obj.getString("validityto")+"'," +
                                        "schemetype='" + obj.getString("schemetype")+"',status='" + obj.getString("status")+"'," +
                                        "makerid='" + obj.getString("makerid")+"',createddate='" + obj.getString("createddate")+"'," +
                                        "updateddate='" + obj.getString("updateddate")+"', " +
                                        "business_type='" + obj.getString("business_type")+"', " +
                                        "multiplebilltypecode='" + obj.getString("multiplebilltypecode")+"' " +
                                        " WHERE schemecode=" + obj.getString("schemecode");
                                mDb.execSQL(sql1);

                                /*String sqldelete="DELETE FROM 'tblschemeitemdetails' WHERE schemecode='"+ obj.getString("schemecode") +"'";
                                mDb.execSQL(sqldelete);*/

                                /*String sqldelete1="DELETE FROM 'tblschemeratedetails' WHERE schemecode='"+ obj.getString("schemecode") +"'";
                                mDb.execSQL(sqldelete1);*/
                            }

                        } catch (JSONException ex) {
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }


    //sync scheme item details
    // column name

//            "autonum"
//            "schemecode"
//            "purchaseitemcode"
//            "purchaseqty"
//            "freeitemcode"
//            "freeqty"
//            "createddate"
//            "updateddate"

    public void syncschemeitemdetails (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    String sqldelete ="DELETE FROM tblschemeitemdetails";
                    mDb.execSQL(sqldelete);
                    json_category = object.getJSONArray("Value");

                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);


                            //if (obj.getString("process").equals("Insert")) {
                            String sql = "SELECT * FROM 'tblschemeitemdetails' WHERE schemecode=" + obj.getString("schemecode");
                            // if (!((mDb.rawQuery(sql, null)).moveToFirst())) {
                            int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                            sql = "INSERT INTO 'tblschemeitemdetails' VALUES('" + gc +"','" + obj.getString("schemecode")+"'," +
                                    "'" + obj.getString("purchaseitemcode")+"','" + obj.getString("purchaseqty")+"'," +
                                    "'" + obj.getString("freeitemcode")+"','" + obj.getString("freeqty")+"'," +
                                    "'" + obj.getString("createddate")+"','" + obj.getString("updateddate")+"')";
                            mDb.execSQL(sql);

                            //  }

                            //}
                        } catch (JSONException ex) {
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }


    //sync schemeratedetails
    //column name

    public void syncschemeratedetails (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    String sqldelete ="DELETE FROM tblschemeratedetails";
                    mDb.execSQL(sqldelete);
                    json_category = object.getJSONArray("Value");
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);
                            //if (obj.getString("process").equals("Insert")) {
                            String sql = "SELECT * FROM 'tblschemeratedetails' WHERE schemecode=" + obj.getString("schemecode");
                            // if (!((mDb.rawQuery(sql, null)).moveToFirst())) {
                            int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                            sql = "INSERT INTO 'tblschemeratedetails' VALUES('" + obj.getString("autonum")+"'," +
                                    "'" + obj.getString("schemecode")+"','" + obj.getString("itemcode")+"'," +
                                    "'" + obj.getString("minqty")+"','" + obj.getString("discountamount")+"'," +
                                    "'" + obj.getString("createddate")+"','" + obj.getString("updateddate")+"')";
                            mDb.execSQL(sql);

                            //}

                            //}
                        } catch (JSONException ex) {
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }

    //sync unitmaster
    //column name

//            "autonum"
//            "unitname"
//            "unitcode"
//            "unitnametamil"
//            "makerid"
//            "noofdecimals"
//            "status"
//            "createddate"
//            "updateddate"

    public void syncunitmaster (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);
                            if (obj.getString("process").equals("Insert")) {
                                String sql = "SELECT * FROM 'tblunitmaster' WHERE unitcode=" + obj.getString("unitcode");
                                if (!((mDb.rawQuery(sql, null)).moveToFirst())) {
                                    int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                    sql = "INSERT INTO 'tblunitmaster' VALUES('" + gc +"','" + obj.getString("unitname").replaceAll("'","''")+"'," +
                                            "'" + obj.getString("unitcode")+"','" + obj.getString("unitnametamil").replaceAll("'","''")+"'," +
                                            "'" + obj.getString("makerid")+"','" + obj.getString("noofdecimals")+"'," +
                                            "'" + obj.getString("status")+"','" + obj.getString("createddate")+"'," +
                                            "'" + obj.getString("updateddate")+"')";
                                    mDb.execSQL(sql);

                                }

                            }
                            else {
                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                String sql1 = "UPDATE 'tblunitmaster' SET autonum='" + gc +"',unitname='" + obj.getString("unitname").replaceAll("'","''")+"'," +
                                        "unitcode='" + obj.getString("unitcode")+"',unitnametamil='" + obj.getString("unitnametamil").replaceAll("'","''")+"'," +
                                        "makerid='" + obj.getString("makerid")+"',noofdecimals='" + obj.getString("noofdecimals")+"'," +
                                        "status='" + obj.getString("status")+"',createddate='" + obj.getString("createddate")+"'," +
                                        "updateddate='" + obj.getString("updateddate")+"' " +
                                        " WHERE unitcode=" + obj.getString("unitcode");
                                mDb.execSQL(sql1);

                            }
                        } catch (JSONException ex) {
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }


    //sync vanstock
    //coulmn name
//    ` `"autonum"
//        "transactionno"
//        "transactiondate"
//        "vancode"
//        "itemcode"
//        "inward"
//        "outward"
//        "type"
//        "refno"
//        "createddate"

    public void syncvanstocktransaction (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    String sql1="DELETE FROM 'tblstocktransaction'";
                    mDb.execSQL(sql1);
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);
                            int varautonum=i+1;
                            String sql ="";
                            if(obj.getInt("syncautonum")==0){
                                sql = "INSERT INTO 'tblstocktransaction' (transactionno,transactiondate,vancode,itemcode,inward,outward,type," +
                                        " refno,createddate,flag,companycode,op,financialyearcode,autonum) VALUES('" + obj.getString("transactionno")+"','" + obj.getString("transactiondate")+"'," +
                                        "'" + obj.getString("vancode")+"','" + obj.getString("itemcode")+"'," +
                                        "'" + obj.getString("inward")+"','" + obj.getString("outward")+"','" + obj.getString("type")+"','" + obj.getString("refno")+"'," +
                                        "'" + obj.getString("createddate")+"',1111,'" + obj.getString("companycode")+"','" + obj.getString("op")+"','" + obj.getString("financialyearcode")+"',(select coalesce(max(autonum),0)+1 from tblstocktransaction)+"+varautonum+")";
                            }
                            else{
                                sql = "INSERT INTO 'tblstocktransaction' (transactionno,transactiondate,vancode,itemcode,inward,outward,type," +
                                        " refno,createddate,flag,companycode,op,financialyearcode,autonum) VALUES('" + obj.getString("transactionno")+"','" + obj.getString("transactiondate")+"'," +
                                        "'" + obj.getString("vancode")+"','" + obj.getString("itemcode")+"'," +
                                        "'" + obj.getString("inward")+"','" + obj.getString("outward")+"','" + obj.getString("type")+"','" + obj.getString("refno")+"'," +
                                        "'" + obj.getString("createddate")+"',1111,'" + obj.getString("companycode")+"','" + obj.getString("op")+"','" + obj.getString("financialyearcode")+"','" + obj.getString("syncautonum")+"')";
                            }

                            mDb.execSQL(sql);

                        } catch (JSONException ex) {

                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }

    public  void  DeleteCashNotPaid(){
        String sql1="DELETE FROM 'tblcashnotpaiddetails'";
        mDb.execSQL(sql1);
    }

    public void synccashnotpaiddetails(JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    String sql1="DELETE FROM 'tblcashnotpaiddetails'";
                    mDb.execSQL(sql1);
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);
                            int varautonum=i+1;
                            String sql ="";
                            sql = "INSERT INTO 'tblcashnotpaiddetails' (autonum,transactionno,bookingno,customercode,grandtotal,customernametamil,schedulecode," +
                                    " areanametamil) VALUES('"+varautonum+"','" + obj.getString("transactionno")+"'," +
                                    "'" + obj.getString("bookingno")+"','" + obj.getString("customercode")+"'," +
                                    "'" + obj.getString("grandtotal")+"','" + obj.getString("customernametamil")+"'," +
                                    " '" + obj.getString("schedulecode")+"','" + obj.getString("areanametamil")+"' )";

                            mDb.execSQL(sql);

                        } catch (JSONException ex) {

                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }


    //sync vehicle master
    // column name
//
//    "autonum"
//            "vehiclecode"
//            "vehiclename"
//            "modelandyear"
//            "registrationno"
//            "capacity"
//            "fc"
//            "permit"
//            "insurance"
//            "documentupload"
//            "status"
//            "makerid"
//            "createddate"
//            "updateddate"

    public void syncvehiclemaster (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);
                            if (obj.getString("process").equals("Insert")) {
                                String sql = "SELECT * FROM 'tblvehiclemaster' WHERE vehiclecode=" + obj.getString("vehiclecode");
                                if (!((mDb.rawQuery(sql, null)).moveToFirst())) {
                                    int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                    sql = "INSERT INTO 'tblvehiclemaster' VALUES('" + gc +"','" + obj.getString("vehiclecode")+"'," +
                                            "'" + obj.getString("vehiclename").replaceAll("'","''")+"','" + obj.getString("modelandyear")+"'," +
                                            "'" + obj.getString("registrationno")+"','" + obj.getString("capacity")+"'," +
                                            "'" + obj.getString("fc")+"','" + obj.getString("permit")+"','" + obj.getString("insurance")+"'," +
                                            "'" + obj.getString("documentupload")+"','" + obj.getString("status")+"','" + obj.getString("makerid")+"'," +
                                            "'" + obj.getString("createddate")+"','" + obj.getString("updateddate")+"')";
                                    mDb.execSQL(sql);

                                }

                            } else {
                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                String sql1 = "UPDATE 'tblvehiclemaster' SET autonum='" + gc +"'," +
                                        "vehiclecode='" + obj.getString("vehiclecode")+"',vehiclename='" + obj.getString("vehiclename").replaceAll("'","''")+"'," +
                                        "modelandyear='" + obj.getString("modelandyear")+"'," +
                                        "registrationno='" + obj.getString("registrationno")+"'," +
                                        "capacity='" + obj.getString("capacity")+"'," +
                                        "fc='" + obj.getString("fc")+"',permit='" + obj.getString("permit")+"'," +
                                        "insurance='" + obj.getString("insurance")+"',documentupload='" + obj.getString("documentupload")+"'," +
                                        "status='" + obj.getString("status")+"',makerid='" + obj.getString("makerid")+"'," +
                                        "createddate='" + obj.getString("createddate")+"',updateddate='" + obj.getString("updateddate")+"' " +
                                        " WHERE vehiclecode=" + obj.getString("vehiclecode");
                                mDb.execSQL(sql1);

                            }
                        } catch (JSONException ex) {
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }

    //sync voucher setiings
    //column name
//
//    "autonum"
//            "vancode"
//            "companycode"
//            "billtypecode"
//            "prefix"
//            "suffix"
//            "startingno"
//            "noofdigit"
//            "type"
//            "financialyearcode"
//            "createddate"
//            "updateddate"


    public void syncvouchersettings (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    String sql1="DELETE FROM 'tblvouchersettings'";
                    mDb.execSQL(sql1);
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);

                            int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                            String sql = "INSERT INTO 'tblvouchersettings' VALUES('" + gc +"'," +
                                    "'" + obj.getString("vancode")+"','" + obj.getString("companycode")+"'," +
                                    "'" + obj.getString("billtypecode")+"','" + obj.getString("prefix")+"'," +
                                    "'" + obj.getString("suffix")+"','" + obj.getString("startingno")+"'," +
                                    "'" + obj.getString("noofdigit")+"','" + obj.getString("type")+"'," +
                                    "'" + obj.getString("financialyearcode")+"','" + obj.getString("createddate")+"'," +
                                    "'" + obj.getString("updateddate")+"')";
                            mDb.execSQL(sql);

                        } catch (JSONException ex) {
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }

    // sync expenses master
    // column name
//            "autonum"
//            "transactionno"
//            "transactiondate"
//            "expensesheadcode"
//            "amount"
//            "remarks"
//            "makerid"
//            "createdate"
//            "schedulecode"
//            "financialyearcode"
//            "vancode"
//            "flag"
    public void syncexpensesmaster (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");

                    String sqlcount = "SELECT coalesce(count(*),0) FROM 'tblexpenses'";
                    Cursor mCur = mDb.rawQuery(sqlcount, null);
                    if (mCur.getCount() > 0)
                    {
                        mCur.moveToFirst();
                    }
                    if (mCur.getInt(0) == 0) {
                        for (int i = 0; i < json_category.length(); i++) {
                            try {
                                JSONObject obj = (JSONObject) json_category.get(i);


                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                String sql = "INSERT INTO 'tblexpenses' (autonum,transactionno,transactiondate,expensesheadcode,amount,remarks," +
                                        "makerid,createdate,schedulecode, financialyearcode,vancode,flag,syncstatus) VALUES('" + gc + "','" + obj.getString("transactionno") + "'," +
                                        "'" + obj.getString("transactiondate") + "'," +
                                        "'" + obj.getString("expensesheadcode") + "','" + obj.getString("amount") + "'," +
                                        "'" + obj.getString("remarks") + "','" + obj.getString("makerid") + "'," +
                                        "'" + obj.getString("createdate") + "','" + obj.getString("schedulecode") + "'," +
                                        "'" + obj.getString("financialyearcode") + "','" + obj.getString("vancode") + "'," +
                                        "'" + obj.getString("flag") + "',1)";
                                mDb.execSQL(sql);


                            } catch (JSONException ex) {
                                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            }
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }

    //Syncashclose
    public void synccashclose (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");

                    String sqlcount = "SELECT coalesce(count(*),0) FROM 'tblclosecash'";
                    Cursor mCur = mDb.rawQuery(sqlcount, null);
                    if (mCur.getCount() > 0)
                    {
                        mCur.moveToFirst();
                    }
                    if (mCur.getInt(0) == 0) {
                        for (int i = 0; i < json_category.length(); i++) {
                            try {
                                JSONObject obj = (JSONObject) json_category.get(i);


                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                String sql = "INSERT INTO 'tblclosecash'(autonum,closedate,vancode,schedulecode,makerid,createddate,status,flag,paidparties,expenseentries)" +
                                        " VALUES('" + gc + "','" + obj.getString("closedate") + "'," +
                                        "'" + obj.getString("vancode") + "'," +
                                        "'" + obj.getString("schedulecode") + "','" + obj.getString("makerid") + "'," +
                                        "'" + obj.getString("createddate") + "','2','11', " +
                                        "'" + obj.getString("paidparties") + "','" + obj.getString("expenseentries") + "' )";
                                mDb.execSQL(sql);


                            } catch (JSONException ex) {
                                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            }
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }

    //Synsalesclose
    public void syncsalesclose (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");

                    String sqlcount = "SELECT coalesce(count(*),0) FROM 'tblclosesales'";
                    Cursor mCur = mDb.rawQuery(sqlcount, null);
                    if (mCur.getCount() > 0)
                    {
                        mCur.moveToFirst();
                    }
                    if (mCur.getInt(0) == 0) {
                        for (int i = 0; i < json_category.length(); i++) {
                            try {
                                JSONObject obj = (JSONObject) json_category.get(i);


                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                String sql = "INSERT INTO 'tblclosesales'(autonum,closedate,vancode,schedulecode,makerid,createddate,flag) " +
                                        "VALUES('" + gc + "'," +
                                        " '" + obj.getString("closedate") + "'," +
                                        "'" + obj.getString("vancode") + "'," +
                                        "'" + obj.getString("schedulecode") + "','" + obj.getString("makerid") + "'," +
                                        "'" + obj.getString("createddate") + "','11')";
                                mDb.execSQL(sql);


                            } catch (JSONException ex) {
                                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            }
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }

    //Syncashreport
    public void synccashreport (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");

                    String sqlcount = "SELECT coalesce(count(*),0) FROM 'tblcashreport'";
                    Cursor mCur = mDb.rawQuery(sqlcount, null);
                    if (mCur.getCount() > 0)
                    {
                        mCur.moveToFirst();
                    }
                    if (mCur.getInt(0) == 0) {
                        for (int i = 0; i < json_category.length(); i++) {
                            try {
                                JSONObject obj = (JSONObject) json_category.get(i);


                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                String sql = "INSERT INTO 'tblcashreport'(autonum,schedulecode,vancode,sales,salesreturn,advance,receipt,expenses,cash,denominationcash,makerid,createddate,flag) " +
                                        " VALUES('" + gc + "'," +
                                        " '" + obj.getString("schedulecode") + "'," +
                                        "'" + obj.getString("vancode") + "'," +
                                        "'" + obj.getString("sales") + "','" + obj.getString("salesreturn") + "'," +
                                        "'" + obj.getString("advance") + "'," +
                                        "'" + obj.getString("receipt") + "'," +
                                        "'" + obj.getString("expenses") + "'," +
                                        "'" + obj.getString("cash") + "'," +
                                        "'" + obj.getString("denominationcash") + "'," +
                                        "'" + obj.getString("makerid") + "'," +
                                        "'" + obj.getString("createddate") + "'," +
                                        " '11')";
                                mDb.execSQL(sql);


                            } catch (JSONException ex) {
                                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            }
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }

    //Syndenomination
    public void syncdenomination (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");

                    String sqlcount = "SELECT coalesce(count(*),0) FROM 'tbldenomination'";
                    Cursor mCur = mDb.rawQuery(sqlcount, null);
                    if (mCur.getCount() > 0)
                    {
                        mCur.moveToFirst();
                    }
                    if (mCur.getInt(0) == 0) {
                        for (int i = 0; i < json_category.length(); i++) {
                            try {
                                JSONObject obj = (JSONObject) json_category.get(i);


                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                String sql = "INSERT INTO 'tbldenomination' VALUES('" + gc + "'," +
                                        " '" + obj.getString("vancode") + "'," +
                                        "'" + obj.getString("schedulecode") + "'," +
                                        "'" + obj.getString("currencycode") + "','" + obj.getString("qty") + "'," +
                                        "'" + obj.getString("amount") + "'," +
                                        "'" + obj.getString("makerid") + "'," +
                                        "'" + obj.getString("createddate") + "'," +
                                        " '11')";
                                mDb.execSQL(sql);


                            } catch (JSONException ex) {
                                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            }
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }


    //sync receipt
    // column name

//    "autonum"
//            "transactionno"
//            "receiptdate"
//            "prefix"
//            "suffix"
//            "voucherno"
//            "refno"
//            "companycode"
//            "vancode"
//            "customercode"
//            "schedulecode"
//            "receiptremarkscode"
//            "receiptmode"
//            "chequerefno"
//            "amount"
//            "makerid"
//            "createddate"
//            "financialyearcode"
//            "flag"

    public void syncreceipt (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    String sqlcount = "SELECT coalesce(count(*),0) FROM 'tblreceipt'";
                    Cursor mCur = mDb.rawQuery(sqlcount, null);
                    if (mCur.getCount() > 0)
                    {
                        mCur.moveToFirst();
                    }
                    if (mCur.getInt(0) == 0) {
                        for (int i = 0; i < json_category.length(); i++) {
                            try {
                                JSONObject obj = (JSONObject) json_category.get(i);


                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                String sql = "INSERT INTO 'tblreceipt' (autonum,transactionno,receiptdate, prefix,suffix,voucherno,refno,companycode,vancode,customercode, " +
                                        "schedulecode,receiptremarkscode,receiptmode,chequerefno,amount, makerid,createddate,financialyearcode,flag,note,syncstatus," +
                                        "receipttime,chequebankname,chequedate,transactionid,venderid,type) VALUES('" + gc + "'," +
                                        "'" + obj.getString("transactionno") + "'," +
                                        "'" + obj.getString("receiptdate") + "','" + obj.getString("prefix") + "'," +
                                        "'" + obj.getString("suffix") + "','" + obj.getString("voucherno") + "'," +
                                        "'" + obj.getString("refno") + "','" + obj.getString("companycode") + "'," +
                                        "'" + obj.getString("vancode") + "','" + obj.getString("customercode") + "'," +
                                        "'" + obj.getString("schedulecode") + "','" + obj.getString("receiptremarkscode") + "'," +
                                        "'" + obj.getString("receiptmode") + "','" + obj.getString("chequerefno") + "'," +
                                        "'" + obj.getString("amount") + "','" + obj.getString("makerid") + "'," +
                                        "'" + obj.getString("createddate") + "','" + obj.getString("financialyearcode") + "'," +
                                        "'" + obj.getString("flag") + "','" + obj.getString("note") + "',1," +
                                        "'"+obj.getString("receipttime")+"','"+obj.getString("chequebankname")+"'," +
                                        "'"+obj.getString("chequedate")+"','"+obj.getString("transactionid")+"'," +
                                        "'"+obj.getString("venderid")+"','"+obj.getString("type")+"')";

                                mDb.execSQL(sql);


                            } catch (JSONException ex) {
                                Log.w(String.valueOf(ex),"errr");
                                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            }
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                Log.w(String.valueOf(ex),"errr===>");
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }

    // sync sales return
    // column name

//    "autonum"
//            "companycode"
//            "vancode"
//            "transactionno"
//            "billno"
//            "refno"
//            "prefix"
//            "suffix"
//            "billdate"
//            "customercode"
//            "billtypecode"
//            "gstin"
//            "schedulecode"
//            "subtotal"
//            "discount"
//            "totaltaxamount"
//            "grandtotal"
//            "billcopystatus"
//            "cashpaidstatus"
//            "flag"
//            "makerid"
//            "createddate"
//            "updateddate"
//            "bitmapimage"
//            "financialyearcode"
//            "remarks"
//            "bookingno"

    public void syncsalesreturn (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    String sqlcount = "SELECT coalesce(count(*),0) FROM 'tblsalesreturn'";

                    Cursor mCur = mDb.rawQuery(sqlcount, null);
                    if (mCur.getCount() > 0)
                    {
                        mCur.moveToFirst();
                    }
                    if (mCur.getInt(0) == 0) {
                        for(int i=0;i<json_category.length();i++) {
                            try {
                                JSONObject obj = (JSONObject) json_category.get(i);

                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                String sql = "INSERT INTO 'tblsalesreturn'(autonum,companycode,vancode,transactionno,billno,refno,prefix,suffix,billdate,customercode,billtypecode,gstin," +
                                        "schedulecode,subtotal,discount,totaltaxamount,grandtotal,billcopystatus,cashpaidstatus,flag,makerid,createddate,updateddate,bitmapimage," +
                                        "financialyearcode,remarks,bookingno,syncstatus,salestime,beforeroundoff) VALUES('" + gc + "','" + obj.getString("companycode") + "'," +
                                        "'" + obj.getString("vancode") + "','" + obj.getString("transactionno") + "'," +
                                        "'" + obj.getString("billno") + "','" + obj.getString("refno") + "'," +
                                        "'" + obj.getString("prefix") + "','" + obj.getString("suffix") + "'," +
                                        "'" + obj.getString("billdate") + "','" + obj.getString("customercode") + "'," +
                                        "'" + obj.getString("billtypecode") + "','" + obj.getString("gstin") + "'," +
                                        "'" + obj.getString("schedulecode") + "','" + obj.getString("subtotal") + "'," +
                                        "'" + obj.getString("discount") + "','" + obj.getString("totaltaxamount") + "'," +
                                        "'" + obj.getString("grandtotal") + "','" + obj.getString("billcopystatus") + "'," +
                                        "'" + obj.getString("cashpaidstatus") + "','" + obj.getString("flag") + "'," +
                                        "'" + obj.getString("makerid") + "','" + obj.getString("createddate") + "'," +
                                        "'" + obj.getString("updateddate") + "','" + obj.getString("bitmapimage") + "'," +
                                        "'" + obj.getString("financialyearcode") + "','" + obj.getString("remarks") + "'," +
                                        "'" + obj.getString("bookingno") + "',1,'" + obj.getString("salestime") + "','" + obj.getString("beforeroundoff") + "')";
                                mDb.execSQL(sql);


                            } catch (JSONException ex) {
                                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            }
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }


    //sync sales return item details
    //column name

//    "autonum"
//            "transactionno"
//            "companycode"
//            "itemcode"
//            "qty"
//            "price"
//            "discount"
//            "amount"
//            "cgst"
//            "sgst"
//            "igst"
//            "cgstamt"
//            "sgstamt"
//            "igstamt"
//            "freeitemstatus"
//            "makerid"
//            "createddate"
//            "updateddate"

    public void syncsalesreturnitemdetails (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    String sqlcount = "SELECT coalesce(count(*),0) FROM 'tblsalesreturnitemdetails'";
                    Cursor mCur = mDb.rawQuery(sqlcount, null);
                    if (mCur.getCount() > 0)
                    {
                        mCur.moveToFirst();
                    }
                    if (mCur.getInt(0) == 0) {
                        for (int i = 0; i < json_category.length(); i++) {
                            try {
                                JSONObject obj = (JSONObject) json_category.get(i);

                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                String sql = "INSERT INTO 'tblsalesreturnitemdetails' VALUES('" + gc + "','" + obj.getString("transactionno") + "'," +
                                        "'" + obj.getString("companycode") + "','" + obj.getString("itemcode") + "','" + obj.getString("qty") + "'," +
                                        "'" + obj.getString("weight") + "','" + obj.getString("price") + "','" + obj.getString("discount") + "'," +
                                        "'" + obj.getString("amount") + "','" + obj.getString("cgst") + "','" + obj.getString("sgst") + "'," +
                                        "'" + obj.getString("igst") + "','" + obj.getString("cgstamt") + "','" + obj.getString("sgstamt") + "'," +
                                        "'" + obj.getString("igstamt") + "','" + obj.getString("freeitemstatus") + "','" + obj.getString("makerid") + "'," +
                                        "'" + obj.getString("createddate") + "','" + obj.getString("updateddate") + "','" + obj.getString("bookingno") + "'," +
                                        " '" + obj.getString("financialyearcode") + "','" + obj.getString("vancode") + "',2)";
                                mDb.execSQL(sql);


                            } catch (JSONException ex) {

                                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            }
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }


    // sync sales
    // column name
//    "autonum"
//            "companycode"
//            "vancode"
//            "transactionno"
//            "billno"
//            "refno"
//            "prefix"
//            "suffix"
//            "billdate"
//            "customercode"
//            "billtypecode"
//            "gstin"
//            "schedulecode"
//            "subtotal"
//            "discount"
//            "totaltaxamount"
//            "grandtotal"
//            "billcopystatus"
//            "cashpaidstatus"
//            "flag"
//            "makerid"
//            "createddate"
//            "updateddate"
//            "bitmapimage"
//            "financialyearcode"
//            "remarks"
//            "bookingno"
    public void syncsales (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    String sqlcount = "SELECT coalesce(count(*),0) FROM 'tblsales'";
                    Cursor mCur = mDb.rawQuery(sqlcount, null);
                    if (mCur.getCount() > 0)
                    {
                        mCur.moveToFirst();
                    }
                    if (mCur.getInt(0) == 0) {
                        for(int i=0;i<json_category.length();i++) {
                            try {
                                JSONObject obj = (JSONObject) json_category.get(i);

                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                String sql = "INSERT INTO 'tblsales' (autonum,companycode,vancode,transactionno,billno,refno,prefix,suffix,billdate,customercode,billtypecode,gstin," +
                                        "schedulecode,subtotal,discount,totaltaxamount,grandtotal,billcopystatus,cashpaidstatus,flag,makerid,createddate,updateddate,bitmapimage," +
                                        "financialyearcode,remarks,bookingno,syncstatus,imgflag,salestime,beforeroundoff,lunchflag,einvoiceurl,irn_no,ack_no,ackdate" +
                                        ",einvoice_status,einvoiceresponse,einvoiceqrcodeurl,ratediscount,schemeapplicable) VALUES('" + gc + "','" + obj.getString("companycode") + "'," +
                                        "'" + obj.getString("vancode") + "','" + obj.getString("transactionno") + "'," +
                                        "'" + obj.getString("billno") + "','" + obj.getString("refno") + "'," +
                                        "'" + obj.getString("prefix") + "','" + obj.getString("suffix") + "'," +
                                        "'" + obj.getString("billdate") + "','" + obj.getString("customercode") + "'," +
                                        "'" + obj.getString("billtypecode") + "','" + obj.getString("gstin") + "','" + obj.getString("schedulecode") + "'," +
                                        "'" + obj.getString("subtotal") + "','" + obj.getString("discount") + "','" + obj.getString("totaltaxamount") + "'," +
                                        "'" + obj.getString("grandtotal") + "','" + obj.getString("billcopystatus") + "','" + obj.getString("cashpaidstatus") + "'," +
                                        "'" + obj.getString("flag") + "','" + obj.getString("makerid") + "','" + obj.getString("createddate") + "'," +
                                        "'" + obj.getString("updateddate") + "','" + obj.getString("bitmapimage") + "','" + obj.getString("financialyearcode") + "'," +
                                        "'" + obj.getString("remarks") + "','" + obj.getString("bookingno") + "',1,1,'" + obj.getString("salestime") + "','" + obj.getString("beforeroundoff") + "'," +
                                        "'" + obj.getString("lunchflag") + "','" + obj.getString("einvoiceurl") + "','" + obj.getString("irn_no") + "'" +
                                        ",'" + obj.getString("ack_no") + "','" + obj.getString("ackdate") + "'," + obj.getInt("einvoice_status") + "," +
                                        "'" + obj.getString("einvoiceresponse") + "','" + obj.getString("einvoiceqrcodeurl") + "'," +
                                        "'" + obj.getString("ratediscount") + "','" +obj.getString("schemeapplicable")+ "')";

                                mDb.execSQL(sql);


                            } catch (JSONException ex) {

                                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            }
                        }
                    }else{
                        for(int i=0;i<json_category.length();i++) {
                            try {
                                JSONObject obj = (JSONObject) json_category.get(i);

                                String exequery = "UPDATE tblsales set flag='"+obj.getString("flag")+"',einvoiceurl='"+obj.getString("einvoiceurl")+"'," +
                                        " irn_no='"+obj.getString("irn_no")+"', ack_no='"+obj.getString("ack_no")+"',ackdate='"+obj.getString("ackdate")+"'" +
                                        ", einvoice_status='"+obj.getInt("einvoice_status")+"',einvoiceresponse='"+obj.getString("einvoiceresponse")+"'" +
                                        ",einvoiceqrcodeurl='"+obj.getString("einvoiceqrcodeurl")+"',ratediscount='"+ obj.getString("ratediscount")+"'," +
                                        "schemeapplicable='" +obj.getString("schemeapplicable")+ "'" +
                                        " where transactionno='"+obj.getString("transactionno")+"' and" +
                                        " financialyearcode ='"+obj.getString("financialyearcode")+"' and companycode='"+obj.getString("companycode")+"' ";
                                mDb.execSQL(exequery);

                            } catch (JSONException ex) {

                                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            }
                        }
//
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }


    //sync sales order
    public void syncsalesorder (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    String sqlcount = "SELECT coalesce(count(*),0) FROM 'tblsalesorder'";
                    Cursor mCur = mDb.rawQuery(sqlcount, null);
                    if (mCur.getCount() > 0)
                    {
                        mCur.moveToFirst();
                    }
                    if (mCur.getInt(0) == 0) {
                        for(int i=0;i<json_category.length();i++) {
                            try {
                                JSONObject obj = (JSONObject) json_category.get(i);

                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                String sql = "INSERT INTO 'tblsalesorder' (autonum,companycode,vancode,transactionno,billno,refno,prefix,suffix,billdate,customercode,billtypecode,gstin," +
                                        "schedulecode,subtotal,discount,totaltaxamount,grandtotal,billcopystatus,cashpaidstatus,flag,makerid,createddate,updateddate,bitmapimage," +
                                        "financialyearcode,remarks,bookingno,syncstatus,imgflag,salestime,beforeroundoff,transportid,status,transportmode) VALUES('" + gc + "','" + obj.getString("companycode") + "'," +
                                        "'" + obj.getString("vancode") + "','" + obj.getString("transactionno") + "'," +
                                        "'" + obj.getString("billno") + "','" + obj.getString("refno") + "'," +
                                        "'" + obj.getString("prefix") + "','" + obj.getString("suffix") + "'," +
                                        "'" + obj.getString("billdate") + "','" + obj.getString("customercode") + "'," +
                                        "'" + obj.getString("billtypecode") + "','" + obj.getString("gstin") + "','" + obj.getString("schedulecode") + "'," +
                                        "'" + obj.getString("subtotal") + "','" + obj.getString("discount") + "','" + obj.getString("totaltaxamount") + "'," +
                                        "'" + obj.getString("grandtotal") + "','" + obj.getString("billcopystatus") + "','" + obj.getString("cashpaidstatus") + "'," +
                                        "'" + obj.getString("flag") + "','" + obj.getString("makerid") + "','" + obj.getString("createddate") + "'," +
                                        "'" + obj.getString("updateddate") + "','" + obj.getString("bitmapimage") + "','" + obj.getString("financialyearcode") + "'," +
                                        "'" + obj.getString("remarks") + "'," +
                                        "'" + obj.getString("bookingno") + "',1,1," +
                                        "'" + obj.getString("salestime") + "'," +
                                        "'" + obj.getString("beforeroundoff") + "','" + obj.getString("transportid") + "','" + obj.getString("status") + "'," +
                                        "'" + obj.getString("transportmode") + "')";
                                mDb.execSQL(sql);




                            } catch (JSONException ex) {

                                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            }
                        }
                    }else{
                        for(int i=0;i<json_category.length();i++) {
                            try {
                                JSONObject obj = (JSONObject) json_category.get(i);

                                String sql="UPDATE tblsalesorder set flag='"+obj.getString("flag")+"',status='" + obj.getString("status") + "' " +
                                        "where schedulecode='"+ obj.getString("schedulecode") +"' and vancode='"+obj.getString("vancode")+"' " +
                                        "and transactionno='"+obj.getString("transactionno")+"'";
                                mDb.execSQL(sql);
                                /*String updatesql="UPDATE tblsalesorder set status='" + obj.getString("status") + "' " +
                                        "where vancode= '" + obj.getString("vancode") + "' and " +
                                        "billno= '" + obj.getString("billno") + "' and " +
                                        "billdate = '" + obj.getString("billdate") + "' ";
                                mDb.execSQL(updatesql);*/

                            } catch (JSONException ex) {

                                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            }
                        }

                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }
    //sync salesitemdetails
    // column name
//    "autonum"
//            "transactionno"
//            "companycode"
//            "itemcode"
//            "qty"
//            "price"
//            "discount"
//            "amount"
//            "cgst"
//            "sgst"
//            "igst"
//            "cgstamt"
//            "sgstamt"
//            "igstamt"
//            "freeitemstatus"
//            "makerid"
//            "createddate"
//            "updateddate"

    public void syncsalesitemdetails (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    String sqlcount = "SELECT coalesce(count(*),0) FROM 'tblsalesitemdetails'";
                    Cursor mCur = mDb.rawQuery(sqlcount, null);
                    if (mCur.getCount() > 0)
                    {
                        mCur.moveToFirst();
                    }
                    if (mCur.getInt(0) == 0) {
                        for(int i=0;i<json_category.length();i++) {
                            try {
                                JSONObject obj = (JSONObject) json_category.get(i);

                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                String sql = "INSERT INTO 'tblsalesitemdetails' VALUES('" + gc + "'," +
                                        "'" + obj.getString("transactionno") + "','" + obj.getString("companycode") + "','" + obj.getString("itemcode") + "'," +
                                        "'" + obj.getString("qty") + "','" + obj.getString("weight") + "','" + obj.getString("price") + "'," +
                                        "'" + obj.getString("discount") + "','" + obj.getString("amount") + "','" + obj.getString("cgst") + "'," +
                                        "'" + obj.getString("sgst") + "','" + obj.getString("igst") + "','" + obj.getString("cgstamt") + "'," +
                                        "'" + obj.getString("sgstamt") + "','" + obj.getString("igstamt") + "','" + obj.getString("freeitemstatus") + "'," +
                                        "'" + obj.getString("makerid") + "','" + obj.getString("createddate") + "','" + obj.getString("updateddate") + "'" +
                                        ",'" + obj.getString("bookingno") + "','" + obj.getString("financialyearcode") + "','"+obj.getString("vancode")+"'" +
                                        ",2,'" + obj.getString("ratediscount") + "','" +obj.getString("schemeapplicable")+ "','"+obj.getString("orgprice")+"')";
                                mDb.execSQL(sql);


                            } catch (JSONException ex) {
                                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            }
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }

    //sync salesorder
    public void syncsalesorderitemdetails (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    String sqlcount = "SELECT coalesce(count(*),0) FROM 'tblsalesorderitemdetails'";
                    Cursor mCur = mDb.rawQuery(sqlcount, null);
                    if (mCur.getCount() > 0)
                    {
                        mCur.moveToFirst();
                    }
                    if (mCur.getInt(0) == 0) {
                        for(int i=0;i<json_category.length();i++) {
                            try {
                                JSONObject obj = (JSONObject) json_category.get(i);

                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                String sql = "INSERT INTO 'tblsalesorderitemdetails' VALUES('" + gc + "'," +
                                        "'" + obj.getString("transactionno") + "','" + obj.getString("companycode") + "','" + obj.getString("itemcode") + "'," +
                                        "'" + obj.getString("qty") + "','" + obj.getString("weight") + "','" + obj.getString("price") + "'," +
                                        "'" + obj.getString("discount") + "','" + obj.getString("amount") + "','" + obj.getString("cgst") + "'," +
                                        "'" + obj.getString("sgst") + "','" + obj.getString("igst") + "','" + obj.getString("cgstamt") + "'," +
                                        "'" + obj.getString("sgstamt") + "','" + obj.getString("igstamt") + "','" + obj.getString("freeitemstatus") + "'," +
                                        "'" + obj.getString("makerid") + "','" + obj.getString("createddate") + "','" + obj.getString("updateddate") + "'" +
                                        ",'" + obj.getString("bookingno") + "','" + obj.getString("financialyearcode") + "','"+obj.getString("vancode")+"',2)";
                                mDb.execSQL(sql);


                            } catch (JSONException ex) {
                                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            }
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }

    // sync sales schedule
    //

    public void syncsalesschedulemobile (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                   /* String sqlcount = "SELECT coalesce(count(*),0) FROM 'tblsalesschedule'";
                    Cursor mCur = mDb.rawQuery(sqlcount, null);
                    if (mCur.getCount() > 0)
                    {
                        mCur.moveToFirst();
                    }*/
                    for (int i = 0; i < json_category.length(); i++) {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);

                            //if (obj.getString("process").equals("Insert")) {
                            String sql = "SELECT * FROM 'tblsalesschedule' WHERE schedulecode='" + obj.getString("schedulecode")+"' ";
                            if (!((mDb.rawQuery(sql, null)).moveToFirst())) {
                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                sql = "INSERT INTO 'tblsalesschedule' (autonum, refno,schedulecode, scheduledate,vancode,routecode,vehiclecode,employeecode, drivercode,helpername," +
                                        "tripadvance,startingkm,endingkm,createddate, updateddate,makerid,flag,lunch_start_time,lunch_end_time) VALUES('" + gc +"','" + obj.getString("refno")+"'," +
                                        "'" + obj.getString("schedulecode")+"','" + obj.getString("scheduledate")+"','" + obj.getString("vancode")+"'," +
                                        "'" + obj.getString("routecode")+"','" + obj.getString("vehiclecode")+"','" + obj.getString("employeecode")+"'," +
                                        "'" + obj.getString("drivercode")+"','" + obj.getString("helpername")+"','" + obj.getString("tripadvance")+"'," +
                                        "'" + obj.getString("startingkm")+"','" + obj.getString("endingkm")+"','" + obj.getString("createddate")+"'," +
                                        "'" + obj.getString("updateddate")+"','" + obj.getString("makerid")+"',1," +
                                        " '" + obj.getString("lunch_start_time")+"','" + obj.getString("lunch_end_time")+"')";
                                mDb.execSQL(sql);
                            } else {
                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                String sql1 = "UPDATE 'tblsalesschedule' SET autonum='" + gc +"',schedulecode='" + obj.getString("schedulecode")+"'," +
                                        "refno='" + obj.getString("refno")+"',scheduledate='" + obj.getString("scheduledate")+"'," +
                                        "vancode='" + obj.getString("vancode")+"',routecode='" + obj.getString("routecode")+"'," +
                                        "vehiclecode='" + obj.getString("vehiclecode")+"',employeecode='" + obj.getString("employeecode")+"'," +
                                        "drivercode='" + obj.getString("drivercode")+"',helpername='" + obj.getString("helpername")+"'," +
                                        "tripadvance='" + obj.getString("tripadvance")+"',startingkm='" + obj.getString("startingkm")+"'," +
                                        "endingkm='" + obj.getString("endingkm")+"',createddate='" + obj.getString("createddate")+"'," +
                                        "updateddate='" + obj.getString("updateddate")+"',makerid='" + obj.getString("makerid")+"', " +
                                        " lunch_start_time='" + obj.getString("lunch_start_time")+"',lunch_end_time='" + obj.getString("lunch_end_time")+"' " +
                                        " WHERE schedulecode='" + obj.getString("schedulecode")+"' ";
                                mDb.execSQL(sql1);
                            }

                            // }

                        } catch (JSONException ex) {

                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }


    //Syncnilstock
    public void syncnilstock (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    String sqlcount = "SELECT coalesce(count(*),0) as count FROM 'tblnilstocktransaction'";
                    Cursor mCur = mDb.rawQuery(sqlcount, null);
                    if (mCur.getCount() > 0)
                    {
                        mCur.moveToFirst();
                    }
                    if(mCur.getString(0).equals("0")) {
                        for (int i = 0; i < json_category.length(); i++) {
                            try {
                                JSONObject obj = (JSONObject) json_category.get(i);

                                //if (obj.getString("process").equals("Insert")) {
                                // String sql = "SELECT * FROM 'tblnilstocktransaction' WHERE schedulecode='" + obj.getString("schedulecode") + "' ";
                                //if (!((mDb.rawQuery(sql, null)).moveToFirst())) {
                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                String sql = "INSERT INTO 'tblnilstocktransaction' (autonum, vancode,schedulecode, salestransactionno,salesbookingno,salesfinacialyearcode" +
                                        " ,salescustomercode,salesitemcode, createddate,flag," +
                                        "syncflag) VALUES('" + gc + "','" + obj.getString("vancode") + "'," +
                                        "'" + obj.getString("schedulecode") + "','" + obj.getString("salestransactionno") + "'," +
                                        " '" + obj.getString("salesbookingno") + "'," +
                                        "'" + obj.getString("salesfinacialyearcode") + "','" + obj.getString("salescustomercode") + "'," +
                                        " '" + obj.getString("salesitemcode") + "'," +
                                        "'" + obj.getString("createddate") + "','" + obj.getString("flag") + "' ,1 )";
                                mDb.execSQL(sql);
                                //} else {
                                   /* int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");
                                    String sql1 = "UPDATE 'tblnilstocktransaction' SET autonum='" + gc + "',schedulecode='" + obj.getString("schedulecode") + "'," +
                                            "refno='" + obj.getString("refno") + "',scheduledate='" + obj.getString("scheduledate") + "'," +
                                            "vancode='" + obj.getString("vancode") + "',routecode='" + obj.getString("routecode") + "'," +
                                            "vehiclecode='" + obj.getString("vehiclecode") + "',employeecode='" + obj.getString("employeecode") + "'," +
                                            "drivercode='" + obj.getString("drivercode") + "',helpername='" + obj.getString("helpername") + "'," +
                                            "tripadvance='" + obj.getString("tripadvance") + "',startingkm='" + obj.getString("startingkm") + "'," +
                                            "endingkm='" + obj.getString("endingkm") + "',createddate='" + obj.getString("createddate") + "'," +
                                            "updateddate='" + obj.getString("updateddate") + "',makerid='" + obj.getString("makerid") + "', " +
                                            " lunch_start_time='" + obj.getString("lunch_start_time") + "',lunch_end_time='" + obj.getString("lunch_end_time") + "' " +
                                            " WHERE schedulecode='" + obj.getString("schedulecode") + "' ";
                                    mDb.execSQL(sql1);
                                }*/

                                // }


                            } catch (JSONException ex) {

                                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            }
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }

    //sync order details

    // sync sales schedule
    //

    public void syncorderdetails (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);
                            if (obj.getString("process").equals("Insert")) {


                                String sqlcount = "SELECT coalesce(count(*),0) FROM 'tblorderdetails'";
                                Cursor mCur = mDb.rawQuery(sqlcount, null);
                                if (mCur.getCount() > 0)
                                {
                                    mCur.moveToFirst();
                                }
                                if (mCur.getInt(0) == 0) {
                                    int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                    String sql = "INSERT INTO 'tblorderdetails'(autonum,vancode,schedulecode,itemcode,qty,makerid,createddate,orderdate,flag,status) VALUES('" + obj.getString("autonum")+"'," +
                                            "'" + obj.getString("vancode")+"','" + obj.getString("schedulecode")+"'," +
                                            "'" + obj.getString("itemcode")+"','" + obj.getString("qty")+"'," +
                                            "'" + obj.getString("makerid")+"','" + obj.getString("createddate")+"'," +
                                            "'" + obj.getString("orderdate") +"',1,1)";
                                    mDb.execSQL(sql);

                                }

                            }
                        } catch (JSONException ex) {

                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }
    //delete error logs in sqlite tabel
    public void DeleteErrorLogDays()
    {
        try{
            String sql = "DELETE FROM tblerrorlogdetails where date <= " +
                    "(SELECT  date('"+ preferenceMangr.pref_getString("getformatdate") +"','-' ||restrictmobileappdays || ' day')" +
                    " FROM tblgeneralsettings  where restrictmobileappdays<>0 ) ";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

    }


    //delete expenses in sqlite tabel
    public void DeleteExpensesDays()
    {
        try{
            String sql = "DELETE FROM tblexpenses where transactiondate <= " +
                    "(SELECT  date('"+ preferenceMangr.pref_getString("getformatdate") +"','-' ||restrictmobileappdays || ' day')" +
                    " FROM tblgeneralsettings  where restrictmobileappdays<>0 ) " +
                    " and schedulecode in (select schedulecode from tblclosecash where status='2')" +
                    " and schedulecode in (select schedulecode from tblclosesales)";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

    }

    //delete receipt in sqlite tabel
    public void DeleteReceiptDays()
    {
        try{
            String sql = "DELETE FROM tblreceipt where receiptdate <= " +
                    "(SELECT  date('"+ preferenceMangr.pref_getString("getformatdate") +"','-' ||restrictmobileappdays || ' day') " +
                    "FROM tblgeneralsettings  where restrictmobileappdays<>0) " +
                    " and schedulecode in (select schedulecode from tblclosecash where status='2')" +
                    " and schedulecode in (select schedulecode from tblclosesales) ";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

    }

    //delete salesitemdetails in sqlite table
    public void DeleteSalesItemDetailsDays()
    {
        try{
            String sql = "DELETE FROM tblsalesitemdetails  where transactionno in " +
                    " (select transactionno from tblsales where companycode=tblsalesitemdetails.companycode " +
                    " and billdate <= (SELECT  date('"+ preferenceMangr.pref_getString("getformatdate") +"','-' ||restrictmobileappdays || ' day') " +
                    " FROM tblgeneralsettings  where restrictmobileappdays<>0 ) and " +
                    " schedulecode in (select schedulecode from tblclosecash where status='2') and " +
                    " schedulecode in (select schedulecode from tblclosesales) )";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

    }


    //delete salesdetails in sqlite table
    public void DeleteSalesDays()
    {
        try{
            String sql = "DELETE FROM tblsales where billdate <= " +
                    "(SELECT  date('"+ preferenceMangr.pref_getString("getformatdate") +"','-' ||restrictmobileappdays || ' day') FROM " +
                    "tblgeneralsettings  where restrictmobileappdays<>0) "+
                    " and schedulecode in (select schedulecode from tblclosecash where status='2')" +
                    " and schedulecode in (select schedulecode from tblclosesales) ";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - DeleteSalesDays", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

    }

    //delete salesreturnitemdetails in sqlite table
    public void DeleteSalesReturnItemDetailsDays()
    {
        try{
            String sql = "DELETE FROM tblsalesreturnitemdetails  where transactionno in " +
                    "(select transactionno from tblsalesreturn where companycode=tblsalesreturnitemdetails.companycode " +
                    "and billdate <= (SELECT  date('"+ preferenceMangr.pref_getString("getformatdate") +"','-' ||restrictmobileappdays " +
                    "|| ' day') FROM tblgeneralsettings  where restrictmobileappdays<>0) " +
                    " and schedulecode in (select schedulecode from tblclosecash where status='2') " +
                    " and schedulecode in (select schedulecode from tblclosesales))";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - DeleteSalesReturnItemDetailsDays", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

    }

    //delete salesreturn in sqlite table
    public void DeleteSalesReturnDays()
    {
        try{
            String sql = "DELETE FROM tblsalesreturn where billdate <=  " +
                    "(SELECT  date('"+ preferenceMangr.pref_getString("getformatdate") +"','-' ||restrictmobileappdays || ' day') " +
                    "FROM tblgeneralsettings  where restrictmobileappdays<>0) "+
                    " and schedulecode in (select schedulecode from tblclosecash where status='2')" +
                    " and schedulecode in (select schedulecode from tblclosesales) ";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - DeleteSalesReturnDays", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

    }


    //delete sales schedule
    public void DeleteSalesSchedule()
    {
        try{
            String sql = "DELETE FROM tblsalesschedule where scheduledate <= " +
                    "(SELECT  date('"+ preferenceMangr.pref_getString("getformatdate") +"','-' ||restrictmobileappdays || ' day') " +
                    "FROM tblgeneralsettings  where restrictmobileappdays<>0) "+
                    " and schedulecode in (select schedulecode from tblclosecash where status='2')" +
                    " and schedulecode in (select schedulecode from tblclosesales) ";
            mDb.execSQL(sql);
            String sql1 = "DELETE FROM tblscheduleeway where scheduledate <= " +
                    "(SELECT  date('"+ preferenceMangr.pref_getString("getformatdate") +"','-' ||restrictmobileappdays || ' day') " +
                    "FROM tblgeneralsettings  where restrictmobileappdays<>0) "+
                    " and schedulecode in (select schedulecode from tblclosecash where status='2')" +
                    " and schedulecode in (select schedulecode from tblclosesales) ";
            mDb.execSQL(sql1);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - DeleteSalesSchedule", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

    }


    //delete order details

    public void DeleteorderDetails()
    {
        try{
            String sql = "DELETE FROM tblorderdetails where orderdate <= " +
                    "(SELECT  date('"+ preferenceMangr.pref_getString("getformatdate") +"','-' ||restrictmobileappdays || ' day') " +
                    "FROM tblgeneralsettings  where restrictmobileappdays<>0) "+
                    " and schedulecode in (select schedulecode from tblclosecash where status='2')" +
                    " and schedulecode in (select schedulecode from tblclosesales) ";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - DeleteorderDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

    }


    //Sync MAxrefNo

    public void syncmaxrefno(JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    String sql1="DELETE FROM 'tblmaxcode'";
                    mDb.execSQL(sql1);
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);

                            String sql = "SELECT * FROM 'tblmaxcode1' WHERE code="+obj.getInt("code");
                            /*if(!((mDb.rawQuery(sql, null)).moveToFirst()))
                            {*/

                            sql = "INSERT INTO 'tblmaxcode' VALUES("+obj.getInt("code")+"," +
                                    " '" + obj.getString("process")+"'," + obj.getInt("refno")+"," +
                                    "" + obj.getInt("bookingno")+"," +
                                    "" + obj.getInt("transactionno")+"," +
                                    " " + obj.getInt("companycode")+"," +
                                    " " + obj.getInt("billtypecode")+"," +
                                    " '" + obj.getString("transactiondate")+"')";
                            mDb.execSQL(sql);
                            //}
                            /*else{
                                String sql1 = "UPDATE 'tblmaxcode' SET  process='" + obj.getString("process")+
                                        "',refno='" + obj.getString("refno")+"' ,bookingno='" + obj.getString("bookingno")+"' " +
                                        " ,transactionno='" + obj.getString("transactionno")+"'" +
                                        " WHERE code="+obj.getInt("code")+" ";
                                mDb.execSQL(sql1);
                            }*/


                        } catch (JSONException ex) {

                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - syncmaxrefno", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }


    /********************PRINT FUNVTIONALITY*******************/
    //Get Sales Rep
    public Cursor GetReceiptPrint(String gettransactionno,String getfinancialyrcode)
    {
        Cursor mCur=null;
        try{
            String sql ="select b.shortname,b.companyname,coalesce(b.street,'') as street,coalesce(b.area,'') as area,coalesce(b.mobileno,'') as mobileno," +
                    "   coalesce(b.telephone,'') as telephone,coalesce(b.gstin,'') as gstin,coalesce(b.city,'') as city," +
                    "   coalesce(b.pincode,'') as pincode, coalesce(b.panno,'') as panno,a.voucherno," +
                    "   strftime('%d-%m-%Y',a.receiptdate) as receiptdate,a.customercode," +
                    "   (select customernametamil from tblcustomer where customercode=a.customercode) as customername," +
                    "   a.vancode,(select vanname from tblvanmaster where vancode=a.vancode) as vanname," +
                    "   (select gstin from tblcustomer where customercode=a.customercode) as gstin, " +
                    "   (select mobileno from tblcustomer where customercode=a.customercode) as mobileno,a.receiptmode," +
                    "   a.chequerefno,a.amount,note," +
                    "   (select employeenametamil from tblemployeemaster where employeecode=c.employeecode) as employeenametamil," +
                    "   (select employeenametamil from tblemployeemaster where employeecode=c.drivercode) as drivercode,c.helpername,receipttime," +
                    "   (select receiptremarks from tblreceiptremarks where receiptremarkscode=a.receiptremarkscode ) as remarks ," +
                    "   f.citynametamil as custcityname,e.pincode as custpincode, " +
                    "   g.statenametamil as custstatename,g.gststatecode as custgststatecode, " +
                    "   (select statename from tblstatemaster as aa where aa.gststatecode=b.gststatecode ) as compstatename," +
                    " b.gststatecode as compgststatecode,a.chequebankname,strftime('%d/%m/%Y',a.chequedate) as chequedate," +
                    "a.venderid,a.transactionid,(SELECT vendername FROM tblupivendermaster WHERE venderid = a.venderid) AS vendername " +
                    " from tblreceipt as a " +
                    " inner join tblcompanymaster as b on a.companycode=b.companycode " +
                    " inner join tblsalesschedule as c on c.schedulecode = a.schedulecode " +
                    " inner JOIN tblcustomer as d   on d.customercode=a.customercode" +
                    " inner join tblareamaster as e on e.areacode = d.areacode " +
                    " inner join tblcitymaster as f on f.citycode = e.citycode " +
                    " inner join tblstatemaster as g on g.gststatecode = f.statecode " +
                    " where transactionno='" + gettransactionno + "' and financialyearcode='" + getfinancialyrcode + "' AND a.type='Receipt'; ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetReceiptPrint", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get SAles Schedule Details
    public Cursor GetSalesScheduleDetails(String getschedulecode)
    {
        Cursor mCur=null;
        try{
            String sql ="select vancode,(select vanname from tblvanmaster where vancode=a.vancode) as vanname,\n" +
                    "employeecode,(select employeenametamil from tblemployeemaster where employeecode=a.employeecode) as employeename,\n" +
                    "drivercode, (select employeenametamil from tblemployeemaster where employeecode=a.drivercode) as drivername,helpername\n" +
                    " from tblsalesschedule as a where schedulecode='"+getschedulecode+"'  ; ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetSalesScheduleDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Sales bills for print
    public Cursor GetSalesPrint(String gettransactionno,String getfinancialyrcode)
    {
        Cursor mCur=null;
        try{
            String sql ="select b.companyname,b.street,b.area,b.city,b.pincode,b.telephone,b.mobileno,b.gstin, "+
                    "   b.fssaino,b.panno, c.vanname,d.customernametamil,a.billno,e.billtype,a.gstin,d.mobileno as cusmobile, "+
                    "   strftime('%d-%m-%Y', a.billdate) as date,a.companycode,b.shortname,a.grandtotal,a.bookingno, "+
                    "   a.billtypecode,a.salestime , g.citynametamil as custcityname,f.pincode as custpincode, "+
                    "   h.statenametamil as custstatename,h.gststatecode as custgststatecode, "+
                    "   (select statename from tblstatemaster as aa where aa.gststatecode=b.gststatecode ) as compstatename," +
                    " b.gststatecode as compgststatecode,a.einvoice_status,a.irn_no,a.ack_no,a.einvoiceurl" +
                    " from tblsales AS a "+
                    " inner join tblcompanymaster as b on a.companycode=b.companycode "+
                    " inner join tblvanmaster as c on a.vancode=c.vancode "+
                    " INNER JOIN tblcustomer as d   on a.customercode=d.customercode "+
                    " inner join tblbilltype as e on a.billtypecode=e.billtypecode "+
                    " inner join tblareamaster as f on f.areacode = d.areacode "+
                    " inner join tblcitymaster as g on g.citycode = f.citycode "+
                    " inner join tblstatemaster as h on h.gststatecode = g.statecode "+
                    "where a.transactionno='"+gettransactionno+"' and a.financialyearcode='"+getfinancialyrcode+"' ; ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }

        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetSalesPrint", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return mCur;
    }

    //Get Sales bills for print
    public Cursor GetSalesOrderPrint(String gettransactionno,String getfinancialyrcode)
    {
        Cursor mCur=null;
        try{
            String sql ="select b.companyname,b.street,b.area,b.city,b.pincode,b.telephone,b.mobileno,b.gstin," +
                    "   b.fssaino,b.panno, c.vanname,d.customernametamil,a.billno,e.billtype,a.gstin,d.mobileno as cusmobile," +
                    "   strftime('%d-%m-%Y', a.billdate) as date,a.companycode,b.shortname,a.grandtotal,a.bookingno," +
                    "   a.billtypecode,a.salestime," +
                    "   coalesce((SELECT transportname FROM tbltransportmaster where transportid=a.transportid),'') as transportname," +
                    "   coalesce((select day_of_dispatch from tbltransportcitymapping where transportid=a.transportid and citycode=" +
                    "   (select citycode from tblareamaster where areacode=(select areacode from tblcustomer where " +
                    "   customercode=a.customercode ))),'') as day_of_dispatch,(select areanametamil from tblareamaster  " +
                    "   where areacode=(select areacode from tblcustomer where customercode=a.customercode ) ) as areaname," +
                    "   coalesce((select aa.transportmodetype from tbltransportmode as aa where " +
                    "   aa.transportmodecode=a.transportmode),'') as transportmode," +
                    "   g.citynametamil as custcityname,f.pincode as custpincode," +
                    "   h.statenametamil as custstatename,h.gststatecode as custgststatecode," +
                    "   (select statename from tblstatemaster as aa where aa.gststatecode=b.gststatecode ) as compstatename," +
                    "   b.gststatecode as compgststatecode" +
                    " from tblsalesorder AS a  " +
                    " inner join tblcompanymaster as b on a.companycode=b.companycode " +
                    " inner join tblvanmaster as c on a.vancode=c.vancode " +
                    " INNER JOIN tblcustomer as d   on a.customercode=d.customercode " +
                    " inner join tblbilltype as e on a.billtypecode=e.billtypecode " +
                    " inner join tblareamaster as f on f.areacode = d.areacode " +
                    " inner join tblcitymaster as g on g.citycode = f.citycode " +
                    " inner join tblstatemaster as h on h.gststatecode = g.statecode " +
                    " where a.transactionno='" + gettransactionno + "' and a.financialyearcode='" + getfinancialyrcode + "' ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }

        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetSalesOrderPrint", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return mCur;
    }

    public Cursor GetSalesReturnPrint(String gettransactionno,String getfinancialyrcode)
    {
        Cursor mCur=null;
        try{
            String sql ="select b.companyname,b.street,b.area,b.city,b.pincode,b.telephone,b.mobileno,b.gstin," +
                    "   b.fssaino,b.panno, c.vanname,d.customernametamil,a.billno,e.billtype,a.gstin,d.mobileno as cusmobile," +
                    "   strftime('%d-%m-%Y', a.billdate) as date,a.companycode,b.shortname,a.grandtotal,a.bookingno," +
                    "   a.billtypecode,a.salestime , g.citynametamil as custcityname,f.pincode as custpincode," +
                    "   h.statenametamil as custstatename,h.gststatecode as custgststatecode," +
                    "   (select statename from tblstatemaster as aa where aa.gststatecode=b.gststatecode ) as compstatename," +
                    "   b.gststatecode as compgststatecode" +
                    " from tblsalesreturn AS a  " +
                    " inner join tblcompanymaster as b on a.companycode=b.companycode " +
                    " inner join tblvanmaster as c on a.vancode=c.vancode " +
                    " INNER JOIN tblcustomer as d   on a.customercode=d.customercode " +
                    " inner join tblbilltype as e on a.billtypecode=e.billtypecode " +
                    " inner join tblareamaster as f on f.areacode = d.areacode " +
                    " inner join tblcitymaster as g on g.citycode = f.citycode " +
                    " inner join tblstatemaster as h on h.gststatecode = g.statecode " +
                    " where a.transactionno='" + gettransactionno + "' and a.financialyearcode='" + getfinancialyrcode + "' ; ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetSalesReturnPrint", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Sales bills for print
    public Cursor GetDCSalesPrint(String gettransactionno,String getfinancialyrcode)
    {
        Cursor mCur=null;
        try{
            String sql ="select b.companyname,b.street,b.area,b.city,b.pincode,b.telephone,b.mobileno,b.gstin, "+
                    "   b.fssaino,b.panno, c.vanname,d.customernametamil,GROUP_CONCAT(a.billno),e.billtype,a.gstin,d.mobileno as cusmobile, "+
                    "   strftime('%d-%m-%Y', a.createddate) as date,a.companycode,b.shortname,a.grandtotal,a.bookingno, "+
                    "   a.billtypecode,a.salestime ,g.citynametamil "+
                    "   from tblsales AS a "+
                    " inner join tblcompanymaster as b on a.companycode=b.companycode "+
                    " inner join tblvanmaster as c on a.vancode=c.vancode "+
                    " INNER JOIN tblcustomer as d   on a.customercode=d.customercode "+
                    " inner join tblbilltype as e on a.billtypecode=e.billtypecode "+
                    " inner join tblareamaster as f on f.areacode = d.areacode "+
                    " inner join tblcitymaster as g on g.citycode = f.citycode "+
                    " where a.transactionno='"+gettransactionno+"' and a.financialyearcode='"+getfinancialyrcode+"' ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }

        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetDCSalesPrint", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return mCur;
    }


    //Get Sales bills for print
    public Cursor GetDCSalesReturnPrint(String gettransactionno,String getfinancialyrcode)
    {
        Cursor mCur=null;
        try{
            String sql ="select b.companyname,b.street,b.area,b.city,b.pincode,b.telephone,b.mobileno,b.gstin," +
                    "   b.fssaino,b.panno, c.vanname,d.customernametamil,GROUP_CONCAT(a.billno),e.billtype,a.gstin,d.mobileno as cusmobile," +
                    "   strftime('%d-%m-%Y', a.createddate) as date,a.companycode,b.shortname,a.grandtotal,a.bookingno," +
                    "   a.billtypecode,a.salestime ,g.citynametamil" +
                    " from tblsalesreturn AS a " +
                    " inner join tblcompanymaster as b on a.companycode=b.companycode " +
                    " inner join tblvanmaster as c on a.vancode=c.vancode " +
                    " INNER JOIN tblcustomer as d   on a.customercode=d.customercode " +
                    " inner join tblbilltype as e on a.billtypecode=e.billtypecode " +
                    " inner join tblareamaster as f on f.areacode = d.areacode " +
                    " inner join tblcitymaster as g on g.citycode = f.citycode" +
                    " where a.transactionno='" + gettransactionno + "' and a.financialyearcode='" + getfinancialyrcode + "' ; ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }

        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetDCSalesReturnPrint", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return mCur;
    }

    //Get Sales bills for print
    public Cursor GetSalesPaymentVoucherPrint(String gettransactionno,String getfinancialyrcode, String companyCode)
    {
        Cursor mCur=null;
        try{
            String whereCompany = " 1=1";
            if (!Utilities.isNullOrEmpty(companyCode))
                whereCompany = " a.companycode='" + companyCode + "'";

            String sql ="select b.companyname,b.street,b.area,b.city,b.pincode,b.telephone,b.mobileno,b.gstin," +
                    " b.fssaino,b.panno, c.vanname,d.customernametamil,a.billno,e.billtype,a.gstin,d.mobileno as cusmobile," +
                    " strftime('%d-%m-%Y', a.createddate) as date,a.companycode,b.shortname,sum(a.grandtotal) as total,a.bookingno," +
                    " a.billtypecode,a.salestime, coalesce(cashpaidstatus,'') as cashpaidstatus " +
                    " from tblsales AS a " +
                    " inner join tblcompanymaster as b on a.companycode=b.companycode " +
                    " inner join tblvanmaster as c on a.vancode=c.vancode " +
                    " INNER JOIN tblcustomer as d on a.customercode=d.customercode " +
                    " inner join tblbilltype as e on a.billtypecode=e.billtypecode " +
                    " where a.transactionno='"+gettransactionno+"' and a.financialyearcode='"+getfinancialyrcode+"' and " + whereCompany +
                    " group by a.transactionno ; ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }

        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetSalesPaymentVoucherPrint", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return mCur;
    }

    public Cursor GetSalesPaymentVoucherDetailsPrint(String gettransactionno,String getfinancialyrcode)
    {
        Cursor mCur=null;
        try{

            String sql ="select billno,companycode,grandtotal,(Select shortname from tblcompanymaster where companycode=a.companycode)" +
                    " as shortname from tblsales as a " +
                    "where a.transactionno='"+gettransactionno+"' and a.financialyearcode='"+getfinancialyrcode+"' ; ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetSalesPaymentVoucherDetailsPrint", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return mCur;
    }

    public Cursor GetCompanyWiseSalesPaymentVoucherDetailsPrint(String gettransactionno,String getfinancialyrcode, String companyCode)
    {
        Cursor mCur=null;
        try{

            String whereCompany = " 1=1";
            if (!Utilities.isNullOrEmpty(companyCode))
                whereCompany = " a.companycode='" + companyCode + "'";

            String sql ="SELECT billno,companycode,grandtotal," +
                    " (SELECT shortname FROM tblcompanymaster WHERE companycode=a.companycode) as shortname " +
                    " FROM tblsales as a " +
                    " WHERE a.transactionno='"+gettransactionno+"' AND a.financialyearcode='"+getfinancialyrcode+"' AND " + whereCompany    ;
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetCompanyWiseSalesPaymentVoucherDetailsPrint", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return mCur;
    }


    public Cursor GetSalesOrderPaymentVoucherDetailsPrint(String gettransactionno,String getfinancialyrcode)
    {
        Cursor mCur=null;
        try{

            String sql ="select billno,companycode,grandtotal,(Select shortname from tblcompanymaster " +
                    " where companycode=a.companycode)" +
                    " as shortname from tblsalesorder as a " +
                    "where a.transactionno='"+gettransactionno+"' and a.financialyearcode='"+getfinancialyrcode+"' ; ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetSalesOrderPaymentVoucherDetailsPrint", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return mCur;
    }
    public Cursor GetSalesReturnPaymentVoucherDetailsPrint(String gettransactionno,String getfinancialyrcode)
    {
        Cursor mCur=null;
        try{

            String sql ="select billno,companycode,grandtotal,(Select shortname from tblcompanymaster where companycode=a.companycode)" +
                    " as shortname from tblsalesreturn as a " +
                    "where a.transactionno='"+gettransactionno+"' and a.financialyearcode='"+getfinancialyrcode+"' ; ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetSalesReturnPaymentVoucherDetailsPrint", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return mCur;
    }

    public Cursor GetSalesDCDetailsPrint(String gettransactionno,String getfinancialyrcode)
    {
        Cursor mCur=null;
        try{

            String sql ="select billno,companycode,grandtotal,(Select shortname from tblcompanymaster where companycode=a.companycode)" +
                    " as shortname from tblsales as a " +
                    "where a.transactionno='"+gettransactionno+"' and a.financialyearcode='"+getfinancialyrcode+"' ; ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetSalesDCDetailsPrint", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return mCur;
    }

    //Get Sales bills for print
    public Cursor GetSalesItemPrint(String gettransactionno,String getfinancialyrcode,String companycode)
    {
        Cursor mCur=null;
        try{

            String sql ="select b.itemnametamil,a.qty ,a.amount,a.price,c.unitname as unit," +
                    "d.hsn,a.cgst+a.sgst+a.igst as tax,printf('%.2f',coalesce((amount-cgstamt-sgstamt-igstamt),0))" +
                    " as taxableamount,printf('%.2f',coalesce((cgstamt+sgstamt+igstamt),0)) as taxvalue," +
                    " printf('%.2f',(printf('%.2f',coalesce((amount-cgstamt-sgstamt-igstamt),0))/a.qty)) as unittaxableamount " +
                    " from tblsalesitemdetails as a inner join tblitemmaster " +
                    "as b on a.itemcode=b.itemcode  inner join tblunitmaster as c on b.unitcode=c.unitcode  " +
                    " inner join tblitemsubgroupmaster as d on  d.itemsubgroupcode=b.itemsubgroupcode " +
                    "where a.transactionno='"+gettransactionno+"'" +
                    " and a.financialyearcode='"+getfinancialyrcode+"' and a.companycode='"+companycode+"' order by itemtype ; ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetSalesItemPrint", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return mCur;
    }

    //Get Sales bills for print
    public Cursor GetSalesOrderItemPrint(String gettransactionno,String getfinancialyrcode,String companycode)
    {
        Cursor mCur=null;
        try{

            String sql ="select b.itemnametamil,a.qty ,a.amount,a.price,c.unitname as unit," +
                    "d.hsn,a.cgst+a.sgst+a.igst as tax from tblsalesorderitemdetails as a inner join tblitemmaster " +
                    "as b on a.itemcode=b.itemcode  inner join tblunitmaster as c on b.unitcode=c.unitcode  " +
                    " inner join tblitemsubgroupmaster as d on  d.itemsubgroupcode=b.itemsubgroupcode " +
                    "where a.transactionno='"+gettransactionno+"'" +
                    " and a.financialyearcode='"+getfinancialyrcode+"'  order by itemtype; ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetSalesOrderItemPrint", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return mCur;
    }
    //Get Sales Return bills for print
    public Cursor GetSalesReturnItemPrint(String gettransactionno,String getfinancialyrcode,String companycode)
    {
        Cursor mCur=null;
        try{

            String sql ="select b.itemnametamil,a.qty ,a.amount,a.price,c.unitname as unit," +
                    "d.hsn,a.cgst+a.sgst+a.igst as tax from tblsalesreturnitemdetails as a inner join tblitemmaster " +
                    "as b on a.itemcode=b.itemcode  inner join tblunitmaster as c on b.unitcode=c.unitcode  " +
                    " inner join tblitemsubgroupmaster as d on  d.itemsubgroupcode=b.itemsubgroupcode " +
                    "where a.transactionno='"+gettransactionno+"' and a.financialyearcode='"+getfinancialyrcode+"' " +
                    "and a.companycode='"+companycode+"' order by itemtype; ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetSalesReturnItemPrint", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return mCur;
    }

    //Get Sales bills for print
    public Cursor GetDCSalesItemPrint(String gettransactionno,String getfinancialyrcode,String companycode)
    {
        Cursor mCur=null;
        try{

            String sql ="select b.itemnametamil,a.qty ,a.amount,a.price,c.unitname as unit," +
                    "d.hsn,a.cgst+a.sgst+a.igst as tax from tblsalesitemdetails as a inner join tblitemmaster " +
                    "as b on a.itemcode=b.itemcode  inner join tblunitmaster as c on b.unitcode=c.unitcode  " +
                    " inner join tblitemsubgroupmaster as d on  d.itemsubgroupcode=b.itemsubgroupcode " +
                    "where a.transactionno='"+gettransactionno+"' and a.financialyearcode='"+getfinancialyrcode+"' order by a.companycode; ";

            //and a.companycode='"+companycode+"'
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetDCSalesItemPrint", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return mCur;
    }

    //Get Sales bills for print
    public Cursor GetDCSalesReturnItemPrint(String gettransactionno,String getfinancialyrcode,String companycode)
    {
        Cursor mCur=null;
        try{

            String sql ="select b.itemnametamil,a.qty ,a.amount,a.price,c.unitname as unit," +
                    "d.hsn,a.cgst+a.sgst+a.igst as tax from tblsalesreturnitemdetails as a inner join tblitemmaster " +
                    "as b on a.itemcode=b.itemcode  inner join tblunitmaster as c on b.unitcode=c.unitcode  " +
                    " inner join tblitemsubgroupmaster as d on  d.itemsubgroupcode=b.itemsubgroupcode " +
                    "where a.transactionno='"+gettransactionno+"' and a.financialyearcode='"+getfinancialyrcode+"' order by a.companycode; ";

            //and a.companycode='"+companycode+"'
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetDCSalesReturnItemPrint", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return mCur;
    }


    //Get Sales tax for print
    public Cursor GetSalestaxPrint(String gettransactionno,String getfinancialyrcode,String companycode)
    {
        Cursor mCur=null;
        try{
            String sql ="select a.tax,coalesce(taxablevalue,0) as TaxableValue,coalesce(cgst,0) as cgst,coalesce(sgst,0) as sgst" +
                    " from tbltax as a left outer join  ( select GST as GST ,coalesce(sum(taxablevalue),0) as TaxableValue,coalesce(sum(cgst),0) as CGST,coalesce(Sum(sgst),0) as SGST from " +
                    "(SELECT b.tax as GST,cast(price/(1+(b.tax/100))*qty as decimal(38,2)) as TaxableValue," +
                    "cast((price/(1+(b.tax/100))*qty)*cgst/100 as decimal(38,2)) as cgst," +
                    "cast((price/(1+(b.tax/100))*qty)*sgst/100 as decimal(38,2)) as sgst" +
                    " FROM tbltax" +
                    " as b left outer join tblsalesitemdetails as a on a.cgst+a.sgst+a.igst=b.tax" +
                    " where a.transactionno='"+ gettransactionno +"' and a.financialyearcode='"+ getfinancialyrcode +"' and a.companycode='"+ companycode +"') as derv " +
                    " group by GST ) as e on a.tax=e.gst where TaxableValue > 0 order by a.tax";

            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }

        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetSalestaxPrint", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return mCur;
    }

    //Get Sales tax for print
    public Cursor GetSalesOrdertaxPrint(String gettransactionno,String getfinancialyrcode,String companycode)
    {
        Cursor mCur=null;
        try{
            String sql ="select tax,coalesce(taxablevalue,0) as TaxableValue,coalesce(cgst,0) as cgst,coalesce(sgst,0) as sgst from tbltax as " +
                    "a left outer join  ( select GST as GST ,coalesce(sum(taxablevalue),0) as TaxableValue,coalesce(sum(cgst),0) as CGST," +
                    "coalesce(Sum(sgst),0) as SGST from " +
                    "(SELECT tax as GST,cast(price/(1+(tax/100))*qty as decimal(38,2)) as TaxableValue," +
                    "cast((price/(1+(tax/100))*qty)*cgst/100 as decimal(38,2)) as cgst," +
                    "cast((price/(1+(tax/100))*qty)*sgst/100 as decimal(38,2)) as sgst" +
                    " FROM tbltax" +
                    " as b left outer join tblsalesorderitemdetails as a on a.cgst+a.sgst+a.igst=b.tax" +
                    " where a.transactionno='"+ gettransactionno +"' and a.financialyearcode='"+ getfinancialyrcode +"' and a.companycode='"+ companycode +"') as derv " +
                    " group by GST ) as e on a.tax=e.gst order by a.tax";

            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }

        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetSalesOrdertaxPrint", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return mCur;
    }


    //Get Sales tax for print
    public Cursor GetSalesReturntaxPrint(String gettransactionno,String getfinancialyrcode,String companycode)
    {
        Cursor mCur=null;
        try{
            String sql ="select tax,coalesce(taxablevalue,0) as TaxableValue,coalesce(cgst,0) as cgst,coalesce(sgst,0) as sgst from tbltax as a left outer join  ( select GST as GST ,coalesce(sum(taxablevalue),0) as TaxableValue,coalesce(sum(cgst),0) as CGST,coalesce(Sum(sgst),0) as SGST from " +
                    "(SELECT tax as GST,cast(price/(1+(tax/100))*qty as decimal(38,2)) as TaxableValue," +
                    "cast((price/(1+(tax/100))*qty)*cgst/100 as decimal(38,2)) as cgst," +
                    "cast((price/(1+(tax/100))*qty)*sgst/100 as decimal(38,2)) as sgst" +
                    " FROM tbltax" +
                    " as b left outer join tblsalesreturnitemdetails as a on a.cgst+a.sgst+a.igst=b.tax" +
                    " where a.transactionno='"+ gettransactionno +"' and a.financialyearcode='"+ getfinancialyrcode +"' and a.companycode='"+ companycode +"') as derv " +
                    " group by GST ) as e on a.tax=e.gst where TaxableValue > 0 order by a.tax";

            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }

        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetSalesReturntaxPrint", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return mCur;
    }

    //Get Sales tax for print
    public Cursor GetSalestaxtotalPrint(String gettransactionno,String getfinancialyrcode,String companycode)
    {
        Cursor mCur=null;
        try{
            String sql ="select GST as 'GST %' ,coalesce(sum(taxablevalue),0) as TaxableValue,coalesce(sum(cgst),0) as CGST,coalesce(Sum(sgst),0) as SGST from " +
                    "(SELECT 'Total' as GST,cast(price/(1+(b.tax/100))*qty as decimal(38,2)) as TaxableValue," +
                    "cast((price/(1+(b.tax/100))*qty)*cgst/100 as decimal(38,2)) as cgst," +
                    "cast((price/(1+(b.tax/100))*qty)*sgst/100 as decimal(38,2)) as sgst" +
                    " FROM tbltax" +
                    " as b left outer join tblsalesitemdetails as a on a.cgst+a.sgst+a.igst=b.tax" +
                    " where a.transactionno='"+ gettransactionno +"' and a.financialyearcode='"+ getfinancialyrcode +"' and a.companycode='"+ companycode +"') as derv " ;


            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }

        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetSalestaxtotalPrint", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return mCur;
    }


    //Get Sales tax for print
    public Cursor GetSalesOrdertaxtotalPrint(String gettransactionno,String getfinancialyrcode,String companycode)
    {
        Cursor mCur=null;
        try{
            String sql ="select GST as 'GST %' ,coalesce(sum(taxablevalue),0) as TaxableValue,coalesce(sum(cgst),0) as CGST,coalesce(Sum(sgst),0) as SGST from " +
                    "(SELECT 'Total' as GST,cast(price/(1+(tax/100))*qty as decimal(38,2)) as TaxableValue," +
                    "cast((price/(1+(tax/100))*qty)*cgst/100 as decimal(38,2)) as cgst," +
                    "cast((price/(1+(tax/100))*qty)*sgst/100 as decimal(38,2)) as sgst" +
                    " FROM tbltax" +
                    " as b left outer join tblsalesorderitemdetails as a on a.cgst+a.sgst+a.igst=b.tax" +
                    " where a.transactionno='"+ gettransactionno +"' and a.financialyearcode='"+ getfinancialyrcode +"' and a.companycode='"+ companycode +"') as derv " ;


            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }

        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetSalesOrdertaxtotalPrint", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return mCur;
    }

    //Get Sales tax for print
    public Cursor GetSalesReturntaxtotalPrint(String gettransactionno,String getfinancialyrcode,String companycode)
    {
        Cursor mCur=null;
        try{
            String sql ="select GST as 'GST %' ,coalesce(sum(taxablevalue),0) as TaxableValue,coalesce(sum(cgst),0) as CGST,coalesce(Sum(sgst),0) as SGST from " +
                    "(SELECT 'Total' as GST,cast(price/(1+(tax/100))*qty as decimal(38,2)) as TaxableValue," +
                    "cast((price/(1+(tax/100))*qty)*cgst/100 as decimal(38,2)) as cgst," +
                    "cast((price/(1+(tax/100))*qty)*sgst/100 as decimal(38,2)) as sgst" +
                    " FROM tbltax" +
                    " as b left outer join tblsalesreturnitemdetails as a on a.cgst+a.sgst+a.igst=b.tax" +
                    " where a.transactionno='"+ gettransactionno +"' and a.financialyearcode='"+ getfinancialyrcode +"' and a.companycode='"+ companycode +"') as derv " ;


            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetSalesReturntaxtotalPrint", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Sales schedule for print
    public Cursor GetSalesschedulePrint(String gettransactionno,String getfinancialyrcode,String companycode)
    {
        Cursor mCur=null;
        try{
            String sql ="select c.vanname,(select employeenametamil from tblemployeemaster where employeecode=b.employeecode) as employeename," +
                    "(select employeenametamil from tblemployeemaster where employeecode=b.drivercode) as drivername,helpername " +
                    " from tblsales as a inner join tblsalesschedule as b " +
                    "on a.schedulecode=b.schedulecode inner join tblvanmaster as c on a.vancode=c.vancode " +

                    " where a.transactionno='"+ gettransactionno +"' and a.financialyearcode='"+ getfinancialyrcode +"' " ;


            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetSalesschedulePrint", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }



    //Get Sales schedule for print
    public Cursor GetSalesOrderschedulePrint(String gettransactionno,String getfinancialyrcode,String companycode)
    {
        Cursor mCur=null;
        try{
            String sql ="select c.vanname,(select employeenametamil from tblemployeemaster where employeecode=b.employeecode) as employeename," +
                    "(select employeenametamil from tblemployeemaster where employeecode=b.drivercode) as drivername,helpername " +
                    " from tblsalesorder as a inner join tblsalesschedule as b " +
                    "on a.schedulecode=b.schedulecode inner join tblvanmaster as c on a.vancode=c.vancode " +

                    " where a.transactionno='"+ gettransactionno +"' and a.financialyearcode='"+ getfinancialyrcode +"' " ;


            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetSalesOrderschedulePrint", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Sales schedule for print
    public Cursor GetSalesReturnSchPrint(String gettransactionno,String getfinancialyrcode,String companycode)
    {
        Cursor mCur=null;
        try{
            String sql ="select c.vanname,(select employeenametamil from tblemployeemaster where employeecode=b.employeecode) as employeename," +
                    "(select employeenametamil from tblemployeemaster where employeecode=b.drivercode) as drivername,helpername " +
                    " from tblsalesreturn as a inner join tblsalesschedule as b " +
                    "on a.schedulecode=b.schedulecode inner join tblvanmaster as c on a.vancode=c.vancode " +

                    " where a.transactionno='"+ gettransactionno +"' and a.financialyearcode='"+ getfinancialyrcode +"' " ;


            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetSalesReturnSchPrint", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Sales schedule for print
    public Cursor GetSalesReturnschedulePrint(String gettransactionno,String getfinancialyrcode,String companycode)
    {
        Cursor mCur=null;
        try{
            String sql ="select c.vanname,(select employeenametamil from tblemployeemaster where employeecode=b.employeecode) as employeename," +
                    "(select employeenametamil from tblemployeemaster where employeecode=b.drivercode) as drivername,helpername " +
                    " from tblsalesreturn as a inner join tblsalesschedule as b " +
                    "on a.schedulecode=b.schedulecode inner join tblvanmaster as c on a.vancode=c.vancode " +

                    " where a.transactionno='"+ gettransactionno +"' and a.financialyearcode='"+ getfinancialyrcode +"' " ;


            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetSalesReturnschedulePrint", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }


    //Get SAles Discount
    public double GetSalesDiscount(String gettransactionno,String getfinancialyrcode,String companycode)
    {
        double getcount = 0;
        try{
            // String sql ="select coalesce(discount,0) from tblsales as a where a.transactionno='"+ gettransactionno +"' and a.financialyearcode='"+ getfinancialyrcode +"' and a.companycode='"+ companycode +"'";
            String sql="select sum(a.discount) from tblsales as a " +
                    " inner join tblsalesitemdetails as b on a.companycode=b.companycode and a.financialyearcode=b.financialyearcode and a.transactionno=b.transactionno and a.bookingno=b.bookingno" +
                    " where a.transactionno='"+ gettransactionno +"' and a.financialyearcode='"+ getfinancialyrcode +"' and a.companycode='"+ companycode +"'" +
                    "and b.freeitemstatus='freeitem'";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getcount = mCur.getFloat(0);
            }
            if(mCur != null && !mCur.isClosed()){
                mCur.close();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetSalesDiscount", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getcount;
    }

    //Get roundoff for print
    public double GetSalesroundoffPrint(String gettransactionno,String getfinancialyrcode,String companycode)
    {
        double getroundoff = 0;
        Cursor mCur=null;
        try{

            String sql =" SELECT case when roundoffs >= 0.5 then round(1 - CAST(roundoffs as numeric(32,2)) ,2 ) else " +
                    " case when  roundoffs != 0 then '-' || CAST(round(roundoffs ,2) as TEXT) else " +
                    " CAST(roundoffs as numeric(32,2)) end  end as roud from (select coalesce(CAST(sum(amount)-discount" +
                    " as numeric(32,2))-CAST(sum(amount)-discount as INT),0) as roundoffs from tblsalesitemdetails as a" +
                    " inner join tblitemmaster as b on a.itemcode=b.itemcode  inner join tblunitmaster as c on b.unitcode=c.unitcode " +
                    " inner join tblitemsubgroupmaster as d on  d.itemsubgroupcode=b.itemsubgroupcode" +
                    " where a.transactionno='"+gettransactionno+"'" +
                    " and a.financialyearcode='"+getfinancialyrcode+"'  and a.companycode='"+companycode+"' " +
                    "order by itemtype) as dev; ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getroundoff = mCur.getFloat(0);
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetSalesItemPrint", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return getroundoff;
    }
    //Get SAles Discount
    public double GetSalesTotalforbill(String gettransactionno,String getfinancialyrcode,String companycode)
    {
        double getgrandtotal = 0;
        try{
            // String sql ="select coalesce(discount,0) from tblsales as a where a.transactionno='"+ gettransactionno +"' and a.financialyearcode='"+ getfinancialyrcode +"' and a.companycode='"+ companycode +"'";
            String sql="select a.grandtotal from tblsales as a " +
                    " where a.transactionno='"+gettransactionno+"' and a.financialyearcode='"+getfinancialyrcode+"' and " +
                    " a.companycode='"+companycode+"' ";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getgrandtotal = mCur.getFloat(0);
            }
            if(mCur != null && !mCur.isClosed()){
                mCur.close();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetSalesDiscount", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getgrandtotal;
    }
    //Get SAles Discount
    public double GetSalesOrderDiscount(String gettransactionno,String getfinancialyrcode,String companycode)
    {
        double getcount = 0;
        try{
            //String sql ="select coalesce(discount,0) from tblsalesorder as a where " +
            //        " a.transactionno='"+ gettransactionno +"' and a.financialyearcode='"+ getfinancialyrcode +"'" +
            //        " and a.companycode='"+ companycode +"'";
            String sql="select sum(a.discount) from tblsalesorder as a " +
                    " inner join tblsalesorderitemdetails as b on a.companycode=b.companycode and a.financialyearcode=b.financialyearcode and a.transactionno=b.transactionno and a.bookingno=b.bookingno" +
                    " where a.transactionno='"+ gettransactionno +"' and a.financialyearcode='"+ getfinancialyrcode +"' and a.companycode='"+ companycode +"'" +
                    "and b.freeitemstatus='freeitem'";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getcount = mCur.getFloat(0);
            }
            if(mCur != null && !mCur.isClosed()){
                mCur.close();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetSalesOrderDiscount", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getcount;
    }

    public Cursor udfngetdevicedetails(String varDeviceID)
    {
        Cursor mCur=null;
        try{
            String sql="SELECT UserName,CompanyName from 'tblActivation' where status='Activated' AND DeviceID='"+ varDeviceID +"' ORDER BY Autonum desc LIMIT 1";// and status='Activated'
            mCur = mDb.rawQuery(sql, null);
            if(mCur.getCount() > 0)
                mCur.moveToFirst();

        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - udfngetdevicedetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return mCur;
    }

    public void insertActivationDetails(String CompanyName,String UserName,String City,String MobileNumber,String EmailID,String RegistrationKey,String Status,String DeviceID,String Activationcode)
    {
        try{
            String sql = "INSERT INTO  'tblActivation'(CompanyName,UserName,City,MobileNumber,EmailID,RegistrationKey,Status,DeviceID,ActivationCode,SyncFlag,CreatedDate) VALUES('"+
                    ReplaceQuotes(CompanyName.toUpperCase()) +"','"+UserName.toUpperCase() +"','"+City.toUpperCase()+"','"+MobileNumber+"','"+EmailID.toUpperCase() +"','"+RegistrationKey+"','"+Status+"','"+ DeviceID +"','"+ Activationcode+"',1,datetime('now', 'localtime'))";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - insertActivationDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

    }

    public String ReplaceQuotes(String strValue)
    {
        strValue = strValue.replace("'", "''");
        return strValue;
    }

    public void updateActivationDetails(String RegistrationKey,String DeviceID,String Activationcode)
    {
        try{
            String sql = "UPDATE  'tblActivation' SET Status='Activated',ActivationCode='"+ Activationcode+"' where RegistrationKey='"+RegistrationKey+"' and DeviceID='"+ DeviceID +"'";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - updateActivationDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

    }

    /*public Cursor getschemebusinesstype(String itemcode){
        Cursor mCur=null;
        String sql = "select * from(select distinct c.business_type,a.itemname,a.itemcode from tblitemmaster as a " +
                " inner join tblschemeitemdetails as b on a.itemcode=b.purchaseitemcode " +
                " inner join tblscheme as c on c.schemecode=b.schemecode and c.status='Active' " +
                " UNION ALL " +
                " select distinct c.business_type,a.itemname,a.itemcode from tblitemmaster as a " +
                " inner join tblschemeratedetails as b on a.itemcode=b.itemcode " +
                " inner join tblscheme as c on c.schemecode=b.schemecode and c.status='Active' " +
                " ) as dev  where dev.itemcode='"+ itemcode +"' ";
        mCur = mDb.rawQuery(sql, null);
        if(mCur.getCount() > 0)
            mCur.moveToFirst();

        return  mCur;
    }*/

    public Cursor getschemebusinesstype(String itemcode){
        Cursor mCur=null;
        try{
            String GenDate= GenCreatedDate();
            String getroutecode = preferenceMangr.pref_getString("getroutecode");
            String sql = "select * from(select distinct c.business_type,a.itemname,a.itemcode,c.multiplebilltypecode from tblitemmaster as a " +
                    " inner join tblschemeitemdetails as b on a.itemcode=b.purchaseitemcode " +
                    " inner join tblscheme as c on c.schemecode=b.schemecode and c.status='Active' and " +
                    " (','||multipleroutecode||',') LIKE '%,"+ getroutecode +",%' and" +
                    " (validityfrom<=datetime('"+GenDate+"')) " +
                    "    and (ifnull(validityto,'')='' " +
                    "    or (validityfrom<=datetime('"+GenDate+"') and  validityto>=datetime('"+GenDate+"'))) " +
                    " UNION ALL " +
                    " select distinct c.business_type,a.itemname,a.itemcode,c.multiplebilltypecode from tblitemmaster as a " +
                    " inner join tblschemeratedetails as b on a.itemcode=b.itemcode " +
                    " inner join tblscheme as c on c.schemecode=b.schemecode and c.status='Active' and " +
                    " (','||multipleroutecode||',') LIKE '%,"+ getroutecode +",%' and" +
                    " (validityfrom<=datetime('"+GenDate+"')) " +
                    "    and (ifnull(validityto,'')='' " +
                    "    or (validityfrom<=datetime('"+GenDate+"') and  validityto>=datetime('"+GenDate+"'))) " +
                    " ) as dev  where dev.itemcode='"+ itemcode +"' ";
            mCur = mDb.rawQuery(sql, null);
            if(mCur.getCount() > 0)
                mCur.moveToFirst();
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - getschemebusinesstype" , String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return  mCur;
    }

    //Get Scheme  Free Item Details
    public Cursor GetOrderSchemeType(String getitemcode,String getroutecode)
    {
        Cursor mCur = null;
        try{

            String GenDate= GenCreatedDate();
            String sql ="select * from(select DISTINCT b.schemetype,a.purchaseitemcode as itemcode,0 as discount from tblschemeitemdetails as a  inner join " +
                    " tblscheme as b on a.schemecode=b.schemecode where " +
                    " b.status='Active' and (','||multipleroutecode||',') LIKE '%,"+ getroutecode +",%' and" +
                    " (validityfrom<=datetime('"+GenDate+"')) " +
                    " and (ifnull(validityto,'')='' " +
                    " or (validityfrom<=datetime('"+GenDate+"') and  validityto>=datetime('"+GenDate+"'))) " +
                    " union all " +
                    " select DISTINCT b.schemetype,a.itemcode as itemcode,coalesce(a.discountamount,0) as discount from tblschemeratedetails as a  inner join " +
                    " tblscheme as b on a.schemecode=b.schemecode where " +
                    " b.status='Active' and (','||multipleroutecode||',') LIKE '%,"+ getroutecode +",%' and" +
                    " (validityfrom<=datetime('"+GenDate+"')) " +
                    " and (ifnull(validityto,'')='' " +
                    " or (validityfrom<=datetime('"+GenDate+"') and  validityto>=datetime('"+GenDate+"')))" +
                    " ) as dev where dev.itemcode='"+ getitemcode +"'";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - GetOrderSchemeType" , String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }


    public Cursor getprinterdetails(){
        Cursor cursor=null;
        try{
            String sql="select * from tblprinter limit 1";
            cursor = mDb.rawQuery(sql,null);
            if (cursor.getCount() > 0)
            {
                cursor.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - getprinterdetails" , String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return cursor;
    }

    public String insertprinterdetails(String printername,String macaddress){
        Cursor cursor=null;
        String result="";
        try{
            String delsql ="delete from tblprinter";
            mDb.execSQL(delsql);

            /*String count="select count(*) from tblprinter";
            cursor=mDb.rawQuery(count,null);*/

            //if(cursor.getString(0).equals("0")){
            String sql = "insert into tblprinter (autonum,printername,macaddress,status) " +
                    "values ((select coalesce(max(autonum),0)+1 from tblprinter),'"+printername+"','"+macaddress+"','Active')";
            mDb.execSQL(sql);
            result="success";
            //}


        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - insertprinterdetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return result;
    }

    public String getcompanywebsite(){
        Cursor cursor=null;
        try{
            String sql="select website from tblcompanymaster limit 1";
            cursor = mDb.rawQuery(sql,null);
            if (cursor.getCount() > 0)
            {
                cursor.moveToFirst();
            }
        }catch(Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - getcompanywebsite", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return cursor.getString(0);
    }

    public String getprintheader(){
        Cursor cursor=null;
        String printerHeader = "";
        try{
            String sql="select printheader from tblgeneralsettings limit 1";
            cursor = mDb.rawQuery(sql,null);
            if (cursor.getCount() > 0)
            {
                cursor.moveToFirst();
            }
            printerHeader = cursor.getString(0);
        }catch(Exception ex){
            if(cursor != null)
                cursor.close();
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - getprintheader", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }finally{
            if(cursor != null)
                cursor.close();
        }
        return printerHeader;
    }


    public String gettodayschedulecount(){
        Cursor cursor=null;
        String date=GenCreatedDate();
        String count = "";
        try{

            String sql="select count(schedulecode) from tblsalesschedule where vancode='"+preferenceMangr.pref_getString("getvancode")+"' " +
                    " and date(scheduledate)=date('"+date+"')";
            cursor = mDb.rawQuery(sql,null);
            if (cursor.getCount() > 0)
            {
                cursor.moveToFirst();
            }
            count = cursor.getString(0);
        }catch(Exception ex){
            if(cursor != null)
                cursor.close();
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - gettodayschedulecount", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        } finally{
            if(cursor != null)
                cursor.close();
        }
        return count;
    }

    public String getshowcashpaidpopupstatus(){
        Cursor cursor=null;
        String paidpopupstatus = "";
        try{
            String sql="select coalesce(showcashpaidpopup,'no') from tblgeneralsettings limit 1";
            cursor = mDb.rawQuery(sql,null);
            if (cursor.getCount() > 0)
            {
                cursor.moveToFirst();
            }
            paidpopupstatus = cursor.getString(0);
        }catch(Exception ex){
            if(cursor != null)
                cursor.close();
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - getshowcashpaidpopupstatus", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        } finally{
            if(cursor != null)
                cursor.close();
        }
        return paidpopupstatus;
    }

    public String Getfreeitem_totaldiscount(String fromdate)
    {
        Cursor mCur =null;
        String total_discount="";
        try{
            String sql;



            sql="select coalesce(sum(amount),0) as amount from(" +
                    "SELECT sum(a.amount) as amount FROM tblsalesorderitemdetails as a inner join " +
                    " tblitemmaster as i on i.itemcode=a.itemcode  inner join tblunitmaster as " +
                    " u on u.unitcode=i.unitcode inner join tblsalesorder as d on d.transactionno=a.transactionno " +
                    " inner join tblitemsubgroupmaster as e on " +
                    " e.itemsubgroupcode=i.itemsubgroupcode inner join tblbrandmaster as g on g.brandcode=i.brandcode " +
                    " where    datetime(d.billdate) = datetime('"+fromdate+"')" +
                    " and d.flag!=3 and d.flag!=6 and d.status<>2 and a.freeitemstatus='freeitem' group by a.itemcode " +
                    " order by  e.itemgroupcode,e.itemsubgroupcode,g.brandname,i.itemcategory desc " +
                    " ) as dev";

            //parentcode,itemorder,i.itemname

            //group by a.itemcode,itemname,itemnametamil,u.unitname
            mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur.getString(0);
    }

    public String getjurisdiction(){
        Cursor cursor=null;
        String jurisdiction = "";
        try{
            String sql="select jurisdiction from tblgeneralsettings limit 1";
            cursor = mDb.rawQuery(sql,null);
            if (cursor.getCount() > 0)
            {
                cursor.moveToFirst();
            }
            jurisdiction = cursor.getString(0);
        }catch(Exception ex){
            if(cursor != null)
                cursor.close();
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() +" - getjurisdiction", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        finally{
            if(cursor != null)
                cursor.close();
        }
        return jurisdiction;
    }

    public String getTotalOrderQuantity(String itemcode){
        Cursor cursor=null;
        String date=GenCreatedDate();
        String totalOrderQuantity = "";
        try{
            String sql="select coalesce(sum(a.qty),0.0) as totalqty from tblsalesorderitemdetails as a " +
                    " inner join tblsalesorder as b on b.transactionno=a.transactionno and a.financialyearcode=b.financialyearcode and a.vancode=b.vancode and a.companycode=b.companycode " +
                    " where a.itemcode='"+itemcode+"' and  b.status<>2 and b.flag<>3 and " +
                    " a.financialyearcode='"+preferenceMangr.pref_getString("getfinanceyrcode")+"' and a.vancode='"+preferenceMangr.pref_getString("getvancode")+"' and  " +
                    " date(a.createddate)=date('"+date+"')";

            cursor = mDb.rawQuery(sql,null);
            if (cursor.getCount() > 0)
            {
                cursor.moveToFirst();
            }
            totalOrderQuantity = cursor.getString(0);
        }catch(Exception ex){
            if(cursor != null)
                cursor.close();
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - getTotalOrderQuantity", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        finally{
            if(cursor != null)
                cursor.close();
        }
        return totalOrderQuantity;
    }

    public String getVanname(String schedulecode){
        Cursor cursor=null;
        String date= GenCreatedDate();
        String vanname = "";
        try{
            String sql="select b.vanname from tblsalesschedule as a " +
                    " inner join tblvanmaster as b on a.vancode=b.vancode " +
                    " where a.schedulecode='"+schedulecode+"' and date(a.scheduledate)=date('"+date+"') ";

            cursor = mDb.rawQuery(sql,null);
            if (cursor.getCount() > 0)
            {
                cursor.moveToFirst();
            }
            vanname = cursor.getString(0);
        }catch(Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - getVanname", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        } finally{
            if(cursor != null)
                cursor.close();
        }
        return vanname;
    }

    public void ClearLocalDB(){
        Cursor cursor=null;
        try{
            String sql_previousdate="select date((select date(scheduledate) from tblsalesschedule order by scheduledate desc limit 1),('-'|| a.restrictmobileappdays ||' day') ) " +
                    " from tblgeneralsettings as a";

            /*String sql_previousdate="select * from( " +
                    " select date(scheduledate) as scheduledate from tblsalesschedule  as a " +
                    " where a.schedulecode in (select schedulecode from tblclosecash where status='2' and closedate=a.scheduledate) and schedulecode in (select schedulecode from tblclosesales where closedate=a.scheduledate) " +
                    " order by scheduledate asc limit ( select restrictmobileappdays from tblgeneralsettings ) ) " +
                    " limit 1;";*/

            cursor = mDb.rawQuery(sql_previousdate,null);
            if (cursor.getCount() > 0)
            {
                cursor.moveToFirst();
            }
            String previousdate=cursor.getString(0);

            if(!previousdate.equals("") && !previousdate.equals("null") && !previousdate.equals(null) ) {

                String sql = "delete from  tblsalesitemdetails where date(createddate) < date('" + previousdate + "');";
                mDb.execSQL(sql);
                String sql1 = "delete from tblsales where date(createddate) < date('" + previousdate + "');";
                mDb.execSQL(sql1);
                String sql2 = "delete from tblsalesreturnitemdetails where date(createddate) < date('" + previousdate + "');";
                mDb.execSQL(sql2);
                String sql3 = "delete from tblsalesreturn where date(createddate) < date('" + previousdate + "');";
                mDb.execSQL(sql3);
                String sql4 = "delete from tblsalesorderitemdetails where date(createddate) < date('" + previousdate + "'); ";
                mDb.execSQL(sql4);
                String sql5 = "delete from tblsalesorder where date(createddate) < date('" + previousdate + "');";
                mDb.execSQL(sql5);
                String sql6 = "delete from tblreceipt where date(createddate) < date('" + previousdate + "');";
                mDb.execSQL(sql6);
                String sql7 = "delete from tblexpenses where date(createdate) < date('" + previousdate + "');";
                mDb.execSQL(sql7);
                String sql8 = "delete from tblcashreport where date(createddate) < date('" + previousdate + "');";
                mDb.execSQL(sql8);
                String sql9 = "delete from tblclosecash where date(createddate) < date('" + previousdate + "');";
                mDb.execSQL(sql9);
                String sql10 = "delete from tblclosesales  where date(createddate) < date('" + previousdate + "');";
                mDb.execSQL(sql10);
                String sql11 = "delete from tblorderdetails  where date(createddate) < date('" + previousdate + "');";
                mDb.execSQL(sql11);
                String sql12 = "delete from tblstocktransaction where date(createddate) < date('" + previousdate + "') ;";
                mDb.execSQL(sql12);
                String sql13 = "delete from tblstocktransfer where date(createddate) < date('" + previousdate + "');";
                mDb.execSQL(sql13);
                String sql14 = "delete from tblvanstockverification where date(createddate) < date('" + previousdate + "') ;";
                mDb.execSQL(sql14);
                String sql15 = "delete from tblphysicalvanstock  where date(createddate) < date('" + previousdate + "');";
                mDb.execSQL(sql15);
                String sql16 = "delete from tblsalesschedule where date(createddate) < date('" + previousdate + "') ;";
                mDb.execSQL(sql16);
                String sql17 = "delete from tblappstocktransactiondetails where date(createddate) < date('" + previousdate + "') ";
                mDb.execSQL(sql17);
            }
        }catch(Exception ex){
            if(cursor != null)
                cursor.close();
            insertErrorLog(ex.toString(), this.getClass().getSimpleName()+ " - ClearLocalDB", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        finally {
            if(cursor != null)
                cursor.close();
        }

    }

    public String getTotalSalesQuantity(String itemcode){
        Cursor cursor=null;
        String date=GenCreatedDate();
        try{
            String sql="select coalesce(sum(a.qty),0.0) as totalqty from tblsalesitemdetails as a " +
                    " inner join tblsales as b on b.transactionno=a.transactionno and a.financialyearcode=b.financialyearcode and a.vancode=b.vancode and a.companycode=b.companycode " +
                    " where a.itemcode='"+itemcode+"' and  b.flag<>3 and  b.flag<>6 and " +
                    " a.financialyearcode='"+preferenceMangr.pref_getString("getfinanceyrcode")+"' and a.vancode='"+preferenceMangr.pref_getString("getvancode")+"' and  " +
                    " date(a.createddate)=date('"+date+"')";

            cursor = mDb.rawQuery(sql,null);
            if (cursor.getCount() > 0)
            {
                cursor.moveToFirst();
            }
        }catch(Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return cursor.getString(0);
    }

    //Get Schedule Datas
    public Cursor GetTodayScheduleDatasDB()
    {
        Cursor mCur = null;
        try{
            String sql ="select * from tblsalesschedule order by scheduledate desc limit 1";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    public String GetCustomerMobileNumber(String customercode)
    {
        String custNumber = "0";
        try{
            String sql ="select coalesce(mobileno,'') as mobileno from tblcustomer where customercode= '" + customercode + "' ";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                custNumber =  mCur.getString(0);
            }else{
                custNumber =  "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return custNumber;
    }

    public void insertPDFFile_LocalPath(String billdate,String transactionno,String bookingno,
                                        String financialyearcode,String companycode,String billno,String filePath){
        try{
            //billno='" + billno + "' and
            String sql ="update tblsales set pdflocalpath='" + filePath + "' where strftime('%d-%m-%Y',billdate) = '" + billdate + "' and transactionno='" + transactionno + "' and " +
                    "  bookingno='" + bookingno + "' and financialyearcode=" + financialyearcode + " and companycode=" + companycode + " ";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
    }

    public void insertImageFile_LocalPath(String companycode,String venderid,String filePath){
        try{

            String sql ="update tblcompanyvenderdetails set localfilepath='" + filePath + "' where companycode = '" + companycode + "' " +
                    "and venderid=" + venderid + " ";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
    }

    public String GetBillInVoicePDFLocalURL(String billdate,String transactionno,String bookingno,
                                            String financialyearcode,String companycode,String billno)
    {
        String custNumber = "0";
        try{
            String sql ="select coalesce(pdflocalpath,'') as filepath from tblsales where strftime('%d-%m-%Y',billdate) = '" + billdate + "' and transactionno='" + transactionno + "' and " +
                    " billno='" + billno + "' and bookingno='" + bookingno + "' and financialyearcode=" + financialyearcode + " and companycode=" + companycode + " ";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                custNumber =  mCur.getString(0);
            }else{
                custNumber =  "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return custNumber;
    }
    public String GetBillInVoiceURL(String billdate,String transactionno,String bookingno,
                                    String financialyearcode,String companycode,String billno)
    {
        String einvoice = "0";
        try{
            String sql ="select coalesce(einvoiceurl,'') as einvoice from tblsales where strftime('%d-%m-%Y',billdate) = '" + billdate + "' and transactionno='" + transactionno + "' and " +
                    " billno='" + billno + "' and bookingno='" + bookingno + "' and financialyearcode=" + financialyearcode + " and companycode=" + companycode + " ";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                einvoice =  mCur.getString(0);
            }else{
                einvoice =  "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return einvoice;
    }
    public String GetBillInVoicecancelstatus(String billdate,String transactionno,String bookingno,
                                             String financialyearcode,String companycode,String billno)
    {
        String custNumber = "0";
        try{
            String sql ="select coalesce(einvoice_status,0) as einvoice_status from tblsales where strftime('%d-%m-%Y',billdate) = '" + billdate + "' and transactionno='" + transactionno + "' and " +
                    " billno='" + billno + "' and bookingno='" + bookingno + "' and financialyearcode=" + financialyearcode + " and companycode=" + companycode + " ";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                custNumber =  mCur.getString(0);
            }else{
                custNumber =  "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return custNumber;
    }
    public String updateSalesEinvoice(String gettransactionno, String getfinancialyr,
                                      int Status,String geteinvoiceurl,String getirn_no,
                                      String getack_no,String getackdate,String geteinvoice_status,
                                      String geteinvoiceresponse,String geteinvoiceqrcodeurl,String getcompanycode)
    {
        Cursor mCurs =null;
        try{
            if(Status==1) {

                String exequery = "UPDATE tblsales set einvoiceurl='"+geteinvoiceurl+"'," +
                        " irn_no='"+getirn_no+"', ack_no='"+getack_no+"',ackdate='"+getackdate+"'" +
                        ", einvoice_status='"+geteinvoice_status+"',einvoiceresponse='"+geteinvoiceresponse+"'" +
                        ", einvoiceqrcodeurl='"+geteinvoiceqrcodeurl+"' where transactionno='"+gettransactionno+"' and" +
                        " financialyearcode ='"+getfinancialyr+"' and companycode='"+getcompanycode+"' ";
                mDb.execSQL(exequery);
            }else{
                String exequery = "UPDATE tblsales set einvoiceresponse='"+geteinvoiceresponse+"' where transactionno='"+gettransactionno+"' and" +
                        " financialyearcode ='"+getfinancialyr+"' and companycode='"+getcompanycode+"' ";
                mDb.execSQL(exequery);
            }

        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return "success";
    }
    //Get CheckPaymentVoucher
    public Cursor CheckEinvoice(String gettransactiono,String getcompanycode)
    {
        Cursor mCur=null;
        try{
            String sql="";

            String getdate= GenCreatedDate();

            sql = "select coalesce(count(*),'0') from tblsales  ";
            Cursor mCur1 = mDb.rawQuery(sql, null);
            if (mCur1.getCount() > 0)
            {
                mCur1.moveToFirst();
            }
            //  if(!mCur1.getString(0).equals("0")) {

            sql = "select gstin,coalesce(einvoice_status,0),coalesce(einvoiceurl,''),coalesce(pdflocalpath,'')" +
                    ",(select count(*) from  tblsales where (einvoiceurl is not NULL) " +
                    " and vancode=a.vancode and date(billdate)=date('"+preferenceMangr.pref_getString("getformatdate")+"')" +
                    " and transactionno=a.transactionno) as count from tblsales as a " +
                    " where  vancode = '" + preferenceMangr.pref_getString("getvancode") + "' and" +
                    " gstin!='' and date(billdate)=date('"+preferenceMangr.pref_getString("getformatdate")+"') " +
                    " and  transactionno = '"+gettransactiono+"' and companycode = '"+getcompanycode+"'  order by transactionno desc  ";
            // billdate = datetime('" + getdate + "') and
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0) {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
        /*}else{
            return mCur1;
        }*/

    }
    public void syncsalescancelandeinvoice (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;
            Cursor cursor = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);
                            if(obj.getString("einvoicestatus").equals("einvoicecancelled")){
                                String sql = "update tblsales set flag=3 where transactionno='"+obj.getString("transactionno")+"' and " +
                                        " financialyearcode='"+obj.getString("financialyearcode")+"' and vancode='"+preferenceMangr.pref_getString("getvancode")+"' and " +
                                        " companycode='"+obj.getString("companycode")+"' ";
                                mDb.execSQL(sql);

                                String sql1 = "update tblsalesitemdetails set flag=3 where transactionno='"+obj.getString("transactionno")+"' and " +
                                        " financialyearcode='"+obj.getString("financialyearcode")+"' and vancode='"+preferenceMangr.pref_getString("getvancode")+"' and" +
                                        " companycode='"+obj.getString("companycode")+"'  ";
                                mDb.execSQL(sql1);
                            }

                            String exequery = "UPDATE tblsales set einvoiceurl='"+obj.getString("downloadURL")+"'," +
                                    " irn_no='"+obj.getString("Irn")+"', ack_no='"+obj.getString("AckNo")+"'," +
                                    "ackdate='"+obj.getString("AckDt")+"'" +
                                    ", einvoice_status='"+obj.getString("einvoice_status")+"',einvoiceresponse='"+obj.getString("einvoiceresponse")+"' where transactionno='"+obj.getString("transactionno")+"' and" +
                                    " financialyearcode ='"+obj.getString("financialyearcode")+"' and companycode='"+obj.getString("companycode")+"' ";
                            mDb.execSQL(exequery);
//                            }



                        } catch (JSONException ex) {
                            if(cursor != null)
                                cursor.close();
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        } finally {
                            if(cursor != null)
                                cursor.close();
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                if(cursor != null)
                    cursor.close();
                insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            } finally {
                if(cursor != null)
                    cursor.close();
            }
        }
    }
    //Get Close sales details
    public String GetSalesPersonname(String getschedulecode)
    {
        String getsalesperson = "0";
        try{
            String sql ="SELECT (select employeename from tblemployeemaster where employeecode=a.employeecode) as salespersonname  from tblsalesschedule as a\n" +
                    "where schedulecode = '"+getschedulecode+"' ";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                getsalesperson =  mCur.getString(0);
            }else{
                getsalesperson =  "0";
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return getsalesperson;
    }
    //Get Company details for print
    public Cursor getCompanydetailsforprint()
    {
        Cursor mCur=null;
        try{

            String sql ="SELECT companycode,companyname,shortname from tblcompanymaster; ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - getCompanydetailsforprint", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return mCur;
    }
    //Get Company details for print
    public Cursor getbillcountdetailsforprint(String getschedulecode,String companycode)
    {
        Cursor mCur=null;
        try{

            String sql ="SELECT 'Cash' as billtype,count(a.billno) as billcount ," +
                    " case when count(a.billno)=0 then 0.00 else cast(sum(coalesce((grandtotal),0)) as decimal(32,3)) end as grandtotal" +
                    " from tblsales as a where a.flag!=3 and schedulecode='"+getschedulecode+"' and billtypecode=1  and companycode="+companycode+"" +
                    " union all " +
                    " SELECT 'Credit' as billtype,count(a.billno) as billcount ," +
                    " case when count(a.billno)=0 then 0.00 else cast(sum(coalesce((grandtotal),0)) as decimal(32,3)) end as grandtotal" +
                    " from tblsales as a where a.flag!=3 and schedulecode='"+getschedulecode+"' and billtypecode=2  and companycode="+companycode+"" +
                    " union all" +
                    " SELECT 'Sales Return' as billtype,count(a.billno) as billcount," +
                    " case when count(a.billno)=0 then 0.00 else cast(sum(coalesce((grandtotal),0)) as decimal(32,3)) end as grandtotal" +
                    " from tblsalesreturn as a where a.flag!=3 and schedulecode='"+getschedulecode+"' and companycode="+companycode+"; ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - getbillcountdetailsforprint", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return mCur;
    }

    //Get free item qty for not scheme bill
    public Cursor getexcessfreeitemqty()
    {
        Cursor mCur=null;
        Cursor mCur2=null;
        try{
            String getscheduledate="";
            String sql1 ="select scheduledate from tblsalesschedule where vancode='"+ preferenceMangr.pref_getString("getvancode") +"' " +
                    " order by scheduledate desc limit 1 ";
            mCur2 = mDb.rawQuery(sql1, null);

            if (mCur2.getCount() > 0)
            {
                mCur2.moveToFirst();
                getscheduledate = mCur2.getString(0);
            }else{
                getscheduledate = "";
            }

            String sql ="SELECT (itemnametamil || ' - '|| (SELECT unitname from tblunitmaster where unitcode=c.unitcode) )" +
                    "as itemname,excessfreeqty,c.itemcode,coalesce((select Sum(op)+Sum(inward)-sum(outward)" +
                    " from tblstocktransaction where itemcode=c.itemcode and " +
                    "datetime(transactiondate)<=datetime('"+getscheduledate+"') and type!='sales' and  " +
                    "type!='salescancel' and flag!=3),0) as op,coalesce((select sum(op)+sum(inward)-sum(outward) from " +
                    "tblstocktransaction where itemcode=c.itemcode and datetime(transactiondate)<=datetime('"+getscheduledate+"') " +
                    "and flag!=3 ),0) as balancestk,coalesce((select Sum(outward) from tblstocktransaction where " +
                    "itemcode=c.itemcode and datetime(transactiondate)=datetime('"+getscheduledate+"') and" +
                    " (type='sales' or type='salescancel') and flag!=3 ),0) as distributedstk" +
                    " FROM (SELECT CAST(weight/purchaseqty as INTEGER)*freeqty as " +
                    " excessfreeqty,purchaseqty,freeqty,itemcode  FROM (SELECT sum(weight) as weight,purchaseqty," +
                    "freeqty,freeitemcode as itemcode FROM (SELECT itemcode,b.weight,d.schemecode,d.purchaseqty," +
                    "d.freeqty,d.freeitemcode from tblsales as a inner join tblsalesitemdetails as b on " +
                    "b.transactionno=a.transactionno and a.vancode=b.vancode and a.companycode=b.companycode INNER JOIN " +
                    "tblschemeitemdetails as c on b.itemcode=c.purchaseitemcode inner join tblscheme as d on " +
                    "c.schemecode=d.schemecode  where  date(billdate)= date('"+getscheduledate+"') and " +
                    "a.schemeapplicable = 'no' and d.status = 'Active'" +
                    " and ((billdate between validityfrom and validityto) or (validityfrom<=billdate and validityto is " +
                    " null))) as dev GROUP BY schemecode,purchaseqty,freeqty) as dev ) as t1" +
                    " inner join tblitemmaster as c on t1.itemcode = c.itemcode";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - getexcessfreeitemqty", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return mCur;
    }

    public String insertSalesReceipt(String companycode,
                                     String vancode,String receiptmode,
                                     String amount,String financialyearcode,
                                     String venderid, String upitransactionID, String transactionno)

    {
        String transno= "1";
        try{
            String Gencode= GenCreatedDate();
            mDb = mDbHelper.getReadableDatabase();
            String getcurtime = GetDateTime();


            String sqlc = "select  count(*) from tblreceipt " ;
            Cursor mCurc = mDb.rawQuery(sqlc, null);
            mCurc.moveToFirst();
            String getcount= (mCurc.moveToFirst())?mCurc.getString(0):"0";


            if(getcount.equals("0")){
                String sql1 = "select coalesce(transactionno,0) from tblmaxcode where code=4  ";
                Cursor mCur = mDb.rawQuery(sql1, null);
                mCur.moveToFirst();
                transno = (mCur.moveToFirst()) ? mCur.getString(0) : "0";
                if(transno.equals("0")){
                    transno ="1";
                }
            }else {
                String sql1 = "select coalesce(max(transactionno),0)+1 from tblreceipt";
                Cursor mCur = mDb.rawQuery(sql1, null);
                mCur.moveToFirst();
                transno = (mCur.moveToFirst()) ? mCur.getString(0) : "0";
            }
            String receiptCountSql = "SELECT count(voucherno) AS count FROM tblreceipt WHERE " +
                    "type = 'Sales' AND companycode = '"+ companycode +"' AND voucherno = (SELECT " +
                    "billno FROM tblsales WHERE transactionno = '"+ transactionno +"' AND " +
                    "financialyearcode = '" + financialyearcode +"' and companycode = '"+ companycode +"') " +
                    "AND financialyearcode = '" + financialyearcode +"' AND receiptmode = '"+ receiptmode +"'";
            Cursor mCurCount = mDb.rawQuery(receiptCountSql, null);
            mCurc.moveToFirst();
            String getReceiptcount= (mCurCount.moveToFirst())?mCurCount.getString(0):"0";
            if(getReceiptcount.equals("0")) {
                String sql = "INSERT INTO tblreceipt(autonum,transactionno,receiptdate,prefix,suffix,voucherno,refno,companycode,vancode," +
                        "customercode,schedulecode,receiptremarkscode,receiptmode,chequerefno,amount," +
                        "makerid,createddate,financialyearcode,flag,note,syncstatus,receipttime,chequebankname," +
                        "chequedate,venderid,transactionid,type) SELECT (select coalesce(max(autonum),0)+1 from tblreceipt)," +
                        "'" + transno + "',billdate,prefix,suffix,billno," +
                        "refno,companycode,vancode,customercode,schedulecode,1,'" + receiptmode + "','','" + amount + "',makerid,datetime('now', 'localtime')," +
                        "financialyearcode,flag,'',0,'" + getcurtime + "','','','" + venderid + "','" + upitransactionID + "','Sales' FROM tblsales WHERE vancode = " +
                        "'" + vancode + "' AND transactionno = '" + transactionno + "'  AND " +
                        "financialyearcode ='" + financialyearcode + "' AND companycode = '" + companycode + "' ";
                mDb.execSQL(sql);
            } else {
                String sql = "UPDATE tblreceipt SET receiptremarkscode = 1, receiptmode='" + receiptmode + "', amount= '" + amount+ "', " +
                        "createddate=datetime('now', 'localtime'), receipttime='" + getcurtime + "', " +
                        "transactionid='"+ upitransactionID +"', venderid='"+ venderid +"', syncstatus = 0 " +
                        "WHERE type = 'Sales' AND voucherno=(SELECT billno FROM tblsales WHERE " +
                        "transactionno = '"+ transactionno +"' AND financialyearcode = '"+ financialyearcode +"' " +
                        "and companycode = '"+ companycode +"') AND companycode='"+ companycode +"' " +
                        "AND vancode= '"+ vancode +"' AND financialyearcode= '"+ financialyearcode +"' AND receiptmode = '"+ receiptmode +"'";
                mDb.execSQL(sql);
            }


        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "insertReceipt", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return  "success";
    }
    //Get Items
    public Cursor GetItemDB(String itemcode,String getroutecode,String getareacode)
    {
        Cursor mCur = null;
        try{
            /*and b.transactiondate=datetime('"+getdate+"')*/
            String getdate = GenCreatedDate();
            String getbusinesstype = "";
            if(preferenceMangr.pref_getString(Constants.KEY_GETBUSINESSTYPE).equals("2")){
                getbusinesstype=" (b.business_type = 2 or b.business_type = 3) ";
            }else  if(preferenceMangr.pref_getString(Constants.KEY_GETBUSINESSTYPE).equals("1")){
                getbusinesstype="(b.business_type = 1 or b.business_type = 3)";
            }else{
                getbusinesstype="(b.business_type = 1 or  b.business_type = 2 or b.business_type = 3)";
            }

            String getitembusinesstype = "";
            if(preferenceMangr.pref_getString(Constants.KEY_GETBUSINESSTYPE).equals("2")){
                getitembusinesstype=" (a.business_type = 2 or a.business_type = 3) ";
            }else  if(preferenceMangr.pref_getString(Constants.KEY_GETBUSINESSTYPE).equals("1")){
                getitembusinesstype="(a.business_type = 1 or a.business_type = 3)";
            }else{
                getitembusinesstype="(a.business_type = 1 or  a.business_type = 2 or a.business_type = 3)";
            }
        /*String sql =" select a.itemcode,a.companycode,a.brandcode,a.manualitemcode,a.itemname,a.itemnametamil,a.unitcode," +
                " a.unitweightunitcode,a.unitweight,a.uppunitcode,a.uppweight,a.itemcategory,a.parentitemcode," +
                " a.allowpriceedit,a.allownegativestock,a.allowdiscount, (sum(b.op)+sum(b.inward)-sum(b.outward)) as stockqty," +
                " (Select unitname  from tblunitmaster where unitcode=a.unitcode) as unitname," +
                " coalesce((Select noofdecimals from tblunitmaster where unitcode=a.unitcode),0) as noofdecimals," +
                " coalesce((select oldprice from tblitempricelisttransaction where itemcode=a.itemcode " +
                " order by autonum desc limit 1),0) as oldprice,coalesce((select newprice from tblitempricelisttransaction" +
                " where itemcode=a.itemcode order by autonum desc limit 1),0) as newprice,coalesce((select colourcode " +
                " from tblcompanymaster where companycode=a.companycode),'#000000') as colourcode,coalesce(c.hsn,'') " +
                " as hsn,coalesce(c.tax,'') as tax,(select allowpriceedit from tblroutedetails where routecode='"+getroutecode+"' and areacode='"+getareacode+"')" +
                " as routeallowpricedit,case when parentitemcode=0 then a.itemcode else parentitemcode " +
                " end as parentcode,case when itemcategory='parent' then 1 else  2 end as itemorder," +
                " c.itemsubgroupname,d.brandname " +
                " from tblitemmaster as a inner join tblstocktransaction as b " +
                " on a.itemcode=b.itemcode inner join tblitemsubgroupmaster as c on" +
                " c.itemsubgroupcode=a.itemsubgroupcode inner join tblbrandmaster as d on a.brandcode=d.brandcode " +
                " where a.itemsubgroupcode ='"+itemsubgroupcode+"' and a.status='"+statusvar+"' " +
                " and b.vancode='"+LoginActivity.getvancode+"' and" +
                " a.companycode in (select companycode from tblcompanymaster where status='"+statusvar+"') " +
                " group by a.itemcode,a.companycode,a.brandcode,a.manualitemcode," +
                " a.itemname,a.itemnametamil,a.unitcode,a.unitweightunitcode,a.unitweight,a.uppunitcode,a.uppweight," +
                " a.itemcategory,a.parentitemcode,a.allowpriceedit,a.allownegativestock,a.allowdiscount" +
                " order by c.itemsubgroupname,d.brandname,parentcode,itemorder,a.itemname ";*/
            String sql = "select * from (select a.itemcode,a.companycode,a.brandcode,a.manualitemcode,a.itemname,a.itemnametamil,a.unitcode," +
                    " a.unitweightunitcode,a.unitweight,a.uppunitcode,a.uppweight,a.itemcategory,a.parentitemcode," +
                    " a.allowpriceedit,a.allownegativestock,a.allowdiscount, " +
                    " coalesce(coalesce((select sum(op)+sum(inward)-Sum(outward) from tblstocktransaction where itemcode=a.itemcode" +
                    " and vancode='"+preferenceMangr.pref_getString(Constants.KEY_GETVANCODE)+"' and flag!=3),0) + coalesce((select sum(inward)-Sum(outward) from tblstockconversion where itemcode=a.itemcode "  +
                    " and vancode='"+preferenceMangr.pref_getString(Constants.KEY_GETVANCODE)+"'  ),0),0)" +
                    " as stockqty," +

                    " (Select unitname  from tblunitmaster where unitcode=a.unitcode) as unitname," +
                    " coalesce((Select noofdecimals from tblunitmaster where unitcode=a.unitcode),0) as noofdecimals," +
                    " coalesce((select oldprice from tblitempricelisttransaction where itemcode=a.itemcode " +
                    " order by autonum desc limit 1),0) as oldprice,coalesce((select newprice from tblitempricelisttransaction " +
                    " where itemcode=a.itemcode order by autonum desc limit 1),0) as newprice,coalesce((select colourcode " +
                    " from tblcompanymaster where companycode=a.companycode),'#000000') as colourcode,coalesce(c.hsn,'') " +
                    " as hsn,coalesce(c.tax,'') as tax,(select allowpriceedit from tblroutedetails where routecode='"+getroutecode+"' and areacode='"+getareacode+"')" +
                    " as routeallowpricedit,case when parentitemcode=0 then a.itemcode else parentitemcode " +
                    " end as parentcode,case when itemcategory='parent' then 1 else  2 end as itemorder," +
                    " c.itemsubgroupname,d.brandname,(select count(*) from tblschemeratedetails as aa inner join " +
                    "  tblscheme as b on aa.schemecode=b.schemecode where aa.itemcode=a.itemcode and b.status='"+statusvar+"' " +
                    "  and (','||multipleroutecode||',') LIKE '%,"+ getroutecode +",%' " +
                    "  and(validityfrom<=datetime('"+getdate+"')) and (ifnull(validityto,'')='' or " +
                    " (validityfrom<=datetime('"+getdate+"') " +
                    " and  validityto>=datetime('"+getdate+"'))) and  "+getbusinesstype+") as ratecount," +
                    " (select count(*) from tblschemeitemdetails as ab  inner join  tblscheme as b " +
                    " on ab.schemecode=b.schemecode where ab.purchaseitemcode=a.itemcode and b.status='"+statusvar+"' and (','||multipleroutecode||',') " +
                    " LIKE '%,"+getroutecode+",%' and(validityfrom<=datetime('"+getdate+"')) and (ifnull(validityto,'')='' or (validityfrom<=datetime('"+getdate+"') " +
                    " and  validityto>=datetime('"+getdate+"')))  and "+getbusinesstype+" ) as freecount, " +
                    " coalesce(coalesce((select sum(op)+sum(inward)-Sum(outward) from tblstocktransaction where itemcode=a.parentitemcode " +
                    " and vancode='"+preferenceMangr.pref_getString(Constants.KEY_GETVANCODE)+"' and flag!=3),0) + " +
                    " coalesce((select sum(inward)-Sum(outward) from tblstockconversion where itemcode=a.parentitemcode " +
                    " and vancode='"+preferenceMangr.pref_getString(Constants.KEY_GETVANCODE)+"'  ),0),0) " +
                    " as parentstockqty, a.itemsubgroupcode "+
                    " from tblitemmaster as a  inner join tblitemsubgroupmaster as c on " +
                    " c.itemsubgroupcode=a.itemsubgroupcode inner join tblbrandmaster as d on a.brandcode=d.brandcode " +
                    " where  a.itemcode ='"+itemcode+"' and a.status='"+statusvar+"'  " +
                    " and (a.itemcode in (select itemcode from tblstocktransaction where  flag!=3) " +
                    " or parentcode in (select itemcode from tblstocktransaction where  flag!=3)) and " +
                    " a.companycode in (select companycode from tblcompanymaster where status='"+statusvar+"' ) ) as dec                " +
                    " where (itemcategory='child'  and (parentstockqty>0 or stockqty>0) ) or (itemcategory= 'parent' and stockqty>0 )   " +
                    " order by   itemcategory desc,uppweight desc";
            //brandname
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0) {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    //Get Stock for parent item details
    public Cursor GetTempStockForItem(String getitemcode)
    {
        Cursor mCur = null;
        try{
            String sql ="select itemqty" +

                    " from tblsalescartdatas as a" +
                    " where a.itemcode='"+getitemcode+"'";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    public String insertOrderStockConversion(String getparentitemcode,String getinward,String getoutward,
                                             String getchilditemcode,String getschedulecode,String getbillno)
    {
        try{
            String GenDate= GenCreatedDate();
            mDb = mDbHelper.getReadableDatabase();
       /* String sql1 = "select coalesce(max(transactionno),0)+1 from tblstocktransaction";
        Cursor mCur = mDb.rawQuery(sql1, null);
        mCur.moveToFirst();
        String getmaxtransactionno = (mCur.moveToFirst()) ? mCur.getString(0) : "0";*/

            String sqlconvert = "select coalesce(max(transactionno),0)+1 from tblstockconversion";
            Cursor mCurconvert = mDb.rawQuery(sqlconvert, null);
            mCurconvert.moveToFirst();
            String getmaxstocktransactionno = (mCurconvert.moveToFirst()) ? mCurconvert.getString(0) : "0";

       /* String sqlstockoutward = "INSERT INTO  'tblstocktransaction' VALUES " +
                "('"+getmaxtransactionno+"',datetime('"+GenDate+"')," +
                " '"+LoginActivity.getvancode+"','"+ getparentitemcode +"',0,'"+ getoutward +"'," +
                " 'stockconversion','"+getmaxstocktransactionno+"',datetime('"+GenDate+"'),1,0,0)";
        mDb.execSQL(sqlstockoutward);

        String sqlstockinward = "INSERT INTO  'tblstocktransaction' VALUES " +
                "('"+getmaxtransactionno+"',datetime('"+GenDate+"')," +
                " '"+LoginActivity.getvancode+"','"+ getchilditemcode +"','"+ getinward +"',0," +
                " 'stockconversion','"+getmaxstocktransactionno+"',datetime('"+GenDate+"'),1,0,0)";
        mDb.execSQL(sqlstockinward);*/

            String sqlconvertinward = "INSERT INTO  'tblstockconversion' VALUES " +
                    "('"+getmaxstocktransactionno+"',datetime('"+GenDate+"')," +
                    " '"+preferenceMangr.pref_getString(Constants.KEY_GETVANCODE)+"','"+ getparentitemcode +"',0,'"+ getoutward +"'," +
                    " 'stockconversion','"+getschedulecode+"',datetime('"+GenDate+"'),'"+getbillno+"')";
            mDb.execSQL(sqlconvertinward);

            String sqlconvertoutward = "INSERT INTO  'tblstockconversion' VALUES " +
                    "('"+getmaxstocktransactionno+"',datetime('"+GenDate+"')," +
                    " '"+preferenceMangr.pref_getString(Constants.KEY_GETVANCODE)+"','"+ getchilditemcode +"','"+ getinward +"',0," +
                    " 'stockconversion','"+getschedulecode+"',datetime('"+GenDate+"'),'"+getbillno+"')";
            mDb.execSQL(sqlconvertoutward);



        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }


        return "success";

    }
    public Cursor GetCustomerOrderDB(String customercode)
    {
        Cursor mCur = null;
        try{
            getdate = GenCreatedDate();

            /* " and a.transactiondate=datetime('"+getdate+"') " +*/
            String sql ="select transactionno,strftime('%d-%m-%Y',billdate) as billdate,billno,cast(grandtotal as decimal(32,2)) as grandtotal,financialyearcode,companycode from tblsalesorder where status = '1' and flag<>3 and flag<>6 and customercode='"+customercode+"'";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }


    public Cursor Getemptyproductcount(String itemsubgroupcode){

        Cursor cur= null;
        try{
            String sql=" select count(*) from tblitemmaster where itemsubgroupcode='"+itemsubgroupcode+"' and withoutstocktransfer='yes' ";
            cur = mDb.rawQuery(sql, null);
            if (cur.getCount() > 0) {
                cur.moveToFirst();
            }
        }catch (Exception e){
            Log.d("Product count error : ",e.toString());
        }
        return cur;
    }


    //not purchased
    public String checkNotPurchasedDetails(String customercode){
        Cursor mCur = null;
        String result = Constants.RESULT_FAILURE;
        int count=-1;
        try{
            String getdate = GenCreatedDate();
            String sql ="select count(*) from " + Constants.TBL_NOT_PURCHASED + " where " + Constants.TBL_COLUMN_CUSTOMERCODE + "='" + customercode + "' and" +
                    " date(" + Constants.TBL_COLUMN_BILLDATE + ")=date('"+getdate+"') and " + Constants.TBL_COLUMN_FINANCIALYEARCODE + "=" + preferenceMangr.pref_getString(Constants.KEY_GETFINANCIALYEARCODE) +" and " +
                    Constants.TBL_COLUMN_VANCODE + "=" + preferenceMangr.pref_getString(Constants.KEY_GETVANCODE);
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0 && mCur.moveToFirst())
            {
                count = mCur.getInt(0);
            }

            if(count == 0)
                result = Constants.RESULT_SUCCESS;
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " Exception in checkNotPurchasedDetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return result;
    }


    // not purchased

    //Get not purchased remarks
    public Cursor getNotPurchasedRemarks()
    {
        Cursor mCur = null;
        try{
            String sql ="select " + Constants.TBL_NOT_PURCHASED_REMARKS_COLUMN_REMARKS + "," + Constants.TBL_NOT_PURCHASED_REMARKS_COLUMN_REMARKS_CODE + ""+
                    " from " + Constants.TBL_NOT_PURCHASED_REMARKS + ";";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - Exception in getNotPurchasedRemarks", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }


    public String insertNotPurchasedSales(String vancode,String billdate,String customercode,String schedulecode,
                                          String financialyearcode,String remarks,String latLong)//,String billlatitute,String billLongtitude)
    {
        try{
            String Gencode= GenCreatedDate();
            mDb = mDbHelper.getReadableDatabase();
            String getcurtime = GetDateTime();

            String sql="INSERT INTO " + Constants.TBL_NOT_PURCHASED + " (" + Constants.TBL_COLUMN_AUTONUM + ", " + Constants.TBL_COLUMN_VANCODE + "," +
                    Constants.TBL_COLUMN_BILLDATE +", " + Constants.TBL_COLUMN_CUSTOMERCODE + ", " + Constants.TBL_COLUMN_SCHEDULECODE + "," +
                    Constants.TBL_COLUMN_CREATEDDATE + "," + Constants.TBL_COLUMN_FINANCIALYEARCODE + "," + Constants.TBL_COLUMN_REMARKS + "," +
                    Constants.TBL_COLUMN_SYNCSTATUS + "," + Constants.TBL_COLUMN_SALESTIME + "," + Constants.TBL_COLUMN_LATLONG + ")" +
                    " values((select coalesce(max(autonum),0)+1 from tblsales),'"+ vancode +"'," +
                    " datetime('"+ billdate +"')," +
                    " '"+ customercode +"','"+ schedulecode +"'," +
                    " datetime('now', 'localtime')," +
                    " '"+ financialyearcode +"','"+ remarks +"' ,0,'"+getcurtime+"'," +
                    " '" + latLong + "' )";
            mDb.execSQL(sql);

            /*Cursor mCur=null;
            try{
                String custLatLong="";
                String sqlCustomerLatLng = "select (coalesce(latitude,null) || ',' || coalesce(longitude,null)) as latlong from tblcustomer " +
                        "where customercode='" + customercode + "' and status='Active' and areacode=" + preferenceMangr.pref_getString(Constants.KEY_GETAREACODE);
                mCur = mDb.rawQuery(sqlCustomerLatLng, null);
                if(mCur != null && mCur.getCount() > 0){
                    mCur.moveToFirst();
                    custLatLong = mCur.getString(0);
                }
                if(mCur != null)
                    mCur.close();
                if(Utilities.isNullOrEmpty(custLatLong) || custLatLong.equals(Constants.LATLONG_NULL_VALUE) ||
                        custLatLong.equals(Constants.LATLONG_ZERO_VALUE)){
                    String sqlupdateCustLatlong =  "UPDATE tblcustomer set latitude='" + billlatitute + "',longitude='" + billLongtitude + "'," +
                            " updateddate=datetime('now', 'localtime'), flag=1 " +
                            " where customercode='" + customercode + "' and status='Active' and areacode=" + preferenceMangr.pref_getString(Constants.KEY_GETAREACODE);
                    mDb.execSQL(sqlupdateCustLatlong);
                }
            }catch (Exception ex){
                insertErrorLog(ex.toString(), this.getClass().getSimpleName() + "insert notpurchased - Exception in customer master latlong insert", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }*/

            return Constants.RESULT_SUCCESS;
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - Exception in insertNotPurchasedSales", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            return Constants.RESULT_FAILURE;
        }
    }

    public Cursor GetNotPurchasedDatasDB(String ...syncDate)
    {
        Cursor mCur = null;
        try{
            String getdate="";
            if(syncDate != null && syncDate.length > 0 ){
                if(Utilities.isNullOrEmpty(syncDate[0]))
                    getdate = GenCreatedDate();
                else
                    getdate = syncDate[0];
            }else
                getdate = GenCreatedDate();

            String sql ="select * from " + Constants.TBL_NOT_PURCHASED + " where " + Constants.TBL_COLUMN_SYNCSTATUS + "=0 and date(" + Constants.TBL_COLUMN_BILLDATE + ")=date('"+getdate+"')";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - Exception in GetNotPurchasedDatasDB", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    public void UpdateNotPurchaseFlag(String customercode)
    {
        try{
            String billdate = GenCreatedDate();
            String sql = "update " + Constants.TBL_NOT_PURCHASED + " set  " + Constants.TBL_COLUMN_SYNCSTATUS + "=1 where " + Constants.TBL_COLUMN_SYNCSTATUS + "=0 and " +
                    Constants.TBL_COLUMN_CUSTOMERCODE + "='" + customercode + "' and date(" + Constants.TBL_COLUMN_BILLDATE + ")=date('" + billdate + "') and " +
                    Constants.TBL_COLUMN_VANCODE + "='" + preferenceMangr.pref_getString(Constants.KEY_GETVANCODE) + "' and " +
                    Constants.TBL_COLUMN_FINANCIALYEARCODE + "='" + preferenceMangr.pref_getString(Constants.KEY_GETFINANCIALYEARCODE) + "' ";
            mDb.execSQL(sql);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - Exception in UpdateNotPurchaseFlag", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

    }

    public void syncNotPurchasedRemarks (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    String sql1="DELETE FROM " + Constants.TBL_NOT_PURCHASED_REMARKS;
                    mDb.execSQL(sql1);
                    for(int i=0;i<json_category.length();i++)
                    {
                        try {
                            JSONObject obj = (JSONObject) json_category.get(i);

                            int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                            String sql = "INSERT INTO " + Constants.TBL_NOT_PURCHASED_REMARKS + " VALUES(" + Integer.toString(gc)
                                    + ",'" + obj.getString(Constants.TBL_NOT_PURCHASED_REMARKS_COLUMN_REMARKS_CODE) + "','" + obj.getString(Constants.TBL_NOT_PURCHASED_REMARKS_COLUMN_REMARKS).replaceAll("'","''") + "')";
                            mDb.execSQL(sql);

                        } catch (JSONException ex) {
                            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - Exception in syncNotPurchasedRemarks", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - Exception in syncNotPurchasedRemarks", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }
    public void syncnotpurchaseddetails (JSONObject object)
    {
        if(object!=null)
        {
            JSONArray json_category = null;

            try {
                String success = object.getString("success");
                if(success.equals("1"))
                {
                    json_category = object.getJSONArray("Value");
                    String sqlcount = "SELECT coalesce(count(*),0) FROM " + Constants.TBL_NOT_PURCHASED ;
                    Cursor mCur = mDb.rawQuery(sqlcount, null);
                    if (mCur.getCount() > 0)
                    {
                        mCur.moveToFirst();
                    }
                    if (mCur.getInt(0) == 0) {
                        for(int i=0;i<json_category.length();i++) {
                            try {
                                JSONObject obj = (JSONObject) json_category.get(i);

                                int gc = obj.isNull("autonum") ? 0 : obj.getInt("autonum");

                                String sql = "INSERT INTO " + Constants.TBL_NOT_PURCHASED + " ("+ Constants.TBL_COLUMN_AUTONUM + ", " + Constants.TBL_COLUMN_VANCODE + "," +
                                        Constants.TBL_COLUMN_BILLDATE +", " + Constants.TBL_COLUMN_CUSTOMERCODE + ", " + Constants.TBL_COLUMN_SCHEDULECODE + "," +
                                        Constants.TBL_COLUMN_CREATEDDATE + "," + Constants.TBL_COLUMN_UPDATEDDATE + "," + Constants.TBL_COLUMN_FINANCIALYEARCODE + "," + Constants.TBL_COLUMN_REMARKS + "," +
                                        Constants.TBL_COLUMN_SYNCSTATUS + "," + Constants.TBL_COLUMN_SALESTIME + "," + Constants.TBL_COLUMN_LATLONG +") " +
                                        " VALUES('" + gc + "','" + obj.getString(Constants.TBL_COLUMN_VANCODE) + "','" + obj.getString(Constants.TBL_COLUMN_BILLDATE) + "'," +
                                        "'" + obj.getString(Constants.TBL_COLUMN_CUSTOMERCODE) + "','" + obj.getString(Constants.TBL_COLUMN_SCHEDULECODE) + "'," +
                                        "'" + obj.getString(Constants.TBL_COLUMN_CREATEDDATE) + "','" + obj.getString(Constants.TBL_COLUMN_UPDATEDDATE) + "'," +
                                        "'" + obj.getString(Constants.TBL_COLUMN_FINANCIALYEARCODE) + "','" + obj.getString(Constants.TBL_COLUMN_REMARKS) + "',1," +
                                        "'" + obj.getString(Constants.TBL_COLUMN_SALESTIME) + "','" + obj.getString(Constants.TBL_COLUMN_LATLONG) + "')";
                                mDb.execSQL(sql);


                            } catch (JSONException ex) {

                                insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - Exception in syncnotpurchaseddetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                            }
                        }
                    }
                }
            } catch (JSONException ex) {
                // TODO Auto-generated catch block
                insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - Exception in syncnotpurchaseddetails", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            }
        }
    }

    public boolean getGPSTrackingStatus(){
        String status = "";
        Cursor cursor=null;
        try{
            String sql="select enablegpstracking from tblgeneralsettings limit 1";
            cursor = mDb.rawQuery(sql,null);
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                status = cursor.getString(0);
            }
            if(cursor != null)
                cursor.close();
            if(!Utilities.isNullOrEmpty(status) && (status.equals("yes") || status.equals("Yes") || status.equals("YES")))
                return true;
        }catch(Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - Exception in getjurisdiction", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            return false;
        }
        return false;
    }

    //order autoapproval settings
    public String GetOrderApproval() {
        Cursor mCur = null;
        try {
            String sql = "select coalesce(orderautoapproval,'yes') from tblgeneralsettings ";
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0) {
                mCur.moveToFirst();
            }
        } catch (Exception ex) {
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur.getString(0);
    }

    public String getCompanyName(String companyCode){
        Cursor cursor=null;
        try{
            String sql="select companyname from tblcompanymaster where companycode='" + companyCode + "'";
            cursor = mDb.rawQuery(sql,null);
            if (cursor.getCount() > 0)
            {
                cursor.moveToFirst();
            }
        }catch(Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - getCompanyName", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return cursor.getString(0);
    }

    public String UpdateCustomerLocation(String getcustomercode,double latitude,double longitude) {
        try{
            String getdatetime=GenCreatedDateTime();
            String sql1 = "UPDATE 'tblcustomer' SET " +
                    "latitude='" + latitude +"',longitude='" + longitude +"'," +
                    "flag=1,updateddate='"+getdatetime+"' " +
                    " WHERE customercode='" + getcustomercode +"' ";
            mDb.execSQL(sql1);
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - UpdateCustomerLocation", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return  "success";
    }

    public String UpdateSalesOrderCompletedFlag(String gettransactiono,String getfinancialyrcode)
    {
        try{
            if (Utilities.isNullOrEmpty(gettransactiono) || Utilities.isNullOrEmpty(getfinancialyrcode))
                return null;

            String sql = "update tblsalesorder set status=4,syncstatus=0,updateddate=datetime('now', 'localtime') where transactionno='"+gettransactiono+"' and " +
                    " financialyearcode='"+getfinancialyrcode+"' and vancode='"+preferenceMangr.pref_getString("getvancode")+"'  ";
            mDb.execSQL(sql);


        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return "success";
    }

    public String getCustomerMobileNumber(String customerCode){
        Cursor cursor=null;
        try{
            String sql="select mobileno from tblcustomer where customercode='" + customerCode + "'";
            cursor = mDb.rawQuery(sql,null);
            if (cursor.getCount() > 0)
            {
                cursor.moveToFirst();
            }
        }catch(Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - getCompanyName", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }
        return cursor.getString(0);
    }

    public Cursor getSalesBillForSMS(String transactionno, String customercode, String vancode, String schedulecode, String financialyercode,
                                     String companyCode, String from)
    {
        Cursor mCur = null;
        try{
            String getdate="";
            String queryCompany = "1=1";
            String queryCashPaidStatus = "(cashpaidstatus='' OR cashpaidstatus='no')";
            if (!Utilities.isNullOrEmpty(companyCode)) { // CompanyCode not null means UPI sms
                queryCompany = "a.companycode=" + companyCode;
                //queryCashPaidStatus = "1=1";
            }
            if (from.equals("ADD")){
                queryCashPaidStatus = "1=1";
            }

            String sql ="select billno, (case when discount>0 then subtotal - coalesce((select sum(amount) from tblsalesitemdetails where transactionno = a.transactionno and financialyearcode = a.financialyearcode and companycode=a.companycode and freeitemstatus='freeitem'),0) else grandtotal end) as grandtotal" +
                    " from tblsales as a" +
                    " where billtypecode=1 and " + queryCashPaidStatus + " and transactionno=" + transactionno + " and customercode='" + customercode + "' and vancode=" + vancode + " and schedulecode='" + schedulecode + "' and financialyearcode=" + financialyercode + " and " + queryCompany;
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - Exception in getSalesBillForSMS", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    public Cursor getReceiptForSMS(String transactionno, String customercode, String vancode, String schedulecode, String financialyercode)
    {
        Cursor mCur = null;
        try{
            String getdate="";

            String sql ="select * from tblreceipt " +
                    " where transactionno=" + transactionno + " and customercode='" + customercode + "' and vancode=" + vancode + " and schedulecode='" + schedulecode + "' and financialyearcode=" + financialyercode;
            mCur = mDb.rawQuery(sql, null);
            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - Exception in getReceiptForSMS", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return mCur;
    }

    public String getLastScheduleDate(String schedulecode)
    {
        String scheduleDate = "";
        try{
            String sql ="select strftime('%d-%m-%Y',scheduledate) as scheduledate from tblsalesschedule where vancode='"+ preferenceMangr.pref_getString(Constants.KEY_GETVANCODE) +"' and " +
                    " schedulecode='" + schedulecode + "' ";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur.getCount() > 0)
            {
                mCur.moveToFirst();
                scheduleDate = mCur.getString(0);
            }
        }catch (Exception ex){
            insertErrorLog(ex.toString(), this.getClass().getSimpleName() + " - Exception in getLastScheduleDate", String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
        }

        return scheduleDate;
    }

}