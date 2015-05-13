package com.blackwhitesoftware.pandalight.ssh;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SerialPanel extends JPanel {

	private final SerialConnection serialCon = new SerialConnection();
	private final ConnectionListener connectionListener = new ConnectionAdapter() {
		@Override
		public void disconnected() {
			addConsoleLine("DISCONNECTED", "black");

			mTestConnectionButton.setAction(mConnectAction);
			
			for (Component c : mConfigPanel.getComponents()) {
				c.setEnabled(false);
			}
			for (Component c : mTestPanel.getComponents()) {
				c.setEnabled(false);
			}
		}
		@Override
		public void connected() {
			addConsoleLine("CONNECTED", "black");

			portNameLabel.setEnabled(false);
			portNameField.setEnabled(false);
			mTestConnectionButton.setAction(mDisconnectAction);
			
			for (Component c : mConfigPanel.getComponents()) {
				c.setEnabled(true);
			}
			for (Component c : mTestPanel.getComponents()) {
				c.setEnabled(true);
			}
		}
	};
	
	private final Action mConnectAction = new AbstractAction("Connect") {
		@Override
		public void actionPerformed(ActionEvent e) {
			String portName = portNameField.getText();
			serialCon.connect(portName);
		}
	};
	private final Action mDisconnectAction = new AbstractAction("Disconnect") {
		@Override
		public void actionPerformed(ActionEvent e) {
			serialCon.disconnect();
		}
	};
	
	private JPanel connectionPanel;
	private JLabel portNameLabel;
	private JTextField portNameField;
	private JButton mTestConnectionButton;
	
	private JPanel mConfigPanel;
	private JLabel mUploadConfigLabel;
	private JButton mUploadConfigButton;
	
	private JPanel mTestPanel;
	private JButton mVersionButton;
	private JLabel mVersionLabel;
	
	private JPanel mConsolePanel;
	private StringBuffer mConsoleBuffer = new StringBuffer("<html><body><font face=\"Courier\" size=\"2\">");
	private JEditorPane mConsolePane;
	
	public SerialPanel() {
		super();
		
		initialise();
		
		for (Component c : mConfigPanel.getComponents()) {
			c.setEnabled(false);
		}
		for (Component c : mTestPanel.getComponents()) {
			c.setEnabled(false);
		}
		serialCon.addConnectionListener(connectionListener);
	}
	
	private void initialise() {
		setLayout(new BorderLayout());
		
		JPanel mCentralPanel = new JPanel();
		mCentralPanel.setLayout(new BoxLayout(mCentralPanel, BoxLayout.PAGE_AXIS));
		mCentralPanel.add(getConnectionPanel());
		mCentralPanel.add(getConfigPanel());
		mCentralPanel.add(getTestPanel());
		
		add(mCentralPanel, BorderLayout.CENTER);
		add(getConsolePanel(), BorderLayout.SOUTH);
	}
	
	private JPanel getConnectionPanel() {
		if (connectionPanel == null) {
			connectionPanel = new JPanel();
			connectionPanel.setLayout(new GridLayout(0, 2));
			connectionPanel.setBorder(BorderFactory.createTitledBorder("Serial Connection"));

			portNameLabel = new JLabel("port name: ");
			connectionPanel.add(portNameLabel);

			//TODO replace text input with dropdown list of possible COM ports
			portNameField = new JTextField("COM1");
			connectionPanel.add(portNameField);

			connectionPanel.add(new JLabel(""));
			
			mTestConnectionButton = new JButton(mConnectAction);
			connectionPanel.add(mTestConnectionButton);
		}
		return connectionPanel;
	}
	
	private JPanel getConfigPanel() {
		if (mConfigPanel == null) {
			mConfigPanel = new JPanel();
			mConfigPanel.setBorder(BorderFactory.createTitledBorder("Installation"));
			mConfigPanel.setLayout(new GridLayout(0, 2, 5, 2));
			
			mUploadConfigButton = new JButton("Upload configuration");
			mUploadConfigButton.setEnabled(false);
			mConfigPanel.add(mUploadConfigButton);
			
			mUploadConfigLabel = new JLabel("Upload configuration");
			mUploadConfigLabel.setEnabled(false);
			mConfigPanel.add(mUploadConfigLabel);
		}
		return mConfigPanel;
	}
	
	private JPanel getTestPanel() {
		if (mTestPanel == null) {
			mTestPanel = new JPanel();
			mTestPanel.setBorder(BorderFactory.createTitledBorder("Test"));
			mTestPanel.setLayout(new GridLayout(0, 2, 5, 2));
			
			mVersionButton = new JButton("Get pandaLight version");
			mVersionButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (!serialCon.isConnected()) {
						return;
					}
					ConnectionMessageCollector cmc = new ConnectionMessageCollector();

					//TODO send 'get version command'

					if (cmc.mLines.isEmpty()) {
						mVersionLabel.setText("FAILED");
					} else {
						String versionLine = cmc.mLines.get(0);
						//TODO set version label
					}
				}
			});
			mVersionButton.setEnabled(false);
			mTestPanel.add(mVersionButton);
			
			mVersionLabel = new JLabel("-");
			mVersionLabel.setEnabled(false);
			mVersionLabel.setHorizontalAlignment(JLabel.CENTER);
			mTestPanel.add(mVersionLabel);
		}
		return mTestPanel;
	}
	
	private void addConsoleLine(String pLine, String pColor) {
		if (pColor != null) {
			mConsoleBuffer.append("<font \"color\"=\"" + pColor + "\">");
		}
		mConsoleBuffer.append(pLine);
		if (pColor != null) {
			mConsoleBuffer.append("</font>");
		}
		mConsoleBuffer.append("<br>\n");
		mConsolePane.setText(mConsoleBuffer.toString() + "</font></html></body>");
	}
	
	private JPanel getConsolePanel() {
		if (mConsolePanel == null) {
			mConsolePanel = new JPanel();
			mConsolePanel.setPreferredSize(new Dimension(1024, 200));
			mConsolePanel.setBorder(BorderFactory.createTitledBorder(""));
			mConsolePanel.setLayout(new BorderLayout());
			
			mConsolePane = new JEditorPane("text/html", "");
			mConsolePanel.add(new JScrollPane(mConsolePane), BorderLayout.CENTER);
			
			serialCon.addConnectionListener(mConnectionConsoleListener);
		}
		return mConsolePanel;
	}
	
	private final ConnectionListener mConnectionConsoleListener = new ConnectionAdapter() {
		@Override
		public void addLine(String pLine) {
			addConsoleLine(pLine, "black");
		}
		@Override
		public void addError(String pLine) {
			addConsoleLine(pLine, "red");
		}
	};
	
	public static void main(final String[] pArgs) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(640, 640);
		
		frame.setContentPane(new SerialPanel());
		
		frame.setVisible(true);
	}
}
