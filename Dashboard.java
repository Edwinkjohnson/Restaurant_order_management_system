import java.awt.*;
import javax.swing.*;

public class Dashboard extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    public Dashboard(String role, String username) {
        setTitle(role + " Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 500, 400);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBackground(new Color(128, 255, 128));
        contentPane.setLayout(null);
        setContentPane(contentPane);

        JLabel lblTitle = new JLabel("Restaurant Management System");
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblTitle.setBounds(100, 20, 300, 25);
        contentPane.add(lblTitle);

        JLabel lblWelcome = new JLabel("Welcome " + role );
        lblWelcome.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblWelcome.setBounds(140, 50, 300, 20);
        contentPane.add(lblWelcome);

        JButton btnMenu = new JButton("Manage Menu");
        btnMenu.setBounds(160, 90, 160, 30);
        btnMenu.setToolTipText("Add or update menu items");
        contentPane.add(btnMenu);

        JButton btnOrder = new JButton("Take Order");
        btnOrder.setBounds(160, 130, 160, 30);
        btnOrder.setToolTipText("Place new customer orders");
        contentPane.add(btnOrder);

        JButton btnViewOrders = new JButton("View Orders");
        btnViewOrders.setBounds(160, 170, 160, 30);
        btnViewOrders.setToolTipText("Check order status and history");
        contentPane.add(btnViewOrders);

        JButton btnUsers = new JButton("Manage Users");
        btnUsers.setBounds(160, 210, 160, 30);
        btnUsers.setToolTipText("Manage staff and admin accounts");
        contentPane.add(btnUsers);

        JButton btnBilling = new JButton("Process Billing");
        btnBilling.setBounds(160, 250, 160, 30);
        btnBilling.setToolTipText("Handle customer payments and generate bills");
        contentPane.add(btnBilling);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBounds(160, 290, 160, 30);
        btnLogout.setToolTipText("Log out and return to login screen");
        contentPane.add(btnLogout);

        // Role-based visibility
        btnUsers.setVisible("Admin".equalsIgnoreCase(role));
        btnBilling.setVisible("Cashier".equalsIgnoreCase(role));

        // Navigation actions
        btnMenu.addActionListener(e -> new MenuManagement().setVisible(true));
        btnOrder.addActionListener(e -> new OrderPage().setVisible(true));
        btnViewOrders.addActionListener(e -> new ViewOrders().setVisible(true));
        btnUsers.addActionListener(e -> new UserManagement().setVisible(true));
        btnBilling.addActionListener(e -> new BillingPage().setVisible(true));

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new Login_Page().setVisible(true);
            }
        });
    }
}
