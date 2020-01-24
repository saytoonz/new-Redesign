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

package com.nsromapa.say.emogifstickerkeyboard.internal.sticker;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
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


public final class StickerFragment extends Fragment implements StickerAdapter.ItemSelectListener {
    private Context mContext;
    private ViewFlipper mViewFlipper;
    private TextView mErrorTv;
    private Button goto_sticker_shop;

    private StickerAdapter mStickerAdapter;
    private StickerSelectListener mStickerSelectListener;

    private List<File> stickers = new ArrayList<>();

    public StickerFragment() {
    }

    public static StickerFragment getNewInstance() {
        return new StickerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sticker, container, false);
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

        mViewFlipper = view.findViewById(R.id.sticker_view_flipper);
        mErrorTv = view.findViewById(R.id.sticker_error_textview);
        goto_sticker_shop = view.findViewById(R.id.goto_sticker_shop);



        File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/FrenzApp/Media/stickers/");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        if (dir.exists()) {
            stickers.clear();
            File[] files = dir.listFiles();
           if (files != null){
               for (File file : files) {
                   if (!file.getName().contains(".nomedia") && file.getName().contains("sayt"))
                       stickers.add(file);
               }
           }
        }


        mStickerAdapter = new StickerAdapter(stickers,getContext(),this);

        GridView stikerGrideView = view.findViewById(R.id.sticker_gridView);
        stikerGrideView.setColumnWidth(getResources().getInteger(R.integer.gif_sticker_sound_recycler_view_span_size));
        stikerGrideView.setAdapter(mStickerAdapter);



        if (stickers.size() != 0) {
            mViewFlipper.setDisplayedChild(0);
        } else {
            goto_sticker_shop.setVisibility(View.VISIBLE);
            mErrorTv.setText(getString(R.string.no_sticker_found));
            mViewFlipper.setDisplayedChild(1);

        }
    }

    @SuppressWarnings("ConstantConditions")
    public void setStickerSelectListener(@Nullable StickerSelectListener stickerSelectListener) {
        mStickerSelectListener = stickerSelectListener;
    }


    @Override
    public void OnListItemSelected(@NonNull File sticker) {
       if (mStickerSelectListener!=null)
           mStickerSelectListener.onStickerSelectListner(sticker);
    }
}
