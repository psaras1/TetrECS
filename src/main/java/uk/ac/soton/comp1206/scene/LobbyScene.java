package uk.ac.soton.comp1206.scene;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GameWindow;

public class LobbyScene extends BaseScene {
  private static final Logger logger = LogManager.getLogger(InstructionsScene.class);
  private final Communicator communicator;
  public LobbyScene(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating Lobby Scene");
    communicator = gameWindow.getCommunicator();
  }

  @Override
  public void initialise() {

  }

  @Override
  public void build() {

  }
}
