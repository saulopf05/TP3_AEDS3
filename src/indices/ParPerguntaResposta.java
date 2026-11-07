package indices;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ParPerguntaResposta implements aed3.InterfaceArvoreBMais<ParPerguntaResposta> {

    private int idPergunta;
    private int idResposta;

    public ParPerguntaResposta() {
        this.idPergunta = -1;
        this.idResposta = -1;
    }

    public ParPerguntaResposta(int idPergunta, int idResposta) {
        this.idPergunta = idPergunta;
        this.idResposta = idResposta;
    }

    public int getIdPergunta() {
        return idPergunta;
    }

    public int getIdResposta() {
        return idResposta;
    }

    @Override
    public short size() {
        return 8;
    }

    @Override
    public byte[] serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(idPergunta);
        dos.writeInt(idResposta);

        return baos.toByteArray();
    }

    @Override
    public void deserialize(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        idPergunta = dis.readInt();
        idResposta = dis.readInt();
    }

    @Override
    public int compareTo(ParPerguntaResposta outro) {
        if (this.idPergunta != outro.idPergunta) {
            return Integer.compare(this.idPergunta, outro.idPergunta);
        }

        return Integer.compare(this.idResposta, outro.idResposta);
    }

    @Override
    public ParPerguntaResposta clone() {
        return new ParPerguntaResposta(this.idPergunta, this.idResposta);
    }

    @Override
    public String toString() {
        return "(" + idPergunta + ";" + idResposta + ")";
    }
}
