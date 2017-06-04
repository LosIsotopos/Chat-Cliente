package frames;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import javax.swing.ScrollPaneConstants;

public class MiChat extends JFrame {

	private JPanel contentPane;
	private JTextField texto;

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					MiChat frame = new MiChat();
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the frame.
	 */
	public MiChat() {
		setTitle("Mi Chat");
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		setResizable(false);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(10, 11, 414, 201);
		contentPane.add(scrollPane);
		
		JTextArea chat = new JTextArea();
		chat.setEditable(false);
		scrollPane.setViewportView(chat);
		
		texto = new JTextField();
		this.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				texto.requestFocus();
			}
		});
		texto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!texto.getText().equals("")) {
					chat.append(texto.getText() + "\n");
					texto.setText("");
				}
			}
		});
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				mostrarVentanaConfirmacion();
			}
		});
		texto.setBounds(10, 223, 314, 27);
		contentPane.add(texto);
		texto.setColumns(10);
		
		JButton enviar = new JButton("ENVIAR");
		enviar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!texto.getText().equals("")) {
					chat.append(texto.getText() + "\n");
					texto.setText("");
				}
				texto.requestFocus();
			}
		});
		enviar.setBounds(334, 225, 81, 23);
		
		contentPane.add(enviar);
	}
	
	private void mostrarVentanaConfirmacion() {
		int res = JOptionPane.showConfirmDialog(this, "¿Desea salir de la sesión de chat?", "Confirmación", JOptionPane.YES_NO_OPTION);
		if(res == JOptionPane.YES_OPTION)
			dispose();
	}
}
