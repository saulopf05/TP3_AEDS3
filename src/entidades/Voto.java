package entidades;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import aed3.Registro;

public class Voto implements Registro {

    public int idVoto;
    public int idUsuario;
    public int idPergunta;
    public int idResposta;
    public short valor;
    public long criacao;

    public Voto() {
        this(-1, -1, -1, -1, (short) 0, 0L);
    }

    public Voto(int idUsuario, int idPergunta, int idResposta, short valor) {
        this(-1,
                idUsuario,
                idPergunta,
                idResposta,
                valor,
                System.currentTimeMillis());
    }

    public Voto(int idVoto, int idUsuario, int idPergunta,
            int idResposta, short valor, long criacao) {
        this.idVoto = idVoto;
        this.idUsuario = idUsuario;
        this.idPergunta = idPergunta;
        this.idResposta = idResposta;
        this.valor = valor;
        this.criacao = criacao;
    }

    @Override
    public void setId(int id) {
        this.idVoto = id;
    }

    @Override
    public int getId() {
        return this.idVoto;
    }

    public int getIdUsuario() {
        return this.idUsuario;
    }

    public int getIdPergunta() {
        return this.idPergunta;
    }

    public int getIdResposta() {
        return this.idResposta;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(this.idVoto);
        dos.writeInt(this.idUsuario);
        dos.writeInt(this.idPergunta);
        dos.writeInt(this.idResposta);
        dos.writeShort(this.valor);
        dos.writeLong(this.criacao);

        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);

        this.idVoto = dis.readInt();
        this.idUsuario = dis.readInt();
        this.idPergunta = dis.readInt();
        this.idResposta = dis.readInt();
        this.valor = dis.readShort();
        this.criacao = dis.readLong();
    }

    @Override
    public String toString() {
        return "\nVoto: " + this.valor +
                "\nPergunta: " + this.idPergunta +
                "\nResposta: " + this.idResposta;
    }
}
