package frames;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class InterfaceLogeo extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private String nombreUsuario;

	/**
	 * Create the frame.
	 */
	public InterfaceLogeo() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 281, 126);
		setLocationRelativeTo(null);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnIniciar = new JButton("Iniciar");
		btnIniciar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(!textField.getText().equals("")) {
					nombreUsuario = textField.getText();
				}
				dispose();
			}
		});
		
		btnIniciar.setBounds(12, 45, 97, 25);
		contentPane.add(btnIniciar);
		
		JButton btnCancelar = new JButton("Cancelar");
		btnCancelar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		btnCancelar.setBounds(158, 45, 97, 25);
		contentPane.add(btnCancelar);
		
		textField = new JTextField();
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(!textField.getText().equals("")) {
					nombreUsuario = textField.getText();
				}
				dispose();
			}
		});
		
		textField.setBounds(139, 13, 116, 22);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblNombreDeUsuario = new JLabel("Nombre De Usuario:");
		lblNombreDeUsuario.setBounds(12, 16, 121, 16);
		contentPane.add(lblNombreDeUsuario);
	}

	public String getNombreUsuario() {
		return nombreUsuario;
	}
}
