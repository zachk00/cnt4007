package torent;

import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Common {

    private int numOfPreferredNeighbors;
    private int unchokingInterval;
    private int optimisticChokingInterval;
    private String filename;
    private int fileSize;
    private int pieceSize;
    private int numOfPieces;
    private int[][] matrix;


    public Common() {
        readConfigFile("Common.cfg");
    }


    public int getNumOfPreferredNeighbors() {
        return numOfPreferredNeighbors;
    }

    public void setNumOfPreferredNeighbors(int numOfPreferredNeighbors) {
        this.numOfPreferredNeighbors = numOfPreferredNeighbors;
    }

    public int getUnchokingInterval() {
        return unchokingInterval;
    }

    public void setUnchokingInterval(int unchokingInterval) {
        this.unchokingInterval = unchokingInterval;
    }

    public int getOptimisticChokingInterval() {
        return optimisticChokingInterval;
    }

    public void setOptimisticChokingInterval(int optimisticChokingInterval) {
        this.optimisticChokingInterval = optimisticChokingInterval;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public int getPieceSize() {
        return pieceSize;
    }

    public void setPieceSize(int pieceSize) {
        this.pieceSize = pieceSize;
    }


    private void readConfigFile(String file) {
        BufferedReader reader;
        try {


            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            String[] commonInfo = line.split(" ");
            String name = commonInfo[0];
            String value = commonInfo[1];

            if (name.equals("NumberOfPreferredNeighbors")) {
                setNumOfPreferredNeighbors(Integer.parseInt(value));
            } else if (name.equals("UnchokingInterval")) {
                setUnchokingInterval(Integer.parseInt(value));

            } else if (name.equals("OptimisticUnchokingInterval")) {
                setOptimisticChokingInterval(Integer.parseInt(value));

            } else if (name.equals("FileName")) {
                setFilename(value);

            } else if (name.equals("FileSize")) {
                setFileSize(Integer.parseInt(value));

            } else if (name.equals("PieceSize")) {
                setPieceSize(Integer.parseInt(value));
            }

            while(line!= null){
                line = reader.readLine();
                commonInfo = line.split(" ");
                name = commonInfo[0];
                value = commonInfo[1];

                if (name.equals("NumberOfPreferredNeighbors")) {
                    setNumOfPreferredNeighbors(Integer.parseInt(value));
                } else if (name.equals("UnchokingInterval")) {
                    setUnchokingInterval(Integer.parseInt(value));

                } else if (name.equals("OptimisticUnchokingInterval")) {
                    setOptimisticChokingInterval(Integer.parseInt(value));

                } else if (name.equals("FileName")) {
                    setFilename(value);

                } else if (name.equals("FileSize")) {
                    setFileSize(Integer.parseInt(value));

                } else if (name.equals("PieceSize")) {
                    setPieceSize(Integer.parseInt(value));
                }
            }

            numOfPieces = (getFileSize()/getPieceSize()) + 1;
            matrix = new int[numOfPieces][2];



            
            
            
            
            
            
            
            
            
            


        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
