package com.saga.banco.externo.funcoesSql;

import android.content.Context;

import com.saga.banco.externo.ConexaoBancoDeDadosExterno;
import com.saga.funcoes.FuncoesPersonalizadas;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by Bruno Nogueira Silva on 15/01/2016.
 */
public class FuncoesExternasSql {

    private String tabela;
    private FuncoesPersonalizadas funcoes;
    private Context context;
    private Connection conexaoBanco;
    private Statement statementBanco;
    private String usuario, senha;


    public FuncoesExternasSql(Context context, String tabela, String usuario, String senha) {
        this.context = context;
        this.tabela = tabela;
        this.usuario = usuario;
        this.senha = senha;

        try {
            ConexaoBancoDeDadosExterno conexaoExterna = new ConexaoBancoDeDadosExterno(context, usuario, senha);
            conexaoBanco = conexaoExterna.conectaBanco();
        } catch (Exception e){

        }
    }


    public ResultSet query(String sql){
        ResultSet resultado = null;
        try {
            statementBanco = conexaoBanco.createStatement();
            resultado = statementBanco.executeQuery(sql);
        } catch (Exception e){

        }
        return resultado;
    }
}
