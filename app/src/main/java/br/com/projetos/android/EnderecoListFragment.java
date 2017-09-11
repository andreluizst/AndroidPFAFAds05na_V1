package br.com.projetos.android;


import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class EnderecoListFragment extends ListFragment implements OnItemLongClickListener, Callback{
	public static final String EXTRA_LISTA = "lista";
	public static final String EXTRA_ACTION_MODE = "Action_Mode";
	private static final String EXTRA_ACTION_MODE_ACTIVATED = "action_mode_activated";
	private static final String EXTRA_CHECKED_ITEMS = "checked_items";
	
	private ArrayList<Endereco> mEnderecos;
	private EnderecoAdapter mAdapter;
	private RepositorioEndereco mRep;
	private String mFilter;;
	private ActionMode mActionMode;
	private TextView mSelectedText;
	private boolean mActionModeActivated;
	private ArrayList<Integer> mCheckedItems;
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mEnderecos = new ArrayList<Endereco>();
		mCheckedItems = new ArrayList<Integer>();
		mActionModeActivated = false;
		mRep = new RepositorioEndereco(getActivity());
		mFilter = "";
		mSelectedText = (TextView)LayoutInflater.from(getActivity()).inflate(android.R.layout.simple_list_item_1, null);
		mSelectedText.setVisibility(View.INVISIBLE);
		mSelectedText.setText(R.string.selected);
		
		
		if (savedInstanceState == null){
			/*if (rep.listar().size() == 0){
				rep.inserir(new Pessoa("Fulano", "De tal"));
				rep.inserir(new Pessoa("Beltrano", "Silva"));
				rep.inserir(new Pessoa("Cicrano", "Santos"));
			}*/
			mEnderecos.clear();
			mEnderecos.addAll(mRep.listar());
			
			mAdapter = new EnderecoAdapter(getActivity(), mEnderecos);//android.R.layout.simple_selectable_list_item, mEnderecos);
			setListAdapter(mAdapter);
			getListView().setOnItemLongClickListener(this);
			
		}else{
			
			mEnderecos = (ArrayList)savedInstanceState.getSerializable(EXTRA_LISTA);
			mAdapter = new EnderecoAdapter(getActivity(), mEnderecos);//android.R.layout.simple_list_item_1, mEnderecos);
			setListAdapter(mAdapter);
			getListView().setOnItemLongClickListener(this);
			
			//Log.i(null, "savedInstanceState != null....");
			mActionModeActivated = savedInstanceState.getBoolean(EXTRA_ACTION_MODE_ACTIVATED);
			mCheckedItems = (ArrayList)savedInstanceState.getSerializable(EXTRA_CHECKED_ITEMS);
			if (mActionModeActivated){
				((ActionBarActivity)getActivity()).startSupportActionMode(EnderecoListFragment.this);
				if (mCheckedItems.size() > 0){
					int itensCount = mCheckedItems.size();
					onItemLongClick(null, null, mCheckedItems.get(0), 0);
					for (int i = 1; i < mCheckedItems.size(); i++) {
						getListView().setItemChecked(mCheckedItems.get(i), true);
					}
					updateSelectdTextBySelectedCount(itensCount);
				}
			}
			mAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		
		if (mActionMode == null){
			super.onListItemClick(l, v, position, id);
			if (getActivity() instanceof AoClicarNoEnderecoListener){
				Endereco endereco = (Endereco)l.getItemAtPosition(position);
				((AoClicarNoEnderecoListener)getActivity()).clicouNoEndereco(endereco);
			}
		}else{
			int checkedCount = updateCheckedItems(l, position);
			if (checkedCount == 0){
				mActionMode.finish();
			}
		}
	}
	
	private int updateCheckedItems(ListView l, int position) {
		SparseBooleanArray checkedArray = l.getCheckedItemPositions();
		mCheckedItems.clear();
		l.setItemChecked(position, l.isItemChecked(position));
		int checkedCount = 0;
		for (int i = 0; i < checkedArray.size(); i++) {
			if (checkedArray.valueAt(i)){
				checkedCount++;
				mCheckedItems.add(i);
			}
		}
		updateSelectdTextBySelectedCount(checkedCount);
		return checkedCount;
	}


	private void updateSelectdTextBySelectedCount(int checkedCount) {
		if (checkedCount <= 1){
			mSelectedText.setText(R.string.selected);
			mActionMode.setTitle(checkedCount + " " + mSelectedText.getText().toString());
		}
		else{
			mSelectedText.setText(R.string.multiselected);
			mActionMode.setTitle(checkedCount + " " + mSelectedText.getText().toString());
		}
	}
	
	public interface AoClicarNoEnderecoListener{
		void clicouNoEndereco(Endereco endereco);
	}
	
	public void addEndereco(Endereco endereco){
		if (endereco.getId() == 0){
			//endereco.setId(mEnderecos.size()+1);
			//mEnderecos.add(endereco);
			mRep.inserir(endereco);
			mEnderecos.clear();
			mEnderecos.addAll(mRep.listar());
			mAdapter.notifyDataSetChanged();
		}
	}

	public void deleteEndereco(Endereco endereco) {
		if (endereco.getId()> 0){
			//mEnderecos.remove((endereco.getId()-1));
			mRep.excluir(endereco.getId());
			mEnderecos.clear();
			mEnderecos.addAll(mRep.listar());
			mAdapter.notifyDataSetChanged();
		}
	}

	public void updateEndereco(Endereco endereco) {
		if (endereco.getId() > 0){
			//mEnderecos.get(Integer.valueOf(String.valueOf(endereco.getId()-1))).setNome(endereco.getNome());
			//mEnderecos.get(Integer.valueOf(String.valueOf(endereco.getId()-1))).setCep(endereco.getCep());
			mRep.alterar(endereco);
			mEnderecos.clear();
			mEnderecos.addAll(mRep.listar());
			mAdapter.notifyDataSetChanged();
		}
	}
	
	public void setFilter(String text){
		mFilter = text.toLowerCase().replace("%", "").replace("*", "")
									.replace("delete", "")
									.replace("select", "")
									.replace("update", "")
									.replace("insert", "")
									.replace("where", "");
		//Log.i(null, "FILTRO = " + mFilter);
		mEnderecos.clear();
		mEnderecos.addAll(mRep.consultarPorNome(mFilter));
		mAdapter.notifyDataSetChanged();
	}
	
	public void clearFilter(){
		mFilter = "";
		mEnderecos.clear();
		mEnderecos.addAll(mRep.listar());
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putSerializable(EXTRA_LISTA, mEnderecos);
		outState.putBoolean(EXTRA_ACTION_MODE_ACTIVATED, mActionModeActivated);
		outState.putSerializable(EXTRA_CHECKED_ITEMS, mCheckedItems);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		boolean consumed = mActionMode == null;
		if (consumed){
			mActionMode = ((ActionBarActivity)getActivity()).startSupportActionMode(EnderecoListFragment.this);
			getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			mActionModeActivated = true;
			getListView().setItemChecked(position, true);
			updateCheckedItems(getListView(), position);
		}
		return consumed;
	}

	@Override
	public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
		if (menuItem.getItemId() == R.id.action_delete_list){
			SparseBooleanArray checkedItems = getListView().getCheckedItemPositions();
			for (int i = 0; i < checkedItems.size(); i++) {
				if (checkedItems.valueAt(i)){
					mRep.excluir(((Endereco)getListView().getItemAtPosition(checkedItems.keyAt(i))).getId());
				}
			}
			mActionMode.finish();
			refreshList();
			return true;
		}
		if (menuItem.getItemId() == R.id.action_cancel_delete){
			mActionMode.finish();
			return true;
		}
		return false;
	}
	
	private void refreshList() {
		mEnderecos.clear();
		if (mFilter == null || mFilter.length() == 0)
			mEnderecos.addAll(mRep.listar());
		else
			mEnderecos.addAll(mRep.consultarPorNome(mFilter));
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
		((ActionBarActivity)getActivity()).getMenuInflater().inflate(R.menu.menu_delete_list, menu);
		return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode arg0) {
		mActionMode = null;
		mActionModeActivated = false;
		getListView().clearChoices();
		((EnderecoAdapter)getListAdapter()).notifyDataSetChanged();
		getListView().setChoiceMode(ListView.CHOICE_MODE_NONE);
	}

	@Override
	public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
