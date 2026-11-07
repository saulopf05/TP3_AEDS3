package entidades;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import aed3.Registro;

public class Pergunta implements Registro {

    public int idPergunta;
    public int idUsuario;
    public long criacao;
    public long alteracao;
    public short nota;
    public String pergunta;
    public String palavrasChave;
    public boolean ativa;

    public Pergunta() {
        this(-1, -1, 0, 0, (short) 0, "", "", true);
    }

    public Pergunta(int idUsuario, String pergunta, String palavrasChave) {
        this(-1,
            idUsuario,
            System.currentTimeMillis(),
            System.currentTimeMillis(),
            (short) 0,
            pergunta,
            palavrasChave,
            true
        );
    }

    public Pergunta(int idPergunta, int idUsuario,
                    long criacao, long alteracao,
                    short nota, String pergunta,
                    String palavrasChave, boolean ativa) {

        this.idPergunta = idPergunta;
        this.idUsuario = idUsuario;
        this.criacao = criacao;
        this.alteracao = alteracao;
        this.nota = nota;
        this.pergunta = pergunta;
        this.palavrasChave = palavrasChave;
        this.ativa = ativa;
    }

    @Override
    public void setId(int id) {
        this.idPergunta = id;
    }

    @Override
    public int getId() {
        return this.idPergunta;
    }

    @Override
    public byte[] toByteArray() throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(this.idPergunta);
        dos.writeInt(this.idUsuario);
        dos.writeLong(this.criacao);
        dos.writeLong(this.alteracao);
        dos.writeShort(this.nota);
        dos.writeUTF(this.pergunta);
        dos.writeUTF(this.palavrasChave);
        dos.writeBoolean(this.ativa);

        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] b) throws IOException {

        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);

        this.idPergunta = dis.readInt();
        this.idUsuario = dis.readInt();
        this.criacao = dis.readLong();
        this.alteracao = dis.readLong();
        this.nota = dis.readShort();
        this.pergunta = dis.readUTF();
        this.palavrasChave = dis.readUTF();
        this.ativa = dis.readBoolean();
    }

    @Override
    public String toString() {

        return "\nPergunta: " + this.pergunta +
               "\nPalavras-chave: " + this.palavrasChave +
               "\nNota: " + this.nota +
               (this.ativa ? "" : "\nARQUIVADA");
    }
    public int getIdUsuario() {
        return this.idUsuario;
}
}