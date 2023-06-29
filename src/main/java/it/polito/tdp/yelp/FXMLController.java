/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.yelp;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.yelp.model.BusinessRecensione;
import it.polito.tdp.yelp.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnLocaleMigliore"
    private Button btnLocaleMigliore; // Value injected by FXMLLoader

    @FXML // fx:id="btnPercorso"
    private Button btnPercorso; // Value injected by FXMLLoader

    @FXML // fx:id="cmbCitta"
    private ComboBox<String> cmbCitta; // Value injected by FXMLLoader

    @FXML // fx:id="txtX"
    private TextField txtX; // Value injected by FXMLLoader

    @FXML // fx:id="cmbAnno"
    private ComboBox<Integer> cmbAnno; // Value injected by FXMLLoader

    @FXML // fx:id="cmbLocale"
    private ComboBox<BusinessRecensione> cmbLocale; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader
    
    private boolean grafoCreato = false;
    
    private BusinessRecensione localeMigliore;

    @FXML
    void doCalcolaPercorso(ActionEvent event) {
    	
    	txtResult.clear();
    	String input = this.txtX.getText();
    	if (input.compareTo("")==0) {
    		txtResult.appendText("Inserire valore soglia'\n");
    	}
    	
    	Double x = 0.0;
    	try {
    		x = Double.parseDouble(input);
    	}catch (NumberFormatException e ) {
    		txtResult.appendText("Valore inseirot non accettabile\n");
    		return;
    	}
    	BusinessRecensione b = this.cmbLocale.getValue();
    	if (b == null) {
    		txtResult.appendText("Inserire locale\n");
    	}
    	
    	List<BusinessRecensione> percorso = this.model.getPercorso(b, this.localeMigliore, x);
    	txtResult.appendText("Migliore percorso : \n");
    	if (percorso.isEmpty()) {
    		txtResult.appendText("No percorso\n");
    	}
    	for (BusinessRecensione br : percorso) {
    		txtResult.appendText(br.toString()+"\n");
    	}
    	
    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	
    	txtResult.clear();
    	this.cmbLocale.getItems().clear();
    	String city = this.cmbCitta.getValue();
    	if (city == null) {
    		txtResult.appendText("Inserire citta'\n");
    	}
    	Integer year = this.cmbAnno.getValue();
    	if (year == null) {
    		txtResult.appendText("Inserire anno\n");
    	}
    	
    	this.model.creaGrafo(city, year);
    	this.grafoCreato = true;
    	txtResult.appendText("Grafo creato!\n#Vertici: " + this.model.getVertici().size()+"\n#Archi: "+ this.model.numArchi()+"\n");
    	this.cmbLocale.getItems().addAll(this.model.getVertici());

    }

    @FXML
    void doLocaleMigliore(ActionEvent event) {

    	txtResult.clear();
    	if (!grafoCreato) {
    		txtResult.appendText("Non e' stato creato un grafo\n");
    	}
    	
    	BusinessRecensione migliore = this.model.localeMigliore();
    	this.localeMigliore = migliore;
    	txtResult.appendText("LOCALE MIGLIORE = " + migliore);
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnLocaleMigliore != null : "fx:id=\"btnLocaleMigliore\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnPercorso != null : "fx:id=\"btnPercorso\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbCitta != null : "fx:id=\"cmbCitta\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtX != null : "fx:id=\"txtX\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbAnno != null : "fx:id=\"cmbAnno\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbLocale != null : "fx:id=\"cmbLocale\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";
    }
    
    public void setModel(Model model) {
    	this.model = model;
    	this.cmbAnno.getItems().addAll(this.model.getYear());
    	this.cmbCitta.getItems().addAll(this.model.getCities());
    }
}
