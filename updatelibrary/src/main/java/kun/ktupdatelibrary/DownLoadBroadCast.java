package kun.ktupdatelibrary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DownLoadBroadCast extends BroadcastReceiver {
    public static final String ACTION_SEND_PROGRESS = "progress";
    public static final String ACTION_FINISH = "finish";
    public static final String EXTRA_PROGRESS = "extra_progress";
    public static final String ACTION_WAITING = "waiting";
    public static final String ACTION_RETRY = "retry";
    public static final String ACTION_TIMEOUT = "timeout";

    private OnReceive listener;

    public DownLoadBroadCast(OnReceive listener) {
        this.listener = listener;
    }

    @Override

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;
        switch (action) {
            case ACTION_SEND_PROGRESS:
                int progress = intent.getIntExtra(EXTRA_PROGRESS, 0);
                listener.onReceiveProgress(progress);
                break;
            case ACTION_FINISH:
                listener.onFinish();
                break;
            case ACTION_WAITING:
                listener.onWaiting();
                break;
            case ACTION_RETRY:
                listener.onRetry();
        }
    }

    public interface OnReceive {
        void onReceiveProgress(int progress);

        void onFinish();

        void onWaiting();

        void onRetry();
    }
}
