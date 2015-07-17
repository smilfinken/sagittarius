package net.smilfinken.sagittarius;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by johlin on 2015-07-15.
 */
public class CompetitionListAdapter extends RecyclerView.Adapter<CompetitionListAdapter.ViewHolder> {
    private String[] strings;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public ViewHolder(View view) {
            super(view);
            this.view = view;
        }
    }

    public CompetitionListAdapter(String[] data) {
        strings = data;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_competition, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView textView = (TextView)holder.view.findViewById(R.id.text_competition_name);
        textView.setText(strings[position]);
    }

    @Override
    public int getItemCount() {
        return strings.length;
    }
}
