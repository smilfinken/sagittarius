package se.sagittarius.collector;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 *
 * @author johan
 */
public class ReviewScores extends Activity {

	int stageCount;
	boolean[] stageHasPoints;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scores);

		try {
			Bundle data = getIntent().getExtras().getBundle(SelectStage.BUNDLED_DATA);
			if (data != null) {
				stageCount = data.getInt(SelectStage.STAGE_COUNT);
				stageHasPoints = data.getBooleanArray(SelectStage.STAGE_HASPOINTS);
			}
		} catch (Exception e) {
			stageCount = -1;
			stageHasPoints = null;
		}

		ListView scores = (ListView) findViewById(R.id.list_scores);
		scores.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				editResults(position);
			}
		});
		ScoreListAdapter adapter = new ScoreListAdapter(this);
		scores.setAdapter(adapter);
	}

	private void editResults(int competitor) {
	}

	private class ScoreListAdapter extends BaseAdapter {

		private Activity activity;
		private String[] competitors;
		private LayoutInflater inflater = null;

		public ScoreListAdapter(Activity activity) {
			this.activity = activity;

			Bundle data = getIntent().getExtras().getBundle(SelectStage.BUNDLED_DATA);
			competitors = data.getStringArray(SelectStage.COMPETITOR_LIST);
			inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			return competitors.length;
		}

		public Object getItem(int position) {
			return competitors[position];
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			String competitor = competitors[position];
			Bundle data = getIntent().getExtras().getBundle(SelectStage.BUNDLED_DATA);

			if (convertView == null) {
				view = inflater.inflate(R.layout.score_item, null);
			} else {
				view = convertView;
			}

			TextView label = (TextView) view.findViewById(R.id.score_label);
			label.setText(String.format("%d. %s", position + 1, competitor));

			int[] hits = data.getIntArray(SelectStage.SCORING_HITS + competitor);
			int[] targets = data.getIntArray(SelectStage.SCORING_TARGETS + competitor);
			int[] points = data.getIntArray(SelectStage.SCORING_POINTS + competitor);

			String scores = "";
			for (int stageIndex = 0; stageIndex < stageCount; stageIndex++) {
				if (stageHasPoints[stageIndex]) {
					scores += String.format("%d/%d (%d)", hits[stageIndex], targets[stageIndex], points[stageIndex]);
				} else {
					scores += String.format("%d/%d", hits[stageIndex], targets[stageIndex]);
				}
				if (stageIndex < stageCount - 1) {
					if (stageIndex == 5) {
						scores += "\n";
					} else {
						scores += ", ";
					}
				}
			}

			TextView display = (TextView) view.findViewById(R.id.score_display);
			display.setText(scores);

			return view;
		}
	}
}
