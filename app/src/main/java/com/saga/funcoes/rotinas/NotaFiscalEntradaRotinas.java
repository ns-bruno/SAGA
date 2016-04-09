package com.saga.funcoes.rotinas;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.widget.ProgressBar;

import com.saga.banco.interno.funcoesSql.ItemNotaFiscalEntradaSql;
import com.saga.banco.interno.funcoesSql.NotaFiscalEntradaSql;
import com.saga.banco.interno.funcoesSql.ProdutoSql;
import com.saga.beans.ClasseProdutosBeans;
import com.saga.beans.ClifoBeans;
import com.saga.beans.CodigoOrigemProdutoBeans;
import com.saga.beans.EstoqueBeans;
import com.saga.beans.FamiliaProdutoBeans;
import com.saga.beans.GrupoProdutoBeans;
import com.saga.beans.ItemNotaFiscalEntradaBeans;
import com.saga.beans.LocesBeans;
import com.saga.beans.MarcaBeans;
import com.saga.beans.NaturezaBeans;
import com.saga.beans.NotaFiscalEntradaBeans;
import com.saga.beans.ProdutoBeans;
import com.saga.beans.ProdutoLojaBeans;
import com.saga.beans.SubGrupoProdutoBeans;
import com.saga.beans.TipoGradeBeans;
import com.saga.beans.UnidadeVendaBeans;
import com.saga.funcoes.FuncoesPersonalizadas;
import com.saga.webservice.WSSisInfoWebservice;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Bruno Nogueira Silva on 21/01/2016.
 */
public class NotaFiscalEntradaRotinas extends Rotinas {

    public static final int CONFERIDO = 999;
    public static final int SEM_CONFERIR = 888;
    public static final int SIM = 777, NAO = 666;

    public NotaFiscalEntradaRotinas(Context context) {
        super(context);
    }


    public List<NotaFiscalEntradaBeans> listaNotaFiscalEntrada(String where, int conferidoSemConferir, final ProgressBar progresso){

        List<NotaFiscalEntradaBeans> listaNotaFiscal = new ArrayList<NotaFiscalEntradaBeans>();
        try {
            FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);
            int limiteDiasConferir = (!funcoes.getValorXml("LimiteDiasConferir").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? Integer.parseInt(funcoes.getValorXml("LimiteDiasConferir")) : 7;

            String sql = "SELECT AEAENTRA.ID_AEAENTRA, AEAENTRA.ID_SMAEMPRE, AEAENTRA.ID_AEASERIE, AEAENTRA.GUID, " +
                    "AEAENTRA.DT_ALT, AEAENTRA.TIPO, AEAENTRA.SERIE, AEAENTRA.NUMERO, AEAENTRA.DT_EMISSAO, " +
                    "AEAENTRA.DT_SAIDA, AEAENTRA.DT_ENTRADA, AEAENTRA.DT_ESTORNO, AEAENTRA.VL_MERCADORIA, " +
                    "AEAENTRA.FC_VL_TOTAL, AEAENTRA.SITUACAO, AEAENTRA.FECHADO, AEAENTRA.TIPO_BAIXA, AEAENTRA.OBS, " +
                    "AEAENTRA.CHV_NFE , AEAENTRA.TIPO_EMISSAO, AEAENTRA.FINALIDADE, " +
                    "CFACLIFO.ID_CFACLIFO, CFACLIFO.NOME_RAZAO, CFACLIFO.NOME_FANTASIA, CFACLIFO.CPF_CGC, " +
                    "CFACLIFO.CODIGO_FOR, CFACLIFO.FORNECEDOR, " +
                    "AEANATUR.ID_AEANATUR, AEANATUR.CODIGO, AEANATUR.DESCRICAO AS DESCRICAO_NATUR " +
                    " FROM AEAENTRA " +
                    "LEFT OUTER JOIN CFACLIFO CFACLIFO " +
                    "ON(AEAENTRA.ID_CFACLIFO = CFACLIFO.ID_CFACLIFO) " +
                    "LEFT OUTER JOIN AEANATUR AEANATUR " +
                    "ON(AEAENTRA.ID_AEANATUR = AEANATUR.ID_AEANATUR) ";

            if (conferidoSemConferir == SEM_CONFERIR) {
                sql += " WHERE ((AEAENTRA.SITUACAO = 0) OR (AEAENTRA.SITUACAO = 1)) ";

            } else if (conferidoSemConferir == CONFERIDO){
                sql += " WHERE ((AEAENTRA.SITUACAO != 0) AND (AEAENTRA.SITUACAO != 1)) ";

            } else {
                sql += " WHERE (AEAENTRA.SITUACAO >= 0)  ";
            }

            if (tipoConexao.equalsIgnoreCase("W")){
                sql += " AND (AEAENTRA.DT_ENTRADA >= (SELECT DATEADD(DAY, -" + limiteDiasConferir + ", CURRENT_DATE) FROM RDB$DATABASE)) ";
            }

            // Checa se tem alguma empresa relacionada
            if (!funcoes.getValorXml("CodigoEmpresa").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)){
                sql += " AND (AEAENTRA.ID_SMAEMPRE = " + funcoes.getValorXml("CodigoEmpresa") + ") ";
            }

            // Adiciona a clausula where passada por parametro no sql
            if (where != null) {
                sql += " AND ( " + where + " ) ";
            }

            sql += "ORDER BY AEAENTRA.DT_ENTRADA DESC, AEAENTRA.SITUACAO ASC ";

            if (tipoConexao.equalsIgnoreCase("W")){

                WSSisInfoWebservice webserviceSisInfo = new WSSisInfoWebservice(context);
                final Vector<SoapObject> listaNota = webserviceSisInfo.executarSelectWebservice(sql, WSSisInfoWebservice.FUNCTION_SELECT_LISTA_NOTA_FISCAL_ENTRADA, null);

                // Checa se retornou alguma coisa
                if (listaNota != null && listaNota.size() > 0){

                    // Checa se tem alguma barra de progresso
                    if (progresso != null) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            public void run() {
                                progresso.setIndeterminate(false);
                                progresso.setProgress(0);
                                progresso.setMax(listaNota.size());
                            }
                        });
                    }
                    int incremento = 0;
                    // Passa por toda a lista
                    for (SoapObject objetoIndividual : listaNota) {
                        if (progresso != null) {

                            incremento++;
                            final int finalIncremento = incremento;
                            ((Activity) context).runOnUiThread(new Runnable() {
                                public void run() {
                                    progresso.setProgress(finalIncremento);
                                }
                            });
                        }
                        NotaFiscalEntradaBeans notaFiscalEntrada = new NotaFiscalEntradaBeans();

                        SoapObject objeto;

                        if (objetoIndividual.hasProperty("return")){
                            objeto = (SoapObject) objetoIndividual.getProperty("return");
                        } else {
                            objeto = objetoIndividual;
                        }
                        notaFiscalEntrada.setIdNotaFiscalEntrada(Integer.parseInt(objeto.getProperty("idNotaFiscalEntrada").toString()));
                        notaFiscalEntrada.setIdEmpresa(Integer.parseInt(objeto.getProperty("idEmpresa").toString()));
                        notaFiscalEntrada.setIdSerie((objeto.hasProperty("idSerie")) ? Integer.parseInt(objeto.getProperty("idSerie").toString()) : -1);
                        notaFiscalEntrada.setGuid(objeto.getProperty("guid").toString());
                        notaFiscalEntrada.setDataAlteracao(objeto.getProperty("dataAlteracao").toString());
                        notaFiscalEntrada.setTipoEntrada(objeto.getProperty("tipoEntrada").toString());
                        notaFiscalEntrada.setSerie((objeto.hasProperty("serie")) ? objeto.getProperty("serie").toString() : "");
                        notaFiscalEntrada.setNumeroEntrada(Integer.parseInt(objeto.getProperty("numeroEntrada").toString()));
                        notaFiscalEntrada.setDataEmissao(objeto.getProperty("dataEmissao").toString());
                        notaFiscalEntrada.setDataSaida((objeto.hasProperty("dataSaida")) ? objeto.getProperty("dataSaida").toString() : "");
                        notaFiscalEntrada.setDataEntrada(objeto.getProperty("dataEntrada").toString());
                        notaFiscalEntrada.setDataEstorno((objeto.hasAttribute("dataEstorno")) ? objeto.getProperty("dataEstorno").toString() : "");
                        notaFiscalEntrada.setValorMercadoria(Double.parseDouble(objeto.getProperty("valorMercadoria").toString()));
                        notaFiscalEntrada.setValorTotalEntrada(Double.parseDouble(objeto.getProperty("valorTotalEntrada").toString()));
                        notaFiscalEntrada.setSituacao((objeto.hasProperty("situacao")) ? Integer.parseInt(objeto.getProperty("situacao").toString()) : -1);
                        notaFiscalEntrada.setFechado((objeto.hasProperty("fechado")) ? Integer.parseInt(objeto.getProperty("fechado").toString()) : -1);
                        notaFiscalEntrada.setTipoBaixa(objeto.getProperty("tipoBaixa").toString());
                        notaFiscalEntrada.setObservacao((objeto.hasProperty("observacao")) ? objeto.getProperty("observacao").toString() : "");
                        notaFiscalEntrada.setChaveNfe((objeto.hasProperty("chaveNfe")) ? objeto.getProperty("chaveNfe").toString() : "");
                        notaFiscalEntrada.setTipoEmissao((objeto.hasProperty("tipoEmissao")) ? objeto.getProperty("tipoEmissao").toString() : "");
                        notaFiscalEntrada.setFinalidade(objeto.hasProperty("finalidade") ? objeto.getProperty("finalidade").toString() : "");

                        // Salva os dados do fornecedor
                        ClifoBeans dadosFornecedor = new ClifoBeans();
                        SoapObject objetoClifo = (SoapObject) objeto.getProperty("clifo");
                        dadosFornecedor.setIdCLifo(Integer.parseInt(objetoClifo.getProperty("idCLifo").toString()));
                        dadosFornecedor.setNomeRazao(objetoClifo.getProperty("nomeRazao").toString());
                        dadosFornecedor.setNomeFantasia((objetoClifo.hasProperty("nomeFantasia")) ? objetoClifo.getProperty("nomeFantasia").toString() : "");
                        dadosFornecedor.setCpfCnpj(objetoClifo.getProperty("cpfCnpj").toString());
                        dadosFornecedor.setCodigoFornecedor((objetoClifo.hasProperty("codigoFornecedor")) ? Integer.parseInt(objetoClifo.getProperty("codigoFornecedor").toString()) : -1);
                        dadosFornecedor.setFornecedor((objetoClifo.hasProperty("fornecedor")) ? objetoClifo.getProperty("fornecedor").toString() : "");
                        // Adiciona os dados do fornecedor a nota fiscal
                        notaFiscalEntrada.setClifo(dadosFornecedor);

                        // Salva os dados da natureza
                        NaturezaBeans naturezaBeans = new NaturezaBeans();
                        SoapObject objetoNatureza = (SoapObject) objeto.getProperty("natureza");
                        naturezaBeans.setIdNatureza((objetoNatureza.hasProperty("idNatureza")) ? Integer.parseInt(objetoNatureza.getProperty("idNatureza").toString()) : -1);
                        naturezaBeans.setCodigo((objetoNatureza.hasProperty("codigo")) ? Integer.parseInt(objetoNatureza.getProperty("codigo").toString()) : -1);
                        naturezaBeans.setDescricaoNatureza((objetoNatureza.hasProperty("descricaoNatureza")) ? objetoNatureza.getProperty("descricaoNatureza").toString() : "");
                        // Adiciona os dados da natureza a nota fiscal
                        notaFiscalEntrada.setNatureza(naturezaBeans);

                        // Adiciona os dados da nota a uma lista
                        listaNotaFiscal.add(notaFiscalEntrada);
                    }
                    //int i = listaNotaFiscal.size();
                    //String s = listaNotaFiscal.toString();
                }

            } else {
                NotaFiscalEntradaSql notaFiscalEntradaSql = new NotaFiscalEntradaSql(context);

                final Cursor cursor = notaFiscalEntradaSql.sqlSelect(sql);

                // Checa se retornou alguma coisa do banco
                if ((cursor != null) && (cursor.getCount() > 0)) {

                    // Checa se tem alguma barra de progresso
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

                        NotaFiscalEntradaBeans notaFiscalEntrada = new NotaFiscalEntradaBeans();
                        notaFiscalEntrada.setIdNotaFiscalEntrada(cursor.getInt(cursor.getColumnIndex("ID_AEAENTRA")));
                        notaFiscalEntrada.setIdEmpresa(cursor.getInt(cursor.getColumnIndex("ID_SMAEMPRE")));
                        notaFiscalEntrada.setIdSerie(cursor.getInt(cursor.getColumnIndex("ID_AEASERIE")));
                        notaFiscalEntrada.setGuid(cursor.getString(cursor.getColumnIndex("GUID")));
                        notaFiscalEntrada.setDataAlteracao(cursor.getString(cursor.getColumnIndex("DT_ALT")));
                        notaFiscalEntrada.setTipoEntrada(cursor.getString(cursor.getColumnIndex("TIPO")));
                        notaFiscalEntrada.setSerie(cursor.getString(cursor.getColumnIndex("SERIE")));
                        notaFiscalEntrada.setNumeroEntrada(cursor.getInt(cursor.getColumnIndex("NUMERO")));
                        notaFiscalEntrada.setDataEmissao(cursor.getString(cursor.getColumnIndex("DT_EMISSAO")));
                        notaFiscalEntrada.setDataSaida(cursor.getString(cursor.getColumnIndex("DT_SAIDA")));
                        notaFiscalEntrada.setDataEntrada(cursor.getString(cursor.getColumnIndex("DT_ENTRADA")));
                        notaFiscalEntrada.setDataEstorno(cursor.getString(cursor.getColumnIndex("DT_ESTORNO")));
                        notaFiscalEntrada.setValorMercadoria(cursor.getDouble(cursor.getColumnIndex("VL_MERCADORIA")));
                        notaFiscalEntrada.setValorTotalEntrada(cursor.getDouble(cursor.getColumnIndex("FC_VL_TOTAL")));
                        notaFiscalEntrada.setSituacao(cursor.getInt(cursor.getColumnIndex("SITUACAO")));
                        notaFiscalEntrada.setFechado(cursor.getInt(cursor.getColumnIndex("FECHADO")));
                        notaFiscalEntrada.setTipoBaixa(cursor.getString(cursor.getColumnIndex("TIPO_BAIXA")));
                        notaFiscalEntrada.setObservacao(cursor.getString(cursor.getColumnIndex("OBS")));
                        notaFiscalEntrada.setChaveNfe(cursor.getString(cursor.getColumnIndex("CHV_NFE")));
                        notaFiscalEntrada.setTipoEmissao(cursor.getString(cursor.getColumnIndex("TIPO_EMISSAO")));
                        notaFiscalEntrada.setFinalidade(cursor.getString(cursor.getColumnIndex("FINALIDADE")));

                        // Salva os dados do fornecedor
                        ClifoBeans dadosFornecedor = new ClifoBeans();
                        dadosFornecedor.setIdCLifo(cursor.getInt(cursor.getColumnIndex("ID_CFACLIFO")));
                        dadosFornecedor.setNomeRazao(cursor.getString(cursor.getColumnIndex("NOME_RAZAO")));
                        dadosFornecedor.setNomeFantasia(cursor.getString(cursor.getColumnIndex("NOME_FANTASIA")));
                        dadosFornecedor.setCpfCnpj(cursor.getString(cursor.getColumnIndex("CPF_CNPJ")));
                        dadosFornecedor.setCodigoFornecedor(cursor.getInt(cursor.getColumnIndex("CODIGO_FOR")));
                        dadosFornecedor.setFornecedor(cursor.getString(cursor.getColumnIndex("FORNECEDOR")));
                        // Adiciona os dados do fornecedor a nota fiscal
                        notaFiscalEntrada.setClifo(dadosFornecedor);

                        // Salva os dados da natureza
                        NaturezaBeans naturezaBeans = new NaturezaBeans();
                        naturezaBeans.setIdNatureza(cursor.getInt(cursor.getColumnIndex("ID_AEANATUR")));
                        naturezaBeans.setIdNatureza(cursor.getInt(cursor.getColumnIndex("CODIGO")));
                        naturezaBeans.setDescricaoNatureza(cursor.getString(cursor.getColumnIndex("DESCRICAO_NATUR")));
                        // Adiciona os dados da natureza a nota fiscal
                        notaFiscalEntrada.setNatureza(naturezaBeans);

                        // Adiciona os dados da nota a uma lista
                        listaNotaFiscal.add(notaFiscalEntrada);
                    } // Fim while
                }
            }
        } catch (Exception e){

            final FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

            // Armazena as informacoes para para serem exibidas e enviadas
            final ContentValues contentValues = new ContentValues();
            contentValues.put("comando", 0);
            contentValues.put("tela", "NotaFiscalEntradaRotinas");
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

        return listaNotaFiscal;
    } // Fim listaNotaFiscalEntrada


    public List<ItemNotaFiscalEntradaBeans> listaItemNotaFiscalEntrada(int idNFEntrada, int resumido, String where, int conferidoSemConferir, final ProgressBar progresso){

        List<ItemNotaFiscalEntradaBeans> listaItem = new ArrayList<ItemNotaFiscalEntradaBeans>();

        try{
            String sql =    "SELECT AEAITENT.ID_AEAITENT, AEAITENT.ID_AEAENTRA, AEAITENT.ID_AEAITSAI, AEAITENT.ID_AEAITPED, AEAITENT.GUID AS GUID_ITENT, " +
                            "AEAITENT.DT_CAD AS DT_CAD_ITENT, AEAITENT.DT_ALT AS DT_ALT_ITENT, AEAITENT.DT_ENTRADA, AEAITENT.SEQUENCIA, AEAITENT.QTDE_DAT_VAL, " +
                            "AEAITENT.QTDE_CONFERIDO, AEAITENT.VL_MERCADORIA, AEAITENT.TIPO AS TIPO_ITENT, AEAITENT.TIPO_PRODUTO, AEAITENT.TIPO_BAIXA, AEAITENT.OBS, " +
                            "AEAITENT.FC_MERCADORIA_UN, AEAITENT.FC_CT_COMP, AEAITENT.QUANTIDADE, \n" +

                            "AEAUNVEN.ID_AEAUNVEN, AEAUNVEN.DT_ALT AS DT_ALT_UNVEN, AEAUNVEN.SIGLA AS SIGLA_UNVEN, AEAUNVEN.DESCRICAO_SINGULAR, AEAUNVEN.DECIMAIS, \n" +

                            "AEAESTOQ.ID_AEAESTOQ, AEAESTOQ.DT_ALT AS DT_ALT_ESTOQ, AEAESTOQ.ATIVO AS ATIVO_ESTOQ, AEAESTOQ.ESTOQUE, AEAESTOQ.RETIDO AS RETIDO_ESTOQ, " +
                            "AEAESTOQ.LOCACAO_ATIVA, AEAESTOQ.LOCACAO_RESERVA, \n" +

                            "AEAPLOJA.ID_AEAPLOJA, AEAPLOJA.ID_SMAEMPRE AS ID_SMAEMPRE_PLOJA, AEAPLOJA.DT_ALT AS DT_ALT_PLOJA, AEAPLOJA.ESTOQUE_C, AEAPLOJA.ESTOQUE_F, " +
                            "AEAPLOJA.RETIDO AS RETIDO_PLOJA, AEAPLOJA.PEDIDO AS PEDIDO_PLOJA, AEAPLOJA.VENDA_ATAC, AEAPLOJA.VENDA_VARE, AEAPLOJA.ATIVO AS ATIVO_PLOJA, \n";

            if (resumido == NAO) {
            sql +=          "AEALOCES.ID_AEALOCES, AEALOCES.ID_SMAEMPRE AS ID_SMAEMPRE_LOCES, AEALOCES.DT_ALT AS DT_ALT_LOCES, AEALOCES.CODIGO AS CODIGO_LOCES, " +
                            "AEALOCES.ATIVO AS ATIVO_LOCES, AEALOCES.DESCRICAO AS DESCRICAO_LOCES, \n";
            }

            sql +=         "AEACODOM.ID_AEACODOM, AEACODOM.DT_ALT AS DT_ALT_CODOM, AEACODOM.CODIGO AS CODIGO_CODOM, AEACODOM.DESCRICAO AS DESCRICAO_CODOM, \n" +

                            "AEAPRODU.ID_AEAPRODU, AEAPRODU.GUID AS GUID_PRODU, AEAPRODU.DT_ALT AS DT_ALT_PRODU, AEAPRODU.DESCRICAO AS DESCRICAO_PRODU, " +
                            "AEAPRODU.DESCRICAO_AUXILIAR, AEAPRODU.DESCRICAO_MASCARA, AEAPRODU.CODIGO AS CODIGO_PRODU, AEAPRODU.CODIGO_ESTRUTURAL, AEAPRODU.REFERENCIA, " +
                            "AEAPRODU.CODIGO_BARRAS, AEAPRODU.PESO_LIQUIDO, AEAPRODU.PESO_BRUTO, AEAPRODU.ATIVO AS ATIVO_PRODU, AEAPRODU.TIPO AS TIPO_PRODU, " +
                            "AEAPRODU.GENERO, AEAPRODU.VALIDADE, AEAPRODU.GARANTIA, AEAPRODU.CONTROLE_SERIAL, AEAPRODU.TIPO_ITEM, AEAPRODU.ROMANEIA, \n";

            if (resumido == NAO) {
                sql +=      "AEAFAMIL.ID_AEAFAMIL, AEAFAMIL.DT_ALT AS DT_ALT_FAMIL, AEAFAMIL.CODIGO AS CODIGO_FAMIL, AEAFAMIL.DESCRICAO AS DESCRICAO_FAMIL,\n" +

                            "AEACLASE.ID_AEACLASE, AEACLASE.DT_ALT AS DT_ALT_CLASE, AEACLASE.CODIGO AS CODIGO_CLASE, AEACLASE.DESCRICAO AS DESCRICAO_CLASE,\n" +

                            "AEAGRUPO.ID_AEAGRUPO, AEAGRUPO.ID_AEACLASE AS ID_AEACLASE_GRUPO, AEAGRUPO.DT_ALT AS DT_ALT_GRUPO, AEAGRUPO.CODIGO AS CODIGO_GRUPO, " +
                            "AEAGRUPO.DESCRICAO AS DESCRICAO_GRUPO,\n" +

                            "AEASGRUP.ID_AEASGRUP, AEASGRUP.ID_AEAGRUPO AS ID_AEAGRUPO_SGRUP, AEASGRUP.DT_ALT AS DT_ALT_SGRUP, AEASGRUP.CODIGO AS CODIGO_SGRUP, " +
                            "AEASGRUP.DESCRICAO AS DESCRICAO_SGRUP, \n";
            }

            sql +=          "AEAMARCA.ID_AEAMARCA, AEAMARCA.ID_CFACLIFO AS ID_CFACLIFO_MARCA, AEAMARCA.DT_ALT AS DT_ALT_MARCA, AEAMARCA.DESCRICAO AS DESCRICAO_MARCA, \n" +

                            "AEAUNVEN_PRODU.ID_AEAUNVEN AS ID_AEAUNVEN_PRODU, AEAUNVEN_PRODU.DT_ALT AS DT_ALT_UNVEN_PRODU, AEAUNVEN_PRODU.SIGLA AS SIGLA_UNVEN_PRODU, " +
                            "AEAUNVEN_PRODU.DESCRICAO_SINGULAR AS DESCRICAO_UNVEN_PRODU, AEAUNVEN_PRODU.DECIMAIS AS DECIMAIS_UNVEN_PRODU,\n" +

                            "AEATPGRD.ID_AEATPGRD, AEATPGRD.DT_ALT AS DT_ALT_TPGRD, AEATPGRD.DESCRICAO AS DESCRICAO_TPGRD ";

            if (resumido == NAO) {
                sql +=      ",\n AEACODOM_PRODU.ID_AEACODOM AS ID_AEACODOM_PRODU, AEACODOM_PRODU.DT_ALT AS DT_ALT_CODOM_PRODU, AEACODOM_PRODU.CODIGO AS CODIGO_CODOM_PRODU, " +
                            "AEACODOM_PRODU.DESCRICAO AS DESCRICAO_CODOM_PRODU \n";
            }

            sql +=         "FROM AEAITENT \n" +

                            "LEFT OUTER JOIN AEAUNVEN AEAUNVEN \n" +
                            "ON(AEAITENT.ID_AEAUNVEN = AEAUNVEN.ID_AEAUNVEN) \n" +
                            "LEFT OUTER JOIN AEAESTOQ AEAESTOQ \n" +
                            "ON(AEAITENT.ID_AEAESTOQ = AEAESTOQ.ID_AEAESTOQ) \n" +
                            "LEFT OUTER JOIN AEAPLOJA AEAPLOJA \n" +
                            "ON(AEAESTOQ.ID_AEAPLOJA = AEAPLOJA.ID_AEAPLOJA) \n";
            if (resumido == NAO) {
                sql +=      "LEFT OUTER JOIN AEALOCES AEALOCES \n" +
                            "ON(AEAESTOQ.ID_AEALOCES = AEALOCES.ID_AEALOCES) \n";
            }
            sql +=          "LEFT OUTER JOIN AEACODOM AEACODOM \n" +
                            "ON(AEAITENT.ID_AEACODOM = AEACODOM.ID_AEACODOM) \n" +
                            "LEFT OUTER JOIN AEAPRODU AEAPRODU \n" +
                            "ON(AEAPLOJA.ID_AEAPRODU = AEAPRODU.ID_AEAPRODU) \n";
            if (resumido == NAO) {
                sql +=      "LEFT OUTER JOIN AEAFAMIL AEAFAMIL \n" +
                            "ON(AEAPRODU.ID_AEAFAMIL = AEAFAMIL.ID_AEAFAMIL) \n" +
                            "LEFT OUTER JOIN AEACLASE AEACLASE \n" +
                            "ON(AEAPRODU.ID_AEACLASE = AEACLASE.ID_AEACLASE) \n" +
                            "LEFT OUTER JOIN AEAGRUPO AEAGRUPO \n" +
                            "ON(AEAPRODU.ID_AEAGRUPO = AEAGRUPO.ID_AEAGRUPO) \n" +
                            "LEFT OUTER JOIN AEASGRUP AEASGRUP \n" +
                            "ON(AEAPRODU.ID_AEASGRUP = AEASGRUP.ID_AEASGRUP) \n";
            }
            sql +=          "LEFT OUTER JOIN AEAMARCA AEAMARCA \n" +
                            "ON(AEAPRODU.ID_AEAMARCA = AEAMARCA.ID_AEAMARCA) \n" +
                            "LEFT OUTER JOIN AEAUNVEN AEAUNVEN_PRODU \n" +
                            "ON(AEAPRODU.ID_AEAUNVEN = AEAUNVEN_PRODU.ID_AEAUNVEN) \n" +
                            "LEFT OUTER JOIN AEATPGRD AEATPGRD \n" +
                            "ON(AEAPRODU.ID_AEATPGRD = AEATPGRD.ID_AEATPGRD) \n";
            if (resumido == NAO) {
                sql +=      "LEFT OUTER JOIN AEACODOM AEACODOM_PRODU \n" +
                            "ON(AEAPRODU.ID_AEACODOM = AEACODOM_PRODU.ID_AEACODOM) \n";
            }
            sql +=          "WHERE (AEAITENT.ID_AEAENTRA = " + idNFEntrada + ") \n";

            if (conferidoSemConferir == SEM_CONFERIR) {
                sql += " AND (AEAITENT.QTDE_CONFERIDO < AEAITENT.QUANTIDADE) ";

            } else if (conferidoSemConferir == CONFERIDO) {
                sql += " AND (AEAITENT.QTDE_CONFERIDO >= AEAITENT.QUANTIDADE) ";
            }

            // Adiciona a clausula where passada por parametro no sql
            if (where != null) {
                sql += " AND ( " + where + " ) ";
            }
            sql += "ORDER BY AEAITENT.SEQUENCIA ASC";

            if (tipoConexao.equalsIgnoreCase("W")){

                // Pega a propriedade de resumido
                PropertyInfo propertyResumido = new PropertyInfo();
                propertyResumido.setName("resumido");
                propertyResumido.setValue(resumido);
                propertyResumido.setType(Integer.class);

                // Adiciona a propriedade em uma lista
                List<PropertyInfo> listaPropriedade = new ArrayList<PropertyInfo>();
                listaPropriedade.add(propertyResumido);

                // Instancia a classe para manipular o webservice
                WSSisInfoWebservice webserviceSisInfo = new WSSisInfoWebservice(context);
                Vector<SoapObject> listaItemNota = webserviceSisInfo.executarSelectWebservice(sql, WSSisInfoWebservice.FUNCTION_SELECT_LISTA_ITEM_NOTA_FISCAL_ENTRADA, listaPropriedade);

                // Checa se retornou alguma coisa
                if (listaItemNota != null && listaItemNota.size() > 0){
                    // Pega o total de registros retornado do banco
                    final int totalRegistro = listaItemNota.size();

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
                    for (SoapObject objetoIndividual : listaItemNota) {
                        if (progresso != null) {

                            incremento++;
                            final int finalIncremento = incremento;
                            ((Activity) context).runOnUiThread(new Runnable() {
                                public void run() {
                                    progresso.setProgress(finalIncremento);
                                }
                            });
                        }
                        SoapObject objeto;
                        if (objetoIndividual.hasProperty("return")){
                            objeto = (SoapObject) objetoIndividual.getProperty("return");

                        } else {
                            objeto = objetoIndividual;
                        }
                        ItemNotaFiscalEntradaBeans itemNotaFiscal = new ItemNotaFiscalEntradaBeans();
                        itemNotaFiscal.setIdItemNotaFiscalEntrada(Integer.parseInt(objeto.getProperty("idItemNotaFiscalEntrada").toString()));
                        itemNotaFiscal.setIdEntrada(Integer.parseInt(objeto.getProperty("idEntrada").toString()));
                        itemNotaFiscal.setIdItemNotaFiscalSaida(Integer.parseInt(objeto.getProperty("idItemNotaFiscalSaida").toString()));
                        itemNotaFiscal.setIdItemPedidoCompras(Integer.parseInt(objeto.getProperty("idItemPedidoCompras").toString()));
                        itemNotaFiscal.setGuidItemEntrada(objeto.getProperty("guidItemEntrada").toString());
                        itemNotaFiscal.setDataCadastro(objeto.getProperty("dataCadastro").toString());
                        itemNotaFiscal.setDataAlteracao(objeto.getProperty("dataAlteracao").toString());
                        itemNotaFiscal.setDataEntrada(objeto.getProperty("dataEntrada").toString());
                        itemNotaFiscal.setSequencia(Integer.parseInt(objeto.getProperty("sequencia").toString()));
                        itemNotaFiscal.setQuantidadeDataValidade(Double.parseDouble(objeto.getProperty("quantidadeDataValidade").toString()));
                        itemNotaFiscal.setQuantidadeConferido(Double.parseDouble(objeto.getProperty("quantidadeConferido").toString()));
                        itemNotaFiscal.setValorMercadoria(Double.parseDouble(objeto.getProperty("valorMercadoria").toString()));
                        itemNotaFiscal.setTipo(objeto.getProperty("tipo").toString());
                        itemNotaFiscal.setTipoProduto(objeto.getProperty("tipoProduto").toString());
                        itemNotaFiscal.setTipoBaixa(objeto.getProperty("tipoBaixa").toString());
                        itemNotaFiscal.setObservacao((objeto.hasProperty("observacao")) ? objeto.getProperty("observacao").toString() : "");
                        itemNotaFiscal.setUnitarioItemMercadoria(Double.parseDouble(objeto.getProperty("unitarioItemMercadoria").toString()));
                        itemNotaFiscal.setTotalItem(Double.parseDouble(objeto.getProperty("totalItem").toString()));
                        itemNotaFiscal.setQuantidade(Double.parseDouble(objeto.getProperty("quantidade").toString()));

                        // Adiciona o codigo de origem da nota
                        CodigoOrigemProdutoBeans codigoOrigemItemNF = new CodigoOrigemProdutoBeans();
                        SoapObject objetoOrigem = (SoapObject) objeto.getProperty("codigoOrigem");
                        codigoOrigemItemNF.setIdCodigoOrigem(Integer.parseInt(objetoOrigem.getProperty("idCodigoOrigem").toString()));
                        codigoOrigemItemNF.setDataAlt(objetoOrigem.getProperty("dataAlt").toString());
                        codigoOrigemItemNF.setCodigo(Integer.parseInt(objetoOrigem.getProperty("codigo").toString()));
                        codigoOrigemItemNF.setDescricaoCodigoOrigem(objetoOrigem.getProperty("descricaoCodigoOrigem").toString());
                        // Adiciona a oregem ao produto
                        itemNotaFiscal.setCodigoOrigem(codigoOrigemItemNF);

                        // Pega a unidade de venda da item da nota fiscal
                        UnidadeVendaBeans unidadeItem = new UnidadeVendaBeans();
                        SoapObject objetoUnidadeItem = (SoapObject) objeto.getProperty("unidadeVenda");
                        unidadeItem.setIdUnidadeVenda(Integer.parseInt(objetoUnidadeItem.getProperty("idUnidadeVenda").toString()));
                        unidadeItem.setDataAlt(objetoUnidadeItem.getProperty("dataAlt").toString());
                        unidadeItem.setSigla(objetoUnidadeItem.getProperty("sigla").toString());
                        unidadeItem.setDescricaoUnidadeVenda(objetoUnidadeItem.getProperty("descricaoUnidadeVenda").toString());
                        unidadeItem.setDecimais(Integer.parseInt(objetoUnidadeItem.getProperty("decimais").toString()));
                        itemNotaFiscal.setUnidadeVenda(unidadeItem);

                        SoapObject objetoEstoque = (SoapObject) objeto.getProperty("estoque");
                        SoapObject objetoProdutoLoja = (SoapObject) objetoEstoque.getProperty("produtoLoja");
                        SoapObject objetoProduto = (SoapObject) objetoProdutoLoja.getProperty("produto");

                        // Pega os dados do produto
                        ProdutoBeans produto = new ProdutoBeans();
                        //SoapObject objetoProduto = (SoapObject) objeto.getProperty("estoque");
                        produto.setIdProduto(Integer.parseInt(objetoProduto.getProperty("idProduto").toString()));
                        produto.setGuid(objetoProduto.getProperty("guid").toString());
                        produto.setDataAlt(objetoProduto.getProperty("dataAlt").toString());
                        produto.setDescricaoProduto(objetoProduto.getProperty("descricaoProduto").toString());
                        produto.setDescricaoAuxiliar((objetoProduto.hasProperty("descricaoAuxiliar")) ? objetoProduto.getProperty("descricaoAuxiliar").toString() : "");
                        produto.setMascara((objetoProduto.hasProperty("mascara")) ? objetoProduto.getProperty("mascara").toString() : "");
                        produto.setCodigo(Integer.parseInt(objetoProduto.getProperty("codigo").toString()));
                        produto.setCodigoEstrutural(objetoProduto.getProperty("codigoEstrutural").toString());
                        produto.setReferencia((objetoProduto.hasProperty("referencia")) ? objetoProduto.getProperty("referencia").toString() : "");
                        produto.setCodigoBarras((objetoProduto.hasProperty("codigoBarras")) ? objetoProduto.getProperty("codigoBarras").toString() : "");
                        produto.setPesoLiquido(Double.parseDouble(objetoProduto.getProperty("pesoLiquido").toString()));
                        produto.setPesoBruto(Double.parseDouble(objetoProduto.getProperty("pesoBruto").toString()));
                        produto.setAtivo(objetoProduto.getProperty("ativo").toString());
                        produto.setTipoProduto(objetoProduto.getProperty("tipoProduto").toString());
                        produto.setGenero((objetoProduto.hasProperty("genero")) ? objetoProduto.getProperty("genero").toString() : "");
                        produto.setValidade((objetoProduto.hasProperty("validade")) ? objetoProduto.getProperty("validade").toString() : "");
                        produto.setGarantia((objetoProduto.hasProperty("garantia")) ? objetoProduto.getProperty("garantia").toString() : "");
                        produto.setControleSerial((objetoProduto.hasProperty("controleSerial")) ? objetoProduto.getProperty("controleSerial").toString() : "");
                        produto.setTipoItem((objetoProduto.hasProperty("tipoItem")) ? objetoProduto.getProperty("tipoItem").toString() : "");
                        produto.setRomaneia((objetoProduto.hasProperty("romaneia")) ? objetoProduto.getProperty("romaneia").toString() : "");

                        // Pega os dados da familia do produto
                        if (resumido == NAO) {
                            FamiliaProdutoBeans familaProduto = new FamiliaProdutoBeans();
                            SoapObject objetoFamilia = (SoapObject) objetoProduto.getProperty("familia");
                            familaProduto.setIdFamilia(Integer.parseInt(objetoFamilia.getProperty("idFamilia").toString()));
                            familaProduto.setDataAlt(objetoFamilia.getProperty("dataAlt").toString());
                            familaProduto.setCodigo(Integer.parseInt(objetoFamilia.getProperty("codigo").toString()));
                            familaProduto.setDescricaoFamilia(objetoFamilia.getProperty("descricaoFamilia").toString());
                            // Adiciona a familia no produto
                            produto.setFamilia(familaProduto);

                            ClasseProdutosBeans classeProduto = new ClasseProdutosBeans();
                            SoapObject objetoClasse = (SoapObject) objetoProduto.getProperty("classe");
                            classeProduto.setIdClasse(Integer.parseInt(objetoClasse.getProperty("idClasse").toString()));
                            classeProduto.setDataAlt(objetoClasse.getProperty("dataAlt").toString());
                            classeProduto.setCodigo(Integer.parseInt(objetoClasse.getProperty("codigo").toString()));
                            classeProduto.setDescricaoClasse(objetoClasse.getProperty("descricaoClasse").toString());
                            // Adiciona a classe no produto
                            produto.setClasse(classeProduto);

                            GrupoProdutoBeans grupoProduto = new GrupoProdutoBeans();
                            SoapObject objetoGrupo = (SoapObject) objetoProduto.getProperty("grupo");
                            grupoProduto.setIdGrupo(Integer.parseInt(objetoGrupo.getProperty("idGrupo").toString()));
                            grupoProduto.setIdClasse(Integer.parseInt(objetoGrupo.getProperty("idClasse").toString()));
                            grupoProduto.setDataAlt(objetoGrupo.getProperty("dataAlt").toString());
                            grupoProduto.setCodigo(Integer.parseInt(objetoGrupo.getProperty("codigo").toString()));
                            grupoProduto.setDescricaoGrupo(objetoGrupo.getProperty("descricaoGrupo").toString());
                            // Adiciona o grupo no produto
                            produto.setGrupo(grupoProduto);

                            if (objetoProduto.hasProperty("subGrupo")) {
                                SubGrupoProdutoBeans subGrupoProduto = new SubGrupoProdutoBeans();
                                SoapObject objetoSubGrupo = (SoapObject) objetoProduto.getProperty("subGrupo");
                                subGrupoProduto.setIdSubgrupo(Integer.parseInt(objetoSubGrupo.getProperty("idSubgrupo").toString()));
                                subGrupoProduto.setIdGrupo(Integer.parseInt(objetoSubGrupo.getProperty("idGrupo").toString()));
                                subGrupoProduto.setDataAlt(objetoSubGrupo.getProperty("dataAlt").toString());
                                subGrupoProduto.setCodigo(Integer.parseInt(objetoSubGrupo.getProperty("codigo").toString()));
                                subGrupoProduto.setDescricaoSubgrupo(objetoSubGrupo.getProperty("descricaoSubgrupo").toString());
                                // Adiciona o subgrupo ao produto
                                produto.setSubGrupo(subGrupoProduto);
                            }

                            CodigoOrigemProdutoBeans codigoOrigemProduto = new CodigoOrigemProdutoBeans();
                            SoapObject objectOrigemProduto = (SoapObject) objetoProduto.getProperty("origemProduto");
                            codigoOrigemProduto.setIdCodigoOrigem(Integer.parseInt(objectOrigemProduto.getProperty("idCodigoOrigem").toString()));
                            codigoOrigemProduto.setDataAlt(objectOrigemProduto.getProperty("dataAlt").toString());
                            codigoOrigemProduto.setCodigo(Integer.parseInt(objectOrigemProduto.getProperty("codigo").toString()));
                            codigoOrigemProduto.setDescricaoCodigoOrigem(objectOrigemProduto.getProperty("descricaoCodigoOrigem").toString());
                            // Adiciona a origem do produto
                            produto.setCodigoOrigem(codigoOrigemProduto);
                        }

                        MarcaBeans marca = new MarcaBeans();
                        SoapObject objetoMarca = (SoapObject) objetoProduto.getProperty("marca");
                        marca.setIdMarca(Integer.parseInt(objetoMarca.getProperty("idMarca").toString()));
                        marca.setDataAlt(objetoMarca.getProperty("dataAlt").toString());
                        marca.setDescricao(objetoMarca.getProperty("descricao").toString());
                        // Adiciona a marca no produto
                        produto.setMarca(marca);

                        UnidadeVendaBeans unidadeProduto = new UnidadeVendaBeans();
                        SoapObject objetoUnidadeProduto = (SoapObject) objetoProduto.getProperty("unidadeVenda");
                        unidadeProduto.setIdUnidadeVenda(Integer.parseInt(objetoUnidadeProduto.getProperty("idUnidadeVenda").toString()));
                        unidadeProduto.setDataAlt(objetoUnidadeProduto.getProperty("dataAlt").toString());
                        unidadeProduto.setSigla(objetoUnidadeProduto.getProperty("sigla").toString());
                        unidadeProduto.setDescricaoUnidadeVenda(objetoUnidadeProduto.getProperty("descricaoUnidadeVenda").toString());
                        unidadeProduto.setDecimais(Integer.parseInt(objetoUnidadeProduto.getProperty("decimais").toString()));
                        // Adiciona a unidade no produto
                        produto.setUnidadeVenda(unidadeProduto);

                        if (objeto.hasProperty("tipoGrade")) {
                            TipoGradeBeans tipoGrade = new TipoGradeBeans();
                            SoapObject objetoTipoGrade = (SoapObject) objetoProduto.getProperty("tipoGrade");
                            tipoGrade.setIdTipoGrade(Integer.parseInt(objetoTipoGrade.getProperty("idTipoGrade").toString()));
                            tipoGrade.setDataAlt(objetoTipoGrade.getProperty("dataAlt").toString());
                            tipoGrade.setDescricaoTipoGrade(objetoTipoGrade.getProperty("descricaoTipoGrade").toString());
                            // Adiciona a grade no produto
                            produto.setTipoGrade(tipoGrade);
                        }


                        // Pega o produto por loja
                        ProdutoLojaBeans produtoLoja = new ProdutoLojaBeans();
                        // Adiciona o produto
                        produtoLoja.setProduto(produto);
                        //SoapObject objetoProdutoLoja = (SoapObject) objeto.getProperty("produtoLoja");
                        produtoLoja.setIdProdutoLoja(Integer.parseInt(objetoProdutoLoja.getProperty("idProdutoLoja").toString()));
                        produtoLoja.setIdEmpresa(Integer.parseInt(objetoProdutoLoja.getProperty("idEmpresa").toString()));
                        produtoLoja.setDataAlt(objetoProdutoLoja.getProperty("dataAlt").toString());
                        produtoLoja.setEstoqueContabil(Double.parseDouble(objetoProdutoLoja.getProperty("estoqueContabil").toString()));
                        produtoLoja.setEstoqueFisico(Double.parseDouble(objetoProdutoLoja.getProperty("estoqueFisico").toString()));
                        produtoLoja.setRetido(Double.parseDouble(objetoProdutoLoja.getProperty("retido").toString()));
                        produtoLoja.setVendaAtacado(Double.parseDouble(objetoProdutoLoja.getProperty("vendaAtacado").toString()));
                        produtoLoja.setVendaVarejo(Double.parseDouble(objetoProdutoLoja.getProperty("vendaVarejo").toString()));
                        produtoLoja.setAtivo(objetoProdutoLoja.getProperty("ativo").toString());

                        // Pega o estoque
                        EstoqueBeans estoque = new EstoqueBeans();
                        // Adiciona o produto por loja no estoque
                        estoque.setProdutoLoja(produtoLoja);

                        //SoapObject objetoEstoque = (SoapObject) objeto.getProperty("estoque");
                        estoque.setIdEstoque(Integer.parseInt(objetoEstoque.getProperty("idEstoque").toString()));
                        estoque.setDataAlt(objetoEstoque.getProperty("dataAlt").toString());
                        estoque.setAtivo(objetoEstoque.getProperty("ativo").toString());
                        estoque.setEstoque(Double.parseDouble(objetoEstoque.getProperty("estoque").toString()));
                        estoque.setRetido(Double.parseDouble(objetoEstoque.getProperty("retido").toString()));
                        estoque.setLocacaoAtiva((objetoEstoque.hasProperty("locacaoAtiva")) ? objetoEstoque.getProperty("locacaoAtiva").toString() : "");
                        estoque.setLocacaoReserva((objetoEstoque.hasProperty("locacaoAtiva")) ? objetoEstoque.getProperty("locacaoReserva").toString() : "");

                        if (resumido == NAO) {
                            LocesBeans loces = new LocesBeans();
                            SoapObject objetoLoces = (SoapObject) objetoEstoque.getProperty("loces");
                            loces.setIdLoces(Integer.parseInt(objetoLoces.getProperty("idLoces").toString()));
                            loces.setIdEmpresa(Integer.parseInt(objetoLoces.getProperty("idEmpresa").toString()));
                            loces.setDataAlt(objetoLoces.getProperty("dataAlt").toString());
                            loces.setCodigo(Integer.parseInt(objetoLoces.getProperty("codigo").toString()));
                            loces.setAtivo(objetoLoces.getProperty("ativo").toString());
                            loces.setDescricaoLoces(objetoLoces.getProperty("descricaoLoces").toString());
                            // Adiciona loces no estoque
                            estoque.setLoces(loces);
                        }
                        // Adiciona o estoque no item da nota
                        itemNotaFiscal.setEstoque(estoque);

                        listaItem.add(itemNotaFiscal);
                    }
                    //int i = listaNotaFiscal.size();
                    //String s = listaNotaFiscal.toString();
                }

            } else {

                ItemNotaFiscalEntradaSql itemNotaFiscalEntradaSql = new ItemNotaFiscalEntradaSql(context);

                final Cursor cursor = itemNotaFiscalEntradaSql.sqlSelect(sql);

                // Checa se retornou alguma coisa do banco
                if ((cursor != null) && (cursor.getCount() > 0)) {
                    // Checa se tem alguma barra de progresso
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
                    // Passa por todos os registros
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

                        ItemNotaFiscalEntradaBeans itemNotaFiscal = new ItemNotaFiscalEntradaBeans();
                        itemNotaFiscal.setIdItemNotaFiscalEntrada(cursor.getInt(cursor.getColumnIndex("ID_AEAITENT")));
                        itemNotaFiscal.setIdEntrada(cursor.getInt(cursor.getColumnIndex("ID_AEAENTRA")));
                        itemNotaFiscal.setIdItemNotaFiscalSaida(cursor.getInt(cursor.getColumnIndex("ID_AEAITSAI")));
                        itemNotaFiscal.setIdItemPedidoCompras(cursor.getInt(cursor.getColumnIndex("ID_AEAITPED")));
                        itemNotaFiscal.setGuidItemEntrada(cursor.getString(cursor.getColumnIndex("GUID_ITENT")));
                        itemNotaFiscal.setDataCadastro(cursor.getString(cursor.getColumnIndex("DT_CAD_ITENT")));
                        itemNotaFiscal.setDataAlteracao(cursor.getString(cursor.getColumnIndex("DT_ALT_ITENT")));
                        itemNotaFiscal.setDataEntrada(cursor.getString(cursor.getColumnIndex("DT_ENTRADA")));
                        itemNotaFiscal.setSequencia(cursor.getInt(cursor.getColumnIndex("SEQUENCIA")));
                        itemNotaFiscal.setQuantidadeDataValidade(cursor.getDouble(cursor.getColumnIndex("QTDE_DAT_VAL")));
                        itemNotaFiscal.setQuantidadeConferido(cursor.getDouble(cursor.getColumnIndex("QTDE_CONFERIDO")));
                        itemNotaFiscal.setValorMercadoria(cursor.getDouble(cursor.getColumnIndex("VL_MERCADORIA")));
                        itemNotaFiscal.setTipo(cursor.getString(cursor.getColumnIndex("TIPO_ITENT")));
                        itemNotaFiscal.setTipoProduto(cursor.getString(cursor.getColumnIndex("TIPO_PRODUTO")));
                        itemNotaFiscal.setTipoBaixa(cursor.getString(cursor.getColumnIndex("TIPO_BAIXA")));
                        itemNotaFiscal.setObservacao(cursor.getString(cursor.getColumnIndex("OBS")));
                        itemNotaFiscal.setUnitarioItemMercadoria(cursor.getDouble(cursor.getColumnIndex("FC_MERCADORIA_UN")));
                        itemNotaFiscal.setTotalItem(cursor.getDouble(cursor.getColumnIndex("FC_CT_COMP")));
                        itemNotaFiscal.setQuantidade(cursor.getDouble(cursor.getColumnIndex("QUANTIDADE")));

                        // Adiciona o codigo de origem da nota
                        CodigoOrigemProdutoBeans codigoOrigemItemNF = new CodigoOrigemProdutoBeans();
                        codigoOrigemItemNF.setIdCodigoOrigem(cursor.getInt(cursor.getColumnIndex("ID_AEACODOM")));
                        codigoOrigemItemNF.setDataAlt(cursor.getString(cursor.getColumnIndex("DT_ALT_CODOM")));
                        codigoOrigemItemNF.setCodigo(cursor.getInt(cursor.getColumnIndex("CODIGO_CODOM")));
                        codigoOrigemItemNF.setDescricaoCodigoOrigem(cursor.getString(cursor.getColumnIndex("DESCRICAO_CODOM")));
                        // Adiciona a oregem ao produto
                        itemNotaFiscal.setCodigoOrigem(codigoOrigemItemNF);

                        // Pega a unidade de venda da item da nota fiscal
                        UnidadeVendaBeans unidadeItem = new UnidadeVendaBeans();
                        unidadeItem.setIdUnidadeVenda(cursor.getInt(cursor.getColumnIndex("ID_AEAUNVEN")));
                        unidadeItem.setDataAlt(cursor.getString(cursor.getColumnIndex("DT_ALT_UNVEN")));
                        unidadeItem.setSigla(cursor.getString(cursor.getColumnIndex("SIGLA_UNVEN")));
                        unidadeItem.setDescricaoUnidadeVenda(cursor.getString(cursor.getColumnIndex("DESCRICAO_SINGULAR")));
                        unidadeItem.setDecimais(cursor.getInt(cursor.getColumnIndex("DECIMAIS")));
                        itemNotaFiscal.setUnidadeVenda(unidadeItem);

                        // Pega os dados do produto
                        ProdutoBeans produto = new ProdutoBeans();
                        produto.setIdProduto(cursor.getInt(cursor.getColumnIndex("ID_AEAPRODU")));
                        produto.setGuid(cursor.getString(cursor.getColumnIndex("GUID_PRODU")));
                        produto.setDataAlt(cursor.getString(cursor.getColumnIndex("DT_ALT_PRODU")));
                        produto.setDescricaoProduto(cursor.getString(cursor.getColumnIndex("DESCRICAO_PRODU")));
                        produto.setDescricaoAuxiliar(cursor.getString(cursor.getColumnIndex("DESCRICAO_AUXILIAR")));
                        produto.setMascara(cursor.getString(cursor.getColumnIndex("DESCRICAO_MASCARA")));
                        produto.setCodigo(cursor.getInt(cursor.getColumnIndex("CODIGO_PRODU")));
                        produto.setCodigoEstrutural(cursor.getString(cursor.getColumnIndex("CODIGO_ESTRUTURAL")));
                        produto.setReferencia(cursor.getString(cursor.getColumnIndex("REFERENCIA")));
                        produto.setCodigoBarras(cursor.getString(cursor.getColumnIndex("CODIGO_BARRAS")));
                        produto.setPesoLiquido(cursor.getDouble(cursor.getColumnIndex("PESO_LIQUIDO")));
                        produto.setPesoBruto(cursor.getDouble(cursor.getColumnIndex("PESO_BRUTO")));
                        produto.setAtivo(cursor.getString(cursor.getColumnIndex("ATIVO_PRODU")));
                        produto.setTipoProduto(cursor.getString(cursor.getColumnIndex("TIPO_PRODU")));
                        produto.setGenero(cursor.getString(cursor.getColumnIndex("GENERO")));
                        produto.setValidade(cursor.getString(cursor.getColumnIndex("VALIDADE")));
                        produto.setGarantia(cursor.getString(cursor.getColumnIndex("GARANTIA")));
                        produto.setControleSerial(cursor.getString(cursor.getColumnIndex("CONTROLE_SERIAL")));
                        produto.setTipoItem(cursor.getString(cursor.getColumnIndex("TIPO_ITEM")));
                        produto.setRomaneia(cursor.getString(cursor.getColumnIndex("ROMANEIA")));

                        // Pega os dados da familia do produto
                        if (resumido == NAO) {
                            FamiliaProdutoBeans familaProduto = new FamiliaProdutoBeans();
                            familaProduto.setIdFamilia(cursor.getInt(cursor.getColumnIndex("ID_AEAFAMIL")));
                            familaProduto.setDataAlt(cursor.getString(cursor.getColumnIndex("DT_ALT_FAMIL")));
                            familaProduto.setCodigo(cursor.getInt(cursor.getColumnIndex("CODIGO_FAMIL")));
                            familaProduto.setDescricaoFamilia(cursor.getString(cursor.getColumnIndex("DESCRICAO_FAMIL")));
                            // Adiciona a familia no produto
                            produto.setFamilia(familaProduto);

                            ClasseProdutosBeans classeProduto = new ClasseProdutosBeans();
                            classeProduto.setIdClasse(cursor.getInt(cursor.getColumnIndex("ID_AEACLASE")));
                            classeProduto.setDataAlt(cursor.getString(cursor.getColumnIndex("DT_ALT_CLASE")));
                            classeProduto.setCodigo(cursor.getInt(cursor.getColumnIndex("CODIGO_CLASE")));
                            classeProduto.setDescricaoClasse(cursor.getString(cursor.getColumnIndex("DESCRICAO_CLASE")));
                            // Adiciona a classe no produto
                            produto.setClasse(classeProduto);

                            GrupoProdutoBeans grupoProduto = new GrupoProdutoBeans();
                            grupoProduto.setIdGrupo(cursor.getInt(cursor.getColumnIndex("ID_AEACLASE")));
                            grupoProduto.setIdClasse(cursor.getInt(cursor.getColumnIndex("ID_AEACLASE_GRUPO")));
                            grupoProduto.setDataAlt(cursor.getString(cursor.getColumnIndex("DT_ALT_GRUPO")));
                            grupoProduto.setCodigo(cursor.getInt(cursor.getColumnIndex("CODIGO_GRUPO")));
                            grupoProduto.setDescricaoGrupo(cursor.getString(cursor.getColumnIndex("DESCRICAO_GRUPO")));
                            // Adiciona o grupo no produto
                            produto.setGrupo(grupoProduto);

                            SubGrupoProdutoBeans subGrupoProduto = new SubGrupoProdutoBeans();
                            subGrupoProduto.setIdSubgrupo(cursor.getInt(cursor.getColumnIndex("ID_AEASGRUP")));
                            subGrupoProduto.setIdGrupo(cursor.getInt(cursor.getColumnIndex("ID_AEAGRUPO_SGRUP")));
                            subGrupoProduto.setDataAlt(cursor.getString(cursor.getColumnIndex("DT_ALT_SGRUP")));
                            subGrupoProduto.setCodigo(cursor.getInt(cursor.getColumnIndex("CODIGO_SGRUP")));
                            subGrupoProduto.setDescricaoSubgrupo(cursor.getString(cursor.getColumnIndex("DESCRICAO_SGRUP")));
                            // Adiciona o subgrupo ao produto
                            produto.setSubGrupo(subGrupoProduto);

                            CodigoOrigemProdutoBeans codigoOrigemProduto = new CodigoOrigemProdutoBeans();
                            codigoOrigemProduto.setIdCodigoOrigem(cursor.getInt(cursor.getColumnIndex("ID_AEACODOM_PRODU")));
                            codigoOrigemProduto.setDataAlt(cursor.getString(cursor.getColumnIndex("DT_ALT_CODOM_PRODU")));
                            codigoOrigemProduto.setCodigo(cursor.getInt(cursor.getColumnIndex("CODIGO_CODOM_PRODU")));
                            codigoOrigemProduto.setDescricaoCodigoOrigem(cursor.getString(cursor.getColumnIndex("DESCRICAO_CODOM_PRODU")));
                            // Adiciona a origem do produto
                            produto.setCodigoOrigem(codigoOrigemProduto);
                        }

                        MarcaBeans marca = new MarcaBeans();
                        marca.setIdMarca(cursor.getInt(cursor.getColumnIndex("ID_AEAMARCA")));
                        marca.setDataAlt(cursor.getString(cursor.getColumnIndex("DT_ALT_MARCA")));
                        marca.setDescricao(cursor.getString(cursor.getColumnIndex("DESCRICAO_MARCA")));
                        // Adiciona a marca no produto
                        produto.setMarca(marca);

                        UnidadeVendaBeans unidadeProduto = new UnidadeVendaBeans();
                        unidadeProduto.setIdUnidadeVenda(cursor.getInt(cursor.getColumnIndex("ID_AEAUNVEN_PRODU")));
                        unidadeProduto.setDataAlt(cursor.getString(cursor.getColumnIndex("DT_ALT_UNVEN_PRODU")));
                        unidadeProduto.setSigla(cursor.getString(cursor.getColumnIndex("SIGLA_UNVEN_PRODU")));
                        unidadeProduto.setDescricaoUnidadeVenda(cursor.getString(cursor.getColumnIndex("DESCRICAO_UNVEN_PRODU")));
                        unidadeProduto.setDecimais(cursor.getInt(cursor.getColumnIndex("DECIMAIS_UNVEN_PRODU")));
                        // Adiciona a unidade no produto
                        produto.setUnidadeVenda(unidadeProduto);

                        TipoGradeBeans tipoGrade = new TipoGradeBeans();
                        tipoGrade.setIdTipoGrade(cursor.getInt(cursor.getColumnIndex("ID_AEATPGRD")));
                        tipoGrade.setDataAlt(cursor.getString(cursor.getColumnIndex("DT_ALT_TPGRD")));
                        tipoGrade.setDescricaoTipoGrade(cursor.getString(cursor.getColumnIndex("DESCRICAO_TPGRD")));
                        // Adiciona a grade no produto
                        produto.setTipoGrade(tipoGrade);


                        // Pega o produto por loja
                        ProdutoLojaBeans produtoLoja = new ProdutoLojaBeans();
                        // Adiciona o produto
                        produtoLoja.setProduto(produto);
                        produtoLoja.setIdProdutoLoja(cursor.getInt(cursor.getColumnIndex("ID_AEAPLOJA")));
                        produtoLoja.setIdEmpresa(cursor.getInt(cursor.getColumnIndex("ID_SMAEMPRE_PLOJA")));
                        produtoLoja.setDataAlt(cursor.getString(cursor.getColumnIndex("DT_ALT_PLOJA")));
                        produtoLoja.setEstoqueContabil(cursor.getDouble(cursor.getColumnIndex("ESTOQUE_C")));
                        produtoLoja.setEstoqueFisico(cursor.getDouble(cursor.getColumnIndex("ESTOQUE_F")));
                        produtoLoja.setRetido(cursor.getDouble(cursor.getColumnIndex("RETIDO_PLOJA")));
                        produtoLoja.setVendaAtacado(cursor.getDouble(cursor.getColumnIndex("VENDA_ATAC")));
                        produtoLoja.setVendaVarejo(cursor.getDouble(cursor.getColumnIndex("VENDA_VARE")));
                        produtoLoja.setAtivo(cursor.getString(cursor.getColumnIndex("ATIVO_PLOJA")));

                        // Pega o estoque
                        EstoqueBeans estoque = new EstoqueBeans();
                        // Adiciona o produto por loja no estoque
                        estoque.setProdutoLoja(produtoLoja);

                        estoque.setIdEstoque(cursor.getInt(cursor.getColumnIndex("ID_AEAESTOQ")));
                        estoque.setDataAlt(cursor.getString(cursor.getColumnIndex("DT_ALT_ESTOQ")));
                        estoque.setAtivo(cursor.getString(cursor.getColumnIndex("ATIVO_ESTOQ")));
                        estoque.setEstoque(cursor.getDouble(cursor.getColumnIndex("ESTOQUE")));
                        estoque.setRetido(cursor.getDouble(cursor.getColumnIndex("RETIDO_ESTOQ")));
                        estoque.setLocacaoAtiva(cursor.getString(cursor.getColumnIndex("LOCACAO_ATIVA")));
                        estoque.setLocacaoReserva(cursor.getString(cursor.getColumnIndex("LOCACAO_RESERVA")));

                        if (resumido == NAO) {
                            LocesBeans loces = new LocesBeans();
                            loces.setIdLoces(cursor.getInt(cursor.getColumnIndex("ID_AEALOCES")));
                            loces.setIdEmpresa(cursor.getInt(cursor.getColumnIndex("ID_SMAEMPRE_LOCES")));
                            loces.setDataAlt(cursor.getString(cursor.getColumnIndex("DT_ALT_LOCES")));
                            loces.setCodigo(cursor.getInt(cursor.getColumnIndex("CODIGO_LOCES")));
                            loces.setAtivo(cursor.getString(cursor.getColumnIndex("ATIVO_LOCES")));
                            loces.setDescricaoLoces(cursor.getString(cursor.getColumnIndex("DESCRICAO_LOCES")));
                            // Adiciona loces no estoque
                            estoque.setLoces(loces);
                        }
                        // Adiciona o estoque no item da nota
                        itemNotaFiscal.setEstoque(estoque);

                        listaItem.add(itemNotaFiscal);
                    }

                }
            }
        } catch (Exception e){

            final FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

            // Armazena as informacoes para para serem exibidas e enviadas
            final ContentValues contentValues = new ContentValues();
            contentValues.put("comando", 0);
            contentValues.put("tela", "NotaFiscalEntradaRotinas");
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
        return listaItem;
    }


    public ProdutoBeans selectProdutoResumidoIdItemEntrada(int idItemEntrada){
        ProdutoBeans produtoRetorno = null;
        try{
            String sql =
                    "SELECT AEAPRODU.ID_AEAPRODU, AEAPRODU.CODIGO, AEAPRODU.CODIGO_ESTRUTURAL, AEAPRODU.CODIGO_BARRAS, "+
                            "AEAPRODU.GUID, AEAPRODU.DESCRICAO, AEAPRODU.DESCRICAO_AUXILIAR, AEAPRODU.REFERENCIA, AEAMARCA.ID_AEAMARCA, "+
                            "AEAMARCA.ID_AEAMARCA, AEAMARCA.DESCRICAO AS DESCRICAO_MARCA, "+
                            "AEAUNVEN.ID_AEAUNVEN, AEAUNVEN.SIGLA, AEAUNVEN.DESCRICAO_SINGULAR, AEAUNVEN.DECIMAIS "+
                            "FROM AEAPRODU "+
                            "LEFT OUTER JOIN AEAMARCA "+
                            "ON(AEAPRODU.ID_AEAMARCA = AEAMARCA.ID_AEAMARCA) "+
                            "LEFT OUTER JOIN AEAUNVEN "+
                            "ON(AEAPRODU.ID_AEAUNVEN = AEAUNVEN.ID_AEAUNVEN) " +

                            "WHERE AEAPRODU.ID_AEAPRODU = (SELECT AEAPRODU.ID_AEAPRODU \n" +
                            "FROM AEAITENT \n" +
                            "LEFT OUTER JOIN AEAESTOQ \n" +
                            "ON(AEAITENT.ID_AEAESTOQ = AEAESTOQ.ID_AEAESTOQ) \n" +
                            "LEFT OUTER JOIN AEAPLOJA \n" +
                            "ON(AEAESTOQ.ID_AEAPLOJA = AEAPLOJA.ID_AEAPLOJA) \n" +
                            "LEFT OUTER JOIN AEAPRODU \n" +
                            "ON(AEAPLOJA.ID_AEAPRODU = AEAPRODU.ID_AEAPRODU) \n" +
                            "WHERE AEAITENT.ID_AEAITENT = " + idItemEntrada + ") ";


            if (tipoConexao.equalsIgnoreCase("W")){
                // Instancia a classe para manipular o webservice
                WSSisInfoWebservice webserviceSisInfo = new WSSisInfoWebservice(context);
                SoapObject objetoProduto = webserviceSisInfo.executarWebservice(sql, WSSisInfoWebservice.FUNCTION_DADOS_PRODUTOS_RESUMIDOS_ITEM_ENTRADA, null);

                if (objetoProduto != null){
                    produtoRetorno = new ProdutoBeans();
                    produtoRetorno.setIdProduto(Integer.parseInt(objetoProduto.getProperty("idProduto").toString()) + 0);
                    produtoRetorno.setCodigo(Integer.parseInt(objetoProduto.getProperty("codigo").toString()));
                    produtoRetorno.setCodigoEstrutural(objetoProduto.getProperty("codigoEstrutural").toString());
                    produtoRetorno.setCodigoBarras((objetoProduto.hasProperty("codigoBarras")) ? objetoProduto.getProperty("codigoBarras").toString() : "");
                    produtoRetorno.setGuid(objetoProduto.getProperty("guid").toString());
                    produtoRetorno.setDescricaoProduto(objetoProduto.getProperty("descricaoProduto").toString());
                    produtoRetorno.setDescricaoAuxiliar((objetoProduto.hasProperty("descricaoAuxiliar")) ? objetoProduto.getProperty("descricaoAuxiliar").toString() : "");
                    produtoRetorno.setReferencia((objetoProduto.hasProperty("referencia")) ? objetoProduto.getProperty("referencia").toString() : "");

                    MarcaBeans marca= new MarcaBeans();
                    SoapObject objetoMarca = (SoapObject) objetoProduto.getProperty("marca");
                    marca.setIdMarca(Integer.parseInt(objetoMarca.getProperty("idMarca").toString()));
                    marca.setDescricao(objetoMarca.getProperty("descricao").toString());
                    produtoRetorno.setMarca(marca);

                    UnidadeVendaBeans unidadeVenda = new UnidadeVendaBeans();
                    SoapObject objetoUnidadeVenda = (SoapObject) objetoProduto.getProperty("unidadeVenda");
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
        return produtoRetorno;
    }
}
