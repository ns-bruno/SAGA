package com.saga.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.saga.R;
import com.saga.beans.ConexaoFIrebirdBeans;
import com.saga.beans.RetornoWebServiceBeans;
import com.saga.funcoes.FuncoesPersonalizadas;
import com.saga.webservice.WSSisInfoWebservice;

import org.ksoap2.serialization.PropertyInfo;

/**
 * Created by Bruno Nogueira Silva on 15/01/2016.
 */
public class LoginActivity extends AppCompatActivity {

    public static final String KEY_USUARIO_SERVIDOR = "KEY_USUARIO_SERVIDOR",
                               KEY_SENHA_SERVIDOR = "KEY_SENHA_SERVIDOR";

    private EditText editUsuario, editSenha;
    private Button buttonEntrar;
    private Toolbar toolbarCabecalho;
    private Toolbar toolbarRodape;
    private ProgressBar progressBarStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Recupera os campos
        recuperarDadosTela();

        FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(LoginActivity.this);
        // Checa se o tipo de conexao eh por webservice
        if ((!funcoes.getValorXml("TipoConexao").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) && (funcoes.getValorXml("TipoConexao").equalsIgnoreCase("W"))) {
            // Pega o nome do usuario que estiver salvo na configuracao
            editUsuario.setText((!funcoes.getValorXml("UsuarioServidor").equalsIgnoreCase(funcoes.NAO_ENCONTRADO) ? funcoes.getValorXml("UsuarioServidor") : ""));
            // Posiciona o curso para o final
            editUsuario.setSelection(editUsuario.getText().length());
        }

        buttonEntrar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                entrarAplicacao();
            }
        });

        toolbarRodape.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.menu_activity_login_toolbar_configuracao) {
                    Intent intent = new Intent(LoginActivity.this, ConfiguracaoActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });

        editSenha.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    Log.d("SAGA", "enter_key_called");

                    entrarAplicacao();
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){



            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void entrarAplicacao(){

        FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(LoginActivity.this);
        if ((!funcoes.getValorXml("TipoConexao").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) && (funcoes.getValorXml("TipoConexao").equalsIgnoreCase("W"))){

            ChecarUsuarioSenha checarUsuarioSenha = new ChecarUsuarioSenha(editUsuario.getText().toString(), editSenha.getText().toString());
            checarUsuarioSenha.execute();

        } else {
            // Colocar aqui um checagem de usuario no sqlite
        }

        //Intent intent = new Intent(LoginActivity.this, InicioActivity.class);
        //startActivity(intent);

    }

    /**
     * Recupera os dados da tela da view
     * @return
     */
    private void recuperarDadosTela(){
        editUsuario = (EditText) findViewById(R.id.activity_login_edit_usuario);
        editSenha = (EditText) findViewById(R.id.activity_login_edit_senha);
        buttonEntrar = (Button) findViewById(R.id.activity_login_button_entrar);
        progressBarStatus = (ProgressBar) findViewById(R.id.activity_login_progressBar_status);
        toolbarCabecalho = (Toolbar) findViewById(R.id.activity_login_toolbar_cabecalho);
        // Adiciona uma titulo para toolbar
        toolbarCabecalho.setTitle(this.getResources().getString(R.string.app_name));
        toolbarCabecalho.setTitleTextColor(getResources().getColor(R.color.branco));
        // Seta uma toolBar para esta activiy(tela)
        setSupportActionBar(toolbarCabecalho);

        toolbarRodape = (Toolbar) findViewById(R.id.activity_login_toolbar_rodape);
        toolbarRodape.inflateMenu(R.menu.menu_activity_login);
    }

    public class ChecarUsuarioSenha extends AsyncTask<Void, Void, Void> {

        String usuario = "", senha = "";

        public ChecarUsuarioSenha(String usuario, String senha) {
            this.usuario = usuario;
            this.senha = senha;
        }

        // Aqui eh o que acontece antes da tarefa principal ser executado
        @Override
        protected void onPreExecute() {
            // o progressBar agora eh setado como visivel
            progressBarStatus.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {

            final FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(LoginActivity.this);

            if (usuario.length() >= 2 && senha.length() >= 1){
                ConexaoFIrebirdBeans conexaoFIrebirdBeans = new ConexaoFIrebirdBeans();
                conexaoFIrebirdBeans.setIPServidor(((!funcoes.getValorXml("IPServidor").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? funcoes.getValorXml("IPServidor") : "localhost"));
                conexaoFIrebirdBeans.setLocalBanco(((!funcoes.getValorXml("NomeBancoDadosServidor").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? funcoes.getValorXml("NomeBancoDadosServidor") : "C:\\si.fir"));
                conexaoFIrebirdBeans.setPorta((!funcoes.getValorXml("PortaServidor").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? funcoes.getValorXml("PortaServidor") : "3050");
                conexaoFIrebirdBeans.setUsuario(usuario);
                conexaoFIrebirdBeans.setSenha(senha);
                conexaoFIrebirdBeans.setCertificado((!funcoes.getValorXml("Certificado").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? funcoes.getValorXml("Certificado") : "");

                PropertyInfo propertyConexaoFirebird = new PropertyInfo();
                propertyConexaoFirebird.setName("dadosConexao");
                propertyConexaoFirebird.setValue(conexaoFIrebirdBeans);
                propertyConexaoFirebird.setType(conexaoFIrebirdBeans.getClass());

                WSSisInfoWebservice wsSisInfoWebservice = new WSSisInfoWebservice(LoginActivity.this);
                RetornoWebServiceBeans retorno = wsSisInfoWebservice.executarWebservice(propertyConexaoFirebird, WSSisInfoWebservice.FUNCTION_CHECA_USUARIO_SENHA);

                // Checa se retornou alguma coisa
                if (retorno != null){

                    // Checa se o retorno equilave a usuario e senha corretos
                    if (retorno.getCodigoRetorno() == 103){

                        final ContentValues contentValues = new ContentValues();
                        contentValues.put("comando", 2);
                        contentValues.put("mensagem", "Login efetuado com sucesso.");

                        ((Activity) LoginActivity.this).runOnUiThread(new Runnable() {
                            public void run() {
                                funcoes.menssagem(contentValues);
                                funcoes.setValorXml("UsuarioServidor", editUsuario.getText().toString());
                                funcoes.setValorXml("SenhaServidor", editSenha.getText().toString());
                            }
                        });


                        Intent intent = new Intent(LoginActivity.this, InicioActivity.class);
                        startActivity(intent);

                    } else {
                        final ContentValues contentValues = new ContentValues();
                        contentValues.put("comando", 0);
                        contentValues.put("tela", "LoginActivity");
                        contentValues.put("mensagem", retorno.getCodigoRetorno() + "\n" + retorno.getMensagemRetorno());
                        contentValues.put("dados", retorno.getExtra().toString());
                        // Pega os dados do usuario

                        contentValues.put("usuario", funcoes.getValorXml("Usuario"));
                        contentValues.put("empresa", funcoes.getValorXml("ChaveEmpresa"));
                        contentValues.put("email", funcoes.getValorXml("Email"));

                        ((Activity) LoginActivity.this).runOnUiThread(new Runnable() {
                            public void run() {
                                funcoes.menssagem(contentValues);
                            }
                        });
                    }
                } else {
                    final ContentValues contentValues = new ContentValues();
                    contentValues.put("comando", 0);
                    contentValues.put("tela", "LoginActivity");
                    contentValues.put("mensagem", "O servidor retornou dados totalmente errados. Entre em contato com o administrador da T.I.");
                    contentValues.put("dados", (retorno != null) ? retorno.getExtra().toString() : "");
                    // Pega os dados do usuario

                    contentValues.put("usuario", funcoes.getValorXml("Usuario"));
                    contentValues.put("empresa", funcoes.getValorXml("ChaveEmpresa"));
                    contentValues.put("email", funcoes.getValorXml("Email"));

                    (LoginActivity.this).runOnUiThread(new Runnable() {
                        public void run() {
                            funcoes.menssagem(contentValues);
                        }
                    });
                }

            } else {
                // Armazena as informacoes para para serem exibidas e enviadas
                final ContentValues contentValues = new ContentValues();
                contentValues.put("comando", 2);
                contentValues.put("mensagem", "Digite usuário e a senha.");

                (LoginActivity.this).runOnUiThread(new Runnable() {
                    public void run() {
                        funcoes.menssagem(contentValues);
                    }
                });
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            editUsuario.setText("");
            editSenha.setText("");
            //tirando o ProgressBar da nossa tela
            progressBarStatus.setVisibility(View.GONE);

        }


    }
}
