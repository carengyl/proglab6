package serverCommandLine;

import UDPutil.Request;
import UDPutil.Response;
import commands.AbstractCommand;
import commands.CommandArgument;
import commands.CommandData;
import commonUtil.OutputUtil;
import exceptions.InvalidNumberOfArgsException;
import exceptions.NoUserInputException;
import exceptions.ValidationException;

import java.util.HashMap;
import java.util.Optional;

public class Invoker {
    private final HashMap<String, CommandData> CLIENT_SENDING_COMMANDS = new HashMap<>();
    private final HashMap<String, AbstractCommand> SERVER_AVAILABLE_COMMAND = new HashMap<>();

    private final HashMap<String, AbstractCommand> CLIENT_AVAILABLE_COMMAND = new HashMap<>();

    public void addServerCommand(AbstractCommand command) {
        this.SERVER_AVAILABLE_COMMAND.put(command.getCommandData().commandName(), command);
    }

    public void addClientCommand(AbstractCommand command) {
        this.CLIENT_AVAILABLE_COMMAND.put(command.getCommandData().commandName(), command);
        this.CLIENT_SENDING_COMMANDS.put(command.getCommandData().commandName(), command.getCommandData());
    }
    public Response executeClientCommand(Request request) {
        AbstractCommand executableCommand = CLIENT_AVAILABLE_COMMAND.get(request.getCommandName());
        Optional<Response> optionalResponse = Optional.empty();
        try {
            optionalResponse = executableCommand.executeCommand(request.getCommandArgument());
        } catch (NoUserInputException e) {
            //Suppress exception, because there will be no input from user
            e.getSuppressed();
        }
        //Suppress warning, because optional will always be present
        //noinspection OptionalGetWithoutIsPresent
        return optionalResponse.get();
    }

    public void executeServerCommand(String commandName, CommandArgument argument) throws NoUserInputException {
        if (SERVER_AVAILABLE_COMMAND.containsKey(commandName)) {
            try {
                AbstractCommand executableCommand = SERVER_AVAILABLE_COMMAND.get(commandName);
                CommandArgument validatedArg = executableCommand.validateArguments(argument, executableCommand.getCommandData());
                executableCommand.executeCommand(validatedArg);
            } catch (ValidationException | InvalidNumberOfArgsException e) {
                OutputUtil.printErrorMessage(e.getMessage());
            }
        } else {
            OutputUtil.printErrorMessage("Command " + commandName + " not found. Type \"help\" to see available commands.");
        }
    }

    public HashMap<String, AbstractCommand> getSERVER_AVAILABLE_COMMAND() {
        return SERVER_AVAILABLE_COMMAND;
    }

    public HashMap<String, CommandData> getClientSendingCommand() {
        return CLIENT_SENDING_COMMANDS;
    }
}
