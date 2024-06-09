package com.hareem.anxietyrelief.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.hareem.anxietyrelief.BookAppointment;
import com.hareem.anxietyrelief.R;

import java.util.List;

public class AvailabilityAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mTimeSlots;
    private TextView mSelectedTextView; // Keep track of the currently selected TextView
    private CardView mSelectedCardView; // Keep track of the currently selected CardView

    private String mSelectedDay;

    private BookAppointment bookAppointment;

    public AvailabilityAdapter(Context context,BookAppointment bookAppointment) {
        mContext = context;
        this.bookAppointment = bookAppointment;
    }



    public void setmTimeSlots(List<String> mTimeSlots) {
        this.mTimeSlots = mTimeSlots;
    }

    @Override
    public int getCount() {
        return mTimeSlots.size();
    }

    @Override
    public Object getItem(int position) {
        return mTimeSlots.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.item_time_slot_table_row, parent, false);
        }

        // Get the full time slot string
        String fullTimeSlot = mTimeSlots.get(position);

        // Extract the start time from the full time slot string
        String[] parts = fullTimeSlot.split(", "); // Split by comma
        String startTime = ""; // Initialize startTime
        for (String part : parts) {
            if (part.startsWith("Start Time:")) {
                startTime = part.substring(part.indexOf(":") + 2); // Extract the start time
                break;
            }
        }

        // Set time slot text to display only the start time
        TextView timeSlotTextView = view.findViewById(R.id.timeSlotTextView);
        timeSlotTextView.setText(startTime);

        return view;
    }

    public void populateTable(TableLayout tableLayout) {
        // Clear existing rows
        tableLayout.removeAllViews();

        // Create a row for the selected day
//        TableRow selectedDayRow = new TableRow(mContext);
//        TextView selectedDayTextView = new TextView(mContext);
//        selectedDayTextView.setText("Selected Day: " + mSelectedDay);
//        selectedDayTextView.setTextColor(Color.BLACK);
//        selectedDayTextView.setTypeface(null, Typeface.BOLD);
//        selectedDayTextView.setGravity(Gravity.START);
//        TableRow.LayoutParams selectedDayParams = new TableRow.LayoutParams(
//                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
//        selectedDayTextView.setLayoutParams(selectedDayParams);
//        selectedDayRow.addView(selectedDayTextView);
//
//        // Add the selected day row to the table layout
//        tableLayout.addView(selectedDayRow);

        // Iterate over the time slots and find the ones for the selected day



        for (String timeSlot : mTimeSlots) {
            // Check if the time slot belongs to the selected day
            if (timeSlot.contains("Day: " + mSelectedDay)) {
                // Extract the start and end times from the time slot
                String startTime = "";
                String endTime = "";
                String[] parts = timeSlot.split(", ");
                for (String part : parts) {
                    if (part.startsWith("Start Time:")) {
                        startTime = part.substring(part.indexOf(":") + 2);
                    } else if (part.startsWith("End Time:")) {
                        endTime = part.substring(part.indexOf(":") + 2);
                    }
                }

                // If start time and end time are within the same hour, display start time only
                if (isOneHourApart(startTime, endTime)) {
                    addTimeSlotRow(tableLayout, startTime);
                } else {
                    // Otherwise, create one-hour intervals
                    int startHour = parseHour(startTime);
                    int endHour = parseHour(endTime);
                    for (int hour = startHour; hour < endHour; hour++) {
                        // Construct the time slot for this hour interval
                        String hourStartTime = formatTime(hour); // Format hour as "XXam" or "XXpm"
                        addTimeSlotRow(tableLayout, hourStartTime);
                    }
                }
            }
        }
    }

    private int parseHour(String time) {
        int hour = Integer.parseInt(time.split("(?<=\\d)(?=\\p{Alpha})")[0]); // Extract hour from time string
        if (time.toLowerCase().contains("pm") && hour != 12) {
            hour += 12; // Convert to 24-hour format if PM and not 12pm
        } else if (time.toLowerCase().contains("am") && hour == 12) {
            hour = 0; // Convert 12am to 0 hour in 24-hour format
        }
        return hour;
    }

    private String formatTime(int hour) {
        String time;
        if (hour == 0) {
            time = "12am";
        } else if (hour == 12) {
            time = "12pm";
        } else if (hour < 12) {
            time = hour + "am";
        } else {
            time = (hour - 12) + "pm";
        }
        return time;
    }

    private boolean isOneHourApart(String startTime, String endTime) {
        int startHour = parseHour(startTime);
        int endHour = parseHour(endTime);
        return (endHour - startHour == 1);
    }

    private void addTimeSlotRow(TableLayout tableLayout, final String time) {
        // Create a CardView to hold the TextView
        final CardView cardView = new CardView(mContext);
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, // Width
                ViewGroup.LayoutParams.MATCH_PARENT // Height
        );
        params.setMargins(8, 8, 8, 8);
        cardView.setLayoutParams(params);
        cardView.setCardElevation(4); // Set elevation

        // Create a TextView to display the time
        final TextView textView = new TextView(mContext);
        textView.setText(time);
        textView.setTextSize(20); // Text size
        textView.setTextColor(mContext.getResources().getColor(R.color.verdigris)); // Use getResources().getColor() to resolve color
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD); // Bold text style
        textView.setPadding(16, 16, 16, 16);
        textView.setGravity(Gravity.CENTER);

        // Add the TextView to the CardView
        cardView.addView(textView);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the clicked CardView is the same as the previously selected one
                if (mSelectedCardView == cardView) {
                    // If it's the same as the previously selected one, do nothing
                    return;
                }

                // If another CardView was previously selected, reset its color and background
                if (mSelectedCardView != null) {
                    TextView selectedTextView = (TextView) mSelectedCardView.getChildAt(0);
                    selectedTextView.setTextColor(mContext.getResources().getColor(R.color.verdigris));
                    mSelectedCardView.setCardBackgroundColor(Color.WHITE);
                }

                // Change text color and background color to indicate selection
                textView.setTextColor(Color.WHITE);
                cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.verdigris));
                bookAppointment.setIsTimeSlotSelected(true);
                bookAppointment.setTimeSlotInputLayout("");

                String TimeSlot = textView.getText().toString();
                SharedPreferences.Editor editor = mContext.getSharedPreferences("Time Slot", Context.MODE_PRIVATE).edit();
                editor.putString("Time Slot", TimeSlot); // Hardcoded value for "Video Session"
                editor.apply();

                // Update the currently selected CardView
                mSelectedCardView = cardView;
                // Here you can handle further actions on selection, if needed
            }
        });

        // Add the CardView to the row
        TableRow row = new TableRow(mContext);
        row.addView(cardView);

        // Add the row to the table layout
        tableLayout.addView(row);
    }

    public void clear() {
        mTimeSlots.clear();
    }



    public void setmSelectedDay(String mSelectedDay) {
        this.mSelectedDay = mSelectedDay;
    }
}
