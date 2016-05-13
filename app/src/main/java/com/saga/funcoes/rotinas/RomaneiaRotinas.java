package com.saga.funcoes.rotinas;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.widget.ProgressBar;

import com.saga.beans.RomaneioBeans;
import com.saga.funcoes.FuncoesPersonalizadas;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Faturamento on 03/05/2016.
 */
public class RomaneiaRotinas extends Rotinas {

    public static final int CONFERIDO = 999;
    public static final int SEM_CONFERIR = 888;

    public RomaneiaRotinas(Context context) {
        super(context);
    }

    public List<RomaneioBeans> listaRomaneio(String where, int conferidoSemConferir, final ProgressBar progresso){

        List<RomaneioBeans> listaRomaneio = new ArrayList<RomaneioBeans>();

        try {
            FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);
            int limiteDiasConferir = (!funcoes.getValorXml("LimiteDiasConferir").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? Integer.parseInt(funcoes.getValorXml("LimiteDiasConferir")) : 7;
            int codigoEmpresa = (!funcoes.getValorXml("CodigoEmpresa").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? Integer.parseInt(funcoes.getValorXml("CodigoEmpresa")) : 1;
            String sql = "SELECT \n" +
                    "    AEAROMAN.ID_AEAROMAN, " +
                    "    CFAAREAS.ID_CFAAREAS, " +
                    "    CFAAREAS.CODIGO, " +
                    "    CFAAREAS.DESCRICAO, " +
                    "    AEAROMAN.GUID, " +
                    "    AEAROMAN.DT_ALT, " +
                    "    AEAROMAN.NUMERO, " +
                    "    AEAROMAN.DT_ROMANEIO, " +
                    "    AEAROMAN.DT_EMISSAO, " +
                    "    AEAROMAN.DT_SAIDA, " +
                    "    AEAROMAN.DT_FECHAMENTO, " +
                    "    AEAROMAN.VALOR, " +
                    "    AEAROMAN.OBS, " +
                    "    AEAROMAN.SITUACAO " +
                    "    FROM AEAROMAN " +
                    "    LEFT OUTER JOIN CFAAREAS ON (AEAROMAN.ID_CFAAREAS = CFAAREAS.ID_CFAAREAS) " +
                    "    WHERE " +
                    "      (AEAROMAN.ID_SMAEMPRE = " + codigoEmpresa +") AND " +
                    "      (AEAROMAN.DT_ROMANEIO >= (SELECT DATEADD(DAY, -" +limiteDiasConferir + ", CURRENT_DATE) FROM RDB$DATABASE)) ";

            if (conferidoSemConferir == SEM_CONFERIR) {
                sql += " AND (AEAROMAN.SITUACAO IS NULL) ";

            } else if (conferidoSemConferir == CONFERIDO){
                sql += " AND (AEAROMAN.SITUACAO IS NOT NULL) ";
            }

            if (where != null) {
                sql += " AND (" + where + ")";
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
    }
}
