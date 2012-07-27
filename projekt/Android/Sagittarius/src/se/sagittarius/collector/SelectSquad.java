package se.sagittarius.collector;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import de.quist.app.errorreporter.ExceptionReporter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
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

	private void populateSquads(Competition competition) {
		Squad[] squads = getCompetitors(competition);
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

	private void listCompetitors(Squad squad) {
		if (squad != null) {
			Competitor[] competitors = getCompetitors(squad);
			if (competitors.length > 0) {
				ListView competitorList = (ListView) findViewById(R.id.list_competitors);
				competitorList.setVisibility(View.VISIBLE);
				CompetitorListAdapter adapter = new CompetitorListAdapter(this, competitors);
				competitorList.setAdapter(adapter);
				Button button = (Button) findViewById(R.id.btn_select_squad);
				button.setVisibility(View.VISIBLE);
			} else {
				TextView message = (TextView) findViewById(R.id.select_message);
				message.setText(R.string.message_no_competitors);
				message.setVisibility(View.VISIBLE);
			}
		}
	}

	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		// hide competitor list
		ListView competitorList = (ListView) findViewById(R.id.list_competitors);
		competitorList.setVisibility(View.INVISIBLE);

		// hide action button
		Button button = (Button) findViewById(R.id.btn_select_squad);
		button.setVisibility(View.INVISIBLE);

		// hide last message
		TextView message = (TextView) findViewById(R.id.select_message);
		message.setText("");
		message.setVisibility(View.INVISIBLE);

		switch (parent.getId()) {
			case R.id.select_competition:
				populateSquads((Competition) parent.getItemAtPosition(position));
				break;
			case R.id.select_squad:
				listCompetitors((Squad) parent.getItemAtPosition(position));
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

	private Squad[] getCompetitors(Competition competition) {
		ArrayList<Squad> squads = new ArrayList<Squad>();

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

	private Competitor[] getCompetitors(Squad squad) {
		TreeSet<Competitor> competitors = new TreeSet<Competitor>(new Comparator<Competitor>() {

			public int compare(Competitor a, Competitor b) {
				return a.getId() - b.getId();
			}
		});

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
						competitors.addAll(serialiser.read(CompetitorContainer.class, response.getEntity().getContent()).competitors);
					}
				} catch (Exception ex) {
					Logger.getLogger(SelectSquad.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
		return competitors.toArray(new Competitor[competitors.size()]);
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
}