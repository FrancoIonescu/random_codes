package s12.testare;

public class Masina {
    private String model;
    private double pret;
    private int nrKm;
    private int anFabricatie;

    public Masina(String model, double pret, int nrKm, int anFabricatie) {
        this.model = model;
        this.pret = pret;
        this.nrKm = nrKm;
        this.anFabricatie = anFabricatie;
    }

    public String getModel() {
        return model;
    }

    public double getPret() {
        return pret;
    }

    public int getNrKm() {
        return nrKm;
    }

    public int getAnFabricatie() {
        return anFabricatie;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setPret(double pret) {
        this.pret = pret;
    }

    // nrKm > 0

    public void setNrKm(int nrKm) throws ExceptieNrKm {
        if (nrKm < 0) {
            throw new ExceptieNrKm();
        } else {
            this.nrKm = nrKm;
        }
    }

    public void setAnFabricatie(int anFabricatie) {
        this.anFabricatie = anFabricatie;
    }

    @Override
    public String toString() {
        return "Masina{" +
                "model='" + model + '\'' +
                ", pret=" + pret +
                ", nrKm=" + nrKm +
                ", anFabricatie=" + anFabricatie +
                '}';
    }
}
