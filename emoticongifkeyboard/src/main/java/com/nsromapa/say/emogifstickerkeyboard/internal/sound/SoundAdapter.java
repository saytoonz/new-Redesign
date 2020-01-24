package com.nsromapa.say.emogifstickerkeyboard.internal.sound;

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

import com.bumptech.glide.Glide;
import com.nsromapa.say.R;

import java.io.File;
import java.util.List;

public class SoundAdapter extends BaseAdapter {

    private List<File> soundImages;
    private Context context;
    private LayoutInflater thisInflater;

    private ItemSelectListener mListener;

    public SoundAdapter(List<File> soundImages, Context context, ItemSelectListener listener) {
        this.soundImages = soundImages;
        this.context = context;
        this.thisInflater = LayoutInflater.from(context);
        this.mListener = listener;
    }

    @Override
    public int getCount() {
        return soundImages.size();
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
        SoundAdapter.ViewHolder holder;
        if (v == null) {
            v = thisInflater.inflate(R.layout.item_sticker, parent, false);

            holder = new SoundAdapter.ViewHolder();
            holder.soundIv = v.findViewById(R.id.sticker_iv);
            v.setTag(holder);
        } else {
            holder = (SoundAdapter.ViewHolder) v.getTag();
        }


        final File soundImage = soundImages.get(position);
        if (soundImage != null) {
            Glide.with(context)
                    .asGif()
                    .load(soundImages.get(position))
                    .into(holder.soundIv);

            holder.soundIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.OnListItemSelected(soundImage);
                }
            });

            holder.soundIv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.item_dialog_delete_sticker);

                    TextView name = dialog.findViewById(R.id.name);
                    ImageView image = dialog.findViewById(R.id.goProDialogImage);
                    ImageView cancel_dialog = dialog.findViewById(R.id.cancel_dialog);
                    ImageView delete = dialog.findViewById(R.id.delete);

                    name.setText(context.getResources().getString(R.string.delete_item) + "" +
                            " - " + soundImages.get(position).getName().replace(".gif", ""));
                    cancel_dialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            File sound = new File(soundImages.get(position)
                                    .getAbsolutePath().replace("SoundImages",
                                            "SoundAudios").replace(".gif",
                                            ".mp3"));
                            if (sound.exists())
                                sound.delete();


                            soundImages.get(position).delete();
                            soundImages.remove(soundImages.get(position));
                            notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    });
                    Glide.with(context)
                            .asGif()
                            .load(soundImages.get(position))
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
        private ImageView soundIv;
    }
}
