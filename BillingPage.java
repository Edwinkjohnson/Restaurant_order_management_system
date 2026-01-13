import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class BillingPage extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JComboBox<String> orderDropdown;
    private JTable itemTable;
    private JLabel totalLabel;
    private JComboBox<String> paymentMethodBox;
    private JButton payButton;

    public BillingPage() {
        setTitle("Billing");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblTitle = new JLabel("Billing Page for Cashier");
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblTitle.setBounds(200, 10, 250, 30);
        add(lblTitle);

        orderDropdown = new JComboBox<>();
        orderDropdown.setBounds(50, 60, 200, 25);
        add(orderDropdown);

        JButton loadButton = new JButton("Load Items");
        loadButton.setBounds(270, 60, 120, 25);
        add(loadButton);

        itemTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(itemTable);
        scrollPane.setBounds(50, 100, 500, 120);
        add(scrollPane);

        totalLabel = new JLabel("Total: ₹0.00");
        totalLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        totalLabel.setBounds(50, 230, 200, 25);
        add(totalLabel);

        paymentMethodBox = new JComboBox<>(new String[]{"Cash", "Card", "UPI"});
        paymentMethodBox.setBounds(270, 230, 120, 25);
        add(paymentMethodBox);

        payButton = new JButton("Process Payment");
        payButton.setBounds(420, 230, 130, 25);
        add(payButton);

        loadPendingOrders();

        loadButton.addActionListener(e -> {
            String selected = (String) orderDropdown.getSelectedItem(); // e.g., "Order #1"
            if (selected != null) {
                try {
                    int orderId = Integer.parseInt(selected.replace("Order #", ""));
                    loadOrderItems(orderId);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid order format.");
                }
            }
        });

        payButton.addActionListener(e -> {
            String selected = (String) orderDropdown.getSelectedItem();
            if (selected != null) {
                try {
                    int orderId = Integer.parseInt(selected.replace("Order #", ""));
                    String method = paymentMethodBox.getSelectedItem().toString();
                    processPayment(orderId, method);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid order format.");
                }
            }
        });
    }

    private void loadPendingOrders() {
        try (Connection conn = DB.DBconnection.getConnection()) {
            String query = "SELECT order_id FROM order_master WHERE status = 'Pending'";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                orderDropdown.addItem("Order #" + rs.getInt("order_id"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading orders: " + ex.getMessage());
        }
    }

    private void loadOrderItems(int orderId) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Item");
        model.addColumn("Qty");
        model.addColumn("Price");

        double total = 0;

        try (Connection conn = DB.DBconnection.getConnection()) {
            String query = "SELECT m.item_name, oi.quantity, oi.price " +
                           "FROM order_items oi " +
                           "JOIN menu m ON oi.item_id = m.id " +
                           "WHERE oi.order_id = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, orderId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String item = rs.getString("item_name");
                int qty = rs.getInt("quantity");
                double price = rs.getDouble("price");
                model.addRow(new Object[]{item, qty, price});
                total += qty * price;
            }
            itemTable.setModel(model);
            totalLabel.setText("Total: ₹" + String.format("%.2f", total));
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading items: " + ex.getMessage());
        }
    }


    private void processPayment(int orderId, String method) {
        try (Connection conn = DB.DBconnection.getConnection()) {
            // Get total from order_master
            String totalQuery = "SELECT total_amount FROM order_master WHERE order_id = ?";
            PreparedStatement pstTotal = conn.prepareStatement(totalQuery);
            pstTotal.setInt(1, orderId);
            ResultSet rs = pstTotal.executeQuery();
            double amount = 0;
            if (rs.next()) {
                amount = rs.getDouble("total_amount");
            }

            // Insert into payments
            String insertQuery = "INSERT INTO payments (order_id, cashier_username, amount_paid, payment_method) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(insertQuery);
            pst.setInt(1, orderId);
            pst.setString(2, "reno"); // Replace with dynamic cashier username if available
            pst.setDouble(3, amount);
            pst.setString(4, method);
            pst.executeUpdate();

            // Update order status
            String updateQuery = "UPDATE order_master SET status = 'Paid' WHERE order_id = ?";
            PreparedStatement pstUpdate = conn.prepareStatement(updateQuery);
            pstUpdate.setInt(1, orderId);
            pstUpdate.executeUpdate();

            JOptionPane.showMessageDialog(this, "Payment successful!");
            dispose();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Payment failed: " + ex.getMessage());
        }
    }
}
