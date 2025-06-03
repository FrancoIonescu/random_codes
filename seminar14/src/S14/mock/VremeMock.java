package S14.mock;

import S14.clase.AbstractVreme;

public class VremeMock implements AbstractVreme {
    private boolean estePloaie;
    private int cantitatePrecipitatii;

    public VremeMock(boolean estePloaie, int cantitatePrecipitatii) {
        this.estePloaie = estePloaie;
        this.cantitatePrecipitatii = cantitatePrecipitatii;
    }

    @Override
    public boolean estePloaie() {
        return this.estePloaie;
    }

    @Override
    public int cantitatePrecipitatii() {
        return this.cantitatePrecipitatii;
    }
}
