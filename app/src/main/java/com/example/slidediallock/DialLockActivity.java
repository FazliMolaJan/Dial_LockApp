package com.example.slidediallock;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;

public class DialLockActivity extends Activity {

    private ArrayList<Integer> pw;

    private Integer pwSize;

    private Integer inputNum;
    private ArrayList<Integer> inputPw;

    private LinearLayout bg_screen;

    private ImageView mImg1;
    private ImageView mImg2;
    private ImageView mImg3;
    private ImageView mImg4;
    private ImageView mImg5;

    private ImageView img_input;

    private ImageView img_cursor;
    private LottieAnimationView img_cursor2;
    private LottieAnimationView img_check;

    private ValueAnimator animator;

    private static final String IMAGEVIEW_TAG = "드래그 이미지";

    private Integer wrongTrigger;

    WindowManager wm;
    View mView;
    Vibrator vibrator;

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch(keyCode) {
            case KeyEvent.KEYCODE_BACK:
                // 여기에 뒤로가기 버튼을 눌렀을 때 행동 입력
                return false;

            case KeyEvent.KEYCODE_HOME:
                // 여기에 홈 버튼을 눌렀을 때 행동 입력
                return false;
            case KeyEvent.KEYCODE_MENU:
                return false;
        }

        return super.onKeyDown(keyCode, event);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 진동
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        getWindow().addFlags(
                // 기본 잠금화면보다 우선출력
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        // 기본 잠금화면 해제시키기
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        // setContentView(R.layout.activity_draglock);
        // bg_screen = (LinearLayout) findViewById(R.id.bg_layout);

        Intent intent = new Intent(
                getApplicationContext(),//현재제어권자
                LockScreenService.class); // 이동할 컴포넌트
        startService(intent);

        LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);


        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        |WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        |WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        |WindowManager.LayoutParams.FLAG_FULLSCREEN,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER;

        mView = inflate.inflate(R.layout.activity_draglock, null);
        mView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
        bg_screen = (LinearLayout)mView.findViewById(R.id.bg_layout);

        inputNum = 0;
        inputPw = new ArrayList<Integer>();

        // 비밀번호 배열 ( 임시비번 = 0000 )
        pw = new ArrayList();
        pw.add(2);
        pw.add(21);
        pw.add(35);
        pw.add(1);
        pwSize = pw.size();

        img_input = (ImageView) mView.findViewById(R.id.input_img);
        if(pwSize==2)
            img_input.setImageResource(R.drawable.input_2pw0);
        else
            img_input.setImageResource(R.drawable.input_4pw0);
        /*
        if(lpw==0) {
            pw.add(0);
            pw.add(0);
            img_Input.setImageResource(R.drawable.input_2pw0);
        }else{
            if(pwSize==2){
                // 비밀번호 사이즈 2
                Integer a = lpw/10;
                Integer b = lpw%10;
                pw.add(a);
                pw.add(b);
                img_Input.setImageResource(R.drawable.input_2pw0);
            }else{
                // 비밀번호 사이즈 4
                Integer a = lpw/1000;
                Integer b = (lpw%1000)/100;
                Integer c = (lpw%100)/10;
                Integer d = lpw%10;
                pw.add(a);
                pw.add(b);
                pw.add(c);
                pw.add(d);
                img_Input.setImageResource(R.drawable.input_4pw0);
            }
        }
        */

        mImg1 = (ImageView) mView.findViewById(R.id.img1);
        mImg1.setTag(IMAGEVIEW_TAG);
        mImg2 = (ImageView) mView.findViewById(R.id.img2);
        mImg2.setTag(IMAGEVIEW_TAG);
        mImg3 = (ImageView) mView.findViewById(R.id.img3);
        mImg3.setTag(IMAGEVIEW_TAG);
        mImg4 = (ImageView) mView.findViewById(R.id.img4);
        mImg4.setTag(IMAGEVIEW_TAG);
        mImg5 = (ImageView) mView.findViewById(R.id.img5);
        mImg5.setTag(IMAGEVIEW_TAG);

        img_cursor = (ImageView) mView.findViewById((R.id.i_cursor));
        img_cursor.setTag(IMAGEVIEW_TAG);

        img_cursor2 = (LottieAnimationView) mView.findViewById(R.id.anim_cursor);
        img_cursor2.setTag(IMAGEVIEW_TAG);

        img_check = (LottieAnimationView) mView.findViewById(R.id.anim_cursor2);
        img_check.setTag(IMAGEVIEW_TAG);

        animator = ValueAnimator.ofFloat(0f, 1f)
                .setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                img_check.setProgress((Float) animation.getAnimatedValue());
            }
        });

        mImg1.setOnTouchListener(new TouchListener());
        mImg1.setClickable(true);
        mImg2.setOnTouchListener(new TouchListener());
        mImg2.setClickable(true);
        mImg3.setOnTouchListener(new TouchListener());
        mImg3.setClickable(true);
        mImg4.setOnTouchListener(new TouchListener());
        mImg4.setClickable(true);
        mImg5.setOnTouchListener(new TouchListener());
        mImg5.setClickable(true);

        //findViewById(R.id.topArea).setOnDragListener(new DragListener());
        //findViewById(R.id.bottomArea).setOnDragListener(new DragListener());
    }

    class TouchListener implements View.OnTouchListener{
        public boolean onTouch(View v, MotionEvent motionEvent) {

            int parentWidth = ((ViewGroup)v.getParent().getParent()).getWidth();    // 부모 View 의 Width
            int parentHeight = ((ViewGroup)v.getParent().getParent()).getHeight();    // 부모 View 의 Height

            switch(motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN :

                    img_cursor2.setX(motionEvent.getRawX()-img_cursor2.getWidth()/2);
                    img_cursor2.setY(motionEvent.getRawY()-(img_cursor2.getHeight()*2)/3);
                    img_cursor2.setVisibility(View.VISIBLE);
                    //animator.start();
                    switch (v.getId()) {
                        case R.id.img1:
                            inputNum = 1;
                            break;
                        case R.id.img2:
                            inputNum = 2;
                            break;
                        case R.id.img3:
                            inputNum = 3;
                            break;
                        case R.id.img4:
                            inputNum = 4;
                            break;
                        case R.id.img5:
                            inputNum = 5;
                            break;
                    }

                    //v.setVisibility(View.INVISIBLE);

                    break;
                case MotionEvent.ACTION_MOVE:

                    img_cursor2.setY(v.getY() + (motionEvent.getY()) - (img_cursor2.getHeight()/2));

                    //img_cursor.setX(v.getX() + (motionEvent.getX()) - (img_cursor.getWidth()/2));

                    float p = 0.0008f;
                    float tmp = img_cursor2.getY()-parentHeight/2;
                    img_cursor2.setX(p*tmp*tmp+parentWidth/2+30f);

                    break;
                case MotionEvent.ACTION_UP:

                    if(img_cursor2.getY() < 650){
                        inputNum*=7;
                        if(inputNum!=7)
                            enterInput(inputNum);
                    }else if(img_cursor2.getY() > 1600){
                        if(inputNum!=5)
                            enterInput(inputNum);
                    }

                    v.setVisibility(View.VISIBLE);
                    img_cursor2.setVisibility(View.INVISIBLE);


                    break;
                case MotionEvent.ACTION_CANCEL:

                    break;
            }
            return true;
        }
    }

    void CheckPW() {

        boolean wrongPw = false;

        int index = 0;

        while(index<pwSize){

            if(inputPw.get(index)!=pw.get(index)){
                wrongPw = true;
                break;
            }
            index++;
        }

        if(wrongPw){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    incorrect();
                }
            }, 200);    //0.2초 뒤에
            wrongPw = false;
            return;
        }else{
            Unlock();
        }

    }

    void Unlock(){
        //wm.removeViewImmediate(mView);
        finishAffinity();
    }

    void incorrect(){
        inputPw.clear();
        if(pwSize==2) {
            img_input.setImageResource(R.drawable.input_2pw0);
        }else if(pwSize==4){
            img_input.setImageResource(R.drawable.input_4pw0);
        }
    }

    void enterInput(Integer num){
        inputPw.add(num);
        img_check.setVisibility(View.VISIBLE);
        img_check.setX(img_cursor2.getX());
        img_check.setY(img_cursor2.getY());
        animator.start();
        Log.d("Input", "INPUT : "+inputPw);


        if(pwSize==2) {
            if (inputPw.size() == 1) {
                img_input.setImageResource(R.drawable.input_2pw1);
            } else if (inputPw.size() == 2) {
                img_input.setImageResource(R.drawable.input_2pw2);
            } else {
                img_input.setImageResource(R.drawable.input_2pw0);
            }
        }else {
            switch (inputPw.size()) {
                case 0:
                    img_input.setImageResource(R.drawable.input_4pw0);
                    break;
                case 1:
                    img_input.setImageResource(R.drawable.input_4pw1);
                    break;
                case 2:
                    img_input.setImageResource(R.drawable.input_4pw2);
                    break;
                case 3:
                    img_input.setImageResource(R.drawable.input_4pw3);
                    break;
                case 4:
                    img_input.setImageResource(R.drawable.input_4pw4);
                    break;
            }
        }

        if(inputPw.size()==pw.size())
            CheckPW();
    }
}
