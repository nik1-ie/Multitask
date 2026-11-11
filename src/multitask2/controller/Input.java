package multitask2.controller;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.github.forax.zen.KeyboardEvent.Key;

/**
 * Mémorise l'état des touches du clavier entre les images.
 *
 * <p>
 * Une touche peut être maintenue enfoncée plusieurs images
 * ({@link #isKeyDown(Key)}) ou détectée uniquement pendant l'image où sa
 * pression a commencé ({@link #isKeyJustPressed(Key)}). L'appel à
 * {@link #nextFrame()} efface les pressions ponctuelles sans oublier les
 * touches encore maintenues.
 * </p>
 */
public final class Input {

  /** Ensemble des touches actuellement considérées comme enfoncées. */
  private final Set<Key> keysDown;
  /**
   * Ensemble des touches dont l'appui a commencé depuis le dernier
   * {@link #nextFrame()}.
   */
  private final Set<Key> keysPressed;

  /** Crée un état d'entrée sans aucune touche enfoncée. */
  public Input() {
    keysDown = new HashSet<Key>();
    keysPressed = new HashSet<Key>();
    super();
  }

  /**
   * Signale l'appui d'une touche.
   *
   * <p>
   * La touche est ajoutée à {@code keysPressed} uniquement si elle ne figurait
   * pas déjà parmi les touches enfoncées : les événements de répétition ne créent
   * donc pas de nouvel appui ponctuel.
   * </p>
   *
   * @param key touche concernée
   * @throws NullPointerException si {@code key} est {@code null}
   */
  public void keyDown(Key key) {
    Objects.requireNonNull(key);
    if (keysDown.add(key)) {
      keysPressed.add(key);
    }
  }

  /**
   * Signale le relâchement d'une touche. Ne fait rien si la touche n'était pas
   * enregistrée comme enfoncée.
   *
   * @param key touche concernée
   * @throws NullPointerException si {@code key} est {@code null}
   */
  public void keyUp(Key key) {
    Objects.requireNonNull(key);
    keysDown.remove(key);
  }

  /**
   * Indique si une touche est actuellement maintenue enfoncée.
   *
   * @param key touche à tester
   * @return {@code true} si la touche appartient à l'ensemble des touches
   *         enfoncées, {@code false} sinon
   * @throws NullPointerException si {@code key} est {@code null}
   */
  public boolean isKeyDown(Key key) {
    Objects.requireNonNull(key);
    return keysDown.contains(key);
  }

  /**
   * Indique si l'appui d'une touche a commencé depuis la dernière avancée
   * d'image.
   *
   * @param key touche à tester
   * @return {@code true} si la touche a été nouvellement pressée pendant l'image
   *         courante, {@code false} sinon
   * @throws NullPointerException si {@code key} est {@code null}
   */
  public boolean isKeyJustPressed(Key key) {
    Objects.requireNonNull(key);
    return keysPressed.contains(key);
  }

  /**
   * Termine l'image courante en effaçant les appuis ponctuels. Les touches
   * toujours maintenues restent enregistrées dans {@code keysDown}.
   */
  public void nextFrame() {
    keysPressed.clear();
  }
}
