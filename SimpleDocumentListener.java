// Simple DocumentListener helper
interface SimpleDocumentListener extends javax.swing.event.DocumentListener {
    void update();
    @Override default void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
    @Override default void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
    @Override default void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
}