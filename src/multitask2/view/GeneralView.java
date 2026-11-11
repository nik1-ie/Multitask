package multitask2.view;

import com.github.forax.zen.ApplicationContext;

import multitask2.model.Model;
import multitask2.model.Slot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Objects;

/**
 * Vue générale de la partie : dessine l'arrière-plan, le temps écoulé et une
 * grille contenant les vues de chaque mini-jeu.
 */
public final class GeneralView implements View {

  /** Couleur de fond de la fenêtre. */
  public static final Color BACKGROUND = new Color(24, 26, 32);
  /** Hauteur réservée à la zone supérieure de l'interface, en pixels. */
  private static final int HUD_HEIGHT = 50;
  /** Espacement utilisé entre les zones dessinées, en pixels. */
  private static final int PADDING = 10;
  /** Marge autour de la grille et de la zone d'informations, en pixels. */
  private static final int MARGIN = 70;

  /**
   * Contexte graphique qui fournit la taille d'écran et réalise le rendu des
   * images.
   */
  private final ApplicationContext context;
  /** Registre des vues spécialisées des mini-jeux. */
  private final Viewers viewers;
  /** Largeur de l'écran relevée à la construction, en pixels. */
  private final int width;
  /** Hauteur de l'écran relevée à la construction, en pixels. */
  private final int height;

  /**
   * Crée la vue et mémorise les dimensions de l'écran courant.
   *
   * @param context contexte graphique utilisé pour le rendu
   * @param viewers registre permettant de retrouver la vue de chaque mini-jeu
   * @throws NullPointerException si {@code context} ou {@code viewers} est
   *                              {@code null}
   */
  public GeneralView(ApplicationContext context, Viewers viewers) {
    this.context = Objects.requireNonNull(context);
    this.viewers = Objects.requireNonNull(viewers);
    var screen = context.getScreenInfo();
    width = screen.width();
    height = screen.height();
    super();
  }

  /**
   * Demande au contexte graphique de dessiner une image représentant l'état
   * actuel du modèle.
   *
   * @param model modèle contenant le temps de partie et les mini-jeux à afficher
   * @throws NullPointerException si {@code model} est {@code null}
   */
  public void render(Model model) {
    Objects.requireNonNull(model);
    context.renderFrame(graphics -> draw(graphics, model));
  }

  /**
   * Dessine le fond, l'affichage du temps puis la grille des mini-jeux. Les
   * cellules sont réparties sur un nombre de colonnes et de lignes visant une
   * grille approximativement carrée.
   *
   * @param graphics surface graphique de l'image en cours
   * @param model    modèle contenant l'état à dessiner
   */
  private void draw(Graphics2D graphics, Model model) {
    graphics.setColor(BACKGROUND);
    graphics.fillRect(0, 0, width, height);
    drawScore(graphics, model);

    var slots = model.slots();
    var n = slots.size();
    if (n > 0) {
      var cols = (int) Math.ceil(Math.sqrt(n));
      var rows = (int) Math.ceil((double) n / cols);
      var areaWidth = width - 2 * MARGIN - PADDING;
      var areaHeight = height - HUD_HEIGHT - 2 * MARGIN - PADDING;
      var cellWidth = areaWidth / cols;
      var cellHeight = areaHeight / rows;

      for (var i = 0; i < n; i++) {
        var col = i % cols;
        var row = i / cols;
        var x = PADDING + MARGIN + col * cellWidth;
        var y = HUD_HEIGHT + MARGIN + row * cellHeight;
        var bounds = new Rectangle(x, y, cellWidth - PADDING, cellHeight - PADDING);
        drawSlot(graphics, slots.get(i), model, bounds);
      }
    }
  }

  /**
   * Dessine le temps entier écoulé depuis le début de la partie dans la zone
   * supérieure.
   *
   * @param graphics surface graphique de l'image en cours
   * @param model    modèle fournissant le temps écoulé
   */
  private void drawScore(Graphics2D graphics, Model model) {
    graphics.setColor(Color.WHITE);
    graphics.setFont(graphics.getFont().deriveFont(Font.BOLD, 18f));
    String text = String.format("Temps : " + model.time() + " s");
    graphics.drawString(text, PADDING + MARGIN, 30 + MARGIN);
  }

  /**
   * Dessine la cellule d'un mini-jeu. Un jeu inactif affiche un compte à rebours
   * ; un jeu actif affiche un contour puis délègue son contenu à la vue
   * spécialisée correspondante.
   *
   * @param graphics surface graphique de l'image en cours
   * @param slot     emplacement contenant le mini-jeu et son état d'activation
   * @param model    modèle utilisé pour calculer le compte à rebours
   * @param bounds   rectangle de la cellule à dessiner
   */
  private void drawSlot(Graphics2D graphics, Slot slot, Model model, Rectangle bounds) {
    graphics.setColor(new Color(40, 42, 54));
    graphics.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 12, 12);

    if (!slot.active()) {
      var remaining = Math.max(0, slot.activationTime() - model.time());
      graphics.setColor(Color.GRAY);
      graphics.setFont(graphics.getFont().deriveFont(Font.PLAIN, 14f));
      centerText(graphics, "Dans " + remaining + " s...", bounds);
      return;
    }

    graphics.setColor(new Color(90, 200, 120));
    graphics.setStroke(new BasicStroke(3));
    graphics.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 12, 12);

    var inner = new Rectangle(bounds.x + 10, bounds.y + 26, bounds.width - 20, bounds.height - 52);
    viewers.view(slot.game()).render(graphics, inner);
  }

  /**
   * Dessine un texte horizontalement centré dans un rectangle, à mi-hauteur de
   * celui-ci.
   *
   * @param graphics surface graphique utilisée pour le dessin
   * @param text     texte à afficher
   * @param bounds   rectangle dans lequel centrer le texte
   */
  private static void centerText(Graphics2D graphics, String text, Rectangle bounds) {
    var fm = graphics.getFontMetrics();
    var x = bounds.x + (bounds.width - fm.stringWidth(text)) / 2;
    var y = bounds.y + bounds.height / 2;
    graphics.drawString(text, x, y);
  }
}