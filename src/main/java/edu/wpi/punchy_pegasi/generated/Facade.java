package edu.wpi.punchy_pegasi.generated;

import edu.wpi.punchy_pegasi.schema.*;
import edu.wpi.punchy_pegasi.backend.PdbController;
import java.util.Map;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import io.github.palexdev.materialfx.controls.MFXTableView;
import java.util.function.Consumer;
import java.util.Optional;


import java.sql.SQLException;

public class Facade {
	private final PdbController dbController;
	private final NodeCachedDaoImpl nodeDao;
	private final EdgeCachedDaoImpl edgeDao;
	private final MoveCachedDaoImpl moveDao;
	private final LocationNameCachedDaoImpl locationNameDao;
	private final RequestEntryCachedDaoImpl requestEntryDao;
	private final GenericRequestEntryCachedDaoImpl genericRequestEntryDao;
	private final FoodServiceRequestEntryCachedDaoImpl foodServiceRequestEntryDao;
	private final FlowerDeliveryRequestEntryCachedDaoImpl flowerDeliveryRequestEntryDao;
	private final ConferenceRoomEntryCachedDaoImpl conferenceRoomEntryDao;
	private final FurnitureRequestEntryCachedDaoImpl furnitureRequestEntryDao;
	private final OfficeServiceRequestEntryCachedDaoImpl officeServiceRequestEntryDao;
	private final EmployeeCachedDaoImpl employeeDao;
	private final AccountCachedDaoImpl accountDao;
	private final SignageCachedDaoImpl signageDao;
	private final AlertCachedDaoImpl alertDao;
    public <K, T, C> IDao<K, T, C> getDaoByClass(Class<T> clazz) {
        if (clazz == null) return null;
        else if (clazz == edu.wpi.punchy_pegasi.generator.schema.Node.class) return (IDao<K, T, C>)nodeDao;
        else if (clazz == edu.wpi.punchy_pegasi.generator.schema.Edge.class) return (IDao<K, T, C>)edgeDao;
        else if (clazz == edu.wpi.punchy_pegasi.generator.schema.Move.class) return (IDao<K, T, C>)moveDao;
        else if (clazz == edu.wpi.punchy_pegasi.generator.schema.LocationName.class) return (IDao<K, T, C>)locationNameDao;
        else if (clazz == edu.wpi.punchy_pegasi.generator.schema.RequestEntry.class) return (IDao<K, T, C>)requestEntryDao;
        else if (clazz == edu.wpi.punchy_pegasi.generator.schema.GenericRequestEntry.class) return (IDao<K, T, C>)genericRequestEntryDao;
        else if (clazz == edu.wpi.punchy_pegasi.generator.schema.FoodServiceRequestEntry.class) return (IDao<K, T, C>)foodServiceRequestEntryDao;
        else if (clazz == edu.wpi.punchy_pegasi.generator.schema.FlowerDeliveryRequestEntry.class) return (IDao<K, T, C>)flowerDeliveryRequestEntryDao;
        else if (clazz == edu.wpi.punchy_pegasi.generator.schema.ConferenceRoomEntry.class) return (IDao<K, T, C>)conferenceRoomEntryDao;
        else if (clazz == edu.wpi.punchy_pegasi.generator.schema.FurnitureRequestEntry.class) return (IDao<K, T, C>)furnitureRequestEntryDao;
        else if (clazz == edu.wpi.punchy_pegasi.generator.schema.OfficeServiceRequestEntry.class) return (IDao<K, T, C>)officeServiceRequestEntryDao;
        else if (clazz == edu.wpi.punchy_pegasi.generator.schema.Employee.class) return (IDao<K, T, C>)employeeDao;
        else if (clazz == edu.wpi.punchy_pegasi.generator.schema.Account.class) return (IDao<K, T, C>)accountDao;
        else if (clazz == edu.wpi.punchy_pegasi.generator.schema.Signage.class) return (IDao<K, T, C>)signageDao;
        else if (clazz == edu.wpi.punchy_pegasi.generator.schema.Alert.class) return (IDao<K, T, C>)alertDao;
        else return null;
    }

    public Facade(PdbController dbController) {
		this.dbController = dbController;
		nodeDao = new NodeCachedDaoImpl(this.dbController);
		edgeDao = new EdgeCachedDaoImpl(this.dbController);
		moveDao = new MoveCachedDaoImpl(this.dbController);
		locationNameDao = new LocationNameCachedDaoImpl(this.dbController);
		requestEntryDao = new RequestEntryCachedDaoImpl(this.dbController);
		genericRequestEntryDao = new GenericRequestEntryCachedDaoImpl(this.dbController);
		foodServiceRequestEntryDao = new FoodServiceRequestEntryCachedDaoImpl(this.dbController);
		flowerDeliveryRequestEntryDao = new FlowerDeliveryRequestEntryCachedDaoImpl(this.dbController);
		conferenceRoomEntryDao = new ConferenceRoomEntryCachedDaoImpl(this.dbController);
		furnitureRequestEntryDao = new FurnitureRequestEntryCachedDaoImpl(this.dbController);
		officeServiceRequestEntryDao = new OfficeServiceRequestEntryCachedDaoImpl(this.dbController);
		employeeDao = new EmployeeCachedDaoImpl(this.dbController);
		accountDao = new AccountCachedDaoImpl(this.dbController);
		signageDao = new SignageCachedDaoImpl(this.dbController);
		alertDao = new AlertCachedDaoImpl(this.dbController);

    }

    public void switchDatabase(PdbController.Source source) throws SQLException, PdbController.DatabaseException{
		 this.dbController.switchSource(source);
		nodeDao.refresh();
		edgeDao.refresh();
		moveDao.refresh();
		locationNameDao.refresh();
		requestEntryDao.refresh();
		genericRequestEntryDao.refresh();
		foodServiceRequestEntryDao.refresh();
		flowerDeliveryRequestEntryDao.refresh();
		conferenceRoomEntryDao.refresh();
		furnitureRequestEntryDao.refresh();
		officeServiceRequestEntryDao.refresh();
		employeeDao.refresh();
		accountDao.refresh();
		signageDao.refresh();
		alertDao.refresh();

    }

	public MFXTableView<Node> generateTableNode(Consumer<Node> onRowClick, Node.Field[] hidden) {
		return nodeDao.generateTable(onRowClick, hidden);
	}
	public MFXTableView<Node> generateTableNode(Consumer<Node> onRowClick) {
		return nodeDao.generateTable(onRowClick);
	}
	public Optional<Node> getNode(java.lang.Long key) {
		return nodeDao.get(key);
	}
	public Map<java.lang.Long, Node> getNode(Node.Field column, Object value) {
		return nodeDao.get(column, value);
	}
	public Map<java.lang.Long, Node> getNode(Node.Field[] params, Object[] value) {
		return nodeDao.get(params, value);
	}
	public ObservableMap<java.lang.Long, Node> getAllNode() {
		return nodeDao.getAll();
	}
	public ObservableList<Node> getAllAsListNode() {
		return nodeDao.getAllAsList();
	}
	public void saveNode(Node node) {
		nodeDao.save(node);
	}
	public void updateNode(Node node, Node.Field[] params) {
		nodeDao.update(node, params);
	}
	public void deleteNode(Node node) {
		nodeDao.delete(node);
	}
	public MFXTableView<Edge> generateTableEdge(Consumer<Edge> onRowClick, Edge.Field[] hidden) {
		return edgeDao.generateTable(onRowClick, hidden);
	}
	public MFXTableView<Edge> generateTableEdge(Consumer<Edge> onRowClick) {
		return edgeDao.generateTable(onRowClick);
	}
	public Optional<Edge> getEdge(java.util.UUID key) {
		return edgeDao.get(key);
	}
	public Map<java.util.UUID, Edge> getEdge(Edge.Field column, Object value) {
		return edgeDao.get(column, value);
	}
	public Map<java.util.UUID, Edge> getEdge(Edge.Field[] params, Object[] value) {
		return edgeDao.get(params, value);
	}
	public ObservableMap<java.util.UUID, Edge> getAllEdge() {
		return edgeDao.getAll();
	}
	public ObservableList<Edge> getAllAsListEdge() {
		return edgeDao.getAllAsList();
	}
	public void saveEdge(Edge edge) {
		edgeDao.save(edge);
	}
	public void updateEdge(Edge edge, Edge.Field[] params) {
		edgeDao.update(edge, params);
	}
	public void deleteEdge(Edge edge) {
		edgeDao.delete(edge);
	}
	public MFXTableView<Move> generateTableMove(Consumer<Move> onRowClick, Move.Field[] hidden) {
		return moveDao.generateTable(onRowClick, hidden);
	}
	public MFXTableView<Move> generateTableMove(Consumer<Move> onRowClick) {
		return moveDao.generateTable(onRowClick);
	}
	public Optional<Move> getMove(java.lang.Long key) {
		return moveDao.get(key);
	}
	public Map<java.lang.Long, Move> getMove(Move.Field column, Object value) {
		return moveDao.get(column, value);
	}
	public Map<java.lang.Long, Move> getMove(Move.Field[] params, Object[] value) {
		return moveDao.get(params, value);
	}
	public ObservableMap<java.lang.Long, Move> getAllMove() {
		return moveDao.getAll();
	}
	public ObservableList<Move> getAllAsListMove() {
		return moveDao.getAllAsList();
	}
	public void saveMove(Move move) {
		moveDao.save(move);
	}
	public void updateMove(Move move, Move.Field[] params) {
		moveDao.update(move, params);
	}
	public void deleteMove(Move move) {
		moveDao.delete(move);
	}
	public MFXTableView<LocationName> generateTableLocationName(Consumer<LocationName> onRowClick, LocationName.Field[] hidden) {
		return locationNameDao.generateTable(onRowClick, hidden);
	}
	public MFXTableView<LocationName> generateTableLocationName(Consumer<LocationName> onRowClick) {
		return locationNameDao.generateTable(onRowClick);
	}
	public Optional<LocationName> getLocationName(java.lang.Long key) {
		return locationNameDao.get(key);
	}
	public Map<java.lang.Long, LocationName> getLocationName(LocationName.Field column, Object value) {
		return locationNameDao.get(column, value);
	}
	public Map<java.lang.Long, LocationName> getLocationName(LocationName.Field[] params, Object[] value) {
		return locationNameDao.get(params, value);
	}
	public ObservableMap<java.lang.Long, LocationName> getAllLocationName() {
		return locationNameDao.getAll();
	}
	public ObservableList<LocationName> getAllAsListLocationName() {
		return locationNameDao.getAllAsList();
	}
	public void saveLocationName(LocationName locationName) {
		locationNameDao.save(locationName);
	}
	public void updateLocationName(LocationName locationName, LocationName.Field[] params) {
		locationNameDao.update(locationName, params);
	}
	public void deleteLocationName(LocationName locationName) {
		locationNameDao.delete(locationName);
	}
	public MFXTableView<RequestEntry> generateTableRequestEntry(Consumer<RequestEntry> onRowClick, RequestEntry.Field[] hidden) {
		return requestEntryDao.generateTable(onRowClick, hidden);
	}
	public MFXTableView<RequestEntry> generateTableRequestEntry(Consumer<RequestEntry> onRowClick) {
		return requestEntryDao.generateTable(onRowClick);
	}
	public Optional<RequestEntry> getRequestEntry(java.util.UUID key) {
		return requestEntryDao.get(key);
	}
	public Map<java.util.UUID, RequestEntry> getRequestEntry(RequestEntry.Field column, Object value) {
		return requestEntryDao.get(column, value);
	}
	public Map<java.util.UUID, RequestEntry> getRequestEntry(RequestEntry.Field[] params, Object[] value) {
		return requestEntryDao.get(params, value);
	}
	public ObservableMap<java.util.UUID, RequestEntry> getAllRequestEntry() {
		return requestEntryDao.getAll();
	}
	public ObservableList<RequestEntry> getAllAsListRequestEntry() {
		return requestEntryDao.getAllAsList();
	}
	public void saveRequestEntry(RequestEntry requestEntry) {
		requestEntryDao.save(requestEntry);
	}
	public void updateRequestEntry(RequestEntry requestEntry, RequestEntry.Field[] params) {
		requestEntryDao.update(requestEntry, params);
	}
	public void deleteRequestEntry(RequestEntry requestEntry) {
		requestEntryDao.delete(requestEntry);
	}
	public MFXTableView<FoodServiceRequestEntry> generateTableFoodServiceRequestEntry(Consumer<FoodServiceRequestEntry> onRowClick, FoodServiceRequestEntry.Field[] hidden) {
		return foodServiceRequestEntryDao.generateTable(onRowClick, hidden);
	}
	public MFXTableView<FoodServiceRequestEntry> generateTableFoodServiceRequestEntry(Consumer<FoodServiceRequestEntry> onRowClick) {
		return foodServiceRequestEntryDao.generateTable(onRowClick);
	}
	public Optional<FoodServiceRequestEntry> getFoodServiceRequestEntry(java.util.UUID key) {
		return foodServiceRequestEntryDao.get(key);
	}
	public Map<java.util.UUID, FoodServiceRequestEntry> getFoodServiceRequestEntry(FoodServiceRequestEntry.Field column, Object value) {
		return foodServiceRequestEntryDao.get(column, value);
	}
	public Map<java.util.UUID, FoodServiceRequestEntry> getFoodServiceRequestEntry(FoodServiceRequestEntry.Field[] params, Object[] value) {
		return foodServiceRequestEntryDao.get(params, value);
	}
	public ObservableMap<java.util.UUID, FoodServiceRequestEntry> getAllFoodServiceRequestEntry() {
		return foodServiceRequestEntryDao.getAll();
	}
	public ObservableList<FoodServiceRequestEntry> getAllAsListFoodServiceRequestEntry() {
		return foodServiceRequestEntryDao.getAllAsList();
	}
	public void saveFoodServiceRequestEntry(FoodServiceRequestEntry foodServiceRequestEntry) {
		foodServiceRequestEntryDao.save(foodServiceRequestEntry);
	}
	public void updateFoodServiceRequestEntry(FoodServiceRequestEntry foodServiceRequestEntry, FoodServiceRequestEntry.Field[] params) {
		foodServiceRequestEntryDao.update(foodServiceRequestEntry, params);
	}
	public void deleteFoodServiceRequestEntry(FoodServiceRequestEntry foodServiceRequestEntry) {
		foodServiceRequestEntryDao.delete(foodServiceRequestEntry);
	}
	public MFXTableView<FlowerDeliveryRequestEntry> generateTableFlowerDeliveryRequestEntry(Consumer<FlowerDeliveryRequestEntry> onRowClick, FlowerDeliveryRequestEntry.Field[] hidden) {
		return flowerDeliveryRequestEntryDao.generateTable(onRowClick, hidden);
	}
	public MFXTableView<FlowerDeliveryRequestEntry> generateTableFlowerDeliveryRequestEntry(Consumer<FlowerDeliveryRequestEntry> onRowClick) {
		return flowerDeliveryRequestEntryDao.generateTable(onRowClick);
	}
	public Optional<FlowerDeliveryRequestEntry> getFlowerDeliveryRequestEntry(java.util.UUID key) {
		return flowerDeliveryRequestEntryDao.get(key);
	}
	public Map<java.util.UUID, FlowerDeliveryRequestEntry> getFlowerDeliveryRequestEntry(FlowerDeliveryRequestEntry.Field column, Object value) {
		return flowerDeliveryRequestEntryDao.get(column, value);
	}
	public Map<java.util.UUID, FlowerDeliveryRequestEntry> getFlowerDeliveryRequestEntry(FlowerDeliveryRequestEntry.Field[] params, Object[] value) {
		return flowerDeliveryRequestEntryDao.get(params, value);
	}
	public ObservableMap<java.util.UUID, FlowerDeliveryRequestEntry> getAllFlowerDeliveryRequestEntry() {
		return flowerDeliveryRequestEntryDao.getAll();
	}
	public ObservableList<FlowerDeliveryRequestEntry> getAllAsListFlowerDeliveryRequestEntry() {
		return flowerDeliveryRequestEntryDao.getAllAsList();
	}
	public void saveFlowerDeliveryRequestEntry(FlowerDeliveryRequestEntry flowerDeliveryRequestEntry) {
		flowerDeliveryRequestEntryDao.save(flowerDeliveryRequestEntry);
	}
	public void updateFlowerDeliveryRequestEntry(FlowerDeliveryRequestEntry flowerDeliveryRequestEntry, FlowerDeliveryRequestEntry.Field[] params) {
		flowerDeliveryRequestEntryDao.update(flowerDeliveryRequestEntry, params);
	}
	public void deleteFlowerDeliveryRequestEntry(FlowerDeliveryRequestEntry flowerDeliveryRequestEntry) {
		flowerDeliveryRequestEntryDao.delete(flowerDeliveryRequestEntry);
	}
	public MFXTableView<ConferenceRoomEntry> generateTableConferenceRoomEntry(Consumer<ConferenceRoomEntry> onRowClick, ConferenceRoomEntry.Field[] hidden) {
		return conferenceRoomEntryDao.generateTable(onRowClick, hidden);
	}
	public MFXTableView<ConferenceRoomEntry> generateTableConferenceRoomEntry(Consumer<ConferenceRoomEntry> onRowClick) {
		return conferenceRoomEntryDao.generateTable(onRowClick);
	}
	public Optional<ConferenceRoomEntry> getConferenceRoomEntry(java.util.UUID key) {
		return conferenceRoomEntryDao.get(key);
	}
	public Map<java.util.UUID, ConferenceRoomEntry> getConferenceRoomEntry(ConferenceRoomEntry.Field column, Object value) {
		return conferenceRoomEntryDao.get(column, value);
	}
	public Map<java.util.UUID, ConferenceRoomEntry> getConferenceRoomEntry(ConferenceRoomEntry.Field[] params, Object[] value) {
		return conferenceRoomEntryDao.get(params, value);
	}
	public ObservableMap<java.util.UUID, ConferenceRoomEntry> getAllConferenceRoomEntry() {
		return conferenceRoomEntryDao.getAll();
	}
	public ObservableList<ConferenceRoomEntry> getAllAsListConferenceRoomEntry() {
		return conferenceRoomEntryDao.getAllAsList();
	}
	public void saveConferenceRoomEntry(ConferenceRoomEntry conferenceRoomEntry) {
		conferenceRoomEntryDao.save(conferenceRoomEntry);
	}
	public void updateConferenceRoomEntry(ConferenceRoomEntry conferenceRoomEntry, ConferenceRoomEntry.Field[] params) {
		conferenceRoomEntryDao.update(conferenceRoomEntry, params);
	}
	public void deleteConferenceRoomEntry(ConferenceRoomEntry conferenceRoomEntry) {
		conferenceRoomEntryDao.delete(conferenceRoomEntry);
	}
	public MFXTableView<FurnitureRequestEntry> generateTableFurnitureRequestEntry(Consumer<FurnitureRequestEntry> onRowClick, FurnitureRequestEntry.Field[] hidden) {
		return furnitureRequestEntryDao.generateTable(onRowClick, hidden);
	}
	public MFXTableView<FurnitureRequestEntry> generateTableFurnitureRequestEntry(Consumer<FurnitureRequestEntry> onRowClick) {
		return furnitureRequestEntryDao.generateTable(onRowClick);
	}
	public Optional<FurnitureRequestEntry> getFurnitureRequestEntry(java.util.UUID key) {
		return furnitureRequestEntryDao.get(key);
	}
	public Map<java.util.UUID, FurnitureRequestEntry> getFurnitureRequestEntry(FurnitureRequestEntry.Field column, Object value) {
		return furnitureRequestEntryDao.get(column, value);
	}
	public Map<java.util.UUID, FurnitureRequestEntry> getFurnitureRequestEntry(FurnitureRequestEntry.Field[] params, Object[] value) {
		return furnitureRequestEntryDao.get(params, value);
	}
	public ObservableMap<java.util.UUID, FurnitureRequestEntry> getAllFurnitureRequestEntry() {
		return furnitureRequestEntryDao.getAll();
	}
	public ObservableList<FurnitureRequestEntry> getAllAsListFurnitureRequestEntry() {
		return furnitureRequestEntryDao.getAllAsList();
	}
	public void saveFurnitureRequestEntry(FurnitureRequestEntry furnitureRequestEntry) {
		furnitureRequestEntryDao.save(furnitureRequestEntry);
	}
	public void updateFurnitureRequestEntry(FurnitureRequestEntry furnitureRequestEntry, FurnitureRequestEntry.Field[] params) {
		furnitureRequestEntryDao.update(furnitureRequestEntry, params);
	}
	public void deleteFurnitureRequestEntry(FurnitureRequestEntry furnitureRequestEntry) {
		furnitureRequestEntryDao.delete(furnitureRequestEntry);
	}
	public MFXTableView<OfficeServiceRequestEntry> generateTableOfficeServiceRequestEntry(Consumer<OfficeServiceRequestEntry> onRowClick, OfficeServiceRequestEntry.Field[] hidden) {
		return officeServiceRequestEntryDao.generateTable(onRowClick, hidden);
	}
	public MFXTableView<OfficeServiceRequestEntry> generateTableOfficeServiceRequestEntry(Consumer<OfficeServiceRequestEntry> onRowClick) {
		return officeServiceRequestEntryDao.generateTable(onRowClick);
	}
	public Optional<OfficeServiceRequestEntry> getOfficeServiceRequestEntry(java.util.UUID key) {
		return officeServiceRequestEntryDao.get(key);
	}
	public Map<java.util.UUID, OfficeServiceRequestEntry> getOfficeServiceRequestEntry(OfficeServiceRequestEntry.Field column, Object value) {
		return officeServiceRequestEntryDao.get(column, value);
	}
	public Map<java.util.UUID, OfficeServiceRequestEntry> getOfficeServiceRequestEntry(OfficeServiceRequestEntry.Field[] params, Object[] value) {
		return officeServiceRequestEntryDao.get(params, value);
	}
	public ObservableMap<java.util.UUID, OfficeServiceRequestEntry> getAllOfficeServiceRequestEntry() {
		return officeServiceRequestEntryDao.getAll();
	}
	public ObservableList<OfficeServiceRequestEntry> getAllAsListOfficeServiceRequestEntry() {
		return officeServiceRequestEntryDao.getAllAsList();
	}
	public void saveOfficeServiceRequestEntry(OfficeServiceRequestEntry officeServiceRequestEntry) {
		officeServiceRequestEntryDao.save(officeServiceRequestEntry);
	}
	public void updateOfficeServiceRequestEntry(OfficeServiceRequestEntry officeServiceRequestEntry, OfficeServiceRequestEntry.Field[] params) {
		officeServiceRequestEntryDao.update(officeServiceRequestEntry, params);
	}
	public void deleteOfficeServiceRequestEntry(OfficeServiceRequestEntry officeServiceRequestEntry) {
		officeServiceRequestEntryDao.delete(officeServiceRequestEntry);
	}
	public MFXTableView<Employee> generateTableEmployee(Consumer<Employee> onRowClick, Employee.Field[] hidden) {
		return employeeDao.generateTable(onRowClick, hidden);
	}
	public MFXTableView<Employee> generateTableEmployee(Consumer<Employee> onRowClick) {
		return employeeDao.generateTable(onRowClick);
	}
	public Optional<Employee> getEmployee(java.lang.Long key) {
		return employeeDao.get(key);
	}
	public Map<java.lang.Long, Employee> getEmployee(Employee.Field column, Object value) {
		return employeeDao.get(column, value);
	}
	public Map<java.lang.Long, Employee> getEmployee(Employee.Field[] params, Object[] value) {
		return employeeDao.get(params, value);
	}
	public ObservableMap<java.lang.Long, Employee> getAllEmployee() {
		return employeeDao.getAll();
	}
	public ObservableList<Employee> getAllAsListEmployee() {
		return employeeDao.getAllAsList();
	}
	public void saveEmployee(Employee employee) {
		employeeDao.save(employee);
	}
	public void updateEmployee(Employee employee, Employee.Field[] params) {
		employeeDao.update(employee, params);
	}
	public void deleteEmployee(Employee employee) {
		employeeDao.delete(employee);
	}
	public MFXTableView<Account> generateTableAccount(Consumer<Account> onRowClick, Account.Field[] hidden) {
		return accountDao.generateTable(onRowClick, hidden);
	}
	public MFXTableView<Account> generateTableAccount(Consumer<Account> onRowClick) {
		return accountDao.generateTable(onRowClick);
	}
	public Optional<Account> getAccount(java.lang.Long key) {
		return accountDao.get(key);
	}
	public Map<java.lang.Long, Account> getAccount(Account.Field column, Object value) {
		return accountDao.get(column, value);
	}
	public Map<java.lang.Long, Account> getAccount(Account.Field[] params, Object[] value) {
		return accountDao.get(params, value);
	}
	public ObservableMap<java.lang.Long, Account> getAllAccount() {
		return accountDao.getAll();
	}
	public ObservableList<Account> getAllAsListAccount() {
		return accountDao.getAllAsList();
	}
	public void saveAccount(Account account) {
		accountDao.save(account);
	}
	public void updateAccount(Account account, Account.Field[] params) {
		accountDao.update(account, params);
	}
	public void deleteAccount(Account account) {
		accountDao.delete(account);
	}
	public MFXTableView<Signage> generateTableSignage(Consumer<Signage> onRowClick, Signage.Field[] hidden) {
		return signageDao.generateTable(onRowClick, hidden);
	}
	public MFXTableView<Signage> generateTableSignage(Consumer<Signage> onRowClick) {
		return signageDao.generateTable(onRowClick);
	}
	public Optional<Signage> getSignage(java.lang.Long key) {
		return signageDao.get(key);
	}
	public Map<java.lang.Long, Signage> getSignage(Signage.Field column, Object value) {
		return signageDao.get(column, value);
	}
	public Map<java.lang.Long, Signage> getSignage(Signage.Field[] params, Object[] value) {
		return signageDao.get(params, value);
	}
	public ObservableMap<java.lang.Long, Signage> getAllSignage() {
		return signageDao.getAll();
	}
	public ObservableList<Signage> getAllAsListSignage() {
		return signageDao.getAllAsList();
	}
	public void saveSignage(Signage signage) {
		signageDao.save(signage);
	}
	public void updateSignage(Signage signage, Signage.Field[] params) {
		signageDao.update(signage, params);
	}
	public void deleteSignage(Signage signage) {
		signageDao.delete(signage);
	}
	public MFXTableView<Alert> generateTableAlert(Consumer<Alert> onRowClick, Alert.Field[] hidden) {
		return alertDao.generateTable(onRowClick, hidden);
	}
	public MFXTableView<Alert> generateTableAlert(Consumer<Alert> onRowClick) {
		return alertDao.generateTable(onRowClick);
	}
	public Optional<Alert> getAlert(java.util.UUID key) {
		return alertDao.get(key);
	}
	public Map<java.util.UUID, Alert> getAlert(Alert.Field column, Object value) {
		return alertDao.get(column, value);
	}
	public Map<java.util.UUID, Alert> getAlert(Alert.Field[] params, Object[] value) {
		return alertDao.get(params, value);
	}
	public ObservableMap<java.util.UUID, Alert> getAllAlert() {
		return alertDao.getAll();
	}
	public ObservableList<Alert> getAllAsListAlert() {
		return alertDao.getAllAsList();
	}
	public void saveAlert(Alert alert) {
		alertDao.save(alert);
	}
	public void updateAlert(Alert alert, Alert.Field[] params) {
		alertDao.update(alert, params);
	}
	public void deleteAlert(Alert alert) {
		alertDao.delete(alert);
	}
}