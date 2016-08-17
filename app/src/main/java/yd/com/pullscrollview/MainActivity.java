package yd.com.pullscrollview;

import android.app.Activity;
import android.os.Bundle;


public class MainActivity extends Activity {

    private PullScrollView test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test = (PullScrollView) findViewById(R.id.test);

    }

}
