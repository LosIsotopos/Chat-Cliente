package frames;

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import cliente.Cliente;
import cliente.EscuchaServer;
import mensajeria.Comando;
import mensajeria.Paquete;
import mensajeria.PaqueteUsuario;

public class VentanaContactos extends JFrame {
	private String user = null;
	private Cliente cliente;
	private PaqueteUsuario paqueteUsuario;
	
	private JPanel contentPane;
	private DefaultListModel<String> modelo = new DefaultListModel<String>();
	private static JList<String> list = new JList<String>();
	private JTextField jTFMiNombre;
	private static JLabel lblNumeroConectados = new JLabel("");
	private static JButton botonMc;
	private JButton botonConectar;

	private String ipScanned = "localhost";
	private int puertoScanned = 9999;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VentanaContactos frame = new VentanaContactos();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public VentanaContactos() {

		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 327, 335);
		setLocationRelativeTo(null);
		
		JTextField ip = new JTextField(5);
		JTextField puerto = new JTextField(5);
		
		ip.setText(ipScanned);
		puerto.setText(String.valueOf(puertoScanned));
		
		JPanel myPanel = new JPanel();
		myPanel.setLayout(new GridLayout(2,2));
		myPanel.add(new JLabel("IP: "));
		myPanel.add(ip);
		myPanel.add(new JLabel("PUERTO: "));
		myPanel.add(puerto);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				if (abrirVentanaConfirmaSalir()) {
					if (cliente != null) {
						synchronized (cliente) {
							// Desconectar para que aparezca que tal usuario deslogeo
							cliente.setAccion(Comando.DESCONECTAR);
							cliente.notify();
						}
						setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					}
					System.exit(0);
				}
			}
		});
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 299, 188);
		contentPane.add(scrollPane);

		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getClickCount() == 2) {
					if(list.getSelectedValue() != null) {
						if(!cliente.getChatsActivos().containsKey(list.getSelectedValue())) {
							if (cliente != null) {
								MiChat chat = new MiChat(cliente);
								cliente.getChatsActivos().put(list.getSelectedValue(), chat);
								chat.setTitle(list.getSelectedValue());
								chat.setVisible(true);
							}	
						}
					}
				}
			}
		});

		botonMc = new JButton("Multichat");
		botonMc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(Integer.valueOf(lblNumeroConectados.getText()) != 0) {
					if(!cliente.getChatsActivos().containsKey("Sala")) {
						MiChat chat = new MiChat(cliente);
						cliente.getChatsActivos().put("Sala", chat);
						chat.setTitle("Sala");
						chat.setVisible(true);
						botonMc.setEnabled(false);
					}
				}
			}
		});
		botonMc.setBounds(220, 264, 89, 23);
		contentPane.add(botonMc);
		
		jTFMiNombre = new JTextField();
		jTFMiNombre.setHorizontalAlignment(SwingConstants.LEFT);
		jTFMiNombre.setEditable(false);
		jTFMiNombre.setBounds(67, 209, 242, 22);
		contentPane.add(jTFMiNombre);
		jTFMiNombre.setColumns(10);

		list.setModel(modelo);
		scrollPane.setViewportView(list);

		botonConectar = new JButton("Conectar");
		botonConectar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (user == null) {
					int result = JOptionPane.showConfirmDialog(null, myPanel, 
				               "Please Enter Values", JOptionPane.OK_CANCEL_OPTION);
					if (result == JOptionPane.OK_OPTION) {
						ipScanned = ip.getText();
						puertoScanned = Integer.valueOf(puerto.getText());
						InterfaceLogeo interfaceLogeo = new InterfaceLogeo();
						interfaceLogeo.setTitle("Logeo");
						interfaceLogeo.setVisible(true);
						interfaceLogeo.addWindowListener(new WindowAdapter() {
							@Override
							public void windowClosed(WindowEvent e) {
								user = interfaceLogeo.getNombreUsuario();
								if (user != null) {
									cliente = new Cliente(ipScanned, puertoScanned);
									cliente.start();
									
			                        while(cliente.getState() != Thread.State.WAITING) {	
			                        }
									logIn(cliente);		
									EscuchaServer em = new EscuchaServer(cliente);
									em.start();
									
									synchronized (this) {
										try {
											this.wait(200);
										} catch (InterruptedException e1) {
											e1.printStackTrace();
										}
									}
									
									if(cliente.getPaqueteUsuario().getMensaje().equals(Paquete.msjExito)) {
										setTitle("User: " + user);
										jTFMiNombre.setText(user);
										actualizarLista(cliente);
										botonConectar.setEnabled(false);
									} else {
										try {
											cliente.getSalida().close();
											cliente.getEntrada().close();
											cliente.getSocket().close();
											cliente.stop();
											user = null;
										} catch (IOException e1) {
											e1.printStackTrace();
										}
									}	
								}
							}
						});
					}
				}
			}
		});
		botonConectar.setBounds(10, 264, 89, 23);
		contentPane.add(botonConectar);

		JLabel lblUsuariosConectados = new JLabel("Usuarios Conectados:");
		lblUsuariosConectados.setBounds(10, 235, 138, 16);
		contentPane.add(lblUsuariosConectados);

		
		lblNumeroConectados.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNumeroConectados.setBounds(253, 235, 56, 16);
		contentPane.add(lblNumeroConectados);
		lblNumeroConectados.setText(String.valueOf(modelo.getSize()));

		JLabel lblMiUser = new JLabel("Mi User: ");
		lblMiUser.setBounds(10, 212, 56, 16);
		contentPane.add(lblMiUser);

		JLabel label = new JLabel("");
		label.setBounds(130, 267, 56, 16);
		contentPane.add(label);
	}

	private boolean abrirVentanaConfirmaSalir() {
		int opcion = JOptionPane.showConfirmDialog(this, "¿Desea salir del Chat?", "Confirmación",
				JOptionPane.YES_NO_OPTION);
		if (opcion == JOptionPane.YES_OPTION) {
			return true;
		}
		return false;
	}

	private void logIn(final Cliente cliente) {
		cliente.setAccion(Comando.INICIOSESION);
		cliente.getPaqueteUsuario().setUsername(user);
		synchronized (cliente) {
			cliente.notify();
		}
	}

	private void actualizarLista(final Cliente cliente) {
		if(cliente != null) {
			synchronized (cliente) {
				modelo.removeAllElements();
				if (cliente.getPaqueteUsuario().getListaDeConectados() != null) {
					cliente.getPaqueteUsuario().getListaDeConectados().remove(cliente.getPaqueteUsuario().getUsername());
					for (String cad : cliente.getPaqueteUsuario().getListaDeConectados()) {
						modelo.addElement(cad);
					}
					lblNumeroConectados.setText(String.valueOf(modelo.getSize()));
					list.setModel(modelo);
				}
			}
		}
	}
	
	public PaqueteUsuario getPaqueteUsuario() {
		return paqueteUsuario;
	}
	
	public static JLabel getLblNumeroConectados() {
		return lblNumeroConectados;
	}
	
	public static JList<String> getList() {
		return list;
	}
	
	public static JButton getBotonMc() {
		return botonMc;
	}
}
