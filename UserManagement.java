import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import DB.DBconnection;

public class UserManagement extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txtFullname, txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;
    private JTable table;
    private DefaultTableModel model;

    public UserManagement() {
        setTitle("Manage Users");
        setBounds(100, 100, 700, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== Top Panel (Form) =====
        JPanel panelTop = new JPanel(new GridLayout(5, 2, 5, 5));
        panelTop.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelTop.add(new JLabel("Full Name:"));
        txtFullname = new JTextField();
        panelTop.add(txtFullname);

        panelTop.add(new JLabel("Username:"));
        txtUsername = new JTextField();
        panelTop.add(txtUsername);

        panelTop.add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        panelTop.add(txtPassword);

        panelTop.add(new JLabel("Role:"));
        cmbRole = new JComboBox<>(new String[]{"Admin", "Staff"});
        panelTop.add(cmbRole);

        JButton btnAdd = new JButton("Add User");
        panelTop.add(btnAdd);

        JButton btnDelete = new JButton("Delete User");
        panelTop.add(btnDelete);

        add(panelTop, BorderLayout.NORTH);

        // ===== Table =====
        model = new DefaultTableModel(new String[]{"ID", "Full Name", "Username", "Role"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== Load Existing Users =====
        loadUsers();

        // ===== Button Actions =====
        btnAdd.addActionListener(e -> addUser());
        btnDelete.addActionListener(e -> deleteUser());
    }

    private void loadUsers() {
        model.setRowCount(0); // clear table
        try (Connection con = DBconnection.getConnection()) {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT id, fullname, username, role FROM users");
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("fullname"),
                        rs.getString("username"),
                        rs.getString("role")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage());
        }
    }

    private void addUser() {
        String fullname = txtFullname.getText().trim();
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String role = cmbRole.getSelectedItem().toString();

        if (fullname.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        try (Connection con = DBconnection.getConnection()) {
            String sql = "INSERT INTO users(fullname, username, password, role) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, fullname);
            pst.setString(2, username);
            pst.setString(3, password); // plain text for now
            pst.setString(4, role);

            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "User Added Successfully!");
            loadUsers();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteUser() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.");
            return;
        }

        int id = (int) model.getValueAt(row, 0);

        try (Connection con = DBconnection.getConnection()) {
            String sql = "DELETE FROM users WHERE id=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "User Deleted Successfully!");
            loadUsers();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    // 🔹 MAIN METHOD FOR TESTING
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new UserManagement().setVisible(true);
        });
    }
}
