/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GestioneSpesaWS;

/**
 *
 * @author Luca
 */
public class Risposta {
    private int idRisposta;
    private int rifUtente;

    public int getRifUtente() {
        return rifUtente;
    }

    public void setRifUtente(int rifUtente) {
        this.rifUtente = rifUtente;
    }
    private int rifRichiesta;

    public int getIdRisposta() {
        return idRisposta;
    }

    public void setIdRisposta(int idRisposta) {
        this.idRisposta = idRisposta;
    }

    public int getRifRichiesta() {
        return rifRichiesta;
    }

    public void setRifRichiesta(int rifRichiesta) {
        this.rifRichiesta = rifRichiesta;
    }

    public Risposta(int idRisposta, int rifUtente, int rifRichiesta) {
        this.idRisposta = idRisposta;
        this.rifUtente = rifUtente;
        this.rifRichiesta = rifRichiesta;
    }
    
    
}
