/*
 * Copyright (C) 2017 Dennis Neufeld
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package space.npstr.wolfia.commands.ingame;

import space.npstr.wolfia.Launcher;
import space.npstr.wolfia.commands.CommRegistry;
import space.npstr.wolfia.commands.CommandContext;
import space.npstr.wolfia.commands.GameCommand;
import space.npstr.wolfia.commands.GuildCommandContext;
import space.npstr.wolfia.config.properties.WolfiaConfig;
import space.npstr.wolfia.game.Game;
import space.npstr.wolfia.game.exceptions.IllegalGameStateException;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Created by napster on 21.05.17.
 * <p>
 * A player shoots another player
 */
public class ShootCommand extends GameCommand {

    public ShootCommand(final String trigger, final String... aliases) {
        super(trigger, aliases);
    }

    @Nonnull
    @Override
    public String help() {
        return invocation() + " @player"
                + "\n#Shoot the mentioned player.";
    }

    @SuppressWarnings("Duplicates")
    @Override
    public boolean execute(@Nonnull final CommandContext commandContext) throws IllegalGameStateException {
        //this command may be called in a guild for popcorn and a private channel for mafia

        final GuildCommandContext context = commandContext.requireGuild(false);
        if (context != null) { // find game through guild / textchannel
            final Optional<Game> game = getGame(context);
            if (!game.isPresent()) {
                context.replyWithMention(String.format("there is no game currently going on in here. Say `%s` to get started!",
                        WolfiaConfig.DEFAULT_PREFIX + CommRegistry.COMM_TRIGGER_HELP));
                return false;
            }

            return game.get().issueCommand(context);
        } else {//handle it being issued in a private channel
            //todo handle a player being part of multiple games properly
            boolean issued = false;
            boolean success = false;
            for (final Game g : Launcher.getBotContext().getGameRegistry().getAll().values()) {
                if (g.isUserPlaying(commandContext.invoker)) {
                    if (g.issueCommand(commandContext)) {
                        success = true;
                    }
                    issued = true;
                }
            }
            if (!issued) {
                commandContext.replyWithMention(String.format("you aren't playing in any game currently. Say `%s` to get started!",
                        WolfiaConfig.DEFAULT_PREFIX + CommRegistry.COMM_TRIGGER_HELP));
                return false;
            }
            return success;
        }
    }
}
