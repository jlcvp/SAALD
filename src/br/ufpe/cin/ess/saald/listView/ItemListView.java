package br.ufpe.cin.ess.saald.listView;


import android.graphics.drawable.Drawable;

/**
 * Class that implements an Item for a grid view
 * @author LeU
 *
 */
public class ItemListView {

    private String texto;
    //private MjolnirImg icone;
    private Drawable icone;
    public ItemListView(String texto, Drawable iconeDrawable) {
        this.texto = texto;
        //this.icone = new MjolnirImg(iconeBytes);

        this.icone = iconeDrawable;

        
        
    }
    
    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
    
    public Drawable getIconeImg()
    {
    	return icone;
    }

}
	
	
	
	
	
	


