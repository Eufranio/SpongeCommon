/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.common.command.brigadier.tree;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.command.CommandSource;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.ValueCompleter;
import org.spongepowered.common.command.brigadier.SpongeStringReader;
import org.spongepowered.common.command.brigadier.argument.parser.ArgumentParser;
import org.spongepowered.common.command.brigadier.context.SpongeCommandContextBuilder;

import java.util.function.Predicate;

// We have to extend ArgumentCommandNode for Brig to even use this...
public class SpongeArgumentCommandNode<T> extends ArgumentCommandNode<CommandSource, T> {

    @Nullable
    private static SuggestionProvider<CommandSource> createSuggestionProvider(@Nullable final ValueCompleter completer) {
        if (completer == null) {
            return null;
        }

        return (context, builder) -> {
            completer.complete((org.spongepowered.api.command.parameter.CommandContext) context).forEach(builder::suggest);
            return builder.buildFuture();
        };
    }

    private final Parameter.Key<? super T> key;
    private final ArgumentParser<T> parser;

    @SuppressWarnings({"unchecked"})
    public SpongeArgumentCommandNode(
            final Parameter.Key<? super T> key,
            final ArgumentParser<T> parser,
            @Nullable final ValueCompleter valueCompleter,
            @Nullable final Command command,
            final Predicate<CommandSource> predicate,
            @Nullable final CommandNode<CommandSource> redirect,
            final RedirectModifier<CommandSource> modifier,
            final boolean forks) {
        super(key.key(),
                (ArgumentType<T>) parser.getClientCompletionType(),
                command,
                predicate,
                redirect,
                modifier,
                forks,
                createSuggestionProvider(valueCompleter));
        this.parser = parser;
        this.key = key;
    }

    @Override
    public void parse(final StringReader reader, final CommandContextBuilder<CommandSource> contextBuilder) throws CommandSyntaxException {
        final int start = reader.getCursor();
        final SpongeCommandContextBuilder builder = (SpongeCommandContextBuilder) contextBuilder;
        final T result = this.parser.parse(this.key, builder, (SpongeStringReader) reader);
        if (result != null) {
            final ParsedArgument<CommandSource, T> parsed = new ParsedArgument<>(start, reader.getCursor(), result);
            builder.withArgumentInternal(this.getName(), parsed, false);
            builder.withNode(this, parsed.getRange());
        }

    }

}
