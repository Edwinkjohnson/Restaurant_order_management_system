import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import DB.DBconnection;

public class OrderPage extends JFrame {
    private static final long serialVersionUID = 1L;
    private JComboBox<String> cmbMenu;
    private JTextField txtQuantity;
    private JTable table;
    private DefaultTableModel model;

    // Map to store item_name → item_id
    private Map<String, Integer> itemMap = new HashMap<>();

    public OrderPage() {
        setTitle("Take Order");
        setBounds(100, 100, 600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== Top Panel =====
        JPanel panelTop = new JPanel(new GridLayout(3, 2, 5, 5));
        panelTop.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelTop.add(new JLabel("Select Item:"));
        cmbMenu = new JComboBox<>();
        loadMenuItems();
        panelTop.add(cmbMenu);

        panelTop.add(new JLabel("Quantity:"));
        txtQuantity = new JTextField();
        panelTop.add(txtQuantity);

        JButton btnAddOrder = new JButton("Add to Order");
        panelTop.add(btnAddOrder);

        JButton btnSubmitOrder = new JButton("Submit Order");
        panelTop.add(btnSubmitOrder);

        add(panelTop, BorderLayout.NORTH);

        // ===== Table =====
        model = new DefaultTableModel(new String[]{"Item", "Quantity", "Price", "Total"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== Button Actions =====
        btnAddOrder.addActionListener(e -> addToOrder());
        btnSubmitOrder.addActionListener(e -> submitOrder());
    }

    private void loadMenuItems() {
        try (Connection con = DBconnection.getConnection()) {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT id, item_name FROM menu");
            while (rs.next()) {
                String name = rs.getString("item_name");
                int id = rs.getInt("id");
                cmbMenu.addItem(name);
                itemMap.put(name, id);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading menu: " + e.getMessage());
        }
    }

    private void addToOrder() {
        String item = (String) cmbMenu.getSelectedItem();
        int qty;

        try {
            qty = Integer.parseInt(txtQuantity.getText());
            if (qty <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid quantity.");
            return;
        }

        try (Connection con = DBconnection.getConnection()) {
            String sql = "SELECT price FROM menu WHERE item_name=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, item);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                double price = rs.getDouble("price");
                double total = price * qty;
                model.addRow(new Object[]{item, qty, price, total});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding item: " + e.getMessage());
        }
    }

    private void submitOrder() {
        try (Connection con = DBconnection.getConnection()) {
            con.setAutoCommit(false); // Start transaction

            // 1. Calculate total
            double total = 0;
            for (int i = 0; i < model.getRowCount(); i++) {
                total += Double.parseDouble(model.getValueAt(i, 3).toString());
            }

            // 2. Insert into order_master
            String masterSql = "INSERT INTO order_master (total_amount, status) VALUES (?, 'Pending')";
            PreparedStatement pstMaster = con.prepareStatement(masterSql, Statement.RETURN_GENERATED_KEYS);
            pstMaster.setDouble(1, total);
            pstMaster.executeUpdate();

            ResultSet rs = pstMaster.getGeneratedKeys();
            int orderId = -1;
            if (rs.next()) {
                orderId = rs.getInt(1);
            }

            // 3. Insert into order_items
            String itemSql = "INSERT INTO order_items (order_id, item_id, quantity, price) VALUES (?, ?, ?, ?)";
            PreparedStatement pstItems = con.prepareStatement(itemSql);

            for (int i = 0; i < model.getRowCount(); i++) {
                String itemName = model.getValueAt(i, 0).toString();
                int itemId = itemMap.get(itemName);   // 🔹 you already stored item_id in map
                int qty = Integer.parseInt(model.getValueAt(i, 1).toString());
                double price = Double.parseDouble(model.getValueAt(i, 2).toString());

                pstItems.setInt(1, orderId);
                pstItems.setInt(2, itemId);
                pstItems.setInt(3, qty);
                pstItems.setDouble(4, price);
                pstItems.addBatch();
            }


            pstItems.executeBatch();
            con.commit(); // End transaction

            JOptionPane.showMessageDialog(this, "Order Submitted Successfully!");
            model.setRowCount(0); // Clear table
            cmbMenu.setSelectedIndex(0);
            txtQuantity.setText("");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error submitting order: " + e.getMessage());
        }
    }

    // 🔹 MAIN METHOD FOR TESTING
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new OrderPage().setVisible(true);
        });
    }
}
