package kr.ac.gachon.www.comu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import kr.ac.gachon.www.R;

public class Main extends AppCompatActivity {
    Button play_btn, friends_btn, setting_btn, info_btn, notice_btn;

    @Override
    protected void onCreate(Bundle si) {
        super.onCreate(si);
        setContentView(R.layout.activity_main);
        Intent intent=getIntent();

        TextView level=(TextView)findViewById(R.id.level);
        if(Load.account!=null) {//비회원이 아니라면 환영 토스트 출력
            Toast.makeText(Main.this, Load.account.name+"님 환영합니다", Toast.LENGTH_SHORT).show();
            display_level();
        }
        else level.setVisibility(View.GONE);


        //플레이 기능|챕터 선택으로 이동
        Button c=(Button)findViewById(R.id.c);
        Button java=(Button)findViewById(R.id.java);
        Button cplus=(Button)findViewById(R.id.cplus);

        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                move_chapter("C");
            }
        });
        java.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                move_chapter("JAVA");
            }
        });
        cplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                move_chapter("C++");
            }
        });
        //하단 버튼들 매칭
        play_btn=(Button)findViewById(R.id.play_btn);
        friends_btn=(Button)findViewById(R.id.friend_btn);
        notice_btn=(Button)findViewById(R.id.notice_btn);
        setting_btn=(Button)findViewById(R.id.setting_btn);
        info_btn=(Button)findViewById(R.id.info_btn);

        play_btn.setBackground(ContextCompat.getDrawable(Main.this, R.drawable.menu_button_focus));
    }
    public void display_level() { //정수로 표현된 레벨을 String으로 표시
        TextView level=(TextView)findViewById(R.id.level);
        String string_level="나의 레벨: ";
        if(Load.account!=null) {
            int int_level = Load.account.level;
            switch (int_level) {
                case 1:
                    string_level += "어린이";
                    break;
                case 2:
                    string_level += "학생";
                    break;
                case 3:
                    string_level += "교수";
                    break;
                default:
                    string_level += "도사";
            }
            level.setText(string_level);
        }
    }


    private void hide_layout() { //레이아웃 숨김 메서드
        //버튼 포커스 해제
        play_btn.setBackground(ContextCompat.getDrawable(Main.this, R.drawable.menu_button));
        friends_btn.setBackground(ContextCompat.getDrawable(Main.this, R.drawable.menu_button));
        notice_btn.setBackground(ContextCompat.getDrawable(Main.this, R.drawable.menu_button));
        setting_btn.setBackground(ContextCompat.getDrawable(Main.this, R.drawable.menu_button));
        info_btn.setBackground(ContextCompat.getDrawable(Main.this, R.drawable.menu_button));

        //레이아웃 매칭
        LinearLayout play=(LinearLayout)findViewById(R.id.play);
        LinearLayout setting=(LinearLayout)findViewById(R.id.setting);
        LinearLayout friends_not_login=(LinearLayout)findViewById(R.id.friends_not_login);
        LinearLayout friends_login=(LinearLayout)findViewById(R.id.friends_login);
        LinearLayout notice=(LinearLayout)findViewById(R.id.notice);
        LinearLayout info=(LinearLayout)findViewById(R.id.my_info);
        //레이아웃 소멸
        play.setVisibility(View.GONE);
        setting.setVisibility(View.GONE);
        friends_login.setVisibility(View.GONE);
        friends_not_login.setVisibility(View.GONE);
        notice.setVisibility(View.GONE);
        info.setVisibility(View.GONE);
    }

    public void set_play(View v) {
        hide_layout();
        LinearLayout layout=(LinearLayout)findViewById(R.id.play);
        layout.setVisibility(View.VISIBLE);
        play_btn.setBackground(ContextCompat.getDrawable(Main.this, R.drawable.menu_button_focus));
    }

    public void set_friends(View v) {
        hide_layout();
        friends_btn.setBackground(ContextCompat.getDrawable(Main.this, R.drawable.menu_button_focus));
        LinearLayout layout;
        //로그인 여부에 따라 다른 화면 설정
        if(Load.account==null) layout=(LinearLayout)findViewById(R.id.friends_not_login);
        else{
            layout=(LinearLayout)findViewById(R.id.friends_login);

            Button friends_list_btn=(Button)findViewById(R.id.friends_list);
            friends_list_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater inflater=getLayoutInflater();
                    View dialog_layout=inflater.inflate(R.layout.dialog_friends, null);
                    LinearLayout friends_list=(LinearLayout)dialog_layout.findViewById(R.id.friends_list);

                    View[] list=new View[Load.account.friends.length];
                    TextView[] name=new TextView[Load.account.friends.length];
                    if(Load.account.friends[0].length()!=0)
                        for(int i=0; i<Load.account.friends.length; i++) {
                            list[i]=inflater.inflate(R.layout.sub_friends, null);
                            name[i]=(TextView)list[i].findViewById(R.id.name);
                            name[i].setText(Load.account.friends[i]);
                            friends_list.addView(list[i]);
                        }
                    else {
                        TextView no_friends=(TextView)friends_list.findViewById(R.id.no_friends);
                        no_friends.setVisibility(View.VISIBLE);
                    }
                    AlertDialog.Builder builder=new AlertDialog.Builder(Main.this);
                    builder.setView(dialog_layout);
                    final AlertDialog dialog=builder.create();
                  dialog.show();
                }
            });
        }
        layout.setVisibility(View.VISIBLE);

        Button free_board=(Button)findViewById(R.id.free_board);
        free_board.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fb=new Intent(Main.this, Free_board.class);
                startActivity(fb);
            }
        });

    }
    static boolean notice_showed=false; //공지사항을 한번만 읽어오게 설정
    public void set_notice(View v) { //공지사항
        hide_layout();
        notice_btn.setBackground(ContextCompat.getDrawable(Main.this, R.drawable.menu_button_focus));
        LinearLayout layout=(LinearLayout)findViewById(R.id.notice);
        layout.setVisibility(View.VISIBLE);
        //파일에서 공지 읽어오기
        if(!notice_showed) {
            LinearLayout notice_list = (LinearLayout) findViewById(R.id.notice_list);
            LayoutInflater inflater = getLayoutInflater();
            View[] list = new View[100];
            TextView[] listtv = new TextView[100];
            String[] notices = new String[100];
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("notice.dat")));
                String tmp = "";
                int i = 0;
                while ((tmp = br.readLine()) != null) {
                    notices[i] = tmp;
                    list[i] = inflater.inflate(R.layout.sub_chapter_list, null);
                    listtv[i] = (TextView) list[i].findViewById(R.id.name);
                    listtv[i].setText(notices[i]);
                    notice_list.addView(list[i]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            notice_showed=true;
        }
    }

    public void set_info(View v) { //정보 표시 레이아웃
        hide_layout();
        info_btn.setBackground(ContextCompat.getDrawable(Main.this, R.drawable.menu_button_focus));
        LinearLayout layout=(LinearLayout)findViewById(R.id.my_info);
        layout.setVisibility(View.VISIBLE);
        //정보 설정
        TextView name=(TextView)findViewById(R.id.name);
        TextView ID=(TextView)findViewById(R.id.ID);
        TextView lv=(TextView)findViewById(R.id.level_info);
        TextView cleared_chapter=(TextView)findViewById(R.id.clear_chapter);
        if(Load.account!=null) {
            name.setText("이름: " + Load.account.name);
            ID.setText("ID: " + Load.account.ID);
            lv.setText("레벨: " + Load.account.level + "LV");
            String chapter = "클리어 챕터";
            try {
                for (int i = 0; i < Load.account.finished_chapeter.length; i++) {
                    chapter += "\n" + Load.account.finished_chapeter[i];
                }
            } catch (NullPointerException e) {

            }
            if (!chapter.equals("클리어 챕터"))
                cleared_chapter.setText(chapter);
        }
    }

    public void set_setting(View v) {
        hide_layout();
        LinearLayout layout=(LinearLayout)findViewById(R.id.setting);
        layout.setVisibility(View.VISIBLE);
        Button logout=(Button)findViewById(R.id.logout);
        if(Load.account==null) logout.setText("로그인 화면으로 이동");
        setting_btn.setBackground(ContextCompat.getDrawable(Main.this, R.drawable.menu_button_focus));
    }


    protected void move_chapter(String lang) {//챕터 선택으로 이동하는 메서드
        Intent chapter=new Intent(Main.this, Select_chapter.class);
        chapter.putExtra("lang", lang);
        startActivity(chapter);
    }

    public void logout(View v) { // 로그아웃
        File Logined=new File(getFilesDir()+"logined.dat");
        try {
            BufferedWriter bw=new BufferedWriter(new FileWriter(Logined));
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        notice_showed=false;
        Intent memeber=new Intent(getApplicationContext(), Member.class);
        startActivity(memeber);
        finish();
    }

    //뒤로가기 2번 눌러 종료
    private final long FINISH_INTERVAL_TIME = 2000;
    private long   backPressedTime = 0;
    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime)
        {
            System.exit(0);
        }
        else
        {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "뒤로 버튼을 한 번 더 누르면 종료합니다", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        display_level();
    }
}
