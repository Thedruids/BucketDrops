package thomas.bucketdrops;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import thomas.bucketdrops.adapters.AdapterDrops;
import thomas.bucketdrops.adapters.AddListener;
import thomas.bucketdrops.adapters.CompleteListener;
import thomas.bucketdrops.adapters.Divider;
import thomas.bucketdrops.adapters.Filter;
import thomas.bucketdrops.adapters.MarkListener;
import thomas.bucketdrops.adapters.SimpleTouchCallback;
import thomas.bucketdrops.beans.Drop;
import thomas.bucketdrops.widgets.BucketRecyclerView;
import thomas.bucketdrops.widgets.DialogMark;

public class ActivityMain extends AppCompatActivity {

    public String TAG = "Thomas";
    Toolbar mToolbar;
    Button mBtnAdd;
    BucketRecyclerView mRecycler;
    Realm mRealm;
    RealmResults<Drop> mResults;
    View mEmptyView;
    AdapterDrops mAdapter;

    private AddListener mAddListener = new AddListener() {
        @Override
        public void add() {
            showDialogAdd();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRealm = Realm.getDefaultInstance();
        int filterOption = load();
        loadResults(filterOption);
        mResults = mRealm.where(Drop.class).findAllAsync();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mEmptyView = findViewById(R.id.empty_drops);
        mBtnAdd = (Button) findViewById(R.id.btn_add);
        mRecycler = (BucketRecyclerView) findViewById(R.id.rv_drops);
        mRecycler.addItemDecoration(new Divider(this, LinearLayoutManager.VERTICAL));
        mRecycler.hideIfEmpty(mToolbar);
        mRecycler.showIfEmpty(mEmptyView);
        mAdapter = new AdapterDrops(this, mRealm, mResults, mAddListener, mMarkListener);
        mRecycler.setAdapter(mAdapter);
        SimpleTouchCallback callback = new SimpleTouchCallback(mAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mRecycler);

        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAdd();
            }
        });

        setSupportActionBar(mToolbar);
        //This is the method call for the glide background initialisation.
        initBackgroundImage();
    }

    private RealmChangeListener mChangeListener = new RealmChangeListener() {
        @Override
        public void onChange() {
            Log.d(TAG, "onChange: was called");
            mAdapter.update(mResults);

        }
    };

    private MarkListener mMarkListener = new MarkListener() {
        @Override
        public void onMark(int position) {
            showDialogMark(position);
        }
    };

    private CompleteListener mCompleteListener = new CompleteListener() {
        @Override
        public void onComplete(int position) {
            mAdapter.markComplete(position);

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        boolean handled = true;
        int filterOption = Filter.NONE;
        switch (id) {
            case R.id.action_add:
                showDialogAdd();
                break;

            case R.id.action_sort_ascending_date:
                filterOption = Filter.LEAST_TIME_LEFT;
                save(Filter.LEAST_TIME_LEFT);
                break;

            case R.id.action_sort_descending_date:
                filterOption = Filter.MOST_TIME_LEFT;
                save(Filter.MOST_TIME_LEFT);
                break;

            case R.id.action_show_complete:
                filterOption = Filter.COMPLETE;
                save(Filter.COMPLETE);
                Log.d("Thomas", "onOptionsItemSelected: Complete");
                break;

            case R.id.action_show_incomplete:
                filterOption = Filter.INCOMPLETE;
                save(Filter.INCOMPLETE);
                break;

            default:
                handled = false;
                break;

        }
        loadResults(filterOption);
        return handled;
    }


    private void save(int filterOption) {
        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("filter", filterOption);
        editor.apply();
    }

    private int load() {
        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        int filterOption = pref.getInt("filter", Filter.NONE);
        return filterOption;
    }

    private void loadResults(int filterOption) {
        Log.d("Thomas", "loadResults: was called. " + filterOption);
        switch (filterOption) {
            case Filter.NONE:
                mResults = mRealm.where(Drop.class).findAllAsync();
                break;
            case Filter.LEAST_TIME_LEFT:
                Log.d("Thomas", "loadResults: LEAST_TIME_LEFT was called " + filterOption);
                mResults = mRealm.where(Drop.class).findAllSortedAsync("when");
                break;
            case Filter.MOST_TIME_LEFT:
                Log.d("Thomas", "loadResults: MOST_TIME_LEFT was called " + filterOption);
                mResults = mRealm.where(Drop.class).findAllSortedAsync("when", Sort.DESCENDING);
                break;
            case Filter.COMPLETE:
                mResults = mRealm.where(Drop.class).equalTo("completed", true).findAllAsync();
                Log.d("Thomas", "loadResults: was called, Filter.Complete ");
                break;
            case Filter.INCOMPLETE:
                mResults = mRealm.where(Drop.class).equalTo("completed", false).findAllAsync();
                break;
        }
        mResults.addChangeListener(mChangeListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mResults.addChangeListener(mChangeListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mResults.removeChangeListener(mChangeListener);
    }

    private void showDialogAdd() {
        DialogAdd dialogAdd = new DialogAdd();
        dialogAdd.show(getSupportFragmentManager(), "Add");
    }

    private void showDialogMark(int position) {
        DialogMark dialog = new DialogMark();
        Bundle bundle = new Bundle();
        bundle.putInt("POSITION", position);
        dialog.setArguments(bundle);
        dialog.setCompleteListener(mCompleteListener);
        dialog.show(getSupportFragmentManager(), "Mark");
        Log.d(TAG, "showDialogMark: was called");
    }



    //When the size of the image exceeds the RAM of the emulator, it is necessary to use a program like Glide to load the image otherwise the program will crash
    //This means that the activity must call a method that loads the image as opposed to declaring the image in the XML, it will require an imageview from the XML though.
    private void initBackgroundImage() {
        ImageView background = (ImageView) findViewById(R.id.iv_background);
        assert background != null : "Background image not found";
        Glide.with(this).load(R.drawable.background).centerCrop().into(background);
    }
}

