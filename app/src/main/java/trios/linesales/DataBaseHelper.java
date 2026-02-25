package trios.linesales;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper
{
    private static int DbVersion = 8;
    private static String DB_PATH;
    private static String DB_NAME = "dblinesales.db";
    private static SQLiteDatabase mDataBase;
    private final Context mContext;


//    public DataBaseHelper(Context mContext, String name, SQLiteDatabase.CursorFactory factory,
//                          int version) {
//        super(mContext, name, factory, version);
//    }

    public DataBaseHelper(Context context)
    {
        super(context, DB_NAME , null, DbVersion);
        if(android.os.Build.VERSION.SDK_INT >= 4.2){
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        }
        else
        {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        Log.i("dppath",DB_PATH);
        this.mContext = context;
    }

    public void createDataBase() throws IOException
    {
        boolean mDataBaseExist = checkDataBase();
        if(mDataBaseExist)
        {
            this.getWritableDatabase();
        }
        mDataBaseExist = checkDataBase();

        if(!mDataBaseExist)
        {
            this.getReadableDatabase();
            this.close();
            try
            {
                copyDataBase();
            }
            catch (IOException mIOException)
            {
                throw new Error("ErrorCopyingDataBase");
            }
        }
        /*openDataBase();
        upgradeDB();
        close();*/
    }

    private void upgradeDB(){

        // You will not need to modify this unless you need to do some android specific things.
        // When upgrading the database, all you need to do is add a file to the assets folder and name it:
        // from_1_to_2.sql with the version that you are upgrading to as the last version.
        try {
            readAndExecuteSQLScript(mDataBase, mContext, "Script.sql");
        } catch (Exception exception) {
            Log.e("Upgrade", "Exception running upgrade script:", exception);
        }
    }

    private void readAndExecuteSQLScript(SQLiteDatabase db, Context ctx, String fileName) {
        try{
            if (TextUtils.isEmpty(fileName)) {
                Log.d("Upgrade", "SQL script file name is empty");
                return;
            }

            Log.d("Upgrade" , "Script found. Executing...");
            AssetManager assetManager = ctx.getAssets();
            BufferedReader reader = null;

            try {
                InputStream is = assetManager.open(fileName);
                InputStreamReader isr = new InputStreamReader(is);
                reader = new BufferedReader(isr);
                executeSQLScript(db, reader);
            } catch (IOException e) {
                Log.e("Upgrade" , "IOException:", e);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e("Upgrade" , "IOException:", e);
                    }
                }
            }
        }
        catch (Exception exception) {
            Log.e("Upgrade", "Exception running upgrade script:", exception);
        }
    }

    private void executeSQLScript(SQLiteDatabase db, BufferedReader reader) throws IOException {
        try{
            String line;
            StringBuilder statement = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                statement.append(line);
                statement.append("\n");
                if (line.endsWith(";")) {
                    db.execSQL(statement.toString());
                    statement = new StringBuilder();
                }
            }
        }
        catch (Exception exception) {
            Log.e("Upgrade", "Exception running upgrade script:", exception);
        }
    }
    public String udfnBackupdb(Context context,PreferenceMangr preferenceMangr)
    {
        String outFileName ="";
        try
        {
            if(android.os.Build.VERSION.SDK_INT >= 4.2){
                DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
            }
            else
            {
                DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
            }
            final String inFileName = DB_PATH + DB_NAME;
            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);
            File sd = new File (Utilities.getExternalStoragePath(context) + "/DBBackup");
//            File sd = new File(Environment.getExternalStorageDirectory()+"/DBBackup");
            if(!sd.exists()){
                sd.mkdirs();
            }
            String fileName = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
            long date = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm");
            String dateString = sdf.format(date);
            fileName= preferenceMangr.pref_getString("getvanname") +"_db_"+dateString+".db";
            outFileName = sd+"/"+fileName;

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer))>0){
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();
        }
        catch (Exception e){
            Log.i("DBBackup",e.toString());
        }
        return outFileName;
    }
    private boolean checkDataBase()
    {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    private void copyDataBase() throws IOException
    {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer))>0)
        {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    public boolean openDataBase() throws SQLException
    {
        String mPath = DB_PATH + DB_NAME;
        mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.OPEN_READWRITE);
        return mDataBase != null;
    }

    @Override
    public synchronized void close()
    {
        if(mDataBase != null)
            mDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase arg0) {
//        mDataBase.execSQL(ProductMasterClass.TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < 3)
        {
            addLocationforcustomer(db);
        }
        if(oldVersion < 5){
            addcompanytype(db);
        }

        if(oldVersion < 6){
            addMaxReceiptAmtInGeneralSettings(db);
        }
        if (oldVersion < 7) {
            CreateStockReturnTable(db);
        }

        if (oldVersion < 8) {
            addProduct_Bill_Scheme(db);
        }
    }
    private void addLocationforcustomer(SQLiteDatabase db) {
        String querytblsales = "ALTER TABLE " + Constants.TBLSALES +
                " ADD COLUMN " + Constants.TBL_COLUMN_LATLONG + " TEXT DEFAULT null;";
        db.execSQL(querytblsales);
        String qrytblsalesorder = "ALTER TABLE " + Constants.TBLSALESORDER +
                " ADD COLUMN " + Constants.TBL_COLUMN_LATLONG + " TEXT DEFAULT null;";
        db.execSQL(qrytblsalesorder);
        String qryautoapproval="ALTER TABLE " + Constants.TBLGENERALSETTINGS +
                " ADD COLUMN " + Constants.COLUMNAUTOAPPROVAL + " TEXT DEFAULT null;";
        db.execSQL(qryautoapproval);
        String qryorgpricesalescartdatas="ALTER TABLE tblsalescartdatas ADD COLUMN  orgprice TEXT DEFAULT null";
        db.execSQL(qryorgpricesalescartdatas);
        String qryorgpricesalesitemdetails="ALTER TABLE tblsalesitemdetails ADD COLUMN  orgprice TEXT DEFAULT null";
        db.execSQL(qryorgpricesalesitemdetails);
        String qryorgpricetempsalesitemdetails="ALTER TABLE tbltempsalesitemdetails ADD COLUMN  orgprice TEXT DEFAULT null";
        db.execSQL(qryorgpricetempsalesitemdetails);

    }
    private void addcompanytype(SQLiteDatabase db) {
        String querytblitem = "ALTER TABLE tblitemmaster ADD COLUMN erpitemcode TEXT;";
        db.execSQL(querytblitem);

        String querytblcompany = "alter table tblcompanymaster add column companytype integer DEFAULT NULL;";
        db.execSQL(querytblcompany);

        String querysalescategory = "CREATE TABLE def_sales_category (\n" +
                "    sacid SERIAL PRIMARY KEY,\n" +
                "    sac_name TEXT ,\n" +
                "    sac_code INT \n" +
                ");\n";
        db.execSQL(querysalescategory);
        String querycustomermaster = "ALTER TABLE  tblcustomer ADD COLUMN categorycode INT;";
        db.execSQL(querycustomermaster);
        String querycustomermastererp = "ALTER TABLE  tblcustomer ADD COLUMN erpitemcode TEXT;";
        db.execSQL(querycustomermastererp);

        String querygeneralmaster = "ALTER TABLE  tblgeneralsettings ADD COLUMN maxbillamount FLOAT;";
        db.execSQL(querygeneralmaster);
        String querygeneralmastererp = "ALTER TABLE  tblgeneralsettings ADD COLUMN maxbillannualamount FLOAT";
        db.execSQL(querygeneralmastererp);

        String salesscheduletodate = "ALTER TABLE  tblsalesschedule ADD COLUMN scheduletodate TIMESTAMP WITHOUT TIME ZONE";
        db.execSQL(salesscheduletodate);
        String salesscheduletarget = "ALTER TABLE  tblsalesschedule ADD COLUMN target FLOAT";
        db.execSQL(salesscheduletarget);
        String salesschedulebudget = "ALTER TABLE  tblsalesschedule ADD COLUMN budget FLOAT";
        db.execSQL(salesschedulebudget);
    }

    private void addMaxReceiptAmtInGeneralSettings(SQLiteDatabase db) {
        String query = "ALTER TABLE tblgeneralsettings ADD COLUMN maxreceiptamt REAL default null;";
        db.execSQL(query);
        String query1 = "ALTER TABLE tblsalesordercartdatas ADD COLUMN actualamount TEXT default null;";
        db.execSQL(query1);
        String query2 = "ALTER TABLE tblsalesordercartdatas ADD COLUMN ratediscount TEXT default null;";
        db.execSQL(query2);
        String query3 = "ALTER TABLE tblsalesordercartdatas ADD COLUMN schemeapplicable TEXT default null;";
        db.execSQL(query3);
        String query4 = "ALTER TABLE tblsalesordercartdatas ADD COLUMN orgprice TEXT default null;";
        db.execSQL(query4);
    }

    private void CreateStockReturnTable(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS tblstockreturn (" +
                " autonum INTEGER NOT NULL," +
                " stocktransferno INTEGER NOT NULL," +
                " transactiondate datetime," +
                " vancode INTEGER NOT NULL," +
                " schedulecode TEXT NOT NULL," +
                " itemcode INTEGER NOT NULL," +
                " qty REAL NOT NULL," +
                " makerid INTEGER DEFAULT NULL," +
                " createddate datetime," +
                " flag INTEGER," +
                " status INTEGER" +
                ")";
        db.execSQL(query);
    }

    private void addProduct_Bill_Scheme(SQLiteDatabase db) {
        String query = "ALTER TABLE tblitemmaster ADD COLUMN product_scheme TEXT default null;";
        db.execSQL(query);
        String query1 = "ALTER TABLE tblitemmaster ADD COLUMN bill_scheme TEXT default null;";
        db.execSQL(query1);
        String query2 = "ALTER TABLE tblcustomer ADD COLUMN bill_scheme TEXT default null;";
        db.execSQL(query2);
        String query3 = "ALTER TABLE tblcustomer ADD COLUMN bill_scheme_disc_percentage TEXT default null;";
        db.execSQL(query3);
        String query4 = "ALTER TABLE tblsalescartdatas ADD COLUMN bill_scheme TEXT default null;";
        db.execSQL(query4);
        String query5 = "ALTER TABLE tbltempsalesitemdetails ADD COLUMN bill_scheme TEXT default null;";
        db.execSQL(query5);
        String query6 = "ALTER TABLE tblsales ADD COLUMN bill_scheme_disc_amount REAL default null;";
        db.execSQL(query6);
        String query7 = "ALTER TABLE tblsales ADD COLUMN bill_scheme_disc_removed TEXT default null;";
        db.execSQL(query7);
        String query8 = "ALTER TABLE tblgeneralsettings ADD COLUMN cash_disc_applicable TEXT default null;";
        db.execSQL(query8);
        String query9 = "ALTER TABLE tblsalesitemdetails ADD COLUMN bill_scheme TEXT default null;";
        db.execSQL(query9);
    }
}
