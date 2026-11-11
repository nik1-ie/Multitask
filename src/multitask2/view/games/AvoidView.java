package multitask2.view.games;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Objects;
import java.util.function.IntUnaryOperator;

import multitask2.model.games.Avoid;

/**
 * Vue graphique du mini-jeu {@link Avoid} : dessine les rangées, le joueur et
 * les missiles.
 */
public final class AvoidView implements GameView {

  /** État du mini-jeu d'esquive lu pour produire le dessin. */
  private final Avoid avoider;

  /**
   * Crée une vue associée au mini-jeu fourni.
   *
   * @param avoider mini-jeu dont l'état doit être représenté
   * @throws NullPointerException si {@code avoider} est {@code null}
   */
  public AvoidView(Avoid avoider) {
    Objects.requireNonNull(avoider);
    this.avoider = avoider;
    super();
  }

  /**
   * Dessine le bord droit de l'aire de jeu, la position du joueur et tous les
   * missiles présents. Les coordonnées du modèle sont mises à l'échelle pour
   * tenir dans le rectangle fourni.
   *
   * @param graphics contexte graphique à utiliser
   * @param area     zone disponible pour le dessin
   * @throws NullPointerException si {@code graphics}, le jeu associé ou
   *                              {@code area} est nul
   */
  @Override
  public void render(Graphics2D graphics, Rectangle area) {
    GameView.checkInput(graphics, avoider, area);

    var scaleX = area.width / Avoid.WIDTH;
    var scaleY = area.height / (2 * Avoid.ROWS);
    IntUnaryOperator rowToY = row -> area.y + (int) ((2 * Avoid.ROWS - 2 * row - 1) * scaleY);

    graphics.setColor(new Color(120, 120, 140));
    graphics.fillRect(area.x + area.width - 8, area.y, 8, area.height);

    var platformCenterY = rowToY.applyAsInt(avoider.row());
    var platformHalf = (int) scaleY;
    var platformWidth = 14;
    graphics.setColor(new Color(90, 200, 220));
    graphics.fillRect(area.x + area.width - platformWidth, platformCenterY - platformHalf, platformWidth,
        platformHalf * 2);

    graphics.setColor(new Color(230, 90, 90));
    for (Avoid.Missile m : avoider.missiles()) {
      int mx = area.x + (int) (m.x() * scaleX);
      int my = rowToY.applyAsInt(m.row());
      graphics.fillOval(mx - 5, my - 3, 10, 6);
    }
  }
}