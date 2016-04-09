package com.saga.beans;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by Bruno Nogueira Silva on 25/01/2016.
 */
public class LocesBeans implements KvmSerializable{

    private int idLoces, idEmpresa, codigo;
    private String dataAlt, descricaoLoces, ativo, tipoVenda, guid;

    public int getIdLoces() {
        return idLoces;
    }

    public void setIdLoces(int idLoces) {
        this.idLoces = idLoces;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
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

    public String getDescricaoLoces() {
        return descricaoLoces;
    }

    public void setDescricaoLoces(String descricaoLoces) {
        this.descricaoLoces = descricaoLoces;
    }

    public String getAtivo() {
        return ativo;
    }

    public void setAtivo(String ativo) {
        this.ativo = ativo;
    }

    public String getTipoVenda() {
        return tipoVenda;
    }

    public void setTipoVenda(String tipoVenda) {
        this.tipoVenda = tipoVenda;
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
                return idLoces;
            case 1:
                return idEmpresa;
            case 2:
                return codigo;
            case 3:
                return dataAlt;
            case 4:
                return descricaoLoces;
            case 5:
                return ativo;
            case 6:
                return tipoVenda;
            case 7:
                return guid;
        }
        return null;
    }

    @Override
    public int getPropertyCount() {

        return 8;
    }

    @Override
    public void setProperty(int i, Object o) {

        switch (i){
            case 0:
                this.idLoces = Integer.parseInt(o.toString());
                break;
            case 1:
                this.idEmpresa = Integer.parseInt(o.toString());
                break;
            case 2:
                this.codigo = Integer.parseInt(o.toString());
                break;
            case 3:
                this.dataAlt = o.toString();
                break;
            case 4:
                this.descricaoLoces = o.toString();
                break;
            case 5:
                this.ativo = o.toString();
                break;
            case 6:
                this.tipoVenda = o.toString();
                break;
            case 7:
                this.guid = o.toString();
                break;
        }
    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {
//        private int idLoces, idEmpresa, codigo;
//        private String dataAlt, descricaoLoces, ativo, tipoVenda, guid;
        switch (i){
            case 0:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "idLoces";
                break;
            case 1:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "idEmpresa";
                break;
            case 2:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "codigo";
                break;
            case 3:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "dataAlt";
                break;
            case 4:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "descricaoLoces";
                break;
            case 5:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "ativo";
                break;
            case 6:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "tipoVenda";
                break;
            case 7:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "guid";
                break;
        }
    }
}
