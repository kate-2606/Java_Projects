package edu.uob;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

/** This class implements the DB server. */
public class DBServer {

    private static final char END_OF_TRANSMISSION = 4;
    private static String storageFolderPath;

    public static void main(String args[]) throws IOException {
        DBServer server = new DBServer();
        server.blockingListenOn(8888);
    }

    /**
    * KEEP this signature otherwise we won't be able to mark your submission correctly.
    */
    public DBServer() {
        storageFolderPath = Paths.get("databases").toAbsolutePath().toString();
        try {
            // Create the database storage folder if it doesn't already exist !
            Files.createDirectories(Paths.get(storageFolderPath));
        } catch(IOException ioe) {
            System.out.println("Can't seem to create database storage folder " + storageFolderPath);
        }
    }

    /**
    * KEEP this signature (i.e. {@code edu.uob.DBServer.handleCommand(String)}) otherwise we won't be
    * able to mark your submission correctly.
    *
    * <p>This method handles all incoming DB commands and carries out the required actions.
    */



    public String handleCommand(String command) {
        ArrayList<Token> tokens = new ArrayList<>();

        Lexer lexer = new Lexer();
        lexer.Lexer(command, tokens);

        Parser parser = new Parser();
        parser.Parser(tokens, lexer);

        String result="";

        if (parser.getParserResult()) {
            result ="[OK]";
            Interpreter interpreter = new Interpreter();
            interpreter.Interpreter(storageFolderPath, tokens);
        }

        return result;

    }
/*
    //change this exception?
    //how to test a private method -- this should be private:
    public Table readFile(String fileName) throws FileNotFoundException {

        Table readTable = null;

        File fileToOpen = new File(storageFolderPath + File.separator + fileName);

        if (!(fileToOpen.isDirectory())) {
            try {
                FileReader reader = new FileReader(fileToOpen);
                BufferedReader buffReader = new BufferedReader(reader);
                readTable = new Table(fileName, buffReader);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return readTable;

    }
 */


    public void exportTable(String fileLocation, Table tableToExport) {
        File fileToOpen = new File(fileLocation);

        if (!(fileToOpen.isDirectory() && fileToOpen.exists())) {
            try {
                FileWriter writer = new FileWriter(fileToOpen);
                BufferedWriter buffWriter = new BufferedWriter(writer);

                int numberOfRows = tableToExport.getNumberOfDataRows();

                buffWriter.write(tableToExport.attributesToString() + "\n");

                for (int i = 0; i < numberOfRows; i++) {
                    buffWriter.write(tableToExport.rowToString(i) + "\n");
                }
                buffWriter.flush();
                buffWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    //  === Methods below handle networking aspects of the project - you will not need to change these ! ===

    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.err.println("Server encountered a non-fatal IO error:");
                    e.printStackTrace();
                    System.err.println("Continuing...");
                }
            }
        }
    }

    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {

            System.out.println("Connection established: " + serverSocket.getInetAddress());
            while (!Thread.interrupted()) {
                String incomingCommand = reader.readLine();
                System.out.println("Received message: " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
    }
}
