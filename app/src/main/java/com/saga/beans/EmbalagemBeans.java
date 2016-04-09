package com.saga.beans;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by Bruno Nogueira Silva on 05/02/2016.
 */
public class EmbalagemBeans implements KvmSerializable {

    private int idEmbalagem, idProduto, idUnidadeVenda, modulo, decimais;
    private String dataAlteracao, principal, descricaoEmbalagem, ativo, codigoBarras, referencia;
    private UnidadeVendaBeans unidadeVenda;
    private double fatorConversao, fatorPreco;

    public int getIdEmbalagem() {
        return idEmbalagem;
    }

    public void setIdEmbalagem(int idEmbalagem) {
        this.idEmbalagem = idEmbalagem;
    }

    public int getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    public int getIdUnidadeVenda() {
        return idUnidadeVenda;
    }

    public void setIdUnidadeVenda(int idUnidadeVenda) {
        this.idUnidadeVenda = idUnidadeVenda;
    }

    public int getModulo() {
        return modulo;
    }

    public void setModulo(int modulo) {
        this.modulo = modulo;
    }

    public int getDecimais() {
        return decimais;
    }

    public void setDecimais(int decimais) {
        this.decimais = decimais;
    }

    public String getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(String dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getDescricaoEmbalagem() {
        return descricaoEmbalagem;
    }

    public void setDescricaoEmbalagem(String descricaoEmbalagem) {
        this.descricaoEmbalagem = descricaoEmbalagem;
    }

    public String getAtivo() {
        return ativo;
    }

    public void setAtivo(String ativo) {
        this.ativo = ativo;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public UnidadeVendaBeans getUnidadeVenda() {
        return unidadeVenda;
    }

    public void setUnidadeVenda(UnidadeVendaBeans unidadeVenda) {
        this.unidadeVenda = unidadeVenda;
    }

    public double getFatorConversao() {
        return fatorConversao;
    }

    public void setFatorConversao(double fatorConversao) {
        this.fatorConversao = fatorConversao;
    }

    public double getFatorPreco() {
        return fatorPreco;
    }

    public void setFatorPreco(double fatorPreco) {
        this.fatorPreco = fatorPreco;
    }

    @Override
    public Object getProperty(int i) {
        switch (i){
            case 0:
                return idEmbalagem;
            case 1:
                return idProduto;
            case 2:
                return idUnidadeVenda;
            case 3:
                return modulo;
            case 4:
                return decimais;
            case 5:
                return dataAlteracao;
            case 6:
                return principal;
            case 7:
                return descricaoEmbalagem;
            case 8:
                return ativo;
            case 9:
                return codigoBarras;
            case 10:
                return referencia;
            case 11:
                return unidadeVenda;
            case 12:
                return fatorConversao;
            case 13:
                return fatorPreco;
        }

        return null;
    }

    @Override
    public int getPropertyCount() {
        return 12;
    }

    @Override
    public void setProperty(int i, Object o) {
        switch (i){
            case 0:
                this.idEmbalagem = Integer.parseInt(o.toString());
                break;

            case 1:
                this.idProduto = Integer.parseInt(o.toString());
                break;

            case 2:
                this.idUnidadeVenda = Integer.parseInt(o.toString());
                break;

            case 3:
                this.modulo = Integer.parseInt(o.toString());
                break;

            case 4:
                this.decimais = Integer.parseInt(o.toString());
                break;

            case 5:
                this.dataAlteracao = o.toString();
                break;

            case 6:
                this.principal = o.toString();
                break;

            case 7:
                this.descricaoEmbalagem = o.toString();
                break;

            case 8:
                this.ativo = o.toString();
                break;

            case 9:
                this.codigoBarras = o.toString();
                break;

            case 10:
                this.referencia = o.toString();
                break;

            case 11:
                this.unidadeVenda = (UnidadeVendaBeans) o;
                break;

            case 12:
                this.fatorConversao = Double.parseDouble(o.toString());
                break;

            case 13:
                this.fatorPreco = Double.parseDouble(o.toString());
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
                propertyInfo.name = "idEmbalagem";
                break;

            case 1:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "idProduto";
                break;

            case 2:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "idUnidadeVenda";
                break;

            case 3:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "modulo";
                break;

            case 4:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "decimais";
                break;

            case 5:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "dataAlteracao";
                break;

            case 6:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "principal";
                break;

            case 7:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "descricaoEmbalagem";
                break;

            case 8:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "ativo";
                break;

            case 9:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "codigoBarras";
                break;

            case 10:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "referencia";
                break;

            case 11:
                propertyInfo.type = UnidadeVendaBeans.class;
                propertyInfo.name = "unidadeVenda";
                break;

            case 12:
                propertyInfo.type = Double.class;
                propertyInfo.name = "fatorConversao";
                break;

            case 13:
                propertyInfo.type = Double.class;
                propertyInfo.name = "fatorPreco";
                break;

            default:
                break;
        }
    }
}
