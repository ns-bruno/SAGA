package com.saga.beans;

/**
 * Created by Bruno Nogueira Silva on 20/01/2016.
 */
public class TipoClifoBeans {

    private int idTipoClifo, codigo;
    private String dataAlt, descricao, descontoPromocao, vendeAtacadoVarejo;
    private double descontoAtacadoVista, descontoAtacadoPrazo, descontoVarejoVista, descontoVarejoPrazo;

    public int getIdTipoClifo() {
        return idTipoClifo;
    }

    public void setIdTipoClifo(int idTipoClifo) {
        this.idTipoClifo = idTipoClifo;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescontoPromocao() {
        return descontoPromocao;
    }

    public void setDescontoPromocao(String descontoPromocao) {
        this.descontoPromocao = descontoPromocao;
    }

    public String getVendeAtacadoVarejo() {
        return vendeAtacadoVarejo;
    }

    public void setVendeAtacadoVarejo(String vendeAtacadoVarejo) {
        this.vendeAtacadoVarejo = vendeAtacadoVarejo;
    }

    public double getDescontoAtacadoVista() {
        return descontoAtacadoVista;
    }

    public void setDescontoAtacadoVista(double descontoAtacadoVista) {
        this.descontoAtacadoVista = descontoAtacadoVista;
    }

    public double getDescontoAtacadoPrazo() {
        return descontoAtacadoPrazo;
    }

    public void setDescontoAtacadoPrazo(double descontoAtacadoPrazo) {
        this.descontoAtacadoPrazo = descontoAtacadoPrazo;
    }

    public double getDescontoVarejoVista() {
        return descontoVarejoVista;
    }

    public void setDescontoVarejoVista(double descontoVarejoVista) {
        this.descontoVarejoVista = descontoVarejoVista;
    }

    public double getDescontoVarejoPrazo() {
        return descontoVarejoPrazo;
    }

    public void setDescontoVarejoPrazo(double descontoVarejoPrazo) {
        this.descontoVarejoPrazo = descontoVarejoPrazo;
    }
}
