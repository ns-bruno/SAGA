package com.saga.funcoes.rotinas;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.widget.ProgressBar;

import com.saga.beans.AreasBeans;
import com.saga.beans.CidadeBeans;
import com.saga.beans.EstadoBeans;
import com.saga.beans.ItemRomaneioBeans;
import com.saga.beans.RomaneioBeans;
import com.saga.beans.SaidaBeans;
import com.saga.beans.SerieBeans;
import com.saga.funcoes.FuncoesPersonalizadas;
import com.saga.webservice.WSSisInfoWebservice;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Bruno Nogueira SIlva on 03/05/2016.
 */
public class RomaneioRotinas extends Rotinas {

    public static final int CONFERIDO = 999;
    public static final int SEM_CONFERIR = 888;

    public RomaneioRotinas(Context context) {
        super(context);
    }

    public List<RomaneioBeans> listaRomaneio(String where, int conferidoSemConferir, final ProgressBar progresso){

        List<RomaneioBeans> listaRomaneio = new ArrayList<RomaneioBeans>();

        try {
            FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);
            int limiteDiasConferir = (!funcoes.getValorXml("LimiteDiasConferir").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? Integer.parseInt(funcoes.getValorXml("LimiteDiasConferir")) : 7;
            int codigoEmpresa = (!funcoes.getValorXml("CodigoEmpresa").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? Integer.parseInt(funcoes.getValorXml("CodigoEmpresa")) : 1;

            String sql = "SELECT " +
                    "    AEAROMAN.ID_AEAROMAN, " +
                    "    CFAAREAS.ID_CFAAREAS, " +
                    "    CFAAREAS.CODIGO AS CODIGO_AREAS, " +
                    "    CFAAREAS.DESCRICAO AS DESCRICAO_AREAS, " +
                    "    AEAROMAN.GUID, " +
                    "    AEAROMAN.DT_ALT, " +
                    "    AEAROMAN.NUMERO, " +
                    "    AEAROMAN.DT_ROMANEIO, " +
                    "    AEAROMAN.DT_EMISSAO, " +
                    "    AEAROMAN.DT_SAIDA, " +
                    "    AEAROMAN.DT_FECHAMENTO, " +
                    "    AEAROMAN.VALOR, " +
                    "    AEAROMAN.OBS, " +
                    "    AEAROMAN.SITUACAO \n" +
                    "    FROM AEAROMAN \n" +
                    "    LEFT OUTER JOIN CFAAREAS ON (AEAROMAN.ID_CFAAREAS = CFAAREAS.ID_CFAAREAS) \n" +
                    "    WHERE " +
                    "      (AEAROMAN.ID_SMAEMPRE = " + codigoEmpresa +") AND " +
                    "      (AEAROMAN.DT_ROMANEIO >= (SELECT DATEADD(DAY, -" +limiteDiasConferir + ", CURRENT_DATE) FROM RDB$DATABASE)) ";

            if (conferidoSemConferir == SEM_CONFERIR) {
                sql += " AND (AEAROMAN.DT_FECHAMENTO IS NULL) ";

            } else if (conferidoSemConferir == CONFERIDO){
                sql += " AND (AEAROMAN.DT_FECHAMENTO IS NOT NULL) ";
            }
            // Checa se foi passado alguma clausua where por parametro
            if (where != null) {
                sql += " AND (" + where + ")";
            }

            if (tipoConexao.equalsIgnoreCase("W")){
                WSSisInfoWebservice webserviceSisInfo = new WSSisInfoWebservice(context);
                final Vector<SoapObject> listaRoman = webserviceSisInfo.executarSelectWebservice(sql, WSSisInfoWebservice.FUNCTION_SELECT_LISTA_ROMANEIO, null);

                // Checa se retornou alguma coisa
                if (listaRoman != null && listaRoman.size() > 0){
                    // Checa se tem alguma barra de progresso
                    if (progresso != null) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            public void run() {
                                progresso.setIndeterminate(false);
                                progresso.setProgress(0);
                                progresso.setMax(listaRoman.size());
                            }
                        });
                    }
                    int incremento = 0;
                    // Passa por toda a lista
                    for (SoapObject objetoIndividual : listaRoman) {
                        if (progresso != null) {

                            incremento++;
                            final int finalIncremento = incremento;
                            ((Activity) context).runOnUiThread(new Runnable() {
                                public void run() {
                                    progresso.setProgress(finalIncremento);
                                }
                            });
                        }

                        RomaneioBeans romaneioBeans = new RomaneioBeans();

                        SoapObject objeto;

                        if (objetoIndividual.hasProperty("return")){
                            objeto = (SoapObject) objetoIndividual.getProperty("return");
                        } else {
                            objeto = objetoIndividual;
                        }
                        romaneioBeans.setIdRomaneio(Integer.parseInt(objeto.getProperty("idRomaneio").toString()));
                        romaneioBeans.setGuidRomaneio(objeto.getProperty("guidRomaneio").toString());
                        romaneioBeans.setDataAlt(objeto.getProperty("dataAlt").toString());
                        romaneioBeans.setNumero(Integer.parseInt(objeto.getProperty("numero").toString()));
                        romaneioBeans.setDataRomaneio(objeto.getProperty("dataRomaneio").toString());
                        romaneioBeans.setDataEmissao( (objeto.hasProperty("dataEmissao")) ? objeto.getProperty("dataEmissao").toString() : "" );
                        romaneioBeans.setDataSaida( (objeto.hasProperty("dataSaida")) ? objeto.getProperty("dataSaida").toString() : "" );
                        romaneioBeans.setDataFechamento( (objeto.hasProperty("dataFechamento")) ? objeto.getProperty("dataFechamento").toString() : "" );
                        romaneioBeans.setValor(Double.parseDouble(objeto.getProperty("valor").toString()));
                        romaneioBeans.setObservacaoRomaneio( (objeto.hasProperty("observacaoRomaneio")) ? objeto.getProperty("observacaoRomaneio").toString() : "" );
                        romaneioBeans.setSituacao( (objeto.hasProperty("situacao")) ? objeto.getProperty("situacao").toString() : "" );

                        SoapObject objetoArea = (SoapObject) objeto.getProperty("areaRomaneio");

                        AreasBeans areasBeans = new AreasBeans();
                        areasBeans.setIdAreas(Integer.parseInt(objetoArea.getProperty("idAreas").toString()));
                        areasBeans.setCodigoAreas(Integer.parseInt(objetoArea.getProperty("codigoAreas").toString()));
                        areasBeans.setDescricaoAreas(objetoArea.getProperty("descricaoAreas").toString());

                        romaneioBeans.setAreaRomaneio(areasBeans);
                        // Adiciona o romaneio a uma lista
                        listaRomaneio.add(romaneioBeans);

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

        return listaRomaneio;
    } // Fim listaRomaneio


    public List<ItemRomaneioBeans> listaItemRomaneio(int idRomaneio, String where, int conferidoSemConferir, final ProgressBar progresso){

        List<ItemRomaneioBeans> listaItemRomaneio = new ArrayList<ItemRomaneioBeans>();

        try {
            FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);
            //int limiteDiasConferir = (!funcoes.getValorXml("LimiteDiasConferir").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? Integer.parseInt(funcoes.getValorXml("LimiteDiasConferir")) : 7;

            String sql =
                    "SELECT AEAITROM.ID_AEAITROM, AEAITROM.ID_AEAROMAN, AEAITROM.GUID AS GUID_ITROM, AEAITROM.DT_CAD AS DT_CAD_ITROM, \n" +
                    "AEAITROM.DT_ALT AS DT_ALT_ITROM, AEAITROM.SEQUENCIA, AEAITROM.VL_SAIDA, AEAITROM.CONFERIDO AS CONFERIDO_ITROM, \n" +
                    "AEAITROM.SITUACAO AS SITUACAO_ITROM, AEAITROM.OBS AS OBS_ITROM, AEASAIDA.ID_AEASAIDA, AEASAIDA.ID_SMAEMPRE, \n" +
                    "AEASAIDA.ID_CFACLIFO, AEASAIDA.ID_CFAESTAD, AEASAIDA.ID_CFACIDAD, AEASAIDA.GUID AS GUID_SAIDA, AEASAIDA.DT_CAD AS DT_CAD_SAIDA, \n" +
                    "AEASAIDA.DT_ALT AS DT_ALT_SAIDA, AEASAIDA.NUMERO AS NUMERO_SAIDA, AEASAIDA.DT_VENDA AS DT_VENDA_SAIDA, AEASAIDA.DT_EMISSAO AS DT_EMISSAO_SAIDA, \n" +
                    "AEASAIDA.DT_SAIDA AS DT_SAIDA_SAIDA, AEASAIDA.DT_CANCEL AS DT_CANCEL_SAIDA, AEASAIDA.ATAC_VAREJO AS ATAC_VAREJO_SAIDA, AEASAIDA.FC_VL_TOTAL AS FC_VL_TOTAL_SAIDA, \n" +
                    "AEASAIDA.SITUACAO AS SITUACAO_SAIDA, AEASAIDA.OBS AS OBS_SAIDA, AEASAIDA.PESSOA_CLIENTE, AEASAIDA.NOME_CLIENTE, \n" +
                    "AEASAIDA.CPF_CGC_CLIENTE, AEASAIDA.IE_RG_CLIENTE, AEASAIDA.ENDERECO_CLIENTE, \n" +
                    "AEASAIDA.BAIRRO_CLIENTE, AEASAIDA.CEP_CLIENTE, AEASAIDA.TIPO_SAIDA AS TIPO_SAIDA_SAIDA, AEASAIDA.TIPO_ENTREGA AS TIPO_ENTREGA_SAIDA,  \n" +
                    "CFAESTAD.ID_CFAESTAD, CFAESTAD.UF, CFAESTAD.DESCRICAO AS DESCRICAO_ESTAD, \n" +
                    "CFACIDAD.ID_CFACIDAD, CFACIDAD.DESCRICAO AS DESCRICAO_CIDAD, \n" +
                    "AEASERIE.ID_AEASERIE, AEASERIE.ID_SMAEMPRE AS ID_SMAEMPRE_SERIE, AEASERIE.GUID AS GUID_SERIE, AEASERIE.DT_ALT AS DT_ALT_SERIE, \n" +
                    "AEASERIE.TIPO AS TIPO_SERIE, AEASERIE.SERIE AS SERIE_SERIE, AEASERIE.SUBSERIE, AEASERIE.CODIGO AS CODIGO_SERIE, AEASERIE.NUMERO AS NUMERO_SERIE " +
                    "FROM AEAITROM \n" +
                    "LEFT OUTER JOIN AEASAIDA AEASAIDA \n" +
                    "ON(AEAITROM.ID_AEASAIDA = AEASAIDA.ID_AEASAIDA) \n" +
                    "LEFT OUTER JOIN CFAESTAD CFAESTAD \n" +
                    "ON(AEASAIDA.ID_CFAESTAD = CFAESTAD.ID_CFAESTAD) \n" +
                    "LEFT OUTER JOIN CFACIDAD CFACIDAD \n" +
                    "ON(AEASAIDA.ID_CFACIDAD = CFACIDAD.ID_CFACIDAD) \n" +
                    "LEFT OUTER JOIN AEASERIE AEASERIE \n" +
                    "ON(AEASAIDA.ID_AEASERIE = AEASERIE.ID_AEASERIE) \n" +
                    " WHERE " +
                    "      (AEAITROM.ID_AEAROMAN = " + idRomaneio +") ";
                    //"      (AEAROMAN.DT_ROMANEIO >= (SELECT DATEADD(DAY, -" +limiteDiasConferir + ", CURRENT_DATE) FROM RDB$DATABASE)) ";

            if (conferidoSemConferir == SEM_CONFERIR) {
                sql += " AND ( (AEAITROM.CONFERIDO = '0') || (AEAITROM.CONFERIDO IS NULL) ) ";

            } else if (conferidoSemConferir == CONFERIDO){
                sql += " AND (AEAITROM.CONFERIDO = '1') ";
            }
            // Checa se foi passado alguma clausua where por parametro
            if (where != null) {
                sql += " AND (" + where + ")";
            }

            if (tipoConexao.equalsIgnoreCase("W")){
                WSSisInfoWebservice webserviceSisInfo = new WSSisInfoWebservice(context);
                final Vector<SoapObject> listaRoman = webserviceSisInfo.executarSelectWebservice(sql, WSSisInfoWebservice.FUNCTION_SELECT_LISTA_ITEM_ROMANEIO, null);

                // Checa se retornou alguma coisa
                if (listaRoman != null && listaRoman.size() > 0){
                    // Checa se tem alguma barra de progresso
                    if (progresso != null) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            public void run() {
                                progresso.setIndeterminate(false);
                                progresso.setProgress(0);
                                progresso.setMax(listaRoman.size());
                            }
                        });
                    }
                    int incremento = 0;
                    // Passa por toda a lista
                    for (SoapObject objetoIndividual : listaRoman) {
                        if (progresso != null) {

                            incremento++;
                            final int finalIncremento = incremento;
                            ((Activity) context).runOnUiThread(new Runnable() {
                                public void run() {
                                    progresso.setProgress(finalIncremento);
                                }
                            });
                        }

                        RomaneioBeans romaneioBeans = new RomaneioBeans();

                        SoapObject objeto;

                        if (objetoIndividual.hasProperty("return")){
                            objeto = (SoapObject) objetoIndividual.getProperty("return");
                        } else {
                            objeto = objetoIndividual;
                        }
                        ItemRomaneioBeans itemRomaneioBeans = new ItemRomaneioBeans();
                        itemRomaneioBeans.setIdItemRomaneio(Integer.parseInt(objeto.getProperty("idItemRomaneio").toString()));
                        itemRomaneioBeans.setIdRomaneio(Integer.parseInt(objeto.getProperty("idRomaneio").toString()));
                        itemRomaneioBeans.setSeguencia(Integer.parseInt(objeto.getProperty("seguencia").toString()));
                        itemRomaneioBeans.setGuidItemRomaneio(objeto.getProperty("guidItemRomaneio").toString());
                        itemRomaneioBeans.setDataCad(objeto.getProperty("dataCad").toString());
                        itemRomaneioBeans.setDataAlt(objeto.getProperty("dataAlt").toString());
                        itemRomaneioBeans.setValor(Double.parseDouble(objeto.getProperty("valor").toString()));
                        itemRomaneioBeans.setConferido(objeto.getProperty("conferido").toString());
                        itemRomaneioBeans.setSituacao(objeto.getProperty("situacao").toString());
                        itemRomaneioBeans.setObs( (objeto.hasProperty("obs")) ? objeto.getProperty("obs").toString() : "");

                        SoapObject objetoSaida = (SoapObject) objeto.getProperty("saida");

                        SaidaBeans saidaBeans = new SaidaBeans();
                        saidaBeans.setIdSaida(Integer.parseInt(objetoSaida.getProperty("idSaida").toString()));
                        saidaBeans.setIdEmpresa(Integer.parseInt(objetoSaida.getProperty("idEmpresa").toString()));
                        saidaBeans.setIdClifo(Integer.parseInt(objetoSaida.getProperty("idClifo").toString()));
                        saidaBeans.setGuidSaida(objetoSaida.getProperty("guidSaida").toString());
                        saidaBeans.setDataCad(objetoSaida.getProperty("dataCad").toString());
                        saidaBeans.setDataAlt(objetoSaida.getProperty("dataAlt").toString());
                        saidaBeans.setNumeroSaida(Integer.parseInt(objetoSaida.getProperty("numeroSaida").toString()));
                        saidaBeans.setDataVenda( (objetoSaida.hasProperty("dataVenda")) ? objetoSaida.getProperty("dataVenda").toString() : "");
                        saidaBeans.setDataEmissao( (objetoSaida.hasProperty("dataEmissao")) ? objetoSaida.getProperty("dataEmissao").toString() : "");
                        saidaBeans.setDataSaida( (objetoSaida.hasProperty("dataSaida")) ? objetoSaida.getProperty("dataSaida").toString() : "");
                        saidaBeans.setDataCancelado( (objetoSaida.hasProperty("dataCancelado")) ? objetoSaida.getProperty("dataCancelado").toString() : "");
                        saidaBeans.setAtacadoVarejo(objetoSaida.getProperty("atacadoVarejo").toString());
                        saidaBeans.setValorTotalSaida(Double.parseDouble(objetoSaida.getProperty("valorTotalSaida").toString()));
                        saidaBeans.setSituacao(objetoSaida.getProperty("situacao").toString());
                        saidaBeans.setObservacao( (objetoSaida.hasProperty("observacao")) ? objetoSaida.getProperty("observacao").toString() : "");
                        saidaBeans.setPessoaCliente( (objetoSaida.hasProperty("pessoaCliente")) ? objetoSaida.getProperty("pessoaCliente").toString() : "");
                        saidaBeans.setNomeCliente( (objetoSaida.hasProperty("nomeCliente")) ? objetoSaida.getProperty("nomeCliente").toString() : "");
                        saidaBeans.setCpfCgcCliente( (objetoSaida.hasProperty("cpfCgcCliente")) ? objetoSaida.getProperty("cpfCgcCliente").toString() : "");
                        saidaBeans.setIeRgCliente( (objetoSaida.hasProperty("ieRgCliente")) ? objetoSaida.getProperty("ieRgCliente").toString() : "");
                        saidaBeans.setEnderecoCliente( (objetoSaida.hasProperty("enderecoCliente")) ? objetoSaida.getProperty("enderecoCliente").toString() : "");
                        saidaBeans.setBairroCliente( (objetoSaida.hasProperty("bairroCliente")) ? objetoSaida.getProperty("bairroCliente").toString() : "");
                        saidaBeans.setCepCliente( (objetoSaida.hasProperty("cepCliente")) ? objetoSaida.getProperty("cepCliente").toString() : "");
                        saidaBeans.setTipoEntrega( (objetoSaida.hasProperty("tipoEntrega")) ? objetoSaida.getProperty("tipoEntrega").toString() : "");

                        SoapObject objetoEstado = (SoapObject) objetoSaida.getProperty("estadoSaida");

                        EstadoBeans estadoBeans = new EstadoBeans();
                        estadoBeans.setIdEstado(Integer.parseInt(objetoEstado.getProperty("idEstado").toString()));
                        estadoBeans.setUf(objetoEstado.getProperty("uf").toString());
                        estadoBeans.setDescricaoEstado(objetoEstado.getProperty("descricaoEstado").toString());
                        // Adiciona o estado na saida
                        saidaBeans.setEstadoSaida(estadoBeans);

                        SoapObject objetoCidade = (SoapObject) objetoSaida.getProperty("cidadeSaida");

                        CidadeBeans cidadeBeans = new CidadeBeans();
                        cidadeBeans.setIdCidade(Integer.parseInt(objetoCidade.getProperty("idCidade").toString()));
                        cidadeBeans.setDescricaoCidade(objetoCidade.getProperty("descricaoCidade").toString());
                        // Adiciona a cidade na saida
                        saidaBeans.setCidadeSaida(cidadeBeans);

                        SoapObject objetoSerie = (SoapObject) objetoSaida.getProperty("serieSaida");

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

                        // Adiciona a saida no item do romaneio
                        itemRomaneioBeans.setSaida(saidaBeans);

                        // Adiciona o item de romaneio em uma lista
                        listaItemRomaneio.add(itemRomaneioBeans);
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
        return listaItemRomaneio;
    }// Fim listaItemRomaneio
}
