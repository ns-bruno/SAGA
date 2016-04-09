package com.saga.beans;

/**
 * Created by Bruno Nogueira Silva on 19/01/2016.
 */
public class NotaFiscalEntradaBeans {

    private int idNotaFiscalEntrada, idEmpresa, idSerie, idNotaFiscalSaida, numeroEntrada, situacao, fechado;
    private String guid, dataAlteracao, tipoEntrada, serie, dataEmissao, dataSaida, dataEntrada, dataEstorno, tipoBaixa, observacao, chaveNfe, tipoEmissao, finalidade;
    private double valorMercadoria, valorTotalEntrada;
    private ClifoBeans clifo;
    private NaturezaBeans natureza;

    public int getIdNotaFiscalEntrada() {
        return idNotaFiscalEntrada;
    }

    public void setIdNotaFiscalEntrada(int idNotaFiscalEntrada) {
        this.idNotaFiscalEntrada = idNotaFiscalEntrada;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public int getIdSerie() {
        return idSerie;
    }

    public void setIdSerie(int idSerie) {
        this.idSerie = idSerie;
    }

    public int getIdNotaFiscalSaida() {
        return idNotaFiscalSaida;
    }

    public void setIdNotaFiscalSaida(int idNotaFiscalSaida) {
        this.idNotaFiscalSaida = idNotaFiscalSaida;
    }

    public int getNumeroEntrada() {
        return numeroEntrada;
    }

    public void setNumeroEntrada(int numeroEntrada) {
        this.numeroEntrada = numeroEntrada;
    }

    public int getSituacao() {
        return situacao;
    }

    public void setSituacao(int situacao) {
        this.situacao = situacao;
    }

    public int getFechado() {
        return fechado;
    }

    public void setFechado(int fechado) {
        this.fechado = fechado;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(String dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public String getTipoEntrada() {
        return tipoEntrada;
    }

    public void setTipoEntrada(String tipoEntrada) {
        this.tipoEntrada = tipoEntrada;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(String dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(String dataSaida) {
        this.dataSaida = dataSaida;
    }

    public String getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(String dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public String getDataEstorno() {
        return dataEstorno;
    }

    public void setDataEstorno(String dataEstorno) {
        this.dataEstorno = dataEstorno;
    }

    public String getTipoBaixa() {
        return tipoBaixa;
    }

    public void setTipoBaixa(String tipoBaixa) {
        this.tipoBaixa = tipoBaixa;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getChaveNfe() {
        return chaveNfe;
    }

    public void setChaveNfe(String chaveNfe) {
        this.chaveNfe = chaveNfe;
    }

    public String getTipoEmissao() {
        return tipoEmissao;
    }

    public void setTipoEmissao(String tipoEmissao) {
        this.tipoEmissao = tipoEmissao;
    }

    public String getFinalidade() {
        return finalidade;
    }

    public void setFinalidade(String finalidade) {
        this.finalidade = finalidade;
    }

    public double getValorMercadoria() {
        return valorMercadoria;
    }

    public void setValorMercadoria(double valorMercadoria) {
        this.valorMercadoria = valorMercadoria;
    }

    public double getValorTotalEntrada() {
        return valorTotalEntrada;
    }

    public void setValorTotalEntrada(double valorTotalEntrada) {
        this.valorTotalEntrada = valorTotalEntrada;
    }

    public ClifoBeans getClifo() {
        return clifo;
    }

    public void setClifo(ClifoBeans clifo) {
        this.clifo = clifo;
    }

    public NaturezaBeans getNatureza() {
        return natureza;
    }

    public void setNatureza(NaturezaBeans natureza) {
        this.natureza = natureza;
    }
}
