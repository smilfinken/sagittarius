package se.sagittarius.collector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

/**
 *
 * @author johan
 */
public class EnterResults extends Activity {

	Bundle data;
	Bundle results = Bundle.EMPTY;
	private int currentCompetitor = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stage);

		Intent intent = getIntent();
		data = intent.getBundleExtra(SelectStage.BUNDLED_DATA);

		TextView stageTitle = (TextView) findViewById(R.id.stage_title);
		stageTitle.setText(data.getString(SelectStage.STAGE_LABEL));

		nextCompetitor(null);

		try {
			NumberPicker hitsView = (NumberPicker) findViewById(R.id.score_hits);
			//hitsView.setWrapSelectorWheel(false);
			hitsView.setMinValue(0);
			hitsView.setMaxValue(6);
		} catch (Error e) {
			Log.e(this.getClass().toString(), e.toString(), new Throwable(e));
		}

		try {
			NumberPicker targetsView = (NumberPicker) findViewById(R.id.score_targets);
			//targetsView.setWrapSelectorWheel(false);
			targetsView.setMinValue(0);
			targetsView.setMaxValue(data.getInt(SelectStage.STAGE_TARGETCOUNT));
		} catch (Error e) {
			Log.e(this.getClass().toString(), e.toString(), new Throwable(e));
		}

		try {
			NumberPicker pointsView = (NumberPicker) findViewById(R.id.score_points);
			//pointsView.setWrapSelectorWheel(true);
			pointsView.setMinValue(0);
			pointsView.setMaxValue(50);
		} catch (Error e) {
			Log.e(this.getClass().toString(), e.toString(), new Throwable(e));
		}
	}

	@Override
	public void finish() {
		// prepare results bundle
		results = new Bundle();
		results.putInt(SelectStage.STAGE_INDEX, data.getInt(SelectStage.STAGE_INDEX));

		// return data to calling activity
		Intent result = new Intent();
		result.putExtra(SelectStage.BUNDLED_DATA, results);
		setResult(RESULT_OK, result);

		super.finish();
	}

	public void nextCompetitor(View view) {
		String[] competitorList = data.getStringArray(SelectStage.COMPETITOR_LIST);
		if (currentCompetitor >= competitorList.length) {
			setResult(RESULT_OK);
			this.finish();
		} else {
			TextView competitorName = (TextView) findViewById(R.id.competitor);
			competitorName.setText(String.format("%s %d (%s)", getResources().getString(R.string.competitor), currentCompetitor + 1, competitorList[currentCompetitor++]));

			View pointsContainer = (View) findViewById(R.id.container_points);
			if (!data.getBoolean(SelectStage.STAGE_POINTS)) {
				pointsContainer.setVisibility(View.INVISIBLE);
			} else {
				pointsContainer.setVisibility(View.VISIBLE);
			}
			//pointsContainer.setVisibility(data.getBoolean(SelectStage.STAGE_POINTS) ? 1 : 0);
			if (currentCompetitor >= competitorList.length) {
				Button nextButton = (Button) findViewById(R.id.next_competitor);
				nextButton.setText(getResources().getString(R.string.next_stage));
			}
		}
	}
}