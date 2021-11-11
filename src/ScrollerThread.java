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
            for (int i = starting; i < length; i++) {
                window.updateMiniplayer(true, true, false, i, length, 0, 0);
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    window.updateMiniplayer(true, false, false, i, length, 0, 0);
                    return;
                }
            }
        }
    }

