package com.saga.banco.interno;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.saga.funcoes.FuncoesPersonalizadas;

import java.io.File;
import java.io.IOException;

/**
 * Created by Bruno Nogueira Silva on 15/01/2016.
 */
public class ConexaoBancoDeDadosInterno extends SQLiteOpenHelper {

    private static final String SQL_DIR = "sql" ;

    private static final String CREATEFILE = "create.sql";

    private static final String UPGRADEFILE_PREFIX = "upgrade-";

    private static final String UPGRADEFILE_SUFFIX = ".sql";

    private static String NOME_BANCO = "DadosSaga.db";
    private static String PATH_BANCO = Environment.getExternalStorageDirectory() + "/SAGA/BancoDeDados/";
    private static String TAG = "SAGA";
    private SQLiteDatabase bancoSaga;
    private Context context;


    public ConexaoBancoDeDadosInterno(Context context, int versao) {
        super(context, PATH_BANCO + NOME_BANCO, null, versao);
        new File(Environment.getExternalStorageDirectory().toString() + "/SAGA/BancoDeDados").mkdirs();
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "create database");
        try {
            //bd.execSQL(SQL_TABELAS[i]);
            execSqlFile(CREATEFILE, db);

        } catch (SQLException e) {
            // Armazena as informacoes para para serem exibidas e enviadas
            ContentValues contentValues = new ContentValues();
            contentValues.put("comando", 0);
            contentValues.put("tela", "ConexaoBancoDeDados");
            contentValues.put("mensagem", "Não foi possível criar as tabelas do banco de dados. \n" + e.getMessage());
            contentValues.put("dados", e.toString());
            // Pega os dados do usuario
            FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas( this.context);
            contentValues.put("usuario", funcoes.getValorXml("Usuario"));
            contentValues.put("empresa", funcoes.getValorXml("ChaveEmpresa"));
            contentValues.put("email", funcoes.getValorXml("Email"));

            funcoes.menssagem(contentValues);

        } catch (IOException e) {
            //e.printStackTrace();
            // Armazena as informacoes para para serem exibidas e enviadas
            ContentValues contentValues = new ContentValues();
            contentValues.put("comando", 0);
            contentValues.put("tela", "ConexaoBancoDeDados");
            contentValues.put("mensagem", "Erro ao tentar pegar o arquivo com as instruções SQL. \n" + e.getMessage());
            contentValues.put("dados", e.toString());
            // Pega os dados do usuario
            FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas( this.context);
            contentValues.put("usuario", funcoes.getValorXml("Usuario"));
            contentValues.put("empresa", funcoes.getValorXml("ChaveEmpresa"));
            contentValues.put("email", funcoes.getValorXml("Email"));

            funcoes.menssagem(contentValues);
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Funcao responsavel por abrir o banco de dados
     *
     * @return
     */
    public SQLiteDatabase abrirBanco() {
        Log.i(TAG, "abrirBanco");

        String bancoDados = PATH_BANCO + NOME_BANCO;
        //File s = context.getExternalFilesDir(null);

        if (bancoSaga == null || !bancoSaga.isOpen()) {
            bancoSaga = SQLiteDatabase.openDatabase(bancoDados, null, SQLiteDatabase.ENABLE_WRITE_AHEAD_LOGGING|
                                                                      SQLiteDatabase.NO_LOCALIZED_COLLATORS|
                                                                      SQLiteDatabase.CREATE_IF_NECESSARY);

            //bancoSaga = getWritableDatabase();
            //bancoSaga = SQLiteDatabase.openDatabase(bancoDados, null, 0);
            // bancoSaga = getWritableDatabase();
        }

        return bancoSaga;
    }

    /**
     * Funcao responsavel por fechar o banco de dados
     */
    public void fechar() {

        if (bancoSaga != null && bancoSaga.isOpen()) {
            bancoSaga.close();
        }
    }

    protected void execSqlFile(String sqlFile, SQLiteDatabase db ) throws SQLException, IOException {
        //log.info("  exec sql file: {}" + sqlFile);
        Log.i("SAVARE", "Executar o SqlFile.");
        for(String sqlInstruction : SqlParser.parseSqlFile(SQL_DIR + "/" + sqlFile, this.context.getAssets())) {
            // Executa a instrucao sql
            db.execSQL(sqlInstruction);
        }
    }
}
