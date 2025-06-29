package Neuroplanner.domain;

import java.util.ArrayList;
import java.util.List;

public class NeuroplannerManager {
    String installatienaam;
    private List<Gebruiker> alleGebruikers = new ArrayList<>();
    private List<Taak> alleTaken = new ArrayList<>();

    public NeuroplannerManager(String installatienaam){
        this.installatienaam = installatienaam;
    }
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NeuroplannerManager manager = (NeuroplannerManager) o;
        return installatienaam.equals(manager.installatienaam);
    }
    @Override
    public int hashCode() {
        return installatienaam.hashCode();
    }

    public String getInstallatienaam() {
        return installatienaam;
    }
    public void setInstallatienaam(String installatienaam) {
        this.installatienaam = installatienaam;
    }

    public List<Gebruiker> getAlleGebruikers() {
        return alleGebruikers;
    }

    public List<Taak> getAlleTaken() {
        return alleTaken;
    }
    public void voegGebruikerToe(Gebruiker gebruiker){alleGebruikers.add(gebruiker);}
    public void voegTaakToe(Taak taak){alleTaken.add(taak);}
    public boolean verwijderGebruiker(Gebruiker gebruiker){return alleGebruikers.remove(gebruiker);}
    public boolean verwijderTaak(Taak taak){return alleTaken.remove(taak);}

}
