package multitask2.controller;

import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.KeyboardEvent;
import com.github.forax.zen.KeyboardEvent.Action;

import multitask2.model.Model;
import multitask2.view.View;

/**
 * Contrôleur principal : collecte les événements clavier, met à jour le modèle
 * et demande le rendu de la vue à chaque itération de la boucle de jeu.
 */
public final class Controller {

  /** Durée cible d'une image, en secondes (environ 60 images par seconde). */
  private static final double TARGET_DT = 1.0 / 60;
  /**
   * Durée maximale d'un pas de simulation, en secondes, pour limiter les grands
   * sauts temporels.
   */
  private static final double MAX_DT = 0.05;

  /** Modèle contenant l'état des mini-jeux. */
  private final Model model;
  /** Vue chargée d'afficher l'état courant du modèle. */
  private final View view;
  /** État des touches maintenues et nouvellement pressées. */
  private final Input input;
  /** Contexte graphique utilisé pour récupérer les événements clavier. */
  private final ApplicationContext context;
  /** Indique si la boucle principale doit continuer. */
  private boolean running;

  /**
   * Crée un contrôleur associé au modèle, à la vue et au contexte graphique
   * fournis.
   *
   * @param model   modèle à faire évoluer
   * @param view    vue à redessiner à chaque image
   * @param context contexte dans lequel les événements clavier sont récupérés
   */
  public Controller(Model model, View view, ApplicationContext context) {
    this.model = model;
    this.view = view;
    this.context = context;
    input = new Input();
    running = false;
    super();
  }

  /**
   * Exécute la boucle principale jusqu'à ce que le modèle signale la fin de la
   * partie ou que l'attente entre deux images soit interrompue.
   *
   * <p>
   * À chaque itération, cette méthode calcule le temps écoulé depuis l'image
   * précédente, plafonne ce pas à {@link #MAX_DT}, traite les événements, met à
   * jour le modèle, avance l'état temporel de l'entrée, dessine l'image puis
   * attend si nécessaire pour approcher la fréquence cible définie par
   * {@link #TARGET_DT}.
   * </p>
   */
  public void run() {
    running = true;
    var lastTime = System.nanoTime();
    while (running) {
      var now = System.nanoTime();
      var dt = Math.min((now - lastTime) / 1_000_000_000.0, MAX_DT);
      lastTime = now;

      pollInput();
      running = model.update(dt, input);
      input.nextFrame();
      view.render(model);

      sleepUntilNextFrame(now);
    }
  }

  /**
   * Vide la file des événements disponibles et transmet les événements clavier à
   * leur traitement. Les événements d'autres types sont consommés sans action
   * particulière.
   */
  private void pollInput() {
    for (var b = true; b;) {
      b = switch (context.pollEvent()) {
      case null -> false;
      case KeyboardEvent ke -> {
        handleKey(ke);
        yield true;
      }
      default -> true;
      };
    }
  }

  /**
   * Convertit un événement clavier en mise à jour de l'état des touches.
   *
   * @param event événement clavier à interpréter
   */
  private void handleKey(KeyboardEvent event) {
    var gameKey = event.key();
    switch (event.action()) {
    case Action.KEY_PRESSED -> input.keyDown(gameKey);
    case Action.KEY_RELEASED -> input.keyUp(gameKey);
    }
  }

  /**
   * Attend la fin de l'intervalle cible d'une image si le traitement courant
   * s'est terminé assez tôt.
   *
   * @param lastTime instant de référence de l'image courante, mesuré par
   *                 {@link System#nanoTime()}
   */
  private void sleepUntilNextFrame(long lastTime) {
    var elapsedTime = (System.nanoTime() - lastTime) / 1_000_000;
    var sleepTime = (long) (TARGET_DT * 1000) - elapsedTime;
    if (sleepTime > 0) {
      try {
        Thread.sleep(sleepTime);
      } catch (InterruptedException e) {
        running = false;
      }
    }
  }
}
