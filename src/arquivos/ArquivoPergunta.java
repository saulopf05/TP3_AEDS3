package arquivos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import aed3.ArvoreBMais;
import entidades.Pergunta;
import indices.ParUsuarioPergunta;
import indices.ParDataPergunta;

public class ArquivoPergunta extends aed3.Arquivo<Pergunta> {
    private ArvoreBMais<ParUsuarioPergunta> indiceUsuarioPergunta;
    private ArvoreBMais<ParDataPergunta> indiceDataPergunta;

    public ArquivoPergunta() throws Exception {
        super("perguntas", Pergunta.class.getConstructor());
        indiceUsuarioPergunta = new ArvoreBMais<>(ParUsuarioPergunta.class.getConstructor(), 5,
                "dados/perguntas/indiceUsuarioPergunta.db");
        indiceDataPergunta = new ArvoreBMais<>(ParDataPergunta.class.getConstructor(), 5,
                "dados/perguntas/indiceDataPergunta.db");
        reconstruirIndiceDataSeNecessario();
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

    @Override public int create(Pergunta p) throws Exception {
        int id = super.create(p);
        indiceUsuarioPergunta.create(new ParUsuarioPergunta(p.getIdUsuario(), id));
        indiceDataPergunta.create(new ParDataPergunta(p.criacao, id));
        return id;
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
