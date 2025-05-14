package thanjavurvansales.sss;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderFormActivity extends AppCompatActivity  implements View.OnClickListener,View.OnTouchListener  {
    public static Context context;
    public static ListView orderformlistview;
    public static  ArrayList<OrderFormDetails> orderFormDetails = new ArrayList<OrderFormDetails>();
    public static ArrayList<OrderFormDetails> cartorderFormDetails = new ArrayList<OrderFormDetails>();
    FloatingActionButton fabgroupitem;
    ImageButton orderlogout,goback;
    String[] itemgroupcode,itemgroupname,itemgroupnametamil;
    String[] itemsubgroupcode,itemsubgroupname,itemsubgroupnametamil;
    TextView orderformcartreview,txtitemgroup,txtitemsubgroup;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_form);

        context = this;
        orderformlistview = (ListView)findViewById(R.id.orderformlistview);
        orderlogout = (ImageButton)findViewById(R.id.orderlogout);
        goback = (ImageButton)findViewById(R.id.goback);
        orderformcartreview = (TextView)findViewById(R.id.orderformcartreview);
        txtitemgroup = (TextView)findViewById(R.id.txtitemgroup);
        txtitemsubgroup = (TextView)findViewById(R.id.txtitemsubgroup);
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



                if(orderFormDetails.size() > 0) {
                    itemcodes.clear();
                    for (int i = 0; i < orderformlistview.getChildCount(); i++) {
                        View listRow = orderformlistview.getChildAt(i);
                        EditText getlistqty = (EditText) listRow.findViewById(R.id.orderqty);
                        TextView getlisttotal = (TextView) listRow.findViewById(R.id.ordertotalstock);
                        TextView getlistname = (TextView) listRow.findViewById(R.id.orderitemname);

                        String getitemlistqty = getlistqty.getText().toString();
                        String getitemlisttotal = getlisttotal.getText().toString();
                        String getitemlistname = getlistname.getText().toString().trim();

                        String itemcode="0";
                        if(!getitemlistqty.equals("") && !getitemlistqty.equals("null") && !getitemlistqty.equals(null)){
                            if(Integer.parseInt(getitemlistqty)>0){
                                // itemcodes.add(orderFormDetails.get(i).getItemcode());
                                itemcode=orderFormDetails.get(i).getItemcode();
                            }

                            //Toast.makeText(context,itemcode,Toast.LENGTH_SHORT).show();

                            if(cartorderFormDetails.size()>0) {
                                for (int j = 0; j < cartorderFormDetails.size(); j++) {
                                    if (cartorderFormDetails.get(j).getItemcode().equals(itemcode)) {
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
                                if(Integer.parseInt(getitemlistqty)>0){
                                    qtyflag = qtyflag +1;
                                }
                            }
                        }


                        if(!getitemlistqty.equals("") && !getitemlistqty.equals(null)){
                            if(getitemlisttotal.trim().equals("") || getitemlisttotal.trim().equals("null")){
                                getitemlisttotal = "0";
                            }
                            if(Double.parseDouble(getitemlistqty)>0 && Double.parseDouble(getitemlisttotal)<=0.0){
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
                                    if(cartorderFormDetails.size()>0){
                                        orderFormDetails.clear();
                                        OrderListBaseAdapter adapter = new OrderListBaseAdapter(context, orderFormDetails);
                                        orderformlistview.setAdapter(adapter);
                                        Intent i = new Intent(context, OrderFormCartActivity.class);
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
                    if(cartorderFormDetails.size()>0){
                        orderFormDetails.clear();
                        OrderListBaseAdapter adapter = new OrderListBaseAdapter(context, orderFormDetails);
                        orderformlistview.setAdapter(adapter);
                        Intent i = new Intent(context, OrderFormCartActivity.class);
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
        carttotamount.setText(String.valueOf(cartorderFormDetails.size()));

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

                    if(orderFormDetails.size()>0){
                        for(int i=0;i<orderFormDetails.size();i++){
                            if(!(orderFormDetails.get(i).getQty().equals("")) &&
                                    !(orderFormDetails.get(i).getQty().equals("0")) &&
                                    !(orderFormDetails.get(i).getQty().equals("null"))
                                    && !(orderFormDetails.get(i).getQty().equals(null)))
                            {
                                if(Double.parseDouble(orderFormDetails.get(i).getQty())>0) {
                                    for (int j = 0; j < cartorderFormDetails.size(); j++) {
                                        if (orderFormDetails.get(i).getItemcode().equals
                                                (cartorderFormDetails.get(j).getItemcode())) {
                                            cartorderFormDetails.remove(j);
                                        }
                                    }
                                    cartorderFormDetails.add(new OrderFormDetails(orderFormDetails.get(i).getItemcode(), orderFormDetails.get(i).getItemname(),
                                            orderFormDetails.get(i).getItemnametamil(), orderFormDetails.get(i).getUnitweight(),
                                            orderFormDetails.get(i).getCompanycode(), orderFormDetails.get(i).getColourcode(),
                                            orderFormDetails.get(i).getUnitname(), orderFormDetails.get(i).getHsn(),
                                            orderFormDetails.get(i).getTax(), orderFormDetails.get(i).getClosingstk(),
                                            orderFormDetails.get(i).getQty(), String.valueOf(i + 1),
                                            orderFormDetails.get(i).getUppweight(),orderFormDetails.get(i).getStatus()));
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
                        carttotamount.setText(String.valueOf(cartorderFormDetails.size()));
                    }else{
                        Toast toast = Toast.makeText(getApplicationContext(),"Please enter valid quantity", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        //Toast.makeText(getApplicationContext(),"Please enter valid quantity",Toast.LENGTH_SHORT).show();
                    }


            }
        });

      /*  //Set Item Group
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
                }
                txtitemgroup.setText(itemgroupnametamil[0]);
                getitemgroupcode = itemgroupcode[0];
                getitemsubgroupcode="0";
                txtitemsubgroup.setText("");
                txtitemsubgroup.setHint("Item Sub-Group");
                GetItem();
            }else{
                txtitemgroup.setText("");
                getitemgroupcode = "0";
                txtitemgroup.setHint("Item Group");
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
        switch (id){
            case R.id.fabgroupitem:
                animateFAB();
                break;
        }
    }
    public void animateFAB(){
            boolean checkitemflag = false;
            int qtyflag = 0;
            int colorCode=0;
            ArrayList<String> itemcodes= new ArrayList<>();



            if(orderFormDetails.size() > 0) {
                itemcodes.clear();
                for (int i = 0; i < orderformlistview.getChildCount(); i++) {
                    View listRow = orderformlistview.getChildAt(i);
                    EditText getlistqty = (EditText) listRow.findViewById(R.id.orderqty);
                    TextView getlisttotal = (TextView) listRow.findViewById(R.id.ordertotalstock);
                    TextView getlistname = (TextView) listRow.findViewById(R.id.orderitemname);

                    String getitemlistqty = getlistqty.getText().toString();
                    String getitemlisttotal = getlisttotal.getText().toString();
                    String getitemlistname = getlistname.getText().toString().trim();

                    String itemcode="0";
                    if(!getitemlistqty.equals("") && !getitemlistqty.equals("null") && !getitemlistqty.equals(null)){
                        if(Integer.parseInt(getitemlistqty)>0){
                           // itemcodes.add(orderFormDetails.get(i).getItemcode());
                            itemcode=orderFormDetails.get(i).getItemcode();
                        }

                        //Toast.makeText(context,itemcode,Toast.LENGTH_SHORT).show();

                       if(cartorderFormDetails.size()>0) {
                            for (int j = 0; j < cartorderFormDetails.size(); j++) {
                                if (cartorderFormDetails.get(j).getItemcode().equals(itemcode)) {
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
                           if(Integer.parseInt(getitemlistqty)>0){
                               qtyflag = qtyflag +1;
                           }
                       }
                    }


                    if(!getitemlistqty.equals("") && !getitemlistqty.equals(null)){
                        if(getitemlisttotal.trim().equals("") || getitemlisttotal.trim().equals("null")){
                            getitemlisttotal = "0";
                        }
                        if(Double.parseDouble(getitemlistqty)>0 && Double.parseDouble(getitemlisttotal)<=0.0){
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

            if(drilldownitem.equals("group")) {
                isopenshowpopup=true;
                LayoutInflater inflater = (LayoutInflater) OrderFormActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.grouppopup, null);
                window = new PopupWindow(layout, 650, 1000, false);

                expList = (ExpandableListView) layout.findViewById(R.id.expandible_listview);
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
                setChildItems();

                expandableAdapter = new ExpandableAdapter(this, listDataHEader, listhash);
                expList.setAdapter(expandableAdapter);

                expList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                    int previousGroup = -1;

                    @Override
                    public void onGroupExpand(int groupPosition) {
                        if (groupPosition != previousGroup)
                            expList.collapseGroup(previousGroup);
                        previousGroup = groupPosition;
                    }
                });
            }else{
                isopenshowpopup=true;
                LayoutInflater inflater = (LayoutInflater) OrderFormActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.subgrouppopup, null);
                window = new PopupWindow(layout, 650, 1000, false);

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
                GetSubGroupList();
            }

        }catch (Exception e){

        }
    }


    public  void GetSubGroupList(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetSubGroupDBSalesReturn();
            if(Cur.getCount()>0) {
                SubGroupCode = new String[Cur.getCount()];
                SubGroupName = new String[Cur.getCount()];
                SubGroupNameTamil = new String[Cur.getCount()];
                for(int i=0;i<Cur.getCount();i++){
                    SubGroupCode[i] = Cur.getString(0);
                    SubGroupName[i] = Cur.getString(1);
                    SubGroupNameTamil[i] = Cur.getString(2);
                    Cur.moveToNext();
                }

                SalesSubGroupAdapter adapter = new SalesSubGroupAdapter(context);
                lv_subgroup.setAdapter(adapter);
            }else{
                Toast toast = Toast.makeText(getApplicationContext(), "No Subgroup Available", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //Toast.makeText(getApplicationContext(),"No Subgroup Available",Toast.LENGTH_SHORT).show();
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
                    GetItem();
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
                    GetItem();
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
                ItemGroupAdapter adapter = new ItemGroupAdapter(context);
                lv_GroupList.setAdapter(adapter);
                itemgroupdialog.show();
            }else{

                Toast toast = Toast.makeText(getApplicationContext(),"No item group available", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
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
                ItemSubGroupAdapter adapter = new ItemSubGroupAdapter(context);
                lv_SubGroupList.setAdapter(adapter);
                itemsubgroupdialog.show();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(),"No item sub group in this item group", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
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
    public static void GetItem(){
        DataBaseAdapter objdatabaseadapter = null;
        Cursor Cur=null;
        try{
            objdatabaseadapter = new DataBaseAdapter(context);
            objdatabaseadapter.open();
            Cur = objdatabaseadapter.GetOrderItemDB(getitemsgroupcode,getstaticsubcode);
            if(Cur.getCount()>0) {
                txt_nodataavailable.setVisibility(View.GONE);
                orderFormDetails.clear();
                for(int i=0;i<Cur.getCount();i++){
                    orderFormDetails.add(new OrderFormDetails(Cur.getString(0),Cur.getString(1),
                            Cur.getString(2),Cur.getString(3),Cur.getString(4),
                            Cur.getString(5),Cur.getString(6),
                            Cur.getString(7),Cur.getString(8),Cur.getString(9),
                            "", String.valueOf(i+1),Cur.getString(10),"" ));
                    Cur.moveToNext();
                }
                //Set ITEMQTY
                for (int i = 0; i < cartorderFormDetails.size(); i++) {
                    for (int j = 0; j < orderFormDetails.size();j++) {
                        if (cartorderFormDetails.get(i).getItemcode().equals(orderFormDetails.get(j).getItemcode())) {
                            orderFormDetails.get(j).setQty(cartorderFormDetails.get(i).getQty());
                        }
                    }
                }
                 adapter = new OrderListBaseAdapter(context, orderFormDetails);
                orderformlistview.setAdapter(adapter);
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
                    txtitemsubgroup.setText("");
                    txtitemsubgroup.setHint("Item Sub-Group");
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


    static class OrderListBaseAdapter extends BaseAdapter {
        LayoutInflater inflater;
        Context context;
        ArrayList<OrderFormDetails> myList;
        DecimalFormat dft = new DecimalFormat("0.00");

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
                convertView = inflater.inflate(R.layout.orderlist, parent, false);
                mHolder = new ViewHolder();
                try {
                    mHolder.ordersno = (TextView) convertView.findViewById(R.id.ordersno);
                    mHolder.orderitemname = (TextView) convertView.findViewById(R.id.orderitemname);
                    mHolder.orderclosingstock = (TextView) convertView.findViewById(R.id.orderclosingstock);
                    mHolder.orderqty = (TextView) convertView.findViewById(R.id.orderqty);
                    mHolder.listLL = (LinearLayout) convertView.findViewById(R.id.listLL);
                    mHolder.card_view = (CardView)convertView.findViewById(R.id.card_view);
                    mHolder.orderunit = (TextView)convertView.findViewById(R.id.orderunit);
                    // mHolder.itemdelete = (ImageView)convertView.findViewById(R.id.itemdelete);
                    mHolder.ordertotalstock = (TextView)convertView.findViewById(R.id.ordertotalstock);
                } catch (Exception e) {
                    Log.i("Orderitem", e.toString());
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
                final OrderFormDetails currentListData = getItem(position);
                mHolder.ordersno.setText(currentListData.getSno());
                if (!(currentListData.getItemnametamil().equals(""))
                        && !(currentListData.getItemnametamil()).equals("null")
                        && !((currentListData.getItemnametamil()).equals(null))) {
                    mHolder.orderitemname.setText(currentListData.getItemnametamil() + " - " +
                            currentListData.getUnitname());
                }else{
                    mHolder.orderitemname.setText(currentListData.getItemname() + " - " +
                            currentListData.getUnitname());
                }
                if(!currentListData.getClosingstk().equals("null") && !currentListData.getClosingstk().equals(null)){
//                if(Double.parseDouble(currentListData.getClosingstk()) > 0){
//                    mHolder.itemdelete.setVisibility(View.VISIBLE);
//                }else{
//                    mHolder.itemdelete.setVisibility(View.GONE);
//                }
                    mHolder.orderclosingstock.setText(currentListData.getClosingstk());
                }else{
                    //   mHolder.itemdelete.setVisibility(View.GONE);
                    mHolder.orderclosingstock.setText(dft.format(Double.parseDouble("0")));
                }


                // mHolder.orderunit.setText(currentListData.getUnitname());

                //Set Quantity
                if (!currentListData.getQty().equals("") && !currentListData.getQty().equals("0")
                        && !currentListData.getQty().equals(null) && !currentListData.getQty().equals("null")) {
                    mHolder.orderqty.setText(String.valueOf(currentListData.getQty()));
                    //calculate total
                    double a = Double.parseDouble(currentListData.getClosingstk());
                    double b = Double.parseDouble(mHolder.orderqty.getText().toString());
                    double c=a+b;
                    int getval = (int) c;
                    mHolder.ordertotalstock.setText(String.valueOf(getval));
                    currentListData.setQty( mHolder.orderqty.getText().toString());
                    mHolder.ordertotalstock.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                }
                currentListData.setStatus("");
                mHolder.orderitemname.setTextColor(Color.parseColor(currentListData.getColourcode()));

                mHolder.orderqty.addTextChangedListener(new TextWatcher() {
                    public void onTextChanged(CharSequence s, int start, int before,
                                              int count) {
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                  int after) {
                    }

                    public void afterTextChanged(Editable s) {
                        if( !mHolder.orderqty.getText().toString().equals("") &&
                                !mHolder.orderqty.getText().toString().equals(null) &&
                                !mHolder.orderqty.getText().toString().equals("null") &&
                                !mHolder.orderqty.getText().toString().equals("0")){
                            double a = Double.parseDouble(currentListData.getClosingstk());
                            double b = Double.parseDouble(mHolder.orderqty.getText().toString());
                            double c=a+b;
                            int getval = (int) c;
                            mHolder.ordertotalstock.setText(String.valueOf(getval));

                            mHolder.ordertotalstock.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                            for (int j = 0; j < OrderFormActivity.cartorderFormDetails.size(); j++) {
                                if (myList.get(position).getItemcode().equals
                                        (OrderFormActivity.cartorderFormDetails.get(j).getItemcode())) {
                                    if(Integer.parseInt(mHolder.orderqty.getText().toString()) !=
                                            Integer.parseInt(OrderFormActivity.cartorderFormDetails.get(j).getQty())){
                                        OrderFormActivity.cartorderFormDetails.remove(j);
                                    }

                                }
                            }
                            OrderFormActivity.carttotamount.setText(String.valueOf(OrderFormActivity.cartorderFormDetails.size()));

                        }else{
                            for (int j = 0; j < OrderFormActivity.cartorderFormDetails.size(); j++) {
                                if (myList.get(position).getItemcode().equals
                                        (OrderFormActivity.cartorderFormDetails.get(j).getItemcode())) {
                                    OrderFormActivity.cartorderFormDetails.remove(j);
                                }
                            }
                            OrderFormActivity.carttotamount.setText(String.valueOf(OrderFormActivity.cartorderFormDetails.size()));
                            mHolder.ordertotalstock.setText("");
                            currentListData.setQty("0");
                            mHolder.ordertotalstock.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                        }
                    }
                });

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
                mHolder.ordertotalstock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!mHolder.orderqty.getText().toString().equals("") &&
                                !mHolder.orderqty.getText().toString().equals(null) &&
                                !mHolder.orderqty.getText().toString().equals("null") &&
                                !mHolder.orderqty.getText().toString().equals("0") ) {
                            //calculate total
                            double a = Double.parseDouble(currentListData.getClosingstk());
                            double b = Double.parseDouble(mHolder.orderqty.getText().toString());
                            double c=a+b;
                            int getval = (int) c;
                            mHolder.ordertotalstock.setText(String.valueOf(getval));

                            currentListData.setQty( mHolder.orderqty.getText().toString());
                            boolean iscart = false;
                            if (orderFormDetails.size() > 0) {
                                mHolder.ordertotalstock.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                                ;
                                for (int i = 0; i < orderFormDetails.size(); i++) {
                                    if (!(orderFormDetails.get(i).getQty().equals("")) &&
                                            !(orderFormDetails.get(i).getQty().equals("0")) &&
                                            !(orderFormDetails.get(i).getQty().equals("null"))
                                            && !(orderFormDetails.get(i).getQty().equals(null))) {
                                        if (Double.parseDouble(orderFormDetails.get(i).getQty()) > 0) {
                                            for (int j = 0; j < cartorderFormDetails.size(); j++) {
                                                if (orderFormDetails.get(i).getItemcode().equals
                                                        (cartorderFormDetails.get(j).getItemcode())) {
                                                    cartorderFormDetails.remove(j);
                                                }
                                            }
                                            cartorderFormDetails.add(new OrderFormDetails(orderFormDetails.get(i).getItemcode(), orderFormDetails.get(i).getItemname(),
                                                    orderFormDetails.get(i).getItemnametamil(), orderFormDetails.get(i).getUnitweight(),
                                                    orderFormDetails.get(i).getCompanycode(), orderFormDetails.get(i).getColourcode(),
                                                    orderFormDetails.get(i).getUnitname(), orderFormDetails.get(i).getHsn(),
                                                    orderFormDetails.get(i).getTax(), orderFormDetails.get(i).getClosingstk(),
                                                    orderFormDetails.get(i).getQty(), String.valueOf(i + 1),
                                                    orderFormDetails.get(i).getUppweight(), orderFormDetails.get(i).getStatus()));
                                            iscart = true;
                                        }
                                    }
                                }
                                carttotamount.setText(String.valueOf(cartorderFormDetails.size()));
                            }
                        }else{
                            for (int j = 0; j < OrderFormActivity.cartorderFormDetails.size(); j++) {
                                if (myList.get(position).getItemcode().equals
                                        (OrderFormActivity.cartorderFormDetails.get(j).getItemcode())) {
                                    OrderFormActivity.cartorderFormDetails.remove(j);
                                }
                            }
                            OrderFormActivity.carttotamount.setText(String.valueOf(OrderFormActivity.cartorderFormDetails.size()));
                            mHolder.ordertotalstock.setText("");
                            currentListData.setQty("0");
                            mHolder.ordertotalstock.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
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
            return convertView;
        }
        private class ViewHolder {
            TextView ordersno,orderitemname,orderclosingstock,orderqty,orderunit,ordertotalstock;
            LinearLayout listLL;
            CardView card_view;
            ImageView itemdelete;

        }

    }
    /************END BASE ADAPTER*************/
    public void goBack(View v) {
        int qtyflag = 0;
        if(orderFormDetails.size() > 0) {
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
                            if(cartorderFormDetails.size() > 0 ){
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Confirmation");
                                builder.setMessage("Are you sure you want to clear cart?")
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                cartorderFormDetails.clear();
                                                orderFormDetails.clear();
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
                                cartorderFormDetails.clear();
                                orderFormDetails.clear();
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
        }else if(cartorderFormDetails.size() > 0 ){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Confirmation");
            builder.setMessage("Are you sure you want to clear cart?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            cartorderFormDetails.clear();
                            orderFormDetails.clear();
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
            cartorderFormDetails.clear();
            orderFormDetails.clear();
            LoginActivity.ismenuopen=true;
            Intent i = new Intent(context, MenuActivity.class);
            startActivity(i);
        }

    }
    @Override
    public void onBackPressed() {
        goBack(null);
    }
}
