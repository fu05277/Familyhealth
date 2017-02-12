package com.example.myescort.familyhealth;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONObject;
import cz.msebera.android.httpclient.Header;

/**
 * Created by user on 2017/1/16.
 */

public class ConnectHelper {

    private static final String SEVERS_URL="http://192.168.1.112/familyhealth/";
    private static final String LOGIN_URL=SEVERS_URL+"Login.php";
    private static final String REGISTER_URL=SEVERS_URL+"Rgister.php";
    private static final String OK_URL=SEVERS_URL+"OK.php";
    private static final String TAG="ConnectHelper";
    private static final AsyncHttpClient mClient = init();
    private static final String MAP_GET_URL = SEVERS_URL+"map_get.php" ;

    private static AsyncHttpClient init() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(50 * 1000);
        client.setEnableRedirects(true, true, true);
        return client;
    }
    private static void onConnectHelperFail(Context context, int statusCode, MyCallBack callback, String errorMessage) {
        if (callback != null) {
            if (statusCode == 0) {
                callback.onFail(context.getString(R.string.connect_get_problem), context.getString(R.string.timeout_message));
            } else if (statusCode == 200) {
                callback.onFail(context.getString(R.string.error), errorMessage);
            } else if (statusCode == 500) {
                callback.onFail(context.getString(R.string.connect_get_problem), context.getString(R.string.server_fixing));
            } else if (statusCode == 502) {
                callback.onFail(context.getString(R.string.connect_get_problem), context.getString(R.string.connection_fixing));
            } else {
                callback.onFail(context.getString(R.string.connect_get_problem), context.getString(R.string.something_error) + "：" +
                        statusCode + (errorMessage == null ? "" : " " + errorMessage));
            }
        }
    }

    /**
     *登入的程式碼
     * @param context           context
     * @param account          帳號
     * @param password        密碼
     * @param callback          callback
     */

    public static void login(final Context context, String account, String password, final MyCallBack callback) { //登入
        Log.d(TAG, "OnLogin");
        try {
            final ProgressDialog LoginDialog = new ProgressDialog(context);
            RequestParams params = new RequestParams();
            params.add("account", account);
            params.add("password", password);
            mClient.post(context, OK_URL, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    String error_message = "000";
                    try {
                        LoginDialog.dismiss();
                        error_message = response.getString("info");
                    } catch (Exception e) {
                    }
                    if (statusCode == 200 && error_message.equals("success")) {
                        callback.onSuccess(response);
                    } else {
                        onConnectHelperFail(context, statusCode, callback, "帳號或密碼錯誤");
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    try {
                        LoginDialog.dismiss();
                    } catch (Exception e) {
                    }
                    onConnectHelperFail(context, statusCode, callback, throwable.getMessage());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    try {
                        LoginDialog.dismiss();
                    } catch (Exception e) {
                    }
                    onConnectHelperFail(context, statusCode, callback, throwable.getMessage());
                }
            });
        } catch (Exception e) {
        }
    }

    /**
     *
     * @param context
     * @param name
     * @param sex
     * @param phone
     * @param email
     * @param account
     * @param password
     * @param callback
     */
    public static void register(final Context context,String name,String sex,String phone,String email, String account, String password, final MyCallBack callback) {
        Log.d(TAG, "OnMember_Insert");
        final ProgressDialog registertDialog = new ProgressDialog(context);
        RequestParams params = new RequestParams();
        params.add("account", account);
        params.add("password", password);
        params.add("name", name);
        params.add("sex", sex);
        params.add("phone", phone);
        params.add("email", email);
        mClient.post(context, REGISTER_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                String error_message = "000";
                try {
                    registertDialog.dismiss();
                    error_message = response.getString("info");
                } catch (Exception e) {

                }
                if (statusCode == 200) {
                    callback.onSuccess(response);
                } else {
                    onConnectHelperFail(context, statusCode, callback, error_message);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                try {
                    registertDialog.dismiss();
                } catch (Exception e) {

                }
                onConnectHelperFail(context, statusCode, callback, throwable.getMessage());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                try {
                    registertDialog.dismiss();
                } catch (Exception e) {
                }
                onConnectHelperFail(context, statusCode, callback, throwable.getMessage());
            }
        });
    }
    public static void map_get(final Context context, String keyword,String p, final MyCallBack callback) {
        Log.d(TAG,"map_get");
        final ProgressDialog mapGetDialog = new ProgressDialog(context);
        RequestParams params = new RequestParams();
        params.add("keyword", keyword);
        params.add("page", p);
        mClient.post(context, MAP_GET_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "OnRoom_get - onSuccess");
                super.onSuccess(statusCode, headers, response);
                String error_message = "000";
                mapGetDialog.dismiss();
                try {
                    Log.d("Zion",response.getString("data"));
                } catch (Exception e) {
                    Log.d(TAG, "OnRoom_get - onSuccess: " + e.getMessage());
                }
                if (statusCode == 200) {
                    callback.onSuccess(response);
                } else {
                    onConnectHelperFail(context, statusCode, callback, error_message);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d(TAG, "OnRoom_get - onFailure");
                try {
                    mapGetDialog.dismiss();
                } catch (Exception e) {
                    Log.d(TAG, "OnRoom_get - onFailure: " + e.getMessage());
                }
                onConnectHelperFail(context, statusCode, callback, throwable.getMessage());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d(TAG, "OnRoom_get - onFailure");
                try {
                    mapGetDialog.dismiss();
                } catch (Exception e) {
                    Log.d(TAG, "OnRoom_get - onFailure: " + e.getMessage());
                }
                onConnectHelperFail(context, statusCode, callback, throwable.getMessage());
            }
        });
    }

}
