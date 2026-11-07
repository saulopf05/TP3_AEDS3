package arquivos;

import aed3.HashExtensivel;

import entidades.Usuario;
import indices.ParEmailID;

public class ArquivoUsuario extends aed3.Arquivo<Usuario> {

    HashExtensivel<ParEmailID> indiceIndiretoEmail;

    public ArquivoUsuario() throws Exception {
        super("usuarios", Usuario.class.getConstructor());

        indiceIndiretoEmail = new HashExtensivel<>(
            ParEmailID.class.getConstructor(),
            4,
            "dados/usuarios/indiceEmail.d.db",
            "dados/usuarios/indiceEmail.c.db"
        );
    }

    @Override
    public int create(Usuario u) throws Exception {
        int id = super.create(u);
        indiceIndiretoEmail.create(new ParEmailID(u.getEmail(), id));
        return id;
    }

    public Usuario read(String email) throws Exception {
        ParEmailID pei = indiceIndiretoEmail.read(ParEmailID.hash(email));

        if(pei == null)
            return null;

        return read(pei.getId());
    }

    @Override
    public boolean update(Usuario novoUsuario) throws Exception {
        Usuario usuarioVelho = read(novoUsuario.getId());

        if(super.update(novoUsuario)) {
            if(novoUsuario.getEmail().compareTo(usuarioVelho.getEmail()) != 0) {

                indiceIndiretoEmail.delete(ParEmailID.hash(usuarioVelho.getEmail()));

                indiceIndiretoEmail.create(
                    new ParEmailID(novoUsuario.getEmail(), novoUsuario.getId())
                );
            }
            return true;
        }
        return false;
    }

    public boolean delete(String email) throws Exception {
        ParEmailID pei = indiceIndiretoEmail.read(ParEmailID.hash(email));

        if(pei != null) {
            if(delete(pei.getId())) {
                return indiceIndiretoEmail.delete(ParEmailID.hash(email));
            }
        }
        return false;
    }

    @Override
    public boolean delete(int id) throws Exception {
        Usuario u = super.read(id);

        if(u != null) {
            if(super.delete(id)) {
                return indiceIndiretoEmail.delete(ParEmailID.hash(u.getEmail()));
            }
        }
        return false;
    }

}
