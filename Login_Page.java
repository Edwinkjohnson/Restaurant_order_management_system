import java.awt.*;
import javax.swing.*;

import DB.DBconnection;

import java.sql.*;

public class Login_Page extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
    private JTextField textFieldUsername;
    private JPasswordField passwordField;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Login_Page frame = new Login_Page();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Login_Page() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 400, 250);
        contentPane = new JPanel();
        contentPane.setBackground(new Color(173, 216, 230));
        contentPane.setLayout(null);
        setContentPane(contentPane);

        JLabel lblTitle = new JLabel("USER LOGIN");
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblTitle.setBounds(140, 20, 120, 25);
        contentPane.add(lblTitle);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setBounds(50, 70, 80, 20);
        contentPane.add(lblUsername);

        textFieldUsername = new JTextField();
        textFieldUsername.setBounds(150, 70, 150, 20);
        contentPane.add(textFieldUsername);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(50, 110, 80, 20);
        contentPane.add(lblPassword);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 110, 150, 20);
        contentPane.add(passwordField);

        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(150, 150, 100, 25);
        contentPane.add(btnLogin);

        // 🔑 Login button action
        btnLogin.addActionListener(e -> {
            String username = textFieldUsername.getText();
            String password = new String(passwordField.getPassword());

            try (Connection conn = DBconnection.getConnection()) {
                String query = "SELECT * FROM users WHERE username=? AND password=?";
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setString(1, username);
                pst.setString(2, password);
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    String role = rs.getString("role");
                    new Dashboard(role, role).setVisible(true);

                    // 👉 Redirect based on role
                    if ("Admin".equalsIgnoreCase(role)) {
                        // open Admin dashboard
                    } else if ("Staff".equalsIgnoreCase(role)) {
                        // open Staff dashboard
                    }
                    dispose(); // close login window
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid Username or Password");
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Database Error: " + ex.getMessage());
            }
        });
    }
}
