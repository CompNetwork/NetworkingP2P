package main.config.reader;

import main.config.pod.CommonConfigData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Map;
import java.util.Scanner;

public class CommonConfigReader {
    private CommonConfigData data;
    public CommonConfigData getData() {
        return data;
    }

    private abstract class FillProperty {
        public String myKeyword;
        public FillProperty(String myKeyword) {
            this.myKeyword = myKeyword;
        }

        boolean calledStore = false;
        public void store(String keyword, Scanner sc) {
            if ( calledStore ) {
                throw new IllegalArgumentException("Error, same keyword found twice in the config file! Keyword was : " + keyword);
            }
            calledStore = true;
            doCalledStore(sc);
        }
        protected abstract void doCalledStore(Scanner sc);
        public void write(CommonConfigData.CommonConfigDataBuilder builder) {
            if ( !calledStore ) {
                throw new IllegalArgumentException("Error, a keyword that was expected to be in the config file, did not appear! Keyword expected but not found was: " + myKeyword);
            }
            doCalledWrite(builder);
        }
        protected abstract void doCalledWrite(CommonConfigData.CommonConfigDataBuilder builder);
    }

    private Map<String, FillProperty> propertyMap = new HashMap<>();

    // Throws either FileNotFound if the file doesn't exist, or IllegalArgumentException if the file is formatted incorrectly.
    public CommonConfigReader(File f) throws FileNotFoundException, IllegalArgumentException {
        populatePropertyMap();
        Scanner sc = new Scanner(f);
        for(int currentLineIdxs = 0; sc.hasNextLine(); ++currentLineIdxs) {
            Scanner lineScanner = new Scanner(sc.nextLine());
            String keyword = lineScanner.next();
            FillProperty fillProp = propertyMap.get(keyword);
            if (fillProp == null) {
                throw new IllegalArgumentException("Error, unrecognized keyword found in common config file!" +
                        " Error found on line:" + currentLineIdxs
                        + ((lineScanner.hasNext()) ? "\nLine Contents are: " + lineScanner.next() : "\nExtra blank line found"));
            }
            try {
                fillProp.store(keyword, lineScanner);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Error occured parsing file, on line:" + currentLineIdxs
                        + ((lineScanner.hasNext()) ? "\nRemaining Line Contents are: " + lineScanner.next() : "\nNo value found in line.") + e.toString());
            }
        }
        sc.close();
        CommonConfigData.CommonConfigDataBuilder builder = CommonConfigData.getBuilder();
        for (FillProperty fillProperty : propertyMap.values() ) {
            fillProperty.write(builder);
        }
        data = builder.build();
    }

    private void populatePropertyMap() {
       propertyMap.put("NumberOfPreferredNeighbors", new FillProperty("NumberOfPreferredNeighbors") {
           int value = 0;
           @Override
           protected void doCalledStore(Scanner sc) {
                if ( sc.hasNextInt()) {
                    value = sc.nextInt();
                } else {
                    throw new IllegalArgumentException("Expected a integer after keyword NumberOfPreferredNeighbors! " + sc.next());
                }
           }

           @Override
           protected void doCalledWrite(CommonConfigData.CommonConfigDataBuilder builder) {
                builder.withNumberPreferredNeighbors(value);
           }
       });

        propertyMap.put("UnchokingInterval", new FillProperty("UnchokingInterval") {
            int value = 0;
            @Override
            protected void doCalledStore(Scanner sc) {
                if ( sc.hasNextInt()) {
                    value = sc.nextInt();
                } else {
                    throw new IllegalArgumentException("Expected a integer after keyword UnchokingInterval");
                }
            }

            @Override
            protected void doCalledWrite(CommonConfigData.CommonConfigDataBuilder builder) {
                builder.withUnchokeInterval(value);
            }
        });

        propertyMap.put("OptimisticUnchokingInterval", new FillProperty("OptimisticUnchokingInterval") {
            int value = 0;
            @Override
            protected void doCalledStore(Scanner sc) {
                if ( sc.hasNextInt()) {
                    value = sc.nextInt();
                } else {
                    throw new IllegalArgumentException("Expected a integer after keyword OptimisticUnchokingInterval");
                }
            }

            @Override
            protected void doCalledWrite(CommonConfigData.CommonConfigDataBuilder builder) {
                builder.withOptimisticUnchokeInterval(value);
            }
        });


        propertyMap.put("FileName", new FillProperty("FileName") {
            String value;
            @Override
            protected void doCalledStore(Scanner sc) {
                if ( sc.hasNext()) {
                    value = sc.next();
                } else {
                    throw new IllegalArgumentException("Expected a name after keyword FileName");
                }
            }

            @Override
            protected void doCalledWrite(CommonConfigData.CommonConfigDataBuilder builder) {
                builder.withFileName(value);
            }
        });


        propertyMap.put("FileSize", new FillProperty("FileSize") {
            int value = 0;
            @Override
            protected void doCalledStore(Scanner sc) {
                if ( sc.hasNextInt()) {
                    value = sc.nextInt();
                } else {
                    throw new IllegalArgumentException("Expected a integer after keyword FileSize");
                }
            }

            @Override
            protected void doCalledWrite(CommonConfigData.CommonConfigDataBuilder builder) {
                builder.withFileSize(value);
            }
        });


        propertyMap.put("PieceSize", new FillProperty("PieceSize") {
            int value = 0;
            @Override
            protected void doCalledStore(Scanner sc) {
                if ( sc.hasNextInt()) {
                    value = sc.nextInt();
                } else {
                    throw new IllegalArgumentException("Expected a integer after keyword PieceSize");
                }
            }

            @Override
            protected void doCalledWrite(CommonConfigData.CommonConfigDataBuilder builder) {
                builder.withPieceSize(value);
            }
        });

    }
}
