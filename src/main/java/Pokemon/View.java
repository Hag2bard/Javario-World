package Pokemon;


import javax.swing.*;

public class View extends JFrame {

    private Canvas canvas;

    public View() {
        canvas = new Canvas();
        add(canvas);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setSize(1024,860);
        setLocationRelativeTo(null);
//        setExtendedState(JFrame.MAXIMIZED_BOTH);

    }

}
