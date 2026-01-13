import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class HomePage extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				HomePage frame = new HomePage();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public HomePage() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(164, 239, 114));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("HOMEPAGE");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 17));
		lblNewLabel.setBounds(174, 22, 113, 13);
		contentPane.add(lblNewLabel);

		JButton btnRegister = new JButton("Registration");
		btnRegister.setBackground(new Color(174, 113, 187));
		btnRegister.setBounds(162, 84, 113, 21);
		contentPane.add(btnRegister);

		JButton btnLogin = new JButton("LogIn");
		btnLogin.setBackground(new Color(174, 113, 187));
		btnLogin.setBounds(162, 131, 113, 21);
		contentPane.add(btnLogin);
		
		JLabel lblNewLabel_1 = new JLabel("New User :");
		lblNewLabel_1.setBounds(162, 67, 85, 13);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Existing User :");
		lblNewLabel_2.setBounds(162, 115, 113, 13);
		contentPane.add(lblNewLabel_2);

		// Action listeners
		btnRegister.addActionListener(e -> {
			Registration regPage = new Registration();
			regPage.setVisible(true);
			dispose();
		});

		btnLogin.addActionListener(e -> {
			Login_Page loginPage = new Login_Page();
			loginPage.setVisible(true);
			dispose();
		});
	}
}
