package net.smilfinken.sagittarius;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

public class SquadSelectionFragment
    extends SelectionFragment
    implements AdapterView.OnItemClickListener {

    private static final String ARG_COMPETITIONID = "competitionId";
    private Integer competitionId;

    public SquadSelectionFragment() {
        tag = TAG_SQUAD;
    }

    public static SquadSelectionFragment newInstance(Integer competitionId) {
        SquadSelectionFragment fragment = new SquadSelectionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COMPETITIONID, competitionId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            competitionId = getArguments().getInt(ARG_COMPETITIONID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ((TextView)view.findViewById(R.id.list_header)).setText(getString(R.string.common_label_squads));

        createApiRequest("competition/" + competitionId + "/squads");

        return view;
    }

    @Override
    protected Integer getItemId(Integer position) {
        return 64;
    }
}
