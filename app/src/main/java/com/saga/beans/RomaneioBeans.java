package com.saga.beans;

/**
 * Created by Bruno Nogueira Silva on 03/05/2016.
 */
public class RomaneioBeans {

    private int idRomaneio, numero;
    private String guidRomaneio, dataAlt, dataRomaneio, dataEmissao, dataSaida, dataFechamento, observacaoRomaneio, situacao;
    private double valor;
    private AreasBeans areaRomaneio;
    private EmpresaBeans empresaRomaneio;
    private ClifoBeans clifoRomaneio;

    public int getIdRomaneio() {
        return idRomaneio;
    }

    public void setIdRomaneio(int idRomaneio) {
        this.idRomaneio = idRomaneio;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getGuidRomaneio() {
        return guidRomaneio;
    }

    public void setGuidRomaneio(String guidRomaneio) {
        this.guidRomaneio = guidRomaneio;
    }

    public String getDataAlt() {
        return dataAlt;
    }

    public void setDataAlt(String dataAlt) {
        this.dataAlt = dataAlt;
    }

    public String getDataRomaneio() {
        return dataRomaneio;
    }

    public void setDataRomaneio(String dataRomaneio) {
        this.dataRomaneio = dataRomaneio;
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

    public String getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(String dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    public String getObservacaoRomaneio() {
        return observacaoRomaneio;
    }

    public void setObservacaoRomaneio(String observacaoRomaneio) {
        this.observacaoRomaneio = observacaoRomaneio;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
