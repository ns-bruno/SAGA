package com.saga.beans;

/**
 * Created by Bruno Nogueira Silva on 25/01/2016.
 */
public class ItemNotaFiscalEntradaBeans {

    private int idItemNotaFiscalEntrada, idEntrada, idItemNotaFiscalSaida, idItemSaida, idItemPedidoCompras, sequencia;
    private UnidadeVendaBeans unidadeVenda;
    private EstoqueBeans estoque;
    private CodigoOrigemProdutoBeans codigoOrigem;
    private String guidItemEntrada, dataCadastro, dataAlteracao, dataEntrada, tipo, tipoProduto, tipoBaixa, observacao;
    private double quantidade, quantidadeDataValidade, quantidadeConferido, valorMercadoria, unitarioItemMercadoria, totalItem;

    public int getIdItemNotaFiscalEntrada() {
        return idItemNotaFiscalEntrada;
    }

    public void setIdItemNotaFiscalEntrada(int idItemNotaFiscalEntrada) {
        this.idItemNotaFiscalEntrada = idItemNotaFiscalEntrada;
    }

    public int getIdEntrada() {
        return idEntrada;
    }

    public void setIdEntrada(int idEntrada) {
        this.idEntrada = idEntrada;
    }

    public int getIdItemNotaFiscalSaida() {
        return idItemNotaFiscalSaida;
    }

    public void setIdItemNotaFiscalSaida(int idItemNotaFiscalSaida) {
        this.idItemNotaFiscalSaida = idItemNotaFiscalSaida;
    }

    public int getIdItemSaida() {
        return idItemSaida;
    }

    public void setIdItemSaida(int idItemSaida) {
        this.idItemSaida = idItemSaida;
    }

    public int getIdItemPedidoCompras() {
        return idItemPedidoCompras;
    }

    public void setIdItemPedidoCompras(int idItemPedidoCompras) {
        this.idItemPedidoCompras = idItemPedidoCompras;
    }

    public int getSequencia() {
        return sequencia;
    }

    public void setSequencia(int sequencia) {
        this.sequencia = sequencia;
    }

    public UnidadeVendaBeans getUnidadeVenda() {
        return unidadeVenda;
    }

    public void setUnidadeVenda(UnidadeVendaBeans unidadeVenda) {
        this.unidadeVenda = unidadeVenda;
    }

    public EstoqueBeans getEstoque() {
        return estoque;
    }

    public void setEstoque(EstoqueBeans estoque) {
        this.estoque = estoque;
    }

    public CodigoOrigemProdutoBeans getCodigoOrigem() {
        return codigoOrigem;
    }

    public void setCodigoOrigem(CodigoOrigemProdutoBeans codigoOrigem) {
        this.codigoOrigem = codigoOrigem;
    }

    public String getGuidItemEntrada() {
        return guidItemEntrada;
    }

    public void setGuidItemEntrada(String guidItemEntrada) {
        this.guidItemEntrada = guidItemEntrada;
    }

    public String getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(String dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(String dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public String getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(String dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTipoProduto() {
        return tipoProduto;
    }

    public void setTipoProduto(String tipoProduto) {
        this.tipoProduto = tipoProduto;
    }

    public String getTipoBaixa() {
        return tipoBaixa;
    }

    public void setTipoBaixa(String tipoBaixa) {
        this.tipoBaixa = tipoBaixa;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public double getQuantidadeDataValidade() {
        return quantidadeDataValidade;
    }

    public void setQuantidadeDataValidade(double quantidadeDataValidade) {
        this.quantidadeDataValidade = quantidadeDataValidade;
    }

    public double getQuantidadeConferido() {
        return quantidadeConferido;
    }

    public void setQuantidadeConferido(double quantidadeConferido) {
        this.quantidadeConferido = quantidadeConferido;
    }

    public double getValorMercadoria() {
        return valorMercadoria;
    }

    public void setValorMercadoria(double valorMercadoria) {
        this.valorMercadoria = valorMercadoria;
    }

    public double getUnitarioItemMercadoria() {
        return unitarioItemMercadoria;
    }

    public void setUnitarioItemMercadoria(double unitarioItemMercadoria) {
        this.unitarioItemMercadoria = unitarioItemMercadoria;
    }

    public double getTotalItem() {
        return totalItem;
    }

    public void setTotalItem(double totalItem) {
        this.totalItem = totalItem;
    }
}
