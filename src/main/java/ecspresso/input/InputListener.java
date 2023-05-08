package ecspresso.input;

import java.util.Scanner;

public class InputListener extends Thread {
    private final IStoppableThread thread;

    public InputListener(IStoppableThread thread) {
        this.thread = thread;
    }

    @Override
    public void run() {
        System.out.println("Press enter to exit the application.");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        thread.stopRunning();
    }
}
