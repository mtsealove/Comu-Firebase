package kr.ac.gachon.www.comu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import kr.ac.gachon.www.R;

public class Free_board extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle si) {
        super.onCreate(si);
        setContentView(R.layout.acticity_board);

        TextView title=(TextView)findViewById(R.id.title);
        title.setText("자유게시판");

        Button back_btn=(Button)findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
