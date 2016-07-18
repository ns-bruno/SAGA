package com.saga.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.saga.R;
import com.saga.adapter.ItemUniversalAdapter;
import com.saga.beans.EstoqueBeans;
import com.saga.beans.LocesBeans;
import com.saga.beans.ProdutoLojaBeans;
import com.saga.funcoes.FuncoesPersonalizadas;
import com.saga.funcoes.rotinas.EstoqueRotinas;
import com.saga.funcoes.rotinas.LocesRotinas;
import com.saga.funcoes.rotinas.ProdutoRotinas;

import java.util.ArrayList;
import java.util.List;

import me.sudar.zxingorient.ZxingOrient;
import me.sudar.zxingorient.ZxingOrientResult;

/**
 * Created by Bruno Nogueira Silva on 07/04/2016.
 */
public class LocacaoEnderecoActivity extends AppCompatActivity {

    private Toolbar toolbarCabecalho;
    private EditText editTextPesquisaProduto, editTextPesquisaLocacao;
    private TextView textTipoLocacao;
    private ListView listViewListaProduto;
    private Spinner spinnerEstoque;
    private Button buttonEscanearCodigoBarra;
    private Button buttonEscanearCodigoBarraProduto;
    private String tipoLocacao = null;
    private ProgressBar progressStatus;
    private ItemUniversalAdapter adapterListaProdutos, adapterListaLoces;
    private List<ProdutoLojaBeans> listaProdutoLojaSelecionado;
    private int totalItemSelecionado = 0;
    private final String CAMPO_LOCACAO = "CAMPO_LOCACAO", CAMPO_PRODUTO = "CAMPO_PRODUTO";
    private String campoQueChamouLeitor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locacao_endereco);

        recuperarCampos();

        editTextPesquisaLocacao.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {

                    // Executa um alert para selecionar o tipo de locacao
                    informarTipoLocacao();
                }

                return false;
            }
        });


        editTextPesquisaProduto.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // Checa se foir precionado o enter
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    Log.d("SAGA", "enter_key_called - LocacaoProdutoActivity");

                    pesquisarProduto();
                }
                return false;
            }
        });


        listViewListaProduto.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                // Checa se a lista de selecionado eh nula
                if (listaProdutoLojaSelecionado == null) {
                    listaProdutoLojaSelecionado = new ArrayList<ProdutoLojaBeans>();
                }
                // Checa se o comando eh de selecao ou descelecao
                if (checked) {
                    // Incrementa o totalizador
                    totalItemSelecionado = totalItemSelecionado + 1;
                    //listaItemOrcamentoSelecionado.add(listaItemOrcamento.get(position));
                    listaProdutoLojaSelecionado.add(adapterListaProdutos.getListaProdutoLoja().get(position));
                    // Mar o adapter para mudar a cor do fundo
                    adapterListaProdutos.getListaProdutoLoja().get(position).setTagSelectContext(true);
                    adapterListaProdutos.notifyDataSetChanged();

                } else {
                    int i = 0;
                    while (i < listaProdutoLojaSelecionado.size()) {

                        // Checar se a posicao desmarcada esta na lista
                        if (listaProdutoLojaSelecionado.get(i).getIdProdutoLoja() == adapterListaProdutos.getListaProdutoLoja().get(position).getIdProdutoLoja()) {
                            // Remove a posicao da lista de selecao
                            listaProdutoLojaSelecionado.remove(i);
                            // Diminui o total de itens selecionados
                            totalItemSelecionado = totalItemSelecionado - 1;

                            // Mar o adapter para mudar a cor do fundo
                            adapterListaProdutos.getListaProdutoLoja().get(position).setTagSelectContext(false);
                            adapterListaProdutos.notifyDataSetChanged();
                        }
                        // Incrementa a variavel
                        i++;
                    }
                }
                // Checa se tem mais de um item selecionados
                if (totalItemSelecionado > 1) {
                    // Muda o titulo do menu de contexto quando seleciona os itens
                    mode.setTitle(totalItemSelecionado + " Selecionados");
                } else {
                    // Muda o titulo do menu de contexto quando seleciona os itens
                    mode.setTitle(totalItemSelecionado + " Selecionado");
                }
                if (totalItemSelecionado == 0) {

                    onDestroyActionMode(mode);
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {

                MenuInflater menuContextual = mode.getMenuInflater();
                menuContextual.inflate(R.menu.menu_activity_locacao_endereco_contextual, menu);
                // Esconde a toolbar
                toolbarCabecalho.setVisibility(View.GONE);
                // Congela a lista de estoques
                spinnerEstoque.setEnabled(false);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.menu_activity_locacao_endereco_contextual_excluir_produto_locacao:

                        LocesBeans loces = (LocesBeans) spinnerEstoque.getItemAtPosition(spinnerEstoque.getSelectedItemPosition());

                        // Passa por todos os itens selecionados
                        for (int i = 0; i < listaProdutoLojaSelecionado.size(); i++) {
                            // Executa o salvar locacao (que no caso vai ficar nulo o endereco da locacao)
                            SalvarLocacao salvarLocacao = new SalvarLocacao(LocacaoEnderecoActivity.this, tipoLocacao, "null", listaProdutoLojaSelecionado.get(i).getIdProdutoLoja(), loces.getIdLoces());
                            salvarLocacao.execute();
                        }
                        break;

                    default:
                        break;
                }

                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // Passa por tota a lista de orcamento/pedido
                for (int i = 0; i < adapterListaProdutos.getListaProdutoLoja().size(); i++) {
                    // Mar o adapter para mudar a cor do fundo
                    adapterListaProdutos.getListaProdutoLoja().get(i).setTagSelectContext(false);
                }
                adapterListaProdutos.notifyDataSetChanged();
                listaProdutoLojaSelecionado = null;
                totalItemSelecionado = 0;

                toolbarCabecalho.setVisibility(View.VISIBLE);
                // Descongela a lista de estoques
                spinnerEstoque.setEnabled(true);
            }
        });

        buttonEscanearCodigoBarra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Marca qual eh o campo que esta chamando o leitor de codigo de barra
                campoQueChamouLeitor = CAMPO_LOCACAO;

                new ZxingOrient(LocacaoEnderecoActivity.this)
                        .setInfo(getResources().getString(R.string.escanear_edereco_locacao))
                        .setVibration(true)
                        .setIcon(R.mipmap.ic_launcher)
                        .initiateScan();

            }
        });

        buttonEscanearCodigoBarraProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editTextPesquisaLocacao.getText().length() > 0) {
                    // Marca qual eh o campo que esta chamando o leitor de codigo de barra
                    campoQueChamouLeitor = CAMPO_PRODUTO;

                    new ZxingOrient(LocacaoEnderecoActivity.this)
                            .setInfo(getResources().getString(R.string.escanear_edereco_locacao))
                            .setVibration(true)
                            .setIcon(R.mipmap.ic_launcher)
                            .initiateScan();

                } else {
                    SuperToast.create(LocacaoEnderecoActivity.this, getResources().getString(R.string.nao_foi_informado_nenhuma_locacao), SuperToast.Duration.LONG, Style.getStyle(Style.BLUE, SuperToast.Animations.POPUP)).show();
                }
            }
        });

        // Carrega a lista de estoques
        CarregarListaLoces carregarListaLoces = new CarregarListaLoces(LocacaoEnderecoActivity.this);
        carregarListaLoces.execute();

        FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(LocacaoEnderecoActivity.this);
        // Fecha o teclado virtual
        funcoes.fecharTecladoVirtual(getCurrentFocus());

    } // Fim onCreate

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //IntentResult retornoEscanerCodigoBarra = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        ZxingOrientResult retornoEscanerCodigoBarra = ZxingOrient.parseActivityResult(requestCode, resultCode, data);

        // Checa a requisicao
        if (requestCode == LocacaoProdutoActivity.REQUISICAO_DADOS_PRODUTOS){
            // Checa se retornou com sucesso
            if (resultCode == LocacaoProdutoActivity.PRODUTO_RETORNADO_SUCESSO){
                // Pega o id passado por parametro
                int idProdutoLoja = (data.getExtras().getInt("ID_AEAPLOJA"));

                // Checa se retornou algum codigo de produto
                if (idProdutoLoja > 0){

                    LocesBeans loces = (LocesBeans) spinnerEstoque.getItemAtPosition(spinnerEstoque.getSelectedItemPosition());

                    SalvarLocacao salvarLocacao = new SalvarLocacao(LocacaoEnderecoActivity.this, tipoLocacao, editTextPesquisaLocacao.getText().toString(), idProdutoLoja, loces.getIdLoces());
                    salvarLocacao.execute();
                }
            }
        }
        if(retornoEscanerCodigoBarra != null) {
            // Checa se retornou algum codigo
            if(retornoEscanerCodigoBarra.getContents() == null) {
                Log.d("SAGA", "Cancelled scan");

                SuperToast.create(LocacaoEnderecoActivity.this, getResources().getString(R.string.cancelado), SuperToast.Duration.LONG, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();

            } else {
                Log.d("SAGA", "Scanned - LocacaoEnderecoActivity");
                //Toast.makeText(this, "Scanned: " + retornoEscanerCodigoBarra.getContents(), Toast.LENGTH_LONG).show();

                if (campoQueChamouLeitor == CAMPO_LOCACAO){
                    // Seta o campo de pesquisa de locacao com os dados retornando da leitura
                    editTextPesquisaLocacao.setText(retornoEscanerCodigoBarra.getContents());

                    // Executa um alert para selecionar o tipo de locacao
                    informarTipoLocacao();

                } else if (campoQueChamouLeitor == CAMPO_PRODUTO){
                    // Preenche o campo de pesquisa de produto com os dados retornado do leitor de codigo de barra
                    editTextPesquisaProduto.setText(retornoEscanerCodigoBarra.getContents());

                    pesquisarProduto();
                }
            }
        } /*else {
            // This is important, otherwise the retornoEscanerCodigoBarra will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }*/
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void recuperarCampos(){
        editTextPesquisaProduto = (EditText) findViewById(R.id.activity_locacao_endereco_editText_pesquisa_produto);
        editTextPesquisaLocacao = (EditText) findViewById(R.id.activity_locacao_endereco_editText_pesquisar_locacao);
        textTipoLocacao = (TextView) findViewById(R.id.activity_locacao_endereco_text_tipo_locacao);
        spinnerEstoque = (Spinner) findViewById(R.id.activity_locacao_endereco_spinner_estoque);
        listViewListaProduto = (ListView) findViewById(R.id.activity_locacao_endereco_listView_lista_produto);
        listViewListaProduto.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        buttonEscanearCodigoBarra = (Button) findViewById(R.id.activity_locacao_endereco_button_escanear_codigo_barra);
        buttonEscanearCodigoBarraProduto = (Button) findViewById(R.id.activity_locacao_endereco_button_escanear_codigo_barra_produto);
        progressStatus = (ProgressBar) findViewById(R.id.activity_locacao_endereco_progressStatus);
        toolbarCabecalho = (Toolbar) findViewById(R.id.activity_locacaoendereco_toolbar_cabecalho);
        // Adiciona uma titulo para toolbar
        toolbarCabecalho.setTitle(this.getResources().getString(R.string.locacao_endereco));
        toolbarCabecalho.setTitleTextColor(getResources().getColor(R.color.branco));
        // Seta uma toolBar para esta activiy(tela)
        setSupportActionBar(toolbarCabecalho);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void informarTipoLocacao(){
        new AlertDialogWrapper.Builder(this)
                .setTitle(getResources().getString(R.string.tipo_locacao) + " | " + editTextPesquisaLocacao.getText().toString())
                .setMessage(R.string.favor_escolha_qual_tipo_dessa_locacao)
                .setPositiveButton(R.string.locacao_ativa, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Marca o tipo de locacao
                        textTipoLocacao.setText(getResources().getString(R.string.locacao_ativa));
                        tipoLocacao = "A";
                        // Muda o foco do cursor
                        editTextPesquisaProduto.requestFocus();

                        CarregarListaProdutosLocacao carregarListaProdutosLocacao = new CarregarListaProdutosLocacao(LocacaoEnderecoActivity.this, tipoLocacao, editTextPesquisaLocacao.getText().toString());
                        carregarListaProdutosLocacao.execute();

                        // Fecha o dialog
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.locacao_reserva, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Marca o tipo de locacao
                        textTipoLocacao.setText(getResources().getString(R.string.locacao_reserva));
                        tipoLocacao = "R";

                        // Muda o foco do cursor para o campo de pesquisa do produto
                        editTextPesquisaProduto.requestFocus();

                        // Carrega a lista de produtos da locacao
                        CarregarListaProdutosLocacao carregarListaProdutosLocacao = new CarregarListaProdutosLocacao(getApplicationContext(), tipoLocacao, editTextPesquisaLocacao.getText().toString());
                        carregarListaProdutosLocacao.execute();

                        // Fecha o dialog
                        dialog.dismiss();
                    }
                }).show();
        toolbarCabecalho.setTitle(editTextPesquisaLocacao.getText().toString());
    }

    private void pesquisarProduto(){
        // Checa se tem alguma coisa digitada no campos
        if (editTextPesquisaProduto.getText().length() > 0) {

            String whereProduto = "";
            String textoPesquisa = editTextPesquisaProduto.getText().toString().replace(" ", "%");
            Intent intent = new Intent(LocacaoEnderecoActivity.this, ListaProdutoActivity.class);

            // Checa se eh uma letra
            if (Character.isLetter(editTextPesquisaProduto.getText().charAt(1))) {

                whereProduto += "(AEAPRODU.DESCRICAO LIKE '%" + textoPesquisa + "%') OR (AEAMARCA.DESCRICAO LIKE '%" + textoPesquisa + "%' ) OR " +
                        "(AEAPRODU.REFERENCIA LIKE '%" + textoPesquisa + "%') OR ((SELECT COUNT(*) FROM AEAEMBAL " +
                        "WHERE (AEAEMBAL.ID_AEAPRODU = AEAPRODU.ID_AEAPRODU) AND (AEAEMBAL.REFERENCIA LIKE '%" + textoPesquisa + "%')) > 0)";

                // Checo se eh um codigo interno ou estrutural
            } else if (editTextPesquisaProduto.getText().toString().length() < 8) {

                boolean apenasNumeros = true;

                for (char digito : editTextPesquisaProduto.getText().toString().toCharArray()) {
                    // Checa se eh um numero
                    if (!Character.isDigit(digito)) {
                        apenasNumeros = false;
                    }
                }
                if (apenasNumeros) {

                    whereProduto += "(AEAPRODU.CODIGO = " + editTextPesquisaProduto.getText().toString() + ") OR (AEAPRODU.CODIGO_ESTRUTURAL = '" + editTextPesquisaProduto.getText().toString() + "') " +
                            "OR (AEAPRODU.REFERENCIA = '" + editTextPesquisaProduto.getText().toString() + "') ";

                } else {
                    whereProduto += "(AEAPRODU.DESCRICAO LIKE '%" + textoPesquisa + "%') OR (AEAMARCA.DESCRICAO LIKE '%" + textoPesquisa + "%' ) OR " +
                            "(AEAPRODU.REFERENCIA LIKE '%" + textoPesquisa + "%')";
                }

            } else if (editTextPesquisaProduto.getText().toString().length() >= 8) {
                boolean apenasNumeros = true;

                for (char digito : editTextPesquisaProduto.getText().toString().toCharArray()) {
                    // Checa se eh um numero
                    if (!Character.isDigit(digito)) {
                        apenasNumeros = false;
                    }
                }
                if (apenasNumeros) {
                    whereProduto += "(AEAPRODU.CODIGO_BARRAS = '" + editTextPesquisaProduto.getText().toString() + "') OR (AEAPRODU.REFERENCIA = '" + editTextPesquisaProduto.getText().toString() + "') " +
                            "OR ((SELECT COUNT(*) FROM AEAEMBAL " +
                            "WHERE (AEAEMBAL.ID_AEAPRODU = AEAPRODU.ID_AEAPRODU) AND (AEAEMBAL.CODIGO_BARRAS = '" + editTextPesquisaProduto.getText().toString() + "')) > 0) ";
                    //"OR (AEAEMBAL.CODIGO_BARRAS = '" + editTextPesquisar.getText().toString() + "')";
                }

            } else {
                whereProduto += "(AEAPRODU.DESCRICAO LIKE '%" + textoPesquisa + "%') OR (AEAMARCA.DESCRICAO LIKE '%" + textoPesquisa + "%' ) OR " +
                        "(AEAPRODU.REFERENCIA LIKE '%" + textoPesquisa + "%') OR " +
                        "((SELECT COUNT(*) FROM AEAEMBAL \n" +
                        "WHERE (AEAEMBAL.ID_AEAPRODU = AEAPRODU.ID_AEAPRODU) AND (AEAEMBAL.REFERENCIA LIKE '%" + textoPesquisa + "%')) > 0)) OR " +
                        "(AEAPRODU.DESCRICAO_AUXILIAR LIKE '%" + textoPesquisa + "%') OR (AEAPRODU.CODIGO_BARRAS = '" + editTextPesquisaProduto.getText().toString() + "') ";
            }

            intent.putExtra(ListaProdutoActivity.KEY_TELA_CHAMADA, ListaProdutoActivity.TELA_LOCACAO_PRODUTO_ACTIVITY);
            intent.putExtra(ListaProdutoActivity.KEY_TEXTO_PESQUISA, editTextPesquisaProduto.getText().toString());
            intent.putExtra(ListaProdutoActivity.KEY_WHERE_PESQUISA, whereProduto);
            // Abre a activity aquardando uma resposta
            startActivityForResult(intent, LocacaoProdutoActivity.REQUISICAO_DADOS_PRODUTOS);

        } else {
            SuperToast.create(LocacaoEnderecoActivity.this, getResources().getString(R.string.campo_pesquisa_vazio), SuperToast.Duration.LONG, Style.getStyle(Style.GRAY, SuperToast.Animations.POPUP)).show();
        }
    }

    public class CarregarListaProdutosLocacao extends AsyncTask<Void, Void, Void> {

        private Context context;
        private List<ProdutoLojaBeans> listaProdutoLoja;
        private String endereco, tipoLocacao;
        private List<LocesBeans> listaLoces;

        public CarregarListaProdutosLocacao(Context context, String tipoLocacao, String endereco) {
            this.context = context;
            this.tipoLocacao = tipoLocacao;
            this.endereco = endereco;
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

            String where = "((SELECT COUNT(*) FROM AEAESTOQ WHERE (AEAESTOQ.ID_AEAPLOJA = AEAPLOJA.ID_AEAPLOJA) " +
                    (tipoLocacao.equalsIgnoreCase("A") ? " AND (AEAESTOQ.LOCACAO_ATIVA = '" + endereco + "')) > 0)" :  " AND (AEAESTOQ.LOCACAO_RESERVA = " + endereco + ")) > 0)");


            ProdutoRotinas produtoRotinas = new ProdutoRotinas(context);
            listaProdutoLoja = produtoRotinas.selectListaProdutoLojaResumido(where, progressStatus);

            // Checa se retornou alguma coisa
            if (listaProdutoLoja != null && listaProdutoLoja.size() > 0){
                // Instancia a classe adapter
                adapterListaProdutos = new ItemUniversalAdapter(context, ItemUniversalAdapter.LISTA_PRODUTO_LOJA);
                // Envia a lista de produto para o adapter
                adapterListaProdutos.setListaProdutoLoja(listaProdutoLoja);
            }

            //LocesRotinas locesRotinas = new LocesRotinas(context);
            //listaLoces = locesRotinas.selectListaLoces(null, progressStatus);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Checa se a lista nao esta vazia
            if ((listaProdutoLoja != null) && (listaProdutoLoja.size() > 0)) {

                // Preenche o listView com o dados e o formato(layout)
                listViewListaProduto.setAdapter(adapterListaProdutos);

            } else {
                mensagemNaoEncontramos();
                // Retorna o foco para o campos de pesquisa de endereco
                editTextPesquisaLocacao.requestFocus();
            }
            //tirando o ProgressBar da nossa tela
            progressStatus.setVisibility(View.GONE);


        }

        private void mensagemNaoEncontramos() {
            ((Activity) LocacaoEnderecoActivity.this).runOnUiThread(new Runnable() {
                public void run() {

                    new MaterialDialog.Builder(LocacaoEnderecoActivity.this)
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


    public class CarregarListaLoces extends AsyncTask<Void, Void, Void> {

        private Context context;
        private List<LocesBeans> listaLoces;

        public CarregarListaLoces(Context context) {
            this.context = context;
        }

        // Aqui eh o que acontece antes da tarefa principal ser executado
        @Override
        protected void onPreExecute() {
            // o progressBar agora eh setado como visivel
            progressStatus.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {

            LocesRotinas locesRotinas = new LocesRotinas(context);
            listaLoces = locesRotinas.selectListaLoces(null, progressStatus);

            if (listaLoces != null && listaLoces.size() > 0){
                adapterListaLoces = new ItemUniversalAdapter(context, ItemUniversalAdapter.LISTA_LOCES);
                adapterListaLoces.setListaLoces(listaLoces);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if ((listaLoces != null) && (listaLoces.size() > 0)) {

                spinnerEstoque.setAdapter(adapterListaLoces);

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
        private String tipoLocacao, descricaoLocacao;
        private int idProdutoLoja, idLoces;
        private boolean atualizadoSucesso = false;

        public SalvarLocacao(Context context, String tipoLocacao, String descricaoLocacao, int idProdutoLoja, int idLoces) {
            this.context = context;
            this.tipoLocacao = tipoLocacao;
            this.descricaoLocacao = descricaoLocacao;
            this.idProdutoLoja = idProdutoLoja;
            this.idLoces = idLoces;
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

            // Checo qual eh o tipo de locacao
            if (tipoLocacao.equalsIgnoreCase("A")) {
                estoqueBeans.setLocacaoAtiva(descricaoLocacao);

            } else if (tipoLocacao.equalsIgnoreCase("R")) {
                estoqueBeans.setLocacaoReserva(descricaoLocacao);
            }

            String where = " (AEAESTOQ.ID_AEAPLOJA = " + idProdutoLoja + ") AND (AEAESTOQ.ID_AEALOCES = " + idLoces + ") ";

            EstoqueRotinas estoqueRotinas = new EstoqueRotinas(LocacaoEnderecoActivity.this);

            atualizadoSucesso = estoqueRotinas.updateLocacaoEstoque(estoqueBeans, where, progressStatus, null);

            return null;
        } // Fim doInBackground

        @Override
        protected void onPostExecute(Void result) {
            final ContentValues contentValues = new ContentValues();
            contentValues.put("comando", 2);

            if (atualizadoSucesso){

                CarregarListaProdutosLocacao carregarListaProdutosLocacao = new CarregarListaProdutosLocacao(context, tipoLocacao, editTextPesquisaLocacao.getText().toString());
                carregarListaProdutosLocacao.execute();

                contentValues.put("mensagem", context.getResources().getString(R.string.produto_inserido_endereco_com_sucesso));
                // Esvazia o campo de pesquisa de produto
                editTextPesquisaProduto.setText("");

                MediaPlayer somPositivo = MediaPlayer.create(context, R.raw.effect_alert_positive);
                somPositivo.start();

            } else {
                contentValues.put("mensagem", context.getResources().getString(R.string.nao_conseguimos_inserir_produto_inserido_endereco_com_sucesso));

                MediaPlayer somErro = MediaPlayer.create(context, R.raw.effect_alert_error);
                somErro.start();
            }
            final FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

            ((Activity) context).runOnUiThread(new Runnable() {
                public void run() {
                    // Executa uma mensagem rapida
                    funcoes.menssagem(contentValues);
                }
            });
            // Tita a barra de progresso do status
            progressStatus.setVisibility(View.GONE);
            // Retorna o foco para o campo de pesquisa de produtoVC7890--M
            editTextPesquisaProduto.requestFocus();
        }

    } // Fim SalvarLocacao
}
