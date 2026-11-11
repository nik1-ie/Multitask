package multitask2.view;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import multitask2.model.Game;
import multitask2.view.games.GameView;

/**
 * Registre reliant chaque instance de mini-jeu à la vue spécialisée qui sait
 * l'afficher.
 */
public final class Viewers {

  /** Association entre les instances de jeu et leurs vues graphiques. */
  private final Map<Game, GameView> viewer;

  /** Crée un registre vide de vues de mini-jeux. */
  public Viewers() {
    viewer = new HashMap<>();
    super();
  }

  /**
   * Enregistre la vue associée à un mini-jeu. Une association existante pour la
   * même clé est remplacée.
   *
   * @param g    instance de mini-jeu à associer à une vue
   * @param view vue spécialisée qui affiche cette instance
   * @throws NullPointerException si {@code g} ou {@code view} est {@code null}
   */
  public void addGame(Game g, GameView view) {
    Objects.requireNonNull(g);
    Objects.requireNonNull(view);
    viewer.put(g, view);
  }

  /**
   * Recherche la vue associée à une instance de mini-jeu.
   *
   * @param g instance de mini-jeu dont la vue est recherchée
   * @return vue enregistrée pour cette instance
   * @throws NullPointerException  si {@code g} est {@code null}
   * @throws IllegalStateException si aucune vue n'a été enregistrée pour cette
   *                               instance
   */
  public GameView view(Game g) {
    Objects.requireNonNull(g);
    var view = viewer.get(g);
    if (view == null) {
      throw new IllegalStateException("No viewer stored for this game");
    }
    return view;
  }
}