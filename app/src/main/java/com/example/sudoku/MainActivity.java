package com.example.sudoku;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private TableLayout table;

    // Массивы содержимого для перезагрузки activity
    private String[] initialField;
    private String[] workingField;

    @SuppressWarnings("FieldCanBeLocal")
    private final String emptyNumber = "0";
    @SuppressWarnings("FieldCanBeLocal")
    private final int singleEraseOffset = 1, doubleEraseOffset = 2;

    private int faults = 0;

    private final int dim = 9;

    private Pair<Integer, Integer> activated = null;
    private boolean isEditing = false;

    enum COMPL {

        EASY ("e", 3), MEDIUM ("m", 2), HARD ("h", 1);

        private final String prefix;
        private final int maxFaults;

        COMPL(String prefix, int maxFaults) {
            this.prefix = prefix;
            this.maxFaults = maxFaults;
        }

        String getPrefix() {
            return prefix;
        }

        int getMaxFaults() {
            return maxFaults;
        }

    }


    public static COMPL complexity = COMPL.EASY;

    // создание всей активности
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        table = (TableLayout) findViewById(R.id.field_layout);

        drawField();

        if (savedInstanceState == null) {
            initialField = new String[dim*dim];
            workingField = new String[dim * dim];
            readField();
        } else {
            initialField = savedInstanceState.getStringArray(initialField.getClass().getName());
            workingField = savedInstanceState.getStringArray(workingField.getClass().getName());
            if (savedInstanceState.getBoolean(getString(R.string.is_activated))) {
                activated = new Pair<>(
                        savedInstanceState.getInt(getString(R.string.activated_first)),
                        savedInstanceState.getInt(getString(R.string.activated_second)));
                getAt(activated.first, activated.second).setState(SButton.State.ACTIVATED);
            }
        }

        fillField();

    }

    private SButton getAt(int row, int col) {
        return (SButton) ((TableRow) table.getChildAt(row)).getChildAt(col);
    }

    // построение (отрисовка) поля
    private void drawField() {
        for (int i = 0; i < dim; ++i) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
            ));
            row.setWeightSum(9);

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SButton it = (SButton) v;
                    if (activated == null) {
                        it.setState(SButton.State.ACTIVATED);
                        activated = it.getCoords();
                        isEditing = false;
                    } else if (activated.equals(it.getCoords())) {
                        it.setState(SButton.State.NORMAL);
                        activated = null;
                        isEditing = false;
                    } else {
                        getAt(activated.first, activated.second)
                                .setState(SButton.State.NORMAL);
                        it.setState(SButton.State.ACTIVATED);
                        activated = it.getCoords();
                        isEditing = false;
                    }
                }
            };

            LayoutInflater inflater = (LayoutInflater)
                    getApplicationContext().getSystemService(
                            Context.LAYOUT_INFLATER_SERVICE);



            for (int j = 0; j < dim; ++j) {
                @SuppressLint("InflateParams")
                SButton btn = (SButton) inflater.inflate(R.layout.s_button, null);
                btn.setCoords(i,j);
                btn.setOnClickListener(listener);
                row.addView(btn, j);
            }
            row.setBaselineAligned(false);
            table.addView(row, i);
        }
        table.setStretchAllColumns(true);
    }

    private void fillField() {
        for (int i = 0; i < dim; ++i) {
            for (int j = 0; j < dim; ++j) {
                String initialNum = initialField[i * dim + j];
                String workingNum = workingField[i * dim + j];
                if (initialNum != null && !initialNum.equals(emptyNumber)) {
                    getAt(i, j).setText(initialNum);
                    getAt(i, j).setState(SButton.State.INITIAL);
                } else if (workingNum != null && !workingNum.equals("")) {
                    getAt(i, j).setText(workingNum);
                }
            }
        }
    }

    private void readField() {
        try {
            initialField = SudokuEngine.readField(getAssets(), complexity);
        } catch (IOException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.input_err)
                    .setPositiveButton(R.string.yes, null)
                    .setTitle(R.string.error)
                    .create()
                    .show();
        }
    }

    // нажатие на кнопку проверки
    @SuppressWarnings("UnusedParameters")
    public void checkClicked(View view) {
        String text, title;
        int remained = 0;
        if (SudokuEngine.check(initialField, workingField)) {
            title = getString(R.string.victory);
            text = getString(R.string.congrats);
        } else {
            faults++;
            title = getString(R.string.fault);
            text = getString(R.string.lives_remained) +
                    (remained = complexity.getMaxFaults() - faults);
        }

        final boolean lost = (remained == 0);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.setMessage(text)
                .setTitle(title)
                .setPositiveButton(R.string.yes, null)
                .create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.cancel();
                if (lost) {
                    finish();
                    startActivity(new Intent(getApplicationContext(), MenuActivity.class));
                }
            }
        });

        Button okButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        View v = (View) okButton.getParent();

        v.setBackgroundColor(
                ContextCompat.getColor(
                        getApplicationContext(),
                        R.color.menu_button_background));

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                if (lost) {
                    finish();
                    startActivity(new Intent(getApplicationContext(), MenuActivity.class));
                }
            }
        });
    }

    // нажатие на кнопку редактирования
    @SuppressWarnings("UnusedParameters")
    public void editClicked(View view) {
        SButton btn;
        if (activated != null) {
            try {
                btn = getAt(activated.first, activated.second);
            } catch (Exception e) {
                return;
            }
            isEditing = !isEditing;
            btn.setState(isEditing ? SButton.State.EDITING : SButton.State.NORMAL);
        }
    }

    // нажатие на аппаратную кнопку назад
    @Override
    public void onBackPressed() {
        // предупреждение о потере прогресса
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.setTitle(R.string.warning)
                .setMessage(R.string.warning_text)
                .setPositiveButton(R.string.yes, null)
                .setNegativeButton(R.string.no, null)
                .setCancelable(true)
                .create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

        Button okButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        View v = (View) okButton.getParent();

        v.setBackgroundColor(
                ContextCompat.getColor(
                        getApplicationContext(),
                        R.color.menu_button_background));

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                finish();
                startActivity(new Intent(getApplicationContext(), MenuActivity.class));
            }
        });

        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
    }

    // нажатие на кнопку удаления цифры
    @SuppressWarnings("UnusedParameters")
    public void eraseClicked(View view) {
        if (activated != null) {
            SButton btn = getAt(activated.first, activated.second);
            CharSequence txt = btn.getText();
            int lght = txt.length();
            if (lght != 0) {
                btn.setText(txt.subSequence(0, (lght > singleEraseOffset
                        ? lght - doubleEraseOffset
                        : lght - singleEraseOffset)));
            }
            if (btn.getText().length() == 0) {
                activated = null;
                isEditing = false;
                btn.setState(SButton.State.NORMAL);
            }
        }
    }

    // нажата одна из цифр для добавления на выделенную кнопку
    public void numClicked(View view) {
        if (activated != null) {
            SButton btn;
            try {
                btn = getAt(activated.first, activated.second);
            } catch (Exception e) {
                return;
            }
            if (isEditing) {
                try {
                    btn.appendNum(((Button) view).getText().toString());
                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            } else {
                btn.setNum(((Button) view).getText().toString());
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(getString(R.string.is_editing), isEditing);
        outState.putInt(getString(R.string.faults), faults);
        outState.putBoolean(getString(R.string.is_activated), activated != null);

        if (activated != null) {
            outState.putInt(getString(R.string.activated_first), activated.first);
            outState.putInt(getString(R.string.activated_second), activated.second);
        }

        for (int i = 0; i < dim; ++i) {
            for (int j = 0; j < dim; ++j) {
                workingField[i * dim + j] = getAt(i, j).getText().toString();
            }
        }
        outState.putStringArray(workingField.getClass().getName(), workingField);
        outState.putStringArray(initialField.getClass().getName(), initialField);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isEditing = savedInstanceState.getBoolean(getString(R.string.is_editing));
        faults = savedInstanceState.getInt(getString(R.string.faults));
    }
}
