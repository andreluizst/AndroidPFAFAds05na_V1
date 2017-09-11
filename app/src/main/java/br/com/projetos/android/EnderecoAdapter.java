package br.com.projetos.android;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;



public class EnderecoAdapter
	   extends ArrayAdapter<Endereco> {
	
	public EnderecoAdapter(Context context, List<Endereco> enderecos) {
		super(context, 0, enderecos);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		Endereco endereco = getItem(position);
		if (convertView == null){
			convertView = LayoutInflater.from(getContext())
					.inflate(R.layout.item_lista_endereco, parent, false);
			holder = new ViewHolder();
			holder.txtNome = (TextView)convertView.findViewById(R.id.textNomeResultado);
			holder.txtCep = (TextView)convertView.findViewById(R.id.textCepResultado);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		holder.txtNome.setText(endereco.getNome());
		holder.txtCep.setText(endereco.getCep());
		ListView listView = (ListView)parent;
		int color = listView.isItemChecked(position)?Color.RED:Color.TRANSPARENT;
		convertView.setBackgroundColor(color);
		return convertView;
	}
	
	 static class ViewHolder{
		TextView txtNome;
		TextView txtCep;
	}

}
