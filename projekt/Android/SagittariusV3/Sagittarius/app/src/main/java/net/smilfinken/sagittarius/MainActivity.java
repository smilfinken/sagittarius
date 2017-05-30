package net.smilfinken.sagittarius;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.logging.Level;
import java.util.logging.Logger;

import static android.R.attr.tag;

public class MainActivity
    extends AppCompatActivity
    implements
        NavigationView.OnNavigationItemSelectedListener,
        SelectionFragment.OnItemSelectedListener {

    private Integer selectedCompetitionId = -1;
    private Integer selectedSquadId = -1;

    private void openCompetitionSelection() {
        CompetitionSelectionFragment fragment = new CompetitionSelectionFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_content, fragment);
        fragmentTransaction.commit();
    }

    private void openSquadSelection() {
        SquadSelectionFragment fragment = SquadSelectionFragment.newInstance(selectedCompetitionId);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_content, fragment);
        fragmentTransaction.commit();
    }

    private void startScoringActivity() {
        Intent intent = new Intent(this, ScoringActivity.class);
        intent.putExtra(ScoringActivity.ARG_COMPETITIONID, selectedCompetitionId);
        intent.putExtra(ScoringActivity.ARG_SQUADID, selectedSquadId);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            }
        });
        */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle( this, drawer, toolbar, R.string.label_action_drawer_open, R.string.label_action_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_competitions);
        openCompetitionSelection();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(item.getItemId()) {
            case R.id.nav_competitions:
                openCompetitionSelection();
                break;
            case R.id.nav_configuration:
                Logger.getGlobal().log(Level.INFO, "opening configuration");
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            default:
                Logger.getGlobal().log(Level.WARNING, "unhandled click on navigation item " + item.toString());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onItemSelected(String tag, Integer itemId) {
        switch (tag) {
            case SelectionFragment.TAG_COMPETITION:
                selectedCompetitionId = itemId;
                openSquadSelection();
                break;
            case SelectionFragment.TAG_SQUAD:
                selectedSquadId = itemId;
                startScoringActivity();
                break;
        }
    }
}