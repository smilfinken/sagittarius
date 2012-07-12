package se.sagittarius.collector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import de.quist.app.errorreporter.ExceptionReporter;

public class SelectStage extends Activity {

	private Score[][] scores;
	public final static String BUNDLED_DATA = "se.sagittarius.collector.BUNDLED_DATA";
	public final static String COMPETITOR_LIST = "se.sagittarius.collector.COMPETITOR_LIST";
	public final static String SCORING_HITS = "se.sagittarius.collector.SCORING_HITS";
	public final static String SCORING_TARGETS = "se.sagittarius.collector.SCORING_TARGETS";
	public final static String SCORING_POINTS = "se.sagittarius.collector.SCORING_POINTS";
	public final static String STAGE_COUNT = "se.sagittarius.collector.STAGE_COUNT";
	public final static String STAGE_HASPOINTS = "se.sagittarius.collector.STAGE_POINTS";
	public final static String STAGE_INDEX = "se.sagittarius.collector.STAGE_INDEX";
	public final static String STAGE_LABEL = "se.sagittarius.collector.STAGE_LABEL";
	public final static String STAGE_POINTS = "se.sagittarius.collector.STAGE_POINTS";
	public final static String STAGE_TARGETCOUNT = "se.sagittarius.collector.STAGE_TARGETCOUNT";
	static final int ENTER_RESULTS = 10001;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		ExceptionReporter reporter = ExceptionReporter.register(this);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		TextView selectTitle = (TextView) findViewById(R.id.select_title);
		selectTitle.setText(getSquadLabel());

		ListView stages = (ListView) findViewById(R.id.list_stages);
		stages.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				enterResults(parent, position);
			}
		});
		StageListAdapter adapter = new StageListAdapter(this);
		stages.setAdapter(adapter);

		scores = new Score[getCompetitors().length][getStageCount()];
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.select_squad:
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

	public void enterResults(AdapterView<?> stages, int position) {
		if (position >= 0 && position <= getStageCount() - 1) {
			Intent intent = new Intent(this, EnterScores.class);
			Bundle data = new Bundle();

			// pass the current stage index (using position from ListView)
			data.putInt(STAGE_INDEX, position);

			// pass the current stage name to the new activity
			data.putString(STAGE_LABEL, getStageLabel(position));

			// pass the target count for the current stage to the new activity
			data.putInt(STAGE_TARGETCOUNT, getStageTargetCount(position));

			// pass the status of targets with point values on them to the new activity
			data.putBoolean(STAGE_POINTS, getStagePoints(position));

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
		for (int i = 0; i < getStageCount(); i++) {
			stageHasPoints[i] = getStagePoints(i);
		}
		data.putBooleanArray(STAGE_HASPOINTS, stageHasPoints);
		data.putInt(STAGE_COUNT, getStageCount());
		data.putStringArray(COMPETITOR_LIST, getCompetitors());
		intent.putExtra(BUNDLED_DATA, data);

		startActivity(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == ENTER_RESULTS) {
			if (data.hasExtra(BUNDLED_DATA)) {
				// get return values
				Bundle result = data.getBundleExtra(BUNDLED_DATA);
				int position = result.getInt(STAGE_INDEX);
				int[] hits = result.getIntArray(SCORING_HITS);
				int[] targets = result.getIntArray(SCORING_HITS);
				int[] points = result.getIntArray(SCORING_HITS);

				// store scores
				for (int i = 0; i < getCompetitors().length; i++) {
					scores[i][position] = new Score(hits[i], targets[i], points[i]);
				}

				// get the stages listview
				ListView stages = (ListView) findViewById(R.id.list_stages);

				// set color for scored stage
				TextView item = (TextView) stages.getChildAt(position);
				item.setBackgroundColor(Color.parseColor("#29cf00"));
				item.setTextColor(Color.parseColor("#ffffff"));

				// bring up activity for the next stage
				enterResults(stages, position + 1);
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