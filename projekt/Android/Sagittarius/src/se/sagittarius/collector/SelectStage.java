package se.sagittarius.collector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class SelectStage extends Activity {

	public final static String EXTRA_MESSAGE = "se.sagittarius.collector.MESSAGE";

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		TextView selectTitle = (TextView) findViewById(R.id.select_title);
		selectTitle.setText("Patrull 1");

		ListView stages = (ListView) findViewById(R.id.list_stages);
		stages.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				enterResults(parent, position);
			}
		});
		StageListAdapter adapter = new StageListAdapter(this);
		stages.setAdapter(adapter);
	}

	public void enterResults(AdapterView<?> stages, int position) {
		Intent intent = new Intent(this, EnterResults.class);
		String message = stages.getAdapter().getItem(position).toString();
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
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