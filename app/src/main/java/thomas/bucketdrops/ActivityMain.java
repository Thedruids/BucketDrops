package thomas.bucketdrops;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ActivityMain extends AppCompatActivity {

    Toolbar mToolbar;
    Button mBtnAdd;
    RecyclerView mRecylcer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mBtnAdd = (Button) findViewById(R.id.btn_add);
        mRecylcer = (RecyclerView) findViewById(R.id.rv_drops);

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

    private void showDialogAdd() {
        DialogAdd dialogAdd = new DialogAdd();
        dialogAdd.show(getSupportFragmentManager(), "Add");
    }

    //When the size of the image exceeds the RAM of the emulator, it is necessary to use a program like Glide to load the image otherwise the program will crash
    //This means that the activity must call a method that loads the image as opposed to declaring the image in the XML, it will require an imageview from the XML though.
    private void initBackgroundImage(){
        ImageView background = (ImageView) findViewById(R.id.iv_background);
        assert background != null : "Background image not found";
        Glide.with(this).load(R.drawable.background).centerCrop().into(background);
        }
    }

