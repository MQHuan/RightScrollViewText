package sample.imco.com.rightscrollviewtext;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by mqh on 3/8/16.
 */
public class RightScrollActivity extends Activity implements RightScrollView.OnRightScrollListener{
    private RightScrollView mView;

    public RightScrollActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mView = new RightScrollView(this);
        super.setContentView(this.mView);
        this.mView.setOnRightScrollListener(this);
        this.mView.disableRightScroll();
    }

    protected boolean enableRightScrollWhenResume() {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(this.enableRightScrollWhenResume()) {
            this.mView.enableRightScroll();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.mView.disableRightScroll();
    }

    public void setContentView(int layoutResID) {
        this.mView.setContentView(layoutResID);
    }

    public void setContentView(View view) {
        this.mView.setContentView(view);
    }

    public void setContentView(View view, ViewGroup.LayoutParams params) {
        this.mView.setContentView(view, params);
    }

    protected RightScrollView getRightScrollView() {
        return this.mView;
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
    }

    @Override
    public void onBackPressed() {
        try {
            super.onBackPressed();
            this.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    public void onRightScroll() {
        this.finish();
    }
}
