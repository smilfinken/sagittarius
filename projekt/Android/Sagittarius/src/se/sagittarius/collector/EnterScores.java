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
	private int currentCompetitor = 0;
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
		for (int i = 0; i < scores.length; i++) {
			values[i] = scores[i].hits;
		}
		return values;
	}

	private int[] getTargetsArray() {
		int[] values = new int[scores.length];
		for (int i = 0; i < scores.length; i++) {
			values[i] = scores[i].targets;
		}
		return values;
	}

	private int[] getPointsArray() {
		int[] values = new int[scores.length];
		for (int i = 0; i < scores.length; i++) {
			values[i] = scores[i].points;
		}
		return values;
	}

	public void nextCompetitor(View view) {
		String[] competitorList = getCompetitors();
		if (currentCompetitor >= competitorList.length) {
			// prepare results bundle
			results = new Bundle();
			results.putInt(SelectStage.STAGE_INDEX, currentStage);
			results.putIntArray(SelectStage.SCORING_HITS, getHitsArray());
			results.putIntArray(SelectStage.SCORING_TARGETS, getTargetsArray());
			results.putIntArray(SelectStage.SCORING_POINTS, getPointsArray());

			// return data to calling activity
			Intent data = new Intent();
			data.putExtra(SelectStage.BUNDLED_DATA, results);
			setResult(RESULT_OK, data);

			this.finish();
		} else {
			scores[currentCompetitor] = new Score(getHits(), getTargets(), getPoints());
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