package com.example.app.screens;



import java.util.ArrayList;

import com.example.app.App;
import com.example.app.Utils;
import com.example.app.controllers.MainController;
import com.example.app.controllers.WatchingButtonControllers;
import com.example.app.controllers.XButtonControllers;
import com.example.app.models.TextFile;
import com.example.app.models.User;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Callback;


public class MainScreen {
    //Main Screen fields
    private App app;
    private User user;
    private boolean isActionActive = false;  //flage to check the state of the addUserButton
    private TextFile activeFile;
    private TextArea textArea;

    private ImageView search_icon = new ImageView(
        new Image(
            getClass().getResourceAsStream("/assets/search_icon.png"),
            0,32,true,false
        )
    );

    private ImageView new_file_icon = new ImageView(
        new Image(
            getClass().getResourceAsStream("/assets/new_file_icon.png"),
            0,32,true,false
        )
    );

    private ComboBox<String> categories = new ComboBox<>();

    private TextField search_bar = new TextField();

    private  ImageView add_user_icon = new ImageView(
        new Image( 
            getClass().getResourceAsStream("/assets/add_user_icon.png"),
            0,27,true,false
        )
    ); 


    private HBox toolbar = new HBox(10);

    private StackPane main_area = new StackPane();
    private VBox work_area = new VBox();
    private StackPane work_area_wrapper = new StackPane();
    private ImageView user_icon = new ImageView(
        new Image( 
            getClass().getResourceAsStream("/assets/user_icon.png"),
            0,75,true,false
        )
    );

    private VBox userData = new VBox();
    private HBox userCard = new HBox(5);

    private HBox screen = new HBox(5);
    
    //Form Fields 
    private TextField firstName = new TextField();
    private TextField lastName = new TextField();
    private TextField username = new TextField();
    private TextField password = new TextField();
    private final RadioButton adminRole = new RadioButton("Admin");
    private final RadioButton writerRole = new RadioButton("Writer");
    private final RadioButton userRole = new RadioButton("User");
    private ToggleGroup roleGroup = new ToggleGroup();

    private ListView<String> categoriesList;
    private Button submit = new Button("submit");

    //All purpose buttons
    private Button goBack = new Button("Go Back");
    private Button save = new Button("Save");


    public MainScreen(App app,User user){
        this.app = app;
        this.user=user;

       createLeftSide();

       createRightSide();



        screen.getChildren().addAll(work_area_wrapper,userCard);

        new MainController(app,this);
    }
    //Construct Screen 
    private void createLeftSide(){
         createToolbar();

        //main screen setup
        main_area.getChildren().add(createFileViewer());
        main_area.setStyle("-fx-border-color: gray; -fx-border-width: 1px;");

        
        work_area.setMaxWidth(400);
        work_area.getChildren().addAll(toolbar,main_area);


        VBox.setVgrow(main_area, Priority.ALWAYS);

        work_area_wrapper.setAlignment(Pos.CENTER_LEFT); // or CENTER, TOP_LEFT, etc.
        work_area_wrapper.getChildren().add(work_area);

        work_area.setMaxWidth(600);
    }

    private void createRightSide(){
         //User Data setup
        final Label nameLabel = new Label(user.getName());
        nameLabel.setStyle("-fx-font-weight: bold;");

        final Label usernameLabel = new Label("@" + user.getUsername());
        usernameLabel.setStyle("-fx-font-style: italic;");

        final Label roleLabel = new Label(user.getRole());
        roleLabel.setStyle("-fx-text-fill: blue; -fx-underline: true; -fx-font-size: 10px;");

        userData.getChildren().addAll(
            nameLabel,
            usernameLabel,
            new Label(), //empty field
            roleLabel
        );


        userCard.getChildren().addAll(user_icon,userData);
        userCard.setPadding(new Insets(5,5,5,5));
        userCard.setAlignment(Pos.TOP_RIGHT);

        
        userData.setMaxHeight(Region.USE_PREF_SIZE);
    }

    private void createToolbar(){
        //searchbar setup
        search_bar.setMinWidth(100);
        search_bar.setMaxWidth(100);
        search_bar.setPrefHeight(20);
        search_bar.setMaxHeight(20);
        search_bar.setStyle(
            "-fx-background-radius: 100em;" 
        );

        categories.getItems().add("All");
        for(String category : user.getCategories()){
            categories.getItems().add(category);
        }
        if(user.getRole().equals("Admin")){
            categories.getItems().add("Create Category...");
        }
        categories.setStyle("-fx-color: red");
        categories.setValue("All");


        //toolbar setup
        toolbar.getChildren().addAll(
            search_icon,
            search_bar,
            categories,
            new Region() {{ setPrefWidth(150); }}, 
            new Label("Add to Watch List")
        );

        if(user.getRole().equals("Admin")){
            toolbar.getChildren().add(add_user_icon);
        }

        toolbar.getChildren().add(new_file_icon);

        toolbar.setPadding(new Insets(15, 10, 15, 10));
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setStyle("-fx-border-color: gray; -fx-border-width: 1px;");
    }

    //Change Main Screen 
    public void createUserForm(){
        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(10);
        grid.add(new Label("Create New User"),0,0,2,1);
        grid.add(new Label("First Name:"),0,1);
        grid.add(firstName,1,1);
        grid.add(new Label("Last Name:"),0,2);
        grid.add(lastName,1,2);
        grid.add(new Label("username:"), 0,3);
        grid.add(username,1,3);
        grid.add(new Label("password:"),0,4);
        grid.add(password,1,4);

        //radio buttons for roles
        HBox rolesBox = new HBox(10);

        roleGroup = new ToggleGroup();
        adminRole.setToggleGroup(roleGroup);
        writerRole.setToggleGroup(roleGroup);
        userRole.setToggleGroup(roleGroup);
        userRole.setSelected(true); 

        rolesBox.getChildren().addAll(userRole, writerRole, adminRole);
        grid.add(new Label("Role:"),0,5);
        grid.add(rolesBox,1,5);

        categoriesList = new ListView<>();
        for(String category : user.getCategories()){
            if(!category.equals("All")) categoriesList.getItems().add(category);
        }
        categoriesList.setPrefHeight(100);
        categoriesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        grid.add(categoriesList,1,6);
        grid.add(submit,1,7 );

        GridPane.setHalignment(submit,HPos.RIGHT);

        // Set white background covering the full area
        grid.setBackground(new Background(new BackgroundFill(
            Color.WHITE, 
            CornerRadii.EMPTY, 
            Insets.EMPTY
        )));

        // Make it fill the StackPane completely
        grid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        grid.setAlignment(Pos.CENTER);

        main_area.getChildren().add(grid); // covert the main area with the form
    }

    public void returnToMainArea(){
        main_area.getChildren().clear();
        main_area.getChildren().add(createFileViewer());
    }

    public void textFileArea(TextFile file){
        activeFile = file;
        main_area.getChildren().clear();
        textArea = new TextArea();
        VBox area = new VBox(5);
        Label title = new Label(file.getTitle() + " By " + file.getAuthor());

        HBox buttonRow = new HBox();
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        buttonRow.setPadding(new Insets(5,10,10,10));
        buttonRow.getChildren().addAll(goBack, spacer,save);


        area.getChildren().addAll(title, textArea, buttonRow);

        VBox.setVgrow(textArea, Priority.ALWAYS);
        area.setAlignment(Pos.CENTER);
        main_area.getChildren().add(area);
    }

    //Setters
    public void setActionActiveFlage(){
        isActionActive= !isActionActive;
    }  

    public void setActiveFiletoNull(){
        activeFile = null;
    }

    //Getters
    public HBox getScreen(){
        return screen;
    }

    public ImageView getAddUsers(){
        return add_user_icon;
    }

    public ImageView getNewFileIcon(){
        return new_file_icon;
    }

    public boolean getActionActiveFlag(){
        return isActionActive;
    }

    public TextField getFirstName() {
        return firstName;
    }

    public TextField getLastName() {
        return lastName;
    }

    public TextField getUsername() {
        return username;
    }

    public TextField getPassword() {
        return password;
    }

    public Button getSubmit() {
        return submit;
    }

    public ToggleGroup getRoleGroup() {
        return roleGroup;
    }

    public ListView<String> getCategoriesList() {
        return categoriesList;
    }

    public ComboBox<String> getCategories() {
        return categories;
    }

    public Button getGoBack() {
        return goBack;
    }

    public Button getSave() {
        return save;
    }

    public User getUser() {
        return user;
    }

    public TextFile getActiveFile() {
        return activeFile;
    }

    public TextArea getTextArea() {
        return textArea;
    }

    private Node createFileViewer(){
        if(Utils.allTextFiles.isEmpty()){
            Label emptyLabel = new Label("There are no files to display.");
            emptyLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 14px;");
            return emptyLabel;
        }


        TableView<TextFile> fileTable = new TableView<>();
        fileTable.setEditable(false);
        //fileTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_NEXT_COLUMN);
        fileTable.setPrefHeight(400);

        fileTable.setSelectionModel(null);

        TableColumn<TextFile, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        titleCol.setMinWidth(150);

        TableColumn<TextFile, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuthor()));
        authorCol.setMinWidth(100);


        TableColumn<TextFile, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategory()));
        categoryCol.setMinWidth(100);

        TableColumn<TextFile, String> modifiedCol = new TableColumn<>("Last Modified");
        modifiedCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLastModified()));
        modifiedCol.setMinWidth(100);
        modifiedCol.setMaxWidth(100);
        modifiedCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<TextFile, String> versionCol = new TableColumn<>("Version");
        versionCol.setCellValueFactory(data -> new SimpleStringProperty("ver" + data.getValue().getVersion()));
        versionCol.setMinWidth(60);
        versionCol.setMaxWidth(80);
        versionCol.setStyle("-fx-alignment: CENTER;");

        //Buttons Column
        TableColumn<TextFile, Void> watchingCol = new TableColumn<>("");
        watchingCol.setMinWidth(40);
        watchingCol.setMaxWidth(40);
        watchingCol.setSortable(false);

        watchingCol.setCellFactory(new WatchingButtonControllers(this));

        TableColumn<TextFile, Void> deleteCol = new TableColumn<>("del");
        deleteCol.setMinWidth(40);
        deleteCol.setMaxWidth(40);
        deleteCol.setSortable(false);

        deleteCol.setCellFactory(new XButtonControllers(this));



        fileTable.getColumns().addAll(titleCol, authorCol, categoryCol, modifiedCol, versionCol,watchingCol,deleteCol);


        ObservableList<TextFile> files = FXCollections.observableArrayList(
            TextFile.filterBasedOnCategory(Utils.allTextFiles, user.getCategories())
        );
        fileTable.setItems(files);
        


        return fileTable;

    }


    public Dialog<String> createFileDialog(){
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Create File");
        dialog.setHeaderText("Create a new file");

        ButtonType createButtonType = new ButtonType("New", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Category:"), 0, 1);
        grid.add(categories, 1, 1);

        dialog.getDialogPane().setContent(grid);
        // Disable Create until valid
        Node createButton = dialog.getDialogPane()
                .lookupButton(createButtonType);
        createButton.setDisable(true);

        nameField.textProperty().addListener((obs, o, n) ->
                createButton.setDisable(n.trim().isEmpty())
        );

        // Focus name field
        Platform.runLater(nameField::requestFocus);
        return dialog;
    }
}
