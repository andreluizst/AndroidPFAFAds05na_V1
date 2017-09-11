package br.com.projetos.android;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import br.com.projetos.android.EnderecoDetalheFragment.IEnderecoLinkedOperationsListener;
import br.com.projetos.android.EnderecoDetalheFragment.IAoAtualizarEndereco;
import br.com.projetos.android.EnderecoListFragment.AoClicarNoEnderecoListener;


public class EnderecoActivity extends ActionBarActivity implements AoClicarNoEnderecoListener, 
		IEnderecoLinkedOperationsListener, OnQueryTextListener, IAoAtualizarEndereco {
	private final String DETALHE_TAG = "tagDetalhe";
    private final String MAPA_TAG = "tagMapa";
    private final String VISIBILIDADE_DO_MAPA_EXTRA = "visibilidadeDoMapaExtra";
	SearchView mSearchView;
	MenuItem mFilterMenuItem;
	MenuItem mClearFilterMenuItem;
	String mTextFilter = "";
	Endereco mEndereco;
	GeoPoint mGeoPoint;
	View mMapFragment;
    private boolean mVisibilidadeDoMapa;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endereco);
        //this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (savedInstanceState != null){
            mVisibilidadeDoMapa = savedInstanceState.getBoolean(VISIBILIDADE_DO_MAPA_EXTRA);
            if (mVisibilidadeDoMapa)
                setMapFragmentAsVisible();
            else
                setMapFragmentAsInvisible();
        }else{
            setMapFragmentAsInvisible();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(VISIBILIDADE_DO_MAPA_EXTRA, mVisibilidadeDoMapa);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.endereco, menu);
    	mFilterMenuItem = (MenuItem)menu.findItem(R.id.filter);
    	mSearchView = (SearchView)MenuItemCompat.getActionView(mFilterMenuItem);
    	mSearchView.setQueryHint(mFilterMenuItem.getTitle());
    	mSearchView.setOnQueryTextListener(this);
    	mClearFilterMenuItem = (MenuItem)menu.findItem(R.id.action_clear_filter);
    	mClearFilterMenuItem.setVisible(false);
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if (item.getItemId() == R.id.action_new){
    		clicouNoEndereco(new Endereco());
    	}
    	if (item.getItemId() == R.id.action_clear_filter){
    		getListFragment().clearFilter();
    		mClearFilterMenuItem.setVisible(false);
    	}
    	return super.onOptionsItemSelected(item);
    }

	@Override
	public void clicouNoEndereco(Endereco endereco) {
		if (isTablet()){
			mEndereco = new Endereco(endereco.getId(), endereco.getNome(), endereco.getTipo_logradouro(), endereco.getLogradouro(),
									endereco.getBairro(), endereco.getCidade(), endereco.getUf(),
									endereco.getCep(), endereco.getNumero(), endereco.getLatitude(), endereco.getLongitude());
			FragmentManager fm;
			FragmentTransaction ft;
			EnderecoDetalheFragment fragment = EnderecoDetalheFragment.novaInstancia(mEndereco);
			fm = getSupportFragmentManager();
			ft = fm.beginTransaction();
			ft.replace(R.id.detalhe, fragment, DETALHE_TAG);
            EnderecoMapFragment fragmentMapa = EnderecoMapFragment.novaInstancia(mEndereco);
            ft.replace(R.id.mapa, fragmentMapa, MAPA_TAG);
			ft.commit();
            if (endereco.getId() == 0)
                setMapFragmentAsInvisible();
            else
			    setMapFragmentAsVisible();
		}else{
			Intent it = new Intent(this, EnderecoDetalheActivity.class);
			it.putExtra(Parameters.EXTRA_ENDERECO, endereco);
			startActivityForResult(it, 0);
		}
	}
	
	
	private void setMapFragmentAsVisible() {
		mMapFragment = (View)findViewById(R.id.mapa);
		if (mMapFragment != null) {
            mMapFragment.setVisibility(android.view.View.VISIBLE);
            mVisibilidadeDoMapa = true;
        }
	}
	
	private void setMapFragmentAsInvisible() {
		mMapFragment = (View)findViewById(R.id.mapa);
		if (mMapFragment != null) {
            mMapFragment.setVisibility(android.view.View.INVISIBLE);
            mVisibilidadeDoMapa = false;
        }
	}


	private boolean isTablet(){
		return findViewById(R.id.detalhe) != null;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0 && resultCode == RESULT_OK){
			Endereco endereco = (Endereco)data.getSerializableExtra(Parameters.EXTRA_ENDERECO);
			saveEndereco(endereco);
		}
		if (requestCode == 0 && resultCode == RESULT_FIRST_USER){
			Endereco endereco = (Endereco)data.getSerializableExtra(Parameters.EXTRA_ENDERECO);
			deleteEndereco(endereco);
		}
	}
	
	private EnderecoListFragment getListFragment(){
		FragmentManager fm = getSupportFragmentManager();
		EnderecoListFragment lista = (EnderecoListFragment)fm.findFragmentById(R.id.fragment1);
		return lista;
	}
	
	private void saveEndereco(Endereco endereco){
		EnderecoListFragment lista = getListFragment();
		if (endereco.getId() == 0)
			lista.addEndereco(endereco);
		else
			lista.updateEndereco(endereco);
	}
	
	private void deleteEndereco(Endereco endereco){
		EnderecoListFragment lista = getListFragment();
		lista.deleteEndereco(endereco);
	}
	
	private void closeDetailFragment(){
		FragmentManager fm;
		FragmentTransaction ft;
		setMapFragmentAsInvisible();
		fm = getSupportFragmentManager();
		ft = fm.beginTransaction();
		EnderecoDetalheFragment detalhe = (EnderecoDetalheFragment)fm.findFragmentByTag(DETALHE_TAG);
        ft.remove(detalhe);
        EnderecoMapFragment fragmentMapa = (EnderecoMapFragment)fm.findFragmentByTag(MAPA_TAG);
		ft.remove(fragmentMapa);
		ft.commit();
	}

	@Override
	public void save(Endereco endereco) {
		saveEndereco(endereco);
		closeDetailFragment();
	}

	@Override
	public void delete(Endereco endereco) {
		deleteEndereco(endereco);
		closeDetailFragment();
	}

	@Override
	public void closeCancel() {
			closeDetailFragment();
	}

	@Override
	public boolean onQueryTextChange(String text) {
		mTextFilter = text;
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String text) {
		getListFragment().setFilter(text);
		mClearFilterMenuItem.setVisible(text.length() > 0 && !text.equals("*") && !text.equals("%"));
		MenuItemCompat.collapseActionView(mFilterMenuItem);
		return true;
	}

    @Override
    public void onAposEnderecoAtualizado(Endereco endereco) {
        Log.i("EnderecoActivity", "onAposEnderecoAtualizado...");
        FragmentManager fm = getSupportFragmentManager();
        EnderecoMapFragment fragment = (EnderecoMapFragment)fm.findFragmentByTag(MAPA_TAG);
        fragment.mostrarEnderecoNoMapaTablet(endereco);
        setMapFragmentAsVisible();
    }
}
