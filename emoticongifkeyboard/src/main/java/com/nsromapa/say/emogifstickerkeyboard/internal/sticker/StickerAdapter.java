package com.nsromapa.say.emogifstickerkeyboard.internal.sticker;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nsromapa.say.R;

import java.io.File;
import java.util.List;

public class StickerAdapter extends BaseAdapter {

    private List<File> stickers;
    private Context context;
    private LayoutInflater thisInflater;

    private ItemSelectListener mListener;

    public StickerAdapter(List<File>stickers, Context context, ItemSelectListener listener) {
        this.stickers = stickers;
        this.context = context;
        this.thisInflater = LayoutInflater.from(context);
        this.mListener = listener;
    }

    @Override
    public int getCount() {
        return stickers.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        View v = convertView;
        StickerAdapter.ViewHolder holder;
        if (v == null) {
            v = thisInflater.inflate(R.layout.item_sticker, parent, false);

            holder = new StickerAdapter.ViewHolder();
            holder.stickerIv = v.findViewById(R.id.sticker_iv);

            v.setTag(holder);
        } else {
            holder = (StickerAdapter.ViewHolder) v.getTag();
        }



        final File sticker = stickers.get(position);
        if (sticker != null) {
            Glide.with(context)
                    .load(stickers.get(position))
                    .into(holder.stickerIv);

            holder.stickerIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.OnListItemSelected(sticker);
                }
            });
            holder.stickerIv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.item_dialog_delete_sticker);

                    TextView name = dialog.findViewById(R.id.name);
                    ImageView image = dialog.findViewById(R.id.goProDialogImage);
                    ImageView cancel_dialog = dialog.findViewById(R.id.cancel_dialog);
                    ImageView delete = dialog.findViewById(R.id.delete);

                    name.setText(context.getResources().getString(R.string.delete_item) +"" +
                            " - "+ sticker.getName().replace(".sayt",""));
                    cancel_dialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sticker.delete();
                            stickers.remove(sticker);
                            notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    });
                    Glide.with(context)
                            .load(sticker)
                            .into(image);
                    dialog.show();
                    return true;
                }
            });
        }
        return v;
    }



    /**
     * Callback listener to get notify when item is clicked.
     */
    interface ItemSelectListener {

        void OnListItemSelected(@NonNull File sticker);
    }


    private class ViewHolder {

        /**
         * Image view to display GIFs.
         */
        private ImageView stickerIv;
    }
}
