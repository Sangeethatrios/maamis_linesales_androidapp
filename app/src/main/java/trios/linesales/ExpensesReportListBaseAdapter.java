package trios.linesales;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

class ExpensesReportListBaseAdapter extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    ArrayList<ExpensesDetails> myList;
    DecimalFormat dft = new DecimalFormat("0.00");

    public ExpensesReportListBaseAdapter(Context context, ArrayList<ExpensesDetails> expenseslist) {
        this.myList = expenseslist;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public ExpensesDetails getItem(int position) {
        return (ExpensesDetails) myList.get(position);
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

        ViewHolder mHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.expenseslist, parent, false);
            mHolder = new ViewHolder();
            try {
                mHolder.expenseslistsno = (TextView) convertView.findViewById(R.id.expenseslistsno);
                mHolder.expenseslistexpenses = (TextView) convertView.findViewById(R.id.expenseslistexpenses);
                mHolder.expenseslistamount = (TextView) convertView.findViewById(R.id.expenseslistamount);
                mHolder.expenseslistdate = (TextView) convertView.findViewById(R.id.expenseslistdate);
                mHolder.listLL = (LinearLayout) convertView.findViewById(R.id.listLL);
                mHolder.card_view = (CardView)convertView.findViewById(R.id.card_view);
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
            ExpensesDetails currentListData = getItem(position);
            mHolder.expenseslistsno.setText(currentListData.getSno());
            mHolder.expenseslistexpenses.setText(currentListData.getExpensesheadname());
            mHolder.expenseslistamount.setText(dft.format(Double.parseDouble(currentListData.getAmount())));
            mHolder.expenseslistdate.setText(currentListData.getTransactiondate());
            calculateTotalAmount();
            if (position % 2 == 1) {
                mHolder.card_view.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            } else {
                mHolder.card_view.setCardBackgroundColor(Color.parseColor("#caeaf3"));
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
        TextView expenseslistsno, expenseslistexpenses,expenseslistamount,expenseslistdate;
        LinearLayout listLL;
        CardView card_view;

    }
    public void calculateTotalAmount() {
        Double addamt = 0.0;
        try {

            for (int i = 0; i < myList.size(); i++) {
                Double total = Double.parseDouble(myList.get(i).getAmount());
                addamt = addamt + total;
            }
            DecimalFormat df = new DecimalFormat("#.00");
            Double gettripadavance = Double.parseDouble(ExpenseReportActivity.expensestripadvance.getText().toString());
            Double getbalamt = gettripadavance-addamt;

            ExpenseReportActivity.expensestotalamt.setText("\u20B9 "+df.format(addamt));
            ExpenseReportActivity.expensesremainingamt.setText("\u20B9 "+df.format(getbalamt));
        } catch (Exception ex) {
            DataBaseAdapter mDbErrHelper = new DataBaseAdapter(context);
            mDbErrHelper.open();
            String geterrror = ex.toString();
            mDbErrHelper.insertErrorLog(geterrror.replace("'"," "), this.getClass().getSimpleName(), String.valueOf(Thread.currentThread().getStackTrace()[1].getLineNumber()));
            mDbErrHelper.close();
        }
    }

}
