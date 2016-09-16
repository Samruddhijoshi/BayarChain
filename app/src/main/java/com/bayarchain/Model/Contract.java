package com.bayarchain.Model;

/**
 * Created by Adi_711 on 31-05-2016.
 */
public class Contract implements Comparable<Contract>{
    String creator_username;
    String creator_password;
    String creator_address;
    String contract_amount;
    String contract_address;
    String contract_timestamp;
    String contract_principal;

    public String getContract_event() {
        return contract_event;
    }

    public void setContract_event(String contract_event) {
        this.contract_event = contract_event;
    }

    String contract_event;

    public String getContract_principal() {
        return contract_principal;
    }

    public void setContract_principal(String contract_principal) {
        this.contract_principal = contract_principal;
    }

    public String getContract_timestamp() {
        return contract_timestamp;
    }

    public void setContract_timestamp(String contract_timestamp) {
        this.contract_timestamp = contract_timestamp;
    }

    public String getContract_status() {
        return contract_status;
    }

    public void setContract_status(String contract_status) {
        this.contract_status = contract_status;
    }

    String contract_status;

    public String getReceiver_address() {
        return receiver_address;
    }

    public void setReceiver_address(String receiver_address) {
        this.receiver_address = receiver_address;
    }

    String receiver_username;

    public String getReceiver_password() {
        return receiver_password;
    }

    public void setReceiver_password(String receiver_password) {
        this.receiver_password = receiver_password;
    }

    public String getReceiver_username() {
        return receiver_username;
    }

    public void setReceiver_username(String receiver_username) {
        this.receiver_username = receiver_username;
    }

    public String getContract_address() {
        return contract_address;
    }

    public void setContract_address(String contract_address) {
        this.contract_address = contract_address;
    }

    public String getContract_amount() {
        return contract_amount;
    }

    public void setContract_amount(String contract_amount) {
        this.contract_amount = contract_amount;
    }

    public String getCreator_address() {
        return creator_address;
    }

    public void setCreator_address(String creator_address) {
        this.creator_address = creator_address;
    }

    public String getCreator_password() {
        return creator_password;
    }

    public void setCreator_password(String creator_password) {
        this.creator_password = creator_password;
    }

    public String getCreator_username() {
        return creator_username;
    }

    public void setCreator_username(String creator_username) {
        this.creator_username = creator_username;
    }

    String receiver_password;
    String receiver_address;


    @Override
    public int compareTo(Contract contract) {
        return 0;
    }
}
