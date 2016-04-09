package com.saga.beans;

/**
 * Created by Bruno Nogueira Silva on 20/01/2016.
 */
public class CidadeBeans {

    private int idCidade;
    private EstadoBeans estado;
    private String dataAlt, descricaoCidade;

    public int getIdCidade() {
        return idCidade;
    }

    public void setIdCidade(int idCidade) {
        this.idCidade = idCidade;
    }

    public EstadoBeans getEstado() {
        return estado;
    }

    public void setEstado(EstadoBeans estado) {
        this.estado = estado;
    }

    public String getDataAlt() {
        return dataAlt;
    }

    public void setDataAlt(String dataAlt) {
        this.dataAlt = dataAlt;
    }

    public String getDescricaoCidade() {
        return descricaoCidade;
    }

    public void setDescricaoCidade(String descricaoCidade) {
        this.descricaoCidade = descricaoCidade;
    }
}
