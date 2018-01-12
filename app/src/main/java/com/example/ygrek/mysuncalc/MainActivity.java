package com.example.ygrek.mysuncalc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void onClickButtonCalc(View view){
        Calendar date = Calendar.getInstance();
        //Calendar date = new GregorianCalendar(1985,Calendar.FEBRUARY,17);
        TextView textViewDate = (TextView) findViewById(R.id.textViewDate);
        TextView textViewJulianDate = (TextView) findViewById(R.id.textViewJulianDate);
        textViewDate.setText("Date: " + date.getTime().toString());
        double JD = AstroCalc.getJD(date);
        textViewJulianDate.setText("JD: " + Double.valueOf(JD).toString());

        TextView textViewFragOfDay = (TextView) findViewById(R.id.textViewFragOfDay);
        double fragOfDay = AstroCalc.getFragmentaryOfDayByTime(date);
        textViewFragOfDay.setText("Fragment of Day: " + Double.valueOf(fragOfDay).toString());

        TextView textViewDayOfWeek = (TextView) findViewById(R.id.textViewDayOfWeek);
        int dayOfWeek = AstroCalc.getDayOfWeek(date);
        textViewDayOfWeek.setText("Day of week: " + dayOfWeek);

        TextView textViewFragmHourOfDay = (TextView) findViewById(R.id.textViewFragmHourOfDay);
        double fragmHourOfDay = AstroCalc.getFragmentaryHourOfDay(date);
        textViewFragmHourOfDay.setText("FragmHour of day: " + fragmHourOfDay);

        TextView textViewTimeFromFragmHour = (TextView) findViewById(R.id.textViewTimeFromFragmHour);
        Calendar time = AstroCalc.getTimaFromFragmentaryHours(fragmHourOfDay);
        textViewTimeFromFragmHour.setText("Time from fragm: " + time.get(Calendar.HOUR_OF_DAY) + ":" + time.get(Calendar.MINUTE) + ":" + time.get(Calendar.SECOND));

        TextView textViewUTDate = (TextView) findViewById(R.id.textViewUTDate);
        Calendar UTdate = AstroCalc.convertToUT(date, 2);
        textViewUTDate.setText("UT date: " + UTdate.getTime().toString());

        TextView textViewEasterDate = (TextView) findViewById(R.id.textViewEasterDate);
        Calendar easterDate = AstroCalc.getPravoslavEasterDate(date);
        textViewEasterDate.setText("Easter date: " + easterDate.get(Calendar.DAY_OF_MONTH) + " " + (easterDate.get(Calendar.MONTH)+1));

        EditText editTextLat = (EditText) findViewById(R.id.editTextLat);
        EditText editTextLng = (EditText) findViewById(R.id.editTextLng);
        double lat = Double.parseDouble(editTextLat.getText().toString());
        double lng = Double.parseDouble(editTextLng.getText().toString());

        AstroCalc.getSunPosition(lat, lng, date);
        double Azimuth = AstroCalc.SunAzimith * AstroCalc.Rad2Deg;
        double Altitude = AstroCalc.SunAltitude * AstroCalc.Rad2Deg;

        TextView textViewSunPos = (TextView) findViewById(R.id.textViewSunPos);
        textViewSunPos.setText("Sun: azi=" + Double.valueOf(Azimuth) + ", alt=" + Double.valueOf(Altitude));
    }

}
