package arquivos;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import aed3.ArvoreBMais;
import aed3.ElementoLista;
import busca.IndiceInvertido;
import entidades.Pergunta;
import indices.ParUsuarioPergunta;
import indices.ParDataPergunta;

public class ArquivoPergunta extends aed3.Arquivo<Pergunta> {
    private static final String DICIONARIO_PALAVRAS_CHAVE = "dados/perguntas/indicePalavrasChave.dic.db";
    private static final String BLOCOS_PALAVRAS_CHAVE = "dados/perguntas/indicePalavrasChave.blocos.db";

    private ArvoreBMais<ParUsuarioPergunta> indiceUsuarioPergunta;
    private ArvoreBMais<ParDataPergunta> indiceDataPergunta;
    private IndiceInvertido indicePalavrasChave;

    public ArquivoPergunta() throws Exception {
        super("perguntas", Pergunta.class.getConstructor());
        indiceUsuarioPergunta = new ArvoreBMais<>(ParUsuarioPergunta.class.getConstructor(), 5,
                "dados/perguntas/indiceUsuarioPergunta.db");
        indiceDataPergunta = new ArvoreBMais<>(ParDataPergunta.class.getConstructor(), 5,
                "dados/perguntas/indiceDataPergunta.db");
        reconstruirIndiceDataSeNecessario();
        abrirIndicePalavrasChave();
    }

    public ArvoreBMais<ParUsuarioPergunta> getIndiceUsuarioPergunta() { return indiceUsuarioPergunta; }
    public ArvoreBMais<ParDataPergunta> getIndiceDataPergunta() { return indiceDataPergunta; }

    /** Garante que registros já existentes também estejam no índice cronológico. */
    private void reconstruirIndiceDataSeNecessario() throws Exception {
        ArrayList<ParDataPergunta> existentes = indiceDataPergunta.read(null);
        ArrayList<ParUsuarioPergunta> perguntasIndexadas = indiceUsuarioPergunta.read(null);
        for (ParUsuarioPergunta parUsuario : perguntasIndexadas) {
            Pergunta p = read(parUsuario.getIdPergunta());
            if (p == null) continue;
            boolean encontrada = false;
            for (ParDataPergunta parData : existentes) {
                if (parData.getIdPergunta() == p.getId() && parData.getCriacao() == p.criacao) {
                    encontrada = true;
                    break;
                }
            }
            if (!encontrada) {
                ParDataPergunta nova = new ParDataPergunta(p.criacao, p.getId());
                indiceDataPergunta.create(nova);
                existentes.add(nova);
            }
        }
    }

    /**
     * Abre o índice invertido das palavras-chave. Se os arquivos ainda não
     * existem (primeira execução depois do TP3, por exemplo), o índice é
     * construído a partir das perguntas ativas já gravadas.
     */
    private void abrirIndicePalavrasChave() throws Exception {
        File dicionario = new File(DICIONARIO_PALAVRAS_CHAVE);
        File blocos = new File(BLOCOS_PALAVRAS_CHAVE);
        boolean construir = !dicionario.exists() || !blocos.exists();
        if (construir) {
            dicionario.delete();
            blocos.delete();
        }

        indicePalavrasChave = new IndiceInvertido(5, DICIONARIO_PALAVRAS_CHAVE, BLOCOS_PALAVRAS_CHAVE);

        if (construir) {
            for (ParUsuarioPergunta par : indiceUsuarioPergunta.read(null)) {
                Pergunta p = read(par.getIdPergunta());
                if (p != null && p.ativa)
                    indicePalavrasChave.inserir(p.getId(), p.palavrasChave);
            }
        }
    }

    @Override public int create(Pergunta p) throws Exception {
        int id = super.create(p);
        indiceUsuarioPergunta.create(new ParUsuarioPergunta(p.getIdUsuario(), id));
        indiceDataPergunta.create(new ParDataPergunta(p.criacao, id));
        if (p.ativa)
            indicePalavrasChave.inserir(id, p.palavrasChave);
        return id;
    }

    /**
     * Mantém o índice invertido coerente com a alteração. Somente perguntas
     * ativas ficam no índice: ao arquivar, a pergunta sai dele; ao trocar as
     * palavras-chave, os termos antigos são removidos e os novos, inseridos.
     * Alterações que não mexem nesses campos (um voto, por exemplo) não
     * tocam no índice.
     */
    @Override public boolean update(Pergunta nova) throws Exception {
        Pergunta antiga = read(nova.getId());
        if (antiga == null || !super.update(nova))
            return false;

        boolean mudouPalavras = !antiga.palavrasChave.equals(nova.palavrasChave);
        if (antiga.ativa && (!nova.ativa || mudouPalavras))
            indicePalavrasChave.remover(antiga.getId(), antiga.palavrasChave);
        if (nova.ativa && (!antiga.ativa || mudouPalavras))
            indicePalavrasChave.inserir(nova.getId(), nova.palavrasChave);
        return true;
    }

    @Override public boolean delete(int id) throws Exception {
        Pergunta p = read(id);
        if (p == null || !super.delete(id))
            return false;
        if (p.ativa)
            indicePalavrasChave.remover(id, p.palavrasChave);
        return true;
    }

    /**
     * Busca perguntas ativas pelas palavras-chave, já ordenadas pelo valor
     * TF x IDF (maior primeiro).
     *
     * @return pares (idPergunta; TF x IDF)
     */
    public List<ElementoLista> buscarPorPalavrasChave(String consulta) throws Exception {
        List<ElementoLista> resultado = new ArrayList<>();
        for (ElementoLista e : indicePalavrasChave.buscar(consulta)) {
            Pergunta p = read(e.getId());
            if (p != null && p.ativa)
                resultado.add(e);
        }
        return resultado;
    }

    /** Retorna uma página de perguntas ativas, cinco por página, mais recentes primeiro. */
    public List<Pergunta> listarRecentes(int pagina, int tamanhoPagina) throws Exception {
        if (pagina < 0 || tamanhoPagina < 1) throw new IllegalArgumentException("Página ou tamanho inválido.");
        ArrayList<ParDataPergunta> chaves = indiceDataPergunta.read(null);
        Collections.sort(chaves, (a, b) -> b.compareTo(a));
        ArrayList<Pergunta> ativas = new ArrayList<>();
        for (ParDataPergunta chave : chaves) {
            Pergunta p = read(chave.getIdPergunta());
            if (p != null && p.ativa) ativas.add(p);
        }
        int inicio = pagina * tamanhoPagina;
        if (inicio >= ativas.size()) return new ArrayList<>();
        return new ArrayList<>(ativas.subList(inicio, Math.min(inicio + tamanhoPagina, ativas.size())));
    }
}
