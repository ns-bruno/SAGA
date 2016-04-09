package com.saga.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.saga.R;
import com.saga.funcoes.FuncoesPersonalizadas;

/**
 * Created by Bruno Nogueira Silva on 11/03/2016.
 */
public class ConfiguracaoActivity extends AppCompatActivity {

    private EditText editIPServidor, editIPServidorWebservice, editPortaServidor, editCaminhoBanco,
    editUsuarioServidor, editSenhaUsuarioServidor, editCertificado, editCasasDecimais, editLimiteDiasConferir,
    editCodigoEmpresa;
    private RadioGroup radioGroupTipoConexao;
    private RadioButton radioConexaoInterna,
                        radioConexaoWebservice,
                        radioConexaoFtp;
    private Button buttonTamanhoFonte;
    private Toolbar toolbarCabecalho;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracao);

        restauraCampos();

        restaurarDadosCampo();

        buttonTamanhoFonte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cria um dialog para selecionar atacado ou varejo
                AlertDialog.Builder mensagemAtacadoVarejo = new AlertDialog.Builder(ConfiguracaoActivity.this);
                // Atributo(variavel) para escolher o tipo da venda
                final String[] opcao = {"Normal", "Médio", "Grande"};
                // Preenche o dialogo com o titulo e as opcoes
                mensagemAtacadoVarejo.setTitle("Tamanho dos Textos").setItems(opcao, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(ConfiguracaoActivity.this);

                        // Checa qual opcao foi selecionado
                        if (which == 0) {
                            // Salva N para fontes normais
                            funcoes.setValorXml("TamanhoFonte", "N");
                            buttonTamanhoFonte.setText("Tamanho do Texto: Normal");
                            buttonTamanhoFonte.setTextSize(13);

                        } else if (which == 1) {
                            // Salva M para fontes Medias
                            funcoes.setValorXml("TamanhoFonte", "M");
                            buttonTamanhoFonte.setText("Tamanho do Texto: Médio");
                            buttonTamanhoFonte.setTextSize(16);

                        } else if (which == 2) {
                            // Salva G para fontes Grandes
                            funcoes.setValorXml("TamanhoFonte", "G");
                            buttonTamanhoFonte.setText("Tamanho do Texto: Grande");
                            buttonTamanhoFonte.setTextSize(21);
                        }

                    }
                });

                // Faz a mensagem (dialog) aparecer
                mensagemAtacadoVarejo.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_configuracao, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;

            case R.id.menu_activity_configuracao_salvar:
                salvarDadosCampo();
                finish();
                break;

            case R.id.menu_activity_configuracao_fechar:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void restauraCampos(){
        editIPServidor = (EditText) findViewById(R.id.activity_configuracao_edit_ip_servidor);
        editIPServidorWebservice = (EditText) findViewById(R.id.activity_configuracao_edit_ip_servidor_webservice);
        editPortaServidor = (EditText) findViewById(R.id.activity_configuracao_edit_porta_servidor);
        editCaminhoBanco = (EditText) findViewById(R.id.activity_configuracao_edit_caminho_banco_dados);
        editUsuarioServidor = (EditText) findViewById(R.id.activity_configuracao_edit_usuario_servidor);
        editSenhaUsuarioServidor = (EditText) findViewById(R.id.activity_configuracao_edit_senha_usuario_servidor);
        editCertificado = (EditText) findViewById(R.id.activity_configuracao_edit_certificado);
        editCasasDecimais = (EditText) findViewById(R.id.activity_configuracao_edit_casas_decimais);
        editLimiteDiasConferir = (EditText) findViewById(R.id.activity_configuracao_edit_limite_dias_conferir);
        editCodigoEmpresa = (EditText) findViewById(R.id.activity_configuracao_edit_codigo_empresa);
        radioGroupTipoConexao = (RadioGroup) findViewById(R.id.activity_configuracao_radio_group_tipo_conexao);
        radioConexaoInterna = (RadioButton) findViewById(R.id.activity_configuracao_radioButton_conexao_interna);
        radioConexaoWebservice = (RadioButton) findViewById(R.id.activity_configuracao_radioButton_conexao_webservice);
        radioConexaoFtp = (RadioButton) findViewById(R.id.activity_configuracao_radioButton_conexao_ftp);
        buttonTamanhoFonte = (Button) findViewById(R.id.activity_configuracao_button_tamanho_fonte);
        toolbarCabecalho = (Toolbar) findViewById(R.id.activity_configuracao_toolbar_cabecalho);
        toolbarCabecalho.setTitle(this.getResources().getString(R.string.configuracoes));
        toolbarCabecalho.setTitleTextColor(getResources().getColor(R.color.branco));
        // Seta uma toolBar para esta activiy(tela)
        setSupportActionBar(toolbarCabecalho);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void salvarDadosCampo(){

        try{
            FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(ConfiguracaoActivity.this);

            funcoes.setValorXml("IPServidor", editIPServidor.getText().toString());
            funcoes.setValorXml("IPServidorWebservice", editIPServidorWebservice.getText().toString());
            funcoes.setValorXml("PortaServidor", editPortaServidor.getText().toString());
            funcoes.setValorXml("NomeBancoDadosServidor", editCaminhoBanco.getText().toString());
            funcoes.setValorXml("UsuarioServidor", editUsuarioServidor.getText().toString());
            funcoes.setValorXml("SenhaServidor", editSenhaUsuarioServidor.getText().toString());
            funcoes.setValorXml("Certificado", editCertificado.getText().toString());
            funcoes.setValorXml("LimiteDiasConferir", editLimiteDiasConferir.getText().toString());
            funcoes.setValorXml("CodigoEmpresa", editCodigoEmpresa.getText().toString());
            funcoes.setValorXml("CasasDecimais", editCasasDecimais.getText().toString());

            if (radioGroupTipoConexao.getCheckedRadioButtonId() == R.id.activity_configuracao_radioButton_conexao_interna) {
                funcoes.setValorXml("TipoConexao", "I");
            } else if (radioGroupTipoConexao.getCheckedRadioButtonId() == R.id.activity_configuracao_radioButton_conexao_webservice) {
                funcoes.setValorXml("TipoConexao", "W");
            } else if (radioGroupTipoConexao.getCheckedRadioButtonId() == R.id.activity_configuracao_radioButton_conexao_ftp){
                funcoes.setValorXml("TipoConexao", "F");
            }

            if (funcoes.getValorXml("EnviarAutomatico").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) {
                funcoes.setValorXml("EnviarAutomatico", "S");
            }
            if (funcoes.getValorXml("ReceberAutomatico").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) {
                funcoes.setValorXml("ReceberAutomatico", "S");
            }
            // Armazena as informacoes para para serem exibidas e enviadas
            ContentValues contentValues = new ContentValues();
            contentValues.put("comando", 2);
            contentValues.put("mensagem", "Conseguimos salvar tudo.");

            funcoes.menssagem(contentValues);

        } catch (Exception e){
            FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(ConfiguracaoActivity.this);

            // Armazena as informacoes para para serem exibidas e enviadas
            ContentValues contentValues = new ContentValues();
            contentValues.put("comando", 0);
            contentValues.put("tela", "ConfiguracaoActivity");
            contentValues.put("mensagem", getResources().getString(R.string.nao_conseguimos_salvar_configuracoes) + funcoes.tratamentoErroBancoDados(e.getMessage()));
            contentValues.put("dados", e.toString());
            // Pega os dados do usuario

            contentValues.put("usuario", funcoes.getValorXml("Usuario"));
            contentValues.put("empresa", funcoes.getValorXml("ChaveEmpresa"));
            contentValues.put("email", funcoes.getValorXml("Email"));

            funcoes.menssagem(contentValues);

        }
    }

    private void restaurarDadosCampo(){
        try{
            FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(ConfiguracaoActivity.this);

            editIPServidor.setText((!funcoes.getValorXml("IPServidor").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? funcoes.getValorXml("IPServidor") : "");
            editIPServidorWebservice.setText((!funcoes.getValorXml("IPServidorWebservice").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? funcoes.getValorXml("IPServidorWebservice") : "");
            editPortaServidor.setText((!funcoes.getValorXml("PortaServidor").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? funcoes.getValorXml("PortaServidor") : "");
            editCaminhoBanco.setText((!funcoes.getValorXml("NomeBancoDadosServidor").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? funcoes.getValorXml("NomeBancoDadosServidor") : "");
            editUsuarioServidor.setText((!funcoes.getValorXml("UsuarioServidor").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? funcoes.getValorXml("UsuarioServidor") : "");
            editSenhaUsuarioServidor.setText((!funcoes.getValorXml("SenhaServidor").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? funcoes.getValorXml("SenhaServidor") : "");
            editCertificado.setText((!funcoes.getValorXml("Certificado").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? funcoes.getValorXml("Certificado") : "");
            editLimiteDiasConferir.setText((!funcoes.getValorXml("LimiteDiasConferir").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? funcoes.getValorXml("LimiteDiasConferir") : "");
            editCodigoEmpresa.setText((!funcoes.getValorXml("CodigoEmpresa").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? funcoes.getValorXml("CodigoEmpresa") : "");
            editCasasDecimais.setText((!funcoes.getValorXml("CasasDecimais").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? funcoes.getValorXml("CasasDecimais") : "");
            //editfuncoes.setValorXml("EnviarAutomatico", "S");
            //funcoes.setValorXml("ReceberAutomatico", "S");
            if (funcoes.getValorXml("TipoConexao").equalsIgnoreCase("I")){
                radioGroupTipoConexao.check(R.id.activity_configuracao_radioButton_conexao_interna);

            } else if (funcoes.getValorXml("TipoConexao").equalsIgnoreCase("W")){
                radioGroupTipoConexao.check(R.id.activity_configuracao_radioButton_conexao_webservice);

            } else if (funcoes.getValorXml("TipoConexao").equalsIgnoreCase("F")){
                radioGroupTipoConexao.check(R.id.activity_configuracao_radioButton_conexao_ftp);
            }
            // Checa o tamanho da fonte salvo
            if(funcoes.getValorXml("TamanhoFonte").equalsIgnoreCase("M")){
                buttonTamanhoFonte.setText("Tamanho do Texto: Médio");
                buttonTamanhoFonte.setTextSize(16);

            } else if(funcoes.getValorXml("TamanhoFonte").equalsIgnoreCase("G")){
                buttonTamanhoFonte.setText("Tamanho do Texto: Grande");
                buttonTamanhoFonte.setTextSize(21);

            } else {
                buttonTamanhoFonte.setText("Tamanho do Texto: Normal");
            }

        } catch (Exception e){
            FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(ConfiguracaoActivity.this);

            // Armazena as informacoes para para serem exibidas e enviadas
            ContentValues contentValues = new ContentValues();
            contentValues.put("comando", 0);
            contentValues.put("tela", "ConfiguracaoActivity");
            contentValues.put("mensagem", getResources().getString(R.string.nao_conseguimos_pegar_configuracoes) + "\n" + funcoes.tratamentoErroBancoDados(e.getMessage()));
            contentValues.put("dados", e.toString());
            // Pega os dados do usuario

            contentValues.put("usuario", funcoes.getValorXml("Usuario"));
            contentValues.put("empresa", funcoes.getValorXml("ChaveEmpresa"));
            contentValues.put("email", funcoes.getValorXml("Email"));

            funcoes.menssagem(contentValues);

        }
    }
}
