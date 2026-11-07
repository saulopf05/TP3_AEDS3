package arquivos;

import aed3.ArvoreBMais;

import entidades.Voto;
import indices.ParUsuarioVoto;

public class ArquivoVoto extends aed3.Arquivo<Voto> {
    private ArvoreBMais<ParUsuarioVoto> indiceUsuarioVoto;

    public ArquivoVoto() throws Exception {
        super("votos", Voto.class.getConstructor());

        indiceUsuarioVoto = new ArvoreBMais<>(
                ParUsuarioVoto.class.getConstructor(),
                5,
                "dados/votos/indiceUsuarioVoto.db");
    }

    public ArvoreBMais<ParUsuarioVoto> getIndiceUsuarioVoto() {
        return indiceUsuarioVoto;
    }

    @Override
    public int create(Voto v) throws Exception {
        int id = super.create(v);
        indiceUsuarioVoto.create(new ParUsuarioVoto(v.getIdUsuario(), id));
        return id;
    }
}
