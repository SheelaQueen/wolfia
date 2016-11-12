package de.npstr.wolfia.pregame.commands;

import de.npstr.wolfia.Command;
import de.npstr.wolfia.CommandListener;
import de.npstr.wolfia.pregame.Pregame;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

/**
 * Created by npstr on 14.09.2016
 * <p>
 * any signed up player can use this command to start a game
 */
public class StartCommand extends Command {

    public final static String COMMAND = "start";
    private final String HELP = "```usage: " + getListener().getPrefix()
            + COMMAND + " \nto start the game. Game will only start if enough players have signed up\n";

    private Pregame pg;

    public StartCommand(CommandListener listener, Pregame pregame) {
        super(listener);
        this.pg = pregame;
    }

    @Override
    public boolean argumentsValid(String[] args, MessageReceivedEvent event) {
        return true;
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent event) {
        return pg.startGame();
    }

    @Override
    public String help() {
        return HELP;
    }
}
