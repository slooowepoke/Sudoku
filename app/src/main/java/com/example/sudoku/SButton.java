package com.example.sudoku;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.TableRow;

import java.util.Arrays;
import java.util.List;

// перегруженный класс кнопки
class SButton extends android.support.v7.widget.AppCompatButton {
    private int row;
    private int col;

    @SuppressWarnings("FieldCanBeLocal")
    private final float initTextSize = 30;
    @SuppressWarnings("FieldCanBeLocal")
    private final int blockMargin = 5,
            normalMargin = 1;

    @SuppressWarnings("FieldCanBeLocal")
    private final int maxSymbolCount = 10;

    @SuppressWarnings("FieldCanBeLocal")
    private final
    List<Integer> blockEdges = Arrays.asList(2, 5);

    @SuppressWarnings("FieldCanBeLocal")
    private final String numberSeparator = " ";

    enum State {
        NORMAL, ACTIVATED, EDITING, INITIAL
    }

    public SButton(Context context) {
        super(context);
        row = col = 0;
        setState(State.NORMAL);
    }

    public SButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        row = col = 0;
        setState(State.NORMAL);
    }

    public SButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        row = col = 0;
        setState(State.NORMAL);
    }

    public void setCoords(int row, int col) {
        this.row = row;
        this.col = col;

        TableRow.LayoutParams params =
                new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f);

        params.setMargins(1, 1,
                (blockEdges.contains(col) ? blockMargin : normalMargin),
                (blockEdges.contains(row) ? blockMargin : normalMargin));

        setLayoutParams(params);
    }

    public Pair<Integer, Integer> getCoords() {
        return new Pair<>(row, col);
    }

    // метод для установки "окончательной" цифры
    public void setNum(String text) {
        setText(text);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        setTextSize(TypedValue.COMPLEX_UNIT_SP,
                getText().length() == 1 ? initTextSize : initTextSize / 2);
    }

    // метод, вызываемый при добавлении цифр-вариантов
    public void appendNum(String text) throws Exception {
        String currText = getText().toString();
        if (currText.contains(numberSeparator + text)) {

            setText(currText.replace(numberSeparator + text, ""));
        } else if (currText.contains(text + numberSeparator)) {
            setText(currText.replace(text + numberSeparator, ""));
        } else if (currText.contains(text)) {
            setText(currText.replace(text, ""));
        } else {
            // если кнопка была пустой
            if (currText.length() == 0) {
                setText(text);
            } else if (currText.length() < maxSymbolCount) {
                setText(currText + numberSeparator + text);
            } else {
                throw new Exception(getResources().getString(R.string.warn_too_much));
            }
        }
    }

    public void setState(State state) {
        switch (state) {
            case NORMAL: {
                setBackgroundColor(ContextCompat.getColor(getContext(), R.color.cell_color));
                break;
            }
            case ACTIVATED: {
                setBackgroundColor(ContextCompat.getColor(getContext(), R.color.activated));
                break;
            }
            case EDITING: {
                setBackgroundColor(ContextCompat.getColor(getContext(), R.color.editing));
                break;
            }
            case INITIAL: {
                setBackgroundColor(ContextCompat.getColor(getContext(), R.color.cell_color));
                setTextColor(ContextCompat.getColor(getContext(), R.color.initial));
                setClickable(false);
                break;
            }
        }
    }
}
