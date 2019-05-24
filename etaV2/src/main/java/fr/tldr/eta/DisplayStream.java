package fr.tldr.eta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * test if the file exist and if the the content is different
 *
 * @return {@code true} if the file is empty and can be replace, {@code false} otherwise
 */
public class DisplayStream implements Runnable {

    private final InputStream inputStream;

    public DisplayStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    private BufferedReader getBufferedReader(InputStream is) {
        return new BufferedReader(new InputStreamReader(is));
    }

    @Override
    public void run() {

        Thread thread = Thread.currentThread();
        BufferedReader br = getBufferedReader(inputStream);
        String ligne;
        try {
            while ((ligne = br.readLine()) != null) {
                System.out.println(ligne);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        thread.interrupt();
    }
}

