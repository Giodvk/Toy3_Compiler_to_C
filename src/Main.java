import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

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

        String inputFile = args[0];

        try {
            // Apri il file di input
            System.out.println("Leggendo il file: " + inputFile);
            FileReader fileReader = new FileReader(inputFile);
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
            TypeChecking typeChecking = new TypeChecking(program);
            program.accept(typeChecking);
            WriteCVisitor writeCVisitor = new WriteCVisitor();
            program.accept(writeCVisitor);

            // Chiudi il file
            bufferedReader.close();

        } catch (IOException e) {
            System.err.println("Errore nella lettura del file: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Errore durante il parsing o l'elaborazione: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

