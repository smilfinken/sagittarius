package net.smilfinken.sagittarius;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static android.view.View.Y;

public class ScoringActivity
    extends AppCompatActivity
    implements
        SagittariusRestApi.OnApiResultListener,
        AdapterView.OnItemClickListener,
        View.OnClickListener,
        View.OnTouchListener {

    SagittariusRestApi sagittariusRestApi;

    public static final String ARG_COMPETITIONID = "competitionId";
    public static final String ARG_SQUADID = "squadId";

    Integer competitionId;
    Integer squadId;

    DrawerLayout stageDrawerLayout;
    ListView stageListView;
    ArrayList<JSONObject> stageList;

    ListView competitorListView;
    ArrayList<JSONObject> competitorList;

    LinearLayout headerLayout;
    final Float slideThreshold = 20f;
    Float slideStart = 0f;
    Boolean isSliding = false;

    private void getCompetition() {
        sagittariusRestApi.getCompetition(competitionId);
    }

    private void getSquad() {
        sagittariusRestApi.getSquad(competitionId, squadId);
        sagittariusRestApi.getCompetitors(competitionId, squadId);
    }

    private void getStages() {
        sagittariusRestApi.getStages(competitionId);
    }

    private JSONObject createBaseItem(JSONObject item) {
        JSONObject result = new JSONObject();

        try {
            result.put(JSONAdapter.LABEL_ID, item.getString("id"));
        } catch (JSONException exception) { }

        return result;
    }
    private JSONObject convertStageItem(JSONObject item) {
        JSONObject result = createBaseItem(item);

        try {
            result.put(JSONAdapter.LABEL_TITLE, item.getString("index"));
        } catch (JSONException exception) { }

        try {
            result.put(JSONAdapter.LABEL_SUBTITLE, item.getString("label"));
        } catch (JSONException exception) { }

        return result;
    }

    private JSONObject convertCompetitorItem(JSONObject item) {
        JSONObject result = createBaseItem(item);

        try {
            result.put(JSONAdapter.LABEL_TITLE, item.getString("name"));
        } catch (JSONException exception) { }

        try {
            result.put(JSONAdapter.LABEL_SUBTITLE, item.getString("class"));
        } catch (JSONException exception) { }

        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoring);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        headerLayout = (LinearLayout)findViewById(R.id.scoring_header);
        View toggleButton = findViewById(R.id.scoring_header_toggle);
        toggleButton.setOnClickListener(this);
        toggleButton.setOnTouchListener(this);
        View scoringHeader = findViewById(R.id.scoring_content);
        scoringHeader.setOnClickListener(this);

        stageDrawerLayout = (DrawerLayout) findViewById(R.id.scoring_stage_drawer);

        stageList = new ArrayList<>();
        stageListView = (ListView) findViewById(R.id.scoring_drawer_list);
        stageListView.setAdapter(new JSONAdapter(getApplicationContext(), R.layout.listitem, stageList));
        stageListView.setOnItemClickListener(this);

        competitorList = new ArrayList<>();
        competitorListView = (ListView) findViewById(R.id.scoring_competitor_list);
        competitorListView.setAdapter(new JSONAdapter(getApplicationContext(), R.layout.listitem, competitorList));
        competitorListView.setOnItemClickListener(this);

        Intent intent = getIntent();
        competitionId = intent.getIntExtra(ARG_COMPETITIONID, -1);
        squadId = intent.getIntExtra(ARG_SQUADID, -1);

        sagittariusRestApi = new SagittariusRestApi(this);
        getCompetition();
        getSquad();
        getStages();
    }

    @Override
    public void onCompetitionFetched(JSONObject competition) {
        try {
            ((TextView)findViewById(R.id.competition_label)).setText(competition.getString("label"));
        } catch (JSONException exception) {
            Logger.getGlobal().log(Level.SEVERE, "unable to get info from competition: " + exception.getLocalizedMessage());
        }
    }

    @Override
    public void onSquadFetched(JSONObject squad) {
        try {
            ((TextView)findViewById(R.id.squad_label)).setText(squad.getString("label") + " " + squad.getString("rollcall"));
        } catch (JSONException exception) {
            Logger.getGlobal().log(Level.SEVERE, "unable to get info from competition: " + exception.getLocalizedMessage());
        }
    }

    @Override
    public void onCompetitorsFetched(JSONArray competitors) {
        competitorList.clear();
        try {
            for (int i = 0; i < competitors.length(); i++) {
                competitorList.add(convertCompetitorItem(competitors.getJSONObject(i)));
                ((JSONAdapter)competitorListView.getAdapter()).notifyDataSetChanged();
            }
        } catch (JSONException exception) {
            Logger.getGlobal().log(Level.SEVERE, "failed to add stage: " + exception.getLocalizedMessage());
        }
    }

    @Override
    public void onStagesFetched(JSONArray stages) {
        stageList.clear();
        try {
            for (int i = 0; i < stages.length(); i++) {
                stageList.add(convertStageItem(stages.getJSONObject(i)));
                ((JSONAdapter) stageListView.getAdapter()).notifyDataSetChanged();
            }
        } catch (JSONException exception) {
            Logger.getGlobal().log(Level.SEVERE, "failed to add stage: " + exception.getLocalizedMessage());
        }

        if (!stageList.isEmpty()) {
            try {
                sagittariusRestApi.getStage(competitionId, stageList.get(0).getInt("id"));
            } catch (JSONException exception) {
                Logger.getGlobal().log(Level.SEVERE, "unable to get info from stage: " + exception.getLocalizedMessage());
            }
        }
    }

    @Override
    public void onStageFetched(JSONObject stage) {
        ((TextView)findViewById(R.id.stage_index)).setText(getResources().getString(R.string.label_heading_stage_number, Utils.getJSONInteger(stage, "index")));
        ((TextView)findViewById(R.id.stage_label)).setText(Utils.getJSONString(stage, "label"));
        ((TextView)findViewById(R.id.stage_brief)).setText(Utils.getJSONString(stage, "brief"));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.scoring_competitor_list:
                Logger.getGlobal().log(Level.INFO, "competitor at position " + position + " clicked");
                break;
            case R.id.scoring_drawer_list:
                try {
                    sagittariusRestApi.getStage(competitionId, stageList.get(position).getInt("id"));
                    stageDrawerLayout.closeDrawers();
                } catch (JSONException exception) {
                    Logger.getGlobal().log(Level.SEVERE, "unable to get stage at position " + position + " from API: " + exception.getLocalizedMessage());
                }
                break;
            default:
                Logger.getGlobal().log(Level.INFO, "unhandled click on item at position " + position + " in view " + parent.toString());
        }
    }

    private Boolean isHeaderCollapsed() {
        return headerLayout.getHeight() <= headerLayout.getMinimumHeight();
    }

    private void toggleHeader() {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) headerLayout.getLayoutParams();
        if (isHeaderCollapsed()) {
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else {
            layoutParams.height = headerLayout.getMinimumHeight();
        }
        headerLayout.setLayoutParams(layoutParams);
    }

    private Boolean isThresholdReached(Float y) {
        if (isHeaderCollapsed()) {
            return y - slideStart > slideThreshold;
        } else {
            return slideStart - y > slideThreshold;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                slideStart = event.getY();
                break;

            case MotionEvent.ACTION_UP:
                if (isSliding) {
                    toggleHeader();
                    isSliding = false;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (isThresholdReached(event.getY())) {
                    isSliding = true;
                }
                break;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.scoring_header_toggle:
            case R.id.scoring_content:
                toggleHeader();
                break;
            default:
                Logger.getGlobal().log(Level.SEVERE, "unhandled click on view " + view.toString());
        }
    }
}
