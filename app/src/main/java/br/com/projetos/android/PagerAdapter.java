package br.com.projetos.android;

import java.net.HttpURLConnection;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.widget.Toast;

public class PagerAdapter extends FragmentPagerAdapter {
	
	private List<Fragment> fragments;
	private List<String> fragmentTitle;
	private Endereco mEndereco;
	
	public PagerAdapter(FragmentManager fm, List<Fragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}
	
	public PagerAdapter(FragmentManager fm, List<Fragment> fragments, List<String> fragmentTitle) {
		super(fm);
		this.fragments = fragments;
		this.fragmentTitle = fragmentTitle;
        this.fragmentTitle.add("Teste");
        this.fragments.add(SupportMapFragment.newInstance());
	}
	
	
	
	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
		/*if (position == 1){
			
			Log.w(null, "PagerAdapter.getItem...");
			
			Endereco endereco = ((EnderecoDetalheFragment)fragments.get(0)).getEndereco();
			
			endereco.setLogradouro("rua consul said adun");
			endereco.setNumero("126");
			endereco.setBairro("cohab");
			endereco.setCidade("recife");
			endereco.setUf("PE");
			endereco.setCep("51320240");
			mEndereco = endereco;
			new MapaTask().execute();
		
			
			//Log.i(null, mEndereco.toString());
			
			if (mEndereco == null)
				mEndereco = endereco;
			else{
				if (mEndereco != null && endereco != null && mEndereco.getLatitude() != null
						&& endereco.getLatitude() != null) {
					if (mEndereco.getLatitude() != endereco.getLatitude())
						mEndereco = endereco;
				}
			}
			
			if (endereco != null)
				Log.i(null, "Enderecço = " + endereco.toString());
			else
				Log.e(null, "Endereço é null");
			
			EnderecoMapFragment fragment = EnderecoMapFragment.novaInstancia(mEndereco);
			return fragment;
		}*/

		return fragments.get(position);
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		// TODO Auto-generated method stub
		return fragmentTitle.get(position);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return fragments.size();
	}
	
	public void setPageTitles(List<String> pageTitles) {
		this.fragmentTitle = pageTitles;
	}
	


}
