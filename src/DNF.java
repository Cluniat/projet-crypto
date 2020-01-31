import java.math.BigInteger;
import java.util.*;

public class DNF {

    public static List<BigInteger> calculateDNF(BigInteger x1, BigInteger x2, BigInteger x3, BigInteger x4, BigInteger x5, BigInteger pk) {
        // Nous faisons le choix d'utiliser la DNF suivante : x1 ^ x2 ^ ¬x3 v ¬x1 ^ x3 ^ x4 v ¬x1 ^ ¬x2 ^ ¬x5
        // Cette fonction correspond au action effectué par Alice dans l'exercice 105 pour permettre à Bob d'obtenir le résultat de la DNF
        BigInteger c1, c2, c3, notX1, notX2, notX3, notX4, notX5;
        Random rand = new Random();
        notX1 = Paillier.substract(Paillier.encrypt(BigInteger.ONE, pk), x1, pk);
        notX2 = Paillier.substract(Paillier.encrypt(BigInteger.ONE, pk), x2, pk);
        notX3 = Paillier.substract(Paillier.encrypt(BigInteger.ONE, pk), x3, pk);
        notX4 = Paillier.substract(Paillier.encrypt(BigInteger.ONE, pk), x4, pk);
        notX5 = Paillier.substract(Paillier.encrypt(BigInteger.ONE, pk), x5, pk);
        // On calcule l'encryption de chacune des clause de la DNF, et on multiplie ensuite le résultat par un nombre aléatoire
        // Pour que Bob n'ait aucune information.
        c1 = Paillier.substract(Paillier.add(Paillier.add(x1, x2, pk), notX3, pk), Paillier.encrypt(BigInteger.valueOf(3), pk), pk);
        c2 = Paillier.substract(Paillier.add(Paillier.add(notX1, x3, pk), x4, pk), Paillier.encrypt(BigInteger.valueOf(3), pk), pk);
        c3 = Paillier.substract(Paillier.add(Paillier.add(notX1, notX2, pk), notX5, pk), Paillier.encrypt(BigInteger.valueOf(3), pk), pk);
        c1 = Paillier.mult(c1, new BigInteger((pk.bitLength() - 1), 1, rand), pk);
        c2 = Paillier.mult(c2, new BigInteger((pk.bitLength() - 1), 1, rand), pk);
        c3 = Paillier.mult(c3, new BigInteger((pk.bitLength() - 1), 1, rand), pk);
        List<BigInteger> clauses = new ArrayList<>();
        clauses.add(c1);
        clauses.add(c2);
        clauses.add(c3);
        // On ajoute ensuite un nombre aléatoire de clause fausse pour que Bob ne puisse pas savoir le nombre de clauses de la DNF
        int n = rand.nextInt() % 5 + 4;
        for (int i = 0; i < n; i++) {
            clauses.add(Paillier.encrypt(new BigInteger((pk.bitLength() - 1), 1, rand), pk));
        }
        Collections.shuffle(clauses);
        return clauses;
    }

    public static boolean decryptClause(List<BigInteger> clause, BigInteger pk, BigInteger sk) {
        // A partir de la liste des clauses, Bob peut calculer le resultat de la DNF
        // Sans avoir obtenue d'information sur la DNF à part le nombre de clauses vérifié.
        BigInteger total = BigInteger.ONE;
        for (BigInteger c: clause
             ) {
            total = total.multiply(Paillier.decrypt(c, pk, sk));
        }
        return total.compareTo(BigInteger.ZERO) == 0;
    }

    public static void main(String[] args) {
        Paillier systeme = new Paillier();
        Keys keys = systeme.keyGen(1024);
        BigInteger pk = keys.pk();
        BigInteger sk = keys.sk();

        int x1, x2, x3, x4, x5;
        x1 = 0;
        x2 = 0;
        x3 = 1;
        x4 = 0;
        x5 = 0;

        // Cette encryption est faite par Bob avant d'envoyer les valeurs à Alice.
        BigInteger X1 = Paillier.encrypt(BigInteger.valueOf(x1), pk);
        BigInteger X2 = Paillier.encrypt(BigInteger.valueOf(x2), pk);
        BigInteger X3 = Paillier.encrypt(BigInteger.valueOf(x3), pk);
        BigInteger X4 = Paillier.encrypt(BigInteger.valueOf(x4), pk);
        BigInteger X5 = Paillier.encrypt(BigInteger.valueOf(x5), pk);

        List<BigInteger> clauses = calculateDNF(X1, X2, X3, X4, X5, pk);
        if(decryptClause(clauses, pk, sk)) {
            System.out.println("La DNF est vraie pour :");
        } else {
            System.out.println("La DNF est fausse pour : ");
        }
        System.out.println("x1 = " + x1 + "\nx2 = " + x2 + "\nx3 = " + x3 + "\nx4 = " + x4 + "\nx5 = " + x5);

    }


}
