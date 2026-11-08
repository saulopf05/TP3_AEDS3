package busca;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import aed3.ElementoLista;
import aed3.ListaInvertida;

/**
 * Índice invertido com ranking TF x IDF, construído sobre a classe
 * ListaInvertida fornecida pelo professor.
 *
 * Para cada termo, a lista invertida guarda pares (id; TF), em que o TF é a
 * taxa de ocorrências do termo entre os termos válidos da entidade. O IDF não
 * é gravado: ele é calculado na hora da busca a partir do número total de
 * entidades (incrementaEntidades / numeroEntidades) e do tamanho da lista do
 * termo.
 */
public class IndiceInvertido {

    private final ListaInvertida lista;

    /**
     * @param elementosPorBloco quantidade de pares (id; TF) em cada bloco
     * @param arquivoDicionario arquivo com os termos e o total de entidades
     * @param arquivoBlocos     arquivo com os blocos das listas
     */
    public IndiceInvertido(int elementosPorBloco, String arquivoDicionario,
                           String arquivoBlocos) throws Exception {
        lista = new ListaInvertida(elementosPorBloco, arquivoDicionario, arquivoBlocos);
    }

    /**
     * Acrescenta uma entidade ao índice.
     *
     * Cada ocorrência de um termo vale 1/n, sendo n o total de termos válidos
     * do texto. Como a ListaInvertida não aceita o mesmo id duas vezes na
     * lista de um termo, a segunda ocorrência soma a sua frequência à que já
     * está gravada (read + update), como pede o enunciado.
     */
    public void inserir(int id, String texto) throws Exception {
        List<String> termos = ProcessadorTermos.extrairTermos(texto);
        float frequencia = termos.isEmpty() ? 0 : 1.0f / termos.size();

        for (String termo : termos) {
            ElementoLista e = lista.read(termo, id);
            if (e == null)
                lista.create(termo, new ElementoLista(id, frequencia));
            else
                lista.update(termo, new ElementoLista(id, frequencia + e.getFrequencia()));
        }
        lista.incrementaEntidades();
    }

    /**
     * Retira uma entidade do índice. O texto deve ser o mesmo usado na
     * inserção, para que o id seja apagado da lista de cada um dos termos.
     */
    public void remover(int id, String texto) throws Exception {
        for (String termo : new LinkedHashSet<>(ProcessadorTermos.extrairTermos(texto)))
            lista.delete(termo, id);
        lista.decrementaEntidades();
    }

    /** Total de entidades registradas no índice (usado no cálculo do IDF). */
    public int numeroEntidades() throws Exception {
        return lista.numeroEntidades();
    }

    /**
     * Busca as entidades que contêm pelo menos um dos termos da consulta.
     *
     * Para cada termo da consulta:
     *   IDF = log10(total de entidades / entidades que contêm o termo)
     * e cada par (id; TF) da lista do termo contribui com TF x IDF para o id.
     * As contribuições de um mesmo id são somadas, e o resultado é ordenado
     * do maior para o menor valor.
     *
     * @return pares (id; TF x IDF) em ordem decrescente de relevância
     */
    public List<ElementoLista> buscar(String consulta) throws Exception {
        List<ElementoLista> resultado = new ArrayList<>();
        int totalEntidades = lista.numeroEntidades();
        if (totalEntidades <= 0)
            return resultado;

        // Termos repetidos na consulta são considerados uma única vez.
        LinkedHashSet<String> termos =
            new LinkedHashSet<>(ProcessadorTermos.extrairTermos(consulta));

        Map<Integer, Double> pontuacao = new HashMap<>();
        for (String termo : termos) {
            ElementoLista[] ocorrencias = lista.read(termo);
            if (ocorrencias.length == 0)
                continue;

            double idf = Math.log10((double) totalEntidades / ocorrencias.length);
            for (ElementoLista e : ocorrencias)
                pontuacao.merge(e.getId(), e.getFrequencia() * idf, Double::sum);
        }

        for (Map.Entry<Integer, Double> par : pontuacao.entrySet())
            resultado.add(new ElementoLista(par.getKey(), par.getValue().floatValue()));

        // Maior TF x IDF primeiro; em caso de empate, o menor id.
        resultado.sort((a, b) -> {
            int c = Float.compare(b.getFrequencia(), a.getFrequencia());
            return c != 0 ? c : Integer.compare(a.getId(), b.getId());
        });
        return resultado;
    }

    /** Mostra todas as listas invertidas (útil para conferir o índice). */
    public void imprimir() throws Exception {
        lista.print();
    }
}
