package thomas.bucketdrops;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.Calendar;

import io.realm.Realm;
import thomas.bucketdrops.beans.Drop;


public class DialogAdd extends DialogFragment{

    //The add it fragment contains 4 variables which will be used to give functionality to the views in the dialog_add.xml file.

    private ImageButton mBtnClose;
    private EditText mInputWhat;
    private DatePicker mInputWhen;
    private Button mButtonAdd;


        //An empty constructor is required when creating fragments.
    public DialogAdd() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Java needs the context in order to find view by id. The context can be returned through the view object.
        mInputWhen = (DatePicker) view.findViewById(R.id.bpv_date);
        mInputWhat = (EditText) view.findViewById(R.id.et_drop);
        mButtonAdd = (Button) view.findViewById(R.id.btn_add_it);
        mBtnClose = (ImageButton) view.findViewById(R.id.btn_close);

        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAction();

                dismiss();
            }
        });

    }

    //TODO process date
    private void addAction() {
        String what = mInputWhat.getText().toString();
        String date = mInputWhen.getDayOfMonth() + "/" + mInputWhen.getMonth() + "/" + mInputWhen.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, mInputWhen.getDayOfMonth());
        calendar.set(Calendar.MONTH, mInputWhen.getMonth());
        calendar.set(Calendar.YEAR, mInputWhen.getYear());
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long now = System.currentTimeMillis();
        Realm realm = Realm.getDefaultInstance();
        Drop drop = new Drop(what, now, calendar.getTimeInMillis(), false);
        realm.beginTransaction();
        realm.copyToRealm(drop);
        realm.commitTransaction();
        realm.close();

    }
}
