package thomas.bucketdrops.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.realm.RealmResults;
import thomas.bucketdrops.R;
import thomas.bucketdrops.beans.Drop;


public class AdapterDrops extends RecyclerView.Adapter<AdapterDrops.DropHolder> {
    private LayoutInflater mInflater;
    private RealmResults<Drop> mResults;
    public static final String TAG = "Thomas";

    public AdapterDrops(Context context, RealmResults<Drop> results) {
        mInflater = LayoutInflater.from(context);
        update(results);
    }

    @Override
    public DropHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //the mInflater creates a new XML view on the screen.
        View view = mInflater.inflate(R.layout.row_drop, parent, false);
        DropHolder holder = new DropHolder(view);
        Log.d(TAG, "onCreateViewHolder: ");
        return holder;
    }

    @Override
    public void onBindViewHolder(DropHolder holder, int position) {
        //On bindViewHolder sets the content of the views that have been created in by the onCreateViewHolder
        //It also recycles the views with new content when the user scrolls, as opposed to creating a new view for each entry.
        Drop drop = mResults.get(position);

        holder.mTextWhat.setText(drop.getWhat());
        Log.d(TAG, "onBindViewHolder: " + position);
    }

    @Override
    public int getItemCount() {
        return mResults.size();
    }

    public void update(RealmResults<Drop> results){
        mResults = results;
        notifyDataSetChanged();
    }


    public static class DropHolder extends RecyclerView.ViewHolder {

        TextView mTextWhat;

        public DropHolder(View itemView) {
            super(itemView);
            mTextWhat = (TextView) itemView.findViewById(R.id.tv_what);
        }
    }

}
