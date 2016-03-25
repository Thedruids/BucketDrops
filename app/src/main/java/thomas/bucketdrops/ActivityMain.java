package thomas.bucketdrops;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import thomas.bucketdrops.adapters.AdapterDrops;
import thomas.bucketdrops.adapters.AddListener;
import thomas.bucketdrops.adapters.Divider;
import thomas.bucketdrops.beans.Drop;
import thomas.bucketdrops.widgets.BucketRecyclerView;

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
        mResults = mRealm.where(Drop.class).findAllAsync();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mEmptyView = findViewById(R.id.empty_drops);
        mBtnAdd = (Button) findViewById(R.id.btn_add);
        mRecycler = (BucketRecyclerView) findViewById(R.id.rv_drops);
        mRecycler.addItemDecoration(new Divider(this, LinearLayoutManager.VERTICAL));
        mRecycler.hideIfEmpty(mToolbar);
        mRecycler.showIfEmpty(mEmptyView);
        mAdapter = new AdapterDrops(this, mResults, mAddListener);
        mRecycler.setAdapter(mAdapter);

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

    private RealmChangeListener mChangeListener = new RealmChangeListener() {
        @Override
        public void onChange() {
            Log.d(TAG, "onChange: was called");
            mAdapter.update(mResults);

        }
    };

    //When the size of the image exceeds the RAM of the emulator, it is necessary to use a program like Glide to load the image otherwise the program will crash
    //This means that the activity must call a method that loads the image as opposed to declaring the image in the XML, it will require an imageview from the XML though.
    private void initBackgroundImage() {
        ImageView background = (ImageView) findViewById(R.id.iv_background);
        assert background != null : "Background image not found";
        Glide.with(this).load(R.drawable.background).centerCrop().into(background);
    }
}

