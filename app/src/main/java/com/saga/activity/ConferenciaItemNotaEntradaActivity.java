package com.saga.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.clans.fab.FloatingActionButton;
import com.saga.R;
import com.saga.adapter.ItemUniversalAdapter;
import com.saga.beans.EmbalagemBeans;
import com.saga.beans.ItemNotaFiscalEntradaBeans;
import com.saga.beans.ProdutoBeans;
import com.saga.funcoes.FuncoesPersonalizadas;
import com.saga.funcoes.rotinas.EmbalagemRotinas;
import com.saga.funcoes.rotinas.NotaFiscalEntradaRotinas;
import com.saga.funcoes.rotinas.ProdutoRotinas;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bruno Nogueira Silva on 05/02/2016.
 */
public class ConferenciaItemNotaEntradaActivity extends AppCompatActivity {

    private Toolbar toolbarCabecalho;
    private TextView textDescricaoProduto, textCodigo, textReferencia, textCodigoBarrasProduto, textMarca, textEmbalagemPricipal, textMensagemEmbalagem;
    private ListView listViewListaEmbalagem;
    private FloatingActionButton floatingButtonNovaEmbalagem;
    private ProgressBar progressStatus;
    private int idEntrada, idItemEntrada, idProduto = -1, idEmbalagem = -1;
    public static final String KEY_ID_AEAITENT = "ID_AEAITENT";
    public static final String KEY_ID_AEAENTRA = "ID_AEAENTRA";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conferencia_item_nota_entrada);

        restauraCampo();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Bundle intentParametro = getIntent().getExtras();
        if (intentParametro != null) {
            idItemEntrada = intentParametro.getInt(KEY_ID_AEAITENT);
            idEntrada = intentParametro.getInt(KEY_ID_AEAENTRA);

            LoaderDetalhesProdutos carregarDetalhes = new LoaderDetalhesProdutos();
            carregarDetalhes.execute();
        }

        floatingButtonNovaEmbalagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt(CadastroEmbalagemActivity.KEY_ID_PRODUTO, idProduto);
                // Abre a tela de detalhes do produto
                Intent intent = new Intent(ConferenciaItemNotaEntradaActivity.this, CadastroEmbalagemActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        listViewListaEmbalagem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EmbalagemBeans embalagem = (EmbalagemBeans) parent.getItemAtPosition(position);

                Bundle bundle = new Bundle();
                bundle.putInt(CadastroEmbalagemActivity.KEY_ID_PRODUTO, idProduto);
                bundle.putInt(CadastroEmbalagemActivity.KEY_ID_AEAEMBAL, embalagem.getIdEmbalagem());
                // Abre a tela de detalhes do produto
                Intent intent = new Intent(ConferenciaItemNotaEntradaActivity.this, CadastroEmbalagemActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void restauraCampo() {
        toolbarCabecalho = (Toolbar) findViewById(R.id.activity_conferencia_item_nota_entrada_toolbar_cabecalho);
        // Adiciona uma titulo para toolbar
        toolbarCabecalho.setTitle(this.getResources().getString(R.string.app_name));
        toolbarCabecalho.setTitleTextColor(getResources().getColor(R.color.branco));
        //toolbarInicio.setLogo(R.drawable.ic_launcher);
        // Seta uma toolBar para esta activiy(tela)
        setSupportActionBar(toolbarCabecalho);

        textDescricaoProduto = (TextView) findViewById(R.id.activity_conferencia_item_nota_entrada_text_descricao_produto);
        textCodigo = (TextView) findViewById(R.id.activity_conferencia_item_nota_entrada_text_codigo);
        textReferencia = (TextView) findViewById(R.id.activity_conferencia_item_nota_entrada_text_referencia);
        textCodigoBarrasProduto = (TextView) findViewById(R.id.activity_conferencia_item_nota_entrada_text_codigo_barras_produto);
        textMarca = (TextView) findViewById(R.id.activity_conferencia_item_nota_entrada_text_marca);
        textEmbalagemPricipal = (TextView) findViewById(R.id.activity_conferencia_item_nota_entrada_text_embalagem_principal);
        textMensagemEmbalagem = (TextView) findViewById(R.id.activity_conferencia_item_nota_entrada_textView_mensagem_embalagem);
        listViewListaEmbalagem = (ListView) findViewById(R.id.activity_conferencia_item_nota_entrada_list_lista_embalagem);
        floatingButtonNovaEmbalagem = (FloatingActionButton) findViewById(R.id.activity_conferencia_item_nota_entrada_floating_nova_embalagem);
        progressStatus = (ProgressBar) findViewById(R.id.activity_conferencia_item_nota_entrada_progress_status);
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

    public class LoaderDetalhesProdutos extends AsyncTask<Void, Void, Void> {
        String where = null;
        ProdutoBeans produto;
        // Cria uma vareavel para salvar a lista de nota fiscal de entrada
        List<ItemNotaFiscalEntradaBeans> listaItemNotaFiscalEntrada;
        List<EmbalagemBeans> listaEmbalagem;

        public LoaderDetalhesProdutos() {
            listaItemNotaFiscalEntrada = new ArrayList<ItemNotaFiscalEntradaBeans>();
        }

        // Aqui eh o que acontece antes da tarefa principal ser executado
        @Override
        protected void onPreExecute() {
            // o progressBar agora eh setado como visivel
            progressStatus.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                NotaFiscalEntradaRotinas notaFiscalEntradaRotinas = new NotaFiscalEntradaRotinas(getApplicationContext());

                //where = "ID_AEAITENT = " + idItemEntrada;

                produto = notaFiscalEntradaRotinas.selectProdutoResumidoIdItemEntrada(idItemEntrada);

                if (produto != null) {
                    listaEmbalagem = new ArrayList<EmbalagemBeans>();

                    EmbalagemRotinas embalagemRotinas = new EmbalagemRotinas(ConferenciaItemNotaEntradaActivity.this);

                    idProduto = produto.getIdProduto();

                    listaEmbalagem = embalagemRotinas.listaEmbalagemProduto(idProduto, null, progressStatus);
                }

            } catch (Exception e) {
                // Armazena as informacoes para para serem exibidas e enviadas
                final ContentValues contentValues = new ContentValues();
                contentValues.put("comando", 0);
                contentValues.put("tela", "ConferenciaItemNotaEntrdadaActivity");
                contentValues.put("mensagem", "Erro ao carregar os dados do produto. \n" + e.getMessage());
                contentValues.put("dados", e.toString());
                // Pega os dados do usuario
                final FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(getApplicationContext());
                contentValues.put("usuario", funcoes.getValorXml("Usuario"));
                contentValues.put("empresa", funcoes.getValorXml("ChaveEmpresa"));
                contentValues.put("email", funcoes.getValorXml("Email"));
                // Exibe a mensagem
                /*((Activity) getApplicationContext()).runOnUiThread(new Runnable() {
                    public void run() {
                        funcoes.menssagem(contentValues);
                    }
                });*/
            }
            return null;
        }

                @Override
        protected void onPostExecute(Void result) {

            // Checa se a lista deta vazia
            if (produto != null) {

                textDescricaoProduto.setText(produto.getDescricaoProduto());
                textCodigo.setText("Cod.: " + produto.getCodigoEstrutural());
                textReferencia.setText("Ref.: " + produto.getReferencia());
                textMarca.setText(produto.getMarca().getDescricao());
                textEmbalagemPricipal.setText("Emb.: " + produto.getUnidadeVenda().getSigla());
                textCodigoBarrasProduto.setText("Cód. Bar. Produtos - " + produto.getCodigoBarras());

                if ((listaEmbalagem != null) && (listaEmbalagem.size() > 0)){

                    listViewListaEmbalagem.setVisibility(View.VISIBLE);

                    ItemUniversalAdapter adapterEmbalagem = new ItemUniversalAdapter(ConferenciaItemNotaEntradaActivity.this, ItemUniversalAdapter.EMBALAGEM);
                    adapterEmbalagem.setListaEmbalagem(listaEmbalagem);

                    listViewListaEmbalagem.setAdapter(adapterEmbalagem);
                } else {
                    listViewListaEmbalagem.setAdapter(null);
                    listViewListaEmbalagem.setVisibility(View.GONE);
                    textMensagemEmbalagem.setVisibility(View.VISIBLE);
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
}
