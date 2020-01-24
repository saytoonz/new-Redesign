/*
 * Copyright 2017 Keval Patel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.nsromapa.say.emogifstickerkeyboard.internal.sound;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.nsromapa.say.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public final class SoundFragment extends Fragment implements SoundAdapter.ItemSelectListener {
    private Context mContext;
    private ViewFlipper mViewFlipper;
    private TextView mErrorTv;
    private Button goto_sound_shop;

    private SoundAdapter mSoundAdapter;
    private SoundSelectListener mSoundSelectListener;


    private List<File> soundImagesArr = new ArrayList<>();

    public SoundFragment() {
    }

    public static SoundFragment getNewInstance() {
        return new SoundFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sound, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @SuppressWarnings("DanglingJavadoc")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewFlipper = view.findViewById(R.id.sound_view_flipper);
        mErrorTv = view.findViewById(R.id.sound_error_textview);
        goto_sound_shop = view.findViewById(R.id.goto_sound_shop);

        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/FrenzApp/Media/sounds/SoundImages/");

        if (!dir.exists())
            dir.mkdirs();

        if (dir.exists()) {
            soundImagesArr.clear();
            File[] files = dir.listFiles();
            for (File file : files) {
                if (!file.getName().contains(".nomedia"))
                    soundImagesArr.add(file);
            }
        }


        mSoundAdapter = new SoundAdapter(soundImagesArr, getContext(), this);

        GridView souncGridView = view.findViewById(R.id.sound_gridView);
        souncGridView.setColumnWidth(getResources().getInteger(R.integer.gif_sticker_sound_recycler_view_span_size));
        souncGridView.setAdapter(mSoundAdapter);


        if (soundImagesArr.size() != 0) {
            mViewFlipper.setDisplayedChild(0);
        } else {
            goto_sound_shop.setVisibility(View.VISIBLE);
            mErrorTv.setText(R.string.no_sound_found);
            mViewFlipper.setDisplayedChild(1);
        }

    }

    @SuppressWarnings("ConstantConditions")
    public void setSoundSelectListener(@Nullable SoundSelectListener soundSelectListener) {
        mSoundSelectListener = soundSelectListener;
    }


    @Override
    public void OnListItemSelected(@NonNull File souncImage) {
        if (mSoundSelectListener != null)
            mSoundSelectListener.onSoundSelectListner(souncImage);
    }
}
