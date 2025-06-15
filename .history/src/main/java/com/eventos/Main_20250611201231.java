// Seu Main.java atualizado
package com.eventos;

import com.eventos.dao.DatabaseInitializer;
import com.eventos.gui.MainFrame; // Importar a nova classe MainFrame
import javax.swing.SwingUtilities; // Importar SwingUtilities

public class Main {
    public static void main(String[] args) {
        // Inicializa o banco de dados
        DatabaseInitializer.initialize();

        // Inicia a aplicação Swing na Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
