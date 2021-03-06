package com.newfivefour.githubevents;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.newfivefour.githubevents.databinding.EventsListItemBinding;
import com.newfivefour.githubevents.databinding.EventsListViewBinding;
import com.newfivefour.githubevents.logique.AppState;

import java.util.ArrayList;

public class EventsListView extends FrameLayout {

  private final EventsListViewBinding bd;
  private ArrayList<AppState.Event> mEvents;

  public EventsListView(Context context) {
    this(context, null, 0);
  }

  public EventsListView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public EventsListView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    bd = DataBindingUtil.inflate(
            (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE),
            R.layout.events_list_view,
            this,
            true);
  }

  public void setEvents(ArrayList<AppState.Event> events) {
    mEvents = events;
    if(bd.recView.getAdapter()==null) {
      bd.recView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
      bd.recView.setAdapter(new EventsListAdapter(mEvents));
    } else {
      bd.recView.getAdapter().notifyDataSetChanged();
    }
  }

  private class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.ViewHolder> {

    public EventsListAdapter(ArrayList<AppState.Event> events) {
      mEvents = events;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      EventsListItemBinding eventsListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.events_list_item, parent, false);
      return new ViewHolder(eventsListItemBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
      holder.binding.eventlistitemNametextview.setText("");
      holder.binding.eventlistitemReponametextview.setText("");
      holder.binding.eventlistitemTimetextview.setText("");
      holder.binding.setEvent(mEvents.get(position));
    }

    @Override
    public int getItemCount() {
      return mEvents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
      private EventsListItemBinding binding;

      public ViewHolder(EventsListItemBinding itemView) {
        super(itemView.getRoot());
        binding = itemView;
      }
    }
  }
}
