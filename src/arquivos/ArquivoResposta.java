package arquivos;

import aed3.ArvoreBMais;

import entidades.Resposta;
import indices.ParPerguntaResposta;

public class ArquivoResposta extends aed3.Arquivo<Resposta> {
    private ArvoreBMais<ParPerguntaResposta> indicePerguntaResposta;

    public ArquivoResposta() throws Exception {
        super("respostas", Resposta.class.getConstructor());

        indicePerguntaResposta = new ArvoreBMais<>(
                ParPerguntaResposta.class.getConstructor(),
                5,
                "dados/respostas/indicePerguntaResposta.db");
    }

    public ArvoreBMais<ParPerguntaResposta> getIndicePerguntaResposta() {
        return indicePerguntaResposta;
    }

    @Override
    public int create(Resposta r) throws Exception {
        int id = super.create(r);
        indicePerguntaResposta.create(new ParPerguntaResposta(r.getIdPergunta(), id));
        return id;
    }
}
