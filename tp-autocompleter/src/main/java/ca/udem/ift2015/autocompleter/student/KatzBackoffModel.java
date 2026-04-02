package ca.udem.ift2015.autocompleter.student;

import ca.udem.ift2015.autocompleter.model.FrequencyTable;
import ca.udem.ift2015.autocompleter.model.NGramModel;
import ca.udem.ift2015.autocompleter.model.TopKStrategy;
import ca.udem.ift2015.autocompleter.model.Trie;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Modèle de langage trigramme avec repli de Katz (Katz Backoff).
 *
 * <p>Structures de données internes :
 * <ul>
 *   <li>{@code unigrams} — fréquence de chaque mot du corpus</li>
 *   <li>{@code bigrams}  — clé = mot précédent, valeur = table de fréquences du mot suivant</li>
 *   <li>{@code trigrams} — clé = {@code "w1 w2"}, valeur = table de fréquences du mot suivant</li>
 *   <li>{@code trie}     — trie préfixe pour complétion de mots</li>
 * </ul>
 */
public class KatzBackoffModel implements NGramModel {

    private final FrequencyTable unigrams;
    private final Map<String, FrequencyTable> bigrams;
    private final Map<String, FrequencyTable> trigrams;
    private final Trie trie;
    private final TopKStrategy strategy;

    public KatzBackoffModel(TopKStrategy strategy) {
        this.strategy = strategy;
        this.unigrams = new HashFrequencyTable();
        this.bigrams  = new HashMap<>();
        this.trigrams = new HashMap<>();
        this.trie     = new PrefixTrie();
    }

    // TODO 11
    @Override
    public void train(List<List<String>> sentences) {
        for (List<String> sentence : sentences) {
            for (int i = 0; i < sentence.size(); i++) {
                String wi = sentence.get(i);

                unigrams.increment(wi);
                trie.insert(wi);

                if (i >= 1) {
                    String prev = sentence.get(i - 1);
                    bigrams.computeIfAbsent(prev, k -> new HashFrequencyTable())
                            .increment(wi);
                }

                if (i >= 2) {
                    String key = sentence.get(i - 2) + " " + sentence.get(i - 1);
                    trigrams.computeIfAbsent(key, k -> new HashFrequencyTable())
                            .increment(wi);
                }
            }
        }
    }

    // TODO 12
    @Override
    public List<String> topK(int k, String... context) {
        if (k <= 0 || unigrams.isEmpty()) return Collections.emptyList();

        int n = context.length;

        if (n >= 2) {
            String triKey = context[n - 2] + " " + context[n - 1];
            if (trigrams.containsKey(triKey)) {
                return strategy.topK(trigrams.get(triKey), k);
            }
        }

        if (n >= 1) {
            String prev = context[n - 1];
            if (bigrams.containsKey(prev)) {
                return strategy.topK(bigrams.get(prev), k);
            }
        }

        return strategy.topK(unigrams, k);
    }

    // TODO 13
    @Override
    public String predict(String... context) {
        List<String> suggestions = topK(1, context);
        return suggestions.isEmpty() ? null : suggestions.get(0);
    }

    // TODO 14
    @Override
    public List<String> complete(String prefix, int k) {
        return trie.complete(prefix, k);
    }

    /** Fourni — délègue à unigrams.get() (fonctionnel dès que TODO 2 est implémenté). */
    @Override
    public int frequency(String word) {
        return unigrams.get(word);
    }

    @Override
    public int unigramCount() {
        return unigrams.vocabulary().size();
    }

    @Override
    public int bigramCount() {
        return bigrams.values().stream()
                .mapToInt(t -> t.vocabulary().size())
                .sum();
    }

    @Override
    public int trigramCount() {
        return trigrams.values().stream()
                .mapToInt(t -> t.vocabulary().size())
                .sum();
    }
}