package com.ebookfrenzy.firebasechatexample;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class StoreLocationRequest extends StringRequest {
    final static private String URL="http://choihr.cafe24.com/StoreLocation.php";
    private Map<String,String> parameters;
    public StoreLocationRequest(String userID, String datetime, String latitude, String longitude, Response.Listener<String> listener){
        super(Method.POST,URL,listener,null);
        parameters=new HashMap<>();
        parameters.put("userID",userID);
        parameters.put("datetime",datetime);
        parameters.put("latitude",latitude);
        parameters.put("longitude",longitude);
    }

    @Override
    protected Map<String,String> getParams() throws AuthFailureError {
        return parameters;
    }

}
