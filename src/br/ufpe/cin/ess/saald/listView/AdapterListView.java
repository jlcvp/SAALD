package br.ufpe.cin.ess.saald.listView;

import java.util.ArrayList;




import br.ufpe.cin.ess.saald.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdapterListView extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<ItemListView> itens;

    public AdapterListView(Context context, ArrayList<ItemListView> itens) {
        //Itens que preencheram o listview
        this.itens = itens;
        //responsavel por pegar o Layout do item.
        mInflater = LayoutInflater.from(context);
        
    }

    /**
     * Retorna a quantidade de itens
     *
     * @return
     */
    public int getCount() {
        return itens.size();
    }

    /**
     * Retorna o item de acordo com a posicao dele na tela.
     *
     * @param position
     * @return
     */
    public ItemListView getItem(int position) {
        return itens.get(position);
    }

    /**
     * Sem implementação
     *
     * @param position
     * @return
     */
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View view, ViewGroup parent) {
        //Pega o item de acordo com a posção.
        ItemListView item = getItem(position);
        //infla o layout para podermos preencher os dados
        view = mInflater.inflate(R.layout.list_item, null);
        

        //pelo layout pego pelo LayoutInflater, pegamos cada id relacionado
        //ao item e definimos as informações.
        
        TextView tv = ((TextView)(view.findViewById(R.id.list_item_text)));
        
        if(tv != null){
        	tv.setText(item.getTexto());
        }
        ImageView iv = ((ImageView)view.findViewById(R.id.list_item_image));
        
        
        if(tv!= null){
        	iv.setImageDrawable(item.getIconeImg());
        }  
        //TODO Mexi aqui, descomentar em cima quando tiver os icones

        return view;
    }
}
