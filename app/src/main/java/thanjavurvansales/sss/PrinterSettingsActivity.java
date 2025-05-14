package thanjavurvansales.sss;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class PrinterSettingsActivity extends AppCompatActivity {

    public static String PrinterType="TM-P20";
    public static String SelectedPrinterAddress ="";
    public static String SelectedPrinterName ="";
    public static String SelectedPrinterstatus ="";
    LinearLayout connectprinterLL;
    Button but_searchprinter;
    Dialog dialog;
    Context context;
    ListView lv_printerlist;
    ListView lv_printlistwithradio;

    CardView list_cardView;
    TextView txtconnectedprinter;
    ArrayAdapter adapter;

    public static ArrayList<String> Printername= new ArrayList<>();
    public static ArrayList<String> Printeraddress = new ArrayList<>();
    public static ArrayList<String> Printerstatus = new ArrayList<>();

    public static ArrayList<String> tempSelectedPrinterName= new ArrayList<>();
    public static ArrayList<String> tempSelectedPrinterAddress = new ArrayList<>();
    BluetoothAdapter mBluetoothAdapter;
    ProgressDialog loading;
    ImageView setting_goback,settings_logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer_settings);

        context=this;
        but_searchprinter = (Button) findViewById(R.id.but_SearchPrint);
        connectprinterLL = (LinearLayout) findViewById(R.id.ConnectPrintLL);
        txtconnectedprinter = (TextView) findViewById(R.id.txtconnectedprinter);
        lv_printlistwithradio = (ListView) findViewById(R.id.lv_printlistwithradio);
        setting_goback = (ImageView) findViewById(R.id.printergoback);
        settings_logout = (ImageView) findViewById(R.id.printerlogout);

        /*DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
        dataBaseAdapter.open();*/

        try{
           // Cursor cursor =dataBaseAdapter.getprinterdetails();
           // if(cursor.getCount()>0){
                //tempSelectedPrinterAddress.clear();
                //tempSelectedPrinterName.clear();

                //connectprinterLL.setVisibility(View.VISIBLE);
                //txtconnectedprinter.setText(cursor.getString(1) +" - "+ cursor.getString(2));

                //SelectedPrinterName=(cursor.getString(1));
                //SelectedPrinterAddress=(cursor.getString(2));

                //tempSelectedPrinterName.add(cursor.getString(1));
                //tempSelectedPrinterAddress.add(cursor.getString(2));

               /* SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

                if(!preferences.getString("SelectedPrinterName","").equals("") && !preferences.getString("SelectedPrinterAddress","").equals("")) {

                    tempSelectedPrinterAddress.clear();
                    tempSelectedPrinterName.clear();

                    connectprinterLL.setVisibility(View.VISIBLE);
                    txtconnectedprinter.setText(preferences.getString("SelectedPrinterName", "") + " - " + preferences.getString("SelectedPrinterAddress", ""));

                    *//*SelectedPrinterName = (preferences.getString("SelectedPrinterName", ""));
                    SelectedPrinterAddress = (preferences.getString("SelectedPrinterAddress", ""));

                    tempSelectedPrinterName.add(preferences.getString("SelectedPrinterName", ""));
                    tempSelectedPrinterAddress.add(preferences.getString("SelectedPrinterAddress", ""));*//*
                }*/

                PreferenceMangr preferenceMangr = new PreferenceMangr(context);
               // SelectedPrinterName = preferenceMangr.pref_getString("SelectedPrinterName");
               // SelectedPrinterAddress = preferenceMangr.pref_getString("SelectedPrinterAddress");

                //tempSelectedPrinterName.add(preferenceMangr.pref_getString("SelectedPrinterName"));
                //tempSelectedPrinterAddress.add(preferenceMangr.pref_getString("SelectedPrinterAddress"));



            if(!preferenceMangr.pref_getString("SelectedPrinterName").equals("") && !preferenceMangr.pref_getString("SelectedPrinterAddress").equals("")) {

                tempSelectedPrinterAddress.clear();
                tempSelectedPrinterName.clear();

                connectprinterLL.setVisibility(View.VISIBLE);
                txtconnectedprinter.setText(preferenceMangr.pref_getString("SelectedPrinterName") + " - " + preferenceMangr.pref_getString("SelectedPrinterAddress"));

                    SelectedPrinterName = (preferenceMangr.pref_getString("SelectedPrinterName"));
                    SelectedPrinterAddress = (preferenceMangr.pref_getString("SelectedPrinterAddress"));

                    tempSelectedPrinterName.add(preferenceMangr.pref_getString("SelectedPrinterName"));
                    tempSelectedPrinterAddress.add(preferenceMangr.pref_getString("SelectedPrinterAddress"));
            }
            //}
        }catch(Exception e){
            Log.d("Insert printer details ",e.toString());
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = e.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
        finally {
            //dataBaseAdapter.close();
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(!Printername.equals("") && !Printeraddress.equals("")){
            PrinterAdapter printerAdapter = new PrinterAdapter(context);
            lv_printlistwithradio.setAdapter(printerAdapter);
        }

        try{

            lv_printlistwithradio.setAdapter(null);
            Printername.clear();
            Printeraddress.clear();

            if(mBluetoothAdapter != null) {

                if(mBluetoothAdapter.getState()==BluetoothAdapter.STATE_ON) {

                    Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();

                    try{
                        for(BluetoothDevice BTdevice : devices){

                            String deviceName = BTdevice.getName();
                            String deviceHardwareAddress = BTdevice.getAddress();

                            String devicenameSubstring= deviceName.substring(0,6);
                            //Toast.makeText(context, devicenameSubstring, Toast.LENGTH_SHORT).show();
                            if(devicenameSubstring.equals(PrinterType)){
                                Printername.add(deviceName);
                                Printeraddress.add(deviceHardwareAddress);
                            }
                            PrinterAdapter printerAdapter = new PrinterAdapter(context);
                            lv_printlistwithradio.setAdapter(printerAdapter);

                        }

                    }catch(Exception e){
                        //Log.i("Printer : " , "device " + deviceName);
                    }

                }else{
                    Toast toast=Toast.makeText(context, "Bluetooth is disabled please turn on bluetooth.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 150);
                    toast.show();
                }
            }
            else {
                Toast toast = Toast.makeText(context, "Bluetooth is not available.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 150);
                toast.show();
            }

        }catch (Exception e){
//            Toast.makeText(context,"Bluetooth Adapter : "+e.toString(),Toast.LENGTH_SHORT).show();
            Log.d("Bluetooth Adapter : ",e.getMessage());
        }

        but_searchprinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{

                    lv_printlistwithradio.setAdapter(null);
                    Printername.clear();
                    Printeraddress.clear();

                    if(mBluetoothAdapter != null) {

                        if(mBluetoothAdapter.getState()==BluetoothAdapter.STATE_ON) {

                            Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();

                            try{
                                for(BluetoothDevice BTdevice : devices){


                                    String deviceName = BTdevice.getName();
                                    String deviceHardwareAddress = BTdevice.getAddress();

                                    String devicenameSubstring= deviceName.substring(0,6);
                                    //Toast.makeText(context, devicenameSubstring, Toast.LENGTH_SHORT).show();
                                    if(devicenameSubstring.equals(PrinterType)){
                                        Printername.add(deviceName);
                                        Printeraddress.add(deviceHardwareAddress);
                                    }
                                    PrinterAdapter printerAdapter = new PrinterAdapter(context);
                                    lv_printlistwithradio.setAdapter(printerAdapter);

                                }

                            }catch(Exception e){
                                //Log.i("Printer : " , "device " + deviceName);
                            }

                        }else{
                            Toast toast=Toast.makeText(context, "Bluetooth is disabled please turn on bluetooth.", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 150);
                            toast.show();
                        }
                    }
                    else {
                        Toast toast = Toast.makeText(context, "Bluetooth is not available.", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 150);
                        toast.show();
                    }

                }catch (Exception e){
//            Toast.makeText(context,"Bluetooth Adapter : "+e.toString(),Toast.LENGTH_SHORT).show();
                    Log.d("Bluetooth Adapter : ",e.getMessage());
                }
            }
        });

        settings_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        setting_goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //goBack(null);
                finish();
            }
        });
    }


    public class PrinterAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;


        PrinterAdapter(Context c){
            context = c;
            layoutInflater = LayoutInflater.from(context);

        }
        @Override
        public int getCount() {
            return Printeraddress.size();
        }

        @Override
        public Object getItem(int position) {
            return Printeraddress.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

       /* @Override
        public int getViewTypeCount() {
            return getCount();
        }*/

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {

            ViewHolder mviewHolder;

            if(view==null){
                view =layoutInflater.inflate(R.layout.printerlist_radio,viewGroup,false);
                mviewHolder = new ViewHolder();
                try{
                    mviewHolder.list_printername = (TextView) view.findViewById(R.id.printername);
                    //mviewHolder.list_printeraddress = (TextView) view.findViewById(R.id.printeraddress);

                    mviewHolder.list_selectedimg = (ImageView) view.findViewById(R.id.list_printerSelected);
                }catch (Exception e){
                    Log.d("Printer list", e.toString());
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                }
                view.setTag(mviewHolder);
            }else{
                mviewHolder = (ViewHolder) view.getTag();
            }

            try{
                mviewHolder.list_printername.setText(Printername.get(position) + " - "+ Printeraddress.get(position));
                //mviewHolder.list_printeraddress.setText(Printeraddress.get(position));
            }catch (Exception e){
                Log.d("Printer List : ",e.toString());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();

            }

            try {
                if(!tempSelectedPrinterAddress.equals("")) {
                    //Toast.makeText(context, tempSelectedPrinterAddress.get(0), Toast.LENGTH_SHORT).show();
                    if (Printeraddress.get(position).equals(tempSelectedPrinterAddress.get(0))) {
                        //mviewHolder.list_printerradio.setChecked(true);
                        mviewHolder.list_selectedimg.setVisibility(View.VISIBLE);
                    } else {
                        //mviewHolder.list_printerradio.setChecked(false);
                        mviewHolder.list_selectedimg.setVisibility(View.INVISIBLE);
                    }
                }
            }catch(Exception e){
                Log.d("Selected Printer : ",e.toString());
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Select "+Printername.get(position)+" printer ?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    tempSelectedPrinterAddress.clear();
                                    tempSelectedPrinterName.clear();


                                    /*SelectedPrinterName = Printername.get(position);
                                    SelectedPrinterAddress = Printeraddress.get(position);*/

                                    tempSelectedPrinterName.add(Printername.get(position));
                                    tempSelectedPrinterAddress.add(Printeraddress.get(position));


                                    //DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
                                    //dataBaseAdapter.open();

                                    try{
                                        //String result=dataBaseAdapter.insertprinterdetails(Printername.get(position),Printeraddress.get(position));
                                        //if(result.equals("success")) {
                                            SelectedPrinterName = Printername.get(position);
                                            SelectedPrinterAddress = Printeraddress.get(position);

                                            connectprinterLL.setVisibility(View.VISIBLE);
                                            txtconnectedprinter.setText(Printername.get(position) +" - "+ Printeraddress.get(position));

                                            //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                                            //SharedPreferences.Editor editor = preferences.edit();
                                            //editor.putString("SelectedPrinterName", Printername.get(position));
                                            //editor.putString("SelectedPrinterAddress", Printeraddress.get(position));
                                            //editor.apply();

                                            PreferenceMangr preferenceMangr = new PreferenceMangr(context);
                                            preferenceMangr.pref_putString("SelectedPrinterName",Printername.get(position));
                                            preferenceMangr.pref_putString("SelectedPrinterAddress",Printeraddress.get(position));

                                            Toast toast = Toast.makeText(context, "Printer "+SelectedPrinterName + " is connected", Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.CENTER, 0, 150);
                                            toast.show();

                                        //}
                                    }catch(Exception e){
                                        Log.d("Insert printer details ",e.toString());
                                        DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                                        mDbErrHelper.open();
                                        String geterrror = e.toString();
                                        mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                                        mDbErrHelper.close();
                                    }
                                    /*finally {
                                        if (dataBaseAdapter != null)
                                            dataBaseAdapter.close();
                                    }*/

                                    notifyDataSetChanged();
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


            return view;
        }

        public class ViewHolder{
            TextView list_printername,list_printeraddress;
            ///RadioButton list_printerradio;
            ImageView list_selectedimg;

        }
    }


    public void goBack(View v) {
        LoginActivity.ismenuopen=true;
        Intent i = new Intent(context, MenuActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }
}