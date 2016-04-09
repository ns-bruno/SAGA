package com.saga.beans;

/**
 * Created by Bruno Nogueira Silva on 25/01/2016.
 */
public class SubGrupoProdutoBeans {

    private int idSubgrupo, idGrupo, codigo;
    private String dataAlt, descricaoSubgrupo;


    public int getIdSubgrupo() {
        return idSubgrupo;
    }

    public void setIdSubgrupo(int idSubgrupo) {
        this.idSubgrupo = idSubgrupo;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
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

    public String getDescricaoSubgrupo() {
        return descricaoSubgrupo;
    }

    public void setDescricaoSubgrupo(String descricaoSubgrupo) {
        this.descricaoSubgrupo = descricaoSubgrupo;
    }
}
