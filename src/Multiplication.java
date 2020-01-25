import java.math.BigInteger;
import java.util.Random;

public class Multiplication {



    public Multiplication() {

    }

    public static BigInteger[][] multi1(BigInteger pk, BigInteger X, BigInteger Y) {
        Random rand = new Random();
        BigInteger r = new BigInteger(pk.bitLength(), 1, rand).mod(pk);
        BigInteger s = new BigInteger(pk.bitLength(), 1, rand).mod(pk);
        BigInteger R = Paillier.encrypt(r, pk);
        BigInteger S = Paillier.encrypt(s, pk);
        BigInteger sumR = Paillier.add(R, X, pk);
        BigInteger sumS = Paillier.add(S, Y, pk);
        BigInteger[][] a = new BigInteger[][] {{sumR, sumS}, {r, s}};
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

    public static BigInteger[] multiProof1(BigInteger pk, BigInteger sk, BigInteger beta){
        Random rand = new Random();
        BigInteger delta = new BigInteger(pk.bitLength(), 1, rand).mod(pk);
        beta = Paillier.decrypt(beta, pk, sk);
        BigInteger pi = delta.multiply(beta).mod(pk);
        BigInteger encryptedPi = Paillier.encrypt(pi, pk);
        BigInteger encryptedDelta = Paillier.encrypt(delta, pk);
        BigInteger[] r = new BigInteger[] {encryptedDelta, encryptedPi};
        return r;
    }

    public static BigInteger multiProof2(BigInteger pk) {
        Random rand = new Random();
        BigInteger e = new BigInteger(pk.bitLength() - 1, 1, rand);
        return e;
    }

    public static BigInteger[][] multiProof3(BigInteger alpha, BigInteger beta, BigInteger gamma, BigInteger delta, BigInteger e, BigInteger pi, BigInteger pk, BigInteger sk) {
        BigInteger encrypteda1 = alpha;
        encrypteda1 = Paillier.mult(encrypteda1, e, pk);
        encrypteda1 = Paillier.add(encrypteda1, delta, pk);
        BigInteger[] ar = Paillier.decryptPlus(encrypteda1, pk, sk);

        BigInteger encrypteda2 = beta;
        encrypteda2 = Paillier.mult(encrypteda2, ar[0], pk);
        encrypteda2 = Paillier.substract(encrypteda2, pi, pk);
        encrypteda2 = Paillier.substract(encrypteda2, Paillier.mult(gamma, e, pk), pk);
        BigInteger[] a2r2 = Paillier.decryptPlus(encrypteda2, pk, sk);

        BigInteger[][] response = new BigInteger[][] {ar, a2r2};
        return response;
    }

    public static boolean multiProof4(BigInteger a1, BigInteger r1, BigInteger a2, BigInteger r2, BigInteger encryptedAlpha,
                                      BigInteger encryptedBeta, BigInteger encryptedGamma, BigInteger encryptedDelta, BigInteger encryptedPi, BigInteger e, BigInteger pk) {
        BigInteger enc1 = a1.multiply(pk)
                .add(BigInteger.ONE)
                .multiply(r1.modPow(pk, pk.pow(2)))
                .mod(pk.pow(2));
        BigInteger comp1 = encryptedAlpha.modPow(e, pk.pow(2)).multiply(encryptedDelta).mod(pk.pow(2));
        if(!(enc1.compareTo(comp1) == 0)) {
            return false;
        }
        BigInteger enc2 = a2.multiply(pk)
                .add(BigInteger.ONE)
                .multiply(r2.modPow(pk, pk.pow(2)))
                .mod(pk.pow(2));
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
        // Sans utiliser multproof
        Paillier systeme = new Paillier();
        Keys keys = systeme.keyGen(1024);
        BigInteger pk = keys.pk();
        BigInteger sk = keys.sk();
        int val1 = 2345;
        int val2 = 10230;
        System.out.println("x = " + val1);
        System.out.println("y = " + val2);
        BigInteger X = Paillier.encrypt(BigInteger.valueOf(val1), pk);
        BigInteger Y = Paillier.encrypt(BigInteger.valueOf(val2), pk);
        BigInteger[][] ret = Multiplication.multi1(pk, X, Y);
        BigInteger sumX = ret[0][0];
        BigInteger sumY = ret[0][1];
        BigInteger r = ret[1][0];
        BigInteger s = ret[1][1];
        BigInteger encryptedProd = Multiplication.multi2(pk, sk, sumX, sumY);
        BigInteger result = Multiplication.multi3(pk, encryptedProd, X, Y, r, s);
        BigInteger res = Paillier.decrypt(result, keys);
        System.out.println("x * y = " + res);

        //En utilisant multProof
        val1 = 2345;
        val2 = 10230;
        System.out.println("x = " + val1);
        System.out.println("y = " + val2);
        X = Paillier.encrypt(BigInteger.valueOf(val1), pk);
        Y = Paillier.encrypt(BigInteger.valueOf(val2), pk);
        ret = Multiplication.multi1(pk, X, Y);
        sumX = ret[0][0];
        sumY = ret[0][1];
        r = ret[1][0];
        s = ret[1][1];
        encryptedProd = Multiplication.multi2(pk, sk, sumX, sumY);
        BigInteger[] rep = Multiplication.multiProof1(pk, sk, sumY);
        BigInteger delta = rep[0];
        BigInteger pi = rep[1];
        BigInteger e = Multiplication.multiProof2(pk);
        ret = Multiplication.multiProof3(sumX, sumY, encryptedProd, delta, e, pi, pk, sk);
        BigInteger a1 = ret[0][0];
        BigInteger r1 = ret[0][1];
        BigInteger a2 = ret[1][0];
        BigInteger r2 = ret[1][1];
        if(Multiplication.multiProof4(a1, r1, a2, r2, sumX, sumY, encryptedProd, delta, pi, e, pk)) {
            System.out.println("Le protocole est respecté par Alice");
            result = Multiplication.multi3(pk, encryptedProd, X, Y, r, s);
            res = Paillier.decrypt(result, keys);
            System.out.println("x * y = " + res);
        } else {
            System.out.println("Protocole interrompu");
        }

    }
}
