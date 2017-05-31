package com.example.sudoku;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import java.util.Locale;


public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button startButton = (Button)findViewById(R.id.start_button);
        final Intent gameIntent = new Intent(this, MainActivity.class);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(gameIntent);
            }
        });

        Button helpButton = (Button)findViewById(R.id.help_button);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeDialog(getString(R.string.help),getString(R.string.rules_message));
            }
        });

        Button infoButton = (Button)findViewById(R.id.about_button);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeDialog(getString(R.string.about),getString(R.string.about_message));
            }
        });

        Button optionsButton = (Button)findViewById(R.id.options_button);
        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeOptionsDialog();
            }
        });

    }

    // диалог с настройками
    private void makeOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder
                .setView(R.layout.options_layout)
                .setCancelable(true)
                .setPositiveButton("OK", null)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        String currLocale;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currLocale = getResources().getConfiguration().getLocales().get(0).toString();
        } else {
            //noinspection deprecation
            currLocale = getResources().getConfiguration().locale.toString();
        }

        final int langIndex = (currLocale.contains("ru") ? 0 : 1);
        final int complIndex = (MainActivity.complexity == MainActivity.COMPL.EASY ? 0 :
                MainActivity.complexity == MainActivity.COMPL.MEDIUM ? 1 : 2);

        Spinner langSpinner = (Spinner) dialog.findViewById(R.id.spinnerLanguage);
        Spinner complSpinner = (Spinner) dialog.findViewById(R.id.spinnerComplexity);

         if (langSpinner != null) {
            langSpinner.setSelection(langIndex);
        }
        if (complSpinner != null) {
            complSpinner.setSelection(complIndex);
        }

        Button okButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        View v = (View) okButton.getParent();

        v.setBackgroundColor(
                ContextCompat.getColor(
                        getApplicationContext(), R.color.menu_button_background));

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner spinnerCompl = (Spinner) dialog.findViewById(R.id.spinnerComplexity);
                int idx;
                if (spinnerCompl != null && (idx = spinnerCompl.getSelectedItemPosition()) != complIndex) {
                    switch (idx) {
                        case 0: {
                            MainActivity.complexity = MainActivity.COMPL.EASY;
                            break;
                        }
                        case 1: {
                            MainActivity.complexity = MainActivity.COMPL.MEDIUM;
                            break;
                        }
                        case 2: {
                            MainActivity.complexity = MainActivity.COMPL.HARD;
                            break;
                        }
                    }
                }
                Spinner spinnerLang = (Spinner) dialog.findViewById(R.id.spinnerLanguage);
                if (spinnerLang != null && spinnerLang.getSelectedItemPosition() != langIndex) {

                    Locale newLocale = new Locale(langIndex == 0 ? "en" : "ru");
                    Configuration conf = new Configuration();
                    conf.setLocale(newLocale);
                    //noinspection deprecation
                    getResources().updateConfiguration(conf, getResources().getDisplayMetrics());

                    finish();
                    startActivity(new Intent(getApplicationContext(), MenuActivity.class));
                }
                dialog.cancel();
            }
        });
    }
    // диалог для остальных кнопок
    private void makeDialog(String title, String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.setMessage(text)
                .setTitle(title)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Button okButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        View v = (View)okButton.getParent();
        v.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.menu_button_background));
    }

}
