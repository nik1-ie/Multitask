package multitask2.view;

import multitask2.model.Model;

/** Interface de la vue générale chargée de représenter visuellement le modèle de la partie. */
public interface View {

  /**
   * Dessine une image correspondant à l'état courant du modèle.
   *
   * @param model modèle dont l'état doit être affiché
   */
  void render(Model model);
}