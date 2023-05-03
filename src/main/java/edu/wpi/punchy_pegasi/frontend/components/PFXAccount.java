package edu.wpi.punchy_pegasi.frontend.components;

import edu.wpi.punchy_pegasi.App;
import edu.wpi.punchy_pegasi.frontend.Screen;
import edu.wpi.punchy_pegasi.frontend.icons.MaterialSymbols;
import edu.wpi.punchy_pegasi.frontend.icons.PFXIcon;
import edu.wpi.punchy_pegasi.schema.Account;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;

public class PFXAccount extends HBox implements PropertyChangeListener {
    private final EventHandler<? super MouseEvent> noAccount = e -> App.getSingleton().navigate(Screen.LOGIN);
    private final Label label = new Label("Login");
    private final PFXIcon loginIcon = new PFXIcon(MaterialSymbols.LOGIN);
    private final PFXSidebarItem logout = new PFXSidebarItem();
    private final PopOver accountMenu = new PopOver();
    private final EventHandler<? super MouseEvent> isAccount = e -> accountMenu.show(this);
    private final PFXIcon defaultIcon = new PFXIcon(MaterialSymbols.ACCOUNT_CIRCLE);

    private final VBox accountInformation = new VBox();
    private final Label nameLabel = new Label();
    private final Label accountLevel = new Label();
    private final MFXToggleButton colorToggle = new MFXToggleButton();


    public PFXAccount() {
        super();
        var content = new VBox();
        content.getStyleClass().add("pfx-account-menu");
        accountMenu.setContentNode(content);
        accountMenu.setDetachable(false);
        accountMenu.setArrowLocation(PopOver.ArrowLocation.TOP_RIGHT);
        logout.setText("Logout");
        logout.setIcon(MaterialSymbols.LOGOUT);
        logout.setOnMouseClicked(e -> {
            App.getSingleton().setAccount(null);
            accountMenu.hide();
        });
        defaultIcon.getStyleClass().add("pfx-default-account-icon");
        getStyleClass().add("pfx-account");
        setAccount(App.getSingleton().getAccount());
        App.getSingleton().addPropertyChangeListener(this);

        colorToggle.setOnAction(e->{
            App.getSingleton().loadTheme();
            App.getSingleton().setAccount(App.getSingleton().getAccount().withTheme(colorToggle.isSelected() ? Account.Theme.DARK : Account.Theme.LIGHT));
            App.getSingleton().getFacade().updateAccount(App.getSingleton().getAccount(), new Account.Field[]{Account.Field.THEME});
        });

        accountInformation.getChildren().add(nameLabel);
        accountInformation.getChildren().add(accountLevel);
        accountInformation.getStyleClass().add("pfx-account-information");

        nameLabel.setStyle("-fx-font-size: 14; -fx-text-fill: -pfx-text;");
        accountLevel.setStyle("-fx-font-size: 12; -fx-text-fill: -pfx-text;");
        colorToggle.setText("Dark Mode");
        colorToggle.setStyle("-fx-font-size: 12; -fx-text-fill: -pfx-text;");
//        ttsToggle.setText("Text to Speech");
//        ttsToggle.setStyle("-fx-font-size: 12");

        content.setAlignment(Pos.CENTER_LEFT);
        content.getChildren().add(accountInformation);
        content.getChildren().add(colorToggle);
//        content.getChildren().add(ttsToggle);
        content.getChildren().add(logout);
    }

    public void setAccount(Account account) {
        if (account.getAccountType() == Account.AccountType.NONE) {
            removeEventFilter(MouseEvent.MOUSE_CLICKED, isAccount);
            addEventFilter(MouseEvent.MOUSE_CLICKED, noAccount);
            getChildren().clear();
            getChildren().addAll(loginIcon, label);
            accountInformation.setVisible(false);
            accountInformation.setManaged(false);
        } else {
            removeEventFilter(MouseEvent.MOUSE_CLICKED, noAccount);
            addEventFilter(MouseEvent.MOUSE_CLICKED, isAccount);
            getChildren().clear();
            getChildren().addAll(defaultIcon);
            accountInformation.setVisible(true);
            accountInformation.setManaged(true);
        }
        nameLabel.setText(account.getUsername());
        accountLevel.setText(account.getAccountType().toString());
        colorToggle.setSelected(switch (account.getTheme()) {
            case LIGHT -> false;
            case DARK -> true;
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (Objects.equals(evt.getPropertyName(), "account")) {
            setAccount((Account) evt.getNewValue());
        }
    }


}
