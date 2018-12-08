package kr.ac.gachon.www.comu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import kr.ac.gachon.www.R;

public class Show_code extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle si) {
        super.onCreate(si);
        setContentView(R.layout.activity_show_code);
        Intent intent=getIntent();
        String code=intent.getStringExtra("code");
        TextView codeTv= findViewById(R.id.code);
        codeTv.setText(code);
    }
}
