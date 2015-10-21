package com.nutriapp.upeu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.github.paolorotolo.appintro.AppIntro;
import com.nutriapp.upeu.managers.ItemsSlide;
import com.nutriapp.upeu.sqlite.DbUsuarios;

/**
 * Created by Kelvin on 18/10/2015.
 */
public class MainIntro extends AppIntro {
    private DbUsuarios dbUsuarios;
    private String ID_GOOGLE;
    @Override
    public void init(Bundle savedInstanceState) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean previouslyStarted = prefs.getBoolean("is_first", false);
        if (!previouslyStarted) {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean("is_first", Boolean.TRUE);
            edit.commit();
            addSlide(ItemsSlide.newInstance(R.layout.layout_slide_1));
            addSlide(ItemsSlide.newInstance(R.layout.layout_slide_2));
            addSlide(ItemsSlide.newInstance(R.layout.layout_slide_3));
            addSlide(ItemsSlide.newInstance(R.layout.layout_slide_4));
            setSkipText("Saltar");
            setDoneText("Entendido");
        } else {
            dbUsuarios = new DbUsuarios(this);
            dbUsuarios.open();
            ID_GOOGLE = dbUsuarios.getIdGoogleUser();
            if(ID_GOOGLE.equals("") || ID_GOOGLE ==null){
                startActivity(new Intent(getApplicationContext(),SignInActivity.class));
                finish();
            }else{
                startActivity(new Intent(getApplicationContext(), DrawerMenu.class));
                finish();
            }

        }


    }


    @Override
    public void onSkipPressed() {
        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
    }

    @Override
    public void onDonePressed() {
        startActivity(new Intent(getApplicationContext(), SignInActivity.class));

    }
}
