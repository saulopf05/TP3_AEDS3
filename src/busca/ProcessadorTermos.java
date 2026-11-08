package busca;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Transforma um texto no vetor de termos usado pelo índice invertido.
 *
 * O tratamento é o mesmo na indexação das palavras-chave e na consulta:
 *   1. o texto é passado para letras minúsculas;
 *   2. os acentos e a cedilha são removidos ("Introdução" vira "introducao");
 *   3. o texto é quebrado em palavras (qualquer caractere que não seja letra
 *      ou dígito separa termos, inclusive o ";" usado entre as palavras-chave);
 *   4. as stop words e os numerais são descartados.
 *
 * As repetições são mantidas, porque o cálculo do TF depende delas.
 */
public class ProcessadorTermos {

    /**
     * Stop words em português (artigos, preposições, contrações, pronomes,
     * conjunções, advérbios e verbos auxiliares muito frequentes e numerais
     * por extenso), acrescidas de palavras comuns no domínio do AJUDA AÍ que
     * não ajudam a diferenciar uma pergunta da outra ("pergunta", "duvida",
     * "ajuda", "alguem"...).
     *
     * As palavras estão sem acento, pois a comparação é feita depois da
     * normalização do texto.
     */
    private static final Set<String> STOP_WORDS = new HashSet<>();

    static {
        String[] palavras = {
            // artigos
            "a", "o", "as", "os", "um", "uma", "uns", "umas",
            // preposições e contrações
            "ante", "apos", "ate", "com", "contra", "de", "desde", "em", "entre",
            "para", "pra", "pro", "pras", "pros", "per", "perante", "por", "sem",
            "sob", "sobre", "tras", "ao", "aos", "do", "da", "dos", "das", "no",
            "na", "nos", "nas", "num", "numa", "nuns", "numas", "dum", "duma",
            "pelo", "pela", "pelos", "pelas", "neste", "nesta", "nestes", "nestas",
            "nesse", "nessa", "nesses", "nessas", "naquele", "naquela", "naqueles",
            "naquelas", "deste", "desta", "destes", "destas", "desse", "dessa",
            "desses", "dessas", "daquele", "daquela", "daqueles", "daquelas",
            "dele", "dela", "deles", "delas", "nele", "nela", "neles", "nelas",
            "aquele", "aquela", "aqueles", "aquelas", "isto", "isso", "aquilo",
            "disto", "disso", "daquilo", "nisto", "nisso", "naquilo",
            // pronomes
            "eu", "tu", "ele", "ela", "vos", "eles", "elas", "voce", "voces",
            "me", "te", "lhe", "lhes", "mim", "ti", "si", "comigo", "contigo",
            "conosco", "meu", "minha", "meus", "minhas", "teu", "tua", "teus",
            "tuas", "seu", "sua", "seus", "suas", "nosso", "nossa", "nossos",
            "nossas", "este", "esta", "estes", "estas", "esse", "essa", "esses",
            "essas", "qual", "quais", "quem", "cujo", "cuja", "cujos", "cujas",
            "algum", "alguma", "alguns", "algumas", "nenhum", "nenhuma", "todo",
            "toda", "todos", "todas", "outro", "outra", "outros", "outras",
            "mesmo", "mesma", "mesmos", "mesmas", "tal", "tais", "cada",
            // conjunções e advérbios
            "e", "ou", "mas", "porem", "contudo", "todavia", "entao", "logo",
            "pois", "porque", "que", "se", "como", "quando", "onde",
            "enquanto", "embora", "nem", "tambem", "ja", "ainda", "nao", "sim",
            "muito", "muita", "muitos", "muitas", "pouco", "pouca", "poucos",
            "poucas", "mais", "menos", "bem", "mal", "so", "apenas", "aqui", "ai",
            "ali", "la", "ca", "agora", "sempre", "nunca", "quanto", "quanta",
            "quantos", "quantas", "tao", "tanto", "tanta", "tantos", "tantas",
            // verbos auxiliares e de ligação mais comuns
            "ser", "sou", "sao", "era", "eram", "foi", "foram", "sera",
            "seria", "sendo", "sido", "seja", "sejam", "estar", "estou", "estao", "estava", "estavam", "esteve", "estive", "ter", "tenho",
            "tem", "temos", "tinha", "tinham", "teve", "tive", "haver", "ha",
            "havia", "houve", "vai", "vou", "vao", "ir", "pode", "podem", "posso",
            "poder", "deve", "devem", "devo", "fazer", "faz", "fiz", "fez",
            // numerais por extenso
            "dois", "duas", "tres", "quatro", "cinco", "seis", "sete", "oito",
            "nove", "dez", "onze", "doze", "treze", "catorze", "quatorze", "quinze",
            "dezesseis", "dezessete", "dezoito", "dezenove", "vinte", "trinta",
            "quarenta", "cinquenta", "sessenta", "setenta", "oitenta", "noventa",
            "cem", "cento", "duzentos", "trezentos", "quatrocentos", "quinhentos",
            "mil", "milhao", "milhoes", "primeiro", "primeira", "primeiros",
            "primeiras", "segundo", "segunda", "terceiro", "terceira", "quarto",
            "quarta", "quinto", "quinta", "ultimo", "ultima",
            // palavras frequentes no domínio de perguntas e respostas
            "pergunta", "perguntas", "duvida", "duvidas", "ajuda", "ajudar",
            "ajudem", "alguem", "pessoal", "gente", "favor", "obrigado",
            "obrigada", "oi", "ola", "coisa", "coisas", "algo", "sei", "saber"
        };
        for (String p : palavras)
            STOP_WORDS.add(normalizar(p));
    }

    private ProcessadorTermos() {
    }

    /** Passa o texto para minúsculas e remove acentos e cedilha. */
    public static String normalizar(String texto) {
        String minusculo = texto.toLowerCase(Locale.ROOT);
        String decomposto = Normalizer.normalize(minusculo, Normalizer.Form.NFD);
        return decomposto.replaceAll("\\p{M}", "");
    }

    /** Indica se o termo (já normalizado) deve ser descartado. */
    public static boolean descartar(String termo) {
        return termo.isEmpty()
            || STOP_WORDS.contains(termo)
            || termo.chars().allMatch(Character::isDigit);   // numerais (2, 10, 2024...)
    }

    /**
     * Gera o vetor de termos válidos do texto, na ordem em que aparecem e
     * mantendo as repetições.
     */
    public static List<String> extrairTermos(String texto) {
        List<String> termos = new ArrayList<>();
        if (texto == null)
            return termos;

        String[] palavras = normalizar(texto).split("[^a-z0-9]+");
        for (String palavra : palavras) {
            if (!descartar(palavra))
                termos.add(palavra);
        }
        return termos;
    }
}
