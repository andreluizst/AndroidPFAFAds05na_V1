package br.com.projetos.android;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/*import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;*/


import android.util.Log;

import com.google.gson.Gson;

public class ConsultaCepWeb {
	private static final String CEP_DESSEJADO = "<CEP_DESEJADO>";
	private static final String URL_CEP = "http://cep.republicavirtual.com.br/web_cep.php?cep=" + CEP_DESSEJADO + "&formato=json";
	
	/*private final String url;
	private DefaultHttpClient httpClient;
	private HttpPost httpPost;
	private HttpResponse response;
	
	public ConsultaCepWeb(String url){
		this.url = url;
	}*/
	
	private static HttpURLConnection abrirConexao(String url) throws IOException{
		
		URL urlToConnect = new URL(url);
        Log.i("ConsultaCepWeb:", "abrirConexao...");
        Log.i("ConsultaCepWeb:", "URL= " + url);
		HttpURLConnection httpUrlConnection = (HttpURLConnection)urlToConnect.openConnection();
		httpUrlConnection.setRequestMethod("GET");
		httpUrlConnection.setDoInput(true);
		httpUrlConnection.connect();
		return httpUrlConnection;
	}
	
	public static Endereco oberEndereco(String cep) throws IOException{
		String url = URL_CEP.replace(CEP_DESSEJADO, cep);
        Log.i("ConsultaCepWeb:", "obterEndereco...");
        Log.i("ConsultaCepWeb:", "URL= " + url);
		HttpURLConnection connection = abrirConexao(url);
		if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
			return parseJSON(bytesToString(connection.getInputStream()));
		}
		return null;
	}
	
	private static String bytesToString(InputStream is) throws IOException{
		byte bytes[] = new byte[1024];
		int bytesLidos;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while ((bytesLidos = is.read(bytes)) != -1){
			baos.write(bytes, 0, bytesLidos);
		}
		return new String(baos.toByteArray());
	}
		
	/*public Endereco obterEndereco(){
		try {
			httpClient = new DefaultHttpClient();
			httpPost = new HttpPost(url);
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");
			response = httpClient.execute(httpPost);
			return parseJSON(EntityUtils.toString(response.getEntity()));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}*/
	
	private static Endereco parseJSON(String json){
		try {
			Gson gson = new Gson();
            Log.i("ConsultaCepWeb:", "parseJSON...");
            Log.i("ConsultaCepWeb:", "JSON= " + json);
			Endereco endereco = gson.fromJson(json, Endereco.class);
            Log.i("ConsultaCepoWeb:", "Endereco= " + endereco.toString());
			return endereco;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
