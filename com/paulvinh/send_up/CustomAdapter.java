package com.paulvinh.send_up;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import com.paulvinh.send_up.Tab1;

public class CustomAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

    public static ArrayList<ListItem> myList = new ArrayList<ListItem>();
    Context context;

    // on passe le context afin d'obtenir un LayoutInflater pour utiliser notre
    // row_layout.xml
    // on passe nos valeurs à l'adapter
    public CustomAdapter(Context context, ArrayList<ListItem> myList) {
        this.myList = myList;
        this.context = context;
    }

    // retourne le nombre d'objet présent dans notre liste
    @Override
    public int getCount() {
        return myList.size();
    }

    // retourne un élément de notre liste en fonction de sa position
    @Override
    public ListItem getItem(int position) {
        return myList.get(position);
    }

    // retourne l'id d'un élément de notre liste en fonction de sa position
    @Override
    public long getItemId(int position) {
        return myList.indexOf(getItem(position));
    }

    public void remove(ListItem position) { myList.remove(position); }

    // retourne la vue d'un élément de la liste
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder = null;

        // au premier appel ConvertView est null, on inflate notre layout
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = mInflater.inflate(R.layout.row_list, parent, false);

            // nous plaçons dans notre MyViewHolder les vues de notre layout
            mViewHolder = new MyViewHolder();
            mViewHolder.text = (TextView) convertView
                    .findViewById(R.id.histor);
            mViewHolder.imageView = (ImageView) convertView
                    .findViewById(R.id.imageView);
            mViewHolder.suppress = (ImageView) convertView
                    .findViewById(R.id.suppression);

            // nous attribuons comme tag notre MyViewHolder à convertView
            convertView.setTag(mViewHolder);
        } else {
            // convertView n'est pas null, nous récupérons notre objet MyViewHolder
            // et évitons ainsi de devoir retrouver les vues à chaque appel de getView
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        // nous récupérons l'item de la liste demandé par getView
        ListItem listItem = (ListItem) getItem(position);

        // nous pouvons attribuer à nos vues les valeurs de l'élément de la liste
        mViewHolder.text.setText(listItem.getSms());
        mViewHolder.imageView.setImageResource(listItem.getImageId());
        mViewHolder.suppress.setImageResource(listItem.getSupprId());

        // nous retournos la vue de l'item demandé
        return convertView;
    }

    // MyViewHolder va nous permettre de ne pas devoir rechercher
    // les vues à chaque appel de getView, nous gagnons ainsi en performance
    private static class MyViewHolder {
        TextView text;
        ImageView imageView;
        ImageView suppress;
    }

    // nous affichons un Toast à chaque clic sur un item de la liste
    // nous récupérons l'objet grâce à sa position
  @Override
   public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Tab1.array_message.remove((int) id);
            Tab1.array_contact.remove((int) id);
            Tab1.array_time.remove((int) id);
            Tab1.array_minute.remove((int) id);
            Tab1.array_heure.remove((int) id);
            Tab1.array_jour.remove((int) id);
            Tab1.array_mois.remove((int) id);
            Tab1.array_annee.remove((int) id);
        Tab1.contact.remove((int) id);
        Tab1.deja_envoi_ou_pas.remove((int) id);
    }
}