package Adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bayarchain.R;

import org.w3c.dom.Text;

import Model.Contract;

/**
 * Created by Adi_711 on 30-05-2016.
 */
public class CustomListAdapter2 extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Contract> schoolList;
    private String tab;

    public CustomListAdapter2(Activity activity, List<Contract> schoolList, String tab){
        super();
        this.activity = activity;
        this.schoolList = schoolList;
        this.tab = tab;
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


    public View getView(int position, View convertView, ViewGroup parent){
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.contract_list_display, null);
        Contract object = schoolList.get(position);

        TextView Created_with = (TextView) convertView.findViewById(R.id.contractID);
        TextView event= (TextView) convertView.findViewById(R.id.event_naa);
        TextView contract_amount = (TextView) convertView.findViewById(R.id.amount);
        TextView amount_label = (TextView)convertView.findViewById(R.id.text17);//DONOT ToUCH OnlY FOR UI
        TextView createTitle = (TextView)convertView.findViewById(R.id.textView15);
        TextView principal_amount = (TextView)convertView.findViewById(R.id.principal_amt);


        Created_with.setText(object.getCreator_username());
        event.setText("Event Name: " + object.getContract_event());
        principal_amount.setText("Principal Amount: " + object.getContract_principal() );


        if(object.getContract_status().toString().trim().equals("1")){
            amount_label.setText("Settled!!");
            amount_label.setTextColor(Color.parseColor("#1b5e20"));
        }
        contract_amount.setText("$ " + object.getContract_amount());

        if(tab.equals("credit")){
            createTitle.setText("You will get from..");
            contract_amount.setTextColor(Color.parseColor("#1b5e20"));
            amount_label.setTextColor(Color.parseColor("#4caf50"));
        }
        else if(tab.equals("debit")){
            createTitle.setText("You owe..");

            contract_amount.setTextColor(Color.parseColor("#b71c1c"));
            amount_label.setTextColor(Color.parseColor("#e53935"));
        }
        return convertView;
    }
    //public View getView(int position, View convertView, ViewGroup parent) {
    // TODO Auto-generated method stub

    //}

}