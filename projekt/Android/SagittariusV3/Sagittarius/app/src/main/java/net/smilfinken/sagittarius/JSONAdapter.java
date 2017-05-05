package net.smilfinken.sagittarius;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JSONAdapter extends ArrayAdapter<JSONObject> {

    public static final String LABEL_ID = "id";
    public static final String LABEL_TITLE = "title";
    public static final String LABEL_SUBTITLE = "subtitle";

    public JSONAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public JSONAdapter(Context context, int resource, List<JSONObject> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            view = vi.inflate(R.layout.listitem, null);
        }

        JSONObject item = getItem(position);

        if (item != null) {
            TextView title = (TextView) view.findViewById(R.id.listitem_title);
            TextView subtitle = (TextView) view.findViewById(R.id.listitem_subtitle);

            try {
                if (title != null) {
                    title.setText(item.getString(LABEL_TITLE));
                }
                if (subtitle != null) {
                    subtitle.setText(item.getString(LABEL_SUBTITLE));
                }
            } catch (JSONException exception) {
                Logger.getGlobal().log(Level.WARNING, "unable to get label element from item: " + item.toString());
            }
        }

        return view;
    }
}
