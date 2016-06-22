package project.suchita.com.nearbyreminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapterReminderActivity extends ArrayAdapter<ReminderList> {

    private ArrayList<ReminderList> reminderLists;
    private LayoutInflater layoutInflater;


    public CustomAdapterReminderActivity(Context context, int resource, ArrayList<ReminderList> reminderLists) {
        super(context, resource);
        this.reminderLists = reminderLists;
        this.layoutInflater = layoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return reminderLists.size();
    }

    @Override
    public ReminderList getItem(int position) {
        return reminderLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.reminder_list_contents, null);
            holder = new ViewHolder();
            holder.add_title = (TextView) convertView.findViewById(R.id.tvAddTitle);
            holder.reminder_note = (TextView) convertView.findViewById(R.id.tvReminderNote);
            holder.place_name = (TextView) convertView.findViewById(R.id.tvPLaceName);
            holder.address= (TextView) convertView.findViewById(R.id.tvAddress);
            convertView.setTag(holder);
        }else {
            holder =(ViewHolder)convertView.getTag();
        }

        ReminderList current = reminderLists.get(position);
        holder.add_title.setText(current.getAdd_title().toString());
        holder.reminder_note.setText(current.getReminder_note());
        holder.place_name.setText(current.getPlace_name().toString());
        holder.address.setText(current.getAddress().toString());
        return convertView;
    }
//


    static class ViewHolder {
        TextView add_title;
        TextView reminder_note;
        TextView place_name;
        TextView address;
    }
}

