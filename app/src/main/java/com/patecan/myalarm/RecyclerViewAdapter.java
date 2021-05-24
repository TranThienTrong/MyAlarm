package com.patecan.myalarm;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.patecan.myalarm.Data.AlarmDatabaseHandler;
import com.patecan.myalarm.Model.Alarm;
import com.patecan.myalarm.View.AlarmActivity;
import com.patecan.myalarm.View.MainActivity;

import java.util.Calendar;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;

/**
 * @author Tran Thien Trong - FX02425
 * @version 1.0
 * @since 2020-11-2
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static final int TURN_ON = 1;
    private static final int TURN_OFF = 0;

    private MainActivity mContext;
    private List<Alarm> mAlarmList;

    /*
     * Get References for Context, Alarm List Thought Constructor
     */
    public RecyclerViewAdapter(MainActivity context, List<Alarm> listAlarm) {
        this.mContext = context;
        this.mAlarmList = listAlarm;
    }


    /*
     * Create View Holder From item_alarm layout
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_alarm, viewGroup, false);
        return new ViewHolder(view);
    }

    /**
     * Binding Values For ViewHolders Attributes each Time Adapter Create New ViewHolder
     *
     * @param viewHolder: ViewHolder Object
     * @param position:   ViewHolder Object Position That it will create
     */
    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewAdapter.ViewHolder viewHolder, int position) {
        final Alarm alarm = mAlarmList.get(position);

        /* Clarify which day time, AM or PM */
        int hour = alarm.getHour();
        if (hour > 12) {
            hour -= 12;
            viewHolder.txtDayTime.setText("pm");
        }

        /* The time have formetted hh:mm */
        String time = String.format("%02d:%02d", hour, alarm.getMinute());
        viewHolder.txtName.setText(alarm.getName());
        viewHolder.txtTime.setText(time);

        /*
         * Set Alarm On if Toggle is Checked
         * Otherwise turn Alarm Off
         */
        viewHolder.toggleTime.setChecked(alarm.getState() == 1);
        viewHolder.toggleTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    setAlarm(alarm, TURN_ON);
                } else {
                    // The toggle is disabled
                    setAlarm(alarm, TURN_OFF);
                }
            }
        });
    }

    /**
     * @return Number of Alarms In AlarmList
     */
    @Override
    public int getItemCount() {
        return mAlarmList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private static final String STATE = "state";

        TextView txtName;
        TextView txtTime;
        TextView txtDayTime;
        ToggleButton toggleTime;
        Button imageButtonEditAlarm;
        Button imgButtonRemoveAlarm;
        AlarmDatabaseHandler alarmDatabaseHandler;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.item_alarm_tvName);
            txtTime = (TextView) itemView.findViewById(R.id.item_alarm_tvTime);
            txtDayTime = (TextView) itemView.findViewById(R.id.item_alarm_tvDayTime);
            toggleTime = (ToggleButton) itemView.findViewById(R.id.item_alarm_toggleBtn);
            imageButtonEditAlarm = (Button) itemView.findViewById(R.id.item_alarm_btnEdit);
            imgButtonRemoveAlarm = (Button) itemView.findViewById(R.id.item_alarm_btnDelete);
            alarmDatabaseHandler = new AlarmDatabaseHandler(mContext);

            /* Set Function For Each Button In ViewHolder */
            imageButtonEditAlarm.setOnClickListener(this);
            imgButtonRemoveAlarm.setOnClickListener(this);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    moveToAlarm(mAlarmList.get(getAdapterPosition()));
                }
            });
        }

        @Override
        public void onClick(View view) {
            Alarm alarm = mAlarmList.get(getAdapterPosition());
            switch (view.getId()) {
                case R.id.item_alarm_btnEdit:
                    updateAlarmPopup(alarm);
                    break;
                case R.id.item_alarm_btnDelete:
                    removeAlarm(alarm);
                    break;
            }
        }


        private void moveToAlarm(Alarm alarm) {
            Intent intent = new Intent(mContext, AlarmActivity.class);

            intent.putExtra("isEdit", alarm.getId());
            intent.putExtra("ten", alarm.getName());
            intent.putExtra("gio", alarm.getHour());
            intent.putExtra("phut", alarm.getMinute());

            mContext.startActivity(intent);
        }


        private void updateAlarmPopup(final Alarm alarm) {
            AlertDialog.Builder aleartDialogBuilder = new AlertDialog.Builder(mContext);
            View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_update_alarm, null);

            final AlertDialog dialog;
            final TimePicker pickTime = dialogView.findViewById(R.id.popup_update_timePicker);
            final EditText edtName = dialogView.findViewById(R.id.popup_update_edtName);
            Button btnBack = dialogView.findViewById(R.id.popup_update_btnCancel);
            Button btnAddAlarm = dialogView.findViewById(R.id.popup_update_btnAddAlarm);


            // Get Previous Value For Alarm Attribute
            btnAddAlarm.setText(R.string.dialog_update_editText_hint);
            pickTime.setCurrentHour(alarm.getHour());
            pickTime.setCurrentMinute(alarm.getMinute());
            edtName.setText(alarm.getName());

            // Popup the Update Dialog
            aleartDialogBuilder.setView(dialogView);
            dialog = aleartDialogBuilder.create();
            dialog.show();


            btnAddAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlarmDatabaseHandler databaseHandler = new AlarmDatabaseHandler(mContext);

                    // Set New Value For Alarm Attribute
                    alarm.setName(edtName.getText().toString());
                    alarm.setHour(pickTime.getCurrentHour());
                    alarm.setMinute(pickTime.getCurrentMinute());
                    alarm.setState(1);

                    // Update New Value For Alarm In Database
                    databaseHandler.updateAlarm(alarm.getName(), alarm.getHour(), alarm.getMinute(), 1, alarm.getId());
                    databaseHandler.close();

                    // Turn Alarm On
                    setAlarm(alarm, TURN_ON);

                    notifyItemChanged(getAdapterPosition());
                    dialog.dismiss();
                }
            });

            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }


        private void removeAlarm(final Alarm alarm) {
            AlertDialog.Builder aleartDialogBuilder = new AlertDialog.Builder(mContext);
            View confirmPopup = LayoutInflater.from(mContext).inflate(R.layout.dialog_delete_confirmation, null);
            aleartDialogBuilder.setView(confirmPopup);

            Button btnConfirm = confirmPopup.findViewById(R.id.popup_delete_btnYes);
            Button btnCancel = confirmPopup.findViewById(R.id.popup_delete_btnCancel);

            // Popup the Delete Dialog
            final AlertDialog dialog = aleartDialogBuilder.create();
            dialog.show();

            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Delete This Alarm From Database
                    alarmDatabaseHandler.deleteAlarm(alarm.getId());

                    // Since this Alarm still in List, We must Remove It
                    mAlarmList.remove(mAlarmList.get(mAlarmList.indexOf(alarm)));
                    notifyItemRemoved(getAdapterPosition());

                    // Turn Off Alarm, Even If Alarm Are Firing Off
                    setAlarm(alarm, TURN_OFF);
                    dialog.dismiss();
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
    }


    private void setAlarm(Alarm alarm, int state) {

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
        Intent intentAlarm = new Intent(mContext, AlarmReceiver.class);

        if (state == 1) {
            /* First we must update Alarm In Database inorder to change its State */
            AlarmDatabaseHandler database = new AlarmDatabaseHandler(mContext);
            database.updateAlarm(alarm.getName(), alarm.getHour(), alarm.getMinute(), state, alarm.getId());
            database.close();

            //This Pending intent has Role to turn On The Alarm
            intentAlarm.putExtra("state", 1);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    mContext,
                    alarm.getId(),
                    intentAlarm,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

            Calendar calendar = Calendar.getInstance();
            long currentTime = calendar.getTimeInMillis();
            calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
            calendar.set(Calendar.MINUTE, alarm.getMinute());
            calendar.set(Calendar.SECOND, 0);
            if (calendar.getTimeInMillis() <= currentTime) {
                calendar.add(Calendar.DATE, 1);
            }

            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        } else if (state == 0) {

            /* First we must update Alarm In Database inorder to change its State */
            AlarmDatabaseHandler database = new AlarmDatabaseHandler(mContext);
            database.updateAlarm(alarm.getName(), alarm.getHour(), alarm.getMinute(), state, alarm.getId());
            database.close();

            //This Pending intent has Role to turn Off The Alarm
            intentAlarm.putExtra("state", 0);

            /*
             * Base On ID of Alarm
             * We can retrieve the same Pending Intent which make Alarm Fired Off
             */
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    mContext,
                    alarm.getId(),
                    intentAlarm,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

            // Cancel The PendingIntent Operation
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();

            //Only send Broadcast If Alarm Sound Is Playing
            long currentTime = Calendar.getInstance().getTimeInMillis();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
            calendar.set(Calendar.MINUTE, alarm.getMinute());
            calendar.set(Calendar.SECOND, 0);

            long checkTime = currentTime - calendar.getTimeInMillis();
            /*
             * This make sure the BroadCast only send Under 1 minutes
             * which is the length of Alarm Sound
             */
            if (checkTime < 60000) {
                mContext.sendBroadcast(intentAlarm);
            }
        }

    }
}
