package serverUtil;

import commands.clientCommands.*;
import commonUtil.OutputUtil;
import commonUtil.Validators;
import entities.CollectionOfHumanBeings;
import exceptions.NoUserInputException;
import serverCommandLine.Invoker;
import serverCommandLine.ServerCommandReader;
import serverCommands.ServerExitCommand;
import serverCommands.ServerHelpCommand;
import serverCommands.ServerSaveCommand;
import threads.ConsoleThread;
import threads.RequestThread;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ServerHandler {
    private static boolean running = true;
    private final Scanner scanner = new Scanner(System.in);
    private final int MAX_PORT = 65535;
    private final int MIN_PORT = 1;
    private ServerSocketHandler socketHandler;
    private final ServerCommandReader commandReader;
    private final Invoker invoker;
    private final CollectionOfHumanBeings collection;

    public ServerHandler(CollectionOfHumanBeings collection) {
        this.collection = collection;
        try {
            this.socketHandler = new ServerSocketHandler();
        } catch (IOException e) {
            OutputUtil.printErrorMessage(e.getMessage());
        }
        this.invoker = new Invoker();
        initCommands();
        this.commandReader = new ServerCommandReader(invoker, scanner);
    }

    private void initCommands() {
        invoker.addClientCommand(new ClearCommand(collection));
        invoker.addClientCommand(new CountGreaterThanMoodCommand(collection));
        invoker.addClientCommand(new CountLessThanMinutesOfWaitingCommand(collection));
        invoker.addClientCommand(new FilterByCarCommand(collection));

        invoker.addClientCommand(new InfoCommand(collection));
        invoker.addClientCommand(new InsertCommand(collection));
        invoker.addClientCommand(new RemoveGreaterCommand(collection));
        invoker.addClientCommand(new RemoveKeyCommand(collection));

        invoker.addClientCommand(new RemoveLowerKeyCommand(collection));
        invoker.addClientCommand(new ShowCommand(collection));
        invoker.addClientCommand(new UpdateCommand(collection));
        invoker.addClientCommand(new ExecuteScriptCommand());

        invoker.addServerCommand(new ServerExitCommand());
        invoker.addServerCommand(new ServerSaveCommand(collection));
        invoker.addServerCommand(new ServerHelpCommand(invoker.getSERVER_AVAILABLE_COMMAND()));
    }

    public static boolean isRunning() {
        return running;
    }

    public static void toggleRunning() {
        running = !running;
    }

    public void startServerHandler() {
        inputPort();
        RequestThread requestThread = new RequestThread(invoker, socketHandler);
        ConsoleThread consoleThread = new ConsoleThread(commandReader);
        requestThread.start();
        consoleThread.start();
    }

    private void inputPort() {
        try {
            boolean useDefaultPort = Validators.validateBooleanInput("Do you want to use a default server port", scanner);
            if (!useDefaultPort) {
                Integer port = Validators.validateInput(arg -> (((int) arg) <= MAX_PORT) && (((int) arg) >= MIN_PORT),
                        "Enter remote host port, it should be in [" + MIN_PORT + ";" + MAX_PORT + "]",
                        "Expected integer value",
                        "Remote host port should be in [" + MIN_PORT + ";" + MAX_PORT + "]",
                        Integer::parseInt,
                        false,
                        scanner);
                //noinspection ConstantConditions, NullPointer handled in Validators class
                socketHandler.setSelfPort(port);
            }
        } catch (NoUserInputException | NoSuchElementException e) {
            OutputUtil.printErrorMessage(e.getMessage());
            System.exit(0);
        }
    }
}
