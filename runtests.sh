#!/bin/bash
set -- "my"

# Imposta percorsi assoluti
REPORT_DIR="$PWD/$1.report"
OUTPUTDIR="$PWD/test_files/c_out"

# Crea directory di report
mkdir -p "$REPORT_DIR"
if [ $? -ne 0 ]; then
    echo "Errore: Impossibile creare $REPORT_DIR"
    exit 1
fi

RESULT=0

# Crea directory di output con permessi espliciti
mkdir -p "$OUTPUTDIR"
chmod -R 755 "$OUTPUTDIR"

for TESTDIR in tests/*; do
    TESTNAME=$(basename -- "$TESTDIR")
    TESTFILE="$TESTDIR/$TESTNAME.txt"

    # Verifica esistenza file di test
    if [ ! -f "$TESTFILE" ]; then
        echo "File $TESTFILE non trovato!" >> "$REPORT_DIR/$1.report.txt"
        RESULT=1
        continue
    fi

    TESTFILE_ABSOLUTE="$PWD/$TESTFILE"

    echo -e "\n\nTest name: $TESTDIR" >> "$REPORT_DIR/$1.report.txt"
    echo "mvn exec:java -Dexec.args=\"$TESTFILE_ABSOLUTE\"" >> "$REPORT_DIR/$1.report.txt"

    # Compila ed esegui
    mvn clean compile
    mvn --batch-mode -q exec:java -Dexec.args="$TESTFILE_ABSOLUTE" >> "$REPORT_DIR/$1.report.txt" 2>&1
    MVN_EXIT_CODE=$?

    if [ $MVN_EXIT_CODE -ne 0 ]; then
        echo "Errore Maven ($MVN_EXIT_CODE) per $TESTFILE" >> "$REPORT_DIR/$1.report.txt"
        RESULT=1
        continue
    fi

    if [ -s "$OUTPUTDIR/$TESTNAME.c" ]; then
        EXEFILE="$OUTPUTDIR/$TESTNAME.out"
        echo -e "\ngcc $OUTPUTDIR/$TESTNAME.c -o $EXEFILE -lm -w" >> "$REPORT_DIR/$1.report.txt"
        gcc "$OUTPUTDIR/$TESTNAME.c" -o "$EXEFILE" -lm -w >> "$REPORT_DIR/$1.report.txt" 2>&1

        for TESTIN in "$TESTDIR/$TESTNAME"_in*; do
            TESTINNAME=$(basename -- "$TESTIN")
            TESTOUT="$OUTPUTDIR/${TESTINNAME/_in/_out}"
            if [ -s "$EXEFILE" ]; then
                "$EXEFILE" < "$TESTIN" &> "$TESTOUT"
            else
                RESULT=1
            fi
            echo -e "\ndiff -w ${TESTIN/_in/_out} $TESTOUT" >> "$REPORT_DIR/$1.report.txt"
            diff -w "${TESTIN/_in/_out}" "$TESTOUT" >> "$REPORT_DIR/$1.report.txt"
        done
    else
        echo "$OUTPUTDIR/$TESTNAME.c non esiste" >> "$REPORT_DIR/$1.report.txt"
        if [[ $TESTNAME != *invalid* ]]; then
            RESULT=1
        fi
    fi
done

# Sposta i risultati nel report
mv "$OUTPUTDIR" "$REPORT_DIR/$(dirname "$OUTPUTDIR")"
mv "$REPORT_DIR/$1.report.txt" "$REPORT_DIR"

# Filtra i warning noti (opzionale)
echo 'WARNING: An illegal reflective access operation has occurred' > "$REPORT_DIR/skip.txt"
echo 'WARNING: Illegal reflective access by com.google.inject.internal.cglib.core.$ReflectUtils$1 (file:/usr/share/maven/lib/guice.jar) to method java.lang.ClassLoader.defineClass(java.lang.String,byte[],int,int,java.security.ProtectionDomain)' >> "$REPORT_DIR/skip.txt"
echo 'WARNING: Please consider reporting this to the maintainers of com.google.inject.internal.cglib.core.$ReflectUtils$1' >> "$REPORT_DIR/skip.txt"
echo 'WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations' >> "$REPORT_DIR/skip.txt"
echo 'WARNING: All illegal access operations will be denied in a future release' >> "$REPORT_DIR/skip.txt"

# Mostra il report senza i warning noti
grep -v -x -F -f "$REPORT_DIR/skip.txt" "$REPORT_DIR/$1.report.txt"

exit $RESULT