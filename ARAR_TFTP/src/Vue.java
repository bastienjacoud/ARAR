
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import java.awt.Font;


/**
 * Created by Brandon on 18/05/2017.
 */


public class Vue extends JFrame implements ActionListener {
    private static final long serialVersionUID = -2279311826638729930L;

    private JPanel contentPane;
    private JPanel receivePanel;
    private JPanel sendPanel;
    private JTabbedPane ongletPanel;

    private JTextField txtAdresse;
    private JTextField txtPort;
    private JTextField txtSendLocal;
    private JTextField txtSendPath;
    private JTextField txtSendDistant;
    private JTextField txtReceiveLocal;
    private JTextField txtReceivePath;
    private JTextField txtReceiveDistant;
    private JTextArea  txtInfoArea;

    public JTextArea getTxtInfoArea() {
        return txtInfoArea;
    }

    private JButton btnReceiveFile;
    private JButton btnSendFile;
    private JButton btnFindSend;
    private JButton btnFindReceive;

    private JMenuBar menuBar;
    private JMenuItem mnQuitter;

    private Client client;

    private String distant;
    private String path;
    private String local;

    public Vue() {

        setType(Type.UTILITY);
        setTitle("Client STF");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBounds(100, 100, 469, 485);

        chargerForm();
        client = new Client(this);
    }

    public void chargerForm() {
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        mnQuitter = new JMenuItem("Quitter");
        mnQuitter.addActionListener(this);
        menuBar.add(mnQuitter);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        setContentPane(contentPane);

        ongletPanel = new JTabbedPane(JTabbedPane.TOP);
        ongletPanel.setBounds(6, 43, 452, 223);
        contentPane.add(ongletPanel);

        sendPanel = new JPanel();
        ongletPanel.addTab("Send", null, sendPanel, null);
        sendPanel.setLayout(null);

        JLabel lblNomLocal = new JLabel("Fichier local");
        lblNomLocal.setBounds(17, 28, 85, 14);
        sendPanel.add(lblNomLocal);

        txtSendLocal = new JTextField();
        txtSendLocal.setBounds(105, 25, 227, 20);
        txtSendLocal.setColumns(10);
        txtSendLocal.setEditable(false);
        sendPanel.add(txtSendLocal);

        JLabel lblNomFichierDistant = new JLabel("Nom fichier distant");
        lblNomFichierDistant.setBounds(17, 94, 124, 14);
        sendPanel.add(lblNomFichierDistant);

        btnFindSend = new JButton("Find");
        btnFindSend.setBounds(344, 25, 59, 20);
        btnFindSend.addActionListener(this);
        sendPanel.add(btnFindSend);

        txtSendDistant = new JTextField();
        txtSendDistant.setBounds(153, 91, 254, 20);
        sendPanel.add(txtSendDistant);
        txtSendDistant.setColumns(10);

        btnSendFile = new JButton("Send");
        btnSendFile.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
        btnSendFile.setBounds(126, 123, 171, 39);
        sendPanel.add(btnSendFile);

        JLabel lblPath = new JLabel("Path");
        lblPath.setBounds(17, 60, 85, 14);
        sendPanel.add(lblPath);

        txtSendPath = new JTextField();
        txtSendPath.setColumns(10);
        txtSendPath.setEditable(false);
        txtSendPath.setBounds(105, 57, 302, 20);
        sendPanel.add(txtSendPath);
        btnSendFile.addActionListener(this);

        receivePanel = new JPanel();
        ongletPanel.addTab("Receive", null, receivePanel, null);
        receivePanel.setLayout(null);

        btnReceiveFile = new JButton("Receive");
        btnReceiveFile.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
        btnReceiveFile.setBounds(126, 123, 171, 39);
        btnReceiveFile.addActionListener(this);
        receivePanel.add(btnReceiveFile);

        JLabel lblNomFichierLocal = new JLabel("Nom fichier local");
        lblNomFichierLocal.setBounds(17, 94, 113, 14);
        receivePanel.add(lblNomFichierLocal);

        txtReceiveLocal = new JTextField();
        txtReceiveLocal.setColumns(10);
        txtReceiveLocal.setBounds(147, 91, 260, 20);
        receivePanel.add(txtReceiveLocal);

        JLabel lblFichierDistant = new JLabel("Fichier distant");
        lblFichierDistant.setBounds(17, 28, 96, 14);
        receivePanel.add(lblFichierDistant);

        btnFindReceive = new JButton("Find");
        btnFindReceive.setBounds(348, 59, 59, 20);
        btnFindReceive.addActionListener(this);
        receivePanel.add(btnFindReceive);

        txtReceiveDistant = new JTextField();
        txtReceiveDistant.setColumns(10);
        txtReceiveDistant.setBounds(130, 25, 280, 20);
        receivePanel.add(txtReceiveDistant);

        JLabel lblPathLocal = new JLabel("Path local");
        lblPathLocal.setBounds(17, 62, 67, 14);
        receivePanel.add(lblPathLocal);

        txtReceivePath = new JTextField();
        txtReceivePath.setColumns(10);
        txtReceivePath.setBounds(96, 59, 243, 20);
        receivePanel.add(txtReceivePath);

        JLabel lblAdresse = new JLabel("Adresse");
        lblAdresse.setBounds(31, 17, 59, 14);
        contentPane.add(lblAdresse);

        txtAdresse = new JTextField();
        txtAdresse.setBounds(102, 14, 124, 20);
        contentPane.add(txtAdresse);
        txtAdresse.setText("127.0.0.1");
        txtAdresse.setColumns(10);

        JLabel lblPort = new JLabel("Port");
        lblPort.setBounds(288, 17, 37, 14);
        contentPane.add(lblPort);

        txtPort = new JTextField();
        txtPort.setBounds(332, 14, 89, 20);
        txtPort.setEditable(false);
        txtPort.setText("69");
        txtPort.setColumns(10);
        contentPane.add(txtPort);

        JPanel infoPanel = new JPanel();
        infoPanel.setBounds(14, 270, 436, 130);
        infoPanel.setLayout(new BorderLayout());

        txtInfoArea = new JTextArea();
        txtInfoArea.setEditable(false);
        txtInfoArea.setWrapStyleWord(true);
        txtInfoArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(txtInfoArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setViewportView(txtInfoArea);
        infoPanel.add(scrollPane, BorderLayout.CENTER);

        contentPane.add(infoPanel);

        String monAdresse = "Inconnue";
        try { monAdresse = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) { e.printStackTrace(); }
        JLabel lblMonAdr = new JLabel("Mon adresse IP : "+monAdresse);
        lblMonAdr.setBounds(24, 412, 285, 16);
        contentPane.add(lblMonAdr);


    }


    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == this.mnQuitter) {
            System.exit(0);
        }

        //Onglet Send, récupérer un fichier
        if (e.getSource() == this.btnFindSend) {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Choisir un fichier");
            int returnVal = chooser.showOpenDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                txtSendLocal.setText(chooser.getSelectedFile().getName());
                txtSendPath.setText(chooser.getCurrentDirectory().toPath().toString());
            }
        }

        //Onglet Receive, récupérer un dossier
        if (e.getSource() == this.btnFindReceive) {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Choisir un dossier");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            int returnVal = chooser.showOpenDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                txtReceivePath.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        }

        //Vérification de l'adresse IP
        if(!txtAdresse.getText().matches(
                "(([0-1]?[0-9]{1,2}\\.)|(2[0-4][0-9]\\.)|(25[0-5]\\.)){3}(([0-1]?[0-9]{1,2})|(2[0-4][0-9])|(25[0-5]))")
                || txtAdresse.getText().isEmpty())
        {
            JOptionPane.showMessageDialog(null, "L'adresse Ip n'est pas correcte");
            return;
        }

        //Vérification du port
        if(txtPort.getText().isEmpty())
        {
            JOptionPane.showMessageDialog(null, "Le port est vide");
            return;
        }
        else {
            try{
                Integer.parseInt(txtPort.getText());
            }catch(NumberFormatException e1){
                JOptionPane.showMessageDialog(null, "Le port est invalide");
            }
        }

        //Clique sur le bouton Send
        if (e.getSource() == this.btnSendFile) {
            //Vérification du fichier local
            if(txtSendLocal.getText().isEmpty())
            {
                JOptionPane.showMessageDialog(null, "Le nom local est vide");
                return;
            }
            //Vérification du champ de renommage distant
            distant = txtSendLocal.getText();
            if(!txtSendDistant.getText().isEmpty())
                distant = txtSendDistant.getText();

            new Thread(new Runnable() {
                public void run() {
                    int Crem = client.sendFile(txtSendPath.getText(), txtSendLocal.getText(), distant, txtAdresse.getText(), Integer.parseInt(txtPort.getText()));
                    txtInfoArea.append("Crem : "+Crem+"\n\n");
                }
            }).start();

        }
        //Clique sur le bouton Receive
        else if (e.getSource() == this.btnReceiveFile) {
            //Vérification du fichier distant
            if(txtReceiveDistant.getText().isEmpty())
            {
                JOptionPane.showMessageDialog(null, "Le nom distant est vide");
                return;
            }
            //Vérification du champ de renommage local
            local = txtReceiveDistant.getText();
            if(!txtReceiveLocal.getText().isEmpty())
                local = txtReceiveLocal.getText();

            //Vérification du path d'enregistrement
            path = txtReceivePath.getText();
            if(!txtReceivePath.getText().isEmpty())
                path += "/";
            new Thread(new Runnable() {
                public void run() {
                    int Crrv = client.receiveFile(txtReceiveDistant.getText(), path, local, txtAdresse.getText(), Integer.parseInt(txtPort.getText()));
                    txtInfoArea.append("Crrv : "+Crrv+"\n\n");
                }
            }).start();
        }
    }
}

