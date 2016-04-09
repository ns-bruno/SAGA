package com.saga.beans;

/**
 * Created by Bruno Nogueira Silva on 25/01/2016.
 */
public class MarcaBeans {

    private int idMarca;
    private ClifoBeans clifo;
    private String dataAlt, descricao;

    public int getIdMarca() {
        return idMarca;
    }

    public void setIdMarca(int idMarca) {
        this.idMarca = idMarca;
    }

    public ClifoBeans getClifo() {
        return clifo;
    }

    public void setClifo(ClifoBeans clifo) {
        this.clifo = clifo;
    }

    public String getDataAlt() {
        return dataAlt;
    }

    public void setDataAlt(String dataAlt) {
        this.dataAlt = dataAlt;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
