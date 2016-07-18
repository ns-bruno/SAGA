package com.saga.funcoes.rotinas;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.widget.ProgressBar;

import com.saga.beans.CidadeBeans;
import com.saga.beans.EmbalagemBeans;
import com.saga.beans.EstadoBeans;
import com.saga.beans.EstoqueBeans;
import com.saga.beans.ItemOrcamentoBeans;
import com.saga.beans.MarcaBeans;
import com.saga.beans.OrcamentoBeans;
import com.saga.beans.ProdutoBeans;
import com.saga.beans.ProdutoLojaBeans;
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
 * Created by Bruno Nogueira Silva on 13/06/2016.
 */
public class OrcamentoRotinas extends Rotinas {

    public OrcamentoRotinas(Context context) {
        super(context);
    }

    public List<OrcamentoBeans> listaOrcamento(String where, int conferidoSemConferir, final ProgressBar progresso){

        List<OrcamentoBeans> listaOrcamentoArray = new ArrayList<OrcamentoBeans>();

        try {
            FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);
            int limiteDiasConferir = (!funcoes.getValorXml("LimiteDiasConferir").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? Integer.parseInt(funcoes.getValorXml("LimiteDiasConferir")) : 7;
            int codigoEmpresa = (!funcoes.getValorXml("CodigoEmpresa").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? Integer.parseInt(funcoes.getValorXml("CodigoEmpresa")) : 1;

            String sql = "SELECT AEAORCAM.ID_AEAORCAM, AEAORCAM.ID_SMAEMPRE, AEAORCAM.ID_CFACLIFO, AEAORCAM.ID_AEAROMAN, \n" +
                    "AEAORCAM.GUID AS GUID_ORCAM, AEAORCAM.DT_ALT AS DT_ALT_ORCAM, AEAORCAM.NUMERO AS NUMERO_ORCAM, \n" +
                    "AEAORCAM.DT_EMISSAO AS DT_EMISSAO_ORCAM, AEAORCAM.DT_ORCAMENTO AS DT_ORCAMENTO_ORCAM, \n" +
                    "AEAORCAM.DT_VALIDADE AS DT_VALIDADE_ORCAM, AEAORCAM.ATAC_VAREJO AS ATAC_VAREJO_ORCAM, \n" +
                    "AEAORCAM.FC_VL_TOTAL AS FC_VL_TOTAL_ORCAM, AEAORCAM.OBS AS OBS_ORCAM, AEAORCAM.PESSOA_CLIENTE, \n" +
                    "AEAORCAM.NOME_CLIENTE, AEAORCAM.CPF_CGC_CLIENTE, AEAORCAM.IE_RG_CLIENTE, AEAORCAM.ENDERECO_CLIENTE, \n" +
                    "AEAORCAM.BAIRRO_CLIENTE, AEAORCAM.CEP_CLIENTE, AEAORCAM.TIPO_ENTREGA AS TIPO_ENTREGA_ORCAM, AEAORCAM.TIPO_BAIXA, \n" +
                    "CFAESTAD.ID_CFAESTAD, CFAESTAD.UF, CFAESTAD.DESCRICAO AS DESCRICAO_ESTAD, \n" +
                    "CFACIDAD.ID_CFACIDAD, CFACIDAD.DESCRICAO AS DESCRICAO_CIDAD, \n" +
                    "AEASERIE.ID_AEASERIE, AEASERIE.ID_SMAEMPRE AS ID_SMAEMPRE_SERIE, AEASERIE.GUID AS GUID_SERIE, AEASERIE.DT_ALT AS DT_ALT_SERIE, \n" +
                    "AEASERIE.TIPO AS TIPO_SERIE, AEASERIE.SERIE AS SERIE_SERIE, AEASERIE.SUBSERIE, AEASERIE.CODIGO AS CODIGO_SERIE \n" +
                    "FROM AEAORCAM \n" +
                    "LEFT OUTER JOIN CFAESTAD CFAESTAD\n" +
                    "ON(AEAORCAM.ID_CFAESTAD = CFAESTAD.ID_CFAESTAD) \n" +
                    "LEFT OUTER JOIN CFACIDAD CFACIDAD \n" +
                    "ON(AEAORCAM.ID_CFACIDAD = CFACIDAD.ID_CFACIDAD) \n" +
                    "LEFT OUTER JOIN AEASERIE AEASERIE \n" +
                    "ON(AEAORCAM.ID_AEASERIE = AEASERIE.ID_AEASERIE) \n" +
                    "    WHERE " +
                    "      (AEAORCAM.ID_SMAEMPRE = " + codigoEmpresa +") AND " +
                    "      (AEAORCAM.DT_ORCAMENTO >= (SELECT DATEADD(DAY, -" +limiteDiasConferir + ", CURRENT_DATE) FROM RDB$DATABASE)) \n";

            if (conferidoSemConferir == SEM_CONFERIR) {
                sql += " AND ( (AEAORCAM.SITUACAO = '0') OR (AEAORCAM.SITUACAO = '1') OR (AEAORCAM.SITUACAO = '3') ) ";

            } else if (conferidoSemConferir == CONFERIDO){
                sql += " AND (AEAORCAM.SITUACAO <> '0') AND (AEAORCAM.SITUACAO <> '1') AND (AEAORCAM.SITUACAO <> '3') ";
            }
            // Checa se foi passado alguma clausua where por parametro
            if (where != null) {
                sql += " AND (" + where + ")";
            }

            sql += " ORDER BY AEAORCAM.DT_ORCAMENTO DESC, AEAORCAM.NUMERO DESC ";

            if (tipoConexao.equalsIgnoreCase("W")){
                WSSisInfoWebservice webserviceSisInfo = new WSSisInfoWebservice(context);
                final Vector<SoapObject> listaOrcamentoSoap = webserviceSisInfo.executarSelectWebservice(sql, WSSisInfoWebservice.FUNCTION_SELECT_LISTA_ORCAMENTO, null);

                // Checa se retornou alguma coisa
                if (listaOrcamentoSoap != null && listaOrcamentoSoap.size() > 0){
                    // Checa se tem alguma barra de progresso
                    if (progresso != null) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            public void run() {
                                progresso.setIndeterminate(false);
                                progresso.setProgress(0);
                                progresso.setMax(listaOrcamentoSoap.size());
                            }
                        });
                    }
                    int incremento = 0;
                    // Passa por toda a lista
                    for (SoapObject objetoIndividual : listaOrcamentoSoap) {
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
                        OrcamentoBeans orcamentoBeans = new OrcamentoBeans();
                        orcamentoBeans.setIdOrcamento(Integer.parseInt(objeto.getProperty("idOrcamento").toString()));
                        orcamentoBeans.setIdEmpresa(Integer.parseInt(objeto.getProperty("idEmpresa").toString()));
                        orcamentoBeans.setIdClifoCliente(Integer.parseInt(objeto.getProperty("idClifoCliente").toString()));
                        orcamentoBeans.setIdRomaneio(Integer.parseInt(objeto.getProperty("idRomaneio").toString()));
                        orcamentoBeans.setGuidOrcamento(objeto.getProperty("guidOrcamento").toString());
                        orcamentoBeans.setDataOrcamento(objeto.getProperty("dataOrcamento").toString());
                        orcamentoBeans.setDataAlt(objeto.getProperty("dataAlt").toString());
                        orcamentoBeans.setDataEmissao( (objeto.hasProperty("dataEmissao")) ? objeto.getProperty("dataEmissao").toString() : "");
                        orcamentoBeans.setDataValidade( (objeto.hasProperty("dataValidade")) ? objeto.getProperty("dataValidade").toString() : "");
                        orcamentoBeans.setNumeroOrcamento(Integer.parseInt(objeto.getProperty("numeroOrcamento").toString()));
                        orcamentoBeans.setAtacadoVarejo(objeto.getProperty("atacadoVarejo").toString());
                        orcamentoBeans.setValorTotalOrcamento(Double.parseDouble(objeto.getProperty("valorTotalOrcamento").toString()));
                        orcamentoBeans.setObsOrcamento( (objeto.hasProperty("obsOrcamento")) ? objeto.getProperty("obsOrcamento").toString() : "");
                        orcamentoBeans.setPessoaCliente( (objeto.hasProperty("pessoaCliente")) ? objeto.getProperty("pessoaCliente").toString() : "");
                        orcamentoBeans.setNomeCliente( (objeto.hasProperty("nomeCliente")) ? objeto.getProperty("nomeCliente").toString() : "");
                        orcamentoBeans.setCpfCgcCliente( (objeto.hasProperty("cpfCgcCliente")) ? objeto.getProperty("cpfCgcCliente").toString() : "");
                        orcamentoBeans.setIeRgCliente( (objeto.hasProperty("ieRgCliente")) ? objeto.getProperty("ieRgCliente").toString() : "");
                        orcamentoBeans.setEnderecoCliente( (objeto.hasProperty("enderecoCliente")) ? objeto.getProperty("enderecoCliente").toString() : "");
                        orcamentoBeans.setBairroCliente( (objeto.hasProperty("bairroCliente")) ? objeto.getProperty("bairroCliente").toString() : "");
                        orcamentoBeans.setCepCliente( (objeto.hasProperty("cepCliente")) ? objeto.getProperty("cepCliente").toString() : "");
                        orcamentoBeans.setTipoEntregaOrcamento( (objeto.hasProperty("tipoEntregaOrcamento")) ? objeto.getProperty("tipoEntregaOrcamento").toString() : "");
                        orcamentoBeans.setTipoBaixaOrcamento( (objeto.hasProperty("tipoBaixaOrcamento")) ? objeto.getProperty("tipoBaixaOrcamento").toString() : "");

                        SoapObject objetoEstado = (SoapObject) objeto.getProperty("estadoOrcamento");

                        EstadoBeans estadoBeans = new EstadoBeans();
                        estadoBeans.setIdEstado(Integer.parseInt( objetoEstado.getProperty("idEstado").toString()));
                        estadoBeans.setUf((objetoEstado.hasProperty("uf")) ? objetoEstado.getProperty("uf").toString() : "");
                        estadoBeans.setDescricaoEstado((objetoEstado.hasProperty("descricaoEstado")) ? objetoEstado.getProperty("descricaoEstado").toString() : "");
                        // Adiciona o estado no orcamento
                        orcamentoBeans.setEstadoOrcamento(estadoBeans);

                        SoapObject objetoCidade = (SoapObject) objeto.getProperty("cidadeOrcamento");

                        CidadeBeans cidadeBeans = new CidadeBeans();
                        cidadeBeans.setIdCidade(Integer.parseInt(objetoCidade.getProperty("idCidade").toString()));
                        cidadeBeans.setDescricaoCidade((objetoCidade.hasProperty("descricaoCidade")) ? objetoCidade.getProperty("descricaoCidade").toString() : "");
                        // Adiciona a cidade no orcamento
                        orcamentoBeans.setCidadeOrcamento(cidadeBeans);

                        SoapObject objetoSerie = (SoapObject) objeto.getProperty("serieOrcamento");

                        SerieBeans serieBeans = new SerieBeans();
                        serieBeans.setIdSerie(Integer.parseInt(objetoSerie.getProperty("idSerie").toString()));
                        serieBeans.setIdEmpresa(Integer.parseInt(objetoSerie.getProperty("idEmpresa").toString()));
                        serieBeans.setGuidSerie(objetoSerie.getProperty("guidSerie").toString());
                        serieBeans.setDataAlt(objetoSerie.getProperty("dataAlt").toString());
                        serieBeans.setTipoSerie(objetoSerie.getProperty("tipoSerie").toString());
                        serieBeans.setSerie(objetoSerie.getProperty("serie").toString());
                        serieBeans.setSubserie( (objetoSerie.hasProperty("subSerie")) ? objetoSerie.getProperty("subSerie").toString() : "");
                        serieBeans.setCodigo( (objetoSerie.hasProperty("codigo")) ? objetoSerie.getProperty("codigo").toString() : "");
                        // Adiciona a serie no orcamento
                        orcamentoBeans.setSerieOrcamento(serieBeans);

                        // Adiciona o item de romaneio em uma lista
                        listaOrcamentoArray.add(orcamentoBeans);
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

        return listaOrcamentoArray;
    } // Fim listaOrcamento

    public List<ItemOrcamentoBeans> listaItemOrcamento(int idOrcamento, int pesquisarProduto, String where, int conferidoSemConferir, final ProgressBar progresso){

        List<ItemOrcamentoBeans> listaItemOrcamentoArray = new ArrayList<ItemOrcamentoBeans>();

        try {

            String sql = "SELECT AEAITORC.ID_AEAITORC, AEAITORC.ID_AEAORCAM, AEAITORC.ID_AEAITORC_CONJ, \n" +
                    "AEAITORC.GUID, AEAITORC.DT_ALT, AEAITORC.SEQUENCIA, AEAITORC.QUANTIDADE, AEAITORC.FC_LIQUIDO, AEAITORC.TIPO_PRODUTO, AEAITORC.COMPLEMENTO, \n" +
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

                    "AEAEMBAL_ITORC.ID_AEAEMBAL AS ID_AEAEMBAL_AEAEMBAL_ITSAI, AEAEMBAL_ITORC.ID_AEAPRODU AS ID_AEAPRODU_AEAEMBAL_ITSAI, AEAEMBAL_ITORC.ID_AEAUNVEN AS ID_AEAUNVEN_AEAEMBAL_ITSAI, \n" +
                    "AEAEMBAL_ITORC.PRINCIPAL AS PRINCIPAL_AEAEMBAL_ITSAI, AEAEMBAL_ITORC.DESCRICAO AS DESCRICAO_AEAEMBAL_ITSAI, AEAEMBAL_ITORC.FATOR_CONVERSAO AS FATOR_AEAEMBAL_ITSAI, \n" +
                    "AEAEMBAL_ITORC.MODULO AS MODULO_AEAEMBAL_ITSAI, AEAEMBAL_ITORC.DECIMAIS AS DECIMAIS_AEAEMBAL_ITSAI, AEAEMBAL_ITORC.CODIGO_BARRAS AS CODIGO_BARRAS_AEAEMBAL_ITSAI, \n" +
                    "AEAEMBAL_ITORC.REFERENCIA AS REFERENCIA_AEAEMBAL_ITSAI, \n" +

                    /*"AEAEMBAL_PRODU.ID_AEAEMBAL AS ID_AEAEMBAL_AEAEMBAL_PRODU, AEAEMBAL_PRODU.ID_AEAPRODU AS ID_AEAPRODU_AEAEMBAL_PRODU, AEAEMBAL_PRODU.ID_AEAUNVEN AS ID_AEAUNVEN_AEAEMBAL_PRODU, \n" +
                    "AEAEMBAL_PRODU.PRINCIPAL AS PRINCIPAL_AEAEMBAL_PRODU, AEAEMBAL_PRODU.DESCRICAO AS DESCRICAO_AEAEMBAL_PRODU, AEAEMBAL_PRODU.FATOR_CONVERSAO AS FATOR_AEAEMBAL_PRODU, \n" +
                    "AEAEMBAL_PRODU.MODULO AS MODULO_AEAEMBAL_PRODU, AEAEMBAL_PRODU.DECIMAIS AS DECIMAIS_AEAEMBAL_PRODU, AEAEMBAL_PRODU.CODIGO_BARRAS AS CODIGO_BARRAS_AEAEMBAL_PRODU, \n" +
                    "AEAEMBAL_PRODU.REFERENCIA AS REFERENCIA_AEAEMBAL_PRODU, \n" +*/

                    "(AEAEMBAL_PRODU.FATOR_CONVERSAO / AEAEMBAL_ITORC.FATOR_CONVERSAO) AS FATOR_PRODUTO_PESQUISADO, \n" +
                    "AEATPGRD.ID_AEATPGRD, AEATPGRD.DT_ALT AS DT_ALT_TPGRD, AEATPGRD.DESCRICAO AS DESCRICAO_TPGRD \n" +

                    "FROM AEAITORC \n" +

                    "LEFT OUTER JOIN AEAUNVEN AEAUNVEN \n" +
                    "ON(AEAITORC.ID_AEAUNVEN = AEAUNVEN.ID_AEAUNVEN) \n" +
                    "LEFT OUTER JOIN AEAESTOQ AEAESTOQ \n" +
                    "ON(AEAITORC.ID_AEAESTOQ = AEAESTOQ.ID_AEAESTOQ) \n" +
                    "LEFT OUTER JOIN AEAPLOJA AEAPLOJA \n" +
                    "ON(AEAESTOQ.ID_AEAPLOJA = AEAPLOJA.ID_AEAPLOJA) \n" +
                    "LEFT OUTER JOIN AEAPRODU AEAPRODU \n" +
                    "ON(AEAPLOJA.ID_AEAPRODU = AEAPRODU.ID_AEAPRODU) \n" +
                    "LEFT OUTER JOIN AEAMARCA AEAMARCA \n" +
                    "ON(AEAPRODU.ID_AEAMARCA = AEAMARCA.ID_AEAMARCA) \n" +
                    "LEFT OUTER JOIN AEAUNVEN AEAUNVEN_PRODU \n" +
                    "ON(AEAPRODU.ID_AEAUNVEN = AEAUNVEN_PRODU.ID_AEAUNVEN) \n" +
                    "LEFT OUTER JOIN AEAEMBAL AEAEMBAL_ITORC \n" +
                    "ON((AEAEMBAL_ITORC.ID_AEAUNVEN = AEAITORC.ID_AEAUNVEN) AND (AEAEMBAL_ITORC.ID_AEAPRODU = AEAPRODU.ID_AEAPRODU)) \n" +
                    "LEFT OUTER JOIN AEAEMBAL AEAEMBAL_PRODU \n" +
                    "ON((AEAEMBAL_PRODU.ID_AEAPRODU = AEAPRODU.ID_AEAPRODU) AND (AEAEMBAL_PRODU.ID_AEAUNVEN = AEAUNVEN_PRODU.ID_AEAUNVEN)) \n" +
                    "LEFT OUTER JOIN AEATPGRD AEATPGRD \n" +
                    "ON(AEAPRODU.ID_AEATPGRD = AEATPGRD.ID_AEATPGRD) \n" +
                    "    WHERE \n" +
                    "      (AEAITORC.ID_AEAORCAM = " + idOrcamento + ") \n";

            if (conferidoSemConferir == SEM_CONFERIR) {
                sql += " AND ( AEAITORC.QTDE_CONFERIDO < AEAORCAM.QUANTIDADE ) ";

            } else if (conferidoSemConferir == CONFERIDO){
                sql += " AND ( AEAITORC.QTDE_CONFERIDO >= AEAORCAM.QUANTIDADE ) ";
            }
            // Checa se foi passado alguma clausua where por parametro
            if (where != null) {
                sql += " AND (" + where + ")";
            }

            sql += " ORDER BY AEAITORC.SEQUENCIA ASC ";

            if (tipoConexao.equalsIgnoreCase("W")){
                WSSisInfoWebservice webserviceSisInfo = new WSSisInfoWebservice(context);
                final Vector<SoapObject> listaItemSoap = webserviceSisInfo.executarSelectWebservice(sql, WSSisInfoWebservice.FUNCTION_SELECT_LISTA_ITEM_ORCAMENTO, null);

                // Checa se retornou alguma coisa
                if (listaItemSoap != null && listaItemSoap.size() > 0){
                    // Checa se tem alguma barra de progresso
                    if (progresso != null) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            public void run() {
                                progresso.setIndeterminate(false);
                                progresso.setProgress(0);
                                progresso.setMax(listaItemSoap.size());
                            }
                        });
                    }
                    int incremento = 0;
                    // Passa por toda a lista
                    for (SoapObject objetoIndividual : listaItemSoap) {
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

                        ItemOrcamentoBeans itemOrcamentoBeans = new ItemOrcamentoBeans();
                        itemOrcamentoBeans.setIdItemOrcamento(Integer.parseInt(objeto.getProperty("idItemOrcamento").toString()));
                        itemOrcamentoBeans.setIdOrcamento(Integer.parseInt(objeto.getProperty("idOrcamento").toString()));
                        itemOrcamentoBeans.setIdItemOrcamentoConjunto(Integer.parseInt(objeto.getProperty("idItemOrcamentoConjunto").toString()));
                        itemOrcamentoBeans.setGuidItemOrcamento(objeto.getProperty("guidItemOrcamento").toString());
                        itemOrcamentoBeans.setDataAlt(objeto.getProperty("dataAlt").toString());
                        itemOrcamentoBeans.setSequencia(Integer.parseInt(objeto.getProperty("sequencia").toString()));
                        itemOrcamentoBeans.setQuantidadeItemOrcamento(Double.parseDouble(objeto.getProperty("quantidadeItemOrcamento").toString()));
                        //itemOrcamentoBeans.setQuantidadeConferido( (objeto.hasProperty("quantidadeConferido")) ? Double.parseDouble(objeto.getProperty("quantidadeConferido").toString()) : 0);
                        itemOrcamentoBeans.setValorTotalItemOrcamento( (objeto.hasProperty("valorTotalLiquido")) ? Double.parseDouble(objeto.getProperty("valorTotalLiquido").toString()) : 0);
                        itemOrcamentoBeans.setTipoProduto( (objeto.hasProperty("tipoProduto")) ? objeto.getProperty("tipoProduto").toString() : "" );
                        itemOrcamentoBeans.setComplemento( (objeto.hasProperty("complemento")) ? objeto.getProperty("complemento").toString() : "" );

                        // Pega a unidade de venda da item da nota fiscal
                        UnidadeVendaBeans unidadeItem = new UnidadeVendaBeans();
                        SoapObject objetoUnidadeItem = (SoapObject) objeto.getProperty("unidadeVenda");
                        unidadeItem.setIdUnidadeVenda(Integer.parseInt(objetoUnidadeItem.getProperty("idUnidadeVenda").toString()));
                        unidadeItem.setDataAlt(objetoUnidadeItem.getProperty("dataAlt").toString());
                        unidadeItem.setSigla(objetoUnidadeItem.getProperty("sigla").toString());
                        unidadeItem.setDescricaoUnidadeVenda(objetoUnidadeItem.getProperty("descricaoUnidadeVenda").toString());
                        unidadeItem.setDecimais(Integer.parseInt(objetoUnidadeItem.getProperty("decimais").toString()));
                        itemOrcamentoBeans.setUnidadeVenda(unidadeItem);

                        SoapObject objetoEstoque = (SoapObject) objeto.getProperty("estoqueItemOrcamento");
                        SoapObject objetoProdutoLoja = (SoapObject) objetoEstoque.getProperty("produtoLoja");
                        SoapObject objetoProduto = (SoapObject) objetoProdutoLoja.getProperty("produto");
                        SoapObject objetoEmbalagemItem = (SoapObject) objeto.getProperty("embalagemOrcamento");

                        EmbalagemBeans embalagem = new EmbalagemBeans();
                        embalagem.setIdEmbalagem(Integer.parseInt(objetoEmbalagemItem.getProperty("idEmbalagem").toString()));
                        embalagem.setIdProduto(Integer.parseInt(objetoEmbalagemItem.getProperty("idProduto").toString()));
                        embalagem.setIdUnidadeVenda(Integer.parseInt(objetoEmbalagemItem.getProperty("idUnidadeVenda").toString()));
                        embalagem.setPrincipal( (objetoEmbalagemItem.hasProperty("principal")) ? objetoEmbalagemItem.getProperty("principal").toString() : "");
                        embalagem.setDescricaoEmbalagem( (objetoEmbalagemItem.hasProperty("descricaoEmbalagem")) ? objetoEmbalagemItem.getProperty("descricaoEmbalagem").toString() : "" );
                        embalagem.setFatorConversao(Double.parseDouble(objetoEmbalagemItem.getProperty("fatorConversao").toString()));
                        embalagem.setModulo(Integer.parseInt(objetoEmbalagemItem.getProperty("modulo").toString()));
                        embalagem.setDecimais(Integer.parseInt(objetoEmbalagemItem.getProperty("decimais").toString()));
                        embalagem.setCodigoBarras( (objetoEmbalagemItem.hasProperty("codigoBarras")) ? objetoEmbalagemItem.getProperty("codigoBarras").toString() : "");
                        embalagem.setReferencia( (objetoEmbalagemItem.hasProperty("referencia")) ? objetoEmbalagemItem.getProperty("referencia").toString() : "" );
                        itemOrcamentoBeans.setEmbalagemOrcamento(embalagem);

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

                        itemOrcamentoBeans.setEstoqueItemOrcamento(estoque);
                        // Adiciona o item do orcamento em uma lista
                        listaItemOrcamentoArray.add(itemOrcamentoBeans);
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

        return listaItemOrcamentoArray;
    } // Fim listaItemOrcamento
}
