import ui.PlayerWindow;
import ui.AddSongWindow;

import java.awt.event.*;
import java.util.Objects;
import java.util.Random;

public class Player {

    private final PlayerWindow window;
    private AddSongWindow currentAddSongWindow;
    private ScrollerThread scrollerThread;
    public String[][] queueArray = {};
    private Random random = new Random();

    private int currentSongID = 0;
    private boolean repeat = false;
    private boolean shuffle = false;
    private int selectedSongIndex = 0;
    private int songLengthInSeconds = 0;

    private Boolean somethingIsPlaying = false;

    public Player() {
        String windowTitle = "Spotifraco";

        ActionListener playNowAction = e -> playSong();
        ActionListener RemoveAction = e -> removeSong();
        ActionListener AddSongAction = e -> addSong();
        ActionListener PlayPauseAction = e -> playAndPause();
        ActionListener StopAction = e -> stopSong();
        ActionListener NextAction = e -> nextSong();
        ActionListener PreviousAction = e -> previousSong();
        ActionListener ShuffleAction = e -> shuffleSong();
        ActionListener RepeatAction = e -> repeatSong();

        MouseListener scrubber = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {
                if (scrollerThread != null) scrollerThread.interrupt();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (somethingIsPlaying) {
                    somethingIsPlaying = false;
                    playAndPause();
                }
            }

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

    private void shuffleSong() { this.shuffle = !this.shuffle; }
    private void repeatSong() { this.repeat = !this.repeat; }

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

    private void updateCurrentSong(String[] song) {
        stopSong();
        this.window.updatePlayingSongInfo(song[0], song[1], song[2]);
        this.songLengthInSeconds = Integer.parseInt(song[5]);
        this.window.enableScrubberArea();
    }

    private int getCurrentSongIndex(int id) {
        for (int index = 0; index < queueArray.length; index++) {
            String[] song = queueArray[index];
            if (Objects.equals(song[6], Integer.toString(id))) {
                return index;
            }
        }
        return -1;
    }

    private void updateSong(int index) {
        String[] song = queueArray[index];
        this.selectedSongIndex = index;
        updateCurrentSong(song);
    }

    public void nextSong() {
        int index = this.selectedSongIndex;

        if (this.shuffle) {
            do {
                index = this.random.nextInt(queueArray.length - 1);
            } while (index == this.selectedSongIndex && this.queueArray.length > 1);
        } else {
            if (index != queueArray.length - 1) {
                index = index + 1;
            } else {
                index = 0;
            }
        }
        boolean wasPlaying = this.somethingIsPlaying;
        this.updateSong(index);
        if (wasPlaying) {
            playAndPause();
        }
    }

    private void previousSong() {
        int index = this.selectedSongIndex;

        if (index != 0) {
            index = index - 1;
        } else {
            index = queueArray.length - 1;
        }
        boolean wasPlaying = this.somethingIsPlaying;
        this.updateSong(index);
        if (wasPlaying) {
            playAndPause();
        }
    }

    private void playSong() {
        int id = this.window.getSelectedSongID();
        int index = getCurrentSongIndex(id);
        this.updateSong(index);
    }

    private void stopSong() {
        this.window.resetMiniPlayer();
        if (scrollerThread != null) scrollerThread.interrupt();
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
                    this,
                    this.repeat,
                    currentSecond,
                    songLengthInSeconds,
                    this.selectedSongIndex,
                    this.queueArray.length
            );
            scrollerThread.start();
        } else {
            scrollerThread.interrupt();
        }
        somethingIsPlaying = !somethingIsPlaying;
        window.updatePlayPauseButton(somethingIsPlaying);
    }
}

