import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import DB.DBconnection;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class Registration extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Registration frame = new Registration();
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
	public Registration() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(128, 255, 128));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("       STAFF REGISTRATION");
		lblNewLabel.setBounds(136, 10, 164, 15);
		lblNewLabel.setFont(new Font("Tw Cen MT", Font.BOLD, 13));
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("FULL NAME");
		lblNewLabel_1.setBounds(53, 57, 119, 13);
		contentPane.add(lblNewLabel_1);
		
		textField = new JTextField();
		textField.setBounds(229, 54, 96, 19);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("USERNAME");
		lblNewLabel_2.setBounds(53, 96, 119, 13);
		contentPane.add(lblNewLabel_2);
		
		textField_1 = new JTextField();
		textField_1.setBounds(229, 93, 96, 19);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		
		JLabel lblNewLabel_3 = new JLabel("PASSWORD");
		lblNewLabel_3.setBounds(53, 133, 119, 13);
		contentPane.add(lblNewLabel_3);
		
		textField_2 = new JTextField();
		textField_2.setBounds(229, 130, 96, 19);
		contentPane.add(textField_2);
		textField_2.setColumns(10);
		
		JLabel lblNewLabel_4 = new JLabel("ROLE");
		lblNewLabel_4.setBounds(53, 167, 119, 13);
		contentPane.add(lblNewLabel_4);
		
		JButton btnNewButton = new JButton("REGISTER");
		btnNewButton.setBackground(new Color(128, 255, 255));
		btnNewButton.setBounds(149, 209, 109, 21);
		contentPane.add(btnNewButton);
		
		JComboBox comboBox = new JComboBox<Object>();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"Admin", "Staff", "Chef", "Cashier", "Manager"}));
		comboBox.setEditable(true);
		comboBox.setBounds(229, 163, 96, 21);
		contentPane.add(comboBox);
		
		btnNewButton.addActionListener(e -> {
		    String fullname = textField.getText();
		    String username = textField_1.getText();
		    String password = textField_2.getText();
		    String role = comboBox.getSelectedItem().toString();

		    try (Connection conn = DBconnection.getConnection()) {
		        String query = "INSERT INTO users (fullname, username, password, role) VALUES (?, ?, ?, ?)";
		        PreparedStatement pst = conn.prepareStatement(query);
		        pst.setString(1, fullname);
		        pst.setString(2, username);
		        pst.setString(3, password);
		        pst.setString(4, role);
		        pst.executeUpdate();

		        JOptionPane.showMessageDialog(null, "Registration successful!");
		        dispose(); // Close registration window
		        new Login_Page().setVisible(true); // Optional: redirect to login
		    } catch (SQLException ex) {
		        ex.printStackTrace();
		        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
		    }
		});

	}
}
