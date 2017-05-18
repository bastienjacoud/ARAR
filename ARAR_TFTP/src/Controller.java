import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

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

	@FXML
	private TextFlow tfRec;

	@FXML
	private TextFlow tfEnv;

	private MainClient _mainClient;

	public Controller()
	{

	}

	@FXML
	private void Initialize()
	{

	}

	public void Envoi()
	{

	}

	public void Reception()
	{
		//String
		//_mainClient.GetClient().receiveFile(fileName, path, localFileName, address, port)
	}

	public void SetMain (MainClient mainClient)
	{
		this._mainClient = mainClient;
		btEnv.setOnMouseClicked(mouseEvent -> Envoi());
		btRec.setOnMouseClicked(mouseEvent -> Reception());
	}



}
