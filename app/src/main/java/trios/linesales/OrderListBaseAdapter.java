package trios.linesales;
/*
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

class OrderListBaseAdapter extends BaseAdapter {
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

            } else {
                mHolder.orderqty.setText("");
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
                            !mHolder.orderqty.getText().toString().equals("null")){
                        double a = Double.parseDouble(currentListData.getClosingstk());
                        double b = Double.parseDouble(mHolder.orderqty.getText().toString());
                        double c=a+b;
                        int getval = (int) c;
                        mHolder.ordertotalstock.setText(String.valueOf(getval));

                        currentListData.setQty( mHolder.orderqty.getText().toString());
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
                    }
                }
            });

           *//* mHolder.itemdelete.setOnClickListener(new View.OnClickListener() {
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
                                    *//**//*for (int j = 0; j < OrderFormActivity.cartorderFormDetails.size(); j++) {
                                        if (OrderFormActivity.orderFormDetails.get(i).getItemcode().equals
                                                (cartorderFormDetails.get(j).getItemcode())) {
                                            OrderFormActivity.cartorderFormDetails.remove(j);
                                        }
                                    }*//**//*
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
            });*//*

            //Total click listener
            mHolder.ordertotalstock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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

}*/
