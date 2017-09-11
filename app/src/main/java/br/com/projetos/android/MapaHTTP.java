package br.com.projetos.android;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpConnection;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.projetos.android.GeoPoint.GeoPointType;


/*
 * F U N C I O N A N D O     C O R R E T A M E N T E 
 * 
 */
public class MapaHTTP {
	// site explicando como obter rotas para os mapas do google: https://developers.google.com/maps/documentation/directions/

	//EXEMPLO 1
	//private static final String EXEMPLO1 = "https://maps.google.com/maps/api/geocode/json?address=Rua+Consul+Said+Adun+126+COHAB+Recife+51320240&sensor=false";
	//EXEMPLO 2
	//private static final String EXEMPLO = "http://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&sensor=true_or_false";
	private static final String ENDERECO_DESEJADO = "<ENDERECO_DESEJADO>" + "&sensor=false";
	// TIRADO DO EXEMPLO 1
	private static final String URL_GEOCODING = "http://maps.googleapis.com/maps/api/geocode/json?address=" + ENDERECO_DESEJADO + "&sensor=false";
	private static final String URL_GEOCODING_CEP = "http://maps.googleapis.com/maps/api/geocode/json?address=" + ENDERECO_DESEJADO;
	
	@Deprecated
	public static HttpURLConnection abrirConexao(String url) throws IOException{
		URL url1 = new URL(url);
		HttpURLConnection conexao = (HttpURLConnection)url1.openConnection();
		conexao.setRequestMethod("GET");
		conexao.setDoInput(true);
		conexao.connect();
		return conexao;
	}
	
	/**
	 * 
	 * @param endereco
	 * Endereço que se quer obter o ponto geográfico (latitude x longitude)
	 * @return
	 * Retorna um objeto do tipo GeoPoint (latitude x longitude), que além do ponto geográfico principal obtido
	 * retorna mais um ponto aproximado ao ponto princial e o tipo de retorno (exato ou aproximado).
	 * Caso a consulta pelo ponto exato lance alguma exceção, o método tenta uma nova consulta pelo CEP que uma
	 * aproximação do endereço originalmente pesquisado. Isso ocorre porque nem todos os endereços consultados são encontrados.
	 * Por exemplo: mesmo que seja informado o endereço correto, na base de dados do google pode não constar o número
	 * de imóvel informado no endereço, o que pode resultar num JSON de array vazio, que lança uma exceção. E qdo isso acontece
	 * e feita uma consulta de endreço aproximado (pelo CEP apenas).
	 * @throws Exception
	 * Lança uma exceção caso não consiga tratá-la.
	 */
    @Deprecated
	public static GeoPoint pegarCoordenadas(Endereco endereco) throws Exception{
		GeoPoint geoPoint = null;
		HttpURLConnection conexao = null;
		HttpURLConnection novaConexao = null;
        Integer statusCode;
		try{
			conexao = abrirConexao(montarURL(endereco));
            statusCode = conexao.getResponseCode();
            Log.i("pegarCoordenadas:", "statusCode=" + statusCode);
			if (statusCode == HttpURLConnection.HTTP_OK){
				geoPoint = extracGeoPoint(conexao.getInputStream());
				conexao.disconnect();
				geoPoint.setGeoPointType(GeoPointType.EXACT);
			}
			else
				throw new Exception("MapaHTTP: erro de conexção");
			if (geoPoint == null)
				throw new Exception("MapaHTTP: tentar consultar pelo CEP");
		}
		catch(Exception e){
			if (conexao != null)
				conexao.disconnect();
			try{
				novaConexao = abrirConexao(montarURLporCEP(endereco));
                statusCode = novaConexao.getResponseCode();
                Log.i("pegarCoordenadas", "novaConexao=" + statusCode);
				if (statusCode == HttpURLConnection.HTTP_OK){
					geoPoint = extracGeoPoint(novaConexao.getInputStream());
					novaConexao.disconnect();
				}else {
                    Log.e("MapaHTTP", "pegarCoordenadas\\montarURLporCEP deu ERRO");
                    throw e;
                }
				geoPoint.setGeoPointType(GeoPointType.APPROXIMATE);

			}catch(Exception ex){
				throw ex;
			}
		}
		return geoPoint;
	}
	
	
	/**
	 * Método responsável por extrair o GeoPoint do JSON retornado no stream.
	 * 
	 * @param is
	 * Objeto do tipo InputStream que é um stream de dados retornados pela consulta e será convertido num GeoPoint.
	 * @return
	 * GeoPOoint extraido do stream retornado pelo geocoder.
	 * @throws JSONException
	 * Caso haja algum problema quando JSON é extraido do stream.
	 * @throws IOException
	 * Caso haja algum problema de E/S.
	 */
    @Deprecated
	private static GeoPoint extracGeoPoint(InputStream is) throws JSONException, IOException {
		GeoPoint geoPoint = null;
		JSONObject json;
		JSONObject jsonLoc;
		JSONArray jsonRes;
		JSONObject jsonGeo;
		JSONObject jsonEnd;
		json = new JSONObject(bytesToString(is));
		jsonRes = json.getJSONArray("results");
		jsonEnd = jsonRes.getJSONObject(0);
		jsonGeo = jsonEnd.getJSONObject("geometry");
		jsonLoc = jsonGeo.getJSONObject("location");
		geoPoint = new GeoPoint(jsonLoc.getDouble("lat"), jsonLoc.getDouble("lng"), GeoPointType.EXACT);
		//atualização para v2.0
		geoPoint.setLatAprox(jsonGeo.getJSONObject("bounds").getJSONObject("southwest").getDouble("lat"));
		geoPoint.setLngAprox(jsonGeo.getJSONObject("bounds").getJSONObject("southwest").getDouble("lng"));
		//v2.0 - fim
        Log.i("extractGeoPoint:", "Json status=" + json.getString("status"));
        if (json.getString("status").toUpperCase().equals("OK"))
		    return geoPoint;
        else
            return null;
	}
	
	/**
	 * Monta uma URL de consulta pelo CEP. O que resulta num ponto geográfico APROXIMADO do endereço informado.
	 * 
	 * @param endereco
	 * Endereço que deseja obter o ponto geográfico.
	 * @return
	 * URL de consulta.
	 */
    @Deprecated
	public static String montarURLporCEP(Endereco endereco) {
		//String url = "http://maps.google.com/maps/api/geocode/json?address=";
        Log.i("MapaHTTP", "montarURLporCEP...");
		String texto = endereco.getCep().substring(0, 5) + "-" + endereco.getCep().substring(5, 8);
		String textoUTF8 = texto;
		try {
			textoUTF8 = URLEncoder.encode(texto, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        String txt = URL_GEOCODING_CEP.replace(ENDERECO_DESEJADO, textoUTF8);
        Log.i("montarURLporCEP:", "URL=" + txt);
		return txt;
		//return url + texto;
	}
	
	/**
	 * Este método cria uma URL com todos os dados do endereço informado para obter o ponto geográfico com o
	 * máximo de precisão.
	 * @param endereco
	 * Endreço que deseja obter o ponto geográfico.
	 * @return
	 * URL para consulta de ponto geográfico.
	 */
    @Deprecated
	public static String montarURL(Endereco endereco){
		/*
		 * 
		 * ESTE MÉTODO ESTÁ PRONTO PARA MONTAR URL DE ACORDO COM O EXEMPLO1 ACIMA
		 * MAS NÃO FUNCIONA CORRETAMENTE SE NÃO CONVERTER O TEXTO DO ENDEREÇO PARA UTF-8
		 * 
		 */
		String enderecoDeConsulta  = "";
        Log.i("MapaHTTP", "montarURL...");
        Log.i("MapaHTTP", "Endereço=" + endereco.toString());
		if (endereco.getLogradouro().length() > 0)
			enderecoDeConsulta = endereco.getLogradouro().replace(" ", "+");
		if (endereco.getNumero().length() > 0){
			if (enderecoDeConsulta.length() > 0)
				enderecoDeConsulta += "+" + endereco.getNumero();
			else
				enderecoDeConsulta = endereco.getNumero();
		}
		if (endereco.getBairro().length() > 0){
			if (enderecoDeConsulta.length() > 0)
				enderecoDeConsulta += "+" + endereco.getBairro().replace(" ", "+");
			else
				enderecoDeConsulta = endereco.getBairro().replace(" ", "+");
		}
		if (endereco.getCidade().length() > 0){
			if (enderecoDeConsulta.length() > 0)
				enderecoDeConsulta += "+" + endereco.getCidade().replace(" ", "+");
			else
				enderecoDeConsulta = endereco.getCidade().replace(" ", "+");
		}
		//O EXEMPLO1 NÃO USA UF NA URL
		 if (endereco.getUf().length() > 0){
			 if (enderecoDeConsulta.length() > 0)
				 enderecoDeConsulta += "+" + endereco.getUf();
			 else
				 enderecoDeConsulta += endereco.getUf();
		 }
		if (endereco.getCep().length() > 0){
			if (enderecoDeConsulta.length() > 0)
				enderecoDeConsulta += "+" + endereco.getCep().substring(0, 5) + "-" + endereco.getCep().substring(5, 8);
			else
				enderecoDeConsulta += endereco.getCep().substring(0, 5) + "-" + endereco.getCep().substring(5, 8);
		}
		String texto = "";
		try {
			texto = URLEncoder.encode(enderecoDeConsulta, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			texto = enderecoDeConsulta;
			e.printStackTrace();
		}
        String txt = URL_GEOCODING.replace(ENDERECO_DESEJADO, texto);
        Log.i("MapaHTTP", "URL=" + txt);
		return txt;
	}
	
	/**
	 * Converte o stream de bytes numa string.
	 * @param is
	 * @return
	 * @throws IOException
	 */
	private static String bytesToString(InputStream is) throws IOException{
		byte[] blocoDeBytes = new byte[1024];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int bytesLidos;
		while ((bytesLidos = is.read(blocoDeBytes)) != -1){
			baos.write(blocoDeBytes, 0, bytesLidos);
		}
        //Log.d("bytesToString:", new String(baos.toByteArray()));
		return new String(baos.toByteArray());
	}


    public static GeoPoint pegarCoordenadasGeocoder(Context context, Endereco endereco){
        GeoPoint geoPoint = new GeoPoint();
        List<Address> enderecos = null;
        String enderecoDeConsulta = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try{
            enderecoDeConsulta = localizarPeloEnderecoCompleto(endereco);
            geoPoint.setGeoPointType(GeoPointType.EXACT);
        }catch(Exception e){
            enderecoDeConsulta = endereco.getCep().substring(0, 5) + "-" + endereco.getCep().substring(5, 8);
            geoPoint.setGeoPointType(GeoPointType.APPROXIMATE);
        }
        try {
            enderecos = geocoder.getFromLocationName(enderecoDeConsulta, 5);
            Log.i("MapaHTTP:", "Latitude= " + String.valueOf(enderecos.get(0).getLatitude()));
            Log.i("MapaHTTP:", "Longitude= " + String.valueOf(enderecos.get(0).getLongitude()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (enderecos.size() > 0){
            geoPoint.setLatitude(enderecos.get(0).getLatitude());
            geoPoint.setLongitude(enderecos.get(0).getLongitude());
        }else
            geoPoint = null;
        return geoPoint;
    }

    private static String localizarPeloEnderecoCompleto(Endereco endereco) throws Exception{
        String enderecoDeConsulta = null;
        if (endereco.getLogradouro().length() > 0)
            enderecoDeConsulta = endereco.getLogradouro() + " ";
        if (endereco.getNumero().length() > 0)
            enderecoDeConsulta += ", " + endereco.getNumero();
        if (endereco.getBairro().length() > 0)
            enderecoDeConsulta += " " + endereco.getBairro();
        if (endereco.getCidade().length() > 0)
            enderecoDeConsulta+= " " + endereco.getCidade();
        if (endereco.getUf().length() > 0)
            enderecoDeConsulta+= " " + endereco.getUf();
        if (endereco.getCep().length() > 0)
            enderecoDeConsulta+= " " + endereco.getCep();
        return enderecoDeConsulta;
    }

}
