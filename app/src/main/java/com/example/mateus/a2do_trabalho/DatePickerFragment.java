package com.example.mateus.a2do_trabalho;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Mateus on 21/11/2016.
 */

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private String campo;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        this.campo = getArguments().getString("campo");

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {

        String data = String.valueOf(day) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(year);
        if(campo.equals("limite")) {
            TextView textDataLimite = (TextView) getActivity().findViewById(R.id.textDataLimiteForm);
            textDataLimite.setText(data);
        } else {
            TextView textDataLembrete = (TextView) getActivity().findViewById(R.id.textDataLembreteForm);
            textDataLembrete.setText(data);
        }
    }

}
