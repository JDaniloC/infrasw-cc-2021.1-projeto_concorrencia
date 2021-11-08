import ui.PlayerWindow;

import java.awt.event.*;

public class Player {
    public Player() {
        String windowTitle = "Spotifraco";

        ActionListener playNowAction = e -> System.out.println("Clicou no playNow!");
        ActionListener RemoveAction = e -> System.out.println("Clicou no Remove!");
        ActionListener AddSongAction = e -> addSong();
        ActionListener PlayPauseAction = e -> System.out.println("Clicou no PlayPause!");
        ActionListener StopAction = e -> System.out.println("Clicou no Stop!");
        ActionListener NextAction = e -> System.out.println("Clicou no Next!");
        ActionListener PreviousAction = e -> System.out.println("Clicou no Previous!");
        ActionListener ShuffleAction = e -> System.out.println("Clicou no Shuffle!");
        ActionListener RepeatAction = e -> System.out.println("Clicou no Repeat!");
        MouseListener scrubber = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Clicou no mouse");
            }

            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println("Pressionou o mouse");
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                System.out.println("Soltou o mouse");
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // Do nothing
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Do nothing
            }
        };
        MouseMotionListener motion = new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                System.out.println("Mouse dragged");
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                // Do nothing
            }
        };
        String[][] queueArray = new String[1][1];
        final PlayerWindow window = new PlayerWindow(
                playNowAction,
                RemoveAction,
                AddSongAction,
                PlayPauseAction,
                StopAction,
                NextAction,
                PreviousAction,
                ShuffleAction,
                RepeatAction,
                scrubber,
                motion,
                windowTitle,
                queueArray
        );
    }
    private void addSong() {
    }
}

