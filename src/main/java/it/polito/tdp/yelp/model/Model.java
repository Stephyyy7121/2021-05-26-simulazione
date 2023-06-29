package it.polito.tdp.yelp.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	
	private YelpDao dao ;
	private Graph<BusinessRecensione, DefaultWeightedEdge> grafo;
	private List<BusinessRecensione> vertici;
	
	private Map<String, Business> idMapBusiness;
	
	//ricorsione 
	
	private List<BusinessRecensione> percorso;
	private int numMin;
	private BusinessRecensione source;
	private BusinessRecensione target;
	
	
	
	public Model() {
		
		this.dao  = new YelpDao();
		this.grafo = new SimpleDirectedWeightedGraph<BusinessRecensione, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		this.idMapBusiness = new HashMap<>();
	}
	
	public List<String> getCities() {
		return this.dao.getCity();
	}
	
	public List<Integer> getYear() {
		
		List<Integer> anni = new ArrayList<>();
		
		for (int i = 2005; i <= 2013 ; i ++) {
			anni.add(i);
		}
		
		return anni;
	}
	
	private void idMap() {
		
		List<Business> business = this.dao.getAllBusiness();
		if (this.idMapBusiness.isEmpty()) {
			for (Business b : business) {
				idMapBusiness.put(b.getBusinessId(), b);
			}
		}
	}
	
	public void clearGraph() {
		
		this.grafo = new SimpleDirectedWeightedGraph<BusinessRecensione, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		this.vertici = new ArrayList<>();
	}
	
	public void loadNodes(String city, Integer year) {
		
		idMap();
		if (this.vertici.isEmpty()) {
			this.vertici = new ArrayList<>(this.dao.getVertici(city, year, idMapBusiness));
		}
		
	}
	
	public void creaGrafo(String city, Integer year) {
		
		clearGraph();
		loadNodes(city, year);
		
		Graphs.addAllVertices(this.grafo, this.vertici);
		
		for (BusinessRecensione b1 : this.vertici) {
			for (BusinessRecensione b2 : this.vertici) {
				if (!b1.equals(b2)) {
					double peso = b1.getMediaRecensione() - b2.getMediaRecensione();
					
					if (peso >0) {
						Graphs.addEdgeWithVertices(this.grafo, b1, b2, peso);
					}
					else if (peso < 0 ) {
						Graphs.addEdgeWithVertices(this.grafo, b2, b1, -peso);
					}
					else {
						
					}
					
				}
			}
		}
		
	}
	
	public List<BusinessRecensione> getVertici() {
		return this.vertici;
	}
	
	public int numArchi() {
		return this.grafo.edgeSet().size();
	}
	
	
	public BusinessRecensione localeMigliore() {
		
		BusinessRecensione migliore = null;
		int gradoMigliore = 0;
		for (BusinessRecensione b : this.vertici) {
			int in = 0;
			int out = 0;
			
			for (DefaultWeightedEdge edge : this.grafo.outgoingEdgesOf(b)) {
				
				out += this.grafo.getEdgeWeight(edge); 
			}
			for (DefaultWeightedEdge edge : this.grafo.incomingEdgesOf(b)) {
				in += this.grafo.getEdgeWeight(edge);
			}
			
			int differenza = in - out;
			if (differenza > gradoMigliore) {
				gradoMigliore = differenza;
				migliore = b;
			}
		}
		
		return migliore;
	
	}
	
	public List<BusinessRecensione> getPercorso(BusinessRecensione source, BusinessRecensione target, double x) {
		
		this.source = source;
		this.target = target;
		this.percorso = new ArrayList<>();
		this.numMin = this.vertici.size();
		
		//dominio
		ConnectivityInspector<BusinessRecensione, DefaultWeightedEdge> ci = new ConnectivityInspector<>(grafo);
		Set<BusinessRecensione> connessa = ci.connectedSetOf(this.source);
		List<BusinessRecensione> predecessori = new ArrayList<>(Graphs.predecessorListOf(this.grafo, this.source));
		List<BusinessRecensione> dominiosource = new ArrayList<>(Graphs.successorListOf(this.grafo, this.source));
		
		List<BusinessRecensione> parziale = new ArrayList<>();
		parziale.add(this.source);
		ricorsione(parziale, dominiosource, x);
		
		return this.percorso;
	}

	private void ricorsione(List<BusinessRecensione> parziale, List<BusinessRecensione> dominio, double x) {
		// TODO Auto-generated method stub
		BusinessRecensione currentNodo = parziale.get(parziale.size()-1);
		int currentLenght = parziale.size();
		if (currentNodo.equals(this.target)) {
			if (currentLenght < this.numMin) {
				
				this.percorso = new ArrayList<>(parziale);
				this.numMin = currentLenght;
			}
			return ;
		}
		
		//aggiornamento
		/*if (currentLenght < this.numMin) {
			//this.percorso = new ArrayList<>(parziale);
			this.numMin = currentLenght;
		}*/
		else {
			for (BusinessRecensione b : dominio) {
				double peso = this.grafo.getEdgeWeight(this.grafo.getEdge(currentNodo, b));
				if (!parziale.contains(b)&& peso > x ) {
					parziale.add(b);
					List<BusinessRecensione> predecessori = new ArrayList<>(Graphs.predecessorListOf(this.grafo, b));
					List<BusinessRecensione> currentDominio = Graphs.successorListOf(this.grafo, b);
					//currentDominio.removeAll(parziale);
					ricorsione(parziale, currentDominio, x);
					parziale.remove(parziale.size()-1);
				}
			}
		}
		
		
	}
	
}
