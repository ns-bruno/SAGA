package com.saga.beans;

/**
 * Created by Bruno Nogueira Silva on 25/01/2016.
 */
public class CodigoOrigemProdutoBeans {

    private int idCodigoOrigem, codigo;
    private String dataAlt, descricaoCodigoOrigem;

    public int getIdCodigoOrigem() {
        return idCodigoOrigem;
    }

    public void setIdCodigoOrigem(int idCodigoOrigem) {
        this.idCodigoOrigem = idCodigoOrigem;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getDataAlt() {
        return dataAlt;
    }

    public void setDataAlt(String dataAlt) {
        this.dataAlt = dataAlt;
    }

    public String getDescricaoCodigoOrigem() {
        return descricaoCodigoOrigem;
    }

    public void setDescricaoCodigoOrigem(String descricaoCodigoOrigem) {
        this.descricaoCodigoOrigem = descricaoCodigoOrigem;
    }
}
