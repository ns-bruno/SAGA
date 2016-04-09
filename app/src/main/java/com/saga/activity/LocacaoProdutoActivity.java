package com.saga.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.saga.R;
import com.saga.adapter.ItemUniversalAdapter;
import com.saga.beans.EstoqueBeans;
import com.saga.beans.ProdutoLojaBeans;
import com.saga.funcoes.FuncoesPersonalizadas;
import com.saga.funcoes.rotinas.EstoqueRotinas;
import com.saga.funcoes.rotinas.ProdutoRotinas;

import java.util.List;

/**
 * Created by Faturamento on 30/03/2016.
 */
public class LocacaoProdutoActivity extends AppCompatActivity {

    private Toolbar totoolbarRodape;
    private EditText editTextPesquisar;
    private EditText editTextLocacaoReserva;
    private EditText editTextLocacaoAtiva;
    private Spinner spinnerEstoque;
    private TextView textCodigoBarrasProduto;
    private TextView textEmbalagemPrincipal;
    private TextView textMarca;
    private TextView textReferencia;
    private TextView textCodigo;
    private TextView textDescricaoProduto;
    private TextView textMensagem;
    private ProgressBar progressStatus;
    private Toolbar toolbarCabecalho;
    private LinearLayout linearLayoutPrincipal;
    private ItemUniversalAdapter adapterEstoque;
    public static final int REQUISICAO_DADOS_PRODUTOS = 100,
                            PRODUTO_RETORNADO_SUCESSO = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locacao_produto);

        recuperaCampos();

        editTextPesquisar.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    Log.d("SAGA", "enter_key_called - LocacaoProdutoActivity");

                    // Checa se tem alguma coisa digitada no campos
                    if (editTextPesquisar.getText().length() > 0) {

                        String whereProduto = "";
                        String textoPesquisa = editTextPesquisar.getText().toString().replace(" ", "%");
                        Intent intent = new Intent(LocacaoProdutoActivity.this, ListaProdutoActivity.class);

                        // Checa se eh uma letra
                        if (Character.isLetter(editTextPesquisar.getText().charAt(1))) {

                            whereProduto += "(AEAPRODU.DESCRICAO LIKE '%" + textoPesquisa + "%') OR (AEAMARCA.DESCRICAO LIKE '%" + textoPesquisa + "%' ) OR " +
                                            "(AEAPRODU.REFERENCIA LIKE '%" + textoPesquisa + "%') OR ((SELECT COUNT(*) FROM AEAEMBAL " +
                                            "WHERE (AEAEMBAL.ID_AEAPRODU = AEAPRODU.ID_AEAPRODU) AND (AEAEMBAL.REFERENCIA LIKE '%" + textoPesquisa + "%')) > 0)";

                            // Checo se eh um codigo interno ou estrutural
                        } else if (editTextPesquisar.getText().toString().length() < 8) {

                            boolean apenasNumeros = true;

                            for (char digito : editTextPesquisar.getText().toString().toCharArray()) {
                                // Checa se eh um numero
                                if (!Character.isDigit(digito)) {
                                    apenasNumeros = false;
                                }
                            }
                            if (apenasNumeros) {

                                whereProduto += "(AEAPRODU.CODIGO = " + editTextPesquisar.getText().toString() + ") OR (AEAPRODU.CODIGO_ESTRUTURAL = '" + editTextPesquisar.getText().toString() + "') " +
                                                "OR (AEAPRODU.REFERENCIA = '" + editTextPesquisar.getText().toString() + "') ";

                            } else {
                                whereProduto += "(AEAPRODU.DESCRICAO LIKE '%" + textoPesquisa + "%') OR (AEAMARCA.DESCRICAO LIKE '%" + textoPesquisa + "%' ) OR " +
                                        "(AEAPRODU.REFERENCIA LIKE '%" + textoPesquisa + "%')";
                            }

                        } else if (editTextPesquisar.getText().toString().length() >= 8) {
                            boolean apenasNumeros = true;

                            for (char digito : editTextPesquisar.getText().toString().toCharArray()) {
                                // Checa se eh um numero
                                if (!Character.isDigit(digito)) {
                                    apenasNumeros = false;
                                }
                            }
                            if (apenasNumeros) {
                                whereProduto += "(AEAPRODU.CODIGO_BARRAS = '" + editTextPesquisar.getText().toString() + "') OR (AEAPRODU.REFERENCIA = '" + editTextPesquisar.getText().toString() + "') " +
                                        "OR ((SELECT COUNT(*) FROM AEAEMBAL " +
                                        "WHERE (AEAEMBAL.ID_AEAPRODU = AEAPRODU.ID_AEAPRODU) AND (AEAEMBAL.CODIGO_BARRAS = '" + editTextPesquisar.getText().toString() + "')) > 0) ";
                                //"OR (AEAEMBAL.CODIGO_BARRAS = '" + editTextPesquisar.getText().toString() + "')";
                            }

                        } else {
                            whereProduto += "(AEAPRODU.DESCRICAO LIKE '%" + textoPesquisa + "%') OR (AEAMARCA.DESCRICAO LIKE '%" + textoPesquisa + "%' ) OR " +
                                    "(AEAPRODU.REFERENCIA LIKE '%" + textoPesquisa + "%') OR " +
                                    "((SELECT COUNT(*) FROM AEAEMBAL \n" +
                                    "WHERE (AEAEMBAL.ID_AEAPRODU = AEAPRODU.ID_AEAPRODU) AND (AEAEMBAL.REFERENCIA LIKE '%" + textoPesquisa + "%')) > 0)) OR " +
                                    "(AEAPRODU.DESCRICAO_AUXILIAR LIKE '%" + textoPesquisa + "%') OR (AEAPRODU.CODIGO_BARRAS = '" + editTextPesquisar.getText().toString() + "') ";
                        }

                        intent.putExtra(ListaProdutoActivity.KEY_TELA_CHAMADA, ListaProdutoActivity.LOCACAO_PRODUTO_ACTIVITY);
                        intent.putExtra(ListaProdutoActivity.KEY_TEXTO_PESQUISA, editTextPesquisar.getText().toString());
                        intent.putExtra(ListaProdutoActivity.KEY_WHERE_PESQUISA, whereProduto);
                        // Abre a activity aquardando uma resposta
                        startActivityForResult(intent, REQUISICAO_DADOS_PRODUTOS);

                    } else {
                        Toast.makeText(LocacaoProdutoActivity.this, "Campos de pesquisa vazio.", Toast.LENGTH_LONG).show();
                    }
                }

                return false;
            }
        });

        spinnerEstoque.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                EstoqueBeans estoqueBeans = (EstoqueBeans) parent.getItemAtPosition(position);

                if (estoqueBeans != null) {
                    editTextLocacaoAtiva.setText((!estoqueBeans.getLocacaoAtiva().equalsIgnoreCase("anyType{}")) ? estoqueBeans.getLocacaoAtiva() : "");
                    editTextLocacaoReserva.setText((!estoqueBeans.getLocacaoReserva().equalsIgnoreCase("anyType{}")) ? estoqueBeans.getLocacaoReserva() : "");
                    // Posiciona o curso no final
                    editTextLocacaoAtiva.setSelection(editTextLocacaoAtiva.getText().length());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        editTextLocacaoAtiva.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    Log.d("SAGA", "enter_key_called - LocacaoProdutoActivity");

                    editTextLocacaoReserva.requestFocus();
                }
                return false;
            }
        });

        editTextLocacaoReserva.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    Log.d("SAGA", "enter_key_called - LocacaoProdutoActivity");

                    EstoqueBeans estoqueSpinner = (EstoqueBeans) spinnerEstoque.getItemAtPosition(spinnerEstoque.getSelectedItemPosition());

                    SalvarLocacao salvarLocacao = new SalvarLocacao(LocacaoProdutoActivity.this, editTextLocacaoAtiva.getText().toString(), editTextLocacaoReserva.getText().toString(), estoqueSpinner.getIdEstoque());
                    salvarLocacao.execute();
                }
                return false;
            }
        });

    } //Fim onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_locacao_produto, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:
                finish();
                break;

            case R.id.menu_activity_locacao_salvar:

                EstoqueBeans estoqueSpinner = (EstoqueBeans) spinnerEstoque.getItemAtPosition(spinnerEstoque.getSelectedItemPosition());

                SalvarLocacao salvarLocacao = new SalvarLocacao(LocacaoProdutoActivity.this, editTextLocacaoAtiva.getText().toString(), editTextLocacaoReserva.getText().toString(), estoqueSpinner.getIdEstoque());
                salvarLocacao.execute();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Checa a requisicao
        if (requestCode == REQUISICAO_DADOS_PRODUTOS){
            // Checa se retornou com sucesso
            if (resultCode == PRODUTO_RETORNADO_SUCESSO){
                // Pega o id passado por parametro
                int idProdutoLoja = (data.getExtras().getInt("ID_AEAPLOJA"));

                // Checa se retornou algum codigo de produto
                if (idProdutoLoja > 0){
                    LoaderDetalhesProdutos loaderDetalhesProdutos = new LoaderDetalhesProdutos(LocacaoProdutoActivity.this, idProdutoLoja);
                    loaderDetalhesProdutos.execute();
                }
            }
        }
    }

    private void recuperaCampos(){
        totoolbarRodape = (Toolbar) findViewById(R.id.activity_locacao_produto_toolbar_rodape);
        editTextPesquisar = (EditText) findViewById(R.id.activity_locacao_produto_editText_pesquisar);
        editTextLocacaoReserva = (EditText) findViewById(R.id.activity_locacao_produto_editText_locacao_passiva);
        editTextLocacaoAtiva = (EditText) findViewById(R.id.activity_locacao_produto_editText_locacao_ativa);
        spinnerEstoque = (Spinner) findViewById(R.id.activity_locacao_produto_spinner_estoque);
        textCodigoBarrasProduto = (TextView) findViewById(R.id.activity_locacao_produto_text_codigo_barras_produto);
        textEmbalagemPrincipal = (TextView) findViewById(R.id.activity_locacao_produto_text_embalagem_principal);
        textMarca = (TextView) findViewById(R.id.activity_locacao_produto_text_marca);
        textReferencia = (TextView) findViewById(R.id.activity_locacao_produto_text_referencia);
        textCodigo = (TextView) findViewById(R.id.activity_locacao_produto_text_codigo);
        textDescricaoProduto = (TextView) findViewById(R.id.activity_locacao_produto_text_descricao_produto);
        textMensagem = (TextView) findViewById(R.id.activity_locacao_produto_mensagem);
        progressStatus = (ProgressBar) findViewById(R.id.activity_locacao_produto_progress_status);

        toolbarCabecalho = (Toolbar) findViewById(R.id.activity_locacao_produto_toolbar_cabecalho);
        // Adiciona uma titulo para toolbar
        toolbarCabecalho.setTitle(this.getResources().getString(R.string.locacao_produto));
        toolbarCabecalho.setTitleTextColor(getResources().getColor(R.color.branco));
        // Seta uma toolBar para esta activiy(tela)
        setSupportActionBar(toolbarCabecalho);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        linearLayoutPrincipal = (LinearLayout) findViewById(R.id.activity_locacao_produto_linearLayoutPrincipal);
    }



    public class LoaderDetalhesProdutos extends AsyncTask<Void, Void, Void> {
        String where = "";
        ProdutoLojaBeans produtoLoja;
        private Context context;
        private int idProdutoLoja;
        private List<EstoqueBeans> listaEstoque;

        public LoaderDetalhesProdutos(Context context, int idProdutoLoja) {
            this.context = context;
            this.idProdutoLoja = idProdutoLoja;
        }

        // Aqui eh o que acontece antes da tarefa principal ser executado
        @Override
        protected void onPreExecute() {
            // o progressBar agora eh setado como visivel
            progressStatus.setVisibility(View.VISIBLE);
            linearLayoutPrincipal.setVisibility(View.VISIBLE);
            textMensagem.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {

            where += "AEAPLOJA.ID_AEAPLOJA = " + idProdutoLoja;

            ProdutoRotinas produtoRotinas = new ProdutoRotinas(context);
            List<ProdutoLojaBeans> listaProdutoLoja = produtoRotinas.selectListaProdutoLojaResumido(where, progressStatus);

            if (listaProdutoLoja != null && listaProdutoLoja.size() > 0) {
                produtoLoja = listaProdutoLoja.get(0);
            }

            if (produtoLoja != null){
                EstoqueRotinas estoqueRotinas = new EstoqueRotinas(context);

                where = "AEAESTOQ.ID_AEAPLOJA = " + idProdutoLoja;

                listaEstoque = estoqueRotinas.selectListaEstoque(where, progressStatus);

                if ((listaEstoque != null) && (listaEstoque.size() > 0)){

                    adapterEstoque = new ItemUniversalAdapter(context, ItemUniversalAdapter.LISTA_ESTOQUE);
                    adapterEstoque.setListaEstoque(listaEstoque);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            // Checa se a lista deta vazia
            if (produtoLoja != null) {

                textDescricaoProduto.setText(produtoLoja.getProduto().getDescricaoProduto());
                textCodigo.setText("Cod.: " + produtoLoja.getProduto().getCodigoEstrutural());
                textReferencia.setText("Ref.: " + produtoLoja.getProduto().getReferencia());
                textMarca.setText(produtoLoja.getProduto().getMarca().getDescricao());
                textEmbalagemPrincipal.setText("Emb.: " + produtoLoja.getProduto().getUnidadeVenda().getSigla());
                textCodigoBarrasProduto.setText("Cód. Bar. Produtos - " + produtoLoja.getProduto().getCodigoBarras());

                if ((adapterEstoque != null) && (adapterEstoque.getListaEstoque() != null)){

                    spinnerEstoque.setAdapter(adapterEstoque);
                    // Coloca o foco dentro do campo locacao ativa
                    editTextLocacaoAtiva.requestFocus();
                }
            } else {
                mensagemNaoEncontramos();
            }
            //tirando o ProgressBar da nossa tela
            progressStatus.setVisibility(View.GONE);

        }

        private void mensagemNaoEncontramos() {
            ((Activity) getApplicationContext()).runOnUiThread(new Runnable() {
                public void run() {
                    new MaterialDialog.Builder(getApplicationContext())
                            .title(R.string.produtos)
                            .content(R.string.nenhum_valor_encontrado)
                            .positiveText(android.R.string.ok)
                                    //.negativeText(R.string.disagree)
                            .autoDismiss(true)
                            .show();
                }
            });
        }

    }


    public class SalvarLocacao extends AsyncTask<Void, Void, Void> {
        private Context context;
        private String locacaoAtiva, locacaoReserva;
        private int idEstoque;
        private boolean atualizadoSucesso = false;

        public SalvarLocacao(Context context, String locacaoAtiva, String locacaoReserva, int idEstoque) {
            this.context = context;
            this.locacaoAtiva = locacaoAtiva;
            this.locacaoReserva = locacaoReserva;
            this.idEstoque = idEstoque;
        }

        // Aqui eh o que acontece antes da tarefa principal ser executado
        @Override
        protected void onPreExecute() {
            // o progressBar agora eh setado como visivel
            progressStatus.setVisibility(View.VISIBLE);
            progressStatus.setIndeterminate(true);
        }

        @Override
        protected Void doInBackground(Void... params) {

            EstoqueBeans estoqueBeans = new EstoqueBeans();
            //estoqueBeans.setIdEstoque(estoqueSpinner.getIdEstoque());
            estoqueBeans.setLocacaoAtiva((locacaoAtiva.length() > 0) ? locacaoAtiva : "null");
            estoqueBeans.setLocacaoReserva((locacaoReserva.length() > 0) ? locacaoReserva : "null");

            String where = "AEAESTOQ.ID_AEAESTOQ = " + idEstoque;

            EstoqueRotinas estoqueRotinas = new EstoqueRotinas(LocacaoProdutoActivity.this);

            atualizadoSucesso = estoqueRotinas.updateLocacaoEstoque(estoqueBeans, where, progressStatus, null);

            return null;
        } // Fim doInBackground

        @Override
        protected void onPostExecute(Void result) {
            final ContentValues contentValues = new ContentValues();
            contentValues.put("comando", 2);

            if (atualizadoSucesso){
                linearLayoutPrincipal.setVisibility(View.INVISIBLE);
                textMensagem.setVisibility(View.VISIBLE);
                // Limpa o campo de pesquisa
                editTextPesquisar.setText("");

                contentValues.put("mensagem", context.getResources().getString(R.string.atualizado_sucesso));

                MediaPlayer somSucesso = MediaPlayer.create(context, R.raw.effect_alert_positive);
                somSucesso.start();

            } else {
                contentValues.put("mensagem", context.getResources().getString(R.string.nao_conseguimos_atualizar_locacao));

                MediaPlayer somErro = MediaPlayer.create(context, R.raw.effect_alert_error);
                somErro.start();

                final FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

                ((Activity) context).runOnUiThread(new Runnable() {
                    public void run() {
                        // Executa uma mensagem rapida
                        funcoes.menssagem(contentValues);
                    }
                });
            }
            progressStatus.setVisibility(View.GONE);
        }

    } // Fim SalvarLocacao

}
