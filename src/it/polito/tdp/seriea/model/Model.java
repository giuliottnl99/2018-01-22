package it.polito.tdp.seriea.model;

import java.util.List;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.seriea.db.SerieADAO;

public class Model {
	SimpleDirectedWeightedGraph<SeasonTeam, DefaultWeightedEdge> grafo;
	SerieADAO dao = new SerieADAO();
	List<SeasonTeam> listaST;
	List<Season> stagioni;
	
	public String pointPerSeason(Team t) {
		stagioni = dao.listAllSeasons(); 
		listaST = dao.listMatchesTeam(t, stagioni);
		String result="";
		for(SeasonTeam st1: listaST) {
			// Aggiungo la vittoria per ogni SeasonTeam
			result+= st1.toString() + " " + st1.punti + "\n";
			
		}
		return result;
		
	}
	
	public void creaGrafo() {
		grafo = new SimpleDirectedWeightedGraph<SeasonTeam, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		Graphs.addAllVertices(this.grafo, listaST);
		// Per ogni vertice prendo gli estremi
		for(SeasonTeam st1: this.grafo.vertexSet()) {
			for(SeasonTeam st2: this.grafo.vertexSet()) {
				if(st1==st2)
					continue;
				// Aggiungo l'edge
				if(st1.punti>st2.punti) {
					DefaultWeightedEdge e = grafo.addEdge(st1, st2);
					if(e!=null) {
						double lungh = st1.punti-st2.punti;
						grafo.setEdgeWeight(e, lungh);
					}
				}
				if(st2.punti>st1.punti) {
					DefaultWeightedEdge e = grafo.addEdge(st2, st1);
					if(e!=null) {
						double lungh = st2.punti-st1.punti;
						grafo.setEdgeWeight(e, lungh);
					}
				}
				
			}
			// Prima creo gli edge
			
		}
		// Ho creato il grafo
		
		
	}
	
	public String getannoOro() {
		SeasonTeam miglioreAttuale=null;
		double migliorSum = -100000;
		for(SeasonTeam v: grafo.vertexSet()) {
			double sum=0;
			List<SeasonTeam> predecessori = Graphs.predecessorListOf(this.grafo, v);
			List<SeasonTeam> successori = Graphs.successorListOf(this.grafo, v);
			// Sommo i predecessori
			for(SeasonTeam pred: predecessori) {
				DefaultWeightedEdge e = this.grafo.getEdge(pred, v);
				sum += grafo.getEdgeWeight(e);
				
				
			}
			for(SeasonTeam succ: successori) {
				DefaultWeightedEdge e = this.grafo.getEdge(v, succ);
				sum -= grafo.getEdgeWeight(e);
				
				
			}
			if(migliorSum<sum) {
				migliorSum=sum;
				miglioreAttuale=v;
				
			}
		}
		return "" + miglioreAttuale.seasonId + " " + migliorSum;
		
	}
}
