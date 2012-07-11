package se.sagittarius.collector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import de.quist.app.errorreporter.ExceptionReporter;

public class SelectStage extends Activity {

	public final static String BUNDLED_DATA = "se.sagittarius.collector.BUNDLED_DATA";
	public final static String COMPETITOR_LIST = "se.sagittarius.collector.COMPETITOR_LIST";
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
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
			Intent intent = new Intent(this, EnterResults.class);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == ENTER_RESULTS) {
			if (data.hasExtra(BUNDLED_DATA)) {
				Bundle result = data.getBundleExtra(BUNDLED_DATA);
				ListView stages = (ListView) findViewById(R.id.list_stages);
				enterResults(stages, result.getInt(STAGE_INDEX) + 1);
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