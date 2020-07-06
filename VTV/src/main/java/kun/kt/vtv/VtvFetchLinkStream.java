package kun.kt.vtv;

import android.os.AsyncTask;

import java.util.ArrayList;

public class VtvFetchLinkStream extends AsyncTask<Void, Stream, ArrayList<Stream>> {
    private int channel;
    private OnSuccessListener onSuccessListener;

    public VtvFetchLinkStream(int channel, OnSuccessListener onSuccessListener) {
        this.channel = channel;
        this.onSuccessListener = onSuccessListener;
    }

    @Override
    protected ArrayList<Stream> doInBackground(Void... voids) {
        return new VTVGo().getLink(this.channel + "");
    }

    @Override
    protected void onPostExecute(ArrayList<Stream> streams) {
        super.onPostExecute(streams);
        onSuccessListener.onGetSuccess(streams.get(0));
    }

    public interface OnSuccessListener {
        void onGetSuccess(Stream stream);
    }
}
