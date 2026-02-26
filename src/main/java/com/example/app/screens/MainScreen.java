package com.example.app.screens;

import com.example.app.App;
import com.example.app.controllers.MainController;
import com.example.app.models.User;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class MainScreen {
    //Main Screen fields
    private App app;
    private User user;
    private ImageView search_icon = new ImageView(
        new Image(
            getClass().getResourceAsStream("/assets/search_icon.png"),
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

    private boolean addUserActive = false;  //flage to check the state of the addUserButton

    private HBox toolbar = new HBox(10);

    private Pane canvas = new Pane();
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
    private Button submit = new Button("submit");


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
        canvas.setStyle("-fx-background-color: black;");
        main_area.getChildren().add(canvas);
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

        //categories setup TODO : add categories
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
            new Region() {{ setPrefWidth(180); }}, 
            new Label("Add to Watch List"),
            add_user_icon
        );
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
        grid.add(submit,1,5);

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
        main_area.getChildren().add(canvas);
    }

    //Setters
    public void setAddUserActive(){
        addUserActive = !addUserActive;
    }

    //Getters
    public HBox getScreen(){
        return screen;
    }

    public ImageView getAddUsers(){
        return add_user_icon;
    }

    public boolean getAddUserActive(){
        return addUserActive;
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

    public ComboBox<String> getCategories() {
        return categories;
    }

    public User getUser() {
        return user;
    }
}
