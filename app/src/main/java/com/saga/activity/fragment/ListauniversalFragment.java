package com.saga.activity.fragment;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.saga.R;
import com.saga.activity.ConferenciaItemNotaEntradaActivity;
import com.saga.activity.ListaUniversalActivity;
import com.saga.adapter.ItemUniversalAdapter;
import com.saga.beans.ItemNotaFiscalEntradaBeans;
import com.saga.beans.NotaFiscalEntradaBeans;
import com.saga.funcoes.FuncoesPersonalizadas;
import com.saga.funcoes.rotinas.NotaFiscalEntradaRotinas;
import com.saga.provider.SearchableProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bruno Nogueira Silva on 21/01/2016.
 */
public class ListauniversalFragment extends Fragment {

    private int tipoTela = -1;
    private View viewOrcamento;
    private ListView listViewListagem;
    private ProgressBar progressBarStatus;
    private ItemUniversalAdapter adapterListagem;
    private TextView textMensagem;
    private EditText editPesquisarProduto;
    private String nomeAba = null;
    private Boolean pesquisando = false;
    private int idEntrada;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        viewOrcamento = inflater.inflate(R.layout.fragment_pagina_lista_universal, container, false);

        recuperarCampos();

        // Ativa a opcao de menus para este fragment
        setHasOptionsMenu(true);

        /**
         * Pega valores passados por parametro de outra Activity
         */
        Bundle parametro = getArguments();

        if(parametro != null){
            tipoTela = parametro.getInt(ListaUniversalActivity.KEY_TIPO_TELA);
            nomeAba = parametro.getString(ListaUniversalActivity.KEY_NOME_ABA);
            idEntrada = (parametro.get("ID_AEAENTRA") != null) ? parametro.getInt("ID_AEAENTRA") : -1; 
        }

        listViewListagem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Checa se o tipo de tela eh de nota fiscal de entrada
                if (tipoTela == ListaUniversalActivity.TELA_NOTA_FISCAL_ENTRADA) {

                    NotaFiscalEntradaBeans notaFiscal = (NotaFiscalEntradaBeans) parent.getItemAtPosition(position);

                    Bundle bundle = new Bundle();
                    bundle.putInt(ListaUniversalActivity.KEY_TIPO_TELA, ListaUniversalActivity.TELA_ITEM_NOTA_FISCAL_ENTRADA);
                    bundle.putInt("ID_AEAENTRA", notaFiscal.getIdNotaFiscalEntrada());

                    // Abre a tela de detalhes do produto
                    Intent intent = new Intent(getContext(), ListaUniversalActivity.class);
                    intent.putExtras(bundle);

                    startActivity(intent);

                } else if (tipoTela == ListaUniversalActivity.TELA_ITEM_NOTA_FISCAL_ENTRADA) {
                    ItemNotaFiscalEntradaBeans itemNota = (ItemNotaFiscalEntradaBeans) parent.getItemAtPosition(position);

                    Bundle bundle = new Bundle();
                    bundle.putInt(ConferenciaItemNotaEntradaActivity.KEY_ID_AEAENTRA, itemNota.getIdEntrada());
                    bundle.putInt(ConferenciaItemNotaEntradaActivity.KEY_ID_AEAITENT, itemNota.getIdItemNotaFiscalEntrada());

                    // Abre a tela de detalhes do produto
                    Intent intent = new Intent(getContext(), ConferenciaItemNotaEntradaActivity.class);
                    intent.putExtras(bundle);

                    startActivity(intent);
                }

            }
        });


        editPesquisarProduto.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    Log.d("SAGA", "enter_key_called - ListaUniversalFragment");

                    // Checa se tem alguma coisa digitada no campos
                    if (editPesquisarProduto.getText().length() > 0) {

                        // Che se eh foi digitado um codigo de barras
                        if (editPesquisarProduto.getText().toString().length() >= 8){

                            boolean apenasNumeros = true;

                            for (char digito : editPesquisarProduto.getText().toString().toCharArray()) {
                                // Checa se eh um numero
                                if (!Character.isDigit(digito)) {
                                    apenasNumeros = false;
                                }
                            }
                            // Checa se realmente foi digitado apena numeros
                            if (apenasNumeros){

                            }
                        }
                    }
                }
                return false;
            }
        });
        return viewOrcamento;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Checa qual eh o tipo de tela
        if (tipoTela == ListaUniversalActivity.TELA_NOTA_FISCAL_ENTRADA){

            if (pesquisando == false) {
                // informa que ja esta sendo pesquisado alguma coisa
                pesquisando = true;
                LoaderLista carregarLista = new LoaderLista(null, null);
                carregarLista.execute();
            }

        // Checa se eh uma tela de item de nota fiscal de entrada
        } else if (tipoTela == ListaUniversalActivity.TELA_ITEM_NOTA_FISCAL_ENTRADA){
            
            // Checa se tem algum id de entrada relacionado
            if (idEntrada > 0) {

                if (pesquisando == false) {
                    // informa que ja esta sendo pesquisado alguma coisa
                    pesquisando = true;

                    ContentValues paramentros = new ContentValues();
                    // Pega o id da entrada
                    paramentros.put("ID_AEAENTRA", idEntrada);

                    LoaderLista carregarLista = new LoaderLista(null, paramentros);
                    carregarLista.execute();
                }
            
            } else {
                // Armazena as informacoes para para serem exibidas e enviadas
                ContentValues contentValues = new ContentValues();
                contentValues.put("comando", 0);
                contentValues.put("tela", "ListaUniversalFragment");
                contentValues.put("mensagem", "Não foi passado o ID da Entrada. \n");
                contentValues.put("dados", idEntrada + " - " + nomeAba + " - " + tipoTela);
                // Pega os dados do usuario
                FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(getContext());
                contentValues.put("usuario", funcoes.getValorXml("Usuario"));
                contentValues.put("empresa", funcoes.getValorXml("ChaveEmpresa"));
                contentValues.put("email", funcoes.getValorXml("Email"));
                // Exibe a mensagem
                funcoes.menssagem(contentValues);
            }
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_lista_universal, menu);

        // Configuracao associando item de pesquisa com a SearchView
        SearchManager searchManager = (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);

        final SearchView searchView;
        final MenuItem itemMenuSearch = menu.findItem(R.id.menu_lista_universal_search_pesquisar);

        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ){
            searchView = (SearchView) itemMenuSearch.getActionView();

        } else{
            searchView = (SearchView) MenuItemCompat.getActionView(itemMenuSearch);
        }

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        //SearchView searchView = (SearchView) findViewById(R.id.search);
        EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.branco));
        searchEditText.setHintTextColor(getResources().getColor(R.color.branco));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Checa se ja esta fazendo alguma pesquisa
                if (!pesquisando) {
                    // Marca para avisar a app que esta fazendo uma pesquisa
                    pesquisando = true;
                    // Checa se o texto a ser pesquisado esta vasio
                    if (query != null && query.length() > 0) {

                        // Adiciona a query no historico de pesquisa
                        SearchRecentSuggestions searchRecentSuggestions = new SearchRecentSuggestions(getContext(), SearchableProvider.AUTHORITY, SearchableProvider.MODE);
                        searchRecentSuggestions.saveRecentQuery(query, null);

                        // Tira o espaco que contem na query de pesquisa e substitui por %
                        query = query.replaceAll(" ", "%");

                        if ((tipoTela == ListaUniversalActivity.TELA_NOTA_FISCAL_ENTRADA)) {

                            // Monta a clausula where para buscar no banco de dados
                            String where = "( (AEAENTRA.DT_ENTRADA LIKE '%" + query + "%') OR "
                                    + "(AEAENTRA.FC_VL_TOTAL LIKE '%" + query + "%') OR "
                                    + "(AEAENTRA.OBS LIKE '%" + query + "%') OR "
                                    + "(AEAENTRA.CHV_NFE LIKE '%" + query + "%') OR "
                                    + "(CFACLIFO.NOME_FANTASIA LIKE '%" + query + "%') OR "
                                    + "(CFACLIFO.NOME_RAZAO LIKE '%" + query + "%') OR "
                                    + "(CFACLIFO.CPF_CNPJ LIKE '%" + query + "%') OR "
                                    + "(AEANATUR.DESCRICAO LIKE '%" + query + "%') OR "
                                    + "(CFACLIFO.CODIGO_FOR LIKE '%" + query + "%') )";

                            // Limpa o listView
                            listViewListagem.setAdapter(null);
                            // Executa
                            LoaderLista loaderListaAsync = new LoaderLista(where, null);
                            loaderListaAsync.execute();

                        } else if (tipoTela == ListaUniversalActivity.TELA_ITEM_NOTA_FISCAL_ENTRADA){

                            String where =  "( (AEAITENT.ID_AEAENTRA LIKE '%" + query + "%') OR "
                                            + "(AEAITENT.DT_ENTRADA LIKE '%" + query + "%') OR "
                                            + "(AEAITENT.OBS LIKE '%" + query + "%') OR "
                                            + "(AEAITENT.SEQUENCIA LIKE '%" + query + "%') OR "
                                            + "(AEAPRODU.DESCRICAO LIKE '%" + query + "%') OR "
                                            + "(AEAMARCA.DESCRICAO LIKE '%" + query + "%') OR "
                                            + "(AEAUNVEN_PRODU.DESCRICAO_SINGULAR LIKE '%" + query + "%') OR "
                                            + "(AEAUNVEN_PRODU.SIGLA LIKE '%" + query + "%') OR "
                                            + "(AEACODOM.DESCRICAO LIKE '%" + query + "%') )";

                            // Lima o listView
                            listViewListagem.setAdapter(null);

                            ContentValues paramentros = new ContentValues();
                            // Pega o id da entrada
                            paramentros.put("ID_AEAENTRA", idEntrada);

                            LoaderLista loaderListaAsync = new LoaderLista(where, paramentros);
                            loaderListaAsync.execute();
                        }
                        // Tira o foco da searchView e fecha o teclado virtual
                        searchView.clearFocus();

                        // Forca o fechamento do teclado virtual
                        if (viewOrcamento != null) {
                            InputMethodManager imm = (InputMethodManager) viewOrcamento.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(viewOrcamento.getWindowToken(), 0);
                        }
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    onResume();
                }
                return false;
            }
        });
        searchView.setQueryHint(getResources().getString(R.string.pesquisar));
    }



    private void recuperarCampos(){
        listViewListagem = (ListView) viewOrcamento.findViewById(R.id.fragment_pagina_lista_universal_list_view_listagem);
        progressBarStatus = (ProgressBar) viewOrcamento.findViewById(R.id.fragment_pagina_lista_universal_progress_bar_status_pesquisa);
        textMensagem = (TextView) viewOrcamento.findViewById(R.id.fragment_pagina_lista_universal_text_mensagem_geral);
        editPesquisarProduto = (EditText) viewOrcamento.findViewById(R.id.fragment_pagina_lista_universal_edit_pesquisar);
    }

    public class LoaderLista extends AsyncTask<Void, Void, Void> {
        ContentValues parametros;
        String where = "";
        // Cria uma vareavel para salvar a lista de nota fiscal de entrada
        List<NotaFiscalEntradaBeans> listaNotaFiscalEntrada;
        List<ItemNotaFiscalEntradaBeans> listaItemNotaFiscalEntrada;

        public LoaderLista(String where, ContentValues parametros) {
            this.where = where;
            this.parametros = parametros;
            listaNotaFiscalEntrada = new ArrayList<NotaFiscalEntradaBeans>();
            listaItemNotaFiscalEntrada = new ArrayList<ItemNotaFiscalEntradaBeans>();
        }

        // Aqui eh o que acontece antes da tarefa principal ser executado
        @Override
        protected void onPreExecute() {
            // o progressBar agora eh setado como visivel
            progressBarStatus.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                NotaFiscalEntradaRotinas notaFiscalEntradaRotinas = new NotaFiscalEntradaRotinas(getContext());

                FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(getContext());

                // Indica para a rotina que o tipo de conexao eh webservice
                notaFiscalEntradaRotinas.setTipoConexao((!funcoes.getValorXml("TipoConexao").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? funcoes.getValorXml("TipoConexao"): "I");

                // Checa se o tipo de tela eh a de lista de nota fiscal de entrada
                if (tipoTela == ListaUniversalActivity.TELA_NOTA_FISCAL_ENTRADA) {

                    // Checa se eh uma aba das notas nao conferidas
                    if (nomeAba.contains("Conferir")){

                        listaNotaFiscalEntrada = notaFiscalEntradaRotinas.listaNotaFiscalEntrada(where, NotaFiscalEntradaRotinas.SEM_CONFERIR, progressBarStatus);

                    } else if (nomeAba.contains("Conferido")){
                        listaNotaFiscalEntrada = notaFiscalEntradaRotinas.listaNotaFiscalEntrada(where, NotaFiscalEntradaRotinas.CONFERIDO, progressBarStatus);
                    }

                    // Checa a tela eh de itens de nota fiscal de entrada
                } else if (tipoTela == ListaUniversalActivity.TELA_ITEM_NOTA_FISCAL_ENTRADA){

                    // Checa se eh uma aba de itens da notas nao conferidos
                    if (nomeAba.contains("Conferir")){
                        listaItemNotaFiscalEntrada = notaFiscalEntradaRotinas.listaItemNotaFiscalEntrada(parametros.getAsInteger("ID_AEAENTRA"),
                                                                                                         NotaFiscalEntradaRotinas.SIM,
                                                                                                         where,
                                                                                                         NotaFiscalEntradaRotinas.SEM_CONFERIR,
                                                                                                         progressBarStatus);
                    }else if (nomeAba.contains("Conferido")){
                        listaItemNotaFiscalEntrada = notaFiscalEntradaRotinas.listaItemNotaFiscalEntrada(parametros.getAsInteger("ID_AEAENTRA"),
                                                                                                         NotaFiscalEntradaRotinas.SIM,
                                                                                                         where,
                                                                                                         NotaFiscalEntradaRotinas.CONFERIDO,
                                                                                                         progressBarStatus);
                    }
                }

                if (tipoTela == ListaUniversalActivity.TELA_NOTA_FISCAL_ENTRADA) {
                    // Checa se a lista de produtos nao esta vazia e nem nula
                    if ((listaNotaFiscalEntrada != null) && (listaNotaFiscalEntrada.size() > 0)) {
                        // Instancia o adapter e o seu tipo(produto)
                        adapterListagem = new ItemUniversalAdapter(getContext(), ItemUniversalAdapter.NOTA_FISCAL_ENTRADA);
                        // Seta a lista de produtos no adapter
                        adapterListagem.setListaNotaFiscalEntrada(listaNotaFiscalEntrada);

                    } else {
                        mensagemNaoEncontramos();
                    }

                } else if (tipoTela == ListaUniversalActivity.TELA_ITEM_NOTA_FISCAL_ENTRADA){
                    // Checa se a lista deta vazia
                    if ((listaItemNotaFiscalEntrada != null) && (listaItemNotaFiscalEntrada.size() > 0)){
                        // Instancia o adapter e o seu tipo(produto)
                        adapterListagem = new ItemUniversalAdapter(getContext(), ItemUniversalAdapter.ITEM_NOTA_FISCAL_ENTRADA);
                        // Seta a lista de produtos no adapter
                        adapterListagem.setListaItemNotaFiscalEntrada(listaItemNotaFiscalEntrada);

                    }
                }
            }catch (Exception e) {
                // Armazena as informacoes para para serem exibidas e enviadas
                ContentValues contentValues = new ContentValues();
                contentValues.put("comando", 0);
                contentValues.put("tela", "ListaUniversalFragment");
                contentValues.put("mensagem", "Erro ao carregar os dados do produto. \n" + e.getMessage());
                contentValues.put("dados", e.toString());
                // Pega os dados do usuario
                FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(getContext());
                contentValues.put("usuario", funcoes.getValorXml("Usuario"));
                contentValues.put("empresa", funcoes.getValorXml("ChaveEmpresa"));
                contentValues.put("email", funcoes.getValorXml("Email"));
                // Exibe a mensagem
                funcoes.menssagem(contentValues);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (tipoTela == ListaUniversalActivity.TELA_NOTA_FISCAL_ENTRADA) {
                if ((listaNotaFiscalEntrada != null) && (listaNotaFiscalEntrada.size() > 0)) {
                    // Preenche a listView com os produtos buscados
                    listViewListagem.setAdapter(adapterListagem);
                }
            } else if (tipoTela == ListaUniversalActivity.TELA_ITEM_NOTA_FISCAL_ENTRADA) {
                if ((listaItemNotaFiscalEntrada != null) && (listaItemNotaFiscalEntrada.size() > 0)){
                    listViewListagem.setAdapter(adapterListagem);

                } else {
                    listViewListagem.setVisibility(View.GONE);
                    textMensagem.setVisibility(View.VISIBLE);
                }
            }
            //tirando o ProgressBar da nossa tela
            progressBarStatus.setVisibility(View.GONE);

            pesquisando = false;
        }

        private void mensagemNaoEncontramos(){
            ((Activity) getContext()).runOnUiThread(new Runnable() {
                public void run() {
                    new MaterialDialog.Builder(getActivity())
                            .title(R.string.produtos)
                            .content(((tipoTela == ListaUniversalActivity.TELA_NOTA_FISCAL_ENTRADA) || (tipoTela == ListaUniversalActivity.TELA_ITEM_NOTA_FISCAL_ENTRADA)) ?
                                    R.string.nao_encontramos_nenhuma_nota_fiscal_entrada : R.string.nenhum_valor_encontrado)
                            .positiveText(android.R.string.ok)
                                    //.negativeText(R.string.disagree)
                            .autoDismiss(true)
                            .show();
                }
            });
        }

    }
}
