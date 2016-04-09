package com.saga.funcoes.rotinas;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.widget.ProgressBar;

import com.saga.beans.LocesBeans;
import com.saga.funcoes.FuncoesPersonalizadas;
import com.saga.webservice.WSSisInfoWebservice;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Bruno Nogueira Silva on 07/04/2016.
 */
public class LocesRotinas extends Rotinas {

    public LocesRotinas(Context context) {
        super(context);
    }

    public List<LocesBeans> selectListaLoces(String where, final ProgressBar progresso){
        List<LocesBeans> listaLocesRetorno = null;

        try{
            FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

            String sql = "SELECT AEALOCES.ID_AEALOCES, AEALOCES.ID_SMAEMPRE, AEALOCES.CODIGO, AEALOCES.DT_ALT, AEALOCES.DESCRICAO, AEALOCES.ATIVO, AEALOCES.TIPO_VENDA, AEALOCES.GUID \n" +
                    "FROM AEALOCES \n" +
                    "WHERE (AEALOCES.ID_SMAEMPRE = " + ((!funcoes.getValorXml("CodigoEmpresa").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? funcoes.getValorXml("CodigoEmpresa") : "1") + ") \n";

            if (where != null){
                sql += " AND ( " + where + " ) \n";
            }

            if (tipoConexao.equalsIgnoreCase("W")){
                // Instancia a classe para manipular o webservice
                WSSisInfoWebservice webserviceSisInfo = new WSSisInfoWebservice(context);
                Vector<SoapObject> listaEstoqueObjeto = webserviceSisInfo.executarSelectWebservice(sql, WSSisInfoWebservice.FUNCTION_SELECT_LISTA_LOCES, null);

                // Checa se retornou alguma coisa
                if ((listaEstoqueObjeto != null) && (listaEstoqueObjeto.size() > 0)) {

                    //Instancia a lista de retorno
                    listaLocesRetorno = new ArrayList<LocesBeans>();

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

                        SoapObject dadosLoces;
                        if (dadosEstoqueIndividual.hasProperty("return")){
                            dadosLoces = (SoapObject) dadosEstoqueIndividual.getProperty("return");

                        } else {
                            dadosLoces = dadosEstoqueIndividual;
                        }
                        LocesBeans locesBeans = new LocesBeans();
                        locesBeans.setIdLoces(Integer.parseInt(dadosLoces.getProperty("idLoces").toString()));
                        locesBeans.setIdEmpresa(Integer.parseInt(dadosLoces.getProperty("idEmpresa").toString()));
                        locesBeans.setGuid(dadosLoces.getProperty("guid").toString());
                        locesBeans.setDataAlt(dadosLoces.getProperty("dataAlt").toString());
                        locesBeans.setCodigo(Integer.parseInt(dadosLoces.getProperty("codigo").toString()));
                        locesBeans.setDescricaoLoces(dadosLoces.getProperty("descricaoLoces").toString());
                        locesBeans.setAtivo(dadosLoces.getProperty("ativo").toString());
                        locesBeans.setTipoVenda(dadosLoces.getProperty("tipoVenda").toString());

                        listaLocesRetorno.add(locesBeans);
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

        return listaLocesRetorno;
    } // Fim selecListaEstoque
}
