# Tutorial 6 — Log4j 2 (159.251 Software Design and Construction)

Solution for Tutorial 6: the `System.out` / `System.err` / `printStackTrace()` calls in
`MergeTransactions.java` have been replaced by **Apache Log4j 2** logging.

## What was done

1. Added the two Log4j 2 dependencies to `pom.xml` (version **2.26.1**):
   - `org.apache.logging.log4j:log4j-api`
   - `org.apache.logging.log4j:log4j-core`
2. Added `exec-maven-plugin` so the app can be run from the command line.
3. Replaced all console printing with two **named loggers**:
   | Logger        | Used for                                   | Output destinations (see `src/main/resources/log4j2.xml`) |
   |---------------|--------------------------------------------|----------------------------------------------------------|
   | `FILE`        | file-access events (which file is read, missing files, read/close errors) | **`logs.txt` + console** (plus `logs.csv` for the optional challenge) |
   | `TRANSACTIONS`| transaction data (each parsed purchase, the final summary) | **console only** |
4. Log levels follow the comments in the original code:
   | Comment                            | Level  | Logger       |
   |------------------------------------|--------|--------------|
   | `// print some info for the user`  | INFO   | TRANSACTIONS |
   | `// print info for user`           | INFO   | FILE         |
   | `// this is for debugging only`    | DEBUG  | TRANSACTIONS |
   | `// print warning`                 | WARN   | FILE         |
   | `// print error message and details`| ERROR | FILE         |
   Exceptions are passed as the second argument (`logger.error(msg, x)`) so the stack trace is
   logged, replacing `x.printStackTrace()`.
5. The layout is **PatternLayout** everywhere.
6. Optional challenge: a second file appender writes `logs.csv` in comma-separated format,
   openable directly in Excel / a spreadsheet program.

## How to run

From the project root:

```
mvn clean compile
mvn exec:java
```

Expected result: 8 transactions are imported (the second line of `transactions3.csv` fails
to parse because the date uses slashes `13/5/2018` instead of dashes, and `transactions4.csv`
does not exist on purpose — both are logged as ERROR / WARN), total value ≈ 840, max value = 240.

After running, look at:

- `logs.txt` — FILE logger output (file-access events only)
- `logs.csv` — the same events in CSV format
- the console — shows both FILE and TRANSACTIONS output

## Trying different log levels

Edit `src/main/resources/log4j2.xml`:

- Set `<Logger name="TRANSACTIONS" level="info">` to hide the DEBUG "imported transaction ..." lines.
- Set `<Logger name="FILE" level="warn">` to hide the INFO "import data from ..." lines.

## Project structure

```
Tutorial6_Log4J_solution/
├── pom.xml
├── README.md
├── transactions1.csv
├── transactions2.csv
├── transactions3.csv
└── src/
    └── main/
        ├── java/nz/ac/vuw/swen301/tuts/log4j/
        │   ├── MergeTransactions.java
        │   └── Purchase.java
        └── resources/
            └── log4j2.xml
```
