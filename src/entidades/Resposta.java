package entidades;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import aed3.Registro;

public class Resposta implements Registro {

    public int idResposta;
    public int idPergunta;
    public int idUsuario;
    public long criacao;
    public String texto;
    public short nota;
    public boolean ativa;

    public Resposta() {
        this(-1, -1, -1, 0, "", (short) 0, true);
    }

    public Resposta(int idPergunta, int idUsuario, String texto) {
        this(-1,
                idPergunta,
                idUsuario,
                System.currentTimeMillis(),
                texto,
                (short) 0,
                true);
    }

    public Resposta(int idResposta, int idPergunta, int idUsuario,
            long criacao, String texto, short nota, boolean ativa) {
        this.idResposta = idResposta;
        this.idPergunta = idPergunta;
        this.idUsuario = idUsuario;
        this.criacao = criacao;
        this.texto = texto;
        this.nota = nota;
        this.ativa = ativa;
    }

    @Override
    public void setId(int id) {
        this.idResposta = id;
    }

    @Override
    public int getId() {
        return this.idResposta;
    }

    public int getIdPergunta() {
        return this.idPergunta;
    }

    public int getIdUsuario() {
        return this.idUsuario;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(this.idResposta);
        dos.writeInt(this.idPergunta);
        dos.writeInt(this.idUsuario);
        dos.writeLong(this.criacao);
        dos.writeUTF(this.texto);
        dos.writeShort(this.nota);
        dos.writeBoolean(this.ativa);

        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);

        this.idResposta = dis.readInt();
        this.idPergunta = dis.readInt();
        this.idUsuario = dis.readInt();
        this.criacao = dis.readLong();
        this.texto = dis.readUTF();
        this.nota = dis.readShort();
        this.ativa = dis.readBoolean();
    }

    @Override
    public String toString() {
        return "\nResposta: " + this.texto +
                "\nNota: " + this.nota +
                (this.ativa ? "" : "\nINATIVA");
    }
}
