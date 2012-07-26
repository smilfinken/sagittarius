package se.sagittarius.collector;

import android.app.Activity;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import de.quist.app.errorreporter.ExceptionReporter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
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
		Squad[] squads = getSquads(competition);
		if (squads.length > 0) {
			ArrayAdapter<Squad> squadAdapter = new ArrayAdapter<Squad>(this, android.R.layout.simple_spinner_item, squads);
			squadAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			Spinner squadSpinner = (Spinner) findViewById(R.id.select_squad);
			squadSpinner.setVisibility(View.VISIBLE);
			squadSpinner.setOnItemSelectedListener(this);
			squadSpinner.setAdapter(squadAdapter);
		} else {
			Spinner squadSpinner = (Spinner) findViewById(R.id.select_squad);
			squadSpinner.setVisibility(View.INVISIBLE);
		}
	}

	private void listCompetitors(Squad squad) {
		if (squad != null) {
			Toast.makeText(this, squad.getLabel(), Toast.LENGTH_LONG);
		}
	}

	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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
			Logger.getLogger(SelectStage.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ClientProtocolException ex) {
			Logger.getLogger(SelectStage.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(SelectStage.class.getName()).log(Level.SEVERE, null, ex);
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
				Logger.getLogger(SelectStage.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		return competitions.toArray(new Competition[competitions.size()]);
	}

	private Squad[] getSquads(Competition competition) {
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
					Logger.getLogger(SelectStage.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}

		return squads.toArray(new Squad[squads.size()]);
	}
}