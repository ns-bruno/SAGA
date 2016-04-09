package com.saga.beans;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by Bruno Nogueira Silva on 08/03/2016.
 */
public class ConexaoFIrebirdBeans implements KvmSerializable{

    private String IPServidor;
    private String porta;
    private String localBanco;
    private String usuario;
    private String senha;
    private String certificado;

    public String getIPServidor() {
        return IPServidor;
    }

    public void setIPServidor(String IPServidor) {
        this.IPServidor = IPServidor;
    }

    public String getPorta() {
        return porta;
    }

    public void setPorta(String porta) {
        this.porta = porta;
    }

    public String getLocalBanco() {
        return localBanco;
    }

    public void setLocalBanco(String localBanco) {
        this.localBanco = localBanco;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCertificado() {
        return certificado;
    }

    public void setCertificado(String certificado) {
        this.certificado = certificado;
    }

    @Override
    public Object getProperty(int i) {

        switch (i){
            case 0:
                return this.IPServidor;

            case 1:
                return this.porta;

            case 2:
                return this.localBanco;

            case 3:
                return this.usuario;

            case 4:
                return this.senha;

            case 5:
                return this.certificado;

            default:
                break;
        }
        return null;
    }

    @Override
    public int getPropertyCount() {
        return 6;
    }

    @Override
    public void setProperty(int i, Object o) {
        switch (i){
            case 0:
                this.IPServidor = o.toString();
                break;

            case 1:
                this.porta = o.toString();
                break;

            case 2:
                this.localBanco = o.toString();
                break;

            case 3:
                this.usuario = o.toString();
                break;

            case 4:
                this.senha = o.toString();
                break;

            case 5:
                this.certificado = o.toString();
                break;
        }
    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {
        switch (i){
            case 0:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "IPServidor";
                break;

            case 1:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "porta";
                break;

            case 2:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "localBanco";
                break;

            case 3:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "usuario";
                break;

            case 4:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "senha";
                break;

            case 5:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "certificado";
                break;
        }
    }
}
