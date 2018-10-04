package com.franktan.memoryleakexamples.vialongrunningtask;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;

import com.franktan.memoryleakexamples.R;

import java.lang.ref.WeakReference;

public class LeakActivityToAsyncTaskContextActivity extends AppCompatActivity {

    private DoNothingTask doNothingTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leak_to_async_task);

        doNothingTask = new DoNothingTask(getApplicationContext());
        doNothingTask.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // FIXED: should cancel the task in activity onDestroy()
        doNothingTask.cancel(true);
    }

    // FIXED: use static class instead of inner class. Static class does not have reference to the
    // containing activity class
    private static class DoNothingTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<Context> contextWeakReference;
        public DoNothingTask(Context applicationContext) {
            contextWeakReference=new WeakReference<>(applicationContext);
        }

        @Override
        protected Void doInBackground(Void... params) {
            // FIXED: should check if cancelled before next loop
            while (!isCancelled()) {
                SystemClock.sleep(1000);
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result){
            contextWeakReference.get().toString();
        }
    }
}
