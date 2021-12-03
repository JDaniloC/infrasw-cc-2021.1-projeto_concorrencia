import ui.AddSongWindow;
import ui.PlayerWindow;

public class ScrollerThread extends Thread{

    private final PlayerWindow window;
    private final Player player;
    private int length;
    private int starting;
    private int songIndex;
    private int queueSize;
    private boolean repeat;

    public ScrollerThread(PlayerWindow window, Player player,
                          boolean repeat, int starting, int length,
                          int songIndex, int queueSize) {
        this.window = window;
        this.player = player;
        this.length = length;
        this.repeat = repeat;
        this.starting = starting;
        this.songIndex = songIndex;
        this.queueSize = queueSize;
    }

    @Override
    public void run() {
            boolean wasInterrupted = false;
            for (int second = starting; second < length; second++) {
                window.updateMiniplayer(
                        true,
                        true,
                        this.repeat,
                        second,
                        length,
                        this.songIndex,
                        this.queueSize
                );
                try {
                    //noinspection BusyWait
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    second = length;
                    wasInterrupted = true;
                }
            }
            if (!wasInterrupted) {
                window.updateMiniplayer(
                        true,
                        false,
                        this.repeat,
                        length,
                        length,
                        this.songIndex,
                        this.queueSize
                );
                if ((this.repeat && this.songIndex == this.queueSize - 1)
                        || this.songIndex < this.queueSize - 1) {
                    try {
                        Thread.sleep(1000);
                        this.player.nextSong();
                    } catch (InterruptedException e) {
                        // Do nothing
                    }
                }
                else {
                    window.updateMiniplayer(
                            false,
                            false,
                            this.repeat,
                            length,
                            length,
                            this.songIndex,
                            this.queueSize
                    );
                }
            }
        }
    }

