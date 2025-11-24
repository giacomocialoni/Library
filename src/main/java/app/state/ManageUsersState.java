package app.state;

import app.StageManager;
import controller.gui.MainControllerGUI;
import controller.gui.ManageUsersControllerGUI;
import exception.DAOException;
import exception.RecordNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManageUsersState implements AppState {

    private static final Logger logger = LoggerFactory.getLogger(ManageUsersState.class);

    private final StateManager stateManager;

    public ManageUsersState(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    @Override
    public void onEnter() {
        ManageUsersControllerGUI manageUsersController =
                stateManager.getStageManager().<ManageUsersControllerGUI>loadContent(StageManager.MANAGE_USERS_VIEW);

        if (manageUsersController != null) {
            // Gestione delle eccezioni senza stack trace sul terminale
            try {
                manageUsersController.setStateManager(stateManager);
            } catch (RecordNotFoundException e) {
                logger.info("Nessun utente trovato durante l'inizializzazione del controller GUI");
            } catch (DAOException e) {
                logger.error("Errore DAO durante l'inizializzazione del controller ManageUsersControllerGUI", e);
            }

            try {
                manageUsersController.loadUsers();
            } catch (RecordNotFoundException e) {
                logger.info("Nessun utente da caricare in ManageUsersControllerGUI");
            } catch (DAOException e) {
                logger.error("Errore DAO durante il caricamento utenti in ManageUsersControllerGUI", e);
            }
        }

        // aggiorna bottone attivo nella main view attiva
        MainControllerGUI controllerMain = stateManager.getStageManager().getActiveMainController();
        if (controllerMain != null) {
            controllerMain.updateActiveButtonByState();
        }
    }

    @Override
    public void onExit() {
        // Cleanup se necessario
    }

    @Override
    public void goBack() {
        stateManager.goBack();
    }
}