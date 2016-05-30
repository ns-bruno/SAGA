package com.saga.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.saga.R;
import com.saga.adapter.ItemUniversalAdapter;
import com.saga.beans.AtivoBeans;
import com.saga.beans.EmbalagemBeans;
import com.saga.beans.ProdutoBeans;
import com.saga.beans.UnidadeVendaBeans;
import com.saga.funcoes.FuncoesPersonalizadas;
import com.saga.funcoes.rotinas.EmbalagemRotinas;
import com.saga.funcoes.rotinas.ProdutoRotinas;
import com.saga.funcoes.rotinas.UnidadeVendaRotinas;

import java.util.ArrayList;
import java.util.List;

import me.sudar.zxingorient.ZxingOrient;
import me.sudar.zxingorient.ZxingOrientResult;

/**
 * Created by Bruno Nogueira Silva on 10/02/2016.
 */
public class CadastroEmbalagemActivity extends AppCompatActivity {

    private Spinner spinnerUnidadeVenda, spinnerAtivo;
    private EditText editModulo, editCasasDecimais, editReferencia, editCodigoBarras, editDescricao;
    private Toolbar toolbarCabecalho;
    private TextView textStatus;
    private ProgressBar progressBarStatus;
    private Button buttonEscanearCodigoBarraProduto;
    public static final String KEY_ID_PRODUTO = "ID_AEAPRODU",
                               KEY_ID_AEAEMBAL = "ID_AEAEMBAL";
    private int idProduto = -1;
    private int idEmbalagem = -1;
    private List<UnidadeVendaBeans> listaUnidadeVenda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_embalagem);

        recuperaCampos();

        Bundle intentParametro = getIntent().getExtras();
        if (intentParametro != null) {
            idProduto = intentParametro.getInt(KEY_ID_PRODUTO);
            idEmbalagem = intentParametro.getInt(KEY_ID_AEAEMBAL);
        }

        carregarAtivo();
        
        if (idProduto > 0){
            ProdutoRotinas produtoRotinas = new ProdutoRotinas(CadastroEmbalagemActivity.this);
            toolbarCabecalho.setTitle(produtoRotinas.getDescricaoProduto(idProduto));
        }


        CarregarListas carregarListas = new CarregarListas(CadastroEmbalagemActivity.this);
        carregarListas.execute();

        buttonEscanearCodigoBarraProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (idProduto > 0) {
                    new ZxingOrient(CadastroEmbalagemActivity.this)
                            .setInfo(getResources().getString(R.string.escanear_codigo_produto))
                            .setVibration(true)
                            .setIcon(R.mipmap.ic_launcher)
                            .initiateScan();

                } else {
                    Toast.makeText(CadastroEmbalagemActivity.this, "Não conseguimos achar o codigo do produto.", Toast.LENGTH_LONG).show();
                }
            }
        });

    } // Fim onCreate


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ZxingOrientResult retornoEscanerCodigoBarra = ZxingOrient.parseActivityResult(requestCode, resultCode, data);

        if(retornoEscanerCodigoBarra != null) {
            // Checha se retornou algum dado
            if(retornoEscanerCodigoBarra.getContents() == null) {
                Log.d("SAGA", "Cancelled scan - CadastroEmbalagemActivity");
                Toast.makeText(this, "Cancelado", Toast.LENGTH_LONG).show();

            } else {
                Log.d("SAGA", "Scanned - CadastroEmbalagemActivity");

                editCodigoBarras.setText(retornoEscanerCodigoBarra.getContents());
                // Posiciona o cursor para o final do texto
                editCodigoBarras.setSelection(editCodigoBarras.length());
            }
        } else {
            // This is important, otherwise the retornoEscanerCodigoBarra will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    } // Fim onActivityResult

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_cadastro_embalagem, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:
                finish();
                break;

            case R.id.menu_activity_cadastro_embalagem_salvar:

                if ( (validaDados()) && (idEmbalagem > 0) ){

                    UnidadeVendaBeans unidadeVenda = (UnidadeVendaBeans) spinnerUnidadeVenda.getSelectedItem();
                    AtivoBeans ativo = (AtivoBeans) spinnerAtivo.getSelectedItem();

                    EmbalagemBeans embalagem = new EmbalagemBeans();
                    embalagem.setIdUnidadeVenda(unidadeVenda.getIdUnidadeVenda());
                    embalagem.setReferencia(editReferencia.getText().toString());
                    embalagem.setCodigoBarras(editCodigoBarras.getText().toString());
                    embalagem.setAtivo(ativo.getSimNao());
                    embalagem.setDecimais((!editCasasDecimais.getText().toString().equals("")) ? Integer.parseInt(editCasasDecimais.getText().toString()) : 0);
                    embalagem.setDescricaoEmbalagem(editDescricao.getText().toString());
                    embalagem.setModulo((!editModulo.getText().toString().equals("")) ? Integer.parseInt(editModulo.getText().toString()) : 1);

                    // Executa uma rotina em background. Esta rotina esta logo abaixo.
                    ExecutarInsertBack executarInsertBack = new ExecutarInsertBack(CadastroEmbalagemActivity.this, embalagem, idEmbalagem);
                    executarInsertBack.execute();


                } else if ( (validaDados()) && (idProduto > 0) ){


                    UnidadeVendaBeans unidadeVenda = (UnidadeVendaBeans) spinnerUnidadeVenda.getSelectedItem();
                    AtivoBeans ativo = (AtivoBeans) spinnerAtivo.getSelectedItem();

                    EmbalagemBeans embalagem = new EmbalagemBeans();
                    embalagem.setIdUnidadeVenda(unidadeVenda.getIdUnidadeVenda());
                    embalagem.setUnidadeVenda(unidadeVenda);
                    embalagem.setIdProduto(idProduto);
                    embalagem.setReferencia(editReferencia.getText().toString());
                    embalagem.setCodigoBarras(editCodigoBarras.getText().toString());
                    embalagem.setAtivo(ativo.getSimNao());
                    embalagem.setDecimais((!editCasasDecimais.getText().toString().equals("")) ? Integer.parseInt(editCasasDecimais.getText().toString()) : 0);
                    embalagem.setDescricaoEmbalagem(editDescricao.getText().toString());
                    embalagem.setModulo((!editModulo.getText().toString().equals("")) ? Integer.parseInt(editModulo.getText().toString()) : 1);

                    // Executa uma rotina em background. Esta rotina esta logo abaixo.
                    ExecutarInsertBack executarInsertBack = new ExecutarInsertBack(CadastroEmbalagemActivity.this, embalagem, -1);
                    executarInsertBack.execute();

                }

                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void recuperaCampos(){
        spinnerUnidadeVenda = (Spinner) findViewById(R.id.activity_cadastro_embalagem_spinner_unidade_venda);
        spinnerAtivo = (Spinner) findViewById(R.id.activity_cadastro_embalagem_spinner_ativo);
        editModulo = (EditText) findViewById(R.id.activity_cadastro_embalagem_edit_modulo);
        editCasasDecimais = (EditText) findViewById(R.id.activity_cadastro_embalagem_edit_casas_decimais);
        editReferencia = (EditText) findViewById(R.id.activity_cadastro_embalagem_edit_referencia);
        editCodigoBarras = (EditText) findViewById(R.id.activity_cadastro_embalagem_edit_codigo_barras);
        editDescricao = (EditText) findViewById(R.id.activity_cadastro_embalagem_edit_descricao);
        textStatus = (TextView) findViewById(R.id.activity_cadastro_embalagem_text_status);
        progressBarStatus = (ProgressBar) findViewById(R.id.activity_cadastro_embalagem_progressBar_status);
        buttonEscanearCodigoBarraProduto = (Button) findViewById(R.id.activity_cadastro_embalagem_button_escanear_codigo_barra_produto);

        toolbarCabecalho = (Toolbar) findViewById(R.id.activity_cadastro_embalagem_toolbar_cabecalho);
        toolbarCabecalho.setTitle(this.getResources().getString(R.string.app_name));
        toolbarCabecalho.setTitleTextColor(getResources().getColor(R.color.branco));
        // Seta uma toolBar para esta activiy(tela)
        setSupportActionBar(toolbarCabecalho);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    private void carregarAtivo(){
        List<AtivoBeans> listaAtivo = new ArrayList<AtivoBeans>();
        AtivoBeans ativo = new AtivoBeans();
        ativo.setSimNao("0");
        listaAtivo.add(ativo);
        ativo = new AtivoBeans();
        ativo.setSimNao("1");
        listaAtivo.add(ativo);

        ItemUniversalAdapter adapterAtivo = new ItemUniversalAdapter(CadastroEmbalagemActivity.this, ItemUniversalAdapter.ATIVO);
        adapterAtivo.setListaAtivo(listaAtivo);

        spinnerAtivo.setAdapter(adapterAtivo);
    }

    private boolean validaDados(){
        boolean retorno = true;

        if (editCodigoBarras.getText().length() > 0){
            final FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(CadastroEmbalagemActivity.this);

            if (!funcoes.isValidarCodigoBarraGS1(editCodigoBarras.getText().toString())){

                final ContentValues contentValues = new ContentValues();
                contentValues.put("comando", 2);
                contentValues.put("tela", "CadastroEmbalagemActivity");
                contentValues.put("mensagem", getResources().getString(R.string.codigo_barras_invalido));

                ((Activity) CadastroEmbalagemActivity.this).runOnUiThread(new Runnable() {
                    public void run() {
                        funcoes.menssagem(contentValues);
                    }
                });

                retorno = false;
            }
        }

        return retorno;
    }


    public class ExecutarInsertBack extends AsyncTask<Void, Void, Void> {

        EmbalagemBeans embalagem;
        Context context;
        boolean inseriuComSucesso = false;
        private int idEmbalagem;

        public ExecutarInsertBack(Context context, EmbalagemBeans embalagem, int idEmbalagem) {
            this.embalagem = embalagem;
            this.context = context;
            this.idEmbalagem = idEmbalagem;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Mostra a barra de status
            progressBarStatus.setVisibility(View.VISIBLE);
            textStatus.setVisibility(View.VISIBLE);
            textStatus.setText(getResources().getString(R.string.espere_mais_pouco_vamos_conectar_servidor));

        }

        @Override
        protected Void doInBackground(Void... params) {

            //WSSisInfoWebservice wsSisInfoWebservice = new WSSisInfoWebservice(CadastroEmbalagemActivity.this);
            //retorno = wsSisInfoWebservice.inserirEmbalagemProduto(embalagem);
            EmbalagemRotinas embalagemRotinas = new EmbalagemRotinas(context);

            if (idEmbalagem > 0){
                // Executa a rotina para atualizar a embalagem
                inseriuComSucesso = embalagemRotinas.updateEmbalagem(embalagem, "AEAEMBAL.ID_AEAEMBAL = " + idEmbalagem, progressBarStatus, textStatus);

            } else {
                inseriuComSucesso = embalagemRotinas.insertEmbalagem(embalagem, progressBarStatus, textStatus);
            }
            return null;
        }

        protected void onPostExecute(Void feed) {
            // Oculata a barra de status
            progressBarStatus.setVisibility(View.GONE);
            textStatus.setVisibility(View.GONE);

            // Checa se teve sucesso
            if (inseriuComSucesso){
                finish();
            }
        }
    }


    public class CarregarListas extends AsyncTask<Void, Void, Void> {

        Context context;
        EmbalagemBeans embalagem;
        ProdutoBeans produto;

        public CarregarListas(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Mostra a barra de status
            progressBarStatus.setVisibility(View.VISIBLE);
            textStatus.setVisibility(View.VISIBLE);
            textStatus.setText(getResources().getString(R.string.espere_mais_pouco_vamos_conectar_servidor));
        }

        @Override
        protected Void doInBackground(Void... params) {

            ProdutoRotinas produtoRotinas = new ProdutoRotinas(CadastroEmbalagemActivity.this);

            produto = produtoRotinas.selectProdutoResumidoId(idProduto, null);

            UnidadeVendaRotinas unidadeVendaRotinas = new UnidadeVendaRotinas(context);

            listaUnidadeVenda = unidadeVendaRotinas.listaUnidadeVenda(null);

            if (idEmbalagem > 0){
                EmbalagemRotinas embalagemRotinas = new EmbalagemRotinas(CadastroEmbalagemActivity.this);

                embalagem = embalagemRotinas.listaEmbalagemProduto(idProduto, "AEAEMBAL.ID_AEAEMBAL = " + idEmbalagem, null).get(0);
            }

            return null;
        }

        protected void onPostExecute(Void feed) {

            if ((listaUnidadeVenda != null) && (listaUnidadeVenda.size() > 0)) {

                ItemUniversalAdapter adapterUnidadeVenda = new ItemUniversalAdapter(context, ItemUniversalAdapter.UNIDADE_VENDA);
                adapterUnidadeVenda.setListaUnidadeVenda(listaUnidadeVenda);

                spinnerUnidadeVenda.setAdapter(adapterUnidadeVenda);
            }

            if ((embalagem != null)){
                editModulo.setText(""+embalagem.getModulo());
                editCasasDecimais.setText(""+embalagem.getDecimais());
                editReferencia.setText(embalagem.getReferencia());
                editCodigoBarras.setText((!embalagem.getCodigoBarras().equalsIgnoreCase("anyType{}")) ? embalagem.getCodigoBarras() : "");
                editDescricao.setText(embalagem.getDescricaoEmbalagem());

                if (listaUnidadeVenda != null && listaUnidadeVenda.size() > 0) {
                    for (int i = 0; i < listaUnidadeVenda.size(); i++) {

                        if (listaUnidadeVenda.get(i).getIdUnidadeVenda() == embalagem.getUnidadeVenda().getIdUnidadeVenda()) {
                            spinnerUnidadeVenda.setSelection(i);
                            break;
                        }
                    }
                }

                if (embalagem.getAtivo().equalsIgnoreCase("0")){
                    spinnerAtivo.setSelection(0);
                } else {
                    spinnerAtivo.setSelection(1);
                }
            }
            // Muda o titulo da tela
            toolbarCabecalho.setTitle(""+((produto != null) ? produto.getDescricaoProduto() : getResources().getString(R.string.nao_achamos_descricao)));

            // Oculata a barra de status
            progressBarStatus.setVisibility(View.GONE);
            textStatus.setVisibility(View.GONE);
        }
    }
}
