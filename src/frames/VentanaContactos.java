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
import mensajeria.Comando;
import mensajeria.PaqueteUsuario;

public class VentanaContactos extends JFrame {

	private JPanel contentPane;
	private String user = "";
	Cliente cliente;
	private PaqueteUsuario paqueteUsuario;
	private boolean flagConexion = false;
	private JTextField jTFMiNombre;
	
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
				if(cliente != null) {
					synchronized(cliente){
					cliente.setAccion(Comando.SALIR);
					cliente.notify();
					}
					setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				}
				abrirVentanaConfirmaSalir();
			}
		});
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 299, 188);
		contentPane.add(scrollPane);
		
		
		DefaultListModel<String> modelo = new DefaultListModel<String>();
		modelo.addElement("Pepe");
		modelo.addElement("Pepa");
		modelo.addElement("Papo");
//		for (int i = 0; i < Servidor.getUsuariosConectados().size(); i++) {
//			modelo.addElement(Servidor.getUsuariosConectados().get(i));
//		}
		
		JList<String> list = new JList<String>();
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getClickCount() == 2) {
					MiChat chat = new MiChat();
					chat.setTitle(list.getSelectedValue());
					chat.setVisible(true);
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
				if(user.equals("")) {
					InterfaceLogeo interfaceLogeo = new InterfaceLogeo();
					interfaceLogeo.setTitle("Logeo");
					interfaceLogeo.setVisible(true);
					interfaceLogeo.addWindowListener(new WindowAdapter() {
			        	@Override
			            public void windowClosed(WindowEvent e) {
			        		user = interfaceLogeo.getNombreUsuario();
			                if(user != null) {
			                	setTitle("User: " + user);
			                	jTFMiNombre.setText(user);
			                	cliente = new Cliente();
			                	cliente.start();
			                	logIn(cliente);
			                }
			            }
			        });
				} else {
					JOptionPane.showMessageDialog(null, "Ya has iniciado sesión!");
					botonConectar.setEnabled(false);
				}
			}
		});
		botonConectar.setBounds(10, 264, 89, 23);
		contentPane.add(botonConectar);
		
		JButton botonMc = new JButton("Multichat");
		botonMc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MiChat chat = new MiChat();
				chat.setTitle(list.getSelectedValue());
				chat.setTitle("Sala");
				chat.setVisible(true);
			}
		});
		botonMc.setBounds(220, 264, 89, 23);
		contentPane.add(botonMc);
		
		JLabel lblUsuariosConectados = new JLabel("Usuarios Conectados:");
		lblUsuariosConectados.setBounds(10, 235, 138, 16);
		contentPane.add(lblUsuariosConectados);
		
		JLabel lblNumeroConectados = new JLabel("");
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

	private void abrirVentanaConfirmaSalir() {
		int opcion = JOptionPane.showConfirmDialog(this, "¿Desea salir del Chat?", "Confirmación", JOptionPane.YES_NO_OPTION);
		if(opcion == JOptionPane.YES_OPTION)
			System.exit(0);
	}
	
	private void logIn(Cliente cliente) {
		synchronized(cliente){
			cliente.setAccion(Comando.INICIOSESION);
			System.out.println(jTFMiNombre.getText());
			cliente.getPaqueteUsuario().setUsername(jTFMiNombre.getText());
			cliente.notify();
		}
	}

	public PaqueteUsuario getPaqueteUsuario() {
		return paqueteUsuario;
	}
}
