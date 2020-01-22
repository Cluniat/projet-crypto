import java.math.BigInteger;
import java.util.Random;

public class Multiplication {



    public Multiplication() {

    }

    public static BigInteger[] multi1(BigInteger pk, BigInteger X, BigInteger Y) {
        Random rand = new Random();
        BigInteger r = new BigInteger(pk.bitLength(), 1, rand).mod(pk);
        BigInteger s = new BigInteger(pk.bitLength(), 1, rand).mod(pk);
        BigInteger R = Paillier.encrypt(r, pk);
        BigInteger S = Paillier.encrypt(s, pk);
        BigInteger sumR = Paillier.add(R, X, pk);
        BigInteger sumS = Paillier.add(S, Y, pk);
        BigInteger[] a = new BigInteger[] {sumR, sumS};
        return a;
    }

    public static BigInteger multi2(BigInteger pk, BigInteger sk, BigInteger encryptedSumX, BigInteger encryptedSumY) {
        BigInteger sumX = Paillier.decrypt(encryptedSumX, pk, sk);
        BigInteger sumY = Paillier.decrypt(encryptedSumY, pk, sk);
        BigInteger prod = sumX.multiply(sumY).mod(pk);
        BigInteger encryptedProd = Paillier.encrypt(prod, pk);
        return  encryptedProd;
    }

    public static BigInteger multi3(BigInteger pk, BigInteger prod, BigInteger X, BigInteger Y, BigInteger r, BigInteger s) {
        BigInteger sX = Paillier.mult(X, s, pk);
        BigInteger rY = Paillier.mult(Y, r, pk);
        BigInteger rs = s.multiply(r).mod(pk);
        BigInteger RS = Paillier.encrypt(rs, pk);
        BigInteger encryptedResult = Paillier.substract(prod, sX, pk);
        encryptedResult = Paillier.substract(encryptedResult, rY, pk);
        encryptedResult = Paillier.substract(encryptedResult, RS, pk);
        return encryptedResult;
    }

    public static void multiProof1(){

    }

    public static BigInteger multiProof2(BigInteger pk) {
        Random rand = new Random();
        BigInteger e = new BigInteger(pk.bitLength() - 1, 1, rand);
        return e;
    }

    public static BigInteger[][] multiProof3(BigInteger alpha, BigInteger beta, BigInteger gamma, BigInteger delta, BigInteger e, BigInteger pi, BigInteger pk, BigInteger sk) {
        BigInteger encrypteda1 = Paillier.encrypt(alpha, pk);
        encrypteda1 = Paillier.mult(encrypteda1, e, pk);
        encrypteda1 = Paillier.add(encrypteda1, Paillier.encrypt(delta, pk), pk);
        BigInteger[] ar = Paillier.decryptPlus(encrypteda1, pk, sk);

        BigInteger encrypteda2 = Paillier.encrypt(beta, pk);
        encrypteda2 = Paillier.mult(encrypteda2, ar[0], pk);
        encrypteda2 = Paillier.substract(encrypteda2, Paillier.encrypt(pi, pk), pk);
        encrypteda2 = Paillier.substract(encrypteda2, Paillier.mult(Paillier.encrypt(gamma, pk), e, pk), pk);
        BigInteger[] a2r2 = Paillier.decryptPlus(encrypteda2, pk, sk);

        BigInteger[][] response = new BigInteger[][] {ar, a2r2};
        return response;
    }

    public static boolean multiProof4(BigInteger a1, BigInteger r1, BigInteger a2, BigInteger r2, BigInteger encryptedAlpha,
                                      BigInteger encryptedBeta, BigInteger encryptedGamma, BigInteger encryptedDelta, BigInteger encryptedPi, BigInteger e, BigInteger pk) {
        BigInteger enc1 = Paillier.encrypt(a1, pk);
        BigInteger comp1 = encryptedAlpha.modPow(e, pk.pow(2)).multiply(encryptedDelta).mod(pk.pow(2));
        if(!(enc1.compareTo(comp1) == 0)) {
            return false;
        }
        BigInteger enc2 = Paillier.encrypt(a2, pk);
        BigInteger comp2 = encryptedBeta.modPow(a1, pk.pow(2)).
                multiply(encryptedPi.modPow(BigInteger.ONE.negate(), pk.pow(2))).
                multiply(encryptedGamma.modPow(e.multiply(BigInteger.ONE.negate()), pk.pow(2))).mod(pk.pow(2));
        if(!(enc2.compareTo(comp2) == 0)) {
            return false;
        }
        if(!(a2.compareTo(BigInteger.ZERO) == 0)) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        System.out.println(12);
    }
}
