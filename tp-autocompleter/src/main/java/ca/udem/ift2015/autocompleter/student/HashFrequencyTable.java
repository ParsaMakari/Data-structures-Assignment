package ca.udem.ift2015.autocompleter.student;

import ca.udem.ift2015.autocompleter.model.FrequencyTable;

import java.util.HashMap;
import java.util.Set;

/**
 * Table de fréquences implémentée avec une HashMap.
 *
 * <p>Chaque appel à {@link #increment(String)} augmente le compteur du token
 * d'une unité. Les méthodes {@link #get(String)}, {@link #total()},
 * {@link #vocabulary()} et {@link #isEmpty()} permettent d'interroger la table.
 */
public class HashFrequencyTable implements FrequencyTable {

    // Ici on utilise une HashMap pour associer chaque token(String) à son compteur (Integer).
    private final HashMap<String, Integer> map = new HashMap<>();

    // totalCount est la somme des compteurs de l'ensemble des tokens.
    private int totalCount = 0;

    /**
     * TODO 1 — Incrémenter le compteur d'un token.
     *
     * <p>Si le token n'est pas encore présent, son compteur passe à 1.
     * Le compteur global {@code totalCount} est mis à jour à chaque appel.
     */
    @Override
    public void increment(String token) {
        Integer count  = this.map.get(token); 
        this.map.put(token, count == null ?  1 : count + 1);
        this.totalCount++;
    }

    /**
     * TODO 2 — Retourner le compteur d'un token.
     *
     * <p>Retourne 0 si le token est absent.
     */
    @Override
    public int get(String token) {
        Integer count  = this.map.get(token); 
        return count == null ? 0 : count;
        
    }

    /**
     * TODO 3 — Retourner le nombre total d'occurrences (somme de tous les compteurs).
     *
     * <p>Ce total doit être maintenu en temps constant — ne pas recalculer
     * à chaque appel.
     */
    @Override
    public int total() {
        return this.totalCount;
    }

    /**
     * TODO 4 — Retourner l'ensemble des tokens connus.
     */
    @Override
    public Set<String> vocabulary() {
        return this.map.keySet();
    }

    /**
     * TODO 5 — Retourner {@code true} si aucun token n'a encore été incrémenté.
     */
    @Override
    public boolean isEmpty() {
        return this.totalCount == 0;
    }

    /**
     * TODO 6 — Ajouter {@code count} au compteur du token donné.
     *
     * <p>Si le token est absent, l'initialiser à {@code count}.
     * Mettre à jour {@code totalCount} en conséquence.
     */
    @Override
    public void incrementBy(String token, int count) {
        Integer current  = this.map.get(token); 
        this.map.put(token, current == null ? count : current + count);
        this.totalCount +=  count;
    }
}
