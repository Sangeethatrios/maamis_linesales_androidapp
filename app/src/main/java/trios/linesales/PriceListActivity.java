package trios.linesales;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class PriceListActivity extends AppCompatActivity {
    Context context;
    ListView pricelistview;
    ArrayList<PriceDetails> priceDetails = new ArrayList<PriceDetails>();
    EditText txtsearchitem;
    boolean ispopensearch = false;
    ImageButton pricelistlogout,goback;
    TextView pricedate,txtitemgroup,txtitemsubgroup;
    private int year, month, day;
    private Calendar calendar;
    Dialog itemgroupdialog,itemsubgroupdialog;
    ListView lv_GroupList,lv_SubGroupList;
    String getitemgroupcode="0",getitemsubgroupcode="0";
    String[] itemgroupcode,itemgroupname,itemgroupnametamil;
    String[] itemsubgroupcode,itemsubgroupname,itemsubgroupnametamil;
    LinearLayout searchLL;
    ImageButton imgsearchbtn;
    TextView pricesorting;
    boolean issorting=true;
    Spinner selectitemstatus;
    String getitemsatus="All Items";
    String[] arraitemstatus=new String[0];
    boolean clicksorting=false;
    public static PreferenceMangr preferenceMangr=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_list);

        context = this;
        pricelistview = (ListView)findViewById(R.id.pricelistview);
        pricelistlogout = (ImageButton)findViewById(R.id.pricelistlogout);
        goback = (ImageButton)findViewById(R.id.goback);
        txtitemgroup = (TextView)findViewById(R.id.txtitemgroup);
        txtitemsubgroup = (TextView)findViewById(R.id.txtitemsubgroup);
        searchLL = (LinearLayout)findViewById(R.id.searchLL);
        imgsearchbtn = (ImageButton)findViewById(R.id.imgsearchbtn);
        pricesorting = (TextView) findViewById(R.id.pricesorting);
        selectitemstatus = (Spinner)findViewById(R.id.selectitemstatus);
        arraitemstatus = getResources().getStringArray(R.array.itemstatus);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        pricedate = (TextView)findViewById(R.id.pricedate);

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

        txtsearchitem = (EditText) findViewById(R.id.txtsearchitem);

        pricedate.setText(preferenceMangr.pref_getString("getcurrentdatetime"));

        fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                if(!ispopensearch) {
                    ispopensearch = true;
                    fab.setImageResource(R.drawable.ic_close);
                    searchLL.setVisibility(View.VISIBLE);
                }else{
                    ispopensearch = false;
                    fab.setImageResource(R.drawable.ic_search);
                    searchLL.setVisibility(View.GONE);
                }
            }
        });

        imgsearchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetItem();
            }
        });

        //Logout process
        pricelistlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack(null);
            }
        });

        //Item Group
        txtitemgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetItemGroup();
            }
        });
        //Item Sub Group
        txtitemsubgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetItemSubGroup();
            }
        });

        pricesorting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicksorting=true;GetItem();
            }
        });


        //Set Item Group

        txtitemgroup.setText("All Item Group");
        getitemgroupcode = "0";
        getitemsubgroupcode="0";
        txtitemsubgroup.setText("All Item Sub-Group");


        selectitemstatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                int index = parentView.getSelectedItemPosition();
                getitemsatus = arraitemstatus[index];
                GetItem();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

       /* DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetOrderItemgroupDB();
            if(Cur.getCount()>0) {
                itemgroupcode = new String[Cur.getCount()];
                itemgroupname = new String[Cur.getCount()];
                itemgroupnametamil = new String[Cur.getCount()];
                for(int i=0;i<Cur.getCount();i++){
                    itemgroupcode[i] = Cur.getString(0);
                    itemgroupname[i] = Cur.getString(1);
                    itemgroupnametamil[i] = Cur.getString(2);
                }
                txtitemgroup.setText("All Group");
                getitemgroupcode = "0";
                getitemsubgroupcode="0";
                txtitemsubgroup.setText("");
                txtitemsubgroup.setHint("Item Sub-Group");
                GetItem();
            }else{
                txtitemgroup.setText("");
                getitemgroupcode = "0";
                txtitemgroup.setHint("Item Group");
                //Toast.makeText(getApplicationContext(),"No item group available",Toast.LENGTH_SHORT).show();
            }
        }  catch (Exception e){
            Log.i("GetItemGroup", e.toString());
        }
        finally {
            // this gets called even if there is an exception somewhere above
            if(objdatabaseadapter != null)
                objdatabaseadapter.close();
            if(Cur != null)
                Cur.close();
        }*/
    }
    /***** drop down functionality**********/

    public  void GetItemGroup(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetOrderItemgroupDB();
            if(Cur.getCount()>0) {
                itemgroupcode = new String[Cur.getCount()];
                itemgroupname = new String[Cur.getCount()];
                itemgroupnametamil = new String[Cur.getCount()];
                for(int i=0;i<Cur.getCount();i++){
                    itemgroupcode[i] = Cur.getString(0);
                    itemgroupname[i] = Cur.getString(1);
                    itemgroupnametamil[i] = Cur.getString(2);
                    Cur.moveToNext();
                }

                itemgroupdialog = new Dialog(context);
                itemgroupdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                itemgroupdialog.setContentView(R.layout.itemgrouppopup);
                lv_GroupList = (ListView) itemgroupdialog.findViewById(R.id.lv_GroupList);
                ImageView close = (ImageView) itemgroupdialog.findViewById(R.id.close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemgroupdialog.dismiss();
                    }
                });
                clicksorting=false;
                ItemGroupAdapter adapter = new ItemGroupAdapter(context);
                lv_GroupList.setAdapter(adapter);
                itemgroupdialog.show();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(),"No item group available", Toast.LENGTH_LONG);
                //toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //Toast.makeText(getApplicationContext(),"No item group available",Toast.LENGTH_SHORT).show();
            }
        }  catch (Exception e){
            Log.i("GetItemGroup", e.toString());
        }
        finally {
            // this gets called even if there is an exception somewhere above
            if(objdatabaseadapter != null)
                objdatabaseadapter.close();
            if(Cur != null)
                Cur.close();
        }
    }
    //Item sub group
    public  void GetItemSubGroup(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetOrderItemSubgroupDB(getitemgroupcode);
            if(Cur.getCount()>0) {
                itemsubgroupcode = new String[Cur.getCount()];
                itemsubgroupname = new String[Cur.getCount()];
                itemsubgroupnametamil = new String[Cur.getCount()];
                for(int i=0;i<Cur.getCount();i++){
                    itemsubgroupcode[i] = Cur.getString(0);
                    itemsubgroupname[i] = Cur.getString(1);
                    itemsubgroupnametamil[i] = Cur.getString(2);
                    Cur.moveToNext();
                }

                itemsubgroupdialog = new Dialog(context);
                itemsubgroupdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                itemsubgroupdialog.setContentView(R.layout.itemsubgrouppopup);
                lv_SubGroupList = (ListView) itemsubgroupdialog.findViewById(R.id.lv_SubGroupList);
                ImageView close = (ImageView) itemsubgroupdialog.findViewById(R.id.close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemsubgroupdialog.dismiss();
                    }
                });
                clicksorting=false;
                ItemSubGroupAdapter adapter = new ItemSubGroupAdapter(context);
                lv_SubGroupList.setAdapter(adapter);
                itemsubgroupdialog.show();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(),"No item sub group in this item group", Toast.LENGTH_LONG);
                //toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //Toast.makeText(getApplicationContext(),"No item sub group in this item group",Toast.LENGTH_SHORT).show();
            }
        }  catch (Exception e){
            Log.i("GetItemSubGroup", e.toString());
        }
        finally {
            // this gets called even if there is an exception somewhere above
            if(objdatabaseadapter != null)
                objdatabaseadapter.close();
            if(Cur != null)
                Cur.close();
        }
    }

    //Item master
    public  void GetItem(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetPriceItemDB(getitemgroupcode,getitemsubgroupcode,
                    txtsearchitem.getText().toString(),getitemsatus);
            if(Cur.getCount()>0) {
                priceDetails.clear();
                for(int i=0;i<Cur.getCount();i++){
                    priceDetails.add(new PriceDetails(Cur.getString(0),Cur.getString(1),
                            Cur.getString(2),Cur.getString(3),Cur.getString(4),
                            Cur.getString(5),Cur.getString(6),Cur.getString(7)
                            ,Cur.getString(8),Cur.getDouble(9),Cur.getDouble(10)
                            ,String.valueOf(i+1),Cur.getString(16),Cur.getDouble(17),Cur.getDouble(18)));

                    Cur.moveToNext();
                }
                if(clicksorting) {
                    if (issorting) {
                        pricesorting.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_downward_white, 0);
                        issorting = false;
                        Collections.sort(priceDetails, new PriceDetails.SortbyPriceDesc());
                    } else {
                        pricesorting.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_upward_white, 0);
                        issorting = true;
                        Collections.sort(priceDetails, new PriceDetails.SortbyPriceAsc());
                    }
                }

                PriceListBaseAdapter adapter = new PriceListBaseAdapter(context, priceDetails);
                pricelistview.setAdapter(adapter);
            }else{
                priceDetails.clear();
                PriceListBaseAdapter adapter = new PriceListBaseAdapter(context, priceDetails);
                pricelistview.setAdapter(adapter);
                Toast toast = Toast.makeText(getApplicationContext(),"No item available", Toast.LENGTH_LONG);
                //toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
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

    /************BASE ADAPTER*************/
    //Item Group Adapter
    public class ItemGroupAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;

        ItemGroupAdapter(Context c) {
            context = c;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return itemgroupcode.length;
        }

        @Override
        public Object getItem(int position) {
            return itemgroupcode[position];
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
                convertView = layoutInflater.inflate(R.layout.itemgrouppopuplist, parent, false);
                mHolder = new ViewHolder();
                try {
                    mHolder.listgroupname = (TextView) convertView.findViewById(R.id.listgroupname);
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
                if (!(String.valueOf(itemgroupnametamil[position])).equals("")
                        && !(String.valueOf(itemgroupnametamil[position])).equals("null")
                        && !(String.valueOf(itemgroupnametamil[position])).equals(null)) {
                    mHolder.listgroupname.setText(String.valueOf(itemgroupnametamil[position]));
                } else {
                    mHolder.listgroupname.setText(String.valueOf(itemgroupname[position]));
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
                    if (!(String.valueOf(itemgroupnametamil[position])).equals("")
                            && !(String.valueOf(itemgroupnametamil[position])).equals("null")
                            && !(String.valueOf(itemgroupnametamil[position])).equals(null)) {
                        txtitemgroup.setText(String.valueOf(itemgroupnametamil[position] ));
                    } else {
                        txtitemgroup.setText(String.valueOf(itemgroupname[position] ));
                    }
                    getitemgroupcode = itemgroupcode[position];
                    itemgroupdialog.dismiss();
                    getitemsubgroupcode="0";
                    txtitemsubgroup.setText("All Item Sub-Group");
                    GetItem();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView listgroupname;

        }

    }
    //Item Sub Group Adapter
    public class ItemSubGroupAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;

        ItemSubGroupAdapter(Context c) {
            context = c;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return itemsubgroupcode.length;
        }

        @Override
        public Object getItem(int position) {
            return itemsubgroupcode[position];
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
                convertView = layoutInflater.inflate(R.layout.itemsubgrouppopuplist, parent, false);
                mHolder = new ViewHolder();
                try {
                    mHolder.listsubgroupname = (TextView) convertView.findViewById(R.id.listsubgroupname);
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
                if (!(String.valueOf(itemsubgroupnametamil[position])).equals("")
                        && !(String.valueOf(itemsubgroupnametamil[position])).equals("null")
                        && !(String.valueOf(itemsubgroupnametamil[position])).equals(null)) {
                    mHolder.listsubgroupname.setText(String.valueOf(itemsubgroupnametamil[position]));
                } else {
                    mHolder.listsubgroupname.setText(String.valueOf(itemsubgroupname[position]));
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
                    if (!(String.valueOf(itemsubgroupnametamil[position])).equals("")
                            && !(String.valueOf(itemsubgroupnametamil[position])).equals("null")
                            && !(String.valueOf(itemsubgroupnametamil[position])).equals(null)) {
                        txtitemsubgroup.setText(String.valueOf(itemsubgroupnametamil[position] ));
                    } else {
                        txtitemsubgroup.setText(String.valueOf(itemsubgroupname[position] ));
                    }
                    getitemsubgroupcode = itemsubgroupcode[position];
                    itemsubgroupdialog.dismiss();
                    GetItem();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView listsubgroupname;

        }

    }
    /************END BASE ADAPTER*************/
    public void goBack(View v) {
        LoginActivity.ismenuopen=true;
        Intent i = new Intent(context, MenuActivity.class);
        startActivity(i);
    }
    @Override
    public void onBackPressed() {
        goBack(null);
    }
}
