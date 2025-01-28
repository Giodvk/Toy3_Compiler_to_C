import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import classNode.main.ProgramOp;
import java_cup.runtime.Symbol;
import esercitazione4.*;
import visitors.PrintTreeVisitor;
import visitors.ScopeVisitor;
import visitors.TypeChecking;
import visitors.WriteCVisitor;

public class Main {
    public static void main(String[] args) {
        // Verifica che venga passato un file come argomento
        if (args.length != 1) {
            System.err.println("Utilizzo: java Main <input_file>");
            System.exit(1);
        }

        String fullInputFilePath = args[0];

        // Estrai solo il nome del file dal percorso completo
        Path path = Paths.get(fullInputFilePath);
        String inputFileName = path.getFileName().toString();  // Ottieni solo il nome del file (es. test1.txt)


        // Percorso di output
        String outputDirectory = "test_files"+ File.separator + "c_out"+ File.separator;


        String Cfile = inputFileName.replace(".txt", ".c");
        // Costruisci il percorso completo di output
        String outputFilePath = outputDirectory + Cfile;

        try {
            // Apri il file di input
            System.out.println("Leggendo il file: " + fullInputFilePath);
            FileReader fileReader = new FileReader(fullInputFilePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            // Crea il lexer passando il BufferedReader
            LexerAnalyzer lexer = new LexerAnalyzer(bufferedReader);

            // Crea il parser passando il lexer
            parser parser = new parser(lexer);

            // Avvia il parsing e ottieni il nodo radice
            Symbol result = parser.parse();
            ProgramOp program = (ProgramOp) result.value;

            // Stampa l'albero sintattico utilizzando PrintVisitor
            System.out.println("\n--- Albero Sintattico ---");
            PrintTreeVisitor printVisitor = new PrintTreeVisitor();
            program.accept(printVisitor);

            // Esegui il controllo dei tipi
            TypeChecking typeChecking = new TypeChecking(program);
            program.accept(typeChecking);


            // Scrivi il risultato in un file C
            WriteCVisitor writeCVisitor = new WriteCVisitor(outputFilePath);
            program.accept(writeCVisitor);

            // Chiudi il file
            bufferedReader.close();

            // Log dell'output
            System.out.println("Output salvato in: " + outputFilePath);

        } catch (IOException e) {
            System.err.println("Errore nella lettura del file: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Errore durante il parsing o l'elaborazione: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
