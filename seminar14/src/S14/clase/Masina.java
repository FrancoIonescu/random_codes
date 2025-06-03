package S14.clase;

import S14.exceptii.ExceptieAn;
import S14.exceptii.ExceptieModel;

public class Masina {
    private String model;
    private double pret;
    private int anFabricatie;

    public Masina() throws ExceptieModel, ExceptieAn {
        if (model == null || model.length() <= 2) {
            throw new ExceptieModel();
        }
        this.model = "Default";
        this.pret = 20000;
        if (anFabricatie < 1900 || anFabricatie > 2025) {
            throw new ExceptieAn();
        }
        this.anFabricatie = 1900;
    }

    public Masina(String model, double pret, int anFabricatie) {
        this.model = model;
        this.pret = pret;
        this.anFabricatie = anFabricatie;
    }

    public String getModel() {
        return model;
    }

    public double getPret() {
        return pret;
    }

    public int getAnFabricatie() {
        return anFabricatie;
    }

    public void setAnFabricatie(int anFabricatie) throws ExceptieAn {
        if (anFabricatie < 1900 || anFabricatie > 2025) {
            throw new ExceptieAn();
        }
        this.anFabricatie = anFabricatie;
    }
}
