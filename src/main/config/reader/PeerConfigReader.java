package main.config.reader;

import main.config.pod.PeerConfigData;

import java.io.File;
import java.util.ArrayList;

public class PeerConfigReader {
    ArrayList<PeerConfigData> data;
    ArrayList<PeerConfigData> getPeerConfigDatas() {
        return data;
    }

    PeerConfigReader(File configFile) {
        // Perform the read and parse.

    }

}
