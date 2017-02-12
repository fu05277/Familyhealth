package com.example.myescort.familyhealth;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapActivity extends Activity implements LocationListener {
    private static final String MAP_URL = "file:///android_asset/googleMap.html";
    private WebView webView;
    private EditText edittext_search;
    private Button search,back;
    private double lat, lng;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private boolean loadingFinished = false;
    private boolean getService = false;
    private JSONObject result;
    private int p=1; //page
    private String TAG="MapActivity";

    private LocationManager lms;
    private String bestProvider;    //最佳資訊提供者



    @Override
    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        webView = (WebView) findViewById(R.id.webview);
        back = (Button)findViewById(R.id.back_button);
        edittext_search = (EditText) findViewById(R.id.edittext_search);
        search = (Button) findViewById(R.id.search_button);
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 無權限，向使用者請求
            ActivityCompat.requestPermissions(this,
                    new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, REQUEST_EXTERNAL_STORAGE);
        } else {
            //已有權限，執行儲存程式
            startmap();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //取得c權限，進行檔案存取
                    startmap();
                } else {
                    //使用者拒絕權限，停用檔案存取功能
                }
                return;
        }
    }

    public void startmap() {
        setupWebView();
        LocationManager status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));

        if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
            locationServiceInitial();
            getService = true;
        } else {
            Toast.makeText(MapActivity.this, "請開啟定位服務", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));    //開啟設定頁面
        }
        search.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if("".equals(edittext_search.getText().toString().trim())) {
                    Toast.makeText(MapActivity.this, "請輸入地點", Toast.LENGTH_LONG).show();
                }else{
                    webView.loadUrl("javascript:deleteMarkers()");
                    getJSON();


                    Toast.makeText(MapActivity.this,"搜尋完成", Toast.LENGTH_LONG).show();
                }
            }
        });
        back.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                webView.loadUrl("javascript:centerAt(" + lat + "," + lng + ")");
            }
        });
    }

    public void setupWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webView.loadUrl("javascript:centerAt(" + lat + "," + lng + ")");
                loadingFinished = true;//webview已經載入完畢
            }

        });
        webView.loadUrl(MAP_URL);
    }



    private void locationServiceInitial() {
        lms = (LocationManager) getSystemService(LOCATION_SERVICE);    //取得系統定位服務
        ProviderChose();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = null;


        if (location == null) {

            lms.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            lms.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        } else {
            ProviderChose();

        }
    }

    public void ProviderChose() {
        Criteria criteria = new Criteria();    //資訊提供者選取標準
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//設置為最大精度
        criteria.setAltitudeRequired(false);//不要求海拔資訊
        criteria.setBearingRequired(false);//不要求方位資訊
        criteria.setCostAllowed(true);//是否允許付費
        criteria.setPowerRequirement(Criteria.POWER_LOW);//對電量的要求
        bestProvider = lms.getBestProvider(criteria, true);    //選擇精準度最高的提供者

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lms.requestLocationUpdates(bestProvider, 0, 0, this);
    }

    private void getLocation(Location location) {    //將定位資訊顯示在畫面中
        if (location != null) {
            lng = location.getLongitude();    //取得經度
            lat = location.getLatitude();    //取得緯度

            webView.loadUrl("javascript:me(" + lat + "," + lng + ")");
         //   webView.loadUrl("javascript:centerAt(" + lat + "," + lng + ")");

        } else {
            Toast.makeText(this, "無法定位座標", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onLocationChanged(Location location) {    //當地點改變時
        // TODO Auto-generated method stub
        getLocation(location);
    }

    @Override
    public void onProviderDisabled(String arg0) {    //當GPS或網路定位功能關閉時
        // TODO Auto-generated method stub
        Toast.makeText(this, "請開啟gps", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onProviderEnabled(String arg0) {    //當GPS或網路定位功能開啟
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {    //定位狀態改變
        //status=OUT_OF_SERVICE 供應商停止服務
        //status=TEMPORARILY_UNAVAILABLE 供應商暫停服務
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (getService) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            lms.requestLocationUpdates(bestProvider, 1000, 0, this);
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (getService) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            lms.removeUpdates(this);
        }
    }


    public void getJSON() {
        final String keyword;
        keyword=edittext_search.getText().toString();
        ConnectHelper.map_get(MapActivity.this,keyword,String.valueOf(p),new MyCallBack(){
            @Override
            public void onSuccess(JSONObject response) {
                super.onSuccess(response);
                result=response;

                try {



                    //假如抓出小於15筆停止下次的抓取

                       String address,name;
                        for (int i = 0; i < response.getJSONArray("data").length(); i++) {

                            address=response.getJSONArray("data").getJSONObject(i).getString("hospital_name");
                            name=response.getJSONArray("data").getJSONObject(i).getString("address");
                            String obj = "'"+address+"'"+","+"'"+name+"'";
                            Log.d("Zion",obj);
                            webView.loadUrl("javascript:codeAddress(" + obj +")");

                        }
                    Thread.sleep(100);
                    if (response.getJSONArray("data").length() > 14){
                        p++;
                        getJSON();
                        Log.d("ZionP",String.valueOf(p));
                    }





                } catch (Exception e) {
                    Log.d(TAG + "Error", "onSuccess: " + e.getMessage());
                }


            }
            @Override
            public void onFail(String title, String errorMessage) {
                super.onFail(title, errorMessage);
                if (errorMessage != null) {
                    new AlertDialog.Builder(MapActivity.this).setTitle(title).setMessage(errorMessage).
                            setPositiveButton(R.string.retry, null).show();
                }
            }
        });

    }


}