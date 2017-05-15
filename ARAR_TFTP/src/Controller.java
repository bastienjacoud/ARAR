import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class Controller {

	@FXML
	private TextField tfAdresse;

	@FXML
	private TextField tfPort;

	@FXML
	private TextField tfPathLocalEnv;

	@FXML
	private TextField tfNomFichierLocalEnv;

	@FXML
	private Button btEnv;

	@FXML
	private TextField tfPathLocalRec;

	@FXML
	private TextField tfNomFichierDistantRec;

	@FXML
	private Button btRec;

	private MainClient mainClient;



}
