package kr.ac.gachon.www.comu;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import kr.ac.gachon.www.R;

public class Load extends AppCompatActivity {
    final String manager_id="manager", manager_pw="password"; //관리자 계정
    static Account account;

    //정보를 로딩할 액티비티, 잠시 애플리케이션 로고 출력
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //전체화면 활성화
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_load);

        //0.5초 후에 로그인 페이지로 이동, 로고를 잠시 출력하기 위함
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                move_login(account);
            }
        }, 500);
    }

    String ID=null;
    String password = null;
    private void kepp_login() { //로컬에서 id와 비번을 읽어옴
        try {
            BufferedReader br=new BufferedReader(new FileReader(new File(getFilesDir()+"logined.dat")));
            String tmp="";

            //로컬에서 ID와 비번 읽기
            while((tmp=br.readLine())!=null) {
                    ID=tmp;
                    tmp=br.readLine();
                    password=tmp;
            }
            if((!ID.equals(null))&&(!password.equals(null))) {
                FirebaseDatabase db=FirebaseDatabase.getInstance();
                DatabaseReference ref=db.getReference();

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot: dataSnapshot.child("Account").getChildren()) { //일치하는 계정을 찾으면 데이터 가져오기
                            if(snapshot.child(ID).child("password").getValue().equals(password)) {
                                String name=snapshot.child("name").getValue().toString();
                                String phone=snapshot.child("phone").getValue().toString();
                                int level=Integer.parseInt(snapshot.child("level").getValue().toString());
                                int exp=Integer.parseInt(snapshot.child("exp").getValue().toString());
                                String fc=snapshot.child("fc").getValue().toString();
                                String friends_split=snapshot.child("friedns_split").getValue().toString();
                                account=new Account(name, ID, phone, password, level, exp, fc, friends_split);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void move_login(Account account) { //로그인 인덱스를 받아 로그인 페이지로 이동하거나 메인 페이지로 이동
            Intent intent;
            if(account==null) intent=new Intent(Load.this, Member.class);
            else intent=new Intent(Load.this, Main.class);
            startActivity(intent);
            finish();
    }
}
