package com.weianyang.learningplatform.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.weianyang.learningplatform.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView text_register;

    public static void actionStart(Context context){
        Intent intent = new Intent(context,LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar_login = findViewById(R.id.toolbar_login);
        setSupportActionBar(toolbar_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        text_register = findViewById(R.id.textview_register);
        text_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.textview_register:
                RegisterActivity.actionStart(LoginActivity.this);
                break;
            default:break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:break;
        }
        return true;
    }
}
