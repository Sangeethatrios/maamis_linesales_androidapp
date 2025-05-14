package trios.linesales;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

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

        final String childtext = (String)getChild(i,j);
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.child_layout, null);
        }

        TextView child_textvew = (TextView) view.findViewById(R.id.child_txt);
        child_textvew.setText(childtext);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // objsales.GetItems();
            }
        });
        return  view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}