package br.ufpe.cin.ess.saald;


import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import org.json.JSONObject;

import br.ufpe.cin.ess.saald.ManagerActivity.PlaceholderFragment;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View.OnClickListener;
//rebs
public class AutenticaActivity extends ActionBarActivity {
	protected static final int RECIEVE_MESSAGE = 334455;
	private static final String URL_BASE = "http://192.168.25.198:8080";
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
					boolean retorno = (Boolean)msg.obj;
					
					Log.d("DEBUG", "HANDLER\nAutenticacao return = "+retorno);

					completalogin(retorno);
					
					break;
				}
			};
		};
		
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText et_login = (EditText)findViewById(R.id.etUserName);
				EditText et_password = (EditText)findViewById(R.id.etPass);
				
				login = et_login.getText().toString();
				password = et_password.getText().toString();
				
				
				if(!login.isEmpty() && !password.isEmpty()) //forms preenchidos
				{
					login();			
				}
				else //algum esta vazio
				{
					//Resetando o estado dos textviews
					et_login.setHintTextColor(Color.parseColor("#CCCCCC")); 
					et_login.setHint("Email");
					et_password.setHintTextColor(Color.parseColor("#CCCCCC"));
					et_password.setHint("Password");
					
					if(login.isEmpty())
					{
						et_login.setHintTextColor(Color.parseColor("#FF6666"));
						et_login.setHint("Informe o Email");
					}
					
					if(password.isEmpty())
					{
						et_password.setHintTextColor(Color.parseColor("#FF6666"));
						et_password.setHint("Informe a Senha");
					}
					
				}
			}
		});
		
	}
	
	protected void completalogin(boolean retorno) {
		if(retorno == true) //credenciais OK
		{
			Intent it = new Intent();
			it.putExtra("id", getIntent().getIntExtra("id", -1));
			setResult(RESULT_OK, it);
			finish();
		}
		else //falha no login
		{
			TextView tv = (TextView)findViewById(R.id.tv_erro_login);
			tv.setText("USUARIO OU SENHA INVALIDOS");
		}
		
	}
	
	String livro=null;
	String login = null;
	String password = null;
	
	class MyThread extends Thread {
		
		String livro;
		String login;
		String password;
		
		public MyThread(String livro, String login, String password) {
			
			this.livro = livro.replaceAll(" ","_");
			this.login = login;
			this.password = password;
			
		}
		
		
		
		
		public void run()
		{
			

			try {

				String url = new String(URL_BASE);
				String postData = new String(login+"/"+password+"/"+ livro);
				HttpURLConnection conn =  (HttpURLConnection) new URL(url+"/userauth/"+postData).openConnection();
				Log.d("DEBUG", "dataquery = "+postData);


				boolean _return = false;

				InputStream is =  conn.getInputStream();
				Log.d("DEBUG", "INPUT STREAM " + is.toString());
				byte[] buffer = new byte[1024];

				StringBuilder sb = new StringBuilder();
				int i=0;
				do 
				{
					i+=is.read(buffer);


					sb.append(new String(buffer,0,i));

					Log.d("HUEBR", "sb= "+sb.toString());

				}while(i<conn.getContentLength());

				String json = sb.toString();
				if(json.equalsIgnoreCase("true"))   //Login bem sucedido
				{
					_return = true;
				}
					Log.d("Thread", "HOLY Hand");
					h.obtainMessage(RECIEVE_MESSAGE, 3, -1, _return).sendToTarget();
				
				
				
			} catch (UnknownHostException e) {
				
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			}

		}
	}
	
	MyThread myThread = null;
	
	public void login()
	{	
		
		livro = getIntent().getStringExtra("titulo");
		login = ((EditText)findViewById(R.id.etUserName)).getText().toString();
		password = ((EditText)findViewById(R.id.etPass)).getText().toString();			
		myThread = new MyThread(livro, login, password);
		Log.d("HUEHUE","login... startando a thread");
		myThread.setName("Login_Thread");
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
