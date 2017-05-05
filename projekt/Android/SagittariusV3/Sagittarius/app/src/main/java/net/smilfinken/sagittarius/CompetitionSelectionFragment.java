package net.smilfinken.sagittarius;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class CompetitionSelectionFragment
    extends SelectionFragment
    implements AdapterView.OnItemClickListener {

    public CompetitionSelectionFragment() {
        tag = TAG_COMPETITION;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ((TextView)view.findViewById(R.id.list_header)).setText(getString(R.string.label_heading_competitions));

        createApiRequest("competitions");

        return view;
    }

    @Override
    protected JSONObject convertItem(JSONObject item) {
        JSONObject result = super.convertItem(item);

        try {
            result.put(JSONAdapter.LABEL_TITLE, item.getString("label"));
        } catch (JSONException exception) { }

        try {
            result.put(JSONAdapter.LABEL_SUBTITLE, item.getString("competitionDate"));
        } catch (JSONException exception) { }

        return result;
    }
}
