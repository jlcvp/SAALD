package br.ufpe.cin.ess.saald;


import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.UnknownHostException;

import br.ufpe.cin.ess.saald.ManagerActivity.PlaceholderFragment;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;
//rebs
public class AutenticaActivity extends ActionBarActivity {
	protected static final int RECIEVE_MESSAGE = 334455;
	Handler h=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in_screen);
		Button btn = (Button) findViewById(R.id.btnSingIn);
		
		h = new Handler() { 
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case RECIEVE_MESSAGE:                                                   // if receive massage
					boolean retorno = (boolean)msg.obj;
					Log.d("Thread", "HOLY HANDLER");
					completalogin(retorno);
					
					break;
				}
			};
		};
		
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				login();			
			}
		});
		
	}
	
	protected void completalogin(boolean retorno) {
		Intent it = new Intent();
		it.putExtra("result", retorno);
		setResult(RESULT_OK, it);
		finish();
		
	}
	
	String livro=null;
	String login = null;
	String password = null;
	
	class MyThread extends Thread {
		String livro;
		String login;
		String password;
		
		public MyThread(String livro, String login, String password) {
			
			this.livro = livro;
			this.login = login;
			this.password = password;
		}
		
		
		public void run()
		{
			Socket sckt=null;
			try {
				sckt = new Socket("172.22.67.237", 8081);
			
			
			OutputStream os = sckt.getOutputStream();
			Log.d("Thread", "HOLY OUTPUT STREAM");
			
			String json = new String("{nome:'"+livro+"', login:'"+ login +"',password:'"+ password +"'}");
			Log.d("Thread", "dat JSON = "+json);
			os.write(json.getBytes());
			
			boolean _return = false;
			
			InputStream is =  sckt.getInputStream();
			Log.d("Thread", "HOLY INPUT STREAM " + is.toString());
			byte[] buffer = new byte[1024];
			boolean ready = false;
			do
			{
				Log.d("Thread", "HOLY LOOP");
				int i = is.read(buffer);
				if(i>3)
				{
					if(String.valueOf(buffer).equalsIgnoreCase("true"))
					{
						_return = true;
						ready = true;
					}
					else if (String.valueOf(buffer).equalsIgnoreCase("false"))
					{
						_return = false;
						ready = true;
					}
				}
				
			}
			while (!ready);
//			DataInputStream mDinput = new DataInputStream(is);			
//        	mDinput.readFully(buffer, 0, 3); //espera até ter 3 caracteres no buffer
//			
			Log.d("Thread", "HOLY Hand");
			h.obtainMessage(RECIEVE_MESSAGE, 3, -1, _return).sendToTarget();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	MyThread myThread = null;
	
	public void login()
	{	//TODO implementar a comunicação WEB (usar asyncTask ou Thread pra não travar a UI e receber mensagem de aplicação não respondendo)
		
		
		livro = getIntent().getStringExtra("titulo");
		login = ((EditText)findViewById(R.id.etUserName)).getText().toString();
		password = ((EditText)findViewById(R.id.etPass)).getText().toString();			
		myThread = new MyThread(livro, login, password);
		Log.d("HUEHUE","login... startando a thread");
		myThread.setName("HUEHUEHUEBRBRBRBR");
		myThread.start();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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

	

}
