package tuyaxiong.asynchttp;

import android.app.Activity;
import android.app.ActivityManager;
import android.os.Bundle;
import android.widget.TextView;

public class AsyncHttp extends Activity {

    TextView viewById;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        viewById = (TextView) findViewById(R.id.textView);
        ActivityManager systemService = (ActivityManager) getSystemService(this.ACTIVITY_SERVICE);
        int memoryClass = systemService.getMemoryClass();
        viewById.setText(String.valueOf(memoryClass));

    }


}

