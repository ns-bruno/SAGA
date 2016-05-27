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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.saga.R;
import com.saga.activity.ConferenciaItemNotaEntradaActivity;
import com.saga.activity.ListaUniversalActivity;
import com.saga.adapter.ItemUniversalAdapter;
import com.saga.beans.ConferenciaItemBeans;
import com.saga.beans.ItemNotaFiscalEntradaBeans;
import com.saga.beans.NotaFiscalEntradaBeans;
import com.saga.beans.RomaneioBeans;
import com.saga.configuracao.ConfiguracaoInterna;
import com.saga.funcoes.FuncoesPersonalizadas;
import com.saga.funcoes.rotinas.ConferenciaItemRotinas;
import com.saga.funcoes.rotinas.NotaFiscalEntradaRotinas;
import com.saga.provider.SearchableProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.sudar.zxingorient.ZxingOrient;
import me.sudar.zxingorient.ZxingOrientResult;

/**
 * Created by Bruno Nogueira Silva on 21/01/2016.
 */
public class ListaUniversalFragment extends Fragment {

    private int tipoTela = -1;
    private View viewOrcamento;
    private ListView listViewListagem;
    private ProgressBar progressBarStatus;
    private ItemUniversalAdapter adapterListagem;
    private TextView textMensagem;
    private EditText editPesquisar;
    private Button buttonEscanerCodigoBarras;
    private Toolbar toolbarRodape;
    private String nomeAba = null;
    private Boolean pesquisando = false;
    private int idEntrada, idRomaneio;
    private int mPreviousVisibleItem;
    private List<NotaFiscalEntradaBeans> listaNotaFiscalEntradaSelecionado;
    private List<ItemNotaFiscalEntradaBeans> listaItemNotaFiscalEntradaSelecionado;
    private int totalItemSelecionado = 0;

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
            idRomaneio = (parametro.get("ID_AEAROMAN") != null) ? parametro.getInt("ID_AEAROMAN") : -1;
        }

        // Checa qual eh o tipo de tela
        if (tipoTela == ListaUniversalActivity.TELA_NOTA_FISCAL_ENTRADA){

            // Mosta a tollbar na tela de lista de nota
            toolbarRodape.setVisibility(View.VISIBLE);

            // Atualiza o titulo da toolbar cabecalho
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getContext().getResources().getString(R.string.lista_nota_fiscal_entrada));

            if (pesquisando == false) {
                // informa que ja esta sendo pesquisado alguma coisa
                pesquisando = true;
                LoaderLista carregarLista = new LoaderLista(null, null);
                carregarLista.execute();
            }

            // Checa se eh uma tela de item de nota fiscal de entrada
        } else if (tipoTela == ListaUniversalActivity.TELA_ITEM_NOTA_FISCAL_ENTRADA){

            // Atualiza o titulo da toolbar cabecalho
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getContext().getResources().getString(R.string.lista_itens_nota_fiscal_entrada));

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
        } else if (tipoTela == ListaUniversalActivity.TELA_ROMANEIO){

            if (pesquisando == false){
                // informa que ja esta sendo pesquisado alguma coisa
                pesquisando = true;
                LoaderLista carregarLista = new LoaderLista(null, null);
                carregarLista.execute();
            }
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

        listViewListagem.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                // Checa se o tipo de tela eh a lista de nota fiscal
                if (tipoTela == ListaUniversalActivity.TELA_NOTA_FISCAL_ENTRADA) {

                    // Checa se a lista de selecionado eh nula
                    if (listaNotaFiscalEntradaSelecionado == null) {
                        listaNotaFiscalEntradaSelecionado = new ArrayList<NotaFiscalEntradaBeans>();
                    }
                } else if (tipoTela == ListaUniversalActivity.TELA_ITEM_NOTA_FISCAL_ENTRADA) {

                    // Checa se a lista de selecionado eh nula
                    if (listaItemNotaFiscalEntradaSelecionado == null) {
                        listaItemNotaFiscalEntradaSelecionado = new ArrayList<ItemNotaFiscalEntradaBeans>();
                    }
                }
                // Checa se o comando eh de selecao ou descelecao
                if (checked) {
                    // Incrementa o totalizador
                    totalItemSelecionado = totalItemSelecionado + 1;

                    // Checa se o tipo de tela eh a lista de nota fiscal
                    if (tipoTela == ListaUniversalActivity.TELA_NOTA_FISCAL_ENTRADA) {

                        listaNotaFiscalEntradaSelecionado.add(adapterListagem.getListaNotaFiscalEntrada().get(position));
                        // Marca o adapter para mudar a cor do fundo
                        adapterListagem.getListaNotaFiscalEntrada().get(position).setTagSelectContext(true);

                    } else if (tipoTela == ListaUniversalActivity.TELA_ITEM_NOTA_FISCAL_ENTRADA){

                        listaItemNotaFiscalEntradaSelecionado.add(adapterListagem.getListaItemNotaFiscalEntrada().get(position));
                        // Marca o adapter para mudar a cor do fundo
                        adapterListagem.getListaItemNotaFiscalEntrada().get(position).setTagSelectContext(true);
                    }
                    adapterListagem.notifyDataSetChanged();
                } else {
                    int i = 0;
                    // Checa se o tipo de tela eh a lista de nota fiscal
                    if (tipoTela == ListaUniversalActivity.TELA_NOTA_FISCAL_ENTRADA) {

                        // Passa por todos os selecionados
                        while (i < listaNotaFiscalEntradaSelecionado.size()) {

                            // Checar se a posicao desmarcada esta na lista
                            if (listaNotaFiscalEntradaSelecionado.get(i).getIdNotaFiscalEntrada() == adapterListagem.getListaNotaFiscalEntrada().get(position).getIdNotaFiscalEntrada()) {
                                // Remove a posicao da lista de selecao
                                listaNotaFiscalEntradaSelecionado.remove(i);
                                // Diminui o total de itens selecionados
                                totalItemSelecionado = totalItemSelecionado - 1;

                                // Mar o adapter para mudar a cor do fundo
                                adapterListagem.getListaNotaFiscalEntrada().get(position).setTagSelectContext(false);
                                adapterListagem.notifyDataSetChanged();
                            }
                            // Incrementa a variavel
                            i++;
                        }
                    } else if (tipoTela == ListaUniversalActivity.TELA_ITEM_NOTA_FISCAL_ENTRADA){

                        // Passa por todos os itens da nota fiscal
                        while (i < listaItemNotaFiscalEntradaSelecionado.size()){

                            // Checar se a posicao desmarcada esta na lista
                            if (listaItemNotaFiscalEntradaSelecionado.get(i).getIdItemNotaFiscalEntrada() == adapterListagem.getListaItemNotaFiscalEntrada().get(position).getIdItemNotaFiscalEntrada()){
                                // Remove a posicao da lista de selecao
                                listaItemNotaFiscalEntradaSelecionado.remove(i);
                                // Diminui o total de itens selecionados
                                totalItemSelecionado = totalItemSelecionado - 1;

                                // Mar o adapter para mudar a cor do fundo
                                adapterListagem.getListaItemNotaFiscalEntrada().get(position).setTagSelectContext(false);
                                adapterListagem.notifyDataSetChanged();
                            }
                            i++;
                        }
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

                if (tipoTela == ListaUniversalActivity.TELA_NOTA_FISCAL_ENTRADA) {
                    menuContextual.inflate(R.menu.menu_lista_universal_fragment_nota_fiscal_entrada_context, menu);

                } else if (tipoTela == ListaUniversalActivity.TELA_ITEM_NOTA_FISCAL_ENTRADA){
                    menuContextual.inflate(R.menu.menu_lista_universal_fragment_item_nota_fiscal_entrada_context, menu);
                }
                // Esconde a toolbar
                toolbarRodape.setVisibility(View.GONE);
                ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.menu_lista_universal_fragment_nota_fiscal_entrada_context_marcar_conferido:

                        // Passa por todos os itens selecionados
                        for (int i = 0; i < listaNotaFiscalEntradaSelecionado.size(); i++) {

                            String sql = "UPDATE AEAENTRA SET SITUACAO = 2 WHERE ID_AEAENTRA = " + listaNotaFiscalEntradaSelecionado.get(i).getIdNotaFiscalEntrada();

                            AtualizarNotaFiscalEntrada atualizarNotaFiscalEntrada = new AtualizarNotaFiscalEntrada(sql);
                            atualizarNotaFiscalEntrada.execute();
                        }
                        onDestroyActionMode(mode);
                        break;

                    case R.id.menu_lista_universal_fragment_item_nota_fiscal_entrada_context_marcar_conferido:

                        // Checa se realmente eh a tela de itens de notas
                        if (tipoTela == ListaUniversalActivity.TELA_ITEM_NOTA_FISCAL_ENTRADA) {

                            // Passa por todos os itens selecionados
                            for (int i = 0; i < listaItemNotaFiscalEntradaSelecionado.size(); i++) {

                                FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(getContext());

                            /*if (listaItemNotaFiscalEntradaSelecionado.get(i).getQuantidadeConferido() == listaItemNotaFiscalEntradaSelecionado.get(i).getQuantidade()){
                                // Mostra uma mensagem dizendo que ja foi tudo conferido
                                Toast.makeText( getContext(),
                                                getContext().getResources().getString(R.string.ja_foi_conferido_o_item) +
                                                        listaItemNotaFiscalEntradaSelecionado.get(i).getEstoque().getProdutoLoja().getProduto().getDescricaoProduto(),
                                                Toast.LENGTH_LONG ).show();

                            } else */
                                Date curDate = new Date();
                                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                                String DateToStr = format.format(curDate);

                                // Pega os dados do item na nota
                                final ConferenciaItemBeans conferenciaItemBeans = new ConferenciaItemBeans();
                                conferenciaItemBeans.setIdItemEntrada(listaItemNotaFiscalEntradaSelecionado.get(i).getIdItemNotaFiscalEntrada());
                                conferenciaItemBeans.setDataConferencia(DateToStr);
                                conferenciaItemBeans.setBaixaPorConferencia("0");

                                // Marca os campos aos quais nao eh pra tra ter valor, ou seja, tem que ser nulo
                                conferenciaItemBeans.setIdItemConferencia(-1);
                                conferenciaItemBeans.setIdItemRomaneio(-1);
                                conferenciaItemBeans.setIdItemsaida(-1);

                                // Checa se nos paramentros esta permitido digitar a quantidade conferida
                                if (funcoes.getValorXml("DigitaQuantidade").equalsIgnoreCase("S")) {
                                    String texto =
                                             "Codigo: " + listaItemNotaFiscalEntradaSelecionado.get(i).getEstoque().getProdutoLoja().getProduto().getCodigoEstrutural()
                                            +"\nMarca: " + listaItemNotaFiscalEntradaSelecionado.get(i).getEstoque().getProdutoLoja().getProduto().getMarca().getDescricao()
                                            +"\nRef.: " + listaItemNotaFiscalEntradaSelecionado.get(i).getEstoque().getProdutoLoja().getProduto().getReferencia();

                                    new MaterialDialog.Builder(getActivity())
                                            .title(listaItemNotaFiscalEntradaSelecionado.get(i).getEstoque().getProdutoLoja().getProduto().getDescricaoProduto())
                                            .content(texto)
                                            .inputRangeRes(1, 10, R.color.vermelho)
                                            .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER)
                                            .input(getContext().getResources().getString(R.string.digitar_quantidade), null, new MaterialDialog.InputCallback() {
                                                @Override
                                                public void onInput(MaterialDialog dialog, CharSequence input) {

                                                    conferenciaItemBeans.setQuantidade(Long.parseLong(input.toString()));

                                                    ConferirItemNotaFiscalEntrada conferirItemNotaFiscalEntrada = new ConferirItemNotaFiscalEntrada(conferenciaItemBeans);
                                                    conferirItemNotaFiscalEntrada.execute();
                                                }
                                            }).show();

                                    // Caso nao seja permitido digitar a quantidade permitida entao eh pego a quantidade total do item de entrada
                                } else {
                                    conferenciaItemBeans.setQuantidade((long) listaItemNotaFiscalEntradaSelecionado.get(i).getQuantidade());

                                    ConferirItemNotaFiscalEntrada conferirItemNotaFiscalEntrada = new ConferirItemNotaFiscalEntrada(conferenciaItemBeans);
                                    conferirItemNotaFiscalEntrada.execute();
                                }


                            } // Fim for
                        }

                        // Fecha o modo de menu context
                        onDestroyActionMode(mode);
                        //mode.

                        break;
                    default:
                        break;
                }

                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

                // Checa o tipo de tela
                if (tipoTela == ListaUniversalActivity.TELA_NOTA_FISCAL_ENTRADA) {
                    // Passa por tota a lista de orcamento/pedido
                    for (int i = 0; i < adapterListagem.getListaNotaFiscalEntrada().size(); i++) {
                        // Mar o adapter para mudar a cor do fundo
                        adapterListagem.getListaNotaFiscalEntrada().get(i).setTagSelectContext(false);
                    }
                    listaNotaFiscalEntradaSelecionado = null;

                    // Mostra a toolbar do rodape (apenas para a tela de lista de notas)
                    toolbarRodape.setVisibility(View.VISIBLE);

                } else if (tipoTela == ListaUniversalActivity.TELA_ITEM_NOTA_FISCAL_ENTRADA){
                    // Passa por tota a lista de orcamento/pedido
                    for (int i = 0; i < adapterListagem.getListaItemNotaFiscalEntrada().size(); i++) {
                        // Mar o adapter para mudar a cor do fundo
                        adapterListagem.getListaItemNotaFiscalEntrada().get(i).setTagSelectContext(false);
                    }
                    listaItemNotaFiscalEntradaSelecionado = null;
                }
                mode.finish();
                adapterListagem.notifyDataSetChanged();
                totalItemSelecionado = 0;

                // Mostra a toolbar cabecalho principal
                ((AppCompatActivity) getActivity()).getSupportActionBar().show();
            }
        });

        listViewListagem.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                // Checa se eh a tela de lista de nota fiscal
                if (tipoTela == ListaUniversalActivity.TELA_NOTA_FISCAL_ENTRADA) {

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


        editPesquisar.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // Checa se foi precionado a tecla enter
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    Log.d("SAGA", "enter_key_called - ListaUniversalFragment");

                    // Checa se tem alguma coisa digitada no campos
                    if (editPesquisar.getText().length() > 0) {

                        // Executa o comando de pesquisa pela digitacao do texto no rodape
                        pesquisarNotaEntrada();

                    } else {
                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.campo_pesquisa_vazio), Toast.LENGTH_LONG).show();
                    }
                }
                return false;
            }
        });

        buttonEscanerCodigoBarras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new ZxingOrient(getActivity())
                        .setInfo(getResources().getString(R.string.escanear_codigo_produto))
                        .setVibration(true)
                        .setIcon(R.mipmap.ic_launcher)
                        .initiateScan();

            }
        });

        return viewOrcamento;
    }

    @Override
    public void onResume() {
        super.onResume();


    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ZxingOrientResult retornoEscanerCodigoBarra = ZxingOrient.parseActivityResult(requestCode, resultCode, data);

        if(retornoEscanerCodigoBarra != null) {
            // Checha se retornou algum dado
            if(retornoEscanerCodigoBarra.getContents() == null) {
                Log.d("SAGA", "Cancelled scan - ListaUniversalFragment");

                Toast.makeText(getContext(), "Cancelado", Toast.LENGTH_LONG).show();

            } else {
                Log.d("SAGA", "Scanned - ListaUniversalFragment");
                //Toast.makeText(this, "Scanned: " + retornoEscanerCodigoBarra.getContents(), Toast.LENGTH_LONG).show();

                editPesquisar.setText(retornoEscanerCodigoBarra.getContents());
                // Posiciona o cursor para o final do texto
                editPesquisar.setSelection(editPesquisar.length());

                // Executa o comando de pesquisa pela digitacao do texto no rodape
                pesquisarNotaEntrada();

            }
        } else {
            // This is important, otherwise the retornoEscanerCodigoBarra will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
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
                                    + "(CFACLIFO.CPF_CGC LIKE '%" + query + "%') OR "
                                    + "(AEANATUR.DESCRICAO LIKE '%" + query + "%') OR "
                                    + "(CFACLIFO.CODIGO_FOR LIKE '%" + query + "%') )";

                            // Limpa o listView
                            listViewListagem.setAdapter(null);
                            // Executa
                            LoaderLista loaderListaAsync = new LoaderLista(where, null);
                            loaderListaAsync.execute();

                        } else if (tipoTela == ListaUniversalActivity.TELA_ITEM_NOTA_FISCAL_ENTRADA) {

                            String where = "( (AEAITENT.ID_AEAENTRA LIKE '%" + query + "%') OR "
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
        listViewListagem.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        progressBarStatus = (ProgressBar) viewOrcamento.findViewById(R.id.fragment_pagina_lista_universal_progress_bar_status_pesquisa);
        textMensagem = (TextView) viewOrcamento.findViewById(R.id.fragment_pagina_lista_universal_text_mensagem_geral);
        editPesquisar = (EditText) viewOrcamento.findViewById(R.id.fragment_pagina_lista_universal_edit_pesquisar);
        buttonEscanerCodigoBarras = (Button) viewOrcamento.findViewById(R.id.fragment_pagina_lista_universal_button_escanear_codigo_barras);
        toolbarRodape = (Toolbar) viewOrcamento.findViewById(R.id.fragment_pagina_lista_universal_toolbar_rodape);
    }


    private void pesquisarNotaEntrada(){

        String where = "";
        // Crio uma vareavel para saber se o que foi digitado eh apenas numeros
        boolean apenasNumeros = true;

        // Passa por todos os caracter checando se eh apenas numero
        for (char digito : editPesquisar.getText().toString().toCharArray()) {
            // Checa se eh um numero
            if (!Character.isDigit(digito)) {
                apenasNumeros = false;
                break;
            }
        }

        // Checa se o tipo de tela eh a lista de nota de entrada
        if (tipoTela == ListaUniversalActivity.TELA_NOTA_FISCAL_ENTRADA) {

            // checa se retornou apenas numero
            if (apenasNumeros) {

                // Checa a quantidade de digito retornado, se eh uma chave nfe
                if (editPesquisar.getText().length() == 44) {
                    where += " (AEAENTRA.CHV_NFE LIKE '%" + editPesquisar.getText() + "%') ";

                } else {
                    where += " (AEAENTRA.NUMERO = " + editPesquisar.getText() + ") OR ( AEAENTRA.ID_AEAENTRA = " + editPesquisar.getText() + ") ";
                }

            } else {
                where += " (CFACLIFO.NOME_RAZAO LIKE '%" + editPesquisar.getText() + "%') OR (CFACLIFO.NOME_FANTASIA LIKE '%" + editPesquisar.getText() + "%') " +
                        " OR (AEAENTRA.OBS LIKE '%" + editPesquisar.getText() + "%') ";
            }
            // Seca se nao esta pesquisando
            if (pesquisando == false) {
                // Marca que esta pesquisando
                pesquisando = true;

                // Executa o preenchimento da lista
                LoaderLista loaderLista = new LoaderLista(where, null);
                loaderLista.execute();
            }

        }
    }

    public void onLoaderLista(){

        if (tipoTela == ListaUniversalActivity.TELA_ITEM_NOTA_FISCAL_ENTRADA) {

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

            }
        }
    }


    public class LoaderLista extends AsyncTask<Void, Void, Void> {
        ContentValues parametros;
        String where = "";
        // Cria uma vareavel para salvar a lista de nota fiscal de entrada
        List<NotaFiscalEntradaBeans> listaNotaFiscalEntrada;
        List<ItemNotaFiscalEntradaBeans> listaItemNotaFiscalEntrada;
        List<RomaneioBeans> listaRomaneio;

        public LoaderLista(String where, ContentValues parametros) {
            this.where = (where != null) ? where.toUpperCase() : null;
            this.parametros = parametros;

            if (tipoTela == ListaUniversalActivity.TELA_NOTA_FISCAL_ENTRADA) {
                listaNotaFiscalEntrada = new ArrayList<NotaFiscalEntradaBeans>();

            } else if (tipoTela == ListaUniversalActivity.TELA_ITEM_NOTA_FISCAL_ENTRADA) {
                listaItemNotaFiscalEntrada = new ArrayList<ItemNotaFiscalEntradaBeans>();

            } else if (tipoTela == ListaUniversalActivity.TELA_ROMANEIO) {
                listaRomaneio = new ArrayList<RomaneioBeans>();

            }
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
                FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(getContext());

                NotaFiscalEntradaRotinas notaFiscalEntradaRotinas = null;

                if (tipoTela == ListaUniversalActivity.TELA_NOTA_FISCAL_ENTRADA || tipoTela == ListaUniversalActivity.TELA_ITEM_NOTA_FISCAL_ENTRADA){

                    // Instancia a classe de manipulacao de nota fiscal de entrada
                    notaFiscalEntradaRotinas = new NotaFiscalEntradaRotinas(getContext());
                    // Indica para a rotina que o tipo de conexao eh webservice
                    notaFiscalEntradaRotinas.setTipoConexao((!funcoes.getValorXml("TipoConexao").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? funcoes.getValorXml("TipoConexao"): "I");
                }

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

                // Checa se a tela eh de lista de romaneio
                } else if (tipoTela == ListaUniversalActivity.TELA_ROMANEIO){

                    // Checa se eh uma aba de itens da notas nao conferidos
                    if (nomeAba.contains("Conferir")){


                    } else if (nomeAba.contains("Conferido")){

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
                final ContentValues contentValues = new ContentValues();
                contentValues.put("comando", 0);
                contentValues.put("tela", "ListaUniversalFragment");
                contentValues.put("mensagem", "Erro ao carregar os dados do produto. \n" + e.getMessage());
                contentValues.put("dados", e.toString());
                // Pega os dados do usuario
                final FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(getContext());
                contentValues.put("usuario", funcoes.getValorXml("Usuario"));
                contentValues.put("empresa", funcoes.getValorXml("ChaveEmpresa"));
                contentValues.put("email", funcoes.getValorXml("Email"));

                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    public void run() {
                        // Exibe a mensagem
                        funcoes.menssagem(contentValues);
                    }
                });

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


    private class AtualizarNotaFiscalEntrada extends  AsyncTask<Void, Void, Void>{

        String sql;

        public AtualizarNotaFiscalEntrada(String sql) {
            this.sql = sql;
        }

        @Override
        protected void onPreExecute() {
            // o progressBar agora eh setado como visivel
            progressBarStatus.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {

            NotaFiscalEntradaRotinas notaFiscalEntradaRotinas = new NotaFiscalEntradaRotinas(getContext());

            // Checa se atualizou com sucesso
            if ( notaFiscalEntradaRotinas.updateNotaFiscalEntrada(sql, progressBarStatus, null) ){

                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(getContext(), getResources().getString(R.string.atualizado_sucesso), Toast.LENGTH_LONG).show();

                        // Atualiza a lista de notas de entrada
                        LoaderLista carregarListaNotas = new LoaderLista(null, null);
                        carregarListaNotas.execute();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Oculta a barra de status
            progressBarStatus.setVisibility(View.GONE);
        }
    } // Fim AtualizarNotaFiscalEntrada


    private class ConferirItemNotaFiscalEntrada extends  AsyncTask<Void, Void, Void>{

        private ConferenciaItemBeans conferenciaItemBeans;

        public ConferirItemNotaFiscalEntrada(ConferenciaItemBeans conferenciaItemBeans) {
            this.conferenciaItemBeans = conferenciaItemBeans;
        }

        @Override
        protected void onPreExecute() {
            // o progressBar agora eh setado como visivel
            progressBarStatus.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {

            ConferenciaItemRotinas conferenciaItemRotinas = new ConferenciaItemRotinas(getContext());

            // Checa se atualizou com sucesso
            if ( conferenciaItemRotinas.insertEmbalagem(conferenciaItemBeans, progressBarStatus, null) ){

                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(getContext(), getResources().getString(R.string.inserido_com_sucesso), Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // Oculta a barra de status
            progressBarStatus.setVisibility(View.GONE);

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
            }
        }

    } // Fim ConferirItemNotaFiscalEntrada


}
