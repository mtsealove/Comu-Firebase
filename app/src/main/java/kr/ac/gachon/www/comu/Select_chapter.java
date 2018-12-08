package kr.ac.gachon.www.comu;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import kr.ac.gachon.www.R;

public class Select_chapter extends AppCompatActivity {
    static ArrayList<Conversation> conversations=new ArrayList<Conversation>();
    ArrayList<String> chapter=new ArrayList<>();
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    View[] sub_list;
    TextView[] name;
    int size=0;
    LayoutInflater inflater;
    LinearLayout layout;
    @Override
    protected void onCreate(Bundle si) {
        super.onCreate(si);
        setContentView(R.layout.activity_select_chapter);
        DatabaseReference reference=database.getReference();
        //사용언어 설정
        Intent intent=getIntent();
        final String lang=intent.getStringExtra("lang");
        TextView langtv= findViewById(R.id.language);
        langtv.setText(lang);

        //xml파일에서 내용을 가져와 삽입
        layout= findViewById(R.id.list_layout);
        inflater=getLayoutInflater();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                size= (int) dataSnapshot.child("Conversation").child(lang).getChildrenCount();
                sub_list=new View[size];
                name=new TextView[size];

                for(DataSnapshot snapshot:dataSnapshot.child("Conversation").child(lang).getChildren()) {
                    String title=snapshot.child("title").getValue(String.class);
                    chapter.add(title);
                }
                for(int i=0; i<size; i++) {
                    sub_list[i]=inflater.inflate(R.layout.sub_chapter_list, null);
                    name[i]= sub_list[i].findViewById(R.id.name);
                    name[i].setText(chapter.get(i));
                    final String Ftitle=chapter.get(i);
                    final String chapter_string=lang+"."+chapter.get(i);
                    sub_list[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent story=new Intent(Select_chapter.this, Story.class);
                            story.putExtra("lang", lang);
                            story.putExtra("chapter", chapter_string);
                            story.putExtra("title", Ftitle);
                            startActivity(story);
                        }
                    });
                    layout.addView(sub_list[i]);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
