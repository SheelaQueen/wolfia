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

package space.npstr.wolfia.commands.game;

import space.npstr.wolfia.commands.BaseCommand;
import space.npstr.wolfia.commands.CommandContext;
import space.npstr.wolfia.commands.GuildCommandContext;
import space.npstr.wolfia.game.Game;
import space.npstr.wolfia.game.exceptions.IllegalGameStateException;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Created by napster on 27.05.17.
 * <p>
 * resend a role PM to a player
 */
public class RolePmCommand extends BaseCommand {

    public RolePmCommand(final String trigger, final String... aliases) {
        super(trigger, aliases);
    }

    @Nonnull
    @Override
    public String help() {
        return invocation() + "\n#Send your role for the ongoing game in a private message.";
    }

    @Override
    public boolean execute(@Nonnull final CommandContext commandContext) throws IllegalGameStateException {
        final GuildCommandContext context = commandContext.requireGuild();
        if (context == null) {
            return false;
        }

        final Optional<Game> gameOpt = getGame(context);
        if (!gameOpt.isPresent()) {
            context.replyWithMention("there is no game going on in here for which I could send you a role pm.");
            return false;
        }

        final Game game = gameOpt.get();
        if (!game.isUserPlaying(context.member)) {
            context.replyWithMention("you aren't playing in this game.");
            return false;
        }

        final String rolePm = game.getRolePm(context.member);

        context.replyPrivate(rolePm, null,
                __ -> context.replyWithMention("I cannot send you a private message, please unblock me and/or adjust your privacy settings."));
        return true;
    }
}
