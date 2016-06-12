

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * Created by casde on 9-6-2016.
 */
public class MemoryClient extends JPanel implements Finals
{
    private ArrayList<ArrayList<Card>> board;
    private ArrayList<Card> selectedCards;
    private ArrayList<Integer> cardColors;
    private DataOutputStream toServer;
    private DataInputStream fromServer;
    private int cardNr;
    private boolean myTurn;
    private int nrOfEmptyRows;
    private boolean twoCardsSelected;
    private int noot = -1;
    private JTextArea area;
    private String text;
    private int playerNr;

    public MemoryClient()
    {
        setLayout(null);
        area = new JTextArea();
        area.setEditable(false);
        area.setBounds(450,0,430,440);
        Border border = new LineBorder(Color.BLACK);
        area.setBorder(border);
        selectedCards = new ArrayList<>();
        board = new ArrayList<>();
        board.add(new ArrayList<Card>());
        myTurn = false;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(myTurn) {
                    int x = e.getX();
                    int y = e.getY();
                    //System.out.println("CLICKED AT: " + x + "----" + y);
                    getSelectedCard(x, y);
                    sendMove(x, y);
                }
            }
        });
        connectToServer();
        text = " Hoi speler: " + playerNr;
        area.setText(text);
        this.add(area);
        new Timer(1000, e -> repaint()).start();
        setPreferredSize(new Dimension(800,800));
        setVisible(true);
        //new Timer(1000/60, e -> checkIfTurn()).start();
    }


    public void connectToServer()
    {
        try {
            // Create a socket to connect to the server
            Socket socket = new Socket("localhost", 1337);

            // Create an input stream to receive data from the server
            fromServer = new DataInputStream(socket.getInputStream());

            // Create an output stream to send data to the server
            toServer = new DataOutputStream(socket.getOutputStream());

            // Get notification from the server
            int player = fromServer.readInt();
            System.out.println(" i am player " + player);

            // Am I player 1 or 2?
            if (player == PLAYER1) {
                playerNr = 1;
                area.setText(text + "wachten op speler 2");

                cardColors = new ArrayList<Integer>();
                for(int i = 0; i < 16; i++)
                {
                    cardColors.add(fromServer.readInt());
                }

                twoCardsSelected = false;
                cardNr = 0;
                createRecursiveBoard(0,0,100,100);

                // The other player has joined
                new Thread(new ClientListener(socket)).start();

            } else if (player == PLAYER2) {
                playerNr = 2;
                System.out.println("Player2");
                cardColors = new ArrayList<Integer>();
                for(int ii = 0; ii < 16; ii++)
                {
                    cardColors.add(fromServer.readInt());
                }
                //Collections.shuffle(cardColors);
                System.out.println("creating board");
                createRecursiveBoard(0,0,100,100);
                twoCardsSelected = false;
                cardNr = 0;

                System.out.println("Player 2");
                area.setText(text + "wachten op speler 1");

                new Thread(new ClientListener(socket)).start();
            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        nrOfEmptyRows = 0;
        Iterator<ArrayList<Card>> it = board.iterator();
        {
            while(it.hasNext()) {
                ArrayList<Card> cards = it.next();
                if (cards.isEmpty()) {
                    nrOfEmptyRows++;
                } else {
                    for (Card card : cards) {
                        card.draw(g2);
                    }
                }
            }
        }
    }

    public void createRecursiveBoard(int x, int y, int width, int height)
    {
        if(y == 4)
        {

        }
        else if(x == 3)
        {
            board.get(y).add(new Card(x * width + x * 10, y * height + y * 10, 100,100,cardColors.get(cardNr)));
            cardNr++;
            x = 0;
            y++;
            board.add(new ArrayList<>());
            createRecursiveBoard(x,y,width,height);
        }
        else {
            board.get(y).add(new Card(x * width + x * 10, y * height + y * 10, 100,100,cardColors.get(cardNr)));
            cardNr++;
            x++;
            createRecursiveBoard(x, y, width, height);
        }
    }

    public static void main(String[] arg)
    {
        MemoryClient panel = new MemoryClient();
        JFrame frame = new JFrame("MemoryClient player " + panel.getPlayerNr());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.setSize(new Dimension(800,800));
        frame.setVisible(true);
    }

        private void receiveMove() throws IOException {
        // Get the other player's move
        int x = fromServer.readInt();
        int y = fromServer.readInt();
            System.out.println(x + "----" + y + " received");
        getSelectedCard(x,y);
    }

    private void sendMove(int x, int y){
        try {
            toServer.writeInt(x); // Send the selected row
            toServer.writeInt(y); // Send the selected column
            System.out.println(x + " ----- " + y + " send ");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void getSelectedCard(int x, int y) {
        if (nrOfEmptyRows == 4) {
        }
        for (ArrayList<Card> cards : board) {
            for (Card card : cards) {
                if (card.getRect().contains(x, y)) {
                    if (!twoCardsSelected) {
                        if (!selectedCards.contains(card)) {
                            card.flip();
                            selectedCards.add(card);
                            repaint();
                            //try{Thread.sleep(1000);}catch(Exception e){e.printStackTrace();}
                            checkCards();
                            break;
                        }
                    }
                }
            }
        }

        //checkCardTimer = new Timer(500, e -> checkCards());
        //checkCardTimer.start();
    }

    public void checkCards()
    {

        try {
            if (selectedCards.size() >= 2) {
                twoCardsSelected = true;
                Card card1 = selectedCards.get(0);
                Card card2 = selectedCards.get(1);

                if (card1.compareTo(card2) == 1) {
                    selectedCards.clear();
                    board.get(card1.getY()).remove(card1);
                    board.get(card2.getY()).remove(card2);
                    if (myTurn) {
                        noot = MATCH_FOUND;
                        System.out.println("score ++");
                        toServer.writeInt(noot);
                    }
                    twoCardsSelected = false;
                } else {
                    card1.flip();
                    card2.flip();
                    if(myTurn) {
                        noot = NO_MATCH_FOUND;
                        toServer.writeInt(noot);
                    }
                    selectedCards.clear();
                    twoCardsSelected = false;
                }
            }
        }catch(Exception e){e.printStackTrace();}
    }

    public int getPlayerNr() {
        return playerNr;
    }

    class ClientListener implements Runnable
    {
        private Socket clientSocket;
        public ClientListener(Socket socket)
        {
            this.clientSocket = socket;
        }
        @Override
        public void run()
        {
            try
            {

                while(true) {
                    fromServer = new DataInputStream(clientSocket.getInputStream());

                    int status = fromServer.readInt();

                    if (status == MY_TURN) {
                        System.out.println(" myturn ");
                        area.setText(text + " \n het is mijn beurt");
                        myTurn = true;
                    }

                    if (status == OPPONTENT_TURN) {
                        area.setText(text + " \n het is niet mijn beurt");
                        System.out.println(" oppontents turn ");
                        myTurn = false;
                        receiveMove();
                        receiveMove();
                    }
                    if(status == DRAW)
                    {
                        int myScore = fromServer.readInt();
                        int opponentScore = fromServer.readInt();
                        area.setText(text + " \n mijn score: " + myScore);
                        area.setText(area.getText() + " \n tegenstanders score: " + opponentScore);
                        area.setText(area.getText() + " \n gelijkspel");
                        System.out.println("player1 won");
                    }

                    if (status == PLAYER1_WON) {
                        int myScore = fromServer.readInt();
                        int opponentScore = fromServer.readInt();
                        area.setText(text + " \n mijn score: " + myScore);
                        area.setText(area.getText() + " \n tegenstanders score: " + opponentScore);
                        area.setText(area.getText() + " \n speler 1 heeft gewonnen");
                        System.out.println("player1 won");
                    }

                    if (status == PLAYER2_WON) {
                        int myScore = fromServer.readInt();
                        int opponentScore = fromServer.readInt();

                        area.setText(text + " \n mijn score: " + myScore);
                        area.setText(area.getText() + " \n tegenstanders score: " + opponentScore);
                        area.setText(area.getText() + " \n speler 2 heeft gewonnen");
                        System.out.println(" player2 won");
                    }
                }
            }
            catch(Exception e){e.printStackTrace();}
        }

    }
    }
