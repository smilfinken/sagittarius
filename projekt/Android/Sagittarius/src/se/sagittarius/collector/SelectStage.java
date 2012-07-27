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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class SelectStage extends Activity {

	// constants for extra data
	public final static String BUNDLED_DATA = "se.sagittarius.collector.BUNDLED_DATA";
	public final static String COMPETITOR_LIST = "se.sagittarius.collector.COMPETITOR_LIST";
	public final static String SCORING_HITS = "se.sagittarius.collector.SCORING_HITS";
	public final static String SCORING_TARGETS = "se.sagittarius.collector.SCORING_TARGETS";
	public final static String SCORING_POINTS = "se.sagittarius.collector.SCORING_POINTS";
	public final static String STAGE_COUNT = "se.sagittarius.collector.STAGE_COUNT";
	public final static String STAGE_HASPOINTS = "se.sagittarius.collector.STAGE_HASPOINTS";
	public final static String STAGE_INDEX = "se.sagittarius.collector.STAGE_INDEX";
	public final static String STAGE_LABEL = "se.sagittarius.collector.STAGE_LABEL";
	public final static String STAGE_POINTS = "se.sagittarius.collector.STAGE_POINTS";
	public final static String STAGE_TARGETCOUNT = "se.sagittarius.collector.STAGE_TARGETCOUNT";
	static final int ENTER_RESULTS = 10001;
	// file name for data storage
	private final static String DATA_FILE = "sagittarius_data";
	// competitors
	private Competitor[] competitors;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stages);

		//selectSquad();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean enabled = (competitors != null);

		menu.findItem(R.id.upload_scores).setEnabled(enabled);
		menu.findItem(R.id.upload_scores).setVisible(enabled);
		menu.findItem(R.id.review_scores).setEnabled(enabled);
		menu.findItem(R.id.review_scores).setVisible(enabled);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.select_squad:
				listStages();
				return true;
			case R.id.upload_scores:
				uploadScores();
				return true;
			case R.id.review_scores:
				reviewScores();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private String getSquadLabel() {
		return getResources().getString(R.string.dummy_squadlabel);
	}

	private int getStageCount() {
		ListView stages = (ListView) findViewById(R.id.list_stages);
		return stages.getAdapter().getCount();
	}

	private String getStageLabel(int position) {
		ListView stages = (ListView) findViewById(R.id.list_stages);
		return stages.getAdapter().getItem(position).toString();
	}

	private int getStageTargetCount(int position) {
		return getResources().getIntArray(R.array.dummy_targets)[position];
	}

	private boolean getStagePoints(int position) {
		return Boolean.valueOf(getResources().getStringArray(R.array.dummy_points)[position]);
	}

	private String[] getCompetitors() {
		return getResources().getStringArray(R.array.dummy_squad);
	}

	private void storeData() {
		FileOutputStream dataStream = null;
		try {
			Serializer serializer = new Persister();
			//FileOutputStream dataStream = openFileOutput(DATA_FILE, Context.MODE_PRIVATE);
			File data = new File(String.format("/sdcard/%s", DATA_FILE));
			dataStream = new FileOutputStream(data);
			serializer.write(new Squad(competitors), data);
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

		ListView stages = (ListView) findViewById(R.id.list_stages);
		stages.setVisibility(View.VISIBLE);
		stages.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				enterResults(parent, position);
			}
		});
		StageListAdapter adapter = new StageListAdapter(this);
		stages.setAdapter(adapter);

		competitors = new Competitor[getCompetitors().length];
		for (int competitorIndex = 0; competitorIndex < competitors.length; competitorIndex++) {
			//TODO: fix constructor properly
			competitors[competitorIndex] = new Competitor(0, 0, getCompetitors()[competitorIndex], null);
		}
	}

	public void enterResults(AdapterView<?> stages, int position) {
		if (position >= 0 && position <= getStageCount() - 1) {
			Intent intent = new Intent(this, EnterScores.class);
			Bundle data = new Bundle();

			// pass the current stage index (using stageIndex from ListView)
			data.putInt(STAGE_INDEX, position);

			// pass the current stage name to the new activity
			data.putString(STAGE_LABEL, getStageLabel(position));

			// pass the target count for the current stage to the new activity
			data.putInt(STAGE_TARGETCOUNT, getStageTargetCount(position));

			// pass the status of targets with point values on them to the new activity
			data.putBoolean(STAGE_HASPOINTS, getStagePoints(position));

			// pass the competitor names to the new activity
			data.putStringArray(COMPETITOR_LIST, getCompetitors());

			intent.putExtra(BUNDLED_DATA, data);

			startActivityForResult(intent, ENTER_RESULTS);
		}
	}

	public void reviewScores() {
		Intent intent = new Intent(this, ReviewScores.class);
		Bundle data = new Bundle();
		boolean[] stageHasPoints = new boolean[getStageCount()];
		for (int i = 0;
				i < getStageCount();
				i++) {
			stageHasPoints[i] = getStagePoints(i);
		}

		data.putBooleanArray(STAGE_HASPOINTS, stageHasPoints);

		data.putInt(STAGE_COUNT, getStageCount());
		data.putStringArray(COMPETITOR_LIST, getCompetitors());

		for (int competitorIndex = 0;
				competitorIndex < getCompetitors().length; competitorIndex++) {
			int[] hits = new int[getStageCount()];
			int[] targets = new int[getStageCount()];
			int[] points = new int[getStageCount()];
			for (int stageIndex = 0; stageIndex < getStageCount(); stageIndex++) {
				Score score = competitors[competitorIndex].getScoresAsArray()[stageIndex];
				if (score != null) {
					hits[stageIndex] = score.getHits();
					targets[stageIndex] = score.getTargets();
					points[stageIndex] = score.getPoints();
				}
			}
			data.putIntArray(SCORING_HITS + getCompetitors()[competitorIndex], hits);
			data.putIntArray(SCORING_TARGETS + getCompetitors()[competitorIndex], targets);
			data.putIntArray(SCORING_POINTS + getCompetitors()[competitorIndex], points);
		}

		intent.putExtra(BUNDLED_DATA, data);

		startActivity(intent);
	}

	private void uploadScores() {
		File data = new File(String.format("/sdcard/%s", DATA_FILE));
		if (data.exists()) {
			data.delete();
		}
		competitors = null;
		ListView stages = (ListView) findViewById(R.id.list_stages);
		stages.setVisibility(View.INVISIBLE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == ENTER_RESULTS) {
			if (data.hasExtra(BUNDLED_DATA)) {
				// get return values
				Bundle result = data.getBundleExtra(BUNDLED_DATA);
				int stageIndex = result.getInt(STAGE_INDEX);
				int[] hits = result.getIntArray(SCORING_HITS);
				int[] targets = result.getIntArray(SCORING_TARGETS);
				int[] points = result.getIntArray(SCORING_POINTS);

				// store scores
				for (int competitorIndex = 0; competitorIndex < getCompetitors().length; competitorIndex++) {
					competitors[competitorIndex].getScoresAsArray()[stageIndex] = new Score(hits[competitorIndex], targets[competitorIndex], points[competitorIndex]);
				}
				storeData();

				// get the stages listview
				ListView stages = (ListView) findViewById(R.id.list_stages);

				// set color for scored stage
				TextView item = (TextView) stages.getChildAt(stageIndex);
				item.setBackgroundColor(Color.parseColor("#29cf00"));
				item.setTextColor(Color.parseColor("#ffffff"));

				// bring up activity for the next stage
				enterResults(stages, stageIndex + 1);
			}
		}
	}

	public void exitApp(View view) {
		this.finish();
	}

	private class StageListAdapter extends BaseAdapter {

		private Activity activity;
		private String[] stages;
		private LayoutInflater inflater = null;

		public StageListAdapter(Activity activity) {
			this.activity = activity;
			stages = getResources().getStringArray(R.array.dummy_stages);
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
			label.setText(stages[position]);

			return view;
		}
	}
}