package net.smilfinken.sagittarius;

import android.support.v4.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class ScoringActivity extends AppCompatActivity implements RecyclerView.OnItemTouchListener {

    private CharSequence appTitle;
    private CharSequence navigationDrawerTitle;
    private DrawerLayout navigationDrawerLayout;
    private ActionBarDrawerToggle navigationDrawerToggle;
    private ListView navigationDrawer;
    private String[] navigationMenuItems;

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        public DrawerItemClickListener() {
            onItemClick(null, null, 0, 0);
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }

        private void selectItem(int position) {
            Fragment fragment;

            switch (position) {
                case 0:
                    fragment = new CompetitionListFragment();
                    break;
                default:
                    fragment = new CompetitionListFragment();
            }

            Bundle args = new Bundle();
            fragment.setArguments(args);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_content, fragment).commit();

            navigationDrawer.setItemChecked(position, true);
            setTitle(navigationMenuItems[position]);
            navigationDrawerLayout.closeDrawer(navigationDrawer);
        }

        private void setTitle(CharSequence title) {
            navigationDrawerTitle = title;
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        appTitle = getSupportActionBar().getTitle();

        navigationMenuItems = getResources().getStringArray(R.array.navigation);
        navigationDrawerLayout = (DrawerLayout) findViewById(R.id.navigation_layout);
        navigationDrawer = (ListView) findViewById(R.id.navigation_drawer);
        navigationDrawer.setAdapter(new ArrayAdapter<String>(this, R.layout.item_navigation, navigationMenuItems));
        navigationDrawer.setOnItemClickListener(new DrawerItemClickListener());

        navigationDrawerToggle = new ActionBarDrawerToggle(this, navigationDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(appTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(navigationDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        navigationDrawerLayout.setDrawerListener(navigationDrawerToggle);
        navigationDrawer.setSelection(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scoring, menu);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        navigationDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        navigationDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (navigationDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
