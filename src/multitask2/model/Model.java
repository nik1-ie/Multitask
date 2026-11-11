package multitask2.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import multitask2.controller.Input;

/**
 * Modèle central de la partie : conserve les mini-jeux, les active à leur
 * échéance et fait évoluer ceux qui sont actifs.
 *
 * <p>
 * Le temps de partie augmente à chaque appel réussi à
 * {@link #update(double, Input)}. Le niveau de difficulté commun augmente
 * linéairement avec ce temps, à raison d'une unité toutes les 5 secondes.
 * </p>
 */
public final class Model {

  /** Mini-jeux enregistrés, dans leur ordre d'ajout. */
  private final List<Slot> slots;
  /** Temps cumulé depuis le début du modèle, en secondes. */
  private double elapsedTime;

  /** Crée un modèle vide dont le temps écoulé est nul. */
  public Model() {
    slots = new ArrayList<>();
    elapsedTime = 0;
    super();
  }

  /**
   * Ajoute un mini-jeu et son instant d'activation au modèle.
   *
   * @param game           mini-jeu à enregistrer
   * @param activationTime instant d'activation depuis le début de la partie, en
   *                       secondes
   * @throws NullPointerException     si {@code game} est {@code null}
   * @throws IllegalArgumentException si {@code activationTime} est négatif
   */
  public void addGame(Game game, int activationTime) {
    Objects.requireNonNull(game);
    if (activationTime < 0) {
      throw new IllegalArgumentException("Activation time cannot be negative.");
    }
    slots.add(new Slot(game, activationTime));
  }

  /**
   * Avance le temps de la partie et met à jour tous les mini-jeux qui sont
   * actifs.
   *
   * <p>
   * Avant de mettre à jour chaque jeu, cette méthode l'active dès que son instant
   * d'activation est atteint. La difficulté transmise aux jeux vaut le temps
   * écoulé divisé par 5. Dès qu'un mini-jeu renvoie {@code false}, la mise à jour
   * s'arrête et la méthode signale la fin de la partie.
   * </p>
   *
   * @param dt    durée à ajouter au temps de partie, en secondes
   * @param input état courant des entrées clavier
   * @return {@code true} si tous les mini-jeux actifs autorisent la poursuite de
   *         la partie, {@code false} dès qu'un mini-jeu signale un échec
   * @throws IllegalArgumentException si {@code dt} est négatif
   * @throws NullPointerException     si {@code input} est {@code null}
   */
  public boolean update(double dt, Input input) {
    if (dt < 0) {
      throw new IllegalArgumentException("Time difference cannot be negative.");
    }
    Objects.requireNonNull(input);

    elapsedTime += dt;
    var difficulty = elapsedTime / 5;

    for (var slot : slots) {
      if (!slot.active() && elapsedTime >= slot.activationTime()) {
        slot.start();
      }
      if (slot.active()) {
        if (!slot.game().update(dt, input, difficulty)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Retourne la liste des emplacements de mini-jeux dans l'ordre de leur
   * enregistrement.
   *
   * <p>
   * La liste retournée ne peut pas être modifiée par l'appelant, mais elle
   * reflète les modifications ultérieures de la liste interne du modèle.
   * </p>
   *
   * @return vue non modifiable de la liste des emplacements
   */
  public List<Slot> slots() {
    return Collections.unmodifiableList(slots);
  }

  /**
   * Retourne le temps de partie écoulé, tronqué à la seconde entière inférieure.
   *
   * @return nombre de secondes entières écoulées depuis le début de la partie
   */
  public int time() {
    return (int) elapsedTime;
  }
}