package com.saga.funcoes.rotinas;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.saga.R;
import com.saga.banco.interno.funcoesSql.EmbalagemSql;
import com.saga.beans.EmbalagemBeans;
import com.saga.beans.RetornoWebServiceBeans;
import com.saga.beans.UnidadeVendaBeans;
import com.saga.funcoes.FuncoesPersonalizadas;
import com.saga.webservice.WSSisInfoWebservice;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bruno Nogueira Silva on 05/02/2016.
 */
public class EmbalagemRotinas extends Rotinas {

    public EmbalagemRotinas(Context context) {
        super(context);
        setTipoConexao("W");
    }

    public List<EmbalagemBeans> listaEmbalagemProduto(int idProduto, String where, final ProgressBar progresso){
        List<EmbalagemBeans> lista = new ArrayList<EmbalagemBeans>();

        try{
            String sql = "SELECT AEAEMBAL.ID_AEAEMBAL, AEAEMBAL.ID_AEAPRODU, AEAUNVEN.ID_AEAUNVEN, AEAEMBAL.DT_ALT AS DT_ALT_EMBAL, " +
                        "AEAEMBAL.PRINCIPAL, AEAEMBAL.DESCRICAO AS DESCRICAO_EMBAL, AEAEMBAL.FATOR_CONVERSAO, AEAEMBAL.FATOR_PRECO, AEAEMBAL.MODULO, " +
                        "AEAEMBAL.DECIMAIS AS DECIMAIS_EMBAL, AEAEMBAL.ATIVO AS ATIVO_EMBAL, AEAEMBAL.CODIGO_BARRAS, AEAEMBAL.REFERENCIA, " +
                        "AEAUNVEN.DT_ALT AS DT_ALT_UNVEN, AEAUNVEN.SIGLA AS SIGLA_UNVEN, AEAUNVEN.DESCRICAO_SINGULAR, " +
                        "AEAUNVEN.DECIMAIS AS DECIMAIS_UNVEN " +
                        "FROM AEAEMBAL " +
                        "LEFT OUTER JOIN AEAUNVEN " +
                        "ON(AEAEMBAL.ID_AEAUNVEN = AEAUNVEN.ID_AEAUNVEN) " +
                        "WHERE (AEAEMBAL.ID_AEAPRODU = " + idProduto + ") ";

            if (where != null) {
                sql += " AND ( " + where + " ) ";
            }

            sql += " ORDER BY AEAEMBAL.ID_AEAEMBAL ";

            if (tipoConexao.equalsIgnoreCase("W")){
                // Instancia a classe para manipular o webservice
                WSSisInfoWebservice webserviceSisInfo = new WSSisInfoWebservice(context);
                java.util.Vector<SoapObject> listaEmbalagem = webserviceSisInfo.executarSelectWebservice(sql, WSSisInfoWebservice.FUNCTION_SELECT_LISTA_EMBALAGEM , null);

                // Checa se retornou alguma coisa
                if (listaEmbalagem != null && listaEmbalagem.size() > 0){
                    // Pega o total de registros retornado do banco
                    final int totalRegistro = listaEmbalagem.size();

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
                    // Passa por toda a lista
                    for (SoapObject objeto : listaEmbalagem) {
                        if (progresso != null) {

                            incremento++;
                            final int finalIncremento = incremento;
                            ((Activity) context).runOnUiThread(new Runnable() {
                                public void run() {
                                    progresso.setProgress(finalIncremento);
                                }
                            });
                        }
                        // Vareavel para salvar os dados da embalagem
                        EmbalagemBeans embalagem = new EmbalagemBeans();

                        // Vareavel para pegar os dados do webservice
                        SoapObject objetoEmbalagem;

                        // Checa o modo que retornou
                        if (objeto.hasProperty("return")) {
                            objetoEmbalagem = (SoapObject) objeto.getProperty("return");
                        } else {
                            objetoEmbalagem = objeto;
                        }

                        embalagem.setIdEmbalagem(Integer.parseInt(objetoEmbalagem.getProperty("idEmbalagem").toString()));
                        embalagem.setIdProduto(Integer.parseInt(objetoEmbalagem.getProperty("idProduto").toString()));
                        embalagem.setIdUnidadeVenda(Integer.parseInt(objetoEmbalagem.getProperty("idUnidadeVenda").toString()));
                        embalagem.setDataAlteracao(objetoEmbalagem.getProperty("dataAlteracao").toString());
                        embalagem.setPrincipal((objetoEmbalagem.hasProperty("principal")) ? objetoEmbalagem.getProperty("principal").toString() : "");
                        embalagem.setDescricaoEmbalagem((objetoEmbalagem.hasProperty("descricaoEmbalagem")) ? objetoEmbalagem.getProperty("descricaoEmbalagem").toString() : "");
                        embalagem.setFatorConversao(Double.parseDouble(objetoEmbalagem.getProperty("fatorConversao").toString()));
                        embalagem.setFatorPreco(Double.parseDouble(objetoEmbalagem.getProperty("fatorPreco").toString()));
                        embalagem.setModulo(Integer.parseInt(objetoEmbalagem.getProperty("modulo").toString()));
                        embalagem.setDecimais(Integer.parseInt(objetoEmbalagem.getProperty("decimais").toString()));
                        embalagem.setAtivo(objetoEmbalagem.getProperty("ativoEmbalagem").toString());
                        embalagem.setCodigoBarras((objetoEmbalagem.hasProperty("codigoBarras")) ? objetoEmbalagem.getProperty("codigoBarras").toString() : "");
                        embalagem.setReferencia((objetoEmbalagem.hasProperty("referencia")) ? objetoEmbalagem.getProperty("referencia").toString() : "");

                        UnidadeVendaBeans unidadeVenda = new UnidadeVendaBeans();
                        SoapObject objetoUnidadeVenda = (SoapObject) objetoEmbalagem.getProperty("unidadeVenda");
                        unidadeVenda.setIdUnidadeVenda(Integer.parseInt(objetoUnidadeVenda.getProperty("idUnidadeVenda").toString()));
                        unidadeVenda.setDataAlt(objetoUnidadeVenda.getProperty("dataAlt").toString());
                        unidadeVenda.setSigla(objetoUnidadeVenda.getProperty("sigla").toString());
                        unidadeVenda.setDescricaoUnidadeVenda(objetoUnidadeVenda.getProperty("descricaoUnidadeVenda").toString());
                        unidadeVenda.setDecimais(Integer.parseInt(objetoUnidadeVenda.getProperty("decimais").toString()));
                        // Add unidade na embalagem
                        embalagem.setUnidadeVenda(unidadeVenda);

                        lista.add(embalagem);
                    }
                    //int i = listaNotaFiscal.size();
                    //String s = listaNotaFiscal.toString();
                }

            } else {
                EmbalagemSql embalagemSql = new EmbalagemSql(context);

                final Cursor cursor = embalagemSql.sqlSelect(sql);
                // Checa se retornou algum dado
                if ((cursor != null) && (cursor.getCount() > 0)) {

                    if (progresso != null) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            public void run() {
                                progresso.setIndeterminate(false);
                                progresso.setProgress(0);
                                progresso.setMax(cursor.getCount());
                            }
                        });
                    }
                    int incremento = 0;

                    while (cursor.moveToNext()) {
                        // Checa se tem alguma barra de progresso
                        if (progresso != null) {

                            incremento++;
                            final int finalIncremento = incremento;
                            ((Activity) context).runOnUiThread(new Runnable() {
                                public void run() {
                                    progresso.setProgress(finalIncremento);
                                }
                            });
                        }

                        EmbalagemBeans embalagem = new EmbalagemBeans();
                        embalagem.setIdEmbalagem(cursor.getInt(cursor.getColumnIndex("ID_AEAEMBAL")));
                        embalagem.setIdProduto(cursor.getInt(cursor.getColumnIndex("ID_AEAPRODU")));
                        embalagem.setIdUnidadeVenda(cursor.getInt(cursor.getColumnIndex("ID_AEAUNVEN")));
                        embalagem.setDataAlteracao(cursor.getString(cursor.getColumnIndex("DT_ALT_EMBAL")));
                        embalagem.setPrincipal(cursor.getString(cursor.getColumnIndex("PRINCIPAL")));
                        embalagem.setDescricaoEmbalagem(cursor.getString(cursor.getColumnIndex("DESCRICAO_EMBAL")));
                        embalagem.setFatorConversao(cursor.getDouble(cursor.getColumnIndex("FATOR_CONVERSAO")));
                        embalagem.setFatorPreco(cursor.getDouble(cursor.getColumnIndex("FATOR_PRECO")));
                        embalagem.setModulo(cursor.getInt(cursor.getColumnIndex("MODULO")));
                        embalagem.setDecimais(cursor.getInt(cursor.getColumnIndex("DECIMAIS_EMBAL")));
                        embalagem.setAtivo(cursor.getString(cursor.getColumnIndex("ATIVO_EMBAL")));
                        embalagem.setCodigoBarras(cursor.getString(cursor.getColumnIndex("CODIGO_BARRAS")));
                        embalagem.setReferencia(cursor.getString(cursor.getColumnIndex("REFERENCIA")));

                        UnidadeVendaBeans unidadeVenda = new UnidadeVendaBeans();
                        unidadeVenda.setIdUnidadeVenda(cursor.getInt(cursor.getColumnIndex("ID_AEAUNVEN")));
                        unidadeVenda.setDataAlt(cursor.getString(cursor.getColumnIndex("DT_ALT_UNVEN")));
                        unidadeVenda.setSigla(cursor.getString(cursor.getColumnIndex("SIGLA_UNVEN")));
                        unidadeVenda.setDescricaoUnidadeVenda(cursor.getString(cursor.getColumnIndex("DESCRICAO_SINGULAR")));
                        unidadeVenda.setDecimais(cursor.getInt(cursor.getColumnIndex("DECIMAIS_UNVEN")));
                        // Add unidade na embalagem
                        embalagem.setUnidadeVenda(unidadeVenda);

                        lista.add(embalagem);
                    }
                }
            }
        } catch (Exception e){
            final FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

            // Armazena as informacoes para para serem exibidas e enviadas
            final ContentValues contentValues = new ContentValues();
            contentValues.put("comando", 0);
            contentValues.put("tela", "EmbalagemRotina");
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
        return lista;
    }// Fim listaEmbalagemProduto


    public boolean updateEmbalagem(EmbalagemBeans embalagem, String where, ProgressBar progressBarStatus, final TextView textStatus){

        try{
            if (getTipoConexao().equalsIgnoreCase("W")){
                // Cria uma lista para salvar todas as propriedades
                List<PropertyInfo> listaPropertyInfos = new ArrayList<PropertyInfo>();

                PropertyInfo propertyEmbalagem = new PropertyInfo();
                propertyEmbalagem.setName("dadosEmbalagem");
                propertyEmbalagem.setValue(embalagem);
                propertyEmbalagem.setType(embalagem.getClass());
                // Adiciona a propriedade em uma lista
                listaPropertyInfos.add(propertyEmbalagem);

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
                final RetornoWebServiceBeans retorno = webserviceSisInfo.executarWebservice(listaPropertyInfos, WSSisInfoWebservice.FUNCTION_UPDATE_EMBALAGEM_PRODUTO);

                // Checa se retornou alguma coisa
                if (retorno != null){

                    if (textStatus != null){
                        ((Activity) context).runOnUiThread(new Runnable() {
                            public void run() {
                                textStatus.setText(context.getResources().getString(R.string.ja_estamos_com_retorno_servidor));
                            }
                        });
                    }
                    // Checa se o retorno teve insercao com sucesso
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
                        contentValues.put("tela", "EmbalagemRotina");
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
                ContentValues dadosEmbalagem = new ContentValues();
                dadosEmbalagem.put("ID_AEAUNVEN", embalagem.getUnidadeVenda().getIdUnidadeVenda());
                dadosEmbalagem.put("DESCRICAO", embalagem.getDescricaoEmbalagem());
                dadosEmbalagem.put("MODULO", embalagem.getModulo());
                dadosEmbalagem.put("DECIMAIS", embalagem.getDecimais());
                dadosEmbalagem.put("ATIVO", embalagem.getAtivo());
                dadosEmbalagem.put("CODIGO_BARRAS", embalagem.getCodigoBarras());
                dadosEmbalagem.put("REFERENCIA", embalagem.getReferencia());
            }

        } catch (Exception e){
            FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

            // Armazena as informacoes para para serem exibidas e enviadas
            ContentValues contentValues = new ContentValues();
            contentValues.put("comando", 0);
            contentValues.put("tela", "EmbalagemRotina");
            contentValues.put("mensagem", "Erro ao inserir a embalagem. \n" + funcoes.tratamentoErroBancoDados(e.getMessage()));
            contentValues.put("dados", e.toString());
            // Pega os dados do usuario
            contentValues.put("usuario", funcoes.getValorXml("Usuario"));
            contentValues.put("empresa", funcoes.getValorXml("ChaveEmpresa"));
            contentValues.put("email", funcoes.getValorXml("Email"));

            funcoes.menssagem(contentValues);
        }

        return false;
    }

    public boolean insertEmbalagem(EmbalagemBeans embalagem, ProgressBar progressBarStatus, final TextView textStatus){
        try{

            final FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

            if (getTipoConexao().equalsIgnoreCase("W")){
                // Cria uma lista para salvar todas as propriedades
                List<PropertyInfo> listaPropertyInfos = new ArrayList<PropertyInfo>();

                PropertyInfo propertyEmbalagem = new PropertyInfo();
                propertyEmbalagem.setName("dadosEmbalagem");
                propertyEmbalagem.setValue(embalagem);
                propertyEmbalagem.setType(embalagem.getClass());

                // Adiciona a propriedade na lista
                listaPropertyInfos.add(propertyEmbalagem);

                if (textStatus != null){
                    ((Activity) context).runOnUiThread(new Runnable() {
                        public void run() {
                            textStatus.setText(context.getResources().getString(R.string.enviando_dados_servidor_webservice));
                        }
                    });
                }
                WSSisInfoWebservice webserviceSisInfo = new WSSisInfoWebservice(context);
                // Executa o webservice
                final RetornoWebServiceBeans retorno = webserviceSisInfo.executarWebservice(listaPropertyInfos, WSSisInfoWebservice.FUNCTION_INSERT_EMBALAGEM_PRODUTO);

                // Checa se retornou alguma coisa
                if (retorno != null){
                    if (textStatus != null){
                        ((Activity) context).runOnUiThread(new Runnable() {
                            public void run() {
                                textStatus.setText(context.getResources().getString(R.string.ja_estamos_com_retorno_servidor));
                            }
                        });
                    }
                    // Checa se o retorno teve insercao com sucesso
                    if (retorno.getCodigoRetorno() == 100){

                        final ContentValues contentValues = new ContentValues();
                        contentValues.put("comando", 2);
                        contentValues.put("mensagem", retorno.getMensagemRetorno());

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
                        // Armazena as informacoes para para serem exibidas e enviadas
                        final ContentValues contentValues = new ContentValues();
                        contentValues.put("comando", 0);
                        contentValues.put("tela", "EmbalagemRotina");
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

                ContentValues dadosEmbalagem = new ContentValues();
                dadosEmbalagem.put("ID_AEAPRODU", embalagem.getIdProduto());
                dadosEmbalagem.put("ID_AEAUNVEN", embalagem.getUnidadeVenda().getIdUnidadeVenda());
                dadosEmbalagem.put("GUID", funcoes.geraGuid(16));
                dadosEmbalagem.put("DESCRICAO", embalagem.getDescricaoEmbalagem());
                dadosEmbalagem.put("MODULO", embalagem.getModulo());
                dadosEmbalagem.put("DECIMAIS", embalagem.getDecimais());
                dadosEmbalagem.put("ATIVO", embalagem.getAtivo());
                dadosEmbalagem.put("CODIGO_BARRAS", embalagem.getCodigoBarras());
                dadosEmbalagem.put("REFERENCIA", embalagem.getReferencia());

                EmbalagemSql embalagemSql = new EmbalagemSql(context);
                long id = embalagemSql.insert(dadosEmbalagem);

                if (id > 0) {
                    return true;
                }
            }

        } catch (Exception e){
            FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

            // Armazena as informacoes para para serem exibidas e enviadas
            ContentValues contentValues = new ContentValues();
            contentValues.put("comando", 0);
            contentValues.put("tela", "EmbalagemRotina");
            contentValues.put("mensagem", "Erro ao inserir a embalagem. \n" + funcoes.tratamentoErroBancoDados(e.getMessage()));
            contentValues.put("dados", e.toString());
            // Pega os dados do usuario
            contentValues.put("usuario", funcoes.getValorXml("Usuario"));
            contentValues.put("empresa", funcoes.getValorXml("ChaveEmpresa"));
            contentValues.put("email", funcoes.getValorXml("Email"));

            funcoes.menssagem(contentValues);
        }
        return false;
    }
}
