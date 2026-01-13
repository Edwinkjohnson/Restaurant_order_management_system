import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import DB.DBconnection;

public class MenuManagement extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField txtItemName, txtPrice;
    private JComboBox<String> cmbCategory;
    private JTable table;
    private DefaultTableModel model;

    public MenuManagement() {
        setTitle("Manage Menu");
        setBounds(100, 100, 600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== Top Panel (Form) =====
        JPanel panelTop = new JPanel(new GridLayout(4, 2, 5, 5));
        panelTop.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelTop.add(new JLabel("Item Name:"));
        txtItemName = new JTextField();
        panelTop.add(txtItemName);

        panelTop.add(new JLabel("Price:"));
        txtPrice = new JTextField();
        panelTop.add(txtPrice);

        panelTop.add(new JLabel("Category:"));
        cmbCategory = new JComboBox<>(new String[]{"Starter", "Main Course", "Dessert", "Drinks"});
        panelTop.add(cmbCategory);

        JButton btnAdd = new JButton("Add Item");
        panelTop.add(btnAdd);

        JButton btnDelete = new JButton("Delete Item");
        panelTop.add(btnDelete);

        add(panelTop, BorderLayout.NORTH);

        // ===== Table =====
        model = new DefaultTableModel(new String[]{"ID", "Item", "Price", "Category"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== Load Existing Items =====
        loadMenuItems();

        // ===== Button Actions =====
        btnAdd.addActionListener(e -> addMenuItem());
        btnDelete.addActionListener(e -> deleteMenuItem());
    }

    private void loadMenuItems() {
        model.setRowCount(0); // clear table
        try (Connection con = DBconnection.getConnection()) {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM menu");
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("item_name"),
                        rs.getDouble("price"),
                        rs.getString("category")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading menu: " + e.getMessage());
        }
    }

    private void addMenuItem() {
        String name = txtItemName.getText();
        String price = txtPrice.getText();
        String category = cmbCategory.getSelectedItem().toString();

        if (name.isEmpty() || price.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter name and price!");
            return;
        }

        try (Connection con = DBconnection.getConnection()) {
            String sql = "INSERT INTO menu(item_name, price, category) VALUES (?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, name);
            pst.setDouble(2, Double.parseDouble(price));
            pst.setString(3, category);

            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Item Added Successfully!");
            loadMenuItems();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteMenuItem() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to delete.");
            return;
        }

        int id = (int) model.getValueAt(row, 0);

        try (Connection con = DBconnection.getConnection()) {
            String sql = "DELETE FROM menu WHERE id=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Item Deleted Successfully!");
            loadMenuItems();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    // 🔹 MAIN METHOD FOR TESTING
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MenuManagement().setVisible(true);
        });
    }
}
