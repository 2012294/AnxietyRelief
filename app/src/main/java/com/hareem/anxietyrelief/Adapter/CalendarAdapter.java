package com.hareem.anxietyrelief.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.icu.text.DateFormatSymbols;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.hareem.anxietyrelief.Appointment;
import com.hareem.anxietyrelief.BookAppointment;
import com.hareem.anxietyrelief.PatientAPI;
import com.hareem.anxietyrelief.R;
import com.hareem.anxietyrelief.RetrofitClientInstance;

import org.bson.types.ObjectId;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalendarAdapter extends BaseAdapter {
    private Context mContext;

    private SharedPreferences sharedPreferences;
    private List<String> mDaysList;
    private List<String> mDays;
    private Calendar mCalendar;
    private TableLayout mAvailabilityTableLayout;

    private BookAppointment bookAppointment;




    public List<String> getAvailableTime() {
        return availableTime;
    }

    public void setAvailableTime(List<String> availableTime) {
        this.availableTime = availableTime;
    }

    private int selectedPosition = -1;

    private int check=0;

    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
    }
    List<String> availableTime;

    private String selectedDay; // Add this variable

    public void setSelectedDay(String selectedDay) {
        this.selectedDay = selectedDay;
    }



    public CalendarAdapter(Context context, List<String> daysList,TableLayout availabilityTableLayout, BookAppointment bookAppointment) {
        mContext = context;
        mDays = new ArrayList<>();
        mCalendar = Calendar.getInstance();
        mDaysList = daysList;
        mAvailabilityTableLayout = availabilityTableLayout;
        this.bookAppointment = bookAppointment;

        // Reset calendar to the beginning of the month
        mCalendar.set(Calendar.DAY_OF_MONTH, 1);

        // Populate days and find the position of the current date
        populateDays();
        int currentDayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        // Initialize selectedPosition and selectedDay to the first selectable date
        selectedPosition = -1;
        selectedDay = null;

        // Find the first available working day
        for (int i = 0; i < mDays.size(); i++) {
            String dayOfWeek = getDayOfWeek(i);
            if (mDaysList.contains(dayOfWeek)) {
                selectedPosition = i;
                selectedDay = mDays.get(i); // Initialize selected day
                break;
            }
        }

        sharedPreferences = mContext.getSharedPreferences("ViewTherapistPreferences", Context.MODE_PRIVATE); // Initialize SharedPreferences

        String therapistId = sharedPreferences.getString("ViewProfiletherapist_id", "");
        fetchTherapistAppointments(therapistId);
    }

    private void setCalendarSelectedInBookAppointment() {
        bookAppointment.setIsCalendarSelected(true);
    }

    private void setDateInputLayoutinBookAppointment(String error) {
        bookAppointment.setDateInputLayout(error);
    }


    private void populateDays() {
        // Clear previous days
        mDays.clear();

        // Get the current month and year
        int currentMonth = mCalendar.get(Calendar.MONTH);
        int currentYear = mCalendar.get(Calendar.YEAR);

        // Set calendar to the first day of the current month
        mCalendar.set(Calendar.DAY_OF_MONTH, 1);

        // Find the day of the week for the first day of the month
        int firstDayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK);

        // Get the actual first day of the week for the current month
        int firstDayOfMonth = mCalendar.getFirstDayOfWeek();

        // Calculate the offset for the first day of the month
        int offset = (firstDayOfWeek < firstDayOfMonth) ?
                firstDayOfWeek + 7 - firstDayOfMonth :
                firstDayOfWeek - firstDayOfMonth;

        // Add empty spaces for preceding days
        for (int i = 0; i < offset; i++) {
            mDays.add("");
        }

        // Add days to the list while the month is still the current month
        while (mCalendar.get(Calendar.MONTH) == currentMonth && mCalendar.get(Calendar.YEAR) == currentYear) {
            mDays.add(String.valueOf(mCalendar.get(Calendar.DAY_OF_MONTH)));
            mCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Reset calendar to the last day of the previous month
        mCalendar.add(Calendar.DAY_OF_MONTH, -1);
    }


    @Override
    public int getCount() {
        return mDays.size();
    }

    @Override
    public Object getItem(int position) {
        return mDays.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.calendar_day_item, parent, false);
        }

        TextView dayTextView = convertView.findViewById(R.id.dayTextView);
        final String day = mDays.get(position);

        // Check if day is empty
        if (!day.isEmpty()) {
            int dayOfMonth = Integer.parseInt(day);
            Calendar todayCalendar = Calendar.getInstance();
            int today = todayCalendar.get(Calendar.DAY_OF_MONTH);
            int currentMonth = todayCalendar.get(Calendar.MONTH);
            int currentYear = todayCalendar.get(Calendar.YEAR);

            if (mCalendar.get(Calendar.MONTH) < currentMonth || mCalendar.get(Calendar.YEAR) < currentYear ||
                    (mCalendar.get(Calendar.MONTH) == currentMonth && dayOfMonth <= today)) {
                // Past date or current date
                dayTextView.setTextColor(ContextCompat.getColor(mContext, R.color.mint_green));
                dayTextView.setBackground(null);
                dayTextView.setOnClickListener(null); // Disable click listener
            } else {
                // Future date
                dayTextView.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                dayTextView.setBackground(null); // Clear background

                if (selectedPosition == position) {
                    // Set background for selected date
                    Drawable circleDrawable = ContextCompat.getDrawable(mContext, R.drawable.baseline_circle_24_green);
                    dayTextView.setBackground(circleDrawable);
                    dayTextView.setTextColor(ContextCompat.getColor(mContext, android.R.color.white));
                    getDayOfWeek(position);
                    setSelectedDay(selectedDay);
                    setCheck(1);
                    setCalendarSelectedInBookAppointment();
                    saveSelectedDateToSharedPreferences(selectedDay, day);
                }

                // Check if the day is in the list of available days
                if (!mDaysList.contains(getDayOfWeek(position))) {
                    // If not available, disable click listener and set text color
                    dayTextView.setOnClickListener(null);
                    dayTextView.setTextColor(ContextCompat.getColor(mContext, R.color.mint_green)); // or any other color for non-working days
                } else {
                    dayTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int previousSelectedPosition = selectedPosition;
                            selectedPosition = position;

                            // Set background for selected date
                            Drawable circleDrawable = ContextCompat.getDrawable(mContext, R.drawable.baseline_circle_24_green);
                            dayTextView.setBackground(circleDrawable);
                            dayTextView.setTextColor(ContextCompat.getColor(mContext, android.R.color.white));
                            setCalendarSelectedInBookAppointment();
                            setDateInputLayoutinBookAppointment("");

                            // Clear background from previously selected date
                            if (previousSelectedPosition != -1 && previousSelectedPosition != selectedPosition) {
                                notifyDataSetChanged();
                            }

                            selectedDay = getDayOfWeek(position);


                            setCheck(1);
                            AvailabilityAdapter adapter = new AvailabilityAdapter(parent.getContext(),bookAppointment);
                            adapter.setmSelectedDay(selectedDay);

                            List<String> selectedDayTimeSlots = new ArrayList<>();
                            String searchDay = "Day: " + selectedDay + ",";
                            for (String timeSlot : availableTime) {
                                if (timeSlot.contains(searchDay)) {
                                    selectedDayTimeSlots.add(timeSlot);
                                    Log.d("123check", timeSlot);
                                }
                            }

                            adapter.setmTimeSlots(selectedDayTimeSlots);
                            adapter.populateTable(mAvailabilityTableLayout);

                            // Save selected day and date to SharedPreferences
                            saveSelectedDateToSharedPreferences(selectedDay, day);
                        }
                    });
                }
            }

            dayTextView.setText(day);
        } else {
            dayTextView.setText("");
            dayTextView.setBackground(null); // Reset background
            dayTextView.setOnClickListener(null); // Disable click listener
        }

        return convertView;
    }

    private void saveSelectedDateToSharedPreferences(String selectedDayOfWeek, String selectedDayOfMonth) {
        // Format the date in the specified format: "Wed, 12th April 2024"
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMMM yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(selectedDayOfMonth));
        String formattedDate = dateFormat.format(calendar.getTime());

        // Save formatted date to SharedPreferences
        SharedPreferences.Editor editor = mContext.getSharedPreferences("SelectedDay&Date", Context.MODE_PRIVATE).edit();
        editor.putString("SelectedDay&Date", formattedDate);
        editor.apply();
    }


    private String getDayOfWeek(int position) {
        // Calculate the day of the week based on the position
        int dayOfWeek = (position + mCalendar.getFirstDayOfWeek() - 1) % 7; // adjust position to start from first day of the week
        // Assuming the calendar starts from Sunday, so adding 1 to dayOfWeek
        String[] daysOfWeek = new DateFormatSymbols().getShortWeekdays();
        return daysOfWeek[dayOfWeek + 1];
    }



    public String getCurrentMonth() {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        return monthFormat.format(mCalendar.getTime());
    }

    public String getSelectedDay() {
        switch(selectedDay) {
            case "1":
                return "Mon";
            case "2":
                return "Tue";
            case "3":
                return "Wed";
            case "4":
                return "Thu";
            case "5":
                return "Fri";
            case "6":
                return "Sat";
            case "7":
                return "Sun";
            default:
                return "";
        }
    }
    private void fetchTherapistAppointments(String therapistId) {
        PatientAPI patientAPI = RetrofitClientInstance.getRetrofitInstance().create(PatientAPI.class);
        Call<List<Appointment>> call = patientAPI.getTherapistAppointments(therapistId);
        call.enqueue(new Callback<List<Appointment>>() {
            @Override
            public void onResponse(Call<List<Appointment>> call, Response<List<Appointment>> response) {
                if (response.isSuccessful()) {
                    List<Appointment> appointments = response.body();


                } else {
                    Toast.makeText(mContext, "Failed to fetch therapist appointments", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Appointment>> call, Throwable t) {
                Toast.makeText(mContext, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}