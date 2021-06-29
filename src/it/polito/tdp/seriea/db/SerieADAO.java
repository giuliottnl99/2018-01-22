package it.polito.tdp.seriea.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.seriea.model.Match;
import it.polito.tdp.seriea.model.Season;
import it.polito.tdp.seriea.model.SeasonTeam;
import it.polito.tdp.seriea.model.Team;

public class SerieADAO {
	
	public List<SeasonTeam> listMatchesTeam(Team t, List<Season> seasons) {
		String sql = "SELECT * FROM matches WHERE HomeTeam=? "
				+ "OR AwayTeam=? ORDER BY Season";
		List<SeasonTeam> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, t.getTeam());
			st.setString(2, t.getTeam());
			ResultSet res = st.executeQuery();

			int seasonIdSaved=-1;
			int puntiTotali=0;
			while (res.next()) {
				int seasonId = res.getInt("Season");
				if(seasonId!=seasonIdSaved && seasonIdSaved!=-1) {
					// A queste condizioni mi fermo: salvo i punti
					SeasonTeam steam = new SeasonTeam(t.getTeam(), seasonIdSaved, puntiTotali);
					result.add(steam);
					puntiTotali=0;
					
				}
				seasonIdSaved = seasonId;
				// Queste invece le condizioni di continuazione, anche se season cambia
				// Se la squadra ha vinto o perso aggiungo punti
				String t1 = res.getString("HomeTeam");
				String t2 = res.getString("AwayTeam");
				// Quali dei due è il nostro?
				int p1 = res.getInt("FTHG");
				int p2 = res.getInt("FTAG");
				
				if(t1.equals(t.getTeam())) {
					// Se t1 è il team, voglio che vinca
					if(p1>p2)
						puntiTotali+=3;
					if(p1==p2)
						puntiTotali+=1;
				}
				if(t2.equals(t.getTeam())) {
					// Se t1 è il team, voglio che vinca
					if(p2>p1)
						puntiTotali+=3;
					if(p1==p2)
						puntiTotali+=1;
				}
				
				
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public List<Season> listAllSeasons() {
		String sql = "SELECT season, description FROM seasons";
		List<Season> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Season(res.getInt("season"), res.getString("description")));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	

	public List<Team> listTeams() {
		String sql = "SELECT team FROM teams";
		List<Team> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Team(res.getString("team")));
			}

			conn.close();
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}
