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
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.saga.R;
import com.saga.adapter.ItemUniversalAdapter;
import com.saga.beans.FotosBeans;
import com.saga.beans.ItemSaidaBeans;
import com.saga.beans.ProdutoLojaBeans;
import com.saga.funcoes.FuncoesPersonalizadas;
import com.saga.funcoes.rotinas.FotoRotinas;
import com.saga.funcoes.rotinas.ProdutoRotinas;
import com.saga.funcoes.rotinas.SaidaRotinas;

import java.util.List;

import me.sudar.zxingorient.ZxingOrient;
import me.sudar.zxingorient.ZxingOrientResult;

/**
 * Created by Faturamento on 31/03/2016.
 */
public class ListaProdutoActivity extends AppCompatActivity {

    private ListView listViewListaProduto;
    private Toolbar toolbarCabecalho;
    private ProgressBar progressBarStatus;
    private ItemUniversalAdapter adapterListaProdutos;
    private Toolbar toolbarRodape;
    private EditText editTextPesquisarProduto;
    private Button buttonEscanearCodigoBarras;
    private int telaChamada = -1,
                idSaida = -1;
    private int mPreviousVisibleItem;
    private String wherePesquisa, textoPesquisa;
    private boolean pesquisando = false, pesquisouPorProduto = false, pesquisouPorEmbalagem = false;
    public static final int TELA_LOCACAO_PRODUTO_ACTIVITY = 0,
                            TELA_LISTA_UNIVERSAL_FRAGMENT_PESQUISA_ITEM_SAIDA = 1;
    public static final String KEY_TELA_CHAMADA = "keyTelaChamada",
                               KEY_WHERE_PESQUISA = "keyWherePesquisa",
                               KEY_TEXTO_PESQUISA = "keyTextoPesquisa",
                               KEY_ID_SAIDA = "ID_SAIDA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_produto);

        recuperarCampos();

        /**
         * Pega valores passados por parametro de outra Activity
         */
        Bundle intentParametro = getIntent().getExtras();
        if (intentParametro != null) {

            this.telaChamada = intentParametro.getInt(KEY_TELA_CHAMADA);
            this.wherePesquisa = intentParametro.getString(KEY_WHERE_PESQUISA);
            this.textoPesquisa = intentParametro.getString(KEY_TEXTO_PESQUISA);
            this.idSaida = (intentParametro.containsKey(KEY_ID_SAIDA) ? intentParametro.getInt(KEY_ID_SAIDA) : -1);
        }

        if (telaChamada == -1){
            // Mostra o campo de pesquisa de produto
            toolbarRodape.setVisibility(View.VISIBLE);
            toolbarCabecalho.setTitle(getResources().getString(R.string.lista_produto));

        } else if (telaChamada == TELA_LOCACAO_PRODUTO_ACTIVITY){
            toolbarCabecalho.setTitle(textoPesquisa);

            LoaderProdutos carregarProduto = new LoaderProdutos(ListaProdutoActivity.this, wherePesquisa);
            carregarProduto.execute();

        } else if (telaChamada == TELA_LISTA_UNIVERSAL_FRAGMENT_PESQUISA_ITEM_SAIDA){
            toolbarCabecalho.setTitle(textoPesquisa);

            if ((wherePesquisa != null) || (wherePesquisa.length() > 0) || (textoPesquisa != null) || (textoPesquisa.length() > 0)) {
                LoaderProdutos carregarProduto = new LoaderProdutos(ListaProdutoActivity.this, wherePesquisa);
                carregarProduto.execute();

            } else {
                SuperToast.create(ListaProdutoActivity.this, getResources().getString(R.string.nenhum_filtro_foi_passado), SuperToast.Duration.LONG, Style.getStyle(Style.RED, SuperToast.Animations.FLYIN)).show();
            }
        }

        listViewListaProduto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Checa se teve alguma outra tela que chamou esta tela (lista de produtos)
                if (telaChamada == -1){
                    // Pega o item clicado na lista
                    ProdutoLojaBeans produtoLojaBeans = (ProdutoLojaBeans) parent.getItemAtPosition(position);

                    Bundle bundle = new Bundle();
                    bundle.putInt(CadastroEmbalagemActivity.KEY_ID_PRODUTO, produtoLojaBeans.getProduto().getIdProduto());
                    bundle.putInt(CadastroEmbalagemActivity.KEY_ID_AEAEMBAL, produtoLojaBeans.getProduto().getUnidadeVenda().getIdUnidadeVenda());

                    // Abre a tela de detalhes do produto
                    Intent intent = new Intent(ListaProdutoActivity.this, ConferenciaItemNotaEntradaActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);

                // Checa se que chamou esta tela foi a tela de locacao de produtos
                } else if (telaChamada == TELA_LOCACAO_PRODUTO_ACTIVITY){
                    // Pega o item clicado na lista
                    ProdutoLojaBeans produtoLojaBeans = (ProdutoLojaBeans) parent.getItemAtPosition(position);

                    Bundle idProduto = new Bundle();
                    idProduto.putInt("ID_AEAPLOJA", produtoLojaBeans.getIdProdutoLoja());

                    // Cria uma intent para returnar um valor para activity ProdutoLista
                    Intent returnIntent = new Intent();
                    returnIntent.putExtras(idProduto);

                    setResult(LocacaoProdutoActivity.PRODUTO_RETORNADO_SUCESSO, returnIntent);

                    finish();

                } else if (telaChamada == TELA_LISTA_UNIVERSAL_FRAGMENT_PESQUISA_ITEM_SAIDA){
                    // Pega o item clicado na lista
                    ItemSaidaBeans itemSaidaBeans = (ItemSaidaBeans) parent.getItemAtPosition(position);

                    Bundle retorno = new Bundle();
                    retorno.putDouble(ListaUniversalActivity.KEY_RETORNO_FATOR_PESQUISADO, itemSaidaBeans.getFatorProdutoPesquisado());
                    retorno.putInt(ListaUniversalActivity.KEY_ID_ITEM_SAIDA, itemSaidaBeans.getIdItemSaida());

                    // Cria uma intent para returnar um valor para activity ProdutoLista
                    Intent returnIntent = new Intent();
                    returnIntent.putExtras(retorno);

                    setResult(ListaUniversalActivity.RETORNO_ITEM_SAIDA_CONFERIDO_OK, returnIntent);

                    finish();
                }
            }
        });

        listViewListaProduto.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {


            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                // Checa se eh a tela de lista de nota fiscal
                if (telaChamada == -1) {

                    // Funcao para ocultar o float button quando rolar a lista de orcamento/pedido
                    if (firstVisibleItem > mPreviousVisibleItem) {
                        //toolbarRodape.animate().translationY(-toolbarRodape.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                        toolbarRodape.setVisibility(View.GONE);

                    } else if (firstVisibleItem < mPreviousVisibleItem) {
                        //toolbarRodape.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
                        toolbarRodape.setVisibility(View.VISIBLE);
                    }
                    mPreviousVisibleItem = firstVisibleItem;
                }
            }
        });

        editTextPesquisarProduto.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    Log.d("SAGA", "enter_key_called - ListaProdutoActivity");

                    pesquisarProduto();
                }

                return false;
            }
        });

        buttonEscanearCodigoBarras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new ZxingOrient(ListaProdutoActivity.this)
                        .setInfo(getResources().getString(R.string.escanear_codigo_produto))
                        .setVibration(true)
                        .setIcon(R.mipmap.ic_launcher)
                        .initiateScan();
            }
        });

    } // Fim onCreate


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ZxingOrientResult retornoEscanerCodigoBarra = ZxingOrient.parseActivityResult(requestCode, resultCode, data);

        // Checa se retornou algum dado do leitor de codigo de barra
        if(retornoEscanerCodigoBarra != null) {

            // Checha se retornou algum dado
            if(retornoEscanerCodigoBarra.getContents() == null) {
                Log.d("SAGA", "Cancelled scan - ListaProdutoActivity");

                SuperToast.create(ListaProdutoActivity.this, getResources().getString(R.string.escaneamento_cancelado), SuperToast.Duration.LONG, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();

            } else {
                Log.d("SAGA", "Scanned - ListaProdutoActivity");

                // Checa se teve alquma outra tela que chamou essa tela de lista de produtos
                if (telaChamada == -1){
                    editTextPesquisarProduto.setText(retornoEscanerCodigoBarra.getContents());

                    pesquisarProduto();
                }
            }
        } else {
            // This is important, otherwise the retornoEscanerCodigoBarra will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
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


    private void pesquisarProduto(){
        // Checa se tem alguma coisa digitada no campos
        if (editTextPesquisarProduto.getText().length() > 0) {

            String whereProduto = "";
            String textoPesquisa = editTextPesquisarProduto.getText().toString().replace(" ", "%");
            //Intent intent = new Intent(LocacaoProdutoActivity.this, ListaProdutoActivity.class);

            // Checa se realmente tem algum texto a ser pesquisado
            if ((textoPesquisa != null) && (textoPesquisa.length() > 1)){
                textoPesquisa = textoPesquisa.toUpperCase();
            }

            // Checa se o primeiro caracter eh uma letra
            if (Character.isLetter(editTextPesquisarProduto.getText().charAt(0))) {

                whereProduto += " (AEAPRODU.ATIVO = '1') AND (AEAPRODU.DESCRICAO LIKE '%" + textoPesquisa + "%') OR (AEAMARCA.DESCRICAO LIKE '%" + textoPesquisa + "%' ) OR " +
                        "(AEAPRODU.REFERENCIA LIKE '%" + textoPesquisa + "%') OR ((SELECT COUNT(*) FROM AEAEMBAL " +
                        "WHERE (AEAEMBAL.ID_AEAPRODU = AEAPRODU.ID_AEAPRODU) AND (AEAEMBAL.REFERENCIA LIKE '%" + textoPesquisa + "%')) > 0)";

                // Checo se eh um codigo interno ou estrutural
            } else if (editTextPesquisarProduto.getText().toString().length() < 8) {

                boolean apenasNumeros = true;

                for (char digito : editTextPesquisarProduto.getText().toString().toCharArray()) {
                    // Checa se eh um numero
                    if (!Character.isDigit(digito)) {
                        apenasNumeros = false;
                        break;
                    }
                }
                if (apenasNumeros) {

                    whereProduto += "(AEAPRODU.CODIGO = " + editTextPesquisarProduto.getText().toString() + ") OR (AEAPRODU.CODIGO_ESTRUTURAL = '" + editTextPesquisarProduto.getText().toString() + "') " +
                            "OR (AEAPRODU.REFERENCIA = '" + editTextPesquisarProduto.getText().toString() + "') ";

                } else {
                    whereProduto += "(AEAPRODU.DESCRICAO LIKE '%" + textoPesquisa + "%') OR (AEAMARCA.DESCRICAO LIKE '%" + textoPesquisa + "%' ) OR " +
                            "(AEAPRODU.REFERENCIA LIKE '%" + textoPesquisa + "%')";
                }

            } else if (editTextPesquisarProduto.getText().toString().length() >= 8) {
                boolean apenasNumeros = true;

                for (char digito : editTextPesquisarProduto.getText().toString().toCharArray()) {
                    // Checa se eh um numero
                    if (!Character.isDigit(digito)) {
                        apenasNumeros = false;
                        break;
                    }
                }
                if (apenasNumeros) {
                    whereProduto += "(AEAPRODU.CODIGO_BARRAS = '" + editTextPesquisarProduto.getText().toString() + "') OR (AEAPRODU.REFERENCIA = '" + editTextPesquisarProduto.getText().toString() + "') " +
                            "OR ((SELECT COUNT(*) FROM AEAEMBAL " +
                            "WHERE (AEAEMBAL.ID_AEAPRODU = AEAPRODU.ID_AEAPRODU) AND (AEAEMBAL.CODIGO_BARRAS = '" + editTextPesquisarProduto.getText().toString() + "')) > 0) ";
                    //"OR (AEAEMBAL.CODIGO_BARRAS = '" + editTextPesquisar.getText().toString() + "')";
                }

            } else {
                whereProduto += "(AEAPRODU.DESCRICAO LIKE '%" + textoPesquisa + "%') OR (AEAMARCA.DESCRICAO LIKE '%" + textoPesquisa + "%' ) OR " +
                        "(AEAPRODU.REFERENCIA LIKE '%" + textoPesquisa + "%') OR " +
                        "((SELECT COUNT(*) FROM AEAEMBAL \n" +
                        "WHERE (AEAEMBAL.ID_AEAPRODU = AEAPRODU.ID_AEAPRODU) AND (AEAEMBAL.REFERENCIA LIKE '%" + textoPesquisa + "%')) > 0)) OR " +
                        "(AEAPRODU.DESCRICAO_AUXILIAR LIKE '%" + textoPesquisa + "%') OR (AEAPRODU.CODIGO_BARRAS = '" + editTextPesquisarProduto.getText().toString() + "') ";
            }

            LoaderProdutos carregarProdutos = new LoaderProdutos(ListaProdutoActivity.this, whereProduto);
            carregarProdutos.execute();

        } else {
            SuperToast.create(ListaProdutoActivity.this, getResources().getString(R.string.campo_pesquisa_vazio), SuperToast.Duration.LONG, Style.getStyle(Style.GRAY, SuperToast.Animations.POPUP)).show();
        }
    } // Fim pesquisaProduto

    private void recuperarCampos(){
        listViewListaProduto = (ListView) findViewById(R.id.activity_lista_produto_listView_lista_produto);
        progressBarStatus = (ProgressBar) findViewById(R.id.activity_lista_produto_progressBar_status);
        toolbarCabecalho = (Toolbar) findViewById(R.id.activity_lista_produto_toolbar_cabecalho);
        toolbarRodape = (Toolbar) findViewById(R.id.activity_lista_produto_toolbar_rodape);
        editTextPesquisarProduto = (EditText) findViewById(R.id.activity_lista_produto_editText_pesquisar);
        buttonEscanearCodigoBarras = (Button) findViewById(R.id.activity_lista_produto_button_escanear_codigo_barra_produto);
        // Adiciona uma titulo para toolbar
        toolbarCabecalho.setTitle(this.getResources().getString(R.string.app_name));
        toolbarCabecalho.setTitleTextColor(getResources().getColor(R.color.branco));
        //toolbarInicio.setLogo(R.drawable.ic_launcher);
        // Seta uma toolBar para esta activiy(tela)
        setSupportActionBar(toolbarCabecalho);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    public class LoaderProdutos extends AsyncTask<Void, Void, Void> {
        String where = "";
        Context context;
        List<ProdutoLojaBeans> listaProdutoLoja;
        List<ItemSaidaBeans> listaItemSaida;

        public LoaderProdutos(Context context, String where) {
            this.where = where;
            this.context = context;
        }

        // Aqui eh o que acontece antes da tarefa principal ser executado
        @Override
        protected void onPreExecute() {
            // o progressBar agora eh setado como visivel
            progressBarStatus.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {

            if (telaChamada == TELA_LISTA_UNIVERSAL_FRAGMENT_PESQUISA_ITEM_SAIDA){

                if ( (where == null) || (where.length() <= 0)) {

                    // Crio uma vareavel para saber se o que foi digitado eh apenas numeros
                    boolean apenasNumeros = true;

                    // Passa por todos os caracter checando se eh apenas numero
                    for (char digito : textoPesquisa.toCharArray()) {
                        // Checa se eh um numero
                        if (!Character.isDigit(digito)) {
                            apenasNumeros = false;
                            break;
                        }
                    }
                    if (apenasNumeros) {
                        // Checa se o numero eh maior que 8 digitos
                        if ( (textoPesquisa.length() < 8) && (pesquisouPorProduto == false) ) {

                            where = "(AEAPRODU.CODIGO_ESTRUTURAL = '" + textoPesquisa + "') OR (AEAPRODU.ID_AEAPRODU = " + textoPesquisa + ")";

                        } else {
                            where = "(AEAEMBAL_PRODU.CODIGO_BARRAS = '" + textoPesquisa + "') OR (AEAPRODU.CODIGO_BARRAS = '" + textoPesquisa + "')";
                        }

                    } else {
                        where = "(AEAPRODU.DESCRICAO LIKE '%" + textoPesquisa + "%') OR (AEAMARCA.DESCRICAO LIKE '%" + textoPesquisa + "%')";
                    }
                }
                // Instancia a classe de rotinas de saidas
                SaidaRotinas saidaRotinas = new SaidaRotinas(context);

                if (pesquisouPorProduto == false){
                    // Marca que ja fez a pesquisa na tabela de embalagem
                    pesquisouPorProduto = true;

                    // Pesquisa o item de acordo com os dados passados por parametro
                    listaItemSaida = saidaRotinas.listaItemSaida(idSaida, SaidaRotinas.NAO, where, SaidaRotinas.SEM_CONFERIR, progressBarStatus);
                }
                // Checa se retornou alguma coisa
                if ( (listaItemSaida == null) || (listaItemSaida.size() <= 0) ){

                    // Checa se ja foi pesquisado por produto e se ainda nao foi pesquisado na tabela de embalagens
                    if ((pesquisouPorEmbalagem == false) && (pesquisouPorProduto)){
                        pesquisouPorEmbalagem = true;

                        // Pesquisa o item de acordo com os dados passados por parametro
                        listaItemSaida = saidaRotinas.listaItemSaida(idSaida, SaidaRotinas.SIM, where, SaidaRotinas.SEM_CONFERIR, progressBarStatus);
                    }
                }
                // Checa se retornou alguma coisa
                if ( (listaItemSaida != null) && (listaItemSaida.size() > 0) ){
                    // Instancia a classe de adapter
                    adapterListaProdutos = new ItemUniversalAdapter(context, ItemUniversalAdapter.LISTA_ITEM_PEDIDO);
                    // Envia a lista de produto para o adapter
                    adapterListaProdutos.setListaItemSaida(listaItemSaida);

                }

            } else {
                ProdutoRotinas produtoRotinas = new ProdutoRotinas(context);
                listaProdutoLoja = produtoRotinas.selectListaProdutoLojaResumido(where, progressBarStatus);

                // Checa se retornou alguma coisa
                if (listaProdutoLoja != null && listaProdutoLoja.size() > 0) {
                    // Instancia a classe adapter
                    adapterListaProdutos = new ItemUniversalAdapter(context, ItemUniversalAdapter.LISTA_PRODUTO_LOJA);
                    // Envia a lista de produto para o adapter
                    adapterListaProdutos.setListaProdutoLoja(listaProdutoLoja);
                }
            }
            return null;
        } // Fim doInBackground

        @Override
        protected void onPostExecute(Void result) {

            if ((listaProdutoLoja != null) && (listaProdutoLoja.size() > 0)) {

                if (telaChamada == TELA_LOCACAO_PRODUTO_ACTIVITY){

                    // Checa se retornou apena um produto
                    if (listaProdutoLoja.size() == 1){

                        Bundle idProduto = new Bundle();
                        idProduto.putInt("ID_AEAPLOJA", listaProdutoLoja.get(0).getIdProdutoLoja());

                        // Cria uma intent para returnar um valor para activity ProdutoLista
                        Intent returnIntent = new Intent();
                        returnIntent.putExtras(idProduto);

                        setResult(LocacaoProdutoActivity.PRODUTO_RETORNADO_SUCESSO, returnIntent);

                        finish();

                    } else {
                        // Preenche a listView com os produtos buscados
                        listViewListaProduto.setAdapter(adapterListaProdutos);

                        LoaderImagemProdutos carregarImagemProduto = new LoaderImagemProdutos(context);
                        carregarImagemProduto.execute();
                    }
                } else {

                    // Preenche a listView com os produtos buscados
                    listViewListaProduto.setAdapter(adapterListaProdutos);

                    LoaderImagemProdutos carregarImagemProduto = new LoaderImagemProdutos(context);
                    carregarImagemProduto.execute();
                }
            } else if ( (listaItemSaida != null) && (listaItemSaida.size() > 0) ){

                if (telaChamada == TELA_LISTA_UNIVERSAL_FRAGMENT_PESQUISA_ITEM_SAIDA){
                    // Checa se retornou apena um produto
                    if (listaItemSaida.size() == 1){

                        Bundle retorno = new Bundle();
                        retorno.putDouble(ListaUniversalActivity.KEY_RETORNO_FATOR_PESQUISADO, listaItemSaida.get(0).getFatorProdutoPesquisado());
                        retorno.putInt(ListaUniversalActivity.KEY_ID_ITEM_SAIDA, listaItemSaida.get(0).getIdItemSaida());

                        // Cria uma intent para returnar um valor para activity ProdutoLista
                        Intent returnIntent = new Intent();
                        returnIntent.putExtras(retorno);

                        setResult(ListaUniversalActivity.RETORNO_ITEM_SAIDA_CONFERIDO_OK, returnIntent);

                        finish();
                    } else {
                        // Preenche a listView com os produtos buscados
                        listViewListaProduto.setAdapter(adapterListaProdutos);

                        LoaderImagemProdutos carregarImagemProduto = new LoaderImagemProdutos(context);
                        carregarImagemProduto.execute();
                    }
                }
            } else if (telaChamada == TELA_LISTA_UNIVERSAL_FRAGMENT_PESQUISA_ITEM_SAIDA){

                // Caso nao tenha retornado nada na lista e ainda seja chamada de tela de item de saida, entao retorna negativo
                setResult(ListaUniversalActivity.RETORNO_ITEM_SAIDA_CONFERIDO_NEG);

                finish();
            }
            //tirando o ProgressBar da nossa tela
            progressBarStatus.setVisibility(View.GONE);

            pesquisando = false;
        }

    } // Fim LoaderProdutos


    public class LoaderImagemProdutos extends AsyncTask<Void, Void, Void> {

        private Context context;

        public LoaderImagemProdutos(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(this.context);

                // Checa se pode mostrar a imagem do produto
                if (funcoes.getValorXml("ImagemProduto").equalsIgnoreCase("S")){
                    // Checa se tem alguma lista de produtos preenchida
                    if (adapterListaProdutos.getListaProduto().size() > 0){

                        for (int i = 0; i < adapterListaProdutos.getListaProduto().size(); i++){
                            FotoRotinas fotoRotinas = new FotoRotinas(context);

                            FotosBeans fotoProduto = fotoRotinas.fotoIdProtudo("" + adapterListaProdutos.getListaProdutoLoja().get(i).getProduto().getIdProduto());
                            // Checa se tem alguma foto
                            if ((fotoProduto != null) && (fotoProduto.getFotos().length > 0)){
                                // Atualiza o adapte com a foto do produto
                                adapterListaProdutos.getListaProdutoLoja().get(i).getProduto().setImagemProduto(fotoProduto);
                            }
                        }
                        // Envia um sinal para o adapter atualizar
                        ((Activity) ListaProdutoActivity.this).runOnUiThread(new Runnable() {
                            public void run() {
                                adapterListaProdutos.notifyDataSetChanged();
                            }
                        });
                    } else if (adapterListaProdutos.getListaItemSaida().size() > 0){

                        for (int i = 0; i < adapterListaProdutos.getListaItemSaida().size(); i++){
                            FotoRotinas fotoRotinas = new FotoRotinas(context);

                            FotosBeans fotoProduto = fotoRotinas.fotoIdProtudo("" + adapterListaProdutos.getListaItemSaida().get(i).getEstoque().getProdutoLoja().getProduto().getIdProduto());
                            // Checa se tem alguma foto
                            if ((fotoProduto != null) && (fotoProduto.getFotos().length > 0)){
                                // Atualiza o adapte com a foto do produto
                                adapterListaProdutos.getListaItemSaida().get(i).getEstoque().getProdutoLoja().getProduto().setImagemProduto(fotoProduto);

                            }
                        }
                        // Envia um sinal para o adapter atualizar
                        ((Activity) ListaProdutoActivity.this).runOnUiThread(new Runnable() {
                            public void run() {
                                adapterListaProdutos.notifyDataSetChanged();
                            }
                        });
                    }
                }
            } catch (Exception e){
                // Armazena as informacoes para para serem exibidas e enviadas
                ContentValues contentValues = new ContentValues();
                contentValues.put("comando", 0);
                contentValues.put("tela", "ProdutoListaMDFragment");
                contentValues.put("mensagem", getResources().getString(R.string.nao_consegimos_carregar_imagem_produtos) + " \n" + e.getMessage());
                contentValues.put("dados", e.toString());
                // Pega os dados do usuario
                FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);
                contentValues.put("usuario", funcoes.getValorXml("Usuario"));
                contentValues.put("empresa", funcoes.getValorXml("ChaveEmpresa"));
                contentValues.put("email", funcoes.getValorXml("Email"));
                // Exibe a mensagem
                funcoes.menssagem(contentValues);
            }
            return null;
        }
    } // Fim LoaderImagemProdutos



}
