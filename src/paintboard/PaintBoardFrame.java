package paintboard;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

import java.awt.event.MouseListener;
import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class PaintBoardFrame extends JFrame {
    private JButton[] tools;

    private final String[] actions = {
            "new", "open", "saveAs", "pencil", "line", "hRect", "fRect", "hOval", "fOval", "hCircle",
            "fCircle", "hrRect", "frRect", "eraser", "palette", "stroke", "text"};

    private final String[] tipText = {
            "Open a new board",
            "Open a saved board",
            "Save current board",
            "Pencil tool",
            "Straight line",
            "Hollow rectangle",
            "Filled rectangle",
            "Hollow Oval",
            "Filled oval",
            "Hollow circle",
            "Fill a circle",
            "Hollow round rectangle",
            "Filled round rectangle",
            "Eraser",
            "Color palette",
            "Set line stroke",
            "Text tool"
    };

    private Color color = Color.black;
    private JLabel statusBar;
    public DrawPanel paintingPanel;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private final int width = 1000, height = 800;
    private volatile int currentTool = 3;
    private float stroke = 1.0f;
    int R, G, B;
    int genre1, genre2;
    int index = 0;
    JToolBar toolsPanel;
    Paintable[] components = new Paintable[5000];

    volatile Paintable newOb = null;

    public PaintBoardFrame() {
        super("Online PaintBoard");
        JMenuBar bar = new JMenuBar();

        // add file menu bar
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        JMenuItem newItem = new JMenuItem("New");
        newItem.setMnemonic('N');
        newItem.addActionListener(
                e -> newFile());
        fileMenu.add(newItem);
        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.setMnemonic('S');
        saveItem.addActionListener(
                e -> saveFile());
        fileMenu.add(saveItem);
        JMenuItem loadItem = new JMenuItem("Load");
        loadItem.setMnemonic('L');
        loadItem.addActionListener(
                e -> loadFile());
        fileMenu.add(loadItem);
        fileMenu.addSeparator();
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic('X');
        exitItem.addActionListener(
                e -> System.exit(0));
        fileMenu.add(exitItem);
        bar.add(fileMenu);

        // set color
        JMenu colorMenu = new JMenu("Color");
        colorMenu.setMnemonic('C');
        JMenuItem colorItem = new JMenuItem("Choose Color");
        colorItem.setMnemonic('O');
        colorItem.addActionListener(
                e -> chooseColor());

        // set stroke
        colorMenu.add(colorItem);
        bar.add(colorMenu);
        JMenu strokeMenu = new JMenu("Stroke");
        strokeMenu.setMnemonic('S');
        JMenuItem strokeItem = new JMenuItem("Set Stroke");
        strokeItem.setMnemonic('K');
        strokeItem.addActionListener(
                e -> setStroke());
        strokeMenu.add(strokeItem);
        bar.add(strokeMenu);

        //set panels
        paintingPanel = new DrawPanel();
        tools = new JButton[actions.length];
        toolsPanel = new JToolBar(JToolBar.HORIZONTAL);
        
        ButtonHandlerx handlerx = new ButtonHandlerx();
        ButtonHandlery handlery = new ButtonHandlery();

        for (int i = 0; i < tools.length; i++) {
            tools[i] = new JButton(actions[i]);
            tools[i].setToolTipText(tipText[i]);
            toolsPanel.add(tools[i]);
        }

        for (int i = 3; i < tools.length - 3; i++) {
            tools[i].addActionListener(handlery);
        }

        for (int i = 1; i < 4; i++) {
            tools[tools.length - i].addActionListener(handlerx);
        }

        tools[0].addActionListener(
                e -> newFile());
        tools[1].addActionListener(
                e -> loadFile());
        tools[2].addActionListener(
                e -> saveFile());

        Container cont = getContentPane();
        super.setJMenuBar(bar);
        cont.add(toolsPanel, BorderLayout.NORTH);
        cont.add(paintingPanel, BorderLayout.CENTER);
        statusBar = new JLabel();
        cont.add(statusBar, BorderLayout.SOUTH);

        createNewItem();
        setSize(width, height);
        setVisible(true);
    }

    public class ButtonHandlery implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            for (int j = 3; j < tools.length - 3; j++) {
                if (e.getSource() == tools[j]) {
                    currentTool = j;
                    createNewItem();
                    repaint();
                }
            }
        }
    }

    public class ButtonHandlerx implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == tools[tools.length - 3]) {
                chooseColor();
            }
            if (e.getSource() == tools[tools.length - 2]) {
                setStroke();
            }
            if (e.getSource() == tools[tools.length - 1]) {
                currentTool = 14;
                createNewItem();
                repaint();
            }
        }
    }

    class mouseEvent1 extends MouseAdapter{
        public void mousePressed(MouseEvent e) {
            statusBar.setText("     Mouse Pressed @:[" + e.getX() +
                    ", " + e.getY() + "]");
            components[index].x1 = components[index].x2 = e.getX();
            components[index].y1 = components[index].y2 = e.getY();
            if (currentTool == 3 || currentTool == 13) {
                components[index].x1 = components[index].x2 = e.getX();
                components[index].y1 = components[index].y2 = e.getY();
                index++;
                createNewItem();
            }
            if (currentTool == 14) {
                components[index].x1 = e.getX();
                components[index].y1 = e.getY();
                String input;
                input = JOptionPane.showInputDialog(
                        "Text you want to display on board");
                ((Text)components[index]).setText(input);
                components[index].x2 = genre1;
                components[index].y2 = genre2;
                ((Text)components[index]).setFont("Avenir Next");
                index++;
                createNewItem();
                paintingPanel.repaint();
            }
        }
        public void mouseReleased(MouseEvent e) {
            statusBar.setText("     Mouse Released @:[" + e.getX() +
                    ", " + e.getY() + "]");
            if (currentTool == 3 || currentTool == 13) {
                components[index].x1 = e.getX();
                components[index].y1 = e.getY();
            }
            components[index].x2 = e.getX();
            components[index].y2 = e.getY();
            newOb = components[index];
            repaint();
            index++;
            createNewItem();
        }
        public void mouseEntered(MouseEvent e) {
            statusBar.setText("     Mouse Entered @:[" + e.getX() +
                    ", " + e.getY() + "]");
        }
        public void mouseExited(MouseEvent e) {
            statusBar.setText("     Mouse Exited @:[" + e.getX() +
                    ", " + e.getY() + "]");
        }

    }

    class mouseEvent2 implements MouseMotionListener,Runnable{
        ObjectOutputStream oss;
        public mouseEvent2() {
            Thread t = new Thread(this);
            t.start();
        }
        public void mouseDragged(MouseEvent e) {
            statusBar.setText("     Mouse Dragged @:[" + e.getX() +
                    ", " + e.getY() + "]");

            if (currentTool == 3 || currentTool == 13) {
                components[index - 1].x1 = components[index].x2 = components[index].x1 = e.getX();
                components[index - 1].y1 = components[index].y2 = components[index].y1 = e.getY();
                newOb = components[index];
                index++;
                createNewItem();
            } else {
                components[index].x2 = e.getX();
                components[index].y2 = e.getY();
            }
            repaint();
        }
        public void mouseMoved(MouseEvent e) {
            statusBar.setText("     Mouse Moved @:[" + e.getX() +
                    ", " + e.getY() + "]");
        }
        public void run() {
            try {
                //设置服务器并置于等待链接状态
                ServerSocket ss = new ServerSocket(9090);

                Socket server = ss.accept();
                //连接成功后得到数据输出流
                oss = new ObjectOutputStream(server.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            //x1,y1为起始点坐标，x2,y2为终点坐标。四个点的初始值设为0
            while (true) {
                //服务器界面画下一条线时，将四个点的信息写入到数据输出流中，之后将四个数据置0
                if(newOb != null && newOb.x1 != 0 && newOb.y1 != 0) {
                    try {
                        oss.writeObject(newOb);

                        //os.writeInt(newOb.R);
                        //os.writeInt(newOb.G);
                        //os.writeInt(newOb.B);

                        //os.writeUTF(newOb.s1);
                        //os.writeUTF(newOb.s2);
                        newOb = null;
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    class DrawPanel extends JPanel {
        public DrawPanel() {
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            setBackground(Color.white);
            addMouseListener(new mouseEvent1());
            addMouseMotionListener(new mouseEvent2());

        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            int j = 0;
            while (j <= index) {
                draw(g2d, components[j]);
                j++;
            }
        }

        void draw(Graphics2D g2d, Paintable i) {
            i.draw(g2d);
        }
    }

    void createNewItem() {
        if (currentTool == 14)
        {
            paintingPanel.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        } else {
            paintingPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }
        switch (currentTool) {
//            case 3:
//                components[index] = new Pencil();
//                break;
//            case 4:
//                components[index] = new Line();
//                break;
//            case 5:
//                components[index] = new Rect();
//                break;
//            case 6:
//                components[index] = new fillRect();
//                break;
//            case 7:
//                components[index] = new Oval();
//                break;
//            case 8:
//                components[index] = new fillOval();
//                break;
//            case 9:
//                components[index] = new Circle();
//                break;
//            case 10:
//                components[index] = new fillCircle();
//                break;
//            case 11:
//                components[index] = new RoundRect();
//                break;
//            case 12:
//                components[index] = new fillRoundRect();
//                break;
//            case 13:
//                components[index] = new Rubber();
//                break;
            case 14:
                components[index] = new Text();
                break;
            default:
                components[index] = new Circle();
                break;
        }

        components[index].type = currentTool;
        System.out.println("Set index: "+ index +" choice as "+currentTool);
        components[index].R = R;
        components[index].G = G;
        components[index].B = B;
        components[index].stroke = stroke;
    }

    public void chooseColor() {
        color = JColorChooser.showDialog(PaintBoardFrame.this,
                "Choose color", color);

        if (color != null) {
            R = color.getRed();
            G = color.getGreen();
            B = color.getBlue();
            components[index].R = R;
            components[index].G = G;
            components[index].B = B;
        }
    }

    public void setStroke() {
        String input;
        input = JOptionPane.showInputDialog(
                "Please input the size of stroke!");
        stroke = Float.parseFloat(input);
        components[index].stroke = stroke;
    }

    public void saveFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }
        File fileName = fileChooser.getSelectedFile();
        fileName.canWrite();

        if (fileName == null || fileName.getName().equals("")) {
            JOptionPane.showMessageDialog(fileChooser, "Invalid File Name",
                    "Invalid File Name", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                fileName.delete();
                FileOutputStream fos = new FileOutputStream(fileName);
                output = new ObjectOutputStream(fos);
                Paintable record;
                output.writeInt(index);
                for (int i = 0; i < index; i++) {
                    Paintable p = components[i];
                    output.writeObject(p);
                    output.flush();
                }
                output.close();
                fos.close();
            } catch (IOException ee) {
                ee.printStackTrace();
            }
        }
    }

    public void loadFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }
        File fileName = fileChooser.getSelectedFile();
        fileName.canRead();
        if (fileName == null || fileName.getName().equals("")) {
            JOptionPane.showMessageDialog(fileChooser, "Invalid File Name",
                    "Invalid File Name", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                FileInputStream fis = new FileInputStream(fileName);
                input = new ObjectInputStream(fis);
                Paintable inputRecord;
                int countNumber = 0;
                countNumber = input.readInt();
                for (index = 0; index < countNumber; index++) {
                    inputRecord = (Paintable) input.readObject();
                    components[index] = inputRecord;
                }
                createNewItem();
                input.close();
                repaint();
            } catch (EOFException endofFileException) {
                JOptionPane.showMessageDialog(this, "no more record in file",
                        "class not found", JOptionPane.ERROR_MESSAGE);
            } catch (ClassNotFoundException classNotFoundException) {
                JOptionPane.showMessageDialog(this, "Unable to Create Object",
                        "end of file", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ioException) {
                JOptionPane.showMessageDialog(this, "error during read from file",
                        "read Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void newFile() {
        index = 0;
        currentTool = 3;
        color = Color.black;
        stroke = 1.0f;
        createNewItem();
        repaint();
    }

}