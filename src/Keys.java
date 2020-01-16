import java.math.BigInteger;

public class Keys {
    private BigInteger _pk;
    private BigInteger _sk;

    public Keys(BigInteger _pk, BigInteger _sk) {
        this._pk = _pk;
        this._sk = _sk;
    }

    public BigInteger pk() {
        return _pk;
    }

    public BigInteger sk() {
        return _sk;
    }
}
