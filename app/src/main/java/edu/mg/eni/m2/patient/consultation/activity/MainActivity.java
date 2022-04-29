package edu.mg.eni.m2.patient.consultation.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.view.MenuItem;

import edu.mg.eni.m2.patient.consultation.R;
import edu.mg.eni.m2.patient.consultation.fragment.MedecinFragment;
import edu.mg.eni.m2.patient.consultation.fragment.PatientFragment;
import edu.mg.eni.m2.patient.consultation.fragment.TraitementFragment;
import edu.mg.eni.m2.patient.consultation.helpers.DBConstants;
import edu.mg.eni.m2.patient.consultation.helpers.FileHelpers;

public class MainActivity extends AppCompatActivity {

    private Explode enterTransition = null;
    private Context mContext;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.navigation_dashboard:
                    MainActivity.this.displaySelectedScreen(new PatientFragment());
                    return true;
                case R.id.navigation_home:
                    MainActivity.this.displaySelectedScreen(new MedecinFragment());
                    return true;
                case R.id.navigation_notifications:
                    MainActivity.this.displaySelectedScreen(new TraitementFragment());
                    return true;
                default:
                    return false;
            }
        }
    };


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        this.mContext = this.getApplicationContext();

        setContentView((int) R.layout.activity_main);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this.mOnNavigationItemSelectedListener);
        if (Build.VERSION.SDK_INT >= 21) {
            this.enterTransition = new Explode();
            this.enterTransition.setDuration(500);
            getWindow().setEnterTransition(this.enterTransition);
        }
        String stringExtra = getIntent().getStringExtra("type");
        char c = 65535;
        int hashCode = stringExtra.hashCode();
        if (hashCode != -1841987083) {
            if (hashCode != -791418107) {
                if (hashCode == 940657167 && stringExtra.equals(DBConstants.MEDECIN_TABLE_NAME)) {
                    c = 0;
                }
            } else if (stringExtra.equals(DBConstants.PATIENT_TABLE_NAME)) {
                c = 1;
            }
        } else if (stringExtra.equals(DBConstants.TRAITEMENT_TABLE_NAME)) {
            c = 2;
        }
        switch (c) {
            case 0:
                bottomNavigationView.getMenu().getItem(0).setChecked(true);
                displaySelectedScreen(new MedecinFragment());
                return;
            case 1:
                bottomNavigationView.getMenu().getItem(1).setChecked(true);
                displaySelectedScreen(new PatientFragment());
                return;
            case 2:
                bottomNavigationView.getMenu().getItem(2).setChecked(true);
                displaySelectedScreen(new TraitementFragment());
                return;
            default:
                return;
        }



    }

    public void displaySelectedScreen(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
            beginTransaction.replace(R.id.content_frame, fragment);
            beginTransaction.commit();
        }
    }

}
