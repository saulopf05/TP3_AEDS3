package menus;

import java.util.Scanner;

/**
 * Entrada padrão compartilhada por todos os menus.
 *
 * Cada menu manter o seu próprio Scanner sobre o System.in faz com que os
 * buffers internos disputem a mesma entrada: o Scanner de um menu lê um bloco
 * inteiro, e as linhas que sobram somem quando o controle passa para o menu
 * seguinte. Por isso existe uma única instância para todo o sistema.
 */
public class Console {

    private static final Scanner ENTRADA = new Scanner(System.in);

    private Console() {
    }

    public static Scanner entrada() {
        return ENTRADA;
    }
}
