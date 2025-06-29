package Neuroplanner.domain;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Gebruiker {

    private String gebruikersnaam;
    private String wachtwoord;
    private String voornaam;
    private String achternaam;
    private Rol rol;
    private List<Taak> eigentaken = new ArrayList<>();
    public Gebruiker(String gebruikersnaam, String wachtwoord, String voornaam, String achternaam, Rol rol) {
        this.gebruikersnaam = gebruikersnaam;
        this.wachtwoord = wachtwoord;
        this.voornaam = voornaam;
        this.achternaam = achternaam;
        this.rol = rol;
    }
    public String getGebruikersnaam() {
        return gebruikersnaam;
    }

    public void setGebruikersnaam(String gebruikersnaam) {
        this.gebruikersnaam = gebruikersnaam;
    }

    public String getWachtwoord() {
        return wachtwoord;
    }

    public void setWachtwoord(String wachtwoord) {
        this.wachtwoord = wachtwoord;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
    public String getVoornaam(){return voornaam;}
    public void setVoornaam(String voornaam){this.voornaam = voornaam;}
    public String getAchternaam(){return achternaam;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Gebruiker)) return false;
        Gebruiker gebruiker = (Gebruiker) o;
        return gebruikersnaam.equals(gebruiker.gebruikersnaam);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gebruikersnaam);
    }

    public void VoegTaakToe(Taak taak){this.eigentaken.add(taak);}
    public boolean VerwijderTaak(Taak taak){return this.eigentaken.remove(taak);}
}
