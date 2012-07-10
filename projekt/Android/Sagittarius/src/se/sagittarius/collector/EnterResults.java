package se.sagittarius.collector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 *
 * @author johan
 */
public class EnterResults extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stage);

		Intent intent = getIntent();
		String message = intent.getStringExtra(SelectStage.EXTRA_MESSAGE);
		TextView stageTitle = (TextView) findViewById(R.id.stage_title);
		stageTitle.setText(message);
	}
}
