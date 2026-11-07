package indices;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ParEmailID implements aed3.RegistroHashExtensivel<ParEmailID> {
    
    private String email; // chave
    private int id;       // valor  
    private final short TAMANHO = 104;
    
    public ParEmailID() {
        this.email = "";
        this.id = -1;
    }

    public ParEmailID(String email, int id) {
        this.email = email;
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public int getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return hash(this.email);
    }

    public static int hash(String email) {
        return Math.abs(email.hashCode());
    }

    @Override
    public String toString() {
        return "(" + this.email + ";" + this.id + ")";
    }

    @Override
    public short size() {
        return TAMANHO;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        byte[] emailBytes = new byte[100];
        byte[] texto = email.getBytes();

        System.arraycopy(
            texto,
            0,
            emailBytes,
            0,
            Math.min(texto.length, 100)
        );

        dos.write(emailBytes);
        dos.writeInt(id);

        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        byte[] b = new byte[100];

        dis.read(b);
        this.email = new String(b).trim();

        this.id = dis.readInt();
    }

}
