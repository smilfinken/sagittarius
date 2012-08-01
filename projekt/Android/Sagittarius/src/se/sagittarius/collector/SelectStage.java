package se.sagittarius.collector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class SelectStage extends Activity {

	// parameters receieved from calling activity
	Bundle parameters;
	ArrayList<Competitor> competitors = null;
	ArrayList<Stage> stages = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stages);

		Intent intent = getIntent();
		parameters = intent.getBundleExtra(Constants.BUNDLED_DATA);
		listStages();
	}

	private Long getSquadID() {
		return parameters.getLong(Constants.SQUAD_ID);
	}

	private String getSquadLabel() {
		return parameters.getString(Constants.SQUAD_LABEL);
	}

	private Stage[] getStages() {
		if (stages == null) {
			String[] labels = parameters.getStringArray(Constants.STAGE_LABEL);
			int[] targets = parameters.getIntArray(Constants.STAGE_TARGETCOUNT);
			boolean[] points = parameters.getBooleanArray(Constants.STAGE_HASPOINTS);
			if (labels.length != 0 && labels.length == targets.length && targets.length == points.length) {
				stages = new ArrayList<Stage>();
				for (int i = 0; i < labels.length; i++) {
					stages.add(new Stage(i + 1, labels[i], targets[i], points[i]));
				}
			}
		}
		return stages.toArray(new Stage[stages.size()]);
	}

	private int getStageCount() {
		return getStages().length;
	}

	private String getStageLabel(int position) {
		String result = "";

		if (position < getStages().length) {
			result = getStages()[position].getLabel();
		}

		return result;
	}

	private int getStageTargetCount(int position) {
		int result = -1;

		if (position < getStages().length) {
			result = getStages()[position].getTargetCount();
		}

		return result;
	}

	private boolean getStagePoints(int position) {
		boolean result = false;

		if (position < getStages().length) {
			result = getStages()[position].getHasPoints();
		}

		return result;
	}

	private String[] getCompetitorNames() {
		ArrayList<String> competitorNames = new ArrayList<String>();
		for (Competitor competitor : getCompetitors()) {
			competitorNames.add(competitor.getName());
		}
		return competitorNames.toArray(new String[competitorNames.size()]);
	}

	private ArrayList<Competitor> getCompetitors() {
		if (competitors == null) {
			long[] competitorIds = parameters.getLongArray(Constants.COMPETITOR_IDS);
			String[] competitorNames = parameters.getStringArray(Constants.COMPETITOR_NAMES);
			if (competitorNames != null && competitorNames.length != 0 && competitorNames.length == competitorIds.length) {
				competitors = new ArrayList<Competitor>();
				for (int i = 0; i < competitorNames.length; i++) {
					competitors.add(new Competitor(competitorIds[i], i + 1, competitorNames[i], getStageCount()));
				}
			}
		}
		return competitors;
	}

	private void storeData() {
		FileOutputStream dataStream = null;
		try {
			Serializer serializer = new Persister();
			//FileOutputStream dataStream = openFileOutput(DATA_FILE, Context.MODE_PRIVATE);
			File data = new File(String.format("/sdcard/%s", Constants.DATA_FILE));
			dataStream = new FileOutputStream(data);
			Squad squad = new Squad(getSquadID(), getSquadLabel(), getCompetitors());
			serializer.write(squad, data);
			dataStream.close();
		} catch (Exception ex) {
			Logger.getLogger(SelectStage.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				dataStream.close();
			} catch (IOException ex) {
				Logger.getLogger(SelectStage.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	private void listStages() {
		TextView title = (TextView) findViewById(R.id.select_stage);
		title.setText(getSquadLabel());

		ListView stageList = (ListView) findViewById(R.id.list_stages);
		stageList.setVisibility(View.VISIBLE);
		stageList.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				enterResults(parent, position);
			}
		});
		StageListAdapter adapter = new StageListAdapter(this);
		stageList.setAdapter(adapter);
	}

	public void enterResults(AdapterView<?> stages, int position) {
		if (position >= 0 && position <= getStageCount() - 1) {
			Intent intent = new Intent(this, EnterScores.class);
			Bundle data = new Bundle();

			// pass the current stage index (using stageIndex from ListView)
			data.putInt(Constants.STAGE_INDEX, position);

			// pass the current stage name to the new activity
			data.putString(Constants.STAGE_LABEL, getStageLabel(position));

			// pass the target count for the current stage to the new activity
			data.putInt(Constants.STAGE_TARGETCOUNT, getStageTargetCount(position));

			// pass the status of targets with point values on them to the new activity
			data.putBoolean(Constants.STAGE_HASPOINTS, getStagePoints(position));

			// pass the competitor info to the new activity
			data.putStringArray(Constants.COMPETITOR_NAMES, getCompetitorNames());

			intent.putExtra(Constants.BUNDLED_DATA, data);

			startActivityForResult(intent, Constants.ENTER_SCORES);
		}
	}

	public void reviewScores(View view) {
		Intent intent = new Intent(this, ReviewScores.class);
		Bundle data = new Bundle();
		boolean[] stageHasPoints = new boolean[getStageCount()];
		for (int i = 0; i < getStageCount(); i++) {
			stageHasPoints[i] = getStagePoints(i);
		}

		data.putBooleanArray(Constants.STAGE_HASPOINTS, stageHasPoints);

		data.putInt(Constants.STAGE_COUNT, getStageCount());
		data.putStringArray(Constants.COMPETITOR_NAMES, getCompetitorNames());

		for (Competitor competitor : getCompetitors()) {
			int[] hits = new int[getStageCount()];
			int[] targets = new int[getStageCount()];
			int[] points = new int[getStageCount()];
			for (int stageIndex = 0; stageIndex < getStageCount(); stageIndex++) {
				Score score = competitor.getScoresAsArray()[stageIndex];
				if (score != null) {
					hits[stageIndex] = score.getHits();
					targets[stageIndex] = score.getTargets();
					points[stageIndex] = score.getPoints();
				}
			}
			data.putIntArray(Constants.SCORING_HITS + competitor.getName(), hits);
			data.putIntArray(Constants.SCORING_TARGETS + competitor.getName(), targets);
			data.putIntArray(Constants.SCORING_POINTS + competitor.getName(), points);
		}

		intent.putExtra(Constants.BUNDLED_DATA, data);

		startActivity(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == Constants.ENTER_SCORES) {
			if (data.hasExtra(Constants.BUNDLED_DATA)) {
				// get return values
				Bundle result = data.getBundleExtra(Constants.BUNDLED_DATA);
				int stageIndex = result.getInt(Constants.STAGE_INDEX);
				int[] hits = result.getIntArray(Constants.SCORING_HITS);
				int[] targets = result.getIntArray(Constants.SCORING_TARGETS);
				int[] points = result.getIntArray(Constants.SCORING_POINTS);

				// store scores
				for (Competitor competitor : getCompetitors()) {
					int competitorIndex = getCompetitors().indexOf(competitor);
					Score score = new Score(hits[competitorIndex], targets[competitorIndex], points[competitorIndex]);
					competitor.setScore(stageIndex, score);
				}
				storeData();

				// get the stageList listview
				ListView stageList = (ListView) findViewById(R.id.list_stages);

				// set color for scored stage
				TextView item = (TextView) stageList.getChildAt(stageIndex);
				item.setBackgroundColor(Color.parseColor("#29cf00"));
				item.setTextColor(Color.parseColor("#ffffff"));

				// bring up activity for the next stage
				enterResults(stageList, stageIndex + 1);
			}
		}
	}

	private class StageListAdapter extends BaseAdapter {

		private Activity activity;
		private Stage[] stages;
		private LayoutInflater inflater = null;

		public StageListAdapter(Activity activity) {
			this.activity = activity;
			stages = getStages();
			inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			return stages.length;
		}

		public Object getItem(int position) {
			return stages[position];
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View view;

			if (convertView == null) {
				view = inflater.inflate(R.layout.stage_item, null);
			} else {
				view = convertView;
			}

			TextView label = (TextView) view.findViewById(R.id.stage_label);
			label.setText(stages[position].toString());

			return view;
		}
	}

	private Intent fetchData() {
		Intent result = new Intent();

		FileInputStream dataStream = null;
		try {
			Serializer serializer = new Persister();
			//FileInputStream dataStream = openFileInput(Constants.DATA_FILE);
			File data = new File(String.format("/sdcard/%s", Constants.DATA_FILE));
			dataStream = new FileInputStream(data);
			Squad squad = serializer.read(Squad.class, data);
			dataStream.close();

			StringWriter xmldata = new StringWriter();
			serializer.write(squad, xmldata);

			Bundle bundle = new Bundle();
			bundle.putString(Constants.XMLDATA, xmldata.toString());
			result.putExtra(Constants.BUNDLED_DATA, bundle);
		} catch (Exception ex) {
			Logger.getLogger(SelectStage.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				dataStream.close();
			} catch (IOException ex) {
				Logger.getLogger(SelectStage.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		return result;
	}

	public void closeActivity(View view) {
		Intent result = fetchData();
		setResult(RESULT_OK, result);
		this.finish();
	}
}