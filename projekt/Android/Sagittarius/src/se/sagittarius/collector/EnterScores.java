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
public class EnterScores extends Activity {

	Bundle data;
	Bundle results = Bundle.EMPTY;
	private int currentStage;
	private int nextCompetitor = 0;
	Score[] scores;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setResult(RESULT_CANCELED);
		setContentView(R.layout.stage);

		Intent intent = getIntent();
		data = intent.getBundleExtra(SelectStage.BUNDLED_DATA);

		currentStage = data.getInt(SelectStage.STAGE_INDEX);
		scores = new Score[getCompetitors().length];

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
		super.finish();
	}

	private String[] getCompetitors() {
		return data.getStringArray(SelectStage.COMPETITOR_LIST);
	}

	private int getHits() {
		NumberPicker view = (NumberPicker) findViewById(R.id.score_hits);
		return view.getValue();
	}

	private int getTargets() {
		NumberPicker view = (NumberPicker) findViewById(R.id.score_targets);
		return view.getValue();
	}

	private int getPoints() {
		NumberPicker view = (NumberPicker) findViewById(R.id.score_points);
		return view.getValue();
	}

	private int[] getHitsArray() {
		int[] values = new int[scores.length];
		for (int competitorIndex = 0; competitorIndex < scores.length; competitorIndex++) {
			values[competitorIndex] = scores[competitorIndex].getHits();
		}
		return values;
	}

	private int[] getTargetsArray() {
		int[] values = new int[scores.length];
		for (int competitorIndex = 0; competitorIndex < scores.length; competitorIndex++) {
			values[competitorIndex] = scores[competitorIndex].getTargets();
		}
		return values;
	}

	private int[] getPointsArray() {
		int[] values = new int[scores.length];
		for (int competitorIndex = 0; competitorIndex < scores.length; competitorIndex++) {
			values[competitorIndex] = scores[competitorIndex].getPoints();
		}
		return values;
	}

	public void nextCompetitor(View view) {
		String[] competitorList = getCompetitors();

		// collect current score unless this is the first call
		if (nextCompetitor > 0) {
			scores[nextCompetitor - 1] = new Score(getHits(), getTargets(), getPoints());
		}

		// check for last competitor
		if (nextCompetitor >= competitorList.length) {
			// prepare results bundle
			results = new Bundle();
			results.putInt(SelectStage.STAGE_INDEX, currentStage);
			results.putIntArray(SelectStage.SCORING_HITS, getHitsArray());
			results.putIntArray(SelectStage.SCORING_TARGETS, getTargetsArray());
			results.putIntArray(SelectStage.SCORING_POINTS, getPointsArray());

			// return result to calling activity
			Intent result = new Intent();
			result.putExtra(SelectStage.BUNDLED_DATA, results);
			setResult(RESULT_OK, result);

			// finish the activity
			this.finish();
		} else {
			// reset values
			NumberPicker hits = (NumberPicker) findViewById(R.id.score_hits);
			hits.setValue(0);
			NumberPicker targets = (NumberPicker) findViewById(R.id.score_targets);
			targets.setValue(0);
			NumberPicker points = (NumberPicker) findViewById(R.id.score_points);
			points.setValue(0);

			// set label for next competitor
			TextView competitorName = (TextView) findViewById(R.id.competitor);
			competitorName.setText(String.format("%s %d (%s)", getResources().getString(R.string.competitor), nextCompetitor + 1, competitorList[nextCompetitor]));

			// set visibility on points input
			View pointsContainer = (View) findViewById(R.id.container_points);
			if (!data.getBoolean(SelectStage.STAGE_HASPOINTS)) {
				pointsContainer.setVisibility(View.INVISIBLE);
			} else {
				pointsContainer.setVisibility(View.VISIBLE);
			}

			// increase index and check for last competitor
			if (++nextCompetitor >= competitorList.length) {
				Button nextButton = (Button) findViewById(R.id.next_competitor);
				nextButton.setText(getResources().getString(R.string.next_stage));
			}
		}
	}
}