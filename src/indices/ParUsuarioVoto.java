package indices;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ParUsuarioVoto implements aed3.InterfaceArvoreBMais<ParUsuarioVoto> {

    private int idUsuario;
    private int idVoto;

    public ParUsuarioVoto() {
        this.idUsuario = -1;
        this.idVoto = -1;
    }

    public ParUsuarioVoto(int idUsuario, int idVoto) {
        this.idUsuario = idUsuario;
        this.idVoto = idVoto;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public int getIdVoto() {
        return idVoto;
    }

    @Override
    public short size() {
        return 8;
    }

    @Override
    public byte[] serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(idUsuario);
        dos.writeInt(idVoto);

        return baos.toByteArray();
    }

    @Override
    public void deserialize(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        idUsuario = dis.readInt();
        idVoto = dis.readInt();
    }

    @Override
    public int compareTo(ParUsuarioVoto outro) {
        if (this.idUsuario != outro.idUsuario) {
            return Integer.compare(this.idUsuario, outro.idUsuario);
        }

        return Integer.compare(this.idVoto, outro.idVoto);
    }

    @Override
    public ParUsuarioVoto clone() {
        return new ParUsuarioVoto(this.idUsuario, this.idVoto);
    }

    @Override
    public String toString() {
        return "(" + idUsuario + ";" + idVoto + ")";
    }
}
