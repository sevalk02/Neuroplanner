package Neuroplanner.domain;

public class Instelling {
    private String gebruikersnaam;
    private boolean donkereModus;
    private boolean toonKalender;
    private boolean toonTakenlijst;
    private String overzichtWeergave;

    public Instelling() {}

    public Instelling(String gebruikersnaam, boolean donkereModus, boolean toonKalender, boolean toonTakenlijst, String overzichtWeergave) {
        this.gebruikersnaam = gebruikersnaam;
        this.donkereModus = donkereModus;
        this.toonKalender = toonKalender;
        this.toonTakenlijst = toonTakenlijst;
        this.overzichtWeergave = overzichtWeergave;
    }

    public String getGebruikersnaam() {
        return gebruikersnaam;
    }

    public void setGebruikersnaam(String gebruikersnaam) {
        this.gebruikersnaam = gebruikersnaam;
    }

    public boolean isDonkereModus() {
        return donkereModus;
    }

    public void setDonkereModus(boolean donkereModus) {
        this.donkereModus = donkereModus;
    }

    public boolean isToonKalender() {
        return toonKalender;
    }

    public void setToonKalender(boolean toonKalender) {
        this.toonKalender = toonKalender;
    }

    public boolean isToonTakenlijst() {
        return toonTakenlijst;
    }

    public void setToonTakenlijst(boolean toonTakenlijst) {
        this.toonTakenlijst = toonTakenlijst;
    }

    public String getOverzichtWeergave() {
        return overzichtWeergave;
    }

    public void setOverzichtWeergave(String overzichtWeergave) {
        this.overzichtWeergave = overzichtWeergave;
    }

}
