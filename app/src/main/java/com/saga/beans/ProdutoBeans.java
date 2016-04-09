package com.saga.beans;

/**
 * Created by Bruno Nogueira Silva on 25/01/2016.
 */
public class ProdutoBeans {

    private int idProduto, codigo;
    private FamiliaProdutoBeans familia;
    private ClasseProdutosBeans classe;
    private GrupoProdutoBeans grupo;
    private SubGrupoProdutoBeans subGrupo;
    private MarcaBeans marca;
    private UnidadeVendaBeans unidadeVenda;
    private TipoGradeBeans tipoGrade;
    private CodigoOrigemProdutoBeans codigoOrigem;
    private String guid, dataAlt, descricaoProduto, descricaoAuxiliar, mascara, codigoEstrutural,
                   referencia, codigoBarras, ativo, tipoProduto, genero, validade, garantia, controleSerial,
                   tipoItem, romaneia;
    private double pesoLiquido, pesoBruto;
    private FotosBeans imagemProduto;

    public int getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public FamiliaProdutoBeans getFamilia() {
        return familia;
    }

    public void setFamilia(FamiliaProdutoBeans familia) {
        this.familia = familia;
    }

    public ClasseProdutosBeans getClasse() {
        return classe;
    }

    public void setClasse(ClasseProdutosBeans classe) {
        this.classe = classe;
    }

    public GrupoProdutoBeans getGrupo() {
        return grupo;
    }

    public void setGrupo(GrupoProdutoBeans grupo) {
        this.grupo = grupo;
    }

    public SubGrupoProdutoBeans getSubGrupo() {
        return subGrupo;
    }

    public void setSubGrupo(SubGrupoProdutoBeans subGrupo) {
        this.subGrupo = subGrupo;
    }

    public MarcaBeans getMarca() {
        return marca;
    }

    public void setMarca(MarcaBeans marca) {
        this.marca = marca;
    }

    public UnidadeVendaBeans getUnidadeVenda() {
        return unidadeVenda;
    }

    public void setUnidadeVenda(UnidadeVendaBeans unidadeVenda) {
        this.unidadeVenda = unidadeVenda;
    }

    public TipoGradeBeans getTipoGrade() {
        return tipoGrade;
    }

    public void setTipoGrade(TipoGradeBeans tipoGrade) {
        this.tipoGrade = tipoGrade;
    }

    public CodigoOrigemProdutoBeans getCodigoOrigem() {
        return codigoOrigem;
    }

    public void setCodigoOrigem(CodigoOrigemProdutoBeans codigoOrigem) {
        this.codigoOrigem = codigoOrigem;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getDataAlt() {
        return dataAlt;
    }

    public void setDataAlt(String dataAlt) {
        this.dataAlt = dataAlt;
    }

    public String getDescricaoProduto() {
        return descricaoProduto;
    }

    public void setDescricaoProduto(String descricaoProduto) {
        this.descricaoProduto = descricaoProduto;
    }

    public String getDescricaoAuxiliar() {
        return descricaoAuxiliar;
    }

    public void setDescricaoAuxiliar(String descricaoAuxiliar) {
        this.descricaoAuxiliar = descricaoAuxiliar;
    }

    public String getMascara() {
        return mascara;
    }

    public void setMascara(String mascara) {
        this.mascara = mascara;
    }

    public String getCodigoEstrutural() {
        return codigoEstrutural;
    }

    public void setCodigoEstrutural(String codigoEstrutural) {
        this.codigoEstrutural = codigoEstrutural;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getAtivo() {
        return ativo;
    }

    public void setAtivo(String ativo) {
        this.ativo = ativo;
    }

    public String getTipoProduto() {
        return tipoProduto;
    }

    public void setTipoProduto(String tipoProduto) {
        this.tipoProduto = tipoProduto;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getValidade() {
        return validade;
    }

    public void setValidade(String validade) {
        this.validade = validade;
    }

    public String getGarantia() {
        return garantia;
    }

    public void setGarantia(String garantia) {
        this.garantia = garantia;
    }

    public String getControleSerial() {
        return controleSerial;
    }

    public void setControleSerial(String controleSerial) {
        this.controleSerial = controleSerial;
    }

    public String getTipoItem() {
        return tipoItem;
    }

    public void setTipoItem(String tipoItem) {
        this.tipoItem = tipoItem;
    }

    public String getRomaneia() {
        return romaneia;
    }

    public void setRomaneia(String romaneia) {
        this.romaneia = romaneia;
    }

    public double getPesoLiquido() {
        return pesoLiquido;
    }

    public void setPesoLiquido(double pesoLiquido) {
        this.pesoLiquido = pesoLiquido;
    }

    public double getPesoBruto() {
        return pesoBruto;
    }

    public void setPesoBruto(double pesoBruto) {
        this.pesoBruto = pesoBruto;
    }

    public FotosBeans getImagemProduto() {
        return imagemProduto;
    }

    public void setImagemProduto(FotosBeans imagemProduto) {
        this.imagemProduto = imagemProduto;
    }
}
