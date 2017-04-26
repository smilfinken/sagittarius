package net.smilfinken.sagittarius;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

public class CompetitionSelectionFragment
    extends SelectionFragment
    implements AdapterView.OnItemClickListener {

    public CompetitionSelectionFragment() {
        tag = TAG_COMPETITION;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ((TextView)view.findViewById(R.id.list_header)).setText(getString(R.string.common_label_competitions));

        createApiRequest("competitions");

        return view;
    }

    @Override
    protected Integer getItemId(Integer position) {
        return 26;
    }
}
