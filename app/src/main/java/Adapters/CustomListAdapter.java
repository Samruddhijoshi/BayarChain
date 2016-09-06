package Adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.bayarchain.R;

import Model.ID;

/**
 * Created by Adi_711 on 30-05-2016.
 */
    public class CustomListAdapter extends BaseAdapter {

        private Activity activity;
        private LayoutInflater inflater;
        private List<ID> schoolList;

        public CustomListAdapter(Activity activity, List<ID> schoolList){
            super();
            this.activity = activity;
            this.schoolList = schoolList;
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return schoolList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return schoolList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

    @Override


    public View getView(final int position, View convertView, ViewGroup parent){
            if (inflater == null)
                inflater = (LayoutInflater) activity
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null)
                convertView = inflater.inflate(R.layout.user_list_display, null);
            ID object = schoolList.get(position);

            TextView username = (TextView) convertView.findViewById(R.id.username1);
            TextView address = (TextView) convertView.findViewById(R.id.address1);
            Button delete_btn = (Button)convertView.findViewById(R.id.delete_list_btn);

        // TextView fullName = (TextView) convertView.findViewById(R.id.fullname1);

            username.setText(object.getUsername());
            address.setText("Unique ID: " + object.getAddress());
            //fullName.setText(object.getName());

            return convertView;
        }
        //public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

        //}

    }