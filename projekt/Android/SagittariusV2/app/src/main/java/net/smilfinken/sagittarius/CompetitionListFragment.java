package net.smilfinken.sagittarius;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Locale;

/**
 * Created by johlin on 2015-07-16.
 */
public class CompetitionListFragment extends Fragment {

    private RecyclerView recyclerView;

    public CompetitionListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scoring, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.availableCompetitions);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new CompetitionListAdapter(new String[]{"foo", "bar"}));

        return rootView;
    }
}
