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
	int[] stageHits;
	int[] stageTargets;
	int[] stagePoints;

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
			stagePoints = null;
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

			if (convertView == null) {
				view = inflater.inflate(R.layout.score_item, null);
			} else {
				view = convertView;
			}

			TextView label = (TextView) view.findViewById(R.id.score_label);
			label.setText(competitors[position]);

			String scores = "";
			for (int i = 0; i < stageCount; i++) {
				if (stageHasPoints[i]) {
					scores += String.format("%d/%d (%d)", 0, 0, 0);
				} else {
					scores += String.format("%d/%d", 0, 0);
				}
				if (i < stageCount - 1) {
					scores += ", ";
				}
			}
			TextView display = (TextView) view.findViewById(R.id.score_display);
			display.setText(scores);

			return view;
		}
	}
}
