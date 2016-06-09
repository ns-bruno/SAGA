package com.saga.activity.fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.saga.R;
import com.saga.activity.ConferenciaItemNotaEntradaActivity;
import com.saga.activity.ListaProdutoActivity;
import com.saga.activity.ListaUniversalActivity;
import com.saga.adapter.ItemUniversalAdapter;
import com.saga.beans.ConferenciaItemBeans;
import com.saga.beans.ItemNotaFiscalEntradaBeans;
import com.saga.beans.ItemRomaneioBeans;
import com.saga.beans.ItemSaidaBeans;
import com.saga.beans.NotaFiscalEntradaBeans;
import com.saga.beans.RomaneioBeans;
import com.saga.beans.SaidaBeans;
import com.saga.funcoes.FuncoesPersonalizadas;
import com.saga.funcoes.rotinas.ConferenciaItemRotinas;
import com.saga.funcoes.rotinas.NotaFiscalEntradaRotinas;
import com.saga.funcoes.rotinas.RomaneioRotinas;
import com.saga.funcoes.rotinas.SaidaRotinas;

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
    private int idEntrada, idRomaneio, idSaida;
    private int mPreviousVisibleItem;
    private List<NotaFiscalEntradaBeans> listaNotaFiscalEntradaSelecionado;
    private List<ItemNotaFiscalEntradaBeans> listaItemNotaFiscalEntradaSelecionado;
    private List<ItemSaidaBeans> listaItemSaidaSelecionado;
    private int totalItemSelecionado = 0;
    private double fatorProdutoPesquisado = -1;


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
            idSaida = (parametro.get("ID_AEASAIDA") != null) ? parametro.getInt("ID_AEASAIDA") : -1;
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
                LoaderLista carregarLista = new LoaderLista(null);
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

                    LoaderLista carregarLista = new LoaderLista(null);
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

            // Mosta a tollbar na tela de lista de nota
            toolbarRodape.setVisibility(View.VISIBLE);

            // Atualiza o titulo da toolbar cabecalho
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getContext().getResources().getString(R.string.romaneio));

            if (pesquisando == false){
                // informa que ja esta sendo pesquisado alguma coisa
                pesquisando = true;
                LoaderLista carregarLista = new LoaderLista(null);
                carregarLista.execute();
            }
        } else if (tipoTela == ListaUniversalActivity.TELA_ITEM_ROMANEIO){

            // Mosta a tollbar na tela de lista de nota
            toolbarRodape.setVisibility(View.VISIBLE);

            // Atualiza o titulo da toolbar cabecalho
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getContext().getResources().getString(R.string.itens_romaneio));

            if (pesquisando == false){
                // informa que ja esta sendo pesquisado alguma coisa
                pesquisando = true;
                LoaderLista carregarLista = new LoaderLista(null);
                carregarLista.execute();
            }
        } else if (tipoTela == ListaUniversalActivity.TELA_PEDIDO){
            // Mosta a tollbar na tela de lista de nota
            toolbarRodape.setVisibility(View.VISIBLE);

            // Atualiza o titulo da toolbar cabecalho
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getContext().getResources().getString(R.string.lista_pedidos));

            if (pesquisando == false){
                // informa que ja esta sendo pesquisado alguma coisa
                pesquisando = true;
                LoaderLista carregarLista = new LoaderLista(null);
                carregarLista.execute();
            }

        } else if (tipoTela == ListaUniversalActivity.TELA_ITEM_PEDIDO){
            // Mosta a tollbar na tela de lista de nota
            toolbarRodape.setVisibility(View.VISIBLE);

            // Atualiza o titulo da toolbar cabecalho
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getContext().getResources().getString(R.string.itens_pedido));

            if (pesquisando == false){
                // informa que ja esta sendo pesquisado alguma coisa
                pesquisando = true;
                LoaderLista carregarLista = new LoaderLista(null);
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

                } else if (tipoTela == ListaUniversalActivity.TELA_ROMANEIO) {
                    RomaneioBeans romaneio = (RomaneioBeans) parent.getItemAtPosition(position);

                    Bundle bundle = new Bundle();
                    bundle.putInt(ListaUniversalActivity.KEY_TIPO_TELA, ListaUniversalActivity.TELA_ITEM_ROMANEIO);
                    bundle.putInt("ID_AEAROMAN", romaneio.getIdRomaneio());

                    // Abre a tela de detalhes do produto
                    Intent intent = new Intent(getContext(), ListaUniversalActivity.class);
                    intent.putExtras(bundle);

                    startActivity(intent);

                } else if (tipoTela == ListaUniversalActivity.TELA_PEDIDO) {
                    SaidaBeans saidaBeans = (SaidaBeans) parent.getItemAtPosition(position);

                    Bundle bundle = new Bundle();
                    bundle.putInt(ListaUniversalActivity.KEY_TIPO_TELA, ListaUniversalActivity.TELA_ITEM_PEDIDO);
                    bundle.putInt("ID_AEASAIDA", saidaBeans.getIdSaida());

                    // Abre a tela de detalhes do produto
                    Intent intent = new Intent(getContext(), ListaUniversalActivity.class);
                    intent.putExtras(bundle);

                    // Abre a nova tela
                    startActivity(intent);

                } else if (tipoTela == ListaUniversalActivity.TELA_ITEM_ROMANEIO) {
                    ItemRomaneioBeans itemRomaneioBeans = (ItemRomaneioBeans) parent.getItemAtPosition(position);

                    Bundle bundle = new Bundle();
                    bundle.putInt(ListaUniversalActivity.KEY_TIPO_TELA, ListaUniversalActivity.TELA_ITEM_PEDIDO);
                    bundle.putInt("ID_AEASAIDA", itemRomaneioBeans.getSaida().getIdSaida());

                    // Abre a tela de detalhes do produto
                    Intent intent = new Intent(getContext(), ListaUniversalActivity.class);
                    intent.putExtras(bundle);

                    // Abre a nova tela
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
                } else if (tipoTela == ListaUniversalActivity.TELA_ITEM_PEDIDO){

                    // Checa se a lista de selecionado eh nula
                    if (listaItemSaidaSelecionado == null){
                        listaItemSaidaSelecionado = new ArrayList<ItemSaidaBeans>();
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

                    } else if (tipoTela == ListaUniversalActivity.TELA_ITEM_PEDIDO){

                        listaItemSaidaSelecionado.add(adapterListagem.getListaItemSaida().get(position));
                        // Marca o adapter para mudar a cor do fundo
                        adapterListagem.getListaItemSaida().get(position).setTagSelectContext(true);
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
                    } else if (tipoTela == ListaUniversalActivity.TELA_ITEM_PEDIDO){

                        // Passa por todos os itens da nota fiscal
                        while (i < listaItemSaidaSelecionado.size()){

                            // Checar se a posicao desmarcada esta na lista
                            if (listaItemSaidaSelecionado.get(i).getIdSaida() == adapterListagem.getListaItemSaida().get(position).getIdSaida()){
                                // Remove a posicao da lista de selecao
                                listaItemSaidaSelecionado.remove(i);
                                // Diminui o total de itens selecionados
                                totalItemSelecionado = totalItemSelecionado - 1;

                                // Mar o adapter para mudar a cor do fundo
                                adapterListagem.getListaItemSaida().get(position).setTagSelectContext(false);
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

                } else if (tipoTela == ListaUniversalActivity.TELA_ITEM_PEDIDO){
                    menuContextual.inflate(R.menu.menu_lista_universal_fragment_item_pedido_context, menu);
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
                        // Marca o item com a quantidade conferida
                        marcarItemComoConferido();

                        // Fecha o modo de menu context
                        onDestroyActionMode(mode);
                        //mode.
                        break;

                    case R.id.menu_lista_universal_fragment_item_pedido_context_marcar_conferido:
                        // Marca o item conferido
                        marcarItemComoConferido();

                        // Fecha o modo de menu context
                        onDestroyActionMode(mode);

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
                    // Passa por tota a lista de nota fiscal de entrada
                    for (int i = 0; i < adapterListagem.getListaNotaFiscalEntrada().size(); i++) {
                        // Mar o adapter para mudar a cor do fundo
                        adapterListagem.getListaNotaFiscalEntrada().get(i).setTagSelectContext(false);
                    }
                    listaNotaFiscalEntradaSelecionado = null;

                    // Mostra a toolbar do rodape (apenas para a tela de lista de notas)
                    toolbarRodape.setVisibility(View.VISIBLE);

                } else if (tipoTela == ListaUniversalActivity.TELA_ITEM_NOTA_FISCAL_ENTRADA){
                    // Passa por tota a lista de itens da nota de entrada
                    for (int i = 0; i < adapterListagem.getListaItemNotaFiscalEntrada().size(); i++) {
                        // Mar o adapter para mudar a cor do fundo
                        adapterListagem.getListaItemNotaFiscalEntrada().get(i).setTagSelectContext(false);
                    }
                    listaItemNotaFiscalEntradaSelecionado = null;

                } else if (tipoTela == ListaUniversalActivity.TELA_ITEM_PEDIDO){
                    // Passa por toda a lista de itens do pedido
                    for (int i=0; i< adapterListagem.getListaItemSaida().size(); i++){
                        adapterListagem.getListaItemSaida().get(i).setTagSelectContext(false);
                    }
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
                //if ( (tipoTela == ListaUniversalActivity.TELA_NOTA_FISCAL_ENTRADA) || (tipoTela == ListaUniversalActivity.TELA_ROMANEIO) || (tipoTela == ListaUniversalActivity.TELA_ITEM_ROMANEIO) ) {

                    // Funcao para ocultar o float button quando rolar a lista de orcamento/pedido
                    if (firstVisibleItem > mPreviousVisibleItem) {
                        //toolbarRodape.animate().translationY(-toolbarRodape.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                        toolbarRodape.setVisibility(View.GONE);

                    } else if (firstVisibleItem < mPreviousVisibleItem) {
                        //toolbarRodape.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
                        toolbarRodape.setVisibility(View.VISIBLE);
                    }
                    mPreviousVisibleItem = firstVisibleItem;
                //}
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
                        pesquisarDigitadoEditTextPesquisar();

                    } else {
                        SuperToast.create(getContext(), getResources().getString(R.string.campo_pesquisa_vazio), SuperToast.Duration.LONG, Style.getStyle(Style.GRAY, SuperToast.Animations.POPUP)).show();

                        // Executa o preenchimento da lista
                        LoaderLista loaderLista = new LoaderLista(null);
                        loaderLista.execute();
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

        // Checa a requisicao
        if (requestCode == ListaUniversalActivity.REQUISICAO_DADOS_PRODUTOS){
            // Checa se retornou com sucesso
            if (resultCode == ListaUniversalActivity.RETORNO_ITEM_SAIDA_CONFERIDO_OK){
                // Pega o id passado por parametro
                fatorProdutoPesquisado = (data.getExtras().getDouble(ListaUniversalActivity.KEY_RETORNO_FATOR_PESQUISADO));
                int idItemSaida = (data.getExtras().getInt(ListaUniversalActivity.KEY_ID_ITEM_SAIDA));

                // Checa se retornou algum codigo de produto
                if ( (fatorProdutoPesquisado > 0) && (idItemSaida > 0) ){

                    for (int i = 0; i < adapterListagem.getListaItemSaida().size(); i++){

                        // Checa se o id item saida Retornado existe na lista
                        if (adapterListagem.getListaItemSaida().get(i).getIdItemSaida() == idItemSaida){

                            // Checa se a lista de selecionados esta instanciada
                            if (listaItemSaidaSelecionado == null){
                                listaItemSaidaSelecionado = new ArrayList<ItemSaidaBeans>();
                            }
                            // Adiciona o item saida em uma lista de selecionados
                            listaItemSaidaSelecionado.add(adapterListagem.getListaItemSaida().get(i));
                            // Para o laco for
                            break;
                        }
                    }
                    marcarItemComoConferido();
                }
            } else if (resultCode == ListaUniversalActivity.RETORNO_ITEM_SAIDA_CONFERIDO_NEG){
                // Emite um som de erro
                MediaPlayer somSucesso = MediaPlayer.create(getContext(), R.raw.effect_alert_error);
                somSucesso.start();

                SuperToast.create(getContext(), getResources().getString(R.string.nao_foi_localizado_produto), SuperToast.Duration.LONG, Style.getStyle(Style.RED, SuperToast.Animations.FLYIN)).show();
            }
        }

        if(retornoEscanerCodigoBarra != null) {
            // Checha se retornou algum dado
            if(retornoEscanerCodigoBarra.getContents() == null) {
                Log.d("SAGA", "Cancelled scan - ListaUniversalFragment");

                SuperToast.create(getContext(), getResources().getString(R.string.escaneamento_cancelado), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.FLYIN)).show();

            } else {
                Log.d("SAGA", "Scanned - ListaUniversalFragment");
                //SuperToast.create(getContext(), getResources().getString(R.string.campo_pesquisa_vazio), SuperToast.Duration.LONG, Style.getStyle(Style.GRAY, SuperToast.Animations.POPUP)).show();

                editPesquisar.setText(retornoEscanerCodigoBarra.getContents());
                // Posiciona o cursor para o final do texto
                editPesquisar.setSelection(editPesquisar.length());

                // Executa o comando de pesquisa pela digitacao do texto no rodape
                pesquisarDigitadoEditTextPesquisar();

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
        /*SearchManager searchManager = (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);

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
        searchView.setQueryHint(getResources().getString(R.string.pesquisar));*/
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.menu_lista_universal_atualizar:
                // Recarrega a listagem de acordo com o tipo de tela
                LoaderLista recarregarLista = new LoaderLista(null);
                recarregarLista.execute();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
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


    private void pesquisarDigitadoEditTextPesquisar(){

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

        // Checa se o tipo de tela eh a lista de romaneio
        } else if (tipoTela == ListaUniversalActivity.TELA_ROMANEIO){
            // Checa se eh apenas numeros
            if (apenasNumeros){
                where += " (AEAROMAN.ID_AEAROMAN = " + editPesquisar.getText().toString() + ") OR (AEAROMAN.NUMERO = " + editPesquisar.getText().toString() + ") OR (CFAAREAS.CODIGO = " + editPesquisar.getText().toString() + ")";

            } else {
                where += " (AEAROMAN.OBS LIKE '%" + editPesquisar.getText().toString() + "%') OR (CFAAREAS.DESCRICAO LIKE '%" + editPesquisar.getText().toString() + "%')";
            }
        } else if (tipoTela == ListaUniversalActivity.TELA_ITEM_ROMANEIO){
            // Checa se eh apenas numeros
            if (apenasNumeros){
                where += " (AEAITROM.ID_AEAITROM = " + editPesquisar.getText().toString() + ") OR (AEASAIDA.NUMERO = " + editPesquisar.getText().toString() + ") OR " +
                         " (AEASAIDA.ID_AEASAIDA = " + editPesquisar.getText().toString() + ") ";

            } else {
                where += " (AEASAIDA.NOME_CLIENTE LIKE '%" + editPesquisar.getText().toString() + "%') OR (AEASAIDA.BAIRRO_CLIENTE LIKE '%" + editPesquisar.getText().toString() + "%') OR " +
                         " (CFAESTAD.DESCRICAO LIKE '%" + editPesquisar.getText().toString() + "%') OR (CFACIDAD.DESCRICAO LIKE '%" + editPesquisar.getText().toString() + "%') OR " +
                         " (CFAESTAD.UF LIKE '%" + editPesquisar.getText().toString() + "%') OR (AEASERIE.CODIGO LIKE '%" +editPesquisar.getText().toString() + "%')";
            }

        } else if (tipoTela == ListaUniversalActivity.TELA_PEDIDO){
            // Checa se eh apenas numeros
            if (apenasNumeros){
                where += " (AEASAIDA.ID_AEASAIDA = " + editPesquisar.getText().toString() + ") OR (AEASAIDA.NUMERO = " + editPesquisar.getText().toString() + ") ";

            } else {
                where += " (AEASAIDA.NOME_CLIENTE LIKE '%" + editPesquisar.getText().toString() + "%') OR (AEASAIDA.CPF_CGC_CLIENTE LIKE '%"+ editPesquisar.getText().toString() + "%') OR " +
                         " (AEASAIDA.ENDERECO_CLIENTE LIKE '%" + editPesquisar.getText().toString() + "'%) OR (AEASAIDA.BAIRRO_CLIENTE LIKE '%" + editPesquisar.getText().toString() + "%') OR " +
                         " (AEASAIDA.FONE_CLIENTE LIKE '%" + editPesquisar.getText().toString() + "%') OR (CFAESTAD.UF LIKE %'" + editPesquisar.getText().toString() + "%') OR " +
                         " (CFAESTAD.DESCRICAO LIKE '%" + editPesquisar.getText().toString() + "%') OR (CFACIDAD.DESCRICAO LIKE '%" + editPesquisar.getText().toString() + "%') ";
            }

        } else if (tipoTela == ListaUniversalActivity.TELA_ITEM_PEDIDO){

            if (apenasNumeros){
                // Checa se o numero eh maior que 8 digitos
                if (editPesquisar.getText().length() >= 8){
                    where += "AEAEMBAL_PRODU.CODIGO_BARRAS = '" + editPesquisar.getText().toString() + "'";

                } else {
                    where += "(AEAPRODU.CODIGO_ESTRUTURAL = '" + editPesquisar.getText().toString() + "') OR (AEAPRODU.ID_AEAPRODU = " + editPesquisar.getText().toString() + ")";
                }

            } else {
                where += "(AEAPRODU.DESCRICAO LIKE '%" +editPesquisar.getText().toString() + "%') OR (AEAMARCA.DESCRICAO LIKE '%" + editPesquisar.getText().toString() + "%')";
            }

            Intent intent = new Intent(getContext(), ListaProdutoActivity.class);
            intent.putExtra(ListaProdutoActivity.KEY_TELA_CHAMADA, ListaProdutoActivity.TELA_LISTA_UNIVERSAL_FRAGMENT_PESQUISA_ITEM_SAIDA);
            intent.putExtra(ListaProdutoActivity.KEY_ID_SAIDA, idSaida);
            intent.putExtra(ListaProdutoActivity.KEY_TEXTO_PESQUISA, editPesquisar.getText().toString());
            intent.putExtra(ListaProdutoActivity.KEY_WHERE_PESQUISA, where);
            // Abre a activity aquardando uma resposta
            startActivityForResult(intent, ListaUniversalActivity.REQUISICAO_DADOS_PRODUTOS);
        }

        if ((tipoTela == ListaUniversalActivity.TELA_NOTA_FISCAL_ENTRADA) || (tipoTela == ListaUniversalActivity.TELA_ROMANEIO) ||
                (tipoTela == ListaUniversalActivity.TELA_ITEM_ROMANEIO) || (tipoTela == ListaUniversalActivity.TELA_PEDIDO)) {

            // Seca se nao esta pesquisando
            if (pesquisando == false) {
                // Marca que esta pesquisando
                pesquisando = true;

                // Executa o preenchimento da lista
                LoaderLista loaderLista = new LoaderLista(where);
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

                    //ContentValues paramentros = new ContentValues();
                    // Pega o id da entrada
                    //paramentros.put("ID_AEAENTRA", idEntrada);

                    LoaderLista carregarLista = new LoaderLista(null);
                    carregarLista.execute();
                }

            }
        } else if (tipoTela == ListaUniversalActivity.TELA_ITEM_PEDIDO){

            if ( (idSaida > 0) && (pesquisando == false) ){
                LoaderLista carregarLista = new LoaderLista(null);
                carregarLista.execute();
            }
        }
    }


    private void marcarItemComoConferido(){

        Date curDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String DateToStr = format.format(curDate);

        // Pega os dados do item na nota
        final ConferenciaItemBeans conferenciaItemBeans = new ConferenciaItemBeans();
        conferenciaItemBeans.setDataConferencia(DateToStr);
        conferenciaItemBeans.setBaixaPorConferencia("0");

        // Marca os campos aos quais nao eh pra tra ter valor, ou seja, tem que ser nulo
        conferenciaItemBeans.setIdItemConferencia(-1);
        conferenciaItemBeans.setIdItemRomaneio(-1);
        conferenciaItemBeans.setIdItemsaida(-1);
        conferenciaItemBeans.setIdItemEntrada(-1);

        String texto = "";

        FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(getContext());

        // Checa se realmente eh a tela de itens de notas
        if (tipoTela == ListaUniversalActivity.TELA_ITEM_NOTA_FISCAL_ENTRADA) {

            // Passa por todos os itens selecionados
            for (int i = 0; i < listaItemNotaFiscalEntradaSelecionado.size(); i++) {

                texto =
                        "Codigo: " + listaItemNotaFiscalEntradaSelecionado.get(i).getEstoque().getProdutoLoja().getProduto().getCodigoEstrutural()
                        +"\nMarca: " + listaItemNotaFiscalEntradaSelecionado.get(i).getEstoque().getProdutoLoja().getProduto().getMarca().getDescricao()
                        +"\nRef.: " + listaItemNotaFiscalEntradaSelecionado.get(i).getEstoque().getProdutoLoja().getProduto().getReferencia();

                // Adiciona o id do item de entrada
                conferenciaItemBeans.setIdItemEntrada(listaItemNotaFiscalEntradaSelecionado.get(i).getIdItemNotaFiscalEntrada());

                // Checa se nos paramentros esta permitido digitar a quantidade conferida
                if (funcoes.getValorXml("DigitaQuantidade").equalsIgnoreCase("S")) {

                    new MaterialDialog.Builder(getActivity())
                            .title(listaItemNotaFiscalEntradaSelecionado.get(i).getEstoque().getProdutoLoja().getProduto().getDescricaoProduto())
                            .content(texto)
                            .inputRangeRes(1, 10, R.color.vermelho)
                            .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER)
                            .input(getContext().getResources().getString(R.string.digitar_quantidade), null, new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(MaterialDialog dialog, CharSequence input) {

                                    conferenciaItemBeans.setQuantidade(input.toString());
                                    ConferirItemAsyncTask conferirItemAsyncTask = new ConferirItemAsyncTask(conferenciaItemBeans);
                                    conferirItemAsyncTask.execute();
                                }
                            }).show();


                // Caso nao seja permitido digitar a quantidade entao eh pego a quantidade retornada da pesquisa do produto na saida
                }else {
                    conferenciaItemBeans.setQuantidade(""+listaItemNotaFiscalEntradaSelecionado.get(i).getQuantidade());

                    ConferirItemAsyncTask conferirItemAsyncTask = new ConferirItemAsyncTask(conferenciaItemBeans);
                    conferirItemAsyncTask.execute();
                }

            } // Fim for
        } else if (tipoTela == ListaUniversalActivity.TELA_ITEM_PEDIDO){

            // Passa por todos os itens selecionados
            for (int i = 0; i < listaItemSaidaSelecionado.size(); i++) {

                texto =
                        "Codigo: " + listaItemSaidaSelecionado.get(i).getEstoque().getProdutoLoja().getProduto().getCodigoEstrutural()
                        +"\nMarca: " + listaItemSaidaSelecionado.get(i).getEstoque().getProdutoLoja().getProduto().getMarca().getDescricao()
                        +"\nRef.: " + listaItemSaidaSelecionado.get(i).getEstoque().getProdutoLoja().getProduto().getReferencia();
                // Adiciona o id do item do pedido
                conferenciaItemBeans.setIdItemsaida(listaItemSaidaSelecionado.get(i).getIdItemSaida());

                // Checa se nos paramentros esta permitido digitar a quantidade conferida
                if (funcoes.getValorXml("DigitaQuantidade").equalsIgnoreCase("S")) {

                    new MaterialDialog.Builder(getActivity())
                            .title(listaItemSaidaSelecionado.get(i).getEstoque().getProdutoLoja().getProduto().getDescricaoProduto())
                            .content(texto)
                            .inputRangeRes(1, 10, R.color.vermelho)
                            .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER)
                            .input(getContext().getResources().getString(R.string.digitar_quantidade), null, new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(MaterialDialog dialog, CharSequence input) {

                                    conferenciaItemBeans.setQuantidade(input.toString());

                                    ConferirItemAsyncTask conferirItemAsyncTask = new ConferirItemAsyncTask(conferenciaItemBeans);
                                    conferirItemAsyncTask.execute();
                                }
                            }).show();

                // Caso nao seja permitido digitar a quantidade permitida entao eh pego a quantidade retornada da pesquisa
                } else if (fatorProdutoPesquisado > 0){

                    conferenciaItemBeans.setQuantidade(""+fatorProdutoPesquisado);
                    ConferirItemAsyncTask conferirItemAsyncTask = new ConferirItemAsyncTask(conferenciaItemBeans);
                    conferirItemAsyncTask.execute();

                // Caso nao seja permitido digitar a quantidade permitida entao eh pego a quantidade total do item do pedido
                } else {
                    conferenciaItemBeans.setQuantidade(""+listaItemSaidaSelecionado.get(i).getQuantidade());

                    ConferirItemAsyncTask conferirItemAsyncTask = new ConferirItemAsyncTask(conferenciaItemBeans);
                    conferirItemAsyncTask.execute();
                }
            }
            // retira o fator do produto pesquisado
            fatorProdutoPesquisado = -1;
        }
    } // FIm marcarItemConferido


    private class LoaderLista extends AsyncTask<Void, Void, Void> {
        String where = "";
        // Cria uma vareavel para salvar a lista de nota fiscal de entrada
        List<NotaFiscalEntradaBeans> listaNotaFiscalEntrada;
        List<ItemNotaFiscalEntradaBeans> listaItemNotaFiscalEntrada;
        List<RomaneioBeans> listaRomaneio;
        List<ItemRomaneioBeans> listaItemRomaneio;
        List<SaidaBeans> listaSaida;
        List<ItemSaidaBeans> listaItemSaida;

        public LoaderLista(String where) {
            this.where = (where != null) ? where.toUpperCase() : null;

            if (tipoTela == ListaUniversalActivity.TELA_NOTA_FISCAL_ENTRADA) {
                listaNotaFiscalEntrada = new ArrayList<NotaFiscalEntradaBeans>();

            } else if (tipoTela == ListaUniversalActivity.TELA_ITEM_NOTA_FISCAL_ENTRADA) {
                listaItemNotaFiscalEntrada = new ArrayList<ItemNotaFiscalEntradaBeans>();

            } else if (tipoTela == ListaUniversalActivity.TELA_ROMANEIO) {
                listaRomaneio = new ArrayList<RomaneioBeans>();

            } else if (tipoTela == ListaUniversalActivity.TELA_ITEM_ROMANEIO){
                listaItemRomaneio = new ArrayList<ItemRomaneioBeans>();

            } else if (tipoTela == ListaUniversalActivity.TELA_PEDIDO){
                listaSaida = new ArrayList<SaidaBeans>();

            } else if (tipoTela == ListaUniversalActivity.TELA_ITEM_PEDIDO){
                listaItemSaida = new ArrayList<ItemSaidaBeans>();

            }
        }

        // Aqui eh o que acontece antes da tarefa principal ser executado
        @Override
        protected void onPreExecute() {
            // o progressBar agora eh setado como visivel
            progressBarStatus.setVisibility(View.VISIBLE);
            textMensagem.setVisibility(View.INVISIBLE);
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
                    // Checa se a lista de produtos nao esta vazia e nem nula
                    if ((listaNotaFiscalEntrada != null) && (listaNotaFiscalEntrada.size() > 0)) {
                        // Instancia o adapter e o seu tipo(produto)
                        adapterListagem = new ItemUniversalAdapter(getContext(), ItemUniversalAdapter.NOTA_FISCAL_ENTRADA);
                        // Seta a lista de produtos no adapter
                        adapterListagem.setListaNotaFiscalEntrada(listaNotaFiscalEntrada);

                    }

                // Checa a tela eh de itens de nota fiscal de entrada
                } else if (tipoTela == ListaUniversalActivity.TELA_ITEM_NOTA_FISCAL_ENTRADA){

                    // Checa se eh uma aba de itens da notas nao conferidos
                    if (nomeAba.contains("Conferir")){
                        listaItemNotaFiscalEntrada = notaFiscalEntradaRotinas.listaItemNotaFiscalEntrada(idEntrada,
                                                                                                         NotaFiscalEntradaRotinas.SIM,
                                                                                                         where,
                                                                                                         NotaFiscalEntradaRotinas.SEM_CONFERIR,
                                                                                                         progressBarStatus);
                    }else if (nomeAba.contains("Conferido")){
                        listaItemNotaFiscalEntrada = notaFiscalEntradaRotinas.listaItemNotaFiscalEntrada(idEntrada,
                                                                                                         NotaFiscalEntradaRotinas.SIM,
                                                                                                         where,
                                                                                                         NotaFiscalEntradaRotinas.CONFERIDO,
                                                                                                         progressBarStatus);
                    }
                    // Checa se a lista deta vazia
                    if ((listaItemNotaFiscalEntrada != null) && (listaItemNotaFiscalEntrada.size() > 0)){
                        // Instancia o adapter e o seu tipo(produto)
                        adapterListagem = new ItemUniversalAdapter(getContext(), ItemUniversalAdapter.ITEM_NOTA_FISCAL_ENTRADA);
                        // Seta a lista de produtos no adapter
                        adapterListagem.setListaItemNotaFiscalEntrada(listaItemNotaFiscalEntrada);

                    }

                // Checa se a tela eh de lista de romaneio
                } else if (tipoTela == ListaUniversalActivity.TELA_ROMANEIO){

                    RomaneioRotinas romaneioRotinas = new RomaneioRotinas(getContext());
                    // Informa o tipo de conexao com o banco de dados que eh para ser feito
                    romaneioRotinas.setTipoConexao((!funcoes.getValorXml("TipoConexao").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? funcoes.getValorXml("TipoConexao"): "I");

                    // Checa se eh uma aba de itens da notas nao conferidos
                    if (nomeAba.contains("Conferir")){

                        listaRomaneio = romaneioRotinas.listaRomaneio(where, RomaneioRotinas.SEM_CONFERIR, progressBarStatus);

                    } else if (nomeAba.contains("Conferido")){

                        listaRomaneio = romaneioRotinas.listaRomaneio(where, RomaneioRotinas.CONFERIDO, progressBarStatus);
                    }

                    if ( (listaRomaneio != null) && (listaRomaneio.size() > 0) ){
                        // Instancia o adapter para sair o tipo de listagem em romaneio
                        adapterListagem = new ItemUniversalAdapter(getContext(), ItemUniversalAdapter.LISTA_ROMANEIO);
                        // Seta a lista de romaneio
                        adapterListagem.setListaRomaneio(listaRomaneio);

                    }

                } else if (tipoTela == ListaUniversalActivity.TELA_ITEM_ROMANEIO){

                    RomaneioRotinas romaneioRotinas = new RomaneioRotinas(getContext());
                    // Informa o tipo de conexao com o banco de dados que eh para ser feito
                    romaneioRotinas.setTipoConexao((!funcoes.getValorXml("TipoConexao").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? funcoes.getValorXml("TipoConexao"): "I");

                    // Checa se eh uma aba de itens da notas nao conferidos
                    if (nomeAba.contains("Conferir")){

                        listaItemRomaneio = romaneioRotinas.listaItemRomaneio(idRomaneio, where, RomaneioRotinas.SEM_CONFERIR, progressBarStatus);

                    } else if (nomeAba.contains("Conferido")){

                        listaItemRomaneio = romaneioRotinas.listaItemRomaneio(idRomaneio, where, RomaneioRotinas.CONFERIDO, progressBarStatus);
                    }

                    if ( (listaItemRomaneio != null) && (listaItemRomaneio.size() > 0) ){
                        // Instancia o adapter para sair o tipo de listagem em romaneio
                        adapterListagem = new ItemUniversalAdapter(getContext(), ItemUniversalAdapter.LISTA_ITEM_ROMANEIO);
                        // Seta a lista de romaneio
                        adapterListagem.setListaItemRomaneio(listaItemRomaneio);
                    }

                } else if (tipoTela == ListaUniversalActivity.TELA_PEDIDO){
                    SaidaRotinas saidaRotinas = new SaidaRotinas(getContext());
                    // Informa o tipo de conexao com o banco de dados que eh para ser feito
                    saidaRotinas.setTipoConexao((!funcoes.getValorXml("TipoConexao").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? funcoes.getValorXml("TipoConexao"): "I");

                    // Checa se eh uma aba de itens da notas nao conferidos
                    if (nomeAba.contains("Conferir")){

                        listaSaida = saidaRotinas.listaSaida(where, SaidaRotinas.SEM_CONFERIR, progressBarStatus);

                    } else if (nomeAba.contains("Conferido")){

                        listaSaida = saidaRotinas.listaSaida(where, SaidaRotinas.CONFERIDO, progressBarStatus);
                    }

                    if ( (listaSaida != null) && (listaSaida.size() > 0) ){
                        // Instancia o adapter para sair o tipo de listagem em romaneio
                        adapterListagem = new ItemUniversalAdapter(getContext(), ItemUniversalAdapter.LISTA_PEDIDO);
                        // Seta a lista de romaneio
                        adapterListagem.setListaSaida(listaSaida);
                    }

                } else if (tipoTela == ListaUniversalActivity.TELA_ITEM_PEDIDO){
                    SaidaRotinas saidaRotinas = new SaidaRotinas(getContext());
                    // Informa o tipo de conexao com o banco de dados que eh para ser feito
                    saidaRotinas.setTipoConexao((!funcoes.getValorXml("TipoConexao").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)) ? funcoes.getValorXml("TipoConexao"): "I");

                    // Checa se eh uma aba de itens da notas nao conferidos
                    if (nomeAba.contains("Conferir")){

                        listaItemSaida = saidaRotinas.listaItemSaida(idSaida, SaidaRotinas.NAO, where, SaidaRotinas.SEM_CONFERIR, progressBarStatus);

                    } else if (nomeAba.contains("Conferido")){

                        listaItemSaida = saidaRotinas.listaItemSaida(idSaida, SaidaRotinas.NAO, where, SaidaRotinas.CONFERIDO, progressBarStatus);
                    }

                    if ( (listaItemSaida != null) && (listaItemSaida.size() > 0) ){
                        // Instancia o adapter para sair o tipo de listagem em romaneio
                        adapterListagem = new ItemUniversalAdapter(getContext(), ItemUniversalAdapter.LISTA_ITEM_PEDIDO);
                        // Seta a lista de romaneio
                        adapterListagem.setListaItemSaida(listaItemSaida);
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
            } else if (tipoTela == ListaUniversalActivity.TELA_ROMANEIO){
                if ( (listaRomaneio != null) && (listaRomaneio.size() > 0) ){
                    listViewListagem.setAdapter(adapterListagem);

                    // Checa se tem apenas um item na lista
                    if (listaRomaneio.size() == 1){
                        // Pega os dados do romaneio da lista
                        RomaneioBeans romaneio = (RomaneioBeans) listViewListagem.getItemAtPosition(0);

                        Bundle bundle = new Bundle();
                        bundle.putInt(ListaUniversalActivity.KEY_TIPO_TELA, ListaUniversalActivity.TELA_ITEM_ROMANEIO);
                        bundle.putInt("ID_AEAROMAN", romaneio.getIdRomaneio());

                        // Abre a tela de detalhes do produto
                        Intent intent = new Intent(getContext(), ListaUniversalActivity.class);
                        intent.putExtras(bundle);

                        startActivity(intent);
                    }

                } else {
                    listViewListagem.setVisibility(View.GONE);
                    textMensagem.setVisibility(View.VISIBLE);
                }
            } else if (tipoTela == ListaUniversalActivity.TELA_ITEM_ROMANEIO){
                if ( (listaItemRomaneio != null) && (listaItemRomaneio.size() > 0) ){
                    listViewListagem.setAdapter(adapterListagem);

                    // Checa se tem apenas um item na lista, se tiver apenas um item na lista entao abre outra tela com os dados do item
                    if (listaItemRomaneio.size() == 1){

                        ItemRomaneioBeans itemRomaneioBeans = (ItemRomaneioBeans) listViewListagem.getItemAtPosition(0);

                        Bundle bundle = new Bundle();
                        bundle.putInt(ListaUniversalActivity.KEY_TIPO_TELA, ListaUniversalActivity.TELA_ITEM_PEDIDO);
                        bundle.putInt("ID_AEASAIDA", itemRomaneioBeans.getSaida().getIdSaida());

                        // Abre a tela de detalhes do produto
                        Intent intent = new Intent(getContext(), ListaUniversalActivity.class);
                        intent.putExtras(bundle);

                        // Abre a nova tela
                        startActivity(intent);
                    }

                } else {
                    listViewListagem.setVisibility(View.GONE);
                    textMensagem.setVisibility(View.VISIBLE);
                }
            } else if (tipoTela == ListaUniversalActivity.TELA_PEDIDO){
                if ( (listaSaida != null) && (listaSaida.size() > 0) ){
                    listViewListagem.setAdapter(adapterListagem);

                    // Checa se tem apenas um item na lista, se tiver apenas um item na lista entao abre outra tela com os dados do item
                    if (listaSaida.size() == 1){
                        SaidaBeans saidaBeans = (SaidaBeans) listViewListagem.getItemAtPosition(0);

                        Bundle bundle = new Bundle();
                        bundle.putInt(ListaUniversalActivity.KEY_TIPO_TELA, ListaUniversalActivity.TELA_ITEM_PEDIDO);
                        bundle.putInt("ID_AEASAIDA", saidaBeans.getIdSaida());

                        // Abre a tela de detalhes do produto
                        Intent intent = new Intent(getContext(), ListaUniversalActivity.class);
                        intent.putExtras(bundle);

                        // Abre a nova tela
                        startActivity(intent);
                    }
                } else {
                    listViewListagem.setVisibility(View.GONE);
                    textMensagem.setVisibility(View.VISIBLE);
                }
            } else if (tipoTela == ListaUniversalActivity.TELA_ITEM_PEDIDO){
                if ( (listaItemSaida != null) && (listaItemSaida.size() > 0) ){
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

                        SuperToast.create(getContext(), getResources().getString(R.string.atualizado_sucesso), SuperToast.Duration.SHORT, Style.getStyle(Style.GREEN, SuperToast.Animations.FLYIN)).show();

                        // Atualiza a lista de notas de entrada
                        LoaderLista carregarListaNotas = new LoaderLista(null);
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


    private class ConferirItemAsyncTask extends  AsyncTask<Void, Void, Void>{

        private ConferenciaItemBeans conferenciaItemBeans;

        public ConferirItemAsyncTask(ConferenciaItemBeans conferenciaItemBeans) {
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
            if ( conferenciaItemRotinas.insertConferenciaItem(conferenciaItemBeans, progressBarStatus, null) ){

                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    public void run() {

                        SuperToast.create(getContext(), getResources().getString(R.string.conferencia_inserido_sucesso), SuperToast.Duration.LONG, Style.getStyle(Style.GREEN, SuperToast.Animations.FLYIN)).show();


                        // Emite um som de positivo
                        MediaPlayer somSucesso = MediaPlayer.create(getContext(), R.raw.effect_alert_positive);
                        somSucesso.start();
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

            // Desmonta toda a lista de selecionados
            if (listaItemSaidaSelecionado != null){
                listaItemSaidaSelecionado = null;
            }

            LoaderLista carregarLista = new LoaderLista(null);
            carregarLista.execute();

        }

    } // Fim ConferirItemAsyncTask

}
