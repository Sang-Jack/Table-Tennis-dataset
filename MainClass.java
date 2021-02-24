package main;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainClass extends Application {

    Database db = new Database();
    TextField newTeam = new TextField();
    TextField newPlayer = new TextField();
    ComboBox<String> teamPlayer = new ComboBox<String>();
    private String teamSelected;

    /* Third Page DropDown*/
    ComboBox<String> homeTeam = new ComboBox<>();
    ComboBox<String> awayTeam = new ComboBox<>();
    ComboBox<String> awayPlayer1 = new ComboBox<>();
    ComboBox<String> awayPlayer2 = new ComboBox<>();
    ComboBox<String> homePlayer1 = new ComboBox<>();
    ComboBox<String> homePlayer2 = new ComboBox<>();
    private String homeTeamSelected;
    private String awayTeamSelected;
    private List<String> player = new ArrayList<>();
    private List<String> checkScore = new ArrayList<>();
    private String finalScoreSet;

    private boolean modify = false;

    public void setTeams() {
        db.getTeams().stream().distinct().forEach(team -> {
            teamPlayer.getItems().add(team.getName());
        });
    }

    public void setHomeMatchToPlay() {
        db.getMatchs().forEach(m -> {
            homeTeam.getItems().add(m.getHomeTeam());
        });
    }

    public void setAwayTeam() {
        db.getMatchs(homeTeamSelected).forEach(ateam -> {
            awayTeam.getItems().add(ateam);
        });
    }

    public void setMatchPlayed() {
        homeTeam.getItems().clear();
        db.getMatchs().stream().filter(m -> m.isStatus()).forEach(m -> {
            homeTeam.getItems().add(m.getHomeTeam());
        });
    }

    public void setPlayer(String team) {
        player = db.getPlayerWithTeam(team);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // First Page Items
        Label lbl1 = new Label("Enter a new team: ");
        Label lbl2 = new Label("Enter the name of the new player : ");
        Label lbl3 = new Label(
                "This will generate a match between all teams\nWarning: all pre-exisiting match information will be removed!");
        Label lbl4 = new Label(
                "The stats report will be generated automatically every 100 secs\nYou can also generate it by clicking the button on the right");

        Button btnAddTeam = new Button("Add Team");
        Button btnAddplayer = new Button("Register Player");
        Button btnGenMatch = new Button("Generate Fixtures");
        Button btn4 = new Button("Generate Team Stats");

        btnAddTeam.setOnAction(e -> {
            addNewTeam();
        });

        teamPlayer.setOnAction(e -> {
            teamSelected = teamPlayer.getValue();
        });

        teamPlayer.setPromptText("Select a Team");
        setTeams();

        btnAddplayer.setOnAction(e -> {
            addPlayer();
        });

        btnGenMatch.setOnAction(e -> {
            generateRandomMatch();
        });

        btn4.setOnAction(e -> {
            long teamA;
            long teamB;
            long teamC;
            long teamD;
            List<Match> match1 = new ArrayList<>();
            List<Team> team1 = db.getTeams();
            db.getMatchs().stream().filter(m -> m.isStatus()).forEach(m -> match1.add(m));
            teamA = match1.stream().filter(m -> m.getHomeTeam().equals(team1.iterator().next().getName())).count();
            teamB = match1.stream().filter(m -> m.getHomeTeam().equals(team1.iterator().next().getName())).count();
            teamC = match1.stream().filter(m -> m.getHomeTeam().equals(team1.iterator().next().getName())).count();
            teamD = match1.stream().filter(m -> m.getHomeTeam().equals(team1.iterator().next().getName())).count();

        });

        HBox hb1 = new HBox();
        HBox hb2 = new HBox();
        HBox hb3 = new HBox();
        HBox hb4 = new HBox();

        hb1.setSpacing(20);
        hb1.setStyle("-fx-border-color:black;-fx-border-radius:5px;");
        hb1.setPadding(new Insets(10, 10, 10, 10));
        hb1.getChildren().addAll(lbl1, newTeam, btnAddTeam);

        hb2.setSpacing(20);
        hb2.setStyle("-fx-border-color:black;-fx-border-radius:5px;");
        hb2.setPadding(new Insets(10, 10, 10, 10));
        hb2.getChildren().addAll(lbl2, newPlayer, teamPlayer, btnAddplayer);

        hb3.setSpacing(20);
        hb3.setStyle("-fx-border-color:black;-fx-border-radius:5px;");
        hb3.setPadding(new Insets(10, 10, 10, 10));
        hb3.getChildren().addAll(lbl3, btnGenMatch);

        hb4.setSpacing(20);
        hb4.setStyle("-fx-border-color:black;-fx-border-radius:5px;");
        hb4.setPadding(new Insets(10, 10, 10, 10));
        hb4.getChildren().addAll(lbl4, btn4);

        VBox vb1 = new VBox();
        vb1.setPadding(new Insets(10, 10, 10, 10));
        vb1.setSpacing(20);
        vb1.getChildren().addAll(hb1, hb2, hb3, hb4);

        // <-------------------------Second page items
        HBox hb5 = new HBox();
        hb5.setPadding(new Insets(10, 10, 10, 10));
        hb5.setSpacing(20);
        Button btn5 = new Button("View fixture and result chart");
        Button btn6 = new Button("Show all team stats");
        Button btn7 = new Button("Show all team ranking");
        Button btn8 = new Button("View a match scores");

        VBox vb2 = new VBox();
        vb2.setPadding(new Insets(10, 10, 10, 10));
        vb2.setSpacing(20);
        vb2.getChildren().addAll(btn5, btn6, btn7, btn8);

        TextArea textArea = new TextArea();
        textArea.setPrefWidth(750);
        textArea.setPrefHeight(200);
        textArea.setEditable(false);
        hb5.getChildren().addAll(vb2, textArea);
        TabPane tabPane = new TabPane();

        btn6.setOnAction(e -> {
            List<Match> matchGamed = new ArrayList<>();
            db.getMatchs().stream().filter(m -> m.isStatus()).distinct().forEach(m -> matchGamed.add(m));
            SetScore sc = db.getAllSetScore(db.getTeamStats().getSetScore_ids().getId()).get(0);
            matchGamed.forEach(m -> {
                textArea.setText("Match: " + m.getHomeTeam() + " vs " + m.getAwayTeam() + "\n"
                        + "SingleSets \n"
                        + "Set{" + db.getPlayerWithTeam(m.getHomeTeam()).get(0) + " vs " + db.getPlayerWithTeam(m.getAwayTeam()).get(0) + "= " + sc.getS1a() + "," + sc.getS1b() + "," + sc.getS1c() + "}\n"
                        + "Set{" + db.getPlayerWithTeam(m.getHomeTeam()).get(0) + " vs " + db.getPlayerWithTeam(m.getAwayTeam()).get(1) + "= " + sc.getS2a() + "," + sc.getS2b() + "," + sc.getS2c() + "}\n"
                        + "Set{" + db.getPlayerWithTeam(m.getHomeTeam()).get(1) + " vs " + db.getPlayerWithTeam(m.getAwayTeam()).get(0) + "= " + sc.getS3a() + "," + sc.getS3b() + "," + sc.getS3c() + "}\n"
                        + "Set{" + db.getPlayerWithTeam(m.getHomeTeam()).get(1) + " vs " + db.getPlayerWithTeam(m.getAwayTeam()).get(1) + "= " + sc.getS4a() + "," + sc.getS4b() + "," + sc.getS4c() + "}\n"
                        + "Double Set: Set= {" + sc.getD1() + "," + sc.getD2() + "," + sc.getD3() + "}\n"
                        + "Final Score: ");
            });
        });

        // <-------------------------Third page items
        VBox vb3 = new VBox();
        vb3.setPadding(new Insets(10, 10, 10, 10));
        vb3.setSpacing(20);

        HBox hb6 = new HBox();
        hb6.setPadding(new Insets(10, 10, 10, 10));
        hb6.setSpacing(20);

        Button btn9 = new Button("New sheet");
        Button btn10 = new Button("Modify Sheet");
        hb6.getChildren().addAll(btn9, btn10);
        Button btn11 = new Button("Calculate and Submit Scores");

        btn10.setOnAction(e -> {
            modify = true;
        });

        Label lbl5 = new Label("Home Team");
        Label lbl6 = new Label("Away Team");
        Label lbl7 = new Label("Single Set");
        Label lbl8 = new Label("Double Set");

        homeTeam.setPromptText("Home Team");
        setHomeMatchToPlay();
        homeTeam.setOnAction(e -> {
            if (modify) {
                setMatchPlayed();
            } else {
                player.clear();
                homePlayer1.getItems().clear();
                homePlayer2.getItems().clear();
                awayPlayer1.getItems().clear();
                awayPlayer2.getItems().clear();
                homeTeamSelected = homeTeam.getValue();
                setPlayer(homeTeamSelected);
                homePlayer1.getItems().add(player.get(0));
                homePlayer2.getItems().add(player.get(1));
                awayTeam.getItems().clear();
                setAwayTeam();
            }

        });

        awayTeam.setOnAction(e -> {
            player.clear();
            awayPlayer1.getItems().clear();
            awayPlayer2.getItems().clear();
            awayTeamSelected = awayTeam.getValue();
            setPlayer(awayTeamSelected);
            if (!player.isEmpty()) {
                awayPlayer1.getItems().add(player.get(0));
                awayPlayer2.getItems().add(player.get(1));
            }
        });

        awayTeam.setPromptText("Away Team");

        awayPlayer1.setPromptText("Away Player");

        awayPlayer2.setPromptText("Away Player");

        homePlayer1.setPromptText("Home Player");

        homePlayer2.setPromptText("Home Player");

        TextField tf1 = new TextField("1:0");
        TextField tf2 = new TextField("2:0");
        TextField tf3 = new TextField("3:0");
        TextField tf4 = new TextField("0:0");
        TextField tf5 = new TextField("0:0");
        TextField tf6 = new TextField("0:0");
        TextField tf7 = new TextField("0:0");
        TextField tf8 = new TextField("0:0");
        TextField tf9 = new TextField("0:0");
        TextField tf10 = new TextField("0:0");
        TextField tf11 = new TextField("0:0");
        TextField tf12 = new TextField("0:0");
        TextField tf13 = new TextField("0:0");
        TextField tf14 = new TextField("0:0");
        TextField tf15 = new TextField("0:0");

        SetScore setSc = new SetScore();
        Score s = new Score();

        Separator sep = new Separator();
        sep.setHalignment(HPos.CENTER);

        TextArea finalScore = new TextArea("Final Team Scores");
        finalScore.setMaxHeight(80);
        finalScore.setMaxWidth(150);

        btn11.setOnAction(e -> {
            setSc.setS1a(tf1.getText());
            setSc.setS1b(tf2.getText());
            setSc.setS1c(tf3.getText());
            setSc.setS2a(tf4.getText());
            setSc.setS2b(tf5.getText());
            setSc.setS2c(tf6.getText());
            setSc.setS3a(tf7.getText());
            setSc.setS3b(tf8.getText());
            setSc.setS3c(tf9.getText());
            setSc.setS4a(tf10.getText());
            setSc.setS4b(tf11.getText());
            setSc.setS4c(tf12.getText());
            setSc.setD1(tf13.getText());
            setSc.setD2(tf14.getText());
            setSc.setD3(tf15.getText());

            setCalculation(tf1.getText(), tf2.getText(), tf3.getText());
            setCalculation(tf4.getText(), tf5.getText(), tf6.getText());
            setCalculation(tf7.getText(), tf8.getText(), tf9.getText());
            setCalculation(tf10.getText(), tf11.getText(), tf12.getText());
            setCalculation(tf13.getText(), tf14.getText(), tf15.getText());

            s.setScore_setA(checkScore.get(0));
            s.setScore_setB(checkScore.get(1));
            s.setScore_setC(checkScore.get(2));
            s.setScore_setD(checkScore.get(3));
            s.setdSet(checkScore.get(4));

            finalCalculation(checkScore);
            finalScore.setText(finalScoreSet);

            db.addTotalScore(setSc, s);
        });

        GridPane pane = new GridPane();
        pane.add(lbl5, 1, 0);
        pane.add(lbl6, 2, 0);
        pane.add(homeTeam, 1, 1);
        pane.add(awayTeam, 2, 1);

        pane.add(lbl7, 0, 2);
        pane.add(awayPlayer1, 1, 2);
        pane.add(awayPlayer2, 2, 2);

        GridPane pane1 = new GridPane();
        pane1.add(tf1, 0, 0);
        pane1.add(tf2, 0, 1);
        pane1.add(tf3, 0, 2);
        GridPane pane2 = new GridPane();
        pane2.add(tf4, 0, 0);
        pane2.add(tf5, 0, 1);
        pane2.add(tf6, 0, 2);
        pane.add(homePlayer1, 0, 3);
        pane.add(pane1, 1, 3);
        pane.add(pane2, 2, 3);

        GridPane pane3 = new GridPane();
        pane3.add(tf7, 0, 0);
        pane3.add(tf8, 0, 1);
        pane3.add(tf9, 0, 2);
        GridPane pane4 = new GridPane();
        pane4.add(tf10, 0, 0);
        pane4.add(tf11, 0, 1);
        pane4.add(tf12, 0, 2);
        pane.add(homePlayer2, 0, 4);
        pane.add(pane3, 1, 4);
        pane.add(pane4, 2, 4);

        pane.add(lbl8, 0, 5);
        GridPane pane5 = new GridPane();
        pane5.add(tf13, 0, 0);
        pane5.add(tf14, 0, 1);
        pane5.add(tf15, 0, 2);
        pane.add(pane5, 1, 5);
        pane.add(finalScore, 2, 5);
        pane.add(btn11, 1, 7);

        // Setting the padding
        pane.setPadding(new Insets(10, 10, 10, 10));

        // Setting the vertical and horizontal gaps between the columns
        pane.setVgap(10);
        pane.setHgap(10);

        pane1.setVgap(5);
        pane2.setVgap(5);
        pane3.setVgap(5);
        pane4.setVgap(5);
        pane5.setVgap(5);

        // Setting the Grid alignment
        pane.setAlignment(Pos.CENTER_LEFT);

        vb3.getChildren().addAll(hb6, sep, pane);
        Tab tab1 = new Tab("Admin Page");
        Tab tab2 = new Tab("Viewer Page");
        Tab tab3 = new Tab("Score Sheet");
        tab1.setClosable(false);
        tab2.setClosable(false);
        tab3.setClosable(false);

        tab1.setContent(vb1);
        tab2.setContent(hb5);
        tab3.setContent(vb3);
        tabPane.getTabs().add(tab1);
        tabPane.getTabs().add(tab2);
        tabPane.getTabs().add(tab3);

        StackPane root = new StackPane();
        root.getChildren().add(tabPane);
        Scene scene = new Scene(root, 1000, 800);
        primaryStage.setTitle("JavaFX TennisScore Application");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void setCalculation(String a, String b, String c) {
        List<String> list = new ArrayList<>();
        int count1 = 0;
        int count2 = 0;
        list.add(a);
        list.add(b);
        list.add(c);
        for (String t : list) {
            int index = t.indexOf(":");
            String t1 = t.substring(0, index);
            String t2 = t.substring(index + 1);
            int num1 = Integer.parseInt(t1);
            int num2 = Integer.parseInt(t2);
            if (num1 > num2) {
                count1++;
            } else if (num2 > num1) {
                count2++;
            }
        }
        String sc = count1 + ":" + count2;
        checkScore.add(sc);
    }

    private void finalCalculation(List<String> list) {
        int count1 = 0;
        int count2 = 0;
        for (String t : list) {
            int index = t.indexOf(":");
            String t1 = t.substring(0, index);
            String t2 = t.substring(index + 1);
            int num1 = Integer.parseInt(t1);
            int num2 = Integer.parseInt(t2);
            if (num1 > num2) {
                count1++;
            } else if (num2 > num1) {
                count2++;
            }
        }
        finalScoreSet = count1 + ":" + count2;
    }

    private void addNewTeam() {
        String team = newTeam.getText();
        if (team.isEmpty()) {
            Alert a = new Alert(AlertType.WARNING);
            a.setHeaderText("Empty name");
            a.setContentText("Team name cant be empty");
            a.showAndWait();
            reset();
        } else {
            boolean status = db.addTeam(team);
            if (status) {
                Alert a = new Alert(AlertType.INFORMATION);
                a.setHeaderText("Team Added Successfully");
                reset();
            } else {
                Alert a = new Alert(AlertType.ERROR);
                a.setHeaderText("DB Error");
                a.setContentText("Check Console");
                reset();
            }
        }

    }

    private void addPlayer() {
        String playerName = newPlayer.getText();
        db.addPlayer(teamSelected, playerName);
        reset();
    }

    private void generateRandomMatch() {
        db.addMatch();
    }

    public void reset() {
        newPlayer.setText("");
        newTeam.setText("");
    }

    public static void main(String[] args) {
        launch(args);
    }

}
