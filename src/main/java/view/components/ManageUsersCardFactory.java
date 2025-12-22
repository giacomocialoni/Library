package view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import bean.UserBean;

public class ManageUsersCardFactory {

    public HBox createUserCard(
        UserBean user,
        String lastPurchaseInfo,
        String lastLoanInfo,
        String statsInfo,
        Runnable onRemoveUser
    ) {

        VBox infoBox = createUserInfo(user, lastPurchaseInfo, lastLoanInfo, statsInfo);
        VBox controlsBox = createUserControls(onRemoveUser);

        HBox card = new HBox(20);
        card.getStyleClass().add("user-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15));
        card.getChildren().addAll(infoBox, controlsBox);

        return card;
    }

    private VBox createUserInfo(
        UserBean user,
        String lastPurchaseInfo,
        String lastLoanInfo,
        String statsInfo
    ) {
        VBox infoBox = new VBox(8);
        infoBox.setPadding(new Insets(10));
        infoBox.setAlignment(Pos.TOP_LEFT);
        infoBox.setPrefWidth(500);

        Label nameLabel = new Label(user.getFirstName() + " " + user.getLastName());
        Label emailLabel = new Label("Email: " + user.getEmail());

        Separator separator1 = new Separator();

        Label lastPurchaseLabel = new Label(lastPurchaseInfo);
        Label lastLoanLabel = new Label(lastLoanInfo);

        Separator separator2 = new Separator();

        Label statsLabel = new Label(statsInfo);

        infoBox.getChildren().addAll(
            nameLabel, emailLabel,
            separator1,
            lastPurchaseLabel, lastLoanLabel,
            separator2,
            statsLabel
        );

        return infoBox;
    }

    private VBox createUserControls(Runnable onRemoveUser) {
        VBox controlsBox = new VBox(15);
        controlsBox.setAlignment(Pos.CENTER);
        controlsBox.setPadding(new Insets(10));

        Button removeButton = new Button("Elimina Utente");
        removeButton.getStyleClass().add("manage-remove-button");

        removeButton.setOnAction(e -> onRemoveUser.run());

        controlsBox.getChildren().add(removeButton);
        return controlsBox;
    }
}