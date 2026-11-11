package multitask2.model;

import java.util.Objects;

import multitask2.controller.Input;

/**
 * Contrat commun à tous les mini-jeux gérés par le modèle.
 *
 * <p>
 * Chaque jeu fait évoluer son état en fonction du temps écoulé, des entrées
 * clavier et du niveau de difficulté global. Les méthodes statiques fournissent
 * des contrôles de paramètres et des fonctions de limitation de valeurs
 * réutilisables par les implémentations.
 * </p>
 */
public interface Game {

  /**
   * Fait évoluer l'état du mini-jeu d'un pas de simulation.
   *
   * @param dt         durée du pas de simulation, en secondes
   * @param input      état courant des entrées clavier
   * @param difficulty niveau de difficulté global fourni par le modèle
   * @return {@code true} si la partie peut continuer, {@code false} si ce
   *         mini-jeu a échoué
   */
  boolean update(double dt, Input input, double difficulty);

  /**
   * Vérifie les préconditions communes utilisées par les mini-jeux.
   *
   * @param dt         durée du pas de simulation, en secondes
   * @param input      état des entrées clavier, qui doit être non nul
   * @param difficulty niveau de difficulté, qui doit être non négatif
   * @throws IllegalArgumentException si {@code dt} ou {@code difficulty} est
   *                                  négatif
   * @throws NullPointerException     si {@code input} est {@code null}
   */
  static void checkInput(double dt, Input input, double difficulty) {
    if (dt < 0) {
      throw new IllegalArgumentException("The time difference cannot be negative.");
    }
    Objects.requireNonNull(input);
    if (difficulty < 0) {
      throw new IllegalArgumentException("The difficulty cannot be negative.");
    }
  }

  /**
   * Limite une valeur à l'intervalle symétrique défini par une borne.
   *
   * @param value valeur à limiter
   * @param bound demi-amplitude de l'intervalle ; elle est attendue non négative.
   * @return résultat des opérations {@link Math#min(double, double)} et
   *         {@link Math#max(double, double)} ; si {@code bound} est non négative,
   *         le résultat appartient à l'intervalle {@code [-bound, bound]}
   * @throws IllegalArgumentException si {@code bound} est négatif
   */
  static double dontExceed(double value, double bound) {
    if (bound < 0) {
      throw new IllegalArgumentException("The upper bound cannot be negative.");
    }
    return Math.min(Math.max(value, -bound), bound);
  }

  /**
   * Limite un entier à un intervalle défini par une borne minimale et une borne
   * maximale.
   *
   * @param value entier à limiter
   * @param min   borne minimale de l'intervalle
   * @param max   borne maximale de l'intervalle
   * @return résultat des opérations de limitation ; lorsque {@code min <= max},
   *         la valeur est comprise dans l'intervalle {@code [min, max]}
   * @throws IllegalArgumentException si {@code max < min}
   */
  static int withinBounds(int value, int min, int max) {
    if (max < min) {
      throw new IllegalArgumentException("The target interval cannot be empty.");
    }
    return Math.min(Math.max(value, min), max);
  }
}