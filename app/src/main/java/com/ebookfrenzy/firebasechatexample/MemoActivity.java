package com.ebookfrenzy.firebasechatexample;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class MemoActivity extends AppCompatActivity {
    public static final String MESSAGES_CHILD_EX1="최혜림"; // 수정사항
    public static final String MESSAGES_CHILD_EX2="이영효";
    private DatabaseReference mFirebaseDatabaseReference;

    private FirebaseRecyclerAdapter<MemoMessage,MemoViewHolder> mFirebaseAdapter;
    private  RecyclerView mMemoRecyclerView;


    // Firebase 인스턴스 변수
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;



    private  String mUsername; // 현재 로그인된 사용자
    private String mPhotoUrl; // 둥근 싸컬에 새져질 글자

    private EditText mMemoUser; //  받는이
    private String recipient;
    private EditText mMemoEditText; // 쪽지 내용
    private String mSendDate; // 쪽지 보낸 시간

    public static class MemoViewHolder extends RecyclerView.ViewHolder{

        TextView nameText;
        TextView dateText;
        TextView messageText;
        CircleImageView photoImage;
        public MemoViewHolder(View v) {
            super(v);

            nameText= itemView.findViewById(R.id.memo_nameText);
            dateText= itemView.findViewById(R.id.memo_timeTextView);
            messageText=itemView.findViewById(R.id.memo_messageTextView);
            photoImage =itemView.findViewById(R.id.memo_photoImage);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

//        //리얼 타임 시작지점을 가져온다.
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if(mFirebaseUser == null){
            Toast.makeText(this,"아 인증이 안됬구나..",Toast.LENGTH_SHORT).show();
            return;
        }else{
           mUsername = mFirebaseUser.getDisplayName();
            if(mFirebaseUser.getPhotoUrl() !=  null){
                mPhotoUrl= mFirebaseUser.getPhotoUrl().toString();
            }
        }
        mMemoRecyclerView= findViewById(R.id.memo_recycler_view);

        mMemoUser=findViewById(R.id.user_edit);  //activity_memo에서 받는이

        mMemoEditText = findViewById(R.id.memo_edit);// activity_memo에서 메모내용

        //Firebase 리얼타임 데이터 베이스 초기화
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();


        findViewById(R.id.send_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long now =System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdfNow= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                mSendDate = sdfNow.format(date);
                MemoMessage memoMessage = new MemoMessage(mMemoEditText.getText().toString(),
                        mUsername,mSendDate,mPhotoUrl);// mUsername은 보낸이
                recipient =mMemoUser.getText().toString();
                mFirebaseDatabaseReference.child(recipient)
                        .push()
                        .setValue(memoMessage);

                mMemoEditText.setText("");
                mMemoUser.setText("");

            }
        });

        //쿼리 수행 위치
        Query query = mFirebaseDatabaseReference.child(mUsername);

        FirebaseRecyclerOptions<MemoMessage> options =
                new FirebaseRecyclerOptions.Builder<MemoMessage>()
                .setQuery(query,MemoMessage.class)
                .build();
        //어뎁터
        mFirebaseAdapter = new FirebaseRecyclerAdapter<MemoMessage, MemoViewHolder>(options) {
            @Override
            protected void onBindViewHolder( MemoViewHolder holder, int position,  MemoMessage model) {
                holder.messageText.setText(model.getText());
                holder.nameText.setText(model.getName());
                holder.dateText.setText(model.getDate());
                if(model.getPhotoUrl() == null){
                    holder.photoImage.setImageDrawable(ContextCompat.getDrawable(MemoActivity.this,
                            R.drawable.ic_account_circle_black_24dp));
                }else{
                    Glide.with(MemoActivity.this)
                            .load(model.getPhotoUrl())
                            .into(holder.photoImage);
                }
            }


            @Override
            public MemoViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_memo,parent,false);

                return new MemoViewHolder(view);
            }
        };

        mMemoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMemoRecyclerView.setAdapter(mFirebaseAdapter);
        // onCreate()끝
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseAdapter.stopListening();
    }
}
