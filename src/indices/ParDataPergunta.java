package indices;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import aed3.InterfaceArvoreBMais;

/** Chave cronológica de pergunta; o ID desempata datas iguais. */
public class ParDataPergunta implements InterfaceArvoreBMais<ParDataPergunta> {
    private long criacao;
    private int idPergunta;

    public ParDataPergunta() { this(0L, -1); }
    public ParDataPergunta(long criacao, int idPergunta) {
        this.criacao = criacao;
        this.idPergunta = idPergunta;
    }
    public long getCriacao() { return criacao; }
    public int getIdPergunta() { return idPergunta; }
    @Override public short size() { return 12; }
    @Override public byte[] serialize() throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream d = new DataOutputStream(b);
        d.writeLong(criacao); d.writeInt(idPergunta); return b.toByteArray();
    }
    @Override public void deserialize(byte[] ba) throws IOException {
        DataInputStream d = new DataInputStream(new ByteArrayInputStream(ba));
        criacao = d.readLong(); idPergunta = d.readInt();
    }
    @Override public int compareTo(ParDataPergunta o) {
        int c = Long.compare(criacao, o.criacao);
        return c != 0 ? c : Integer.compare(idPergunta, o.idPergunta);
    }
    @Override public ParDataPergunta clone() { return new ParDataPergunta(criacao, idPergunta); }
    @Override public String toString() { return "(" + criacao + ";" + idPergunta + ")"; }
}
