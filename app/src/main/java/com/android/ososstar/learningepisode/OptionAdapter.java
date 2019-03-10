package com.android.ososstar.learningepisode;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class OptionAdapter extends RecyclerView.Adapter<com.android.ososstar.learningepisode.OptionAdapter.ViewHolder> {
    private com.android.ososstar.learningepisode.OptionAdapter.OnItemClickListener mListener;
    private Context mContext;
    private List<Option> mOptionList;

    public interface OnItemClickListener {
        void onItemClickOptions(int position);
    }

    public void setOnItemClickListener(com.android.ososstar.learningepisode.OptionAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    /**
     * Create a new {@link com.android.ososstar.learningepisode.OptionAdapter} object.
     *
     * @param context is the current context (i.e. Activity) that the adapter is being created in.
     * @param optionList is the list of {@link Option} to be displayed.
     */
    public OptionAdapter(Context context, ArrayList<Option> optionList) {
        mContext = context;
        mOptionList = optionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_option, parent, false);


        return new ViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull com.android.ososstar.learningepisode.OptionAdapter.ViewHolder holder, int position) {
        Option currentOption = mOptionList.get(position);

        holder.optionTitle.setText(currentOption.getTitle());
        holder.optionImage.setImageResource(currentOption.getImageResourceId());
    }

    @Override
    public int getItemCount() {
        return mOptionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView optionImage;
        private TextView optionTitle;

        public ViewHolder(View itemView, final com.android.ososstar.learningepisode.OptionAdapter.OnItemClickListener listener) {
            super(itemView);

            optionTitle = itemView.findViewById(R.id.option_title);
            optionImage = itemView.findViewById(R.id.option_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            mListener.onItemClickOptions(position);
                        }
                    }
                }
            });
        }
    }

    final static int ITEMS_PER_PAGE = 3;

}
