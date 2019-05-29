package com.ebookfrenzy.firebasechatexample;

import android.Manifest;
//

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.android.gms.common.api.Response;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
//import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQUEST_CODE_PERMISSIONS = 1000;
    private static final Integer MY_LOCATION =100 ;
    private static final Integer FRIEND_LOCATION = 999;

    private GoogleMap mMap;
//    private TextView idText;
//    private Button saveButton;


    private FusedLocationProviderClient mFusedLocationClient;
    private double lat;
    private double lon;
    private String idName;
   // private ActionBar ab;
    private static String resultUserLocation;

    // 나를 제외한 사람들의 Marker객체를 저장한다.
    ArrayList<Marker> friendMarker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

       //  saveButton = (Button)findViewById(R.id.currentLocationSave);
       // ab= getActionBar();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
         friendMarker= new ArrayList<>();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Intent intent = getIntent();
        idName=intent.getStringExtra("id");
       // idText.setText(idName);
       // ab.setTitle(idName);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_PERMISSIONS);

            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if (location != null) {
                    // 현재 위치
                    lat=location.getLatitude();
                    lon=location.getLongitude();
                    LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                     Marker  maker = mMap.addMarker(new MarkerOptions()
                                .position(myLocation)
                                .title(idName)
                                .snippet("쪽지함으로 이동"));
                            maker.setTag(MY_LOCATION);
                    // 카메라 줌
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,16));
                }
            }



        });


        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

               Integer clickCount=(Integer) marker.getTag();
               if(clickCount != null) {
                   if (clickCount.equals(MY_LOCATION)) {
                       // 나의 쪽지함으로
                       Intent memointent = new Intent(getBaseContext(), MemoActivity.class);
                      // memointent.putExtra("userName",idName);
                        startActivity(memointent);

                   } else if (clickCount.equals(FRIEND_LOCATION)){
                       // 쪽지 보내기
                      //Intent popupIntent = new Intent(getApplicationContext(), PopupActivity.class);
                       //Toast.makeText(getApplicationContext(),"우선 성공",Toast.LENGTH_SHORT).show();
                       //startActivity(popupIntent);
                   }
               }else{
                  Toast.makeText(getApplicationContext(),"clickcount 에러",Toast.LENGTH_SHORT).show();
               }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSIONS:
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "권한 체크 거부 됨", Toast.LENGTH_SHORT).show();
                }
                return;
        }

    }

    public void onSaveLocation(View view) {
        long now =System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String datetime = sdfNow.format(date);

        String latitude=String.valueOf(lat);
        String longitude=String.valueOf(lon);



            // 새로운 위치를 DB에 저장
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success=jsonResponse.getBoolean("success");
                    if(success){
                        Toast.makeText(getApplicationContext(),"새로운 위치 저장",Toast.LENGTH_SHORT).show();

                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                        builder.setMessage("위치를 공유하겠습니까?")
                                .setPositiveButton("ok",null)
                                .create()
                                .show();


                    }else{
                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MapsActivity.this);
                        builder.setMessage("오류오류요")
                                .setNegativeButton("ok",null)
                                .create()
                                .show();

                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }

            }
        };

        StoreLocationRequest storeLocationRequest = new StoreLocationRequest(idName, datetime, latitude, longitude, responseListener);
        RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);
        queue.add(storeLocationRequest);


    }
    // 다른 사람 위치 보이기 버튼
    public void onShowLocation(View view) {
        new BackgroundTask().execute();

        try{
            JSONObject jsonObject = new JSONObject(resultUserLocation);

            org.json.JSONArray  jsonArray = jsonObject.getJSONArray("response");
            int count = 0;
            int index_count=1;
            String userID, datetime, latitude, longitude;

            while(count <jsonArray.length()){

                JSONObject object = jsonArray.getJSONObject(count);

                userID = object.getString("userID");//여기서 ID가 대문자임을 유의
                datetime = object.getString("datetime");
                latitude = object.getString("latitude");
                longitude = object.getString("longitude");

                Double lattmp=Double.parseDouble(latitude);
                Double longtmp=Double.parseDouble(longitude);


                if(!idName.equals(userID))
                {
//                    MarkerOptions makerOptions = new MarkerOptions();
//                    makerOptions.position(new LatLng(lattmp,longtmp))
//                            .title(userID)
//                            .snippet("쪽지보내기\n"+"저장한 시간 :"+ datetime);
//
//                    Marker maker = mMap.addMarker(makerOptions);
//                    maker.setTag(FRIEND_LOCATION);
                    Marker maker = mMap.addMarker(new MarkerOptions().position(new LatLng(lattmp,longtmp))
                                                                        .title(userID)
                                                                        .snippet("쪽지보내기"));
                            maker.setTag(FRIEND_LOCATION);
                     friendMarker.add(index_count,maker);
                     index_count++;
                }


                count++;

            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void onDeleteLocation(View view) {

        // DB에 현재 id로 저장되어 있던 위치 삭제
        Response.Listener<String> responseListenerDelete = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if(success){
                        Toast.makeText(getApplicationContext(),"Delete success",Toast.LENGTH_SHORT).show();
                    }
                }catch(Exception e){
                    e.printStackTrace();

                }
            }
        };

        DeleteRequest deleteRequest = new DeleteRequest(idName,responseListenerDelete);
        RequestQueue queueDelete = Volley.newRequestQueue(MapsActivity.this);
        queueDelete.add(deleteRequest);

    }

   static class BackgroundTask extends AsyncTask<Void,Void,String>{
        String target;

        @Override
        protected void onPreExecute(){
            target="http://choihr.cafe24.com/LoadFile.php";

        }

        @Override
        protected String doInBackground(Void... voids){
            try{
                URL url = new URL(target);
                
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream)) ;
                String temp;
                StringBuilder stringBuilder = new StringBuilder();

                while((temp = bufferedReader.readLine()) != null){
                    stringBuilder.append(temp + "\n");//stringBuilder에 넣어줌
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return stringBuilder.toString().trim();

            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            resultUserLocation = result;

        }

    }

    }

