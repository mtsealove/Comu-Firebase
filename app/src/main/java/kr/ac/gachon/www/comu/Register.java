package kr.ac.gachon.www.comu;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import kr.ac.gachon.www.R;

public class Register extends AppCompatActivity {
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    final boolean[] is_reuse = {true, true};
    EditText name;
    EditText id;
    EditText password;
    EditText password_confirm;
    EditText phone;

    String myNumber=null;
    @Override
    protected void onCreate(Bundle si) {
        super.onCreate(si);
        setContentView(R.layout.activity_register);
        name= findViewById(R.id.input_name);
        id= findViewById(R.id.input_id);
        password= findViewById(R.id.input_password);
        password_confirm= findViewById(R.id.input_password_confirm);
        phone= findViewById(R.id.input_phone);

        phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        TelephonyManager mgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        try {
            if (ActivityCompat.checkSelfPermission(Register.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Register.this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Register.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            myNumber = mgr.getLine1Number();
            myNumber = myNumber.replace("+82", "0");
            phone.setText(myNumber);

        }catch(Exception e){}

    }
    public void Check_ID_reuse(View v) {

        if(id.getText().toString().length()==0) Toast.makeText(Register.this, "ID를 입력하세요", Toast.LENGTH_SHORT).show();
        else {
            is_reuse[0]=false;
            DatabaseReference ref = database.getReference();
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.child("Account").getChildren()) {
                        if (snapshot.child(id.getText().toString()).exists())
                            is_reuse[0] = true;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            if (is_reuse[0])
                Toast.makeText(Register.this, "이미 존재하는 ID입니다", Toast.LENGTH_SHORT).show();
            else Toast.makeText(Register.this, "사용 가능한 ID입니다", Toast.LENGTH_SHORT).show();
        }
    }

    public void Check_Phone_reuse(View v) {
        if(phone.getText().toString().length()==0) Toast.makeText(Register.this, "전화번호를 입력하세요", Toast.LENGTH_SHORT).show();
        else {
            is_reuse[1] = false;
            DatabaseReference reference = database.getReference();
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot: dataSnapshot.child("Account").getChildren()) {
                        if(snapshot.child("phone").equals(phone.getText().toString()))
                            is_reuse[1]=true;
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            if (is_reuse[1])
                Toast.makeText(Register.this, "이미 사용되고 있는 전화번호입니다", Toast.LENGTH_SHORT).show();
            else Toast.makeText(Register.this, "사용 가능한 전화번호입니다", Toast.LENGTH_SHORT).show();
        }
    }

    public void Register(View v) {
        String tname = name.getText().toString();
        String tID = id.getText().toString();
        String tpassword = password.getText().toString();
        String tpassword_confirm = password_confirm.getText().toString();
        String tphone = phone.getText().toString();
        if (is_reuse[0]) Toast.makeText(Register.this, "ID중복을 확인하세요", Toast.LENGTH_SHORT).show();
        else if (is_reuse[1])
            Toast.makeText(Register.this, "전화번호 중복을 확인하세요", Toast.LENGTH_SHORT).show();
        else if (!tpassword.equals(tpassword_confirm))
            Toast.makeText(Register.this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
        else {
            Load.account = new Account(tname, tID, tphone, tpassword, 1, 0, "", "");
            create_account(Load.account);
            Toast.makeText(Register.this, "회원가입이 완료되었습니다", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void create_account(Account account) {
        DatabaseReference ref = database.getReference();
        ref.child("Account").child(account.ID).child("name").setValue(account.name);
        ref.child("Account").child(account.ID).child("ID").setValue(account.ID);
        ref.child("Account").child(account.ID).child("exp").setValue(account.exp);
        ref.child("Account").child(account.ID).child("fc").setValue(account.fc);
        ref.child("Account").child(account.ID).child("friends_split").setValue(account.friends_split);
        ref.child("Account").child(account.ID).child("level").setValue(account.level);
        ref.child("Account").child(account.ID).child("password").setValue(account.password);
        ref.child("Account").child(account.ID).child("phone").setValue(account.phone);

    }
}
