package cn.hollo.www;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * Created by orson on 14-11-13.
 */
public class ActivityBase extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    }
}
