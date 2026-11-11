package multitask2.model;

/**
 * Emplacement du modèle associant un mini-jeu à son instant d'activation et à
 * son état actif.
 */
public final class Slot {

  /** Mini-jeu associé à cet emplacement. */
  private final Game game;
  /**
   * Instant auquel cet emplacement doit devenir actif, en secondes depuis le
   * début.
   */
  private final int activationTime;
  /** Indique si l'instant d'activation a été atteint. */
  private boolean active;

  /**
   * Crée un emplacement inactif pour le mini-jeu et l'échéance donnés.
   *
   * @param miniGame mini-jeu associé à l'emplacement
   * @param time     instant d'activation, en secondes
   */
  Slot(Game miniGame, int time) {
    game = miniGame;
    activationTime = time;
    active = false;
    super();
  }

  /**
   * Retourne le mini-jeu associé à cet emplacement.
   *
   * @return mini-jeu associé
   */
  public Game game() {
    return game;
  }

  /**
   * Retourne l'instant prévu d'activation de cet emplacement.
   *
   * @return instant d'activation en secondes depuis le début de la partie
   */
  public int activationTime() {
    return activationTime;
  }

  /**
   * Marque l'emplacement comme actif. Cette opération ne réinitialise pas le
   * mini-jeu.
   */
  void start() {
    active = true;
  }

  /**
   * Indique si le mini-jeu a été activé.
   *
   * @return {@code true} si l'emplacement a été activé, {@code false} sinon
   */
  public boolean active() {
    return active;
  }
}