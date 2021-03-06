package com.bayarchain.Push;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Content implements Serializable {

    public List<String> registration_ids;
    public Map<String,String> data;

    public void addRegId(String regId){
        if(registration_ids == null)
            registration_ids = new LinkedList<String>();
        registration_ids.add(regId);
    }

    public void createData(String title, String message){
        if(data == null)
            data = new HashMap<String,String>();
        data.put("title", title); //title shall be the code number to distinguish the type of notification
        data.put("message", message); // message will be the content of the notification
    }
    public void createDataForContract(String title, String message, String contractID){
        if(data == null)
            data = new HashMap<String,String>();
        data.put("title", title); //title shall be the code number to distinguish the type of notification
        data.put("message", message); // message will be the content of the notification
        data.put("contractID", contractID);
    }
}
