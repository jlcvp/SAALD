package br.ufpe.cin.ess.saald;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;









import java.util.UUID;

import br.ufpe.cin.ess.saald.listView.AdapterListView;
import br.ufpe.cin.ess.saald.listView.ItemListView;
import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
	public static final int RECIEVE_MESSAGE = 1;

	Handler h;
	PlaceholderFragment mFragment = null;
	
	private BluetoothAdapter btAdapter = null;
	private BluetoothSocket btSocket = null;
	private StringBuilder sb = new StringBuilder();
	
	private ConnectedThread mConnectedThread;
	

	// SPP UUID service
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static final String TAG = "HUE";

	// MAC-address of Bluetooth module
	//private static String address = "20:14:03:24:10:57"; //meu adaptador HC-06
	private static String address = "20:14:03:24:50:43"; //Rafis' adaptador HC-05

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manager);
		
		
		h = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case RECIEVE_MESSAGE:                                                   // if receive massage
					byte[] readBuf = (byte[]) msg.obj;
					String strIncom = new String(readBuf, 0, msg.arg1);                 // create string from bytes array
					sb.append(strIncom);                                                // append string
					int endOfLineIndex = sb.indexOf("\n");
					Log.i("HUE","buffer = "+strIncom);// determine the end-of-line
					if (endOfLineIndex > 0) {                                            // if end-of-line,
						
						String sbprint = sb.substring(0, endOfLineIndex);               // extract string
						sb.delete(0, sb.length());                                      // and clear
						Log.d("HUE","Data from Arduino: " + sbprint); // debug text
						mFragment = (PlaceholderFragment) getFragmentManager().findFragmentById(R.id.container);
						mFragment.actionInShelf(sbprint);
						

					}
					//Log.d(TAG, "...String:"+ sb.toString() +  "Byte:" + msg.arg1 + "...");
					break;
				}
			};
		};

		btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
		checkBTState();

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		
		
	}
	
	private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
	      if(Build.VERSION.SDK_INT >= 10){
	          try {
	              final Method  m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
	              return (BluetoothSocket) m.invoke(device, MY_UUID);
	          } catch (Exception e) {
	              Log.e("HUE", "Could not create Insecure RFComm Connection",e);
	          }
	      }
	      return  device.createRfcommSocketToServiceRecord(MY_UUID);
	  }

	@Override
	public void onResume() {
		super.onResume();

		Log.d(TAG, "...onResume - try connect...");

		// Set up a pointer to the remote node using it's address.
		BluetoothDevice device = btAdapter.getRemoteDevice(address);

		// Two things are needed to make a connection:
		//   A MAC address, which we got above.
		//   A Service ID or UUID.  In this case we are using the
		//     UUID for SPP.

		try {
			btSocket = createBluetoothSocket(device);
		} catch (IOException e) {
			Log.e("HUE", "FATAL ERROR - In onResume() and socket create failed: " + e.getMessage() + ".");
		}

		// Discovery is resource intensive.  Make sure it isn't going on
		// when you attempt to connect and pass your message.
		btAdapter.cancelDiscovery();

		// Establish the connection.  This will block until it connects.
		Log.d(TAG, "...Connecting...");
		try {
			btSocket.connect();
			Log.d(TAG, "....Connection ok...");
		} catch (IOException e) {
			try {
				btSocket.close();
			} catch (IOException e2) {
				Log.e(TAG, "Fatal Error- In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
			}
		}

		// Create a data stream so we can talk to server.
		Log.d(TAG, "...Create Socket...");

		mConnectedThread = new ConnectedThread(btSocket);
		mConnectedThread.start();
	}

	@Override
	public void onPause() {
		super.onPause();

		Log.d(TAG, "...In onPause()...");

		try     {
			btSocket.close();
		} catch (IOException e2) {
			Log.e(TAG, "Fatal Error - In onPause() and failed to close socket." + e2.getMessage() + ".");
		}
	} 
	
	private void checkBTState() {
	    // Check for Bluetooth support and then check to make sure it is turned on
	    // Emulator doesn't support Bluetooth and will return null
	    if(btAdapter==null) {
	      Log.e(TAG, "Fatal Error - Bluetooth not support");
	    } else {
	      if (btAdapter.isEnabled()) {
	        Log.d(TAG, "...Bluetooth ON...");
	      } else {
	        //Prompt user to turn on Bluetooth
	        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	        startActivityForResult(enableBtIntent, 1);
	      }
	    }
	  }
	
//	 @Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.manager, menu);
//		return true;
//	}

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar item clicks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
	
	

	
	private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
      
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
      
            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }
      
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
      
        public void run() {
            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes; // bytes returned from read()
 
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);        // Get number of bytes and message in "buffer"
                    h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();     // Send to message queue Handler
                } catch (IOException e) {
                    break;
                }
            }
        }
	}
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment implements OnItemClickListener {
		private static final int LOGIN_RESULT = 20620; //pastor cleiton collins
		AdapterListView adapterListView;
		ArrayList<ItemListView> itens;
		ListView lv;
		public PlaceholderFragment() {
			
		}
		
		

		public void actionInShelf(String rawBTData) {
			int posicao;
			char acao;
			
			posicao = rawBTData.charAt(0) -'0';
			acao = rawBTData.charAt(1);
			
			updateList(posicao,acao);
			
		}



		private void updateList(int posicao, char acao) {
			
			boolean desistiu = false;
			for(int i = 0;i<adapterListView.getCount();i++){
				if(adapterListView.getItem(i).getId()==posicao)
				{
					desistiu = true;
					adapterListView.removeItem(i);
					adapterListView.notifyDataSetChanged();
				}
			}
			
			if(!desistiu)
			{
				ItemListView livro = new ItemListView(posicao,TITULO_LIVRO[posicao],getResources().getDrawable(R.drawable.book));
				itens.add(livro);
				adapterListView.notifyDataSetChanged();
			}
			
		}

		public void chamaAutenticacao(ItemListView livro,int posicaoNaLista) {
			Intent it = new Intent(this.getActivity(), AutenticaActivity.class);
			it.putExtra("titulo", livro.getTexto());
			int id = livro.getId();
			it.putExtra("id", id);
			startActivityForResult(it, LOGIN_RESULT);
		}
		
		private AdapterListView criaAdapter(){
			itens = new ArrayList<ItemListView>();
			
//			for(int i = 0; i<QTD_LIVROS;i++)
//			{
//				ItemListView livro = new ItemListView(i,TITULO_LIVRO[i],getResources().getDrawable(R.drawable.book));
//				itens.add(livro);
//			}
//			
			return new AdapterListView(this.getActivity(),itens); 
			
		}


		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_manager,
					container, false);
			lv = (ListView) rootView.findViewById(R.id.listView1);
			adapterListView = criaAdapter();
			lv.setAdapter(adapterListView);
			lv.setOnItemClickListener(this);
			return rootView;
		}

		public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
			//on item click
			ItemListView livro = adapterListView.getItem(position);
			chamaAutenticacao(livro,position);
			
		}
		
		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {

		    if (requestCode == LOGIN_RESULT) {
		        if(resultCode == RESULT_OK){
		            int id=data.getIntExtra("result",-1);
		            //mudar listView
		            if(id!=-1){
		            	int position;
		            	
		            	for(int i = 0;i<adapterListView.getCount();i++)
		            		if(adapterListView.getItem(i).getId()==id)
		            		{
		            			adapterListView.removeItem(i);
		            			adapterListView.notifyDataSetChanged();
		            		}
		            }
		        }
		        if (resultCode == RESULT_CANCELED) {
		           //não mudar list view, talvez dar um aviso...
		        }
		    }
		}//onActivityResult
		
		
		
		
	}



	

}
