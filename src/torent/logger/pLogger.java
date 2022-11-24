package torent.logger;

import java.util.logging.Handler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.Level;

import java.io.IOException;

public class pLogger {
    Logger logger = Logger.getLogger(pLogger.class.getName());

    String peerId;
    String logFilePath;
    Handler fileHandler;

    Level pLevel = Level.INFO;

    // ======================================== //
    //               Constructor                //
    // ======================================== //
    public pLogger(String _peerId) {
        try {
            this.peerId = _peerId;
            
            this.logFilePath = "./log_peer_" + this.peerId + ".log";
            
            this.fileHandler = new FileHandler(logFilePath);
            this.fileHandler.setFormatter(new pFormatter());
            this.logger.addHandler(fileHandler);
            
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    // ======================================== //
    //                 Getters                  //
    // ======================================== //
    public String getPeerId() {
        return this.peerId;
    }

    public String getLogFilePath() {
        return this.logFilePath;
    }

    // ======================================== //
    //          Logging Functionality           //
    // ======================================== //
    public void log(Level level, String msg) {
        logger.log(level, msg);
    }

    public void sentTCP(String peerId2) {
        logger.log(pLevel, String.format("Peer %s makes a connection to Peer %s.", this.peerId, peerId2));
    }

    public void recTCP(String peerId2) {
        logger.log(pLevel, String.format("Peer %s is connected from Peer %s.", this.peerId, peerId2));
    }

    public void changePrefNeighbors(String[] neighbors) {
        String neighs = neighbors[0];

        for (int i = 1; i < neighbors.length; i++) {
            neighs += ", " + neighbors[i];
        }

        logger.log(pLevel, String.format("Peer %s has the preferred neighbors %s.", this.peerId, neighs));
    }

    public void changeOptimUnchokedNeighbor(String peerId2) {
        logger.log(pLevel, String.format("Peer %s has the optimistically unchoked neighbor %s.", this.peerId, peerId2));
    }

    public void unchoked(String peerId2) {
        logger.log(pLevel, String.format("Peer %s is unchoked by %s.", this.peerId, peerId2));
    }

    public void choked(String peerId2) {
        logger.log(pLevel, String.format("Peer %s is choked by %s.", this.peerId, peerId2));
    }

    // TODO: Possibly change pieceIdx from String -> int, depending on implementation
    public void recHave(String peerId2, String pieceIdx) {
        logger.log(pLevel, String.format("Peer %s received the 'have' message from %s for the piece %s.", this.peerId, peerId2, pieceIdx));
    }

    public void recInterested(String peerId2) {
        logger.log(pLevel, String.format("Peer %s received the 'interested' message from %s.", this.peerId, peerId2));
    }

    public void recNotInterested(String peerId2) {
        logger.log(pLevel, String.format("Peer %s received the 'not interested' message from %s.", this.peerId, peerId2));
    }

    // TODO: Possibly change pieceIdx and numPieces from String -> int, depending on implementation
    public void pieceDownloaded(String peerId2, String pieceIdx, String numPieces) {
        logger.log(pLevel, String.format("Peer %s has downloaded the piece %s from %s. Now the number of pieces it has is %s.", this.peerId, pieceIdx, peerId2, numPieces));
    }

    public void fileDownloaded() {
        logger.log(pLevel, String.format("Peer %s has downloaded the complete file.", this.peerId));
    }

    // ======================================== //
    //             Testing of pLog              //
    // ======================================== //
    public static void main(String[] args) {

        try {
            pLogger testpLog = new pLogger("0001");
            String peerId2 = "0002";
            String[] neighbors = {"0002", "0003", "0004"};
            String pieceIdx = "5";
            String numPieces = "50";
            for (int i = 0; i < 1; i++) {
                testpLog.sentTCP(peerId2);
                testpLog.recTCP(peerId2);
                testpLog.changePrefNeighbors(neighbors);
                testpLog.changeOptimUnchokedNeighbor(peerId2);
                testpLog.unchoked(peerId2);
                testpLog.choked(peerId2);
                testpLog.recHave(peerId2,pieceIdx);
                testpLog.recInterested(peerId2);
                testpLog.recNotInterested(peerId2);
                testpLog.pieceDownloaded(peerId2, pieceIdx, numPieces);
                testpLog.fileDownloaded();
            }
            
            // Log at CONFIG level, confirm levels set properly for log file
            testpLog.log(Level.CONFIG, "Config Data");
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}