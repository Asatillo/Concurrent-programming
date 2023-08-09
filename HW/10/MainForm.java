import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;

import static javax.swing.JOptionPane.showMessageDialog;

public class MainForm {

    private static void createAndShowGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);

        JFrame frame = new JFrame("Prime counter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new GridLayout(3, 1));

        JLabel label = new JLabel("Enter a number!");

        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);
        JFormattedTextField textField = new JFormattedTextField(formatter);

        JButton button = new JButton("Do it!");
        button.setEnabled(false);

        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changed();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changed();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                changed();
            }

            private void changed() {
                button.setEnabled(!textField.getText().isBlank());
            }
        });
        
        
        
        button.addActionListener(e -> {
            
            System.out.println(textField.getText());
            int n = Integer.parseInt(textField.getText().replaceAll(",", ""));

            button.setEnabled(false);

            long time = System.currentTimeMillis();
            new Thread(() -> {
        
                Timer timer = new Timer(10, (ee) -> {
                    label.setText(String.format("%d ms", (System.currentTimeMillis() - time)));
                });
                timer.start();

                List<Integer> primes = SingleThreadPrime.primeFinder(n);
                
                SwingUtilities.invokeLater(() -> {
                    timer.stop();
                    label.setText("Prime counter");

                    JTextArea textArea = new JTextArea(6, 25);
                    textArea.setText(primes.toString());
                    textArea.setEditable(false);

                    JScrollPane scrollPane = new JScrollPane(textArea);
                    showMessageDialog(null, scrollPane);

                    button.setEnabled(!textField.getText().isBlank());
                });
            }).start();
        });

        frame.getContentPane().add(label);
        frame.getContentPane().add(textField);
        frame.getContentPane().add(button);

//        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainForm::createAndShowGUI);
    }
}