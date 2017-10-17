package main.config.reader;

import main.config.pod.PeerConfigData;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class PeerConfigReader {
    ArrayList<PeerConfigData> data = new ArrayList<>();
    ArrayList<PeerConfigData> getPeerConfigDatas() {
        return data;
    }

    private String getFieldExceptionMessage(int lineIdx, String fieldMissing, String line, String error) {
        return "On line:" + lineIdx + ", Field:" + fieldMissing + ", is " + error + ". \n \t Line is:" + line;
    }

    private PeerConfigData getPeerConfigDataFromLine(String line, int currentLineIdx) {
        Scanner lineScanner = new Scanner(line);
        int peerId = 0;
        if (lineScanner.hasNextInt()) {
            peerId = lineScanner.nextInt();
            if (peerId < 0) {
                throw new IllegalArgumentException(getFieldExceptionMessage(currentLineIdx, "PeerId", line, "a negative value"));
            }
        } else {
                throw new IllegalArgumentException(getFieldExceptionMessage(currentLineIdx, "PeerId", line, "missing"));
        }

        String hostName = "";
        if (lineScanner.hasNext()) {
            hostName = lineScanner.next();
        } else {
            throw new IllegalArgumentException(getFieldExceptionMessage(currentLineIdx,"HostName",line, "missing"));
        }

        int listeningPort = 0;
        if (lineScanner.hasNextInt()) {
            listeningPort = lineScanner.nextInt();
            if (listeningPort < 0) {
                throw new IllegalArgumentException(getFieldExceptionMessage(currentLineIdx, "Listening Port", line, "a negative value"));
            }
        } else {
            throw new IllegalArgumentException(getFieldExceptionMessage(currentLineIdx,"ListeningPort",line, "missing"));
        }

        boolean hasFile = false;
        if (lineScanner.hasNextInt()) {
            int hasFileRawInt = lineScanner.nextInt();
            if (hasFileRawInt < 0) {
                throw new IllegalArgumentException(getFieldExceptionMessage(currentLineIdx, "hasFile", line, "a negative value"));
            }
            hasFile = (hasFileRawInt != 0);

        } else {
            throw new IllegalArgumentException(getFieldExceptionMessage(currentLineIdx,"hasFile",line, "missing"));
        }

        if ( lineScanner.hasNext() ) {
            throw new IllegalArgumentException(getFieldExceptionMessage(currentLineIdx,"N/A",line,"extra data on this line"));
        }

        return new PeerConfigData(peerId,hostName,listeningPort,hasFile);

    }

    // Throws either FileNotFound if the file doesn't exist, or IllegalArgumentException if the file is formatted incorrectly.
    PeerConfigReader(File configFile) throws FileNotFoundException, IllegalArgumentException {
        // Perform the read and parse.
        Scanner sc = new Scanner(configFile);
        for(int currentLineIdx = 0; sc.hasNext();++currentLineIdx) {
            data.add(getPeerConfigDataFromLine(sc.nextLine(),currentLineIdx));

        }
    }

}
