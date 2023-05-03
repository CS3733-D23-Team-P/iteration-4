package edu.wpi.punchy_pegasi;

import edu.wpi.punchy_pegasi.backend.PdbController;
import edu.wpi.punchy_pegasi.frontend.Screen;
import edu.wpi.punchy_pegasi.frontend.components.PageLoading;
import edu.wpi.punchy_pegasi.frontend.controllers.ErrorController;
import edu.wpi.punchy_pegasi.frontend.controllers.LayoutController;
import edu.wpi.punchy_pegasi.frontend.controllers.SplashController;
import edu.wpi.punchy_pegasi.generated.Facade;
import edu.wpi.punchy_pegasi.schema.Account;
import edu.wpi.punchy_pegasi.schema.TableType;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class App extends Application {
    @Getter
    private static App singleton;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    @Getter
    private final Stage popupStage = new Stage();
    private final List<LoadedFXML> loadedFXML = new ArrayList<>();
    private final Debouncer<String> fxmlDebouncer = new Debouncer<>(s -> {
        loadedFXML.stream().filter(l -> l.path.toExternalForm().endsWith(s)).forEach(fxml -> Platform.runLater(() -> {
            try {
                if (fxml.checkFXML())
                    fxml.load();
                else System.out.println("Failed to hot-load " + s + ", invalid XML");
            } catch (IOException ignored) {
            }
        }));
        System.out.println("Hot-loaded FXML: " + s);
    }, 250);
    @Getter
    ExecutorService executorService = Executors.newFixedThreadPool(8);
    @Getter
    private PdbController pdb;
    @Getter
    private Facade facade;
    @Getter
    private Stage primaryStage;
    @Getter
    private Screen currentScreen;
    @Getter
    private Scene scene;
    @Getter
    private Account account = new Account(0L, "", "", 0L, Account.AccountType.NONE, Account.Theme.LIGHT);
    @Getter
    private LayoutController layout;
    @Getter
    private boolean development = false;


    private final Debouncer<String> cssDebouncer = new Debouncer<>(s -> {
        Platform.runLater(this::loadTheme);
        System.out.println("Hot-loaded CSS: " + s);
    }, 250);
    private IdleScreen idleScreen;
    @Getter
    private BorderPane viewPane;
    private Thread loadingThread = null;

    private static void showError(Thread t, Throwable e) {
        log.error("An unexpected error occurred in " + t, e);
        if (Platform.isFxApplicationThread()) showErrorDialog(e);
    }

    private static void showErrorDialog(Throwable e) {
        var errorMsg = new StringWriter();
        e.printStackTrace(new PrintWriter(errorMsg));
        var dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        var loader = new FXMLLoader(App.class.getResource("frontend/components/Error.fxml"));
        try {
            Parent root = loader.load();
            ((ErrorController) loader.getController()).setErrorText(errorMsg.toString());
            dialog.setScene(new Scene(root, 600, 400));
            dialog.show();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    public void loadTheme() {
        var colorsPath = switch (getAccount().getTheme()) {
            case LIGHT -> "frontend/css/PFXColors.css";
            case DARK -> "frontend/css/PFXColorsDark.css";
        };
        scene.getStylesheets().clear();
        loadStylesheet(colorsPath);
        loadStylesheet("frontend/css/DefaultTheme.css");
    }

    public void setAccount(Account account) {
        if (account == null) {
            navigate(Screen.LOGIN);
            account = new Account(0L, "", "", 0L, Account.AccountType.NONE, Account.Theme.LIGHT);
        }
        var changeTheme = this.account.getTheme() != account.getTheme();
        support.firePropertyChange("account", this.account, account);
        this.account = account;
        if(changeTheme) loadTheme();
    }

    public void exit() {
        Platform.exit();
    }

    public void setCurrentScreen(Screen value) {
        support.firePropertyChange("page", currentScreen, value);
        currentScreen = value;
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    @Override
    public void init() {
        singleton = this;
        idleScreen = new IdleScreen(90);
        log.info("Starting Up");
    }

    @Override
    public void stop() {
        try {
            pdb.exposeConnection().close();
            log.info("Shut Down Connection");
        } catch (SQLException e) {
            log.error("Failed to close database connection", e);
        }
        executorService.shutdownNow();
        log.info("Application Shutting Down");
    }

    public synchronized void navigate(final Screen screen) {
        if (screen == null) return;
        if (loadingThread != null) loadingThread.interrupt();
        if (account.getAccountType().getShieldLevel() >= screen.getShield().getShieldLevel()) {
            getLayout().showTopLayout(screen.isHeader());
            getLayout().showLeftLayout(screen.isSidebar());
            enableTimeout(screen.isTimeout());
            getViewPane().setCenter(new PageLoading());
            if(loadingThread !=null)
                loadingThread.interrupt();
            getExecutorService().execute(() -> {
                loadingThread = Thread.currentThread();
                var loaded = screen.get();
                if (!Thread.interrupted())
                    Platform.runLater(() -> {
                        setCurrentScreen(screen);
                        getViewPane().setCenter(loaded);
                    });
            });
        }
    }

    public Optional<URL> resolveResource(String resourcePath) {
        if (development) {
            try {
                var resource = Paths.get(System.getProperty("user.dir"), "src/main/resources/edu/wpi/punchy_pegasi", resourcePath);
                if (Files.exists(resource)) return Optional.of(resource.toUri().toURL());
            } catch (MalformedURLException e) {
                return Optional.empty();
            }
        } else {
            var resource = App.class.getResource(resourcePath);
            if (resource != null) return Optional.of(resource);
        }
        return Optional.empty();
    }

    public void loadStylesheet(String resourcePath) {
        var resource = resolveResource(resourcePath);
        resource.ifPresent(url -> scene.getStylesheets().add(url.toExternalForm()));
    }

    private void registerRecursive(WatchService watchService, final Path root) throws IOException {
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void loadUI(PdbController pdb) {
        if (pdb == null) {
            log.error("No database connection");
            return;
        }
        log.info("Application started with database {}", pdb.source);
        this.pdb = pdb;
        facade = new Facade(pdb);
        layout = new LayoutController();
        viewPane = layout.getViewPane();
        scene = new Scene(layout, 1280, 720);
        loadTheme();
        primaryStage.setScene(scene);
        primaryStage.show();
        navigate(Screen.LOGIN);
        App.getSingleton().getExecutorService().execute(this::initDatabaseTables);

        // Idle Screen

        var idleTimeline = new Timeline(new KeyFrame(Duration.seconds(idleScreen.getIdleTimeSeconds()), e -> {
            if (!idleScreen.isIdle() && idleScreen.isEnabled()) {
                idleScreen.setIdle(true);
                getLayout().showOverlay(idleScreen, false);
            }
        }));
        idleTimeline.setCycleCount(Timeline.INDEFINITE);
        idleTimeline.play();

        // Add event handling to show/hide the screensaver on user activity
        scene.addEventHandler(MouseEvent.MOUSE_MOVED, e -> disableScreenSaver(idleTimeline));
        scene.addEventHandler(KeyEvent.ANY, e -> disableScreenSaver(idleTimeline));
        scene.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> disableScreenSaver(idleTimeline));
    }

    public void enableTimeout(boolean enable) {
        idleScreen.setEnabled(enable);
    }

    private void initDatabaseTables() {
        for (var tt : TableType.values()) {
            try {
                pdb.initTableByType(tt);
            } catch (PdbController.DatabaseException e) {
                log.error("Could not init table " + tt.name());
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Thread.setDefaultUncaughtExceptionHandler(App::showError);
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Brigham and Women's Hospital");
        var genericResource = this.getClass().getResource("");
        if (genericResource != null && Objects.equals(genericResource.getProtocol(), "file")) development = true;

        final var loader = loadWithCache("frontend/views/Splash.fxml");
        final SplashController splashController = loader.getController();
        scene = new Scene(loader.getRoot(), 600, 400);
        splashController.setOnConnection(pdb -> Platform.runLater(() -> loadUI(pdb)));
        splashController.getConnection();

        //var testComponent = new PFXCard();
        //scene = new Scene(new BorderPane(new VBox(new HBox(testComponent))), 600, 400);

        loadTheme();
        primaryStage.getIcons().add(new Image(resolveResource("frontend/assets/bwhlogo.png").get().toString()));

        if (development) {
            App.getSingleton().getExecutorService().execute(() -> {
                try {
                    WatchService watchService = FileSystems.getDefault().newWatchService();
                    Path path = Paths.get(System.getProperty("user.dir"), "src/main/resources/edu/wpi/punchy_pegasi/frontend");

                    registerRecursive(watchService, path);

                    WatchKey key;
                    while ((key = watchService.take()) != null) {
                        for (WatchEvent<?> event : key.pollEvents()) {
                            var context = event.context().toString().replace("~", "");
                            if (context.contains(".css")) cssDebouncer.call(context);
                            if (context.contains(".fxml")) fxmlDebouncer.call(context);
                        }
                        key.reset();
                    }
                } catch (Exception e) {
                    log.error("Error with hotloading css", e);
                }
            });
        }
        this.primaryStage.setScene(scene);
        this.primaryStage.show();
    }

    public void disableScreenSaver(Timeline idleTimeline) {
        if (idleScreen.isIdle()) {
            getLayout().hideOverlay();
            idleScreen.setIdle(false);
        }
        idleTimeline.playFromStart(); // Reset the idle timeline
    }

    public FXMLLoader loadWithCache(String path) throws IOException {
        return loadWithCache(path, null, null);
    }

    public FXMLLoader loadWithCache(String path, Object controller) throws IOException {
        return loadWithCache(path, null, controller);
    }

    public FXMLLoader loadWithCache(String path, Node root, Object controller) throws IOException {
        var resource = resolveResource(path);
        if (resource.isEmpty()) {
            log.error("Could not find file {}", path);
            throw new IOException("No such file");
        }
        return new LoadedFXML(resource.get(), root, controller).load();
    }

    private static class LoadedFXML {
        private final URL path;
        @Getter
        private final FXMLLoader fxmlLoader;

        public LoadedFXML(URL path, Node root, Object controller) {
            fxmlLoader = new FXMLLoader();
            fxmlLoader.setRoot(root);
            fxmlLoader.setController(controller);
            fxmlLoader.setLocation(path);
            this.path = path;
        }

        public boolean checkFXML() {
            try {
                File fXmlFile = new File(path.toURI());
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(fXmlFile);
                return true;
            } catch (ParserConfigurationException | IOException | SAXException | URISyntaxException e) {
                return false;
            }
        }

        public FXMLLoader load() throws IOException {
            fxmlLoader.load();
            return fxmlLoader;
        }
    }
}
