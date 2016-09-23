package com.bayarchain.Adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bayarchain.Model.Contract;
import com.bayarchain.R;
import com.google.android.gms.vision.text.Text;

import java.util.List;

/**
 * Created by Aditya Aggarwal on 16-09-2016.
 */
public class CustomContractAdapter extends RecyclerView.Adapter<CustomContractAdapter.MyViewHolder> {
    private List<Contract> contractList;
    private String credeb;
    TextView Created_with, event, contract_amount, amount_label, createTitle, principal_amount,arrow,date;
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.contract_list_display, parent, false);

        return new MyViewHolder(itemView);
    }

    public CustomContractAdapter(List<Contract> contractList, String credeb) {
        this.contractList = contractList;
        this.credeb = credeb;
    }
    @Override
    public void onBindViewHolder(MyViewHolder convertView, int position) {
        Contract object = contractList.get(position);

        Created_with.setText(object.getCreator_username());
        event.setText("Event Name: " + object.getContract_event());
        principal_amount.setText("Principal Amount: " + object.getContract_principal() );
        date.setText("Date: " + object.getContract_timestamp());


        contract_amount.setText("$ " + object.getContract_amount());

        if(credeb.equals("credit")){
            createTitle.setText("You will get from..");
            arrow.setVisibility(View.INVISIBLE);
            contract_amount.setTextColor(Color.parseColor("#1b5e20"));
            amount_label.setTextColor(Color.parseColor("#4caf50"));
        }
        else if(credeb.equals("debit")){
            createTitle.setText("You owe..");
            contract_amount.setTextColor(Color.parseColor("#b71c1c"));
            amount_label.setTextColor(Color.parseColor("#e53935"));
        }
        if(object.getContract_status().toString().trim().equals("1")){
            amount_label.setText("Settled!!");
            amount_label.setTextColor(Color.parseColor("#1b5e20"));
        }
        else if(object.getContract_status().toString().trim().equals("2")){
            amount_label.setText("Rejected");
            amount_label.setBackgroundColor(Color.parseColor("#FFDCDCDC"));
            amount_label.setTextColor(Color.parseColor("#b71c1c"));

            createTitle.setText("Please Create Again");
            createTitle.setTextColor(Color.parseColor("#b71c1c"));
            convertView.itemView.setBackgroundColor(Color.parseColor("#0FE6E6E6"));
        }
    }

    @Override
    public int getItemCount() {
        return contractList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(View convertView) {
            super(convertView);

            arrow = (TextView)convertView.findViewById(R.id.text20);
            Created_with = (TextView) convertView.findViewById(R.id.contractID);
            event= (TextView) convertView.findViewById(R.id.event_naa);
            contract_amount = (TextView) convertView.findViewById(R.id.amount);
            amount_label = (TextView)convertView.findViewById(R.id.text17);//DONOT ToUCH OnlY FOR UI
            createTitle = (TextView)convertView.findViewById(R.id.textView15);
            principal_amount = (TextView)convertView.findViewById(R.id.principal_amt);
            date = (TextView)convertView.findViewById(R.id.contractDate);
        }
    }
}
