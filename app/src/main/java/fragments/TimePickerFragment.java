package fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    // reference: https://developer.android.com/develop/ui/views/components/pickers#java
    public static final String REQUEST_KEY = "timeRequestKey";

    public static final String KEY_HOUR_OF_DAY = "hourOfDay";
    public static final String KEY_MINUTE = "minute";
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker.
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it.
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time the user picks.
        // reference: https://developer.android.com/guide/fragments/communicate#fragment-result
        Bundle result = new Bundle();
        result.putInt(KEY_HOUR_OF_DAY, hourOfDay);
        result.putInt(KEY_MINUTE, minute);
        getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
    }
}
