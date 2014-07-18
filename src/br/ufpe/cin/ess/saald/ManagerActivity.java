package br.ufpe.cin.ess.saald;

import java.util.ArrayList;



import br.ufpe.cin.ess.saald.listView.AdapterListView;
import br.ufpe.cin.ess.saald.listView.ItemListView;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ManagerActivity extends Activity {
	

	public static final int QTD_LIVROS = 3;
	public static final String[] TITULO_LIVRO = {"O Guia do Mochileiro da Galáctea",
												 "O Retorno de Dariru",
												 "Galinha Pintadinha - Ultimato"};

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manager);
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manager, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment implements OnItemClickListener {
		AdapterListView adapterListView;
		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_manager,
					container, false);
			ListView lv = (ListView) rootView.findViewById(R.id.listView1);
			adapterListView = criaAdapter();
			lv.setAdapter(adapterListView);
			lv.setOnItemClickListener(this);
			return rootView;
		}

		public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
			//on item click
			ItemListView livro = adapterListView.getItem(position);
			chamaAutenticacao(livro);			
		}
		
		public void chamaAutenticacao(ItemListView livro) {
			Intent it = new Intent(this.getActivity(), AutenticaActivity.class);
			it.putExtra("titulo", livro.getTexto());
			startActivity(it);
		}
		
		private AdapterListView criaAdapter(){
			ArrayList<ItemListView> itens = new ArrayList<ItemListView>();
			
			for(int i = 0; i<QTD_LIVROS;i++)
			{
				ItemListView livro = new ItemListView(TITULO_LIVRO[i],getResources().getDrawable(R.drawable.book));
				itens.add(livro);
			}
			
			return new AdapterListView(this.getActivity(),itens); 
			
		}
		
		
	}



	

}
