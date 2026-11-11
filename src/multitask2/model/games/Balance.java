package multitask2.model.games;

import com.github.forax.zen.KeyboardEvent.Key;

import multitask2.controller.Input;
import multitask2.model.Game;

/**
 * Mini-jeu d'équilibre : le joueur incline une planche et doit empêcher une
 * bille de sortir de ses limites.
 *
 * <p>
 * Les flèches gauche et droite règlent l'inclinaison. La gravité fait accélérer
 * la bille dans le sens de l'inclinaison, tandis qu'un facteur de friction
 * atténue sa vitesse.
 * </p>
 */
public final class Balance implements Game {

  /** Demi-longueur de la piste, en unités du modèle. */
  public static final double TRACK_HALF_LENGTH = 50;
  /** Valeur absolue maximale de l'inclinaison de la planche, en degrés. */
  public static final double MAX_TILT_DEGREES = 35;

  /** Vitesse de variation de l'inclinaison, en degrés par seconde. */
  private static final double TILT_SPEED_DEG_PER_SEC = 90;
  /** Accélération gravitationnelle de base utilisée par la simulation. */
  private static final double GRAVITY = 55;
  /** Coefficient de réduction de la vitesse appliqué à chaque mise à jour. */
  private static final double FRICTION = 0.995;

  /** Position longitudinale de la bille sur la piste, centrée en zéro. */
  private double position;
  /** Vitesse longitudinale de la bille. */
  private double speed;
  /** Inclinaison actuelle de la planche, en degrés. */
  private double tilt;

  /**
   * Crée un jeu avec la bille au centre, immobile, et une planche horizontale.
   */
  public Balance() {
    position = 0;
    speed = 0;
    tilt = 0;
    super();
  }

  /**
   * Met à jour les commandes d'inclinaison, la dynamique de la bille et vérifie
   * les limites.
   *
   * @param dt         durée du pas de simulation, en secondes
   * @param input      état des entrées clavier
   * @param difficulty niveau de difficulté global
   * @return {@code true} tant que la bille reste dans les limites de la piste ;
   *         {@code false} si elle dépasse l'une de ces limites
   * @throws IllegalArgumentException si {@code dt} ou {@code difficulty} est
   *                                  négatif
   * @throws NullPointerException     si {@code input} est {@code null}
   */
  @Override
  public boolean update(double dt, Input input, double difficulty) {
    Game.checkInput(dt, input, difficulty);

    readInput(dt, input);
    positionAndSpeed(dt, difficulty);
    return checkBorder();
  }

  /**
   * Modifie l'inclinaison selon les touches gauche et droite maintenues, sans
   * dépasser les angles autorisés. En l'absence de ces touches, l'inclinaison
   * reste inchangée.
   *
   * @param dt    durée du pas de simulation, en secondes
   * @param input état des entrées clavier
   */
  private void readInput(double dt, Input input) {
    var dir = (input.isKeyDown(Key.LEFT) ? -1 : 0) + (input.isKeyDown(Key.RIGHT) ? 1 : 0);
    if (dir != 0) {
      tilt = Game.dontExceed(tilt + dir * TILT_SPEED_DEG_PER_SEC * dt, MAX_TILT_DEGREES);
    }
  }

  /**
   * Calcule l'accélération liée à l'inclinaison, puis intègre la vitesse et la
   * position de la bille. La difficulté augmente l'accélération gravitationnelle
   * appliquée.
   *
   * @param dt         durée du pas de simulation, en secondes
   * @param difficulty niveau de difficulté global
   */
  private void positionAndSpeed(double dt, double difficulty) {
    var acceleration = GRAVITY * Math.sin(Math.toRadians(tilt)) * (1 + difficulty / 4);
    speed += acceleration * dt;
    speed *= FRICTION;
    position += speed * dt;
  }

  /**
   * Limite la position de la bille aux bords de la piste et détermine si elle les
   * a franchis.
   *
   * @return {@code true} si la position n'a pas été corrigée par la limitation,
   *         {@code false} si la bille a dépassé un bord
   */
  private boolean checkBorder() {
    var old = position;
    position = Game.dontExceed(position, TRACK_HALF_LENGTH);
    return old == position;
  }

  /**
   * Retourne la position longitudinale courante de la bille.
   *
   * @return position de la bille, comprise entre les limites de la piste après
   *         une mise à jour
   */
  public double position() {
    return position;
  }

  /**
   * Retourne l'inclinaison courante de la planche.
   *
   * @return inclinaison en degrés
   */
  public double tilt() {
    return tilt;
  }
}