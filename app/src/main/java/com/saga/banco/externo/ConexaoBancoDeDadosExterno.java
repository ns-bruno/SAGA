package com.saga.banco.externo;

import android.content.Context;

import com.saga.funcoes.FuncoesPersonalizadas;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Bruno Nogueira Silva on 15/01/2016.
 */
public class ConexaoBancoDeDadosExterno {

    private String driverLinux = "jdbc:firebirdsql";
    private String driverFirebird = "org.firebirdsql.jdbc.FBDriver";
    private String IP_Servidor;
    private String porta;
    private String nome_banco; // = "c:\\SisInfo\\delphi\\SINOVO.FIR";
    private String urlFirebird;
    private String usuario = "";
    private String senha = "";
    private String tipoConexao = null;
    private Context context;

    public static final String FIREBIRD = "0";

    public ConexaoBancoDeDadosExterno(Context context, String usuario, String senha) {
        this.context = context;
        this.usuario = usuario;
        this.senha = senha;
        // Pega as configuracoes salvar
        restauraDadosConexao();
    }

    /**
     * @conctaBancoDados Cria a conexão com banco de dados, caso occorra de não
     * localizar o driver ou o banco, será mostrado um erro.
     */
    public Connection conectaBanco() {
        Connection iniciaConexao = null;
        try {
            if ((tipoConexao != null) && (tipoConexao.equalsIgnoreCase(FIREBIRD))) {
                Class.forName(driverFirebird);
                iniciaConexao = DriverManager.getConnection(urlFirebird, usuario, senha);
            }
            //JOptionPane.showMessageDialog(null, "Conectado no Banco de dados " + nome_banco, "Banco de dados", JOptionPane.INFORMATION_MESSAGE);
            return iniciaConexao;

        } catch (ClassNotFoundException erroSQL) {
            //JOptionPane.showMessageDialog(null, "Não foi localizado o Banco de Dados \n" + erroSQL + "\n" + url + "\n" + erroSQL.getMessage(), "Erro Banco de dados", JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (SQLException erroSQL) {
            //JOptionPane.showMessageDialog(null, "Erro após conectar no banco de dados \n" + erroSQL + "\n" + url, "Erro Banco de dados", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void restauraDadosConexao(){
        FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

        IP_Servidor = funcoes.getValorXml("IPServidor");
        porta = funcoes.getValorXml("PortaServidor");
        nome_banco = funcoes.getValorXml("NomeBancoDadosServidor");
        tipoConexao = funcoes.getValorXml("TipoConexao");

        // Checa se retornou alguma coisa
        IP_Servidor = ((IP_Servidor != null) && (IP_Servidor.length() > 0)) ? IP_Servidor : "localhost";
        porta = ((porta != null) && (porta.length() > 0)) ? porta : "3050";
        nome_banco = ((nome_banco != null) && (nome_banco.length() > 0)) ? nome_banco : "c:\\SisInfo\\SI.FIR";
        tipoConexao = ((tipoConexao != null) && (tipoConexao.length() > 0)) ? tipoConexao : null;


        urlFirebird = "jdbc:firebirdsql:" + IP_Servidor + "/" + porta + ":" + nome_banco;
    }
}
