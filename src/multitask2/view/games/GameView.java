package multitask2.view.games;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Objects;

import multitask2.model.Game;

/**
 * Contrat des vues spécialisées qui dessinent le contenu d'un mini-jeu dans une
 * zone donnée.
 */
public interface GameView {

  /**
   * Dessine l'état actuel du mini-jeu dans la zone rectangulaire fournie.
   *
   * @param graphics contexte graphique à utiliser pour le dessin
   * @param area     zone disponible pour le rendu, en pixels
   */
  void render(Graphics2D graphics, Rectangle area);

  /**
   * Vérifie les arguments communs attendus par les vues de mini-jeux.
   *
   * @param graphics contexte graphique à utiliser
   * @param game     mini-jeu représenté par la vue
   * @param area     rectangle de rendu
   * @throws NullPointerException si l'un des arguments est {@code null}
   */
  static void checkInput(Graphics2D graphics, Game game, Rectangle area) {
    Objects.requireNonNull(graphics);
    Objects.requireNonNull(game);
    Objects.requireNonNull(area);
  }
}