package com.chaika;

import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.chaika.application.ApplicationConfig;
import com.chaika.application.ChaikaApplication;
import com.chaika.componentes.AppComponent;
import com.chaika.componentes.DaggerAppComponent;
import com.chaika.databases.Data;
import com.chaika.estructuraDatos.constantes.MyStatus;
import com.chaika.fragmentos.AllSeriesFragment;
import com.chaika.fragmentos.ShowListByStatus;
import com.chaika.fragmentos.adaptadores.ViewPagerAdapter;
import com.chaika.llamadasAPI.RestApiMal;
import com.chaika.search.content_provider.Provider;
import com.orhanobut.logger.Logger;

import javax.inject.Inject;

/***
 *
 * Actividad principal de la aplicación, en ella se muestran todas las series del usuario.
 *
 * Esta compuesta por un adaptador de fragmentos, este permite agregar distintas vistas con desplazamiento lateral.
 *
 * Cada una de esta vistas es un fragmento y cada fragmento contiene un RecyclerView que nos permite mostrar una pila de datos en forma de lista.
 *
 * Cada lista es distinta, que se filtran según un criterio.
 *
 */
public class MainActivity extends AppCompatActivity  {

    AppComponent component;

    //Barra de carga
    public static ProgressBar progressBar;

    @Inject
    Data data;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager_main);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        //Definimos en la configuración la activad y contexto que podemos necesitar en distintos puntos de la aplicación

        ApplicationConfig.getInstance().setActivity(this);
        ApplicationConfig.getInstance().setContext(this);



        //////////dagger2
        component = DaggerAppComponent.builder()
               .databaseApplicationComponent(ChaikaApplication.get(this).component())
                .build();

        component.injectMain(this);

        //revisar: http://blog.rhesoft.com/2015/03/30/tutorial-android-actionbar-with-material-design-and-search-field/
        //toolbar de la aplicación



        initViewPagerAndTabs();

    }//fin onCreate

    //history
    private void addQueryToRecent(String query) {
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                Provider.AUTHORITY,
                Provider.MODE);
        suggestions.saveRecentQuery(query, "recent");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_box, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                Logger.d(query);
                if (!String.valueOf(query).isEmpty()) {
                    RestApiMal.getInstance().getAnimeSearch(query.trim(), ApplicationConfig.getInstance().getUsername(), ApplicationConfig.getInstance().getPassword(), ApplicationConfig.getInstance());
                }
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Logger.d(newText);//parte que se actualiza cada nuevo caracter
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    /***
     * Localiza el viewpager en el que se incrustará la vista en la pantalla y se agregan los fragmentod que utilizaremos
     */
    private void initViewPagerAndTabs() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);

        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFrag(AllSeriesFragment.instance(),getString(R.string.app_main_tab_series));

        //Definimos todo el contenido de cada uno de los fragmentos

        pagerAdapter.addFrag(ShowListByStatus.newInstance(MyStatus.watching),getString(R.string.app_main_tab_watching));
        pagerAdapter.addFrag(ShowListByStatus.newInstance(MyStatus.completed),getString(R.string.app_main_tab_completed));
        pagerAdapter.addFrag(ShowListByStatus.newInstance(MyStatus.onhold),getString(R.string.app_main_tab_on_hold));
        pagerAdapter.addFrag(ShowListByStatus.newInstance(MyStatus.dropped),getString(R.string.app_main_tab_dropped));
        pagerAdapter.addFrag(ShowListByStatus.newInstance(MyStatus.plantowatch),getString(R.string.app_main_tab_plan_to_watch));


        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);//produce efecto scroll en tabLayout para futuras mejoras -> https://medium.com/@elsenovraditya/set-tab-minimum-width-of-scrollable-tablayout-programmatically-8146d6101efe
        tabLayout.setupWithViewPager(viewPager);
    }



}//fin clase






//    /*        int sizeList = ChaikaApplication.get(this).component().getData().getSizeListMal();
//            if (sizeList == 0) {
//                RestApiMal.getInstance().getMalUserProfile(ApplicationConfig.getInstance().getUsername(),"all","anime", AllSeriesFragment.instance());
//            }*/
//    //donde implementarlo?
//    AnimeData animeData = ChaikaApplication.get(this).component().getData().getAnimeById(16397);
//
//    String query = "Aldnoah.Zero";
////  RestApiMal.getInstance().getAnimeSearch(query,ApplicationConfig.getInstance().getUsername(),ApplicationConfig.getInstance().getPassword());

