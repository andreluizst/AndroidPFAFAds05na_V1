package br.com.projetos.android;


import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import br.com.projetos.android.EnderecoDetalheFragment.IEnderecoLinkedOperationsListener;


public class EnderecoDetalheActivity extends ActionBarActivity implements IEnderecoLinkedOperationsListener, TabListener{
	private static final String EXTRA_LIST_FRAGMENT = "extra_list_fragment";
	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;
	private ActionBar mActionBar;
	private ArrayList<Fragment> mListFragments;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detalhe_endereco);
		
		mListFragments = new ArrayList<Fragment>();
		mActionBar = getSupportActionBar();
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mPager = (ViewPager)findViewById(R.id.pager);
		Endereco endereco =(Endereco)getIntent().getSerializableExtra(Parameters.EXTRA_ENDERECO);
		if (savedInstanceState == null) {
			//Log.w(null, "EnderecoDetalheActivity.onCreate....instanciando EnderecoDetalheFragment...");
			EnderecoDetalheFragment fragment = EnderecoDetalheFragment.novaInstancia(endereco);
			mListFragments.add(fragment);
			/*FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.replace(R.id.detalhe, fragment, "tagDetalhe");
			ft.commit();*/
			EnderecoMapFragment mapFragment = EnderecoMapFragment.novaInstancia(endereco);
			mListFragments.add(mapFragment);
		}
		if (savedInstanceState != null) {
			mListFragments = (ArrayList)savedInstanceState.getSerializable(EXTRA_LIST_FRAGMENT);
        }
		
		mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), mListFragments);
		mPager.setAdapter(mPagerAdapter);
		mPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				mActionBar.setSelectedNavigationItem(position);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
			}
		});

		List<String> pageTitles = new ArrayList<String>();
		Tab tab1 = mActionBar.newTab();
		tab1.setText(R.string.address);
		tab1.setTabListener(this);
		pageTitles.add(tab1.getText().toString());
		Tab tab2 = mActionBar.newTab();
		tab2.setText(R.string.map);
		tab2.setTabListener(this);
		pageTitles.add(tab2.getText().toString());
		mPagerAdapter.setPageTitles(pageTitles);
		mActionBar.addTab(tab1);
		mActionBar.addTab(tab2);
	}
	
	public void setFragmetNaAba2(Fragment fragment){
		mListFragments.remove(1);
		mListFragments.add(fragment);
		mPagerAdapter.notifyDataSetChanged();
	}

	@Override
	public void save(Endereco endereco) {
		if (endereco.getNome() == null || endereco.getNome().length() < 3)
			return;
		Intent it = new Intent();
		it.putExtra(Parameters.EXTRA_ENDERECO, endereco);
		setResult(RESULT_OK, it);
		finish();
	}

	@Override
	public void delete(Endereco endereco) {
		Intent it = new Intent();
		it.putExtra(Parameters.EXTRA_ENDERECO, endereco);
		setResult(RESULT_FIRST_USER, it);
		finish();
	}

	@Override
	public void closeCancel() {
		Intent it = new Intent();
		setResult(RESULT_CANCELED, it);
		finish();
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction arg1) {
		//Log.w(null, "onTabSelected........");
		//Log.i(null, "Tag-" + tab.getPosition() + " clicada...");
		if (tab.getPosition() == 1){
			//((EnderecoDetalheFragment)mPagerAdapter.getItem(0)).updateMap();
            Endereco endereco = ((EnderecoDetalheFragment)mPagerAdapter.getItem(0)).getEndereco();
            if (mPagerAdapter.getItem(1) == null)
                Log.e("EnderecoDetalheActivity", "onTabSelected...mPagerAdapter.get(1)==NULL!!!!");
            else
                ((EnderecoMapFragment)mPagerAdapter.getItem(1)).mostrarEnderecoNoMapa(this, endereco);
		}
		mPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putSerializable(EXTRA_LIST_FRAGMENT, mListFragments);
	}

    public interface updateMapCoordinate{
		void setCoordinateToAddress(Endereco endereco);
	}
	
	
}
