package teamsylvanmatthew.memecenter.Adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mb3364.twitch.api.models.Channel;
import com.mb3364.twitch.api.models.Stream;

import java.util.ArrayList;

import teamsylvanmatthew.memecenter.R;


public class HomeStreamAdapter extends RecyclerView.Adapter<HomeStreamAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<Stream> streams;

    public HomeStreamAdapter(Context context, ArrayList<Stream> streams) {
        inflater = LayoutInflater.from(context);
        this.streams = streams;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public HomeStreamAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {

        View view = inflater.inflate(R.layout.stream_list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        Stream stream = streams.get(position);
        Channel channel = stream.getChannel();

        viewHolder.tv_name.setText("Name: " + channel.getName());
        viewHolder.tv_game.setText("Game: " + stream.getGame());
        viewHolder.tv_viewers.setText("Viewers: " + String.valueOf(stream.getViewers()));
        viewHolder.tv_title.setText("Title: " + channel.getStatus());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return streams.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView tv_name;
        TextView tv_game;
        TextView tv_viewers;
        TextView tv_title;

        public ViewHolder(View v) {
            super(v);
            tv_name = (TextView) v.findViewById(R.id.streamer_name);
            tv_game = (TextView) v.findViewById(R.id.stream_game);
            tv_viewers = (TextView) v.findViewById(R.id.stream_viewers);
            tv_title = (TextView) v.findViewById(R.id.stream_title);
        }
    }
}

