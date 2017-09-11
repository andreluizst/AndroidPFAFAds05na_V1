package br.com.projetos.android;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class RepositorioEndereco {
	private SQLHelperEndereco helper;
	private static final String TABLE_NAME = "endereco";
	private static final String COLUNA_ID = "_id";
	private static final String COLUNA_NOME = "nome";
	private static final String COLUNA_TIPO_LOGRADOURO = "tipo_logradouro";
	private static final String COLUNA_LOGRADOURO = "logradouro";
	private static final String COLUNA_BAIRRO = "bairro";
	private static final String COLUNA_CIDADE = "cidade";
	private static final String COLUNA_UF = "uf";
	private static final String COLUNA_CEP = "cep";
	private static final String COLUNA_NUMERO = "numero";
	private static final String COLUNA_LATITUDE = "latitude";
	private static final String COLUNA_LONGITUDE = "longitude";
	
	
	
	public RepositorioEndereco(Context context){
		helper = new SQLHelperEndereco(context);
	}
	
	public long inserir(Endereco endereco){
		SQLiteDatabase db = helper.getWritableDatabase();
		long id = db.insert(TABLE_NAME,null, valuesFromEndereco(endereco));
		endereco.setId(id);
		db.close();
		return id;
	}
	
	public int alterar(Endereco endereco){
		SQLiteDatabase db = helper.getWritableDatabase();
		int linhasAfetadas = db.update(TABLE_NAME, valuesFromEndereco(endereco), "_id = ?", new String[] {String.valueOf(endereco.getId())});
		db.close();
		return linhasAfetadas;
	}
	
	public int excluir(long id){
		SQLiteDatabase db = helper.getWritableDatabase();
		int linhasAfetadas = db.delete(TABLE_NAME, "_id = " + id, null);
		db.close();
		return linhasAfetadas;
	}
	
	public List<Endereco> listar(){
		//Log.w(null, "RepositorioEndereco.listar()...");
		List<Endereco> lista = new ArrayList<Endereco>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " order by "+ COLUNA_NOME, null);
		while (cursor.moveToNext()){
			lista.add(cursorToEndereco(cursor));
		}
		cursor.close();
		db.close();
		//Log.i(null, "RepositorioEndereco.listar()...OK");
		return lista;
	}

	private ContentValues valuesFromEndereco(Endereco endereco) {
		ContentValues values = new ContentValues();
		//substuir o código abaixo pelo correto
		values.put(RepositorioEndereco.COLUNA_NOME, endereco.getNome());
		values.put(COLUNA_TIPO_LOGRADOURO, endereco.getTipo_logradouro());
		values.put(COLUNA_LOGRADOURO, endereco.getLogradouro());
		values.put(COLUNA_BAIRRO, endereco.getBairro());
		values.put(COLUNA_CIDADE, endereco.getCidade());
		values.put(COLUNA_UF, endereco.getUf());
		values.put(RepositorioEndereco.COLUNA_CEP, endereco.getCep());
		values.put(COLUNA_NUMERO, endereco.getNumero());
		values.put(COLUNA_LATITUDE, endereco.getLatitude());
		values.put(COLUNA_LONGITUDE, endereco.getLongitude());
		return values;
	}
	
	private Endereco cursorToEndereco(Cursor cursor){
		return new Endereco(
				cursor.getLong(cursor.getColumnIndex(COLUNA_ID)),
				cursor.getString(cursor.getColumnIndex(COLUNA_NOME)),
				cursor.getString(cursor.getColumnIndex(COLUNA_TIPO_LOGRADOURO)),
				cursor.getString(cursor.getColumnIndex(COLUNA_LOGRADOURO)),
				cursor.getString(cursor.getColumnIndex(COLUNA_BAIRRO)),
				cursor.getString(cursor.getColumnIndex(COLUNA_CIDADE)),
				cursor.getString(cursor.getColumnIndex(COLUNA_UF)),
				cursor.getString(cursor.getColumnIndex(COLUNA_CEP)),
				cursor.getString(cursor.getColumnIndex(COLUNA_NUMERO)),
				cursor.getDouble(cursor.getColumnIndex(COLUNA_LATITUDE)),
				cursor.getDouble(cursor.getColumnIndex(COLUNA_LONGITUDE))
				);
	}

	public List<Endereco> consultarPorNome(String mFilter) {
		List<Endereco> lista = new ArrayList<Endereco>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where " + COLUNA_NOME + " like ? order by " + COLUNA_NOME, 
				new String[] {"%"+mFilter+"%"});
		while (cursor.moveToNext()){
			lista.add(cursorToEndereco(cursor));
		}
		cursor.close();
		db.close();
		return lista;
	}
}
