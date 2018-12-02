package kr.ac.gachon.www.comu;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.ac.gachon.www.R;

public class Select_chapter extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle si) {
        super.onCreate(si);
        setContentView(R.layout.activity_select_chapter);

        //사용언어 설정
        Intent intent=getIntent();
        String lang=intent.getStringExtra("lang");
        TextView langtv=(TextView)findViewById(R.id.language);
        langtv.setText(lang);

        //xml파일에서 내용을 가져와 삽입
        Resources resources=getResources();
        String[] chapter = null;
        LayoutInflater inflater=getLayoutInflater();
        LinearLayout layout=(LinearLayout)findViewById(R.id.list_layout);
        if(lang.equals("JAVA")) chapter=resources.getStringArray(R.array.java_chapter);
        else if(lang.equals("C")) chapter=resources.getStringArray(R.array.c);
        else if(lang.equals("C++")) chapter=resources.getStringArray(R.array.cplus);

        View[] sub_list=new View[chapter.length];
        TextView[] name=new TextView[chapter.length];
        //화면에 추가
        for(int i=0; i<chapter.length; i++) {
            sub_list[i]=inflater.inflate(R.layout.sub_chapter_list, null);
            name[i]=(TextView)sub_list[i].findViewById(R.id.name);
            name[i].setText(chapter[i]);
            final String chapter_string=lang+"."+chapter[i];
            sub_list[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent story=new Intent(Select_chapter.this, Story.class);
                    story.putExtra("chapter", chapter_string);
                    startActivity(story);
                }
            });
            layout.addView(sub_list[i]);
        }

    }
}
