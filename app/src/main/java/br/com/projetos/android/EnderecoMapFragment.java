package br.com.projetos.android;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class EnderecoMapFragment extends Fragment {

    public static final double RADIUS_OF_EARTH_METERS = 6371009;


    private Endereco mEndereco;
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private float mZoom;
    private TextView mTextWait;
    private GeoPoint mGeoPoint;
    private View mMapContainer;
    private Context mContext;
	
	public static EnderecoMapFragment novaInstancia(Endereco e){
		Bundle parametros = new Bundle();
		parametros.putSerializable(Parameters.EXTRA_ENDERECO, e);
		EnderecoMapFragment fragment = new EnderecoMapFragment();
		fragment.setArguments(parametros);
		return fragment;
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("EnderecoMapFragment", "onCreate...");
        mZoom = 0f;
        mContext = null;
        if (savedInstanceState != null) {
			mEndereco = (Endereco)savedInstanceState.getSerializable(Parameters.EXTRA_ENDERECO);
		} else {
			mEndereco = (Endereco)getArguments().getSerializable(Parameters.EXTRA_ENDERECO);
			if (mEndereco == null)
				mEndereco = new Endereco();
		}
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("EnderecoMapFragment", "onCreateView...");
        View view = inflater.inflate(R.layout.fragment_mapa_endereco, container, false);
        mTextWait = new TextView(getActivity());
        /*  O código comentado abaixo NÃO funciona mais dentro dos fragments como usamos no app original.
        //  Tentei várias vezes e fiz várias alterações para usar o código abaixo, mas sem sucesso.
        //  Para ter o efeito esperado é preciso usar a classe MapView conforme novo código abaixo.
        //SupportMapFragment mapFragment = (SupportMapFragment)getFragmentManager().findFragmentById(R.id.map);
        //mGoogleMap = mapFragment.getMap();
        */

        mMapView = (MapView)view.findViewById(R.id.mapView);
        mMapView.onCreate(null);
        mGoogleMap = mMapView.getMap();
        MapsInitializer.initialize(getActivity());

        return view;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
        Log.i("EnderecoMapFragment", "onActivityCreated...");
    	if (savedInstanceState != null)
			mEndereco = (Endereco)savedInstanceState.getSerializable(Parameters.EXTRA_ENDERECO);
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
        Log.i("EnderecoMapFragment", "onResume...");
        mostrarEnderecoNoMapa();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EnderecoMapFragment", "onDestroy...");
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void setPositionMap(GoogleMap googleMap, LatLng latLong) {
		googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLong, 19.0f));
	}
	
	@Deprecated
	private void setEndereco(Endereco endereco){
		mEndereco = endereco;
	}
	
	private void mostrarEnderecoNoMapa(){
		Log.w(null, "mostrarEnderecoNoMapa()....");
        Log.i("EnderecoMapFragment:", "mEndereco= " + mEndereco.toString());
        if (mEndereco != null && mEndereco.getCep() != null) {
            if (mContext != null)
                new MapaTask(mContext).execute();
            else
                new MapaTask(getActivity()).execute();
        }else{
            Log.e("EnderecoMapFragment", "mostrarEnderecoNoMapa...mEndereco=NULL");
        }
	}
	
	public void mostrarEnderecoNoMapaTablet(Endereco endereco){
		mEndereco = endereco;
		mostrarEnderecoNoMapa();
	}


    public void mostrarEnderecoNoMapa(Context context, Endereco endereco){
        mEndereco = endereco;
        mContext = context;
        if (mGoogleMap != null)
            mGoogleMap.clear();
        mostrarEnderecoNoMapa();
    }

	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putSerializable(Parameters.EXTRA_ENDERECO, mEndereco);
	}



    private class MapaTask extends AsyncTask<Void, String, GeoPoint> {
        private ProgressDialog progress = null;
        private Context context;

        public MapaTask(Context context){
            this.context = context;
        }


        @Override
        protected void onPreExecute()
        {
            //Cria novo um ProgressDialogo e exibe
            //progress = new ProgressDialog(context);
            //progress.setMessage(mTextWait.getText().toString());
            //progress.show();
        }

        protected GeoPoint doInBackground(Void... params) {
            try {
                /*
                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                String sEndereco = URLDecoder.decode(MapaHTTP.montarURL(mEndereco), "UTF-8");
                sEndereco = sEndereco.substring(sEndereco.indexOf("=")+1, sEndereco.indexOf("&")).replace("+", " ");
                Log.i("EndrecoDetalheFragment:", "MapaTask.doInBackground..." + sEndereco);
                List<Address> enderecos = geocoder.getFromLocationName(sEndereco, 5);
                Log.i("Latitude:", String.valueOf(enderecos.get(0).getLatitude()));
                Log.i("Longitude:", String.valueOf(enderecos.get(0).getLongitude()));
                Log.i(null, enderecos.toString());
                if (enderecos.size() > 0){
                    if (mGeoPoint == null)
                        mGeoPoint = new GeoPoint();
                    mGeoPoint.setLatitude(enderecos.get(0).getLatitude());
                    mGeoPoint.setLongitude(enderecos.get(0).getLongitude());
                }
                Log.i("EnderecoDetalheFragment", "GeoPoint:" + mGeoPoint.toString());
				return MapaHTTP.pegarCoordenadas(mEndereco);
                */
                return MapaHTTP.pegarCoordenadasGeocoder(context, mEndereco);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(GeoPoint result) {
            super.onPostExecute(result);
            if (result != null){
                mGeoPoint = result;
                mEndereco.setLatitude(result.getLatitude());
                mEndereco.setLongitude(result.getLongitude());
                //if (isTablet())
                    buildMap();
                //progress.dismiss();
            }else{
                //if (progress != null && progress.isShowing())
                    //progress.dismiss();
                Toast.makeText(context, R.string.address_coordinates_err, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        @Override
        protected void onProgressUpdate(String... values){
            //Atualiza mensagem
           // progress.setMessage(values[0]);
        }
    }

    private void buildMap() {
        String logradouro = "";
        String texto = "";
        if (mGeoPoint != null){
            logradouro = mEndereco.getLogradouro();
            if (mGeoPoint.getGeoPointType() == GeoPoint.GeoPointType.APPROXIMATE)
                logradouro += "  (Aproximado)";
            else{
                if (mEndereco.getNome() != null)
                    logradouro = mEndereco.getNome();//markerOptions.title(mEndereco.getNome());

                if (mEndereco.getLogradouro() != null)
                    texto = mEndereco.getLogradouro();
                if (mEndereco.getNumero() != null && texto != null)
                    texto += ", " + mEndereco.getNumero();
                if (mEndereco.getBairro() != null && texto != null)
                    texto += " - " + mEndereco.getBairro();
                if (mEndereco.getCidade() != null && texto != null)
                    texto += "\n" + mEndereco.getCidade();
                if (mEndereco.getUf() != null && texto != null)
                    texto += " - " + mEndereco.getUf();
                if (mEndereco.getCep() != null && texto != null)
                    texto += " - " + mEndereco.getCep();
                //if (texto != null)
                  //  markerOptions.snippet(texto);
            }
        }
            if (mGoogleMap == null)
                Log.e("buildMap()", "googleMap está NULO!!!");
        LatLng latLng = new LatLng(mEndereco.getLatitude(), mEndereco.getLongitude());
        mGoogleMap.clear();
        mGoogleMap.addMarker(new MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title(logradouro)
                .snippet(texto));
        //v2.0
        if (mContext != null)
            MapsInitializer.initialize(mContext);
        else
            MapsInitializer.initialize(getActivity());
        Log.i("buildMap", "mEndereco= " + mEndereco.toString());
        Log.i("buildMap", "mGeoPoint= " + mGeoPoint.toString());
        if (mGeoPoint != null && mGeoPoint.getGeoPointType() == GeoPoint.GeoPointType.APPROXIMATE){
            // o 2º parametro deve sersubstituido por um ponto geográfico próximo do ponto central
            Log.w("EnderecoMapFragment", "Endereço paroximado!!! ");
            int opacity = 40;
            // hsv = Hue, Saturation, Value
            float [] hsv = new float[]{0,1,1};
            Color.colorToHSV(Color.GREEN, hsv);
            LatLng latLngAprox = new LatLng(mGeoPoint.getLatitude()*1.00004, mGeoPoint.getLongitude()*1.00004);//mGeoPoint.getLatAprox(), mGeoPoint.getLngAprox());
            CircleOptions circleOptions = new CircleOptions()
                    .center(latLng)
                    .strokeWidth(2.0f)
                    .strokeColor(Color.CYAN)
                    .radius(latLngToRadiusMeters(latLng, latLngAprox))
                    .fillColor(Color.HSVToColor(opacity, hsv));
            mGoogleMap.addCircle(circleOptions);
        }
        //v2.0 - fim
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mGoogleMap.setTrafficEnabled(true);
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19.0f));
        mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {

            @Override
            public boolean onMyLocationButtonClick() {
                updateMap();
                return true;
            }
        });
        if (this.mMapContainer != null)
            mMapContainer.setVisibility(android.view.View.VISIBLE);
    }


    //métodos retirados de um exemplo do google maps, mas ainda não está em usos nesta aplicação
	/*
	// Generate LatLng of radius marker
    private static LatLng toRadiusLatLng(LatLng center, double radius) {
        double radiusAngle = Math.toDegrees(radius / Parameters.RADIUS_OF_EARTH_METERS) /
                Math.cos(Math.toRadians(center.latitude));
        return new LatLng(center.latitude, center.longitude + radiusAngle);
    }*/

    /*atualização para v2.0
     * 		Este método calcula a diferença entre o ponto central (geoPoint) retornado e um de seus pontos geográficos
     * próximos ao ponto central.
     * 		Este médoto será executado para gerar um circulo que será um raio de proximidade ao ponto principal
     * quando este ponto não for a localização exata por falta de paramentros na pesquisa do ponto geográfico
     */
    private static double latLngToRadiusMeters(LatLng center, LatLng radius) {
        float[] result = new float[1];
        Location.distanceBetween(center.latitude, center.longitude,
                radius.latitude, radius.longitude, result);
        /*O resultado e multiplicado por 0.5 para diminuir o raio de proximidade com o ponto central,
         * pois o raio inicial ainda é muito grande
         */
        return result[0]*0.5;
    }
    // v2.0 - fim

    private boolean isTablet(){
        if (getActivity().findViewById(R.id.detalhe) != null){
            mMapContainer = getActivity().findViewById(R.id.mapa);
            if (mMapContainer != null)
                mMapContainer.setVisibility(android.view.View.VISIBLE);
            return true;
        }else
            return false;
    }

    private void updateMap() {
        if (mEndereco != null && (mEndereco.getLatitude() != null && mEndereco.getLongitude() != null)) {
            if (mContext != null)
                new MapaTask(mContext).execute();
            else
                new MapaTask(getActivity()).execute();
        }
    }


}
