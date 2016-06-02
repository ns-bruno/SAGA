package com.saga.beans;

/**
 * Created by Bruno Nogueira Silva on 01/06/2016.
 */
public class ItemRomaneioBeans {

    private int idItemRomaneio, idRomaneio, seguencia;
    private String guidItemRomaneio, dataCad, dataAlt, conferido, situacao, obs;
    private SaidaBeans saida;
    private double valor;

    public int getIdItemRomaneio() {
        return idItemRomaneio;
    }

    public void setIdItemRomaneio(int idItemRomaneio) {
        this.idItemRomaneio = idItemRomaneio;
    }

    public int getIdRomaneio() {
        return idRomaneio;
    }

    public void setIdRomaneio(int idRomaneio) {
        this.idRomaneio = idRomaneio;
    }

    public int getSeguencia() {
        return seguencia;
    }

    public void setSeguencia(int seguencia) {
        this.seguencia = seguencia;
    }

    public String getGuidItemRomaneio() {
        return guidItemRomaneio;
    }

    public void setGuidItemRomaneio(String guidItemRomaneio) {
        this.guidItemRomaneio = guidItemRomaneio;
    }

    public String getDataCad() {
        return dataCad;
    }

    public void setDataCad(String dataCad) {
        this.dataCad = dataCad;
    }

    public String getDataAlt() {
        return dataAlt;
    }

    public void setDataAlt(String dataAlt) {
        this.dataAlt = dataAlt;
    }

    public String getConferido() {
        return conferido;
    }

    public void setConferido(String conferido) {
        this.conferido = conferido;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public SaidaBeans getSaida() {
        return saida;
    }

    public void setSaida(SaidaBeans saida) {
        this.saida = saida;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
