package net.smilfinken.sagittarius;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CompetitionSelectionFragment
    extends Fragment
    implements AdapterView.OnItemClickListener {

    final String SAGITTARIUSAPI = "http://10.0.2.2:9000/api/v1/";
    final String APIREQUEST = "APIREQUEST";
    RequestQueue requestQueue;

    ArrayList<String> competitionList;
    ArrayAdapter<String> competitionListAdapter;

    OnCompetitionSelectedListener eventListener;

    public CompetitionSelectionFragment() { }

    private void initialiseListView(View view) {
        competitionList = new ArrayList<>();
        ListView competitionListView = (ListView) view.findViewById(R.id.competition_list);
        competitionListAdapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_list_item_1, competitionList);
        competitionListView.setAdapter(competitionListAdapter);
        competitionListView.setOnItemClickListener(this);
    }

    private void cancelApiRequests() {
        if (requestQueue != null) {
            requestQueue.cancelAll(APIREQUEST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_competition_selection, container, false);

        initialiseListView(view);

        requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add((new JsonArrayRequest(SAGITTARIUSAPI + "competitions",
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    competitionList.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            competitionList.add(response.getJSONObject(i).getString("label"));
                        }
                    } catch (org.json.JSONException exception) {
                        Logger.getGlobal().log(Level.SEVERE, "error iterating through available competitions in network response: " + exception.getLocalizedMessage());
                    }
                    competitionListAdapter.notifyDataSetChanged();
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Logger.getGlobal().log(Level.SEVERE, "request failed: " + error.getLocalizedMessage());
                }
            }
        )).setTag(APIREQUEST));

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        cancelApiRequests();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        eventListener.onCompetitionSelected(26);
    }

     public interface OnCompetitionSelectedListener {
         public void onCompetitionSelected(Integer competitionId);
     }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnCompetitionSelectedListener) {
            eventListener = (OnCompetitionSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnCompetitionSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        eventListener = null;
    }
}
