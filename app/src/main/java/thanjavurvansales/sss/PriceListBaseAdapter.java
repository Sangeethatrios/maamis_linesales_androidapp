package thanjavurvansales.sss;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

class PriceListBaseAdapter  extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    ArrayList<PriceDetails> myList;
    DecimalFormat dft = new DecimalFormat("0.00");

    public PriceListBaseAdapter( Context context,ArrayList<PriceDetails> myList) {
        this.myList = myList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public PriceDetails getItem(int position) {
        return (PriceDetails) myList.get(position);
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
            convertView = inflater.inflate(R.layout.pricelist, parent, false);
            mHolder = new ViewHolder();
            try {
                mHolder.pricesno = (TextView) convertView.findViewById(R.id.pricesno);
                mHolder.priceitemname = (TextView) convertView.findViewById(R.id.priceitemname);
                mHolder.priceunit = (TextView) convertView.findViewById(R.id.priceunit);
                mHolder.priceamount = (TextView) convertView.findViewById(R.id.priceamount);
                mHolder.listLL = (LinearLayout) convertView.findViewById(R.id.listLL);
                mHolder.card_view = (CardView)convertView.findViewById(R.id.card_view);
                mHolder.pricearrow = (ImageView)convertView.findViewById(R.id.pricearrow);
                mHolder.hsnandtax = (TextView)convertView.findViewById(R.id.hsnandtax);
                mHolder.orderpricearrow = (ImageView) convertView.findViewById(R.id.orderpricearrow);
                mHolder.orderpriceamount= (TextView)convertView.findViewById(R.id.orderpriceamount);
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
            PriceDetails currentListData = getItem(position);
            mHolder.pricesno.setText(String.valueOf(position+1));
            if(!currentListData.getItemnametamil().equals("") || !currentListData.getItemnametamil().equals("null")
                || !currentListData.getItemnametamil().equals(null) ) {
                mHolder.priceitemname.setText(currentListData.getItemnametamil());
            }else{
                mHolder.priceitemname.setText(currentListData.getItemname());
            }
            mHolder.priceamount.setText(dft.format(Double.parseDouble(String.valueOf(currentListData.getNewprice()))));
            mHolder.orderpriceamount.setText(dft.format(Double.parseDouble(String.valueOf(currentListData.getNewOrderprice()))));
            mHolder.priceunit.setText("Per "+currentListData.getUnitname());
            /*if (position % 2 == 1) {
                mHolder.card_view.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            } else {
                mHolder.card_view.setCardBackgroundColor(Color.parseColor("#caeaf3"));
            }*/

            if (currentListData.getPricetatus().equals("pricechanged")) {
                mHolder.card_view.setCardBackgroundColor(Color.parseColor("#1daad1"));
            } else {
                mHolder.card_view.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            }


            mHolder.priceitemname.setTextColor(Color.parseColor(currentListData.getColourcode()));
            if(Double.parseDouble(String.valueOf(currentListData.getOldprice())) >
                    Double.parseDouble(String.valueOf(currentListData.getNewprice())) ){
                mHolder.pricearrow.setImageResource(R.drawable.ic_arrow_downward_small);
            }else{
                mHolder.pricearrow.setImageResource(R.drawable.ic_arrow_upward_black_small_24dp);
            }

            if(Double.parseDouble(String.valueOf(currentListData.getOldOrderprice())) >
                    Double.parseDouble(String.valueOf(currentListData.getNewOrderprice())) ){
                mHolder.orderpricearrow.setImageResource(R.drawable.ic_arrow_downward_small);
            }else{
                mHolder.orderpricearrow.setImageResource(R.drawable.ic_arrow_upward_black_small_24dp);
            }

            mHolder.priceitemname.setTextColor(Color.parseColor(currentListData.getColourcode()));
            mHolder.hsnandtax.setText(currentListData.getHsn()+" @ "+currentListData.getTax()+"%");
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
        TextView pricesno,priceitemname,priceunit,priceamount,hsnandtax,orderpriceamount;
        LinearLayout listLL;
        CardView card_view;
        ImageView pricearrow,orderpricearrow;

    }

}