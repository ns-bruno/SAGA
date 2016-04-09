package com.saga.beans;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by Bruno Nogueira Silva on 25/01/2016.
 */
public class EstoqueBeans implements KvmSerializable {

    private int idEstoque;
    private String dataAlt, ativo, locacaoAtiva, locacaoReserva, guid;
    private double estoque, retido;
    private ProdutoLojaBeans produtoLoja;
    private LocesBeans loces;

    public int getIdEstoque() {
        return idEstoque;
    }

    public void setIdEstoque(int idEstoque) {
        this.idEstoque = idEstoque;
    }

    public String getDataAlt() {
        return dataAlt;
    }

    public void setDataAlt(String dataAlt) {
        this.dataAlt = dataAlt;
    }

    public String getAtivo() {
        return ativo;
    }

    public void setAtivo(String ativo) {
        this.ativo = ativo;
    }

    public String getLocacaoAtiva() {
        return locacaoAtiva;
    }

    public void setLocacaoAtiva(String locacaoAtiva) {
        this.locacaoAtiva = locacaoAtiva;
    }

    public String getLocacaoReserva() {
        return locacaoReserva;
    }

    public void setLocacaoReserva(String locacaoReserva) {
        this.locacaoReserva = locacaoReserva;
    }

    public double getEstoque() {
        return estoque;
    }

    public void setEstoque(double estoque) {
        this.estoque = estoque;
    }

    public double getRetido() {
        return retido;
    }

    public void setRetido(double retido) {
        this.retido = retido;
    }

    public ProdutoLojaBeans getProdutoLoja() {
        return produtoLoja;
    }

    public void setProdutoLoja(ProdutoLojaBeans produtoLoja) {
        this.produtoLoja = produtoLoja;
    }

    public LocesBeans getLoces() {
        return loces;
    }

    public void setLoces(LocesBeans loces) {
        this.loces = loces;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    @Override
    public Object getProperty(int i) {
        switch (i){
            case 0:
                return idEstoque;
            case 1:
                return dataAlt;
            case 2:
                return ativo;
            case 3:
                return locacaoAtiva;
            case 4:
                return locacaoReserva;
            /*case 5:
                return guid;
            case 6:
                return estoque;
            case 7:
                return retido;
            case 8:
                return produtoLoja;
            case 9:
                return loces;*/
        }
        return null;
    }

    @Override
    public int getPropertyCount() {
        return 5;
    }

    @Override
    public void setProperty(int i, Object o) {

        switch (i){
            case 0:
                this.idEstoque = Integer.parseInt(o.toString());
                break;
            case 1:
                this.dataAlt = o.toString();
                break;
            case 2:
                this.ativo = o.toString();
                break;
            case 3:
                this.locacaoAtiva = o.toString();
                break;
            case 4:
                this.locacaoReserva = o.toString();
                break;
            /*case 5:
                this.guid = o.toString();
                break;
            case 6:
                this.estoque = Double.parseDouble(o.toString());
                break;
            case 7:
                this.retido = Double.parseDouble(o.toString());
                break;
            case 8:
                this.produtoLoja = (ProdutoLojaBeans) o;
                break;
            case 9:
                this.loces = (LocesBeans) o;
                break;*/

            default:
                break;
        }
    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {

        switch (i){
            case 0:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "idEstoque";
                break;
            case 1:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "dataAlt";
                break;
            case 2:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "ativo";
                break;
            case 3:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "locacaoAtiva";
                break;
            case 4:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "locacaoReserva";
                break;
            /*case 5:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "guid";
                break;
            case 6:
                propertyInfo.type = Double.class;
                propertyInfo.name = "estoque";
                break;
            case 7:
                propertyInfo.type = Double.class;
                propertyInfo.name = "retido";
                break;
            case 8:
                propertyInfo.type = ProdutoLojaBeans.class;
                propertyInfo.name = "produtoLoja";
                break;
            case 9:
                propertyInfo.type = LocesBeans.class;
                propertyInfo.name = "loces";
                break;*/

            default:
                break;
        }
    }
}
