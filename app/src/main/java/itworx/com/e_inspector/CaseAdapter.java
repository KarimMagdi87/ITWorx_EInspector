package itworx.com.e_inspector;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Adapter to bind a ToDoItem List to a view
 */
public class CaseAdapter extends ArrayAdapter<Case> {

    Context mContext;

    int mLayoutResourceId;
    ArrayList<Case> mLst;

    public CaseAdapter(Context context, int layoutResourceId, ArrayList<Case> lst) {
        super(context, layoutResourceId, lst);
        mContext = context;
        mLayoutResourceId = layoutResourceId;
        mLst = lst;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        final Case currentItem = mLst.get(position);
        Log.d("","item:"+currentItem);
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item, parent, false);
        TextView tv_title = (TextView) rowView.findViewById(R.id.title);
        TextView tv_name = (TextView) rowView.findViewById(R.id.cname);
        TextView tv_agentId = (TextView) rowView.findViewById(R.id.agentid);
        TextView tv_status = (TextView) rowView.findViewById(R.id.status);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);

        tv_title.setText(currentItem.title);
        tv_name.setText(currentItem.caseNumber);
        tv_status.setText(currentItem.status);
        tv_agentId.setText(currentItem.description);
        Log.d("","einspector:blopurl:"+currentItem.incidentImageURI);
        if(currentItem.incidentImageURI==null)
            currentItem.incidentImageURI= "http://www.test.com/test.jpg";
        else if(currentItem.incidentImageURI.equals(""))
            currentItem.incidentImageURI= "http://www.test.com/test.jpg";
        Picasso.with(mContext)
                .load(currentItem.incidentImageURI)
                .placeholder(R.drawable.picasedefault)
                .into(imageView);
       return rowView;
    }
}