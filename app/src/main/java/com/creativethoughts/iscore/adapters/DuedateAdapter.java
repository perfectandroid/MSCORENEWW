package com.creativethoughts.iscore.adapters;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.creativethoughts.iscore.R;
import com.creativethoughts.iscore.utility.CommonUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DuedateAdapter extends RecyclerView.Adapter {

    JSONArray jsonArray;
    JSONObject jsonObject=null;
    Context context;
    EditText etdate;
    EditText ettime;
    String title;
    private int mYear, mMonth, mDay, mHour, mMinute;
    int yr, month, day, hr, min;
    Date newDate, datecurrent;

    public DuedateAdapter(Context context, JSONArray jsonArray, String title) {
        this.context=context;
        this.jsonArray=jsonArray;
        this.title=title;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.activity_duedate, parent, false);
        vh = new MainViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        try {
            jsonObject=jsonArray.getJSONObject(position);
            if (holder instanceof MainViewHolder) {
                if(position %2 == 1){
                    holder.itemView.setBackgroundColor(Color.parseColor("#CED1D1"));
                }else{
                    holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
                }

                if(jsonObject.getString("Type").equals("Y")){
                    ((MainViewHolder) holder).tvSLNO.setTextColor(Color.parseColor("#ff0000"));
                    ((MainViewHolder) holder).txtv_duedate.setTextColor(Color.parseColor("#ff0000"));
                    ((MainViewHolder) holder).txtv_accno.setTextColor(Color.parseColor("#ff0000"));
                    ((MainViewHolder) holder).txtv_amount.setTextColor(Color.parseColor("#ff0000"));
                }else{}

                ((MainViewHolder) holder).txtv_duedate.setText(jsonObject.getString("DueDate"));
                ((MainViewHolder)holder).tvSLNO.setText(""+(position+1));
                ((MainViewHolder)holder).txtv_accno.setText(jsonObject.getString("AccountNo")+"\n("+jsonObject.getString("AccountType")+")");
                ((MainViewHolder)holder).txtv_amount.setText("₹ "+ CommonUtilities.getDecimelFormate(Double.parseDouble(jsonObject.getString("Amount"))));
            }
            ((MainViewHolder)holder).addReminder.setTag(position);
            ((MainViewHolder)holder).addReminder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        jsonObject=jsonArray.getJSONObject(position);
                        setReminder(jsonObject.getString("DueDate"), jsonObject.getString("AccountType"),  jsonObject.getString("AccountBranchName"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException /*| ParseException*/ e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    private class MainViewHolder extends RecyclerView.ViewHolder {
        TextView txtv_duedate,txtv_amount,txtv_accno,tvSLNO;
        ImageView addReminder;
        public MainViewHolder(View v) {
            super(v);
            txtv_duedate=v.findViewById(R.id.txtv_duedate);
            txtv_amount=v.findViewById(R.id.txtv_amount);
            txtv_accno=v.findViewById(R.id.txtv_accno);
            tvSLNO=v.findViewById(R.id.tvSLNO);
            addReminder=v.findViewById(R.id.addReminder);
        }
    }




    private void setReminder(String dueDate, String accountType, String branchName ) {
        try {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
            LayoutInflater inflater1 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater1.inflate(R.layout.reminder_popup, null);
            LinearLayout ll_ok = (LinearLayout) layout.findViewById(R.id.ll_ok);
            LinearLayout ll_cancel = (LinearLayout) layout.findViewById(R.id.ll_cancel);
            etdate = (EditText) layout.findViewById(R.id.etdate);
            ettime = (EditText) layout.findViewById(R.id.ettime);
            EditText etdis = (EditText) layout.findViewById(R.id.etdis);

            etdate.setKeyListener(null);
            ettime.setKeyListener(null);


            builder.setView(layout);
            final android.app.AlertDialog alertDialog = builder.create();
            etdis.setText("Your "+accountType.toLowerCase()+" account in "+context.getString(R.string.app_name)+
                    "("+branchName.toLowerCase()+") will due on "+dueDate+". Please do the needful actions.");
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm a");
            SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm");


            String currentdate=sdf.format(c.getTime());

            try {
                datecurrent = sdf.parse(currentdate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //Date datecurrent = c.getTime();
            Date datedue = sdf.parse(dueDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(datedue);
            calendar.add(Calendar.DAY_OF_YEAR, -3);
            newDate = calendar.getTime();

         /*   String date = sdf.format(newDate);
            yr= calendar.get(Calendar.YEAR);
            month= calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);

            //etdate.setText(sdf.format(c.getTime()));
            etdate.setText(date);*/


            if(newDate.after(datecurrent)){
                String date = sdf.format(newDate);
                yr= calendar.get(Calendar.YEAR);
                month= calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                etdate.setText(date);
                newDate=sdf.parse(date);
            }
            if(newDate.equals(datecurrent)){
                String date = sdf.format(newDate);
                yr= calendar.get(Calendar.YEAR);
                month= calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                etdate.setText(date);
                newDate=sdf.parse(date);
            }
            if(newDate.before(datecurrent)){
                yr= c.get(Calendar.YEAR);
                month= c.get(Calendar.MONTH);
                month=month+1;
                day = c.get(Calendar.DAY_OF_MONTH);
                etdate.setText(sdf.format(c.getTime()));
                newDate=sdf.parse(sdf.format(c.getTime()));
            }


            ettime.setText(sdf1.format(c.getTime()));

            String s = sdf2.format(c.getTime());
            String[] split = s.split(":");
            String strhr = split[0];
            String strmin = split[1];
            hr= Integer.parseInt(strhr);
            min= Integer.parseInt(strmin);

        /*    yr= c.get(Calendar.YEAR);
            month= c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);*/


            ettime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  timeSelector();
                }
            });
            etdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dateSelector(dueDate);
                }
            });
            ll_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });
            ll_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  //  alertDialog.dismiss();
                    if(newDate.after(datecurrent)){
                        addEvent(yr, month, day, hr, min, etdis.getText().toString(),title+" Due Notification");
                        alertDialog.dismiss();
                    }
                    if(newDate.equals(datecurrent)){

                        Date date = new Date() ;
                        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm") ;
                        dateFormat.format(date);
                        System.out.println(dateFormat.format(date));
                        try {
                            if(dateFormat.parse(dateFormat.format(date)).after(dateFormat.parse(hr+":"+min)))
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("Set time greater than current time.")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }else{

                                addEvent(yr, month, day, hr, min, etdis.getText().toString(),title+" Due Notification");
                                alertDialog.dismiss();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    if(newDate.before(datecurrent)){
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Set date greater than or equal to current date.")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                    //addEvent(yr, month, day, hr, min, etdis.getText().toString(),title+" Due Notification");
                }
            });
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void timeSelector() {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                      String strDate = String.format("%02d:%02d %s", hourOfDay == 0 ? 12 : hourOfDay,
                                minute, hourOfDay < 12 ? "am" : "pm");
                        ettime.setText(strDate);
                        hr=hourOfDay;
                        min=minute;
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    public void dateSelector(String strdate) {
        try {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        yr= year;
                        month= monthOfYear ;
                        day = dayOfMonth;
                        etdate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        try {
                            newDate = sdf.parse(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, mYear, mMonth, mDay);
/*
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");*/
        Date parse = null;
        parse = sdf.parse(strdate);
        Calendar c1 = Calendar.getInstance();
        c1.setTime(parse);
      //  datePickerDialog.getDatePicker().setMaxDate(c1.getTimeInMillis());
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        datePickerDialog.show();

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void addEvent(int iyr, int imnth, int iday, int ihour, int imin, String descriptn, String Title ) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_CALENDAR}, 1);
        }

        ContentResolver cr = context.getContentResolver();
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2019, 11-1, 28, 9, 30);
        Calendar endTime = Calendar.getInstance();
        endTime.set(iyr, imnth, iday, ihour, imin);
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, endTime.getTimeInMillis());
        values.put(CalendarContract.Events.DTEND, endTime.getTimeInMillis());
        values.put(CalendarContract.Events.TITLE, Title);
        values.put(CalendarContract.Events.DESCRIPTION, "[ "+descriptn+" ]");
        values.put(CalendarContract.Events.CALENDAR_ID, 1);
        TimeZone tz = TimeZone.getDefault();
        values.put(CalendarContract.Events.EVENT_TIMEZONE, tz.getID());
        values.put(CalendarContract.Events.EVENT_LOCATION, "India");

        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        ContentValues reminders = new ContentValues();
        reminders.put(CalendarContract.Reminders.EVENT_ID, uri.getLastPathSegment());
        reminders.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        reminders.put(CalendarContract.Reminders.MINUTES, 10);
        cr.insert(CalendarContract.Reminders.CONTENT_URI, reminders);


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Due date reminder set on calender successfully.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();



    }
}
