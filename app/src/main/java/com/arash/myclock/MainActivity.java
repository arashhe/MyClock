package com.arash.myclock;


import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.widget.TimePicker;

public class MainActivity extends AppCompatActivity {

    private TextView clockTextView;
    private EditText setTextVew;
    private Handler handler = new Handler();
    private long difference;
    private long timeToShowInClockTextView;
    private Button setButton,localTime;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
    private int currentMinute,currentHour;
    private static final String CLOCK_TIME_PREF = "CLOCK_TIME_PREF";
    private static final String CLOCK_TIME = "CLOCK_TIME";
    private static final String CURRENT_TIME = "CURRENT_TIME";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clockTextView = findViewById(R.id.txt_clock);
        setButton = findViewById(R.id.btn_set);
        setTextVew = findViewById(R.id.txt_set);
        localTime = findViewById(R.id.btn_local_time);


        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time =String.valueOf(setTextVew.getText());
                setClock(time);
            }
        });
        localTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = formatTimeFromMillisecondToString(makeMillisShort(nowTime()));
                setClock(time);
            }
        });
        setTextVew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar calendar = Calendar.getInstance();
                currentHour = calendar.get(Calendar.HOUR);
                currentMinute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {

                        String hourString = String.valueOf(hourOfDay);
                        String minuteString = String.valueOf(minutes);
                        if(hourString.equals("0"))
                            hourString="00";
                        if(minuteString.equals("0"))
                            minuteString="00";
                        setTextVew.setText(hourString + ":" + minuteString + ":00");

                    }

                }, 0, 0, false);

                timePickerDialog.show();
            }
        });


        handler.postDelayed(updateTime, 1000);

    }

    @Override
    protected void onResume() {
        super.onResume();


        SharedPreferences sharedPreferences = getSharedPreferences(CLOCK_TIME_PREF, MODE_PRIVATE);
        long myClockTime = sharedPreferences.getLong(CLOCK_TIME, 0);

        myClockTime= timeAtToday12AM()+myClockTime;
        long lastCurrentTime = sharedPreferences.getLong(CURRENT_TIME, 0);
        difference = nowTime()-lastCurrentTime;
        timeToShowInClockTextView = myClockTime+difference;
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences sharedPreferences = getSharedPreferences(CLOCK_TIME_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        long myClockTime = formatTimeFromStringToMillisecond(String.valueOf(clockTextView.getText()));

        myClockTime= timeAtToday0330004AM()+myClockTime;
        myClockTime = myClockTime - timeAtToday12AM();
        editor.putLong(CLOCK_TIME, myClockTime);
        editor.apply();
        editor.commit();

        editor.putLong(CURRENT_TIME, nowTime());
        editor.apply();
        editor.commit();
    }

    private Runnable updateTime = new Runnable() {
        @Override
        public void run() {
            timeToShowInClockTextView = plusOneSecond(timeToShowInClockTextView);
            setTimeText(timeToShowInClockTextView);

            handler.postDelayed(this, 1000);
        }
    };

    private void setClock(String time){
        if (time.equals(""))
            time="08:00:00";
        setTimeToShowInClockTextView(formatTimeFromStringToMillisecond(time));
    }

    private void setTimeToShowInClockTextView (long time){

        timeToShowInClockTextView = time;
    }

    private long plusOneSecond(long time) {
        return time+=1000;
    }

    private void setTimeText (long time){

        clockTextView.setText(formatTimeFromMillisecondToString(time));

    }

    private long nowTime(){
        long currentTime = System.currentTimeMillis();
        return currentTime;
    }

    private long timeAtToday0330004AM(){
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 3);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long timeAt12AM = calendar.getTimeInMillis();

        return timeAt12AM;
    }
    private long timeAtToday12AM(){
        Calendar calendar = Calendar.getInstance();


        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long timeAt12AM = calendar.getTimeInMillis();

        return timeAt12AM;
    }

    private String formatTimeFromMillisecondToString(long time) {
        long shortedTime = makeMillisShort(time);
        String stringFormattedTimeFromMilliSecond = simpleDateFormat.format(new Date(shortedTime));
        return stringFormattedTimeFromMilliSecond;
    }

    private Long formatTimeFromStringToMillisecond (String time){
        long parsedMilliSecond;
        try {
            Date date = simpleDateFormat .parse(time);
//            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            parsedMilliSecond = date.getTime();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return parsedMilliSecond;
    }
    private long makeMillisShort (long timeInMillis){

        String stringFormattedTimeFromMilliSecond = simpleDateFormat.format(new Date(timeInMillis));

        long parsedMilliSecond;
        try {
            Date date = simpleDateFormat .parse(stringFormattedTimeFromMilliSecond);
            parsedMilliSecond = date.getTime();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return parsedMilliSecond;
    }
}
