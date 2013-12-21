package com.wordpress.nprogramming;

import com.mongodb.*;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;

public class TodoApp {
    private static Logger log = LoggerFactory.getLogger(TodoApp.class);

    public static void main(String[] args) throws UnknownHostException {
        CommandLineParser parser = new BasicParser();

        try {
            Options options = new Options();
            options.addOption("a", "add", true, "Add todo item.");
            options.addOption("l", "list", false, "List all todo items.");
            options.addOption("d", "delete", true, "Delete todo item.");

            CommandLine line = parser.parse(options, args);

            new TodoEngine(line).process(options);

            log.info("Work Done");
        } catch (ParseException exp) {
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
        }
    }
}

class TodoEngine {

    private static Logger log = LoggerFactory.getLogger(TodoEngine.class);

    private final CommandLine commandLine;

    public TodoEngine(CommandLine commandLine) {

        this.commandLine = commandLine;
    }

    public void process(Options options) throws UnknownHostException {

        MongoClient mongoClient = new MongoClient();
        DB db = mongoClient.getDB("mydb");

        DBCollection todoItems = db.getCollection("todoItems");

        if (commandLine.hasOption("add")) {
            String value = commandLine.getOptionValue("add");
            log.info("Add " + value);
            todoItems.insert(new BasicDBObject("value", value));
        } else if (commandLine.hasOption("list")) {
            log.info("Listing items");

            try (DBCursor cursor = todoItems.find()) {
                while (cursor.hasNext())
                    System.out.println(cursor.next().get("value"));
            }
        } else if (commandLine.hasOption("delete")) {
            String value = commandLine.getOptionValue("delete");
            log.info("Delete " + value);
            todoItems.remove(new BasicDBObject("value", value));
        } else {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("TodoApp", options);
        }
    }
}