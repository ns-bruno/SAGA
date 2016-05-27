package com.saga.beans;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by Bruno Nogueira Silva on 17/05/2016.
 */
public class ConferenciaItemBeans implements KvmSerializable {

    private int idItemConferencia, idItemsaida, idItemRomaneio, idItemEntrada;
    private String guid, dataAlt, dataConferencia, observacao, situacao, baixaPorConferencia;
    private long quantidade;

    public int getIdItemConferencia() {
        return idItemConferencia;
    }

    public void setIdItemConferencia(int idItemConferencia) {
        this.idItemConferencia = idItemConferencia;
    }

    public int getIdItemsaida() {
        return idItemsaida;
    }

    public void setIdItemsaida(int idItemsaida) {
        this.idItemsaida = idItemsaida;
    }

    public int getIdItemRomaneio() {
        return idItemRomaneio;
    }

    public void setIdItemRomaneio(int idItemRomaneio) {
        this.idItemRomaneio = idItemRomaneio;
    }

    public int getIdItemEntrada() {
        return idItemEntrada;
    }

    public void setIdItemEntrada(int idItemEntrada) {
        this.idItemEntrada = idItemEntrada;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getDataAlt() {
        return dataAlt;
    }

    public void setDataAlt(String dataAlt) {
        this.dataAlt = dataAlt;
    }

    public String getDataConferencia() {
        return dataConferencia;
    }

    public void setDataConferencia(String dataConferencia) {
        this.dataConferencia = dataConferencia;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getBaixaPorConferencia() {
        return baixaPorConferencia;
    }

    public void setBaixaPorConferencia(String baixaPorConferencia) {
        this.baixaPorConferencia = baixaPorConferencia;
    }

    public long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(long quantidade) {
        this.quantidade = quantidade;
    }

    @Override
    public Object getProperty(int i) {

        switch (i){
            case 0:
                return idItemConferencia;
            case 1:
                return idItemsaida;
            case 2:
                return idItemRomaneio;
            case 3:
                return idItemEntrada;
            case 4:
                return guid;
            case 5:
                return dataAlt;
            case 6:
                return dataConferencia;
            case 7:
                return observacao;
            case 8:
                return situacao;
            case 9:
                return baixaPorConferencia;
            case 10:
                return quantidade;
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
                this.idItemConferencia = Integer.parseInt(o.toString());
                break;

            case 1:
                this.idItemsaida = Integer.parseInt(o.toString());
                break;

            case 2:
                this.idItemRomaneio = Integer.parseInt(o.toString());
                break;

            case 3:
                this.idItemEntrada = Integer.parseInt(o.toString());
                break;

            case 4:
                this.guid = o.toString();
                break;

            case 5:
                this.dataAlt = o.toString();
                break;

            case 6:
                this.dataConferencia = o.toString();
                break;

            case 7:
                this.observacao = o.toString();
                break;

            case 8:
                this.situacao = o.toString();
                break;

            case 9:
                this.baixaPorConferencia = o.toString();
                break;

            case 10:
                this.quantidade = Long.parseLong(o.toString());
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
                propertyInfo.name = "idItemConferencia";
                break;

            case 1:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "idItemsaida";
                break;

            case 2:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "idItemRomaneio";
                break;

            case 3:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "idItemEntrada";
                break;

            case 4:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "guid";
                break;

            case 5:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "dataAlt";
                break;

            case 6:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "dataConferencia";
                break;

            case 7:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "observacao";
                break;

            case 8:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "situacao";
                break;

            case 9:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "baixaPorConferencia";
                break;

            case 10:
                propertyInfo.type = PropertyInfo.LONG_CLASS;
                propertyInfo.name = "quantidade";
                break;

            default:
                break;
        }
    }
}
