package multitask2.model.games;

import multitask2.controller.Input;
import multitask2.model.Game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.github.forax.zen.KeyboardEvent.Key;

/**
 * Mini-jeu d'esquive : le joueur change de rangée pour éviter des missiles qui
 * traversent horizontalement la zone de jeu de gauche à droite.
 *
 * <p>
 * La cadence d'apparition et la vitesse des missiles augmentent avec la
 * difficulté. Une collision avec un missile arrivé au bord droit sur la rangée
 * du joueur met fin à la partie.
 * </p>
 */
public final class Avoid implements Game {

  /** Abscisse du bord droit de l'aire de jeu dans les coordonnées du modèle. */
  public static final double WIDTH = 100;
  /** Nombre de rangées disponibles pour le joueur et les missiles. */
  public static final int ROWS = 5;

  /** Vitesse de déplacement initiale d'un missile, en unités par seconde. */
  private static final double BASE_MISSILE_SPEED = 32;
  /** Intervalle initial entre deux apparitions de missiles, en secondes. */
  private static final double BASE_SPAWN_INTERVAL = 1.5;

  /**
   * Missile se déplaçant horizontalement sur une rangée fixe.
   *
   * @param x   position horizontale du missile dans les coordonnées du modèle
   * @param row indice de la rangée occupée par le missile
   */
  public record Missile(double x, int row) {

    /**
     * Crée un missile déplacé horizontalement, en conservant sa rangée.
     *
     * @param dx déplacement horizontal à ajouter à la position courante
     * @return nouvelle valeur immuable représentant le missile déplacé
     */
    private Missile moveRight(double dx) {
      return new Missile(x + dx, row);
    }
  }

  /** Rangée actuellement occupée par le joueur. */
  private int row;
  /** Missiles présents dans l'aire de jeu. */
  private final List<Missile> missiles;
  /** Temps restant avant la prochaine apparition d'un missile, en secondes. */
  private double spawnTimer;
  /** Générateur utilisé pour choisir aléatoirement la rangée des missiles. */
  private final Random random;

  /**
   * Crée le jeu avec le joueur sur la rangée centrale et initialise le compte à
   * rebours.
   */
  public Avoid() {
    row = ROWS / 2;
    missiles = new ArrayList<>();
    spawnTimer = 1;
    random = new Random();
    super();
  }

  /**
   * Traite les changements de rangée, fait apparaître et avance les missiles,
   * puis vérifie si l'un d'eux a atteint le joueur.
   *
   * @param dt         durée du pas de simulation, en secondes
   * @param input      état des entrées clavier
   * @param difficulty niveau de difficulté global
   * @return {@code true} si aucun missile n'a atteint le bord droit sur la rangée
   *         du joueur, {@code false} en cas de collision
   * @throws IllegalArgumentException si {@code dt} ou {@code difficulty} est
   *                                  négatif
   * @throws NullPointerException     si {@code input} est {@code null}
   */
  @Override
  public boolean update(double dt, Input input, double difficulty) {
    Game.checkInput(dt, input, difficulty);

    readInput(input);
    spawnMissiles(dt, difficulty);
    moveMissiles(dt, difficulty);
    return checkBorder();
  }

  /**
   * Déplace le joueur d'au plus une rangée en réponse à un nouvel appui sur la
   * flèche haut ou bas. Le déplacement est limité à l'intervalle des rangées
   * disponibles.
   *
   * @param input état des entrées clavier
   */
  private void readInput(Input input) {
    var dir = (input.isKeyJustPressed(Key.UP) ? 1 : 0) + (input.isKeyJustPressed(Key.DOWN) ? -1 : 0);
    if (dir != 0) {
      row = Game.withinBounds(row + dir, 0, ROWS - 1);
    }
  }

  /**
   * Diminue le compte à rebours et fait apparaître un missile sur une rangée
   * aléatoire lorsque celui-ci devient strictement négatif. L'intervalle diminue
   * avec la difficulté, sans passer sous 0,45 seconde.
   *
   * @param dt         durée du pas de simulation, en secondes
   * @param difficulty niveau de difficulté global
   */
  private void spawnMissiles(double dt, double difficulty) {
    var spawnInterval = Math.max(0.45, BASE_SPAWN_INTERVAL - difficulty * 0.15);
    spawnTimer -= dt;
    if (spawnTimer < 0) {
      missiles.add(new Missile(0.0, random.nextInt(ROWS)));
      spawnTimer = spawnInterval;
    }
  }

  /**
   * Déplace chaque missile vers la droite à la vitesse déterminée par la
   * difficulté.
   *
   * @param dt         durée du pas de simulation, en secondes
   * @param difficulty niveau de difficulté global
   */
  private void moveMissiles(double dt, double difficulty) {
    var missileSpeed = BASE_MISSILE_SPEED + difficulty * 10;
    missiles.replaceAll(m -> m.moveRight(dt * missileSpeed));
  }

  /**
   * Détecte si un missile a atteint le bord droit sur la rangée du joueur, puis
   * retire les missiles sortis de l'aire de jeu lorsque cela ne met pas fin à la
   * partie.
   *
   * @return {@code false} si un missile a atteint le joueur ; {@code true} sinon
   */
  private boolean checkBorder() {
    if (missiles.stream().anyMatch(m -> m.x >= WIDTH && m.row == row)) {
      return false;
    }
    missiles.removeIf(m -> m.x >= WIDTH);
    return true;
  }

  /**
   * Retourne la rangée actuellement occupée par le joueur.
   *
   * @return indice de rangée, compris entre {@code 0} et {@code ROWS - 1}
   */
  public int row() {
    return row;
  }

  /**
   * Retourne les missiles actuellement présents.
   *
   * <p>
   * La liste ne peut pas être modifiée par l'appelant, mais elle reflète les
   * modifications effectuées lors des mises à jour suivantes du jeu.
   * </p>
   *
   * @return vue non modifiable de la liste des missiles
   */
  public List<Missile> missiles() {
    return Collections.unmodifiableList(missiles);
  }
}