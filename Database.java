package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Anonymous
 * @versoin 1.0.0
 * @since 22 02 2021
 */
public class Database {

    private static String dbURL = "jdbc:derby://localhost:1527/ScoreSheet;";
    private static Connection conn = null;
    private static ResultSet rs = null;
    String user = "root";
    String password = " ";

    private String insertQuery;
    private String selectQuery;
    private boolean status = false;

    List<Team> teams = new ArrayList<>();
    List<Player> players = new ArrayList<>();
    List<Match> matchs = new ArrayList<>();
    List<Score> scores = new ArrayList<>();
    List<SetScore> setscores = new ArrayList<>();
    List<Team_Has_Match> teamHasMatch = new ArrayList<>();

    public Database() {
        dbConfig();
        createTeam();
        createPlayer();
        createSetScore();
        createScore();
        createMatch();
    }

    private void dbConfig() {
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
            //Get a connection
            conn = DriverManager.getConnection(dbURL, user, password);
            System.out.println("Sucessfully Connected To ScoreSheet");
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException ex) {
            System.out.println("Error on DB Connection: " + ex.getLocalizedMessage());
        }
    }

    public Connection getConnection() {
        return conn;
    }

    public boolean addTeam(String name) {
        try {
            insertQuery = "INSERT INTO Team(Name) VALUES ('" + name + "')";
            PreparedStatement prep = conn.prepareStatement(insertQuery);
            prep.executeUpdate();
            status = true;
        } catch (SQLException ex) {
            System.out.println("SQL Error " + ex.getLocalizedMessage() + " " + ex.getSQLState());
        }
        return status;
    }

    public boolean addPlayer(String team, String name) {
        try {
            insertQuery = "INSERT INTO Player(Player_Name, Team_Name) VALUES ('" + name + "','" + team + "')";
            PreparedStatement prep = conn.prepareStatement(insertQuery);
            prep.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("SQL Error " + ex.getLocalizedMessage() + " " + ex.getSQLState());
        }
        return status;
    }

    public boolean addTotalScore(SetScore sc, Score s) {
        try {
            insertQuery = "INSERT INTO SetScore(S1A, S1B, S1C, S2A, S2B, S2C, S3A, S3B, S3C, S4A, S4B, S4C, D1, D2, D3) VALUES "
                    + "('" + sc.getS1a() + "','" + sc.getS1b() + "','" + sc.getS1c() + "'"
                    + ",'" + sc.getS2a() + "','" + sc.getS2b() + "','" + sc.getS2c() + "'"
                    + ",'" + sc.getS3a() + "','" + sc.getS3b() + "','" + sc.getS3c() + "'"
                    + ",'" + sc.getS4a() + "','" + sc.getS4b() + "','" + sc.getS4c() + "'"
                    + ",'" + sc.getD1() + "','" + sc.getD2() + "','" + sc.getD3() + "')";
            PreparedStatement prep = conn.prepareStatement(insertQuery);
            prep.executeUpdate();
            System.out.println("Insert into SetScore Successfull");

            selectQuery = "SELECT Id FROM SetScore";
            PreparedStatement prep1 = conn.prepareStatement(selectQuery);
            rs = prep1.executeQuery();
            while (rs.next()) {
                setscores.add(new SetScore(rs.getInt("Id")));
            }
            System.out.println("Select from SetScore Successfull");

            int size = setscores.size() - 1;
            insertQuery = "INSERT INTO Score(Score_SetA, Score_SetB, Score_SetC, Score_SetD, DSet, Set_Id) VALUES"
                    + "('" + s.getScore_setA() + "'"
                    + ",'" + s.getScore_setB() + "'"
                    + ",'" + s.getScore_setC() + "'"
                    + ",'" + s.getScore_setD() + "'"
                    + ",'" + s.getdSet() + "'"
                    + "," + setscores.get(size).getId() + ""
                    + ")";
            PreparedStatement prep2 = conn.prepareStatement(insertQuery);
            prep2.executeUpdate();
            System.out.println("Insert into Score Successfull");
            status = true;
        } catch (SQLException ex) {
            System.out.println("SQL Error " + ex.getLocalizedMessage() + " " + ex.getSQLState());
        }
        return status;
    }

    public void addMatch() {
        //First Delete All Match
        truncateMatchTable();
        try {
            selectQuery = "SELECT * FROM Team";
            PreparedStatement prep = conn.prepareStatement(selectQuery);
            rs = prep.executeQuery();
            while (rs.next()) {
                teams.add(new Team(rs.getInt("Id"), rs.getString("Name")));
            }
            int size = teams.size() - 1;
            String homeTeam, awayTeam;

            int permutation = 4; //For Four Teams

            //Small Permutation Trying To Register Enough Teams against those the have not yet played with.
            for (int i = 0; i < permutation; i++) {
                homeTeam = teams.get(getRandomNumber(0, size)).getName();
                awayTeam = teams.get(getRandomNumber(0, size)).getName();

                //If the random generation of match gives thesame team together restart the random
                while (homeTeam.equals(awayTeam)) {
                    homeTeam = teams.get(getRandomNumber(0, size)).getName();
                    awayTeam = teams.get(getRandomNumber(0, size)).getName();
                }
                //For Home Team
                insertQuery = "INSERT INTO Matchh(Team_Home, Team_Away, Status) VALUES ('" + homeTeam + "', '" + awayTeam + "'," + false + ")";
                PreparedStatement prep1 = conn.prepareStatement(insertQuery);
                prep1.executeUpdate();

                //Home Team becomes now awayTeam vice versa
                insertQuery = "INSERT INTO Matchh(Team_Home, Team_Away, Status) VALUES ('" + awayTeam + "', '" + homeTeam + "'," + false + ")";
                PreparedStatement prep2 = conn.prepareStatement(insertQuery);
                prep2.executeUpdate();

            }

        } catch (SQLException ex) {
            System.out.println("SQL Error " + ex.getLocalizedMessage() + " " + ex.getSQLState());
        }
    }

    protected void truncateMatchTable() {
        try {
            String deleteAll = "TRUNCATE TABLE MATCHH";
            PreparedStatement prep = conn.prepareStatement(deleteAll);
            prep.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("SQL Error " + ex.getLocalizedMessage() + " " + ex.getSQLState());
        }
    }

    public List<Team> getTeams() {
        try {
            selectQuery = "SELECT * FROM Team";
            PreparedStatement prep = conn.prepareStatement(selectQuery);
            rs = prep.executeQuery();
            while (rs.next()) {
                teams.add(new Team(rs.getInt("Id"), rs.getString("Name")));
            }
        } catch (SQLException ex) {
            System.out.println("SQL Error " + ex.getLocalizedMessage() + " " + ex.getSQLState());
        }
        return teams;
    }

    public List<Match> getMatchs() {
        try {
            selectQuery = "SELECT * FROM Matchh";
            PreparedStatement prep = conn.prepareStatement(selectQuery);
            rs = prep.executeQuery();
            Score s = new Score();
            while (rs.next()) {
                s.setId(rs.getInt("Score_Id"));
                matchs.add(new Match(rs.getInt("Id"), rs.getString("Team_Home"), rs.getString("Team_Away"), rs.getBoolean("Status"), s));
            }
        } catch (SQLException ex) {
            System.out.println("SQL Error " + ex.getLocalizedMessage() + " " + ex.getSQLState());
        }
        return matchs;
    }

    public List<String> getMatchs(String team) {
        ArrayList<String> listAwayTeam = new ArrayList<>();
        try {
            selectQuery = "SELECT * FROM Matchh WHERE Team_Home=?";
            PreparedStatement prep = conn.prepareStatement(selectQuery);
            prep.setString(1, team);
            rs = prep.executeQuery();
            while (rs.next()) {
                listAwayTeam.add(rs.getString("Team_Away"));
            }
        } catch (SQLException ex) {
            System.out.println("SQL Error " + ex.getLocalizedMessage() + " " + ex.getSQLState());
        }
        return listAwayTeam;
    }

    public List<SetScore> getAllSetScore(int id) {
        try {
            selectQuery = "SELECT * FROM SetScore WHERE Id=?";
            PreparedStatement prep = conn.prepareStatement(selectQuery);
            prep.setInt(1, id);
            rs = prep.executeQuery();
            while (rs.next()) {
                setscores.add(
                        new SetScore(
                                rs.getInt("Id"),
                                rs.getString("S1A"), rs.getString("S1B"), rs.getString("S1C"),
                                rs.getString("S2A"), rs.getString("S2B"), rs.getString("S2C"),
                                rs.getString("S3A"), rs.getString("S3B"), rs.getString("S3C"),
                                rs.getString("S4A"), rs.getString("S4B"), rs.getString("S4C"),
                                rs.getString("D1"), rs.getString("D2"), rs.getString("D3")
                        ));
            }
        } catch (SQLException ex) {
            System.out.println("SQL Error " + ex.getLocalizedMessage() + " " + ex.getSQLState());
        }
        return setscores;
    }

    public Score getTeamStats() {
        Score s = new Score();
        SetScore sc = new SetScore();
        try {
            List<Match> match1 = new ArrayList<>();
            getMatchs().stream().filter(m -> m.isStatus()).forEach(m -> match1.add(m));

            int size = match1.size() - 1;
            selectQuery = "SELECT * FROM Score WHERE Id=?";
            PreparedStatement prep = conn.prepareStatement(selectQuery);
            prep.setInt(1, match1.get(size).getScore_id().getId());
            rs = prep.executeQuery();
            while (rs.next()) {
                s.setId(rs.getInt("Id"));
                sc.setId(rs.getInt("Set_Id"));
                s.setSetScore_ids(sc);
            }
        } catch (SQLException ex) {
            System.out.println("SQL Error " + ex.getLocalizedMessage() + " " + ex.getSQLState());
        }
        return s;
    }

    public List<String> getPlayerWithTeam(String team) {
        List<String> playernames = new ArrayList<>();
        try {
            selectQuery = "SELECT * FROM Player WHERE Team_Name=?";
            PreparedStatement prep = conn.prepareStatement(selectQuery);
            prep.setString(1, team);
            rs = prep.executeQuery();
            while (rs.next()) {
                playernames.add(rs.getString("Player_Name"));
            }
        } catch (SQLException ex) {
            System.out.println("SQL Error " + ex.getLocalizedMessage() + " " + ex.getSQLState());
        }
        return playernames;
    }

    /* Creaction of Tables in the Database */
    protected final void createTeam() {
        try {
            String query = CONSTANT.TABLE_TEAM;
            PreparedStatement prep = conn.prepareStatement(query);
            prep.execute();
        } catch (SQLException ex) {
            System.out.println("SQL Error " + ex.getLocalizedMessage() + " " + ex.getSQLState());
        }
    }

    protected final void createPlayer() {
        try {
            String query = CONSTANT.TABLE_PLAYER;
            PreparedStatement prep = conn.prepareStatement(query);
            prep.execute();
        } catch (SQLException ex) {
            System.out.println("SQL Error " + ex.getLocalizedMessage() + " " + ex.getSQLState());
        }
    }

    protected final void createMatch() {
        try {
            String query = CONSTANT.TABLE_MATCH;
            PreparedStatement prep = conn.prepareStatement(query);
            prep.execute();
        } catch (SQLException ex) {
            System.out.println("SQL Error " + ex.getLocalizedMessage() + " " + ex.getSQLState());
        }
    }

    protected final void createScore() {
        try {
            String query = CONSTANT.TABLE_SCORE;
            PreparedStatement prep = conn.prepareStatement(query);
            prep.execute();
        } catch (SQLException ex) {
            System.out.println("SQL Error " + ex.getLocalizedMessage() + " " + ex.getSQLState());
        }
    }

    protected final void createSetScore() {
        try {
            String query = CONSTANT.TABLE_SETSCORE;
            PreparedStatement prep = conn.prepareStatement(query);
            prep.execute();
        } catch (SQLException ex) {
            System.out.println("SQL Error " + ex.getLocalizedMessage() + " " + ex.getSQLState());
        }
    }

    protected void reset() {
        teams = null;
        players = null;
        scores = null;
        teamHasMatch = null;
        setscores = null;
        matchs = null;
        teams = new ArrayList<>();
        players = new ArrayList<>();
        matchs = new ArrayList<>();
        scores = new ArrayList<>();
        setscores = new ArrayList<>();
        teamHasMatch = new ArrayList<>();
    }

    protected int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

}
