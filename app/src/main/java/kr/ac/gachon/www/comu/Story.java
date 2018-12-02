package kr.ac.gachon.www.comu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import kr.ac.gachon.www.R;

public class Story extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle si) {
        super.onCreate(si);
        setContentView(R.layout.activity_story);
        final int max_script=1000;
        final Conversation[] conversations=new Conversation[max_script];
        int script_count=0; //대화의 개수

        String file_name="java_variable.dat";
        try {
            BufferedReader br=new BufferedReader(new InputStreamReader(getAssets().open(file_name)));
            String tmp="";
            String character, comment;
            while((tmp=br.readLine())!=null) {
                character=tmp;
                tmp=br.readLine();
                comment=tmp;
                conversations[script_count]=new Conversation(character, comment);
                script_count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //대화 내용 설정
        final TextView name = (TextView) findViewById(R.id.name);
        final TextView comment = (TextView) findViewById(R.id.comment);
        name.setText(conversations[0].character);
        comment.setText(conversations[0].comment);

        //대화 터치시 다음 대화로 이동
        final int[] ing = {1};
        final Bundle final_si=si;
        RelativeLayout script=(RelativeLayout)findViewById(R.id.script);
        script.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    name.setText(conversations[ing[0]].character);
                    comment.setText(conversations[ing[0]].comment);
                    ing[0]++;
                } catch (NullPointerException e) {
                    Toast.makeText(Story.this, "대화가 종료되었습니다", Toast.LENGTH_SHORT).show();
                    Intent intent=getIntent();
                    String chapter=intent.getStringExtra("chapter");
                    Load.account.exp+=10;
                    boolean already_exist=false;
                    for(int i=0; i<Load.account.finished_chapeter.length; i++) //이미 했는지 확인
                        if(chapter.equals(Load.account.finished_chapeter[i])) already_exist=true;
                    if(!already_exist) //존재하지 않으면 추가
                        Load.account.fc+=chapter+",";
                    Load.account.re_split_chapter();
                    update_db();
                    if(Load.account.exp>=100) {
                        Load.account.exp-=100;
                        Load.account.level++;
                        update_db();
                    }
                    finish();
                }
            }
        });

    }
    public void go_Edit(View v) {
        Intent editor=new Intent(Story.this, Editor.class);
        startActivity(editor);
    }

    int current_account=0;
    private void update_db() { //파이어베이스 업데이트
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference ref=db.getReference();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) { //계정의 인덱스를 찾음
                for(DataSnapshot snapshot:dataSnapshot.child("Account").getChildren()) {
                    if(snapshot.child("ID").getValue().toString().equals(Load.account.ID))
                        break;
                    current_account++;
                    Log.d("계정번호: ", String.valueOf(current_account));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ref.child("Account").child(Integer.toString(current_account)).child("fc").setValue(Load.account.fc);
        ref.child("Account").child(Integer.toString(current_account)).child("exp").setValue(Load.account.exp);
        ref.child("Account").child(Integer.toString(current_account)).child("level").setValue(Load.account.level);
    }


    @Override
    public void onBackPressed() {
        Toast.makeText(Story.this, "이 단계에서는 돌아갈 수 없습니다", Toast.LENGTH_SHORT).show();
    }
}
