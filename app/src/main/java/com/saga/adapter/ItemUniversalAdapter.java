package com.saga.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.saga.R;
import com.saga.activity.CadastroEmbalagemActivity;
import com.saga.activity.LocacaoProdutoActivity;
import com.saga.beans.AtivoBeans;
import com.saga.beans.EmbalagemBeans;
import com.saga.beans.EstoqueBeans;
import com.saga.beans.ItemNotaFiscalEntradaBeans;
import com.saga.beans.LocesBeans;
import com.saga.beans.NotaFiscalEntradaBeans;
import com.saga.beans.ProdutoBeans;
import com.saga.beans.ProdutoLojaBeans;
import com.saga.beans.UnidadeVendaBeans;
import com.saga.funcoes.FuncoesPersonalizadas;

import java.util.List;
import java.util.zip.InflaterInputStream;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Bruno Nogueira Silva on 19/01/2016.
 */
public class ItemUniversalAdapter extends BaseAdapter {

    public static final int NOTA_FISCAL_ENTRADA = 0,
                            ITEM_NOTA_FISCAL_ENTRADA = 1,
                            EMBALAGEM = 2,
                            UNIDADE_VENDA = 3,
                            LISTA_PRODUTO = 5,
                            ATIVO = 4,
                            LISTA_PRODUTO_LOJA = 6,
                            LISTA_ESTOQUE = 7,
                            LISTA_LOCES = 8;
    private Context context;
    private int tipoItem;
    private List<NotaFiscalEntradaBeans> listaNotaFiscalEntrada;
    private List<ItemNotaFiscalEntradaBeans> listaItemNotaFiscalEntrada;
    private List<EmbalagemBeans> listaEmbalagem;
    private List<UnidadeVendaBeans> listaUnidadeVenda;
    private List<AtivoBeans> listaAtivo;
    private List<ProdutoBeans> listaProduto;
    private List<ProdutoLojaBeans> listaProdutoLoja;
    private List<EstoqueBeans> listaEstoque;
    private List<LocesBeans> listaLoces;

    public ItemUniversalAdapter(Context context, int tipoItem) {
        this.context = context;
        this.tipoItem = tipoItem;
    }

    public List<NotaFiscalEntradaBeans> getListaNotaFiscalEntrada() {
        return listaNotaFiscalEntrada;
    }

    public void setListaNotaFiscalEntrada(List<NotaFiscalEntradaBeans> listaNotaFiscalEntrada) {
        this.listaNotaFiscalEntrada = listaNotaFiscalEntrada;
    }

    public List<ItemNotaFiscalEntradaBeans> getListaItemNotaFiscalEntrada() {
        return listaItemNotaFiscalEntrada;
    }

    public void setListaItemNotaFiscalEntrada(List<ItemNotaFiscalEntradaBeans> listaItemNotaFiscalEntrada) {
        this.listaItemNotaFiscalEntrada = listaItemNotaFiscalEntrada;
    }

    public List<EmbalagemBeans> getListaEmbalagem() {
        return listaEmbalagem;
    }

    public void setListaEmbalagem(List<EmbalagemBeans> listaEmbalagem) {
        this.listaEmbalagem = listaEmbalagem;
    }

    public List<UnidadeVendaBeans> getListaUnidadeVenda() {
        return listaUnidadeVenda;
    }

    public void setListaUnidadeVenda(List<UnidadeVendaBeans> listaUnidadeVenda) {
        this.listaUnidadeVenda = listaUnidadeVenda;
    }

    public List<AtivoBeans> getListaAtivo() {
        return listaAtivo;
    }

    public void setListaAtivo(List<AtivoBeans> listaAtivo) {
        this.listaAtivo = listaAtivo;
    }

    public List<ProdutoBeans> getListaProduto() {
        return listaProduto;
    }

    public void setListaProduto(List<ProdutoBeans> listaProduto) {
        this.listaProduto = listaProduto;
    }

    public List<ProdutoLojaBeans> getListaProdutoLoja() {
        return listaProdutoLoja;
    }

    public void setListaProdutoLoja(List<ProdutoLojaBeans> listaProdutoLoja) {
        this.listaProdutoLoja = listaProdutoLoja;
    }

    public List<EstoqueBeans> getListaEstoque() {
        return listaEstoque;
    }

    public void setListaEstoque(List<EstoqueBeans> listaEstoque) {
        this.listaEstoque = listaEstoque;
    }

    public List<LocesBeans> getListaLoces() {
        return listaLoces;
    }

    public void setListaLoces(List<LocesBeans> listaLoces) {
        this.listaLoces = listaLoces;
    }

    @Override
    public int getCount() {
        // Verifica o tipo de item
        if(this.tipoItem == NOTA_FISCAL_ENTRADA){
            // Retorna a quantidade de item de orcamento da lista
            return listaNotaFiscalEntrada.size();

        } else if(this.tipoItem == ITEM_NOTA_FISCAL_ENTRADA){
            // Retorna a quantidade de item de orcamento da lista
            return listaItemNotaFiscalEntrada.size();

        } else if(this.tipoItem == EMBALAGEM){
            return listaEmbalagem.size();

        } else if (this.tipoItem == UNIDADE_VENDA){
            return listaUnidadeVenda.size();

        } else if (this.tipoItem == ATIVO){
            return listaAtivo.size();

        } else if (this.tipoItem == LISTA_PRODUTO){
            return listaProduto.size();

        } else if (this.tipoItem == LISTA_PRODUTO_LOJA){
            return listaProdutoLoja.size();

        } else if (this.tipoItem == LISTA_ESTOQUE){
            return listaEstoque.size();

        } else if (this.tipoItem == LISTA_LOCES){
            return listaLoces.size();

        }
        else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        // Verifica o tipo de item
        if(this.tipoItem == NOTA_FISCAL_ENTRADA){
            // Retorna a quantidade de item de orcamento da lista
            return listaNotaFiscalEntrada.get(position);

        } else if(this.tipoItem == ITEM_NOTA_FISCAL_ENTRADA){
            // Retorna a quantidade de item de orcamento da lista
            return listaItemNotaFiscalEntrada.get(position);

        } else if (this.tipoItem == EMBALAGEM){
            return listaEmbalagem.get(position);

        } else if (this.tipoItem == UNIDADE_VENDA){
            return listaUnidadeVenda.get(position);

        } else if (this.tipoItem == ATIVO){
            return listaAtivo.get(position);

        } else if (this.tipoItem == LISTA_PRODUTO){
            return listaProduto.get(position);

        } else if (this.tipoItem == LISTA_PRODUTO_LOJA){
            return listaProdutoLoja.get(position);

        } else if (this.tipoItem == LISTA_ESTOQUE){
            return listaEstoque.get(position);

        } else if (this.tipoItem == LISTA_LOCES){
            return listaLoces.get(position);

        }
        else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        // Verifica o tipo de item
        if(this.tipoItem == NOTA_FISCAL_ENTRADA){
            // Retorna a quantidade de item de orcamento da lista
            return listaNotaFiscalEntrada.get(position).getIdNotaFiscalEntrada();

        } else if(this.tipoItem == ITEM_NOTA_FISCAL_ENTRADA){
            // Retorna a quantidade de item de orcamento da lista
            return listaItemNotaFiscalEntrada.get(position).getIdItemNotaFiscalEntrada();

        } else if (this.tipoItem == EMBALAGEM){
            return listaEmbalagem.get(position).getIdEmbalagem();

        } else if (this.tipoItem == UNIDADE_VENDA){
            return listaUnidadeVenda.get(position).getIdUnidadeVenda();

        } else if (this.tipoItem == ATIVO){
            return position;

        } else if (this.tipoItem == LISTA_PRODUTO){
            return listaProduto.get(position).getIdProduto();

        } else if (this.tipoItem == LISTA_PRODUTO_LOJA){
            return listaProdutoLoja.get(position).getIdProdutoLoja();

        } else if (this.tipoItem == LISTA_ESTOQUE){
            return listaEstoque.get(position).getIdEstoque();

        } else if (this.tipoItem == LISTA_LOCES){
            return listaLoces.get(position).getIdLoces();

        }
        else {
            return position;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;

			/*
			 * Recupera o servico LayoutInflater que eh o servidor que ira
			 * transformar o nosso layout layout_pessoa em uma View
			 */
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			/*
			 * Converte nosso layout em uma view
			 */
        view = inflater.inflate(R.layout.layout_item_universal, null);

        /**
         * Recupero os compoentes que estao dentro do layout_item_universal
         */
        TextView textDescricao = (TextView) view.findViewById(R.id.layout_item_universal_text_descricao);
        TextView textAbaixoDescricaoEsqueda = (TextView) view.findViewById(R.id.layout_item_universal_text_abaixo_descricao_esquerda);
        TextView textAbaixoDescricaoDireita = (TextView) view.findViewById(R.id.layout_item_universal_text_abaixo_descricao_direita);
        TextView textBottonEsquerdo = (TextView) view.findViewById(R.id.layout_item_universal_text_botton_esquerdo);
        TextView textBottonEsquerdoDois = (TextView) view.findViewById(R.id.layout_item_universal_text_botton_esquerdo_dois);
        TextView textBottonDireito = (TextView) view.findViewById(R.id.layout_item_universal_text_botton_direito);
        View viewTopo = (View) view.findViewById(R.id.layout_item_universal_view_topo);
        View viewRodape = (View) view.findViewById(R.id.layout_item_universal_view_rodape);
        ImageView imageOpcao = (ImageView) view.findViewById(R.id.layout_item_universal_imageView_opcao);
        CircleImageView imageCirclePrincipal = (CircleImageView) view.findViewById(R.id.layout_item_universal_profile_image);

        FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(context);

        // Checa se o tamanho da fonte eh grante
        if(funcoes.getValorXml("TamanhoFonte").equalsIgnoreCase("G")){

            textDescricao.setTextAppearance(context, R.style.textoGrante);
            textAbaixoDescricaoEsqueda.setTextAppearance(context, R.style.textoGrante);
            textAbaixoDescricaoDireita.setTextAppearance(context, R.style.textoGrante);
            textBottonEsquerdo.setTextAppearance(context, R.style.textoGrante);
            textBottonEsquerdoDois.setTextAppearance(context, R.style.textoGrante);
            textBottonDireito.setTextAppearance(context, R.style.textoGrante);

            // Checa se o tamanho da fonte eh grante
        } else if(funcoes.getValorXml("TamanhoFonte").equalsIgnoreCase("M")){
            textDescricao.setTextAppearance(context, R.style.textoMendio);
            textAbaixoDescricaoEsqueda.setTextAppearance(context, R.style.textoMendio);
            textAbaixoDescricaoDireita.setTextAppearance(context, R.style.textoMendio);
            textBottonEsquerdo.setTextAppearance(context, R.style.textoMendio);
            textBottonEsquerdoDois.setTextAppearance(context, R.style.textoMendio);
            textBottonDireito.setTextAppearance(context, R.style.textoMendio);
        }
        try {
            if (this.tipoItem == NOTA_FISCAL_ENTRADA) {


                String obs = ( (listaNotaFiscalEntrada.get(position).getObservacao() != null) && (listaNotaFiscalEntrada.get(position).getObservacao().length() > 0) ) ?
                                (listaNotaFiscalEntrada.get(position).getObservacao().replace("TRANSFERENCIA REFERENTE", "")) : "" ;

                // Checa se retornou o campos com anyType{}
                if (obs.contains("anyType{}")){
                    //  Remove o anyType{}
                    obs = obs.replace("anyType{}", "");
                }

                int totalCaracObs = ( (obs != null) && (obs.length() >= 30) ) ? 30 : obs.length();

                textDescricao.setText(listaNotaFiscalEntrada.get(position).getClifo().getNomeRazao() + " (" +
                                      listaNotaFiscalEntrada.get(position).getClifo().getNomeFantasia().replace("anyType{}", "") + ") " +
                                     ( (obs != null && obs.length() > 0) ? obs.substring(0, totalCaracObs) : "") );

                textAbaixoDescricaoEsqueda.setText("Nº: " + listaNotaFiscalEntrada.get(position).getNumeroEntrada());
                textAbaixoDescricaoDireita.setText("Dt. Ent.: " + funcoes.formataData(listaNotaFiscalEntrada.get(position).getDataEntrada()));
                textBottonEsquerdo.setText((listaNotaFiscalEntrada.get(position).getClifo().getCidadeClifo() != null) ?
                                            listaNotaFiscalEntrada.get(position).getClifo().getCidadeClifo().getDescricaoCidade() : "");
                if (listaNotaFiscalEntrada.get(position).getFinalidade().contains("4")){
                    textBottonEsquerdoDois.setText(" | " + context.getResources().getString(R.string.devolucao));
                } else {
                    textBottonEsquerdoDois.setText(" | " + listaNotaFiscalEntrada.get(position).getNatureza().getDescricaoNatureza());
                }
                textBottonDireito.setText("Total: " + funcoes.arredondarValor(listaNotaFiscalEntrada.get(position).getValorTotalEntrada()));

                if(listaNotaFiscalEntrada.get(position).isTagSelectContext()){
                    view.setBackgroundColor(context.getResources().getColor(android.R.color.background_light));
                    textDescricao.setTypeface(null, Typeface.BOLD);
                    textAbaixoDescricaoEsqueda.setTypeface(null, Typeface.BOLD);
                    textAbaixoDescricaoDireita.setTypeface(null, Typeface.BOLD);
                    textBottonDireito.setTypeface(null, Typeface.BOLD);
                    textBottonEsquerdo.setTypeface(null, Typeface.BOLD);
                    textBottonEsquerdoDois.setTypeface(null, Typeface.BOLD);

                } else {
                    view.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
                    textDescricao.setTypeface(null, Typeface.NORMAL);
                    textAbaixoDescricaoEsqueda.setTypeface(null, Typeface.NORMAL);
                    textAbaixoDescricaoDireita.setTypeface(null, Typeface.NORMAL);
                    textBottonDireito.setTypeface(null, Typeface.NORMAL);
                    textBottonEsquerdo.setTypeface(null, Typeface.NORMAL);
                    textBottonEsquerdoDois.setTypeface(null, Typeface.NORMAL);
                }

                viewTopo.setVisibility(View.INVISIBLE);
                viewRodape.setVisibility(View.INVISIBLE);

            } else if (this.tipoItem == ITEM_NOTA_FISCAL_ENTRADA){

                textDescricao.setText(listaItemNotaFiscalEntrada.get(position).getEstoque().getProdutoLoja().getProduto().getDescricaoProduto() +
                                       " - " + listaItemNotaFiscalEntrada.get(position).getEstoque().getProdutoLoja().getProduto().getMarca().getDescricao() );
                textAbaixoDescricaoEsqueda.setText("Cod.: " + listaItemNotaFiscalEntrada.get(position).getEstoque().getProdutoLoja().getProduto().getCodigoEstrutural());
                textAbaixoDescricaoDireita.setText("Ref.: " + listaItemNotaFiscalEntrada.get(position).getEstoque().getProdutoLoja().getProduto().getReferencia());
                textBottonEsquerdo.setText(listaItemNotaFiscalEntrada.get(position).getUnidadeVenda().getSigla());
                textBottonEsquerdoDois.setText(" | " + funcoes.arredondarValor(listaItemNotaFiscalEntrada.get(position).getQuantidade()) +
                        (( (listaItemNotaFiscalEntrada.get(position).getQuantidadeConferido() > 0) ) ?
                                                      " | C." + funcoes.arredondarValor(listaItemNotaFiscalEntrada.get(position).getQuantidadeConferido()) : "") );
                textBottonDireito.setText("Est.: " + funcoes.arredondarValor(listaItemNotaFiscalEntrada.get(position).getEstoque().getEstoque()));

                // Checa se este item ja foi conferido
                if (listaItemNotaFiscalEntrada.get(position).getQuantidadeConferido() == listaItemNotaFiscalEntrada.get(position).getQuantidade()){

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        viewTopo.setBackgroundColor(context.getColor(R.color.verde));
                    } else {
                        viewTopo.setBackgroundColor(context.getResources().getColor(R.color.verde));
                    }
                } else if ((listaItemNotaFiscalEntrada.get(position).getQuantidadeConferido() < listaItemNotaFiscalEntrada.get(position).getQuantidade()) &&
                    (listaItemNotaFiscalEntrada.get(position).getQuantidadeConferido() > 0)){

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        viewTopo.setBackgroundColor(context.getColor(R.color.amarelo));
                    } else {
                        viewTopo.setBackgroundColor(context.getResources().getColor(R.color.amarelo));
                    }
                } else if ((listaItemNotaFiscalEntrada.get(position).getQuantidadeConferido() > listaItemNotaFiscalEntrada.get(position).getQuantidade())) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        viewTopo.setBackgroundColor(context.getColor(R.color.vermelho));
                    } else {
                        viewTopo.setBackgroundColor(context.getResources().getColor(R.color.vermelho));
                    }

                } else{
                    viewTopo.setVisibility(View.INVISIBLE);
                }
                viewRodape.setVisibility(View.INVISIBLE);

                if(listaItemNotaFiscalEntrada.get(position).isTagSelectContext()){
                    view.setBackgroundColor(context.getResources().getColor(android.R.color.background_light));
                    textDescricao.setTypeface(null, Typeface.BOLD);
                    textAbaixoDescricaoEsqueda.setTypeface(null, Typeface.BOLD);
                    textAbaixoDescricaoDireita.setTypeface(null, Typeface.BOLD);
                    textBottonDireito.setTypeface(null, Typeface.BOLD);
                    textBottonEsquerdo.setTypeface(null, Typeface.BOLD);
                    textBottonEsquerdoDois.setTypeface(null, Typeface.BOLD);

                } else {
                    view.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
                    textDescricao.setTypeface(null, Typeface.NORMAL);
                    textAbaixoDescricaoEsqueda.setTypeface(null, Typeface.NORMAL);
                    textAbaixoDescricaoDireita.setTypeface(null, Typeface.NORMAL);
                    textBottonDireito.setTypeface(null, Typeface.NORMAL);
                    textBottonEsquerdo.setTypeface(null, Typeface.NORMAL);
                    textBottonEsquerdoDois.setTypeface(null, Typeface.NORMAL);
                }

            } else if (this.tipoItem == EMBALAGEM){
                textDescricao.setText(listaEmbalagem.get(position).getUnidadeVenda().getSigla() + " | " + listaEmbalagem.get(position).getDescricaoEmbalagem());
                textAbaixoDescricaoEsqueda.setText("Módulo: " + listaEmbalagem.get(position).getModulo());
                textAbaixoDescricaoDireita.setText("Fator Conv.:" + listaEmbalagem.get(position).getFatorConversao());
                textBottonEsquerdo.setText("Ref.: " + ((listaEmbalagem.get(position).getReferencia() != null) ? listaEmbalagem.get(position).getReferencia() : ""));
                textBottonEsquerdoDois.setText(" | " + ((listaEmbalagem.get(position).getAtivo().equalsIgnoreCase("1")) ? "Ativo" : "Inativo"));
                textBottonDireito.setText(listaEmbalagem.get(position).getCodigoBarras());

                viewRodape.setVisibility(View.INVISIBLE);
                viewTopo.setVisibility(View.INVISIBLE);

            } else if (this.tipoItem == UNIDADE_VENDA){
                textDescricao.setText(listaUnidadeVenda.get(position).getDescricaoUnidadeVenda());
                textAbaixoDescricaoEsqueda.setText(listaUnidadeVenda.get(position).getSigla());

                textAbaixoDescricaoDireita.setVisibility(View.GONE);
                textBottonDireito.setVisibility(View.GONE);
                textBottonEsquerdo.setVisibility(View.GONE);
                textBottonEsquerdoDois.setVisibility(View.GONE);
                viewRodape.setVisibility(View.GONE);
                viewTopo.setVisibility(View.GONE);

            } else if (this.tipoItem == ATIVO){

                if (listaAtivo.get(position).getSimNao().equalsIgnoreCase("0")){
                    textDescricao.setText("Sim");
                } else {
                    textDescricao.setText("Não");
                }

                textAbaixoDescricaoEsqueda.setVisibility(View.GONE);
                textAbaixoDescricaoDireita.setVisibility(View.GONE);
                textBottonDireito.setVisibility(View.GONE);
                textBottonEsquerdo.setVisibility(View.GONE);
                textBottonEsquerdoDois.setVisibility(View.GONE);
                viewRodape.setVisibility(View.GONE);
                viewTopo.setVisibility(View.GONE);

            } else if (this.tipoItem == LISTA_PRODUTO){

                textDescricao.setText(listaProduto.get(position).getDescricaoProduto() + " - " + listaProduto.get(position).getMarca().getDescricao());
                textAbaixoDescricaoEsqueda.setText("Cód. " + listaProduto.get(position).getCodigoEstrutural());
                textAbaixoDescricaoDireita.setText("Ref. " + listaProduto.get(position).getReferencia());
                textBottonEsquerdoDois.setText(listaProduto.get(position).getUnidadeVenda().getSigla());

                // Checa se tem alguma imagem salva no produto
                if ( (funcoes.getValorXml("ImagemProduto").equalsIgnoreCase("S")) &&
                     (listaProduto.get(position).getImagemProduto() != null) &&
                     (listaProduto.get(position).getImagemProduto().getFotos() != null)){

                    // Torna o campo da imagem do produto visivel
                    imageCirclePrincipal.setVisibility(View.VISIBLE);
                    // Pega a imagem que esta no banco de dados
                    imageCirclePrincipal.setImageBitmap(listaProduto.get(position).getImagemProduto().getImagem());
                }
                imageOpcao.setVisibility(View.VISIBLE);


            } else if (this.tipoItem == LISTA_PRODUTO_LOJA){

                textDescricao.setText(listaProdutoLoja.get(position).getProduto().getDescricaoProduto() + " - " + listaProdutoLoja.get(position).getProduto().getMarca().getDescricao());
                textAbaixoDescricaoEsqueda.setText("Cód. " + listaProdutoLoja.get(position).getProduto().getCodigoEstrutural());
                textAbaixoDescricaoDireita.setText("Ref. " + listaProdutoLoja.get(position).getProduto().getReferencia());
                textBottonEsquerdo.setText(listaProdutoLoja.get(position).getProduto().getCodigoBarras());
                textBottonEsquerdoDois.setText(listaProdutoLoja.get(position).getProduto().getUnidadeVenda().getSigla());
                textBottonDireito.setText("Est. " + funcoes.arredondarValor(listaProdutoLoja.get(position).getEstoqueFisico()));

                // Checa se tem alguma imagem salva no produto
                if ( (funcoes.getValorXml("ImagemProduto").equalsIgnoreCase("S")) &&
                        (listaProdutoLoja.get(position).getProduto().getImagemProduto() != null) &&
                        (listaProdutoLoja.get(position).getProduto().getImagemProduto().getFotos() != null)){

                    // Torna o campo da imagem do produto visivel
                    imageCirclePrincipal.setVisibility(View.VISIBLE);
                    // Pega a imagem que esta no banco de dados
                    imageCirclePrincipal.setImageBitmap(listaProdutoLoja.get(position).getProduto().getImagemProduto().getImagem());
                }

                // Verifica se o estoque eh menor ou igual a que zero
                if(listaProdutoLoja.get(position).getEstoqueFisico() < 1){
                    textBottonDireito.setTextColor(context.getResources().getColor(R.color.vermelho));
                    //textBottonDireito.setTag(produto.getEstoqueFisico());
                }

                // Verifica se tem estoque contabil
                if(listaProdutoLoja.get(position).getEstoqueContabil() < 1){
                    viewRodape.setBackgroundColor(context.getResources().getColor(R.color.vermelho_escuro));
                    viewRodape.setTag(listaProdutoLoja.get(position).getEstoqueContabil());
                }else{
                    viewRodape.setVisibility(View.INVISIBLE);
                }
                viewTopo.setVisibility(View.INVISIBLE);

                if(listaProdutoLoja.get(position).isTagSelectContext()){
                    view.setBackgroundColor(context.getResources().getColor(android.R.color.background_light));
                    textDescricao.setTypeface(null, Typeface.BOLD);
                    textAbaixoDescricaoEsqueda.setTypeface(null, Typeface.BOLD);
                    textAbaixoDescricaoDireita.setTypeface(null, Typeface.BOLD);
                    textBottonDireito.setTypeface(null, Typeface.BOLD);
                    textBottonEsquerdo.setTypeface(null, Typeface.BOLD);
                    textBottonEsquerdoDois.setTypeface(null, Typeface.BOLD);

                } else {
                    view.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
                    textDescricao.setTypeface(null, Typeface.NORMAL);
                    textAbaixoDescricaoEsqueda.setTypeface(null, Typeface.NORMAL);
                    textAbaixoDescricaoDireita.setTypeface(null, Typeface.NORMAL);
                    textBottonDireito.setTypeface(null, Typeface.NORMAL);
                    textBottonEsquerdo.setTypeface(null, Typeface.NORMAL);
                    textBottonEsquerdoDois.setTypeface(null, Typeface.NORMAL);
                }
                imageOpcao.setVisibility(View.VISIBLE);

            } else if (this.tipoItem == LISTA_ESTOQUE){

                textDescricao.setText(listaEstoque.get(position).getLoces().getDescricaoLoces());
                textAbaixoDescricaoEsqueda.setText("Cod. " + listaEstoque.get(position).getLoces().getCodigo());
                textAbaixoDescricaoDireita.setText("Est. " + funcoes.arredondarValor(listaEstoque.get(position).getEstoque()));
                textBottonEsquerdo.setText("Retido: " + funcoes.arredondarValor(listaEstoque.get(position).getRetido()));

                if (listaEstoque.get(position).getEstoque() <= 0){
                    viewRodape.setBackgroundColor(context.getResources().getColor(R.color.vermelho_escuro));

                } else {
                    viewRodape.setVisibility(View.INVISIBLE);
                }

                textBottonEsquerdoDois.setVisibility(View.GONE);
                textBottonDireito.setVisibility(View.GONE);
                viewTopo.setVisibility(View.INVISIBLE);


            } else if (this.tipoItem == LISTA_LOCES){

                textDescricao.setText(listaLoces.get(position). getDescricaoLoces());
                textAbaixoDescricaoEsqueda.setText("Cod. " + listaLoces.get(position).getCodigo());

                if (listaLoces.get(position).getTipoVenda().equalsIgnoreCase("0")) {
                    textAbaixoDescricaoDireita.setText("Tipo Venda: Atacado");

                } else if (listaLoces.get(position).getTipoVenda().equalsIgnoreCase("1")) {
                    textAbaixoDescricaoDireita.setText("Tipo Venda: Varejo");

                } else if (listaLoces.get(position).getTipoVenda().equalsIgnoreCase("2")) {
                    textAbaixoDescricaoDireita.setText("Tipo Venda: Todos");
                }

                textBottonDireito.setVisibility(View.INVISIBLE);
                textBottonEsquerdo.setVisibility(View.INVISIBLE);
                textBottonEsquerdoDois.setVisibility(View.INVISIBLE);
                viewRodape.setVisibility(View.INVISIBLE);
                viewTopo.setVisibility(View.INVISIBLE);
            }

            if ( (tipoItem == LISTA_PRODUTO) || (tipoItem == LISTA_PRODUTO_LOJA) ){
                imageOpcao.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Checa se eh uma lista de produtos
                        if ( (tipoItem == LISTA_PRODUTO) || (tipoItem == LISTA_PRODUTO_LOJA) ) {
                            // Mostra um menu popup
                            showPopup(v, position);
                        }
                    }
                });
            }

        }catch (Exception e){
            // Armazena as informacoes para para serem exibidas e enviadas
            ContentValues contentValues = new ContentValues();
            contentValues.put("comando", 0);
            contentValues.put("tela", "ItemUniversalAdapter");
            contentValues.put("mensagem", funcoes.tratamentoErroBancoDados(e.getMessage()));
            contentValues.put("dados", e.toString());
            // Pega os dados do usuario
            funcoes = new FuncoesPersonalizadas(context);
            contentValues.put("usuario", funcoes.getValorXml("Usuario"));
            contentValues.put("empresa", funcoes.getValorXml("ChaveEmpresa"));
            contentValues.put("email", funcoes.getValorXml("Email"));

            funcoes.menssagem(contentValues);
        }
        return view;
    }

    public void showPopup(View v, final int posicao) {
        PopupMenu popup = new PopupMenu(context, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_lista_produto_context, popup.getMenu());
        popup.show();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.menu_lista_produto_context_locacao_produto:

                        ProdutoBeans produto;
                        ProdutoLojaBeans produtoLoja;

                        Intent intent = new Intent(context, LocacaoProdutoActivity.class);

                        if (tipoItem == LISTA_PRODUTO){

                            produto = (ProdutoBeans) getItem(posicao);
                            intent.putExtra(LocacaoProdutoActivity.KEY_CODIGO_ESTRUTURAL, produto.getCodigoEstrutural());

                        } else if (tipoItem == LISTA_PRODUTO_LOJA){

                            produtoLoja = (ProdutoLojaBeans) getItem(posicao);
                            intent.putExtra(LocacaoProdutoActivity.KEY_CODIGO_ESTRUTURAL, produtoLoja.getProduto().getCodigoEstrutural());
                        }
                        context.startActivity(intent);
                        break;

                    default:
                        break;
                }


                return true;
            }
        });
    }
}
