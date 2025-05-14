package thanjavurvansales.sss;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SettingsActivity extends AppCompatActivity {

    LinearLayout PrinterLL,DBshareLL;
    ImageView settingsgoback,settingslogout;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        context = this;

        PrinterLL = (LinearLayout) findViewById(R.id.PrinterLL);
        DBshareLL = (LinearLayout) findViewById(R.id.DBshareLL);
        settingsgoback = (ImageView) findViewById(R.id.settingsgoback);
        settingslogout = (ImageView) findViewById(R.id.settingslogout);


        settingsgoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack(null);
            }
        });

        settingslogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirmation");
                builder.setIcon(R.mipmap.ic_vanlauncher);
                builder.setMessage("Are you sure you want to logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(context, LoginActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(i);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        PrinterLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SettingsActivity.this,PrinterSettingsActivity.class);
                startActivity(i);
            }
        });
        DBshareLL.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {

                ShareDataBaseBackup.ShareDBBackup(SettingsActivity.this,context);

            }
        });
    }
    public void goBack(View v) {
        LoginActivity.ismenuopen=true;
        Intent i = new Intent(context, MenuActivity.class);
        startActivity(i);
    }

}