import ui.AddSongWindow;
import ui.PlayerWindow;

public class ScrollerThread extends Thread{

    private final PlayerWindow window;
    private int length;
    private int starting;

    public ScrollerThread(PlayerWindow window, int starting, int length) {
        this.window = window;
        this.length = length;
        this.starting = starting;
    }

    @Override
    public void run() {
            boolean wasInterrupted = false;
            for (int second = starting; second < length; second++) {
                window.updateMiniplayer(
                        true,
                        true,
                        false,
                        second,
                        length,
                        0,
                        0
                );
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    second = length;
                    wasInterrupted = true;
                }
            }
            if (!wasInterrupted) {
                window.updateMiniplayer(
                        true,
                        false,
                        false,
                        length,
                        length,
                        0,
                        0
                );
            }
        }
    }

