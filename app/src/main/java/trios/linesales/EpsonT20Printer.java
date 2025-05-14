package trios.linesales;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class EpsonT20Printer implements ReceiveListener {

    public BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    public BluetoothDevice mmDevice;

    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    int PRINTER_TIMEOUT = 3000;
    volatile boolean stopWorker;
    DecimalFormat dft = new DecimalFormat("0.00");
    public static Context mContext = null;
    public static Activity mActivity = null;
    SharedPreferences preferences = null;
    public static PreferenceMangr preferenceMangr = null;
    /* Printer printer = null;
     public boolean billPrinted = false;*/
    public static Printer mPrinter = null;
    private static final int DISCONNECT_INTERVAL = 500;
    public static ReceiveListener mReceiveListener = null;

    private EpsonT20Printer() {
    }

    public EpsonT20Printer(Activity activity, Context ctx, ReceiveListener receiveListener) {
        mContext = ctx;
        mActivity = activity;
        preferenceMangr = new PreferenceMangr(mContext);
        mReceiveListener = receiveListener;
        initializePrinter(ctx);
    }

    public void initializePrinter(Context ctx) {
        try {
            mPrinter = new Printer(Printer.TM_P20, Printer.MODE_GRAY16, ctx);
        } catch (Exception e) {
            Log.e("", "Exception in printer initialize : " + e.getLocalizedMessage());
        }

        mPrinter.setReceiveEventListener(this);
    }

    @Override
    public void onPtrReceive(Printer printer, int i, PrinterStatusInfo printerStatusInfo, String s) {
        if (mActivity == null)
            return;
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        disconnectPrinter();
                    }
                }).start();
            }
        });
    }

    public boolean connectPrinter() {
        if (mPrinter == null) {
            return false;
        }
        boolean res = false;
        try {
            mPrinter.connect("BT:"+preferenceMangr.pref_getString("SelectedPrinterAddress"),PRINTER_TIMEOUT);
            res = true;
            //mPrinter.beginTransaction();
        } catch (Exception e) {
            mPrinter.clearCommandBuffer();
            res = false;
            Log.d("PRINT", "printer failed to connect Exception "+e);
        }
        return res;
    }

    private void disconnectPrinter() {
        if (mPrinter == null) {
            return;
        }

        while (true) {
            try {
                mPrinter.disconnect();
                if (mReceiveListener != null)
                    mReceiveListener.onPtrReceive(null, 0, null, null);
                break;
            } catch (final Exception e) {
                if (e instanceof Epos2Exception) {
                    //Note: If printer is processing such as printing and so on, the disconnect API returns ERR_PROCESSING.
                    if (((Epos2Exception) e).getErrorStatus() == Epos2Exception.ERR_PROCESSING) {
                        try {
                            Thread.sleep(DISCONNECT_INTERVAL);
                        } catch (Exception ex) {
                        }
                    } else {
                        mActivity.runOnUiThread(new Runnable() {
                            public synchronized void run() {
                                if (mReceiveListener != null)
                                    mReceiveListener.onPtrReceive(null, 0, null, null);
                            }
                        });
                        break;
                    }
                } else {
                    mActivity.runOnUiThread(new Runnable() {
                        public synchronized void run() {
                            if (mReceiveListener != null)
                                mReceiveListener.onPtrReceive(null, 0, null, null);
                        }
                    });
                    break;
                }
            }
        }

        mPrinter.clearCommandBuffer();
    }

    private static final String[] getalphabet = {
            "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    };

    private static final String[] tensNames = {
            "",
            " Ten",
            " Twenty",
            " Thirty",
            " Torty",
            " Fifty",
            " Sixty",
            " Seventy",
            " Eighty",
            " Einety"
    };

    private static final String[] numNames = {
            "",
            " One",
            " Two",
            " Three",
            " Four",
            " Five",
            " Six",
            " Seven",
            " Eight",
            " Nine",
            " Ten",
            " Eleven",
            " Twelve",
            " Thirteen",
            " Fourteen",
            " Fifteen",
            " Sixteen",
            " Seventeen",
            " Eighteen",
            " Nineteen"
    };
    public static final String[] units = {"", "One", "Two", "Three", "Four",
            "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve",
            "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen",
            "Eighteen", "Nineteen"};

    public static final String[] tens = {
            "",        // 0
            "",        // 1
            "Twenty",    // 2
            "Thirty",    // 3
            "Forty",    // 4
            "Fifty",    // 5
            "Sixty",    // 6
            "Seventy",    // 7
            "Eighty",    // 8
            "Ninety"    // 9
    };

    public String GenCreatedDate() {
        //Date trueTime = TrueTimeRx.now();
        Date deviceTime = new Date();
        String dateString;
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        dateString = sdf.format(date);
        //   dateString = _formatDate(trueTime, "dd-MM-yyyy hh:mm a", TimeZone.getDefault());
        return dateString;
    }

    void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean findBT() {
        boolean status = false;
        try {
            preferenceMangr = new PreferenceMangr(mContext);
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter != null) {
                if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                    if (pairedDevices.size() > 0) {
                        for (BluetoothDevice device : pairedDevices) {
                            if (!device.getAddress().equals(preferenceMangr.pref_getString("SelectedPrinterAddress"))){}
                            else{

                                mmDevice = device;
                                status = true;
                            }
                            /*if (device.getName().equals("")){}
                            else{
                                mmDevice = device;
                                status = true;
                            }*/
                            //	String mac=mmDevice.getAddress();

                        }
                    }
                    else
                    {
                        status=false;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            status=false;
        }

        /*if (printer == null) {
            try {
                printer = new Printer(Printer.TM_P20, Printer.MODE_GRAY16, mContext);
            } catch (Epos2Exception e) {
                printer = null;

                e.printStackTrace();
                Log.d("PRINT", "printer failed to initialize class");
                return  false;

            }

            try {
                mPrinter.connect("BT:" + mBluetoothAdapter.getRemoteDevice(mmDevice.getAddress()), Printer.PARAM_DEFAULT);
                mPrinter.beginTransaction();
            } catch (Exception e) {
                printer = null;
                Log.d("PRINT", "printer failed to connect Exception " + e);
                return  false;
            }

        }*/


        return  status;
    }

    void openBT() throws IOException {
        try {

            UUID SPP_UUID =  UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            //		mmSocket =  mmDevice.createInsecureRfcommSocketToServiceRecord(SPP_UUID);

            mmSocket = mmDevice.createRfcommSocketToServiceRecord(SPP_UUID);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            beginListenForData();
            Log.i("connection", "open");

        } catch (Exception e) {
            e.printStackTrace();
            try {
                mmSocket =(BluetoothSocket) mmDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(mmDevice,2);
                mmSocket.connect();

                mmOutputStream = mmSocket.getOutputStream();
                mmInputStream = mmSocket.getInputStream();
                if(mmOutputStream == null){
                    //		Toast.makeText(PrintActivity.this,"OutputStream null",Toast.LENGTH_SHORT).show();
                }
                beginListenForData();

            } catch (NoSuchMethodException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (InvocationTargetException e1) {
                e1.printStackTrace();
            }
        }
    }

    void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // this is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {

                        try {
                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {

                                    byte b = packetBytes[i];
                                    if (b == delimiter) {

                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        readBuffer = new byte[encodedBytes.length];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );

                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
                                            }
                                        });

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("PrintData", e.getLocalizedMessage());
        }
    }

    //SAles Print
    @SuppressLint("LongLogTag")
    @SuppressWarnings("rawtypes")
    public boolean GetSalesBillPrint(String gettransactiono,String getfinancialyearcode,Activity objActivity, boolean printDC) {

        if (mPrinter == null) {
            initializePrinter(objActivity.getApplicationContext());
        }

        if (mPrinter == null)
            return false;

        PrinterStatusInfo status = mPrinter.getStatus();

        //Printer printer = null;
        boolean billPrinted = false;
        String jurisdiction="";
        String printheader ="";
        try {

            DataBaseAdapter mDbHelper = new DataBaseAdapter(mContext);
            mDbHelper.open();
            Cursor mCur = mDbHelper.GetSalesPrint(gettransactiono,getfinancialyearcode);
            Cursor mCurDetails = mDbHelper.GetSalesPaymentVoucherDetailsPrint(gettransactiono,getfinancialyearcode);

            jurisdiction = mDbHelper.getjurisdiction();

            if (mCur.getCount() > 0) {

                for (int i=0;i<mCur.getCount();i++ )
                {
                    mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_A);

                    /*String startmsg = "       உ        \n";
                    mPrinter.addText(startmsg);
                    String startmsgline = "       --        \n";
                    mPrinter.addText(startmsgline);*/
                    printheader=mDbHelper.getprintheader();

                    if(!printheader.equals("")&&!printheader.equals(null)&&!printheader.equals("null")){

                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        String startmsg = printheader+"\n";
                        mPrinter.addText(startmsg);
                        String startmsgline = "       --        \n";
                        mPrinter.addText(startmsgline);
                    }

                    if(SalesViewActivity.isduplicate){
                        String startmsgline1 = "       DUPLICATE        \n";
                        mPrinter.addText(startmsgline1);
                    }

                    mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addText(mCur.getString(0) +"\n");


                    mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_B);
                    mPrinter.addText(mCur.getString(1)+"\n");

                    mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_B);
                    //mPrinter.addText(mCur.getString(3) +" - " + mCur.getString(4)+"\n");
                    String companyCityStateDetails = mCur.getString(3) +" - " + mCur.getString(4);

                    // show statename and gst state code
                    String company_stateNameWithGSTStatecode = "";
                    if(!Utilities.isNullOrEmpty(mCur.getString(27)))
                        company_stateNameWithGSTStatecode = mCur.getString(27).trim();
                    if(!Utilities.isNullOrEmpty(mCur.getString(28)))
                        company_stateNameWithGSTStatecode = company_stateNameWithGSTStatecode + " - " + mCur.getString(28).trim();

                    if(!Utilities.isNullOrEmpty(company_stateNameWithGSTStatecode))
                        companyCityStateDetails = companyCityStateDetails.trim() + " , " + company_stateNameWithGSTStatecode.trim();

                    mPrinter.addText(companyCityStateDetails.trim() + "\n");

                   /* mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_B);
                    mPrinter.addText("Ph " + mCur.getString(5) +"  " + mCur.getString(6) + "    "+mCur.getString(10)+"\n");*/
                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_B);
                    mPrinter.addText("Ph  " + mCur.getString(5) +"  " + mCur.getString(6) );


                    mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addText("  "+mCur.getString(10)+"\n");


                    if(!Utilities.isNullOrEmpty(mCur.getString(7))) {
                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addText("GSTIN : " + mCur.getString(7) + "\n");
                    }

                    if(!Utilities.isNullOrEmpty(mCur.getString(8))) {
                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addText("FSSAI No : " + mCur.getString(8) + "\n");
                    }

                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_C);
                    mPrinter.addText("PAN : " + mCur.getString(9));


                    mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addText("         BK.No. "+mCur.getString(20) +"\n");

                    mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    String line_space6 = "--------------------------------";
                    mPrinter.addText(line_space6);

                    mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_B);

                    mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_A);

                    String billtype = " ";
                    if(mCur.getString(21).equals("1")){
                        billtype =  "CASH";
                    }else{
                        billtype =  "CREDIT";
                    }
                    String invoice="";
                    if(!Utilities.isNullOrEmpty(mCur.getString(7)) && mCur.getInt(29)!=2 &&
                            !Utilities.isNullOrEmpty(mCur.getString(32)) && !mCur.getString(32).equals("null")) {
                        invoice=" E-INVOICE";
                    }else{
                        invoice=" INVOICE";
                    }
                    mPrinter.addText(billtype+ invoice +"\n");

                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    String line_space5 = "--------------------------------";
                    mPrinter.addText(line_space5);



                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
//                    mPrinter.addTextFont(Printer.FONT_B);
                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addText("Invoice No. : " + mCur.getString(12)+"\n");

                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
//                    mPrinter.addTextFont(Printer.FONT_C);
                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addText("Invoice Date:" + mCur.getString(16) +" " +mCur.getString(22) +"\n");

//                    show einvoice irn_no and ack_no
                    if(!Utilities.isNullOrEmpty(mCur.getString(7)) && mCur.getInt(29)!=2){
                        if(!Utilities.isNullOrEmpty(mCur.getString(30))) {
                            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                            mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                            mPrinter.addTextFont(Printer.FONT_C);
                            mPrinter.addText("IRNNo. : " + mCur.getString(30)+"\n");
                        }
                        if(!Utilities.isNullOrEmpty(mCur.getString(31))) {
                            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                            mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
//                            mPrinter.addTextFont(Printer.FONT_A);
                            mPrinter.addTextFont(Printer.FONT_C);
                            mPrinter.addText("ACKNo. : " + mCur.getString(31) +"\n");
                        }


                    }


                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addText("Mr/Ms : " + mCur.getString(11) +"\n");

                    // show the city name with area pincode
                    String cityNameWithPincode = "";
                    if(!Utilities.isNullOrEmpty(mCur.getString(23)))
                        cityNameWithPincode = mCur.getString(23);
                    if(!Utilities.isNullOrEmpty(mCur.getString(24)))
                        cityNameWithPincode = cityNameWithPincode + " - " +mCur.getString(24);

                    if(!Utilities.isNullOrEmpty(cityNameWithPincode)) {
                        mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
//                        mPrinter.addTextFont(Printer.FONT_C);
                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addText(cityNameWithPincode + "\n");
                    }

                    // show statename and gst state code
                    String stateNameWithGSTStatecode = "";
                    if(!Utilities.isNullOrEmpty(mCur.getString(25)))
                        stateNameWithGSTStatecode = mCur.getString(25).trim();
                    if(!Utilities.isNullOrEmpty(mCur.getString(26)))
                        stateNameWithGSTStatecode = stateNameWithGSTStatecode + " - " +mCur.getString(26).trim();

                    if(!Utilities.isNullOrEmpty(stateNameWithGSTStatecode)) {
                        mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
//                        mPrinter.addTextFont(Printer.FONT_C);
                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addText(stateNameWithGSTStatecode.trim() + "\n");
                    }

                    if(!mCur.getString(15).equals("")) {
                        mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
//                        mPrinter.addTextFont(Printer.FONT_B);
                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addText("Contact : " + mCur.getString(15) +"\n");
                    }

                    if(!mCur.getString(14).equals("")) {
                        mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addText("GSTIN : " + mCur.getString(14) +"\n");
                    }


//                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
//                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
//                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
//                    mPrinter.addTextFont(Printer.FONT_B);
//                    mPrinter.addText("Ph : " + mCur.getString(15)+"\n");


                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);

                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                    String line_space1 = "--------------------------------";
                    mPrinter.addText(line_space1);

                    mPrinter.addTextFont(Printer.FONT_B);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addText(" Qty      Unit Rate       Rate      Value" + "\n");

                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    String line_space12 = "--------------------------------";
                    mPrinter.addText(line_space12);


                    Cursor mCur1 = mDbHelper.GetSalesItemPrint(gettransactiono,getfinancialyearcode,mCur.getString(17));

                    double gettotal=0;
                    double gettotaltaxablevalue=0;
                    double gettotaltaxvalue=0;
                    double cnt=0.0;
                    for (int j = 0; j < mCur1.getCount(); j++)
                    {
                        String Product = (mCur1.getString(0));


                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addText( Product+  "\n");

                        mPrinter.addTextFont(Printer.FONT_C);
                        mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                        mPrinter.addText( "HSN:"+ mCur1.getString(5)+ " GST:"+ mCur1.getString(6)+ "%" + "\n");


                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        String SalesDetails = "";
                        StringBuffer SalesBuffer = new StringBuffer(100);
                        SalesBuffer.append(SalesDetails);


                        SalesBuffer.append(Util.nameLeftValueRightJustifysalesitem1(mCur1.getString(1),mCur1.getString(4),
                                String.format("%.2f",mCur1.getFloat(3)),String.format("%.2f",mCur1.getFloat(9)),
                                String.format("%.2f",mCur1.getFloat(7)),
                                32));
                        SalesDetails = SalesBuffer.toString();
                        mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addText(SalesDetails+"\n");
//
//
//                        cnt=cnt+mCur.getFloat(4);
                        gettotal = gettotal + Double.parseDouble(String.valueOf(mCur1.getDouble(2)));
                        gettotaltaxablevalue=gettotaltaxablevalue+ Double.parseDouble(String.valueOf(mCur1.getDouble(7)));
                        gettotaltaxvalue=gettotaltaxvalue+ Double.parseDouble(String.valueOf(mCur1.getDouble(8)));
                        mCur1.moveToNext();
                    }

                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                    String line_space3 = "--------------------------------\n";
                    mPrinter.addText(line_space3);

                    String subtotal = "";
                    String gettotaltaxableamt = dft.format(gettotaltaxablevalue);
                    int valuetotaltaxable = (int)gettotaltaxablevalue;
                    mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addText(mCur1.getCount() + " Taxable value " + " : "+ Util.rightJustify( dft.format(gettotaltaxablevalue),11) + "\n");

                    String gettotaltaxamt = dft.format(gettotaltaxvalue);
                    int valuetotaltax = (int)gettotaltaxvalue;
                    mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addText(" Tax value " + " : "+ Util.rightJustify( dft.format(gettotaltaxvalue),11) + "\n");

                    double discunt= mDbHelper.GetSalesDiscount(gettransactiono,getfinancialyearcode,mCur.getString(17));
                    String discount=dft.format(discunt);

                    double roundoff= mDbHelper.GetSalesroundoffPrint(gettransactiono,getfinancialyearcode,mCur.getString(17));
                    String roundoffs=dft.format(roundoff);

                    double getgrandtotal= mDbHelper.GetSalesTotalforbill(gettransactiono,getfinancialyearcode,mCur.getString(17));
                    String grandtotal=dft.format(getgrandtotal);
                    if(!discount.equals("") && !discount.equals("0") && !discount.equals("0.00")) {

                        mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_A);
                        int valuediscunt = (int)discunt;
                        mPrinter.addText(" Scheme " + " : " + Util.rightJustify(dft.format(discunt), 11) + "\n");
                    }

                    mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_A);
                    int valueroundoff = (int)roundoff;
                    mPrinter.addText(" Round Off " + " : " + Util.rightJustify(dft.format(roundoff), 11) + "\n");
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);

                    String line_space141 = "--------------------------------\n";
                    mPrinter.addText(line_space141);

                    mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addText(" Total " + " : " + Util.rightJustify(dft.format(getgrandtotal), 11) + "\n");
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);

                    String line_space14 = "--------------------------------\n";
                    mPrinter.addText(line_space14);

                   /* double net=gettotal-discunt;
                    String nettotal = dft.format(net);
                    mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addText(" Nett Amount " + " : "+ Util.rightJustify( nettotal,11) + "\n");



                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                    String line_space4 = "--------------------------------\n";
                    mPrinter.addText(line_space4);*/




                    mPrinter.addTextFont(Printer.FONT_C);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addText("  GST %    Taxable        CGST       SGST " + "\n");

                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    String line_space112 = "-------------------------------\n";
                    mPrinter.addText(line_space112);


                    Cursor mCur2 = mDbHelper.GetSalestaxPrint(gettransactiono,getfinancialyearcode,mCur.getString(17));


                    for (int j = 0; j < mCur2.getCount(); j++)
                    {

                        mPrinter.addTextFont(Printer.FONT_B);
                       /* String SalesDetails = "";
                        StringBuffer SalesBuffer = new StringBuffer(100);
                        SalesBuffer.append(SalesDetails);


                        SalesBuffer.append(Util.nameLeftValueRightJustifysalestaxvalue(mCur2.getString(0)+" %",String.format("%.2f",mCur2.getFloat(1)),
                                String.format("%.2f",mCur2.getFloat(2)),String.format("%.2f",mCur2.getFloat(3)),
                                32));
                        SalesDetails = SalesBuffer.toString();
                        mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                        mPrinter.addText(SalesDetails+"\n");*/
                        String printdetails = "";
                        StringBuffer CashtaxBuffer2 = new StringBuffer(100);
                        CashtaxBuffer2.append(printdetails);


                        CashtaxBuffer2.append(Util.nameLeftValueRightJustifycashtaxvalues(mCur2.getString(0)+" %",
                                dft.format(mCur2.getDouble(1)),dft.format(mCur2.getDouble(2)),
                                dft.format(mCur2.getDouble(3)),32));
                        printdetails = CashtaxBuffer2.toString();
                        mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                        mPrinter.addText(printdetails+"\n");


                        mCur2.moveToNext();
                    }




                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                    String line_space13 = "------------------------------------------\n";
                    mPrinter.addText(line_space13);

                    Cursor mCur3 = mDbHelper.GetSalestaxtotalPrint(gettransactiono,getfinancialyearcode,mCur.getString(17));


                    for (int j = 0; j < mCur3.getCount(); j++)

                    {

                        mPrinter.addTextFont(Printer.FONT_B);
                        String SalesDetails = "";
                        StringBuffer SalesBuffer = new StringBuffer(100);
                        SalesBuffer.append(SalesDetails);

                        SalesBuffer.append(Util.nameLeftValueRightJustifycashtaxvalues(mCur3.getString(0),
                                dft.format(mCur3.getDouble(1)),dft.format(mCur3.getDouble(2)),
                                dft.format(mCur3.getDouble(3)),32));

                        SalesDetails = SalesBuffer.toString();
                        mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                        mPrinter.addText(SalesDetails+"\n");


                    }

                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    String line_space122 = "--------------------------------\n";
                    mPrinter.addText(line_space122);

                    String emptylines2 = "\n";
                    // mPrinter.addText(emptylines2);
                    mCur.moveToNext();
                }
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_B);
                mPrinter.addText("Subject to "+jurisdiction+" Jurisdiction" +"\n");


                Cursor mCur4 = mDbHelper.GetSalesschedulePrint(gettransactiono,getfinancialyearcode,"0");
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_C);
                // mPrinter.addText( mCur4.getString(0) +"/" +mCur4.getString(1) +"/"+ mCur4.getString(2)+"/"+ GenCreatedDate() +"\n");
                mPrinter.addText( mCur4.getString(1) +"/"+ GenCreatedDate() +"\n");


                mPrinter.addTextAlign(Printer.ALIGN_LEFT);

                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                String line_space11 = "--------------------------------";
                mPrinter.addText(line_space11);

                mPrinter.addTextFont(Printer.FONT_C);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addText("Concern         Bill No.            Amount" + "\n");
                double net1=0;

                for(int j=0;j<mCurDetails.getCount();j++) {
                    mPrinter.addTextFont(Printer.FONT_A);
                    String SalesDetails = "";
                    StringBuffer SalesBuffer = new StringBuffer(100);
                    SalesBuffer.append(SalesDetails);


                    SalesBuffer.append(Util.nameLeftValueRightJustifybottomsalesv2(mCurDetails.getString(3), mCurDetails.getString(0),
                            String.format("%.2f", mCurDetails.getFloat(2)),
                            32));
                    SalesDetails = SalesBuffer.toString();
                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addText(SalesDetails + "\n");
                    net1 = net1+mCurDetails.getFloat(2);
                    mCurDetails.moveToNext();
                }

                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                String line_space19 = "--------------------------------\n";
                mPrinter.addText(line_space19);

                String nettotal1 = dft.format(net1);
                int valuenet1 = (int)net1;
                mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addText(" Net Amount " + " : "+ Util.rightJustify( dft.format(Math.round(net1)),11) + "\n");



                  /*  String SalesDetails = "";
                    StringBuffer SalesBuffer = new StringBuffer(100);
                    SalesBuffer.append(SalesDetails);


                    SalesBuffer.append(Util.nameLeftValueRightJustifybottomsales(mCur.getString(18),mCur.getString(12),
                            String.format("%.2f",mCur.getFloat(19)),
                            32));
                    SalesDetails = SalesBuffer.toString();
                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                    mPrinter.addText(SalesDetails+"\n");*/

                mPrinter.addTextFont(Printer.FONT_C);
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                String line_spacee = "------------------------------------------\n";
                mPrinter.addText(line_spacee);

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                String poweredby ="Powered by www.shivasoftwares.com\n";
                mPrinter.addText(poweredby);

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                String line_spacee1 = "------------------------------------------\n";
                mPrinter.addText(line_spacee1);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextFont(Printer.FONT_B);
                String thankmsg = "         "+preferenceMangr.pref_getString("getwishmsg")+"      \n\n\n";
                // String thankmsg = "    நன்றி      \n\n\n\n";
                mPrinter.addText(thankmsg);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);

                String emptylines1 = "\n";
                mPrinter.addText(emptylines1);

                if(printDC)
                    GetDCPrint(gettransactiono, getfinancialyearcode, objActivity, printDC);

                try {

                    if(!connectPrinter()) {
                        mPrinter.clearCommandBuffer();
                        return false;
                    }

                    //PrinterStatusInfo status = mPrinter.getStatus();
                    status = mPrinter.getStatus();

                    Log.d("STATUS", Integer.toString(status.getConnection()));
                    Log.d("ONLINE", Integer.toString(status.getOnline()));
                    //Log.d("BATTERY", Integer.toString(status.getBatteryLevel()));

                    if ((status.getConnection() == 1) && (status.getOnline() == 1)) {
                        try {

                            mPrinter.sendData(Printer.PARAM_DEFAULT);
                            billPrinted = true;
                        } catch (Exception e) {
                            Log.d("Send data PRINT", e.getMessage());
                            mPrinter.clearCommandBuffer();
                            try {
                                mPrinter.disconnect();
                            } catch (Exception ex) {
                                // Do nothing
                            }
                        }
                    }


                }catch (Exception ex) {
                    Log.d("End PRINT", ex.getMessage());
                }

            }

        } catch (Epos2Exception e) {
            mPrinter.clearCommandBuffer();
            Log.d("PrintData : GetSalesBillPrint", e.getMessage());
        }


        return billPrinted;
    }

    //Delivery challen print
    @SuppressWarnings("rawtypes")
    public boolean GetDCPrint(String gettransactiono,String getfinancialyearcode,Activity objActivity, boolean printDC) {

        if (mPrinter == null) {
            initializePrinter(objActivity.getApplicationContext());
        }

        if (mPrinter == null)
            return false;

        PrinterStatusInfo status = mPrinter.getStatus();

        boolean billPrinted = false;
        try{
            /*try {
                mPrinter = new Printer(Printer.TM_P20, Printer.MODE_GRAY16, mContext);
            } catch (Epos2Exception e) {
                e.printStackTrace();
                Log.d("PRINT", "printer failed to initialize class");
                return billPrinted;
            }

            try {

                // preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                preferenceMangr = new PreferenceMangr(mContext);
                mPrinter.connect("BT:"+preferenceMangr.pref_getString("SelectedPrinterAddress"),PRINTER_TIMEOUT);
                mPrinter.beginTransaction();
            }
            catch (Exception e) {

                Log.d("PRINT", "printer failed to connect Exception "+e);
                return billPrinted;
            }*/
            DataBaseAdapter mDbHelper = new DataBaseAdapter(mContext);
            mDbHelper.open();
            Cursor mCur = mDbHelper.GetDCSalesPrint(gettransactiono,getfinancialyearcode);
            Cursor mCurDetails = mDbHelper.GetSalesPaymentVoucherDetailsPrint(gettransactiono,getfinancialyearcode);

            if (mCur.getCount() > 0) {

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_A);

                // for (int i=0;i<mCur.getCount();i++ )
                //{
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addText(mCur.getString(10)+" Delivery Chalan " +"\n");


                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                String line_space6 = "--------------------------------\n";
                mPrinter.addText(line_space6);

                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_B);
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addText("Bk.No. : " + mCur.getString(20)+"\n");

                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_B);
                mPrinter.addText("Invoice No. : " + mCur.getString(12)+"\n");

                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_B);
                mPrinter.addText("Invoice Date : " + mCur.getString(16) +"  "+mCur.getString(22)+" \n");



                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addText("Mr/Ms : " + mCur.getString(11) +"\n");

                String cityNameWithPincode = "";
                if(!Utilities.isNullOrEmpty(mCur.getString(23)))
                    cityNameWithPincode = mCur.getString(23).trim();

                if(!Utilities.isNullOrEmpty(cityNameWithPincode)) {
                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_C);
                    mPrinter.addText(cityNameWithPincode.trim() + "\n");
                }

                if(!mCur.getString(14).equals("")) {
                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addText("GSTIN : " + mCur.getString(14) + "\n");
                }

                mPrinter.addTextAlign(Printer.ALIGN_LEFT);

                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                String line_space1 = "--------------------------------";
                mPrinter.addText(line_space1);

                mPrinter.addTextFont(Printer.FONT_C);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addText("Particulars              Qty        Unit" + "\n");

                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                String line_space12 = "--------------------------------";
                mPrinter.addText(line_space12);


                Cursor mCur1 = mDbHelper.GetDCSalesItemPrint(gettransactiono,getfinancialyearcode,mCur.getString(17));

                for (int j = 0; j < mCur1.getCount(); j++)
                {
                    String Product = (mCur1.getString(0));

                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addText( Product+  "\n");


                    mPrinter.addTextFont(Printer.FONT_A);
                    String SalesDetails = "";
                    StringBuffer SalesBuffer = new StringBuffer(100);
                    SalesBuffer.append(SalesDetails);


                    SalesBuffer.append(Util.nameLeftValueRightJustify("",
                            mCur1.getString(1),
                            mCur1.getString(4),
                            32));
                    SalesDetails = SalesBuffer.toString();
                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addText(SalesDetails+"\n");

                    mCur1.moveToNext();
                }

                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                String line_space3 = "--------------------------------\n";
                mPrinter.addText(line_space3);


                Cursor mCur4 = mDbHelper.GetSalesschedulePrint(gettransactiono,getfinancialyearcode,"0");
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_C);
                // mPrinter.addText( mCur4.getString(0) +"/" +mCur4.getString(1) +"/"+ mCur4.getString(2)+"/"+ GenCreatedDate() +"\n");
                mPrinter.addText(  " "+mCur1.getCount()+"  "+mCur4.getString(1) +"/"+ GenCreatedDate() +"\n");


                mPrinter.addTextAlign(Printer.ALIGN_LEFT);

                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                String line_space11 = "--------------------------------";
                mPrinter.addText(line_space11);


                mPrinter.addTextFont(Printer.FONT_C);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addText("Concern         Bill No.            Amount" + "\n");



                mPrinter.addTextFont(Printer.FONT_A);
                double net1=0;
                for(int j=0;j<mCurDetails.getCount();j++) {
                    String SalesDetails = "";
                    StringBuffer SalesBuffer = new StringBuffer(100);
                    SalesBuffer.append(SalesDetails);


                    SalesBuffer.append(Util.nameLeftValueRightJustifybottomsalesv2(mCurDetails.getString(3), mCurDetails.getString(0),
                            String.format("%.2f", mCurDetails.getFloat(2)),
                            32));
                    SalesDetails = SalesBuffer.toString();
                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addText(SalesDetails + "\n");
                    net1 = net1+mCurDetails.getFloat(2);
                    mCurDetails.moveToNext();
                }
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                String line_space19 = "--------------------------------\n";
                mPrinter.addText(line_space19);

                String nettotal1 = dft.format(net1);
                mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addText(" Nett Amount " + " : "+ Util.rightJustify( dft.format(Math.round(net1)),11) + "\n");


                mPrinter.addTextFont(Printer.FONT_C);
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                String line_space13 = "-----------------------------------------\n";
                mPrinter.addText(line_space13);

                mPrinter.addText("\n\n\n");

                mCur.moveToNext();
                //}

                if (printDC)
                    return true;
                try {

                    if(!connectPrinter()) {
                        mPrinter.clearCommandBuffer();
                        return false;
                    }

                    //PrinterStatusInfo status = mPrinter.getStatus();
                    status = mPrinter.getStatus();

                    Log.d("STATUS", Integer.toString(status.getConnection()));
                    Log.d("ONLINE", Integer.toString(status.getOnline()));

                    if ((status.getConnection() == 1) && (status.getOnline() == 1)) {
                        try {
                            mPrinter.sendData(Printer.PARAM_DEFAULT);
                            billPrinted = true;
                        } catch (Exception e) {
                            Log.d("bbb", e.getLocalizedMessage());
                            Log.d("PRINT", "failed to send data");
                            mPrinter.clearCommandBuffer();
                            try {
                                mPrinter.disconnect();
                            } catch (Exception ex) {
                                // Do nothing
                            }
                        }
                    }

                }catch (Exception ex) {
                    ex.printStackTrace();
                }

            }else{
                Log.d("nodata","nodata");
                //Toast.makeText(mContext,"No data Available",Toast.LENGTH_SHORT).show();
                objActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d("thread","thread");
                        Toast.makeText(mContext, "No data Available", Toast.LENGTH_SHORT).show();
                    }
                });
            }


        } catch (Epos2Exception e) {
            e.printStackTrace();
            Log.d("PrintData : printDC",e.getLocalizedMessage());
        }

        return billPrinted;
    }

    //Sales Payment Voucher
    @SuppressLint("LongLogTag")
    public boolean GetSalesReceipt(String gettransactiono, String getfinancialyearcode, Activity objActivity, String companyCode) {

        if (mPrinter == null) {
            initializePrinter(objActivity.getApplicationContext());
        }

        if (mPrinter == null)
            return false;

        //Printer printer = null;

        boolean billPrinted = false;
        String printheader ="";
        try{
            DataBaseAdapter mDbHelper = new DataBaseAdapter(mContext);
            mDbHelper.open();
            Cursor mCur = mDbHelper.GetSalesPaymentVoucherPrint(gettransactiono,getfinancialyearcode, companyCode);
            Cursor mCurDetails = mDbHelper.GetCompanyWiseSalesPaymentVoucherDetailsPrint(gettransactiono,getfinancialyearcode, companyCode);

            if (mCur.getCount() > 0) {

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_A);

                /*String startmsg = "       உ        \n";
                mPrinter.addText(startmsg);
                String startmsgline = "       --        \n";
                mPrinter.addText(startmsgline);*/

                printheader=mDbHelper.getprintheader();

                if(!printheader.equals("")&&!printheader.equals(null)&&!printheader.equals("null")){

                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    String startmsg = printheader+"\n";
                    mPrinter.addText(startmsg);
                    String startmsgline = "       --        \n";
                    mPrinter.addText(startmsgline);
                }

                // for (int i=0;i<mCur.getCount();i++ )
                //{
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addText(mCur.getString(10)+" Payment Voucher " +"\n");


                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                String line_space6 = "--------------------------------\n";
                mPrinter.addText(line_space6);

                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_B);
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addText("BK.No. : " + mCur.getString(20)+"\n");


                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_B);
                mPrinter.addText("Invoice Date : " + mCur.getString(16) +"  "+mCur.getString(22)+"\n");



                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_B);
                mPrinter.addText("Mr/Ms  : " + mCur.getString(11) +"\n");

                if(! mCur.getString(14).equals("")) {
                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_B);
                    mPrinter.addText("GSTIN : " + mCur.getString(14) + "\n");
                }

                if(!Utilities.isNullOrEmpty(mCur.getString(23))) {

                    mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                    String line_spaceparticulars = "------------------------------------------\n";
                    mPrinter.addText(line_spaceparticulars);

                    mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_A);
                    String paymentType = mCur.getString(23);
                    if (paymentType.equals("yes"))
                        paymentType = "CASH";
                    else if (paymentType.equals("upi"))
                        paymentType = "UPI";

                    mPrinter.addText("Payment Type : " + paymentType + "\n");
                }

                mPrinter.addTextAlign(Printer.ALIGN_LEFT);

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_B);
                String line_spaceparticulars = "------------------------------------------\n";
                mPrinter.addText(line_spaceparticulars);

                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addText("Invoice No.              Amount \n");

                String line_spaceparticularsend = "--------------------------------\n";
                mPrinter.addText(line_spaceparticularsend);


                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);

                for (int i=0;i<mCurDetails.getCount();i++ ){
                    String SalesDetails = "";
                    StringBuffer SalesBuffer = new StringBuffer(100);
                    SalesBuffer.append(SalesDetails);

                    SalesBuffer.append(Util.nameLeftValueRightJustify_cashrpt(mCurDetails.getString(0),
                            mCurDetails.getString(3),String.format("%.2f", mCurDetails.getFloat(2)),
                            32));
                    SalesDetails = SalesBuffer.toString();
                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addText(SalesDetails+"\n");

                    mCurDetails.moveToNext();

                }
                String line_spaceparticularsend1 = "--------------------------------\n";
                mPrinter.addText(line_spaceparticularsend1);

                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                String SalesDetails = "";
                StringBuffer SalesBuffer = new StringBuffer(100);
                SalesBuffer.append(SalesDetails);

                SalesBuffer.append(Util.nameLeftValueRightJustify_cashrpt("Net Amount : ",
                        "",String.format("%.2f", mCur.getFloat(19)),
                        32));
                SalesDetails = SalesBuffer.toString();
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addText(SalesDetails+"\n");
                mPrinter.addText(line_spaceparticularsend1);

                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);

                //numbertotextconvert
                String amounttxt = PrintData.convert(mCur.getInt(19))+ " Only \n";

                String amountString=containsWhiteSpace(amounttxt);

                mPrinter.addText(amountString);

                /*objActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d("amountString : ",amountString);
                        Toast.makeText(mContext, amountString, Toast.LENGTH_SHORT).show();
                    }
                });*/

                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);

                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                String line_space3 = "--------------------------------\n";
                mPrinter.addText(line_space3);

                Cursor mCur4 = mDbHelper.GetSalesschedulePrint(gettransactiono,getfinancialyearcode,"0");
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_C);
                mPrinter.addText( mCur4.getString(1) +"/"+ GenCreatedDate() +"\n");


                mPrinter.addTextFont(Printer.FONT_C);
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                String line_space13 = "-----------------------------------------\n";
                mPrinter.addText(line_space13);

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                String poweredby ="Powered by www.shivasoftwares.com\n";
                mPrinter.addText(poweredby);

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                String line_space4 = "------------------------------------------\n";
                mPrinter.addText(line_space4);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextFont(Printer.FONT_B);
                String thankmsg = "         "+preferenceMangr.pref_getString("getwishmsg")+"      \n\n\n";
                //String thankmsg = "         நன்றி      \n\n\n";
                mPrinter.addText(thankmsg);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                // mCur.moveToNext();
                //}

                try {

                    if(!connectPrinter()) {
                        mPrinter.clearCommandBuffer();
                        return false;
                    }

                    PrinterStatusInfo status = mPrinter.getStatus();
                    Log.d("STATUS", Integer.toString(status.getConnection()));
                    Log.d("ONLINE", Integer.toString(status.getOnline()));

                    if ((status.getConnection() == 1) && (status.getOnline() == 1)) {
                        try {
                            mPrinter.sendData(Printer.PARAM_DEFAULT);
                            billPrinted = true;
                        } catch (Epos2Exception e) {
                            Log.d("bbb", e.getLocalizedMessage());
                            Log.d("PRINT", "failed to send data");
                            mPrinter.clearCommandBuffer();
                            try {
                                mPrinter.disconnect();
                            } catch (Exception ex) {
                                // Do nothing
                            }
                        }
                    }
                }catch (Exception ex) {
                    ex.printStackTrace();
                    Log.d("PRINT", "failed to send data");
                }

            }else{
                Log.d("nodata","nodata");
                //Toast.makeText(mContext,"No data Available",Toast.LENGTH_SHORT).show();
                objActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d("thread","thread");
                        Toast.makeText(mContext, "No data Available", Toast.LENGTH_SHORT).show();
                    }
                });
            }



        } catch (Epos2Exception e) {
            e.printStackTrace();
            Log.d("PrintData : GetSalesReceipt",e.getLocalizedMessage());
        }

        return billPrinted;
    }

    //SAles Return Print
    @SuppressLint("LongLogTag")
    public boolean GetSalesReturnBillPrint(String gettransactiono, String getfinancialyearcode, Activity objActivity, boolean printDC) {

        if (mPrinter == null) {
            initializePrinter(objActivity.getApplicationContext());
        }

        if (mPrinter == null)
            return false;

        //Printer printer = null;
        boolean billPrinted = false;
        String jurisdiction="";
        String printheader ="";
        try {
            DataBaseAdapter mDbHelper = new DataBaseAdapter(mContext);
            mDbHelper.open();
            Cursor mCur = mDbHelper.GetSalesReturnPrint(gettransactiono,getfinancialyearcode);
            Cursor mCurDetails = mDbHelper.GetSalesReturnPaymentVoucherDetailsPrint(gettransactiono,getfinancialyearcode);
            jurisdiction = mDbHelper.getjurisdiction();

            if (mCur.getCount() > 0) {

                for (int i=0;i<mCur.getCount();i++ )
                {
                    mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_A);

                    /*String startmsg = "       உ        \n";
                    mPrinter.addText(startmsg);
                    String startmsgline = "       --        \n";
                    mPrinter.addText(startmsgline);*/

                    printheader=mDbHelper.getprintheader();

                    if(!printheader.equals("")&&!printheader.equals(null)&&!printheader.equals("null")){

                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        String startmsg = printheader+"\n";
                        mPrinter.addText(startmsg);
                        String startmsgline = "       --        \n";
                        mPrinter.addText(startmsgline);
                    }

                    if(SalesReturnViewActivity.isduplicate){
                        String startmsgline1 = "       DUPLICATE        \n";
                        mPrinter.addText(startmsgline1);
                    }

                    mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addText(mCur.getString(0) +"\n");


                    mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_B);
                    mPrinter.addText(mCur.getString(1)+"\n");

                    mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_B);
                    //mPrinter.addText(mCur.getString(3) +" - " + mCur.getString(4)+"\n");
                    String companyCityStateDetails = mCur.getString(3) +" - " + mCur.getString(4);

                    String company_stateNameWithGSTStatecode = "";
                    if(!Utilities.isNullOrEmpty(mCur.getString(27)))
                        company_stateNameWithGSTStatecode = mCur.getString(27).trim();
                    if(!Utilities.isNullOrEmpty(mCur.getString(28)))
                        company_stateNameWithGSTStatecode = company_stateNameWithGSTStatecode + " - " +mCur.getString(28).trim();

                    if(!Utilities.isNullOrEmpty(company_stateNameWithGSTStatecode))
                        companyCityStateDetails = companyCityStateDetails.trim() + " , " +company_stateNameWithGSTStatecode.trim();

                    mPrinter.addText(companyCityStateDetails.trim() +"\n");

                   /* mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_B);
                    mPrinter.addText("Ph " + mCur.getString(5) +"  " + mCur.getString(6) + "    "+mCur.getString(10)+"\n");*/
                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_C);
                    mPrinter.addText("Ph  " + mCur.getString(5) +"  " + mCur.getString(6) );


                    mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addText("  "+mCur.getString(10)+"\n");


                    if(!mCur.getString(7).equals("")) {
                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addText("GSTIN : " + mCur.getString(7) + "\n");
                    }

                    if(!Utilities.isNullOrEmpty(mCur.getString(8))) {
                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addText("FSSAI No : " + mCur.getString(8) + "\n");
                    }

                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_C);
                    mPrinter.addText("PAN : " + mCur.getString(9));


                    mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addText("         BK.No. "+mCur.getString(20) +"\n");

                    mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    String line_space6 = "--------------------------------";
                    mPrinter.addText(line_space6);


                    mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_B);
                    String billtype = " ";
                    if(mCur.getString(21).equals("1")){
                        billtype =  "SALES RETURN - CA";
                    }else{
                        billtype =  "SALES RETURN - CR";
                    }
                    mPrinter.addText(billtype +"\n");

                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    String line_space5 = "--------------------------------";
                    mPrinter.addText(line_space5);

                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_B);
                    mPrinter.addText("Invoice No. : " + mCur.getString(12)+"\n");

                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_C);
                    mPrinter.addText("Invoice Date : " + mCur.getString(16) +"  " +mCur.getString(22) +"\n");

                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addText("Mr/Ms : " + mCur.getString(11) +"\n");

                    // show the city name with area pincode
                    String cityNameWithPincode = "";
                    if(!Utilities.isNullOrEmpty(mCur.getString(23)))
                        cityNameWithPincode = mCur.getString(23).trim();
                    if(!Utilities.isNullOrEmpty(mCur.getString(24)))
                        cityNameWithPincode = cityNameWithPincode + " - " +mCur.getString(24).trim();

                    if(!Utilities.isNullOrEmpty(cityNameWithPincode)) {
                        mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_C);
                        mPrinter.addText(cityNameWithPincode.trim() + "\n");
                    }

                    // show statename and gst state code
                    String stateNameWithGSTStatecode = "";
                    if(!Utilities.isNullOrEmpty(mCur.getString(25)))
                        stateNameWithGSTStatecode = mCur.getString(25).trim();
                    if(!Utilities.isNullOrEmpty(mCur.getString(26)))
                        stateNameWithGSTStatecode = stateNameWithGSTStatecode + " - " +mCur.getString(26).trim();

                    if(!Utilities.isNullOrEmpty(stateNameWithGSTStatecode)) {
                        mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_C);
                        mPrinter.addText(stateNameWithGSTStatecode.trim() + "\n");
                    }

                    if(!mCur.getString(15).equals("")) {
                        mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_B);
                        mPrinter.addText("Contact : " + mCur.getString(15) +"\n");
                    }

                    if(!mCur.getString(14).equals("")) {
                        mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addText("GSTIN : " + mCur.getString(14) +"\n");
                    }

//                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
//                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
//                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
//                    mPrinter.addTextFont(Printer.FONT_B);
//                    mPrinter.addText("Ph : " + mCur.getString(15)+"\n");

                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);

                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                    String line_space1 = "--------------------------------";
                    mPrinter.addText(line_space1);

                    mPrinter.addTextFont(Printer.FONT_C);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addText("      Qty           Rate            Value" + "\n");

                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    String line_space12 = "--------------------------------";
                    mPrinter.addText(line_space12);

                    Cursor mCur1 = mDbHelper.GetSalesReturnItemPrint(gettransactiono,getfinancialyearcode,mCur.getString(17));

                    double gettotal=0;
                    double cnt=0.0;
                    for (int j = 0; j < mCur1.getCount(); j++)
                    {
                        String Product = (mCur1.getString(0));

                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addText( Product+  "\n");

                        mPrinter.addTextFont(Printer.FONT_C);
                        mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                        mPrinter.addText( "HSN:"+ mCur1.getString(5)+ " GST:"+ mCur1.getString(6)+ "%" + "\n");

                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        String SalesDetails = "";
                        StringBuffer SalesBuffer = new StringBuffer(100);
                        SalesBuffer.append(SalesDetails);

                        SalesBuffer.append(Util.nameLeftValueRightJustifysalesitem(mCur1.getString(1),mCur1.getString(4),
                                String.format("%.2f",mCur1.getFloat(3)),String.format("%.2f",mCur1.getFloat(2)),
                                32));
                        SalesDetails = SalesBuffer.toString();
                        mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addText(SalesDetails+"\n");
//
//
//                        cnt=cnt+mCur.getFloat(4);
                        gettotal = gettotal + Double.parseDouble(String.valueOf(mCur1.getDouble(2)));
                        mCur1.moveToNext();
                    }

                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                    String line_space3 = "--------------------------------\n";
                    mPrinter.addText(line_space3);

                    String subtotal = "";
                    String gettotalamt = dft.format(gettotal);
                    int valuetotal = (int)gettotal;
                    mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addText(mCur1.getCount() + "      Total " + " : "+ Util.rightJustify( dft.format(Math.round(gettotal)),11) + "\n");




                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                    String line_space14 = "--------------------------------\n";
                    mPrinter.addText(line_space14);

                    mPrinter.addTextFont(Printer.FONT_C);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addText("  GST %    Taxable        CGST       SGST " + "\n");

                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    String line_space112 = "-------------------------------\n";
                    mPrinter.addText(line_space112);


                    Cursor mCur2 = mDbHelper.GetSalesReturntaxPrint(gettransactiono,getfinancialyearcode,mCur.getString(17));


                    for (int j = 0; j < mCur2.getCount(); j++)
                    {

                        mPrinter.addTextFont(Printer.FONT_B);
                        String printdetails = "";
                        StringBuffer CashtaxBuffer2 = new StringBuffer(100);
                        CashtaxBuffer2.append(printdetails);


                        CashtaxBuffer2.append(Util.nameLeftValueRightJustifycashtaxvalues(mCur2.getString(0)+" %",
                                dft.format(mCur2.getDouble(1)),dft.format(mCur2.getDouble(2)),
                                dft.format(mCur2.getDouble(3)),32));
                        printdetails = CashtaxBuffer2.toString();
                        mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                        mPrinter.addText(printdetails+"\n");


                        mCur2.moveToNext();
                    }




                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                    String line_space13 = "------------------------------------------\n";
                    mPrinter.addText(line_space13);

                    Cursor mCur3 = mDbHelper.GetSalesReturntaxtotalPrint(gettransactiono,getfinancialyearcode,mCur.getString(17));


                    for (int j = 0; j < mCur3.getCount(); j++)

                    {

                        mPrinter.addTextFont(Printer.FONT_B);
                        String SalesDetails = "";
                        StringBuffer SalesBuffer = new StringBuffer(100);
                        SalesBuffer.append(SalesDetails);

                        SalesBuffer.append(Util.nameLeftValueRightJustifycashtaxvalues(mCur3.getString(0),
                                dft.format(mCur3.getDouble(1)),dft.format(mCur3.getDouble(2)),
                                dft.format(mCur3.getDouble(3)),32));

                        SalesDetails = SalesBuffer.toString();
                        mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                        mPrinter.addText(SalesDetails+"\n");


                    }

                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    String line_space122 = "--------------------------------\n";
                    mPrinter.addText(line_space122);

                    String emptylines2 = "\n";
                    // mPrinter.addText(emptylines2);
                    mCur.moveToNext();
                }
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_B);
                mPrinter.addText("Subject to "+jurisdiction+" Jurisdiction" +"\n");


                Cursor mCur4 = mDbHelper.GetSalesReturnschedulePrint(gettransactiono,getfinancialyearcode,"0");
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_C);
                // mPrinter.addText( mCur4.getString(0) +"/" +mCur4.getString(1) +"/"+ mCur4.getString(2)+"/"+ GenCreatedDate() +"\n");
                mPrinter.addText( mCur4.getString(1) +"/"+ GenCreatedDate() +"\n");


                mPrinter.addTextAlign(Printer.ALIGN_LEFT);

                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                String line_space11 = "--------------------------------";
                mPrinter.addText(line_space11);

                mPrinter.addTextFont(Printer.FONT_C);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addText("Concern         Bill No.            Amount" + "\n");
                double net1=0;

                for(int j=0;j<mCurDetails.getCount();j++) {
                    mPrinter.addTextFont(Printer.FONT_A);
                    String SalesDetails = "";
                    StringBuffer SalesBuffer = new StringBuffer(100);
                    SalesBuffer.append(SalesDetails);


                    SalesBuffer.append(Util.nameLeftValueRightJustifybottomsalesv2(mCurDetails.getString(3), mCurDetails.getString(0),
                            String.format("%.2f", mCurDetails.getFloat(2)),
                            32));
                    SalesDetails = SalesBuffer.toString();
                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addText(SalesDetails + "\n");
                    net1 = net1+mCurDetails.getFloat(2);
                    mCurDetails.moveToNext();
                }

                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                String line_space19 = "--------------------------------\n";
                mPrinter.addText(line_space19);

                String nettotal1 = dft.format(net1);
                int valuenet1 = (int)net1;
                mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addText(" Nett Amount " + " : "+ Util.rightJustify( dft.format(Math.round(net1)),11) + "\n");


                mPrinter.addTextFont(Printer.FONT_C);
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                String line_spacee = "------------------------------------------\n";
                mPrinter.addText(line_spacee);

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                String poweredby ="Powered by www.shivasoftwares.com\n";
                mPrinter.addText(poweredby);

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                String line_spacee1 = "------------------------------------------\n";
                mPrinter.addText(line_spacee1);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextFont(Printer.FONT_B);
                String thankmsg = "         "+preferenceMangr.pref_getString("getwishmsg")+"      \n\n\n";
                // String thankmsg = "    நன்றி      \n\n\n\n";
                mPrinter.addText(thankmsg);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                String emptylines1 = "";
                // mPrinter.addText(emptylines1);

                if (printDC)
                    GetDCSalesReturnPrint(gettransactiono, getfinancialyearcode, objActivity);

                try {

                    if(!connectPrinter()) {
                        mPrinter.clearCommandBuffer();
                        return false;
                    }

                    PrinterStatusInfo status = mPrinter.getStatus();

                    Log.d("STATUS", Integer.toString(status.getConnection()));
                    Log.d("ONLINE", Integer.toString(status.getOnline()));

                    if ((status.getConnection() == 1) && (status.getOnline() == 1)) {
                        try {
                            mPrinter.sendData(Printer.PARAM_DEFAULT);
                            billPrinted = true;
                        } catch (Epos2Exception e) {
                            Log.d("bbb", e.getLocalizedMessage());
                            Log.d("PRINT", "failed to send data");
                            mPrinter.clearCommandBuffer();
                            try {
                                mPrinter.disconnect();
                            } catch (Exception ex) {
                                // Do nothing
                            }
                        }
                    }

                }catch (Exception ex) {
                    ex.printStackTrace();
                }

            }else{

                Log.d("nodata","nodata");
                //Toast.makeText(mContext,"No data Available",Toast.LENGTH_SHORT).show();
                objActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d("thread","thread");
                        Toast.makeText(mContext, "No data Available", Toast.LENGTH_SHORT).show();
                    }
                });
            }


        } catch (Epos2Exception e) {
            e.printStackTrace();
            Log.d("PrintData : GetSalesReturnBillPrint", e.getMessage());
        }

        return billPrinted;
    }

    //Delivery challen print for sales return
    @SuppressLint("LongLogTag")
    public boolean GetDCSalesReturnPrint(String gettransactiono, String getfinancialyearcode, Activity objActivity) {

        if (mPrinter == null) {
            initializePrinter(objActivity.getApplicationContext());
        }

        if (mPrinter == null)
            return false;

        //Printer printer = null;
        boolean billPrinted = false;
        try{
            DataBaseAdapter mDbHelper = new DataBaseAdapter(mContext);
            mDbHelper.open();
            Cursor mCur = mDbHelper.GetDCSalesReturnPrint(gettransactiono,getfinancialyearcode);
            Cursor mCurDetails = mDbHelper.GetSalesReturnPaymentVoucherDetailsPrint(gettransactiono,getfinancialyearcode);

            if (mCur.getCount() > 0) {

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_A);

                // for (int i=0;i<mCur.getCount();i++ )
                //{
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addText("சேல்ஸ் ரிட்டர்ன் \n");

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addText(mCur.getString(10)+"\n");

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                String line_space6 = "--------------------------------\n";
                mPrinter.addText(line_space6);

                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_B);
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addText("Bk.No. : " + mCur.getString(20)+"\n");

                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_B);
                mPrinter.addText("Invoice No. : " + mCur.getString(12)+"\n");

                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_B);
                mPrinter.addText("Invoice Date : " + mCur.getString(16) +"  "+mCur.getString(22)+" \n");

                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addText("Mr/Ms : " + mCur.getString(11) +"\n");

                String cityNameWithPincode = "";
                if(!Utilities.isNullOrEmpty(mCur.getString(23)))
                    cityNameWithPincode = mCur.getString(23).trim();

                if(!Utilities.isNullOrEmpty(cityNameWithPincode)) {
                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_C);
                    mPrinter.addText(cityNameWithPincode.trim() + "\n");
                }

                if(!mCur.getString(14).equals("")) {
                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addText("GSTIN : " + mCur.getString(14) + "\n");
                }

                mPrinter.addTextAlign(Printer.ALIGN_LEFT);

                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                String line_space1 = "--------------------------------";
                mPrinter.addText(line_space1);

                mPrinter.addTextFont(Printer.FONT_C);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addText("Particulars              Qty        Unit" + "\n");

                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                String line_space12 = "--------------------------------";
                mPrinter.addText(line_space12);


                Cursor mCur1 = mDbHelper.GetDCSalesReturnItemPrint(gettransactiono,getfinancialyearcode,mCur.getString(17));

                for (int j = 0; j < mCur1.getCount(); j++)
                {
                    String Product = (mCur1.getString(0));

                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addText( Product+  "\n");


                    mPrinter.addTextFont(Printer.FONT_A);
                    String SalesDetails = "";
                    StringBuffer SalesBuffer = new StringBuffer(100);
                    SalesBuffer.append(SalesDetails);


                    SalesBuffer.append(Util.nameLeftValueRightJustify("",
                            mCur1.getString(1),
                            mCur1.getString(4),
                            32));
                    SalesDetails = SalesBuffer.toString();
                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addText(SalesDetails+"\n");

                    mCur1.moveToNext();
                }

                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                String line_space3 = "--------------------------------\n";
                mPrinter.addText(line_space3);


                Cursor mCur4 = mDbHelper.GetSalesReturnSchPrint(gettransactiono,getfinancialyearcode,"0");
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_C);
                // printer.addText( mCur4.getString(0) +"/" +mCur4.getString(1) +"/"+ mCur4.getString(2)+"/"+ GenCreatedDate() +"\n");
                mPrinter.addText(  " "+mCur1.getCount()+"  "+mCur4.getString(1) +"/"+ GenCreatedDate() +"\n");


                mPrinter.addTextAlign(Printer.ALIGN_LEFT);

                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                String line_space11 = "--------------------------------";
                mPrinter.addText(line_space11);


                mPrinter.addTextFont(Printer.FONT_C);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addText("Concern         Bill No.            Amount" + "\n");



                mPrinter.addTextFont(Printer.FONT_A);
                double net1=0;
                for(int j=0;j<mCurDetails.getCount();j++) {
                    String SalesDetails = "";
                    StringBuffer SalesBuffer = new StringBuffer(100);
                    SalesBuffer.append(SalesDetails);


                    SalesBuffer.append(Util.nameLeftValueRightJustifybottomsalesv2(mCurDetails.getString(3), mCurDetails.getString(0),
                            String.format("%.2f", mCurDetails.getFloat(2)),
                            32));
                    SalesDetails = SalesBuffer.toString();
                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addText(SalesDetails + "\n");
                    net1 = net1+mCurDetails.getFloat(2);
                    mCurDetails.moveToNext();
                }
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                String line_space19 = "--------------------------------\n";
                mPrinter.addText(line_space19);

                String nettotal1 = dft.format(net1);
                mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addText(" Nett Amount " + " : "+ Util.rightJustify( dft.format(Math.round(net1)),11) + "\n");


                mPrinter.addTextFont(Printer.FONT_C);
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                String line_space13 = "-----------------------------------------\n";
                mPrinter.addText(line_space13);

                mPrinter.addText("\n\n\n");

                mCur.moveToNext();
                //}

                /*try {

                    if(!connectPrinter()) {
                        mPrinter.clearCommandBuffer();
                        return false;
                    }

                    PrinterStatusInfo status = mPrinter.getStatus();

                    Log.d("STATUS", Integer.toString(status.getConnection()));
                    Log.d("ONLINE", Integer.toString(status.getOnline()));

                    if ((status.getConnection() == 1) && (status.getOnline() == 1)) {
                        try {
                            mPrinter.sendData(Printer.PARAM_DEFAULT);
                            billPrinted = true;

                        } catch (Epos2Exception e) {
                            Log.d("bbb", e.getLocalizedMessage());
                            Log.d("PRINT", "failed to send data");
                        }
                    }

                }catch (Exception ex) {
                    ex.printStackTrace();
                }*/
            }else{
                Log.d("nodata","nodata");
                //Toast.makeText(mContext,"No data Available",Toast.LENGTH_SHORT).show();
                objActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d("thread","thread");
                        Toast.makeText(mContext, "No data Available", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch (Epos2Exception e) {
            e.printStackTrace();
            Log.d("PrintData : GetDCSalesReturnPrint",e.getLocalizedMessage());
        }

        return billPrinted;
    }

    //Order print
    @SuppressLint("LongLogTag")
    public boolean GetSalesOrderBillPrint(String gettransactiono, String getfinancialyearcode, Activity objActivity) {

        if (mPrinter == null) {
            initializePrinter(objActivity.getApplicationContext());
        }

        if (mPrinter == null)
            return false;

        //Printer printer = null;
        boolean billPrinted = false;
        String jurisdiction="";
        String printheader ="";
        try {
            DataBaseAdapter mDbHelper = new DataBaseAdapter(mContext);
            mDbHelper.open();
            Cursor mCur = mDbHelper.GetSalesOrderPrint(gettransactiono,getfinancialyearcode);
            Cursor mCurDetails = mDbHelper.GetSalesOrderPaymentVoucherDetailsPrint(gettransactiono,getfinancialyearcode);
            jurisdiction = mDbHelper.getjurisdiction();

            if (mCur.getCount() > 0) {

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_A);

                    /*String startmsg = "       உ        \n";
                    printer.addText(startmsg);
                    String startmsgline = "       --        \n";
                    printer.addText(startmsgline);*/

                printheader=mDbHelper.getprintheader();

                if(!printheader.equals("")&&!printheader.equals(null)&&!printheader.equals("null")){

                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    String startmsg = printheader+"\n";
                    mPrinter.addText(startmsg);
                    String startmsgline = "       --        \n";
                    mPrinter.addText(startmsgline);
                }

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addText("Natarjan & Co Group.," +"\n");


                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.TRUE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addText("Order Slip \n");

                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_B);
                mPrinter.addText("Customer Details : \n");

                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_B);
                mPrinter.addText("Mr/Ms : " + mCur.getString(11) +"\n");


                    /*printer.addTextAlign(Printer.ALIGN_LEFT);
                    printer.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    printer.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                    printer.addTextFont(Printer.FONT_B);
                    printer.addText("Area : " + mCur.getString(25) +"\n");*/

                // show the city name with area pincode
                String cityNameWithPincode = "";
                if(!Utilities.isNullOrEmpty(mCur.getString(27)))
                    cityNameWithPincode = mCur.getString(27).trim();
                if(!Utilities.isNullOrEmpty(mCur.getString(28)))
                    cityNameWithPincode = cityNameWithPincode + " - " +mCur.getString(28).trim();

                if(!Utilities.isNullOrEmpty(cityNameWithPincode)) {
                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_C);
                    mPrinter.addText(cityNameWithPincode.trim() + "\n");
                }

                // show statename and gst state code
                String stateNameWithGSTStatecode = "";
                if(!Utilities.isNullOrEmpty(mCur.getString(29)))
                    stateNameWithGSTStatecode = mCur.getString(29).trim();
                if(!Utilities.isNullOrEmpty(mCur.getString(30)))
                    stateNameWithGSTStatecode = stateNameWithGSTStatecode + " - " +mCur.getString(30).trim();

                if(!Utilities.isNullOrEmpty(stateNameWithGSTStatecode)) {
                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_C);
                    mPrinter.addText(stateNameWithGSTStatecode.trim() + "\n");
                }

                if(!mCur.getString(15).equals("")) {
                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_B);
                    mPrinter.addText("Contact : " + mCur.getString(15) +"\n");
                }

                if(!mCur.getString(14).equals("")) {
                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_B);
                    mPrinter.addText("GSTIN : " + mCur.getString(14) +"\n");
                }

                    /*if(!mCur.getString(6).equals("")) {
                        printer.addTextAlign(Printer.ALIGN_LEFT);
                        printer.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        printer.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                        printer.addTextFont(Printer.FONT_B);
                        printer.addText("C.Ph : " + mCur.getString(15) +"\n");
                    }*/

                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                String line_space11 = "--------------------------------";
                mPrinter.addText(line_space11);

                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_B);
                mPrinter.addText("O.BK.No. "+mCur.getString(20) +"\n");

                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_B);
                mPrinter.addText("Order.No. "+mCur.getString(12) +"\n");

                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_B);
                mPrinter.addText("Order Date : " + mCur.getString(16) +"  " +mCur.getString(22) +"\n");

                Cursor mCur4 = mDbHelper.GetSalesOrderschedulePrint(gettransactiono,getfinancialyearcode,"0");
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_B);
                // printer.addText( mCur4.getString(0) +"/" +mCur4.getString(1) +"/"+ mCur4.getString(2)+"/"+ GenCreatedDate() +"\n");
                mPrinter.addText( "Selasman : " +mCur4.getString(1)  +"\n");

                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                String line_space6 = "--------------------------------";
                mPrinter.addText(line_space6);

                mPrinter.addTextFont(Printer.FONT_C);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addText("      Qty           Rate            Value" + "\n");

                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                String line_space12 = "--------------------------------";
                mPrinter.addText(line_space12);
                int getitemcount=0;
                double gettotal = 0;
                // for(int i=0;i<mCur.getCount();i++) {
                Cursor mCur1 = mDbHelper.GetSalesOrderItemPrint(gettransactiono, getfinancialyearcode, mCur.getString(17));
                getitemcount = mCur1.getCount();

                double cnt = 0.0;

                for (int j = 0; j < mCur1.getCount(); j++) {
                    String Product = (mCur1.getString(0));

                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addText(Product + "\n");

                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    String SalesDetails = "";
                    StringBuffer SalesBuffer = new StringBuffer(100);
                    SalesBuffer.append(SalesDetails);

                    SalesBuffer.append(Util.nameLeftValueRightJustifysalesitem(mCur1.getString(1), mCur1.getString(4),
                            String.format("%.2f", mCur1.getFloat(3)), String.format("%.2f", mCur1.getFloat(2)),
                            32));
                    SalesDetails = SalesBuffer.toString();
                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addText(SalesDetails + "\n");

                    gettotal = gettotal + Double.parseDouble(String.valueOf(mCur1.getDouble(2)));
                    mCur1.moveToNext();
                }

                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                String line_space3 = "--------------------------------\n";
                mPrinter.addText(line_space3);

                //mCur.moveToNext();
                // }

                String subtotal = "";
                String gettotalamt = dft.format(gettotal);
                int valuetotal = (int)gettotal;
                mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addText(getitemcount + "      Total " + " : "+ Util.rightJustify( dft.format(Math.round(gettotal)),11) + "\n");

                double discunt= mDbHelper.GetSalesOrderDiscount(gettransactiono,getfinancialyearcode,mCur.getString(17));
                String discount=dft.format(discunt);

                if(!discount.equals("") && !discount.equals("0") && !discount.equals("0.00")) {
                    mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_A);
                    int valuediscunt = (int)discunt;
                    mPrinter.addText(" Discount " + " : " + Util.rightJustify(dft.format(discunt), 11) + "\n");
                }

                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                String line_space145 = "--------------------------------\n";
                mPrinter.addText(line_space145);

                double net1=0;
                if(!discount.equals("") && !discount.equals("0") && !discount.equals("0.00")) {
                    net1=gettotal-discunt;
                }else{
                    net1=gettotal-0;
                }

                mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addText(" Nett Amount " + " : "+ Util.rightJustify( dft.format(Math.round(net1)),11) + "\n");

                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                String line_space14 = "--------------------------------\n";
                mPrinter.addText(line_space14);

                String transportmode = "-";
                if(!mCur.getString(26).equals("") && !mCur.getString(26).equals("null") && !mCur.getString(26).equals(null)){
                    transportmode = mCur.getString(26);
                }
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_B);
                mPrinter.addText("Transport Mode : "+transportmode+" \n");

                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_B);
                String lorryset = "-";
                if(!mCur.getString(23).equals("")){
                    lorryset = mCur.getString(23);
                }
                String day = "-";
                if(!mCur.getString(24).equals("")){
                    day = mCur.getString(24);
                }
                mPrinter.addText("Transport Name : "+lorryset +"\n");

                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_B);
                mPrinter.addText("Day    : "+day +"\n");

                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                String line_space121 = "--------------------------------\n";
                mPrinter.addText(line_space121);

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_C);
                mPrinter.addText("Customer Care : 9361807141" +"\n");

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_C);
                mPrinter.addText("Subject to "+jurisdiction+" Jurisdiction" +"\n");

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_C);
                mPrinter.addText( mCur4.getString(1) +"/"+ GenCreatedDate() +"\n");

                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                String line_space19 = "--------------------------------\n";
                mPrinter.addText(line_space19);

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextFont(Printer.FONT_C);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                String poweredby ="Powered by www.shivasoftwares.com\n";
                mPrinter.addText(poweredby);

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                String line_space1  = "--------------------------------\n";
                mPrinter.addText(line_space1);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextFont(Printer.FONT_B);
                String thankmsg = "         "+preferenceMangr.pref_getString("getwishmsg")+"      \n\n\n";
                // String thankmsg = "    நன்றி      \n\n\n\n";
                mPrinter.addText(thankmsg);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                String emptylines1 = "";
                // printer.addText(emptylines1);

                //String emptylines1 = "\n";
                //printer.addText(emptylines1);
                try {

                    if(!connectPrinter()) {
                        mPrinter.clearCommandBuffer();
                        return false;
                    }

                    PrinterStatusInfo status = mPrinter.getStatus();

                    Log.d("STATUS", Integer.toString(status.getConnection()));
                    Log.d("ONLINE", Integer.toString(status.getOnline()));
                    //Log.d("BATTERY", Integer.toString(status.getBatteryLevel()));

                    if ((status.getConnection() == 1) && (status.getOnline() == 1)) {
                        try {
                            mPrinter.sendData(Printer.PARAM_DEFAULT);
                            billPrinted = true;
                        } catch (Exception e) {
                            Log.d("bbb", e.getLocalizedMessage());
                            Log.d("PRINT", "failed to send data");
                            mPrinter.clearCommandBuffer();
                            try {
                                mPrinter.disconnect();
                            } catch (Exception ex) {
                                // Do nothing
                            }
                        }
                    }

                } catch (Exception ex) {
                    Log.d("End PRINT", ex.getMessage());
                }

            } else {
                Log.d("nodata","nodata");
                //Toast.makeText(mContext,"No data Available",Toast.LENGTH_SHORT).show();
                objActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d("thread","thread");
                        Toast.makeText(mContext, "No data Available", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Epos2Exception e) {
            e.printStackTrace();
            Log.d("PrintData : GetSalesOrderBillPrint", e.getMessage());
        }
        return billPrinted;
    }

    //Receipt
    @SuppressLint("LongLogTag")
    public boolean GetReceiptBillPrint(String gettransactiono, String getfinancialyearcode, Activity objActivity) {

        if (mPrinter == null) {
            initializePrinter(objActivity.getApplicationContext());
        }

        if (mPrinter == null)
            return false;

        //Printer printer = null;
        boolean billPrinted = false;
        //TamilUtil tamilUtil;
        String printheader ="";
        try{
            DataBaseAdapter mDbHelper = new DataBaseAdapter(mContext);
            mDbHelper.open();
            Cursor mCur = mDbHelper.GetReceiptPrint(gettransactiono,getfinancialyearcode);

            if (mCur.getCount() > 0) {

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_A);
//ZdCU0p
                //உ
               /* String startmsg = "       ஸ்ரீ        \n";
                printer.addText(startmsg);
                String startmsgline = "       --        \n";
                printer.addText(startmsgline);*/

                printheader=mDbHelper.getprintheader();

                if(!printheader.equals("")&&!printheader.equals(null)&&!printheader.equals("null")){

                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    String startmsg = printheader+"\n";
                    mPrinter.addText(startmsg);
                    String startmsgline = "       --        \n";
                    mPrinter.addText(startmsgline);
                }

                mPrinter.addText(mCur.getString(1)+ "\n");
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_B);

                mPrinter.addText(mCur.getString(2)+"\n");

                //printer.addText(mCur.getString(7)+ "-" + mCur.getString(8)+ "\n");
                String companyCityStateDetails = mCur.getString(7)+ " - " + mCur.getString(8);

                // show statename and gst state code
                String company_stateNameWithGSTStatecode = "";
                if(!Utilities.isNullOrEmpty(mCur.getString(31)))
                    company_stateNameWithGSTStatecode = mCur.getString(31).trim();
                if(!Utilities.isNullOrEmpty(mCur.getString(32)))
                    company_stateNameWithGSTStatecode = company_stateNameWithGSTStatecode + " - " + mCur.getString(32).trim();

                if(!Utilities.isNullOrEmpty(company_stateNameWithGSTStatecode))
                    companyCityStateDetails= companyCityStateDetails.trim() + " , " + company_stateNameWithGSTStatecode.trim();

                mPrinter.addText(companyCityStateDetails.trim() + "\n");

                if(mCur.getString(16).equals("")){
                    mPrinter.addText(" Ph: " + mCur.getString(5) + "\n");
                }else {

                    mPrinter.addText(" Ph: " + mCur.getString(5) + "\n");
                }
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_A);

                mPrinter.addText(" GSTIN:"+mCur.getString(6) +"\n");

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_B);
                mPrinter.addText(" PAN:"+mCur.getString(9)+"          "+
                        mCur.getString(15) +"\n");

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_A);
                String line_space = "--------------------------------\n";
                mPrinter.addText(line_space);

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addText("Receipt \n");
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);

                String line_space1 = "--------------------------------\n";
                mPrinter.addText(line_space1);

                mPrinter.addTextFont(Printer.FONT_B);
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);

                String billdetails = "Rec No. : "+ mCur.getString(10) +"\n";
                mPrinter.addText(billdetails);

                String billdetails1 = "Date    : "+ mCur.getString(11) +"  "+
                        mCur.getString(25) +" \n";
                mPrinter.addText(billdetails1);

                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);

                /*String cusname= mCur.getString(13);
                if(cusname.contains("ஸ்ரீ")){
                    cusname = TamilUtil.convertToTamil(TamilUtil.BAMINI,cusname);

                }*/
                String custname = "Mr/Ms : "+ mCur.getString(13) +"\n";
                mPrinter.addText(custname);

                // show the city name with area pincode
                String cityNameWithPincode = "";
                if(!Utilities.isNullOrEmpty(mCur.getString(27)))
                    cityNameWithPincode = mCur.getString(27).trim();
                if(!Utilities.isNullOrEmpty(mCur.getString(28)))
                    cityNameWithPincode = cityNameWithPincode + " - " +mCur.getString(28).trim();

                if(!Utilities.isNullOrEmpty(cityNameWithPincode)) {
                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_C);
                    mPrinter.addText(cityNameWithPincode.trim() + "\n");
                }

                // show statename and gst state code
                String stateNameWithGSTStatecode = "";
                if(!Utilities.isNullOrEmpty(mCur.getString(29)))
                    stateNameWithGSTStatecode = mCur.getString(29).trim();
                if(!Utilities.isNullOrEmpty(mCur.getString(30)))
                    stateNameWithGSTStatecode = stateNameWithGSTStatecode + " - " +mCur.getString(30).trim();

                if(!Utilities.isNullOrEmpty(stateNameWithGSTStatecode)) {
                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_C);
                    mPrinter.addText(stateNameWithGSTStatecode.trim() + "\n");
                }

                mPrinter.addTextFont(Printer.FONT_B);
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);

                if(!mCur.getString(16).equals("")) {
                    String gstin = "GSTIN   : " + mCur.getString(16) + "\n";
                    mPrinter.addText(gstin);
                }

                if(!mCur.getString(17).equals("")) {
                    String phonno = "Contact : " + mCur.getString(17) + "\n";
                    mPrinter.addText(phonno);
                }

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                String line_spaceparticulars = "------------------------------------------\n";
                mPrinter.addText(line_spaceparticulars);

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addText("Particulars \n");

                String line_spaceparticularsend = "--------------------------------\n";
                mPrinter.addText(line_spaceparticularsend);

                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);

                String receivedsum = "  Received Sum of Rupees \n";
                mPrinter.addText(receivedsum);

                String amtval = "  Rs "+String.format("%.2f", mCur.getFloat(20))+"\n";
                mPrinter.addText(amtval);

                mPrinter.addTextFont(Printer.FONT_B);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);

                String amounttxt = PrintData.convert(mCur.getInt(20))+ " Only\n";
                String amountString=containsWhiteSpace(amounttxt);
                mPrinter.addText(amountString);

                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                String paymode = "Pay mode  : "+ mCur.getString(18) +" \n";
                mPrinter.addText(paymode);

                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);

                String remarks =   mCur.getString(26) +"\n";
                mPrinter.addText(remarks);

                String getcheque ="";
                if(!mCur.getString(19).equals("")) {
                    getcheque = " Cheque No. : " +mCur.getString(19);

                    String cheque =  getcheque  +"\n";
                    mPrinter.addText(cheque);
                }

                String getbankname ="";
                if(!mCur.getString(33).equals("")) {
                    getbankname = " Bank Name : " +mCur.getString(33);

                    String bankname =  getbankname  +"\n";
                    mPrinter.addText(bankname);
                }
                String getchequedate ="";
                if( mCur.getString(34) != null && !mCur.getString(34).equals("") && !mCur.getString(34).equals("null")) {
                    getchequedate = " Cheque Date : " +mCur.getString(34);

                    String chequedate =  getchequedate  +"\n";
                    mPrinter.addText(chequedate);
                }
                if(mCur.getString(18).equals("UPI")) {
                    String getUPIVenderName ="";
                    if(mCur.getString(37) != null && !mCur.getString(37).equals("") && !mCur.getString(37).equals("null")) {
                        getUPIVenderName = " Payment Vender Name : " +mCur.getString(37);

                        String cheque =  getUPIVenderName  +"\n";
                        mPrinter.addText(cheque);
                    }
                    String getUPITranactionId ="";
                    if(mCur.getString(36) != null && !mCur.getString(36).equals("") && !mCur.getString(36).equals("null")) {
                        getUPITranactionId = " Transaction ID : " +mCur.getString(36);

                        String cheque =  getUPITranactionId  +"\n";
                        mPrinter.addText(cheque);
                    }
                }

                if(!mCur.getString(21).equals("")) {

                    String details = "Details  : " + mCur.getString(21) + "\n";
                    mPrinter.addText(details);
                }
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                String line_space61 = "--------------------------------";
                mPrinter.addText(line_space61);

                mPrinter.addTextFont(Printer.FONT_C);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                String gethelp = "";
                if(!mCur.getString(24).equals("")) {
                    gethelp = "/" + mCur.getString(24);
                }
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                String getdate = GenCreatedDate();
                String scheduledetails = mCur.getString(22) +"/"+getdate+"\n";
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addText(scheduledetails);

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                String line_spacep = "------------------------------------------";
                mPrinter.addText(line_spacep);

                mPrinter.addTextFont(Printer.FONT_C);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                String poweredby ="Powered by www.shivasoftwares.com\n";
                mPrinter.addText(poweredby);

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                String line_space4 = "------------------------------------------\n";
                mPrinter.addText(line_space4);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextFont(Printer.FONT_B);
                //String thankmsg = "       நன்றி      \n\n\n";
                String thankmsg = "         "+preferenceMangr.pref_getString("getwishmsg")+"      \n\n\n";
                mPrinter.addText(thankmsg);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);

            }else{
                Log.d("nodata","nodata");
                //Toast.makeText(mContext,"No data Available",Toast.LENGTH_SHORT).show();
                objActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d("thread","thread");
                        Toast.makeText(mContext, "No data Available", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            if(!connectPrinter()) {
                mPrinter.clearCommandBuffer();
                return false;
            }

            PrinterStatusInfo status = mPrinter.getStatus();

            Log.d("STATUS", Integer.toString(status.getConnection()));
            Log.d("ONLINE", Integer.toString(status.getOnline()));

            if ((status.getConnection()==1) && (status.getOnline()==1)) {
                try {
                    mPrinter.sendData(Printer.PARAM_DEFAULT);
                    billPrinted = true;
                } catch (Epos2Exception e) {
                    Log.d("bbb", e.getLocalizedMessage());
                    Log.d("PRINT", "failed to send data");
                    mPrinter.clearCommandBuffer();
                    try {
                        mPrinter.disconnect();
                    } catch (Exception ex) {
                        // Do nothing
                    }
                }
            }
        } catch (Epos2Exception e) {
            e.printStackTrace();
            Log.d("PrintData : GetReceiptBillPrint", e.getLocalizedMessage());
        }
        return billPrinted;
    }

    //Van Stock print
    @SuppressLint("LongLogTag")
    public boolean GetVanStockPrint(Activity objActivity) {

        if (mPrinter == null) {
            initializePrinter(objActivity.getApplicationContext());
        }

        if (mPrinter == null)
            return false;

        //Printer printer = null;
        boolean billPrinted = false;
        String printheader ="";
        try{

            DataBaseAdapter objcustomerAdaptor = new DataBaseAdapter(mContext);
            objcustomerAdaptor.open();

            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
            mPrinter.addTextFont(Printer.FONT_A);

            /*String startmsg = "       உ        \n";
            printer.addText(startmsg);
            String startmsgline = "       --        \n";
            printer.addText(startmsgline);*/

            printheader=objcustomerAdaptor.getprintheader();

            if(!printheader.equals("")&&!printheader.equals(null)&&!printheader.equals("null")){

                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                String startmsg = printheader+"\n";
                mPrinter.addText(startmsg);
                String startmsgline = "       --        \n";
                mPrinter.addText(startmsgline);
            }

            //printer.addText(" Van Stock " + " - "+ LoginActivity.getvanname +" - " + LoginActivity.getcurrentdatetime + "\n");
            mPrinter.addText(" Van Stock \n");

         /*   printer.addTextAlign(Printer.ALIGN_CENTER);
            printer.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
            printer.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
            printer.addTextFont(Printer.FONT_B);
            printer.addText("Date : "+LoginActivity.getcurrentdatetime + "\n");*/
            Cursor mCur1 = objcustomerAdaptor.GetVanStockCompany(VanStockActivity.getlistcompanycode,
                    VanStockActivity.getitemsatus, VanStockActivity.getlistitemsubgroupcode);

            if (mCur1.getCount() > 0) {

                for (int j = 0; j < mCur1.getCount(); j++) {

                    Cursor mCur = objcustomerAdaptor.GetVanStock(mCur1.getString(0),
                            VanStockActivity.getitemsatus, VanStockActivity.getlistitemsubgroupcode);

                    if (mCur.getCount() > 0){
                        String getschedulecode = objcustomerAdaptor.GetScheduleCode();
                        Cursor mCurSchedule = objcustomerAdaptor.GetBillScheduleDetails(getschedulecode);

                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_A);

                        mPrinter.addText(mCur1.getString(1) + " " + mCur1.getString(8) + "\n");

                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_A);

                        mPrinter.addText(" GSTIN:" + mCur1.getString(7) + "\n");


                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_B);
                        mPrinter.addText("Date : " + preferenceMangr.pref_getString("getcurrentdatetime") + " " + preferenceMangr.pref_getString("getvanname") + "\n");

                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addText("Route : " + mCurSchedule.getString(0) + "\n");
                        mPrinter.addText("Vehicle : " + mCurSchedule.getString(1) + "\n");
                        mPrinter.addText("Salesman : " + mCurSchedule.getString(2) + "\n");
                        mPrinter.addText("Driver : " + mCurSchedule.getString(3) + "\n");

                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                        String line_space6 = "--------------------------------";
                        mPrinter.addText(line_space6);

                        mPrinter.addTextFont(Printer.FONT_C);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addText("    OP     In    Sales    Out       CL " + "\n");

                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        String line_space1 = "-------------------------------\n";
                        mPrinter.addText(line_space1);


                        DecimalFormat df;

                        double gettotal = 0;
                        double cnt = 0.0;
                        for (int i = 0; i < mCur.getCount(); i++) {
                            String Product = (mCur.getString(0));
                            String unit = (mCur.getString(1));

                            mPrinter.addTextFont(Printer.FONT_B);
                            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                            mPrinter.addText(Product + " - (" + unit + ") " + "\n");
                            mPrinter.addFeedLine(1);
                            mPrinter.addTextFont(Printer.FONT_A);
                            String SalesDetails = "";
                            StringBuffer SalesBuffer = new StringBuffer(100);
                            SalesBuffer.append(SalesDetails);

                            String getdecimalvalue = mCur.getString(6);
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
                            if (getnoofdigits.equals("")) {
                                SalesBuffer.append(Util.nameLeftValueRightJustify5(mCur.getString(2),
                                        mCur.getString(3), mCur.getString(12), mCur.getString(4),
                                        mCur.getString(5), "",
                                        32));
                            } else {
                                SalesBuffer.append(Util.nameLeftValueRightJustify5(df.format(mCur.getFloat(2)),
                                        df.format(mCur.getFloat(3)), df.format(mCur.getFloat(12)),
                                        df.format(mCur.getFloat(4)), df.format(mCur.getFloat(5)), "",
                                        32));
                            }


                            SalesDetails = SalesBuffer.toString();
                            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                            mPrinter.addText(SalesDetails );
                            mPrinter.addFeedLine(0);
                            mPrinter.addLineSpace(0);
                            mCur.moveToNext();
                        }

                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                        String line_space3 = "--------------------------------\n";
                        mPrinter.addText(line_space3);

                        mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addText(" Total Product " + " :  " +
                                Util.leftJustify(String.valueOf(mCur.getCount()), 4) + "\n");
                        String line_space41 = "--------------------------------\n";
                        mPrinter.addText(line_space41);

                    }
                    mCur1.moveToNext();
                }

                mPrinter.addTextFont(Printer.FONT_C);
                mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                String Gencode= GenCreatedDate();
                String retailerdetails =  Gencode +"\n";
                mPrinter.addText(retailerdetails);

                mPrinter.addTextFont(Printer.FONT_C);
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                String line_space13 = "------------------------------------------\n";
                mPrinter.addText(line_space13);

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                String poweredby ="Powered by www.shivasoftwares.com\n\n";
                mPrinter.addText(poweredby);

                String emptylines1 = "\n\n\n\n";
                mPrinter.addText(emptylines1);

                //objcustomerAdaptor.close();

                try {

                    if(!connectPrinter()) {
                        mPrinter.clearCommandBuffer();
                        return false;
                    }

                    PrinterStatusInfo status = mPrinter.getStatus();

                    Log.d("STATUS", Integer.toString(status.getConnection()));
                    Log.d("ONLINE", Integer.toString(status.getOnline()));

                    if ((status.getConnection() == 1) && (status.getOnline() == 1)) {
                        try {
                            mPrinter.sendData(Printer.PARAM_DEFAULT);
                            billPrinted = true;
                        } catch (Epos2Exception e) {
                            Log.d("bbb", e.getLocalizedMessage());
                            Log.d("PRINT", "failed to send data");
                            mPrinter.clearCommandBuffer();
                            try {
                                mPrinter.disconnect();
                            } catch (Exception ex) {
                                // Do nothing
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            else {
                Log.d("nodata", "nodata");
                //Toast.makeText(mContext,"No data Available",Toast.LENGTH_SHORT).show();
                objActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d("thread", "thread");
                        Toast.makeText(mContext, "No data Available", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Epos2Exception e) {
            e.printStackTrace();
            Log.d("PrintData : GetVanStockPrint", e.getLocalizedMessage());
        }

        return billPrinted;
    }

    // Sales Bill Wise Report
    @SuppressLint("LongLogTag")
    public boolean GetSalesBillWiseReportPrint(Activity objActivity) {

        if (mPrinter == null) {
            initializePrinter(objActivity.getApplicationContext());
        }

        if (mPrinter == null)
            return false;

        //Printer printer = null;
        boolean billPrinted = false;
        String printheader ="";
        try{

            DataBaseAdapter objcustomerAdaptor = new DataBaseAdapter(mContext);
            objcustomerAdaptor.open();

            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
            mPrinter.addTextFont(Printer.FONT_A);

            /*String startmsg = "       உ        \n";
            printer.addText(startmsg);
            String startmsgline = "       --        \n";
            printer.addText(startmsgline);*/

            printheader=objcustomerAdaptor.getprintheader();

            if(!printheader.equals("")&&!printheader.equals(null)&&!printheader.equals("null")){

                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                String startmsg = printheader+"\n";
                mPrinter.addText(startmsg);
                String startmsgline = "       --        \n";
                mPrinter.addText(startmsgline);
            }

            mPrinter.addText(" Sales Bill-wise Report \n");

           /* printer.addTextAlign(Printer.ALIGN_CENTER);
            printer.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
            printer.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
            printer.addTextFont(Printer.FONT_B);
            printer.addText("Date : "+LoginActivity.getcurrentdatetime + "\n");*/
            Cursor mCur1 = objcustomerAdaptor.GetSalesBillWiseCompanyListDB(SalesBillWiseReportActivity.getsaleslistdate,
                    SalesBillWiseReportActivity.getfiltercompanycode,SalesBillWiseReportActivity.getpaymenttype,
                    SalesBillWiseReportActivity.getpaymentstatus);

            if (mCur1.getCount() > 0) {

                for (int j = 0; j < mCur1.getCount(); j++) {

                    Cursor mCur = objcustomerAdaptor.GetSalesBillPrintListDB(SalesBillWiseReportActivity.getsaleslistdate,
                            mCur1.getString(0), SalesBillWiseReportActivity.getpaymenttype,
                            SalesBillWiseReportActivity.getpaymentstatus);
                    if (mCur.getCount() > 0){

                        Cursor mCurSchedule = objcustomerAdaptor.GetBillScheduleDetails(mCur1.getString(12));

                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_A);

                        mPrinter.addText(mCur1.getString(1) + " " + mCur1.getString(8) + "\n");

                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_A);

                        mPrinter.addText(" GSTIN:" + mCur1.getString(7) + "\n");


                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_B);
                        mPrinter.addText("Date : " + SalesBillWiseReportActivity.getsalesfilterdate + " " + preferenceMangr.pref_getString("getvanname") + "\n");

                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addText("Route : " + mCurSchedule.getString(0) + "\n");
                        mPrinter.addText("Vehicle : " + mCurSchedule.getString(1) + "\n");
                        mPrinter.addText("Salesman : " + mCurSchedule.getString(2) + "\n");
                        mPrinter.addText("Driver : " + mCurSchedule.getString(3) + "\n");

                    /*    printer.addText(mCur1.getString(4) + "\n");

                        printer.addText(mCur1.getString(8) + "-" +
                                mCur1.getString(11) + "\n");


                        printer.addText(" Ph: " + mCur1.getString(9) + "\n");


                        printer.addTextAlign(Printer.ALIGN_CENTER);
                        printer.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        printer.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        printer.addTextFont(Printer.FONT_B);
                        printer.addText(" PAN:" + mCur1.getString(10) + "          " +
                                LoginActivity.getvanname + "\n");*/

                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addTextAlign(Printer.ALIGN_LEFT);

                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                        String line_space6 = "--------------------------------";
                        mPrinter.addText(line_space6);

                        mPrinter.addTextFont(Printer.FONT_C);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addText(" Bill No.        Bill Type        Amount" + "\n");

                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        String line_space1 = "--------------------------------";
                        mPrinter.addText(line_space1);


                        DecimalFormat df;

                        double gettotal = 0;
                        double cnt = 0.0;
                        for (int i = 0; i < mCur.getCount(); i++) {

                            String getcustomername = mCur.getString(21) + " - " + mCur.getString(22);

                            mPrinter.addTextFont(Printer.FONT_B);
                            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                            mPrinter.addText(getcustomername + "\n");

                            mPrinter.addTextFont(Printer.FONT_C);
                            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);

                            mPrinter.addTextFont(Printer.FONT_B);
                            String SalesDetails = "";
                            StringBuffer SalesBuffer = new StringBuffer(100);
                            SalesBuffer.append(SalesDetails);
                            String getbilltype = "";
                            if (mCur.getString(9).equals("1")) {
                                getbilltype = "CASH";
                            } else {
                                getbilltype = "CREDIT";
                            }
                            SalesBuffer.append(Util.nameLeftValueRightJustifysalesbill(mCur.getString(3),
                                    getbilltype,
                                    String.format("%.2f", mCur.getFloat(14)),
                                    32));
                            SalesDetails = SalesBuffer.toString();
                            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                            mPrinter.addText(SalesDetails + "\n");

                            mPrinter.addTextFont(Printer.FONT_C);
                            String SalesDetails1 = "";
                            StringBuffer SalesBuffer1 = new StringBuffer(100);
                            SalesBuffer1.append(SalesDetails1);

                            String getbillcopystatus = "";
                            if (mCur.getString(15).equals("yes")) {
                                getbillcopystatus = "BCR";
                            } else {
                                getbillcopystatus = "";
                            }
                            String getcashpaidstatus = "";
                            if (mCur.getString(16).equals("yes")) {
                                getcashpaidstatus = "";
                            } else if (mCur.getString(16).equals("no") && mCur.getString(9).equals("1")) {
                                getcashpaidstatus = "Not Paid";
                            }else{
                                getcashpaidstatus = "";
                            }

                            SalesBuffer1.append(Util.nameLeftValueRightJustifysalesbill("BK.No. " + mCur.getString(18),
                                    getcashpaidstatus,
                                    getbillcopystatus,
                                    32));
                            SalesDetails1 = SalesBuffer1.toString();
                            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                            mPrinter.addText(SalesDetails1 + "\n");

                            gettotal = gettotal + mCur.getFloat(14);
                            mCur.moveToNext();
                        }

                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                        String line_space3 = "------------------------------------------\n";
                        mPrinter.addText(line_space3);

                        String gettotalamt = dft.format(gettotal);
                        mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addText(mCur.getCount()+"     Total " + " : " + Util.rightJustify(gettotalamt, 11) + "\n");

                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                        String line_space4 = "--------------------------------\n";
                        mPrinter.addText(line_space4);

                        mCur1.moveToNext();
                    }
                }

                mPrinter.addTextFont(Printer.FONT_C);
                mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                String Gencode= GenCreatedDate();
                String retailerdetails =  Gencode +"\n";
                mPrinter.addText(retailerdetails);

                mPrinter.addTextFont(Printer.FONT_C);
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                String line_space13 = "-----------------------------------------\n";
                mPrinter.addText(line_space13);

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                String poweredby ="Powered by www.shivasoftwares.com\n\n";
                mPrinter.addText(poweredby);

                String emptylines1 = "\n\n\n";
                mPrinter.addText(emptylines1);

                try {

                    if(!connectPrinter()) {
                        mPrinter.clearCommandBuffer();
                        return false;
                    }

                    PrinterStatusInfo status = mPrinter.getStatus();

                    Log.d("STATUS", Integer.toString(status.getConnection()));
                    Log.d("ONLINE", Integer.toString(status.getOnline()));

                    if ((status.getConnection() == 1) && (status.getOnline() == 1)) {
                        try {
                            mPrinter.sendData(Printer.PARAM_DEFAULT);
                            billPrinted = true;
                        } catch (Epos2Exception e) {
                            Log.d("PRINT", "failed to send data");
                            mPrinter.clearCommandBuffer();
                            try {
                                mPrinter.disconnect();
                            } catch (Exception ex) {
                                // Do nothing
                            }
                        }
                    }
                }catch (Exception ex) {
                    ex.printStackTrace();
                }

            }else{
                Log.d("nodata","nodata");
                //Toast.makeText(mContext,"No data Available",Toast.LENGTH_SHORT).show();
                objActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d("thread","thread");
                        Toast.makeText(mContext, "No data Available", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }catch (Epos2Exception e) {
            e.printStackTrace();
            Log.d("PrintData : GetSalesBillWiseReportPrint", e.getLocalizedMessage());
        }
        return billPrinted;
    }

    //Sales Item Wise Report
    @SuppressLint("LongLogTag")
    public boolean GetSalesItemreportBillPrint(Activity objActivity) {

        if (mPrinter == null) {
            initializePrinter(objActivity.getApplicationContext());
        }

        if (mPrinter == null)
            return false;

        //Printer printer = null;
        boolean billPrinted = false;
        String printheader ="";
        try {
            DataBaseAdapter objcustomerAdaptor = new DataBaseAdapter(mContext);
            objcustomerAdaptor.open();

            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
            mPrinter.addTextFont(Printer.FONT_A);

            /*String startmsg = "       உ        \n";
            printer.addText(startmsg);
            String startmsgline = "       --        \n";
            printer.addText(startmsgline);*/

            printheader=objcustomerAdaptor.getprintheader();

            if(!printheader.equals("")&&!printheader.equals(null)&&!printheader.equals("null")){

                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                String startmsg = printheader+"\n";
                mPrinter.addText(startmsg);
                String startmsgline = "       --        \n";
                mPrinter.addText(startmsgline);
            }

            mPrinter.addText(" Sales Item-wise Report \n");

 /*
            printer.addTextAlign(Printer.ALIGN_CENTER);
            printer.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
            printer.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
            printer.addTextFont(Printer.FONT_B);
            printer.addText("Date : "+SalesItemWiseReportActivity.getsaleslistdate + "\n");
*/
            Cursor mCur1 = objcustomerAdaptor.Getcompanydetails(SalesItemWiseReportActivity.getfiltercompanycode,
                    SalesItemWiseReportActivity.getbillfromdate,
                    SalesItemWiseReportActivity.getbilltodate);

            if (mCur1.getCount() > 0) {

                for (int j = 0; j < mCur1.getCount(); j++) {

                    Cursor mCur = objcustomerAdaptor.GetSalesItemWiseReport_Date(
                            SalesItemWiseReportActivity.getbillfromdate, mCur1.getString(0));

                    if (mCur.getCount() > 0) {

                        Cursor mCurSchedule = objcustomerAdaptor.GetBillScheduleDetails(mCur1.getString(12));

                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_A);

                        mPrinter.addText(mCur1.getString(1) + " " + mCur1.getString(8) + "\n");

                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_A);

                        mPrinter.addText(" GSTIN:" + mCur1.getString(7) + "\n");

                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_B);
                        mPrinter.addText("Date : " + SalesItemWiseReportActivity.getsaleslistdate + " " + preferenceMangr.pref_getString("getvanname") + "\n");

                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addText("Route : " + mCurSchedule.getString(0) + "\n");
                        mPrinter.addText("Vehicle : " + mCurSchedule.getString(1) + "\n");
                        mPrinter.addText("Salesman : " + mCurSchedule.getString(2) + "\n");
                        mPrinter.addText("Driver : " + mCurSchedule.getString(3) + "\n");

                        mPrinter.addTextAlign(Printer.ALIGN_LEFT);

                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                        String line_space6 = "--------------------------------";
                        mPrinter.addText(line_space6);

                        mPrinter.addTextFont(Printer.FONT_C);

                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addText("Item Name    Qty       Unit         Amount" + "\n");

                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        String line_space1 = "--------------------------------";
                        mPrinter.addText(line_space1);

                        double gettotal = 0;
                        double cnt = 0.0;
                        for (int i = 0; i < mCur.getCount(); i++) {
                            String Product = (mCur.getString(1));

                            mPrinter.addTextFont(Printer.FONT_B);
                            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                            mPrinter.addText(Product + "\n");

                            mPrinter.addTextFont(Printer.FONT_A);
                            String SalesDetails = "";
                            StringBuffer SalesBuffer = new StringBuffer(100);
                            SalesBuffer.append(SalesDetails);


                            SalesBuffer.append(Util.nameLeftValueRightJustify(Integer.toString(mCur.getInt(2)),
                                    mCur.getString(3), String.format("%.2f", mCur.getFloat(4)),
                                    32));
                            SalesDetails = SalesBuffer.toString();
                            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                            mPrinter.addText(SalesDetails + "\n");


                            cnt = cnt + mCur.getFloat(4);
                            gettotal = gettotal + Double.parseDouble(String.valueOf(mCur.getDouble(4)));
                            mCur.moveToNext();
                        }

                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                        String line_space3 = "--------------------------------\n";
                        mPrinter.addText(line_space3);

                        String subtotal = "";
                        String gettotalamt = dft.format(gettotal);

                        mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addText(mCur.getCount() + "    Total " + " : " + Util.rightJustify(gettotalamt, 11) + "\n");

                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                        String line_space4 = "--------------------------------\n";
                        mPrinter.addText(line_space4);
                        mCur1.moveToNext();
                    }
                }

                mPrinter.addTextFont(Printer.FONT_C);
                mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                String Gencode = GenCreatedDate();
                String retailerdetails = Gencode + "\n";
                mPrinter.addText(retailerdetails);

                mPrinter.addTextFont(Printer.FONT_C);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                String line_space13 = "-----------------------------------------\n";
                mPrinter.addText(line_space13);

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                String poweredby = "Powered by www.shivasoftwares.com\n\n";
                mPrinter.addText(poweredby);

                String emptylines1 = "\n\n\n";
                mPrinter.addText(emptylines1);

                try {

                    if(!connectPrinter()) {
                        mPrinter.clearCommandBuffer();
                        return false;
                    }

                    PrinterStatusInfo status = mPrinter.getStatus();

                    Log.d("STATUS", Integer.toString(status.getConnection()));
                    Log.d("ONLINE", Integer.toString(status.getOnline()));

                    if ((status.getConnection() == 1) && (status.getOnline() == 1)) {
                        try {
                            mPrinter.sendData(Printer.PARAM_DEFAULT);
                            billPrinted = true;
                        } catch (Epos2Exception e) {
                            Log.d("bbb", e.getLocalizedMessage());
                            Log.d("PRINT", "failed to send data");
                            mPrinter.clearCommandBuffer();
                            try {
                                mPrinter.disconnect();
                            } catch (Exception ex) {
                                // Do nothing
                            }
                        }
                    }

                }catch (Exception ex) {
                    ex.printStackTrace();
                }
            }else{
                Log.d("nodata","nodata");
                //Toast.makeText(mContext,"No data Available",Toast.LENGTH_SHORT).show();
                objActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d("thread","thread");
                        Toast.makeText(mContext, "No data Available", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Epos2Exception e) {
            e.printStackTrace();
            Log.d("PrintData : GetSalesItemreportBillPrint", e.getLocalizedMessage());
        }
        return billPrinted;
    }

    //SalesReturmBill wise Report
    @SuppressLint("LongLogTag")
    public boolean GetSalesReturnBillWiseReportPrint(Activity objActivity) {

        if (mPrinter == null) {
            initializePrinter(objActivity.getApplicationContext());
        }

        if (mPrinter == null)
            return false;

        //Printer printer = null;
        boolean billPrinted = false;
        String printheader ="";
        try{

            DataBaseAdapter objcustomerAdaptor = new DataBaseAdapter(mContext);
            objcustomerAdaptor.open();

            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
            mPrinter.addTextFont(Printer.FONT_A);

            /*String startmsg = "       உ        \n";
            printer.addText(startmsg);
            String startmsgline = "       --        \n";
            printer.addText(startmsgline);*/

            printheader=objcustomerAdaptor.getprintheader();

            if(!printheader.equals("")&&!printheader.equals(null)&&!printheader.equals("null")){

                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                String startmsg = printheader+"\n";
                mPrinter.addText(startmsg);
                String startmsgline = "       --        \n";
                mPrinter.addText(startmsgline);
            }

            mPrinter.addText(" Sales Return Bill-wise Report \n");

            /*printer.addTextAlign(Printer.ALIGN_CENTER);
            printer.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
            printer.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
            printer.addTextFont(Printer.FONT_B);
            printer.addText("Date : "+LoginActivity.getcurrentdatetime + "\n");*/
            Cursor mCur1 = objcustomerAdaptor.GetSalesReturnBillWiseCompanyListDB(SalesReturnBillwiseReportActivity.getsaleslistdate,
                    SalesReturnBillwiseReportActivity.getfiltercompanycode,
                    SalesReturnBillwiseReportActivity.getpaymenttype,SalesReturnBillwiseReportActivity.getpaymentstatus);

            if (mCur1.getCount() > 0) {

                for (int j = 0; j < mCur1.getCount(); j++) {

                    Cursor mCur = objcustomerAdaptor.GetSalesReturnBillListDB(SalesReturnBillwiseReportActivity.getsaleslistdate,
                            mCur1.getString(0), SalesReturnBillwiseReportActivity.getpaymenttype);


                    if (mCur.getCount() > 0){

                        Cursor mCurSchedule = objcustomerAdaptor.GetBillScheduleDetails(mCur1.getString(12));

                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_A);

                        mPrinter.addText(mCur1.getString(1) + " " + mCur1.getString(8) + "\n");

                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_A);

                        mPrinter.addText(" GSTIN:" + mCur1.getString(7) + "\n");

                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_B);
                        mPrinter.addText("Date : " + SalesReturnBillwiseReportActivity.salesdate.getText().toString() + " " + preferenceMangr.pref_getString("getvanname") + "\n");

                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addText("Route : " + mCurSchedule.getString(0) + "\n");
                        mPrinter.addText("Vehicle : " + mCurSchedule.getString(1) + "\n");
                        mPrinter.addText("Salesman : " + mCurSchedule.getString(2) + "\n");
                        mPrinter.addText("Driver : " + mCurSchedule.getString(3) + "\n");

                    /*    printer.addText(mCur1.getString(4) + "\n");

                        printer.addText(mCur1.getString(8) + "-" +
                                mCur1.getString(11) + "\n");


                        printer.addText(" Ph: " + mCur1.getString(9) + "\n");


                        printer.addTextAlign(Printer.ALIGN_CENTER);
                        printer.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        printer.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        printer.addTextFont(Printer.FONT_B);
                        printer.addText(" PAN:" + mCur1.getString(10) + "          " +
                                LoginActivity.getvanname + "\n");*/

                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addTextAlign(Printer.ALIGN_LEFT);

                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                        String line_space6 = "--------------------------------";
                        mPrinter.addText(line_space6);

                        /*printer.addTextAlign(Printer.ALIGN_CENTER);
                        printer.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        printer.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        printer.addTextFont(Printer.FONT_A);

                        printer.addText(mCur1.getString(1) + "\n");
                        printer.addTextAlign(Printer.ALIGN_CENTER);
                        printer.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        printer.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                        printer.addTextFont(Printer.FONT_B);

                        printer.addText(mCur1.getString(4) + "\n");

                        printer.addText(mCur1.getString(8) + "-" +
                                mCur1.getString(11) + "\n");

                        printer.addText(" Ph: " + mCur1.getString(9) + "\n");

                        printer.addTextAlign(Printer.ALIGN_CENTER);
                        printer.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        printer.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        printer.addTextFont(Printer.FONT_A);

                        printer.addText(" GSTIN:" + mCur1.getString(7) + "\n");


                        printer.addTextAlign(Printer.ALIGN_CENTER);
                        printer.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        printer.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        printer.addTextFont(Printer.FONT_B);
                        printer.addText(" PAN:" + mCur1.getString(10) + "          " +
                                LoginActivity.getvanname + "\n");

                        printer.addTextAlign(Printer.ALIGN_CENTER);
                        printer.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        printer.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        printer.addTextFont(Printer.FONT_A);
                        printer.addTextAlign(Printer.ALIGN_LEFT);

                        printer.addTextFont(Printer.FONT_A);
                        printer.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                        String line_space6 = "--------------------------------";
                        printer.addText(line_space6);*/

                        mPrinter.addTextFont(Printer.FONT_C);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addText(" Bill No.        Bill Type        Amount" + "\n");

                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        String line_space1 = "--------------------------------";
                        mPrinter.addText(line_space1);

                        double gettotal = 0;
                        double cnt = 0.0;
                        for (int i = 0; i < mCur.getCount(); i++) {

                            String getcustomername = mCur.getString(21) + " - " + mCur.getString(22);

                            mPrinter.addTextFont(Printer.FONT_B);
                            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                            mPrinter.addText(getcustomername + "\n");

                            mPrinter.addTextFont(Printer.FONT_C);
                            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);

                            mPrinter.addTextFont(Printer.FONT_B);
                            String SalesDetails = "";
                            StringBuffer SalesBuffer = new StringBuffer(100);
                            SalesBuffer.append(SalesDetails);
                            String getbilltype = "";
                            if (mCur.getString(9).equals("1")) {
                                getbilltype = "CASH";
                            } else {
                                getbilltype = "CREDIT";
                            }
                            SalesBuffer.append(Util.nameLeftValueRightJustifysalesbill(mCur.getString(3),
                                    getbilltype,
                                    String.format("%.2f", mCur.getFloat(14)),
                                    32));
                            SalesDetails = SalesBuffer.toString();
                            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                            mPrinter.addText(SalesDetails + "\n");

                            mPrinter.addTextFont(Printer.FONT_C);
                            String SalesDetails1 = "";
                            StringBuffer SalesBuffer1 = new StringBuffer(100);
                            SalesBuffer1.append(SalesDetails1);


                            SalesBuffer1.append(Util.nameLeftValueRightJustifysalesbill("BK.No. " + mCur.getString(18),
                                    "",
                                    "",
                                    32));
                            SalesDetails1 = SalesBuffer1.toString();
                            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                            mPrinter.addText(SalesDetails1 + "\n");

                            gettotal = gettotal + mCur.getFloat(14);
                            mCur.moveToNext();
                        }

                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                        String line_space3 = "------------------------------------------\n";
                        mPrinter.addText(line_space3);

                        String gettotalamt = dft.format(gettotal);
                        mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addText(mCur.getCount() + "      Total " + " : " + Util.rightJustify(gettotalamt, 11) + "\n");

                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                        String line_space4 = "--------------------------------\n";
                        mPrinter.addText(line_space4);

                        mCur1.moveToNext();
                    }
                }

                mPrinter.addTextFont(Printer.FONT_C);
                mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                String Gencode= GenCreatedDate();
                String retailerdetails =  Gencode +"\n";
                mPrinter.addText(retailerdetails);

                mPrinter.addTextFont(Printer.FONT_C);
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                String line_space13 = "-----------------------------------------\n";
                mPrinter.addText(line_space13);

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                String poweredby ="Powered by www.shivasoftwares.com\n\n";
                mPrinter.addText(poweredby);

                String emptylines1 = "\n\n\n";
                mPrinter.addText(emptylines1);

                try {

                    if(!connectPrinter()) {
                        mPrinter.clearCommandBuffer();
                        return false;
                    }

                    PrinterStatusInfo status = mPrinter.getStatus();

                    Log.d("STATUS", Integer.toString(status.getConnection()));
                    Log.d("ONLINE", Integer.toString(status.getOnline()));

                    if ((status.getConnection() == 1) && (status.getOnline() == 1)) {
                        try {
                            mPrinter.sendData(Printer.PARAM_DEFAULT);
                            billPrinted = true;
                        } catch (Epos2Exception e) {
                            Log.d("bbb", e.getLocalizedMessage());
                            Log.d("PRINT", "failed to send data");
                            mPrinter.clearCommandBuffer();
                            try {
                                mPrinter.disconnect();
                            } catch (Exception ex) {
                                // Do nothing
                            }
                        }
                    }

                }catch (Exception ex) {
                    ex.printStackTrace();
                }
            }else{
                Log.d("nodata","nodata");
                //Toast.makeText(mContext,"No data Available",Toast.LENGTH_SHORT).show();
                objActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d("thread","thread");
                        Toast.makeText(mContext, "No data Available", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Epos2Exception e) {
            e.printStackTrace();
            Log.d("PrintData : GetSalesReturnBillWiseReportPrint",e.getLocalizedMessage());
        }
        return billPrinted;
    }

    //SalesReturn Item wise REport
    @SuppressLint("LongLogTag")
    public boolean GetSalesReturnItemreportBillPrint(Activity objActivity) {

        if (mPrinter == null) {
            initializePrinter(objActivity.getApplicationContext());
        }

        if (mPrinter == null)
            return false;

        //Printer printer = null;
        boolean billPrinted = false;
        String printheader ="";
        try{
            DataBaseAdapter objcustomerAdaptor = new DataBaseAdapter(mContext);
            objcustomerAdaptor.open();

            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
            mPrinter.addTextFont(Printer.FONT_A);

            /*String startmsg = "       உ        \n";
            printer.addText(startmsg);
            String startmsgline = "       --        \n";
            printer.addText(startmsgline);*/

            printheader=objcustomerAdaptor.getprintheader();


            if(!printheader.equals("")&&!printheader.equals(null)&&!printheader.equals("null")){

                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                String startmsg = printheader+"\n";
                mPrinter.addText(startmsg);
                String startmsgline = "       --        \n";
                mPrinter.addText(startmsgline);
            }

            mPrinter.addText(" Sales Return Item-wise Report \n");

 /*
            printer.addTextAlign(Printer.ALIGN_CENTER);
            printer.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
            printer.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
            printer.addTextFont(Printer.FONT_B);
            printer.addText("Date : "+SalesItemWiseReportActivity.getsaleslistdate + "\n");
*/
            Cursor mCur1 = objcustomerAdaptor.Getsalesreturncompanydetails(SalesReturnItemWiseReportActivity.getfiltercompanycode,
                    SalesReturnItemWiseReportActivity.getbillfromdate,
                    SalesReturnItemWiseReportActivity.getbilltodate);

            if (mCur1.getCount() > 0) {

                for (int j = 0; j < mCur1.getCount(); j++) {

                    Cursor mCur = objcustomerAdaptor.GetSalesReturnItemWiseReport_Date(
                            SalesReturnItemWiseReportActivity.getbillfromdate, mCur1.getString(0));

                    if (mCur.getCount() > 0){

                        Cursor mCurSchedule = objcustomerAdaptor.GetBillScheduleDetails(mCur1.getString(12));

                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_A);

                        mPrinter.addText(mCur1.getString(1) + " " + mCur1.getString(8) + "\n");

                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_A);

                        mPrinter.addText(" GSTIN:" + mCur1.getString(7) + "\n");

                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_B);
                        mPrinter.addText("Date : " + SalesReturnItemWiseReportActivity.getsaleslistdate + " " + preferenceMangr.pref_getString("getvanname") + "\n");

                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addText("Route : " + mCurSchedule.getString(0) + "\n");
                        mPrinter.addText("Vehicle : " + mCurSchedule.getString(1) + "\n");
                        mPrinter.addText("Salesman : " + mCurSchedule.getString(2) + "\n");
                        mPrinter.addText("Driver : " + mCurSchedule.getString(3) + "\n");

                        mPrinter.addTextAlign(Printer.ALIGN_LEFT);

                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                        String line_space6 = "--------------------------------";
                        mPrinter.addText(line_space6);

                        mPrinter.addTextFont(Printer.FONT_C);

                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addText("Item Name    Qty       Unit         Amount" + "\n");

                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        String line_space1 = "--------------------------------";
                        mPrinter.addText(line_space1);

                        double gettotal = 0;
                        double cnt = 0.0;
                        for (int i = 0; i < mCur.getCount(); i++) {
                            String Product = (mCur.getString(1));

                            mPrinter.addTextFont(Printer.FONT_B);
                            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                            mPrinter.addText(Product + "\n");

                            mPrinter.addTextFont(Printer.FONT_A);
                            String SalesDetails = "";
                            StringBuffer SalesBuffer = new StringBuffer(100);
                            SalesBuffer.append(SalesDetails);


                            SalesBuffer.append(Util.nameLeftValueRightJustify(Integer.toString(mCur.getInt(2)),
                                    mCur.getString(3), String.format("%.2f", mCur.getFloat(4)),
                                    32));
                            SalesDetails = SalesBuffer.toString();
                            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                            mPrinter.addText(SalesDetails + "\n");

                            cnt = cnt + mCur.getFloat(4);
                            gettotal = gettotal + Double.parseDouble(String.valueOf(mCur.getDouble(4)));
                            mCur.moveToNext();
                        }

                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                        String line_space3 = "--------------------------------\n";
                        mPrinter.addText(line_space3);

                        String subtotal = "";
                        String gettotalamt = dft.format(gettotal);

                        mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addText(mCur.getCount()+"    Total " + " : " + Util.rightJustify(gettotalamt, 11) + "\n");

                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                        String line_space4 = "--------------------------------\n";
                        mPrinter.addText(line_space4);
                        mCur1.moveToNext();
                    }
                }

                mPrinter.addTextFont(Printer.FONT_C);
                mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                String Gencode= GenCreatedDate();
                String retailerdetails =  Gencode +"\n";
                mPrinter.addText(retailerdetails);

                mPrinter.addTextFont(Printer.FONT_C);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                String line_space13 = "-----------------------------------------\n";
                mPrinter.addText(line_space13);

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                String poweredby ="Powered by www.shivasoftwares.com\n\n";
                mPrinter.addText(poweredby);

                String emptylines1 = "\n\n\n";
                mPrinter.addText(emptylines1);

                try {

                    if(!connectPrinter()) {
                        mPrinter.clearCommandBuffer();
                        return false;
                    }

                    PrinterStatusInfo status = mPrinter.getStatus();

                    Log.d("STATUS", Integer.toString(status.getConnection()));
                    Log.d("ONLINE", Integer.toString(status.getOnline()));

                    if ((status.getConnection() == 1) && (status.getOnline() == 1)) {
                        try {
                            mPrinter.sendData(Printer.PARAM_DEFAULT);
                            billPrinted = true;
                        } catch (Epos2Exception e) {
                            Log.d("bbb", e.getLocalizedMessage());
                            Log.d("PRINT", "failed to send data");
                            mPrinter.clearCommandBuffer();
                            try {
                                mPrinter.disconnect();
                            } catch (Exception ex) {
                                // Do nothing
                            }
                        }
                    }
                }catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            else{
                Log.d("nodata","nodata");
                //Toast.makeText(mContext,"No data Available",Toast.LENGTH_SHORT).show();
                objActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d("thread","thread");
                        Toast.makeText(mContext, "No data Available", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Epos2Exception e) {
            e.printStackTrace();
            Log.d("PrintData : GetSalesReturnItemreportBillPrint", e.getLocalizedMessage());
        }
        return billPrinted;
    }

    //SalesOrderBill wise Report
    @SuppressLint("LongLogTag")
    public boolean GetSalesOrderBillWiseReportPrint(Activity objActivity) {

        if (mPrinter == null) {
            initializePrinter(objActivity.getApplicationContext());
        }

        if (mPrinter == null)
            return false;

        //Printer printer = null;
        boolean billPrinted = false;
        String printheader ="";
        try{

            DataBaseAdapter objcustomerAdaptor = new DataBaseAdapter(mContext);
            objcustomerAdaptor.open();

            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
            mPrinter.addTextFont(Printer.FONT_A);

            /*String startmsg = "       உ        \n";
            printer.addText(startmsg);
            String startmsgline = "       --        \n";
            printer.addText(startmsgline);*/

            printheader=objcustomerAdaptor.getprintheader();

            if(!printheader.equals("")&&!printheader.equals(null)&&!printheader.equals("null")){

                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                String startmsg = printheader+"\n";
                mPrinter.addText(startmsg);
                String startmsgline = "       --        \n";
                mPrinter.addText(startmsgline);
            }

            mPrinter.addText(" Order Bill-wise Report \n");

          /*  Cursor mCur1 = objcustomerAdaptor.GetSalesReturnBillWiseCompanyListDB(SalesReturnBillwiseReportActivity.getsaleslistdate,
                    SalesReturnBillwiseReportActivity.getfiltercompanycode,
                    SalesReturnBillwiseReportActivity.getpaymenttype,SalesReturnBillwiseReportActivity.getpaymentstatus);*/

            Cursor mCur1 = objcustomerAdaptor.Getsalesordercompanydetails(
                    SalesOrderBillwiseReportActivity.getbillfromdate,
                    SalesOrderBillwiseReportActivity.getbilltodate);

            if (mCur1.getCount() > 0) {

                for (int j = 0; j < mCur1.getCount(); j++) {

                    Cursor mCur = objcustomerAdaptor.GetSalesOrderBillListDB(SalesOrderBillwiseReportActivity.getsaleslistdate);

                    if (mCur.getCount() > 0){

                        Cursor mCurSchedule = objcustomerAdaptor.GetBillScheduleDetails(mCur.getString(11));

                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_A);

                        mPrinter.addText(mCur1.getString(1) + "\n");
                        mPrinter.addText(mCur1.getString(8) + "\n");

                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_A);

                        mPrinter.addText(" GSTIN:" + mCur1.getString(7) + "\n");

                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_B);
                        mPrinter.addText("Date : " + SalesOrderBillwiseReportActivity.salesdate.getText().toString() + " " + preferenceMangr.pref_getString("getvanname") + "\n");

                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addText("Route : " + mCurSchedule.getString(0) + "\n");
                        mPrinter.addText("Vehicle : " + mCurSchedule.getString(1) + "\n");
                        mPrinter.addText("Salesman : " + mCurSchedule.getString(2) + "\n");
                        mPrinter.addText("Driver : " + mCurSchedule.getString(3) + "\n");

                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addTextAlign(Printer.ALIGN_LEFT);

                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                        String line_space6 = "--------------------------------";
                        mPrinter.addText(line_space6);

                        mPrinter.addTextFont(Printer.FONT_C);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addText(" Order No.        Bill Type        Amount" + "\n");

                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        String line_space1 = "--------------------------------";
                        mPrinter.addText(line_space1);

                        double gettotal = 0;
                        double cnt = 0.0;
                        for (int i = 0; i < mCur.getCount(); i++) {

                            String getcustomername = mCur.getString(21) + " - " + mCur.getString(22);

                            mPrinter.addTextFont(Printer.FONT_B);
                            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                            mPrinter.addText(getcustomername + "\n");

                            mPrinter.addTextFont(Printer.FONT_C);
                            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);

                            mPrinter.addTextFont(Printer.FONT_B);
                            String SalesDetails = "";
                            StringBuffer SalesBuffer = new StringBuffer(100);
                            SalesBuffer.append(SalesDetails);
                            String getbilltype = "";
                            if (mCur.getString(9).equals("1")) {
                                getbilltype = "CASH";
                            } else {
                                getbilltype = "CREDIT";
                            }
                            SalesBuffer.append(Util.nameLeftValueRightJustifysalesbill(mCur.getString(3),
                                    " ",
                                    String.format("%.2f", mCur.getFloat(14)),
                                    32));
                            SalesDetails = SalesBuffer.toString();
                            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                            mPrinter.addText(SalesDetails + "\n");

                            mPrinter.addTextFont(Printer.FONT_C);
                            String SalesDetails1 = "";
                            StringBuffer SalesBuffer1 = new StringBuffer(100);
                            SalesBuffer1.append(SalesDetails1);

                            SalesBuffer1.append(Util.nameLeftValueRightJustifysalesbill("Order.No. " + mCur.getString(18),
                                    "",
                                    "",
                                    32));
                            SalesDetails1 = SalesBuffer1.toString();
                            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                            mPrinter.addText(SalesDetails1 + "\n");

                            gettotal = gettotal + mCur.getFloat(14);
                            mCur.moveToNext();
                        }

                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                        String line_space3 = "------------------------------------------\n";
                        mPrinter.addText(line_space3);

                        String gettotalamt = dft.format(gettotal);
                        mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addText(mCur.getCount() + "      Total " + " : " + Util.rightJustify(gettotalamt, 11) + "\n");

                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                        String line_space4 = "--------------------------------\n";
                        mPrinter.addText(line_space4);

                        mPrinter.addTextFont(Printer.FONT_C);
                        mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                        String Gencode= GenCreatedDate();
                        String retailerdetails =  Gencode +"\n";
                        mPrinter.addText(retailerdetails);

                        mPrinter.addTextFont(Printer.FONT_C);
                        mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        String line_space13 = "-----------------------------------------\n";
                        mPrinter.addText(line_space13);

                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        String poweredby ="Powered by www.shivasoftwares.com\n\n";
                        mPrinter.addText(poweredby);

                        String emptylines1 = "\n\n\n";
                        mPrinter.addText(emptylines1);

                        try {

                            if(!connectPrinter()) {
                                mPrinter.clearCommandBuffer();
                                return false;
                            }

                            PrinterStatusInfo status = mPrinter.getStatus();

                            Log.d("STATUS", Integer.toString(status.getConnection()));
                            Log.d("ONLINE", Integer.toString(status.getOnline()));

                            if ((status.getConnection() == 1) && (status.getOnline() == 1)) {
                                try {
                                    mPrinter.sendData(Printer.PARAM_DEFAULT);
                                    billPrinted = true;
                                } catch (Epos2Exception e) {
                                    Log.d("bbb", e.getLocalizedMessage());
                                    Log.d("PRINT", "failed to send data");
                                    mPrinter.clearCommandBuffer();
                                    try {
                                        mPrinter.disconnect();
                                    } catch (Exception ex) {
                                        // Do nothing
                                    }
                                }
                            }
                        }catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    else{
                        Log.d("nodata","nodata");
                        //Toast.makeText(mContext,"No data Available",Toast.LENGTH_SHORT).show();
                        objActivity.runOnUiThread(new Runnable() {
                            public void run() {
                                Log.d("thread","thread");
                                Toast.makeText(mContext, "No data Available", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }else{
                Log.d("no company data","no company data");
                //Toast.makeText(mContext,"No data Available",Toast.LENGTH_SHORT).show();
                objActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d("thread","thread");
                        Toast.makeText(mContext, "No Company data Available", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Epos2Exception e) {
            e.printStackTrace();
            Log.d("PrintData : GetSalesOrderBillWiseReportPrint",e.getLocalizedMessage());
        }
        return billPrinted;
    }

    //SalesOrder Item wise REport
    @SuppressLint("LongLogTag")
    public boolean GetSalesOrderItemreportBillPrint(Activity objActivity) {

        if (mPrinter == null) {
            initializePrinter(objActivity.getApplicationContext());
        }

        if (mPrinter == null)
            return false;

        //Printer printer = null;
        boolean billPrinted = false;
        String printheader ="";
        try{

            DataBaseAdapter objcustomerAdaptor = new DataBaseAdapter(mContext);
            objcustomerAdaptor.open();

            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
            mPrinter.addTextFont(Printer.FONT_A);

            /*String startmsg = "       உ        \n";
            printer.addText(startmsg);
            String startmsgline = "       --        \n";
            printer.addText(startmsgline);*/

            printheader=objcustomerAdaptor.getprintheader();

            if(!printheader.equals("")&&!printheader.equals(null)&&!printheader.equals("null")){

                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                String startmsg = printheader+"\n";
                mPrinter.addText(startmsg);
                String startmsgline = "       --        \n";
                mPrinter.addText(startmsgline);
            }

            mPrinter.addText("Order Item-wise Report \n");

            Cursor mCur1 = objcustomerAdaptor.Getsalesordercompanydetails(
                    SalesOrderItemWiseActivity.getbillfromdate,
                    SalesOrderItemWiseActivity.getbilltodate);

            if (mCur1.getCount() > 0) {

                for (int j = 0; j < mCur1.getCount(); j++) {

                    Cursor mCur = objcustomerAdaptor.GetSalesOrderItemCompanyWiseReport_Date(
                            SalesOrderItemWiseActivity.getbillfromdate, mCur1.getString(0));

                    if (mCur.getCount() > 0){

                        Cursor mCurSchedule = objcustomerAdaptor.GetBillScheduleDetails(mCur1.getString(12));

                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_A);

                        mPrinter.addText(mCur1.getString(1) + "\n");
                        mPrinter.addText(mCur1.getString(8) + "\n");

                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_A);

                        mPrinter.addText(" GSTIN:" + mCur1.getString(7) + "\n");


                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_B);
                        mPrinter.addText("Date : " + SalesOrderItemWiseActivity.salesreturnitemfilterdate.getText().toString() + " " + preferenceMangr.pref_getString("getvanname") + "\n");

                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addText("Route : " + mCurSchedule.getString(0) + "\n");
                        mPrinter.addText("Vehicle : " + mCurSchedule.getString(1) + "\n");
                        mPrinter.addText("Salesman : " + mCurSchedule.getString(2) + "\n");
                        mPrinter.addText("Driver : " + mCurSchedule.getString(3) + "\n");

                        mPrinter.addTextAlign(Printer.ALIGN_LEFT);

                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                        String line_space6 = "--------------------------------";
                        mPrinter.addText(line_space6);

                        mPrinter.addTextFont(Printer.FONT_C);

                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addText("Item Name    Qty       Unit         Amount" + "\n");

                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        String line_space1 = "--------------------------------";
                        mPrinter.addText(line_space1);

                        double gettotal = 0;
                        double cnt = 0.0;
                        for (int i = 0; i < mCur.getCount(); i++) {
                            String Product = (mCur.getString(1));

                            mPrinter.addTextFont(Printer.FONT_B);
                            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                            mPrinter.addText(Product + "\n");

                            mPrinter.addTextFont(Printer.FONT_A);
                            String SalesDetails = "";
                            StringBuffer SalesBuffer = new StringBuffer(100);
                            SalesBuffer.append(SalesDetails);


                            SalesBuffer.append(Util.nameLeftValueRightJustify(Integer.toString(mCur.getInt(2)),
                                    mCur.getString(3), String.format("%.2f", mCur.getFloat(4)),
                                    32));
                            SalesDetails = SalesBuffer.toString();
                            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                            mPrinter.addText(SalesDetails + "\n");


                            cnt = cnt + mCur.getFloat(4);
                            gettotal = gettotal + Double.parseDouble(String.valueOf(mCur.getDouble(4)));
                            mCur.moveToNext();
                        }

                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                        String line_space3 = "--------------------------------\n";
                        mPrinter.addText(line_space3);

                        String subtotal = "";
                        String gettotalamt = dft.format(gettotal);

                        mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addText(mCur.getCount()+"    Total " + " : " + Util.rightJustify(gettotalamt, 11) + "\n");

                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                        String line_space4 = "--------------------------------\n";
                        mPrinter.addText(line_space4);
                        mCur1.moveToNext();
                    }
                }

                mPrinter.addTextFont(Printer.FONT_C);
                mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                String Gencode= GenCreatedDate();
                String retailerdetails =  Gencode +"\n";
                mPrinter.addText(retailerdetails);

                mPrinter.addTextFont(Printer.FONT_C);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                String line_space13 = "-----------------------------------------\n";
                mPrinter.addText(line_space13);

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                String poweredby ="Powered by www.shivasoftwares.com\n\n";
                mPrinter.addText(poweredby);

                String emptylines1 = "\n\n\n";
                mPrinter.addText(emptylines1);

                try {

                    if(!connectPrinter()) {
                        mPrinter.clearCommandBuffer();
                        return false;
                    }

                    PrinterStatusInfo status = mPrinter.getStatus();

                    Log.d("STATUS", Integer.toString(status.getConnection()));
                    Log.d("ONLINE", Integer.toString(status.getOnline()));

                    if ((status.getConnection() == 1) && (status.getOnline() == 1)) {
                        try {
                            mPrinter.sendData(Printer.PARAM_DEFAULT);
                            billPrinted = true;
                        } catch (Epos2Exception e) {
                            Log.d("PRINT", "failed to send data");
                            mPrinter.clearCommandBuffer();
                            try {
                                mPrinter.disconnect();
                            } catch (Exception ex) {
                                // Do nothing
                            }
                        }
                    }

                }catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            else{
                Log.d("nodata","nodata");
                //Toast.makeText(mContext,"No data Available",Toast.LENGTH_SHORT).show();
                objActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d("thread","thread");
                        Toast.makeText(mContext, "No data Available", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch (Epos2Exception e) {
            e.printStackTrace();
            Log.d("PrintData : GetSalesOrderItemreportBillPrint", e.getLocalizedMessage());
        }
        return billPrinted;
    }

    //Cash Summary report
    @SuppressLint("LongLogTag")
    public boolean GetCashSummaryReportPrintBill(Activity objActivity) {

        if (mPrinter == null) {
            initializePrinter(objActivity.getApplicationContext());
        }

        if (mPrinter == null)
            return false;

        //Printer printer = null;
        boolean billPrinted = false;
        String printheader ="";
        try{

            DataBaseAdapter mDbHelper = new DataBaseAdapter(mContext);
            mDbHelper.open();
            Cursor mCur = mDbHelper.GetCompanyDetails();
            Cursor mCur1 = mDbHelper.GetScheduleSummaryDetails(CashSummaryActivity.summaryschedulecode);

            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
            mPrinter.addTextFont(Printer.FONT_A);

            /*String startmsg = "       உ        \n";
            printer.addText(startmsg);
            String startmsgline = "       --        \n";
            printer.addText(startmsgline);*/

            printheader=mDbHelper.getprintheader();

            if(!printheader.equals("")&&!printheader.equals(null)&&!printheader.equals("null")){

                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                String startmsg = printheader+"\n";
                mPrinter.addText(startmsg);
                String startmsgline = "       --        \n";
                mPrinter.addText(startmsgline);
            }

            mPrinter.addText("Cash Detail Report \n");
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
            mPrinter.addTextFont(Printer.FONT_B);

            //  printer.addText(" Date :" + LoginActivity.getcurrentdatetime + "\n");

            if (mCur.getCount() > 0) {
                for (int j = 0; j < mCur.getCount(); j++) {
                    mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_A);

                    mPrinter.addText(mCur.getString(1) + " "+mCur.getString(4)+"\n");
                    mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_A);

                    mPrinter.addText(" GSTIN:" + mCur.getString(8) + "\n");
                    mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_B);

                    //printer.addText(mCur.getString(4) + "\n");
                    mPrinter.addText(" Date :" + preferenceMangr.pref_getString("getcurrentdatetime") + " "+ preferenceMangr.pref_getString("getvanname") + "\n");


                    /*   printer.addText(" Ph: " + mCur.getString(9) + "\n");*/

              /*


                    printer.addTextAlign(Printer.ALIGN_CENTER);
                    printer.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    printer.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    printer.addTextFont(Printer.FONT_B);
                    printer.addText(" PAN:" + mCur.getString(10) + "          " +
                            LoginActivity.getvanname + "\n");*/

                    mPrinter.addTextAlign(Printer.ALIGN_CENTER) ;

                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_B);
                    mPrinter.addText("Route : " + MenuActivity.getroutenametamil + "\n");

                    mPrinter.addText("Vechicle : " + mCur1.getString(0) +  "\n");
                    mPrinter.addText("Salesman : " +  mCur1.getString(1) + "\n");
                    mPrinter.addText("Driver : " + mCur1.getString(2) +"\n");

                    String linespace1 = "------------------------------------------\n";
                    String line_space = "------------------------------------------\n";
                    String smalllinesapce = "--------------------------------\n";
                    mPrinter.addText(linespace1);
                    Cursor mCurSales = mDbHelper.GetSalesSummaryDetails(CashSummaryActivity.summaryschedulecode,
                            mCur.getString(0));
                    if(mCurSales.getCount()>0) {
                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                        mPrinter.addText("Cash Sales Details \n");
                        mPrinter.addText(smalllinesapce);
                        mPrinter.addTextFont(Printer.FONT_B);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addText("Customer Name" + "\n");
                        mPrinter.addText("Inv No.     Cash Amt     UPI Amt   Inv Amt"+ "\n");
                        mPrinter.addText(line_space);

                        String gettotalbill = "0";
                        double getnetamt =0;
                        double getcashamt =0;
                        double getupiamt =0;
                        double getcashnotpaidamt =0;
                        double gettotalamt  = 0;


                        for (int k = 0; k < mCurSales.getCount(); k++) {
                            mPrinter.addTextFont(Printer.FONT_B);

                            if (mCurSales.getString(3).equals("no")) {
                                getcashnotpaidamt = getcashnotpaidamt + mCurSales.getFloat(2);
                                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                            } else {
                                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                            }
                            mPrinter.addTextFont(Printer.FONT_B);

//                            String SalesCusDetails = "";
//                            StringBuffer SalesCusBuffer = new StringBuffer(100);
//                            SalesCusBuffer.append(SalesCusDetails);
//
//                            SalesCusBuffer.append(Util.nameLeftValueRightJustifysalessummary(mCurSales.getString(1),
//                                    "",
//                                    String.format("%.2f", mCurSales.getFloat(2)),
//                                    32));
//                            SalesCusDetails = SalesCusBuffer.toString();
//                            printer.addTextAlign(Printer.ALIGN_LEFT);
//                            printer.addText(  SalesCusDetails +  "\n");

                            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                            mPrinter.addText(  mCurSales.getString(1)+  "\n");

                            String SalesDetails = "";
                            StringBuffer SalesBuffer = new StringBuffer(100);
                            SalesBuffer.append(SalesDetails);

//                            SalesBuffer.append(Util.nameLeftValueRightJustifysalessummary("",
//                                    mCurSales.getString(0),
//                                    String.format("%.2f", mCurSales.getFloat(2)),
//                                    32));
                            SalesBuffer.append(Util.nameLeftValueRightJustifysalesamountsummary(mCurSales.getString(0),
                                    String.format("%.2f", mCurSales.getFloat(4)),
                                    String.format("%.2f", mCurSales.getFloat(5)),
                                    String.format("%.2f", mCurSales.getFloat(2)),
                                    32));

                            SalesDetails = SalesBuffer.toString();
                            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                            mPrinter.addText(SalesDetails + "\n");

                            getnetamt = getnetamt + mCurSales.getFloat(2);
                            getcashamt = getcashamt + mCurSales.getFloat(4);
                            getupiamt = getupiamt + mCurSales.getFloat(5);
                            mCurSales.moveToNext();
                        }
                        gettotalbill = String.valueOf(mCurSales.getCount());
                        gettotalamt = getnetamt - getcashnotpaidamt - getupiamt;

                        mPrinter.addText(line_space);

                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_C);

                        String SalesDetails1 = "";
                        StringBuffer SalesBuffer1 = new StringBuffer(100);
                        SalesBuffer1.append(SalesDetails1);

                        SalesBuffer1.append(Util.nameLeftValueRightJustifysalessummary(gettotalbill,
                                "Nett Amount : ",
                                String.format("%.2f", getnetamt),
                                32));

                        SalesDetails1 = SalesBuffer1.toString();
                        mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                        mPrinter.addText(SalesDetails1 + "\n");

                        String SalesDetails2 = "";
                        StringBuffer SalesBuffer2 = new StringBuffer(100);
                        SalesBuffer2.append(SalesDetails2);

                        SalesBuffer2.append(Util.nameLeftValueRightJustifynotreceived("",
                                "Cash Not Received : ",
                                String.format("%.2f", getcashnotpaidamt),
                                32));

                        if(getcashnotpaidamt > 0 ) {
                            SalesDetails2 = SalesBuffer2.toString();
                            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                            mPrinter.addText(SalesDetails2 + "\n");
                        }

                        String SalesDetails3 = "";
                        StringBuffer SalesBuffer3 = new StringBuffer(100);
                        SalesBuffer3.append(SalesDetails3);

                        SalesBuffer3.append(Util.nameLeftValueRightJustifysalessummary("",
                                "UPI Receipt : ",
                                String.format("%.2f", getupiamt),
                                32));

                        SalesDetails3 = SalesBuffer3.toString();
                        mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                        mPrinter.addText(SalesDetails3 + "\n");

                        mPrinter.addText(line_space);

                        mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_B);
                        mPrinter.addText(" Cash By Sales " + " : " + Util.rightJustify(String.format("%.2f", gettotalamt), 8) + "\n");
                        mPrinter.addText(line_space);
                        String emptyline = "\n\n";
                        mPrinter.addText(emptyline);
                    }

                    Cursor mCurSalesreturn = mDbHelper.GetSalesReturnSummaryDetails(CashSummaryActivity.summaryschedulecode,
                            mCur.getString(0));
                    if(mCurSalesreturn.getCount()>0) {
                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addText("Cash Sales Return Details \n");
                        mPrinter.addText(smalllinesapce);
                        mPrinter.addTextFont(Printer.FONT_B);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addText("Customer Name      Inv No.      Inv Amt" + "\n");
                        mPrinter.addText(line_space);

                        String getreturntotalbill = "0";
                        double getreturnnetamt =0;

                        for (int k = 0; k < mCurSalesreturn.getCount(); k++) {
                            mPrinter.addTextFont(Printer.FONT_B);
                            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);

                            mPrinter.addTextFont(Printer.FONT_B);
                            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                            mPrinter.addText(  mCurSalesreturn.getString(1)+  "\n");

                            String SalesDetails = "";
                            StringBuffer SalesBuffer = new StringBuffer(100);
                            SalesBuffer.append(SalesDetails);

                            SalesBuffer.append(Util.nameLeftValueRightJustifysalessummary("",
                                    mCurSalesreturn.getString(0),
                                    String.format("%.2f", mCurSalesreturn.getFloat(2)),
                                    32));

                            SalesDetails = SalesBuffer.toString();
                            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                            mPrinter.addText(SalesDetails + "\n");

                            getreturnnetamt = getreturnnetamt + mCurSalesreturn.getFloat(2);

                            mCurSalesreturn.moveToNext();
                        }
                        getreturntotalbill = String.valueOf(mCurSalesreturn.getCount());

                        mPrinter.addText(line_space);

                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_C);

                        String SalesDetails1 = "";
                        StringBuffer SalesBuffer1 = new StringBuffer(100);
                        SalesBuffer1.append(SalesDetails1);

                        SalesBuffer1.append(Util.nameLeftValueRightJustifysalessummary(getreturntotalbill,
                                "Nett Amount : ",
                                String.format("%.2f", getreturnnetamt),
                                32));

                        SalesDetails1 = SalesBuffer1.toString();
                        mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                        mPrinter.addText(SalesDetails1 + "\n");
                        mPrinter.addText(line_space);


                        mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_B);
                        mPrinter.addText(" Sales Return " + " : " + Util.rightJustify(String.format("%.2f", getreturnnetamt), 8) + "\n");
                        mPrinter.addText(linespace1);
                        String emptyline = "\n\n";
                        mPrinter.addText(emptyline);
                    }

                    //Receipt Details
                    Cursor mCurReceipt = mDbHelper.GetReceiptSummaryDetails(CashSummaryActivity.summaryschedulecode,
                            mCur.getString(0));
                    if(mCurReceipt.getCount()>0) {

                        mPrinter.addText(linespace1);
                        mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addText("Receipt Details \n");
                        mPrinter.addText(smalllinesapce);
                        mPrinter.addTextFont(Printer.FONT_B);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addText("Customer Name     R.No.     Receipt Amt" + "\n");
                        mPrinter.addText(line_space);


                        String gettotalreceiptbill = "0";
                        double getreceiptnetamt =0;
                        double getchequeamt =0;
                        double getupiamt =0;
                        double getreceipttotalamt  = 0;

                        for (int k = 0; k < mCurReceipt.getCount(); k++) {

                            mPrinter.addTextFont(Printer.FONT_B);
                            if (mCurReceipt.getString(3).equals("Cash")) {

                                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                            } else if (mCurReceipt.getString(3).equals("UPI")) {
                                getupiamt = getupiamt + mCurReceipt.getFloat(2);
                                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                            } else {
                                getchequeamt = getchequeamt + mCurReceipt.getFloat(2);
                                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                            }

                            mPrinter.addTextFont(Printer.FONT_B);
                            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                            mPrinter.addText(  mCurReceipt.getString(1)+  "\n");


                            String SalesDetails = "";
                            StringBuffer SalesBuffer = new StringBuffer(100);
                            SalesBuffer.append(SalesDetails);

                            SalesBuffer.append(Util.nameLeftValueRightJustifysalessummary(" ",
                                    mCurReceipt.getString(0),
                                    String.format("%.2f", mCurReceipt.getFloat(2)),
                                    32));

                            SalesDetails = SalesBuffer.toString();
                            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                            mPrinter.addText(SalesDetails + "\n");

                            getreceiptnetamt = getreceiptnetamt + mCurReceipt.getFloat(2);

                            mCurReceipt.moveToNext();
                        }
                        gettotalreceiptbill = String.valueOf(mCurReceipt.getCount());
                        getreceipttotalamt = getreceiptnetamt - (getchequeamt + getupiamt);

                        mPrinter.addText(line_space);

                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_C);
                        String SalesDetails1 = "";
                        StringBuffer SalesBuffer1 = new StringBuffer(100);
                        SalesBuffer1.append(SalesDetails1);

                        SalesBuffer1.append(Util.nameLeftValueRightJustifysalessummary(gettotalreceiptbill,
                                "Nett Amount : ",
                                String.format("%.2f", getreceiptnetamt),
                                32));

                        SalesDetails1 = SalesBuffer1.toString();
                        mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                        mPrinter.addText(SalesDetails1 + "\n");


                        String SalesDetails2 = "";
                        StringBuffer SalesBuffer2 = new StringBuffer(100);
                        SalesBuffer2.append(SalesDetails2);

                        SalesBuffer2.append(Util.nameLeftValueRightJustifysalessummary("",
                                "By Cheque-dd : ",
                                String.format("%.2f", getchequeamt),
                                32));
                        if(getchequeamt>0) {

                            SalesDetails2 = SalesBuffer2.toString();
                            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                            mPrinter.addText(SalesDetails2 + "\n");
                        }

                        String SalesDetails3 = "";
                        StringBuffer SalesBuffer3 = new StringBuffer(100);
                        SalesBuffer2.append(SalesDetails3);

                        SalesBuffer3.append(Util.nameLeftValueRightJustifysalessummary("",
                                "By UPI Receipt : ",
                                String.format("%.2f", getupiamt),
                                32));

                        if(getupiamt>0) {

                            SalesDetails2 = SalesBuffer2.toString();
                            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                            mPrinter.addText(SalesBuffer3 + "\n");
                        }

                        mPrinter.addText(line_space);

                        mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                        mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addTextFont(Printer.FONT_B);
                        mPrinter.addText(" Cash By Receipt " + " : " +
                                Util.rightJustify(String.format("%.2f", getreceipttotalamt), 11) + "\n");
                        mPrinter.addText(linespace1);
                        String emptyline = "\n\n";
                        mPrinter.addText(emptyline);
                    }

                    mCur.moveToNext();
                }
            }
            String linespace1 = "------------------------------------------\n";
            String line_space = "--------------------------------\n";
            mPrinter.addText(linespace1);
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
            mPrinter.addTextFont(Printer.FONT_A);
            mPrinter.addText("Cash Summary Report \n");

            mPrinter.addTextFont(Printer.FONT_B);

            mPrinter.addText(linespace1);
            Cursor mCurCompany = mDbHelper.GetCompanyDetails();
            if(mCurCompany.getCount()>0){
                double getalltotal = 0;
                double getallupitotal = 0;
                //String[] getalphabet = {"B","C"};
                String getadvance = mDbHelper.GetSummaryAdvanceAmount(CashSummaryActivity.summaryschedulecode);
                mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_B);
                mPrinter.addText(" Advance (A)" + " : " +
                        Util.rightJustify(String.format("%.2f", Double.parseDouble(getadvance)), 11) + "\n");
                String sumofCompanytext="A";
                for(int p=0;p<mCurCompany.getCount();p++){
                    double gettotal = 0;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String getsalesamount = mDbHelper.GetSummarySalesAmount(CashSummaryActivity.summaryschedulecode,
                            mCurCompany.getString(0));

                    String getreceiptamount = mDbHelper.GetSummaryReceiptAmount(CashSummaryActivity.summaryschedulecode,
                            mCurCompany.getString(0));

                    String getsalesreturnamount = mDbHelper.GetSummarySalesReturnAmount(CashSummaryActivity.summaryschedulecode,
                            mCurCompany.getString(0));

                    String getsalesupiamount = mDbHelper.GetSummarySalesUpiAmount(CashSummaryActivity.summaryschedulecode,
                            mCurCompany.getString(0));

                    String getreceiptupiamount = mDbHelper.GetSummaryReceiptUpiAmount(CashSummaryActivity.summaryschedulecode,
                            mCurCompany.getString(0));

                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addText(mCurCompany.getString(1)+"\n");
                    mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_B);
                    mPrinter.addText(" Cash By Sales  " + " : " +
                            Util.rightJustify(String.format("%.2f", Double.parseDouble(getsalesamount)), 11) + "\n");

                    mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_B);
                    mPrinter.addText(" Cash By Receipt  " + " : " +
                            Util.rightJustify(String.format("%.2f", Double.parseDouble(getreceiptamount)), 11) + "\n");
                    mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_B);

                    mPrinter.addText(" UPI By Sales  " + " : " +
                            Util.rightJustify(String.format("%.2f", Double.parseDouble(getsalesupiamount)), 11) + "\n");

                    mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_B);
                    mPrinter.addText(" UPI By Receipt  " + " : " +
                            Util.rightJustify(String.format("%.2f", Double.parseDouble(getreceiptupiamount)), 11) + "\n");

                    mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_B);
                    mPrinter.addText(" Sales Return  " + " : " +
                            Util.rightJustify("(-) "+String.format("%.2f", Double.parseDouble(getsalesreturnamount)), 11) + "\n");

                    mPrinter.addText(line_space);

                    mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_B);
                    gettotal = (Double.parseDouble(getsalesamount) + Double.parseDouble(getreceiptamount) +
                            Double.parseDouble(getsalesupiamount) +
                            Double.parseDouble(getreceiptupiamount) ) - (Double.parseDouble(getsalesreturnamount));
                    mPrinter.addText(" Total ("+getalphabet[p]+") " + " : " +
                            Util.rightJustify(String.format("%.2f", gettotal), 11) + "\n");

                    mPrinter.addText(line_space);

                    getallupitotal = getallupitotal + (Double.parseDouble(getsalesupiamount) +
                            Double.parseDouble(getreceiptupiamount));

                    getalltotal = getalltotal + gettotal;

                    sumofCompanytext=sumofCompanytext+"+"+getalphabet[p];
                    mCurCompany.moveToNext();
                }

              /*  printer.addTextAlign(Printer.ALIGN_LEFT);
                printer.addText("Nett Cash (A+B+C) \n");*/
                double getnetcash = Double.parseDouble(getadvance) + getalltotal;
                mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_B);
                mPrinter.addText(" Nett Cash ("+sumofCompanytext+") " + " : " +
                        Util.rightJustify(String.format("%.2f", getnetcash), 11) + "\n");

                String getexpenses = mDbHelper.GetExpenseAmount(CashSummaryActivity.summaryschedulecode);

                mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_B);
                mPrinter.addText(" Expenses " + " : " +
                        Util.rightJustify("(-) "+String.format("%.2f", Double.parseDouble(getexpenses)), 11) + "\n");


                mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_B);
                mPrinter.addText(" UPI Receipt " + " : " +
                        Util.rightJustify("(-) "+String.format("%.2f", getallupitotal), 11) + "\n");

                mPrinter.addText(line_space);


                double getcashinhand = (getalltotal+Double.parseDouble(getadvance)) -
                        (Double.parseDouble(getexpenses) + getallupitotal);

                mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_B);
                mPrinter.addText(" Cash in Hand " +
                        Util.rightJustify(String.format("%.2f", getcashinhand), 11) + "\n");
                mPrinter.addText(linespace1);
            }

            mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
            mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
            mPrinter.addTextFont(Printer.FONT_C);
            mPrinter.addText(  GenCreatedDate() +"\n");
            mPrinter.addText(linespace1);
            mPrinter.addTextFont(Printer.FONT_C);
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
            String poweredby ="Powered by www.shivasoftwares.com\n";
            mPrinter.addText(poweredby);

            mPrinter.addText("\n\n\n");

            if(!connectPrinter()) {
                mPrinter.clearCommandBuffer();
                return false;
            }

            PrinterStatusInfo status = mPrinter.getStatus();

            Log.d("STATUS", Integer.toString(status.getConnection()));
            Log.d("ONLINE", Integer.toString(status.getOnline()));

            if ((status.getConnection() == 1) && (status.getOnline() == 1)) {
                try {
                    mPrinter.sendData(Printer.PARAM_DEFAULT);
                    billPrinted = true;
                } catch (Epos2Exception e) {
                    Log.d("PRINT", "failed to send data");
                    mPrinter.clearCommandBuffer();
                    try {
                        mPrinter.disconnect();
                    } catch (Exception ex) {
                        // Do nothing
                    }
                }
            }
        }
        catch (Epos2Exception e) {
            Log.d("PrintData : GetCashSummaryReportPrintBill",e.getLocalizedMessage());
        }

        return billPrinted;
    }

    //SeparateCash Summary report
    @SuppressLint("LongLogTag")
    public boolean  CashSummaryReportPrintBill(Activity objActivity) {

        if (mPrinter == null) {
            initializePrinter(objActivity.getApplicationContext());
        }

        if (mPrinter == null)
            return false;

        //Printer printer = null;
        boolean billPrinted = false;
        String printheader ="";
        try{

            DataBaseAdapter mDbHelper = new DataBaseAdapter(mContext);
            mDbHelper.open();

            /*printer.addTextAlign(Printer.ALIGN_CENTER);
            printer.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
            printer.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
            printer.addTextFont(Printer.FONT_A);
            String startmsg = "       உ        \n";
            printer.addText(startmsg);
            String startmsgline = "       --        \n";
            printer.addText(startmsgline);


            String linespace1 = "------------------------------------------\n";
            String line_space = "--------------------------------\n";
            printer.addTextAlign(Printer.ALIGN_CENTER);
            printer.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
            printer.addTextFont(Printer.FONT_A);
            printer.addText("Cash Summary Report \n");

            printer.addTextFont(Printer.FONT_B);

            printer.addText(linespace1);*/
            Cursor mCur = mDbHelper.GetCompanyDetails();
            Cursor mCur1 = mDbHelper.GetScheduleSummaryDetails(CashSummaryActivity.summaryschedulecode);

            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
            mPrinter.addTextFont(Printer.FONT_A);

            /*String startmsg = "       உ        \n";
            printer.addText(startmsg);
            String startmsgline = "       --        \n";
            printer.addText(startmsgline);*/

            printheader=mDbHelper.getprintheader();

            if(!printheader.equals("")&&!printheader.equals(null)&&!printheader.equals("null")){

                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                String startmsg = printheader+"\n";
                mPrinter.addText(startmsg);
                String startmsgline = "       --        \n";
                mPrinter.addText(startmsgline);
            }

            mPrinter.addText("Cash Summary Report \n");
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
            mPrinter.addTextFont(Printer.FONT_B);

            //  printer.addText(" Date :" + LoginActivity.getcurrentdatetime + "\n");

            if (mCur.getCount() > 0) {
                for (int j = 0; j < 1; j++) {
                    mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_A);


                    //printer.addText(mCur.getString(4) + "\n");
                    //printer.addText(" Date :" + LoginActivity.getcurrentdatetime + " " + LoginActivity.getvanname + "\n");
                    mPrinter.addText(" Date :" + preferenceMangr.pref_getString("getcurrentdatetime") + " "+ preferenceMangr.pref_getString("getvanname") + "\n");


                    /*   printer.addText(" Ph: " + mCur.getString(9) + "\n");*/

              /*


                    printer.addTextAlign(Printer.ALIGN_CENTER);
                    printer.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    printer.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    printer.addTextFont(Printer.FONT_B);
                    printer.addText(" PAN:" + mCur.getString(10) + "          " +
                            LoginActivity.getvanname + "\n");*/

                    mPrinter.addTextAlign(Printer.ALIGN_CENTER);

                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_B);
                    mPrinter.addText("Route : " + MenuActivity.getroutenametamil + "\n");

                    mPrinter.addText("Vechicle : " + mCur1.getString(0) + "\n");
                    mPrinter.addText("Salesman : " + mCur1.getString(1) + "\n");
                    mPrinter.addText("Driver : " + mCur1.getString(2) + "\n");
                }
            }
            String linespace1 = "------------------------------------------\n";
            String line_space = "--------------------------------\n";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
            mPrinter.addTextFont(Printer.FONT_A);
            mPrinter.addText(line_space);
            Cursor mCurCompany = mDbHelper.GetCompanyDetails();
            if(mCurCompany.getCount()>0){
                double getalltotal = 0;
                double getallupitotal = 0;
                //String[] getalphabet = {"B","C"};
                String getadvance = mDbHelper.GetSummaryAdvanceAmount(CashSummaryActivity.summaryschedulecode);
                mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_B);
                mPrinter.addText(" Advance (A)" + " : " +
                        Util.rightJustify(String.format("%.2f", Double.parseDouble(getadvance)), 11) + "\n");
                String sumofCompanytext="A";
                for(int p=0;p<mCurCompany.getCount();p++){
                    double gettotal = 0;

                    String getsalesamount = mDbHelper.GetSummarySalesAmount(CashSummaryActivity.summaryschedulecode,
                            mCurCompany.getString(0));

                    String getreceiptamount = mDbHelper.GetSummaryReceiptAmount(CashSummaryActivity.summaryschedulecode,
                            mCurCompany.getString(0));

                    String getsalesreturnamount = mDbHelper.GetSummarySalesReturnAmount(CashSummaryActivity.summaryschedulecode,
                            mCurCompany.getString(0));

                    String getsalesupiamount = mDbHelper.GetSummarySalesUpiAmount(CashSummaryActivity.summaryschedulecode,
                            mCurCompany.getString(0));

                    String getreceiptupiamount = mDbHelper.GetSummaryReceiptUpiAmount(CashSummaryActivity.summaryschedulecode,
                            mCurCompany.getString(0));

                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addText(mCurCompany.getString(1)+"\n");
                    mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_B);
                    mPrinter.addText(" Cash By Sales  " + " : " +
                            Util.rightJustify(String.format("%.2f", Double.parseDouble(getsalesamount)), 11) + "\n");

                    mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_B);
                    mPrinter.addText(" Cash By Receipt  " + " : " +
                            Util.rightJustify(String.format("%.2f", Double.parseDouble(getreceiptamount)), 11) + "\n");

                    mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_B);
                    mPrinter.addText(" UPI By Sales  " + " : " +
                            Util.rightJustify(String.format("%.2f", Double.parseDouble(getsalesupiamount)), 11) + "\n");

                    mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_B);
                    mPrinter.addText(" UPI By Receipt  " + " : " +
                            Util.rightJustify(String.format("%.2f", Double.parseDouble(getreceiptupiamount)), 11) + "\n");

                    mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_B);
                    mPrinter.addText(" Sales Return  " + " : " +
                            Util.rightJustify("(-) "+String.format("%.2f", Double.parseDouble(getsalesreturnamount)), 11) + "\n");

                    mPrinter.addText(line_space);

                    mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_B);
                    gettotal = (Double.parseDouble(getsalesamount) + Double.parseDouble(getreceiptamount) +
                            Double.parseDouble(getsalesupiamount) +
                            Double.parseDouble(getreceiptupiamount)
                    ) - (Double.parseDouble(getsalesreturnamount));
                    mPrinter.addText(" Total ("+getalphabet[p]+") " + " : " +
                            Util.rightJustify(String.format("%.2f", gettotal), 11) + "\n");

                    mPrinter.addText(line_space);
                    getallupitotal = getallupitotal + (Double.parseDouble(getsalesupiamount) +
                            Double.parseDouble(getreceiptupiamount));
                    getalltotal = getalltotal + gettotal;
                    sumofCompanytext=sumofCompanytext+"+"+getalphabet[p];
                    mCurCompany.moveToNext();
                }

                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                double getnetcash = Double.parseDouble(getadvance) + getalltotal;

                /*printer.addText("Nett Cash (A+B+C) \n");*/
                mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_B);
                mPrinter.addText(" Nett Cash ("+sumofCompanytext+") " + " : " +
                        Util.rightJustify(String.format("%.2f", getnetcash), 11) + "\n");

                String getexpenses = mDbHelper.GetExpenseAmount(CashSummaryActivity.summaryschedulecode);

                mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_B);
                mPrinter.addText(" Expenses " + " : " +
                        Util.rightJustify("(-) "+String.format("%.2f", Double.parseDouble(getexpenses)), 11) + "\n");

                mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_B);
                mPrinter.addText(" UPI Receipt " + " : " +
                        Util.rightJustify("(-) "+String.format("%.2f", getallupitotal), 11) + "\n");

                mPrinter.addText(line_space);

                double getcashinhand = (getalltotal+Double.parseDouble(getadvance)) -
                        (Double.parseDouble(getexpenses) + getallupitotal);

                mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_B);
                mPrinter.addText(" Cash in Hand " +
                        Util.rightJustify(String.format("%.2f", getcashinhand), 11) + "\n");
                mPrinter.addText(linespace1);
            }

            mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
            mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
            mPrinter.addTextFont(Printer.FONT_C);
            mPrinter.addText(  GenCreatedDate() +"\n");
            mPrinter.addText(linespace1);
            mPrinter.addTextFont(Printer.FONT_C);
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
            String poweredby ="Powered by www.shivasoftwares.com\n";
            mPrinter.addText(poweredby);

            mPrinter.addText("\n\n\n");

            if(!connectPrinter()) {
                mPrinter.clearCommandBuffer();
                return false;
            }

            PrinterStatusInfo status = mPrinter.getStatus();

            Log.d("STATUS", Integer.toString(status.getConnection()));
            Log.d("ONLINE", Integer.toString(status.getOnline()));

            if ((status.getConnection() == 1) && (status.getOnline() == 1)) {
                try {
                    mPrinter.sendData(Printer.PARAM_DEFAULT);
                    billPrinted = true;
                } catch (Epos2Exception e) {
                    Log.d("bbb", e.getLocalizedMessage());
                    Log.d("PRINT", "failed to send data");
                    mPrinter.clearCommandBuffer();
                    try {
                        mPrinter.disconnect();
                    } catch (Exception ex) {
                        // Do nothing
                    }
                }
            }
        }
        catch (Epos2Exception e) {
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(mContext);
            mDbErrHelper.open();
            String geterrror = e.toString();
            String getfunname = new Object(){}.getClass().getEnclosingMethod().getName();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), getfunname);
            mDbErrHelper.close();
            e.printStackTrace();
            Log.d("PrintData : CashSummaryReportPrintBill",e.getLocalizedMessage());
        }
        return billPrinted;
    }

    //Cash report
    @SuppressLint("LongLogTag")
    public boolean GetCashReportPrintBill(Activity objActivity) {

        if (mPrinter == null) {
            initializePrinter(objActivity.getApplicationContext());
        }

        if (mPrinter == null)
            return false;

        //Printer printer = null;
        boolean billPrinted = false;
        String printheader ="";
        try{

            DataBaseAdapter mDbHelper = new DataBaseAdapter(mContext);
            mDbHelper.open();
            Cursor mCur = mDbHelper.GetSalesScheduleDetails(CashReportActivity.getschedulecode);
            Cursor mCur1 =  mDbHelper.getCompanydetailsforprint();

            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
            mPrinter.addTextFont(Printer.FONT_A);

            /*String startmsg = "       உ        \n";
            printer.addText(startmsg);
            String startmsgline = "       --        \n";
            printer.addText(startmsgline);*/

            printheader=mDbHelper.getprintheader();

            if(!printheader.equals("")&&!printheader.equals(null)&&!printheader.equals("null")){

                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                String startmsg = printheader+"\n";
                mPrinter.addText(startmsg);
                String startmsgline = "       --        \n";
                mPrinter.addText(startmsgline);
            }

            mPrinter.addText("Cash Close Report \n");
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
            mPrinter.addTextFont(Printer.FONT_B);

            String line_space = "------------------------------------------\n";
            String line_space12 = "--------------------------------\n";
            mPrinter.addText(line_space);

            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
            mPrinter.addTextFont(Printer.FONT_B);
            mPrinter.addText("Route : "+ CashReportActivity.getstaticcashroutename+"\n");
            mPrinter.addText("Van   : "+ preferenceMangr.pref_getString("getvanname") +"\n");

            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
            mPrinter.addTextFont(Printer.FONT_A);
            mPrinter.addText(line_space12);

            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
            mPrinter.addTextFont(Printer.FONT_B);

            mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
            mPrinter.addTextFont(Printer.FONT_B);
            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);

           /* double  getsaleamt = (Double.parseDouble(CashReportActivity.getsalescash));
            double  getexpamt = (Double.parseDouble(CashReportActivity.getexpense));
            double  getrecamt = (Double.parseDouble(CashReportActivity.getreceiptcash));
            double  getsalretamt = (Double.parseDouble(CashReportActivity.getsalesreturn));
            double  getadvamt = (Double.parseDouble(CashReportActivity.getadvance));

            printer.addText("Sal  :  "+Util.rightJustify( dft.format(getsaleamt),11) +
                    "Exp  :  "+Util.rightJustify( dft.format(getexpamt),11) +  "\n");

            printer.addText("Rec  :  "+Util.rightJustify( dft.format(getrecamt),11) +
                    "Sa Ret  :  "+Util.rightJustify( dft.format(getsalretamt),11) +  "\n");

            printer.addText("Adv  :  "+Util.rightJustify( dft.format(getadvamt),11) +
                    "                     \n");
*/
           /* printer.addText(Util.nameLeftValueRightJustify("Sal  :  "  ,
                    "    ",dft.format(Double.parseDouble(CashReportActivity.getsalescash))
                    ,"    Exp  :  "+dft.format(Double.parseDouble(CashReportActivity.getexpense))+"\n",32));

            printer.addText(Util.nameLeftValueRightJustify("Rec  :  " ,
                    "    ",dft.format(Double.parseDouble(CashReportActivity.getreceiptcash)) ,
                    "    Sa Re :  "+dft.format(Double.parseDouble(CashReportActivity.getsalesreturn))+"\n",32));

            printer.addText(Util.nameLeftValueRightJustify("Adv  :  " ,
                    "    ",dft.format(Double.parseDouble(CashReportActivity.getadvance )),"      \n",
                    32));*/

            String CashDetails1 = "";
            StringBuffer CashBuffer2 = new StringBuffer(100);
            CashBuffer2.append(CashDetails1);


            CashBuffer2.append(Util.nameLeftValueRightJustifycashvalues("Sal : ",
                    dft.format(Double.parseDouble(CashReportActivity.getsalescash))," Exp : " ,
                    dft.format(Double.parseDouble(CashReportActivity.getexpense)),32));
            CashDetails1 = CashBuffer2.toString();
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
            mPrinter.addText(CashDetails1+"\n");

            String CashDetails2 = "";
            StringBuffer CashBuffer1 = new StringBuffer(100);
            CashBuffer1.append(CashDetails2);

            CashBuffer1.append(Util.nameLeftValueRightJustifycashvalues("Rec : ",
                    dft.format(Double.parseDouble(CashReportActivity.getreceiptcash)),"Sal Re : " ,
                    dft.format(Double.parseDouble(CashReportActivity.getsalesreturn)),32));
            CashDetails2 = CashBuffer1.toString();
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
            mPrinter.addText(CashDetails2+"\n");

            String CashDetails3 = "";
            StringBuffer CashBuffer3 = new StringBuffer(100);
            CashBuffer3.append(CashDetails3);

            CashBuffer3.append(Util.nameLeftValueRightJustifycashvalues("Adv : ",
                    dft.format(Double.parseDouble(CashReportActivity.getadvance))," " ,
                    " ",32));
            CashDetails3 = CashBuffer3.toString();
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
            mPrinter.addText(CashDetails3+"\n");

            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
            mPrinter.addTextFont(Printer.FONT_A);
            mPrinter.addText(line_space12);

            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
            mPrinter.addTextFont(Printer.FONT_B);

            mPrinter.addText(Util.nameLeftValueRightJustify("Total :" ,
                    "    ",dft.format(Double.parseDouble(CashReportActivity.gettotalcash)) ,"        "
                            +dft.format(Double.parseDouble(CashReportActivity.gettotalexp))+"\n",32));

            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
            mPrinter.addTextFont(Printer.FONT_A);
            mPrinter.addText(line_space12);

            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
            mPrinter.addTextFont(Printer.FONT_B);

            String totalamt = "";
            StringBuffer Bufferout2 = new StringBuffer(100);
            Bufferout2.append(totalamt);
            mPrinter.addTextFont(Printer.FONT_A);
            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);

          /*  Vector v2out2 = Util.processWrappedText("               Cash in Hand  ", 30);


            Bufferout2.append(v2out2.get(0).toString()+Util.rightJustify( "\u20B9 "+CashReportActivity.getcashinhand+"0", 10) + "\n");
            totalamt = Bufferout2.toString();
            printer.addText(totalamt);*/
            mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
            mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
            mPrinter.addTextFont(Printer.FONT_A);

            double gecashinhand = Double.parseDouble(CashReportActivity.getcashinhand);
            mPrinter.addText("Cash in Hand   "+  "\u20B9 "+Util.rightJustify( dft.format(gecashinhand),11) + "\n");


            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
            mPrinter.addTextFont(Printer.FONT_A);
            mPrinter.addText(line_space12);

            mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
            mPrinter.addTextFont(Printer.FONT_A);
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);


            mPrinter.addTextFont(Printer.FONT_A);
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);

            mPrinter.addText(" Denomination  \n");
            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
            mPrinter.addTextFont(Printer.FONT_B);
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);

            mPrinter.addText(line_space);

            mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
            mPrinter.addTextFont(Printer.FONT_B);
            //denomination details
            for (int i = 0; i < CashReportActivity.TotalValues.length; i++) {
                if(!(CashReportActivity.TotalValues[i]).equals("0.00") && !(CashReportActivity.TotalValues[i]).equals("")
                        && !(CashReportActivity.TotalValues[i]).equals("0") ) {

                    mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                    mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                    mPrinter.addTextFont(Printer.FONT_B);

                    double getcashtotal = Double.parseDouble(CashReportActivity.TotalValues[i]);
                  /*  printer.addText("\u20B9 "+CashReportActivity.Values[i]+"X"+CashReportActivity.QtyValues[i]+"" +
                            "                     "+  Util.rightJustify( dft.format(getcashtotal),11) + "\n");
*/
                    String CashDetails = "";
                    StringBuffer CashBuffer = new StringBuffer(100);
                    CashBuffer.append(CashDetails);


                    CashBuffer.append(Util.nameLeftValueRightJustifycash("\u20B9 "+CashReportActivity.Values[i]+" X "
                            ,CashReportActivity.QtyValues[i],
                            String.valueOf(dft.format(getcashtotal)),
                            32));
                    CashDetails = CashBuffer.toString();
                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addText(CashDetails+"\n");

                   /* printer.addText(Util.nameLeftValueRightJustify_tot("\u20B9 "+CashReportActivity.Values[i],"X"+CashReportActivity.QtyValues[i],"     " ,
                            CashReportActivity.TotalValues[i]+"\n",32));*/
                 /*   printer.addText(Util.nameLeftValueRightJustify("\u20B9 "+CashReportActivity.Values[i] ,
                            "X",CashReportActivity.QtyValues[i]+"        ","          "+CashReportActivity.TotalValues[i]+"\n",32));*/

                }
            }
            mPrinter.addTextFont(Printer.FONT_B);
            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);

            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            mPrinter.addTextFont(Printer.FONT_A);
            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);

            mPrinter.addText(line_space12);

            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            mPrinter.addTextFont(Printer.FONT_B);
            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);

           /* printer.addText(Util.nameLeftValueRightJustify_tot("","","Total     " ,
                    CashReportActivity.getsubtotalval+"\n",32));*/
            mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
            mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
            mPrinter.addTextFont(Printer.FONT_B);

            mPrinter.addText("Total  "+  Util.rightJustify( CashReportActivity.getsubtotalval,11) + "\n");

            mPrinter.addTextFont(Printer.FONT_A);
            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
            mPrinter.addText(line_space12);
            // bill count details
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
            mPrinter.addTextStyle(Printer.FALSE, Printer.TRUE, Printer.TRUE, Printer.PARAM_DEFAULT);
            mPrinter.addTextFont(Printer.FONT_B);

            mPrinter.addText("Bill Count Details\n");
            Cursor mCur2=null;
            if (mCur1.getCount() > 0) {
                int netbillcount=0;
                double netbillvalue=0;
                int totalcashcount=0;
                int totalcreditcount=0;
                int totalreturncount=0;
                double totalcashvalue=0;
                double totalcreditvalue=0;
                double totalreturnvalue=0;
                for (int i = 0; i < mCur1.getCount(); i++) {
                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addText(line_space12);

                    mPrinter.addTextFont(Printer.FONT_C);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    int cl=14-mCur1.getString(2).length();
                    String cn=mCur1.getString(2);
                    for(int k=0;k<cl;k++){
                        cn=cn+" ";
                    }
                    mPrinter.addText(cn+"Count        Value" + "\n");

                    mPrinter.addTextFont(Printer.FONT_A);
                    mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                    mPrinter.addText(line_space12);
                    try{
                        mCur2 =  mDbHelper.getbillcountdetailsforprint(CashReportActivity.getschedulecode,mCur1.getString(0));
                        if (mCur2.getCount() > 0) {
                            int billcount=0;
                            double billvalue= 0;

                            for (int j = 0; j < mCur2.getCount(); j++) {
                                billcount=billcount+mCur2.getInt(1);
                                billvalue=billvalue+mCur2.getFloat(2);


                                if(mCur2.getString(0).equals("Cash")){
                                    totalcashcount=totalcashcount+mCur2.getInt(1);
                                    totalcashvalue=totalcashvalue+mCur2.getFloat(2);
                                }
                                if(mCur2.getString(0).equals("Credit")){
                                    totalcreditcount=totalcreditcount+mCur2.getInt(1);
                                    totalcreditvalue=totalcreditvalue+mCur2.getInt(2);
                                }
                                if(mCur2.getString(0).equals("Sales Return")){
                                    totalreturncount=totalreturncount+mCur2.getInt(1);
                                    totalreturnvalue=totalreturnvalue+mCur2.getInt(2);
                                }
                                mPrinter.addTextFont(Printer.FONT_B);
                                String Billcountdetails = "";
                                StringBuffer BillBuffer = new StringBuffer(100);
//                                BillBuffer.append(Billcountdetails);


                                BillBuffer.append(Util.nameLeftValueRightJustifybottomsalesv1(mCur2.getString(0),String.valueOf( mCur2.getInt(1)),
                                        String.format("%.2f", mCur2.getFloat(2)),
                                        32));
                                Billcountdetails = BillBuffer.toString();
                                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                                mPrinter.addText(Billcountdetails + "\n");
                                //net1 = net1+mCurDetails.getFloat(2);
                                mCur2.moveToNext();


                            }
                            mPrinter.addTextFont(Printer.FONT_A);
                            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                            mPrinter.addText(line_space12);

                            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                            mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                            mPrinter.addTextFont(Printer.FONT_B);
                            netbillcount=netbillcount+billcount;
                            netbillvalue=netbillvalue+billvalue;
                            mPrinter.addText(Util.nameLeftValueRightJustifybottomsalesv1("Total", String.valueOf(billcount),
                                    String.format("%.2f", billvalue),32 ));
                        }
                        mPrinter.addTextFont(Printer.FONT_A);
                        mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                        mPrinter.addText("\n"+line_space12);
                        mCur1.moveToNext();
                    }catch (Exception e) {
                        DataBaseAdapter mDbErrHelper = new DataBaseAdapter(mContext);
                        mDbErrHelper.open();
                        String geterrror = e.toString();
                        mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                        mDbErrHelper.close();
                    } finally {
                        if (mCur2 != null)
                            mCur2.close();
                    }
                }
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_B);
                mPrinter.addText(Util.nameLeftValueRightJustifybottomsalesv1("Concern Total", "Count",
                        "Value",32 ) + "\n");

                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addText(line_space12);

                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_B);

                mPrinter.addText(Util.nameLeftValueRightJustifybottomsalesv1("Cash", String.valueOf(totalcashcount),
                        String.format("%.2f", totalcashvalue),32 ) + "\n");
                mPrinter.addText(Util.nameLeftValueRightJustifybottomsalesv1("Credit", String.valueOf(totalcreditcount),
                        String.format("%.2f", totalcreditvalue),32 ) + "\n");
                mPrinter.addText(Util.nameLeftValueRightJustifybottomsalesv1("Sales Return", String.valueOf(totalreturncount),
                        String.format("%.2f", totalreturnvalue),32 ) + "\n");

                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addText(line_space12);

                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextSize(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addTextFont(Printer.FONT_B);
                mPrinter.addText(Util.nameLeftValueRightJustifybottomsalesv1("Grand Total", String.valueOf(netbillcount),
                        String.format("%.2f", netbillvalue),32 ) + "\n");

                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
                mPrinter.addText(line_space12);

                mPrinter.addTextFont(Printer.FONT_A);
                mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.PARAM_DEFAULT);
            }
            mPrinter.addTextFont(Printer.FONT_C);
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
            String gethelp = "";
            if(!mCur.getString(6).equals("")) {
                gethelp = "/" + mCur.getString(6);
            }
            String getdate = GenCreatedDate();
            String scheduledetails = mCur.getString(3) +"/"+ mCur.getString(5) +
                    gethelp+"/"+getdate+"\n";
            mPrinter.addText(scheduledetails);

            mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
            mPrinter.addText(line_space);

            mPrinter.addTextFont(Printer.FONT_C);
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.PARAM_DEFAULT);
            String poweredby ="Powered by www.shivasoftwares.com\n";
            mPrinter.addText(poweredby);

            mPrinter.addText("\n\n\n");

            if(!connectPrinter()) {
                mPrinter.clearCommandBuffer();
                return false;
            }

            PrinterStatusInfo status = mPrinter.getStatus();

            Log.d("STATUS", Integer.toString(status.getConnection()));
            Log.d("ONLINE", Integer.toString(status.getOnline()));

            if ((status.getConnection() == 1) && (status.getOnline() == 1)) {
                try {
                    mPrinter.sendData(Printer.PARAM_DEFAULT);
                    billPrinted = true;
                } catch (Epos2Exception e) {
                    Log.d("bbb", e.getLocalizedMessage());
                    Log.d("PRINT", "failed to send data");
                    mPrinter.clearCommandBuffer();
                    try {
                        mPrinter.disconnect();
                    } catch (Exception ex) {
                        // Do nothing
                    }
                }
            }
        }
        catch (Epos2Exception e) {
            Log.d("PrintData : GetCashReportPrintBill", e.getMessage());
        }
        return billPrinted;
    }


    public static String convert(final int n) {
        if (n < 0) {
            return "Minus " + convert(-n);
        }

        if (n < 20) {
            return units[n];
        }

        if (n < 100) {
            return tens[n / 10] + ((n % 10 != 0) ? " " : "") + units[n % 10];
        }

        if (n < 1000) {
            return units[n / 100] + " Hundred" + ((n % 100 != 0) ? " " : "") + convert(n % 100);
        }

        if (n < 100000) {
            return convert(n / 1000) + " Thousand" + ((n % 10000 != 0) ? " " : "") + convert(n % 1000);
        }

        if (n < 10000000) {
            return convert(n / 100000) + " Lakh" + ((n % 100000 != 0) ? " " : "") + convert(n % 100000);
        }

        return convert(n / 10000000) + " Crore" + ((n % 10000000 != 0) ? " " : "") + convert(n % 10000000);
    }

    public static void main(final String[] args) {

        int n;

        n = 5;
        System.out.println(NumberFormat.getInstance().format(n) + "='" + convert(n) + "'");

        n = 16;
        System.out.println(NumberFormat.getInstance().format(n) + "='" + convert(n) + "'");

        n = 50;
        System.out.println(NumberFormat.getInstance().format(n) + "='" + convert(n) + "'");

        n = 78;
        System.out.println(NumberFormat.getInstance().format(n) + "='" + convert(n) + "'");

        n = 456;
        System.out.println(NumberFormat.getInstance().format(n) + "='" + convert(n) + "'");

        n = 1000;
        System.out.println(NumberFormat.getInstance().format(n) + "='" + convert(n) + "'");

        n = 99999;
        System.out.println(NumberFormat.getInstance().format(n) + "='" + convert(n) + "'");

        n = 199099;
        System.out.println(NumberFormat.getInstance().format(n) + "='" + convert(n) + "'");

        n = 10005000;
        System.out.println(NumberFormat.getInstance().format(n) + "='" + convert(n) + "'");
    }

    public String containsWhiteSpace(String line){

        int tempindex=-1;
        int newlineindex=28;
        StringBuffer string = new StringBuffer(line);

        if(line != null){

            for(int i = 0; i < line.length(); i++){

                if(line.charAt(i) == ' '){
                    if(i>=newlineindex){
                        // line=line.replace(line.charAt(tempindex),'\n');
                        string.setCharAt(tempindex, '\n');
                        Log.d("Space Index : ",String.valueOf(tempindex));
                        Log.d("Line : ",string.toString());
                        newlineindex=newlineindex+newlineindex;
                    }
                    tempindex=i;
                }

            }
        }
        return string.toString();
    }

}
