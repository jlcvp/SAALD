package br.ufpe.cin.ess.saald.listView;


import android.graphics.drawable.Drawable;

/**
 * Class that implements an Item for a grid view
 * @author LeU
 *
 */
public class ItemListView {
	int id;
    private String texto;
    //private MjolnirImg icone;
    private Drawable icone;
    public ItemListView(int id_livro ,String texto, Drawable iconeDrawable) {
        this.texto = texto;
        //this.icone = new MjolnirImg(iconeBytes);
        id = id_livro;
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

	public int getId() {
		// TODO Auto-generated method stub
		return id;
	}

}
	
	
	
	
	
	


