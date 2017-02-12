package com.example.myescort.familyhealth;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button button_login;
    private EditText Login_account,editText_password;
    private TextView textView_register;
    private String TAG="MainActivite";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findview();
        setview();
    }

    private void findview(){
        button_login=(Button)findViewById(R.id.button_login);
        Login_account=(EditText)findViewById(R.id.editText_account);
        editText_password=(EditText)findViewById(R.id.editText_password);
        textView_register=(TextView)findViewById(R.id.textView_register);
    }

    private void setview(){
        button_login.setOnClickListener(this);
        textView_register.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_login:
                login_function();
                break;
            case R.id.textView_register:
                startActivity(new  Intent(MainActivity.this ,Main2Activity.class));
                break;
        }
    }

    public void login_function(){
        final String id;
        final String password;
        id = Login_account.getText().toString();
        password = editText_password.getText().toString();
        Log.d(TAG, "onClick: " + id + " " + password);
        ConnectHelper.login(MainActivity.this, id, password, new MyCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                super.onSuccess(response);
                try {
                    startActivity(new Intent(MainActivity.this, Main3Activity.class));
                    Toast.makeText(MainActivity.this,"登入成功",Toast.LENGTH_SHORT).show();
                } catch (Exception e) {

                }

            }

            @Override
            public void onFail(String title, String errorMessage) {
                super.onFail(title, errorMessage);
                if (errorMessage != null) {
                    new AlertDialog.Builder(MainActivity.this).setTitle(title).setMessage(errorMessage).
                            setPositiveButton(R.string.retry, null).show();
                }
            }
        });
    }
}
