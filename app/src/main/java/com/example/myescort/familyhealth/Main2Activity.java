package com.example.myescort.familyhealth;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener {
    private EditText editText_account,editText_password,editText_check,editText_name,editText_sex,editText_phone,editText_email;
    private Button button_register;
    private String TAG="Register";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        findview();
        setview();
    }
    public void findview(){
        editText_name=(EditText)findViewById(R.id.register_name);
        //editText_sex=(EditText)findViewById(R.id.)
        editText_phone=(EditText)findViewById(R.id.register_phone);
        editText_email=(EditText)findViewById(R.id.register_email);
        editText_account=(EditText)findViewById(R.id.register_account);
        editText_password=(EditText)findViewById(R.id.register_password);
        editText_check=(EditText)findViewById(R.id.register_passwordcheck);
        button_register=(Button)findViewById(R.id.register_button);

    }
    public void setview(){
        button_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.register_button:
                //Log.d(TAG,"按下了註冊按鈕");
                regiter_function();
                break;
        }
    }
    public void regiter_function(){
        final String account,password,name,sex,phone,email;
        account=editText_account.getText().toString();
        password=editText_password.getText().toString();
        name=editText_name.getText().toString();
        sex=editText_sex.getText().toString();
        phone=editText_phone.getText().toString();
        email=editText_email.getText().toString();

        Log.d(TAG,"帳號為"+account+"密碼為"+password);
        ConnectHelper.register(Main2Activity.this,name,sex,phone,email, account, password, new MyCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                super.onSuccess(response);
                try {
                    if(response.getString("info").equals("success")) {
                        startActivity(new Intent(Main2Activity.this, MainActivity.class));
                        Toast.makeText(Main2Activity.this,"註冊成功",Toast.LENGTH_SHORT);
                        finish();
                    }
                }catch (Exception e){
                }
            }
            @Override
            public void onFail(String title, String errorMessage) {
                super.onFail(title, errorMessage);
                if (errorMessage != null) {
                    new AlertDialog.Builder(Main2Activity.this).setTitle(title).setMessage(errorMessage).
                            setPositiveButton(R.string.retry, null).show();
                }
            }
        });
    }
}
