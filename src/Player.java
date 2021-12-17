import ui.PlayerWindow;
import ui.AddSongWindow;

import java.awt.event.*;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Player {

    private final Lock lock = new ReentrantLock();

    private AddSongWindow currentAddSongWindow;
    private ScrollerThread scrollerThread;
    private final PlayerWindow window;
    public String[][] queueArray = {};
    private Random random = new Random();

    private int currentSongID = 0;
    private int nextSongID = 0;
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
        this.window.updateQueueList(this.queueArray);
    }

    private void shuffleSong() { this.shuffle = !this.shuffle; }
    private void repeatSong() { this.repeat = !this.repeat; }

    private void addSongToQueue(String[] newSong) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock.lock();
                    String[][] newQueueList = new String[queueArray.length + 1][7];
                    System.arraycopy(
                            queueArray, 0,
                            newQueueList, 0,
                            queueArray.length
                    );
                    newQueueList[queueArray.length] = newSong;
                    window.updateQueueList(newQueueList);
                    queueArray = newQueueList;
                }
                finally {
                    lock.unlock();
                }
            }
        }).start();
    }

    private void addSong() {
        ActionListener addSongOkAction = e -> {
            String[] array = currentAddSongWindow.getSong();
            addSongToQueue(array);
        };
        WindowListener listener = this.window.getAddSongWindowListener();
        AddSongWindow newWindow = new AddSongWindow(
                Integer.toString(this.nextSongID),
                addSongOkAction,
                listener
        );
        this.currentAddSongWindow = newWindow;
        this.nextSongID ++;
        newWindow.start();
    }

    private void removeSong() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock.lock();
                    int id = window.getSelectedSongID();
                    int songIndex = getCurrentSongIndex(id);
                    if (songIndex == queueArray.length-1) {stopSong();}
                    else if (songIndex == selectedSongIndex) {nextSong();}
                    String[][] newQueueList = new String[queueArray.length - 1][7];
                    for (int index = 0; index < queueArray.length-1; index++) {
                        if (index < songIndex) {newQueueList[index] = queueArray[index];}
                        else {newQueueList[index] = queueArray[index+1];}
                    }
                    queueArray = newQueueList;
                    window.updateQueueList(newQueueList);
                    selectedSongIndex -= 1;
                    currentSongID = Integer.parseInt(queueArray[selectedSongIndex][6]);
                }
                finally {
                    lock.unlock();
                }
            }
        }).start();
    }

    private void updateCurrentSong(String[] song) {
        stopSong();
        try {
            lock.lock();
            this.window.updatePlayingSongInfo(song[0], song[1], song[2]);
            this.songLengthInSeconds = Integer.parseInt(song[5]);
            this.currentSongID = Integer.parseInt(song[6]);
            this.window.enableScrubberArea();
        }
        finally {
            lock.unlock();
        }
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
        try {
            lock.lock();
            String[] song = queueArray[index];
            this.selectedSongIndex = index;
            updateCurrentSong(song);
        }
        finally {
            lock.unlock();
        }

    }

    public void nextSong() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int index = selectedSongIndex;
                    if (shuffle) {
                        do {
                            index = random.nextInt(queueArray.length - 1);
                        } while (index == selectedSongIndex && queueArray.length > 1);
                    } else {
                        if (index != queueArray.length - 1) {
                            index = index + 1;
                        } else {
                            index = 0;
                        }
                    }
                    boolean wasPlaying = somethingIsPlaying;
                    updateSong(index);
                    if (wasPlaying) {
                        playAndPause();
                    }
                }
                finally {
                    lock.unlock();
                }
            }
        }).start();
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
        try {
            lock.lock();
            int id = this.window.getSelectedSongID();
            currentSongID = id;
            int index = getCurrentSongIndex(id);
            this.updateSong(index);
            this.playAndPause();
        }
        finally {
            lock.unlock();
        }
    }

    private void stopSong() {
        try {
            lock.lock();
            this.window.resetMiniPlayer();
            if (scrollerThread != null) scrollerThread.interrupt();
            somethingIsPlaying = false;
            this.currentSongID = -1;
        }
        finally {
            lock.unlock();
        }

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

