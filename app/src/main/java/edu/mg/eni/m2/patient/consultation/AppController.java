package edu.mg.eni.m2.patient.consultation;

import android.app.Application;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.IoniconsModule;

public class AppController extends Application {
    public void onCreate() {
        super.onCreate();
        Iconify.with(new IoniconsModule());
    }
}
