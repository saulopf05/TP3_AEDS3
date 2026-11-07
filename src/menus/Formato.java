package menus;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Apresentação das datas na interface.
 *
 * As entidades guardam data e hora em milissegundos, como o enunciado pede.
 * Esse formato não serve para leitura, então toda tela que mostra uma data
 * passa por aqui.
 */
public class Formato {

    private static final DateTimeFormatter DATA_HORA =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Formato() {
    }

    public static String dataHora(long milissegundos) {
        return DATA_HORA.format(
            Instant.ofEpochMilli(milissegundos).atZone(ZoneId.systemDefault())
        );
    }
}
