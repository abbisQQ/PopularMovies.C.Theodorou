package com.example.babis.favoritemoviesctheodorou;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static boolean TABLET = false;
    public boolean isTablet(Context context)
    {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)==4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)==Configuration.SCREENLAYOUT_SIZE_LARGE);
        return(xlarge||large);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TABLET=isTablet(this);

        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().add(R.id.main_fragment_container,new MainFragment()).commit();
        }


    }

    //Creates the option Menu in our main fragment

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_fragment_menu,menu);
        return true;
    }

    //handles the click in main fragment option menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItem =  item.getItemId();


        switch (menuItem){
            case R.id.most_popular_menu_item:
                MainFragment.sortByPop=true;
                break;
            case R.id.top_rated_menu_item:
                MainFragment.sortByPop=false;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container,new MainFragment()).commit();


        return true;
    }
}
