package Neuroplanner.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Taak {
    private TaakType type;
    private TaakKleur kleur;
    private String titel;
    private String omschrijving;
    private LocalDateTime startTijd;
    private LocalDateTime eindTijd;
    private boolean voltooid;
    private Gebruiker gebruiker;

    public Taak(String titel, String omschrijving,TaakType type, TaakKleur kleur, Gebruiker gebruiker) {
        this.titel = titel;
        this.type = type;
        this.kleur = kleur;
        this.gebruiker = gebruiker;
    }
    public boolean isVoltooid() {
        return voltooid;
    }

    public void setVoltooid(boolean voltooid) {
        this.voltooid = voltooid;
    }
    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public TaakType getType() {
        return type;
    }

    public void setType(TaakType type) {
        this.type = type;
    }

    public TaakKleur getKleur() {
        return kleur;
    }

    public void setKleur(TaakKleur kleur) {
        this.kleur = kleur;
    }


    public LocalDateTime getStartTijd() {
        return startTijd;
    }

    public void setStartTijd(LocalDateTime startTijd) {
        this.startTijd = startTijd;
    }

    public LocalDateTime getEindTijd() {
        return eindTijd;
    }

    public void setEindTijd(LocalDateTime eindTijd) {
        this.eindTijd = eindTijd;
    }

    public Gebruiker getGebruiker(){
        return gebruiker;
    }
    public void setGebruiker(Gebruiker gebruiker){
        this.gebruiker = gebruiker;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Taak)) return false;
        Taak taak = (Taak) o;
        return Objects.equals(titel, taak.titel) &&
                type == taak.type &&
                kleur == taak.kleur &&
                Objects.equals(omschrijving, taak.omschrijving) &&
                Objects.equals(startTijd, taak.startTijd) &&
                Objects.equals(eindTijd, taak.eindTijd);
    }
    @Override
    public int hashCode() {
        return Objects.hash(titel, omschrijving, type, kleur, startTijd, eindTijd);
    }


}
