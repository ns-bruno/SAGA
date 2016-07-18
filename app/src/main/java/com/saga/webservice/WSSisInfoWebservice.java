package com.saga.webservice;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.SweepGradient;
import android.os.Handler;

import com.saga.beans.ConexaoFIrebirdBeans;
import com.saga.beans.EmbalagemBeans;
import com.saga.beans.RetornoWebServiceBeans;
import com.saga.configuracao.ServicoWeb;
import com.saga.funcoes.FuncoesPersonalizadas;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

/**
 * Created by Bruno Nogueira Silva on 08/03/2016.
 */
public class WSSisInfoWebservice {

    private Context context;
    private ConexaoFIrebirdBeans conexaoFIrebirdBeans;
    public static final String FUNCTION_INSERT_EMBALAGEM_PRODUTO = "inserirEmbalagemProduto";
    public static final String FUNCTION_UPDATE_EMBALAGEM_PRODUTO = "updateEmbalagemProduto";
    public static final String FUNCTION_SELECT_LISTA_NOTA_FISCAL_ENTRADA = "selectNotaFiscalEntrada";
    public static final String FUNCTION_SELECT_LISTA_ITEM_NOTA_FISCAL_ENTRADA = "selectItemNotaFiscalEntrada";
    public static final String FUNCTION_SELECT_LISTA_ROMANEIO = "selectRomaneio";
    public static final String FUNCTION_SELECT_LISTA_ITEM_ROMANEIO = "selectItemRomaneio";
    public static final String FUNCTION_SELECT_LISTA_SAIDA = "selectSaida";
    public static final String FUNCTION_SELECT_LISTA_ITEM_SAIDA = "selectItemSaida";
    public static final String FUNCTION_SELECT_LISTA_ORCAMENTO = "selectOrcamento";
    public static final String FUNCTION_SELECT_LISTA_ITEM_ORCAMENTO = "selectItemOrcamento";
    public static final String FUNCTION_CHECA_USUARIO_SENHA = "checaUsuarioSenha";
    public static final String FUNCTION_SELECT_LISTA_EMBALAGEM = "selectListaEmbalagem";
    public static final String FUNCTION_DADOS_PRODUTOS_RESUMIDOS = "selectProdutoResumido";
    public static final String FUNCTION_DADOS_PRODUTOS_LOJA_RESUMIDOS = "selectProdutoLojaResumido";
    public static final String FUNCTION_DADOS_PRODUTOS_RESUMIDOS_ITEM_ENTRADA = "selectProdutoResumidoIdItemEntrada";
    public static final String FUNCTION_SELECT_LISTA_UNIDADE_VENDA = "selectListaUnidadeVenda";
    public static final String FUNCTION_SELECT_LISTA_ESTOQUE = "selectListaEstoque";
    public static final String FUNCTION_UPDATE_LOCACAO_ESTOQUE = "updateLocacaoEstoque";
    public static final String FUNCTION_SELECT_LISTA_LOCES = "selectListaLoces";
    public static final String FUNCTION_UPDATE_PRODUTO = "updateNotaFiscalEntrada";
    public static final String FUNCTION_UPDATE_NOTA_FISCAL_ENTRADA = "updateNotaFiscalEntrada";
    public static final String FUNCTION_INSERT_CONFERENCIA_ITEM = "inserirConferenciaItem";


    public WSSisInfoWebservice(Context context) {
        this.context = context;

        FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

        conexaoFIrebirdBeans = new ConexaoFIrebirdBeans();
        conexaoFIrebirdBeans.setIPServidor(((!funcoes.getValorXml("IPServidor").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? funcoes.getValorXml("IPServidor") : "localhost"));
        conexaoFIrebirdBeans.setLocalBanco(((!funcoes.getValorXml("NomeBancoDadosServidor").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? funcoes.getValorXml("NomeBancoDadosServidor") : "C:\\si.fir"));
        conexaoFIrebirdBeans.setPorta((!funcoes.getValorXml("PortaServidor").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? funcoes.getValorXml("PortaServidor") : "3050");
        conexaoFIrebirdBeans.setUsuario((!funcoes.getValorXml("UsuarioServidor").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? funcoes.getValorXml("UsuarioServidor") : "");
        conexaoFIrebirdBeans.setSenha((!funcoes.getValorXml("SenhaServidor").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? funcoes.getValorXml("SenhaServidor") : "");
        conexaoFIrebirdBeans.setCertificado((!funcoes.getValorXml("Certificado").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? funcoes.getValorXml("Certificado") : "");
    }

    public RetornoWebServiceBeans executarWebservice(PropertyInfo propriedades, String funcao){
        RetornoWebServiceBeans retorno = new RetornoWebServiceBeans();
        try {
            SoapObject soap = new SoapObject(ServicoWeb.WS_NAME_SPACE, funcao);

            if (propriedades != null) {
                soap.addProperty(propriedades);
            }
            // Definicao da versao do protocolo do webservice, a forma de como os dados serao enviados
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            // Adiciona os objetos/propriedades
            envelope.setOutputSoapObject(soap);

            FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

            String ipServidor = funcoes.getValorXml("IPServidorWebservice");

            // Checa se retornou algum endereco de ip do servidor
            if ((ipServidor == null) || (ipServidor.length() <= 1) || (ipServidor.equalsIgnoreCase(funcoes.NAO_ENCONTRADO))){
                ipServidor = "localhost";
            }
            String enderecoWebService = "http://" + ipServidor + ":8080/" + ServicoWeb.WS_ENDERECO_WEBSERVICE;

            HttpTransportSE httpTransporte = new HttpTransportSE(enderecoWebService, 50000);

            // Requisicao dos dados
            httpTransporte.call(funcao, envelope);

            SoapObject response;

            if (envelope.getResponse() != null) {
                response = (SoapObject) envelope.getResponse();
                retorno.setCodigoRetorno(Integer.parseInt(response.getPropertyAsString("codigoRetorno")));
                retorno.setMensagemRetorno(response.getPropertyAsString("mensagemRetorno"));
                retorno.setExtra(response.getProperty("extra"));
            } else {
                return null;
            }
            return retorno;

        } catch (IOException e) {
            //e.printStackTrace();

            final FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

            // Armazena as informacoes para para serem exibidas e enviadas
            final ContentValues contentValues = new ContentValues();
            contentValues.put("comando", 0);
            contentValues.put("tela", "WSSisInfoWebservice");
            contentValues.put("mensagem", funcoes.tratamentoErroBancoDados(e.toString()));
            contentValues.put("dados", e.toString());
            // Pega os dados do usuario

            contentValues.put("usuario", funcoes.getValorXml("Usuario"));
            contentValues.put("empresa", funcoes.getValorXml("ChaveEmpresa"));
            contentValues.put("email", funcoes.getValorXml("Email"));

            ((Activity) context).runOnUiThread(new Runnable() {
                public void run() {
                    funcoes.menssagem(contentValues);
                }
            });


        } catch (XmlPullParserException e) {
            //e.printStackTrace();

            final FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

            // Armazena as informacoes para para serem exibidas e enviadas
            final ContentValues contentValues = new ContentValues();
            contentValues.put("comando", 0);
            contentValues.put("tela", "WSSisInfoWebservice");
            contentValues.put("mensagem", funcoes.tratamentoErroBancoDados(e.toString()));
            contentValues.put("dados", e.toString());
            // Pega os dados do usuario

            contentValues.put("usuario", funcoes.getValorXml("Usuario"));
            contentValues.put("empresa", funcoes.getValorXml("ChaveEmpresa"));
            contentValues.put("email", funcoes.getValorXml("Email"));

            ((Activity) context).runOnUiThread(new Runnable() {
                public void run() {
                    funcoes.menssagem(contentValues);
                }
            });
        }
        return null;
    }

    public RetornoWebServiceBeans executarWebservice(List<PropertyInfo> listaPropriedades, String funcao){
        RetornoWebServiceBeans retorno = new RetornoWebServiceBeans();
        try {
            SoapObject soap = new SoapObject(ServicoWeb.WS_NAME_SPACE, funcao);

            PropertyInfo propertyConexaoFirebird = new PropertyInfo();
            propertyConexaoFirebird.setName("dadosConexao");
            propertyConexaoFirebird.setValue(conexaoFIrebirdBeans);
            propertyConexaoFirebird.setType(conexaoFIrebirdBeans.getClass());
            //propertyConexaoFirebird.setNamespace(ServicoWeb.WS_NAME_SPACE);
            soap.addProperty(propertyConexaoFirebird);

            if (listaPropriedades != null) {
                // Pega todas propriedades passado por paramentros
                for (PropertyInfo propriedadInfo : listaPropriedades) {
                    soap.addProperty(propriedadInfo);
                }
            }
            // Definicao da versao do protocolo do webservice, a forma de como os dados serao enviados
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            // Adiciona os objetos/propriedades
            envelope.setOutputSoapObject(soap);

            FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

            String ipServidor = funcoes.getValorXml("IPServidorWebservice");

            // Checa se retornou algum endereco de ip do servidor
            if ((ipServidor == null) || (ipServidor.length() <= 1) || (ipServidor.equalsIgnoreCase(funcoes.NAO_ENCONTRADO))){
                ipServidor = "localhost";
                //ipServidor = "172.16.0.4";
            }
            String enderecoWebService = "http://" + ipServidor + ":8080/" + ServicoWeb.WS_ENDERECO_WEBSERVICE;

            HttpTransportSE httpTransporte = new HttpTransportSE(enderecoWebService, 50000);

            // Requisicao dos dados
            httpTransporte.call(funcao, envelope);

            SoapObject response;

            if (envelope.getResponse() != null) {
                response = (SoapObject) envelope.getResponse();
                retorno.setCodigoRetorno(Integer.parseInt(response.getPropertyAsString("codigoRetorno")));
                retorno.setMensagemRetorno(response.getPropertyAsString("mensagemRetorno"));
                retorno.setExtra(response.getProperty("extra"));
            } else {
                return null;
            }
            return retorno;

        } catch (IOException e) {
            //e.printStackTrace();

            final FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

            // Armazena as informacoes para para serem exibidas e enviadas
            final ContentValues contentValues = new ContentValues();
            contentValues.put("comando", 0);
            contentValues.put("tela", "WSSisInfoWebservice");
            contentValues.put("mensagem", funcoes.tratamentoErroBancoDados(e.toString()));
            contentValues.put("dados", e.toString());
            // Pega os dados do usuario

            contentValues.put("usuario", funcoes.getValorXml("Usuario"));
            contentValues.put("empresa", funcoes.getValorXml("ChaveEmpresa"));
            contentValues.put("email", funcoes.getValorXml("Email"));

            ((Activity) context).runOnUiThread(new Runnable() {
                public void run() {
                    funcoes.menssagem(contentValues);
                }
            });


        } catch (XmlPullParserException e) {
            //e.printStackTrace();

            final FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

            // Armazena as informacoes para para serem exibidas e enviadas
            final ContentValues contentValues = new ContentValues();
            contentValues.put("comando", 0);
            contentValues.put("tela", "WSSisInfoWebservice");
            contentValues.put("mensagem", funcoes.tratamentoErroBancoDados(e.toString()));
            contentValues.put("dados", e.toString());
            // Pega os dados do usuario

            contentValues.put("usuario", funcoes.getValorXml("Usuario"));
            contentValues.put("empresa", funcoes.getValorXml("ChaveEmpresa"));
            contentValues.put("email", funcoes.getValorXml("Email"));

            ((Activity) context).runOnUiThread(new Runnable() {
                public void run() {
                    funcoes.menssagem(contentValues);
                }
            });
        }
        return null;
    }

    public Vector<SoapObject> executarSelectWebservice(String sql, String funcao, List<PropertyInfo> listaPropriedadesExtra){

        java.util.Vector<SoapObject> retorno = null;

        try {
            SoapObject soap = new SoapObject(ServicoWeb.WS_NAME_SPACE, funcao);

            PropertyInfo propertyConexaoFirebird = new PropertyInfo();
            propertyConexaoFirebird.setName("dadosConexao");
            propertyConexaoFirebird.setValue(conexaoFIrebirdBeans);
            propertyConexaoFirebird.setType(conexaoFIrebirdBeans.getClass());
            soap.addProperty(propertyConexaoFirebird);

            PropertyInfo propertySql = new PropertyInfo();
            propertySql.setName("sql");
            propertySql.setValue(sql);
            propertySql.setType(sql.getClass());
            soap.addProperty(propertySql);

            if (listaPropriedadesExtra != null) {
                // Pega todas propriedades passado por paramentros
                for (PropertyInfo propriedadInfo : listaPropriedadesExtra) {
                    soap.addProperty(propriedadInfo);
                }
            }

            // Definicao da versao do protocolo do webservice, a forma de como os dados serao enviados
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            // Adiciona os objetos/propriedades
            envelope.setOutputSoapObject(soap);

            FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

            String ipServidor = funcoes.getValorXml("IPServidorWebservice");

            // Checa se retornou algum endereco de ip do servidor
            if ((ipServidor == null) || (ipServidor.length() <= 1) || (ipServidor.equalsIgnoreCase(funcoes.NAO_ENCONTRADO))){
                ipServidor = "localhost";
                //ipServidor = "172.16.0.4";
            }
            String enderecoWebService = "http://" + ipServidor + ":8080/" + ServicoWeb.WS_ENDERECO_WEBSERVICE;

            HttpTransportSE httpTransporte = new HttpTransportSE(enderecoWebService, 30000);

            // Requisicao dos dados
            httpTransporte.call(funcao, envelope);

            if (envelope.getResponse() != null) {
                // Instancia a classe de vetor para salvar a lista
                retorno = new Vector<SoapObject>();

                SoapObject objetoPropriedade = (SoapObject) envelope.bodyIn;

                // Checa se retornou apena um registro na lista
                if (objetoPropriedade.getPropertyCount() == 1){
                    retorno.add(objetoPropriedade);

                } else {
                    for (SoapObject objeto : (Vector<SoapObject>) envelope.getResponse()) {
                        retorno.add(objeto);
                    }
                }

            } else {
                return null;
            }
            return retorno;

        } catch (IOException e) {
            //e.printStackTrace();

            final FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

            // Armazena as informacoes para para serem exibidas e enviadas
            final ContentValues contentValues = new ContentValues();
            contentValues.put("comando", 0);
            contentValues.put("tela", "WSSisInfoWebservice");
            contentValues.put("mensagem", funcoes.tratamentoErroBancoDados(e.toString()));
            contentValues.put("dados", e.toString());
            // Pega os dados do usuario

            contentValues.put("usuario", funcoes.getValorXml("Usuario"));
            contentValues.put("empresa", funcoes.getValorXml("ChaveEmpresa"));
            contentValues.put("email", funcoes.getValorXml("Email"));

            ((Activity) context).runOnUiThread(new Runnable() {
                public void run() {
                    funcoes.menssagem(contentValues);
                }
            });
        } catch (XmlPullParserException e) {
            //e.printStackTrace();

            final FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

            // Armazena as informacoes para para serem exibidas e enviadas
            final ContentValues contentValues = new ContentValues();
            contentValues.put("comando", 0);
            contentValues.put("tela", "WSSisInfoWebservice");
            contentValues.put("mensagem", funcoes.tratamentoErroBancoDados(e.toString()));
            contentValues.put("dados", e.toString());
            // Pega os dados do usuario

            contentValues.put("usuario", funcoes.getValorXml("Usuario"));
            contentValues.put("empresa", funcoes.getValorXml("ChaveEmpresa"));
            contentValues.put("email", funcoes.getValorXml("Email"));

            ((Activity) context).runOnUiThread(new Runnable() {
                public void run() {
                    funcoes.menssagem(contentValues);
                }
            });
        }
        return null;
    }

    public SoapObject executarWebservice(String sql, String funcao, List<PropertyInfo> listaPropriedadesExtra){

        SoapObject retorno;

        try {
            SoapObject soap = new SoapObject(ServicoWeb.WS_NAME_SPACE, funcao);

            PropertyInfo propertyConexaoFirebird = new PropertyInfo();
            propertyConexaoFirebird.setName("dadosConexao");
            propertyConexaoFirebird.setValue(conexaoFIrebirdBeans);
            propertyConexaoFirebird.setType(conexaoFIrebirdBeans.getClass());
            soap.addProperty(propertyConexaoFirebird);

            PropertyInfo propertySql = new PropertyInfo();
            propertySql.setName("sql");
            propertySql.setValue(sql);
            propertySql.setType(sql.getClass());
            soap.addProperty(propertySql);

            if (listaPropriedadesExtra != null) {
                // Pega todas propriedades passado por paramentros
                for (PropertyInfo propriedadInfo : listaPropriedadesExtra) {
                    soap.addProperty(propriedadInfo);
                }
            }

            // Definicao da versao do protocolo do webservice, a forma de como os dados serao enviados
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            // Adiciona os objetos/propriedades
            envelope.setOutputSoapObject(soap);

            FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

            String ipServidor = funcoes.getValorXml("IPServidorWebservice");

            // Checa se retornou algum endereco de ip do servidor
            if ((ipServidor == null) || (ipServidor.length() <= 1) || (ipServidor.equalsIgnoreCase(funcoes.NAO_ENCONTRADO))){
                ipServidor = "localhost";
                //ipServidor = "172.16.0.4";
            }
            String enderecoWebService = "http://" + ipServidor + ":8080/" + ServicoWeb.WS_ENDERECO_WEBSERVICE;

            HttpTransportSE httpTransporte = new HttpTransportSE(enderecoWebService, 30000);

            // Requisicao dos dados
            httpTransporte.call(funcao, envelope);

            if (envelope.getResponse() != null) {

                retorno = (SoapObject) envelope.getResponse();

            } else {
                return null;
            }
            return retorno;

        } catch (IOException e) {
            //e.printStackTrace();

            final FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

            // Armazena as informacoes para para serem exibidas e enviadas
            final ContentValues contentValues = new ContentValues();
            contentValues.put("comando", 0);
            contentValues.put("tela", "WSSisInfoWebservice");
            contentValues.put("mensagem", funcoes.tratamentoErroBancoDados(e.toString()));
            contentValues.put("dados", e.toString());
            // Pega os dados do usuario

            contentValues.put("usuario", funcoes.getValorXml("Usuario"));
            contentValues.put("empresa", funcoes.getValorXml("ChaveEmpresa"));
            contentValues.put("email", funcoes.getValorXml("Email"));

            ((Activity) context).runOnUiThread(new Runnable() {
                public void run() {
                    funcoes.menssagem(contentValues);
                }
            });
        } catch (XmlPullParserException e) {
            //e.printStackTrace();

            final FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

            // Armazena as informacoes para para serem exibidas e enviadas
            final ContentValues contentValues = new ContentValues();
            contentValues.put("comando", 0);
            contentValues.put("tela", "WSSisInfoWebservice");
            contentValues.put("mensagem", funcoes.tratamentoErroBancoDados(e.toString()));
            contentValues.put("dados", e.toString());
            // Pega os dados do usuario

            contentValues.put("usuario", funcoes.getValorXml("Usuario"));
            contentValues.put("empresa", funcoes.getValorXml("ChaveEmpresa"));
            contentValues.put("email", funcoes.getValorXml("Email"));

            ((Activity) context).runOnUiThread(new Runnable() {
                public void run() {
                    funcoes.menssagem(contentValues);
                }
            });
        }
        return null;
    }

}
