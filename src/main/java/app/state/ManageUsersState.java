package app.state;

import app.StageManager;
import controller.gui.ManageUsersControllerGUI;
import exception.DAOException;
import exception.RecordNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManageUsersState extends PrimaryState {

    private static final Logger logger = LoggerFactory.getLogger(ManageUsersState.class);

    public ManageUsersState(StateManager stateManager) {
        super(stateManager);
    }

    @Override
    protected void loadContent() {
        ManageUsersControllerGUI manageUsersController = 
            stateManager.getStageManager().<ManageUsersControllerGUI>loadContent(StageManager.MANAGE_USERS_VIEW);

        if (manageUsersController != null) {
            try {
                manageUsersController.setStateManager(stateManager);
                manageUsersController.loadUsers();
            } catch (RecordNotFoundException e) {
                logger.info("Nessun utente trovato durante l'inizializzazione del controller GUI");
            } catch (DAOException e) {
                logger.error("Errore DAO durante l'inizializzazione del controller ManageUsersControllerGUI", e);
            }
        }
    }
}