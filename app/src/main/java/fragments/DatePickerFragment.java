package fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    public static final String REQUEST_KEY = "dateRequestKey";

    public static final String KEY_YEAR = "year";
    public static final String KEY_MONTH = "month";
    public static final String KEY_DAY = "day";

    // reference: https://developer.android.com/develop/ui/views/components/pickers#java
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker.
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it.
        return new DatePickerDialog(requireContext(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date the user picks.
        // reference: https://developer.android.com/guide/fragments/communicate#fragment-result
        Bundle result = new Bundle();
        result.putInt(KEY_YEAR, year);
        result.putInt(KEY_MONTH, month);
        result.putInt(KEY_DAY, day);
        getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
    }
}
