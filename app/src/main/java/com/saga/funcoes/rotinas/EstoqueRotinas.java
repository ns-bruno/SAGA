package com.saga.funcoes.rotinas;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.saga.R;
import com.saga.beans.EstoqueBeans;
import com.saga.beans.LocesBeans;
import com.saga.beans.RetornoWebServiceBeans;
import com.saga.funcoes.FuncoesPersonalizadas;
import com.saga.webservice.WSSisInfoWebservice;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Faturamento on 01/04/2016.
 */
public class EstoqueRotinas extends Rotinas {


    public EstoqueRotinas(Context context) {
        super(context);
    }


    public List<EstoqueBeans> selectListaEstoque(String where, final ProgressBar progresso){
        List<EstoqueBeans> listaEstoqueRetorno = null;

        try{
            String sql = "SELECT AEAESTOQ.ID_AEAESTOQ, AEAESTOQ.ID_AEAPLOJA, AEAESTOQ.ID_AEALOCES, AEAESTOQ.GUID AS GUID_ESTOQ, \n" +
                         "AEAESTOQ.US_CAD AS US_CAD_ESTOQ, AEAESTOQ.DT_ALT AS DT_ALT_ESTOQ, AEAESTOQ.ESTOQUE, \n" +
                         "AEAESTOQ.RETIDO, AEAESTOQ.LOCACAO_ATIVA, AEAESTOQ.LOCACAO_RESERVA, \n" +
                         "AEALOCES.ID_AEALOCES, AEALOCES.ID_SMAEMPRE, AEALOCES.GUID AS GUID_LOCES, AEALOCES.DT_CAD AS DT_CAD_LOCES, \n" +
                         "AEALOCES.DT_ALT AS DT_ALT_LOCES, AEALOCES.CODIGO AS CODIGO_LOCES, AEALOCES.DESCRICAO AS DESCRICAO_LOCES \n" +
                         "FROM AEAESTOQ LEFT OUTER JOIN AEALOCES ON(AEAESTOQ.ID_AEALOCES = AEALOCES.ID_AEALOCES) ";

            if (where != null){
                sql += "WHERE ( " + where + " ) \n";
            }

            /*sql +=  "GROUP BY AEAPRODU.ID_AEAPRODU, AEAPRODU.CODIGO, AEAPRODU.CODIGO_ESTRUTURAL, \n" +
                    "AEAPRODU.CODIGO_BARRAS, AEAPRODU.GUID, AEAPRODU.DESCRICAO, \n" +
                    "AEAPRODU.DESCRICAO_AUXILIAR, AEAPRODU.REFERENCIA, AEAMARCA.ID_AEAMARCA, \n" +
                    "AEAMARCA.ID_AEAMARCA, AEAMARCA.DESCRICAO, \n" +
                    "AEAUNVEN.ID_AEAUNVEN, AEAUNVEN.SIGLA, AEAUNVEN.DESCRICAO_SINGULAR, AEAUNVEN.DECIMAIS, \n" +
                    "AEAPLOJA.ID_AEAPLOJA, AEAPLOJA.ESTOQUE_F, AEAPLOJA.ESTOQUE_C, AEAPLOJA.RETIDO, AEAPLOJA.PEDIDO, \n" +
                    "AEAPLOJA.VENDA_ATAC, AEAPLOJA.VENDA_VARE ";*/

            if (tipoConexao.equalsIgnoreCase("W")){
                // Instancia a classe para manipular o webservice
                WSSisInfoWebservice webserviceSisInfo = new WSSisInfoWebservice(context);
                Vector<SoapObject> listaEstoqueObjeto = webserviceSisInfo.executarSelectWebservice(sql, WSSisInfoWebservice.FUNCTION_SELECT_LISTA_ESTOQUE, null);

                // Checa se retornou alguma coisa
                if ((listaEstoqueObjeto != null) && (listaEstoqueObjeto.size() > 0)) {

                    //Instancia a lista de retorno
                    listaEstoqueRetorno = new ArrayList<EstoqueBeans>();

                    // Pega o total de registros retornado do banco
                    final int totalRegistro = listaEstoqueObjeto.size();

                    // Checa se tem alguma barra de progresso
                    if (progresso != null) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            public void run() {
                                progresso.setIndeterminate(false);
                                progresso.setProgress(0);
                                progresso.setMax(totalRegistro);
                            }
                        });
                    }
                    int incremento = 0;

                    for(SoapObject dadosEstoqueIndividual : listaEstoqueObjeto){

                        if (progresso != null) {
                            incremento++;
                            final int finalIncremento = incremento;
                            ((Activity) context).runOnUiThread(new Runnable() {
                                public void run() {
                                    progresso.setProgress(finalIncremento);
                                }
                            });
                        }

                        SoapObject dadosEstoque;
                        if (dadosEstoqueIndividual.hasProperty("return")){
                            dadosEstoque = (SoapObject) dadosEstoqueIndividual.getProperty("return");

                        } else {
                            dadosEstoque = dadosEstoqueIndividual;
                        }

                        SoapObject objetoLoces = (SoapObject) dadosEstoque.getProperty("loces");
                        LocesBeans locesBeans = new LocesBeans();
                        locesBeans.setIdLoces(Integer.parseInt(objetoLoces.getProperty("idLoces").toString()));
                        locesBeans.setIdEmpresa(Integer.parseInt(objetoLoces.getProperty("idEmpresa").toString()));
                        locesBeans.setGuid(objetoLoces.getProperty("guid").toString());
                        locesBeans.setDataAlt(objetoLoces.getProperty("dataAlt").toString());
                        locesBeans.setCodigo(Integer.parseInt(objetoLoces.getProperty("codigo").toString()));
                        locesBeans.setDescricaoLoces(objetoLoces.getProperty("descricaoLoces").toString());

                        EstoqueBeans estoqueBeans = new EstoqueBeans();
                        estoqueBeans.setLoces(locesBeans);
                        estoqueBeans.setIdEstoque(Integer.parseInt(dadosEstoqueIndividual.getProperty("idEstoque").toString()));
                        estoqueBeans.setGuid(dadosEstoqueIndividual.getProperty("guid").toString());
                        estoqueBeans.setDataAlt(dadosEstoqueIndividual.getProperty("dataAlt").toString());
                        estoqueBeans.setEstoque(Double.parseDouble(dadosEstoqueIndividual.getProperty("estoque").toString()));
                        estoqueBeans.setRetido(Double.parseDouble(dadosEstoqueIndividual.getProperty("retido").toString()));
                        estoqueBeans.setLocacaoAtiva((dadosEstoqueIndividual.hasProperty("locacaoAtiva")) ? dadosEstoqueIndividual.getProperty("locacaoAtiva").toString() : "");
                        estoqueBeans.setLocacaoReserva((dadosEstoqueIndividual.hasProperty("locacaoReserva")) ? dadosEstoqueIndividual.getProperty("locacaoReserva").toString() : "");

                        listaEstoqueRetorno.add(estoqueBeans);
                    }
                }

            } else {

            }

        } catch (Exception e){
            final FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

            // Armazena as informacoes para para serem exibidas e enviadas
            final ContentValues contentValues = new ContentValues();
            contentValues.put("comando", 0);
            contentValues.put("tela", "EstoqueRotinas");
            contentValues.put("mensagem", "Não carregar a lista de estoque. \n" + funcoes.tratamentoErroBancoDados(e.getMessage()));
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

        return listaEstoqueRetorno;
    } // Fim selecListaEstoque


    public boolean updateLocacaoEstoque(EstoqueBeans estoque, String where, ProgressBar progressBarStatus, final TextView textStatus){

        try{
            if (getTipoConexao().equalsIgnoreCase("W")){
                // Cria uma lista para salvar todas as propriedades
                List<PropertyInfo> listaPropertyInfos = new ArrayList<PropertyInfo>();

                PropertyInfo propertyEstoque = new PropertyInfo();
                propertyEstoque.setName("dadosEstoque");
                propertyEstoque.setValue(estoque);
                propertyEstoque.setType(estoque.getClass());
                // Adiciona a propriedade em uma lista
                listaPropertyInfos.add(propertyEstoque);

                PropertyInfo propertyWhere = new PropertyInfo();
                propertyWhere.setName("where");
                propertyWhere.setValue(where);
                propertyWhere.setType(where.getClass());
                // Adiciona a propriedade na lista
                listaPropertyInfos.add(propertyWhere);

                if (textStatus != null){
                    ((Activity) context).runOnUiThread(new Runnable() {
                        public void run() {
                            textStatus.setText(context.getResources().getString(R.string.enviando_dados_servidor_webservice));
                        }
                    });
                }
                WSSisInfoWebservice webserviceSisInfo = new WSSisInfoWebservice(context);
                // Executa o webservice
                final RetornoWebServiceBeans retorno = webserviceSisInfo.executarWebservice(listaPropertyInfos, WSSisInfoWebservice.FUNCTION_UPDATE_LOCACAO_ESTOQUE);

                // Checa se retornou alguma coisa
                if (retorno != null){

                    if (textStatus != null){
                        ((Activity) context).runOnUiThread(new Runnable() {
                            public void run() {
                                textStatus.setText(context.getResources().getString(R.string.ja_estamos_com_retorno_servidor));
                            }
                        });
                    }
                    // Checa se o retorno teve atualizado com sucesso
                    if (retorno.getCodigoRetorno() == 101){

                        final ContentValues contentValues = new ContentValues();
                        contentValues.put("comando", 2);
                        contentValues.put("mensagem", retorno.getMensagemRetorno());

                        final FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

                        if (textStatus != null){
                            ((Activity) context).runOnUiThread(new Runnable() {
                                public void run() {
                                    textStatus.setText(retorno.getMensagemRetorno() + "\n" + retorno.getExtra().toString());
                                    // Executa uma mensagem rapida
                                    funcoes.menssagem(contentValues);
                                }
                            });
                        }
                        return true;
                    } else {
                        final FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

                        // Armazena as informacoes para para serem exibidas e enviadas
                        final ContentValues contentValues = new ContentValues();
                        contentValues.put("comando", 0);
                        contentValues.put("tela", "EstoqueRotina");
                        contentValues.put("mensagem", "\n Codigo Erro: " + retorno.getCodigoRetorno() +
                                "\n Mensagem: " + retorno.getMensagemRetorno() + "\n" + retorno.getExtra().toString());

                        ((Activity) context).runOnUiThread(new Runnable() {
                            public void run() {
                                funcoes.menssagem(contentValues);
                            }
                        });
                    }
                }
            } else {
                //ContentValues dadosEstoque = new ContentValues();
            }

        } catch (Exception e){
            final FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

            // Armazena as informacoes para para serem exibidas e enviadas
            final ContentValues contentValues = new ContentValues();
            contentValues.put("comando", 0);
            contentValues.put("tela", "EstoqueRotinas");
            contentValues.put("mensagem", "Erro ao atualizar o estoque. \n" + funcoes.tratamentoErroBancoDados(e.getMessage()));
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

        return false;
    } // Fim updateLocacaoEstoque
}