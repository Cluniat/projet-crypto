import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

public class Paillier {

    private BigInteger pk;
    private BigInteger sk;

    public void keyGen() {

        ArrayList<BigInteger> keys = new ArrayList<>();
        //soit k = 1024

        // Générer p et q de 512 bits
        Random rand = new Random();
        BigInteger p = new BigInteger(1024, 1, rand);
        BigInteger q = new BigInteger(1024, 1, rand);

        // Calculer n = pq
        BigInteger n = p.multiply(q);

        //Calculer phin = (p-1)(q-1)
        BigInteger phi_n = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        // calculer rho = n^-1 mod phi
        BigInteger rho = n.modInverse(phi_n);

        this.pk = n;
        this.sk = rho;
    }

    public BigInteger encrypt(BigInteger message) {
        Random rand = new Random();
        BigInteger r = new BigInteger(pk.bitLength(), 1, rand).mod(pk);
        return message.multiply(pk).add(BigInteger.ONE).multiply(r.modPow(pk, pk.pow(2))).mod(pk.pow(2));
    }

    public BigInteger decrypt(BigInteger encryptedMessage) {
        BigInteger r = encryptedMessage.modPow(sk, pk);
        return  encryptedMessage.multiply(r.modPow(pk, pk.pow(2)).modInverse(pk.pow(2))).mod(pk.pow(2)).subtract(BigInteger.ONE).divide(pk);
    }

    public ArrayList<BigInteger> decryptPlus(BigInteger encryptedMessage) {
        ArrayList<BigInteger> response = new ArrayList<>();
        BigInteger r = encryptedMessage.modPow(sk, pk);
        BigInteger m = encryptedMessage.multiply(r.modPow(pk, pk.pow(2)).modInverse(pk.pow(2))).mod(pk.pow(2)).subtract(BigInteger.ONE).divide(pk);
        response.add(r);
        response.add(m);
        return response;
    }

    public static void main(String[] args) {
        BigInteger message = BigInteger.valueOf(1234);
        Paillier systeme = new Paillier();
        systeme.keyGen();
        System.out.println("message à envoyer " + message);
        BigInteger encryptedMess = systeme.encrypt(message);
        System.out.println("message encrypté " + encryptedMess);
        System.out.println("message décrypté " + systeme.decrypt(encryptedMess));

    }
}
