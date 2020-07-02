package org.spongepowered.common.command;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.text.Text;
import org.spongepowered.common.command.brigadier.SpongeParameterTranslator;
import org.spongepowered.common.command.brigadier.TranslatedParameter;
import org.spongepowered.common.command.parameter.subcommand.SpongeSubcommandParameterBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SpongeParameterizedCommandBuilder implements Command.Parameterized.Builder {

    private final Set<String> claimedSubcommands = new HashSet<>();
    private final Map<Command.Parameterized, List<String>> subcommands = new HashMap<>();
    private final List<Parameter> parameters = new ArrayList<>();
    @Nullable private CommandExecutor commandExecutor;
    @Nullable private Function<CommandCause, Optional<Text>> extendedDescription;
    @Nullable private Function<CommandCause, Optional<Text>> shortDescription;
    @Nullable private Predicate<CommandCause> executionRequirements;

    @Override
    public Command.@NonNull Builder child(final Command.@NonNull Parameterized child, @NonNull final Iterable<String> aliases) {
        for (final String alias : aliases) {
            if (this.claimedSubcommands.contains(alias.toLowerCase())) {
                throw new IllegalStateException("The alias " + alias + " already has an associated subcommand.");
            }
        }

        final List<String> s = new ArrayList<>();
        aliases.forEach(x -> s.add(x.toLowerCase()));
        this.claimedSubcommands.addAll(s);
        this.subcommands.put(child, s);
        return this;
    }

    @Override
    public Command.@NonNull Builder parameter(@NonNull final Parameter parameter) {
        this.parameters.add(parameter);
        return this;
    }

    @Override
    public Command.@NonNull Builder setExecutor(@NonNull final CommandExecutor executor) {
        this.commandExecutor = executor;
        return this;
    }

    @Override
    public Command.@NonNull Builder setExtendedDescription(@NonNull final Function<CommandCause, Optional<Text>> extendedDescriptionFunction) {
        this.extendedDescription = extendedDescriptionFunction;
        return this;
    }

    @Override
    public Command.@NonNull Builder setShortDescription(@NonNull final Function<CommandCause, Optional<Text>> descriptionFunction) {
        this.shortDescription = descriptionFunction;
        return this;
    }

    @Override
    public Command.@NonNull Builder setPermission(@Nullable final String permission) {
        if (permission == null) {
            return this.setExecutionRequirements(null);
        }
        return this.setExecutionRequirements(commandCause -> commandCause.hasPermission(permission));
    }

    @Override
    public Command.@NonNull Builder setExecutionRequirements(@Nullable final Predicate<CommandCause> executionRequirements) {
        this.executionRequirements = executionRequirements;
        return this;
    }

    @Override
    public Command.@NonNull Parameterized build() {
        if (this.subcommands.isEmpty()) {
            Preconditions.checkState(this.commandExecutor != null, "Either a subcommand or an executor must exist!");
        } else {
            Preconditions.checkState(!(!this.parameters.isEmpty() && this.commandExecutor == null), "An executor must exist if you set parameters!");
        }

        final List<Parameter.Subcommand> subcommands =
                this.subcommands.entrySet().stream()
                        .map(x -> new SpongeSubcommandParameterBuilder().aliases(x.getValue()).setSubcommand(x.getKey()).build())
                        .collect(Collectors.toList());

        // build the node.
        final TranslatedParameter translatedParameter;
        if (this.commandExecutor == null) {
            translatedParameter = SpongeParameterTranslator.createCommandTreeWithSubcommandsOnly(subcommands);
        } else {
            translatedParameter = SpongeParameterTranslator.createCommandTree(this.parameters, subcommands, this.commandExecutor);
        }

        return new SpongeParameterizedCommand(
                translatedParameter,
                ImmutableList.copyOf(this.parameters),
                this.shortDescription,
                this.extendedDescription,
                this.executionRequirements,
                this.commandExecutor
        );
    }

    @Override
    public Command.@NonNull Builder reset() {
        this.subcommands.clear();
        this.claimedSubcommands.clear();
        this.commandExecutor = null;
        this.parameters.clear();
        this.executionRequirements = null;
        this.extendedDescription = null;
        this.shortDescription = null;
        return this;
    }

}
