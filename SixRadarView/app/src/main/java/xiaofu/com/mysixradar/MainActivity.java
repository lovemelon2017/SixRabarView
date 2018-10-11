package xiaofu.com.mysixradar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    SixRadarView sixRadarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sixRadarView = findViewById(R.id.sixBar);
        //点击事件
        sixRadarView.setRadarClickInterface(new RadarClickInterface() {
            @Override
            public void onRightTop() {
                Toast.makeText(MainActivity.this, "点击了右上角", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onRightCenter() {
                Toast.makeText(MainActivity.this, "点击了右中间", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onRightBottom() {
                Toast.makeText(MainActivity.this, "点击了右下", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onLeftTop() {
                Toast.makeText(MainActivity.this, "点击了左上角", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onLeftCenter() {
                Toast.makeText(MainActivity.this, "点击了左中间", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onLeftBottom() {
                Toast.makeText(MainActivity.this, "点击了左下", Toast.LENGTH_LONG).show();
            }
        });
    }
}
