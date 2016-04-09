package com.saga.beans;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by Bruno Nogueira Silva on 25/01/2016.
 */
public class ProdutoLojaBeans implements KvmSerializable {

    private int idProdutoLoja, idEmpresa;
    private ProdutoBeans produto;
    private String dataAlt, ativo;
    private double estoqueFisico, estoqueContabil, retido, pedido, vendaAtacado, vendaVarejo;
    private boolean tagSelectContext;

    public int getIdProdutoLoja() {
        return idProdutoLoja;
    }

    public void setIdProdutoLoja(int idProdutoLoja) {
        this.idProdutoLoja = idProdutoLoja;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public ProdutoBeans getProduto() {
        return produto;
    }

    public void setProduto(ProdutoBeans produto) {
        this.produto = produto;
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

    public double getEstoqueFisico() {
        return estoqueFisico;
    }

    public void setEstoqueFisico(double estoqueFisico) {
        this.estoqueFisico = estoqueFisico;
    }

    public double getEstoqueContabil() {
        return estoqueContabil;
    }

    public void setEstoqueContabil(double estoqueContabil) {
        this.estoqueContabil = estoqueContabil;
    }

    public double getRetido() {
        return retido;
    }

    public void setRetido(double retido) {
        this.retido = retido;
    }

    public double getPedido() {
        return pedido;
    }

    public void setPedido(double pedido) {
        this.pedido = pedido;
    }

    public double getVendaAtacado() {
        return vendaAtacado;
    }

    public void setVendaAtacado(double vendaAtacado) {
        this.vendaAtacado = vendaAtacado;
    }

    public double getVendaVarejo() {
        return vendaVarejo;
    }

    public void setVendaVarejo(double vendaVarejo) {
        this.vendaVarejo = vendaVarejo;
    }

    public boolean isTagSelectContext() {
        return tagSelectContext;
    }

    public void setTagSelectContext(boolean tagSelectContext) {
        this.tagSelectContext = tagSelectContext;
    }

    @Override
    public Object getProperty(int i) {


        switch (i){
            case 0:
                return idProdutoLoja;
            case 1:
                return idEmpresa;
            case 2:
                return produto;
            case 3:
                return dataAlt;
            case 4:
                return ativo;
            case 5:
                return estoqueFisico;
            case 6:
                return estoqueContabil;
            case 7:
                return retido;
            case 8:
                return pedido;
            case 9:
                return vendaAtacado;
            case 10:
                return vendaVarejo;
        }

        return null;
    }

    @Override
    public int getPropertyCount() {

        return 11;
    }

    @Override
    public void setProperty(int i, Object o) {

        switch (i){
            case 0:
                this.idProdutoLoja = Integer.parseInt(o.toString());
                break;
            case 1:
                this.idEmpresa = Integer.parseInt(o.toString());
                break;
            case 2:
                this.produto = (ProdutoBeans) o;
                break;
            case 3:
                this.dataAlt = o.toString();
                break;
            case 4:
                this.ativo = o.toString();
                break;
            case 5:
                this.estoqueFisico = Double.parseDouble(o.toString());
                break;
            case 6:
                this.estoqueContabil = Double.parseDouble(o.toString());
                break;
            case 7:
                this.retido = Double.parseDouble(o.toString());
                break;
            case 8:
                this.pedido = Double.parseDouble(o.toString());
                break;
            case 9:
                this.vendaAtacado = Double.parseDouble(o.toString());
                break;
            case 10:
                this.vendaVarejo = Double.parseDouble(o.toString());
                break;

            default:
                break;
        }
    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {

        switch (i){
            case 0:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "idProdutoLoja";
                break;
            case 1:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "idEmpresa";
                break;
            case 2:
                propertyInfo.type = ProdutoBeans.class;
                propertyInfo.name = "produto";
                break;
            case 3:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "dataAlt";
                break;
            case 4:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "ativo";
                break;
            case 5:
                propertyInfo.type = Double.class;
                propertyInfo.name = "estoqueFisico";
                break;
            case 6:
                propertyInfo.type = Double.class;
                propertyInfo.name = "estoqueContabil";
                break;
            case 7:
                propertyInfo.type = Double.class;
                propertyInfo.name = "retido";
                break;
            case 8:
                propertyInfo.type = Double.class;
                propertyInfo.name = "pedido";
                break;
            case 9:
                propertyInfo.type = Double.class;
                propertyInfo.name = "vendaAtacado";
                break;
            case 10:
                propertyInfo.type = Double.class;
                propertyInfo.name = "vendaVarejo";
                break;

            default:
                break;
        }

    }
}
