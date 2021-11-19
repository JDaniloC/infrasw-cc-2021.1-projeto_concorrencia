import ui.PlayerWindow;
import ui.AddSongWindow;

import java.awt.event.*;
import java.util.Objects;

public class Player {

    private final PlayerWindow window;
    private AddSongWindow currentAddSongWindow;
    private ScrollerThread scrollerThread;
    public String[][] queueArray = {};

    private int currentSongID = 0;
    private int currentScrollerPosition = 0;
    private int songLengthInSeconds = 0;

    private Boolean somethingIsPlaying = false;


    public Player() {
        String windowTitle = "Spotifraco";

        ActionListener playNowAction = e -> playSong();
        ActionListener RemoveAction = e -> removeSong();
        ActionListener AddSongAction = e -> addSong();
        ActionListener PlayPauseAction = e -> playAndPause();
        ActionListener StopAction = e -> stopSong();
        ActionListener NextAction = e -> System.out.println("Clicou no Next!");
        ActionListener PreviousAction = e -> System.out.println("Clicou no Previous!");
        ActionListener ShuffleAction = e -> System.out.println("Clicou no Shuffle!");
        ActionListener RepeatAction = e -> System.out.println("Clicou no Repeat!");

        MouseListener scrubber = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        };
        MouseMotionListener motion = new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {}

            @Override
            public void mouseMoved(MouseEvent e) {}
        };
        String[][] queueArray = new String[1][1];
        this.window = new PlayerWindow(
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

    private void addSongToQueue(String[] newSong) {
        String[][] newQueueList = new String[queueArray.length + 1][7];
        System.arraycopy(
                queueArray, 0,
                newQueueList, 0,
                queueArray.length
        );
        newQueueList[queueArray.length] = newSong;
        window.updateQueueList(newQueueList);
        this.queueArray = newQueueList;
    }

    private void addSong() {
        ActionListener addSongOkAction = e -> {
            String[] array = currentAddSongWindow.getSong();
            addSongToQueue(array);
        };
        WindowListener listener = this.window.getAddSongWindowListener();
        AddSongWindow newWindow = new AddSongWindow(
                Integer.toString(this.currentSongID),
                addSongOkAction,
                listener
        );
        this.currentAddSongWindow = newWindow;
        this.currentSongID ++;
        newWindow.start();
    }

    private void removeSong() {
        int id = this.window.getSelectedSongID();
        String[][] newQueueList = new String[queueArray.length - 1][7];
        for (int index = 0; index < queueArray.length; index++) {
            String[] song = queueArray[index];
            if (!Objects.equals(song[6], Integer.toString(id))) {
                newQueueList[index] = song;
            }
        }
        this.queueArray = newQueueList;
        this.window.updateQueueList(newQueueList);
    }

    private void playSong() {
        int id = this.window.getSelectedSongID();
        for (String[] song: queueArray) {
            if (Objects.equals(song[6], Integer.toString(id))) {
                this.window.updatePlayingSongInfo(
                        song[0], song[1], song[2]);
                this.songLengthInSeconds = Integer.parseInt(song[5]);
            }
        }
        this.window.enableScrubberArea();
    }

    private void stopSong() {
        this.window.resetMiniPlayer();
        somethingIsPlaying = false;
    }

    private void playAndPause() {
        int currentSecond = 0;
        if (songLengthInSeconds != window.getScrubberValue()) {
            currentSecond = window.getScrubberValue();
        } else {
            somethingIsPlaying = false;
        }

        if (!somethingIsPlaying) {
            scrollerThread = new ScrollerThread(
                    window,
                    currentSecond,
                    songLengthInSeconds
            );
            scrollerThread.start();
        } else {
            scrollerThread.interrupt();
        }
        somethingIsPlaying = !somethingIsPlaying;
        window.updatePlayPauseButton(somethingIsPlaying);
    }
}

