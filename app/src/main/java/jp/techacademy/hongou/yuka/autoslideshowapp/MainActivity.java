package jp.techacademy.hongou.yuka.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    ImageView mImageView;
    private Cursor cursor;
    Button mNextButton;
    Button mBackButton;
    Button mAutoSlideButton;
    Timer mTimer;
    int mTimerSec = 0;

    Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //許可されている
                getContentsInfo();
            } else {
                //許可されていないので、ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
        } else {
            getContentsInfo();
        }

        final Button mBackButton = (Button) findViewById(R.id.backButton);
        final Button mNextButton = (Button) findViewById(R.id.nextButton);
        final Button mAutoSlideButton = (Button) findViewById(R.id.autoSlideButton);

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cursor.isBeforeFirst()){
                    cursor.moveToFirst();}

                if (!cursor.isLast()){
                    cursor.moveToNext();}
                else{
                    cursor.moveToFirst();
                }
                int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                Long id = cursor.getLong(fieldIndex);
                Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                ImageView mImageView = (ImageView) findViewById(R.id.imageView);
                mImageView.setImageURI(imageUri);
            }
        });


        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cursor.isBeforeFirst()) {
                    cursor.moveToFirst();
                }
                if (!cursor.isFirst()){
                    cursor.moveToPrevious();
                }else {cursor.moveToLast();}

                int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                Long id = cursor.getLong(fieldIndex);
                Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                ImageView mImageView = (ImageView) findViewById(R.id.imageView);
                mImageView.setImageURI(imageUri);
            }
        });

        mAutoSlideButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (mTimer == null) {
                    //タイマーの作成
                    mTimer = new Timer();

                    mTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            mTimerSec += 0.1;

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {

                                    if (cursor.isBeforeFirst()) {
                                        cursor.moveToFirst();
                                    }

                                    if (!cursor.isLast()) {
                                        cursor.moveToNext();
                                    } else {
                                        cursor.moveToFirst();
                                    }
                                    int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                                    Long id = cursor.getLong(fieldIndex);
                                    Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                                    ImageView mImageView = (ImageView) findViewById(R.id.imageView);
                                    mImageView.setImageURI(imageUri);
                                    mNextButton.setEnabled(false);
                                    mBackButton.setEnabled(false);
                                    mAutoSlideButton.setText("停止");
                                }
                            });
                        }
                    }, 0, 2000);    // 最初に始動させるまで 100ミリ秒、ループの間隔を 100ミリ秒 に設定

                }
                else if (mTimer != null) {
                    mTimer.cancel();
                    mTimer = null;
                    mNextButton.setEnabled(true);
                    mBackButton.setEnabled(true);
                    mAutoSlideButton.setText("再生");

                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                    TextView textView = (TextView) findViewById(R.id.textView);
                    textView.setText("");
                }
                else if (grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
                    TextView textView = (TextView) findViewById(R.id.textView);
                    textView.setText("設定アプリよりパーミッションを\nONにしてください");
                    }
                break;
            default:
                break;
        }
    }

    private void getContentsInfo() {
        cursor = null;
        ContentResolver resolver = getContentResolver();
        cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null
        );

        cursor.moveToFirst();
        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
        Long id = cursor.getLong(fieldIndex);
        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

        ImageView mImageView = (ImageView) findViewById(R.id.imageView);
        mImageView.setImageURI(imageUri);
        }
    }





