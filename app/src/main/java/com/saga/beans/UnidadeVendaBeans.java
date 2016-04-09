package com.saga.beans;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by Bruno Nogueira Silva on 25/01/2016.
 */
public class UnidadeVendaBeans implements KvmSerializable {

    private int idUnidadeVenda, decimais;
    private String dataAlt, sigla, descricaoUnidadeVenda;

    public int getIdUnidadeVenda() {
        return idUnidadeVenda;
    }

    public void setIdUnidadeVenda(int idUnidadeVenda) {
        this.idUnidadeVenda = idUnidadeVenda;
    }

    public int getDecimais() {
        return decimais;
    }

    public void setDecimais(int decimais) {
        this.decimais = decimais;
    }

    public String getDataAlt() {
        return dataAlt;
    }

    public void setDataAlt(String dataAlt) {
        this.dataAlt = dataAlt;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getDescricaoUnidadeVenda() {
        return descricaoUnidadeVenda;
    }

    public void setDescricaoUnidadeVenda(String descricaoUnidadeVenda) {
        this.descricaoUnidadeVenda = descricaoUnidadeVenda;
    }

    @Override
    public Object getProperty(int i) {
        switch (i){
            case 0:
                return this.idUnidadeVenda;
            case 1:
                return this.decimais;
            case 2:
                return this.dataAlt;
            case 3:
                return this.sigla;
            case 4:
                return this.descricaoUnidadeVenda;
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
                this.idUnidadeVenda = Integer.parseInt(o.toString());
                break;

            case 1:
                this.decimais = Integer.parseInt(o.toString());
                break;

            case 2:
                this.dataAlt = o.toString();
                break;

            case 3:
                this.sigla = o.toString();
                break;

            case 4:
                this.descricaoUnidadeVenda = o.toString();
                break;
        }
    }
    //    private int idUnidadeVenda, decimais;
//    private String dataAlt, sigla, descricaoUnidadeVenda;
    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {
        switch (i){
            case 0:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "idUnidadeVenda";
                break;

            case 1:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "decimais";
                break;

            case 2:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "dataAlt";
                break;

            case 3:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "sigla";
                break;

            case 4:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "descricaoUnidadeVenda";
                break;
        }
    }
}
