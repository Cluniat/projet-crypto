public class CryptoMariage1 {

    static private int[] ASendPosition() {
        int[] position = {457790870, 486923170};
        return position;
    }

    static private boolean getDistance(int[] positionA) {
//        int[] positionB = {457701431, 489542721};
        int[] positionB = {457790872, 486923172};
        double x = Math.abs(positionA[0] - positionB[0]);
        double y = Math.abs(positionA[1] - positionB[1]);
        if (x > 100 || y > 100) {
            return false;
        } else {
            return Math.sqrt(x) + Math.sqrt(y) < 10000;
        }
    }

    public static void main(String[] args) {
        //Alice envoie sa position au bracelet de Bob
        int[] alice = ASendPosition();

        //Le bracelet de Bob calcule s'il est trop près ou non d'Alice ...
        boolean tooclose = getDistance(alice);

        //... et envoie sa conclusion à Alice
        if (tooclose) {
            System.out.println("Attention Bob est de retour!");
        } else {
            System.out.println("Tout va bien, Alice n'entend plus parler de Bob.");
        }

    }


}
