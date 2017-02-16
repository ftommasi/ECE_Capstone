package edu.slu.iot.client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import edu.slu.iot.client.Strand;

import javax.swing.JFileChooser;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotTimeoutException;

import javax.swing.JScrollPane;
import java.awt.SystemColor;
import javax.swing.JList;
import javax.swing.AbstractListModel;

public class StrandWindow {

	private Strand currentStrand;
	private JFrame frame;
	private JTextField topicField;
	private File configFile = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e){
			System.out.println("UIManager Error");
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StrandWindow window = new StrandWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public StrandWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 525, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new MigLayout("", "[157.00px,grow][91px][][grow]", "[25.00px][17.00px,grow][grow]"));
		
		JTextPane txtpnChooseAConfiguration = new JTextPane();
		txtpnChooseAConfiguration.setBackground(SystemColor.control);
		txtpnChooseAConfiguration.setText("Choose a configuration file (.conf)");
		frame.getContentPane().add(txtpnChooseAConfiguration, "cell 0 0,alignx left,aligny top");
		
		JButton btnBrowse = new JButton("Browse...");
		btnBrowse.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				JFileChooser configFileChooser = new JFileChooser();
				File workingDirectory = new File(System.getProperty("user.dir"));
				configFileChooser.setCurrentDirectory(workingDirectory);
				frame.getContentPane().add(configFileChooser);
				int chooseStatus = configFileChooser.showOpenDialog(frame);
				if (chooseStatus == JFileChooser.APPROVE_OPTION) {
                    configFile = configFileChooser.getSelectedFile();
				}
			}
		});
		frame.getContentPane().add(btnBrowse, "cell 1 0,alignx center,growy");
		
		topicField = new JTextField();
		frame.getContentPane().add(topicField, "cell 1 1,alignx center,aligny top");
		topicField.setColumns(10);
		
		JButton btnNewButton = new JButton("Connect to topic");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
					try {
						currentStrand =  new Strand(topicField.getText(), configFile);
					} catch (InterruptedException | AWSIotException | AWSIotTimeoutException e) {
						e.printStackTrace();
					}
			}
		});
		frame.getContentPane().add(btnNewButton, "cell 2 0,alignx center,growy");
		
		JButton btnStream = new JButton("Stream");
		frame.getContentPane().add(btnStream, "cell 3 0 1 2,alignx center,aligny top");
		
		JTextPane txtpnEnterTheTopic = new JTextPane();
		txtpnEnterTheTopic.setBackground(SystemColor.control);
		txtpnEnterTheTopic.setText("Enter the topic name");
		frame.getContentPane().add(txtpnEnterTheTopic, "cell 0 1,alignx left,aligny top");
		
		
		JTextPane txtpnStatusNotConnected = new JTextPane();
		txtpnStatusNotConnected.setBackground(SystemColor.control);
		txtpnStatusNotConnected.setText("Status: Not Connected");
		frame.getContentPane().add(txtpnStatusNotConnected, "cell 2 1,alignx center,aligny top");
		
		JScrollPane scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane, "cell 0 2 4 1,grow");
		
		JList list = new JList();
		list.setModel(new AbstractListModel() {
			String[] values = new String[] {"Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage", "Device ID, Session ID, Time Stamp, Voltage"};
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		scrollPane.setViewportView(list);
		
	}
}
