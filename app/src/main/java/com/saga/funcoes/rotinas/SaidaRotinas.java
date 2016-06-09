package com.saga.funcoes.rotinas;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.widget.ProgressBar;

import com.saga.beans.CidadeBeans;
import com.saga.beans.EmbalagemBeans;
import com.saga.beans.EstadoBeans;
import com.saga.beans.EstoqueBeans;
import com.saga.beans.ItemSaidaBeans;
import com.saga.beans.MarcaBeans;
import com.saga.beans.ProdutoBeans;
import com.saga.beans.ProdutoLojaBeans;
import com.saga.beans.SaidaBeans;
import com.saga.beans.SerieBeans;
import com.saga.beans.TipoGradeBeans;
import com.saga.beans.UnidadeVendaBeans;
import com.saga.funcoes.FuncoesPersonalizadas;
import com.saga.webservice.WSSisInfoWebservice;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Bruno Nogueira Silva on 06/06/2016.
 */
public class SaidaRotinas extends Rotinas {

    public SaidaRotinas(Context context) {
        super(context);
    }

    public List<SaidaBeans> listaSaida(String where, int conferidoSemConferir, final ProgressBar progresso){

        List<SaidaBeans> listaSaidaArray = new ArrayList<SaidaBeans>();

        try {
            FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);
            int limiteDiasConferir = (!funcoes.getValorXml("LimiteDiasConferir").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? Integer.parseInt(funcoes.getValorXml("LimiteDiasConferir")) : 7;
            int codigoEmpresa = (!funcoes.getValorXml("CodigoEmpresa").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? Integer.parseInt(funcoes.getValorXml("CodigoEmpresa")) : 1;

            String sql = "SELECT AEASAIDA.ID_AEASAIDA, AEASAIDA.ID_SMAEMPRE, AEASAIDA.ID_CFACLIFO, \n" +
                    "AEASAIDA.ID_CFAESTAD, AEASAIDA.ID_CFACIDAD, AEASAIDA.GUID AS GUID_SAIDA, AEASAIDA.DT_CAD AS DT_CAD_SAIDA, \n" +
                    "AEASAIDA.DT_ALT AS DT_ALT_SAIDA, AEASAIDA.NUMERO AS NUMERO_SAIDA, AEASAIDA.DT_VENDA AS DT_VENDA_SAIDA, AEASAIDA.DT_EMISSAO AS DT_EMISSAO_SAIDA, \n" +
                    "AEASAIDA.DT_SAIDA AS DT_SAIDA_SAIDA, AEASAIDA.DT_CANCEL AS DT_CANCEL_SAIDA, AEASAIDA.ATAC_VAREJO AS ATAC_VAREJO_SAIDA, AEASAIDA.FC_VL_TOTAL AS FC_VL_TOTAL_SAIDA, \n" +
                    "AEASAIDA.SITUACAO AS SITUACAO_SAIDA, AEASAIDA.OBS AS OBS_SAIDA, AEASAIDA.PESSOA_CLIENTE, AEASAIDA.NOME_CLIENTE, \n" +
                    "AEASAIDA.CPF_CGC_CLIENTE, AEASAIDA.IE_RG_CLIENTE, AEASAIDA.ENDERECO_CLIENTE, \n" +
                    "AEASAIDA.BAIRRO_CLIENTE, AEASAIDA.CEP_CLIENTE, AEASAIDA.TIPO_SAIDA AS TIPO_SAIDA_SAIDA, AEASAIDA.TIPO_ENTREGA AS TIPO_ENTREGA_SAIDA,  \n" +
                    "CFAESTAD.ID_CFAESTAD, CFAESTAD.UF, CFAESTAD.DESCRICAO AS DESCRICAO_ESTAD, \n" +
                    "CFACIDAD.ID_CFACIDAD, CFACIDAD.DESCRICAO AS DESCRICAO_CIDAD, \n" +
                    "AEASERIE.ID_AEASERIE, AEASERIE.ID_SMAEMPRE AS ID_SMAEMPRE_SERIE, AEASERIE.GUID AS GUID_SERIE, AEASERIE.DT_ALT AS DT_ALT_SERIE, \n" +
                    "AEASERIE.TIPO AS TIPO_SERIE, AEASERIE.SERIE AS SERIE_SERIE, AEASERIE.SUBSERIE, AEASERIE.CODIGO AS CODIGO_SERIE, AEASERIE.NUMERO AS NUMERO_SERIE \n" +
                    "FROM AEASAIDA \n" +
                    "LEFT OUTER JOIN CFAESTAD CFAESTAD\n" +
                    "ON(AEASAIDA.ID_CFAESTAD = CFAESTAD.ID_CFAESTAD) \n" +
                    "LEFT OUTER JOIN CFACIDAD CFACIDAD \n" +
                    "ON(AEASAIDA.ID_CFACIDAD = CFACIDAD.ID_CFACIDAD) \n" +
                    "LEFT OUTER JOIN AEASERIE AEASERIE \n" +
                    "ON(AEASAIDA.ID_AEASERIE = AEASERIE.ID_AEASERIE) " +
                    "    WHERE " +
                    "      (AEASAIDA.ID_SMAEMPRE = " + codigoEmpresa +") AND " +
                    "      (AEASAIDA.DT_VENDA >= (SELECT DATEADD(DAY, -" +limiteDiasConferir + ", CURRENT_DATE) FROM RDB$DATABASE)) \n";

            if (conferidoSemConferir == SEM_CONFERIR) {
                sql += " AND ( (AEASAIDA.SITUACAO = '0') OR (AEASAIDA.SITUACAO = '1') OR (AEASAIDA.SITUACAO = '3') ) ";

            } else if (conferidoSemConferir == CONFERIDO){
                sql += " AND (AEASAIDA.SITUACAO <> '0') AND (AEASAIDA.SITUACAO <> '1') AND (AEASAIDA.SITUACAO <> '3') ";
            }
            // Checa se foi passado alguma clausua where por parametro
            if (where != null) {
                sql += " AND (" + where + ")";
            }

            sql += " ORDER BY AEASAIDA.DT_VENDA DESC, AEASAIDA.NUMERO DESC ";

            if (tipoConexao.equalsIgnoreCase("W")){
                WSSisInfoWebservice webserviceSisInfo = new WSSisInfoWebservice(context);
                final Vector<SoapObject> listaSaidaSoap = webserviceSisInfo.executarSelectWebservice(sql, WSSisInfoWebservice.FUNCTION_SELECT_LISTA_SAIDA, null);

                // Checa se retornou alguma coisa
                if (listaSaidaSoap != null && listaSaidaSoap.size() > 0){
                    // Checa se tem alguma barra de progresso
                    if (progresso != null) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            public void run() {
                                progresso.setIndeterminate(false);
                                progresso.setProgress(0);
                                progresso.setMax(listaSaidaSoap.size());
                            }
                        });
                    }
                    int incremento = 0;
                    // Passa por toda a lista
                    for (SoapObject objetoIndividual : listaSaidaSoap) {
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
                        SaidaBeans saidaBeans = new SaidaBeans();
                        saidaBeans.setIdSaida(Integer.parseInt(objeto.getProperty("idSaida").toString()));
                        saidaBeans.setIdEmpresa(Integer.parseInt(objeto.getProperty("idEmpresa").toString()));
                        saidaBeans.setIdClifo(Integer.parseInt(objeto.getProperty("idClifo").toString()));
                        saidaBeans.setGuidSaida(objeto.getProperty("guidSaida").toString());
                        saidaBeans.setDataCad(objeto.getProperty("dataCad").toString());
                        saidaBeans.setDataAlt(objeto.getProperty("dataAlt").toString());
                        saidaBeans.setNumeroSaida(Integer.parseInt(objeto.getProperty("numeroSaida").toString()));
                        saidaBeans.setDataVenda( (objeto.hasProperty("dataVenda")) ? objeto.getProperty("dataVenda").toString() : "");
                        saidaBeans.setDataEmissao( (objeto.hasProperty("dataEmissao")) ? objeto.getProperty("dataEmissao").toString() : "");
                        saidaBeans.setDataSaida( (objeto.hasProperty("dataSaida")) ? objeto.getProperty("dataSaida").toString() : "");
                        saidaBeans.setDataCancelado( (objeto.hasProperty("dataCancelado")) ? objeto.getProperty("dataCancelado").toString() : "");
                        saidaBeans.setAtacadoVarejo(objeto.getProperty("atacadoVarejo").toString());
                        saidaBeans.setValorTotalSaida(Double.parseDouble(objeto.getProperty("valorTotalSaida").toString()));
                        saidaBeans.setSituacao(objeto.getProperty("situacao").toString());
                        saidaBeans.setObservacao( (objeto.hasProperty("observacao")) ? objeto.getProperty("observacao").toString() : "");
                        saidaBeans.setPessoaCliente( (objeto.hasProperty("pessoaCliente")) ? objeto.getProperty("pessoaCliente").toString() : "");
                        saidaBeans.setNomeCliente( (objeto.hasProperty("nomeCliente")) ? objeto.getProperty("nomeCliente").toString() : "");
                        saidaBeans.setCpfCgcCliente( (objeto.hasProperty("cpfCgcCliente")) ? objeto.getProperty("cpfCgcCliente").toString() : "");
                        saidaBeans.setIeRgCliente( (objeto.hasProperty("ieRgCliente")) ? objeto.getProperty("ieRgCliente").toString() : "");
                        saidaBeans.setEnderecoCliente( (objeto.hasProperty("enderecoCliente")) ? objeto.getProperty("enderecoCliente").toString() : "");
                        saidaBeans.setBairroCliente( (objeto.hasProperty("bairroCliente")) ? objeto.getProperty("bairroCliente").toString() : "");
                        saidaBeans.setCepCliente( (objeto.hasProperty("cepCliente")) ? objeto.getProperty("cepCliente").toString() : "");
                        saidaBeans.setTipoEntrega( (objeto.hasProperty("tipoEntrega")) ? objeto.getProperty("tipoEntrega").toString() : "");

                        SoapObject objetoEstado = (SoapObject) objeto.getProperty("estadoSaida");

                        EstadoBeans estadoBeans = new EstadoBeans();
                        estadoBeans.setIdEstado(Integer.parseInt(objetoEstado.getProperty("idEstado").toString()));
                        estadoBeans.setUf(objetoEstado.getProperty("uf").toString());
                        estadoBeans.setDescricaoEstado(objetoEstado.getProperty("descricaoEstado").toString());
                        // Adiciona o estado na saida
                        saidaBeans.setEstadoSaida(estadoBeans);

                        SoapObject objetoCidade = (SoapObject) objeto.getProperty("cidadeSaida");

                        CidadeBeans cidadeBeans = new CidadeBeans();
                        cidadeBeans.setIdCidade(Integer.parseInt(objetoCidade.getProperty("idCidade").toString()));
                        cidadeBeans.setDescricaoCidade(objetoCidade.getProperty("descricaoCidade").toString());
                        // Adiciona a cidade na saida
                        saidaBeans.setCidadeSaida(cidadeBeans);

                        SoapObject objetoSerie = (SoapObject) objeto.getProperty("serieSaida");

                        SerieBeans serieBeans = new SerieBeans();
                        serieBeans.setIdSerie(Integer.parseInt(objetoSerie.getProperty("idSerie").toString()));
                        serieBeans.setIdEmpresa(Integer.parseInt(objetoSerie.getProperty("idEmpresa").toString()));
                        serieBeans.setGuidSerie(objetoSerie.getProperty("guidSerie").toString());
                        serieBeans.setDataAlt(objetoSerie.getProperty("dataAlt").toString());
                        serieBeans.setTipoSerie(objetoSerie.getProperty("tipoSerie").toString());
                        serieBeans.setSerie(objetoSerie.getProperty("serie").toString());
                        serieBeans.setSubserie( (objetoSerie.hasProperty("subSerie")) ? objetoSerie.getProperty("subSerie").toString() : "");
                        serieBeans.setCodigo( (objetoSerie.hasProperty("codigo")) ? objetoSerie.getProperty("codigo").toString() : "");
                        serieBeans.setNumero(Integer.parseInt(objetoSerie.getProperty("numero").toString()));
                        // Adiciona a serie na saida
                        saidaBeans.setSerieSaida(serieBeans);

                        // Adiciona o item de romaneio em uma lista
                        listaSaidaArray.add(saidaBeans);
                    }
                }

            } else {

            }
        } catch (Exception e){

            final FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

            // Armazena as informacoes para para serem exibidas e enviadas
            final ContentValues contentValues = new ContentValues();
            contentValues.put("comando", 0);
            contentValues.put("tela", "RomaneiraRotinas");
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

        return listaSaidaArray;
    } // Fim listaRomaneio


    public List<ItemSaidaBeans> listaItemSaida(int idSaida, int pesquisaProduto, String where, int conferidoSemConferir, final ProgressBar progresso){

        List<ItemSaidaBeans> listaItemSaidaArray = new ArrayList<ItemSaidaBeans>();

        try {
            FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

            String sql = "SELECT AEAITSAI.ID_AEAITSAI, AEAITSAI.ID_AEASAIDA, AEAITSAI.ID_AEAUNVEN, AEAITSAI.ID_AEAESTOQ, AEAITSAI.ID_CFACLIFO_VENDEDOR, \n" +
                    "AEAITSAI.GUID, AEAITSAI.DT_ALT, AEAITSAI.DT_VENDA, AEAITSAI.SEQUENCIA, AEAITSAI.QUANTIDADE, AEAITSAI.QTDE_DEVOL, \n" +
                    "AEAITSAI.QTDE_RETORNO, AEAITSAI.QTDE_CONFERIDO, AEAITSAI.FC_VL_LIQUIDO, AEAITSAI.TIPO_PRODUTO, AEAITSAI.ATAC_VAREJO, \n" +
                    "AEAITSAI.TIPO_BAIXA, AEAITSAI.TIPO_SAIDA, AEAITSAI.COMPLEMENTO, AEAITSAI.BAIXA_POR_CONF, \n" +
                    "AEAUNVEN.ID_AEAUNVEN, AEAUNVEN.DT_ALT AS DT_ALT_UNVEN, AEAUNVEN.SIGLA AS SIGLA_UNVEN, AEAUNVEN.DESCRICAO_SINGULAR, AEAUNVEN.DECIMAIS, \n" +
                    "AEAESTOQ.ID_AEAESTOQ, AEAESTOQ.DT_ALT AS DT_ALT_ESTOQ, AEAESTOQ.ATIVO AS ATIVO_ESTOQ, AEAESTOQ.ESTOQUE, AEAESTOQ.RETIDO AS RETIDO_ESTOQ, \n" +
                    "AEAESTOQ.LOCACAO_ATIVA, AEAESTOQ.LOCACAO_RESERVA, \n" +
                    "AEAPLOJA.ID_AEAPLOJA, AEAPLOJA.ID_SMAEMPRE AS ID_SMAEMPRE_PLOJA, AEAPLOJA.DT_ALT AS DT_ALT_PLOJA, AEAPLOJA.ESTOQUE_C, AEAPLOJA.ESTOQUE_F, \n" +
                    "AEAPLOJA.RETIDO AS RETIDO_PLOJA, AEAPLOJA.PEDIDO AS PEDIDO_PLOJA, AEAPLOJA.VENDA_ATAC, AEAPLOJA.VENDA_VARE, AEAPLOJA.ATIVO AS ATIVO_PLOJA, \n" +
                    "AEAPRODU.ID_AEAPRODU, AEAPRODU.GUID AS GUID_PRODU, AEAPRODU.DT_ALT AS DT_ALT_PRODU, AEAPRODU.DESCRICAO AS DESCRICAO_PRODU, \n" +
                    "AEAPRODU.DESCRICAO_AUXILIAR, AEAPRODU.DESCRICAO_MASCARA, AEAPRODU.CODIGO AS CODIGO_PRODU, AEAPRODU.CODIGO_ESTRUTURAL, AEAPRODU.REFERENCIA, \n" +
                    "AEAPRODU.CODIGO_BARRAS, AEAPRODU.PESO_LIQUIDO, AEAPRODU.PESO_BRUTO, AEAPRODU.ATIVO AS ATIVO_PRODU, AEAPRODU.TIPO AS TIPO_PRODU, \n" +
                    "AEAPRODU.GENERO, AEAPRODU.VALIDADE, AEAPRODU.GARANTIA, AEAPRODU.CONTROLE_SERIAL, AEAPRODU.TIPO_ITEM, AEAPRODU.ROMANEIA, \n" +
                    "AEAMARCA.ID_AEAMARCA, AEAMARCA.ID_CFACLIFO AS ID_CFACLIFO_MARCA, AEAMARCA.DT_ALT AS DT_ALT_MARCA, AEAMARCA.DESCRICAO AS DESCRICAO_MARCA, \n" +
                    "AEAUNVEN_PRODU.ID_AEAUNVEN AS ID_AEAUNVEN_PRODU, AEAUNVEN_PRODU.DT_ALT AS DT_ALT_UNVEN_PRODU, AEAUNVEN_PRODU.SIGLA AS SIGLA_UNVEN_PRODU, \n" +
                    "AEAUNVEN_PRODU.DESCRICAO_SINGULAR AS DESCRICAO_UNVEN_PRODU, AEAUNVEN_PRODU.DECIMAIS AS DECIMAIS_UNVEN_PRODU, \n" +

                    "AEAEMBAL_ITSAI.ID_AEAEMBAL AS ID_AEAEMBAL_AEAEMBAL_ITSAI, AEAEMBAL_ITSAI.ID_AEAPRODU AS ID_AEAPRODU_AEAEMBAL_ITSAI, AEAEMBAL_ITSAI.ID_AEAUNVEN AS ID_AEAUNVEN_AEAEMBAL_ITSAI, \n" +
                    "AEAEMBAL_ITSAI.PRINCIPAL AS PRINCIPAL_AEAEMBAL_ITSAI, AEAEMBAL_ITSAI.DESCRICAO AS DESCRICAO_AEAEMBAL_ITSAI, AEAEMBAL_ITSAI.FATOR_CONVERSAO AS FATOR_AEAEMBAL_ITSAI, \n" +
                    "AEAEMBAL_ITSAI.MODULO AS MODULO_AEAEMBAL_ITSAI, AEAEMBAL_ITSAI.DECIMAIS AS DECIMAIS_AEAEMBAL_ITSAI, AEAEMBAL_ITSAI.CODIGO_BARRAS AS CODIGO_BARRAS_AEAEMBAL_ITSAI, \n" +
                    "AEAEMBAL_ITSAI.REFERENCIA AS REFERENCIA_AEAEMBAL_ITSAI, \n" +

                    /*"AEAEMBAL_PRODU.ID_AEAEMBAL AS ID_AEAEMBAL_AEAEMBAL_PRODU, AEAEMBAL_PRODU.ID_AEAPRODU AS ID_AEAPRODU_AEAEMBAL_PRODU, AEAEMBAL_PRODU.ID_AEAUNVEN AS ID_AEAUNVEN_AEAEMBAL_PRODU, \n" +
                    "AEAEMBAL_PRODU.PRINCIPAL AS PRINCIPAL_AEAEMBAL_PRODU, AEAEMBAL_PRODU.DESCRICAO AS DESCRICAO_AEAEMBAL_PRODU, AEAEMBAL_PRODU.FATOR_CONVERSAO AS FATOR_AEAEMBAL_PRODU, \n" +
                    "AEAEMBAL_PRODU.MODULO AS MODULO_AEAEMBAL_PRODU, AEAEMBAL_PRODU.DECIMAIS AS DECIMAIS_AEAEMBAL_PRODU, AEAEMBAL_PRODU.CODIGO_BARRAS AS CODIGO_BARRAS_AEAEMBAL_PRODU, \n" +
                    "AEAEMBAL_PRODU.REFERENCIA AS REFERENCIA_AEAEMBAL_PRODU, \n" +*/

                    "(AEAEMBAL_PRODU.FATOR_CONVERSAO / AEAEMBAL_ITSAI.FATOR_CONVERSAO) AS FATOR_PRODUTO_PESQUISADO, \n" +
                    "AEATPGRD.ID_AEATPGRD, AEATPGRD.DT_ALT AS DT_ALT_TPGRD, AEATPGRD.DESCRICAO AS DESCRICAO_TPGRD \n" +
                    "FROM AEAITSAI \n" +
                    "LEFT OUTER JOIN AEAUNVEN AEAUNVEN \n" +
                    "ON(AEAITSAI.ID_AEAUNVEN = AEAUNVEN.ID_AEAUNVEN) \n" +
                    "LEFT OUTER JOIN AEAESTOQ AEAESTOQ \n" +
                    "ON(AEAITSAI.ID_AEAESTOQ = AEAESTOQ.ID_AEAESTOQ) \n" +
                    "LEFT OUTER JOIN AEAPLOJA AEAPLOJA \n" +
                    "ON(AEAESTOQ.ID_AEAPLOJA = AEAPLOJA.ID_AEAPLOJA) \n" +
                    "LEFT OUTER JOIN AEAPRODU AEAPRODU \n" +
                    "ON(AEAPLOJA.ID_AEAPRODU = AEAPRODU.ID_AEAPRODU) \n" +
                    "LEFT OUTER JOIN AEAMARCA AEAMARCA \n" +
                    "ON(AEAPRODU.ID_AEAMARCA = AEAMARCA.ID_AEAMARCA) \n" +
                    "LEFT OUTER JOIN AEAUNVEN AEAUNVEN_PRODU \n" +
                    "ON(AEAPRODU.ID_AEAUNVEN = AEAUNVEN_PRODU.ID_AEAUNVEN) \n" +
                    "LEFT OUTER JOIN AEAEMBAL AEAEMBAL_ITSAI \n" +
                    "ON((AEAEMBAL_ITSAI.ID_AEAUNVEN = AEAITSAI.ID_AEAUNVEN) AND (AEAEMBAL_ITSAI.ID_AEAPRODU = AEAPRODU.ID_AEAPRODU)) \n" +
                    "LEFT OUTER JOIN AEAEMBAL AEAEMBAL_PRODU \n" +
                    "ON((AEAEMBAL_PRODU.ID_AEAPRODU = AEAPRODU.ID_AEAPRODU) " + ((pesquisaProduto == NAO) ? "AND (AEAEMBAL_PRODU.ID_AEAUNVEN = AEAUNVEN_PRODU.ID_AEAUNVEN)) \n" : ")\n") +
                    "LEFT OUTER JOIN AEATPGRD AEATPGRD \n" +
                    "ON(AEAPRODU.ID_AEATPGRD = AEATPGRD.ID_AEATPGRD) \n" +
                    "    WHERE " +
                    "      (AEAITSAI.ID_AEASAIDA = " + idSaida +") ";

            if (conferidoSemConferir == SEM_CONFERIR) {
                sql += " AND ( AEAITSAI.QTDE_CONFERIDO < AEAITSAI.QUANTIDADE ) ";

            } else if (conferidoSemConferir == CONFERIDO){
                sql += " AND ( AEAITSAI.QTDE_CONFERIDO >= AEAITSAI.QUANTIDADE ) ";
            }
            // Checa se foi passado alguma clausua where por parametro
            if (where != null) {
                sql += " AND (" + where + ")";
            }

            sql += " ORDER BY AEAITSAI.SEQUENCIA ASC ";

            if (tipoConexao.equalsIgnoreCase("W")){
                WSSisInfoWebservice webserviceSisInfo = new WSSisInfoWebservice(context);
                final Vector<SoapObject> listaSaidaSoap = webserviceSisInfo.executarSelectWebservice(sql, WSSisInfoWebservice.FUNCTION_SELECT_LISTA_ITEM_SAIDA, null);

                // Checa se retornou alguma coisa
                if (listaSaidaSoap != null && listaSaidaSoap.size() > 0){
                    // Checa se tem alguma barra de progresso
                    if (progresso != null) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            public void run() {
                                progresso.setIndeterminate(false);
                                progresso.setProgress(0);
                                progresso.setMax(listaSaidaSoap.size());
                            }
                        });
                    }
                    int incremento = 0;
                    // Passa por toda a lista
                    for (SoapObject objetoIndividual : listaSaidaSoap) {
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

                        ItemSaidaBeans itemSaidaBeans = new ItemSaidaBeans();
                        itemSaidaBeans.setIdItemSaida(Integer.parseInt(objeto.getProperty("idItemSaida").toString()));
                        itemSaidaBeans.setIdSaida(Integer.parseInt(objeto.getProperty("idSaida").toString()));
                        itemSaidaBeans.setIdClifoVendedor(Integer.parseInt(objeto.getProperty("idClifoVendedor").toString()));
                        itemSaidaBeans.setGuid(objeto.getProperty("guid").toString());
                        itemSaidaBeans.setDataAlt(objeto.getProperty("dataAlt").toString());
                        itemSaidaBeans.setDataVenda(objeto.getProperty("dataVenda").toString());
                        itemSaidaBeans.setSequencia(Integer.parseInt(objeto.getProperty("sequencia").toString()));
                        itemSaidaBeans.setQuantidade(Double.parseDouble(objeto.getProperty("quantidade").toString()));
                        itemSaidaBeans.setQuantidadeDevolvida( (objeto.hasProperty("quantidadeDevolvida")) ? Double.parseDouble(objeto.getProperty("quantidadeDevolvida").toString()) : 0);
                        itemSaidaBeans.setQuantidadeRetorno( (objeto.hasProperty("quantidadeRetorno")) ? Double.parseDouble(objeto.getProperty("quantidadeRetorno").toString()) : 0);
                        itemSaidaBeans.setQuantidadeConferido( (objeto.hasProperty("quantidadeConferido")) ? Double.parseDouble(objeto.getProperty("quantidadeConferido").toString()) : 0);
                        itemSaidaBeans.setValorTotalLiquido( (objeto.hasProperty("valorTotalLiquido")) ? Double.parseDouble(objeto.getProperty("valorTotalLiquido").toString()) : 0);
                        itemSaidaBeans.setTipoProduto( (objeto.hasProperty("tipoProduto")) ? objeto.getProperty("tipoProduto").toString() : "" );
                        itemSaidaBeans.setAtacadoVarejo( (objeto.hasProperty("atacadoVarejo")) ? objeto.getProperty("atacadoVarejo").toString() : "" );
                        itemSaidaBeans.setTipoBaixa( (objeto.hasProperty("tipoBaixa")) ? objeto.getProperty("tipoBaixa").toString() : "" );
                        itemSaidaBeans.setTipoSaida( (objeto.hasProperty("tipoSaida")) ? objeto.getProperty("tipoSaida").toString() : "" );
                        itemSaidaBeans.setComplemento( (objeto.hasProperty("complemento")) ? objeto.getProperty("complemento").toString() : "" );
                        itemSaidaBeans.setBaixaPorConferencia( (objeto.hasProperty("baixaPorConferencia")) ? objeto.getProperty("baixaPorConferencia").toString() : "" );
                        itemSaidaBeans.setFatorProdutoPesquisado( (objeto.hasProperty("fatorProdutoPesquisado")) ? Double.parseDouble(objeto.getProperty("fatorProdutoPesquisado").toString()) : 0 );

                        // Pega a unidade de venda da item da nota fiscal
                        UnidadeVendaBeans unidadeItem = new UnidadeVendaBeans();
                        SoapObject objetoUnidadeItem = (SoapObject) objeto.getProperty("unidadeVenda");
                        unidadeItem.setIdUnidadeVenda(Integer.parseInt(objetoUnidadeItem.getProperty("idUnidadeVenda").toString()));
                        unidadeItem.setDataAlt(objetoUnidadeItem.getProperty("dataAlt").toString());
                        unidadeItem.setSigla(objetoUnidadeItem.getProperty("sigla").toString());
                        unidadeItem.setDescricaoUnidadeVenda(objetoUnidadeItem.getProperty("descricaoUnidadeVenda").toString());
                        unidadeItem.setDecimais(Integer.parseInt(objetoUnidadeItem.getProperty("decimais").toString()));
                        itemSaidaBeans.setUnidadeVenda(unidadeItem);

                        SoapObject objetoEstoque = (SoapObject) objeto.getProperty("estoque");
                        SoapObject objetoProdutoLoja = (SoapObject) objetoEstoque.getProperty("produtoLoja");
                        SoapObject objetoProduto = (SoapObject) objetoProdutoLoja.getProperty("produto");
                        SoapObject objetoEmbalagemSaida = (SoapObject) objeto.getProperty("embalagemSaida");

                        EmbalagemBeans embalagem = new EmbalagemBeans();
                        embalagem.setIdEmbalagem(Integer.parseInt(objetoEmbalagemSaida.getProperty("idEmbalagem").toString()));
                        embalagem.setIdProduto(Integer.parseInt(objetoEmbalagemSaida.getProperty("idProduto").toString()));
                        embalagem.setIdUnidadeVenda(Integer.parseInt(objetoEmbalagemSaida.getProperty("idUnidadeVenda").toString()));
                        embalagem.setPrincipal( (objetoEmbalagemSaida.hasProperty("principal")) ? objetoEmbalagemSaida.getProperty("principal").toString() : "");
                        embalagem.setDescricaoEmbalagem( (objetoEmbalagemSaida.hasProperty("descricaoEmbalagem")) ? objetoEmbalagemSaida.getProperty("descricaoEmbalagem").toString() : "" );
                        embalagem.setFatorConversao(Double.parseDouble(objetoEmbalagemSaida.getProperty("fatorConversao").toString()));
                        embalagem.setModulo(Integer.parseInt(objetoEmbalagemSaida.getProperty("modulo").toString()));
                        embalagem.setDecimais(Integer.parseInt(objetoEmbalagemSaida.getProperty("decimais").toString()));
                        embalagem.setCodigoBarras( (objetoEmbalagemSaida.hasProperty("codigoBarras")) ? objetoEmbalagemSaida.getProperty("codigoBarras").toString() : "");
                        embalagem.setReferencia( (objetoEmbalagemSaida.hasProperty("referencia")) ? objetoEmbalagemSaida.getProperty("referencia").toString() : "" );
                        itemSaidaBeans.setEmbalagemSaida(embalagem);

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

                        itemSaidaBeans.setEstoque(estoque);
                        // Adiciona o item de saida em uma lista
                        listaItemSaidaArray.add(itemSaidaBeans);
                    }
                }

            } else {

            }
        } catch (Exception e){

            final FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

            // Armazena as informacoes para para serem exibidas e enviadas
            final ContentValues contentValues = new ContentValues();
            contentValues.put("comando", 0);
            contentValues.put("tela", "RomaneiraRotinas");
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

        return listaItemSaidaArray;
    } // Fim listaRomaneio
}
