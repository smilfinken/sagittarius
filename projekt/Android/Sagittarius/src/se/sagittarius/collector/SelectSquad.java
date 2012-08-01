package se.sagittarius.collector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import de.quist.app.errorreporter.ExceptionReporter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.simpleframework.xml.core.Persister;

/**
 *
 * @author johan
 */
public class SelectSquad extends Activity implements OnItemSelectedListener {

	// globals
	Competitor[] competitors = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		ExceptionReporter reporter = ExceptionReporter.register(this);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.squads);

		populateCompetitions();
	}

	private void populateCompetitions() {
		Competition[] competitions = getCompetitions();
		ArrayAdapter<Competition> competitionAdapter = new ArrayAdapter<Competition>(this, android.R.layout.simple_spinner_item, competitions);
		competitionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner competitionSpinner = (Spinner) findViewById(R.id.select_competition);
		competitionSpinner.setOnItemSelectedListener(this);
		competitionSpinner.setAdapter(competitionAdapter);
	}

	private void populateSquads() {
		Squad[] squads = getSquads();
		Spinner squadSpinner = (Spinner) findViewById(R.id.select_squad);
		if (squads.length > 0) {
			ArrayAdapter<Squad> squadAdapter = new ArrayAdapter<Squad>(this, android.R.layout.simple_spinner_item, squads);
			squadAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			squadSpinner.setVisibility(View.VISIBLE);
			squadSpinner.setOnItemSelectedListener(this);
			squadSpinner.setAdapter(squadAdapter);
		} else {
			squadSpinner.setVisibility(View.INVISIBLE);
			TextView message = (TextView) findViewById(R.id.select_message);
			message.setText(R.string.message_no_squads);
			message.setVisibility(View.VISIBLE);
		}
	}

	private void listCompetitors() {
		competitors = getCompetitors();
		if (competitors.length > 0) {
			ListView competitorList = (ListView) findViewById(R.id.list_competitors);
			competitorList.setVisibility(View.VISIBLE);
			CompetitorListAdapter adapter = new CompetitorListAdapter(this, competitors);
			competitorList.setAdapter(adapter);
			Button select = (Button) findViewById(R.id.btn_select_squad);
			select.setVisibility(View.VISIBLE);
		} else {
			TextView message = (TextView) findViewById(R.id.select_message);
			message.setText(R.string.message_no_competitors);
			message.setVisibility(View.VISIBLE);
		}
	}

	public void viewStages(View view) {
		if (competitors != null) {
			Intent intent = new Intent(this, SelectStage.class);
			Bundle data = new Bundle();

			Stage[] stages = getStages();
			String[] stageLabels = new String[stages.length];
			int[] stageTargets = new int[stages.length];
			boolean[] stagePoints = new boolean[stages.length];
			for (int i = 0; i < stages.length; i++) {
				stageLabels[i] = stages[i].getLabel();
				stageTargets[i] = stages[i].getTargetCount();
				stagePoints[i] = stages[i].getHasPoints();
			}
			data.putStringArray(Constants.STAGE_LABEL, stageLabels);
			data.putIntArray(Constants.STAGE_TARGETCOUNT, stageTargets);
			data.putBooleanArray(Constants.STAGE_HASPOINTS, stagePoints);

			Squad squad = getSelectedSquad();
			data.putString(Constants.SQUAD_LABEL, squad.getLabel());
			data.putLong(Constants.SQUAD_ID, squad.getId());

			long[] competitorIds = new long[competitors.length];
			String[] competitorNames = new String[competitors.length];
			for (int i = 0; i < competitors.length; i++) {
				competitorIds[i] = competitors[i].getId();
				competitorNames[i] = competitors[i].getName();
			}
			data.putLongArray(Constants.COMPETITOR_IDS, competitorIds);
			data.putStringArray(Constants.COMPETITOR_NAMES, competitorNames);

			intent.putExtra(Constants.BUNDLED_DATA, data);
			startActivityForResult(intent, Constants.SELECT_STAGE);
		}
	}

	public void uploadScores(View view) {
		//public static void addSquad(long competitionID, long squadID, Result[][] results, long[] competitors) {
		DefaultHttpClient httpClient = doLogin();
		if (httpClient != null) {
			try {
				// prepare data
				Squad squad = new Squad(getSelectedSquad().getId(), getSelectedSquad().getLabel(), new ArrayList<Competitor>(Arrays.asList(competitors)));
				StringWriter xmldata = new StringWriter();
				Persister serializer = new Persister();
				serializer.write(squad, xmldata);

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
				nameValuePairs.add(new BasicNameValuePair("xmlData", xmldata.toString()));

				// upload data to server
				HttpPost dataURL = new HttpPost(String.format("%s%s", getResources().getString(R.string.url_base), getResources().getString(R.string.url_upload)));
				dataURL.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				if (httpClient.execute(dataURL).getStatusLine().getStatusCode() < 400) {
					// reset
					File data = new File(String.format("/sdcard/%s", Constants.DATA_FILE));
					if (data.exists()) {
						data.delete();
					}

					Button select = (Button) findViewById(R.id.btn_select_squad);
					select.setVisibility(View.VISIBLE);
					Button upload = (Button) findViewById(R.id.btn_upload_scores);
					upload.setVisibility(View.INVISIBLE);
				}
			} catch (Exception ex) {
				Logger.getLogger(SelectSquad.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == Constants.SELECT_STAGE) {
			if (data.hasExtra(Constants.BUNDLED_DATA)) {
				// get return values
				try {
					Bundle result = data.getBundleExtra(Constants.BUNDLED_DATA);
					String xmldata = result.getString(Constants.XMLDATA);
					Persister serializer = new Persister();
					Squad squad = serializer.read(Squad.class, xmldata);
					competitors = squad.getCompetitors().toArray(new Competitor[squad.getCompetitors().size()]);

					if (squad.isScored()) {
						Button select = (Button) findViewById(R.id.btn_select_squad);
						select.setVisibility(View.INVISIBLE);
						Button upload = (Button) findViewById(R.id.btn_upload_scores);
						upload.setVisibility(View.VISIBLE);
					}
				} catch (Exception ex) {
					Logger.getLogger(SelectSquad.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
	}

	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		// hide competitor list
		ListView competitorList = (ListView) findViewById(R.id.list_competitors);
		competitorList.setVisibility(View.INVISIBLE);

		// hide action select
		Button button = (Button) findViewById(R.id.btn_select_squad);
		button.setVisibility(View.INVISIBLE);

		// hide last message
		TextView message = (TextView) findViewById(R.id.select_message);
		message.setText("");
		message.setVisibility(View.INVISIBLE);

		switch (parent.getId()) {
			case R.id.select_competition:
				populateSquads();
				break;
			case R.id.select_squad:
				listCompetitors();
				break;
		}
	}

	public void onNothingSelected(AdapterView<?> parent) {
	}

	private DefaultHttpClient doLogin() {
		DefaultHttpClient result = null;

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
			nameValuePairs.add(new BasicNameValuePair("username", getResources().getString(R.string.username)));
			nameValuePairs.add(new BasicNameValuePair("password", getResources().getString(R.string.password)));

			HttpPost loginURL = new HttpPost(String.format("%s%s", getResources().getString(R.string.url_base), getResources().getString(R.string.url_loginform)));
			loginURL.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			DefaultHttpClient httpClient = new DefaultHttpClient();

			// login
			if (httpClient.execute(loginURL).getStatusLine().getStatusCode() < 400) {
				// login successful
				result = httpClient;
			}
		} catch (UnsupportedEncodingException ex) {
			Logger.getLogger(SelectSquad.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ClientProtocolException ex) {
			Logger.getLogger(SelectSquad.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(SelectSquad.class.getName()).log(Level.SEVERE, null, ex);
		}

		return result;
	}

	private Competition[] getCompetitions() {
		ArrayList<Competition> competitions = new ArrayList<Competition>();
		DefaultHttpClient httpClient = doLogin();

		if (httpClient != null) {
			try {
				// get competition list
				HttpResponse response;
				HttpGet dataURL = new HttpGet(String.format("%s%s", getResources().getString(R.string.url_base), getResources().getString(R.string.url_competitionlist)));
				response = httpClient.execute(dataURL);
				if (response.getStatusLine().getStatusCode() < 400) {
					// read response
					Persister serialiser = new Persister();
					competitions = (ArrayList<Competition>) serialiser.read(CompetitionContainer.class, response.getEntity().getContent()).competitions;
				}
			} catch (Exception ex) {
				Logger.getLogger(SelectSquad.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		return competitions.toArray(new Competition[competitions.size()]);
	}

	private Competition getSelectedCompetition() {
		Competition result = null;
		Spinner view = (Spinner) findViewById(R.id.select_competition);
		if (view != null) {
			result = (Competition) view.getSelectedItem();
		}

		return result;
	}

	private Squad getSelectedSquad() {
		Squad result = null;
		Spinner view = (Spinner) findViewById(R.id.select_squad);
		if (view != null) {
			result = (Squad) view.getSelectedItem();
		}

		return result;
	}

	private Squad[] getSquads() {
		ArrayList<Squad> squads = new ArrayList<Squad>();

		Competition competition = getSelectedCompetition();
		if (competition != null) {
			DefaultHttpClient httpClient = doLogin();

			if (httpClient != null) {
				try {
					// get squad list
					HttpResponse response;
					HttpGet dataURL = new HttpGet(String.format("%s%s?competitionID=%d", getResources().getString(R.string.url_base), getResources().getString(R.string.url_squadlist), competition.getId()));
					response = httpClient.execute(dataURL);
					if (response.getStatusLine().getStatusCode() < 400) {
						// read response
						Persister serialiser = new Persister();
						squads = (ArrayList<Squad>) serialiser.read(SquadContainer.class, response.getEntity().getContent()).squads;
					}
				} catch (Exception ex) {
					Logger.getLogger(SelectSquad.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}

		return squads.toArray(new Squad[squads.size()]);
	}

	private Competitor[] getCompetitors() {
		TreeSet<Competitor> result = new TreeSet<Competitor>(new Comparator<Competitor>() {

			public int compare(Competitor a, Competitor b) {
				return (int) (a.getPosition() - b.getPosition());
			}
		});

		Squad squad = getSelectedSquad();
		if (squad != null) {
			DefaultHttpClient httpClient = doLogin();

			if (httpClient != null) {
				try {
					// get squad list
					HttpResponse response;
					HttpGet dataURL = new HttpGet(String.format("%s%s?squadID=%d", getResources().getString(R.string.url_base), getResources().getString(R.string.url_competitorlist), squad.getId()));
					response = httpClient.execute(dataURL);
					if (response.getStatusLine().getStatusCode() < 400) {
						// read response
						Persister serialiser = new Persister();
						result.addAll(serialiser.read(CompetitorContainer.class, response.getEntity().getContent()).competitors);
					}
				} catch (Exception ex) {
					Logger.getLogger(SelectSquad.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
		return result.toArray(new Competitor[result.size()]);
	}

	private Stage[] getStages() {
		ArrayList<Stage> result = new ArrayList<Stage>();

		Competition competition = getSelectedCompetition();
		if (competition != null) {
			DefaultHttpClient httpClient = doLogin();

			if (httpClient != null) {
				try {
					// get stage list
					HttpResponse response;
					HttpGet dataURL = new HttpGet(String.format("%s%s?competitionID=%d", getResources().getString(R.string.url_base), getResources().getString(R.string.url_stagelist), competition.getId()));
					response = httpClient.execute(dataURL);
					if (response.getStatusLine().getStatusCode() < 400) {
						// read response
						Persister serialiser = new Persister();
						result.addAll(serialiser.read(StageContainer.class, response.getEntity().getContent()).stages);
					}
				} catch (Exception ex) {
					Logger.getLogger(SelectSquad.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
		return result.toArray(new Stage[result.size()]);
	}

	private class CompetitorListAdapter extends BaseAdapter {

		private Activity activity;
		private Competitor[] competitors;
		private LayoutInflater inflater = null;

		public CompetitorListAdapter(Activity activity, Competitor[] competitors) {
			this.activity = activity;
			this.competitors = competitors;
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
				view = inflater.inflate(R.layout.competitor_item, null);
			} else {
				view = convertView;
			}

			TextView label = (TextView) view.findViewById(R.id.competitor_name);
			label.setText(competitors[position].toString());

			return view;
		}
	}

	public void closeActivity(View view) {
		this.finish();
	}
}