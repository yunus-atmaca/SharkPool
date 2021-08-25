package com.sharkpool21game;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.sharkpool21game.utils.Constants;
import com.sharkpool21game.utils.SharedValues;

public class Review extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "Review-Fragment";

    private View root;

    private ImageView title;
    private EditText editText;
    private ImageView send;

    private String lan;
    private boolean disabled;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(330));
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    public int dpToPx(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.review, container, false);

        getAppValues();
        init();

        return root;
    }

    private void init() {
        root.findViewById(R.id.close).setOnClickListener(this);
        disabled = true;

        send = root.findViewById(R.id.send);
        send.setOnClickListener(this);

        title = root.findViewById(R.id.title);
        title.setImageResource(lan.equals(Constants.LAN_ENG) ? R.drawable.review_text_en
                : (lan.equals(Constants.LAN_RU) ? R.drawable.review_text_ru : R.drawable.review_text_ch));

        editText = root.findViewById(R.id.editText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 0){
                    disabled = true;
                    send.setImageResource(R.drawable.ic_send_disable);
                }else{
                    disabled = false;
                    send.setImageResource(R.drawable.ic_send);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void getAppValues() {
        if (getContext() == null)
            return;

        lan = SharedValues.getString(getContext(), Constants.KEY_LANGUAGE, Constants.LAN_ENG);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.close) {
            dismiss();
            onDestroy();
        } else if (view.getId() == R.id.send) {
            if(disabled)
                return;
            onSend();
        }  else {
            Log.d(TAG, "Unimplemented call");
        }
    }

    private void onSend() {
        dismiss();
        onDestroy();
    }
}
