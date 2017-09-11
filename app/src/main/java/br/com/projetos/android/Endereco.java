package br.com.projetos.android;

import java.io.Serializable;

public class Endereco implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8108599235128113624L;
	private long id;
	private String nome;
	private Integer resultado;
	private String resultado_txt;
	private String uf;
	private String cidade;
	private String bairro;
	private String tipo_logradouro;
	private String logradouro;
	private String cep;
	private String numero;
	private Double latitude;
	private Double longitude;
	

	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCep() {
		return cep;
	}
	public void setCep(String cep) {
		this.cep = cep;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Integer getResultado() {
		return resultado;
	}
	public void setResultado(Integer resultado) {
		this.resultado = resultado;
	}
	public String getResultado_txt() {
		return resultado_txt;
	}
	public void setResultado_txt(String resultado_txt) {
		this.resultado_txt = resultado_txt;
	}
	public String getUf() {
		return uf;
	}
	public void setUf(String uf) {
		this.uf = uf;
	}
	public String getCidade() {
		return cidade;
	}
	public void setCidade(String cidade) {
		this.cidade = cidade;
	}
	public String getBairro() {
		return bairro;
	}
	public void setBairro(String bairro) {
		this.bairro = bairro;
	}
	public String getTipo_logradouro() {
		return tipo_logradouro;
	}
	public void setTipo_logradouro(String tipo_logradouro) {
		this.tipo_logradouro = tipo_logradouro;
	}
	public String getLogradouro() {
		return logradouro;
	}
	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}
	
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	@Override
	public String toString() {
		return nome + " - " + tipo_logradouro + " " + logradouro + ", " + numero
                + " - " + bairro + " - " + cidade + " - " + uf + " - " + cep
				+ " [" + latitude +", " + longitude + "]";
	}
	
	
	
	public Endereco(Integer id,String nome, String cep) {
		super();
		this.id = id;
		this.nome = nome;
		this.cep = cep;
	}
	public Endereco() {
		super();
	}
	
	public Endereco(String nome, String cep) {
		super();
		this.nome = nome;
		this.cep = cep;
	}
	public Endereco(long id, String nome, String tipo_logradouro, String logradouro,
			String bairro, String cidade, String uf,
			String cep, String numero, Double latitude, Double longitude) {
		super();
		this.id = id;
		this.nome = nome;
		this.uf = uf;
		this.cidade = cidade;
		this.bairro = bairro;
		this.tipo_logradouro = tipo_logradouro;
		this.logradouro = logradouro;
		this.cep = cep;
		this.numero = numero;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public Endereco (Double latitude, Double longitude){
		this.latitude= latitude;
		this.longitude = longitude;
	}
	
}
