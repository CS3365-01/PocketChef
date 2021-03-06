package ttu.edu.pocketchef;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ViewFlipper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import ttu.edu.pocketchef.content.DB;
import ttu.edu.pocketchef.content.RecipeViewKind;
import ttu.edu.pocketchef.fragments.AddRecipeFragment;
import ttu.edu.pocketchef.fragments.HomeFragment;
import ttu.edu.pocketchef.fragments.SearchFragment;
import ttu.edu.pocketchef.fragments.ViewFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HomeFragment.OnFragmentInteractionListener,
        SearchFragment.OnFragmentInteractionListener,
        AddRecipeFragment.OnFragmentInteractionListener,
        ViewFragment.OnFragmentInteractionListener {

    private ViewFlipper vf;
    private HomeFragment homeFragment;
    private AddRecipeFragment addRecipeFragment;
    private SearchFragment searchFragment;
    private ViewFragment viewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        vf = (ViewFlipper)findViewById(R.id.vf);

        homeFragment = new HomeFragment();
        homeFragment.setArguments(new Bundle());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_home, homeFragment)
                .commit();

        searchFragment = new SearchFragment();
        searchFragment.setArguments(new Bundle());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_search, searchFragment)
                .commit();

        addRecipeFragment = new AddRecipeFragment();
        addRecipeFragment.setArguments(new Bundle());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_add_recipe, addRecipeFragment)
                .commit();

        viewFragment = new ViewFragment();
        viewFragment.setArguments(new Bundle());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_view, viewFragment)
                .commit();

        DB.create(getResources(), getApplicationContext().getCacheDir());
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
        // automatically handle clicks on the HomeFragment/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            vf.setDisplayedChild(0);
        } else if (id == R.id.nav_search) {
            vf.setDisplayedChild(1);
        } else if (id == R.id.nav_add) {
            vf.setDisplayedChild(2);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onHomeFragmentInteraction(long id, RecipeViewKind kind) {
        if (kind == RecipeViewKind.VIEW) {
            viewFragment.populateWithRecipe(id);
            vf.setDisplayedChild(3);
        } else {
            addRecipeFragment.populateWithRecipe(id);
            vf.setDisplayedChild(2);
        }
    }

    @Override
    public void onSearchFragmentInteraction(long id, RecipeViewKind kind) {
        if (kind == RecipeViewKind.VIEW) {
            viewFragment.populateWithRecipe(id);
            vf.setDisplayedChild(3);
        } else {
            addRecipeFragment.populateWithRecipe(id);
            vf.setDisplayedChild(2);
        }
    }

    @Override
    public void onAddRecipeFragmentInteraction(boolean saved) {
        vf.setDisplayedChild(0);

        homeFragment.refreshHome();
    }

    @Override
    public void onViewRecipeFragmentInteraction() {

    }
}
