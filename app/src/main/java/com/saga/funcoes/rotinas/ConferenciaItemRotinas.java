package com.saga.funcoes.rotinas;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.saga.R;
import com.saga.beans.ConferenciaItemBeans;
import com.saga.beans.RetornoWebServiceBeans;
import com.saga.funcoes.FuncoesPersonalizadas;
import com.saga.webservice.WSSisInfoWebservice;

import org.ksoap2.serialization.PropertyInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bruno Nogueira Silva on 17/05/2016.
 */
public class ConferenciaItemRotinas extends Rotinas {

    public ConferenciaItemRotinas(Context context) {
        super(context);
    }

    public boolean insertEmbalagem(ConferenciaItemBeans conferenciaItem, ProgressBar progressBarStatus, final TextView textStatus){
        try{
            final FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

            if (getTipoConexao().equalsIgnoreCase("W")){
                // Cria uma lista para salvar todas as propriedades
                List<PropertyInfo> listaPropertyInfos = new ArrayList<PropertyInfo>();

                PropertyInfo propertyEmbalagem = new PropertyInfo();
                propertyEmbalagem.setName("dadosConferencia");
                propertyEmbalagem.setValue(conferenciaItem);
                propertyEmbalagem.setType(conferenciaItem.getClass());

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
                final RetornoWebServiceBeans retorno = webserviceSisInfo.executarWebservice(listaPropertyInfos, WSSisInfoWebservice.FUNCTION_INSERT_CONFERENCIA_ITEM);

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
                        contentValues.put("tela", "ConferenciaItemRotinas");
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

                /*ContentValues dadosEmbalagem = new ContentValues();
                dadosEmbalagem.put("ID_AEAPRODU", conferenciaItem.getIdProduto());
                dadosEmbalagem.put("ID_AEAUNVEN", conferenciaItem.getUnidadeVenda().getIdUnidadeVenda());
                dadosEmbalagem.put("GUID", funcoes.geraGuid(16));
                dadosEmbalagem.put("DESCRICAO", conferenciaItem.getDescricaoEmbalagem());
                dadosEmbalagem.put("MODULO", conferenciaItem.getModulo());
                dadosEmbalagem.put("DECIMAIS", conferenciaItem.getDecimais());
                dadosEmbalagem.put("ATIVO", conferenciaItem.getAtivo());
                dadosEmbalagem.put("CODIGO_BARRAS", conferenciaItem.getCodigoBarras());
                dadosEmbalagem.put("REFERENCIA", conferenciaItem.getReferencia());

                EmbalagemSql embalagemSql = new EmbalagemSql(context);
                long id = embalagemSql.insert(dadosEmbalagem);

                if (id > 0) {
                    return true;
                }*/
            }

        } catch (Exception e){
            final FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

            // Armazena as informacoes para para serem exibidas e enviadas
            final ContentValues contentValues = new ContentValues();
            contentValues.put("comando", 0);
            contentValues.put("tela", "ConferenciaItemRotinas");
            contentValues.put("mensagem", "Erro ao inserir a conferencia. \n" + funcoes.tratamentoErroBancoDados(e.getMessage()));
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
    }
}
