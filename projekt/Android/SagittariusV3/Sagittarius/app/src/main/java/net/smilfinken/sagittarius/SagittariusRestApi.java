package net.smilfinken.sagittarius;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.NoCache;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.android.volley.VolleyLog.setTag;

public class SagittariusRestApi {
    final String SAGITTARIUSAPI = "http://10.0.2.2:9000/api/v1/";
    final String APIREQUEST = "APIREQUEST";

    RequestQueue requestQueue;

    OnApiResultListener eventListener;

    public void getCompetition(Integer competitionId) {
        requestQueue.add((new JsonObjectRequest(JsonRequest.Method.GET, SAGITTARIUSAPI + "competition/" + competitionId, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    eventListener.onCompetitionFetched(response);
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

    public void getSquad(Integer competitionId, Integer squadId) {
        requestQueue.add((new JsonObjectRequest(JsonRequest.Method.GET, SAGITTARIUSAPI + "competition/" + competitionId + "/squad/" + squadId, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    eventListener.onSquadFetched(response);
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

    public void getCompetitors(Integer competitionId, Integer squadId) {
        requestQueue.add((new JsonArrayRequest(SAGITTARIUSAPI + "competition/" + competitionId + "/squad/" + squadId + "/competitors",
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    eventListener.onCompetitorsFetched(response);
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

    public void getStages(Integer competitionId) {
        requestQueue.add((new JsonArrayRequest(SAGITTARIUSAPI + "competition/" + competitionId + "/stages",
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    eventListener.onStagesFetched(response);
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

    public void getStage(Integer competitionId, Integer stageId) {
        requestQueue.add((new JsonObjectRequest(JsonRequest.Method.GET, SAGITTARIUSAPI + "competition/" + competitionId + "/stage/" + stageId, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    eventListener.onStageFetched(response);
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

    public SagittariusRestApi(Context context) {
        requestQueue = new RequestQueue(new NoCache(), new BasicNetwork(new HurlStack()));
        requestQueue.start();

        if (context instanceof OnApiResultListener) {
            eventListener = (OnApiResultListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnItemSelectedListener");
        }
    }

    public interface OnApiResultListener {
        void onCompetitionFetched(JSONObject competition);
        void onSquadFetched(JSONObject squad);
        void onCompetitorsFetched(JSONArray competitors);
        void onStagesFetched(JSONArray stages);
        void onStageFetched(JSONObject stage);
    }
}
