package entidades;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Locale;

import aed3.Registro;

public class Usuario implements Registro {

    public int idUsuario;
    public String nome;
    public String email;
    public String hashSenha;
    public String perguntaSecreta;
    public String hashRespostaSecreta;

    public Usuario() {
        this(-1, "", "", "", "", "");
    }

    public Usuario(String nome, String email, String senha, String perguntaSecreta, String respostaSecreta) {
        this(-1, nome, email, gerarHash(senha), perguntaSecreta, gerarHashResposta(respostaSecreta));
    }

    public Usuario(int idUsuario, String nome, String email,
                   String hashSenha, String perguntaSecreta,
                   String hashRespostaSecreta) {

        this.idUsuario = idUsuario;
        this.nome = nome;
        this.email = email;
        this.hashSenha = hashSenha;
        this.perguntaSecreta = perguntaSecreta;
        this.hashRespostaSecreta = hashRespostaSecreta;
    }

    @Override
    public void setId(int id) {
        this.idUsuario = id;
    }

    @Override
    public int getId() {
        return this.idUsuario;
    }

    public String getEmail() {
        return this.email;
    }

    public static String gerarHash(String texto) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");

            byte[] hash = md.digest(texto.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();

            for(byte b : hash) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();

        } catch(java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar hash.", e);
        }
    }

    public static String gerarHashResposta(String resposta) {

        String respostaNormalizada = Normalizer
            .normalize(resposta, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "")
            .toLowerCase(Locale.ROOT);

        return gerarHash(respostaNormalizada);
    }

    @Override
    public String toString() {
        return "\nID.................: " + this.idUsuario +
               "\nNome...............: " + this.nome +
               "\nEmail..............: " + this.email +
               "\nPergunta secreta...: " + this.perguntaSecreta;
    }

    @Override
    public byte[] toByteArray() throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(this.idUsuario);
        dos.writeUTF(this.nome);
        dos.writeUTF(this.email);
        dos.writeUTF(this.hashSenha);
        dos.writeUTF(this.perguntaSecreta);
        dos.writeUTF(this.hashRespostaSecreta);

        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] b) throws IOException {

        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);

        this.idUsuario = dis.readInt();
        this.nome = dis.readUTF();
        this.email = dis.readUTF();
        this.hashSenha = dis.readUTF();
        this.perguntaSecreta = dis.readUTF();
        this.hashRespostaSecreta = dis.readUTF();
    }
}