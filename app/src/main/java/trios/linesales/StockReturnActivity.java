package trios.linesales;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StockReturnActivity extends AppCompatActivity  implements View.OnClickListener,View.OnTouchListener  {
    public static Context context;
    public static ListView stockreturnlistview;
    public static  ArrayList<OrderFormDetails> stockReturnDetails = new ArrayList<OrderFormDetails>();
    public static ArrayList<OrderFormDetails> cartStockReturnDetails = new ArrayList<OrderFormDetails>();
    FloatingActionButton fabgroupitem;
    ImageButton orderlogout,goback;
    String[] itemgroupcode,itemgroupname,itemgroupnametamil;
    String[] itemsubgroupcode,itemsubgroupname,itemsubgroupnametamil;
    TextView orderformcartreview;
    public static TextView carttotamount;
    Dialog itemgroupdialog,itemsubgroupdialog;
    ListView lv_GroupList,lv_SubGroupList,lv_subgroup;
    String[] SubGroupCode,SubGroupName,SubGroupNameTamil;
    public static String getitemgroupcode="0",getitemsubgroupcode="0";
    FloatingActionButton addsordertocart;
    boolean isopenpopup;
    private Boolean isFabOpen = false;
    private PopupWindow window;
    private Animation rotate_forward,rotate_backward;
    float dX;
    float dY;
    int lastAction;
    boolean isopenshowpopup;
    private List<String> listDataHEader;
    private HashMap<String,List<String>> listhash;
    public static String getstaticsubcode = "0";
    public static String getitemsgroupcode = "0";
    private ExpandableAdapter expandableAdapter;
    private ExpandableListView expList;
    public static OrderListBaseAdapter adapter;
    public static TextView txt_nodataavailable;
    public static PreferenceMangr preferenceMangr=null;
    ArrayList<DisplayGroupDetails> displayGroupDetails = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_return);

        context = this;
        stockreturnlistview = (ListView)findViewById(R.id.orderformlistview);
        orderlogout = (ImageButton)findViewById(R.id.orderlogout);
        goback = (ImageButton)findViewById(R.id.goback);
        orderformcartreview = (TextView)findViewById(R.id.orderformcartreview);
        addsordertocart = (FloatingActionButton) findViewById(R.id.addsordertocart);
        carttotamount = (TextView)findViewById(R.id.carttotamount) ;
        txt_nodataavailable = (TextView)findViewById(R.id.txt_nodataavailable);
        carttotamount.setText("0");
        fabgroupitem = (FloatingActionButton)findViewById(R.id.fabgroupitem);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
        getstaticsubcode = "0";
        getitemsgroupcode = "0";

        try {
            preferenceMangr = new PreferenceMangr(context);
        }catch (Exception e){
            Log.d("Preference Manager : ",e.toString());
        }

        if (!Utilities.isNetworkAvailable(context, false)) {
            Toast.makeText(context, "Internet is required for this transaction", Toast.LENGTH_SHORT).show();
            return;
        }

        //Get Current date
        DataBaseAdapter objdatabaseadapter1 = null;
        try{
            objdatabaseadapter1 = new DataBaseAdapter(context);
            objdatabaseadapter1.open();
            //LoginActivity.getformatdate = objdatabaseadapter1.GenCreatedDate();
            //LoginActivity.getcurrentdatetime = objdatabaseadapter1.GenCurrentCreatedDate();

            preferenceMangr.pref_putString("getformatdate",objdatabaseadapter1.GenCreatedDate());
            preferenceMangr.pref_putString("getcurrentdatetime",objdatabaseadapter1.GenCurrentCreatedDate());
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

        //Item and Item Subgroup data
        fabgroupitem.setOnClickListener(this);

        if (getIntent() != null) {
            if (getIntent().hasExtra("MODE") &&
                    !Utilities.isNullOrEmpty(getIntent().getStringExtra("MODE")) &&
                            getIntent().getStringExtra("MODE").equals("EDIT")) {
                DataBaseAdapter objdatabaseadapter2 = null;
                try {
                    objdatabaseadapter2 = new DataBaseAdapter(context);
                    objdatabaseadapter2.open();
                    String getschedulecode = objdatabaseadapter2.GetScheduleCode();
                    String getstockreturncount = objdatabaseadapter2.GetStockReturnCount(getschedulecode);
                    if (!getstockreturncount.equals("") && !getstockreturncount.equals("0") && !getstockreturncount.equals("null")) {
                        if (Double.parseDouble(getstockreturncount) > 0) {
                            GetStockReturnItems(getschedulecode);
                        }
                    }
                } catch (Exception e) {
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'", " "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                } finally {
                    // this gets called even if there is an exception somewhere above
                    if (objdatabaseadapter1 != null)
                        objdatabaseadapter1.close();
                }
            }
        }

        //open Cart Screen
        orderformcartreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //int qtyflag=0;
                /*if(orderFormDetails.size() > 0) {
                    for (int i = 0; i < orderformlistview.getChildCount(); i++) {
                        View listRow = orderformlistview.getChildAt(i);
                        EditText getlistqty = (EditText) listRow.findViewById(R.id.orderqty);
                        TextView getlisttotal = (TextView) listRow.findViewById(R.id.ordertotalstock);
                        String getitemlistqty = getlistqty.getText().toString();
                        String getitemlisttotal = getlisttotal.getText().toString();
                        if(!getitemlistqty.equals("") && !getitemlistqty.equals(null)){
                            if(getitemlisttotal.trim().equals("") || getitemlisttotal.trim().equals("null")){
                                getitemlisttotal = "0";
                            }
                            if(Double.parseDouble(getitemlistqty)>0 && Double.parseDouble(getitemlisttotal)<=0.0){
                                qtyflag = qtyflag+1;
                                getlistqty.requestFocus();
                               *//* Toast toast = Toast.makeText(getApplicationContext(),"Please enter valid total amount", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();*//*

                            }
                        }
                    }
                }*/
                boolean checkitemflag = false;
                int qtyflag = 0;
                int colorCode=0;
                ArrayList<String> itemcodes= new ArrayList<>();



                if(stockReturnDetails.size() > 0) {
                    itemcodes.clear();
                    for (int i = 0; i < stockreturnlistview.getChildCount(); i++) {
                        View listRow = stockreturnlistview.getChildAt(i);
                        EditText getlistqty = (EditText) listRow.findViewById(R.id.orderqty);
                        TextView getlisttotal = (TextView) listRow.findViewById(R.id.ordertotalstock);
                        TextView getlistname = (TextView) listRow.findViewById(R.id.orderitemname);

                        String getitemlistqty = getlistqty.getText().toString();
                        String getitemlisttotal = getlisttotal.getText().toString();
                        String getitemlistname = getlistname.getText().toString().trim();

                        String itemcode="0";
                        if(!Utilities.isNullOrEmpty(getitemlistqty) && !getitemlistqty.equals(".")){
                            if(Double.parseDouble(getitemlistqty)>0){
                                // itemcodes.add(orderFormDetails.get(i).getItemcode());
                                itemcode= stockReturnDetails.get(i).getItemcode();
                            }

                            //Toast.makeText(context,itemcode,Toast.LENGTH_SHORT).show();

                            if(cartStockReturnDetails.size()>0) {
                                for (int j = 0; j < cartStockReturnDetails.size(); j++) {
                                    if (cartStockReturnDetails.get(j).getItemcode().equals(itemcode)) {
                                        checkitemflag=false;
                                        break;
                                    }else{
                                        checkitemflag=true;
                                    }
                                }
                                if(checkitemflag){
                                    qtyflag = qtyflag +1;
                                }
                            }else{
                                if(Double.parseDouble(getitemlistqty)>0){
                                    qtyflag = qtyflag +1;
                                }
                            }
                        }


                        if(!getitemlistqty.equals("") && !getitemlistqty.equals("null") && !getitemlistqty.equals(null) &&
                                !getitemlistqty.equals(".")){
                            if(getitemlisttotal.trim().equals("") || getitemlisttotal.trim().equals("null")){
                                getitemlisttotal = "0";
                            }
                            if(Double.parseDouble(getitemlistqty)>0 && Double.parseDouble(getitemlisttotal)<0.0){
                                getlistqty.requestFocus();
                         /*   Toast toast = Toast.makeText(getApplicationContext(),"Please enter valid total amount", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return;*/
                                qtyflag = qtyflag +1;
                            }
                        }
                    }
                }

                if(qtyflag > 0){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setMessage("Some of the items are yet to be added in cart. Are you sure want to skip those items?");
                    alertDialogBuilder.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    if(cartStockReturnDetails.size()>0){
                                        stockReturnDetails.clear();
                                        OrderListBaseAdapter adapter = new OrderListBaseAdapter(context, stockReturnDetails);
                                        stockreturnlistview.setAdapter(adapter);
                                        Intent i = new Intent(context, StockReturnCartActivity.class);
                                        if (getIntent() != null) {
                                            if (getIntent().hasExtra("MODE") &&
                                                !Utilities.isNullOrEmpty(getIntent().getStringExtra("MODE")) &&
                                                getIntent().getStringExtra("MODE").equals("EDIT")) {
                                                i.putExtra("MODE", "EDIT");
                                            } else {
                                                i.putExtra("MODE", "NEW");
                                            }
                                        }
                                        startActivity(i);
                                    }else{
                                        Toast toast = Toast.makeText(getApplicationContext(),"Cart is empty", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                        //Toast.makeText(getApplicationContext(),"Cart is empty",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                    alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    return;
                }else{
                    if(cartStockReturnDetails.size()>0){
                        stockReturnDetails.clear();
                        OrderListBaseAdapter adapter = new OrderListBaseAdapter(context, stockReturnDetails);
                        stockreturnlistview.setAdapter(adapter);
                        Intent i = new Intent(context, StockReturnCartActivity.class);
                        if (getIntent() != null) {
                            if (getIntent().hasExtra("MODE") &&
                                    !Utilities.isNullOrEmpty(getIntent().getStringExtra("MODE")) &&
                                    getIntent().getStringExtra("MODE").equals("EDIT")) {
                                i.putExtra("MODE", "EDIT")  ;
                            } else {
                                i.putExtra("MODE", "NEW");
                            }
                        }
                        startActivity(i);
                    }else{
                        Toast toast = Toast.makeText(getApplicationContext(),"Cart is empty", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        //Toast.makeText(getApplicationContext(),"Cart is empty",Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });
        carttotamount.setText(String.valueOf(cartStockReturnDetails.size()));

        //Logout process
        orderlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // HomeActivity.logoutprocess = "True";
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

            }
        });
        //Goback process
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack(null);
            }
        });

        //Add to cart
        addsordertocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean iscart=false;

                    if(stockReturnDetails.size()>0){
                        for(int i = 0; i< stockReturnDetails.size(); i++){
                            if(!(stockReturnDetails.get(i).getQty().equals("")) &&
                                    !(stockReturnDetails.get(i).getQty().equals("0")) &&
                                    !(stockReturnDetails.get(i).getQty().equals("null"))
                                    && !(stockReturnDetails.get(i).getQty().equals(null)))
                            {
                                if(Double.parseDouble(stockReturnDetails.get(i).getQty())>0) {
                                    for (int j = 0; j < cartStockReturnDetails.size(); j++) {
                                        if (stockReturnDetails.get(i).getItemcode().equals
                                                (cartStockReturnDetails.get(j).getItemcode())) {
                                            cartStockReturnDetails.remove(j);
                                        }
                                    }
                                    cartStockReturnDetails.add(new OrderFormDetails(stockReturnDetails.get(i).getItemcode(), stockReturnDetails.get(i).getItemname(),
                                            stockReturnDetails.get(i).getItemnametamil(), stockReturnDetails.get(i).getUnitweight(),
                                            stockReturnDetails.get(i).getCompanycode(), stockReturnDetails.get(i).getColourcode(),
                                            stockReturnDetails.get(i).getUnitname(), stockReturnDetails.get(i).getHsn(),
                                            stockReturnDetails.get(i).getTax(), stockReturnDetails.get(i).getClosingstk(),
                                            stockReturnDetails.get(i).getQty(), String.valueOf(i + 1),
                                            stockReturnDetails.get(i).getUppweight(), stockReturnDetails.get(i).getStatus(),
                                            stockReturnDetails.get(i).getNoofdeciaml()));
                                    iscart = true;
                                }
                            }
                        }
                    }
                    if(iscart){
                        Toast toast = Toast.makeText(getApplicationContext(),"Item added to cart", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        //Toast.makeText(getApplicationContext(),"Item added to cart",Toast.LENGTH_SHORT).show();
                        carttotamount.setText(String.valueOf(cartStockReturnDetails.size()));
                    }else{
                        Toast toast = Toast.makeText(getApplicationContext(),"Please enter valid quantity", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        //Toast.makeText(getApplicationContext(),"Please enter valid quantity",Toast.LENGTH_SHORT).show();
                    }


            }
        });

      //No data available
        txt_nodataavailable.setVisibility(View.VISIBLE);


    }

    /*********************Fab button override function***********/

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                dX = view.getX() - event.getRawX();
                dY = view.getY() - event.getRawY();
                lastAction = MotionEvent.ACTION_DOWN;
                break;

            case MotionEvent.ACTION_MOVE:
                view.setY(event.getRawY() + dY);
                view.setX(event.getRawX() + dX);
                lastAction = MotionEvent.ACTION_MOVE;
                break;

            case MotionEvent.ACTION_UP:

                break;

            default:
                return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.fabgroupitem) {
            animateFAB();
        }
    }
    public void animateFAB(){
            boolean checkitemflag = false;
            int qtyflag = 0;
            int colorCode=0;
            ArrayList<String> itemcodes= new ArrayList<>();



            if(stockReturnDetails.size() > 0) {
                itemcodes.clear();
                for (int i = 0; i < stockreturnlistview.getChildCount(); i++) {
                    View listRow = stockreturnlistview.getChildAt(i);
                    EditText getlistqty = (EditText) listRow.findViewById(R.id.orderqty);
                    TextView getlisttotal = (TextView) listRow.findViewById(R.id.ordertotalstock);
                    TextView getlistname = (TextView) listRow.findViewById(R.id.orderitemname);

                    String getitemlistqty = getlistqty.getText().toString();
                    String getitemlisttotal = getlisttotal.getText().toString();
                    String getitemlistname = getlistname.getText().toString().trim();

                    String itemcode="0";
                    if(!getitemlistqty.equals("") && !getitemlistqty.equals("null") && !getitemlistqty.equals(null) &&
                        !getitemlistqty.equals(".")){
                        if(Double.parseDouble(getitemlistqty)>0){
                           // itemcodes.add(orderFormDetails.get(i).getItemcode());
                            itemcode= stockReturnDetails.get(i).getItemcode();
                        }

                        //Toast.makeText(context,itemcode,Toast.LENGTH_SHORT).show();

                       if(cartStockReturnDetails.size()>0) {
                            for (int j = 0; j < cartStockReturnDetails.size(); j++) {
                                if (cartStockReturnDetails.get(j).getItemcode().equals(itemcode)) {
                                    checkitemflag=false;
                                    break;
                                }else{
                                    checkitemflag=true;
                                }
                            }
                            if(checkitemflag){
                                qtyflag = qtyflag +1;
                            }
                        }else{
                           if(Double.parseDouble(getitemlistqty)>0){
                               qtyflag = qtyflag +1;
                           }
                       }
                    }


                    if(!getitemlistqty.equals("") && !getitemlistqty.equals("null") && !getitemlistqty.equals(null) &&
                            !getitemlistqty.equals(".")){
                        if(getitemlisttotal.trim().equals("") || getitemlisttotal.trim().equals("null")){
                            getitemlisttotal = "0";
                        }
                        if(Double.parseDouble(getitemlistqty)>0 && Double.parseDouble(getitemlisttotal)<0.0){
                            getlistqty.requestFocus();
                         /*   Toast toast = Toast.makeText(getApplicationContext(),"Please enter valid total amount", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return;*/
                          qtyflag = qtyflag +1;
                        }
                    }
                }
            }
            if(qtyflag > 0){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage("Some of the items are yet to be added in cart. Are you sure want to skip those items?");
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                if (isopenpopup) {
                                    window.dismiss();
                                    isFabOpen = true;
                                    isopenpopup = false;
                                } else {
                                    ShowPopupWindow();
                                }
                                if (isFabOpen) {
                                    fabgroupitem.startAnimation(rotate_backward);
                                    isFabOpen = false;
                                    Log.d("Fab", "close");
                                } else {
                                    fabgroupitem.startAnimation(rotate_forward);
                                    isFabOpen = true;
                                    Log.d("Fab", "open");
                                }
                            }
                        });

                alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return;
            }else{
                if (isopenpopup) {
                    window.dismiss();
                    isFabOpen = true;
                    isopenpopup = false;
                } else {
                    ShowPopupWindow();
                }
                if (isFabOpen) {
                    fabgroupitem.startAnimation(rotate_backward);
                    isFabOpen = false;
                    Log.d("Fab", "close");
                } else {
                    fabgroupitem.startAnimation(rotate_forward);
                    isFabOpen = true;
                    Log.d("Fab", "open");
                }
            }

    }
    private void ShowPopupWindow(){
        try {
            //Get phone imei number
            //Get phone imei number
            DataBaseAdapter objdatabaseadapter = null;
            String drilldownitem="subgroup";
            try{
                objdatabaseadapter = new DataBaseAdapter(context);
                objdatabaseadapter.open();
                drilldownitem = objdatabaseadapter.GetDrildownGroupStatusDB();

            }  catch (Exception e){
                Log.i("GetDrildown", e.toString());
            }
            finally {
                // this gets called even if there is an exception somewhere above
                if(objdatabaseadapter != null)
                    objdatabaseadapter.close();
            }

//            if(drilldownitem.equals("group")) {
//                isopenshowpopup=true;
//                LayoutInflater inflater = (LayoutInflater) OrderFormActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                View layout = inflater.inflate(R.layout.grouppopup, null);
//                window = new PopupWindow(layout, 650, 1000, false);
//
//                expList = (ExpandableListView) layout.findViewById(R.id.expandible_listview);
//                ImageView close = (ImageView) layout.findViewById(R.id.close);
//
//                close.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        window.dismiss();
//                        fabgroupitem.startAnimation(rotate_backward);
//                        isFabOpen = false;
//                        isopenpopup = false;
//                    }
//                });
//                //window.setOutsideTouchable(true);
//                window.showAtLocation(layout, Gravity.BOTTOM, 0, 140);
//                //setUpAdapter();
//                window.setOutsideTouchable(false);
//                isopenpopup = true;
//                setChildItems();
//
//                expandableAdapter = new ExpandableAdapter(this, listDataHEader, listhash);
//                expList.setAdapter(expandableAdapter);
//
//                expList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
//                    int previousGroup = -1;
//
//                    @Override
//                    public void onGroupExpand(int groupPosition) {
//                        if (groupPosition != previousGroup)
//                            expList.collapseGroup(previousGroup);
//                        previousGroup = groupPosition;
//                    }
//                });
//            }
//            else{
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            int screenHeight = displayMetrics.heightPixels;
            int popupHeight = (int) (screenHeight * 0.8); // 80% of screen height
            int popupWidth = (int) (screenHeight * 0.40); // or use screen width similarly
            isopenshowpopup=true;
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.subgrouppopup, null);
            window = new PopupWindow(layout, popupWidth, popupHeight, false);

            lv_subgroup = (ListView) layout.findViewById(R.id.lv_subgroup);
                ImageView close = (ImageView) layout.findViewById(R.id.close);

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        window.dismiss();
                        fabgroupitem.startAnimation(rotate_backward);
                        isFabOpen = false;
                        isopenpopup = false;
                    }
                });
                //window.setOutsideTouchable(true);
                window.showAtLocation(layout, Gravity.BOTTOM, 0, 140);
                //setUpAdapter();
                window.setOutsideTouchable(false);
                isopenpopup = true;

                //Call Sub group list
                GetDisplayGroupList();
//            }

        }catch (Exception e){

        }
    }


    @SuppressLint("Range")
    public  void GetDisplayGroupList(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        displayGroupDetails.clear();
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetDisplayGroupList();
            if(Cur.getCount()>0) {
                for(int i=0;i<Cur.getCount();i++){
                    displayGroupDetails.add(new DisplayGroupDetails(
                            Cur.getString(Cur.getColumnIndex("dgroupcode")),
                            Cur.getString(Cur.getColumnIndex("dgroupname")),
                            Cur.getString(Cur.getColumnIndex("dgrouptamil"))));
                    Cur.moveToNext();
                }

                DisplayGroupAdapter adapter = new DisplayGroupAdapter(context, displayGroupDetails);
                lv_subgroup.setAdapter(adapter);
            }else{
                Toast toast = Toast.makeText(getApplicationContext(),"Van out of stock", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }  catch (Exception e){
            Log.i("GetDisplayGroupList", e.toString());
        }
        finally {
            if(objdatabaseadapter != null)
                objdatabaseadapter.close();
            if(Cur != null)
                Cur.close();
        }
    }



    public class DisplayGroupAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;
        ArrayList<DisplayGroupDetails> displayGroupDetails;
        DisplayGroupAdapter(Context c, ArrayList<DisplayGroupDetails> displayGroupDetails) {
            context = c;
            this.displayGroupDetails = displayGroupDetails;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return this.displayGroupDetails.size();
        }

        @Override
        public Object getItem(int position) {
            return this.displayGroupDetails.get(position);
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
                convertView = layoutInflater.inflate(R.layout.subgrouppopuplist, parent, false);
                mHolder = new ViewHolder();
                try {
                    mHolder.listsubgroup = (TextView) convertView.findViewById(R.id.listsubgroup);
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

            DisplayGroupDetails currentListData = (DisplayGroupDetails) getItem(position);
            try {
                mHolder.listsubgroup.setText(currentListData.getDisplayGroupNameTamil());
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
                    getstaticsubcode = currentListData.getDisplayGroupCode();
//                    getitemsfromcode = currentListData.getDisplayGroupCode();


                    GetItem(currentListData.getDisplayGroupCode());
                    window.dismiss();
                    fabgroupitem.startAnimation(rotate_backward);
                    isFabOpen = false;
                    isopenpopup = false;
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView listsubgroup;

        }

    }


    //Subgroup Adapter
    public class SalesSubGroupAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;
        SalesSubGroupAdapter(Context c) {
            context = c;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return SubGroupCode.length;
        }

        @Override
        public Object getItem(int position) {
            return SubGroupCode[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
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
        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder mHolder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.subgrouppopuplist, parent, false);
                mHolder = new ViewHolder();
                try {
                    mHolder.listsubgroup = (TextView) convertView.findViewById(R.id.listsubgroup);
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
                mHolder.listsubgroup.setText(String.valueOf(SubGroupNameTamil[position]));
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
                    getstaticsubcode = SubGroupCode[position];
                    getitemsgroupcode = "0";
                    GetItem(getstaticsubcode);
                    window.dismiss();
                    fabgroupitem.startAnimation(rotate_backward);
                    isFabOpen = false;
                    isopenpopup = false;
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView listsubgroup;

        }

    }
    //EXPENDABLE ADAPTER

    public class ExpandableAdapter extends BaseExpandableListAdapter {

        Context ctx;
        private List<String> listDataheader;
        private HashMap<String,List<String>> listHashMap;
        SalesActivity objsales = new SalesActivity();
        //public static ArrayList<ArrayList<String>> childList;
        //private String[] parents;
        public ExpandableAdapter(Context ctx, List<String> listDataheader, HashMap<String, List<String>> listHashMap) {
            this.ctx = ctx;
            this.listDataheader = listDataheader;
            this.listHashMap = listHashMap;
        }

        @Override
        public int getGroupCount() {
            return listDataheader.size();
        }

        @Override
        public int getChildrenCount(int i) {
            return listHashMap.get(listDataheader.get(i)).size();
        }

        @Override
        public Object getGroup(int i) {
            return listDataheader.get(i);
        }

        @Override
        public Object getChild(int i, int j) {
            return listHashMap.get(listDataheader.get(i)).get(j);
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int j) {
            return j;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            String headertitle = (String)getGroup(i);
            if(view == null){
                LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.parent_layout, null);

            }

            TextView parent_textvew = (TextView) view.findViewById(R.id.parent_txt);
            parent_textvew.setText(headertitle);
            return  view;
        }

        @Override
        public View getChildView(int i, int j, boolean b, View view, ViewGroup viewGroup) {
            String getsubgroupcode="";
            String getgroupcode="";
            final String childtext = (String)getChild(i,j);
            if(view == null){
                LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.child_layout, null);
            }

            TextView child_textvew = (TextView) view.findViewById(R.id.child_txt);
            getsubgroupcode =  childtext.split("-")[1];
            child_textvew.setText(childtext.split("-")[0]);
            getgroupcode =  childtext.split("-")[2];
            final String finalGetsubgroupcode = getsubgroupcode;
            final String finalGetgroupcode = getgroupcode;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     getstaticsubcode = finalGetsubgroupcode;
                     getitemsgroupcode = finalGetgroupcode;
                    GetItem(getstaticsubcode);
                    window.dismiss();
                    fabgroupitem.startAnimation(rotate_backward);
                    isFabOpen = false;
                    isopenpopup = false;
                }
            });
            return  view;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }
    private  void setChildItems(){
        listDataHEader = new ArrayList<>();
        listhash = new HashMap<>();

        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        Cursor Cur1=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetAllGroupDB();
            if(Cur.getCount()>0) {
                for(int i=0;i<Cur.getCount();i++){
                    listDataHEader.add(Cur.getString(2));
                    Cur1 = objdatabaseadapter.GetAllSubGroup_GroupDB(Cur.getString(0));
                    List<String> subgrouplist = new ArrayList<>();
                    List<String> subgrouplistcode = new ArrayList<>();
                    for(int j=0;j<Cur1.getCount();j++){
                        subgrouplist.add(Cur1.getString(2) + "-" +Cur1.getString(0)  + "-" +Cur1.getString(3));
                        Cur1.moveToNext();
                    }
                    listhash.put(listDataHEader.get(i),subgrouplist);
                    Cur.moveToNext();
                }

            }else{
                Toast toast = Toast.makeText(getApplicationContext(),"No data available", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //Toast.makeText(getApplicationContext(),"Van out of stock",Toast.LENGTH_SHORT).show();

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
    /****************FILTER FUNCTIONALITY********************/

    //Item master
    public static void GetItem(String getdisplaygroupcode){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            stockreturnlistview.setAdapter(null);
            Cur = objdatabaseadapter.GetStockReturnItemDB(getitemsgroupcode,getdisplaygroupcode);
            if(Cur.getCount()>0) {
                txt_nodataavailable.setVisibility(View.GONE);
                stockReturnDetails.clear();
                for(int i=0;i<Cur.getCount();i++){
                    stockReturnDetails.add(new OrderFormDetails(Cur.getString(0),Cur.getString(1),
                            Cur.getString(2),Cur.getString(3),Cur.getString(4),
                            Cur.getString(5),Cur.getString(6),
                            Cur.getString(7),Cur.getString(8),Cur.getString(9),
                            "", String.valueOf(i+1),Cur.getString(10),"", Cur.getString(11) ));
                    Cur.moveToNext();
                }
                //Set ITEMQTY
                for (int i = 0; i < cartStockReturnDetails.size(); i++) {
                    for (int j = 0; j < stockReturnDetails.size(); j++) {
                        if (cartStockReturnDetails.get(i).getItemcode().equals(stockReturnDetails.get(j).getItemcode())) {
                            stockReturnDetails.get(j).setQty(cartStockReturnDetails.get(i).getQty());
                        }
                    }
                }
                 adapter = new OrderListBaseAdapter(context, stockReturnDetails);
                stockreturnlistview.setAdapter(adapter);
            }else{
                txt_nodataavailable.setVisibility(View.VISIBLE);
                Toast toast = Toast.makeText(context,"No item available", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //Toast.makeText(getApplicationContext(),"No item available",Toast.LENGTH_SHORT).show();
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

    /**************END FILTER FUNCTIONALITY*******************/

    /************BASE ADAPTER*************/


    static class OrderListBaseAdapter extends BaseAdapter {
        LayoutInflater inflater;
        Context context;
        ArrayList<OrderFormDetails> myList;
        DecimalFormat dft = new DecimalFormat("0.00");
        DecimalFormat defor = null;

        public OrderListBaseAdapter( Context context,ArrayList<OrderFormDetails> myList) {
            this.myList = myList;
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return myList.size();
        }

        @Override
        public OrderFormDetails getItem(int position) {
            return (OrderFormDetails) myList.get(position);
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
        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder mHolder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.stockreturnlist, parent, false);
                mHolder = new ViewHolder();
                try {
                    mHolder.listsno = (TextView) convertView.findViewById(R.id.ordersno);
                    mHolder.listitemname = (TextView) convertView.findViewById(R.id.orderitemname);
                    mHolder.listclosingstock = (TextView) convertView.findViewById(R.id.orderclosingstock);
                    mHolder.listreturnqty = (TextView) convertView.findViewById(R.id.orderqty);
                    mHolder.listLL = (LinearLayout) convertView.findViewById(R.id.listLL);
                    mHolder.card_view = (CardView)convertView.findViewById(R.id.card_view);
                    mHolder.listunit = (TextView)convertView.findViewById(R.id.orderunit);
                    // mHolder.itemdelete = (ImageView)convertView.findViewById(R.id.itemdelete);
                    mHolder.listremainingstock = (TextView)convertView.findViewById(R.id.ordertotalstock);
                    mHolder.qtyWatcher = new QuantityTextWatcher(mHolder);
                    mHolder.listreturnqty.addTextChangedListener(mHolder.qtyWatcher);

                    convertView.setTag(mHolder);
                } catch (Exception e) {
                    Log.i("Orderitem", e.toString());
                    DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                    mDbErrHelper.open();
                    String geterrror = e.toString();
                    mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                    mDbErrHelper.close();
                }

            } else {
                mHolder = (ViewHolder) convertView.getTag();
            }
            try {
                mHolder.qtyWatcher.updatePosition(position);
                final OrderFormDetails currentListData = getItem(position);
                mHolder.listsno.setText(currentListData.getSno());
                if (!(currentListData.getItemnametamil().equals(""))
                        && !(currentListData.getItemnametamil()).equals("null")
                        && !((currentListData.getItemnametamil()).equals(null))) {
                    mHolder.listitemname.setText(currentListData.getItemnametamil() + " - " +
                            currentListData.getUnitname());
                }else{
                    mHolder.listitemname.setText(currentListData.getItemname() + " - " +
                            currentListData.getUnitname());
                }
                if(!currentListData.getClosingstk().equals("null") && !currentListData.getClosingstk().equals(null)){
//                if(Double.parseDouble(currentListData.getClosingstk()) > 0){
//                    mHolder.itemdelete.setVisibility(View.VISIBLE);
//                }else{
//                    mHolder.itemdelete.setVisibility(View.GONE);
//                }
                    mHolder.listclosingstock.setText(currentListData.getClosingstk());
                }else{
                    //   mHolder.itemdelete.setVisibility(View.GONE);
                    mHolder.listclosingstock.setText(dft.format(Double.parseDouble("0")));
                }


                // mHolder.orderunit.setText(currentListData.getUnitname());

                String noOfDecimal = currentListData.getNoofdeciaml();

                if (Integer.parseInt(noOfDecimal) > 0) {
                    // Allow decimals
                    mHolder.listreturnqty.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                } else {
                    // Disallow decimals — only integers
                    mHolder.listreturnqty.setInputType(InputType.TYPE_CLASS_NUMBER);
                }


                String getnoofdigits = "0";
                if (noOfDecimal.equals("0")) {
                    getnoofdigits = "";
                }
                if (noOfDecimal.equals("1")) {
                    getnoofdigits = "0";
                }
                if (noOfDecimal.equals("2")) {
                    getnoofdigits = "00";
                }
                if (noOfDecimal.equals("3")) {
                    getnoofdigits = "000";
                }

                if (Utilities.isNullOrEmpty(getnoofdigits))
                    defor = new DecimalFormat("0");
                else
                    defor = new DecimalFormat("0." + getnoofdigits );

                //Set Quantity
                if (!Utilities.isNullOrEmpty(currentListData.getClosingstk()) &&
                        Double.parseDouble(currentListData.getClosingstk()) > 0 &&
                        !Utilities.isNullOrEmpty(currentListData.getQty()) &&
                        Double.parseDouble(currentListData.getQty()) > 0) {
                    mHolder.listreturnqty.setText(String.valueOf(currentListData.getQty()));
                    //calculate total
                    double a = Double.parseDouble(currentListData.getClosingstk());
                    double b = Double.parseDouble(mHolder.listreturnqty.getText().toString());
                    double c=a-b;
                    int getval = (int) c;
                    mHolder.listremainingstock.setText(defor.format(c));
                    currentListData.setQty( mHolder.listreturnqty.getText().toString());
                    mHolder.listremainingstock.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                }
                currentListData.setStatus("");
                mHolder.listitemname.setTextColor(Color.parseColor(currentListData.getColourcode()));

//                mHolder.listreturnqty.addTextChangedListener(new TextWatcher() {
//                    public void onTextChanged(CharSequence s, int start, int before,
//                                              int count) {
//                    }
//
//                    public void beforeTextChanged(CharSequence s, int start, int count,
//                                                  int after) {
//                    }
//
//                    public void afterTextChanged(Editable s) {
//
//                    }
//                });

           /* mHolder.itemdelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //move stock item
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Are you sure you want move to cart ?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    for (int j = 0; j < OrderFormActivity.cartorderFormDetails.size(); j++) {
                                        if (myList.get(position).getItemcode().equals
                                                (OrderFormActivity.cartorderFormDetails.get(j).getItemcode())) {
                                            OrderFormActivity.cartorderFormDetails.remove(j);
                                        }
                                    }
                                    *//*for (int j = 0; j < OrderFormActivity.cartorderFormDetails.size(); j++) {
                                        if (OrderFormActivity.orderFormDetails.get(i).getItemcode().equals
                                                (cartorderFormDetails.get(j).getItemcode())) {
                                            OrderFormActivity.cartorderFormDetails.remove(j);
                                        }
                                    }*//*
                                    OrderFormActivity.cartorderFormDetails.add(new OrderFormDetails(myList.get(position).getItemcode(), myList.get(position).getItemname(),
                                            myList.get(position).getItemnametamil(), myList.get(position).getUnitweight(),
                                            myList.get(position).getCompanycode(), myList.get(position).getColourcode(),
                                            myList.get(position).getUnitname(), myList.get(position).getHsn(),
                                            myList.get(position).getTax(), myList.get(position).getClosingstk(),
                                           "0", "1",myList.get(position).getUppweight(),"deleted"));
                                    OrderFormActivity.carttotamount.setText(String.valueOf(OrderFormActivity.cartorderFormDetails.size()));
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
            });*/

                //Total click listener
                mHolder.listremainingstock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!mHolder.listreturnqty.getText().toString().equals("") &&
                                !mHolder.listreturnqty.getText().toString().equals(null) &&
                                !mHolder.listreturnqty.getText().toString().equals("null") &&
                                !mHolder.listreturnqty.getText().toString().equals("0") &&
                                !mHolder.listreturnqty.getText().toString().equals(".") &&
                                Double.parseDouble(mHolder.listreturnqty.getText().toString()) > 0 &&
                                Double.parseDouble(myList.get(position).getClosingstk()) > 0 &&
                                (Double.parseDouble(mHolder.listreturnqty.getText().toString()) <= Double.parseDouble(myList.get(position).getClosingstk()))) {
                            //calculate total
                            double a = Double.parseDouble(currentListData.getClosingstk());
                            double b = Double.parseDouble(mHolder.listreturnqty.getText().toString());
                            double c=a-b;
                            int getval = (int) c;
                            mHolder.listremainingstock.setText(defor.format(c));

                            currentListData.setQty( mHolder.listreturnqty.getText().toString());
                            boolean iscart = false;
                            if (stockReturnDetails.size() > 0) {
                                mHolder.listremainingstock.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                                ;
                                for (int i = 0; i < stockReturnDetails.size(); i++) {
                                    if (!(stockReturnDetails.get(i).getQty().equals("")) &&
                                            !(stockReturnDetails.get(i).getQty().equals("0")) &&
                                            !(stockReturnDetails.get(i).getQty().equals("null"))
                                            && !(stockReturnDetails.get(i).getQty().equals(null))) {
                                        if (Double.parseDouble(stockReturnDetails.get(i).getQty()) > 0) {
                                            for (int j = 0; j < cartStockReturnDetails.size(); j++) {
                                                if (stockReturnDetails.get(i).getItemcode().equals
                                                        (cartStockReturnDetails.get(j).getItemcode())) {
                                                    cartStockReturnDetails.remove(j);
                                                }
                                            }
                                            cartStockReturnDetails.add(new OrderFormDetails(stockReturnDetails.get(i).getItemcode(), stockReturnDetails.get(i).getItemname(),
                                                    stockReturnDetails.get(i).getItemnametamil(), stockReturnDetails.get(i).getUnitweight(),
                                                    stockReturnDetails.get(i).getCompanycode(), stockReturnDetails.get(i).getColourcode(),
                                                    stockReturnDetails.get(i).getUnitname(), stockReturnDetails.get(i).getHsn(),
                                                    stockReturnDetails.get(i).getTax(), stockReturnDetails.get(i).getClosingstk(),
                                                    stockReturnDetails.get(i).getQty(), String.valueOf(i + 1),
                                                    stockReturnDetails.get(i).getUppweight(), stockReturnDetails.get(i).getStatus(),
                                                    stockReturnDetails.get(i).getNoofdeciaml()));
                                            iscart = true;
                                        }
                                    }
                                }
                                carttotamount.setText(String.valueOf(cartStockReturnDetails.size()));
                            }
                        }else{
                            for (int j = 0; j < StockReturnActivity.cartStockReturnDetails.size(); j++) {
                                if (myList.get(position).getItemcode().equals
                                        (StockReturnActivity.cartStockReturnDetails.get(j).getItemcode())) {
                                    StockReturnActivity.cartStockReturnDetails.remove(j);
                                }
                            }
                            StockReturnActivity.carttotamount.setText(String.valueOf(StockReturnActivity.cartStockReturnDetails.size()));
                            mHolder.listremainingstock.setText("");
                            currentListData.setQty("0");
                            mHolder.listremainingstock.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                        }
                    }
                });
            } catch (Exception e) {
                Log.i("Order value", e.toString());
                DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
                mDbErrHelper.open();
                String geterrror = e.toString();
                mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
                mDbErrHelper.close();
            }
            mHolder.listreturnqty.setTag(position);
            mHolder.listremainingstock.setTag(position);
            return convertView;
        }
        private class ViewHolder {
            TextView listsno, listitemname, listclosingstock, listreturnqty, listunit, listremainingstock;
            LinearLayout listLL;
            CardView card_view;
            ImageView itemdelete;
            private QuantityTextWatcher qtyWatcher;

        }

        class QuantityTextWatcher implements TextWatcher {
            private int position;
            private ViewHolder mHolder;

            public QuantityTextWatcher(ViewHolder mHolder) {
                this.mHolder = mHolder;
            }

            public void updatePosition(int position) {
                this.position = position;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {
                if( !mHolder.listreturnqty.getText().toString().equals("") &&
                        !mHolder.listreturnqty.getText().toString().equals(null) &&
                        !mHolder.listreturnqty.getText().toString().equals("null") &&
                        !mHolder.listreturnqty.getText().toString().equals("0") &&
                        !mHolder.listreturnqty.getText().toString().equals(".") &&
                        Double.parseDouble(mHolder.listreturnqty.getText().toString()) > 0 &&
                        Double.parseDouble(myList.get(position).getClosingstk()) > 0 &&
                        (Double.parseDouble(mHolder.listreturnqty.getText().toString()) <= Double.parseDouble(myList.get(position).getClosingstk()))) {
                    double a = Double.parseDouble(myList.get(position).getClosingstk());
                    double b = Double.parseDouble(mHolder.listreturnqty.getText().toString());
                    double c=a-b;
                    int getval = (int) c;
                    mHolder.listremainingstock.setText(defor.format(c));

                    mHolder.listremainingstock.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                    for (int j = 0; j < StockReturnActivity.cartStockReturnDetails.size(); j++) {
                        if (myList.get(position).getItemcode().equals
                                (StockReturnActivity.cartStockReturnDetails.get(j).getItemcode())) {
                            if(Double.parseDouble(mHolder.listreturnqty.getText().toString()) !=
                                    Double.parseDouble(StockReturnActivity.cartStockReturnDetails.get(j).getQty())){
                                StockReturnActivity.cartStockReturnDetails.remove(j);
                            }

                        }
                    }
                    StockReturnActivity.carttotamount.setText(String.valueOf(StockReturnActivity.cartStockReturnDetails.size()));

                }else{
                    for (int j = 0; j < StockReturnActivity.cartStockReturnDetails.size(); j++) {
                        if (myList.get(position).getItemcode().equals
                                (StockReturnActivity.cartStockReturnDetails.get(j).getItemcode())) {
                            StockReturnActivity.cartStockReturnDetails.remove(j);
                        }
                    }
                    StockReturnActivity.carttotamount.setText(String.valueOf(StockReturnActivity.cartStockReturnDetails.size()));
                    mHolder.listremainingstock.setText("");
                    myList.get(position).setQty("0");
                    mHolder.listremainingstock.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                }
            }
        }

    }
    /************END BASE ADAPTER*************/
    public void goBack(View v) {
        int qtyflag = 0;
        if(stockReturnDetails.size() > 0) {
            for (int i = 0; i < stockreturnlistview.getChildCount(); i++) {
                View listRow = stockreturnlistview.getChildAt(i);
                EditText getlistqty = (EditText) listRow.findViewById(R.id.orderqty);
                TextView getlisttotal = (TextView) listRow.findViewById(R.id.ordertotalstock);
                String getitemlistqty = getlistqty.getText().toString();
                String getitemlisttotal = getlisttotal.getText().toString();
                if(!getitemlistqty.equals("") && !getitemlistqty.equals(null) && !getitemlistqty.equals("null") &&
                        !getitemlistqty.equals(".")){
                    if(getitemlisttotal.trim().equals("") || getitemlisttotal.trim().equals("null")){
                        getitemlisttotal = "0";
                    }
                    if(Double.parseDouble(getitemlistqty)>0 && Double.parseDouble(getitemlisttotal)<0.0){
                        getlistqty.requestFocus();
                        qtyflag = qtyflag+1;
//                        Toast toast = Toast.makeText(getApplicationContext(),"Please enter valid total amount", Toast.LENGTH_LONG);
//                        toast.setGravity(Gravity.CENTER, 0, 0);
//                        toast.show();
                    }
                }
            }
        }
        if(qtyflag > 0){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setMessage("Some of the items are yet to be added in cart. Are you sure want to skip those items?");
            alertDialogBuilder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            if(cartStockReturnDetails.size() > 0 ){
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Confirmation");
                                builder.setMessage("Are you sure you want to clear cart?")
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                cartStockReturnDetails.clear();
                                                stockReturnDetails.clear();
                                                LoginActivity.ismenuopen=true;
                                                Intent i = new Intent(context, MenuActivity.class);
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
                                cartStockReturnDetails.clear();
                                stockReturnDetails.clear();
                                LoginActivity.ismenuopen=true;
                                Intent i = new Intent(context, MenuActivity.class);
                                startActivity(i);
                            }
                        }
                    });

            alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return;
        }else if(cartStockReturnDetails.size() > 0 ){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Confirmation");
            builder.setMessage("Are you sure you want to clear cart?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            cartStockReturnDetails.clear();
                            stockReturnDetails.clear();
                            LoginActivity.ismenuopen=true;
                            Intent i = new Intent(context, MenuActivity.class);
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
            cartStockReturnDetails.clear();
            stockReturnDetails.clear();
            LoginActivity.ismenuopen=true;
            Intent i = new Intent(context, MenuActivity.class);
            startActivity(i);
        }

    }
    @Override
    public void onBackPressed() {
        goBack(null);
    }

    public  void GetStockReturnItems(String getorderschedulecode){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetStockReturnItemDB("0","0",getorderschedulecode);
            if(Cur.getCount()>0) {
                StockReturnActivity.cartStockReturnDetails.clear();
                for(int i=0;i<Cur.getCount();i++){
                    StockReturnActivity.cartStockReturnDetails.add(new OrderFormDetails(Cur.getString(0),Cur.getString(1),
                            Cur.getString(2),Cur.getString(3),Cur.getString(4),
                            Cur.getString(5),Cur.getString(6),
                            Cur.getString(7),Cur.getString(8),Cur.getString(9),
                            Cur.getString(11), String.valueOf(i+1),Cur.getString(10),"", Cur.getString(12) ));
                    Cur.moveToNext();
                }

            }else{
                Toast toast = Toast.makeText(getApplicationContext(),"No item available", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //Toast.makeText(getApplicationContext(),"No item available",Toast.LENGTH_SHORT).show();
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
}
