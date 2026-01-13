import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import DB.DBconnection;

public class ViewOrders extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable table;
    private DefaultTableModel model;

    public ViewOrders() {
        setTitle("View Orders");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 800, 400);
        contentPane = new JPanel();
        contentPane.setBackground(new Color(255, 255, 200));
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        JLabel lblTitle = new JLabel("Orders List", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 18));
        contentPane.add(lblTitle, BorderLayout.NORTH);

        // Table model
        model = new DefaultTableModel(
            new Object[][] {},
            new String[] { "Order ID", "Item Name", "Quantity", "Price", "Total", "Order Time" }
        );
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadOrders());
        contentPane.add(btnRefresh, BorderLayout.SOUTH);

        // Load data initially
        loadOrders();
    }

    private void loadOrders() {
        model.setRowCount(0); // clear table
        try (Connection con = DBconnection.getConnection()) {
            String sql =
                    "SELECT oi.order_id, m.item_name, oi.quantity, oi.price, " +
                            "(oi.quantity * oi.price) AS total, om.order_time " +
                            "FROM order_items oi " +
                            "JOIN menu m ON oi.item_id = m.id " +
                            "JOIN order_master om ON oi.order_id = om.order_id " + // add space here
                            "ORDER BY om.order_time DESC";

            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("order_id"),
                    rs.getString("item_name"),
                    rs.getInt("quantity"),
                    rs.getDouble("price"),
                    rs.getDouble("total"),
                    rs.getTimestamp("order_time")
                };
                model.addRow(row);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading orders: " + ex.getMessage());
        }
    }

    // For standalone testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ViewOrders().setVisible(true);
        });
    }
}
