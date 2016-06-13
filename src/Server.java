
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

/**
 * Created by casde on 11-6-2016.
 */
public class Server implements Finals, Runnable
{
    private ServerSocket serverSocket;
    private int sessionNo = 1;
    public Server()
    {
        new Thread(this).start();
    }


    @Override
    public void run()
    {
        try {

            // Create a server socket
            ServerSocket serverSocket = new ServerSocket(1337);
            System.out.println(new Date() +
                    ": Server started at socket 1337\n");

            // Ready to create a session for every two players
            while (true) {
                System.out.println(new Date() +
                        ": Wait for players to join session " + sessionNo + '\n');

                // Connect to player 1
                Socket player1 = serverSocket.accept();
                // Connect to player 2
                Socket player2 = serverSocket.accept();

                // Display this session and increment session number
                System.out.println(new Date() +
                                ": Start a thread for session " + sessionNo++ + '\n');

                // Launch a new thread for this session of two players
                new Thread(new HandleASession(player1, player2)).start();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    class HandleASession implements Runnable
    {
        private Socket player1;
        private Socket player2;
        private DataInputStream fromPlayer1;
        private DataOutputStream toPlayer1;
        private DataInputStream fromPlayer2;
        private DataOutputStream toPlayer2;
        private int player1Score;
        private int player2Score;
        private boolean playing;

        public HandleASession(Socket player1, Socket player2)
        {
            this.player1 = player1;
            this.player2 = player2;
            playing = true;
        }

        @Override
        public void run()
        {
            try {
                DataInputStream fromPlayer1 = new DataInputStream(
                        player1.getInputStream());
                DataOutputStream toPlayer1 = new DataOutputStream(
                        player1.getOutputStream());
                DataInputStream fromPlayer2 = new DataInputStream(
                        player2.getInputStream());
                DataOutputStream toPlayer2 = new DataOutputStream(
                        player2.getOutputStream());


                Integer[] colors = {1,1,2,2,3,3,4,4,5,5,6,6,7,7,8,8};
                ArrayList<Integer> cardColors = new ArrayList<Integer>(Arrays.asList(colors));
                Collections.shuffle(cardColors);
                System.out.println(new Date() + ": Player 1 joined session "
                        + sessionNo + '\n');
                System.out.println("Player 1's IP address" +
                        player1.getInetAddress().getHostAddress() + '\n');
                // Notify that the player is Player 1
                new DataOutputStream(
                        player1.getOutputStream()).writeInt(PLAYER1);
                System.out.println("wrote player nr to player 1");
                for(int i : cardColors)
                {
                    new DataOutputStream(player1.getOutputStream()).writeInt(i);
                }

                System.out.println(new Date() +
                        ": Player 2 joined session " + sessionNo + '\n');
                System.out.println("Player 2's IP address" +
                        player2.getInetAddress().getHostAddress() + '\n');

                // Notify that the player is Player 2
                new DataOutputStream(
                        player2.getOutputStream()).writeInt(PLAYER2);

                for(int i : cardColors)
                {
                    new DataOutputStream(player2.getOutputStream()).writeInt(i);
                }

                while (playing)
                {
                    toPlayer2.writeInt(OPPONTENT_TURN);
                    toPlayer1.writeInt(MY_TURN);


                    System.out.println("player1 turn :: player 2 not turn");

                    int row1 = fromPlayer1.readInt();
                    int column1 = fromPlayer1.readInt();

                    System.out.println("move 1 received: " + row1 + "-----" + column1);

                    toPlayer2.writeInt(row1);
                    toPlayer2.writeInt(column1);

                    System.out.println(" move 1 sent" + row1 + " -----" + column1);

                    int row2 = fromPlayer1.readInt();
                    int column2 = fromPlayer1.readInt();

                    System.out.println("move 2 received: " + row2 + "-----" + column2);

                    toPlayer2.writeInt(row2);
                    toPlayer2.writeInt(column2);

                    System.out.println(" move 2 sent" + row2 + " -----" + column2);

                    int noot1 = fromPlayer1.readInt();
                    System.out.println(noot1 + " MATCHFOUND?");
                    if(noot1 == MATCH_FOUND)
                    {
                        player1Score++;
                    }

                    int isChecked = fromPlayer1.readInt();
                    int isChecked2 = fromPlayer2.readInt();

                    System.out.println("is it checked cuck? " + isChecked + "------" + isChecked2);
                    toPlayer1.writeInt(OPPONTENT_TURN);
                    toPlayer2.writeInt(MY_TURN);


                    System.out.println("player2 turn :: player 1 not turn");

                        int row3 = fromPlayer2.readInt();
                        int column3 = fromPlayer2.readInt();

                        System.out.println(" move 3 received: " + row3 + "----" + column3);

                        toPlayer1.writeInt(row3);
                        toPlayer1.writeInt(column3);

                    System.out.println(" move 3 sent" + row3 + " -----" + column3);

                        int row4 = fromPlayer2.readInt();
                        int column4 = fromPlayer2.readInt();

                        System.out.println(" move 4 received: " + row4 + "----" + column4);

                        toPlayer1.writeInt(row4);
                        toPlayer1.writeInt(column4);

                    System.out.println(" move 4 sent" + row4 + " -----" + column4);

//                    boolean isMatchFound2 = fromPlayer2.readBoolean();
//                    System.out.println(" boolean received: " + isMatchFound2);
//                    if(isMatchFound2 == true) {
//                        System.out.println("player 1 score++");
//                        player2Score++;
//                    }

                    int noot2 = fromPlayer2.readInt();
                    System.out.println(noot1 + "MATCHFOUND?");
                    if(noot2 == MATCH_FOUND)
                        player2Score++;

                    int isChecked3 = fromPlayer1.readInt();
                    int isChecked4 = fromPlayer2.readInt();

                    System.out.println(" PLAYERSCORES: " + player1Score + "----" + player2Score);

                    if(player2Score + player1Score > 7)
                    {
                        System.out.println("board empty mofo");
                        ArrayList<Integer> scores = new ArrayList<>();
                        scores.add(player1Score);
                        scores.add(player2Score);

                        if(player1Score == player2Score)
                        {
                            toPlayer1.writeInt(DRAW);
                            toPlayer2.writeInt(DRAW);

                            toPlayer1.writeInt(player1Score);
                            toPlayer1.writeInt(player2Score);

                            toPlayer2.writeInt(player2Score);
                            toPlayer2.writeInt(player1Score);
                            playing = false;
                        }
                        else {
                            Collections.sort(scores, new ScoreComparator());
                            if (scores.get(0) == player1Score) {
                                toPlayer1.writeInt(PLAYER1_WON);
                                toPlayer2.writeInt(PLAYER1_WON);

                                toPlayer1.writeInt(player1Score);
                                toPlayer1.writeInt(player2Score);

                                toPlayer2.writeInt(player2Score);
                                toPlayer2.writeInt(player1Score);
                                playing = false;
                            } else {
                                toPlayer1.writeInt(PLAYER2_WON);
                                toPlayer2.writeInt(PLAYER2_WON);

                                toPlayer1.writeInt(player1Score);
                                toPlayer1.writeInt(player2Score);

                                toPlayer2.writeInt(player2Score);
                                toPlayer2.writeInt(player1Score);

                                playing = false;
                            }
                        }
                    }

                    }
                }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] arg)
    {
        new Server();
    }
}
