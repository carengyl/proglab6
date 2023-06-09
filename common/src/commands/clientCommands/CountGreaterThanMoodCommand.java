package commands.clientCommands;

import UDPutil.Response;
import commands.AbstractCommand;
import commands.CommandArgument;
import commands.CommandData;
import commonUtil.Validators;
import entities.Mood;
import exceptions.InvalidNumberOfArgsException;
import exceptions.NoUserInputException;
import exceptions.ValidationException;
import entities.CollectionOfHumanBeings;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

public class CountGreaterThanMoodCommand extends AbstractCommand implements Serializable {
    private final CollectionOfHumanBeings collection;
    public CountGreaterThanMoodCommand(CollectionOfHumanBeings collection) {
        super("count_greater_than_mood", "Count collection elements which mood is greater than @mood",1, "@mood from Mood enum");
        this.collection = collection;
    }

    @Override
    public Optional<Response> executeCommand(CommandArgument argument) throws NoUserInputException {
        Mood mood = Mood.getMoodByNumber(argument.getEnumNumber());
        int greaterMoods = 0;
        for (long key: collection.getHumanBeings().keySet()) {
            if (Objects.requireNonNull(mood).compareTo(collection.getHumanBeings().get(key).getMood()) < 0) {
                greaterMoods++;
            }
        }
        return Optional.of(new Response("People with Mood greater than " + mood + ":" + greaterMoods));
    }

    @Override
    public CommandArgument validateArguments(CommandArgument arguments, CommandData commandData) throws ValidationException, InvalidNumberOfArgsException {
        Validators.validateNumberOfArgs(arguments.getNumberOfArgs(), commandData.numberOfArgs());
        int moodNumber = Validators.validateArg(arg -> ((int) arg < Mood.values().length + 1) && ((int) arg > 0),
                "Pick a Mood number:\n" + Mood.show(),
                Integer::parseInt,
                arguments.getArg());

        arguments.setEnumNumber(moodNumber);
        return arguments;
    }
}
