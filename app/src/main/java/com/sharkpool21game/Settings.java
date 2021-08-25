package com.sharkpool21game;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.sharkpool21game.utils.Constants;
import com.sharkpool21game.utils.SharedValues;

public class Settings extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "Setting-Fragment";

    private View root;

    private ImageView language;
    private ImageView lanText;
    private ImageView sound;
    private ImageView soundText;
    private ImageView controller;
    private ImageView contText;

    private boolean soundOn;
    private String controllerText;
    private String lan;

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
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(300));
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
        root = inflater.inflate(R.layout.settings, container, false);

        getAppValues();
        init();

        return root;
    }

    private void init() {
        root.findViewById(R.id.close).setOnClickListener(this);

        language = root.findViewById(R.id.language);
        language.setOnClickListener(this);
        language.setImageResource(lan.equals(Constants.LAN_ENG) ? R.drawable.language_ic_en
                : (lan.equals(Constants.LAN_RU) ? R.drawable.language_ic_ru : R.drawable.language_ic_ch));

        sound = root.findViewById(R.id.sound);
        sound.setOnClickListener(this);
        sound.setImageResource(soundOn ? R.drawable.ic_sound_on : R.drawable.ic_sound_off);

        controller = root.findViewById(R.id.controller);
        controller.setOnClickListener(this);
        controller.setImageResource(controllerText.equals("1") ? R.drawable.controller_1 : R.drawable.controller_2);

        lanText = root.findViewById(R.id.lanText);
        lanText.setImageResource(lan.equals(Constants.LAN_ENG) ? R.drawable.language_text_en
                : (lan.equals(Constants.LAN_RU) ? R.drawable.language_text_ru : R.drawable.language_text_ch));

        soundText = root.findViewById(R.id.soundText);
        soundText.setImageResource(lan.equals(Constants.LAN_ENG) ? R.drawable.sound_text_en
                : (lan.equals(Constants.LAN_RU) ? R.drawable.sound_text_ru : R.drawable.sound_text_ch));

        contText = root.findViewById(R.id.contText);
        contText.setImageResource(lan.equals(Constants.LAN_ENG) ? R.drawable.control_text_en
                : (lan.equals(Constants.LAN_RU) ? R.drawable.control_text_ru : R.drawable.control_text_ch));

    }

    private void getAppValues() {
        if (getContext() == null)
            return;

        soundOn = SharedValues.getBoolean(getContext(), Constants.KEY_SOUND, true);
        controllerText = SharedValues.getString(getContext(), Constants.KEY_CONTROL, "1");
        lan = SharedValues.getString(getContext(), Constants.KEY_LANGUAGE, Constants.LAN_ENG);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.close) {
            dismiss();
            onDestroy();
        } else if (view.getId() == R.id.sound) {
            onSound();
        } else if (view.getId() == R.id.language) {
            onLanguage();
        } else if (view.getId() == R.id.controller) {
            onController();
        } else {
            Log.d(TAG, "Unimplemented call");
        }
    }

    private void onLanguage() {
        if (lan.equals(Constants.LAN_ENG)) {
            lan = Constants.LAN_RU;
            language.setImageResource(R.drawable.language_ic_ru);
        } else if (lan.equals(Constants.LAN_RU)) {
            lan = Constants.LAN_CN;
            language.setImageResource(R.drawable.language_ic_ch);
        } else {
            lan = Constants.LAN_ENG;
            language.setImageResource(R.drawable.language_ic_en);
        }
        setTexts();
        SharedValues.setString(getContext(), Constants.KEY_LANGUAGE, lan);
    }

    private void setTexts() {
        if (lan.equals(Constants.LAN_ENG)) {
            lanText.setImageResource(R.drawable.language_text_en);
            soundText.setImageResource(R.drawable.sound_text_en);
            contText.setImageResource(R.drawable.control_text_en);
        } else if (lan.equals(Constants.LAN_RU)) {
            lanText.setImageResource(R.drawable.language_text_ru);
            soundText.setImageResource(R.drawable.sound_text_ru);
            contText.setImageResource(R.drawable.control_text_ru);
        } else {
            lanText.setImageResource(R.drawable.language_text_ch);
            soundText.setImageResource(R.drawable.sound_text_ch);
            contText.setImageResource(R.drawable.control_text_ch);
        }
    }

    private void onSound() {
        if (soundOn) {
            soundOn = false;
            sound.setImageResource(R.drawable.ic_sound_off);
        } else {
            soundOn = true;
            sound.setImageResource(R.drawable.ic_sound_on);
        }
        SharedValues.setBoolean(getContext(), Constants.KEY_SOUND, soundOn);
    }

    private void onController(){

        if(controllerText.equals("1")){
            controllerText = "2";
            controller.setImageResource(R.drawable.controller_2);
        }else{
            controllerText = "1";
            controller.setImageResource(R.drawable.controller_1);
        }
        SharedValues.setString(getContext(), Constants.KEY_CONTROL, controllerText);
    }
}
