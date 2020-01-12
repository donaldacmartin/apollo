package scot.martin.apollo;

import scot.martin.apollo.worker.Downloader;

import java.util.Optional;
import java.util.concurrent.Callable;

public class Main {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: MP3_STREAM SECONDS MAX_BYTES?");
            System.exit(-1);
        }

        try {
            String mp3Stream = args[0];
            long seconds = Long.parseLong(args[1]);
            long millis = seconds * 1000;
            long sizeLimit = args.length > 2 ? Long.parseLong(args[2]) : Long.MAX_VALUE;

            Callable<Optional<String>> downloader = new Downloader(mp3Stream, millis, sizeLimit);
            Optional<String> fileName = downloader.call();

            if (fileName.isPresent()) {
                System.out.println(fileName.get());
            } else {
                System.out.println("Unable to create file");
            }
        } catch (NumberFormatException n) {
            System.out.println("Invalid number of seconds");
            System.exit(-1);
        } catch (Exception e) {
            System.out.println("Error while streaming");
            System.exit(-1);
        }
    }
}
