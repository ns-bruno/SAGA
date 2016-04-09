package com.saga.funcoes.rotinas;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.saga.banco.interno.funcoesSql.UnidadeVendaSql;
import com.saga.beans.UnidadeVendaBeans;
import com.saga.funcoes.FuncoesPersonalizadas;
import com.saga.webservice.WSSisInfoWebservice;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bruno Nogueira Silva on 10/02/2016.
 */
public class UnidadeVendaRotinas extends Rotinas {

    public UnidadeVendaRotinas(Context context) {
        super(context);
    }


    public List<UnidadeVendaBeans> listaUnidadeVenda(String where){
        List<UnidadeVendaBeans> listaRetorno = null;

        try {
            if (tipoConexao.equalsIgnoreCase("W")){
                // Instancia a classe para manipular o webservice

                String sql = "SELECT * FROM AEAUNVEN";

                if (where != null){
                    sql += " WHERE (" + where + ")";
                }

                sql += " ORDER BY AEAUNVEN.SIGLA";

                WSSisInfoWebservice webserviceSisInfo = new WSSisInfoWebservice(context);
                java.util.Vector<SoapObject> listaUnidade = webserviceSisInfo.executarSelectWebservice(sql, WSSisInfoWebservice.FUNCTION_SELECT_LISTA_UNIDADE_VENDA, null);

                if (listaUnidade != null && listaUnidade.size() > 0){
                    // Instancia a classe da listaRetorno de retorno
                    listaRetorno = new ArrayList<UnidadeVendaBeans>();

                    for (SoapObject objeto : listaUnidade) {

                        UnidadeVendaBeans unidade = new UnidadeVendaBeans();
                        unidade.setIdUnidadeVenda(Integer.parseInt(objeto.getProperty("idUnidadeVenda").toString()));
                        unidade.setDataAlt(objeto.getProperty("dataAlt").toString());
                        unidade.setSigla(objeto.getProperty("sigla").toString());
                        unidade.setDescricaoUnidadeVenda(objeto.getProperty("descricaoUnidadeVenda").toString());
                        unidade.setDecimais(Integer.parseInt(objeto.getProperty("decimais").toString()));
                        // Adiciona a unidade na listaRetorno
                        listaRetorno.add(unidade);
                    }
                }
            } else {
                UnidadeVendaSql unidadeVendaSql = new UnidadeVendaSql(context);

                Cursor cursor = unidadeVendaSql.query(where, "AEAUNVEN.SIGLA");

                if ((cursor != null) && (cursor.getCount() > 0)) {
                    listaRetorno = new ArrayList<UnidadeVendaBeans>();

                    // Passa por todos os registro
                    while (cursor.moveToNext()) {
                        UnidadeVendaBeans unidade = new UnidadeVendaBeans();
                        unidade.setIdUnidadeVenda(cursor.getInt(cursor.getColumnIndex("ID_AEAUNVEN")));
                        unidade.setDataAlt(cursor.getString(cursor.getColumnIndex("DT_ALT")));
                        unidade.setSigla(cursor.getString(cursor.getColumnIndex("SIGLA")));
                        unidade.setDescricaoUnidadeVenda(cursor.getString(cursor.getColumnIndex("DESCRICAO_SINGULAR")));
                        unidade.setDecimais(cursor.getInt(cursor.getColumnIndex("DECIMAIS")));
                        // Adiciona a unidade na listaRetorno
                        listaRetorno.add(unidade);
                    }
                }
            }

        }catch (Exception e){
            final FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

            // Armazena as informacoes para para serem exibidas e enviadas
            final ContentValues contentValues = new ContentValues();
            contentValues.put("comando", 0);
            contentValues.put("tela", "UnidadeVendaRotinas");
            contentValues.put("mensagem", funcoes.tratamentoErroBancoDados(e.getMessage()));
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

            return listaRetorno;
    }

}
