package net.smilfinken.sagittarius;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class SelectionFragment
        extends Fragment
        implements AdapterView.OnItemClickListener {

    public static final String TAG_LISTVIEW = "listView";
    public static final String TAG_SPINNER = "spinner";

    public static final String TAG_COMPETITION = "COMPETITION";
    public static final String TAG_SQUAD = "SQUAD";

    String tag = "";

    final String SAGITTARIUSAPI = "http://10.0.2.2:9000/api/v1/";
    final String APIREQUEST = "APIREQUEST";
    RequestQueue requestQueue;

    ArrayList<JSONObject> list;
    JSONAdapter adapter;

    ListView listView;
    ProgressBar spinner;

    OnItemSelectedListener eventListener;

    public SelectionFragment() { }

    private void showSpinner(Boolean visible) {
        if (visible) {
            listView.setVisibility(View.GONE);
            spinner.setVisibility(View.VISIBLE);
        } else {
            spinner.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }
    protected void initialiseListView(View view) {
        list = new ArrayList<>();
        ListView listView = (ListView) view.findViewWithTag(TAG_LISTVIEW);
        if (listView != null) {
            adapter = new JSONAdapter(this.getContext(), R.layout.listitem, list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);
        } else {
            Logger.getGlobal().log(Level.SEVERE, "unable to locate listview");
        }
    }

    protected void createApiRequest(String queryPath) {
        showSpinner(true);
        requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add((new JsonArrayRequest(SAGITTARIUSAPI + queryPath,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    showSpinner(false);
                    list.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            list.add(convertItem(response.getJSONObject(i)));
                        }
                    } catch (org.json.JSONException exception) {
                        Logger.getGlobal().log(Level.SEVERE, "error iterating through available items in network response: " + exception.getLocalizedMessage());
                    }
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Logger.getGlobal().log(Level.SEVERE, "request failed: " + error.getLocalizedMessage());
                }
            }
        )).setTag(APIREQUEST));
    }

    private void cancelApiRequests() {
        if (requestQueue != null) {
            requestQueue.cancelAll(APIREQUEST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selection, container, false);
        listView = (ListView)view.findViewWithTag(TAG_LISTVIEW);
        spinner = (ProgressBar)view.findViewWithTag(TAG_SPINNER);
        initialiseListView(view);
        return view;
    }
    @Override
    public void onStop() {
        super.onStop();
        cancelApiRequests();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnItemSelectedListener) {
            eventListener = (OnItemSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnItemSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        eventListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        eventListener.onItemSelected(tag, getItemId(position));
    }

    public interface OnItemSelectedListener {
        void onItemSelected(String tag, Integer itemId);
    }

    protected JSONObject convertItem(JSONObject item) {
        JSONObject result = new JSONObject();

        try {
            result.put(JSONAdapter.LABEL_ID, item.getInt("id"));
        } catch (JSONException exception) {
            return item;
        }

        return result;
    }

    protected Integer getItemId(Integer position) {
        Integer result = -1;

        try {
            result = list.get(position).getInt("id");
        } catch (JSONException exception) { }

        return result;
    }
}
