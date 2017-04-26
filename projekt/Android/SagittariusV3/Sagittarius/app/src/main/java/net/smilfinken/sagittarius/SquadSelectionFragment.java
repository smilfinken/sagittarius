package net.smilfinken.sagittarius;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class SquadSelectionFragment
    extends Fragment
    implements AdapterView.OnItemClickListener {

    private static final String ARG_COMPETITIONID = "competitionId";
    private Integer competitionid;

    final String SAGITTARIUSAPI = "http://10.0.2.2:9000/api/v1/";
    final String APIREQUEST = "APIREQUEST";
    RequestQueue requestQueue;

    ArrayList<String> squadList;
    ArrayAdapter<String> squadListAdapter;

    private OnSquadSelectedListener eventListener;

    public SquadSelectionFragment() { }

    private void initialiseListView(View view) {
        squadList = new ArrayList<>();
        ListView squadListView = (ListView) view.findViewById(R.id.squad_list);
        squadListAdapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_list_item_1, squadList);
        squadListView.setAdapter(squadListAdapter);
        squadListView.setOnItemClickListener(this);
    }

    private void cancelApiRequests() {
        if (requestQueue != null) {
            requestQueue.cancelAll(APIREQUEST);
        }
    }

    public static SquadSelectionFragment newInstance(Integer competitionid) {
        SquadSelectionFragment fragment = new SquadSelectionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COMPETITIONID, competitionid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            competitionid = getArguments().getInt(ARG_COMPETITIONID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_squad_selection, container, false);

        initialiseListView(view);

        requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add((new JsonArrayRequest(SAGITTARIUSAPI + "competition/" + competitionid + "/squads",
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    squadList.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            squadList.add(response.getJSONObject(i).getString("label") + " " + response.getJSONObject(i).getString("rollcall"));
                        }
                    } catch (org.json.JSONException exception) {
                        Logger.getGlobal().log(Level.SEVERE, "error iterating through available squads in network response: " + exception.getLocalizedMessage());
                    }
                    squadListAdapter.notifyDataSetChanged();
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
        eventListener.onSquadSelected(position);
    }

    public interface OnSquadSelectedListener {
        void onSquadSelected(Integer squadId);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnSquadSelectedListener) {
            eventListener = (OnSquadSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnSquadSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        eventListener = null;
    }
}
