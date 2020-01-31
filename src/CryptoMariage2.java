import java.math.BigInteger;

public class CryptoMariage2 {

    public static void main(String[] args) {
        Paillier systeme = new Paillier();
        Keys keys = systeme.keyGen(1024);
        BigInteger pk = keys.pk();
        BigInteger sk = keys.sk();

//        double posA_long = 4.868187;
//        double posA_lat = 45.779263;
        long posA_long = 486818;
        long posA_lat = 4577926;

        // ==========================================================
        // Cas où B est proche
//        double posB_long = 4.867652;
//        double posB_lat = 45.779181;
        long posB_long = 486765;
        long posB_lat = 4577918;
        printPosition("A", posA_lat, posA_long);
        printPosition("B", posB_lat, posB_long);

        BigInteger[] posA = p2_encryptAndSendPosition(pk, posA_lat, posA_long);
        BigInteger distB = p2_distanceB(pk, posA, posB_lat, posB_long);
        BigInteger decrypted_distance = Paillier.decrypt(distB, keys);
        if(decrypted_distance.compareTo(BigInteger.valueOf(10000)) < 0) {
            System.out.println("Bob est dans un rayon de 100 mètres autour d'Alice (" + decrypted_distance + "m)");
            System.out.println("Alice va donc demander au bracelet de Bob sa position exacte.");
            BigInteger r = Paillier.findR(distB, keys);
            long[] posB = p2_returnPositionB(pk, r, decrypted_distance, distB, posB_lat, posB_long);
            printPosition("B", posB[0], posB[1]);
        } else {
            System.out.println("Bob est loin. (" + decrypted_distance.longValue() + "m)");
        }

        // ==========================================================
        // Cas où B est loin
//        double posB_long_far = -115.8131452023175;
//        double posB_lat_far = 37.23679686497278;
        posB_long = -1158131452023175L;
        posB_lat = 3723679686497278L;

        printPosition("A", posA_lat, posA_long);
        printPosition("B", posB_lat, posB_long);

        posA = p2_encryptAndSendPosition(pk, posA_lat, posA_long);
        distB = p2_distanceB(pk, posA, posB_lat, posB_long);
        decrypted_distance = Paillier.decrypt(distB, keys);
        if(decrypted_distance.compareTo(BigInteger.valueOf(10000)) < 0) {
            System.out.println("Bob est dans un rayon de 100 mètres autour d'Alice");
            System.out.println("Alice va donc demander au bracelet de Bob sa position exacte.");
            BigInteger r = Paillier.findR(distB, keys);
            long[] posB = p2_returnPositionB(pk, r, decrypted_distance, distB, posB_lat, posB_long);
            printPosition("B", posB[0], posB[1]);
        } else {
            System.out.println("Bob est loin.");
        }
    }

    static BigInteger[] p2_encryptAndSendPosition(BigInteger pk, long latitude, long longitude) {
        BigInteger XA = Paillier.encrypt(BigInteger.valueOf(longitude), pk);
        BigInteger YA = Paillier.encrypt(BigInteger.valueOf(latitude), pk);
        BigInteger XA_squared = Paillier.encrypt(BigInteger.valueOf(longitude), pk);
        BigInteger YA_squared = Paillier.encrypt(BigInteger.valueOf(latitude), pk);
        return new BigInteger[]{XA, YA, XA_squared, YA_squared};
    }

    static BigInteger p2_distanceB(BigInteger pk, BigInteger[] posA, long latitude, long longitude) {
        BigInteger sum_posA_squared = Paillier.add(posA[2], posA[3], pk);

        BigInteger x_squared = BigInteger.valueOf(longitude).pow(2);
        BigInteger y_squared = BigInteger.valueOf(latitude).pow(2);
        BigInteger x_plus_y_squared = Paillier.encrypt(x_squared.add(y_squared), pk);

        BigInteger x_mults = Paillier.mult(posA[0], BigInteger.valueOf(-2*longitude), pk);
        BigInteger y_mults = Paillier.mult(posA[1], BigInteger.valueOf(-2*latitude), pk);

        BigInteger sum1 = Paillier.add(sum_posA_squared, x_plus_y_squared, pk);
        BigInteger sum2 = Paillier.add(x_mults, y_mults, pk);
        return Paillier.add(sum1, sum2, pk);
    }

    static long[] p2_returnPositionB(BigInteger pk, BigInteger r, BigInteger distB, BigInteger distBCrypted,
                                     long posB_lat, long posB_long) {
        BigInteger test_encryption = Paillier.encrypt_with_r(distB, pk, r);
        if(test_encryption.compareTo(distBCrypted) == 0) {
            return new long[]{posB_lat, posB_long};
        } else {
            return new long[]{0, 0};
        }
    }

    static void printPosition(String name, long latitude, long longitude) {
        System.out.println(name + ": ~(" + latitude + "°N, " + longitude + "°E)");
    }
}
