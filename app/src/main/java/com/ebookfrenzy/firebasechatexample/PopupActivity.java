package com.ebookfrenzy.firebasechatexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class PopupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_popup);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 바깥 레이어 클릭시 안닫히게 공부중
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE){
            return  false;
        }
        return true;
    }

    public void mOnClose(View view) {
        // 팝업 창 닫기
        finish();
    }

    @Override
    public void onBackPressed(){
        // 안드로이드 백버튼 막기
        return;
    }
}
