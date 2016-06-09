package com.saga.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.clans.fab.FloatingActionButton;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
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

import me.sudar.zxingorient.ZxingOrient;
import me.sudar.zxingorient.ZxingOrientResult;

/**
 * Created by Bruno Nogueira Silva on 05/02/2016.
 */
public class ConferenciaItemNotaEntradaActivity extends AppCompatActivity {

    private Toolbar toolbarCabecalho;
    private TextView textDescricaoProduto, textCodigo, textReferencia, textMarca, textEmbalagemPricipal, textMensagemEmbalagem;
    private ListView listViewListaEmbalagem;
    private FloatingActionButton floatingButtonNovaEmbalagem;
    private ProgressBar progressStatus;
    private Button buttonEscanecarCodigoBarrasProduto;
    private EditText editCodigoBarraProduto, editPesoBruto, editPesoLiquido;
    private int idEntrada = -1, idItemEntrada = -1, idProduto = -1, idEmbalagem = -1;
    public static final String KEY_ID_AEAITENT = "ID_AEAITENT";
    public static final String KEY_ID_AEAENTRA = "ID_AEAENTRA",
                               KEY_ID_AEAPRODU = "ID_AEAPRODU";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conferencia_item_nota_entrada);

        restauraCampo();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Bundle intentParametro = getIntent().getExtras();
        if (intentParametro != null) {

            if ( (intentParametro.containsKey(KEY_ID_AEAENTRA) && (intentParametro.containsKey(KEY_ID_AEAITENT))) ) {
                idItemEntrada = intentParametro.getInt(KEY_ID_AEAITENT);
                idEntrada = intentParametro.getInt(KEY_ID_AEAENTRA);

            } else if ( (intentParametro.containsKey(KEY_ID_AEAPRODU)) ){
                idProduto = intentParametro.getInt(KEY_ID_AEAPRODU);
            }
            // Carrega os detalhes do produto
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

        buttonEscanecarCodigoBarrasProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new ZxingOrient(ConferenciaItemNotaEntradaActivity.this)
                        .setInfo(getResources().getString(R.string.escanear_codigo_produto))
                        .setVibration(true)
                        .setIcon(R.mipmap.ic_launcher)
                        .initiateScan();

            }
        });
    } // onCreate

    @Override
    protected void onResume() {
        super.onResume();

        // Carrega os detalhes do produto
        //LoaderDetalhesProdutos carregarDetalhes = new LoaderDetalhesProdutos();
        //carregarDetalhes.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ZxingOrientResult retornoEscanerCodigoBarra = ZxingOrient.parseActivityResult(requestCode, resultCode, data);

        if(retornoEscanerCodigoBarra != null) {
            // Checha se retornou algum dado
            if(retornoEscanerCodigoBarra.getContents() == null) {
                Log.d("SAGA", "Cancelled scan - ConferenciaItemNotaEntradaActivity");

                SuperToast.create(ConferenciaItemNotaEntradaActivity.this, getResources().getString(R.string.cancelado), SuperToast.Duration.LONG, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();

            } else {
                Log.d("SAGA", "Scanned - ConferenciaItemNotaEntradaActivity");

                editCodigoBarraProduto.setText(retornoEscanerCodigoBarra.getContents());
                // Posiciona o cursor para o final do texto
                editCodigoBarraProduto.setSelection(editCodigoBarraProduto.length());
            }
        } else {
            // This is important, otherwise the retornoEscanerCodigoBarra will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    } // Fim onActivityResult

    private void restauraCampo() {
        toolbarCabecalho = (Toolbar) findViewById(R.id.activity_conferencia_item_nota_entrada_toolbar_cabecalho);
        // Adiciona uma titulo para toolbar
        toolbarCabecalho.setTitle(this.getResources().getString(R.string.detalhes_produto));
        toolbarCabecalho.setTitleTextColor(getResources().getColor(R.color.branco));
        //toolbarInicio.setLogo(R.drawable.ic_launcher);
        // Seta uma toolBar para esta activiy(tela)
        setSupportActionBar(toolbarCabecalho);

        textDescricaoProduto = (TextView) findViewById(R.id.activity_conferencia_item_nota_entrada_text_descricao_produto);
        textCodigo = (TextView) findViewById(R.id.activity_conferencia_item_nota_entrada_text_codigo);
        textReferencia = (TextView) findViewById(R.id.activity_conferencia_item_nota_entrada_text_referencia);
        textMarca = (TextView) findViewById(R.id.activity_conferencia_item_nota_entrada_text_marca);
        textEmbalagemPricipal = (TextView) findViewById(R.id.activity_conferencia_item_nota_entrada_text_embalagem_principal);
        textMensagemEmbalagem = (TextView) findViewById(R.id.activity_conferencia_item_nota_entrada_textView_mensagem_embalagem);
        listViewListaEmbalagem = (ListView) findViewById(R.id.activity_conferencia_item_nota_entrada_list_lista_embalagem);
        floatingButtonNovaEmbalagem = (FloatingActionButton) findViewById(R.id.activity_conferencia_item_nota_entrada_floating_nova_embalagem);
        progressStatus = (ProgressBar) findViewById(R.id.activity_conferencia_item_nota_entrada_progress_status);
        editCodigoBarraProduto = (EditText) findViewById(R.id.activity_conferencia_item_nota_entrada_edit_codigo_barra_produto);
        editPesoBruto = (EditText) findViewById(R.id.activity_conferencia_item_nota_entrada_edit_peso_bruto);
        editPesoLiquido = (EditText) findViewById(R.id.activity_conferencia_item_nota_entrada_edit_peso_liquido);
        buttonEscanecarCodigoBarrasProduto = (Button) findViewById(R.id.activity_conferencia_item_nota_entrada_button_escanear_codigo_barras_produto);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_conferencia_item_nota_entrada, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:
                finish();
                break;

            case R.id.menu_activity_conferencia_item_nota_entrada_salvar:

                if ( (validaDados()) && (idProduto > 0) ){

                    // Checa se foi digitado um codigo de barra mais que 5 digitos
                    if ( (editCodigoBarraProduto.getText().length() > 5) || (editPesoLiquido.getText().length() > 0) || (editPesoBruto.getText().length() > 0) ) {

                        String sql = "UPDATE AEAPRODU SET CODIGO_BARRAS = '" + editCodigoBarraProduto.getText().toString() + "' ";

                        if (editPesoLiquido.getText().length() > 0){
                            sql += ", PESO_LIQUIDO = " + editPesoLiquido.getText();
                        }

                        if (editPesoBruto.getText().length() > 0){
                            sql += ", PESO_BRUTO = " + editPesoBruto.getText();
                        }

                        sql += "WHERE ID_AEAPRODU = " + idProduto;

                        AtualizarProdutos atualizarProdutos = new AtualizarProdutos(sql);
                        atualizarProdutos.execute();

                    } else {
                        SuperToast.create(ConferenciaItemNotaEntradaActivity.this, getResources().getString(R.string.digite_algum_valor_valido_em_aguns_campos), SuperToast.Duration.LONG, Style.getStyle(Style.BLUE, SuperToast.Animations.POPUP)).show();
                    }

                }
                break;

            case R.id.menu_activity_conferencia_item_nota_entrada_atualizar:
                // Carrega os detalhes do produto
                LoaderDetalhesProdutos carregarDetalhes = new LoaderDetalhesProdutos();
                carregarDetalhes.execute();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean validaDados(){
        boolean retorno = true;

        if (editCodigoBarraProduto.getText().length() > 0){
            final FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(ConferenciaItemNotaEntradaActivity.this);

            if (!funcoes.isValidarCodigoBarraGS1(editCodigoBarraProduto.getText().toString())){

                final ContentValues contentValues = new ContentValues();
                contentValues.put("comando", 2);
                contentValues.put("tela", "ConferenciaItemNotaEntradaActivity");
                contentValues.put("mensagem", getResources().getString(R.string.codigo_barras_invalido));

                ((Activity) ConferenciaItemNotaEntradaActivity.this).runOnUiThread(new Runnable() {
                    public void run() {
                        funcoes.menssagem(contentValues);
                    }
                });

                retorno = false;
            }
        }

        return retorno;
    }


    public class LoaderDetalhesProdutos extends AsyncTask<Void, Void, Void> {
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
                if (idItemEntrada > 0) {
                    NotaFiscalEntradaRotinas notaFiscalEntradaRotinas = new NotaFiscalEntradaRotinas(getApplicationContext());

                    produto = notaFiscalEntradaRotinas.selectProdutoResumidoIdItemEntrada(idItemEntrada);

                } else if (idProduto > 0){
                    ProdutoRotinas produtoRotinas = new ProdutoRotinas(getApplicationContext());

                    produto = produtoRotinas.selectProdutoResumidoId(idProduto, null);
                }
                // Checa se foi pego os dados de algum produto
                if (produto != null) {

                    idProduto = produto.getIdProduto();

                    listaEmbalagem = new ArrayList<EmbalagemBeans>();

                    EmbalagemRotinas embalagemRotinas = new EmbalagemRotinas(ConferenciaItemNotaEntradaActivity.this);

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
                editCodigoBarraProduto.setText(!produto.getCodigoBarras().equalsIgnoreCase("anyType{}") ? produto.getCodigoBarras() : "");
                editCodigoBarraProduto.setSelection(editCodigoBarraProduto.length());
                editPesoBruto.setText((produto.getPesoBruto() > 0) ? String.valueOf(produto.getPesoBruto()) : "");
                editPesoLiquido.setText((produto.getPesoLiquido() > 0) ? String.valueOf(produto.getPesoLiquido()) : "");

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

    public class AtualizarProdutos extends AsyncTask<Void, Void, Void> {

        String sql;

        public AtualizarProdutos(String sql) {
            this.sql = sql;
        }

        @Override
        protected void onPreExecute() {
            // o progressBar agora eh setado como visivel
            progressStatus.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {

            ProdutoRotinas produtoRotinas = new ProdutoRotinas(ConferenciaItemNotaEntradaActivity.this);

            // Checa se atualizou com sucesso
            if ( produtoRotinas.updateProduto(sql, progressStatus, null) ){

                (ConferenciaItemNotaEntradaActivity.this).runOnUiThread(new Runnable() {
                    public void run() {
                        SuperToast.create(ConferenciaItemNotaEntradaActivity.this, getResources().getString(R.string.atualizado_sucesso), SuperToast.Duration.LONG, Style.getStyle(Style.GREEN, SuperToast.Animations.POPUP)).show();

                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //tirando o ProgressBar da nossa tela
            progressStatus.setVisibility(View.GONE);
        }
    }
}
