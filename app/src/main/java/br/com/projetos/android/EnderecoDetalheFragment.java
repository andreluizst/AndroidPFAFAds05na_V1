package br.com.projetos.android;

import org.json.JSONObject;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class EnderecoDetalheFragment extends Fragment implements OnClickListener, OnKeyListener  {
	private TextView mTextWait;
    private TextView txtUF;
    private TextView txtLogradouro;
    private TextView txtCidade;
    private TextView txtBairro;
    private TextView txtNumero;
    private TextView txtNome ;
    private TextView txtCep;
	private Endereco mEndereco;
    private BuscaCepTask mBuscaCepTask;
    private GeoPoint mGeoPoint;
    private Button mBotao;
    private Button mBtnUpdateMap;
    private Context mContext;
    private JSONObject mJson;
    private View mMapContainer;
	
	public interface IAoAtualizarEndereco{
        public void onAposEnderecoAtualizado(Endereco endereco);
    }

	public static EnderecoDetalheFragment novaInstancia(Endereco e){
		Bundle parametros = new Bundle();
		parametros.putSerializable(Parameters.EXTRA_ENDERECO, e);
		EnderecoDetalheFragment fragment = new EnderecoDetalheFragment();
		fragment.setArguments(parametros);
		return fragment;
	}
	
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
		if (savedInstanceState != null) {
			mEndereco = (Endereco) savedInstanceState.getSerializable(Parameters.EXTRA_ENDERECO);
		} else {
			mEndereco = (Endereco) getArguments().getSerializable(Parameters.EXTRA_ENDERECO);
			if (mEndereco == null)
				mEndereco = new Endereco();
		}
		mContext = getActivity();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null){
			mEndereco = (Endereco)savedInstanceState.getSerializable(Parameters.EXTRA_ENDERECO);
			updateFilds(mEndereco);
			mGeoPoint = (GeoPoint)savedInstanceState.getSerializable(Parameters.EXTRA_GEOPOINT);
		}

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mTextWait = new TextView(getActivity());
		mTextWait.setText(R.string.wait);
		View layout = inflater.inflate(R.layout.fragment_detalhe_endereco, null);//container, false);
		txtNumero = (TextView) layout.findViewById(R.id.textNumero);
		txtNumero.setOnKeyListener(this);
		txtNome = (TextView) layout.findViewById(R.id.textNomeResultado);
		txtNome.setOnKeyListener(this);
		txtCep = (TextView) layout.findViewById(R.id.textCepResultado);
		txtCep.setOnKeyListener(this);
		txtLogradouro = (TextView) layout.findViewById(R.id.textLogradouro);
		txtLogradouro.setOnKeyListener(this);
		txtCidade = (TextView) layout.findViewById(R.id.textCidade);
		txtCidade.setOnKeyListener(this);
		txtBairro = (TextView) layout.findViewById(R.id.textBairro);
		txtBairro.setOnKeyListener(this);
		txtUF = (TextView) layout.findViewById(R.id.textUF);
		txtUF.setOnKeyListener(this);
		mBotao = (Button) layout.findViewById(R.id.btnPesquisar);
		mBotao.setOnClickListener(this);
		
		/*mBtnUpdateMap = (Button)layout.findViewById(R.id.btnUpdateMap);
		if (mBtnUpdateMap != null){
			mBtnUpdateMap.setOnClickListener(this);
		}
		mBtnUpdateMap.setVisibility(android.view.View.INVISIBLE);*/
		updateFilds(mEndereco);
		return layout;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		/*if (isTablet()){//setMapContainerAsVisible()){
			if (mEndereco != null && mGeoPoint != null){
				if (mEndereco.getLatitude() != null && mEndereco.getLongitude() != null)
					if (mEndereco.getLatitude() != mGeoPoint.getLatitude() 
								&& mEndereco.getLongitude() != mGeoPoint.getLongitude()){
						updateMap();
					}else
						buildMap();
			}else
				updateMap();//buildMap();
		}*/
	}
	

	
	/*private boolean setMapContainerAsVisible() {
		mMapContainer = getActivity().findViewById(R.id.map);
		if (mMapContainer != null){
			mMapContainer.setVisibility(android.view.View.VISIBLE);
			return true;
		}
		return false;
	}*/
		
	private void updateFilds(Endereco endereco) {
		txtNome.setText(endereco.getNome());
		txtCep.setText(endereco.getCep());
		txtBairro.setText(endereco.getBairro());
		txtCidade.setText(endereco.getCidade());
		txtLogradouro.setText(endereco.getLogradouro());
		txtNumero.setText(endereco.getNumero());
		txtUF.setText(endereco.getUf());
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(Parameters.EXTRA_ENDERECO, mEndereco);
		if (mGeoPoint != null)
			outState.putSerializable(Parameters.EXTRA_GEOPOINT, mGeoPoint);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		getActivity().getMenuInflater().inflate(R.menu.detalhe_endereco, menu);
		menu.findItem(R.id.action_delete).setVisible(mEndereco != null && mEndereco.getId() > 0);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_save){
			if (mEndereco != null){
				updateEndereco();
			}else
				mEndereco = new Endereco(txtNome.getText().toString(), txtCep.getText().toString());
			if (getActivity() instanceof IEnderecoLinkedOperationsListener){
				((IEnderecoLinkedOperationsListener)getActivity()).save(mEndereco);
			}
		}
		if (item.getItemId() == R.id.action_delete){
			if (getActivity() instanceof IEnderecoLinkedOperationsListener){
				((IEnderecoLinkedOperationsListener)getActivity()).delete(mEndereco);
			}
		}
		if (item.getItemId() == R.id.action_close){
			if (getActivity() instanceof IEnderecoLinkedOperationsListener){
				((IEnderecoLinkedOperationsListener)getActivity()).closeCancel();
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	

	public interface IEnderecoLinkedOperationsListener{
		void save(Endereco endereco);
		void delete(Endereco endereco);
		void closeCancel();
	}


	public void onClick(View v) {
		
		if(v.getId()==R.id.btnPesquisar){
			mEndereco.setCep(txtCep.getText().toString());
			if(txtCep.getText().toString().length() > 0){
				BuscaCepTask bcp = new BuscaCepTask(getActivity());
				bcp.execute();
                //MapaTask mapaTask = new MapaTask(getActivity());
                //mapaTask.execute();
			}
		}
		if (v.getId() == R.id.btnUpdateMap){
			updateMap();
		}
		
	}

    @Deprecated
	public void updateMap() {
		//new MapaTask(getActivity()).execute();

	}


	private void updateEndereco(){
		if (mEndereco == null)
			mEndereco = new Endereco();
		if (txtNome != null) {
			mEndereco.setNome(txtNome.getText().toString());
			mEndereco.setCep(txtCep.getText().toString());
			mEndereco.setBairro(txtBairro.getText().toString());
			mEndereco.setCidade(txtCidade.getText().toString());
			mEndereco.setLogradouro(txtLogradouro.getText().toString());
			mEndereco.setUf(txtUF.getText().toString());
			mEndereco.setNumero(txtNumero.getText().toString());
		}
	}
	
	public Endereco getEndereco(){
		return mEndereco;
	}
	
	

    

	
	private class BuscaCepTask extends AsyncTask<Void, String, Boolean>{

		private Endereco endereco;
		private ProgressDialog progress;
        private Context context;
 
        public BuscaCepTask(Context context){
            this.context = context;
        }
 
        @Override
        protected void onPreExecute()
        {
            //Cria novo um ProgressDialogo e exibe
            progress = new ProgressDialog(context);
            progress.setMessage(mTextWait.getText().toString());
            progress.show();
        }
		
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				endereco = ConsultaCepWeb.oberEndereco(mEndereco.getCep());
                endereco.setCep(mEndereco.getCep());
                //mGeoPoint = MapaHTTP.pegarCoordenadas(endereco);
                //mEndereco.setLatitude(mGeoPoint.getLatitude());
                //mEndereco.setLongitude(mGeoPoint.getLongitude());
				progress.dismiss();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {
				if (endereco == null || endereco.getResultado() == 0) {
					Toast.makeText(getActivity(), R.string.query_not_found_zip_code, Toast.LENGTH_LONG).show();
				} else {
					mEndereco.setLogradouro(endereco.getTipo_logradouro() + " "
							+ endereco.getLogradouro());
					mEndereco.setBairro(endereco.getBairro());
					mEndereco.setCidade(endereco.getCidade());
					mEndereco.setUf(endereco.getUf());
					txtBairro.setText(mEndereco.getBairro().toString());
					txtCidade.setText(mEndereco.getCidade().toString());
					txtLogradouro.setText(mEndereco.getLogradouro());
					txtUF.setText(mEndereco.getUf());
					if (isTablet()) {
                        Log.i("EnderecoDetalheFragment", "dispositivo = Tablet");
                        ((IAoAtualizarEndereco) getActivity()).onAposEnderecoAtualizado(mEndereco);
                    }else
                        Log.e("EnderecoDetalheFragment", "dispositivo = NÃO é um Tablet!!");
				}
			} else {
				if (progress != null && progress.isShowing())
					progress.dismiss();
				Toast.makeText(getActivity(), R.string.query_err_zip_code, Toast.LENGTH_SHORT).show();
			}

		}
		
		@Override
        protected void onProgressUpdate(String... values){
            //Atualiza mensagem
            progress.setMessage(values[0]);
        }
	}
	
	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		switch (v.getId()) {
		    case R.id.textNomeResultado:
			    mEndereco.setNome(txtNome.getText().toString());
			    break;
		    case R.id.textLogradouro:
			    mEndereco.setLogradouro(txtLogradouro.getText().toString());
			    break;
		    case R.id.textNumero:
			    mEndereco.setNumero(txtNumero.getText().toString());
                Log.i("txtNumero:", " = " + mEndereco.getNumero().toString());
			    break;
		    case R.id.textBairro:
			    mEndereco.setBairro(txtBairro.getText().toString());
			    break;
		    case R.id.textCidade:
			    mEndereco.setCidade(txtCidade.getText().toString());
			    break;
		    case R.id.textUF:
			    mEndereco.setUf(txtUF.getText().toString());
			    break;
		    case R.id.textCepResultado:
			    mEndereco.setCep(txtCep.getText().toString());
			    break;
		    default:
			    break;
		}
		return false;
	}

    public Endereco getmEndereco(){
        return mEndereco;
    }


    private boolean isTablet(){
        if (getActivity().findViewById(R.id.detalhe) != null){
            mMapContainer = getActivity().findViewById(R.id.mapa);
            if (mMapContainer != null)
                mMapContainer.setVisibility(android.view.View.VISIBLE);
            return true;
        }else
            return false;
    }

}
