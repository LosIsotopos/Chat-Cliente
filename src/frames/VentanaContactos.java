package frames;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
import cliente.EscuchaMensajes;
import mensajeria.Comando;
import mensajeria.PaqueteUsuario;

//public class VentanaContactos extends JFrame {
public class VentanaContactos extends JFrame {
	private String user = null;
	private static Cliente cliente;
	private boolean flagConexion = false;
	private PaqueteUsuario paqueteUsuario;
	
	private JPanel contentPane;
	private DefaultListModel<String> modelo = new DefaultListModel<String>();
	private static JList<String> list = new JList<String>();
	private JTextField jTFMiNombre;
	private static JLabel lblNumeroConectados = new JLabel("");

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

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				if (abrirVentanaConfirmaSalir()) {
					if (cliente != null) {
						synchronized (cliente) {
							// Desconectar para que aparezca que tal usuario
							// deslogeo
							cliente.setAccion(Comando.DESCONECTAR);
							cliente.notify();
							// Salir para que aparezca que tal IP deslogeo
							cliente.setAccion(Comando.SALIR);
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
					if (cliente != null) {
						MiChat chat = new MiChat();
						chat.setTitle(list.getSelectedValue());
						chat.setVisible(true);
					}
				}
			}
		});

		jTFMiNombre = new JTextField();
		jTFMiNombre.setHorizontalAlignment(SwingConstants.LEFT);
		jTFMiNombre.setEditable(false);
		jTFMiNombre.setBounds(67, 209, 242, 22);
		contentPane.add(jTFMiNombre);
		jTFMiNombre.setColumns(10);

		list.setModel(modelo);
		scrollPane.setViewportView(list);

		JButton botonConectar = new JButton("Conectar");
		botonConectar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (user == null) {
					InterfaceLogeo interfaceLogeo = new InterfaceLogeo();
					interfaceLogeo.setTitle("Logeo");
					interfaceLogeo.setVisible(true);
					interfaceLogeo.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosed(WindowEvent e) {
							user = interfaceLogeo.getNombreUsuario();
							if (user != null) {
								setTitle("User: " + user);
								jTFMiNombre.setText(user);
								cliente = new Cliente();
								cliente.start();
		                        while(cliente.getState() != Thread.State.WAITING) {	
		                        }
								logIn(cliente);		
								
								actualizarLista(cliente);
								
								EscuchaMensajes em = new EscuchaMensajes(cliente);
								em.start();
								
								botonConectar.setEnabled(false);
							}
						}
					});
				}
			}
		});
		botonConectar.setBounds(10, 264, 89, 23);
		contentPane.add(botonConectar);

		JButton botonMc = new JButton("Multichat");
		botonMc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MiChat chat = new MiChat();
				chat.setTitle("Sala");
				chat.setVisible(true);
			}
		});
		botonMc.setBounds(220, 264, 89, 23);
		contentPane.add(botonMc);

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
		System.out.println(jTFMiNombre.getText());
		cliente.getPaqueteUsuario().setUsername(jTFMiNombre.getText());
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

}
