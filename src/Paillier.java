import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

public class Paillier {

    private BigInteger pk;
    private BigInteger sk;

    public Keys keyGen(int k) {
        //soit k = 1024

        // Générer p et q de k bits
        Random rand = new Random();
        BigInteger p = new BigInteger(k, 1, rand);
        BigInteger q = new BigInteger(k, 1, rand);

        // Calculer n = pq
        BigInteger n = p.multiply(q);

        //Calculer phin = (p-1)(q-1)
        BigInteger phi_n = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        // calculer rho = n^-1 mod phi
        BigInteger rho = n.modInverse(phi_n);

        this.pk = n;
        this.sk = rho;

        return new Keys(n, rho);
    }

    public static BigInteger encrypt(BigInteger message, Keys keys) {
        Random rand = new Random();
        BigInteger r = new BigInteger(keys.pk().bitLength(), 1, rand).mod(keys.pk());
        return message.multiply(keys.pk())
                .add(BigInteger.ONE)
                .multiply(r.modPow(keys.pk(), keys.pk().pow(2)))
                .mod(keys.pk().pow(2));
    }

    public static BigInteger decrypt(BigInteger encryptedMessage, Keys keys) {
        BigInteger r = encryptedMessage.modPow(keys.sk(), keys.pk());
        return  encryptedMessage.multiply(r.modPow(keys.pk(), keys.pk().pow(2)).modInverse(keys.pk().pow(2)))
                .mod(keys.pk().pow(2))
                .subtract(BigInteger.ONE)
                .divide(keys.pk());
    }

    // ====================== OPERATIONS ===========================

    public static BigInteger add(BigInteger encryption1, BigInteger encryption2, Keys keys) {
        return encryption1.multiply(encryption2).mod(keys.pk().pow(2));
    }

    public static BigInteger substract(BigInteger encryption1, BigInteger encryption2, Keys keys) {
        // Substraction is just the opposite of addition
        return add(encryption1, encryption2.modPow(BigInteger.ONE.negate(), keys.pk().pow(2)), keys);
    }

    public static BigInteger mult(BigInteger encryption, BigInteger factor, Keys keys) {
        return encryption.modPow(factor, keys.pk().pow(2));
    }


    // ===============================================================================

    public static BigInteger[] decryptPlus(BigInteger encryptedMessage, Keys keys) {
        // FIXME J'ai dû oublier des trucs parce que je vois pas ce que ça fait...
        //ArrayList<BigInteger> response = new ArrayList<>();
        BigInteger r = encryptedMessage.modPow(keys.sk(), keys.pk());
        BigInteger m = encryptedMessage.multiply(r.modPow(keys.pk(), keys.pk().pow(2)).modInverse(keys.pk().pow(2))).mod(keys.pk().pow(2)).subtract(BigInteger.ONE).divide(keys.pk());
        BigInteger[] response = new BigInteger[] {m, r};
        return response;
    }

    public static void main(String[] args) {
        BigInteger message = BigInteger.valueOf(1234);
        Paillier systeme = new Paillier();
        Keys keys = systeme.keyGen();
        System.out.println("message à envoyer " + message);
        BigInteger encryptedMess = systeme.encrypt(message, keys);
        System.out.println("message encrypté " + encryptedMess);
        System.out.println("message décrypté " + systeme.decrypt(encryptedMess, keys));

    }

    /**
     * @deprecated Kept for compatibility
     * @param message
     * @return
     */
    public BigInteger encrypt(BigInteger message) {
        return encrypt(message, new Keys(this.pk, this.sk));
    }

    /**
     * @deprecated Kept for compatibility
     * @param encryptedMessage
     * @return
     */
    public BigInteger decrypt(BigInteger encryptedMessage) {
        return  decrypt(encryptedMessage, new Keys(this.pk, this.sk));
    }

    public static BigInteger encrypt(BigInteger message, BigInteger pk) {
        return encrypt(message, new Keys(pk, null));
    }

    public static BigInteger decrypt(BigInteger encryptedMessage, BigInteger pk, BigInteger sk) {
        return  decrypt(encryptedMessage, new Keys(pk, sk));
    }

    public static BigInteger[] decryptPlus(BigInteger encryptedMessage, BigInteger pk, BigInteger sk) {
        return decryptPlus(encryptedMessage, new Keys(pk, sk));
    }

    public static BigInteger add(BigInteger encryption1, BigInteger encryption2, BigInteger pk) {
        return add(encryption1, encryption2, new Keys(pk, null));
    }

    public static BigInteger mult(BigInteger encryption, BigInteger factor, BigInteger pk) {
        return mult(encryption, factor, new Keys(pk, null));
    }

    public static BigInteger substract(BigInteger encryption1, BigInteger encryption2, BigInteger pk) {
        return substract(encryption1, encryption2, new Keys(pk, null));
    }


    /**
     * @deprecated Kept for compatibility
     * @return
     */
    public Keys keyGen() {
        return keyGen(1024);
    }
}
