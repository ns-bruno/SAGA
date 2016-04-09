package com.saga.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
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

import com.saga.R;
import com.saga.adapter.ItemUniversalAdapter;
import com.saga.beans.FotosBeans;
import com.saga.beans.ProdutoLojaBeans;
import com.saga.funcoes.FuncoesPersonalizadas;
import com.saga.funcoes.rotinas.FotoRotinas;
import com.saga.funcoes.rotinas.ProdutoRotinas;

import java.util.List;

/**
 * Created by Faturamento on 31/03/2016.
 */
public class ListaProdutoActivity extends AppCompatActivity {

    private ListView listViewListaProduto;
    private Toolbar toolbarCabecalho;
    private ProgressBar progressBarStatus;
    private ItemUniversalAdapter adapterListaProdutos;
    private int telaChamada;
    private String wherePesquisa, textoPesquisa;
    private boolean pesquisando = false;
    public static final int LOCACAO_PRODUTO_ACTIVITY = 0;
    public static final String KEY_TELA_CHAMADA = "keyTelaChamada",
                               KEY_WHERE_PESQUISA = "keyWherePesquisa",
                               KEY_TEXTO_PESQUISA = "keyTextoPesquisa";

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
        }

        if (telaChamada == LOCACAO_PRODUTO_ACTIVITY){
            toolbarCabecalho.setTitle(textoPesquisa);

            LoaderProdutos carregarProduto = new LoaderProdutos(ListaProdutoActivity.this, wherePesquisa);
            carregarProduto.execute();
        }

        listViewListaProduto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ProdutoLojaBeans produtoLojaBeans = (ProdutoLojaBeans) parent.getItemAtPosition(position);

                // Checa se que chamou esta tela foi a tela de locacao de produtos
                if (telaChamada == LOCACAO_PRODUTO_ACTIVITY){
                    Bundle idProduto = new Bundle();
                    idProduto.putInt("ID_AEAPLOJA", produtoLojaBeans.getIdProdutoLoja());

                    // Cria uma intent para returnar um valor para activity ProdutoLista
                    Intent returnIntent = new Intent();
                    returnIntent.putExtras(idProduto);

                    setResult(LocacaoProdutoActivity.PRODUTO_RETORNADO_SUCESSO, returnIntent);

                    finish();
                }
            }
        });

    } // Fim onCreate


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
        listViewListaProduto = (ListView) findViewById(R.id.activity_lista_produto_listView_lista_produto);
        progressBarStatus = (ProgressBar) findViewById(R.id.activity_lista_produto_progressBar_status);
        toolbarCabecalho = (Toolbar) findViewById(R.id.activity_lista_produto_toolbar_cabecalho);
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

            ProdutoRotinas produtoRotinas = new ProdutoRotinas(context);
            listaProdutoLoja = produtoRotinas.selectListaProdutoLojaResumido(where, progressBarStatus);

            // Checa se retornou alguma coisa
            if (listaProdutoLoja != null && listaProdutoLoja.size() > 0){
                // Instancia a classe adapter
                adapterListaProdutos = new ItemUniversalAdapter(context, ItemUniversalAdapter.LISTA_PRODUTO_LOJA);
                // Envia a lista de produto para o adapter
                adapterListaProdutos.setListaProdutoLoja(listaProdutoLoja);
            }
            return null;
        } // Fim doInBackground

        @Override
        protected void onPostExecute(Void result) {

            if ((listaProdutoLoja != null) && (listaProdutoLoja.size() > 0)) {

                if (telaChamada == LOCACAO_PRODUTO_ACTIVITY){

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
