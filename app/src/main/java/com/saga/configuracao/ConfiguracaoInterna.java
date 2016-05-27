package com.saga.configuracao;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import me.sudar.zxingorient.Barcode;

/**
 * Created by Bruno Nogueira Silva on 27/05/2016.
 */
public class ConfiguracaoInterna {

    public static final Collection<String> TIPOS_CODIGO_BARRAS_1D_2D = list("UPC_A", "UPC_E", "UPC_EAN_EXTENSION", "EAN_8", "EAN_13", "CODABAR", "CODE_39",
            "CODE_93", "CODE_128", "ITF", "RSS_14", "RSS_EXPANDED", "QR_CODE", "DATA_MATRIX", "PDF_417", "AZTEC");


    private static List<String> list(String... values) {
        return Collections.unmodifiableList(Arrays.asList(values));
    }
}
