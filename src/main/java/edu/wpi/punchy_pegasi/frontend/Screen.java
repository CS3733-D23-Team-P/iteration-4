package edu.wpi.punchy_pegasi.frontend;

import edu.wpi.punchy_pegasi.App;
import edu.wpi.punchy_pegasi.frontend.controllers.requests.*;
import edu.wpi.punchy_pegasi.schema.Account;
import javafx.scene.Parent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.function.Function;

@Slf4j
public enum Screen {
    HOME("Home", "frontend/views/HomePage.fxml", Account.AccountType.STAFF),
    SIGNAGE("Signage", "frontend/views/Signage.fxml", Account.AccountType.NONE, true, true, false),
    MAP_PAGE("Map", "frontend/views/PathfindingMap.fxml", Account.AccountType.NONE, true, true, false),
    EDIT_MAP_PAGE("Edit Map", "frontend/views/AdminMap.fxml", Account.AccountType.ADMIN),
    LOGIN("Login", "frontend/views/Login.fxml", Account.AccountType.NONE, false, false, true),
    ADMIN_PAGE("Admin Page", "frontend/requests/admin/AdminPage.fxml", Account.AccountType.ADMIN),
    ADMIN_TABLE_PAGE("Admin Table Page", "frontend/requests/admin/AdminTablePage.fxml", Account.AccountType.ADMIN),
    ADMIN_ALERT_PAGE("Admin Alert Page", "frontend/requests/admin/AlertPage.fxml", Account.AccountType.ADMIN),
    FLOWER_DELIVERY_REQUEST("Request Flower Delivery", "frontend/requests/FlowerDeliveryRequest.fxml", Account.AccountType.STAFF, FlowerDeliveryRequestController::create),
    OFFICE_SERVICE_REQUEST("Request Office Supplies", "frontend/requests/OfficeServiceRequest.fxml", Account.AccountType.STAFF, OfficeServiceRequestController::create),
    FOOD_SERVICE_REQUEST("Request Food Delivery", "frontend/requests/FoodServiceRequest.fxml", Account.AccountType.STAFF, FoodServiceRequestController::create),
    CONFERENCE_ROOM_SERVICE_REQUEST("Request Conference Room", "frontend/requests/ConferenceRoomRequest.fxml", Account.AccountType.STAFF, ConferenceRoomController::create),
    FURNITURE_DELIVERY_SERVICE_REQUEST("Request Furniture Delivery", "frontend/requests/FurnitureDeliveryRequest.fxml", Account.AccountType.STAFF, FurnitureRequestController::create),
    SERVICE_REQUEST("Service Request", "frontend/requests/ServiceRequest.fxml", Account.AccountType.STAFF),
    ABOUT("About", "frontend/views/About.fxml", Account.AccountType.NONE),
    INFO("Info", "frontend/views/Info.fxml", Account.AccountType.NONE),
    CREDITS("Credits", "frontend/views/Credits.fxml", Account.AccountType.NONE);
    private final Function<String, ? extends Parent> createFunction;
    private final String path;
    private final String readable;
    @Getter
    private final boolean header;
    @Getter
    private final boolean sidebar;
    @Getter
    private final boolean timeout;
    @Getter
    private final Account.AccountType shield;

    Screen(String readable, String path, Account.AccountType shield, Function<String, ? extends Parent> createFunction, boolean header, boolean sidebar, boolean timeout) {
        this.path = path;
        this.readable = readable;
        this.createFunction = createFunction;
        this.shield = shield;
        this.header = header;
        this.sidebar = sidebar;
        this.timeout = timeout;
    }
    Screen(String readable, String path, Account.AccountType shield, Function<String, ? extends Parent> createFunction) {
        this(readable, path, shield, createFunction, true, true, true);
    }

    Screen(String readable, String path, Account.AccountType shield, boolean header, boolean sidebar, boolean timeout) {
        this(readable, path, shield, Screen::defaultCreate, header, sidebar, timeout);
    }
    Screen(String readable, String path, Account.AccountType shield) {
        this(readable, path, shield, true, true, true);
    }

    private static Parent defaultCreate(String path) {
        try {
            return App.getSingleton().loadWithCache(path).getRoot();
        } catch (IOException e) {
            log.error("Error in screen", e);
            throw new RuntimeException(e);
        }
    }

    public Parent get() {
        return createFunction.apply(path);
    }

    public String getReadable() {
        return readable;
    }
}