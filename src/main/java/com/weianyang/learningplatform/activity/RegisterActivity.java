package com.weianyang.learningplatform.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.weianyang.learningplatform.R;

public class RegisterActivity extends AppCompatActivity {

    private ArrayAdapter<CharSequence> adapter_user_type;
    private ArrayAdapter<CharSequence> adapter_user_sex;
    private ArrayAdapter<CharSequence> adapter_user_major;
    private AppCompatSpinner spinner_user_type;
    private AppCompatSpinner spinner_user_sex;
    private AppCompatSpinner spinner_major;
    public static void actionStart(Context context){
        Intent intent = new Intent(context,RegisterActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar_register = findViewById(R.id.toolbar_register);
        setSupportActionBar(toolbar_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        spinner_user_type = findViewById(R.id.spinner_user_type);
        spinner_user_sex = findViewById(R.id.spinner_user_sex);
        spinner_major = findViewById(R.id.spinner_major);
        adapter_user_type = ArrayAdapter.createFromResource(RegisterActivity.this
                ,R.array.user_type,R.layout.support_simple_spinner_dropdown_item);
        adapter_user_sex = ArrayAdapter.createFromResource(RegisterActivity.this
                ,R.array.user_sex,R.layout.support_simple_spinner_dropdown_item);
        adapter_user_major = ArrayAdapter.createFromResource(RegisterActivity.this
                ,R.array.majors,R.layout.support_simple_spinner_dropdown_item);
        spinner_user_type.setAdapter(adapter_user_type);
        spinner_user_sex.setAdapter(adapter_user_sex);
        spinner_major.setAdapter(adapter_user_major);
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
