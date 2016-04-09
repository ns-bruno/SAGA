package com.saga.beans;

/**
 * Created by Bruno Nogueira Silva on 20/01/2016.
 */
public class StatusBeans {

    private int idStatus, codigo;
    private String descricaoStatus, mensagemStatus, bloqueia, parcelaEmAberto, vistaPrazo;

    public int getIdStatus() {
        return idStatus;
    }

    public void setIdStatus(int idStatus) {
        this.idStatus = idStatus;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getDescricaoStatus() {
        return descricaoStatus;
    }

    public void setDescricaoStatus(String descricaoStatus) {
        this.descricaoStatus = descricaoStatus;
    }

    public String getMensagemStatus() {
        return mensagemStatus;
    }

    public void setMensagemStatus(String mensagemStatus) {
        this.mensagemStatus = mensagemStatus;
    }

    public String getBloqueia() {
        return bloqueia;
    }

    public void setBloqueia(String bloqueia) {
        this.bloqueia = bloqueia;
    }

    public String getParcelaEmAberto() {
        return parcelaEmAberto;
    }

    public void setParcelaEmAberto(String parcelaEmAberto) {
        this.parcelaEmAberto = parcelaEmAberto;
    }

    public String getVistaPrazo() {
        return vistaPrazo;
    }

    public void setVistaPrazo(String vistaPrazo) {
        this.vistaPrazo = vistaPrazo;
    }
}
