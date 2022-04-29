package edu.mg.eni.m2.patient.consultation.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import edu.mg.eni.m2.patient.consultation.R;
import edu.mg.eni.m2.patient.consultation.adapter.HomeAdapter;
import edu.mg.eni.m2.patient.consultation.helpers.DBConstants;


public class HomeActivity extends AppCompatActivity {
    private HomeAdapter adapter;
    private Integer[] imge = {Integer.valueOf(R.drawable.medecin), Integer.valueOf(R.drawable.patient_bed), Integer.valueOf(R.drawable.patient)};
    private GridView mGridView;
    private String[] titre = {"Mes médecin", "Nos patient", "Consultations"};

    private  int PERMISSION_ALL = 1;
    private String[] PERMISSIONS = {

            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,

    };

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_home);

        if (!hasPermissions(HomeActivity.this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(HomeActivity.this, PERMISSIONS, PERMISSION_ALL);
        }

        this.adapter = new HomeAdapter(this, this.imge, this.titre);
        this.mGridView = (GridView) findViewById(R.id.gridView);
        this.mGridView.setAdapter(this.adapter);
        this.mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                Intent intent = new Intent(HomeActivity.this.getApplicationContext(), MainActivity.class);
                Bundle bundle = new Bundle();
                switch (i) {
                    case 0:
                        bundle.putString("type", DBConstants.MEDECIN_TABLE_NAME);
                        intent.putExtras(bundle);
                        break;
                    case 1:
                        bundle.putString("type", DBConstants.PATIENT_TABLE_NAME);
                        intent.putExtras(bundle);
                        break;
                    case 2:
                        bundle.putString("type", DBConstants.TRAITEMENT_TABLE_NAME);
                        intent.putExtras(bundle);
                        break;
                }
                HomeActivity.this.startActivity(intent);
            }
        });
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}