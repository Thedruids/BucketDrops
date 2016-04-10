package thomas.bucketdrops.widgets;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import thomas.bucketdrops.R;
import thomas.bucketdrops.adapters.CompleteListener;

public class DialogMark extends DialogFragment {

    private ImageButton mBtnclose;
    private Button mBtnCompleted;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);
    }

    private View.OnClickListener mBtnListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
           switch (v.getId()){
               case (R.id.btn_completed):
                   markAsComplete();
                   break;
           }
            dismiss();
        }
    };
    private CompleteListener mListener;

    private void markAsComplete() {


        Bundle arguments = getArguments();
        if (mListener != null && arguments != null){
            int position = arguments.getInt("POSITION");
           mListener.onComplete(position);
        }

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_mark, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBtnclose = (ImageButton) view.findViewById(R.id.btn_close);
        mBtnCompleted = (Button) view.findViewById(R.id.btn_completed);
        mBtnclose.setOnClickListener(mBtnListener);
        mBtnCompleted.setOnClickListener(mBtnListener);
    }


    public void setCompleteListener(CompleteListener mCompleteListener) {
        mListener = mCompleteListener;
    }
}
