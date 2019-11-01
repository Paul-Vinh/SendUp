package com.paulvinh.send_up;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import mail.Tab3;

public class Fichier extends ListActivity {
    public static String add = "";
    public static String pass = "";
    public static String destination = "";
    public static String sub = "";
    public static String body = "";

    /**
     * Représente le texte qui s'affiche quand la liste est vide
     */
    private TextView mEmpty = null;
    /**
     * La liste qui contient nos fichiers et répertoires
     */
    private ListView mList = null;
    /**
     * Notre Adapter personnalisé qui lie les fichiers à la liste
     */
    private FileAdapter mAdapter = null;

    /**
     * Représente le répertoire actuel
     */
    private File mCurrentFile = null;

    /**
     * Indique si l'utilisateur est à la racine ou pas
     * pour savoir s'il veut quitter
     */
    private boolean mCountdown = false;

    // chaîne de caractères qui stocke le chemin du fichier choisi
    public static String chemin;

    private float mPreviousX;
    public static final String MY_PREFS_NAME = "MyPrefsFile"; // SharedPreferences
    private ColorDrawable colorDrawable;
    private int color;
    private boolean nuit;
    private String coloris = "";

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explorateur);

        // Mode Nuit
        SharedPreferences pref = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        nuit = pref.getBoolean("nuit", false);

        if(nuit) {
            color = R.color.blanc;
            colorDrawable = new ColorDrawable(Color.parseColor("#080808"));
            this.findViewById(android.R.id.content).setBackgroundColor(getResources().getColor(R.color.noir));
        }

        else {
            // Couleur verte pour l'ActionBar
            color = R.color.noir;
            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            coloris = prefs.getString("couleur_actionbar", "0");
            if (coloris == "0")
                coloris = "#84BE58";
            colorDrawable = new ColorDrawable(Color.parseColor(coloris));
        }

        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setTitle("");
        actionBar.setDisplayUseLogoEnabled(false);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Tab3.code == 1) {
                    Bundle bundle2 = intent.getExtras();

                }
            }
        };

        // On récupère la ListView de notre activité
        mList = (ListView) getListView();

        // On vérifie que le répertoire externe est bien accessible
        if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            // S'il ne l'est pas, on affiche un message
            mEmpty = (TextView) mList.getEmptyView();
            mEmpty.setText("Vous ne pouvez pas accéder aux fichiers");
        } else {
            // S'il l'est...
            // On déclare qu'on veut un menu contextuel sur les éléments de la liste
            registerForContextMenu(mList);

            // On récupère la racine de la carte SD pour qu'elle soit
            mCurrentFile = Environment.getExternalStorageDirectory();

            // On change le titre de l'activité pour y mettre le chemin actuel
            setTitle(mCurrentFile.getAbsolutePath());

            // On récupère la liste des fichiers dans le répertoire actuel
            File[] fichiers = mCurrentFile.listFiles();

            // On transforme le tableau en une structure de données de taille variable
            ArrayList<File> liste = new ArrayList<File>();
            for(File f : fichiers)
                liste.add(f);

            mAdapter = new FileAdapter(this, android.R.layout.simple_list_item_1, liste) {

                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {

                            View view = super.getView(position, convertView, parent);
                            TextView text = (TextView) view.findViewById(android.R.id.text1);

                            text.setTextColor(getResources().getColor(color));
                            return view;
                        }
                    };
            // On ajoute l'adaptateur à la liste
            mList.setAdapter(mAdapter);
            // On trie la liste
            mAdapter.sort();

            // On ajoute un Listener sur les items de la liste
            mList.setOnItemClickListener(new OnItemClickListener() {

                // Que se passe-il en cas de cas de clic sur un élément de la liste ?
                public void onItemClick(AdapterView<?> adapter, View view,
                                        int position, long id) {
                    File fichier = mAdapter.getItem(position);
                    // Si le fichier est un répertoire...
                    if(fichier.isDirectory())
                        // On change de répertoire courant
                        updateDirectory(fichier);
                    else
                        // Sinon on lance l'irzm
                        seeItem(fichier);
                }
            });
        }
    }
    
         /* Méthodes pour le Swipe */

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        float x = e.getX();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = x - mPreviousX;

                if(dx < -50) {
                    Intent event = new Intent(Fichier.this, Event.class);
                    startActivity(event);
                    overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                }
                else if(dx > 50) {
                    Intent parametres = new Intent(Fichier.this, Parametres.class);
                    startActivity(parametres);
                    overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);
                }
        }

        mPreviousX = x;
        return true;
    }
    // ***************************************** //

    // Initialise le menu quand tu cliques sur la touche Menu ou sur la barre d'outils (ActionBar)
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.message).setIcon(R.mipmap.lettre_selec);

        if (!nuit) {
            switch (coloris) {
                case "#0099CC":
                    menu.findItem(R.id.calendrier).setIcon(R.mipmap.calendrier_bleu);
                    menu.findItem(R.id.shutdown).setIcon(R.mipmap.shut_bleu);
                    menu.findItem(R.id.options).setIcon(R.mipmap.options_bleu);
                    break;
                case "#CC0000":
                    menu.findItem(R.id.calendrier).setIcon(R.mipmap.calendrier_rouge);
                    menu.findItem(R.id.shutdown).setIcon(R.mipmap.shut_rouge);
                    menu.findItem(R.id.options).setIcon(R.mipmap.options_rouge);
                    break;
                case "#84BE58":
                    menu.findItem(R.id.calendrier).setIcon(R.mipmap.calendrier_vert);
                    menu.findItem(R.id.shutdown).setIcon(R.mipmap.shut_vert);
                    menu.findItem(R.id.options).setIcon(R.mipmap.options_vert);
                    break;
                default:
                    break;
            }
        }
        else {
            menu.findItem(R.id.calendrier).setIcon(R.mipmap.calendrier_blanc);
            menu.findItem(R.id.shutdown).setIcon(R.mipmap.shut_blanc);
            menu.findItem(R.id.options).setIcon(R.mipmap.options_blanc);
        }

        return true;
    }

    // Gère les clics sur les items du Menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.message:
                Intent sms = new Intent(this, MainActivity.class);
                startActivity(sms);
                overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);
                return true;
            case R.id.calendrier:
                Intent event = new Intent(this, Event.class);
                startActivity(event);
                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                return true;
            case R.id.shutdown:
                Intent eteindre = new Intent(this, Eteindre.class);
                startActivity(eteindre);
                overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);
                return true;
            case R.id.options:
                Intent options = new Intent(this, Parametres.class);
                startActivity(options);
                overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Utilisé pour visualiser un fichier
     * @param pFile le fichier à visualiser
     */
    private void seeItem(File pFile) {

        try {
            chemin = pFile.getAbsolutePath();
            if(Tab3.code == 1) {
                Tab3.code = 0;
                add = Tab3.use;
                pass = Tab3.passw;
                destination = Tab3.dest;
                sub = Tab3.obj;
                body = Tab3.mes;
                Intent intent = new Intent(Fichier.this, Tab3.class);
                startActivity(intent);
            }

            // Et s'il n'y a pas d'activité qui puisse gérer ce type de fichier
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Désolé, vous ne pouvez pas envoyer ce fichier !", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * Utilisé pour naviguer entre les répertoires
     * @param pFile le nouveau répertoire dans lequel aller
     */

    public void updateDirectory(File pFile) {
        // On change le titre de l'activité
        setTitle(pFile.getAbsolutePath());

        // L'utilisateur ne souhaite plus sortir de l'application
        mCountdown = false;

        // On change le repertoire actuel
        mCurrentFile = pFile;
        // On vide les répertoires actuels
        setEmpty();

        // On récupère la liste des fichiers du nouveau répertoire
        File[] fichiers = mCurrentFile.listFiles();

        // Si le répertoire n'est pas vide...
        if(fichiers != null)
            // On les ajoute à  l'adaptateur
            for(File f : fichiers)
                mAdapter.add(f);
        // Et on trie l'adaptateur
        mAdapter.sort();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Si on a appuyé sur le retour arrière
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            // On prend le parent du répertoire courant
            File parent = mCurrentFile.getParentFile();
            // S'il y a effectivement un parent
            if(parent != null)
                updateDirectory(parent);
            else {
                // Sinon, si c'est la première fois qu'on fait un retour arrière
                if(mCountdown != true) {
                    // On indique à l'utilisateur qu'appuyer dessus une seconde fois le fera sortir
                    Toast.makeText(this, "Vous êtes déjà à la racine !", Toast.LENGTH_SHORT).show();
                    mCountdown  = true;
                } else
                    // Si c'est la seconde fois on sort effectivement
                    finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * On enlève tous les éléments de la liste
     */

    public void setEmpty() {
        // Si l'adapteur n'est pas vide...
        if(!mAdapter.isEmpty())
            // Alors on le vide !
            mAdapter.clear();
    }

    /**
     * L'adaptateur spécifique à nos fichiers
     */

    private class FileAdapter extends ArrayAdapter<File> {
        /**
         * Permet de comparer deux fichiers
         *
         */
        private class FileComparator implements Comparator<File> {

            public int compare(File lhs, File rhs) {
                // si lhs est un répertoire et pas l'autre, il est plus petit
                if(lhs.isDirectory() && rhs.isFile())
                    return -1;
                // dans le cas inverse, il est plus grand
                if(lhs.isFile() && rhs.isDirectory())
                    return 1;

                //Enfin on ordonne en fonction de l'ordre alphabétique sans tenir compte de la casse
                return lhs.getName().compareToIgnoreCase(rhs.getName());
            }

        }

        public FileAdapter(Context context, int textViewResourceId,
                           List<File> objects) {
            super(context, textViewResourceId, objects);
            mInflater = LayoutInflater.from(context);
        }

        private LayoutInflater mInflater = null;

        /**
         * Construit la vue en fonction de l'item
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView vue = null;

            if(convertView != null)
                vue = (TextView) convertView;
            else
                vue = (TextView) mInflater.inflate(android.R.layout.simple_list_item_1, null);
            File item = getItem(position);
            //Si c'est un répertoire, on choisit la couleur dans les préférences

            vue.setText(item.getName());
            return vue;
        }

        /**
         * Pour trier rapidement les éléments de l'adaptateur
         */
        public void sort () {
            super.sort(new FileComparator());
        }
    }
}
