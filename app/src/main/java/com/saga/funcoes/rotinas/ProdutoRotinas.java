package com.saga.funcoes.rotinas;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.saga.R;
import com.saga.banco.interno.funcoesSql.ProdutoSql;
import com.saga.beans.MarcaBeans;
import com.saga.beans.ProdutoBeans;
import com.saga.beans.ProdutoLojaBeans;
import com.saga.beans.RetornoWebServiceBeans;
import com.saga.beans.UnidadeVendaBeans;
import com.saga.funcoes.FuncoesPersonalizadas;
import com.saga.webservice.WSSisInfoWebservice;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Bruno Nogueira Silva on 08/03/2016.
 */
public class ProdutoRotinas extends Rotinas {

    public ProdutoRotinas(Context context) {
        super(context);
    }

    public String getDescricaoProduto(int idProduto){
        try{
            ProdutoSql produtoSql = new ProdutoSql(context);

            Cursor cursor = produtoSql.sqlSelect("SELECT AEAPRODU.DESCRICAO FROM AEAPRODU WHERE AEAPRODU.ID_AEAPRODU = " + idProduto);

            if ((cursor != null) && (cursor.getCount() > 0)){
                // Move o cursor para o primeiro registro
                cursor.moveToFirst();

                return cursor.getString(cursor.getColumnIndex("DESCRICAO"));
            }

        } catch (Exception e){
            FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

            // Armazena as informacoes para para serem exibidas e enviadas
            ContentValues contentValues = new ContentValues();
            contentValues.put("comando", 0);
            contentValues.put("tela", "ProdutoRotinas");
            contentValues.put("mensagem", "Não conseguimos pegar a descrição do produto. \n" + funcoes.tratamentoErroBancoDados(e.getMessage()));
            contentValues.put("dados", e.toString());
            // Pega os dados do usuario
            contentValues.put("usuario", funcoes.getValorXml("Usuario"));
            contentValues.put("empresa", funcoes.getValorXml("ChaveEmpresa"));
            contentValues.put("email", funcoes.getValorXml("Email"));

            funcoes.menssagem(contentValues);
        }
        return context.getResources().getString(R.string.nao_achamos_descricao);
    }

    public ProdutoBeans selectProdutoResumidoId(int idProduto, String where){
        ProdutoBeans produtoRetorno = null;
        try{
            String sql =
                    "SELECT AEAPRODU.ID_AEAPRODU, AEAPRODU.CODIGO, AEAPRODU.CODIGO_ESTRUTURAL, AEAPRODU.CODIGO_BARRAS, "+
                    "AEAPRODU.GUID, AEAPRODU.DESCRICAO, AEAPRODU.DESCRICAO_AUXILIAR, AEAPRODU.REFERENCIA, " +
                    "AEAPRODU.PESO_BRUTO, AEAPRODU.PESO_LIQUIDO, "+
                    "AEAMARCA.ID_AEAMARCA, AEAMARCA.DESCRICAO AS DESCRICAO_MARCA, "+
                    "AEAUNVEN.ID_AEAUNVEN, AEAUNVEN.SIGLA, AEAUNVEN.DESCRICAO_SINGULAR, AEAUNVEN.DECIMAIS "+
                    "FROM AEAPRODU "+
                    "LEFT OUTER JOIN AEAMARCA "+
                    "ON(AEAPRODU.ID_AEAMARCA = AEAMARCA.ID_AEAMARCA) "+
                    "LEFT OUTER JOIN AEAUNVEN "+
                    "ON(AEAPRODU.ID_AEAUNVEN = AEAUNVEN.ID_AEAUNVEN) " +
                    "WHERE (AEAPRODU.ID_AEAPRODU = " + idProduto + ") ";

            if (where != null){
                sql += " AND (" + where + " ) ";
            }

            if (tipoConexao.equalsIgnoreCase("W")){
                // Instancia a classe para manipular o webservice
                WSSisInfoWebservice webserviceSisInfo = new WSSisInfoWebservice(context);
                SoapObject dadosProduto = webserviceSisInfo.executarWebservice(sql, WSSisInfoWebservice.FUNCTION_DADOS_PRODUTOS_RESUMIDOS, null);

                if (dadosProduto != null){
                    // Instancia a variavel do produto
                    produtoRetorno = new ProdutoBeans();
                    // Pega os dados que chegou do webservice
                    produtoRetorno.setIdProduto(Integer.parseInt(dadosProduto.getProperty("idProduto").toString()));
                    produtoRetorno.setCodigo(Integer.parseInt(dadosProduto.getProperty("codigo").toString()));
                    produtoRetorno.setCodigoEstrutural(dadosProduto.getProperty("codigoEstrutural").toString());
                    produtoRetorno.setCodigoBarras((dadosProduto.hasProperty("codigoBarras")) ? dadosProduto.getProperty("codigoBarras").toString() : "");
                    produtoRetorno.setGuid(dadosProduto.getProperty("guid").toString());
                    produtoRetorno.setDescricaoProduto(dadosProduto.getProperty("descricaoProduto").toString());
                    produtoRetorno.setDescricaoAuxiliar((dadosProduto.hasProperty("descricaoAuxiliar")) ? dadosProduto.getProperty("descricaoAuxiliar").toString() : "");
                    produtoRetorno.setReferencia((dadosProduto.hasProperty("referencia")) ? dadosProduto.getProperty("referencia").toString() : "");
                    produtoRetorno.setPesoBruto( (dadosProduto.hasProperty("pesoBruto")) ? Double.parseDouble(dadosProduto.getProperty("pesoBruto").toString()) : 0 );
                    produtoRetorno.setPesoLiquido( (dadosProduto.hasProperty("pesoLiquido")) ? Double.parseDouble(dadosProduto.getProperty("pesoLiquido").toString()) : 0 );

                    MarcaBeans marca= new MarcaBeans();
                    SoapObject objetoMarca = (SoapObject) dadosProduto.getProperty("marca");
                    marca.setIdMarca(Integer.parseInt(objetoMarca.getProperty("idMarca").toString()));
                    marca.setDescricao(objetoMarca.getProperty("descricao").toString());
                    produtoRetorno.setMarca(marca);

                    UnidadeVendaBeans unidadeVenda = new UnidadeVendaBeans();
                    SoapObject objetoUnidadeVenda = (SoapObject) dadosProduto.getProperty("unidadeVenda");
                    unidadeVenda.setIdUnidadeVenda(Integer.parseInt(objetoUnidadeVenda.getProperty("idUnidadeVenda").toString()));
                    unidadeVenda.setSigla(objetoUnidadeVenda.getProperty("sigla").toString());
                    unidadeVenda.setDescricaoUnidadeVenda(objetoUnidadeVenda.getProperty("descricaoUnidadeVenda").toString());
                    unidadeVenda.setDecimais(Integer.parseInt(objetoUnidadeVenda.getProperty("decimais").toString()));
                    produtoRetorno.setUnidadeVenda(unidadeVenda);
                }

            } else {
                ProdutoSql produtoSql = new ProdutoSql(context);

                Cursor cursor = produtoSql.sqlSelect(sql);

                if ((cursor != null) && (cursor.getCount() > 0)) {
                    // Move o cursor para o primeiro registro
                    cursor.moveToFirst();

                    return null;
                }
            }

        } catch (Exception e){
            final FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

            // Armazena as informacoes para para serem exibidas e enviadas
            final ContentValues contentValues = new ContentValues();
            contentValues.put("comando", 0);
            contentValues.put("tela", "ProdutoRotinas");
            contentValues.put("mensagem", "Não conseguimos pegar a descrição do produto. \n" + funcoes.tratamentoErroBancoDados(e.getMessage()));
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
        return produtoRetorno;
    }


    public List<ProdutoLojaBeans> selectListaProdutoLojaResumido(String where, final ProgressBar progresso){
        List<ProdutoLojaBeans> listaProdutoLojaRetorno = null;
        try{
            FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

            String sql =
                    "SELECT AEAPRODU.ID_AEAPRODU, AEAPRODU.CODIGO, AEAPRODU.CODIGO_ESTRUTURAL, \n" +
                    "AEAPRODU.CODIGO_BARRAS, AEAPRODU.GUID, AEAPRODU.DESCRICAO, \n" +
                    "AEAPRODU.DESCRICAO_AUXILIAR, AEAPRODU.REFERENCIA, AEAMARCA.ID_AEAMARCA, \n" +
                    "AEAMARCA.ID_AEAMARCA, AEAMARCA.DESCRICAO AS DESCRICAO_MARCA, \n" +
                    "AEAUNVEN.ID_AEAUNVEN, AEAUNVEN.SIGLA, AEAUNVEN.DESCRICAO_SINGULAR, AEAUNVEN.DECIMAIS, \n" +
                    "AEAPLOJA.ID_AEAPLOJA, AEAPLOJA.ESTOQUE_F, AEAPLOJA.ESTOQUE_C, AEAPLOJA.RETIDO, AEAPLOJA.PEDIDO, \n" +
                    "AEAPLOJA.VENDA_ATAC, AEAPLOJA.VENDA_VARE \n" +

                    "FROM AEAPLOJA \n" +

                    "LEFT OUTER JOIN AEAPRODU \n" +
                    "ON(AEAPLOJA.ID_AEAPRODU = AEAPRODU.ID_AEAPRODU) \n" +
                    "LEFT OUTER JOIN AEAMARCA \n" +
                    "ON(AEAPRODU.ID_AEAMARCA = AEAMARCA.ID_AEAMARCA) \n" +
                    "LEFT OUTER JOIN AEAUNVEN \n" +
                    "ON(AEAPRODU.ID_AEAUNVEN = AEAUNVEN.ID_AEAUNVEN) \n ";
                    //"LEFT OUTER JOIN AEAEMBAL \n" +
                    //"ON(AEAEMBAL.ID_AEAPRODU = AEAPRODU.ID_AEAPRODU) \n";

            if (where != null){
                sql += " WHERE (" + where + ") AND " +
                       " (AEAPLOJA.ID_SMAEMPRE = " + ((!funcoes.getValorXml("CodigoEmpresa").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? funcoes.getValorXml("CodigoEmpresa") : "1") + ") ";
                       //" AND (AEAPLOJA.ATIVO = '1')";
            }

            if (tipoConexao.equalsIgnoreCase("W")){
                // Instancia a classe para manipular o webservice
                WSSisInfoWebservice webserviceSisInfo = new WSSisInfoWebservice(context);
                Vector<SoapObject> listaDadosProduto = webserviceSisInfo.executarSelectWebservice(sql, WSSisInfoWebservice.FUNCTION_DADOS_PRODUTOS_LOJA_RESUMIDOS, null);

                if (listaDadosProduto != null && listaDadosProduto.size() > 0) {

                    // Instancia a lista
                    listaProdutoLojaRetorno = new ArrayList<ProdutoLojaBeans>();

                    // Pega o total de registros retornado do banco
                    final int totalRegistro = listaDadosProduto.size();

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

                    for(SoapObject dadosProdutoIndividual : listaDadosProduto){
                        if (progresso != null) {
                            incremento++;
                            final int finalIncremento = incremento;
                            ((Activity) context).runOnUiThread(new Runnable() {
                                public void run() {
                                    progresso.setProgress(finalIncremento);
                                }
                            });
                        }

                        SoapObject dadosProdutoLoja;
                        if (dadosProdutoIndividual.hasProperty("return")){
                            dadosProdutoLoja = (SoapObject) dadosProdutoIndividual.getProperty("return");

                        } else {
                            dadosProdutoLoja = dadosProdutoIndividual;
                        }
                        // Cria variavel para salvar os dados do produto por loja
                        ProdutoLojaBeans produtoLojaBeans = new ProdutoLojaBeans();

                        SoapObject dadosProduto = (SoapObject) dadosProdutoLoja.getProperty("produto");
                        // Instancia a variavel do produto
                        ProdutoBeans produtoBeans = new ProdutoBeans();
                        // Pega os dados que chegou do webservice
                        produtoBeans.setIdProduto(Integer.parseInt(dadosProduto.getProperty("idProduto").toString()));
                        produtoBeans.setCodigo(Integer.parseInt(dadosProduto.getProperty("codigo").toString()));
                        produtoBeans.setCodigoEstrutural(dadosProduto.getProperty("codigoEstrutural").toString());
                        produtoBeans.setCodigoBarras((dadosProduto.hasProperty("codigoBarras")) ? dadosProduto.getProperty("codigoBarras").toString() : "");
                        produtoBeans.setGuid(dadosProduto.getProperty("guid").toString());
                        produtoBeans.setDescricaoProduto(dadosProduto.getProperty("descricaoProduto").toString());
                        produtoBeans.setDescricaoAuxiliar((dadosProduto.hasProperty("descricaoAuxiliar")) ? dadosProduto.getProperty("descricaoAuxiliar").toString() : "");
                        produtoBeans.setReferencia((dadosProduto.hasProperty("referencia")) ? dadosProduto.getProperty("referencia").toString() : "");

                        MarcaBeans marca = new MarcaBeans();
                        SoapObject objetoMarca = (SoapObject) dadosProduto.getProperty("marca");
                        marca.setIdMarca(Integer.parseInt(objetoMarca.getProperty("idMarca").toString()));
                        marca.setDescricao((objetoMarca.hasProperty("descricao")) ? objetoMarca.getProperty("descricao").toString() : "");
                        produtoBeans.setMarca(marca);

                        UnidadeVendaBeans unidadeVenda = new UnidadeVendaBeans();
                        SoapObject objetoUnidadeVenda = (SoapObject) dadosProduto.getProperty("unidadeVenda");
                        unidadeVenda.setIdUnidadeVenda(Integer.parseInt(objetoUnidadeVenda.getProperty("idUnidadeVenda").toString()));
                        unidadeVenda.setSigla(objetoUnidadeVenda.getProperty("sigla").toString());
                        unidadeVenda.setDescricaoUnidadeVenda(objetoUnidadeVenda.getProperty("descricaoUnidadeVenda").toString());
                        unidadeVenda.setDecimais(Integer.parseInt(objetoUnidadeVenda.getProperty("decimais").toString()));
                        produtoBeans.setUnidadeVenda(unidadeVenda);

                        produtoLojaBeans.setProduto(produtoBeans);
                        produtoLojaBeans.setIdProdutoLoja(Integer.parseInt(dadosProdutoLoja.getProperty("idProdutoLoja").toString()));
                        produtoLojaBeans.setEstoqueFisico(Double.parseDouble(dadosProdutoLoja.getProperty("estoqueFisico").toString()));
                        produtoLojaBeans.setEstoqueContabil(Double.parseDouble(dadosProdutoLoja.getProperty("estoqueContabil").toString()));
                        produtoLojaBeans.setVendaAtacado(Double.parseDouble(dadosProdutoLoja.getProperty("vendaAtacado").toString()));
                        produtoLojaBeans.setVendaVarejo(Double.parseDouble(dadosProdutoLoja.getProperty("vendaVarejo").toString()));
                        produtoLojaBeans.setPedido(Double.parseDouble(dadosProdutoLoja.getProperty("pedido").toString()));
                        produtoLojaBeans.setRetido(Double.parseDouble(dadosProdutoLoja.getProperty("retido").toString()));

                        listaProdutoLojaRetorno.add(produtoLojaBeans);
                    }
                }
            } else {
                ProdutoSql produtoSql = new ProdutoSql(context);

                Cursor cursor = produtoSql.sqlSelect(sql);

                if ((cursor != null) && (cursor.getCount() > 0)) {
                    // Move o cursor para o primeiro registro
                    cursor.moveToFirst();

                    return null;
                }
            }

        } catch (Exception e){
            final FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

            // Armazena as informacoes para para serem exibidas e enviadas
            final ContentValues contentValues = new ContentValues();
            contentValues.put("comando", 0);
            contentValues.put("tela", "ProdutoRotinas");
            contentValues.put("mensagem", "Não conseguimos pegar a descrição do produto. \n" + funcoes.tratamentoErroBancoDados(e.getMessage()));
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

        return listaProdutoLojaRetorno;
    }

    /**
     * Atualiza os dados dos produtos.
     * Tem que ser passado uma instrucao sql ("UPDATE TABLE SET COLUMN = VALUE WHERE ID = ID").
     *
     * @param sql
     * @param progressBarStatus
     * @param textStatus
     * @return
     */
    public boolean updateProduto(String sql, ProgressBar progressBarStatus, final TextView textStatus){

        try{
            if (getTipoConexao().equalsIgnoreCase("W")){
                // Cria uma lista para salvar todas as propriedades
                List<PropertyInfo> listaPropertyInfos = new ArrayList<PropertyInfo>();

                PropertyInfo propertySql = new PropertyInfo();
                propertySql.setName("sql");
                propertySql.setValue(sql);
                propertySql.setType(sql.getClass());
                // Adiciona a propriedade na lista
                listaPropertyInfos.add(propertySql);

                if (textStatus != null){
                    ((Activity) context).runOnUiThread(new Runnable() {
                        public void run() {
                            textStatus.setText(context.getResources().getString(R.string.enviando_dados_servidor_webservice));
                        }
                    });
                }
                WSSisInfoWebservice webserviceSisInfo = new WSSisInfoWebservice(context);
                // Executa o webservice
                final RetornoWebServiceBeans retorno = webserviceSisInfo.executarWebservice(listaPropertyInfos, WSSisInfoWebservice.FUNCTION_UPDATE_PRODUTO);

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
                /*ContentValues dadosEmbalagem = new ContentValues();
                dadosEmbalagem.put("ID_AEAUNVEN", produto.getUnidadeVenda().getIdUnidadeVenda());
                dadosEmbalagem.put("DESCRICAO", produto.getDescricaoEmbalagem());
                dadosEmbalagem.put("MODULO", produto.getModulo());
                dadosEmbalagem.put("DECIMAIS", produto.getDecimais());
                dadosEmbalagem.put("ATIVO", produto.getAtivo());
                dadosEmbalagem.put("CODIGO_BARRAS", produto.getCodigoBarras());
                dadosEmbalagem.put("REFERENCIA", produto.getReferencia());*/
            }

        } catch (Exception e){
            final FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

            // Armazena as informacoes para para serem exibidas e enviadas
            final ContentValues contentValues = new ContentValues();
            contentValues.put("comando", 0);
            contentValues.put("tela", "EmbalagemRotina");
            contentValues.put("mensagem", "Erro ao atualizar o codigo de barras do produto. \n");
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
} // Fim da classe