package com.example.sudoku;

import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.Vector;

class SudokuEngine {

    private static final int dim = 9;
    private static final int blockDim = 3;
    private static final int fileAmount = 15;

    private static final String filenamePostfix = ".txt";
    private static final String emptyNumber = "0";

    static String[] readField(AssetManager assetManager,
                              MainActivity.COMPL complexity) throws IOException {

        String[] initialField = new String[dim*dim];

        String compl = complexity.getPrefix();

        Random rnd = new Random();
        int var = rnd.nextInt(fileAmount - 1) + 1;
        String filename = compl + var + filenamePostfix;

        //filename = "e1s.txt";  // solved variant, only for testing

        InputStream inputStream = assetManager.open(filename);

        for (int i = 0; i < dim; ++i) {
            for (int j = 0; j < dim; ++j) {
                char num = (char) inputStream.read();
                initialField[i * dim + j] = String.valueOf(num);
            }
        }

        return initialField;
    }

    private static String[] merge(String[] initialField, String[] workingField) {
        String[] mergedField = new String[dim*dim];
        for (int i = 0; i < dim; ++i) {
            for (int j = 0; j < dim; ++j) {
                String initialNum = initialField[i * dim + j];
                String workingNum = workingField[i * dim + j];

                if (!initialNum.equals(emptyNumber)) {
                    mergedField[i * dim + j] = initialNum;
                } else if (workingNum != null && !workingNum.equals("")) {
                    mergedField[i * dim + j] = workingNum;
                } else {
                    mergedField[i * dim + j] = "";
                }
            }
        }
        return mergedField;
    }

    static boolean check(String[] initialField, String[] workingField) {

        String[] mergedField = merge(initialField, workingField);

        for (int i = 0; i < dim; ++i) {

            // Rows and columns

            Vector<String> rowOfNums = new Vector<>();
            Vector<String> colOfNums = new Vector<>();
            for (int j = 0; j < dim; ++j) {
                String rowCell = mergedField[i*dim + j];
                String colCell = mergedField[j*dim + i];
                if (rowCell.length() != 1 || colCell.length() != 1 ||
                        rowOfNums.contains(rowCell) || colOfNums.contains(colCell)) {
                    return false;
                } else {
                    rowOfNums.add(rowCell);
                    colOfNums.add(colCell);
                }
            }

            // Blocks

            Vector<String> setOfNums = new Vector<>();
            int row_ind = i / blockDim * blockDim;
            int col_ind = i * blockDim - row_ind * blockDim;

            for (int j = row_ind; j < row_ind + blockDim; ++j) {
                for (int k = col_ind; k < col_ind + blockDim; ++k) {
                    String currNum = mergedField[j * dim + k];
                    if (setOfNums.contains(currNum)) {
                        return false;
                    } else {
                        setOfNums.add(currNum);
                    }
                }
            }
        }

        return true;
    }

}
