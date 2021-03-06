package com.chaika.fragmentos.adaptadores;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chaika.R;
import com.chaika.application.ApplicationConfig;
import com.chaika.application.ChaikaApplication;
import com.chaika.estructuraDatos.Database.AnimeData;
import com.chaika.estructuraDatos.malAppInfo.Anime;
import com.chaika.interfaces.RecyclerViewClickListener;
import com.orhanobut.logger.Logger;

import java.util.List;

import io.reactivex.annotations.Nullable;

/**
 * Adaptador para RecyclerView en el cual declaramos los componentes que se van a mostrar en cada una de los items que componen la lista creada.
 *
 * Created by Gato on 14/05/2017.
 */
// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class RecyclerViewAdaptador extends
        RecyclerView.Adapter<RecyclerViewAdaptador.ViewHolder> {

    // Store a member variable for the contacts
    private List<AnimeData> animeList;// = new ArrayList<>();
    // Store the context for easy access
    private Context mContext;

    RecyclerViewClickListener listenerAdap;


    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView title_series;
        public TextView studio_series;
        public TextView vistos_series;
        public TextView season_series;
        public TextView source_series;
        public TextView my_status_series;

        public ImageView cover_series;


        RecyclerViewClickListener listener;


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView, RecyclerViewClickListener listener) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            this.listener = listener;
            itemView.setOnClickListener(this);

            title_series = (TextView) itemView.findViewById(R.id.title_series);
            studio_series = (TextView) itemView.findViewById(R.id.studio_series);
            vistos_series = (TextView) itemView.findViewById(R.id.episodios_vistos);
            season_series = (TextView) itemView.findViewById(R.id.season_series);
            source_series = (TextView) itemView.findViewById(R.id.source_series);
            my_status_series = (TextView) itemView.findViewById(R.id.my_status_series);

            cover_series =  (ImageView) itemView.findViewById(R.id.cover_serie);

        }

        @Override
        public void onClick(View v) {

            listener.onClick(v,getAdapterPosition());
        }
    }//fin viewHolder


    // Pass in the contact array into the constructor
    public RecyclerViewAdaptador(Context context, List<AnimeData> animes,RecyclerViewClickListener listenerAdap) {
        animeList = animes;
        mContext = context;
        this.listenerAdap = listenerAdap;
    }
    public RecyclerViewAdaptador() {

    }

    // Easy access to the context object in the recycler_view
    private Context getContext() {
        return mContext;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public RecyclerViewAdaptador.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.serie_item_layout, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView,listenerAdap);

        return viewHolder;
    }

    // poblar de datos cada item
    @Override
    public void onBindViewHolder(RecyclerViewAdaptador.ViewHolder viewHolder, int position) {
        // modelo
        AnimeData animeData = animeList.get(position);

        // Set item views based on your views and data model
        TextView title_series = viewHolder.title_series;
        title_series.setText(animeData.getAnimeMalinfo().getSeries_title());
        TextView studio_series = viewHolder.studio_series;
        studio_series.setText("");//falta studio
        TextView vistos_series = viewHolder.vistos_series;
        vistos_series.setText(getContext().getString(R.string.my_status_series_episodes,
                animeData.getAnimeMalinfo().getMy_watched_episodes(), animeData.getAnimeMalinfo().getSeries_episodes()));
        TextView season_series = viewHolder.season_series;
        season_series.setText(animeData.getSeason());
        TextView source_series = viewHolder.source_series;
        source_series.setText(animeData.getSource());
        TextView my_status_series = viewHolder.my_status_series;
        //1/watching, 2/completed, 3/onhold, 4/dropped, 6/plantowatch
        String [] estados = {getContext().getString(R.string.my_status_series_watching),
                                getContext().getString(R.string.my_status_series_completed)
                                ,getContext().getString(R.string.my_status_series_onhold)
                                ,getContext().getString(R.string.my_status_series_dropped)
                                ,getContext().getString(R.string.app_name)
                                ,getContext().getString(R.string.my_status_series_plantowatch)};
        int statusValue = animeData.getAnimeMalinfo().getMy_status() - 1;
        try {
            my_status_series.setText(statusValue < 0 || statusValue > 5 ? estados[4]:estados[statusValue]);
        }catch (Exception e){
            Logger.e(e.getMessage());
            Logger.e(String.valueOf(animeData.getAnimeMalinfo().getSeries_animedb_id() + " status: " + statusValue));
        }

        ImageView cover_series = viewHolder.cover_series;
        //pulir glide
        Glide.with(ApplicationConfig.getInstance().getContext()).load(animeData.getAnimeMalinfo().getSeries_image()).into(cover_series);



    }

    @Override
    public void onBindViewHolder(RecyclerViewAdaptador.ViewHolder holder, int position, List<Object> payloads) {

        if(!payloads.isEmpty()) {//se llama cuando se actualiza información en la pantalla de detalles
            if (payloads.get(0) instanceof Integer) {//temporal-mejorable

                Anime a = ChaikaApplication.get(ApplicationConfig.getInstance().getActivity()).component().getData().getMyAnimeById((Integer) payloads.get(0));
                AnimeData animeData = ChaikaApplication.get(ApplicationConfig.getInstance().getActivity()).component().getData().getAnimeById((Integer) payloads.get(0));

                Logger.d("onBindUpdate payload" + payloads.get(0));
                holder.vistos_series.setText(getContext().getString(R.string.my_status_series_episodes,
                        a.getMy_watched_episodes(),animeData.getAnimeMalinfo().getSeries_episodes()));

            }
        }else {
            super.onBindViewHolder(holder,position, payloads);
        }
    }

    // Cantidad de items en la lista
    @Override
    public int getItemCount() {
        return animeList.size();
    }

    public void setAnimeList(List<AnimeData> animeList){
        this.animeList = animeList;
    }
    public void setListMAL(@Nullable List<AnimeData> animeDataList) {
        if (animeDataList == null) {
            return;
        }
        animeList.clear();
        animeList.addAll(animeDataList);
        notifyDataSetChanged();
    }
    public void removeItemList(int position) {

        animeList.remove(position);
        notifyDataSetChanged();

    }

}//fin clase