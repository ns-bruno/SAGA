package com.saga.funcoes;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;
import com.saga.R;
import com.saga.banco.interno.ConexaoBancoDeDadosInterno;

import org.jasypt.util.text.BasicTextEncryptor;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Scanner;
import java.util.UUID;

/**
 * Created by Bruno Nogueira Silva on 15/01/2016.
 */
public class FuncoesPersonalizadas {

    private Context context;
    private static final String CHAVE_UNICA = "117ff1e4cfcafb0370b3517042bf90c9";
    public static final String NAO_ENCONTRADO = "nao encontrado";
    public static final String ENVIAR_ORCAMENTO_SAGA = "ENVIAR_ORCAMENTO_SAGA";
    public static final String RECEBER_DADOS_SAGA = "RECEBER_DADOS_SAGA";
    public static final String ENVIAR_OUTROS_DADOS_SAGA = "ENVIAR_OUTROS_DADOS_SAGA";
    private static final int RAPIDO = 2;
    String TAG = "SAGA";
    private int orientacaoTela;

    public FuncoesPersonalizadas(Context context) {
        super();
        //menssagem = new AlertDialog.Builder(context);
        this.context = context;
    }

    /**
     * Criptografa qualquer texto.
     * @param textoPleno - Tem que ser passado uma String com o valor dentro para ser criptografado.
     * @return
     */
    public String criptografaSenha(String textoPleno)  {

        String textoCrip = criptyDescripty(0, textoPleno, null);

        return textoCrip;
    }


    /**
     * Descriptografa qualquer texto.
     * @param textoCriptografado - Tem que ser passado uma String com o valor dentro para ser descriptografado.
     * @return
     */
    public String descriptografaSenha(String textoCriptografado)  {

        String textoPleno = criptyDescripty(1, null, textoCriptografado);

        return textoPleno;
    }

    private String criptyDescripty(int tipo, String senhaPlena, String textoCriptografado){

        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(CHAVE_UNICA);
        String myEncryptedText = textEncryptor.encrypt(senhaPlena);
        String plainText = textEncryptor.decrypt(textoCriptografado);

        // Checa se o tipo eh para criptografar
        if(tipo == 0){
            return myEncryptedText;
        } else if(tipo == 1){
            return plainText;
        } else {
            return "";
        }
    }


    /**
     * Funcao para salvar um valor em um xml na pasta padrao da aplicacao.
     * @param campo - Nome do campos que eh para ser salvo.
     * @param texto - Valor do campo, o conteudo.
     */
    public void setValorXml(String campo, String texto) {

        // Cria ou abre.
        SharedPreferences prefs = this.context.getSharedPreferences("prefSaga_1", Context.MODE_PRIVATE);

        // Precisamos utilizar um editor para alterar Shared Preferences.
        SharedPreferences.Editor ed = prefs.edit();

        // salvando informações de acordo com o tipo
        ed.putString(campo, texto);

        // Grava efetivamente as alterações.
        ed.commit();
    } // Fim do salvarXml

    /**
     * Funcao para pegar o valor de um determinado campo no arquivo XML.
     * Este XML esta localizado na pasta padrão da aplicacao
     *
     * @param campo - Nome do campos que esta salvo no arquivo XML
     * @return - Retorna uma String com o valor do campos
     */
    public String getValorXml(String campo) {
        // Acesso às Shared Preferences usando o nome definido.
        SharedPreferences prefs = this.context.getSharedPreferences("prefSaga_1", Context.MODE_PRIVATE);

        // Acesso às informações de acordo com o tipo.
        String texto = prefs.getString(campo, NAO_ENCONTRADO);

        if (texto.length() <= 0){
            texto = NAO_ENCONTRADO;
        }
        // Formata um string com todo o conteúdo separado por linha.
        return texto;
    } // FIm do selecionaValorXml

    public String tratamentoErroBancoDados(String erro){

        // Tratamento para erro de registro unico
        if(erro.toLowerCase().contains("code 2067")){
            erro = context.getResources().getString(R.string.erro_sqlite_code_2067) + "\n" + erro + "\n";
        }
        // Tratamento de erro para restrincoes em trigger
        if(erro.toLowerCase().contains("code 1811")){
            erro = context.getResources().getString(R.string.erro_sqlite_code_1811) + "\n" + erro + "\n";
        }
        // Tratamento de erro para campo obrigatorio (not null)
        if(erro.toLowerCase().contains("code 1299")){
            erro = context.getResources().getString(R.string.erro_sqlite_code_1299) + "\n" + erro + "\n";
        }

        if ((erro.toLowerCase().contains("no such table")) || (erro.toLowerCase().contains("no such column"))){
            erro = context.getResources().getString(R.string.nao_existe_tabela_banco_dados)  + "\n" + erro + "\n" + context.getResources().getString(R.string.vamos_executar_processo_criar_tabelas);
            try {
                ConexaoBancoDeDadosInterno conexaoBancoDeDados = new ConexaoBancoDeDadosInterno(context, VersionUtils.getVersionCode(context));
                // Pega o banco de dados do SAVARE
                SQLiteDatabase bancoDados = conexaoBancoDeDados.abrirBanco();
                // Executa o onCreate para criar todas as tabelas do banco de dados
                conexaoBancoDeDados.onCreate(bancoDados);

            } catch (PackageManager.NameNotFoundException e) {
                erro = e.getMessage() + "\n" + erro;
            }
        }

        if ((erro.toLowerCase().contains("failed to connect")) && (erro.toLowerCase().contains("connection refused"))){
            erro = context.getResources().getString(R.string.servidor_offline_por_isso_nao_conseguimos_conectar) + "\n" + erro + "\n";
        }

        if (erro.toLowerCase().contains("timeoutexception")){
            erro = context.getResources().getString(R.string.demorou_demais_servidor_webservice_responter_internet_lenta) + "\n" + erro + "\n";
        }

        return erro;
    } // Fim tratamentoErroBancoDados


    public void criarAlarmeEnviarReceberDadosAutomatico(Boolean ativarEnvio, Boolean ativarRecebimento){

        // Checa se o alarme nao foi criado
        boolean alarmeEnviarDesativado =  (PendingIntent.getBroadcast(context, 0, new Intent(ENVIAR_ORCAMENTO_SAGA), PendingIntent.FLAG_NO_CREATE) == null);
        boolean alarmeReceberDesativado = (PendingIntent.getBroadcast(context, 0, new Intent(RECEBER_DADOS_SAGA), PendingIntent.FLAG_NO_CREATE) == null);
        boolean alarmeEnviarOutrosDesativado = (PendingIntent.getBroadcast(context, 0, new Intent(ENVIAR_OUTROS_DADOS_SAGA), PendingIntent.FLAG_NO_CREATE) == null);

        // Checa se esta configurado para enviar os orcamentos automaticos
        if( ((getValorXml("EnviarAutomatico").equalsIgnoreCase("S")) || (getValorXml("EnviarAutomatico") == null) || (ativarEnvio == true)) &&
                (!getValorXml("ModoConexao").equalsIgnoreCase("S"))){

            // Checa se o alarme de envio de orcamento esta desativado
            if(alarmeEnviarDesativado){
                Log.i(TAG, "Novo alarme Enviar");

                // Cria a intent com identificacao do alarme
                Intent intent = new Intent(ENVIAR_ORCAMENTO_SAGA);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

                Calendar tempoInicio = Calendar.getInstance();
                // Pega a hora atual do sistema em milesegundo
                tempoInicio.setTimeInMillis(System.currentTimeMillis());
                // Adiciona mais alguns segundo para executar o alarme depois de alguns segundo que esta Activity for abaerta
                tempoInicio.add(Calendar.SECOND, 10);
                // Cria um intervalo de quanto em quanto tempo o alarme vai repetir
                long intervalo = 120 * 1000; // 2 Minutos

                AlarmManager alarmeEnviarOrcamento = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmeEnviarOrcamento.setRepeating(AlarmManager.RTC_WAKEUP, tempoInicio.getTimeInMillis(), intervalo, alarmIntent);
            }

            // Checa se o alarme de envio de outros dados qualquers esta desativado
            if (alarmeEnviarOutrosDesativado){
                Log.i(TAG, "Novo alarme Enviar Outros Dados");

                // Cria a intent com identificacao do alarme
                Intent intent = new Intent(ENVIAR_OUTROS_DADOS_SAGA);
                PendingIntent alarmIntentOutros = PendingIntent.getBroadcast(context, 0, intent, 0);

                Calendar tempoInicio = Calendar.getInstance();
                // Pega a hora atual do sistema em milesegundo
                tempoInicio.setTimeInMillis(System.currentTimeMillis());
                // Adiciona mais alguns segundo para executar o alarme depois de alguns segundo que esta Activity for abaerta
                tempoInicio.add(Calendar.SECOND, 20);
                // Cria um intervalo de quanto em quanto tempo o alarme vai repetir
                long intervalo = 180 * 1000; // 3 Minutos

                AlarmManager alarmeEnviarOrcamento = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmeEnviarOrcamento.setRepeating(AlarmManager.RTC_WAKEUP, tempoInicio.getTimeInMillis(), intervalo, alarmIntentOutros);

            } else if (ativarEnvio == false){
                Intent intentCancelar = new Intent(ENVIAR_ORCAMENTO_SAGA);
                PendingIntent alarmeIntent = PendingIntent.getBroadcast(context, 0, intentCancelar, 0);

                AlarmManager alarme = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarme.cancel(alarmeIntent);
                Log.i(TAG, "Desativado alarme Enviar");
            }
        } else {
            // Checa se o alarme foi cria para enviar a desativacao
            if((!alarmeEnviarDesativado) || (ativarEnvio == false)){
                Intent intentCancelar = new Intent(ENVIAR_ORCAMENTO_SAGA);
                PendingIntent alarmeIntent = PendingIntent.getBroadcast(context, 0, intentCancelar, 0);

                AlarmManager alarme = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarme.cancel(alarmeIntent);
                Log.i(TAG, "Desativado alarme Enviar");
            }
        }

        // Checa se esta configurado para receber dados automaticos
        if( ((getValorXml("ReceberAutomatico").equalsIgnoreCase("S")) || (getValorXml("ReceberAutomatico") == null) || (ativarRecebimento == true)) &&
                (!getValorXml("ModoConexao").equalsIgnoreCase("S"))){

            // Checa se o alarme de recebimento de dados esta desativado
            if(alarmeReceberDesativado){
                Log.i(TAG, "Novo alarme Receber");

                Intent intent = new Intent(RECEBER_DADOS_SAGA);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

                Calendar tempoInicio = Calendar.getInstance();
                // Pega a hora atual do sistema em milesegundo
                tempoInicio.setTimeInMillis(System.currentTimeMillis());
                // Adiciona mais alguns segundo para executar o alarme depois de alguns segundo que esta Activity for abaerta
                tempoInicio.add(Calendar.SECOND, 10);

                long intervalo = 60 * 1000; // 1 Minutos

                AlarmManager alarmeReceberOrcamento = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmeReceberOrcamento.setRepeating(AlarmManager.RTC_WAKEUP, tempoInicio.getTimeInMillis(), intervalo, alarmIntent);

            } else if (ativarRecebimento == false){
                Log.i(TAG, "Desativado alarme Receber");

                Intent intentCancelar = new Intent(RECEBER_DADOS_SAGA);
                PendingIntent alarmeIntent = PendingIntent.getBroadcast(context, 0, intentCancelar, 0);

                AlarmManager alarme = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarme.cancel(alarmeIntent);
            }
        } else {
            // Checa se o alarme foi criado para podermos desativalo
            if((!alarmeReceberDesativado) || (ativarRecebimento == false)){
                Log.i(TAG, "Desativado alarme Receber");
                Intent intentCancelar = new Intent(RECEBER_DADOS_SAGA);
                PendingIntent alarmeIntent = PendingIntent.getBroadcast(context, 0, intentCancelar, 0);

                AlarmManager alarme = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarme.cancel(alarmeIntent);
            }
        }
    }


    public void menssagem(ContentValues dados){

        if (dados.getAsInteger("comando") == RAPIDO){

            Toast.makeText(context, dados.getAsString("mensagem"), Toast.LENGTH_SHORT).show();

        } else {

            new AlertDialogWrapper.Builder(context)
                    .setTitle(dados.getAsString("tela"))
                    .setMessage(dados.getAsString("mensagem"))
                    .setNegativeButton(R.string.fechar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton(R.string.enviar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }
    }

    /**
     * Funcao para arrendondar os valores apos a virgula e
     * retornar um valor em formato padrao.
     * @param valor
     * @return - Retorna uma String com tres casas decimais.
     */
    public String arredondarValor(Double valor){
        String str = String.valueOf(valor);

        return valorFormatado(str);
    }
    /**
     * Funcao para arrendondar os valores apos a virgula e
     * retornar um valor em formato padrao.
     * @param valor
     * @return - Retorna uma String com tres casas decimais.
     */
    public String arredondarValor(String valor){
        return valorFormatado(valor);
    }
    /**
     * Funcao para arrendondar os valores apos a virgula e
     * retornar um valor em formato padrao.
     * @param valor
     * @return - Retorna uma String com tres casas decimais.
     */
    public String arredondarValor(int valor){
        String str = String.valueOf(valor);

        return valorFormatado(str);
    }
    /**
     * Funcao para arrendondar os valores apos a virgula e
     * retornar um valor em formato padrao.
     * @param valor
     * @return - Retorna uma String com tres casas decimais.
     */
    public String arredondarValor(long valor){
        String str = String.valueOf(valor);

        return valorFormatado(str);
    }


    private String valorFormatado(String valor){
        String retorno = null;
        try {
            //Converte a string em double
            double vlDouble = Double.parseDouble(valor);

            //Pega o valor e faz arredondamento com tres casas decimais
            BigDecimal valorFinal = new BigDecimal(vlDouble).setScale(3, BigDecimal.ROUND_HALF_EVEN);

            //Crica uma vareavel colo o local
            Locale localPtBr = new Locale("pt", "BR");
            NumberFormat formatarNumero = NumberFormat.getInstance(localPtBr);

            int qtdCasasDecimais;

            // Checa se tem algum parametro de casas decimais
            if ((getValorXml("CasasDecimais") != null) && (getValorXml("CasasDecimais").length() > 0) && (!getValorXml("CasasDecimais").equalsIgnoreCase(NAO_ENCONTRADO))){
                qtdCasasDecimais = Integer.valueOf(getValorXml("CasasDecimais"));
            }else {
                qtdCasasDecimais = 3;
            }

            // Informa que os casas decimais eh com minimo de 3 casas
            formatarNumero.setMinimumFractionDigits(qtdCasasDecimais);
            formatarNumero.setMinimumIntegerDigits(1);

            retorno = formatarNumero.format(valorFinal.doubleValue());

        } catch (Exception e) {

            try {
                valor = valor.replace(".", "").replace(",", "");
                double vlDouble = Double.parseDouble(valor) / 1000;
                //Pega o valor e faz arredondamento com tres casas decimais
                BigDecimal valorFinal = new BigDecimal(vlDouble).setScale(3, BigDecimal.ROUND_HALF_EVEN);
                retorno = String.valueOf(valorFinal.doubleValue());

            } catch (Exception e2) {
                retorno = "0.000";
            }

        }
        return retorno;
    }


    /**
     * Tirar a formatacao do padrao brasileiro de numeros.
     * Por exemplo:
     * R$ 1.025,35 retorna 1025.35
     * 1.025,30 retorna 1025.3
     *
     * @param valor
     * @return
     */
    public double desformatarValor(String valor){
        double valorDesformatado = 0;

        try {
            //Crica uma vareavel colo o local
            Locale localPtBr = new Locale("pt", "BR");
            NumberFormat formatarNumero = NumberFormat.getInstance(localPtBr);
            // Informa que os casas decimais eh com minimo de 3 casas
            formatarNumero.setMinimumFractionDigits(3);
            formatarNumero.setMinimumIntegerDigits(1);

            valorDesformatado = formatarNumero.parse(valor).doubleValue();

        } catch (Exception e) {
            e.getMessage();
        }
        return valorDesformatado;
    }

    public void bloqueiaOrientacaoTela() {
        // Pega a orientacao atual da tela
        orientacaoTela = context.getResources().getConfiguration().orientation;

        // Checa em qual orientacao esta atualmente para bloquear
        if (orientacaoTela == Configuration.ORIENTATION_PORTRAIT) {

            ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    public void desbloqueiaOrientacaoTela() {
        if (orientacaoTela > 0) {
            ((Activity) context).setRequestedOrientation(orientacaoTela);
        } else {
            ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    } // Fim desbloqueiaOrientacaoTela


    /**
     * Formata data e a hora de acordo com o padrão brasileiro dd/MM/yyyy.
     * Tem que ser passado por parametro o seguinte formato:
     * yyyy-MM-dd hh:mm:ss
     *
     * @param dataHora - yyyy-MM-dd hh:mm:ss
     * @return - dd/MM/yyyy hh:mm:ss
     */
    public String formataDataHora(String dataHora){
        String dataFormatada = "";

        try{
            if(dataHora != null){
                Scanner scannerParametro = new Scanner(dataHora.replace(" ", "-").replace(":", "-").replace(".", "-")).useDelimiter("\\-");
                int ano = scannerParametro.nextInt();
                int mes = scannerParametro.nextInt();
                int dia = scannerParametro.nextInt();
                int hora = scannerParametro.nextInt();
                int minuto = scannerParametro.nextInt();
                int segundo = scannerParametro.nextInt();

                // Instancia a classe de calendario
                Calendar calendario = Calendar.getInstance();
                calendario.set(ano, mes -1, dia, hora, minuto, segundo);
                // Cria um formato para data e hora
                DateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                // Salva a data e hora passada por parametro com o novo formato
                dataFormatada = dataFormat.format(calendario.getTime());
            }
        }catch(Exception e){
            String s = e.getMessage();
            int i = s.length();
        }
        return dataFormatada;
    }


    /**
     * Formata apenas a data de acordo com o padr�o brasileiro dd/MM/yyyy.
     * Tem que ser passado por parametro a data no seguinte formato:
     * yyyy-MM-dd.
     *
     * @param data - yyyy-MM-dd
     * @return dd/MM/yyyy
     */
    public String formataData(String data){
        String dataFormatada = "";

        if(data != null){
            Scanner scannerParametro = new Scanner(data).useDelimiter("\\-");
            int ano = scannerParametro.nextInt();
            int mes = scannerParametro.nextInt();
            int dia = scannerParametro.nextInt();

            Calendar calendario = Calendar.getInstance();
            calendario.set(ano, mes -1, dia);

            DateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");

            dataFormatada = dataFormat.format(calendario.getTime());
        }
        return dataFormatada;
    }


    /**
     * Tira o formato brasileiro da data e hora.
     * Tem que ser passado por paramento no formato brasileiro dd/MM/yyyy hh:mm:ss
     * Retorna o seguinte formato:
     * yyyy-MM-dd hh:mm:ss
     *
     * @param dataHora dd/MM/yyyy hh:mm:ss (06/04/2015 08:32:15)
     * @return - yyyy-MM-dd hh:mm:ss
     */
    public String desformataDataHora(String dataHora){
        String dataFormatada = "";

        if(dataHora != null){
            Scanner scannerParametro = new Scanner(dataHora).useDelimiter("\\/");

            int dia = scannerParametro.nextInt();
            int mes = scannerParametro.nextInt();
            int ano = scannerParametro.nextInt();
            // Inseri um novo delimitador para pegar a hora, minuto e segundo
            scannerParametro.useDelimiter("\\:");
            int hora = scannerParametro.nextInt();
            int minuto = scannerParametro.nextInt();
            int segundo = scannerParametro.nextInt();

            // Instancia a classe de calendario
            Calendar calendario = Calendar.getInstance();
            calendario.set(ano, mes -1, dia, hora, minuto, segundo);
        }
        DateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        dataFormatada = dataFormat.format(dataHora);

        return dataFormatada;
    }


    public String desformataData(String dataHora){
        String dataFormatada = "";

        if(dataHora != null){
            Scanner scannerParametro = new Scanner(dataHora).useDelimiter("\\/");

            int dia = scannerParametro.nextInt();
            int mes = scannerParametro.nextInt();
            int ano = scannerParametro.nextInt();

            // Instancia a classe de calendario
            Calendar calendario = Calendar.getInstance();
            calendario.set(ano, mes -1, dia);
        }

        DateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");

        dataFormatada = dataFormat.format(dataHora);

        return dataFormatada;
    } // desformataData


    public String geraGuid(int tamanho){
        if (tamanho > 0){
            return UUID.randomUUID().toString().replace("-", "").toUpperCase().substring(0, tamanho);
        }else {
            return  UUID.randomUUID().toString();
        }
    }

    public static void fecharTecladoVirtual(View activity){
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getWindowToken(), 0);
    }

    public boolean isValidarCodigoBarraGS1(String codigoBarras){
        boolean apenasNumeros = true;

        // Passa por todos os caracter checando se eh apenas numero
        for (char digito : codigoBarras.toCharArray()) {
            // Checa se eh um numero
            if (!Character.isDigit(digito)) {
                apenasNumeros = false;
                break;
            }
        }
        // Checa se o que foi passado por parametro eh apenas numero
        if (apenasNumeros){
            // Salva o ultimo digito do codigo (digito verificador)
            int digito = Integer.parseInt(""+codigoBarras.charAt(codigoBarras.length() -1));
            // Pega todos os digetos mas sem o digito verificador
            String codigoSemDigitoVerificador = codigoBarras.substring(0, codigoBarras.length() -1);
            // Vareavel para armazenar a soma dos digitos
            int soma = 0;

            // Checa a quantidade de digito
            if ( (codigoBarras.length() == 13) || (codigoBarras.length() == 17) ){

                // Passa por todos os digitos do codigo sem o digito verificador
                for(int i = 0; i < codigoSemDigitoVerificador.length(); i++){
                    // Checa se i eh par
                    if (( (i+1) % 2) == 0) {
                        // Mutiplica por 3 e depois soma
                        soma += Integer.parseInt(""+codigoSemDigitoVerificador.charAt(i)) * 3;
                    } else {
                        // Soma os digitos
                        soma += Integer.parseInt(""+codigoSemDigitoVerificador.charAt(i));
                    }
                }
            } else if ( (codigoBarras.length() == 8) || (codigoBarras.length() == 12) || (codigoBarras.length() == 14) || (codigoBarras.length() == 18) ){
                // Passa por todos os digitos do codigo sem o digito verificador
                for(int i = 0; i < codigoSemDigitoVerificador.length(); i++){

                    // Checa se i eh par
                    if (( (i+1) % 2) == 0) {
                        // Soma os digitos
                        soma += Integer.parseInt(""+codigoSemDigitoVerificador.charAt(i));
                    } else {
                        // Mutiplica por 3 e depois soma
                        soma += Integer.parseInt(""+codigoSemDigitoVerificador.charAt(i)) * 3;
                    }
                }
            // Returna falso caso nao seja um codigo de barra do tamanho valido pelo GS1
            } else {
                return false;
            }
            int somaMultipla = soma;

            // Entra no while enquanto a soma nao for multiplo de 10
            while ( (somaMultipla % 10) != 0 ){
                somaMultipla ++;
            }
            // Subtraia soma por um múltiplo de 10 superior mais próximo a ele
            // Depois checa se o resultado da subtracao eh igual ao digito passado por paramento
            return (soma - somaMultipla) == digito;

        } else {
            return false;
        }
    }
}
