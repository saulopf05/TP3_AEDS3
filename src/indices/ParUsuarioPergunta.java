package indices;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ParUsuarioPergunta implements aed3.InterfaceArvoreBMais<ParUsuarioPergunta> {

    private int idUsuario;
    private int idPergunta;

    public ParUsuarioPergunta() {
        this.idUsuario = -1;
        this.idPergunta = -1;
    }

    public ParUsuarioPergunta(int idUsuario, int idPergunta) {
        this.idUsuario = idUsuario;
        this.idPergunta = idPergunta;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public int getIdPergunta() {
        return idPergunta;
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
        dos.writeInt(idPergunta);

        return baos.toByteArray();
    }

    @Override
    public void deserialize(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        idUsuario = dis.readInt();
        idPergunta = dis.readInt();
    }

    @Override
    public int compareTo(ParUsuarioPergunta outro) {
        if(this.idUsuario != outro.idUsuario) {
            return Integer.compare(this.idUsuario, outro.idUsuario);
        }

        return Integer.compare(this.idPergunta, outro.idPergunta);
    }

    @Override
    public ParUsuarioPergunta clone() {
        return new ParUsuarioPergunta(this.idUsuario, this.idPergunta);
    }

    @Override
    public String toString() {
        return "(" + idUsuario + ";" + idPergunta + ")";
    }
}