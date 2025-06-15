package com.eventos;

import com.eventos.dao.DatabaseInitializer;
import com.eventos.gui.MainFrame; // Importar a nova classe MainFrame
import javax.swing.SwingUtilities; // Importar SwingUtilities

// As classes EventoView, PalestranteView e ParticipanteView no pacote com.eventos.view
// não serão mais usadas para a interface gráfica, mas você pode mantê-las se quiser
// a funcionalidade de console ainda. Para a GUI, elas são redundantes.
// import com.eventos.view.EventoView;
// import com.eventos.view.PalestranteView;
// import com.eventos.view.ParticipanteView;


public class Main {
    public static void main(String[] args) {
        // Inicializa o banco de dados e cria as tabelas se não existirem
        DatabaseInitializer.initialize();

        // Inicia a aplicação Swing na Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
