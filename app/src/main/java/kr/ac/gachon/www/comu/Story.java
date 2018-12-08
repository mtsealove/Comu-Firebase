package kr.ac.gachon.www.comu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import kr.ac.gachon.www.R;

public class Story extends AppCompatActivity {
    ArrayList<Conversation> conversations=new ArrayList<>();
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    int script_count=0; //대화의 개수
    TextView name, comment;
    @Override
    protected void onCreate(Bundle si) {
        super.onCreate(si);
        setContentView(R.layout.activity_story);
        DatabaseReference ref=database.getReference();
        Intent intent=getIntent();
        final String lang=intent.getStringExtra("lang");
        final String title=intent.getStringExtra("title");



        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.child("Conversation").child(lang).getChildren()) {
                    if(snapshot.child("title").getValue(String.class).equals(title)) {
                        for(DataSnapshot snapshot1: snapshot.getChildren()) {
                            String character=snapshot1.child("character").getValue(String.class);
                            String comment=snapshot1.child("comment").getValue(String.class);
                            try {
                                String code=snapshot1.child("code").getValue(String.class);
                                conversations.add(new Conversation(character, comment, code));
                            } catch (NullPointerException e) {
                                conversations.add(new Conversation(character, comment));
                            }

                        }
                    }
                }
                conversations.remove(conversations.size()-1);
                //대화 내용 설정
                name = findViewById(R.id.name);
                comment = findViewById(R.id.comment);
                name.setText(conversations.get(0).character);
                comment.setText(conversations.get(0).comment);

                //대화 터치시 다음 대화로 이동
                RelativeLayout script= findViewById(R.id.script);
                script.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getNextChapter();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
    public void go_Edit(View v) {
        Intent editor=new Intent(Story.this, Editor.class);
        startActivity(editor);
    }
    private void getNextChapter() {
        try {
            name.setText(conversations.get(script_count).character);
            comment.setText(conversations.get(script_count).comment);
            if(conversations.get(script_count).code!=null) {
                showCode();
            }
            ++script_count;
        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(Story.this, "대화가 종료되었습니다", Toast.LENGTH_SHORT).show();
            Intent intent=getIntent();
            String chapter=intent.getStringExtra("chapter");

            if(Load.account!=null) {
                Load.account.exp += 10;
                boolean already_exist = false;
                for (int i = 0; i < Load.account.finished_chapeter.length; i++) //이미 했는지 확인
                    if (chapter.equals(Load.account.finished_chapeter[i]))
                        already_exist = true;
                if (!already_exist) //존재하지 않으면 추가
                    Load.account.fc += chapter + ",";
                Load.account.re_split_chapter();
                update_db();
                if (Load.account.exp >= 100) {
                    Load.account.exp -= 100;
                    Load.account.level++;
                    update_db();
                }
            }
            finish();
        }
    }

    private void showCode() {
        if(conversations.get(script_count).code!=null) {
            Intent intent=new Intent(Story.this, Show_code.class);
            intent.putExtra("code", conversations.get(script_count).code);
            startActivity(intent);
        }
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
        ref.child("Account").child(Load.account.ID).child("fc").setValue(Load.account.fc);
        ref.child("Account").child(Load.account.ID).child("exp").setValue(Load.account.exp);
        ref.child("Account").child(Load.account.ID).child("level").setValue(Load.account.level);
    }


    @Override
    public void onBackPressed() {
        Toast.makeText(Story.this, "이 단계에서는 돌아갈 수 없습니다", Toast.LENGTH_SHORT).show();
    }
}
